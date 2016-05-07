package com.lowwor.realtimebus.data.api;

/**
 * Created by lowworker on 2015/10/14.
 */

import com.lowwor.realtimebus.data.model.postdata.PostGetBusListOnRoad;
import com.lowwor.realtimebus.data.model.postdata.PostGetStationByLineId;
import com.lowwor.realtimebus.data.model.postdata.PostSearchLine;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import rx.Observable;


/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApiRepository {
    private BusService busService;

    public BusApiRepository(BusService busService) {
        this.busService = busService;
    }

    public Observable<BusLineWrapper> searchLine(String key) {
        PostSearchLine postSearchLine = new PostSearchLine(key);
        return busService.searchLine(postSearchLine);
    }


    public Observable<BusStationWrapper> getStationByLineId(String lineId) {

        PostGetStationByLineId postGetStationByLineId = new PostGetStationByLineId(lineId);
        return busService.getStationByLineId(postGetStationByLineId);
    }

    public Observable<BusWrapper> getBusListOnRoad(String lineName,String fromStation) {
        PostGetBusListOnRoad postGetBusListOnRoad = new PostGetBusListOnRoad(lineName,fromStation);
        return busService.getBusListOnRoad(postGetBusListOnRoad);
    }



}
