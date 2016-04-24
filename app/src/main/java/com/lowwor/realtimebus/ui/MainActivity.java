package com.lowwor.realtimebus.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.service.TrackService;
import com.lowwor.realtimebus.injector.component.ActivityComponent;
import com.lowwor.realtimebus.injector.component.DaggerActivityComponent;
import com.lowwor.realtimebus.injector.component.DaggerTrackComponent;
import com.lowwor.realtimebus.injector.component.TrackComponent;
import com.lowwor.realtimebus.injector.module.ActivityModule;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.base.BaseActivity;
import com.lowwor.realtimebus.ui.track.TrackFragment;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;


/**
 * Created by lowworker on 2015/10/15.
 */
public class MainActivity extends BaseActivity {

    TrackComponent mTrackComponent;

    @Inject
    PreferencesHelper mPreferencesHelper;
    private ActivityComponent activityComponent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i("onCreate activity");
        setContentView(R.layout.activity_main);
        initService();
        initDependencyInjector();
        replaceTrackFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        updateNotification();
    }

    private void updateNotification() {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(this, TrackService.class);
        stopIntent.putExtra(TrackService.EXTRA_STOP_KEY, true);
        PendingIntent pendingIntentStop = PendingIntent.getService(this, TrackService.ACTION_STOP_FLAG, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_track_service);
        remoteViews.setOnClickPendingIntent(R.id.button, pendingIntentStop);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getResources().getText(R.string.service_track_background_notification_text))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setContent(remoteViews)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mPreferencesHelper.getTrackBackground()) {
            mNotificationManager.notify(TrackService.BACKGROUND_NOTIFICATION_FLAG, notification);
        }

    }

    private void initService() {
        Intent intent = new Intent(this, TrackService.class);
        startService(intent);
    }

    private void stopService() {
        if (!mPreferencesHelper.getTrackBackground()) {
            Intent intent = new Intent(this, TrackService.class);
            stopService(intent);
        }
    }

    private void initDependencyInjector() {
        trackComponent().inject(this);
    }

    public TrackComponent trackComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .appComponent(((BusApplication) getApplication()).getAppComponent())
                    .build();
        }

        if (mTrackComponent == null) {
            mTrackComponent = DaggerTrackComponent.builder()
                    .activityComponent(activityComponent)
                    .trackModule(new TrackModule())
                    .build();
        }
        return mTrackComponent;
    }

    void replaceTrackFragment() {

        TrackFragment trackFragment = new TrackFragment();
        getFragmentManager().beginTransaction().replace(R.id.container, trackFragment).commit();
    }
}
