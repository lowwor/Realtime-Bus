package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.ui.base.BasePresenter;

/**
 * Created by lowworker on 2016/4/23 0023.
 */
public abstract class TrackPresenter extends BasePresenter<TrackVista> {

    public abstract void addAlarmStation(String stationName);

    public abstract void removeAlarmStation(String stationName);

    public abstract void searchLineIfNetworkConnected(String lineName);

    public abstract void loadBusIfNetworkConnected();

    public abstract void switchDirection();


}
