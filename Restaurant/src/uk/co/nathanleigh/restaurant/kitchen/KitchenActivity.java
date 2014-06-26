package uk.co.nathanleigh.restaurant.kitchen;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.annotation.TargetApi;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.ExpandableListView.OnGroupClickListener;

// class which has an expandable list, the parent/group list 
// items display the order number id and the the child views
// show the menu item names of what was in that order
public class KitchenActivity extends  ExpandableListActivity   {

	
	private static final String TAG = "KitchenActivity";
	//private ListAdapter listAdapter;
	DatabaseAdapter db;
	private Cursor groupsCursor;
	private ExpandableListAdapter expListAdapter;
	private ExpandableListView list;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kitchen); 
		openDB();
		//context = this;
		// fill the expandable list with values
		fillListData();
		// Show the Up button in the action bar.
		setupActionBar();
		context = this;
		
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
			Log.d("timer", "timer ");
			Log.i(TAG, "timer test");
			Intent intent = new Intent(context, KitchenActivity.class);
			startActivity(intent);
			}
			}, 100000, 100000);

		
	}
	
	
	private void fillListData() {

		// get all the rows for the kitchen table
		Cursor cursor = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_ORDERS);
		//Cursor cursor = db.getAllRowsKitchen();

		startManagingCursor(cursor);
		// set up the new adapter
		
		//listAdapter = new KitchenOrderListAdapter(this, cursor, 0);
		
		// get all the rows for the kitchen table
		//groupsCursor = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_ORDER_DETAILS);
		groupsCursor = db.getAllRowsKitchen();

		startManagingCursor(groupsCursor);

		// TODO add addon name list to db to use in simple cursor
		
		// set up the new adapter
		expListAdapter = new KitchenExpandableListAdapter(
				
				// the cursor containing the groups and the context
				groupsCursor, this,
				// layout for groups, uses layout from android
				//android.R.layout.simple_expandable_list_item_1,
				R.layout.item_view_kitchen_order_details,
				// layout for children
				R.layout.item_view_kitchen_order,
				// group from, the key row id will aquired
				new String[] { DatabaseAdapter.KEY_ROWID, 
						DatabaseAdapter.KEY_TABLENUM, 
						DatabaseAdapter.KEY_DATETIME, 
						DatabaseAdapter.KEY_ORDERCOMMENTS },
				// groups to, will be placed in a textview
				new int[] { R.id.tvOrderId, R.id.tvTableNum, R.id.tvTime, R.id.tvOrderComments },
				// children from
				new String[] { DatabaseAdapter.KEY_ROWID,
						DatabaseAdapter.KEY_ITEMNAME,
						DatabaseAdapter.KEY_OPTIONNAME,
						DatabaseAdapter.KEY_ADDONNAMELIST
						},
				// children to
				new int[] {R.id.tvOrderId, R.id.tvItemName,
						R.id.tvOption, R.id.tvAddons
						
				}
		);

		// set up the expandable list adapter
		
		//lvOrders.setListAdapter(expListAdapter);
		
		  //final ListView lvOrders = (ListView) findViewById(R.id.lvKitchen);

		// set up the expandable list adapter
		//setListAdapter(listAdapter);
		Log.i(TAG, "setting list adapter");
		  setListAdapter(expListAdapter);
		//lvOrders.setAdapter(listAdapter);
		  list = (ExpandableListView) 
					findViewById(android.R.id.list);
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



	
	public class KitchenExpandableListAdapter extends SimpleCursorTreeAdapter {

		DatabaseAdapter db;
		Context context;

		
		public KitchenExpandableListAdapter(Cursor cursor, Context context,
				int groupLayout, int childLayout, String[] groupFrom,
				int[] groupTo, String[] childrenFrom, int[] childrenTo) {
			
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout,
					childrenFrom, childrenTo);
			
			this.context = context;

		}

		// returns cursor with subitems for given group cursor
		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {

			openDB();

			// gets the unique order Id for the group, from the kitchen table
			int groupId = groupCursor.getInt(groupCursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

			// want to search the orders table for rows where the orderID matches
			String where = DatabaseAdapter.KEY_ORDERGROUPID + "=" + groupId;
			// If the orderId matches then add to the cursor
			String table = DatabaseAdapter.DATABASE_TABLE_ORDERS;
			// cursor will contain all the children of the group
			// all will have the same orderId value
			Cursor c = db.myDBQuery(table, where);

			closeDB();

			return c;

		}

		private void openDB() {
			db = new DatabaseAdapter(context);
			db.open();
		}

		private void closeDB() {
			db.close();
		}

	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	private void openDB() {
		db = new DatabaseAdapter(this);
		db.open();
	}

	private void closeDB() {
		db.close();
	}

	
	 // Set up the {@link android.app.ActionBar}, if the API is available.
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kitchen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
}

