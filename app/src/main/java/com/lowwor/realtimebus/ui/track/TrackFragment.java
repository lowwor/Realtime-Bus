package com.lowwor.realtimebus.ui.track;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
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

    public static TrackFragment newInstance() {
        return new TrackFragment();
    }
}
