package kr.googu.googu.pojo;

/**
 * Created by Jay on 2017-12-28.
 */
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NearTradeArr implements Parcelable {

    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lon")
    @Expose
    private String lon;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("itemPrice")
    @Expose
    private String itemPrice;
    @SerializedName("itemDepTime")
    @Expose
    private String itemDepTime;
    @SerializedName("itemArrPlace")
    @Expose
    private String itemArrPlace;
    @SerializedName("tradeNum")
    @Expose
    private String tradeNum;

    protected NearTradeArr(Parcel in) {
        lat = in.readString();
        lon = in.readString();
        itemName = in.readString();
        itemPrice = in.readString();
        itemDepTime = in.readString();
        itemArrPlace = in.readString();
        tradeNum = in.readString();
    }

    public static final Creator<NearTradeArr> CREATOR = new Creator<NearTradeArr>() {
        @Override
        public NearTradeArr createFromParcel(Parcel in) {
            return new NearTradeArr(in);
        }

        @Override
        public NearTradeArr[] newArray(int size) {
            return new NearTradeArr[size];
        }
    };

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDepTime() {
        return itemDepTime;
    }

    public void setItemDepTime(String itemDepTime) {
        this.itemDepTime = itemDepTime;
    }

    public String getItemArrPlace() {
        return itemArrPlace;
    }

    public void setItemArrPlace(String itemArrPlace) {
        this.itemArrPlace = itemArrPlace;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(itemName);
        dest.writeString(itemPrice);
        dest.writeString(itemDepTime);
        dest.writeString(itemArrPlace);
        dest.writeString(tradeNum);
    }
}