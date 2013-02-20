package uk.ac.aston.pyzerg.restaurantguide.model;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import uk.ac.aston.pyzerg.restaurantguide.config.Config;
import uk.ac.aston.pyzerg.restaurantguide.config.MyHttpTransport;
import uk.ac.aston.pyzerg.restaurantguide.ui.R;
import android.content.res.Resources;
import android.os.AsyncTask;
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
		if (result != null) {
			return result.toString();
		}
		return super.toString();
	}

	public static PlaceDetail getPlaceDetail(Place place, Resources res) {

		class GoogleRequest extends AsyncTask<Object, Void, PlaceDetail> {

			@Override
			protected PlaceDetail doInBackground(Object... params) {
				Place place = (Place) params[0];
				Resources res = (Resources) params[1];

				HttpRequestFactory hrf = MyHttpTransport
						.createRequestFactory();
				HttpRequest request;

				PlaceDetail placeDetail = null;

				try {
					request = hrf.buildGetRequest(new GenericUrl(res
							.getString(R.string.places_details_url)));
					request.getUrl().put("key",
							res.getString(R.string.google_places_key));
					request.getUrl().put("reference", place.getReference());
					request.getUrl().put("sensor", "false");
					Log.i(Config.MSGTAG, "Request resp: "
							+ request.execute().parseAsString());
					placeDetail = request.execute().parseAs(PlaceDetail.class);
				} catch (IOException e) {
					Log.e("OVERLAY", e.getMessage());
				}

				return placeDetail;
			}

		}

		GoogleRequest googleRequest = new GoogleRequest();
		try {
			return googleRequest.execute(place, res).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
