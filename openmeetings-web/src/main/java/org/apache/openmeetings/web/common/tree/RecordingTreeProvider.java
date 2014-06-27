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

import static org.apache.openmeetings.web.app.Application.getBean;

import java.util.Iterator;

import org.apache.openmeetings.db.dao.record.FlvRecordingDao;
import org.apache.openmeetings.db.entity.file.FileItem.Type;
import org.apache.openmeetings.db.entity.record.FlvRecording;
import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class RecordingTreeProvider implements ITreeProvider<FlvRecording> {
	private static final long serialVersionUID = 1L;

	public void detach() {
		// TODO LDM should be used
	}

	public boolean hasChildren(FlvRecording node) {
		return node.getId() <= 0 || Type.Folder == node.getType();
	}

	public Iterator<? extends FlvRecording> getChildren(FlvRecording node) {
		return getBean(FlvRecordingDao.class).getFlvRecordingByParent(node.getId()).iterator();
	}

	public IModel<FlvRecording> model(FlvRecording object) {
		// TODO LDM should be used
		return Model.of(object);
	}
	
}
