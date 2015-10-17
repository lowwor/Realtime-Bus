package com.lowwor.realtimebus.ui;

import android.os.Bundle;

import com.lowwor.realtimebus.BusApplication;
import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.injector.component.DaggerTrackComponent;
import com.lowwor.realtimebus.injector.component.TrackComponent;
import com.lowwor.realtimebus.injector.module.FragmentModule;
import com.lowwor.realtimebus.injector.module.TrackModule;
import com.lowwor.realtimebus.ui.base.BaseActivity;
import com.lowwor.realtimebus.ui.track.TrackFragment;
import com.orhanobut.logger.Logger;


/**
 * Created by lowworker on 2015/10/15.
 */
public class MainActivity extends BaseActivity {

    TrackComponent mTrackComponent;



    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        Logger.i("onCreate activity");
        setContentView(R.layout.activity_main);
        replaceTrackFragment();
    }



  public TrackComponent trackComponent(){
       if (mTrackComponent == null) {
           mTrackComponent = DaggerTrackComponent.builder()
                   .appComponent(((BusApplication) getApplication()).getAppComponent())
                   .fragmentModule(new FragmentModule())
                   .trackModule(new TrackModule())
                   .build();
       }
       return mTrackComponent;
    }

    void replaceTrackFragment(){

        TrackFragment trackFragment = new TrackFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,trackFragment).commit();
    }
}
