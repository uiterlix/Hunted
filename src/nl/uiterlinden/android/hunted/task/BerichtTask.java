package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class BerichtTask extends AbstractTask {

	private String team;
	private String message;

	public BerichtTask(Context context, HuntedService huntedService, String team, String message) {
		super(context, huntedService);
		this.team = team;
		this.message = message;
	}
	
	public BerichtTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// BERICHT <TEAMNUMMER> <BERICHT>
		return "BERICHT " + team + " " + message;
	}


	@Override
	int getDialogResource() {
		return R.layout.berichtdialog;
	}

	@Override
	String getTaskTitle() {
		return "Bericht";
	}

	@Override
	void processInput(View dialogView) {
		EditText txtTeam = (EditText) dialogView.findViewById(R.id.editBerichtTeam);
		EditText txtMessage = (EditText) dialogView.findViewById(R.id.editBerichtInhoud);
		this.team = txtTeam.getText().toString();
		this.message = txtMessage.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		// TODO Auto-generated method stub
		
	}

}
