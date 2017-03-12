package com.hehe.hehexposedlocation.def_setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.*;
import com.hehe.hehexposedlocation.Common;

import java.util.Arrays;
import java.util.Objects;

public class DefActivity extends Activity {
    private Spinner spinnerDef;
    private ArrayAdapter adapter;
    private static boolean te = true;
    private static SharedPreferences CUSTOMER = null;
    private static SharedPreferences SAVE_ACTION = null;
    private Button Restart = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_def);
        initControls();
        Restart = (Button) findViewById(R.id.restart_it);
        String msg = "Don't need to restart now";
        boolean setTextMsg = CUSTOMER.getBoolean(Common.DISPLAY_SPINNER_DEFAULT_RESTARTBUTTON,false);
        if(!setTextMsg)
            msg = "You should restart now";
        Restart.setText(msg);
        Restart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyChange();
            }
        });
        TextView tv = (TextView) findViewById(R.id.def_setting_intro);
        //Show the introduction
        String[] word = {"Default means my function is being stop\n",
                "Customer means enable any function of my application\n",
                "Low means low noise strength\n",
                "Medium means medium noise strength\n",
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

        SAVE_ACTION = getSharedPreferences(Common.SHARED_PREFERENCES_DEFAULT_POSITION, 0);
        if (!(SAVE_ACTION == null))
            spinnerDef.setSelection(SAVE_ACTION.getInt(Common.SHARED_PREFERENCES_DEFAULT_POSITION, 0));
        spinnerDef.setOnItemSelectedListener(new MyOnItemSelectedListener());

        CUSTOMER = getSharedPreferences(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER, 0);

    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            int OrigPosition = SAVE_ACTION.getInt(Common.SHARED_PREFERENCES_DEFAULT_POSITION, -1);
            //pass the position value to HockNoise
            SharedPreferences.Editor PE = SAVE_ACTION.edit();

            PE.remove(Common.SHARED_PREFERENCES_DEFAULT_POSITION);
            PE.putInt(Common.SHARED_PREFERENCES_DEFAULT_POSITION, position);
            PE.apply();
            try {
                if (Objects.equals(item, "Customer")) {
                    if (te) {
                        showInputDialog();
                    }
                } else {
                    te = true;
                }
                if( OrigPosition != -1 && OrigPosition != position) {
                    String msg = "Click me to restart";
                    Restart.setText(msg);
                }
            } catch (Exception e) {
                Log.d("", "FF");
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Rly othing
        }

    }

    protected void onDestory() {
        super.onDestroy();
    }

    private void showInputDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(DefActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_customer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DefActivity.this);
        alertDialogBuilder.setView(promptView);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int haha = R.id.customer_text;
                if (CUSTOMER.getBoolean(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER_CHECK, true))
                    te = false;
                try {
                    SharedPreferences.Editor PE = CUSTOMER.edit();
                    PE.putInt(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER, haha);
                    PE.putBoolean(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER_CHECK, true);
                    PE.putBoolean(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER_RECORD, true);
                    PE.putBoolean(Common.DISPLAY_SPINNER_DEFAULT_RESTARTBUTTON,true);
                    PE.apply();
                } catch (Exception e) {
                    dialog.notify();
                }
                String msg = "Restart your phone is required";
                Restart.setText(msg);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void ApplyChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To apply the change");
        builder.setTitle("Restart your phone");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor PE = CUSTOMER.edit();
                PE.remove(Common.DISPLAY_SPINNER_DEFAULT_RESTARTBUTTON);
                PE.apply();
                rebootAfterchange();
            }
        });
        builder.setNegativeButton("Don't need now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void rebootAfterchange() {
        try {
            //Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot -p"});
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot now"});
            //Process proc = Runtime.getRuntime().exec(new String[]{ "su", "-c", "busybox killall system_server"});
            proc.waitFor();
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}