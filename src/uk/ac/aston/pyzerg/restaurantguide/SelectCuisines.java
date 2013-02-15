package uk.ac.aston.pyzerg.restaurantguide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectCuisines extends MySherlockActivity {

	private LinearLayout checkboxes;
	private CheckBox all;
	private Button search;

	private StringBuffer selectedCuisines;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectcuisines_layout);
		
		this.setTitle("Select Cuisines");

		checkboxes = (LinearLayout) findViewById(R.id.selectedcuisines_checkboxes);

		selectedCuisines = new StringBuffer();

		all = (CheckBox) findViewById(R.id.selectcuisines_all);

		all.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// mark all checkboxes checked or unchecked
				for (int i = 0; i < checkboxes.getChildCount(); i++) {
					CheckBox checkbox = (CheckBox) checkboxes.getChildAt(i);
					if (!checkbox.getText().toString().equals("All")) {
						checkbox.setChecked(all.isChecked());
					}
				}
			}
		});

		search = (Button) findViewById(R.id.selectcuisines_search);

		search.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				// find out how many checkboxes are checked and add to arraylist
				for (int i = 0; i < checkboxes.getChildCount(); i++) {
					CheckBox checkbox = (CheckBox) checkboxes.getChildAt(i);
					if (!checkbox.getText().toString().equals("All")) {
						if (checkbox.isChecked()) {
							selectedCuisines.append(checkbox.getText().toString());
							selectedCuisines.append("|");
						}
					}
				}

				if (selectedCuisines.length() > 0) {
					
					// remove the last | sign at the end
					selectedCuisines.deleteCharAt(selectedCuisines.length()-1);
					
					Intent intent = new Intent(SelectCuisines.this, ViewResults.class);
					intent.putExtra("keyword", selectedCuisines.toString());
					selectedCuisines.setLength(0);
					startActivity(intent);
			
				}
				else {
					Toast.makeText(v.getContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
				}
			}

		});

	}
}
