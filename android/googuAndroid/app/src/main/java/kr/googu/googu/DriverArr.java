package kr.googu.googu;

/**
 * Created by Jay on 2017-12-28.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverArr implements Parcelable {

    @SerializedName("driverLat")
    @Expose
    private String driverLat;
    @SerializedName("driverLon")
    @Expose
    private String driverLon;
    @SerializedName("driverId")
    @Expose
    private String driverId;
    @SerializedName("tradeNum")
    @Expose
    private String tradeNum;
    @SerializedName("tradeName")
    @Expose
    private String tradeName;

    protected DriverArr(Parcel in) {
        driverLat = in.readString();
        driverLon = in.readString();
        driverId = in.readString();
        tradeNum = in.readString();
        tradeName = in.readString();
    }

    public static final Creator<DriverArr> CREATOR = new Creator<DriverArr>() {
        @Override
        public DriverArr createFromParcel(Parcel in) {
            return new DriverArr(in);
        }

        @Override
        public DriverArr[] newArray(int size) {
            return new DriverArr[size];
        }
    };

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

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(driverLat);
        dest.writeString(driverLon);
        dest.writeString(driverId);
        dest.writeString(tradeNum);
        dest.writeString(tradeName);
    }
}