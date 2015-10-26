package com.lowwor.realtimebus.ui.base;

import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by lowworker on 2015/10/15.
 */
public class BaseActivity extends AppCompatActivity {
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
