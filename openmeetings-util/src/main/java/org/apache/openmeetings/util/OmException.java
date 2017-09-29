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
package org.apache.openmeetings.util;

public class OmException extends Exception {
	private static final long serialVersionUID = 1L;
	public static final OmException UNKNOWN = new OmException("error.unknown");
	public static final OmException BAD_CREDENTIALS = new OmException("error.bad.credentials");
	private final String key;

	public OmException(String key) {
		super();
		this.key = key;
	}

	public OmException(Throwable cause) {
		super(cause);
		this.key = null;
	}

	public String getKey() {
		return key;
	}
}
