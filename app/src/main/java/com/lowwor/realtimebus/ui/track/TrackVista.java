package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.ui.base.Vista;

import java.util.List;

/**
 * Created by lowwor on 2016/4/28 0028.
 */
public interface TrackVista extends Vista {

    void showLoading(boolean isLoading);

    void showOffline(boolean isOffline);

    void showError(int errorType);

    void showBuses(List<Bus> buses);

    void showStations(List<BusStation> busStations);

    void showInitLineName(String lineName);

    void showSearchLineHistory(List<String> items);


}
