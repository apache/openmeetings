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
import static org.apache.openmeetings.util.OpenmeetingsVariables.EXT_PROCESS_TTL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.openmeetings.util.OpenmeetingsVariables;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class ProcessHelper {
	public static final Logger log = Red5LoggerFactory.getLogger(ProcessHelper.class, OpenmeetingsVariables.webAppRootKey);

	public static ConverterProcessResult executeScriptWindows(String process, String[] argv) {
		try {
			String[] cmd = new String[argv.length + 2];
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			System.arraycopy(argv, 0, cmd, 2, argv.length);
			Map<String, String> env = new HashMap<>();
			return executeScript(process, cmd, env);
		} catch (Exception t) {
			log.error("executeScriptWindows", t);
			return new ConverterProcessResult(process, t.getMessage(), t);
		}
	}

	private static String getCommand(String[] argv) {
		StringBuffer tString = new StringBuffer();
		for (int i = 0; i < argv.length; i++) {
			tString.append(argv[i]).append(" ");
		}
		return tString.toString();
	}

	private static void debugCommandStart(String desc, String[] argv) {
		if (log.isDebugEnabled()) {
			log.debug("START " + desc + " ################# ");
			log.debug(getCommand(argv));
		}
	}

	private static void debugCommandEnd(String desc) {
		if (log.isDebugEnabled()) {
			log.debug("END " + desc + " ################# ");
		}
	}

	public static ConverterProcessResult executeScript(String process, String[] argv) {
		Map<String, String> env = new HashMap<>();
		return executeScript(process, argv, env);
	}

	public static ConverterProcessResult executeScript(String process, String[] argv, Map<? extends String, ? extends String> env) {
		ConverterProcessResult res = new ConverterProcessResult();
		res.setProcess(process);
		debugCommandStart(process, argv);

		Process proc = null;
		try {
			res.setCommand(getCommand(argv));
			res.setOut("");

			// By using the process Builder we have access to modify the
			// environment variables
			// that is handy to set variables to run it inside eclipse
			ProcessBuilder pb = new ProcessBuilder(argv);
			pb.environment().putAll(env);

			proc = pb.start();

			// 20-minute timeout for command execution
			// FFMPEG conversion of Recordings may take a real long time until
			// its finished
			proc.waitFor(EXT_PROCESS_TTL, TimeUnit.MINUTES);

			res.setExitCode(proc.exitValue());
			res.setOut(IOUtils.toString(proc.getInputStream(), UTF_8));
			res.setError(IOUtils.toString(proc.getErrorStream(), UTF_8));
		} catch (Throwable t) {
			log.error("executeScript", t);
			res.setError(t.getMessage());
			res.setException(t.toString());
			res.setExitCode(-1);
		} finally {
			if (proc != null) {
				proc.destroy();
			}
		}

		debugCommandEnd(process);
		return res;
	}
}

