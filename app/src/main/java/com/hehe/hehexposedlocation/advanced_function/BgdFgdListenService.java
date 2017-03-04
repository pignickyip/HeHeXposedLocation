package com.hehe.hehexposedlocation.advanced_function;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//繼承android.app.Service
public class BgdFgdListenService extends Service {
    /** indicates how to behave if the service is killed */
    int mStartMode;
    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private Handler handler = new Handler();
    private SharedPreferences record = null;
    private SharedPreferences.Editor PE =null;
    private final List<String> RunningApps = new ArrayList<>();
    private final List<Integer> RunningAppsID = new ArrayList<>();

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
    }
    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        record = getSharedPreferences(Common.BGDFGDRECORDKEY, 0);
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String hehe = "";
        for (int i = 0; i < recentTasks.size(); i++) {
            String adapter = recentTasks.get(i).baseActivity.toShortString();
            int SlashPosition = adapter.indexOf("/");
            String apps = (String) adapter.subSequence(1,SlashPosition);
            RunningApps.add(apps);
            RunningAppsID.add(recentTasks.get(i).id);
            if(isAppOnForeground(this,apps))
                hehe = apps;
        }
        Collections.sort(RunningApps);
        Collections.sort(RunningAppsID);

        PE = record.edit();
        PE.putStringSet(Common.BGDFGDAPPLICATION, new HashSet<String>(RunningApps));
        PE.putString(Common.CURRENTAPPLICATION,hehe);
        PE.putBoolean(Common.BGDFGDRECORDKEYUP, true);
        //PE.putStringSet(Common.BGDFGDAPPLICATIONID, new HashSet<Integer>(RunningAppsID));
        PE.apply();
        Toast.makeText(this, "Service Success", Toast.LENGTH_LONG).show();

        return START_STICKY;
    }
    private boolean isAppOnForeground(Context context , String pkg) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        String Compare ="";
        if(Objects.equals(pkg, packageName))
            Compare = packageName;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(Compare)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        //handler.removeCallbacks(showTime);
        super.onDestroy();
        PE = record.edit();
        PE.clear();
        PE.apply();
        Toast.makeText(this, "Service end", Toast.LENGTH_LONG).show();
    }
}
