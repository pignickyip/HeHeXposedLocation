package com.hehe.hehexposedlocation.mode;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.feedback.FeedbackActivity;

import java.util.Calendar;

public class ModeActivity extends Activity {
    private SharedPreferences Rest = null;
    private SharedPreferences Work = null;
    private SharedPreferences.Editor PE = null;

    private TextView restmode = null;
    private TextView workmode = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);
//http://javapapers.com/android/android-alarm-clock-tutorial/
        initSet();
        ButtonAction();
        clearsSetting();
    }
    private void initSet(){
        Rest = getSharedPreferences(Common.MODE_REST_SETUP, MODE_WORLD_READABLE);
        Work = getSharedPreferences(Common.MODE_WORK_SETUP, MODE_WORLD_READABLE);
        restmode = (TextView) findViewById(R.id.restmode);
        Boolean RestStart_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_STARTTIME_KEY,false);
        Boolean RestEnd_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_ENDTIME_KEY,false);
        if(RestStart_Setup && RestEnd_Setup) {
            String msg = "Rest mode is on";
            restmode.setText(msg);
        }
        else {
            String msg = "Rest mode is off";
            restmode.setText(msg);
        }
        workmode = (TextView) findViewById(R.id.workmode);
        Boolean WorkStart_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_STARTTIME_KEY,false);
        Boolean WorkEnd_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_ENDTIME_KEY,false);
        if(WorkStart_Setup && WorkEnd_Setup) {
            String msg = "Work mode is on";
            workmode.setText(msg);
        }
        else {
            String msg = "Work mode is off";
            workmode.setText(msg);
        }
    }
    private void ButtonAction(){

        Button rest_start_but = (Button) findViewById(R.id.but_starttime_rest);
        Button rest_end_but= (Button) findViewById(R.id.but_endtime_rest);
        Button work_start_but = (Button) findViewById(R.id.but_starttime_work);
        Button work_end_but = (Button) findViewById(R.id.but_endtime_work);

        rest_start_but.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ModeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        restmode.setText( "The Start time set as " + selectedHour + ":" + selectedMinute);
                        PE = Rest.edit();
                        PE.putBoolean(Common.MODE_REST_SETUP_STARTTIME_KEY, true);
                        PE.putInt(Common.MODE_REST_SETUP_STARTTIME_KEY_HOUR,selectedHour);
                        PE.putInt(Common.MODE_REST_SETUP_STARTTIME_KEY_MINUTES,selectedMinute);
                        PE.commit();
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.setCancelable(true);
                mTimePicker.show();
            }
        });
        rest_end_but.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ModeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        restmode.setText( "The End time set as " + selectedHour + ":" + selectedMinute);
                        PE = Rest.edit();
                        PE.putBoolean(Common.MODE_REST_SETUP_ENDTIME_KEY,true);
                        PE.putInt(Common.MODE_REST_SETUP_ENDTIME_KEY_HOUR,selectedHour);
                        PE.putInt(Common.MODE_REST_SETUP_ENDTIME_KEY_MINUTES,selectedMinute);
                        PE.commit();
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }
    private void clearsSetting(){
        Button ClearRest = (Button) findViewById(R.id.but_clear_rest);
        ClearRest.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModeActivity.this);
                builder.setMessage("Sure? One Way able");
                builder.setTitle("Clear Rest mode Setting");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PE = Rest.edit();
                        PE.clear();
                        PE.commit();
                        String msg = "Rest mode is off";
                        restmode.setText(msg);
                        Toast.makeText(getApplicationContext(), "Successfully reset it", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        Button ClearWork= (Button) findViewById(R.id.but_clear_work);
        ClearWork.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModeActivity.this);
                builder.setMessage("Sure? One Way able");
                builder.setTitle("Clear Work mode Setting");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PE = Work.edit();
                        PE.clear();
                        PE.commit();
                        String msg = "Work mode is off";
                        workmode.setText(msg);
                        Toast.makeText(getApplicationContext(), "Successfully reset it", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }
}
