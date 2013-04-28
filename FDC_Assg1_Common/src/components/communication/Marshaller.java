package components.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

import components.Log;

public class Marshaller {
	public static String marshall(Object data) {
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
//			return request.createResponse("", Status.INVALID_INPUT); // TODO add status
		}
	}

	public static Object unmarshall(String rawData) {
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
