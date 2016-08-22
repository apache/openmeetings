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
package org.apache.openmeetings.web.pages;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getRecordingId;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.IUpdatable;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.openmeetings.web.user.record.VideoPlayer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public class HashPage extends BaseInitedPage implements IUpdatable {
	private static final long serialVersionUID = 1L;
	private final VideoInfo vi = new VideoInfo("info", null);
	private final VideoPlayer vp = new VideoPlayer("player", null);

	public HashPage(PageParameters p) {
		WebSession ws = WebSession.get();
		StringValue hash = p.get("hash");
		Recording r = null;
		if (!hash.isEmpty()) {
			ws.signIn(hash.toString(), true);
		}
		Invitation i = ws.getInvitation();
		if (i != null && !i.isPasswordProtected()) {
			r = i.getRecording();
		} else if (i == null) {
			Long recId = getRecordingId();
			if (recId != null) {
				r = getBean(RecordingDao.class).get(recId);
			}
		}
		add(vi.setShowShare(false).update(null, r).setOutputMarkupPlaceholderTag(true).setVisible(r != null)
			, vp.update(null, r).setOutputMarkupPlaceholderTag(true).setVisible(r != null)
			, new InvitationPasswordDialog("i-pass", this));
	}

	@Override
	protected void onParameterArrival(IRequestParameters requestParameters, AjaxRequestTarget target) {
	}
	
	@Override
	public void update(AjaxRequestTarget target) {
		Invitation i = WebSession.get().getInvitation();
		target.add(vi.update(target, i.getRecording()).setVisible(true)
				, vp.update(target, i.getRecording()).setVisible(true));
	}
}
