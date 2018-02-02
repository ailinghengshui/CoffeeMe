package com.hzjytech.coffeeme.jpushmanager;

import android.app.Notification;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.service.carrier.CarrierService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.me.SettingActivity;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;

import java.util.LinkedHashSet;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class PushSetActivity extends InstrumentedActivity implements View.OnClickListener {

    private static final String TAG="JPush";

    private Button mSetTag;
    private Button mSetAlias;
    private Button mStyleBasic;
    private Button mStyleCustom;
    private Button mSetPushTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_set);

        init();
        initListener();
    }

    private void init() {
        mSetTag=(Button)findViewById(R.id.bt_tag);
        mSetAlias=(Button)findViewById(R.id.bt_alias);
        mStyleBasic=(Button)findViewById(R.id.setStyle1);
        mStyleCustom=(Button)findViewById(R.id.setStyle2);
        mSetPushTime=(Button)findViewById(R.id.bu_setTime);
    }

    private void initListener() {
        mSetTag.setOnClickListener(this);
        mSetAlias.setOnClickListener(this);
        mStyleBasic.setOnClickListener(this);
        mStyleCustom.setOnClickListener(this);
        mSetPushTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_tag:
                setTag();
                break;
            case R.id.bt_alias:
                setAlias();
                break;
            case R.id.setStyle1:
                setStyleBasic();
                break;
            case R.id.setStyle2:
                setStyleCustom();
                break;
            case R.id.bu_setTime:
                Intent intent=new Intent(PushSetActivity.this, JPushSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setTag(){
        EditText tagEdit= (EditText) findViewById(R.id.et_tag);
        String tag=tagEdit.getText().toString().trim();
        if(TextUtils.isEmpty(tag)){
            Toast.makeText(PushSetActivity.this,"TAG EMPTY",Toast.LENGTH_SHORT).show();
            return;
        }

        String[] sArray=tag.split(",");
        Set<String> tagSet=new LinkedHashSet<>();
        for(String sTagItem:sArray){
            if(!ExampleUtil.isValidTagAndAlias(sTagItem)){
                Toast.makeText(PushSetActivity.this,"TAG EMPTY",Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItem);
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS,tagSet));
    }

    private void setAlias(){
        EditText aliasEdit= (EditText) findViewById(R.id.et_alias);
        String alias=aliasEdit.getText().toString().trim();
        if(TextUtils.isEmpty(alias)){
            Toast.makeText(PushSetActivity.this,"alias empty error",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!ExampleUtil.isValidTagAndAlias(alias)){
            Toast.makeText(PushSetActivity.this,"tag gs error",Toast.LENGTH_SHORT).show();
            return ;
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS,alias));
    }

    private void setStyleBasic(){
        BasicPushNotificationBuilder builder=new BasicPushNotificationBuilder(PushSetActivity.this);
        builder.statusBarDrawable=R.mipmap.icon_launcher;
        builder.notificationFlags= Notification.FLAG_AUTO_CANCEL;
        builder.notificationDefaults=Notification.DEFAULT_SOUND;
        JPushInterface.setPushNotificationBuilder(1,builder);
        Toast.makeText(PushSetActivity.this,"Basic Builder -1",Toast.LENGTH_SHORT).show();
    }

    private void setStyleCustom(){
        CustomPushNotificationBuilder builder=new CustomPushNotificationBuilder(PushSetActivity.this,R.layout.cutomer_notification_layout,R.id.icon,R.id.title,R.id.text);
        builder.layoutIconDrawable=R.mipmap.icon_launcher;
        builder.developerArg0="developerArg2";
        JPushInterface.setPushNotificationBuilder(2,builder);
        Toast.makeText(PushSetActivity.this,"Custom Builder -2",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private final TagAliasCallback mAliasCallback=new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code){
                case 0:
                    logs="Set tag and alias success";
                    Log.i(TAG,logs);
                    break;
                case 6002:
                    logs="Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG,logs);
                    if(ExampleUtil.isConnected(getApplicationContext())){
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS,alias),1000*60);
                    }else{
                        LogUtil.i(TAG,"No network");
                    }
                    break;
                default:
                    logs="Failed with errorCode= "+code;
                    LogUtil.e(TAG,logs);
            }
            ExampleUtil.showToast(logs,getApplicationContext());

        }
    };

    private final TagAliasCallback mTagsCallback=new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code){
                case 0:
                    logs="Set tag and alias success";
                    Log.i(TAG,logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    LogUtil.e(TAG, logs);
            }
            ExampleUtil.showToast(logs, getApplicationContext());
        }
    };

    private static final int MSG_SET_ALIAS=1001;
    private static final int MSG_SET_TAGS=1002;

    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SET_ALIAS:
                    Log.d(TAG,"Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(),(String)msg.obj,null,mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    Log.d(TAG,"Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(),null,(Set<String>) msg.obj,mTagsCallback);
                    break;
                default:
                    Log.i(TAG,"Unhandler msg -"+msg.what);
                    break;
            }
        }
    };

}
