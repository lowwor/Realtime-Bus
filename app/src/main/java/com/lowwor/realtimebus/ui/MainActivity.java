package com.lowwor.realtimebus.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.service.TrackService;
import com.lowwor.realtimebus.injector.component.ActivityComponent;
import com.lowwor.realtimebus.injector.component.DaggerActivityComponent;
import com.lowwor.realtimebus.injector.component.DaggerProfileComponent;
import com.lowwor.realtimebus.injector.component.DaggerTrackComponent;
import com.lowwor.realtimebus.injector.component.ProfileComponent;
import com.lowwor.realtimebus.injector.component.TrackComponent;
import com.lowwor.realtimebus.injector.module.ActivityModule;
import com.lowwor.realtimebus.injector.module.ProfileModule;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.base.BaseActivity;
import com.lowwor.realtimebus.ui.profile.ProfileFragment;
import com.lowwor.realtimebus.ui.track.TrackFragment;
import com.ncapdevi.fragnav.FragNavController;
import com.orhanobut.logger.Logger;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by lowwor on 2015/10/15.
 */
public class MainActivity extends BaseActivity {

    private final int INDEX_TRACK = FragNavController.TAB1;
    private final int INDEX_PROFILE = FragNavController.TAB2;
    TrackComponent mTrackComponent;
    @Inject
    PreferencesHelper mPreferencesHelper;
    private ActivityComponent activityComponent;
    private BottomBar mBottomBar;
    private FragNavController mNavController;
    private ProfileComponent mProfileComponent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.i("onCreate activity");
        setContentView(R.layout.activity_main);
        initService();
        initDependencyInjector();
        initBottomBar(savedInstanceState);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }


    private void initBottomBar(Bundle savedInstanceState) {
        List<Fragment> fragments = new ArrayList<>(2);

        fragments.add(TrackFragment.newInstance());
        fragments.add(ProfileFragment.newInstance());

        mNavController =
                new FragNavController(getSupportFragmentManager(), R.id.container, fragments);

        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noTabletGoodness();
        mBottomBar.setActiveTabColor("#ff8a65");
        mBottomBar.setItemsFromMenu(R.menu.bottom_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {

                switch (menuItemId) {
                    case R.id.bottom_track:
                        mNavController.switchTab(INDEX_TRACK);
                        break;
                    case R.id.bottom_profile:
                        mNavController.switchTab(INDEX_PROFILE);
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
        updateNotificationIfAlarmStationNotNull();
    }

    private void updateNotificationIfAlarmStationNotNull() {
        if (mPreferencesHelper.getTrackBackground()) {
            Intent intent = new Intent(this, TrackService.class);
            intent.putExtra(TrackService.EXTRA_UPDATE_NOTIFICATION, true);
            startService(intent);
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

    public ProfileComponent profileComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .appComponent(((BusApplication) getApplication()).getAppComponent())
                    .build();
        }

        if (mProfileComponent == null) {
            mProfileComponent = DaggerProfileComponent.builder()
                    .activityComponent(activityComponent)
                    .profileModule(new ProfileModule())
                    .build();
        }
        return mProfileComponent;
    }


}
