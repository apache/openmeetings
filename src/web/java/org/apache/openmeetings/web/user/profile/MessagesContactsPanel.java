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
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.openmeetings.db.dao.user.PrivateMessageFolderDao;
import org.apache.openmeetings.db.dao.user.PrivateMessagesDao;
import org.apache.openmeetings.db.dao.user.UserContactsDao;
import org.apache.openmeetings.db.entity.user.PrivateMessage;
import org.apache.openmeetings.db.entity.user.PrivateMessageFolder;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.entity.user.UserContact;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.util.ContactsHelper;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.web.util.ConfirmAjaxCallListener;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.plugins.fixedheadertable.FixedHeaderTableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessagesContactsPanel extends UserPanel {
	private static final long serialVersionUID = 8098087441571734957L;
	private final static long MOVE_CHOOSE = -1;
	private final static int SELECT_CHOOSE = 1252;
	private final static int SELECT_ALL = 1239;
	private final static int SELECT_NONE = 1240;
	private final static int SELECT_UNREAD = 1241;
	private final static int SELECT_READ = 1242;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final WebMarkupContainer folders = new WebMarkupContainer("folders");
	private final Label unread = new Label("unread", Model.of(0L));
	private final Label pendingContacts = new Label("pendingContacts", Model.of(0L));
	private final Label allContacts = new Label("allContacts", Model.of(0L));
	private final IModel<Long> selectedFolderModel = Model.of(INBOX_FOLDER_ID);
	private final IModel<List<? extends PrivateMessageFolder>> foldersModel;
	private final WebMarkupContainer inbox = new WebMarkupContainer("inbox");
	private final WebMarkupContainer sent = new WebMarkupContainer("sent");
	private final WebMarkupContainer trash = new WebMarkupContainer("trash");
	private final WebMarkupContainer selectedMessage = new WebMarkupContainer("selectedMessage");
	private final WebMarkupContainer buttons = new WebMarkupContainer("buttons");
	private final WebMarkupContainer contacts = new WebMarkupContainer("contacts");
	private final MessageDialog newMessage;
	private final DataViewContainer<PrivateMessage> dataContainer;
	private final Set<Long> selectedMessages = new HashSet<Long>();
	private final Set<Long> allMessages = new HashSet<Long>();
	private final Set<Long> readMessages = new HashSet<Long>();
	private final Set<Long> unreadMessages = new HashSet<Long>();
	private final Button toInboxBtn = new Button("toInboxBtn");
	private final Button deleteBtn = new Button("deleteBtn");
	private final Button readBtn = new Button("readBtn");
	private final Button unreadBtn = new Button("unreadBtn");
	private final FixedHeaderTableBehavior fixedTable = new FixedHeaderTableBehavior("#messagesTable", new Options("height", 100));
	private final DropDownChoice<Integer> selectDropDown = new DropDownChoice<Integer>(
		"msgSelect", Model.of(SELECT_CHOOSE)
		, Arrays.asList(SELECT_CHOOSE, SELECT_ALL, SELECT_NONE, SELECT_UNREAD, SELECT_READ)
		, new IChoiceRenderer<Integer>() {
			private static final long serialVersionUID = 1L;
	
			public Object getDisplayValue(Integer object) {
				return WebSession.getString(object);
			}
			
			public String getIdValue(Integer object, int index) {
				return "" + object;
			}
		});
	private static PrivateMessageFolder NOT_MOVE_FOLDER = new PrivateMessageFolder();
	static {
		NOT_MOVE_FOLDER.setPrivateMessageFolderId(MOVE_CHOOSE);
		NOT_MOVE_FOLDER.setFolderName(WebSession.getString(1243));
	}
	private final DropDownChoice<PrivateMessageFolder> moveDropDown = new DropDownChoice<PrivateMessageFolder>("msgMove", Model.of(NOT_MOVE_FOLDER)
		, Arrays.asList(NOT_MOVE_FOLDER)
		, new IChoiceRenderer<PrivateMessageFolder>() {
			private static final long serialVersionUID = 1L;

			public Object getDisplayValue(PrivateMessageFolder object) {
				return object.getFolderName();
			}

			public String getIdValue(PrivateMessageFolder object, int index) {
				return "" + object.getPrivateMessageFolderId();
			}
		});
	private WebMarkupContainer selectedFolder;
	
	private void setDefaultFolderClass() {
		inbox.add(AttributeAppender.replace("class", "email inbox clickable"));
		sent.add(AttributeAppender.replace("class", "email sent clickable"));
		trash.add(AttributeAppender.replace("class", "email trash clickable"));
	}
	
	private void selectFolder(WebMarkupContainer folder) {
		folder.add(AttributeAppender.append("class", "ui-widget-header ui-corner-all"));
	}
	
	private void setFolderClass(ListItem<PrivateMessageFolder> folder) {
		folder.add(AttributeAppender.replace("class", "email folder clickable"));
		if (folder.getModelObject().getPrivateMessageFolderId() == selectedFolderModel.getObject()) {
			selectFolder(folder);
		}
	}
	
	private void updateControls(AjaxRequestTarget target) {
		deleteBtn.setEnabled(!selectedMessages.isEmpty());
		readBtn.setEnabled(TRASH_FOLDER_ID != selectedFolderModel.getObject() && !selectedMessages.isEmpty());
		unreadBtn.setEnabled(TRASH_FOLDER_ID != selectedFolderModel.getObject() && !selectedMessages.isEmpty());
		toInboxBtn.setVisible(INBOX_FOLDER_ID != selectedFolderModel.getObject() && SENT_FOLDER_ID != selectedFolderModel.getObject() && !selectedMessages.isEmpty());
		target.add(buttons);
	}
	
	private String getEmail(User u) {
		return u == null || u.getAdresses() == null ? "" : u.getAdresses().getEmail();
	}
	
	private void selectMessage(long id, AjaxRequestTarget target) {
		PrivateMessage msg = getBean(PrivateMessagesDao.class).get(id);
		selectedMessage.addOrReplace(new Label("from", msg == null ? "" : getEmail(msg.getFrom())));
		selectedMessage.addOrReplace(new Label("to", msg == null ? "" : getEmail(msg.getTo())));
		selectedMessage.addOrReplace(new Label("subj", msg == null ? "" : msg.getSubject()));
		selectedMessage.addOrReplace(new Label("body", msg == null ? "" : msg.getMessage()).setEscapeModelStrings(false));
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
	
	private void selectFolder(WebMarkupContainer folder, long id, AjaxRequestTarget target) {
		selectedFolder = folder;
		selectedFolderModel.setObject(id);
		setDefaultFolderClass();
		selectFolder(folder);
		emptySelection(target);
		selectDropDown.setModelObject(SELECT_CHOOSE);
		moveDropDown.setModelObject(NOT_MOVE_FOLDER);
		deleteBtn.add(AttributeModifier.replace("value", WebSession.getString(TRASH_FOLDER_ID == id ? 1256 : 1245)));
		readBtn.setEnabled(false);
		unreadBtn.setEnabled(false);
		if (target != null) {
			updateTable(target);
			target.add(folders, unread, selectDropDown, moveDropDown);
			target.add(dataContainer.container, dataContainer.navigator);
			target.add(dataContainer.orderLinks);
		}
	}
	
	private void emptySelection(AjaxRequestTarget target) {
		selectedMessages.clear();
		selectMessage(-1, target);
		unread.setDefaultModelObject(getBean(PrivateMessagesDao.class).count(getUserId(), selectedFolderModel.getObject(), null));
	}
	
	private String getDisplayName(User u) {
		return new StringBuilder().append(u.getFirstname()).append(" ")
				.append(u.getLastname()).append(" ")
				.append("<").append(getEmail(u)).append(">")
				.toString();
	}
	
	private void updateMoveModel() {
		List<PrivateMessageFolder> list = new ArrayList<PrivateMessageFolder>();
		list.add(NOT_MOVE_FOLDER);
		list.addAll(foldersModel.getObject());
		moveDropDown.setChoices(list);
	}
	
	private void updateContacts(AjaxRequestTarget target) {
		pendingContacts.setDefaultModelObject(getBean(UserContactsDao.class).getContactRequestsByUserAndStatus(getUserId(), true).size());
		allContacts.setDefaultModelObject(getBean(UserContactsDao.class).getContactsByUserAndStatus(getUserId(), false).size());
		if (target != null) {
			target.add(contacts);
		}
	}
	
	@SuppressWarnings("unchecked")
	public MessagesContactsPanel(String id) {
		super(id);
		foldersModel = Model.ofList(getBean(PrivateMessageFolderDao.class).get(0, Integer.MAX_VALUE));
		updateMoveModel();
		add(newMessage = new MessageDialog("newMessage", new CompoundPropertyModel<PrivateMessage>(new PrivateMessage())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				if (send.equals(button)) {
					target.add(container);
				}
			}
		});
		
		final AddFolderDialog addFolder = new AddFolderDialog("addFolder") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				PrivateMessageFolderDao fDao = getBean(PrivateMessageFolderDao.class);
				fDao.addPrivateMessageFolder(getModelObject(), getUserId());
				foldersModel.setObject(fDao.get(0, Integer.MAX_VALUE));
				updateMoveModel();
				target.add(folders, moveDropDown);
			}
		};
		add(addFolder);
		add(new WebMarkupContainer("new").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				newMessage.reset(true).open(target);
			}
		}).add(new JQueryBehavior(".email.new", "button")));
		folders.add(inbox.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(inbox, INBOX_FOLDER_ID, target);
			}
		}));
		folders.add(sent.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(sent, SENT_FOLDER_ID, target);
			}
		}));
		folders.add(trash.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				selectFolder(trash, TRASH_FOLDER_ID, target);
			}
		}));
		folders.add(new WebMarkupContainer("newdir").add(new AjaxEventBehavior("click") {
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
				item.add(new WebMarkupContainer("delete").add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						PrivateMessageFolderDao fDao = getBean(PrivateMessageFolderDao.class);
						fDao.delete(item.getModelObject(), getUserId());
						foldersModel.setObject(fDao.get(0, Integer.MAX_VALUE));
						target.add(folders);
					}
					
					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getAjaxCallListeners().add(new ConfirmAjaxCallListener(WebSession.getString(713)));
					}
				}));
				item.add(new AjaxEventBehavior("click") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						selectFolder(item, item.getModelObject().getPrivateMessageFolderId(), target);
					}
				});
				setFolderClass(item);
			}
		}).setOutputMarkupId(true));
		
		SearchableDataProvider<PrivateMessage> sdp = new SearchableDataProvider<PrivateMessage>(PrivateMessagesDao.class) {
			private static final long serialVersionUID = 1L;

			@Override
			protected PrivateMessagesDao getDao() {
				return (PrivateMessagesDao)super.getDao();
			}
			
			@Override
			public Iterator<? extends PrivateMessage> iterator(long first, long count) {
				allMessages.clear();
				readMessages.clear();
				unreadMessages.clear();
				String sort = getSort() == null ? "" : "m." + getSort().getProperty();
				boolean isAsc = getSort() == null ? true : getSort().isAscending();
				return getDao().get(getUserId(), selectedFolderModel.getObject(), search, sort, isAsc, (int)first, (int)count).iterator();
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
				final long id = m.getId();
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
				item.add(new AjaxEventBehavior("click") {
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
				StringBuilder cssClass = new StringBuilder(Boolean.FALSE.equals(m.getIsRead()) ? "unread" : "");
				if (selectedMessages.contains(id)) {
					if (cssClass.length() > 0) {
						cssClass.append(" ");
					}
					cssClass.append("selected");
				}
				item.add(AttributeModifier.replace("class", cssClass.toString()));
			}
		};
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dv) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				emptySelection(target);
				target.add(container);
			}
		};
		dataContainer = new DataViewContainer<PrivateMessage>(container, dv, navigator);
		dataContainer.setLinks(new OmOrderByBorder<PrivateMessage>("orderById", "id", dataContainer)
				, new OmOrderByBorder<PrivateMessage>("orderByFrom", "from.lastname", dataContainer)
				, new OmOrderByBorder<PrivateMessage>("orderBySubject", "subject", dataContainer)
				, new OmOrderByBorder<PrivateMessage>("orderBySend", "inserted", dataContainer));
		add(dataContainer.orderLinks);
		add(navigator);
		
		add(unread.setOutputMarkupId(true));
		
		add(buttons.setOutputMarkupId(true));
		buttons.add(toInboxBtn.add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onEvent(AjaxRequestTarget target) {
				getBean(PrivateMessagesDao.class).moveMailsToFolder(selectedMessages, INBOX_FOLDER_ID);
				selectFolder(selectedFolder, selectedFolderModel.getObject(), target);
			}
		}));
		buttons.add(deleteBtn.add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;
	
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					if (TRASH_FOLDER_ID == selectedFolderModel.getObject()) {
						getBean(PrivateMessagesDao.class).delete(selectedMessages);
					} else {
						getBean(PrivateMessagesDao.class).moveMailsToFolder(selectedMessages, TRASH_FOLDER_ID);
					}
					emptySelection(target);
					target.add(container);
				}
			}));
		buttons.add(readBtn.add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					getBean(PrivateMessagesDao.class).updateReadStatus(selectedMessages, true);
					emptySelection(target);
					target.add(container, unread);
				}
			}));
		buttons.add(unreadBtn.add(new AjaxEventBehavior("click") {
				private static final long serialVersionUID = 1L;
				
				@Override
				protected void onEvent(AjaxRequestTarget target) {
					getBean(PrivateMessagesDao.class).updateReadStatus(selectedMessages, false);
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
				long folderId = moveDropDown.getModelObject().getPrivateMessageFolderId();
				if (folderId != MOVE_CHOOSE) {
					getBean(PrivateMessagesDao.class).moveMailsToFolder(selectedMessages, folderId);
				}
				selectFolder(selectedFolder, selectedFolderModel.getObject(), target);
			}
		}));
		
		selectMessage(-1, null);
		add(container.add(dv).setOutputMarkupId(true));
		//TODO add valid autoupdate add(new AjaxSelfUpdatingTimerBehavior(seconds(15)));
		add(selectedMessage.setOutputMarkupId(true));
		
		IDataProvider<UserContact> dp = new IDataProvider<UserContact>() {
			private static final long serialVersionUID = 1L;

			public void detach() {
			}

			public Iterator<? extends UserContact> iterator(long first, long count) {
				return getBean(UserContactsDao.class).get(getUserId(), (int)first, (int)count).iterator();
			}

			public long size() {
				return getBean(UserContactsDao.class).count(getUserId());
			}

			public IModel<UserContact> model(UserContact object) {
				return Model.of(object);
			}
		};
		final UserInfoDialog d = new UserInfoDialog("infoDialog", newMessage);
		final DataView<UserContact> dw = new DataView<UserContact>("users", dp) {
			private static final long serialVersionUID = 1L;

			private String getName(UserContact uc) {
				return uc.getOwner().getFirstname() + " " + uc.getOwner().getLastname(); //FIXME salutation
			}
			
			@Override
			protected void populateItem(Item<UserContact> item) {
				UserContact uc = item.getModelObject();
				final long contactId = uc.getUserContactId();
				final long userId = uc.getOwner().getUser_id();
				if (uc.getPending()) {
					item.add(AttributeModifier.append("class", "unread"));
				}
				item.add(new Label("name", getName(uc)));	
				item.add(new WebMarkupContainer("accept").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 7223188816617664993L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						ContactsHelper.acceptUserContact(contactId);
						updateContacts(target);
					}
				}).setVisible(uc.getPending()));
				item.add(new WebMarkupContainer("decline").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						getBean(UserContactsDao.class).deleteUserContact(contactId);
						updateContacts(target);
					}
				}).setVisible(uc.getPending()));
				item.add(new WebMarkupContainer("view").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						d.open(target, userId);
					}
				}));
				item.add(new WebMarkupContainer("message").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						newMessage.reset(true).open(target, userId);
					}
				}).setVisible(!uc.getPending()));
				item.add(new WebMarkupContainer("delete").add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						getBean(UserContactsDao.class).deleteUserContact(contactId);
						updateContacts(target);
					}
				}).setVisible(!uc.getPending()));
			}
		};
		updateContacts(null);
		add(d, contacts.add(dw, pendingContacts, allContacts).setOutputMarkupId(true));//TODO update
		
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

}
