package com.example.grid.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.grid.R;

public class Prefs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Handle the back button
     * @param item MenuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the music preference
     * @param c context
     * @return
     */
    public static boolean getMusicPref(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean("MUSIC_PREF", false);
    }

    /**
     * Get the animation speed preference
     * @param context Context
     * @return
     */
    public static String getAnimationSpeedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("ANIMATION_SPEED", "medium");
    }

    /**
     * Fragment for settings
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String s) {
            Context context = getPreferenceManager().getContext();
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

            //TODO add preference widgets here
            SwitchPreference music = new SwitchPreference(context);
            music.setTitle( R.string.music_title);
            music.setSummaryOn(getString(R .string .music_summary_on));
            music.setSummaryOff(getString(R .string .music_summary_off));
            music.setDefaultValue(false);
            music.setKey("MUSIC_PREF");
            screen.addPreference(music);

            // Animation Speed Preference (List)
            ListPreference animationSpeed = new ListPreference(context);
            animationSpeed.setTitle(getString(R.string.animation_speed_title));
            animationSpeed.setSummary(getString(R.string.animation_speed_summary));
            animationSpeed.setEntries(R.array.animation_speed_options);
            animationSpeed.setEntryValues(R.array.animation_speed_values);
            animationSpeed.setDefaultValue("medium");
            animationSpeed.setKey("ANIMATION_SPEED");
            screen.addPreference(animationSpeed);

            setPreferenceScreen(screen);
        }
    }
}
