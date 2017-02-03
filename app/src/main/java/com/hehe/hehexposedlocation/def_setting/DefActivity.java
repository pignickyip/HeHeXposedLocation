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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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

    private PackageManager pm = this.getPackageManager();
    private SharedPreferences.Editor PE;

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
        //HashMAp
        /*Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);//http://blog.csdn.net/jackrex/article/details/9189657
        pkgAppsList = this.getPackageManager().queryIntentActivities(mainIntent, 0);*/
        //http://stackoverflow.com/questions/6418945/retrieving-application-information-from-package-manager
        //http://blog.csdn.net/qinjuning/article/details/6867806
        // http://stackoverflow.com/questions/14675978/getting-attributes-values-from-xml-in-java
        //获取所有应用的名称，包名，以及权限 有了包名就可以判断是否有某个应用了
        // Category
        /*pkgApp = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        try {
            PackageManager packageManager = getPackageManager();
            for (PackageInfo pkgInfo : pkgApp) {
                String[] perms = pkgInfo.requestedPermissions;
                if (perms != null)
                    for (String perm : perms) {
                    }
            }
        }
        catch(Exception e){
            Log.e("WTF","FUCK",e);//http://blog.csdn.net/Android_Tutor/article/details/5081713
        }*/
        //http://blog.csdn.net/feng88724/article/details/6198446
        // Android】获取手机中已安装apk文件信息(PackageInfo、ResolveInfo)(应用图片、应用名、包名等)
        // 查询所有已经安装的应用程序
        List<String> UserpkgName = new ArrayList<String>();
        List<String> SyspkgName = new ArrayList<String>();
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
        UserApplicationFile = getSharedPreferences(Common.USER_PACKET_NAME, 0);
        SystemApplicationFile = getSharedPreferences(Common.SYSTEM_PACKET_NAME, 0);

        PE = UserApplicationFile.edit();
        PE.putStringSet(Common.USER_PACKET_NAME_KEY, (Set<String>) UserpkgName);
        PE.apply();

        PE = SystemApplicationFile.edit();
        PE.putStringSet(Common.SYSTEM_PACKET_NAME_KEY, (Set<String>) SyspkgName);
        PE.apply();

        GetWebData(UserpkgName, SyspkgName);
    }
    private void GetWebData(List<String> UserpkgName, List<String> SyspkgName){
        try {
            WebContent = getSharedPreferences(Common.WEB_CONTENT, 0);
            String playstoreURL = "https://play.google.com/store/apps/details?id=";
            String test = "com.google.android.apps.maps";
            //Document[] doc;
            Document doc = Jsoup.connect(playstoreURL+test).get();
            Elements links = doc.select("a[href]");
            Elements imports = doc.select("link[href]");
            String a = " " ,b = " ";
            for (Element link : imports) {
                a = " * %s <%s> (%s)" + link.tagName() + link.attr("abs:href") + link.attr("rel");
            }

            PE = WebContent.edit();
            PE.putString(test,a);
            PE.apply();

            for (Element link : links) {
               b = " * a: <%s>  (%s)" + link.attr("abs:href") + trim(link.text(), 35);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}