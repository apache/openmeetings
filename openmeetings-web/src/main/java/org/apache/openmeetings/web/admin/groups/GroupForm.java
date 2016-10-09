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
package org.apache.openmeetings.web.admin.groups;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogo;
import static org.apache.openmeetings.web.util.GroupLogoResourceReference.getUrl;

import java.io.File;

import org.apache.openmeetings.core.converter.GenerateImage;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.util.StoredFile;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.admin.AdminUserChoiceProvider;
import org.apache.openmeetings.web.common.UploadableImagePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.select2.Select2Choice;

public class GroupForm extends AdminBaseForm<Group> {
	private static final long serialVersionUID = 1L;
	private GroupUsersPanel usersPanel;
	private final WebMarkupContainer groupList;
	private final Select2Choice<User> userToadd;
	private final NumberTextField<Integer> maxFilesSize = new NumberTextField<>("maxFilesSize");
	private final NumberTextField<Integer> maxRecordingsSize = new NumberTextField<>("maxRecordingsSize");
	private final NumberTextField<Integer> maxRooms = new NumberTextField<>("maxRooms");
	private final NumberTextField<Integer> recordingTtl = new NumberTextField<>("recordingTtl");
	private final NumberTextField<Integer> reminderDays = new NumberTextField<>("reminderDays");
	
	static String formatUser(User choice) {
		return String.format("%s [%s %s]", choice.getLogin(), choice.getFirstname(), choice.getLastname());
	}
	
	public GroupForm(String id, WebMarkupContainer groupList, Group group) {
		super(id, new CompoundPropertyModel<Group>(group));
		this.groupList = groupList;
		setOutputMarkupId(true);
		
		usersPanel = new GroupUsersPanel("users", getGroupId());
		add(usersPanel);

		add(userToadd = new Select2Choice<User>("user2add", Model.of((User)null), new AdminUserChoiceProvider() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(User choice) {
				return formatUser(choice);
			}
		}));
		userToadd.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Group o = GroupForm.this.getModelObject();
				User u = userToadd.getModelObject();
				boolean found = false;
				if (o.getId() != null) {
					found = null != getBean(GroupUserDao.class).getByGroupAndUser(o.getId(), u.getId());
				}
				if (!found && u != null) {
					for (GroupUser ou : usersPanel.getUsers2add()) {
						if (ou.getUser().getId().equals(u.getId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						GroupUser ou = new GroupUser(o, u);
						usersPanel.getUsers2add().add(ou);

						userToadd.setModelObject(null);
						target.add(usersPanel, userToadd);
					}
				}
			}
		});
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new RequiredTextField<String>("name").setLabel(Model.of(getString("165"))));
		add(new UploadableImagePanel("logo") {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getImageUrl() {
				return getUrl(getRequestCycle(), GroupForm.this.getModelObject().getId());
			}

			@Override
			protected void processImage(StoredFile sf, File f) throws Exception {
				getBean(GenerateImage.class).resize(f, getGroupLogo(GroupForm.this.getModelObject().getId(), false), null, 23);
			}

			@Override
			protected String getTitle() {
				return getString("admin.group.form.logo");
			}
		});
		add(new TextField<String>("tag").setLabel(Model.of(getString("admin.group.form.tag"))));
		add(new AjaxCheckBox("limited") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				target.add(
					maxFilesSize.setEnabled(getModelObject())
					, maxRecordingsSize.setEnabled(getModelObject())
					, maxRooms.setEnabled(getModelObject())
					, recordingTtl.setEnabled(getModelObject())
					, reminderDays.setEnabled(getModelObject())
				);
			}
		}.setLabel(Model.of(getString("admin.group.form.limited"))));
		add(maxFilesSize.setLabel(Model.of(getString("admin.group.form.maxFilesSize"))).setEnabled(false).setOutputMarkupId(true));
		add(maxRecordingsSize.setLabel(Model.of(getString("admin.group.form.maxRecordingsSize"))).setEnabled(false).setOutputMarkupId(true));
		add(maxRooms.setLabel(Model.of(getString("admin.group.form.maxRooms"))).setEnabled(false).setOutputMarkupId(true));
		add(recordingTtl.setLabel(Model.of(getString("admin.group.form.recordingTtl"))).setEnabled(false).setOutputMarkupId(true));
		add(reminderDays.setLabel(Model.of(getString("admin.group.form.reminderDays"))).setEnabled(false).setOutputMarkupId(true));
	}

	public void updateView(AjaxRequestTarget target) {
		userToadd.setModelObject(null);
		usersPanel.update(getGroupId());
		maxFilesSize.setEnabled(getModelObject().isLimited());
		maxRecordingsSize.setEnabled(getModelObject().isLimited());
		maxRooms.setEnabled(getModelObject().isLimited());
		recordingTtl.setEnabled(getModelObject().isLimited());
		reminderDays.setEnabled(getModelObject().isLimited());
		target.add(this, groupList);
		target.appendJavaScript("groupsInit();");
	}

	private long getGroupId() {
		return getModelObject().getId() != null ? getModelObject().getId() : 0;
	}
	
	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> f) {
		setModelObject(new Group());
		updateView(target);
	}
	
	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Group org = getModelObject();
		if (org.getId() != null) {
			org = getBean(GroupDao.class).get(org.getId());
		} else {
			org = new Group();
		}
		setModelObject(org);
		updateView(target);
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(GroupDao.class).delete(getModelObject(), getUserId());
		setModelObject(new Group());
		updateView(target);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Group o = getModelObject();
		o = getBean(GroupDao.class).update(o, getUserId());
		setModelObject(o);
		for (GroupUser grpUser : usersPanel.getUsers2add()) {
			GroupUsersPanel.update(grpUser);
		}
		hideNewRecord();
		updateView(target);
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}
}
