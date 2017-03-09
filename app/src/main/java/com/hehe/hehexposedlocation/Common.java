package com.hehe.hehexposedlocation;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

//import hehexposed.BuildConfig;

import com.hehe.hehexposedlocation.DefaultLists;
import com.hehe.hehexposedlocation.def_setting.DefActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;


public class Common {
    public static final String SYSTEM_LOCATION = "android.location.Location";
    public static final String SYSTEM_LOCATION_MANGER = "android.location.LocationManager";
    public static final String SYSTEM_LOCATION_LISTENER = "android.location.LocationListener";

    public static final String TIME_CATEGORY_GET = "TIME_CATEGORY_GET";
    public static final String TIME_CATEGORY_GET_DISPLAY = "TIME_CATEGORY_GET_DISPLAY";

    public static final String SHARED_WHITELIST_PREFERENCES_FILE = "SHARED_WHITELIST_PREFERENCES_FILE";
    public static final String SHARED_WHITELIST_PKGS_PREFERENCES_FILE = "SHARED_WHITELIST_PKGS_PREFERENCES_FILE";

    public static final String SHARED_PREFERENCES_DEFAULT_POSITION = "SHARED_PREFERENCES_DEFAULT_POSITION";
    public static final String SHARED_PREDERENCES_DEFAULT_CUSTOMER = "SHARED_PREDERENCES_DEFAULT_CUSTOMER";
    public static final String SHARED_PREDERENCES_DEFAULT_CUSTOMER_CHECK = "SHARED_PREDERENCES_DEFAULT_CUSTOMER_CHECK";
    public static final String SHARED_PREDERENCES_DEFAULT_CUSTOMER_RECORD = "SHARED_PREDERENCES_DEFAULT_CUSTOMER_RECORD";
    public static final String DISPLAY_SPINNER_DEFAULT_RESTARTBUTTON = "DISPLAY_SPINNER_DEFAULT_RESTARTBUTTON";

    public static final String SHARE_APPLICATIONS_TYPE = "APPLICATIONTYPE";
    public static final String PREF_KEY_WHITELIST_ALL = "whitelist_all_apps";
    public static final String PREF_KEY_WHITELIST_APP_LIST = "whitelist_apps_list";
    public static final String PREFS_SETTINGS = "CustomizeSettings";

    public static final String USER_PACKET_NAME = "USER_PACKET_NAME";
    public static final String USER_PACKET_NAME_KEY = "USER_PACKET_NAME_KEY";
    public static final String USER_PACKET_NAME_KEY_ALL = "USER_PACKET_NAME_KEY_ALL";
    public static final String SYSTEM_PACKET_NAME = "SYSTEM_PACKET_NAME";
    public static final String SYSTEM_PACKET_NAME_KEY = "SYSTEM_PACKET_NAME_KEY";
    public static final String SYSTEM_PACKET_NAME_KEY_ALL = "SYSTEM_PACKET_NAME_KEY_ALL";

    public static final String WEB_CONTENT = "WEB_CONTENT";
    public static final String WEB_CONTENT_KEY = "WEB_CONTENT_KEY";

    public static final String FEEDBACK_COMFORTABLE = "FEEDBACK_COMFORTABLE";
    public static final String FEEDBACK_COMFORTABLE_KEY = "FEEDBACK_COMFORTABLE_KEY";
    public static final String FEEDBACK_COMFORTABLE_CHECK = "FEEDBACK_COMFORTABLE_CHECK";

    public static final String MODE_REST_SETUP = "MODE_REST_SETUP";
    public static final String MODE_REST_SETUP_KEY = "MODE_REST_SETUP_KEY";
    public static final String MODE_REST_SETUP_STARTTIME_KEY =  "MODE_REST_SETUP_STARTTIME_KEY";
    public static final String MODE_REST_SETUP_STARTTIME_KEY_HOUR = "MODE_REST_SETUP_STARTTIME_KEY_HOUR";
    public static final String MODE_REST_SETUP_STARTTIME_KEY_MINUTES = "MODE_REST_SETUP_STARTTIME_KEY_MINUTES";
    public static final String MODE_REST_SETUP_ENDTIME_KEY = "MODE_REST_SETUP_ENDTIME_KEY";
    public static final String MODE_REST_SETUP_ENDTIME_KEY_HOUR = "MODE_REST_SETUP_ENDTIME_KEY_HOUR";
    public static final String MODE_REST_SETUP_ENDTIME_KEY_MINUTES = "MODE_REST_SETUP_ENDTIME_KEY_MINUTES";
    public static final String MODE_WORK_SETUP = "MODE_WORK_SETUP";
    public static final String MODE_WORK_SETUP_KEY = "MODE_WORK_SETUP_KEY";
    public static final String MODE_WORK_SETUP_STARTTIME_KEY =  "MODE_WORK_SETUP_STARTTIME_KEY";
    public static final String MODE_WORK_SETUP_STARTTIME_KEY_HOUR = "MODE_WORK_SETUP_STARTTIME_KEY_HOUR";
    public static final String MODE_WORK_SETUP_STARTTIME_KEY_MINUTES = "MODE_WORK_SETUP_STARTTIME_KEY_MINUTES";
    public static final String MODE_WORK_SETUP_ENDTIME_KEY = "MODE_WORK_SETUP_ENDTIME_KEY";
    public static final String MODE_WORK_SETUP_ENDTIME_KEY_HOUR = "MODE_WORK_SETUP_ENDTIME_KEY_HOUR";
    public static final String MODE_WORK_SETUP_ENDTIME_KEY_MINUTES = "MODE_WORK_SETUP_ENDTIME_KEY_MINUTES";

