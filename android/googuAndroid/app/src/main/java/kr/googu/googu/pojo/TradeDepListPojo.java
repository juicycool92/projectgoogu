package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 2018-01-26.
 */

public class TradeDepListPojo implements Parcelable {
    @SerializedName("tradeDepPojo")
    @Expose
    private List<TradeDepPojo> tradeDepPojo = null;

    public static final Creator<TradeDepListPojo> CREATOR = new Creator<TradeDepListPojo>() {
        @Override
        public TradeDepListPojo createFromParcel(Parcel source) {
            return new TradeDepListPojo(source);
        }

        @Override
        public TradeDepListPojo[] newArray(int size) {
            return new TradeDepListPojo[0];
        }
    };

    public TradeDepListPojo(Parcel source) {
        source.readTypedList(getTradeDepPojo(),TradeDepPojo.CREATOR);
    }

    public List<kr.googu.googu.pojo.TradeDepPojo> getTradeDepPojo() {
        return tradeDepPojo;
    }

    public void setTradeDepPojo(List<kr.googu.googu.pojo.TradeDepPojo> tradeDepPojo) {
        this.tradeDepPojo = tradeDepPojo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.getTradeDepPojo());
    }
}
