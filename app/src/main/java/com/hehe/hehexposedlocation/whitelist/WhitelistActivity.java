package com.hehe.hehexposedlocation.whitelist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hehe.hehexposedlocation.Common;
import com.hehe.hehexposedlocation.R;

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
    @BindView(R.id.btntest) ImageButton filter;

    RadioGroup filtergp ;
    RadioButton allappsFilter ;
    RadioButton systemappsFilter ;
    RadioButton userappsFilter ;

    ArrayAdapter<String> mAdapter;
    SharedPreferences whitelist;
    SharedPreferences DisplaymSharedPrefs;
    static int getback;

    private static HashMap<String,Object> appIcon = new HashMap<>();
    final List<String> pkgList = new ArrayList<String>();
    final List<String> appList = new ArrayList<String>();

    boolean whitelistAllApps;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist2);
        ButterKnife.bind(this);

        init();
        resetUi();

        final ToolbarListener listener = new ToolbarListener();
        addAppButton.setOnClickListener(listener);
        allAppsCheckbox.setOnCheckedChangeListener(listener);
        filter.setOnClickListener(listener);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList);
        setListAdapter(mAdapter);
    }

    private void init() {

        this.getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        whitelist = getSharedPreferences(Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE, 0);
        DisplaymSharedPrefs = getSharedPreferences(Common.SHARED_WHITELIST_PREFERENCES_FILE, 0);

        whitelistAllApps = DisplaymSharedPrefs.getBoolean(Common.PREF_KEY_WHITELIST_ALL, true);

        pkgList.clear();
        pkgList.addAll(whitelist.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(pkgList);

        appList.clear();
        appList.addAll(DisplaymSharedPrefs.getStringSet(Common.PREF_KEY_WHITELIST_APP_LIST, new HashSet<String>()));
        Collections.sort(appList);

        getback = DisplaymSharedPrefs.getInt(Common.WHITELIST_FILTER_CHOICE, -1);
    }

    private void saveToPrefs() {
        whitelist.edit()
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

        builder.show();

    }
    private RadioGroup.OnCheckedChangeListener rglistener = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            int buttonId = group.getCheckedRadioButtonId();
            SharedPreferences.Editor pe = DisplaymSharedPrefs.edit();
            // find the radiobutton by returned id
            RadioButton returnvalue = (RadioButton) findViewById(buttonId);
            switch(buttonId) {
                case R.id.allapps_show_whitelist:
                    pe.putInt(Common.WHITELIST_FILTER_CHOICE, 0);
                    break;
                case R.id.systemapps_show_whitelist:
                    pe.putInt(Common.WHITELIST_FILTER_CHOICE, 1);
                    break;
                case R.id.userapps_show_whitelist:
                    pe.putInt(Common.WHITELIST_FILTER_CHOICE, 2);
                    break;
            }
            pe.apply();
        }
    };
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
                case R.id.btntest:{
                    showfilterDialog();
                    break;
                }
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
        final List<String> UserpkgName = new ArrayList<String>();
        final List<String> SyspkgName = new ArrayList<String>();

        public PackageLookupThread(final WhitelistActivity context) {
            this.contextHolder = new WeakReference<WhitelistActivity>(context);

        }
        @Override
        public void run() {
            //app.loadLabel(getPackageManager())
            Log.d("h",getback+"");
            PackageManager pm = contextHolder.get().getPackageManager();
            final List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            final String[] names = new String[packages.size()];
            final HashMap<String, String> nameMap = new HashMap<>();
            final HashMap<String, String> allpkgMap = new HashMap<>();

            //获取手机内所有应用
            List<PackageInfo> paklist = pm.getInstalledPackages(0);
            for (int j = 0; j < paklist.size(); j++) {
                PackageInfo pak = paklist.get(j);
                switch (getback){
                    case 0://all
                        allpkgMap.put(names[j], pak.packageName);
                        // applicationInfo is subclass of packageinfo
                        names[j] = (String)pak.applicationInfo.loadLabel(pm);
                        nameMap.put(pak.packageName,names[j]);

                        appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                        Log.d("h",names[j]);
                        break;
                    case 1://system
                        if (!((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)) {
                            SyspkgName.add(pak.packageName);
                            // applicationInfo is subclass of packageinfo
                            names[j] = (String)pak.applicationInfo.loadLabel(pm);
                            nameMap.put(pak.packageName,names[j]);

                            appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                            Log.d("h",names[j]);
                        }
                        break;
                    case 2://user
                        if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                            // customs applications
                            UserpkgName.add(pak.packageName);
                            // applicationInfo is subclass of packageinfo
                            names[j] = (String)pak.applicationInfo.loadLabel(pm);
                            nameMap.put(pak.packageName,names[j]);

                            appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                            Log.d("h",names[j]);
                        }
                        break;
                    default:
                        allpkgMap.put(names[j], pak.packageName);
                        // applicationInfo is subclass of packageinfo
                        names[j] = (String)pak.applicationInfo.loadLabel(pm);
                        nameMap.put(pak.packageName,names[j]);

                        appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                        Log.d("h",names[j]);
                        break;
                }
            }
            Arrays.sort(names);
            Collections.sort(UserpkgName);
            Collections.sort(SyspkgName);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    contextHolder.get().showAddAppDialog(nameMap,allpkgMap, names);
                }
            });
        }
    }
    private void showfilterDialog(){
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(WhitelistActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_whitelist_filter, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WhitelistActivity.this);
        alertDialogBuilder.setView(promptView);

        filtergp = (RadioGroup) promptView.findViewById(R.id.filterRadiogroup);
        allappsFilter = (RadioButton) promptView.findViewById(R.id.allapps_show_whitelist);
        systemappsFilter = (RadioButton) promptView.findViewById(R.id.systemapps_show_whitelist);
        userappsFilter = (RadioButton) promptView.findViewById(R.id.userapps_show_whitelist);
        filtergp.setOnCheckedChangeListener(rglistener);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        getback = DisplaymSharedPrefs.getInt(Common.WHITELIST_FILTER_CHOICE, -1);
        Log.d("Ad",getback+"");
        switch (getback) {
            case -1:
                break;
            case 0:
                allappsFilter.setChecked(true);
                break;
            case 1:
                systemappsFilter.setChecked(true);
                break;
            case 2:
                userappsFilter.setChecked(true);
                break;
            default:
                break;
        }
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
