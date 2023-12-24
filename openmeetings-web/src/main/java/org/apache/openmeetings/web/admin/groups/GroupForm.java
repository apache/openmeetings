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

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.web.util.GroupLogoResourceReference.getUrl;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogo;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogoDir;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import org.apache.openmeetings.core.converter.ImageConverter;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.admin.AdminUserChoiceProvider;
import org.apache.openmeetings.web.common.UploadableImagePanel;
import org.apache.openmeetings.util.StoredFile;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.wicketstuff.select2.Select2Choice;

import jakarta.inject.Inject;

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
	private final NumberTextField<Integer> notifyInterval = new NumberTextField<>("notifyInterval");
	private final UploadableImagePanel logo = new UploadableImagePanel("logo", true) {
		private static final long serialVersionUID = 1L;

		@Override
		protected String getImageUrl() {
			return getUrl(getRequestCycle(), GroupForm.this.getModelObject().getId());
		}

		@Override
		protected void processImage(StoredFile sf, File f) throws Exception {
			imageConverter.resize(f, getGroupLogo(GroupForm.this.getModelObject().getId(), false), null, 28, true);
		}

		@Override
		protected void deleteImage() throws Exception {
			Long groupId = GroupForm.this.getModelObject().getId();
			File flogo = new File(getGroupLogoDir(), String.format("logo%s.png", groupId));
			if (groupId != null) {
				Files.deleteIfExists(flogo.toPath());
			}
		}

		@Override
		protected String getTitle() {
			return getString("admin.group.form.logo");
		}
	};

	@Inject
	private ImageConverter imageConverter;
	@Inject
	private GroupUserDao groupUserDao;
	@Inject
	private GroupDao groupDao;

	public GroupForm(String id, WebMarkupContainer groupList, Group group) {
		super(id, new CompoundPropertyModel<>(group));
		setMultiPart(true);
		this.groupList = groupList;
		setOutputMarkupId(true);

		usersPanel = new GroupUsersPanel("users", getGroupId());
		add(usersPanel);

		add(userToadd = new Select2Choice<>("user2add", Model.of((User)null), new AdminUserChoiceProvider() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(User choice) {
				return formatUser(choice);
			}
		}));
		userToadd.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
			Group o = GroupForm.this.getModelObject();
			User u = userToadd.getModelObject();
			boolean found = false;
			if (o.getId() != null) {
				found = null != groupUserDao.getByGroupAndUser(o.getId(), u.getId());
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
		}));
	}

	static String formatUser(User choice) {
		return String.format("%s [%s]", choice.getLogin(), choice.getDisplayName());
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final boolean isGroupAdmin = hasGroupAdminLevel(getRights());
		setNewVisible(!isGroupAdmin);
		setNewRecordVisible(!isGroupAdmin);
		userToadd.setEnabled(!isGroupAdmin);
		add(new RequiredTextField<String>("name").setLabel(new ResourceModel("165")));
		add(logo);
		add(new TextField<String>("tag").setLabel(new ResourceModel("admin.group.form.tag")));
		add(new CheckBox("restricted").setLabel(new ResourceModel("restricted.group.files")));
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
		}.setLabel(new ResourceModel("admin.group.form.limited")));
		add(maxFilesSize.setLabel(new ResourceModel("admin.group.form.maxFilesSize")).setEnabled(false).setOutputMarkupId(true));
		add(maxRecordingsSize.setLabel(new ResourceModel("admin.group.form.maxRecordingsSize")).setEnabled(false).setOutputMarkupId(true));
		add(maxRooms.setLabel(new ResourceModel("admin.group.form.maxRooms")).setEnabled(false).setOutputMarkupId(true));
		add(recordingTtl.setLabel(new ResourceModel("admin.group.form.recordingTtl")).setEnabled(false).setOutputMarkupId(true));
		add(reminderDays.setLabel(new ResourceModel("admin.group.form.reminderDays")).setEnabled(false).setOutputMarkupId(true));
		add(notifyInterval.setLabel(new ResourceModel("admin.group.new.users.notification.interval")));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		setDelVisible(!hasGroupAdminLevel(getRights()));
	}

	public void updateView(AjaxRequestTarget target) {
		userToadd.setModelObject(null);
		usersPanel.update(getGroupId());
		maxFilesSize.setEnabled(getModelObject().isLimited());
		maxRecordingsSize.setEnabled(getModelObject().isLimited());
		maxRooms.setEnabled(getModelObject().isLimited());
		recordingTtl.setEnabled(getModelObject().isLimited());
		reminderDays.setEnabled(getModelObject().isLimited());
		notifyInterval.setModelObject(getModelObject().getNotifyInterval());
		logo.update();
		target.add(this, groupList);
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
			org = groupDao.get(org.getId());
		} else {
			org = new Group();
		}
		setModelObject(org);
		updateView(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		groupDao.delete(getModelObject(), getUserId());
		setModelObject(new Group());
		updateView(target);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Group o = getModelObject();
		o = groupDao.update(o, getUserId());
		setModelObject(o);
		for (GroupUser grpUser : usersPanel.getUsers2add()) {
			usersPanel.update(grpUser);
		}
		logo.process(Optional.of(target));
		setNewRecordVisible(false);
		updateView(target);
	}
}
