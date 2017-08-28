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
package org.apache.openmeetings.web.room.wb;

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class InterviewWbPanel extends AbstractWbPanel {
	private static final long serialVersionUID = 1L;
	public final static ResourceReference INTERVIEWWB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "interviewwb.js");

	public InterviewWbPanel(String id, RoomPanel rp) {
		super(id, rp);
	}

	@Override
	public InterviewWbPanel update(IPartialPageRequestHandler handler) {
		return this;
	}

	@Override
	public void sendFileToWb(final FileItem fi, boolean clean) {
	}
}
