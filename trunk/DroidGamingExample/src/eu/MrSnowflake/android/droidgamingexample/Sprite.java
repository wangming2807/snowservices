package eu.MrSnowflake.android.droidgamingexample;

import android.graphics.Bitmap;

public class Sprite {
	public Sprite(Bitmap bmp) {
		mBitmap = bmp;
		x = 0;
		y = 0;
	}
	
	public float x;
	public float y;
	public Bitmap mBitmap;
}
