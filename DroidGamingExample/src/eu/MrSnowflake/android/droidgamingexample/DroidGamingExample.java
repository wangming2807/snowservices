package eu.MrSnowflake.android.droidgamingexample;

import eu.MrSnowflake.android.droidgaming.DroidGamingActivity;
import eu.MrSnowflake.android.droidgaming.DroidGamingThread;
import android.os.Bundle;

public class DroidGamingExample extends DroidGamingActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setFullscreen();
		setContentView(R.layout.main);
		
		init(R.id.gameview);
		
		DroidGamingThread thread = getGameView().getGameThread();
		DroidGamingExampleGame game = new DroidGamingExampleGame(this);
		thread.registerGameListener(game);
    }
}