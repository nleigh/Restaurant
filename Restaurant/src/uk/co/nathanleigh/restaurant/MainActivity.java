package uk.co.nathanleigh.restaurant;

import uk.co.nathanleigh.restaurant.kitchen.KitchenActivity;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import uk.co.nathanleigh.restaurant.menu.MenuActivity;
import uk.co.nathanleigh.restaurant.waiter.CurrentOrderActivity;
import uk.co.nathanleigh.restaurant.waiter.WaiterActivity;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

// The main activity, can click buttons to open either
// functions for the manager, waiter or the kitchen 
// can also click to view the menu or current order

public class MainActivity extends Activity {
	
	private static final String TAG = "MAIN";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// Opens an activity when button is clicked

	public void onClick_waiter(View view) {
		Intent intent = new Intent(this, WaiterActivity.class);
		startActivity(intent);

	}

	public void onClick_kitchen(View view) {
		Intent intent = new Intent(this, KitchenActivity.class);
		startActivity(intent);
	}

	public void onClick_manager(View view) {
		Intent intent = new Intent(this, ManagerActivity.class);
		startActivity(intent);
		
	}
	public void onClick_customer(View view) {
		Intent intent = new Intent(this, CustomerActivity.class);
		startActivity(intent);
		
	}



}
