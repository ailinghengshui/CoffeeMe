package com.hzjytech.coffeeme.me;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.VisibleForTesting;
import android.support.v7.view.menu.MenuWrapperFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.ForceUpdateDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.UpdateDialog;
import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppVersion;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DiskCacheUtil;
import com.hzjytech.coffeeme.utils.DownloadManagerPro;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.StorageUtils;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_setting)
public class SettingActivity extends BaseActivity {

    private static final String DOWNLOAD_FOLDER_NAME = "CoffeeMe";
    private static final String DOWNLOAD_FILE_NAME = "CoffeeMe.apk";
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";

    @ViewInject(R.id.titleBar)
    private TitleBar tbSettingTitle;

    @ViewInject(R.id.tvSettiongCachesize)
    private TextView tvSettiongCachesize;


    private Context context;
    private DownloadManager downloadManager;
    private DownloadManagerPro downloadManagerPro;
    private long downloadId = 0;
    private CompleteReceiver completeReceiver;


    @Event(R.id.llSettingClearCache)
    private void onClearCacheClick(View view) {

        ImageLoader.getInstance().clearDiskCache();
        try {
            tvSettiongCachesize.setText("0.0Byte");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.SETTINGACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.SETTINGACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Event(R.id.btnSettingFeedback)
    private void onFeedbackClick(View view) {
        Intent intent = new Intent(SettingActivity.this, FeedbackActivity.class);
        startActivity(intent);

    }

    @VisibleForTesting
    public int remoteCode = 10;
    @VisibleForTesting
    public int ignoreCode = 10;

    @VisibleForTesting
    public boolean isUpdate=true;

    @Event(R.id.btnSettingUpdate)
    private void onUpdateClick(View view) {


        showLoading();
        RequestParams entity=new RequestParams(Configurations.URL_APP_VERSION);
        entity.addParameter(Configurations.APP_ID,AppUtil.getVersionCode(SettingActivity.this));

        String device_id= JPushInterface.getRegistrationID(SettingActivity.this);
        long timeStamp= TimeUtil.getCurrentTime();
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(SettingActivity.this)));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                hideLoading();

                try {
                    if(result.getInt(Configurations.STATUSCODE)==200){
                        if(!TextUtils.isEmpty(result.getJSONObject("results").getString("app_version"))){
                            final AppVersion appVersion=new Gson().fromJson(result.getJSONObject("results").getString("app_version"),AppVersion.class);
                            if(appVersion.getUpdate_version_id()>AppUtil.getVersionCode(SettingActivity.this)){
                                final UpdateDialog updateDialog = UpdateDialog.newInstance("新版本更新", appVersion.getDescription());
                                updateDialog.setOnTwoClickListener(new ITwoButtonClick() {
                                    @Override
                                    public void onLeftButtonClick() {
                                        SharedPrefUtil.putInt(Configurations.KEY_IGNORE_VERSION,appVersion.getUpdate_version_id());
                                    }

                                    @Override
                                    public void onRightButtonClick() {
                                        downLoadApk(appVersion.getDownload_url(),updateDialog);
                                    }
                                });
                                updateDialog.show(getSupportFragmentManager(),"updateDialog");
                            }
                        }

                    }else{
                        ToastUtil.showShort(SettingActivity.this,result.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

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
    private void downLoadApk(String downLoadUrl) {
        downloadId = SharedPrefUtil.getLong(KEY_NAME_DOWNLOAD_ID);
        File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downLoadUrl));
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


    @Event(R.id.btnSettingSupport)
    private void onSupportClick(View view) {


        if (queryInstalledMarketPkgs(SettingActivity.this)) {
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } catch (Exception e) {

            }
        } else {
            ToastUtil.showShort(SettingActivity.this, "请先安装一个应用商店");
        }
    }

    @Event(R.id.btnSettingAboutus)
    private void onAboutusClick(View view) {
        Intent intent = new Intent(SettingActivity.this, AboutusActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tbSettingTitle.setTitle("设置");
        tbSettingTitle.setTitleColor(Color.WHITE);

        tbSettingTitle.setLeftImageResource(R.drawable.icon_left);
        tbSettingTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        context = getApplicationContext();

        initDownloadConfig();

        try {
            tvSettiongCachesize.setText(DiskCacheUtil.getTotalCacheSize(StorageUtils.getCacheDirectory(SettingActivity.this)));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean queryInstalledMarketPkgs(Context context) {

        if (context == null)
            return false;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        if (infos == null || infos.size() == 0)
            return false;
        int size = infos.size();
        if (size == 0)
            return false;
        else
            return true;
    }

}