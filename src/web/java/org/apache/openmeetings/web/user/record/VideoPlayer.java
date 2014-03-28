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

import static org.apache.openmeetings.util.OmFileHelper.getMp4Recording;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.web.util.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.OggRecordingResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.html5.media.MediaSource;
import org.wicketstuff.html5.media.video.Html5Video;

public class VideoPlayer extends Panel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer wait = new WebMarkupContainer("wait"); //FIXME not used
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Mp4RecordingResourceReference mp4res = new Mp4RecordingResourceReference();
	private final OggRecordingResourceReference oggres = new OggRecordingResourceReference();
	private final IModel<List<MediaSource>> playerModel = new ListModel<MediaSource>(new ArrayList<MediaSource>());
	private final OmHtml5Video player = new OmHtml5Video("player", playerModel, null);

	public VideoPlayer(String id) {
		this(id, null);
	}
	
	public VideoPlayer(String id, FlvRecording r) {
		super(id);
		add(container.setOutputMarkupPlaceholderTag(true));
		container.add(wait.setVisible(false), player);
		update(null, r);
	}
	
	public VideoPlayer update(AjaxRequestTarget target, FlvRecording r) {
		boolean videoExists = r != null && getMp4Recording(r.getFileHash()).exists();
		if (videoExists) {
			PageParameters pp = new PageParameters().add("id", r.getFlvRecordingId());
			playerModel.setObject(Arrays.asList(
					new MediaSource("" + getRequestCycle().urlFor(mp4res, pp), "video/mp4")
					, new MediaSource("" + getRequestCycle().urlFor(oggres, pp), "video/ogg")));
			player.recId = r.getFlvRecordingId();
		}
		container.setVisible(videoExists);
		if (target != null) {
			target.add(container);
		}
		
		return this;
	}
	
	private static class OmHtml5Video extends Html5Video {
		private static final long serialVersionUID = 1L;
		Long recId = null;
		
		OmHtml5Video(String id, IModel<List<MediaSource>> model, Long recId) {
			super(id, model);
			this.recId = recId;
		}
		
		@Override
		protected boolean isAutoPlay() {
			return false;
		}
		
		@Override
		protected boolean isControls() {
			return true;
		}
		
		@Override
		protected String getPoster() {
			return recId == null ? null : "recordings/jpg/" + recId;
		}
	}
}
