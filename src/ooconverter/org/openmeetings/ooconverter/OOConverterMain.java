package org.openmeetings.ooconverter;

public class OOConverterMain {

	/**
	 * 
	 * The OpenOffice conversion should always happen in a separated Java Task
	 * from the Application server
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		
		if (args.length != 2) {
			new Exception("Wrong number of arguments, requires input and output path");
		}
		
		String inputPath = args[0];
		String outputPath = args[1];

		String[] commandArgs = {
		// command line arguments
		};

		ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);

	}

}
