package com.lowwor.realtimebus;

import android.app.Application;
import android.content.Intent;

import com.facebook.stetho.Stetho;
import com.lowwor.realtimebus.data.service.TrackService;
import com.lowwor.realtimebus.injector.component.AppComponent;
import com.lowwor.realtimebus.injector.component.DaggerAppComponent;
import com.lowwor.realtimebus.injector.module.AppModule;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.umeng.update.UmengUpdateAgent;

/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApplication extends Application {

    private AppComponent mAppComponent;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

//        Logger.i("onCreate BusApplication");
        initializeInjector();
        initStetho();
        initLogger();
        initLeakCanary();
        initService();

        UmengUpdateAgent.update(this);
    }

    private void initService() {
        Intent intent = new Intent(this, TrackService.class);
        startService(intent);
    }

    private void initLeakCanary() {
        refWatcher = LeakCanary.install(this);
    }

    private void initLogger() {

        if (!BuildConfig.DEBUG) {
            // default LogLevel.FULL
            Logger.init().setLogLevel(LogLevel.NONE);
        }

    }

    private void initializeInjector() {

        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

    }



    public AppComponent getAppComponent() {

        return mAppComponent;
    }

    void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

}
