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

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getDateFormat;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.wicket.util.time.Duration.seconds;

import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.data.user.dao.PrivateMessageFolderDao;
import org.apache.openmeetings.data.user.dao.PrivateMessagesDao;
import org.apache.openmeetings.persistence.beans.user.PrivateMessage;
import org.apache.openmeetings.persistence.beans.user.PrivateMessageFolder;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import ro.fortsoft.wicket.dashboard.web.util.ConfirmAjaxCallListener;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
//TODO not released yet import com.googlecode.wicket.jquery.ui.plugins.fixedheadertable.FixedHeaderTableBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class MessagesContactsPanel extends UserPanel {
	private static final long serialVersionUID = 8098087441571734957L;
	private final WebMarkupContainer container = new WebMarkupContainer("container");
	private final WebMarkupContainer folders = new WebMarkupContainer("folders");
	private final IModel<Long> unreadModel = Model.of(0L);
	private final static long INBOX_FOLDER_ID = -1;
	private final static long SENT_FOLDER_ID = -2;
	private final static long TRASH_FOLDER_ID = -3;
	private final IModel<Long> selectedModel = Model.of(INBOX_FOLDER_ID);
	private final IModel<List<? extends PrivateMessageFolder>> foldersModel;
	private final WebMarkupContainer inbox = new WebMarkupContainer("inbox");
	private final WebMarkupContainer sent = new WebMarkupContainer("sent");
	private final WebMarkupContainer trash = new WebMarkupContainer("trash");
	private final MessageDialog newMessage;
	
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
		if (folder.getModelObject().getPrivateMessageFolderId() == selectedModel.getObject()) {
			selectFolder(folder);
		}
	}
	
	private void selectFolder(WebMarkupContainer folder, long id, AjaxRequestTarget target) {
		selectedModel.setObject(id);
		setDefaultFolderClass();
		selectFolder(folder);
		//TODO not released yet container.add(new FixedHeaderTableBehavior("#messagesTable", new Options("height", 100)));
		if (target != null) {
			target.add(folders, container);
		}
	}
	
	private String getDisplayName(User u) {
		return new StringBuilder().append(u.getFirstname()).append(" ")
				.append(u.getLastname()).append(" ")
				.append("<").append(u.getAdresses().getEmail()).append(">")
				.toString();
	}
	
	@SuppressWarnings("unchecked")
	public MessagesContactsPanel(String id) {
		super(id);
		foldersModel = Model.ofList(getBean(PrivateMessageFolderDao.class).get(0, Integer.MAX_VALUE));
		newMessage = new MessageDialog("newMessage", new CompoundPropertyModel<PrivateMessage>(new PrivateMessage())) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClose(AjaxRequestTarget target, DialogButton button) {
				if (button.equals(send)) {
					target.add(container);
				}
			}
		};
		
		final AddFolderDialog addFolder = new AddFolderDialog("addFolder") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				PrivateMessageFolderDao fDao = getBean(PrivateMessageFolderDao.class);
				fDao.addPrivateMessageFolder(getModelObject(), getUserId());
				foldersModel.setObject(fDao.get(0, Integer.MAX_VALUE));
				target.add(folders);
			}
		};
		add(addFolder);
		add(new WebMarkupContainer("new").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				newMessage.open(target);
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
		unreadModel.setObject(getBean(PrivateMessagesDao.class).count(getUserId(), null, false, false));
		
		SearchableDataProvider<PrivateMessage> sdp = new SearchableDataProvider<PrivateMessage>(PrivateMessagesDao.class) {
			private static final long serialVersionUID = 1L;

			@Override
			protected PrivateMessagesDao getDao() {
				return (PrivateMessagesDao)super.getDao();
			}
			
			@Override
			public Iterator<? extends PrivateMessage> iterator(long first, long count) {
				//FIXME need to be refactored + sort + search
				long folder = selectedModel.getObject();
				if (INBOX_FOLDER_ID == folder) {
					return getDao().getPrivateMessagesByUser(getUserId(), "", "", (int)first, true, 0L, (int)count).iterator();
				} else if (SENT_FOLDER_ID == folder) {
					return getDao().getSendPrivateMessagesByUser(getUserId(), "", "", (int)first, true, 0L, (int)count).iterator();
				} else if (TRASH_FOLDER_ID == folder) {
					return getDao().getTrashPrivateMessagesByUser(getUserId(), "", "", (int)first, true, (int)count).iterator();
				} else {
					return getDao().getPrivateMessagesByUser(getUserId(), "", "", (int)first, true, folder, (int)count).iterator();
				}
			}
			
			@Override
			public long size() {
				//FIXME need to be refactored + sort + search
				long folder = selectedModel.getObject();
				if (INBOX_FOLDER_ID == folder) {
					return getDao().countPrivateMessagesByUser(getUserId(), "", 0L);
				} else if (SENT_FOLDER_ID == folder) {
					return getDao().countSendPrivateMessagesByUser(getUserId(), "", 0L);
				} else if (TRASH_FOLDER_ID == folder) {
					return getDao().countTrashPrivateMessagesByUser(getUserId(), "");
				} else {
					return getDao().countPrivateMessagesByUser(getUserId(), "", folder);
				}
			}
		};
		final SearchableDataView<PrivateMessage> dv = new SearchableDataView<PrivateMessage>("messages", sdp) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<PrivateMessage> item) {
				PrivateMessage m = item.getModelObject();
				item.add(new Label("id", m.getPrivateMessageId()));
				item.add(new Label("from", getDisplayName(m.getFrom())));
				item.add(new Label("subject", m.getSubject()));
				item.add(new Label("send", getDateFormat().format(m.getInserted())));
			}
		};
		DataViewContainer<PrivateMessage> dataContainer = new DataViewContainer<PrivateMessage>(container, dv);
		dataContainer.setLinks(new OrderByBorder<PrivateMessage>("orderById", "privateMessageId", dataContainer)
				, new OrderByBorder<PrivateMessage>("orderByFrom", "from.lastname", dataContainer)
				, new OrderByBorder<PrivateMessage>("orderBySubject", "subject", dataContainer)
				, new OrderByBorder<PrivateMessage>("orderBySend", "inserted", dataContainer));
		container.add(dataContainer.orderLinks);
		container.add(new PagedEntityListPanel("navigator", dv) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(container);
			}
		});
		
		selectFolder(inbox, INBOX_FOLDER_ID, null);
		container.add(new Label("unread", unreadModel));
		add(new WebMarkupContainer("pendingContacts"));//FIXME
		add(new WebMarkupContainer("na1"));//FIXME
		
		add(container.add(dv).setOutputMarkupId(true));
		container.add(new AjaxSelfUpdatingTimerBehavior(seconds(15)));
		add(newMessage);
	}
}
