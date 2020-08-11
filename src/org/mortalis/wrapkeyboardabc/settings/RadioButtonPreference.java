package org.mortalis.wrapkeyboardabc.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.preference.CheckBoxPreference;


public class RadioButtonPreference extends CheckBoxPreference {

  public RadioButtonPreference(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    // setWidgetLayoutResource(R.layout.preference_widget_radiobutton);
  }

  public RadioButtonPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    // setWidgetLayoutResource(R.layout.preference_widget_radiobutton);
  }

  public RadioButtonPreference(Context context) {
    this(context, null);
  }

  @Override
  public void onClick() {
    if (this.isChecked()) {
      return;
    }
    super.onClick();
  }
    
}
