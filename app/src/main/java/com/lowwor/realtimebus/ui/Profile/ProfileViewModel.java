package com.lowwor.realtimebus.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;

import com.lowwor.realtimebus.ui.settings.SettingsActivity;
import com.lowwor.realtimebus.utils.FeedBackUtils;
import com.lowwor.realtimebus.utils.ShareUtils;

/**
 * Created by lowworker on 2016/5/21 0021.
 */
public class ProfileViewModel extends BaseObservable implements ProfileVista {

    private Context context;

    public ProfileViewModel(Context context) {
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
