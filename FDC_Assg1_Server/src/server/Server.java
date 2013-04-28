package server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import components.CSVUtility;
import components.IDGenerator;
import components.Log;
import components.communication.RPCMessage;
import components.communication.ServerInterface;
import components.communication.marshalling.SimpleMarshaller;
import components.messages.Mailbox;
import components.messages.ChatMessage;
import components.texts.Status;

public class Server implements ServerInterface {

	private List<Long> connectedClients = null;
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
		connectedClients = new ArrayList<Long>();
	}

	private boolean isConnected(long clientID) {
		// Check if the client is currently connected to the system
		return connectedClients.contains(new Long(clientID));
	}

	@Override
	public RPCMessage connect(RPCMessage request) {
		// Generate a unique ID for the client
		long clientID = IDGenerator.getNextInSequence("client");
		// Add the client to the list of connected clients
		connectedClients.add(new Long(clientID));
		// Return the generated client ID to the client
		RPCMessage response = request.createResponse("" + clientID, Status.SUCCESS);

		Log.debug("Server", "connect", "Generated client ID: " + clientID);
		return response;
	}

	@Override
	public RPCMessage disconnect(RPCMessage request) {
		// Extract the client ID
		long clientID = Long.parseLong(CSVUtility.fromCSV(request.getCsv_data()).get(0));

		// Check if the client is currently connected
		if (isConnected(clientID)) {

			// Remove the client from the list of connected clients
			connectedClients.remove(new Long(clientID));

			Log.debug("Server", "disconnect", "Client disconnected");
			return request.createResponse("", Status.SUCCESS);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_CLIENT);
	}

	@Override
	public RPCMessage deposit(RPCMessage request) {
		// Extract the message info
		List<String> data = CSVUtility.fromCSV(request.getCsv_data());
		long senderID = Long.parseLong(data.get(0));
		long recipientID = Long.parseLong(data.get(1));
		String content = data.get(2);
		ChatMessage message = new ChatMessage(IDGenerator.NULL_ID, senderID, recipientID, content, null);

		// Check if the sender is currently connected
		if (isConnected(message.getSenderID())) {

			// Set an ID and the receive date for the message
			message.setID(IDGenerator.getNextInSequence("message"));
			message.setReceiveDate(Calendar.getInstance());

			// Add the mail to the mailbox
			Mailbox.getInstance().addMessage(message);

			Log.debug("Server", "deposit", "Message stored");
			return request.createResponse("", Status.SUCCESS);

		} else
			// Else return an error about invalid sender
			return request.createResponse("", Status.INVALID_SENDER);
	}

	@Override
	public RPCMessage retrieve(RPCMessage request) {
		// Extract the client ID
		long clientID = Long.parseLong(CSVUtility.fromCSV(request.getCsv_data()).get(0));

		// Check if the client is currently connected
		if (isConnected(clientID)) {

			// Get all waiting messages for this client from the mailbox
			List<ChatMessage> messages = Mailbox.getInstance().getMessagesByRecipient(clientID, true);

			// TODO marshal
			String marshalledData = SimpleMarshaller.marshallToString(messages);
			if(marshalledData!=null)
				return request.createResponse(marshalledData, Status.SUCCESS);
			else
				return request.createResponse("", Status.MARSHAL_FAILED);
			
		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_CLIENT);
	}

	@Override
	public RPCMessage inquire(RPCMessage request) {
		// Extract the client and user IDs
		List<String> data = CSVUtility.fromCSV(request.getCsv_data());
		long clientID = Long.parseLong(data.get(0));
		long userID = Long.parseLong(data.get(1));

		// Check if the client is connected
		if (isConnected(clientID)) {
			// Return true if the required user is connected, false if not
			Log.debug("Server", "inquire", "User is connected");
			return request.createResponse("" + isConnected(userID), Status.SUCCESS);
		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_CLIENT);
	}
}
