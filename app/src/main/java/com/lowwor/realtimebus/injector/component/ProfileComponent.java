package com.lowwor.realtimebus.injector.component;

import com.lowwor.realtimebus.injector.ProfileScope;
import com.lowwor.realtimebus.injector.module.ProfileModule;
import com.lowwor.realtimebus.ui.profile.ProfileFragment;
import com.lowwor.realtimebus.ui.profile.ProfilePresenter;
import com.lowwor.realtimebus.ui.profile.ProfileViewModel;

import dagger.Component;

/**
 * Created by lowworker on 2015/10/15.
 */

@ProfileScope
@Component(dependencies = {ActivityComponent.class}, modules = {ProfileModule.class})
public interface ProfileComponent {

    ProfileViewModel profileViewModel();

    ProfilePresenter trackPresenter();

    void inject(ProfileFragment profileFragment);

}
