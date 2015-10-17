package com.lowwor.realtimebus;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.lowwor.realtimebus.injector.AppModule;
import com.lowwor.realtimebus.injector.component.AppComponent;
import com.lowwor.realtimebus.injector.component.DaggerAppComponent;
import com.orhanobut.logger.Logger;

/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApplication extends Application {

    private AppComponent mAppComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        Logger.i("onCreate BusApplication");
        initializeInjector();
        initStetho();
    }

    private void initializeInjector(){

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

    }


    public AppComponent getAppComponent() {

        return mAppComponent;
    }

    void initStetho(){
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

}
