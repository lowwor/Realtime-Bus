package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.api.BusApiRepository;
import com.lowwor.realtimebus.model.Bus;
import com.lowwor.realtimebus.model.BusStation;
import com.lowwor.realtimebus.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.base.BaseFragment;
import com.lowwor.realtimebus.utils.Constants;
import com.lowwor.realtimebus.utils.NetworkUtils;
import com.lowwor.realtimebus.utils.RxUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lowworker on 2015/10/15.
 */
public class TrackFragment extends BaseFragment {

    @Bind(R.id.swipe_container)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.btn_try_again)
    Button btnTryAgain;
    @Bind(R.id.offline_container)
    LinearLayout mOfflineContainer;
    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @Bind(R.id.et_line_name)
    EditText etLineName;
    @Bind(R.id.fab_switch)
    FloatingActionButton fabSwitch;


    @Inject
    BusApiRepository mBusApiRepository;


    private String fromStation;
    private String toStation;
    private LinearLayoutManager mLinearLayoutManager;
    private List<BusStation> mStations;
    private BusStationAdapter mBusStationAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_track, container, false);
        ButterKnife.bind(this, fragmentView);
        Logger.i("onCreateView BaseFragment");

        initToolbar();
        restoreEdittextText();
        initSwipeRefresh();
        initRecyclerView();
        initDependencyInjector();
        loadStationsIfNetworkConnected();
        return fragmentView;
    }

    @OnClick(R.id.btn_query)
    public void onQuery() {
        saveEdittextText();
        loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.btn_try_again)
    public void onTryAgainClick() {
        loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.fab_switch)
    public void onFabSwitchClick() {
        switchDirection();
        getBus();
    }


    private void loadStationsIfNetworkConnected() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            getStations();

        } else {
            showHideOfflineLayout(true);
        }
    }

    private void getStations() {
        Logger.i("getStations");
        mSubscriptions.add(mBusApiRepository.searchLine(etLineName.getText().toString())
                .flatMap(new Func1<BusLineWrapper, Observable<BusStationWrapper>>() {
                    @Override
                    public Observable<BusStationWrapper> call(BusLineWrapper busLineWrapper) {
                        return mBusApiRepository.getStationByLineId(busLineWrapper.getData().get(1).id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BusStationWrapper>() {
                    @Override
                    public void onCompleted() {

                        Logger.i("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Logger.e("There was a problem loading the top stories " + e);
                        e.printStackTrace();
                        hideLoadingViews();
                    }

                    @Override
                    public void onNext(BusStationWrapper busStationWrapper) {
//                        Logger.i(busStationWrapper.getData().get(0).name);
                        hideLoadingViews();
                        setupBusStations(busStationWrapper.getData());
                        fromStation = mStations.get(0).name;
                        toStation = mStations.get(mStations.size() - 1).name;
                        getBus();
                    }
                }));
    }

    private void getBus() {
        Logger.i("getBus" + mStations.get(0).name);
        mSubscriptions.add(mBusApiRepository.getBusListOnRoad(etLineName.getText().toString().toUpperCase(), fromStation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BusWrapper>() {
                    @Override
                    public void onCompleted() {

                        Logger.i("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Logger.e("There was a problem loading bus on line " + e);
                        e.printStackTrace();
                        hideLoadingViews();
                    }

                    @Override
                    public void onNext(BusWrapper busWrapper) {
                        hideLoadingViews();

                        List<Bus> buses = busWrapper.getData();

                        for (BusStation busStation : mStations) {
                            busStation.buses = null;
                            for (Bus bus : buses) {
                                if (bus.currentStation.equals(busStation.name)) {

                                    if (busStation.buses != null) {
                                        busStation.buses.add(bus);
                                    } else {
                                        List<Bus> stationBuses = new ArrayList<>();
                                        stationBuses.add(bus);
                                        busStation.buses = stationBuses;
                                    }


                                }
                            }
                        }
                        refreshBuses();
                    }
                }));
    }

    private void switchDirection() {
        String temp = fromStation;
        fromStation = toStation;
        toStation = temp;
        Collections.reverse(mStations);
    }

    private void refreshBuses() {
        mBusStationAdapter.notifyDataSetChanged();
    }

    private void setupBusStations(List<BusStation> busStations) {
        mStations.clear();
        mStations.addAll(busStations);
        mBusStationAdapter.notifyDataSetChanged();
    }

    private void restoreEdittextText() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Constants.SP_BUS, Context.MODE_PRIVATE);
        String busLineName = mSharedPreferences.getString(Constants.KEY_SP_EDITTEXT, "3a");
        etLineName.setText(busLineName);
    }

    private void saveEdittextText() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences(Constants.SP_BUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(Constants.KEY_SP_EDITTEXT, etLineName.getText().toString());
        mEditor.commit();
    }

    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBus();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.hn_orange);
    }

    private void initRecyclerView() {

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mStations = new ArrayList<>();
        mBusStationAdapter = new BusStationAdapter(getActivity());
        mBusStationAdapter.setItems(mStations);
        mRecyclerview.setAdapter(mBusStationAdapter);
        mRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mBusStationAdapter.getItemCount();

                if (lastVisibleItem > totalItemCount - 4 && dy > 0) {

                }

            }
        });
    }

    private void initDependencyInjector() {
        ((MainActivity) getActivity()).trackComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSubscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(mSubscriptions);
    }


    @Override
    public void onPause() {
        super.onPause();


        RxUtils.unsubscribeIfNotNull(mSubscriptions);
    }

    private void initToolbar() {
//        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayShowTitleEnabled(true);
//            if (mUser != null) {
//                actionBar.setTitle(mUser);
//                actionBar.setDisplayHomeAsUpEnabled(true);
//            }
//        }
    }

    private void hideLoadingViews() {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineContainer.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mRecyclerview.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }
}
