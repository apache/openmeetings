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
package org.apache.openmeetings.web.admin.rooms;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getSid;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.IUserService;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.admin.AdminUserChoiceProvider;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.util.time.Duration;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2Choice;
import org.wicketstuff.select2.Select2MultiChoice;

public class RoomForm extends AdminBaseForm<Room> {
	private static final long serialVersionUID = 1L;
	private final static List<Long> DROPDOWN_NUMBER_OF_PARTICIPANTS = Arrays.asList(2L, 4L, 6L, 8L, 10L, 12L, 14L, 16L, 20L, 25L, 32L, 50L,
			100L, 150L, 200L, 500L, 1000L);
	private final WebMarkupContainer roomList;
	private final TextField<String> pin;
	private final WebMarkupContainer moderatorContainer = new WebMarkupContainer("moderatorContainer");
	private final WebMarkupContainer clientsContainer = new WebMarkupContainer("clientsContainer");
	private final ListView<Client> clients;
	private List<Client> clientsInRoom = null;
	private IModel<User> moderator2add = Model.of((User)null);
	
	public RoomForm(String id, WebMarkupContainer roomList, final Room room) {
		super(id, new CompoundPropertyModel<Room>(room));
		this.roomList = roomList;
		setOutputMarkupId(true);
		RequiredTextField<String> name = new RequiredTextField<String>("name");
		name.setLabel(new Model<String>(Application.getString(193)));
		add(name);

		add(new DropDownChoice<Long>("numberOfPartizipants", //
				DROPDOWN_NUMBER_OF_PARTICIPANTS, //
				new ChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;
					@Override
					public Object getDisplayValue(Long id) {
						return id;
					}
					@Override
					public String getIdValue(Long id, int index) {
						return "" + id;
					}
				}));

		add(new RoomTypeDropDown("type").setRequired(true).setLabel(Model.of(Application.getString(194))));

		add(new TextArea<String>("comment"));

		add(new CheckBox("appointment").setEnabled(false));
		add(new CheckBox("ispublic"));

