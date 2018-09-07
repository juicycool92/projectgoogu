package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 2018-01-24.
 */

public class TradeLocList implements Parcelable{
    @SerializedName("tradeNum")
    @Expose
    private String tradeNum;
    @SerializedName("tradeStatus")
    @Expose
    private String tradeStatus;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    public final static Parcelable.Creator<TradeLocList> CREATOR = new Creator<TradeLocList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TradeLocList createFromParcel(Parcel in) {
            return new TradeLocList(in);
        }

        public TradeLocList[] newArray(int size) {
            return (new TradeLocList[size]);
        }

    }
            ;

    protected TradeLocList(Parcel in) {
        this.tradeNum = ((String) in.readValue((String.class.getClassLoader())));
        this.tradeStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.lat = ((String) in.readValue((String.class.getClassLoader())));
        this.lon = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TradeLocList() {
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(tradeNum);
        dest.writeValue(tradeStatus);
        dest.writeValue(lat);
        dest.writeValue(lon);
    }

    public int describeContents() {
        return 0;
    }
}
