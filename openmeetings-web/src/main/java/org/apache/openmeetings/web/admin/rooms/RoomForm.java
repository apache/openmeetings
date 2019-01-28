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

import static org.apache.openmeetings.db.util.AuthLevelUtil.hasGroupAdminLevel;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.admin.AdminUserChoiceProvider.PAGE_SIZE;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.Application.kickUser;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.basic.Client;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.room.Room.RoomElement;
import org.apache.openmeetings.db.entity.room.RoomFile;
import org.apache.openmeetings.db.entity.room.RoomGroup;
import org.apache.openmeetings.db.entity.room.RoomModerator;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.admin.AdminUserChoiceProvider;
import org.apache.openmeetings.web.app.ClientManager;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.util.RestrictiveChoiceProvider;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.CollectionModel;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.Select2Choice;
import org.wicketstuff.select2.Select2MultiChoice;

import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

public class RoomForm extends AdminBaseForm<Room> {
	private static final long serialVersionUID = 1L;
	private static final List<Long> DROPDOWN_NUMBER_OF_PARTICIPANTS = Arrays.asList(2L, 4L, 6L, 8L, 10L, 12L, 14L, 16L, 20L, 25L, 32L, 50L,
			100L, 150L, 200L, 500L, 1000L);
	private final WebMarkupContainer roomList;
	private final TextField<String> pin = new TextField<>("pin");
	private final WebMarkupContainer moderatorContainer = new WebMarkupContainer("moderatorContainer");
	private final WebMarkupContainer filesContainer = new WebMarkupContainer("filesContainer");
	private final WebMarkupContainer clientsContainer = new WebMarkupContainer("clientsContainer");
	private final ListView<Client> clients = new ListView<Client>("clients", new ArrayList<>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final ListItem<Client> item) {
			Client client = item.getModelObject();
			item.add(new Label("clientId", "" + client.getUserId()))
				.add(new Label("clientLogin", "" + client.getLogin()))
				.add(new ConfirmableAjaxBorder("clientDelete", getString("80"), getString("833")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						kickUser(item.getModelObject());
						updateClients(target);
					}
				});
		}
	};
	private IModel<User> moderator2add = Model.of((User)null);
	private IModel<Collection<BaseFileItem>> files2add = new CollectionModel<>(new ArrayList<BaseFileItem>());
	private IModel<Long> wbIdx = Model.of(0L);

	public RoomForm(String id, WebMarkupContainer roomList, final Room room) {
		super(id, new CompoundPropertyModel<>(room == null ? newRoom() : room));
		this.roomList = roomList;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		RequiredTextField<String> name = new RequiredTextField<>("name");
		name.setLabel(new Model<>(getString("165")));
		add(name);

		add(new DropDownChoice<>("capacity", //
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

		add(new RoomTypeDropDown("type").setRequired(true).setLabel(new ResourceModel("45")));

		add(new TextArea<String>("comment"));

		boolean isGroupAdmin = hasGroupAdminLevel(getRights());
		add(new CheckBox("appointment").setEnabled(false));
		add(new CheckBox("ispublic").setEnabled(!isGroupAdmin));

		List<Group> orgList = isGroupAdmin
				? getBean(GroupDao.class).adminGet(null, getUserId(), 0, Integer.MAX_VALUE, null)
				: getBean(GroupDao.class).get(0, Integer.MAX_VALUE);
		final List<RoomGroup> orgRooms = new ArrayList<>(orgList.size());
		for (Group org : orgList) {
			orgRooms.add(new RoomGroup(org, getModelObject()));
		}
		add(new Select2MultiChoice<>("groups", null, new RestrictiveChoiceProvider<RoomGroup>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(RoomGroup choice) {
				String name = choice.getGroup().getName();
				return name == null ? "" : name;
			}

			@Override
			public String toId(RoomGroup choice) {
				Long id = choice.getGroup().getId();
				return id == null ? null : "" + id;
			}

			@Override
			public void query(String term, int page, Response<RoomGroup> response) {
				response.addAll(orgRooms.stream()
						.filter(rg -> Strings.isEmpty(term) || rg.getGroup().getName().contains(term))
						.collect(Collectors.toList())
						);
			}

			@Override
			public RoomGroup fromId(String _id) {
				Long id = Long.valueOf(_id);

				if (!orgList.stream().filter(g -> g.getId().equals(id)).findFirst().isPresent()) {
					return null; // seems to be hacked
				}
				Group g = getBean(GroupDao.class).get(id);
				return new RoomGroup(g, RoomForm.this.getModelObject());
			}
		}).setLabel(new ResourceModel("828")).setRequired(isGroupAdmin));

		add(new CheckBox("isDemoRoom"));
		TextField<Integer> demoTime = new TextField<>("demoTime");
		demoTime.setLabel(new Model<>(getString("637")));
		add(demoTime);
		add(new CheckBox("allowUserQuestions"));
		add(new CheckBox("audioOnly"));
		add(new CheckBox("closed"));
		add(new TextField<String>("redirectURL"));
		add(new CheckBox("waitForRecording"));
		add(new CheckBox("allowRecording"));
		add(new CheckBox("chatModerated"));

		add(new Select2MultiChoice<>("hiddenElements", null, new ChoiceProvider<RoomElement>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getDisplayValue(RoomElement choice) {
				return choice.name();
			}

			@Override
			public String getIdValue(RoomElement choice) {
				return choice.name();
			}

			@Override
			public void query(String term, int page, Response<RoomElement> response) {
				for (RoomElement r : RoomElement.values()) {
					if (Strings.isEmpty(term) || r.name().contains(term)) {
						response.add(r);
					}
				}
			}

			@Override
			public Collection<RoomElement> toChoices(Collection<String> ids) {
				Collection<RoomElement> rights = new ArrayList<>(ids.size());
				for (String id : ids) {
					rights.add(RoomElement.valueOf(id));
				}
				return rights;
			}
		}));
		add(new CheckBox("chatOpened"));
		add(new CheckBox("filesOpened"));

		// Moderators
		final Select2Choice<User> moderatorChoice = new Select2Choice<>("moderator2add", moderator2add, new AdminUserChoiceProvider() {
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
		moderatorChoice.getSettings().setCloseOnSelect(true);
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
					name.add(AttributeModifier.append(ATTR_CLASS, "newItem"));
				}
				item.add(new CheckBox("superModerator", new PropertyModel<Boolean>(moderator, "superModerator")))
					.add(new Label("userId", "" + moderator.getUser().getId()))
					.add(name)
					.add(new Label("email", moderator.getUser().getAddress().getEmail()))
					.add(new ConfirmableAjaxBorder("delete", getString("80"), getString("833")) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onSubmit(AjaxRequestTarget target) {
							RoomForm.this.getModelObject().getModerators().remove(item.getIndex());
							target.add(moderatorContainer);
						}
					});
			}
		}).setOutputMarkupId(true));

		add(new CheckBox("moderated"));

		// Files
		Form<Void> filesForm = new Form<>("filesForm");
		add(filesForm.add(new Select2MultiChoice<>("files2add", files2add, new ChoiceProvider<BaseFileItem>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getDisplayValue(BaseFileItem f) {
					return f.getName();
				}

				@Override
				public String getIdValue(BaseFileItem f) {
					return "" + f.getId();
				}

				@Override
				public void query(String term, int page, Response<BaseFileItem> response) {
					response.addAll(getBean(FileItemDao.class).getAllRoomFiles(term, page * PAGE_SIZE, PAGE_SIZE, RoomForm.this.getModelObject().getId(), orgList));
					response.setHasMore(PAGE_SIZE == response.getResults().size());
				}

				@Override
				public Collection<BaseFileItem> toChoices(Collection<String> ids) {
					return getBean(FileItemDao.class).get(ids);
				}
			}).setLabel(new ResourceModel("245")))
			.add(new TextField<Long>("wbidx", wbIdx) {
				private static final long serialVersionUID = 1L;

				@Override
				protected String[] getInputTypes() {
					return new String[] {"number"};
				}
			})
			.add(new AjaxButton("addFiles") {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onSubmit(AjaxRequestTarget target) {
					Room r = RoomForm.this.getModelObject();
					if (r.getFiles() == null) {
						r.setFiles(new ArrayList<>());
					}
					for (BaseFileItem f : files2add.getObject()) {
						r.getFiles().add(new RoomFile(r.getId(), f, wbIdx.getObject()));
					}
					target.add(filesContainer, filesForm);
				}

				@Override
				protected String getIcon() {
					return JQueryIcon.PLUSTHICK;
				}
			}).setOutputMarkupId(true)
		);
		add(filesContainer.add(new ListView<RoomFile>("files") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<RoomFile> item) {
				final RoomFile rf = item.getModelObject();
				item.add(new Label("name", new PropertyModel<>(rf.getFile(), "name")))
					.add(new Label("wbIdx", new PropertyModel<>(rf, "wbIdx")))
					.add(new ConfirmableAjaxBorder("delete", getString("80"), getString("833")) {
						private static final long serialVersionUID = 1L;

						@Override
						protected void onSubmit(AjaxRequestTarget target) {
							Room r = RoomForm.this.getModelObject();
							for (Iterator<RoomFile> iter = r.getFiles().iterator(); iter.hasNext();) {
								RoomFile _rf = iter.next();
								if (_rf.getFile().getId().equals(rf.getFile().getId())) {
									iter.remove();
									break;
								}
							}
							target.add(filesContainer);
						}
					});
			}
		}).setOutputMarkupId(true));

		// Users in this Room
		add(clientsContainer.add(clients.setOutputMarkupId(true)).setOutputMarkupId(true));

		add(new TextField<String>("confno").setEnabled(false));
		add(pin);
		pin.setEnabled(getModelObject().isSipEnabled());
		add(new TextField<String>("ownerId").setEnabled(false));
		add(new AjaxCheckBox("sipEnabled") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				updateView(target);
			}
		}.setOutputMarkupId(true));
	}

	void updateClients(AjaxRequestTarget target) {
		long roomId = getModelObject().getId() != null ? getModelObject().getId() : 0;
		final List<Client> clientsInRoom = getBean(ClientManager.class).listByRoom(roomId);
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
			for (RoomFile rf : r.getFiles()) {
				rf.setRoomId(r.getId());
			}
			getBean(RoomDao.class).update(getModelObject(), getUserId());
		}
		setNewVisible(false);
		updateView(target);
	}

	private static Room newRoom() {
		Room r = new Room();
		r.hide(RoomElement.MicrophoneStatus);
		return r;
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		setModelObject(newRoom());
		updateView(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Room r = getModelObject();
		if (r.getId() != null) {
			r = getBean(RoomDao.class).get(r.getId());
		} else {
			r = newRoom();
		}
		setModelObject(r);
		updateView(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(RoomDao.class).delete(getModelObject(), getUserId());
		target.add(roomList);
		setModelObject(newRoom());
		updateView(target);
	}

	public void updateView(AjaxRequestTarget target) {
		moderator2add.setObject(null);
		files2add.getObject().clear();
		wbIdx.setObject(0L);
		target.add(this);
		target.add(roomList);
		target.add(pin.setEnabled(getModelObject().isSipEnabled()));
		updateClients(target);
		reinitJs(target);
	}

	@Override
	protected void onDetach() {
		moderator2add.detach();
		files2add.detach();
		wbIdx.detach();
		super.onDetach();
	}
}
