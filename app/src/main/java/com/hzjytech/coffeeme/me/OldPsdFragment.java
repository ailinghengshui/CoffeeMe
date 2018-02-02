package com.hzjytech.coffeeme.me;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
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

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_old_psd)
public class OldPsdFragment extends BaseFragment {

    public interface OldPsdFragmentable{
        void onPsdFragment(String string);
    }

    private OldPsdFragmentable oldPsdFragmentable;

    @ViewInject(R.id.etOldPsdPsd)
    private EditText etOldPsdPsd;

    @ViewInject(R.id.ivOldpsdfrgClear)
    private ImageView ivOldpsdfrgClear;

    @ViewInject(R.id.tvOldpsgErr)
    private TextView tvOldpsgErr;

    @Event(R.id.btnOldPsdNext)
    private void onOldPsdClick(View v){

        if(TextUtil.checkParams(etOldPsdPsd.getText().toString())){

            RequestParams entity=new RequestParams(Configurations.URL_CHECK_PASSWORD);
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            entity.addParameter(Configurations.PASSWORD, etOldPsdPsd.getText().toString());
            String device_id= JPushInterface.getRegistrationID(getContext());
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );
            Map<String, String> map=new TreeMap<String, String>();
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put("password", etOldPsdPsd.getText().toString());
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            x.http().post(entity, new Callback.CommonCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {

                    try {
                        if (result.getInt(Configurations.STATUSCODE) == 200) {
                            oldPsdFragmentable.onPsdFragment(etOldPsdPsd.getText().toString());

                        }else{
                            tvOldpsgErr.setText("密码输入错误，请重新输入");
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


        }else{
            ToastUtil.showShort(getActivity(),"原密码不能为空");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.OLDPSDFRAGMENT);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.OLDPSDFRAGMENT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etOldPsdPsd.addTextChangedListener(mTextWathcer);

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
                        etOldPsdPsd.setText("");
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OldPsdFragmentable){
            oldPsdFragmentable= (OldPsdFragmentable) context;
        }else{
            throw new ClassCastException(context+" must implements OldPsdFragmentable");
        }
    }
}