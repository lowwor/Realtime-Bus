package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.ui.base.BasePresenter;

/**
 * Created by lowworker on 2016/4/23 0023.
 */
public abstract class TrackPresenter extends BasePresenter<TrackViewModel> {

    public abstract void loadBusIfNetworkConnected();

    public abstract void loadStationsIfNetworkConnected();

    public abstract void executeAutoRefresh();

    public abstract void switchDirection();

    public abstract boolean getAutoRefresh();

    public abstract void saveAutoRefresh(boolean isAutoRefresh);

    public abstract void addAlarmStation(String stationName);

    public abstract void removeAlarmStation(String stationName);

    public abstract void gotoSettings();

    public abstract void showShare();

}
