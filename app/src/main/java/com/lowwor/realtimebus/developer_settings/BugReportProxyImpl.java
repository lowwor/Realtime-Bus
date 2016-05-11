package com.lowwor.realtimebus.developer_settings;

import com.lowwor.realtimebus.BusApplication;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by lowworker on 2016/5/9 0009.
 */
public class BugReportProxyImpl implements BugReportProxy {

    private BusApplication busApplication;
    public static final String BUGLY_APPID = "900027065";

    public BugReportProxyImpl(BusApplication busApplication) {
        this.busApplication = busApplication;
    }

    @Override
    public void init() {
        Bugly.init(busApplication, BUGLY_APPID, false);
        CrashReport.initCrashReport(busApplication);
    }
}
