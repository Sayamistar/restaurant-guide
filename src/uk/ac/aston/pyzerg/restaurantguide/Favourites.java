package uk.ac.aston.pyzerg.restaurantguide;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class Favourites extends SherlockActivity implements OnItemClickListener {

	private boolean showDeleteButton;
	private MenuItem deleteFavourite;
	private int removePosition;
	private TextView noFavourites;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favourites_layout);

		this.setTitle("Favourites");

		showDeleteButton = false;
		
		noFavourites = (TextView) findViewById(R.id.favouritesList_noFavourites);
		noFavourites.setVisibility(View.GONE);

		showFavourites();
	}

	@Override
	public void onResume() {
		super.onResume();
		showFavourites();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.favourites_menu, menu);

		deleteFavourite = menu.findItem(R.id.delete_favourite);
		if (!showDeleteButton) {
			deleteFavourite.setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Intent intent;

		switch (item.getItemId()) {
		case R.id.delete_favourite:
			FavouritesList.getInstance().getPlaces().remove(removePosition);
			deleteFavourite.setVisible(false);
			// refresh activity
			this.finish();
			startActivity(getIntent());

			break;
		case R.id.preferences:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Place p = FavouritesList.getInstance().getPlaces().get(position);
		Intent i = new Intent(this, ViewPlaceDetails.class);
		i.putExtra("place", p);
		startActivity(i);
	}

	private void showFavourites() {
		ListView placeList = (ListView) findViewById(R.id.placeList);
		placeList.setAdapter(null);
		placeList.invalidate();

		List<String> titles = new ArrayList<String>(FavouritesList
				.getInstance().getPlaces().size());
		for (Place p : FavouritesList.getInstance().getPlaces()) {
			titles.add(p.toString());
		}
		
		if (titles.size() == 0) {
			noFavourites.setVisibility(View.VISIBLE);
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				Favourites.this, R.layout.place_title, R.id.placeTitle, titles);
		ListView l = (ListView) Favourites.this.findViewById(R.id.placeList);
		l.setAdapter(adapter);
		l.setOnItemClickListener(Favourites.this);
		l.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				showDeleteButton = true;
				deleteFavourite.setVisible(true);
				removePosition = position;
				return false;
			}

		});
	}
}
