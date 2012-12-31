package uk.ac.aston.pyzer.restaurantguide.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.api.client.util.Key;

public class Place implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4041502421563593320L;
	@Key
	private String name;
	@Key
	private String vicinity;
	@Key
	private String formatted_address;
	@Key
	private String formatted_phone_number;

	@Key
	public List<String> types;

	public static class Geometry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2946649576104623502L;

		public static class Location implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = -1861462299276634548L;
			@Key
			private double lat;
			@Key
			private double lng;

			/**
			 * @return the lat
			 */
			public double getLat() {
				return lat;
			}

			/**
			 * @param lat
			 *            the lat to set
			 */
			public void setLat(double lat) {
				this.lat = lat;
			}

			/**
			 * @return the lng
			 */
			public double getLng() {
				return lng;
			}

			/**
			 * @param lng
			 *            the lng to set
			 */
			public void setLng(double lng) {
				this.lng = lng;
			}

			public GeoPoint getGeoPoint() {
				int latE6 = (int) (getLat() * 1e6);
				int lonE6 = (int) (getLng() * 1e6);
				return new GeoPoint(latE6, lonE6);

			}
		}

		@Key
		private Location location;

		/**
		 * @param location
		 *            the location to set
		 */
		public void setLocation(Location location) {
			this.location = location;
		}

		/**
		 * @return the location
		 */
		public Location getLocation() {
			return location;
		}
	}

	@Key
	private Geometry geometry;

	@Key
	private String icon;

	@Key
	private String id;

	@Key
	private String reference;

	@Key
	private int rating;

	@Key
	private String url;

	@Key
	private String website;

	public Place() {
		types = new ArrayList<String>();
	}

	@Override
	public String toString() {
		String typeList = "";
		for (String type : types) {
			typeList = typeList + type + " ";
		}
		return name + "\n" + vicinity + "\n" + typeList + "\n"
				+ this.getGeometry().getLocation().getLat() + ", "
				+ this.getGeometry().getLocation().getLng();
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	/**
	 * @return the geometry
	 */
	public Geometry getGeometry() {
		return geometry;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public List<String> getTypes() {
		return types;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void addType(String type) {
		types.add(type);
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getFormatted_address() {
		return formatted_address;
	}

	public void setFormatted_address(String formatted_address) {
		this.formatted_address = formatted_address;
	}

	public String getFormatted_phone_number() {
		return formatted_phone_number;
	}

	public void setFormatted_phone_number(String formatted_phone_number) {
		this.formatted_phone_number = formatted_phone_number;
	}

	public GeoPoint getGeoPoint() {
		return this.geometry.getLocation().getGeoPoint();
	}

}
