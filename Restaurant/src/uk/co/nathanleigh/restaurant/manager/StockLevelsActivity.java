package uk.co.nathanleigh.restaurant.manager;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StockLevelsActivity extends FragmentActivity implements
		OnItemClickListener {

	protected static final String TAG = "STOCK_LEVELS_ACTIVITY";
	public static final String EXTRAS_PAYLOAD_KEY = null;
	// View lvStockLevels;
	DatabaseAdapter db;

	private int rowId; // static??
	private int ingId;
	private String ingName;
	private String ingUnit;
	private int ingAmount;
	private int ingRecAmount;
	private int ingWarningAmount;
	private ListView lvStockLevels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_levels);
		// Show the Up button in the action bar.
		setupActionBar();
		openDb();
		lvStockLevels = (ListView) findViewById(R.id.lvStockLevels);
		populateListViewFromDB();

	}

	
	
	private void populateListViewFromDB() {

		Cursor cursor = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_STOCK_LEVELS);

		startManagingCursor(cursor);
		
		 StockLevelListAdapter stockLevelListAdapter = new
		  StockLevelListAdapter( this, cursor, 0);
		  
		  // userList.setItemsCanFocus(false);
		  
		  final ListView lvStockLevels = (ListView) findViewById(R.id.lvStockLevels);
		  lvStockLevels.setAdapter(stockLevelListAdapter);
		/* 
		// Define the on-click listener for the list items
					lvStockLevels.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// Get the cursor, positioned to the corresponding row in
							// the result set
							Cursor cursor = (Cursor) lvStockLevels
									.getItemAtPosition(position);

							Log.i(TAG, "POSITION CLICKED = " + position);
							Log.i(TAG, "ID CLICKED = " + id);
						}
					});

	
*/
	}

	private static class StockLevelListAdapter extends CursorAdapter {

		private LayoutInflater inflator;
		private Context context;
		private DatabaseAdapter myDB;
		private Cursor TempCursor;

		public static final String TAG = "STOCK_LEVELS";
		// Constant for identifying the dialog
		private static final int DIALOG_ALERT = 10;
		Button editButton;

		public StockLevelListAdapter(Context context, Cursor c, int flags) {
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

			TextView tvIngName = (TextView) view.findViewById(R.id.tvIngName);
			TextView tvIngUnit = (TextView) view.findViewById(R.id.tvIngUnit);
			ImageView ivStockAlert = (ImageView) view.findViewById(R.id.ivStockAlert);
			TextView tvIngAmount = (TextView) view
					.findViewById(R.id.tvIngCurrentAmount);
			TextView tvIngRecAmount = (TextView) view
					.findViewById(R.id.tvIngRecAmount);
			TextView tvIngWarningAmount = (TextView) view
					.findViewById(R.id.tvIngWarningAmount);

			// System.out.println("getting values from cursor");
			final int rowId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			final int ingId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			final String ingName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
			final String ingUnit = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));
			final int ingAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));
			final int ingRecAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_RECAMOUNT));
			final int ingWarningAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_WARNINGAMOUNT));
			
			ivStockAlert = setStockAlertWarining(ingAmount, ingRecAmount, 
					ingWarningAmount, ivStockAlert);
			
			tvIngName.setText(ingName);
			tvIngUnit.setText(ingUnit);
			tvIngAmount.setText("" + ingAmount);
			tvIngRecAmount.setText("" + ingRecAmount);
			tvIngWarningAmount.setText("" + ingWarningAmount);
			
			  editButton = (Button) view.findViewById(R.id.bEditAmounts);
			  
			  // editButton.setVisibility(ImageButton.VISIBLE); // need this
			
			  
			  editButton.setOnClickListener(new OnClickListener() {
			  
			  @Override public void onClick(View v) {
			  
			  StockLevelDialogFragment df = new StockLevelDialogFragment();
			  
			  Log.i(TAG, "STOCK LEVEL ING PRESSED"); Log.i(TAG, ("RowId = " +
			  rowId)); Log.i(TAG, ("ingId = " + ingId)); Log.i(TAG,
			  ("ingName = " + ingName)); Log.i(TAG, ("ingUnit = " + ingUnit));
			  Log.i(TAG, ("ingAmount = " + ingAmount)); Log.i(TAG,
			  ("ingRecAmount = " + ingRecAmount)); Log.i(TAG,
			  ("ingWarningAmount = " + ingWarningAmount)); Bundle args = new
			  Bundle(); args.putInt("rowId", rowId); args.putInt("ingId",
			  ingId); args.putString("ingName", ingName);
			  args.putString("ingUnit", ingUnit); args.putInt("ingAmount",
			  ingAmount); args.putInt("ingRecAmount", ingRecAmount);
			  args.putInt("ingWarningAmount", ingWarningAmount); Log.i(TAG,
			  "LOG TEST, set arguments for the df"); df.setArguments(args);
			  
			  // context.sh; // df.show();
			  
			  FragmentManager fm = ((FragmentActivity) context)
			  .getSupportFragmentManager();
			  
			 // showDialog(DIALOG_ALERT);
			  System.out.println("DF>SHOW CALLED"); //
			  StockLevelsActivity.showStockLevelDialog(df, context);
			  df.show(fm, ingName); // FragmentManager
			  cursor.requery();
			  notifyDataSetChanged();
			  //StockLevelsActivity.refreshList();
			  }
			  
			  });
			  
			 

		}

		private ImageView setStockAlertWarining(int ingAmount, int ingRecAmount,
				int ingWarningAmount, ImageView ivStockAlert) {
				
				if (ingAmount < ingWarningAmount){
					ivStockAlert.setImageResource(R.drawable.red_circle);
					return ivStockAlert;
				}
				if (ingAmount > ingRecAmount){
					ivStockAlert.setImageResource(R.drawable.green_circle);
					return ivStockAlert;
				}
				ivStockAlert.setImageResource(R.drawable.orange_circle);

			return ivStockAlert;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.item_view_stock_level, parent,
					false);
			bindView(v, context, cursor);
			return v;

		}

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
		getMenuInflater().inflate(R.menu.stock_levels, menu);
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

	private void openDb() {
		db = new DatabaseAdapter(this);
		db.open();
	}

	private void closeDB() {
		db.close();
	}

	public static void showStockLevelDialog(StockLevelDialogFragment df,
			Context context) {
		// df.show(, "stockLevels")

	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}

