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
package org.apache.openmeetings.util.mail;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

public class ByteArrayDataSource implements DataSource {
	private byte[] data; // data

	private String type; // content-type

	/* Create a DataSource from an input stream */
	public ByteArrayDataSource(InputStream is, String type) {
		this.type = type;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			int ch;

			while ((ch = is.read()) != -1) {
				os.write(ch);
			}
			data = os.toByteArray();
		} catch (IOException ioex) {
			//no-op
		}
	}

	/* Create a DataSource from a byte array */
	public ByteArrayDataSource(byte[] data, String type) {
		this.data = data;
		this.type = type;
	}

	/* Create a DataSource from a String */
	public ByteArrayDataSource(String data, String type) {
		this.data = data.getBytes(UTF_8);
		this.type = type;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (data == null)
			throw new IOException("no data");
		return new ByteArrayInputStream(data);
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("cannot do this");
	}

	@Override
	public String getContentType() {
		return type;
	}

	@Override
	public String getName() {
		return "dummy";
	}
}
