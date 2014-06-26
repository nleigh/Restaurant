package uk.co.nathanleigh.restaurant.manager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import uk.co.nathanleigh.restaurant.CurrencyFormat;
import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManagerSettingsActivity extends Activity implements
		OnClickListener {

	private static final String TAG = "MANAGER_SETTINGS_ACTIVITY";
	private DatabaseAdapter db;
	private Button bBack;
	private Button bConfirm;
	private Button bResetIngs;
	private EditText etNumOfTables;
	private double price;

	private int ingAmount = 30000;
	private int recIngAmount = 50000;
	private int warningIngAmount = 10000;

	private String ingName;
	private String ingUnit;
	private double calories;
	private double sugars;
	private double fat;
	private double saturates;
	private double salt;
	private int vegetarian;
	private int containsNuts;
	private int containsGluten;
	private int dairy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manager_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		openDB();

		etNumOfTables = (EditText) findViewById(R.id.etNumOfTables);
		// int numOfTables = getNumOfTables();
		// etNumOfTables.setText("" + numOfTables);

		bBack = (Button) findViewById(R.id.bBack);
		bConfirm = (Button) findViewById(R.id.bConfirm);
		bBack.setOnClickListener(this);
		bConfirm.setOnClickListener(this);

		bResetIngs = (Button) findViewById(R.id.bResetIngs);
		bResetIngs.setOnClickListener(this);
		// TODO set et with value from db

	}

	private int getNumOfTables() {
		// TODO get number of tables from DB
		return 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bBack: {
			Log.i(TAG, " Backbutton pressed");

			Intent intent = new Intent(this, ManagerActivity.class);
			startActivity(intent);
		}
			break;

		case R.id.bConfirm: {
			Log.i(TAG, " Confirm pressed");

			updateSettings();

			Intent intent = new Intent(this, ManagerActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.bResetIngs: {
			Log.i(TAG, " bResetIngs pressed");

			resetIngs();
			resetMenu();

			Intent intent = new Intent(this, ManagerActivity.class);
			startActivity(intent);
		}
			break;
		}

	}

	private void resetMenu() {
		// create sections

	}

	private void resetIngs() {

		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_INGREDIENTS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_STOCK_LEVELS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_SECTION_MENU_ITEMS);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_SECTIONS);

		ingName = "TOMATO";
		ingUnit = "g";
		calories = 18;
		sugars = 2.6;
		fat = 0.2;
		saturates = 0;
		salt = 0.005;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int tomatoId = addIng();
		// ////////////////////////////////////////
		ingName = "APPLE";
		ingUnit = "g";
		calories = 52;
		sugars = 10.4;
		fat = 0.2;
		saturates = 0;
		salt = 0.001;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int appleId = addIng();
		// ////////////////////////////////////////
		ingName = "BREAD";
		ingUnit = "g";
		calories = 289;
		sugars = 2.6;
		fat = 1.8;
		saturates = 0.5;
		salt = 0.513;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int breadId = addIng();
		// ////////////////////////////////////////
		ingName = "CHEESE";
		ingUnit = "g";
		calories = 371;
		sugars = 2.3;
		fat = 32;
		saturates = 18;
		salt = 1.671;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 1;
		int cheeseId = addIng();
		// ////////////////////////////////////////
		ingName = "RICE";
		ingUnit = "g";
		calories = 111;
		sugars = 0.4;
		fat = 0.9;
		saturates = 0.2;
		salt = 0.005;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 1;
		dairy = 0;
		int riceId = addIng();
		// ////////////////////////////////////////
		ingName = "ONION";
		ingUnit = "g";
		calories = 40;
		fat = 0.1;
		saturates = 0;
		salt = 0.004;
		sugars = 4.2;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 1;
		int onionId = addIng();
		// ////////////////////////////////////////
		ingName = "CHICKEN";
		ingUnit = "g";
		calories = 172;
		fat = 9;
		saturates = 2.7;
		salt = 0.063;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int chickenId = addIng();

		// ////////////////////////////////////////
		ingName = "BEEF";
		ingUnit = "g";
		calories = 250;
		fat = 15;
		saturates = 6;
		salt = 0.072;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int beefId = addIng();

		// ////////////////////////////////////////
		ingName = "PORK";
		ingUnit = "g";
		calories = 173;
		fat = 9;
		saturates = 2.9;
		salt = 0.744;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int porkId = addIng();

		// ////////////////////////////////////////
		ingName = "POTATO";
		ingUnit = "g";
		calories = 77;
		fat = 0.1;
		saturates = 0;
		salt = 0.006;
		sugars = 0.8;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int potatoId = addIng();

		// ////////////////////////////////////////
		ingName = "EGG";
		ingUnit = "g";
		calories = 155;
		fat = 11;
		saturates = 3.3;
		salt = 0.124;
		sugars = 1.1;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int eggId = addIng();

		// ////////////////////////////////////////
		ingName = "FLOUR";
		ingUnit = "g";
		calories = 366;
		fat = 1.4;
		saturates = 0.4;
		salt = 0;
		sugars = 0.1;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int flourId = addIng();

		// ////////////////////////////////////////
		ingName = "BUTTER";
		ingUnit = "g";
		calories = 717;
		fat = 88;
		saturates = 51;
		salt = 0.011;
		sugars = 0.1;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int butterId = addIng();
		// ////////////////////////////////////////

		ingName = "PEPPERONI";
		ingUnit = "g";
		calories = 494;
		fat = 44;
		saturates = 15;
		salt = 1.761;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int pepperoniId = addIng();

		// ////////////////////////////////////////

		ingName = "MUSHROOM";
		ingUnit = "g";
		calories = 38;
		fat = 0.5;
		saturates = 0;
		salt = 0.009;
		sugars = 1.2;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int mushroomId = addIng();

		ingName = "HAM";
		ingUnit = "g";
		calories = 145;
		fat = 6;
		saturates = 1.8;
		salt = 1.203;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int hamId = addIng();

		ingName = "PINEAPPLE";
		ingUnit = "g";
		calories = 50;
		fat = 0.1;
		saturates = 0;
		salt = 0.001;
		sugars = 10;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int pineappleId = addIng();

		ingName = "MILK";
		ingUnit = "g";
		calories = 42;
		fat = 1;
		saturates = 0.6;
		salt = 0.044;
		sugars = 5;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 1;
		int milkId = addIng();

		ingName = "PEAS";
		ingUnit = "g";
		calories = 81;
		fat = 0.4;
		saturates = 0.1;
		salt = 0.005;
		sugars = 6;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int peasId = addIng();

		ingName = "STEAK";
		ingUnit = "g";
		calories = 277;
		fat = 20;
		saturates = 9;
		salt = 0.071;
		sugars = 0;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int steakId = addIng();

		ingName = "PASTRY";
		ingUnit = "g";
		calories = 100;
		fat = 22;
		saturates = 7;
		salt = 0.417;
		sugars = 7;
		vegetarian = 0;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int pastryId = addIng();

		ingName = "LETTUCE";
		ingUnit = "g";
		calories = 15;
		fat = 0.2;
		saturates = 0;
		salt = 0.028;
		sugars = 0.8;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int lettuceId = addIng();
		
		ingName = "CURRY POWDER";
		ingUnit = "g";
		calories = 325;
		fat = 14;
		saturates = 2.2;
		salt = 0.052;
		sugars = 2.8;
		vegetarian = 1;
		containsNuts = 0;
		containsGluten = 0;
		dairy = 0;
		int curryPowderId = addIng();

		// TODO add ings

		// ////////////////////////////////////////////////////////////////

		String optionName;
		String addonName;
		int optionId;
		int addonId;
		String menuItemName;
		String itemDesc;
		String image = "NO IMAGE";

		String pizzaSectionName = "Pizzas";
		String pubSectionName = "Pub Food";
		String indianSectionName = "Indian";
		String cocktailsSectionName = "Cocktails";

		// insert sections
		int pizzaSectionId = (int) db.insertRowSections(pizzaSectionName);
		int pubSectionId = (int) db.insertRowSections(pubSectionName);
		int indianSectionId = (int) db.insertRowSections(indianSectionName);
		int cocktailsSectionId = (int) db
				.insertRowSections(cocktailsSectionName);

		// insert menuItem
		menuItemName = "Cheese and Tomato";
		itemDesc = "Margheritta";
		int pizzaId = (int) db.insertRowMenuItem(pizzaSectionId,
				pizzaSectionName, menuItemName, "Margheritta");

		int menuItemId = pizzaId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, cheeseId, "CHEESE");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, tomatoId, "TOMATO");

		// addon is for all options
		addonName = "Cheese Stuffed Crust";
		addonId = (int) db.insertRowMenuItemAddons(menuItemId, menuItemName,
				addonName, cheeseId, menuItemName);

		// adding option and amounts
		optionName = "Small";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 30);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 6);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 5, 500);

		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 40);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 7);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 8);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 6, 600);

		// adding option and amounts
		optionName = "Large";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 50);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 9);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 7, 70);

		db.insertRowSectionMenuItems(pizzaSectionId, pizzaSectionName,
				menuItemId, menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Pepperoni Pizza";
		itemDesc = "Spicy Pepperoni Pizza";
		pizzaId = (int) db.insertRowMenuItem(pizzaSectionId, pizzaSectionName,
				menuItemName, itemDesc);

		menuItemId = pizzaId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, cheeseId, "CHEESE");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, tomatoId, "TOMATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, pepperoniId,
				"PEPPERONI");

		// addon is for all options
		addonName = "Cheese Stuffed Crust";
		addonId = (int) db.insertRowMenuItemAddons(menuItemId, menuItemName,
				addonName, cheeseId, menuItemName);

		// adding option and amounts
		optionName = "Small";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 6);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pepperoniId, "PEPPERONI", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 5, 500);

		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 20);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 7);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pepperoniId, "PEPPERONI", 5);

		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 6, 600);

		// adding option and amounts
		optionName = "Large";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 25);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 9);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pepperoniId, "PEPPERONI", 6);

		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 7, 70);

		db.insertRowSectionMenuItems(pizzaSectionId, pizzaSectionName,
				menuItemId, menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Ham and Pineapple Pizza";
		itemDesc = "Delicious Ham and Pineapple Pizza";
		pizzaId = (int) db.insertRowMenuItem(pizzaSectionId, pizzaSectionName,
				menuItemName, itemDesc);

		menuItemId = pizzaId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, cheeseId, "CHEESE");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, tomatoId, "TOMATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, hamId, "HAM");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, pineappleId,
				"PINEAPPLE");

		// addon is for all options
		addonName = "Cheese Stuffed Crust";
		addonId = (int) db.insertRowMenuItemAddons(menuItemId, menuItemName,
				addonName, cheeseId, menuItemName);

		// adding option and amounts
		optionName = "Small";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 6);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, hamId, "HAM", 4);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pineappleId, "PINEAPPLE", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 5, 500);

		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 20);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 7);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, hamId, "HAM", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pineappleId, "PINEAPPLE", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 6, 600);

		// adding option and amounts
		optionName = "Large";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 25);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 9);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, hamId, "HAM", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pineappleId, "PINEAPPLE", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 7, 70);

		db.insertRowSectionMenuItems(pizzaSectionId, pizzaSectionName,
				menuItemId, menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Chicken Pizza";
		itemDesc = "Filling Chicken Pizza";
		pizzaId = (int) db.insertRowMenuItem(pizzaSectionId, pizzaSectionName,
				menuItemName, itemDesc);

		menuItemId = pizzaId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, cheeseId, "CHEESE");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, tomatoId, "TOMATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, chickenId, "CHICKEN");

		// addon is for all options
		addonName = "Cheese Stuffed Crust";
		addonId = (int) db.insertRowMenuItemAddons(menuItemId, menuItemName,
				addonName, cheeseId, menuItemName);

		// adding option and amounts
		optionName = "Small";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 6);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 5, 500);

		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 20);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 7);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 5);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 6, 600);

		// adding option and amounts
		optionName = "Large";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 25);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 9);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, addonId, addonName,
				cheeseId, menuItemName, 7, 70);

		db.insertRowSectionMenuItems(pizzaSectionId, pizzaSectionName,
				menuItemId, menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Custom Pizza";
		itemDesc = "Add your own toppings to a basic Cheese and Tomato Pizza";
		pizzaId = (int) db.insertRowMenuItem(pizzaSectionId, pizzaSectionName,
				menuItemName, itemDesc);

		menuItemId = pizzaId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, cheeseId, "CHEESE");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, tomatoId, "TOMATO");

		// addon is for all options
		String cheeseStuffAddonName = "Cheese Stuffed Crust";
		int cheeseStuffAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, addonName, cheeseId, menuItemName);

		// addon is for all options
		String chickenAddonName = "Chicken";
		int chickenAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, chickenAddonName, chickenId, menuItemName);

		// addon is for all options
		String mushroomAddonName = "Mushrooms";
		int mushroomAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, mushroomAddonName, mushroomId, menuItemName);

		// addon is for all options
		String beefAddonName = "Beef";
		int beefAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, beefAddonName, beefId, beefAddonName);
		
		// adding option and amounts
		optionName = "Small";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 5);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, cheeseStuffAddonId,
				cheeseStuffAddonName, cheeseId, menuItemName, 5, 50);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, chickenAddonId,
				chickenAddonName, chickenId, menuItemName, 5, 55);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, mushroomAddonId,
				mushroomAddonName, mushroomId, menuItemName, 5, 45);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, beefAddonId, beefAddonName,
				beefId, menuItemName, 5, 50);

		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 20);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 7);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 5);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, cheeseStuffAddonId,
				cheeseStuffAddonName, cheeseId, menuItemName, 5, 60);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, chickenAddonId,
				chickenAddonName, chickenId, menuItemName, 5, 65);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, mushroomAddonId,
				mushroomAddonName, mushroomId, menuItemName, 5, 55);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, beefAddonId, beefAddonName,
				beefId, menuItemName, 5, 60);

		// adding option and amounts
		optionName = "Large";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 25);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, cheeseId, "CHEESE", 8);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, tomatoId, "TOMATO", 9);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 4);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, cheeseStuffAddonId,
				cheeseStuffAddonName, cheeseId, menuItemName, 5, 70);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, chickenAddonId,
				chickenAddonName, chickenId, menuItemName, 5, 75);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, mushroomAddonId,
				mushroomAddonName, mushroomId, menuItemName, 5, 65);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, beefAddonId, beefAddonName,
				beefId, menuItemName, 5, 70);

		db.insertRowSectionMenuItems(pizzaSectionId, pizzaSectionName,
				menuItemId, menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Steak Meal";
		itemDesc = "Tasty Steak meal served with chips and peas";
		int pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, steakId, "STEAK");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, potatoId, "POTATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, peasId, "PEAS");

		// adding option and amounts
		optionName = "Rare";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, steakId, "STEAK", 40);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, peasId, "PEAS", 10);
		// adding option and amounts
		optionName = "Medium";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 600);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, steakId, "STEAK", 40);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, peasId, "PEAS", 10);
		// adding option and amounts
		optionName = "Well Done";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 700);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, steakId, "STEAK", 40);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, peasId, "PEAS", 10);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ///////////////////////////////////////////////////////////////////////////////////////////////
		// ///////////////////////////////////////////////////////////////////////////////////

		// insert menuItem
		menuItemName = "Apple Pie";
		itemDesc = "Tasty Apple Pie";
		pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, pastryId, "PASTRY");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, appleId, "APPLE");

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pastryId, "PASTRY", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, appleId, "APPLE", 5);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Steak Pie";
		itemDesc = "Tasty Steak Pie";
		pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, pastryId, "PASTRY");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, steakId, "STEAK");

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, pastryId, "PASTRY", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, steakId, "STEAK", 5);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Sausage and Mash";
		itemDesc = "Sausage and Mash in Gravy";
		pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, porkId, "PORK");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, potatoId, "POTATO");

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, porkId, "PORK", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 5);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Beef Burger";
		itemDesc = "Beef Burger Meal, served with Chips";
		pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, potatoId, "POTATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");

		// addon is for all options
		String cheeseAddonName = "Cheese";
		int cheeseAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, cheeseAddonName, cheeseId, menuItemName);

		// addon is for all options
		String chickenBurgerAddonName = "Extra Chicken burger";
		int chickenBurgerAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, chickenBurgerAddonName, chickenId, menuItemName);

		// addon is for all options
		String beefBurgerAddonName = "Extra Beef burger";
		int beefBurgerAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, beefBurgerAddonName, beefId, menuItemName);

		// addon is for all options
		String saladAddonName = "Salad";
		int saladAddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, addonName, lettuceId, saladAddonName);

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 5);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, beefBurgerAddonId,
				beefAddonName, beefId, menuItemName, 5, 50);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, chickenBurgerAddonId,
				chickenBurgerAddonName, chickenId, menuItemName, 5, 100);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, cheeseAddonId,
				cheeseAddonName, cheeseId, menuItemName, 5, 45);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, saladAddonId,
				saladAddonName, lettuceId, menuItemName, 5, 50);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Chicken Burger";
		itemDesc = "Chicken Burger Meal, served with Chips";
		pubId = (int) db.insertRowMenuItem(pubSectionId, pubSectionName,
				menuItemName, itemDesc);

		menuItemId = pubId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, breadId, "BREAD");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, potatoId, "POTATO");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, chickenId, "CHICKEN");

		// addon is for all options
		String cheese2AddonName = "Cheese";
		int cheese2AddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, cheeseAddonName, cheeseId, menuItemName);

		// addon is for all options
		String chickenBurger2AddonName = "Extra Chicken burger";
		int chickenBurger2AddonId = (int) db.insertRowMenuItemAddons(
				menuItemId, menuItemName, chickenBurgerAddonName, chickenId,
				menuItemName);

		// addon is for all options
		String beefBurger2AddonName = "Extra Beef burger";
		int beefBurger2AddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, beefBurgerAddonName, beefId, menuItemName);

		// addon is for all options
		String salad2AddonName = "Salad";
		int salad2AddonId = (int) db.insertRowMenuItemAddons(menuItemId,
				menuItemName, addonName, lettuceId, saladAddonName);

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, breadId, "BREAD", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, potatoId, "POTATO", 5);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 5);
		// addon amount, price
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, beefBurger2AddonId,
				beefAddonName, beefId, menuItemName, 5, 50);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, chickenBurger2AddonId,
				chickenBurgerAddonName, chickenId, menuItemName, 5, 100);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, cheese2AddonId,
				cheese2AddonName, cheeseId, menuItemName, 5, 45);
		db.insertRowAddonIngAmountPriceForMenuItemOption(menuItemId,
				menuItemName, optionId, optionName, salad2AddonId,
				salad2AddonName, lettuceId, menuItemName, 5, 50);

		db.insertRowSectionMenuItems(pubSectionId, pubSectionName, menuItemId,
				menuItemName);

		// ////////////////////////////
		// ////////////////////////////

		// ////////////////////////////
		// ////////////////////////////

		// insert menuItem
		menuItemName = "Chicken Curry";
		itemDesc = "Mild Chicken Curry";
		int indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);

		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);

		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, chickenId, "CHICKEN");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");

		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, chickenId, "CHICKEN", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);

		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Beef Curry";
		itemDesc = "Mild Beef Curry";
		 indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);

		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Josh Rogan Curry";
		itemDesc = "Curry";
		indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		
		
		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Madras Curry";
		itemDesc = "Curry";
		indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Chicken Korma Curry";
		itemDesc = "Curry";
		indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		
		
		// insert menuItem
		menuItemName = "Boiled Rice";
		itemDesc = "rice";
		indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, riceId, "RICE");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, riceId, "RICE", 15);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Jalfrezi";
		itemDesc = "Curry";
		indianId = (int) db.insertRowMenuItem(indianSectionId, indianSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		

		// ////////////////////////////
		// ////////////////////////////
		
		
		// ////////////////////////////
		// ////////////////////////////
		
		// insert menuItem
		menuItemName = "Red Wine";
		itemDesc = "Wine";
		int cocktailsId = (int) db.insertRowMenuItem(cocktailsSectionId, cocktailsSectionName,
				menuItemName, itemDesc);
		
		menuItemId = indianId;
		db.insertRowMenuItemDesc(menuItemId, image, itemDesc);
		
		// insert menuitem ings
		db.insertRowMenuItemIngs(menuItemId, menuItemName, beefId, "BEEF");
		db.insertRowMenuItemIngs(menuItemId, menuItemName, curryPowderId, "CURRYPOWDER");
		
		// adding option and amounts
		optionName = "Default";
		optionId = (int) db.insertRowMenuItemOptions(menuItemId, menuItemName,
				optionName, 500);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, beefId, "BEEF", 15);
		db.insertRowIngAmountForMenuItemOption(menuItemId, menuItemName,
				optionId, optionName, curryPowderId, "CURRYPOWDER", 5);
		
		db.insertRowSectionMenuItems(indianSectionId, indianSectionName, menuItemId,
				menuItemName);
		
		
		
		// TODO add menuItems
	}

	private int addIng() {
		int ingId = (int) db.insertRowIngredients(ingName, ingUnit, 0);
		// db.insertRowIngredients(ingName, ingUnit, ingComplex)

		calories = calories / 100;
		sugars = sugars / 100;
		fat = fat / 100;
		saturates = saturates / 100;
		salt = salt / 100;

		db.insertRowStockLevels(ingId, ingName, ingUnit, ingAmount,
				recIngAmount, warningIngAmount);
		// db.insertRowStockLevels(ingId, ingName, ingUnit, ingAmount,
		// recAmount, warningAmount)

		// db.insertRowNutrionalInfo(ingId, calories, sugars, fat, saturates,
		// salt, vegetarian, containsNuts, containsGluten, dairy)
		db.insertRowNutrionalInfo(ingId, calories, sugars, fat, saturates,
				salt, vegetarian, containsNuts, containsGluten, dairy);
		return ingId;
	}

	private void updateSettings() {
		// for table num, delete all table nums and sessions etc
		String newNumOfTables = etNumOfTables.getText().toString();
		int numOfTables;
		if (newNumOfTables.equals("")) {
			numOfTables = 0;
		} else {
			numOfTables = Integer.parseInt(newNumOfTables);
		}
		Log.i(TAG, " Update Table Number = " + numOfTables);

		db.updateRowManagerSettings(1, numOfTables);

		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLES);
		db.deleteAllRows(DatabaseAdapter.DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS);
		int intialSessionId = 0;
		for (int tableNum = 1; tableNum < numOfTables + 1; tableNum++) {
			db.insertRowRestaurantTables(tableNum, 1, intialSessionId);
		}

		Toast t = Toast.makeText(this, "Settings Updated", Toast.LENGTH_SHORT);
		t.show();
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
		getMenuInflater().inflate(R.menu.manager_settings, menu);
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

	private void openDB() {
		db = new DatabaseAdapter(this);
		db.open();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeDB();
	}

	private void closeDB() {
		db.close();
	}
}
