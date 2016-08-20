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
package org.apache.openmeetings.web.common.tree;

import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Date;
import java.util.UUID;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;

public abstract class FileTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	private final IModel<FileItem> selected = new CompoundPropertyModel<FileItem>((FileItem)null);
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((Recording)null));
	final FileItemTree tree;

	public FileTreePanel(String id, Long roomId) {
		super(id);
		OmTreeProvider tp = new OmTreeProvider(roomId);
		setSelected(tp.getRoot(), null);
		add(tree = new FileItemTree("tree", this, tp));
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		final AddFolderDialog addFolder = new AddFolderDialog("addFolder", Application.getString(712)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				createFolder(target, getModelObject());
			}
		};
		add(addFolder);
		Droppable<FileItem> trashToolbar = new Droppable<FileItem>("trash-toolbar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("hoverClass", Options.asString("ui-state-hover trash-toolbar-hover"));
				behavior.setOption("accept", Options.asString(".recorditem, .fileitem"));
			}
			
			@Override
			public JQueryBehavior newWidgetBehavior(String selector) {
				return new DroppableBehavior(selector, this) {
					private static final long serialVersionUID = 1L;

					@Override
					protected JQueryAjaxBehavior newOnDropAjaxBehavior(IJQueryAjaxAware source) {
						return new OnDropAjaxBehavior(source) {
							private static final long serialVersionUID = 1L;

							@Override
							public CharSequence getCallbackFunctionBody(CallbackParameter... parameters) {
								String dialogId = UUID.randomUUID().toString();

								String statement = "var $drop = $(this);";
								statement += "$('body').append('<div id=" + dialogId + ">" + getString("713") + "</div>');";
								statement += "$( '#" + dialogId
										+ "' ).dialog({ title: '" + getString("80") + "', dialogClass: 'no-close', buttons: [";
								statement += "    { text: '" + getString("54") + "', click: function() { $drop.append(ui.draggable); $(this).dialog('close'); " + super.getCallbackFunctionBody(parameters) + " } },";
								statement += "    { text: '" + getString("25") + "', click: function() { $( this ).dialog('close'); } } ";
								statement += "],";
								statement += "close: function(event, ui) { $(this).dialog('destroy').remove(); }";
								statement += "});";

								return statement;
							}
						};
					}
				};
			}
			
			@Override
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (o instanceof FileItem) {
					delete((FileItem)o, target);
				}
			}
		};
		add(trashToolbar);
		trashToolbar.add(getUpload("upload"));
		trashToolbar.add(new WebMarkupContainer("create").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				addFolder.open(target);
			}
		}));
		trashToolbar.add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				update(target);
			}
		}));
		trashToolbar.add(new ConfirmableAjaxBorder("trash", getString("80"), getString("713")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				FileItem f = selected.getObject();
				if (f != null && f.getId() != null) {
					super.onEvent(target);
				}
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				delete(selected.getObject(), target);
			}
		});
		
		add(trees.add(tree).setOutputMarkupId(true));
		updateSizes();
		add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		add(errorsDialog);
	}
	
	protected String getContainment() {
		return ".file.item.drop.area";
	}
	
	protected Component getUpload(String id) {
		return new WebMarkupContainer(id).setVisible(false);
	}
	
	void delete(FileItem f, IPartialPageRequestHandler handler) {
		long id = f.getId();
		if (id > 0) {
			if (f instanceof Recording) {
				getBean(RecordingDao.class).delete((Recording)f);
			} else {
				getBean(FileExplorerItemDao.class).delete((FileExplorerItem)f);
			}
		}
		update(handler);
	}
	
	protected abstract void update(AjaxRequestTarget target, FileItem f);

	protected void createFolder(AjaxRequestTarget target, String name) {
		FileItem p = selected.getObject();
		boolean isRecording = p instanceof Recording;
		if (Type.Folder != p.getType()) {
			
		}
		FileItem f = isRecording ? new Recording() : new FileExplorerItem();
		f.setName(name);
		f.setInsertedBy(getUserId());
		f.setInserted(new Date());
		f.setType(Type.Folder);
		f.setOwnerId(p.getOwnerId());
		f.setParentId(Type.Folder == p.getType() ? p.getId() : null);
		if (isRecording) {
			Recording r = (Recording)f;
			r.setGroupId(((Recording)p).getGroupId());
			getBean(RecordingDao.class).update(r);
		} else {
			f.setRoomId(p.getRoomId());
			getBean(FileExplorerItemDao.class).update((FileExplorerItem)f);
		}
		update(target);
	}

	public abstract void updateSizes();
	
	public FileItem getSelected() {
		return selected.getObject();
	}

	public void update(IPartialPageRequestHandler handler) {
		updateSizes();
		handler.add(sizes, trees);
	}

	void updateNode(AjaxRequestTarget target, FileItem fi) {
		if (fi != null && target != null) {
			if (Type.Folder == fi.getType()) {
				tree.updateBranch(fi, target);
			} else {
				tree.updateNode(fi, target);
			}
		}
	}
	
	public void setSelected(FileItem fi, AjaxRequestTarget target) {
		FileItem _prev = selected.getObject();
		updateNode(target, _prev);
		selected.setObject(fi);
		updateNode(target, fi);
	}
	
	@Override
	protected void onDetach() {
		selected.detach();
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}
}
