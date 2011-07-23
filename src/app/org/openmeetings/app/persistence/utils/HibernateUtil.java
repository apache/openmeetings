package org.openmeetings.app.persistence.utils;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class HibernateUtil {
	
	private static final Logger log = Red5LoggerFactory.getLogger(HibernateUtil.class, ScopeApplicationAdapter.webAppRootKey);

	/** Read the configuration, will share across threads**/
	  private static EntityManagerFactory sessionFactory;
	  /** the per thread session **/
	  private static final ThreadLocal<EntityManager> currentSession = new ThreadLocal<EntityManager>();
	  /** The constants for describing the ownerships **/
	  private static final Owner trueOwner = new Owner(true);
	  private static final Owner fakeOwner = new Owner(false); 
	  
	  /** set this to false to test with JUnit **/
	  private static final boolean isLife = true;
	  /**
	   * get the hibernate session and set it on the thread local. Returns trueOwner if 
	   * it actually opens a session
	   */
	  public static Object createSession() throws Exception{
		  EntityManager em = (EntityManager)currentSession.get();  
	    if(em == null){
	      em = getSessionFactory().createEntityManager(); 
	      currentSession.set(em);
	      return trueOwner;
	    }
	    return fakeOwner;
	  }
	  /**
	   * The method for closing a session. The close  and flush 
	   * will be executed only if the session is actually created
	   * by this owner.  
	   */
	  public synchronized static void closeSession(Object ownership) throws Exception{
	    if(((Owner)ownership).identity){
	      //System.out.println("Identity is accepted. Now closing the session");
	    	EntityManager em = (EntityManager)currentSession.get();
		    em.close();
		    currentSession.set(null);
	    }else {
	       //System.out.println("Identity is rejected. Ignoring the request");
	    }
	  }  
	  /**
	   * returns the current session
	   */
	  public synchronized static EntityManager getSession(){ 
	  	return (EntityManager)currentSession.get();
	  } 
	  
	  /** 
	   * Creating a session factory , if not already loaded
	   */
	  private synchronized static EntityManagerFactory getSessionFactory() {
		try {
			if (sessionFactory == null) {
	            sessionFactory = Persistence.createEntityManagerFactory(ScopeApplicationAdapter.webAppRootKey);
			}
			return sessionFactory;
		} catch (Exception err) {
			log.error("getSessionFactory",err);
		}
		return null;
	}  
	 
	  /**
		 * Internal class , for handling the identity. Hidden for the developers
		 */
	  private static class Owner {
	     public Owner(boolean identity){
	      this.identity = identity;
	     }
	     boolean identity = false;        
	  }  
}
