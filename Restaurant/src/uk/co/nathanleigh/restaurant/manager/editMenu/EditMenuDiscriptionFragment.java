package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
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

public class EditMenuDiscriptionFragment extends Fragment implements
		OnClickListener {

	private View rootView;
	private Button bBack;
	private Button bConfirm;
	private Button bUploadImage;
	private EditText etDescription;
	private DatabaseAdapter db;
	private String itemDesc;
	private String imageTitle;
	private String menuItemName;
	private String editType;
	private int menuItemId;

	public static String TAG = "EDIT_MENU_DESCRIPTION";

	public EditMenuDiscriptionFragment() {

	}

	@Override
	public void onCreate(Bundle onSavedInstanceState) {
		super.onCreate(onSavedInstanceState);
		openDb();
		Bundle bundle = getArguments();
		
		menuItemName = bundle.getString("menuItemName");
		Log.i("TAG", "menuItemName : " + menuItemName);
		editType = bundle.getString("editType");
		Log.i("TAG", "Edit Type, create or update : " + editType);
		menuItemId = bundle.getInt("menuItemId");
		Log.i("TAG", "menuItem Id is : " + menuItemId);
	

		if (editType.equals("update")){
			
			Log.i("TAG", "deleting temp menuItemdescription");

			db.deleteAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
			// get item description from real table
			String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
			Cursor c = db.myDBQuery(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_DESCRIPTION, where);
			Log.i("TAG", "	c.getCount " + 	c.getCount());
			// get first from cursor
			String desc = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_ITEMDESC));
			String image = c.getString(c
					.getColumnIndex(DatabaseAdapter.KEY_IMAGETITLE));
			Log.i("TAG", "	desc " + 	desc);
			Log.i("TAG", "	imge " + 	image);
			Log.i("TAG", "	inserting into temp db" );

			db.insertRowTempMenuItemDesc(image, desc);
			c.close();
			
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i("TAG", "getting views");

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_description, container, false);
		
		etDescription = (EditText) rootView.findViewById(R.id.etDescription);
		
		bBack = (Button) rootView.findViewById(R.id.bBack);
		bConfirm = (Button) rootView.findViewById(R.id.bConfirm);
		bUploadImage = (Button) rootView.findViewById(R.id.bUploadImage);
		Log.i("TAG", "setting listeners");

		bConfirm.setOnClickListener(this);
		bUploadImage.setOnClickListener(this);
		bBack.setOnClickListener(this);

		Log.i("TAG", "check if editType is update");

		
		
		fillDescAndImage();
		
		return rootView;
	}

	private void fillDescAndImage() {
		Log.i("TAG", "	fillDescAndImage" );

		Cursor c = db.getAllRows(DatabaseAdapter.
				DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
		Log.i("TAG", "	c.getCount()" + c.getCount() );
		if (c.getCount() != 0){
		String desc = c.getString(c
				.getColumnIndex(DatabaseAdapter.KEY_ITEMDESC));
		String image = c.getString(c
				.getColumnIndex(DatabaseAdapter.KEY_IMAGETITLE));
		etDescription.setText("" + desc);
		//TODO image
		
		}
		
		
		c.close();
	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.bBack: {
			Log.i(TAG, "Button back pressed");
			EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
					.findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);
	
			getFragmentManager()
					.beginTransaction()
					// getChildFragmentManager().beginTransaction()
					.replace(R.id.container, NameDiscIngFrag)
					.addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

			break;
		}
		
		case R.id.bConfirm: {
			Log.i(TAG, "Button confirm pressed");
			
			db.deleteAllRows(DatabaseAdapter.
					DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
			
			EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
					.findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);
			
			itemDesc = etDescription.getText().toString();
			imageTitle = ""; //TODO
			db.insertRowTempMenuItemDesc(imageTitle, itemDesc);
				
			getFragmentManager()
			.beginTransaction()
			// getChildFragmentManager().beginTransaction()
			.replace(R.id.container, NameDiscIngFrag)
			.addToBackStack(null)
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.commit();
			
			break;
		}
		
		case R.id.bUploadImage: {
			Log.i(TAG, "Button upload Image pressed");
			//TODO
			
			
			break;
		}

		}
	}
}
