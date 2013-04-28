/* Packet of information used during any communication.
 * 
 * Usage:
 * 1. Create a request packet using RPCMessage(...)
 * 2. Create a response packet based on the request using createResponse(...)
 * 3. Get all contained information using the various getters.
 * 4. Validate the requests and replies at various stages using:
 * 		4.1 validateRequest() on the server side.
 * 		4.2 validateResponse(...) on the client side.
 * 		4.3 validateProcedure(...) on either side, before beginning any operation.
 */
package components.communication;

import java.io.Serializable;

import components.Commands;
import components.Commands.Command;
import components.notices.Status;
import components.utilities.CSVUtility;
import components.utilities.IDGenerator;
import components.utilities.Log;

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

	/**
	 * Check if the packet is marked as request by type.
	 * Useful at the server side to validate incoming requests.
	 * 
	 * @return True if the packet is explicitly typed as a request.
	 */
	public boolean validateRequest() {
		Log.debug("RPCMessage", "validateRequest", "request has type " + messageType + " and should be "
				+ MessageType.REQUEST);
		return messageType == MessageType.REQUEST;
	}

	/**
	 * Check if the packet is marked as response,
	 * and all the identifiers are correct as expected.
	 * Useful at the client side to validate incoming responses.
	 * 
	 * @param request
	 *            The original request packet sent by the client.
	 * @return True if the packet is explicitly typed as a reply,
	 *         and if the transaction, RPC, request and procedure IDs
	 *         are the same as those in the original request packet.
	 */
	public boolean validateResponse(RPCMessage request) {
		boolean isValid = true;

		// Step 1: check if the response is correctly typed
		isValid = isValid && (messageType == MessageType.REPLY);
		Log.debug("RPCMessage", "validateResponse", "request has type " + messageType + ", should be "
				+ MessageType.REPLY);

		// Step 2: check if the transaction ID is the same as that in the request
		isValid = isValid && (transactionID == request.getTransactionID());
		Log.debug("RPCMessage", "validateResponse",
				"transaction ID is " + transactionID + ", should be " + request.getTransactionID());

		// Step 3: check if the RPC ID is the same as that in the request
		isValid = isValid && (RPCId == request.getRPCId());
		Log.debug("RPCMessage", "validateResponse", "RPC ID is " + RPCId + ", should be " + request.getRPCId());

		// Step 4: check if the request ID in the response is the same as that in the request
		isValid = isValid && (requestID == request.getRequestID());
		Log.debug("RPCMessage", "validateResponse",
				"Request ID is " + requestID + ", should be " + request.getRequestID());

		// Step 5: check if the procedure ID is the same as that in the request
		isValid = isValid && (procedureID == request.getProcedureID());
		Log.debug("RPCMessage", "validateResponse",
				"Procedure ID is " + procedureID + ", should be " + request.getProcedureID());

		return isValid;
	}

	/**
	 * Check if the procedure suggested in the packet is the one
	 * that is intended.
	 * Use this function before processing each particular operation.
	 * 
	 * @param expectedCommand
	 *            The Command for the procedure that is currently expected.
	 * @return True if the packet is intended for the expected procedure.
	 */
	public boolean validateProcedure(Command expectedCommand) {
		Log.debug("RPCMessage", "validateProcedure", "expected: " + expectedCommand.getID() + " response: "
				+ procedureID);
		return procedureID == expectedCommand.getID();
	}

	public String toString() {
		StringBuilder prettyPrint = new StringBuilder();
		
		if (messageType == MessageType.REQUEST)
			prettyPrint.append("REQUEST ");
		else if (messageType == MessageType.REPLY)
			prettyPrint.append("RESPONSE ");
		
		prettyPrint.append(" Transaction " + transactionID);
		prettyPrint.append(" RPC ID " + RPCId);
		prettyPrint.append(" Request ID " + requestID);
		prettyPrint.append(" Procedure " + procedureID);
		prettyPrint.append(" CSV Data " + csv_data);
		prettyPrint.append(" Status " + status);
		
		return prettyPrint.toString();
	}
}