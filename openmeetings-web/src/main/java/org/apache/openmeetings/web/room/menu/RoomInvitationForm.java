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
package org.apache.openmeetings.web.room.menu;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.room.Invitation.MessageType;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.AuthLevelUtil;
import org.apache.openmeetings.service.room.InvitationManager;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.InvitationForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2MultiChoice;

import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class RoomInvitationForm extends InvitationForm {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Red5LoggerFactory.getLogger(RoomInvitationForm.class, webAppRootKey);
	private final RadioGroup<InviteeType> rdi = new RadioGroup<>("inviteeType", Model.of(InviteeType.user));
	private final Long roomId;
	private final WebMarkupContainer groupContainer = new WebMarkupContainer("groupContainer");
	final Select2MultiChoice<Group> groups = new Select2MultiChoice<Group>("groups"
			, new CollectionModel<Group>(new ArrayList<Group>())
			, new ChoiceProvider<Group>() {
				private static final long serialVersionUID = 1L;

				@Override
				public void query(String term, int page, Response<Group> response) {
					if (WebSession.getRights().contains(User.Right.Admin)) {
						List<Group> groups = getBean(GroupDao.class).get(0, Integer.MAX_VALUE);
						for (Group g : groups) {
							if (Strings.isEmpty(term) || g.getName().toLowerCase().contains(term.toLowerCase())) {
								response.add(g);
							}
						}
					} else {
						User u = getBean(UserDao.class).get(getUserId());
						for (GroupUser ou : u.getGroupUsers()) {
							if (Strings.isEmpty(term) || ou.getGroup().getName().toLowerCase().contains(term.toLowerCase())) {
								response.add(ou.getGroup());
							}
						}
					}
				}

				@Override
				public Collection<Group> toChoices(Collection<String> ids) {
					Collection<Group> c = new ArrayList<>();
					for (String id : ids) {
						c.add(getBean(GroupDao.class).get(Long.valueOf(id)));
					}
					return c;
				}

				@Override
				public String getDisplayValue(Group choice) {
					return choice.getName();
				}

				@Override
				public String getIdValue(Group choice) {
					Long id = choice.getId();
					return id == null ? null : "" + id;
				}
			});
	final WebMarkupContainer sipContainer = new WebMarkupContainer("sip-container");

	enum InviteeType {
		user
		, group
	}

	public RoomInvitationForm(String id, Long roomId) {
		super(id);
		this.roomId = roomId;
		boolean showGroups = AuthLevelUtil.hasAdminLevel(getRights());
		add(rdi.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean groupsEnabled = InviteeType.group == rdi.getModelObject();
				updateButtons(target);
				target.add(groups.setEnabled(groupsEnabled), recipients.setEnabled(!groupsEnabled));
			}
		}));
		groupContainer.add(
			groups.setLabel(Model.of(Application.getString(126))).setRequired(true).add(new AjaxFormComponentUpdatingBehavior("change") {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					url.setModelObject(null);
					updateButtons(target);
				}
			}).setOutputMarkupId(true)
			, new Radio<InviteeType>("group", Model.of(InviteeType.group))
		);
		rdi.add(recipients, groupContainer.setVisible(showGroups));
		rdi.add(new Radio<InviteeType>("user", Model.of(InviteeType.user)));
		add(sipContainer.setOutputMarkupPlaceholderTag(true).setOutputMarkupId(true));
		sipContainer.add(new Label("room.confno", "")).setVisible(false);
	}

	@Override
	protected void updateButtons(AjaxRequestTarget target) {
		if (rdi.getModelObject() == InviteeType.user) {
			super.updateButtons(target);
		} else {
			Collection<Group> to = groups.getModelObject();
			dialog.send.setEnabled(to.size() > 0, target);
			dialog.generate.setEnabled(false, target);
		}
	}
	
	@Override
	public void updateModel(AjaxRequestTarget target) {
		super.updateModel(target);
		Invitation i = getModelObject();
		i.setRoom(getBean(RoomDao.class).get(roomId));
		if (i.getRoom() != null) {
			target.add(sipContainer.replace(new Label("room.confno", i.getRoom().getConfno())).setVisible(i.getRoom().isSipEnabled()));
		}
		groups.setModelObject(new ArrayList<Group>());
		groups.setEnabled(false);
		rdi.setModelObject(InviteeType.user);
	}

	@Override
	public void onClick(AjaxRequestTarget target, DialogButton button) {
		//TODO need to be reviewed
		if (button.equals(dialog.send) && Strings.isEmpty(url.getModelObject()) && rdi.getModelObject() == InviteeType.group) {
			for (Group g : groups.getModelObject()) {
				for (GroupUser ou : getBean(GroupUserDao.class).get(g.getId(), 0, Integer.MAX_VALUE)) {
					Invitation i = create(ou.getUser());
					try {
						getBean(InvitationManager.class).sendInvitationLink(i, MessageType.Create, subject.getModelObject(), message.getModelObject(), false);
					} catch (Exception e) {
						log.error("error while sending invitation by Group ", e);
					}
				}
			}
		}
		super.onClick(target, button);
	}
}
