package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

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

    public TrackPresenterImp(Context context,  BusApiRepository busApiRepository, PreferencesHelper preferencesHelper, RxTrackService rxTrackService) {
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
            loadStationsIfNetworkConnected();
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
            viewModel.setIsOffline(false);
            getBus();
        } else {
            viewModel.setIsOffline(true);
            viewModel.setIsLoading(false);
        }
    }

    @Override
    public void loadStationsIfNetworkConnected() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            viewModel.setIsOffline(false);
            searchLine();
        } else {
            viewModel.setIsOffline(true);
            viewModel.setIsLoading(false);
        }
    }


    @Override
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

    @Override
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

    @Override
    public void gotoSettings() {
       context.startActivity(new Intent(context, SettingsActivity.class));
    }

    @Override
    public void showShare() {
        ShareUtils.share(context);
    }

    private void searchLine() {
        busApiRepository.searchLine(viewModel.text.get())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        viewModel.setIsLoading(true);
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
                        viewModel.setIsLoading(false);
                        Toast.makeText(context, "找不到线路,请重试！", Toast.LENGTH_SHORT).show();
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
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Subscriber<BusStationWrapper>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Logger.e("There was a problem loading the top stories " + e);
                                e.printStackTrace();
                                Toast.makeText(context, "找不到线路,请重试！", Toast.LENGTH_SHORT).show();
                                viewModel.setIsLoading(false);
                            }

                            @Override
                            public void onNext(BusStationWrapper busStationWrapper) {
                                viewModel.setIsLoading(false);
                                setupBusStations(busStationWrapper.getData());
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getBusSubscriber()));
    }


    private Subscriber<BusWrapper> getBusSubscriber() {
        return new Subscriber<BusWrapper>() {
            @Override
            public void onCompleted() {

//                Logger.i("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

                Logger.e("There was a problem loading bus on line " + e);
                e.printStackTrace();
                viewModel.setIsLoading(false);
            }

            @Override
            public void onNext(BusWrapper busWrapper) {
//                Logger.i("onNext");
                viewModel.setIsLoading(false);
                viewModel.setBuses(busWrapper.getData());
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
                        // TODO: 2016/3/3 0003 move to view model
//                        Logger.i("auto onNext" + strings.toString());

                        viewModel.setAutoCompleteItems(new ArrayList<String>(strings));
                    }
                }));
    }


    private void initTrackService() {
        Logger.d("initTrackService() called with: " + "");

        Subscriber<List<Bus>> subscriber = new Subscriber<List<Bus>>() {
            @Override
            public void onCompleted() {
                Logger.d("onCompleted() called with: " + "");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d("onError() called with: " + "e = [" + e + "]");
            }

            @Override
            public void onNext(List<Bus> buses) {
//                Logger.d("onNext() called with: " + "buses = [" + buses + "]");
                viewModel.setIsLoading(false);
                viewModel.setBuses(buses);
            }
        };
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
        viewModel.setText(preferencesHelper.getLastQueryLine());
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

    public void switchDirection() {
        switchStartFrom();
        getStations();
    }


    private void setupBusStations(List<BusStation> busStations) {
        viewModel.setItems(busStations);
    }

    private void saveAutoComplete() {
//        Logger.i("save auto");
        preferencesHelper.saveAutoCompleteItem(mLineName);
    }


}
