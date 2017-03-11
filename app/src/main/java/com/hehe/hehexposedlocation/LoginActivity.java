package com.hehe.hehexposedlocation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.app.Activity;

import android.os.AsyncTask;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.hehe.hehexposedlocation.R;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    String userEntered;
    String userPin ;

    SharedPreferences password = null;
    final int PIN_LENGTH = 4;
    boolean keyPadLockedFlag = false;
    Context appContext;

    TextView titleView;

    TextView pinBox0;
    TextView pinBox1;
    TextView pinBox2;
    TextView pinBox3;


    TextView statusView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button button10;
    Button buttonExit;
    Button buttonDelete;
    EditText passwordInput;
    ImageView backSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = this;
        userEntered = "";
        password = getSharedPreferences(Common.PASSWORD_SETTING, 0);

        userPin = password.getString(Common.PASSWORD_PIN_CODE, "");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        //Typeface xpressive=Typeface.createFromAsset(getAssets(), "fonts/XpressiveBold.ttf");

        statusView = (TextView) findViewById(R.id.statusview);
        passwordInput = (EditText) findViewById(R.id.editText);
        backSpace = (ImageView) findViewById(R.id.imageView);
        buttonExit = (Button) findViewById(R.id.buttonExit);
        backSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordInput.setText(passwordInput.getText().toString().substring(0, passwordInput.getText().toString().length() - 2));
            }
        });
        buttonExit.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              //Exit app
                                              Intent i = new Intent();
                                              i.setAction(Intent.ACTION_MAIN);
                                              i.addCategory(Intent.CATEGORY_HOME);
                                              appContext.startActivity(i);
                                              finish();
                                          }
                                      }
        );
        //buttonExit.setTypeface(xpressive);

        buttonDelete = (Button) findViewById(R.id.buttonDeleteBack);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {

                                                if (keyPadLockedFlag == true) {
                                                    return;
                                                }

                                                if (userEntered.length() > 0) {
                                                    userEntered = userEntered.substring(0, userEntered.length() - 1);
                                                    passwordInput.setText("");
                                                }


                                            }

                                        }
        );

        titleView = (TextView) findViewById(R.id.app_title);
        //titleView.setTypeface(xpressive);


        View.OnClickListener pinButtonHandler = new View.OnClickListener() {
            public void onClick(View v) {

                if (keyPadLockedFlag == true) {
                    return;
                }

                Button pressedButton = (Button) v;


                if (userEntered.length() < PIN_LENGTH) {
                    userEntered = userEntered + pressedButton.getText();
                    Log.v("PinView", "User entered=" + userEntered);

                    //Update pin boxes
                    passwordInput.setText(passwordInput.getText().toString() + "*");
                    passwordInput.setSelection(passwordInput.getText().toString().length());

                    if (userEntered.length() == PIN_LENGTH) {
                        //Check if entered PIN is correct
                        if (userEntered.equals(userPin)) {
                            statusView.setTextColor(Color.GREEN);
                            statusView.setText("Correct");
                            Log.v("PinView", "Correct PIN");
                            finish();
                        } else {
                            statusView.setTextColor(Color.RED);
                            statusView.setText("Wrong PIN. Keypad Locked");
                            keyPadLockedFlag = true;
                            Log.v("PinView", "Wrong PIN");

                            new LockKeyPadOperation().execute("");
                        }
                    }
                } else {
                    //Roll over
                    passwordInput.setText("");

                    userEntered = "";

                    statusView.setText("");

                    userEntered = userEntered + pressedButton.getText();
                    Log.v("PinView", "User entered=" + userEntered);

                    //Update pin boxes
                    passwordInput.setText("8");

                }


            }
        };


        button0 = (Button) findViewById(R.id.button0);
        //button0.setTypeface(xpressive);
        button0.setOnClickListener(pinButtonHandler);

        button1 = (Button) findViewById(R.id.button1);
        //button1.setTypeface(xpressive);
        button1.setOnClickListener(pinButtonHandler);

        button2 = (Button) findViewById(R.id.button2);
        //button2.setTypeface(xpressive);
        button2.setOnClickListener(pinButtonHandler);


        button3 = (Button) findViewById(R.id.button3);
        //button3.setTypeface(xpressive);
        button3.setOnClickListener(pinButtonHandler);

        button4 = (Button) findViewById(R.id.button4);
        //button4.setTypeface(xpressive);
        button4.setOnClickListener(pinButtonHandler);

        button5 = (Button) findViewById(R.id.button5);
        //button5.setTypeface(xpressive);
        button5.setOnClickListener(pinButtonHandler);

        button6 = (Button) findViewById(R.id.button6);
        //button6.setTypeface(xpressive);
        button6.setOnClickListener(pinButtonHandler);

        button7 = (Button) findViewById(R.id.button7);
        //button7.setTypeface(xpressive);
        button7.setOnClickListener(pinButtonHandler);

        button8 = (Button) findViewById(R.id.button8);
        //button8.setTypeface(xpressive);
        button8.setOnClickListener(pinButtonHandler);

        button9 = (Button) findViewById(R.id.button9);
        //button9.setTypeface(xpressive);
        button9.setOnClickListener(pinButtonHandler);


        buttonDelete = (Button) findViewById(R.id.buttonDeleteBack);
        //buttonDelete.setTypeface(xpressive);


    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //App not allowed to go back to Parent activity until correct pin entered.
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_pin_entry_view, menu);
        return true;
    }


    private class LockKeyPadOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 2; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            statusView.setText("");

            //Roll over
            passwordInput.setText("");

            userEntered = "";

            keyPadLockedFlag = false;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
