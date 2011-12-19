package org.openmeetings.screen.codec;

import org.apache.log4j.Logger;

public class CaptureScreenByDelta {
	
	private static Logger log = Logger.getLogger(CaptureScreenByDelta.class);

	/**
	 * 
	 * 
	 * 1) Fill Buffer
	 * 2) Check Buffer Full
	 * 3) Do comparison => switch buffer index
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * each Buffer Pool is one second 
	 * so the PRE buffer is one second
	 * => we buffer always 1 second, analyze 
	 * that data and then send it
	 * 
	 * Buffer 1 
	 * Buffer 2
	 * Buffer 3
	 * Buffer 4
	 * Buffer 5
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4 && buffer1 != buffer5
	 * => send buffer1 with 0.1 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3 && buffer1 != buffer4
	 * => send buffer1 with 0.2 quality
	 * 
	 * buffer1 != buffer2 && buffer1 != buffer3
	 * => send buffer1 with 0.4 quality
	 * 
	 * buffer1 != buffer2 
	 * => send buffer1 with 0.6 quality
	 * 
	 * buffer1 == buffer2
	 * => send nothing
	 * 
	 * => switch buffers in one direction
	 * 
	 */
	
	/**
	 * 
	 *
	 * 24.09.2009 10:24:43
	 * sebastianwagner
	 * 
	 *
	 */
	public CaptureScreenByDelta() {
		try {
			
		} catch (Exception err) {
			log.error("[ClientCaptureScreen]",err);
		}
	}
}