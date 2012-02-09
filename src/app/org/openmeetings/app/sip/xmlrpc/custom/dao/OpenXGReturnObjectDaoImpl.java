/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.app.sip.xmlrpc.custom.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.openmeetings.app.OpenmeetingsVariables;
import org.openmeetings.app.persistence.beans.sip.OpenXGReturnObject;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OpenXGReturnObjectDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGReturnObjectDaoImpl.class, OpenmeetingsVariables.webAppRootKey);
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
