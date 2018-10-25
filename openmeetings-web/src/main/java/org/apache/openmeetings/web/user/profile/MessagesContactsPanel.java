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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.db.entity.user.PrivateMessage.INBOX_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.SENT_FOLDER_ID;
import static org.apache.openmeetings.db.entity.user.PrivateMessage.TRASH_FOLDER_ID;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.util.CallbackFunctionHelper.addOnClick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageDao;
import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.dao.user.UserContactDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.user.MessageDialog;
import org.apache.openmeetings.web.user.rooms.RoomEnterBehavior;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes.EventPropagation;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.plugins.fixedheadertable.FixedHeaderTableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessagesContactsPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private static final Long MOVE_CHOOSE = Long.valueOf(-1);
	private static final String SELECT_CHOOSE = "1252";
	private static final String SELECT_ALL = "1239";
	private static final String SELECT_NONE = "1240";
	private static final String SELECT_UNREAD = "1241";
	private static final String SELECT_READ = "1242";
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final WebMarkupContainer folders = new WebMarkupContainer("folders");
	private final Label unread = new Label("unread", Model.of(0L));
	private final Label pendingContacts = new Label("pendingContacts", Model.of(0L));
	private final Label allContacts = new Label("allContacts", Model.of(0L));
	private final IModel<Long> selectedFolderModel = Model.of(INBOX_FOLDER_ID);
	private final IModel<List<PrivateMessageFolder>> foldersModel = new ListModel<>(null);
	private final WebMarkupContainer inbox = new WebMarkupContainer("inbox");
	private final WebMarkupContainer sent = new WebMarkupContainer("sent");
	private final WebMarkupContainer trash = new WebMarkupContainer("trash");
	private final WebMarkupContainer selectedMessage = new WebMarkupContainer("selectedMessage");
	private final WebMarkupContainer roomContainer = new WebMarkupContainer("roomContainer");
	private final WebMarkupContainer buttons = new WebMarkupContainer("buttons");
	private final WebMarkupContainer contacts = new WebMarkupContainer("contacts");
	private final DataViewContainer<PrivateMessage> dataContainer;
	private final Set<Long> selectedMessages = new HashSet<>();
	private Long lastSelected = null;
	private final Set<Long> allMessages = new HashSet<>();
	private final Set<Long> readMessages = new HashSet<>();
	private final Set<Long> unreadMessages = new HashSet<>();
	private final Button toInboxBtn = new Button("toInboxBtn");
	private final Button deleteBtn = new Button("deleteBtn");
	private final Button replyBtn = new Button("replyBtn");
	private final Button readBtn = new Button("readBtn");
	private final Button unreadBtn = new Button("unreadBtn");
	private final FixedHeaderTableBehavior fixedTable = new FixedHeaderTableBehavior("#messagesTable", new Options("height", 100));
	private final DropDownChoice<String> selectDropDown = new DropDownChoice<>(
		"msgSelect", Model.of(SELECT_CHOOSE)
		, Arrays.asList(SELECT_CHOOSE, SELECT_ALL, SELECT_NONE, SELECT_UNREAD, SELECT_READ)
		, new ChoiceRenderer<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(String object) {
				return Application.getString(object);
			}

			@Override
			public String getIdValue(String object, int index) {
				return object;
			}
		});
	private PrivateMessageFolder NOT_MOVE_FOLDER = new PrivateMessageFolder();
	private final DropDownChoice<PrivateMessageFolder> moveDropDown = new DropDownChoice<>("msgMove", Model.of(NOT_MOVE_FOLDER)
		, Arrays.asList(NOT_MOVE_FOLDER)
		, new ChoiceRenderer<PrivateMessageFolder>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(PrivateMessageFolder object) {
				return object.getFolderName();
			}

			@Override
			public String getIdValue(PrivateMessageFolder object, int index) {
				return "" + object.getId();
			}
		});
	private WebMarkupContainer selectedFolder;
	@SpringBean
	private PrivateMessageDao msgDao;
	@SpringBean
	private PrivateMessageFolderDao folderDao;
	@SpringBean
	private UserContactDao contactDao;
	@SpringBean
	private AppointmentDao apptDao;

	public MessagesContactsPanel(String id) {
		super(id);
		NOT_MOVE_FOLDER.setId(MOVE_CHOOSE);
		NOT_MOVE_FOLDER.setFolderName(Application.getString("1243"));
		foldersModel.setObject(folderDao.get(0, Integer.MAX_VALUE));
		updateMoveModel();

		final NameDialog addFolder = new NameDialog("addFolder") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, DialogButton btn) {
				super.onSubmit(target, btn);
				folderDao.addPrivateMessageFolder(getModelObject(), getUserId());
				foldersModel.setObject(folderDao.get(0, Integer.MAX_VALUE));
				updateMoveModel();
				target.add(folders, moveDropDown);
			}
		};
		add(addFolder);
		add(new JQueryBehavior(".email.new", "button"));
		folders.add(inbox.add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(inbox, INBOX_FOLDER_ID, target);
			}
		}));
		folders.add(sent.add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(sent, SENT_FOLDER_ID, target);
			}
		}));
		folders.add(trash.add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(trash, TRASH_FOLDER_ID, target);
			}
		}));
		folders.add(new WebMarkupContainer("newdir").add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				addFolder.open(target);
			}
		}).add(new JQueryBehavior(".email.newdir", "button")));
		add(folders.add(new ListView<PrivateMessageFolder>("folder", foldersModel) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<PrivateMessageFolder> item) {
				item.add(new Label("name", item.getModelObject().getFolderName()));
				item.add(new ConfirmableAjaxBorder("delete", getString("80"), getString("833")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						attributes.setEventPropagation(EventPropagation.STOP_IMMEDIATE);
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						folderDao.delete(item.getModelObject(), getUserId());
						foldersModel.setObject(folderDao.get(0, Integer.MAX_VALUE));
						updateMoveModel();
						target.add(folders, moveDropDown);
					}
				});
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						selectFolder(item, item.getModelObject().getId(), target);
					}
				});
				setFolderClass(item);
			}
		}).setOutputMarkupId(true));

		SearchableDataProvider<PrivateMessage> sdp = new SearchableDataProvider<PrivateMessage>(PrivateMessageDao.class) {
			private static final long serialVersionUID = 1L;

			@Override
			protected PrivateMessageDao getDao() {
				return (PrivateMessageDao)super.getDao();
			}

			@Override
			public Iterator<? extends PrivateMessage> iterator(long first, long count) {
				allMessages.clear();
				readMessages.clear();
				unreadMessages.clear();
				String sort = getSort() == null ? "" : "m." + getSort().getProperty();
				boolean isAsc = getSort() == null ? true : getSort().isAscending();
				return getDao().get(getUserId(), selectedFolderModel.getObject(), search, sort, isAsc, first, count).iterator();
			}

			@Override
			public long size() {
				return getDao().count(getUserId(), selectedFolderModel.getObject(), search);
			}
		};
		final SearchableDataView<PrivateMessage> dv = new SearchableDataView<PrivateMessage>("messages", sdp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<PrivateMessage> item) {
				PrivateMessage m = item.getModelObject();
				final Long id = m.getId();
				allMessages.add(id);
				if (m.getIsRead()) {
					readMessages.add(id);
				} else {
					unreadMessages.add(id);
				}
				item.add(new Label("id", id));
				item.add(new Label("from", getDisplayName(m.getFrom())));
				item.add(new Label("subject", m.getSubject()));
				item.add(new Label("send", getDateFormat().format(m.getInserted())));
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						long selected = id;
						if (selectedMessages.contains(id)) {
							selectedMessages.remove(id);
							selected = selectedMessages.isEmpty() ? -1 : selectedMessages.iterator().next();
						} else {
							selectedMessages.add(id);
						}
						selectMessage(selected, target);
						target.add(container);
					}
				});
				StringBuilder cssClass = new StringBuilder(m.getIsRead() ? "" : "unread");
				if (selectedMessages.contains(id)) {
					if (cssClass.length() > 0) {
						cssClass.append(" ");
					}
					cssClass.append("ui-state-active");
				}
				item.add(AttributeModifier.replace(ATTR_CLASS, cssClass.toString()));
			}
		};
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dv) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				emptySelection(target);
				target.add(container);
			}
		};
		dataContainer = new DataViewContainer<>(container, dv, navigator);
		dataContainer.addLink(new OmOrderByBorder<>("orderById", "id", dataContainer))
			.addLink(new OmOrderByBorder<>("orderByFrom", "from.lastname", dataContainer))
			.addLink(new OmOrderByBorder<>("orderBySubject", "subject", dataContainer))
			.addLink(new OmOrderByBorder<>("orderBySend", "inserted", dataContainer));
		add(dataContainer.getLinks());
		add(navigator);

		add(unread.setOutputMarkupId(true));

		add(buttons.setOutputMarkupId(true));
		buttons.add(toInboxBtn.add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					msgDao.moveMailsToFolder(selectedMessages, INBOX_FOLDER_ID);
					selectFolder(selectedFolder, selectedFolderModel.getObject(), target);
				}
			}));
		buttons.add(replyBtn.add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					PrivateMessage opm = msgDao.get(lastSelected);
					if (opm != null) {
						MessageDialog newDlg = getMainPanel().getMessageDialog();
						PrivateMessage pm = newDlg.reset(true).getModelObject();
						pm.setTo(opm.getFrom());
						pm.setSubject(String.format("%s %s", getString("messages.subject.re"), opm.getSubject()));
						pm.setMessage(String.format("<br/><blockquote class=\"quote\">%s</blockquote>", opm.getMessage()));
						newDlg.open(target);
					}
				}
			}));
		buttons.add(deleteBtn.add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					if (TRASH_FOLDER_ID.equals(selectedFolderModel.getObject())) {
						msgDao.delete(selectedMessages);
					} else {
						msgDao.moveMailsToFolder(selectedMessages, TRASH_FOLDER_ID);
					}
					emptySelection(target);
					target.add(container);
				}
			}));
		buttons.add(readBtn.add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					msgDao.updateReadStatus(selectedMessages, true);
					emptySelection(target);
					target.add(container, unread);
				}
			}));
		buttons.add(unreadBtn.add(new AjaxEventBehavior(EVT_CLICK) {
				private static final long serialVersionUID = 1L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					msgDao.updateReadStatus(selectedMessages, false);
					emptySelection(target);
					target.add(container);
				}
			}));
		buttons.add(selectDropDown.setOutputMarkupId(true).add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				switch (selectDropDown.getModelObject()) {
					case SELECT_CHOOSE:
						break;
					case SELECT_ALL:
						selectedMessages.clear();
						selectedMessages.addAll(allMessages);
						break;
					case SELECT_NONE:
						selectedMessages.clear();
						break;
					case SELECT_UNREAD:
						selectedMessages.clear();
						selectedMessages.addAll(unreadMessages);
						break;
					case SELECT_READ:
						selectedMessages.clear();
						selectedMessages.addAll(readMessages);
						break;
				}
				target.add(container);
			}
		}));
		buttons.add(moveDropDown.setOutputMarkupId(true).add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				Long folderId = moveDropDown.getModelObject().getId();
				if (!MOVE_CHOOSE.equals(folderId)) {
					msgDao.moveMailsToFolder(selectedMessages, folderId);
				}
				selectFolder(selectedFolder, selectedFolderModel.getObject(), target);
			}
		}));

		selectMessage(-1, null);
		add(container.add(dv).setOutputMarkupId(true));
		add(selectedMessage.add(roomContainer.setVisible(false)).setOutputMarkupId(true));

		IDataProvider<UserContact> dp = new IDataProvider<UserContact>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Iterator<? extends UserContact> iterator(long first, long count) {
				return contactDao.get(getUserId(), first, count).iterator();
			}

			@Override
			public long size() {
				return contactDao.count(getUserId());
			}

			@Override
			public IModel<UserContact> model(UserContact object) {
				return Model.of(object);
			}
		};
		final DataView<UserContact> dw = new DataView<UserContact>("users", dp) {
			private static final long serialVersionUID = 1L;

			private String getName(UserContact uc) {
				return uc.getOwner().getFirstname() + " " + uc.getOwner().getLastname();
			}

			@Override
			protected void populateItem(Item<UserContact> item) {
				UserContact uc = item.getModelObject();
				final Long contactId = uc.getId();
				final Long userId = uc.getOwner().getId();
				if (uc.isPending()) {
					item.add(AttributeModifier.append(ATTR_CLASS, "unread"));
				}
				item.add(new Label("name", getName(uc)));
				item.add(new WebMarkupContainer("accept").add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						ContactsHelper.acceptUserContact(contactId);
						updateContacts(target);
					}
				}).setVisible(uc.isPending()));
				item.add(new WebMarkupContainer("decline").add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						contactDao.delete(contactId);
						updateContacts(target);
					}
				}).setVisible(uc.isPending()));
				item.add(new WebMarkupContainer("view").add(addOnClick(String.format("showUserInfo(%s);", userId))));
				item.add(new WebMarkupContainer("message").add(addOnClick(String.format("privateMessage(%s);", userId))).setVisible(!uc.isPending()));
				item.add(new ConfirmableAjaxBorder("delete", getString("80"), getString("833")) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						attributes.setEventPropagation(EventPropagation.STOP_IMMEDIATE);
					}

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						contactDao.delete(contactId);
						updateContacts(target);
					}
				}.setVisible(!uc.isPending()));
			}
		};
		updateContacts(null);
		add(contacts.add(dw, pendingContacts, allContacts).setOutputMarkupId(true));

		//hack to add FixedHeaderTable after Tabs.
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void respond(AjaxRequestTarget target) {
				selectFolder(inbox, INBOX_FOLDER_ID, target);
				selectMessage(-1, target);
			}

			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(OnDomReadyHeaderItem.forScript(getCallbackScript()));
			}
		});
	}

	private void setDefaultFolderClass() {
		inbox.add(AttributeModifier.replace(ATTR_CLASS, "email inbox clickable"));
		sent.add(AttributeModifier.replace(ATTR_CLASS, "email sent clickable"));
		trash.add(AttributeModifier.replace(ATTR_CLASS, "email trash clickable"));
	}

	private static void selectFolder(WebMarkupContainer folder) {
		folder.add(AttributeModifier.append(ATTR_CLASS, "ui-widget-header ui-corner-all"));
	}

	private void setFolderClass(ListItem<PrivateMessageFolder> folder) {
		folder.add(AttributeModifier.replace(ATTR_CLASS, "email folder clickable"));
		if (folder.getModelObject().getId().equals(selectedFolderModel.getObject())) {
			selectFolder(folder);
		}
	}

	private void updateControls(AjaxRequestTarget target) {
		deleteBtn.setEnabled(!selectedMessages.isEmpty());
		Long selFldr = selectedFolderModel.getObject();
		readBtn.setEnabled(!TRASH_FOLDER_ID.equals(selFldr) && !selectedMessages.isEmpty());
		unreadBtn.setEnabled(!TRASH_FOLDER_ID.equals(selFldr) && !selectedMessages.isEmpty());
		toInboxBtn.setVisible(!INBOX_FOLDER_ID.equals(selFldr) && !SENT_FOLDER_ID.equals(selFldr) && !selectedMessages.isEmpty());
		replyBtn.setEnabled(lastSelected != null);
		target.add(buttons);
	}

	private static String getEmail(User u) {
		return u == null || u.getAddress() == null ? "" : u.getAddress().getEmail();
	}

	private void selectMessage(long id, AjaxRequestTarget target) {
		PrivateMessage msg = msgDao.get(id);
		selectedMessage.addOrReplace(new Label("from", msg == null ? "" : getEmail(msg.getFrom())));
		selectedMessage.addOrReplace(new Label("to", msg == null ? "" : getEmail(msg.getTo())));
		selectedMessage.addOrReplace(new Label("subj", msg == null ? "" : msg.getSubject()));
		selectedMessage.addOrReplace(new Label("body", msg == null ? "" : msg.getMessage()).setEscapeModelStrings(false));
		if (msg == null) {
			lastSelected = null;
		} else {
			lastSelected = id;
			Room r = msg.getRoom();
			if (r != null) {
				Appointment a = apptDao.getByRoom(r.getId());
				roomContainer.addOrReplace(new Label("start", a == null ? "" : getDateFormat().format(a.getStart())));
				roomContainer.addOrReplace(new Label("end", a == null ? "" : getDateFormat().format(a.getEnd())));
				roomContainer.addOrReplace(new Button("enter").add(new RoomEnterBehavior(r.getId())));
			}
			roomContainer.setVisible(r != null);
		}
		if (target != null) {
			target.add(selectedMessage);
			updateControls(target);
		}
	}

	void updateTable(AjaxRequestTarget target) {
		container.add(fixedTable);
		if (target != null) {
			target.add(container);
		}
	}

	private void selectFolder(WebMarkupContainer folder, Long id, AjaxRequestTarget target) {
		selectedFolder = folder;
		selectedFolderModel.setObject(id);
		setDefaultFolderClass();
		selectFolder(folder);
		emptySelection(target);
		selectDropDown.setModelObject(SELECT_CHOOSE);
		moveDropDown.setModelObject(NOT_MOVE_FOLDER);
		deleteBtn.add(AttributeModifier.replace("value", Application.getString(TRASH_FOLDER_ID.equals(id) ? "1256" : "80")));
		readBtn.setEnabled(false);
		unreadBtn.setEnabled(false);
		if (target != null) {
			updateTable(target);
			target.add(folders, unread, selectDropDown, moveDropDown);
			target.add(dataContainer.getContainer(), dataContainer.getNavigator());
			target.add(dataContainer.getLinks());
		}
	}

	private void emptySelection(AjaxRequestTarget target) {
		selectedMessages.clear();
		selectMessage(-1, target);
		unread.setDefaultModelObject(msgDao.count(getUserId(), selectedFolderModel.getObject(), null));
		if (target != null) {
			target.add(unread);
		}
	}

	private static String getDisplayName(User u) {
		return new StringBuilder().append(u.getFirstname()).append(" ")
				.append(u.getLastname()).append(" ")
				.append("<").append(getEmail(u)).append(">")
				.toString();
	}

	private void updateMoveModel() {
		List<PrivateMessageFolder> list = new ArrayList<>();
		list.add(NOT_MOVE_FOLDER);
		list.addAll(foldersModel.getObject());
		moveDropDown.setChoices(list);
	}

	private void updateContacts(AjaxRequestTarget target) {
		pendingContacts.setDefaultModelObject(contactDao.getContactRequestsByUserAndStatus(getUserId(), true).size());
		allContacts.setDefaultModelObject(contactDao.getContactsByUserAndStatus(getUserId(), false).size());
		if (target != null) {
			target.add(contacts);
		}
	}

	@Override
	public void onNewMessageClose(IPartialPageRequestHandler handler) {
		handler.add(container);
	}

	@Override
	protected void onDetach() {
		foldersModel.detach();
		selectedFolderModel.detach();
		super.onDetach();
	}
}
