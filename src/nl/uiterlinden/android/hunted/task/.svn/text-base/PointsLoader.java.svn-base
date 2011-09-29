package nl.uiterlinden.android.hunted.task;

import java.util.ArrayList;
import java.util.List;

import nl.uiterlinden.android.hunted.Map;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.data.ProgressMonitor;
import nl.uiterlinden.android.hunted.domain.Clue;
import nl.uiterlinden.android.hunted.io.Client;
import nl.uiterlinden.android.hunted.io.HuntedIOException;
import nl.uiterlinden.android.hunted.service.HuntedService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PointsLoader extends AsyncTask<Void, Integer, Void> {

	private static final String LOG_TAG = "PointsLoader";
	
	private final Context context;
	ProgressDialog dialog;
	boolean error = false;

	private final Client client;

	private final HuntedService huntedService;
	
	public PointsLoader(Context context, Client client, HuntedService huntedService) {
		this.context = context;
		this.client = client;
		this.huntedService = huntedService;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
        dialog.setMessage("Punten laden");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		List<Clue> clues = new ArrayList<Clue>();
		Log.d(LOG_TAG, "Create new client");
        publishProgress(30);
        Log.d(LOG_TAG, "Retrieve");
        String result;
		try {
			result = client.retrieve(Client.FETCH_CLUES_PATH, null);

	        Log.d(LOG_TAG, "Result: " + result);
	        try {
	        	JSONObject json = new JSONObject(result);
				JSONArray array = json.getJSONArray("clues");
				for (int i = 0; i < array.length(); i++) {
					JSONObject clueObject = array.getJSONObject(i);
					String name = clueObject.getString("name");
					String coordinate = clueObject.getString("coordinate");
					Clue clue = new Clue(name, coordinate);
					clues.add(clue);
				}
				Log.d(LOG_TAG, "Array: " + array.length());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			publishProgress(50);
			Log.d(LOG_TAG, "Delete all clues");
			huntedService.getDataProvider().deleteAllClues();
			Log.d(LOG_TAG, "Insert " + clues.size() + " clues");
			huntedService.getDataProvider().insertClues(clues, new ProgressMonitor() {
				
				private int size;

				@Override
				public void update(int progress) {
					int newProgress = (int) (new Integer(progress).floatValue() / new Integer(size).floatValue() * 50f + 50f);
					publishProgress(newProgress);
					
				}
				
				@Override
				public void setMaxProgress(int size) {
					this.size = size;
					
				}
			});
		} catch (HuntedIOException ex) {
			error = true;
		}
		publishProgress(100);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (error) {
			Toast.makeText(context, "Er is een fout opgetreden. Controleer de verbinding en de instellingen en probeer het opnieuw.",
	  		          Toast.LENGTH_LONG).show();				
		} else {
			Toast.makeText(context, "Punten ingeladen. Happy Hunted!",
  		          Toast.LENGTH_SHORT).show();
		}
		((Map)context).createMapOverlay();
		((Map)context).resetPosition();
		dialog.hide();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		Integer amount = values[0];
		if (amount == 60) {
			Toast.makeText(context, "Punten opslaan.",
	  		          Toast.LENGTH_SHORT).show();
		}
		dialog.setProgress(amount);
	}


}