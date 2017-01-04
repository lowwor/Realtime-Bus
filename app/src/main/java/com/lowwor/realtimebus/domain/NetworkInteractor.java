package com.lowwor.realtimebus.domain;

import io.reactivex.Completable;

public interface NetworkInteractor {

    boolean hasNetworkConnection();

    Completable hasNetworkConnectionCompletable();

     class NetworkUnavailableException extends Throwable {
        public NetworkUnavailableException(String message) {
            super(message);
        }

        public NetworkUnavailableException() {
        }
    }


}