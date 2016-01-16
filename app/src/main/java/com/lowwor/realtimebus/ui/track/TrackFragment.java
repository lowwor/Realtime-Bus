package com.lowwor.realtimebus.ui.track;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.data.api.BusApiRepository;
import com.lowwor.realtimebus.data.local.PreferencesHelper;
import com.lowwor.realtimebus.data.model.Bus;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.base.BaseFragment;
import com.lowwor.realtimebus.ui.widget.MySwipeRefreshLayout;
import com.lowwor.realtimebus.utils.Constants;
import com.lowwor.realtimebus.utils.NetworkUtils;
import com.lowwor.realtimebus.utils.RxUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lowworker on 2015/10/15.
 */
public class TrackFragment extends BaseFragment {

    @Bind(R.id.swipe_container)
    MySwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.btn_try_again)
    Button btnTryAgain;
    @Bind(R.id.offline_container)
    LinearLayout mOfflineContainer;
    @Bind(R.id.progress_indicator)
    ProgressBar mProgressBar;
    @Bind(R.id.auto_text)
    AutoCompleteTextView mAutoComplete;
    @Bind(R.id.fab_switch)
    FloatingActionButton fabSwitch;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private static final int START_FROM_FIRST = 0;
    private static final int START_FROM_LAST = 1;
    private String mLineName;


    @Inject
    BusApiRepository mBusApiRepository;
    @Inject
    PreferencesHelper mPreferencesHelper;

    private String fromStation;
    private LinearLayoutManager mLinearLayoutManager;
    private List<BusStation> mStations;
    private BusStationAdapter mBusStationAdapter;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    private Subscription mAutoRefreshSubscription = null;
    private static final int NOTIFICATION_FLAG = 1;
    private List<String> mAutoCompleteBuses = new ArrayList<>();
    private ArrayAdapter<String> mAutoCompleteAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_track, container, false);
        ButterKnife.bind(this, fragmentView);
        Logger.i("onCreateView BaseFragment");

        initDependencyInjector();
        initToolbar();
        initAutoComplete();
        initSwipeRefresh();
        initRecyclerView();
        loadStationsIfNetworkConnected();
        return fragmentView;
    }


    @OnClick(R.id.btn_query)
    public void onQuery() {
        loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.btn_try_again)
    public void onTryAgainClick() {
        loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.fab_switch)
    public void onFabSwitchClick() {
        switchDirection();
        loadBusIfNetworkConnected();
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
        mSubscriptions.add(mBusApiRepository.searchLine(mAutoComplete.getText().toString())
                .flatMap(new Func1<BusLineWrapper, Observable<BusStationWrapper>>() {
                    @Override
                    public Observable<BusStationWrapper> call(BusLineWrapper busLineWrapper) {
                        mLineName = busLineWrapper.getData().get(0).name;
                        mPreferencesHelper.saveLastQueryLine(mLineName);
                        return mBusApiRepository.getStationByLineId(busLineWrapper.getData().get(getStartFrom()).id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BusStationWrapper>() {
                    @Override
                    public void onCompleted() {

//                        Logger.i("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                        Logger.e("There was a problem loading the top stories " + e);
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "找不到线路,请重试！", Toast.LENGTH_SHORT).show();
                        hideLoadingViews();
                    }

                    @Override
                    public void onNext(BusStationWrapper busStationWrapper) {
                        Logger.i("getStations onNext");
                        hideLoadingViews();
                        setupBusStations(busStationWrapper.getData());
                        saveAutoComplete();
                        refreshAutoComplete();
                        fromStation = mStations.get(0).name;
                        loadBusIfNetworkConnected();
                        executeAutoRefresh();
                    }
                }));
    }

    private void loadBusIfNetworkConnected() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            showHideOfflineLayout(false);
            getBus();

        } else {
            showHideOfflineLayout(true);
        }
    }

    private void getBus() {
        Logger.i("getBus" + mStations.get(0).name);
        mSubscriptions.add(mBusApiRepository.getBusListOnRoad(mLineName, fromStation)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getBusSubscriber()));
    }

    public void executeAutoRefresh() {

        if (mAutoRefreshSubscription != null && !mAutoRefreshSubscription.isUnsubscribed() && !getAutoRefresh()) {
            mAutoRefreshSubscription.unsubscribe();
            return;
        }
        if (getAutoRefresh()) {
            mAutoRefreshSubscription =
                    Observable.interval(Constants.REFRESH_INTERVAL, TimeUnit.SECONDS)
                            .timeInterval().flatMap(new Func1<TimeInterval<Long>, Observable<BusWrapper>>() {
                        @Override
                        public Observable<BusWrapper> call(TimeInterval<Long> longTimeInterval) {
                            return mBusApiRepository.getBusListOnRoad(mLineName, fromStation);
                        }
                    })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(getBusSubscriber());
        }
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
                hideLoadingViews();
            }

            @Override
            public void onNext(BusWrapper busWrapper) {
//                Logger.i("onNext");
                hideLoadingViews();
                List<Bus> buses = busWrapper.getData();
                List<BusStation> tempBustations = new ArrayList<BusStation>();
                tempBustations.addAll(mStations);
                for (BusStation busStation : tempBustations) {
                    if (busStation.buses != null && busStation.buses.size() != 0) {
                        busStation.buses.clear();
                        mBusStationAdapter.notifyItemChanged(mStations.indexOf(busStation));
                    }
                    for (Bus bus : buses) {
                        if (bus.currentStation.equals(busStation.name)) {

                            if (busStation.isAlarm) {
                                notifyBusArriveNotificatoin(busStation);
                            }

                            if (busStation.buses != null) {
                                busStation.buses.add(bus);
                            } else {
                                List<Bus> stationBuses = new ArrayList<>();
                                stationBuses.add(bus);
                                busStation.buses = stationBuses;
                            }

                            mBusStationAdapter.notifyItemChanged(mStations.indexOf(busStation));
                        }
                    }
                }

            }
        };
    }


    private void notifyBusArriveNotificatoin(BusStation busStation) {
         Intent  notificationIntent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getActivity(), NOTIFICATION_FLAG, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder mBuilder = new Notification.Builder(getActivity())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setTicker(busStation.name + "的公交到站了")
                .setContentIntent(pendingIntent)
                .setContentTitle(busStation.name + "的公交到站了")
                .setContentText(busStation.name + "的公交到站了")
                .setSmallIcon(R.drawable.ic_time_to_leave);

        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_FLAG, mBuilder.build());

    }

    private void switchDirection() {
        switchStartFrom();
        getStations();
    }


    private void setupBusStations(List<BusStation> busStations) {
        mStations.clear();
        mStations.addAll(busStations);
        mBusStationAdapter.notifyDataSetChanged();
    }

    private void switchStartFrom() {
        boolean startFrom = mPreferencesHelper.getIsStartFromFirst();
        mPreferencesHelper.saveStartFromFirst(!startFrom);
    }

    private int getStartFrom() {
        return mPreferencesHelper.getIsStartFromFirst() ? START_FROM_FIRST : START_FROM_LAST;
    }


    private void initAutoComplete() {
        mAutoCompleteAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_auto_complete, mAutoCompleteBuses);
        mAutoComplete.setAdapter(mAutoCompleteAdapter);
        mAutoComplete.setThreshold(3);
        mAutoComplete.setText(mPreferencesHelper.getLastQueryLine());
        refreshAutoComplete();
    }

    private void refreshAutoComplete() {
        //don't know why didn't auto refresh
        mSubscriptions.add(mPreferencesHelper.getIsAutoRefreshAsObservable()
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
                        Logger.i("auto onNext" + strings.toString());
                        mAutoCompleteAdapter.clear();
                        mAutoCompleteAdapter.addAll(strings);
                        mAutoCompleteAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void saveAutoComplete() {
//        Logger.i("save auto");
        if (mAutoComplete.getText().toString().equals("")) {
            return;
        }
        mPreferencesHelper.saveAutoCompleteItem(mAutoComplete.getText().toString());
    }

    private boolean getAutoRefresh() {
        return mPreferencesHelper.getAutoRefresh();
    }

    private void saveAutoRefresh(boolean isAutoRefresh) {
        mPreferencesHelper.saveAutoRefresh(isAutoRefresh);
    }

    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBusIfNetworkConnected();
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
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
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.auto_refresh:
                        saveAutoRefresh(!item.isChecked());
                        item.setChecked(!item.isChecked());
                        executeAutoRefresh();
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.auto_refresh).setChecked(getAutoRefresh());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.track_menu, menu);
    }

    private void hideLoadingViews() {
        mProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mSwipeRefreshLayout.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mOfflineContainer.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mRecyclerview.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        fabSwitch.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }
}
