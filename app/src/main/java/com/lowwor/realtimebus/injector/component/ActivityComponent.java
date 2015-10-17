package com.lowwor.realtimebus.injector.component;


import android.content.Context;

import com.lowwor.realtimebus.injector.ActivityScope;
import com.lowwor.realtimebus.injector.module.ActivityModule;

import dagger.Component;

/**
 * Created by lowworker on 2015/9/13.
 */
@ActivityScope
@Component(dependencies = AppComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    Context context();
}