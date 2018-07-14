
package org.mortalis.wrapkeyboardabc;

import org.mortalis.wrapkeyboardabc.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

public class LatinKeyboard extends Keyboard {

  private Key mEnterKey;
  private Key mSpaceKey;

  public LatinKeyboard(Context context, int xmlLayoutResId) {
    super(context, xmlLayoutResId);
  }

  public LatinKeyboard(Context context, int layoutTemplateResId,
      CharSequence characters, int columns, int horizontalPadding) {
    super(context, layoutTemplateResId, characters, columns, horizontalPadding);
  }

  @Override
  protected Key createKeyFromXml(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
    Key key = new LatinKey(res, parent, x, y, parser);
    if (key.codes[0] == 10) {
      mEnterKey = key;
    } else if (key.codes[0] == ' ') {
      mSpaceKey = key;
    }
    return key;
  }

  /**
   * This looks at the ime options given by the current editor, to set the
   * appropriate label on the keyboard's enter key (if it has one).
   */
  void setImeOptions(Resources res, int options) {
    if (mEnterKey == null) {
      return;
    }

    switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
    case EditorInfo.IME_ACTION_GO:
      mEnterKey.iconPreview = null;
      mEnterKey.icon = null;
      mEnterKey.label = res.getText(R.string.label_go_key);
      break;
    case EditorInfo.IME_ACTION_NEXT:
      mEnterKey.iconPreview = null;
      mEnterKey.icon = null;
      mEnterKey.label = res.getText(R.string.label_next_key);
      break;
    case EditorInfo.IME_ACTION_SEARCH:
      mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
      mEnterKey.label = null;
      break;
    case EditorInfo.IME_ACTION_SEND:
      mEnterKey.iconPreview = null;
      mEnterKey.icon = null;
      mEnterKey.label = res.getText(R.string.label_send_key);
      break;
    default:
      mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
      mEnterKey.label = null;
      break;
    }
  }

  void setSpaceIcon(final Drawable icon) {
    if (mSpaceKey != null) {
      mSpaceKey.icon = icon;
    }
  }

  static class LatinKey extends Keyboard.Key {

    private final static int[] KEY_STATE_NORMAL_ON = {
        android.R.attr.state_checkable, android.R.attr.state_checked };

    private final static int[] KEY_STATE_PRESSED_ON = {
        android.R.attr.state_pressed, android.R.attr.state_checkable,
        android.R.attr.state_checked };

    private final static int[] KEY_STATE_NORMAL_OFF = { android.R.attr.state_checkable };

    private final static int[] KEY_STATE_PRESSED_OFF = {
        android.R.attr.state_pressed, android.R.attr.state_checkable };
 
    private final static int[] KEY_STATE_FUNCTION = { android.R.attr.state_single };

    private final static int[] KEY_STATE_FUNCTION_PRESSED = {
        android.R.attr.state_pressed, android.R.attr.state_single };

    private final static int[] KEY_STATE_NORMAL = {};

    private final static int[] KEY_STATE_PRESSED = { android.R.attr.state_pressed };

    
    public LatinKey(Resources res, Keyboard.Row parent, int x, int y,
        XmlResourceParser parser) {
      super(res, parent, x, y, parser);
    }

    /**
     * Overriding this method so that we can reduce the target area for the key
     * that closes the keyboard.
     */
    @Override
    public boolean isInside(int x, int y) {
      return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
    }
    
    @Override
    public int[] getCurrentDrawableState() {
        int[] states = KEY_STATE_NORMAL;

        if (on) {
          if (modifier) {
            if (pressed) {
                states = KEY_STATE_FUNCTION_PRESSED;
            } else {
                states = KEY_STATE_FUNCTION;
            }
          } else{
            if (pressed) {
              states = KEY_STATE_PRESSED_ON;
            } else {
              states = KEY_STATE_NORMAL_ON;
            } 
          }
        } else {   
            if (sticky) {
                if (pressed) {  
                    states = KEY_STATE_PRESSED_OFF; 
                } else {    
                    states = KEY_STATE_NORMAL_OFF; 
                } 
            } else if(modifier){
                if (pressed) {
                    states = KEY_STATE_FUNCTION_PRESSED;
                } else {
                    states = KEY_STATE_FUNCTION;
                }
            } else { 
                if (pressed) {
                    states = KEY_STATE_PRESSED;
                }
            }
        }
        return states;
    }
  }

}
