package kr.googu.googu.serverConnector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import kr.googu.googu.pojo.MapRepo;
import kr.googu.googu.pojo.TradeDepListPojo;
import kr.googu.googu.pojo.TradeDepPojo;
import kr.googu.googu.pojo.TradeListForGetScadule;
import kr.googu.googu.pojo.depUpdateListenerPojo;
import kr.googu.googu.pojo.depUpdateRepeatorPojo;
import kr.googu.googu.pojo.driverLiteInfoPojo;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Jay on 2018-01-04.
 * 서버 연결을 관리하는 서버 쓰레드, 해당 클래스에서는 모든 커넥션 정보들이 담겨져 있습니다.
 */

public class serverConnecterST implements Runnable{
    final static int _REQUEST_MAP_ALL_REFRESH =100;
    final static int _REQUEST_MY_PROFILE_IMAGE = 310;
    final static int _UPDATE_MY_IMAGE = 320;
    final static int _CHECK_SESSION_ALIVE = 400;
    final static String TAG = "serverConncterSubT";
    Handler conHandler;
    Handler mHandler; //메인 헨들러
    Handler ioHandler;//이게 쓰이나?
    Context mContext; //현제 실행중인 엑티비티의 컨텍스트

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    Bundle bundle;
    int statusCode;

    public serverConnecterST(Handler mHandler,Context mContext) {
        this.mHandler = mHandler;
        this.mContext = mContext;

    }


    public void setIoHandler(Handler ioHandler) {
        this.ioHandler = ioHandler;
    }


    @Override
    public void run() {
        boolean b = Looper.myLooper() == Looper.getMainLooper();
        Log.i(TAG,"in run(), is mainTh?"+b);

    }

