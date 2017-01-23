package com.hehe.hehexposedlocation.def_setting;

import android.app.Activity;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.hehe.hehexposedlocation.*;
import com.hehe.hehexposedlocation.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;



public class DefActivity extends Activity  {
    Spinner spinnerDef;
    ArrayAdapter adapter;
    String [] intro;
    List<PackageInfo> pkgs;

    // private EditText Customertext  = (EditText) findViewById(R.id.customnumber);
    private ListView vlist;
    private Intent parentIntent;
    public static int POSITION;
    public static SharedPreferences CUSTOMER = null;
    public static SharedPreferences SAVE_ACTION = null;
    public static List<ResolveInfo> pkgAppsList;
    public static List<PackageInfo> pkgApp;
    private ArrayList results = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_def);

        initControls();

        //Show the introduction
        intro = getResources().getStringArray(R.array.def_setting_intro);
        vlist = (ListView) findViewById(R.id.def_setting_intro);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, intro);
        vlist.setAdapter(adapter);

        Control();//DO logic
    }
    protected void Control()  {
        //HashMAp
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);//http://blog.csdn.net/jackrex/article/details/9189657
        pkgAppsList = this.getPackageManager().queryIntentActivities(mainIntent, 0);
        //获取所有应用的名称，包名，以及权限 有了包名就可以判断是否有某个应用了
        // TODO Problem to Category
        /*pkgApp = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        try {
            PackageManager packageManager = getPackageManager();
            for (PackageInfo pkgInfo : pkgApp) {
                String[] perms = pkgInfo.requestedPermissions;
                if (perms != null)
                    for (String perm : perms) {
                        if (Objects.equals(perm, "android.permission.ACCESS_FINE_LOCATION"))
                            DEFSETTING.put(pkgInfo.packageName, 1);
                        else if (Objects.equals(perm, "ACCESS_FINE_LOCATION"))
                            DEFSETTING.put(pkgInfo.packageName, 2);
                        else if (Objects.equals(perm, "android:name=\"android.permission.ACCESS_FINE_LOCATION\""))
                            DEFSETTING.put(pkgInfo.packageName, 3);
                        else if (Objects.equals(perm, "android.permission.ACCESS_COARSE_LOCATION"))
                            DEFSETTING.put(pkgInfo.packageName, 4);
                    }
            }
        }
        catch(Exception e){//TODO not yet test
            Log.e("WTF","FUCK",e);//http://blog.csdn.net/Android_Tutor/article/details/5081713
        }*/

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
            //TODO pass the position value to DefNoise, try the code from overridesettings
            SharedPreferences.Editor PE = SAVE_ACTION.edit();
            PE.putInt(Common.SHARED_PREFERENCES_POSITION, position);
            PE.apply();
            try {
                if (Objects.equals(item, "Customer")) {
                    //TODO popup box to get the value,,, first get above
                    CUSTOMER = getSharedPreferences(Common.SHARED_PREDERENCES_CUSTOMER, 5);
                }
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