package com.dygame.myimplicitintentservicesample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * 通過隱式Intent開啟Service:
 * Android不建議在AndroidManifest.xml的<service>標籤中使用<intent-filter>的，即不建議使用隱式Intent啟動Service；
 * 而且常用的隱式Intent（比如android.intent.action.VIEW）也無法將Intent喚醒，這些Intent通常都是Activity Action，即只能用於Activity。
 * 若要直接使用這些Activity Action的Intent開啟Service而不影響到前台程序，通過隱式Intent開啟Service，只需要繞一個小彎就可以了，
 * 首先建立一個Activity，將其Theme設置為android:theme="@android:style/Theme.NoDisplay"。
 * 當外部的隱式Intent到達的時候就會啟動這個Activity，我們只需要在其onCreate()方法中開啟Service並傳遞相應的參數，然後立即finish()掉這個Activity就OK。
 * 採用上述方法可以立即開啟一個Service而不影響到當前正在運行的前台程序，效果非常不錯。
 * 20150519@
 * AndroidStudio的Activity，繼承自 ActionBarActivity的類必須指定固定的集中Theme風格，而這些 Theme 風格是需要導入V7中的 appcompat LIB庫工程，
 * 編譯後再引用才能引用使用。然後不能再用@android:style/Theme.NoDisplay這個了。要改成@style/Theme.AppCompat。然後隱式Intent時,畫面會一閃....
 * 還有一種方法 把ActionBarActivity換成Activity...
 * 通過broadcastReceiver開啟Service:
 * 收廣播
 */
public class MainActivity extends ActionBarActivity
{
    protected MyService mBoundService;
    protected boolean mIsBound = false;
    protected static String TAG = "" ;
    protected Button testButton ;
    protected Button quitButton ;
    MyReceiver pReceiver;//BroadcastReceiver
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Crash Handler
        MyCrashHandler pCrashHandler = MyCrashHandler.getInstance();
        pCrashHandler.init(getApplicationContext());
        TAG = pCrashHandler.getTag() ;
        //在註冊廣播接收:
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.dygame.broadcast");//為BroadcastReceiver指定action，使之用於接收同action的廣播
        pReceiver = new MyReceiver();
        registerReceiver(pReceiver, intentFilter);
        //
        setContentView(R.layout.activity_main);
        //onClickListener
        testButton = (Button)findViewById(R.id.button) ;
        quitButton = (Button)findViewById(R.id.button2) ;
        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "test this activity and startService");
                Intent service = new Intent(MainActivity.this, NickyService.class);
                startService(service);
            }
        });
        quitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish() ;
            }
        });
    }

    protected ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            // Tell the user about this.
            Log.d(TAG, "onServiceDisconnected");
            //Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((MyService.LocalBinder) service).getService();
            // Tell the user about this.
            Log.d(TAG, "onServiceConnected");
            //Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, NickyService.class);
        stopService(intent);
        //註銷
        unregisterReceiver(pReceiver);
    }

    void doBindService()
    {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(MainActivity.this, MyService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        // Tell the user about this.
        Log.d(TAG, "Bind Service");
        //Toast.makeText(MainActivity.this, "Bind Service", Toast.LENGTH_SHORT).show();
    }

    void doUnbindService()
    {
        if (mIsBound)
        {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            // Tell the user about this.
            Log.d(TAG, "Unbinding Service");
            //Toast.makeText(MainActivity.this, "Unbinding Service", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyReceiver  extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (action.equals("android.intent.action.BOOT_COMPLETED"))
            {
                Log.i(TAG, "You've got mail");
            }
            if (action.equals("com.dygame.broadcast"))
            {
                Log.d(TAG, "Broadcast Recevie and Start Service");
                Intent service = new Intent(MainActivity.this, NickyService.class);
                startService(service);
                Bundle bundle = intent.getExtras();
                if (bundle != null)
                {
                    String sMessage = bundle.getString(TAG);
                    Log.i(TAG, "broadcast receiver action:" + action + "=" + sMessage);
                }
            }
            if (action.equals("com.dygame.unknown"))
            {
                Intent service = new Intent(MainActivity.this, NickyService.class);
                stopService(service);
            }
        }
    }
}
