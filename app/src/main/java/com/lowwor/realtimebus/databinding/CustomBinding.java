package com.lowwor.realtimebus.databinding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.ui.widget.InstantAutoComplete;
import com.lowwor.realtimebus.ui.widget.LimitArrayAdapter;
import com.lowwor.realtimebus.databinding.bindinghelper.OnSearchActionListener;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by lowwor on 2016/3/2 0002.
 */
public class CustomBinding {


    @BindingAdapter("isRefreshing")
    public static void bindRefresh(SwipeRefreshLayout swipeRefreshLayout, boolean isLoading) {
        if (!isLoading) {
            swipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    //Binding array
    @BindingAdapter("colorSchemeResources")
    public static void bindRefreshColor(SwipeRefreshLayout swipeRefreshLayout, int[] colors) {
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

    @BindingAdapter("busNumberAnimation")
    public static void bindBusNumberAnimation(final View view, int busNumber) {
        Animation animation = view.getAnimation();
        if (busNumber != 0 && animation == null) {
            view.startAnimation(getAnimation(view));
        } else if (animation != null) {
            animation.cancel();
            view.setAnimation(null);
        }

    }

    private static Animation getAnimation(View view) {
        return AnimationUtils.loadAnimation(view.getContext(), R.anim.bus_working);
    }


}
