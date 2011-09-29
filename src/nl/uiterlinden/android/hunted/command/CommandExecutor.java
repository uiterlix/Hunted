package nl.uiterlinden.android.hunted.command;

import java.util.Properties;

import nl.uiterlinden.android.hunted.io.Client;
import nl.uiterlinden.android.hunted.io.HuntedIOException;
import android.content.SharedPreferences;

public class CommandExecutor {
	
	// local path: /huntedhq/rest/message
	private static final String COMMAND_PATH = Client.MESSAGE_PATH;

	private static CommandExecutor commandExecutor;

	private static Client client;

	public static synchronized CommandExecutor initialize(Client client, SharedPreferences preferences) {
		CommandExecutor.client = client;
		commandExecutor = new CommandExecutor();
		return commandExecutor;
	}
	
	public static synchronized CommandExecutor getInstance() {
		if (commandExecutor == null) {
			throw new IllegalStateException("CommandExecutor has not been initialized!");
		}
		return commandExecutor;
	}
	
	private CommandExecutor() { }
	
	public CommandResult executeCommandUsingData(Command command, String sender) throws HuntedIOException {
		// send it through the client
		Properties params = new Properties();
		params.setProperty("sender", sender);
		params.setProperty("message", command.getCommandString());
		String response = client.retrieve(COMMAND_PATH, params);
		
		if (response.startsWith("OK")) {
			return new CommandResult();
		} else {
			return new CommandResult("Fout bij versturen van bericht!");
		}
	}
	
	public boolean executeCommandUsingSMS(Command command) throws HuntedIOException {
		return true;
	}

}
