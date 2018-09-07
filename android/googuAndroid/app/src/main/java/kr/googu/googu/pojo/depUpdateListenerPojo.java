package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 2018-01-22.
 */

public class depUpdateListenerPojo implements Parcelable{

    @SerializedName("tradeLocList")
    @Expose
    private List<TradeLocList> tradeLocList = null;
    public final static Parcelable.Creator<depUpdateListenerPojo> CREATOR = new Creator<depUpdateListenerPojo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public depUpdateListenerPojo createFromParcel(Parcel in) {
            return new depUpdateListenerPojo(in);
        }

        public depUpdateListenerPojo[] newArray(int size) {
            return (new depUpdateListenerPojo[size]);
        }

    }
            ;

    protected depUpdateListenerPojo(Parcel in) {
        in.readList(this.tradeLocList, (kr.googu.googu.pojo.TradeLocList.class.getClassLoader()));
    }

    public depUpdateListenerPojo() {
    }

    public List<TradeLocList> getTradeLocList() {
        return tradeLocList;
    }

    public void setTradeLocList(List<TradeLocList> tradeLocList) {
        this.tradeLocList = tradeLocList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(tradeLocList);
    }

    public int describeContents() {
        return 0;
    }
}
