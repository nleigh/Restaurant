package uk.co.nathanleigh.restaurant.manager;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class StockLevelListAdapterTwo extends CursorAdapter{

	private LayoutInflater inflator;
	private Context context;
	private DatabaseAdapter myDB;
	private Cursor TempCursor;
	private int rowId;
	private int ingId;
	private  String ingName; 
	private String ingUnit;
	private int ingAmount;
	private int ingRecAmount;
	private int ingWarningAmount;
	public static final String TAG = "STOCK_LEVELS";

	
	public StockLevelListAdapterTwo(Context context, Cursor c, int flags) {
		super(context, c, flags);
		inflator = LayoutInflater.from(context);
		this.context = context;
	} 
	
	private void openDB() {
		myDB = new DatabaseAdapter(context);
		myDB.open();
	}

	private void closeDB() {
		myDB.close();
	}


	@Override
	public void bindView(View view, final Context context, Cursor cursor) {
		
		TextView tvIngName = (TextView) view.findViewById(R.id.tvIngName);
		TextView tvIngUnit = (TextView) view.findViewById(R.id.tvIngUnit);
		TextView tvIngAmount = (TextView) view.findViewById(R.id.tvIngCurrentAmount);
		TextView tvIngRecAmount = (TextView) view.findViewById(R.id.tvIngRecAmount);
		TextView tvIngWarningAmount = (TextView) view.findViewById(R.id.tvIngWarningAmount);
		
		 System.out.println("getting values from cursor");
		 rowId = (int) cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_ROWID)); 
		 ingId = (int) cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGID));
		 ingName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGNAME)); 
		  ingUnit = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGUNIT)); 
		  ingAmount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_INGAMOUNT)); 
		 ingRecAmount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_RECAMOUNT)); 
		 ingWarningAmount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseAdapter.KEY_WARNINGAMOUNT)); 
		
		 System.out.println("setting text views stock levels");
		 tvIngName.setText(ingName);
		 tvIngUnit.setText(ingUnit);
		 tvIngAmount.setText("" + ingAmount);
		 tvIngRecAmount.setText("" + ingRecAmount);
		 tvIngWarningAmount.setText("" + ingWarningAmount);
		 
		Button editButton = (Button) view.findViewById(R.id.bEditAmounts);
	
	       
	      
	        editButton.setVisibility(ImageButton.VISIBLE); // need this
	        
		
		editButton.setOnClickListener(new OnClickListener(){
		
			@Override
			public void onClick(View v){
				
				StockLevelDialogFragment df = new StockLevelDialogFragment();
				
				Bundle args = new Bundle();
				args.putInt("rowId", rowId);
				args.putInt("ingId", ingId);
				args.putString("rowId", ingName);
				args.putString("ingUnit", ingUnit);
				args.putInt("ingAmount", ingAmount);
				args.putInt("ingRecAmount", ingRecAmount);
				args.putInt("ingWarningAmount", ingWarningAmount);
				
				df.setArguments(args);
				//df.show(getFragmentManager(), TAG);
				StockLevelsActivity.showStockLevelDialog(df, context);
			//	df.show(FragmentManager , ingName);
				//FragmentManager
			
		        }
	
			
		});
		
		
	}

	

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.item_view_stock_level , parent, false);
		bindView(v,context,cursor);
		return v;
		
	
	}
}
