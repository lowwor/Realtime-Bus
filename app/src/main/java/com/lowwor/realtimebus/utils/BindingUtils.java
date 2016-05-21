package com.lowwor.realtimebus.utils;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.ui.widget.InstantAutoComplete;
import com.lowwor.realtimebus.ui.widget.LimitArrayAdapter;
import com.lowwor.realtimebus.ui.widget.TextWatcherAdapter;
import com.lowwor.realtimebus.utils.bindinghelper.OnSearchActionListener;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by lowworker on 2016/3/2 0002.
 */
public class BindingUtils {


    @BindingAdapter("isRefreshing")
    public static void bindRefresh(SwipeRefreshLayout swipeRefreshLayout, boolean isLoading) {
        if (!isLoading) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    //Binding array
    @BindingAdapter("colorSchemeResources")
    public static void bindRefreshColor(SwipeRefreshLayout swipeRefreshLayout,   int[] colors) {
            swipeRefreshLayout.setColorSchemeColors(colors);
    }

    @BindingAdapter("items")
    public static <T> void bindAutoCompleteAdapter(InstantAutoComplete autoCompleteTextView, List<T> lineNameItems) {
//        Logger.d("bindAutoCompleteAdapter() called with: " + "autoCompleteTextView = [" + autoCompleteTextView + "], lineNameItems = [" + lineNameItems + "]");
        LimitArrayAdapter<T> adapter = (LimitArrayAdapter<T>) autoCompleteTextView.getAdapter();
        if (adapter == null) {
            adapter = new LimitArrayAdapter<>(autoCompleteTextView.getContext(), R.layout.item_auto_complete, lineNameItems);
            autoCompleteTextView.setAdapter(adapter);
            Logger.i("bindAutoCompleteAdapter: setAdapter");
        } else {
            adapter.clear();
            adapter.addAll(lineNameItems);
            adapter.notifyDataSetChanged();
//            Logger.i("bindAutoCompleteAdapter: not null");
        }
    }

    //can't use ObservableField to bind directly
//    https://medium.com/@fabioCollini/android-data-binding-f9f9d3afc761#.m2yxintjl
    @BindingAdapter({"textChange"})
    public static void bindEditText(final EditText view,
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
            view.append(newValue);
        }
    }

    @BindingAdapter({"searchAction"})
    public static void bindEditorAction(final EditText view,
                                        final OnSearchActionListener onSearchActionListener) {
        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchActionListener.onSearch();
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE
                    );
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    @BindingConversion
    public static String convertBindableToString(
            BindableString bindableString) {
        return bindableString.get();
    }




}
