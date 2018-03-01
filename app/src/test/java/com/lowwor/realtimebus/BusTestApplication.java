package com.lowwor.realtimebus;

import android.support.annotation.NonNull;

import com.lowwor.realtimebus.developer_settings.AnalyticsProxy;
import com.lowwor.realtimebus.developer_settings.BugReportProxy;
import com.lowwor.realtimebus.developer_settings.LeakCanaryProxy;
import com.lowwor.realtimebus.developer_settings.StethoProxy;
import com.lowwor.realtimebus.injector.component.DaggerAppComponent;
import com.lowwor.realtimebus.injector.module.DeveloperSettingsModule;

import static org.mockito.Mockito.mock;

/**
 * Created by lowwor on 2016/5/10 0010.
 */
public class BusTestApplication extends BusApplication {


    @Override
    @NonNull
    protected DaggerAppComponent.Builder prepareApplicationComponent() {
        return

                super.prepareApplicationComponent()
                        .developerSettingsModule(new DeveloperSettingsModule() {

                            @Override
                            public LeakCanaryProxy provideLeakCanaryProxy(BusApplication busApplication) {
                                return mock(LeakCanaryProxy.class);

                            }

                            @Override
                            public BugReportProxy provideBugReportProxy(BusApplication busApplication) {
                                return mock(BugReportProxy.class);

                            }

                            @Override
                            public AnalyticsProxy provideAnalyticsProxy(BusApplication busApplication) {
                                return mock(AnalyticsProxy.class);

                            }

                            @Override
                            public StethoProxy provideStethoProxy(BusApplication busApplication){
                                return mock(StethoProxy.class);

                            }
                        });
    }
}
