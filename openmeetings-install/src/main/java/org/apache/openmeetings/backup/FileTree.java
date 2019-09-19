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
package org.apache.openmeetings.backup;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.openmeetings.db.entity.file.BaseFileItem;

class FileTree<T extends BaseFileItem> {
	private final T item;
	private final Map<Long, FileTree<T>> children = new HashMap<>();

	FileTree() {
		this(null);
	}

	FileTree(T item) {
		this.item = item;
	}

	boolean add(T child) {
		if (child.getParentId() != null && child.getParentId() < 1) {
			child.setParentId(null);
		}
		if ((item == null && child.getParentId() == null) || (item != null && item.getId().equals(child.getParentId()))) {
			children.put(child.getId(), new FileTree<>(child));
			return true;
		}
		for (Entry<Long, FileTree<T>> e : children.entrySet()) {
			if (e.getValue().add(child)) {
				return true;
			}
		}
		return false;
	}

	void process(Predicate<T> invalid, Consumer<T> consumer) {
		if (item != null) {
			if (invalid.test(item)) {
				return; // we will not process invalid and i's children
			}
			consumer.accept(item);
		}
		if (!children.isEmpty()) {
			children.forEach((id, e) -> e.process(invalid, consumer));
		}
	}

	@Override
	public String toString() {
		String val = "FileTree[type ='" + (item == null ? "root" : item.getType()) + "'";
		if (item != null) {
			val += ", name='" + item.getName() + "'";
		}
		if (!children.isEmpty()) {
			val += ", children='" + children + "'";
		}
		return val + "]";
	}
}
