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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioTone {
	public static final float SAMPLE_RATE = 44100F;

	private AudioTone() {}

	public static void play() {
		byte[] buf = new byte[1];

		AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
		try (SourceDataLine sdl = AudioSystem.getSourceDataLine(af)) {
			sdl.open(af);
			sdl.start();
			for (int i = 0; i < (int)SAMPLE_RATE; ++i) {
				double angle = i / (SAMPLE_RATE / 440) * 2.0 * Math.PI;
				buf[0] = (byte) (Math.sin(angle) * 128);
				sdl.write(buf, 0, 1);
			}
		} catch (LineUnavailableException e) {
			//no-op
		}
	}
}
