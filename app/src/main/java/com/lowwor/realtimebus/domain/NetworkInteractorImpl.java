package com.lowwor.realtimebus.domain;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

import io.reactivex.Completable;

/**
 * Created by lowwor on 2017/1/4 0004.
 */
public class NetworkInteractorImpl implements NetworkInteractor {
    private ConnectivityManager connectivityManager;

    @Inject
    public NetworkInteractorImpl(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    public boolean hasNetworkConnection() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public Completable hasNetworkConnectionCompletable() {
        if (hasNetworkConnection()) {
            return Completable.complete();
        } else {
            return Completable.error(new NetworkUnavailableException("Network unavailable!"));
        }
    }
}
