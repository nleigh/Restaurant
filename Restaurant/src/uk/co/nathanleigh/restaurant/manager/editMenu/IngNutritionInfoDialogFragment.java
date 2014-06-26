package uk.co.nathanleigh.restaurant.manager.editMenu;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.IngredientsActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.EditText;

public class IngNutritionInfoDialogFragment extends DialogFragment implements
		OnItemSelectedListener {
//TODO get decimal amounts doubles/ real store?
	// make it possible to update ing nutrition
	private DatabaseAdapter db;
	private String ingUnit;
	private String ingName;
	private String ingType;
	private String editType;
	private String complexIngName;
	private String complexIngUnit;
	private int complexIngAmount;
	private int rawIngId;
	private String ingTypePrev;
	private long rowId;
	private EditText etIngAmount, etCalories,etSugars,
	etFat, etSaturates, etSalt;
	private CheckBox cbDairy, cbVegetarian, cbNuts, cbGluten;
	private int ingId, ingAmount, calories, sugars,
	fat, saturates,salt, vegetarian,
	containsNuts, containsGluten, dairy = 0;
	
	public static final String TAG = "ING_NUTRITION_DIALOG";
	

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		openDb();

		View dialogView = inflater.inflate(R.layout.dialog_ing_nutrition_info, null);
		Bundle bundle = getArguments();
		
		ingName = bundle.getString("ingName");
		ingType = bundle.getString("ingType");
		editType = bundle.getString("editType");
		ingId = bundle.getInt("ingId");
		Log.i("TAG", "ingId : " + ingId);

		
		ingAmount = bundle.getInt("ingAmount");
		calories = bundle.getInt("alories");
		sugars = bundle.getInt("sugars");
		fat = bundle.getInt("fat");
		saturates = bundle.getInt("saturates");
		salt = bundle.getInt("salt");
		ingUnit = bundle.getString("ingUnit", ingUnit);
		
		Log.i(TAG, "IngType = " + ingType);
		Log.i(TAG, "IngName = " + ingName);
		Log.i(TAG, "ingUnit = " + ingUnit);
		Log.i(TAG, "editType = " + editType);
		
		if (ingType.equals(CreateComplexIngFragment.TAG)) {
			complexIngName = bundle.getString("complexIngName");
			complexIngUnit = bundle.getString("complexIngUnit");
			complexIngAmount = bundle.getInt("complexIngAmount");
			rawIngId = bundle.getInt("rawIngId");
			ingTypePrev = bundle.getString("ingTypePrev");
			Log.i("TAG", "complexIngUnit : " + complexIngUnit);
			Log.i("TAG", "complexIngName : " + complexIngName);
			Log.i("TAG", "ComplexIngAmount : " + complexIngAmount);

		}

		 etIngAmount = (EditText) dialogView.findViewById(R.id.etIngAmount);
		 etCalories = (EditText) dialogView.findViewById(R.id.etCalories);
		 etSugars =  (EditText) dialogView.findViewById(R.id.etSugars);
		 etFat = (EditText) dialogView.findViewById(R.id.etFat);
		 etSaturates = (EditText)dialogView.findViewById(R.id.etSaturates);
		 etSalt =(EditText) dialogView.findViewById(R.id.etSalt);
		
		cbVegetarian = (CheckBox) dialogView.findViewById(R.id.cbVeg);
		cbNuts = (CheckBox) dialogView.findViewById(R.id.cbNuts);
		cbDairy = (CheckBox) dialogView.findViewById(R.id.cbDairy);
		cbGluten = (CheckBox) dialogView.findViewById(R.id.cbGluten);
		
		etIngAmount.setText("" + ingAmount);
		etCalories.setText("" + calories);
		etSugars.setText("" + sugars);
		etFat.setText("" + fat);
		etSaturates.setText("" + saturates);
		etSalt.setText("" + salt);
		
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(dialogView).setTitle(
				ingName);

		builder.setMessage("Nutrional Infomation")
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Log.i(TAG, "EditTextIngAMount =" + etIngAmount);
								if(etIngAmount.equals("")){
									ingAmount = 0;
								}
								else{
									ingAmount = Integer.parseInt(etIngAmount.getText().toString()); 
								}
								
								if(etCalories.equals("")){
									calories = 0;
								}
								else{
									calories = Integer.parseInt(etCalories.getText().toString()); 
								}
								
								if(etSugars.equals("")){
									sugars = 0;
								}
								else{
									sugars = Integer.parseInt(etSugars.getText().toString()); 
								}
								
								if(etFat.equals("")){
									fat = 0;
								}
								else{
									fat = Integer.parseInt(etFat.getText().toString()); 
								}
								
								if(etSaturates.equals("")){
									saturates = 0;
								}
								else{
									saturates = Integer.parseInt(etSaturates.getText().toString()); 
								}
								
								if(etSalt.equals("")){
									salt = 0;
								}
								else{
									salt = Integer.parseInt(etSalt.getText().toString()); 
								}
								
								
								
								if(cbNuts.isChecked()){
									containsNuts = 1;
								}
								else{
									containsNuts = 0;
								}
								if(cbDairy.isChecked()){
									dairy = 1;
								}
								else{
									dairy = 0;
								}
								if(cbGluten.isChecked()){
									containsGluten = 1;
								}
								else{
									containsGluten = 0;
								}
								if(cbVegetarian.isChecked()){
									vegetarian = 1;
								}
								else{
									vegetarian = 0;
								}
								
								
								
								Log.i(TAG, "inserting into nutriotional info Table");
								Log.i(TAG, "ingAmount: " + ingAmount + " calories: " + calories  +
										" sugars: " + sugars + " fat: " + fat +
								"saturates: " + saturates + 
								" salt: " + salt + " vegetarian: " + vegetarian +
								" containsNuts " + containsNuts + " containsGluten: " + containsGluten
								+ " dairy: " + dairy);
								
								double caloriesAmountRatio, sugarsAmountRatio, fatAmountRatio, 
								saturatesAmountRatio, saltAmountRatio;
								
								caloriesAmountRatio = (double) calories/(double)ingAmount;
								sugarsAmountRatio = (double)sugars/(double)ingAmount;
								fatAmountRatio = (double)fat/(double)ingAmount;
								saturatesAmountRatio = (double)saturates/(double)ingAmount;
								saltAmountRatio = (double)salt/(double)ingAmount;
								
								Log.i(TAG, "ingAmount: " + ingAmount + " caloriesAmountRatio: " 
								+ caloriesAmountRatio  + " sugarsAmountRatio: " + sugarsAmountRatio 
								+ " fatAmountRatio: " + fatAmountRatio +
								"saturatesAmountRatio: " + saturatesAmountRatio + 
								" saltAmountRatio: " + saltAmountRatio );
								
								db.insertRowNutrionalInfo(ingId,  caloriesAmountRatio, sugarsAmountRatio, fatAmountRatio, 
										saturatesAmountRatio, saltAmountRatio, vegetarian,
										containsNuts, containsGluten, dairy);

								if (ingType
										.equals(EditMenuNameDiscIngsFragment.TAG)) {

									nameDiscIngs();

								}

								if (ingType.equals(EditMenuAddonsFragment.TAG)) {
									addons();

								}
								if (ingType.equals(CreateComplexIngFragment.TAG)) {
									complex();
									
								}

								if (ingType.equals(IngredientsActivity.TAG)) {
									System.out
											.println(" INGREDIENTS Add this new ing to database");
								}
								System.out.println(" followed through");

							}

						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	private void nameDiscIngs() {

		// check to see it item already exsts
		// TODO
		Log.i(TAG, "updating list in name disc ings and returning to fragment");
		//EditMenuNameDiscIngsFragment.updateIngListView((int) rowId, ingName);
		
		System.out.println("insert ing in temp ing table");
		Log.i(TAG, "ingId = " + ingId + " ingName " + ingName);
		
		db.insertRowTempMenuItemIng(ingId, ingName.toUpperCase(), ingUnit);

		
		closeDb();

		EditMenuNameDiscIngsFragment NameDiscIngFrag = (EditMenuNameDiscIngsFragment) getFragmentManager()
				.findFragmentByTag(EditMenuNameDiscIngsFragment.TAG);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, NameDiscIngFrag)
				.addToBackStack("null")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();

	}

	private void addons() {
		//TODO which one right>
		//EditMenuAddonsFragment.updateAddonTable((int) rowId, ingName);
		EditMenuAddonsFragment.updateAddonTable( ingId, ingName, ingUnit);
		closeDb();

		EditMenuAddonsFragment AddonsFrag = (EditMenuAddonsFragment) getFragmentManager()
				.findFragmentByTag(EditMenuAddonsFragment.TAG);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, AddonsFrag).addToBackStack("null")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commit();

	}
	
	private void complex() {
		//TODO 
		CreateComplexIngFragment.updateIngListView(rawIngId, ingName, ingUnit);
		//CreateComplexIngFragment.updateIngListView(ingId, ingName, ingUnit);
		closeDb();
		
		//CreateComplexIngFragment complexFrag = (CreateComplexIngFragment) getFragmentManager()
		//		.findFragmentByTag(CreateComplexIngFragment.TAG);
		
		CreateComplexIngFragment complexIngFrag = new CreateComplexIngFragment();

		Log.i(TAG, "Returning to a complexIng Fragment");

		
		Bundle bundle = new Bundle();
		//bundle.putString("ingType", ingType);
		//bundle.putString("ingComplexName", ingName);
		//bundle.putString("ingComplexUnit", ingUnit);
		
		//// bundle.putString("ingComplexAmount", ingAmount);

		//// bundle.putInt("menuItemId", menuItemId);
		//// bundle.putString("editType", editType);
	
		bundle.putInt("complexIngAmount", complexIngAmount);
		bundle.putString("complexIngName", complexIngName);
		bundle.putString("complexIngUnit", complexIngUnit);
		bundle.putString("ingTypePrev", ingTypePrev);
		
		complexIngFrag.setArguments(bundle);
		
		getFragmentManager()
		.beginTransaction()
		// .replace(R.id.container, new
		// CreateComplexIngFragment(), "complexIng")
		.replace(R.id.container, complexIngFrag, "compexIngFrag")
		.addToBackStack("null")
		.setTransition(
				FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		.commit();

		
	}

	private void openDb() {
		db = new DatabaseAdapter(getActivity());
		db.open();
	}

	private void closeDb() {
		db.close();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		ingUnit = (String) parent.getItemAtPosition(pos);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}