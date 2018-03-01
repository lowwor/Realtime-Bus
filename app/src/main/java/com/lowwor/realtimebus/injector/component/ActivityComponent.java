package com.lowwor.realtimebus.injector.component;


import android.content.Context;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.domain.NetworkInteractor;
import com.lowwor.realtimebus.injector.ActivityScope;
import com.lowwor.realtimebus.injector.module.ActivityModule;

import dagger.Component;

/**
 * Created by lowwor on 2015/9/13.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    BusApplication app();

    BusApiRepository busApiRepository();

    PreferencesHelper preferencesHelper();

    NetworkInteractor networkInteractor();

    Context context();
}