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
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.openmeetings.data.flvrecord.FlvRecordingDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class RecordingsPanel extends UserPanel {
	private static final long serialVersionUID = 1321258690447136958L;
	WebMarkupContainer info = new WebMarkupContainer("info");
	WebMarkupContainer trees = new WebMarkupContainer("trees");
	private IModel<FlvRecording> rm = new CompoundPropertyModel<FlvRecording>(new FlvRecording());

	public RecordingsPanel(String id) {
		super(id);
		rm.getObject().setFlvRecordingId(-3);
		rm.getObject().setOwnerId(getUserId());
		add(new WebMarkupContainer("create").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -110084769805785972L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				FlvRecording f = new FlvRecording();
				f.setFileName(WebSession.getString(712));
				f.setInsertedBy(getUserId());
				f.setInserted(new Date());
				f.setIsFolder(true);
				f.setIsImage(false);
				f.setIsPresentation(false);
				f.setIsRecording(true);
				f.setOwnerId(rm.getObject().getOwnerId());
				getBean(FlvRecordingDao.class).updateFlvRecording(f);
				target.add(trees);
			}
		}));
		add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = -110084769805785972L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(trees);
			}
		}));
		add(trees
			.add(new RecordingTree("myrecordings", new MyRecordingTreeProvider()))
			.add(new RecordingTree("publicrecordings", new PublicRecordingTreeProvider()))
			.setOutputMarkupId(true)
			);
		add(new Label("homeSize", ""));
		add(new Label("publicSize", ""));
		add(info.setDefaultModel(rm)
			.add(new Label("fileName"))
			.add(new Label("fileSize"))
			.add(new Label("recordEnd"))
			.add(new Label("room_id"))
			.setOutputMarkupId(true)
			);
	}
	
	class RecordingTree extends DefaultNestedTree<FlvRecording> {
		private static final long serialVersionUID = 2527395034256868022L;

		public RecordingTree(String id, ITreeProvider<FlvRecording> tp) {
			super(id, tp);
		}
		
		@Override
		protected Component newContentComponent(String id, IModel<FlvRecording> node) {
			return new Folder<FlvRecording>(id, this, node) {
				private static final long serialVersionUID = 1020976220467419084L;

				@Override
				protected Component newLabelComponent(String id, final IModel<FlvRecording> lm) {
					FlvRecording r = lm.getObject();
					if (r.getIsFolder() != null && r.getIsFolder()) {
						return new AjaxEditableLabel<String>(id, newLabelModel(lm)) {
							private static final long serialVersionUID = -6631089550858911148L;

							@Override
							public void onEdit(AjaxRequestTarget target) {
								// TODO Auto-generated method stub
								super.onEdit(target);
							}
						};
					} else {
						return super.newLabelComponent(id, lm);
					}
				}
				
				@Override
				protected boolean isClickable() {
					return true;
				}
				
				@Override
				protected void onClick(AjaxRequestTarget target) {
					FlvRecording r = getModelObject();
					rm.setObject(r);
					if (r.getIsFolder() == null || r.getIsFolder()) {
						super.onClick(target);
					} else {
						target.add(info);
					}
				}
				
				@Override
				protected String getOtherStyleClass(FlvRecording t) {
					return t.getIsFolder() ? super.getOtherStyleClass(t) : "recording-icon";
				}
				
				@Override
				protected IModel<String> newLabelModel(IModel<FlvRecording> model) {
					return Model.of(model.getObject().getFileName());
				}
			};
		}
	}
	
	class MyRecordingTreeProvider extends RecordingTreeProvider {
		private static final long serialVersionUID = -4463900798616753927L;

		public Iterator<? extends FlvRecording> getRoots() {
			FlvRecording r = new FlvRecording();
			r.setFlvRecordingId(-2);
			r.setFileName(WebSession.getString(860));
			r.setOwnerId(getUserId());
			return Arrays.asList(r).iterator();
		}
		
		public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
			if (node.getFlvRecordingId() < 0) {
				List<FlvRecording> roots = new ArrayList<FlvRecording>();
				for (Organisation_Users ou : getBean(UsersDao.class).get(getUserId()).getOrganisation_users()) {
					roots.addAll(getBean(FlvRecordingDao.class).getFlvRecordingRootByOwner(ou.getOrganisation().getOrganisation_id()));
				}
				return roots.iterator();
			} else {
				return super.getChildren(node);
			}
		}
	}
	
	class PublicRecordingTreeProvider extends RecordingTreeProvider {
		private static final long serialVersionUID = 5502610991599632079L;

		public Iterator<? extends FlvRecording> getRoots() {
			FlvRecording r = new FlvRecording();
			r.setFlvRecordingId(-1);
			r.setFileName(WebSession.getString(861));
			return Arrays.asList(r).iterator();
		}
		
		public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
			if (node.getFlvRecordingId() < 0) {
				return getBean(FlvRecordingDao.class).getFlvRecordingRootByPublic(getUserId()).iterator();
			} else {
				return super.getChildren(node);
			}
		}
	}
	
	abstract class RecordingTreeProvider implements ITreeProvider<FlvRecording> {
		private static final long serialVersionUID = -3149843028275612342L;

		public void detach() {
			// TODO LDM should be used
		}

		public boolean hasChildren(FlvRecording node) {
			return node.getFlvRecordingId() < 0 || node.getIsFolder();
		}

		public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
			return getBean(FlvRecordingDao.class).getFlvRecordingByParent(node.getFlvRecordingId()).iterator();
		}

		public IModel<FlvRecording> model(FlvRecording object) {
			// TODO LDM should be used
			return Model.of(object);
		}
		
	}
}
