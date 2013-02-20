package uk.ac.aston.pyzerg.restaurantguide.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceManager {

	private SharedPreferences prefs = null;
	private Editor editor = null;

	public PreferenceManager(Context context) {
		prefs = context.getSharedPreferences("PREFS_PRIVATE",
				Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	public void setRadius(String radius) {
		if (editor == null)
			return;
		else {
			editor.putString("radius", radius);
		}
	}
	
	public String getRadius() {
		if (prefs == null) {
			return Config.RADIUS;
		} else {
			return prefs.getString("radius", Config.RADIUS);
		}
	}

	public void save() {
		if (editor == null)
			return;
		else {
			editor.commit();
		}
	}
}