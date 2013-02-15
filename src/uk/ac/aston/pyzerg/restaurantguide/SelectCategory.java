package uk.ac.aston.pyzerg.restaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SelectCategory extends MySherlockActivity {
	
	private Button coffeeShops, fastFoodOutlets, foodCourts, bars, sandwichShops, cuisines;

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
       
    }
}
