package uk.ac.aston.pyzerg.restaurantguide;


import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class TouristHttpTransport {
	
	// private static final HttpTransport transport = new ApacheHttpTransport();

	public static HttpRequestFactory createRequestFactory() {
		// work around for lost ssl connections that still get reused
		// related? http://code.google.com/p/android/issues/detail?id=8625
		HttpTransport transport = new ApacheHttpTransport();
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("API Project");
				request.setHeaders(headers);
				request.setParser(new JsonObjectParser(new JacksonFactory()));

			}
		});
	}
}
