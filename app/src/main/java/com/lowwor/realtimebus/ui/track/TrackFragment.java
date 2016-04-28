package com.lowwor.realtimebus.ui.track;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.databinding.FragmentTrackBinding;
import com.lowwor.realtimebus.ui.MainActivity;
import com.lowwor.realtimebus.ui.base.BaseFragment;

import javax.inject.Inject;

/**
 * Created by lowworker on 2015/10/15.
 */
public class TrackFragment extends BaseFragment {

    @Inject
    TrackViewModel trackViewModel;
    @Inject
    TrackPresenter trackPresenter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTrackBinding fragmentTrackBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false);
        initDependencyInjector();
        fragmentTrackBinding.setTrackViewModel(trackViewModel);
        fragmentTrackBinding.setPresenter(trackPresenter);
        initToolbar(fragmentTrackBinding.toolbar);
        fragmentTrackBinding.executePendingBindings();
        return fragmentTrackBinding.getRoot();
    }


    private void initDependencyInjector() {
        ((MainActivity) getActivity()).trackComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        trackPresenter.attachView(trackViewModel);
        trackPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        trackPresenter.detachView();
        trackPresenter.onStop();
    }

    private void initToolbar(Toolbar toolbar) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // TODO: 2016/4/27 0027 hide presenter 
        menu.findItem(R.id.auto_refresh).setChecked(trackPresenter.getAutoRefresh());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.track_menu, menu);
    }


}
