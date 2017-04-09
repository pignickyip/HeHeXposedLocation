package com.hehe.hehexposedlocation.introduction;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hehe.hehexposedlocation.BuildConfig;
import com.hehe.hehexposedlocation.R;
import com.hehe.hehexposedlocation.whitelist.WhitelistActivity;

import java.util.Hashtable;


public class InstructionsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String API_KEY = "751426042441-k7lpb2dh90urck1acc1m8oe74u283srp.apps.googleusercontent.com";

    String msg = "";

    //https://www.youtube.com/watch?v=<VIDEO_ID>
    private static final String BasicSettingVideo = "1s9p7_mDw1Q";
    private static final String ModeSettingVideo = "1s9p7_mDw1Q";

    Hashtable<String, Boolean> DisplayVideoList = new Hashtable<String, Boolean>();

    TextView instruction_intro;
    TextView videoIntro1;
    TextView videoIntro2;
    Button basicSettingText;
    Button modeSettingText;


    Context ctx;
    private Context getCtx() {
        if(ctx != null) {
            return ctx;
        }
        ctx = InstructionsActivity.this;
        return ctx;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        ctx = this;
        new AlertDialog.Builder(this)
                .setMessage("Here to show the basic setting of HeHeXposed")
                .setTitle("Introduction")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
        instruction_intro = (TextView) findViewById(R.id.instruction_intro);
        videoIntro1 = (TextView) findViewById(R.id.videoIntro1);
        videoIntro2 = (TextView) findViewById(R.id.videoIntro2);
        basicSettingText = (Button) findViewById(R.id.basicsettingText);
        modeSettingText = (Button) findViewById(R.id.modesettingText);

        final InstructionsActivity.ToolbarListener listener = new InstructionsActivity.ToolbarListener();
        basicSettingText.setOnClickListener(listener);
        modeSettingText.setOnClickListener(listener);

        msg = "Please follow those step to set up HeHeXpsoed";
        instruction_intro.setText(msg);
        msg = "Basic setting";
        videoIntro1.setText(msg);
        basicSettingText.setText(msg);
        msg = "Advanced setting";
        videoIntro2.setText(msg);
        modeSettingText.setText(msg);

        DisplayVideoList.put("HI",true);
        //Initializing and adding YouTubePlayerFragment
        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        YouTubePlayerFragment playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);
        youtubefragementoption(playerFragment,fm,tag, 1);
        youtubefragementoption(playerFragment,fm,tag, 2);
    }

    private class ToolbarListener implements View.OnClickListener{
        String msg = "";
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.basicsettingText:
                    msg = "......";
                    new AlertDialog.Builder(InstructionsActivity.this)
                            .setMessage(msg)
                            .setTitle("Basic Setting")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    break;
                case R.id.modesettingText:
                    msg = ".......";
                    new AlertDialog.Builder(InstructionsActivity.this)
                            .setMessage(msg)
                            .setTitle("Advance Setting")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                    break;
                default:
                    break;
                }
            }
        }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
    public void youtubefragementoption(YouTubePlayerFragment playerFragment, FragmentManager fm, String tag , final int option){
        if (playerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            playerFragment = YouTubePlayerFragment.newInstance();
            //ft.add(android.R.id.content, playerFragment, tag);
            int ressourceId = getResources().getIdentifier("video" + option, "id", ctx.getPackageName());
            ft.add(ressourceId, playerFragment, tag);
            ft.commit();
        }
        playerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                //youTubePlayer.cueVideo(VIDEO_ID);
                if(null== player) return;

                // Start buffering
                switch (option){
                    case 1:
                        player.cueVideo(BasicSettingVideo);
                        break;
                    case 2:
                        player.cueVideo(ModeSettingVideo);
                        break;
                    default:
                        player.cueVideo(BasicSettingVideo);
                        break;
                }

                player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);

                // Add listeners to YouTubePlayer instance
                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override public void onAdStarted() { }
                    @Override public void onError(YouTubePlayer.ErrorReason arg0) { }
                    @Override public void onLoaded(String arg0) { }
                    @Override public void onLoading() { }
                    @Override public void onVideoEnded() { }
                    @Override public void onVideoStarted() { }
                });


                player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    @Override public void onBuffering(boolean arg0) { }
                    @Override public void onPaused() { }
                    @Override public void onPlaying() { }
                    @Override public void onSeekTo(int arg0) { }
                    @Override public void onStopped() { }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(InstructionsActivity.this, "Error while initializing YouTubePlayer.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO  WORD-BASED
}
