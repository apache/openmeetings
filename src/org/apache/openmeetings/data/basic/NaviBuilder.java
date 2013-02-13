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
package org.apache.openmeetings.data.basic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.persistence.beans.basic.Naviglobal;
import org.apache.openmeetings.persistence.beans.basic.Navimain;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NaviBuilder {

	private static final Logger log = Red5LoggerFactory.getLogger(
			NaviBuilder.class, OpenmeetingsVariables.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FieldManager fieldManager;

	public Naviglobal getGlobalMenuEntry(long globalId) {
		try {
			TypedQuery<Naviglobal> query = em.createNamedQuery("getNavigationById", Naviglobal.class);
			query.setParameter("global_id", globalId);
			return query.getSingleResult();
		} catch (Exception ex2) {
			log.error("getGlobalMenuEntry", ex2);
		}
		return null;
		
	}
	
	public List<Naviglobal> getMainMenu(long user_level, long USER_ID, long language_id) {
		List<Naviglobal> ll = getMainMenu(user_level, USER_ID);
		for (Naviglobal navigl : ll) {
			navigl.setLabel(fieldManager.getFieldByIdAndLanguageByNavi(
					navigl.getFieldvalues_id(), language_id));
			navigl.setTooltip(fieldManager.getFieldByIdAndLanguageByNavi(
					navigl.getTooltip_fieldvalues_id(), language_id));
			for (Navimain navim : navigl.getMainnavi()) {
				navim.setLabel(fieldManager.getFieldByIdAndLanguageByNavi(
						navim.getFieldvalues_id(), language_id));
				navim.setTooltip(fieldManager.getFieldByIdAndLanguageByNavi(
						navim.getTooltip_fieldvalues_id(), language_id));
			}
		}
		return ll;
	}

	public List<Naviglobal> getMainMenu(long user_level, long USER_ID) {
		try {
			TypedQuery<Naviglobal> query = em.createNamedQuery("getNavigation", Naviglobal.class);
			query.setParameter("level_id", user_level);
			List<Naviglobal> navi = query.getResultList();
			return navi;
		} catch (Exception ex2) {
			log.error("getMainMenu", ex2);
		}
		return null;
	}

	public void addGlobalStructure(String action, int naviorder,
			long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, boolean deleted, Long tooltip_fieldvalues_id) {
		try {
			Naviglobal ng = new Naviglobal();
			ng.setAction(action);
			ng.setComment("");
			ng.setIcon("");
			ng.setNaviorder(naviorder);
			ng.setFieldvalues_id(fieldvalues_id);
			ng.setIsleaf(isleaf);
			ng.setIsopen(isopen);
			ng.setDeleted(deleted);
			ng.setLevel_id(level_id);
			ng.setName(name);
			ng.setStarttime(new Date());
			ng.setTooltip_fieldvalues_id(tooltip_fieldvalues_id);
			// CriteriaBuilder crit = em.getCriteriaBuilder();

			em.merge(ng);

		} catch (Exception ex2) {
			log.error("addGlobalStructure", ex2);
		}
	}

	public void addMainStructure(String action, String params, int naviorder,
			long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, long global_id, boolean deleted,
			Long tooltip_fieldvalues_id) {
		try {
			Naviglobal ng = getGlobalMenuEntry(global_id);
			List<Navimain> mainEntries = ng.getMainnavi();
			mainEntries = (mainEntries == null) ? new ArrayList<Navimain>() : mainEntries;
			
			Navimain nm = new Navimain();
			nm.setAction(action);
			nm.setParams(params);
			nm.setComment("");
			nm.setIcon("");
			nm.setFieldvalues_id(fieldvalues_id);
			nm.setIsleaf(isleaf);
			nm.setNaviorder(naviorder);
			nm.setIsopen(isopen);
			nm.setLevel_id(level_id);
			nm.setName(name);
			nm.setDeleted(deleted);
			nm.setGlobal_id(global_id);
			nm.setStarttime(new Date());
			nm.setTooltip_fieldvalues_id(tooltip_fieldvalues_id);
			
			mainEntries.add(nm);
			ng.setMainnavi(mainEntries);
			
			em.merge(ng);

		} catch (Exception ex2) {
			log.error("addMainStructure", ex2);
		}
	}

}
