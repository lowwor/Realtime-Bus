package com.lowwor.realtimebus.ui.profile;

import android.databinding.BaseObservable;

/**
 * Created by lowworker on 2016/5/21 0021.
 */
public class ProfileViewModel extends BaseObservable implements ProfileVista {

    private ProfilePresenter profilePresenter;

    public ProfileViewModel(ProfilePresenter profilePresenter) {
        this.profilePresenter = profilePresenter;
    }


}
