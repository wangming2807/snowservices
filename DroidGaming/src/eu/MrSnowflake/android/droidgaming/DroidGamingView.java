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

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * The View you need to inflate in your contentview to be able to use DroidGaming. 
 * 
 * @author Maarten 'MrSnowflake' Krijn
 * @version %I%, %G%
 */
public class DroidGamingView extends SurfaceView implements SurfaceHolder.Callback {
    public DroidGamingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        mThread = new DroidGamingThread(context, holder);

        setFocusable(true); // make sure we get key events
    }
    
    public DroidGamingThread getGameThread() {
    	return mThread;
    }

    public void setClearColor(int a, int r, int g, int b) {
    	
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return mThread.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return mThread.onKeyMultiple(keyCode, repeatCount, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mThread.onKeyUp(keyCode, event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mThread.onTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return mThread.onTrackballEvent(event);
	}
	
	/* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        mThread.setSurfaceSize(width, height);
    }

    /**
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        mThread.setRunning(true);
        mThread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
	
	private DroidGamingThread mThread;
}
