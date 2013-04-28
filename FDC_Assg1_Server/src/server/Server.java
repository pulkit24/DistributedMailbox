package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import components.Commands.Command;
import components.communication.RPCMessage;
import components.communication.ServerInterface;
import components.communication.marshalling.SimpleMarshaller;
import components.messages.ChatMessage;
import components.messages.Mailbox;
import components.notices.Status;
import components.utilities.CSVUtility;
import components.utilities.IDGenerator;
import components.utilities.Log;

public class Server extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = -7869497993259504486L;

	// Singleton instance, if needed
	private static Server instance = null;

	// Clients connected to the system
	private int maxClients = 0; // loaded from the properties file
	private List<Long> connectedClients = null;

	// Keep track of the RPC IDs observed, in order to keep them globally unique
	private long largestSeenRPCID = 0l;

	// Handle for the ID generator
	private IDGenerator idGenerator = null;

	// Maximum message length
	private int maxMessageLength = 0; // loaded from the properties file

	// RMI connection parameters
	private int port = 0; // loaded from the properties file

	public static void main(String args[]) {
		// Set the logger mode
		Log.setUserFriendlyMode();
		// Launch a new server
		try {
			new Server();
		} catch (RemoteException e) {
			Log.error("Server", "main", "Could not initialize RMI", e);
		}
	}

	public static Server getInstance() throws RemoteException {
		if (instance == null)
			instance = new Server();
		return instance;
	}

	private Server() throws RemoteException {
		// Initialize the list of clients
		connectedClients = new ArrayList<Long>();
		
		// Create a new ID generator
		idGenerator = new IDGenerator();

		// Load the configuration parameters
		boolean isConfigured = loadConfiguration();
		// All set?
		if (isConfigured) {

			// Register with RMI
			try {
				Registry registry = LocateRegistry.createRegistry(port);
				System.setProperty("java.rmi.server.codebase", Server.class.getProtectionDomain().getCodeSource()
						.getLocation().toString());
				registry.rebind("Server", this);
				Log.debug("Server", "constructor", "RMI has been setup");

			} catch (RemoteException e) {
				// Could not register RMI
				Log.error("Server", "constructor", "Could not initialize the RMI service", e);
			}

		} else
			// No, show an error
			Log.error("Server", "constructor", "Could not configure the server properly", null);
	}

	// Load parameters from the properties file
	private boolean loadConfiguration() {
		try {
			// Read the properties file
			Properties config = new Properties();
			config.load(new FileInputStream("config.properties"));

			// Load the port number for RMI
			port = Integer.parseInt(config.getProperty("port"));

			// Load the maximum allowed connected clients at any time
			maxClients = Integer.parseInt(config.getProperty("clients.max"));

			// Load the maximum length of a message
			maxMessageLength = Integer.parseInt(config.getProperty("message.length.max"));

			return true;

		} catch (FileNotFoundException e) {
			Log.error("Server", "loadConfiguration", "Could not find the properties file", e);
			return false;

		} catch (IOException e) {
			Log.error("Server", "loadConfiguration", "Could not read the properties file", e);
			return false;
		}
	}

	private boolean isConnected(long clientID) {
		// Check if the client is currently connected to the system
		return connectedClients.contains(new Long(clientID));
	}

	@Override
	public RPCMessage connect(RPCMessage request) throws RemoteException {
		// Validate request and procedure
		if (request.validateRequest() && request.validateProcedure(Command.Connect)) {

			// Record the RPC ID
			largestSeenRPCID = Math.max(largestSeenRPCID, request.getRPCId());

			// Do we have space left?
			if (connectedClients.size() < maxClients) {

				// Generate a unique ID for the client
				long clientID = idGenerator.getNextInSequence("client");
				// Add the client to the list of connected clients
				connectedClients.add(new Long(clientID));
				// Return the generated client ID to the client
				RPCMessage response = request.createResponse("" + clientID, Status.SUCCESS);

				Log.debug("Server", "connect", "Generated client ID: " + clientID);
				return response;

			} else
				// No, we can't connect the client
				return request.createResponse("", Status.MAX_CLIENTS_REACHED);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_REQUEST);
	}

	@Override
	public RPCMessage disconnect(RPCMessage request) throws RemoteException {
		// Validate request and procedure
		if (request.validateRequest() && request.validateProcedure(Command.Disconnect)) {

			// Record the RPC ID
			largestSeenRPCID = Math.max(largestSeenRPCID, request.getRPCId());

			// Extract the client ID
			long clientID = 0l;

			try {
				clientID = Long.parseLong(CSVUtility.fromCSV(request.getCsv_data()).get(0));

			} catch (NumberFormatException e) {
				// Invalid client ID
				Log.error("Server", "disconnect", "Argument is not a number", e);
				return request.createResponse("", Status.INVALID_REQUEST);
			}

			// Check if the client is currently connected
			if (isConnected(clientID)) {

				// Remove the client from the list of connected clients
				connectedClients.remove(new Long(clientID));

				Log.debug("Server", "disconnect", "Client disconnected");
				return request.createResponse("", Status.SUCCESS);

			} else
				// Else return an error
				return request.createResponse("", Status.INVALID_CLIENT);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_REQUEST);
	}

	@Override
	public RPCMessage deposit(RPCMessage request) throws RemoteException {
		// Validate request and procedure
		if (request.validateRequest() && request.validateProcedure(Command.Deposit)) {

			// Record the RPC ID
			largestSeenRPCID = Math.max(largestSeenRPCID, request.getRPCId());

			// Extract the message info
			List<String> data = CSVUtility.fromCSV(request.getCsv_data());

			// Get the various identifiers
			long senderID = 0l;
			long recipientID = 0l;

			try {
				senderID = Long.parseLong(data.get(0));
				recipientID = Long.parseLong(data.get(1));

			} catch (NumberFormatException e) {
				// Invalid IDs
				Log.error("Server", "deposit", "Argument is not a number", e);
				return request.createResponse("", Status.INVALID_REQUEST);
			}

			// Get the chat message
			String content = data.get(2);
			// If there are more data arguments, concatenate them back into the full text string
			// (because any word separated by spaces is taken as an argument to the function)
			if (data.size() > 3)
				for (int i = 3; i < data.size(); i++)
					content += " " + data.get(i);
			// Limit the content to the max limit
			if (content.length() > maxMessageLength)
				content = content.substring(0, maxMessageLength);
			// Construct a new Chat Message object
			ChatMessage message = new ChatMessage(IDGenerator.NULL_ID, senderID, recipientID, content, null);

			// Check if the sender is currently connected
			if (isConnected(message.getSenderID())) {

				// Check if the recipient is currently connected
				if (isConnected(message.getRecipientID())) {

					// Set an ID and the receive date for the message
					message.setID(idGenerator.getNextInSequence("message"));
					message.setReceiveDate(Calendar.getInstance());

					// Add the mail to the mailbox
					Mailbox.getInstance().addMessage(message);

					Log.debug("Server", "deposit", "Message stored");
					return request.createResponse("", Status.SUCCESS);

				} else
					// Else return an error about invalid recipient
					return request.createResponse("", Status.INVALID_RECIPIENT);

			} else
				// Else return an error about invalid sender
				return request.createResponse("", Status.INVALID_SENDER);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_REQUEST);
	}

	@Override
	public RPCMessage retrieve(RPCMessage request) throws RemoteException {
		// Validate request and procedure
		if (request.validateRequest() && request.validateProcedure(Command.Retrieve)) {

			// Record the RPC ID
			largestSeenRPCID = Math.max(largestSeenRPCID, request.getRPCId());

			// Extract the client ID
			long clientID = 0l;

			try {
				clientID = Long.parseLong(CSVUtility.fromCSV(request.getCsv_data()).get(0));

			} catch (NumberFormatException e) {
				// Invalid client ID
				Log.error("Server", "retrieve", "Argument is not a number", e);
				return request.createResponse("", Status.INVALID_REQUEST);
			}

			// Check if the client is currently connected
			if (isConnected(clientID)) {

				// Get all waiting messages for this client from the mailbox
				List<ChatMessage> messages = Mailbox.getInstance().getMessagesByRecipient(clientID, true);

				String marshalledData = SimpleMarshaller.marshallToString(messages);
				if (marshalledData != null)
					return request.createResponse(marshalledData, Status.SUCCESS);
				else
					return request.createResponse("", Status.MARSHAL_FAILED);

			} else
				// Else return an error
				return request.createResponse("", Status.INVALID_CLIENT);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_REQUEST);
	}

	@Override
	public RPCMessage inquire(RPCMessage request) throws RemoteException {
		// Validate request and procedure
		if (request.validateRequest() && request.validateProcedure(Command.Inquire)) {

			// Record the RPC ID
			largestSeenRPCID = Math.max(largestSeenRPCID, request.getRPCId());

			// Extract the client and user IDs
			List<String> data = CSVUtility.fromCSV(request.getCsv_data());
			long clientID = 0l;
			long userID = 0l;

			try {
				clientID = Long.parseLong(data.get(0));
				userID = Long.parseLong(data.get(1));

			} catch (NumberFormatException e) {
				// Invalid IDs
				Log.error("Server", "deposit", "Argument is not a number", e);
				return request.createResponse("", Status.INVALID_REQUEST);
			}

			// Check if the client is connected
			if (isConnected(clientID)) {
				// Return true if the required user is connected, false if not
				Log.debug("Server", "inquire", "User is connected");
				return request.createResponse("" + isConnected(userID), Status.SUCCESS);
			} else
				// Else return an error
				return request.createResponse("", Status.INVALID_CLIENT);

		} else
			// Else return an error
			return request.createResponse("", Status.INVALID_REQUEST);
	}

	@Override
	public long getNextRPCID(long largestRPCIDSeenByClient) throws RemoteException {
		// Update own track of largest RPC ID with the client's information
		largestSeenRPCID = Math.max(largestSeenRPCID, largestRPCIDSeenByClient);
		
		// Increment and send a new ID
		return ++largestSeenRPCID;
	}
}
