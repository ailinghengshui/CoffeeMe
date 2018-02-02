package com.hzjytech.coffeeme;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.Dialogs.ForceUpdateDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.UpdateDialog;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.culture.CultureFragment;
import com.hzjytech.coffeeme.entities.AppVersion;
import com.hzjytech.coffeeme.home.HomeFragment;
import com.hzjytech.coffeeme.me.MeActivity;
import com.hzjytech.coffeeme.me.MeFragment;
import com.hzjytech.coffeeme.order.OrderFragment;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DownloadManagerPro;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.widgets.StatusbarColorUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity implements MeFragment.MeFragmentable {

    public boolean goOrder = false;
    FragmentManager fragmentManager = getSupportFragmentManager();
    @ViewInject(R.id.flMainContent)
    private FrameLayout flHomeContent;

    @ViewInject(R.id.llMainHome)
    private LinearLayout llMainHome;
    @ViewInject(R.id.llMainOrder)
    private LinearLayout llMainOrder;
    @ViewInject(R.id.llMainCulture)
    private LinearLayout llMainCulture;
    @ViewInject(R.id.llMainMe)
    private LinearLayout llMainMe;

    private static MainActivity mInstance;

    private long mClickExitTime = 0;

    private HomeFragment homeFragment;
    private OrderFragment orderFragment;
    private CultureFragment cultureFragment;
    private MeFragment meFragment;
    private Context context;
    private static final String DOWNLOAD_FOLDER_NAME = "CoffeeMe";
    private static final String DOWNLOAD_FILE_NAME = "CoffeeMe.apk";
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";
    private DownloadManager downloadManager;
    private DownloadManagerPro downloadManagerPro;
    private long downloadId = 0;
    private CompleteReceiver completeReceiver;
    private ProgressDialog pBar;

    @Event(R.id.llMainHome)
    private void onHomeClick(View view) {

        initBottom();
        llMainHome.setSelected(true);
        setTabSelection(0);
    }

    @Event(R.id.llMainOrder)
    private void onOrderClick(View view) {

        setTabSelection(1);
        initBottom();
        llMainOrder.setSelected(true);
    }

    @Event(R.id.llMainCulture)
    private void onCultureClick(View view) {

        setTabSelection(2);
        initBottom();
        llMainCulture.setSelected(true);
    }

    @Event(R.id.llMainMe)
    private void onMeClick(View view) {
        if(!llMainMe.isSelected()){
            setTabSelection(3);
            initBottom();
            llMainMe.setSelected(true);
        }

    }


    private void initBottom() {

        llMainHome.setSelected(false);
        llMainOrder.setSelected(false);
        llMainCulture.setSelected(false);
        llMainMe.setSelected(false);
    }

    public static MainActivity Instance() {
        if (null == mInstance)
            mInstance = new MainActivity();
        return mInstance;
    }


    //记录Fragment的位置
    private int position = 0;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setTabSelection(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //记录当前的position
        outState.putInt("position", position);
    }


    private void setTabSelection(int pos) {

        this.position = pos;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况

        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (orderFragment != null) {
            transaction.hide(orderFragment);
        }
        if (cultureFragment != null) {
            transaction.hide(cultureFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }

        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.flMainContent, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case 1:
                if (orderFragment == null) {
                    orderFragment = new OrderFragment();
                    transaction.add(R.id.flMainContent, orderFragment);
                } else {
                    transaction.show(orderFragment);
                }
                break;
            case 2:
                if (cultureFragment == null) {
                    cultureFragment = new CultureFragment();
                    transaction.add(R.id.flMainContent, cultureFragment);
                } else {
                    transaction.show(cultureFragment);
                }
                break;
            case 3:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    transaction.add(R.id.flMainContent, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusbarColorUtils.setStatusBarDarkIcon(this,false);  //参数 false 白色 true 黑色

        mInstance = this;

        goOrder = false;

        setTabSelection(position);
        initBottom();
        llMainHome.setSelected(true);

        context = getApplicationContext();

        initDownloadConfig();

        checkAppUpdate();
    }
    private void checkAppUpdate() {
        RequestParams entity = new RequestParams(Configurations.URL_APP_VERSION);
        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(MainActivity.this));
        String device_id= JPushInterface.getRegistrationID(MainActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(MainActivity.this)));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.e("result", result.toString());
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        if (!TextUtils.isEmpty(result.getJSONObject("results").getString("app_version"))) {
                            final AppVersion appVersion = new Gson().fromJson(result.getJSONObject("results").getString("app_version"), AppVersion.class);
                            if (appVersion.isForce_update()) {
                                final ForceUpdateDialog dialogFragment = ForceUpdateDialog.newInstance("新版本更新", appVersion.getDescription(), "退出", "更新");
                                dialogFragment.setOnTwoClickListener(new ITwoButtonClick() {
                                    @Override
                                    public void onLeftButtonClick() {
                                        android.os.Process.killProcess(android.os.Process.myPid());   //获取PID
                                        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                                        //finish();
                                    }
                                    @Override
                                    public void onRightButtonClick() {
                                        //downLoadApk(appVersion.getDownload_url());
                                        downLoadApkWithProgress(appVersion.getDownload_url(),dialogFragment);
                                        //finish();
                                    }
                                });
                                MyApplication application = (MyApplication) getApplication();
                                application.setHasShowDialog(true);
                                dialogFragment.show(getSupportFragmentManager(), "forceUpdate");
                            } else {
                                if (appVersion.getUpdate_version_id() > SharedPrefUtil.getInt(Configurations.KEY_IGNORE_VERSION) && appVersion.getUpdate_version_id() > AppUtil.getVersionCode(MainActivity.this)) {
                                    final UpdateDialog updateDialog = UpdateDialog.newInstance("新版本更新", appVersion.getDescription());
                                    updateDialog.setOnTwoClickListener(new ITwoButtonClick() {
                                        @Override
                                        public void onLeftButtonClick() {
                                            SharedPrefUtil.putInt(Configurations.KEY_IGNORE_VERSION, appVersion.getUpdate_version_id());
                                        }

                                        @Override
                                        public void onRightButtonClick() {
                                            downLoadApk(appVersion.getDownload_url(),updateDialog);
                                        }
                                    });
                                    MyApplication application = (MyApplication) getApplication();
                                    application.setHasShowDialog(true);
                                    updateDialog.show(getSupportFragmentManager(), "updateDialog");
                                }
                            }
                        }

                        //current apk is the newest
                    } else if (result.getInt(Configurations.STATUSCODE) == 201) {
                    } else {
                        ToastUtil.showShort(MainActivity.this, result.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
private void downLoadApkWithProgress(final String downloadUrl, final ForceUpdateDialog dialogFragment){
    dialogFragment.setProgress(0);
    new Thread(new Runnable() {
        @Override
        public void run() {


            //Xutils3联网获取数据
            RequestParams param  = new RequestParams(downloadUrl);
            param.setSaveFilePath(Environment.getExternalStorageDirectory()+File.separator+DOWNLOAD_FOLDER_NAME+File.separator+DOWNLOAD_FILE_NAME);
            x.http().get(param, new Callback.ProgressCallback<File>() {
                @Override
                public void onSuccess(File file) {

                    Log.d("TAG",file.toString());
                    //down();
                    String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator)
                            .append(DOWNLOAD_FILE_NAME).toString();
                    install(context, apkFilePath);
                    dialogFragment.dismiss();
                    MainActivity.this.finish();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onWaiting() {

                }

                @Override
                public void onStarted() {

                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    dialogFragment.setProgress(Math.round(current*1000f/total)/10f);

                }
            });




        }
    }).start();
}
    private void downLoadApk(final String downloadUrl, final UpdateDialog updateDialog){
        updateDialog.setProgress(0);
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Xutils3联网获取数据
                RequestParams param  = new RequestParams(downloadUrl);
                param.setSaveFilePath(Environment.getExternalStorageDirectory()+File.separator+DOWNLOAD_FOLDER_NAME+File.separator+DOWNLOAD_FILE_NAME);
                x.http().get(param, new Callback.ProgressCallback<File>() {
                    @Override
                    public void onSuccess(File file) {

                        Log.d("TAG",file.toString());
                        //down();
                        String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                                .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator)
                                .append(DOWNLOAD_FILE_NAME).toString();
                        install(context, apkFilePath);
                        updateDialog.dismiss();
                        MainActivity.this.finish();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }

                    @Override
                    public void onWaiting() {

                    }

                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        updateDialog.setProgress(Math.round(current*1000f/total)/10f);

                    }
                });




            }
        }).start();
    }
    private void downLoadApk(String downloadUrl) {
        downloadId = SharedPrefUtil.getLong(KEY_NAME_DOWNLOAD_ID);
        File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
        request.setTitle("CoffeeMe");
//                request.setDescription("meilishuo desc");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        // request.allowScanningByMediaScanner();
//                 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//                 request.setShowRunningNotification(false);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//        request.setMimeType("application/cn.trinea.download.file");
        downloadId = downloadManager.enqueue(request);
        /** save download id to preferences **/
        SharedPrefUtil.putLong(KEY_NAME_DOWNLOAD_ID, downloadId);
    }

    private void initDownloadConfig() {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
        completeReceiver = new CompleteReceiver();
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(completeReceiver);
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == downloadId) {
                if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    String apkFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator)
                            .append(DOWNLOAD_FILE_NAME).toString();
                    install(context, apkFilePath);
                }
            }
        }
    }

    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }


    @Override
    public void onMeFragmentClick(String info) {
        Intent intent = new Intent(MainActivity.this, MeActivity.class);
        intent.putExtra("info", info);
        startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            showQuitAlert();
            return true;
        }
        return false;
    }


    public void showQuitAlert() {
        long clickTime = System.currentTimeMillis();
        if (mClickExitTime != 0 && clickTime - mClickExitTime < 2000) {
            finish();
        } else {
            mClickExitTime = clickTime;
            Toast.makeText(mInstance, "再按一次退出 Coffee Me", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onResume() {

        MobclickAgent.onResume(this);

        if (goOrder) {
            goOrder = false;
            setTabSelection(1);
            initBottom();
            llMainOrder.setSelected(true);

        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}