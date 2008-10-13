/* GameActivity - This is a game development library for android.
 * Copyright (C) 2008  Maarten 'MrSnowflake' Krijn
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package eu.MrSnowflake.android.droidgaming;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * 
 * @author Maarten 'MrSnowflake' Krijn
 */
public abstract class DroidGamingActivity extends Activity {
	private static final String TAG = "DroidGamingActivity";
	/** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentSet = false;
    }
    
    /**
     * Set the Activity as fullscreen.
     * Do not call this after calling setContentView() as this won't have effect!
     */
    public void setFullscreen() {
    	// To prevent errors, when called after setContentView()
    	if (mContentSet) {
    		Log.e(TAG, "ContentView already set. Unable to set fullscreen.");
    		return;
    	}
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    /**
     * Set the Activity as having no titlebar, leaving only the notification area visible.
     * Do not call this after calling setContentView() as this won't have effect!
     */
    public void setNoTitle() {
    	// To prevent errors, when called after setContentView()
    	if (mContentSet) {
    		Log.e(TAG, "ContentView already set. Unable to set fullscreen.");
    		return;
    	}
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    @Override
    public void setContentView(int layoutResID) {
    	// Help detect if setContentView got called before setFullscreen() of setNoTitle()
    	mContentSet = true;
    	super.setContentView(layoutResID);
    }

    /**
     * Call this in your onCreate, after setContentView().
     * /
    protected void init() throws GameViewNotFoundException {
    	/*mGameView = (DroidGamingView)findViewById(this.getResources().getR.id.game_view);
    	if (mGameView == null)
    		throw(new GameViewNotFoundException("No DroidGamingView with id game_view could be found."));
    		* /
    }*/

    protected void init(int resourceId) {
    	mGameView = (DroidGamingView)findViewById(resourceId);
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGameView.getGameThread().pause(); // pause game when Activity pauses
    }
    
    /**
     * Returns the GameView
     * @return The GameView
     */
    public DroidGamingView getGameView() {
    	return mGameView;
    }
    
    protected DroidGamingView mGameView;
    private boolean mContentSet;
}