package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.MyCurrentLocationOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.MyOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.MyPlaceOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceOverlay;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ViewPlaceDetails extends MySherlockMapActivity implements
		LocationListener {

	private static final int DEFAULT_ZOOM = 15;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;

	private Place place;

	private List<Overlay> mapOverlays;
	private PlaceOverlay placeOverlay;
	private MyCurrentLocationOverlay myCurrentLocationOverlay;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);

		// set up location manager to work with GPS
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mapView = (MapView) this.findViewById(R.id.map);

		Drawable myCurrentMarker = this.getResources().getDrawable(
				R.drawable.my_pin_red);
		Drawable placeMarker = this.getResources().getDrawable(
				R.drawable.my_pin);

		myCurrentLocationOverlay = new MyCurrentLocationOverlay(
				myCurrentMarker, mapView);
		placeOverlay = new PlaceOverlay(placeMarker, mapView);
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();

		mapView.setBuiltInZoomControls(true);

		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		place = extras != null ? (Place) extras.getSerializable("place") : null;
		
		this.setTitle(place.getName());
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

	public static GeoPoint createGeoPoint(double lati, double longi) {
		return new GeoPoint((int) (lati * 1E6), (int) (longi * 1E6));
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

	private void displayPlacesOnMap() {
		mapOverlays.remove(placeOverlay);

		GeoPoint point = null;
		MyPlaceOverlayItem overlayitem = null;
		placeOverlay.clear();

		point = place.getGeoPoint();
		overlayitem = new MyPlaceOverlayItem(point, place);
		placeOverlay.addOverlay(overlayitem);

		placeOverlay.calculateItems();

		placeOverlay.doPopulate();

		if (placeOverlay.size() > 0) {
			mapOverlays.add(placeOverlay);
			mapView.postInvalidate();
		}

	}
}
