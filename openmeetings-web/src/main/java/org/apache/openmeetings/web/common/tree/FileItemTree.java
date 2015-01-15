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

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.record.FlvRecording.Status;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FileItemTree<T extends FileItem> extends DefaultNestedTree<T> {
	private static final long serialVersionUID = 1L;
	private final FileTreePanel treePanel;
	private final IModel<T> selectedItem = Model.of((T)null);

	public FileItemTree(String id, FileTreePanel treePanel, ITreeProvider<T> tp) {
		super(id, tp);
		this.treePanel = treePanel;
		setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
	}
	
	@Override
	protected Component newContentComponent(String id, IModel<T> node) {
		return new Folder<T>(id, this, node) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Component newLabelComponent(String id, final IModel<T> lm) {
				FileItem r = lm.getObject();
				return Type.Folder == r.getType() || r.getId() < 1 ? new FolderPanel(id, lm, treePanel) : new FileItemPanel(id, lm, treePanel);
			}
			
			@Override
			protected boolean isSelected() {
				return getModelObject().getId().equals(treePanel.selectedFile.getObject().getId());
			}
			
			@Override
			protected boolean isClickable() {
				return true;
			}
			
			@Override
			protected void onClick(AjaxRequestTarget target) {
				T r = getModelObject();
				treePanel.selected.resetSelected(target);
				selectedItem.setObject(r);
				treePanel.selectedFile.setObject(r);
				treePanel.selected = FileItemTree.this;
				if (Type.Folder == r.getType()) {
					if (getState(r) == State.COLLAPSED) {
						super.onClick(target);
					}
					updateBranch(r, target);
				} else {
					treePanel.update(target, r);
					updateNode(r, target);
				}
			}
			
			private String getItemStyle(T f, String def) {
				String style;
				if (f.getId() == 0) {
					style = "my file om-icon";
				} else if (f.getId() < 0) {
					style = "public file om-icon";
				} else {
					switch(f.getType()) {
						case Folder:
							style = def;
							break;
						case Image:
							style = "image file om-icon";
							break;
						case PollChart:
							style = "chart file om-icon";
							break;
						case WmlFile:
							style = "wml file om-icon";
							break;
						case Recording:
						{
							FlvRecording r = (FlvRecording)f;
							if (isRecordingExists(r.getFileHash() + MP4_EXTENSION)) {
								style = "recording om-icon";
							} else if (Status.PROCESSING == r.getStatus()) {
								style = "processing-recording om-icon";
							} else {
								style = "broken-recording om-icon";
							}
						}
							break;
						case Presentation:
							style = "doc file om-icon";
							break;
						case Video:
						default:
							style = "recording om-icon";
							break;
					}
				}
				return style;
			}
			
			@Override
			protected String getOtherStyleClass(T r) {
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
			protected IModel<String> newLabelModel(IModel<T> model) {
				return Model.of(model.getObject().getFileName());
			}
		};
	}

	private void resetSelected(AjaxRequestTarget target) {
		T _prev = selectedItem.getObject();
		if (_prev != null) {
			if (Type.Folder == _prev.getType()) {
				updateBranch(_prev, target);
			} else {
				updateNode(_prev, target);
			}
			selectedItem.setObject(null);
		}
	}
}
