package org.openmeetings.app.data.basic.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.openmeetings.app.persistence.beans.basic.OmTimeZone;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OmTimeZoneDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(
			OmTimeZoneDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	public Long addOmTimeZone(String name, String label, String iCal,
			Integer orderId) {
		try {

			OmTimeZone omTimeZone = new OmTimeZone();

			omTimeZone.setJname(name);
			omTimeZone.setLabel(label);
			omTimeZone.setIcal(iCal);
			omTimeZone.setOrderId(orderId);
			omTimeZone.setInserted(new Date());

			omTimeZone = em.merge(omTimeZone);
			Long omTimeZoneId = omTimeZone.getOmtimezoneId();

			return omTimeZoneId;

		} catch (Exception ex) {
			log.error("[addOmTimeZone]", ex);
		}
		return null;
	}

	public List<OmTimeZone> getOmTimeZones() {
		try {
			String hql = "select sl from OmTimeZone as sl "
					+ "ORDER BY sl.orderId";

			TypedQuery<OmTimeZone> query = em.createQuery(hql, OmTimeZone.class);
			List<OmTimeZone> sList = query.getResultList();

			for (OmTimeZone omTimeZone : sList) {
				omTimeZone.setFrontEndLabel(omTimeZone.getJname() + " ("
						+ omTimeZone.getLabel() + ")");
			}

			return sList;

		} catch (Exception ex2) {
			log.error("[getOmTimeZones]: ", ex2);
		}
		return null;
	}

	public OmTimeZone getOmTimeZone(String jname) {
		try {
			String hql = "select sl from OmTimeZone as sl "
					+ "WHERE sl.jname LIKE :jname";
			TypedQuery<OmTimeZone> query = em.createQuery(hql, OmTimeZone.class);
			query.setParameter("jname", jname);
			List<OmTimeZone> sList = query.getResultList();

			if (sList.size() > 0) {
				return sList.get(0);
			}

		} catch (Exception ex2) {
			log.error("[getOmTimeZone]: ", ex2);
		}
		return null;
	}

	public OmTimeZone getOmTimeZoneById(Long omtimezoneId) {
		try {
			String hql = "select sl from OmTimeZone as sl "
					+ "WHERE sl.omtimezoneId = :omtimezoneId";
			TypedQuery<OmTimeZone> query = em.createQuery(hql, OmTimeZone.class);
			query.setParameter("omtimezoneId", omtimezoneId);
			List<OmTimeZone> sList = query.getResultList();

			if (sList.size() > 0) {
				return sList.get(0);
			}

		} catch (Exception ex2) {
			log.error("[getOmTimeZoneById]: ", ex2);
		}
		return null;
	}

}
