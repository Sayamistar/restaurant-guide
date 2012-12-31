package uk.ac.aston.pyzer.restaurantguide.model;

import java.io.IOException;

import uk.ac.aston.pyzerg.restaurantguide.Config;
import uk.ac.aston.pyzerg.restaurantguide.R;
import uk.ac.aston.pyzerg.restaurantguide.TouristHttpTransport;
import android.content.res.Resources;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.util.Key;

public class PlaceDetail {

	@Key
	private Place result;

	public Place getResult() {
		return result;
	}

	public void setResult(Place result) {
		this.result = result;
	}

	@Override
	public String toString() {
		if (result!=null) {
			return result.toString();
		}
		return super.toString();
	}
	
	public static PlaceDetail getPlaceDetail(Place place, Resources res) {
		HttpRequestFactory hrf = TouristHttpTransport.createRequestFactory();
		HttpRequest request;
		PlaceDetail placeDetail = null;
		try {
			request = hrf.buildGetRequest(new GenericUrl(res.getString(R.string.places_details_url)));
			request.getUrl().put("key", res.getString(R.string.google_places_key));
			request.getUrl().put("reference", place.getReference());
			request.getUrl().put("sensor", "false");
			Log.i(Config.MSGTAG, "Request resp: "
					+ request.execute().parseAsString());
			placeDetail = request.execute().parseAs(
					PlaceDetail.class);
		} catch (IOException e) {
			Log.e("OVERLAY", e.getMessage());
		}
		return placeDetail;
	}
}
