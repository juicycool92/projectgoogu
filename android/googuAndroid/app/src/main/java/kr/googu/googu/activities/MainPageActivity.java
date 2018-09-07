package kr.googu.googu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;

import kr.googu.googu.R;
import kr.googu.googu.serverConnector.serverConnecterST;
import kr.googu.googu.resourceController.SharedPreferencesBeans;

/**
 * Created by Jay on 2018-01-09.
 * 앱의 최초 출발지점.
 */

public class MainPageActivity extends AppCompatActivity {
    final static String TAG = "메인페이지 엑티비티";
    SharedPreferencesBeans spb;
    MyHandler myHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spb = new SharedPreferencesBeans(this);//로컬 캐쉬 저장소
        myHandler = new MyHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!spb.getValue(SharedPreferencesBeans.getRegistered(),true)){
            //계정 등록이 안되어 있는 경우
            //로그인 창으로 가는 분기
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }else{
            //계정 등록은 되어있는 경우, server SessionManager에게 auth되어있는가 확인,
            checkSession();
        }
    }
    private void checkSession(){ //통신요청을 보내어, 해당 계정의 세션이 살아있는지 검사한다, 결과는 아래의  myHandler로 전달.
        final Thread th = new Thread(new serverConnecterST(myHandler,getApplicationContext()){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000); //임의로 만들어둔 대기시간, 추후 모든 작업이 완료되면 없어도 될것. 로딩 대기시간이 길면 줄일것.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.checkSessionAlive();
            }
        },"authCheckThread");//서브 스레드 생성, 동작. 해당 스레드는 checkSessionAlive 함수를 실행한다. 이 함수는 serverConnectST에서 확인
        th.start();

    }
    private static class MyHandler extends Handler{//메인 헨들러 약한 참조 타입으로 선언, 이유는 https://juicybrainjello.blogspot.kr/2018/01/android-using-mainhandler-cause-memory.html
        private final WeakReference<MainPageActivity> mActivity;

        private MyHandler(MainPageActivity mActivity) {
            this.mActivity = new WeakReference<MainPageActivity>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) { //결과값이 들어오면 k,v 로 올것. 헨들러 코드 참조
            super.handleMessage(msg);
            MainPageActivity mainPageActivity = mActivity.get();
            switch (msg.getData().getInt("sessionResult")){
                case 401 : {
                    Log.i(TAG,"통신성공, auth 중");
                    //메인 엑티비티로 넘어가자
                    Intent intent = new Intent(mainPageActivity.getApplicationContext(),MapActivity.class);
                    mainPageActivity.startActivity(intent);
                    break;
                }
                case 402 : {
                    Log.i(TAG,"통신성공, auth 해제");
                    Intent intent = new Intent(mainPageActivity.getApplicationContext(),LoginActivity.class);
                    mainPageActivity.startActivity(intent);
                    //로그인 필요
                    break;
                }
                default: {
                    Log.i(TAG,"통신실패, 받은 코드는"+msg.getData().getInt("sessionResult"));
                    break;
                }

            }
        }
    }
}