		List<Group> orgList = Application.getBean(GroupDao.class).get(0, Integer.MAX_VALUE);
		final List<RoomGroup> orgRooms = new ArrayList<RoomGroup>(orgList.size());
		for (Group org : orgList) {
			orgRooms.add(new RoomGroup(org, getModelObject()));
		}
		add(new Select2MultiChoice<RoomGroup>("roomGroups", null, new ChoiceProvider<RoomGroup>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(RoomGroup choice) {
				String name = choice.getGroup().getName();
				return name == null ? "" : name;
			}

			@Override
			public String getIdValue(RoomGroup choice) {
				Long id = choice.getGroup().getId();
				return id == null ? null : "" + id;
			}

			@Override
			public void query(String term, int page, Response<RoomGroup> response) {
				for (RoomGroup or : orgRooms) {
					if (Strings.isEmpty(term) || or.getGroup().getName().contains(term)) {
						response.add(or);
					}
				}
			}

			@Override
			public Collection<RoomGroup> toChoices(Collection<String> _ids) {
				List<Long> ids = new ArrayList<Long>();
				for (String id : _ids) {
					ids.add(Long.valueOf(id));
				}
				List<RoomGroup> list = new ArrayList<RoomGroup>();
				for (Group o : getBean(GroupDao.class).get(ids)) {
					list.add(new RoomGroup(o, RoomForm.this.getModelObject()));
				}
				return list;
			}
		}));

		add(new CheckBox("isDemoRoom"));
		TextField<Integer> demoTime = new TextField<Integer>("demoTime");
		demoTime.setLabel(new Model<String>(Application.getString(637)));
		add(demoTime);
		add(new CheckBox("allowUserQuestions"));
		add(new CheckBox("audioOnly"));
		add(new CheckBox("allowFontStyles"));
		add(new CheckBox("closed"));
		add(new TextField<String>("redirectURL"));
		add(new CheckBox("waitForRecording"));
		add(new CheckBox("allowRecording"));

		add(new CheckBox("hideTopBar"));
		add(new CheckBox("chatHidden"));
		add(new CheckBox("activitiesHidden"));
		add(new CheckBox("hideFilesExplorer"));
		add(new CheckBox("hideActionsMenu"));
		add(new CheckBox("hideScreenSharing"));
		add(new CheckBox("hideWhiteboard"));
		add(new CheckBox("showMicrophoneStatus"));
		add(new CheckBox("chatModerated"));
		add(new CheckBox("chatOpened"));
		add(new CheckBox("filesOpened"));
		add(new CheckBox("autoVideoSelect"));	
		
		// Users in this Room 
		clients = new ListView<Client>("clients", clientsInRoom) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<Client> item) {
				Client client = item.getModelObject();
				item.add(new Label("clientId", "" + client.getId()))
					.add(new Label("clientLogin", "" + client.getUsername()))
					.add(new ConfirmableAjaxBorder("clientDelete", getString("80"), getString("833")) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
							Client c = item.getModelObject();
							getBean(IUserService.class).kickUserByStreamId(getSid(), c.getStreamid()
									, c.getServer() == null ? 0 : c.getServer().getId());
							
							updateClients(target);
						}
					});
			}
		};
		add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));
		
		// Moderators
		final Select2Choice<User> moderatorChoice = new Select2Choice<User>("moderator2add", moderator2add, new AdminUserChoiceProvider() {
			private static final long serialVersionUID = 1L;

			@Override
			public void query(String term, int page, Response<User> response) {
				response.addAll(getBean(UserDao.class).get(term, false, page * PAGE_SIZE, PAGE_SIZE));
				response.setHasMore(PAGE_SIZE == response.getResults().size());
			}

			@Override
			public String getDisplayValue(User choice) {
				Address a = choice.getAddress();
				return String.format("\"%s %s\" <%s>", choice.getFirstname(), choice.getLastname(), a == null ? "" : a.getEmail());
			}
		});
		add(moderatorChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Room r = RoomForm.this.getModelObject();
				User u = moderator2add.getObject();
				boolean found = false;
				if (u != null) {
					if (r.getModerators() == null) {
						r.setModerators(new ArrayList<RoomModerator>());
					}
					for (RoomModerator rm : r.getModerators()) {
						if (rm.getUser().getId().equals(u.getId())) {
							found = true;
							break;
						}
					}
					if (!found) {
						RoomModerator rm = new RoomModerator();
						rm.setRoomId(r.getId());
						rm.setUser(u);
						r.getModerators().add(0, rm);
						moderator2add.setObject(null);
						target.add(moderatorContainer, moderatorChoice);
					}
				}
			}
		}).setOutputMarkupId(true));
		add(moderatorContainer.add(new ListView<RoomModerator>("moderators") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<RoomModerator> item) {
				RoomModerator moderator = item.getModelObject();
				Label name = new Label("uName", moderator.getUser().getFirstname() + " " + moderator.getUser().getLastname());
				if (moderator.getId() == null) {
					name.add(AttributeAppender.append("class", "newItem"));
				}
				item.add(new CheckBox("superModerator", new PropertyModel<Boolean>(moderator, "superModerator")))
					.add(new Label("userId", "" + moderator.getUser().getId()))
					.add(name)
					.add(new Label("email", moderator.getUser().getAddress().getEmail()))
					.add(new ConfirmableAjaxBorder("delete", getString("80"), getString("833")) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
							RoomForm.this.getModelObject().getModerators().remove(item.getIndex());
							target.add(moderatorContainer);
						}
					});
			}
		}).setOutputMarkupId(true));

        add(new CheckBox("moderated"));

		add(new TextField<String>("confno").setEnabled(false));
		add(pin = new TextField<String>("pin"));
		pin.setEnabled(room.isSipEnabled());
		add(new TextField<String>("ownerId").setEnabled(false));
		add(new AjaxCheckBox("sipEnabled") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateView(target);
			}
		}.setOutputMarkupId(true));
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	void updateClients(AjaxRequestTarget target) {
		long roomId = (getModelObject().getId() != null ? getModelObject().getId() : 0);  
		final List<Client> clientsInRoom = Application.getBean(ISessionManager.class).getClientListByRoom(roomId);
		clients.setDefaultModelObject(clientsInRoom);
		target.add(clientsContainer);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Room r = getModelObject();
		boolean newRoom = r.getId() == null;
		r = getBean(RoomDao.class).update(r, getUserId());
		if (newRoom) {
			for (RoomModerator rm : r.getModerators()) {
				rm.setRoomId(r.getId());
			}
			// FIXME double update
			getBean(RoomDao.class).update(getModelObject(), getUserId());
		}
		hideNewRecord();
		updateView(target);
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		setModelObject(new Room());
		updateView(target);
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Room r = getModelObject();
		if (r.getId() != null) {
			r = getBean(RoomDao.class).get(r.getId());
		} else {
			r = new Room();
		}
		setModelObject(r);
		updateView(target);
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(RoomDao.class).delete(getModelObject(), getUserId());
		target.add(roomList);
		setModelObject(new Room());
		updateView(target);
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	public void updateView(AjaxRequestTarget target) {
		target.add(this);
		target.add(roomList);
		target.add(pin.setEnabled(getModelObject().isSipEnabled()));
		updateClients(target);
		target.appendJavaScript("omRoomPanelInit();");
	}
	
	@Override
	protected void onDetach() {
		moderator2add.detach();
		super.onDetach();
	}
}