    public void myLocationUpdateRapidly(List<String> updateRepeaterTradeList, Location mapCenterLocation) {
        depUpdateRepeatorPojo pojo = new depUpdateRepeatorPojo(updateRepeaterTradeList,mapCenterLocation);
        Gson gson = new Gson();
        gson.toJson(pojo);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),  gson.toJson(pojo));


        router_Main.MapApiInterface service = new ServiceGenerator(mContext).createService(router_Main.MapApiInterface.class);
        Call<depUpdateListenerPojo> call = service.updateMyLocForDepTime(body);
        call.enqueue(new Callback<depUpdateListenerPojo>() {
            @Override
            public void onResponse(Call<depUpdateListenerPojo> call, Response<depUpdateListenerPojo> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                if(response.code()==200){
                    depUpdateListenerPojo pojo = response.body();
                    bundle.putInt("result",731);
                    bundle.putParcelable("pojo",pojo);
                }else{
                    bundle.putInt("result",732);
                    bundle.putInt("code",response.code());
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<depUpdateListenerPojo> call, Throwable t) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }

    public void driverInfoLiteRequest(String driverId){
        Log.i(TAG,"여까진? th3");
        router_Main.infoInterface service = new ServiceGenerator(mContext).createService(router_Main.infoInterface.class);
        Log.i(TAG,"여까진? th4");
        Call<driverLiteInfoPojo> call = service.driverInfoLite(driverId);
        Log.i(TAG,"여까진? th5");

        call.enqueue(new Callback<driverLiteInfoPojo>() {
            @Override
            public void onResponse(Call<driverLiteInfoPojo> call, Response<driverLiteInfoPojo> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();

                if(response.code()==200){
                    driverLiteInfoPojo driverPojo = response.body();
                    bundle.putInt("result",611);
                    bundle.putParcelable("driverInfo",driverPojo);
                }else if(response.code()==203){//auth expired
                    bundle.putInt("result",9401);
                }else if(response.code()==204){ // non content
                    bundle.putInt("result",612);
                }else{
                    bundle.putInt("result",613);
                    Log.i(TAG,"err on driverInfoLiteRequenst :\n code["+response.code()+"]");
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<driverLiteInfoPojo> call, Throwable t) {
                Log.i(TAG,"response failed on driverInfoLiteRequest");
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }
    public void getPickupListTime(List<String> tradeNumList) {
        TradeListForGetScadule pojo = new TradeListForGetScadule(tradeNumList);
        Gson gson = new Gson();
        gson.toJson(pojo);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),  gson.toJson(pojo));

        router_Main.tradeInterface service  = new ServiceGenerator(mContext).createService(router_Main.tradeInterface.class);
        Call<TradeDepListPojo> call = service.getTradeDepList(body);
        call.enqueue(new Callback<TradeDepListPojo>() {
            @Override
            public void onResponse(Call<TradeDepListPojo> call, Response<TradeDepListPojo> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                if(response.isSuccessful()){
                    switch (response.code()){
                        case 200 : {
                            bundle.putInt("result",723);
                            TradeDepListPojo pojo= response.body();
                            bundle.putParcelable("tradeDepListPojo",pojo);
                            break;
                        }
                        case 203 : {
                            bundle.putInt("result",9401);
                            break;
                        }
                        case 204 : {
                            bundle.putInt("result",724);
                            break;
                        }
                        default : {
                            Log.i(TAG,"unknown status code :"+response.code());
                            bundle.putInt("result",9001);
                            break;
                        }
                    }
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<TradeDepListPojo> call, Throwable t) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }
    public void getPickupTime(String tradeNum){
        Log.i(TAG,"getPickupTime 에 값이 드러갔나?"+tradeNum);
        router_Main.tradeInterface service  = new ServiceGenerator(mContext).createService(router_Main.tradeInterface.class);
        Call<TradeDepPojo> call = service.getTradeDep(tradeNum);
        call.enqueue(new Callback<TradeDepPojo>() {
            @Override
            public void onResponse(Call<TradeDepPojo> call, Response<TradeDepPojo> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                if(response.isSuccessful()){
                    switch (response.code()){
                        case 200 : {
                            bundle.putInt("result",721);
                            TradeDepPojo pojo= response.body();
                            bundle.putParcelable("tradeDepPojo",pojo);
                            break;
                        }
                        case 203 : {
                            bundle.putInt("result",9401);
                            break;
                        }
                        case 204 : {
                            bundle.putInt("result",722);
                            break;
                        }
                        default : {
                            Log.i(TAG,"unknown status code :"+response.code());
                            bundle.putInt("result",9001);
                            break;
                        }
                    }
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<TradeDepPojo> call, Throwable t) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }
    public void mapRefresh(Double myLat, Double myLon, int zoom){
        router_Main.MapApiInterface service = new ServiceGenerator(mContext).createService(router_Main.MapApiInterface.class);
        Call<MapRepo> call = service.getMapInfo(myLat.toString(),myLon.toString(),zoom);
        call.enqueue(new Callback<MapRepo>() {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            @Override
            public void onResponse(Call<MapRepo> call, Response<MapRepo> response) {
                if(response.code()==200){
                    MapRepo mapRepo = response.body();
                    bundle.putInt("result",521);
                    bundle.putParcelable("mapRepo",mapRepo);
                }else{
                    bundle.putInt("result",522);
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);

            }

            @Override
            public void onFailure(Call<MapRepo> call, Throwable t) {
                Log.i(TAG,"mapRefresh onFailed err: "+t.getMessage());
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });

    }
    public void sign(Bundle signInfo){
        File f = convertImgToFile(signInfo.getString("image"));
        if(!f.exists()){
            //오류 보내주자
            return;
        }


        router_Main.accountInterface service = new ServiceGenerator(mContext).createService(router_Main.accountInterface.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),f);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",f.getName(),requestBody);
        RequestBody userId = RequestBody.create(MultipartBody.FORM,signInfo.getString("userId"));
        RequestBody userPw = RequestBody.create(MultipartBody.FORM,signInfo.getString("userPw"));
        RequestBody userName = RequestBody.create(MultipartBody.FORM,signInfo.getString("userName"));
        RequestBody userPhone = RequestBody.create(MultipartBody.FORM,signInfo.getString("userPhone"));

        Call<ResponseBody> call = service.Sign(body,userId,userPw,userName,userPhone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();

                if(response.code()==201){
                    bundle.putInt("result",111);
                }else if(response.code()==400){
                    Log.i(TAG,"sign in retun 400 server code, message below :"+response.message());
                    bundle.putInt("result",112);
                }else{
                    Log.i(TAG,"sign in server code un expected :"+response.code());
                    bundle.putInt("result",9110);

                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                Log.i(TAG,"sign in failed, throwable :"+t.getMessage());
                bundle.putInt("result",9110);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }
    public void login(String id,String pw){
        router_Main.accountInterface service = new ServiceGenerator(mContext).createService(router_Main.accountInterface.class);
        Call<Void> call = service.login(id,pw);
        call.enqueue(new Callback<Void>() {
            Message msg=Message.obtain();
            Bundle bundle = new Bundle();
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG,"값 확인용"+call.request().toString());
                if(response.code()==202){
                    bundle.putInt("loginResult",211);
                    Log.i(TAG,"login success");
                }else if(response.code()==401){ //passport return statusCode 401, cannot change ATM
                    bundle.putInt("loginResult",212);
                    Log.i(TAG,"login failed");
                }else{
                    bundle.putInt("loginResult",9001);
                    Log.i(TAG,"login failed with crazy reasom code:"+response.code());
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                bundle.putInt("loginResult",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
                Log.i(TAG,"server connect failed");
            }
        });

    }
    public void checkSessionAlive(){
        router_Main.accountInterface service = new ServiceGenerator(mContext).createService(router_Main.accountInterface.class);
        Call<Void> call = service.checkSessionAlive();
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG,"야호"+call.request().headers().size());
                for(int i = 0 ; i <call.request().headers().size(); i++ ){
                    String tmp = call.request().headers().name(i);
                    Log.i(TAG,"루프 key["+tmp+"] value["+call.request().header(tmp)+"]");
                }
                Message msg=Message.obtain();
                Bundle bundle = new Bundle();
                if(response.code()==202){
                    bundle.putInt("sessionResult",401);
                    msg.setData(bundle);
                }else if(response.code()==203) {
                    bundle.putInt("sessionResult",402);
                    msg.setData(bundle);

                }else{
                    Log.i(TAG,"auth failed statuscode :"+response.code());
                    for(int i = 0 ; i < call.request().headers().size();i++){
                        String tmp = call.request().headers().name(i);
                        Log.i(TAG,"헤더 "+tmp+" ["+call.request().headers().get(tmp));
                    }
                    bundle.putInt("sessionResult",9400);
                    msg.setData(bundle);
                }
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(TAG,"에러나가신다 "+t.getMessage());
                Message msg=Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putInt("sessionResult",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }
    private File convertImgToFile(String imageName){
     File f = null;
     try{
         Bitmap bitmap = BitmapFactory.decodeStream(mContext.openFileInput(imageName));
         f = new File(mContext.getCacheDir(),imageName);
         f.createNewFile();
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
         byte[] bitmapData = bos.toByteArray();
         FileOutputStream fos = new FileOutputStream(f);
         fos.write(bitmapData);
         fos.flush();
         fos.close();
     }catch(FileNotFoundException e){
         e.printStackTrace();
         return null;
     }catch (Exception e){
         e.printStackTrace();
         return null;
     }
     return f;
    }
    public void requestUpdateMyProfileImageWithBitmap(Bundle data){
        File f = null;
        try {
            String dataName = data.getString("image");
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.openFileInput(dataName));
            f = new File(mContext.getCacheDir(),dataName);
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt("result",9310);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt("result",9311);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
            return;
        }
        router_Main.imageInterface service = new ServiceGenerator(mContext).createService(router_Main.imageInterface.class);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),f);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("image",f.getName(),requestBody);
        String usrId = data.getString("userId");
        RequestBody userId = RequestBody.create(MultipartBody.FORM,usrId);

        Call<ResponseBody> call = service.uploadMyImage(userId,body);
        call.enqueue(new Callback<ResponseBody>() {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    bundle.putInt("result",311);
                }else{
                    bundle.putInt("result",312);
                    Log.i(TAG,"upload image failed with server status code "+response.code());
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG,t.getMessage());
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });


    }
    public void requestUpdateMyProfileImage(Bundle data){ //프로필사진 업데이트 전송을 하는 부분, 리턴으로 mHandler에게 result 코드를 전송함

        router_Main.imageInterface service = new ServiceGenerator(mContext).createService(router_Main.imageInterface.class);
        File myFile = new File(data.getString("Uri"));
        final RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),myFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image",myFile.getName(),requestBody);
        String usrId = data.getString("userId");
        RequestBody userId = RequestBody.create(MultipartBody.FORM,usrId);

        Call<ResponseBody> call = service.uploadMyImage(userId,body);
        call.enqueue(new Callback<ResponseBody>() {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    //전송성공
                    Log.i(TAG,"image update request successfully");
                    bundle.putInt("result",311);
                }
                else{
                    //전송실패
                    Log.i(TAG,"image update request failed code :"+response.code());
                    bundle.putInt("result",312);
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //연결실패
                Log.i(TAG,"reqyest failed message :"+t.fillInStackTrace());
                Log.i(TAG,call.request().body().toString());
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

        });
    }

    public void requestMyProfileImage(Bundle data){ //내 이미지를 요청하는 부분,
        router_Main.imageInterface service = new ServiceGenerator(mContext).createService(router_Main.imageInterface.class);
        Log.i(TAG,"what is wrong with userId?"+data.getString("userId"));
        Call<ResponseBody> call = service.downloadMyImage(data.getString("userId"));
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                if(response.code()==200){//연결이 성공되고, res가 있는경우
                    bundle.putInt("result",321);
                    try{
                        bundle.putByteArray("data", response.body().bytes());
                    }catch (IOException e){
                        Log.i(TAG,"response body cast to String failed");
                    }
                    msg.setData(bundle);
                    mHandler.sendMessage(msg); //ioHandler 에 requestCode 1 을 알려줍니다

                }else{//res가 없는 경우,err
                    Log.i(TAG,"error, response failed"+response.code());
                    bundle.putInt("result",322);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //연결실패
                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                Log.i(TAG,t.getMessage());
                bundle.putInt("result",9999);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        });
    }



}
