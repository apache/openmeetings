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
package org.apache.openmeetings.web.common.menu;

public class RoomMenuItem extends MenuItem {
	private static final long serialVersionUID = 1L;

	public RoomMenuItem(String name) {
		this(name, null);
	}
	
	public RoomMenuItem(String name, String desc) {
		this(name, desc, true, null);
	}
	
	public RoomMenuItem(String name, String desc, String cssClass) {
		this(name, desc, true, cssClass);
	}
	
	public RoomMenuItem(String name, String desc, boolean enabled) {
		this(name, desc, enabled, null);
	}
	
	public RoomMenuItem(String name, String desc, boolean enabled, String cssClass) {
		super(name, desc);
		setCssClass(cssClass);
		setEnabled(enabled);
	}
}
