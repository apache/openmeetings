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
package org.apache.openmeetings.web.common.tree;

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.record.RecordingLogDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingLog;
import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class ConvertingErrorsDialog extends AbstractDialog<Recording> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Label message = new Label("message", Model.of((String)null));
	private final ListView<RecordingLog> logView = new ListView<RecordingLog>("row") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<RecordingLog> item) {
			RecordingLog l = item.getModelObject();
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

	public ConvertingErrorsDialog(String id, IModel<Recording> model) {
		super(id, Application.getString(887), model);
		add(container.add(message.setVisible(false), logView.setVisible(false)).setOutputMarkupId(true));
	}
	
	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		Recording f = getModelObject();
		List<RecordingLog> logs = getBean(RecordingLogDao.class).getByRecordingId(f.getId());
		if (f.getHash() == null) {
			message.setVisible(true);
			message.setDefaultModelObject(Application.getString(888));
		} else if (!f.exists()) {
			message.setVisible(true);
			message.setDefaultModelObject(Application.getString(1595));
		} else {
			message.setVisible(false);
		}
		if (!logs.isEmpty()) {
			logView.setVisible(false);
			logView.setList(logs).setVisible(true);
		}
		handler.add(container);
		super.onOpen(handler);
	}
	
	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
	}
	
	@Override
	protected List<DialogButton> getButtons() {
		return new ArrayList<DialogButton>();
	}
}
