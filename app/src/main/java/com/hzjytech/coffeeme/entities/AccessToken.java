package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/2/23.
 */
public class AccessToken {

    /**
     * access_token : OezXcEiiBSKSxW0eoylIeL48W-JuhW7djnYLKm2kWLFZ5U3yqIDud1JO0XCxcTd84ycLpONdMh0gaSniDwJY6oTqAX9YDiQfdJgTMExKQns1fj1RSKWvKIt6Nh6Wa88R-X--Q6bt_Xj93jWhaMOiAg
     * expires_in : 7200
     * refresh_token : OezXcEiiBSKSxW0eoylIeL48W-JuhW7djnYLKm2kWLFZ5U3yqIDud1JO0XCxcTd8PDONDpdgm41uGi4AkfrzJu0Mxw2sswxZuroiSgyH0JVuZoX8_AyZUArS2PIPrdk1KJdVa63b_vByzBVRPrxKyg
     * openid : od8cIw60lMTMrMffiSguvRjDVluk
     * scope : snsapi_userinfo
     * unionid : otwhYuAXlaJUxN5NEvhNd_Pr4UkI
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;
    /**
     * errcode : 40029
     * errmsg : invalid code
     */

    private int errcode;
    private String errmsg;


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public String getScope() {
        return scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrcode() {
        return errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }
}
