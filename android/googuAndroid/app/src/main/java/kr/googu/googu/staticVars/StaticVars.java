package kr.googu.googu.staticVars;

/**
 * Created by Jay on 2017-12-27.
 * 전역변수들을 만들어 관리하는곳.
 */

public final class StaticVars {
    final private static String _SERVER_URL = "http://googu.kr/"; //구구 서버 URL
    final private static long _DEPART_TIMER_5MIN = 300;
    final private static int _ALLOWABLE_ERROR_TIME_5SEC = 5;
    final private static long _RAPID_UPDATE_DELAY = 1000;

    public static long getRapidUpdateDelay() {
        return _RAPID_UPDATE_DELAY;
    }

    public static int getAllowableErrorTime5sec() {
        return _ALLOWABLE_ERROR_TIME_5SEC;
    }

    public static String getServerUrl() {
        return _SERVER_URL;
    }

    public static long getDepartTimer5min() {
        return _DEPART_TIMER_5MIN;
    }
}
