package uk.ac.aston.pyzerg.restaurantguide.ui;

import java.util.ArrayList;

import uk.ac.aston.pyzerg.restaurantguide.model.FavouritePlace;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectCategory extends MySherlockActivity implements OnItemClickListener {
	
	private String[] categories;
	private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcategory_layout);
        
        this.setTitle("Select Category");
        
        categories = new String[] {"All Restaurants", "Coffee Shops", "Fast Food", "Food Court", "Bars", "Sandwich Shops", "Buffet", "Vegetarian Food", "Select Cuisines"};
        
    	adapter = new ArrayAdapter<String>(
    			this, android.R.layout.simple_list_item_1, android.R.id.text1, categories);
		
		ListView l = (ListView) this.findViewById(R.id.categoryList);
		l.setAdapter(adapter);
		l.setOnItemClickListener(this);

    }

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		String category = categories[position];
		
		
		if (category.equals("All Restaurants")) {
			Intent intent = new Intent(SelectCategory.this, ViewResults.class);
			intent.putExtra("keyword", "restaurant");
			startActivity(intent);
		} else if (category.equals("Select Cuisines")) {
			Intent intent = new Intent(SelectCategory.this, SelectCuisines.class);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(SelectCategory.this, ViewResults.class);
			intent.putExtra("keyword", category);
			startActivity(intent);
		}
	}
	
}

class MyWrapper {
	public ArrayList<FavouritePlace> favourites;
}
