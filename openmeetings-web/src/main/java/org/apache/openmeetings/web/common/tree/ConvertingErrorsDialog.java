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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItemLog;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class ConvertingErrorsDialog extends AbstractDialog<BaseFileItem> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Label message = new Label("message", Model.of((String)null));
	private final ListView<FileItemLog> logView = new ListView<FileItemLog>("row") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<FileItemLog> item) {
			FileItemLog l = item.getModelObject();
			item.add(new Label("exitCode", l.getExitCode()));
			item.add(new Label("message", l.getMessage()));
			if (!l.isOk()) {
				item.add(AttributeModifier.append(ATTR_CLASS, "alert"));
			}
			if (l.isWarn()) {
				item.add(AttributeModifier.append(ATTR_CLASS, "warn"));
			}
		}
	};

	public ConvertingErrorsDialog(String id, IModel<BaseFileItem> model) {
		super(id, "", model);
		add(container.add(message.setVisible(false), logView.setVisible(false)).setOutputMarkupId(true));
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("887"));
		super.onInitialize();
	}

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

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		BaseFileItem f = getModelObject();
		setTitle(handler, new ResourceModel(f.getType() == BaseFileItem.Type.Recording ? "887" : "convert.errors.file"));
		List<FileItemLog> logs = getBean(FileItemLogDao.class).get(f);
		if (f.getHash() == null) {
			message.setVisible(true);
			message.setDefaultModelObject(getString("888"));
		} else if (!f.exists()) {
			message.setVisible(true);
			message.setDefaultModelObject(getString(f.getType() == BaseFileItem.Type.Recording ? "1595" : "convert.errors.file.missing"));
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
		//no-op
	}

	@Override
	protected List<DialogButton> getButtons() {
		return new ArrayList<>();
	}
}
