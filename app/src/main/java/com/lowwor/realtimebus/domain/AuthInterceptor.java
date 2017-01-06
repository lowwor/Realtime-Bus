package com.lowwor.realtimebus.domain;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lowworker on 2017/1/6 0006.
 */

public class AuthInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .header("Cookie", "IfAuth=93cba07454f06a4a960172bbd6e2a435")
                .header("Cookie","ptcz=93cba07454f06a4a960172bbd6e2a435")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
