package uk.co.nathanleigh.restaurant.menu;

import java.util.ArrayList;
import java.util.List;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuActivity;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuNameDiscIngsFragment;
import uk.co.nathanleigh.restaurant.menu.MenuFragment.SectionDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MenuSectionsFragment extends Fragment implements OnClickListener {

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static String ARG_SECTIONID = "sectionId";
	public static String ARG_MENUTYPE = "menuType";
	public static String ARG_SESSIONID = "sessionId";
	public static String ARG_TABLEID = "tableId";
	public static String ARG_POSITION = "position";
	
	public static String TAG = "section";
	public static String clickedMenuItemName;
	public static int clickedMenuItemId;

	private View view;

	// creates a list of menuItems
	private List<MenuItem> menuItemsList = new ArrayList<MenuItem>();

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int sectionPage;
	private int sectionId;
	 private int mPageNumber;
	public int tableId;
	public int sessionId;
	public String menuType;
	private DatabaseAdapter db;

	public static MenuSectionsFragment create(int pageNumber,
			int argSectionId, String argMenuType, int argSessionId, int argTableId) {
		MenuSectionsFragment fragment = new MenuSectionsFragment();
		Log.i(TAG, "menuType = " + argMenuType);
		Bundle args = new Bundle();
		args.putInt(ARG_POSITION,  pageNumber);
		args.putInt(ARG_SECTIONID, argSectionId);
		args.putString(ARG_MENUTYPE, argMenuType);
		args.putInt(ARG_SESSIONID, argSessionId);
		args.putInt(ARG_TABLEID, argTableId);
		fragment.setArguments(args);
		Log.i(TAG, "set args");
		return fragment;
	}

	public MenuSectionsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();
		sectionPage = (getArguments().getInt(ARG_PAGE)) + 1;
		sectionId = getArguments().getInt(ARG_SECTIONID);
		menuType = getArguments().getString(ARG_MENUTYPE);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		
		Log.i(TAG, "sectionPage = " + sectionPage);
		Log.i(TAG, "sectionId = " + sectionId);
		Log.i(TAG, "menuType got arg!!! = " + menuType);
		sessionId = getArguments().getInt(ARG_SESSIONID);
		tableId = getArguments().getInt(ARG_TABLEID);
		Log.i(TAG, "got args");
		
		 //populate the list with menu items
		//populateSectionList();

	}//TODO section Name not correxct

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_section, container, false);

		view = rootView;

		// populate the list with menu items
		populateSectionList();

		// populate the list view with item in the list
		populateListView();

		registerClickCallBack();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		//populateSectionList();

		//populateListView();
		//registerClickCallBack();

	}

	@Override
	public void onStart() {
		super.onStart();
		//populateSectionList();

		//populateListView();
		//registerClickCallBack();

	}
	
	private void populateSectionList() {

		int sectionId = getSectionId();
		
		Log.i(TAG, "sectionId to populate sectionList = " + sectionId);
		
		int menuItemId = -1;
		String sectionName = null, menuItemName = null;

		
		
		String where = DatabaseAdapter.KEY_SECTIONID + "=" + sectionId;
		Cursor c = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS, where);

		System.out.println("SectionId in query" + sectionId);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			sectionId = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONID));
			sectionName = c.getString(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONNAME));
			menuItemId = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_MENUITEMID));
			menuItemName = c.getString(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMNAME));
		
			menuItemsList.add(new MenuItem(sectionId, sectionName, menuItemId,
					menuItemName));
			// System.out.println("ADDED ITEM T LIST");
		}
		c.close();
		Log.i(TAG, "Populated Sections");
	}



	public int getPageNumber() {
        return mPageNumber;
    }
	
	public int getSectionPage() {
		return sectionPage;
	}

	public int getSectionId() {
		return sectionId;
	}


	public void populateListView() {

		ArrayAdapter<MenuItem> adapter = new MenuListAdapter(getActivity(),
				R.layout.item_view_menu, menuItemsList);

		ListView list = (ListView) view.findViewById(R.id.lvMenuItems);
		list.setAdapter(adapter);

	}

	@Override
	public void onClick(View v) {

		
	}

	private void registerClickCallBack() {
		ListView list = (ListView) view.findViewById(R.id.lvMenuItems);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			// gets the position id of the item clicked in the list
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {

				// returns the MenuItem that was clicked
				MenuItem clickedMenuItem = menuItemsList.get(position);
				clickedMenuItemName = clickedMenuItem.getMenuItemName();
				clickedMenuItemId = clickedMenuItem.getMenuItemId();

				Log.i(TAG, "Clicked MenuItem");
				Log.i(TAG, "menuType "+ menuType);

				if (menuType.equals("view")) {

					Log.i(TAG, "Creating menu order fragment");
					//TODO right args?
					MenuItemOrderFragment fragment = MenuItemOrderFragment
							.create(clickedMenuItemId, clickedMenuItemName, 
									sessionId, tableId);

					Log.i(TAG, "Created menu order fragment");

					
					getFragmentManager()
							.beginTransaction()
							// getChildFragmentManager().beginTransaction()
							.replace(R.id.container, fragment,
									MenuItemOrderFragment.TAG)
							.addToBackStack(MenuFragment.TAG)

							.setTransition(
									FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
							.commit();
					
					Log.i(TAG, "moved to menu order fragment");

				}

				if (menuType.equals("edit")) {
					// dialog box, edit or delete
					MenuItemEditDialogFragment menuItemEditDialog = new MenuItemEditDialogFragment();

					Bundle bundle = new Bundle();
					Log.i(TAG, "SectionId" + sectionId);

					bundle.putInt("pageNumber", getPageNumber());
					Log.i(TAG, "pageNumber" + getPageNumber());
					
					bundle.putInt("sectionId", sectionId);
					menuItemEditDialog.setArguments(bundle);

					menuItemEditDialog.show(getFragmentManager(),
							MenuItemEditDialogFragment.TAG);

				}

			}

		});

	}

	private class MenuListAdapter extends ArrayAdapter<MenuItem> {

		private Context context;
		private List<MenuItem> menuItems;

		public MenuListAdapter(Context context, int textViewResourceId,
				List<MenuItem> menuItems) {
			super(context, textViewResourceId, menuItems);
			this.context = context;
			this.menuItems = menuItems;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			// make sure we have a view to work with, may have been null
			if (itemView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				itemView = inflater.inflate(R.layout.item_view_menu, null);

			}

			MenuItem currentMenuItem = menuItems.get(position);

			TextView textView = (TextView) itemView
					.findViewById(R.id.menuItemName);
			textView.setText(currentMenuItem.getMenuItemName());
			return itemView;

		}

	}

	public static class MenuItemEditDialogFragment extends DialogFragment {

		private int sectionId;
		private int mPageNumber;
		private RadioGroup rgEditChoice;
		private View rbUpdate;
		private View rbDelete;
		private View rbDuplicate;
		private DatabaseAdapter db;
		private final static String TAG = "EDITMENUITEM_DIALOGFRAGMENT";
		private int menuItemId;
		private String menuItemName;
		private int duplicateMenuItemId;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			LayoutInflater inflater = getActivity().getLayoutInflater();
			Bundle bundle = getArguments();
			sectionId = bundle.getInt("sectionId");
			mPageNumber = bundle.getInt("pageNumber");
			 
			Log.i(TAG, "SectionId " + sectionId);

			Log.i(TAG, "pageNumber " + mPageNumber);
			

			openDb();

			menuItemName = MenuSectionsFragment.clickedMenuItemName;
			menuItemId = MenuSectionsFragment.clickedMenuItemId;

			View dialogView = inflater.inflate(R.layout.dialog_edit_menu_item,
					null);

			rgEditChoice = (RadioGroup) dialogView
					.findViewById(R.id.rgEditMenuItem);
			rbUpdate = rgEditChoice.findViewById(R.id.rbUpdate);
			rbDelete = rgEditChoice.findViewById(R.id.rbDelete);
			rbDuplicate = rgEditChoice.findViewById(R.id.rbDuplicate);
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog
			// layout
			builder.setView(dialogView).setTitle("Edit Menu Item");

			builder.setMessage(
					"Choose if you want to update or delete the Menu Item: "
							+ menuItemName)
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									int checkedId = rgEditChoice
											.getCheckedRadioButtonId();

									Log.i(TAG, "Checked Id of rg = "
											+ checkedId);
									// if update,
									if (checkedId == rbUpdate.getId()) {
										openMenuItemEditor();

									}
									// if duplicate,
									if (checkedId == rbDuplicate.getId()) {

										duplicateMenuItem();
										Intent intent = new Intent(getActivity(), EditMenuActivity.class);
								    	startActivity(intent);
									}
									// if delete,
									if (checkedId == rbDelete.getId()) {

										deleteMenuItem();

									}
								
								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// User cancelled the dialog
								}
							});
			//db.close();
			// Create the AlertDialog object and return it
			return builder.create();
		}
		
		public int getPageNumber() {
	        return mPageNumber;
	    }
		
		
		public int getSectionId() {
			return sectionId;
		}

		private void openMenuItemEditor() {

			Fragment editMenuNameDiscIngsFragment = new EditMenuNameDiscIngsFragment();

			Bundle args = new Bundle();
			args.putInt("sectionId", sectionId);
			args.putInt("menuItemId", menuItemId);
			args.putString("editType", "update");
			args.putString("menuItemName", menuItemName);

			editMenuNameDiscIngsFragment.setArguments(args);

			getFragmentManager()
					.beginTransaction()
					// getChildFragmentManager().beginTransaction()
					.replace(R.id.container, editMenuNameDiscIngsFragment,
							EditMenuNameDiscIngsFragment.TAG)
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

		}

		private void deleteMenuItem() {
			// get row Id of table section menu items
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor c = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS, where);
			int rowId = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			Log.i(TAG,
					" Deleting row from section menu items tabel");
			db.deleteRow(DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS,
					rowId);
			Log.i(TAG,
					" Deleting row from menu items tabel");
			db.deleteRow(DatabaseAdapter.DATABASE_TABLE_MENU_ITEMS, menuItemId);
			c.close();
			//TODO
			
			
			
			// ///////////////////////////////////////////////////////////////////////////
			// cascade delete ??
			// /////////////////////////////////////////////////////
			// ////////////////////////////////////////////////////////////////////

			String msg = "Deleted Menu Item. Id: " + menuItemId + ", Name: "
					+ menuItemName;
			Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
			toast.show();
			Log.i(TAG,
					" Deleted Menu Item form db table section menu items and menu items : Id: "
							+ menuItemId + ", Name: " + menuItemName);
		}

		private void duplicateMenuItem() {
			// get database info for the menu item id
			// insert this into the database
			Log.i(TAG, "--Duplicating menu Item in DB--");
			
			insertInMenuItemsTable();
			Log.i(TAG, "Duplicated in menuItem table");
			
			insertInMenuItemDescTable();
			Log.i(TAG, "Duplicated in menuItemDescription table");
			
			insertInMenuItemIngsTable();
			Log.i(TAG, "Duplicated in menuItemIngs table");

			insertInOptionsTable();
			Log.i(TAG, "Duplicated menu Item optionas table");

			insertInAddonsTable();
			Log.i(TAG, "Duplicated menu Item addons table");

			insertInOptionsIngsAmountTable();
			Log.i(TAG, "Duplicated menu Item optionings amout");

			insertInAddonsIngsPriceAmountTable();
			Log.i(TAG, "Duplicated menu Item addon ing price table");

			
			String msg = "Duplicated Menu Item. Id: " + menuItemId + ", Name: "
					+ menuItemName;
			Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
			toast.show();
			Log.i(TAG, " Duplicate Menu Item : Id: " + menuItemId + ", Name: "
					+ menuItemName);

		}

		

		private void insertInMenuItemsTable() {
			// duplicate menuItemTable
			Cursor cMenuItemTable = db.getRow(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEMS, menuItemId);

			String sectionName = cMenuItemTable.getString(cMenuItemTable
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONNAME));

			String itemDesc = cMenuItemTable.getString(cMenuItemTable
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMDESC));

			duplicateMenuItemId = (int) db.insertRowMenuItem(sectionId,
					sectionName, menuItemName, itemDesc);
			
			db.insertRowSectionMenuItems(sectionId, sectionName, duplicateMenuItemId, menuItemName);
			Log.i(TAG, "duplicated in menuItems table amd section menu items table");
			cMenuItemTable.close();
		}

		private void insertInMenuItemDescTable() {
			// TODO Auto-generated method stub
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cMenuItemDesc = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_DESCRIPTION,
							where);
			for (cMenuItemDesc.moveToFirst(); !cMenuItemDesc.isAfterLast(); cMenuItemDesc
					.moveToNext()) {

				String image = cMenuItemDesc.getString(cMenuItemDesc
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_IMAGETITLE));
				
				String description = cMenuItemDesc.getString(cMenuItemDesc
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMDESC));
				
				db.insertRowMenuItemDesc(duplicateMenuItemId,image, description);
			}
			Log.i(TAG, "duplicated in menuItemDesc table");

			cMenuItemDesc.close();
		}
		
		private void insertInMenuItemIngsTable() {
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cMenuItemIngs = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_INGREDIENTS,
							where);

			for (cMenuItemIngs.moveToFirst(); !cMenuItemIngs.isAfterLast(); cMenuItemIngs
					.moveToNext()) {

				String ingName = cMenuItemIngs.getString(cMenuItemIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				int ingId = cMenuItemIngs.getInt(cMenuItemIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));

				db.insertRowMenuItemIngs(duplicateMenuItemId, menuItemName,
						ingId, ingName);
			}
			cMenuItemIngs.close();
			Log.i(TAG, "duplicated in menuItemIngs table");

		}

	
		private void insertInOptionsTable() {
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cMenuItemOptions = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);

			for (cMenuItemOptions.moveToFirst(); !cMenuItemOptions
					.isAfterLast(); cMenuItemOptions.moveToNext()) {

				String optionName = cMenuItemOptions.getString(cMenuItemOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
				int optionPrice = cMenuItemOptions
						.getInt(cMenuItemOptions
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));

				db.insertRowMenuItemOptions(duplicateMenuItemId, menuItemName,
						optionName, optionPrice);
			}
			cMenuItemOptions.close();
			Log.i(TAG, "duplicated in menuItemOption table");
		}

		private void insertInAddonsTable() {
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cMenuItemAddons = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);

			for (cMenuItemAddons.moveToFirst(); !cMenuItemAddons.isAfterLast(); cMenuItemAddons
					.moveToNext()) {

				String addonName = cMenuItemAddons.getString(cMenuItemAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
				String ingName = cMenuItemAddons.getString(cMenuItemAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				int ingId = cMenuItemAddons.getInt(cMenuItemAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));

				db.insertRowMenuItemAddons(duplicateMenuItemId, menuItemName,
						addonName, ingId, ingName);
			}
			cMenuItemAddons.close();
			Log.i(TAG, "duplicated in menuItemAddons table");
		}

		
		
		private void insertInOptionsIngsAmountTable() {

			String ingName;
			int ingId;
			int optionId;
			String optionName;
			int ingAmount;
			
			// get the ing amounts cursor for the item that is being duplicated
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cIngAmountOptions = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
							where);
			
			//get the number count of ings for the menu item, used in loop
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cIngsMenuItem = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_INGREDIENTS,
							where);
			int numOfIngs = cIngsMenuItem.getCount();
			

			// get the option info for the recently inserted suplicate menu item
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + duplicateMenuItemId;
			Cursor cOptionsForDuplicate = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
		//	System.out.println("Cursor  options count = "
			//		+ cOptionsForDuplicate.getCount());

			int ingsAddedForOptionCounter = 0;
			// loops through for each option the ing amounts
			for (cIngAmountOptions.moveToFirst(); !cIngAmountOptions
					.isAfterLast(); cIngAmountOptions.moveToNext()) {
			
				// need to get option id 
				optionId = cOptionsForDuplicate.getInt(cOptionsForDuplicate
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

				optionName = cOptionsForDuplicate
						.getString(cOptionsForDuplicate
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				// now get the info from the cursor about the ing amount
				ingId = cIngAmountOptions.getInt(cIngAmountOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				ingName = cIngAmountOptions.getString(cIngAmountOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				ingAmount = Integer
						.parseInt(cIngAmountOptions.getString(cIngAmountOptions
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT)));

			
				// insert the duplicated information into the DB
				db.insertRowIngAmountForMenuItemOption(duplicateMenuItemId,
						menuItemName, optionId, optionName, ingId, ingName,
						ingAmount);

				// checks if all ings amounts for that option have been added
				// moves the option cursor to the next option
				ingsAddedForOptionCounter++;
				if (ingsAddedForOptionCounter == numOfIngs) {
					//System.out.println("moving c option to next");
					cOptionsForDuplicate.moveToNext();
					ingsAddedForOptionCounter = 0;
				}

				// System.out.println("---------------------------------------");
			}
			
			cIngsMenuItem.close();
			cIngAmountOptions.close();
			cOptionsForDuplicate.close();
			// System.out.println("================================");
			Log.i(TAG, "duplicated in menuItemOptionsAmounts table");

		}
		
		private void insertInAddonsIngsPriceAmountTable() {

			String addonName;
			String ingName;
			int ingId;
			int optionId;
			int addonAmount;
			int addonPrice;
			int addonId;
			String optionName;
			
			// get cursor of addon ing/price amounts for the menu item thats 
			// being duplicated
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor cAddonIngsAmountToBeDuplicated = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS, where);
			
			// get cursor of all menu item options for the duplictaed menu item
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + duplicateMenuItemId;
			Cursor cOptions = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
			
			// get the addons recently added for the duplicate menu item and count
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + duplicateMenuItemId;
			Cursor cAddons = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);
			int numOfAddons = cAddons.getCount();
			Log.i(TAG, " numOfAddons = " + numOfAddons);
			int addonsCounter = 0;
			
			for (cAddonIngsAmountToBeDuplicated.moveToFirst(); !cAddonIngsAmountToBeDuplicated.
					isAfterLast(); cAddonIngsAmountToBeDuplicated.moveToNext()) {
				
				// get the option info from using duplicat menu item id info
				optionId = cOptions.getInt(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				optionName = cOptions.getString(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

				// get addon info using duplicat menu item id cursor info
				addonId = cAddons.getInt(cAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				addonName = cAddons.getString(cAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

				// get ing info for the item that is to be duplicated
				ingId = cAddonIngsAmountToBeDuplicated.getInt(cAddonIngsAmountToBeDuplicated
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				ingName = cAddonIngsAmountToBeDuplicated.getString(cAddonIngsAmountToBeDuplicated
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				addonAmount = Integer
						.parseInt(cAddonIngsAmountToBeDuplicated.getString(cAddonIngsAmountToBeDuplicated
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT)));
				addonPrice = Integer
						.parseInt(cAddonIngsAmountToBeDuplicated.getString(cAddonIngsAmountToBeDuplicated
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE)));
				
				System.out.println("Inseting into MenuItemAddons Table");
				System.out.println("MenuItem Id " + menuItemId
						+ " MenuItemName " + menuItemName);
				System.out.println("Option Id " + optionId + " OptionName "
						+ optionName);
				System.out.println("Addon Id " + addonId + " AddonName "
						+ addonName);
				System.out.println(" Ing Id " + ingId + " ingName = " + ingName
						+ " addonAmount = " + addonAmount + " addonPrice = "
						+ addonPrice);
				
				
				db.insertRowAddonIngAmountPriceForMenuItemOption(duplicateMenuItemId,
						menuItemName, optionId, optionName, addonId, addonName,
						ingId, ingName, addonAmount, addonPrice);

				addonsCounter++;

				cAddons.moveToNext();
				if (addonsCounter == numOfAddons) {

					cAddons.moveToFirst();

					addonsCounter = 0;
					cOptions.moveToNext();
				}

				System.out.println("---------------------------------------");
			}
			cAddonIngsAmountToBeDuplicated.close();
			cAddons.close();
			cOptions.close();
			System.out.println("================================");

			Log.i(TAG, "duplicated in menuItemAddonsPriceAmounts table");
		}

		
		private void openDb() {
			db = new DatabaseAdapter(getActivity());
			db.open();

		}
	}
}
