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
package org.apache.openmeetings.web.admin.connection.dto;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.db.entity.basic.Client;

/**
 * Wrapper object for the UI display as the list item might be a client (that
 * may have also has ConnectionListKStreamItem(KStream) referenced).<br>
 * Or<br>
 * A single ConnectionListKStreamItem(KStream).<br>
 *
 * We want to see a total list of connections, a client represents a WebSocket and session.
 * A ConnectionListKStreamItem is a wrapper of KStream (which is a MediaStream).
 *
 * @author sebawagner
 *
 */
public class ConnectionListItem implements IDataProviderEntity {
	private static final long serialVersionUID = 1L;

	private Client client;
	private ConnectionListKStreamItem stream;

	public ConnectionListItem(Client client, ConnectionListKStreamItem stream) {
		super();
		this.client = client;
		this.stream = stream;
	}
	public Client getClient() {
		return client;
	}
	public ConnectionListKStreamItem getStream() {
		return stream;
	}
	@Override
	public Long getId() {
		if (client != null) {
			return client.getId();
		}
		return null;
	}
	@Override
	public void setId(Long id) {
		// no-op
	}
}
