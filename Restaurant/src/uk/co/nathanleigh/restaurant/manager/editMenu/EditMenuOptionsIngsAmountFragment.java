package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
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
import android.widget.TextView;

public class EditMenuOptionsIngsAmountFragment extends Fragment implements
		OnClickListener {

	private static Activity context;
	private ViewGroup rootView;
	private static DatabaseAdapter db;
	private Cursor cTempMenuItemIngs; // cursor for list of groups (list top
										// nodes)
	private Cursor cTempMenuItemOptions; // cursor for list of groups (list top
											// nodes)
	private ExpandableListAdapter expListAdapter;
	public static final String TAG = "OPTIONSINGSAMOUNT";
	public static String[][] amount;
	private ExpandableListView list;
	private static String editType;
	private static int menuItemId;
	private Button bBack;
	private Button bNext;

	// TODO bug on update, if added a ing, options amounts get filled in for ing
	// yet none were ever given
	// shopuld be 0 still
	// array fills each empty position, if new positions added array order
	// gets incorrext, ???

	public EditMenuOptionsIngsAmountFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();

		editType = getEditType();
		menuItemId = getMenuItemId();

		System.out.println("editTYpe = " + editType);
		// if temp options ing db already filled, do not need to intialise
		// TODO close cursor?
		Cursor cTempOptionIngsAmount = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);

		if (cTempOptionIngsAmount.getCount() == 0) {
			initialiseAmounts();
		}
		cTempOptionIngsAmount.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_options_ings_amount, container,
				false);

		context = getActivity();
		bBack = (Button) rootView.findViewById(R.id.bBack);
		bNext = (Button) rootView.findViewById(R.id.bNext);
		bBack.setOnClickListener(this);
		bNext.setOnClickListener(this);

		fillExpListData();
		setUpExpListAdapter();

		return rootView;

	}

	public static void initialiseAmounts() {

		System.out.println("Instialising Amountss");
		// TODO already intialised?
		// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);

		// if create set all temp orders ing amounts to 0
		// if update set all temp order ing amount to amounts
		// from temporder dbs

		Cursor cMenuItemIngs = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		Cursor cOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		int menuItemIngsCount = cMenuItemIngs.getCount();
		int optionsCount = cOptions.getCount();

		System.out.println("cOptions " + optionsCount);
		System.out.println("cMenuItemIngs " + menuItemIngsCount);

		if (editType.equals("create")) {

			for (cOptions.moveToFirst(); !cOptions.isAfterLast(); cOptions
					.moveToNext()) {
				int optionId = cOptions.getInt(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				String optionName = cOptions.getString(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				for (cMenuItemIngs.moveToFirst(); !cMenuItemIngs.isAfterLast(); cMenuItemIngs
						.moveToNext()) {
					int ingId = cMenuItemIngs.getInt(cMenuItemIngs
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
					String ingName = cMenuItemIngs
							.getString(cMenuItemIngs
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

					int amount = 0;
					// TODO intialise array?

					db.insertRowTempIngAmountForMenuItemOption(optionId,
							optionName, ingId, ingName, amount);

				}
			}

		}
		if (editType.equals("update")) {

			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cIngAmounts = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
							where);

			for (cOptions.moveToFirst(); !cOptions.isAfterLast(); cOptions
					.moveToNext()) {
				int optionId = cOptions.getInt(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				String optionName = cOptions.getString(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				for (cMenuItemIngs.moveToFirst(); !cMenuItemIngs.isAfterLast(); cMenuItemIngs
						.moveToNext()) {
					int ingId = cMenuItemIngs.getInt(cMenuItemIngs
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
					String ingName = cMenuItemIngs
							.getString(cMenuItemIngs
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

					int amount = cIngAmounts
							.getInt(cIngAmounts
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));

					if (!cIngAmounts.isLast()) {
						cIngAmounts.moveToNext();
						Log.i(TAG, "Cursor ing amounts options moved to next");
					}
					// TODO ingId
					db.insertRowTempIngAmountForMenuItemOption(optionId,
							optionName, ingId, ingName, amount);

				}
			}
			cIngAmounts.close();
		}
		cOptions.close();
		cMenuItemIngs.close();

	}

	private void fillExpListData() {
		Cursor cTempOptionIngsAmount = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
		Cursor cMenuItemIngs = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		Cursor cOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		int menuItemIngsCount = cMenuItemIngs.getCount();
		int optionsCount = cOptions.getCount();

		System.out.println("cOptions " + optionsCount);
		System.out.println("cMenuItemIngs " + menuItemIngsCount);

		// array which holds the data
		amount = new String[optionsCount][menuItemIngsCount]; // Initialize
																// array to
		// proper # of items
		int i = 0;
		int j = 0;

		while (i < optionsCount) {
			while (j < menuItemIngsCount) {
				int ingAmountForOption = cTempOptionIngsAmount
						.getInt(cTempOptionIngsAmount
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));

				amount[i][j] = "" + ingAmountForOption; // set all EditTexts
														// value

				j++;
				if (!cTempOptionIngsAmount.isLast()) {
					cTempOptionIngsAmount.moveToNext();
					Log.i(TAG, "addend ingAmounfor option to array "
							+ ingAmountForOption);
				}

			}
			j = 0; //
			i++;
		}
		cMenuItemIngs.close();
		cOptions.close();
		cTempOptionIngsAmount.close();
	}

	@SuppressWarnings("deprecation")
	private void setUpExpListAdapter() {

		// get all the rows for the kitchen table
		db.open();
		cTempMenuItemOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		// get all the rows for the kitchen table
		cTempMenuItemIngs = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);

		getActivity().startManagingCursor(cTempMenuItemOptions);

		// set up the new adapter
		expListAdapter = new MenuOptionsExpandableListAdapter(

		// the cursor containing the groups and the context
				cTempMenuItemOptions, context,
				// layout for groups, uses layout from android
				android.R.layout.simple_expandable_list_item_1,
				// layout for children
				R.layout.item_view_ing_amount,
				// group from, the key row id will aquired
				new String[] { DatabaseAdapter.KEY_OPTIONNAME },
				// groups to, will be placed in a textview
				new int[] { android.R.id.text1 },
				// children from
				new String[] { DatabaseAdapter.KEY_INGNAME, DatabaseAdapter.KEY_INGUNIT },
				// children to
				new int[] { R.id.tvIngName, R.id.tvIngUnit }

		);

		list = (ExpandableListView) rootView
				.findViewById(R.id.expandableListView1);
		System.out.println("set up list adapter");
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

		// cTempMenuItemOptions.close();
		// cTempMenuItemIngs.close();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bNext: {
			System.out.println("next button press");
			// TODO focus change listener not picking up last value you
			// entered??
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.container,
							new EditMenuAddonsIngsAmountFragment(),
							EditMenuAddonsIngsAmountFragment.TAG)
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

		}
			break;
		case R.id.bBack: {
			System.out.println("back button press");

			EditMenuAddonsFragment addonsFrag = (EditMenuAddonsFragment) getFragmentManager()
					.findFragmentByTag(EditMenuAddonsFragment.TAG);

			getFragmentManager().beginTransaction()
					.replace(R.id.container, addonsFrag).addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

		}
			break;
		}
	}

	@Override
	public void onPause() {
		super.onStart();

		addAmountsToDatabase();

	}

	private void addAmountsToDatabase() {
		openDb();
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);

		// get all the rows for the kitchen table
		cTempMenuItemOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		// get all the rows for the kitchen table
		cTempMenuItemIngs = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		cTempMenuItemOptions.moveToFirst();
		cTempMenuItemIngs.moveToFirst();
		int optionId;
		String optionName;
		int ingId;
		String ingName;
		int i = 0;
		int j = 0;

		System.out.println("START OPTION AMOUNT DB INSERT");

		for (cTempMenuItemOptions.moveToFirst(); !cTempMenuItemOptions
				.isAfterLast(); cTempMenuItemOptions.moveToNext()) {
			j = 0;
			System.out.println("inserting group " + i);

			optionId = cTempMenuItemOptions.getInt(cTempMenuItemOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			optionName = cTempMenuItemOptions.getString(cTempMenuItemOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			System.out.println("option Id = " + optionId + " optionName = "
					+ optionName);

			for (cTempMenuItemIngs.moveToFirst(); !cTempMenuItemIngs
					.isAfterLast(); cTempMenuItemIngs.moveToNext()) {
				System.out.println("inserting child " + j);

				System.out.println("checknothing entered");
				System.out.println("i = " + i + " and j = " + j);

				if (MenuOptionsExpandableListAdapter.amount[i][j] == null) {

					System.out.println("ARRAY IJ NULL");
					// TODO ERRROR FIND OUT WHY
					MenuOptionsExpandableListAdapter.amount[i][j] = "0";
				}

				if (MenuOptionsExpandableListAdapter.amount[i][j].equals("")) {

					MenuOptionsExpandableListAdapter.amount[i][j] = "0";

				}

				System.out.println("get the ammount from the array");
				int ingAmount = Integer
						.parseInt(MenuOptionsExpandableListAdapter.amount[i][j]);
				System.out
						.println("trying to get child cursor row id and ing name");

				ingId = cTempMenuItemIngs.getInt(cTempMenuItemIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				ingName = cTempMenuItemIngs.getString(cTempMenuItemIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

				System.out.println("ing Id = " + ingId + " ingName = "
						+ ingName);

				// TODO ingId changed above ing id from row id
				db.insertRowTempIngAmountForMenuItemOption(optionId,
						optionName, ingId, ingName, ingAmount);

				System.out.println("ing " + ingName
						+ " insting into db Amount = " + ingAmount);
				System.out
						.println("-----------------------------------------------");

				j++;
			}

			i++;
			System.out
					.println("===============================================");
		}

		cTempMenuItemOptions.close();
		cTempMenuItemIngs.close();

		System.out.println("ENDED ADDING O DATABASE");
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	private int getMenuItemId() {
		return EditMenuNameDiscIngsFragment.menuItemId;
	}

	private String getEditType() {
		return EditMenuNameDiscIngsFragment.editType;
	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////

	private static class MenuOptionsExpandableListAdapter extends
			SimpleCursorTreeAdapter implements TextWatcher {

		private static DatabaseAdapter db;
		private Context context;
		private Cursor cTempMenuItemIng;
		public static String[][] amount;
		private EditText textMessage;

		public MenuOptionsExpandableListAdapter(Cursor cursor, Context context,
				int groupLayout, int childLayout, String[] groupFrom,
				int[] groupTo, String[] childrenFrom, int[] childrenTo) {

			super(context, cursor, groupLayout, groupFrom, groupTo,
					childLayout, childrenFrom, childrenTo);

			this.context = context;
			// the array containing all the ing amounts for the diffenent option
			amount = EditMenuOptionsIngsAmountFragment.amount;

		}

		// returns cursor with subitems for given group cursor
		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			openDB();
			cTempMenuItemIng = db
					.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
			return cTempMenuItemIng;
		}

		// @Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			View rowView = super.getChildView(groupPosition, childPosition,
					isLastChild, convertView, parent);

			final int cPos = childPosition;
			final int gPos = groupPosition;

			textMessage = (EditText) rowView.findViewById(R.id.etIngAmount);


			String ingAmount = "";

			String where = DatabaseAdapter.KEY_OPTIONID + " = " + gPos
					+ " AND " + DatabaseAdapter.KEY_INGID + " = " + cPos;
			Cursor cTempOptionIngAmount = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
							where);
			int cCount = cTempOptionIngAmount.getCount();
			System.out.println("cc count = " + cCount);
			System.out.println("cPos = " + cPos);

			textMessage.setText(amount[gPos][cPos]);
			
			cTempOptionIngAmount.close();

			// TODO last text box that you have focus never gets saved, treid
			// using a text watcher didnt work?

			textMessage
					.setOnFocusChangeListener(new View.OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (!hasFocus) {

								EditText etIngAmount = (EditText) v
										.findViewById(R.id.etIngAmount);

								String ingAmount = etIngAmount.getText()
										.toString();

								amount[gPos][cPos] = etIngAmount.getText()
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

			System.out.println("amount end[gPos][cPos] is "
					+ amount[gPos][cPos]);
			textMessage
					.setText(EditMenuOptionsIngsAmountFragment.amount[gPos][cPos]);
		
			return rowView;
		}

		private void openDB() {
			db = new DatabaseAdapter(context);
			db.open();
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
