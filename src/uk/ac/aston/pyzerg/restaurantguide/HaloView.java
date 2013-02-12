package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.IOnZoomListener;
import uk.ac.aston.pyzer.restaurantguide.model.MyCurrentLocationOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.MyMapView;
import uk.ac.aston.pyzer.restaurantguide.model.MyOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.MyPlaceOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceList;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceOverlay;
import uk.ac.aston.pyzerg.restaurantguide.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;

public class HaloView extends SherlockMapActivity implements
		LocationListener, IOnZoomListener {

	private static final int DEFAULT_ZOOM = 15;

	private MyMapView mapView = null;
	private Drawable myCurrentMarker = null;
	private Drawable placeMarker = null;

	private List<Overlay> mapOverlays;

	private PlaceOverlay placeOverlay;
	private MyCurrentLocationOverlay myCurrentLocationOverlay;

	private MapController mapController;

	private LocationManager locationManager;

	private PlaceList places;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.halo_view);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		mapView = (MyMapView) findViewById(R.id.halomap);

		myCurrentMarker = this.getResources()
				.getDrawable(R.drawable.my_pin_red);
		placeMarker = this.getResources().getDrawable(R.drawable.my_pin);

		myCurrentLocationOverlay = new MyCurrentLocationOverlay(
				myCurrentMarker, mapView);
		placeOverlay = new PlaceOverlay(placeMarker, mapView);
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();

		mapView.setBuiltInZoomControls(true);
		mapView.setOnZoomListener(this);

		// extract the current location and list of places from the intent
		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			this.places = (PlaceList) extras.getSerializable("placeList");
		} else {
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

	private void animateToPlaceOnMap(final GeoPoint geopoint) {
		mapView.post(new Runnable() {

			@Override
			public void run() {
				mapView.invalidate();
				mapController.animateTo(geopoint);
				mapController.setZoom(DEFAULT_ZOOM);
			}
		});
	}

	private void setCurrentGeopoint(double myLatitude, double myLongitude) {
		final GeoPoint myCurrentGeoPoint = new GeoPoint(
				(int) (myLatitude * 1E6), (int) (myLongitude * 1E6));

		MyOverlayItem myCurrentItem = new MyOverlayItem(myCurrentGeoPoint,
				"Current Location");
		myCurrentLocationOverlay.addOverlay(myCurrentItem);
		mapOverlays.add(myCurrentLocationOverlay);

		animateToPlaceOnMap(myCurrentGeoPoint);
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 5000, 100, this);

	}

	private void displayPlacesOnMap() {
		mapOverlays.remove(placeOverlay);

		GeoPoint point = null;
		MyPlaceOverlayItem overlayitem = null;
		placeOverlay.clear();

		for (Place place : places.getResults()) {
			point = place.getGeoPoint();
			overlayitem = new MyPlaceOverlayItem(point, place);
			placeOverlay.addOverlay(overlayitem);
		}

		placeOverlay.calculateItems();

		placeOverlay.doPopulate();

		if (placeOverlay.size() > 0) {
			mapOverlays.add(placeOverlay);
			mapView.postInvalidate();
		}

	}

	@Override
	public void onLocationChanged(Location location) {

		locationManager.removeUpdates(this);

		double myLatitude = location.getLatitude();
		double myLongitude = location.getLongitude();

		setCurrentGeopoint(myLatitude, myLongitude);

		displayPlacesOnMap();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onZoomChanged() {
		if (placeOverlay != null) {
			placeOverlay.calculateItems();
		}

	}

}
