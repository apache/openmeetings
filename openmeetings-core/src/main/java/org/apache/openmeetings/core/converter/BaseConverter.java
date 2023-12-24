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
package org.apache.openmeetings.core.converter;

import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.openmeetings.util.CalendarHelper.formatMillis;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.getPublicDir;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingChunk;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsSubDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioBitrate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioRate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getVideoPreset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.dao.record.RecordingChunkDao;
import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingChunk;
import org.apache.openmeetings.db.entity.record.RecordingChunk.Status;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;


public abstract class BaseConverter {
	private static final Logger log = LoggerFactory.getLogger(BaseConverter.class);
	private static final Pattern p = Pattern.compile("\\d{2,5}(x)\\d{2,5}");
	public static final String EXEC_EXT = System.getProperty("os.name").toUpperCase(Locale.ROOT).indexOf("WINDOWS") < 0 ? "" : ".exe";
	private static final int MINUTE_MULTIPLIER = 60 * 1000;
	public static final int TIME_TO_WAIT_FOR_FRAME = 5 * MINUTE_MULTIPLIER;
	public static final double HALF_STEP = 1. / 2;

	@Inject
	protected ConfigurationDao cfgDao;
	@Inject
	protected RecordingChunkDao chunkDao;
	@Inject
	protected FileItemLogDao logDao;
	@Inject
	protected RecordingDao recordingDao;

	protected record Dimension(int width, int height) {}

	private String getPath(String key, String app) {
		final String cfg = cfgDao.getString(key, "");
		StringBuilder path = new StringBuilder(cfg);
		if (!Strings.isEmpty(path) && !cfg.endsWith(File.separator)) {
			path.append(File.separator);
		}
		path.append(app).append(EXEC_EXT);
		return path.toString();
	}

	public String getPathToFFMPEG() {
		return getPath(CONFIG_PATH_FFMPEG, "ffmpeg");
	}

	protected String getPathToSoX() {
		return getPath(CONFIG_PATH_SOX, "sox");
	}

	protected String getPathToConvert() {
		return getPath(CONFIG_PATH_IMAGEMAGIC, "convert");
	}

	protected File getStreamFolder(Recording recording) {
		return getStreamsSubDir(recording.getRoomId());
	}

	protected long diff(Date from, Date to) {
		return from == null || to == null ? 0 : from.getTime() - to.getTime();
	}

	protected double diffSeconds(Date from, Date to) {
		return diffSeconds(diff(from, to));
	}

	protected double diffSeconds(long val) {
		return ((double)val) / 1000;
	}

	protected void updateDuration(Recording r) {
		r.setDuration(formatMillis(diff(r.getRecordEnd(), r.getRecordStart())));
	}

	protected void deleteFileIfExists(File f) throws IOException {
		Files.deleteIfExists(f.toPath());
	}

	private List<String> mergeAudioToWaves(List<File> waveFiles, File wav) throws IOException {
		List<String> argv = new ArrayList<>();

		argv.add(getPathToSoX());
		argv.add("-m");
		for (File arg : waveFiles) {
			argv.add(arg.getCanonicalPath());
		}
		argv.add(wav.getCanonicalPath());

		return argv;
	}

	protected void createWav(Recording r, ProcessResultList logs, File streamFolder, List<File> waveFiles, File wav, List<RecordingChunk> chunks) throws IOException {
		deleteFileIfExists(wav);
		stripAudioFirstPass(r, logs, waveFiles, streamFolder, chunks == null ? chunkDao.getNotScreenChunksByRecording(r.getId()) : chunks);
		if (waveFiles.isEmpty()) {
			// create default Audio to merge it. strip to content length
			String oneSecWav = new File(getPublicDir(), "one_second.wav").getCanonicalPath();

			// Calculate delta at beginning
			double duration = diffSeconds(r.getRecordEnd(), r.getRecordStart());

			List<String> cmd = List.of(getPathToSoX(), oneSecWav, wav.getCanonicalPath(), "pad", "0", String.valueOf(duration));

			logs.add(ProcessHelper.exec("generateSampleAudio", cmd));
		} else if (waveFiles.size() == 1) {
			copyFile(waveFiles.get(0), wav);
		} else {
			logs.add(ProcessHelper.exec("mergeAudioToWaves", mergeAudioToWaves(waveFiles, wav)));
		}
	}

