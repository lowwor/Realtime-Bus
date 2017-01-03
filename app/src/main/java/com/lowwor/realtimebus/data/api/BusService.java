package com.lowwor.realtimebus.data.api;

import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
/**
 * Created by lowworker on 2015/10/14.
 */
public interface BusService {

    String BASE_URL = "http://www.zhbuswx.com/Handlers/";


    @GET("BusQuery.ashx")
    @Headers("Referer: http://www.zhbuswx.com/busline/BusQuery.html")
    Single<BusLineWrapper> searchLine(@Query("handlerName") String handlerName, @Query("key") String key, @Query("_") long timestamp);


    @GET("BusQuery.ashx")
    @Headers("Referer: http://www.zhbuswx.com/busline/BusQuery.html")
    Single<BusStationWrapper> getStationByLineId(@Query("handlerName") String handlerName, @Query("lineId") String lineId, @Query("_") long timestamp);


    @GET("BusQuery.ashx")
    @Headers("Referer: http://www.zhbuswx.com/busline/BusQuery.html")
    Single<BusWrapper> getBusListOnRoad(@Query("handlerName") String handlerName, @Query("lineName") String lineName, @Query("fromStation") String fromStation, @Query("_") long timestamp);
}
