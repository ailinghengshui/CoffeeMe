package com.hzjytech.coffeeme.me;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.fragments.BaseFragment;

import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

@ContentView(R.layout.fragment_setting)
public class SettingFragment extends BaseFragment {

    private OnSettingFragmentable onSettingFragmentable;

    @Event(R.id.btnSettingExit)
    private void onSettingExitClick(View v){
        SharedPreferences preferences=getActivity().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String auth_token=preferences.getString("auth_token","");
        SharedPreferences.Editor editor=preferences.edit();
        editor.clear();
        editor.commit();

        onSettingFragmentable.onSetingExitClick();
        RequestParams entity=new RequestParams(Configurations.URL_SESSIONS);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        x.http().request(HttpMethod.DELETE, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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

    @Event(R.id.ivFgsettingFeedback)
    private void onFgSettingFeedback(View v){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSettingFragmentable){
            onSettingFragmentable= (OnSettingFragmentable) context;
        }else{
            throw new ClassCastException(context+" must implement OnSettingFragmentable");
        }
    }

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public interface OnSettingFragmentable {
        void onSetingExitClick();
    }
}
