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

import static org.apache.openmeetings.util.CalendarHelper.formatMillis;
import static org.apache.openmeetings.util.OmFileHelper.getRecordingMetaData;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.openmeetings.db.dao.record.RecordingDao;
import org.apache.openmeetings.db.dao.record.RecordingMetaDataDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.record.RecordingMetaData;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.openmeetings.util.process.ProcessHelper;
import org.apache.openmeetings.util.process.ProcessResult;
import org.apache.openmeetings.util.process.ProcessResultList;
import org.apache.wicket.util.string.Strings;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InterviewConverter extends BaseConverter implements IRecordingConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(InterviewConverter.class, getWebAppRootKey());

	// Spring loaded Beans
	@Autowired
	private RecordingDao recordingDao;
	@Autowired
	private RecordingMetaDataDao metaDataDao;

	@Override
	public void startConversion(Long id) {
		Recording r = null;
		ProcessResultList logs = new ProcessResultList();
		List<File> waveFiles = new ArrayList<>();
		try {
			// Default Image for empty interview video pods
			final File interviewCamFile = new File(OmFileHelper.getImagesDir(), "interview_webcam.png");
			if (!interviewCamFile.exists()) {
				throw new ConversionException("defaultInterviewImageFile does not exist!");
			}

			r = recordingDao.get(id);
			log.debug("recording {}", r.getId());
			if (Strings.isEmpty(r.getHash())) {
				r.setHash(UUID.randomUUID().toString());
			}
			r.setStatus(Recording.Status.CONVERTING);
			r = recordingDao.update(r);

			File streamFolder = getStreamFolder(r);
			List<RecordingMetaData> metaList = metaDataDao.getByRecording(r.getId());

			File wav = new File(streamFolder, String.format("INTERVIEW_%s_FINAL_WAVE.wav", r.getId()));
			createWav(r, logs, streamFolder, waveFiles, wav, metaList);

			final String interviewCam = interviewCamFile.getCanonicalPath();

			final int width = 320;
			final int height = 260;
			// Merge Audio with Video / Calculate resulting FLV

			// group by sid first to get all pods
			Map<String, List<RecordingMetaData>> metaBySid = metaList.stream().collect(
					Collectors.groupingBy(RecordingMetaData::getSid
					, () -> new LinkedHashMap<>()
					, Collectors.collectingAndThen(Collectors.toList(), l -> l.stream().sorted(Comparator.comparing(RecordingMetaData::getRecordStart)).collect(Collectors.toList()))));
			List<String> pods = new ArrayList<>();
			int N = pods.size();
			for (Entry<String, List<RecordingMetaData>> e : metaBySid.entrySet()) {
				Date pStart = r.getRecordStart();
				List<PodPart> parts = new ArrayList<>();
				for (RecordingMetaData meta : e.getValue()) {
					File flv = getRecordingMetaData(r.getRoomId(), meta.getStreamName());
					if (flv.exists()) {
						String path = flv.getCanonicalPath();
						/* CHECK FILE:
						 * ffmpeg -i rec_316_stream_567_2013_08_28_11_51_45.flv -v error -f null file.null
						 */
						String[] args = new String[] {getPathToFFMPEG(), "-y"
								, "-i", path
								, "-v", "error"
								, "-f", "null"
								, "file.null"};
						ProcessResult res = ProcessHelper.executeScript(String.format("checkFlvPod_%s_%s", N, parts.size()), args, true);
						logs.add(res);
						if (!res.isWarn()) {
							long diff = diff(meta.isAudioOnly() ? meta.getRecordEnd() : meta.getRecordStart(), pStart);
							//createBlankPod(id, streamFolder, interviewCam, diff, logs, pods, parts);
							PodPart.add(parts, diff);
							if (!meta.isAudioOnly()) {
								parts.add(new PodPart(path, diff(meta.getRecordEnd(), meta.getRecordStart())));
							}
							pStart = meta.getRecordEnd();
						}
					} else {
						log.debug("Meta FLV doesn't exist: {}", flv);
					}
				}
				if (!parts.isEmpty()) {
					String podX = new File(streamFolder, String.format("rec_%s_pod_%s.flv", id, N)).getCanonicalPath();
					long diff = diff(r.getRecordEnd(), pStart);
					// add blank pod till the end
					//createBlankPod(id, streamFolder, interviewCam, diff, logs, pods, parts);
					PodPart.add(parts, diff);
					/* create continuous pod
					 * ffmpeg \
					 *	-loop 1 -framerate 24 -t 10 -i image1.jpg \
					 *	-i video.mp4 \
					 *	-loop 1 -framerate 24 -t 10 -i image2.jpg \
					 *	-loop 1 -framerate 24 -t 10 -i image3.jpg \
					 *	-filter_complex "[0][1][2][3]concat=n=4:v=1:a=0" out.mp4
					 */
					List<String> args = new ArrayList<>();
					args.add(getPathToFFMPEG());
					args.add("-y");
					StringBuilder videos = new StringBuilder();
					StringBuilder concat = new StringBuilder();
					for (int i = 0; i < parts.size(); ++i) {
						PodPart p = parts.get(i);
						if (p.getFile() == null) {
							args.add("-loop");
							args.add("1");
							args.add("-t");
							args.add(formatMillis(p.getDuration()));
							args.add("-i");
							args.add(interviewCam);
						} else {
							args.add("-t");
							args.add(formatMillis(p.getDuration()));
							args.add("-i");
							args.add(p.getFile());
						}
						videos.append('[').append(i).append(']')
							.append("scale=").append(width).append(':').append(height)
							.append("[v").append(i).append("]; ");
						concat.append("[v").append(i).append(']');
					}
					args.add("-filter_complex");
					args.add(concat.insert(0, videos).append("concat=n=").append(parts.size()).append(":v=1:a=0").toString());
					args.add("-an");
					args.add(podX);
					ProcessResult res = ProcessHelper.executeScript(String.format("Full Flv pod_%s", N), args.toArray(new String[0]), true);
					logs.add(res);
					if (res.isWarn()) {
						throw new ConversionException("Fail to create pod");
					}
					pods.add(podX);
					N = pods.size();
				}
			}
			if (N == 0) {
				ProcessResult res = new ProcessResult();
				res.setProcess("CheckFlvFilesExists");
				res.setError("No valid pods found");
				res.setExitCode(-1);
				logs.add(res);
				return;
			}
			double ratio = Math.sqrt(N / Math.sqrt(2));
			int w = ratio < 1 ? N : (int)Math.round(ratio);
			w = Math.max(w, (int)Math.round(1. * N / w));
			List<String> args = new ArrayList<>();
			if (N == 1) {
				args.add("-i");
				args.add(pods.get(0));
				args.add("-i");
				args.add(wav.getCanonicalPath());
				args.add("-map");
				args.add("0:v");
			} else {
				/* Creating grid
				 * ffmpeg -i top_l.mp4 -i top_r.mp4 -i bottom_l.mp4 -i bottom_r.mp4 -i audio.mp4 \
				 *	-filter_complex "[0:v][1:v]hstack=inputs=2[t];[2:v][3:v]hstack=inputs=2[b];[t][b]vstack=inputs=2[v]" \
				 *	-map "[v]" -map 4:a -c:a copy -shortest output.mp4
				 */
				StringBuilder cols = new StringBuilder();
				StringBuilder rows = new StringBuilder();
				for (int i = 0, j = 0; i < N; ++i) {
					args.add("-i");
					args.add(pods.get(i));
					cols.append('[').append(i).append(":v]");
					if (i != 0 && (i + 1) % w == 0) {
						cols.append("hstack=inputs=").append(w);
						if (j == 0 && i == N - 1) {
							cols.append("[v]");
						} else {
							cols.append("[c").append(j).append("];");
						}
						rows.append("[c").append(j).append(']');
						j++;
					}
					if (i == N - 1) {
						if (j > 1) {
							rows.append("vstack=inputs=").append(j).append("[v]");
						} else {
							rows.setLength(0);
						}
					}
				}
				args.add("-i");
				args.add(wav.getCanonicalPath());
				args.add("-filter_complex");
				args.add(cols.append(rows).toString());
				args.add("-map");
				args.add("[v]");
			}
			args.add("-map");
			args.add(String.format("%s:a", N));
			args.add("-qmax"); args.add("1");
			args.add("-qmin"); args.add("1");

			r.setWidth(w * width);
			r.setHeight((N / w) * height);

			String mp4path = convertToMp4(r, args, logs);

			finalize(r, mp4path, logs);
		} catch (Exception err) {
			log.error("[startConversion]", err);
			r.setStatus(Recording.Status.ERROR);
		} finally {
			if (Recording.Status.CONVERTING == r.getStatus()) {
				r.setStatus(Recording.Status.ERROR);
			}
			postProcess(r, logs);
			postProcess(waveFiles);
			recordingDao.update(r);
		}
	}

	private static class PodPart {
		final String file;
		final long duration;

		public PodPart(String file, long duration) {
			this.file = file;
			this.duration = duration;
		}

		public PodPart(long duration) {
			this(null, duration);
		}

		public String getFile() {
			return file;
		}

		public long getDuration() {
			return duration;
		}

		public static void add(List<PodPart> parts, long duration) {
			if (duration != 0L) {
				parts.add(new PodPart(duration));
			}
		}
	}
}
