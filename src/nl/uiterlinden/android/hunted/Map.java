package nl.uiterlinden.android.hunted;

import java.util.List;

import nl.uiterlinden.android.hunted.command.CommandExecutor;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.domain.Clue;
import nl.uiterlinden.android.hunted.io.Client;
import nl.uiterlinden.android.hunted.map.ClueOverlay;
import nl.uiterlinden.android.hunted.service.HuntedService;
import nl.uiterlinden.android.hunted.task.AanmeldTask;
import nl.uiterlinden.android.hunted.task.PointsLoader;
import nl.uiterlinden.android.hunted.task.UndoTask;
import nl.uiterlinden.android.hunted.util.HuntedLatLonLocation;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity {
	
	private static final String LOG_TAG = "Map";
	private static final int RELOAD_POINTS_DIALOG = 1;
	private static final int RESET_DIALOG = 2;
	
	private MapView mapView;
	
	private Drawable targetIcon;
	private Drawable flagIcon;
	private Drawable checkIcon;
	
	private Client client;
	
	private HuntedService mBoundService;
	private boolean mIsBound;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.settings:
	        showSettingsDialog();
	        return true;
	    case R.id.loadPoints:
	    	if (mBoundService.validatePreferences()) {
		    	if (mBoundService.getDataProvider().getClueCount() > 0) {
		    		showDialog(RELOAD_POINTS_DIALOG);
		    	} else {
		    		new PointsLoader(this, client, mBoundService).execute(null);
		    	}
	    	}
	    	return true;
	    case R.id.resetApp:
	    	showDialog(RESET_DIALOG);
	    	return true;
	    case R.id.toggleLocation:
	    	toggleLocationOverlay();
	    	return true;
	    case R.id.undoLatestMove:
	    	new UndoTask(mBoundService, this).execute(null);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showSettingsDialog() {
  	  Intent myIntent = new Intent(Map.this, Preferences.class);
	  Map.this.startActivity(myIntent);	    
	}
	
	

    @Override
	protected void onDestroy() {
		doUnbindService();
		super.onDestroy();
	}
    
	@Override
	protected void onStop() {
		super.onStop();
	}

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        
        // init icons
        targetIcon = getResources().getDrawable(R.drawable.target);
        targetIcon.setBounds(0, 0, targetIcon.getIntrinsicWidth(), targetIcon.getIntrinsicHeight());        

        flagIcon = getResources().getDrawable(R.drawable.redflag);
        flagIcon.setBounds(0, 0, flagIcon.getIntrinsicWidth(), flagIcon.getIntrinsicHeight());        
        
        checkIcon = getResources().getDrawable(R.drawable.check);
        checkIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());        
        
        // initialize client
        // get client parameters from preferences
        client = new Client(this);
        CommandExecutor.initialize(client, getPreferences(Preferences.MODE_PRIVATE));
        
    }
    
    public void resetPosition() {
    	// set center and zoom level
        // hunted start location (N 52¡16.788 E 006¡10.860)
        HuntedLatLonLocation startLocation = HuntedLatLonLocation.parseWGS84DegreesLatLon("N52 16.788 E06 10.860");
        mapView.getController().setCenter(new GeoPoint((int)(startLocation.getX() * 1E6), (int)(startLocation.getY() * 1E6)));
        mapView.getController().setZoom(13);    	
    }
    
    @Override
	protected void onResume() {
    	System.out.println("onResume!");
    	if (myLocOverlay != null) {
    		myLocOverlay.enableCompass();
    		myLocOverlay.enableMyLocation();
    	}
    	if (mapView.getOverlays().size() > 0) {
    		createMapOverlay();
    	}
    	if (mBoundService != null) {
    		mBoundService.registerMapActivity(Map.this);
    	}
    	super.onResume();
	}

	@Override
	protected void onPause() {
		System.out.println("onPause!");
		if (myLocOverlay != null) {
			myLocOverlay.disableCompass();
			myLocOverlay.disableMyLocation();
		}
		if (mBoundService != null) {
			mBoundService.unregisterMapActivity();
		}
		super.onPause();
	}

	public void createMapOverlay() {
    	// create the map overlay
		Log.d(LOG_TAG, "Create map overlay");
        List<Overlay> mapOverlays = mapView.getOverlays();
        if (clueOverlay == null) {
        	clueOverlay = new ClueOverlay(this, mBoundService, flagIcon);
        	mapOverlays.add(clueOverlay);
        } else {
        	clueOverlay.clear();
        }
        
        List<Clue> clues = mBoundService.getDataProvider().fetchClues();
        Log.d(LOG_TAG, "read clues from database: " + clues.size());
        String currentTarget = mBoundService.getDataProvider().getStatus(DataProvider.CURRENT_TARGET);
        Log.d(LOG_TAG, "current target: " + currentTarget);
        for (Clue clue : clues) {
//        	Log.d(LOG_TAG, "Overlay item for clue: " + clue);
        	try {
        		GeoPoint point = getPointForClue(clue);
        		OverlayItem overlayItem = new OverlayItem(point, clue.getName(), clue.getName());
        		if (currentTarget != null && clue.getName().equals(currentTarget)) {
        			// target icon
        			overlayItem.setMarker(clueOverlay.boundToCenter(targetIcon));
        		}
        		if (clue.getFound() != null) {
        			// check icon
        			overlayItem.setMarker(clueOverlay.boundToCenter(checkIcon));
        		}
        		clueOverlay.addOverlay(overlayItem, this);
        	} catch (Exception e) {
        		// no valid point
        	}
        }
        if (clues.size() > 0) {
        	Log.d(LOG_TAG, "added overlay");
        }
        clueOverlay.populateOverlay();
    }
	
	public static GeoPoint getPointForClue(Clue clue) {
    	HuntedLatLonLocation location = HuntedLatLonLocation.parseWGS84DegreesLatLon(clue.getCoordinate());
    	GeoPoint point = new GeoPoint((int)(location.getX() * 1E6), (int)(location.getY() * 1E6));
    	return point;
	}
	
	private void moveMapToClue(Clue clue) {
		GeoPoint point = getPointForClue(clue);
		mapView.getController().animateTo(point);
	}
	
	public void moveMapToCurrentTargetClue() {
		if (mBoundService != null) {
			String currentTarget = mBoundService.getDataProvider().getStatus(DataProvider.CURRENT_TARGET);
			if (currentTarget != null) {
				moveMapToClue(mBoundService.getDataProvider().getClue(currentTarget));
			} else {
				mapView.getController().scrollBy(1, 1);
			}
		}
	}
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    } 
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		if (id == RELOAD_POINTS_DIALOG) {
		builder.setMessage("Weet je zeker dat je de punten opnieuw wilt inladen? Alle spelvoortgang wordt gewist.")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Log.d(LOG_TAG, "Reload points");
		        	    new PointsLoader(Map.this, client, mBoundService).execute(null);
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		           }
		       });
		} else if (id == RESET_DIALOG) {
			builder.setMessage("Weet je zeker dat je de applicatie wilt resetten? Alle spelvoortgang wordt gewist.")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
			   			mBoundService.getDataProvider().reset();
				    	createMapOverlay();
				    	resetPosition();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.cancel();
		           }
		       });			
		}
		
		AlertDialog alert = builder.create();
		return alert;
	}
	
	void doBindService() {
		System.out.println("Bind hunted service...");
		getApplicationContext().bindService(new Intent(Map.this, HuntedService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
		getApplicationContext().unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			System.out.println("Hunted service connected....");
			mBoundService = ((HuntedService.LocalBinder) binder).getService();
	        createMapOverlay();
	        initMyLocation();
	        
	        resetPosition();
	        mBoundService.registerMapActivity(Map.this);
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
	
	private MyLocationOverlay myLocOverlay;
	private ClueOverlay clueOverlay;

	/**
	 * Initialises the MyLocationOverlay and adds it to the overlays of the map
	 */
	private void initMyLocation() {
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		myLocOverlay.enableCompass();
		mapView.getOverlays().add(myLocOverlay);
 
	}	
	
	public void toggleLocationOverlay() {
		if (myLocOverlay != null) {
			myLocOverlay.disableCompass();
			myLocOverlay.disableMyLocation();
			mapView.getOverlays().remove(myLocOverlay);
			myLocOverlay = null;
			mBoundService.stopLocationUpdates();
		} else {
			initMyLocation();
			mBoundService.startLocationUpdates();
		}
	}

	public DataProvider getPointsProvider() {
		return mBoundService.getDataProvider();
	}

}
