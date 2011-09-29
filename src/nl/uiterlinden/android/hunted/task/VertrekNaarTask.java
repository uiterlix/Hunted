package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class VertrekNaarTask extends AbstractTask {

	private String post;

	public VertrekNaarTask(Context context, HuntedService huntedService, String post) {
		super(context, huntedService);
		this.post = post;
	}
	
	public VertrekNaarTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// VERTREK NAAR <POST>
		return "VERTREK NAAR " + post;
	}


	@Override
	int getDialogResource() {
		return R.layout.vertrekdialog;
	}

	@Override
	String getTaskTitle() {
		return "Vertrek";
	}
	
	@Override
	void processInput(View dialogView) {
		EditText txtPost = (EditText) dialogView.findViewById(R.id.editVertrekNaarPost);
		this.post = txtPost.getText().toString();
	}
	
	void postExecute(DataProvider dataProvider) {
		dataProvider.updateStatus(
				DataProvider.CURRENT_TARGET, post);
	}
}
