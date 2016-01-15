package com.lowwor.realtimebus.data.model.wrapper;

import com.google.gson.annotations.SerializedName;
import com.lowwor.realtimebus.data.model.BusLine;

import java.util.List;

/**
 * Created by lowworker on 2015/10/14.
 */
public class BusLineWrapper {

    /**
     * d : [{"__type":"Goophee.ZHGJ.Entity.Line","Id":"98eb0b59-bf91-4f4d-a504-28f7ae129c46","Name":"3A","LineNumber":"3A","Direction":0,"FromStation":"九洲港","ToStation":"城轨珠海北站","BeginTime":"06:15","EndTime":"22:25","Price":"3","Interval":"12","Description":"","StationCount":39},{"__type":"Goophee.ZHGJ.Entity.Line","Id":"a6231ab9-99af-4eba-914c-8104fd9aad3a","Name":"3A","LineNumber":"3A","Direction":1,"FromStation":"城轨珠海北站","ToStation":"九洲港","BeginTime":"06:10","EndTime":"22:20","Price":"3","Interval":"12","Description":"","StationCount":39}]
     */

    @SerializedName("d")
    private List<BusLine> data;

    public void setData(List<BusLine> data) {
        this.data = data;
    }

    public List<BusLine> getData() {
        return data;
    }


}
