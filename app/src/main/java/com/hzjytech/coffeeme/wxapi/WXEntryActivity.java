package com.hzjytech.coffeeme.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by Hades on 2016/2/22.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api= WXAPIFactory.createWXAPI(this, Configurations.WX_APP_ID,true);
        api.registerApp(Configurations.WX_APP_ID);
        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp!=null){
            Configurations.baseResp=baseResp;
        }
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastUtil.showShort(this, "您取消授权");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                ToastUtil.showShort(this, "您拒绝授权");
                break;
            default:
                break;
        }
        WXEntryActivity.this.finish();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent,this);
    }
}
