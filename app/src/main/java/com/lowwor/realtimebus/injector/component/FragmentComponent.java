package com.lowwor.realtimebus.injector.component;


import com.lowwor.realtimebus.injector.FragmentScope;
import com.lowwor.realtimebus.injector.module.FragmentModule;

import dagger.Component;

/**
 * Created by lowworker on 2015/9/13.
 */
@FragmentScope
@Component(dependencies = AppComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
}
