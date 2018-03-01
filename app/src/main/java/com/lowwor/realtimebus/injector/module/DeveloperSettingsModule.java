package com.lowwor.realtimebus.injector.module;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.developer_settings.AnalyticsProxy;
import com.lowwor.realtimebus.developer_settings.AnalyticsProxyImpl;
import com.lowwor.realtimebus.developer_settings.BugReportProxy;
import com.lowwor.realtimebus.developer_settings.BugReportProxyImpl;
import com.lowwor.realtimebus.developer_settings.LeakCanaryProxy;
import com.lowwor.realtimebus.developer_settings.LeakCanaryProxyImpl;
import com.lowwor.realtimebus.developer_settings.StethoProxy;
import com.lowwor.realtimebus.developer_settings.StethoProxyImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowwor on 2016/5/8 0008.
 */
@Module
public class DeveloperSettingsModule {

    @Provides
    @Singleton
    public LeakCanaryProxy provideLeakCanaryProxy(BusApplication busApplication) {
        return new LeakCanaryProxyImpl(busApplication);

    }

    @Provides
    @Singleton
    public BugReportProxy provideBugReportProxy(BusApplication busApplication) {
        return new BugReportProxyImpl(busApplication);

    }

    @Provides
    @Singleton
    public AnalyticsProxy provideAnalyticsProxy(BusApplication busApplication) {
        return new AnalyticsProxyImpl(busApplication);

    }


    @Provides
    @Singleton
    public StethoProxy provideStethoProxy(BusApplication busApplication) {
        return new StethoProxyImpl(busApplication);

    }
}
