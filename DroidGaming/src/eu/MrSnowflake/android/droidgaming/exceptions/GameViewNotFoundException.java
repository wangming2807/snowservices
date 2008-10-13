package eu.MrSnowflake.android.droidgaming.exceptions;

public class GameViewNotFoundException extends Exception {
	private static final long serialVersionUID = 3944581775430711831L;

	public GameViewNotFoundException() {
		super();
	}
	
	public GameViewNotFoundException(String message) {
		super(message);
	}
	
	public GameViewNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
    
	public GameViewNotFoundException(Throwable cause){
		super(cause);
	}
}
