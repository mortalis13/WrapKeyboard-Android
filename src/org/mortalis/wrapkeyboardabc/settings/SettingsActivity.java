package org.mortalis.wrapkeyboardabc.settings;

import org.mortalis.wrapkeyboardabc.utils.Fun;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;


public class SettingsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Fun.logd("SettingsActivity.onCreate()");
    
    super.onCreate(savedInstanceState);

    PreferenceFragment preferenceFragment = new SettingsFragment();
    getFragmentManager().beginTransaction()
            .replace(android.R.id.content, preferenceFragment)
            .commit();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Fun.logd("SettingsActivity.onOptionsItemSelected()");
    
    int id = item.getItemId();
    switch (id) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(0, 0);
    finish();
  }

}
