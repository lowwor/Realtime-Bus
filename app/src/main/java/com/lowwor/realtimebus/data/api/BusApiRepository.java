package com.lowwor.realtimebus.data.api;

/**
 * Created by lowworker on 2015/10/14.
 */

import com.lowwor.realtimebus.BuildConfig;
import com.lowwor.realtimebus.data.model.postdata.PostGetBusListOnRoad;
import com.lowwor.realtimebus.data.model.postdata.PostGetStationByLineId;
import com.lowwor.realtimebus.data.model.postdata.PostSearchLine;
import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;


/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApiRepository {
    BusService mBusService;

    public BusApiRepository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BusService.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        mBusService = mRetrofit.create(BusService.class);

    }

    public Observable<BusLineWrapper> searchLine(String key) {
        PostSearchLine postSearchLine = new PostSearchLine(key);
        return mBusService.searchLine(postSearchLine);
    }


    public Observable<BusStationWrapper> getStationByLineId(String lineId) {

        PostGetStationByLineId postGetStationByLineId = new PostGetStationByLineId(lineId);
        return mBusService.getStationByLineId(postGetStationByLineId);
    }

    public Observable<BusWrapper> getBusListOnRoad(String lineName,String fromStation) {
        PostGetBusListOnRoad postGetBusListOnRoad = new PostGetBusListOnRoad(lineName,fromStation);
        return mBusService.getBusListOnRoad(postGetBusListOnRoad);
    }



}
