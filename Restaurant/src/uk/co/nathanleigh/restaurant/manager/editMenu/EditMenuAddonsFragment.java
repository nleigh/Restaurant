package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class EditMenuAddonsFragment extends Fragment implements
			OnClickListener {

		private static ViewGroup mContainerView;
		private static Activity context;
		private ViewGroup rootView;
		private static DatabaseAdapter db;
		private static int newAddon = 0;
		private static String addonName;
		public static final String TAG = "ADDONS";
		private EditText etAddonName;
		private static String editType;
		private static int menuItemId;
		
		public EditMenuAddonsFragment() {

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			 openDb();
			 
			 editType = getEditType();
			 menuItemId = getMenuItemId();
			 
			// db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
				
				//fill TEMP OPTIONS IF UPDATE
				if (editType.equals("update")){
					
					db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
					String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
					Cursor c = db.myDBQuery(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);
					for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
						
						String addonName = c.getString(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
						int ingId = c.getInt(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
						String ingName = c.getString(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
						
						Cursor cIngInfo = db.getRow(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, ingId);
						Log.i(TAG, "cIngInfo count = " + cIngInfo.getCount());

						String ingUnit = cIngInfo.getString(cIngInfo
								.getColumnIndex(DatabaseAdapter.KEY_INGUNIT));

						int complexIng = cIngInfo.getInt(cIngInfo
								.getColumnIndex(DatabaseAdapter.KEY_INGCOMPLEX));
						Log.i(TAG, "ingUnit = " + ingUnit + " complex " + complexIng);

					
						db.insertRowTempMenuItemAddon(addonName, ingId, ingName, ingUnit);
					}
					c.close();
					Log.i(TAG, "Filled temp menu item addon table for use in update");
				}
				
			}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// Inflate the layout containing a title and body text.
			rootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_edit_menu_addons, container, false);

			mContainerView = (ViewGroup) rootView.findViewById(R.id.container);
			context = getActivity();
			
			Button bAddOption = (Button) rootView.findViewById(R.id.bAddAddon);
			Button bBack = (Button) rootView.findViewById(R.id.bBack);
			Button bNext = (Button) rootView.findViewById(R.id.bNext);
			bAddOption.setOnClickListener(this);
			bBack.setOnClickListener(this);
			bNext.setOnClickListener(this);
			etAddonName = (EditText) rootView.findViewById(R.id.etAddonName);
			etAddonName.requestFocus();
			
			fillList();
			
			return rootView;

		}
		
		@Override
		public void onStart() {
			super.onStart();
			//db.open();
			/*if (newAddon != 0) {
				System.out.println("newAddon Ing value " + newAddon);
				// addIng(newIng);
				fillList();
			}
			// During startup, check if there are arguments passed to the fragment.
			// onStart is a good place to do this because the layout has already
			// been
			// applied to the fragment at this point so we can safely call the
			// method
			// below that sets the article text.
			
			 */
			
		}

	

		private static void addAddon(final int rowId, final String addonName) {
			
			
			final ViewGroup newView = (ViewGroup) context.getLayoutInflater()
					.inflate(R.layout.item_view_option, mContainerView, false);
			
			System.out.println("adding to list the addon Name "+ addonName);
			
			((TextView) newView.findViewById(R.id.tvOptionName)).setText(addonName );
			
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
							System.out.println(rowId + " delete addonName" + addonName);
							db.deleteRow(
									DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS,
									rowId);

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
			case R.id.bAddAddon: {
				
				System.out.println("Add Option button press");
				addonName =  etAddonName.getText().toString();
				
				if (addonName.equals("")) {
					String message = "Please enter a name for the addon to add it to the menu item";
					Toast t = Toast.makeText(getActivity(), message,
							Toast.LENGTH_SHORT);
					t.show();
				}
				else{
				Fragment fragment = new EditMenuAddIngFragment();
				 Bundle bundle = new Bundle();
		         bundle.putString("ingType", EditMenuAddonsFragment.TAG);
		         
		         fragment.setArguments(bundle);
		         
		         getFragmentManager()
					.beginTransaction()
					.replace(R.id.container, fragment, "ingAdd")
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();
				}
					}
				break;
			case R.id.bNext: {
				System.out.println("next button press");
				Fragment fragment = new EditMenuOptionsIngsAmountFragment();
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.container,  fragment, EditMenuOptionsIngsAmountFragment.TAG)
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
			
			}
				break;
			case R.id.bBack: {
				System.out.println("back button press");
				EditMenuOptionsFragment optionsFrag = (EditMenuOptionsFragment)
		                getFragmentManager().findFragmentByTag(EditMenuOptionsFragment.TAG);
				
				getFragmentManager()
				.beginTransaction()
				.replace(R.id.container, optionsFrag)
				.addToBackStack(null)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();
			}
				break;
			}
		}
		


		private static void fillList() {
			Cursor cursor = db
					.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					do {
						int rowId = cursor.getInt(cursor
								.getColumnIndex(DatabaseAdapter.KEY_ROWID));
						 String addonName =
						 cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ADDONNAME));
						//TODO check if addon is already in list
						 
						 addAddon(rowId, addonName);
					} while (cursor.moveToNext());
				}
			}
			cursor.close();

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
		// called in complex frag
		public static void updateAddonTable(int ingId, String ingName, String ingUnit) {
			System.out.println("uupdate addon table " + ingId);
		
			newAddon = ingId;
			System.out.println("ADDON ING NAME TEST = " + ingName);
			db.insertRowTempMenuItemAddon(addonName, ingId, ingName, ingUnit);
			
		}

}
