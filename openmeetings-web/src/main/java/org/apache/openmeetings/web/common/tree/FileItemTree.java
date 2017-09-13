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

import java.util.Optional;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONObject;

public class FileItemTree extends DefaultNestedTree<BaseFileItem> {
	private static final long serialVersionUID = 1L;
	private final static String CSS_CLASS_FILE = "file ";
	private final static String PARAM_MOD = "mod";
	private final static String PARAM_SHIFT = "s";
	private final static String PARAM_CTRL = "c";
	final FileTreePanel treePanel;

	public FileItemTree(String id, FileTreePanel treePanel, ITreeProvider<BaseFileItem> tp) {
		super(id, tp);
		this.treePanel = treePanel;
		setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
	}

	private void onClick(AjaxRequestTarget target, BaseFileItem f) {
		String mod = getRequest().getRequestParameters().getParameterValue(PARAM_MOD).toOptionalString();
		boolean shift = false, ctrl = false;
		if (!Strings.isEmpty(mod)) {
			JSONObject o = new JSONObject(mod);
			shift = o.optBoolean(PARAM_SHIFT);
			ctrl = o.optBoolean(PARAM_CTRL);
		}
		treePanel.select(f, target, shift, ctrl);
		if (Type.Folder == f.getType() && getState(f) == State.COLLAPSED) {
			this.expand(f);
		} else {
			treePanel.update(target, f);
		}
	}

	private static boolean isClickable() {
		return true;
	}

	@Override
	public OmTreeProvider getProvider() {
		return (OmTreeProvider)super.getProvider();
	}

	public void refreshRoots(boolean all) {
		modelChanging();
		getModelObject().clear();
		modelChanged();
		getProvider().refreshRoots(all);
		replace(newSubtree("subtree", Model.of((BaseFileItem)null)));
	}

	@Override
	protected Component newContentComponent(String id, IModel<BaseFileItem> node) {
		return new Folder<BaseFileItem>(id, this, node) {
			private static final long serialVersionUID = 1L;

			@Override
			protected Component newLabelComponent(String id, final IModel<BaseFileItem> lm) {
				BaseFileItem r = lm.getObject();
				return Type.Folder == r.getType() || r.getId() == null
						? new FolderPanel(id, lm, treePanel)
						: new FileItemPanel(id, lm, treePanel);
			}

			@Override
			protected boolean isSelected() {
				return treePanel.isSelected(getModelObject());
			}

			@Override
			protected boolean isClickable() {
				return FileItemTree.isClickable();
			}

			@Override
			protected void onClick(Optional<AjaxRequestTarget> targetOptional) {
				FileItemTree.this.onClick(targetOptional.get(), getModelObject());
			}

			@Override
			protected MarkupContainer newLinkComponent(String id, IModel<BaseFileItem> model) {
				final BaseFileItem f = getModelObject();
				return new AjaxLink<Void>(id) {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isEnabled() {
						return FileItemTree.isClickable();
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						FileItemTree.this.onClick(target, f);
					}

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						attributes.getDynamicExtraParameters().add(
								String.format("return {%s: JSON.stringify({%s: attrs.event.shiftKey, %s: attrs.event.ctrlKey})};"
										, PARAM_MOD, PARAM_SHIFT, PARAM_CTRL));
					}
				};
			}

			@Override
			protected String getOtherStyleClass(BaseFileItem r) {
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
			protected IModel<String> newLabelModel(IModel<BaseFileItem> model) {
				return Model.of(model.getObject().getName());
			}
		};
	}

	private static String getItemStyle(BaseFileItem f, boolean open) {
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
}
