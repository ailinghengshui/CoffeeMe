package com.hzjytech.coffeeme.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.hzjytech.coffeeme.configurations.Configurations;

/**
 * Created by Hades on 2016/3/16.
 */
public class SharedPrefUtil {

    protected static SharedPreferences mPreferences;
    private static final String MY_PROGRESS_ID = "myprogressid";
    public static SharedPreferences getMPreferences(){

        return mPreferences;
    }
    protected static SharedPreferences.Editor mEditor;
    public static final boolean DEBUG = true;
    private static final String PREFERENCE_FILE_NAME = "Coffeeme";
    private static final String STORE_POSITION = "setting_store_position";

    @SuppressLint("CommitPrefEdits")
    public static void load(Context a){
        try{
            mPreferences = a.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            mEditor = mPreferences.edit();
        }
        catch (Exception e){
        }
    }

    public static boolean save(){
        if (mEditor == null){
            return false;
        }

        return mEditor.commit();
    }

    public static boolean clear(){
        if (mEditor == null){
            return false;
        }
        mEditor.clear();
        return mEditor.commit();
    }

    private static final String LOGIN_STATE = "mlstate";
    public static final String LOGINING = "dlld";
    public static final String LOGINOUT = "zxld";
    public static final String NOTLOGIN = "mydld";

    public static final int LOGIN_VIA = -1;
    public static final int LOGIN_VIA_PHONE = 0x1111111;
    public static final int LOGIN_VIA_WEIBO = 0x2222222;
    public static final int LOGIN_VIA_WECHAT = 0x3333333;

    public static void loginSuccess(int loginMethod){

        if (mEditor == null){
            return;
        }

        mEditor.putString(LOGIN_STATE, LOGINING);
        mEditor.putInt(Configurations.LOGIN_METHOD,loginMethod);
        mEditor.commit();
    }

    public static void loginout(){

        if (mEditor == null){
            return;
        }
        mEditor.putString(LOGIN_STATE, LOGINOUT);
        mEditor.putInt(Configurations.LOGIN_METHOD,LOGIN_VIA);
        mEditor.commit();
    }

    public static String getLoginType(){

        if (mPreferences == null){
            return "";
        }
        return mPreferences.getString(LOGIN_STATE, NOTLOGIN);
    }
    public static void setLoginMethod(int type){
        if(mEditor==null){
            return;
        }
        mEditor.putInt(Configurations.LOGIN_METHOD,type);
        mEditor.commit();
    }

    public static int getLoginMethod(){

        if (mPreferences == null){
            return -1;
        }
        return mPreferences.getInt(Configurations.LOGIN_METHOD, -1);
    }


    public static final String URI = "uri";

    public static void saveUri(String uri){

        if (mEditor == null){
            return;
        }
        mEditor.putString(URI, uri);
        mEditor.commit();
    }


    public static String getUri(){

        if (mPreferences == null){
            return "";
        }
        return mPreferences.getString(URI,"");
    }


    public static final String AVATOR_URL = "avator_url";

    public static void saveAvatorUrl(String url){

        if (mEditor == null){
            return;
        }
        mEditor.putString(AVATOR_URL, url);
        mEditor.commit();
    }


    public static String getAvatorUrl(){

        if (mPreferences == null){
            return "";
        }
        return mPreferences.getString(AVATOR_URL,"");
    }
    public static void saveIsFromCoupon(boolean fromCoupon){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("fromcoupon",fromCoupon);
        mEditor.commit();
    }
    public static boolean getIsFromCoupon(){
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("fromcoupon",true);
    }
    public static final String WEIBO_OPEN_ID ="weibo_open_id" ;

    public static void saveWeiboId(String id){

        if (mEditor == null){
            return;
        }
        mEditor.putString(WEIBO_OPEN_ID, id);
        mEditor.commit();
    }


    public static String getWeiboId(){

        if (mPreferences == null){
            return "";
        }
        return mPreferences.getString(WEIBO_OPEN_ID,"");
    }

    public static long getLong( String keyNameDownloadId) {
        if(mPreferences==null){
            return -1;
        }
        return mPreferences.getLong(keyNameDownloadId,-1);

    }

    public static boolean putLong(String keyNameDownloadId, long downloadId) {
        if(mEditor==null){
            return false;
        }
        mEditor.putLong(keyNameDownloadId,downloadId);
        return mEditor.commit();
    }


    public static boolean putBoolean(String key, boolean b) {
        if(mEditor==null){
            return false;
        }
        mEditor.putBoolean(key,b);
        return mEditor.commit();
    }

    public static boolean getBoolean(String key) {
        if(mPreferences==null){
            return false;
        }
        return mPreferences.getBoolean(key,false);
    }

    public static boolean putInt(String key, int versionCode) {
        if(mEditor==null){
            return false;
        }
        mEditor.putInt(key,versionCode);
        return mEditor.commit();
    }

    public static int getInt(String key) {
        if(mPreferences==null){
            return 0;
        }
        return mPreferences.getInt(key,0);
    }
    public static void saveIsFirstEnter(boolean isFirstEnter){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnter",isFirstEnter);
        mEditor.commit();
    }
    public static boolean getIsFirstEnter(){
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnter",true);
    }
    public static void saveIsFirstEnterAdjust(boolean isFirstEnterAdjust){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnterAdjust",isFirstEnterAdjust);
        mEditor.commit();
    }
    public static boolean getIsFirstEnterAdjust(){
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnterAdjust",true);
    }
    public static void saveIsFirstEnterFine(boolean isFirstEnterFine){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnterFine",isFirstEnterFine);
        mEditor.commit();
    }
    public static boolean getIsFirstEnterFine(){
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnterFine",true);
    }
 public static void saveIsFirstRegister(boolean isFirstRegister){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstRegister",isFirstRegister);
        mEditor.commit();
    }
    public static boolean getIsFirstRegister(){
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstRegister",false);
    }
    public static void saveIsFirstEnterMe(boolean isFirstEnterMe){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnterMe",isFirstEnterMe);
        mEditor.commit();
    }
    public static boolean getIsFirstEnterMe() {
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnterMe",true);
    }
    public static void saveIsFirstEnterReceipeFromMy(boolean isFirstEnterReceipeFromMy){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnterReceipeFromMy",isFirstEnterReceipeFromMy);
        mEditor.commit();
    }
    public static boolean getIsFirstEnterReceipeFromMy() {
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnterReceipeFromMy",true);
    }
    public static void saveIsFirstEnterReceipeFromShare(boolean isFirstEnterReceipeFromShare){
        if(mEditor==null){
            return;
        }
        mEditor.putBoolean("isFirstEnterReceipeFromShare",isFirstEnterReceipeFromShare);
        mEditor.commit();
    }
    public static boolean getIsFirstEnterReceipeFromShare() {
        if (mPreferences == null){
            return true;
        }
        return mPreferences.getBoolean("isFirstEnterReceipeFromShare",true);
    }
}
