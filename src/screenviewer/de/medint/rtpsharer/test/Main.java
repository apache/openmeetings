package de.medint.rtpsharer.test;

import de.medint.rtpsharer.main.*;
import de.medint.rtpsharer.util.ConfigUtil;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		


		//To test this you have to set the values in the ConfigUtil

		//For example
		ConfigUtil.destinationAddress = "127.0.0.1";
		
		Streamer streamer = new Streamer();
		streamer.start();
		


	}

}
