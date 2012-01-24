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

import java.awt.Image;
import java.awt.Rectangle;

/**
 * @author sebastianwagner
 *
 */
public class ClientImageFrameModus {
	 
	private Image image;
	private byte[] payload;
	private Rectangle rect;
	private int originalRectWidth;
	private int originalRectHeight;
	
	
	
	/**
	 * @param image
	 * @param payload
	 * @param rect
	 * @param originalRectWidth
	 * @param originalRectHeight
	 *
	 * 27.09.2009 17:25:52
	 * sebastianwagner
	 * 
	 * 
	 */
	public ClientImageFrameModus(Image image, byte[] payload, Rectangle rect,
			int originalRectWidth, int originalRectHeight) {
		super();
		this.image = image;
		this.payload = payload;
		this.rect = rect;
		this.originalRectWidth = originalRectWidth;
		this.originalRectHeight = originalRectHeight;
	}
	public int getOriginalRectWidth() {
		return originalRectWidth;
	}
	public void setOriginalRectWidth(int originalRectWidth) {
		this.originalRectWidth = originalRectWidth;
	}
	public int getOriginalRectHeight() {
		return originalRectHeight;
	}
	public void setOriginalRectHeight(int originalRectHeight) {
		this.originalRectHeight = originalRectHeight;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
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
