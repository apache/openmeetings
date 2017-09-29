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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.red5.logging.Red5LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.resource.FileSystemResource;
import org.apache.wicket.resource.FileSystemResourceReference;
import org.slf4j.Logger;

public abstract class FileItemResourceReference<T extends BaseFileItem> extends FileSystemResourceReference {
	private static final long serialVersionUID = 1L;
	protected static final Logger log = getLogger(FileItemResourceReference.class, getWebAppRootKey());

	public FileItemResourceReference(String name) {
		super(name);
	}

	@Override
	public IResource getResource() {
		return new FileSystemResource() {
			private static final long serialVersionUID = 1L;
			private File file;
			private T r;

			@Override
			protected String getMimeType() throws IOException {
				return FileItemResourceReference.this.getMimeType(r);
			}

			@Override
			protected ResourceResponse newResourceResponse(Attributes attr) {
				r = getFileItem(attr);
				if (r != null) {
					file = getFile(r, attr);
				}
				if (file != null && file.exists()) {
					ResourceResponse rr = createResourceResponse(attr, file.toPath());
					rr.setFileName(getFileName(r));
					return rr;
				} else {
					log.debug("No file item was found");
					ResourceResponse rr = new ResourceResponse();
					rr.setError(HttpServletResponse.SC_NOT_FOUND);
					return rr;
				}
			}
		};
	}

	protected abstract String getMimeType(T r);
	protected abstract String getFileName(T r);
	protected abstract File getFile(T r, Attributes attr);
	protected abstract T getFileItem(Attributes attr);
}
