package com.lowwor.realtimebus.injector.module;


import com.lowwor.realtimebus.data.api.BusApiRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/9/12.
 */
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    BusApiRepository provideBusApiRepository(){
        return  new BusApiRepository();
    }



}
