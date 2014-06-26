package uk.co.nathanleigh.restaurant.menu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuActivity;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuAddIngFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuNameDiscIngsFragment;
import uk.co.nathanleigh.restaurant.waiter.CurrentOrderActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MenuFragment extends Fragment implements OnClickListener {
	MenuSectionsPagerAdapter menuSectionsPagerAdapter;

	// The {@link ViewPager} that will host the section contents.
	//not getting the correct sectionId find out why
	private ViewPager mViewPager;
	private DatabaseAdapter db;
	private static ViewGroup mContainerView;
	private static Activity context;

	public static final String TAG = "MENUFRAGMENT";
	private ViewGroup rootView;
	private String menuType;
	private int sectionId;
	private int currentSectionId;
	public int sessionId;
	public int tableId;

	public MenuFragment() {

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();

		menuType = bundle.getString("menuType");
		sessionId = bundle.getInt("sessionId");
		tableId = bundle.getInt("tableId");

		Log.i(TAG, " Table Id  " + tableId);
		Log.i(TAG, " Session Id  " + sessionId);

		openDb();
		Log.i(TAG, "MenuType view or edit :  " + menuType);
		// fillSections();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// if menu view or edit load appropriate layout
		if (menuType.equals("view")) {
			// Inflate the layout containing a title and body text.
			rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu,
					container, false);

			TextView tvSessionId = (TextView) rootView
					.findViewById(R.id.tvSessionId);
			TextView tvTableNum = (TextView) rootView
					.findViewById(R.id.tvTableNum);
			tvSessionId.setText("" + sessionId);
			tvTableNum.setText("" + tableId);

			// appears when vieving menu
			Button bViewOrder = (Button) rootView.findViewById(R.id.bViewOrder);
			bViewOrder.setOnClickListener(this);
		}

		if (menuType.equals("edit")) {
			// Inflate the layout containing a title and body text.
			rootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_edit_menu, container, false);

			// appears when editing menu
			Button bViewOrder = (Button) rootView.findViewById(R.id.bNewItem);
			bViewOrder.setOnClickListener(this);
			Button bNewSection = (Button) rootView
					.findViewById(R.id.bNewSection);
			bNewSection.setOnClickListener(this);
			Button bDeleteSection = (Button) rootView
					.findViewById(R.id.bDeleteSection);
			bDeleteSection.setOnClickListener(this);

		}

		mContainerView = (ViewGroup) rootView.findViewById(R.id.container);

		context = getActivity();

		setUpViewPagers();
		
		return rootView;

	}

	private void setUpViewPagers() {
		// Create the adapter that will return a fragment
		// for each menu section

		menuSectionsPagerAdapter = new MenuSectionsPagerAdapter(
				getFragmentManager(), context);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) rootView.findViewById(R.id.pager_menu);
		mViewPager.setAdapter(menuSectionsPagerAdapter);

	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bViewOrder: {
			Log.i(TAG, "View order button presed");

			Intent intent = new Intent(getActivity(),
					CurrentOrderActivity.class);
			intent.putExtra("sessionId", sessionId);
			intent.putExtra("tableId", tableId);
			startActivity(intent);
		}
			break;

		case R.id.bNewItem: {
			Log.i(TAG, "New Menu Item button presed");
		
			int testSectionId = mViewPager.getCurrentItem();
			
			sectionId = getSectionId(mViewPager.getCurrentItem());
		
			Log.i(TAG, "testsectionId = " + testSectionId);
			Log.i(TAG, "sectionId after getSectionId(vp.getcurrentItem) " +
					"= " + sectionId);
			
			Cursor cNumOfSections = db
					.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
			int numOfSections = cNumOfSections.getCount();
			cNumOfSections.close();

			if (numOfSections == 0) {
				String message = "Must create a section to add menu items to it";
				Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
				toast.show();
			} else {
				Fragment editMenuNameDiscIngsFragment = new EditMenuNameDiscIngsFragment();

				Bundle args = new Bundle();
				args.putInt("sectionId", sectionId);
				args.putString("editType", "create");

				editMenuNameDiscIngsFragment.setArguments(args);

				getFragmentManager()
						.beginTransaction()
						// getChildFragmentManager().beginTransaction()
						.replace(R.id.container, editMenuNameDiscIngsFragment,
								EditMenuNameDiscIngsFragment.TAG)
						.addToBackStack(null)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.commit();
			}
		}
			break;

		case R.id.bNewSection: {
			Log.i(TAG, "New Section button presed");

			//TODO crashes after attempt to create new setcyion
			 mViewPager.invalidate();
			
			SectionDialogFragment sectionDF = new SectionDialogFragment();
			Bundle bundle = new Bundle();

			bundle.putString("sectionEditType", "create");
			sectionDF.setArguments(bundle);

			sectionDF.show(getFragmentManager(), SectionDialogFragment.TAG);

			//TODO
			//menuSectionsPagerAdapter.notifyDataSetChanged();
			 
			// refersh menu

		}
			break;

		case R.id.bDeleteSection: {
			Log.i(TAG, "Delete Section button presed");

			// show a confirmation box
			//TODO delet section function
		}
			break;

		// need edit section name!
		}

	}


	private int getSectionId(int position) {
		Cursor c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
		int count = c.getCount();
		int index = 0;
		int sectionRowId = 0;

		do {

			if (index == position) {
				sectionRowId = c.getInt(c
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				System.out.println("SECTION ROW ID (getSectionId)= " + sectionId);
			}

			index++;
			c.moveToNext();
		} while (index < count);

		c.close();
		return sectionRowId;
	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();
	}

	@Override
	public void onStart() {
		super.onStart();

		openDb();
//TODO not loading
		//setUpViewPagers();
	}

	@Override
	public void onResume() {
		super.onResume();
		// mViewPager.
		openDb();
		//TODO not loading
		//setUpViewPagers();

		//db.close();

	}
	
	private class MenuSectionsPagerAdapter extends FragmentPagerAdapter {

		private DatabaseAdapter db;
		private Context context;

		public MenuSectionsPagerAdapter(FragmentManager fm, Context c) {
			super(fm);
			context = c;
			openDb();

		}

		// getItem is called to instantiate the fragment for the given page.
		@Override
		public Fragment getItem(int position) {

			// depending on the postion opens a fragment
			// for that menu section
			Cursor c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
			int count = c.getCount();
			int index = 0;

			do {

				if (index == position) {
					sectionId = c.getInt(c
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
					System.out.println("SECTION ID (getItem)= " + sectionId);
				}

				index++;
				c.moveToNext();
			} while (index < count);

			c.close();
			Log.i(TAG, "Create MenuSectionsFragment, menuType = " + menuType);
			Log.i(TAG, "Create MenuSectionsFragment, position = " + position);
			Log.i(TAG, "Create MenuSectionsFragment, sectionId = " + sectionId);
			Log.i(TAG, "Create MenuSectionsFragment, tableId = " + tableId);
			Log.i(TAG, "Create MenuSectionsFragment, sessionId = " + sessionId);
			return MenuSectionsFragment.create(position, sectionId, menuType,
					tableId, sessionId);
		}

		@Override
		public int getCount() {

			Cursor c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
			// System.out.println("got section cursor");
			int sectionCount = c.getCount();
			// System.out.println("section count = " + c.getCount());
			c.close();
			//Log.i(TAG, "sectionCount = " + sectionCount);

			return sectionCount;
			// return 5;

		}

		@Override
		public CharSequence getPageTitle(int position) {

			Cursor c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
			int count = c.getCount();
			int index = 0;
			String pageTitle = "No Section Name";

			do {

				if (index == position) {
					pageTitle = c
							.getString(c
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONNAME));
					// System.out.println("PAGE TITLE GOT = " + pageTitle);
				}

				index++;
				c.moveToNext();
			} while (index < count);

			c.close();
			return pageTitle;

		}
		
		public int getPageId(int position){

			Cursor c = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);
			int count = c.getCount();
			int index = 0;
			int pageId = 0;
			//String pageTitle = "No Section Name";

			do {

				if (index == position) {
					pageId = c
							.getInt(c
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONID));
					// System.out.println("PAGE TITLE GOT = " + pageTitle);
				}

				index++;
				c.moveToNext();
			} while (index < count);

			c.close();
			
			return pageId;

		}
		

		private void openDb() {
			// System.out.println("db = new");
			db = new DatabaseAdapter(context);
			// System.out.println("db.open");
			db.open();
			// System.out.println("db now open");
		}

	}

	
	public static class SectionDialogFragment extends DialogFragment {

		private int sectionId;
		private String sectionEditType;
		private DatabaseAdapter db;
		private EditText etSectionName;
		private final static String TAG = "SECTIONDF";

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = getActivity().getLayoutInflater();
			Bundle bundle = getArguments();

			sectionEditType = bundle.getString("sectionEditType");

			if (sectionEditType.equals("create")) {
				// fill this
			}
			if (sectionEditType.equals("edit")) {
				// fill this
			}
			if (sectionEditType.equals("delete")) {
				// fill this
			}

			sectionId = bundle.getInt("sectionId");
			Log.i(TAG, "Current sectionId dialog = " + sectionId);

			openDb();

			View dialogView = inflater.inflate(R.layout.dialog_new_section,
					null);
			// setDialogView(DialogView);

			etSectionName = (EditText) dialogView
					.findViewById(R.id.etSectionName);

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog
			// layout
			builder.setView(dialogView).setTitle(
					EditMenuAddIngFragment.ingNameClicked);

			builder.setMessage("Enter a name for the new Section")
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									// check to see it item already exsts

									// //long rowId =
									// db.insertRowIngredients(ingName,
									// ingUnit);
									// EditMenuNameDiscIngsFragment.updateIngListView((int)rowId,
									// ingName);

									String sectionName = etSectionName
											.getText().toString();
									long rowId = db
											.insertRowSections(sectionName);

									String msg = "Created new section. Id: "
											+ rowId + ", Name: " + sectionName;
									Toast toast = Toast.makeText(getActivity(),
											msg, Toast.LENGTH_LONG);
									toast.show();
									Log.i(TAG, " New Section created: Id: "
											+ rowId + ", Name: " + sectionName);
									Intent intent = new Intent(getActivity(), EditMenuActivity.class);
							    	startActivity(intent);
									
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
								}
							});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		private void openDb() {
			db = new DatabaseAdapter(getActivity());
			db.open();

		}
	}
	
	/*
	private void fillSections() {
		int id;
		String sectionName;

		// delete first foreign keys
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS);

		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);

		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_MENU_ITEMS);

		int itemId;
		String itemName;

		sectionName = "section1";

		id = (int) db.insertRowSections(sectionName);
		itemName = "menuItem1";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		// id = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
		// optionName, optionPrice)
		// db.in
		// db.insertRowMenuItemOptions(menuItemId, menuItemName, optionName,
		// optionPrice)

		// db.insertRowMenuItemAddons(menuItemId, menuItemName, addonName,
		// ingId, ingName)

		// /////////////////////////////////////
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);
		itemName = "menuItem2";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		// /////////////////////////////////////////
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);
		itemName = "menuItem3";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);

		// ///////////////////////////
		// ///////////////////////////

		sectionName = "section2";
		id = (int) db.insertRowSections(sectionName);
		itemName = "menuItem4";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);
		itemName = "menuItem5";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);
		itemName = "menuItem6";
		itemId = (int) db.insertRowMenuItem(id, sectionName, itemName,
				"itemDesc");
		db.insertRowSectionMenuItems(id, sectionName, itemId, itemName);

	}
*/

}
