package nl.uiterlinden.android.hunted;

import java.text.DecimalFormat;

import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.domain.Clue;
import nl.uiterlinden.android.hunted.service.HuntedService;
import nl.uiterlinden.android.hunted.util.HuntedLatLonLocation;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;

public class Cockpit extends Activity {

	private Handler mHandler = new Handler();
	DecimalFormat dec = new DecimalFormat("###.##");
	DecimalFormat decLoc = new DecimalFormat("###.#####");
	
	TextView txtGoal;
	TextView txtDistance;
	TextView txtLocation;
	TextView txtAccuracy;
	TextView txtSpeed;
	
	private HuntedService mBoundService;
	private boolean mIsBound;
	private UpdateUITask updateTask;
	private TextView txtFoundClueCount;
	private TextView txtImmunityPeriod;
	private TextView txtCatchCount;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        setContentView(R.layout.cockpit);
        
        txtGoal = (TextView) findViewById(R.id.txtGoal);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtAccuracy = (TextView) findViewById(R.id.txtAccuracy);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtFoundClueCount = (TextView) findViewById(R.id.txtCluesFound);
        txtImmunityPeriod = (TextView) findViewById(R.id.txtImmunityPeriod);
        txtCatchCount = (TextView) findViewById(R.id.txtCatchCount);
    }
    
    @Override
	protected void onResume() {
    	if (mBoundService != null) {
    		if (updateTask == null) {
    			updateTask = new UpdateUITask();
    		}
    		mHandler.postDelayed(updateTask, 3000);
    	}
		super.onResume();
	}
    
    	@Override
	protected void onPause() {
    	if (updateTask != null) {
    		mHandler.removeCallbacks(updateTask);
    	}
		super.onPause();
	}

	private void calculate() {
		if (mBoundService == null) {
			return;
		}
    	// (re)calculate the numbers for the cockpit
//		Toast.makeText(Cockpit.this, "Recalculate", Toast.LENGTH_SHORT).show();

		String currentTarget = mBoundService.getDataProvider().getStatus(DataProvider.CURRENT_TARGET);
		Location currentLocation = mBoundService.getLocation();
		if (currentTarget != null) {
			txtGoal.setText(currentTarget);
			
			if (currentLocation != null) {
				Clue targetClue = mBoundService.getDataProvider().getClue(currentTarget);
				// calculate distance to current target
				Location targetLocation = new Location("self");
				HuntedLatLonLocation location = HuntedLatLonLocation.parseWGS84DegreesLatLon(targetClue.getCoordinate());
				targetLocation.setLatitude(location.getX());
				targetLocation.setLongitude(location.getY());
				
				float distance = targetLocation.distanceTo(currentLocation);
				txtDistance.setText("" + dec.format(distance) + "m");
			}
		}
		
		if (currentLocation != null) {
			txtLocation.setText(decLoc.format(currentLocation.getLatitude()) + " " + decLoc.format(currentLocation.getLongitude()));
			txtAccuracy.setText(currentLocation.getAccuracy() + "m");
			txtSpeed.setText(dec.format(currentLocation.getSpeed()) + "m/s");
		}
		
		// catch count
		String catchCountString = mBoundService.getDataProvider().getStatus(DataProvider.CATCH_COUNT);
		int catchCount = 0;
		if (catchCountString != null) {
			catchCount = Integer.parseInt(catchCountString);
		}
		txtCatchCount.setText("" + catchCount);
		
		// immunity time
		long now = System.currentTimeMillis();
		String latestCatchString = mBoundService.getDataProvider().getStatus(DataProvider.LAST_CATCH_TIME);
		long latestCatchTime = 0;
		if (latestCatchString != null) {
			latestCatchTime = Long.parseLong(latestCatchString);
		}
		if (latestCatchTime > 0) {
			long diff = now - latestCatchTime;
			int minutes = (int) (30f - (float) diff / 60000f ) ;
			if (minutes > 0) {
				txtImmunityPeriod.setText("" + minutes + " minuten");
			} else {
				txtImmunityPeriod.setText("Niet immuun!");
			}
		} else {
			txtImmunityPeriod.setText("Niet immuun!");
		}
		
		// clues found
		int foundClueCount = mBoundService.getDataProvider().getFoundClueCount();
		txtFoundClueCount.setText("" + foundClueCount);
    }
	
	void doBindService() {
		System.out.println("Bind hunted service...");
		getApplicationContext().bindService(new Intent(Cockpit.this, HuntedService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
		getApplicationContext().unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("Huntedservice connected to Cockpit");
			mBoundService = ((HuntedService.LocalBinder) binder).getService();
			calculate();
			// start scheduled update
			updateTask = new UpdateUITask();
            mHandler.postDelayed(updateTask, 5000);
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
	
	private class UpdateUITask implements Runnable {
		
		public UpdateUITask() {
			
		}
		public void run() {
			calculate();
			mHandler.postDelayed(this, 5000);
		}
	}
}
