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
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.util.OmFileHelper;

public abstract class CleanupEntityUnit extends CleanupUnit {
	protected List<File> invalid = new ArrayList<>();
	protected List<File> deleted = new ArrayList<>();
	private long sizeInvalid = 0;
	private long sizeDeleted = 0;
	protected int missing = 0;
	
	public CleanupEntityUnit(File parent) {
		super(parent);
		fill();
		for (File i : invalid) {
			sizeInvalid += OmFileHelper.getSize(i);
		}
		for (File i : deleted) {
			sizeDeleted += OmFileHelper.getSize(i);
		}
	}
	
	public abstract void fill();

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
