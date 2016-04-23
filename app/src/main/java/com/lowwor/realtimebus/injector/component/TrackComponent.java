package com.lowwor.realtimebus.injector.component;

import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.injector.TrackScope;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.track.TrackFragment;
import com.lowwor.realtimebus.ui.track.TrackPresenter;
import com.lowwor.realtimebus.ui.track.TrackViewModel;

import dagger.Component;

/**
 * Created by lowworker on 2015/10/15.
 */

@TrackScope
@Component(dependencies = {ActivityComponent.class}, modules = {TrackModule.class})
public interface TrackComponent {
    RxTrackService rxTrackService();

    TrackViewModel trackViewModel();

    TrackPresenter trackPresenter();

    void inject(TrackFragment trackFragment);

    void inject(MainActivity mainActivity);
}
