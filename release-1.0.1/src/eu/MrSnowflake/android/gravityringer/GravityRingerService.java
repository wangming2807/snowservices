package eu.MrSnowflake.android.gravityringer;

import org.openintents.hardware.SensorManagerSimulator;
import org.openintents.provider.Hardware;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GravityRingerService extends Service implements SensorListener {
	// To be deleted in final
	// Only here to make it possible to use the OpenIntents sensor simulator
	public static final boolean USE_ANDROID_SENSORS = false;
	
	public static final String TAG = "GravityRingerService";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, TAG+" started");
		
		//to be deleted in final
	    if (USE_ANDROID_SENSORS) {
	        // Android sensor Manager
	    	mSensorMgr = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
	    } else {
	    	// OpenIntents Sensor Emulator!
			// Before calling any of the Simulator data, 
			// the Content resolver has to be set !! 
			Hardware.mContentResolver = getContentResolver(); 
			
			// Link sensor manager to OpenIntents Sensor simulator 
			mSensorMgr = (SensorManager) new SensorManagerSimulator((SensorManager)
					getSystemService(SENSOR_SERVICE));
			
			//sensorMgr.unregisterListener(mGraphView); 
			SensorManagerSimulator.connectSimulator(); 
	    }
	    
	    if (!mSensorMgr.registerListener(this, SensorManager.SENSOR_ORIENTATION))
	    	Log.e(TAG, "No suited sensor found");
	    else
	    	Log.i(TAG, "SensorListener found");
        audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mSilenced = audioMgr.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
        mKeyguardMgr = (KeyguardManager)this.getSystemService(Context.KEYGUARD_SERVICE);
	    
       SharedPreferences settings = getSharedPreferences(GravityRinger.Preferences.PREFS_NAME, 0);
       mDelayMillis = settings.getInt(GravityRinger.Preferences.DELAY, 1000);
       mSilenceGravity = settings.getFloat(GravityRinger.Preferences.SILENCE_GRAVITY, 5.0f);
	   mNoisyGravity = settings.getFloat(GravityRinger.Preferences.NOISY_GRAVITY, -5.0f);
	   mLockKeys = settings.getBoolean(GravityRinger.Preferences.LOCK_KEYS, true);
	}

	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		 /* Pitch indicates the tilt of the top of the device, with range -90 to 90. 
		 * Positive values indicate that the bottom of the device is tilted up and 
		 */
		if ((sensor & SensorManager.SENSOR_ORIENTATION) != 0) {
			// was 5.0
			if ((!mSilenced || noisyTask.active) && !silenceTask.active && values[SensorManager.DATA_Y] > mSilenceGravity) {
				mHandler.removeCallbacks(noisyTask);
				noisyTask.active = false;
				mHandler.postDelayed(silenceTask, mDelayMillis);
				Log.d(GravityRingerService.TAG, "SilenceTask Scheduled");
				silenceTask.active = true;
				//was -5.0
			} else if ((mSilenced || silenceTask.active) && !noisyTask.active && values[SensorManager.DATA_Y] < mNoisyGravity) {
				mHandler.removeCallbacks(silenceTask);
				silenceTask.active = false;
				mHandler.postDelayed(noisyTask, mDelayMillis);
				Log.d(GravityRingerService.TAG, "NoisyTask Scheduled");
				noisyTask.active = true;
			}
		}
	}	
	
	private NoisyTask noisyTask = new NoisyTask(); 
	class NoisyTask implements Runnable {
		public boolean active = false;
		@Override
		public void run() {
			if (!active)
				return;
			active = false;
			Log.d(GravityRingerService.TAG, "NoisyTask.run()");
			mSilenced = false;
			audioMgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			if (mLockKeys) {
				Log.d(TAG, "lockKeys");
				mKeyguardLock = mKeyguardMgr.newKeyguardLock(TAG);
				mKeyguardLock.reenableKeyguard();
				Log.i(TAG, "State--"+mKeyguardMgr.inKeyguardRestrictedInputMode()); 				
			}
		}			
	};
	
	private SilenceTask silenceTask = new SilenceTask();
	class SilenceTask implements Runnable {
		public boolean active = false;
		@Override
		public void run() {
			if (!active)
				return;
			active = false;
			Log.d(GravityRingerService.TAG, "SilenceTask.run()");
			mSilenced = true;
			audioMgr.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			if (mLockKeys && mKeyguardLock != null) {
				mKeyguardLock.disableKeyguard();
				Log.i(TAG, "State--"+mKeyguardMgr.inKeyguardRestrictedInputMode()); 				
			}
		}			
	};
	
	private boolean mSilencing = false;
	private boolean mNoising = false;
	private Handler mHandler = new Handler();
	
	// PREFERENCES
	//! The delay it will take to switch state
	private int mDelayMillis;
	//! The threshold at which angle the phone will get silenced 
	private float mSilenceGravity;
	//! The threshold at which angle the phone will get noisy 
	private float mNoisyGravity;
	//! Whether to lock the keys when silencing
	private boolean mLockKeys;
	
	public boolean mSilenced = false;

	private SensorManager mSensorMgr = null;
	private AudioManager audioMgr = null;
	private KeyguardManager mKeyguardMgr = null;
	private KeyguardManager.KeyguardLock mKeyguardLock = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
