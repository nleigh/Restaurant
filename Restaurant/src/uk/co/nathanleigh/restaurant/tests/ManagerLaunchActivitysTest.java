package uk.co.nathanleigh.restaurant.tests;

import uk.co.nathanleigh.restaurantsystem.R;
import uk.co.nathanleigh.restaurant.manager.ManagerActivity;
import uk.co.nathanleigh.restaurant.manager.StockLevelsActivity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;

public class ManagerLaunchActivitysTest extends ActivityUnitTestCase<ManagerActivity> {

    public ManagerLaunchActivitysTest() {
		super(ManagerActivity.class);
		// TODO Auto-generated constructor stub
	}

	private Intent mLaunchStockLevelsIntent;

	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchStockLevelsIntent = new Intent(getInstrumentation()
                .getTargetContext(), ManagerActivity.class);
        startActivity(mLaunchStockLevelsIntent, null, null);
        final Button launchNextButton =
                (Button) getActivity()
                .findViewById(R.id.bStockLevels);
    }
	
	@MediumTest
	public void testStockLevelsActivityWasLaunchedWithIntent() {
	   // startActivity(mLaunchStockLevelsIntent, null, null);
	    final Button launchStockLevelsButton =
	            (Button) getActivity()
	            .findViewById(R.id.bStockLevels);
	    launchStockLevelsButton.performClick();

	    final Intent launchStockLevelsIntent = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchStockLevelsIntent);
	   // assertTrue(isFinishCalled());

	    final String payload =
	    		launchStockLevelsIntent.getStringExtra(StockLevelsActivity.EXTRAS_PAYLOAD_KEY);
	    assertEquals("Payload is empty", ManagerActivity.STRING_PAYLOAD, payload);
	}
}
