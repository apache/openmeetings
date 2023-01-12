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

import java.io.Serializable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.openmeetings.util.OmException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResult implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final ServiceResult UNKNOWN = new ServiceResult(OmException.UNKNOWN.getKey(), Type.ERROR);
	public static final ServiceResult NO_PERMISSION = new ServiceResult("error.notallowed", Type.ERROR);
	private String message;
	private String type;
	@XmlType(namespace="org.apache.openmeetings.db.dto.basic.type")
	public enum Type {
		ERROR
		, SUCCESS
	}

	public ServiceResult() {
		//def constructor
	}

	public ServiceResult(String message, String type) {
		super();
		this.message = message;
		this.type = type;
	}

	public ServiceResult(String message, Type type) {
		this(message, type.name());
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
