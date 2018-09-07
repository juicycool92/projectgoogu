package kr.googu.googu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by H on 2017-12-07.
 */

public class LoginRequest extends StringRequest{
    final static private String URL = "http://www.googu.kr/loginAndroid";
    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userID);
        parameters.put("userPw", userPassword);
}

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
