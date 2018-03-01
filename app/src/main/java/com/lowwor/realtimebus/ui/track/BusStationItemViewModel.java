package com.lowwor.realtimebus.ui.track;

/**
 * Created by lowwor on 2015/10/15.
 */

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.data.model.BusStation;


public class BusStationItemViewModel extends BaseObservable {


    private BusStation busStation;
    @Bindable
    private int busNumber = 0;
    @Bindable
    private boolean isAlarm = false;


    public BusStationItemViewModel(BusStation busStation) {
        this.busStation = busStation;
    }


    public String getBusStationName() {
        return busStation.name;
    }


    public int getBusNumber() {
        return busNumber;
    }


    public void setBusNumber(int busNumber) {
        this.busNumber = busNumber;
        notifyPropertyChanged(BR.busNumber);
    }


    public boolean getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(boolean isAlarm){
        this.isAlarm = isAlarm;
        notifyPropertyChanged(BR.isAlarm);
    }



}

