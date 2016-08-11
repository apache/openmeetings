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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FileItemTree extends DefaultNestedTree<FileItem> {
	private static final long serialVersionUID = 1L;
	private final FileTreePanel treePanel;

	public FileItemTree(String id, FileTreePanel treePanel, ITreeProvider<FileItem> tp) {
		super(id, tp);
		this.treePanel = treePanel;
		setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
	}
	
	@Override
	protected Component newContentComponent(String id, IModel<FileItem> node) {
		return new Folder<FileItem>(id, this, node) {
			private static final long serialVersionUID = 1L;

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
					if (getState(r) == State.COLLAPSED) {
						super.onClick(target);
					}
				} else {
					treePanel.update(target, r);
				}
			}
			
			private String getItemStyle(FileItem f, String def) {
				String style;
				if (f.getId() == null) {
					style = f.getHash().indexOf("my") > -1 ? "my file om-icon" : "public file om-icon";
				} else {
					String _style, addStyle = f.exists() ? "" : "broken";
					switch(f.getType()) {
						case Folder:
							_style = def;
							break;
						case Image:
							_style = "image file om-icon";
							break;
						case PollChart:
							_style = "chart file om-icon";
							break;
						case WmlFile:
							_style = "wml file om-icon";
							break;
						case Video:
						case Recording:
						{
							_style = "recording om-icon";
							if (f instanceof Recording) {
								Status st = ((Recording)f).getStatus();
								if (Status.RECORDING == st || Status.CONVERTING == st) {
									addStyle = "processing";
								}
							}
						}
							break;
						case Presentation:
							_style = "doc file om-icon";
							break;
						default:
							_style = "file om-icon";
							break;
					}
					style = String.format("%s %s", addStyle, _style);
				}
				return style;
			}
			
			@Override
			protected String getOtherStyleClass(FileItem r) {
				return getItemStyle(r, super.getOtherStyleClass(r));
			}
			
			@Override
			protected String getOpenStyleClass() {
				return getItemStyle(getModelObject(), super.getOpenStyleClass());
			}
			
			@Override
			protected String getClosedStyleClass() {
				return getItemStyle(getModelObject(), super.getClosedStyleClass());
			}
			
			@Override
			protected String getSelectedStyleClass() {
				return "ui-state-active";
			}
			
			@Override
			protected IModel<String> newLabelModel(IModel<FileItem> model) {
				return Model.of(model.getObject().getName());
			}
		};
	}
}
