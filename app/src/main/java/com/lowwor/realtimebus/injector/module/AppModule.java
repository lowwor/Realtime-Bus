package com.lowwor.realtimebus.injector.module;

import com.lowwor.realtimebus.BusApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowwor on 2015/9/19.
 */
@Module
public class AppModule {

    private final BusApplication mBusApplication;

    public AppModule(BusApplication busApplication) {
        this.mBusApplication = busApplication;
    }

    @Provides
    @Singleton
    BusApplication provideBusApplication() {
        return mBusApplication;
    }

}
