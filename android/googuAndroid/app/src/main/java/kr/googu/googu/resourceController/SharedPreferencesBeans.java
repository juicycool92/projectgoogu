package kr.googu.googu.resourceController;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jay on 2018-01-09.
 * 쿠키를 관리하는 beans 클래스
 */

public class SharedPreferencesBeans {
    final static String _PREFS_NAME = "GooguPrefFile";
    final static String _REGISTERED = "isFirstStart";
    static Context mContext;

    public SharedPreferencesBeans(Context mContext) {
        this.mContext = mContext;
    }
    public void put(String key,String value){
        SharedPreferences sp = mContext.getSharedPreferences(_PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void put(String key,boolean value){
        SharedPreferences sp = mContext.getSharedPreferences(_PREFS_NAME,0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public String getValue(String key,String defVal){
        SharedPreferences sp = mContext.getSharedPreferences(_PREFS_NAME,0);
        try{
            return sp.getString(key,defVal);
        }catch (Exception e){
            return defVal;
        }
    }
    public Boolean getValue(String key,Boolean defVal){
        SharedPreferences sp = mContext.getSharedPreferences(_PREFS_NAME,0);
        try{
            return sp.getBoolean(key,defVal);
        }catch (Exception e){
            return defVal;
        }
    }

    public static String getPrefsName() {
        return _PREFS_NAME;
    }

    public static String getRegistered() {
        return _REGISTERED;
    }
}
