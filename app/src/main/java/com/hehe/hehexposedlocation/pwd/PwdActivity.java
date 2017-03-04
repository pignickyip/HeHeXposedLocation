package com.hehe.hehexposedlocation.pwd;

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
    }

}
