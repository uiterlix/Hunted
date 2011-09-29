package nl.uiterlinden.android.hunted;

import nl.uiterlinden.android.hunted.service.HuntedService;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;

public class HuntedMain extends TabActivity {
	
	private static final String LOG_TAG = "Hunted";
	
	private HuntedService mBoundService;
	private boolean mIsBound;
	private boolean updatesWereEnabled = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        doBindService();

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, Map.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("map").setIndicator("Kaart")
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, Cockpit.class);
        spec = tabHost.newTabSpec("cockpit").setIndicator("Cockpit")
        				.setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, SMS.class);
        spec = tabHost.newTabSpec("sms").setIndicator("SMS")
        				.setContent(intent);
        tabHost.addTab(spec);

        
//        intent = new Intent().setClass(this, Stand.class);
//        spec = tabHost.newTabSpec("stand").setIndicator("Stand")
//        				.setContent(intent);
//        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, nl.uiterlinden.android.hunted.Log.class);
        spec = tabHost.newTabSpec("log").setIndicator("Log")
        				.setContent(intent);
        tabHost.addTab(spec);
    }
    
	@Override
	protected void onPause() {
		if (mBoundService != null) {
			updatesWereEnabled = mBoundService.locationUpdatesEnabled();
			mBoundService.stopLocationUpdates();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (updatesWereEnabled && mBoundService != null) {
			mBoundService.startLocationUpdates();
		}
		super.onResume();
	}

	void doBindService() {
		System.out.println("Bind hunted service...");
		getApplicationContext().bindService(new Intent(HuntedMain.this, HuntedService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
		getApplicationContext().unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("Hunted service connected....");
			mBoundService = ((HuntedService.LocalBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
}