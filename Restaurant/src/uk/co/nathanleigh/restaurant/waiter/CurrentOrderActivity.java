package uk.co.nathanleigh.restaurant.waiter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.menu.MenuActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CurrentOrderActivity extends FragmentActivity implements
		OnClickListener {

	// TODO order comments dissapear when exiting, save to a file?

	private static final String TAG = "CURRENT_ORDER_ACTIVITY";
	DatabaseAdapter db;
	private int currentOptionPrice = 0;
	private int currentAddonPrice = 0;
	private long orderDetailsRowId;
	private static ViewGroup containerView;
	private int sessionId;
	private int tableId;
	private TextView tvOrderComments;
	private String orderComments = "";
	private int addonSum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_order);

		Intent myIntent = getIntent(); // gets the previously created intent
		sessionId = myIntent.getIntExtra("sessionId", 0);
		tableId = myIntent.getIntExtra("tableId", 0);

		Log.i(TAG, " Table Id  " + tableId);
		Log.i(TAG, " Session Id  " + sessionId);

		TextView tvSessionId = (TextView) findViewById(R.id.tvSessionId);
		TextView tvTableNum = (TextView) findViewById(R.id.tvTableNum);
		tvOrderComments = (TextView) findViewById(R.id.tvOrderComments);
		// if (tvOrderComments.equals("Item Description")){
		tvOrderComments.setText("Click here to add order comments/details");
		// }
		tvSessionId.setText("" + sessionId);
		tvTableNum.setText("" + tableId);

		// Show the Up button in the action bar.
		setupActionBar();
		openDB();
		// populateListViewFromDB();
		Button bBackToMenu = (Button) findViewById(R.id.bBackToMenu);
		Button bOrder = (Button) findViewById(R.id.bOrder);

		containerView = (ViewGroup) findViewById(R.id.container);
		// when order is clicked, add details of order
		// to database and adjust stocklevels
		tvOrderComments.setOnClickListener(this);
		bOrder.setOnClickListener(this);
		bBackToMenu.setOnClickListener(this);

		fillList();

	}

	private void fillList() {
		Log.i(TAG, "Getting table temp order cursor");
		Cursor cursor = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER);
		cursor.moveToFirst();
		Log.i(TAG, "table temp order cursor count = " + cursor.getCount());

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					int tempOrderId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_ROWID));

					int menuItemId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_MENUITEMID));
					String menuItemName = cursor.getString(cursor
							.getColumnIndex(DatabaseAdapter.KEY_ITEMNAME));

					int optionId = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_OPTIONID));

					String optionName = cursor.getString(cursor
							.getColumnIndex(DatabaseAdapter.KEY_OPTIONNAME));

					int optionPrice = cursor.getInt(cursor
							.getColumnIndex(DatabaseAdapter.KEY_OPTIONPRICE));

					String addons = getListOfAddons(tempOrderId);

					Log.i(TAG, "got " + menuItemId + " " + optionName
							+ " to tempOrder");

					addTempOrder(tempOrderId, menuItemId, menuItemName,
							optionId, optionName, optionPrice, orderComments,
							addons, addonSum);

				} while (cursor.moveToNext());
			}
		}
		cursor.close();

	}

	private String getListOfAddons(int tempOrderId) {
		String addonList = "";
		Log.i(TAG, " get addonlist for order id = " + tempOrderId);
		addonSum = 0;
		String where = DatabaseAdapter.KEY_ORDERID + "=" + tempOrderId;
		Cursor cOrderAddons = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS, where);
		Log.i(TAG, " got cursor all session order addons");
		int orderAddoncount = cOrderAddons.getCount();
		Log.i(TAG, " sessionOrder Count = " + orderAddoncount);

		if (orderAddoncount != 0) {

			while (!cOrderAddons.isAfterLast()) {
				Log.i(TAG,
						" looping through cursor and adding addons to string");
				String addonName = cOrderAddons.getString(cOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

				int addonPrice = cOrderAddons.getInt(cOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

				addonSum += addonPrice;

				addonList += addonName + " "
						+ CurrencyFormat.currencyIntToString(addonPrice);
				cOrderAddons.moveToNext();
				if (!cOrderAddons.isAfterLast()) {
					addonList += ", ";
				} else {
					addonList += ".";
				}
			}
			Log.i(TAG, " addons sum " + addonSum);

		}

		cOrderAddons.close();
		return addonList;
	}

	private void addTempOrder(int tempOrderIdArg, int menuItemIdArg,
			String menuItemNameArg, int optionIdArg, String optionNameArg,
			int optionPriceArg, String orderCommentsArg, String addonsArg,
			int addonsSumArg) {

		// Log.i(TAG, " adding " + menuItemId + " " + optionName +
		// " to tempOrder")

		final ViewGroup newView = (ViewGroup) this.getLayoutInflater().inflate(
				R.layout.item_view_current_order, containerView, false);

		final int tempOrderId = tempOrderIdArg;
		final String menuItemName = menuItemNameArg;
		final String optionName = optionNameArg;
		final int optionPrice = optionPriceArg;
		final String addons = addonsArg;
		final int addonsTotalSum = addonsSumArg;

		((TextView) newView.findViewById(R.id.tvMenuItem)).setText(menuItemName
				+ " " + optionName + " "
				+ CurrencyFormat.currencyIntToString(optionPrice));

		((TextView) newView.findViewById(R.id.tvAddons)).setText("" + addons);

		int price = addonsTotalSum + optionPrice;
		Log.i(TAG, "price " + price);

		((TextView) newView.findViewById(R.id.tvPrice)).setText(""
				+ CurrencyFormat.currencyIntToString(price));

		newView.findViewById(R.id.delete_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						// Remove the row from its parent (the container view).
						// Because mContainerView has
						// android:animateLayoutChanges set to true,
						// this removal is automatically animated.

						containerView.removeView(newView);
						System.out.println(tempOrderId
								+ "  temp order Id deleting  ");

						db.deleteRow(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER,
								tempOrderId);
						System.out.println(tempOrderId
								+ "  temp order Id deleted  ");

						String where = DatabaseAdapter.KEY_ORDERID + "="
								+ tempOrderId;
						Cursor tempOrderAddons = db
								.myDBQuery(
										DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS,
										where);

						if (tempOrderAddons != null) {
							if (tempOrderAddons.moveToFirst()) {
								do {
									int tempOrderId = tempOrderAddons.getInt(tempOrderAddons
											.getColumnIndex(DatabaseAdapter.KEY_ROWID));

									db.deleteRow(
											DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS,
											tempOrderId);
									System.out
											.println("Deleted tempOrderAddon row id "
													+ tempOrderId);
								} while (tempOrderAddons.moveToNext());
							}
						}
						tempOrderAddons.close();

					}
				});

		// Because mContainerView has android:animateLayoutChanges set to true,
		// adding this view is automatically animated.
		Log.i(TAG, "Adding new view to container");

		containerView.addView(newView, 0);
		Log.i(TAG, "Added View");
	}

	private void adjustStockLevels() {

		// for each of the temp orders and get optionId for each order
		// use optionId to search tableIngForOptions
		// for each result get ingredient id and amount
		// update the stock levels database by deducting the ingId with ing
		// amount

		// do similar for temp addons
		Log.i(TAG, "ADJUSTING STOCK LEVELS ");
		Log.i(TAG, "ADJUSTING STOCK LEVELS FOR OPTION OF THE ORDER");

		Cursor cAllTempOrders = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER);
		Log.i(TAG, "GOT ALL TEMP ORDERS, ITERATING THROUGH");
		for (cAllTempOrders.moveToFirst(); !cAllTempOrders.isAfterLast(); cAllTempOrders
				.moveToNext()) {

			int optionId = cAllTempOrders.getInt(cAllTempOrders
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONID));
			String optionName = cAllTempOrders.getString(cAllTempOrders
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
			int rowId = cAllTempOrders.getInt(cAllTempOrders
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			Log.i(TAG, "rowid, optionId, optionName for temp order"
					+ " in list is " + rowId + ", " + optionId + ", "
					+ optionName);
			String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionId;
			Cursor cOptionIngAmounts = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
							where);
			Log.i(TAG,
					"Got cursor for all ing amount for that option, ITERATING THROUGH LIST");

			for (cOptionIngAmounts.moveToFirst(); !cOptionIngAmounts
					.isAfterLast(); cOptionIngAmounts.moveToNext()) {

				// getIngId and ing amount and update stock levels
				int ingId = cOptionIngAmounts.getInt(cOptionIngAmounts
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				int ingAmountToDeduct = cOptionIngAmounts
						.getInt(cOptionIngAmounts
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));
				Log.i(TAG, "Option: got the ing id " + ingId
						+ " and ing amount to deduct " + ingAmountToDeduct);

				if (ingAmountToDeduct != 0) {

					int ingComplex;

					// check if ing is raw or complex
					Cursor cIng = db.getRow(
							DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, ingId);
					ingComplex = cIng
							.getInt(cIng
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGCOMPLEX));
					cIng.close();
					Log.i(TAG, "Ing Is Complex = " + ingComplex);

					// check if ingredient is complex, if true multiple
					// ingAmountToDeduct by
					// the cinsituent ingredients ing proportion/ratio amount
					if (ingComplex == 1) {
						// get list of ings which make up the complex Ing
						where = DatabaseAdapter.KEY_COMPLEXINGID + "=" + ingId;
						Cursor cComplexConsituents = db
								.myDBQuery(
										DatabaseAdapter.DATABASE_TABLE_COMPLEX_INGREDIENTS,
										where);
						for (cComplexConsituents.moveToFirst(); !cComplexConsituents
								.isAfterLast(); cComplexConsituents
								.moveToNext()) {

							int rawIngId = cComplexConsituents
									.getInt(cComplexConsituents
											.getColumnIndexOrThrow(DatabaseAdapter.KEY_RAWINGID));

							int ingRatio = cComplexConsituents
									.getInt(cComplexConsituents
											.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNTPROPORTION));

							Log.i(TAG, "ingAmountToDeduct " + ingAmountToDeduct);
							Log.i(TAG, "ingRatio " + ingRatio);

							int rawIngAmountToDeduct = ingAmountToDeduct
									* ingRatio;
							Log.i(TAG, "ingAmountToDeduct * ingRatio = "
									+ "rawIngAmountToDeduct = "
									+ rawIngAmountToDeduct);

							updateStockLevelAmount(rawIngId,
									rawIngAmountToDeduct);
						}
					} else {
						// ing is raw, update stockLevels
						updateStockLevelAmount(ingId, ingAmountToDeduct);

					}
				}
			}

			Log.i(TAG, "Updated ing amounts for the optionId in the tempOrder");
			Log.i(TAG, "=============================================");
			Log.i(TAG,
					"Updating Addon ing amounts for the optionId in the tempOrder");

			updateAddonsStockLevels(optionId);

		}
		Log.i(TAG, "FINISHED ADJUSTING STOCK LEVELS FOR OPTION");
		Log.i(TAG, "=-=-=-=-=-=--=-=-=-=-=--=-=-=-=-=-=-=-==--=");

	}

	private void updateAddonsStockLevels(int optionId) {

		Log.i(TAG, "ADJUSTING STOCK LEVELS FOR ADDONS IN THE TEMP ORDER");
		Cursor cAllTempOrderAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS);
		Log.i(TAG, "GOT ALL TEMP ORDERS ADDONS, ITERATING THROUGH");
		for (cAllTempOrderAddons.moveToFirst(); !cAllTempOrderAddons
				.isAfterLast(); cAllTempOrderAddons.moveToNext()) {

			int addonId = cAllTempOrderAddons.getInt(cAllTempOrderAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONID));

			Log.i(TAG, "addonId for temp order in list is " + addonId);
			Log.i(TAG, "optionId for temp order in list is " + optionId);

			String where = DatabaseAdapter.KEY_ADDONID + "=" + addonId
					+ " AND " + DatabaseAdapter.KEY_OPTIONID + "=" + optionId;

			Cursor cAddonsIngAmounts = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
							where);
			Log.i(TAG,
					"Got cursor for the addon ing amount for that option, ITERATING THROUGH LIST");

			for (cAddonsIngAmounts.moveToFirst(); !cAddonsIngAmounts
					.isAfterLast(); cAddonsIngAmounts.moveToNext()) {

				// getIngId and ing amount and update stock levels
				int ingId = cAddonsIngAmounts.getInt(cAddonsIngAmounts
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				int ingAmountToDeduct = cAddonsIngAmounts
						.getInt(cAddonsIngAmounts
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT));
				Log.i(TAG, "Addon: got the ing id " + ingId
						+ " and ing amount to deduct" + ingAmountToDeduct);

				if (ingAmountToDeduct != 0) {

					int ingComplex;

					// check if ing is raw or complex
					Cursor cIng = db.getRow(
							DatabaseAdapter.DATABASE_TABLE_INGREDIENTS, ingId);
					ingComplex = cIng
							.getInt(cIng
									.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGCOMPLEX));
					cIng.close();
					Log.i(TAG, "Ing Is Complex = " + ingComplex);

					// check if ingredient is complex, if true multiple
					// ingAmountToDeduct by
					// the cinsituent ingredients ing proportion/ratio amount
					if (ingComplex == 1) {
						// get list of ings which make up the complex Ing
						where = DatabaseAdapter.KEY_COMPLEXINGID + "=" + ingId;
						Cursor cComplexConsituents = db
								.myDBQuery(
										DatabaseAdapter.DATABASE_TABLE_COMPLEX_INGREDIENTS,
										where);
						for (cComplexConsituents.moveToFirst(); !cComplexConsituents
								.isAfterLast(); cComplexConsituents
								.moveToNext()) {

							int rawIngId = cComplexConsituents
									.getInt(cComplexConsituents
											.getColumnIndexOrThrow(DatabaseAdapter.KEY_RAWINGID));

							int ingRatio = cComplexConsituents
									.getInt(cComplexConsituents
											.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNTPROPORTION));

							Log.i(TAG, " rawingID " + rawIngId);
							Log.i(TAG, "ingAmountToDeduct " + ingAmountToDeduct);
							Log.i(TAG, "ingRatio " + ingRatio);

							int rawIngAmountToDeduct = ingAmountToDeduct
									* ingRatio;
							Log.i(TAG, "ingAmountToDeduct * ingRatio = "
									+ "rawIngAmountToDeduct = "
									+ rawIngAmountToDeduct);

							updateStockLevelAmount(rawIngId,
									rawIngAmountToDeduct);
						}
					} else {
						// ing is raw, update stockLevels
						updateStockLevelAmount(ingId, ingAmountToDeduct);
					}
				}
			}
			Log.i(TAG, "Updated ing amounts for the optionId in the tempOrder");
			Log.i(TAG, "=============================================");
		}
		Log.i(TAG, "FINISHED ADJUSTING STOCK LEVELS FOR OPTION");
		Log.i(TAG, "=-=-=-=-=-=--=-=-=-=-=--=-=-=-=-=-=-=-==--=");

	}

	public void updateStockLevelAmount(int ingId, int ingAmountToDeduct) {

		String where = DatabaseAdapter.KEY_INGID + "=" + ingId;
		Cursor cStockLevels = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_STOCK_LEVELS, where);

		// calculate stock level deduction for a raw Ing
		Log.i(TAG, "Got Stock Levels Cursor with row for ing id " + ingId);
		if (cStockLevels != null) {
			int rowId = cStockLevels.getInt(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

			String ingName = cStockLevels.getString(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

			String ingUnit = cStockLevels.getString(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT));

			int ingAmount = cStockLevels.getInt(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));

			int ingRecAmount = cStockLevels.getInt(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_RECAMOUNT));

			int ingWarningAmount = cStockLevels.getInt(cStockLevels
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_WARNINGAMOUNT));

			Log.i(TAG, "Updating Stock Levels RowId = " + rowId + " ingName "
					+ ingName);
			Log.i(TAG, "Ing Old Amount = " + ingAmount);

			int newIngAmount = ingAmount - ingAmountToDeduct;
			Log.i(TAG, "Ing new Amount = " + newIngAmount);

			db.updateRowStockLevels(rowId, ingId, ingName, ingUnit,
					newIngAmount, ingRecAmount, ingWarningAmount);

			Log.i(TAG, "UPDATED STOCK LEVELS");
			Log.i(TAG, "-----------------------------------------------------");
		}

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.bBackToMenu: {
			Log.i(TAG, " Back to menu button pressed");

			Intent intent = new Intent(this, MenuActivity.class);
			intent.putExtra("sessionId", sessionId);
			intent.putExtra("tableId", tableId);
			startActivity(intent);
		}
			break;

		case R.id.bOrder: {
			Log.i(TAG, " Order button pressed");
			addTempOrderToOrderDb();

			adjustStockLevels();

			db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER);
			db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS);

			System.out
					.println("added order to db and adjusted stock levels, deleted temp tablesS");
			Intent intent = new Intent(this, MenuActivity.class);
			intent.putExtra("sessionId", sessionId);
			intent.putExtra("tableId", tableId);
			startActivity(intent);
		}
			break;

		case R.id.tvOrderComments: {
			Log.i("TAG", "DESCRIPTION   EXT VIEW CLICKED");
			LayoutInflater layoutInflater = LayoutInflater.from(this);
			View promptView = layoutInflater.inflate(
					R.layout.dialog_order_comments, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			// set prompts.xml to be the layout file of the alertdialog
			// builder
			alertDialogBuilder.setView(promptView);

			final EditText input = (EditText) promptView
					.findViewById(R.id.etOrderComments);

			input.setText("" + tvOrderComments.getText());

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
										itemDesc = "(No Order Comments)";
									}
									tvOrderComments.setText(itemDesc);
									orderComments = itemDesc;
									// db.insertRowTempMenuItemDesc(
									// imageTitle, itemDesc);

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

	private void addTempOrderToOrderDb() {

		String dateTime = getCurrentDateTime();
		int totalPrice = 0;

		System.out
				.println("Inserting order details tablesession id, table num date time, tootal price");
		// System.out.println(tableSessionId + ", " + tableNum);
		// TODO wrong way round?
		Log.i(TAG, "session id = " + sessionId + " tableId = " + tableId);
		orderDetailsRowId = db.insertRowOrderDetails(sessionId, tableId,
				dateTime, totalPrice, orderComments);
		int orderId = (int) orderDetailsRowId;

		Cursor cTempOrder = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER);

		for (cTempOrder.moveToFirst(); !cTempOrder.isAfterLast(); cTempOrder
				.moveToNext()) {

			int menuItemId = cTempOrder.getInt(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_MENUITEMID));

			int tempOrderId = cTempOrder.getInt(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

			String menuItemName = cTempOrder.getString(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMNAME));

			int optionId = cTempOrder.getInt(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONID));

			String optionName = cTempOrder.getString(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
			int optionPrice = cTempOrder.getInt(cTempOrder
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));

			currentOptionPrice += optionPrice;
			Log.i(TAG, "current optionPrice = " + currentOptionPrice);

			Log.i(TAG, "insertining into order table");
			Log.i(TAG, "session id = " + sessionId + " tableId = " + tableId);

			String addonNameList = "Addons: ";

			int orderItemId = (int) db.insertRowOrder(sessionId, orderId,
					menuItemId, menuItemName, optionId, optionName,
					optionPrice, addonNameList);

			String where = DatabaseAdapter.KEY_ORDERID + "=" + tempOrderId;

			Log.i(TAG, "TempOrderId = " + tempOrderId);
			Cursor cTempOrderAddons = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_TEMP_ORDER_ADDONS, where);

			for (cTempOrderAddons.moveToFirst(); !cTempOrderAddons
					.isAfterLast(); cTempOrderAddons.moveToNext()) {

				int addonId = cTempOrderAddons.getInt(cTempOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONID));

				String addonName = cTempOrderAddons.getString(cTempOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

				int addonPrice = cTempOrderAddons.getInt(cTempOrderAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

				currentAddonPrice += addonPrice;
				addonNameList = addonNameList + ", " + addonName;
				Log.i(TAG, "current addonPrice = " + currentAddonPrice);

				Log.i(TAG, "adding addon " + addonName);

				Log.i(TAG, "Inserting in order addons table");
				Log.i(TAG, " orderItemId " + orderItemId + ", menuItemId "
						+ menuItemId + ", menuItemName " + menuItemName
						+ ", optionId " + optionId + ", optionName "
						+ optionName + ", addonId " + addonId + ", addonName "
						+ addonName + ", addonPrice " + addonPrice);

				db.insertRowOrderAddons(orderItemId, menuItemId, menuItemName,
						optionId, optionName, addonId, addonName, addonPrice);

			}
			db.updateRowOrders(orderItemId, sessionId, orderId, menuItemId,
					menuItemName, optionId, optionName, optionPrice,
					addonNameList);

			cTempOrderAddons.close();

		}

		int totalPriceInt = currentOptionPrice + currentAddonPrice;
		Log.i(TAG, "total price = " + totalPriceInt);

		totalPrice = totalPriceInt;

		// update order details with total cost
		Log.i(TAG, "update order details with total cost");

		Log.i(TAG, "session id = " + sessionId + " tableId = " + tableId);

		db.updateRowOrderDetails(orderDetailsRowId, sessionId, tableId,
				dateTime, totalPrice, orderComments);
		currentAddonPrice = 0;
		currentOptionPrice = 0;
		// cTempOrderAddons.close();
		cTempOrder.close();
	}

	private String getAddonNameList(Cursor cTempOrderAddons) {
		// TODO Auto-generated method stub
		return null;
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

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.current_order, menu);
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
