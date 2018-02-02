package com.hzjytech.coffeeme;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ShareClipDialogWithTwoButton;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.ComputeAppItem;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Hades on 2016/1/26.
 */
public class BaseActivity extends AppCompatActivity {
    
    private ProgressDialog mProgressDlg = null;
    //程序是否恢复为前台
    private boolean isActive=false;
    private ClipboardManager c ;
    private MyApplication application;
    private InputMethodManager manager;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        handler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            FlymeSetStatusBarLightMode(window,false);
            MIUISetStatusBarLightMode(window,false);
        }
        c= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        application = (MyApplication) getApplication();
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }
    /**
     * 设置状态栏图标为深色和魅族特定的文字风格，Flyme4.0以上
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result=true;
            }catch (Exception e){

            }
        }
        return result;
    }

    public void showLoading() {
        if (null == mProgressDlg) {
            mProgressDlg = new ProgressDialog(this, R.style.MyDialogStyle);
            mProgressDlg.setCancelable(false);
        } else {
            mProgressDlg.dismiss();
        }
        mProgressDlg.show();
        mProgressDlg.setContentView(R.layout.dlalog_loading);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               mProgressDlg.dismiss();
                showNetError();
                handler.removeCallbacksAndMessages(null);
            }
        },10000);
    }

    public void hideLoading() {
        if (null != mProgressDlg) {
            mProgressDlg.dismiss();
            handler.removeCallbacksAndMessages(null);
        }
    }


    public void hideSoftKeyboard() {
        View focusView = getCurrentFocus();
        if (null != focusView && null != focusView.getWindowToken()) {
            ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        hideSoftKeyboard();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        hideSoftKeyboard();
        super.onBackPressed();
    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        FragmentManager fm = getSupportFragmentManager();
//        int index = requestCode >> 16;
//        if (index != 0) {
//            index--;
//            if (fm.getFragments() == null || index < 0
//                    || index >= fm.getFragments().size()) {
//                LogUtil.i("", "---Activity result fragment index out of range: 0x"
//                        + Integer.toHexString(requestCode));
//                return;
//            }
//            Fragment frag = fm.getFragments().get(index);
//            if (frag == null) {
//                LogUtil.i("", "---Activity result no fragment exists for index: 0x"
//                        + Integer.toHexString(requestCode));
//            } else {
//                handleResult(frag, requestCode, resultCode, data);
//            }
//            return;
//        }
//
//    }


    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }


    public void checkResOld(JSONObject res) {
        try {
            if (res.getInt(Configurations.STATUSCODE) == 401 || res.getInt(Configurations.STATUSCODE) == 403) {
                if(res.getString(Configurations.STATUSMSG).equals("非法请求")){
                    return;
                }
                ToastUtil.showShort(BaseActivity.this, res.getString(Configurations.STATUSMSG));
                goLogin();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void goLogin() {
        SharedPrefUtil.loginout();

        Intent intent = new Intent(BaseActivity.this,  LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * 点击空白区域收起键盘
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 监听剪切板，更加信息判断是否有携带分享饮品
     */
    @Override
    protected void onResume() {
        super.onResume();
    }



    /**
     * 1、除了欢迎界面都可以弹出
     * 2、退出后台再次进入和初始进入都可以弹出
     * 3、弹出后复制取消都会消除剪切板
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        application.setHasShowDialog(false);

    }


    public void showNetError() {
        hideLoading();
        ToastUtil.showShort(BaseActivity.this, getString(R.string.network_error_info));
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
    public boolean buyEnable(DisplayItems.AppItem appItem){
        if(appItem.isBuy_enable()){
            return true;
        }else{
            showAppItemIsClose();
            return false;
        }
    }
    public boolean buyEnable(List<NewGood> goods) {
        for (NewGood good : goods) {
            if (!good.getItem()
                    .isBuy_enable()) {
                showSomeAppItemIsClose();
                return false;
            }
        }
        return true;

    }

    private void showSomeAppItemIsClose() {
        HintDialog.newInstance("提示", "所含饮品已下架", "确定").show(getSupportFragmentManager(), "personInfoHint");
    }

    private void showAppItemIsClose() {
        HintDialog.newInstance("提示", "该饮品已下架", "确定").show(getSupportFragmentManager(), "personInfoHint");
    }
}
