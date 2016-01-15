package com.lowwor.realtimebus.data.model.postdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lowworker on 2015/10/14.
 */
public class PostGetStationByLineId {

    /**
     * lineId : a6231ab9-99af-4eba-914c-8104fd9aad3a
     */

    @SerializedName("lineId")
    private String lineId;

    public PostGetStationByLineId(String lineId) {
        this.lineId = lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getLineId() {
        return lineId;
    }
}
