package kr.googu.googu.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by Jay on 2017-12-27.
 */

public class MapRepo implements Parcelable {


    @SerializedName("nearTradeArr")
    @Expose
    private List<NearTradeArr> nearTradeArr = null;
    @SerializedName("myActiveTradeDriver")
    @Expose
    private List<MyActiveTradeDriver> myActiveTradeDriver = null;


    protected MapRepo(Parcel in) {
        in.readTypedList(getNearTradeArr(),NearTradeArr.CREATOR);
        in.readTypedList(getMyActiveTradeDriver(),MyActiveTradeDriver.CREATOR);

    }

    public static final Creator<MapRepo> CREATOR = new Creator<MapRepo>() {
        @Override
        public MapRepo createFromParcel(Parcel in) {
            return new MapRepo(in);
        }

        @Override
        public MapRepo[] newArray(int size) {
            return new MapRepo[size];
        }
    };

    public List<NearTradeArr> getNearTradeArr() {
        return nearTradeArr;
    }

    public void setNearTradeArr(List<NearTradeArr> nearTradeArr) {
        this.nearTradeArr = nearTradeArr;
    }

    public List<MyActiveTradeDriver> getMyActiveTradeDriver() {
        return myActiveTradeDriver;
    }

    public void setMyActiveTradeDriver(List<MyActiveTradeDriver> myActiveTradeDriver) {
        this.myActiveTradeDriver = myActiveTradeDriver;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.nearTradeArr);
        dest.writeList(this.myActiveTradeDriver);

    }


    public interface MapApiInterface{
        @Headers({
                "Accept: application/json"
        })
        @FormUrlEncoded
        @POST("/mapReqForAndroid")
        Call<MapRepo> get_Map_retrofit(@Field("userId") String userId, @Field("location") String location, @Field("zoom") int zoom);
        //Call<ResponseBody> get_ResponseBody(@Field("userId") String userId, @Field("location") String location, @Field("zoom") int zoom);
        //Call<MapRepo> get_Map_retrofit(@Body bodyTester.class bodyTester);

    }
}
