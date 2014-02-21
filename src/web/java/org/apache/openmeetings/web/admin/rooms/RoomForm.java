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
import java.util.Date;
import java.util.List;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.room.RoomModeratorsDao;
import org.apache.openmeetings.db.dao.room.RoomTypeDao;
import org.apache.openmeetings.db.dao.server.ISessionManager;
import org.apache.openmeetings.db.dao.user.IUserService;
import org.apache.openmeetings.db.dao.user.OrganisationDao;
import org.apache.openmeetings.db.entity.room.Client;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.room.RoomOrganisation;
import org.apache.openmeetings.db.entity.room.RoomType;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminCommonUserForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.ConfirmCallListener;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

public class RoomForm extends AdminCommonUserForm<Room> {
	private static final long serialVersionUID = 1L;
	private final static List<Long> DROPDOWN_NUMBER_OF_PARTICIPANTS = Arrays.asList(2L, 4L, 6L, 8L, 10L, 12L, 14L, 16L, 20L, 25L, 32L, 50L,
			100L, 150L, 200L, 500L, 1000L);
	private final WebMarkupContainer roomList;
	private final TextField<String> confno;
	private final CheckBox sipEnabled;
	final WebMarkupContainer moderatorContainer;
	final WebMarkupContainer clientsContainer;
	final ListView<RoomModerator> moderators;
	final ListView<Client> clients;
	List<RoomModerator> moderatorsInRoom = null;
	List<Client> clientsInRoom = null;
	
	public RoomForm(String id, WebMarkupContainer roomList, final Room room) {
		super(id, new CompoundPropertyModel<Room>(room));
		this.roomList = roomList;
		setOutputMarkupId(true);
		RequiredTextField<String> name = new RequiredTextField<String>("name");
		name.setLabel(new Model<String>(WebSession.getString(193)));
		add(name);

		add(new DropDownChoice<Long>("numberOfPartizipants", //
				DROPDOWN_NUMBER_OF_PARTICIPANTS, //
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;
					public Object getDisplayValue(Long id) {
						return id;
					}
					public String getIdValue(Long id, int index) {
						return "" + id;
					}
				}));

		add(new DropDownChoice<RoomType>("roomtype", Application.getBean(RoomTypeDao.class).getAll(WebSession.getLanguage()),
				new ChoiceRenderer<RoomType>("label.value", "roomtypes_id")));

		add(new TextArea<String>("comment"));

		add(new CheckBox("appointment"));
		add(new CheckBox("ispublic"));

		List<Organisation> orgList = Application.getBean(OrganisationDao.class).get(0, Integer.MAX_VALUE);
		List<RoomOrganisation> orgRooms = new ArrayList<RoomOrganisation>(orgList.size());
		for (Organisation org : orgList) {
			orgRooms.add(new RoomOrganisation(org));
		}
		ListMultipleChoice<RoomOrganisation> orgChoiceList = new ListMultipleChoice<RoomOrganisation>(
				"roomOrganisations", orgRooms,
				new ChoiceRenderer<RoomOrganisation>("organisation.name",
						"organisation.organisation_id"));
		orgChoiceList.setMaxRows(6);
		add(orgChoiceList);

		add(new CheckBox("isDemoRoom"));
		TextField<Integer> demoTime = new TextField<Integer>("demoTime");
		demoTime.setLabel(new Model<String>(WebSession.getString(637)));
		add(demoTime);
		add(new CheckBox("allowUserQuestions"));
		add(new CheckBox("isAudioOnly"));
		add(new CheckBox("allowFontStyles"));
		add(new CheckBox("isClosed"));
		add(new TextField<String>("redirectURL"));
		add(new CheckBox("waitForRecording"));
		add(new CheckBox("allowRecording"));

