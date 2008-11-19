package com.DeepFrozen.android.snowcore;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class FlingHelper {
	public FlingHelper(Activity parent) {
		mParent = parent;
		mGestureDetector = new GestureDetector(mGestureListener);
	}
    
    protected GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
    	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
    		float x = event2.getX();
			float y = event2.getY();

			float dist = (float)Math.sqrt(Math.pow(event1.getX() - x, 2)+Math.pow(event1.getY() - y, 2));
			//Log.d("fling", "Distnce = "+dist+" velocity="+velocityX);
			if (dist < mMinDistance)// && velocityX > mMinVelocity)
				return false;
			float mX = event1.getX() - x;
			float mY = event1.getY() - y;
			if (Math.abs(mX) > Math.abs(mY))
				if (event1.getX() - x >= 0) {
					mParent.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK)); 
					mParent.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
					return true;
				}
			return false;
    	}
    };
    
    public boolean onTouchEvent(MotionEvent event) {
    	return mGestureDetector.onTouchEvent(event);
    }
    
    protected float mMinDistance;
    protected float mMinVelocity;
	protected GestureDetector mGestureDetector;
	protected Activity mParent;
}
