package com.lowwor.realtimebus.ui.track;

import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusLine;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.domain.NetworkManager;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.tencent.bugly.crashreport.crash.c.e;

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
        getCompositeDisposable().add(busApiRepository.searchLine(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        vista.showLoading(true);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<BusLineWrapper, List<BusLine>>() {
                    @Override
                    public List<BusLine> apply(BusLineWrapper busLineWrapper) throws Exception {

                        if (busLineWrapper.getData().isEmpty()) {
                            throw new IllegalStateException("Bus line is null");
                        } else {
                            return busLineWrapper.getData();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<BusLine>>() {
                    @Override
                    public void onSuccess(List<BusLine> busLines) {
                        Logger.d("onNext() called with: " + "busLines = [" + busLines + "]");
                        mLineName = busLines.get(0).name;
                        preferencesHelper.saveLastQueryLine(mLineName);

                        firstStation = busLines.get(0).fromStation;
                        lastStation = busLines.get(0).toStation;
                        fromStation = getStartFrom() ? firstStation : lastStation;

                        mNormalLineId = busLines.get(0).id;
                        mReverseLineId = busLines.get(1).id;
                        mLineId = getStartFrom() ? mNormalLineId : mReverseLineId;
                        getStations();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        vista.showLoading(false);
                        vista.showError(ERROR_SEARCH_LINE);
                    }
                }));
    }

    private void getStations() {
        getCompositeDisposable().add(
                busApiRepository.getStationByLineId(mLineId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                vista.showLoading(true);
                            }
                        })
                        .observeOn(Schedulers.io())
                        .map(new Function<BusStationWrapper, List<BusStation>>() {
                            @Override
                            public List<BusStation> apply(BusStationWrapper busStationWrapper) throws Exception {
                                return busStationWrapper.getData();
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<BusStation>>() {
                            @Override
                            public void onSuccess(List<BusStation> busStations) {
                                vista.showLoading(false);
                                vista.showStations(busStations);
                                saveAutoComplete();
                                loadBusIfNetworkConnected();
                                executeAutoRefresh();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Logger.e("There was a problem loading the top stories " + e);
                                throwable.printStackTrace();
                                vista.showError(ERROR_SEARCH_LINE);
                                vista.showLoading(false);
                            }
                        }));
    }

    private void getBus() {
        getCompositeDisposable().add(busApiRepository.getBusListOnRoad(mLineName, fromStation)
                .map(new Function<BusWrapper, List<Bus>>() {
                    @Override
                    public List<Bus> apply(BusWrapper busWrapper) throws Exception {
                        return busWrapper.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getBusObserver()));
    }


    private DisposableSingleObserver<List<Bus>> getBusObserver() {
        return new DisposableSingleObserver<List<Bus>>() {
            @Override
            public void onSuccess(List<Bus> buses) {
                vista.showLoading(false);
                vista.showBuses(buses);
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.e("There was a problem loading bus on line " + e);
                throwable.printStackTrace();
                vista.showLoading(false);
            }
        };
    }

    private void initTrackService() {
        Logger.d("initTrackService() called with: " + "");
        getCompositeDisposable().add(rxTrackService.getBusObservable()
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        rxTrackService.close();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Bus>>() {
                    @Override
                    public void onNext(List<Bus> buses) {
                        vista.showLoading(false);
                        vista.showBuses(buses);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        vista.showLoading(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private void initAutoComplete() {
        vista.showInitLineName(preferencesHelper.getLastQueryLine());
        getCompositeDisposable().add(preferencesHelper.getAutoCompleteAsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<String>>() {
                    @Override
                    public void onNext(List<String> strings) {
                        Logger.d("onNext() called with: " + "strings = [" + strings + "]");
                        vista.showSearchLineHistory(strings);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Logger.d("onError() called with: " + "throwable = [" + throwable + "]");

                    }

                    @Override
                    public void onComplete() {

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
