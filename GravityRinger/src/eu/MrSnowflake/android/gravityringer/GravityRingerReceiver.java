package eu.MrSnowflake.android.gravityringer;

import eu.MrSnowflake.android.gravityringer.GravityRinger.Preferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class GravityRingerReceiver extends BroadcastReceiver {
	private static final String TAG = "GravityRingerReceiver";
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION))
		{
			SharedPreferences settings = context.getSharedPreferences(Preferences.PREFS_NAME, 0);
		
			if (settings.getBoolean(Preferences.AUTO_START, false)) {
				Log.i(TAG, "GravityRingerService Started");
				context.startService(new Intent(context, GravityRingerService.class));
			}
		} 
	}
}
