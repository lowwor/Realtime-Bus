package com.lowwor.realtimebus.ui.base;

import android.support.annotation.CallSuper;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BasePresenter<V extends Vista> {

    protected CompositeDisposable compositeDisposable;
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
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        this.compositeDisposable = new CompositeDisposable();
    }

    @CallSuper
    public void onStop() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    public CompositeDisposable getCompositeDisposable(){
        return compositeDisposable;
    }

}