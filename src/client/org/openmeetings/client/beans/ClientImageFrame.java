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
package org.openmeetings.client.beans;

import java.awt.Rectangle;

/**
 * @author sebastianwagner
 *
 */
public class ClientImageFrame {
	
	private byte[] payload;
	private Rectangle rect;
	
	/**
	 * @param rect
	 * @param payload
	 *
	 * 12.09.2009 11:19:43
	 * sebastianwagner
	 * 
	 */
	public ClientImageFrame(Rectangle rect, byte[] payload) {
		super();
		this.payload = payload;
		this.rect = rect;
	}
	
	
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	public Rectangle getRect() {
		return rect;
	}
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

}
