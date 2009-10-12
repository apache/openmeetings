package org.openmeetings.utils.mail;

import java.io.*;
import javax.activation.*;

public class ByteArrayDataSource implements DataSource {
	private byte[] data; // data

	private String type; // content-type

	/* Create a DataSource from an input stream */
	public ByteArrayDataSource(InputStream is, String type) {
		this.type = type;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int ch;

			while ((ch = is.read()) != -1)
				os.write(ch);
			data = os.toByteArray();

		} catch (IOException ioex) {
		}
	}

	/* Create a DataSource from a byte array */
	public ByteArrayDataSource(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/* Create a DataSource from a String */
	public ByteArrayDataSource(String data, String type) {
		try {
			// Assumption that the string contains only ASCII
			// characters! Otherwise just pass a charset into this
			// constructor and use it in getBytes()
			this.data = data.getBytes("utf-8");
		} catch (UnsupportedEncodingException uex) {
		}
		this.type = type;
	}

	public InputStream getInputStream() throws IOException {
		if (data == null)
			throw new IOException("no data");
		return new ByteArrayInputStream(data);
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	public String getContentType() {
		return type;
	}

	public String getName() {
		return "dummy";
	}
}
