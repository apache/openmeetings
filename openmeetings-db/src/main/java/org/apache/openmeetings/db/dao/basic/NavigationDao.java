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
package org.apache.openmeetings.db.dao.basic;

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.LEVEL_GROUP_ADMIN;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.entity.basic.Naviglobal;
import org.apache.openmeetings.db.entity.basic.Navimain;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class NavigationDao {
	private static final Logger log = Red5LoggerFactory.getLogger(NavigationDao.class, webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Naviglobal getGlobalMenuEntry(Long globalId) {
		try {
			TypedQuery<Naviglobal> query = em.createNamedQuery("getNavigationById", Naviglobal.class);
			query.setParameter("id", globalId);
			return query.getSingleResult();
		} catch (Exception ex2) {
			log.error("getGlobalMenuEntry", ex2);
		}
		return null;

	}

	public List<Naviglobal> getMainMenu(int level) {
		List<Naviglobal> menu = em.createNamedQuery("getNavigation", Naviglobal.class)
				.setParameter("levelId", level)
				.getResultList();
		if (LEVEL_GROUP_ADMIN == level) {
			// filtering of child objects is not working on JPQL level
			for (Naviglobal ng : menu) {
				em.detach(ng);
				ng.setId(null); // we don't need below changed to be saved
				List<Navimain> sub = new ArrayList<>();
				for (Navimain nm : ng.getMainnavi()) {
					if (nm.getLevelId() <= level) {
						sub.add(nm);
					}
				}
				ng.setMainnavi(sub);
			}
		}
		return menu;
	}

	public Naviglobal addGlobalStructure(int naviorder, String labelId, int levelId, String name, String tooltipLabelId) {
		Naviglobal ng = new Naviglobal();
		ng.setComment("");
		ng.setIcon("");
		ng.setNaviorder(naviorder);
		ng.setLabelId(labelId);
		ng.setDeleted(false);
		ng.setLevelId(levelId);
		ng.setName(name);
		ng.setInserted(new Date());
		ng.setTooltipLabelId(tooltipLabelId);

		em.persist(ng);
		return ng;
	}

	public void addMainStructure(String action, String params, int naviorder, String labelId,
			int levelId, String name, Long globalId, String tooltipLabelId) {
		Naviglobal ng = getGlobalMenuEntry(globalId);

		Navimain nm = new Navimain();
		nm.setAction(action);
		nm.setParams(params);
		nm.setComment("");
		nm.setIcon("");
		nm.setLabelId(labelId);
		nm.setNaviorder(naviorder);
		nm.setLevelId(levelId);
		nm.setName(name);
		nm.setDeleted(false);
		nm.setGlobalId(globalId);
		nm.setInserted(new Date());
		nm.setTooltipLabelId(tooltipLabelId);

		ng.getMainnavi().add(nm);

		em.merge(ng);
	}

}
