package com.hehe.hehexposedlocation.def_setting;

import android.os.Build;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.*;

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

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final XSharedPreferences sharedPreferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_FILE);
        sharedPreferences.makeWorldReadable();
        int sdk = Build.VERSION.SDK_INT;
        String hoho = Common.DEFAULT;
        //http://blog.csdn.net/yzzst/article/details/47659479
        Random rand = new Random(); // random number
        if(sdk > 18){
            int range = 1 ;
            if(!Objects.equals(hoho, "Default") || !Objects.equals(hoho, "Customer"))
                if(Objects.equals(hoho, "Low"))
                    range = 2;
                else if(Objects.equals(hoho, "Medium"))
                    range = 3;
                else if (Objects.equals(hoho, "Highest"))
                    range = 4;
            float minX = 50.0f/range;
            float maxX = 100.0f/range;
            final float hehe =  rand.nextFloat() * (maxX - minX) % (float)range;
            findAndHookMethod(Common.SYSTEM_LOCATION, lpparam.classLoader, "getAccuracy",
                    new XC_MethodHook() {
                        @Override //TODO! Maybe the noise overlap
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult((float)hehe);
                            XposedBridge.log("Loaded app: " + Common.SYSTEM_LOCATION + " -  "  + hehe + " getAccuracy is changed in def_noise") ;
                        }
                    });
        }
    }
}
