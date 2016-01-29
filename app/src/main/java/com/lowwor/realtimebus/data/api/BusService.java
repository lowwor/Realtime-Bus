package com.lowwor.realtimebus.data.api;

import com.lowwor.realtimebus.data.model.postdata.PostGetBusListOnRoad;
import com.lowwor.realtimebus.data.model.postdata.PostGetStationByLineId;
import com.lowwor.realtimebus.data.model.postdata.PostSearchLine;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lowworker on 2015/10/14.
 */
public interface BusService {

    public final String BASE_URL = "http://www.zhbuswx.com/BusLine/WS.asmx/";


    @POST("SearchLine")
    Observable<BusLineWrapper> searchLine( @Body PostSearchLine postSearchLine);


    @POST("LoadStationByLineId")
    Observable<BusStationWrapper> getStationByLineId( @Body PostGetStationByLineId postGetStationByLineId);


    @POST("GetBusListOnRoad")
    Observable<BusWrapper> getBusListOnRoad( @Body PostGetBusListOnRoad postGetBusListOnRoad);
}
