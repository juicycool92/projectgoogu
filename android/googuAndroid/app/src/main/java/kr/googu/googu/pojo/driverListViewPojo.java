package kr.googu.googu.pojo;

import android.content.Context;
import android.graphics.drawable.Drawable;

import kr.googu.googu.activities.MapActivity;

/**
 * Created by Jay on 2018-01-17.
 */

public class driverListViewPojo {
    private Drawable driverImage;
    private String driverId;
    private Double driverLat;
    private Double driverLon;
    private Context mContext;
    private MapActivity mMapActivity;
    public driverListViewPojo(Context mContext, Drawable driverImage, String driverId, Double driverLat, Double driverLon, MapActivity mMapActivity) {
        this.mContext=mContext;
        this.driverImage = driverImage;
        this.driverId = driverId;
        this.driverLat=driverLat;
        this.driverLon=driverLon;
        this.mMapActivity=mMapActivity;
    }
    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }




    public Drawable getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(Drawable driverImage) {
        this.driverImage = driverImage;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Double getDriverLat() {
        return driverLat;
    }

    public void setDriverLat(Double driverLat) {
        this.driverLat = driverLat;
    }

    public Double getDriverLon() {
        return driverLon;
    }

    public void setDriverLon(Double driverLon) {
        this.driverLon = driverLon;
    }

    public MapActivity getmMapActivity() {
        return mMapActivity;
    }

    public void setmMapActivity(MapActivity mMapActivity) {
        this.mMapActivity = mMapActivity;
    }
}
