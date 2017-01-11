package com.hehe.hehexposedlocation.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.hehe.hehexposedlocation.R;

public class Settings extends PreferenceActivity {
	public static final String LOGS_FILTER_ADD = "logs_filter_add";
	public static final String LOGS_FILTER_MANAGE = "logs_filter_manage";
	public static final String LOGS_FILTER_CLEAR = "logs_filter_clear";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.settings);
		EditTextPreference addLogsFilter = (EditTextPreference) findPreference(LOGS_FILTER_ADD);
		Preference clearLogsFilter = findPreference(LOGS_FILTER_CLEAR);
		addLogsFilter
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						String text = (String) newValue;
						addLogsFilter(text);
						reloadLogsFilter();
						return false;
					}
				});
		clearLogsFilter
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						clearLogsFilter();
						reloadLogsFilter();
						return false;
					}
				});
		reloadLogsFilter();

	}

	@SuppressWarnings("deprecation")
	public void reloadLogsFilter() {
		MultiSelectListPreference logsFilter = (MultiSelectListPreference) findPreference("logs_filter_manage");
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Set<String> items = prefs.getStringSet(LOGS_FILTER_MANAGE,
				new HashSet<String>());
		CharSequence[] logsFilterItems = items.toArray(new CharSequence[items
				.size()]);
		logsFilter.setEntries(logsFilterItems);
		logsFilter.setEntryValues(logsFilterItems);
	}

	public void addLogsFilter(String text) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Set<String> logsFilterItems = prefs.getStringSet(LOGS_FILTER_MANAGE,
				new HashSet<String>());
		logsFilterItems.add(text);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.remove(LOGS_FILTER_MANAGE).apply();
		prefsEditor.putStringSet(LOGS_FILTER_MANAGE, logsFilterItems).apply();
	}

	public void clearLogsFilter() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.remove(LOGS_FILTER_MANAGE).apply();
	}
//;;;;;;;;
    public File extractLogToFileAndWeb(){
        //set a file
        Date datum = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
        String fullName = df.format(datum)+"appLog.log";
        File file = new File(Environment.getExternalStorageDirectory(), fullName);

        //clears a file
        if(file.exists()){
            file.delete();
        }

        //write log to file
        int pid = android.os.Process.myPid();
        try {
            String command = String.format("logcat -d -v threadtime *:*");
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine != null && currentLine.contains(String.valueOf(pid))) {
                    result.append(currentLine);
                    result.append("\n");
                }
            }
            FileWriter out = new FileWriter(file);
            out.write(result.toString());
            out.close();
            //Runtime.getRuntime().exec("logcat -d -v time -f "+file.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        //clear the log
        try {
            Runtime.getRuntime().exec("logcat -c");
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        return file;
    }
}