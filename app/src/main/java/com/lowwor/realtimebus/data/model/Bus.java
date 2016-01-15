package com.lowwor.realtimebus.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hannesdorfmann.parcelableplease.annotation.ParcelablePlease;
import com.lowwor.realtimebus.model.BusParcelablePlease;

/**
 * Created by lowworker on 2015/10/14.
 */
@ParcelablePlease
public class Bus implements Parcelable {
    @SerializedName("BusNumber")
    public String busNumber;
    @SerializedName("CurrentStation")
    public String currentStation;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        BusParcelablePlease.writeToParcel(this, dest, flags);
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        public Bus createFromParcel(Parcel source) {
            Bus target = new Bus();
            BusParcelablePlease.readFromParcel(target, source);
            return target;
        }

        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}
