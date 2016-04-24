package com.lowwor.realtimebus.data.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.ITrackCallback;
import com.lowwor.realtimebus.ITrackService;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.ui.MainActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    final RemoteCallbackList<ITrackCallback> mCallbacks = new RemoteCallbackList<>();
    private static final int NOTIFICATION_FLAG = 1;
    public static final int BACKGROUND_NOTIFICATION_FLAG = 2;
    public static final int ACTION_STOP_FLAG = 3;
    private Toast toast;
    private List<String> mAlarmStations = new ArrayList<>();
    private boolean shouldShowNotification = true;
    private boolean shouldShowPopupNotification = true;

    public static final String EXTRA_STOP_KEY = "extra_stop_key";

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
            compositeSubscription.clear();
        }

        @Override
        public void startAutoRefresh(final String lineName, final String fromStation, int interval) throws RemoteException {
            Logger.d("startAutoRefresh() called with: " + "lineName = [" + lineName + "], fromStation = [" + fromStation + "]");
            Subscription autoRefreshSupscription = Observable.interval(interval, TimeUnit.SECONDS).timeInterval()
                    .flatMap(new Func1<TimeInterval<Long>, Observable<BusWrapper>>() {
                        @Override
                        public Observable<BusWrapper> call(TimeInterval<Long> longTimeInterval) {
                            return busApiRepository.getBusListOnRoad(lineName, fromStation)
                                    .doOnError(new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Toast.makeText(getApplicationContext(), R.string.error_get_bus, Toast.LENGTH_SHORT).show();
                                            callbackFailed(throwable.getMessage());
                                        }
                                    }).onErrorResumeNext(new Func1<Throwable, Observable<? extends BusWrapper>>() {
                                @Override
                                public Observable<? extends BusWrapper> call(Throwable throwable) {
                                    return Observable.empty();
                                }
                            });
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
                            Logger.d("onError() called with: " + "e = [" + e + "]");
                        }

                        @Override
                        public void onNext(BusWrapper busWrapper) {
                            Logger.d("onNext() called with: " + "busWrapper = [" + busWrapper + "]");
                            callbackSuccess(busWrapper.getData());
                        }
                    });
            compositeSubscription.add(autoRefreshSupscription);
        }

        @Override
        public void setShowNotificatoin(boolean shouldShow) throws RemoteException {
            shouldShowNotification = shouldShow;
        }

        @Override
        public void setShowPopupNotification(boolean shouldShow) throws RemoteException {
            shouldShowPopupNotification = shouldShow;
        }

        @Override
        public void addAlarmStation(String stationName) throws RemoteException {
            if (!mAlarmStations.contains(stationName)) {
                mAlarmStations.add(stationName);
            }
        }

        @Override
        public void removeAlarmStation(String stationName) throws RemoteException {
            if (mAlarmStations.contains(stationName)) {
                mAlarmStations.remove(stationName);
            }
        }

        @Override
        public void clearAlarmStation() throws RemoteException {
            mAlarmStations.clear();
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
        if (intent != null && intent.getExtras() != null) {
            if (intent.getBooleanExtra(EXTRA_STOP_KEY, false)) {
                Logger.i("onStartCommand: stopSelf");

                stopSelf();
            }

        } else {
            showForegroundNotification();
        }
        return START_NOT_STICKY;
    }


    void callbackSuccess(List<Bus> buses) {
//        Logger.d("callbackSuccess() called with: " + "buses = [" + buses.size() + "]");
        for (Bus bus : buses) {
            for (String alarmStation : mAlarmStations) {
                if (alarmStation.equals(bus.currentStation)) {
                    if (shouldShowNotification) {
                        showNotification(alarmStation);
                    }
                    if (shouldShowPopupNotification) {
                        showToast(alarmStation);
                    }
                }
            }
        }
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

    public void showNotification(String busStationName) {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), NOTIFICATION_FLAG, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setTicker(busStationName + "的公交到站了")
                .setContentIntent(pendingIntent)
                .setContentTitle(busStationName + "的公交到站了")
                .setContentText(busStationName + "的公交到站了")
                .setSmallIcon(R.drawable.ic_directions_bus)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_FLAG, mBuilder.build());

        ImageView iconView = new ImageView(getApplicationContext());
        iconView.setImageResource(R.mipmap.ic_launcher);
    }

    private void showToast(String busStationName) {
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.toast_bus_arrived, null);
        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        tvToast.setText(busStationName + "的公交到站了");
        toast.setView(view);
        toast.show();
    }

    private void showForegroundNotification() {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getResources().getText(R.string.service_track_notification_text))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(BACKGROUND_NOTIFICATION_FLAG, notification);
    }


    @Override
    public void onDestroy() {
        Logger.d("onDestroy() called with: " + "");
        super.onDestroy();
        try {
            mBinder.stopAutoRefresh();
        } catch (RemoteException e) {
            // The RemoteCallbackList will take care of removing
            // the dead object for us.
        }
        stopForeground(true);
    }
}
