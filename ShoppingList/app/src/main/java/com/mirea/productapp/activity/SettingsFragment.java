

package com.mirea.productapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.mirea.productapp.R;
import com.mirea.productapp.dialog.DirectoryChooser;

import org.jetbrains.annotations.NotNull;

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_DIRECTORY_LOCATION = "FILE_LOCATION";
    private static final int REQUEST_CODE_CHOOSE_DIR = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // show the current value in the settings screen
        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onActivityCreated(@NotNull Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Preference fileLocationPref = findPreference("FILE_LOCATION");

        fileLocationPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), DirectoryChooser.class);
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_DIR);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory cat = (PreferenceCategory) p;
            for (int i = 0; i < cat.getPreferenceCount(); i++) {
                initSummary(cat.getPreference(i));
            }
        } else {
            updatePreferences(p);
        }
    }

    private void updatePreferences(Preference p) {
        if (KEY_DIRECTORY_LOCATION.equals(p.getKey())) {
            String path = getSharedPreferences().getString(KEY_DIRECTORY_LOCATION, "");
            p.setSummary(path);
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }

    private SharedPreferences getSharedPreferences() {
        FragmentActivity activity = getActivity();
        assert activity != null;
        return PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_DIRECTORY_LOCATION)) {
            Preference p = findPreference(key);
            updatePreferences(p);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_CHOOSE_DIR): {
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(DirectoryChooser.SELECTED_PATH);
                    SharedPreferences.Editor editor = getSharedPreferences().edit();
                    editor.putString(KEY_DIRECTORY_LOCATION, path).apply();
                }
                break;
            }
        }
    }
}
