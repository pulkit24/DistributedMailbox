/* Represents a chat message with all the relevant details associated.
 * 
 * Usage:
 * 1. Store a message by creating a ChatMessage object.
 * 2. You can only override the ID and receive date, since they are likely to be set by the recipient.
 * 3. Get message details using the various getters.
 */
package components.messages;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;

import components.utilities.IDGenerator;

public class ChatMessage implements Serializable {
	private static final long serialVersionUID = -8701290828758468158L;
	
	private long id;
	private long senderID;
	private long recipientID;
	private String message;
	private Calendar receiveDate;

	/**
	 * Constructs a ChatMessage object.
	 * 
	 * @param id
	 *            A unique id to refer to the message.
	 *            The uniqueness constraint isn't validated by this class.
	 *            Instead, it is up to the programmer to supply unique IDs at construction time.
	 * @param senderID
	 *            The client ID of the sender.
	 * @param recipientID
	 *            The client ID of the recipient.
	 * @param message
	 *            The content of the message.
	 * @param receiveDate
	 *            The date and time at the point of reception.
	 */
	public ChatMessage(long id, long senderID, long recipientID, String message, Calendar receiveDate) {
		this.id = id;
		this.senderID = senderID;
		this.recipientID = recipientID;
		this.message = message;
		this.receiveDate = receiveDate;
	}

	/**
	 * Returns the id of the message.
	 * 
	 * @return Assigned message id.
	 */
	public long getID() {
		return id;
	}

	/**
	 * Sets the id of the message.
	 * Useful when the id is set at a later time (eg. by the mailbox service provider)
	 * 
	 * @param id
	 *            ID to be assigned to the message.
	 */
	public void setID(long id) {
		this.id = id;
	}

	/**
	 * Returns the client ID of the sender of this message (the "from" field).
	 * 
	 * @return The client ID of the sender.
	 */
	public long getSenderID() {
		return senderID;
	}

	/**
	 * Returns the client ID of the intended recipient of this message (the "to" field).
	 * 
	 * @return The client ID of the recipient.
	 */
	public long getRecipientID() {
		return recipientID;
	}

	/**
	 * Returns the contents of the chat message.
	 * 
	 * @return The message content.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the date and time when the message was received.
	 * 
	 * @return A calendar object set to the correct date and time when the message was received.
	 */
	public Calendar getReceiveDate() {
		return receiveDate;
	}

	/**
	 * Updates the date and time when the message was received.
	 * 
	 * @param receiveDate
	 *            A calendar object set to the correct date and time when the message was received.
	 */
	public void setReceiveDate(Calendar receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * Display the message in a human-readable format.
	 */
	public String toString() {
		StringBuilder messageAsString = new StringBuilder();
		messageAsString.append("Message");
		if (senderID != IDGenerator.NULL_ID)
			messageAsString.append(" from: " + senderID);
		if (recipientID != IDGenerator.NULL_ID)
			messageAsString.append(" to: " + recipientID);
		if (receiveDate != null)
			messageAsString.append(" arrived-on: " + DateFormat.getInstance().format(receiveDate.getTime()));
		if (message != null)
			messageAsString.append(" \"" + message + "\"");
		return messageAsString.toString();
	}
}
