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
package org.apache.openmeetings.data.whiteboard.dto;

import java.util.Date;

public class WhiteboardSyncLockObject {

	private String publicSID;
	private boolean isInitialLoaded = false;
	private boolean isCurrentLoadingItem = false;

	private Date addtime;
	private Date starttime;

	public String getPublicSID() {
		return publicSID;
	}

	public void setPublicSID(String publicSID) {
		this.publicSID = publicSID;
	}

	public boolean isInitialLoaded() {
		return isInitialLoaded;
	}

	public void setInitialLoaded(boolean isInitialLoaded) {
		this.isInitialLoaded = isInitialLoaded;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public boolean isCurrentLoadingItem() {
		return isCurrentLoadingItem;
	}

	public void setCurrentLoadingItem(boolean isCurrentLoadingItem) {
		this.isCurrentLoadingItem = isCurrentLoadingItem;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

}
