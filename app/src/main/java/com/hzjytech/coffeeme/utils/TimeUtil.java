package com.hzjytech.coffeeme.utils;

import android.util.Log;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hades on 2016/10/12.
 */
public class TimeUtil {


    public static synchronized long getCurrentTime() {
        long timeStamp = System.currentTimeMillis()+new Random().nextInt(30);
//        LogUtil.d("TimeUtil", String.valueOf(timeStamp));
        return timeStamp;
    }

    public static synchronized String getCurrentTimeString() {
        String timeStamp=String.valueOf(System.currentTimeMillis())+"."+new Random().nextInt(300);
        Log.d("TimeUtils",timeStamp);
        return timeStamp;


    }


}
