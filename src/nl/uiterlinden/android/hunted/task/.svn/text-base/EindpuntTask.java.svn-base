package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class EindpuntTask extends AbstractTask {

	private String woord;

	public EindpuntTask(Context context, HuntedService huntedService, String woord) {
		super(context, huntedService);
		this.woord = woord;
	}
	
	public EindpuntTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// EINDPUNT <WOORD>
		return "EINDPUNT " + woord;
	}


	@Override
	int getDialogResource() {
		return R.layout.eindpuntdialog;
	}

	@Override
	String getTaskTitle() {
		return "Eindpunt";
	}
	
	@Override
	void processInput(View dialogView) {
		EditText txtWoord = (EditText) dialogView.findViewById(R.id.editEindpuntWoord);
		this.woord = txtWoord.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		dataProvider.clearStatus();
		
	}
}
