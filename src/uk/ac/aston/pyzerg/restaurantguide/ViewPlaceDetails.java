package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritePlace;
import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.MyCurrentLocationOverlay;
import uk.ac.aston.pyzer.restaurantguide.model.MyOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.MyPlaceOverlayItem;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceDetail;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceOverlay;
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

public class ViewPlaceDetails extends SherlockMapActivity implements
		LocationListener {

	private static final int DEFAULT_ZOOM = 15;
	private MapView mapView;
	private MapController mapController;
	private LocationManager locationManager;

	private Place place;
	private PlaceDetail placeDetail;

	private List<Overlay> mapOverlays;
	private PlaceOverlay placeOverlay;
	private MyCurrentLocationOverlay myCurrentLocationOverlay;

	private ImageView alreadyFavourite;

	private Button sendToFriend;
	private Button savePlace;

	private boolean isFavourite = false;

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

					AlertDialog.Builder alert = new AlertDialog.Builder(
							ViewPlaceDetails.this);

					alert.setTitle("Added to favourites!");
					alert.setMessage("Add some notes too:");

					// Set an EditText view to get user input
					final EditText input = new EditText(ViewPlaceDetails.this);
					alert.setView(input);

					alert.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									String value = input.getText().toString();
									// add the item to the favourites list
									FavouritesList
											.getInstance()
											.getPlaces()
											.add(new FavouritePlace(place,
													value));
								}
							});

					alert.setNegativeButton("Ignore",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// add the item to the favourites list
									FavouritesList
											.getInstance()
											.getPlaces()
											.add(new FavouritePlace(place,
													"None"));
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
