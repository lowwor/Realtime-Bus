package com.lowwor.realtimebus.utils;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.EditText;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.ui.widget.TextWatcherAdapter;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class BindingUtils {


    @BindingAdapter("bind:isRefreshing")
    public static void bindRefresh(SwipeRefreshLayout swipeRefreshLayout, boolean isLoading) {
        if (!isLoading) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    //can't use ObservableField to bind directly
//    https://medium.com/@fabioCollini/android-data-binding-f9f9d3afc761#.m2yxintjl
    @BindingAdapter({"app:textChange"})
    public static void bindEditText(EditText view,
                                    final BindableString bindableString) {
        Pair<BindableString, TextWatcherAdapter> pair =
                (Pair) view.getTag(R.id.bound_observable);
        if (pair == null || pair.first != bindableString) {
            if (pair != null) {
                view.removeTextChangedListener(pair.second);
            }
            TextWatcherAdapter watcher = new TextWatcherAdapter() {
                public void onTextChanged(CharSequence s,
                                          int start, int before, int count) {
                    bindableString.set(s.toString());
                }
            };
            view.setTag(R.id.bound_observable,
                    new Pair<>(bindableString, watcher));
            view.addTextChangedListener(watcher);
        }
        String newValue = bindableString.get();
        if (!view.getText().toString().equals(newValue)) {
            view.setText(newValue);
        }
    }

    @BindingConversion
    public static String convertBindableToString(
            BindableString bindableString) {
        return bindableString.get();
    }


}
