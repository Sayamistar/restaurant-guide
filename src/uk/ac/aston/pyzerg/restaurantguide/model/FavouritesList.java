package uk.ac.aston.pyzerg.restaurantguide.model;

import java.util.ArrayList;

/**
 * We only need one instance of a favourites list, so this is a singleton
 * @author Gideon
 *
 */
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
	
	public void setPlaces(ArrayList<FavouritePlace> items) {
		FavouritesList.items = items;
	}
	
}
