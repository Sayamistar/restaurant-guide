package uk.ac.aston.pyzer.restaurantguide.model;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

public class PlaceList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5867142215256256810L;

	@Key
	private String status;

	@Key
	private List<Place> results;
	
	@Key
	private String next_page_token;

	public String getNext_page_token() {
		return next_page_token;
	}

	public void setNext_page_token(String next_page_token) {
		this.next_page_token = next_page_token;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Place> getResults() {
		return results;
	}

	public void setResults(List<Place> results) {
		this.results = results;
	}
	

}
