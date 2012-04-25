package org.openmeetings.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.openmeetings.app.documents.GenerateSWF;

public class ProcessHelper {

	private static class Worker extends Thread {
		private final Process process;
		private Integer exitCode;
	
		private Worker(Process process) {
			this.process = process;
		}
	
		@Override
		public void run() {
			try {
				exitCode = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	private static class StreamWatcher extends Thread {
		public StringBuilder output;
		private final InputStream is;
		private final BufferedReader br;
	
		private StreamWatcher(Process process, boolean isError) throws UnsupportedEncodingException {
			output = new StringBuilder();
			is = isError ? process.getErrorStream() : process.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		}
	
		@Override
		public void run() {
			try {
				String line = br.readLine();
				while (line != null) {
					output.append(line).append('\n');
					line = br.readLine();
				}
			} catch (IOException ioexception) {
				return;
			}
		}
	}

	public static HashMap<String, String> executeScript(String process, String[] argv) {
		Map<String, String> env = new HashMap<String, String>();
		return executeScript(process, argv, env);
	}
	
	public static HashMap<String, String> executeScript(String process,
			String[] argv, Map<? extends String, ? extends String> env) {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("process", process);
		GenerateSWF.log.debug("process: " + process);
		GenerateSWF.log.debug("args: " + Arrays.toString(argv));
	
		try {
			returnMap.put("command", Arrays.toString(argv));
			returnMap.put("out","");
	
			// By using the process Builder we have access to modify the
			// environment variables
			// that is handy to set variables to run it inside eclipse
			ProcessBuilder pb = new ProcessBuilder(argv);
			pb.environment().putAll(env);
	
			Process proc = pb.start();
	
			// 20-minute timeout for command execution
			// FFMPEG conversion of Recordings may take a real long time until
			// its finished
			long timeout = 60000 * 20;
	
			StreamWatcher errorWatcher = new StreamWatcher(proc, true);
			Worker worker = new Worker(proc);
			StreamWatcher inputWatcher = new StreamWatcher(proc, false);
			errorWatcher.start();
			inputWatcher.start();
			worker.start();
			
			
			try {
				worker.join(timeout);
				if (worker.exitCode != null) {
					returnMap.put("exitValue", "" + worker.exitCode);
					GenerateSWF.log.debug("exitVal: " + worker.exitCode);
					returnMap.put("error", errorWatcher.output.toString());
				} else {
					returnMap.put("exception", "timeOut");
					returnMap.put("error", errorWatcher.output.toString());
					returnMap.put("exitValue", "-1");
	
					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				errorWatcher.interrupt();
				inputWatcher.interrupt();
				Thread.currentThread().interrupt();
	
				returnMap.put("error", ex.getMessage());
				returnMap.put("exitValue", "-1");
	
				throw ex;
			} finally {
				proc.destroy();
			}
			
		} catch (TimeoutException e) {
			// Timeout exception is processed above
			GenerateSWF.log.error("executeScript",e);
			e.printStackTrace();
			returnMap.put("error", e.getMessage());
			returnMap.put("exception", e.toString());
			returnMap.put("exitValue", "-1");
		} catch (Throwable t) {
			// Any other exception is shown in debug window
			GenerateSWF.log.error("executeScript",t);
			t.printStackTrace();
			returnMap.put("error", t.getMessage());
			returnMap.put("exception", t.toString());
			returnMap.put("exitValue", "-1");
		}
		
		return returnMap;
	}

}
