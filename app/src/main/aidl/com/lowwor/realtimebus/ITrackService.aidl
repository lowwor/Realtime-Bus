// ITrackService.aidl
package com.lowwor.realtimebus;

// Declare any non-default types here with import statements

import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.ITrackCallback;

interface ITrackService {
//    /**
//     * Demonstrates some basic types that you can use as parameters
//     * and return values in AIDL.
//     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
//

    void stopAutoRefresh();

    void startAutoRefresh(String lineName,  String fromStation);

    void registerCallback(ITrackCallback callback);

    void unregisterCallback(ITrackCallback callback);

    void addAlarmStation(String stationName);

    void removeAlarmStation(String stationName);

    void clearAlarmStation();



}
