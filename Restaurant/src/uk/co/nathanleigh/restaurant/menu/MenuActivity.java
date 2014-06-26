package uk.co.nathanleigh.restaurant.menu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The {@link android.support.v4.view.PagerAdapter} that will provide
 * fragments for each of the sections. We use a
 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
 * will keep every loaded fragment in memory. If this becomes too memory
 * intensive, it may be best to switch to a
 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
 */

// Class which displays the menu and allows ordering
// of menu items

public class MenuActivity extends FragmentActivity {


	private static final String TAG = "MenuActivity";
	private DatabaseAdapter db;
	private int sessionId;
	private int tableId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
     
        openDB();
		Intent myIntent= getIntent(); // gets the previously created intent

        sessionId = myIntent.getIntExtra("sessionId", 0 );
		tableId = myIntent.getIntExtra("tableId", 0);
		Log.i(TAG, " Menu activity - Table Id  " + tableId);
		Log.i(TAG, " Session Id  " + sessionId);
		
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment fragment = new EditMenuWizardFragment();
        MenuFragment menuFragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString("menuType", "view");
        args.putInt("sessionId", sessionId);
        args.putInt("tableId", tableId);
 
        menuFragment.setArguments(args);
        //Fragment fragment = new EditMenuOptionsFragment();
        fragmentTransaction.replace(R.id.container, menuFragment, MenuFragment.TAG);
        fragmentTransaction.commit();
       
    
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
		//getMenuInflater().inflate(R.menu.edit_menu, menu);
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
	

	private void openDB() {
		db = new DatabaseAdapter(this);
		db.open();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	
	private void closeDB() {
		db.close();
	}


}
