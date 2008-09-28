package eu.MrSnowflake.android.gravityringer;

import org.openintents.hardware.SensorManagerSimulator;
import org.openintents.provider.Hardware;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class GravityRinger extends Activity  implements SensorListener {
	// To be deleted in final
	// Only here to make it possible to use the OpenIntents sensor simulator
	public static final boolean USE_ANDROID_SENSORS = false;
	
	public static final String TAG = "GravityRinger";
	
	private static final int MODE_NOISY = 0;
	private static final int MODE_SILENCE = 1;
	
	private static final int MENU_SAVE = Menu.FIRST + 1;
	private static final int MENU_CANCEL = Menu.FIRST + 2;
	
	public class Preferences
	{
		public static final String PREFS_NAME = "GravityRinger";
		public static final String DELAY = "DelayMilis";
		public static final String SILENCE_GRAVITY = "SilenceGravity";
		public static final String NOISY_GRAVITY = "NoisyGravity";
		public static final String LOCK_KEYS = "LockKeys";
		public static final String AUTO_START = "AutoStart";
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
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
			
			SensorManagerSimulator.connectSimulator(); 
	    }
	    
	    if (!mSensorMgr.registerListener(this, SensorManager.SENSOR_ORIENTATION))
	    {
	    	Log.e(TAG, "Could not register a listener for SENSOR_ORIENTATION");
	    	//Error msg
	    	finish();
	    }
        
        txtDelay = (EditText)findViewById(R.id.txtDelay);
        txtNoisyThreshold = (EditText)findViewById(R.id.txtNoisyThreshold);
        txtSilenceThreshold = (EditText)findViewById(R.id.txtSilenceThreshold);
        chkKeyLock = (CheckBox)findViewById(R.id.chkKeyLock);
		chkAutoStart = (CheckBox)findViewById(R.id.chkAutoStart);

        mSettings = getSharedPreferences(Preferences.PREFS_NAME, 0);
        
        txtDelay.setText(""+mSettings.getInt(Preferences.DELAY, 1000));
        txtSilenceThreshold.setText(""+mSettings.getFloat(Preferences.SILENCE_GRAVITY, 5.0f));
		txtNoisyThreshold.setText(""+mSettings.getFloat(Preferences.NOISY_GRAVITY, -5.0f));
		chkKeyLock.setChecked(mSettings.getBoolean(Preferences.LOCK_KEYS, true));
		chkAutoStart.setChecked(mSettings.getBoolean(Preferences.AUTO_START, true));

		btnSetSilence = (Button)findViewById(R.id.btnSilenceThreshold);
		btnSetSilence.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mReadSensor = true;
				mReadMode = MODE_SILENCE;
				btnSetSilence.setEnabled(false);
				btnSetNoisy.setEnabled(false);
			}
		});
		btnSetNoisy = (Button)findViewById(R.id.btnNoisyThreshold);
		btnSetNoisy.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				mReadSensor = true;
				mReadMode = MODE_NOISY;
				btnSetSilence.setEnabled(false);
				btnSetNoisy.setEnabled(false);
			}
		});
		
        btnActivateService = (Button)findViewById(R.id.activateServiceBtn);
        btnActivateService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startService();
		        Toast.makeText(GravityRinger.this, "GravityRinger service started", Toast.LENGTH_SHORT).show();
			}
        });

        btnDeactivateService = (Button)findViewById(R.id.deactivateServiceBtn);
        btnDeactivateService.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent serviceIntent = new Intent(GravityRinger.this, GravityRingerService.class);
				Log.d(TAG, "Stop service");
		        if (stopService(serviceIntent)) {
			        Toast.makeText(GravityRinger.this, "GravityRinger service stopped", Toast.LENGTH_SHORT).show();
		        	Log.d(TAG, "Service Stopped!");
		        }
			}
        });
    }
    
    private void startService() {
		Intent serviceIntent = new Intent(GravityRinger.this, GravityRingerService.class);
		Log.d(TAG, "Start service");
        startService(serviceIntent);
    }
    
	@Override
	public void onAccuracyChanged(int arg0, int arg1) {
	}
	
	@Override
	public void onSensorChanged(int sensor, float[] values) {
		if (mReadSensor && (sensor | SensorManager.SENSOR_ORIENTATION) != 0) {
			Log.i(TAG, "Threshold changed "+values[SensorManager.DATA_Y]+" "+mReadMode);
			switch (mReadMode) {
			case MODE_NOISY:
				txtNoisyThreshold.setText(""+values[SensorManager.DATA_Y]);
				break;
			case MODE_SILENCE:
				txtSilenceThreshold.setText(""+values[SensorManager.DATA_Y]);
				break;
			}
			mReadSensor = false;
			btnSetSilence.setEnabled(true);
			btnSetNoisy.setEnabled(true);
		}
	}    
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuItem item = menu.add(0, MENU_SAVE, 0, "Save");
    	item.setIcon(android.R.drawable.ic_menu_save);
    	//item.setAlphabeticShortcut('S');

    	MenuItem mnuClose = menu.add(0, MENU_CANCEL, 0, "Cancel/close");
    	mnuClose.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	//mnuClose.setAlphabeticShortcut('Q');
    	
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SAVE:
			if (saveSettings())
				;//finish();
			break;
		case MENU_CANCEL:
			finish();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private boolean saveSettings() {
		SharedPreferences.Editor editor = mSettings.edit();
		int delay = 0;
		Float silence = 0f, noisy = 0f;
		boolean error = false, lockKeys;
		try {
			delay = Integer.parseInt(this.txtDelay.getText().toString());
		} catch (NumberFormatException ex) {
			Toast.makeText(this, "Delay is not a valid integer!", Toast.LENGTH_SHORT).show();
			error = true;
		}
		try {
			silence = Float.parseFloat(this.txtSilenceThreshold.getText().toString());
		} catch (NumberFormatException ex) {
			Toast.makeText(this, "Silence threshold is not a valid number!", Toast.LENGTH_SHORT).show();
			error = true;
		}
		try {
			noisy = Float.parseFloat(this.txtNoisyThreshold.getText().toString());
		} catch (NumberFormatException ex) {
			Toast.makeText(this, "Noisy threashold is not a valid number!", Toast.LENGTH_SHORT).show();
			error = true;
		}
		
		lockKeys = this.chkKeyLock.isChecked();
		boolean autoStart = chkAutoStart.isChecked();
		
		if (!error) {
			editor.putInt(Preferences.DELAY, delay);
			editor.putFloat(Preferences.NOISY_GRAVITY, noisy);
			editor.putFloat(Preferences.SILENCE_GRAVITY, silence);
			editor.putBoolean(Preferences.LOCK_KEYS, lockKeys);
			editor.putBoolean(Preferences.AUTO_START, autoStart);
			editor.commit();
			Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
			startService();
		}
		return !error;
	}

	private EditText txtDelay;
    private EditText txtNoisyThreshold;
    private EditText txtSilenceThreshold;
    private CheckBox chkKeyLock;
    private CheckBox chkAutoStart;
    
    private Button btnSetNoisy;
    private Button btnSetSilence;
    private Button btnActivateService;
    private Button btnDeactivateService;
	private SensorManager mSensorMgr = null;
	
	private SharedPreferences mSettings;
	private float mCurrentAngle;
	private boolean mReadSensor = false;
	private int mReadMode = 0;
}