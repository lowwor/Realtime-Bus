// ITrackService.aidl
package com.lowwor.realtimebus;

// Declare any non-default types here with import statements

import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.ITrackCallback;

interface ITrackService {

    void stopAutoRefresh();

    void startAutoRefresh(String lineName,  String fromStation,int interval);

    //Not safe for multi thread SharedPreferences
    void setShowNotification(boolean shouldShow);

    void setShowPopupNotification(boolean shouldShow);

    void registerCallback(ITrackCallback callback);

    void unregisterCallback(ITrackCallback callback);

    void addAlarmStation(String stationName);

    void removeAlarmStation(String stationName);

    void clearAlarmStation();



}
