package org.mortalis.wrapkeyboardabc_test;

import java.util.List;

import org.mortalis.wrapkeyboardabc_test.utils.Fun;
import org.mortalis.wrapkeyboardabc_test.utils.Vars;

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


public class PopupKeyboardViewExt extends KeyboardView {
  
  public PopupKeyboardViewExt(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }
  
  public PopupKeyboardViewExt(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }
  
  public void init() {
    
  }
  
  
  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
  }
  
}