    public static final String BGDFGDRECORDKEY = "BGDFGDRECORDKEY";
    public static final String BGDFGDRECORDKEYUP = "BGDFGDRECORDKEYUP";
    public static final String BGDFGDAPPLICATION = "BGDFGDAPPLICATION";
    public static final String BGDFGDAPPLICATIONID = "BGDFGDAPPLICATIONID";
    public static final String CURRENTAPPLICATION = "CURRENTAPPLICATION";

    public static final String PASSWORD_SETTING = "PASSWORD_SETTING";
    public static final String PASSWORD_SETTING_ON = "PASSWORD_SETTING_ON";
    public static final String PASSWORD_ALREADY_UP = "PASSWORD_ALREADY_UP";
    public static final String PASSWORD_PIN_CODE = "PASSWORD_PIN_CODE";

    // public static final PrefSet APPS = new AppsSet();
    public static final PrefSet KEYWORDS = new KeywordSet();
    public static final PrefSet COMMANDS = new CommandSet();
    public static final PrefSet LIBRARIES = new LibrarySet();

    public static abstract class PrefSet {
        abstract String getPrefKey();
        abstract String getSetKey();
        abstract Set<String> getDefaultSet();

        public SharedPreferences getSharedPreferences(PreferenceActivity activity) {
            activity.getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
            return activity.getSharedPreferences(getPrefKey(), Context.MODE_WORLD_READABLE);
        }
    }

    public static class KeywordSet extends PrefSet {
        public static final String PREFS_KEYWORDS = "CustomizeKeywords";
        public static final String KEYWORD_SET_KEY = PACKAGE_NAME+"KEYWORD_SET";
        public static final Set<String> DEFAULT_KEYWORD_SET = new HashSet<String>(Arrays.asList(DefaultLists.DEFAULT_KEYWORD_LIST));

        @Override
        public String getPrefKey() {
            return PREFS_KEYWORDS;
        }
        @Override
        public String getSetKey() {
            return KEYWORD_SET_KEY;
        }
        @Override
        public Set<String> getDefaultSet() {
            return DEFAULT_KEYWORD_SET;
        }
    }

    public static class CommandSet extends PrefSet {
        public static final String PREFS_COMMANDS = "CustomizeCommands";
        public static final String COMMAND_SET_KEY = PACKAGE_NAME+"APPS_SET";
        public static final Set<String> DEFAULT_COMMAND_SET = new HashSet<String>(Arrays.asList(DefaultLists.DEFAULT_COMMAND_LIST));

        @Override
        public String getPrefKey() {
            return PREFS_COMMANDS;
        }
        @Override
        public String getSetKey() {
            return COMMAND_SET_KEY;
        }
        @Override
        public Set<String> getDefaultSet() {
            return DEFAULT_COMMAND_SET;
        }
    }

    public static class LibrarySet extends PrefSet {
        public static final String PREFS_LIBNAMES = "CustomizeLibnames";
        public static final String LIBRARY_SET_KEY = "LIBNAMES_SET";
        public static final Set<String> DEFAULT_LIBNAME_SET = new HashSet<String>(Arrays.asList(DefaultLists.DEFAULT_LIBNAME_LIST));

        @Override
        public String getPrefKey() {
            return PREFS_LIBNAMES;
        }
        @Override
        public String getSetKey() {
            return LIBRARY_SET_KEY;
        }
        @Override
        public Set<String> getDefaultSet() {
            return DEFAULT_LIBNAME_SET;
        }
    }
}

