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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.util.OmFileHelper.MP4_MIME_TYPE;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.common.MainPanel;
import org.apache.openmeetings.web.room.RoomResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.media.Source;
import org.apache.wicket.markup.html.media.video.Video;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class VideoPlayer extends Panel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Mp4RecordingResourceReference mp4RecRes = new Mp4RecordingResourceReference();
	private final PngRecordingResourceReference posterRecRes = new PngRecordingResourceReference();
	private final RoomResourceReference mp4FileRes = new RoomResourceReference();
	private final RoomResourceReference posterFileRes = new RoomResourceReference();
	private final Video player = new Video("player") {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isAutoplay() {
			return false;
		}

		@Override
		public boolean hasControls() {
			return true;
		}
	};
	private final Source mp4Rec = new Source("mp4", mp4RecRes);
	private final Source mp4File = new Source("mp4", mp4FileRes);

	public VideoPlayer(String id) {
		super(id);
		setRenderBodyOnly(true);
		add(container.setOutputMarkupPlaceholderTag(true));
		mp4Rec.setDisplayType(true);
		mp4Rec.setType(MP4_MIME_TYPE);
		mp4File.setDisplayType(true);
		mp4File.setType(MP4_MIME_TYPE);
		player.add(mp4Rec);
		container.add(player);
		update(null, null);
	}

	public VideoPlayer update(AjaxRequestTarget target, BaseFileItem r) {
		boolean videoExists = r != null && r.exists();
		if (videoExists) {
			PageParameters pp = new PageParameters();
			if (r instanceof Recording) {
				pp.add("id", r.getId());
				mp4Rec.setPageParameters(pp);
				player.replace(mp4Rec);
				player.setPoster(posterRecRes, pp);
			} else {
				pp.add("id", r.getId()).add("uid", findParent(MainPanel.class).getClient().getUid());
				mp4File.setPageParameters(pp);
				player.replace(mp4File);
				player.setPoster(posterFileRes, new PageParameters(pp).add("preview", true));
			}
		}
		container.setVisible(videoExists);
		if (target != null) {
			target.add(container);
		}

		return this;
	}
}
