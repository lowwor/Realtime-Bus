package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.Toast;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.ui.settings.SettingsActivity;
import com.lowwor.realtimebus.utils.BindableString;
import com.lowwor.realtimebus.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class TrackViewModel extends BaseObservable implements TrackVista {


    private Context context;
    private TrackPresenter trackPresenter;
    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading = true;


    @Bindable
    public BindableString text = new BindableString();
    public ObservableList<BusStationItemViewModel> mBusStations = new ObservableArrayList<>();

    public TrackViewModel(Context context, TrackPresenter trackPresenter) {
        this.context = context;
        this.trackPresenter = trackPresenter;
    }

    public void setItems(List<BusStation> busStations) {
//        Logger.i("setItems: " + busStations);
        mBusStations.clear();
        for (BusStation busStation : busStations) {
            mBusStations.add(new BusStationItemViewModel(busStation, trackPresenter));
        }
    }

    public void setBuses(List<Bus> buses) {
        for (int i = 0; i < mBusStations.size(); i++) {
            BusStationItemViewModel busStationItemViewModel = mBusStations.get(i);
            int busNumber = 0;
            for (Bus bus : buses) {
                if (bus.currentStation.equals(busStationItemViewModel.getBusStationName())) {
                    busNumber++;
                }
            }
            if (busStationItemViewModel.getBusNumber() != 0 && busNumber == 0) {
                busStationItemViewModel.setBusNumber(0);
            } else if (busNumber != 0) {
                busStationItemViewModel.setBusNumber(busNumber);
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

    @Override
    public void showLoading(boolean isLoading) {
        setIsLoading(isLoading);
    }

    @Override
    public void showError(String erroMsg) {
        Toast.makeText(context, erroMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBuses(List<Bus> buses) {
        setBuses(buses);
    }

    @Override
    public void showStations(List<BusStation> busStations) {
        setItems(busStations);
    }

    @Override
    public void showOffline(boolean isOffline) {
        setIsOffline(isOffline);
    }

    @Override
    public void showSearchLineHistory(List<String> items) {
        setAutoCompleteItems(new ArrayList<String>(items));
    }

    @Override
    public void showInitLineName(String lineName) {
        setText(lineName);
    }

    @Override
    public void showShare() {
        ShareUtils.share(context);
    }

    @Override
    public void gotoSettings() {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }
}
