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
    protected void onDestory(){
    }
}