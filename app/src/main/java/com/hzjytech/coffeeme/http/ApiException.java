package com.hzjytech.coffeeme.http;


import android.text.TextUtils;

public class ApiException extends RuntimeException {


    public ApiException(String detailMessage) {
        super(detailMessage);
    }

    public ApiException(HttpResult httpResult) {
        this(getExceptionsMsg(httpResult));
    }

    public static String getExceptionsMsg(HttpResult status) {
        String msg;
        switch (status.getStatusCode()) {
            case 100:
                msg="账号或密码错误";
                break;
            case 401:
            case 403:
                if(!status.getStatusMsg().equals("非法请求")){
                    msg="token过期，请重新登录!";
                }else{
                    msg=status.getStatusMsg();
                }
                break;
            case 500:
                msg="服务器正在玩命加载中";
                break;
            default:
                if (TextUtils.isEmpty(status.getStatusMsg())) {
                    msg = "未知错误";
                } else {
                    msg = status.getStatusMsg();
                }
                break;
        }
        return msg;
    }
}

