package com.lowwor.realtimebus.developer_settings;

import com.facebook.stetho.Stetho;
import com.lowwor.realtimebus.BusApplication;

/**
 * Created by lowwor on 2016/5/11 0011.
 */
public class StethoProxyImpl implements StethoProxy {
    private BusApplication busApplication;

    public StethoProxyImpl(BusApplication busApplication) {
        this.busApplication = busApplication;
    }

    @Override
    public void init() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(busApplication)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(busApplication))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(busApplication))
                        .build());
    }
}
