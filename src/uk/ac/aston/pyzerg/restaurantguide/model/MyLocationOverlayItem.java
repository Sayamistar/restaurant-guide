package uk.ac.aston.pyzerg.restaurantguide.model;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlayItem extends OverlayItem {
	
	private String name;

	public MyLocationOverlayItem(GeoPoint point, String name) {
		super(point, "", "");
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
