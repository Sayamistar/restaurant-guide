package uk.ac.aston.pyzerg.restaurantguide.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This is a wrapper class so that we can combine the place and the notes together
 * into a single object to be stored in the favourites list
 * @author Gideon
 *
 */
public class FavouritePlace implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8490373818976948565L;
	private Place place;
	private String notes;
	
	@JsonCreator
	public FavouritePlace(@JsonProperty("place") Place place, @JsonProperty("notes") String notes) {
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
		return place.toString() + "\nNotes: " + this.notes;
	}
}
