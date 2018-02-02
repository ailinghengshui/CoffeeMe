package com.hzjytech.coffeeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hzjytech.coffeeme.Dialogs.ForceUpdateDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.OtherLoginDialog;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.UserUtils;

import cn.jpush.android.api.JPushInterface;

public class OtherLoginDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearData();

        final Bundle bundle=getIntent().getBundleExtra("notifi_id");
        OtherLoginDialog updateDialog = OtherLoginDialog.newInstance("下线提示", "您的账号在另一地点登录，您被迫下线。如非本人操作，则密码可能已泄露，建议重新登录后及时修改密码。","返回主页","重新登录");
        updateDialog.setOnTwoClickListener(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {
                JPushInterface.clearNotificationById(OtherLoginDialogActivity.this, (Integer) bundle.get(JPushInterface.EXTRA_NOTIFICATION_ID));
                Intent mainIntent = new Intent(OtherLoginDialogActivity.this, MainActivity.class);
                MainActivity.Instance().goHome = true;
                startActivity(mainIntent);
                finish();
            }

            @Override
            public void onRightButtonClick() {
                JPushInterface.clearNotificationById(OtherLoginDialogActivity.this, (Integer) bundle.get(JPushInterface.EXTRA_NOTIFICATION_ID));
                Intent loginIntent = new Intent(OtherLoginDialogActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                finish();
            }
        });

        updateDialog.show(getSupportFragmentManager(),"otherLogin");
    }

    private void clearData() {
        SharedPrefUtil.loginout();
        SharedPrefUtil.saveAvatorUrl("");
        SharedPrefUtil.saveUri("");
        SharedPrefUtil.saveWeiboId("");
        UserUtils.saveUserInfo(null);
    }
}
