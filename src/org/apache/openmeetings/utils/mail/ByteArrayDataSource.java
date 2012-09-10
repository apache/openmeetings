/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.utils.mail;

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
			os.close();
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
