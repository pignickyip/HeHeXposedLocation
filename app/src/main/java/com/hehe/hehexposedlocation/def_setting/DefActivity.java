package com.hehe.hehexposedlocation.def_setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureGroupInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.*;
import com.hehe.hehexposedlocation.Common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.helper.Validate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import java.io.IOException;

public class DefActivity extends Activity  {
    Spinner spinnerDef;
    ArrayAdapter adapter;
    static boolean te = true;
    public static int POSITION;
    public static SharedPreferences CUSTOMER = null;
    public static SharedPreferences SAVE_ACTION = null;
    public static SharedPreferences UserApplicationFile = null;
    public static SharedPreferences SystemApplicationFile = null;
    public static SharedPreferences WebContent = null;

    private PackageManager pm;
    private SharedPreferences.Editor PE;

    final List<String> UserpkgName = new ArrayList<String>();
    final List<String> SyspkgName = new ArrayList<String>();
    final List<String> Webcontent = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_def);

        initControls();

        TextView tv = (TextView) findViewById(R.id.def_setting_intro);
        //Show the introduction
        String[] word = { "Default means my function is being stop\n" ,
                "Customer means enable any function of my application\n" ,
                "Low means low noise strength\n" ,
                "Medium means medium noise strength\n" ,
                "Highest means highest noise strength"};
        // Prints [Pretty, Cool, Weird]
        tv.setText(Arrays.toString(word));
        // Prints Pretty, Cool, Weird
        tv.setText(Arrays.toString(word).replaceAll("\\[|\\]", ""));

        GetFile();//DO logic
    }
    protected void GetFile()  {
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

        // https://jsoup.org/cookbook/extracting-data/dom-navigation
        // http://stackoverflow.com/questions/11026937/parsing-particular-data-from-website-in-android
        WebContent = getSharedPreferences(Common.WEB_CONTENT, 0);
        String test = "com.google.android.apps.maps";
        String Domain = "https://play.google.com";
        String ShortURL = "/store/apps/details?id=";
        String ContrySet = "&hl=en";
        final String url = Domain + ShortURL + test + ContrySet;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        }catch (Exception e) {
            e.printStackTrace();
            Webcontent.add("sss");
        }
        if(doc != null) {
            Element link = doc.select("span[itemprop=genre]").first();
            String subCategory = link.text();
            Webcontent.add(subCategory);
        }
        else
            Webcontent.add("doc is null");

        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                //http://stackoverflow.com/questions/10710442/how-to-get-category-for-each-app-on-device-on-android
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                }catch (Exception e) {
                    e.printStackTrace();
                    Webcontent.add("sss");
                }
                    // http://www.oodlestechnologies.com/blogs/Getting-app-information-from-Google-play-store-by-Url
                   // Elements links = doc.select("a[href]");
                   // Element linkss = doc.select("a").first();
                  //  Webcontent.add(linkss.className());
                if(doc != null) {
                    Element link = doc.select("span[itemprop=genre]").first();
                    //String subCategory = Jsoup.parse(detailsInfo.select("span[itemprop=genre]").first().toString().replace("<span itemprop=\"genre\">", "").replace("</span>", "")).text();
                    //String category = Jsoup.parse(detailsInfo.select("a.category").attr("href")).text();
                    String subCategory = link.text();
                    Webcontent.add(subCategory);
                    //Webcontent.add(category);
                    /*int gameIndex = category.indexOf("GAME");
                    if(gameIndex == -1){
                        category = subCategory;
                    }else{
                        category = "GAMES";
                    }
                    Webcontent.add(category);
                    for (Element link : links) {
                        String className = link.className();
                        String classNameHref = link.attr("href");
                        if (classNameHref.startsWith("/store/apps/category/")) {
                            Webcontent.add(classNameHref);
                        }
                        if (Objects.equals(className, "document-subtitle category")) {
                            Webcontent.add(classNameHref);
                        }
                        Webcontent.add("errr");

                    } */
                }
                else
                    Webcontent.add("doc is null");
            }
        });
        //t0.start();
        if(Webcontent.isEmpty())
            Webcontent.add("url");
        else
            Webcontent.add("had value");
        // <a class="document-subtitle category" href="/store/apps/category/TRAVEL_AND_LOCAL">
        // <span itemprop="genre">
        // 旅遊XXXX
        //</span>
        // </a>
        PE = WebContent.edit();
        PE.putStringSet(Common.WEB_CONTENT_KEY,new HashSet<String>(Webcontent));
        PE.apply();

    }
    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
    protected void initControls() {
        spinnerDef = (Spinner) findViewById(R.id.def_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.def_setting, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDef.setAdapter(adapter);

        SAVE_ACTION = getSharedPreferences(Common.SHARED_PREFERENCES_POSITION, 0);
        if(!(SAVE_ACTION==null))
           spinnerDef.setSelection(SAVE_ACTION.getInt(Common.SHARED_PREFERENCES_POSITION, 0));
        spinnerDef.setOnItemSelectedListener(new MyOnItemSelectedListener());

        CUSTOMER = getSharedPreferences(Common.SHARED_PREDERENCES_CUSTOMER, 0);

        UserpkgName.clear();
        SyspkgName.clear();
        Webcontent.clear();
    }
    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(DefActivity.this);
        View promptView = layoutInflater.inflate(R.layout.customer_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DefActivity.this);
        alertDialogBuilder.setView(promptView);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int haha = R.id.customer_text;
                        if(CUSTOMER.getBoolean(Common.SHARED_PREDERENCES_CUSTOMER_CHECK, true))
                            te = false;
                        try {
                            SharedPreferences.Editor PE = CUSTOMER.edit();
                            PE.putInt(Common.SHARED_PREDERENCES_CUSTOMER, haha);
                            PE.putBoolean(Common.SHARED_PREDERENCES_CUSTOMER_CHECK, true);
                            PE.putBoolean(Common.SHARED_PREDERENCES_CUSTOMER_RECORD,true);
                            PE.apply();
                        }
                        catch(Exception e){
                            dialog.notify();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
           // Customertext.setFocusable(false);
           // Customertext.setClickable(false);
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            //pass the position value to DefNoise
            SharedPreferences.Editor PE = SAVE_ACTION.edit();
            PE.putInt(Common.SHARED_PREFERENCES_POSITION, position);
            PE.apply();
            //TODO Set the dialog to notice user restart the phone,,ASAP
            try {
                if (Objects.equals(item, "Customer")) {
                    if(te)
                        showInputDialog();
                }
                else
                    te = true;
            }
            catch(Exception e){
                Log.d("","FF");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Rly othing
        }

    }
    protected void onDestory(){
    }
}