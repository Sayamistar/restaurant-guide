package uk.ac.aston.pyzer.restaurantguide.model;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class MyCurrentLocationOverlay extends ItemizedOverlay<MyOverlayItem> {
	
	private MyOverlayItem myOverlayItem;
	private MapView mapView;
	
	public MyCurrentLocationOverlay(Drawable defaultMarker, MapView mapView) {
		 super(boundCenterBottom(defaultMarker));
			 this.mapView = mapView;
	}

	public void addOverlay(MyOverlayItem overlay) {
	   myOverlayItem = overlay;
	   populate();
	}
	
	public MyOverlayItem getMyItem(){
		return myOverlayItem;
	}
	
	@Override
	// we only have one item so just return it
	protected MyOverlayItem createItem(int i) {
		return myOverlayItem;
	}
	
	@Override
	public int size() {
		if(myOverlayItem != null){
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
