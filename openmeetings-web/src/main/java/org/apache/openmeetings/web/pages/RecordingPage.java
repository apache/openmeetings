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

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.user.record.VideoInfo;
import org.apache.openmeetings.web.user.record.VideoPlayer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class RecordingPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;

	public RecordingPage(PageParameters p) {
		String hash = p.get("hash").toString();
		FlvRecording r = null;
		if (WebSession.get().signIn(hash)) {
			Long recId = getRecordingId();
			if (recId != null) {
				r = getBean(FlvRecordingDao.class).get(recId);
			}
		}
		add(new VideoInfo("info", r).setVisible(r != null), new VideoPlayer("player", r).setVisible(r != null));
	}

	@Override
	protected void onParameterArrival(IRequestParameters requestParameters, AjaxRequestTarget target) {
	}
}
