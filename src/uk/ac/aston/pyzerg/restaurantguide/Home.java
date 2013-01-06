package uk.ac.aston.pyzerg.restaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Home extends SherlockActivity {
	
	private ImageButton findButton;
	private ImageButton favouritesButton;
	
	private PreferenceManager prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        
        findButton = (ImageButton) findViewById(R.id.find);
        favouritesButton = (ImageButton) findViewById(R.id.favourites);
        
        findButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Home.this, SelectCategory.class);
				startActivity(intent);
			}
        });
        
        // retrieve preferences
        prefs = new PreferenceManager(this.getApplicationContext());
        Config.RADIUS = prefs.getRadius();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Intent intent;

		switch (item.getItemId()) {
		case R.id.preferences:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
