package com.hzjytech.coffeeme.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hzjytech.coffeeme.R;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hades on 2016/1/26.
 */
public class NetUtil {

    //check network status
    public static boolean isNetworkAvailable(Context context, boolean showToast) {

        boolean res = false;
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                res = networkInfo.isConnected();
            }
        }

        if (!res && showToast) {
            ToastUtil.showShort(context, context.getString(R.string.network_error_info));
        }

        return res;
    }

    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(context, true);
    }

    public static boolean isWifi(Context context){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null)
            return false;
        return connectivityManager.getActiveNetworkInfo().getType()==ConnectivityManager.TYPE_WIFI;
    }

    public static String getIPAddress(boolean useIPv4){
        try {
            List<NetworkInterface> interfaces= Collections.list(NetworkInterface.getNetworkInterfaces());
            for(NetworkInterface intf:interfaces){
                List<InetAddress> addresses=Collections.list(intf.getInetAddresses());
                for(InetAddress address:addresses){
                    if(!address.isLoopbackAddress()){
                        String sAddr=address.getHostAddress();

                        boolean isIPv4=sAddr.indexOf(':')<0;

                        if(useIPv4){
                            if(isIPv4){
                                return sAddr;
                            }
                        }else{
                            if(!isIPv4){
                                int delim=sAddr.indexOf('%');
                                return delim<0?sAddr.toUpperCase():sAddr.substring(0,delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void openNetSetting(Activity activity){
        Intent intent=new Intent("/");
        ComponentName componentName=new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }
}


















