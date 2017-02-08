package com.hehe.hehexposedlocation.feedback;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.WhitelistActivity;
import com.hehe.hehexposedlocation.def_setting.DefActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class FeedbackActivity extends PreferenceActivity {
    private GoogleApiClient client;

    String[] menuItems;

    private PackageManager pm;

    SharedPreferences clear;
    SharedPreferences.Editor PE;

    public static SharedPreferences ComfortableChoise = null;

    private RadioButton week;
    private RadioButton strong;
    private RadioButton suitable;
    private RadioGroup rgroup;
    private TextView show;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        SetRadio();

        Resources res = getResources();
        menuItems = res.getStringArray(R.array.FeedbackList);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuItems);
        setListAdapter(adapter);

        ComfortableChoise = getSharedPreferences(Common.FEEDBACK_COMFORTABLE, MODE_WORLD_READABLE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }
    public void SetRadio(){

        week = (RadioButton) findViewById(R.id.Strong);
        strong = (RadioButton) findViewById(R.id.Suitable);
        suitable = (RadioButton) findViewById(R.id.Week);
        rgroup = (RadioGroup) findViewById(R.id.comfortable_radio_group);
        show = (TextView) findViewById(R.id.show);
        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int buttonId = radioGroup.getCheckedRadioButtonId();
                switch(buttonId) {
                    case R.id.Strong:
                        Toast.makeText(getApplicationContext(), "Your Male", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Suitable:
                        Toast.makeText(getApplicationContext(), "Your Female", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Week:
                        Toast.makeText(getApplicationContext(), "Your Other", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onListItemClick(ListView parent, View v, int position, long id) {
        switch (position) {
            case 0: //
               /* final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.comfortable_radiogroup);

                dialog.setTitle("Choise which you want?");
                dialog.setCancelable(true);

                rgroup = (RadioGroup) dialog.findViewById(R.id.comfortable_radio_group);
                int rgid = rgroup.getCheckedRadioButtonId();
                final RadioButton rb = (RadioButton) findViewById(rgid);
                week  = new RadioButton(this);
                suitable  = new RadioButton(this);
                strong  = new RadioButton(this);
                boolean temp = ComfortableChoise.getBoolean("Strong", true);
                if(temp)
                    if(ComfortableChoise.getBoolean("Suitable", true))
                        suitable.setChecked(true);
                    else
                        week.setChecked(true);
                else
                    strong.setChecked(true);

                rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        SharedPreferences.Editor spe = ComfortableChoise.edit();
                        if (checkedId == R.id.Strong) {
                            spe.putBoolean("Strong",false);
                        }
                        else if (checkedId == R.id.Suitable) {
                            spe.putBoolean("Suitable",false);
                        }
                        else if (checkedId == R.id.Week) {
                            spe.putBoolean("Week",false);
                        }
                        spe.commit();
                    }
                });
                dialog.show();
*/
                break;
            case 1:
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                View promptView = layoutInflater.inflate(R.layout.feedback_text_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(promptView);

                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int haha = R.id.feedback_text;
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"15049782d@connect.polyu.hk"});
                                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                                i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(FeedbackActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
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
}