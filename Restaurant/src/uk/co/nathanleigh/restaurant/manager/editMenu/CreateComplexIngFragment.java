package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.IngredientsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateComplexIngFragment extends Fragment implements
		OnClickListener {
	private static ViewGroup mContainerView;
	private static Activity context;
	private static EditText etComplexIngAmount;
	public static String editType;
	public static int menuItemId;
	public static int complexIngId;
	public static String complexIngName;
	public static String complexIngUnit;
	public static String description;
	public static int complexIngAmount;
	public static String ingType;
	public static String ingTypePrev;
	private TextView tvComplexIngName;
	private TextView tvComplexIngUnit;
	private static DatabaseAdapter db;
	public static final String TAG = "CREATE_COMPLEX_INGS";
	private ViewGroup rootView;

	// set up thing so 1st time enters this, when it goes back to add ing can
	// only add
	// raw ings and will return to this screen, pressing back button will act
	// correct

	public CreateComplexIngFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Creating complex Ing Frag");
		openDb();
		// deleteTempOrder();
		Bundle bundle = getArguments();

		ingType = bundle.getString("ingType");
		Log.i("TAG", "menuItem Id is : " + ingType);
		ingTypePrev = bundle.getString("ingTypePrev");
		Log.i("TAG", "menuItem Id is : " + ingTypePrev);
		menuItemId = bundle.getInt("menuItemId");
		Log.i("TAG", "menuItem Id is : " + menuItemId);
		editType = bundle.getString("editType");
		Log.i("TAG", "Edit Type, create or update : " + editType);
		complexIngUnit = bundle.getString("complexIngUnit");
		Log.i("TAG", "MenuItemUnit : " + complexIngUnit);
		complexIngName = bundle.getString("complexIngName");
		Log.i("TAG", "MenuItemName : " + complexIngName);
		complexIngAmount = bundle.getInt("complexIngAmount");
		Log.i("TAG", "ComplexIngAmount : " + complexIngAmount);


		Log.i(TAG, "Created complex Ing Frag and got args");

	}

	private void deleteTempOrder() {
		// TODO delete temp complex ings
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG, "Create View");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_create_complex_ing, container, false);

		mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
		context = getActivity();

		etComplexIngAmount = (EditText) rootView
				.findViewById(R.id.etIngAmount);
		tvComplexIngUnit = (TextView) rootView
				.findViewById(R.id.tvComplexIngUnit);
		tvComplexIngName = (TextView) rootView
				.findViewById(R.id.tvComplexIngName);
		
	
		
		Button bAddIng = (Button) rootView.findViewById(R.id.bAddIng);
		Button bCancel = (Button) rootView.findViewById(R.id.bCancel);
		Button bConfirm = (Button) rootView.findViewById(R.id.bConfirm);
		bAddIng.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		bConfirm.setOnClickListener(this);

		Log.i(TAG, "setting Text");
		Log.i("TAG", "ComplexIngAmount : " + complexIngAmount);
		 
		etComplexIngAmount.setText("" + complexIngAmount);
		Log.i("TAG", "MenuItemUnit : " + complexIngUnit);

		tvComplexIngUnit.setText("" + complexIngUnit);
		Log.i("TAG", "MenuItemName : " + complexIngName);

		tvComplexIngName.setText("" + complexIngName);
		
		fillList();

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();

		// if(editType.equals("update")){
		// Log.i(TAG, "set text OnResume");
		// etComplexIngAmount.setText("" + complexIngAmount);
		// }

	}

	public static void updateIngListView(int rawIngId, String rawIngName,
			String ingUnit) {

		Log.i(TAG, "insert ing in temp ing table");
		int ingAmountProportion = 0;
		// TODO fix db, dont need complexRawId?
		//int complexIngId = 0;
		db.insertRowTempComplexIngredients(rawIngId, rawIngName, 
				ingUnit, ingAmountProportion);
		Log.i(TAG, "inserted into temp complex table");

	}

	private static void fillList() {
		Log.i(TAG, "Fill List");

		Cursor cursor = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
		// cursor.moveToFirst();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					Log.i(TAG, "TEST: cursor moved to first");

					int rowId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_ROWID));
					int rawIngId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_RAWINGID));
					String rawIngName = cursor.getString(cursor
							.getColumnIndex(DatabaseAdapter.KEY_RAWINGNAME));
					int ingAmount = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_INGAMOUNTPROPORTION));
					Log.i(TAG, "adding to list " + rowId + " " + rawIngId + " "
							+ rawIngName);
					String ingUnit = cursor.getString(cursor
							.getColumnIndex(DatabaseAdapter.KEY_RAWINGUNIT));
					addIngToList(rowId,  rawIngId, rawIngName,
							ingUnit, ingAmount);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

	}

	private static void addIngToList(int argRowId, int argRawIngId,
			String argRawIngName, String argIngUnit, int argIngAmount) {
		Log.i(TAG, "add Ing to list");
		final ViewGroup newView = (ViewGroup) context.getLayoutInflater()
				.inflate(R.layout.item_view_complex_ing, mContainerView, false);
		final int rowId = argRowId;
		final int ingAmount = argIngAmount;
		final int rawIngId = argRawIngId;
		final String rawIngName = argRawIngName;
		final String rawIngUnit = argIngUnit;
		// Set the text in the new row to a random country.
		((TextView) newView.findViewById(R.id.ingName)).setText(rawIngId + " "
				+ rawIngName);
		((TextView) newView.findViewById(R.id.tvIngUnit)).setText("" + rawIngUnit);
		
		
		EditText etIngAmount = (EditText) newView.findViewById(R.id.etIngAmount);
		etIngAmount.setText("" + ingAmount);

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

						mContainerView.removeView(newView);
						System.out.println(rawIngId + " delete ing" + rawIngName);
						db.deleteRow(
								DatabaseAdapter.DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS,
								rowId);

						// If there are no rows remaining, show the empty view.
						// if (mContainerView.getChildCount() == 0) {
						// getView().findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
						// }
					}
				});
		etIngAmount.setOnFocusChangeListener(
				new View.OnFocusChangeListener() {
			
			//null error db add ing
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO xhexk of ""
				EditText etIngAmount = (EditText) v.findViewById(R.id.etIngAmount);
				int newAmount = Integer.parseInt(etIngAmount.getText().toString());
				System.out.println("New amount " + newAmount);

				db.updateRowTempComplexIngredients(rowId, rawIngId, rawIngName, 
						rawIngUnit, newAmount);
			}
		});
		// text changed listener
		/*
		 *   textMessage.addTextChangedListener(new TextWatcher(){
        public void afterTextChanged(Editable s) {
            i++;
            tv.setText(String.valueOf(i) + " / " + String.valueOf(charCounts));
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
		 */
		
		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		mContainerView.addView(newView, 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bAddIng: {
			Log.i(TAG, "pressed add ing");
			Fragment fragment = new EditMenuAddIngFragment();
			Bundle bundle = new Bundle();
			bundle.putString("ingType", CreateComplexIngFragment.TAG);
			bundle.putString("ingTypePrev", ingTypePrev);
			// ingredients should show in list
			
			if(etComplexIngAmount.getText().toString().equals("")){
				complexIngAmount = 0;
			}
			else{
				complexIngAmount = Integer.parseInt
						(etComplexIngAmount.getText().toString());
				
			}
			
			bundle.putString("complexIngUnit", complexIngUnit);
			bundle.putString("complexIngName", complexIngName);
			bundle.putInt("complexIngAmount", complexIngAmount);

			fragment.setArguments(bundle);

			getFragmentManager()
					.beginTransaction()
					// getChildFragmentManager().beginTransaction()
					.replace(R.id.container, fragment, "addIng")
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

			// addItem();

		}

			break;

		case R.id.bConfirm: {
			Log.i(TAG, "Pressed confirm");
			if(etComplexIngAmount.getText().toString().equals("")){
				complexIngAmount = 0;
			}
			else{
				complexIngAmount = Integer.parseInt
						(etComplexIngAmount.getText().toString());
				
			}			// TODO add complex ing to database
			// dialog geting each ing amount
			// 1/ComplexIngAmount * rawIngAmount = ratioOfRawIngToComplexIng or proportion
			
			Log.i(TAG, "complexIngAmount = " + complexIngAmount);
			Log.i(TAG, "Inserting into ingredient database");
			
			complexIngId = (int)db.insertRowIngredients(complexIngName, complexIngUnit, 1);
			
			Log.i(TAG, "IngId = " + complexIngId);
			
			//TODO this right??
			//Log.i(TAG, "Inserting into temp menu items ingredient database");
			
			double ingAmountProportion = 0;
		
			Cursor cTempComplexIngs = db.getAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
			while (!cTempComplexIngs.isAfterLast()){
				
				
				int rawIngId = cTempComplexIngs.getInt(cTempComplexIngs
						.getColumnIndex(DatabaseAdapter.KEY_RAWINGID));
				String rawIngName = cTempComplexIngs.getString(cTempComplexIngs
						.getColumnIndex(DatabaseAdapter.KEY_RAWINGNAME));
				String rawIngUnit = cTempComplexIngs.getString(cTempComplexIngs
						.getColumnIndex(DatabaseAdapter.KEY_RAWINGUNIT));
				int rawIngAmount = cTempComplexIngs.getInt(cTempComplexIngs
						.getColumnIndex(DatabaseAdapter.KEY_INGAMOUNTPROPORTION));
				
				if(!(complexIngAmount == 0)){
					Log.i(TAG, "complex Ing amount != 0 ");
					Log.i(TAG, "rawIngName " + rawIngName);
					Log.i(TAG, "rawIngAmount = " + rawIngAmount);
					Log.i(TAG, "complexIngAmount = " + complexIngAmount);
					
					ingAmountProportion =  (double)rawIngAmount/(double)complexIngAmount;
					Log.i(TAG, "ingAmountProportion = " +
							"rawIngAmount/ complexIngAmount = " + ingAmountProportion);
				}
				else{
					Log.i(TAG, "complex Ing amount == 0 ");

					ingAmountProportion = 0;
				}
				Log.i(TAG, " inserting into complexIngTable");
				Log.i(TAG, "complexId:" + complexIngId + " complexIngName:" + complexIngName +
						" complexIngUnit:" + complexIngUnit + " rawIngId:" + rawIngId + 
						" rawIngName:" + rawIngName + " rawIngUnit " +
						rawIngUnit + " ingAmountProportion:" + ingAmountProportion);
				
				db.insertRowComplexIngredients(complexIngId, complexIngName, complexIngUnit,
						 rawIngId, rawIngName, rawIngUnit, ingAmountProportion);
				cTempComplexIngs.moveToNext();
				
			}
			cTempComplexIngs.close();
			Log.i(TAG, " Finished adding to db");
			Log.i(TAG, " Previous screen was : " + ingTypePrev);

			db.deleteAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
			
			if (ingTypePrev
					.equals(EditMenuNameDiscIngsFragment.TAG)) {
				//db.insertRowTempMenuItemIng(complexId, complexIngName);

				nameDiscIngs();

			}

			if (ingTypePrev.equals(EditMenuAddonsFragment.TAG)) {
				//db.insertRowTempMenuItemAddon(addonName, ingId, ingName)
				addons();

			}

			if (ingTypePrev.equals(IngredientsActivity.TAG)) {
				System.out
						.println(" INGREDIENTS Add this new ing to database");
			}
			System.out.println(" followed through");
			
			
		}
			break;
		case R.id.bCancel: {
			db.deleteAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
			Fragment fragment = new EditMenuAddIngFragment();
			Bundle bundle = new Bundle();
			bundle.putString("ingType", ingTypePrev);
			// ingredients should show in list
			
			
			fragment.setArguments(bundle);

			getFragmentManager()
					.beginTransaction()
					// getChildFragmentManager().beginTransaction()
					.replace(R.id.container, fragment, "addIng")
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
			
		}
			break;

		}
	}
	
	private void nameDiscIngs() {

		// check to see it item already exsts
		// TODO
		Log.i(TAG, "updating list in name disc ings and returning to fragment");
		//EditMenuNameDiscIngsFragment.updateIngListView((int) complexIngId, complexIngName);

		
		System.out.println("insert ing in temp ing table");
		Log.i(TAG, "ingId = " + complexIngId + " ingName " + complexIngName);
		
		db.insertRowTempMenuItemIng(complexIngId, complexIngName.toUpperCase(), complexIngUnit);

		
		EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
				.findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, NameDiscIngFrag)
				.addToBackStack("null")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();

	}

	private void addons() {
		Log.i(TAG, "updating list in addons and returning to fragment");
		//TODO
		EditMenuAddonsFragment.updateAddonTable((int) complexIngId, complexIngName,
				complexIngUnit);

		EditMenuAddonsFragment AddonsFrag = (EditMenuAddonsFragment) getFragmentManager()
				.findFragmentByTag(EditMenuAddonsFragment.TAG);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, AddonsFrag).addToBackStack("null")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();

	}
}
