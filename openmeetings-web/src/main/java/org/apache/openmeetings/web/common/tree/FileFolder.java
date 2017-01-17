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

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.markup.html.link.AjaxLink;

/* FIXME TODO need to be removed */
public class FileFolder extends Folder<FileItem> {
	private static final long serialVersionUID = 1L;
	private final static String CSS_CLASS_FILE = "file ";
	final FileTreePanel treePanel;
	final FileItemTree tree;

	public FileFolder(String id, FileItemTree tree, IModel<FileItem> model, FileTreePanel treePanel) {
		super(id, tree, model);
		this.treePanel = treePanel;
		this.tree = tree;
	}

	@Override
	protected Component newLabelComponent(String id, final IModel<FileItem> lm) {
		FileItem r = lm.getObject();
		return Type.Folder == r.getType() || r.getId() == null ? new FolderPanel(id, lm, treePanel) : new FileItemPanel(id, lm, treePanel);
	}

	@Override
	protected boolean isSelected() {
		FileItem f = getModelObject(), s = treePanel.getSelected();
		return (s.getId() == null && s.getId() == f.getId() && s.getHash().equals(f.getHash()))
				|| (s.getId() != null && s.getId().equals(f.getId()));
	}

	@Override
	protected boolean isClickable() {
		return true;
	}

	@Override
	protected void onClick(AjaxRequestTarget target) {
		FileItem r = getModelObject();
		treePanel.setSelected(r, target);
		if (Type.Folder == r.getType()) {
			if (tree.getState(r) == State.COLLAPSED) {
				super.onClick(target);
			}
		} else {
			treePanel.update(target, r);
		}
	}

	@Override
	protected MarkupContainer newLinkComponent(String id, IModel<FileItem> model) {
		return new AjaxLink<Void>(id) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return FileFolder.this.isClickable();
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				FileFolder.this.onClick(target);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getDynamicExtraParameters().add("return {'aa': 1};");
			}
		};
	}

	private String getItemStyle(FileItem f, boolean open) {
		StringBuilder style = new StringBuilder("big om-icon ");
		if (f.getId() == null) {
			style.append(CSS_CLASS_FILE).append(f.getHash().indexOf("my") > -1 ? "my" : "public");
		} else {
			if (!f.exists()) {
				style.append("broken ");
			}
			switch(f.getType()) {
				case Folder:
					style.append(CSS_CLASS_FILE).append(open ? "folder-open " : "folder ");
					break;
				case Image:
					style.append(CSS_CLASS_FILE).append("image ");
					break;
				case PollChart:
					style.append(CSS_CLASS_FILE).append("chart ");
					break;
				case WmlFile:
					style.append(CSS_CLASS_FILE).append("wml ");
					break;
				case Video:
				case Recording:
				{
					style.append("recording ");
					if (f instanceof Recording) {
						Status st = ((Recording)f).getStatus();
						if (Status.RECORDING == st || Status.CONVERTING == st) {
							style.append("processing");
						}
					}
				}
					break;
				case Presentation:
					style.append(CSS_CLASS_FILE).append("doc ");
					break;
				default:
					break;
			}
		}
		return style.toString();
	}

	@Override
	protected String getOtherStyleClass(FileItem r) {
		return getItemStyle(r, false);
	}

	@Override
	protected String getOpenStyleClass() {
		return getItemStyle(getModelObject(), true);
	}

	@Override
	protected String getClosedStyleClass() {
		return getItemStyle(getModelObject(), false);
	}

	@Override
	protected String getSelectedStyleClass() {
		return "ui-state-active";
	}

	@Override
	protected IModel<String> newLabelModel(IModel<FileItem> model) {
		return Model.of(model.getObject().getName());
	}
}
