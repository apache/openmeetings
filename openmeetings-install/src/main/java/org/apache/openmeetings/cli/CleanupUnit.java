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
package org.apache.openmeetings.cli;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.util.OmFileHelper;

public class CleanupUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	private final File parent;
	private final long sizeTotal;

	public CleanupUnit() {
		parent = new File(".");
		sizeTotal = 0;
	}

	public CleanupUnit(File parent) {
		this.parent = parent;
		if (!parent.isDirectory() || !parent.exists()) {
			throw new RuntimeException("Parent doesn't exist or not directory");
		}
		sizeTotal = OmFileHelper.getSize(parent);
	}

	/**
	 * @throws IOException some of the subclussed can throw
	 */
	public void cleanup() throws IOException {
		for (File f : getParent().listFiles()) {
			FileUtils.deleteQuietly(f);
		}
	}

	public File getParent() {
		return parent;
	}

	public long getSizeTotal() {
		return sizeTotal;
	}

	public String getHumanTotal() {
		return OmFileHelper.getHumanSize(sizeTotal);
	}
}
