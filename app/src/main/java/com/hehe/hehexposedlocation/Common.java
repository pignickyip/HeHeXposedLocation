package com.hehe.hehexposedlocation;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

//import hehexposed.BuildConfig;

import com.hehe.hehexposedlocation.DefaultLists;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;


public class Common {
    public static final String SHARED_PREFERENCES_FILE = "mockmocklocations";
    public static final String PREF_KEY_WHITELIST_ALL = "whitelist_all_apps";
    public static final String PREF_KEY_WHITELIST_APP_LIST = "whitelist_apps_list";
    public static final String PREFS_SETTINGS = "CustomizeSettings";
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String FIRST_RUN_KEY = Common.PACKAGE_NAME + "IS_FIRST_RUN";
    public static final String DEBUG_KEY = Common.PACKAGE_NAME + "DEBUGGERPREF";
    public static final String SHOW_WARNING = "SHOW_WARNING";

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