	private List<String> addSoxPad(ProcessResultList logs, String job, double length, double position, File inFile, File outFile) throws IOException {
		if (length < 0 || position < 0) {
			log.debug("::addSoxPad {} Invalid parameters: length = {}; position = {}; inFile = {}", job, length, position, inFile);
		}
		List<String> argv = List.of(getPathToSoX(), inFile.getCanonicalPath(), outFile.getCanonicalPath(), "pad"
				, String.valueOf(length < 0 ? 0 : length)
				, String.valueOf(position < 0 ? 0 : position));

		logs.add(ProcessHelper.exec(job, argv));
		return argv;
	}

	public static void printChunkInfo(RecordingChunk chunk, String prefix) {
		if (log.isDebugEnabled()) {
			log.debug("### {}:: recording id {}; stream with id {}; current status: {} ", prefix, chunk.getRecording().getId()
					, chunk.getId(), chunk.getStreamStatus());
			File chunkFlv = getRecordingChunk(chunk.getRecording().getRoomId(), chunk.getStreamName());
			log.debug("### {}:: Chunk file [{}] exists ? {}; size: {}, lastModified: {} ", prefix, chunkFlv.getPath(), chunkFlv.exists(), chunkFlv.length(), chunkFlv.lastModified());
		}
	}

	protected RecordingChunk waitForTheStream(long chunkId) {
		RecordingChunk chunk = null;
		try {
			long counter = 0;
			long maxTimestamp = 0;
			while (true) {
				chunk = chunkDao.get(chunkId);

				if (chunk.getStreamStatus() == Status.STOPPED) {
					printChunkInfo(chunk, "Stream now written");
					log.debug("### Chunk stopped, unblocking thread ... " );
					break;
				}
				File chunkFlv = getRecordingChunk(chunk.getRecording().getRoomId(), chunk.getStreamName());
				if (chunkFlv.exists() && maxTimestamp < chunkFlv.lastModified()) {
					maxTimestamp = chunkFlv.lastModified();
				}
				if (maxTimestamp + TIME_TO_WAIT_FOR_FRAME < System.currentTimeMillis()) {
					log.debug("### long time without any update, closing ... ");
					chunk.setStreamStatus(Status.STOPPED);
					chunkDao.update(chunk);
					break;
				}
				if (++counter % 1000 == 0) {
					printChunkInfo(chunk, "Still waiting");
				}

				log.trace("### Stream not yet written Thread Sleep - {}", chunkId);
				Thread.sleep(100L);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return chunk;
	}

	private void stripAudioFirstPass(Recording recording,
			ProcessResultList logs,
			List<File> waveFiles, File streamFolder,
			List<RecordingChunk> chunks) {
		try {
			// Init variables
			log.debug("### Chunks count - {}", chunks.size());
			log.debug("###################################################");

			for (RecordingChunk chunk : chunks) {
				long chunkId = chunk.getId();
				log.debug("### processing chunk: {}", chunkId);
				if (chunk.getStreamStatus() == Status.NONE) {
					log.debug("Stream has not been started, error in recording {}", chunkId);
					continue;
				}

				chunk = waitForTheStream(chunkId);

				File inputFlvFile = getRecordingChunk(chunk.getRecording().getRoomId(), chunk.getStreamName());

				File outputWav = new File(streamFolder, chunk.getStreamName() + "_WAVE.wav");

				log.debug("FLV File Name: {} Length: {} ", inputFlvFile.getName(), inputFlvFile.length());

				if (inputFlvFile.exists()) {
					List<String> argv = List.of(
							getPathToFFMPEG(), "-y"
							, "-i", inputFlvFile.getCanonicalPath()
							, "-af", String.format("aresample=%s:min_comp=0.001:min_hard_comp=0.100000", getAudioBitrate())
							, outputWav.getCanonicalPath());
					//there might be no audio in the stream
					logs.add(ProcessHelper.exec("stripAudioFromFLVs", argv, true));
				}

				if (outputWav.exists() && outputWav.length() != 0) {
					// Strip Wave to Full Length
					// Strip Wave to Full Length
					String hashFileFullName = chunk.getStreamName() + "_FULL_WAVE.wav";
					File outputFullWav = new File(streamFolder, hashFileFullName);

					// Calculate delta at beginning
					double startPad = diffSeconds(chunk.getStart(), recording.getRecordStart());

					// Calculate delta at ending
					double endPad = diffSeconds(recording.getRecordEnd(), chunk.getEnd());

					addSoxPad(logs, "addStartEndToAudio", startPad, endPad, outputWav, outputFullWav);

					// Fix for Audio Length - Invalid Audio Length in Recorded Files
					// Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");

					if (!outputFullWav.exists()) {
						throw new ConversionException("Audio File does not exist , could not extract the Audio correctly");
					}

					// Finally add it to the row!
					waveFiles.add(outputFullWav);
				}
				chunkDao.update(chunk);
			}
		} catch (Exception err) {
			log.error("[stripAudioFirstPass]", err);
		}
	}

	protected String getDimensions(Recording r, char delim) {
		return String.format("%s%s%s", r.getWidth(), delim, r.getHeight());
	}

	protected String getDimensions(Recording r) {
		return getDimensions(r, 'x');
	}

	/**
	 * This method should be overridden to supply any additional parameters
	 *
	 * @param r - recording to get params from
	 * @return additional conversion parameters
	 */
	protected List<String> additionalMp4OutParams(Recording r) {
		return List.of();
	}

	private List<String> addMp4OutParams(Recording r, List<String> argv, boolean interview, String mp4path) {
		argv.addAll(List.of(
				"-c:v", "h264" //
				, "-crf", "24"
				, "-vsync", "0"
				, "-pix_fmt", "yuv420p"
				, "-preset", getVideoPreset()
				, "-profile:v", "baseline"
				, "-level", "3.0"
				, "-movflags", "faststart"
				, "-c:a", "aac"
				, "-ar", String.valueOf(getAudioRate())
				, "-b:a", getAudioBitrate()
				));
		if (!interview) {
			argv.addAll(List.of("-vf", "pad=ceil(iw/2)*2:ceil(ih/2)*2"));
		}
		argv.addAll(additionalMp4OutParams(r));
		argv.add(mp4path);
		return argv;
	}

	protected String convertToMp4(Recording r, List<String> inArgv, boolean interview, ProcessResultList logs) throws IOException {
		String mp4path = r.getFile().getCanonicalPath();
		List<String> argv = new ArrayList<>(List.of(getPathToFFMPEG(), "-y"));
		argv.addAll(inArgv);
		logs.add(ProcessHelper.exec("generate MP4", addMp4OutParams(r, argv, interview, mp4path)));
		return mp4path;
	}

	protected void convertToPng(BaseFileItem f, String mp4path, ProcessResultList logs) throws IOException {
		// Extract first Image for preview purpose
		// ffmpeg -i movie.mp4 -vf  "thumbnail,scale=640:-1" -frames:v 1 movie.png
		File png = f.getFile(EXTENSION_PNG);
		List<String> argv = List.of(
				getPathToFFMPEG(), "-y"
				, "-i", mp4path
				, "-vf", "thumbnail,scale=640:-1"
				, "-frames:v", "1"
				, png.getCanonicalPath());
		logs.add(ProcessHelper.exec("generate preview PNG :: " + f.getHash(), argv));
	}

	/**
	 * Parse the width height from the FFMPEG output
	 *
	 * @param txt FFMPEG output
	 * @return {@link Dimension} parsed
	 */
	protected static Dimension getDimension(String txt, Dimension def) {
		Matcher matcher = p.matcher(txt);

		if (matcher.find()) {
			String foundResolution = txt.substring(matcher.start(), matcher.end());
			String[] resolutions = foundResolution.split("x");
			return new Dimension(toInt(resolutions[0]), toInt(resolutions[1]));
		}

		return def;
	}

	protected void finalizeRec(Recording r, String mp4path, ProcessResultList logs) throws IOException {
		convertToPng(r, mp4path, logs);

		updateDuration(r);
		r.setStatus(Recording.Status.PROCESSED);
	}

	protected void postProcess(Recording r, ProcessResultList logs) {
		logDao.delete(r);
		for (ProcessResult res : logs.getJobs()) {
			logDao.add("generateFFMPEG", r, res);
		}
	}

	protected void postProcess(List<File> waveFiles) {
		// Delete Wave Files
		for (File audio : waveFiles) {
			try {
				Files.deleteIfExists(audio.toPath());
			} catch (IOException e) {
				log.error("Unexpected error while deleting waveFile {}", audio, e);
			}
		}
	}
}
