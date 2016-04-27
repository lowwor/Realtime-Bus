package com.lowwor.realtimebus.ui.track;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

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


    private TrackPresenter trackPresenter;
    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading = true;



    @Bindable
    public BindableString text = new BindableString();
    public ObservableList<BusStationItemViewModel> mBusStations = new ObservableArrayList<>();

    public TrackViewModel(TrackPresenter trackPresenter) {
        this.trackPresenter = trackPresenter;
    }

    public void setItems(List<BusStation> busStations) {
//        Logger.i("setItems: " + busStations);
        mBusStations.clear();
        for (BusStation busStation : busStations) {
            mBusStations.add(new BusStationItemViewModel(busStation,trackPresenter));
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


    public View.OnClickListener onQueryClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPresenter.loadStationsIfNetworkConnected();
            }
        };
    }


    public View.OnClickListener  onTryAgainClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPresenter.loadStationsIfNetworkConnected();
            }
        };
    }


    public View.OnClickListener  onFabSwitchClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackPresenter.switchDirection();
                trackPresenter.loadBusIfNetworkConnected();
            }
        };
    }

    public SwipeRefreshLayout.OnRefreshListener onRefresh(){
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.d("onRefresh() called with: " + "");
                trackPresenter.loadBusIfNetworkConnected();
            }
        };
    }

    public Toolbar.OnMenuItemClickListener onMenuItemClick(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.auto_refresh:
                        trackPresenter.saveAutoRefresh(!item.isChecked());
                        item.setChecked(trackPresenter.getAutoRefresh());
                        trackPresenter.executeAutoRefresh();
                        break;
                    case R.id.settings:
                        trackPresenter.gotoSettings();
                        break;
                    case R.id.share:
                        trackPresenter.showShare();
                        break;
                }
                return true;
            }
        };
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
