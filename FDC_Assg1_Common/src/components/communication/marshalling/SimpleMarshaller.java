/* Utility for marshalling objects into strings.
 * Uses inbuilt Java features to stream bytes out of an object,
 * along with a third-party base64 encoder from Apache Commons library
 * to ensure the bytes are encoded in the proper format.
 * 
 * Useful for passing lists of Message objects, when conversion to CSV 
 * would be a nightmare!
 * 
 * Usage:
 * 1. Compress an object into a string using marshallToString(...)
 * 2. Restore the object back using unmarshallString(...)
 */
package components.communication.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

import components.utilities.Log;

public class SimpleMarshaller {

	/**
	 * Convert an object into a string.
	 * Uses inbuilt Java features to stream bytes out of an object,
	 * along with a third-party base64 encoder from Apache Commons library
	 * to ensure the bytes are encoded in the proper format.
	 * 
	 * @param data
	 *            Object to be marshalled.
	 * @return The string representing the full object.
	 */
	public static String marshallToString(Object data) {
		// Marshal them all into a string
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			// Output the object into a stream of bytes
			oos = new ObjectOutputStream(baos);
			oos.writeObject(data);
			oos.close();

			Log.debug("Marshaller", "marshall", "Data marshalled");
			return new String(Base64.encodeBase64(baos.toByteArray()));

		} catch (IOException e) {
			// Marshalling failed
			Log.error("Marshaller", "marshall", "Couldn't write the data, apparently!", e);
			return null;
			// return request.createResponse("", Status.INVALID_INPUT); // TODO add status
		}
	}

	/**
	 * Restore an object back from a marshalled string.
	 * Uses inbuilt Java features to stream bytes into of an object,
	 * along with a third-party base64 encoder from Apache Commons library
	 * to ensure the bytes are decoded in the proper format.
	 * 
	 * @param rawData
	 *            The marshalled string representing the object.
	 * @return The full, restored object.
	 */
	public static Object unmarshallString(String rawData) {
		// Unmarshalling step 1: Decode from base 64
		byte[] data = Base64.decodeBase64(rawData.getBytes());
		ObjectInputStream ois;
		try {
			// Unmarshalling step 2: read string as object
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object reconstitutedObject = ois.readObject();
			ois.close();

			Log.debug("Marshaller", "unmarshall", "Successfully reconstructed raw data");
			return reconstitutedObject;

		} catch (IOException e) {
			// Unmarshalling failed
			Log.error("Marshaller", "unmarshall", "Couldn't read from the data, apparently!", e);
			return null;

		} catch (ClassNotFoundException e) {
			// Unmarshalling failed
			Log.error("Marshaller", "unmarshall", "Pretty sure the data is not a marshalled object!", e);
			return null;
		}
	}
}
