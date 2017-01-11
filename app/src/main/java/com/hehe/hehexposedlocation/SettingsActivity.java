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
import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.*;
import com.hehe.hehexposedlocation.intro.*;
import com.hehe.hehexposedlocation.log.LogsFragment;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends PreferenceActivity  {
    private GoogleApiClient client;

    public static final String LOG_TAG = "Settings";
    SharedPreferences sharedPref;
    String[] menuItems;
    String instructionsString;
    String instructionsTitle;
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
        instructionsString = res.getString(R.string.instructions1) + "\n\n"
                + res.getString(R.string.instructions2) + "\n\n"
                + res.getString(R.string.instructions3) + "\n\n"
                + res.getString(R.string.instructions4);

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
                case 4: //Mock
                    intent = new Intent ( this, WhitelistActivity.class );
                    startActivity ( intent );
                    break;
                case 5: //Enable
                    intent = new Intent ( this, com.hehe.hehexposedlocation.appsettings.XposedModActivity.class );
                    startActivity ( intent );
                    break;
                case 6://Log
                    intent = new Intent ( this, com.hehe.hehexposedlocation.log.Settings.class );
                    // intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                     startActivity ( intent );
                     finish ();
                    break;
                case 7: //debug
                    boolean debugPref = sharedPref.getBoolean ( Common.DEBUG_KEY, false );
                    debugPref = !debugPref;
                    sharedPref.edit ()
                            .putBoolean ( Common.DEBUG_KEY, debugPref )
                            .apply ();
                    String debugStatus = getString ( debugPref ? R.string.debug_on : R.string.debug_off );
                    Log.d ( LOG_TAG, debugStatus );
                    Toast.makeText ( getApplicationContext (), debugStatus, Toast.LENGTH_LONG ).show ();
                    break;
                case 8: //test
                    intent = new Intent ( this, com.hehe.hehexposedlocation.log.LogsFragment.class );
                    // intent.setFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity ( intent );
                    break;
                case 9: //test
                  //  intent = new Intent ( this, com.example.user.hehexposed.gpsfake.MainActivity.class );
                  //  startActivity ( intent );
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
                .setName("Settings Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
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
