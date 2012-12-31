package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceItemizedOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceList;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceOverlayItem;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class HaloView extends SherlockMapActivity implements OnClickListener {
	private String myLoc;
	private PlaceList places;
	private PlaceItemizedOverlay itemizedOverlay;
	private MapView mv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.halo_view);
		itemizedOverlay = null;
		
		// find the UI components
		mv = (MapView) findViewById(R.id.halomap);
		mv.setBuiltInZoomControls(true);
		Button circleButton = (Button) this.findViewById(R.id.circleButton);
		Button lineButton = (Button) this.findViewById(R.id.lineButton);
		
		// extract the current location and list of places from the intent
		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			this.myLoc = (String) extras.getString("location");
			Log.i("HALO", "Location is " + myLoc);
			this.places = (PlaceList) extras.getSerializable("placeList");

			String[] locs = myLoc.split(",");
			if (locs.length == 2) {
				// create a GeoPoint for my location (the initial center of the
				// map)
				GeoPoint gp = new GeoPoint(
						(int) (Float.parseFloat(locs[0]) * 1e6),
						(int) (Float.parseFloat(locs[1]) * 1e6));
				int maxZoom = mv.getMaxZoomLevel();
				int initZoom = (int) (0.80 * (double) maxZoom);

				MapController mc = mv.getController();
				mc.setZoom(initZoom);
				mc.animateTo(gp);
				mc.setCenter(gp);

				// now set up the overlays
				List<Overlay> mapOverlays = mv.getOverlays();
				// this is the drawable which is plotted at the site on the map (a pushpin)
				Drawable drawable = this.getResources().getDrawable(
						R.drawable.red_pushpin);
				// The itemizedOverlay contains a list of overlays
				itemizedOverlay = new PlaceItemizedOverlay(drawable,
						HaloView.this);
				// set the center location
				itemizedOverlay.setCenter(gp);
				// add all the places to the itemizedOverlay
				for (Place p : places.getResults()) {
					// GeoPoint point = p.getGeoPoint();
					PlaceOverlayItem overlayItem = new PlaceOverlayItem(p);
					itemizedOverlay.addOverlay(overlayItem);
				}
				// add our itemizedOverlay to the overlays for this map view
				mapOverlays.add(itemizedOverlay);
				
				// set up the callbacks for the buttons
				circleButton.setOnClickListener(this);
				lineButton.setOnClickListener(this);
			}

		} else {
			this.myLoc = null;
			this.places = null;
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

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onClick(View v) {
		if (itemizedOverlay != null) {
			if (v.getId() == R.id.circleButton) {
				// toggle circles
				itemizedOverlay.toggleCircles();
			} else if (v.getId() == R.id.lineButton) {
				// toggle lines
				itemizedOverlay.toggleLines();
			}
			mv.invalidate();
		}
	}

}