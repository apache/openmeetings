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
package org.apache.openmeetings.screenshare;

import static org.red5.io.IoConstants.FLAG_CODEC_SCREEN;
import static org.red5.io.IoConstants.FLAG_FRAMETYPE_INTERFRAME;
import static org.red5.io.IoConstants.FLAG_FRAMETYPE_KEYFRAME;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.openmeetings.screenshare.gui.ScreenDimensions;
import org.red5.server.net.rtmp.event.VideoData;

public class ScreenV1Encoder extends BaseScreenEncoder {
	private int[][] last = null;
	private static final int DEFAULT_BLOCK_SIZE = 32;
	private static final int DEFAULT_SCREEN_WIDTH = 1920;
	private static final int DEFAULT_SCREEN_HEIGHT = 1080;
	private int keyFrameIndex;
	private int frameCount = 0;
	private int blockSize = DEFAULT_BLOCK_SIZE;
	private ByteArrayOutputStream ba = new ByteArrayOutputStream(50 + 3 * DEFAULT_SCREEN_WIDTH * DEFAULT_SCREEN_HEIGHT);
	private byte[] areaBuf = null;
	private Deflater d = new Deflater(Deflater.DEFAULT_COMPRESSION);
	private byte[] zipBuf = null;
	private VideoData unalteredFrame = null;
	private final ScreenDimensions dim;

	public ScreenV1Encoder(ScreenDimensions dim) {
		this.dim = dim;
		this.keyFrameIndex = 3 * dim.getFps();
		if (blockSize < 16 || blockSize > 256 || blockSize % 16 != 0) {
			throw new RuntimeException("Invalid block size passed: " + blockSize + " should be: 'from 16 to 256 in multiples of 16'");
		}

		areaBuf = new byte[3 * blockSize * blockSize];
		zipBuf = new byte[3 * blockSize * blockSize];
	}

	private static VideoData getData(byte[] data) {
		IoBuffer buf = IoBuffer.allocate(data.length);
		buf.clear();
		buf.put(data);
		buf.flip();
		return new VideoData(buf);
	}

	@Override
	public void createUnalteredFrame() throws IOException {
		if (last == null) {
			return;
		}
		if (unalteredFrame == null) {
			ByteArrayOutputStream arr = new ByteArrayOutputStream(200);

			Rectangle _area = new Rectangle(dim.getResizeX(), dim.getResizeY());
			//header
			arr.write(getTag(FLAG_FRAMETYPE_INTERFRAME, FLAG_CODEC_SCREEN));
			writeShort(arr, _area.width + ((blockSize / 16 - 1) << 12));
			writeShort(arr, _area.height + ((blockSize / 16 - 1) << 12));
			Rectangle area = getNextBlock(_area, null);
			while (area.width > 0 && area.height > 0) {
				writeShort(arr, 0);
				area = getNextBlock(_area, area);
			}
			unalteredFrame = getData(arr.toByteArray());
		}
	}

	@Override
	public VideoData getUnalteredFrame() {
		if (unalteredFrame != null && (frameCount % keyFrameIndex) != 0) {
			frameCount++;
		}
		return unalteredFrame;
	}

	@Override
	public synchronized VideoData encode(int[][] img) throws IOException {
		ba.reset();
		Rectangle imgArea = new Rectangle(img.length, img[0].length);
		Rectangle area = getNextBlock(imgArea, null);
		boolean isKeyFrame = (frameCount++ % keyFrameIndex) == 0 || last == null;

		//header
		ba.write(getTag(isKeyFrame ? FLAG_FRAMETYPE_KEYFRAME : FLAG_FRAMETYPE_INTERFRAME, FLAG_CODEC_SCREEN));
		writeShort(ba, imgArea.width + ((blockSize / 16 - 1) << 12));
		writeShort(ba, imgArea.height + ((blockSize / 16 - 1) << 12));

		while (area.width > 0 && area.height > 0) {
			writeBytesIfChanged(ba, isKeyFrame, img, area);
			area = getNextBlock(imgArea, area);
		}
		last = img;
		return getData(ba.toByteArray());
	}

	@Override
	public void reset() {
		last = null;
		unalteredFrame = null;
	}

	private Rectangle getNextBlock(Rectangle img, Rectangle _prev) {
		Rectangle prev;
		if (_prev == null) {
			prev = new Rectangle(0, Math.max(0, img.height - blockSize), blockSize, blockSize);
		} else {
			prev = new Rectangle(_prev);
			if (Double.compare(0. + prev.x + prev.width, img.getWidth()) == 0) {
				if (prev.y == 0) {
					return new Rectangle(); //the end of the image
				}
				//next row
				prev.x = 0; //reset position
				prev.width = blockSize; //reset width
				prev.height = prev.y > blockSize ? blockSize : prev.y;
				prev.y -= prev.height;
			} else {
				prev.x += blockSize;
			}
		}
		return img.intersection(prev);
	}

	private void writeBytesIfChanged(ByteArrayOutputStream ba, boolean isKeyFrame, int[][] img, Rectangle area) throws IOException {
		boolean changed = isKeyFrame;
		int count = 0;
		for (int y = area.y + area.height - 1; y >= area.y; --y) {
			for (int x = area.x; x < area.x + area.width; ++x) {
				int pixel = img[x][y];
				if (!changed && (last == null || pixel != last[x][y])) {
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
		return ((frame & 0x0F) << 4) + (codec & 0x0F);
	}

	private static void writeShort(OutputStream os, final int n) throws IOException {
		os.write((n >> 8) & 0xFF);
		os.write( n       & 0xFF);
	}

	public static int[][] getImage(ScreenDimensions dim, Rectangle screen, Robot robot) {
		int[][] buffer = new int[dim.getResizeX()][dim.getResizeY()];
		BufferedImage image = resize(robot.createScreenCapture(screen), new Rectangle(dim.getResizeX(), dim.getResizeY()));
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				buffer[x][y] = image.getRGB(x, y);
			}
		}
		return buffer;
	}
}
