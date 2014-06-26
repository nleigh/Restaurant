package uk.co.nathanleigh.restaurant.manager.editMenu;

import java.text.DecimalFormat;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurant.Nutrition;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class EditMenuReviewFragment extends Fragment implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener,
		OnCheckedChangeListener {

	private View rootView;
	private DatabaseAdapter db;
	private Context context;
	private String optionName;
	private int optionPrice;
	private RadioGroup optionsRadioGroup;
	private CheckBox checkBox;

	private LinearLayout linearLayoutAddons;
	private TextView tvMenuItemName, tvItemDescription, tvCurrentPrice, tvSectionName;

	private TextView tvCalorieStatus, tvCalorieAmount, tvCaloriePercentage;
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

	private int position;
	private int addonCheckBoxId = 0;
	private boolean addonCheckBoxChecked[];
	private int totalNumOfAddons = 0;
	private int menuItemId;
	private String itemName;
	private int[] optionIdToRowId;
	private int[] addonIdToRowId;
	private static String editType;
	private static int menuItemIdUpdate;
	private static int sectionId;
	private static String sectionName = "";

	public static final String TAG = "REVIEW";

	// price got from db conver tocurrency, remove £ symbols

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openDb();
		System.out.println("creating revie frgament");

		// TODO better way of getting sectionId, could get lost if menuNames
		// crashes?
		editType = getEditType();
		menuItemIdUpdate = getMenuItemId();
		sectionId = getSectionId();
		Log.i(TAG, "getSectionId = " + sectionId);
		sectionName = getSectionName(sectionId);
		Log.i(TAG, "getSectionName = " + sectionName);

		System.out.println("editTYpe = " + editType);

	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_edit_menu_preview, container, false);

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
		tvMenuItemName = (TextView) rootView.findViewById(R.id.tvMenuItemName);
		tvCurrentPrice = (TextView) rootView.findViewById(R.id.tvCurrentPrice);
		tvSectionName = (TextView) rootView.findViewById(R.id.tvSectionName);
		tvItemDescription = (TextView) rootView.findViewById(R.id.tvItemDescription);
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

		optionsRadioGroup = (RadioGroup) rootView.findViewById(R.id.rgOptions);
		linearLayoutAddons = (LinearLayout) rootView
				.findViewById(R.id.linearLayoutAddons);

		Button bBack = (Button) rootView.findViewById(R.id.bBack);
		Button bNext = (Button) rootView.findViewById(R.id.bNext);

		bBack.setOnClickListener(this);
		bNext.setOnClickListener(this);

		tvSectionName.setText(sectionName);

		optionsRadioGroup.setOnCheckedChangeListener(this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// options addons checkboxes
	// ////////////////////////////////////////////////////////////////////

	private void addMenuItemNameAndDesc() {
		//TODO this the best way, get from db instead?
		String menuItemName = EditMenuNameDiscIngsFragment.menuItemName;
		String menuItemDesc = EditMenuNameDiscIngsFragment.menuItemDesc;
		tvMenuItemName.setText(menuItemName);
		tvItemDescription.setText(menuItemDesc);

	}

	// create

	private void addOptionsRadioButtons() {

		Cursor c = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);

		// TODO max number of options instead?
		optionIdToRowId = new int[100]; // change ths?
		int optionRowId;
		c.moveToFirst();
		int checkFirstButton = 0;

		int i = 1;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

			RadioButton optionRadioButton = new RadioButton(getActivity());

			optionName = c.getString(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));
			optionPrice = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));
			optionRowId = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			optionRadioButton.setText("" + optionName + " " + 
					CurrencyFormat.currencyIntToString(optionPrice));

			optionsRadioGroup.addView(optionRadioButton);

			i = optionRadioButton.getId();
			System.out
					.println("ADDED RADIO OPTION KEY ROW ID = " + optionRowId);
			System.out.println("I value = " + i);

			optionIdToRowId[i] = optionRowId;

			if (checkFirstButton == 0) {
				// check changed gets called here?
				optionsRadioGroup.check(optionRadioButton.getId());
				checkFirstButton++;
			}

		}

		c.close();

	}

	private void addAddonsCheckBoxes() {

		Cursor cAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);

		cAddons.moveToFirst();
		// added 3 just to make the array bigger
		addonIdToRowId = new int[cAddons.getCount() + 3];

		String addonName;
		int addonPrice;
		int addonKeyRowId;

		System.out.println("OPTION ID SELECTED ID "
				+ optionsRadioGroup.getCheckedRadioButtonId());
		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();

		addonCheckBoxId = 0;
		System.out.println("Iterate and print addons from addon table");

		int checkedOptionRowId = optionIdToRowId[checkedRadioOption];
		System.out.println("Checked ROWId " + checkedOptionRowId);

		String where = DatabaseAdapter.KEY_OPTIONID + "=" + checkedOptionRowId;

		System.out.println("Got MYQUEY");

		Cursor cursorAddonIngAmount = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						where);

		int i = 0;

		for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons
				.moveToNext()) {

			checkBox = new CheckBox(getActivity());

			addonName = cAddons.getString(cAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

			addonKeyRowId = cAddons.getInt(cAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

			addonPrice = cursorAddonIngAmount.getInt(cursorAddonIngAmount
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

			cursorAddonIngAmount.moveToNext();

			System.out.println("Got addonPrice from MYQUEY");

			System.out.println("Option Id = " + checkedRadioOption);
			System.out.println("Addon Name = " + addonName + " AddonPrice = "
					+ addonPrice);

			checkBox.setText(addonName + " " 
					+ CurrencyFormat.currencyIntToString(addonPrice));

			linearLayoutAddons.addView(checkBox);

			int checkedRadioId = optionsRadioGroup.getCheckedRadioButtonId();
			System.out.println("Checked radio option" + checkedRadioId);

			System.out.println("Check box check for true");

			if (addonCheckBoxChecked[addonCheckBoxId] == true) {
				checkBox.setChecked(true);
				System.out.println("Checkbox was true so set to true");
			}

			addonIdToRowId[i] = addonKeyRowId;

			checkBox.setId(addonCheckBoxId);
			checkBox.setOnCheckedChangeListener(this);

			i++;
			addonCheckBoxId++;
			totalNumOfAddons = addonCheckBoxId;
		}
		cAddons.close();
		cursorAddonIngAmount.close();
		System.out.println("ADDED ADDONS");

	}
	
	// set up addons boxes to false
	private void addonCheckBoxesCheckedToFalse() {
		Cursor c = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
		totalNumOfAddons = c.getCount();
		addonCheckBoxChecked = new boolean[totalNumOfAddons];
		c.close();
		System.out.println("Number of addons = " + totalNumOfAddons);

		for (int i = 0; i < totalNumOfAddons; i++) {
			addonCheckBoxChecked[addonCheckBoxId] = false;
		}

	}

	private int getAddonsCheckedPrice() {

		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		int optionKeyRowId = optionIdToRowId[checkedRadioOption];

		String where = DatabaseAdapter.KEY_OPTIONID + "=" + optionKeyRowId;
		int addonPrice = 0;
		int addonsPrice = 0;
		Cursor cursor = db
				.myDBQuery(
						DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
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
				System.out.println("current addonsPrice = " + addonsPrice);

				addonsPrice += addonPrice;
				System.out.println("new addonsPrice runing total = "
						+ addonsPrice);
			}

			cursor.moveToNext();
		}

		cursor.close();
		return addonsPrice;

	}

	// update

	private int getOptionSelectedPrice() {

		int checkedRadioOption = optionsRadioGroup.getCheckedRadioButtonId();
		// System.out.println("Checked Id" + checkedId);optionIdToRowId
		String where = DatabaseAdapter.KEY_ROWID + "="
				+ optionIdToRowId[checkedRadioOption];

		Cursor cursor = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS, where);
		if (cursor.getCount() == 0)
			return 0;
		int optionPrice = cursor.getInt(cursor
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));

		cursor.close();
		// if (optionPriceStr = null)
		// System.out.println("Option Price = NULL");
		return optionPrice;
	}

	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	// NUTRITION
	/////////////////////////////////////////////////////////////////////////////////
	
	
	
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
						DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
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
						DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
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
				System.out.println("calculated nutrition AMounts added to sum");

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
			saltSum += cacluateRatioOfIngAmount( saltRatio, ingAmount);
			Log.i(TAG, "saltSum: " + saltSum);
			
			Log.i(TAG, "current calorieSum: " + caloriesSum);
			caloriesSum += cacluateRatioOfIngAmount( calorieRatio, ingAmount);
			Log.i(TAG, "caloriesSum: " + caloriesSum);
			
			Log.i(TAG, "current saturatesSum: " + saturatesSum);
			saturatesSum += cacluateRatioOfIngAmount( saturatesRatio, ingAmount);
			Log.i(TAG, "saturatesSum: " + saturatesSum);
			
			Log.i(TAG, "current sugarsSum: " + sugarsSum);
			sugarsSum += cacluateRatioOfIngAmount( sugarsRatio, ingAmount);
			Log.i(TAG, "sugarsSum: " + sugarsSum);
			
			Log.i(TAG, "current fatSum: " + fatSum);
			fatSum += cacluateRatioOfIngAmount( fatRatio, ingAmount);
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
				Log.i(TAG, " got rawIngId" + rawIngId);
				Log.i(TAG,"calculate nurtriton amount for complex consituent ing");
				Log.i(TAG," RECURSION CALL");
				calculateNutritionalAmounts(rawIngId, ingAmount);
				Log.i(TAG, " calculated nurtriton amount for complex "
						+ "consituent ing id = " + rawIngId);

			}

		}
		Log.i(TAG, "-------------------------------------------");
	}

	private double cacluateRatioOfIngAmount(
			double ratio, double ingAmount) {
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
		tvCalorieAmount.setText("" + df.format(caloriesSum) );
		double caloriePercentage = (caloriesSum / Nutrition
				.getCalorieGuidelineDailyAmount()) * 100;
		Log.i(TAG, "caloriePercentage = " + caloriePercentage);
		tvCaloriePercentage.setText("" + (int) caloriePercentage + "%");
		tvCalorieStatus.setText(""); //TODO should conver to Kcal? J?
		ivCalories.setImageResource(R.drawable.nutrition_logo_white);
		
		//sugars
		tvSugarsAmount.setText("" + df.format(sugarsSum) + "g");
		double sugarsPercentage = (sugarsSum / Nutrition
				.getSugarsGuidelineDailyAmount()) * 100;
		Log.i(TAG, "sugarsPercentage = " + sugarsPercentage);
		tvSugarsPercentage.setText("" + (int) sugarsPercentage + "%");
		int sugarStatus = Nutrition.calculateSugarStatus(sugarsPercentage);
		Log.i(TAG, "sugarStatus = " + sugarStatus);
		setImageColourAndWarningStatus(sugarStatus, tvSugarsStatus, ivSugars);
		
		
		//salt
		tvSaltAmount.setText("" + df.format(saltSum) + "g");
		double saltPercentage = (saltSum / Nutrition
				.getSaltGuidelineDailyAmount()) * 100;
		Log.i(TAG, "saltPercentage = " + saltPercentage);
		tvSaltPercentage.setText("" + (int) saltPercentage + "%");
		int saltStatus = Nutrition.calculateSaltStatus(saltPercentage);
		Log.i(TAG, "saltStatus = " + saltStatus);
		setImageColourAndWarningStatus(saltStatus, tvSaltStatus, ivSalt);
		
		
		//saturates
		tvSaturatesAmount.setText("" + df.format(saturatesSum) + "g");
		double saturatesPercentage = (saturatesSum / Nutrition
				.getSaturatesGuidelineDailyAmount()) * 100;
		Log.i(TAG, "saturatesPercentage = " + saturatesPercentage);
		tvSaturatesPercentage.setText("" + (int) saturatesPercentage + "%");
		int saturatesStatus = Nutrition.calculateSaturatesStatus(saturatesPercentage);
		Log.i(TAG, "saturatesStatus = " + saturatesStatus);
		setImageColourAndWarningStatus(saturatesStatus, tvSaturatesStatus, ivSaturates);
		
		
		//fat
		tvFatAmount.setText("" + df.format(fatSum) + "g");
		double fatPercentage = (fatSum / Nutrition
				.getFatGuidelineDailyAmount()) * 100;
		Log.i(TAG, "fatPercentage = " + fatPercentage);
		tvFatPercentage.setText("" + (int) fatPercentage + "%");
		int fatStatus = Nutrition.calculateFatStatus(fatPercentage);
		Log.i(TAG, "fatStatus = " + fatStatus);
		setImageColourAndWarningStatus(fatStatus, tvFatStatus, ivFat);
		
		Log.i(TAG, "finished setting up nutritionalInfo display");

	}

	private void setImageColourAndWarningStatus(int status,
			TextView tvStatus, ImageView ivLogo) {
		
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
	
	
	
	
	
	
	// ////////////////////////////////////////////////////////////////////////////
	// CHeckedChanged
	// //////////////////////////////////////////////////////////////////

	private void updateAddonsCheckBoxes() {
		linearLayoutAddons.removeAllViews();
		addAddonsCheckBoxes();
		// readd the views again??

	}

	// options button select
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

	// addons checkBoxs
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

	// //////////////////////////////////////////////////////////////////////////////////
	// price
	// ///////////////////////////////////////////////////////////

	private void addCurrentPrice() {

		int optionPrice = getOptionSelectedPrice();
		System.out.println("Option Price = " + optionPrice);
		tvCurrentPrice.setText("Current Price: "
				+ CurrencyFormat.currencyIntToString(optionPrice));
	}

	private void updatePrice() {
		int optionPrice = getOptionSelectedPrice();
		System.out.println("Option Price to update = " + optionPrice);
		int addonsPrice = getAddonsCheckedPrice();
		System.out.println("Addons Price to update = " + addonsPrice);
		int currentPrice = optionPrice + addonsPrice;
		System.out.println("CURRENT Price to update = " + currentPrice);

		tvCurrentPrice.setText("Current Price:  "
				+ CurrencyFormat.currencyIntToString(currentPrice));

	}

	private int getMenuItemId() {
		return EditMenuNameDiscIngsFragment.menuItemId;
	}

	private String getEditType() {
		return EditMenuNameDiscIngsFragment.editType;
	}

	private int getSectionId() {

		return EditMenuNameDiscIngsFragment.sectionId;
	}

	private String getSectionName(int sectionIdArg) {
		Cursor c = db.getRow(DatabaseAdapter.DATABASE_TABLE_SECTIONS,
				(long) sectionIdArg);
		String sectName = c.getString(c
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_SECTIONNAME));
		return sectName;
	}

	// ///////////////////////////////////////////////////////////////////////////////
	// OnButtonClick
	// ////////////////////////////////////////////////////////////////////////

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bNext: {
			System.out.println("next button pressed");

			addMenuItemToDb();

			Intent intent = new Intent(context, ManagerActivity.class);
			startActivity(intent);

		}
			break;
		case R.id.bBack: {
			System.out.println("back button press");

			EditMenuAddonsIngsAmountFragment optionsFrag = (EditMenuAddonsIngsAmountFragment) getFragmentManager()
					.findFragmentByTag(EditMenuAddonsIngsAmountFragment.TAG);

			getFragmentManager().beginTransaction()
					.replace(R.id.container, optionsFrag).addToBackStack(null)
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.commit();

		}
			break;
		}

	}

	
	// //////////////////////////////////////////////////////
