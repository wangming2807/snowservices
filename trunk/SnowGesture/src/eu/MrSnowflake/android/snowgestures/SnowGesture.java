package eu.MrSnowflake.android.snowgestures;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import android.view.MotionEvent;

/**
 * A class which will make gesture recognition a lot easier!
 * @author MrSnowflake
 */
public class SnowGesture {
	private static final String TAG = "SnowGesture";
	
	/**
	 * The listener interface which is used to be called when a gesture is completed.
	 * @author MrSnowflake
	 */
	public interface GestureCompleteListener {
		/**
		 * This method will be called when a gesture is completed. The gesture will be 
		 * given as the parameter.
		 * @param gesture The gesture which just got completed.
		 */
		public void onGestureCompleted(String gesture);
	}
	
	/**
	 * The listener interface which is used to be called when a gesture got completed and 
	 * got recognized as a gesture you supplied by registerGesture(String gesture, GestureListener listener).
	 * @author MrSnowflake
	 */
	public interface GestureListener {
		/**
		 * This method will be called when the gesture got recognized.
		 */
		public void onRecognized();
	}
	
	/**
	 * To be deleted!!!!
	 * @param bool
	 */
	public SnowGesture(boolean bool) {
		mDebug = bool;
		mGesture = new String();
		
		mGestureCompleteListeners = new ArrayList<GestureCompleteListener>();
		mGestureListeners = new HashMap<String, GestureListener>();		
	}
		
	public SnowGesture() {
		mDebug = false;
		mGesture = new String();
		
		mGestureCompleteListeners = new ArrayList<GestureCompleteListener>();
		mGestureListeners = new HashMap<String, GestureListener>();		
	}
	
	/**
	 * Register a GestureCompleteListener for when a gesture is completed.
	 * @param listener The GestureCompleteListener to be registered.
	 */
	public void registerGestureCompleteListener(GestureCompleteListener listener) {
		mGestureCompleteListeners.add(listener);
	}
	
	/**
	 * Unregisters a GestureCompleteListener.
	 * @param listener The GestureCompleteListener to be unregistered.
	 */
	public void unregisterGestureCompleteListener(GestureCompleteListener listener) {
		mGestureCompleteListeners.remove(listener);
	}

	/**
	 * Registers a gesture and GestureListener combination. The listener will get called when the
	 * supplied gesture has been completed. 
	 * Note: Only 1 listener per gesture! If you try to register another listener to an already
	 * defined gesture, the previous listener will be unregistered.
	 * @param gesture The gesture which should invoke the listener.
	 * @param listener The GestureListener to be invoked.
	 * @return
	 */
	public boolean registerGesture(String gesture, GestureListener listener) {
		int gestureLength = gesture.length();
		boolean valid = true;
		for (int i = 0; i < gestureLength && valid; ++i)
			valid = isValidDirection(gesture.charAt(i));
		if (valid) {
			mGestureListeners.put(gesture, listener);
		}
		return valid;
	}
	
	private boolean isValidDirection(char direction) {
		String dir = String.valueOf(direction).toUpperCase();
		return dir.equals("U") || dir.equals("D") || dir.equals("L") || dir.equals("R");
	}
	
	private void gestureRecognizer(String gesture) {
		GestureListener listener = mGestureListeners.get(gesture);
		if (listener != null)
			listener.onRecognized();			
	}

	private void notifyGestureCompleteListeners(String gesture) {
		for (GestureCompleteListener listener : mGestureCompleteListeners)
			listener.onGestureCompleted(gesture);
	}

	/**
	 * This method will interpret the MotionEvents provided as parameter.
	 * You should invoke this method in your Activity.onTouchEvent(MotionEvent event)
	 * @param event The event to interpret
	 */
    public void touchEvent(MotionEvent event) {
    	int eventAction = event.getAction();
    	if (eventAction == MotionEvent.ACTION_DOWN) {
    		mGesture = "G"; // For convenience only, erases the need of a lot of index bounds tests
			mGestureX = -1;
			mGestureY = -1;
    	} else if (eventAction == MotionEvent.ACTION_MOVE) {
    		if (mGestureX == -1 || mGestureY == -1) {
    			mGestureX = event.getX();
    			mGestureY = event.getY();
    		} else {
    			float x = event.getX();
    			float y = event.getY();
    			Log.d(TAG, "pX: "+mGestureX+" x: "+x+" pY: "+mGestureY+" y: "+y);
    			float mX = mGestureX - x;
    			float mY = mGestureY - y;
    			if (Math.abs(mX) < Math.abs(mY))
    				if (mGestureY - y == 0)
    					;
    				else if (mGestureY - y > 0) {
    					if (mGesture.charAt(mGesture.length() - 1) != 'U')
    						mGesture += "U";
    				}
    				else {
    					if (mGesture.charAt(mGesture.length() - 1) != 'D')
    						mGesture += "D";
    				}
				else if (Math.abs(mX) > Math.abs(mY))
    				if (mGestureX - x == 0)
    					;
    				else if (mGestureX - x >= 0) {
    					if (mGesture.charAt(mGesture.length() - 1) != 'L')
    						mGesture += "L";
    				} else {
    					if (mGesture.charAt(mGesture.length() - 1) != 'R')
    						mGesture += "R";
    				}
    			
    			mGestureX = event.getX();
    			mGestureY = event.getY();
    		}    		
    	} else if (eventAction == MotionEvent.ACTION_UP) {
    		String gesture = mGesture.substring(1);
    		notifyGestureCompleteListeners(gesture);
    		gestureRecognizer(gesture);
    		
    		if (mDebug)
    			Log.d(TAG, "Gestures" + mGesture);
    	}
    		
	}
    
	private String mGesture;
	private float mGestureX;
	private float mGestureY;
	private boolean mDebug;
	
	private ArrayList<GestureCompleteListener> mGestureCompleteListeners;
	private HashMap<String, GestureListener> mGestureListeners;
}
