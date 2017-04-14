package com.hehe.hehexposedlocation.feedback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

import java.util.Objects;


public class FeedbackActivity extends AppCompatActivity {
    private GoogleApiClient client;

    private PackageManager pm;
    private Resources res;

    public static SharedPreferences ComfortableChoise = null;

    private RadioButton week;
    private RadioButton strong;
    private RadioButton suitable;
    private RadioGroup rgroup;

    private TextView FeedbackIntro;
    private TextView show;
    private ImageButton ShowMeFeedback;

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

        initSet();
        SetRadio();

        ShowMeFeedback = (ImageButton)findViewById(R.id.feedback_button);
        ShowMeFeedback.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(FeedbackActivity.this);
                View promptView = layoutInflater.inflate(R.layout.dialog_feedback, null);
                AlertDialog.Builder b = new AlertDialog.Builder(FeedbackActivity.this);
                b.setView(promptView);
                final EditText feedbackContent= (EditText) promptView.findViewById(R.id.feedback_text);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String content = feedbackContent.getText().toString();
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"15049782d@connect.polyu.hk"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "Opinion of HeHeXposed Xposed module");
                        i.putExtra(Intent.EXTRA_TEXT   , content);
                        Log.d("Email","To admin");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Log.e("Email", String.valueOf(ex));
                            Toast.makeText(FeedbackActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    protected void initSet(){
        res  = getResources();
        ComfortableChoise = getSharedPreferences(Common.FEEDBACK_COMFORTABLE, MODE_WORLD_READABLE);

        strong = (RadioButton) findViewById(R.id.Strong);
        suitable = (RadioButton) findViewById(R.id.Suitable);
        week = (RadioButton) findViewById(R.id.Week);
        rgroup = (RadioGroup) findViewById(R.id.comfortable_radio_group);

        FeedbackIntro = (TextView) findViewById(R.id.feedback_intro);

        boolean hehe = ComfortableChoise.getBoolean(Common.FEEDBACK_COMFORTABLE_CHECK, true);
        if(!hehe) {
            String Choise = ComfortableChoise.getString(Common.FEEDBACK_COMFORTABLE_KEY, " ");
            String msg = "";
            if(Objects.equals(Choise, "Strong"))
                msg = "Your using the 1/2x Noise";
            else if(Objects.equals(Choise, "Suitable"))
                msg = "Thank you for your using.";
            else if(Objects.equals(Choise, "Week"))
                msg = "Your using the 2x Noise";
            FeedbackIntro.setText(msg);
        }

        show = (TextView) findViewById(R.id.show);
        String showmsg = "Administration email: " + res.getString(R.string.admin_email);
        show.setText(showmsg);
    }
    protected void SetRadio(){
        String getback = ComfortableChoise.getString(Common.FEEDBACK_COMFORTABLE_KEY, " ");
        if(Objects.equals(getback, "Strong"))
            strong.setChecked(true);
        else if(Objects.equals(getback, "Suitable"))
            suitable.setChecked(true);
        else if(Objects.equals(getback, "Week"))
            week.setChecked(true);

        rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int buttonId = radioGroup.getCheckedRadioButtonId();
                SharedPreferences.Editor spe = ComfortableChoise.edit();
                // find the radiobutton by returned id
                RadioButton returnvalue = (RadioButton) findViewById(buttonId);
                spe.putBoolean(Common.FEEDBACK_COMFORTABLE_CHECK, false);
                switch(buttonId) {
                    case R.id.Strong:
                        spe.putString(Common.FEEDBACK_COMFORTABLE_KEY,"Strong");
                        Toast.makeText(getApplicationContext(), returnvalue.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Suitable:
                        spe.putString(Common.FEEDBACK_COMFORTABLE_KEY,"Suitable");
                        Toast.makeText(getApplicationContext(), returnvalue.getText(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.Week:
                        spe.putString(Common.FEEDBACK_COMFORTABLE_KEY,"Week");
                        Toast.makeText(getApplicationContext(), returnvalue.getText(), Toast.LENGTH_SHORT).show();
                        break;
                }
                Log.d("Feedback", "Comfortable change");
                spe.apply();
            }
        });
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