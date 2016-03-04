package com.lowwor.realtimebus.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.lowwor.realtimebus.BR;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class TrackViewModel extends BaseObservable {


    @Bindable
    private boolean isOffline;
    @Bindable
    private boolean isLoading;

    public void setIsOffline(boolean isOffline) {
        this.isOffline = isOffline;
        notifyPropertyChanged(BR.isOffline);
    }

    public boolean getIsOffline(){
        return isOffline;
    }


    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyPropertyChanged(BR.isLoading);
    }


    public boolean getIsLoading(){
        return isLoading;
    }
}
