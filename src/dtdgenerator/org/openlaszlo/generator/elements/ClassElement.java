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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class ClassElement implements Comparable<ClassElement> {

	private String name;
	private SortedSet<ClassAttribute> attributes = new TreeSet<ClassAttribute>();
	private String parentAsString; // It is not sure that the parent is already
									// parsed
	private ClassElement parent;
	private boolean isRoot = false;
	private String comment;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentAsString() {
		return parentAsString;
	}

	public void setParentAsString(String parentAsString) {
		this.parentAsString = parentAsString;
	}

	public SortedSet<ClassAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(SortedSet<ClassAttribute> attributes) {
		this.attributes = attributes;
	}

	public ClassElement getParent() {
		return parent;
	}

	public void setParent(ClassElement parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Get all class attributes including the ones from the parent
	 * 
	 * @return
	 */
	public SortedSet<ClassAttribute> getAllClassAttributes() {
		SortedSet<ClassAttribute> attributes = this.attributes;

		if (this.parent != null) {
			attributes.addAll(parent.getAllClassAttributes());
		} else {
			// System.err.println("Has no parent " + this.parentAsString);
		}

		return attributes;
	}

	public void clearDuplicated() {
		if (this.parent != null) {
			SortedSet<ClassAttribute> allParentAttributes = parent
					.getAllClassAttributes();

			for (Iterator<ClassAttribute> iter = attributes.iterator(); iter
					.hasNext();) {
				ClassAttribute attr = iter.next();
				if (allParentAttributes.contains(attr)) {
					iter.remove();
				}
			}

		}
	}
	
	public String getClassRoot() {
		if (this.parent != null) {
			return this.parent.getClassRoot();
		}
		
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ClassElement) {
			ClassElement attr = (ClassElement) obj;
			return attr.getName().equals(name);
		}

		return false;
	}

	public int compareTo(ClassElement attr) {
		return attr.getName().compareTo(name);
	}

}
