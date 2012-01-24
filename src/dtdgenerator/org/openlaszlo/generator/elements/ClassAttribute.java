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
package org.openlaszlo.generator.elements;

public class ClassAttribute implements Comparable<ClassAttribute> {

	private String name;
	private boolean required = false;
	private String type;
	private String defaultValue;
	private String comment;

	public ClassAttribute(String name2, boolean required2, String type2,
			String defaultValue2, String comment2) {
		this.name = name2;
		this.required = required2;
		this.type = type2;
		this.defaultValue = defaultValue2;
		this.comment = comment2;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ClassAttribute) {
			ClassAttribute attr = (ClassAttribute) obj;
			return attr.getName().equals(name);
		}

		return false;
	}

	public int compareTo(ClassAttribute attr) {
		return attr.getName().compareTo(name);
	}

}
