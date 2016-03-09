package com.lowwor.realtimebus.data.rx;

import com.lowwor.realtimebus.data.model.Bus;

import java.util.List;

import rx.Observable;

/**
 * Created by lowworker on 2016/3/8 0008.
 */
public interface RxTrackService {


    void stopAutoRefresh();

    void startAutoRefresh(String lineName, String fromStation);

    void close();

    Observable<List<Bus>> getBusObservable();

}