		add(new CheckBox("hideTopBar"));
		add(new CheckBox("hideChat"));
		add(new CheckBox("hideActivitiesAndActions"));
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
		clientsContainer = new WebMarkupContainer("clientsContainer");
		clients = new ListView<Client>("clients", clientsInRoom){
			private static final long serialVersionUID = 8542589945574690054L;

			@Override
			protected void populateItem(final ListItem<Client> item) {
				Client client = item.getModelObject();
				item.add(new Label("clientId", "" + client.getId()))
					.add(new Label("clientLogin", "" + client.getUsername()))
					.add(new WebMarkupContainer("clientDelete").add(new AjaxEventBehavior("onclick"){

					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
					}
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						Client c = item.getModelObject();
						getBean(IUserService.class).kickUserByStreamId(getSid(), c.getStreamid()
								, c.getServer() == null ? 0 : c.getServer().getId());
						
						updatClients(target);
					}
				}));
			}
		};
		add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));
		
		// Moderators
		moderators =	new ListView<RoomModerator>("moderators", moderatorsInRoom) {
			private static final long serialVersionUID = -7935197812421549677L;

			@Override
			protected void populateItem(final ListItem<RoomModerator> item) {
				final RoomModerator moderator = item.getModelObject();
				item.add(new Label("isSuperModerator", "" + moderator.getIsSuperModerator()))
					.add(new Label("userId", "" + moderator.getUser().getUser_id()))
					.add(new Label("uName", "" + moderator.getUser().getFirstname() + " " + moderator.getUser().getLastname()))
					.add(new Label("email", ""+ moderator.getUser().getAdresses().getEmail()))
					.add(new WebMarkupContainer("delete").add(new AjaxEventBehavior("onclick"){
					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
					}
					
					@Override
					protected void onEvent(AjaxRequestTarget target) {
						Application.getBean(RoomModeratorsDao.class).removeRoomModeratorByUserId(moderator.getRoomModeratorsId());
						updateModerators(target);
					}
				})); 

				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};

		// FIXME: Room user moderator list
		CheckBox isModeratedRoom = new CheckBox("isModeratedRoom");
        add(isModeratedRoom.setOutputMarkupId(true));

		
		moderatorContainer = new WebMarkupContainer("moderatorContainer");
		
		add(moderatorContainer.add(moderators).setOutputMarkupId(true));
		
		confno = new TextField<String>("confno");
		add(confno);
		add(new TextField<String>("pin"));
		add(new TextField<String>("ownerId"));
		sipEnabled = new CheckBox("sipEnabled");
		add(sipEnabled.setOutputMarkupId(true).add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -1206667381066917517L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				sipEnabled.setModelObject(!sipEnabled.getModelObject());
				if (sipEnabled.getModelObject() && confno.getModelObject() == null){
					getBean(RoomDao.class).update(getModelObject(), getUserId());
				}
				updateView(target);				
			}
		}));
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}

	void updateModerators(AjaxRequestTarget target) {
		long roomId = (getModelObject().getRooms_id() != null ? getModelObject().getRooms_id() : 0);  
		RoomModeratorsDao moderatorsDao = getBean(RoomModeratorsDao.class);
		final List<RoomModerator> moderatorsInRoom = moderatorsDao.getRoomModeratorByRoomId(roomId);
		moderators.setDefaultModelObject(moderatorsInRoom);
		target.add(moderatorContainer);
	}
	
	void updatClients(AjaxRequestTarget target) {
		long roomId = (getModelObject().getRooms_id() != null ? getModelObject().getRooms_id() : 0);  
		final List<Client> clientsInRoom = Application.getBean(ISessionManager.class).getClientListByRoom(roomId);
		clients.setDefaultModelObject(clientsInRoom);
		target.add(clientsContainer);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(RoomDao.class).update(getModelObject(), getUserId());
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
		if (r.getRooms_id() != null) {
			r = getBean(RoomDao.class).get(r.getRooms_id());
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
		updateView(target);
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateView(AjaxRequestTarget target) {
		target.add(this);
		target.add(roomList);
		confno.setEnabled(sipEnabled.getModelObject());
		target.add(confno);
		updateModerators(target);
		updatClients(target);
		target.appendJavaScript("omRoomPanelInit();");
	}

	@Override
	public void submitView(AjaxRequestTarget target, List<User> usersToAdd) {
		// TODO Auto-generated method stub
		long roomId = getModelObject().getRooms_id();
		RoomModeratorsDao moderatorsDao = getBean(RoomModeratorsDao.class);
		List<RoomModerator> moderators = moderatorsDao.getRoomModeratorByRoomId(roomId);
		moderatorsInRoom = (moderatorsInRoom == null) ? new ArrayList<RoomModerator>() : moderatorsInRoom;
		for (User u : usersToAdd) {
			boolean found = false;
			for ( RoomModerator rm : moderators) {
				if (rm.getUser().getUser_id().equals(u.getUser_id())) {
					found = true;
					break;
				}
			}
			if (!found) {
				RoomModerator rModerator = new RoomModerator();
				rModerator.setUser(u);
				rModerator.setIsSuperModerator(false);
				rModerator.setStarttime(new Date());
				rModerator.setDeleted(false);
				rModerator.setRoomId(roomId);
				moderatorsInRoom.add(rModerator);
				moderatorsDao.addRoomModeratorByUserId(u, false, roomId);
			}
		}
		updateModerators(target);
	}
}
