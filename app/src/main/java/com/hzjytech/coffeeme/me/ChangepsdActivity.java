package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_changepsd)
public class ChangepsdActivity extends BaseActivity implements OldPsdFragment.OldPsdFragmentable {

    OldPsdFragment oldPsdFragment;
    NewPsdFragment newPsdFragment;
    FragmentTransaction fragmentTransaction;

    @ViewInject(R.id.titleBar)
    private TitleBar tbChangePsdTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle();

        oldPsdFragment=new OldPsdFragment();

        fragmentTransaction=getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.flChangepsdContainer,oldPsdFragment,"OldPsdFragment");

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

    private void initTitle() {
        tbChangePsdTitle.setTitle("修改登录密码");
        tbChangePsdTitle.setTitleColor(Color.WHITE);
        tbChangePsdTitle.setLeftImageResource(R.drawable.icon_left);
        tbChangePsdTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }else{
                    finish();
                }
            }
        });

    }

    @Override
    public void onPsdFragment(String oldPsd) {
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(oldPsdFragment);
        fragmentTransaction.addToBackStack("OldPsdFragment");
        newPsdFragment=NewPsdFragment.newInstance(oldPsd);
        fragmentTransaction.addToBackStack("OldPsdFragment");
        fragmentTransaction.add(R.id.flChangepsdContainer,newPsdFragment,"NewPsdFragment");
        fragmentTransaction.commit();
    }
}