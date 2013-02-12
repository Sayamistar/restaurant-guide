package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyPlaceOverlayItem extends OverlayItem {

	private Place place;

	private ArrayList<MyPlaceOverlayItem> list = new ArrayList<MyPlaceOverlayItem>();

	public MyPlaceOverlayItem(GeoPoint point, Place place) {
		super(point, "", "");
		this.place = place;
	}

	// if there is more than one item in the list, return false
	// because user will need to zoom more to see the place
	public boolean placeDetailsAvailable() {
		if (list.size() > 0) {
			//return "There are " + (list.size() + 1) + " places.";
			return false;
		} else {
			return true;
		}

	}

	// add item to the group (i.e. this is not a single place anymore)
	public void addList(MyPlaceOverlayItem item) {
		list.add(item);
	}

	public ArrayList<MyPlaceOverlayItem> getList() {
		return list;
	}
	
	public Place getPlace() {
		return this.place;
	}
}
