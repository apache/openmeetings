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
import static org.apache.openmeetings.core.data.record.listener.async.BaseStreamWriter.TIME_TO_WAIT_FOR_FRAME;
import static org.apache.openmeetings.util.CalendarHelper.formatMillis;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_FLV;
import static org.apache.openmeetings.util.OmFileHelper.EXTENSION_PNG;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingMetaData;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsHibernateDir;
import static org.apache.openmeetings.util.OmFileHelper.getStreamsSubDir;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_FFMPEG;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_IMAGEMAGIC;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_PATH_SOX;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioBitrate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getAudioRate;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getVideoPreset;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.file.FileItemLogDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDeltaDao;
import org.apache.openmeetings.db.entity.file.BaseFileItem;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.db.entity.record.RecordingMetaData.Status;
import org.apache.openmeetings.db.entity.record.RecordingMetaDelta;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.red5.io.flv.impl.FLVWriter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(BaseConverter.class, getWebAppRootKey());
	private static final Pattern p = Pattern.compile("\\d{2,5}(x)\\d{2,5}");
	public static final String EXEC_EXT = System.getProperty("os.name").toUpperCase(Locale.ROOT).indexOf("WINDOWS") < 0 ? "" : ".exe";

	@Autowired
	protected ConfigurationDao cfgDao;
	@Autowired
	private RecordingMetaDataDao metaDataDao;
	@Autowired
	private RecordingMetaDeltaDao metaDeltaDao;
	@Autowired
	protected FileItemLogDao logDao;

	public static class Dimension {
		private final int width;
		private final int height;

		public Dimension(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}

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

	protected void deleteFileIfExists(File f) {
		if (f.exists()) {
			f.delete();
		}
	}

	private String[] mergeAudioToWaves(List<File> waveFiles, File wav) throws IOException {
		List<String> argv = new ArrayList<>();

		argv.add(getPathToSoX());
		argv.add("-m");
		for (File arg : waveFiles) {
			argv.add(arg.getCanonicalPath());
		}
		argv.add(wav.getCanonicalPath());

		return argv.toArray(new String[0]);
	}

	protected void createWav(Recording r, ProcessResultList logs, File streamFolder, List<File> waveFiles, File wav, List<RecordingMetaData> metaList) throws IOException {
		deleteFileIfExists(wav);
		stripAudioFirstPass(r, logs, waveFiles, streamFolder, metaList == null ? metaDataDao.getNotScreenMetaDataByRecording(r.getId()) : metaList);
		if (waveFiles.isEmpty()) {
			// create default Audio to merge it. strip to content length
			String oneSecWav = new File(getStreamsHibernateDir(), "one_second.wav").getCanonicalPath();

			// Calculate delta at beginning
			double duration = diffSeconds(r.getRecordEnd(), r.getRecordStart());

			String[] cmd = new String[] { getPathToSoX(), oneSecWav, wav.getCanonicalPath(), "pad", "0", String.valueOf(duration) };

			logs.add(ProcessHelper.executeScript("generateSampleAudio", cmd));
		} else if (waveFiles.size() == 1) {
			copyFile(waveFiles.get(0), wav);
		} else {
			String[] soxArgs = mergeAudioToWaves(waveFiles, wav);

			logs.add(ProcessHelper.executeScript("mergeAudioToWaves", soxArgs));
		}
	}

	private String[] addSoxPad(ProcessResultList logs, String job, double length, double position, File inFile, File outFile) throws IOException {
		if (length < 0 || position < 0) {
			log.debug("::addSoxPad {} Invalid parameters: length = {}; position = {}; inFile = {}", job, length, position, inFile);
		}
		String[] argv = new String[] { getPathToSoX(), inFile.getCanonicalPath(), outFile.getCanonicalPath(), "pad"
				, String.valueOf(length < 0 ? 0 : length)
				, String.valueOf(position < 0 ? 0 : position) };

		logs.add(ProcessHelper.executeScript(job, argv));
		return argv;
	}

	private static File getMetaFlvSer(RecordingMetaData metaData) {
		File metaDir = getStreamsSubDir(metaData.getRecording().getRoomId());
		return new File(metaDir, metaData.getStreamName() + ".flv.ser");
	}

	public static void printMetaInfo(RecordingMetaData metaData, String prefix) {
		if (log.isDebugEnabled()) {
			log.debug("### {}:: recording id {}; stream with id {}; current status: {} ", prefix, metaData.getRecording().getId()
					, metaData.getId(), metaData.getStreamStatus());
			File metaFlv = getRecordingMetaData(metaData.getRecording().getRoomId(), metaData.getStreamName());
			File metaSer = getMetaFlvSer(metaData);
			log.debug("### {}:: Flv file [{}] exists ? {}; size: {}, lastModified: {} ", prefix, metaFlv.getPath(), metaFlv.exists(), metaFlv.length(), metaFlv.lastModified());
			log.debug("### {}:: Ser file [{}] exists ? {}; size: {}, lastModified: {} ", prefix, metaSer.getPath(), metaSer.exists(), metaSer.length(), metaSer.lastModified());
		}
	}

	protected RecordingMetaData waitForTheStream(long metaId) throws InterruptedException {
		RecordingMetaData metaData = metaDataDao.get(metaId);
		if (metaData.getStreamStatus() != Status.STOPPED) {
			log.debug("### meta Stream not yet written to disk {}", metaId);
			long counter = 0;
			long maxTimestamp = 0;
			while(true) {
				log.trace("### Stream not yet written Thread Sleep - {}", metaId);

				metaData = metaDataDao.get(metaId);

				if (metaData.getStreamStatus() == Status.STOPPED) {
					printMetaInfo(metaData, "Stream now written");
					log.debug("### Thread continue ... " );
					break;
				} else {
					File metaFlv = getRecordingMetaData(metaData.getRecording().getRoomId(), metaData.getStreamName());
					if (metaFlv.exists() && maxTimestamp < metaFlv.lastModified()) {
						maxTimestamp = metaFlv.lastModified();
					}
					File metaSer = getMetaFlvSer(metaData);
					if (metaSer.exists() && maxTimestamp < metaSer.lastModified()) {
						maxTimestamp = metaSer.lastModified();
					}
					if (maxTimestamp + TIME_TO_WAIT_FOR_FRAME < System.currentTimeMillis()) {
						if (metaSer.exists()) {
							log.debug("### long time without any update, trying to repair ... ");
							try {
								if (FLVWriter.repair(metaSer.getCanonicalPath(), null, null) && !metaSer.exists()) {
									log.debug("### Repairing was successful ... ");
								} else {
									log.debug("### Repairing was NOT successful ... ");
								}
							} catch (IOException e) {
								log.error("### Error while file repairing ... ", e);
							}
						} else {
							log.debug("### long time without any update, closing ... ");
						}
						metaData.setStreamStatus(Status.STOPPED);
						metaDataDao.update(metaData);
						break;
					}
				}
				if (++counter % 1000 == 0) {
					printMetaInfo(metaData, "Still waiting");
				}

				Thread.sleep(100L);
			}
		}
		return metaData;
	}

	private void stripAudioFirstPass(Recording recording,
			ProcessResultList logs,
			List<File> waveFiles, File streamFolder,
			List<RecordingMetaData> metaDataList) {
		try {
			// Init variables
			log.debug("### meta Data Number - {}", metaDataList.size());
			log.debug("###################################################");

			for (RecordingMetaData metaData : metaDataList) {
				long metaId = metaData.getId();
				log.debug("### processing metadata: {}", metaId);
				if (metaData.getStreamStatus() == Status.NONE) {
					log.debug("Stream has not been started, error in recording {}", metaId);
					continue;
				}

				metaData = waitForTheStream(metaId);

				File inputFlvFile = new File(streamFolder, OmFileHelper.getName(metaData.getStreamName(), EXTENSION_FLV));

				File outputWav = new File(streamFolder, metaData.getStreamName() + "_WAVE.wav");

				log.debug("FLV File Name: {} Length: {} ", inputFlvFile.getName(), inputFlvFile.length());

				if (inputFlvFile.exists()) {
					String[] argv = new String[] {
							getPathToFFMPEG(), "-y"
							, "-i", inputFlvFile.getCanonicalPath()
							, "-af", String.format("aresample=%s:min_comp=0.001:min_hard_comp=0.100000", getAudioBitrate())
							, outputWav.getCanonicalPath()};
					//there might be no audio in the stream
					logs.add(ProcessHelper.executeScript("stripAudioFromFLVs", argv, true));
				}

				if (outputWav.exists() && outputWav.length() != 0) {
					metaData.setAudioValid(true);
					// Strip Wave to Full Length
					File outputGapFullWav = outputWav;

					// Fix Start/End in Audio
					List<RecordingMetaDelta> metaDeltas = metaDeltaDao.getByMetaId(metaId);

					int counter = 0;

					for (RecordingMetaDelta metaDelta : metaDeltas) {
						File inputFile = outputGapFullWav;

						// Strip Wave to Full Length
						String hashFileGapsFullName = metaData.getStreamName() + "_GAP_FULL_WAVE_" + counter + ".wav";
						outputGapFullWav = new File(streamFolder, hashFileGapsFullName);

						metaDelta.setWaveOutPutName(hashFileGapsFullName);

						String[] soxArgs = null;

						if (metaDelta.getDeltaTime() != null) {
							double gapSeconds = diffSeconds(metaDelta.getDeltaTime());
							if (metaDelta.isStartPadding()) {
								soxArgs = addSoxPad(logs, "fillGap", gapSeconds, 0, inputFile, outputGapFullWav);
							} else if (metaDelta.isEndPadding()) {
								soxArgs = addSoxPad(logs, "fillGap", 0, gapSeconds, inputFile, outputGapFullWav);
							}
						}

						if (soxArgs != null) {
							log.debug("START fillGap ################# Delta-ID :: {}", metaDelta.getId());

							metaDeltaDao.update(metaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
					}

					// Strip Wave to Full Length
					String hashFileFullName = metaData.getStreamName() + "_FULL_WAVE.wav";
					File outputFullWav = new File(streamFolder, hashFileFullName);

					// Calculate delta at beginning
					double startPad = diffSeconds(metaData.getRecordStart(), recording.getRecordStart());

					// Calculate delta at ending
					double endPad = diffSeconds(recording.getRecordEnd(), metaData.getRecordEnd());

					addSoxPad(logs, "addStartEndToAudio", startPad, endPad, outputGapFullWav, outputFullWav);

					// Fix for Audio Length - Invalid Audio Length in Recorded Files
					// Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");

					if (!outputFullWav.exists()) {
						throw new ConversionException("Audio File does not exist , could not extract the Audio correctly");
					}
					metaData.setFullWavAudioData(hashFileFullName);

					// Finally add it to the row!
					waveFiles.add(outputFullWav);
				}
				metaDataDao.update(metaData);
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

	protected List<String> addMp4OutParams(Recording r, List<String> argv, String mp4path) {
		argv.addAll(Arrays.asList(
				"-c:v", "h264", //
				"-crf", "24",
				"-pix_fmt", "yuv420p",
				"-preset", getVideoPreset(),
				"-profile:v", "baseline",
				"-level", "3.0",
				"-movflags", "faststart",
				"-c:a", "aac",
				"-ar", String.valueOf(getAudioRate()),
				"-b:a", getAudioBitrate(),
				"-s", getDimensions(r), //
				mp4path
				));
		return argv;
	}

	protected String convertToMp4(Recording r, List<String> _argv, ProcessResultList logs) throws IOException {
		String mp4path = r.getFile().getCanonicalPath();
		List<String> argv = new ArrayList<>(Arrays.asList(getPathToFFMPEG(), "-y"));
		argv.addAll(_argv);
		logs.add(ProcessHelper.executeScript("generate MP4", addMp4OutParams(r, argv, mp4path).toArray(new String[]{})));
		return mp4path;
	}

	protected void convertToPng(BaseFileItem f, String mp4path, ProcessResultList logs) throws IOException {
		// Extract first Image for preview purpose
		// ffmpeg -i movie.mp4 -vf  "thumbnail,scale=640:-1" -frames:v 1 movie.png
		File png = f.getFile(EXTENSION_PNG);
		String[] argv = new String[] { //
				getPathToFFMPEG(), "-y" //
				, "-i", mp4path //
				, "-vf", "thumbnail,scale=640:-1" //
				, "-frames:v", "1" //
				, png.getCanonicalPath() };
		logs.add(ProcessHelper.executeScript(String.format("generate preview PNG :: %s", f.getHash()), argv));
	}

	public static Dimension getDimension(String txt) {
		Matcher matcher = p.matcher(txt);

		while (matcher.find()) {
			String foundResolution = txt.substring(matcher.start(), matcher.end());
			String[] resultions = foundResolution.split("x");
			return new Dimension(toInt(resultions[0]), toInt(resultions[1]));
		}

		return new Dimension(100, 100); // will return 100x100 for non-video to be able to play
	}

	protected void finalize(Recording r, String mp4path, ProcessResultList logs) throws IOException {
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
			if (audio.exists()) {
				audio.delete();
			}
		}
	}
}
