package uk.ac.aston.pyzerg.restaurantguide;

import android.util.Log;

public class Config {
	public static final boolean WANT_LOG_MESSAGES = true;
	public static final String MSGTAG = "Restaurant Guide";
	public static String RADIUS = "2000";
	public static final String TYPE = "food";
	public static final int pinsPerWidth = 20;
	public static final int DEFAULT_ZOOM = 15;
	
	public static void printLogMessage(String msg) {
		if (WANT_LOG_MESSAGES) {
			Log.i(MSGTAG, msg);
		}
	}
}
