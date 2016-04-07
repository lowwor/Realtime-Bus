package com.lowwor.realtimebus.injector.component;

import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.injector.ActivityScope;
import com.lowwor.realtimebus.injector.module.FragmentModule;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.track.TrackFragment;
import com.lowwor.realtimebus.viewmodel.TrackViewModel;

import dagger.Component;

/**
 * Created by lowworker on 2015/10/15.
 */

@ActivityScope
@Component(dependencies = AppComponent.class,modules = {FragmentModule.class,TrackModule.class})
public interface TrackComponent {
   RxTrackService rxTrackService();
   TrackViewModel trackViewModel();
   void inject(TrackFragment trackFragment);
   void inject(MainActivity mainActivity);
}
