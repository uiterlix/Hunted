package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.Preferences;
import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;

public class AanmeldTask extends AbstractTask {

	public AanmeldTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// AANMELDEN <TEAM WACHTWOORD>
		return "AANMELDEN " + getPreferences().getString(Preferences.PREF_TEAMPASSWORD, null);
	}

	@Override
	int getDialogResource() {
		return R.layout.aanmelddialog;
	}

	@Override
	String getTaskTitle() {
		return "Aanmelden";
	}

	@Override
	void processInput(View dialogView) {
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		
	}

}
