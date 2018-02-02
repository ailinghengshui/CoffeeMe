package com.hzjytech.coffeeme.authorization.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.UserAgreementActivity;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
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
@ContentView(R.layout.fragment_register_next)
public class RegisterFragmentNext extends BaseFragment {

    private static final String TAG = RegisterFragmentNext.class.getSimpleName();
    private EditText etRegPsd;
    private EditText etRegReferralcode;
    private TextView agreetv;

    private String token;
    private ImageView ivOldpsdfrgClear1;
    private ImageView ivOldpsdfrgClear2;
    private Button btnRegReg;

    @Event(R.id.regnext_agree)
    private void onAgreeClick(View v) {

        if (AppUtil.isFastClick())
            return;

        Intent intent = new Intent(getActivity(), UserAgreementActivity.class);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etRegPsd = (EditText) view.findViewById(R.id.etRegPsd);
        etRegReferralcode = (EditText) view.findViewById(R.id.etRegReferralcode);
        ivOldpsdfrgClear1 = (ImageView) view.findViewById(R.id.ivOldpsdfrgClear1);
        ivOldpsdfrgClear2 = (ImageView) view.findViewById(R.id.ivOldpsdfrgClear2);
        btnRegReg= (Button) view.findViewById(R.id.btnRegReg);
        agreetv = (TextView) view.findViewById(R.id.regnext_agree);

        btnRegReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegRegClick();
            }
        });

        etRegPsd.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (temp.length() > 0) {
                    ivOldpsdfrgClear1.setVisibility(View.VISIBLE);
                    ivOldpsdfrgClear1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etRegPsd.setText("");
                            ivOldpsdfrgClear1.setVisibility(View.GONE);
                        }
                    });
                }

                if (temp.length() == 0) {
                    ivOldpsdfrgClear1.setVisibility(View.GONE);
                }
            }
        });


        if (etRegReferralcode != null) {
            etRegReferralcode.addTextChangedListener(new TextWatcher() {

                private CharSequence temp;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    temp = s;

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (temp.length() > 0) {
                        ivOldpsdfrgClear2.setVisibility(View.VISIBLE);
                        ivOldpsdfrgClear2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                etRegReferralcode.setText("");
                                ivOldpsdfrgClear2.setVisibility(View.GONE);
                            }
                        });
                    }

                    if (temp.length() == 0) {
                        ivOldpsdfrgClear2.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.REGISTERFRAGMENTNEXT);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.REGISTERFRAGMENTNEXT);
    }

    private void onRegRegClick() {

        if (AppUtil.isFastClick())
            return;

        if (NetUtil.isNetworkAvailable(getActivity())) {

            if (TextUtil.checkParams(token, etRegPsd.getText().toString().trim())) {

                if (etRegPsd.getText().toString().trim().length() > 5) {

                    checkToken();
                } else {
                    ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_hint_newpassword_lw));
                }
            } else {
                ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_hint_newpassword_null));
            }

        } else {
            ToastUtil.showShort(getActivity(), "没有网络");
        }

    }

    /**
     * return true if Token is valid
     * else return false
     *
     * @return
     */
    private void checkToken() {
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, token);

        long timeStamp = TimeUtil.getCurrentTime();
        String device_id = JPushInterface.getRegistrationID(getContext());
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, token);
        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.d(TAG, result);
                boolean isAuthTokenValid = parseResult(result);
                if (isAuthTokenValid) {
                    goReg();
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

    private void goReg() {
        RequestParams entity = new RequestParams(Configurations.URL_SET_PASSWORD);

        String device_id = JPushInterface.getRegistrationID(getContext());

        entity.addParameter(Configurations.TOKEN, token);
        entity.addParameter(Configurations.PASSWORD, etRegPsd.getText().toString().trim());
       // entity.addParameter(Configurations.REFERRAL_CODE, etRegReferralcode.getText().toString().trim());
        entity.addParameter(Configurations.REG_ID, device_id);

        long timeStamp = TimeUtil.getCurrentTime();
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.TOKEN, token);
        map.put(Configurations.PASSWORD, etRegPsd.getText().toString().trim());
       // map.put(Configurations.REFERRAL_CODE, etRegReferralcode.getText().toString().trim());
        map.put(Configurations.REG_ID, device_id);

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result",result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt(Configurations.STATUSCODE) == 200) {

                        MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_REGISTER);
                        User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);
                        //新用户赠送优惠券
                        User.BenefitCouponBean benefitCouponBean = user.getBenefit_coupon();
                        if(benefitCouponBean!=null){
                            SharedPrefUtil.saveIsFirstRegister(true);
                        }
                        SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_PHONE);
                        UserUtils.saveUserInfo(user);
                        UserUtils.saveLocalMobileAndPwd(user.getPhone(), "");
                        getActivity().finish();
                        LoginActivity.Instance().finish();

                    } else {
                        ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
                    }

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


    private boolean parseResult(String result) {
        boolean isValid = false;
        try {
            int statusCode = new JSONObject(result).getInt("statusCode");

            if (statusCode == 200) {
                isValid = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return isValid;
    }


    public static RegisterFragmentNext newInstance(String auth_token) {
        RegisterFragmentNext registerFragmentNext = new RegisterFragmentNext();
        Bundle args = new Bundle();
        args.putString(Configurations.TOKEN, auth_token);
        registerFragmentNext.setArguments(args);
        return registerFragmentNext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(Configurations.TOKEN);
        }
    }
}