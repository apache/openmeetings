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

import java.io.IOException;

import org.apache.openmeetings.core.remote.KurentoHandler;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room.Right;
import org.apache.openmeetings.web.room.RoomPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.github.openjson.JSONObject;

public class InterviewWbPanel extends AbstractWbPanel {
	private static final long serialVersionUID = 1L;
	public static final ResourceReference INTERVIEWWB_JS_REFERENCE = new JavaScriptResourceReference(WbPanel.class, "interviewwb.js");
	@SpringBean
	private KurentoHandler kurento;

	public InterviewWbPanel(String id, RoomPanel rp) {
		super(id, rp);
	}

	@Override
	protected String getRole() {
		return rp.getClient().hasRight(Right.moderator) ? Right.moderator.name() : ROLE_NONE;
	}

	@Override
	public void sendFileToWb(final BaseFileItem fi, boolean clean) {
		//no-op
	}

	@Override
	protected void processWbAction(WbAction a, JSONObject obj, AjaxRequestTarget target) throws IOException {
		Client c = rp.getClient();
		if (rp.getRoom().isAllowRecording() && c.hasRight(Right.moderator)) {
			switch (a) {
				case startRecording:
					kurento.startRecording(c);
					break;
				case stopRecording:
					kurento.stopRecording(c);
					break;
				default:
					//no-op
			}
		}
	}
}
