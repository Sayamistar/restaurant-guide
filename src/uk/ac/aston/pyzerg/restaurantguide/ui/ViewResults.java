package uk.ac.aston.pyzerg.restaurantguide.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzerg.restaurantguide.config.Config;
import uk.ac.aston.pyzerg.restaurantguide.config.MyHttpTransport;
import uk.ac.aston.pyzerg.restaurantguide.model.Place;
import uk.ac.aston.pyzerg.restaurantguide.model.PlaceList;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

public class ViewResults extends MySherlockActivity implements
		OnItemClickListener, OnClickListener, LocationListener {
	
	private ArrayList<Place> places = new ArrayList<Place>();
	private List<String> titles = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private PlaceList requestPlaces;
	private HttpRequestFactory hrf;
	private String location;
	private String keyword;

	private TextView loading;

	private LocationManager locationManager;
	private Location currentLocation;

	private boolean initialRequest = true;
	private String pageTokenPlz = "";
	private long lastNanoTime = 0;
	private int numberOfItemsBeforeRequest = 0;
	
	// * ensure only 1 request at any one time 
	// * stop it scrolling to top - do updates not replace
	// * ... show some sort of indicator... NOT A BODGE JOB
	// * ensure 2 seconds have passed since last run... or retry 1 time on failure. 
	      // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	/*
	 * 
	 * AsyncHTTPRequest
	 * 
	 * new ...() setURL(); // example setPostData(); // example
	 * 
	 * setCallbackListener(new AsyncHTTPRequestCallback() { public void
	 * onComplete(int status) { // like a click listener
	 * 
	 * } })
	 */

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		createAdapter();

		this.setTitle("Results");

		Button plotAllButton = (Button) this.findViewById(R.id.plot);
		plotAllButton.setOnClickListener(this);

		loading = (TextView) this.findViewById(R.id.loading);

		// set up location manager to work with location service
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (checkInternet()) {
			
			if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				// locationManager.requestLocationUpdates(
					// LocationManager.NETWORK_PROVIDER, 5000, 100, this);
				currentLocation = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			} else {
				
				// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);
				currentLocation = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
			
			if (currentLocation == null) {
				currentLocation = new Location("__current__");
				currentLocation.setLatitude(51.50746);
				currentLocation.setLongitude(-0.122967);
			}
			
			location = currentLocation.getLatitude() + ","
				+ currentLocation.getLongitude();
			
			Intent intent = this.getIntent();
			
			keyword = intent.getStringExtra("keyword");
			GoogleRequest googleRequest = new GoogleRequest();
			googleRequest.execute();
			
		}  else {
			currentLocation = null;
			loading.setText("No connection available");
		}
	}

	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Place p = places.get(position);
		Intent i = new Intent(this, HaloView.class);
		i.putExtra("place", p);
		startActivity(i);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.plot) {
			if (places.size() > 0) {
				// plot all the places with halos
				Intent intent = new Intent(this, HaloView.class);
				//intent.putExtra("location", location);
				intent.putExtra("placeList", places);
				startActivity(intent);
			}
		}
	}

	// when the activity starts up, request updates
	@Override
	protected void onResume() {
		super.onResume();
	}

	// when activity is paused, stop listening for updates
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		//locationManager.removeUpdates(this);
		currentLocation = location;
	}

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

	public boolean checkInternet() {

		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (wifi.isConnected()) {
			return true;
		} else if (mobile.isConnected()) {
			return true;
		}
		return false;

	}
	
	private void createAdapter() {
		adapter = new ArrayAdapter<String>(
				ViewResults.this, R.layout.place_title,
				R.id.placeTitle, titles);
		
		ListView l = (ListView) ViewResults.this
				.findViewById(R.id.placeList);
		l.setAdapter(adapter);
		l.setOnItemClickListener(ViewResults.this);
		l.setOnScrollListener(new OnScrollListener() {
			@Override
			// random number generator calls this!
			public void onScroll(AbsListView view,
					int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				// critical section to protect 
				// usage of <numberOfItemsBeforeRequest>
				synchronized (this) {
					if (totalItemCount == 0) return;
					// 0 == 0
					// 0 == 0
					// 0 == 0
					// 20 == 0
					// 20 == 0
					// 20 == 0
					// 20 == 20
					// 20 == 20
					// 20 == 20
					// 20 == 40
					// 20 == 40
					// 20 == 40
					// 40 == 40
					// 40 == 40
					// 40 == 40
					// 40 == 60
					// 60 == 60
					// 60 == 60
					
					// * if they are equal
					// Case 1: Request has started, not finished, so list has not updated yet.
					// Case 2: Request has finished, nothing to be added to list.
					// * if they are NOT equal
					// Request has finished and items have been added to the list
					// so that the item count is more than it was before the request
					
					if (totalItemCount == numberOfItemsBeforeRequest) return;
					if (++firstVisibleItem + visibleItemCount > totalItemCount) {
						// we have scrolled to the end of the updated list
						// so we need to do another request
						numberOfItemsBeforeRequest = totalItemCount;
						GoogleRequest googleRequest = new GoogleRequest();
						googleRequest.execute();
					}
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view,
					int scrollState) {
			}

		});
		}
	
	
	class GoogleRequest extends AsyncTask<Void, Void, Integer> {

		protected Integer doInBackground(Void... params) {
			synchronized (ViewResults.class) {
			hrf = MyHttpTransport.createRequestFactory();
			Resources res = getResources();
			try {
				HttpRequest request = hrf
						.buildGetRequest(new GenericUrl(
								res.getString(R.string.places_search_url)));
				request.getUrl().put("key",
						res.getString(R.string.google_places_key));
				request.getUrl().put("location", location);
				request.getUrl().put("radius", Config.RADIUS);
				request.getUrl().put("keyword", keyword);
				request.getUrl().put("sensor", false);
				
				if (!initialRequest) {
					if (pageTokenPlz == null || pageTokenPlz.equals("")) return 2;
					request.getUrl().put("pagetoken", pageTokenPlz);
				}
				
				initialRequest = false;
	
				long elapsedTime = System.nanoTime() - lastNanoTime;
				lastNanoTime = System.nanoTime();
				long sleepTime = 2000000000 - elapsedTime;
				if (sleepTime > 0) {
					try { Thread.sleep((int) (sleepTime / 1000000)); } 
					catch (Exception e) {}
				}

				requestPlaces = request.execute().parseAs(PlaceList.class);
				pageTokenPlz = requestPlaces.getNext_page_token();
				places.addAll(requestPlaces.getResults());							

				Log.i(Config.MSGTAG,
						"Response: " + requestPlaces.getStatus());
				return Integer.valueOf(0);
			} catch (IOException e) {
				ViewResults.this.runOnUiThread(new Runnable() {
					public void run() {
						loading.setText("Connection lost");
					}
				});

			}

			return Integer.valueOf(1);
			
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			// do a check on <result> before continue
			if (result > 0)
				return;
			loading.setVisibility(View.GONE);
			for (Place p : requestPlaces.getResults()) {

				Location placeLocation = new Location(
						"Place Location");
				placeLocation.setLatitude(p.getGeometry()
						.getLocation().getLat());
				placeLocation.setLongitude(p.getGeometry()
						.getLocation().getLng());

				int distance = Math.round(currentLocation
						.distanceTo(placeLocation));

				titles.add((titles.size() + 1) + ". " + p.getName() + "\n" + p.getVicinity()
						+ "\nEuclidean Distance: " + distance + "m");
			}
			
			adapter.notifyDataSetChanged();
			
			
			Toast.makeText(getApplicationContext(),
					places.size() + " results",
					Toast.LENGTH_LONG).show();
		}
	}	
}