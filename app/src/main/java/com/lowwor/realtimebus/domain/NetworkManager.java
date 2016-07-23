package com.lowwor.realtimebus.domain;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by lowworker on 2016/5/7 0007.
 */
public class NetworkManager {

    private Context context;

    public NetworkManager(Context context) {

        this.context = context;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

}
