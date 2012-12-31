package uk.ac.aston.pyzerg.restaurantguide;

import android.util.Log;

public class Config {
	public static final boolean WANT_LOG_MESSAGES = true;
	public static final String MSGTAG = "TOURIST_INFO";
	public static String RADIUS = "2000";
	public static final String TYPE = "food";

	public static void printLogMessage(String msg) {
		if (WANT_LOG_MESSAGES) {
			Log.i(MSGTAG, msg);
		}
	}
}
