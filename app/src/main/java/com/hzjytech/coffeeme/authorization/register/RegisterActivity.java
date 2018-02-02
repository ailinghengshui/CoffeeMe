package com.hzjytech.coffeeme.authorization.register;

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


/**
 * Created by Hades on 2016/1/26.
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseActivity implements RegisterFragment.RegisterFragmentListener {

    private final String TAG=RegisterActivity.class.getSimpleName();

    @ViewInject(R.id.tbRegTitle)
    private TitleBar tbRegTitle;

    private FragmentTransaction fragmentTransaction;
    private RegisterFragment registerFragment;
    private RegisterFragmentNext registerFragmentNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tbRegTitle.setTitle("注册");
        tbRegTitle.setTitleColor(Color.WHITE);
        tbRegTitle.setLeftImageResource(R.drawable.icon_left);
        tbRegTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        registerFragment=new RegisterFragment();
        fragmentTransaction.add(R.id.flRegisterFragments,registerFragment,"registerfragment");
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
    public void onRegisterFragment(String auto_token) {
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(registerFragment);
        fragmentTransaction.addToBackStack("registerfragment");
        registerFragmentNext=RegisterFragmentNext.newInstance(auto_token);
        fragmentTransaction.add(R.id.flRegisterFragments,registerFragmentNext,"registerfragmentnext");
        fragmentTransaction.commit();

    }
}