package com.hehe.hehexposedlocation.def_setting;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;

import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
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

import static com.hehe.hehexposedlocation.def_setting.FreeList.APPLICATION_CATEGORY_LIST;
import static com.hehe.hehexposedlocation.def_setting.FreeList.APPLICATION_GAME_CATEGORY_LIST;
import static com.hehe.hehexposedlocation.def_setting.FreeList.KEYWORD_LIST;
import static com.hehe.hehexposedlocation.def_setting.FreeList.PACKAGE_LIST;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorExact;
import static de.robv.android.xposed.XposedHelpers.setDoubleField;

/**
 * The Spinner noise setting
 */
//TODO the sharedPreferences_whitelist need to reset/clear
public class DefNoise implements IXposedHookLoadPackage  {

    private final static int sdk = Build.VERSION.SDK_INT;
    private final static double MaxLat= -90.0;
    private final static double MinLat = 90.0;
    private final static double MaxLong = 180.0;
    private final static double MinLong = -180.0;
    private final static String[] FreePackageList = PACKAGE_LIST;
    private final static String[] FreeKeywordList = KEYWORD_LIST;
    private final static String[] AndroidPlayStoreApplicationCategory = APPLICATION_CATEGORY_LIST;
    private final static String[] AndroidPlayStoreGameCategory = APPLICATION_GAME_CATEGORY_LIST;
    private final List<String> WhiteListappList = new ArrayList<String>();
    private final List<String> UserpkgName = new ArrayList<String>();
    private final List<String> SyspkgName = new ArrayList<String>();
    private final List<String> GetWebContent = new ArrayList<String>();
    private final Hashtable<String,String> WebContent = new Hashtable<String,String>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //http://api.xposed.info/reference/de/robv/android/xposed/XSharedPreferences.html
        //White List
        final XSharedPreferences sharedPreferences_whitelist = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE);
        //Setting
        final XSharedPreferences sharedPreferences_posit = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREFERENCES_POSITION);
        final XSharedPreferences sharedPreferences_customer = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SHARED_PREDERENCES_CUSTOMER);
        //Application reset
        final XSharedPreferences sharedPreferences_UserApplicationFile = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.USER_PACKET_NAME);
        final XSharedPreferences sharedPreferences_SystemApplicationFile = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SYSTEM_PACKET_NAME);
        final XSharedPreferences sharedPreferences_WebContent = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.WEB_CONTENT);
        //Feedback
        final XSharedPreferences sharedPreferences_Feedback = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.FEEDBACK_COMFORTABLE);
        sharedPreferences_whitelist.makeWorldReadable();
        sharedPreferences_posit.makeWorldReadable();
        sharedPreferences_customer.makeWorldReadable();
        sharedPreferences_UserApplicationFile.makeWorldReadable();
        sharedPreferences_SystemApplicationFile.makeWorldReadable();
        sharedPreferences_WebContent.makeWorldReadable();
        sharedPreferences_Feedback.makeWorldReadable();

        WhiteListappList.clear();
        WhiteListappList.addAll(sharedPreferences_whitelist.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(WhiteListappList);

        UserpkgName.clear();
        UserpkgName.addAll(sharedPreferences_UserApplicationFile.getStringSet(Common.USER_PACKET_NAME_KEY, new HashSet<String>()));
        Collections.sort(UserpkgName);

        SyspkgName.clear();
        SyspkgName.addAll(sharedPreferences_SystemApplicationFile.getStringSet(Common.SYSTEM_PACKET_NAME_KEY, new HashSet<String>()));
        Collections.sort(SyspkgName);

        GetWebContent.clear();
        GetWebContent.addAll(sharedPreferences_WebContent.getStringSet(Common.WEB_CONTENT_KEY, new HashSet<String>()));
        Collections.sort(GetWebContent);

        if(!(WebContent.size() == GetWebContent.size()))
            WebContent.clear();
        for (String Web : GetWebContent) {
            Boolean next =true;
            for (String User : UserpkgName) {
                if (Web.startsWith(User)) {
                    WebContent.put(User, Web.substring(User.length()));
                    next = false;
                    break;
                }
            }
            if(next){
                for(String System : SyspkgName){
                    if(Web.startsWith(System)) {
                        WebContent.put(System,Web.substring(System.length()));
                        break;
                    }
                }
            }
        }
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

                int FeedbackValue = 1;
                String Feedback_choice = sharedPreferences_Feedback.getString(Common.FEEDBACK_COMFORTABLE_KEY," ");
                if(Objects.equals(Feedback_choice, "Strong")) {
                    FeedbackValue = adapter / 2;
                }
                else if(Objects.equals(Feedback_choice, "Week")) {
                    FeedbackValue = adapter * 2;
                }
                else if(Objects.equals(Feedback_choice, "Suitable")){
                    FeedbackValue = adapter;
                }else if (Objects.equals(Feedback_choice, " ")){
                    FeedbackValue = adapter;
                }
                final int range = FeedbackValue;
            /*
            Source file of android location api
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/Location.java
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/LocationManager.java
            */
                if (omg != 0) {
                    if (sharedPreferences_whitelist.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true) || WhiteListappList.contains(lpparam.packageName)) {
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
                                    double ha =  (rand.nextDouble() % (MaxLat));
                                    //int he =(rand.nextInt(range)) ;
                                   // he /= 10;
                                    double he = (rand.nextDouble() % (range));
                                    double RanLat = (
                                           ha%he
                                            //(rand.nextDouble() % (MaxLat)) / (rand.nextInt(range)) / 1000
                                    );
                                    XposedBridge.log("ghisd" + RanLat);
                                    String packageName = AndroidAppHelper.currentPackageName();
                                    String CurrpackageName = lpparam.packageName;
                                    try {
                                        double ori = (double) param.getResult();//get the original result
                                        for (String List_pkg : FreePackageList) {
                                            //TODO escape
                                            String Category_1 = WebContent.get(packageName);
                                            String Category_2 = WebContent.get(CurrpackageName);
                                            //Todo not yet done
                                            //Check the package in free list -> created by admin
                                            if(Objects.equals(Category_1, "Travel & Local")) {
                                                param.setResult(ori);
                                                break;
                                            }
                                            else if( Objects.equals(Category_2, "Travel & Local")){
                                                param.setResult(ori);
                                                break;
                                            }
                                            else if (Objects.equals(List_pkg, packageName) || Objects.equals(List_pkg, CurrpackageName) ) {
                                                //within white list
                                                if((WhiteListappList.contains(packageName))||(WhiteListappList.contains(CurrpackageName))){
                                                    param.setResult(ori);
                                                    XposedBridge.log(packageName + " needs the accuracy location" );
                                                    break;
                                                }
                                                else{//if not in white list
                                                    double ra = rand.nextDouble()%0.01;
                                                    XposedBridge.log("hihi"+ra);
                                                    ra += ori;
                                                    ra = MakeItNegOrPost(ra,range);
                                                    param.setResult(ra);
                                                    XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra );
                                                    break;
                                                }

                                            } else {
                                                //Match apart of
                                                for (String List_keyword : FreeKeywordList) {
                                                    if (packageName.startsWith(List_keyword)) {
                                                        double result = ori + RanLat;
                                                        result = MakeItNegOrPost(result,range);
                                                        param.setResult(result);
                                                        XposedBridge.log(packageName + " get the Latitude " + result);
                                                    } else {
                                                        double result = ori + RanLat*2;
                                                        result = MakeItNegOrPost(result,range);
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
                                    //Change the value
                                    (rand.nextDouble() % (MaxLong)) / rand.nextInt(range) * 1000 / 1000
                            );
                            String packageName = AndroidAppHelper.currentPackageName();
                            String CurrpackageName = lpparam.packageName;

                            List<String> appList = new ArrayList<String>();
                            appList.addAll(sharedPreferences_whitelist.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
                            Collections.sort(appList);

                            String Category = WebContent.get(packageName);
                            try {
                                double ori = (double) param.getResult();//get the original result
                                for (String List_pkg : FreePackageList) {
                                    //TODO escape
                                    String Category_1 = WebContent.get(packageName);
                                    String Category_2 = WebContent.get(CurrpackageName);
                                    //Todo not yet done
                                    //Check the package in free list -> created by admin
                                    if(Objects.equals(Category_1, "Travel & Local")) {
                                        param.setResult(ori);
                                        XposedBridge.log(packageName +  Category_1  );
                                        break;
                                    }
                                    else if( Objects.equals(Category_2, "Travel & Local")){
                                        param.setResult(ori);
                                        XposedBridge.log(CurrpackageName + Category_2);
                                        break;
                                    }
                                    else if (Objects.equals(List_pkg, packageName) || (appList.contains(packageName))) {

                                        if(Objects.equals(List_pkg, CurrpackageName) ||(appList.contains(CurrpackageName)) || Objects.equals(Category, "Travel & Local")){
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location" );
                                        }
                                        else{
                                            double ra = rand.nextInt(10) / 100000;
                                            ra += ori;
                                            ra = MakeItNegOrPost(ra,range);
                                            param.setResult(ra);
                                            XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra );
                                        }
                                    } else {
                                        //Match apart of
                                        for (String List_keyword : FreeKeywordList) {
                                            if (packageName.startsWith(List_keyword)) {
                                                double result = ori;
                                                result = MakeItNegOrPost(result,range);
                                                param.setResult(result);
                                                XposedBridge.log(packageName + " get the Longitude " + result);
                                            } else {
                                                double result = ori;
                                                result = MakeItNegOrPost(result,range);
                                                param.setResult(result);
                                                XposedBridge.log(packageName + " get the Longitude " + result);
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                XposedBridge.log("Problem 1 at " + e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                XposedBridge.log("Wrong here");
            }
        }
    }
    public double MakeItNegOrPost(double haha, int range){
        double change = haha;
        Random rand = new Random(range);
        int Mod = rand.nextInt()%2;
        if(Mod == 1)
            change *= (-1);
        return change;
    }
}
