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

import static org.apache.openmeetings.util.OmFileHelper.MP4_EXTENSION;
import static org.apache.openmeetings.util.OmFileHelper.getHumanSize;
import static org.apache.openmeetings.util.OmFileHelper.isRecordingExists;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.file.RecordingContainerData;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.db.entity.user.Organisation_Users;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.ConfirmableAjaxLink;
import org.apache.openmeetings.web.common.UserPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import wicketdnd.DragSource;
import wicketdnd.DropTarget;
import wicketdnd.Location;
import wicketdnd.Operation;
import wicketdnd.Reject;
import wicketdnd.Transfer;

public class RecordingsPanel extends UserPanel {
	private static final long serialVersionUID = 1321258690447136958L;
	private final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	private final VideoPlayer video = new VideoPlayer("video");
	private final VideoInfo info = new VideoInfo("info");
	private final IModel<FlvRecording> rm = new CompoundPropertyModel<FlvRecording>(new FlvRecording());
	private final IModel<String> homeSize = Model.of((String)null);
	private final IModel<String> publicSize = Model.of((String)null);
	private final RecordingErrorsDialog errorsDialog = new RecordingErrorsDialog("errors", Model.of((FlvRecording)null));
	private RecordingTree selected;
	
	public RecordingsPanel(String id) {
		super(id);
		rm.getObject().setFlvRecordingId(Long.MIN_VALUE);
		final AddFolderDialog addFolder = new AddFolderDialog("addFolder", WebSession.getString(712)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				FlvRecording f = new FlvRecording();
				f.setFileName(getModelObject());
				f.setInsertedBy(getUserId());
				f.setInserted(new Date());
				f.setFolder(true);
				f.setIsImage(false);
				f.setIsPresentation(false);
				f.setIsRecording(true);
				FlvRecording p = rm.getObject();
				long parentId = p.getFlvRecordingId();
				if (p.isFolder()) {
					f.setParentFileExplorerItemId(parentId);
				}
				f.setOwnerId(p.getOwnerId());
				f.setOrganization_id(p.getOrganization_id());
				getBean(FlvRecordingDao.class).update(f);
				target.add(trees); //FIXME add correct refresh
			}
		};
		add(addFolder);
		add(new WebMarkupContainer("create").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				addFolder.open(target);
			}
		}));
		add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior("onclick") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(trees); //FIXME add correct refresh
			}
		}));
		ConfirmableAjaxLink trash = new ConfirmableAjaxLink("trash", 713) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				long id = rm.getObject().getFlvRecordingId();
				if (id > 0) {
					getBean(FlvRecordingDao.class).delete(rm.getObject());
				}
				target.add(trees); //FIXME add correct refresh
			}
		};
		trash.add(new WebMarkupContainer("drop-center").setOutputMarkupId(true)).add(new DropTarget(Operation.MOVE) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location) throws Reject {
				FlvRecording r = transfer.getData();
				getBean(FlvRecordingDao.class).delete(r);
				target.add(trees); //FIXME add correct refresh
			}
		}.dropCenter("span"));
		add(trash/*.add(new WindowsTheme())*/); //TODO check theme here
		RepeatingView treesView = new RepeatingView("tree");
		treesView.add(selected = new RecordingTree(treesView.newChildId(), new MyRecordingTreeProvider()));
		treesView.add(new RecordingTree(treesView.newChildId(), new PublicRecordingTreeProvider(null, null)));
		for (Organisation_Users ou : getBean(UserDao.class).get(getUserId()).getOrganisation_users()) {
			Organisation o = ou.getOrganisation();
			treesView.add(new RecordingTree(treesView.newChildId(), new PublicRecordingTreeProvider(o.getOrganisation_id(), o.getName())));
		}
		add(trees.add(treesView).setOutputMarkupId(true));
		updateSizes();
		add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		sizes.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(30)) {
			private static final long serialVersionUID = 1L;

			protected void onPostProcessTarget(AjaxRequestTarget target) {
				updateSizes();
			}
		});
		add(video, info, errorsDialog);
	}

	private void updateSizes() {
		RecordingContainerData sizeData = getBean(FlvRecordingDao.class).getRecordingContainerData(getUserId());
		if (sizeData != null) {
			homeSize.setObject(getHumanSize(sizeData.getUserHomeSize()));
			publicSize.setObject(getHumanSize(sizeData.getPublicFileSize()));
		}
	}
	
	//FIXME need to be generalized to use as Room files explorer
	class RecordingTree extends DefaultNestedTree<FlvRecording> {
		private static final long serialVersionUID = 2527395034256868022L;

		public RecordingTree(String id, ITreeProvider<FlvRecording> tp) {
			super(id, tp);
			setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
		}
		
		@Override
		protected Component newContentComponent(String id, IModel<FlvRecording> node) {
			return new Folder<FlvRecording>(id, this, node) {
				private static final long serialVersionUID = 1020976220467419084L;

				@Override
				protected Component newLabelComponent(String id, final IModel<FlvRecording> lm) {
					FlvRecording r = lm.getObject();
					Component result = r.isFolder() || r.getFlvRecordingId() < 1 ? new RecordingPanel(id, lm) : new RecordingItemPanel(id, lm, errorsDialog);
					if (r.getFlvRecordingId() > 0) {
						result.add(new DragSource(Operation.MOVE) {
							private static final long serialVersionUID = 1L;

							@Override
							public void onBeforeDrop(Component drag, Transfer transfer) throws Reject {
								transfer.setData(lm.getObject());
							};
							
							@Override
							public void onAfterDrop(AjaxRequestTarget target, wicketdnd.Transfer transfer) {
								transfer.setData(null);
							}
						}.drag("div"));
					}
					if (r.getFlvRecordingId() < 0 || r.isFolder()) {
						result.add(new DropTarget(Operation.MOVE) {
							private static final long serialVersionUID = 1L;

							@Override
							public void onDrop(AjaxRequestTarget target, Transfer transfer, Location location) throws Reject {
								FlvRecording p = lm.getObject();
								long pid = p.getFlvRecordingId();
								FlvRecording r = transfer.getData();
								r.setParentFileExplorerItemId(pid > 0 ? pid : null);
								r.setOrganization_id(p.getOrganization_id());
								r.setOwnerId(p.getOwnerId());
								getBean(FlvRecordingDao.class).update(r);
								target.add(trees); //FIXME add correct refresh
							}
						}.dropCenter("div"));
					}
					return result;
				}
				
				@Override
				protected boolean isSelected() {
					return getModelObject().getFlvRecordingId() == rm.getObject().getFlvRecordingId();
				}
				
				@Override
				protected boolean isClickable() {
					return true;
				}
				
				@Override
				protected void onClick(AjaxRequestTarget target) {
					FlvRecording r = getModelObject();
					FlvRecording _prev = rm.getObject();
					rm.setObject(r);
					if (_prev != null) {
						if (_prev.isFolder()) {
							selected.updateBranch(_prev, target);
						} else {
							selected.updateNode(_prev, target);
						}
					}
 					selected = RecordingTree.this;
					if (r.isFolder()) {
						if (getState(r) == State.COLLAPSED) {
							super.onClick(target);
						}
						updateBranch(r, target);
					} else {
						video.update(target, r);
						info.update(target, r);
						updateNode(r, target);
					}
				}
				
				@Override
				protected String getOtherStyleClass(FlvRecording t) {
					String style;
					if (t.getFlvRecordingId() == 0) {
						style = "my-recordings om-icon";
					} else if (t.getFlvRecordingId() < 0) {
						style = "public-recordings om-icon";
					} else {
						style = t.isFolder() ? super.getOtherStyleClass(t)
								: (isRecordingExists(t.getFileHash() + MP4_EXTENSION) ? "recording om-icon" : "broken-recording om-icon");
					}
					if (isSelected()) {
						style += " selected";
					}
					return style;
				}
				
				@Override
				protected String getOpenStyleClass() {
					String style;
					FlvRecording r = getModelObject();
					if (r.getFlvRecordingId() == 0) {
						style = "my-recordings om-icon";
					} else if (r.getFlvRecordingId() < 0) {
						style = "public-recordings om-icon";
					} else {
						style = super.getOpenStyleClass();
					}
					return style;
				}
				
				@Override
				protected String getClosedStyleClass() {
					String style;
					FlvRecording r = getModelObject();
					if (r.getFlvRecordingId() == 0) {
						style = "my-recordings om-icon";
					} else if (r.getFlvRecordingId() < 0) {
						style = "public-recordings om-icon";
					} else {
						style = super.getClosedStyleClass();
					}
					return style;
				}
				
				@Override
				protected IModel<String> newLabelModel(IModel<FlvRecording> model) {
					return Model.of(model.getObject().getFileName());
				}
			};
		}
	}
	
	class MyRecordingTreeProvider extends RecordingTreeProvider {
		private static final long serialVersionUID = 1L;

		public Iterator<? extends FlvRecording> getRoots() {
			FlvRecording r = new FlvRecording();
			r.setFlvRecordingId(0);
			r.setFileName(WebSession.getString(860));
			r.setOwnerId(getUserId());
			return Arrays.asList(r).iterator();
		}
		
		public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
			if (node.getFlvRecordingId() == 0) {
				return getBean(FlvRecordingDao.class).getFlvRecordingRootByOwner(getUserId()).iterator();
			} else {
				return super.getChildren(node);
			}
		}
	}
	
	class PublicRecordingTreeProvider extends RecordingTreeProvider {
		private static final long serialVersionUID = 5502610991599632079L;
		private final Long orgId;
		private final String name;

		public PublicRecordingTreeProvider(Long orgId, String name) {
			this.orgId = orgId;
			this.name = name;
		}
		
		public Iterator<? extends FlvRecording> getRoots() {
			FlvRecording r = new FlvRecording();
			r.setFlvRecordingId(-1);
			r.setOrganization_id(orgId);
			r.setOwnerId(null);
			String pub = WebSession.getString(861);
			r.setFileName(orgId == null ? pub : String.format("%s (%s)", pub, name));
			return Arrays.asList(r).iterator();
		}
		
		public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
			if (node.getFlvRecordingId() < 0) {
				return getBean(FlvRecordingDao.class).getFlvRecordingRootByPublic(orgId).iterator();
			} else {
				return super.getChildren(node);
			}
		}
	}
	
	abstract class RecordingTreeProvider implements ITreeProvider<FlvRecording> {
		private static final long serialVersionUID = 1L;

		public void detach() {
			// TODO LDM should be used
		}

		public boolean hasChildren(FlvRecording node) {
			return node.getFlvRecordingId() <= 0 || node.isFolder();
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
