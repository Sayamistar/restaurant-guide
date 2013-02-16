package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem {

	private String name;

	private ArrayList<MyOverlayItem> list = new ArrayList<MyOverlayItem>();

	public MyOverlayItem(GeoPoint point, String name) {
		super(point, "", "");
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void addList(MyOverlayItem item) {
		list.add(item);
	}

	public ArrayList<MyOverlayItem> getList() {
		return list;
	}
}
