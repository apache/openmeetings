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

import static de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal.BUTTON_MARKUP_ID;
import static java.time.Duration.ZERO;
import static java.util.UUID.randomUUID;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.BasePanel.EVT_CLICK;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirmCfg;
import static org.apache.openmeetings.web.pages.BasePage.ALIGN_LEFT;
import static org.apache.openmeetings.web.pages.BasePage.ALIGN_RIGHT;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_MP4;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_TITLE;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.openmeetings.db.dao.file.FileItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.common.NameDialog;
import org.apache.openmeetings.util.OmFileHelper;
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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.resource.FileSystemResource;

import org.wicketstuff.jquery.core.JQueryBehavior;
import org.wicketstuff.jquery.core.Options;
import org.wicketstuff.jquery.core.ajax.IJQueryAjaxAware;
import org.wicketstuff.jquery.core.ajax.JQueryAjaxBehavior;
import org.wicketstuff.jquery.ui.interaction.droppable.Droppable;
import org.wicketstuff.jquery.ui.interaction.droppable.DroppableBehavior;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.ButtonBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.SplitButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.confirmation.ConfirmationBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public abstract class FileTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String BASE_CLASS = " om-icon big clickable";
	private static final String UPLOAD_CLASS = "upload" + BASE_CLASS + " " + ALIGN_LEFT;
	private static final String CREATE_DIR_CLASS = "folder-create" + BASE_CLASS + " " + ALIGN_LEFT;
	private static final String TRASH_CLASS = "trash" + BASE_CLASS + " " + ALIGN_RIGHT;
	private static final String DISABLED_CLASS = " disabled";
	final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	private BaseFileItem lastSelected = null;
	private Map<String, BaseFileItem> selected = new HashMap<>();
	private File dwnldFile;
	private String dwnldName;
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
					response.setFileName(dwnldName);
					return response;
				}
			}.respond(attributes);
		}
	});
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((Recording)null));
	FileItemTree tree;
	private final WebMarkupContainer buttons = new WebMarkupContainer("buttons");
	private final Form<Void> form = new Form<>("form");
	private final NameDialog addFolder;
	private final WebMarkupContainer trash = new WebMarkupContainer("trash");
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

	@Inject
	private RecordingDao recDao;
	@Inject
	private FileItemDao fileDao;

	protected FileTreePanel(String id, Long roomId, NameDialog addFolder) {
		super(id);
		this.roomId = roomId;
		this.addFolder = addFolder;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final OmTreeProvider tp = new OmTreeProvider(roomId);
		select(tp.getRoot(), null, false, false);
		form.add(tree = new FileItemTree("tree", this, tp));
		add(form.add(downloader));
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
				if (o instanceof BaseFileItem f) {
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
		trashToolbar.add(trash.setOutputMarkupId(true));
		trash.add(new AjaxEventBehavior("confirmed.bs.confirmation") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				deleteAll(target);
			}
		});

		ConfirmationBehavior trashConfirm = new ConfirmationBehavior(newOkCancelDangerConfirmCfg(trashToolbar, getString("80")).withContent(getString("713"))) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled(Component component) {
				return !readOnly && !selected.isEmpty();
			}
		};
		trash.add(trashConfirm);

		form.add(trees.add(tree).setOutputMarkupId(true));
		updateSizes();
		form.add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		form.add(errorsDialog);
		setReadOnly(false, null);
		final SplitButton download = new SplitButton("download", Model.of("")) {
			private static final long serialVersionUID = 1L;

			private boolean isDownloadable(BaseFileItem f) {
				return !f.isReadOnly() && (Type.PRESENTATION == f.getType() || Type.IMAGE == f.getType() || Type.RECORDING == f.getType());
			}

			private AbstractLink createLink(String markupId, IModel<String> model, String ext) {
				return new BootstrapAjaxLink<>(markupId, model, Buttons.Type.Outline_Primary, model) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onConfigure() {
						super.onConfigure();
						for (CssClassNameAppender b : getBehaviors(CssClassNameAppender.class)) {
							remove(b);
						}
						File f = null;
						if (getSelected().size() == 1) {
							BaseFileItem fi = getLastSelected();
							if (isDownloadable(fi)) {
								f = fi.getFile(ext);
							}
						}
						setEnabled(f != null && f.exists());
					}

					@Override
					public void onClick(AjaxRequestTarget target) {
						onDownlownClick(target, ext);
					}
				}.setIconType(FontAwesome6IconType.download_s);
			}

			@Override
			protected List<AbstractLink> newSubMenuButtons(String buttonMarkupId) {
				return List.of(
						createLink(buttonMarkupId, new ResourceModel("files.download.original"), null)
						, createLink(buttonMarkupId, new ResourceModel("files.download.pdf"), EXTENSION_PDF)
						, createLink(buttonMarkupId, new ResourceModel("files.download.jpg"), EXTENSION_JPG)
						, createLink(buttonMarkupId, Model.of(EXTENSION_MP4), EXTENSION_MP4)
						);
			}

			@Override
			protected void addButtonBehavior(ButtonBehavior buttonBehavior) {
				buttonBehavior.setSize(Buttons.Size.Small).setType(Buttons.Type.Outline_Secondary);
				super.addButtonBehavior(buttonBehavior);
			}

			@Override
			protected AbstractLink newBaseButton(String markupId, IModel<String> labelModel, IModel<IconType> iconTypeModel) {
				AbstractLink btn = createLink(markupId, Model.of(""), null);
				btn.add(AttributeModifier.append(ATTR_TITLE, new ResourceModel("867")));
				return btn;
			}

			@Override
			protected void onConfigure() {
				super.onConfigure();
				boolean enabled = false;
				if (getSelected().size() == 1) {
					enabled = isDownloadable(getLastSelected());
				}
				setEnabled(enabled);
			}

			public void onDownlownClick(AjaxRequestTarget target, String ext) {
				BaseFileItem fi = getLastSelected();
				File f = ext == null && (Type.IMAGE == fi.getType() || Type.PRESENTATION == fi.getType())
						? fi.getOriginal() : fi.getFile(ext);
				if (f != null && f.exists()) {
					dwnldFile = f;
					String fileExt = OmFileHelper.getFileExt(f.getName());
					dwnldName = fi.getName();
					if (!dwnldName.endsWith(fileExt)) {
						dwnldName += "." + fileExt;
					}
					downloader.initiate(target);
				}
			}
		};
		buttons.setOutputMarkupId(true);
		form.add(buttons.add(download, new ListView<>("other-buttons", newOtherButtons(BUTTON_MARKUP_ID)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<AbstractLink> item) {
				item.add(item.getModelObject());
			}
		}));
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(FileTreePanel.class, "filetree.js")));
	}

	/**
	 * can be overridden by children to provide custom containment
	 * @return custom containment
	 */
	protected String getContainment() {
		return ".file.item.drop.area";
	}

	protected Component getUpload() {
		return upload.setVisible(false);
	}

	private void deleteAll(AjaxRequestTarget target) {
		for (BaseFileItem f : selected.values()) {
			if (!f.isReadOnly()) {
				delete(f, target);
			}
		}
		updateSelected(target); // update nodes
		selected.clear();
		updateSelected(target); // update trash icon
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
			updateTrash(handler);
			if (handler != null) {
				handler.add(createDir, upload);
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
		for (BaseFileItem f : selected.values()) {
			updateNode(target, f);
		}
		updateTrash(target);
	}

	private void updateTrash(IPartialPageRequestHandler handler) {
		final boolean hasDeletable = selected.values().stream()
				.map(BaseFileItem::getId)
				.anyMatch(Objects::nonNull);
		trash.add(AttributeModifier.replace(ATTR_CLASS, TRASH_CLASS + (hasDeletable && !readOnly ? "" : DISABLED_CLASS)));
		if (handler != null) {
			handler.add(trash);
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
			target.add(trash, buttons);
		}
	}

	/**
	 * This method can be overridden to provide more buttons
	 *
	 * @param markupId - markup id for buttons
	 * @return the list of additional buttons
	 */
	protected List<AbstractLink> newOtherButtons(String markupId) {
		return List.of();
	}

	@Override
	protected void onDetach() {
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}
}
