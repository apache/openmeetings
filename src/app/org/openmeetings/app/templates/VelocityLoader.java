package org.openmeetings.app.templates;

import java.io.File;

import org.apache.velocity.app.Velocity;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 * 
 */

public abstract class VelocityLoader {

	private static final Logger log = Red5LoggerFactory.getLogger(
			VelocityLoader.class, ScopeApplicationAdapter.webAppRootKey);

	public VelocityLoader() {

	}

	/**
	 * Loads the Path from the Red5-Scope
	 * 
	 */
	public void init() {
		try {
			String current_dir = ScopeApplicationAdapter.webAppPath
					+ File.separatorChar;
			
			File fInit = new File(current_dir + "WEB-INF" + File.separatorChar
					+ "velocity.properties");
			
			if (fInit.exists()) {
				Velocity.init(current_dir + "WEB-INF" + File.separatorChar
						+ "velocity.properties");
			} else {
				Velocity.init("WeBContent"+File.separatorChar+"WEB-INF" + File.separatorChar
						+ "velocity.properties");
			}

			
		} catch (Exception e) {
			log.error("Problem initializing Velocity : ", e);
			System.out.println("Problem initializing Velocity : " + e);
		}
	}

	/**
	 * http://$server:$port/openmeetings/?param=value&param2=value2 Loads the
	 * path by a given string, this is necessary cause if invoked by Servlet
	 * there is no Red5-Scope available
	 * 
	 * @param path
	 */
	public VelocityLoader(String path) {
		try {
			Velocity.init(path + "WEB-INF/velocity.properties");
		} catch (Exception e) {
			log.error("Problem initializing Velocity : " + e);
			System.out.println("Problem initializing Velocity : " + e);
		}
	}

}