/*
 * 
 * private static class KitchenOrderListAdapter extends CursorAdapter {

		private LayoutInflater inflator;
		private Context context;
		private DatabaseAdapter myDB;
		private Cursor TempCursor;

		public static final String TAG = "KITCHEN_LIST_ADAPTER";
		// Constant for identifying the dialog
		private static final int DIALOG_ALERT = 10;
		Button orderCompleteButton;

		public KitchenOrderListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			inflator = LayoutInflater.from(context);
			this.context = context;
		}

		private void openDB() {
			myDB = new DatabaseAdapter(context);
			myDB.open();
		}

		private void closeDB() {
			myDB.close();
		}

		@Override
		public void bindView(View view, final Context context, final Cursor cursor) {
			openDB();
			TextView tvOrderId = (TextView) view.findViewById(R.id.tvOrderId);
			TextView tvTableNum = (TextView) view.findViewById(R.id.tvTableNum);
			TextView tvTime = (TextView) view
					.findViewById(R.id.tvTime);
			TextView tvMenuItemName = (TextView) view
					.findViewById(R.id.tvMenuItemName);
			TextView tvOption = (TextView) view
					.findViewById(R.id.tvOption);
			TextView tvAddonsList = (TextView) view
					.findViewById(R.id.tvAddonList);
			TextView tvOrderComments = (TextView) view
					.findViewById(R.id.tvOrderComments);
			
			 System.out.println("getting values from cursor");
			final int rowId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			final int orderId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			
			final String menuItemName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMNAME));
			
			final String optionName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
			final String orderComments = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ORDERCOMMENTS));
			
			
			String addonsList = getListOfAddons(orderId);
			
			Cursor cOrderDetails = myDB.getRow(DatabaseAdapter.
					DATABASE_TABLE_ORDER_DETAILS, orderId);
			

	
			final String time = cOrderDetails.getString(cOrderDetails
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_DATETIME));
			final int tableNum = cOrderDetails.getInt(cOrderDetails
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_TABLENUM));
			
			//format times string
			 //System.out.println("Setting textViews");
			tvOrderId.setText("" + rowId);
			
			tvMenuItemName.setText(menuItemName);
			tvOption.setText(optionName);
			tvOrderComments.setText(orderComments);

			tvTableNum.setText("" + tableNum);
			tvTime.setText(time);
			tvAddonsList.setText(addonsList);
		
			
			 
			 // set up button to show order made and alert waiter
			
			//  orderCompleteButton = (Button) view.findViewById(R.id.bOrderComplete);
			  
			  // editButton.setVisibility(ImageButton.VISIBLE); // need this
			
			  
		//	  orderCompleteButton.setOnClickListener(new OnClickListener() {
			  
			//  @Override public void onClick(View v) {
			  
			 
			//  }
			  
			 // });
			  
			

		}

		private String getListOfAddons(int orderId) {
			
			String where = DatabaseAdapter.KEY_ORDERID + "=" + orderId; 
					String addonList = "";
					//Log.i(TAG, "Getting Addons cursor");
					Cursor cAddons = myDB.myDBQuery(DatabaseAdapter.
							DATABASE_TABLE_ORDERS_ADDONS, where);
					if(cAddons.getCount() == 0){
						return "No addons";
					}
					//Log.i(TAG, "Taversing though cAddons getting addonsList on " +
					//		"Cursor with orderId" + orderId);
					
					for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons.moveToNext()) {
						
						final String addonName = cAddons.getString(cAddons
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
					
						addonList += ", " + addonName;
					}
					
					cAddons.close();
			return addonList;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.item_view_kitchen_order, parent,
					false);
			bindView(v, context, cursor);
			return v;

		}

	}
	
 */

/*public static class KitchenActivity extends ListFragment{
    boolean mDualPane;
    int mCurCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (mDualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(mCurCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    
     // Helper function to show the details of a selected item, either by
     // displaying a fragment in-place in the current UI, or starting a
     // whole new activity in which it is displayed.
     
    void showDetails(int index) {
        mCurCheckPosition = index;

        if (mDualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = DetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.details, details);
                } else {
                    ft.replace(R.id.a_item, details);
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
	
	
}

*/

/*
public class KitchenActivity extends ExpandableListActivity {

	private Cursor groupsCursor; // cursor for list of groups (list top nodes)
	private ExpandableListAdapter expListAdapter;
	DatabaseAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kitchen);
		openDB();

		// fill the expandable list with values
		fillExpListData();
		// Show the Up button in the action bar.
		setupActionBar();
	}

	@SuppressWarnings("deprecation")
	private void fillExpListData() {

		// get all the rows for the kitchen table
		groupsCursor = db.getAllRows(DatabaseAdapter.DATABASE_TABLE_ORDER_DETAILS);

		startManagingCursor(groupsCursor);

		// set up the new adapter
		expListAdapter = new KitchenExpandableListAdapter(
				
				// the cursor containing the groups and the context
				groupsCursor, this,
				// layout for groups, uses layout from android
				android.R.layout.simple_expandable_list_item_1,
				// layout for children
				android.R.layout.simple_expandable_list_item_1,
				// group from, the key row id will aquired
				new String[] { DatabaseAdapter.KEY_ROWID },
				// groups to, will be placed in a textview
				new int[] { android.R.id.text1 },
				// children from
				new String[] { DatabaseAdapter.KEY_NAME },
				// children to
				new int[] { android.R.id.text1 }

		);

		// set up the expandable list adapter
		setListAdapter(expListAdapter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	private void openDB() {
		db = new DatabaseAdapter(this);
		db.open();
	}

	private void closeDB() {
		db.close();
	}

	
	 // Set up the {@link android.app.ActionBar}, if the API is available.
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kitchen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
*/
