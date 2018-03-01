package com.lowwor.realtimebus.injector.module;

import android.content.Context;

import com.lowwor.realtimebus.injector.ProfileScope;
import com.lowwor.realtimebus.ui.profile.ProfilePresenter;
import com.lowwor.realtimebus.ui.profile.ProfilePresenterImpl;
import com.lowwor.realtimebus.ui.profile.ProfileViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowwor on 2015/10/15.
 */
@Module
public class ProfileModule {

    @ProfileScope
    @Provides
    ProfileViewModel provideProfileViewModel(ProfilePresenter profilePresenter) {
        return new ProfileViewModel(profilePresenter);
    }

    @ProfileScope
    @Provides
    ProfilePresenter provideProfilePresenter(Context context) {
        return new ProfilePresenterImpl(context);
    }

}
