package kr.googu.googu.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import kr.googu.googu.R;
import kr.googu.googu.serverConnector.serverConnecterST;
import kr.googu.googu.resourceController.SharedPreferencesBeans;

/*
* 로그인 창, 미완성, 회원가입 창과 연결 안됨.
* */

public class LoginActivity extends AppCompatActivity {
    private final static String TAG="로그인 엑티비티";
    EditText etUserId,etUserPw;
    Button btnLogin,btnSignin;
    SharedPreferencesBeans spb;
    TextView tvFlashMsg;

    private final MyHandler myHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserId = (EditText)findViewById(R.id.login_et_userId);
        etUserPw = (EditText)findViewById(R.id.login_et_userPw);
        btnLogin = (Button)findViewById(R.id.login_btn_login);
        btnSignin = (Button)findViewById(R.id.login_btn_sign);
        tvFlashMsg = (TextView)findViewById(R.id.login_tv_flashMsg);
        spb = new SharedPreferencesBeans(this);
        settingOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        etUserId.setText(spb.getValue("userId",""));
        etUserPw.setText(spb.getValue("userPw",""));
        //아이디와 비밀번호의 캐쉬값을 가져오는 부분, 캐쉬값이 없으면 defVal 이 된다.
    }

    private void settingOnClickListener(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 버튼을 눌렀을 경우
                Thread th = new Thread(new serverConnecterST(myHandler,getApplicationContext()){
                    @Override
                    public void run() {
                        super.run();
                        this.login(etUserId.getText().toString(),etUserPw.getText().toString());
                    }
                });
                th.start();

            }
        });//serverConnecterST.login(userId,userPw) 를 호출한다. 서브스레드로 작동함.
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignActivity.class);
                startActivity(intent);
            }
        });
    }


    private static class MyHandler extends Handler {//메인 헨들러 약한 참조 타입으로 선언, 이유는 https://juicybrainjello.blogspot.kr/2018/01/android-using-mainhandler-cause-memory.html
        private final WeakReference<LoginActivity> mActivity;
        private SharedPreferencesBeans spb;
        private MyHandler(LoginActivity mActivity) {
            this.mActivity = new WeakReference<LoginActivity>(mActivity);
            spb = new SharedPreferencesBeans(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginActivity loginActivity = mActivity.get();
            if(loginActivity != null){
                switch (msg.getData().getInt("loginResult")){
                    case 211 : {// 로그인 성공
                        Log.i(TAG,"handle msg success");
                        saveLoginInfo(loginActivity.etUserId.getText().toString(),loginActivity.etUserPw.getText().toString()); // 캐쉬값에 아이디와 비밀번호를 수정해준다.
                        Snackbar.make(mActivity.get().getWindow().getDecorView().getRootView(),"로그인 성공",Snackbar.LENGTH_SHORT).show();
                        //메인화면으로 넘어가기
                        Intent intent = new Intent(loginActivity.getApplicationContext(),MapActivity.class);
                        loginActivity.startActivity(intent);
                        break;
                    }
                    case 212 : {// 아이디 혹은 비밀번호 오류
                        Log.i(TAG,"handle msg wrong info");
                        drawFlashMsg(loginActivity);
                        break;
                    }
                    case 9210 : {// 예기치 못한 오류
                        Log.i(TAG,"handle msg server conn err,(9210)unknow status code");
                    }
                    default:{// 이게 뜨면 심각한 문제
                        Log.i(TAG,"handle msg default");
                        break;
                    }
                }
            }
        }

        private void drawFlashMsg(LoginActivity loginActivity) {
            loginActivity.tvFlashMsg.setText("wrong id or password ! ! !");
        }


        private void saveLoginInfo(String userId, String userPw) {
            spb.put("userId",userId);
            spb.put("userPw",userPw);
        }// 캐쉬값에 아이디와 비밀번호를 수정해준다.
    }
}