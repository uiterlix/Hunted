package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class GaWegTask extends AbstractTask {

	private String team;

	public GaWegTask(Context context, HuntedService huntedService, String team) {
		super(context, huntedService);
		this.team = team;
	}
	
	public GaWegTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// GA WEG <TEAMNR>
		return "GAWEG " + team;
	}


	@Override
	int getDialogResource() {
		return R.layout.gawegdialog;
	}

	@Override
	String getTaskTitle() {
		return "Ga weg";
	}
	
	@Override
	void processInput(View activity) {
		EditText txtTeam = (EditText) activity.findViewById(R.id.editGaWegTeam);
		this.team = txtTeam.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		// TODO Auto-generated method stub
		
	}
}
