package com.hehe.hehexposedlocation;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WhitelistActivity extends PreferenceActivity {

    // These 2 are mutually exclusive
    @BindView(R.id.list_and_empty_container) View mListAndEmptyContainer;
    @BindView(R.id.whitelist_all_view_container) View mWhitelistAllViewContainer;

    @BindView(R.id.whitelist_all_view) View mWhitelistAllView;
    @BindView(R.id.add_app) Button addAppButton;
    @BindView(R.id.all_apps) CheckBox allAppsCheckbox;

    ArrayAdapter<String> mAdapter;
    SharedPreferences mSharedPrefs;
    SharedPreferences DisplaymSharedPrefs;

    private static HashMap<String,Object> appIcon = new HashMap<>();
    final List<String> pkgList = new ArrayList<String>();
    final List<String> appList = new ArrayList<String>();
    boolean whitelistAllApps;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
        ButterKnife.bind(this);

        this.getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        mSharedPrefs = getSharedPreferences(Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE, MODE_WORLD_READABLE);
        DisplaymSharedPrefs = getSharedPreferences(Common.SHARED_WHITELIST_PREFERENCES_FILE, MODE_WORLD_READABLE);
        loadFromPrefs();
        resetUi();

        final ToolbarListener listener = new ToolbarListener();
        addAppButton.setOnClickListener(listener);
        allAppsCheckbox.setOnCheckedChangeListener(listener);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList);
        setListAdapter(mAdapter);
    }

    private void loadFromPrefs() {
        whitelistAllApps = DisplaymSharedPrefs.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true);

        pkgList.clear();
        pkgList.addAll(mSharedPrefs.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(pkgList);

        appList.clear();
        appList.addAll(DisplaymSharedPrefs.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(appList);
    }

    private void saveToPrefs() {
        mSharedPrefs.edit()
                .putBoolean(Common.PREF_KEY_WHITELIST_ALL, whitelistAllApps)
                .putStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>(pkgList))
                .apply();
        DisplaymSharedPrefs.edit()
                .putBoolean(Common.PREF_KEY_WHITELIST_ALL, whitelistAllApps)
                .putStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>(appList))
                .apply();
        Toast.makeText(this, R.string.restart_required, Toast.LENGTH_SHORT).show();
    }

    private void resetUi() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mListAndEmptyContainer.setVisibility(whitelistAllApps ? View.GONE : View.VISIBLE);
        mWhitelistAllViewContainer.setVisibility(whitelistAllApps ? View.VISIBLE : View.GONE);
        addAppButton.setEnabled(!whitelistAllApps);
        allAppsCheckbox.setChecked(whitelistAllApps);
    }

    @Override
    protected void onListItemClick(ListView l, View v, final int position, long id) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_app_title)
                .setMessage(R.string.remove_app_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        appList.remove(position);
                        resetUi();
                        saveToPrefs();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showAddAppDialog(final HashMap<String, String> nameMap, final HashMap<String, String> pkgMap,final String[] sortedNames) {
        progressDialog.dismiss();
        final View view = getLayoutInflater().inflate(R.layout.dialog_whitelistadd, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(WhitelistActivity.this);
        builder.setView(view);
        builder.setTitle(R.string.dialog_whitelist_title_add_app);
        builder.setCancelable(true);
        builder.setItems(sortedNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pkg = pkgMap.get(sortedNames[which]);
                        appList.add(nameMap.get(pkg));
                        Collections.sort(appList);
                        pkgList.add(pkg);
                        Collections.sort(pkgList);
                        resetUi();
                        saveToPrefs();
                        addAppButton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Successfully make the "+sortedNames[which]+" to White List", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        addAppButton.setEnabled(true);
                    }
        });
        /*builder.setPositiveButton(R.string.whitelist_Search_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) view.findViewById(R.id.searchbox);
                        String cho = editText.getText().toString();
                        boolean checc = false;
                        for(String chec : sortedNames){
                            if(Objects.equals(chec.toLowerCase(), cho.toLowerCase())) {
                                checc = true;
                            }
                        }
                        if(checc) {
                            String pkg = pkgMap.get(cho);
                            appList.add(nameMap.get(pkg));
                            Collections.sort(appList);
                            pkgList.add(nameMap.get(cho));
                            Collections.sort(pkgList);

                            resetUi();
                            saveToPrefs();
                            addAppButton.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Successfully make the " + cho + " to White List", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Wrong word " + cho , Toast.LENGTH_SHORT).show();
                    }
                });*/
        builder.show();

    }

    private class ToolbarListener implements View.OnClickListener, CheckBox.OnCheckedChangeListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.add_app:
                    addAppButton.setEnabled(false);

                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(WhitelistActivity.this);
                        progressDialog.setMessage(getString(R.string.loading_app_list));
                    }
                    progressDialog.show();
                    new PackageLookupThread(WhitelistActivity.this).start();

                    break;
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            whitelistAllApps = isChecked;
            resetUi();
            saveToPrefs();
        }
    }

    private static class PackageLookupThread extends Thread {
        final WeakReference<WhitelistActivity> contextHolder;
        public PackageLookupThread(final WhitelistActivity context) {
            this.contextHolder = new WeakReference<WhitelistActivity>(context);

        }
        @Override
        public void run() {
            //app.loadLabel(getPackageManager())
            PackageManager pm = contextHolder.get().getPackageManager();
            final List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            final String[] names = new String[packages.size()];
            final HashMap<String, String> nameMap = new HashMap<>();
            final HashMap<String, String> pkgMap = new HashMap<>();
            int i = 0;

            for (ApplicationInfo info : packages) {
                //names[i] = info.loadLabel(pm) + "\n(" + info.packageName + ")";
                names[i] = (String)info.loadLabel(pm);
                nameMap.put(info.packageName,names[i]);
                pkgMap.put(names[i], info.packageName);
                appIcon.put(info.packageName,info.loadIcon(pm));
                i++;
            }
            Arrays.sort(names);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    contextHolder.get().showAddAppDialog(nameMap,pkgMap, names);
                }
            });
        }
    }
}
