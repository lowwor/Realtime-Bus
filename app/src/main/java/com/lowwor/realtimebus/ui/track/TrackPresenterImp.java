package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.domain.NetworkManager;
import com.orhanobut.logger.Logger;

import java.util.List;

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

    private NetworkManager networkManager;
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

    public TrackPresenterImp(NetworkManager networkManager, BusApiRepository busApiRepository, PreferencesHelper preferencesHelper, RxTrackService rxTrackService) {
        this.networkManager = networkManager;
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
            searchLineIfNetworkConnected(preferencesHelper.getLastQueryLine());
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
        if (networkManager.isNetworkAvailable()) {
            vista.showOffline(false);
            getBus();
        } else {
            vista.showOffline(true);
            vista.showLoading(false);
        }
    }

    @Override
    public void searchLineIfNetworkConnected(String lineName) {
        if (networkManager.isNetworkAvailable()) {
            vista.showOffline(false);
            searchLine(lineName.toUpperCase());
        } else {
            vista.showOffline(true);
            vista.showLoading(false);
        }
    }

    private void loadStationsIfNetworkConnected() {
        if (networkManager.isNetworkAvailable()) {
            vista.showOffline(false);
            getStations();
        } else {
            vista.showOffline(true);
            vista.showLoading(false);
        }
    }

    public void executeAutoRefresh() {
        rxTrackService.stopAutoRefresh();
        rxTrackService.startAutoRefresh(mLineName, fromStation);
    }


    @Override
    public void addAlarmStation(String stationName) {
        rxTrackService.addAlarmStation(stationName);
    }

    @Override
    public void removeAlarmStation(String stationName) {
        rxTrackService.removeAlarmStation(stationName);
    }


    @Override
    public void switchDirection() {
        switchStartFrom();
        loadStationsIfNetworkConnected();
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
                        vista.showError(ERROR_SEARCH_LINE);
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
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                vista.showLoading(true);
                            }
                        })
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
                                vista.showError(ERROR_SEARCH_LINE);
                                vista.showLoading(false);
                            }

                            @Override
                            public void onNext(List<BusStation> busStations) {
                                vista.showLoading(false);
                                vista.showStations(busStations);
                                saveAutoComplete();
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
        subscriptions.add(preferencesHelper.getAutoCompleteAsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<String>>() {
                    @Override
                    public void onCompleted() {
                        Logger.i("auto onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError() called with: " + "e = [" + e + "]");

                    }

                    @Override
                    public void onNext(List<String> strings) {
                        Logger.d("onNext() called with: " + "strings = [" + strings + "]");
                        vista.showSearchLineHistory(strings);
                    }
                }));
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
