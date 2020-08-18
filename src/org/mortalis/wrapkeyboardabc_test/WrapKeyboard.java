package org.mortalis.wrapkeyboardabc_test;

import java.util.ArrayList;
import java.util.List;

import org.mortalis.wrapkeyboardabc_test.R;
import org.mortalis.wrapkeyboardabc_test.utils.Fun;
import org.mortalis.wrapkeyboardabc_test.utils.Vars;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.SystemClock;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;


public class WrapKeyboard extends InputMethodService implements CustomKeyboardView.OnKeyboardActionListener {
  
  private static final boolean DEBUG = false;
  private static final boolean PROCESS_HARD_KEYS = true;
  
  private InputMethodManager mInputMethodManager;
  
  private CustomKeyboardView mInputView;
  private CompletionInfo[] mCompletions;
  
  private StringBuilder mComposing = new StringBuilder();
  private boolean mPredictionOn;
  private boolean mCompletionOn;
  private int mLastDisplayWidth;
  private boolean mCapsLock;
  private long mLastShiftTime;
  private long mMetaState;
  
  private CustomKeyboard mSymbolsKeyboard;
  private CustomKeyboard mSymbolsExtKeyboard;
  private CustomKeyboard mLatinKeyboard;
  
  private CustomKeyboard mLatinKeyboard_none;
  private CustomKeyboard mLatinKeyboard_all;
  private CustomKeyboard mLatinKeyboard_fr;
  private CustomKeyboard mLatinKeyboard_de;
  private CustomKeyboard mLatinKeyboard_it;
  private CustomKeyboard mLatinKeyboard_es;
  
  private CustomKeyboard mCyrillicKeyboard;
  private CustomKeyboard mTextEditKeyboard;
  private CustomKeyboard mExtCharsKeyboard;
  
  private CustomKeyboard mCurKeyboard;
  private CustomKeyboard mCurLangKeyboard;
  
  private String mWordSeparators;
  
  
  @Override
  public void onCreate() {
    Fun.logd("WrapKeyboard.onCreate()");
    
    super.onCreate();
    Fun.setContext(this);
    
    mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    mWordSeparators = getResources().getString(R.string.word_separators);
  }
  
