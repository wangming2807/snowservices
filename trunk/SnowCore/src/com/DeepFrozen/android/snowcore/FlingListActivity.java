package com.DeepFrozen.android.snowcore;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class FlingListActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFlingHelper = new FlingHelper(this);
    }
   
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	boolean returnVal = mFlingHelper.onTouchEvent(event); 
    	if (returnVal)
    		return returnVal;
    	else
    		return super.onTouchEvent(event);
	}
    
	protected FlingHelper mFlingHelper;
}