package kr.googu.googu.pojo;

/**
 * Created by Jay on 2017-12-28.
 * edited by jay on 2018-01-16
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyActiveTradeDriver implements Parcelable {

    @SerializedName("tradeNum")
    @Expose
    private String tradeNum;
    @SerializedName("tradeStatus")
    @Expose
    private String tradeStatus;
    @SerializedName("driverId")
    @Expose
    private String driverId;
    @SerializedName("driverTprofile")
    @Expose
    private String driverTprofile;
    @SerializedName("driverLat")
    @Expose
    private String driverLat;
    @SerializedName("driverLon")
    @Expose
    private String driverLon;

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverTprofile() {
        return driverTprofile;
    }

    public void setDriverTprofile(String driverTprofile) {
        this.driverTprofile = driverTprofile;
    }

    public String getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(String driverLat) {
        this.driverLat = driverLat;
    }

    public String getDriverLon() {
        return driverLon;
    }

    public void setDriverLon(String driverLon) {
        this.driverLon = driverLon;
    }


    protected MyActiveTradeDriver(Parcel in) {
        tradeNum = in.readString();
        tradeStatus = in.readString();
        driverId = in.readString();
        driverTprofile = in.readString();
        driverLat = in.readString();
        driverLon= in.readString();
    }

    public static final Creator<MyActiveTradeDriver> CREATOR = new Creator<MyActiveTradeDriver>() {
        @Override
        public MyActiveTradeDriver createFromParcel(Parcel in) {
            return new MyActiveTradeDriver(in);
        }

        @Override
        public MyActiveTradeDriver[] newArray(int size) {
            return new MyActiveTradeDriver[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tradeNum);
        dest.writeString(tradeStatus);
        dest.writeString(driverId);
        dest.writeString(driverTprofile);
        dest.writeString(driverLat);
        dest.writeString(driverLon);
    }
}