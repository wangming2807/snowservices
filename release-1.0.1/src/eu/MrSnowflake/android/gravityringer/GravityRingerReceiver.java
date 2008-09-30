package eu.MrSnowflake.android.gravityringer;

import eu.MrSnowflake.android.gravityringer.GravityRinger.Preferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class GravityRingerReceiver extends BroadcastReceiver {
	private static final String TAG = "GravityRingerReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		GravityRinger act = new GravityRinger();
		SharedPreferences settings = act.getSharedPreferences(Preferences.PREFS_NAME, 0);
		
		if (settings.getBoolean(Preferences.AUTO_START, true)) {
			Log.i(TAG, "Service Started");
			act.startService(new Intent(act, GravityRingerService.class));
		}
	}
}
