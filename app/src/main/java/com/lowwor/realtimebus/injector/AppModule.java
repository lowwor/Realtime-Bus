package com.lowwor.realtimebus.injector;

import android.content.Context;
import android.content.SharedPreferences;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.utils.Constants;

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
    RxSharedPreferences provideRxSharedPreferences(BusApplication busApplication){
        SharedPreferences preferences = busApplication.getSharedPreferences(Constants.SP_BUS, Context.MODE_PRIVATE);
        return RxSharedPreferences.create(preferences);
    }

}
