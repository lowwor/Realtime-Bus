package com.lowwor.realtimebus.data.rx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.lowwor.realtimebus.ITrackCallback;
import com.lowwor.realtimebus.ITrackService;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.service.TrackService;
import com.orhanobut.logger.Logger;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lowworker on 2016/3/8 0008.
 */
public class RxTrackServiceImpl implements RxTrackService {

    private final Context mContext;
    private BehaviorSubject<ITrackService> trackServiceSubject = BehaviorSubject.create();
    private final PublishSubject<List<Bus>> mBusSubject = PublishSubject.create();
    private ITrackService mService;

    private CompositeSubscription compositeSubscription;

    public RxTrackServiceImpl(Context context) {
        mContext = context;
        compositeSubscription = new CompositeSubscription();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d("onServiceConnected() called with: " + "name = [" + name + "], service = [" + service + "]");
            mService = ITrackService.Stub.asInterface(service);
            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                mService.registerCallback(mCallback);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            trackServiceSubject.onNext(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Logger.d("onServiceDisconnected() called with: " + "name = [" + name + "]");
            mService = null;
            trackServiceSubject.onCompleted();
        }
    };

    private ITrackCallback mCallback = new ITrackCallback.Stub() {
        @Override
        public void onBusArrived(List<Bus> buses) throws RemoteException {
            Logger.d("onBusArrived() called with: " + "buses = [" + buses + "]");
            mBusSubject.onNext(buses);
        }

        @Override
        public void onFail(String errorMessage) throws RemoteException {
            Logger.d("onFail() called with: " + "errorMessage = [" + errorMessage + "]");
            mBusSubject.onError(new Exception(errorMessage));
        }
    };

    @Override
    public void stopAutoRefresh() {
        Subscription stopAutoRefreshSubscription =
                trackServiceSubject.subscribe(new Action1<ITrackService>() {
                    @Override
                    public void call(ITrackService trackService) {
                        Logger.d("call() called with: " + "trackService = [" + trackService + "]");
                        try {
                            mService.stopAutoRefresh();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mBusSubject.onError(throwable);
                    }
                });
        compositeSubscription.add(stopAutoRefreshSubscription);
    }



    @Override
    public void startAutoRefresh(final String lineName, final String fromStation) {

        Subscription startAutoRefreshSubscription =
                trackServiceSubject.subscribe(new Action1<ITrackService>() {
                    @Override
                    public void call(ITrackService trackService) {
                        try {
                            mService.startAutoRefresh(lineName,fromStation);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mBusSubject.onError(throwable);
                    }
                });
        compositeSubscription.add(startAutoRefreshSubscription);
    }

    @Override
    public Observable<List<Bus>> getBusObservable(){
        Intent intent = new Intent(mContext, TrackService.class);
        mContext.startService(intent);
        mContext.bindService(intent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        return mBusSubject.asObservable();
    }


    public void close()  {
        Logger.d("close() called with: " + "");
        try {
            mService.unregisterCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mContext.unbindService(mServiceConnection);
        if (compositeSubscription != null && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }
}
