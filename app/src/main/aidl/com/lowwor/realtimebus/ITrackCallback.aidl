// ITrackCallback.aidl
package com.lowwor.realtimebus;

// Declare any non-default types here with import statements

import com.lowwor.realtimebus.data.model.Bus;

interface ITrackCallback {

    void onBusArrived(in List<Bus> buses);

    void onFail( String errorMessage);

}
