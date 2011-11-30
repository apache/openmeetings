package org.openmeetings.app.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class GenerateThumbs {

	private static final Logger log = Red5LoggerFactory
			.getLogger(GenerateThumbs.class);

	@Autowired
	private GenerateImage generateImage;

	public HashMap<String, String> generateThumb(String pre,
			String current_dir, String filepath, Integer thumbSize) {
		// Init variables
		File f = new File(filepath);
		String name = f.getName();
		String folder = f.getParentFile().getAbsolutePath()
				+ File.separatorChar;

		String[] argv = new String[] {
				generateImage.getPathToImageMagic(),
				"-thumbnail",
				Integer.toString(thumbSize) + "x" + Integer.toString(thumbSize),
				filepath + ".jpg", folder + pre + name + ".jpg" };

		log.debug("START generateThumb ################# ");
		for (int i = 0; i < argv.length; i++) {
			log.debug(" i " + i + " argv-i " + argv[i]);
		}
		log.debug("END generateThumb ################# ");

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return GenerateSWF.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}
	}

	public HashMap<String, String> decodePDF(String inputfile, String outputfile) {

		String[] argv = new String[] { generateImage.getPathToImageMagic(), // FIXME
				inputfile, outputfile };

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return GenerateSWF.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}

	}

	public HashMap<String, String> generateBatchThumb(String current_dir,
			String inputfile, String outputpath, Integer thumbSize, String pre) {

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			String[] argv = new String[] {
					generateImage.getPathToImageMagic(),
					"-thumbnail", // FIXME
					Integer.toString(thumbSize), inputfile,
					outputpath + "_" + pre + "_page-%04d.jpg" };

			return GenerateSWF.executeScript("generateBatchThumbByWidth", argv);
		} else {

			String[] argv = new String[] {
					generateImage.getPathToImageMagic(),
					"-thumbnail", // FIXME
					Integer.toString(thumbSize), inputfile,
					outputpath + "_" + pre + "_page-%%04d.jpg" };

			// return GenerateSWF.executeScript("generateBatchThumbByWidth",
			// argv);
			return this.processImageWindows(argv);
		}
	}

	public HashMap<String, String> generateImageBatchByWidth(
			String current_dir, String inputfile, String outputpath,
			Integer thumbWidth, String pre) {

		String[] argv = new String[] { generateImage.getPathToImageMagic(),
				"-resize", Integer.toString(thumbWidth), inputfile,
				outputpath + "_" + pre + "_page.png" };

		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") == -1) {
			return GenerateSWF.executeScript("generateBatchThumbByWidth", argv);
		} else {
			return this.processImageWindows(argv);
		}
	}

	public HashMap<String, String> processImageWindows(String[] args) {
		HashMap<String, String> returnMap = new HashMap<String, String>();
		returnMap.put("process", "processImageWindows");
		try {

			// Init variables
			String[] cmd;
			String executable_fileName = "";

			String runtimeFile = "interviewMerge.bat";
			executable_fileName = ScopeApplicationAdapter.batchFileFir
					+ runtimeFile;

			cmd = new String[4];
			cmd[0] = "cmd.exe";
			cmd[1] = "/C";
			cmd[2] = "start";
			cmd[3] = executable_fileName;

			// log.debug("executable_fileName: "+executable_fileName);

			// Create the Content of the Converter Script (.bat or .sh File)

			String fileContent = "";

			for (int k = 0; k < args.length; k++) {
				if (k != 0) {
					fileContent += " ";
				}
				fileContent += args[k];
			}

			fileContent += ScopeApplicationAdapter.lineSeperator + "exit";

			File previous = new File(executable_fileName);
			if (previous.exists()) {
				previous.delete();
			}

			// execute the Script
			FileOutputStream fos = new FileOutputStream(executable_fileName);
			fos.write(fileContent.getBytes());
			fos.close();

			Runtime rt = Runtime.getRuntime();
			returnMap.put("command", cmd.toString());

			Process proc = rt.exec(cmd);

			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			String error = "";
			while ((line = br.readLine()) != null) {
				error += line;
				// log.debug("line: "+line);
			}
			returnMap.put("error", error);
			int exitVal = proc.waitFor();
			returnMap.put("exitValue", "" + exitVal);
			return returnMap;
		} catch (Throwable t) {
			t.printStackTrace();
			returnMap.put("error", t.getMessage());
			returnMap.put("exitValue", "-1");
			return returnMap;
		}
	}
}
