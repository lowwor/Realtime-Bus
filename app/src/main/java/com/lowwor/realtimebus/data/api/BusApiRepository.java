package com.lowwor.realtimebus.data.api;

/**
 * Created by lowworker on 2015/10/14.
 */

import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import rx.Observable;


/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApiRepository {
    private BusService busService;
    private static final String HANDLER_GET_STATION_LIST = "GetStationList";
    private static final String HANDLER_GET_LINELIST_BY_LINENAME = "GetLineListByLineName";
    private static final String HANDLER_GET_BUSLIST_ONROAD = "GetBusListOnRoad";


    public BusApiRepository(BusService busService) {
        this.busService = busService;
    }

    public Observable<BusLineWrapper> searchLine(String key) {
        return busService.searchLine(HANDLER_GET_LINELIST_BY_LINENAME,key);
    }


    public Observable<BusStationWrapper> getStationByLineId(String lineId) {
        return busService.getStationByLineId(HANDLER_GET_STATION_LIST,lineId);
    }

    public Observable<BusWrapper> getBusListOnRoad(String lineName,String fromStation) {
        return busService.getBusListOnRoad(HANDLER_GET_BUSLIST_ONROAD,lineName,fromStation);
    }



}
