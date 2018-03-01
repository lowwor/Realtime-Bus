package com.lowwor.realtimebus.injector.module;


import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.Injection;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.api.BusService;
import com.lowwor.realtimebus.data.local.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowwor on 2015/9/12.
 */
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    BusApiRepository provideBusApiRepository(BusService busService) {
        return new BusApiRepository(busService);
    }


    @Singleton
    @Provides
    BusService provideBusService(BusApplication busApplication) {

        return Injection.provideBusService(busApplication.getApplicationContext());
    }


    @Singleton
    @Provides
    PreferencesHelper providePreferencesHelper(BusApplication busApplication) {
        return new PreferencesHelper(busApplication);
    }
}
