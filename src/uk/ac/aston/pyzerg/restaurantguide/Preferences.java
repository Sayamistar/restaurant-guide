package uk.ac.aston.pyzerg.restaurantguide;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Preferences extends SherlockActivity {
	
	private Spinner radiusSpinner;
	private PreferenceManager prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences_layout);
		
		radiusSpinner = (Spinner) findViewById(R.id.radius_spinner);
		
		String[] radiuses = new String[] {"50", "100", "500", "1000", "2000", "5000", "10000", "50000"};
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, radiuses);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		radiusSpinner.setAdapter(adapter);
		
		int existingRadius = adapter.getPosition(Config.RADIUS);
		radiusSpinner.setSelection(existingRadius);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.save:
			// save the radius in application context
			Config.RADIUS = (String) radiusSpinner.getSelectedItem();
			prefs = new PreferenceManager(this.getApplicationContext());
			prefs.setRadius(Config.RADIUS);
			prefs.save();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
