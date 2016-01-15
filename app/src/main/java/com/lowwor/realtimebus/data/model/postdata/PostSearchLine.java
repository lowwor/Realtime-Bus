package com.lowwor.realtimebus.data.model.postdata;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lowworker on 2015/10/14.
 */
public class PostSearchLine {

    /**
     * key : 3a
     */

    @SerializedName("key")
    private String key;

    public PostSearchLine(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
