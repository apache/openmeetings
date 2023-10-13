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
package org.apache.openmeetings.util.process;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.util.CalendarHelper.formatMillis;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getExtProcessTtl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHelper {
	private static final Logger log = LoggerFactory.getLogger(ProcessHelper.class);

	// This is necessary to prevent 'buffer overflow'
	// https://stackoverflow.com/questions/9885643/ffmpeg-executed-from-javas-processbuilder-does-not-return-under-windows-7
	private static class StreamWatcher extends Thread {
		public final StringBuilder output = new StringBuilder();
		private final InputStream is;
		private boolean run = true;

		private StreamWatcher(final InputStream is) {
			this.is = is;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is, UTF_8))) {
				String line = br.readLine();
				while (run && line != null) {
					output.append(line).append('\n');
					line = br.readLine();
				}
			} catch (IOException ioexception) {
				// no-op
			}
		}

		public void finish() {
			run = false;
		}

		@Override
		public String toString() {
			return output.toString();
		}
	}

	private ProcessHelper() {}

	private static String getCommand(List<String> argv) {
		return String.join(" ", argv);
	}

	private static void debugCommandStart(String desc, List<String> argv) {
		if (log.isDebugEnabled()) {
			log.debug("START {} ################# ", desc);
			log.debug(getCommand(argv));
		}
	}

	private static void debugCommandEnd(String desc) {
		if (log.isDebugEnabled()) {
			log.debug("END {} ################# ", desc);
		}
	}

	public static ProcessResult exec(String process, List<String> argv) {
		return exec(process, argv, false);
	}

	public static ProcessResult exec(String process, List<String> argv, boolean optional) {
		return exec(process, argv, Map.of(), optional);
	}

	private static ProcessResult exec(String process, List<String> argv, Map<String, String> env, boolean optional) {
		ProcessResult res = new ProcessResult()
				.setProcess(process)
				.setOptional(optional);
		debugCommandStart(process, argv);

		Process proc = null;
		StreamWatcher errorWatcher = null;
		StreamWatcher inputWatcher = null;
		final long start = System.currentTimeMillis();
		try {
			res.setCommand(getCommand(argv)).setOut("");

			// By using the process Builder we have access to modify the
			// environment variables
			// that is handy to set variables to run it inside eclipse
			ProcessBuilder pb = new ProcessBuilder(argv);
			pb.environment().putAll(env);

			proc = pb.start();
			errorWatcher = new StreamWatcher(proc.getErrorStream());
			inputWatcher = new StreamWatcher(proc.getInputStream());
			errorWatcher.start();
			inputWatcher.start();

			// 20-minute timeout for command execution
			// FFMPEG conversion of Recordings may take a real long time until
			// its finished
			proc.waitFor(getExtProcessTtl(), TimeUnit.MINUTES);

			res.setExitCode(proc.exitValue())
				.setOut(inputWatcher.toString())
				.setError(errorWatcher.toString());
		} catch (InterruptedException e) {
			onException(e, start, res);
			Thread.currentThread().interrupt();
		} catch (Throwable t) {
			onException(t, start, res);
		} finally {
			if (errorWatcher != null) {
				errorWatcher.finish();
			}
			if (inputWatcher != null) {
				inputWatcher.finish();
			}
			if (proc != null) {
				proc.destroy();
			}
		}

		debugCommandEnd(process);
		return res;
	}

	private static void onException(Throwable t, long start, ProcessResult res) {
		log.error("executeScript", t);
		res.setExitCode(-1)
			.setError("Exception after " + formatMillis(System.currentTimeMillis() - start)
					+ " of work; " + t.getMessage())
			.setException(t.toString());
	}
}
