package uk.co.nathanleigh.restaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {

	// Class used to make the database
	// database has multiple tables
	// maybe have time and date?
	
	// ////////////////////
	// TABLES IN DB INFO //
	// ////////////////////

	// TempOrder - used to store current order customer is making
	// Orders - used to stroe all orders that have been ordered
	// Kitchen - used to store info for the unique orders
	// Menu - used to hold details about a menu item
	// Price - used to store details about the menu item price
	// MenuCustom - used to store specific details and customisations for
	// the menu item
	// IngsForMenuItem - used to store what ingredients are in each menu item,
	// and the qty of the ingredient for specific size of meal

	// Section
	// Ingredients
	// Stock Levels
	// MenuSectionItems
	// MenuItem
	// Item Ingredients
	// Item Options
	// Item Addons Info
	// IngAmountsForItemOption
	// AddonIngAmountsPriceForItemOption
	// TempIngredients
	// TempItemOptions
	// Temp Addons
	// TempIngAmountsForItemOption
	// TempAddonIngAmountPriceForItemOption

	// ftsVirtualTableIngs - used to keep a mirror of the values in table
	// ingredients
	// used for searching through the ingredients fast

	// ///////////////////////////////////////////////////////////////////
	// Constants & Data
	// ///////////////////////////////////////////////////////////////////

	// For logging:
	private static final String TAG = "DatabaseAdapter";


	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;
	
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 18;
	//TODO update here

	// DB info: it's name, and the tables we are using .
	public static final String DATABASE_NAME = "Database.db";
	
	public static final String DATABASE_TABLE_TEMP_ORDER = "tableTempOrder";
	public static final String DATABASE_TABLE_TEMP_ORDER_ADDONS = "tableTempOrderAddons";
	public static final String DATABASE_TABLE_ORDERS = "tableOrders";
	public static final String DATABASE_TABLE_ORDER_DETAILS = "tableOrderDetails";
	public static final String DATABASE_TABLE_ORDERS_ADDONS = "tableOrdersAddons";
	public static final String DATABASE_TABLE_RESTAURANT_TABLES = "tableRestaurantTables";
	public static final String DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS = "tableRestaurantTableSessions";
	public static final String DATABASE_TABLE_INGREDIENTS = "tableIngredients";
	public static final String DATABASE_TABLE_COMPLEX_INGREDIENTS = "tableComplexIngredients";
	public static final String DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS = "tableTempComplexIngredients";
	public static final String DATABASE_FTS_VIRTUAL_TABLE_ING = "tableFTSIng";
	public static final String DATABASE_TABLE_NUTRITIONALINFO = "tableNutritionInfomation";

	public static final String DATABASE_TABLE_STOCK_LEVELS = "tableStockLevels";
	public static final String DATABASE_TABLE_MANAGER_SETTINGS = "tableManagerSettings";

	public static final String DATABASE_TABLE_SECTIONS = "tableSections";
	public static final String DATABASE_TABLE_SECTION_MENU_ITEMS = "tableSectionMenuItems";
	public static final String DATABASE_TABLE_MENU_ITEMS = "tableMenuItems";
	public static final String DATABASE_TABLE_MENU_ITEM_DESCRIPTION = "tableMenuItemDescription";
	public static final String DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION = "tableTempMenuItemDescription";
	public static final String DATABASE_TABLE_MENU_ITEM_INGREDIENTS = "tableMenuItemIngredients";
	public static final String DATABASE_TABLE_MENU_ITEM_OPTIONS = "tableMenuItemOptions";
	public static final String DATABASE_TABLE_MENU_ITEM_ADDONS = "tableMenuItemAddons";
	public static final String DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS = "tableIngAmountForMenuItemOptions";
	public static final String DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS = "tableAddonIngAmountPriceForMenuItemOptions";

	
	public static final String DATABASE_TABLE_TEMP_MENU_ITEM = "tableTempMenuItem";
	
	public static final String DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS = "tableTempMenuItemIngredients";
	public static final String DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS = "tableTempMenuItemOptions";
	public static final String DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS = "tableTempMenuItemAddons";
	public static final String DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS = "tableTempIngAmountForMenuItemOptions";
	public static final String DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS = "tableTempAddonIngAmountPriceForMenuItemOptions";

	
	// /////////////////////////////////
	// String names for all the keys //
	// /////////////////////////////////

	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;

	// used in table for TempOrder

	public static final String KEY_NAME = "name";

	public static final String KEY_QTY = "qty";
	public static final String KEY_PRICE = "price";
	public static final String KEY_TOTALPRICE = "totalPrice";
	public static final String KEY_ADDONTOTAL = "addonTotalPrice";


	// used in table for kitchen
	public static final String KEY_TABLENUM = "tableNum";
	public static final String KEY_TABLEOPEN = "tableOpen";
	public static final String KEY_TABLESESSIONID = "tableSessionId";
	public static final String KEY_DATETIME = "dateTime";
	public static final String KEY_ORDERCOMMENTS = "orderComments";

	// used in table for orders
	public static final String KEY_ORDERID = "orderId";
	public static final String KEY_ORDERGROUPID = "orderGroupId";
	public static final String KEY_WAITERID = "waiterId";
	public static final String KEY_SESSIONACTIVE = "sessionActive";
	public static final String KEY_ADDONNAMELIST = "addonNameList";


	// used in table menu
	public static final String KEY_SECTION = "section";

	public static final String KEY_DESCRIPTION = "description";

	// used in table menuCustom
	//public static final String KEY_MENUID = "menuId";

	// add others when think of them??
	public static final String KEY_NUMOFTABLES = "numOfTables";

	
	
	// used in table for Ingredients, ing short for ingredient
	public static final String KEY_INGID = "ingId";
	public static final String KEY_INGNAME = "ingredientName";
	public static final String KEY_INGUNIT = "ingredientUnit";
	public static final String KEY_INGCOMPLEX = "ingredientComplex";
	
	public static final String KEY_COMPLEXINGID = "complexIngId";
	public static final String KEY_COMPLEXINGNAME = "complexIngName";
	public static final String KEY_COMPLEXINGUNIT = "complexIngUnit";
	public static final String KEY_RAWINGID = "rawIngId";
	public static final String KEY_RAWINGNAME = "rawIngName";
	public static final String KEY_RAWINGUNIT = "rawIngUnit";
	public static final String KEY_INGAMOUNTPROPORTION = "IngAmountProportion";
	public static final String KEY_CALORIES = "calories";
	public static final String KEY_SALT = "salt";
	public static final String KEY_FAT = "fat";
	public static final String KEY_SUGARS = "sugars";
	public static final String KEY_SATURATES = "saturates";
	public static final String KEY_VEGETARIAN = "vegetarian";
	public static final String KEY_CONTAINSNUTS = "containsNuts";
	public static final String KEY_CONTAINSGLUTEN = "containsGluten";
	public static final String KEY_DAIRY = "dairy";

	// used in table for stockLevels, ing short for ingredient
	// public static final String KEY_INGNAME = "ingName";
	//public static final String KEY_INGQTY = "ingQty";


	// SectionS
	
	// Ingredients

	// Stock Levels
	public static final String KEY_RECAMOUNT = "recAmount";
	public static final String KEY_WARNINGAMOUNT = "warningAmount";

	// SectionMenuItems
	public static final String KEY_SECTIONID = "sectionId";
	public static final String KEY_SECTIONNAME = "sectionName";
	public static final String KEY_MENUITEMID = "menuItemId";
	// MenuItem
	public static final String KEY_ITEMNAME = "itemName";
	public static final String KEY_ITEMDESC = "itemDescription";
	public static final String KEY_IMAGETITLE = "imageTitle";

	// mENU Item Ingredients

	// Item Options
	public static final String KEY_OPTIONID = "optionId";
	public static final String KEY_OPTIONNAME = "optionName";
	public static final String KEY_OPTIONPRICE = "optionPrice";
	// Item Addons Info
	public static final String KEY_ADDONNAME = "addonName";
	public static final String KEY_ADDONID = "addonId";
	public static final String KEY_ADDONPRICE = "addonPrice";
	public static final String KEY_ADDONLIST = "addonList";
	public static final String KEY_ADDONTOTALPRICE = "addonTotalPrice";

	// IngAmountsForItemOption
	public static final String KEY_INGAMOUNT = "ingAmount";
	// AddonIngAmountsPriceForItemOption
	public static final String KEY_ADDONAMOUNT = "addonAmount";
	// TempIngredients
	// TempItemOptions
	// Temp Addons
	// TempIngAmountsForItemOption
	// TempAddonIngAmountPriceForItemOption

	// ////////////////////////////////////////////////
	// Keys for the column names used in each table //
	// ////////////////////////////////////////////////

	// keys for the temp order table, used in keeping
	// track of the current order that customer is placing
	public static final String[] ALL_TEMP_ORDER_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONID, KEY_OPTIONNAME,
			KEY_OPTIONPRICE };
	
	public static final String[] ALL_ORDERS_KEYS = new String[] {
		KEY_ROWID, KEY_TABLESESSIONID, KEY_ORDERGROUPID, KEY_MENUITEMID, KEY_ITEMNAME, 
		KEY_OPTIONID, KEY_OPTIONNAME, KEY_OPTIONPRICE, KEY_ADDONNAMELIST  };
	
	public static final String[] ALL_RESTAURANT_TABLE_SESSION_KEYS = new String[] {
		KEY_ROWID, KEY_TABLENUM, KEY_DATETIME, KEY_WAITERID, KEY_SESSIONACTIVE
	};
	
	public static final String[] ALL_ORDER_DETAILS_KEYS = new String[] {
		KEY_ROWID, KEY_TABLESESSIONID,  KEY_TABLENUM, KEY_TOTALPRICE, KEY_ORDERCOMMENTS, KEY_DATETIME,
	};
	
	public static final String[] ALL_TEMP_ORDER_ADDONS_KEYS = new String[] {
	KEY_ROWID, KEY_ORDERID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONID, KEY_OPTIONNAME, KEY_ADDONID,
	KEY_ADDONNAME, KEY_ADDONPRICE};
	
	public static final String[] ALL_ORDER_ADDONS_KEYS = new String[] {
		KEY_ROWID, KEY_ORDERID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONID, KEY_OPTIONNAME, KEY_ADDONID,
		KEY_ADDONNAME, KEY_ADDONPRICE };
	
	public static final String[] ALL_RESTAURANT_TABLE_KEYS = new String[] {
		KEY_ROWID, KEY_TABLENUM, KEY_TABLEOPEN, KEY_TABLESESSIONID };
	
	public static final String[] ALL_MANAGER_SETTINGS_KEYS = new String[] {
		KEY_ROWID, KEY_NUMOFTABLES };
	


	// keys for the kitchen table, has the info
	// the kitchen needs to separate the different orders
	//public static final String[] ALL_KITCHEN_KEYS = new String[] { KEY_ROWID,
	//		KEY_TABLENUM, KEY_DATETIME, KEY_ORDERCOMMENTS };

	// keys for all the individual orders that have been placed
	//public static final String[] ALL_ORDERS_KEYS = new String[] { KEY_ROWID,
	///		KEY_MENUTYPE, KEY_NAME, KEY_SIZE, KEY_CRUST, KEY_QTY, KEY_PRICE,
	//		KEY_ORDERID };

	// keys for the ingredients table
	public static final String[] ALL_INGREDIENTS_KEYS = new String[] {
			KEY_ROWID, KEY_INGNAME, KEY_INGUNIT, KEY_INGCOMPLEX };
	
	public static final String[] ALL_COMPLEX_INGREDIENTS_KEYS = new String[] {
		KEY_ROWID, KEY_COMPLEXINGID, KEY_COMPLEXINGNAME, KEY_COMPLEXINGUNIT, KEY_RAWINGID, KEY_RAWINGNAME, 
		KEY_COMPLEXINGUNIT, KEY_INGAMOUNTPROPORTION };
	
	public static final String[] ALL_TEMP_COMPLEX_INGREDIENTS_KEYS = new String[] {
		KEY_ROWID,  KEY_RAWINGID, KEY_RAWINGNAME, 
		KEY_RAWINGUNIT, KEY_INGAMOUNTPROPORTION };


	
	public static final String[] ALL_NUTRIONAL_INFORMATION_KEYS = new String[] {
		KEY_ROWID, KEY_INGID, KEY_CALORIES, KEY_SUGARS, KEY_FAT, KEY_SATURATES, KEY_SALT, 
		KEY_VEGETARIAN, KEY_CONTAINSNUTS, KEY_CONTAINSGLUTEN, KEY_DAIRY};
	
	// keys for the stock levels table
	public static final String[] ALL_STOCK_LEVELS_KEYS = new String[] {
			KEY_ROWID, KEY_INGID, KEY_INGNAME, KEY_INGUNIT, KEY_INGAMOUNT, 
			KEY_RECAMOUNT, KEY_WARNINGAMOUNT };

	


	
	
	public static final String[] ALL_SECTIONS_KEYS = new String[] { KEY_ROWID,
			KEY_SECTIONNAME };

	public static final String[] ALL_MENU_ITEMS_KEYS = new String[] {
			KEY_ROWID, KEY_SECTIONID, KEY_SECTIONNAME, KEY_ITEMNAME, KEY_ITEMDESC };

	public static final String[] ALL_SECTION_MENU_ITEMS_KEYS = new String[] {
			KEY_ROWID, KEY_SECTIONID, KEY_SECTIONNAME, KEY_MENUITEMID, KEY_ITEMNAME };

	public static final String[] ALL_MENU_ITEM_DESCRIPTION = new String[] {
		KEY_ROWID, KEY_MENUITEMID, KEY_IMAGETITLE, KEY_ITEMDESC };
	
	public static final String[] ALL_TEMP_MENU_ITEM_DESCRIPTION = new String[] {
		KEY_ROWID, KEY_IMAGETITLE, KEY_ITEMDESC };
	
	public static final String[] ALL_MENU_ITEM_INGREDIENTS_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_INGID, KEY_INGNAME };

	public static final String[] ALL_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONNAME, KEY_OPTIONPRICE };

	public static final String[] ALL_MENU_ITEM_ADDONS_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_ADDONNAME, KEY_INGID, KEY_INGNAME };

	public static final String[] ALL_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONID, KEY_OPTIONNAME, KEY_INGID, KEY_INGNAME, KEY_INGAMOUNT };

	public static final String[] ALL_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_MENUITEMID, KEY_ITEMNAME, KEY_OPTIONID, KEY_OPTIONNAME, KEY_ADDONID, KEY_ADDONNAME, KEY_INGID, KEY_INGNAME,
			KEY_ADDONAMOUNT, KEY_ADDONPRICE };

	public static final String[] ALL_TEMP_MENU_ITEM_INGREDIENTS_KEYS = new String[] {
			KEY_ROWID, KEY_INGID, KEY_INGNAME, KEY_INGUNIT};

	public static final String[] ALL_TEMP_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_OPTIONNAME, KEY_OPTIONPRICE };

	public static final String[] ALL_TEMP_MENU_ITEM_ADDONS_KEYS = new String[] {
			KEY_ROWID, KEY_ADDONNAME, KEY_INGID, KEY_INGNAME, KEY_INGUNIT};

	public static final String[] ALL_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_OPTIONID, KEY_OPTIONNAME, KEY_INGID, KEY_INGNAME, KEY_INGAMOUNT };

	public static final String[] ALL_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS_KEYS = new String[] {
			KEY_ROWID, KEY_OPTIONID, KEY_OPTIONNAME, KEY_ADDONID, KEY_ADDONNAME, KEY_INGID, KEY_INGNAME, KEY_INGUNIT,
			KEY_ADDONAMOUNT, KEY_ADDONPRICE };


	
	

	// /////////////////////////////
	// Create the table Strings //
	// /////////////////////////////

	// create table in DB for TempOrder
	private static final String DATABASE_CREATE_SQL_TEMP_ORDER = "create table "
			+ DATABASE_TABLE_TEMP_ORDER
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " int not null, "
			+ KEY_ITEMNAME
			+ " string not null, "
			+ KEY_OPTIONID
			+ " int not null, "
			+ KEY_OPTIONNAME
			+ " string not null, "
			+ KEY_OPTIONPRICE
			+ " int not null "
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_ORDERS = "create table "
			+ DATABASE_TABLE_ORDERS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_TABLESESSIONID
			+ " integer, "
			+ KEY_ORDERGROUPID
			+ " integer not null, "
			+ KEY_MENUITEMID
			+ " int not null, "
			+ KEY_ITEMNAME
			+ " string not null, "
			+ KEY_OPTIONID
			+ " int not null, "
			+ KEY_OPTIONNAME
			+ " string not null, "
			+ KEY_ADDONNAMELIST
			+ " int not null, "
			+ KEY_OPTIONPRICE
			+ " int not null "
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_ORDER_DETAILS = "create table "
			+ DATABASE_TABLE_ORDER_DETAILS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_TABLESESSIONID
			+ " int , "
			+ KEY_TABLENUM
			+ " int , "
			+ KEY_DATETIME
			+ " string , "
			+ KEY_ORDERCOMMENTS
			+ " string, " 
			//+ KEY_OPTIONPRICE
			//+ " int , "
			//+ KEY_ADDONTOTALPRICE 
			//+ " int , "
			+ KEY_TOTALPRICE
			+ " int "  + ");";

	private static final String DATABASE_CREATE_SQL_TEMP_ORDER_ADDONS = "create table "
			+ DATABASE_TABLE_TEMP_ORDER_ADDONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_ORDERID
			+ " int not null, "
			+ KEY_MENUITEMID
			+ " int not null, "
			+ KEY_ITEMNAME
			+ " string not null, "
			+ KEY_OPTIONID
			+ " INT not null, "
			+ KEY_OPTIONNAME
			+ " string not null, "
			+ KEY_ADDONID
			+ " int not null, "
			+ KEY_ADDONNAME
			+ " string not null, "
			+ KEY_ADDONPRICE
			+ " int not null "
			 + ");";
	
	
	//ADD FOREING KEY CONSTRAINTS TO THIS
	private static final String DATABASE_CREATE_SQL_ORDER_ADDONS = "create table "
			+ DATABASE_TABLE_ORDERS_ADDONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_ORDERID
			+ " int not null, "
			+ KEY_MENUITEMID
			+ " int not null, "
			+ KEY_ITEMNAME
			+ " string not null, "
			+ KEY_OPTIONID
			+ " int not null, "
			+ KEY_OPTIONNAME
			+ " string not null, "
			+ KEY_ADDONID
			+ " int not null, "
			+ KEY_ADDONNAME
			+ " string not null, "
			+ KEY_ADDONPRICE
			+ " int not null "
			+ ");";
	
	
	private static final String DATABASE_CREATE_SQL_MANAGER_SETTINGS = "create table "
			+ DATABASE_TABLE_MANAGER_SETTINGS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_NUMOFTABLES
			+ " int default 0 "
			+ ");";	
	
	private static final String DATABASE_CREATE_SQL_RESTAURANT_TABLES = "create table "
			+ DATABASE_TABLE_RESTAURANT_TABLES
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_TABLENUM
			+ " int not null, "
			+ KEY_TABLEOPEN
			+ " int not null, "
			+ KEY_TABLESESSIONID
			+ " int  "
			+ ");";	
	
	private static final String DATABASE_CREATE_SQL_RESTAURANT_TABLE_SESSIONS = "create table "
			+ DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_TABLENUM
			+ " int , "
			+ KEY_DATETIME
			+ " string , "
			+ KEY_WAITERID
			+ " int, " 
			+ KEY_SESSIONACTIVE
			+ " int " 
			+ ");";
	
	// create table in DB for Ingredients
	private static final String DATABASE_CREATE_SQL_INGREDIENTS = "create table "
			+ DATABASE_TABLE_INGREDIENTS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_INGNAME
			+ " string not null, " + KEY_INGUNIT + " string not null, " 
			+ KEY_INGCOMPLEX + " int not null " + ");";
	
	// create table in DB for Ingredients
	private static final String DATABASE_CREATE_SQL_COMPLEX_INGREDIENTS = "create table "
			+ DATABASE_TABLE_COMPLEX_INGREDIENTS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_COMPLEXINGID
			+ " int not null, " 
			+ KEY_COMPLEXINGNAME
			+ " string not null, " 
			+ KEY_COMPLEXINGUNIT
			+ " string not null, " 
			+ KEY_RAWINGID
			+ " int not null, " 
			+ KEY_RAWINGNAME
			+ " string not null, " 
			+ KEY_INGAMOUNTPROPORTION
			+ " real not null, " 
			+ KEY_RAWINGUNIT + " string not null " 
			+ ");";
	
	//TODO this be real ingAMountProportion
	private static final String DATABASE_CREATE_SQL_TEMP_COMPLEX_INGREDIENTS = "create table "
			+ DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_RAWINGID
			+ " int not null, " 
			+ KEY_RAWINGNAME
			+ " string not null, " 
			+ KEY_INGAMOUNTPROPORTION
			+ " int not null, " 
			+ KEY_RAWINGUNIT + " string not null " 
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_NUTRIONAL_INFOMATION = "create table "
			+ DATABASE_TABLE_NUTRITIONALINFO
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_INGID
			+ " int , " 
			+ KEY_CALORIES
			+ " real , " 
			+ KEY_SUGARS
			+ " real, " 
			+ KEY_FAT
			+ " real , " 
			+ KEY_SATURATES
			+ " real , " 
			+ KEY_SALT + " real,  " 
			+ KEY_VEGETARIAN + " int,  " 
			+ KEY_CONTAINSNUTS + " int,  " 
			+ KEY_CONTAINSGLUTEN + " int,  " 
			+ KEY_DAIRY + " int  " 
			+ ");";
	


	// create table in DB for Stock Levels
	private static final String DATABASE_CREATE_SQL_STOCK_LEVELS = "create table "
			+ DATABASE_TABLE_STOCK_LEVELS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_INGID
			+ " integer not null, "
			+ KEY_INGNAME
			+ " string, "
			+ KEY_INGUNIT
			+ " string, "
			+ KEY_INGAMOUNT + " int,  "
			+ KEY_RECAMOUNT + " int,  "
			+ KEY_WARNINGAMOUNT + " int  "
			+ ");";

	// create table in DB for IngsForMenuItems
	private static final String DATABASE_CREATE_SQL_SECTIONS = "create table "
			+ DATABASE_TABLE_SECTIONS + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_SECTIONNAME
			+ " string  " + ");";

	private static final String DATABASE_CREATE_SQL_MENU_ITEMS = "create table "
			+ DATABASE_TABLE_MENU_ITEMS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_SECTIONID
			+ " integer not null,  "
			+ KEY_SECTIONNAME
			+ " string, "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_ITEMDESC + " string  "
			+ ");";

	private static final String DATABASE_CREATE_SQL_SECTION_MENU_ITEMS = "create table "
			+ DATABASE_TABLE_SECTION_MENU_ITEMS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_SECTIONID
			+ " integer not null,  "
			+ KEY_SECTIONNAME
			+ " string,  "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string  "
			//	+ " foreign key( " + KEY_SECTIONID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_SECTIONS + "(" + KEY_ROWID + "),"
			//	+ " foreign key( " + KEY_MENUITEMID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + ")"
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_MENU_ITEM_DESCRIPTION = "create table "
			+ DATABASE_TABLE_MENU_ITEM_DESCRIPTION
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " int not null , " 
			+ KEY_IMAGETITLE
			+ " string , " 
			+ KEY_ITEMDESC
			+ " string " 
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_TEMP_MENU_ITEM_DESCRIPTION = "create table "
			+ DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_IMAGETITLE
			+ " string , " 
			+ KEY_ITEMDESC
			+ " string " 
			+ ");";
	
	private static final String DATABASE_CREATE_SQL_MENU_ITEM_INGREDIENTS = "create table "
			+ DATABASE_TABLE_MENU_ITEM_INGREDIENTS
			+ " ("      
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer not null,  "
			+ KEY_INGNAME
			+ " string,  "
			+ KEY_INGCOMPLEX
			+ " int  "
			//	+ " foreign key( " + KEY_MENUITEMID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + "),"
			//	+ " foreign key( " + KEY_INGID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_INGREDIENTS + "(" + KEY_ROWID + ")"
			+ ");";
	

	private static final String DATABASE_CREATE_SQL_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_OPTIONNAME
			+ " string,  "
			+ KEY_OPTIONPRICE + " integer  " 
			//	+ " foreign key( " + KEY_MENUITEMID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + ")"
			+ ");";

	private static final String DATABASE_CREATE_SQL_MENU_ITEM_ADDONS = "create table "
			+ DATABASE_TABLE_MENU_ITEM_ADDONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_ADDONNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer not null,  "
			+ KEY_INGNAME
			+ " string  "
			//	+ " foreign key( " + KEY_MENUITEMID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + "),"
			//	+ " foreign key( " + KEY_INGID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_INGREDIENTS + "(" + KEY_ROWID + ")"
			+ ");";

	private static final String DATABASE_CREATE_SQL_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_OPTIONID
			+ " integer not null,  "
			+ KEY_OPTIONNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer not null,  "
			+ KEY_INGNAME
			+ " string,  "
			+ KEY_INGAMOUNT + " integer  not null "
			//	+ " foreign key( " + KEY_MENUITEMID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + "),"
			//	+ " foreign key( " + KEY_OPTIONID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_MENU_ITEM_OPTIONS + "(" + KEY_ROWID + "),"
			//	+ " foreign key( " + KEY_INGID + " ) " 
			//+ " REFERENCES " + DATABASE_TABLE_INGREDIENTS + "(" + KEY_ROWID + ")"
			+ ");";

	private static final String DATABASE_CREATE_SQL_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_MENUITEMID
			+ " integer not null,  "
			+ KEY_ITEMNAME
			+ " string,  "
			+ KEY_OPTIONID
			+ " integer not null,  "
			+ KEY_OPTIONNAME
			+ " string,  "
			+ KEY_ADDONID
			+ " integer not null,  "
			+ KEY_ADDONNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer not null,  "
			+ KEY_INGNAME
			+ " string,  "
			+ KEY_ADDONAMOUNT
			+ " integer not null,  "
			+ KEY_ADDONPRICE
			+ " integer not null "
			//+ " foreign key( " + KEY_MENUITEMID + " ) " 
		//	+ " REFERENCES " + DATABASE_TABLE_MENU_ITEMS + "(" + KEY_ROWID + "),"
			//+ " foreign key( " + KEY_OPTIONID + " ) " 
		//	+ " REFERENCES " + DATABASE_TABLE_MENU_ITEM_OPTIONS + "(" + KEY_ROWID + "),"
			//+ " foreign key( " + KEY_ADDONID + " ) " 
		//	+ " REFERENCES " + DATABASE_TABLE_MENU_ITEM_ADDONS + "(" + KEY_ROWID + "),"
		//	+ " foreign key( " + KEY_INGID + " ) " 
		//	+ " REFERENCES " + DATABASE_TABLE_INGREDIENTS + "(" + KEY_ROWID + ")"
			+ ");";

	private static final String DATABASE_CREATE_SQL_TEMP_MENU_ITEM_INGREDIENTS = "create table "
			+ DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_INGID
			+ " integer not null,  "
			+ KEY_INGNAME
			+ " string not null,"
			+ KEY_INGUNIT
			+ " string not null"
			+ ");";

	private static final String DATABASE_CREATE_SQL_TEMP_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_OPTIONNAME
			+ " string,  " + KEY_OPTIONPRICE + " integer  " + ");";

	private static final String DATABASE_CREATE_SQL_TEMP_MENU_ITEM_ADDONS = "create table "
			+ DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_ADDONNAME
			+ " string,  " + KEY_INGID + " integer,  " 
			+ KEY_INGNAME + " string, "
			+ KEY_INGUNIT + " string "
			+ ");";

	private static final String DATABASE_CREATE_SQL_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_OPTIONID
			+ " integer,  "
			+ KEY_OPTIONNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer,  "
			+ KEY_INGNAME
			+ " string,  "
			+ KEY_INGAMOUNT
			+ " integer  " + ");";

	private static final String DATABASE_CREATE_SQL_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS = "create table "
			+ DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "
			+ KEY_OPTIONID
			+ " integer,  "
			+ KEY_OPTIONNAME
			+ " string,  "
			+ KEY_ADDONID
			+ " integer,  "
			+ KEY_ADDONNAME
			+ " string,  "
			+ KEY_INGID
			+ " integer,  "
			+ KEY_INGNAME
			+ " string,  "
			+ KEY_INGUNIT
			+ " string,  "
			+ KEY_ADDONAMOUNT
			+ " integer,  "
			+ KEY_ADDONPRICE + " integer  " + ");";

	// Create a FTS3 Virtual Table for fast searches
	// used this for help http://www.sqlite.org/fts3.html
	// uses content from ingredient table to fill it, copies data from it
	// and puts the data into its own
	private static final String DATABASE_CREATE_SQL_VIRTUAL_ING = "CREATE VIRTUAL TABLE "
			+ DATABASE_FTS_VIRTUAL_TABLE_ING
			+ " USING fts4("
			+ "content="
			+ DATABASE_TABLE_INGREDIENTS + ","
			// + KEY_INGNAME + " string "
			+ KEY_INGNAME + " string, " + KEY_INGUNIT + " string, " + KEY_INGCOMPLEX + " int " + ");";

	// ////////////////////
	// Triggers Strings //
	// ////////////////////

	// used in the fts virtual ing table to maintain consistincy between itself
	// and the ingredientsTable, when a row is changed/added/deleted it
	// will apply the same function on the virtual table

	// when a record from tableIngredients is deleted the corresponding
	// row from ftsVirtualTableIng is also deleted, other triggers
	// below follow similar pattern
	private static String TRIGGER_BEFORE_UPDATE = "CREATE TRIGGER t2_bu "
			+ " BEFORE UPDATE " + " ON " + DATABASE_TABLE_INGREDIENTS
			+ " BEGIN " + " DELETE FROM " + DATABASE_FTS_VIRTUAL_TABLE_ING
			+ " WHERE docid=old.rowid;" + " END;";

	private static String TRIGGER_BEFORE_DELETE = "CREATE TRIGGER t2_bd "
			+ " BEFORE DELETE " + " ON " + DATABASE_TABLE_INGREDIENTS
			+ " BEGIN " + " DELETE FROM " + DATABASE_FTS_VIRTUAL_TABLE_ING
			+ " WHERE docid=old.rowid;" + " END;";

	private static String TRIGGER_AFTER_UPDATE = "CREATE TRIGGER t2_au "
			+ " AFTER UPDATE " + " ON " + DATABASE_TABLE_INGREDIENTS
			+ " BEGIN " + " INSERT INTO " + DATABASE_FTS_VIRTUAL_TABLE_ING
			+ "(docid, " + KEY_INGNAME + ", " + KEY_INGUNIT + ", " 
			+ KEY_INGCOMPLEX + ")"
			+ " VALUES(new.rowid, new." + KEY_INGNAME + ", new." + KEY_INGUNIT
			+ ", new." + KEY_INGCOMPLEX
			+ ");" + " END;";

	private static String TRIGGER_AFTER_INSERT = "CREATE TRIGGER t2_ai "
			+ " AFTER INSERT " + " ON " + DATABASE_TABLE_INGREDIENTS
			+ " BEGIN " + " INSERT INTO " + DATABASE_FTS_VIRTUAL_TABLE_ING
			+ "(docid, " + KEY_INGNAME + ", " + KEY_INGUNIT + ", " 
			+ KEY_INGCOMPLEX + ")"
			+ " VALUES(new.rowid, new." + KEY_INGNAME + ", new." + KEY_INGUNIT
			+ ", new." + KEY_INGCOMPLEX
			+ ");" + " END;";

	
	// ///////////////////////////////////////////////////////////////////
	// Public methods:
	// ///////////////////////////////////////////////////////////////////

	public DatabaseAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DatabaseAdapter open() {
		db = myDBHelper.getWritableDatabase();
		 
	        	db.execSQL("PRAGMA foreign_keys = ON;");
	        
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	
	
	// //////////////////////////////////////////////////////////////////////
	// CRUD METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////
	// CREATE //
	// /////////

	// Add a new set of values to the TEMPORDER table.
	public long insertRowTempOrder(int menuItemId, String menuItemName, int optionId, 
			String optionName, int optionPrice) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		//initialValues.put(KEY_ORDERCOMMENTS, orderComments);
		initialValues.put(KEY_OPTIONPRICE, optionPrice);
	

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_ORDER, null, initialValues);
	}
	
	// Add a new set of values to the ORDER table.
	public long insertRowOrder(int tableSessionId, int orderGroupId, int menuItemId, String menuItemName, int optionId, 
			String optionName, int optionPrice, String addonNameList) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ORDERGROUPID, orderGroupId);
		initialValues.put(KEY_TABLESESSIONID, tableSessionId);
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_OPTIONPRICE, optionPrice);
		initialValues.put(KEY_ADDONNAMELIST, addonNameList);
		
	
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_ORDERS, null, initialValues);
	}
	
	// Add a new set of values to the ORDER table.
	public long insertRowOrderDetails( int tableSessionId, int tableNum, String dateTime, 
			  int totalPrice,  String orderComments) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_TABLESESSIONID, tableSessionId);
		initialValues.put(KEY_TABLENUM, tableNum);
		initialValues.put(KEY_DATETIME, dateTime);
		//initialValues.put(KEY_OPTIONPRICE, optionPrice);
		//initialValues.put(KEY_ADDONTOTALPRICE, addonTotalPrice);
		initialValues.put(KEY_ORDERCOMMENTS, orderComments);
		initialValues.put(KEY_TOTALPRICE, totalPrice);
	
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_ORDER_DETAILS, null, initialValues);
	}
	
	// Add a new set of values to the ORDER table.
	public long insertRowTempOrderAddons( int orderId, int menuItemId, String menuItemName,
			int optionId, String optionName, int addonId, String addonName, int addonPrice) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
	
		initialValues.put(KEY_ORDERID, orderId);
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_ADDONID, addonId);
		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_ADDONPRICE, addonPrice);
	
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_ORDER_ADDONS, null, initialValues);
	}
	
	// Add a new set of values to the ORDER table.
	public long insertRowOrderAddons( int orderId, int menuItemId, String menuItemName, 
			int optionId, String optionName, int addonId, String addonName, int addonPrice) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ORDERID, orderId);
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_ADDONID, addonId);
		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_ADDONPRICE, addonPrice);
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_ORDERS_ADDONS, null, initialValues);
	}
	
	// Add a new set of values to the ORDER table.
	public long insertRowRestaurantTables(int tableNum, int isOpen, int tableSessionId) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_TABLENUM, tableNum);
		initialValues.put(KEY_TABLEOPEN, isOpen);
		initialValues.put(KEY_TABLESESSIONID, tableSessionId);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_RESTAURANT_TABLES, null, initialValues);
	}

	
	public long insertRowManagerSettings(int numOfTables) {
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NUMOFTABLES, numOfTables);
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_MANAGER_SETTINGS, null, initialValues);
	}

	// Add a new set of values to the ORDER table.
	public long insertRowRestaurnatTableSessions( int tableNum, String dateTime, 
			int waiterId, int sessionActive) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_TABLENUM, tableNum);
		initialValues.put(KEY_DATETIME, dateTime);
		initialValues.put(KEY_WAITERID, waiterId);
		initialValues.put(KEY_SESSIONACTIVE, sessionActive);
	
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS, null, initialValues);
	}

	// Add a new set of values to the Ingredients table.
	public long insertRowIngredients(String ingName, String ingUnit, int ingComplex) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGUNIT, ingUnit);
		initialValues.put(KEY_INGCOMPLEX, ingComplex);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_INGREDIENTS, null, initialValues);
	}
	// Add a new set of values to the Ingredients table.
	public long insertRowComplexIngredients(int complexId, String complexIngName,
			String complexIngUnit, int rawIngId, String rawIngName, 
			String rawIngUnit, double ingAmountProportion) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_COMPLEXINGID, complexId);
		initialValues.put(KEY_COMPLEXINGNAME, complexIngName);
		initialValues.put(KEY_COMPLEXINGUNIT, complexIngUnit);
		initialValues.put(KEY_RAWINGID, rawIngId);
		initialValues.put(KEY_RAWINGNAME, rawIngName);
		initialValues.put(KEY_RAWINGUNIT, rawIngUnit);
		initialValues.put(KEY_INGAMOUNTPROPORTION, ingAmountProportion);
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_COMPLEX_INGREDIENTS, null, initialValues);
	}
	
	// Add a new set of values to the Ingredients table.
	public long insertRowTempComplexIngredients(int rawIngId, 
			String rawIngName, String ingUnit, int ingAmountProportion) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_RAWINGID, rawIngId);
		initialValues.put(KEY_RAWINGNAME, rawIngName);
		initialValues.put(KEY_RAWINGUNIT, ingUnit);
		initialValues.put(KEY_INGAMOUNTPROPORTION, ingAmountProportion);
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS, null, initialValues);
	}
	
	// Add a new set of values to the Ingredients table.
	public long insertRowNutrionalInfo(int ingId, double calories, double sugars,
			double fat, double saturates, double salt, 
			int vegetarian, int containsNuts, int containsGluten, int dairy) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CALORIES, calories);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_SUGARS, sugars);
		initialValues.put(KEY_FAT, fat);
		initialValues.put(KEY_SATURATES, saturates);
		initialValues.put(KEY_SALT, salt);
		initialValues.put(KEY_VEGETARIAN, vegetarian);
		initialValues.put(KEY_CONTAINSNUTS, containsNuts);
		initialValues.put(KEY_CONTAINSGLUTEN, containsGluten);
		initialValues.put(KEY_DAIRY, dairy);
		
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_NUTRITIONALINFO, null, initialValues);
	}

	// Add a new set of values to the StockLevels table.
	public long insertRowStockLevels(int ingId, String ingName, String ingUnit, int ingAmount,
			int recAmount, int warningAmount) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGUNIT, ingUnit);
		initialValues.put(KEY_INGAMOUNT, ingAmount);
		initialValues.put(KEY_RECAMOUNT, recAmount);
		initialValues.put(KEY_WARNINGAMOUNT, warningAmount);
	
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_STOCK_LEVELS, null, initialValues);
	}

	public long insertRowSections(String sectionName) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SECTIONNAME, sectionName);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_SECTIONS, null, initialValues);
	}

	public long insertRowMenuItem(int sectionId, String sectionName, String itemName,
			String itemDesc) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SECTIONID, sectionId);
		initialValues.put(KEY_SECTIONNAME, sectionName);
		initialValues.put(KEY_ITEMNAME, itemName);
		initialValues.put(KEY_ITEMDESC, itemDesc);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_MENU_ITEMS, null, initialValues);
	}

	public long insertRowSectionMenuItems(int sectionId, String sectionName,
			int menuItemId, String menuItemName) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_SECTIONID, sectionId);
		initialValues.put(KEY_SECTIONNAME, sectionName);
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);

		// Insert it into the database.
		return db
				.insert(DATABASE_TABLE_SECTION_MENU_ITEMS, null, initialValues);
	}
	
	public long insertRowTempMenuItemDesc( String imageTitle,
			String itemDisc) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_IMAGETITLE, imageTitle);
		initialValues.put(KEY_ITEMDESC, itemDisc);
		
		
		// Insert it into the database.
		return db
				.insert(DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION, null, initialValues);
	}
	
	public long insertRowMenuItemDesc(int menuItemId, String imageTitle,
			String itemDisc) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_MENUITEMID, menuItemId);
				initialValues.put(KEY_IMAGETITLE, imageTitle);
				initialValues.put(KEY_ITEMDESC, itemDisc);
				
		
		// Insert it into the database.
		return db
				.insert(DATABASE_TABLE_MENU_ITEM_DESCRIPTION, null, initialValues);
	}
	
	public long insertRowMenuItemIngs(int menuItemId, String menuItemName,
			int ingId, String ingName) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		
		// Insert it into the database.
		return db
				.insert(DATABASE_TABLE_MENU_ITEM_INGREDIENTS, null, initialValues);
	}

	public long insertRowMenuItemOptions(int menuItemId, String menuItemName, String optionName,
			int optionPrice) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONPRICE, optionPrice);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_MENU_ITEM_OPTIONS, null, initialValues);
	}
	public long insertRowMenuItemAddons(int menuItemId, String menuItemName,
			String addonName, int ingId, String ingName) {
		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_MENU_ITEM_ADDONS, null, initialValues);
	}

	public long insertRowIngAmountForMenuItemOption(int menuItemId, String menuItemName,
			int optionId, String optionName, int ingId, String ingName, int ingAmount) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGAMOUNT, ingAmount);
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS, null,
				initialValues);
	}

	public long insertRowAddonIngAmountPriceForMenuItemOption(int menuItemId, String menuItemName,
			int optionId, String optionName, int addonId, String addonName,
			int ingId, String ingName, int addonAmount, int addonPrice) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_MENUITEMID, menuItemId);
		initialValues.put(KEY_ITEMNAME, menuItemName);
		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_ADDONID, addonId);
		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_ADDONAMOUNT, addonAmount);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_ADDONPRICE, addonPrice);

		// Insert it into the database.
		return db.insert(
				DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
				null, initialValues);
	}

	public long insertRowTempMenuItemIng(int ingId, String ingName, String ingUnit) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGUNIT, ingUnit);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS, null,
				initialValues);
	}

	public long insertRowTempMenuItemOption(String optionName, int optionPrice) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_OPTIONPRICE, optionPrice);
		initialValues.put(KEY_OPTIONNAME, optionName);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS, null,
				initialValues);
	}

	public long insertRowTempMenuItemAddon(String addonName, int ingId,
			String ingName, String ingUnit) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGUNIT, ingUnit);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS, null,
				initialValues);
	}

	public long insertRowTempIngAmountForMenuItemOption(int optionId, String optionName,
			int ingId, String ingName, int ingAmount) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGAMOUNT, ingAmount);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
				null, initialValues);
	}

	public long insertRowTempAddonAmountPriceForMenuItemOption(int optionId, String optionName,
			int addonId, String addonName, int ingId, String ingName, String ingUnit,
			int addonAmount, int addonPrice) {

		// Create row's data:
		ContentValues initialValues = new ContentValues();

		initialValues.put(KEY_OPTIONID, optionId);
		initialValues.put(KEY_OPTIONNAME, optionName);
		initialValues.put(KEY_ADDONNAME, addonName);
		initialValues.put(KEY_INGID, ingId);
		initialValues.put(KEY_INGNAME, ingName);
		initialValues.put(KEY_INGUNIT, ingUnit);
		initialValues.put(KEY_ADDONAMOUNT, addonAmount);
		initialValues.put(KEY_ADDONID, addonId);
		initialValues.put(KEY_ADDONPRICE, addonPrice);

		// Insert it into the database.
		return db
				.insert(DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						null, initialValues);
	}

	
	
	
	// //////////////////////////////////////////////////////////////////////
	// UPDATE //
	// /////////////////////////////////////////////////////////////////////////

	// Change an existing row to be equal to new data for TEMPORDER
	public boolean updateRowTempOrder(long rowId, int menuItemId, String menuItemName,
			int optionId, String optionName, int optionPrice) {
		String where = KEY_ROWID + "=" + rowId;

		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, menuItemName);
		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_OPTIONPRICE, optionPrice);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_ORDER, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowOrders(long rowId, int tableSessionId, int orderGroupId, int menuItemId, String menuItemName,
			int optionId, String optionName, int optionPrice, String addonNameList) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TABLESESSIONID, tableSessionId);
		newValues.put(KEY_ORDERGROUPID, orderGroupId);
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, menuItemName);
		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_OPTIONPRICE, optionPrice);
		newValues.put(KEY_ADDONNAMELIST, addonNameList);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_ORDERS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowOrderDetails(long rowId, int tableSessionId, 
			int tableNum, String dateTime, int totalPrice, String orderComments) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		
		newValues.put(KEY_TABLESESSIONID, tableSessionId);
		newValues.put(KEY_DATETIME, dateTime);
		newValues.put(KEY_ORDERCOMMENTS, orderComments);

		newValues.put(KEY_TABLENUM, tableNum);
		newValues.put(KEY_TOTALPRICE, totalPrice);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_ORDER_DETAILS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowTempOrderAddons(long rowId, int orderId, int menuItemId, String menuItemName,
			int optionId, String optionName, int addonId, String addonName, int addonPrice ) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ORDERID, orderId);
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, menuItemName);
		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_ADDONID, addonId);
		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_ADDONPRICE, addonPrice);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_ORDER_ADDONS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowOrderAddons(long rowId, int orderId, int menuItemId, String menuItemName,
			int addonId, String addonName, int addonPrice ) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ORDERID, orderId);
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, menuItemName);
		newValues.put(KEY_ADDONID, addonId);
		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_ADDONPRICE, addonPrice);

		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_ORDERS_ADDONS, newValues, where, null) != 0;
	}
	
	// TODO this have to be uniwque because of only 1 row, no add?
	public boolean updateRowManagerSettings(long rowId, int numOfTables) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_NUMOFTABLES, numOfTables);
	
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_MANAGER_SETTINGS, newValues, where, null) != 0;
	}
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowRestaurantTables(long rowId, int tableNum, int tableOpen,
			int tableSessionId) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TABLENUM, tableNum);
		newValues.put(KEY_TABLEOPEN, tableOpen);
		newValues.put(KEY_TABLESESSIONID, tableSessionId);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_RESTAURANT_TABLES, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for ORDER
	public boolean updateRowRestaurantTableSessions(long rowId,
			int tableNum, String dateTime,  int waiterId, int sessionActive) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		
		newValues.put(KEY_DATETIME, dateTime);
		newValues.put(KEY_TABLENUM, tableNum);
		newValues.put(KEY_WAITERID, waiterId);
		newValues.put(KEY_SESSIONACTIVE, sessionActive);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS, newValues, where, null) != 0;
	}
	

	// Change an existing row to be equal to new data for INGREDIENTS
	public boolean updateRowIngredients(int ingId, String ingName,
			String ingUnit, int complex) {
		String where = KEY_ROWID + "=" + ingId;

		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGUNIT, ingUnit);
		newValues.put(KEY_INGCOMPLEX, complex);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_INGREDIENTS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for INGREDIENTS
	public boolean updateRowComplexIngredients(int rowId, int complexId, String complexIngName,
			String complexIngUnit, int rawIngId, String rawIngName, 
			String rawIngUnit, int ingAmountProportion) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_COMPLEXINGID, complexId);
		newValues.put(KEY_COMPLEXINGNAME, complexIngName);
		newValues.put(KEY_COMPLEXINGUNIT, complexIngUnit);
		newValues.put(KEY_RAWINGID, rawIngId);
		newValues.put(KEY_RAWINGNAME, rawIngName);
		newValues.put(KEY_RAWINGUNIT, rawIngUnit);
		newValues.put(KEY_INGAMOUNTPROPORTION, ingAmountProportion);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_COMPLEX_INGREDIENTS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for INGREDIENTS
	public boolean updateRowTempComplexIngredients(int rowId, 
			int rawIngId, String rawIngName, String ingUnit, double ingAmountProportion) {
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		
		newValues.put(KEY_RAWINGID, rawIngId);
		newValues.put(KEY_RAWINGNAME, rawIngName);
		newValues.put(KEY_RAWINGUNIT, ingUnit);
		newValues.put(KEY_INGAMOUNTPROPORTION, ingAmountProportion);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS, newValues, where, null) != 0;
	}
	
	// Change an existing row to be equal to new data for INGREDIENTS
	public boolean updateRowNutrionalInfo(int rowId, double ingId, double calories, double sugars, double fat,
			double saturates, double salt, int vegetarian, 
			int containsNuts, int containsGluten, int dairy) {
		
		String where = KEY_ROWID + "=" + rowId;
		
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_CALORIES, calories);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_CALORIES, calories);
		newValues.put(KEY_SUGARS, sugars);
		newValues.put(KEY_FAT, fat);
		newValues.put(KEY_SATURATES, saturates);
		newValues.put(KEY_SALT, salt);
		newValues.put(KEY_VEGETARIAN, vegetarian);
		newValues.put(KEY_CONTAINSNUTS, containsNuts);
		newValues.put(KEY_CONTAINSGLUTEN, containsGluten);
		newValues.put(KEY_DAIRY, dairy);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_NUTRITIONALINFO, newValues, where, null) != 0;
	}

	// Change an existing row to be equal to new data for STOCK LEVELS
	public boolean updateRowStockLevels(long rowId, int ingId, String ingName,
			String ingUnit, int ingAmount, int recAmount, int warningAmount) {
		String where = KEY_ROWID + "=" + rowId;

		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGUNIT, ingUnit);
		newValues.put(KEY_INGAMOUNT, ingAmount);
		newValues.put(KEY_RECAMOUNT, recAmount);
		newValues.put(KEY_WARNINGAMOUNT, warningAmount);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_STOCK_LEVELS, newValues, where, null) != 0;
	}



	public boolean updateRowSections(long rowId, String sectionName) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_SECTIONNAME, sectionName);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_SECTIONS, newValues, where, null) != 0;
	}

	public boolean updateRowMenuItem(long rowId, int sectionId, String sectionName,
			String itemName, String itemDesc) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_SECTIONID, sectionId);
		newValues.put(KEY_SECTIONNAME, sectionName);
		newValues.put(KEY_ITEMNAME, itemName);
		newValues.put(KEY_ITEMDESC, itemDesc);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_MENU_ITEMS, newValues, where, null) != 0;
	}

	public boolean updateRowSectionMenuItems(long rowId, int sectionId, String sectionName,
			int menuItemId, String itemName) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_SECTIONID, sectionId);
		newValues.put(KEY_SECTIONNAME, sectionName);
		newValues.put(KEY_ITEMNAME, itemName);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_SECTION_MENU_ITEMS, newValues, where,
				null) != 0;
	}

	public boolean updateRowTempMenuItemDesc(long rowId, String imageTitle, 
			String desc) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_IMAGETITLE, imageTitle);
		newValues.put(KEY_ITEMDESC, desc);
		
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION, newValues, where,
				null) != 0;
	}
	
	public boolean updateRowMenuItemDesc(long rowId, int menuItemId, 
			String imageTitle, String desc) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_IMAGETITLE, imageTitle);
		newValues.put(KEY_ITEMDESC, desc);
		
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_MENU_ITEM_DESCRIPTION, newValues, where,
				null) != 0;
	}
	
	public boolean updateRowMenuItemIngs(long rowId, int menuItemId, String menuItemName,
			int ingId, String ingName) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, menuItemName);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_MENU_ITEM_INGREDIENTS, newValues, where,
						null) != 0;
	}
	
	public boolean updateRowMenuItemOptions(long rowId, int menuItemId, String itemName,
			String optionName, int optionPrice) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, itemName);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_OPTIONPRICE, optionPrice);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_MENU_ITEM_OPTIONS, newValues, where,
				null) != 0;
	}
	
	public boolean updateRowMenuItemAddons(long rowId, int menuItemId, String itemName,
			String addonName, int ingId, String ingName) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, itemName);
		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE_MENU_ITEM_OPTIONS, newValues, where,
				null) != 0;
	}

	public boolean updateRowIngAmountForMenuItemOption(long rowId, int menuItemId,String itemName,
			int optionId, String optionName, int ingId, String ingName, int ingAmount) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, itemName);
		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGAMOUNT, ingAmount);
		// Insert it into the database.
		return db.update(DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
				newValues, where, null) != 0;
	}

	public boolean updateRowAddonIngAmountPriceForMenuItemOption(long rowId, int menuItemId, String itemName,
			int optionId, String optionName, int addonId, String addonName, int ingId, String ingName,
			int addonAmount, int addonPrice) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_MENUITEMID, menuItemId);
		newValues.put(KEY_ITEMNAME, itemName);
		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
	
		newValues.put(KEY_ADDONID, addonId);
		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_ADDONAMOUNT, addonAmount);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_ADDONPRICE, addonPrice);

		// Insert it into the database.
		return db.update(
				DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
				newValues, where, null) != 0;
	}

	public boolean updateRowTempMenuItemIng(long rowId, int ingId, int ingName, 
			String ingUnit) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGUNIT, ingUnit);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS, newValues,
				where, null) != 0;
	}

	public boolean updateRowTempMenuItemOption(long rowId, String optionName,
			int optionPrice) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_OPTIONPRICE, optionPrice);
		newValues.put(KEY_OPTIONNAME, optionName);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS, newValues,
				where, null) != 0;
	}

	public boolean updateRowTempMenuItemAddon(long rowId, String addonName,
			int ingId, String ingName, String ingUnit) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGUNIT, ingUnit);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS, newValues,
				where, null) != 0;
	}

	public boolean updateRowTempIngAmountForMenuItemOption(long rowId,
			int optionId, String optionName, int ingId, String ingName, int ingAmount) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGNAME, ingName);
		newValues.put(KEY_INGAMOUNT, ingAmount);

		// Insert it into the database.
		return db.update(DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS,
				newValues, where, null) != 0;
	}

	public boolean updateRowTempAddonAmountPriceForMenuItemOption(long rowId,
			int optionId, String optionName, int addonId, int ingId, String ingName,
			String ingUnit, String addonName, int addonAmount, int addonPrice) {
		String where = KEY_ROWID + "=" + rowId;
		// Create row's data:
		ContentValues newValues = new ContentValues();

		newValues.put(KEY_OPTIONID, optionId);
		newValues.put(KEY_OPTIONNAME, optionName);
		newValues.put(KEY_ADDONID, addonId);
		newValues.put(KEY_INGID, ingId);
		newValues.put(KEY_INGID, ingName);
		newValues.put(KEY_INGUNIT, ingUnit);
		newValues.put(KEY_ADDONNAME, addonName);
		newValues.put(KEY_ADDONAMOUNT, addonAmount);
		newValues.put(KEY_ADDONPRICE, addonPrice);

		// Insert it into the database.
		return db
				.update(DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS,
						newValues, where, null) != 0;
	}

	// READ

	// Return all data in the database for a given table.
	public Cursor getAllRows(String dbTable) {
		String where = null;
		String[] tableKeys = returnKeys(dbTable);
		Cursor c = db.query(true, dbTable, tableKeys, where, null, null, null,
				null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	// Return all data in the database for a given table.
	public Cursor getAllRowsKitchen() {
		String dbTable = DATABASE_TABLE_ORDER_DETAILS;
		String where = null;
		String[] tableKeys = returnKeys(dbTable);
		Cursor c = db.query(true, dbTable, tableKeys, where, null, null, null,
				KEY_DATETIME +" DESC", null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId) for a given db Table
	public Cursor getRow(String dbTable, long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		String[] tableKeys = returnKeys(dbTable);
		Cursor c = db.query(true, dbTable, tableKeys, where, null, null, null,
				null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// DELETE

	// Delete a row from a given database table, by rowId (primary key)
	public boolean deleteRow(String dbTable, long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(dbTable, where, null) != 0;
	}

	// delete all the rows from a given database table
	public void deleteAllRows(String dbTable) {
		Cursor c = getAllRows(dbTable);
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(dbTable, c.getLong((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// /////////////////
	// OTHER METHODS //
	// /////////////////

	// custom query, provide a table and where string
	// returns cursor
	public Cursor myDBQuery(String dbTable, String where) {
		String[] tableKeys = returnKeys(dbTable);
		Cursor c = db.query(true, dbTable, tableKeys, where, null, null, null,
				null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// cursor which is used to search the ingredient names in the
	// ftsVirtualIng table looking for matches with the inputText
	// returns any matches it finds in a cursor
	//TODO customise for searching raw and complex ings
	public Cursor searchIng(String inputText) throws SQLException {
		Log.w(TAG, inputText);
		String query = "SELECT docid as _id," + KEY_INGNAME + ", "
				+ KEY_INGUNIT + " from " + DATABASE_FTS_VIRTUAL_TABLE_ING
				+ " where " + KEY_INGNAME + " MATCH '" + inputText + "'" 
				+  " ORDER BY " + KEY_INGNAME + ";";
		Log.w(TAG, query);
		Cursor mCursor = db.rawQuery(query, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}
	
	// cursor which is used to search the ingredient names in the
	// ftsVirtualIng table looking for matches with the inputText
	// returns any matches it finds in a cursor
	//TODO customise for searching raw and complex ings
	public Cursor searchRawIng(String inputText) throws SQLException {
		Log.w(TAG, inputText);
		String query = "SELECT docid as _id," + KEY_INGNAME + ", "
				+ KEY_INGUNIT + " from " + DATABASE_FTS_VIRTUAL_TABLE_ING
				+ " where " + KEY_INGCOMPLEX + "=" + 0 + " and " 
				+ KEY_INGNAME + " MATCH '" + inputText + "'" 
				+  " ORDER BY " + KEY_INGNAME + ";";
		Log.w(TAG, query);
		Cursor mCursor = db.rawQuery(query, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
		
	}
	
	
	
	

	// Method which returns the corresponding keys for the given database table
	// case statements were being strange with using string values
	private String[] returnKeys(String dbTable) {
		if (dbTable == DATABASE_TABLE_TEMP_ORDER)
			return ALL_TEMP_ORDER_KEYS;

		//if (dbTable == DATABASE_TABLE_KITCHEN)
		//	return ALL_KITCHEN_KEYS;

		if (dbTable == DATABASE_TABLE_ORDERS)
			return ALL_ORDERS_KEYS;
		
		if (dbTable == DATABASE_TABLE_ORDER_DETAILS)
			return ALL_ORDER_DETAILS_KEYS;
		
		if (dbTable == DATABASE_TABLE_TEMP_ORDER_ADDONS)
			return ALL_TEMP_ORDER_ADDONS_KEYS;
		
		if (dbTable == DATABASE_TABLE_ORDERS_ADDONS)
			return ALL_ORDER_ADDONS_KEYS;
		
		if (dbTable == DATABASE_TABLE_RESTAURANT_TABLES)
			return ALL_RESTAURANT_TABLE_KEYS;
		
		if (dbTable == DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS)
			return ALL_RESTAURANT_TABLE_SESSION_KEYS;

		if (dbTable == DATABASE_TABLE_STOCK_LEVELS)
			return ALL_STOCK_LEVELS_KEYS;
		
		if (dbTable == DATABASE_TABLE_MANAGER_SETTINGS)
			return ALL_MANAGER_SETTINGS_KEYS;
		
		if (dbTable == DATABASE_TABLE_COMPLEX_INGREDIENTS)
			return ALL_COMPLEX_INGREDIENTS_KEYS;
		
		if (dbTable == DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS)
			return ALL_TEMP_COMPLEX_INGREDIENTS_KEYS;
		
		if (dbTable == DATABASE_TABLE_NUTRITIONALINFO)
			return ALL_NUTRIONAL_INFORMATION_KEYS;

		if (dbTable == DATABASE_TABLE_INGREDIENTS)
			return ALL_INGREDIENTS_KEYS;

		if (dbTable == DATABASE_TABLE_SECTIONS)
			return ALL_SECTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_SECTION_MENU_ITEMS)
			return ALL_SECTION_MENU_ITEMS_KEYS;

		if (dbTable == DATABASE_TABLE_MENU_ITEMS)
			return ALL_MENU_ITEMS_KEYS;

		if (dbTable == DATABASE_TABLE_MENU_ITEM_INGREDIENTS)
			return ALL_MENU_ITEM_INGREDIENTS_KEYS;

		if (dbTable == DATABASE_TABLE_MENU_ITEM_OPTIONS)
			return ALL_MENU_ITEM_OPTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_MENU_ITEM_ADDONS)
			return ALL_MENU_ITEM_ADDONS_KEYS;

		if (dbTable == DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS)
			return ALL_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS)
			return ALL_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS)
			return ALL_TEMP_MENU_ITEM_INGREDIENTS_KEYS;

		if (dbTable == DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS)
			return ALL_TEMP_MENU_ITEM_OPTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS)
			return ALL_TEMP_MENU_ITEM_ADDONS_KEYS;

		if (dbTable == DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS)
			return ALL_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS_KEYS;

		if (dbTable == DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS)
			return ALL_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS_KEYS;
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////////////
	// Private Helper Classes:
	// ///////////////////////////////////////////////////////////////////

	/**
	 * Private class which handles database creation and upgrading. Used to
	 * handle low-level database access.
	 */

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onOpen(SQLiteDatabase sqLiteDB) {
		    super.onOpen(sqLiteDB);
		    if (!sqLiteDB.isReadOnly()) {
		        // Enable foreign key constraints
		    	sqLiteDB.execSQL("PRAGMA foreign_keys=ON;");
		    }
		}
		
		@Override
		public void onCreate(SQLiteDatabase sqLiteDB) {
			
		
	    
	        	 sqLiteDB.execSQL("PRAGMA foreign_keys = ON;");
	          
	        	 
	 			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MANAGER_SETTINGS);
	 
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_ORDER);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_ORDERS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_ORDER_DETAILS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_ORDER_ADDONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_ORDER_ADDONS);
			
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_RESTAURANT_TABLES);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_RESTAURANT_TABLE_SESSIONS);
			
		
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_INGREDIENTS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_COMPLEX_INGREDIENTS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_COMPLEX_INGREDIENTS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_NUTRIONAL_INFOMATION);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_STOCK_LEVELS);
	
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_SECTIONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_SECTION_MENU_ITEMS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MENU_ITEMS);
		
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_MENU_ITEM_DESCRIPTION);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MENU_ITEM_DESCRIPTION);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MENU_ITEM_INGREDIENTS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MENU_ITEM_OPTIONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_MENU_ITEM_ADDONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
			
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_MENU_ITEM_INGREDIENTS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_MENU_ITEM_OPTIONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_MENU_ITEM_ADDONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
			sqLiteDB.execSQL(DATABASE_CREATE_SQL_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);

			sqLiteDB.execSQL(DATABASE_CREATE_SQL_VIRTUAL_ING);

			sqLiteDB.execSQL(TRIGGER_BEFORE_UPDATE);
			sqLiteDB.execSQL(TRIGGER_BEFORE_DELETE);
			sqLiteDB.execSQL(TRIGGER_AFTER_UPDATE);
			sqLiteDB.execSQL(TRIGGER_AFTER_INSERT);
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion,
				int newVersion) {
			// Destroy old database:
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MANAGER_SETTINGS);

			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_ORDER);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ORDERS);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ORDER_DETAILS);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_ORDER_ADDONS);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ORDERS_ADDONS);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANT_TABLES);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_RESTAURANT_TABLE_SESSIONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_INGREDIENTS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_COMPLEX_INGREDIENTS);
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_COMPLEX_INGREDIENTS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_NUTRITIONALINFO);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_STOCK_LEVELS);
;
		
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_FTS_VIRTUAL_TABLE_ING);

			sqLiteDB.execSQL("DROP TABLE IF EXISTS " 
					+ DATABASE_TABLE_SECTIONS);
		
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_SECTION_MENU_ITEMS);
		
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_MENU_ITEMS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_MENU_ITEM_DESCRIPTION);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_MENU_ITEM_DESCRIPTION);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_MENU_ITEM_INGREDIENTS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_MENU_ITEM_OPTIONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_MENU_ITEM_ADDONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
		
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);
			
			
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_MENU_ITEM_INGREDIENTS);
		
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_MENU_ITEM_OPTIONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_ING_AMOUNT_FOR_MENU_ITEM_OPTIONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_MENU_ITEM_ADDONS);
			
			sqLiteDB.execSQL("DROP TABLE IF EXISTS "
					+ DATABASE_TABLE_TEMP_ADDON_ING_AMOUNT_PRICE_FOR_MENU_ITEM_OPTIONS);

			// Recreate new database:
			onCreate(sqLiteDB);

		}

	}

	

}
