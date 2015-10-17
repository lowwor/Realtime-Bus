package com.lowwor.realtimebus.viewmodel;

/**
 * Created by lowworker on 2015/10/15.
 */

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.lowwor.realtimebus.model.BusStation;


public class BusStationViewModel extends BaseObservable {

    private Context context;
    private BusStation busStation;
    private Boolean isUserPosts;

    public BusStationViewModel(Context context, BusStation busStation ) {
        this.context = context;
        this.busStation = busStation;
    }



    public String getBusStationName() {
        return busStation.name;
    }

    public String getBusNumber() {
        return (busStation.buses==null||busStation.buses.size()==0) ? "0" :String.valueOf(busStation.buses.size());
    }

    public int getBusNumberVisibility() {
        return  (busStation.buses==null||busStation.buses.size()==0) ? View.GONE : View.VISIBLE;
    }








}

