package com.lowwor.realtimebus.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;

import java.util.List;

/**
 * Created by lowworker on 2015/10/14.
 */
@ParcelablePlease
public class BusStation implements Parcelable {
    /**
     * __type : Goophee.ZHGJ.Entity.Station
     * Id : ee5dd05e-9332-48d1-bb21-b2e05391c35f
     * Name : 城轨珠海北站
     * Lng : 113.546275
     * Lat : 22.405286
     * Description :
     */

    @SerializedName("__type")
    public String type;
    @SerializedName("Id")
    public String id;
    @SerializedName("Name")
    public String name;
    @SerializedName("Lng")
    public String longtitude;
    @SerializedName("Lat")
    public String latitude;
    @SerializedName("Description")
    public String description;

    public boolean isAlarm = false;
    public List<Bus> buses;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        BusStationParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<BusStation> CREATOR = new Creator<BusStation>() {
        public BusStation createFromParcel(Parcel source) {
            BusStation target = new BusStation();
            BusStationParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public BusStation[] newArray(int size) {
            return new BusStation[size];
        }
    };
}
