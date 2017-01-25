package com.hehe.hehexposedlocation;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.appsettings.settings.ApplicationSettings;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;

import static android.R.attr.max;
import static com.hehe.hehexposedlocation.appsettings.XposedMod.prefs;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class OverrideSettingsSecure implements IXposedHookLoadPackage {
    private Map<String, Object> ChangeSetting = new HashMap<String, Object>();
    final static double MaxLat= -90.0;
    final static double MinLat = 90.0;
    final static double MaxLong = 180.0;
    final static double MinLong = -180.0;
    final static int sdk = Build.VERSION.SDK_INT;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        final XSharedPreferences sharedPref = new XSharedPreferences(BuildConfig.APPLICATION_ID, com.hehe.hehexposedlocation.appsettings.Common.PREFS);
        sharedPref.makeWorldReadable();
        sharedPreferences.makeWorldReadable();
        //http://blog.csdn.net/yzzst/article/details/47659479
        //TODO try to do seem like the DefNoise
        ChangeSetting = ApplicationSettings.getSetting();
        if (sharedPreferences.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true) ||
                sharedPreferences.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>(0)).contains(lpparam.packageName)) {

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
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/Location.java
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/LocationManager.java
            // at API level 18, the function Location.isFromMockProvider is added
            if (sdk >= 18) {
                Random rand = new Random(); // random number
                String packageName = AndroidAppHelper.currentPackageName();
                int adapter = 0,omg = 0,Noise;
                try {
                    //TODO test  those code need no error
                    omg = sharedPreferences.getInt(packageName + com.hehe.hehexposedlocation.appsettings.Common.PREF_NOISE, 0);
                    if(omg == 0)
                        XposedBridge.log("Noise get 0 - " + omg);

                    int packageNOISE = prefs.getInt(packageName + com.hehe.hehexposedlocation.appsettings.Common.PREF_NOISE,
                            prefs.getInt(com.hehe.hehexposedlocation.appsettings.Common.PREF_DEFAULT + com.hehe.hehexposedlocation.appsettings.Common.PREF_NOISE, 0));
                    if(packageNOISE !=omg)
                        XposedBridge.log("packageNoise != omg");

                    if(sdk >= 21)
                        adapter = ThreadLocalRandom.current().nextInt(1, (50*omg)) + 1;
                    else
                        adapter = (rand.nextInt(50*omg)) + 1;

                    final int bubu = (int) ChangeSetting.get(packageName);
                }
                catch (Exception e){
                    XposedBridge.log("Problem 2");
                }
                if(adapter != 0)
                    Noise = adapter;
                else
                    Noise = 0;
                XposedBridge.log("Noise: " + Noise);
                //TODO!  Customer change
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLatitude",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLongitude",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                //if(bubu > 0)
                               //      param.setResult(54.1);
                               // XposedBridge.log("Here Over: " + 54.1 + " getLongitude is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                               // param.setResult((float)hoho);
                                //XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  "  + hoho+ " getAccuracy is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAltitude",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                               // param.setResult(true);
                               // XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " hasAltitude is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                              //  param.setResult(true);
                             //   XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " hasAccuracy is changed") ;
                            }
                        });
                        //For location listener
                findAndHookMethod(Common.SYSTEM_LOCATION_LISTENER, lpparam.classLoader, "onLocationChanged",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
        }
    }
}
