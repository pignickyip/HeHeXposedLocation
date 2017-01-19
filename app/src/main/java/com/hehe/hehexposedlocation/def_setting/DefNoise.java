package com.hehe.hehexposedlocation.def_setting;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.location.GpsStatus;
import android.os.Build;
import android.widget.Toast;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import static com.hehe.hehexposedlocation.def_setting.DefActivity.DEFSETTING;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by User on 13/1/2017.
 */

public class DefNoise implements IXposedHookLoadPackage {
    String hoho = Common.DEFAULT;
    int sdk = Build.VERSION.SDK_INT;
    private static Map<String, String> HAHA  = new HashMap<String, String>();
    private static SharedPreferences SAVE_NAME = null;
    List<ResolveInfo> pkgAppsList;
    final static double MaxLat= -90.0;
    final static double MinLat = 90.0;
    final static double MaxLong = 180.0;
    final static double MinLong = -180.0;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        sharedPreferences.makeWorldReadable();
        //pkgAppsList = DefActivity.pkgAppsList;
        try{
            HAHA = DEFSETTING;
            if(HAHA == null){
                XposedBridge.log("HERE the HAHA is empty");
            }
        }
        catch(Exception e){
            XposedBridge.log(e);
        }
        //  https://www.google.com.hk/search?q=how+to+use+the+data+in+hashmap+android&spell=1&sa=X&ved=0ahUKEwjy3e_XuMHRAhWEn5QKHZqmCtcQvwUIGCgA&biw=1451&bih=660
        //http://blog.csdn.net/yzzst/article/details/47659479
        Random rand = new Random(); // random number
        if(sdk > 18) {
            try{
            if (true){//!HAHA.isEmpty()) {
                //Latitudes range from -90 to 90.
               // Longitudes range from -180 to 180.
                int adapter = 1;
                if (!Objects.equals(hoho, "Default") || !Objects.equals(hoho, "Customer"))
                    if (Objects.equals(hoho, "Low"))
                        adapter = 2;
                    else if (Objects.equals(hoho, "Medium"))
                        adapter = 3;
                    else if (Objects.equals(hoho, "Highest"))
                        adapter = 4;

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
                                        /(range*range))*1000/1000
                                );

                                String packageName = AndroidAppHelper.currentPackageName();
                                String test = HAHA.get(packageName);
                                try{
                                    double ori = (double)param.getResult();
                                    if(!(Objects.equals(test, "")) || !(Objects.equals(test,null))) {//TODO MAKE the Category
                                        double result = ori+RanLat;
                                        param.setResult(result);
                                        XposedBridge.log("Loaded app: " + packageName + " -  " + result + " getLatitudes is changed in def_noise - " +test );
                                    }
                                    else {
                                        param.setResult(ori);
                                        XposedBridge.log("Failed to change.");
                                    }
                                }
                                catch (Exception e){
                                    XposedBridge.log("Failed" + e );
                                }

                            }
                        });
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getLongitude", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Random rand = new Random();
                        double RanLat = Math.floor( //Original value + random value
                                ((rand.nextDouble() % (MaxLong))
                                        /(range*range))*1000/1000
                        );
                        String packageName = AndroidAppHelper.currentPackageName();
                        String test = HAHA.get(packageName);
                        try{
                            double ori = (double)param.getResult();
                            if(!(Objects.equals(test, "")) || !(Objects.equals(test,null))) {//TODO MAKE the Category
                                double result = ori+RanLat;
                                param.setResult(result);
                                XposedBridge.log("DLLM" + hoho); //TODO CHECK
                            }
                            else {
                                param.setResult(ori);
                                XposedBridge.log("Failed to change.");
                            }
                        }
                        catch (Exception e){
                            XposedBridge.log("Failed" + e );
                        }

                    }
                });
                //TODO http://www.jianshu.com/p/796e94d8af31
                findAndHookMethod(Common.SYSTEM_LOCATION_MANGER, lpparam.classLoader,
                        "getGpsStatus", GpsStatus.class, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                GpsStatus gss = (GpsStatus) param.getResult();
                                if (gss == null)
                                    return;
                                Class<?> clazz = GpsStatus.class;
                                Method m = null;
                                for (Method method : clazz.getDeclaredMethods()) {
                                    if (method.getName().equals("setStatus")) {
                                        if (method.getParameterTypes().length > 1) {
                                            m = method;
                                            break;
                                        }
                                    }
                                }
                                if (m == null)
                                    return;

                                //access the private setStatus function of GpsStatus
                                m.setAccessible(true);

                                //make the apps belive GPS works fine now
                                int svCount = 5;
                                int[] prns = {1, 2, 3, 4, 5};
                                float[] snrs = {0, 0, 0, 0, 0};
                                float[] elevations = {0, 0, 0, 0, 0};
                                float[] azimuths = {0, 0, 0, 0, 0};
                                int ephemerisMask = 0x1f;
                                int almanacMask = 0x1f;

                                //5 satellites are fixed
                                int usedInFixMask = 0x1f;

                                XposedHelpers.callMethod(gss, "setStatus", svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                                param.args[0] = gss;
                                param.setResult(gss);
                                try {
                                    m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                                    param.setResult(gss);
                                } catch (Exception e) {
                                    XposedBridge.log(e);
                                }
                            }
                        });
                }
                else
                     XposedBridge.log("GPS status " );
            }
            catch (Exception e) {
                XposedBridge.log("Wrong here" );
            }
        }
    }
    private String valueToStringOrEmpty(Map<String, ?> map, String key) {
        Object value = map.get(key);
        return value == null ? "" : value.toString();
    }
    private float checkPermission(){//TODO copy the code from OverrideSettingSecure
        String packageName = AndroidAppHelper.currentPackageName();

        return (float) 0.0;
    }
    @SuppressLint("DefaultLocale")
    private boolean FindPerrmission(String pa){
        return false;
    }

}
