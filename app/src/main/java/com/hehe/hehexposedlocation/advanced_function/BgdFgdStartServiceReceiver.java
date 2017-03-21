package com.hehe.hehexposedlocation.advanced_function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 */

public class BgdFgdStartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("HEHEXPOSED_TEST","23123213213213131");
        Log.i(BgdFgdStartServiceReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        BgdFgdListenService mBoundService = new BgdFgdListenService(context);
        Intent service = new Intent(context, mBoundService.getClass());
       // Intent service = new Intent(context, BgdFgdListenService.class);
        context.startService(service);
    }
}