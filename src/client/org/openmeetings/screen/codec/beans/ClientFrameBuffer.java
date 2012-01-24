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
package org.openmeetings.screen.codec.beans;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sebastianwagner
 *
 */
public class ClientFrameBuffer {
 
	private List<ClientImageFrameModus> buffer = new LinkedList<ClientImageFrameModus>();
	
	private Rectangle rect;
	private byte index;
	
	
	public List<ClientImageFrameModus> getBuffer() {
		return buffer;
	}
	public void setBuffer(List<ClientImageFrameModus> buffer) {
		this.buffer = buffer;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	public byte getIndex() {
		return index;
	}
	public void setIndex(byte index) {
		this.index = index;
	}
	
}
