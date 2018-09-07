package kr.googu.googu.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

import kr.googu.googu.R;
import kr.googu.googu.resourceController.SharedPreferencesBeans;
import kr.googu.googu.serverConnector.serverConnecterST;

/**
 * Created by Jay on 2018-01-27.
 */

public class SignActivity extends AppCompatActivity {
    EditText id, pw, pw2, name, phone;
    Button btnOkay, btnNo;
    Handler myHandler;

    ImageView myProfileIV;
    Button btnSelectImg, btnTakeImg, btnRestoreImg;
    Handler mHandler;
    Bitmap photo;
    Uri fileUri;
    SharedPreferencesBeans spb;

    final static String TAG = "SignActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Log.i(TAG,"on create");
        id = (EditText) findViewById(R.id.sign_et_id);
        pw = (EditText) findViewById(R.id.sign_et_pw);
        pw2 = (EditText) findViewById(R.id.sign_et_pw2);
        name = (EditText) findViewById(R.id.sign_et_name);
        phone = (EditText) findViewById(R.id.sign_et_phone);
        myProfileIV = (ImageView)findViewById(R.id.sign_lv_myImg);
        btnSelectImg = (Button)findViewById(R.id.sign_btn_selectImg);
        btnTakeImg = (Button)findViewById(R.id.sign_btn_takeImg);
        btnRestoreImg = (Button)findViewById(R.id.sign_btn_restoreImg);

        btnOkay = (Button) findViewById(R.id.sign_btn_okay);
        btnNo = (Button) findViewById(R.id.sign_btn_no);

        spb = new SharedPreferencesBeans(this);
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 취소
            }
        });
//        btnOkay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(photo!=null){
//                    doUploadPicture();
//                } //서버통신으로 이미지 저장.
//            }
//        });
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkData()) {
                    final String data = createFromBitmap(photo);
                    Thread th = new Thread(new serverConnecterST(myHandler, v.getContext()) {
                        @Override
                        public void run() {
                            super.run();
                            Bundle bundle = new Bundle();
                            bundle.putString("image",data);
                            bundle.putString("userId",id.getText().toString());
                            bundle.putString("userPw",pw.getText().toString());
                            bundle.putString("userName",name.getText().toString());
                            bundle.putString("userPhone",phone.getText().toString());
                            sign(bundle);
                        }
                    });
                    th.start();
                }

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
    private boolean checkData() {
        if (id.getText().toString().equals("") || id.getText().toString().equals(" ")) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "ID 입력란이 공백", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (pw.getText().toString().equals("") || pw2.getText().toString().equals("") || pw.getText().toString().equals(" ") || pw2.getText().toString().equals(" ")) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "PW미입력", Snackbar.LENGTH_LONG).show();
            return false;
        }
        String StrPw1 = pw.getText().toString();
        String StrPw2 = pw2.getText().toString();
        if (!StrPw1.equals(StrPw2)) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "PW불일치", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (name.getText().toString().equals("") || name.getText().toString().equals(" ")) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "이름 입력란이 공백", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (phone.getText().toString().equals("") || phone.getText().toString().equals(" ")) {
            Snackbar.make(this.getWindow().getDecorView().getRootView(), "전화번호 입력란이 공백", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(photo==null){
            photo= BitmapFactory.decodeResource(getResources(),R.drawable.defaultmyimage);
        }
        return true;

    }

    private static class MyHandler extends Handler {
        private final WeakReference<SignActivity> mActivity;
        private SharedPreferencesBeans spb;

        private MyHandler(SignActivity mActivity) {
            this.mActivity = new WeakReference<SignActivity>(mActivity);
            spb = new SharedPreferencesBeans(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("result")){
                case 111 : {
                    Log.i(TAG,"handler code 111 ");
                    spb.put("userId",mActivity.get().id.getText().toString());
                    spb.put("userPw",mActivity.get().pw.getText().toString());
                    Snackbar.make(mActivity.get().getWindow().getDecorView().getRootView(),"회원가입 성공! 다음단계로 넘어갑니다 . . .",Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(mActivity.get().getApplicationContext(),LoginActivity.class);
                    mActivity.get().startActivity(intent);
                    break;
                }
                case 112 : {
                    Snackbar.make(mActivity.get().getWindow().getDecorView().getRootView(),"회원가입 실패! 중복된 아이디 . . .",Snackbar.LENGTH_SHORT).show();
                    Log.i(TAG,"handler code 112 ");
                    break;
                }
                case 113 : {
                    Snackbar.make(mActivity.get().getWindow().getDecorView().getRootView(),"회원가입 실패! 중복된 전화번호 . . .",Snackbar.LENGTH_SHORT).show();
                    Log.i(TAG,"handler code 113 ");
                    break;
                }
                case 9110 : {
                    Log.i(TAG,"handler code 9110 ");
                    break;
                }
                case 9999 : {
                    Log.i(TAG,"handler code 9999 ");
                    break;
                }
                default: {
                    Log.i(TAG,"unknown handler code :"+msg.getData().getInt("result"));
                    break;
                }
            }
        }
    }
}

