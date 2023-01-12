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
package org.apache.openmeetings.db.bind.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.util.string.Strings;

public class RecordingStatusAdapter extends XmlAdapter<String, Recording.Status> {

	@Override
	public String marshal(Recording.Status v) throws Exception {
		return "" + (v == null
				? Recording.Status.NONE.name()
				: v.name());
	}

	@Override
	public Recording.Status unmarshal(String v) throws Exception {
		if ("PROCESSING".equalsIgnoreCase(v)) {
			return Recording.Status.CONVERTING;
		}
		Recording.Status result = Status.NONE;
		if (!Strings.isEmpty(v)) {
			try {
				result = Recording.Status.valueOf(v);
			} catch (Exception e) {
				//no-op
			}
		}
		return result;
	}
}
