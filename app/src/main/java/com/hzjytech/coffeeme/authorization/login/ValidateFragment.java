package com.hzjytech.coffeeme.authorization.login;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hzjytech.coffeeme.Dialogs.ITwoButtonClickWithOneParam;
import com.hzjytech.coffeeme.Dialogs.ValidateCodeDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.StringUtil;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
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
 * Created by Hades on 2016/2/16.
 */
@ContentView(value = R.layout.fragment_validate)
public class ValidateFragment extends BaseFragment {

    private static final String TAG = ValidateFragment.class.getSimpleName();
    private EditText metForgetpsdPhone;
    private EditText fetForgetpsdSmscode;
    @ViewInject(R.id.btnForgetpsdSmscode)
    private Button btnForgetpsdSmscode;

    private OnValidateFragmentListener listener;
    private ValidateCodeDialog validateCodeDialog;
    private ImageView ivOldpsdfrgClear;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        metForgetpsdPhone= (EditText) view.findViewById(R.id.etForgetpsdPhone);
        fetForgetpsdSmscode= (EditText) view.findViewById(R.id.etForgetpsdSmscode);
        ivOldpsdfrgClear= (ImageView) view.findViewById(R.id.ivOldpsdfrgClear);

        metForgetpsdPhone.addTextChangedListener(mTextWathcer);

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
                        metForgetpsdPhone.setText("");
                        ivOldpsdfrgClear.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.VALIDATEFRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.VALIDATEFRAGMENT);
    }

    @Event(R.id.btnForgetpsdSmscode)
    private void onForgetpsdSmscodeClick(View view) {

        if (AppUtil.isFastClick())
            return;
        if (!(TextUtil.checkParams(metForgetpsdPhone.getText().toString().trim())) ||!(metForgetpsdPhone.getText().toString().trim().length() == 11)
                ||!(StringUtil.isMobile(metForgetpsdPhone.getText().toString().trim()))){
            ToastUtil.showShort(getActivity(),"请输入11位中国大陆号码");
            return;
        }
        showLoading();
        //默认显示灰色验证码区
        validateCodeDialog = ValidateCodeDialog.newInstance(R.drawable.bg_validate);
        validateCodeDialog.show(getFragmentManager(), "validateCodeDialog");
        if (TextUtil.checkParams(metForgetpsdPhone.getText().toString().trim()) && metForgetpsdPhone.getText().toString().trim().length() == 11) {
            if (NetUtil.isNetworkAvailable(getActivity())) {
                RequestParams entity = new RequestParams(Configurations.URL_CAPTCHAS);
                entity.addParameter("phone", metForgetpsdPhone.getText().toString().trim());

                String device_id= JPushInterface.getRegistrationID(getContext());
                long timeStamp= TimeUtil.getCurrentTime();
                entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                entity.addParameter(Configurations.DEVICE_ID,device_id );

                Map<String,String> map=new TreeMap<>();
                map.put("phone", metForgetpsdPhone.getText().toString().trim());

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

                x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                hideLoading();
                                validateCodeDialog.setImageurl(result.getJSONObject("results").getString("captcha"));
                                //validateCodeDialog = ValidateCodeDialog.newInstance(result.getJSONObject("results").getString("captcha"));
                                validateCodeDialog.setOnImageClick(new ValidateCodeDialog.IImageClickListener() {
                                    @Override
                                    public void onImageClickListener() {
                                        RequestParams entity = new RequestParams(Configurations.URL_CAPTCHAS);
                                        entity.addParameter("phone", metForgetpsdPhone.getText().toString().trim());

                                        String device_id= JPushInterface.getRegistrationID(getContext());
                                        long timeStamp= TimeUtil.getCurrentTime();
                                        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                                        entity.addParameter(Configurations.DEVICE_ID,device_id );

                                        Map<String,String> map=new TreeMap<>();
                                        map.put("phone", metForgetpsdPhone.getText().toString().trim());

                                        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

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

                                //validateCodeDialog.show(getFragmentManager(), "validateCodeDialog");
                            } else {
                                ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));
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
                ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_hint_nonet));
            }

        } else {
            ToastUtil.showShort(getActivity(), "手机号码不符合规范");
        }
    }

    private void sendSmsCode(String captcha) {

        RequestParams params = new RequestParams(Configurations.URL_SEND_SMS_CODE);
        params.addParameter(Configurations.PHONE, metForgetpsdPhone.getText().toString().trim());
        params.addParameter(Configurations.TYPE, "reset_password");
        params.addParameter(Configurations.CAPTCHA, captcha);

        String device_id= JPushInterface.getRegistrationID(getContext());
        long timeStamp= TimeUtil.getCurrentTime();
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.PHONE, metForgetpsdPhone.getText().toString().trim());
        map.put(Configurations.TYPE, "reset_password");
        map.put(Configurations.CAPTCHA, captcha);

        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

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
            btnForgetpsdSmscode.setClickable(false);
            btnForgetpsdSmscode.setText(((int) millisUntilFinished / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            btnForgetpsdSmscode.setText("再次发送");
            btnForgetpsdSmscode.setClickable(true);

        }
    }

    @Event(value = R.id.btnForgetpsdNext)
    private void onForgetpsdNext(View v) {

        if (AppUtil.isFastClick())
            return;

        if (TextUtil.checkParams(metForgetpsdPhone.getText().toString().trim(), fetForgetpsdSmscode.getText().toString())) {
            if (metForgetpsdPhone.getText().toString().length() == 11) {

                RequestParams entity = new RequestParams(Configurations.URL_CHECK_SMS_CODE);
                entity.addParameter(Configurations.PHONE, metForgetpsdPhone.getText().toString().trim());
                entity.addParameter(Configurations.SMS_CODE, fetForgetpsdSmscode.getText().toString().trim());
                entity.addParameter(Configurations.FROM, "reset_password");

                String device_id= JPushInterface.getRegistrationID(getContext());
                long timeStamp= TimeUtil.getCurrentTime();
                entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                entity.addParameter(Configurations.DEVICE_ID,device_id );

                Map<String,String> map=new TreeMap<>();
                map.put(Configurations.PHONE, metForgetpsdPhone.getText().toString().trim());
                map.put(Configurations.SMS_CODE, fetForgetpsdSmscode.getText().toString().trim());
                map.put(Configurations.FROM, "reset_password");

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

                x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        parseCheckSmsResult(result);
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
                ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_err_supphone));
            }

        } else {
            ToastUtil.showShort(getActivity(), getResources().getString(R.string.str_err_nophonesmscode));
        }

    }

    private void parseCheckSmsResult(String result) {

        Log.d(TAG, result);
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                listener.onValidateInteract(object.getJSONObject("results").getJSONObject("user").getString("auth_token"));

            } else {
                ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnValidateFragmentListener) {
            listener = (OnValidateFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implements OnValidateFragmentListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnValidateFragmentListener {
        void onValidateInteract(String token);
    }
}