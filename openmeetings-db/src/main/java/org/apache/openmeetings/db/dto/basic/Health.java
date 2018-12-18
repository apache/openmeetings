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
package org.apache.openmeetings.db.dto.basic;

import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Health implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final Health INSTANCE = new Health();
	private boolean inited;
	private boolean installed;
	private boolean dbOk;

	public Health() {
		inited = isInitComplete();
	}

	public boolean isInited() {
		return inited;
	}

	public Health setInited(boolean inited) {
		this.inited = inited;
		return this;
	}

	public boolean isInstalled() {
		return installed;
	}

	public Health setInstalled(boolean installed) {
		this.installed = installed;
		return this;
	}

	public boolean isDbOk() {
		return dbOk;
	}

	public Health setDbOk(boolean dbOk) {
		this.dbOk = dbOk;
		return this;
	}
}
