package com.rodrigo.lock.app.data.source;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.rodrigo.lock.app.Constants;
import com.rodrigo.lock.app.LockApplication;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class Preferences {
    public static final String DEFAULT_VAULTS_DIRECTORY= Environment.getExternalStorageDirectory().toString() + File.separator +"Lock" + File.separator + "Vaults";
    public static final String DEFAULT_EXTRACT_DIRECTORY= Environment.getExternalStorageDirectory().toString()  + File.separator +"Lock" + File.separator + "Unlocked";

    public static final boolean DEFAULT_IS_GRID_VIEW_IN_VAULT=false;

    public static void savePreference(String preference, Boolean value){
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(preference, value);
        editor.commit();
    }
    public static void savePreference(String preference, String value){
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(preference, value);
        editor.commit();
    }



    public static String getPreference(String preference, String defaultValue) {
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        return settings.getString(preference, defaultValue);
    }

    public static Boolean getPreference(String preference, Boolean defaultValue) {
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        return settings.getBoolean(preference, defaultValue);
    }

    public static String getDefaultVaultDirectory(){
        return getPreference(Constants.Preferences.PREFERENCE_VAULT_DIRECTORY, DEFAULT_VAULTS_DIRECTORY);
    }

    public static String getDefaultUnlockDirectory(){
        return getPreference(Constants.Preferences.PREFERENCE_EXTRACT_DIRECTORY, DEFAULT_EXTRACT_DIRECTORY);
    }
    public static Boolean isGridViewInVault(){
        return getPreference(Constants.Preferences.PREFERENCE_IS_GRID_VIEW_IN_VAULT, DEFAULT_IS_GRID_VIEW_IN_VAULT);
    }
    public static void saveGridViewInVault(Boolean value){
        savePreference(Constants.Preferences.PREFERENCE_IS_GRID_VIEW_IN_VAULT, value);
    }




    public static void saveVaultsToSync(Set<String> value){
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(Constants.Preferences.PREFERENCE_VAULTS_TO_SYNC, value);
        editor.commit();
    }

    public static Set<String> getVaultsToSync(){
        SharedPreferences settings  =PreferenceManager.getDefaultSharedPreferences(LockApplication.getAppContext());
        return settings.getStringSet(Constants.Preferences.PREFERENCE_VAULTS_TO_SYNC, new LinkedHashSet<String>());

    }


}
