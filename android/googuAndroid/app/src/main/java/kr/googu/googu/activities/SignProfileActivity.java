package kr.googu.googu.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import kr.googu.googu.R;
import kr.googu.googu.resourceController.SharedPreferencesBeans;
import kr.googu.googu.serverConnector.serverConnecterST;

/**
 * Created by Jay on 2018-01-29.
 */

public class SignProfileActivity extends AppCompatActivity {
    ImageView myProfileIV;
    Button btnSelectImg, btnTakeImg, btnRestoreImg, btnCancel, btnOkay;
    Handler mHandler;
    Bitmap photo;
    Uri fileUri;
    final static String TAG ="ProfileActivity";
    SharedPreferencesBeans spb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_steptwo);
        myProfileIV = (ImageView)findViewById(R.id.signImg_lv_myImg);
        btnSelectImg = (Button)findViewById(R.id.signImg_btn_selectImg);
        btnTakeImg = (Button)findViewById(R.id.signImg_btn_takeImg);
        btnRestoreImg = (Button)findViewById(R.id.signImg_btn_restoreImg);
        btnCancel = (Button)findViewById(R.id.signImg_btn_cancel);
        btnOkay = (Button)findViewById(R.id.signImg_btn_okay);
        mHandler = new MyHandler(this);
        spb = new SharedPreferencesBeans(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myProfileIV.setImageResource(R.drawable.defaultmyimage);
        setBtnListener();
    }
    private void setBtnListener(){
        btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSelectPicture();
            }
        });
        btnTakeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTakePicture();
            }
        });
        btnRestoreImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myProfileIV.setImageResource(R.drawable.defaultmyimage);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다음 회원가입창으로 바로 넘김
            }
        });
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photo!=null){
                    doUploadPicture();
                } //서버통신으로 이미지 저장.
            }
        });
    }
    private void doUploadPicture(){
        final String data = createFromBitmap(photo);
        Log.i(TAG,data);
        if(!data.isEmpty()){
            Thread th = new Thread(new serverConnecterST(mHandler,this){
                @Override
                public void run() {
                    super.run();
                    Bundle bundle = new Bundle();
                    bundle.putString("image",data);
                    bundle.putString("userId",spb.getValue("userId","guest"));
                    this.requestUpdateMyProfileImageWithBitmap(bundle);
                }
            });
            th.start();
        }
    }
    private void doTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_"+String.valueOf(System.currentTimeMillis()+".jpg");
        fileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));
        //fileUri = Uri.parse(myImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intent,0);
    }

    private void doSelectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK)
            return;
        switch (requestCode){
            case 1:
                fileUri=data.getData();
                Log.i(TAG,"선택된게"+fileUri.getPath().toString()+fileUri.toString());
            case 0:{
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(fileUri,"image/*");
                intent.putExtra("outputX",400);
                intent.putExtra("outputY",400);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.name());
                startActivityForResult(intent,2);
                break;
            }
            case 2: {
                Log.i(TAG,"resultCode failed?"+resultCode);
                if (resultCode != RESULT_OK)
                    return;
                photo = null;
                Bundle extra = data.getExtras();
                Log.i(TAG,"extra is null?"+extra.isEmpty());
                if (extra != null) {
                    photo = extra.getParcelable("data");
                    myProfileIV.setImageBitmap(photo);
                    break;
                }
                File f = new File(fileUri.getPath());
                if (f.exists())
                    f.delete();
                myProfileIV.setImageBitmap(photo);
                Log.i(TAG, "안대?");
            }
            default: return;
        }
    }
    private String createFromBitmap(Bitmap img){
        String imgName = "myImage";
        try{
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            FileOutputStream fo = openFileOutput(imgName, Context.MODE_PRIVATE);
            fo.write(bytes.toByteArray());
            fo.close();
        }catch (Exception e){
            e.printStackTrace();
            imgName=null;
        }
        return imgName;
    }
    private static class MyHandler extends Handler {
        private final WeakReference<SignProfileActivity> mActivity;
        private SharedPreferencesBeans spb;
        public Thread th;

        private MyHandler(SignProfileActivity mActivity) {
            this.mActivity = new WeakReference<SignProfileActivity>(mActivity);
            spb = new SharedPreferencesBeans(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
