package uk.ac.aston.pyzerg.restaurantguide.ui;

import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzerg.restaurantguide.config.Config;
import uk.ac.aston.pyzerg.restaurantguide.model.MyLocationOverlay;
import uk.ac.aston.pyzerg.restaurantguide.model.MyLocationOverlayItem;
import uk.ac.aston.pyzerg.restaurantguide.model.MyMapView;
import uk.ac.aston.pyzerg.restaurantguide.model.OnZoomListener;
import uk.ac.aston.pyzerg.restaurantguide.model.Place;
import uk.ac.aston.pyzerg.restaurantguide.model.Place.Geometry;
import uk.ac.aston.pyzerg.restaurantguide.model.PlaceOverlay;
import uk.ac.aston.pyzerg.restaurantguide.model.PlaceOverlayItem;
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

	protected MyMapView mapView;
	private MapController mapController;
	
	private Drawable myLocationMarker;
	private Drawable placeMarker;

	protected List<Overlay> mapOverlays;
	
	private MyLocationOverlay myLocationOverlay;
	protected PlaceOverlay placeOverlay;
	
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

		myLocationMarker = this.getResources().getDrawable(R.drawable.green_pin);
		placeMarker = this.getResources().getDrawable(R.drawable.ic_launcher);

		myLocationOverlay = new MyLocationOverlay(
				myLocationMarker, mapView);
		placeOverlay = new PlaceOverlay(placeMarker, mapView);
		
		mapOverlays = mapView.getOverlays();
		mapController = mapView.getController();

		mapView.setOnZoomListener(this);
		mapView.setBuiltInZoomControls(true);
		

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

	private void setCurrentLocation(GeoPoint myGeoPoint) {
		mapOverlays.remove(myLocationOverlay);
		MyLocationOverlayItem myCurrentItem = new MyLocationOverlayItem(myGeoPoint,
				"You are here!");
		myLocationOverlay.addOverlay(myCurrentItem);
		mapOverlays.add(myLocationOverlay);
		mapView.invalidate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO: tidy this up a bit
		Location currentLocation;
		GeoPoint myGeoPoint;
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
				 LocationManager.NETWORK_PROVIDER, 5000, 10, this);
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		} else {			
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
			currentLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		
		if (currentLocation != null) {
			myGeoPoint = this.getGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
		} else {
			myGeoPoint = this.getGeoPoint(51.50746, -0.122967);
		}
		
		setCurrentLocation(myGeoPoint);
		addPlacesToMap();
		
		mapController.animateTo(myGeoPoint);
		mapController.setZoom(Config.DEFAULT_ZOOM_LEVEL);
	}

	private void addPlacesToMap() {
		// remove the place overlay from the view
		mapOverlays.remove(placeOverlay);
		// and clear it
		placeOverlay.clear();
		
		GeoPoint point;
		PlaceOverlayItem overlayitem;
		
		if (place == null) { 
			// the case when a list of places it to be viewed
			for (Place place : places) {
				Geometry.Location location = place.getGeometry().getLocation();
				point = this.getGeoPoint(location.getLat(), location.getLng());
				
				overlayitem = new PlaceOverlayItem(point, place);
				placeOverlay.addOverlay(overlayitem);
				placeOverlay.calculateItems();
			}
		} else {
			// the case when a single place is to be viewed
			Geometry.Location location = place.getGeometry().getLocation();
			point = this.getGeoPoint(location.getLat(), location.getLng());
			overlayitem = new PlaceOverlayItem(point, place);
			placeOverlay.addOverlay(overlayitem);
		}
		
		// populate the place overlay only after all have been added
		placeOverlay.doPopulate();

		// only add the overlay to the map if items were added
		if (placeOverlay.size() > 0) {
			mapOverlays.add(placeOverlay);
			mapView.postInvalidate();
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		// locationManager.removeUpdates(this);
		setCurrentLocation(this.getGeoPoint(location.getLatitude(), location.getLongitude()));
		// addPlacesToMap();
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
	
	private GeoPoint getGeoPoint(double lat, double lon) {
		int latE6 = (int) (lat * 1e6);
		int lonE6 = (int) (lon * 1e6);
		
		return new GeoPoint(latE6, lonE6);
	}
}