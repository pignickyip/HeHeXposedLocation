package com.hehe.hehexposedlocation.advanced_function;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.ProcessManager;
import com.hehe.hehexposedlocation.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

//繼承android.app.Service
public class BgdFgdListenService extends Service {
    //找到UI工人的經紀人，這樣才能派遣工作  (找到顯示畫面的UI Thread上的Handler)
    private Handler mUI_Handler = new Handler();
    //宣告特約工人的經紀人
    private Handler mThreadHandler;
    //宣告特約工人
    private HandlerThread mThread;

    final List<String> RunningApps = new ArrayList<String>();
    //private final ArrayList<Integer> RunningAppsID = new ArrayList<>();
    List<ActivityManager.AppTask> recentTasks;

    SharedPreferences RunningAppsPref = null;
    SharedPreferences.Editor PE;

    List<ActivityManager.RunningAppProcessInfo> Runinng;

    Context ctx;
    public Context getCtx() {
        return ctx;
    }
    public BgdFgdListenService(Context applicationContext) {
        super();
        ctx = applicationContext;
        Log.i("Service", "here I am!");
    }
    public BgdFgdListenService() {
        super();
    }
    // 与界面交互的类，由于service跟界面总是运行在同一程序里，所以不用处理IPC
    public class LocalBinder extends Binder {
        BgdFgdListenService getService() {
            return BgdFgdListenService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d("HEHEXPOSED_TEST", "MainService onCreate");
        RunningAppsPref = getSharedPreferences(Common.BGDFGDRECORDKEY , 0);
        findApp();
    }
    private void findApp(){
        //聘請一個特約工人，有其經紀人派遣其工人做事 (另起一個有Handler的Thread)
        mThread = new HandlerThread("name");
        //讓Worker待命，等待其工作 (開啟Thread)
        mThread.start();
        //找到特約工人的經紀人，這樣才能派遣工作 (找到Thread上的Handler)
        mThreadHandler=new Handler(mThread.getLooper());
        //請經紀人指派工作名稱 r，給工人做
        mThreadHandler.post(r1);
    }
    private Runnable r1=new Runnable () {
        public void run() {
            dosth();
        }
    };
    private void dosth(){
        String hehe = "";
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Runinng = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo app : Runinng) {
                try  {
                    hehe = app.processName;
                }
                catch (Exception e){
                    Log.d("Fuck","Fuck");
                }
                Log.d("BUUBU",hehe);
            }
            Runinng.clear();
            //Get the running application list
            //Source link : http://stackoverflow.com/questions/30619349/android-5-1-1-and-above-getrunningappprocesses-returns-my-application-packag
            List<ProcessManager.Process> processes = ProcessManager.getRunningApps();
            for (ProcessManager.Process process : processes) {
                StringBuilder sb = new StringBuilder();
                RunningApps.add(sb.append(process.name).toString());
            }
        }
        else {//For android 5.0
            List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++) {
                String adapter = recentTasks.get(i).baseActivity.toShortString();
                int SlashPosition = adapter.indexOf("/");
                String apps = (String) adapter.subSequence(1, SlashPosition);
                RunningApps.add(apps);
                //RunningAppsID.add(recentTasks.get(i).id);

                RunningApps.add(apps);
               // Log.d("HEHEXPOSED_TEST", apps);
            }
        }
        Collections.sort(RunningApps);
        //Collections.sort(RunningAppsID);
        PE = RunningAppsPref.edit();
        PE.putStringSet(Common.BGDFGDRUNNINGAPPLICATION, new HashSet<String>(RunningApps));
        PE.putString(Common.CURRENTAPPLICATION, hehe);
        PE.apply();
    }
    // 兼容2.0以前版本
    @Override
    public void onStart(Intent intent, int startId) {
    }

    // 在2.0以后的版本如果重写了onStartCommand，那onStart将不会被调用，注：在2.0以前是没有onStartCommand方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Service", "Received start id " + startId + ": " + intent);
// 如果服务进程在它启动后(从onStartCommand()返回后)被kill掉, 那么让他呆在启动状态但不取传给它的intent.
// 随后系统会重写创建service，因为在启动时，会在创建新的service时保证运行onStartCommand
// 如果没有任何开始指令发送给service，那将得到null的intent，因此必须检查它.
// 该方式可用在开始和在运行中任意时刻停止的情况，例如一个service执行音乐后台的重放
        //return super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service is end", Toast.LENGTH_SHORT).show();
        //移除工人上的工作
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(r1);
        }
        //解聘工人 (關閉Thread)
        if (mThread != null) {
            mThread.quit();
        }
        timer.cancel();
        timer.purge();
        timerTask.cancel();
        t = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();
    private Timer timer;
    private TimerTask timerTask;
    private boolean t = true;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, to wake up every 10 second
        timer.schedule(timerTask, 10000, 10000);
    }
    /**
     * it sets the timer to print the counter every x seconds
     */
    private int counter = 0;
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.d("HeHeXposed_Test" ,"im fuckng here" + counter++);
                if(t) {
                    try {
                        mThreadHandler.post(r1);
                        //startService(new Intent(ctx, BgdFgdStartServiceReceiver.class));
                    } catch (EmptyStackException e) {
                        Log.d("HeHeXposed_Test", "unl");
                    }
                }
                else{
                    timer.cancel();
                    timer.purge();
                    timerTask.cancel();
                    Log.d("HEHEXPOSED","Timer is end");
                }
            }
        };
    }
}
