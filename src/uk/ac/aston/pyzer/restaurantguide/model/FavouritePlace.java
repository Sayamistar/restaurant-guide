package uk.ac.aston.pyzer.restaurantguide.model;

public class FavouritePlace {
	private Place place;
	private String notes;
	
	public FavouritePlace(Place place, String notes) {
		this.place = place;
		this.notes = notes;
	}
	
	public Place getPlace() {
		return this.place;
	}
	
	public void setPlace(Place place) {
		this.place = place;
	}
	
	public String getNotes() {
		return this.notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String toString() {
		return place.toString() + "\n\nNotes: " + notes;
	}
}
