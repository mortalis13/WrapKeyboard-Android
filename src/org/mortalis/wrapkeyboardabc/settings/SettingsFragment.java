package org.mortalis.wrapkeyboardabc.settings;

import java.util.Map;

import org.mortalis.wrapkeyboardabc_test.R;
import org.mortalis.wrapkeyboardabc.utils.Fun;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Fun.logd("SettingsFragment.onCreate()");
    
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }
  
  
  @Override
  public void onAttach(Activity context) {
    super.onAttach(context);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    try {
      
      Preference pref = findPreference(key);
      
      if (pref instanceof CheckBoxPreference) {
        String pref_key_ext_type_none = Fun.getString(R.string.pref_key_ext_type_none);
        String pref_key_ext_type_french = Fun.getString(R.string.pref_key_ext_type_french);
        String pref_key_ext_type_german = Fun.getString(R.string.pref_key_ext_type_german);
        String pref_key_ext_type_spanish = Fun.getString(R.string.pref_key_ext_type_spanish);
        
        Fun.logd("pref_key_ext_type_none: [%s]", pref_key_ext_type_none);
        Fun.logd("pref_key_ext_type_french: [%s]", pref_key_ext_type_french);
        Fun.logd("pref_key_ext_type_german: [%s]", pref_key_ext_type_german);
        Fun.logd("pref_key_ext_type_spanish: [%s]", pref_key_ext_type_spanish);
        
        boolean value = sharedPreferences.getBoolean(key, false);
        Fun.logd("onSharedPreferenceChanged(): [%s]: [%s]", key, sharedPreferences.getBoolean(key, false));
        
        if (value) {
          if (key.equals(pref_key_ext_type_none)) {
            Fun.log("==pref_key_ext_type_none");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_french)) {
            Fun.log("==pref_key_ext_type_french");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_german)) {
            Fun.log("==pref_key_ext_type_german");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_spanish)) {
            Fun.log("==pref_key_ext_type_spanish");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
          }
        }
      }
      
      // showPrefValue(sharedPreferences, key);
    }
    catch (Exception e) {
      Fun.loge("onSharedPreferenceChanged Exception, " + e);
    }
  }

  @Override
  public void onResume() {
    Fun.logd("SettingsFragment.onResume()");
    super.onResume();

    SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    // Map<String, ?> prefs = sharedPreferences.getAll();
    // for (String key: prefs.keySet()) {
    //   showPrefValue(sharedPreferences, key);
    // }
  }

  @Override
  public void onPause() {
    Fun.logd("SettingsFragment.onPause()");
    super.onPause();
    
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }


//---------------------------------------------- Service ----------------------------------------------

  private void showPrefValue(SharedPreferences sharedPreferences, String key) {
    Fun.logd("SettingsFragment.showPrefValue()");
    
    Preference pref = findPreference(key);
    if (pref != null) {
      if (pref instanceof CheckBoxPreference) return;
      
      String summary = sharedPreferences.getString(key, "");
      if (pref instanceof ListPreference) {
        ListPreference listPref = (ListPreference) pref;
        summary = (String) listPref.getEntry();
      }

      pref.setSummary(summary);
    }
  }

}
