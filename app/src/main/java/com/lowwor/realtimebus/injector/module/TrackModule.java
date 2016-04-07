package com.lowwor.realtimebus.injector.module;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.data.rx.RxTrackServiceImpl;
import com.lowwor.realtimebus.injector.ActivityScope;
import com.lowwor.realtimebus.viewmodel.TrackViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/10/15.
 */
@Module
public class TrackModule {
    @ActivityScope
    @Provides
    RxTrackService provideRxTrackService(BusApplication busApplication, PreferencesHelper preferencesHelper){
        return new RxTrackServiceImpl(busApplication,preferencesHelper);
    }
    @ActivityScope
    @Provides
    TrackViewModel provideTrackViewModel(RxTrackService rxTrackService){
        return new TrackViewModel(rxTrackService);
    }
}
