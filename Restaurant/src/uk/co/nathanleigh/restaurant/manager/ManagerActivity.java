package uk.co.nathanleigh.restaurant.manager;

import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ManagerActivity extends Activity {

	// The manager activity, can click buttons to open either
	// functions to manage the database, editIngredients
	// view statistics, edit the menu or edit/view stocklevels

	
	public static final String STRING_PAYLOAD = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager);
		// Show the Up button in the action bar.
		//setupActionBar();
	}
	
	   public void manageDatabase(View view){
	    	Intent intent = new Intent(this, ManageDatabaseActivity.class);
	    	startActivity(intent);
	    	
	    }
	    
	    public void onClick_ingredients(View view){
	    	Intent intent = new Intent(this, IngredientsActivity.class);
	    	startActivity(intent);
	    }
	    public void onClick_statistics(View view){
	    	//Intent intent = new Intent(this, StatisticsActivity.class);
	    	//startActivity(intent);
	    }
	    public void onClick_manageDatabase(View view){
	    //	Intent intent = new Intent(this, ManageDatabaseActivity.class);
	    	//startActivity(intent);
	    }
	    
	    public void onClick_stockLevels(View view){
	    	Intent intent = new Intent(this, StockLevelsActivity.class);
	    	startActivity(intent);
	    }
	    
	    public void onClick_editMenu(View view){
	    	Intent intent = new Intent(this, EditMenuActivity.class);
	    	startActivity(intent);
	    }
	    
	    public void onClick_settings(View view){
	    	Intent intent = new Intent(this, ManagerSettingsActivity.class);
	    	startActivity(intent);
	    }
	    public void onClick_orderHistory(View view){
	    	//Intent intent = new Intent(this, ManagerSettingsActivity.class);
	    	//startActivity(intent);
	    }

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manager, menu);
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
