package com.hehe.hehexposedlocation;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.hehe.hehexposedlocation.BuildConfig;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;

import static android.R.attr.max;
import static com.hehe.hehexposedlocation.appsettings.XposedMod.prefs;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class OverrideSettingsSecure implements IXposedHookLoadPackage {
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        sharedPreferences.makeWorldReadable();
        int sdk = Build.VERSION.SDK_INT;
        //http://blog.csdn.net/yzzst/article/details/47659479
        Random rand = new Random(); // random number
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
                String packageName = AndroidAppHelper.currentPackageName();
                //get NaN
                int packageNOISE = prefs.getInt(packageName + com.hehe.hehexposedlocation.appsettings.Common.PREF_NOISE,
                        prefs.getInt(com.hehe.hehexposedlocation.appsettings.Common.PREF_DEFAULT + com.hehe.hehexposedlocation.appsettings.Common.PREF_NOISE, 0));
                if(packageNOISE != R.id.txtNoise && R.id.txtNoise != 0)
                    packageNOISE = R.id.txtNoise;
                double a = packageNOISE/Build.VERSION.SDK_INT;
                double r = Math.random() % packageNOISE - (1.0);
                final double haha = randdouble(a , r);
                final float hoho =  rand.nextFloat() * ((float)a - (float)r ) + (float)r ;
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "isFromMockProvider",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                //param.setResult(false);
                                param.setResult(true);
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " isFromMockProvider is changed");
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLatitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((double)haha/((Math.random()%10)));
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " getLatitude is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLongitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((double)haha/(Math.random()%5));
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " getLongitude is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult((float)hoho);
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  "  + hoho+ " getAccuracy is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAltitude",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(true);
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " hasAltitude is changed") ;
                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "hasAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(true);
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  " + haha+ " hasAccuracy is changed") ;
                            }
                        });
                        //For location listener
                findAndHookMethod(Common.SYSTEM_LOCATION_LISTENER, lpparam.classLoader, "onLocationChanged",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                double latitude = haha;
                                double longitude = (-1)*haha;
                                float accuracy = (float)hoho;
                                XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION_LISTENER +  " Value: " + latitude +", " + longitude + "\n Accuracy: " +accuracy) ;

                            }
                        });
            }
        }
    }
    private int mod(double x, int y)
    {
        int result = (int)x % y;
        if (result < 0)
            result += y;
        return result;
    }
    private static int randInt(int min, int max) {
        Random rand  = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    private static double randdouble(double ha, double he){
        double r = Math.random();
        if (r < 0.5) {
            return ((1 - Math.random()) * (ha - he) + he);
        }
        return (Math.random() * (ha - he) + he);
    }
}
