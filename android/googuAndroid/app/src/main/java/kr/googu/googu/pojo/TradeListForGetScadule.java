package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jay on 2018-01-26.
 */

public class TradeListForGetScadule implements Parcelable {
    @SerializedName("TradeNumList")
    @Expose
    private List<String> tradeNumList = null;
    public final static Parcelable.Creator<TradeListForGetScadule> CREATOR = new Creator<TradeListForGetScadule>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TradeListForGetScadule createFromParcel(Parcel in) {
            return new TradeListForGetScadule(in);
        }

        public TradeListForGetScadule[] newArray(int size) {
            return (new TradeListForGetScadule[size]);
        }

    }
            ;

    protected TradeListForGetScadule(Parcel in) {
        in.readList(this.tradeNumList, (java.lang.String.class.getClassLoader()));
    }

    public TradeListForGetScadule(List<String> tradeNumList) {
        this.tradeNumList = tradeNumList;
    }

    public List<String> getTradeListForGetScadule() {
        return tradeNumList;
    }

    public void setTradeListForGetScadule(List<String> tradeListForGetScadule) {
        this.tradeNumList = tradeListForGetScadule;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(tradeNumList);
    }

    public int describeContents() {
        return 0;
    }

}
