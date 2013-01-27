package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;

public class FavouritesList {
	
	private static FavouritesList instance = null;
	private static ArrayList<Place> items = null;
	
	protected FavouritesList() {
		
	}
	
	public static FavouritesList getInstance() {
		if (instance == null) {
			instance = new FavouritesList();
			
			items = new ArrayList<Place>();
		}
		
		return instance;
	}
	
	public ArrayList<Place> getPlaces() {
		return items;
	}
	
}
