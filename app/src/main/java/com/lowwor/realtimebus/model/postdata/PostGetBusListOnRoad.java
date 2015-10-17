package com.lowwor.realtimebus.model.postdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lowworker on 2015/10/14.
 */
public class PostGetBusListOnRoad {

    /**
     * lineName : 3A
     * fromStation : 城轨珠海北站
     */

    @SerializedName("lineName")
    private String lineName;
    @SerializedName("fromStation")
    private String fromStation;

    public PostGetBusListOnRoad(String lineName, String fromStation) {
        this.lineName = lineName;
        this.fromStation = fromStation;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getLineName() {
        return lineName;
    }

    public String getFromStation() {
        return fromStation;
    }
}
