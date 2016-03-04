package com.lowwor.realtimebus.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.lowwor.realtimebus.utils.BindableString;
import com.lowwor.realtimebus.BR;

/**
 * Created by lowworker on 2016/3/3 0003.
 */
public class AutoCompleteViewModel extends BaseObservable {

    @Bindable
    public BindableString text =new BindableString();


    public final ObservableList<String> lineNameItems = new ObservableArrayList<>();

    public void setText(String text) {
        this.text.set(text);
        notifyPropertyChanged(BR.autoCompleteViewModel);
    }
}
