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

import java.io.File;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.wicket.ajax.AjaxRequestTarget;

import com.googlecode.wicket.jquery.ui.JQueryIcon;
import com.googlecode.wicket.jquery.ui.widget.menu.MenuItem;

public class DownloadMenuItem extends MenuItem {
	private static final long serialVersionUID = 1L;
	private final String ext;
	private final FileTreePanel tree;

	public DownloadMenuItem(String title, FileTreePanel tree, String ext) {
		super(title, JQueryIcon.ARROWTHICKSTOP_1_S);
		this.ext = ext;
		this.tree = tree;
	}

	@Override
	public boolean isEnabled() {
		File f = null;
		if (tree.getSelected().size() == 1) {
			f = tree.getLastSelected().getFile(ext);
		}
		return f != null && f.exists();
	}

	@Override
	public void onClick(AjaxRequestTarget target) {
		BaseFileItem fi = tree.getLastSelected();
		File f = ext == null && (Type.Image == fi.getType() || Type.Presentation == fi.getType())
				? fi.getOriginal() : fi.getFile(ext);
		if (f != null && f.exists()) {
			tree.dwnldFile = f;
			tree.downloader.initiate(target);
		}
	}
}
