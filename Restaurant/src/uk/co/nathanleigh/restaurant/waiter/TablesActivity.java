package uk.co.nathanleigh.restaurant.waiter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.menu.MenuActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

// table states
// green - table open, nobody at table
// yellow - table occupied, can not open table, can only close
// red - unsure yet

public class TablesActivity extends FragmentActivity implements OnItemClickListener {

	private static final String TAG = "TABLES_ACTIVITY";
	private DatabaseAdapter db;
	private Context context;
	private GridView gridView;
	private TableImageAdapter tableImageAdapter ;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tables);
		openDB();
		// Show the Up button in the action bar.
		setupActionBar();

		Log.i(TAG, "Create Restaurant Tables");
		
		//createTables();
		//db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER);
		//db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_ORDERS);
		//db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS);
		//db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_ORDERS_ADDONS);
		
		
		Log.i(TAG, "Set up grid Adapter");
		 gridView = (GridView) findViewById(R.id.gridview);
		 
		tableImageAdapter = new TableImageAdapter(this);
		gridView.setAdapter(tableImageAdapter);
		
		gridView.setOnItemClickListener(this);

	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Log.i(TAG, "Updating Table");
		// 
		position = position + 1;
		//DialogFragment 
		int tableState = getTableState(position);
		
		if (tableState == 0){ //Table closed, open options
			// open dialog fragment with table options
			/*
			TableDialogFragment tableDialog =
					new TableDialogFragment();

			Bundle bundle = new Bundle();
			bundle.putInt("tableId", position);
			//menuItemEditDialog.setArguments(bundle);
			Log.i(TAG, "Showing Table Dialog");
			tableDialog.show(getSupportFragmentManager(), TableDialogFragment.TAG);
			//TODO call update state on order completion
			*/
			int sessionId = getSessionId(position);
			Log.i(TAG, "SessionId " + sessionId + " tableId " + position);
			showTableDialog(sessionId, position);
			
			
		}
		if (tableState == 1){ //Table open, update state
			updateTableState(position);
		}
		
		
		gridView.invalidateViews();
		
		
	}
	
	private void showTableDialog(int sessionId, int tableId) {
	  //  mStackLevel++;

	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("tableDialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    DialogFragment newFragment = TableDialogFragment.newInstance(sessionId, tableId);
	    newFragment.show(ft, "tableDialog");
	}
	
	
		private void updateTableState(int position) {
			//TODO figure out why not updating
		int sessionId = 0;
		int tableNum = position;
		String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
		Cursor cTable = db.myDBQuery(DatabaseAdapter.
				DATABASE_TABLE_RESTAURANT_TABLES, where);
		
		int isOpen = cTable.getInt(cTable.getColumnIndexOrThrow
				(DatabaseAdapter.KEY_TABLEOPEN));
		int rowId = cTable.getInt(cTable.getColumnIndexOrThrow
				(DatabaseAdapter.KEY_ROWID));
		int tableOpen = 2;
		
		String dateTime = getCurrentDateTime();
		//TODO make table seesion waiter id
		int waiterId = 1;
		int sessionActive = 1;
		
		Log.i(TAG, " isOpen value " + isOpen);
		
		if(isOpen==1){ // start session
			
		sessionId = (int) db.insertRowRestaurnatTableSessions(tableNum, dateTime, 
				waiterId, sessionActive);
		Log.i(TAG, " created session, id = " + sessionId);
		
		
		}
		
		if(isOpen==1){
			tableOpen=0;
		}
		else{
			tableOpen=1;
		}
		
		Log.i(TAG, " Updating row id and table num " 
			+ rowId + " table Num "+ tableNum + "new isOpen value " + tableOpen);
		
		db.updateRowRestaurantTables(rowId, tableNum, tableOpen, sessionId);
		
		Toast.makeText(TablesActivity.this, "" + position,
				Toast.LENGTH_SHORT).show();
	}
		
	private int getTableState(int position) {
			
			int tableNum = position;
			String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
			Cursor cTable = db.myDBQuery(DatabaseAdapter.
					DATABASE_TABLE_RESTAURANT_TABLES, where);
			
			int isOpen = cTable.getInt(cTable.getColumnIndexOrThrow
					(DatabaseAdapter.KEY_TABLEOPEN));

			return isOpen;
		}
	
	private int getSessionId(int position) {
		
		int tableNum = position;
		String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
		Cursor cTable = db.myDBQuery(DatabaseAdapter.
				DATABASE_TABLE_RESTAURANT_TABLES, where);
		
		int sessionId = cTable.getInt(cTable.getColumnIndexOrThrow
				(DatabaseAdapter.KEY_TABLESESSIONID));
		
		return sessionId;
	}

/*
	private void createTables() {
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES);
		//TODO manager chooses table number
		int intialSessionId = 0;
		for (int tableNum = 1; tableNum<16; tableNum++){
			db.insertRowRestaurantTables(tableNum, 1, intialSessionId);
		}
		
	}
*/
	
	private String getCurrentDateTime(){
		  String time = "";
		  String timeFormat = "yyyy-MM-dd HH:mm:ss";
		 // Locale locale = Locale.ENGLISH
		  SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
		  sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		  Calendar c = Calendar.getInstance();
		  time = sdf.format(c.getTime());
		  Log.i(TAG, "TIME/DATE = " + time);
		 
		  return time;
		}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tables, menu);
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

	// //////////////////////////////////////////////////////////////////////

	private class TableImageAdapter extends BaseAdapter {
		private Context mContext;
		private DatabaseAdapter myDb;
		private static final String TAG = "TABLE_IMAGE_ADAPTE";
		
		public TableImageAdapter(Context c) {
			mContext = c;
			openDB();
		}

		public int getCount() {
			Cursor cTables = myDb.getAllRows(DatabaseAdapter.
					DATABASE_TABLE_RESTAURANT_TABLES);
			int count = cTables.getCount();
			cTables.close();
			return count;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {
			//Log.i(TAG, " getting view");
			View grid;
			position = position + 1;
			int isOpen;
			if (convertView == null) {
				grid = new View(mContext);
				LayoutInflater inflater = getLayoutInflater();
				grid = inflater.inflate(R.layout.table_grid, parent, false);
			} else {
				grid = (View) convertView;
			}
			
			ImageView imageView = (ImageView) grid
					.findViewById(R.id.ivTableStatus);
			TextView textView = (TextView) grid
					.findViewById(R.id.tvTableNumber);
			//Log.i(TAG, " got views");
			
			//Log.i(TAG, "getting cursor for table pressed");
			//Log.i(TAG, " position = " + position);
			String where = DatabaseAdapter.KEY_TABLENUM + "=" + position;
			Cursor cTable = myDb.myDBQuery(DatabaseAdapter.
					DATABASE_TABLE_RESTAURANT_TABLES, where);
			//Log.i(TAG, " got Cursor");
			isOpen = cTable.getInt(cTable.getColumnIndexOrThrow
					(DatabaseAdapter.KEY_TABLEOPEN));
			
		//	Log.i(TAG, " is open value " + isOpen);
			
			if (isOpen == 1){
				imageView.setImageResource(R.drawable.table_closed);
			}
			if (isOpen == 0){
				imageView.setImageResource(R.drawable.table_open);
			}
			if (isOpen == 2){
				imageView.setImageResource(R.drawable.table_active);
			}
			// imageView.setImageResource(R.drawable.table_open);
		//	imageView.setImageResource(R.drawable.table_closed);
			Log.i(TAG, " created table " + position + " on gridView");
			textView.setText(String.valueOf(position));
			cTable.close();
			return grid;

		}


		private void openDB() {
			myDb = new DatabaseAdapter(mContext);
			myDb.open();
		}

	}

	/////////////////////////////////////////////////////////
	
	public static class TableDialogFragment extends DialogFragment {

		private int tableId;
		private int sessionId;
		private RadioGroup rgTableOptions;
		private View rbViewCurrentOrder;
		private View rbViewOrderHistory;
		private View rbTakeOrders;
		private View rbCompleteOrder;
		private DatabaseAdapter db;
		private final static String TAG = "TABLE_DIALOGFRAGMENT";
		private Context context;

		int mNum;

		/**
		 * Create a new instance of MyDialogFragment, providing "num"
		 * as an argument.
		 */
		public static TableDialogFragment newInstance(int sessionIdArg, int tableNum) {
			TableDialogFragment tableDialog = new TableDialogFragment();
		
		    // Supply num input as an argument.
		    Bundle args = new Bundle();
		    args.putInt("tableNum", tableNum);
		    args.putInt("sessionId", sessionIdArg);
		    tableDialog.setArguments(args);
		
		    return tableDialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			Log.i(TAG, "creating Dialog");
			
			context = getActivity();

		    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_table, null);
		    
			//LayoutInflater inflater = getActivity().getLayoutInflater();
			//Bundle bundle = getArguments();
			//tableId = bundle.getInt("tableId");
		    tableId = getArguments().getInt("tableNum");
		    sessionId = getArguments().getInt("sessionId");
	
			//openDb();
			Log.i(TAG, "inflating layout");

			//View dialogView = inflater.inflate(R.layout.dialog_table,
				//	null);
			Log.i(TAG,"Got dialog View");
			rgTableOptions = (RadioGroup) view
					.findViewById(R.id.rgTableOptions);
			rbViewCurrentOrder = rgTableOptions.findViewById(R.id.rbViewCurrentOrder);
			rbViewOrderHistory = rgTableOptions.findViewById(R.id.rbViewOrderHistory);
			rbTakeOrders = rgTableOptions.findViewById(R.id.rbTakeOrders);
			rbCompleteOrder = rgTableOptions.findViewById(R.id.rbCompleteOrder);
			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog
			// layout
			builder.setView(view).setTitle("Table " + tableId + " Options");

			builder.setMessage(
					"Choose an option for this table")
					.setPositiveButton("Confirm",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									int checkedId = rgTableOptions
											.getCheckedRadioButtonId();

									Log.i(TAG, "Checked Id of rg = "
											+ checkedId);
									
									//TODO have a session for this tableId
									
									if (checkedId == rbTakeOrders.getId()) {
										//openMenu
										Intent intent = new Intent(getActivity(), MenuActivity.class);
										intent.putExtra("sessionId", sessionId);
										intent.putExtra("tableId", tableId);
										startActivity(intent);
									}
									if (checkedId == rbViewCurrentOrder.getId()) {
										//open current order 
										//TODO have a session for this tableId
										Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
										intent.putExtra("sessionId", sessionId);
										intent.putExtra("tableId", tableId);
										startActivity(intent);
									}
									if (checkedId == rbViewOrderHistory.getId()) {
										//open current order 
										//TODO have a session for this tableId
										Intent intent = new Intent(getActivity(), OrderCompletionActivity.class);
										intent.putExtra("sessionId", sessionId);
										intent.putExtra("tableId", tableId);
										
										startActivity(intent);
									}
									
									if (checkedId == rbCompleteOrder.getId()) {
										//openOrderCompletion
										Intent intent = new Intent(getActivity(), OrderCompletionActivity.class);
										intent.putExtra("sessionId", sessionId);
										intent.putExtra("tableId", tableId);
										startActivity(intent);
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
	
		private void openDb() {
			db = new DatabaseAdapter(getActivity());
			db.open();

		}
	
	}
	
	
}
