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

import java.util.List;

import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.FileItemLog;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import jakarta.inject.Inject;

public class ConvertingErrorsDialog extends Modal<BaseFileItem> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final Label message = new Label("message", Model.of((String)null));
	private final ListView<FileItemLog> logView = new ListView<>("row") {
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
	private Component headerLabel;

	@Inject
	private FileItemLogDao fileLogDao;

	public ConvertingErrorsDialog(String id, IModel<BaseFileItem> model) {
		super(id, model);
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("887"));
		size(Modal.Size.Large);

		super.onInitialize();
		add(container.add(message.setVisible(false), logView.setVisible(false)).setOutputMarkupId(true));
	}

	@Override
	protected Component createHeaderLabel(String id, String label) {
		headerLabel = super.createHeaderLabel(id, label);
		return headerLabel;
	}

	@Override
	public Modal<BaseFileItem> show(IPartialPageRequestHandler handler) {
		BaseFileItem f = getModelObject();
		headerLabel.setDefaultModel(new ResourceModel(f.getType() == BaseFileItem.Type.RECORDING ? "887" : "convert.errors.file"));

		List<FileItemLog> logs = fileLogDao.get(f);
		if (f.getHash() == null) {
			message.setVisible(true);
			message.setDefaultModelObject(getString("888"));
		} else if (!f.exists()) {
			message.setVisible(true);
			message.setDefaultModelObject(getString(f.getType() == BaseFileItem.Type.RECORDING ? "1595" : "convert.errors.file.missing"));
		} else {
			message.setVisible(false);
		}
		if (!logs.isEmpty()) {
			logView.setVisible(false);
			logView.setList(logs).setVisible(true);
		}
		handler.add(container, headerLabel);
		return super.show(handler);
	}
}
