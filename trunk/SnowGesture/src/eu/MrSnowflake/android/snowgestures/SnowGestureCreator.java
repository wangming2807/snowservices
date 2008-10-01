package eu.MrSnowflake.android.snowgestures;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;

public class SnowGestureCreator extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtGesture = (EditText)findViewById(R.id.txtGesture);
        
        mGesture = new SnowGesture(true);
        mGesture.registerGestureCompleteListener(new SnowGesture.GestureCompleteListener() {
			@Override
			public void onGestureCompleted(String gesture) {
				SnowGestureCreator.this.txtGesture.setText(gesture);
			}
        });
    }    
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	mGesture.touchEvent(event);

		return super.onTouchEvent(event);
	}
    
    private EditText txtGesture;
	private SnowGesture mGesture;
}