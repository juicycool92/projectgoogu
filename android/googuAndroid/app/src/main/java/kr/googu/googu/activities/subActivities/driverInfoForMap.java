package kr.googu.googu.activities.subActivities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import kr.googu.googu.R;
import kr.googu.googu.resourceController.SharedPreferencesBeans;
import kr.googu.googu.serverConnector.serverConnecterST;
import kr.googu.googu.pojo.driverListViewPojo;
import kr.googu.googu.pojo.driverLiteInfoPojo;

/**
 * Created by Jay on 2018-01-19.
 */

public class driverInfoForMap extends AppCompatActivity {
    ImageView driverPic;    //driverProfile_ivDriverProfile
    TextView driverName;    //driverProfile_tvDriverName
    TextView driverRank;    //driverProfile_tvDriverRank
    TextView driverContext; //driverProfile_tvDriverContext
    String driverId;

    ImageButton btnClose;

    MyHandler mHandler;

    DisplayMetrics ds;
    WindowManager.LayoutParams wm;

    driverListViewPojo tmpInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);
        driverId = getIntent().getStringExtra("driverId");
        Log.i("시발","이까진왔나"+driverId);

        driverPic = (ImageView) findViewById(R.id.driverProfile_ivDriverProfile);
        driverName = (TextView) findViewById(R.id.driverProfile_tvDriverName);
        driverRank = (TextView) findViewById(R.id.driverProfile_tvDriverRank);
        driverContext = (TextView) findViewById(R.id.driverProfile_tvDriverContext);
        mHandler = new MyHandler(this);
        btnClose = (ImageButton)findViewById(R.id.driverProfile_ibClose);

        ds= new DisplayMetrics();
        wm=this.getWindow().getAttributes();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setWindow();

        Log.i("시발","이까진왔나");
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Thread th = new Thread(new serverConnecterST(mHandler,getApplicationContext()){
            @Override
            public void run() {
                super.run();
                Log.i("시발","이까진왔나th");
                this.driverInfoLiteRequest(driverId);
                Log.i("시발","이까진왔나th1");
            }
        },"driverInfoRequester");
        th.start();
    }
    private void setTmpInfo(){
        driverPic.setBackground(tmpInfo.getDriverImage());
    }
    private void setWindow(){
        getWindowManager().getDefaultDisplay().getMetrics(ds); //window info 긁어주는애
        int width = ds.widthPixels; //ds로부터 기기의 고유 사이즈를 측정한뒤
        int height = ds.heightPixels;
        getWindow().setLayout((int)(width*0.95),(int)(height*0.25));//해당 엑티비티를 알맞는 사이즈로 변형한다
        wm.gravity= Gravity.BOTTOM;//해당 엑티비티를 바닥에서 작동되게 한다

    }
    public void setDriverLiteInfo(driverLiteInfoPojo pojo){
        byte[] decodedString = Base64.decode(pojo.getDriverPic(),Base64.DEFAULT);
        Bitmap imgBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);
        Drawable d = new BitmapDrawable(getResources(),imgBitmap);
        driverPic.setImageDrawable(d);
        //driverPic.setBackground(pojo.getDriverPic());
        driverName.setText(pojo.getDriverName());
        driverRank.setText(pojo.getDriverRank());
        driverContext.setText(pojo.getDriverContext());
    }


    private static class MyHandler extends Handler {
        private final WeakReference<driverInfoForMap> mDriverInfoForMap;
        private SharedPreferencesBeans spb;

        private MyHandler(driverInfoForMap mDriverInfoForMap) {
            this.mDriverInfoForMap = new WeakReference<driverInfoForMap>(mDriverInfoForMap);
            spb = new SharedPreferencesBeans(mDriverInfoForMap);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("result")){
                case 611 : {
                    driverLiteInfoPojo pojo = msg.getData().getParcelable("driverInfo");
                    mDriverInfoForMap.get().setDriverLiteInfo(pojo);
                    break;
                }
                case 612 : {
                    break;
                }
                case 613 : {
                    break;
                }
                case 9401 : {
                    break;
                }
                case 9999 : {
                    break;
                }
                default: {

                }
            }
        }


    }
}
