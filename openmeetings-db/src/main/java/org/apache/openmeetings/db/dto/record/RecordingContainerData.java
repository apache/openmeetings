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
package org.apache.openmeetings.db.dto.record;

import java.io.Serializable;

public class RecordingContainerData implements Serializable {
	private static final long serialVersionUID = 1L;

	private long userHomeSize;
	private long publicFileSize;

	public long getUserHomeSize() {
		return userHomeSize;
	}

	public void setUserHomeSize(long userHomeSize) {
		this.userHomeSize = userHomeSize;
	}

	public long getPublicFileSize() {
		return publicFileSize;
	}

	public void setPublicFileSize(long publicFileSize) {
		this.publicFileSize = publicFileSize;
	}
}
