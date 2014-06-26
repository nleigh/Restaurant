package uk.co.nathanleigh.restaurant.waiter;

import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.kitchen.KitchenActivity;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import uk.co.nathanleigh.restaurant.menu.MenuActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

// will be used to start all waiter functions
public class WaiterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waiter);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	// Opens an activity when button is clicked

		public void onClick_Tables(View view) {
			Intent intent = new Intent(this, TablesActivity.class);
			startActivity(intent);

		}

		public void onClick_Menu(View view) {
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
		}

		public void onClick_ViewNotifications(View view) {
			//Intent intent = new Intent(this, ViewNotificationsActivity.class);
			//startActivity(intent);
		}

		public void onClick_Payment(View view) {
			//Intent intent = new Intent(this, PaymentActivity.class);
			//startActivity(intent);
		}

		public void onClick_ViewOrderHistory(View view) {
			//Intent intent = new Intent(this, OrderHistoryActivity.class);
			//startActivity(intent);
		}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waiter, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
