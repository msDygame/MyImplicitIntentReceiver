package com.dygame.myimplicitintentservicesample;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

/**
 * Created by Administrator on 2015/5/18.
 * Start Service/Stop Service Sample , �b�I���C�j�@��Log�ثe���ɶ�
 * Nicky = �ӧQ��(?
 */
public class NickyService extends Service
{
    protected Handler pHandler = new Handler();
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        pHandler.postDelayed(showTime, 1000);
        Log.i("MyCrashHandler", "PostDelay");
        return Service.START_STICKY ;
    }

    @Override
    public void onDestroy()
    {
        pHandler.removeCallbacks(showTime);
        stopSelf();
        super.onDestroy();
    }

    private Runnable showTime = new Runnable()
    {
        public void run()
        {
            //log�ثe�ɶ�
            Log.i("MyCrashHandler", new Date().toString());
            pHandler.postDelayed(this, 1000);
        }
    };
}
