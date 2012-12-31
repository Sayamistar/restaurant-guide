package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ViewMap_ extends SherlockMapActivity implements LocationListener {
	
    private MapView mapView;
    private LocationManager locationManager;
    private Location currentLocation;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewmap_layout);
        
        mapView = (MapView) findViewById(R.id.mapview);       
        mapView.setBuiltInZoomControls(true);

        // set up location manager to work with GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // get the current location (or last known) from the location manager
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        createMapOverlays();

        // when the current location is found, stop listening for updates (to preserve battery)
        // locationManager.removeUpdates(this);
        
    }
    
    public static GeoPoint createGeoPoint(double lati, double longi) {
    	return new GeoPoint((int) (lati * 1E6), (int) (longi * 1E6));
    }
    
    /**
     * Create map overlays and position a pointer at the current GPS location.
     */
    private void createMapOverlays() {
        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.clear();
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.red_pin);
        
        CustomItemizedOverlay itemizedOverlay = new CustomItemizedOverlay(drawable, this);
        
        
        double lati = 0;
        double longi = 0;
        
        if (currentLocation != null) {
        	
        	lati = currentLocation.getLatitude();
        	longi = currentLocation.getLongitude();
        	
        	Log.i("test", lati + longi +"");
        }
        
        GeoPoint point = createGeoPoint(lati, longi);
        
        OverlayItem overlayitem = 
        		new OverlayItem(point, "Your location:", "Lat: " + lati 
            	+ "\n Long: " + longi);
        
        
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
        
        MapController mapController = mapView.getController();
        
        mapController.animateTo(point);
        mapController.setZoom(15);
    }
    
    /**
     * When the activity starts up, request updates
     */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    
    /**
     * When the activity is paused, stop listening for updates
     */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    protected boolean isRouteDisplayed() {	
        return false;
    }

	public void onLocationChanged(Location arg0) {	
		currentLocation = arg0;
		createMapOverlays();
	}

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

}
