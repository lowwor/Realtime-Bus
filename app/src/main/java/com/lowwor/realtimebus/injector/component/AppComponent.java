package com.lowwor.realtimebus.injector.component;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.service.TrackService;
import com.lowwor.realtimebus.developer_settings.AnalyticsProxy;
import com.lowwor.realtimebus.developer_settings.BugReportProxy;
import com.lowwor.realtimebus.developer_settings.LeakCanaryProxy;
import com.lowwor.realtimebus.injector.module.AppModule;
import com.lowwor.realtimebus.injector.module.DeveloperSettingsModule;
import com.lowwor.realtimebus.injector.module.RepositoryModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lowworker on 2015/9/19.
 */

@Singleton
@Component(modules = {AppModule.class, DeveloperSettingsModule.class, RepositoryModule.class})
public interface AppComponent {

    BusApplication app();

    BusApiRepository busApiRepository();

    PreferencesHelper preferencesHelper();

    AnalyticsProxy analyticsProxy();

    BugReportProxy bugReportProxy();

    LeakCanaryProxy leakCanaryProxy();

    void inject(TrackService trackService);

    void inject(BusApplication busApplication);
}
