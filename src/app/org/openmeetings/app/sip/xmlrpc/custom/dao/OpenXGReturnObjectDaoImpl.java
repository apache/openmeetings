package org.openmeetings.app.sip.xmlrpc.custom.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openmeetings.app.persistence.beans.sip.OpenXGReturnObject;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OpenXGReturnObjectDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGReturnObjectDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);
	@PersistenceContext
	private EntityManager em;
	
	public String addOpenXGReturnObject(OpenXGReturnObject openXGReturnObject) {
	try {
		
		openXGReturnObject.setInserted(new Date());
		
		em.merge(openXGReturnObject);
	} catch (Exception ex2) {
		log.error("[addOpenXGReturnObject]: ",ex2);
	}
	return null;
}
}
