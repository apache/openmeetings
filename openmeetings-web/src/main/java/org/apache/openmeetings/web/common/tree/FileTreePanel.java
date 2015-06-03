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

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

public abstract class FileTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	protected final IModel<FileItem> selectedFile = new CompoundPropertyModel<FileItem>((FileItem)null);
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((FlvRecording)null));
	protected FileItemTree<? extends FileItem> selected;
	protected RepeatingView treesView = new RepeatingView("tree");

	public FileTreePanel(String id) {
		super(id);
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
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (o instanceof FileItem) {
					delete((FileItem)o, target);
				}
			}
		};
		add(trashToolbar);
		trashToolbar.add(new WebMarkupContainer("create").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				addFolder.open(target);
			}
		}));
		trashToolbar.add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(trees); //FIXME add correct refresh
			}
		}));
		trashToolbar.add(new ConfirmableAjaxLink("trash", 713) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				delete(selectedFile.getObject(), target);
			}
		});
		
		add(trees.add(treesView).setOutputMarkupId(true));
		updateSizes();
		add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		sizes.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
			private static final long serialVersionUID = 1L;

			protected void onPostProcessTarget(AjaxRequestTarget target) {
				updateSizes();
			}
		});
		add(errorsDialog);
	}
	
	void delete(FileItem f, AjaxRequestTarget target) {
		long id = f.getId();
		if (id > 0) {
			if (f instanceof FlvRecording) {
				getBean(FlvRecordingDao.class).delete((FlvRecording)f);
			} else {
				getBean(FileExplorerItemDao.class).delete((FileExplorerItem)f);
			}
		}
		target.add(trees); //FIXME add correct refresh
	}
	
	public void createRecordingFolder(String name) {
		FlvRecording f = new FlvRecording();
		f.setFileName(name);
		f.setInsertedBy(getUserId());
		f.setInserted(new Date());
		f.setType(Type.Folder);;
		FlvRecording p = (FlvRecording)selectedFile.getObject();
		long parentId = p.getId();
		if (Type.Folder == p.getType()) {
			f.setParentItemId(parentId);
		}
		f.setOwnerId(p.getOwnerId());
		f.setOrganizationId(p.getOrganizationId());
		getBean(FlvRecordingDao.class).update(f);
	}
	
	public abstract void defineTrees();
	
	public abstract void update(AjaxRequestTarget target, FileItem f);

	public abstract void createFolder(String name);

	public abstract void updateSizes();
	
	@Override
	protected void onDetach() {
		selectedFile.detach();
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}
}
