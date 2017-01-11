package com.hehe.hehexposedlocation.appsettings;


import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.removeAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.setDoubleField;
import static de.robv.android.xposed.XposedHelpers.setFloatField;
import static de.robv.android.xposed.XposedHelpers.setIntField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AndroidAppHelper;
import android.app.Notification;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XResources;
import android.content.res.XResources.DimensionReplacement;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioTrack;
import android.media.JetPlayer;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewConfiguration;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.appsettings.hooks.Activities;
import com.hehe.hehexposedlocation.appsettings.hooks.PackagePermissions;

public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage {

	public static final String this_package = XposedMod.class.getPackage().getName();

	//private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String SYSTEMUI_PACKAGE = com.hehe.hehexposedlocation.Common.SYSTEM_LOCATION;

	public static XSharedPreferences prefs;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		loadPrefs();
        //adjustSystemDimensions();
        adjustSystemData();
        // Hook to override DPI (globally, including resource load + rendering)
		try{
			findAndHookMethod(Location.class, "getLatitude", double.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String packageName = AndroidAppHelper.currentPackageName();
                    if (!isActive(packageName)) {
                        // No overrides for this package
                        return;
                    }
                    int packageNOISE = prefs.getInt(packageName + Common.PREF_NOISE,
                            prefs.getInt(Common.PREF_DEFAULT + Common.PREF_NOISE, 0));
                    if (packageNOISE > 0) {
                        // Density for this package is overridden, change density
                        setDoubleField(param.thisObject, "getLatitude", (double) 34.33 );//packageNOISE / 1.0);
                    }
                    XposedBridge.log("Test1123");
                }
            });
            findAndHookMethod(Location.class, "getLongitude", double.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String packageName = AndroidAppHelper.currentPackageName();
                    if (!isActive(packageName)) {
                        // No overrides for this package
                        return;
                    }
                    int packageNOISE = prefs.getInt(packageName + Common.PREF_NOISE,
                            prefs.getInt(Common.PREF_DEFAULT + Common.PREF_NOISE, 0));
                    int hah = R.id.txtNoise;
                    if(packageNOISE != hah && hah != 0)
                        packageNOISE = hah;
                    if (packageNOISE > 0) {
                        // Density for this package is overridden, change density
                        setDoubleField(param.thisObject, "getLongitude", (double) 34.33 );//packageNOISE / 2.5);
                    }
                    XposedBridge.log("Test1144");
                }
            });
		}catch (Throwable t) {
			XposedBridge.log(t);
		}
		PackagePermissions.initHooks();
		Activities.hookActivitySettings();
	}


	@SuppressLint("NewApi")
	private void setConfigurationLocale(Configuration config, Locale loc) {
		config.locale = loc;
		if (Build.VERSION.SDK_INT >= 17) {
			// Don't use setLocale() in order not to trigger userSetLocale
			config.setLayoutDirection(loc);
		}
	}


	/** Adjust all framework dimensions that should reflect
	 *  changes related with SystemUI, namely statusbar and navbar sizes.
	 *  The values are adjusted and replaced system-wide by fixed px values.
	 */
	private void adjustSystemData() {
		if (!isActive(SYSTEMUI_PACKAGE))
			return;
		int test = prefs.getInt(SYSTEMUI_PACKAGE + Common.PREF_NOISE,
				prefs.getInt(Common.PREF_DEFAULT + Common.PREF_NOISE, 0));
		if (test <= 0)
			return;

	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		prefs.reload();

		// Override the default Locale if one is defined (not res-related, here)
		if (isActive(lpparam.packageName)) {
			Locale packageLocale = getPackageSpecificLocale(lpparam.packageName);
			if (packageLocale != null)
				Locale.setDefault(packageLocale);
		}

		if (this_package.equals(lpparam.packageName)) {
			findAndHookMethod("com.hehe.hehexposedlocation.appsettings.XposedModActivity",
					lpparam.classLoader, "isModActive", XC_MethodReplacement.returnConstant(true));
		}


	}

	private static Locale getPackageSpecificLocale(String packageName) {
		String locale = prefs.getString(packageName + Common.PREF_LOCALE, null);
		if (locale == null || locale.isEmpty())
			return null;

		String[] localeParts = locale.split("_", 3);
		String language = localeParts[0];
		String region = (localeParts.length >= 2) ? localeParts[1] : "";
		String variant = (localeParts.length >= 3) ? localeParts[2] : "";
		return new Locale(language, region, variant);
	}


	public static void loadPrefs() {
		prefs = new XSharedPreferences(Common.MY_PACKAGE_NAME, Common.PREFS);
		prefs.makeWorldReadable();
	}

	public static boolean isActive(String packageName) {
		return prefs.getBoolean(packageName + Common.PREF_ACTIVE, false);
	}

	public static boolean isActive(String packageName, String sub) {
		return prefs.getBoolean(packageName + Common.PREF_ACTIVE, false) && prefs.getBoolean(packageName + sub, false);
	}
}
