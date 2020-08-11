package org.mortalis.wrapkeyboardabc;

import org.mortalis.wrapkeyboardabc.inputmethodcommon.InputMethodSettingsFragment;
import org.mortalis.wrapkeyboardabc.utils.Fun;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.mortalis.wrapkeyboardabc.R;


/**
 * Displays the IME preferences inside the input method setting.
 */
public class ImePreferences extends PreferenceActivity {
    @Override
    public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, Settings.class.getName());
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We overwrite the title of the activity, as the default one is "Voice Search".
        setTitle(R.string.settings_name);
    }

    public static class Settings extends InputMethodSettingsFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }
    
    protected boolean isValidFragment(String fragmentName) {
      Fun.log("..fragmentName: " + fragmentName);
      return Settings.class.getName().equals(fragmentName);
    }
}
