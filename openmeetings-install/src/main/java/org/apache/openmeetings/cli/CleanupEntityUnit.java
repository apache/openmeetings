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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.openmeetings.util.OmFileHelper;

public class CleanupEntityUnit extends CleanupUnit {
	private static final long serialVersionUID = 1L;
	private final List<File> invalid;
	private final List<File> deleted;
	private long sizeInvalid = 0;
	private long sizeDeleted = 0;
	protected final int missing;

	public CleanupEntityUnit() {
		invalid = new ArrayList<>();
		deleted = new ArrayList<>();
		missing = 0;
	}

	public CleanupEntityUnit(File parent, List<File> invalid, List<File> deleted, int missing) {
		super(parent);
		this.invalid = invalid;
		this.deleted = deleted;
		this.missing = missing;
		for (File i : invalid) {
			sizeInvalid += OmFileHelper.getSize(i);
		}
		for (File i : deleted) {
			sizeDeleted += OmFileHelper.getSize(i);
		}
	}

	@Override
	public void cleanup() throws IOException {
		for (File i : invalid) {
			FileUtils.deleteQuietly(i);
		}
		for (File i : deleted) {
			FileUtils.deleteQuietly(i);
		}
	}

	public long getSizeInvalid() {
		return sizeInvalid;
	}

	public String getHumanInvalid() {
		return OmFileHelper.getHumanSize(sizeInvalid);
	}

	public long getSizeDeleted() {
		return sizeDeleted;
	}

	public String getHumanDeleted() {
		return OmFileHelper.getHumanSize(sizeDeleted);
	}

	public int getMissing() {
		return missing;
	}
}
