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

import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.web.util.Mp4RecordingResourceReference;
import org.apache.openmeetings.web.util.OggRecordingResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.media.Source;
import org.apache.wicket.markup.html.media.video.Video;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.request.resource.UrlResourceReference;

public class VideoPlayer extends Panel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer wait = new WebMarkupContainer("wait"); //FIXME not used
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Mp4RecordingResourceReference mp4res = new Mp4RecordingResourceReference();
	private final OggRecordingResourceReference oggres = new OggRecordingResourceReference();
	private final OmVideo player = new OmVideo("player", null);
	private final Source mp4 = new Source("mp4", mp4res);
	private final Source ogg = new Source("ogg", oggres);

	public VideoPlayer(String id) {
		this(id, null);
	}
	
	public VideoPlayer(String id, FlvRecording r) {
		super(id);
		add(container.setOutputMarkupPlaceholderTag(true));
		mp4.setDisplayType(true);
		mp4.setType("video/mp4");
		ogg.setDisplayType(true);
		ogg.setType("video/ogg");
		player.add(mp4, ogg);
		container.add(wait.setVisible(false), player);
		update(null, r);
	}
	
	public VideoPlayer update(AjaxRequestTarget target, FlvRecording r) {
		boolean videoExists = r != null && getMp4Recording(r.getFileHash()).exists();
		if (videoExists) {
			PageParameters pp = new PageParameters().add("id", r.getId());
			mp4.setPageParameters(pp);
			ogg.setPageParameters(pp);
			player.recId = r.getId();
		}
		container.setVisible(videoExists);
		if (target != null) {
			target.add(container);
		}
		
		return this;
	}
	
	private static class OmVideo extends Video {
		private static final long serialVersionUID = 1L;
		Long recId = null;
		
		OmVideo(String id, Long recId) {
			super(id);
			this.recId = recId;
		}
		
		@Override
		public boolean isAutoplay() {
			return false;
		}
		
		@Override
		public boolean hasControls() {
			return true;
		}
		
		@Override
		public ResourceReference getPoster() {
			return recId == null ? null : new UrlResourceReference(Url.parse("recordings/jpg/" + recId)).setContextRelative(true);
		}
	}
}
