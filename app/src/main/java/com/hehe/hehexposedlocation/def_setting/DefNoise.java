package com.hehe.hehexposedlocation.def_setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


import static com.hehe.hehexposedlocation.def_setting.DefActivity.POSITION;
import static com.hehe.hehexposedlocation.def_setting.FreeList.HoHo;
import static com.hehe.hehexposedlocation.def_setting.FreeList.KEYWORD_LIST;
import static com.hehe.hehexposedlocation.def_setting.FreeList.PACKGE_LIST;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorExact;

/**
 * The Spinner noise setting
 */
//TODO the sharedPreferences_whitelist need to reset/clear
public class DefNoise implements IXposedHookLoadPackage  {
    final static int sdk = Build.VERSION.SDK_INT;
    final static double MaxLat= -90.0;
    final static double MinLat = 90.0;
    final static double MaxLong = 180.0;
    final static double MinLong = -180.0;
    final static String[] FreePacketList = PACKGE_LIST;
    final static String[] FreeKeywordList = KEYWORD_LIST;
    final List<String> appList = new ArrayList<String>();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //http://api.xposed.info/reference/de/robv/android/xposed/XSharedPreferences.html
        final XSharedPreferences sharedPreferences_whitelist = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE);
        final XSharedPreferences sharedPreferences_posit = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_POSITION);
        final XSharedPreferences sharedPreferences_customer = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREDERENCES_CUSTOMER);
        sharedPreferences_whitelist.makeWorldReadable();
        sharedPreferences_posit.makeWorldReadable();
        sharedPreferences_customer.makeWorldReadable();

        appList.clear();
        appList.addAll(sharedPreferences_whitelist.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(appList);

        for(String as :appList)
                XposedBridge.log(as);

        // https://www.google.com.hk/search?q=how+to+use+the+data+in+hashmap+android&spell=1&sa=X&ved=0ahUKEwjy3e_XuMHRAhWEn5QKHZqmCtcQvwUIGCgA&biw=1451&bih=660
        //http://blog.csdn.net/yzzst/article/details/47659479
        if (sdk > 18) {
            try {
                Random rand = new Random(sdk);
                int omg = sharedPreferences_posit.getInt(Common.SHARED_PREFERENCES_POSITION, 0);
                // Latitudes range from -90 to 90.
                // Longitudes range from -180 to 180.

                int adapter = 1;
                if (omg == 0) {//Default
                    //XposedBridge.log("The User chose Default");
                } else if (omg == 1) {//Customer
                    adapter = sharedPreferences_customer.getInt(Common.SHARED_PREDERENCES_CUSTOMER, 5);
                    XposedBridge.log("The User chose Customer and the value is " + adapter);
                    if (sdk >= 21)
                        adapter = ThreadLocalRandom.current().nextInt(1, adapter);
                    else
                        adapter = ((rand.nextInt(adapter))) + 1;
                } else if (omg >= 2) {//Low, Medium,Highest
                    //adapter = sharedPreferences.getInt(Common.SHARED_PREFERENCES_POSITION,0);
                    if (sdk >= 21)
                        adapter = ThreadLocalRandom.current().nextInt(1, (50 * omg)) + 1;
                    else
                        adapter = (rand.nextInt(50 * omg)) + 1;

                    XposedBridge.log("The User chose Low, Medium, High.");
                } else
                    XposedBridge.log("The SharePreferences get wrong...");
                final int range = adapter;
            /*
            Source file of android location api
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/Location.java
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/LocationManager.java
            */
                if (omg != 0) {
                    if (sharedPreferences_whitelist.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true) || appList.contains(lpparam.packageName)) {
                        //https://android.googlesource.com/platform/frameworks/base/+/9637d474899d9725da8a41fdf92b9bd1a15d301e/core/java/android/provider/Settings.java
                        findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getString",
                                ContentResolver.class, String.class, new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                        String requested = (String) param.args[1];
                                        if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
                                            param.setResult("0");
                                            XposedBridge.log("Loaded app: " + lpparam.packageName);
                                        }
                                    }
                                });
                        if (Build.VERSION.SDK_INT >= 17) {
                            findAndHookMethod("android.provider.Settings.Secure", lpparam.classLoader, "getStringForUser",
                                    ContentResolver.class, String.class, Integer.TYPE, new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                            String requested = (String) param.args[1];
                                            if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
                                                param.setResult("0");
                                            }
                                        }
                                    });
                        }
                    }
                    findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLatitude",
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    //XposedBridge.log("Here im Xpsoed Hooked");
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    //super.afterHookedMethod(param);
                                    Random rand = new Random(range);
                                    //Original value + random value
                                    double RanLat = (
                                            (rand.nextDouble() % (MaxLat)) / (rand.nextInt(range)) * 1000 / 1000
                                    );
                                    String packageName = AndroidAppHelper.currentPackageName();
                                    String CurrpackageName = lpparam.packageName;
                                    try {
                                        double ori = (double) param.getResult();//get the original result
                                        for (String List_pkg : FreePacketList) {
                                            //TODO escape
                                            //Check the package in free list -> created by admin
                                            if (Objects.equals(List_pkg, packageName) || (appList.contains(packageName))) {
                                                //within white list
                                                if(Objects.equals(List_pkg, CurrpackageName) ||(appList.contains(CurrpackageName))){
                                                    param.setResult(ori);
                                                    XposedBridge.log(packageName + " needs the accuracy location" );
                                                }
                                                else{//if not in white list
                                                    double ra = rand.nextInt(5) / 10000000;
                                                    ra += ori;
                                                    param.setResult(ra);
                                                    XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra );
                                                }

                                            } else {
                                                //Match apart of
                                                for (String List_keyword : FreeKeywordList) {
                                                    int PlusORMinus = rand.nextInt(1);

                                                    if (packageName.startsWith(List_keyword)) {
                                                        double result = ori;
                                                        switch (PlusORMinus) {
                                                            case 0: {
                                                                result = ori + (RanLat * (range - 1));
                                                                break;
                                                            }
                                                            case 1: {
                                                                result = ori - (RanLat * (range - 1));
                                                                break;
                                                            }
                                                        }
                                                        param.setResult(result);
                                                        XposedBridge.log(packageName + " get the Latitude " + result);
                                                    } else {
                                                        double result = ori;
                                                        switch (PlusORMinus) {
                                                            case 0: {
                                                                result = ori + ((RanLat * (range - 1)) * 2);
                                                                break;
                                                            }
                                                            case 1: {
                                                                result = ori - ((RanLat * (range - 1)) * 2);
                                                                break;
                                                            }
                                                        }
                                                        param.setResult(result);
                                                        XposedBridge.log( packageName + " get the Latitude " + result);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        XposedBridge.log("Problem 1 at " + e);
                                    }
                                }
                            });
                    findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLongitude", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            //XposedBridge.log("Here im Xpsoed Hooked");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            //super.afterHookedMethod(param); Math.floor
                            Random rand = new Random(range);
                            //Original value + random value
                            double RanLong = (
                                    (rand.nextDouble() % (MaxLong)) / rand.nextInt(range) * 1000 / 1000
                            );
                            String packageName = AndroidAppHelper.currentPackageName();
                            String CurrpackageName = lpparam.packageName;

                            List<String> appList = new ArrayList<String>();
                            appList.addAll(sharedPreferences_whitelist.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
                            Collections.sort(appList);

                            try {
                                double ori = (double) param.getResult();//get the original result
                                for (String List_pkg : FreePacketList) {
                                    //TODO escape
                                    if (Objects.equals(List_pkg, packageName) || (appList.contains(packageName))) {
                                        if(Objects.equals(List_pkg, CurrpackageName) ||(appList.contains(CurrpackageName))){
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location" );
                                        }
                                        else{
                                            double ra = rand.nextInt(5) / 10000000;
                                            ra += ori;
                                            param.setResult(ra);
                                            XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra );
                                        }
                                    } else {
                                        //Match apart of
                                        for (String List_keyword : FreeKeywordList) {
                                            int PlusORMinus = rand.nextInt(1);
                                            if (packageName.startsWith(List_keyword)) {
                                                double result = ori;
                                                switch (PlusORMinus) {
                                                    case 0: {
                                                        result = ori + (RanLong * (range - 1));
                                                        break;
                                                    }
                                                    case 1: {
                                                        result = ori - (RanLong * (range - 1));
                                                        break;
                                                    }
                                                }
                                                param.setResult(result);
                                                XposedBridge.log(packageName + " get the getLongitude " + result);
                                            } else {
                                                double result = ori;
                                                switch (PlusORMinus) {
                                                    case 0: {
                                                        result = ori + ((RanLong * (range - 1)) * 2);
                                                        break;
                                                    }
                                                    case 1: {
                                                        result = ori - ((RanLong * (range - 1)) * 2);
                                                        break;
                                                    }
                                                }
                                                param.setResult(result);
                                                XposedBridge.log(packageName + " get the getLongitude " + result);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                XposedBridge.log("Problem 1 at " + e);
                            }
                        }
                    });
                    findAndHookMethod(Common.SYSTEM_LOCATION_LISTENER, lpparam.classLoader, "onLocationChanged",
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    //double latitude = haha;
                                    // double longitude = (-1)*haha;
                                    //  float accuracy = (float)hoho;
                                    // XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION_LISTENER +  " Value: " + latitude +", " + longitude + "\n Accuracy: " +accuracy) ;
                                }
                            });
                    findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                       /* Location l = new Location(LocationManager.GPS_PROVIDER);
                        l.setLatitude(2.33);
                        l.setLongitude(1.33);
                        l.setAccuracy(1000f);
                        l.setTime(System.currentTimeMillis());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                        }
                        param.setResult(l);*/
                        }
                    });
                }
            } catch (Exception e) {
                XposedBridge.log("Wrong here");
            }
        }
    }

    private String valueToStringOrEmpty(Map<String, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }
}
