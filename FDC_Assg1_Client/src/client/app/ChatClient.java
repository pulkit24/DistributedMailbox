package client.app;

import java.util.Calendar;
import java.util.List;

import server.Server;
import client.input.InputInterface;
import client.input.UserInput;

import components.ChatInterface;
import components.Commands.Command;
import components.IDGenerator;
import components.Log;
import components.messages.Message;
import components.texts.ErrorMessages;
import components.texts.NoticeMessages;

public class ChatClient implements ChatInterface {

	public static void main(String args[]) {
		// Set the logger mode
		Log.setSilentMode();
		// Launch the client
		ChatClient client = new ChatClient();
		// Get the UI handle
		InputInterface ui = UserInput.getInstance();

		while (true) {
			// Is there any input?
			if (ui.hasValidInput())
				// Process the command
				client.executeCommand(ui.getCommand(), ui.getArguments());
			else
				ui.display(ErrorMessages.INVALID_INPUT);
		}
	}

	public ChatClient() {
		Log.debug("ChatClient", "constructor", "new ChatClient started.");
	}

	public void executeCommand(Command command, List<String> args) {
		Log.debug("ChatClient", "executeCommand", "executing " + command.getName());

		try {
			// Determine the command type
			if (command.equals(Command.Connect)) {
				// Connect to the server
				int id = connect();
				UserInput.getInstance().display(NoticeMessages.CLIENT_CONNECTED);
				UserInput.getInstance().display(NoticeMessages.CLIENT_ID_GENERATED + id);

			} else if (command.equals(Command.Disconnect)) {
				// Disconnect from the server
				int clientID = Integer.parseInt(args.get(0));
				disconnect(clientID);
				UserInput.getInstance().display(NoticeMessages.CLIENT_DISCONNECTED);

			} else if (command.equals(Command.Deposit)) {
				// Construct the Message object
				int senderID = Integer.parseInt(args.get(0));
				int recipientID = Integer.parseInt(args.get(1));
				// Join remaining arguments (possibly words) into one string
				String messageContent = "";
				for (String arg : args.subList(2, args.size())) {
					messageContent += arg + " ";
				}
				Message message = new Message(IDGenerator.NULL_ID, senderID, recipientID, messageContent.trim(), null);

				// Send the message
				deposit(message);
				UserInput.getInstance().display(NoticeMessages.DEPOSIT_SUCCESS);

			} else if (command.equals(Command.Retrieve)) {
				// Get all messages
				int clientID = Integer.parseInt(args.get(0));
				List<Message> messages = retrieve(clientID);

				// Did we get any messages?
				if (messages != null && messages.size() > 0) {
					// Yes!
					UserInput.getInstance().display(NoticeMessages.RETRIEVE_SUCCESS);
					Calendar currentTime = Calendar.getInstance();
					for (Message message : messages) {
						StringBuilder messageToDisplay = new StringBuilder();
						messageToDisplay.append("New message received: " + "\n\tFrom: " + message.getSenderID()
								+ "\n\tContent: " + message.getMessage()
								+ "\n\tThis message has been waiting at the server for ");
						int yearsBetween = currentTime.get(Calendar.YEAR) - message.getReceiveDate().get(Calendar.YEAR);
						if (yearsBetween > 0)
							messageToDisplay.append(yearsBetween + " years");
						int daysBetween = currentTime.get(Calendar.DATE) - message.getReceiveDate().get(Calendar.DATE);
						if (daysBetween > 0)
							messageToDisplay.append(daysBetween + " days");
						int hoursBetween = currentTime.get(Calendar.HOUR_OF_DAY)
								- message.getReceiveDate().get(Calendar.HOUR_OF_DAY);
						if (hoursBetween > 0)
							messageToDisplay.append(hoursBetween + " hours");
						int minutesBetween = currentTime.get(Calendar.MINUTE)
								- message.getReceiveDate().get(Calendar.MINUTE);
						if (minutesBetween > 0)
							messageToDisplay.append(minutesBetween + " minutes");
						int secondsBetween = currentTime.get(Calendar.SECOND)
								- message.getReceiveDate().get(Calendar.SECOND);
						if (secondsBetween > 0)
							messageToDisplay.append(secondsBetween + " seconds");

						UserInput.getInstance().display(messageToDisplay.toString());
					}
				} else {
					// No
					UserInput.getInstance().display(NoticeMessages.RETRIEVE_FAILURE);
				}

			} else if (command.equals(Command.Inquire)) {
				// Check if a user is online
				int clientID = Integer.parseInt(args.get(0));
				int userID = Integer.parseInt(args.get(1));
				boolean isUserOnline = inquire(clientID, userID);
				if (isUserOnline)
					UserInput.getInstance().display(NoticeMessages.INQUIRE_SUCCESS);
				else
					UserInput.getInstance().display(NoticeMessages.INQUIRE_FAILURE);
			}
		} catch (IllegalAccessException e) {
			// Server threw an error, possibly validation, that the client should get to know
			UserInput.getInstance().display(e.getMessage());
		}
	}

	@Override
	public int connect() {
		Log.debug("ChatClient", "connect", "connecting to the server");
		return Server.getInstance().connect();
	}

	@Override
	public void disconnect(int clientID) throws IllegalAccessException {
		Log.debug("ChatClient", "disconnect", "disconnecting as client " + clientID);
		Server.getInstance().disconnect(clientID);
	}

	@Override
	public void deposit(Message message) throws IllegalAccessException {
		Log.debug("ChatClient", "deposit", "sending message " + message.toString());
		Server.getInstance().deposit(message);
	}

	@Override
	public List<Message> retrieve(int clientID) throws IllegalAccessException {
		Log.debug("ChatClient", "retrieve", "getting messages for client " + clientID);
		return Server.getInstance().retrieve(clientID);
	}

	@Override
	public boolean inquire(int clientID, int userID) throws IllegalAccessException {
		Log.debug("ChatClient", "inquire", "checking as client " + clientID + " for user " + userID);
		return Server.getInstance().inquire(clientID, userID);
	}
}
