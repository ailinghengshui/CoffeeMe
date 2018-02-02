package com.hzjytech.coffeeme.wechatmanager;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.AccessToken;
import com.hzjytech.coffeeme.utils.URLUtil;

import org.xutils.http.RequestParams;

/**
 * Created by Hades on 2016/5/11.
 */
public class WeChatManager {

    public static String getAccessTokenUrl(String urlAccessToken,String token){
        urlAccessToken=urlAccessToken.replace("APPID", URLUtil.encodeURLUTF8(Configurations.WX_APP_ID));
        urlAccessToken=urlAccessToken.replace("SECRET", URLUtil.encodeURLUTF8(Configurations.WX_APP_SECRET));
        urlAccessToken=urlAccessToken.replace("CODE", URLUtil.encodeURLUTF8(token));
        return urlAccessToken;
    }

    public static String getValidAccessTokenUrl(String urlValidAccessToken,AccessToken accessToken){
        urlValidAccessToken=urlValidAccessToken.replace("ACCESS_TOKEN",accessToken.getAccess_token());
        urlValidAccessToken=urlValidAccessToken.replace("OPENID",accessToken.getOpenid());
        return urlValidAccessToken;
    }

    public static String getUserInfoUrl(String urlUserInfo,AccessToken accessToken){
        urlUserInfo = urlUserInfo.replace("ACCESS_TOKEN", URLUtil.encodeURLUTF8(accessToken.getAccess_token()));
        urlUserInfo = urlUserInfo.replace("OPENID", URLUtil.encodeURLUTF8(accessToken.getOpenid()));
        return urlUserInfo;
    }

    public static String getRefreshAccessTokenUrl(String urlRefreshAccessToken,AccessToken accessToken){
        urlRefreshAccessToken.replace("APPID", URLUtil.encodeURLUTF8(Configurations.WX_APP_ID));
        urlRefreshAccessToken.replace("REFRESH_TOKEN", accessToken.getRefresh_token());
        return urlRefreshAccessToken;
    }

}
