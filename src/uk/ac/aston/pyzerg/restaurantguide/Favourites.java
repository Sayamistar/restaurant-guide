package uk.ac.aston.pyzerg.restaurantguide;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceDetail;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Favourites extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_layout);
        
        this.setTitle("Favourites");
        
        for (Place place : FavouritesList.getInstance().getPlaces()) {
        	PlaceDetail placeDetail = PlaceDetail.getPlaceDetail(place,
    				this.getResources());
        	
        	TextView name = new TextView(this);
    		name.setText(placeDetail.getResult().getName());
    		
    		TextView vicinity = new TextView(this);
    		vicinity.setText(placeDetail.getResult().getFormatted_address() + "\n"
    				+ placeDetail.getResult().getFormatted_phone_number());
    		
    		TextView rating = new TextView(this);
    		rating.setText("Rating: " + placeDetail.getResult().getRating());
    		
    		LinearLayout favouritesListLayout = (LinearLayout) findViewById(R.id.favouritesList_list);
    		favouritesListLayout.addView(name);
    		favouritesListLayout.addView(vicinity);
    		favouritesListLayout.addView(rating);
        }

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
