package com.hzjytech.coffeeme.authorization.login;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(value = R.layout.activity_forget_psd)
public class ForgetPsdActivity extends BaseActivity implements ValidateFragment.OnValidateFragmentListener {

    @ViewInject(value = R.id.tbForgetpsdTitle)
    private TitleBar tbForgetpsdTitle;
    private FragmentTransaction fragmentTransaction;
    private ValidateFragment validateFragment = new ValidateFragment();
    private ValidateFragmentNext validateFragmentNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        tbForgetpsdTitle.setTitle(getResources().getString(R.string.str_validatephone));
        tbForgetpsdTitle.setTitleColor(Color.WHITE);
        tbForgetpsdTitle.setLeftText("登录");
        tbForgetpsdTitle.setLeftTextColor(Color.WHITE);
        tbForgetpsdTitle.setLeftImageResource(R.drawable.icon_left);
        tbForgetpsdTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragmentTransaction.add(R.id.flForgetpsgFragments,validateFragment,"validateFragment");
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onValidateInteract(String token) {
        tbForgetpsdTitle.setTitle(getResources().getString(R.string.str_resetpassword));
        tbForgetpsdTitle.setLeftImageResource(R.drawable.icon_left);
        tbForgetpsdTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbForgetpsdTitle.setLeftText("验证手机");
        tbForgetpsdTitle.setLeftTextColor(Color.WHITE);
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(validateFragment);
        fragmentTransaction.addToBackStack("validateFragment");
        validateFragmentNext=ValidateFragmentNext.newInstance(token);
        fragmentTransaction.add(R.id.flForgetpsgFragments,validateFragmentNext);
        fragmentTransaction.commit();

    }
}