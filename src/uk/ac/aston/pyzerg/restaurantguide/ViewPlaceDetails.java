package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritePlace;
import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceDetail;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceItemizedOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceOverlayItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ViewPlaceDetails extends SherlockMapActivity implements
		LocationListener {
	private GeoPoint gp;
	private Place place;
	private PlaceDetail placeDetail;

	private ImageView alreadyFavourite;

	private Button sendToFriend;
	private Button savePlace;
	private LocationManager locationManager;
	private Location currentLocation;

	private boolean isFavourite = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);

		// set up location manager to work with GPS
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		// get the current location (or last known) from the location manager
		currentLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		place = extras != null ? (Place) extras.getSerializable("place") : null;
		placeDetail = PlaceDetail.getPlaceDetail(place, this.getResources());

		TextView name = (TextView) this.findViewById(R.id.name);
		name.setText(placeDetail.getResult().getName());

		// hide the favourite info until we know what to display
		alreadyFavourite = (ImageView) findViewById(R.id.alreadyFavourite);
		alreadyFavourite.setVisibility(View.GONE);
		savePlace = (Button) this.findViewById(R.id.savePlace);
		savePlace.setVisibility(View.GONE);

		alreadyFavourite.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), Favourites.class);
				startActivity(intent);
			}

		});

		sendToFriend = (Button) this.findViewById(R.id.sendToFriend);
		sendToFriend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.setData(Uri.parse("sms:"));
				sendIntent.putExtra("sms_body", placeDetail.getResult()
						.getName()
						+ "\n\n"
						+ placeDetail.getResult().getVicinity()
						+ "\n"
						+ placeDetail.getResult().getFormatted_phone_number());
				startActivity(sendIntent);
			}

		});

		checkFavourites();

		createMapOverlays();
	}

	private void createMapOverlays() {
		MapView mv = (MapView) this.findViewById(R.id.map);
		mv.setSatellite(false);
		// mv.setTraffic(true);
		mv.setBuiltInZoomControls(true);

		gp = placeDetail.getResult().getGeoPoint();
		int maxZoom = mv.getMaxZoomLevel();
		int initZoom = (int) (0.90 * (double) maxZoom);

		MapController mc = mv.getController();
		mc.setZoom(initZoom);
		mc.animateTo(gp);
		mc.setCenter(gp);

		Drawable restaurantIcon = this.getResources().getDrawable(
				R.drawable.ic_launcher);

		Drawable youAreHere = this.getResources().getDrawable(
				R.drawable.red_pushpin);

		PlaceItemizedOverlay placeItemizedOverlay = new PlaceItemizedOverlay(
				restaurantIcon, this);
		
		//placeItemizedOverlay.setCenter(gp);
		PlaceOverlayItem placeOverlayItem = new PlaceOverlayItem(place);

		// get current position
		double lati = 0;
		double longi = 0;

		if (currentLocation != null) {

			lati = currentLocation.getLatitude();
			longi = currentLocation.getLongitude();

		}

		GeoPoint point = createGeoPoint(lati, longi);

		CustomItemizedOverlay customItemizedOverlay = new CustomItemizedOverlay(
				youAreHere, this);

		OverlayItem currentLocation = new OverlayItem(point, "You are here!",
				"Lat: " + lati + "\nLong: " + longi);

		// now set up the overlays
		List<Overlay> mapOverlays = mv.getOverlays();

		placeItemizedOverlay.addOverlay(placeOverlayItem);
		customItemizedOverlay.addOverlay(currentLocation);
		
		mapOverlays.add(placeItemizedOverlay);
		mapOverlays.add(customItemizedOverlay);

		mv.invalidate();
		
		Log.i(Config.MSGTAG, "Got back: " + placeDetail.getResult());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public static GeoPoint createGeoPoint(double lati, double longi) {
		return new GeoPoint((int) (lati * 1E6), (int) (longi * 1E6));
	}

	/**
	 * When the activity starts up, request updates
	 */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		// re-check whether place is in favourites
		checkFavourites();
	}

	/**
	 * When the activity is paused, stop listening for updates
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// locationManager.removeUpdates(this);
	}

	public void onLocationChanged(Location arg0) {
		// currentLocation = arg0;
		// createMapOverlays();

	}

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	private void checkFavourites() {

		isFavourite = false;
		// check if the active place is in favourites list
		// if it is, show the favourites icon
		for (FavouritePlace fp : FavouritesList.getInstance().getPlaces()) {
			if (fp.getPlace().getName().equals(place.getName())) {
				isFavourite = true;
				break;
			}
		}

		// if is favourite, show the favourites icon
		if (isFavourite) {
			alreadyFavourite.setVisibility(View.VISIBLE);
		} else {
			// swap fields around
			alreadyFavourite.setVisibility(View.GONE);
			savePlace.setVisibility(View.VISIBLE);

			savePlace.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					
					AlertDialog.Builder alert = new AlertDialog.Builder(ViewPlaceDetails.this);

					alert.setTitle("Added to favourites!");
					alert.setMessage("Add some notes too:");

					// Set an EditText view to get user input 
					final EditText input = new EditText(ViewPlaceDetails.this);
					alert.setView(input);

					alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						// add the item to the favourites list
						FavouritesList.getInstance().getPlaces().add(new FavouritePlace(place, value));
					  }
					});

					alert.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
					  public void onClick(DialogInterface dialog, int whichButton) {
						  // add the item to the favourites list
						  FavouritesList.getInstance().getPlaces().add(new FavouritePlace(place, "None"));
					  }
					});

					alert.show();
					
					// hide the save button and show the already favourite icon
					ViewPlaceDetails.this.isFavourite = true;
					savePlace.setVisibility(View.GONE);
					alreadyFavourite.setVisibility(View.VISIBLE);
				}
			});
		}
	}
}
