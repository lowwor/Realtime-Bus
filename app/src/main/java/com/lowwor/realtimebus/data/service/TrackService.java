package com.lowwor.realtimebus.data.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.ITrackCallback;
import com.lowwor.realtimebus.ITrackService;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.utils.Constants;
import com.lowwor.realtimebus.utils.RxUtils;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lowworker on 2016/3/6 0006.
 */
public class TrackService extends Service {

    @Inject
    BusApiRepository busApiRepository;

    CompositeSubscription compositeSubscription;

    final RemoteCallbackList<ITrackCallback> mCallbacks = new RemoteCallbackList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("onBind() called with: " + "intent = [" + intent + "]");
        return mBinder;
    }


    /**
     * 在AIDL文件中定义的接口实现。
     */
    private ITrackService.Stub mBinder = new ITrackService.Stub() {


        @Override
        public void stopAutoRefresh() throws RemoteException {
            Logger.d("stopAutoRefresh() called with: " + "");
            RxUtils.unsubscribeIfNotNull(compositeSubscription);
        }

        @Override
        public void startAutoRefresh(final String lineName, final String fromStation) throws RemoteException {
            Logger.d("startAutoRefresh() called with: " + "lineName = [" + lineName + "], fromStation = [" + fromStation + "]");
            compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
            Subscription autoRefreshSupscription = Observable.interval(Constants.REFRESH_INTERVAL, TimeUnit.SECONDS).timeInterval()
                    .flatMap(new Func1<TimeInterval<Long>, Observable<BusWrapper>>() {
                        @Override
                        public Observable<BusWrapper> call(TimeInterval<Long> longTimeInterval) {
                            return busApiRepository.getBusListOnRoad(lineName, fromStation);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<BusWrapper>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            callbackFailed(e.getMessage());
                        }

                        @Override
                        public void onNext(BusWrapper busWrapper) {
//                            Logger.d("onNext() called with: " + "busWrapper = [" + busWrapper + "]");
                            callbackSuccess(busWrapper.getData());
                        }
                    });
            compositeSubscription.add(autoRefreshSupscription);
        }

        @Override
        public void registerCallback(ITrackCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbacks.register(callback);
            }
        }

        @Override
        public void unregisterCallback(ITrackCallback callback) throws RemoteException {
            if (callback != null) {
                mCallbacks.unregister(callback);
            }

        }

    };


    private void initDependencyInjector() {
        ((BusApplication) getApplication()).getAppComponent().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("onCreate: ");
        initDependencyInjector();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Logger.d("onStartCommand() called with: " + "intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.d("onStart() called with: " + "intent = [" + intent + "], startId = [" + startId + "]");
    }

    void callbackSuccess(List<Bus> buses) {
//        Logger.d("callbackSuccess() called with: " + "buses = [" + buses.size() + "]");
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).onBusArrived(buses);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing
                // the dead object for us.
            }
        }
        mCallbacks.finishBroadcast();
    }

    void callbackFailed(String errorMsg) {
        final int N = mCallbacks.beginBroadcast();
        for (int i = 0; i < N; i++) {
            try {
                mCallbacks.getBroadcastItem(i).onFail(errorMsg);
            } catch (RemoteException e) {
                // The RemoteCallbackList will take care of removing
                // the dead object for us.
            }
        }
        mCallbacks.finishBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
