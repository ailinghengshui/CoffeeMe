package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_feedback)
public class FeedbackActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar tbFeedbackActTitle;

    @ViewInject(R.id.etFeedbackOpnion)
    private EditText etFeedbackOpnion;

    @ViewInject(R.id.etFeedbackEmail)
    private EditText etFeedbackEmail;

    @ViewInject(R.id.etFeedbackPhone)
    private EditText etFeedbackPhone;

    @Event(R.id.btnFeedbackCommit)
    private void onFeedbackCommitClick(View view){
        if(NetUtil.isNetworkAvailable(FeedbackActivity.this)){
            if(!TextUtils.isEmpty(etFeedbackEmail.getText().toString().trim())&&!TextUtils.isEmpty(etFeedbackOpnion.getText().toString().trim())){
                if(isEmailValid(etFeedbackEmail.getText().toString().trim())){
                    commitOpinion();
                    finish();
                }else{
                    ToastUtil.showShort(FeedbackActivity.this,"邮箱格式不正确");
                }

            }else{
                ToastUtil.showShort(FeedbackActivity.this,"邮箱/意见为空");
            }
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void commitOpinion() {
        RequestParams entity=new RequestParams(Configurations.URL_FEEDBACKS);
        entity.addParameter(Configurations.EMAIL,etFeedbackEmail.getText().toString().trim());
        entity.addParameter(Configurations.PHONE,etFeedbackPhone.getText().toString().trim());
        entity.addParameter(Configurations.DESCRIPTION,etFeedbackOpnion.getText().toString().trim());

        String device_id= JPushInterface.getRegistrationID(FeedbackActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.EMAIL,etFeedbackEmail.getText().toString().trim());
        map.put(Configurations.PHONE,etFeedbackPhone.getText().toString().trim());
        map.put(Configurations.DESCRIPTION,etFeedbackOpnion.getText().toString().trim());



        if(SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)){
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        }

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ToastUtil.showShort(FeedbackActivity.this,result.getString(Configurations.STATUSMSG));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNetError();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tbFeedbackActTitle.setTitle("建议反馈");
        tbFeedbackActTitle.setTitleColor(Color.WHITE);
        tbFeedbackActTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbFeedbackActTitle.setLeftImageResource(R.drawable.icon_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.FEEDBACKACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.FEEDBACKACTIVITY);
        MobclickAgent.onPause(this);
    }
}