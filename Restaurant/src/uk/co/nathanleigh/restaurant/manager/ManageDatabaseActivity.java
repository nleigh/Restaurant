package uk.co.nathanleigh.restaurant.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;

import uk.co.nathanleigh.restaurant.DatabaseAdapter;
import uk.co.nathanleigh.restaurantsystem.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ManageDatabaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_database);
		// Show the Up button in the action bar.
		setupActionBar();

		File direct = new File(Environment.getExternalStorageDirectory(),
				"/Exam Creator");

		if (!direct.exists()) {
			// direct.getParentFile().mkdirs();
			if (direct.mkdir()) {
				// directory is created;
				//exportDB();
			}

		}
		
		//db();
		 exportDb();

		// importDB();
	}

	public void db() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();

		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hours = c.get(Calendar.HOUR);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);

		String currentDBPath = "/data/uk.co.nathanleigh.restaurant/databases/Database.db";
		String backUpSystemData = "myDatabase-" + year + "-" + month + "-"
				+ day + "-" + hours + "-" + minute + "-" + second + ".sqlite";
		File currentDB = new File(data, currentDBPath);
		File path = new File(sd + "/Database/");
		if (!path.exists()) {
			path.mkdirs();
		}
		try {
		

			File backupDB = new File(path, backUpSystemData);
			FileChannel src = new FileInputStream(currentDBPath).getChannel();
			FileChannel dst = new FileOutputStream(backUpSystemData).getChannel();
			dst.transferFrom(src, 0, src.size());
			src.close();
			dst.close();

		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
					.show();

		}
	}

	// importing database
	private void importDB() {
		// TODO Auto-generated method stub

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//" + "PackageName"
						+ "//databases//" + "DatabaseName";
				String backupDBPath = "/BackupFolder/DatabaseName";
				File backupDB = new File(data, currentDBPath);
				File currentDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(getBaseContext(), backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
					.show();

		}
	}

	public static void copyFile(FileInputStream fromFile,
			FileOutputStream toFile) throws IOException {
		FileChannel fromChannel = null;
		FileChannel toChannel = null;
		try {
			fromChannel = fromFile.getChannel();
			toChannel = toFile.getChannel();
			fromChannel.transferTo(0, fromChannel.size(), toChannel);
		} finally {
			try {
				if (fromChannel != null) {
					fromChannel.close();
				}
			} finally {
				if (toChannel != null) {
					toChannel.close();
				}
			}
		}
	}

	private void exportDb() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		FileChannel source = null;
		FileChannel destination = null;
		String currentDBPath = "/data/" + "uk.co.nathanleigh.restaurant"
				+ "/databases/" + DatabaseAdapter.DATABASE_NAME;
		String backupDBPath = DatabaseAdapter.DATABASE_NAME;
		File currentDB = new File(data, currentDBPath);
		File backupDB = new File(sd, backupDBPath);
		try {
			source = new FileInputStream(currentDB).getChannel();
			destination = new FileOutputStream(backupDB).getChannel();
			destination.transferFrom(source, 0, source.size());
			source.close();
			destination.close();
			Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// exporting database
	private void exportDB() {
		// TODO Auto-generated method stub

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//"
						+ "uk.co.nathanleigh.restaurant" + "//databases//"
						+ DatabaseAdapter.DATABASE_NAME;
				String backupDBPath = "/Database";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				Toast.makeText(getBaseContext(), backupDB.toString(),
						Toast.LENGTH_LONG).show();

			}
		} catch (Exception e) {

			Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
					.show();

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
		getMenuInflater().inflate(R.menu.manage_database, menu);
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
