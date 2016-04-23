package com.lowwor.realtimebus.ui.base;

import android.databinding.Observable;
import android.support.annotation.CallSuper;

import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter<VM extends Observable> {

    protected CompositeSubscription subscriptions;
    private VM viewModel;

    public final void attachViewModel(VM viewModel) {
        if (this.viewModel == null) {
            this.viewModel = viewModel;
        }
    }


    public final void detachViewModel() {
        this.viewModel = null;
    }

    protected final boolean hasViewModel() {
        return viewModel != null;
    }

    public final VM getViewModel() {
        return viewModel;
    }


    @CallSuper
    public  void onStart(){
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }

        this.subscriptions = new CompositeSubscription();
    }

    @CallSuper
    public void onStop(){
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

}