package server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import components.ChatInterface;
import components.IDGenerator;
import components.messages.Mailbox;
import components.messages.Message;
import components.texts.ErrorMessages;

public class Server implements ChatInterface {

	private List<Integer> connectedClients = null;
	private static Server instance = null;

	public static void main(String args[]) {
		// Launch a new server
		new Server();
	}

	public static Server getInstance() {
		if (instance == null)
			instance = new Server();
		return instance;
	}

	private Server() {
		// Initialize the list of clients
		connectedClients = new ArrayList<Integer>();
	}

	private boolean isConnected(int clientID) {
		// Check if the client is currently connected to the system
		return connectedClients.contains(new Integer(clientID));
	}

	@Override
	public int connect() {
		// Generate a unique ID for the client
		int clientID = IDGenerator.generateClientID();
		// Add the client to the list of connected clients
		connectedClients.add(new Integer(clientID));
		// Return the generated client ID to the client
		return clientID;
	}

	@Override
	public void disconnect(int clientID) throws IllegalAccessException {
		// Check if the client is currently connected
		if (isConnected(clientID))
			// Remove the client from the list of connected clients
			connectedClients.remove(new Integer(clientID));
		else
			// Else raise an error
			throw new IllegalAccessException(ErrorMessages.INVALID_CLIENT);
	}

	@Override
	public void deposit(Message message) throws IllegalAccessException {
		// Check if the sender is currently connected
		if (isConnected(message.getSenderID()))
			// Check if the recipient is currently connected
			if (isConnected(message.getRecipientID())) {
				// Set an ID and the receive date for the message
				message.setID(IDGenerator.generateMessageID());
				message.setReceiveDate(Calendar.getInstance());
				// Add the mail to the mailbox
				Mailbox.getInstance().addMessage(message);
			} else
				// Else raise an error about invalid recipient
				throw new IllegalAccessException(
						ErrorMessages.INVALID_RECIPIENT);
		else
			// Else raise an error about invalid sender
			throw new IllegalAccessException(ErrorMessages.INVALID_SENDER);
	}

	@Override
	public List<Message> retrieve(int clientID) throws IllegalAccessException {
		// Check if the client is currently connected
		if (isConnected(clientID))
			// Get all waiting messages for this client from the mailbox
			return Mailbox.getInstance().getMessagesByRecipient(clientID, true);
		else
			// Else raise an error
			throw new IllegalAccessException(ErrorMessages.INVALID_CLIENT);
	}

	@Override
	public boolean inquire(int clientID, int userID)
			throws IllegalAccessException {
		// Check if the client is connected
		if (isConnected(clientID))
			// Return true if the required user is connected, false if not
			return isConnected(userID);
		else
			// Else raise an error
			throw new IllegalAccessException(ErrorMessages.INVALID_CLIENT);
	}
}
