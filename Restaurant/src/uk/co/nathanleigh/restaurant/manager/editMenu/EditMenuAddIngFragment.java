package uk.co.nathanleigh.restaurant.manager.editMenu;

import java.util.Currency;
import java.util.Locale;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.IngredientsActivity;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import uk.co.nathanleigh.restaurant.manager.StockLevelDialogFragment;
import uk.co.nathanleigh.restaurant.manager.StockLevelsActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
//TODO create complex, add ing, create asks if raw or complex, should just auto raw
public class EditMenuAddIngFragment extends Fragment implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener,
		OnClickListener {

	public static final String TAG = "ADDING";

	AlertDialog complexRawDialog;

	// Strings to Show In Dialog with Radio Buttons
	final CharSequence[] ingRawOrComplex = { " Raw ", " Complex " };
	final CharSequence[] ingUnits = { " mililitres(ml) ", " grams(g) " };
	private String rawOrComplex = "";
	private String ingUnit = "";

	public static String ingNameClicked = null;
	private ViewGroup rootView;
	private ListView mListView;
	private SearchView searchView;
	private DatabaseAdapter db;
	private String ingType;
	private String ingTypePrev;
	private String complexIngName;
	private String complexIngUnit;
	private int complexIngAmount;

	// private ViewGroup mContainerView;
	public EditMenuAddIngFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setHasOptionsMenu(true);
		Bundle bundle = getArguments();
		ingType = bundle.getString("ingType");

		if (ingType.equals(CreateComplexIngFragment.TAG)) {
			complexIngName = bundle.getString("complexIngName");
			complexIngUnit = bundle.getString("complexIngUnit");
			complexIngAmount = bundle.getInt("complexIngAmount");
			ingTypePrev = bundle.getString("ingTypePrev");
			Log.i("TAG", "complexIngUnit : " + complexIngUnit);
			Log.i("TAG", "complexIngName : " + complexIngName);
			Log.i("TAG", "ComplexIngAmount : " + complexIngAmount);

		}

		
		System.out.println("ingType is " + ingType);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_add_ing, container, false);

		Button bBack = (Button) rootView.findViewById(R.id.bBack);
		bBack.setOnClickListener(this);
		Button bAddNewIng = (Button) rootView.findViewById(R.id.bAddNewIng);
		bAddNewIng.setOnClickListener(this);

		searchView = (SearchView) rootView.findViewById(R.id.svIng);
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
		searchView.setOnCloseListener(this);

		mListView = (ListView) rootView.findViewById(R.id.lvSearchIng);

		openDb();
		fillList();
		// fillDbWithIngs();

		return rootView;
	}


	private void fillList() {

		Cursor cIngredients = null;
		
		// get list dependant on adding raw or both types of ings
		if (ingType.equals(CreateComplexIngFragment.TAG)){
			String where = DatabaseAdapter.KEY_INGCOMPLEX + "=" + 0;
			cIngredients = db.myDBQuery
					(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, where);
		}
		else{
			cIngredients = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS);
		}
		
		if (cIngredients == null) {
			//
		} else {
			// Specify the columns we want to display in the result
			String[] from = new String[] { DatabaseAdapter.KEY_INGNAME,
					DatabaseAdapter.KEY_INGUNIT, DatabaseAdapter.KEY_INGCOMPLEX };

			// Specify the Corresponding layout elements where we want the
			// columns to go
			int[] to = new int[] { R.id.tvIngNameList, R.id.tvIngUnit, R.id.tvIngComplex };

			// Create a simple cursor adapter for the definitions and apply them
			// to the ListView
			SimpleCursorAdapter customers = new SimpleCursorAdapter(
					getActivity(), R.layout.item_view_ing_name_search,
					cIngredients, from, to);
			mListView.setAdapter(customers);

			// Define the on-click listener for the list items
			mListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Get the cursor, positioned to the corresponding row in
					// the result set
					Cursor cClickResult = (Cursor) mListView
							.getItemAtPosition(position);

					String ingName = cClickResult.getString(cClickResult
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
					int ingId = cClickResult.getInt(cClickResult
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
					String ingUnit = cClickResult.getString(cClickResult
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));
					// System.out.println("TESTERROR ing name, ing rowId = " +
					// ingName +
					// ", " + ingId);
					cClickResult.close();

					ingNameClicked = ingName;

					if (ingType.equals(EditMenuNameDiscIngsFragment.TAG)) {
						EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
								.findFragmentByTag(
										EditMenuNameDiscIngsFragment.TAG);
						// check if ing is already in temp order db
						String where = DatabaseAdapter.KEY_ROWID + "=" + ingId;
						Cursor cTempMenuItemIngs = db
								.myDBQuery(
										DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS,
										where);
						int ingCount = cTempMenuItemIngs.getCount();
						cTempMenuItemIngs.close();
						if (ingCount == 0) {
							System.out.println("updating list");
							//EditMenuNameDiscIngsFragment.updateIngListView(
							//		ingId, ingName);
							
							System.out.println("insert ing in temp ing table");
							Log.i(TAG, "ingId = " + ingId + " ingName " + ingName);
							
							db.insertRowTempMenuItemIng(ingId, ingName.toUpperCase(), ingUnit);

							getFragmentManager()
									.beginTransaction()
									.replace(R.id.container, NameDiscIngFrag)
									.addToBackStack("null")
									.setTransition(
											FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
									.commit();
						} else {
							// TODO dont add repeat ings
							String message = "Cannot add "
									+ ingName
									+ ". It is already an ingredient for the menu item";
							Toast t = Toast.makeText(getActivity(), message,
									Toast.LENGTH_LONG);
							t.show();

						}
					}

					if (ingType.equals(EditMenuAddonsFragment.TAG)) {
						EditMenuAddonsFragment addonsFrag = (EditMenuAddonsFragment) getFragmentManager()
								.findFragmentByTag(EditMenuAddonsFragment.TAG);

						System.out.println("ingType ing return addons");

						//TODO
						EditMenuAddonsFragment.updateAddonTable(ingId, ingName, ingUnit);
						
						
						
						getFragmentManager()
								.beginTransaction()
								.replace(R.id.container, addonsFrag)
								.addToBackStack("null")
								.setTransition(
										FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
								.commit();
					}

					if (ingType.equals(IngredientsActivity.TAG)) {

						Boolean delete;
						System.out
								.println("ingType ing activity, implement delete later");
						// TODO
					}
//TODO keeps crashing, wont create/return to create comple frag
					
					if (ingType.equals(CreateComplexIngFragment.TAG)) {
						//CreateComplexIngFragment complexIngFrag = (CreateComplexIngFragment) getFragmentManager()
						//		.findFragmentByTag(CreateComplexIngFragment.TAG);
						CreateComplexIngFragment complexIngFrag = new CreateComplexIngFragment();
						Bundle bundle = new Bundle();
						//bundle.putString("ingType", ingType);
						//bundle.putString("ingComplexName", ingName);
						//bundle.putString("ingComplexUnit", ingUnit);
						
						//// bundle.putString("ingComplexAmount", ingAmount);

						//// bundle.putInt("menuItemId", menuItemId);
						//// bundle.putString("editType", editType);
					
						bundle.putInt("complexIngAmount", complexIngAmount);
						bundle.putString("complexIngName", complexIngName);
						bundle.putString("complexIngUnit", complexIngUnit);
						bundle.putString("ingTypePrev", ingTypePrev);

						complexIngFrag.setArguments(bundle);
						System.out.println("ingType ing return complexIng");

						//TODO
						CreateComplexIngFragment.updateIngListView(ingId,
								ingName, ingUnit);
						Log.i(TAG, "Returning to a complexIng Fragment");

						getFragmentManager()
								.beginTransaction()
								// .replace(R.id.container, new
								// CreateComplexIngFragment(), "complexIng")
								.replace(R.id.container, complexIngFrag, "compexIngFrag")
								.addToBackStack("null")
								.setTransition(
										FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
								.commit();
					
					}

				}

			});
		}
		// cIngredients.close();
	}

	public boolean onQueryTextChange(String newText) {
		newText.toUpperCase();
		showResults(newText + "*");
		return false;
	}

	public boolean onQueryTextSubmit(String query) {
		query.toUpperCase();
		showResults(query + "*");
		searchView.clearFocus();
		showComplexRawAlert(ingType, query);
		// showIngUnitAlert(ingType, rawOrComplex, query);

		return false;
	}

	public boolean onClose() {
		showResults("");
		return false;
	}

	private void showResults(String query) {
		query.toUpperCase();
		if (query.equals("*")) {
			fillList();
			return;
		}
		
		Cursor cSearchIng = null;
		//set list o show al ingor just raw ings
		if(ingType.equals(CreateComplexIngFragment.TAG)){
			 cSearchIng = db.searchRawIng((query != null ? query.toString()
						: "@@@@"));
		}
		else{
		 cSearchIng = db.searchIng((query != null ? query.toString()
				: "@@@@"));
		}
		
		if (cSearchIng == null) {
			//
		} else {
			// Specify the columns we want to display in the result
			String[] from = new String[] { DatabaseAdapter.KEY_INGNAME,
					DatabaseAdapter.KEY_INGUNIT };

			// Specify the Corresponding layout elements where we want the
			// columns to go
			int[] to = new int[] { R.id.tvIngNameList, R.id.tvIngUnit, };

			// Create a simple cursor adapter for the definitions and apply them
			// to the ListView
			SimpleCursorAdapter customers = new SimpleCursorAdapter(
					getActivity(), R.layout.item_view_ing_name_search,
					cSearchIng, from, to);
			mListView.setAdapter(customers);

			// Define the on-click listener for the list items
			mListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// Get the cursor, positioned to the corresponding row in
					// the result set
					Cursor cClickResult = (Cursor) mListView
							.getItemAtPosition(position);

					String ingName = cClickResult.getString(cClickResult
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
					int ingId = cClickResult.getInt(cClickResult
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
					cClickResult.close();
					System.out.println(ingName);
					System.out.println("ing id " + ingId);
					ingNameClicked = ingName;

					if (ingType.equals(EditMenuNameDiscIngsFragment.TAG)) {

						String where = DatabaseAdapter.KEY_ROWID + "="
								+ ingName;
						Cursor cIngs = db.myDBQuery(
								DatabaseAdapter.DATABASE_TABLE_INGREDIENTS,
								where);
						int ingCount = cIngs.getCount();
						cIngs.close();
						if (ingCount == 0) {
							EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
									.findFragmentByTag(
											EditMenuNameDiscIngsFragment.TAG);

						//	EditMenuNameDiscIngsFragment.updateIngListView(
							//		ingId, ingName);
							
							System.out.println("insert ing in temp ing table");
							Log.i(TAG, "ingId = " + ingId + " ingName " + ingName);
							
							db.insertRowTempMenuItemIng(ingId, ingName.toUpperCase(), ingUnit);

							getFragmentManager()
									.beginTransaction()
									.replace(R.id.container, NameDiscIngFrag)
									.addToBackStack("null")
									.setTransition(
											FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
									.commit();
						} else {
							String message = "Cannot add "
									+ ingName
									+ ". It is already an ingredient for the menu item";
							Toast t = Toast.makeText(getActivity(), message,
									Toast.LENGTH_LONG);
							t.show();
						}
					}

					if (ingType.equals(EditMenuAddonsFragment.TAG)) {
						EditMenuAddonsFragment addonsFrag = (EditMenuAddonsFragment) getFragmentManager()
								.findFragmentByTag(EditMenuAddonsFragment.TAG);

						EditMenuAddonsFragment.updateAddonTable(ingId, ingName, ingUnit);

						getFragmentManager()
								.beginTransaction()
								// getChildFragmentManager().beginTransaction()
								.replace(R.id.container, addonsFrag)
								.addToBackStack("null")
								.setTransition(
										FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
								.commit();
					}

					if (ingType.equals(IngredientsActivity.TAG)) {

						Boolean delete;
						// TODO
						System.out
								.println("ingType ing activity, implement delete later");

					}

				}
			});

		}
		// cSearchIng.close();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bBack: {

			if (ingType.equals(EditMenuNameDiscIngsFragment.TAG)) {
				EditMenuNameDiscIngsFragment nameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
						.findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);

				getFragmentManager()
						.beginTransaction()
						.replace(R.id.container, nameDiscIngFrag)
						.addToBackStack(null)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.commit();
			} else if (ingType.equals(EditMenuAddonsFragment.TAG)) {
				EditMenuAddonsFragment addonsFrag = (EditMenuAddonsFragment) getFragmentManager()
						.findFragmentByTag(EditMenuAddonsFragment.TAG);

				getFragmentManager()
						.beginTransaction()
						.replace(R.id.container, addonsFrag)
						.addToBackStack(null)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.commit();

			} else if (ingType.equals(IngredientsActivity.TAG)) {
				Intent intent = new Intent(getActivity(), ManagerActivity.class);
				startActivity(intent);

			}

		}
			break;

		case R.id.bAddNewIng: {

			// TODO check if query is coming from a create complex frag

			System.out.println("bAddnewing dialog show");

			String query = searchView.getQuery().toString();

			query = query.toUpperCase(Locale.getDefault());

			if (query.equals("")) {
				String message = "Please enter a name to add a new Ingredient";
				Toast t = Toast.makeText(getActivity(), message,
						Toast.LENGTH_SHORT);
				t.show();
			} else {

				showComplexRawAlert(ingType, query);

				// showIngUnitAlert(ingType, rawOrComplex, query);

			}

		}
			break;
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////

	private void showComplexRawAlert(final String ingType, final String query) {
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Is the ingredient you want to create " + query + " "
				+ "going to be a raw or complex ingredient");
		builder.setSingleChoiceItems(ingRawOrComplex, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0: // create raw ing selected, ask for units,
							// ask for nutrional info, create ingredient
							rawOrComplex = "RAW";

							break;
						case 1: // create complex selected, ask for units,
								// Ask for consituents, create complex ing
							rawOrComplex = "COMPLEX";

							break;

						}
						complexRawDialog.dismiss();
						showIngUnitAlert(ingType, rawOrComplex, query);

					}
				});
		complexRawDialog = builder.create();
		complexRawDialog.show();

	}

	private void showIngUnitAlert(final String ingType,
			final String rawOrComplex, final String query) {
		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Is the ingredient you want to create " + query + " "
				+ "going to be a raw or complex ingredient");
		builder.setSingleChoiceItems(ingUnits, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0: // create raw ing selected, ask for units,
							// ask for nutrional info, create ingredient
							ingUnit = "ML";
							break;
						case 1: // create complex selected, ask for units,
								// Ask for consituents, create complex ing
							ingUnit = "G";

							break;

						}
						complexRawDialog.dismiss();

						if (rawOrComplex.equals("RAW")) {
							createRawIngredient(ingType, query, ingUnit);
						}
						if (rawOrComplex.equals("COMPLEX")) {
							createComplexIngredient(ingType, query, ingUnit);
						}

					}

				});
		complexRawDialog = builder.create();
		complexRawDialog.show();

	}

	///////////////////////////////////////////////////////////////////////////////////////
	
	private void createRawIngredient(String ingType, String ingName,
			String ingUnit) {
		Log.i(TAG, "create raw ing");

		int complexIng = 0;

		long rowId = db.insertRowIngredients(ingName, ingUnit, complexIng);

		showStockLevelDialog(rowId, ingName, ingUnit);

		// TODO nutrition info

		// showNutritionDialog(rowId, ingName, ingUnit);

		// TODO specify this amount
		// db.insertRowStockLevels((int) rowId, ingName,
		// ingUnit, 10000, 20000, 5000);

	}

	private void createComplexIngredient(String ingType, String ingName,
			String ingUnit) {

		int complexIng = 1;

		//long rowId = db.insertRowIngredients(ingName, ingUnit, complexIng);

		db.deleteAllRows(DatabaseAdapter.
				DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
		
		
		CreateComplexIngFragment fragment = new CreateComplexIngFragment();
		Bundle bundle = new Bundle();
		bundle.putString("ingType", ingType);
		bundle.putString("ingTypePrev", ingType);
		bundle.putString("complexIngName", ingName);
		bundle.putString("complexIngUnit", ingUnit);
		// bundle.putString("ingComplexAmount", ingAmount);

		// bundle.putInt("menuItemId", menuItemId);
		// bundle.putString("editType", editType);
		
		//TODO keep a record of where it came from, menu item, addons ing menu,
		// saved separate 
		
		fragment.setArguments(bundle);

		getFragmentManager()
				.beginTransaction()
				// getChildFragmentManager().beginTransaction()
				.replace(R.id.container, fragment, CreateComplexIngFragment.TAG)
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();

	}
	
	////////////////////////////////////////////////////////////////////////////////////

	/*
	private void showNutritionDialog(long ingId, String ingName, String ingUnit2) {
		Log.i(TAG, "Show nutrion dialog");
		IngNutritionInfoDialogFragment df = new IngNutritionInfoDialogFragment();

		Bundle args = new Bundle();
		args.putInt("rowId", (int) ingId);
		args.putString("rowId", ingName);
		args.putString("ingUnit", ingUnit);

		df.setArguments(args);
		df.show(getFragmentManager(), TAG);
		// getActivity().showStockLevelDialog(df, getActivity());

	}
	*/

	private void showStockLevelDialog(long ingId, String ingName,
			String ingUnit2) {
		StockLevelDialogFragment df = new StockLevelDialogFragment();
		Log.i(TAG, "Show stcok Level dialog");

		int ingAmount = 0;
		int ingRecAmount = 0;
		int ingWarningAmount = 0;

		Bundle args = new Bundle();
		args.putInt("ingId", (int) ingId);
		args.putString("ingName", ingName);
		args.putString("ingUnit", ingUnit);
		args.putInt("ingAmount", ingAmount);
		args.putInt("ingRecAmount", ingRecAmount);
		args.putInt("ingWarningAmount", ingWarningAmount);
		args.putString("ingType", ingType);
		
		//bundle.putString("ingType", ingType);
		//bundle.putString("ingComplexName", ingName);
		//bundle.putString("ingComplexUnit", ingUnit);
		
		//// bundle.putString("ingComplexAmount", ingAmount);

		//// bundle.putInt("menuItemId", menuItemId);
		//// bundle.putString("editType", editType);
	
		args.putInt("complexIngAmount", complexIngAmount);
		args.putString("complexIngName", complexIngName);
		args.putString("complexIngUnit", complexIngUnit);
		args.putString("ingTypePrev", ingTypePrev);
		
		df.setArguments(args);
		df.show(getFragmentManager(), TAG);
		// df.show(getFragmentManager(), TAG);

	}

	/////////////////////////////////////////////////////////////////////////////
	
	
	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (db != null) {
			db.close();
		}
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////

	public String getIngName(int ingId) {
		String where = DatabaseAdapter.KEY_ROWID + "=" + ingId;
		Cursor c = db.myDBQuery(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS,
				where);
		String ingName = c.getString(c
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
		c.close();
		return ingName;
	}
	
	private void fillDbWithIngs() {
		// TODO fill with data use another class/file?
		// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_STOCK_LEVELS);

		// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS);
		// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_STOCK_LEVELS);
		// Add some Customer data as a sample
		int ingId;
		int ingQty = 100;
		String ingName;

		ingId = (int) db.insertRowIngredients("Egg", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Apple", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Avacardo", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Almond", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Apple Juice", "ml", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "ml", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Egg Shells", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Bannana", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Bread", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Milk", "ml", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "ml", ingQty, 50, 75);

		ingId = (int) db.insertRowIngredients("Tomato", "g", 0);
		ingName = getIngName(ingId);
		db.insertRowStockLevels(ingId, ingName, "g", ingQty, 50, 75);

	}
}
