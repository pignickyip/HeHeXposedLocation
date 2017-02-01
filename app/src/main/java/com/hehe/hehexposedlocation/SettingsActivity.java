package com.hehe.hehexposedlocation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends PreferenceActivity  {
    private GoogleApiClient client;

    SharedPreferences sharedPref;
    String[] menuItems;
    String instructionsString;
    String instructionsTitle;
    SharedPreferences clear;
    SharedPreferences.Editor PE;
    private int mPrevSelectedId;
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
                 + "\n\n"
                 ;

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
                    intent = new Intent ( this, IndexActivity.class );
                    startActivity ( intent );
                    break;
                case 7: //clear all setting
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Sure? One Way able");
                    builder.setTitle("Clear All Setting");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clear = getSharedPreferences(Common.SHARED_PREFERENCES_POSITION, 0);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();
                            clear = getSharedPreferences(Common.SHARED_PREDERENCES_CUSTOMER, 0);
                            PE = clear.edit();
                            PE.clear();
                            PE.apply();
                            clear = getSharedPreferences(Common.SHARED_PREFERENCES_FILE, MODE_WORLD_READABLE);
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
}
