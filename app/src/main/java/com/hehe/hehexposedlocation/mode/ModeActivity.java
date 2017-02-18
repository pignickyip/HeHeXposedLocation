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
        ToggleSetting();
    }
    private void initSet(){
        Rest = getSharedPreferences(Common.MODE_REST_SETUP, MODE_WORLD_READABLE);
        Work = getSharedPreferences(Common.MODE_WORK_SETUP, MODE_WORLD_READABLE);
        restmode = (TextView) findViewById(R.id.restmode);
        Boolean Rest_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_KEY,false);
        Boolean RestStart_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_STARTTIME_KEY,false);
        Boolean RestEnd_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_ENDTIME_KEY,false);
        if(Rest_Setup && RestStart_Setup && RestEnd_Setup) {
            String msg = "Rest mode is working";
            restmode.setText(msg);
        }
        else if(Rest_Setup || !(RestStart_Setup && RestEnd_Setup)){
            String msg = "Rest mode is not working since not yet set up the start time and end time";
            restmode.setText(msg);
        }
        else {
            String msg = "Rest mode is not working";
            restmode.setText(msg);
        }
        workmode = (TextView) findViewById(R.id.workmode);
        Boolean Work_Setup = Rest.getBoolean(Common.MODE_WORK_SETUP_KEY,false);
        Boolean WorkStart_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_STARTTIME_KEY,false);
        Boolean WorkEnd_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_ENDTIME_KEY,false);
        if( Work_Setup && WorkStart_Setup && WorkEnd_Setup) {
            String msg = "Work mode is working";
            workmode.setText(msg);
        }
        else if(Work_Setup || !(WorkStart_Setup && WorkEnd_Setup)){
            String msg = "Work mode is not working since not yet set up the start time and end time";
            restmode.setText(msg);
        }
        else {
            String msg = "Work mode is not working";
            workmode.setText(msg);
        }
    }
    private void ButtonAction(){

        Button rest_start_but = (Button) findViewById(R.id.but_starttime_rest);
        Button rest_end_but= (Button) findViewById(R.id.but_endtime_rest);
        Button work_start_but = (Button) findViewById(R.id.but_starttime_work);
        Button work_end_but = (Button) findViewById(R.id.but_endtime_work);

        ///Rest mode
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
                        PE.putBoolean(Common.MODE_REST_SETUP_STARTTIME_KEY,true);
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
        //Work mode
        work_start_but.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ModeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        workmode.setText( "The Start time set as " + selectedHour + ":" + selectedMinute);
                        PE = Work.edit();
                        PE.putBoolean(Common.MODE_WORK_SETUP_STARTTIME_KEY,true);
                        PE.putInt(Common.MODE_WORK_SETUP_STARTTIME_KEY_HOUR,selectedHour);
                        PE.putInt(Common.MODE_WORK_SETUP_STARTTIME_KEY_MINUTES,selectedMinute);
                        PE.commit();
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        work_end_but.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ModeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        workmode.setText( "The End time set as " + selectedHour + ":" + selectedMinute);
                        PE = Work.edit();
                        PE.putBoolean(Common.MODE_WORK_SETUP_ENDTIME_KEY,true);
                        PE.putInt(Common.MODE_WORK_SETUP_ENDTIME_KEY_HOUR,selectedHour);
                        PE.putInt(Common.MODE_WORK_SETUP_ENDTIME_KEY_MINUTES,selectedMinute);
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
                        String msg = "Rest mode already reset";
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
                        String msg = "Work mode already reset";
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
    private void ToggleSetting(){
        final String Special_msg = ". \nNotice that the setting not yet done. It would not executed";

        final ToggleButton WorkModeOn = (ToggleButton) findViewById(R.id.WorkModeOn);
        final Boolean WorkStart_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_STARTTIME_KEY,false);
        final Boolean WorkEnd_Setup = Work.getBoolean(Common.MODE_WORK_SETUP_ENDTIME_KEY,false);
        final Boolean Work_Setup = Rest.getBoolean(Common.MODE_WORK_SETUP_KEY,false);

        Boolean Workcheck = false;
        if(Work_Setup)
            Workcheck = true;
        WorkModeOn.setChecked(Workcheck);
        WorkModeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PE = Work.edit();
                PE.putBoolean(Common.MODE_WORK_SETUP_KEY,true);
                PE.commit();

                String status = "Work mode : " + WorkModeOn.getText();
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show(); // display the current state of toggle button's

                if(!(WorkStart_Setup&&WorkEnd_Setup))
                    status += Special_msg;
                workmode.setText(status);
            }
        });

        final ToggleButton RestModeOn = (ToggleButton) findViewById(R.id.RestModeOn);
        final Boolean RestStart_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_KEY,false);
        final Boolean RestEnd_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_ENDTIME_KEY,false);
        final Boolean Rest_Setup = Rest.getBoolean(Common.MODE_REST_SETUP_ENDTIME_KEY,false);
        Boolean Restcheck = false;
        if(Rest_Setup)
            Restcheck = true;
        RestModeOn.setChecked(Restcheck);
        RestModeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PE = Rest.edit();
                PE.putBoolean(Common.MODE_REST_SETUP_KEY,true);
                PE.commit();

                String status = "Rest mode : " + RestModeOn.getText();
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show(); // display the current state of toggle button's
                if(!(RestStart_Setup&&RestEnd_Setup))
                    status += Special_msg;
                restmode.setText(status);
            }
        });
    }
}
