package org.red5.screen.webstart;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenKeyListener implements KeyListener {
	
	 private static final Logger logger = LoggerFactory.getLogger( ScreenKeyListener.class );

	public void keyPressed(KeyEvent kEvent) {
		// TODO Auto-generated method stub
		logger.debug("keyPressed :Code: "+kEvent.getKeyCode());
	}

	public void keyReleased(KeyEvent kEvent) {
		// TODO Auto-generated method stub
		logger.debug("keyReleased :Code: "+kEvent.getKeyCode());
	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
