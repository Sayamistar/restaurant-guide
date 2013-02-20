package uk.ac.aston.pyzerg.restaurantguide.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import uk.ac.aston.pyzerg.restaurantguide.model.FavouritePlace;
import uk.ac.aston.pyzerg.restaurantguide.model.FavouritesList;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class Splash extends SherlockActivity {

	private TextView loadingPreferences;
	private TextView loadingFavourites;
	private Intent intent;

	private String filePath;
	private static final int SPLASH_LENGTH = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		loadingPreferences = (TextView) this
				.findViewById(R.id.splash_preferences);
		loadingFavourites = (TextView) this
				.findViewById(R.id.splash_favourites);

		this.setTitle("Launching");
		
		intent = new Intent(Splash.this,
				SelectCategory.class);

		filePath = this.getFilesDir().getPath().toString() + "/favourites.json";

		loadUserData();
		saveUserData();

	}

	private void loadUserData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectMapper mapper = new ObjectMapper();
					ArrayList<FavouritePlace> favourites = mapper.readValue(
							new File(filePath),
							new TypeReference<ArrayList<FavouritePlace>>() {
							});
					FavouritesList.getInstance().setPlaces(favourites);

					// loadingPreferences is simply for aesthetic reasons
					loadingPreferences.append("Done!");
					loadingFavourites.append("Done!");

					startActivity(intent);

				} catch (FileNotFoundException e) {
					// if the user has not run the application before, no
					// loading will occur
					Log.i("Loading favourites",
							"favourites.json does not exist");
					
					startActivity(intent);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, SPLASH_LENGTH);
	}

	// user data will be saved every 5 seconds
	private void saveUserData() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

					ObjectMapper mapper = new ObjectMapper();
					try {
						mapper.writeValue(new File(filePath), FavouritesList
								.getInstance().getPlaces());
						Log.e("Saving favourites", "Favourites saved!");
					} catch (JsonGenerationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JsonMappingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
		}).start();
	}
}
