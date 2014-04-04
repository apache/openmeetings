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
package org.apache.openmeetings.batik.beans;

import java.util.List;

public class PrintBean {

	public String hash;
	@SuppressWarnings("rawtypes")
	public List map;
	public int width;
	public int height;

	public PrintBean(String hash, @SuppressWarnings("rawtypes") List map,
			int width, int height) {
		super();
		this.hash = hash;
		this.height = height;
		this.map = map;
		this.width = width;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@SuppressWarnings("rawtypes")
	public List getMap() {
		return map;
	}

	@SuppressWarnings("rawtypes")
	public void setMap(List map) {
		this.map = map;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
