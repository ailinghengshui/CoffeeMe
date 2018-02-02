package com.hzjytech.coffeeme.utils;

import android.content.Context;
import android.util.Log;

import com.hzjytech.coffeeme.EnterActivity;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hades on 2016/10/11.
 */
public final class SignUtils {


    private static String keyString="key=okbr6kq3bici6qis009dagtayryjvrh3"+"&";

    public static  String createSignString(String device_id, long timeStamp , Map<String, String> map){
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append(keyString);
        stringBuilder.append("device_id="+ device_id+"&");
        stringBuilder.append("timestamp="+String.valueOf(timeStamp));

        if(map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                stringBuilder.append("&");
                stringBuilder.append(entry.getKey() + "=" + entry.getValue());
            }
        }

        return MD5Util.getMD5(stringBuilder.toString());
    }

    public static  String createSignString(String device_id, String timeStamp , Map<String, String> map){
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append(keyString);
        stringBuilder.append("device_id="+ device_id+"&");
        stringBuilder.append("timestamp="+timeStamp);

        if(map!=null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                stringBuilder.append("&");
                stringBuilder.append(entry.getKey() + "=" + entry.getValue());
            }
        }

        return MD5Util.getMD5(stringBuilder.toString());
    }

    public static String createSignString(String device_id,String timestamp) {

        return createSignString(device_id,timestamp,null);
    }

    public static String createSignString(String device_id,long timestamp) {

        return createSignString(device_id,timestamp,null);
    }
}
