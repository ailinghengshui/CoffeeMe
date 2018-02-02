package com.hzjytech.coffeeme.authorization.login;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.MyEditText;
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
 * Created by Hades on 2016/1/28.
 */
@ContentView(R.layout.fragment_validate_next)
public class ValidateFragmentNext extends BaseFragment {

    private static final String TAG = ValidateFragmentNext.class.getSimpleName();
    private EditText etFgpsdNewpsd;
    @ViewInject(value = R.id.etFgpsdRenewpsd)
    private EditText etFgpsdRenewpsd;
    private String token;
    private ImageView ivOldpsdfrgClear1;
    private ImageView ivOldpsdfrgClear2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFgpsdNewpsd= (EditText) view.findViewById(R.id.etFgpsdNewpsd);
        etFgpsdRenewpsd= (EditText) view.findViewById(R.id.etFgpsdRenewpsd);
        ivOldpsdfrgClear1= (ImageView) view.findViewById(R.id.ivOldpsdfrgClear1);
        ivOldpsdfrgClear2= (ImageView) view.findViewById(R.id.ivOldpsdfrgClear2);

        etFgpsdNewpsd.addTextChangedListener(mTextWathcer1);
        etFgpsdRenewpsd.addTextChangedListener(mTextWathcer2);

    }

    private TextWatcher mTextWathcer1=new TextWatcher(){

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
                ivOldpsdfrgClear1.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etFgpsdNewpsd.setText("");
                        ivOldpsdfrgClear1.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear1.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher mTextWathcer2=new TextWatcher(){

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
                ivOldpsdfrgClear2.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etFgpsdRenewpsd.setText("");
                        ivOldpsdfrgClear2.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear2.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.VALIDATEFRAGMENTNEXT);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.VALIDATEFRAGMENTNEXT);
    }

    @Event(value = R.id.btnFgpsdComplete)
    private void onFgpsdClick(View v) {

        if(AppUtil.isFastClick())
            return;

        if (NetUtil.isNetworkAvailable(getActivity())) {
            if (TextUtil.checkParams(etFgpsdNewpsd.getText().toString().trim(), etFgpsdRenewpsd.getText().toString().trim())) {

                LogUtil.d(TAG, etFgpsdNewpsd.getText().toString().trim().length()+"");
                if (etFgpsdNewpsd.getText().toString().trim().length() > 5) {
                    if (etFgpsdNewpsd.getText().toString().trim().equals(etFgpsdRenewpsd.getText().toString().trim())) {

                        RequestParams entity = new RequestParams(Configurations.URL_RESET_PASSWORD);
                        String device_id = JPushInterface.getRegistrationID(getContext());
                        entity.addParameter(Configurations.TOKEN, token);
                        entity.addParameter(Configurations.PASSWORD, etFgpsdNewpsd.getText().toString().trim());
                        entity.addParameter(Configurations.REG_ID, device_id);

                        long timeStamp= TimeUtil.getCurrentTime();
                        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                        entity.addParameter(Configurations.DEVICE_ID, device_id);

                        Map<String, String> map = new TreeMap<>();
                        map.put(Configurations.TOKEN, token);
                        map.put(Configurations.PASSWORD, etFgpsdNewpsd.getText().toString().trim());
                        map.put(Configurations.REG_ID, device_id);

                        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                parseResetResult(result);

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

                    } else {
                        ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_err_diffpassword));
                    }

                } else{
                    ToastUtil.showShort(getActivity(),getResources().getString(R.string.str_hint_newpassword_lw));
                }

            }else {
                ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_err_password));
            }

        } else {
            ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_hint_nonet));
        }
    }

    private void parseResetResult(String result) {

        try {
            MobclickAgent.onEvent(getContext(),UmengConfig.EVENT_FGPSD);
            JSONObject object = new JSONObject(result);
            if(object.getInt(Configurations.STATUSCODE)==200){
                ToastUtil.showShort(getActivity(),"重置密码成功！");
            }else {
                ToastUtil.showShort(getActivity(),object.getString(Configurations.STATUSMSG));
            }


            User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);

            SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_PHONE);
            UserUtils.saveUserInfo(user);
            UserUtils.saveLocalMobileAndPwd(user.getPhone(), "");
            LoginActivity.Instance().finish();
            getActivity().finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static ValidateFragmentNext newInstance(String token) {
        ValidateFragmentNext validateFragmentNext = new ValidateFragmentNext();
        Bundle args = new Bundle();
        args.putString(Configurations.TOKEN, token);
        validateFragmentNext.setArguments(args);
        return validateFragmentNext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(Configurations.TOKEN);
        }
    }
}