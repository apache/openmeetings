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

import static java.time.Duration.ZERO;
import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.web.common.confirmation.ConfirmableAjaxBorder;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.ajax.AjaxDownloadBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.SplitButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;

public abstract class FileTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String ALIGN_LEFT_CLASS = " align-left";
	private static final String ALIGN_RIGHT_CLASS = " align-right";
	private static final String BASE_CLASS = " om-icon big clickable";
	private static final String UPLOAD_CLASS = "add" + BASE_CLASS + ALIGN_LEFT_CLASS;
	private static final String CREATE_DIR_CLASS = "folder-create" + BASE_CLASS + ALIGN_LEFT_CLASS;
	private static final String TRASH_CLASS = "trash" + BASE_CLASS + ALIGN_RIGHT_CLASS;
	private static final String DISABLED_CLASS = " disabled";
	final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	private BaseFileItem lastSelected = null;
	private Map<String, BaseFileItem> selected = new HashMap<>();
	private File dwnldFile;
	final AjaxDownloadBehavior downloader = new AjaxDownloadBehavior(new IResource() {
		private static final long serialVersionUID = 1L;

		@Override
		public void respond(Attributes attributes) {
			new FileSystemResource(dwnldFile.toPath()) {
				private static final long serialVersionUID = 1L;

				@Override
				protected ResourceResponse createResourceResponse(Attributes attr, Path path) {
					ResourceResponse response = super.createResourceResponse(attr, path);
					response.setCacheDuration(ZERO);
					return response;
				}
			}.respond(attributes);
		}
	});
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((Recording)null));
	final FileItemTree tree;
	private final SplitButton download = new SplitButton("download", Model.of("")) {
		private static final long serialVersionUID = 1L;

		private AbstractLink createLink(String markupId, IModel<String> model, String ext) {
			return new BootstrapAjaxLink<>(markupId, model, Buttons.Type.Outline_Primary, model) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isEnabled() {
					File f = null;
					if (getSelected().size() == 1) {
						f = getLastSelected().getFile(ext);
					}
					return f != null && f.exists();
				}

				@Override
				public void onClick(AjaxRequestTarget target) {
					onDownlownClick(target, ext);
				}
			}.setIconType(FontAwesome5IconType.download_s);
		}

		@Override
		protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
			return List.of(
					createLink(buttonMarkupId, new ResourceModel("files.download.original"), null)
					, createLink(buttonMarkupId, new ResourceModel("files.download.pdf"), EXTENSION_PDF)
					, createLink(buttonMarkupId, new ResourceModel("files.download.jpg"), EXTENSION_JPG)
					);
		}

		@Override
		protected void addButtonBehavior(ButtonBehavior buttonBehavior) {
			buttonBehavior.setSize(Buttons.Size.Small).setType(Buttons.Type.Outline_Secondary);
			super.addButtonBehavior(buttonBehavior);
		}

		@Override
		protected AbstractLink newBaseButton(String markupId, IModel<String> labelModel, IModel<IconType> iconTypeModel) {
			return createLink(markupId, new ResourceModel("files.download.original"), null);
		}

		public void onDownlownClick(AjaxRequestTarget target, String ext) {
			BaseFileItem fi = getLastSelected();
			File f = ext == null && (Type.IMAGE == fi.getType() || Type.PRESENTATION == fi.getType())
					? fi.getOriginal() : fi.getFile(ext);
			if (f != null && f.exists()) {
				dwnldFile = f;
				downloader.initiate(target);
			}
		}
	};
	private final Form<Void> form = new Form<>("form");
	private final NameDialog addFolder;
	private ConfirmableAjaxBorder trashBorder;
	private final Long roomId;
	private boolean readOnly = true;
	private final Component createDir = new WebMarkupContainer("create").add(new AjaxEventBehavior(EVT_CLICK) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onEvent(AjaxRequestTarget target) {
			addFolder.show(target);
		}
	});
	private final Component upload = new WebMarkupContainer("upload");
	@SpringBean
	private RecordingDao recDao;
	@SpringBean
	private FileItemDao fileDao;

	public FileTreePanel(String id, Long roomId, NameDialog addFolder) {
		super(id);
		this.roomId = roomId;
		this.addFolder = addFolder;
		final OmTreeProvider tp = new OmTreeProvider(roomId);
		select(tp.getRoot(), null, false, false);
		form.add(tree = new FileItemTree("tree", this, tp));
		form.add(download.setVisible(false).setOutputMarkupPlaceholderTag(true));
		add(form.add(downloader));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Droppable<BaseFileItem> trashToolbar = new Droppable<>("trash-toolbar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("hoverClass", Options.asString("trash-toolbar-hover"));
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
								return "OmFileTree.confirmTrash($(this), ui, function() {"
										+ super.getCallbackFunctionBody(parameters)
										+ "});";
							}
						};
					}
				};
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (o instanceof BaseFileItem) {
					BaseFileItem f = (BaseFileItem)o;
					if (isSelected(f)) {
						deleteAll(target);
					} else {
						delete(f, target);
					}
				}
			}
		};
		form.add(trashToolbar);
		trashToolbar.add(getUpload());
		trashToolbar.add(createDir);
		trashToolbar.add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior(EVT_CLICK) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				update(target);
			}
		}));
		trashToolbar.add(getTrashBorder());

		form.add(trees.add(tree).setOutputMarkupId(true));
		updateSizes();
		form.add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		form.add(errorsDialog);
		setReadOnly(false, null);
	}

	private ConfirmableAjaxBorder getTrashBorder() {
		if (trashBorder == null) {
			trashBorder = new ConfirmableAjaxBorder("trash", new ResourceModel("80"), new ResourceModel("713")) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean isClickable() {
					return !readOnly && !selected.isEmpty();
				}

				@Override
				protected void onConfirm(AjaxRequestTarget target) {
					deleteAll(target);
				}
			};
		}
		return trashBorder;
	}

	public FileTreePanel setBorderTitle(IModel<String> title) {
		getTrashBorder().setTitle(title);
		return this;
	}

	public FileTreePanel setBorderMessage(IModel<String> message) {
		getTrashBorder().setMessage(message);
		return this;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(FileTreePanel.class, "filetree.js")));
	}

	protected String getContainment() {
		return ".file.item.drop.area";
	}

	protected Component getUpload() {
		return upload.setVisible(false);
	}

	private void deleteAll(AjaxRequestTarget target) {
		for (Entry<String, BaseFileItem> e : selected.entrySet()) {
			BaseFileItem f = e.getValue();
			if (!f.isReadOnly()) {
				delete(f, target);
			}
		}
		updateSelected(target);
		selected.clear();
	}

	void delete(BaseFileItem f, IPartialPageRequestHandler handler) {
		Long id = f.getId();
		if (id != null) {
			if (f instanceof Recording) {
				recDao.delete(f);
			} else {
				fileDao.delete(f);
			}
		}
		update(handler);
	}

	public void setReadOnly(boolean readOnly, IPartialPageRequestHandler handler) {
		if (this.readOnly != readOnly) {
			this.readOnly = readOnly;
			tree.refreshRoots(!readOnly);
			createDir.setEnabled(!readOnly);
			createDir.add(AttributeModifier.replace(ATTR_CLASS, CREATE_DIR_CLASS + (readOnly ? DISABLED_CLASS : "")));
			upload.setEnabled(!readOnly);
			upload.add(AttributeModifier.replace(ATTR_CLASS, UPLOAD_CLASS + (readOnly ? DISABLED_CLASS : "")));
			trashBorder.add(AttributeModifier.replace(ATTR_CLASS, TRASH_CLASS + (readOnly ? DISABLED_CLASS : "")));
			if (handler != null) {
				handler.add(createDir, upload, trashBorder);
				update(handler);
			}
		}
	}

	public boolean isEditable() {
		return !readOnly;
	}

	protected abstract void update(AjaxRequestTarget target, BaseFileItem f);

	public void createFolder(AjaxRequestTarget target, String name) {
		BaseFileItem p = lastSelected;
		boolean isRecording = p instanceof Recording;
		BaseFileItem f = isRecording ? new Recording() : new FileItem();
		f.setName(name);
		f.setHash(randomUUID().toString());
		f.setInsertedBy(getUserId());
		f.setInserted(new Date());
		f.setType(Type.FOLDER);
		f.setOwnerId(p.getOwnerId());
		f.setGroupId(p.getGroupId());
		f.setRoomId(p.getRoomId());
		f.setParentId(Type.FOLDER == p.getType() ? p.getId() : null);
		if (isRecording) {
			recDao.update((Recording)f);
		} else {
			fileDao.update((FileItem)f);
		}
		update(target);
	}

	public abstract void updateSizes();

	public boolean isSelected(BaseFileItem f) {
		return selected.containsKey(f.getHash());
	}

	public Map<String, BaseFileItem> getSelected() {
		return selected;
	}

	public BaseFileItem getLastSelected() {
		return lastSelected;
	}

	public void update(IPartialPageRequestHandler handler) {
		updateSizes();
		handler.add(sizes, trees);
	}

	private void updateSelected(AjaxRequestTarget target) {
		for (Entry<String, BaseFileItem> e : selected.entrySet()) {
			updateNode(target, e.getValue());
		}
	}

	void updateNode(AjaxRequestTarget target, BaseFileItem fi) {
		if (fi != null && !fi.isDeleted() && target != null) {
			if (Type.FOLDER == fi.getType()) {
				tree.updateBranch(fi, target);
			} else {
				tree.updateNode(fi, target);
			}
		}
	}

	private static boolean sameParent(Long roomId, BaseFileItem f1, BaseFileItem f2) {
		if (f1 instanceof Recording && f2 instanceof FileItem) {
			return false;
		}
		if (f1.getParentId() != null && f1.getParentId().equals(f2.getParentId())) {
			return true;
		}
		if (f1.getParentId() == null && f2.getParentId() == null) {
			if (f1.getOwnerId() != null && f1.getOwnerId().equals(f2.getOwnerId())) {
				return true;
			}
			if (f1.getRoomId() != null && f1.getRoomId().equals(f2.getRoomId())) {
				return true;
			}
			if (f2 instanceof FileItem && roomId != null && f1.getRoomId() == null && f2.getRoomId() == null && f1.getOwnerId() == null && f2.getOwnerId() == null) {
				return true;
			}
		}
		return false;
	}

	private static boolean isDownloadable(BaseFileItem f) {
		return !f.isReadOnly() && (f.getType() == Type.PRESENTATION || f.getType() == Type.IMAGE);
	}

	public void select(BaseFileItem fi, AjaxRequestTarget target, boolean shift, boolean ctrl) {
		updateSelected(target); //all previously selected are in update list
		if (ctrl) {
			if (isSelected(fi)) {
				selected.remove(fi.getHash());
			} else {
				selected.put(fi.getHash(), fi);
			}
			lastSelected = fi;
		} else if (shift && lastSelected != null && !lastSelected.getHash().equals(fi.getHash()) && sameParent(roomId, fi, lastSelected)) {
			selected.clear();
			String lastHash = null;
			for (BaseFileItem f : tree.getProvider().getByParent(fi, fi.getParentId())) {
				if (lastHash == null) {
					if (f.getHash().equals(lastSelected.getHash())) {
						lastHash = fi.getHash();
					}
					if (f.getHash().equals(fi.getHash())) {
						lastHash = lastSelected.getHash();
					}
				}
				if (lastHash != null) {
					selected.put(f.getHash(), f);
					if (f.getHash().equals(lastHash)) {
						break;
					}
				}
			}
		} else {
			selected.clear();
			selected.put(fi.getHash(), fi);
			lastSelected = fi;
		}
		updateSelected(target); //all finally selected are in the update list
		if (target != null) {
			target.add(trashBorder, download.setVisible(isDownloadable(lastSelected)));
		}
	}

	@Override
	protected void onDetach() {
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}
}
