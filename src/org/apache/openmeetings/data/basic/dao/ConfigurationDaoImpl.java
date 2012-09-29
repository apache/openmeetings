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
package org.apache.openmeetings.data.basic.dao;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.OmDAO;
import org.apache.openmeetings.data.beans.basic.SearchResult;
import org.apache.openmeetings.data.user.dao.UsersDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.utils.mappings.CastMapToObject;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ConfigurationDaoImpl implements OmDAO<Configuration> {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ConfigurationDaoImpl.class, OpenmeetingsVariables.webAppRootKey);

	public static final String DEFAULT_APP_NAME = "OpenMeetings";

	@PersistenceContext
	private EntityManager em;

	@Resource(name = "usersDao")
	private UsersDaoImpl usersDao;
	private String appName = null;

	public Configuration getConfKey(String CONF_KEY) {
		try {
			TypedQuery<Configuration> query = em.createNamedQuery(
					"getConfigurationByKey", Configuration.class);
			query.setParameter("conf_key", CONF_KEY);

			List<Configuration> configs = query.getResultList();

			if (configs != null && configs.size() > 0) {
				return configs.get(0);
			}
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public List<Configuration> getConfKeys(String... keys) {
		try {
			TypedQuery<Configuration> query = em.createNamedQuery(
					"getConfigurationsByKeys", Configuration.class);
			query.setParameter("conf_keys", Arrays.asList(keys));

			return query.getResultList();
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	/**
	 * Return a object using a custom type and a default value if the key is not
	 * present
	 * 
	 * Example: Integer my_key = getConfValue("my_key", Integer.class, "15");
	 * 
	 * @param confKey
	 * @param typeObject
	 * @param defaultValue
	 * @return
	 */
	public <T> T getConfValue(String confKey, Class<T> typeObject,
			String defaultValue) {
		try {
			Configuration conf_reminder = getConfKey(confKey);

			if (conf_reminder == null) {
				log.warn("Could not find key in configuration CONF_KEY: "
						+ confKey);
			} else {
				// Use the custom value as default value
				defaultValue = conf_reminder.getConf_value();
			}

			// Either this can be directly assigned or try to find a constructor
			// that handles it
			if (typeObject.isAssignableFrom(defaultValue.getClass())) {
				return typeObject.cast(defaultValue);
			}
			Constructor<T> c = typeObject.getConstructor(defaultValue
					.getClass());
			return c.newInstance(defaultValue);

		} catch (Exception err) {
			log.error(
					"cannot be cast to return type, you have misconfigured your configuration CONF_KEY: "
							+ confKey, err);
			return null;
		}
	}

	public SearchResult<Configuration> getAllConf(int start, int max,
			String orderby, boolean asc) {
		try {
			SearchResult<Configuration> sresult = new SearchResult<Configuration>();
			sresult.setRecords(this.selectMaxFromConfigurations());
			sresult.setResult(this.getConfigurations(start, max, orderby, asc));
			sresult.setObjectName(Configuration.class.getName());
			return sresult;
		} catch (Exception ex2) {
			log.error("[getAllConf]: ", ex2);
		}
		return null;
	}

	public List<Configuration> getConfigurations(int start, int max,
			String orderby, boolean asc) {
		try {

			String query = "SELECT c FROM Configuration c " //
					+ "LEFT JOIN FETCH c.user " //
					+ "WHERE c.deleted = false " //
					+ "ORDER BY " + orderby;

			if (asc) {
				query += " ASC";
			} else {
				query += " DESC";
			}

			TypedQuery<Configuration> q = em.createQuery(query,
					Configuration.class);
			q.setFirstResult(start);
			q.setMaxResults(max);
			return q.getResultList();
		} catch (Exception ex2) {
			log.error("[getConfigurations]", ex2);
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Long selectMaxFromConfigurations() {
		try {
			log.debug("selectMaxFromConfigurations ");
			// get all users
			TypedQuery<Long> query = em
					.createQuery(
							"select count(c.configuration_id) from Configuration c where c.deleted = false",
							Long.class);
			List<Long> ll = query.getResultList();
			log.debug("selectMaxFromConfigurations" + ll.get(0));
			return ll.get(0);
		} catch (Exception ex2) {
			log.error("[selectMaxFromConfigurations] ", ex2);
		}
		return null;
	}

	public String addConfByKey(String confKey, String confValue, Long userId,
			String comment) {
		String ret = "Add Configuration";
		Configuration configuration = new Configuration();
		configuration.setConf_key(confKey);
		configuration.setConf_value(confValue);
		configuration.setStarttime(new Date());
		configuration.setDeleted(false);
		configuration.setComment(comment);
		if (userId != null)
			configuration.setUser(usersDao.get(userId));
		try {
			configuration = em.merge(configuration);
			ret = "Erfolgreich";
		} catch (Exception ex2) {
			log.error("[addConfByKey]: ", ex2);
		}
		return ret;
	}

	public Long saveOrUpdateConfiguration(LinkedHashMap<String, ?> values,
			Long userId) {
		try {
			Configuration conf = (Configuration) CastMapToObject.getInstance()
					.castByGivenObject(values, Configuration.class);
			if (conf.getConfiguration_id().equals(null)
					|| conf.getConfiguration_id() == 0
					|| conf.getConfiguration_id() == 0L) {
				conf.setConfiguration_id(null);
				conf.setStarttime(new Date());
				conf.setDeleted(false);
				return this.addConfig(conf);
			} else {
				Configuration conf2 = this.get(conf.getConfiguration_id());
				conf2.setComment(conf.getComment());
				conf2.setConf_key(conf.getConf_key());
				conf2.setConf_value(conf.getConf_value());
				conf2.setUser(usersDao.get(userId));
				conf2.setDeleted(false);
				conf2.setUpdatetime(new Date());
				return this.updateConfig(conf2);
			}
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: ", ex2);
		}
		return new Long(-1);
	}

	public Long addConfig(Configuration conf) {
		try {
			conf = em.merge(conf);
			Long configuration_id = conf.getConfiguration_id();
			return configuration_id;
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: ", ex2);
		}
		return new Long(-1);
	}

	public Long updateConfig(Configuration conf) {
		try {
			if (conf.getConfiguration_id() == null
					|| conf.getConfiguration_id() == 0
					|| conf.getConfiguration_id() == 0L) {
				em.persist(conf);
			} else {
				if (!em.contains(conf)) {
					conf = em.merge(conf);
				}
			}
			if ("crypt_ClassName".equals(conf.getConf_key())) {
				ScopeApplicationAdapter.configKeyCryptClassName = conf
						.getConf_value();
			} else if ("show.whiteboard.draw.status".equals(conf.getConf_key())) {
				ScopeApplicationAdapter.whiteboardDrawStatus = "1".equals(conf
						.getConf_value());
			}
			return conf.getConfiguration_id();
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: ", ex2);
		}
		return new Long(-1);
	}

	public Long deleteConfByConfiguration(LinkedHashMap<String, ?> values,
			Long users_id) {
		try {
			Configuration conf = (Configuration) CastMapToObject.getInstance()
					.castByGivenObject(values, Configuration.class);
			conf.setUser(usersDao.getUser(users_id));
			conf.setUpdatetime(new Date());
			conf.setDeleted(true);

			Configuration conf2 = this.get(conf.getConfiguration_id());
			conf2.setComment(conf.getComment());
			conf2.setConf_key(conf.getConf_key());
			conf2.setConf_value(conf.getConf_value());
			conf2.setUser(usersDao.getUser(users_id));
			conf2.setDeleted(true);
			conf2.setUpdatetime(new Date());

			this.updateConfig(conf2);
			return new Long(1);
		} catch (Exception ex2) {
			log.error("[deleteConfByUID]: ", ex2);
		}
		return new Long(-1);
	}

	public String getAppName() {
		if (appName == null) {
			appName = getConfValue("application.name", String.class,
					ConfigurationDaoImpl.DEFAULT_APP_NAME);
		}
		return appName;
	}

	public List<Configuration> getNondeletedConfiguration(int first, int count) {
		TypedQuery<Configuration> q = em.createNamedQuery(
				"getNondeletedConfiguration", Configuration.class);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.getResultList();
	}

	public Configuration get(long id) {
		try {
			if (id == 0) {
				return null;
			}
			return em
					.createNamedQuery("getConfigurationById",
							Configuration.class)
					.setParameter("configuration_id", id).getSingleResult();
		} catch (PersistenceException ex) {
			log.error("Could not find id " + id, ex);
		}
		return null;
	}

	public List<Configuration> get(int start, int count) {
		return getNondeletedConfiguration(start, count);
	}

	public long count() {
		return selectMaxFromConfigurations();
	}

	public void update(Configuration entity, long userId) {
		if (entity.getConfiguration_id() == null
				|| entity.getConfiguration_id() == 0) {
			entity.setStarttime(new Date());
			entity.setDeleted(false);
			this.updateConfig(entity);
		} else {
			entity.setUser(usersDao.getUser(userId));
			entity.setDeleted(false);
			entity.setUpdatetime(new Date());
			this.updateConfig(entity);
		}
	}

	public void delete(Configuration entity, long userId) {
		entity.setUser(usersDao.getUser(userId));
		entity.setDeleted(true);
		entity.setUpdatetime(new Date());
		this.updateConfig(entity);
	}
}
