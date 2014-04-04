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

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.record.FlvRecordingLogDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecordingLog;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class RecordingErrorsDialog extends AbstractDialog<FlvRecording> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Label message = new Label("message", Model.of((String)null));
	private final ListView<FlvRecordingLog> logView = new ListView<FlvRecordingLog>("row") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<FlvRecordingLog> item) {
			FlvRecordingLog l = item.getModelObject();
			item.add(new Label("exitCode", l.getExitValue()));
			item.add(new Label("message", l.getFullMessage()));
			if (!"0".equals(l.getExitValue())) {
				item.add(AttributeModifier.replace("class", "alert"));
			}
		}
	};

	@Override
	public int getWidth() {
		return 600;
	}
	
	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public boolean isModal() {
		return true;
	}

	public RecordingErrorsDialog(String id, IModel<FlvRecording> model) {
		super(id, WebSession.getString(887), model);
		add(container.add(message.setVisible(false), logView.setVisible(false)).setOutputMarkupId(true));
	}
	
	@Override
	protected void onOpen(AjaxRequestTarget target) {
		FlvRecording f = getModelObject();
		List<FlvRecordingLog> logs = getBean(FlvRecordingLogDao.class).getByRecordingId(f.getFlvRecordingId());
		if (f.getFileHash() == null) {
			message.setVisible(true);
			message.setDefaultModelObject(WebSession.getString(888));
		} else if (!isRecordingExists(f.getFileHash() + MP4_EXTENSION)) {
			message.setVisible(true);
			message.setDefaultModelObject(WebSession.getString(1595));
		} else {
			message.setVisible(false);
		}
		if (!logs.isEmpty()) {
			logView.setVisible(false);
			logView.setList(logs).setVisible(true);
		}
		target.add(container);
		super.onOpen(target);
	}
	
	public void onClose(AjaxRequestTarget target, DialogButton button) {
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return new ArrayList<DialogButton>();
	}
}
