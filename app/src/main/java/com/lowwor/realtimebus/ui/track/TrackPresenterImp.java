package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.ui.settings.SettingsActivity;
import com.lowwor.realtimebus.utils.NetworkUtils;
import com.lowwor.realtimebus.utils.ShareUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by lowworker on 2016/4/22 0022.
 */
public class TrackPresenterImp extends TrackPresenter {

    private Context context;
    private BusApiRepository busApiRepository;
    private PreferencesHelper preferencesHelper;
    private RxTrackService rxTrackService;
    private String mNormalLineId;
    private String mReverseLineId;
    private String mLineId;
    private String mLineName;
    private String fromStation;
    private String firstStation;
    private String lastStation;
    private boolean isFirstIn = true;

    public TrackPresenterImp(Context context, BusApiRepository busApiRepository, PreferencesHelper preferencesHelper, RxTrackService rxTrackService) {
        this.context = context;
        this.busApiRepository = busApiRepository;
        this.preferencesHelper = preferencesHelper;
        this.rxTrackService = rxTrackService;
    }

    @Override
    public void onStart() {
        super.onStart();
        initTrackService();
        initAutoComplete();
        if (isFirstIn) {
            isFirstIn = false;
            loadStationsIfNetworkConnected(vista.getLineName());
        } else {
            loadBusIfNetworkConnected();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void loadBusIfNetworkConnected() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            vista.showOffline(false);
            getBus();
        } else {
            vista.showOffline(true);
            vista.showLoading(false);
        }
    }
    @Override
    public void loadStationsIfNetworkConnected(String lineName) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            vista.showOffline(false);
            searchLine(lineName);
        } else {
            vista.showOffline(true);
            vista.showLoading(false);
        }
    }

    public void executeAutoRefresh() {
        rxTrackService.stopAutoRefresh();
        if (getAutoRefresh()) {
            rxTrackService.startAutoRefresh(mLineName, fromStation);
        }
    }

    @Override
    public boolean getAutoRefresh() {
        return preferencesHelper.getAutoRefresh();
    }

    public void saveAutoRefresh(boolean isAutoRefresh) {
        preferencesHelper.saveAutoRefresh(isAutoRefresh);
    }

    @Override
    public void addAlarmStation(String stationName) {
        rxTrackService.addAlarmStation(stationName);
    }

    @Override
    public void removeAlarmStation(String stationName) {
        rxTrackService.removeAlarmStation(stationName);
    }

    public void gotoSettings() {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public void showShare() {
        ShareUtils.share(context);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.auto_refresh:
                        saveAutoRefresh(!item.isChecked());
                        item.setChecked(getAutoRefresh());
                        executeAutoRefresh();
                        break;
                    case R.id.settings:
                        gotoSettings();
                        break;
                    case R.id.share:
                        showShare();
                        break;
                }
        return true;
    }

    @Override
    public void switchDirection() {
        switchStartFrom();
        getStations();
    }


    private void searchLine(String name) {
        busApiRepository.searchLine(name)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        vista.showLoading(true);
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<BusLineWrapper, Observable<BusLineWrapper>>() {
                    @Override
                    public Observable<BusLineWrapper> call(BusLineWrapper busLineWrapper) {
                        if (busLineWrapper.getData().isEmpty()) {
                            return Observable.error(new IndexOutOfBoundsException());
                        } else {
                            return Observable.just(busLineWrapper);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BusLineWrapper>() {
                    @Override
                    public void onCompleted() {
                        getStations();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError() called with: " + "e = [" + e + "]");
                        vista.showLoading(false);
                        vista.showError("找不到线路,请重试！");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(BusLineWrapper busLineWrapper) {
                        Logger.d("onNext() called with: " + "busLineWrapper = [" + busLineWrapper + "]");
                        mLineName = busLineWrapper.getData().get(0).name;
                        preferencesHelper.saveLastQueryLine(mLineName);

                        firstStation = busLineWrapper.getData().get(0).fromStation;
                        lastStation = busLineWrapper.getData().get(0).toStation;
                        fromStation = getStartFrom() ? firstStation : lastStation;

                        mNormalLineId = busLineWrapper.getData().get(0).id;
                        mReverseLineId = busLineWrapper.getData().get(1).id;
                        mLineId = getStartFrom() ? mNormalLineId : mReverseLineId;

                    }
                });
    }

    private void getStations() {
        subscriptions.add(
                busApiRepository.getStationByLineId(mLineId)
                        .map(new Func1<BusStationWrapper, List<BusStation>>() {
                            @Override
                            public List<BusStation> call(BusStationWrapper busStationWrapper) {
                                return busStationWrapper.getData();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<BusStation>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("There was a problem loading the top stories " + e);
                                e.printStackTrace();
                                vista.showError("找不到线路,请重试！");
                                vista.showLoading(false);
                            }

                            @Override
                            public void onNext(List<BusStation> busStations) {
                                vista.showLoading(false);
                                vista.showStations(busStations);
                                saveAutoComplete();
                                refreshAutoComplete();
                                loadBusIfNetworkConnected();
                                executeAutoRefresh();
                            }
                        }));
    }

    private void getBus() {
//        Logger.i("getBus" + fromStation);
        subscriptions.add(busApiRepository.getBusListOnRoad(mLineName, fromStation)
                .map(new Func1<BusWrapper, List<Bus>>() {
                    @Override
                    public List<Bus> call(BusWrapper busWrapper) {
                        return busWrapper.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getBusSubscriber()));
    }


    private Subscriber<List<Bus>> getBusSubscriber() {
        return new Subscriber<List<Bus>>() {
            @Override
            public void onCompleted() {

//                Logger.i("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

                Logger.e("There was a problem loading bus on line " + e);
                e.printStackTrace();
                vista.showLoading(false);
            }

            @Override
            public void onNext(List<Bus> buses) {
//                Logger.i("onNext");
                vista.showLoading(false);
                vista.showBuses(buses);
            }
        };
    }

    private void refreshAutoComplete() {
        //don't know why didn't auto refresh
        subscriptions.add(preferencesHelper.getAutoCompleteAsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Set<String>>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("auto onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i(" auto e");

                    }

                    @Override
                    public void onNext(Set<String> strings) {
                        Logger.d("onNext() called with: " + "strings = [" + strings + "]");
                        vista.showSearchLineHistory(new ArrayList<>(strings));
                    }
                }));
    }


    private void initTrackService() {
        Logger.d("initTrackService() called with: " + "");

        Subscriber<List<Bus>> subscriber = getBusSubscriber();
        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                rxTrackService.close();
            }
        }));
        Subscription autoRefreshSubscription = rxTrackService.getBusObservable().subscribe(
                subscriber);
        subscriptions.add(autoRefreshSubscription);

    }

    private void initAutoComplete() {
        vista.showInitLineName(preferencesHelper.getLastQueryLine());
        refreshAutoComplete();
    }

    private void switchStartFrom() {
        preferencesHelper.saveStartFromFirst(!getStartFrom());
        boolean startFromFirst = preferencesHelper.getIsStartFromFirst();
        mLineId = startFromFirst ? mNormalLineId : mReverseLineId;
        fromStation = startFromFirst ? firstStation : lastStation;
    }

    private boolean getStartFrom() {
        return preferencesHelper.getIsStartFromFirst();
    }


    private void saveAutoComplete() {
//        Logger.i("save auto");
        preferencesHelper.saveAutoCompleteItem(mLineName);
    }


}
