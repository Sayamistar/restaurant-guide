package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class PlaceItemizedOverlay extends ItemizedOverlay<PlaceOverlayItem> {
	private List<PlaceOverlayItem> items;
	private Context context;
	private GeoPoint center;
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
				dialog.setMessage(placeDetail.getResult()
						.getFormatted_address());
			} else {
				dialog.setMessage("No address");
			}
			dialog.show();
		}
		return true;
	}

	public void setCenter(GeoPoint center) {
		this.center = center;
	}

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
		mapview.getProjection().toPixels(center, centerSP);
		// set up a paint for the circles
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.STROKE);
		// set up a paint for the lines
		Paint line = new Paint();
		line.setStyle(Paint.Style.STROKE);
		line.setColor(Color.BLUE);
		// find out the width and height of the view
		int width = mapview.getWidth() / 2;
		int height = mapview.getHeight() / 2;

		// now process each overlay in the list
		for (PlaceOverlayItem place : this.items) {
			Point screenPts = new Point();
			mapview.getProjection().toPixels(place.getPoint(), screenPts);
			// check if this item is off screen or not
			if (screenPts.x > centerSP.x + width
					|| screenPts.x < centerSP.x - width
					|| screenPts.y > centerSP.y + height
					|| screenPts.y < centerSP.y - height) {
				// screenPts is offscreen
				// pythagoras theorum to calculate the distance of this item
				// from the center (my location)
				double dx = Math.abs(centerSP.x - screenPts.x);
				double dy = Math.abs(centerSP.y - screenPts.y);
				double radius = Math.sqrt((dx * dx) + (dy * dy));
				/*
				 * Log.i("OVERYLAY", "X diff " + dx + " Y Diff " + dy +
				 * " radius " + radius);
				 */

				// draw a circle around this item centered at its location
				if (showCircles) {
					paint.setStrokeWidth((float) (2500 / radius));
					canvas.drawCircle(screenPts.x, screenPts.y,
							(float) (radius * 0.7), paint);
				}
				// draw a line from the center to the location of this item
				if (showLines) {
					line.setStrokeWidth((float) (2500 / radius));
					canvas.drawLine(centerSP.x, centerSP.y, screenPts.x,
							screenPts.y, line);
				}
				// draw the pushpin icon
				canvas.drawBitmap(bitmap, screenPts.x, screenPts.y, null);
			} else {
				// the item is on the screen
				// so just draw the pushpin
				canvas.drawBitmap(bitmap, screenPts.x, screenPts.y, paint);
			}
		}
	}

}
