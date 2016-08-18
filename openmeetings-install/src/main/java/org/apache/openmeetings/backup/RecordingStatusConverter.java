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
package org.apache.openmeetings.backup;

import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class RecordingStatusConverter extends OmConverter<Recording.Status> {
	private static final String PROCESSING = "PROCESSING";
	public RecordingStatusConverter() {}

	@Override
	public Recording.Status read(InputNode node) throws Exception {
		Recording.Status result = null;
		String val = node.getValue();
		try {
			result = Recording.Status.valueOf(val);
		} catch (Exception e) {
			result = PROCESSING.equals(val) ? Status.CONVERTING : Status.NONE;
		}
		return result;
	}

	@Override
	public void write(OutputNode node, Recording.Status value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? Status.NONE.name() : value.name());
	}
}
