package com.lowwor.realtimebus.developer_settings;

import com.lowwor.realtimebus.BusApplication;

/**
 * Created by lowwor on 2016/5/9 0009.
 */
public class AnalyticsProxyImpl implements AnalyticsProxy {

    private BusApplication busApplication;

    public AnalyticsProxyImpl(BusApplication busApplication) {
        this.busApplication = busApplication;
    }

    @Override
    public void init() {

    }
}
