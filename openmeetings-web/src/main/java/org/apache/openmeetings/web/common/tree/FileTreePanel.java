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

import static org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;
import static org.apache.openmeetings.web.app.Application.getBean;
import static org.apache.openmeetings.web.app.WebSession.getUserId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.apache.openmeetings.db.dao.file.FileExplorerItemDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.FileExplorerItem;
import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.web.common.AddFolderDialog;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder;
import org.apache.openmeetings.web.common.ConfirmableAjaxBorder.ConfirmableBorderDialog;
import org.apache.openmeetings.web.util.AjaxDownload;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.ui.form.button.AjaxSplitButton;
import com.googlecode.wicket.jquery.ui.interaction.droppable.Droppable;
import com.googlecode.wicket.jquery.ui.interaction.droppable.DroppableBehavior;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;

public abstract class FileTreePanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final static String ALIGN_LEFT_CLASS = " align-left";
	private final static String ALIGN_RIGHT_CLASS = " align-right";
	private final static String BASE_CLASS = " om-icon big clickable";
	private final static String UPLOAD_CLASS = "add" + BASE_CLASS + ALIGN_LEFT_CLASS;
	private final static String CREATE_DIR_CLASS = "folder-create" + BASE_CLASS + ALIGN_LEFT_CLASS;
	private final static String TRASH_CLASS = "trash" + BASE_CLASS + ALIGN_RIGHT_CLASS;
	private final static String DISABLED_CLASS = " disabled";
	final WebMarkupContainer trees = new WebMarkupContainer("tree-container");
	private final WebMarkupContainer sizes = new WebMarkupContainer("sizes");
	private FileItem lastSelected = null;
	private Map<String, FileItem> selected = new HashMap<>();
	final AjaxDownload downloader = new AjaxDownload();
	protected final IModel<String> homeSize = Model.of((String)null);
	protected final IModel<String> publicSize = Model.of((String)null);
	final ConvertingErrorsDialog errorsDialog = new ConvertingErrorsDialog("errors", Model.of((Recording)null));
	final FileItemTree tree;
	final AjaxSplitButton download = new AjaxSplitButton("download", new ArrayList<IMenuItem>()) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onSubmit(AjaxRequestTarget target, IMenuItem item) {
			item.onClick(target);
		}
	};
	private final Form<Void> form = new Form<Void>("form");
	private final AddFolderDialog addFolder;
	private final ConfirmableBorderDialog trashConfirm;
	private ConfirmableAjaxBorder trashBorder;
	private final Long roomId;
	private final OmTreeProvider tp;
	private boolean readOnly = true;
	private final Component createDir = new WebMarkupContainer("create").add(new AjaxEventBehavior("click") {
		private static final long serialVersionUID = 1L;

		@Override
		protected void onEvent(AjaxRequestTarget target) {
			addFolder.open(target);
		}
	});
	private final Component upload = new WebMarkupContainer("upload");

	public FileTreePanel(String id, Long roomId, AddFolderDialog addFolder, ConfirmableBorderDialog trashConfirm) {
		super(id);
		this.roomId = roomId;
		this.addFolder = addFolder;
		this.trashConfirm = trashConfirm;
		tp = new OmTreeProvider(roomId);
		select(tp.getRoot(), null, false, false);
		form.add(tree = new FileItemTree("tree", this, tp));
		form.add(download.setVisible(false).setOutputMarkupPlaceholderTag(true));
		add(form.add(downloader));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		download.setDefaultModelObject(newDownloadMenuList());
		Droppable<FileItem> trashToolbar = new Droppable<FileItem>("trash-toolbar") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onConfigure(JQueryBehavior behavior) {
				super.onConfigure(behavior);
				behavior.setOption("hoverClass", Options.asString("ui-state-hover trash-toolbar-hover"));
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
								String dialogId = UUID.randomUUID().toString();

								String statement = "var $drop = $(this);";
								statement += "$('body').append('<div id=" + dialogId + ">" + getString("713") + "</div>');";
								statement += "$( '#" + dialogId
										+ "' ).dialog({ title: '" + escapeEcmaScript(getString("80")) + "', dialogClass: 'no-close', buttons: [";
								statement += "	{ text: '" + escapeEcmaScript(getString("54")) + "', click: function() { $drop.append(ui.draggable); $(this).dialog('close'); " + super.getCallbackFunctionBody(parameters) + " } },";
								statement += "	{ text: '" + escapeEcmaScript(getString("25")) + "', click: function() { $(this).dialog('close'); } } ";
								statement += "],";
								statement += "close: function(event, ui) { $(this).dialog('destroy').remove(); }";
								statement += "});";

								return statement;
							}
						};
					}
				};
			}

			@Override
			public void onDrop(AjaxRequestTarget target, Component component) {
				Object o = component.getDefaultModelObject();
				if (o instanceof FileItem) {
					FileItem f = (FileItem)o;
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
		trashToolbar.add(new WebMarkupContainer("refresh").add(new AjaxEventBehavior("click") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				update(target);
			}
		}));
		trashToolbar.add(trashBorder = new ConfirmableAjaxBorder("trash", getString("80"), getString("713"), trashConfirm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean isClickable() {
				return !readOnly && !selected.isEmpty();
			}

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				super.onEvent(target);
			}

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				deleteAll(target);
			}
		});

		form.add(trees.add(tree).setOutputMarkupId(true));
		updateSizes();
		form.add(sizes.add(new Label("homeSize", homeSize), new Label("publicSize", publicSize)).setOutputMarkupId(true));
		form.add(errorsDialog);
		setReadOnly(false, null);
	}

	protected String getContainment() {
		return ".file.item.drop.area";
	}

	protected Component getUpload() {
		return upload.setVisible(false);
	}

	private void deleteAll(AjaxRequestTarget target) {
		for (Entry<String, FileItem> e : selected.entrySet()) {
			delete(e.getValue(), target);
		}
		selected.clear();
	}

	void delete(FileItem f, IPartialPageRequestHandler handler) {
		Long id = f.getId();
		if (id != null) {
			if (f instanceof Recording) {
				getBean(RecordingDao.class).delete((Recording)f);
			} else {
				getBean(FileExplorerItemDao.class).delete((FileExplorerItem)f);
			}
		}
		update(handler);
	}

	public void setReadOnly(boolean readOnly, IPartialPageRequestHandler handler) {
		if (this.readOnly != readOnly) {
			this.readOnly = readOnly;
			tp.refreshRoots(!readOnly);
			createDir.setEnabled(!readOnly);
			createDir.add(AttributeModifier.replace("class", new StringBuilder(CREATE_DIR_CLASS).append(readOnly ? DISABLED_CLASS : "")));
			upload.add(AttributeModifier.replace("class", new StringBuilder(UPLOAD_CLASS).append(readOnly ? DISABLED_CLASS : "")));
			trashBorder.add(AttributeModifier.replace("class", new StringBuilder(TRASH_CLASS).append(readOnly ? DISABLED_CLASS : "")));
			if (handler != null) {
				handler.add(createDir, upload, trashBorder);
			}
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	protected abstract void update(AjaxRequestTarget target, FileItem f);

	public void createFolder(AjaxRequestTarget target, String name) {
		FileItem p = lastSelected;
		boolean isRecording = p instanceof Recording;
		FileItem f = isRecording ? new Recording() : new FileExplorerItem();
		f.setName(name);
		f.setHash(UUID.randomUUID().toString());
		f.setInsertedBy(getUserId());
		f.setInserted(new Date());
		f.setType(Type.Folder);
		f.setOwnerId(p.getOwnerId());
		//TODO lastSelected.parent??
		f.setParentId(Type.Folder == p.getType() ? p.getId() : null);
		if (isRecording) {
			Recording r = (Recording)f;
			r.setGroupId(((Recording)p).getGroupId());
			getBean(RecordingDao.class).update(r);
		} else {
			f.setRoomId(p.getRoomId());
			getBean(FileExplorerItemDao.class).update((FileExplorerItem)f);
		}
		update(target);
	}

	public abstract void updateSizes();

	public boolean isSelected(FileItem f) {
		return selected.containsKey(f.getHash());
	}

	public Map<String, FileItem> getSelected() {
		return selected;
	}

	public FileItem getLastSelected() {
		return lastSelected;
	}

	public void update(IPartialPageRequestHandler handler) {
		updateSizes();
		handler.add(sizes, trees);
	}

	private void updateSelected(AjaxRequestTarget target) {
		for (Entry<String, FileItem> e : selected.entrySet()) {
			updateNode(target, e.getValue());
		}
	}

	void updateNode(AjaxRequestTarget target, FileItem fi) {
		if (fi != null && target != null) {
			if (Type.Folder == fi.getType()) {
				tree.updateBranch(fi, Optional.of(target));
			} else {
				tree.updateNode(fi, Optional.of(target));
			}
		}
	}

	private static boolean sameParent(Long roomId, FileItem f1, FileItem f2) {
		if (f1 instanceof Recording && f2 instanceof FileExplorerItem) {
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
			if (f2 instanceof FileExplorerItem && roomId != null && f1.getRoomId() == null && f2.getRoomId() == null && f1.getOwnerId() == null && f2.getOwnerId() == null) {
				return true;
			}
		}
		return false;
	}

	public void select(FileItem fi, AjaxRequestTarget target, boolean shift, boolean ctrl) {
		updateSelected(target);
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
			for (FileItem f : ((OmTreeProvider)tree.getProvider()).getByParent(fi, fi.getParentId())) {
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
		updateSelected(target);
		if (target != null) {
			target.add(trashBorder, download.setVisible(lastSelected.getType() == Type.Presentation || lastSelected.getType() == Type.Image));
		}
	}

	@Override
	protected void onDetach() {
		homeSize.detach();
		publicSize.detach();
		super.onDetach();
	}

	private List<IMenuItem> newDownloadMenuList() {
		List<IMenuItem> list = new ArrayList<>();

		//original
		list.add(new DownloadMenuItem(getString("files.download.original"), this, null));
		//pdf
		list.add(new DownloadMenuItem(getString("files.download.pdf"), this, EXTENSION_PDF));
		//jpg
		list.add(new DownloadMenuItem(getString("files.download.jpg"), this, EXTENSION_JPG));
		return list;
	}
}
