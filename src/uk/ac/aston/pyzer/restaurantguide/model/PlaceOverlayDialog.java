package uk.ac.aston.pyzer.restaurantguide.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import uk.ac.aston.pyzer.restaurantguide.model.Place.Photo;
import uk.ac.aston.pyzerg.restaurantguide.R;
import uk.ac.aston.pyzerg.restaurantguide.TouristHttpTransport;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

public class PlaceOverlayDialog extends AlertDialog.Builder {

	private Context context;
	private Place place;

	private ImageView favouriteIcon;
	private boolean alreadyFavourite;

	private Bitmap image;
	private String photoRef;

	public PlaceOverlayDialog(final Context context, Place place,
			final PlaceDetail placeDetail) {
		super(context);

		this.context = context;
		this.place = place;

		// get the layout inflater
		LayoutInflater factory = LayoutInflater.from(context);
		// get the dialog view
		final View dialogView = factory.inflate(R.layout.dialog_layout, null);

		// set the dialog to use this custom view
		this.setView(dialogView);

		// get the custom dialog title and set the text
		RelativeLayout titleView = (RelativeLayout) factory.inflate(
				R.layout.custom_title_dialog_box, null);
		TextView tv = (TextView) titleView
				.findViewById(R.id.custom_dialog_title);
		tv.setText(place.getName());

		favouriteIcon = (ImageView) titleView
				.findViewById(R.id.custom_dialog_favourite);
		favouriteIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addToFavourites(v);
			}
		});

		ImageView sendToFriend = (ImageView) titleView
				.findViewById(R.id.custom_dialog_send);
		sendToFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.setData(Uri.parse("sms:"));
				sendIntent.putExtra("sms_body", placeDetail.getResult()
						.getName()
						+ "\n\n"
						+ placeDetail.getResult().getVicinity()
						+ "\n"
						+ placeDetail.getResult().getFormatted_phone_number());
				v.getContext().startActivity(sendIntent);
			}

		});

		if (isAlreadyFavourite()) {
			favouriteIcon.setImageResource(R.drawable.favourite_on);
			alreadyFavourite = true;
		}

		// add the custom title to the dialog
		this.setCustomTitle(titleView);

		if (placeDetail != null) {
			int rating = 0;
			int reviews = 0;

			if (placeDetail.getResult().getRating() != 0) {
				rating = placeDetail.getResult().getRating();
				
			}
			if (placeDetail.getResult().getReviews() != null) {
				reviews = placeDetail.getResult().getReviews().size();
			}
			// get the photos list
			List<Photo> photos = new ArrayList<Photo>();
			photos = placeDetail.getResult().getPhotos();

			// assuming that photos were sent back
			// get the first photo reference
			if (photos != null && photos.size() > 0) {
				photoRef = photos.get(0).getPhoto_reference();

				class GoogleRequest extends AsyncTask<Void, Void, Integer> {

					@Override
					protected Integer doInBackground(Void... params) {
						HttpRequestFactory hrf = TouristHttpTransport
								.createRequestFactory();
						try {
							// send the photo reference to the photos API
							HttpRequest request = hrf
									.buildGetRequest(new GenericUrl(context
											.getResources().getString(
													R.string.places_photos_url)));
							request.getUrl().put(
									"key",
									context.getResources().getString(
											R.string.google_places_key));
							request.getUrl().put("photoreference", photoRef);
							request.getUrl().put("sensor", true);
							request.getUrl().put("maxheight", 400);

							// get the photo back as a bitmap
							image = BitmapFactory.decodeStream(request
									.execute().getContent());

						} catch (IOException e) {
							Log.e("IOException",
									"Photo request failed" + e.getMessage());
						}
						return null;
					}

					@Override
					protected void onPostExecute(Integer result) {
						if (image != null) {
							// get the layout from the dialog view
							LinearLayout ll = (LinearLayout) dialogView
									.findViewById(R.id.dialogLayout);
							// get the image view from layout
							// ImageView imageView = (ImageView)
							// ll.findViewById(R.id.placePhoto);
							ImageView imageView = new ImageView(ll.getContext());
							// set the bitmap as the image source
							imageView.setImageBitmap(image);

							ll.addView(imageView);
						}
					}

				}

				GoogleRequest googleRequest = new GoogleRequest();
				googleRequest.execute();

			}

			LinearLayout dialogLayout = (LinearLayout) dialogView
					.findViewById(R.id.dialogLayout);

			TextView address = (TextView) dialogLayout
					.findViewById(R.id.dialogAddress);

			address.setText(placeDetail.getResult().getVicinity());
			
			TextView phone = (TextView) dialogLayout.findViewById(R.id.dialogPhone);
			final String phoneNumber = placeDetail.getResult().getFormatted_phone_number().trim();
			
			// underlining the phone number to look like a link
			SpannableString phoneNumberUnderlined = new SpannableString(phoneNumber);
			phoneNumberUnderlined.setSpan(new UnderlineSpan(), 0, phoneNumberUnderlined.length(), 0);
			phone.setText(phoneNumberUnderlined);
			
			// phone the place if the user clicks on the number
			phone.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					 try {
						 Intent callIntent = new Intent(Intent.ACTION_CALL);
						 callIntent.setData(Uri.parse("tel:" + phoneNumber));
					     v.getContext().startActivity(callIntent);
					     
						 Log.e("Phone", "No. is: " + phoneNumber);
					    } catch (ActivityNotFoundException e) {
					         Log.e("Phone", "Call failed", e);
					    }
				}
				
			});
			
			TextView reviewText = (TextView) dialogLayout.findViewById(R.id.dialogReviewText);
			reviewText.setText("Rating (based on " + reviews + " reviews):");

			LinearLayout ratingsLayout = (LinearLayout) dialogLayout
					.findViewById(R.id.dialogRatings);

			for (int i = 0; i < rating; i++) {
				ImageView star = new ImageView(ratingsLayout.getContext());
				star.setImageResource(R.drawable.favourite_on_small);
				ratingsLayout.addView(star);
			}

			for (int i = rating; i < 5; i++) {
				ImageView star = new ImageView(ratingsLayout.getContext());
				star.setImageResource(R.drawable.favourite_off_small);
				ratingsLayout.addView(star);
			}

		} else {
			this.setMessage("No address");
		}
		this.setNegativeButton("Close", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		this.show();
	}

	// add the selected place to the favourites list
	public void addToFavourites(View v) {
		if (!alreadyFavourite) {
			AlertDialog.Builder alert = new AlertDialog.Builder(context);

			alert.setTitle("Added to favourites!");
			alert.setMessage("Add some notes too:");

			// Set an EditText view to get user input
			final EditText input = new EditText(context);
			alert.setView(input);

			alert.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String value = input.getText().toString();
							// add the item to the favourites list
							FavouritesList.getInstance().getPlaces()
									.add(new FavouritePlace(place, value));
							
							ObjectMapper mapper = new ObjectMapper();
							try {
								String filePath = context.getFilesDir().getPath().toString() + "/favourites.json";
								mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
								mapper.writeValue(new File(filePath), FavouritesList.getInstance().getPlaces());
								Log.e("saved", "Saved favourite");
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
					});

			alert.setNegativeButton("No Thanks!",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// add the item to the favourites list
							FavouritesList.getInstance().getPlaces()
									.add(new FavouritePlace(place, "None"));
						}
					});

			alert.show();

			favouriteIcon.setImageResource(R.drawable.favourite_on);
			alreadyFavourite = true;
		} else {
			Toast.makeText(v.getContext(), "Already a favourite",
					Toast.LENGTH_SHORT).show();
		}
	}

	// check if the selected place is already in the favourites list
	private boolean isAlreadyFavourite() {
		alreadyFavourite = false;
		for (FavouritePlace fp : FavouritesList.getInstance().getPlaces()) {
			if (fp.getPlace().getName().equals(place.getName())) {
				alreadyFavourite = true;

				return alreadyFavourite;
			}
		}

		return false;
	}
}
