package kr.googu.googu.activities.subActivities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import kr.googu.googu.R;
import kr.googu.googu.staticVars.StaticVars;

/**
 * Created by Jay on 2018-01-27.
 */

public class AddressPopupActivity extends AppCompatActivity{
    Handler mHandler;
    WebView wv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initWebView();
    }

    private void initWebView() {
        wv = (WebView)findViewById(R.id.wv_address_popup_window);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.addJavascriptInterface(new jsInterfaceForAddress(),"jsInterfaceForAddress");
        wv.loadUrl(StaticVars.getServerUrl()+"/loadAddressPopupForMobile");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private class jsInterfaceForAddress{
        public jsInterfaceForAddress() {

        }

        @JavascriptInterface
        public void setAddress(String zoneCode,String roadAddress, String buildingName){

        }

    }
}
