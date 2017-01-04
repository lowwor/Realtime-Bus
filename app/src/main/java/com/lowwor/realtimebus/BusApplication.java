package com.lowwor.realtimebus;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lowwor.realtimebus.developer_settings.AnalyticsProxy;
import com.lowwor.realtimebus.developer_settings.BugReportProxy;
import com.lowwor.realtimebus.developer_settings.LeakCanaryProxy;
import com.lowwor.realtimebus.developer_settings.StethoProxy;
import com.lowwor.realtimebus.injector.component.AppComponent;
import com.lowwor.realtimebus.injector.component.DaggerAppComponent;
import com.lowwor.realtimebus.injector.module.AppModule;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApplication extends Application {

    @Inject
    BugReportProxy bugReportProxy;
    @Inject
    LeakCanaryProxy leakCanaryProxy;
    @Inject
    AnalyticsProxy analyticsProxy;
    @Inject
    StethoProxy stethoProxy;
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

//        Logger.i("onCreate BusApplication");
        initializeInjector();
        initStetho();
        initLogger();
        initLeakCanary();
        initBugReport();
        initAnalytics();

        initRxJavaErrorPluginHandler();
    }

    // FIXME: 2017/1/4 0004 wait for  https://github.com/ReactiveX/RxJava/pull/4928
    private void initRxJavaErrorPluginHandler() {
//        https://github.com/ReactiveX/RxJava/issues/4894
        //https://github.com/ReactiveX/RxJava/issues/4863
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                Log.e("RealTimeBus","Uncaught: "+throwable.toString());
            }
        });
    }

    private void initBugReport() {
        bugReportProxy.init();
    }

    private void initLeakCanary() {
        leakCanaryProxy.init();
    }

    private void initAnalytics() {
        analyticsProxy.init();
    }

    void initStetho() {
        stethoProxy.init();
    }

    private void initLogger() {

        if (!BuildConfig.DEBUG) {
            // default LogLevel.FULL
            Logger.init().setLogLevel(LogLevel.NONE);
        }

    }

    protected void initializeInjector() {
        mAppComponent = prepareApplicationComponent().build();
        mAppComponent.inject(this);

    }

    @NonNull
    protected DaggerAppComponent.Builder prepareApplicationComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this));
    }

    public AppComponent getAppComponent() {

        return mAppComponent;
    }


}
