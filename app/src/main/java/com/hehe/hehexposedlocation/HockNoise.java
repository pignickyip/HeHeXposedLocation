package com.hehe.hehexposedlocation;

import android.app.AndroidAppHelper;
import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.hehe.hehexposedlocation.FreeList.FREE_CATEGORY_LIST;
import static com.hehe.hehexposedlocation.FreeList.KEYWORD_LIST;
import static com.hehe.hehexposedlocation.FreeList.PACKAGE_LIST;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findConstructorExact;

/**
 * The Spinner noise setting
 */
public class HockNoise implements IXposedHookLoadPackage {

    private final static int sdk = Build.VERSION.SDK_INT;
    private final static double MaxLat = -90.0;
    private final static double MinLat = 90.0;
    private final static double MaxLong = 180.0;
    private final static double MinLong = -180.0;
    private final static String[] FreePackageList = PACKAGE_LIST;
    private final static String[] FreeKeywordList = KEYWORD_LIST;
    private final static String[] FreeCategoryList = FREE_CATEGORY_LIST;
    private final List<String> WhiteListappList = new ArrayList<String>();
    private final List<String> UserpkgName = new ArrayList<String>();
    private final List<String> SyspkgName = new ArrayList<String>();
    private final Hashtable<String, String> WebContent = new Hashtable<String, String>();
    private final Hashtable<String, Double> ApplicationRate = new Hashtable<String, Double>();
    private final Hashtable<String, Integer> ApplicationRateCount = new Hashtable<String, Integer>();
    private final Hashtable<String, Integer> ApplicationNumberDownoad = new Hashtable<String, Integer>();
    private final HashMap<String, String> Record = new HashMap<String, String>();
    private final List<String> RunningAppsList = new ArrayList<String>();
    private String OnRunningFrontgroundApplication;
    private boolean whiteListEnable;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //http://api.xposed.info/reference/de/robv/android/xposed/XSharedPreferences.
        //White List
        final XSharedPreferences WHITELIST = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.WHITELIST_HEHEXPOSED_KEY);
        //Setting
        final XSharedPreferences USERSPINNERCHOICE = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.DEFAULT_HEHEXPOSED_KEY);
        final XSharedPreferences USERNOISECUSTOMER = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.DEFAULT_HEHEXPOSED_KEY);
        //Application reset
        final XSharedPreferences USERAPPLICATIONFILE = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.USER_PACKET_NAME);
        final XSharedPreferences SYSTEMAPPLICATIONFILE = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.SYSTEM_PACKET_NAME);
        final XSharedPreferences WEBCONTENT = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.WEB_CONTENT);
        //Feedback
        final XSharedPreferences FEEDBACK = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.FEEDBACK_COMFORTABLE);
        //Mode
        final XSharedPreferences MODE_WORK = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.MODE_WORK_SETUP);
        final XSharedPreferences MODE_REST = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.MODE_REST_SETUP);
        //Background Service
        final XSharedPreferences RUNNINGAPPSLISTENER = new XSharedPreferences(BuildConfig.APPLICATION_ID, Common.BGDFGDRECORDKEY);

        WHITELIST.makeWorldReadable();
        USERSPINNERCHOICE.makeWorldReadable();
        USERNOISECUSTOMER.makeWorldReadable();
        USERAPPLICATIONFILE.makeWorldReadable();
        SYSTEMAPPLICATIONFILE.makeWorldReadable();
        WEBCONTENT.makeWorldReadable();
        FEEDBACK.makeWorldReadable();
        MODE_WORK.makeWorldReadable();
        MODE_REST.makeWorldReadable();
        RUNNINGAPPSLISTENER.makeWorldReadable();


        WhiteListappList.clear();
        whiteListEnable = WHITELIST.getBoolean(Common.WHITELIST_ENABLE_KEY, false);
        if (whiteListEnable) {
            WhiteListappList.addAll(WHITELIST.getStringSet(Common.WHITELIST_APPS_LIST_KEY, new HashSet<String>()));
            Collections.sort(WhiteListappList);
            XposedBridge.log("White List is enabled");
        }

        //Category
        UserpkgName.clear();
        UserpkgName.addAll(USERAPPLICATIONFILE.getStringSet(Common.USER_PACKET_NAME_KEY, new HashSet<String>()));
        Collections.sort(UserpkgName);
        SyspkgName.clear();
        SyspkgName.addAll(SYSTEMAPPLICATIONFILE.getStringSet(Common.SYSTEM_PACKET_NAME_KEY, new HashSet<String>()));
        Collections.sort(SyspkgName);

        final List<String> adapterWeb = new ArrayList<String>();
        adapterWeb.clear();
        adapterWeb.addAll(WEBCONTENT.getStringSet(Common.WEB_CONTENT_KEY, new HashSet<String>()));
        Collections.sort(adapterWeb);
        if (!(WebContent.size() == adapterWeb.size()))
            WebContent.clear();
        for (String Web : adapterWeb) {
            boolean check = false;
            for (String User : UserpkgName) {
                if (Web.startsWith(User)) {
                    WebContent.put(User, Web.substring(User.length()));
                    check = true;
                }
            }
            if (check) {
                continue;
            }
            for (String System : SyspkgName) {
                if (Web.startsWith(System)) {
                    WebContent.put(System, Web.substring(System.length()));
                    break;
                }
            }
        }

        adapterWeb.clear();
        adapterWeb.addAll(WEBCONTENT.getStringSet(Common.WEB_CONTENT_RATE, new HashSet<String>()));
        Collections.sort(adapterWeb);
        if (!(ApplicationRate.size() == adapterWeb.size()))
            ApplicationRate.clear();
        for (String Web : adapterWeb) {
            Boolean next = true;
            for (String User : UserpkgName) {
                if (Web.startsWith(User)) {
                    String temp = Web.substring(User.length());
                    Double RateClassified = ClassTheRate(temp);
                    ApplicationRate.put(User, RateClassified);
                    next = false;
                    break;
                }
            }
            if (next) {
                for (String System : SyspkgName) {
                    if (Web.startsWith(System)) {
                        String temp = Web.substring(System.length());
                        Double RateClassified = ClassTheRate(temp);
                        ApplicationRate.put(System, RateClassified);
                        break;
                    }
                }
            }
        }

        adapterWeb.clear();
        adapterWeb.addAll(WEBCONTENT.getStringSet(Common.WEB_CONTENT_RATE_COUNT, new HashSet<String>()));
        Collections.sort(adapterWeb);
        if (!(ApplicationRateCount.size() == adapterWeb.size()))
            ApplicationRateCount.clear();
        for (String Web : adapterWeb) {
            Boolean next = true;
            for (String User : UserpkgName) {
                if (Web.startsWith(User)) {
                    String temp = Web.substring(User.length());
                    Integer Lenth = temp.length();
                    ApplicationRateCount.put(User, Lenth);
                    next = false;
                    break;
                }
            }
            if (next) {
                for (String System : SyspkgName) {
                    if (Web.startsWith(System)) {
                        String temp = Web.substring(System.length());
                        Integer Lenth = temp.length();
                        ApplicationRateCount.put(System, Lenth);
                        break;
                    }
                }
            }
        }
        adapterWeb.clear();
        adapterWeb.addAll(WEBCONTENT.getStringSet(Common.WEB_CONTENT_NUMDOWNLOAD, new HashSet<String>()));
        Collections.sort(adapterWeb);
        if (!(ApplicationNumberDownoad.size() == adapterWeb.size()))
            ApplicationNumberDownoad.clear();
        for (String Web : adapterWeb) {
            Boolean next = true;
            for (String User : UserpkgName) {
                if (Web.startsWith(User)) {
                    String temp = Web.substring(User.length());
                    Integer ho = temp.length();
                    ApplicationNumberDownoad.put(User, ho);
                    next = false;
                    break;
                }
            }
            if (next) {
                for (String System : SyspkgName) {
                    if (Web.startsWith(System)) {
                        String temp = Web.substring(System.length());
                        Integer ho = temp.length();
                        ApplicationNumberDownoad.put(System, ho);
                        break;
                    }
                }
            }
        }
        //Mode value
        double TimeModeCheck = -1;
        Calendar mcurrentTime = Calendar.getInstance();
        int Curr_Hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int Curr_Minute = mcurrentTime.get(Calendar.MINUTE);
        //Mode change
        final boolean WorkMode_ON = MODE_WORK.getBoolean(Common.MODE_WORK_SETUP_KEY, false);
        final int WorkMode_Start_Hour = MODE_WORK.getInt(Common.MODE_WORK_SETUP_STARTTIME_KEY_HOUR, -1);
        final int WorkMode_Start_Mintues = MODE_WORK.getInt(Common.MODE_WORK_SETUP_STARTTIME_KEY_MINUTES, -1);
        final int WorkMode_End_Hour = MODE_WORK.getInt(Common.MODE_WORK_SETUP_ENDTIME_KEY_HOUR, -1);
        final int WorkMode_End_Mintues = MODE_WORK.getInt(Common.MODE_WORK_SETUP_ENDTIME_KEY_MINUTES, -1);
        if (WorkMode_ON) {
            if (WorkMode_Start_Hour <= Curr_Hour) {
                if (WorkMode_Start_Mintues <= Curr_Minute) {
                    if (WorkMode_Start_Hour != -1)
                        TimeModeCheck = 0.9;
                    XposedBridge.log("Work mode on Time");
                }
            } else if (WorkMode_End_Hour <= Curr_Hour) {
                if (WorkMode_End_Mintues < Curr_Minute) {
                    TimeModeCheck = 1;
                    XposedBridge.log("Current " + Curr_Hour + ":" + Curr_Minute);
                }
            }
            XposedBridge.log("Work mode ON");
        } else
            TimeModeCheck = WorkMode_Start_Hour;

        Boolean RestMode_ON = MODE_REST.getBoolean(Common.MODE_REST_SETUP_KEY, false);
        final int RestMode_Start_Hour = MODE_REST.getInt(Common.MODE_REST_SETUP_STARTTIME_KEY_HOUR, -1);
        final int RestMode_Start_Mintues = MODE_REST.getInt(Common.MODE_REST_SETUP_STARTTIME_KEY_MINUTES, -1);
        final int RestMode_End_Hour = MODE_REST.getInt(Common.MODE_REST_SETUP_ENDTIME_KEY_HOUR, -1);
        final int RestMode_End_Mintues = MODE_REST.getInt(Common.MODE_REST_SETUP_ENDTIME_KEY_MINUTES, -1);
        if (RestMode_ON) {
            if (RestMode_Start_Hour <= Curr_Hour) {
                if (RestMode_Start_Mintues <= Curr_Minute) {
                    TimeModeCheck = 1.1;
                    XposedBridge.log("Rest mode on time");
                }
            } else if (RestMode_End_Hour <= Curr_Hour) {
                if (RestMode_End_Mintues < Curr_Minute) {
                    TimeModeCheck = 1;
                }
            }
            XposedBridge.log("Rest mode ON");
        }
        final boolean serviceEnable = RUNNINGAPPSLISTENER.getBoolean(Common.BGDFGDRECORDKEY_SERVICE_ENABLE, false);
        if (serviceEnable) {
            RunningAppsList.clear();
            RunningAppsList.addAll(RUNNINGAPPSLISTENER.getStringSet(Common.BGDFGDRUNNINGAPPLICATION, new HashSet<String>()));
            Collections.sort(RunningAppsList);
            OnRunningFrontgroundApplication = RUNNINGAPPSLISTENER.getString(Common.CURRENTAPPLICATION, "No Application Running");
            XposedBridge.log("Running application listener");
        }
        //https://www.google.com.hk/search?q=how+to+use+the+data+in+hashmap+android&spell=1&sa=X&ved=0ahUKEwjy3e_XuMHRAhWEn5QKHZqmCtcQvwUIGCgA&biw=1451&bih=660
        //http://blog.csdn.net/yzzst/article/details/47659479
        if (sdk > 18) {
            try {
                Random rand = new Random(sdk);
                int omg = USERSPINNERCHOICE.getInt(Common.SHARED_PREFERENCES_DEFAULT_POSITION, 0);
                // Latitudes range from -90 to 90.
                // Longitudes range from -180 to 180.
                int adapter = 1;
                switch (omg){
                    case 0:
                        break;
                    case 1:{//Customer
                        adapter = USERNOISECUSTOMER.getInt(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER, 40);
                        XposedBridge.log("The User chose Customer and the value is " + adapter);

                        if (adapter <= 40) {
                            adapter = 20;
                        }
                        adapter %= 79;

                        adapter++;

                        if (sdk >= 21)
                            adapter = ThreadLocalRandom.current().nextInt(1, adapter);
                        else
                            adapter = ((rand.nextInt(adapter))) + 1;
                        break;
                    }
                    case 2:{
                        if (sdk >= 21)
                            adapter = ThreadLocalRandom.current().nextInt(1, 20) + 1;
                        else
                            adapter = (rand.nextInt(20)) + 1;
                        XposedBridge.log("The User chose Low");
                        break;
                    }
                    case 3:{
                        if (sdk >= 21)
                            adapter = ThreadLocalRandom.current().nextInt(1, 40) + 1;
                        else
                            adapter = (rand.nextInt(40)) + 1;
                        XposedBridge.log("The User chose Medium");
                        break;
                    }
                    case 4:{
                        if (sdk >= 21)
                            adapter = ThreadLocalRandom.current().nextInt(1, 80) + 1;
                        else
                            adapter = (rand.nextInt(80)) + 1;
                        XposedBridge.log("The User chose High");
                        break;
                    }
                    default:
                        XposedBridge.log("Choice Error");
                        break;
                }

                int FeedbackValue = 1;
                String Feedback_choice = FEEDBACK.getString(Common.FEEDBACK_COMFORTABLE_KEY, " ");
                if (Objects.equals(Feedback_choice, "Strong")) {
                    if(adapter > 1)
                        FeedbackValue = adapter / 2;
                    else
                        FeedbackValue = adapter;
                } else if (Objects.equals(Feedback_choice, "Week")) {
                    FeedbackValue = adapter * 2;
                } else if (Objects.equals(Feedback_choice, "Suitable")) {
                    FeedbackValue = adapter;
                } else if (Objects.equals(Feedback_choice, " ")) {
                    FeedbackValue = adapter;
                }
                if(FeedbackValue >1) {
                    XposedBridge.log("The feedback system get " + Feedback_choice + " - " + FeedbackValue);
            }
                final int range = FeedbackValue;
            /*
            Source file of android location api
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/Location.java
            //https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/location/java/android/location/LocationManager.java
            */
                if (omg != 0) {
                    final double modeChange = TimeModeCheck;
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
                                    String packageName = AndroidAppHelper.currentPackageName();
                                    String CurrpackageName = lpparam.packageName;
                                    double popular = SearchInFromWebContent(CurrpackageName);
                                    //Original value + random value
                                    double ha = (rand.nextDouble() % (MaxLat));
                                    double he = (rand.nextDouble() % (range * popular)) % 0.1 * modeChange;
                                    double RanLat =
                                            BigDecimal.valueOf(ha % he)
                                                    .setScale(5, RoundingMode.HALF_UP)
                                                    .doubleValue();
                                    if (serviceEnable) {
                                        XposedBridge.log("Running application service enable");
                                        for (String heee : RunningAppsList){
                                            if (heee.startsWith(packageName)) {
                                                RanLat = RunningApplicationPlusNoise(RanLat, heee.startsWith(CurrpackageName));
                                                RanLat = FroundApplication(RanLat, OnRunningFrontgroundApplication.startsWith(CurrpackageName));
                                                XposedBridge.log("Running application = Requesting application");
                                            } else if(heee.startsWith(CurrpackageName)){
                                                RanLat = RunningApplicationPlusNoise(RanLat, heee.startsWith(packageName));
                                                RanLat = FroundApplication(RanLat, OnRunningFrontgroundApplication.startsWith(packageName));
                                                XposedBridge.log("Running application = Requesting application");
                                            }
                                            else if(heee.startsWith("android.process.acore")){
                                                RanLat = RunningApplicationPlusNoise(RanLat, heee.startsWith(packageName));
                                                RanLat = FroundApplication(RanLat, OnRunningFrontgroundApplication.startsWith(packageName));
                                                XposedBridge.log("Running application = Requesting application");
                                            }
                                            else if(heee.startsWith("com.google.android.gms.persistent")){
                                                RanLat = RunningApplicationPlusNoise(RanLat, heee.startsWith(packageName));
                                                RanLat = FroundApplication(RanLat, OnRunningFrontgroundApplication.startsWith(packageName));
                                                XposedBridge.log("Running application = Requesting application");
                                            }
                                        }
                                    }
                                    String ho123 = Record.get(packageName);
                                    String ha123 = Record.get(CurrpackageName);
                                    try {
                                        double ori = (double) param.getResult();//get the original result
                                        //TODO escape
                                        String Category_1 = WebContent.get(packageName);
                                        String Category_2 = WebContent.get(CurrpackageName);
                                        //Check the package in free list -> created by admin
                                        if (Arrays.asList(FreeCategoryList).contains(Category_1)) {
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location cause the category is " + Category_1);
                                            if (Objects.equals(ho123, ha123))
                                                Record.put(ho123, "Original");
                                        } else if (Arrays.asList(FreeCategoryList).contains(Category_2)) {
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location cause the category is " + Category_2);
                                        } else if (Arrays.asList(FreePackageList).contains(packageName) || Arrays.asList(FreePackageList).contains(CurrpackageName)) {
                                            /*double ra = BigDecimal.valueOf(rand.nextDouble() % 0.1)
                                                            .setScale(5, RoundingMode.HALF_UP)
                                                            .doubleValue();*/
                                            double ra = BigDecimal.valueOf(RanLat * 0.9998).setScale(7, RoundingMode.HALF_UP).doubleValue();
                                            ra = MakeItNegOrPost(ra, range) / 10000;
                                            ra += ori;
                                            param.setResult(ra);
                                            XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra);
                                        }
                                        //within white list
                                        else if ((WhiteListappList.contains(packageName)) || (WhiteListappList.contains(CurrpackageName))) {
                                            param.setResult(ori);
                                            XposedBridge.log(packageName + " needs the accuracy location cause it listed in white list");
                                        } else if (Objects.equals(ha123, "Original")) {
                                            param.setResult(ori);
                                        } else {
                                            //Match apart of
                                            for (String List_keyword : FreeKeywordList) {
                                                if (packageName.startsWith(List_keyword)) {
                                                    double result = ori + MakeItNegOrPost(RanLat, range);
                                                    param.setResult(result);
                                                    XposedBridge.log(packageName + " get the Latitude " + result);
                                                } else if (RunningAppsList.contains(CurrpackageName) || RunningAppsList.contains(packageName)) {
                                                    double result = ori + (MakeItNegOrPost(RanLat, range) * 1.00000001);
                                                    if (OnRunningFrontgroundApplication.startsWith(packageName)
                                                            || OnRunningFrontgroundApplication.startsWith(CurrpackageName)) {
                                                        result *= 1.00000005;
                                                    }
                                                    param.setResult(result);
                                                    XposedBridge.log("The running application " + packageName + " get the Latitude " + result);
                                                } else {
                                                    double result = ori + (MakeItNegOrPost(RanLat, range) * 1.0000001);
                                                    param.setResult(result);
                                                    XposedBridge.log(packageName + " get the Latitude " + result);
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
                            String packageName = AndroidAppHelper.currentPackageName();
                            String CurrpackageName = lpparam.packageName;
                            //Original value + random value
                            double ha = (rand.nextDouble() % (MaxLong));
                            double popular = SearchInFromWebContent(CurrpackageName);
                            double he = (rand.nextDouble() % (range * popular)) % 0.1 * modeChange;
                            double RanLong =
                                    BigDecimal.valueOf(ha % he)
                                            .setScale(5, RoundingMode.HALF_UP)
                                            .doubleValue();
                            if(serviceEnable) {
                                XposedBridge.log("Running application service enable");
                                for (String heee : RunningAppsList) {
                                    if (heee.startsWith(packageName)) {
                                        RanLong = RunningApplicationPlusNoise(RanLong, heee.startsWith(CurrpackageName));
                                        RanLong = FroundApplication(RanLong, OnRunningFrontgroundApplication.startsWith(CurrpackageName));
                                        XposedBridge.log("Running application = Requesting application");
                                    } else if (heee.startsWith(CurrpackageName)) {
                                        RanLong = RunningApplicationPlusNoise(RanLong, heee.startsWith(packageName));
                                        RanLong = FroundApplication(RanLong, OnRunningFrontgroundApplication.startsWith(packageName));
                                        XposedBridge.log("Running application = Requesting application");
                                    }
                                    else if(heee.startsWith("android.process.acore")){
                                        RanLong = RunningApplicationPlusNoise(RanLong, heee.startsWith(packageName));
                                        RanLong = FroundApplication(RanLong, OnRunningFrontgroundApplication.startsWith(packageName));
                                        XposedBridge.log("Running application = Requesting application");
                                    }
                                    else if(heee.startsWith("com.google.android.gms.persistent")){
                                        RanLong = RunningApplicationPlusNoise(RanLong, heee.startsWith(packageName));
                                        RanLong = FroundApplication(RanLong, OnRunningFrontgroundApplication.startsWith(packageName));
                                        XposedBridge.log("Running application = Requesting application");
                                    }
                                }
                            }
                            try {
                                double ori = (double) param.getResult();//get the original result
                                //TODO escape
                                String Category_1 = WebContent.get(packageName);
                                String Category_2 = WebContent.get(CurrpackageName);
                                //Check the package in free list -> created by admin
                                if (Arrays.asList(FreeCategoryList).contains(Category_1)) {
                                    param.setResult(ori);
                                    XposedBridge.log(packageName + " needs the accuracy location cause the category is " + Category_1);
                                } else if (Arrays.asList(FreeCategoryList).contains(Category_2)) {
                                    param.setResult(ori);
                                    XposedBridge.log(packageName + " needs the accuracy location cause the category is " + Category_2);
                                } else if (Arrays.asList(FreePackageList).contains(packageName) || Arrays.asList(FreePackageList).contains(CurrpackageName)) {
                                    /*double ra = BigDecimal.valueOf(rand.nextDouble() % 0.001)
                                            .setScale(5, RoundingMode.HALF_UP)
                                            .doubleValue();*/
                                    double ra = BigDecimal.valueOf(RanLong * 0.9998).setScale(7, RoundingMode.HALF_UP).doubleValue();
                                    ra = MakeItNegOrPost(ra, range) / 10000;
                                    ra += ori;
                                    param.setResult(ra);
                                    XposedBridge.log(CurrpackageName + " needs the seems accuracy location - " + ra);
                                } else if ((WhiteListappList.contains(packageName)) || (WhiteListappList.contains(CurrpackageName))) {
                                    param.setResult(ori);
                                    XposedBridge.log(packageName + " needs the accuracy location cause it listed in whitelist");
                                } else {
                                    //Match apart of
                                    for (String List_keyword : FreeKeywordList) {
                                        if (packageName.startsWith(List_keyword)) {
                                            double result = ori + MakeItNegOrPost(RanLong, range);
                                            param.setResult(result);
                                            XposedBridge.log(packageName + " get the Longitude " + result);
                                        } else if (RunningAppsList.contains(CurrpackageName) || RunningAppsList.contains(packageName)) {
                                            double result = ori + (MakeItNegOrPost(RanLong, range) * 1.00000001);
                                            if (OnRunningFrontgroundApplication.startsWith(packageName)
                                                    || OnRunningFrontgroundApplication.startsWith(CurrpackageName)) {
                                                result *= 1.00000005;
                                            }
                                            param.setResult(result);
                                            XposedBridge.log("The running application " + packageName + " get the Longitude " + result);
                                        } else {
                                            double result = ori + (MakeItNegOrPost(RanLong, range) * 1.0000001);
                                            param.setResult(result);
                                            XposedBridge.log(packageName + " get the Longitude " + result);
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

    private double MakeItNegOrPost(double haha, int range) {
        double change = haha;
        Random rand = new Random(sdk);
        if (rand.nextBoolean())
            change *= (-1);
        return change;
    }

    private Double ClassTheRate(String val) {
        Double hehe = 0.0;
        if (val.startsWith("0")) {
            if (!val.endsWith("0")) {
                hehe = 0.5;
            }
        } else if (val.startsWith("1")) {
            if (!val.endsWith("1")) {
                hehe = 1.5;
            } else {
                hehe = 1.0;
            }
        } else if (val.startsWith("2")) {
            if (!val.endsWith("2")) {
                hehe = 2.5;
            } else {
                hehe = 2.0;
            }
        } else if (val.startsWith("3")) {
            if (!val.endsWith("3")) {
                hehe = 3.5;
            } else {
                hehe = 3.0;
            }
        } else if (val.startsWith("4")) {
            if (!val.endsWith("4")) {
                hehe = 4.5;
            } else {
                hehe = 4.0;
            }
        } else {
            hehe = 5.0;
        }
        return hehe;
    }

    private double SearchInFromWebContent(String CurrpackageName) {
        Double rate = ApplicationRate.get(CurrpackageName);
        Integer ratecount = ApplicationRateCount.get(CurrpackageName);
        Integer numofDownloader_Length = ApplicationNumberDownoad.get(CurrpackageName);

        if (rate == (null))
            return 1.0;
        if (ratecount == (null))
            return 1.0;
        if (numofDownloader_Length == (null))
            return 1.0;
        double hehe = 1.0;
        if (numofDownloader_Length >= 20) { // more or equal to 1 million
            return 0.8;
        } else {
            if (numofDownloader_Length >= 15) { //more than 10 thousand
                if (rate > 2.5) {
                    if (ratecount > 5) { //at least 10 thousand
                        return 0.85;
                    }
                } else if (ratecount > 5) { //more or equal to 1 thousand
                    return 0.9;
                } else {
                    return 0.925;
                }
            } else if (numofDownloader_Length > 12) { //more or equal to 1 thousand
                if (rate > 2.5) {
                    if (ratecount > 3) { //at least 1 hundred
                        return 0.9;
                    } else {
                        return 0.925;
                    }
                } else {
                    return 0.95;
                }
            } else {
                return 0.975;
            }
        }
        return hehe;
    }

    private double RunningApplicationPlusNoise(double noise, boolean runningORNot) {
        if (runningORNot)
            return noise * 0.99999925;
        return noise;
    }

    private double FroundApplication(double noise, boolean runningORNot) {
        if (runningORNot)
            return noise * 0.9999995;
        return noise;
    }
}
