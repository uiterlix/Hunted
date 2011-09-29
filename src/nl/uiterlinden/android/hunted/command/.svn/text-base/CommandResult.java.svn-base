package nl.uiterlinden.android.hunted.command;

public class CommandResult {
	enum Status { OK, ERROR };
	
	private Status status;
	private String errorMessage = null;
	
	public CommandResult() {
		this.status = Status.OK;
	}
	
	public CommandResult(String errorMessage) {
		this.status = Status.ERROR;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean hasErrors() {
		return status.equals(Status.ERROR);
	}
}
