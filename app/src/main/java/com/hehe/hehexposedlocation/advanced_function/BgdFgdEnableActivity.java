package com.hehe.hehexposedlocation.advanced_function;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

/**
 * Background and frontground detect service
 */

public class BgdFgdEnableActivity extends Activity {
    private Button startButton = null;
    private Button stopButton;
    private TextView Text;
    private static SharedPreferences BGDFGDRECORD = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgdfgdenable );
        BGDFGDRECORD = getSharedPreferences(Common.BGDFGDRECORDKEY, 0);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        startButton.setOnClickListener(startClickListener);
        stopButton.setOnClickListener(stopClickListener);
        boolean OnOff = BGDFGDRECORD.getBoolean(Common.BGDFGDRECORDKEYUP, false);
        Text = (TextView) findViewById(R.id.bgdfgdText);
        String msg = "It is not yet up";
        if(OnOff)
            msg = "It is up";
        Text.setText(msg);
    }
    private Button.OnClickListener startClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            //啟動服務
            Intent intent = new Intent(BgdFgdEnableActivity.this, BgdFgdListenService.class);
            startService(intent);
        }
    };

    private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
        public void onClick(View arg0) {
            //停止服務
            Intent intent = new Intent(BgdFgdEnableActivity.this, BgdFgdListenService.class);
            stopService(intent);
        }
    };
}
