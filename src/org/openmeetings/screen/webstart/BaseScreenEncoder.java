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
package org.openmeetings.screen.webstart;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class BaseScreenEncoder implements IScreenEncoder {

	public BufferedImage resize(BufferedImage _img, Rectangle size) {
		BufferedImage img = _img;
		if (_img.getWidth() != size.width || _img.getHeight() != size.height) {
			img = (BufferedImage)_img.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
		}
		return img;
	}
	
}
