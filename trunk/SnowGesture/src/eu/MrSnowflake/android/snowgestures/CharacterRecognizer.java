package eu.MrSnowflake.android.snowgestures;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;

public class CharacterRecognizer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        txtGesture = (EditText)findViewById(R.id.txtGesture);
        
        mLetters = new HashMap<String, Character>();
        
        mLetters.put("LDRUD", 'A');
        mLetters.put("DURDL", 'B');
        mLetters.put("LDR", 'C');
        mLetters.put("URDL", 'D');
        mLetters.put("LDRLDR", 'E');
        mLetters.put("LDRLD", 'F');
        mLetters.put("LDRULRD", 'G');
        mLetters.put("DURD", 'H');
        mLetters.put("D", 'I');
        mLetters.put("DL", 'J');
        mLetters.put("DRULDR", 'K');
        mLetters.put("DR", 'L');
        mLetters.put("UDUD", 'M');
        mLetters.put("UDU", 'N');
        mLetters.put("LDRUL", 'O');
        mLetters.put("URDL", 'P');
        mLetters.put("LURD", 'Q');
        mLetters.put("URDLD", 'R');
        mLetters.put("LDRDL", 'S');
        mLetters.put("LD", 'T');
        mLetters.put("DRU", 'U');
        mLetters.put("DU", 'V');
        mLetters.put("DUDU", 'W');
        mLetters.put("DLRURLDR", 'X');
        mLetters.put("DURDLD", 'Y');
        mLetters.put("RDLR", 'Z');
        mLetters.put("LURDLD", '?');
        
        
        mGesture = new SnowGesture(3.0f, true);
        mGesture.registerGestureCompleteListener(new SnowGesture.GestureCompleteListener() {
			@Override
			public void onGestureCompleted(String gesture) {
				String val = CharacterRecognizer.this.txtGesture.getText().toString();
				Character character = mLetters.get(gesture);
				if (character != null)
					CharacterRecognizer.this.txtGesture.setText(val+character);
				else if (gesture.equals("L")) {
					if (val.length() > 0)
						CharacterRecognizer.this.txtGesture.setText(val.substring(0, val.length() - 1));
				} else if (gesture.equals("R"))
					CharacterRecognizer.this.txtGesture.setText(val+" ");
				//CharacterRecognizer.this.txtGesture.setse
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
	private HashMap<String, Character> mLetters;
}
