package uk.co.nathanleigh.restaurant.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class StatisticsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		// Show the Up button in the action bar.
		setupActionBar();
		//copyDBToSDCard();
		exportDB();
	}
	
	
	public void exportDB(){
	    try {
	        File sd = Environment.getExternalStorageDirectory();
	        if (sd.canWrite()) {
	            String currentDBPath = "data/data/uk.co.nathanleigh.restaurant/databases/Database.db";
	            String backupDBPath = sd + "/filename.db";
	            File currentDB = new File(currentDBPath);
	            File backupDB = new File(backupDBPath);

	            if (currentDB.exists()) {
	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void copyDBToSDCard() {
		String DATABASE_NAME = "Database.db";
	    try {
	        InputStream myInput = new FileInputStream("/data/data/uk.co.nathanleigh.restaurant/databases/"+DATABASE_NAME);

	        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+DATABASE_NAME);
	        if (!file.exists()){
	            try {
	                file.createNewFile();
	            } catch (IOException e) {
	                Log.i("FO","File creation failed for " + file);
	            }
	        }

	        OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/"+DATABASE_NAME);

	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = myInput.read(buffer))>0){
	            myOutput.write(buffer, 0, length);
	        }

	        //Close the streams
	        myOutput.flush();
	        myOutput.close();
	        myInput.close();
	        Log.i("FO","copied");

	    } catch (Exception e) {
	        Log.i("FO","exception="+e);
	    }


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
		getMenuInflater().inflate(R.menu.statistics, menu);
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

}
