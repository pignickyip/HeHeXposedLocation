package com.hehe.hehexposedlocation.advanced_function;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

import java.util.List;

/**
 * Background and frontground detect service
 */

public class BgdFgdEnableActivity extends Activity {
    ToggleButton enable_servicebtn = null;
    SharedPreferences enable_service;
    SharedPreferences.Editor PE;
    TextView service_text;
    TextView infoService;
    Intent mServiceIntent;
    String msg = "";
    private BgdFgdListenService mBoundService;
    /** Called when the activity is first created. */
    private boolean mIsBound;
    static Context ctx;
    public Context getCtx() {
        if(ctx != null) {
            return ctx;
        }
        ctx = BgdFgdEnableActivity.this;
        return ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgdfgdenable);
        ctx = this;
        enable_service = getSharedPreferences(Common.BGDFGDRECORD_ENABLEKEY,0);
        service_text = (TextView) findViewById(R.id.bgdfgdText);
        enable_servicebtn = (ToggleButton) findViewById(R.id.bgdfgdtoggleButton);
        infoService = (TextView) findViewById(R.id.bgdfgdInfo);

        msg = "Here to set up background and front-ground application noise";
        infoService.setText(msg);

        final boolean serviceEnable = enable_service.getBoolean(Common.BGDFGDRECORDKEY_SERVICE_ENABLE, false);
        if(serviceEnable)
            msg = "Service running";
        else
            msg = "Not service running";
        service_text.setText(msg);

        enable_servicebtn.setChecked(serviceEnable);
        PE = enable_service.edit();
        enable_servicebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PE.remove(Common.BGDFGDRECORDKEY_SERVICE_ENABLE);
                if (!serviceEnable) {
                        doBindService();
                        PE.putBoolean(Common.BGDFGDRECORDKEY_SERVICE_ENABLE, true);
                        msg = "Service running";
                        service_text.setText(msg);
                        Toast.makeText(getApplicationContext(), msg , Toast.LENGTH_LONG).show();
                    } else {
                        PE.putBoolean(Common.BGDFGDRECORDKEY_SERVICE_ENABLE, false);
                        msg = "Service is end";
                        service_text.setText(msg);
                        doUnbindService();
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                    PE.apply();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
// 当进程崩溃时将被调用，因为运行在同一程序，如果是崩溃将所以永远不会发生
// 当解除绑定时也被调用
            mBoundService = null;
            Toast.makeText(BgdFgdEnableActivity.this,
                    "dis", Toast.LENGTH_SHORT)
                    .show();

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
// service连接建立时将调用该方法
            BgdFgdListenService.LocalBinder binder = (BgdFgdListenService.LocalBinder) service;
            BgdFgdEnableActivity.this.mBoundService = binder.getService();
            Toast.makeText(BgdFgdEnableActivity.this,
                    "connet", Toast.LENGTH_SHORT)
                    .show();
        }
    };

    void doBindService() {
// 建立service连接。因为我们知道程序会运行在本地里，因此使用显示的类名来实现service
// （但是不支持跟其他程序交互）
// 两种传递，一种是在manifest里写好intent-filter的action，一种是显示传递
// bindService(new Intent("com.LocalService.LocalService"), mConnection,
// Context.BIND_AUTO_CREATE);
//         bindService(new Intent(LocalActivity.this, LocalService.class),
//         mConnection, Context.BIND_AUTO_CREATE);

//如果用这种方法将会调用onStartCommand方法
        mBoundService = new BgdFgdListenService(getCtx());
        mServiceIntent = new Intent(getCtx(), mBoundService.getClass());
        if (!isMyServiceRunning(mBoundService.getClass())) {
            startService(mServiceIntent);
        }
        startService(new Intent(getCtx(), BgdFgdListenService.class));
        mIsBound = true;
       // Intent serviceIntent = new Intent(BgdFgdEnableActivity.this, BgdFgdListenService.class);
        //this.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (mIsBound) {
// Detach our existing connection.
            stopService(new Intent(BgdFgdEnableActivity.this, BgdFgdListenService.class));
//            unbindService(mConnection);
            mIsBound = false;
            // 綁定 Servic
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
}
