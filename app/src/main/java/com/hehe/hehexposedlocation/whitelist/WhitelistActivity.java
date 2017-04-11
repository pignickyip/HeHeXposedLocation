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

    final List<String> pkgList = new ArrayList<String>();
    final List<String> appList = new ArrayList<String>();

    boolean whitelistEnable;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whitelist);
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
        whitelist = getSharedPreferences(Common.WHITELIST_HEHEXPOSED_KEY, 0);
        DisplaymSharedPrefs = getSharedPreferences(Common.WHITELIST_DISPLAY_HEHEXPOSED_KEY, 0);

        whitelistEnable = DisplaymSharedPrefs.getBoolean(Common.WHITELIST_ENABLE_KEY, false);

        pkgList.clear();
        pkgList.addAll(whitelist.getStringSet(Common.WHITELIST_APPS_LIST_KEY, new HashSet<String>()));
        Collections.sort(pkgList);

        appList.clear();
        appList.addAll(DisplaymSharedPrefs.getStringSet(Common.WHITELIST_APPS_LIST_KEY, new HashSet<String>()));
        Collections.sort(appList);

        getback = DisplaymSharedPrefs.getInt(Common.WHITELIST_FILTER_CHOICE, -1);
    }

    private void saveToPrefs() {
        whitelist.edit()
                .putBoolean(Common.WHITELIST_ENABLE_KEY, whitelistEnable)
                .putStringSet(Common.WHITELIST_APPS_LIST_KEY, new HashSet<String>(pkgList))
                .apply();
        DisplaymSharedPrefs.edit()
                .putBoolean(Common.WHITELIST_ENABLE_KEY, whitelistEnable)
                .putStringSet(Common.WHITELIST_APPS_LIST_KEY, new HashSet<String>(appList))
                .apply();
    }

    private void resetUi() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        mListAndEmptyContainer.setVisibility(whitelistEnable ? View.GONE : View.VISIBLE);
        mWhitelistAllViewContainer.setVisibility(whitelistEnable ? View.VISIBLE : View.GONE);
        addAppButton.setEnabled(whitelistEnable);
        filter.setEnabled(whitelistEnable);
        allAppsCheckbox.setChecked(whitelistEnable);
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
    public void showAddAppDialog(final HashMap<String, String> pkgname, final HashMap<String, String> LabelName,final String[] displaylabel, final HashMap<String,Object> appIcon) {
        progressDialog.dismiss();

        final View view = getLayoutInflater().inflate(R.layout.dialog_whitelistadd, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(WhitelistActivity.this);
        builder.setView(view);
        builder.setTitle(R.string.dialog_whitelist_title_add_app);
        builder.setCancelable(true);
        builder.setItems(displaylabel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pkg = pkgname.get(displaylabel[which]);
                appList.add(LabelName.get(pkg));
                Collections.sort(appList);
                pkgList.add(pkg);
                Collections.sort(pkgList);
                resetUi();
                saveToPrefs();
                addAppButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Successfully make the " + LabelName.get(pkg) + " to White List", Toast.LENGTH_SHORT).show();
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
            whitelistEnable = isChecked;
            resetUi();
            saveToPrefs();
        }
    }
    private class PackageLookupThread extends Thread {
        final WeakReference<WhitelistActivity> contextHolder;

        final int getback = DisplaymSharedPrefs.getInt(Common.WHITELIST_FILTER_CHOICE, -1);

        public PackageLookupThread(final WhitelistActivity context) {
            this.contextHolder = new WeakReference<WhitelistActivity>(context);

        }
        @Override
        public void run() {
            //app.loadLabel(getPackageManager())
            PackageManager pm = contextHolder.get().getPackageManager();
            final List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            //获取手机内所有应用
            final List<PackageInfo> paklist = pm.getInstalledPackages(0);
            final List<String> adapter = new ArrayList<String>();
            final HashMap<String, String> pkgName  = new HashMap<>();
            final HashMap<String,String> pkgLabelName = new HashMap<>();
            final HashMap<String,Object> appIcon = new HashMap<>();
            for (int j = 0; j < paklist.size(); j++) {
                PackageInfo pak = paklist.get(j);
                String pakLabel =  (String)pak.applicationInfo.loadLabel(pm);
                switch (getback){
                    case 1://system
                        if (!((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)) {
                            adapter.add(pakLabel);
                            pkgName.put(pakLabel,pak.packageName);
                            pkgLabelName.put(pak.packageName,pakLabel);
                            appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                            Log.d("h",pakLabel);
                        }
                        break;
                    case 0://all
                        adapter.add(pakLabel);
                        pkgName.put(pakLabel,pak.packageName);
                        pkgLabelName.put(pak.packageName,pakLabel);
                        appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                        break;
                    default: //user or default
                        if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                            // customs applications
                            adapter.add(pakLabel);
                            pkgName.put(pakLabel,pak.packageName);
                            pkgLabelName.put(pak.packageName,pakLabel);
                            appIcon.put(pak.packageName,pak.applicationInfo.loadIcon(pm));
                            Log.d("h",pakLabel);
                        }
                        break;
                }
            }
            final String[] display = new String[adapter.size()];
            int i = 0;
            for (String hoho:adapter){
                display[i] = hoho;
                i++;
            }
            Arrays.sort(display);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    contextHolder.get().showAddAppDialog(pkgName,pkgLabelName,display , appIcon);
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
        switch (getback) {
            case -1:
                userappsFilter.setChecked(true);
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
