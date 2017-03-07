package com.hehe.hehexposedlocation.pwd;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.hehe.hehexposedlocation.R;

public class PwdActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        Toast.makeText(getApplicationContext(), "整緊呀 _ 你", Toast.LENGTH_SHORT).show();
        //Pin
        //http://lomza.totem-soft.com/pin-input-view-in-android/
        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            //https://developer.xamarin.com/guides/android/platform_features/fingerprint-authentication/
        }
    }

}
