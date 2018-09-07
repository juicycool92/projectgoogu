package kr.googu.googu;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.googu.googu.resourceController.SharedPreferencesBeans;
import kr.googu.googu.resourceController.storeIOST;
import kr.googu.googu.serverConnector.serverConnecterST;

/**
 * Created by Jay on 2018-01-01.
 * 프로필사진을 관리하는 기능들이 모여져 있는 클래스, 틀만 잡혀져 있기 때문에, 나중에 기능을 완성할때 이 함수를 이용할것.
 * 볼 필요가 없음. 폐기대상
 * serverConnectST
 * storeIoST
 * 두 쓰레드 클래스를 이용한다.
 *
 */

public class uploadImageTestActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSelect, btnClear, btnOkay;
    ImageView ivImage;
    TextView tvCurImage;
    AlertDialog imageDialog;
    Uri fileUri;
    final static String TAG = "오졌따";

    String myImage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Googu/MyImage.jpg";
    Bitmap photo;
    SharedPreferencesBeans spb;

    serverConnecterST connecterST;
    storeIOST ioST;
    Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);
        btnSelect = (Button)findViewById(R.id.btnSelectImage);

        btnClear = (Button)findViewById(R.id.btnClearImage);

        btnOkay = (Button)findViewById(R.id.btnOKAY);

        ivImage = (ImageView)findViewById(R.id.ivTestImage);
        tvCurImage=(TextView)findViewById(R.id.tvSelectedItemName);
        connecterST = new serverConnecterST(mainHandler,getApplicationContext());
        ioST = new storeIOST(mainHandler);
        settingMainHandler();
        spb = new SharedPreferencesBeans(this);
        displayImage();

    }

    @Override
    protected void onStart() {
        super.onStart();
        btnSelect.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnClear.setEnabled(false);
        btnOkay.setOnClickListener(this);

        connecterST.setIoHandler(ioST.getIoHandler());
    }

    private void settingMainHandler(){ //메인헨들러로써, 해당 클래스에서, 서브클래스를 호출했을때 결과값을 받는곳.
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.getData().getInt("result")) {
                    case 321: {
                        ivImage.setImageURI(Uri.parse(myImage));
                        loadImageFromStream(msg.getData().getByteArray("data"));
                        Snackbar.make(getWindow().getDecorView().getRootView(), "프로필사진 로드", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case 203: {
                        ivImage.setImageResource(R.drawable.defaultmyimage);
                        Snackbar.make(getWindow().getDecorView().getRootView(), "기기의 용량이 부족합니다!", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case 322: {
                        ivImage.setImageResource(R.drawable.defaultmyimage);
                        Snackbar.make(getWindow().getDecorView().getRootView(), "프로필사진이 설정되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case 311: {  //이미지 업로드 성공
                        Snackbar.make(getWindow().getDecorView().getRootView(), "프로필사진 업데이트 성공", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case 312: {//이미지 업로드 실패
                        deleteExistImage();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "프로필사진 업데이트 실패", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    case 9999: {//서버연결실패
                        Snackbar.make(getWindow().getDecorView().getRootView(), "연결실패, 네트워크 확인", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                    default: {
                        Log.i(TAG, "메인헨들러는 읽은값이 무엇인지 판별실패" + msg.getData().getInt("result"));
                        Snackbar.make(getWindow().getDecorView().getRootView(), "헨들러 오류", Snackbar.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
    }

    private void loadImageFromStream(final byte[] data) {
        Thread th = new Thread(new storeIOST(mainHandler){
            @Override
            public void run() {
                super.run();
                this.saveMyImageFromServerRequest(data);
            }
        });
        th.start();
    }

    private void displayImage(){ //이미지를 출력하는 함수, 이미지가있으면 단순히 출력이나, 없다면 서버에 요청을 보낸다.
        if(new File(myImage).exists()) {
            ivImage.setImageURI(Uri.parse(new File(myImage).toString()));
            btnClear.setEnabled(true);
        }else {
            Log.i(TAG,"reqyestMyImage 시도");
            requestMyImage();
        }
    }
    @Override
    public void onClick(View v) { //클릭 리스너들
        if(v.getId()==R.id.btnSelectImage){

            DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doTakePicture();
                }
            };

            final DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doSelectPicture();
                }
            };

            DialogInterface.OnClickListener cancleListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageDialog.dismiss();
                }
            };

            imageDialog = new AlertDialog.Builder(this)
                    .setTitle("upload test")
                    .setPositiveButton("촬영하기",cameraListener)
                    .setNeutralButton("앨범에서 선택",albumListener)
                    .setNegativeButton("취소",cancleListener)
                    .show();
        }
        else if(v.getId()==R.id.btnClearImage){
            deleteExistImage();
            Log.i(TAG,"클리거 클릭");
        }
        else if(v.getId()==R.id.btnOKAY){
            storeCropImage(photo,myImage);
            Log.i(TAG,"올바른 이미지 URI"+fileUri.toString());
            uploadFile();
        }
    }

    public void doTakePicture(){ //사진 촬영을 했을때
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url = "tmp_"+String.valueOf(System.currentTimeMillis()+".jpg");
        fileUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));
        //fileUri = Uri.parse(myImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intent,0);
    }

    public void doSelectPicture(){ //앨범에서 사진을 픽업할 때
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //사진 관련 기능들의 결과값 처리
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
                intent.putExtra("outputX",200);
                intent.putExtra("outputY",200);
                intent.putExtra("aspectX",1);
                intent.putExtra("aspectY",1);
                intent.putExtra("scale",true);
                intent.putExtra("return-data",true);
                intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.name());
                startActivityForResult(intent,2);
                break;
            }
            case 2:
                if(resultCode!=RESULT_OK)
                    return;
                Bundle extra = data.getExtras();
                if(extra!=null){
                    photo = extra.getParcelable("data");
                    ivImage.setImageBitmap(photo);
                    break;
                }
                File f = new File(fileUri.getPath());
                if(f.exists())
                    f.delete();
                default: return;
        }
    }
    private void storeCropImage(Bitmap photo,String filePath){ //자른 이미지를 저장하는 함수 이거 ioST로 옮길방법을 강구해 보자
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Googu";
        File directory_SmartWheel = new File(dirPath);
        if(!directory_SmartWheel.exists())
            directory_SmartWheel.mkdirs();
        File copyFile = new File(filePath);
        BufferedOutputStream bout = null;
        try{
            bout = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            photo.compress(Bitmap.CompressFormat.JPEG,100,bout);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,fileUri.fromFile(copyFile)));
            bout.flush();
            bout.close();
            displayImage();
        }catch (IOException e){
            Log.i(TAG,e.getMessage());
        }
    }
    private void deleteExistImage(){ //현제 보여주는 이미지를 삭제하는 함수.
        File delFile = new File(myImage);
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(delFile)));
        delFile.delete();
    }
    public void requestMyImage(){ //이미지를 서버로부터 요청하는 함수
        Log.i(TAG,"으음?"+spb.getValue("userId",""));



//        connecterST.conHandler.sendMessage(msg);
        Thread th = new Thread(new serverConnecterST(mainHandler,this){
            @Override
            public void run() {
                super.run();
                Bundle bundle2 = new Bundle();
                bundle2.putInt("requestType",310);
                bundle2.putString("userId",spb.getValue("userId",""));
                this.requestMyProfileImage(bundle2);
            }
        });
        th.start();
    }

    public void uploadFile(){ //현제 선택한 이미지를 서버에 업로드하여 내 이미지를 변경하는 함수

        //connecterST.conHandler.sendMessage(msg);
        Thread th = new Thread(new serverConnecterST(mainHandler,this){
            @Override
            public void run() {
                super.run();
                Bundle bundle = new Bundle();
                bundle.putInt("requestType",320);
                bundle.putString("Uri",myImage);
                bundle.putString("userId",spb.getValue("userId",""));
                this.requestUpdateMyProfileImage(bundle);
            }
        },"mapUpdateThread");
        th.start();
    }

}

