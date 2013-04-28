/* A collection of notice messages in the system.
 */
package components.notices;

public class NoticeMessages {
	// Client-Server Connection
	public static final String NO_CONNECTION = "Could not establish communication with the server. Is it online? Do you have the right details?";
	public static final String INVALID_REPLY = "The response from the server was invalid.";
	
	// User Interface
	public static final String WELCOME = "Hello.";
	public static final String INVALID_INPUT = "Invalid input provided. Valid commands are: \n\t connect \n\t disconnect (your id) \n\t deposit (your id) (recipient id) (message) \n\t retrieve (your id) \n\t inquire (your id) (user's id) \n\t exit or quit";
	public static final String GOODBYE = "Have a nice day.";
	
	// Connect/Disconnect
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
