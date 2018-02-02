package com.hzjytech.coffeeme.weibomanager;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;

/**
 * Created by Hades on 2016/5/11.
 */
public class AuthListener implements WeiboAuthListener {

    private final Context mContext;
    private final RequestListener mListener;

    public AuthListener(Context context ,RequestListener listener){
        this.mContext=context;
        this.mListener=listener;
    }

    @Override
    public void onComplete(Bundle bundle) {
        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);

        if (accessToken != null && accessToken.isSessionValid()) {
            AccessTokenKeeper.writeAccessToken(mContext, accessToken);
            UsersAPI mUserAPI = new UsersAPI(mContext, LoginActivity.APP_KEY, accessToken);
            mUserAPI.show(Long.parseLong(accessToken.getUid()), mListener);

        } else {
            String code = bundle.getString("code");
            if (!TextUtils.isEmpty(code)) {
                ToastUtil.showShort(mContext, "Error code:" + code);
            } else {
                ToastUtil.showShort(mContext, "Error code:" + code);
            }
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(mContext, "取消授权", Toast.LENGTH_SHORT).show();
    }
}