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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class IngUnitsDialogFragment extends DialogFragment implements
		OnItemSelectedListener {

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
/*
	private DatabaseAdapter db;
	private String ingUnit = null;
	private String ingName = null;
	private String ingType = null;
	private long rowId;
	public static final String TAG = "ING_UNITS_DIALOG";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		openDb();

		View DialogView = inflater.inflate(R.layout.dialog_ing_unit, null);
		Bundle bundle = getArguments();

		ingName = bundle.getString("ingName");
		ingType = bundle.getString("ingType");
		ingName.toUpperCase();
		
		Spinner spinner = (Spinner) DialogView.findViewById(R.id.spinnerUnits);
		// Create an array adapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), R.array.units_array,
				android.R.layout.simple_spinner_dropdown_item);
		// Specify the layout when the list of choices appear
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		// apply the adapter to the spinner
		spinner.setAdapter(adapter);
		// set a on item selected listener for the spinner
		spinner.setOnItemSelectedListener(this);
		
		//TODO IMPLEMENT COMPLEX/RAW ING
		final int complexIng = 0;
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(DialogView).setTitle(
				EditMenuAddIngFragment.ingNameClicked);

		builder.setMessage("Pick which units " + ingName + " uses")
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								rowId = db.insertRowIngredients(ingName,
										ingUnit, complexIng);
								
								//TODO specify this amount
								db.insertRowStockLevels((int) rowId, ingName,
										ingUnit, 10000, 20000, 5000);

								if (ingType
										.equals(EditMenuNameDiscIngsFragment.TAG)) {

									nameDiscIngs();

								}

								if (ingType.equals(EditMenuAddonsFragment.TAG)) {
									addons();

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

		EditMenuNameDiscIngsFragment.updateIngListView((int) rowId, ingName);
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

		EditMenuAddonsFragment.updateAddonTable((int) rowId, ingName);
		closeDb();

		EditMenuAddonsFragment AddonsFrag = (EditMenuAddonsFragment) getFragmentManager()
				.findFragmentByTag(EditMenuAddonsFragment.TAG);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, AddonsFrag).addToBackStack("null")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
	*/
}