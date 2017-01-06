package com.lowwor.realtimebus.data.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Single;
import retrofit2.http.Query;
import retrofit2.mock.BehaviorDelegate;

/**
 * Created by lowworker on 2015/10/14.
 */
public class MockBusService implements BusService {

    private final Gson gson;
    private Context context;
    private BehaviorDelegate<BusService> delegate;

    public MockBusService(Context context, BehaviorDelegate<BusService> delegate) {
        this.context = context;

        this.delegate = delegate;
        gson = new Gson();
    }

    @Override
    public Single<BusLineWrapper> searchLine(@Query("handlerName") String handlerName, @Query("key") String key) {
        BusLineWrapper busLineWrapper = new BusLineWrapper();
        try {
            InputStream inputStream = context.getResources().getAssets().open("BusLines.json");
            busLineWrapper = gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), BusLineWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delegate.returningResponse(busLineWrapper).searchLine(handlerName, key);
    }


    @Override
    public Single<BusStationWrapper> getStationByLineId(@Query("handlerName") String handlerName, @Query("lineId") String lineId) {
        BusStationWrapper busStationWrapper = new BusStationWrapper();
        try {
            InputStream inputStream = context.getResources().getAssets().open("BusStations.json");
            busStationWrapper = gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), BusStationWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delegate.returningResponse(busStationWrapper).getStationByLineId(handlerName, lineId);
    }


    @Override
    public Single<BusWrapper> getBusListOnRoad(@Query("handlerName") String handlerName, @Query("lineName") String lineName, @Query("fromStation") String fromStation) {
        BusWrapper busWrapper = new BusWrapper();
        try {
            InputStream inputStream = context.getResources().getAssets().open("Buses.json");
            busWrapper = gson.fromJson(new JsonReader(new InputStreamReader(inputStream)), BusWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delegate.returningResponse(busWrapper).getBusListOnRoad(handlerName, lineName, fromStation);

    }
}
