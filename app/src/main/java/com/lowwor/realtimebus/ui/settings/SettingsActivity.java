package com.lowwor.realtimebus.ui.settings;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lowwor.realtimebus.R;
import com.orhanobut.logger.Logger;

import java.util.Set;

public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


//    http://stackoverflow.com/questions/26564400/creating-a-preference-screen-with-support-v21-toolbar
//    https://commonsware.com/blog/2012/10/16/conditional-preference-headers.html
//    http://gmariotti.blogspot.com/2013/02/preference-summary-or-secondary-text.html
//    Nested settings need more works.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.preferences_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle(R.string.settings_toolbar_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //update summary
        updatePrefsSummary(sharedPreferences, key);


    }

    private void updatePrefsSummary(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref == null)
            return;


        if (pref instanceof ListPreference) {
            // List Preference
            ListPreference listPref = (ListPreference) pref;
            listPref.setSummary(listPref.getEntry());

        } else if (pref instanceof EditTextPreference) {
            // EditPreference
            if (key.equals(getString(R.string.preferences_settings_key_auto_refresh_interval))) {
                // Set summary to be the user-description for the selected value
                Logger.i("settingsAutoRefreshInterval: "+sharedPreferences.getString(key, ""));
                pref.setSummary(sharedPreferences.getString(key, ""));
            } else {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                editTextPref.setSummary(editTextPref.getText());

            }

        } else if (pref instanceof MultiSelectListPreference) {
            // MultiSelectList Preference
            MultiSelectListPreference mlistPref = (MultiSelectListPreference) pref;
            String summaryMListPref = "";
            String and = "";

            // Retrieve values
            Set<String> values = mlistPref.getValues();
            for (String value : values) {
                // For each value retrieve index
                int index = mlistPref.findIndexOfValue(value);
                // Retrieve entry from index
                CharSequence mEntry = index >= 0
                        && mlistPref.getEntries() != null ? mlistPref
                        .getEntries()[index] : null;
                if (mEntry != null) {
                    // add summary
                    summaryMListPref = summaryMListPref + and + mEntry;
                    and = ";";
                }
            }
            // set summary
            mlistPref.setSummary(summaryMListPref);

        } else if (pref instanceof RingtonePreference) {
            // RingtonePreference
            RingtonePreference rtPref = (RingtonePreference) pref;
            String uri;
            if (rtPref != null) {
                uri = sharedPreferences.getString(rtPref.getKey(), null);
                if (uri != null) {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            this, Uri.parse(uri));
                    pref.setSummary(ringtone.getTitle(this));
                }
            }

        }
//        else if (pref instanceof NumberPickerPreference) {
//            // My NumberPicker Preference
//            NumberPickerPreference nPickerPref = (NumberPickerPreference) pref;
//            nPickerPref.setSummary(nPickerPref.getValue());
//        }
    }

    /*
  * Init summary
  */
    protected void initSummary() {
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initPrefsSummary(getPreferenceManager().getSharedPreferences(),
                    getPreferenceScreen().getPreference(i));
        }
    }

    /*
     * Init single Preference
     */
    protected void initPrefsSummary(SharedPreferences sharedPreferences,
                                    Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initPrefsSummary(sharedPreferences, pCat.getPreference(i));
            }
        } else {
            updatePrefsSummary(sharedPreferences, p.getKey());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        initSummary();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}