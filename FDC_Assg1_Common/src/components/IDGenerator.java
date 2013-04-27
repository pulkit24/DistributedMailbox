/* Utility for generating unique identifiers that are totally ordered.
 * Maintains separate tracks for clients, messages etc.
 * 
 * Usage:
 * 1. Use generate...ID() corresponding to the entity for which you need an ID.
 */
package components;

public class IDGenerator {

	/**
	 * Represents a null (ie. not set) IDs.
	 */
	public static final int NULL_ID = -1;

	private static int clientIDCounter = 0;
	private static int messageIDCounter = 0;

	/**
	 * Returns a unique (total ordered) ID for clients.
	 * 
	 * @return A unique ID for the client.
	 */
	public static int generateClientID() {
		return ++clientIDCounter;
	}

	/**
	 * Returns a unique (total ordered) ID for messages.
	 * 
	 * @return A unique ID for the message.
	 */
	public static int generateMessageID() {
		return ++messageIDCounter;
	}
}
