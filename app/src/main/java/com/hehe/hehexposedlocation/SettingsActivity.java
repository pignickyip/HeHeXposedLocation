package com.hehe.hehexposedlocation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hehe.hehexposedlocation.def_setting.DefActivity;

import java.io.IOException;
import java.math.BigInteger;
import java.net.CookieHandler;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;


public class SettingsActivity extends PreferenceActivity {
    private GoogleApiClient client;

    private String[] menuItems;
    private String instructionsMsg;
    private String instructionsTitle;

    private PackageManager pm = null;

    private SharedPreferences clear = null;
    private SharedPreferences.Editor PE = null;

    private static SharedPreferences UserApplicationFile = null;
    private static SharedPreferences SystemApplicationFile = null;
    private static SharedPreferences WebContent = null;
    private static SharedPreferences GetFileDisplay = null;
    private static SharedPreferences password = null;

    final List<String> UserpkgName = new ArrayList<String>();
    final List<String> SyspkgName = new ArrayList<String>();
    final List<String> ApplicationRate = new ArrayList<String>();
    final List<String> ApplicationRateCount = new ArrayList<String>();
    final List<String> ApplicationNumberDownoad = new ArrayList<String>();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @SuppressLint("WorldReadableFiles")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        GetFileDisplay = getSharedPreferences(Common.TIME_CATEGORY_GET, 0);
        password = getSharedPreferences(Common.PASSWORD_SETTING, 0);

        ApplicationRate.clear();
        ApplicationRateCount.clear();
        ApplicationNumberDownoad.clear();

        Resources res = getResources();
        menuItems = res.getStringArray(R.array.menu_array);
        instructionsMsg = "First, Go to Enable HeHeXposed to choose the setting which specify your needs" +
                "\n Second, Choose those function you want to use\n"
                + "\n Remind that, you should restart the mobile phone to apply change\n"
                + "\n\n";

