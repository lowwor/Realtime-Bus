package com.lowwor.realtimebus.injector.module;

import android.content.Context;
import android.net.ConnectivityManager;

import com.lowwor.realtimebus.domain.NetworkInteractor;
import com.lowwor.realtimebus.domain.NetworkInteractorImpl;
import com.lowwor.realtimebus.injector.ActivityScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/9/13.
 */

@Module
public class ActivityModule {

    private final Context mContext;

    public ActivityModule(Context mContext) {

        this.mContext = mContext;
    }

    @Provides
    @ActivityScope
    Context provideActivityContext() {
        return mContext;
    }

    @Provides
    @ActivityScope
    NetworkInteractor provideNetworkInteractor(Context context) {
        return new NetworkInteractorImpl(
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
    }
}
