package com.lowwor.realtimebus.ui.track;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.databinding.FragmentTrackBinding;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.base.BaseFragment;
import com.lowwor.realtimebus.ui.settings.SettingsActivity;
import com.lowwor.realtimebus.utils.ShareUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lowworker on 2015/10/15.
 */
public class TrackFragment extends BaseFragment {

    @Inject
    TrackViewModel trackViewModel;
    @Inject
    TrackPresenter trackPresenter;

    private boolean isFirstIn = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTrackBinding fragmentTrackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false);
        ButterKnife.bind(this, fragmentTrackBinding.getRoot());
        initDependencyInjector();
        fragmentTrackBinding.setTrackViewModel(trackViewModel);
        initToolbar(fragmentTrackBinding.toolbar);
        initSwipeRefresh(fragmentTrackBinding.swipeContainer);
        fragmentTrackBinding.executePendingBindings();
        return fragmentTrackBinding.getRoot();
    }

    @OnClick(R.id.btn_query)
    public void onQuery() {
        trackPresenter.loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.btn_try_again)
    public void onTryAgainClick() {
        trackPresenter.loadStationsIfNetworkConnected();
    }

    @OnClick(R.id.fab_switch)
    public void onFabSwitchClick() {
        trackPresenter.switchDirection();
        trackPresenter.loadBusIfNetworkConnected();
    }

    private void initSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                trackPresenter.loadBusIfNetworkConnected();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
    }

    private void initDependencyInjector() {
        ((MainActivity) getActivity()).trackComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        trackPresenter.onStart();
        if (isFirstIn) {
            isFirstIn = false;
            trackPresenter.loadStationsIfNetworkConnected();
        } else {
            trackPresenter.loadBusIfNetworkConnected();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        trackPresenter.onStop();
    }

    private void initToolbar(Toolbar toolbar) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.auto_refresh:
                        trackPresenter.saveAutoRefresh(!item.isChecked());
                        item.setChecked(trackPresenter.getAutoRefresh());
                        trackPresenter.executeAutoRefresh();
                        break;
                    case R.id.settings:
                        startActivity(new Intent(getActivity(), SettingsActivity.class));
                        break;
                    case R.id.share:
                        ShareUtils.share(getActivity());
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.auto_refresh).setChecked(trackPresenter.getAutoRefresh());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.track_menu, menu);
    }


}
