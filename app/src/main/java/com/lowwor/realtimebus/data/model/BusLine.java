package com.lowwor.realtimebus.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

/**
 * Created by lowworker on 2015/10/14.
 */
@ParcelablePlease
public class BusLine implements Parcelable {
    /**
     * Id : 98eb0b59-bf91-4f4d-a504-28f7ae129c46
     * Name : 3A
     * LineNumber : 3A
     * Direction : 0
     * FromStation : 九洲港
     * ToStation : 城轨珠海北站
     * BeginTime : 06:15
     * EndTime : 22:25
     * Price : 3
     * Interval : 12
     * Description :
     * StationCount : 39
     */

    @SerializedName("Id")
    public String id;
    @SerializedName("Name")
    public String name;
    @SerializedName("LineNumber")
    public String lineNumber;
    @SerializedName("Direction")
    public int direction;
    @SerializedName("FromStation")
    public String fromStation;
    @SerializedName("ToStation")
    public String toStation;
    @SerializedName("BeginTime")
    public String beginTime;
    @SerializedName("EndTime")
    public String endTime;
    @SerializedName("Price")
    public String price;
    @SerializedName("Interval")
    public String interval;
    @SerializedName("Description")
    public String description;
    @SerializedName("StationCount")
    public int stationCount;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        BusLineParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<BusLine> CREATOR = new Creator<BusLine>() {
        public BusLine createFromParcel(Parcel source) {
            BusLine target = new BusLine();
            BusLineParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public BusLine[] newArray(int size) {
            return new BusLine[size];
        }
    };
}
