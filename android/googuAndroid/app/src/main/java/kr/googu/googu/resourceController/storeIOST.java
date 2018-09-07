package kr.googu.googu.resourceController;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * 수정 필요
 * Created by Jay on 2018-01-05.
 */

public class storeIOST implements Runnable {
    Handler mHandler,ioHandler;
    final static String myImagePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Googu/MyImage.jpg";
    final static String TAG = "myImageManagerTh";
    final static int _SAVE_MY_IMAGE_FROM_SERVER_REQ = 1;

    public storeIOST(Handler mainHandler) {
        this.mHandler = mainHandler;
        ioHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.getData().getInt("requestCode")){
                    case _SAVE_MY_IMAGE_FROM_SERVER_REQ :{
                        Log.i(TAG,"request my image start");
                        //Log.i(TAG,"save image:"+saveMyImage(msg.getData().getString("data")));
                        boolean result =saveMyImageFromServerRequest(msg.getData().getByteArray("data"));
                        Message msgToMainT = Message.obtain();
                        Bundle bundle = new Bundle();

                        if(result){
                            bundle.putInt("result",201);
                        }else{
                            bundle.putInt("result",203);
                        }
                        msgToMainT.setData(bundle);
                        mHandler.sendMessage(msgToMainT);

                    }
                }
            }
        };
    }

    public Handler getIoHandler() {
        return ioHandler;
    }

    public boolean saveMyImageFromServerRequest(byte[] dataP){
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        try{
            ResponseBody realData = ResponseBody.create(MediaType.parse("image/*"),dataP);
            File myImageFile = new File(myImagePath);
            InputStream is = null;
            OutputStream os = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = realData.contentLength();
                long fileSizeDownload = 0;

                is = realData.byteStream();
                os = new FileOutputStream(myImageFile);
                while (true) {
                    int read = is.read(fileReader);
                    if (read == -1)
                        break;
                    os.write(fileReader, 0, read);
                    fileSizeDownload += read;
                    Log.i(TAG, "file download: " + fileSizeDownload + "/" + fileSize);
                }
                os.flush();
            } catch (FileNotFoundException e) {
                Log.i(TAG, e.getMessage());
                return false;
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
                return false;
            } finally {
                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if ( os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }catch (Exception e){
            Log.i(TAG,e.getMessage());
            return false;
        }finally {
           return true;
        }
    }

    @Override
    public void run() {

    }
}