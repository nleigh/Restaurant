package uk.co.nathanleigh.restaurant;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

//////////////////////////////////////////////////////////////////////////////////
//Currency Format Manipulation with user input
// class which formats what user is entering into currency for their locale
// provides methods for changing format form string to int and then to double and then 
// back to string 
public class CurrencyFormat {

	private static final String TAG = "CurrencyFormat";

	public CurrencyFormat(){
		
	}

	
	//Returns a textwatcher which automatically formats input into currency
	public static TextWatcher getCurrencyInputTextWatcher(final EditText etNumOfTables) {
		TextWatcher textWatcher = new TextWatcher() {
			private String current = "";
			// allows user to enter currency value, shows fixed symbol and decimal
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!s.toString().equals(current)) {
					etNumOfTables.removeTextChangedListener(this);
					 // strip off the currency symbol
					String replaceable = String.format("[%s,.\\s]",
							NumberFormat.getCurrencyInstance().getCurrency()
									.getSymbol());
					String cleanString = s.toString().replaceAll(replaceable,
							"");
					double parsed = Double.parseDouble(cleanString);
					// format the double into a currency format
					String formated = NumberFormat.getCurrencyInstance()
							.format((parsed / 100));

					current = formated;
					etNumOfTables.setText(formated);
					etNumOfTables.setSelection(formated.length());

					etNumOfTables.addTextChangedListener(this);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};

		return textWatcher;
	}
	
	// returns currency represented in a string format into an int
	public static int currencyStringIntoInteger(String currencyString) {

		currencyString = currencyString.replaceAll("\\.", "");
		currencyString = currencyString.replaceAll("\\,", "");
		currencyString = currencyString.replaceAll("\\"
				+ NumberFormat.getCurrencyInstance().getCurrency().getSymbol(),
				"");

		int currencyInt = Integer.parseInt(currencyString);
		Log.i(TAG, "Input string = " + currencyString);
		return currencyInt;
	}
	
	// returns currency represented in a int format into an double
	public static double currencyIntToDouble(int currencyInt) {
		Currency c = Currency.getInstance(Locale.getDefault());
		// calculate how many digits the decimal point needs to be by dividing
		// the result to the power of 10, 1 digit = 10^1 =1, 2 digit = 10^2 =
		// 100
		double decimalPointPlaceCalculation = Math.pow(10,
				c.getDefaultFractionDigits());
		// divides by a number to place the decimal point in orrect position
		// depending on the locale
		double inputDouble = (double) currencyInt
				/ decimalPointPlaceCalculation;
		return inputDouble;
	}

	public static String currencyDoubleToString(double currencyDouble){
		String currencyString = NumberFormat.getCurrencyInstance()
			 .format(currencyDouble);
		return currencyString;
	}
	
	public static int currencyDoubleToInt(double currencyDouble){
		//0.74
		Currency c = Currency.getInstance(Locale.getDefault());
		
		int decimalPointPlaceCalculation = (int) Math.pow(10,
				c.getDefaultFractionDigits());
		
		double currencyInt =  currencyDouble * decimalPointPlaceCalculation;
		return (int) currencyInt;
	}
	
	public static double currencyStringToDouble(String currencyString){
		int currencyInt = currencyStringIntoInteger(currencyString);
		double currencyDouble = currencyIntToDouble(currencyInt);
		return currencyDouble;
	}
	
	public static String currencyIntToString(int currencyInt){
		double currencyDouble = currencyIntToDouble(currencyInt);
		String currencyString = currencyDoubleToString( currencyDouble);
		return currencyString;
	}


	//public static void setEditTextBlank(EditText et) {
		
	//	et.removeTextChangedListener(getCurrencyInputTextWatcher(et));
	//	et.setText("");
	//	et.addTextChangedListener(getCurrencyInputTextWatcher(et));
	//}
	
}
//int maxLength = 13; // prevent entering a number too large
	// etNumOfTables.setFilters(new InputFilter[] { 
	//		new InputFilter.LengthFilter(maxLength)});
	// TextWatcher currencyInputTextWatcher = getCurrencyInputTextWatcher();
	// etNumOfTables.addTextChangedListener(currencyInputTextWatcher);
	//
	// String currencyString = [EditTextBameHere].getText().toString();
	// int currencyInt = currencyStringIntoInteger(currencyString);
	// double currencyDouble = currencyIntToDouble(currencyInt);
	// String currencyDoubleToString = NumberFormat.getCurrencyInstance()
	//	 .format(currencyDouble);
	
