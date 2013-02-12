package uk.ac.aston.pyzerg.restaurantguide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.aston.pyzer.restaurantguide.model.Place;
import uk.ac.aston.pyzer.restaurantguide.model.PlaceList;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

public class ViewResults extends SherlockActivity implements 
		OnItemClickListener, OnClickListener, LocationListener {
	private PlaceList places;
	private HttpRequestFactory hrf;
	private String location;
	
	private String keyword;
	
	private TextView loading;
	
    private LocationManager locationManager;
    private Location currentLocation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		this.setTitle("Results");
		
		Button plotAllButton = (Button) this.findViewById(R.id.plot);
		plotAllButton.setOnClickListener(this);
		
		loading = (TextView) this.findViewById(R.id.loading);

		/* Use the LocationManager class to obtain GPS locations */
        // set up location manager to work with GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        // get the current location (or last known) from the location manager
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        if (currentLocation != null) {
        	location = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        }
        
        Intent intent = this.getIntent();
        keyword = intent.getStringExtra("keyword");
        
        if (keyword != "") {         	
        	
        	class GoogleRequest extends AsyncTask<Void, Void, Integer> {

				protected Integer doInBackground(Void...params) {
					hrf = TouristHttpTransport.createRequestFactory();
					Resources res = getResources();
					try {
						HttpRequest request = hrf.buildGetRequest(new GenericUrl(
								res.getString(R.string.places_search_url)));
						request.getUrl().put("key", res.getString(R.string.google_places_key));
						request.getUrl().put("location", location);
						request.getUrl().put("radius", Config.RADIUS);
						request.getUrl().put("keyword", keyword);
						request.getUrl().put("sensor", false);
						Log.i(Config.MSGTAG, "Request: " + request.toString());
						places = request.execute().parseAs(PlaceList.class);

						 /*if (places.getNext_page_token() != null || places.getNext_page_token() != "") {
				    
							 try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				           
				                request.getUrl().put("pagetoken", places.getNext_page_token());
				                PlaceList temp = request.execute().parseAs(PlaceList.class);
				                places.getResults().addAll(temp.getResults());

				                if (temp.getNext_page_token() != null || temp.getNext_page_token() != "") {
				                    try {
										Thread.sleep(2000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
				                    request.getUrl().put("pagetoken",temp.getNext_page_token());
				                    PlaceList tempList =  request.execute().parseAs(PlaceList.class);
				                    places.getResults().addAll(tempList.getResults());
				                }
				            }*/
					

						Log.i(Config.MSGTAG, "Response: " + places.getStatus());
					} catch (IOException e) {
						Log.e(Config.MSGTAG, e.getMessage());
					}  
					
					return null;
				}

				@Override
				protected void onPostExecute(Integer result) {
					loading.setVisibility(View.GONE);
					List<String> titles = new ArrayList<String>(places.getResults()
							.size());
					for (Place p : places.getResults()) {
						
						Location placeLocation = new Location("Place Location");
						placeLocation.setLatitude(p.getGeometry().getLocation().getLat());
						placeLocation.setLongitude(p.getGeometry().getLocation().getLng());
						
						float distance = currentLocation.distanceTo(placeLocation);
	
						titles.add(p.getName() + "\n" + p.getVicinity() + "\nEuclidean Distance: "+
								distance + "m");
						//titles.add(p.toString());
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewResults.this,
							R.layout.place_title, R.id.placeTitle, titles);
					ListView l = (ListView) ViewResults.this.findViewById(R.id.placeList);
					l.setAdapter(adapter);
					l.setOnItemClickListener(ViewResults.this);
						
					Toast.makeText(getApplicationContext(), places.getResults().size() + " results", Toast.LENGTH_LONG).show();
					}
        	
        	}	
	
        	
        	GoogleRequest googleRequest = new GoogleRequest();
            googleRequest.execute();
        }
        else {
        	Toast.makeText(this, "No results found", Toast.LENGTH_LONG).show();
        }
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Intent intent;

		switch (item.getItemId()) {
		case R.id.preferences:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Place p = places.getResults().get(position);
		Intent i = new Intent(this, ViewPlaceDetails.class);
		i.putExtra("place", p);
		startActivity(i);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.plot) {
			// plot all the places with halos and lines.
			Intent intent = new Intent(this, HaloView.class);
			intent.putExtra("location", location);
			intent.putExtra("placeList", this.places);
			startActivity(intent);
		}
	}
	
    /**
     * When the activity starts up, request updates
     */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }
    
    /**
     * When the activity is paused, stop listening for updates
     */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    protected boolean isRouteDisplayed() {	
        return false;
    }

	public void onLocationChanged(Location arg0) {	
		currentLocation = arg0;
	}

	public void onProviderDisabled(String arg0) {
	}

	public void onProviderEnabled(String arg0) {
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}