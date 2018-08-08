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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;
import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Map.Entry;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.Recording.Status;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree.State;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;

import com.github.openjson.JSONObject;
import com.googlecode.wicket.jquery.core.IJQueryWidget.JQueryWidget;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.DraggableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.draggable.IDraggableListener;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;
import com.googlecode.wicket.jquery.ui.interaction.droppable.IDroppableListener;

public class FolderPanel extends Panel implements IDraggableListener, IDroppableListener {
	private static final long serialVersionUID = 1L;
	private static final String CSS_CLASS_FILE = "file ";
	private static final String PARAM_MOD = "mod";
	private static final String PARAM_SHIFT = "s";
	private static final String PARAM_CTRL = "c";
	private final StyleBehavior styleClass;
	private final FileTreePanel treePanel;

	public FolderPanel(String id, final IModel<BaseFileItem> model, final FileTreePanel treePanel) {
		super(id, model);
		this.treePanel = treePanel;
		styleClass = new StyleBehavior();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final BaseFileItem f = (BaseFileItem)getDefaultModelObject();
		boolean editable = !treePanel.isReadOnly() && !f.isReadOnly();
		final String selector = JQueryWidget.getSelector(this);
		if (f.getType() == Type.Folder && editable) {
			add(new DroppableBehavior(
					selector
					, new Options()
						.set("hoverClass", Options.asString("ui-state-hover"))
						.set("accept", Options.asString(getDefaultModelObject() instanceof Recording ? ".recorditem" : ".fileitem"))
					, this));
		}
		if (f.getId() != null && !treePanel.isReadOnly()) {
			add(new DraggableBehavior(
					selector
					, new Options()
						.set("revert", "treeRevert")
						.set("cursor", Options.asString("move"))
						.set("helper", "dragHelper")
						.set("cursorAt", "{left: 40, top: 18}")
						.set("containment", Options.asString(treePanel.getContainment()))
					, this));
		}
		Component name = f.getId() == null || !editable ? new Label("name", f.getName()) : new AjaxEditableLabel<String>("name", Model.of(f.getName())) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getLabelAjaxEvent() {
				return "dblclick";
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				f.setName(getEditor().getModelObject());
				if (f instanceof Recording) {
					getBean(RecordingDao.class).update((Recording)f);
				} else {
					getBean(FileItemDao.class).update((FileItem)f);
				}
			}
		};
		add(name);
		add(AttributeModifier.append(ATTR_TITLE, f.getName()));
		add(styleClass);
		add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onEvent(AjaxRequestTarget target) {
				onClick(target, f);
			}

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getDynamicExtraParameters().add(
						String.format("return {%s: JSON.stringify({%s: attrs.event.shiftKey, %s: attrs.event.ctrlKey})};"
								, PARAM_MOD, PARAM_SHIFT, PARAM_CTRL));
			}
		});
	}

	private static void moveAll(final FileTreePanel treePanel, AjaxRequestTarget target, BaseFileItem p) {
		for (Entry<String, BaseFileItem> e : treePanel.getSelected().entrySet()) {
			move(treePanel, target, p, e.getValue());
		}
	}

	private static void move(final FileTreePanel treePanel, AjaxRequestTarget target, BaseFileItem p, BaseFileItem f) {
		Long pid = p.getId();
		if (pid != null && pid.equals(f.getId())) {
			return;
		}
		f.setParentId(pid);
		f.setOwnerId(p.getOwnerId());
		f.setRoomId(p.getRoomId());
		f.setGroupId(p.getGroupId());
		if (f instanceof Recording) {
			getBean(RecordingDao.class).update((Recording)f);
		} else {
			getBean(FileItemDao.class).update((FileItem)f);
		}
		treePanel.updateNode(target, f);
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
		if (Type.Folder == f.getType() && treePanel.tree.getState(f) == State.COLLAPSED) {
			treePanel.tree.expand(f);
		} else {
			treePanel.update(target, f);
		}
	}

	private CharSequence getItemStyle() {
		final BaseFileItem f = (BaseFileItem)getDefaultModelObject();
		boolean open = State.EXPANDED == treePanel.tree.getState(f);
		StringBuilder style = new StringBuilder("big om-icon ");
		if (f.getId() == null) {
			style.append(CSS_CLASS_FILE).append(f.getHash().indexOf("my") > -1 ? "my " : "public ");
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
		if (treePanel.isSelected(f)) {
			style.append("ui-state-active ");
		}
		String cls = f instanceof Recording ? "recorditem " : "fileitem ";
		style.append(f.isReadOnly() ? "readonlyitem " : cls);
		return style;
	}

	@Override
	public boolean isStopEventEnabled() {
		return false;
	}

	@Override
	public void onDragStart(AjaxRequestTarget target, int top, int left) {
		// noop
	}

	@Override
	public void onDragStop(AjaxRequestTarget target, int top, int left) {
		// noop
	}

	@Override
	public boolean isOverEventEnabled() {
		return false;
	}

	@Override
	public boolean isExitEventEnabled() {
		return false;
	}

	@Override
	public void onOver(AjaxRequestTarget target, Component component) {
		// noop
	}

	@Override
	public void onExit(AjaxRequestTarget target, Component component) {
		// noop
	}

	@Override
	public void onDrop(AjaxRequestTarget target, Component component) {
		Object o = component.getDefaultModelObject();
		if (o instanceof BaseFileItem) {
			BaseFileItem p = (BaseFileItem)getDefaultModelObject();
			BaseFileItem f = (BaseFileItem)o;
			if (treePanel.isSelected(f)) {
				moveAll(treePanel, target, p);
			} else {
				move(treePanel, target, p, f);
			}
			treePanel.updateNode(target, p);
		}
		target.add(treePanel.trees);
	}

	private class StyleBehavior extends Behavior {
		private static final long serialVersionUID = 1L;

		@Override
		public void onComponentTag(Component component, ComponentTag tag) {
			tag.put(ATTR_CLASS, getItemStyle());
		}
	}
}
