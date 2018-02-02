package com.hzjytech.coffeeme.jpushmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.OtherLoginDialogActivity;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.AppItem;
import com.hzjytech.coffeeme.entities.PointRecord;
import com.hzjytech.coffeeme.home.ModulationActivity;
import com.hzjytech.coffeeme.me.MyCouponActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hades on 2016/8/15.
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtil.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ",extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtil.d(TAG, "[MyReceiver] Registration Id: " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtil.d(TAG, "[MyReceiver] message received: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.d(TAG, "[MyReceiver] notification received: " + bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID));
            if (!TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                try {
                    JSONObject extra = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    if ("OTHERLOGIN".equals(extra.getString(Configurations.JPUSHTYPE))) {

                        if (!AppUtil.isAppBackground(context)) {
                            SharedPrefUtil.loginout();
                            Intent otherLoginIntent = new Intent(context, OtherLoginDialogActivity.class);
                            otherLoginIntent.putExtra("notifi_id", bundle);
                            otherLoginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(otherLoginIntent);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtil.d(TAG, "[MyReceiver] richpush callback: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            JPushInterface.clearNotificationById(context, (Integer) bundle.get(JPushInterface.EXTRA_NOTIFICATION_ID));
            if (!TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                try {
                    JSONObject extra = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    if(extra.has(Configurations.JPUSHTYPE)){
                        if ("OTHERLOGIN".equals(extra.getString(Configurations.JPUSHTYPE))) {

                            SharedPrefUtil.loginout();
                            Intent loginIntent = new Intent(context, LoginActivity.class);
                            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(loginIntent);
                        } else if ("UPDATE".equals(extra.getString(Configurations.JPUSHTYPE))) {
                            Intent updateIntent = new Intent(context, MainActivity.class);
                            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(updateIntent);
                        }  else if ("NEWGOOD".equals(extra.getString(Configurations.JPUSHTYPE))) {

                                Intent newGood = new Intent(context, ModulationActivity.class);
                                AppItem appItem = new AppItem();
                                if (extra.has("id")) {
                                    appItem.setId(extra.getInt("id"));
                                }
                                if (extra.has("image_key")) {
                                    appItem.setImage_key(extra.getString("image_key"));
                                }
                                if (extra.has("name")) {
                                    appItem.setName(extra.getString("name"));
                                }
                                if (extra.has("price")) {
                                    appItem.setPrice((float) extra.getDouble("price"));
                                }

                                if (extra.has("volume")) {
                                    appItem.setVolume(extra.getInt("volume"));
                                }

                                if (extra.has("current_price")) {
                                    appItem.setCurrent_price((float) extra.getDouble("current_price"));
                                }
                                if (extra.has("description")) {
                                    appItem.setDescription(extra.getString("description"));
                                }
                                newGood.putExtra(Configurations.APP_ITEM, appItem);
                                newGood.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(newGood);
                        } else if ("COUPON".equals(extra.getString(Configurations.JPUSHTYPE))) {
                            Intent couponIntent = new Intent(context, MyCouponActivity.class);
                            couponIntent.putExtra(MyCouponActivity.SHOW_BOTTOM_BTN, true);
                            couponIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(couponIntent);
                        }
                    }else{
                        Intent mainIntent = new Intent(context, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(mainIntent);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            Intent i=new Intent(context,MainActivity.class);
//            i.putExtras(bundle);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LogUtil.d(TAG, "[MyReceiver] richpush callback: " + bundle.getString(JPushInterface.EXTRA_EXTRA));

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtil.w(TAG, "[MyReceiver]  " + intent.getAction() + " connected state change to " + connected);
        } else {
            LogUtil.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey" + key + ",value" + bundle.get(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ",value" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    LogUtil.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ",value: [" + myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtil.e(TAG, "Get message extra JSON error");
                }
            } else {
                sb.append("\n key: " + key + ",value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

//    private void processCustomMessage(Context context, Bundle bundle) {
//        if (JPushMainActivity.isForeground) {
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//            Intent msgIntent = new Intent(JPushMainActivity.MESSAGE_RECEIVE_ACTION);
//            msgIntent.putExtra(JPushMainActivity.KEY_MESSAGE, message);
//            if (!ExampleUtil.isEmpty(extras)) {
//                try {
//                    JSONObject extraJson = new JSONObject(extras);
//                    if (null != extraJson && extraJson.length() > 0) {
//                        msgIntent.putExtra(JPushMainActivity.KEY_EXTRAS, extras);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            context.sendBroadcast(msgIntent);
//        }
//
//    }
}
