/* Main app for the client.
 * 
 * Usage:
 * 1. Launch using ChatClient() and watch the fireworks!
 */
package client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import client.input.InputInterface;
import client.input.UserInput;
import client.network.Communication;

import components.Commands.Command;
import components.messages.ChatMessage;
import components.notices.NoticeMessages;
import components.notices.Status;
import components.utilities.Log;

public class ChatClient {

	// Handles for the UI and network communication layers
	private Communication comm = null;
	private InputInterface ui = null;

	// Server connection details
	private String serverAddress = null; // loaded from the properties file
	private int serverPort = 0; // loaded from the properties file
	
	// Launch the client!
	public static void main(String args[]) {
		// Set the logger mode
		Log.setSilentMode();
		// Launch the client
		new ChatClient();
	}

	/**
	 * Starts the chat client.
	 * Continually asks for user input and issues the command to the server.
	 * The results are displayed to the user.
	 */
	public ChatClient() {
		Log.debug("ChatClient", "constructor", "new ChatClient started.");

		// Load the configuration parameters
		boolean isConfigured = loadConfiguration();
		// All set?
		if (isConfigured) {

			// Get the UI handle
			ui = UserInput.getInstance();
			// Start the communication helper
			comm = new Communication(serverAddress, serverPort);

			// Do we have an active connection with the server now?
			if (comm.isActive()) {

				// Welcome the user
				ui.display(NoticeMessages.WELCOME);

				// Prompt for input
				do {

					// Is there any input...
					if (ui.hasValidInput()) {

						// ...that is not an exit command?
						if (!isExit(ui.getCommand())) {

							// Process the command
							short status;
							try {
								status = executeCommand(ui.getCommand(), ui.getArguments());
								ui.display(Status.getDescription(status));
							} catch (RemoteException e) {
								Log.error("ChatClient", "constructor", "Remote exception encountered", e);
							}

						} else
							// Exit command, say goodbye and exit!
							ui.display(NoticeMessages.GOODBYE);

					} else
						// Input was not valid
						ui.display(NoticeMessages.INVALID_INPUT);

					// Keep prompting for commands unless the user has exited
				} while (!isExit(ui.getCommand()));

			} else
				// No, we couldn't establish a connection with the server
				ui.display(NoticeMessages.NO_CONNECTION);
			
		}
	}

	// Load parameters from the properties file
	private boolean loadConfiguration() {
		try {
			// Read the properties file
			Properties config = new Properties();
			config.load(new FileInputStream("config.properties"));

			// Load the connection details for RMI
			serverAddress = config.getProperty("server.address");
			serverPort = Integer.parseInt(config.getProperty("server.port"));

			return true;

		} catch (FileNotFoundException e) {
			Log.error("ChatClient", "loadConfiguration", "Could not find the properties file", e);
			return false;

		} catch (IOException e) {
			Log.error("ChatClient", "loadConfiguration", "Could not read the properties file", e);
			return false;
		}
	}

	// Check if the user requested to exit
	private boolean isExit(Command command) {
		// Check for preemptive local exit commands
		if (command != null)
			return command.equals(Command.Exit) || command.equals(Command.Quit);
		else
			return false;
	}

	// Send the command to the communication layer to forward it to the server
	private short executeCommand(Command command, List<String> args) throws RemoteException {
		Log.debug("ChatClient", "executeCommand", "executing " + command.getName());

		// Create an RPC message request object
		boolean isOperationSuccessful = comm.sendOperation(command, args);
		// Was the operation successful?
		if (isOperationSuccessful) {
			// Handle the response based on the type of command
			handleResponse(command);
			return Status.SUCCESS;
		} else
			return comm.getStatus();
	}

	// Take necessary action on receiving a response for the command
	private void handleResponse(Command command) {

		if (command != null) {

			// Act based on the command issued
			if (command.equals(Command.Connect))
				connect();
			else if (command.equals(Command.Disconnect))
				disconnect();
			else if (command.equals(Command.Deposit))
				deposit();
			else if (command.equals(Command.Retrieve))
				retrieve();
			else if (command.equals(Command.Inquire))
				inquire();
		}
	}

