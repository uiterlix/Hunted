package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class ControleTask extends AbstractTask {

	private String woord;

	public ControleTask(Context context, HuntedService huntedService, String woord) {
		super(context, huntedService);
		this.woord = woord;
	}
	
	public ControleTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// CONTROLE <WOORD>
		return "CONTROLE " + woord;
	}


	@Override
	int getDialogResource() {
		return R.layout.controledialog;
	}

	@Override
	String getTaskTitle() {
		return "Controle";
	}
	
	@Override
	void processInput(View dialogView) {
		EditText txtWoord = (EditText) dialogView.findViewById(R.id.editControleWoord);
		this.woord = txtWoord.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		
	}
}
