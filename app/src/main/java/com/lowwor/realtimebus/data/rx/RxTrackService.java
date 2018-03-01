package com.lowwor.realtimebus.data.rx;

import com.lowwor.realtimebus.data.model.Bus;

import java.util.List;

import io.reactivex.Observable;


/**
 * Created by lowwor on 2016/3/8 0008.
 */
public interface RxTrackService {


    void stopAutoRefresh();

    void startAutoRefresh(String lineName, String fromStation);

    void addAlarmStation(String stationName);

    void removeAlarmStation(String stationName);

    void close();

    Observable<List<Bus>> getBusObservable();

}
