package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 2018-01-22.
 */

public class MyLocationPojo implements Parcelable {
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;


    public static final Creator<MyLocationPojo> CREATOR = new Creator<MyLocationPojo>() {
        @Override
        public MyLocationPojo createFromParcel(Parcel source) {
            return new MyLocationPojo(source);
        }

        @Override
        public MyLocationPojo[] newArray(int size) {
            return new MyLocationPojo[size];
        }
    };

    public MyLocationPojo(Parcel source) {
        this.lat = ((String) source.readValue((String.class.getClassLoader())));
        this.lon = ((String) source.readValue((String.class.getClassLoader())));
    }

    public MyLocationPojo(double lat, double lon) {
        this.lat = String.valueOf(lat);
        this.lon = String.valueOf(lon);

    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.lat);
        dest.writeValue(this.lon);
    }
}
