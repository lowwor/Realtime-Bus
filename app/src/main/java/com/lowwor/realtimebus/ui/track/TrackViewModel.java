package com.lowwor.realtimebus.ui.track;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.utils.BindableString;
import com.orhanobut.logger.Logger;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class TrackViewModel extends BaseObservable {


    private final RxTrackService rxTrackService;
    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading = true;


    @Bindable
    public BindableString text = new BindableString();
    public ObservableList<BusStationItemViewModel> mBusStations = new ObservableArrayList<>();

    public TrackViewModel(RxTrackService rxTrackService) {
        this.rxTrackService = rxTrackService;
    }

    public void setItems(List<BusStation> busStations) {
//        Logger.i("setItems: " + busStations);
        mBusStations.clear();
        for (BusStation busStation : busStations) {
            mBusStations.add(new BusStationItemViewModel(busStation,rxTrackService));
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
        }
    }


    /**
     * ItemView of a single type
     */
    public final ItemView itemViewStation = ItemView.of(BR.busStationItemViewModel, R.layout.item_station);




    public final ObservableList<String> lineNameItems = new ObservableArrayList<>();

    public void setAutoCompleteItems(List<String> historyItems) {
//        Logger.i("setAutoCompleteItems: " + historyItems);
        lineNameItems.clear();
        lineNameItems.addAll(historyItems);
    }


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
