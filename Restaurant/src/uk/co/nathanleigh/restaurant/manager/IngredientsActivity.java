package uk.co.nathanleigh.restaurant.manager;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuAddIngFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuAddonsFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuNameDiscIngsFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.IngUnitsDialogFragment;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class IngredientsActivity extends FragmentActivity  {

	private DatabaseAdapter db;
	public static final String TAG = "INGS";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
     
      
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment fragment = new EditMenuWizardFragment();
    	Fragment fragment = new EditMenuAddIngFragment();
		 Bundle bundle = new Bundle();
        bundle.putString("ingType", TAG);
        fragment.setArguments(bundle);
        //Fragment fragment = new EditMenuOptionsFragment();
        fragmentTransaction.replace(R.id.container, fragment, "addIng");
        fragmentTransaction.commit();
       
        
    
    }
    


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void openDb() {
		db = new DatabaseAdapter(this);
		db.open();
		
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
		getMenuInflater().inflate(R.menu.ingredients, menu);
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
