package uk.co.nathanleigh.restaurant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.nathanleigh.restaurant.waiter.TablesActivity.TableDialogFragment;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerActivity extends FragmentActivity implements
		OnClickListener {
//TODO tableOpen wrong way round fix
	private static final String TAG = "CUSTOMER";
	private static final int TABLENUM = 1;
	private DatabaseAdapter db;
	private Context context;
	private TextView tvTableNumber;
	private TextView tvOpenClosed;
	private ImageButton ibOpenClosed;
	private int tableOpen;
	private int sessionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer);
		// Show the Up button in the action bar.
		setupActionBar();
		openDB();
		// set table num in a file on device
		Log.i(TAG, " Getting views");

		tvTableNumber = (TextView) findViewById(R.id.tvTableNumber);
		tvOpenClosed = (TextView) findViewById(R.id.tvOpenClosed);
		ibOpenClosed = (ImageButton) findViewById(R.id.ibOpenClosed);

		ibOpenClosed.setOnClickListener(this);

		tvTableNumber.setText("This is Table Number " + TABLENUM);
		// tableNum is 1
		Log.i(TAG, " Getting table state");
		tableOpen = getTableState(TABLENUM);
		Log.i(TAG, "  table open = " + tableOpen);

		// table 1 dosent exist
		if (tableOpen == 2) {
			tvOpenClosed.setText("No Tables Exist");
			ibOpenClosed.setImageResource(R.drawable.closed);

		}

		if (tableOpen == 1) {
			tvOpenClosed.setText("Click to open the table");
			ibOpenClosed.setImageResource(R.drawable.closed);

		}

		if (tableOpen == 0) {
			tvOpenClosed.setText("Click to choose table options");
			ibOpenClosed.setImageResource(R.drawable.open);

		}
		Log.i(TAG, " Finished creating");

	}

	private int getTableState(int position) {

		int tableNum = position;
		String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
		Cursor cTable = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES, where);
		if (cTable.getCount() == 0) {
			return 2;
		}
		int isOpen = cTable.getInt(cTable
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_TABLEOPEN));

		return isOpen;
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
		getMenuInflater().inflate(R.menu.customer, menu);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibOpenClosed: {
			Log.i(TAG, " table buttone pressed");
			switch (tableOpen) {
			case 1: {
				tvOpenClosed.setText("Click to open the table");
				ibOpenClosed.setImageResource(R.drawable.open);
				changeTableStatus();

				int sessionId = getSessionId(TABLENUM);
				Log.i(TAG, "SessionId " + sessionId + " tableId " + TABLENUM);
				showTableDialog(sessionId, TABLENUM);

				// tableOpen = 1;

			}
				break;
			case 0: {
				
				int sessionId = getSessionId(TABLENUM);
				Log.i(TAG, "SessionId " + sessionId + " tableId " + TABLENUM);
				showTableDialog(sessionId, TABLENUM);
				//ibOpenClosed.setImageResource(R.drawable.closed);
				//changeTableStatus();
				//tvOpenClosed.setText("Click to choose table options");

				// showDialog

			}
				break;
			}
			ibOpenClosed.invalidate();

		}
		}
		// reload activity?
	}

	private void changeTableStatus() {
		int tableNum = TABLENUM;
		String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
		Cursor cTable = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES, where);

		int isOpen = cTable.getInt(cTable
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_TABLEOPEN));
		int rowId = cTable.getInt(cTable
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
		// int tableOpen = 2;

		String dateTime = getCurrentDateTime();
		// TODO make table seesion waiter id
		int waiterId = 1;
		int sessionActive = 1;

		Log.i(TAG, " isOpen value " + isOpen);

		if (isOpen == 0) { // start session

			sessionId = (int) db.insertRowRestaurnatTableSessions(tableNum,
					dateTime, waiterId, sessionActive);
			Log.i(TAG, " created session, id = " + sessionId);

		}
		String openOrClosed = "";
		if (isOpen == 1) {
			tableOpen = 0;
			openOrClosed = " is now Closed";
		} else {
			tableOpen = 1;
			openOrClosed = " is now Open";

		}

		Log.i(TAG, " Updating row id and table num " + rowId + " table Num "
				+ tableNum + "new isOpen value " + tableOpen);

		db.updateRowRestaurantTables(rowId, tableNum, tableOpen, sessionId);

		Toast.makeText(this, "" + tableNum + openOrClosed, Toast.LENGTH_SHORT)
				.show();
	}

	private int getSessionId(int position) {

		int tableNum = position;
		String where = DatabaseAdapter.KEY_TABLENUM + "=" + tableNum;
		Cursor cTable = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES, where);

		int sessionId = cTable.getInt(cTable
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_TABLESESSIONID));
		Log.i(TAG, "Session Id = " + sessionId);
		return sessionId;
	}

	private void showTableDialog(int sessionId, int tableId) {
		// mStackLevel++;

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag(
				"tableDialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);
		getFragmentManager();
		// Create and show the dialog.
		DialogFragment newFragment = TableDialogFragment.newInstance(sessionId,
				tableId);
		newFragment.show(ft, "tableDialog");
	}

	private String getCurrentDateTime() {
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

}
