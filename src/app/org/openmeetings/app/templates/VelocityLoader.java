package org.openmeetings.app.templates;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.app.Velocity;
import org.red5.server.api.IScope;
import org.red5.server.api.Red5;

/**
 * 
 * @author swagner
 *
 */

public class VelocityLoader {
	
	private static final Logger log = Logger.getLogger(VelocityLoader.class);
	
	/**
	 * Loads the Path from the Red5-Scope
	 *
	 */
	public VelocityLoader(){
		try {
			IScope scope = Red5.getConnectionLocal().getScope().getParent();			
			String current_dir = scope.getResource("WEB-INF/").getFile().getAbsolutePath();	
            Velocity.init(current_dir+"/velocity.properties");
        } catch(Exception e) {
        	log.error("Problem initializing Velocity : " + e );
            System.out.println("Problem initializing Velocity : " + e );
        }
	}
	
	/**
	 * Loads the path by a given string, this is necessary cause if invoked
	 * by Servlet there is no Red5-Scope availible
	 * @param path
	 */
	public VelocityLoader(String path){
		try {
            Velocity.init(path+"WEB-INF/velocity.properties");
        } catch(Exception e) {
        	log.error("Problem initializing Velocity : " + e );
            System.out.println("Problem initializing Velocity : " + e );
        }
	}

}
