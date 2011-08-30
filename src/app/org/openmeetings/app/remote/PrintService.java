package org.openmeetings.app.remote;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.openmeetings.app.batik.beans.PrintBean;
import org.openmeetings.app.data.basic.AuthLevelmanagement;
import org.openmeetings.app.data.basic.Sessionmanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class PrintService {


	private static final Logger log = Red5LoggerFactory.getLogger(PrintService.class, ScopeApplicationAdapter.webAppRootKey);	
	
	private static HashMap<String,PrintBean> currentExportList = new HashMap<String,PrintBean>();
	@Autowired
	private Sessionmanagement sessionManagement;
    @Autowired
    private Usermanagement userManagement;
	@Autowired
	private AuthLevelmanagement authLevelManagement;
	
	/*
	 * Export List
	 * 
	 */
	
	public String addPrintList(String SID, List map, int width, int height) {
		try {
			Long users_id = sessionManagement.checkSession(SID);
	        Long user_level = userManagement.getUserLevelByID(users_id);
	        if (authLevelManagement.checkUserLevel(user_level)) {
	        	String hashRaw = ""+new Date();
	        	String hash = MD5.do_checksum(hashRaw);
	        	this.addPrintItembyMap(hash, map, width, height);
	        	return hash;
	        }
		} catch (Exception err) {
    		log.error("loginUser",err);
    	}
    	return null;
	}
	
	public static synchronized PrintBean getPrintItemByHash(String hash) throws Exception {
		PrintBean itemList = currentExportList.get(hash);
//		if (itemList != null) {
//			currentExportList.remove(hash);
//		}
		return itemList;
	}
	
	public static synchronized void addPrintItembyMap(String hash, List map, int width, int height) throws Exception {
		PrintBean pBean = new PrintBean(hash, map, width, height);
		currentExportList.put(hash, pBean);
	}
}
