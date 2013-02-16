package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;

/**
 * The technique for checking for an overlap is taken from 
 * an online tutorial provided by Abramovich Igor.
 * 
 * Source: http://habrahabr.ru/post/139929/ 
 * (Russion Language, converted into English through Google Translator)
 * 
 */

import uk.ac.aston.pyzerg.restaurantguide.Config;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class PlaceOverlay extends ItemizedOverlay<MyPlaceOverlayItem> {
	
	private ArrayList<MyPlaceOverlayItem> myOverlaysAll = new ArrayList<MyPlaceOverlayItem>();
	private ArrayList<MyPlaceOverlayItem> myOverlays = new ArrayList<MyPlaceOverlayItem>();
	
	private MapView mapView;
	private Bitmap bitmap;
	private Paint circlePaint;
	private Point centerSP;
	private Place place;
	private PlaceDetail placeDetail;

	public PlaceOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;
		bitmap = ((BitmapDrawable) defaultMarker).getBitmap();
		populate();
	}

	public void addOverlay(MyPlaceOverlayItem overlay) {
		myOverlaysAll.add(overlay);
		myOverlays.add(overlay);

	}

	public void doPopulate() {
		populate();
		setLastFocusedIndex(-1);
	}

	@Override
	protected MyPlaceOverlayItem createItem(int i) {
		return myOverlays.get(i);
	}

	@Override
	public int size() {
		return myOverlays.size();
	}

	public MapView getMapView() {
		return this.mapView;
	}

	// check to see if items/groups are overlapping
	private boolean isImposition(MyPlaceOverlayItem item1,
			MyPlaceOverlayItem item2) {

		// get the width of the map in pixels
		int latspan = mapView.getLatitudeSpan();
		// find out minimum distance between pins based on width 
		// and no. of pins per view
		int delta = latspan / Config.pinsPerWidth;

		// work out the x and y distances
		int dx = item1.getPoint().getLatitudeE6()
				- item2.getPoint().getLatitudeE6();
		int dy = item1.getPoint().getLongitudeE6()
				- item2.getPoint().getLongitudeE6();

		// calculate the distance between the two points using pythag
		double dist = Math.sqrt(dx * dx + dy * dy);

		// if the distance between the points is within the minimum distance
		// range, then there is overlapping
		if (dist < delta) {
			return true;
		} else {
			return false;
		}

	}

	// whenever we launch the MapView, we need to clear both lists of overlays
	public void clear() {
		myOverlaysAll.clear();
		myOverlays.clear();
	}

	public void calculateItems() {

		myOverlaysClear();

		boolean isImposition;

		// loop through all the items 
		// (an item may be single, or have a list of items)
		for (MyPlaceOverlayItem itemFromAll : myOverlaysAll) {
			isImposition = false;
			// loop through all the items/groups again
			// using a sep list so that we can modify
			for (MyPlaceOverlayItem item : myOverlays) {
				// if the items/groups match, then we have the item already
				// so we don't need to do anything except break out the loop
				if (itemFromAll == item) {
					isImposition = true;
					break;

				}
				// if there is overlapping between the items/groups
				// then we will need to add the item to the group
				if (isImposition(itemFromAll, item)) {
					item.addList(itemFromAll);
					isImposition = true;
					break;
				}
			}

			// if there is no overlapping, then we will need to add this new
			// item/group to the list
			if (!isImposition) {
				myOverlays.add(itemFromAll);
			}
		}

		// only populate after all the items are added to improve performance
		doPopulate();

	}

	private void myOverlaysClear() {
		for (MyPlaceOverlayItem item : myOverlaysAll) {
			item.getList().clear();

		}
		
		myOverlays.clear();
	}

	@Override
	protected boolean onTap(int index) {

		// if there are no details available, it is because
		// there is overlapping so user must zoom more
		if (!myOverlays.get(index).placeDetailsAvailable()) {
			Toast.makeText(
					mapView.getContext(),
					(myOverlays.get(index).getList().size() + 1)
							+ " places here (Zoom for more)", Toast.LENGTH_SHORT).show();
		} else {
			// display the place details
			Resources res = mapView.getContext().getResources();
			placeDetail = null;
			// we look up the extended details to get the formatted address
			placeDetail = PlaceDetail.getPlaceDetail(myOverlays.get(index)
					.getPlace(), res);
			place = myOverlays.get(index).getPlace();
			
			// construct a dialog box with the place details
			new PlaceOverlayDialog(mapView.getContext(), place, placeDetail);
		}

		return true;
	}

	// draw thte places with the halos
	public void draw(Canvas canvas, MapView mapview, boolean b) {
		if (!objectExists(centerSP)) {
			centerSP = new Point();
		}

		mapview.getProjection().toPixels(mapview.getMapCenter(), centerSP);

		if (!objectExists(circlePaint)) {
			circlePaint = new Paint();
			circlePaint.setColor(0x55ff0000);
			circlePaint.setAlpha(10);
			circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		}

		// find out the width and height of the view
		int width = mapview.getWidth();
		int height = mapview.getHeight();

		for (MyPlaceOverlayItem p : this.myOverlays) {

			Point screenPts = new Point();
			mapview.getProjection().toPixels(p.getPoint(), screenPts);

			// check if this item is off screen or not
			if (screenPts.x > centerSP.x + (width / 2)
					|| screenPts.x < centerSP.x - (width / 2)
					|| screenPts.y > centerSP.y + (height / 2)
					|| screenPts.y < centerSP.y - (height / 2)) {
				
				// screenPts is offscreen
				// pythag to calculate the distance of this item
				// from the center (my location)
				double dx = Math.abs(centerSP.x - screenPts.x);
				double dy = Math.abs(centerSP.y - screenPts.y);

				int padding = 30;

				double ox = dx - ((width / 2) - padding);
				double oy = dy - ((height / 2) - padding);

				if (ox < 0)
					ox = 0;
				if (oy < 0)
					oy = 0;

				double distanceSP = Math.sqrt((ox * ox) + (oy * oy));

				// if distance in pixels is > 1000, we won't draw the circles
				// improves performance
				// TODO: not perfect
				if (distanceSP <= 1000) {
					// draw a circle around this item centered at its location
					drawCircle(screenPts, (float) distanceSP, circlePaint,
							canvas);
				}

				canvas.drawBitmap(bitmap, screenPts.x - 32, screenPts.y - 32,
						null);
			} else {
				canvas.drawBitmap(bitmap, screenPts.x - 32, screenPts.y - 32,
						null);
			}
		}
	}

	private void drawCircle(Point location, float radius, Paint paint,
			Canvas canvas) {
		paint.setStrokeWidth((float) 3.0);
		canvas.drawCircle(location.x, location.y, radius, paint);
	}

	// used to prevent objects that don't change from being created
	// everytime draw() is called
	private boolean objectExists(Object o) {
		if (o != null) {
			return true;
		}

		return false;
	}
}