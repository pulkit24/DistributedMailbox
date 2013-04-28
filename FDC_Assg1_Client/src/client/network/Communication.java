/* Communication layer for the client.
 * Abstracts the method of calling server functions.
 * 
 * Usage:
 * 1. Establish connection to the server by creating a new Communication object.
 * 2. Check if the connection is active and usable using isActive()
 * 3. Send a command using sendOperation(...)
 * 4. Use the various getter functions to get the results from the response.
 * Note: Only use a function when you are sure that is the result of the operation.
 */
package client.network;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import client.input.UserInput;

import components.Commands.Command;
import components.communication.RPCMessage;
import components.communication.ServerInterface;
import components.communication.marshalling.SimpleMarshaller;
import components.messages.ChatMessage;
import components.notices.Status;
import components.utilities.CSVUtility;
import components.utilities.IDGenerator;
import components.utilities.Log;

public class Communication {

	// Flag for active connection
	private boolean isActive = false;

	// Handle for the server
	private ServerInterface server = null;

	// Handle for the ID generator
	private IDGenerator idGenerator = null;

	// Stores the response for further scrutiny by the client
	private RPCMessage response = null;

	/**
	 * Initializes the communication layer by establishing a connection to the server.
	 */
	public Communication(String serverAddress, int serverPort) {
		Log.debug("Communication", "constructor", "Connecting to the server...");

		// Create a new ID generator
		idGenerator = new IDGenerator();

		// Connect to the server
		try {
			// Get the RMI registry
			Registry reg = LocateRegistry.getRegistry(serverAddress, serverPort);
			server = (ServerInterface) reg.lookup("Server");
			// Mark connection as active
			isActive = true;
			Log.debug("Communication", "constructor", "Connected to the server");
		} catch (AccessException e) {
			Log.error("Communication", "constructor", "Could not access the server registry", e);
		} catch (RemoteException e) {
			Log.error("Communication", "constructor", "Could not find the RMI registry on the server", e);
		} catch (NotBoundException e) {
			Log.error("Communication", "constructor", "Could not bind to the provided RMI address", e);
		}
	}

	/**
	 * Check if communication has been established with the server.
	 * 
	 * @return True if connection has been establish with the server.
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Issue the command to the server. The response, if any,
	 * can be queried using other functions offered by this class.
	 * 
	 * @param command
	 *            The Command object corresponding to the operation.
	 * @param args
	 *            A list of arguments, if any, associated with the command.
	 * @return True if the operation executed successfully on the server.
	 * @throws RemoteException
	 */
	public boolean sendOperation(Command command, List<String> args) throws RemoteException {
		// Get the next globally unique RPC ID
		long RPCID = server.getNextRPCID(idGenerator.getCurrentInSequence("RPC"));

		// Construct an RPC Message request object
		RPCMessage request = new RPCMessage(RPCMessage.MessageType.REQUEST,
				idGenerator.getNextInSequence("transaction"), RPCID, idGenerator.getNextInSequence("request"),
				command.getID(), CSVUtility.toCSV(args), Status.UNSET);

		// Show the request message for understanding the "behind-the-scenes"
		UserInput.getInstance().display("\t" + request.toString());

		// Call the corresponding remote functions
		if (command.equals(Command.Connect)) {
			// Connect to the server
			response = server.connect(request);

		} else if (command.equals(Command.Disconnect)) {
			// Disconnect from the server
			response = server.disconnect(request);

		} else if (command.equals(Command.Deposit)) {
			// Send the message
			response = server.deposit(request);

		} else if (command.equals(Command.Retrieve)) {
			// Get all messages
			response = server.retrieve(request);

		} else if (command.equals(Command.Inquire)) {
			// Check if a user is online
			response = server.inquire(request);
		}

		// Show the response message for understanding the "behind-the-scenes"
		UserInput.getInstance().display("\t" + response.toString());
		
		// Validate response
		if (response.validateResponse(request)) {

			// Was the operation successful?
			return isOperationSuccessful();

		} else {
			// No, not valid
			return false;
		}
	}

	// Check for a success status in the response
	private boolean isOperationSuccessful() {
		if (response != null)
			// Is the status a success notice?
			return response.getStatus() == Status.SUCCESS;
		else
			return false;
	}

	/**
	 * Returns the status of the operation.
	 * 
	 * @return The status as specified by the Status class.
	 */
	public short getStatus() {
		if (response != null)
			return response.getStatus();
		else
			return Status.UNSET;
	}

	/**
	 * Returns the client ID generated by the server.
	 * Note: Only use this when you are sure this is the result of the operation.
	 * 
	 * @return The ID for the client.
	 */
	public Long getClientID() {
		List<String> responseData = CSVUtility.fromCSV(response.getCsv_data());
		if (responseData != null)
			try {
				return Long.parseLong(responseData.get(0));
			} catch (NumberFormatException e) {
				// Invalid IDs
				Log.error("Communication", "getClientID", "Argument is not a number", e);
				return IDGenerator.NULL_ID;
			}
		else
			return IDGenerator.NULL_ID;
	}

	/**
	 * Returns a list of messages for the client retrieved from the server.
	 * Note: Only use this when you are sure this is the result of the operation.
	 * 
	 * @return A list of Chat Message objects.
	 */
	@SuppressWarnings("unchecked")
	public List<ChatMessage> getChatMessages() {
		// Get the CSV data
		String responseData = response.getCsv_data();

		Object unmarshalledData = SimpleMarshaller.unmarshallString(responseData);
		if (unmarshalledData != null)
			return (List<ChatMessage>) unmarshalledData;
		else
			return null;
	}

	/**
	 * Check whether the inquired user is currently online.
	 * Note: Only use this when you are sure this is the result of the operation.
	 * 
	 * @return True if the user is connected to the server.
	 */
	public boolean isUserOnline() {
		return Boolean.parseBoolean(response.getCsv_data());
	}

	/**
	 * Check if the response if valid as per the expected procedure.
	 * Relay function for RPCMessage's validateProcedure() command,
	 * that is not visible to the ChatClient.
	 * 
	 * @param command
	 *            The expected operation.
	 * @return True if the response is marked for the expected operation.
	 */
	public boolean isResponseValidAsPerProcedure(Command command) {
		return response.validateProcedure(command);
	}
}
