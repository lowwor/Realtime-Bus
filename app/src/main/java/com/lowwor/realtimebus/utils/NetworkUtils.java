package com.lowwor.realtimebus.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by lowworker on 2015/10/17.
 */
public class NetworkUtils {

        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager.getActiveNetworkInfo() != null;
        }


}
