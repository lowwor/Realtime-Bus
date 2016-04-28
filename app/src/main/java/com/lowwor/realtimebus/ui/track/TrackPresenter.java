package com.lowwor.realtimebus.ui.track;

import android.view.MenuItem;
import android.view.View;

import com.lowwor.realtimebus.ui.base.BasePresenter;

/**
 * Created by lowworker on 2016/4/23 0023.
 */
public abstract class TrackPresenter extends BasePresenter<TrackVista> {

    public abstract boolean getAutoRefresh();

    public abstract void addAlarmStation(String stationName);

    public abstract void removeAlarmStation(String stationName);

    public abstract void onQueryClick(View v);

    public abstract void onTryAgainClick(View v);

    public abstract void onFabSwitchClick(View v);

    public abstract void onRefresh();

    public abstract boolean onMenuItemClick(MenuItem item);


}
