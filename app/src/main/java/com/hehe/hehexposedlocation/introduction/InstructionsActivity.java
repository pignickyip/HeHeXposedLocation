package com.hehe.hehexposedlocation.introduction;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.hehe.hehexposedlocation.R;


public class InstructionsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String API_KEY = "751426042441-k7lpb2dh90urck1acc1m8oe74u283srp.apps.googleusercontent.com";

    //https://www.youtube.com/watch?v=<VIDEO_ID>
    public static final String VIDEO_ID = "-m3V8w_7vhk";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_player);

        new AlertDialog.Builder(this)
                .setMessage("Here to show the basic setting of HeHeXposed")
                .setTitle("Introduction")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
        //Initializing and adding YouTubePlayerFragment
        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        YouTubePlayerFragment playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);
        if (playerFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            playerFragment = YouTubePlayerFragment.newInstance();
            //ft.add(android.R.id.content, playerFragment, tag);
            ft.add(R.id.video1, playerFragment, tag);
            ft.commit();
        }

        playerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                //youTubePlayer.cueVideo(VIDEO_ID);
                if(null== player) return;

                // Start buffering
                if (!wasRestored) {
                    player.cueVideo(VIDEO_ID);
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

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
