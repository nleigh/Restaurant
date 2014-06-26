package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.menu.MenuFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class EditMenuActivity extends FragmentActivity  {

	private static final String TAG = "EDIT_MENU_ACTIVITY";
	private DatabaseAdapter db;
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
     
        openDB();
        Log.i(TAG,"Deleting Temp DB records");
        deleteTempDbRecords();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment fragment = new EditMenuWizardFragment();
        Fragment editMenuFragment = new MenuFragment();
        //Fragment fragment = new EditMenuOptionsFragment();
        Bundle args = new Bundle();
        args.putString("menuType", "edit");
        editMenuFragment.setArguments(args);
       // fragmentTransaction.replace(R.id.container, fragment, EditMenuNameDiscIngsFragment.TAG);
        fragmentTransaction.replace(R.id.container, editMenuFragment, MenuFragment.TAG);
        fragmentTransaction.commit();
       
        
    
    }
    


    
	private void deleteTempDbRecords() {
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	private void openDB() {
		db = new DatabaseAdapter(this);
		db.open();
	}

	private void closeDB() {
		db.close();
	}



}
