/* Main app for the client.
 * 
 * Usage:
 * 1. Launch using ChatClient() and watch the fireworks!
 */
package client;

import java.util.Calendar;
import java.util.List;

import client.input.InputInterface;
import client.input.UserInput;
import client.network.Communication;

import components.Commands.Command;
import components.Log;
import components.messages.ChatMessage;
import components.texts.NoticeMessages;
import components.texts.Status;

public class ChatClient {

	// Handles for the UI and network communication layers
	private Communication comm = null;
	private InputInterface ui = null;

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

		// Get the UI handle
		ui = UserInput.getInstance();
		// Start the communication helper
		comm = new Communication();

		while (true) {
			// Is there any input?
			if (ui.hasValidInput()) {
				// Process the command
				short status = executeCommand(ui.getCommand(), ui.getArguments());
				ui.display(Status.getDescription(status));
			} else
				ui.display(Status.getDescription(Status.INVALID_INPUT));
		}
	}

	// Send the command to the communication layer to forward it to the server
	private short executeCommand(Command command, List<String> args) {
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

	// Tell the user they got connected
	private void connect() {
		ui.display(NoticeMessages.CLIENT_CONNECTED);
		// Display client ID
		ui.display(NoticeMessages.CLIENT_ID_GENERATED + comm.getClientID());
	}

	// Tell the user they got disconnected
	private void disconnect() {
		ui.display(NoticeMessages.CLIENT_DISCONNECTED);
	}

	// Tell the user their message was deposited
	private void deposit() {
		ui.display(NoticeMessages.DEPOSIT_SUCCESS);
	}

	// Show the user all the messages they have received
	private void retrieve() {
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
	}

	// Tell the user about their inquiry
	private void inquire() {
		if (comm.isUserOnline())
			ui.display(NoticeMessages.INQUIRE_SUCCESS);
		else
			ui.display(NoticeMessages.INQUIRE_FAILURE);
	}
}
