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
import static org.apache.openmeetings.web.admin.AdminUserChoiceProvider.PAGE_SIZE;
import static org.apache.openmeetings.web.app.Application.kickUser;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CHANGE;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isRecordingsEnabled;
import static org.apache.wicket.validation.validator.StringValidator.maximumLength;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import org.apache.openmeetings.web.util.RestrictiveChoiceProvider;
import org.apache.openmeetings.web.util.RoomTypeDropDown;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.LambdaChoiceRenderer;
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

import de.agilecoders.wicket.core.markup.html.bootstrap.badge.BootstrapBadge;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.utilities.BackgroundColorBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public class RoomForm extends AdminBaseForm<Room> {
	private static final long serialVersionUID = 1L;
	private static final List<Long> DROPDOWN_NUMBER_OF_PARTICIPANTS = List.of(2L, 4L, 6L, 8L, 10L, 12L, 14L, 16L, 20L, 25L, 32L, 50L,
			100L, 150L, 200L, 500L, 1000L);
	private final WebMarkupContainer roomList;
	private final TextField<String> pin = new TextField<>("pin");
	private final WebMarkupContainer moderatorContainer = new WebMarkupContainer("moderatorContainer");
	private final WebMarkupContainer filesContainer = new WebMarkupContainer("filesContainer");
	private final WebMarkupContainer clientsContainer = new WebMarkupContainer("clientsContainer");
	private final ListView<Client> clients = new ListView<>("clients", new ArrayList<>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final ListItem<Client> item) {
			Client c = item.getModelObject();
			BootstrapAjaxLink<String> del = new BootstrapAjaxLink<>("clientDelete", Buttons.Type.Outline_Danger) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					kickUser(item.getModelObject());
					updateClients(target);
				}
			};
			del.setIconType(FontAwesome6IconType.xmark_s)
					.add(newOkCancelDangerConfirm(this, getString("833")));
			item.add(new Label("clientId", "" + c.getUserId()))
				.add(new Label("clientLogin", "" + c.getUser().getLogin()))
				.add(del);
		}
	};
	private IModel<User> moderator2add = Model.of((User)null);
	private IModel<Collection<BaseFileItem>> files2add = new CollectionModel<>(new ArrayList<>());
	private IModel<Long> wbIdx = Model.of(0L);

	@Inject
	private GroupDao groupDao;
	@Inject
	private UserDao userDao;
	@Inject
	private FileItemDao fileDao;
	@Inject
	private ClientManager cm;
	@Inject
	private RoomDao roomDao;

	public RoomForm(String id, WebMarkupContainer roomList, final Room room) {
		super(id, new CompoundPropertyModel<>(room == null ? newRoom() : room));
		this.roomList = roomList;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		RequiredTextField<String> name = new RequiredTextField<>("name");
		name.setLabel(new ResourceModel("165"));
		add(name);
		TextField<String> tag = new TextField<>("tag");
		tag.add(maximumLength(10));
		tag.setLabel(new ResourceModel("admin.group.form.tag"));
		add(tag);

		add(new DropDownChoice<>("capacity", //
				DROPDOWN_NUMBER_OF_PARTICIPANTS, //
				new LambdaChoiceRenderer<>(num -> num, String::valueOf)));

		add(new RoomTypeDropDown("type").setRequired(true).setLabel(new ResourceModel("45")));

		add(new TextArea<String>("comment"));

		boolean isGroupAdmin = hasGroupAdminLevel(getRights());
		add(new CheckBox("appointment").setEnabled(false));
		add(new CheckBox("ispublic").setEnabled(!isGroupAdmin));

		List<Group> orgList = isGroupAdmin
				? groupDao.adminGet(null, getUserId(), 0, Integer.MAX_VALUE, null)
				: groupDao.get(0, Integer.MAX_VALUE);
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
						.toList()
						);
			}

			@Override
			public RoomGroup fromId(String inId) {
				Long id = Long.valueOf(inId);

				if (orgList.stream().noneMatch(g -> g.getId().equals(id))) {
					return null; // seems to be hacked
				}
				Group g = groupDao.get(id);
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
		add(new CheckBox("waitRecording"));
		add(new CheckBox("allowRecording").setEnabled(isRecordingsEnabled()));
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
				response.addAll(userDao.get(term, false, page * PAGE_SIZE, PAGE_SIZE));
				response.setHasMore(PAGE_SIZE == response.getResults().size());
			}

			@Override
			public String getDisplayValue(User choice) {
				Address a = choice.getAddress();
				return String.format("\"%s\" <%s>", choice.getDisplayName(), a == null ? "" : a.getEmail());
			}
		});
		moderatorChoice.getSettings().setCloseOnSelect(true);
		add(moderatorChoice.add(AjaxFormComponentUpdatingBehavior.onUpdate(EVT_CHANGE, target -> {
			Room r = RoomForm.this.getModelObject();
			User u = moderator2add.getObject();
			boolean found = false;
			if (u != null) {
				if (r.getModerators() == null) {
					r.setModerators(new ArrayList<>());
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
		})).setOutputMarkupId(true));
		add(moderatorContainer.add(new ListView<RoomModerator>("moderators") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<RoomModerator> item) {
				RoomModerator moderator = item.getModelObject();
				Label name = new Label("uName", moderator.getUser().getDisplayName());
				BootstrapAjaxLink<String> del = new BootstrapAjaxLink<>("delete", Buttons.Type.Outline_Danger) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						RoomForm.this.getModelObject().getModerators().remove(item.getIndex());
						target.add(moderatorContainer);
					}
				};
				del.setIconType(FontAwesome6IconType.xmark_s)
						.add(newOkCancelDangerConfirm(this, getString("833")));
				item.add(new CheckBox("superModerator", new PropertyModel<>(moderator, "superModerator")))
					.add(new Label("userId", String.valueOf(moderator.getUser().getId())))
					.add(name)
					.add(new Label("email", moderator.getUser().getAddress().getEmail()))
					.add(del)
					.add(new BootstrapBadge("new", new ResourceModel("lbl.new"), BackgroundColorBehavior.Color.Warning).setVisible((moderator.getId() == null)));
			}
		}).setOutputMarkupId(true));

		add(new CheckBox("moderated"));
		add(new CheckBox("waitModerator"));

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
					response.addAll(fileDao.getAllRoomFiles(term, page * PAGE_SIZE, PAGE_SIZE, RoomForm.this.getModelObject().getId(), orgList));
					response.setHasMore(PAGE_SIZE == response.getResults().size());
				}

				@Override
				public Collection<BaseFileItem> toChoices(Collection<String> ids) {
					return fileDao.get(ids);
				}
			}).setLabel(new ResourceModel("245")))
			.add(new TextField<>("wbidx", wbIdx) {
				private static final long serialVersionUID = 1L;

				@Override
				protected String[] getInputTypes() {
					return new String[] {"number"};
				}
			})
			.add(new BootstrapAjaxButton("addFiles", new ResourceModel("1261"), filesForm, Buttons.Type.Outline_Primary) {
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
			}).setOutputMarkupId(true)
		);
		add(filesContainer.add(new ListView<RoomFile>("files") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<RoomFile> item) {
				final RoomFile rf = item.getModelObject();
				BootstrapAjaxLink<String> del = new BootstrapAjaxLink<>("delete", Buttons.Type.Outline_Danger) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						Room r = RoomForm.this.getModelObject();
						for (Iterator<RoomFile> iter = r.getFiles().iterator(); iter.hasNext();) {
							RoomFile curRf = iter.next();
							if (curRf.getFile().getId().equals(rf.getFile().getId())) {
								iter.remove();
								break;
							}
						}
						target.add(filesContainer);
					}
				};
				del.setIconType(FontAwesome6IconType.xmark_s)
						.add(newOkCancelDangerConfirm(this, getString("833")));
				item.add(new Label("name", new PropertyModel<>(rf.getFile(), "name")))
					.add(new Label("wbIdx", new PropertyModel<>(rf, "wbIdx")))
					.add(del);
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
		clients.setDefaultModelObject(cm.streamByRoom(roomId).toList());
		target.add(clientsContainer);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Room r = getModelObject();
		boolean newRoom = r.getId() == null;
		r = roomDao.update(r, getUserId());
		if (newRoom) {
			for (RoomModerator rm : r.getModerators()) {
				rm.setRoomId(r.getId());
			}
			for (RoomFile rf : r.getFiles()) {
				rf.setRoomId(r.getId());
			}
			roomDao.update(getModelObject(), getUserId());
		}
		setNewRecordVisible(false);
		updateView(target);
	}

	private static Room newRoom() {
		Room r = new Room();
		r.hide(RoomElement.MICROPHONE_STATUS);
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
			r = roomDao.get(r.getId());
		} else {
			r = newRoom();
		}
		setModelObject(r);
		updateView(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		roomDao.delete(getModelObject(), getUserId());
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
	}

	@Override
	protected void onDetach() {
		moderator2add.detach();
		files2add.detach();
		wbIdx.detach();
		super.onDetach();
	}
}
