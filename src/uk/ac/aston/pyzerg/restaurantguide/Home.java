package uk.ac.aston.pyzerg.restaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class Home extends MySherlockActivity {
	
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
        
        favouritesButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Favourites.class);
				startActivity(intent);
				
			}
        	
        });
        
        // retrieve preferences
        prefs = new PreferenceManager(this.getApplicationContext());
        Config.RADIUS = prefs.getRadius();
    }
}
