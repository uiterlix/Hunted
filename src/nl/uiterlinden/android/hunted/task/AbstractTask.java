package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.Preferences;
import nl.uiterlinden.android.hunted.SMS;
import nl.uiterlinden.android.hunted.command.Command;
import nl.uiterlinden.android.hunted.command.CommandExecutor;
import nl.uiterlinden.android.hunted.command.CommandResult;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.domain.LogItem;
import nl.uiterlinden.android.hunted.domain.LogItem.Direction;
import nl.uiterlinden.android.hunted.io.HuntedIOException;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

// AANMELDEN
// VERTREK NAAR <POST>
// GEVONDEN <WACHTWOORD> NAAR <POST>/EINDPUNT
// CONTROLE <WOORD>
// WISSEL NAAR <POST>
// EINDPUNT <WOORD>
// BERICHT <TEAMNUMMER> <BERICHT>
public abstract class AbstractTask extends AsyncTask<Void, Integer, Void>
		implements Command {

	protected final Context context;
	protected CommandResult commandResult;
	private final HuntedService huntedService;

	public AbstractTask(Context context, HuntedService huntedService) {
		this.context = context;
		this.huntedService = huntedService;
	}
	
	protected SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	@Override
	protected Void doInBackground(Void... args) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		try {
			String sender = preferences.getString(Preferences.PREF_PHONENUMBER, null);
			System.out.println("Sender: " + sender);
			LogItem logItem = new LogItem(Direction.OUT, getCommandString());
			huntedService.getDataProvider().storeLogItem(logItem);
			commandResult = CommandExecutor.getInstance().executeCommandUsingData(this, sender);
		} catch (HuntedIOException e) {
			// SMS Fallback
			publishProgress(new Integer(-1));
			SmsManager sm = SmsManager.getDefault();
			// HERE IS WHERE THE DESTINATION OF THE TEXT SHOULD GO
			String number = preferences.getString(Preferences.PREF_HQPHONENUMBER, null);
			sm.sendTextMessage(number, null, "HUNT " + getCommandString(), null, null);
			commandResult = new CommandResult();
		}
		postExecute(huntedService.getDataProvider());
		if (huntedService.isMapActivityActive()) {
			huntedService.refreshMap();
		}
		return null;
	}
	
	
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if (values[0].intValue() == -1) {
			// notify SMS workaround
			Toast.makeText(context, "Error sending data message, sending SMS instead.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Yet to handle hunted exception from background action.
		if (commandResult.hasErrors()) {
			Toast.makeText(context, commandResult.getErrorMessage(),
	  		          Toast.LENGTH_LONG).show();				
		} else {
			Toast.makeText(context, "Bericht verstuurd: " + getCommandString(),
	  		          Toast.LENGTH_LONG).show();				
		}
	}
	
	abstract int getDialogResource();
	
	abstract String getTaskTitle();
	
	abstract void processInput(View dialogView);
	
	abstract void postExecute(DataProvider dataProvider);
	
	public void handleTaskDialog(final Activity activity) {
		LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(getDialogResource(), null);
        Dialog dialog = new AlertDialog.Builder(context)
            .setTitle(getTaskTitle())
            .setView(dialogView)
            .setPositiveButton("Versturen", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	processInput(dialogView);
                    /* User clicked OK so do some stuff */
                	execute(null);
                }
            })
            .setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// do nothing
                    /* User clicked cancel so do some stuff */
                }
            })
            .create();
        dialog.show();
	}

}
