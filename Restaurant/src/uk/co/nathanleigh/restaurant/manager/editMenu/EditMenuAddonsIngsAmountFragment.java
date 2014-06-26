package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.SimpleCursorTreeAdapter;

public class EditMenuAddonsIngsAmountFragment extends Fragment implements
		OnClickListener {

	private DatabaseAdapter db;
	private View rootView;
	private Cursor cTempOptions;
	private Cursor cTempAddons;
	private ExpandableListAdapter expListAdapter;
	private Context context;;
	public static final String TAG = "ADDONSINGSAMOUNT";
	private static String editType;
	private static int menuItemId;
	public static double[][] price;
	public static String[][] amount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		editType = getEditType();
		menuItemId = getMenuItemId();

		System.out.println("editTYpe = " + editType);

		// if temp options ing db already filled, do not need to intialise
		Cursor cTempAddonsIngsAmount = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);

		if (cTempAddonsIngsAmount.getCount() == 0) {
			initialiseAmounts();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_addons_ings_amount, container,
				false);
		context = getActivity();

		Button bBack = (Button) rootView.findViewById(R.id.bBack);
		Button bNext = (Button) rootView.findViewById(R.id.bNext);
		bBack.setOnClickListener(this);
		bNext.setOnClickListener(this);

		fillExpListData();
		setUpExpListAdapter();

		return rootView;
	}

	@SuppressWarnings("deprecation")
	private void setUpExpListAdapter() {
		cTempOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		getActivity().startManagingCursor(cTempOptions);

		System.out.println("set UP list adapter");
		expListAdapter = new MenuAddonsExpandableListAdapter(

		// the cursor containing the groups and the context
				cTempOptions, context,
				// layout for groups, uses layout from android
				// R.layout.item_view_ing_addon_ing,
				android.R.layout.simple_expandable_list_item_1,

				// layout for children
				// R.layout.item_view_ing_amount_price,
				R.layout.item_view_ing_addon_ing,
				// group from, the key row id will aquired
				new String[] { DatabaseAdapter.KEY_OPTIONNAME },
				// groups to, will be placed in a textview
				new int[] { android.R.id.text1 },
				// children from
				new String[] { DatabaseAdapter.KEY_ADDONNAME,
						DatabaseAdapter.KEY_INGNAME,
						DatabaseAdapter.KEY_INGUNIT },
				// children to
				new int[] { R.id.tvAddonName, R.id.tvIngName, R.id.tvIngUnit }

		);

		ExpandableListView list = (ExpandableListView) rootView
				.findViewById(R.id.expandableListView2);

		System.out.println("set THE list adapter");
		// set up the expandable list adapter
		list.setAdapter((ExpandableListAdapter) expListAdapter);

		for (int i = 0; i < expListAdapter.getGroupCount(); i++)
			list.expandGroup(i);

		list.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true; // This way the expander cannot be collapsed
			}
		});

	}

	private void initialiseAmounts() {
		System.out.println("get cursor counts");

		System.out.println("Instialising Amountss");
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		System.out.println("Deleted temp Tabe");
		// if create set all temp orders Addon ing amounts and price to 0
		// if update set all temp order addon ing amounts and price to amounts
		// from temporder dbs
		Cursor cOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
		Cursor cAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);

		Log.i(TAG, "Number in cOptions temp table option = " + cOptions.getCount());
		Log.i(TAG, "Number in cAddons temp table addon = " + cAddons.getCount());

		if (editType.equals("create")) {

			for (cOptions.moveToFirst(); !cOptions.isAfterLast(); cOptions
					.moveToNext()) {
				
				int optionId = cOptions.getInt(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				String optionName = cOptions.getString(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons
						.moveToNext()) {
					int addonId = cAddons.getInt(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
					String addonName = cAddons.getString(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

					int ingId = cAddons.getInt(cAddons
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
					String ingName = cAddons.getString(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
					String ingUnit = cAddons.getString(cAddons
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));

					int addonAmount = 0; 
					int addonPrice = 0;

					db.insertRowTempAddonAmountPriceForMenuItemOption(optionId,
							optionName, addonId, addonName, ingId, ingName, ingUnit,
							addonAmount, addonPrice);
					System.out.println("Create: Inserted 0 into Temp addon ing table");

				}
			}

		}
		
		
		if (editType.equals("update")) {

			Log.i(TAG, "MenyItemId = " + menuItemId);
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cIngAmounts = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
							where);
			Log.i(TAG, "Number in cIngsAMount table addon ing amount price for menu item option" +
					" = " + cIngAmounts.getCount());


			for (cOptions.moveToFirst(); !cOptions.isAfterLast(); cOptions
					.moveToNext()) {
				int optionId = cOptions.getInt(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				String optionName = cOptions.getString(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons
						.moveToNext()) {
					
					int ingId = cAddons.getInt(cAddons
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
					String ingName = cAddons
							.getString(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
					String ingUnit = cAddons
							.getString(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));

					int addonId = cAddons
							.getInt(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
					String addonName = cAddons
							.getString(cAddons
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
					Log.i(TAG, " getting the addon amount and price from the cIngAMounts");
					
					int addonAmount = cIngAmounts
							.getInt(cIngAmounts
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT));
					int addonPrice = cIngAmounts
							.getInt(cIngAmounts
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

				
					db.insertRowTempAddonAmountPriceForMenuItemOption(optionId,
							optionName, addonId, addonName, ingId, ingName, ingUnit,
							addonAmount, addonPrice);

					System.out.println("Update: Inserted addonName and addon Price" +
							" into Temp addon ing table " + addonName + " "+
							addonAmount + " " + addonPrice);

					if (!cIngAmounts.isLast()) {
						cIngAmounts.moveToNext();
					}
				}
			}
			cIngAmounts.close();
		}

		cAddons.close();
		cOptions.close();
	}

	private void fillExpListData() {

		Cursor cTempAddonsIngsAmount = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		Cursor cOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
		Cursor cAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);

		int optionsCount = cOptions.getCount();
		int addonsCount = cAddons.getCount();

		cAddons.close();
		cOptions.close();

		System.out.println("temp addons count =  "
				+ cTempAddonsIngsAmount.getCount());
		System.out.println("menu item option count =  " + optionsCount);
		System.out.println("menu item addons count =  " + addonsCount);

		amount = new String[optionsCount][addonsCount]; // Initialize array
														// to proper # of
														// items
		price = new double[optionsCount][addonsCount]; // Initialize array to
														// proper # of items
		int i = 0;
		int j = 0;

		while (i < optionsCount) {
			while (j < addonsCount) {

				int ingAmountForAddon = cTempAddonsIngsAmount
						.getInt(cTempAddonsIngsAmount
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT));

				amount[i][j] = "" + ingAmountForAddon; // set all EditTexts
														// value

				j++;
				if (!cTempAddonsIngsAmount.isLast()) {
					cTempAddonsIngsAmount.moveToNext();
					Log.i(TAG, "addend ingAmounfor addon to array "
							+ ingAmountForAddon);
				}

			}
			j = 0; //
			i++;
		}

		cTempAddonsIngsAmount.moveToFirst();
		i = 0;
		j = 0;

		while (i < optionsCount) {
			while (j < addonsCount) {
				int priceForAddon = cTempAddonsIngsAmount
						.getInt(cTempAddonsIngsAmount
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

				price[i][j] = CurrencyFormat.currencyIntToDouble(priceForAddon); // set
																					// all
																					// EditTexts
																					// value

				j++;
				if (!cTempAddonsIngsAmount.isLast()) {
					cTempAddonsIngsAmount.moveToNext();
					Log.i(TAG, "addend price for addon to array "
							+ priceForAddon);
				}

			}
			j = 0; //
			i++;
		}
		cTempAddonsIngsAmount.close();
		System.out.println("finished setting up price and quanity arrays");
	}

	private void addAmountsAndPriceToDatabase() {
		openDb();
		
		db.deleteAllRows(DatabaseAdapter.
				DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		
		// get all the rows for the kitchen table
		cTempOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		// get all the rows for the addons table
		cTempAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);

		cTempOptions.moveToFirst();
		cTempAddons.moveToFirst();

		int optionId;
		String optionName;
		int addonId;
		int ingId;
		String ingName;
		String ingUnit;
		String addonName;
		int i = 0;
		int j = 0;

		System.out.println("START ADDON AND PRICE AMOUNT DB INSERT");

		for (cTempOptions.moveToFirst(); !cTempOptions.isAfterLast(); cTempOptions
				.moveToNext()) {
			j = 0;
			System.out.println("inserting group " + i);

			optionId = cTempOptions.getInt(cTempOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			optionName = cTempOptions.getString(cTempOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			System.out.println("option Id = " + optionId + " optionName = "
					+ optionName);

			for (cTempAddons.moveToFirst(); !cTempAddons.isAfterLast(); cTempAddons
					.moveToNext()) {
				System.out.println("inserting child " + j);
				if (MenuAddonsExpandableListAdapter.amount[i][j] == null) {

					System.out.println("amount ARRAY IS NULL");
					//TODO ERRROR FIND OUT WHY
					MenuAddonsExpandableListAdapter.amount[i][j] = "0";
				}

				if (MenuAddonsExpandableListAdapter.amount[i][j].equals("")) {

					MenuAddonsExpandableListAdapter.amount[i][j] = "0";

				}

				//if (MenuAddonsExpandableListAdapter.price[i][j] == null) {

					//System.out.println("price ARRAY IJ NULL");
					//TODO ERRROR FIND OUT WHY
					//MenuAddonsExpandableListAdapter.price[i][j] = "0";
				//}
//TODO
				//if (MenuAddonsExpandableListAdapter.price[i][j].equals("")) {
					//TODO SHOuld this be int 0.0 ???
				//	MenuAddonsExpandableListAdapter.price[i][j] = "0";
				//}
				
				if (MenuAddonsExpandableListAdapter.price[i][j] == 0.0) {
					//TODO SHOuld this be int 0.0 ???
					MenuAddonsExpandableListAdapter.price[i][j] = 0.0;

				}

				// System.out.println("get the ammount from the array");

				int addonAmount = Integer
						.parseInt(MenuAddonsExpandableListAdapter.amount[i][j]);

				int addonPrice = CurrencyFormat.currencyDoubleToInt
						(MenuAddonsExpandableListAdapter.price[i][j]);

				System.out
						.println("trying to get child cursor row id and ing name");

				addonId = cTempAddons.getInt(cTempAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				addonName = cTempAddons.getString(cTempAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
				ingId = cTempAddons.getInt(cTempAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				ingName = cTempAddons.getString(cTempAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				ingUnit = cTempAddons.getString(cTempAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

				System.out.println("addon Id = " + addonId + " addonName = "
						+ addonName);
				System.out.println("ing Id = " + ingId + " ingName = "
						+ ingName);
				System.out.println("Price for addon " + addonName
						+ " in option " + optionName + " is " + addonPrice);
 
				db.insertRowTempAddonAmountPriceForMenuItemOption(optionId,
						optionName, addonId, addonName, ingId, ingName, ingUnit,
						addonAmount, addonPrice);

				System.out.println("INSERTED INTO TABLE");
				System.out
						.println("-----------------------------------------------");

				j++;
			}

			i++;
			// j = 0;
			// cTempAddons.moveToFirst();
			System.out
					.println("===============================================");
		}

		cTempOptions.close();
		cTempAddons.close();

		System.out.println("ENDED ADDING TO DATABASE");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bNext: {
			System.out.println("next button pressed");

			getFragmentManager()
					.beginTransaction()
					.replace(R.id.container, new EditMenuReviewFragment(),
							EditMenuReviewFragment.TAG).addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

		}
			break;
		case R.id.bBack: {
			System.out.println("back button press");

			EditMenuOptionsIngsAmountFragment optionsFrag = (EditMenuOptionsIngsAmountFragment) getFragmentManager()
					.findFragmentByTag(EditMenuOptionsIngsAmountFragment.TAG);

			getFragmentManager().beginTransaction()
					.replace(R.id.container, optionsFrag).addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
		}
			break;
		}

	}

	@Override
	public void onPause() {
		super.onStart();

		addAmountsAndPriceToDatabase();

	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	private int getMenuItemId() {
		return EditMenuNameDiscIngsFragment.menuItemId;
	}

	private String getEditType() {
		return EditMenuNameDiscIngsFragment.editType;
	}

	// ///////////////////////////////////////////////////////////

	private static class MenuAddonsExpandableListAdapter extends
			SimpleCursorTreeAdapter implements TextWatcher {

		private DatabaseAdapter db;
		private Context context;
		private String tempText = "";
		// need to store price as DOUBLE save in db as int, get from db convert
		// to double
		private String tempPriceText = "0.0";
		private Cursor c;
		public static String[][] amount;
		public static double[][] price;
		private EditText etIngAmount;
		private EditText etPrice;

		public MenuAddonsExpandableListAdapter(Cursor cursor, Context context,
				int groupLayout, int childLayout, String[] groupFrom,
				int[] groupTo, String[] childrenFrom, int[] childrenTo) {

			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childrenFrom, childrenTo);

			this.context = context;
			openDB();

			amount = EditMenuAddonsIngsAmountFragment.amount;
			price = EditMenuAddonsIngsAmountFragment.price;

		}

		// returns cursor with subitems for given group cursor
		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			openDB();
			c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
			closeDB();
			return c;
		}

		// @Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View rowView = super.getChildView(groupPosition, childPosition,
					isLastChild, convertView, parent);

			final int cPos = childPosition;
			final int gPos = groupPosition;

			etIngAmount = (EditText) rowView.findViewById(R.id.etIngAmount);

			etPrice = (EditText) rowView.findViewById(R.id.etPrice);
			int maxLength = 13; // prevent entering a number too large
			etPrice.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					maxLength) });
			etPrice.addTextChangedListener(CurrencyFormat
					.getCurrencyInputTextWatcher(etPrice));

			System.out
					.println("setting focus on ing amount and price edit text boxes");

			// need this?
			etIngAmount.setText(tempText);
			// etPrice.setText(tempText);

			// TODO BUG: last text box that you have focus never gets saved,
			// treid
			// using a text watcher didnt work?

			etIngAmount
					.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {

								EditText etAddonIngAmount = (EditText) v
										.findViewById(R.id.etIngAmount);

								String ingAmount = etAddonIngAmount.getText()
										.toString();

								amount[gPos][cPos] = etAddonIngAmount.getText()
										.toString();

								System.out.println("ing amount changed"
										+ ingAmount);
								System.out.println("childPosition "
										+ childPosition);
								System.out.println("groupPosition "
										+ groupPosition);
								System.out
										.println("================================= ");

							}
						}
					});

			etIngAmount.setText(amount[gPos][cPos]);

			// etPrice.setText(tempText);

			etPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {

						EditText etAddonPrice = (EditText) v
								.findViewById(R.id.etPrice);

						String priceAmount = etAddonPrice.getText().toString();
						double priceDouble = CurrencyFormat
								.currencyStringToDouble(priceAmount);
						price[gPos][cPos] = priceDouble;

						System.out
								.println("price amount changed" + priceAmount);
						System.out.println("childPosition " + childPosition);
						System.out.println("groupPosition " + groupPosition);
						System.out
								.println("================================= ");

					}
				}
			});

			etPrice.setText("" + price[gPos][cPos]);

			return rowView;

		}

		private void openDB() {
			db = new DatabaseAdapter(context);
			db.open();
		}

		private void closeDB() {
			db.close();
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

}
