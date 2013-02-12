package uk.ac.aston.pyzer.restaurantguide.model;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.google.android.maps.MapView;

public class MyMapView extends MapView {
	int oldZoomLevel = -1;
	IOnZoomListener onZoomListener;

	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyMapView(Context context, String apiKey) {
		super(context, apiKey);
	}

	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnZoomListener(IOnZoomListener onZoomListener) {

		this.onZoomListener = onZoomListener;

	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		// get new zoom level
		int newZoom = this.getZoomLevel();

		// if the new zome is different from old
		if (newZoom != oldZoomLevel) {
			// and the map has already been zoomed before
			if (oldZoomLevel != -1 && onZoomListener != null) {
				// notify the listener that the map has changed
				onZoomListener.onZoomChanged();
			}

			// old zoom level becomes the new one
			oldZoomLevel = getZoomLevel();
		}
	}
}
