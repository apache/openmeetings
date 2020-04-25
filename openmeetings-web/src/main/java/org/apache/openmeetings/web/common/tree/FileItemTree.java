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

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.file.BaseFileItem.Type;
import org.apache.openmeetings.web.util.TouchPunchResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FileItemTree extends DefaultNestedTree<BaseFileItem> {
	private static final long serialVersionUID = 1L;
	final FileTreePanel treePanel;

	public FileItemTree(String id, FileTreePanel treePanel, ITreeProvider<BaseFileItem> tp) {
		super(id, tp);
		this.treePanel = treePanel;
		setItemReuseStrategy(new ReuseIfModelsEqualStrategy());
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
	protected Component newContentComponent(String id, IModel<BaseFileItem> lm) {
		BaseFileItem r = lm.getObject();
		return Type.FOLDER == r.getType() || r.getId() == null
				? new FolderPanel(id, lm, treePanel)
				: new FileItemPanel(id, lm, treePanel);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(TouchPunchResourceReference.instance()));
	}
}
