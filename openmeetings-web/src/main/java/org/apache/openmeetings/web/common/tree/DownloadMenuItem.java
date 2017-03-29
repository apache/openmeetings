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

import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_JPG;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PDF;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.openmeetings.db.entity.file.FileItem;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.resource.FileResourceStream;

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
		FileItem fi = tree.getLastSelected();
		File f = fi.getFile(ext);
		if (f != null && f.exists()) {
			if (ext == null && (Type.Image == fi.getType() || Type.Presentation == fi.getType())) {
				File[] ff = f.getParentFile().listFiles(new OriginalFilter(fi));
				if (ff.length > 0) {
					f = ff[0];
				}
			}
			tree.downloader.setFileName(f.getName());
			tree.downloader.setResourceStream(new FileResourceStream(f));
			tree.downloader.initiate(target);
		}
	}

	private static class OriginalFilter implements FileFilter {
		final FileItem fi;
		Set<String> exclusions = new HashSet<>();

		OriginalFilter(FileItem fi) {
			this.fi = fi;
			exclusions.add(EXTENSION_JPG);
			if (Type.Presentation == fi.getType()) {
				exclusions.add(EXTENSION_PDF);
			}
		}

		@Override
		public boolean accept(File f) {
			String n = f.getName();
			String ext = OmFileHelper.getFileExt(n);
			return n.startsWith(fi.getHash()) && !exclusions.contains(ext);
		}
	}
}
