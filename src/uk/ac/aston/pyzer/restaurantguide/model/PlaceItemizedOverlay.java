package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class PlaceItemizedOverlay extends ItemizedOverlay<PlaceOverlayItem> {
	private List<PlaceOverlayItem> items;
	private Context context;
	//private GeoPoint center;
	private Bitmap bitmap;
	private boolean showCircles;
	private boolean showLines;


	public PlaceItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		init(defaultMarker, null);
	}

	public PlaceItemizedOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		init(defaultMarker, context);
	}

	private void init(Drawable defaultMarker, Context context) {
		this.context = context;
		items = new ArrayList<PlaceOverlayItem>();
		bitmap = ((BitmapDrawable) defaultMarker).getBitmap();
		showLines = showCircles = true;
	}

	/**
	 * Add an overlay to the list.
	 * 
	 * @param overlay
	 *            The overlay to be added
	 */
	public void addOverlay(PlaceOverlayItem overlay) {
		items.add(overlay);
		//this.populate();
	}
	
	public void populateOverlay() {
		this.populate();
	}

	@Override
	protected PlaceOverlayItem createItem(int i) {
		return items.get(i);
	}

	@Override
	public int size() {
		return items.size();
	}

	/**
	 * The onTab handler for when we tap on one of the icons on the screen.
	 * 
	 * @param index
	 *            The index in the list of the icon that was tapped
	 */
	@Override
	protected boolean onTap(int index) {
		PlaceOverlayItem item = items.get(index);
		Log.i("PLACES", item.getTitle() + "\n" + item.getSnippet());
		if (item != null) {
			Resources res = context.getResources();
			PlaceDetail placeDetail = null;
			// we look up the extended details to get the formatted address
			placeDetail = PlaceDetail.getPlaceDetail(item.getPlace(), res);
			// create the dialog to show the place details
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setTitle(item.getTitle());
			if (placeDetail != null) {
				
				
				String ratingValue = "";
				String reviews = "";
				
				if (placeDetail.getResult().getRating() != 0) {
					ratingValue = placeDetail.getResult().getRating() + "/5";
					
					reviews = " (based on " + placeDetail.getResult().getReviews().size() + " reviews)";
				}
				else {
					ratingValue = "None";
					reviews = " (no reviews)";
				}
				
				dialog.setMessage("Address: " + 
						placeDetail.getResult().getVicinity() + "\n" + 
						placeDetail.getResult().getFormatted_phone_number() + "\n\nRating: " +
						ratingValue +  reviews);
			} else {
				dialog.setMessage("No address");
			}
			dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}                      
		      });
			
			dialog.show();
		}
		return true;
	}

	/*public void setCenter(GeoPoint center) {
		this.center = center;
	}*/

	/**
	 * Toggle the display of Halos.
	 */
	public void toggleCircles() {
		showCircles = !showCircles;
	}

	/**
	 * Toggle the display of lines.
	 */
	public void toggleLines() {
		showLines = !showLines;
	}

	/**
	 * Draw all the overlays in the list.
	 */
	public void draw(Canvas canvas, MapView mapview, boolean b) {
		// create a
		Point centerSP = new Point();
		mapview.getProjection().toPixels(mapview.getMapCenter(), centerSP);
		
		// set up a paint for the circles
		Paint circlePaint = new Paint();
		circlePaint.setColor(0x55ff0000);
		circlePaint.setAlpha(10);
		circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		// set up a paint for the lines
		Paint linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(Color.BLUE);

		// find out the width and height of the view
		int width = mapview.getWidth();
		int height = mapview.getHeight();

		// now process each overlay in the list
		for (PlaceOverlayItem place : this.items) {
			Point screenPts = new Point();
			mapview.getProjection().toPixels(place.getPoint(), screenPts);
			// check if this item is off screen or not
			if (screenPts.x > centerSP.x + (width / 2)
					|| screenPts.x < centerSP.x - (width / 2)
					|| screenPts.y > centerSP.y + (height / 2)
					|| screenPts.y < centerSP.y - (height / 2)) {
				// screenPts is offscreen
				// pythagoras theorum to calculate the distance of this item
				// from the center (my location)
				double dx = Math.abs(centerSP.x - screenPts.x);
				double dy = Math.abs(centerSP.y - screenPts.y);
				
				int padding = 30;
				
				double ox = dx - ((width / 2) - padding);
				double oy = dy - ((height / 2) - padding);
				
				if (ox < 0) ox = 0;
				if (oy < 0) oy = 0;
				
				double distanceSP = Math.sqrt((ox*ox) + (oy*oy));
				
				// draw a circle around this item centered at its location
				drawCircle(screenPts, (float)distanceSP, circlePaint, canvas);
				// draw a line from the center to the location of this item
				//drawLine(centerSP, screenPts, linePaint, canvas);
				
				// draw the pushpin icon
				canvas.drawBitmap(bitmap, screenPts.x-16, screenPts.y-16, null);
			} else {
				// the item is on the screen
				// so just draw the pushpin
				canvas.drawBitmap(bitmap, screenPts.x-16, screenPts.y-16, null);
			}
		}
	}
	
	/*
	 * Draw a circle centered at the given location with the given radius.
	 * Uses the paint provided and drawn on the given canvas.
	 */
	private void drawCircle(Point location, float radius, Paint paint, Canvas canvas) {
		if (showCircles) {
			// set the width of the line
			paint.setStrokeWidth((float) 3.0);
			// draw a circle radius 100 pixels
			canvas.drawCircle(location.x, location.y, radius, paint);
		}
	}

	private void drawLine(Point from, Point to, Paint paint, Canvas canvas) {
		if (showLines) {
			// set the width of the line
			paint.setStrokeWidth((float) 3.0);
			// draw a line between the two points
			canvas.drawLine(from.x, from.y, to.x, to.y, paint);
		}

	}

}



