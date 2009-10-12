package org.openmeetings.app.data.basic;

import java.util.Iterator;
import java.util.List;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmeetings.app.hibernate.beans.basic.Naviglobal;
import org.openmeetings.app.hibernate.beans.basic.Navimain;
import org.openmeetings.app.hibernate.beans.basic.Navisub;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

public class Navimanagement {

	private static final Logger log = Logger.getLogger(Navimanagement.class);

	private static Navimanagement instance;

	private Navimanagement() {
	};

	public static synchronized Navimanagement getInstance() {
		if (instance == null) {
			instance = new Navimanagement();
		}
		return instance;
	}

	public List getMainMenu(long user_level, long USER_ID, long language_id) {
		List ll = this.getMainMenu(user_level, USER_ID);
		for (Iterator it2 = ll.iterator(); it2.hasNext();) {
			Naviglobal navigl = (Naviglobal) it2.next();
			navigl.setLabel(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navigl.getFieldvalues_id(),language_id));
			navigl.setTooltip(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navigl.getTooltip_fieldvalues_id(),language_id));
			Set s = navigl.getMainnavi();
			for (Iterator it3 = s.iterator(); it3.hasNext();) {
				Navimain navim = (Navimain) it3.next();
				navim.setLabel(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navim.getFieldvalues_id(),language_id));
				navim.setTooltip(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navim.getTooltip_fieldvalues_id(),language_id));
				for (Iterator it4 = navim.getSubnavi().iterator(); it4.hasNext();) {
					Navisub navis = (Navisub) it4.next();
					navis.setLabel(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navis.getFieldvalues_id(),language_id));
					navis.setTooltip(Fieldmanagment.getInstance().getFieldByIdAndLanguageByNavi(navis.getTooltip_fieldvalues_id(),language_id));
				}

			}
		}
		return ll;
	}

	public List getMainMenu(long user_level, long USER_ID) {
		try {
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			// Criteria crit = session.createCriteria();
			Query query = session.createQuery("select c from Naviglobal as c " +
					"where c.level_id <= :level_id AND " +
					"c.deleted='false' " +
					"order by c.naviorder");
			query.setLong("level_id", user_level);
			List navi = query.list();

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			log.error("getMainMenu "+navi.size());
			
			return navi;
		} catch (HibernateException ex) {
			log.error("getMainMenu",ex);
		} catch (Exception ex2) {
			log.error("getMainMenu",ex2);
		}
		return null;
	}

	public void addGlobalStructure(String action, int naviorder,
			long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, String deleted, Long tooltip_fieldvalues_id) {
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

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			// Criteria crit = session.createCriteria();

			session.save(ng);

			tx.commit();
			HibernateUtil.closeSession(idf);

		} catch (HibernateException ex) {
			log.error("addGlobalStructure",ex);
		} catch (Exception ex2) {
			log.error("addGlobalStructure",ex2);
		}
	}

	public void addMainStructure(String action, int naviorder,
			long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, long global_id, String deleted) {
		try {
			Navimain ng = new Navimain();
			ng.setAction(action);
			ng.setComment("");
			ng.setIcon("");
			ng.setFieldvalues_id(fieldvalues_id);
			ng.setIsleaf(isleaf);
			ng.setNaviorder(naviorder);
			ng.setIsopen(isopen);
			ng.setLevel_id(level_id);
			ng.setName(name);
			ng.setDeleted(deleted);
			ng.setGlobal_id(global_id);
			ng.setStarttime(new Date());

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			// Criteria crit = session.createCriteria();

			session.save(ng);

			tx.commit();
			HibernateUtil.closeSession(idf);

		} catch (HibernateException ex) {
			log.error("addMainStructure",ex);
		} catch (Exception ex2) {
			log.error("addMainStructure",ex2);
		}
	}

	public void addSubStructure(String action, int naviorder,
			long fieldvalues_id, boolean isleaf, boolean isopen, long level_id,
			String name, long main_id) {
		try {
			Navisub ng = new Navisub();
			ng.setAction(action);
			ng.setComment("");
			ng.setIcon("");
			ng.setNaviorder(naviorder);
			ng.setFieldvalues_id(fieldvalues_id);
			ng.setIsleaf(isleaf);
			ng.setIsopen(isopen);
			ng.setLevel_id(level_id);
			ng.setName(name);
			ng.setDeleted("false");
			ng.setMain_id(main_id);
			ng.setStarttime(new Date());

			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			// Criteria crit = session.createCriteria();

			session.save(ng);

			tx.commit();
			HibernateUtil.closeSession(idf);

		} catch (HibernateException ex) {
			log.error("addSubStructure",ex);
		} catch (Exception ex2) {
			log.error("addSubStructure",ex2);
		}
	}
}
