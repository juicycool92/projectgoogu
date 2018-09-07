package kr.googu.googu.serverConnector;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.IOException;

import kr.googu.googu.ReceivedCookiesInterceptor;
import kr.googu.googu.staticVars.StaticVars;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by Jay on 2018-01-02.
 * Retrofit 서비스 연결을 위한 bridge,
 */

public class ServiceGenerator {
    HttpLoggingInterceptor logging;
    private OkHttpClient.Builder httpClients;
    private Retrofit.Builder builer;
    private Retrofit retrofit;
    private  OkHttpClient.Builder httpClient;
    public static final String PREF_COOKIES = "PREF_COOKIES";
    public static final String TAG = "서버제네레이터";


    public  <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }

    public ServiceGenerator(final Context context) {

        logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClients = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60,java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS);


        httpClients.addInterceptor(logging);
        httpClients.interceptors().add(new ReceivedCookiesInterceptor(context));
        httpClients.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                String cookieNow = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_COOKIES,"");
                Request.Builder requestBuilder = original.newBuilder().addHeader("Cookie",cookieNow);
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });


        builer = new Retrofit.Builder()
                .baseUrl(StaticVars.getServerUrl())
                .client(httpClients.build())
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builer.build();

        httpClient = new OkHttpClient.Builder();


    }
}
