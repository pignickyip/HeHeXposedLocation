package com.hehe.hehexposedlocation.def_setting;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.*;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.appsettings.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

import static android.R.attr.cursorVisible;
import static android.R.attr.focusable;
import static android.R.attr.focusableInTouchMode;

public class DefActivity extends Activity  {
    Spinner spinnerDef;
    ArrayAdapter adapter;
    String [] intro;
    List<PackageInfo> pkgs;

    private static Boolean check = true;
    // private EditText Customertext  = (EditText) findViewById(R.id.customnumber);
    private ListView vlist;
    private Intent parentIntent;
    public static SharedPreferences SAVE_NAME = null;
    public static Map<String, String> DEFSETTING = new HashMap<String, String>();
    public static SharedPreferences SAVE_PREF = null;
    public static List<ResolveInfo> pkgAppsList;
    public static List<PackageInfo> pkgApp;
    private ArrayList results = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def);

        initControls();

        intro = getResources().getStringArray(R.array.def_setting_intro);
        vlist = (ListView) findViewById(R.id.def_setting_intro);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, intro);
        vlist.setAdapter(adapter);

        Control();
    }
    protected void Control()  {
        //HashMAp
        if(check) {
            if (SAVE_NAME == null)
                SAVE_NAME = PreferenceManager.getDefaultSharedPreferences(DefActivity.this);//= getSharedPreferences ("pkgName", 0);

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);//http://blog.csdn.net/jackrex/article/details/9189657
            pkgAppsList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
            //获取所有应用的名称，包名，以及权限 有了包名就可以判断是否有某个应用了
            pkgApp = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);

            SharedPreferences.Editor PE = SAVE_NAME.edit();

            DEFSETTING.put("android", "android");//TODO Problem to Category

            for (PackageInfo packageInfo : pkgApp) {
                String pkgName = packageInfo.packageName;
                ApplicationInfo applicationInfo = packageInfo.applicationInfo;
                //stringBuilder.append("应用名称:"+ applicationInfo.loadLabel(getPackageManager())+ "\n");
                //http://stackoverflow.com/questions/39421952/packageinfo-requestedpermissions-vs-permissions
                CharSequence appsName = applicationInfo.loadLabel(getPackageManager());
                if (packageInfo.permissions != null) {
                    for (PermissionInfo p : packageInfo.permissions) {
                        if (Objects.equals(packageInfo.permissions, "android.permission.ACCESS_FINE_LOCATION")) {
                            DEFSETTING.put(pkgName, p.name);
                        } else if (Objects.equals(packageInfo.permissions, "android.permission.ACCESS_COARSE_LOCATION")) {
                            DEFSETTING.put(pkgName, p.name);
                        } else if (Objects.equals(packageInfo.permissions, "android.permission.ACCESS_NETWORK_STATE")) {
                            DEFSETTING.put(pkgName, p.name);
                        }
                    }
                }
            }
            SAVE_PREF = getSharedPreferences("SNIPPER", 0);
        }
    }
    protected void initControls() {
        spinnerDef = (Spinner) findViewById(R.id.def_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this, R.array.def_setting, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerDef.setAdapter(adapter);
        if(!(SAVE_PREF==null))
           spinnerDef.setSelection(SAVE_PREF.getInt("SPINNER", 0));
        spinnerDef.setOnItemSelectedListener(new MyOnItemSelectedListener());
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
            //TODO
            SharedPreferences.Editor PE = SAVE_PREF.edit();
            PE.putInt("SPINNER", position);
            PE.apply();

            Common.DEFAULT = item;
            if(position == 1){ //TODO Set up the field for user input its customer value
            //    Customertext.setFocusable(true);
            //    Customertext.setClickable(true);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Rly othing
        }

    }
}