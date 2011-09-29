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

public class UndoTask extends AsyncTask<Void, Integer, Void> {

	private final HuntedService huntedService;
	private final Context context;

	public UndoTask(HuntedService huntedService, Context context) {
		this.huntedService = huntedService;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		
	}

	@Override
	protected Void doInBackground(Void... params) {
		huntedService.getDataProvider().undo();
		if (huntedService.isMapActivityActive()) {
			huntedService.refreshMap();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		Toast.makeText(context, "Laatste stap ongedaan gemaakt.",
	  		          Toast.LENGTH_SHORT).show();				

	}

}