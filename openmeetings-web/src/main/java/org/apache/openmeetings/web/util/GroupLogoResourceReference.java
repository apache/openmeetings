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
package org.apache.openmeetings.web.util;

import static org.apache.openmeetings.util.OmFileHelper.PNG_MIME_TYPE;
import static org.apache.openmeetings.util.OmFileHelper.getGroupLogo;

import java.io.File;
import java.io.IOException;

import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.resource.FileSystemResourceReference;
import org.apache.wicket.util.string.StringValue;

public class GroupLogoResourceReference extends FileSystemResourceReference {
	private static final long serialVersionUID = 1L;

	public GroupLogoResourceReference() {
		super(GroupLogoResourceReference.class, "grouplogo");
	}

	@Override
	public IResource getResource() {
		return new FileSystemResource() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getMimeType() throws IOException {
				return PNG_MIME_TYPE;
			}

			@Override
			protected ResourceResponse newResourceResponse(Attributes attributes) {
				PageParameters params = attributes.getParameters();
				StringValue _id = params.get("id");
				Long id = null;
				try {
					id = _id.toOptionalLong();
				} catch (Exception e) {
					//no-op expected
				}
				return createResourceResponse(getGroupLogo(id, true).toPath());
			}
		};
	}

	public static String getUrl(RequestCycle rc, Long groupId) {
		PageParameters pp = new PageParameters();
		if (groupId != null) {
			pp.add("id", groupId);
		}
		File img = getGroupLogo(groupId, true);
		return rc.urlFor(new GroupLogoResourceReference(), pp.add("anticache", img.lastModified())).toString();
	}

}
