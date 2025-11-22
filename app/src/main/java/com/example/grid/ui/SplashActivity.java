package com.example.grid.ui;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grid.R;

public class SplashActivity extends AppCompatActivity {
    ImageView iv;
    private boolean player;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setImageResource(R.drawable.title);
        setContentView(iv);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent m){

        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float w = iv.getWidth();
            float h = iv.getHeight();
            float x = m.getX();
            float y = m.getY();

            if (x >= w * 1 / 4 && x < w * 3 / 4 && y >= h / 2 && y <= h * 7 / 10) {
                player = true;
                startGame();
            } else if (x >= w * 1 / 4 && x < w * 3 / 4 && y >= h * 7 / 10 && y <= h * 9 / 10) {
                player = false;
                startGame();
            } else if (x <= w * 1 / 4 && y >= h * 9 / 10) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("How to Play");
                builder.setMessage("Slide Your Pieces: Move your image pieces in straight lines (up, down, left, or right) until they hit an obstacle or edge.\n" +
                        "Strategize: Plan your moves to align five of your pieces in a row, column, or diagonal.\n" +
                        "Block Opponents: If it’s multiplayer, stop others from completing their alignment.\n" +
                        "Win Condition: Be the first to get five of your pieces in a row, column, or diagonal!");
                builder.show();
            } else if (x >= w * 3 / 4 && y >= h * 9 / 10) {
                Intent settings = new Intent(this, Prefs.class);
                startActivity(settings);
            }

        }



        return true;
    }


    private void startGame() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("PLAYER", player);
        startActivity(intent);
        finish();
    }

}

