package com.lowwor.realtimebus.ui.profile;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.databinding.FragmentProfileBinding;
import com.lowwor.realtimebus.ui.MainActivity;

import javax.inject.Inject;

/**
 * Created by lowworker on 2016/5/21 0021.
 */
public class ProfileFragment extends Fragment {


    @Inject
    ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentProfileBinding fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        initDependencyInjector();
        fragmentProfileBinding.setProfileViewModel(profileViewModel);
//        initToolbar(fragmentProfileBinding.toolbar);

        fragmentProfileBinding.executePendingBindings();
        return fragmentProfileBinding.getRoot();
    }

    private void initDependencyInjector() {
        ((MainActivity) getActivity()).profileComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static Fragment newInstance() {
        return new ProfileFragment();
    }
}
