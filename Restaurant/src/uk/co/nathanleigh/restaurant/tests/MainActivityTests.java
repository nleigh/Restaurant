package uk.co.nathanleigh.restaurant.tests;

import uk.co.nathanleigh.restaurant.MainActivity;
import uk.co.nathanleigh.restaurantsystem.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivityTests extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity mainActivityTests;
	private TextView mFirstTestText;
	private Button bCustomer;

	public MainActivityTests() {
		super(MainActivity.class);
	}

	public void testPreconditions() {
		assertNotNull("mFirstTestActivity is null", mainActivityTests);
		assertNotNull("mFirstTestText is null", mFirstTestText);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		 setActivityInitialTouchMode(true);

		 mainActivityTests = getActivity();
	       bCustomer = (Button) 
	                mainActivityTests
	                .findViewById(R.id.bCustomer);
		mFirstTestText = (TextView) mainActivityTests
				.findViewById(R.id.tvMainInstruction);
	}

	public void testMyFirstTestTextView_labelText() {
		final String expected = mainActivityTests
				.getString(R.string.main_instruction);
		final String actual = mFirstTestText.getText().toString();
		assertEquals(expected, actual);
	}
	
	@MediumTest
	public void testClickMeButton_clickButtonAndExpectInfoText() {
	   // String expectedInfoText = mainActivityTests.getString(R.string.info_text);
	    TouchUtils.clickView(this, bCustomer);
	   // assertTrue(View.VISIBLE == mainActivityTests.getVisibility());
	   // assertEquals(expectedInfoText, mInfoTextView.getText());
	}

}