// Specify the columns we want to display in the result

/*
 
if (cursor == null) {
	//
} else {

	String[] from = new String[] { DatabaseAdapter.KEY_INGNAME,
			DatabaseAdapter.KEY_INGUNIT, DatabaseAdapter.KEY_INGAMOUNT,
			DatabaseAdapter.KEY_RECAMOUNT,
			DatabaseAdapter.KEY_WARNINGAMOUNT };

	// Specify the Corresponding layout elements where we want the
	// columns to go
	int[] to = new int[] { R.id.tvIngName, R.id.tvIngUnit,
			R.id.tvIngCurrentAmount, R.id.tvIngRecAmount,
			R.id.tvIngWarningAmount };

	// Create a simple cursor adapter for the definitions and apply them
	// to the ListView
	SimpleCursorAdapter stockLevels = new SimpleCursorAdapter(this,
			R.layout.item_view_stock_level, cursor, from, to);

	Log.i(TAG, "STOCK LEVEL LIST ADAPTER SETTTING");
	lvStockLevels.setAdapter(stockLevels);

	Log.i(TAG, "STOCK LEVEL LIST ADAPTER SET");

	// Define the on-click listener for the list items
	lvStockLevels.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			// Get the cursor, positioned to the corresponding row in
			// the result set
			Cursor cursor = (Cursor) lvStockLevels
					.getItemAtPosition(position);

			Log.i(TAG, "POSITION CLICKED = " + position);
			Log.i(TAG, "ID CLICKED = " + id);

			rowId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			ingId = (int) cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			ingName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
			ingUnit = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));
			ingAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));
			ingRecAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_RECAMOUNT));
			ingWarningAmount = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_WARNINGAMOUNT));

			StockLevelDialogFragment df = new StockLevelDialogFragment();

			Log.i(TAG, "STOCK LEVEL ING PRESSED");
			Log.i(TAG, ("RowId = " + rowId));
			Log.i(TAG, ("ingId = " + ingId));
			Log.i(TAG, ("ingName = " + ingName));
			Log.i(TAG, ("ingUnit = " + ingUnit));
			Log.i(TAG, ("ingAmount = " + ingAmount));
			Log.i(TAG, ("ingRecAmount = " + ingRecAmount));
			Log.i(TAG, ("ingWarningAmount = " + ingWarningAmount));
			Bundle args = new Bundle();
			args.putInt("rowId", rowId);
			args.putInt("ingId", ingId);
			args.putString("ingName", ingName);
			args.putString("ingUnit", ingUnit);
			args.putInt("ingAmount", ingAmount);
			args.putInt("ingRecAmount", ingRecAmount);
			args.putInt("ingWarningAmount", ingWarningAmount);
			Log.i(TAG, "LOG TEST, set arguments for the df");
			df.setArguments(args);

			// context.sh;
			// df.show();

			FragmentManager fm = getSupportFragmentManager();

			// showDialog(DIALOG_ALERT);
			System.out.println("DF>SHOW CALLED");
			// StockLevelsActivity.showStockLevelDialog(df, context);
			df.show(fm, ingName);
			// FragmentManager
		}
	});
	
	lvStockLevels.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {
	        final String item = (String) parent.getItemAtPosition(position);
	      
	        
			Log.i(TAG, "LOG TEST, set arguments for the df");

	      }

	    });
	    
	   
}
 */
