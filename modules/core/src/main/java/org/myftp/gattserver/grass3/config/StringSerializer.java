package org.myftp.gattserver.grass3.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.myftp.gattserver.grass3.util.Base64Coder;

public class StringSerializer {

	/** Read the object from Base64 string. */
	public static <T> T deserialize(String s, Class<T> type)
			throws IOException, ClassNotFoundException {
		byte[] data = Base64Coder.decode(s);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bais);
		T result = (T) in.readObject();
		in.close();
		return result;
	}

	/** Write the object to a Base64 string. */
	public static String serialize(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(o); // Serialize Object
		out.close();
		return new String(Base64Coder.encode(baos.toByteArray()));
	}
}
