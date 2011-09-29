package nl.uiterlinden.android.hunted.service;

import nl.uiterlinden.android.hunted.Log;
import nl.uiterlinden.android.hunted.Map;
import nl.uiterlinden.android.hunted.Preferences;
import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.io.SMSReceiver;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class HuntedService extends Service {

	private final IBinder binder = new LocalBinder();
	private Log log;
	private Map map;
	private BroadcastReceiver receiver;
	private LocationManager locationManager;
	private DataProvider dataProvider;
	private Location location;
	private DispLocListener locListenD;
	
    public class LocalBinder extends Binder {
    	public HuntedService getService() {
            return HuntedService.this;
        }
    }
    
	@Override
	public void onCreate() {
		  IntentFilter filter = new IntentFilter();
		  filter.addAction(SMSReceiver.SMS_RECEIVED);

		  receiver = new BroadcastReceiver() {
		    @Override
			public void onReceive(Context context, Intent intent) {
				// handle incoming sms
				System.out.println("Notify log");
				if (log != null) {
					log.refresh();
				} else {
					System.out.println("Log is not available");
				}
			}
		  };

		  dataProvider = new DataProvider(this);
		  registerReceiver(receiver, filter);
		  
		  String context = Context.LOCATION_SERVICE; 
		  locationManager = (LocationManager)getSystemService(context); 

		  Criteria crta = new Criteria(); 
		  crta.setAccuracy(Criteria.ACCURACY_FINE); 
		  crta.setAltitudeRequired(false); 
		  crta.setBearingRequired(false); 
		  crta.setCostAllowed(true); 
		  crta.setPowerRequirement(Criteria.POWER_LOW); 
		  
		// ask the Location Manager to send us location updates
		  location = locationManager.getLastKnownLocation("gps");
		  System.out.println("****** Location: " + location);
	      startLocationUpdates();
	}
	
	
	
	@Override
	public void onDestroy() {
		dataProvider.close();
	}

	public void stopLocationUpdates() {
		if (locListenD != null) {
			locationManager.removeUpdates(locListenD);
			locListenD = null;
			Toast.makeText(this, "Location updates stopped.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void startLocationUpdates() {
		if (locListenD == null) {
			locListenD = new DispLocListener();
			locationManager.requestLocationUpdates("gps", 30000L, 10.0f, locListenD);
			Toast.makeText(this, "Location updates started.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean locationUpdatesEnabled() {
		return locListenD != null;
	}
	
	public Location getLocation() {
		if (location == null) {
			location = locationManager.getLastKnownLocation("gps");
		}
		return location;
	}
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public boolean validatePreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = preferences.getString(Preferences.PREF_PHONENUMBER, this.getString(R.string.defaultTelefoonNummer));
        String username = preferences.getString(Preferences.PREF_TEAMNUMBER, this.getString(R.string.defaultTeamNummer));
        String password = preferences.getString(Preferences.PREF_TEAMPASSWORD, this.getString(R.string.defaultTeamPassword));
        String hostName = preferences.getString(Preferences.PREF_HUNTEDHQHOST, this.getString(R.string.defaultHqHost));
        int port = Integer.parseInt(preferences.getString(Preferences.PREF_HUNTEDHQPORT, this.getString(R.string.defaultHqPort)));
		
        if (username.equals(this.getString(R.string.defaultTeamNummer)) || phoneNumber.equals(this.getString(R.string.defaultTelefoonNummer))) {
        	Toast.makeText(this, "Vul eerst teamnummer, wachtwoord en telefoonnummer in bij de instellingen.", Toast.LENGTH_LONG).show();
        	return false;
        }
        
        return true;
	}
	
	private String normalizeNumber(String number) {
		String result = null;
		// 0626868669
		// 0031626868669
		// +31626868669
		if (number.startsWith("00") && number.length() == 13) {
			return number;
		}
		if (number.startsWith("+") && number.length() == 12) {
			return "00" + number.substring(1);
		}
		if (number.startsWith("0") && number.length() == 10) {
			return "0031" + number.substring(1);
		}
		
		return result;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public void registerLogActivity(Log log) {
		this.log = log;
	}
	
	public void unregisterLogActivity() {
		this.log = null;
	}
	
	public boolean isLogAcivityActive() {
		return (log != null);
	}
	
	public void registerMapActivity(Map map) {
		this.map = map;
	}
	
	public void unregisterMapActivity() {
		this.map = null;
	}
	
	public boolean isMapActivityActive() {
		return (map != null);
	}
	
	public void refreshMap() {
		if (map != null) {
			map.createMapOverlay();
			map.moveMapToCurrentTargetClue();
		}
	}
	
    private class DispLocListener implements LocationListener {

        @Override
        public void onLocationChanged(Location newLocation) {
            // update TextViews
            location = newLocation;
//            Toast.makeText(HuntedService.this, "Location changed.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
