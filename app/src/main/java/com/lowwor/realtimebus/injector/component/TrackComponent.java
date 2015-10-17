package com.lowwor.realtimebus.injector.component;

import com.lowwor.realtimebus.injector.FragmentScope;
import com.lowwor.realtimebus.injector.module.FragmentModule;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.track.TrackFragment;

import dagger.Component;

/**
 * Created by lowworker on 2015/10/15.
 */

@FragmentScope
@Component(dependencies = AppComponent.class,modules = {FragmentModule.class,TrackModule.class})
public interface TrackComponent {
   void inject(TrackFragment trackFragment);
}
