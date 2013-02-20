package uk.ac.aston.pyzerg.restaurantguide.model;

import java.util.ArrayList;

import uk.ac.aston.pyzerg.restaurantguide.config.Config;
import uk.ac.aston.pyzerg.restaurantguide.ui.PlaceOverlayDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

/**
 * The technique for checking for an overlap is taken from 
 * an online tutorial provided by Abramovich Igor.
 * 
 * Source: http://habrahabr.ru/post/139929/ 
 * (Russion Language, converted into English through Google Translator)
 * 
 */

public class PlaceOverlay extends ItemizedOverlay<PlaceOverlayItem> {
	
	private ArrayList<PlaceOverlayItem> myOverlaysAll = new ArrayList<PlaceOverlayItem>();
	private ArrayList<PlaceOverlayItem> myOverlays = new ArrayList<PlaceOverlayItem>();
	
	private MapView mapView;
	private Bitmap bitmap;
	private Paint circlePaint;
	private Point centerSP;
	private Point screenPts;
	private Place place;
	private PlaceDetail placeDetail;
	private Canvas buffer;
	private Bitmap bufferBitmap;
	private GeoPoint lastDrawGeoPoint;
	private int lastZoomLevel = -1;
	
	public PlaceOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;
		this.bitmap = ((BitmapDrawable) defaultMarker).getBitmap();
		
		populate();
	}

	// add to both lists so that later we can compare for overlaps
	// TODO: not that efficient if there are hundreds of items
	public void addOverlay(PlaceOverlayItem overlay) {
		this.myOverlaysAll.add(overlay);
		this.myOverlays.add(overlay);
	}

	public void doPopulate() {
		populate();
		setLastFocusedIndex(-1);
	}

	@Override
	protected PlaceOverlayItem createItem(int i) {
		return this.myOverlays.get(i);
	}

	@Override
	public int size() {
		return this.myOverlays.size();
	}

	public MapView getMapView() {
		return this.mapView;
	}

	// check to see if items/groups are overlapping
	// Source: Abramovich Igor (http://habrahabr.ru/post/139929/)
	private boolean isImposition(PlaceOverlayItem item1,
			PlaceOverlayItem item2) {

		// get the width of the map in pixels
		int latspan = this.mapView.getLatitudeSpan();
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
		this.myOverlaysAll.clear();
		this.myOverlays.clear();
	}

	// work out which items overlap by comparing both lists
	// Source: Abramovich Igor (http://habrahabr.ru/post/139929/)
	public void calculateItems() {

		myOverlaysClear();

		boolean isImposition;

		// loop through all the items 
		// (an item may be single, or have a list of items)
		for (PlaceOverlayItem itemFromAll : this.myOverlaysAll) {
			isImposition = false;
			// loop through all the items/groups again
			// using a separate list so that we can modify
			for (PlaceOverlayItem item : this.myOverlays) {
				// if the items/groups match, then we have the item already
				// so we don't need to do anything except break out the loop
				if (itemFromAll == item) {
					isImposition = true;
					break;

				}
				// if there is overlapping between the items/groups
				// then we will need to add the item to the group
				if (isImposition(itemFromAll, item)) {
					item.addOverlayList(itemFromAll);
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
		for (PlaceOverlayItem item : myOverlaysAll) {
			item.getOverlayList().clear();

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
					(myOverlays.get(index).getOverlayList().size() + 1)
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
		
		boolean paintCircles = false;
		
		// find out the width and height of the view
		int width = mapview.getWidth();
		int height = mapview.getHeight();
		
		if (!objectExists(this.centerSP)) {
			this.centerSP = new Point();
			this.screenPts = new Point();
		}
		
		Projection projection = mapview.getProjection();
		projection.toPixels(mapview.getMapCenter(), this.centerSP);
		
		int cX = this.centerSP.x;
		int cY = this.centerSP.y;
		
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		
		if (this.lastDrawGeoPoint == null || mapview.getZoomLevel() != this.lastZoomLevel) {
			// *** refactor this because it is a repeating block
			this.bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    this.buffer = new Canvas(this.bufferBitmap);
		    this.lastDrawGeoPoint = mapview.getMapCenter();
		    this.lastZoomLevel = mapview.getZoomLevel();
		    paintCircles = true;		    
		} else {
			Point lastDrawPoint = new Point();
			projection.toPixels(lastDrawGeoPoint, lastDrawPoint);
			int lastDrawDX = lastDrawPoint.x - cX;
			int lastDrawDY = lastDrawPoint.y - cY; 
			int lastDrawDistance = (int) Math.sqrt(Math.pow(lastDrawDX, 2) + Math.pow(lastDrawDY, 2));			
			if (lastDrawDistance > 20) {
				// *** refactor this because it is a repeating block
				this.bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			    this.buffer = new Canvas(this.bufferBitmap);
			    this.lastDrawGeoPoint = mapview.getMapCenter();
			    this.lastZoomLevel = mapview.getZoomLevel();
			    paintCircles = true;		    
			}
		}

		if (!objectExists(this.circlePaint)) {
			this.circlePaint = new Paint();
			this.circlePaint.setColor(0x55ff0000);
			this.circlePaint.setAlpha(30);
			this.circlePaint.setStyle(Paint.Style.FILL);
		}

		for (PlaceOverlayItem p : this.myOverlays) {

			projection.toPixels(p.getPoint(), screenPts);
			
			int sX = screenPts.x;
			int sY = screenPts.y;

			// check if this item is off screen or not
			if (paintCircles && (sX > cX + halfWidth
					|| sX < cX - halfWidth
					|| sY > cY + halfHeight
					|| sY < cY - halfHeight)) {
				
				// screenPts is offscreen
				// pythag to calculate the distance of this item
				// from the center (my location)
				double dx = Math.abs(cX - sX);
				double dy = Math.abs(cY - sY);

				int padding = 30;

				double outerX = dx - (halfWidth - padding);
				double outerY = dy - (halfHeight - padding);

				if (outerX < 0) outerX = 0;
				if (outerY < 0) outerY = 0;

				int distanceSP = (int) Math.sqrt((outerX * outerX) + (outerY * outerY));				
				this.buffer.drawCircle(sX, sY, distanceSP, this.circlePaint);
			}
			
			canvas.drawBitmap(this.bitmap, sX - 32, sY - 40,
					null);
		}
		
		// copying the data from the buffer to the map
		Rect src = new Rect(0, 0, width, height); // rectangle from the buffer
		Rect dst = new Rect(cX - halfWidth, cY - halfHeight, cX + halfWidth, cY + halfHeight); // rectangle on the map canvas 
		canvas.drawBitmap(this.bufferBitmap, src, dst, null);
	}

	/* private void drawCircle(Point location, float radius, Paint paint,
			Canvas canvas) {
		// paint.setStrokeWidth((float) 3.0);
		// canvas.drawCircle(location.x, location.y, radius, paint);
	}*/

	// used to prevent objects that don't change from being created
	// everytime draw() is called
	private boolean objectExists(Object o) {
		if (o != null) {
			return true;
		}

		return false;
	}
}