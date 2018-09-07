package kr.googu.googu;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
/*
* 서버통신중, Response 에 setCookie 명령이 있을떄 이 클래스가 작동된다, 현제 구현한것은 로그인시, 세션 id를 쿠키에 담는 기능 하나뿐인데, 이(로그인) 이외의 작업중,
* 해당 클래스가 작동하게 되면, 안드로이드 담당자와 상의가 필요함.
* */
public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    private final static String TAG = "RECEIVECOOKIE";
    public static final String PREF_COOKIES = "PREF_COOKIES";
    public ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            Log.i(TAG,"서버 response 에 cookie set을 요청, 현제 로그인시에만 구현, 그 이외작업에 출현시 보고요망!!");
            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            String header = originalResponse.headers("Set-Cookie").get(0);
            int indexPos = header.indexOf(";");
            header = header.substring(0,indexPos);
            memes.putString(PREF_COOKIES, header).apply();
            memes.commit();
        }

        return originalResponse;
    }
}
