package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_update_nickname)
public class UpdateNicknameActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar tbUpdnicknameTitle;

    @ViewInject(R.id.etUpdnickname)
    private EditText etUpdnickname;

    @ViewInject(R.id.ivOldpsdfrgClear)
    private ImageView ivOldpsdfrgClear;


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.UPDATENICKNAMEACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.UPDATENICKNAMEACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tbUpdnicknameTitle.setTitle("昵称");
        tbUpdnicknameTitle.setLeftTextColor(Color.WHITE);
        tbUpdnicknameTitle.setTitleColor(Color.WHITE);
        tbUpdnicknameTitle.setLeftImageResource(R.drawable.icon_left);
        tbUpdnicknameTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etUpdnickname.setText(UserUtils.getUserInfo().getNickname());
        etUpdnickname.setSelection(etUpdnickname.getText().length());
        etUpdnickname.addTextChangedListener(mTextWathcer);
        if(etUpdnickname.getText()!=null&&!etUpdnickname.getText().toString().equals("")){
            ivOldpsdfrgClear.setVisibility(View.VISIBLE);
            ivOldpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etUpdnickname.setText("");
                    ivOldpsdfrgClear.setVisibility(View.INVISIBLE);
                }
            });
        }

        tbUpdnicknameTitle.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return 0;
            }

            @Override
            public void performAction(View view) {

                if (NetUtil.isNetworkAvailable(UpdateNicknameActivity.this)) {
                    if (!TextUtils.isEmpty(etUpdnickname.getText().toString().trim())) {
                        changeNickName(false);
                    } else {
                        changeNickName(true);
                    }
                } else {
                    ToastUtil.showShort(UpdateNicknameActivity.this, "网络没有了");
                }

            }
        });
    }

    private TextWatcher mTextWathcer=new TextWatcher(){

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(temp.length()>0){
                ivOldpsdfrgClear.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etUpdnickname.setText("");
                        ivOldpsdfrgClear.setVisibility(View.INVISIBLE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear.setVisibility(View.INVISIBLE);
            }
        }
    };


    private void changeNickName(final boolean isPhone) {
        RequestParams entity = new RequestParams(Configurations.URL_UPDATE_NICKNAME);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(UpdateNicknameActivity.this);
        long timeStamp= TimeUtil.getCurrentTime();
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());


        if (isPhone) {
            entity.addParameter(Configurations.NICKNAME, "");
            map.put(Configurations.NICKNAME, "");
        } else {
            entity.addParameter(Configurations.NICKNAME, etUpdnickname.getText().toString().trim());
            map.put(Configurations.NICKNAME, etUpdnickname.getText().toString().trim());
        }

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {

                    checkResOld(new JSONObject(result));

                    if (isPhone) {
                        UserUtils.getUserInfo().setNickname("");
                    } else {
                        UserUtils.getUserInfo().setNickname(etUpdnickname.getText().toString().trim());
                    }
                    String message = new JSONObject(result).getString(Configurations.STATUSMSG);
                    ToastUtil.showShort(UpdateNicknameActivity.this, message);
                    UpdateNicknameActivity.this.finish();
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
}