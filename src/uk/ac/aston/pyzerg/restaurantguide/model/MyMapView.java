package uk.ac.aston.pyzerg.restaurantguide.model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class MyMapView extends MapView {
	
	// by default zoom is -1, because 0 could be a valid level
	private int oldZoomLevel = -1;
	private OnZoomListener onZoomListener;

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnZoomListener(OnZoomListener onZoomListener) {
		this.onZoomListener = onZoomListener;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// get new zoom level
		int newZoom = this.getZoomLevel();

		// if the new zoom is different from old
		if (newZoom != this.oldZoomLevel) {
			// and the map has already been zoomed before
			if (this.oldZoomLevel != -1 && this.onZoomListener != null) {
				// notify the listener that the map has changed
				this.onZoomListener.onZoomChanged();
			}

			// old zoom level becomes the new one
			this.oldZoomLevel = newZoom;
		}
	}
}
