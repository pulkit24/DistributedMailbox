/* All functions supported by the chat server.
 * 
 * Usage:
 * 1. Connect to the system using connect()
 * This returns an ID that must be used for all succeeding interactions.
 * 
 * 2. Interact with the system:
 * 		2.1 Send messages using deposit(...)
 * 		2.2 Retrieve messages using retrieve(...)
 * 		2.3 Find out about other users using inquire(...)
 * 
 * 3. Disconnect from the system using disconnect(...)
 */
package components;

import java.util.List;

import components.messages.Message;

public interface ChatInterface {

	/**
	 * Connect to the system.
	 * 
	 * @return The client's ID generated by the server.
	 */
	public int connect();

	/**
	 * Disconnect from the system.
	 * 
	 * @param clientID
	 *            The unique ID of the client connected to the system.
	 * @throws IllegalAccessException
	 *             If the client is not connected to the system.
	 */
	public void disconnect(int clientID) throws IllegalAccessException;

	/**
	 * Send message to another user.
	 * 
	 * @param senderID
	 *            The unique ID of the current connected user.
	 * @param recipientID
	 *            The unique ID of the recipient user.
	 * @param message
	 *            The chat message to be sent.
	 * @throws IllegalAccessException
	 *             If the client is not connected to the system.
	 */
	public void deposit(Message message) throws IllegalAccessException;

	/**
	 * Check and retrieve any messages available for the current user from the server.
	 * 
	 * @param clientID
	 *            The unique ID of the current connected user.
	 * @return An array of messages waiting at the server.
	 *         Null, if no messages are available.
	 * @throws IllegalAccessException
	 *             If the client is not connected to the system.
	 */
	public List<Message> retrieve(int clientID) throws IllegalAccessException;

	/**
	 * Check if a particular user is connected to the system.
	 * 
	 * @param clientID
	 *            The unique ID of the current connected user.
	 * @param userID
	 *            The unique ID of the user you'd like to look up.
	 * @return True if the user is connected, false otherwise.
	 * @throws IllegalAccessException
	 *             If the client is not connected to the system.
	 */
	public boolean inquire(int clientID, int userID)
			throws IllegalAccessException;
}
