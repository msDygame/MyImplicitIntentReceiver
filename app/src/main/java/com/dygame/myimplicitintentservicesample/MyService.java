package com.dygame.myimplicitintentservicesample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/4/16.
 * Todo : ���@�Ӧ�StartService + BindServie �� Service
 * 1.�I�sContext��bindService()�|�s��Service�A�YService���}�ҴN�|�۰ʶ}�ҡC
 * 2.�}��Service��|�I�sonCreate()���O���|�I�sonStartCommand()
 * 3.�I�sonBind()�öǦ^IBinder����
 * 4.�I�sServiceConnection���O����onServiceConnected()��k�A�ñN�B�J3��IBinder�����@�Ѽƶǻ���onServiceConnected()�A�z�LIBinder�i�H���o��Service���pô�C�Ҧp�����I�s��Service�������Τ�k�C
 * 5.�I�sContext��unbindService()��k�A�h�t�η|�I�s��Service��onUnbind()�H�Ѱ�Context�PService���pô�F�H��|�۰ʩI�sService��onDestroy()�P���A�ȡC
 * 6.��@�����`�N���O�G
 *    context.bindService()�B�ΨӸj�w�A�ȴ��ѨϥΪ̤��\Activity�PSevice���ʡF�o�e�ШD�F��o���G�A�Ʀܦh��{���۰���o�Ǿާ@�C�A�ȩM�t�@�ӻP���j�w���ե����ɶ��@�˪��F�Y���h�Ӳե�]�u��M�P�@�ӪA�ȸj�w�@���C
 *    ���Ҧ��ե�����j�w����A�A�ȴN�|�۰ʾP���C(�o�I��context.startService()�ܤ��@�ˡI)
 *    �����A�ȥi�H�P�ɥΥH�W��ؤ覡�u�@�GstartService()�BbindService()�A�ҥH�i�H�Ұʤ���L��������ӥB���\�j�wActivity�C�o�u���M�A���S����{�o��تA�Ȫ��^�դ���: �uonBind()�v�C
 */
public class MyService extends Service
{
    private NotificationManager mNM;
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 1234 ;//?
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder
    {
        MyService getService()
        {
            return MyService.this;
        }
    }
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    //
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    //
    @Override
    public void onCreate()
    {
            super.onCreate();
            //follow android developer sample
            mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            // Display a notification about us starting.  We put an icon in the status bar.
            // Show a notification while this service is running.
            // In this sample, we'll use the same text for the ticker and the expanded notification
            CharSequence text = "Message Incoming" ;
            // Set the icon, scrolling text and timestamp
            Notification notification = new Notification(R.mipmap.ic_launcher, text, System.currentTimeMillis());
            // The PendingIntent to launch our activity if the user selects this notification
            Intent notificationIntent = new Intent(this, MainActivity.class);//new Intent(this, LocalServiceActivities.Controller.class)
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent , 0);
            // Set the info for the views that show in the notification panel.
            notification.setLatestEventInfo(this, "title", "infomation" , contentIntent);
            //�e��Service
            startForeground(NOTIFICATION, notification);
            // Send the notification.
            mNM.notify(NOTIFICATION, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        // Tell the user we stopped.
        Toast.makeText(this, R.string.string_stop_service, Toast.LENGTH_SHORT).show();
    }
}