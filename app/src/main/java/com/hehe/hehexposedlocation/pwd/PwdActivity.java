package com.hehe.hehexposedlocation.pwd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.def_setting.DefActivity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Objects;

public class PwdActivity extends Activity {
    String msg = "";
    EditText input_pwd;
    TextView pwd_title;
    TextView subview;
    ToggleButton OnOff;
    Button resetpwd;
    ImageButton touchid;
    ImageButton touchidCancel;
    TextView touchidView;
    SharedPreferences password;
    SharedPreferences.Editor PE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        pwd_title = (TextView) findViewById(R.id.pwd_setting);
        subview = (TextView) findViewById(R.id.statusview_pwd);
        input_pwd = (EditText) findViewById(R.id.pwdsetup);
        OnOff = (ToggleButton) findViewById(R.id.UsePWD);
        resetpwd = (Button) findViewById(R.id.reset_pwd);
        touchid = (ImageButton) findViewById(R.id.touchid_but);
        touchidCancel = (ImageButton) findViewById(R.id.touchid_cancel);
        touchidView = (TextView) findViewById(R.id.touchid_view);
        password = getSharedPreferences(Common.PASSWORD_SETTING, 0);

        msg = "Here to set up your own password";
        pwd_title.setText(msg);
        msg = "Please input your secure password";
        subview.setText(msg);

        input_pwd.setBackgroundResource(R.drawable.edittext_color);
        input_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = input_pwd.length();
                if (length < 4) {
                    msg = "You need to input " + (4 - length) + " more.";
                    subview.setText(msg);
                } else {
                    msg = "Please submit your password";
                    subview.setText(msg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String adapter = input_pwd.getText().toString();
                String pwd = (EncryptFunction(adapter));
                //Integer.parseInt
                Log.d("password", pwd);
            }
        });
        boolean setup_already = password.getBoolean(Common.PASSWORD_ALREADY_UP, false);
        if (setup_already)
            input_pwd.setHint("You have already set up the password");
        else
            input_pwd.setHint("Here to set up");
        ImageButton pwd_submit_but = (ImageButton) findViewById(R.id.pwd_submit);
        pwd_submit_but.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adapter = input_pwd.getText().toString();
                if (adapter.length() == 4) {
                    String pwd = (EncryptFunction(adapter));
                    PE = password.edit();
                    PE.putString(Common.PASSWORD_PIN_CODE, pwd);
                    PE.putBoolean(Common.PASSWORD_ALREADY_UP, true);
                    PE.apply();

                    input_pwd.setText("");
                    input_pwd.setHint("You have already set up the password");
                    Toast.makeText(getApplicationContext(), "The password is up-to-date", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong action", Toast.LENGTH_LONG).show();
                }
            }
        });
        resetpwd.setOnClickListener(new Button.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                ShowpwdMatchDialog();
            }
        });

        final boolean isUp = password.getBoolean(Common.PASSWORD_SETTING_ON, false);
        OnOff.setChecked(isUp);
        OnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean input_pwd_exist = password.getBoolean(Common.PASSWORD_ALREADY_UP,false);
                if(input_pwd_exist) {
                    PE = password.edit();
                    PE.remove(Common.PASSWORD_SETTING_ON);
                    if (isUp) {//it is up already
                        PE.putBoolean(Common.PASSWORD_SETTING_ON, false);
                    } else {
                        PE.putBoolean(Common.PASSWORD_SETTING_ON, true);
                    }
                    PE.apply();
                }
                else{
                    OnOff.setChecked(isUp);
                    Toast.makeText(getApplicationContext(), "Wrong action", Toast.LENGTH_LONG).show();
                }
            }
        });
        //Pin
        //http://lomza.totem-soft.com/pin-input-view-in-android/

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            //http://www.androidhive.info/2016/11/android-add-fingerprint-authentication/
            boolean touchUP = password.getBoolean(Common.PASSWORD_FINGERPRINT_ON,false);
            if(touchUP) {
                msg = "Already enable fingerprint";
                touchid.setVisibility(View.INVISIBLE);
                touchidCancel.setVisibility(View.VISIBLE);
            }
            else {
                msg = "Enable touch id";
                touchid.setVisibility(View.VISIBLE);
                touchidCancel.setVisibility(View.INVISIBLE);
            }
            touchidView.setText(msg);
            touchid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PwdActivity.this)
                            .setMessage("Are you sure?")
                            .setTitle("Enable the fingerprint")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PE = password.edit();
                                    PE.remove(Common.PASSWORD_FINGERPRINT_ON);
                                    PE.putBoolean(Common.PASSWORD_FINGERPRINT_ON, true);
                                    PE.apply();
                                    Toast.makeText(getApplicationContext(), "Enable now", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
            touchidCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(PwdActivity.this)
                            .setMessage("Are you sure?")
                            .setTitle("Disable the fingerprint")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PE = password.edit();
                                    PE.remove(Common.PASSWORD_FINGERPRINT_ON);
                                    PE.apply();
                                    Toast.makeText(getApplicationContext(), "Disable now", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            });
        } else {
            msg = "Your mobile phone cannot use fingerprint, upgrade your android or user the PIN";
            touchidView.setText(msg);
            touchid.setVisibility(View.INVISIBLE);
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

    private void ShowpwdMatchDialog() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(PwdActivity.this);
        View promptView = layoutInflater.inflate(R.layout.password_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PwdActivity.this);
        alertDialogBuilder.setView(promptView);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        final EditText auth_match = (EditText) promptView.findViewById(R.id.pwd_auth);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int attempt = password.getInt(Common.PASSWORD_PIN_RESET_ATTEMPT, 0);
                if (attempt < 4) {
                    String adapter = auth_match.getText().toString();
                    String challenge = EncryptFunction(adapter);
                    String real_pwd = password.getString(Common.PASSWORD_PIN_CODE, "");
                    if (Objects.equals(challenge, real_pwd)) {
                        PE = password.edit();
                        PE.clear();
                        PE.apply();
                        input_pwd.setHint("Here to set up");
                        msg = "Here to set up your own password";
                        pwd_title.setText(msg);
                        msg = "Password already reset";
                        subview.setText(msg);
                        OnOff.setChecked(false);
                    } else {
                        PE = password.edit();
                        PE.putInt(Common.PASSWORD_PIN_RESET_ATTEMPT, attempt + 1);
                        Toast.makeText(getApplicationContext(), "Wrong input", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } else {
                    //enable fnction in some mins
                }
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
}