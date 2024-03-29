/* A collection of error messages in the system.
 * Note: Does not include log/debug error messages that are used during development.
 */
package components.notices;

import java.util.HashMap;

public class Status {
	// Null status
	public static final short UNSET = -1;

	// Success
	public static final short SUCCESS = 0;

	// Errors
	public static final short INVALID_CLIENT = 1;
	public static final short INVALID_SENDER = 2;
	public static final short INVALID_RECIPIENT = 3;
	public static final short MARSHAL_FAILED = 5;
	public static final short INVALID_REQUEST = 6;
	public static final short MAX_CLIENTS_REACHED = 7;

	// Map of the status with their full descriptive texts
	private HashMap<Short, String> descriptions = new HashMap<Short, String>();
	private static Status instance = null;

	/**
	 * Returns the descriptive text for a status.
	 * 
	 * @param statusID
	 *            The status to be described.
	 * @return Description as a text string.
	 */
	public static String getDescription(short statusID) {
		return getInstance().descriptions.get(statusID);
	}

	// Create an instance to store the status-description mappings
	private static Status getInstance() {
		if (instance == null)
			instance = new Status();
		return instance;
	}

	private Status() {
		// Set the descriptive messages for each status type
		descriptions.put(Status.UNSET, "No result received (response may be null or corrupt).");
		
		descriptions.put(Status.SUCCESS, "Operation executed successfully.");

		descriptions.put(Status.INVALID_CLIENT, "You are not connected to the system.");
		descriptions.put(Status.INVALID_SENDER, "The indicated sender is not connected to the system.");
		descriptions.put(Status.INVALID_RECIPIENT, "The indicated recipient is not connected to the system.");
		descriptions.put(Status.MARSHAL_FAILED, "System error: the messages could not be loaded.");
		descriptions.put(Status.INVALID_REQUEST, "The request information was invalid.");
		descriptions.put(Status.MAX_CLIENTS_REACHED, "The server is at full capacity (cannot handle any more clients unless someone disconnects).");
	}
}
