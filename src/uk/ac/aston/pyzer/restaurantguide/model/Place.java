package uk.ac.aston.pyzer.restaurantguide.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	@Key
	public Photo photo;
	@Key
	public List<Review> reviews;
	

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public static class Review implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5224389036420043871L;
		
		@Key
		private List<Aspect> aspects;
		@Key
		private String author_name;
		public String getAuthor_name() {
			return author_name;
		}

		public void setAuthor_name(String author_name) {
			this.author_name = author_name;
		}

		public String getAuthor_url() {
			return author_url;
		}

		public void setAuthor_url(String author_url) {
			this.author_url = author_url;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		@Key
		private String author_url;
		@Key
		private String text;
		@Key
		private long time;
		
		public List<Aspect> getAspects() {
			return aspects;
		}

		public void setAspects(List<Aspect> aspects) {
			this.aspects = aspects;
		}

		public void addAspect(Aspect aspect) {
			this.aspects.add(aspect);
		}
		
		public Aspect getAspect(int i) { 
			return this.aspects.get(i);
		}
		
		public static class Aspect implements Serializable {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3718548117310822585L;
			@Key
			private double rating;
			@Key
			private String type;
			
			public double getRating() {
				return rating;
			}
			public void setRating(double rating) {
				this.rating = rating;
			}
			public String getType() {
				return type;
			}
			public void setType(String type) {
				this.type = type;
			}
		}
	}

	public static class Photo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4300416270367518113L;

		@Key
		private String[] html_attributions;
		@Key
		private int height;
		@Key
		private int width;
		@Key
		private String photo_reference;

		public String[] getHtml_attributions() {
			return html_attributions;
		}

		public void setHtml_attributions(String[] html_attributions) {
			this.html_attributions = html_attributions;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public String getPhoto_reference() {
			return photo_reference;
		}

		public void setPhoto_reference(String photo_reference) {
			this.photo_reference = photo_reference;
		}
	}
	
	public void addPhoto(Photo photo) {
		this.photos.add(photo);
	}

	public Photo getPhoto(int i) {
		return this.photos.get(i);
	}

	@Key
	public List<Photo> photos;

	public List<Photo> getPhotos() {
		return this.photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

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
		return name + "\n" + vicinity + "\n";
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



}
