package com.lowwor.realtimebus.injector.module;

import com.lowwor.realtimebus.injector.ProfileScope;
import com.lowwor.realtimebus.ui.profile.ProfilePresenter;
import com.lowwor.realtimebus.ui.profile.ProfilePresenterImpl;
import com.lowwor.realtimebus.ui.profile.ProfileViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lowworker on 2015/10/15.
 */
@Module
public class ProfileModule {

    @ProfileScope
    @Provides
    ProfileViewModel provideProfileViewModel(){
        return new ProfileViewModel();
    }

    @ProfileScope
    @Provides
    ProfilePresenter provideProfilePresenter(){
        return new ProfilePresenterImpl();
    }

}
