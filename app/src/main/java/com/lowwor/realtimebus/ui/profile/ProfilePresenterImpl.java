package com.lowwor.realtimebus.ui.profile;

import android.content.Context;
import android.content.Intent;

import com.lowwor.realtimebus.ui.settings.SettingsActivity;
import com.lowwor.realtimebus.utils.FeedBackUtils;
import com.lowwor.realtimebus.utils.ShareUtils;

/**
 * Created by lowwor on 2016/5/21 0021.
 */
public class ProfilePresenterImpl extends ProfilePresenter {

    private Context context;
    public ProfilePresenterImpl(Context context) {
        this.context = context;
    }


    @Override
    public void sendFeedBack() {
        FeedBackUtils.sendFeedbackEmail(context);
    }

    @Override
    public void showShare() {
        ShareUtils.share(context);
    }

    @Override
    public void gotoSettings() {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }
}
