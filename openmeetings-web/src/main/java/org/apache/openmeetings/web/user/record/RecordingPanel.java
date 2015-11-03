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
package org.apache.openmeetings.web.user.record;

import static org.apache.openmeetings.web.app.Application.getBean;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.interaction.draggable.Draggable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;

public class RecordingPanel extends Panel {
	private static final long serialVersionUID = 1L;
	protected final MarkupContainer drop;
	protected final MarkupContainer drag;

	public RecordingPanel(String id, final IModel<FlvRecording> model, final RecordingsPanel treePanel) {
		super(id, model);
		FlvRecording r = model.getObject();
		drop = r.isFolder() ? new Droppable<FlvRecording>("drop", model) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("hoverClass", Options.asString("ui-state-hover"));
				behavior.setOption("accept", Options.asString(".recorditem"));
			}
			
			@Override
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (o instanceof FlvRecording) {
					FlvRecording p = (FlvRecording)drop.getDefaultModelObject();
					FlvRecording f = (FlvRecording)o;
					long pid = p.getFlvRecordingId();
					//FIXME parent should not be moved to child !!!!!!!
					if (pid == f.getFlvRecordingId()) {
						return;
					}
					f.setParentFileExplorerItemId(pid > 0 ? pid : null);
					f.setOwnerId(p.getOwnerId());
					f.setRoom_id(p.getRoom_id());
					f.setOrganization_id((p).getOrganization_id());
					getBean(FlvRecordingDao.class).update(f);
				}
				target.add(treePanel.trees); //FIXME add correct refresh
			}
		} : new WebMarkupContainer("drop");
		if (r.getFlvRecordingId() < 1) {
			drag = new WebMarkupContainer("drag");
		} else {
			Draggable<FlvRecording> d = new Draggable<FlvRecording>("drag", model) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onConfigure(JQueryBehavior behavior) {
					super.onConfigure(behavior);
					behavior.setOption("revert", "treeRevert");
					behavior.setOption("cursor", Options.asString("move"));
				}
			};
			d.setContainment(".file.tree");
			d.add(AttributeAppender.append("class", "recorditem"));
			drag = d;
		}
		drag.add(r.getFlvRecordingId() < 1 ? new Label("name", r.getFileName()) : new AjaxEditableLabel<String>("name", Model.of(model.getObject().getFileName())) {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected String getLabelAjaxEvent() {
				return "dblClick";
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				super.onSubmit(target);
				FlvRecording fi = model.getObject();
				fi.setFileName(getEditor().getModelObject());
				getBean(FlvRecordingDao.class).update(fi);
			}
			
			@Override
			public void onEdit(AjaxRequestTarget target) {
				super.onEdit(target);
			}
		});
		add(drop.add(drag).setOutputMarkupId(true));
	}
}
