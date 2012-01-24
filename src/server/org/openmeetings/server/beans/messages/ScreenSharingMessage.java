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
package org.openmeetings.server.beans.messages;

import java.util.List;

/**
 * @author sebastianwagner
 *
 */
public class ScreenSharingMessage {
	
	private String s = null; //publicSID
	private int w;//width
	private int h;//height
	private List<ScreenSharingNewFrame> l;//list
	
	private boolean deleted;
	
	public String getS() {
		return s;
	}
	public void setS(String s) {
		this.s = s;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public List<ScreenSharingNewFrame> getL() {
		return l;
	}
	public void setL(List<ScreenSharingNewFrame> l) {
		this.l = l;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
