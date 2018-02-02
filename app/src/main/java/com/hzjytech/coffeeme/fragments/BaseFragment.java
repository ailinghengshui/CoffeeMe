package com.hzjytech.coffeeme.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

/**
 * Created by Hades on 2016/1/28.
 */
public class BaseFragment extends Fragment {
    private boolean injected = false;
    private ProgressDialog mProgressDlg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        injected = true;

        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!injected) {
            x.view().inject(this, this.getView());
        }
    }


    public void checkResOld(JSONObject res) {
        try {
            if (res.getInt(Configurations.STATUSCODE) == 401 || res.getInt(Configurations.STATUSCODE) == 403) {

                goLogin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void goLogin() {

        SharedPrefUtil.loginout();
//        SharedPrefUtil.saveAvatorUrl("");
//        SharedPrefUtil.saveUri("");
//        SharedPrefUtil.saveWeiboId("");
//        UserUtils.saveUserInfo(null);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onPause() {
        super.onPause();
    }


    public void showLoading() {
        if (null == mProgressDlg) {
            mProgressDlg = new ProgressDialog(getActivity(), R.style.MyDialogStyle);
            mProgressDlg.setCancelable(false);
        } else {
            mProgressDlg.dismiss();
        }
        mProgressDlg.show();
        mProgressDlg.setContentView(R.layout.dlalog_loading);
    }

    public void hideLoading() {
        if (null != mProgressDlg) {
            mProgressDlg.dismiss();
        }
    }

    public void showNetError() {
        ToastUtil.showShort(getActivity(), getActivity().getString(R.string.network_error_info));
    }


}
