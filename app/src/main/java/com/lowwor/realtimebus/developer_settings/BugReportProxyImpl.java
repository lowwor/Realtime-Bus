package com.lowwor.realtimebus.developer_settings;

import com.lowwor.realtimebus.BuildConfig;
import com.lowwor.realtimebus.BusApplication;
import com.tencent.bugly.Bugly;

/**
 * Created by lowworker on 2016/5/9 0009.
 */
public class BugReportProxyImpl implements BugReportProxy {

    public static final String BUGLY_APPID = "900027065";
    private BusApplication busApplication;

    public BugReportProxyImpl(BusApplication busApplication) {
        this.busApplication = busApplication;
    }

    @Override
    public void init() {
        if (!BuildConfig.DEBUG) {
            Bugly.init(busApplication, BUGLY_APPID, false);
        } else {
            Bugly.init(busApplication, BUGLY_APPID, true);
        }
    }
}
