package com.lowwor.realtimebus.ui.track;

import android.util.Log;

import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusLine;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.data.rx.RxTrackService;
import com.lowwor.realtimebus.domain.NetworkInteractor;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.reactivex.Notification;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lowworker on 2016/4/22 0022.
 */
public class TrackPresenterImp extends TrackPresenter {
    private static final String TAG = "TrackPresenterImp";
    private NetworkInteractor networkInteractor;
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

    public TrackPresenterImp(NetworkInteractor networkInteractor, BusApiRepository busApiRepository, PreferencesHelper preferencesHelper, RxTrackService rxTrackService) {
        this.networkInteractor = networkInteractor;
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
        getCompositeDisposable()
                .add(networkInteractor.hasNetworkConnectionCompletable()
                        .andThen(
                                busApiRepository.getBusListOnRoad(mLineName, fromStation)
                                        .map(new Function<BusWrapper, List<Bus>>() {
                                            @Override
                                            public List<Bus> apply(BusWrapper busWrapper) throws Exception {
                                                return busWrapper.getData();
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                        )
                        .subscribeWith(getBusObserver()));
    }

    @Override
    public void searchLineIfNetworkConnected(String lineName) {
        getCompositeDisposable()
                .add(networkInteractor.hasNetworkConnectionCompletable()
                        .andThen(busApiRepository.searchLine(lineName.toUpperCase())
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
                                .observeOn(AndroidSchedulers.mainThread()))
                        .doOnSuccess(new Consumer<List<BusLine>>() {
                            @Override
                            public void accept(List<BusLine> busLines) throws Exception {
                                mLineName = busLines.get(0).name;
                                preferencesHelper.saveLastQueryLine(mLineName);

                                firstStation = busLines.get(0).fromStation;
                                lastStation = busLines.get(0).toStation;
                                fromStation = getStartFrom() ? firstStation : lastStation;

                                mNormalLineId = busLines.get(0).id;
                                mReverseLineId = busLines.get(1).id;
                                mLineId = getStartFrom() ? mNormalLineId : mReverseLineId;
                            }
                        })
                        .flatMap(new Function<List<BusLine>, SingleSource<List<BusStation>>>() {
                            @Override
                            public SingleSource<List<BusStation>> apply(List<BusLine> busLines) throws Exception {
                                return getStationsSingle();
                            }
                        })
                        .subscribeWith(getStationsObserver()));
    }

    private void loadStationsIfNetworkConnected() {
        getCompositeDisposable()
                .add(networkInteractor.hasNetworkConnectionCompletable()
                        .andThen(getStationsSingle())
                        .subscribeWith(getStationsObserver()));
    }

    public void restartAutoRefreshService() {
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

    private Single<List<BusStation>> getStationsSingle() {
        return busApiRepository.getStationByLineId(mLineId)
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
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableSingleObserver<List<BusStation>> getStationsObserver() {
        return new DisposableSingleObserver<List<BusStation>>() {
            @Override
            public void onSuccess(List<BusStation> busStations) {
                vista.showLoading(false);
                vista.showStations(busStations);
                vista.showOffline(false);
                preferencesHelper.saveAutoCompleteItem(mLineName);
                loadBusIfNetworkConnected();
                restartAutoRefreshService();
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                vista.showLoading(false);
                vista.showError(ERROR_SEARCH_LINE);
                if (throwable instanceof NetworkInteractor.NetworkUnavailableException) {
                    vista.showOffline(true);
                } else {
                    vista.showOffline(false);
                }
            }
        };
    }

    private DisposableSingleObserver<List<Bus>> getBusObserver() {
        return new DisposableSingleObserver<List<Bus>>() {
            @Override
            public void onSuccess(List<Bus> buses) {
                vista.showOffline(false);
                vista.showLoading(false);
                if (buses == null || buses.isEmpty()) {
                    vista.showError(ERROR_NO_BUS);
                } else {
                    vista.showBuses(buses);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Logger.e("There was a problem loading bus on line " + throwable);
                throwable.printStackTrace();
                vista.showLoading(false);
                if (throwable instanceof NetworkInteractor.NetworkUnavailableException) {
                    vista.showOffline(true);
                } else {
                    vista.showOffline(false);
                }
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
                .materialize()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Notification<List<Bus>>>() {
                    @Override
                    public void onNext(Notification<List<Bus>> listNotification) {
                        vista.showOffline(false);
                        vista.showLoading(false);
                        if (listNotification.isOnNext()) {
                            List<Bus> buses = listNotification.getValue();
                            if (buses == null ||buses.isEmpty()) {
                                vista.showError(ERROR_NO_BUS);
                            } else {
                                vista.showBuses(buses);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
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


}