///Adding menuItem from temp tables to real tables      //////
	///////////////////////////////////////////////////////////
	
	
	private void addMenuItemToDb() {

		System.out.println("Adding MenuName and Disc");
		addMenuItemNameAndDiscToDb();

		System.out.println("Adding menuItem Ings");
		addMenuItemIngsToDb();

		System.out.println("Adding MenuItem Options");
		addMenuItemOptionsToDb();

		System.out.println("Adding MenuItem addons");
		addMenuItemAddonsToDb();

		System.out.println("Adding MenuItem Option ings amount");
		addMenuItemOptionsIngsToDb();

		System.out
				.println("Adding MenuItem addong ing and price amount for options");
		addMenuItemAddonsIngsPriceToDb();

		System.out.println("Finished adding menuItem to Db");

		System.out.println("Adding MenuItem to a Section");
		addMenuItemToSection();

		deleteTempDb();

	}



	private void addMenuItemNameAndDiscToDb() {

		// String sectionName = "test";
		itemName = EditMenuNameDiscIngsFragment.menuItemName;
	
		
		String itemDesc = "Desciption Test";

		if (editType.equals("create")) {

			System.out.println("Inserting into MenuItem Table");

			System.out.println("ItemName " + itemName + " Item Desc "
					+ itemDesc);
			System.out.println("====================================");

			menuItemId = (int) db.insertRowMenuItem(sectionId, sectionName,
					itemName, itemDesc);
			System.out.println("Menu ITEM ID = " + menuItemId);
		} else if (editType.equals("update")) {

			System.out.println("Updating MenuItem Table");

			System.out.println("ItemName " + itemName + " Item Desc "
					+ itemDesc);
			System.out.println("====================================");

			db.updateRowMenuItem(menuItemIdUpdate, sectionId, sectionName,
					itemName, itemDesc);

			System.out.println("Menu ITEM ID UPDATED = " + menuItemIdUpdate);
		}
		
	// description save to db
		
		Cursor cDesc = db.getAllRows(DatabaseAdapter.
				DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
		Log.i(TAG, "cDesc Count = " + cDesc.getCount());
		
		String desc = "No Description";
		String image = "No Image";
		
		if(cDesc.getCount() != 0){
		// first item in cursor should contain info
		desc = cDesc.getString(cDesc
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_ITEMDESC));
		image = cDesc.getString(cDesc
				.getColumnIndexOrThrow(DatabaseAdapter.KEY_IMAGETITLE));
		Log.i(TAG, "desc = " + desc);
		Log.i(TAG, "image = " + image);
		Log.i(TAG, "inserting into menuItemDesc db");
		}
		
		db.insertRowMenuItemDesc(menuItemId, image, desc);
		
		cDesc.close();
	}

	private void addMenuItemIngsToDb() {

		Cursor cTempItemIngs = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		Log.i(TAG, "cTempItemIngs Count = " + cTempItemIngs);
		int ingId;
		String ingName;
		// if update delete data in table, insert new data
		if (editType.equals("update")) {
			System.out
					.println("Update: deleting old record in menu item ings table");

			String where = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cItemIngs = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_INGREDIENTS,
							where);

			for (cItemIngs.moveToFirst(); !cItemIngs.isAfterLast(); cItemIngs
					.moveToNext()) {
				long rowId = cItemIngs.getLong(cItemIngs
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				db.deleteRow(
						DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_INGREDIENTS,
						rowId);

			}
			cItemIngs.close();
		}

		for (cTempItemIngs.moveToFirst(); !cTempItemIngs.isAfterLast(); cTempItemIngs
				.moveToNext()) {

			ingId = cTempItemIngs.getInt(cTempItemIngs
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			ingName = cTempItemIngs.getString(cTempItemIngs
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
			System.out.println("Inseting into MenuItemIngs Table");
			System.out.println("MenuItem Id " + menuItemId + " MenuItemName "
					+ itemName);
			System.out.println("Ing Id " + ingId + " IngName " + ingName);
			System.out.println("---------------------------------------");
			if (editType.equals("create")) {
				db.insertRowMenuItemIngs(menuItemId, itemName, ingId, ingName);
			}
			if (editType.equals("update")) {
				db.insertRowMenuItemIngs(menuItemIdUpdate, itemName, ingId,
						ingName);
			}
		}
		System.out.println("======================================");

		cTempItemIngs.close();
	}

	private void addMenuItemOptionsToDb() {

		Cursor cTempOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
		String optionNameArg;
		int optionPrice;

		// if update delete data in table, insert new data
		if (editType.equals("update")) {
			System.out
					.println("Update: deleting old record in menu item options table");

			String where = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cOptions = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);

			for (cOptions.moveToFirst(); !cOptions.isAfterLast(); cOptions
					.moveToNext()) {
				long rowId = cOptions.getLong(cOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				db.deleteRow(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS,
						rowId);

			}
			cOptions.close();
		}

		for (cTempOptions.moveToFirst(); !cTempOptions.isAfterLast(); cTempOptions
				.moveToNext()) {

			optionPrice = cTempOptions.getInt(cTempOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONPRICE));
			optionNameArg = cTempOptions.getString(cTempOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			System.out.println("Inseting into MenuItemOptions Table");
			System.out.println("MenuItem Id " + menuItemId + " MenuItemName "
					+ itemName);
			System.out.println("OptionName " + optionNameArg + " optionPrice "
					+ optionPrice);

			if (editType.equals("create")) {
				db.insertRowMenuItemOptions(menuItemId, itemName,
						optionNameArg, optionPrice);
			}
			if (editType.equals("update")) {
				db.insertRowMenuItemOptions(menuItemIdUpdate, itemName,
						optionNameArg, optionPrice);
			}

			System.out.println("---------------------------------------");
		}
		cTempOptions.close();
		System.out.println("================================");
	}

	private void addMenuItemAddonsToDb() {

		Cursor cTempAddons = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
		String addonName;
		String ingName;
		int ingId;

		// if update delete data in table, insert new data
		if (editType.equals("update")) {
			System.out
					.println("Update: deleting old record in menu item addons table");

			String where = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cAddons = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);

			for (cAddons.moveToFirst(); !cAddons.isAfterLast(); cAddons
					.moveToNext()) {
				long rowId = cAddons.getLong(cAddons
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				db.deleteRow(DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS,
						rowId);

			}
			cAddons.close();
		}

		for (cTempAddons.moveToFirst(); !cTempAddons.isAfterLast(); cTempAddons
				.moveToNext()) {

			addonName = cTempAddons.getString(cTempAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));
			ingId = cTempAddons.getInt(cTempAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			ingName = cTempAddons.getString(cTempAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));

			System.out.println("Inseting into MenuItemAddons Table");
			System.out.println("MenuItem Id " + menuItemId + " MenuItemName "
					+ itemName);
			System.out.println("AddonName " + addonName + " Ing Id " + ingId
					+ " ingName = " + ingName);

			if (editType.equals("create")) {
				db.insertRowMenuItemAddons(menuItemId, itemName, addonName,
						ingId, ingName);
			}
			if (editType.equals("update")) {
				db.insertRowMenuItemAddons(menuItemIdUpdate, itemName,
						addonName, ingId, ingName);
			}
			System.out.println("---------------------------------------");
		}
		cTempAddons.close();
		System.out.println("================================");

	}

	private void addMenuItemOptionsIngsToDb() {
		Cursor cTempIngAmountOptions = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);

		Cursor cTempIngsMenuItem = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		int numOfIngs = cTempIngsMenuItem.getCount();
		cTempIngsMenuItem.close();

		// if update delete data in table, insert new data
		if (editType.equals("update")) {
			System.out.println("Update: deleting old record in menu item ing"
					+ "amount for options table");

			String where = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cIngAmountOptions = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
							where);

			for (cIngAmountOptions.moveToFirst(); !cIngAmountOptions
					.isAfterLast(); cIngAmountOptions.moveToNext()) {

				long rowId = cIngAmountOptions.getLong(cIngAmountOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				db.deleteRow(
						DatabaseAdapter.DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
						rowId);

			}
			cIngAmountOptions.close();
		}

		String ingName;
		int ingId;
		int optionId;
		int ingAmount;
		String where = null;

		if (editType.equals("create")) {
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		}
		if (editType.equals("update")) {
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemIdUpdate;
		}

		Cursor cOptions = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);

		System.out.println("Cursor temp ing amounts options count = "
				+ cTempIngAmountOptions.getCount());
		System.out.println("Cursor  options count = " + cOptions.getCount());

		int ingsAddedForOptionCounter = 0;

		for (cTempIngAmountOptions.moveToFirst(); !cTempIngAmountOptions
				.isAfterLast(); cTempIngAmountOptions.moveToNext()) {
			System.out.println("GETTING optiobnsId /name");
			// need to get option id that was inserted before, not from temp
			// table
			optionId = cOptions.getInt(cOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));

			optionName = cOptions.getString(cOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			ingId = cTempIngAmountOptions.getInt(cTempIngAmountOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			ingName = cTempIngAmountOptions.getString(cTempIngAmountOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
			ingAmount = Integer
					.parseInt(cTempIngAmountOptions.getString(cTempIngAmountOptions
							.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT)));

			System.out.println("Inseting into MenuItemAddons Table");
			System.out.println("MenuItem Id " + menuItemId + " MenuItemName "
					+ itemName);
			System.out.println("Option Id " + optionId + " OptionName "
					+ optionName + " Ing Id " + ingId + " ingName = " + ingName
					+ " ingAmount " + ingAmount);

			if (editType.equals("create")) {
				db.insertRowIngAmountForMenuItemOption(menuItemId, itemName,
						optionId, optionName, ingId, ingName, ingAmount);
			}
			if (editType.equals("update")) {
				db.insertRowIngAmountForMenuItemOption(menuItemIdUpdate,
						itemName, optionId, optionName, ingId, ingName,
						ingAmount);
			}

			ingsAddedForOptionCounter++;
			if (ingsAddedForOptionCounter == numOfIngs) {
				System.out.println("moving c option to next");
				cOptions.moveToNext();
				ingsAddedForOptionCounter = 0;
			}

			System.out.println("---------------------------------------");
		}
		cTempIngAmountOptions.close();
		cOptions.close();
		System.out.println("================================");

	}

	private void addMenuItemAddonsIngsPriceToDb() {
		Cursor c = db
				.getAllRows(DatabaseAdapter.DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
		String addonName;
		String ingName;
		int ingId;
		int optionId;
		int addonAmount;
		int addonPrice;
		int addonId;
		String where = null;

		// if update delete data in table, insert new data
		if (editType.equals("update")) {
			System.out.println("Update: deleting old record in addon item ing"
					+ "amount and price for options table");

			String whereUpdate = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cIngAmountOptions = db
					.myDBQuery(
							DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
							whereUpdate);

			for (cIngAmountOptions.moveToFirst(); !cIngAmountOptions
					.isAfterLast(); cIngAmountOptions.moveToNext()) {

				long rowId = cIngAmountOptions.getLong(cIngAmountOptions
						.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
				db.deleteRow(
						DatabaseAdapter.DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						rowId);

			}
			cIngAmountOptions.close();
		}

		if (editType.equals("create")) {
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemId;
		}
		if (editType.equals("update")) {
			where = DatabaseAdapter.KEY_MENUITEMID + "=" + menuItemIdUpdate;
		}

		Cursor cOptions = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_OPTIONS, where);

		Cursor cAddons = db.myDBQuery(
				DatabaseAdapter.DATABASE_TABLE_MENU_ITEM_ADDONS, where);

		int numOfAddons = cAddons.getCount();

		System.out.println("Cursor temp ing amounts addons count = "
				+ c.getCount());
		System.out.println("Cursor  options count = " + cOptions.getCount());
		System.out.println("Cursor  addons count = " + cAddons.getCount());

		int addonsCounter = 0;

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			System.out.println("GETTING OptionId /name");

			optionId = cOptions.getInt(cOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			optionName = cOptions.getString(cOptions
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_OPTIONNAME));

			System.out.println("GETTING addonId /name");

			addonId = cAddons.getInt(cAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			addonName = cAddons.getString(cAddons
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONNAME));

			ingId = c
					.getInt(c.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
			ingName = c.getString(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME));
			addonAmount = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONAMOUNT));
			addonPrice = c.getInt(c
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ADDONPRICE));

			System.out.println("Inseting into MenuItemAddons Table");
			System.out.println("MenuItem Id " + menuItemId + " MenuItemName "
					+ itemName);
			System.out.println("Option Id " + optionId + " OptionName "
					+ optionName);
			System.out.println("Addon Id " + addonId + " AddonName "
					+ addonName);
			System.out.println(" Ing Id " + ingId + " ingName = " + ingName
					+ " addonAmount = " + addonAmount + " addonPrice = "
					+ addonPrice);

			if (editType.equals("create")) {
				db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
						itemName, optionId, optionName, addonId, addonName,
						ingId, ingName, addonAmount, addonPrice);
			}
			if (editType.equals("update")) {
				db.insertRowAddonIngAmountPriceForMenuItemOption(
						menuItemIdUpdate, itemName, optionId, optionName,
						addonId, addonName, ingId, ingName, addonAmount,
						addonPrice);
			}

			addonsCounter++;

			cAddons.moveToNext();
			if (addonsCounter == numOfAddons) {

				cAddons.moveToFirst();

				addonsCounter = 0;
				cOptions.moveToNext();
			}

			System.out.println("---------------------------------------");
		}
		c.close();
		cAddons.close();
		cOptions.close();
		System.out.println("================================");

	}

	private void addMenuItemToSection() {

		if (editType.equals("create")) {
			db.insertRowSectionMenuItems(sectionId, sectionName, menuItemId,
					itemName);
		}
		if (editType.equals("update")) {

			String where = DatabaseAdapter.KEY_MENUITEMID + "="
					+ menuItemIdUpdate;
			Cursor cSectionMenuItems = db.myDBQuery(
					DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS, where);
			long rowId = cSectionMenuItems.getLong(cSectionMenuItems
					.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID));
			db.updateRowSectionMenuItems(rowId, sectionId, sectionName,
					menuItemIdUpdate, itemName);

		}
	}
	
	/////////////////////////////////////////////////
	
	private void deleteTempDb() {
		openDb();
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

}
