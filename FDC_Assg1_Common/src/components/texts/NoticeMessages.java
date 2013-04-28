/* A collection of notice messages in the system.
 */
package components.texts;

public class NoticeMessages {
	// Connection
	public static final String CLIENT_CONNECTED = "You are now connected.";
	public static final String CLIENT_ID_GENERATED = "Server generated an id for you: ";
	public static final String CLIENT_DISCONNECTED = "You have been disconnected.";
	
	// Deposit/Retrieval
	public static final String DEPOSIT_SUCCESS = "Message saved.";
	public static final String DEPOSIT_FAILURE = "Message could not be saved.";
	public static final String RETRIEVE_SUCCESS = "Messages retrieved.";
	public static final String RETRIEVE_EMPTY = "No messages for you.";
	public static final String RETRIEVE_FAILED = "System error: the messages were corrupted.";
	
	// Inquire
	public static final String INQUIRE_SUCCESS = "The user is currently connected.";
	public static final String INQUIRE_FAILURE = "No such user is connected.";
}
