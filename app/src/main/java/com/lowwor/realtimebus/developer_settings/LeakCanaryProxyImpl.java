package com.lowwor.realtimebus.developer_settings;

import android.support.annotation.NonNull;

import com.lowwor.realtimebus.BusApplication;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by lowworker on 2016/5/9 0009.
 */
public class LeakCanaryProxyImpl implements LeakCanaryProxy{


    private RefWatcher watcher;
    private BusApplication busApplication;

    public LeakCanaryProxyImpl(BusApplication busApplication) {
        this.busApplication = busApplication;
    }

    @Override
    public void init() {
        watcher = LeakCanary.install(busApplication);
    }

    @Override
    public void watch(@NonNull Object object) {
        watcher.watch(object);
    }
}
