package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditMenuOptionsFragment extends Fragment implements
		OnClickListener {

	private static ViewGroup mContainerView;
	private static Activity context;
	private ViewGroup rootView;
	private static DatabaseAdapter db;
	public static final String TAG = "OPTIONS";
	private static String editType;
	private static int menuItemId;
	private EditText etOptionName;
	private EditText etOptionPrice ;

	public EditMenuOptionsFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 openDb();
		 
		 editType = getEditType();
		 menuItemId = getMenuItemId();
		 
		// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
			
			//fill TEMP OPTIONS IF UPDATE
			if (editType.equals("update")){
				db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
				String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
				Cursor c = db.myDBQuery(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					
				int optionPrice = c.getInt(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));
				String optionName = c.getString(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
				
				db.insertRowTempMenuItemOption(optionName, optionPrice);
				}
				c.close();
				Log.i(TAG, "Filled temp menu item options table for use in update");
			}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_options, container, false);

		mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
		
		context = getActivity();
	
		etOptionName = (EditText) rootView.findViewById(R.id.etOptionName);
		etOptionPrice = (EditText) rootView.findViewById(R.id.etOptionPrice);
		
		int maxLength = 13; // prevent entering a number too large
		etOptionPrice.setFilters(new InputFilter[] { 
			new InputFilter.LengthFilter(maxLength)});
		etOptionPrice.addTextChangedListener(CurrencyFormat.
				getCurrencyInputTextWatcher(etOptionPrice));
	
		
		Button bAddOption = (Button) rootView.findViewById(R.id.bAddOption);
		Button bBack = (Button) rootView.findViewById(R.id.bBack);
		Button bNext = (Button) rootView.findViewById(R.id.bNext);
		bAddOption.setOnClickListener(this);
		bBack.setOnClickListener(this);
		bNext.setOnClickListener(this);
		
		fillList();
		
		return rootView;

	}
	
	
	
	private int getMenuItemId() {
		return EditMenuNameDiscIngsFragment.menuItemId;
	}

	private String getEditType() {
		return EditMenuNameDiscIngsFragment.editType;
	}

	@Override
	public void onStart() {
		super.onStart();

			//fillList();
		
	}

	private static void addOption(long reqRowId, String optionName, int optionPrice) {
		
		final long rowId = reqRowId;
		final ViewGroup newView = (ViewGroup) context.getLayoutInflater()
				.inflate(R.layout.item_view_option, mContainerView, false);
		
		System.out.println("option Name "+ optionName);
		
		((TextView) newView.findViewById(R.id.tvOptionName)).setText(optionName + "  "
				+ CurrencyFormat.currencyDoubleToString(
						CurrencyFormat.currencyIntToDouble(optionPrice)));
		
		
		// Set a click listener for the "X" button in the row that will remove
		// the row.
		newView.findViewById(R.id.delete_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Remove the row from its parent (the container view).
						// Because mContainerView has
						// android:animateLayoutChanges set to true,
						// this removal is automatically animated.
						db.deleteRow(DatabaseAdapter.
								DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS, rowId);
						
						mContainerView.removeView(newView);
						
					}
				});

		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		mContainerView.addView(newView, 0);
	}
	
	private static void fillList() {
		
	
		
		Cursor cursor = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
		// cursor.moveToFirst();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					long rowId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_ROWID));
					int optionPrice = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_OPTIONPRICE));
					 String optionName =
					 cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_OPTIONNAME));
					addOption( rowId, optionName, optionPrice);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bAddOption: {
			System.out.println("Add Option button press");
				
			
			String optionName =  etOptionName.getText().toString();
			String optionPriceStr =  etOptionPrice.getText().toString();
			
			if(checkIfInputValid(optionName, optionPriceStr)){
			
				//TODO insert as int in db table
				
				
			//int optionPrice =  Integer.parseInt(optionPriceStr);
			int optionPriceInt =  CurrencyFormat.
					currencyStringIntoInteger(optionPriceStr);
			
			final long rowId = db.insertRowTempMenuItemOption(optionName, optionPriceInt);

			addOption(rowId, optionName, optionPriceInt);
			etOptionName.setText("");
			//etOptionPrice.getText().clear();
			//etOptionPrice.setText(null);
			etOptionPrice.setText("0.0");
			//CurrencyFormat.setEditTextBlank(etOptionPrice);
			


			etOptionName.requestFocus(); 
			}
		}
			break;
		case R.id.bNext: {
			System.out.println("next button press");
			
			//TODO save option data to db on next and back?
			
			Cursor c = db.getAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
			
			if (c.getCount() == 0) {
				String message = "Please enter at least one default option and price";
				Toast t = Toast.makeText(getActivity(), message,
						Toast.LENGTH_SHORT);
				t.show();
				c.close();
			}
			else{
			getFragmentManager()
			.beginTransaction()
			.replace(R.id.container, new EditMenuAddonsFragment(), EditMenuAddonsFragment.TAG)
			.addToBackStack(null)
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.commit();
			}
		}
			break;
		case R.id.bBack: {
			System.out.println("back button press");

			EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment)
	                getFragmentManager().findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);
			
			getFragmentManager()
			.beginTransaction()
			.replace(R.id.container, NameDiscIngFrag)
			.addToBackStack(null)
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.commit();
			
		}
			break;
		}
	}
	
	private Boolean checkIfInputValid(String optionNameArg, String optionPriceArg) {
		Log.i(TAG, "Checking if input correct");
		String optionName =  optionNameArg;
		String optionPriceStr =	optionPriceArg;
		
		if (optionName.equals("")){
			String message = "Please Enter an option Name";
			Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
			t.show();
			return false;
		}
		if (optionPriceStr.equals("")){
			String message = "Please enter a Price for that option";
			Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
			t.show();
			return false;
		}
		//TODO other checks, price range etc
		Log.i(TAG, "input is valid");

		return true;
		
	}
	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}
	
	
	
}
