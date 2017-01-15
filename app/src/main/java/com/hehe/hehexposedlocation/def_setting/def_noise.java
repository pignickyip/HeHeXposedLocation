package com.hehe.hehexposedlocation.def_setting;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.widget.Toast;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.*;

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
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by User on 13/1/2017.
 */

public class def_noise implements IXposedHookLoadPackage {
    String hoho = Common.DEFAULT;
    int sdk = Build.VERSION.SDK_INT;
    Map<String, Object> HAHA;
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        sharedPreferences.makeWorldReadable();
        if(!DefActivity.DEFSETTING.isEmpty()) {
            Map<String, Object> ada = DefActivity.DEFSETTING;
            Iterator GetDATA = ada.keySet().iterator();
            while (GetDATA.hasNext()) {
                String key = (String) GetDATA.next();
                String value = (String) ada.get(key);
                if(!(Objects.equals(valueToStringOrEmpty(HAHA, key), ""))){ //the hash map not equal to null
                    if(Objects.equals(value, "android.permission.ACCESS LOCATIONEXTRA_COMMANDS"))
                         HAHA.put(key,value);
                }
            }
        }
        //TODO
        //  https://www.google.com.hk/search?q=how+to+use+the+data+in+hashmap+android&spell=1&sa=X&ved=0ahUKEwjy3e_XuMHRAhWEn5QKHZqmCtcQvwUIGCgA&biw=1451&bih=660
        //http://blog.csdn.net/yzzst/article/details/47659479
        Random rand = new Random(); // random number
        if(sdk > 18) {
            if (!HAHA.isEmpty()) {
                //if(HAHA)
                int range = 1;
                if (!Objects.equals(hoho, "Default") || !Objects.equals(hoho, "Customer"))
                    if (Objects.equals(hoho, "Low"))
                        range = 2;
                    else if (Objects.equals(hoho, "Medium"))
                        range = 3;
                    else if (Objects.equals(hoho, "Highest"))
                        range = 4;
                float minX = 50.0f / range;
                float maxX = 100.0f / range;
                final float hehe = rand.nextFloat() * (maxX - minX) % (float) range;
                findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getAccuracy",
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                XposedBridge.log("Here im");
                            }
                            @Override //TODO! Maybe the noise overlap
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                String packageName = AndroidAppHelper.currentPackageName();
                                String yama = (String) HAHA.get(packageName);
                                if(!Objects.equals(yama, ""))
                                    param.setResult((float) hehe);
                                XposedBridge.log("Loaded app: " + packageName + " -  " + hehe + " getAccuracy is changed in def_noise - " + yama );
                            }
                        });
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
