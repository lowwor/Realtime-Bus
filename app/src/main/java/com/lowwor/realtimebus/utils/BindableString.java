package com.lowwor.realtimebus.utils;

import android.databinding.BaseObservable;

public class BindableString extends BaseObservable {
    private String value;

    public String get() {
        return value != null ? value : "";
    }

    public void set(String value) {
        if (!((this.value == null) ? (value == null) : this.value.equals(value))) {
            this.value = value;
            notifyChange();
        }
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}