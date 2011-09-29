package nl.uiterlinden.android.hunted;

import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.SimpleCursorAdapter;

public class Log extends ListActivity {

	private HuntedService mBoundService;
	private boolean mIsBound;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        doBindService();
    }
    
    @Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onStart() {
		if (mBoundService != null) {
			mBoundService.registerLogActivity(Log.this);
		}
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		if (mBoundService != null) {
			mBoundService.registerLogActivity(Log.this);
			refresh();
		}    	
		super.onResume();
	}
	
	public void refresh() {
		((SimpleCursorAdapter)getListAdapter()).getCursor().requery();
	}

	@Override
	protected void onStop() {
		if (mBoundService != null) {
			mBoundService.unregisterLogActivity();
		}		
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		if (mBoundService != null) {
			mBoundService.unregisterLogActivity();
		}		
	    super.onDestroy();
	    doUnbindService();
	}	

	private void fillData() {
        // Get all of the notes from the database and create the item list
        Cursor cursor = mBoundService.getDataProvider().fetchAllLogItems();
        startManagingCursor(cursor);

        String[] from = new String[] { "DIRECTION", "MESSAGE", "MOMENT" };
        int[] to = new int[] { R.id.listItemDirection, R.id.listItem, R.id.listItemDate };
        
        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
            new LogItemListAdapter(getLayoutInflater(), this, R.layout.logitem, cursor, from, to);
        setListAdapter(notes);
    }
	
	void doBindService() {
		System.out.println("Bind hunted service...");
		getApplicationContext().bindService(new Intent(Log.this, HuntedService.class), mConnection,
				Context.BIND_AUTO_CREATE);
	}
	
	void doUnbindService() {
		getApplicationContext().unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			mBoundService = ((HuntedService.LocalBinder) binder).getService();
			mBoundService.registerLogActivity(Log.this);
			fillData();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

}
