/* User Interface for the front-end.
 * Handles interactions with the end-user: getting the input and displaying the output.
 * 
 * Usage:
 * 1. Check if the user has provided any valid input using hasValidInput()
 * 2. Get the supplied commands and arguments using getCommand() and getArguments()
 * 3. Display the resultant output using display(...)
 */
package client.input;

import java.util.List;

import components.Commands.Command;

public interface InputInterface {

	/**
	 * Check if the user has provided any valid input.
	 * 
	 * @return True if there is some input to process.
	 */
	public boolean hasValidInput();

	/**
	 * Get the command specified by the user.
	 * 
	 * @return Command object corresponding to the user's specified command.
	 */
	public Command getCommand();

	/**
	 * Get the arguments associated with the command.
	 * 
	 * @return List of arguments as strings.
	 *         Convert to the correct data types as needed.
	 */
	public List<String> getArguments();

	/**
	 * Print a message for the user, usually the output resulting from their issued command.
	 * 
	 * @param output
	 *            The message to be displayed.
	 */
	public void display(String output);
}
