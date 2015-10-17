package com.lowwor.realtimebus.api;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.internal.$Gson$Types;
import com.lowwor.realtimebus.api.exceptions.RetrofitException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.concurrent.Executor;

import retrofit.Call;
import retrofit.CallAdapter;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by lowworker on 2015/10/14.
 */
public class ErrorHandlingExecutorCallAdapterFactory implements CallAdapter.Factory {
    private final Executor callbackExecutor;


    ErrorHandlingExecutorCallAdapterFactory(Executor callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
    }


    @Override
    public CallAdapter<Call<?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if ($Gson$Types.getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = getCallResponseType(returnType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }


            @Override
            public <R> Call<R> adapt(Call<R> call) {
                return new ExecutorCallbackCall<>(callbackExecutor, call);
            }
        };
    }


    static final class ExecutorCallbackCall<T> implements Call<T> {
        private final Executor callbackExecutor;
        private final Call<T> delegate;


        ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
            this.callbackExecutor = callbackExecutor;
            this.delegate = delegate;
        }


        @Override
        public void enqueue(Callback<T> callback) {
            delegate.enqueue(new ExecutorCallback<>(callbackExecutor, callback));
        }


        @Override
        public Response<T> execute() throws IOException {
            return delegate.execute();
        }


        @Override
        public void cancel() {
            delegate.cancel();
        }


        @SuppressWarnings("CloneDoesntCallSuperClone") // Performing deep clone.
        @Override
        public Call<T> clone() {
            return new ExecutorCallbackCall<>(callbackExecutor, delegate.clone());
        }
    }


    static final class ExecutorCallback<T> implements Callback<T> {
        private final Executor callbackExecutor;
        private final Callback<T> delegate;


        ExecutorCallback(Executor callbackExecutor, Callback<T> delegate) {
            this.callbackExecutor = callbackExecutor;
            this.delegate = delegate;
        }


        @Override
        public void onResponse(final Response<T> response, final Retrofit retrofit) {
            if (response.isSuccess()) {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        delegate.onResponse(response, retrofit);
                    }
                });
            } else {
                callbackExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        delegate.onFailure(RetrofitException.httpError(response.raw().request().urlString(), response, retrofit));
                    }
                });
            }
        }


        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void onFailure(final Throwable t) {
            RetrofitException exception;
            if (t instanceof IOException) {
                exception = RetrofitException.networkError((IOException) t);
            }
            else {
                exception = RetrofitException.unexpectedError(t);
            }
            final RetrofitException finalException = exception;
            callbackExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    delegate.onFailure(finalException);
                }
            });
        }
    }


    public static class MainThreadExecutor implements Executor {
        private final Handler handler = new Handler(Looper.getMainLooper());


        @Override
        public void execute(@NonNull Runnable r) {
            handler.post(r);
        }
    }


    static Type getCallResponseType(Type returnType) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException(
                    "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>");
        }
        final Type responseType = getSingleParameterUpperBound((ParameterizedType) returnType);


        // Ensure the Call response type is not Response, we automatically deliver the Response object.
        if ($Gson$Types.getRawType(responseType) == retrofit.Response.class) {
            throw new IllegalArgumentException(
                    "Call<T> cannot use Response as its generic parameter. "
                            + "Specify the response body type only (e.g., Call<TweetResponse>).");
        }
        return responseType;
    }


    public static Type getSingleParameterUpperBound(ParameterizedType type) {
        Type[] types = type.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException(
                    "Expected one type argument but got: " + Arrays.toString(types));
        }
        Type paramType = types[0];
        if (paramType instanceof WildcardType) {
            return ((WildcardType) paramType).getUpperBounds()[0];
        }
        return paramType;
    }
}
