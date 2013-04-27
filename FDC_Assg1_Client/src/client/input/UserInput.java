package client.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import components.Commands;
import components.Commands.Command;
import components.Log;

public class UserInput implements InputInterface {

	private BufferedReader br = null;
	private Command command = null;
	private List<String> args = null;

	private static UserInput instance;

	/**
	 * Get the singleton instance of the UI handler.
	 * 
	 * @return The singleton instance.
	 */
	public static UserInput getInstance() {
		Log.debug("UserInput", "getInstance", "getting singleton instance");
		if (instance == null)
			instance = new UserInput();
		return instance;
	}

	// Just to keep the constructor private.
	private UserInput() {
		Log.debug("UserInput", "constructor", "new UserInput created");
	}

	// Get a line of input from the user.
	private String getInput() {
		Log.debug("UserInput", "getInput", "waiting for user input");
		if (br == null)
			br = new BufferedReader(new InputStreamReader(System.in));
		try {
			return br.readLine();
		} catch (IOException e) {
			Log.error("UserInput", "getInput", "Could not read user input.");
			return null;
		}
	}

	@Override
	public boolean hasValidInput() {
		Log.debug("UserInput", "hasInput", "checking for and getting input");
		// Get the input
		displayPrompt();
		String userInput = getInput();
		if (userInput != null) {

			// Extract the command
			String[] inputParts = userInput.split(" ", 2);
			command = Commands.getCommandByName(inputParts[0]);
			Log.debug("UserInput", "hasInput", "command supplied: " + command.getName());

			args = null;
			// How many arguments do we need?
			int argumentsNeeded = command.getArgumentCount();
			Log.debug("UserInput", "hasInput", "arguments needed: " + command.getArgumentCount());
			if (argumentsNeeded > 0) {
				// Do we have any?
				if (inputParts.length == 2) {
					// Yes, we have some! Save them
					args = new ArrayList<String>();
					for (String arg : inputParts[1].split(" "))
						args.add(arg);
					// Now, did we get enough?
					Log.debug("UserInput", "hasInput", "arguments supplied: " + args.size());
					if (args.size() >= argumentsNeeded)
						// Yes, we have enough!
						return true;
					// No, we don't have enough
					else
						return false;
				} else
					// No, we don't have any
					return false;
			} else
				// We don't need any arguments!
				return true;
		}
		return false;
	}

	@Override
	public Command getCommand() {
		Log.debug("UserInput", "getCommand", "returning command");
		return command;
	}

	@Override
	public List<String> getArguments() {
		Log.debug("UserInput", "getArguments", "returning arguments");
		return args;
	}

	@Override
	public void display(String output) {
		System.out.println(output);
	}

	// Display a prompt to hint the user that their input is required
	private void displayPrompt() {
		System.out.print("> ");
	}
}
