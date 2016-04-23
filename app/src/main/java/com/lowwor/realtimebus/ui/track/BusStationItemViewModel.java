package com.lowwor.realtimebus.ui.track;

/**
 * Created by lowworker on 2015/10/15.
 */

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.rx.RxTrackService;


public class BusStationItemViewModel extends BaseObservable {


    private final RxTrackService rxTrackService;
    private BusStation busStation;
    @Bindable
    private int busNumber = 0;


    public BusStationItemViewModel(BusStation busStation, RxTrackService rxTrackService) {
        this.busStation = busStation;
        this.rxTrackService = rxTrackService;
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
                if ( busStation.isAlarm) {
                    rxTrackService.addAlarmStation(busStation.name);
                } else {
                    rxTrackService.removeAlarmStation(busStation.name);
                }
                notifyPropertyChanged(BR.isAlarm);

            }
        };
    }





}

