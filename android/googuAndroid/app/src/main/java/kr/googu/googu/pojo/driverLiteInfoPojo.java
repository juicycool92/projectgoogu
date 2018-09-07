package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 2018-01-19.
 */

public class driverLiteInfoPojo implements Parcelable{

    @SerializedName("driverName")
    @Expose
    private String driverName;
    @SerializedName("driverPic")
    @Expose
    private String driverPic;
    @SerializedName("driverRank")
    @Expose
    private String driverRank;
    @SerializedName("driverContext")
    @Expose
    private String driverContext;

    public driverLiteInfoPojo(Parcel source) {

    }

    public static final Creator<driverLiteInfoPojo> CREATOR = new Creator<driverLiteInfoPojo>() {
        @Override
        public driverLiteInfoPojo createFromParcel(Parcel source) {
            return new driverLiteInfoPojo(source);
        }

        @Override
        public driverLiteInfoPojo[] newArray(int size) {
            return new driverLiteInfoPojo[size];
        }
    };

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPic() {
        return driverPic;
    }

    public void setDriverPic(String driverPic) {
        this.driverPic = driverPic;
    }

    public String getDriverRank() {
        return driverRank;
    }

    public void setDriverRank(String driverRank) {
        this.driverRank = driverRank;
    }

    public String getDriverContext() {
        return driverContext;
    }

    public void setDriverContext(String driverContext) {
        this.driverContext = driverContext;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