        instructionsTitle = res.getString(R.string.instructions_title);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, menuItems);
        setListAdapter(adapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onListItemClick(ListView parent, View v, int position, long id) {
        Intent intent;
        Fragment navFragment = null;
        switch (position) {
            case 0: //about me
                String aboutMsg = getString(R.string.app_name) + ": " + BuildConfig.VERSION_NAME
                        + "\n The Hong Kong Polytechnic University \n Student Final Year Project - 2016 "
                        + "\n Smart Location Obfuscation Module for \n Xposed Framework in Android "
                        + "\n Yip Tim Yan";

                new AlertDialog.Builder(this)
                        .setMessage(aboutMsg)
                        .setTitle(R.string.about)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case 1: //Introduction
                intent = new Intent(this, com.hehe.hehexposedlocation.intro.MainActivity.class);
                startActivity(intent);
                break;
            case 2: //Reference List
                String[] ref_ar;
                String ref = getString(R.string.app_name) + ": " + '\n'
                        + "Thanks those module: \n";
                ref_ar = getResources().getStringArray(R.array.ref_list);
                List<String> ha = Arrays.asList(ref_ar);
                new AlertDialog.Builder(this)
                        .setMessage(ref + ha)
                        .setTitle("Reference List")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case 3: //instructions
                new AlertDialog.Builder(this)
                        .setMessage(instructionsMsg)
                        .setTitle(instructionsTitle)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                break;
            case 4: //Default Noise setting
                intent = new Intent(this, com.hehe.hehexposedlocation.def_setting.DefActivity.class);
                startActivity(intent);
                break;
            case 5: //White List
                intent = new Intent(this, WhitelistActivity.class);
                startActivity(intent);
                break;
            case 6:
                String LastTime = GetFileDisplay.getString(Common.TIME_CATEGORY_GET_DISPLAY, "Never");

                new AlertDialog.Builder(this)
                        .setMessage("This action require you connect to network! \nLast time to get the category: " + LastTime)
                        .setTitle("Get the category of all your mobile phone application")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    //http://androidexample.com/Show_Loader_To_Open_Url_In_WebView__-_Android_Example/index.php?view=article_discription&aid=125
                                    GetFile();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case 7:
                intent = new Intent(this, com.hehe.hehexposedlocation.mode.ModeActivity.class);
                startActivity(intent);
                break;
            case 8:
                intent = new Intent(this, com.hehe.hehexposedlocation.feedback.FeedbackActivity.class);
                startActivity(intent);
                break;
            case 9: {
                intent = new Intent(this, com.hehe.hehexposedlocation.advanced_function.BgdFgdEnableActivity.class);
                startActivity(intent);
                break;
            }
            case 10: {
                //clear all setting
                ClearAllSetting();
                break;
            }
            case 11: //Enable
                UserActivityIdentity();
                break;
            case 12:
                intent = new Intent(this, com.hehe.hehexposedlocation.pwd.PwdActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Settings Page") //  Define a title for the content shown.
                // Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void UserActivityIdentity() {
        //TODO http://stackoverflow.com/questions/27058741/detect-user-activity-running-cycling-driving-using-android
        //https://developer.xamarin.com/samples/monodroid/google-services/Location/ActivityRecognition/
        Toast.makeText(getApplicationContext(), "整緊呀 _ 你", Toast.LENGTH_SHORT).show();
    }

    private void ClearAllSetting() {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_clearall, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Clear all setting");
        String msg = "Sure? One Way only";
        final TextView clearall_msg = (TextView) promptView.findViewById(R.id.clearall_text);
        final EditText pwd_auth = (EditText) promptView.findViewById(R.id.clear_text);
        final boolean isUp = password.getBoolean(Common.PASSWORD_SETTING_ON, false);
        if (isUp) {
            clearall_msg.setText(msg + ", please input your password.");
            msg = "Pin";
            pwd_auth.setHint(msg);
        } else {
            clearall_msg.setText(msg);
            pwd_auth.setVisibility(View.INVISIBLE);
        }
        // setup a dialog window
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String adapter = pwd_auth.getText().toString();
                String challenge = EncryptFunction(adapter);
                String real_pwd = password.getString(Common.PASSWORD_PIN_CODE, "");
                if (Objects.equals(challenge, real_pwd) || !isUp) {
                    ClearFunction(Common.SHARED_PREFERENCES_DEFAULT_POSITION);

                    //Customer setting value
                    ClearFunction(Common.SHARED_PREDERENCES_DEFAULT_CUSTOMER);
                    //white list
                    ClearFunction(Common.SHARED_WHITELIST_PREFERENCES_FILE);
                    ClearFunction(Common.SHARED_WHITELIST_PKGS_PREFERENCES_FILE);

                    //Web content
                    ClearFunction(Common.TIME_CATEGORY_GET);
                    ClearFunction(Common.WEB_CONTENT);

                    //Mode
                    ClearFunction(Common.MODE_REST_SETUP);
                    ClearFunction(Common.MODE_WORK_SETUP);

                    ClearFunction(Common.TIME_CATEGORY_GET);

                    Toast.makeText(getApplicationContext(), "Successfully reset all the setting", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();

                //SettingsActivity.this.finish();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
                    /*boolean debugPref = sharedPref.getBoolean(Common.DEBUG_KEY, false);
                    debugPref = !debugPref;
                    sharedPref.edit()
                            .putBoolean(Common.DEBUG_KEY, debugPref)
                            .apply();*/
    }

    private void GetFile() throws InterruptedException {
        UserpkgName.clear();
        SyspkgName.clear();

        //http://blog.csdn.net/feng88724/article/details/6198446
        // Android】获取手机中已安装apk文件信息(PackageInfo、ResolveInfo)(应用图片、应用名、包名等)
        // 查询所有已经安装的应用程序
        pm = this.getPackageManager();

        //获取手机内所有应用
        List<PackageInfo> paklist = pm.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                UserpkgName.add(pak.packageName);
            } else
                SyspkgName.add(pak.packageName);
        }
        Collections.sort(UserpkgName);
        Collections.sort(SyspkgName);

        UserApplicationFile = getSharedPreferences(Common.USER_PACKET_NAME, 0);
        SystemApplicationFile = getSharedPreferences(Common.SYSTEM_PACKET_NAME, 0);

        PE = UserApplicationFile.edit();
        PE.putBoolean(Common.USER_PACKET_NAME_KEY_ALL, true);
        PE.putStringSet(Common.USER_PACKET_NAME_KEY, new HashSet<String>(UserpkgName));
        PE.apply();

        PE = SystemApplicationFile.edit();
        PE.putBoolean(Common.SYSTEM_PACKET_NAME_KEY_ALL, true);
        PE.putStringSet(Common.SYSTEM_PACKET_NAME_KEY, new HashSet<String>(SyspkgName));
        PE.apply();

        List<String> Category = new ArrayList<String>();
        Category.clear();
        WebContent = getSharedPreferences(Common.WEB_CONTENT, 0);

        for (String User : UserpkgName) {
            String temp = WebGet(User);
            if (temp != null) {
                Category.add(User + temp);
            }
        }
        for (String System : SyspkgName) {
            String temp = WebGet(System);
            if (temp != null) {
                Category.add(System + temp);
            }
        }
        Collections.sort(Category);
        Collections.sort(ApplicationRate);
        Collections.sort(ApplicationRateCount);
        Collections.sort(ApplicationNumberDownoad);

        boolean Cant = false;
        if (Category.isEmpty())
            Cant = true;
        PE = WebContent.edit();
        PE.putStringSet(Common.WEB_CONTENT_KEY, new HashSet<String>(Category));
        PE.putStringSet(Common.WEB_CONTENT_RATE, new HashSet<String>(ApplicationRate));
        PE.putStringSet(Common.WEB_CONTENT_RATE_COUNT, new HashSet<String>(ApplicationRateCount));
        PE.putStringSet(Common.WEB_CONTENT_NUMDOWNLOAD, new HashSet<String>(ApplicationNumberDownoad));
        PE.apply();

        if (!Cant) {
            Announcement();
            Toast.makeText(getApplicationContext(), "Category already Get", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "Not successfully get the category", Toast.LENGTH_SHORT).show();
        // https://jsoup.org/cookbook/extracting-data/dom-navigation
        // http://stackoverflow.com/questions/11026937/parsing-particular-data-from-website-in-android

    }

    private String WebGet(final String pkg) throws InterruptedException {
        String Domain = "https://play.google.com";
        String ShortURL = "/store/apps/details?id=";
        String ContrySet = "&hl=en";
        final String url = Domain + ShortURL + pkg + ContrySet;
        final AtomicReference<String> category = new AtomicReference<String>();
        Thread t0 = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc = null;
                try {
                    // Reference code
                    // http://stackoverflow.com/questions/10710442/how-to-get-category-for-each-app-on-device-on-android
                    // http://www.oodlestechnologies.com/blogs/Getting-app-information-from-Google-play-store-by-Url

                    //Problem Solve by
                    //http://stackoverflow.com/questions/7714432/how-do-i-get-returned-value-from-inner-thread-runnable-method-in-java
                    // http://stackoverflow.com/questions/9148899/returning-value-from-thread
                    doc = connect(url);
                    if (doc != null) {
                        // Element link = doc.select("span[itemprop=genre]").first();
                        Element Alink = doc.select("a.category").first();
                        String ge = Alink.text();
                        category.set(ge);

                        Element RateCountElement = doc.select("span.rating-count").first();
                        String RateCount = RateCountElement.text();
                        ApplicationRateCount.add(pkg + RateCount);

                        Element RateElement = doc.select("div.score").first();
                        String Rate = RateElement.text();
                        ApplicationRate.add(pkg + Rate);

                        Element NumberDownloadElement = doc.select("[itemprop=numDownloads]").first();
                        String NumberDownload = NumberDownloadElement.text();
                        ApplicationNumberDownoad.add(pkg + NumberDownload);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t0.start();
        t0.join();
        return category.get();
    }

    private static Document connect(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).timeout(1000 * 3).get();//.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0").referrer("http://www.google.com")
        } catch (NullPointerException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return doc;
    }

    private void Announcement() {
        Calendar cal = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy EEE MMM dd hh:mm:ss");
        String display = dateFormat.format(cal.getTime());

        PE = GetFileDisplay.edit();
        PE.putString(Common.TIME_CATEGORY_GET_DISPLAY, display);
        PE.apply();
    }

    private void ClearFunction(String Key) {
        clear = getSharedPreferences(Key, 0);
        PE = clear.edit();
        PE.clear();
        PE.apply();
        Log.d("Clear", Key);
    }

    private static String EncryptFunction(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, digest);
            String sha = number.toString(16);

            while (sha.length() < 64) {
                sha = "0" + sha;
            }
            return sha;
        } catch (Exception e) {
            Log.e("sha256", e.getMessage());
            return null;
        }
    }
}
