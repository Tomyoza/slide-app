package com.example.grid.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grid.R;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer song;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean player = getIntent().getBooleanExtra("PLAYER", true);
        song = MediaPlayer.create(this, R.raw.bg);
        if (Prefs.getMusicPref(this)){
            song.start();
            song.setLooping(true);
        }

        AppView appView = new AppView(this, null, player);  // Instantiate your custom view
        setContentView(appView);  // Set the custom view as the main content

    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO pause the music
        if (Prefs.getMusicPref(this)){
            song.pause();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //TODO unpause the music
        if (Prefs.getMusicPref(this)){
            song.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (song != null || Prefs.getMusicPref(this)) {
            song.release();
            song = null;
        }
    }


}