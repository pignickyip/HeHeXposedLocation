package com.hehe.hehexposedlocation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class SettingsActivity extends PreferenceActivity  {
    private GoogleApiClient client;

    SharedPreferences sharedPref;
    String[] menuItems;
    String instructionsString;
    String instructionsTitle;

    private PackageManager pm;

    SharedPreferences clear;
    SharedPreferences.Editor PE;

    public static SharedPreferences UserApplicationFile = null;
    public static SharedPreferences SystemApplicationFile = null;
    public static SharedPreferences WebContent = null;

    final List<String> UserpkgName = new ArrayList<String>();
    final List<String> SyspkgName = new ArrayList<String>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main );
        Resources res = getResources();
        menuItems = res.getStringArray(R.array.menu_array);
        instructionsString = "First, Go to Enable HeHeXposed to choose the setting which specify your needs" + "\n\n"
               + "\n\n"
                 + "\n\n";

        instructionsTitle = res.getString(R.string.instructions_title);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuItems);
        setListAdapter(adapter);

        sharedPref = getSharedPreferences(Common.PREFS_SETTINGS, MODE_WORLD_READABLE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onListItemClick(ListView parent, View v, int position, long id) {
            Intent intent;
            Fragment navFragment = null;
            switch (position) {
                case 0: //about me
                    String aboutMsg = getString ( R.string.app_name ) + ": " + BuildConfig.VERSION_NAME
                            + "\n The Hong Kong Polytechnic University \n Student Final Year Project - 2016 "
                            + "\n Smart Location Obfuscation Module for \n Xposed Framework in Android "
                            + "\n Yip Tim Yan";

                    new AlertDialog.Builder ( this )
                            .setMessage ( aboutMsg )
                            .setTitle ( R.string.about )
                            .setPositiveButton ( R.string.ok, new DialogInterface.OnClickListener () {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel ();
                                }
                            } )
                            .show ();
                    break;
                case 1: //Introduction
                    intent = new Intent(this, com.hehe.hehexposedlocation.intro.MainActivity.class);
                    startActivity(intent);
                    break;
                case 2: //Reference List
                    String[] ref_ar;
                    String ref = getString ( R.string.app_name ) + ": " + '\n'
                            + "Using those module: \n";
                    ref_ar = getResources ().getStringArray ( R.array.ref_list );
                    List<String> ha = Arrays.asList ( ref_ar );
                    new AlertDialog.Builder ( this )
                            .setMessage ( ref + ha )
                            .setTitle ( "Reference List" )
                            .setPositiveButton ( R.string.ok, new DialogInterface.OnClickListener () {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel ();
                                }
                            } )
                            .show ();
                    break;
                case 3: //instructions
                    new AlertDialog.Builder ( this )
                            .setMessage ( instructionsString )
                            .setTitle ( instructionsTitle )
                            .setPositiveButton ( R.string.ok, new DialogInterface.OnClickListener () {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel ();
                                }
                            } )
                            .show ();
                    break;
                case 4: //Default Noise setting
                    intent = new Intent ( this, com.hehe.hehexposedlocation.def_setting.DefActivity.class );
                    startActivity ( intent );
                    break;
                case 5: //White List
                    intent = new Intent ( this, WhitelistActivity.class );
                    startActivity ( intent );
                    break;
                case 6:
                    try {
                        GetFile();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), hoho , Toast.LENGTH_LONG).show();
                    break;
                case 7: //clear all setting
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sure? One Way able");
                    builder.setTitle("Clear All Setting");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Default setting position
                            clear = getSharedPreferences(Common.SHARED_PREFERENCES_POSITION, 0);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();
                            //Customer setting value
                            clear = getSharedPreferences(Common.SHARED_PREDERENCES_CUSTOMER, 0);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();

                            //white list
                            clear = getSharedPreferences(Common.SHARED_WHITELIST_PREFERENCES_FILE, MODE_WORLD_READABLE);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();
                            clear = getSharedPreferences(Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE, MODE_WORLD_READABLE);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();

                            Toast.makeText(getApplicationContext(), "Successfully reset all the setting", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            //SettingsActivity.this.finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    /*boolean debugPref = sharedPref.getBoolean(Common.DEBUG_KEY, false);
                    debugPref = !debugPref;
                    sharedPref.edit()
                            .putBoolean(Common.DEBUG_KEY, debugPref)
                            .apply();*/
                    break;
                }
                case 8: //Enable
                    intent = new Intent ( this, com.hehe.hehexposedlocation.appsettings.XposedModActivity.class );
                    startActivity ( intent );
                    break;
                default:
                    break;
            }

        }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Settings Page") //  Define a title for the content shown.
                // Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    protected void GetFile() throws InterruptedException {
        UserpkgName.clear();
        SyspkgName.clear();
        //http://blog.csdn.net/feng88724/article/details/6198446
        // Android】获取手机中已安装apk文件信息(PackageInfo、ResolveInfo)(应用图片、应用名、包名等)
        // 查询所有已经安装的应用程序
        pm = this.getPackageManager();

        //获取手机内所有应用
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                UserpkgName.add(pak.packageName);
            }
            else
                SyspkgName.add(pak.packageName);
        }
        Collections.sort(UserpkgName);
        Collections.sort(SyspkgName);

        UserApplicationFile = getSharedPreferences(Common.USER_PACKET_NAME, 0);
        SystemApplicationFile = getSharedPreferences(Common.SYSTEM_PACKET_NAME, 0);

        PE = UserApplicationFile.edit();
        PE.putBoolean(Common.USER_PACKET_NAME_KEY_ALL, true);
        PE.putStringSet(Common.USER_PACKET_NAME_KEY, new HashSet<String>(UserpkgName));
        PE.apply();

        PE = SystemApplicationFile.edit();
        PE.putBoolean(Common.SYSTEM_PACKET_NAME_KEY_ALL, true);
        PE.putStringSet(Common.SYSTEM_PACKET_NAME_KEY, new HashSet<String>(SyspkgName));
        PE.apply();

        List<String> Category = new ArrayList<String>();
        Category.clear();
        WebContent = getSharedPreferences(Common.WEB_CONTENT, 0);

        for(String User : UserpkgName) {
            String temp = WebGet(User);
            if(temp != null) {
                Category.add(User + temp);
            }
        }
        //Wait Dr.hu declar need or not
        /*for(String System : SyspkgName){
            String temp = WebGet(System);
            if(temp != null) {
                Category.add(System + temp);
            }
        }*/
        Collections.sort(Category);

        PE = WebContent.edit();
        PE.putStringSet(Common.WEB_CONTENT_KEY, new HashSet<String>(Category));
        PE.apply();
        // https://jsoup.org/cookbook/extracting-data/dom-navigation
        // http://stackoverflow.com/questions/11026937/parsing-particular-data-from-website-in-android
    }
    protected String WebGet(String pkg) throws InterruptedException {//TODO get null
        String Domain = "https://play.google.com";
        String ShortURL = "/store/apps/details?id=";
        String ContrySet = "&hl=en";
        final String url = Domain + ShortURL + pkg + ContrySet;
         final AtomicReference<String> b = new AtomicReference<String>();
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    // Reference code
                    // http://stackoverflow.com/questions/10710442/how-to-get-category-for-each-app-on-device-on-android
                    // http://www.oodlestechnologies.com/blogs/Getting-app-information-from-Google-play-store-by-Url
                    doc = connect(url);
                    if(doc != null) {
                       // Element link = doc.select("span[itemprop=genre]").first();
                        Element Alink = doc.select("a.category").first();
                        String ge = Alink.text();
                        b.set(ge);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t0.start();
        t0.join();
        return b.get();
    }
    private static Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(1000*3).get();//.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com")
        } catch (NullPointerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doc;
    }
}
