package com.hzjytech.coffeeme.weibomanager;

import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.ClipDialogWithTwoButton;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * 避免多次调用微博分享后返回界面混乱
 * Created by hehongcan on 2017/2/22.
 */
public class WBEntryActivity extends BaseActivity implements IWeiboHandler.Response{

    //sian微博分享
    private IWeiboShareAPI mWeiboShareAPI;
    private ClipboardManager c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, LoginActivity.APP_KEY);
        mWeiboShareAPI.registerApp();
        mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
    }

    /**
     *
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        if(baseResponse!= null){
            switch (baseResponse.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    ToastUtil.showShort(this, "分享成功");
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    ToastUtil.showShort(this, "分享取消");
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    ToastUtil.showShort(this,"分享失败"+ "Error Message: " + baseResponse.errMsg);
                    break;
            }
        }
        this.finish();
    }

}
