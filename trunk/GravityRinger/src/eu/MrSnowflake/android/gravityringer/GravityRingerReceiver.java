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
		SharedPreferences settings = context.getSharedPreferences(Preferences.PREFS_NAME, 0);
		
		if (settings.getBoolean(Preferences.AUTO_START, true)) {
			Log.i(TAG, "Service Started");
			Intent i = new Intent(context, GravityRingerService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(i);
		}
	}
}
