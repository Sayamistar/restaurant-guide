package uk.ac.aston.pyzer.restaurantguide.model;

import java.util.ArrayList;

public class FavouritesList {
	
	private static FavouritesList instance = null;
	private static ArrayList<FavouritePlace> items = null;
	
	protected FavouritesList() {
		
	}
	
	public static FavouritesList getInstance() {
		if (instance == null) {
			instance = new FavouritesList();
			
			items = new ArrayList<FavouritePlace>();
		}
		
		return instance;
	}
	
	public ArrayList<FavouritePlace> getPlaces() {
		return items;
	}
	
}
