package eu.MrSnowflake.android.gravityringer;

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
import android.telephony.TelephonyManager;
import android.util.Log;

public class GravityRingerService extends Service implements SensorListener {
	public static final String TAG = "GravityRingerService";

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG, TAG+" started");
		
		//to be deleted in final
        // Android sensor Manager
    	mSensorMgr = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
	    
	    if (!mSensorMgr.registerListener(this, SensorManager.SENSOR_ORIENTATION)) {
	    	Log.e(TAG, "No suited sensor found");
	    	stopSelf();
	    } else
	    	Log.i(TAG, "SensorListener found");
        audioMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mSilenced = audioMgr.getRingerMode() != AudioManager.RINGER_MODE_NORMAL;
        //mKeyguardMgr = (KeyguardManager)this.getSystemService(Context.KEYGUARD_SERVICE);
        mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    
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
			if (mTelephonyMgr.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
				// Don't switch to noisy when the phone is ringing.
				Log.i(TAG, "Noisy postponed");
				mHandler.postDelayed(this, mDelayMillis);
				return;
			}
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
	private TelephonyManager mTelephonyMgr;	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
