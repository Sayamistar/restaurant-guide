package uk.ac.aston.pyzerg.restaurantguide;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritePlace;
import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Favourites extends SherlockActivity implements OnItemClickListener {
	
	private TextView noFavourites;
	
	private boolean editMode;
	private boolean emptyList;
	private MenuItem editButton;
	private MenuItem deleteButton;
	
	private ListView placeList;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favourites_layout);

		this.setTitle("Favourites");
		
		// by default, assume there are favourites
		noFavourites = (TextView) findViewById(R.id.favouritesList_noFavourites);
		noFavourites.setVisibility(View.GONE);
		
		// check whether "editMode" was passed to the activity
		editMode = getIntent().getBooleanExtra("editMode", false);
		placeList = (ListView) Favourites.this.findViewById(R.id.placeList);

		// if there are no favourites, we need to know whether to display edit button
		if (showFavourites(editMode) == 0) { emptyList = true; }
	}

	@Override
	public void onResume() {
		super.onResume();
		if (showFavourites(editMode) == 0) { emptyList = true; }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.favourites_menu, menu);
		
		deleteButton = (MenuItem) menu.findItem(R.id.delete_favourite);
		editButton = (MenuItem) menu.findItem(R.id.edit_favourite);
		
		// if there are no favourites, we don't want to display the edit button
		if (emptyList) { 
			editButton.setVisible(false);
		}
		
		// if we are not in "edit mode", make sure delete button is not active
		// if we are in "edit mode", make sure it is active, and remove the edit button
		if (!editMode) { 
			deleteButton.setVisible(false);
		}
		else {
			deleteButton.setVisible(true);
			editButton.setVisible(false);
			
			this.setTitle("Edit Mode");
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Intent intent;

		switch (item.getItemId()) {
		case R.id.edit_favourite:
			editMode = true;
			// we are now in edit mode so pass this to the new activity
			this.finish();
			this.startActivity(getIntent().putExtra("editMode", editMode));

			break;
			
		case R.id.delete_favourite:
			// find all the checked items and remove them from the arraylist
			SparseBooleanArray checkedItems = placeList.getCheckedItemPositions();
			if (checkedItems != null) {
			    for (int i = checkedItems.size()-1; i >= 0; i--) {
			        if (checkedItems.valueAt(i)) {
			        	FavouritesList.getInstance().getPlaces().remove(checkedItems.keyAt(i));
			        }
			    }
			}

			// we are no longer in edit mode so we need to discard the "editMode"
			// instance from the intent bundle
			editMode = false;
			deleteButton.setVisible(false);
			// refresh activity
			this.finish();
			this.getIntent().removeExtra("editMode");
			this.startActivity(this.getIntent());

			break;
		case R.id.preferences:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Place p = FavouritesList.getInstance().getPlaces().get(position).getPlace();
		Intent i = new Intent(this, ViewPlaceDetails.class);
		i.putExtra("place", p);
		startActivity(i);
	}

	private int showFavourites(boolean editMode) {
		placeList.setAdapter(null);
		placeList.invalidate();

		List<String> titles = new ArrayList<String>(FavouritesList
				.getInstance().getPlaces().size());
		for (FavouritePlace fp : FavouritesList.getInstance().getPlaces()) {
			titles.add(fp.toString());
		}

		// if there are no results, show the textview
		if (titles.size() == 0) {
			noFavourites.setVisibility(View.VISIBLE);
		}
		
		ArrayAdapter<String> adapter;
		
		// in edit mode we use separate listview with checked boxes
		if (editMode) {
			adapter = new ArrayAdapter<String>(this, R.layout.place_title_checked, titles);
		}
		else {
			adapter = new ArrayAdapter<String>(
				Favourites.this, R.layout.place_title, R.id.placeTitle, titles);
			placeList.setOnItemClickListener(Favourites.this);
		}
		placeList.setAdapter(adapter);
		
		return titles.size();

	}
}
