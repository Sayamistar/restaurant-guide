package uk.ac.aston.pyzerg.restaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SelectCategory extends SherlockActivity {
	
	private Button coffeeShops, fastFoodOutlets, foodCourts, bars, sandwichShops, cuisines, cost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectcategory_layout);
        
        this.setTitle("Select Category");
        
        coffeeShops = (Button) findViewById(R.id.coffeeShops);
        fastFoodOutlets = (Button) findViewById(R.id.fastFoodOutlets);
        foodCourts = (Button) findViewById(R.id.foodCourts);
        bars = (Button) findViewById(R.id.bars);
        sandwichShops = (Button) findViewById(R.id.sandwichShops);
        cuisines = (Button) findViewById(R.id.cuisines);
        cost = (Button) findViewById(R.id.cost);
        
        coffeeShops.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, ViewResults.class);
				intent.putExtra("keyword", "coffee");
				startActivity(intent);
			}
        	
        });
        fastFoodOutlets.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, ViewResults.class);
				intent.putExtra("keyword", "fast food");
				startActivity(intent);
			}
        	
        });
        foodCourts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, ViewResults.class);
				intent.putExtra("keyword", "food court");
				startActivity(intent);
			}
        	
        });
        bars.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, ViewResults.class);
				intent.putExtra("keyword", "bar");
				startActivity(intent);
			}
        	
        });
        sandwichShops.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, ViewResults.class);
				intent.putExtra("keyword", "sandwich");
				startActivity(intent);
			}
        	
        });
        cuisines.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SelectCategory.this, SelectCuisines.class);
				startActivity(intent);
			}
        	
        });
        
        cost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
			}
        	
        });

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		Intent intent;

		switch (item.getItemId()) {
		case R.id.preferences:
			intent = new Intent(this, Preferences.class);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}
}
