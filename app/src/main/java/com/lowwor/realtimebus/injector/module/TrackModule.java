package com.lowwor.realtimebus.injector.module;

import android.content.Context;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.data.rx.RxTrackServiceImpl;
import com.lowwor.realtimebus.injector.TrackScope;
import com.lowwor.realtimebus.ui.track.TrackPresenter;
import com.lowwor.realtimebus.ui.track.TrackPresenterImp;
import com.lowwor.realtimebus.ui.track.TrackViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/10/15.
 */
@Module
public class TrackModule {
    @TrackScope
    @Provides
    RxTrackService provideRxTrackService(BusApplication busApplication, PreferencesHelper preferencesHelper){
        return new RxTrackServiceImpl(busApplication,preferencesHelper);
    }
    @TrackScope
    @Provides
    TrackViewModel provideTrackViewModel(RxTrackService rxTrackService){
        return new TrackViewModel(rxTrackService);
    }
    @TrackScope
    @Provides
    TrackPresenter provideTrackPresenter(Context context, RxTrackService rxTrackService, TrackViewModel trackViewModel, PreferencesHelper preferencesHelper, BusApiRepository busApiRepository){
        return new TrackPresenterImp(context,trackViewModel,busApiRepository,preferencesHelper,rxTrackService);
    }
}
