package com.lowwor.realtimebus.data.api;

/**
 * Created by lowworker on 2015/10/14.
 */

import com.lowwor.realtimebus.data.model.wrapper.BusLineWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusStationWrapper;
import com.lowwor.realtimebus.data.model.wrapper.BusWrapper;
import com.lowwor.realtimebus.data.model.postdata.PostGetBusListOnRoad;
import com.lowwor.realtimebus.data.model.postdata.PostGetStationByLineId;
import com.lowwor.realtimebus.data.model.postdata.PostSearchLine;
import com.squareup.okhttp.OkHttpClient;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;


/**
 * Created by lowworker on 2015/9/19.
 */
public class BusApiRepository {
    Retrofit mRetrofit;
    BusService mBusService;

    public BusApiRepository() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.interceptors().add(new LoggingInterceptor());

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(BusService.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addCallAdapterFactory(new ErrorHandlingExecutorCallAdapterFactory(new ErrorHandlingExecutorCallAdapterFactory.MainThreadExecutor()))
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
