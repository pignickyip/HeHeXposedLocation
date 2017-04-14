package com.hehe.hehexposedlocation;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.app.Activity;

import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.advanced_function.FingerprintHandler;
import com.hehe.hehexposedlocation.pwd.PwdActivity;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.Objects;

import javax.crypto.Cipher;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    String userEntered;
    String userPin;
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
    ImageButton buttonExit;
    ImageButton buttonDelete;
    EditText passwordInput;
    ImageView backSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        password = getSharedPreferences(Common.PASSWORD_SETTING, 0);
        appContext = this;
        userEntered = "";

        boolean setup_already = password.getBoolean(Common.PASSWORD_SETTING_ON, false);
        boolean touchUP = password.getBoolean(Common.PASSWORD_FINGERPRINT_ON,false);
        if (setup_already) {
            if(touchUP){
                Intent intent = new Intent(this, com.hehe.hehexposedlocation.FingerprintActivity.class);
                Log.d("Login", "Login by fingerprint");
                startActivity(intent);
                finish();
            }
            userPin = password.getString(Common.PASSWORD_PIN_CODE, "");
        } else {
            MovetoContent();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //Typeface xpressive=Typeface.createFromAsset(getAssets(), "fonts/XpressiveBold.ttf");
        statusView = (TextView) findViewById(R.id.statusview);
        passwordInput = (EditText) findViewById(R.id.editText);
        backSpace = (ImageView) findViewById(R.id.imageView);
        buttonExit = (ImageButton) findViewById(R.id.buttonExit);
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
        buttonDelete = (ImageButton) findViewById(R.id.buttonDeleteBack);
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
                int Login_attemp = password.getInt(Common.PASSWORD_LOGIN_ATTEMPT, 0);
                SharedPreferences.Editor PE = password.edit();
                if (Login_attemp < 4) {
                    if (userEntered.length() < PIN_LENGTH) {
                        userEntered = userEntered + pressedButton.getText();
                        //Update pin boxes
                        passwordInput.setText(passwordInput.getText().toString() + "*");
                        passwordInput.setSelection(passwordInput.getText().toString().length());
                        if (userEntered.length() == PIN_LENGTH) {
                            //Check if entered PIN is correct
                            if (EncryptFunction(userEntered).equals(userPin)) {
                                statusView.setTextColor(Color.GREEN);
                                statusView.setText("Correct");
                                PE.putBoolean(Common.PASSWORD_LAST_LOGIN_ATTEMPT, true);
                                PE.remove(Common.PASSWORD_LOGIN_ATTEMPT);
                                PE.putInt(Common.PASSWORD_LOGIN_ATTEMPT, 0);
                                PE.apply();
                                Log.v("PinView", "Correct PIN");
                                MovetoContent();
                            } else {
                                statusView.setTextColor(Color.RED);
                                statusView.setText("Wrong PIN. Keypad Locked");
                                keyPadLockedFlag = true;
                                Log.v("PinView", "Wrong PIN");
                                PE.remove(Common.PASSWORD_LOGIN_ATTEMPT);
                                PE.putInt(Common.PASSWORD_LOGIN_ATTEMPT, Login_attemp + 1);
                                PE.apply();
                                new LockKeyPadOperation().execute("");
                            }
                        }
                    } else {
                        //Roll over
                        passwordInput.setText("");
                        userEntered = "";
                        statusView.setText("");
                        userEntered = userEntered + pressedButton.getText();
                        //Update pin boxes
                        passwordInput.setText("8");
                    }
                } else {
                    statusView.setTextColor(Color.RED);
                    statusView.setText("You cant login now");
                    keyPadLockedFlag = true;
                    PE.remove(Common.PASSWORD_LOGIN_ATTEMPT);
                    PE.putInt(Common.PASSWORD_LOGIN_ATTEMPT, 0);
                    PE.apply();
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
        buttonDelete = (ImageButton) findViewById(R.id.buttonDeleteBack);
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

    private static String EncryptFunction(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, digest);
            String sha = number.toString(16);

            while (sha.length() < 64) {
                sha = "0" + sha;
            }
            return sha;
        } catch (Exception e) {
            Log.e("sha256", e.getMessage());
            return null;
        }
    }

    private void MovetoContent() {
        Intent intent = new Intent(this, com.hehe.hehexposedlocation.SettingsActivity.class);
        Log.d("Login", "Get access");
        startActivity(intent);
        finish();
    }
}
