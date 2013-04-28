/* Packet of information used during any communication.
 * 
 * Usage:
 * 1. Create a request packet using RPCMessage(...)
 * 2. Create a response packet based on the request using createResponse(...)
 * 3. Get all contained information using the various getters.
 */
package components.communication;

import java.io.Serializable;

import components.CSVUtility;
import components.IDGenerator;
import components.texts.Status;

public class RPCMessage implements Serializable {
	private static final long serialVersionUID = 6307407935412844886L;

	// Message type
	public static final short REQUEST = 0;
	public static final short REPLY = 1;

	public enum MessageType {
		REQUEST, REPLY
	};

	private MessageType messageType;

	// ID of the individual transaction
	private long transactionID; /* transaction id */
	// Globally unique ID
	private long RPCId; /* Globally unique identifier */
	// Counter for client's requests
	private long requestID; /* Client request message counter */
	// Command ID
	private short procedureID; /* e.g.(1,2,3,4) */
	// Arguments for this command as CSV
	private String csv_data; /* data as comma separated values */
	// Success/error status
	private short status;

	/**
	 * Create a new RPCMessage packet.
	 * 
	 * @param messageType
	 *            Specify whether the message packet is a request or a reply.
	 * @param transactionID
	 *            To identify the current transaction (may span multiple packet exchanges).
	 * @param RPCId
	 *            Globally unique identifier for the RPC communication between this client and the server.
	 * @param requestID
	 *            To identify this particular request within this transaction.
	 * @param procedureID
	 *            The command for the operation requested from the server.
	 * @param csv_data
	 *            All arguments necessary for this operation, in a CSV format.
	 * @param status
	 *            The result of the operation as described by the Status class.
	 *            (Use Status.UNSET for request packets.)
	 * @see {@link IDGenerator} {@link CSVUtility}, {@link Status}
	 */
	public RPCMessage(MessageType messageType, long transactionID, long RPCId, long requestID, short procedureID,
			String csv_data, short status) {
		this.messageType = messageType;
		this.transactionID = transactionID;
		this.RPCId = RPCId;
		this.requestID = requestID;
		this.procedureID = procedureID;
		this.csv_data = csv_data;
		this.status = status;
	}

	/**
	 * Create a response packet for this request.
	 * This is a convenience function supplied to quickly generate an appropriate
	 * response packet, since most of the identifiers are copied from the request anyway.
	 * You need only supply the new pieces of information in the arguments.
	 * 
	 * @param resultData
	 *            The result of the operation, if any, in CSV format.
	 * @param resultStatus
	 *            The status of the operation.
	 * @return The new RPCMessage object ready to be sent as a response.
	 * @see {@link CSVUtility}, {@link Status}
	 */
	public RPCMessage createResponse(String resultData, short resultStatus) {
		return new RPCMessage(MessageType.REPLY, transactionID, RPCId, requestID, procedureID, resultData, resultStatus);
	}

	/**
	 * Returns the type of the message.
	 * 
	 * @return MessageType.REQUEST or MessageType.REPLY
	 */
	public MessageType getMessageType() {
		return messageType;
	}

	/**
	 * Returns the transaction ID of the RPC message.
	 * Use this to validate responses.
	 * 
	 * @return Transaction ID.
	 */
	public long getTransactionID() {
		return transactionID;
	}

	/**
	 * Returns the RPC ID of the RPC message.
	 * Use this to validate incoming requests and responses.
	 * 
	 * @return RPC ID.
	 */
	public long getRPCId() {
		return RPCId;
	}

	/**
	 * Returns the request ID of the RPC message.
	 * Use this to validate responses.
	 * 
	 * @return Request ID.
	 */
	public long getRequestID() {
		return requestID;
	}

	/**
	 * Returns the ID of the operation command requested by the user.
	 * 
	 * @return Command ID, as specified by Commands.Command
	 * @see {@link Commands}
	 */
	public short getProcedureID() {
		return procedureID;
	}

	/**
	 * Returns the supplied data in CSV format.
	 * For requests, this includes all arguments for the operation.
	 * For responses, this includes any output generated on successful operation.
	 * 
	 * @return Data as CSV.
	 * @see {@link CSVUtility}
	 */
	public String getCsv_data() {
		return csv_data;
	}

	/**
	 * Returns the status supplied with the RPC message.
	 * In responses, use this to check whether the operation was successful.
	 * 
	 * @return Status ID, as specified by the Status class.
	 * @see {@link Status}
	 */
	public short getStatus() {
		return status;
	}
}