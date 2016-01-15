package com.lowwor.realtimebus.viewmodel;

/**
 * Created by lowworker on 2015/10/15.
 */

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.data.model.BusStation;


public class BusStationViewModel extends BaseObservable {

    private Context context;
    private BusStation busStation;

    public BusStationViewModel(Context context, BusStation busStation ) {
        this.context = context;
        this.busStation = busStation;
    }



    public String getBusStationName() {
        return busStation.name;
    }

    public int getBusNumber() {
        return (busStation.buses==null||busStation.buses.size()==0) ? 0 :busStation.buses.size();
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

