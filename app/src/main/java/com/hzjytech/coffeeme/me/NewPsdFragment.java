package com.hzjytech.coffeeme.me;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_new_psd)
public class NewPsdFragment extends BaseFragment {


    private static final String OLDPSD = "oldPsd";
    @ViewInject(R.id.etNewPsdPsd)
    private EditText etNewPsdPsd;

    @ViewInject(R.id.ivNewpsdfrgClear)
    private ImageView ivNewpsdfrgClear;

    @ViewInject(R.id.etNewPsdRePsd)
    private EditText etNewPsdRePsd;

    @ViewInject(R.id.ivNewpsdfrgReclear)
    private ImageView ivNewpsdfrgReclear;

    @ViewInject(R.id.tvNewpsgErr)
    private TextView tvNewpsgErr;

    private String oldPsd;

    @Event(R.id.btnNewPsdOK)
    private void onNewPsdOKClick(View v){
        if(TextUtil.checkParams(etNewPsdPsd.getText().toString(),etNewPsdRePsd.getText().toString())){

            if(etNewPsdPsd.getText().toString().trim().length()>5&&etNewPsdRePsd.getText().toString().trim().length()>5){
                if(etNewPsdPsd.getText().toString().equals(etNewPsdRePsd.getText().toString())){
                    doChangePsd(etNewPsdPsd.getText().toString());
                    getActivity().finish();
                }else{
                    tvNewpsgErr.setText("两次密码不一致");
                }
            }else{
                tvNewpsgErr.setText("请输入至少六位密码");
            }


        }else{
            tvNewpsgErr.setText("密码不能为空");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.NEWPSDFRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.NEWPSDFRAGMENT);
    }

    private void doChangePsd(String newPsd) {

        RequestParams entity=new RequestParams(Configurations.URL_CHANGE_PASSWORD);
        entity.addParameter(Configurations.PHONE, UserUtils.getUserInfo().getPhone());
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.OLD_PASSWORD, oldPsd);
        entity.addParameter(Configurations.PASSWORD, newPsd);

        String device_id= JPushInterface.getRegistrationID(getContext());
        long timeStamp= TimeUtil.getCurrentTime();
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<>();
        map.put(Configurations.PHONE, UserUtils.getUserInfo().getPhone());
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.OLD_PASSWORD, oldPsd);
        map.put(Configurations.PASSWORD, newPsd);
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {
                    ToastUtil.showShort(getActivity(),result.getString("statusMsg"));
                    User user = JSON.parseObject(result.getJSONObject("results").getString("user"), User.class);

                    SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_PHONE);
                    UserUtils.saveUserInfo(user);

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


    public static NewPsdFragment newInstance(String oldPsd) {
        Bundle args=new Bundle();
        args.putString(OLDPSD,oldPsd);
        NewPsdFragment newPsdFragment=new NewPsdFragment();
        newPsdFragment.setArguments(args);
        return newPsdFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            this.oldPsd=getArguments().getString(OLDPSD);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNewPsdPsd.addTextChangedListener(mTextWathcer);
        etNewPsdRePsd.addTextChangedListener(mReTextWathcer);



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
                ivNewpsdfrgClear.setVisibility(View.VISIBLE);
                ivNewpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etNewPsdPsd.setText("");
                        ivNewpsdfrgClear.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivNewpsdfrgClear.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher mReTextWathcer=new TextWatcher(){

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
                ivNewpsdfrgReclear.setVisibility(View.VISIBLE);
                ivNewpsdfrgReclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etNewPsdRePsd.setText("");
                        ivNewpsdfrgReclear.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivNewpsdfrgReclear.setVisibility(View.GONE);
            }
        }
    };


}