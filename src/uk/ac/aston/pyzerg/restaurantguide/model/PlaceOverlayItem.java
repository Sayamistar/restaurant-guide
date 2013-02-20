package uk.ac.aston.pyzerg.restaurantguide.model;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PlaceOverlayItem extends OverlayItem {

	private Place place;

	// an overlay item may simply be one item, or a list of more overlay items
	// this is used for clustering items that meet a specific criteria
	private ArrayList<PlaceOverlayItem> overlayList = new ArrayList<PlaceOverlayItem>();

	public PlaceOverlayItem(GeoPoint point, Place place) {
		super(point, "", "");
		this.place = place;
	}

	// if there is more than one item in the list, return false
	// because user will need to zoom more to see the place
	public boolean placeDetailsAvailable() {
		if (this.overlayList.size() > 0) {
			return false;
		} else {
			return true;
		}

	}

	// add item to the overlay list
	// which means this is a group overlay now
	public void addOverlayList(PlaceOverlayItem item) {
		this.overlayList.add(item);
	}

	public ArrayList<PlaceOverlayItem> getOverlayList() {
		return this.overlayList;
	}
	
	public Place getPlace() {
		return this.place;
	}
}
