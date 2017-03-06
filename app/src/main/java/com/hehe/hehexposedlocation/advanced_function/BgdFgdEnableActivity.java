package com.hehe.hehexposedlocation.advanced_function;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

import java.util.List;

/**
 * Background and frontground detect service
 */

public class BgdFgdEnableActivity extends Activity {
    Button startButton = null;
    Button stopButton = null;
    ToggleButton Service = null;
    TextView Text;
    static SharedPreferences BGDFGDRECORD = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgdfgdenable );
        BGDFGDRECORD = getSharedPreferences(Common.BGDFGDRECORDKEY, 0);
        boolean OnOff = BGDFGDRECORD.getBoolean(Common.BGDFGDRECORDKEYUP, false);
        Text = (TextView) findViewById(R.id.bgdfgdText);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        String msg = "It is not yet up";
        String butmsg = "Not yet started";
        if(OnOff) {
            msg = "It is up";
        }
        Text.setText(msg);
        startButton.setOnClickListener(startClickListener);
        stopButton.setOnClickListener(stopClickListener);
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
