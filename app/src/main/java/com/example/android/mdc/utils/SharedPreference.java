package com.example.android.mdc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


/**
 * Created by Jandro on 1/15/2017.
 */

public class SharedPreference {
    Context ctx; SharedPreferences prefs; Editor prefsEditor;
    private static final String prefName = "MDCUserData";
    public SharedPreference(Context context){
        super();
        ctx = context;
        prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    public void setValue(String prefKey, String prefValue){
        prefsEditor.putString(prefKey, prefValue);
        prefsEditor.commit();
    }

    public String getValue(String prefKey){
        return prefs.getString(prefKey, null);
    }

    public void removeValue(String prefKey){
        prefsEditor.remove(prefKey);
        prefsEditor.commit();
    }

    public void clearPrefs(){
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
