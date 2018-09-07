package kr.googu.googu.serverConnector;

import kr.googu.googu.pojo.MapRepo;
import kr.googu.googu.pojo.TradeDepListPojo;
import kr.googu.googu.pojo.TradeDepPojo;
import kr.googu.googu.pojo.depUpdateListenerPojo;
import kr.googu.googu.pojo.driverLiteInfoPojo;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Jay on 2018-01-04.
 * 서버와 커넥션을 하기위한 라으터들을 관리하는곳 입니다.
 * 필요에따라 외부 pojo클래스를 참조합니다.
 */

public class router_Main {
    public interface MapApiInterface {
        @Headers({"Accept: application/json"})
        @FormUrlEncoded
        @POST("/mapReqForAndroid")
        Call<MapRepo> getMapInfo(@Field("locationLat") String locationLat, @Field("locationLon") String locationLon, @Field("zoom") int zoom);


        @POST("/updateMyLocForDepTime")
        Call<depUpdateListenerPojo> updateMyLocForDepTime(@Body RequestBody pojo);
    }
    public interface imageInterface{
        @Multipart
        @POST("/uploadMyImageForAndroid")
        Call<ResponseBody> uploadMyImage(
                @Part("userId") RequestBody userId,
                @Part MultipartBody.Part file
        );

        @FormUrlEncoded
        @POST("/requestMyImageForAndroid")
        Call<ResponseBody> downloadMyImage(
                @Field("userId")String userId
        );
    }
    public interface accountInterface{
        @POST("/sessionCheckForAndroid")
        Call<Void> checkSessionAlive();

        @FormUrlEncoded
        @POST("/loginForAndroid")
        Call<Void> login(@Field("userId")String userId, @Field("userPw")String userPw);

        @Multipart
        @POST("/signinForAndroid")
        Call<ResponseBody> Sign(
                @Part MultipartBody.Part file,
                @Part("userId")RequestBody userId,
                @Part("userPw")RequestBody userPw,
                @Part("userName")RequestBody userName,
                @Part("userPhone")RequestBody userPhone
                );
    }
    public interface infoInterface{
        @Headers({"Accept: application/json"})
        @FormUrlEncoded
        @POST("/requestDriverInfoLiteForAndroid")
        Call<driverLiteInfoPojo> driverInfoLite(@Field("driverId")String driverId);
    }
    public interface tradeInterface{
        @Headers({"Accept: application/json"})
        @FormUrlEncoded
        @POST("/getTradeDepTime")
        Call<TradeDepPojo> getTradeDep(@Field("tradeNum")String tradeNum);

        @Headers({"Accept: application/json"})
        @POST("/getTradeDepListTime")
        Call<TradeDepListPojo> getTradeDepList(@Body RequestBody pojo);
    }
}
