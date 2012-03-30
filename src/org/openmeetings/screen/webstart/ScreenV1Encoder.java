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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

public class ScreenV1Encoder implements IScreenEncoder {
	private BufferedImage last = null;
	private static int KEY_FRAME_INDEX = 100;
	private static int DEFAULT_BLOCK_SIZE = 32;
	private int keyFrameIndex;
	private int frameCount = 0;
	private int blockSize;
	private Rectangle screen;
	
	public ScreenV1Encoder() {
		this(KEY_FRAME_INDEX, DEFAULT_BLOCK_SIZE);
	}
	
	//will create square blocks
	public ScreenV1Encoder(int keyFrameIndex, int blockSize) {
		this.keyFrameIndex = keyFrameIndex;
		if (blockSize < 16 || blockSize > 256 || blockSize % 16 != 0) {
			throw new RuntimeException("Invalid block size passed: " + blockSize + " should be: 'from 16 to 256 in multiples of 16'");
		}
		this.blockSize = blockSize;
	}
	
	public BufferedImage resize(BufferedImage _img, Rectangle size) {
		BufferedImage img = _img;
		if (_img.getWidth() != size.width || _img.getHeight() != size.height) {
			img = new BufferedImage(size.width, size.height,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D graphics2D = img.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics2D.drawImage(_img, 0, 0, size.width, size.height, null);
			graphics2D.dispose();
		}
		return img;
	}
	
	public byte[] encode(Rectangle screen, BufferedImage _img, Rectangle size) throws IOException {
		BufferedImage img = resize(_img, size);
		Rectangle imgArea = new Rectangle(img.getWidth(), img.getHeight());
		Rectangle area = getNextBlock(imgArea, null);
		boolean isKeyFrame = (frameCount++ % keyFrameIndex) == 0 || last == null || (screen.equals(this.screen));
		
		ByteArrayOutputStream ba = new ByteArrayOutputStream(50 + 3 * imgArea.width * imgArea.height);
		//header
		ba.write(getTag(isKeyFrame ? 0x01 : 0x02, 0x03));
		writeShort(ba, imgArea.width + ((blockSize / 16 - 1) << 12));
		writeShort(ba, imgArea.height + ((blockSize / 16 - 1) << 12));
		
		while (area.width > 0 && area.height > 0) {
			writeBytesIfChanged(ba, isKeyFrame, img, area);
			area = getNextBlock(imgArea, area);
		}
		this.screen = screen;
		last = img;
		return ba.toByteArray();
	}
	
	public void reset() {
		last = null;
	}
	
	private Rectangle getNextBlock(Rectangle img, Rectangle _prev) {
		Rectangle prev;
		if (_prev == null) {
			prev = new Rectangle(0, Math.max(0, img.height - blockSize), blockSize, blockSize);
		} else {
			prev = new Rectangle(_prev);
			if (prev.x + prev.width == img.getWidth()) {
				if (prev.y == 0) return new Rectangle(); //the end of the image
				//next row
				prev.x = 0; //reset position
				prev.width = blockSize; //reset width
				prev.y -= (prev.y > blockSize ? blockSize : prev.y);
			} else {
				prev.x += blockSize;
			}
		}
		return img.intersection(prev); 
	}

	private void writeBytesIfChanged(ByteArrayOutputStream ba, boolean isKeyFrame, BufferedImage img, Rectangle area) throws IOException {
		boolean changed = isKeyFrame;
		ByteArrayOutputStream baos = new ByteArrayOutputStream(3 * area.width * area.height);
		DeflaterOutputStream dos = new DeflaterOutputStream(baos);
		for (int y = area.y + area.height - 1; y >= area.y; --y) {
			for (int x = area.x; x < area.x + area.width; ++x) {
				int pixel = img.getRGB(x, y);
				if (!changed && pixel != last.getRGB(x, y)) {
					changed = true;
				}
				dos.write(new byte[]{
					(byte)(pixel & 0xFF)				// Blue component
					, (byte)((pixel >> 8) & 0xFF)		// Green component
					, (byte)((pixel >> 16) & 0xFF)		// Red component
				});
			}
		}
		dos.finish();
		if (changed) {
			final int written = baos.size();
			writeShort(ba, written);
			ba.write(baos.toByteArray(), 0, written);
		} else {
			writeShort(ba, 0);
		}
	}

	public int getTag(final int frame, final int codec) {
		return ((frame & 0x0F) << 4) + ((codec & 0x0F) << 0);
	}
	
	private void writeShort(OutputStream os, final int n) throws IOException {
		os.write((n >> 8) & 0xFF);
		os.write((n >> 0) & 0xFF);
	}
}
