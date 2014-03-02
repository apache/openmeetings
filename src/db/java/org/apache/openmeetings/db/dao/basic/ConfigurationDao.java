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

import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_CRYPT_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_MAX_UPLOAD_SIZE_KEY;
import static org.apache.openmeetings.util.OpenmeetingsVariables.configKeyCryptClassName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.whiteboardDrawStatus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.openmeetings.db.dao.IDataProviderDao;
import org.apache.openmeetings.db.dao.user.AdminUserDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.util.DaoHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Insert/update/Delete on {@link Configuration}<br/>
 * <br/>
 * It provides basic mechanism to get a Conf Key:<br/>
 * {@link #getConfValue(String, Class, String)} <br/>
 * <br/>
 * <b> {@link #get(String)} is deprecated!</b>
 * 
 * @author swagner
 * 
 */
@Transactional
public class ConfigurationDao implements IDataProviderDao<Configuration> {
	private static final Logger log = Red5LoggerFactory.getLogger(ConfigurationDao.class, webAppRootKey);
	public static final long DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 1024; // 1GB
	public static final String DEFAULT_APP_NAME = "OpenMeetings";
	public final static String[] searchFields = {"conf_key", "conf_value"};

	@PersistenceContext
	private EntityManager em;

	@Resource(name = "adminUserDao")
	private AdminUserDao adminUserDao;

	/**
	 * @deprecated Dao's are not the place to store session variables, also
	 *             updates to the key won't update this variable
	 */
	@Deprecated
	private String appName = null;

	/**
	 * Retrieves Configuration regardless of its deleted status
	 * 
	 * @param confKey
	 * @return
	 */
	public Configuration forceGet(String confKey) {
		try {
			List<Configuration> list = em.createNamedQuery("forceGetConfigurationByKey", Configuration.class)
					.setParameter("conf_key", confKey).getResultList();
			return list.isEmpty() ? null : list.get(0);
		} catch (Exception e) {
			log.error("[forceGet]: ", e);
		}
		return null;
	}

	public List<Configuration> get(String... keys) {
		List<Configuration> result = new ArrayList<Configuration>();
		for (String key : keys) { //iteration is necessary to fill list with all values 
			List<Configuration> r = em.createNamedQuery("getConfigurationsByKeys", Configuration.class)
					.setParameter("conf_keys", Arrays.asList(key))
					.getResultList();
			result.add(r.isEmpty() ? null : r.get(0));
		}
		return result;
	}

	/**
	 * Return a object using a custom type and a default value if the key is not
	 * present, or value is not set
	 * 
	 * Example: Integer my_key = getConfValue("my_key", Integer.class, "15");
	 * 
	 * @param key
	 * @param type
	 * @param defaultValue
	 * @return
	 */
	public <T> T getConfValue(String key, Class<T> type, String defaultValue) {
		try {
			List<Configuration> list = get(key);

			if (list == null || list.isEmpty() || list.get(0) == null) {
				log.warn("Could not find key in configuration CONF_KEY: " + key);
			} else {
				String val = list.get(0).getConf_value();
				// Use the custom value as default value
				if (val != null) {
					defaultValue = val;
				}
			}

			if (defaultValue == null) {
				return null;
			}
			// Either this can be directly assigned or try to find a constructor
			// that handles it
			if (type.isAssignableFrom(defaultValue.getClass())) {
				return type.cast(defaultValue);
			}
			Constructor<T> c = type.getConstructor(defaultValue.getClass());
			return c.newInstance(defaultValue);

		} catch (Exception err) {
			log.error("cannot be cast to return type, you have misconfigured your configuration CONF_KEY: " + key, err);
			return null;
		}
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
	 */
	public Configuration add(String key, String value, Long userId, String comment) {
		Configuration c = new Configuration();
		c.setConf_key(key);
		c.setConf_value(value);
		c.setComment(comment);
		return update(c, userId);
	}

	/**
	 * @deprecated please use {@link ConfigurationDao#update(Configuration, Long)}
	 */
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

	/**
	 * @deprecated please use {@link ConfigurationDao#update(Configuration, Long)}
	 * @param conf
	 * @return
	 */
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
			if (CONFIG_CRYPT_KEY.equals(conf.getConf_key())) {
				configKeyCryptClassName = conf.getConf_value();
			} else if ("show.whiteboard.draw.status".equals(conf.getConf_key())) {
				whiteboardDrawStatus = "1".equals(conf.getConf_value());
			}
			return conf.getConfiguration_id();
		} catch (Exception ex2) {
			log.error("[updateConfByUID]: ", ex2);
		}
		return new Long(-1);
	}

	public String getAppName() {
		if (appName == null) {
			appName = getConfValue("application.name", String.class,
					ConfigurationDao.DEFAULT_APP_NAME);
		}
		return appName;
	}

	public Configuration get(long id) {
		if (id <= 0) {
			return null;
		}
		return em.createNamedQuery("getConfigurationById", Configuration.class)
				.setParameter("configuration_id", id).getSingleResult();
	}

	public List<Configuration> get(int start, int count) {
		return em.createNamedQuery("getNondeletedConfiguration", Configuration.class)
				.setFirstResult(start).setMaxResults(count).getResultList();
	}

	public List<Configuration> get(String search, int start, int count, String sort) {
		TypedQuery<Configuration> q = em.createQuery(DaoHelper.getSearchQuery("Configuration", "c", search, true, false, sort, searchFields), Configuration.class);
		q.setFirstResult(start);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	public long count() {
		return em.createNamedQuery("countConfigurations", Long.class).getSingleResult();
	}

	public long count(String search) {
		TypedQuery<Long> q = em.createQuery(DaoHelper.getSearchQuery("Configuration", "c", search, true, true, null, searchFields), Long.class);
		return q.getSingleResult();
	}
	
	public Configuration update(Configuration entity, Long userId) {
		return update(entity, userId, false);
	}
	
	public Configuration update(Configuration entity, Long userId, boolean deleted) {
		String key = entity.getConf_key();
		String value = entity.getConf_value();
		if (entity.getConfiguration_id() == null || entity.getConfiguration_id() <= 0) {
			entity.setStarttime(new Date());
			entity.setDeleted(deleted);
			em.persist(entity);
		} else {
			if (userId != null) {
				entity.setUser(adminUserDao.get(userId));
			}
			entity.setDeleted(deleted);
			entity.setUpdatetime(new Date());
			entity = em.merge(entity);
		}
		if (CONFIG_CRYPT_KEY.equals(key)) {
			configKeyCryptClassName = value;
		} else if ("show.whiteboard.draw.status".equals(key)) {
			whiteboardDrawStatus = "1".equals(value);
		} else if ("application.name".equals(key)) {
			appName = value;
		}
		//TODO ensure entity returned is updated
		return entity;
	}

	public void delete(Configuration entity, Long userId) {
		entity.setUpdatetime(new Date());
		this.update(entity, userId, true);
	}

	/**
	 * returns the max upload size configured by max_upload_size config key
	 * 
	 * @param configurationDao
	 * @return
	 */
	public final long getMaxUploadSize() {
		try {
			return getConfValue(CONFIG_MAX_UPLOAD_SIZE_KEY, Long.class, "" + DEFAULT_MAX_UPLOAD_SIZE);
		} catch (Exception e) {
			log.error("Invalid value saved for max_upload_size conf key: ", e);
		}
		return DEFAULT_MAX_UPLOAD_SIZE;
	}
	
	public String getCryptKey() {
		if (configKeyCryptClassName == null) {
			String cryptClass = getConfValue(CONFIG_CRYPT_KEY, String.class, null);
			if (cryptClass != null) {
				configKeyCryptClassName = cryptClass;
			}
		}

		return configKeyCryptClassName;
	}

	public boolean getWhiteboardDrawStatus() {
		if (whiteboardDrawStatus == null) {
			String drawStatus = getConfValue("show.whiteboard.draw.status", String.class, "0");
			whiteboardDrawStatus = "1".equals(drawStatus);
		}
		return whiteboardDrawStatus;
	}
}
