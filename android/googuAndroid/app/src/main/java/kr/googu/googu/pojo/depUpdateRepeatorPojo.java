package kr.googu.googu.pojo;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 2018-01-22.
 */

public class depUpdateRepeatorPojo implements Parcelable{
    @SerializedName("myLocationPojo")
    @Expose
    private MyLocationPojo myLocationPojo;
    @SerializedName("tradeNumlist")
    @Expose
    private List<String> tradeNumlist = null;

    public final static Creator<depUpdateRepeatorPojo> CREATOR = new Creator<depUpdateRepeatorPojo>() {
        @Override
        public depUpdateRepeatorPojo createFromParcel(Parcel source) {
            return new depUpdateRepeatorPojo(source);
        }

        @Override
        public depUpdateRepeatorPojo[] newArray(int size) {
            return new depUpdateRepeatorPojo[size];
        }
    };

    public depUpdateRepeatorPojo(Parcel source) {
        this.myLocationPojo = ((MyLocationPojo) source.readValue(MyLocationPojo.class.getClassLoader()));
        source.readList(this.tradeNumlist,(java.lang.String.class.getClassLoader()));
    }

    public depUpdateRepeatorPojo(List<String> updateRepeaterTradeList, Location mapCenterLocation) {
        this.tradeNumlist = updateRepeaterTradeList;
        myLocationPojo = new MyLocationPojo(mapCenterLocation.getLatitude(),mapCenterLocation.getLongitude());
    }


    public MyLocationPojo getMyLocationPojo() {
        return myLocationPojo;
    }

    public void setMyLocationPojo(MyLocationPojo myLocationPojo) {
        this.myLocationPojo = myLocationPojo;
    }

    public List<String> getTradeNumlist() {
        return tradeNumlist;
    }

    public void setTradeNumlist(List<String> tradeNumlist) {
        this.tradeNumlist = tradeNumlist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(myLocationPojo);
        dest.writeList(tradeNumlist);
    }
}
