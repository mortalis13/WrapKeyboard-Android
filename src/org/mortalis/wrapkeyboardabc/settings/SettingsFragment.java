package org.mortalis.wrapkeyboardabc.settings;

import java.util.Map;

import org.mortalis.wrapkeyboardabc.utils.Fun;
import org.mortalis.wrapkeyboardabc.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
  
  private Context context;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Fun.logd("SettingsFragment.onCreate()");
    
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    
    Preference button = findPreference(getString(R.string.pref_key_switch_keyboard_button));
    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      public boolean onPreferenceClick(Preference preference) {   
        showSwitchDialog();
        return true;
      }
    });
  }
  
  
  @Override
  public void onAttach(Activity context) {
    super.onAttach(context);
    if (context == null) return;
    this.context = context;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context == null) return;
    this.context = context;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    try {
      Preference pref = findPreference(key);
      
      if (pref instanceof CheckBoxPreference) {
        String pref_key_ext_type_none = getString(R.string.pref_key_ext_type_none);
        String pref_key_ext_type_all = getString(R.string.pref_key_ext_type_all);
        String pref_key_ext_type_french = getString(R.string.pref_key_ext_type_french);
        String pref_key_ext_type_german = getString(R.string.pref_key_ext_type_german);
        String pref_key_ext_type_italian = getString(R.string.pref_key_ext_type_italian);
        String pref_key_ext_type_spanish = getString(R.string.pref_key_ext_type_spanish);
        
        boolean value = sharedPreferences.getBoolean(key, false);
        Fun.logd("onSharedPreferenceChanged(): [%s]: [%s]", key, sharedPreferences.getBoolean(key, false));
        
        if (value) {
          if (key.equals(pref_key_ext_type_none)) {
            Fun.log("==pref_key_ext_type_none");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_all")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_italian")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_all)) {
            Fun.log("==pref_key_ext_type_all");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_italian")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_french)) {
            Fun.log("==pref_key_ext_type_french");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_all")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_italian")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_german)) {
            Fun.log("==pref_key_ext_type_german");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_all")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_italian")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_italian)) {
            Fun.log("==pref_key_ext_type_italian");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_all")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_spanish")).setChecked(false);
          }
          else if (key.equals(pref_key_ext_type_spanish)) {
            Fun.log("==pref_key_ext_type_spanish");
            ((CheckBoxPreference) findPreference("pref_key_ext_type_none")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_all")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_french")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_german")).setChecked(false);
            ((CheckBoxPreference) findPreference("pref_key_ext_type_italian")).setChecked(false);
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
  
  private void showSwitchDialog() {
    if (context == null) return;
    InputMethodManager imeManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imeManager.showInputMethodPicker();
  }

}
