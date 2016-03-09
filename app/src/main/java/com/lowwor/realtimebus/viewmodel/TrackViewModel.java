package com.lowwor.realtimebus.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.utils.BindableString;
import com.orhanobut.logger.Logger;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class TrackViewModel extends BaseObservable {


    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading = true;


    @Bindable
    public BindableString text = new BindableString();
    private final NotificationView notificationView;
    public ObservableList<BusStationItemViewModel> mBusStations = new ObservableArrayList<>();

    public TrackViewModel(NotificationView notificationView) {
        this.notificationView = notificationView;
    }

    public void setItems(List<BusStation> busStations) {
        Logger.i("setItems: " + busStations);
        mBusStations.clear();
        for (BusStation busStation : busStations) {
            mBusStations.add(new BusStationItemViewModel(busStation));
        }
    }

    public void setBuses(List<Bus> buses) {
        for (int i = 0; i < mBusStations.size(); i++) {
            BusStationItemViewModel busStationItemViewModel = mBusStations.get(i);
            busStationItemViewModel.resetBusNumber();
            for (Bus bus : buses) {
                if (bus.currentStation.equals(busStationItemViewModel.getBusStationName())) {
                    busStationItemViewModel.increaseBusNumber();
                }
            }
            if (busStationItemViewModel.getBusNumber() != 0 && busStationItemViewModel.getIsAlarm()) {
                notificationView.showNotification(busStationItemViewModel.getBusStationName());
            }
        }
    }


    /**
     * ItemView of a single type
     */
    public final ItemView itemViewStation = ItemView.of(BR.busStationItemViewModel, R.layout.item_station);


    public final ObservableList<String> lineNameItems = new ObservableArrayList<>();

    public void setText(String text) {
        this.text.set(text);
        notifyPropertyChanged(BR.trackViewModel);
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
        notifyPropertyChanged(BR.isOffline);
    }

    public boolean getIsOffline() {
        return isOffline;
    }


    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyPropertyChanged(BR.isLoading);
    }


    public boolean getIsLoading() {
        return isLoading;
    }
}
