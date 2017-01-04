package com.lowwor.realtimebus.domain;

/**
 * Created by lowworker on 2017/1/4 0004.
 */

public class NoBusException extends IndexOutOfBoundsException {
    public NoBusException(String message) {
        super(message);
    }

    public NoBusException() {
    }
}
