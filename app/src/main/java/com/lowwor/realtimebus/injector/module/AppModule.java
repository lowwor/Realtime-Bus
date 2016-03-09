package com.lowwor.realtimebus.injector.module;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.data.rx.RxTrackServiceImpl;
import com.lowwor.realtimebus.data.local.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/9/19.
 */
@Module
public class AppModule {

    private final BusApplication mBusApplication;

    public AppModule(BusApplication busApplication){
        this.mBusApplication = busApplication;
    }

    @Provides
    @Singleton
    BusApplication provideBusApplication(){
        return mBusApplication;
    }

    @Singleton
    @Provides
    PreferencesHelper providePreferencesHelper(BusApplication busApplication){
        return new PreferencesHelper(busApplication);
    }

    @Singleton
    @Provides
    RxTrackService provideRxTrackService(BusApplication busApplication){
        return new RxTrackServiceImpl(busApplication);
    }

}
