package uk.co.nathanleigh.restaurant.waiter;

import java.util.ArrayList;
import java.util.TreeSet;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuAddonsFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuNameDiscIngsFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OrderCompletionActivity extends ListActivity implements
		OnClickListener {

	private static final String TAG = "ORDER_COMPLETION";
	private OrderHistoryListAdapter orderHistoryListAdapter;
	private DatabaseAdapter db;
	private int sessionId;
	private int tableId;
	private int currentTotalPrice = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_completion);
		// Show the Up button in the action bar.
		openDB();
		setupActionBar();
		Log.i(TAG, " Creating Order completion");
		Intent myIntent = getIntent(); // gets the previously created intent
		sessionId = myIntent.getIntExtra("sessionId", 0);
		tableId = myIntent.getIntExtra("tableId", 0);
		Log.i(TAG, " Table Id  " + tableId);
		Log.i(TAG, " Session Id  " + sessionId);

		Button bBack = (Button) findViewById(R.id.bBack);
		Button bPay = (Button) findViewById(R.id.bPay);
		bPay.setOnClickListener(this);
		bBack.setOnClickListener(this);

		
		Log.i(TAG, " set up list");
		setUpList();
		Log.i(TAG, " Set up text views");
		setUpTextViews();
	}


	private void setUpList() {

		OrderHistoryListAdapter orderHistoryListAdapter = new OrderHistoryListAdapter();
		
		// Log.i(TAG, " getting cursor");
		String where = DatabaseAdapter.KEY_TABLESESSIONID + "=" + sessionId;
		Cursor cAllSessionOrders = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_ORDERS, where);
		// Log.i(TAG, " got cursor all session orders");
		int sessionOrderCount = cAllSessionOrders.getCount();
		Log.i(TAG, " sessionOrder Count = " + sessionOrderCount);

		//outputs list of orders for the session, if order have been in groups 
		// then separates the order groups
		
		if (sessionOrderCount != 0) {
			// set 1st order group id as old. checks if current id has changed, if new order group
			// if true adds a separotor
			int oldOrderGroupId = cAllSessionOrders.getInt(cAllSessionOrders
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ORDERGROUPID));

			while (!cAllSessionOrders.isAfterLast()) {

				int currentOrderGroupId = cAllSessionOrders
						.getInt(cAllSessionOrders
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ORDERGROUPID));

				if (oldOrderGroupId != currentOrderGroupId) { // if new make
																// separator
					// TODO get orderdetails price
					where = DatabaseAdapter.KEY_ROWID + "=" + oldOrderGroupId;
					Cursor cOrderDetail = db
							.myDBQuery(DatabaseAdapter.DATABASE_TABLE_ORDER_DETAILS,
									where);

					int totalPrice = cOrderDetail
							.getInt(cOrderDetail
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_TOTALPRICE));

					currentTotalPrice += totalPrice;

					// Log.i(TAG, " adding separator total price = " +
					// totalPrice);
					orderHistoryListAdapter.addSeparatorItem(oldOrderGroupId,
							totalPrice);
					// this new order group id becomes old one.
					oldOrderGroupId = currentOrderGroupId;

				}

				int orderId = cAllSessionOrders.getInt(cAllSessionOrders
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				String itemName = cAllSessionOrders.getString(cAllSessionOrders
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMNAME));
				String optionName = cAllSessionOrders
						.getString(cAllSessionOrders
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
				int optionPrice = cAllSessionOrders
						.getInt(cAllSessionOrders
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));

				int addonTotalPrice = getAddonPriceTotal(orderId);
				
				
				 Log.i(TAG, " addonTotalPrice = " + addonTotalPrice);
				
				orderHistoryListAdapter.addItem(orderId, itemName, optionName,
						optionPrice, addonTotalPrice);

				//if item is last add group  total price separoter and 
				
				if (cAllSessionOrders.isLast()) {
					where = DatabaseAdapter.KEY_ROWID + "="
							+ currentOrderGroupId;
					Cursor cOrderDetail = db
							.myDBQuery(
									DatabaseAdapter.DATABASE_TABLE_ORDER_DETAILS,
									where);

					int totalPrice = cOrderDetail
							.getInt(cOrderDetail
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_TOTALPRICE));

					// Log.i(TAG, " adding separator total price = " +
					// totalPrice);
					orderHistoryListAdapter.addSeparatorItem(oldOrderGroupId,
							totalPrice);
				}

				cAllSessionOrders.moveToNext();

			}

			// list.setAdapter(orderHistoryListAdapter);
			setListAdapter(orderHistoryListAdapter);
			cAllSessionOrders.close();
		}

	}

	private int getAddonPriceTotal(int orderId) {
		int addonPriceTotal = 0;
		 Log.i(TAG, " get addonlist for order id = " + orderId);

		String where = DatabaseAdapter.KEY_ORDERID + "=" + orderId;
		Cursor cOrderAddons = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_ORDERS_ADDONS, where);
		 Log.i(TAG, " got cursor all session order addons");
		int orderAddoncount = cOrderAddons.getCount();
		Log.i(TAG, " sessionOrder Count = " + orderAddoncount);

		if (orderAddoncount != 0) {

			while (!cOrderAddons.isAfterLast()) {
				 Log.i(TAG, " looping through cursor and adding addons prices");
			
				int addonPrice = cOrderAddons.getInt(cOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));
				
				//TODO get price format right
				addonPriceTotal +=  addonPrice;
				
				cOrderAddons.moveToNext();
				
			}
			
		}
		cOrderAddons.close();
		return addonPriceTotal;
	
	}


	private void setUpTextViews() {
		TextView tvTotalCost = (TextView) findViewById(R.id.tvTotalCost);
		TextView tvTableId = (TextView) findViewById(R.id.tvTableId);
		TextView tvSessionId = (TextView) findViewById(R.id.tvSessionId);
		TextView tvTime = (TextView) findViewById(R.id.tvTime);
		String where = DatabaseAdapter.KEY_ROWID + "=" + sessionId;

		Cursor cThisSession = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS,
						where);

		if (cThisSession != null) {
			String time = cThisSession.getString(cThisSession
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_DATETIME));

			tvSessionId.setText("" + sessionId);
			tvTime.setText("" + time);
			tvTotalCost.setText("" + CurrencyFormat.
					currencyIntToString(currentTotalPrice));
			tvTableId.setText("" + tableId);

		}
		cThisSession.close();
	}

	
	private class OrderHistoryListAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

		private ArrayList mData = new ArrayList();
		private ArrayList<OrderDetails> orderInfo = new ArrayList<OrderDetails>();
		private LayoutInflater mInflater;

		private TreeSet mSeparatorsSet = new TreeSet();

		public OrderHistoryListAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addItem(final int orderId, final String itemName,
				final String optionName, final int optionPrice, 
				final int addonTotalPrice) {
			// mData.add(itemName);
			OrderDetails orderDetails = new OrderDetails(orderId, itemName,
					optionName, optionPrice, addonTotalPrice);
			orderInfo.add(orderDetails);
			notifyDataSetChanged();
		}

		public void addSeparatorItem(final int orderGroupId,
				final int totalPrice) {
			// mData.add(item);
			OrderDetails orderDetails = new OrderDetails(orderGroupId,
					totalPrice);
			orderInfo.add(orderDetails);

			// save separator position
			mSeparatorsSet.add(orderInfo.size() - 1);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR
					: TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getCount() {
			return orderInfo.size();
		}

		@Override
		public OrderDetails getItem(int position) {
			// return "" + mData.get(position);
			return orderInfo.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int type = getItemViewType(position);
			// System.out.println("getView " + position + " " + convertView +
			// " type = " + type);
			if (convertView == null) {
				System.out.println("Got holder");
				holder = new ViewHolder();
				switch (type) {
				case TYPE_ITEM:
					System.out.println("converting/inflating view");
					convertView = mInflater.inflate(
							R.layout.item_view_order_history_item, null);
					System.out.println("inf;lated view");
					holder.tvItemName = (TextView) convertView
							.findViewById(R.id.tvItemName);
					holder.tvOptionName = (TextView) convertView
							.findViewById(R.id.tvOptionName);
					holder.tvOptionPrice = (TextView) convertView
							.findViewById(R.id.tvOptionPrice);
					holder.tvOrderId = (TextView) convertView
							.findViewById(R.id.tvOrderId);
					holder.tvAddonTotalPrice = (TextView) convertView
							.findViewById(R.id.tvAddonTotalPrice);
					holder.tvItemTotalCost = (TextView) convertView
							.findViewById(R.id.tvItemTotalCost);
					// System.out.println("set type item holder text view");

					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(
							R.layout.item_view_order_history_seperator, null);
					holder.tvGroupOrderId = (TextView) convertView
							.findViewById(R.id.tvOrderGroupId);
					holder.tvPrice = (TextView) convertView
							.findViewById(R.id.tvPrice);
					// System.out.println("set type separator holder text views");

					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			System.out.println("getting order details");

			if (type == TYPE_ITEM) {

				// holder.textView.setText("" + mData.get(position));
				OrderDetails orderDetails = getItem(position);
				String itemName = orderDetails.getItemName();
				final int orderId = orderDetails.getOrderId();
				String optionName = orderDetails.getOptionName();
				int optionPrice = orderDetails.getOptionPrice();
				int addonTotalPrice = orderDetails.getAddonTotalPrice();
				
			
				
				int totalPrice = addonTotalPrice + optionPrice;

				holder.tvItemName.setText("" + itemName);
				holder.tvOrderId.setText("" + orderId);
				holder.tvOptionName.setText("" + optionName);
				holder.tvOptionPrice.setText("" + CurrencyFormat.
						currencyIntToString(optionPrice));
				holder.tvAddonTotalPrice.setText("" + CurrencyFormat.
						currencyIntToString(addonTotalPrice));
				holder.tvItemTotalCost.setText("" + 
						CurrencyFormat.currencyIntToString(totalPrice));

				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						System.out.println("clicked addon list item");
						//TODO returning null
						String addonList = getAddonList(orderId);
						Log.i(TAG, "ADDON LIST " + addonList);
						
						Toast t = Toast.makeText(getApplicationContext(),
								addonList, Toast.LENGTH_LONG);
						t.show();

					}

				});
			}

			if (type == TYPE_SEPARATOR) {
				// holder.textView.setText("" + mData.get(position));
				OrderDetails orderDetails = getItem(position);
				int totalPrice = orderDetails.getTotalPrice();
				int orderGroupId = orderDetails.getOrderGroupId();

				// System.out.println("set ing item holder text views");
				// System.out.println("ItemName, optionName, optionPrice");

				holder.tvPrice.setText("" + CurrencyFormat.
						currencyIntToString(totalPrice));
				holder.tvGroupOrderId.setText("" + orderGroupId);
			}

			// holder.textView.setText("" + mData.get(position));
			// System.out.println("set the item holder text views");

			return convertView;
		}

	}

	public static class ViewHolder {
		public TextView tvPrice;
		public TextView tvItemName;
		public TextView tvOptionName;
		public TextView tvOptionPrice;
		public TextView tvOrderId;
		public TextView tvGroupOrderId;
		public TextView tvAddonTotalPrice;
		public TextView tvItemTotalCost;
	}
	
	private String getAddonList(int orderId) {
		String addonList = "";
		 Log.i(TAG, " get addonlist for order id = " + orderId);

		String where = DatabaseAdapter.KEY_ORDERID + "=" + orderId;
		Cursor cOrderAddons = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_ORDERS_ADDONS, where);
		 Log.i(TAG, " got cursor all session order addons");
		int orderAddoncount = cOrderAddons.getCount();
		Log.i(TAG, " sessionOrder Count = " + orderAddoncount);

		if (orderAddoncount != 0) {

			while (!cOrderAddons.isAfterLast()) {
				 Log.i(TAG, " looping through cursor and adding addons to string");
				String addonName = cOrderAddons.getString(cOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
				
				String addonPrice = cOrderAddons.getString(cOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));
				
				addonList +=  addonName + " £" + addonPrice;
				cOrderAddons.moveToNext();
				if(!cOrderAddons.isAfterLast())
				{
					addonList +=   ", " ;
				}
				else{
					addonList +=   "." ;
				}
			}
		}
		
		cOrderAddons.close();
		return addonList;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bPay: {
			System.out.println("Pay button press");
			// TODO implement pay system but for now just close table, end
			// session

			payment();

			updateTableStatus();

			String message = "Payment Complete, table session ended, table closed";
			Toast t = Toast.makeText(getApplicationContext(), message,
					Toast.LENGTH_LONG);
			t.show();

			Intent intent = new Intent(this, TablesActivity.class);
			startActivity(intent);

		}

			break;

		case R.id.bBack: {
			System.out.println("back button press");
			Intent intent = new Intent(this, TablesActivity.class);
			startActivity(intent);

		}
			break;
		}

	}

	private void updateTableStatus() {
		String where = DatabaseAdapter.KEY_TABLESESSIONID + "=" + sessionId;
		Cursor cAllSessionOrders = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES, where);
		// Log.i(TAG, " got cursor all session orders");
		int sessionOrderCount = cAllSessionOrders.getCount();
		Log.i(TAG, " sessionOrder Count = " + sessionOrderCount);

		if (sessionOrderCount != 0) {

			int rowId = cAllSessionOrders.getInt(cAllSessionOrders
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			// set table to open
			db.updateRowRestaurantTables(rowId, tableId, 1, sessionId);

		}
		cAllSessionOrders.close();
	}

	private void payment() {
		// TODO Auto-generated method stub

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
		getMenuInflater().inflate(R.menu.order_completion, menu);
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
}