  @Override
  public void onInitializeInterface() {
    Fun.logd("WrapKeyboard.onInitializeInterface()");
    
    int displayWidth = getMaxWidth();
    if (mLatinKeyboard != null) {
      if (displayWidth == mLastDisplayWidth) return;
      mLastDisplayWidth = displayWidth;
    }
    
    mLatinKeyboard_none = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin);
    mLatinKeyboard_all  = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin_all);
    mLatinKeyboard_fr   = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin_fr);
    mLatinKeyboard_de   = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin_de);
    mLatinKeyboard_it   = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin_it);
    mLatinKeyboard_es   = new CustomKeyboard(this, displayWidth, R.xml.keyboard_latin_es);
    mLatinKeyboard = mLatinKeyboard_none;
    
    mSymbolsKeyboard = new CustomKeyboard(this, displayWidth, R.xml.keyboard_symbols);
    mSymbolsExtKeyboard = new CustomKeyboard(this, displayWidth, R.xml.keyboard_symbols_ext);
    
    mCyrillicKeyboard = new CustomKeyboard(this, displayWidth, R.xml.keyboard_cyrillic);
    mTextEditKeyboard = new CustomKeyboard(this, displayWidth, R.xml.keyboard_text_edit);
    mExtCharsKeyboard = new CustomKeyboard(this, displayWidth, R.xml.keyboard_ext_chars);
    
    if (Fun.getPrefExtType_None()) {
      mLatinKeyboard = mLatinKeyboard_none;
    }
    else if (Fun.getPrefExtType_All()) {
      mLatinKeyboard = mLatinKeyboard_all;
    }
    else if (Fun.getPrefExtType_French()) {
      mLatinKeyboard = mLatinKeyboard_fr;
    }
    else if (Fun.getPrefExtType_German()) {
      mLatinKeyboard = mLatinKeyboard_de;
    }
    else if (Fun.getPrefExtType_Italian()) {
      mLatinKeyboard = mLatinKeyboard_it;
    }
    else if (Fun.getPrefExtType_Spanish()) {
      mLatinKeyboard = mLatinKeyboard_es;
    }
    
    mCurLangKeyboard = mLatinKeyboard;
  }
  
  @Override
  public View onCreateInputView() {
    Fun.logd("WrapKeyboard.onCreateInputView()");
    
    mInputView = (CustomKeyboardView) getLayoutInflater().inflate(R.layout.input, null);
    mInputView.setOnKeyboardActionListener(this);
    mInputView.setKeyboard(mCurLangKeyboard);
    
    return mInputView;
  }
  
  @Override
  public View onCreateCandidatesView() {
    Fun.logd("WrapKeyboard.onCreateCandidatesView()");
    return null;
  }
  
  @Override
  public void onStartInput(EditorInfo attribute, boolean restarting) {
    Fun.logd("WrapKeyboard.onStartInput()");
    super.onStartInput(attribute, restarting);
    
    if (mCurLangKeyboard == mLatinKeyboard) {
      if (Fun.getPrefExtType_None()) {
        mLatinKeyboard = mLatinKeyboard_none;
      }
      else if (Fun.getPrefExtType_All()) {
        mLatinKeyboard = mLatinKeyboard_all;
      }
      else if (Fun.getPrefExtType_French()) {
        mLatinKeyboard = mLatinKeyboard_fr;
      }
      else if (Fun.getPrefExtType_German()) {
        mLatinKeyboard = mLatinKeyboard_de;
      }
      else if (Fun.getPrefExtType_Italian()) {
        mLatinKeyboard = mLatinKeyboard_it;
      }
      else if (Fun.getPrefExtType_Spanish()) {
        mLatinKeyboard = mLatinKeyboard_es;
      }
      mCurLangKeyboard = mLatinKeyboard;
    }
    
    mComposing.setLength(0);
    updateCandidates();
    
    if (!restarting) {
      mMetaState = 0;
    }
    
    mPredictionOn = false;
    mCompletionOn = false;
    mCompletions = null;
    
    switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
      case InputType.TYPE_CLASS_NUMBER:
      case InputType.TYPE_CLASS_DATETIME:
        mCurKeyboard = mSymbolsKeyboard;
        break;
      
      case InputType.TYPE_CLASS_PHONE:
        mCurKeyboard = mSymbolsKeyboard;
        break;
      
      case InputType.TYPE_CLASS_TEXT:
        mCurKeyboard = mCurLangKeyboard;
        
        int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
        if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
          mPredictionOn = false;
        }
        
        if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || variation == InputType.TYPE_TEXT_VARIATION_URI || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
          mPredictionOn = false;
        }
        
        if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
          mPredictionOn = false;
          mCompletionOn = isFullscreenMode();
        }
        break;
      
      default:
        mCurKeyboard = mCurLangKeyboard;
    }
    
    mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
  }
  
  @Override
  public void onFinishInput() {
    Fun.logd("WrapKeyboard.onFinishInput()");
    super.onFinishInput();
    
    mComposing.setLength(0);
    updateCandidates();
    
    setCandidatesViewShown(false);
    
    mCurKeyboard = mCurLangKeyboard;
    if (mInputView != null) {
      mInputView.closing();
    }
  }
  
  @Override
  public void onStartInputView(EditorInfo attribute, boolean restarting) {
    Fun.logd("WrapKeyboard.onStartInputView()");
    super.onStartInputView(attribute, restarting);
    
    mInputView.setKeyboard(mCurKeyboard);
    mInputView.closing();
    
    final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
  }
  
  @Override
  public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
    Fun.logd("WrapKeyboard.onCurrentInputMethodSubtypeChanged()");
  }
  
  @Override
  public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
    Fun.logd("WrapKeyboard.onUpdateSelection()");
    super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
    
    if (mComposing.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
      mComposing.setLength(0);
      updateCandidates();
      
      InputConnection ic = getCurrentInputConnection();
      if (ic != null) {
        ic.finishComposingText();
      }
    }
  }
  
  @Override
  public void onDisplayCompletions(CompletionInfo[] completions) {
    Fun.logd("WrapKeyboard.onDisplayCompletions()");
    
    if (mCompletionOn) {
      mCompletions = completions;
      if (completions == null) {
        setSuggestions(null, false, false);
        return;
      }
      
      List<String> stringList = new ArrayList<String>();
      for (int i = 0; i < completions.length; i++) {
        CompletionInfo ci = completions[i];
        if (ci != null) stringList.add(ci.getText().toString());
      }
      
      setSuggestions(stringList, true, true);
    }
  }
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KeyEvent.KEYCODE_BACK:
        if (event.getRepeatCount() == 0 && mInputView != null) {
          if (mInputView.handleBack()) {
            return true;
          }
        }
        break;
      
      case KeyEvent.FLAG_EDITOR_ACTION:
        Fun.log("FLAG_EDITOR_ACTION");
        break;
      
      case KeyEvent.KEYCODE_DEL:
        Fun.log("KEYCODE_DEL");
        
        if (mComposing.length() > 0) {
          onKey(Keyboard.KEYCODE_DELETE, null);
          return true;
        }
        break;
      
      case KeyEvent.KEYCODE_ENTER:
        Fun.log("KEYCODE_ENTER");
        return false;
      
      default:
        if (PROCESS_HARD_KEYS) {
          if (keyCode == KeyEvent.KEYCODE_SPACE && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
              ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
              keyDownUp(KeyEvent.KEYCODE_A);
              keyDownUp(KeyEvent.KEYCODE_N);
              keyDownUp(KeyEvent.KEYCODE_D);
              keyDownUp(KeyEvent.KEYCODE_R);
              keyDownUp(KeyEvent.KEYCODE_O);
              keyDownUp(KeyEvent.KEYCODE_I);
              keyDownUp(KeyEvent.KEYCODE_D);
              return true;
            }
          }
          
          if (mPredictionOn && translateKeyDown(keyCode, event)) {
            return true;
          }
        }
    }
    
    return super.onKeyDown(keyCode, event);
  }
  
  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (PROCESS_HARD_KEYS) {
      if (mPredictionOn) {
        mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
      }
    }
    return super.onKeyUp(keyCode, event);
  }
  
  @Override
  public boolean onKeyLongPress(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return super.onKeyLongPress(keyCode, event);
  }
  
  
  private boolean translateKeyDown(int keyCode, KeyEvent event) {
    mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState, keyCode, event);
    int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
    mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
    
    InputConnection ic = getCurrentInputConnection();
    if (c == 0 || ic == null) {
      return false;
    }
    
    boolean dead = false;
    if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
      dead = true;
      c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
    }
    
    if (mComposing.length() > 0) {
      char accent = mComposing.charAt(mComposing.length() - 1);
      int composed = KeyEvent.getDeadChar(accent, c);
      
      if (composed != 0) {
        c = composed;
        mComposing.setLength(mComposing.length() - 1);
      }
    }
    
    onKey(c, null);
    return true;
  }
  
  
  // -- KeyboardView.OnKeyboardActionListener
  @Override
  public void onKey(int primaryCode, int[] keyCodes) {
    if (isWordSeparator(primaryCode)) {
      if (mComposing.length() > 0) {
        commitTyped(getCurrentInputConnection());
      }
      
      // sendKey(primaryCode);
      sendKeyChar((char) primaryCode);
      updateShiftKeyState(getCurrentInputEditorInfo());
    }
    else if (primaryCode == Keyboard.KEYCODE_DELETE) {
      handleBackspace();
    }
    else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
      handleShift();
    }
    else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
      handleClose();
      return;
    }
    else if (primaryCode == CustomKeyboardView.KEYCODE_OPTIONS) {
      
    }
    else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mInputView != null) {
      Keyboard current = mInputView.getKeyboard();
      if (current == mSymbolsKeyboard || current == mSymbolsExtKeyboard || current == mTextEditKeyboard || current == mExtCharsKeyboard) {
        if (mCurLangKeyboard == mLatinKeyboard) {
          current = mLatinKeyboard;
        }
        else if (mCurLangKeyboard == mCyrillicKeyboard) {
          current = mCyrillicKeyboard;
        }
      }
      else {
        current = mSymbolsKeyboard;
      }
      
      mInputView.setKeyboard(current);
      if (current == mSymbolsKeyboard) {
        current.setShifted(false);
      }
    }
    else if (primaryCode == Vars.KEY_SWITCH_LANG_KEYBOARD) {
      Keyboard current = mInputView.getKeyboard();
      mCurLangKeyboard = current == mLatinKeyboard ? mCyrillicKeyboard: mLatinKeyboard;
      mCurKeyboard = mCurLangKeyboard;
      mInputView.setKeyboard(mCurLangKeyboard);
    }
    else if (primaryCode == Vars.KEY_OPEN_TEXT_EDIT_KEYBOARD) {
      Keyboard current = mTextEditKeyboard;
      mInputView.setKeyboard(current);
    }
    else if (primaryCode == Vars.KEY_LANG_SYMBOLS_KEYBOARD) {
      Keyboard current = mExtCharsKeyboard;
      mInputView.setKeyboard(current);
    }
    else if (primaryCode == Vars.KEY_MOVE_LEFT) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
    }
    else if (primaryCode == Vars.KEY_MOVE_UP) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP);
    }
    else if (primaryCode == Vars.KEY_MOVE_RIGHT) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
    }
    else if (primaryCode == Vars.KEY_MOVE_DOWN) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
    }
    else if (primaryCode == Vars.KEY_HOME) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_MOVE_HOME);
    }
    else if (primaryCode == Vars.KEY_END) {
      sendDownUpKeyEvents(KeyEvent.KEYCODE_MOVE_END);
    }
    else if (primaryCode == Vars.KEY_SELECT) {
      int keyEventCode = KeyEvent.KEYCODE_SHIFT_LEFT;
      long eventTime = SystemClock.uptimeMillis();
      InputConnection ic = getCurrentInputConnection();
      
      if (!CustomKeyboardView.selectionModeEnabled) {
        ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyEventCode, 0, 0));
        CustomKeyboardView.selectionModeEnabled = true;
      }
      else {
        ic.sendKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyEventCode, 0, 0));
        CustomKeyboardView.selectionModeEnabled = false;
      }
    }
    else if (primaryCode == Vars.KEY_ALL) {
      InputConnection ic = getCurrentInputConnection();
      ic.setSelection(0, 0);
      ic.performContextMenuAction(android.R.id.selectAll);
    }
    else if (primaryCode == Vars.KEY_COPY) {
      InputConnection ic = getCurrentInputConnection();
      ic.performContextMenuAction(android.R.id.copy);
    }
    else if (primaryCode == Vars.KEY_CUT) {
      InputConnection ic = getCurrentInputConnection();
      ic.performContextMenuAction(android.R.id.cut);
    }
    else if (primaryCode == Vars.KEY_PASTE) {
      InputConnection ic = getCurrentInputConnection();
      ic.performContextMenuAction(android.R.id.paste);
    }
    else {
      handleCharacter(primaryCode, keyCodes);
      if (mInputView.isShifted() && !mInputView.capsLock) mInputView.setShifted(false);
    }
  }
  
  @Override
  public void onText(CharSequence text) {
    InputConnection ic = getCurrentInputConnection();
    if (ic == null) return;
    ic.beginBatchEdit();
    
    if (mComposing.length() > 0) {
      commitTyped(ic);
    }
    
    ic.commitText(text, 0);
    ic.endBatchEdit();
    updateShiftKeyState(getCurrentInputEditorInfo());
  }
  
  
  @Override
  public void swipeRight() {
    Fun.log("swipeRight()");
    if (mCompletionOn) {
      pickDefaultCandidate();
    }
  }
  
  @Override
  public void swipeLeft() {
    Fun.log("swipeLeft()");
    handleBackspace();
  }
  
  @Override
  public void swipeDown() {
    Fun.log("swipeDown()");
    handleClose();
  }
  
  @Override
  public void swipeUp() {
    Fun.log("swipeUp()");
  }
  
  @Override
  public void onPress(int primaryCode) {
    mInputView.setPreviewEnabled(false);
  }
  
  @Override
  public void onRelease(int primaryCode) {
  }
  
  
  private void commitTyped(InputConnection inputConnection) {
    if (mComposing.length() > 0) {
      inputConnection.commitText(mComposing, mComposing.length());
      mComposing.setLength(0);
      updateCandidates();
    }
  }
  
  private void updateShiftKeyState(EditorInfo attr) {}
  
  private void handleBackspace() {
    final int length = mComposing.length();
    
    if (length > 1) {
      mComposing.delete(length - 1, length);
      getCurrentInputConnection().setComposingText(mComposing, 1);
      updateCandidates();
    }
    else if (length > 0) {
      mComposing.setLength(0);
      getCurrentInputConnection().commitText("", 0);
      updateCandidates();
    }
    else {
      keyDownUp(KeyEvent.KEYCODE_DEL);
    }
    
    updateShiftKeyState(getCurrentInputEditorInfo());
  }
  
  private void handleShift() {
    if (mInputView == null) {
      return;
    }
    
    Keyboard currentKeyboard = mInputView.getKeyboard();
    if (mLatinKeyboard == currentKeyboard || mCyrillicKeyboard == currentKeyboard || mExtCharsKeyboard == currentKeyboard) {
      mInputView.setShifted(mCapsLock || !mInputView.isShifted());
      if (mInputView.capsLock) mInputView.capsLock = false;
    }
    else if (currentKeyboard == mSymbolsKeyboard) {
      mSymbolsKeyboard.setShifted(true);
      mInputView.setKeyboard(mSymbolsExtKeyboard);
      mSymbolsExtKeyboard.setShifted(true);
    }
    else if (currentKeyboard == mSymbolsExtKeyboard) {
      mSymbolsExtKeyboard.setShifted(false);
      mInputView.setKeyboard(mSymbolsKeyboard);
      mSymbolsKeyboard.setShifted(false);
    }
  }
  
  private void handleCharacter(int primaryCode, int[] keyCodes) {
    if (isInputViewShown()) {
      if (mInputView.isShifted()) {
        primaryCode = Character.toUpperCase(primaryCode);
      }
    }
    
    if (isAlphabet(primaryCode) && mPredictionOn) {
      mComposing.append((char) primaryCode);
      getCurrentInputConnection().setComposingText(mComposing, 1);
      updateShiftKeyState(getCurrentInputEditorInfo());
      updateCandidates();
    }
    else {
      getCurrentInputConnection().commitText(String.valueOf((char) primaryCode), 1);
    }
  }
  
  private void handleClose() {
    commitTyped(getCurrentInputConnection());
    requestHideSelf(0);
    mInputView.closing();
  }
  
  private void checkToggleCapsLock() {
    long now = System.currentTimeMillis();
    if (mLastShiftTime + 800 > now) {
      mCapsLock = !mCapsLock;
      mLastShiftTime = 0;
    }
    else {
      mLastShiftTime = now;
    }
  }
  
  
  private void updateCandidates() {
    if (!mCompletionOn) {
      if (mComposing.length() > 0) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(mComposing.toString());
        setSuggestions(list, true, true);
      }
      else {
        setSuggestions(null, false, false);
      }
    }
  }
  
  public void setSuggestions(List<String> suggestions, boolean completions, boolean typedWordValid) {
    if (suggestions != null && suggestions.size() > 0) {
      setCandidatesViewShown(true);
    }
    else if (isExtractViewShown()) {
      setCandidatesViewShown(true);
    }
  }
  
  public void pickDefaultCandidate() {
    pickSuggestionManually(0);
  }
  
  public void pickSuggestionManually(int index) {
    if (mCompletionOn && mCompletions != null && index >= 0 && index < mCompletions.length) {
      CompletionInfo ci = mCompletions[index];
      getCurrentInputConnection().commitCompletion(ci);
      updateShiftKeyState(getCurrentInputEditorInfo());
    }
    else if (mComposing.length() > 0) {
      commitTyped(getCurrentInputConnection());
    }
  }
  
  
  // ------------------------------------ Utils ------------------------------------
  private boolean isAlphabet(int code) {
    if (Character.isLetter(code)) {
      return true;
    }
    else {
      return false;
    }
  }
  
  private void keyDownUp(int keyEventCode) {
    getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
    getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
  }
  
  private void sendKey(int keyCode) {
    switch (keyCode) {
      case '\n':
        keyDownUp(KeyEvent.KEYCODE_ENTER);
        break;
      
      default:
        if (keyCode >= '0' && keyCode <= '9') {
          keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
        }
        else {
          getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
        }
    }
  }
  
  private String getWordSeparators() {
    return mWordSeparators;
  }
  
  public boolean isWordSeparator(int code) {
    String separators = getWordSeparators();
    return separators.contains(String.valueOf((char) code));
  }
  
  private String getKeyboardType(Keyboard keyboard) {
    String result = "";
    if (keyboard == mSymbolsKeyboard) {
      result = "mSymbolsKeyboard";
    }
    else if (keyboard == mSymbolsExtKeyboard) {
      result = "mSymbolsExtKeyboard";
    }
    else if (keyboard == mLatinKeyboard) {
      result = "mLatinKeyboard";
    }
    else if (keyboard == mCyrillicKeyboard) {
      result = "mCyrillicKeyboard";
    }
    else if (keyboard == mTextEditKeyboard) {
      result = "mTextEditKeyboard";
    }
    else if (keyboard == mExtCharsKeyboard) {
      result = "mExtCharsKeyboard";
    }
    else if (keyboard == null) {
      result = "[null]";
    }
    else {
      result = "unknown";
    }
    return result;
  }
  
}
