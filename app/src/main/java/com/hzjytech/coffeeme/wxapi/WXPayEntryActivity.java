package com.hzjytech.coffeeme.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;
	private String appid = null;
	private String noncestr = null;
	private String partnerid = null;
	private String prepayid = null;
	private String timestamp = null;
	private String sign = null;

	private boolean isPaused = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Intent intent = getIntent();
		appid = intent.getStringExtra("appid");
		noncestr = intent.getStringExtra("noncestr");
		partnerid = intent.getStringExtra("partnerid");
		prepayid = intent.getStringExtra("prepayid");
		timestamp = intent.getStringExtra("timestamp");
		sign = intent.getStringExtra("sign");

		api = WXAPIFactory.createWXAPI(this, appid);

		if (!api.isWXAppInstalled()) {

			setResult(RESULT_CANCELED);

			ToastUtil.showShort(WXPayEntryActivity.this, "抱歉微信未安装");

			WXPayEntryActivity.this.finish();
		} else if (!api.isWXAppSupportAPI()) {

			setResult(RESULT_CANCELED);

			ToastUtil.showShort(WXPayEntryActivity.this, "抱歉该版本微信不支持支付");

			WXPayEntryActivity.this.finish();
		} else {
			api.registerApp(appid);
			api.handleIntent(getIntent(), this);
			sendPayReq();
			isPaused = false;

		}
	}

	private void sendPayReq() {

		PayReq req = new PayReq();
		req.appId = appid;
		req.partnerId = partnerid;
		req.prepayId = prepayid;
		req.nonceStr = noncestr;
		req.timeStamp = timestamp;
		req.packageValue = "Sign=WXPay";
		req.sign = sign;
		req.extData= "app data"; // optional
		api.sendReq(req);
	}


	@Override
	public void onReq(BaseReq resp) {
		LogUtil.i("", "---onReq(BaseReq resp)");
	}

	@Override
	public void onResp(BaseResp resp1) {
		LogUtil.i("", "---onResp(BaseResp resp1, errCode = " + resp1.errCode);
		if (resp1.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int errCode = resp1.errCode;
			Intent intent = new Intent();

			switch (errCode) {
				case 0:// success

					break;
				case -1:// failed

					break;
				case -2:// cancel

					break;
				default:

					break;
			}
			// sendBroadcast(intent);
			intent.putExtra("errCode",errCode);
			setResult(RESULT_OK, intent);
		}
		WXPayEntryActivity.this.finish();
		LogUtil.i("", "---WXPayEntryActivity---finish---end---");
	}


	@Override
	protected void onNewIntent(Intent intent) {
		LogUtil.i("", "---onNewIntent");
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}


	@Override
	protected void onResume() {
		LogUtil.i("", "---onResume...isPaused..." + isPaused);
		if (isPaused) {
			WXPayEntryActivity.this.finish();
			LogUtil.i("", "finish WXPayEntryActivity in method onResume...");
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		LogUtil.i("", "---onPause...isPaused..." + isPaused);
		isPaused = true;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		LogUtil.i("", "---onDestroy");
		super.onDestroy();
	}
}