/* Manager for all chat messages in the system.
 * Uses concurrency measures to allow trouble-free concurrent access by multiple threads.
 * 
 * Usage:
 * 1. Retrieve the singleton instance using getInstance()
 * 2. Add messages using addMessage(...)
 * 		2.1 You cannot remove messages explicitly. However, you can ask for them to be removed during retrieval.
 * 3. Retrieve messages by using:
 * 		3.1 getMessageById(...) to fetch a known message.
 * 		3.2 getMessagesBySender(...) to get all messages sent by a particular user.
 * 		3.3 getMessagesByRecipient(...) to get all messages set to be recieved by a particular user.
 * In all cases, you can set removeFromMailbox = true to delete the messages after retrieval.
 */
package components.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Mailbox {
	// Store for the messages
	private Map<Integer, Message> messages;

	// Singleton instance of the mailbox
	private static Mailbox instance = null;

	// Key for synchronized access (can't use 'messages' itself, could be null)
	// private Object synchronousAccessKey = new Object();

	// Initialize the mailbox object
	private Mailbox() {
		messages = new ConcurrentHashMap<Integer, Message>();
	}

	/**
	 * Get the singleton instance of the mailbox.
	 * 
	 * @return The singleton, static instance of the mailbox.
	 */
	public static Mailbox getInstance() {
		// Create a new instance if none exists, otherwise use the existing one
		if (instance == null)
			instance = new Mailbox();
		return instance;
	}

	/**
	 * Insert a message into the mailbox.
	 * 
	 * @param message
	 *            The Message object to be inserted.
	 */
	public void addMessage(Message message) {
		messages.put(new Integer(message.getID()), message);
	}

	/**
	 * Returns the message referred to by the supplied ID.
	 * The returned message can be removed the message from the mailbox, if required.
	 * 
	 * @param messageID
	 *            The unique ID for the message.
	 * @param removeFromMailbox
	 *            If true, the message is removed from the mailbox.
	 * @return The Message object representing the message and all associated details.
	 */
	public Message getMessageById(int messageID, boolean removeFromMailbox) {
		Message message = null;
		message = messages.get(new Integer(messageID));
		if (removeFromMailbox)
			removeMessage(messageID);
		return message;
	}

	/**
	 * Returns a list of messages that were sent by the specified sender.
	 * The messages can also be removed from the mailbox if required.
	 * 
	 * @param senderID
	 *            The client ID of the sender of the message(s).
	 * @param removeFromMailbox
	 *            If true, the matching messages are removed from the mailbox.
	 * @return A list of messages that were sent by the specified sender.
	 */
	public List<Message> getMessagesBySender(int senderID,
			boolean removeFromMailbox) {
		List<Message> filteredMessages = new ArrayList<Message>();

		for (Message message : messages.values()) {
			if (message.getSenderID() == senderID) {
				filteredMessages.add(message);
				if (removeFromMailbox)
					removeMessage(message.getID());
			}
		}
		return filteredMessages;
	}

	/**
	 * Returns a list of messages that have been sent to the specified recipient.
	 * The messages can also be removed from the mailbox if required.
	 * 
	 * @param recipientID
	 *            The client ID of the recipient for the message(s).
	 * @param removeFromMailbox
	 *            If true, the matching messages are removed from the mailbox.
	 * @return A list of messages whose intended recipient is the specified recipient.
	 */
	public List<Message> getMessagesByRecipient(int recipientID,
			boolean removeFromMailbox) {
		List<Message> filteredMessages = new ArrayList<Message>();
		for (Message message : messages.values()) {
			if (message.getRecipientID() == recipientID) {
				filteredMessages.add(message);
				if (removeFromMailbox)
					removeMessage(message.getID());
			}
		}
		return filteredMessages;
	}

	// Removes the message from the mailbox.
	private boolean removeMessage(int messageID) {
		Integer messageIDAsObject = new Integer(messageID);
		if (messages.containsKey(messageIDAsObject)) {
			messages.remove(messageIDAsObject);
			return true;
		} else
			return false;
	}
}
