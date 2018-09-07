package kr.googu.googu;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.googu.googu.pojo.MapRepo;
import kr.googu.googu.staticVars.StaticVars;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Created by Jay on 2017-12-27.
 * 이거 보지마, 정리 안되서 내가봐도 멘탈 깨져
 */

public class MapRefreshThread implements Runnable {
    Context mContext;
    MapRepo mMapRepo;
    String userId;
    int zoom;
    String location;
    Handler mHandler;
    StaticVars _GLOBAL_VAR;
    public MapRefreshThread(Handler mHandler,Context mContext,String userId){
        _GLOBAL_VAR = new StaticVars();
        this.mHandler = mHandler;
        this.mContext = mContext;
        this.userId = userId;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }



    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public void run() {
        Log.i("MapRefreshThread","스레드 런 동작 시작 location:"+location);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(logger);

        Gson gson = new GsonBuilder()
                .setLenient() //building as lenient mode`enter code here`
                .create();

        //Retrofit client = new Retrofit.Builder().baseUrl(_GLOBAL_VAR.get_SERVER_URL()).build();//.addConverterFactory(GsonConverterFactory.create(gson)).build();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(StaticVars.getServerUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
        MapRepo.MapApiInterface service = client.create(MapRepo.MapApiInterface.class);
        Call<MapRepo>call =service.get_Map_retrofit(userId,location,zoom);
        //Call<ResponseBody>call = service.get_ResponseBody(userId,location,zoom);
        call.enqueue(new Callback<MapRepo>() {
            @Override
            public void onResponse(Call<MapRepo> call, Response<MapRepo> res) {
                if(res.isSuccessful()){
                    Log.i("MapRefreshThread","연결성공 code"+res.code());
                    if(res.code()==200){
                        mMapRepo=res.body();

                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("mMapRepo",mMapRepo);

                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                        Log.i("MapRefreshThread","전송성공");
                    }
                }else{
                    Log.i("MapRefreshThread","연결failed");
                }
            }

            @Override
            public void onFailure(Call<MapRepo> call, Throwable t) {
                Log.i("MapRefreshThread","연결 애초부터 실패"+t.fillInStackTrace()+"\n"+call.request().body().toString());
            }
        });


    }
}
