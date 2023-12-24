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
package org.apache.openmeetings.web.admin.extra;

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getRights;

import java.util.ArrayList;

import org.apache.openmeetings.db.dao.room.ExtraMenuDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.GroupChoiceProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.wicketstuff.select2.Select2MultiChoice;

import jakarta.inject.Inject;

public class ExtraForm extends AdminBaseForm<ExtraMenu> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer list;
	final Select2MultiChoice<Group> groups = new Select2MultiChoice<>("groups"
			, new CollectionModel<>(new ArrayList<>())
			, new GroupChoiceProvider());

	@Inject
	private ExtraMenuDao menuDao;
	@Inject
	private GroupDao groupDao;

	public ExtraForm(String id, final WebMarkupContainer list, ExtraMenu m) {
		super(id, new CompoundPropertyModel<>(m));
		this.list = list;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new RequiredTextField<String>("name").setLabel(new ResourceModel("165")));
		add(new RequiredTextField<String>("link").setLabel(new ResourceModel("admin.extra.link")));
		add(groups.setLabel(new ResourceModel("126")).setRequired(hasGroupAdminLevel(getRights())));
		add(new TextArea<String>("description").setLabel(new ResourceModel("lbl.description")));
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		ExtraMenu m = getModelObject();
		m.getGroups().clear();
		for (Group g : groups.getModelObject()) {
			m.getGroups().add(g.getId());
		}
		setModelObject(menuDao.update(m, WebSession.getUserId()));
		setNewVisible(false);
		target.add(this, list);
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		ExtraMenu m = getModelObject();
		groups.setModelObject(m == null || m.getGroups() == null || m.getGroups().isEmpty() ? new ArrayList<>() : groupDao.get(m.getGroups()));
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new ExtraMenu());
		target.add(this);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		ExtraMenu m = getModelObject();
		if (m.getId() != null) {
			m = menuDao.get(m.getId());
		} else {
			m = new ExtraMenu();
		}
		this.setModelObject(m);
		target.add(this);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		menuDao.delete(getModelObject(), WebSession.getUserId());
		this.setModelObject(new ExtraMenu());
		target.add(list, this);
	}
}
