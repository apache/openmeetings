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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

public class ScreenV1Encoder extends BaseScreenEncoder {
	private BufferedImage last = null;
	private static int KEY_FRAME_INDEX = 100;
	private static int DEFAULT_BLOCK_SIZE = 32;
	private static int DEFAULT_SCREEN_WIDTH = 1920;
	private static int DEFAULT_SCREEN_HEIGHT = 1080;
	private int keyFrameIndex;
	private int frameCount = 0;
	private int blockSize;
	private Rectangle screen;
	private ByteArrayOutputStream ba = new ByteArrayOutputStream(50 + 3 * DEFAULT_SCREEN_WIDTH * DEFAULT_SCREEN_HEIGHT);
	private byte[] areaBuf = null;
	private Deflater d = new Deflater(Deflater.DEFAULT_COMPRESSION);
	private byte[] zipBuf = null;
	
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

		areaBuf = new byte[3 * blockSize * blockSize];
		zipBuf = new byte[3 * blockSize * blockSize];
	}
	
	public byte[] encode(Rectangle screen, BufferedImage _img, Rectangle size) throws IOException {
		BufferedImage img = resize(_img, size);
		ba.reset();
		Rectangle imgArea = new Rectangle(img.getWidth(), img.getHeight());
		Rectangle area = getNextBlock(imgArea, null);
		boolean isKeyFrame = (frameCount++ % keyFrameIndex) == 0 || last == null || !screen.equals(this.screen);
		
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
		int count = 0;
		for (int y = area.y + area.height - 1; y >= area.y; --y) {
			for (int x = area.x; x < area.x + area.width; ++x) {
				int pixel = img.getRGB(x, y);
				if (!changed && (last == null || pixel != last.getRGB(x, y))) {
					changed = true;
				}
				areaBuf[count++] = (byte)(pixel & 0xFF);			// Blue component
				areaBuf[count++] = (byte)((pixel >> 8) & 0xFF);		// Green component
				areaBuf[count++] = (byte)((pixel >> 16) & 0xFF);	// Red component
			}
		}
		if (changed) {
			d.reset();
			d.setInput(areaBuf, 0, count);
			d.finish();
			int written = d.deflate(zipBuf);
			writeShort(ba, written);
			ba.write(zipBuf, 0, written);
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
