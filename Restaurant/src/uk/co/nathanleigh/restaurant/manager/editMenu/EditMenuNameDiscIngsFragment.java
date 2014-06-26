package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import uk.co.nathanleigh.restaurant.menu.MenuFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditMenuNameDiscIngsFragment extends Fragment implements
		OnClickListener {

	public static final String TAG = "NAMEDESCINGS";
	private ViewGroup rootView;

	private static DatabaseAdapter db;
	public static int sectionId;

	public static String menuItemName = "";

	private static ViewGroup mContainerView;
	private static Activity context;
	private static EditText etMenuItemName;
	public static String editType;
	public static int menuItemId;
	public static String description;
	public static TextView tvItemDescription;
	public static String menuItemDesc;

	public EditMenuNameDiscIngsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		openDb();
		// deleteTempOrder();
		Bundle bundle = getArguments();

		menuItemId = bundle.getInt("menuItemId");
		Log.i(TAG, "menuItem Id is : " + menuItemId);
		sectionId = bundle.getInt("sectionId");
		Log.i(TAG, "section Id is : " + sectionId);
		editType = bundle.getString("editType");
		Log.i(TAG, "Edit Type, create or update : " + editType);
		menuItemName = bundle.getString("menuItemName");
		Log.i(TAG, "MenuItemNAme : " + menuItemName);

		// if create temp cursor will be empty at start
		// if update load menu item table into temp table
		if (editType.equals("update")) {
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor c = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_INGREDIENTS,
							where);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				int ingId = c.getInt(c
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				String ingName = c.getString(c
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
				Log.i(TAG, "ingUnit Test ggfd: ");

				Cursor cIngInfo = db.getRow(
						DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, ingId);
				Log.i(TAG, "cIngInfo count = " + cIngInfo.getCount());

				String ingUnit = cIngInfo.getString(cIngInfo
						.getColumnIndex(DatabaseAdapter.KEY_INGUNIT));

				int complexIng = cIngInfo.getInt(cIngInfo
						.getColumnIndex(DatabaseAdapter.KEY_INGCOMPLEX));
				Log.i(TAG, "ingUnit = " + ingUnit + " complex " + complexIng);

				cIngInfo.close();
				Log.i(TAG, "ingUnit Test gotd: ");

				db.insertRowTempMenuItemIng(ingId, ingName.toUpperCase(),
						ingUnit);
			}
			c.close();

			Log.i(TAG, "Filled temp menu item ing table for use in update");

			Log.i(TAG, "deleting temp menuItemdescription");
			db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
			// get item description from real table
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			c = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_DESCRIPTION, where);
			Log.i(TAG, "	c.getCount " + c.getCount());
			// get first from cursor
			String desc = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_ITEMDESC));
			String image = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_IMAGETITLE));
			Log.i(TAG, "	desc " + desc);
			Log.i(TAG, "	imge " + image);
			Log.i(TAG, "	inserting into temp db");

			db.insertRowTempMenuItemDesc(image, desc);
			c.close();

		}

	}

	private void deleteTempOrder() {
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

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_name_desc_ings, container, false);

		mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
		context = getActivity();

		description = getArguments().getString("desription");

		etMenuItemName = (EditText) rootView.findViewById(R.id.etMenuItemName);
		// if(editType.equals("update")){
		// Log.i(TAG, "set text OnCreateView");
		// etMenuItemName.setText("" + menuItemName);
		// }

		tvItemDescription = (TextView) rootView
				.findViewById(R.id.tvItemDescription);
		Button bAddIng = (Button) rootView.findViewById(R.id.bAddIng);
		Button bCancel = (Button) rootView.findViewById(R.id.bCancel);
		Button bNext = (Button) rootView.findViewById(R.id.bNext);
		bAddIng.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		bNext.setOnClickListener(this);

		// if(editType.equals("update")){
		// fillTempOrderDb();
		// }

		tvItemDescription.setOnClickListener(this);

		fillList();
		fillDesc();

		return rootView;

	}

	private void fillDesc() {
		Log.i(TAG, "	fillDescAndImage");

		Cursor c = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
		Log.i(TAG, "	c.getCount()" + c.getCount());
		if (c.getCount() != 0) {
			String desc = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_ITEMDESC));
			String image = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_IMAGETITLE));
			tvItemDescription.setText("" + desc);
			// TODO image

		}

		c.close();

	}

	@Override
	public void onResume() {
		super.onResume();

		if (editType.equals("update")) {
			Log.i(TAG, "set text OnResume");
			etMenuItemName.setText("" + menuItemName);
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		/*
		 * if (newIng != 0) { System.out.println("newIng value " + newIng); //
		 * addIng(newIng); fillList(); }
		 */

	}

	// TODO this needed
	/*
	 * public static void updateIngListView(int ingId, String ingName) {
	 * 
	 * System.out.println("insert ing in temp ing table"); Log.i(TAG, "ingId = "
	 * + ingId + " ingName " + ingName);
	 * 
	 * db.insertRowTempMenuItemIng(ingId, ingName.toUpperCase(), ingUnit);
	 * 
	 * }
	 */
	private static void fillList() {

		Cursor cursor = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		// cursor.moveToFirst();
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					int ingId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_INGID));
					String ingName = cursor.getString(cursor
							.getColumnIndex(DatabaseAdapter.KEY_INGNAME));

					Cursor cIngInfo = db.getRow(
							DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, ingId);
					Log.i(TAG, "cIngInfo count = " + cIngInfo.getCount());

					String ingUnit = cIngInfo.getString(cIngInfo
							.getColumnIndex(DatabaseAdapter.KEY_INGUNIT));

					int complexIng = cIngInfo.getInt(cIngInfo
							.getColumnIndex(DatabaseAdapter.KEY_INGCOMPLEX));
					Log.i(TAG, "ingUnit = " + ingUnit + " complex "
							+ complexIng);

					cIngInfo.close();
					addIngToList(ingId, ingName, complexIng, ingUnit);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();

	}

	private static void addIngToList(int argIngId, String argIngName,
			int complexIngArg, String ingUnitArg) {

		final ViewGroup newView = (ViewGroup) context.getLayoutInflater()
				.inflate(R.layout.item_view_ing, mContainerView, false);
		final int ingId = argIngId;
		final String ingUnit = ingUnitArg;
		final String ingName = argIngName;
		final int complexIng = complexIngArg;
		// Set the text in the new row to a random country.
		((TextView) newView.findViewById(R.id.ingName)).setText("" + ingName);

		String complexOrRaw = "";

		if (complexIng == 0) {
			complexOrRaw = "raw ing";
		}
		if (complexIng == 1) {
			complexOrRaw = "complex ing";
		}

		((TextView) newView.findViewById(R.id.tvIngUnit)).setText("" + ingUnit);
		((TextView) newView.findViewById(R.id.tvIngType)).setText(""
				+ complexOrRaw);

		final int ingID = ingId;

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
						System.out.println(ingID + " delete ing" + ingName);
						db.deleteRow(
								DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS,
								ingId);

						// If there are no rows remaining, show the empty view.
						// if (mContainerView.getChildCount() == 0) {
						// getView().findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
						// }
					}
				});

		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		mContainerView.addView(newView, 0);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bAddIng: {

			Log.i("TAG", "add Ing clicked");

			Fragment fragment = new EditMenuAddIngFragment();
			Bundle bundle = new Bundle();
			bundle.putString("ingType", EditMenuNameDiscIngsFragment.TAG);
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

		case R.id.bNext: {
			Log.i("TAG", "next button clicked");

			menuItemName = etMenuItemName.getText().toString();
			menuItemDesc = tvItemDescription.getText().toString();

			if (menuItemName.equals("")) {
				String message = "Please enter a Name for the Menu Item";
				Toast t = Toast.makeText(getActivity(), message,
						Toast.LENGTH_SHORT);
				t.show();
			} else {
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.container, new EditMenuOptionsFragment(),
								EditMenuOptionsFragment.TAG)
						.addToBackStack(null)
						.setTransition(
								FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.commit();
			}
		}
			break;
		case R.id.bCancel: {
			Log.i("TAG", "cancel button clicked");

			Intent intent = new Intent(getActivity(), EditMenuActivity.class);
	    	startActivity(intent);

		}
			break;

		case R.id.tvItemDescription: {
			Log.i("TAG", "DESCRIPTION   EXT VIEW CLICKED");
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			View promptView = layoutInflater.inflate(
					R.layout.dialog_description, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			// set prompts.xml to be the layout file of the alertdialog
			// builder
			alertDialogBuilder.setView(promptView);

			final EditText input = (EditText) promptView
					.findViewById(R.id.etDescription);

			input.setText("" + tvItemDescription.getText());

			// setup a dialog window
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int id) {

									// get user input and set it to result
									Log.i(TAG,
											"description is " + input.getText());
									String imageTitle = "NULL"; // TODO
									String itemDesc = "" + input.getText();

									if (itemDesc.equals("")) {
										itemDesc = "(No Description)";
									}
									tvItemDescription.setText(itemDesc);
									db.insertRowTempMenuItemDesc(imageTitle,
											itemDesc);

									// editTextMainScreen.setText(input.getText());
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int id) {

									dialog.cancel();

								}
							});

			// create an alert dialog
			AlertDialog alertD = alertDialogBuilder.create();
			alertD.show();
		}

			break;

		}

	}

}
