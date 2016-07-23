package com.lowwor.realtimebus.ui.base;

import android.support.annotation.CallSuper;

import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter<V extends Vista> {

    protected CompositeSubscription subscriptions;
    protected V vista;

    public final void attachView(V vista) {
        if (this.vista == null) {
            this.vista = vista;
        }
    }


    public final void detachView() {
        this.vista = null;
    }

    @CallSuper
    public void onStart() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }

        this.subscriptions = new CompositeSubscription();
    }

    @CallSuper
    public void onStop() {
        if (subscriptions != null) {
            subscriptions.unsubscribe();
        }
    }

}