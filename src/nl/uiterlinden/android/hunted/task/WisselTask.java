package nl.uiterlinden.android.hunted.task;

import nl.uiterlinden.android.hunted.R;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.service.HuntedService;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

public class WisselTask extends AbstractTask {

	private String post;

	public WisselTask(Context context, HuntedService huntedService, String post) {
		super(context, huntedService);
		this.post = post;
	}
	
	public WisselTask(Context context, HuntedService huntedService) {
		super(context, huntedService);
	}
	
	@Override
	public String getCommandString() {
		// WISSEL NAAR <POST>
		return "WISSEL NAAR " + post;
	}


	@Override
	int getDialogResource() {
		return R.layout.wisseldialog;
	}

	@Override
	String getTaskTitle() {
		return "Wissel";
	}
	
	@Override
	void processInput(View dialogView) {
		EditText txtPost = (EditText) dialogView.findViewById(R.id.editWisselPost);
		this.post = txtPost.getText().toString();
	}

	@Override
	void postExecute(DataProvider dataProvider) {
		dataProvider.updateStatus(
				DataProvider.CURRENT_TARGET, post);
		
	}
}
