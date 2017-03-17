package com.lowwor.realtimebus.ui.profile;

import com.lowwor.realtimebus.ui.base.BasePresenter;

/**
 * Created by lowworker on 2016/4/23 0023.
 */
public abstract class ProfilePresenter extends BasePresenter<ProfileVista> {


    public abstract void sendFeedBack();

    public abstract void showShare();

    public abstract void gotoSettings();


}
