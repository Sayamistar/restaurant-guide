package uk.ac.aston.pyzer.restaurantguide.model;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PlaceOverlayItem extends OverlayItem {
	private final GeoPoint point;
	private final Place place;

	public PlaceOverlayItem(Place p) {
		super(p.getGeoPoint(), p.getName(), p.getFormatted_address());
		this.point = p.getGeoPoint();
		this.place = p;
	}

	/**
	 * @return the point
	 */
	public GeoPoint getPoint() {
		return point;
	}

	/**
	 * @return the place
	 */
	public Place getPlace() {
		return place;
	}

}
