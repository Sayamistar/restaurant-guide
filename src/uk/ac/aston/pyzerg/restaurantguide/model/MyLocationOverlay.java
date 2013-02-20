package uk.ac.aston.pyzerg.restaurantguide.model;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class MyLocationOverlay extends ItemizedOverlay<MyLocationOverlayItem> {
	
	private MyLocationOverlayItem myOverlayItem;
	private MapView mapView;
	
	public MyLocationOverlay(Drawable defaultMarker, MapView mapView) {
		 super(boundCenterBottom(defaultMarker));
			 this.mapView = mapView;
	}

	public void addOverlay(MyLocationOverlayItem overlay) {
	   this.myOverlayItem = overlay;
	   // populate the itemized overlay
	   // since we are only ever adding one overlay, this can be done here
	   populate();
	}
	
	public MyLocationOverlayItem getItem(){
		return this.myOverlayItem;
	}
	
	@Override
	// this is called when the populate() method is invoked
	protected MyLocationOverlayItem createItem(int i) {
		return this.myOverlayItem;
	}
	
	@Override
	public int size() {
		// we only have one item here
		if (this.myOverlayItem != null){
			return 1;
		} else {
			return 0;
		}
	}
	
	@Override
	// if my location is on the map, then display toast
	protected boolean onTap(int index) {
		if (this.size() == 1) {
			Toast.makeText(mapView.getContext(), myOverlayItem.getName(), Toast.LENGTH_SHORT).show();
		}
		
		return true;	
	}
}
