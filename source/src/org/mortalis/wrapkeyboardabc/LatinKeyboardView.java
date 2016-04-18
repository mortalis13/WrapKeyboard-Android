/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mortalis.wrapkeyboardabc;

import java.util.List;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodSubtype;

public class LatinKeyboardView extends KeyboardView {
    
    static final int KEYCODE_OPTIONS = -100;
    static final int OpenTextEditKeyboardKey=55001;
    
    private LatinKeyboard mTextEditKeyboard;
    
    boolean capsLock=false;
    static boolean selectionModeEnabled=false;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } 
        else if (key.codes[0] == Keyboard.KEYCODE_SHIFT) {
//        	System.out.println("Long Press Shift, capsLock: "+capsLock);
        	setShifted(!capsLock);
        	capsLock=!capsLock;
        	return true;
        }
        else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
        	getOnKeyboardActionListener().onKey(OpenTextEditKeyboardKey, null);
        	return true;
        }
        else if (key.codes[0] == 'e') {
            getOnKeyboardActionListener().onKey('é', null);
            return true;
        }
        else if (key.codes[0] == 'u') {
        	getOnKeyboardActionListener().onKey('ú', null);
        	return true;
        }
        else if (key.codes[0] == 'i') {
        	getOnKeyboardActionListener().onKey('í', null);
        	return true;
        }
        else if (key.codes[0] == 'o') {
        	getOnKeyboardActionListener().onKey('ó', null);
        	return true;
        }
        else if (key.codes[0] == 'a') {
        	getOnKeyboardActionListener().onKey('á', null);
        	return true;
        }
        else if (key.codes[0] == 'n') {
        	getOnKeyboardActionListener().onKey('ñ', null);
        	return true;
        }
        else {
            return super.onLongPress(key);
        }
    }

    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        // keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
    
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        Paint paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(25);
        paint.setColor(Color.YELLOW);
  
        List<Key> keys = getKeyboard().getKeys();
        for (Key key : keys) {
          if(key.codes[0] == Keyboard.KEYCODE_SHIFT && capsLock){
          	Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.capson);
          	Rect src=new Rect(0,0,b.getWidth(),b.getHeight()); 
          	Rect dist=new Rect(key.x,key.y,key.x+key.width,key.y+key.height);
          	canvas.drawBitmap(b, src, dist, paint);
          }
        	
        	if(key.codes[0] == WrapKeyboard.KeySelect && selectionModeEnabled){
            Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.selection_mode); 
            Rect src=new Rect(0,0,b.getWidth(),b.getHeight()); 
            Rect dist=new Rect(key.x,key.y,key.x+key.width,key.y+key.height);
            canvas.drawBitmap(b, src, dist, paint);
          }
        }
    }
    
    
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
        int mSwipeXDistanceThreshold = (int)(mSwipeDistanceThreshold * density);
        int mSwipeSpaceXDistanceThreshold = 0;
        
        Keyboard kbd = getKeyboard();
        if (kbd != null) {
            mSwipeYDistanceThreshold = (int) (mSwipeXDistanceThreshold * (((float) kbd.getHeight()) / ((float) getWidth())));
        } 
        else {
            Log.d("kbd", "getKeyboard() == null");
            mSwipeYDistanceThreshold = 0;
        }
        
        if (mSwipeYDistanceThreshold == 0)
            mSwipeYDistanceThreshold = mSwipeXDistanceThreshold;

        mSwipeSpaceXDistanceThreshold = mSwipeXDistanceThreshold / 2;
        mSwipeYDistanceThreshold = mSwipeYDistanceThreshold / 2;
        

        final int direction;
        final int slide;
        final int maxSlide;

        if (horizontalSlideAbs > verticalSlideAbs) {
            if (horizontalSlide > 0) {
                direction = DIRECTION_RIGHT;
            } else {
                direction = DIRECTION_LEFT;
            }
            maxSlide = mSwipeSpaceXDistanceThreshold;
            slide = Math.min(horizontalSlideAbs, maxSlide);
        } else {
            if (verticalSlide > 0) {
                direction = DIRECTION_DOWN;
            } else {
                direction = DIRECTION_UP;
            }
            maxSlide = mSwipeYDistanceThreshold;
            slide = Math.min(verticalSlideAbs, maxSlide);
        }

        final int slideRatio = (255 * slide) / maxSlide;

        return direction + slideRatio;
    }
    

//     @Override
//     public boolean onTouchEvent(MotionEvent me) {
// //      Log.d("WrapKeyboard", "onTouchEvent with "+me.getPointerCount()+" points");
      
//       if (me.getAction() == MotionEvent.ACTION_DOWN) {
// //          Log.d("me.getAction()_DOWN", String.valueOf(me.getAction()));
            
//           mFirstTouchPont.x = (int) me.getX();
//           mFirstTouchPont.y = (int) me.getY();
//       }
//       else{
//         if (me.getAction() == MotionEvent.ACTION_MOVE) {
//             // setGesturePreviewText(mSwitcher, me);
// //            Log.d("me.getAction()_MOVE", String.valueOf(me.getAction()));
            
//             return true;
//         } 
//         else if (me.getAction() == MotionEvent.ACTION_UP) {
// //            Log.d("me.getAction()_UP", String.valueOf(me.getAction()));
            
//             final int slide = getSlideDistance(me);
//             final int distance = slide & 0x00FF;// removing direction
            
//             if (distance > SLIDE_RATIO_FOR_GESTURE) {
//                 // gesture!!
                
//                 Log.d("slide", String.valueOf(slide & 0xFF00));
              
//                 switch (slide & 0xFF00) {
//                     case DIRECTION_DOWN:
//                         Log.d("slide", "down");
//                         // mKeyboardActionListener.onSwipeDown(true);
//                         break;
//                     case DIRECTION_UP:
//                         Log.d("slide", "up");
//                         // mKeyboardActionListener.onSwipeUp(true);
//                         break;
//                     case DIRECTION_LEFT:
//                         Log.d("slide", "left");
//                         // mKeyboardActionListener.onSwipeLeft(true, isAtTwoFingersState());
//                         break;
//                     case DIRECTION_RIGHT:
//                         Log.d("slide", "right");
//                         // mKeyboardActionListener.onSwipeRight(true, isAtTwoFingersState());
//                         break;
//                 }
//             } else {
//                 // just a key press
//                 super.onTouchEvent(me);
//             }
//             return true;
//         }
//       }
      
//       return super.onTouchEvent(me);
//     }
    
} 
