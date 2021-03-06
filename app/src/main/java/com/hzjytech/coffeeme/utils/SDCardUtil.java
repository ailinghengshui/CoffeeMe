package com.hzjytech.coffeeme.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by Hades on 2016/3/16.
 */
public class SDCardUtil {

    private SDCardUtil(){
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static boolean isSDCardEnable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDCardPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
    }

    public static long getSDCardAllSize(){
        if(isSDCardEnable()){
            StatFs stat=new StatFs(getSDCardPath());
            long availabelBlocks=(long )stat.getAvailableBlocksLong()-4;
            long freeBlocks=stat.getAvailableBlocks();
            return freeBlocks*availabelBlocks;
        }
        return  0;
    }

    public static long getFreeBytes(String filePath){
        if(filePath.startsWith(getSDCardPath())){
            filePath=getSDCardPath();
        }else{
            filePath=Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat=new StatFs(filePath);
        long availableBlocks=(long)stat.getAvailableBlocks()-4;
        return stat.getBlockSize()*availableBlocks;
    }

    public static String getRootDirectoryPath(){
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
