package uk.ac.aston.pyzerg.restaurantguide;

import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.FavouritesList;
import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceDetail;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class ViewPlaceDetails extends SherlockMapActivity {
	private GeoPoint gp;
	private Place place;

	private ImageView alreadyFavourite;
	private Button savePlace;
	
	class MapOverlay extends com.google.android.maps.Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(gp, screenPts);

			// ---add the marker---

			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.red_pushpin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y -25, null);

			// draw a circle
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			canvas.drawCircle(screenPts.x, screenPts.y, 50, paint);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);
		Intent i = this.getIntent();
		Bundle extras = i.getExtras();
		place = extras != null ? (Place) extras.getSerializable("place")
				: null;
		PlaceDetail placeDetail = PlaceDetail.getPlaceDetail(place,
				this.getResources());

		TextView name = (TextView) this.findViewById(R.id.name);
		name.setText(placeDetail.getResult().getName());
		TextView vicinity = (TextView) this.findViewById(R.id.vicinity);
		vicinity.setText(placeDetail.getResult().getFormatted_address() + "\n"
				+ placeDetail.getResult().getFormatted_phone_number());
		TextView rating = (TextView) this.findViewById(R.id.rating);
		rating.setText("Rating: " + placeDetail.getResult().getRating());
		
		// hide the favourite info until we know what to display
		alreadyFavourite = (ImageView) findViewById(R.id.alreadyFavourite);
		alreadyFavourite.setVisibility(View.GONE);
		savePlace = (Button) this.findViewById(R.id.savePlace);
		savePlace.setVisibility(View.GONE);
		
		boolean isFavourite = false;
		
		// check if the active place is in favourites list
		// if it is, show the favourites icon
		for (Place p : FavouritesList.getInstance().getPlaces()) {
			if (p.getName().equals(place.getName())) {
				isFavourite = true;
				alreadyFavourite.setVisibility(View.VISIBLE);
				break;
			}
		}
		
		// if the active place is not in the favourites list
		// display the save button only
		if (!isFavourite) { 
			savePlace.setVisibility(View.VISIBLE);
			
			savePlace.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// add the item to the favourites list
					FavouritesList.getInstance().getPlaces().add(place);
					// hide the save button and show the already favourite icon
					savePlace.setVisibility(View.GONE);
					alreadyFavourite.setVisibility(View.VISIBLE);
					
					alreadyFavourite.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							Intent intent = new Intent(v.getContext(), Favourites.class);
							startActivity(intent);
						}
						
					});
				}
			});
		}

		////////////////////////////////////////////

		MapView mv = (MapView) this.findViewById(R.id.map);
		mv.setSatellite(false);
		//mv.setTraffic(true);
		mv.setBuiltInZoomControls(true);

		gp = placeDetail.getResult().getGeoPoint();
		int maxZoom = mv.getMaxZoomLevel();
		int initZoom = (int) (0.90 * (double) maxZoom);

		MapController mc = mv.getController();
		mc.setZoom(initZoom);
		mc.animateTo(gp);
		mc.setCenter(gp);

		MapOverlay mapOverlay = new MapOverlay();
		List<Overlay> listOfOverlays = mv.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);

		mv.invalidate();
		Log.i(Config.MSGTAG, "Got back: " + placeDetail.getResult());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
	}
}
