package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.Toast;

import com.lowwor.realtimebus.BR;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemBinding;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class TrackViewModel extends BaseObservable implements TrackVista {


    /**
     * ItemView of a single type
     */
    public final ObservableList<String> lineNameItems = new ObservableArrayList<>();
    @Bindable
    public String text;
    public ObservableList<BusStationItemViewModel> mBusStations = new ObservableArrayList<>();
    private Context context;
    private TrackPresenter trackPresenter;
    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading = true;

    public OnBusStationClickListener busStationListener = new OnBusStationClickListener() {
        @Override
        public void onAlarmClick(BusStationItemViewModel item) {

            item.setIsAlarm(!item.getIsAlarm());
            if (item.getIsAlarm()) {
                trackPresenter.addAlarmStation(item.getBusStationName());
            } else {
                trackPresenter.removeAlarmStation(item.getBusStationName());
            }
        }
    };

    public ItemBinding<BusStationItemViewModel> itemViewStation = ItemBinding.<BusStationItemViewModel>of(BR.busStationItemViewModel, R.layout.item_station).bindExtra(BR.busStationListener, busStationListener);
    
    public TrackViewModel(Context context, TrackPresenter trackPresenter) {
        this.context = context;
        this.trackPresenter = trackPresenter;
    }

    public void setItems(List<BusStation> busStations) {
//        Logger.i("setItems: " + busStations);
        mBusStations.clear();
        for (BusStation busStation : busStations) {
            mBusStations.add(new BusStationItemViewModel(busStation));
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

    public void setAutoCompleteItems(List<String> historyItems) {
//        Logger.i("setAutoCompleteItems: " + historyItems);
        lineNameItems.clear();
        lineNameItems.addAll(historyItems);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    public boolean getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
        notifyPropertyChanged(BR.isOffline);
    }

    public boolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyPropertyChanged(BR.isLoading);
    }

    @Override
    public void showLoading(boolean isLoading) {
        setIsLoading(isLoading);
    }

    @Override
    public void showError(int errorType) {
        String errorMsg;
        switch (errorType){
            case TrackPresenter.ERROR_SEARCH_LINE:
                errorMsg = context.getString(R.string.error_search_line);
                break;
            case TrackPresenter.ERROR_NO_BUS:
                errorMsg = context.getString(R.string.error_no_bus);
                break;
            case TrackPresenter.ERROR_ONLY_ONE_DIRECTION:
                errorMsg = context.getString(R.string.error_only_one_direction);
                break;
            default:
                errorMsg = context.getString(R.string.error_unknown);
                break;
        }
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
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


}
