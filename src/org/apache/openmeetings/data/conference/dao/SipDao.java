package org.apache.openmeetings.data.conference.dao;

import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.action.ConfbridgeListAction;
import org.asteriskjava.manager.action.DbDelAction;
import org.asteriskjava.manager.action.DbDelTreeAction;
import org.asteriskjava.manager.action.DbGetAction;
import org.asteriskjava.manager.action.DbPutAction;
import org.asteriskjava.manager.action.ManagerAction;
import org.asteriskjava.manager.response.ManagerResponse;

public class SipDao {
	public static final String ASTERISK_OM_FAMILY = "openmeetings";
	public static final String ASTERISK_OM_KEY = "rooms";
	private String sipHostname;
	private int sipPort;
	private String sipUsername;
	private String sipPassword;
	private ManagerConnectionFactory factory;
	private ManagerConnection connection;
	
	@SuppressWarnings("unused")
	private SipDao() {
		//prohibited default constructor
	}
	
    public SipDao(String sipHostname, int sipPort, String sipUsername, String sipPassword) {
    	this.sipHostname = sipHostname;
    	this.sipPort = sipPort;
    	this.sipUsername = sipUsername;
    	this.sipPassword = sipPassword;
    	factory = new ManagerConnectionFactory(this.sipHostname, this.sipPort, this.sipUsername, this.sipPassword);
    	connection = factory.createManagerConnection(); //TODO secure
    }

    private ManagerResponse exec(ManagerAction action) {
    	try {
	    	connection.login();
	    	ManagerResponse r = connection.sendAction(action);
	    	connection.logoff();
	    	return r;
    	} catch (Exception e) {
    		//
    	}
    	return null;
    }
    
    public String get(String confno) {
    	String pin = null;
    	DbGetAction da = new DbGetAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY + "/" + confno);
    	ManagerResponse r = exec(da);
    	if (r != null) {
    		pin = r.getResponse();
    	}
    	return pin;
    }
    
    public void update(String confno, String pin) {
    	delete(confno);
		DbPutAction da = new DbPutAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY + "/" + confno, pin);
		exec(da);
    }
    
    public void delete() {
    	DbDelTreeAction da = new DbDelTreeAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY);
		exec(da);
    }
    
    public void delete(String confno) {
    	DbDelAction da = new DbDelAction(ASTERISK_OM_FAMILY, ASTERISK_OM_KEY + "/" + confno);
    	exec(da);
    }
    
    public Integer countUsers(String confno) {//TODO fixme
    	if (confno == null) {
    		return null;
    	}
    	ConfbridgeListAction da = new ConfbridgeListAction(confno);
    	ManagerResponse r = exec(da);
    	if (r != null) {
    		
    	}
    	return 0;//TBD
    }
}
