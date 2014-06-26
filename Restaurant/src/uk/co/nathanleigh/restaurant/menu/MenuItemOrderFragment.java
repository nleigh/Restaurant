package uk.co.nathanleigh.restaurant.menu;

import java.text.DecimalFormat;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurant.Nutrition;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//TODO options price too large

public class MenuItemOrderFragment extends Fragment implements OnClickListener,
		android.widget.CompoundButton.OnCheckedChangeListener,
		OnCheckedChangeListener, OnKeyListener {

	private View rootView;
	private DatabaseAdapter db;
	private Context context;
	private String optionName;
	private int optionPrice;
	private RadioGroup optionsRadioGroup;
	private CheckBox checkBox;
	private LinearLayout linearLayoutAddons;
	private LinearLayout linearLayoutOptions;
	private TextView tvMenuItemName, tvCurrentPrice;
	private int position;
	private int addonCheckBoxId = 0;
	boolean addonCheckBoxChecked[];
	private int totalNumOfAddons = 0;
	private int menuItemId;
	private String menuItemName;
	public static final String TAG = "MENU_ITEM_ORDER";
	public static final String ARG_MENUITEMNAME = "menuItemName";
	public static String ARG_MENUITEMID = "menuItemId";
	public static String ARG_SESSIONID = "sessionId";
	public static String ARG_TABLEID = "tableId";
	private int[] optionIdToRowId;
	private int tempOrderId;
	private int tableId;
	private int sessionId;

	private TextView tvItemDescription, tvTableId, tvSessionId,
	tvCalorieStatus, tvCalorieAmount, tvCaloriePercentage;
	private TextView tvSaltStatus, tvSaltAmount, tvSaltPercentage;
	private TextView tvSaturatesStatus, tvSaturatesAmount,
			tvSaturatesPercentage;
	private TextView tvSugarsStatus, tvSugarsAmount, tvSugarsPercentage;
	private TextView tvFatStatus, tvFatAmount, tvFatPercentage;
	private ImageView ivCalories, ivFat, ivSalt, ivSugars, ivSaturates;
	private ImageView ivVegetarian, ivNuts, ivDairy, ivGluten;

	double caloriesSum = 0;
	double saturatesSum = 0;
	double saltSum = 0;
	double sugarsSum = 0;
	double fatSum = 0;

	//TODO is this called sending wrong order of args
	public static MenuItemOrderFragment create(int menuItemId,
			String menuItemName, int sessionId, int tableId) {
		MenuItemOrderFragment fragment = new MenuItemOrderFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_MENUITEMID, menuItemId);
		args.putString(ARG_MENUITEMNAME, menuItemName);
		args.putInt(ARG_SESSIONID, sessionId);
		args.putInt(ARG_TABLEID, tableId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();
		menuItemId = getArguments().getInt(ARG_MENUITEMID);
		menuItemName = getArguments().getString(ARG_MENUITEMNAME);
		sessionId = getArguments().getInt(ARG_SESSIONID);
		tableId = getArguments().getInt(ARG_TABLEID);
		
		Log.i(TAG, "Created menuItemOrderFrag sessionId " + sessionId
				+ " and table Id " + tableId);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_menu_item_order_small, container, false);

		context = getActivity();

		setUpViews();
		addMenuItemNameAndDesc();
		addonCheckBoxesCheckedToFalse();
		addOptionsRadioButtons();
		addCurrentPrice();
		calculateOptionNutrionalInfo();
		// dont have to calculate addons, all unchecked
		displayNutritionalInfo();

		return rootView;

	}

	private void setUpViews() {
		tvSessionId = (TextView) rootView.findViewById(R.id.tvSessionId);
		tvTableId = (TextView) rootView.findViewById(R.id.tvTableId);
		tvMenuItemName = (TextView) rootView.findViewById(R.id.tvMenuItemName);
		tvItemDescription = (TextView) rootView.findViewById(R.id.tvItemDescription);
		tvCurrentPrice = (TextView) rootView.findViewById(R.id.tvCurrentPrice);
		optionsRadioGroup = (RadioGroup) rootView.findViewById(R.id.rgOptions);
		linearLayoutAddons = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutAddons);
	

		Button bBack = (Button) rootView.findViewById(R.id.bBack);
		Button bOrder = (Button) rootView.findViewById(R.id.bOrder);
		bBack.setOnClickListener(this);
		bOrder.setOnClickListener(this);
		optionsRadioGroup.setOnCheckedChangeListener(this);

		tvCalorieStatus = (TextView) rootView
				.findViewById(R.id.tvCalorieStatus);
		tvCalorieAmount = (TextView) rootView
				.findViewById(R.id.tvCalorieAmount);
		tvCaloriePercentage = (TextView) rootView
				.findViewById(R.id.tvCaloriePercentage);
		tvSaltStatus = (TextView) rootView.findViewById(R.id.tvSaltStatus);
		tvSaltAmount = (TextView) rootView.findViewById(R.id.tvSaltAmount);
		tvSaltPercentage = (TextView) rootView
				.findViewById(R.id.tvSaltPercentage);
		tvSaturatesStatus = (TextView) rootView
				.findViewById(R.id.tvSaturatesStatus);
		tvSaturatesAmount = (TextView) rootView
				.findViewById(R.id.tvSaturatesAmount);
		tvSaturatesPercentage = (TextView) rootView
				.findViewById(R.id.tvSaturatesPercentage);
		tvSugarsStatus = (TextView) rootView.findViewById(R.id.tvSugarsStatus);
		tvSugarsAmount = (TextView) rootView.findViewById(R.id.tvSugarsAmount);
		tvSugarsPercentage = (TextView) rootView
				.findViewById(R.id.tvSugarsPercentage);
		tvFatStatus = (TextView) rootView.findViewById(R.id.tvFatStatus);
		tvFatAmount = (TextView) rootView.findViewById(R.id.tvFatAmount);
		tvFatPercentage = (TextView) rootView
				.findViewById(R.id.tvFatPercentage);

		ivCalories = (ImageView) rootView.findViewById(R.id.ivCalories);
		ivFat = (ImageView) rootView.findViewById(R.id.ivFat);
		ivSugars = (ImageView) rootView.findViewById(R.id.ivSugars);
		ivSaturates = (ImageView) rootView.findViewById(R.id.ivSaturates);
		ivSalt = (ImageView) rootView.findViewById(R.id.ivSalt);

		// ivDairy = (ImageView) rootView.findViewById(R.id.ivDairy);
		// ivNuts = (ImageView) rootView.findViewById(R.id.ivNuts);
		// ivGluten = (ImageView) rootView.findViewById(R.id.ivGluten);
		// ivVegetarian = (ImageView) rootView.findViewById(R.id.ivVegetarian);

		// TODO DOES THIS WORK?
		/*
		 * rootView.setOnKeyListener(new OnKeyListener() {
		 * 
		 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		 * if( keyCode == KeyEvent.KEYCODE_BACK ){ // back to previous fragment
		 * by tag System.out.println("BACK PRESSED"); Intent intent = new
		 * Intent(getActivity(), MenuActivity.class); startActivity(intent);
		 * 
		 * return true; } return false;
		 * 
		 * } });
		 */
		
		String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		Cursor c = db.myDBQuery(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_DESCRIPTION, where);
		String itemDesc = c.getString(c.getColumnIndexOrThrow
				(DatabaseAdapter.KEY_ITEMDESC));
		c.close();
		//TODO still wrong way round
		tvTableId.setText("" + sessionId);
		tvSessionId.setText("" + tableId);
		tvItemDescription.setText("" + itemDesc);

	}

	
	///////////////////////////////////////////////////////////////////////////

	
	
	private void addonCheckBoxesCheckedToFalse() {
		String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		Cursor c = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);

		totalNumOfAddons = c.getCount();
		addonCheckBoxChecked = new boolean[totalNumOfAddons];
		c.close();
		System.out.println("Number of addons = " + totalNumOfAddons);

		for (int i = 0; i < totalNumOfAddons; i++) {
			addonCheckBoxChecked[addonCheckBoxId] = false;
		}

	}

	private void addMenuItemNameAndDesc() {
		// TODO GET DESC
		tvMenuItemName.setText(menuItemName);

	}

	private void addOptionsRadioButtons() {

		String where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		Cursor c = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
		// TODO 100 SHOULD BE MAX OPTIONS
		optionIdToRowId = new int[100]; // THIS NEEDS CHANGING

		c.moveToFirst();
		int checkFirstButton = 0;
		int optionRowId;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			RadioButton optionRadioButton = new RadioButton(getActivity());

			optionName = c.getString(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
			optionRowId = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			optionPrice = c.getInt(c
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));
			optionRadioButton.setText(optionName + " " + 
					CurrencyFormat.currencyIntToString(optionPrice));

			optionsRadioGroup.addView(optionRadioButton);

			optionIdToRowId[optionRadioButton.getId()] = optionRowId;

			System.out
					.println("ADDED RADIO OPTION KEY ROW ID = " + optionRowId);
			System.out.println("OptionradiobuttonID value = "
					+ optionRadioButton.getId());

			if (checkFirstButton == 0) {
				optionsRadioGroup.check(optionRadioButton.getId());
				checkFirstButton++;
			}
		}

		c.close();

	}

	private void addAddonsCheckBoxes() {

		String where1 = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		Cursor cAddons = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where1);

		String addonName;
		int addonPrice;
		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		addonCheckBoxId = 0;
		int optionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Iterate and print addons from addon table");
		System.out.println("Checked Id" + checkedRadioOption);
		System.out.println("KEY ROW ID FOR OPTION = " + optionRowId);
		System.out.println("cAddons get count = " + cAddons.getCount());

		String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionRowId;

		System.out.println("Getting cursor for all addoningsamount "
				+ "that KEYOPTIONROWID = optionRowId");

		Cursor cAddonIngAmount = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						where);

		System.out.println("cAddonsIngAmount get count = "
				+ cAddonIngAmount.getCount());

		for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons
				.moveToNext()) {
			System.out.println("Moved to next record in cursor Addons");
			checkBox = new CheckBox(getActivity());

			addonName = cAddons.getString(cAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

			addonPrice = cAddonIngAmount.getInt(cAddonIngAmount
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));
			cAddonIngAmount.moveToNext();
			System.out.println("Got addonPrice from MYQUEY");

			System.out.println("Option Id = " + checkedRadioOption);
			System.out.println("Addon Name = " + addonName + " AddonPrice = "
					+ addonPrice);

			checkBox.setText(addonName + " "
					+ CurrencyFormat.currencyIntToString(addonPrice));

			linearLayoutAddons.addView(checkBox);
			
			//linearLayoutOptions.addView(child);
			int checkedRadioId = optionsRadioGroup.getCheckedRadioButtonId();
			System.out.println("Checked radio option" + checkedRadioId);

			System.out.println("Check box check for true");

			if (addonCheckBoxChecked[addonCheckBoxId] == true) {
				checkBox.setChecked(true);
				System.out.println("Checkbox was true so set to true");
			}

			checkBox.setId(addonCheckBoxId);
			checkBox.setOnCheckedChangeListener(this);

			addonCheckBoxId++;
			totalNumOfAddons = addonCheckBoxId;
		}
		cAddons.close();
		cAddonIngAmount.close();
		System.out.println("FINISHED ADDING ADDON BOXES");

	}

	// /////////////////////////////////////////////////////////////
	// Pricw
	// //////////////////////////////////////////////////

	private void updatePrice() {
		int optionPrice = getOptionSelectedPrice();
		System.out.println("Option Price to update = " + optionPrice);
		int addonsPrice = getAddonsCheckedPrice();
		System.out.println("Addons Price to update = " + addonsPrice);
		int currentPrice = optionPrice + addonsPrice;
		System.out.println("CURRENT Price to update = " + currentPrice);

		//tvCurrentPrice.setText("Current Price: £ " + currentPrice);
		tvCurrentPrice.setText("" + CurrencyFormat.currencyIntToString(currentPrice));
	}

	private int getOptionSelectedPrice() {

		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Checked Id" + checkedRadioOption);
		System.out.println("Checked RowId" + optionRowId);
		String where = DatabaseAdapter.KEY_ROWID + "=" + optionRowId;

		Cursor cursor = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
		if (cursor.getCount() == 0)
			return 0;
		int optionPrice = cursor.getInt(cursor
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));

		cursor.close();

		return optionPrice;
	}

	private int getAddonsCheckedPrice() {

		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Checked Id" + checkedRadioOption);
		System.out.println("Checked RowId" + optionRowId);
		String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionRowId;
		int addonPrice;
		int addonTotalPrice = 0;
		Cursor cursor = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						where);

		cursor.moveToFirst();
		System.out.println("CURSOR COUNT = " + cursor.getCount());

		for (int i = 0; i < cursor.getCount(); i++) {

			if (addonCheckBoxChecked[i] == true)
				System.out
						.println("Addon checked is True so add to addonsPrice");
			else
				System.out
						.println("Addon checked is False so dont add to addonsPrice");

			if (addonCheckBoxChecked[i] == true) {

				addonPrice = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

				System.out.println("addonPrice = " + addonPrice);
				System.out.println("current addonsPrice = " + addonPrice);

				addonTotalPrice += addonPrice;
				System.out.println("new addonsPrice runing total = "
						+ addonTotalPrice);
			}

			cursor.moveToNext();
		}

		cursor.close();
		return addonTotalPrice;

	}

	private void addCurrentPrice() {

		int optionPrice = getOptionSelectedPrice();
		System.out.println("Option Price = " + optionPrice);
		tvCurrentPrice.setText("Current Price:  "
				+ CurrencyFormat.currencyIntToString(optionPrice));
	}

	// /////////////////////////////////////////////////////////////////////////////
	// NUTRITION
	// ///////////////////////////////////////////////////////////////////////////////

	private void calculateOptionNutrionalInfo() {

		int ingId;
		double ingAmount;
		Log.i(TAG, "Reset all sum amounts");
		caloriesSum = 0;
		saturatesSum = 0;
		saltSum = 0;
		sugarsSum = 0;
		fatSum = 0;

		Log.i(TAG, "calculating nutritionalInfo for option selected");
		// get option ing id and amounts and sum up the nutritional info
		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		// System.out.println("Checked Id" + checkedId);optionIdToRowId
		String where = DatabaseAdapter.KEY_OPTIONID + "="
				+ optionIdToRowId[checkedRadioOption];

		Cursor cursor = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
						where);
		if (cursor.getCount() == 0)
			Log.i(TAG, "cursor temp ing amount for option empty");
		else {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				ingId = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				ingAmount = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT));

				if (ingAmount != 0) {
					calculateNutritionalAmounts(ingId, ingAmount);
				}

			}

			Log.i(TAG, "============================================");
			Log.i(TAG, "Finished calculating calories caloriesSum: "
					+ caloriesSum);
		}
		cursor.close();

	}

	private void calculateAddonNutrionalInfo() {

		Log.i(TAG, "calculating nutritionalInfo for addons");
		// get option ing id and amounts and sum up the nutritional info
		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionKeyRowId = optionIdToRowId[checkedRadioOption];

		String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionKeyRowId;
		Cursor cursor = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						where);

		System.out.println("CURSOR Addon for option id COUNT = "
				+ cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {

			if (addonCheckBoxChecked[i] == true) {

				int ingId = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
				double ingAmount = cursor
						.getInt(cursor
								.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT));

				System.out.println("addon ingId = " + ingId);
				System.out.println("addon ingAmount = " + ingAmount);

				// calculate Nutritional amounts
				if (ingAmount != 0) {
					calculateNutritionalAmounts(ingId, ingAmount);
				}
				//System.out.println("calculated nutrition AMounts added to sum");

			} else {
				// Addon checked is False so dont add to addonsPrice
			}
			cursor.moveToNext();
		}

		cursor.close();

		Log.i(TAG, "current caloriesSum: " + caloriesSum);
		Log.i(TAG, "current saltSum: " + saltSum);
		Log.i(TAG, "current sugarsSum: " + sugarsSum);
		Log.i(TAG, "current fatSum: " + fatSum);
		Log.i(TAG, "current saturatesSum: " + saturatesSum);

	}

	private void calculateNutritionalAmounts(int ingId, double ingAmount) {

		Log.i(TAG, "Calculating NutritionalAmounts for ing Id: " + ingId
				+ " ingAmount: " + ingAmount);
		// gets nutritional info cursor for ing Id
		String where = DatabaseAdapter.KEY_INGID + "=" + ingId;
		Cursor cNutrition = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_NUTRITIONALINFO, where);

		Cursor cIngInfo = db.getRow(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS,
				ingId);
		int ingComplex = cIngInfo.getInt(cIngInfo
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGCOMPLEX));
		cIngInfo.close();

		// if ing is raw
		if (ingComplex == 0) {
			Log.i(TAG, "Ing is a raw ingredient");
			// get all nutrition ratios
			double calorieRatio = cNutrition.getDouble(cNutrition
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_CALORIES));
			double sugarsRatio = cNutrition.getDouble(cNutrition
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SUGARS));
			double saltRatio = cNutrition.getDouble(cNutrition
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SALT));
			double saturatesRatio = cNutrition.getDouble(cNutrition
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_SATURATES));
			double fatRatio = cNutrition.getDouble(cNutrition
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_FAT));

			Log.i(TAG, "current saltSum: " + saltSum);
			saltSum += cacluateRatioOfIngAmount(saltRatio, ingAmount);
			Log.i(TAG, "saltSum: " + saltSum);

			Log.i(TAG, "current calorieSum: " + caloriesSum);
			caloriesSum += cacluateRatioOfIngAmount(calorieRatio, ingAmount);
			Log.i(TAG, "caloriesSum: " + caloriesSum);

			Log.i(TAG, "current saturatesSum: " + saturatesSum);
			saturatesSum += cacluateRatioOfIngAmount(saturatesRatio, ingAmount);
			Log.i(TAG, "saturatesSum: " + saturatesSum);

			Log.i(TAG, "current sugarsSum: " + sugarsSum);
			sugarsSum += cacluateRatioOfIngAmount(sugarsRatio, ingAmount);
			Log.i(TAG, "sugarsSum: " + sugarsSum);

			Log.i(TAG, "current fatSum: " + fatSum);
			fatSum += cacluateRatioOfIngAmount(fatRatio, ingAmount);
			Log.i(TAG, "fatSum: " + fatSum);

			cNutrition.close();

		} else {// if ing is complex
			Log.i(TAG, "Ing is complex");
			// get ingConsituents and calculate nutritionAmounts
			// RECURSION
			where = DatabaseAdapter.KEY_COMPLEXINGID + "=" + ingId;
			Cursor cConsituentIngs = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_COMPLEX_INGREDIENTS, where);
			Log.i(TAG,
					"cConsituentIngs count" + cConsituentIngs.getColumnCount());

			for (cConsituentIngs.moveToFirst(); !cConsituentIngs.isAfterLast(); cConsituentIngs
					.moveToNext()) {

				int rawIngId = cConsituentIngs.getInt(cConsituentIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_RAWINGID));
				//Log.i(TAG, " got rawIngId" + rawIngId);
				////Log.i(TAG,
						//"calculate nurtriton amount for complex consituent ing");
				//Log.i(TAG, " RECURSION CALL");
				calculateNutritionalAmounts(rawIngId, ingAmount);
				Log.i(TAG, " calculated nurtriton amount for complex "
						+ "consituent ing id = " + rawIngId);

			}

		}
		Log.i(TAG, "-------------------------------------------");
	}

	private double cacluateRatioOfIngAmount(double ratio, double ingAmount) {
		Log.i(TAG, "ratio: " + ratio);
		double amount = ratio * ingAmount;
		Log.i(TAG, "amount: " + amount);
		return amount;
	}

	private void displayNutritionalInfo() {

		// TODO Add image for contains nuts/gluten/vegatarian and dairy
		/*
		 * tvCalorieStatus, tvCalorieAmount, tvCaloriePercentage; tvSaltStatus,
		 * tvSaltAmount, tvSaltPercentage; tvSaturatesStatus, tvSaturatesAmount,
		 * tvSaturatesPercentage; tvSugarsStatus, tvSugarsAmount,
		 * tvSugarsPercentage; tvFatStatus, tvFatAmount, tvFatPercentage;
		 * ivCalories, ivFat, ivSalt, ivSugars, ivSaturates; ivVegetarian,
		 * ivNuts, ivDairy, ivGluten;
		 */

		//  make sure deimal place limit
		DecimalFormat df = new DecimalFormat("####0.00");
		
		// calories
		tvCalorieAmount.setText("" + df.format(caloriesSum));
		double caloriePercentage = (caloriesSum / Nutrition
				.getCalorieGuidelineDailyAmount()) * 100;
		Log.i(TAG, "caloriePercentage = " + caloriePercentage);
		tvCaloriePercentage.setText("" + (int) caloriePercentage + "%");
		//tvCalorieStatus.setText(""); // TODO should conver to Kcal? J?
		ivCalories.setImageResource(R.drawable.nutrition_logo_white);

		// sugars
		tvSugarsAmount.setText("" + df.format(sugarsSum) + "g");
		double sugarsPercentage = (sugarsSum / Nutrition
				.getSugarsGuidelineDailyAmount()) * 100;
		Log.i(TAG, "sugarsPercentage = " + sugarsPercentage);
		tvSugarsPercentage.setText("" + (int) sugarsPercentage + "%");
		int sugarStatus = Nutrition.calculateSugarStatus(sugarsPercentage);
		Log.i(TAG, "sugarStatus = " + sugarStatus);
		setImageColourAndWarningStatus(sugarStatus, tvSugarsStatus, ivSugars);

		// salt
		tvSaltAmount.setText("" + df.format(saltSum) + "g");
		double saltPercentage = (saltSum / Nutrition
				.getSaltGuidelineDailyAmount()) * 100;
		Log.i(TAG, "saltPercentage = " + saltPercentage);
		tvSaltPercentage.setText("" + (int) saltPercentage + "%");
		int saltStatus = Nutrition.calculateSaltStatus(saltPercentage);
		Log.i(TAG, "saltStatus = " + saltStatus);
		setImageColourAndWarningStatus(saltStatus, tvSaltStatus, ivSalt);

		// saturates
		tvSaturatesAmount.setText("" + df.format(saturatesSum) + "g");
		double saturatesPercentage = (saturatesSum / Nutrition
				.getSaturatesGuidelineDailyAmount()) * 100;
		Log.i(TAG, "saturatesPercentage = " + saturatesPercentage);
		tvSaturatesPercentage.setText("" + (int) saturatesPercentage + "%");
		int saturatesStatus = Nutrition
				.calculateSaturatesStatus(saturatesPercentage);
		Log.i(TAG, "saturatesStatus = " + saturatesStatus);
		setImageColourAndWarningStatus(saturatesStatus, tvSaturatesStatus,
				ivSaturates);

		// fat
		tvFatAmount.setText("" + df.format(fatSum) + "g");
		double fatPercentage = (fatSum / Nutrition.getFatGuidelineDailyAmount()) * 100;
		Log.i(TAG, "fatPercentage = " + fatPercentage);
		tvFatPercentage.setText("" + (int) fatPercentage + "%");
		int fatStatus = Nutrition.calculateFatStatus(fatPercentage);
		Log.i(TAG, "fatStatus = " + fatStatus);
		setImageColourAndWarningStatus(fatStatus, tvFatStatus, ivFat);

		Log.i(TAG, "finished setting up nutritionalInfo display");

	}

	private void setImageColourAndWarningStatus(int status, TextView tvStatus,
			ImageView ivLogo) {

		if (status == 1) {
			tvStatus.setText("Low");
			ivLogo.setImageResource(R.drawable.nutrition_logo_green);
		}
		if (status == 2) {
			tvStatus.setText("Med");
			ivLogo.setImageResource(R.drawable.nutrition_logo_orange);
		}
		if (status == 3) {
			tvStatus.setText("High");
			ivLogo.setImageResource(R.drawable.nutrition_logo_red);
		}

	}

	// ////////UPDATES////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////
	// checked change listener
	// ////////////////////////////////////////

	private void updateAddonsCheckBoxes() {
		linearLayoutAddons.removeAllViews();
		addAddonsCheckBoxes();
		// readd the views again??

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		position = optionsRadioGroup.indexOfChild(rootView
				.findViewById(checkedId));
		System.out.println("RadioGroup Position = " + position);
		System.out.println("Update addons chaeckboxes");
		updateAddonsCheckBoxes();
		System.out.println("Updte CurrentPrice");
		updatePrice();
		updateNutrionalInfo();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int checkBoxId = buttonView.getId(); // updatePrice();
		System.out.println("Is addon checked");
		System.out.println(isChecked);
		// System.out.println("Addon Checked Id = " + addonCheckBoxId);
		System.out.println("Addon Checked Id = " + checkBoxId);
		if (isChecked) {

			addonCheckBoxChecked[checkBoxId] = true;
			System.out.println("True set to true");
		} else
			addonCheckBoxChecked[checkBoxId] = false;
		System.out.println("False, set to false");

		System.out.println("CheckBox ID pressed = " + checkBoxId);
		System.out.println("CheckBox Checked = "
				+ addonCheckBoxChecked[checkBoxId]);

		updatePrice();
		updateNutrionalInfo();
	}

	private void updateNutrionalInfo() {

		System.out.println("calculating options nutritionAmounts ");
		calculateOptionNutrionalInfo();
		System.out.println("Option Nutrioin amounts calculated");

		System.out.println("calculating Addons nutritionAmounts ");
		calculateAddonNutrionalInfo();
		System.out.println("Addons nutritionAmounts calculated");

		System.out.println("Displayng Nutritional Info");
		displayNutritionalInfo();
		System.out.println("Displayed Nutritional Info");

	}

	// ///////////////////////////////////////////////////////
	// onClick
	// /////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bOrder: {
			System.out.println("order button pressed");

			addMenuItemToCurrentOrder();

			String message = "MenuItem " + menuItemName
					+ " added to Current Order";
			Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
			t.show();

			Intent intent = new Intent(context, MenuActivity.class);
			Log.i(TAG, "Creating new Intent passing sessionId " + sessionId
					+ " and table Id " + tableId);
			//TODO have to swap these round??
			//intent.putExtra("sessionId", sessionId);
			//intent.putExtra("tableId", tableId);
			intent.putExtra("sessionId", tableId);
			intent.putExtra("tableId", sessionId);
			startActivity(intent);

		}
			break;
		case R.id.bBack: {
			System.out.println("back button press");

			Intent intent = new Intent(context, MenuActivity.class);
			Log.i(TAG, "Creating new Intent passing sessionId " + sessionId
					+ " and table Id " + tableId);
			//TODO have to swap these round??
			//intent.putExtra("sessionId", sessionId);
			//intent.putExtra("tableId", tableId);
			intent.putExtra("sessionId", tableId);
			intent.putExtra("tableId", sessionId);
			startActivity(intent);

		}
			break;
		}

	}

	// ///////////////////////////////////////////////////
	// add to current order
	// ///////////////////////////////////////////////

	private void addMenuItemToCurrentOrder() {
		addOptionSelectedToDb();
		addAddonsSelectedToDb();
	}

	private void addAddonsSelectedToDb() {

		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Checked Id" + checkedRadioOption);
		System.out.println("Checked RowId" + optionRowId);
		String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionRowId;
		int addonPrice;
		Cursor cursor = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						where);

		cursor.moveToFirst();
		System.out.println("CURSOR COUNT = " + cursor.getCount());

		for (int i = 0; i < cursor.getCount(); i++) {

			if (addonCheckBoxChecked[i] == true)
				System.out
						.println("Addon checked is True so add to addonsPrice");
			else
				System.out
						.println("Addon checked is False so dont add to addonsPrice");

			if (addonCheckBoxChecked[i] == true) {

				addonPrice = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));
				int optionId = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONID));
				String optionName = cursor.getString(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
				int addonId = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONID));
				String addonName = cursor.getString(cursor
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

				System.out.println("addonPrice = " + addonPrice);

				Log.i(TAG, "Inserting into Temp Order Addons Db");
				Log.i(TAG, "tempOrderId = " + tempOrderId + " menuItemId "
						+ menuItemId);
				Log.i(TAG, "optionId = " + optionId + " optionName "
						+ optionName);
				Log.i(TAG, "addonId = " + addonId + " addonName " + addonName);
				db.insertRowTempOrderAddons(tempOrderId, menuItemId,
						menuItemName, optionId, optionName, addonId, addonName,
						addonPrice);
				Log.i(TAG, "INSERTED into Temp Order Addons Db");

			}

			cursor.moveToNext();

		}

		cursor.close();

	}

	private void addOptionSelectedToDb() {
		
		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Checked Option Id" + checkedRadioOption);
		System.out.println("Checked Option RowId" + optionRowId);
		String where = DatabaseAdapter.KEY_ROWID + "=" + optionRowId;

		Cursor cursor = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);
		if (cursor.getCount() == 0) {
		} else {

			int optionPrice = cursor.getInt(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));
			String optionName = cursor.getString(cursor
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			Log.i(TAG, "Inserting into Temp Order Db");
			Log.i(TAG, "menuItemId = " + menuItemId + " menuItemName "
					+ menuItemName);
			Log.i(TAG, "optionRowId = " + optionRowId + " optionName "
					+ optionName);
			
			tempOrderId = (int) db.insertRowTempOrder(menuItemId, menuItemName,
					optionRowId, optionName, optionPrice);
			Log.i(TAG, "INSERTED into Temp Order Db");

			cursor.close();

		}

	}

	public int getMenuItemId() {
		return menuItemId;
	}

	public String menuItemName() {
		return menuItemName;
	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();

	}

	// /////////////////////////////////////////////////////////////////////

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return false;
		// TODO Auto-generated method stub

	}

}
