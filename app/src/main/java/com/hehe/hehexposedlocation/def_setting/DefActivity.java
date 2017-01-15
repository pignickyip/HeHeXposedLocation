package com.hehe.hehexposedlocation.def_setting;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XSharedPreferences;

import static android.R.attr.cursorVisible;
import static android.R.attr.focusable;
import static android.R.attr.focusableInTouchMode;

public class DefActivity extends Activity  {
    //TODO! Need to make the application save it
    Spinner spinnerDef;
    ArrayAdapter adapter;
    String [] intro;
    List<PackageInfo> pkgs;
    String pkgName;
    // private EditText Customertext  = (EditText) findViewById(R.id.customnumber);
    private ListView vlist;
    private Intent parentIntent;
    public static Map<String, Object> DEFSETTING = new HashMap<String, Object>();
    public static SharedPreferences SAVE_PREF = null;
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
        pkgName = getPackageName();

        pkgs = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        DEFSETTING.put(pkgName,pkgs);
        SAVE_PREF = getSharedPreferences ("SNIPPER", 0);


        //SharedPreferences.Editor editor = getSharedPreferences(String.valueOf(sharedPref), MODE_PRIVATE).edit();
        // editor.putStringSet(packageName,pkgs);
        //editor.commit();
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
            if(position == 1){
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