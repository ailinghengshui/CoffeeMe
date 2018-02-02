package com.hzjytech.coffeeme.authorization.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClickWithOneParam;
import com.hzjytech.coffeeme.Dialogs.ValidateCodeDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.UserAgreementActivity;
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

@ContentView(R.layout.fragment_register)
public class RegisterFragment extends BaseFragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText etRegPhone;

    private EditText etRegSmscode;

    private Button btnRegNext;

    private Button btnRegSmscode;

    @ViewInject(R.id.reg_agree)
    private TextView agreetv;

    private RegisterFragmentListener mListener;

    private ValidateCodeDialog validateCodeDialog;
    private ImageView ivOldpsdfrgClear;
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.REGISTERFRAGMENT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etRegPhone = (EditText) view.findViewById(R.id.etRegPhone);
        etRegSmscode = (EditText) view.findViewById(R.id.etRegSmscode);
        ivOldpsdfrgClear = (ImageView) view.findViewById(R.id.ivOldpsdfrgClear1);

        btnRegNext=(Button)view.findViewById(R.id.btnRegNext);
        btnRegSmscode=(Button)view.findViewById(R.id.btnRegSmscode);

        etRegPhone.addTextChangedListener(new TextWatcher() {

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
                    ivOldpsdfrgClear.setVisibility(View.VISIBLE);
                    ivOldpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etRegPhone.setText("");
                            ivOldpsdfrgClear.setVisibility(View.GONE);
                        }
                    });
                }

                if (temp.length() == 0) {
                    ivOldpsdfrgClear.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.REGISTERFRAGMENT);
    }

    @Event(R.id.btnRegSmscode)
    private void onRegSmscodeClick(View v) {

        if (AppUtil.isFastClick())
            return;

        if (TextUtil.checkParams(etRegPhone.getText().toString().trim()) && etRegPhone.getText().toString().trim().length() == 11) {

            if (NetUtil.isNetworkAvailable(getActivity())) {

                showLoading();
                //默认显示灰色验证码区
                validateCodeDialog = ValidateCodeDialog.newInstance(R.drawable.bg_validate);
                validateCodeDialog.show(getFragmentManager(), "validateCodeDialog");
                RequestParams entity = new RequestParams(Configurations.URL_CAPTCHAS);
                entity.addParameter("phone", etRegPhone.getText().toString().trim());

                String device_id = JPushInterface.getRegistrationID(getContext());
                String timeStamp = TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);
                Map<String, String> map = new TreeMap<>();
                map.put("phone", etRegPhone.getText().toString().trim());

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));
                x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                hideLoading();
                                //validateCodeDialog = ValidateCodeDialog.newInstance(result.getJSONObject("results").getString("captcha"));
                                validateCodeDialog.setImageurl(result.getJSONObject("results").getString("captcha"));
                                validateCodeDialog.setOnImageClick(new ValidateCodeDialog.IImageClickListener() {
                                    @Override
                                    public void onImageClickListener() {
                                        RequestParams entity = new RequestParams(Configurations.URL_CAPTCHAS);
                                        entity.addParameter("phone", etRegPhone.getText().toString().trim());

                                        String device_id = JPushInterface.getRegistrationID(getContext());
                                        String timeStamp = TimeUtil.getCurrentTimeString();
                                        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                                        entity.addParameter(Configurations.DEVICE_ID, device_id);

                                        Map<String, String> map = new TreeMap<>();
                                        map.put("phone", etRegPhone.getText().toString().trim());
                                        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));
                                        x.http().get(entity, new CommonCallback<JSONObject>() {
                                            @Override
                                            public void onSuccess(JSONObject result) {
                                                try {
                                                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                                                        validateCodeDialog.setImageurl(result.getJSONObject("results").getString("captcha"));
                                                    } else {
                                                        ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable ex, boolean isOnCallback) {
                                               new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastUtil.showShort(getActivity(),"验证码刷不出来？别灰心，先检查一下您的网络再试试看。");
                                                        validateCodeDialog.dismiss();
                                                    }
                                                },5000);


                                            }

                                            @Override
                                            public void onCancelled(CancelledException cex) {

                                            }

                                            @Override
                                            public void onFinished() {

                                            }
                                        });

                                    }
                                });
                                validateCodeDialog.setOnTwoClickListener(new ITwoButtonClickWithOneParam() {
                                    @Override
                                    public void onLeftButtonClick() {
                                        //TODO onCancel
//                        btnRegNext.setText("取消");
                                    }

                                    @Override
                                    public void onRightButtonClick(String string) {
                                        //TODO
                                        sendSmsCode(string);

//                                        if(true) {
//                            btnRegNext.setText("成功");
//                            sendSmsCode();
//                                        }else{
//                            btnRegNext.setText("失败");
//                                        }
                                    }
                                });

                               // validateCodeDialog.show(getFragmentManager(), "validateCodeDialog");
                            } else {
                                ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));
                                //无法获得图片验证码，关闭dialog并提示状态信息

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

            } else {

                ToastUtil.showShort(getActivity(), getResources().getString(R.string.network_error_info));
            }

        } else {
            ToastUtil.showShort(getActivity(), "手机号码不符合规范");
        }
    }

    private void sendSmsCode(final String captcha) {

        RequestParams params = new RequestParams(Configurations.URL_SEND_SMS_CODE);
        params.addParameter(Configurations.PHONE, etRegPhone.getText().toString().trim());
        params.addParameter(Configurations.TYPE, "register");
        params.addParameter(Configurations.CAPTCHA, captcha);

        String device_id = JPushInterface.getRegistrationID(getContext());
        long timeStamp = TimeUtil.getCurrentTime();
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.PHONE, etRegPhone.getText().toString().trim());
        map.put(Configurations.TYPE, "register");
        map.put(Configurations.CAPTCHA, captcha);

        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt(Configurations.STATUSCODE) == 200) {
                        new TimeCount(60000, 1000).start();
                    }
                    ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
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

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnRegSmscode.setClickable(false);
            btnRegSmscode.setText(((int) millisUntilFinished / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            btnRegSmscode.setText("再次发送");
            btnRegSmscode.setClickable(true);
        }
    }


    @Event(value = R.id.btnRegNext)
    private void onRegFgClick(View view) {

        if (AppUtil.isFastClick())
            return;

        if (!TextUtils.isEmpty(etRegPhone.getText().toString().trim()) && !TextUtils.isEmpty(etRegSmscode.getText().toString().trim())) {
            if (mListener != null) {
                RequestParams entity = new RequestParams(Configurations.URL_CHECK_SMS_CODE);
                entity.addParameter(Configurations.PHONE, etRegPhone.getText().toString().trim());
                entity.addParameter(Configurations.SMS_CODE, etRegSmscode.getText().toString().trim());
                entity.addParameter(Configurations.FROM, "register");

                String device_id = JPushInterface.getRegistrationID(getContext());
                String timeStamp = TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<>();
                map.put(Configurations.PHONE, etRegPhone.getText().toString().trim());
                map.put(Configurations.SMS_CODE, etRegSmscode.getText().toString().trim());
                map.put(Configurations.FROM, "register");

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {

                        LogUtil.i(TAG, result);

                        try {
                            JSONObject results = new JSONObject(result);
                            if (results.getInt("statusCode") == 200) {
                                User user = new Gson().fromJson(results.getJSONObject("results").getString("user"), User.class);

                                mListener.onRegisterFragment(user.getAuth_token());
                            } else {
                                ToastUtil.showShort(getActivity(), new JSONObject(result).getString("statusMsg"));
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
        } else {
            ToastUtil.showShort(getActivity(), "手机号或验证码为空");
        }

    }

    @Event(R.id.reg_agree)
    private void onAgreeClick(View v) {

        if (AppUtil.isFastClick())
            return;

        Intent intent = new Intent(getActivity(), UserAgreementActivity.class);
        startActivity(intent);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RegisterFragmentListener) {
            mListener = (RegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface RegisterFragmentListener {
        void onRegisterFragment(String token);
    }
}