	// Tell the user they got connected
	private void connect() {
		Log.debug("ChatClient", "connect", "processing...");

		// Validate response procedure
		// (all other response validation is handled automatically by the communication layer)
		if (comm.isResponseValidAsPerProcedure(Command.Connect)) {

			ui.display(NoticeMessages.CLIENT_CONNECTED);
			// Display client ID
			ui.display(NoticeMessages.CLIENT_ID_GENERATED + comm.getClientID());

		} else
			// Display an error
			ui.display(NoticeMessages.INVALID_REPLY);
	}

	// Tell the user they got disconnected
	private void disconnect() {
		// Validate response procedure
		// (all other response validation is handled automatically by the communication layer)
		if (comm.isResponseValidAsPerProcedure(Command.Disconnect)) {

			ui.display(NoticeMessages.CLIENT_DISCONNECTED);

		} else
			// Display an error
			ui.display(NoticeMessages.INVALID_REPLY);
	}

	// Tell the user their message was deposited
	private void deposit() {
		// Validate response procedure
		// (all other response validation is handled automatically by the communication layer)
		if (comm.isResponseValidAsPerProcedure(Command.Deposit)) {

			ui.display(NoticeMessages.DEPOSIT_SUCCESS);

		} else
			// Display an error
			ui.display(NoticeMessages.INVALID_REPLY);
	}

	// Show the user all the messages they have received
	private void retrieve() {
		// Validate response procedure
		// (all other response validation is handled automatically by the communication layer)
		if (comm.isResponseValidAsPerProcedure(Command.Retrieve)) {

			ui.display(NoticeMessages.RETRIEVE_SUCCESS);
			// Extract messages
			List<ChatMessage> messages = comm.getChatMessages();
			if (messages != null) {
				// Did we get any messages?
				if (messages.size() > 0) {
					// Yes!
					Calendar currentTime = Calendar.getInstance();
					for (ChatMessage message : messages) {
						// Print the message details
						StringBuilder messageToDisplay = new StringBuilder();
						messageToDisplay.append("New message received: " + "\n\tFrom: " + message.getSenderID()
								+ "\n\tContent: " + message.getMessage()
								+ "\n\tThis message has been waiting at the server for ");
						// How long was the message waiting? Compute year gap
						int yearsBetween = currentTime.get(Calendar.YEAR) - message.getReceiveDate().get(Calendar.YEAR);
						if (yearsBetween > 0)
							messageToDisplay.append(yearsBetween + " year(s)");
						// Compute day gap
						int daysBetween = currentTime.get(Calendar.DATE) - message.getReceiveDate().get(Calendar.DATE);
						if (daysBetween > 0)
							messageToDisplay.append(daysBetween + " day(s)");
						// Compute hour gap
						int hoursBetween = currentTime.get(Calendar.HOUR_OF_DAY)
								- message.getReceiveDate().get(Calendar.HOUR_OF_DAY);
						if (hoursBetween > 0)
							messageToDisplay.append(hoursBetween + " hour(s)");
						// Compute minute gap
						int minutesBetween = currentTime.get(Calendar.MINUTE)
								- message.getReceiveDate().get(Calendar.MINUTE);
						if (minutesBetween > 0)
							messageToDisplay.append(minutesBetween + " minute(s)");
						// Compute second gap
						int secondsBetween = currentTime.get(Calendar.SECOND)
								- message.getReceiveDate().get(Calendar.SECOND);
						if (secondsBetween > 0)
							messageToDisplay.append(secondsBetween + " second(s)");

						// Print it all!
						ui.display(messageToDisplay.toString());
					}
				} else
					// No messages
					ui.display(NoticeMessages.RETRIEVE_EMPTY);
			} else
				// Corrupted messages
				ui.display(NoticeMessages.RETRIEVE_FAILED);

		} else
			// Display an error
			ui.display(NoticeMessages.INVALID_REPLY);
	}

	// Tell the user about their inquiry
	private void inquire() {
		// Validate response procedure
		// (all other response validation is handled automatically by the communication layer)
		if (comm.isResponseValidAsPerProcedure(Command.Inquire)) {

			if (comm.isUserOnline())
				ui.display(NoticeMessages.INQUIRE_SUCCESS);
			else
				ui.display(NoticeMessages.INQUIRE_FAILURE);

		} else
			// Display an error
			ui.display(NoticeMessages.INVALID_REPLY);
	}
}
