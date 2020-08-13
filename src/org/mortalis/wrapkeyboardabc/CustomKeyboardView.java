package org.mortalis.wrapkeyboardabc;

import java.util.List;

import org.mortalis.wrapkeyboardabc.utils.Fun;
import org.mortalis.wrapkeyboardabc.utils.Vars;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class CustomKeyboardView extends KeyboardView {
  
  static final int KEYCODE_OPTIONS = -100;
  
  static boolean selectionModeEnabled;
  static boolean capsLock;
  
  private Bitmap bitmapShift, bitmapSelect;
  private Rect shiftRect, selectRect;
  private Paint specKeyPaint;
  
  
  public CustomKeyboardView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }
  
  public CustomKeyboardView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }
  
  public void init() {
    specKeyPaint = new Paint();
    specKeyPaint.setTextAlign(Paint.Align.CENTER);
    specKeyPaint.setTextSize(25);
    specKeyPaint.setColor(Color.YELLOW);
    
    bitmapShift = BitmapFactory.decodeResource(getResources(), R.drawable.capson);
    shiftRect = new Rect(0, 0, bitmapShift.getWidth(), bitmapShift.getHeight());
    
    bitmapSelect = BitmapFactory.decodeResource(getResources(), R.drawable.selection_mode);
    selectRect = new Rect(0, 0, bitmapSelect.getWidth(), bitmapSelect.getHeight());
  }
  
  
  @Override
  protected boolean onLongPress(Key key) {
    if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
      getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
      return true;
    }
    else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
      setShifted(!capsLock);
      capsLock = !capsLock;
      return true;
    }
    else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
      getOnKeyboardActionListener().onKey(Vars.KEY_OPEN_TEXT_EDIT_KEYBOARD, null);
      return true;
    }
    else if (key.codes[0] == Vars.KEY_SWITCH_LANG_KEYBOARD) {
      getOnKeyboardActionListener().onKey(Vars.KEY_LANG_SYMBOLS_KEYBOARD, null);
      return true;
    }
    
    // else if (key.codes[0] == 'e') {
    //   getOnKeyboardActionListener().onKey('é', null);
    //   return true;
    // }
    // else if (key.codes[0] == 'u') {
    //   getOnKeyboardActionListener().onKey('ú', null);
    //   return true;
    // }
    // else if (key.codes[0] == 'i') {
    //   getOnKeyboardActionListener().onKey('í', null);
    //   return true;
    // }
    // else if (key.codes[0] == 'o') {
    //   getOnKeyboardActionListener().onKey('ó', null);
    //   return true;
    // }
    // else if (key.codes[0] == 'a') {
    //   getOnKeyboardActionListener().onKey('á', null);
    //   return true;
    // }
    // else if (key.codes[0] == 'n') {
    //   getOnKeyboardActionListener().onKey('ñ', null);
    //   return true;
    // }
    
    else {
      return super.onLongPress(key);
    }
  }
  
  // --- Uncomment to override and disable key resize 
  // --- (will draw keys with true size specified in XML or createKeyFromXml() via key.width)
  // @Override
  // public void onSizeChanged(int w, int h, int oldw, int oldh) {
  // }
  
  
  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    List<Key> keys = getKeyboard().getKeys();
    for (Key key: keys) {
      if (key.codes[0] == Keyboard.KEYCODE_SHIFT && capsLock) {
        Rect dist = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
        canvas.drawBitmap(bitmapShift, shiftRect, dist, specKeyPaint);
      }
      
      if (key.codes[0] == Vars.KEY_SELECT && selectionModeEnabled) {
        Rect dist = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
        canvas.drawBitmap(bitmapSelect, selectRect, dist, specKeyPaint);
      }
    }
  }
  
  
  // -------------------------- Swipe -----------------------------
  
  private final static int DIRECTION_UP = 0x0100;
  private final static int DIRECTION_DOWN = 0x0200;
  private final static int DIRECTION_LEFT = 0x0400;
  private final static int DIRECTION_RIGHT = 0x0800;
  
  private Point mFirstTouchPont = new Point(0, 0);
  
  private static final int SLIDE_RATIO_FOR_GESTURE = 250;
  
  
  private int getSlideDistance(MotionEvent me) {
    Resources res = getResources();
    final float density = res.getDisplayMetrics().density;
    
    final int horizontalSlide = ((int) me.getX()) - mFirstTouchPont.x;
    final int horizontalSlideAbs = Math.abs(horizontalSlide);
    final int verticalSlide = ((int) me.getY()) - mFirstTouchPont.y;
    final int verticalSlideAbs = Math.abs(verticalSlide);
    
    int mSwipeDistanceThreshold = 240;
    
    int mSwipeYDistanceThreshold = 0;
    int mSwipeXDistanceThreshold = (int) (mSwipeDistanceThreshold * density);
    int mSwipeSpaceXDistanceThreshold = 0;
    
    Keyboard kbd = getKeyboard();
    if (kbd != null) {
      mSwipeYDistanceThreshold = (int) (mSwipeXDistanceThreshold * (((float) kbd.getHeight()) / ((float) getWidth())));
    }
    else {
      mSwipeYDistanceThreshold = 0;
    }
    
    if (mSwipeYDistanceThreshold == 0) mSwipeYDistanceThreshold = mSwipeXDistanceThreshold;
    
    mSwipeSpaceXDistanceThreshold = mSwipeXDistanceThreshold / 2;
    mSwipeYDistanceThreshold = mSwipeYDistanceThreshold / 2;
    
    final int direction;
    final int slide;
    final int maxSlide;
    
    if (horizontalSlideAbs > verticalSlideAbs) {
      if (horizontalSlide > 0) {
        direction = DIRECTION_RIGHT;
      }
      else {
        direction = DIRECTION_LEFT;
      }
      maxSlide = mSwipeSpaceXDistanceThreshold;
      slide = Math.min(horizontalSlideAbs, maxSlide);
    }
    else {
      if (verticalSlide > 0) {
        direction = DIRECTION_DOWN;
      }
      else {
        direction = DIRECTION_UP;
      }
      maxSlide = mSwipeYDistanceThreshold;
      slide = Math.min(verticalSlideAbs, maxSlide);
    }
    
    final int slideRatio = (255 * slide) / maxSlide;
    
    return direction + slideRatio;
  }
  
}
