package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.ui.base.BasePresenter;

/**
 * Created by lowworker on 2016/4/23 0023.
 */
public abstract class TrackPresenter extends BasePresenter<TrackVista> {

    public static final int ERROR_SEARCH_LINE = 1;
    public static final int ERROR_NO_BUS = 2;
    public static final int ERROR_ONLY_ONE_DIRECTION = 3;

    public abstract void addAlarmStation(String stationName);

    public abstract void removeAlarmStation(String stationName);

    public abstract void searchLineIfNetworkConnected(String lineName);

    public abstract void loadBusIfNetworkConnected();

    public abstract void switchDirection();


}
