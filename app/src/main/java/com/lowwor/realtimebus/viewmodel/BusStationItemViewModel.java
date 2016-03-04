package com.lowwor.realtimebus.viewmodel;

/**
 * Created by lowworker on 2015/10/15.
 */

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.data.model.BusStation;


public class BusStationItemViewModel extends BaseObservable {


    private BusStation busStation;
    @Bindable
    private int busNumber = 0;


    public BusStationItemViewModel(BusStation busStation ) {
        this.busStation = busStation;
    }



    public String getBusStationName() {
        return busStation.name;
    }


    public int getBusNumber() {
        return busNumber;
    }

    public void resetBusNumber() {
        if (busNumber!=0) {
            busNumber = 0;
            notifyPropertyChanged(BR.busNumber);
        }
    }

    public void increaseBusNumber() {
        busNumber++;
        notifyPropertyChanged(BR.busNumber);
    }



    @Bindable
    public boolean getIsAlarm() {
        return busStation.isAlarm;
    }


    public View.OnClickListener onClickAlarm(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busStation.isAlarm = !busStation.isAlarm;
                notifyPropertyChanged(BR.isAlarm);

            }
        };
    }





}

