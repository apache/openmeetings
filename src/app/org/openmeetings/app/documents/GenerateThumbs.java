package org.openmeetings.app.documents;

import java.io.File;
import java.util.HashMap;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class GenerateThumbs {
	
	private static final Logger log = Red5LoggerFactory.getLogger(GenerateThumbs.class);
	
	private static GenerateThumbs instance;

	private GenerateThumbs() {}

	public static synchronized GenerateThumbs getInstance() {
		if (instance == null) {
			instance = new GenerateThumbs();
		}
		return instance;
	}
	
	public HashMap<String, Object> generateThumb(String pre,
			String current_dir, String filepath, Integer thumbSize) {
		// Init variables
		File f = new File(filepath);
		String name = f.getName();
		String folder = f.getParentFile().getAbsolutePath()
				+ File.separatorChar;

		String[] argv = new String[] {
				GenerateImage.getPathToImageMagic(), 
				"-thumbnail",
				Integer.toString(thumbSize), 
				filepath + ".jpg",
				folder + pre + name + ".jpg" };

		log.debug("START generateThumb ################# ");
		for (int i=0;i<argv.length;i++) {
			log.debug(" i "+i+" argv-i "+argv[i]);
		}
		log.debug("END generateThumb ################# ");
		
		return GenerateSWF.executeScript("generateThumb", argv);
	}

	public HashMap<String, Object> generateBatchThumb(String current_dir,
			String inputfile, String outputpath, Integer thumbSize, String pre) {

		String[] argv = new String[] {
				GenerateImage.getPathToImageMagic(), "-thumbnail", //FIXME
				Integer.toString(thumbSize), inputfile,
				outputpath + "_" + pre + "_page-%04d.jpg" };

		return GenerateSWF.executeScript("generateBatchThumb", argv);
	}

	public HashMap<String, Object> generateImageBatchByWidth(
			String current_dir, String inputfile, String outputpath,
			Integer thumbWidth, String pre) {

		String[] argv = new String[] {
				GenerateImage.getPathToImageMagic(), "-resize",
				Integer.toString(thumbWidth), inputfile,
				outputpath + "_" + pre + "_page.png" };

		return GenerateSWF.executeScript("generateBatchThumbByWidth", argv);
	}
}
