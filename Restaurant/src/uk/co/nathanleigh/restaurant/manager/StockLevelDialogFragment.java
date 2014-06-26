package uk.co.nathanleigh.restaurant.manager;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.editMenu.CreateComplexIngFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuAddonsFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.EditMenuNameDiscIngsFragment;
import uk.co.nathanleigh.restaurant.manager.editMenu.IngNutritionInfoDialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class StockLevelDialogFragment extends DialogFragment {

	private static final String TAG = "STOCK_LEVEL_DIALOG_FRAGMENT";
	Context context;
	// create a DBAdapter
	DatabaseAdapter dB;
	private long rowId;
	private int ingId;
	private String ingName;
	private String ingUnit, ingType;
	private int ingAmount;
	private int ingRecAmount;
	private int ingWarningAmount;
	private String complexIngName;
	private String complexIngUnit;
	private int complexIngAmount;
	private String ingTypePrev;
	private int rawIngId;
	View view;
	EditText etIngAmount;
	EditText etIngRecAmount;
	EditText etIngWarningAmount;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		// context
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View dialogView = inflater.inflate(R.layout.dialog_edit_stock_level,
				null);
		view = dialogView;
		Bundle mArgs = getArguments();
		ingId = mArgs.getInt("ingId");
		rowId = mArgs.getInt("rowId");
		ingType = mArgs.getString("ingType");
		ingName = mArgs.getString("ingName");
		ingUnit = mArgs.getString("ingUnit");
		ingAmount = mArgs.getInt("ingAmount");
		ingRecAmount = mArgs.getInt("ingRecAmount");
		ingWarningAmount = mArgs.getInt("ingWarningAmount");
		
		Log.i("TAG", "ingId from stocklevel bundle : " + ingId);
		Log.i("TAG", "ingType : " + ingType);
		if (ingType == null) {
			ingType ="";
		}
		if (ingType.equals(CreateComplexIngFragment.TAG)) {
			complexIngName =mArgs.getString("complexIngName");
			complexIngUnit = mArgs.getString("complexIngUnit");
			complexIngAmount = mArgs.getInt("complexIngAmount");
			ingTypePrev = mArgs.getString("ingTypePrev");
			Log.i("TAG", "complexIngUnit : " + complexIngUnit);
			Log.i("TAG", "complexIngName : " + complexIngName);
			Log.i("TAG", "ComplexIngAmount : " + complexIngAmount);

		}
		
		
		Log.i("TAG", "gfghffg : ");

		// need this?
		// setDialogView(DialogView);

		// EditText tvIngName = (EditText) view.findViewById(R.id.etName);
		// EditText tvIngUnit = (EditText) view.findViewById(R.id.etUnit);
		etIngAmount = (EditText) view.findViewById(R.id.etCurrentAmount);
		etIngRecAmount = (EditText) view.findViewById(R.id.etRecAmount);
		etIngWarningAmount = (EditText) view.findViewById(R.id.etWarningAmount);

		Log.i(TAG, "SETTING text for ing amount");
		etIngAmount.setText("" + ingAmount);
		Log.i(TAG, "has set text for ing amount");
		etIngRecAmount.setText("" + ingRecAmount);
		etIngWarningAmount.setText("" + ingWarningAmount);

		openDB();

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(view)
				.setTitle(ingName)
				// Add action buttons
				.setPositiveButton("Edit Stock Levels",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								int ingAmount, ingRecAmount, ingWarningAmount;
								
								if(etIngAmount.equals("")){
									ingAmount = 0;
								}
								else{
									 ingAmount = Integer.parseInt(etIngAmount
												.getText().toString());	
								}
								
								if(etIngRecAmount.equals("")){
									ingRecAmount = 0;
								}
								else{
									ingRecAmount = Integer
											.parseInt(etIngRecAmount.getText()
													.toString());
								}
								
								if(etIngWarningAmount.equals("")){
									ingWarningAmount = 0;
								}
								else{
									ingWarningAmount = Integer
											.parseInt(etIngWarningAmount.getText()
													.toString());

								}
								
								
								// send NumberPicker values to a temp order
								// database table

								/*
								 * String toastOrderMessage = "Stock Level for "
								 * + ingName + "( Current amount: " + ingAmount
								 * + "  Rec Amount: " + ingRecAmount +
								 * " IngWarningAmount:  " + ingWarningAmount +
								 * " updated in db ";
								 * 
								 * 
								 * 
								 * Toast.makeText( getActivity()
								 * .getApplicationContext(), toastOrderMessage,
								 * Toast.LENGTH_LONG).show();
								 */
								Log.i(TAG, "Stock Level for " + ingName
										+ "( Current amount: " + ingAmount
										+ "  Rec Amount: " + ingRecAmount
										+ " IngWarningAmount:  "
										+ ingWarningAmount + " updated in db ");
								openDB();
								if (ingType.equals(EditMenuNameDiscIngsFragment.TAG)) {
									dB.insertRowStockLevels( ingId, ingName,
											ingUnit, ingAmount, ingRecAmount,
											ingWarningAmount);
								}
								else if (ingType.equals(EditMenuAddonsFragment.TAG)) {
									dB.insertRowStockLevels( ingId, ingName,
											ingUnit, ingAmount, ingRecAmount,
											ingWarningAmount);
								}
								else if (ingType.equals(CreateComplexIngFragment.TAG)) {
									//TODO implement theis
									rawIngId = (int) dB.insertRowStockLevels( ingId, ingName,
											ingUnit, ingAmount, ingRecAmount,
											ingWarningAmount);
								}
								else{ // in ingredients 
								dB.updateRowStockLevels(rowId, ingId, ingName,
										ingUnit, ingAmount, ingRecAmount,
										ingWarningAmount);
								}

								closeDB();
								dialog.dismiss();
								
								
								if (ingType.equals(EditMenuNameDiscIngsFragment.TAG)) {
									showNutritionDialog(ingId, ingName, ingUnit);
								}
								if (ingType.equals(EditMenuAddonsFragment.TAG)) {
									showNutritionDialog(ingId, ingName, ingUnit);
								}
								if (ingType.equals(CreateComplexIngFragment.TAG)) {
									showNutritionDialog(ingId, ingName, ingUnit);
								}
								if (ingType==null) {
									Intent intent = new Intent(getActivity(), StockLevelsActivity.class);
							    	startActivity(intent);
									
								}

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

							}
						});
		return builder.create();
	}

	private void showNutritionDialog(int ingId, String ingName, String ingUnit2) {
		Log.i(TAG, "Show nutrion dialog");
		IngNutritionInfoDialogFragment df = new IngNutritionInfoDialogFragment();

		Bundle args = new Bundle();
		args.putInt("ingId", ingId);
		args.putString("ingName", ingName);
		args.putString("ingType", ingType);
		args.putString("ingUnit", ingUnit);
		
		//bundle.putString("ingType", ingType);
		//bundle.putString("ingComplexName", ingName);
		//bundle.putString("ingComplexUnit", ingUnit);
		
		//// bundle.putString("ingComplexAmount", ingAmount);

		//// bundle.putInt("menuItemId", menuItemId);
		//// bundle.putString("editType", editType);
	
		args.putInt("complexIngAmount", complexIngAmount);
		args.putInt("rawIngId", rawIngId);
		args.putString("complexIngName", complexIngName);
		args.putString("complexIngUnit", complexIngUnit);
		args.putString("ingTypePrev", ingTypePrev);


		df.setArguments(args);
		df.show(getFragmentManager(), TAG);
		// getActivity().showStockLevelDialog(df, getActivity());

	}

	private void openDB() {
		dB = new DatabaseAdapter(getActivity());
		dB.open();
	}

	private void closeDB() {
		dB.close();
	}
}
