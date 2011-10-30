package org.openmeetings.app.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateSWF {

	private static final Logger log = Red5LoggerFactory
			.getLogger(GeneratePDF.class);

	@Autowired
	private Configurationmanagement cfgManagement;

	public final static boolean isPosix = System.getProperty("os.name")
			.toUpperCase().indexOf("WINDOWS") == -1;

	public final static String execExt = isPosix ? "" : ".exe";

	public static HashMap<String, Object> executeScript(String process,
			String[] argv) {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("process", process);
		log.debug("process: " + process);
		log.debug("args: " + Arrays.toString(argv));

		try {
			// Runtime rt = Runtime.getRuntime();
			returnMap.put("command", Arrays.toString(argv));

			// By using the process Builder we have access to modify the
			// environment variables
			// that is handy to set variables to run it inside eclipse
			ProcessBuilder pb = new ProcessBuilder(argv);

			// Map<String, String> env = pb.environment();

			// System.out.println("key "+"pb.toString"+" value "+pb.toString());
			//
			// System.out.println("key "+"os.name"+" value "+System.getProperty("os.name").toUpperCase());
			// System.out.println("key "+"isPosix"+" value "+isPosix);
			// System.out.println("key "+"execExt"+" value "+execExt);

			// for (Iterator<String> iter =
			// env.keySet().iterator();iter.hasNext();) {
			// String key = iter.next();
			//
			// System.out.println("key "+key+" value "+env.get(key));
			// //log.debug("key "+key);
			//
			// }

			Process proc = pb.start();

			// 20-minute timeout for command execution
			// FFMPEG conversion of Recordings may take a real long time until
			// its finished
			long timeout = 60000 * 20;

			ErrorStreamWatcher errorWatcher = new ErrorStreamWatcher(proc);
			Worker worker = new Worker(proc);
			InputStreamWatcher inputWatcher = new InputStreamWatcher(proc);
			errorWatcher.start();
			worker.start();

			inputWatcher.start();
			try {
				worker.join(timeout);
				if (worker.exit != null) {
					returnMap.put("exitValue", worker.exit);
					log.debug("exitVal: " + worker.exit);
					returnMap.put("error", errorWatcher.error);
				} else {
					returnMap.put("exception", "timeOut");
					returnMap.put("error", errorWatcher.error);
					returnMap.put("exitValue", -1);

					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				errorWatcher.interrupt();
				inputWatcher.interrupt();
				Thread.currentThread().interrupt();

				returnMap.put("error", ex.getMessage());
				returnMap.put("exitValue", -1);

				throw ex;
			} finally {
				proc.destroy();
			}
		} catch (TimeoutException e) {
			// Timeout exception is processed above
		} catch (Throwable t) {
			// Any other exception is shown in debug window
			t.printStackTrace();
			returnMap.put("error", t.getMessage());
			returnMap.put("exitValue", -1);
		}
		return returnMap;
	}

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		@Override
		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	// This one collects errors coming from script execution
	private static class ErrorStreamWatcher extends Thread {
		private String error;
		private final InputStream stderr;
		private final InputStreamReader isr;
		private final BufferedReader br;

		private ErrorStreamWatcher(Process process) {
			error = "";
			stderr = process.getErrorStream();
			isr = new InputStreamReader(stderr);
			br = new BufferedReader(isr);
		}

		@Override
		public void run() {
			try {
				String line = br.readLine();
				while (line != null) {
					error += line;
					log.debug("line: " + line);
					line = br.readLine();
				}
			} catch (IOException ioexception) {
				return;
			}
		}
	}

	// This one just reads script's output stream so it can
	// finish normally, see issue 801
	private static class InputStreamWatcher extends Thread {
		private final InputStream stderr;
		private final InputStreamReader isr;
		private final BufferedReader br;

		private InputStreamWatcher(Process process) {
			stderr = process.getInputStream();
			isr = new InputStreamReader(stderr);
			br = new BufferedReader(isr);
		}

		@Override
		public void run() {
			try {
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
				}
			} catch (IOException ioexception) {
				return;
			}
		}
	}

	private String getPathToSwfTools() {
		String pathToSWFTools = cfgManagement.getConfKey(3, "swftools_path")
				.getConf_value();
		// If SWFTools Path is not blank a File.separator at the end of the path
		// is needed
		if (!pathToSWFTools.equals("")
				&& !pathToSWFTools.endsWith(File.separator)) {
			pathToSWFTools = pathToSWFTools + File.separator;
		}
		return pathToSWFTools;
	}

	public HashMap<String, Object> generateSwf(String current_dir,
			String originalFolder, String destinationFolder, String fileNamePure) {
		
		// Create the Content of the Converter Script (.bat or .sh File)
		String[] argv = new String[] {
				getPathToSwfTools() + "pdf2swf" + execExt, "-s",
				"insertstop", // insert Stop command into every frame
				"-s","poly2bitmap", //http://www.swftools.org/gfx_tutorial.html#Rendering_pages_to_SWF_files
				"-i", // change draw order to reduce pdf complexity
				originalFolder + fileNamePure + ".pdf",
				destinationFolder + fileNamePure + ".swf" };

		return executeScript("generateSwf", argv);
	}

	/**
	 * Generates an SWF from the list of files.
	 */
	public HashMap<String, Object> generateSwfByImages(List<String> images,
			String outputfile, int fps) {
		List<String> argvList = Arrays.asList(new String[] {
				getPathToSwfTools() + "png2swf" + execExt, "-s", "insertstop", // Insert
																				// Stop
																				// command
																				// into
																				// every
																				// frame
				"-o", outputfile, "-r", Integer.toString(fps), "-z" });

		argvList.addAll(images);
		return executeScript("generateSwfByImages",
				(String[]) argvList.toArray());
	}

	/**
	 * Combines a bunch of SWFs into one SWF by concatenate.
	 */
	public HashMap<String, Object> generateSWFByCombine(List<String> swfs,
			String outputswf, int fps) {
		List<String> argvList = Arrays.asList(new String[] {
				getPathToSwfTools() + "swfcombine" + execExt, "-s",
				"insertstop", // Insert Stop command into every frame
				"-o", outputswf, "-r", Integer.toString(fps), "-z", "-a" });

		argvList.addAll(swfs);
		return executeScript("generateSwfByImages",
				(String[]) argvList.toArray());
	}

	public HashMap<String, Object> generateSWFByFFMpeg(String inputWildCard,
			String outputswf, int fps, int width, int height) {
		// FIXME: ffmpeg should be on the system path
		String[] argv = new String[] { "ffmpeg" + execExt, "-r",
				Integer.toString(fps), "-i", inputWildCard, "-s",
				width + "x" + height, "-b", "750k", "-ar", "44100", "-y",
				outputswf };

		return executeScript("generateSWFByFFMpeg", argv);
	}

}
