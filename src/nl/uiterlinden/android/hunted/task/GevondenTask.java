package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class GevondenTask extends AbstractTask {

	private String woord;
	private String post;

	public GevondenTask(Context context, HuntedService huntedService, String woord, String post) {
		super(context, huntedService);
		this.woord = woord;
		this.post = post;
	}
	
	public GevondenTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// GEVONDEN <WACHTWOORD> NAAR <POST>/EINDPUNT
		return "GEVONDEN " + woord + " NAAR " + post;
	}


	@Override
	int getDialogResource() {
		return R.layout.gevondendialog;
	}

	@Override
	String getTaskTitle() {
		return "Gevonden";
	}
	
	@Override
	void processInput(View activity) {
		EditText txtWoord = (EditText) activity.findViewById(R.id.editGevondenWoord);
		EditText txtNaar = (EditText) activity.findViewById(R.id.editGevondenNaar);
		this.woord = txtWoord.getText().toString();
		this.post = txtNaar.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		String currentTarget = dataProvider.getStatus(DataProvider.CURRENT_TARGET);
		dataProvider.updateStatus(DataProvider.CURRENT_TARGET, post);
		dataProvider.foundClue(currentTarget, woord);
	}
}
