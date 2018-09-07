package kr.googu.googu.locationManager;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Jay on 2018-01-11.
 */

public class LocationRequestor {
    FusedLocationProviderClient mFusedLocationClient;
    SettingsClient mSettingsClient;
    Location mLastLocation;
    Activity mActivity;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    String mLastUpdateTime;
    LocationSettingsRequest mLocationSettingRequest;
    final static String TAG = "LocationRequenster";
    Handler mHandler;

    LocationHandler locHandler;
    Thread locUpdaterTh;
    Context mContext;
    long intervalFrequency;
    long fastIntervalFrequency;

    public LocationRequestor(Activity mActivity, Handler mHandler,Context mContext,long intervalFrequency, long fastIntervalFrequency){ //생성자
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mActivity = mActivity;
        this.intervalFrequency = intervalFrequency;
        this.fastIntervalFrequency = fastIntervalFrequency;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mSettingsClient =  LocationServices.getSettingsClient(mActivity);
        locHandler = new LocationHandler(this,intervalFrequency);
        locUpdaterTh = new Thread(new LocationUpdater());
    }

    public void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {//위치 결과를 받는곳?
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation(); //왜 이지랄을 할까?
                mLastUpdateTime  = DateFormat.getTimeInstance().format(new Date());//너도말이야

            }
        };
    }
    public boolean createLocationRequest(long intervalFrequency, long fastIntervalFrequency, int proirityLevel){
        this.intervalFrequency = intervalFrequency;
        this.fastIntervalFrequency = fastIntervalFrequency;
        try{
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(intervalFrequency);
            mLocationRequest.setFastestInterval(fastIntervalFrequency);
            switch (proirityLevel){
                case 1:{
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    break;
                }
                case  2:{
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    break;
                }
                case  3:{
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                    break;
                }
                case  4:{
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                    break;
                }
                default:{
                    return false;
                }
            }
        }catch (Exception e){
            Log.i(TAG,"createLocationReqeust failed, 인터벌 주기 :"+intervalFrequency + "\n빠른 주기 :"+fastIntervalFrequency + "\n우선도 래벨 :"+proirityLevel);
            return false;
        }finally {
            return true;
        }
    }
    public void createLocationRequest(){ //리퀘스터 선언, 경우에따라 다양한 값 수정이 되게 할것.
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }
    public void setLocationRequesterPriority(int proirityLevel){
        switch (proirityLevel){
            case 1:{
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
            }
            case  2:{
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            }
            case  3:{
                mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;
            }
            case  4:{
                mLocationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                break;
            }
            default:{
                return;
            }
        }
    }

    public void buildLocationSettingsRequest(){ // 서비스 빌드
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingRequest = builder.build();
    }
    
    public void startLocationUpdates(){
        locUpdaterTh.run();
    }

    @SuppressWarnings("MissingPermission")
    public Location getLastLocation(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() !=null){
                    mLastLocation = task.getResult();
                }else{
                    Log.i("Location","error at getLastLocationReturn");
                }
            }
        });
        return mLastLocation;
    }
    @SuppressWarnings("MissingPermission")
    public Location getLastLocationForSendHandleMsg(){
        mFusedLocationClient.getLastLocation().addOnCompleteListener(mActivity, new OnCompleteListener<Location>() {
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();

            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() !=null){
                    mLastLocation = task.getResult();
                    bundle.putInt("result",515);
                    bundle.putDouble("locationLat",task.getResult().getLatitude());
                    bundle.putDouble("locationLon",task.getResult().getLongitude());

                }else{
                    Log.i("Location","error at getLastLocationReturn");
                    bundle.putInt("result",516);
                }
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }

        });


        return mLastLocation;
    }
    public class LocationUpdater implements Runnable{

        @Override
        public void run() {
            mSettingsClient.checkLocationSettings(mLocationSettingRequest).addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Log.i("location-update","locationupdate enable");

                    try {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            Message msg = Message.obtain();
                            Bundle bundle = new Bundle();
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.i("location-update","locationupdate success");
                                    mLastLocation = getLastLocation();
                                    Log.i("location-update","locationupdate success - update done");
                                    bundle.putInt("result",511);
                                    bundle.putDouble("locationLat",mLastLocation.getLatitude());
                                    bundle.putDouble("locationLon",mLastLocation.getLongitude());
                                    bundle.putParcelable("location",mLastLocation);
                                    //mv.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mLastLocation.getLatitude(),mLastLocation.getLongitude()),3,true);
                                    //old style code
                                }else{
                                    Log.i("location-update","locationupdate failed");
                                    bundle.putInt("result",512);
                                }
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            }
                        });
                    }catch (SecurityException e){
                        Log.i(TAG,"513 exception :"+e.getMessage());
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("result",513);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"514 exception : "+e.getMessage());
                    Message msg = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putInt("result",514);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            });
        }
    }

    public class LocationHandler extends Handler {
        private final WeakReference<LocationRequestor> mActivity;
        private boolean flag;
        private long intervalFreq;
        public void setFlag(boolean flag) {
            this.flag = flag;

        }

        private LocationHandler(LocationRequestor mActivity,long intervalFreq) {
            this.mActivity = new WeakReference<LocationRequestor>(mActivity);
            this.intervalFreq = intervalFreq;
            flag =false;
        }

        public boolean isFlag() {
            return flag;
        }

        public long getIntervalFreq() {
            return intervalFreq;
        }

        public void setIntervalFreq(long intervalFreq) {
            this.intervalFreq = intervalFreq;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(!msg.getData().isEmpty()){
                flag = msg.getData().getBoolean("setFlag");
                intervalFreq = msg.getData().getLong("intervalFreq");
            }
            mActivity.get().getLastLocation();
            mActivity.get().startLocationUpdates();
            if(flag){
                this.sendEmptyMessageDelayed(0,intervalFreq);
            }else{
                Log.i(TAG,"LocationRequester handler flag :"+flag);
            }
        }
    }

    public LocationHandler getLocHandler() {
        return locHandler;
    }
}

