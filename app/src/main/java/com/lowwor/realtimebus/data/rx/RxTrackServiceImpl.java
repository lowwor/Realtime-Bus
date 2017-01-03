package com.lowwor.realtimebus.data.rx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.lowwor.realtimebus.ITrackCallback;
import com.lowwor.realtimebus.ITrackService;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.service.TrackService;
import com.orhanobut.logger.Logger;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by lowworker on 2016/3/8 0008.
 */
public class RxTrackServiceImpl implements RxTrackService {

    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final PublishSubject<List<Bus>> mBusSubject = PublishSubject.create();
    private BehaviorSubject<ITrackService> trackServiceSubject = BehaviorSubject.create();
    private ITrackService mService;
    private CompositeDisposable compositeDisposable;
    private ITrackCallback mCallback = new ITrackCallback.Stub() {
        @Override
        public void onBusArrived(List<Bus> buses) throws RemoteException {
//            Logger.d("onBusArrived() called with: " + "buses = [" + buses + "]");
            mBusSubject.onNext(buses);
        }

        @Override
        public void onFail(String errorMessage) throws RemoteException {
//            Logger.d("onFail() called with: " + "errorMessage = [" + errorMessage + "]");
            mBusSubject.onError(new Exception(errorMessage));
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.d("onServiceConnected() called with: " + "name = [" + name + "], service = [" + service + "]");
            mService = ITrackService.Stub.asInterface(service);
            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                mService.registerCallback(mCallback);
                mService.clearAlarmStation();
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
            trackServiceSubject.onComplete();
        }
    };

    @Inject
    public RxTrackServiceImpl(Context context, PreferencesHelper preferencesHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void stopAutoRefresh() {

        compositeDisposable.add(trackServiceSubject.subscribeWith(new DisposableObserver<ITrackService>() {
            @Override
            public void onNext(ITrackService iTrackService) {
                Logger.d("call() called with: " + "trackService = [" + iTrackService + "]");
                try {
                    mService.stopAutoRefresh();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mBusSubject.onError(throwable);
            }

            @Override
            public void onComplete() {

            }
        }));
    }


    @Override
    public void startAutoRefresh(final String lineName, final String fromStation) {

        compositeDisposable.add(trackServiceSubject.subscribeWith(new DisposableObserver<ITrackService>() {
            @Override
            public void onNext(ITrackService iTrackService) {
                try {
                    mService.setShowNotification(mPreferencesHelper.getShowNotification());
                    mService.setShowPopupNotification(mPreferencesHelper.getShowPopupNotification());
                    mService.startAutoRefresh(lineName, fromStation, mPreferencesHelper.getAutoRefreshInterval());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mBusSubject.onError(throwable);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void addAlarmStation(final String stationName) {

        compositeDisposable.add(trackServiceSubject.subscribeWith(new DisposableObserver<ITrackService>() {
            @Override
            public void onNext(ITrackService iTrackService) {
                Logger.d("call() called with: " + "trackService = [" + iTrackService + "]");
                try {
                    mService.addAlarmStation(stationName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mBusSubject.onError(throwable);

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void removeAlarmStation(final String stationName) {

        compositeDisposable.add(trackServiceSubject.subscribeWith(new DisposableObserver<ITrackService>() {
            @Override
            public void onNext(ITrackService iTrackService) {
                Logger.d("call() called with: " + "trackService = [" + iTrackService + "]");
                try {
                    mService.removeAlarmStation(stationName);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                mBusSubject.onError(throwable);

            }

            @Override
            public void onComplete() {

            }
        }));
    }


    @Override
    public Observable<List<Bus>> getBusObservable() {

        Intent intent = new Intent(mContext, TrackService.class);
        mContext.bindService(intent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        return mBusSubject.hide();
    }


    public void close() {
        Logger.d("close() called with: " + "");
        try {
            mService.unregisterCallback(mCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mContext.unbindService(mServiceConnection);

        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
