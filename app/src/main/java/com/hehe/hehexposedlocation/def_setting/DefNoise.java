package com.hehe.hehexposedlocation.def_setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.location.GpsStatus;
import android.os.Build;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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

/**
 * The Spinner noise setting
 */

public class DefNoise implements IXposedHookLoadPackage  {
    int sdk = Build.VERSION.SDK_INT;
    private static Map<String, Integer> HAHA = new HashMap<>();
    final static double MaxLat= -90.0;
    final static double MinLat = 90.0;
    final static double MaxLong = 180.0;
    final static double MinLong = -180.0;
    final static String[] FreePacketList = PACKGE_LIST;
    final static String[] FreeKeywordList = KEYWORD_LIST;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //http://api.xposed.info/reference/de/robv/android/xposed/XSharedPreferences.html
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_POSITION);
        sharedPreferences.makeWorldReadable();

        try{
            if(HAHA == null){
                XposedBridge.log("HERE the HAHA is empty");
            }
        }
        catch(Exception e){
            //DONT TRY SHAREPREF
            XposedBridge.log("Problem get "+e);
        }
        // https://www.google.com.hk/search?q=how+to+use+the+data+in+hashmap+android&spell=1&sa=X&ved=0ahUKEwjy3e_XuMHRAhWEn5QKHZqmCtcQvwUIGCgA&biw=1451&bih=660
        //http://blog.csdn.net/yzzst/article/details/47659479
        if(sdk > 18) {
            try {
                Random rand = new Random();
                int omg = sharedPreferences.getInt(Common.SHARED_PREFERENCES_POSITION,0);
                // Latitudes range from -90 to 90.
                // Longitudes range from -180 to 180.
                //Need to restart the phone each time
                int adapter = 1;
                if(omg == 0 ){//Default
                    adapter = 1;
                    XposedBridge.log("The User chose  Default");
                }
                else if(omg==1) {//Customer
                    adapter = sharedPreferences.getInt(Common.SHARED_PREDERENCES_CUSTOMER,5);
                    XposedBridge.log("The User chose  Customer and the value is " + adapter);
                }
                else if(omg>=2){//Low, Medium,Highest
                    adapter = sharedPreferences.getInt(Common.SHARED_PREFERENCES_POSITION,0);
                    adapter = rand.nextInt(adapter);
                    XposedBridge.log("The User chose....");
                }
                else
                    adapter = 5;
                final int range = adapter;

                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLatitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                //XposedBridge.log("Here im Xpsoed Hooked");
                            }
                            @Override //TODO!
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                //super.afterHookedMethod(param);
                                Random rand = new Random();
                                double RanLat = Math.floor( //Original value + random value
                                        ((rand.nextDouble() % (MaxLat))
                                                / (range * range)) * 2
                                );
                                String packageName = AndroidAppHelper.currentPackageName();
                                try {
                                    //Integer test = 0;
                                    //test = DEFSETTING.get(packageName);
                                    double ori = (double) param.getResult();//get the original result
                                    for (String List_pkg : FreePacketList) {
                                        if (Objects.equals(List_pkg, packageName)) {
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location" );
                                        } else {
                                            //Match apart of
                                            for (String List_keyword : FreeKeywordList) {
                                                if (packageName.startsWith(List_keyword)) {
                                                    double result = ori + (RanLat * 20);
                                                    param.setResult(result);
                                                    XposedBridge.log(packageName + " get the Latitude " + result);
                                                } else {
                                                    double result = ori + (RanLat * 200);
                                                    param.setResult(result);
                                                    XposedBridge.log("Loaded app: " + packageName + " get the Latitude " + result);
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
                    @Override //TODO!
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //super.afterHookedMethod(param);
                        Random rand = new Random();
                        double RanLong = Math.floor( //Original value + random value
                                ((rand.nextDouble() % (MaxLong))
                                        / (range * range)) * 2
                        );
                        String packageName = AndroidAppHelper.currentPackageName();
                        try {
                            //Integer test = 0;
                            //test = DEFSETTING.get(packageName);
                            double ori = (double) param.getResult();//get the original result
                            for (String List_pkg : FreePacketList) {
                                if (Objects.equals(List_pkg, packageName)) {
                                    param.setResult(ori);
                                    XposedBridge.log(packageName + " needs the accuracy location" );
                                } else {
                                    //Match apart of
                                    for (String List_keyword : FreeKeywordList) {
                                        if (packageName.startsWith(List_keyword)) {
                                            double result = ori + (RanLong * 20);
                                            param.setResult(result);
                                            XposedBridge.log(packageName + " get the getLongitude " + result);
                                        } else {
                                            double result = ori + (RanLong * 200);
                                            param.setResult(result);
                                            XposedBridge.log("Loaded app: " + packageName + " get the getLongitude " + result);
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            XposedBridge.log("Problem 1 at " + e);
                        }

                    }
                });
            } catch (Exception e) {
                XposedBridge.log("Wrong here");
            }
        }
    }
    private String valueToStringOrEmpty(Map<String, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }
    @SuppressLint("DefaultLocale")
    private boolean FindPerrmission(String pa){
        return false;
    }


}
