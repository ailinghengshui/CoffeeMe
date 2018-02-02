package com.hzjytech.coffeeme.utils;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.DialogFragment;

import com.baidu.mapapi.SDKInitializer;
import com.hzjytech.coffeeme.baidumap.BaiduLocationService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Hades on 2016/1/28.
 */
public class MyApplication extends Application {

    public static final boolean IS_DEBUG =false ;

    public BaiduLocationService baiduLocationService;
    //用于控制dialog弹出顺序
    protected boolean hasShowDialog=false;

    @Override
    public void onCreate() {

        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(IS_DEBUG);

        SharedPrefUtil.load(this);
        UserUtils.init(this);
        initImageLoader(getApplicationContext());

        baiduLocationService=new BaiduLocationService(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());

        initJPush();
    }

    private void initJPush() {
        JPushInterface.setDebugMode(IS_DEBUG);
        JPushInterface.init(this);
    }


    public static void initImageLoader(Context context) {
        /*
        * configuration表示ImageLoader的全局配置信息，创建ImageLoader时使用
        * 可包括图片最大尺寸，线程池，缓存，下载器，解码器等等。
        * 这里可以根据需要自行配置，修改慎重！
        * */
        ImageLoaderConfiguration configuration =new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3)/*线程池的大小，默认值为3,注意不要设置的过大，过大之后会有OOM问题*/
                .threadPriority(Thread.NORM_PRIORITY - 1)/*设置线程的优先级别：5-1*/
                /*
                * tasksProcessingOrder:设置图片加载和显示队列处理的类型 默认为QueueProcessingType.
                FIFO注:如果设置了taskExecutor或者taskExecutorForCachedImages 此设置无效
                */
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))/*设置内存缓存 默认为一个当前应用可用内存的1/8大小的LruMemoryCache*/
                .memoryCacheSize(2 * 1024 * 1014)/*设置内存缓存的最大大小 默认为一个当前应用可用内存的1/8    */
                .memoryCacheSizePercentage(13)/*设置内存缓存最大大小占当前应用可用内存的百分比 默认为一个当前应用可用内存的1/8*/
                .diskCache(new UnlimitedDiskCache(StorageUtils.getCacheDirectory(context)))/*默认为StorageUtils.getCacheDirectory(getApplicationContext()),即/mnt/sdcard/android/data/包名/cache/*/
                .diskCacheSize(50 * 1024 * 1024)/*设置硬盘缓存的最大大小*/
                .diskCacheFileCount(100)/*设置硬盘缓存的文件的最多个数*/
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())/*设置硬盘缓存文件名生成规范*/
                .imageDownloader(new BaseImageDownloader(context))/*设置图片下载器*/
                .imageDecoder(DefaultConfigurationFactory.createImageDecoder(false))/*设置图片解码器*/
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())/*设置默认的图片显示选项*/
                .denyCacheImageMultipleSizesInMemory()/*不缓存图片的多种尺寸在内存中*/
                .writeDebugLogs()/*打印调试Log,注意上线之前要去掉这句话*/
                .imageDownloader(/*图片下载器的设置*/new BaseImageDownloader(context, 5 * 1000, 30 * 100)/*（connectTimeout,readTimeout）超时时间*/
                )
                .build();
        ImageLoader.getInstance().init(configuration);/*使用基本配置信息初始化ImageLoader*/
    }
    public void setHasShowDialog(boolean hasShowDialog){
        this.hasShowDialog=hasShowDialog;
    }
    public boolean getHasShowDialog(){
        return hasShowDialog;
    }
}
