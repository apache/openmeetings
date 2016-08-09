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

import java.util.ArrayList;
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
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
	protected final IModel<FileItem> selectedFile = new CompoundPropertyModel<FileItem>((FileItem)null);
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((Recording)null));
	protected FileItemTree<? extends FileItem> selected;
	protected ListView<ITreeProvider<? extends FileItem>> treesView = new ListView<ITreeProvider<? extends FileItem>>("tree", new ArrayList<ITreeProvider<? extends FileItem>>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(ListItem<ITreeProvider<? extends FileItem>> item) {
			@SuppressWarnings({ "unchecked", "rawtypes" }) //TODO investigate this
			FileItemTree<? extends FileItem> fit = new FileItemTree("item", FileTreePanel.this, item.getModelObject());
			if (selected == null) {
				selected = fit;
			}
			item.add(fit);
		}
	};

	public FileTreePanel(String id) {
		super(id);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		defineTrees();
		selectedFile.getObject().setId(Long.MIN_VALUE);
		final AddFolderDialog addFolder = new AddFolderDialog("addFolder", Application.getString(712)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				createFolder(getModelObject());
				target.add(trees); //FIXME add correct refresh
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
				target.add(trees); //FIXME add correct refresh
			}
		}));
		trashToolbar.add(new ConfirmableAjaxBorder("trash", getString("80"), getString("713")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				FileItem f = selectedFile.getObject();
				if (f != null && f.getId() > 0) {
					super.onEvent(target);
				}
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				delete(selectedFile.getObject(), target);
			}
		});
		
		add(trees.add(treesView).setOutputMarkupId(true));
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
		handler.add(trees); //FIXME add correct refresh
	}
	
	public void createRecordingFolder(String name) {
		Recording f = new Recording();
		f.setName(name);
		f.setInsertedBy(getUserId());
		f.setInserted(new Date());
		f.setType(Type.Folder);;
		Recording p = (Recording)selectedFile.getObject();
		long parentId = p.getId();
		if (Type.Folder == p.getType()) {
			f.setParentId(parentId);
		}
		f.setOwnerId(p.getOwnerId());
		f.setGroupId(p.getGroupId());
		getBean(RecordingDao.class).update(f);
	}
	
	public abstract void defineTrees();
	
	public abstract void update(AjaxRequestTarget target, FileItem f);

	public abstract void createFolder(String name);

	public abstract void updateSizes();
	
	public FileItem getSelectedFile() {
		return selectedFile.getObject();
	}
	
	@Override
	protected void onDetach() {
		selectedFile.detach();
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}
}
