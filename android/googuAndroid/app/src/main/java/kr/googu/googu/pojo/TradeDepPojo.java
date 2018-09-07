package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jay on 2018-01-21.
 */

public class TradeDepPojo implements Parcelable {

    @SerializedName("tradeNum")
    @Expose
    private String tradeNum;

    @SerializedName("tradeDepTime")
    @Expose
    private String tradeDepTime;


    public static final Creator<TradeDepPojo> CREATOR = new Creator<TradeDepPojo>() {
        @Override
        public TradeDepPojo createFromParcel(Parcel source) {
            return new TradeDepPojo(source);
        }

        @Override
        public TradeDepPojo[] newArray(int size) {
            return new TradeDepPojo[0];
        }
    };

    public TradeDepPojo(Parcel source) {}

    public String getTradeNum() {return tradeNum;}

    public void setTradeNum(String tradeNum) {this.tradeNum = tradeNum;}

    public String getTradeDepTime() {
        return tradeDepTime;
    }

    public void setTradeDepTime(String tradeDepTime) {
        this.tradeDepTime = tradeDepTime;
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tradeNum);
        dest.writeString(this.tradeDepTime);
    }
}
