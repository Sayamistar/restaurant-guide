package uk.ac.aston.pyzerg.restaurantguide;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.OnZoomListener;
import uk.ac.aston.pyzer.restaurantguide.model.MyCurrentLocationOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.MyMapView;
import uk.ac.aston.pyzer.restaurantguide.model.MyOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.MyPlaceOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.Place.Geometry;
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
import com.google.android.maps.Overlay;

public class HaloView extends MySherlockMapActivity implements
		LocationListener, OnZoomListener {

	protected MyMapView mapView = null;
	private Drawable myCurrentMarker = null;
	private Drawable placeMarker = null;

	protected List<Overlay> mapOverlays;

	protected PlaceOverlay placeOverlay;
	private MyCurrentLocationOverlay myCurrentLocationOverlay;

	private MapController mapController;

	private LocationManager locationManager;

	private ArrayList<Place> places;
	
	private Place place;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.halo_view);

		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		mapView = (MyMapView) findViewById(R.id.halomap);

		myCurrentMarker = this.getResources().getDrawable(R.drawable.my_pin);
		placeMarker = this.getResources().getDrawable(R.drawable.ic_launcher);

		myCurrentLocationOverlay = new MyCurrentLocationOverlay(
				myCurrentMarker, mapView);
		placeOverlay = new PlaceOverlay(placeMarker, mapView);
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();

		mapView.setBuiltInZoomControls(true);
		mapView.setOnZoomListener(this);

		// extract the list of places from the intent
		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			// if placeList is passed, then we know that we want to view
			// a list of places, otherwise we are viewing a single one
			this.places = (ArrayList<Place>) extras
					.getSerializable("placeList");
			this.place = (Place) extras.getSerializable("place");
			if (this.place != null) { this.setTitle(place.getName()); }
		} else {
			this.places = null;
			this.place = null;
		}

	}

	private void animateToPlaceOnMap(final GeoPoint geopoint) {
		mapView.post(new Runnable() {

			@Override
			public void run() {
				mapView.invalidate();
				mapController.animateTo(geopoint);
				mapController.setZoom(Config.DEFAULT_ZOOM);
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
		
		if (place == null) { 
			// the case when a list of places it to be viewed
			for (Place place : places) {
				//point = place.getGeoPoint();
				Geometry.Location location = place.getGeometry().getLocation();
				point = this.getGeoPoint(location.getLat(), location.getLng());
				
				overlayitem = new MyPlaceOverlayItem(point, place);
				placeOverlay.addOverlay(overlayitem);
				placeOverlay.calculateItems();
			}
		} else {
			// the case when a single place is to be viewed
			//point = place.getGeoPoint();
			Geometry.Location location = place.getGeometry().getLocation();
			point = this.getGeoPoint(location.getLat(), location.getLng());
			overlayitem = new MyPlaceOverlayItem(point, place);
			placeOverlay.addOverlay(overlayitem);
		}
		
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
	
	public GeoPoint getGeoPoint(double lat, double lon) {
		int latE6 = (int) (lat * 1e6);
		int lonE6 = (int) (lon * 1e6);
		return new GeoPoint(latE6, lonE6);

	}
}
