package com.hzjytech.coffeeme.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hzjytech.coffeeme.R;

/**
 * Created by hehongcan on 2017/3/30.
 */
public class MusicService extends Service {

    private MediaPlayer mPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.morning);
        //设置可以重复播放
        mPlayer.setLooping(true);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPlayer.start();
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPlayer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {

        mPlayer.stop();

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer=null;
        super.onDestroy();
    }
}
