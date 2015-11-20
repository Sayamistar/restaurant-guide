# Restaurant Guide
This is a simple Android application that allows users to find restaurants in the local area using the Google Places API, Google Maps API and the ActionBarSherlock library. 

## Features
- Users can view all restaurants as icons on a map based on a radius specified in Preferences
- Users can view red halo overlays at the edges of the screen to indicate restaurants that are off-screen.
- Users can view restaurants in close proximity with each other by zooming in due to a clustering algorithm used
- Users can tap restaurant icons on the map to view a dialogue including address, phone number, rating, open/closed status and a photo
- Users can save restaurants into Favourites with optional additional notes
- Users can filter restaurants based on category and cuisine using the filter activity

## Installation
### Using the pre-compiled APK

1. Navigate to the "bin" folder of the project
2. Copy "RestaurantGuide.apk" to your phone
3. Install it via the Package Installer

## Using the source code
1. Download the ActionBarSherlock library from http://actionbarsherlock.com/
2. Import both restaurant-guide and the ActionBarSherlock library projects into an IDE (e.g. Eclipse, Android Studio etc.)
3. Mark the ActionBarSherlock library as a Library (e.g. "Is Library" option ticked in Eclipse)
4. Make sure restaurant-guide is referencing the library
5. Build the project to generate the new APK

