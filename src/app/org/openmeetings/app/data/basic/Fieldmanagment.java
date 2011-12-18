package org.openmeetings.app.data.basic;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.persistence.beans.lang.FieldLanguage;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.persistence.beans.lang.Fieldvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author sebastianwagner
 * 
 */
@Transactional
public class Fieldmanagment {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Fieldmanagment.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FieldLanguageDaoImpl fieldLanguageDaoImpl;

	@Autowired
	private Configurationmanagement cfgManagement;
	
	// Reflect the Reverse Order!!
	public Fieldlanguagesvalues getFieldByIdAndLanguageByNavi(
			Long fieldvalues_id, Long language_id) {
		try {
			if (fieldvalues_id == null) {
				return null;
			}

			Query query = em
					.createQuery("select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id AND f.fieldvalues_id = :fieldvalues_id");
			query.setParameter("fieldvalues_id", fieldvalues_id);
			query.setParameter("language_id", language_id);
			Fieldlanguagesvalues flv = null;
			try {
				flv = performReplace((Fieldlanguagesvalues) query.getSingleResult());
			} catch (NoResultException ex) {
			}

			FieldLanguage fieldLanguage = fieldLanguageDaoImpl
					.getFieldLanguageById(language_id);

			log.debug("Getting FieldById for Language "
					+ fieldLanguage.getName());

			// Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {

				log.debug("Language requieres RTL");

				Fieldlanguagesvalues remote = flv;
				Fieldlanguagesvalues toAdd = new Fieldlanguagesvalues();
				toAdd.setFieldlanguagesvalues_id(remote
						.getFieldlanguagesvalues_id());
				toAdd.setFieldvalues_id(remote.getFieldvalues_id());
				toAdd.setLanguage_id(remote.getLanguage_id());

				String[] splitted = remote.getValue().split(" ");
				String reverseOrder = "";
				for (int i = splitted.length - 1; i >= 0; i--) {
					reverseOrder += splitted[i];
					if (splitted.length != 1) {
						reverseOrder += " ";
					}
				}
				toAdd.setValue(reverseOrder);

				return toAdd;
			} else {
				log.debug("Language doesnt requiere RTL");

				return flv;
			}

		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: ", ex2);
		}
		return null;
	}

	public Fieldlanguagesvalues getFieldByIdAndLanguage(Long fieldvalues_id,
			Long language_id) {
		try {

			String hql = "select f from Fieldlanguagesvalues as f "
					+ "WHERE f.language_id = :language_id "
					+ "AND f.fieldvalues_id = :fieldvalues_id";

			Fieldlanguagesvalues flv = null;

			Query query = em.createQuery(hql);

			query.setParameter("fieldvalues_id", fieldvalues_id);
			query.setParameter("language_id", language_id);
			@SuppressWarnings("unchecked")
			List<Fieldlanguagesvalues> fList = query.getResultList();

			if (fList.size() > 0) {
				flv = fList.get(0); //replace should not be performed here to enable string editing via admin
			}

			return flv;
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: ", ex2);
		}
		return null;
	}

	public Long deleteFieldlanguagesvaluesById(Long fieldlanguagesvalues_id) {
		try {
			Fieldlanguagesvalues flv = this
					.getFieldlanguagesvaluesById(fieldlanguagesvalues_id);
			if (flv == null) {
				return new Long(-27);
			}

			flv = em.find(Fieldlanguagesvalues.class, fieldlanguagesvalues_id);

			return new Long(-28);
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: ", ex2);
		}
		return new Long(-1);
	}

	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id) {
		try {

			Query query = em
					.createQuery("select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id ");
			query.setParameter("language_id", language_id);
			@SuppressWarnings("unchecked")
			List<Fieldlanguagesvalues> returnList = performReplace(query.getResultList());

			return returnList;
		} catch (Exception ex2) {
			log.error("[getConfKey]: " + ex2);
		}
		return null;
	}

	public List<Map<String, Object>> getLabelsByLanguage(Long language_id,
			int start, int max) {
		try {

			String sql = "select f from Fieldlanguagesvalues as f "
					+ "WHERE f.language_id = :language_id "
					+ "AND f.fieldvalues_id >= :start AND f.fieldvalues_id <  :max";

			Query query = em.createQuery(sql);
			query.setParameter("language_id", language_id);
			query.setParameter("start", new Long(start));
			query.setParameter("max", new Long(start + max));

			@SuppressWarnings("unchecked")
			List<Fieldlanguagesvalues> results = performReplace(query.getResultList());
			List<Map<String, Object>> returnList = new LinkedList<Map<String, Object>>();
			if (results.size() != 0) {
				Iterator<Fieldlanguagesvalues> flIterator = results.iterator();
				while (flIterator.hasNext()) {
					Fieldlanguagesvalues fl = flIterator.next();
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", fl.getFieldvalues_id());
					map.put("value", fl.getValue());
					returnList.add(map);
				}
			}
			FieldLanguage fieldLanguage = fieldLanguageDaoImpl
					.getFieldLanguageById(language_id);

			log.debug("Getting Labels for Language " + fieldLanguage.getName());

			// Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				log.debug("Language requieres RTL!");

				List<Map<String, Object>> returnRtlList = new LinkedList<Map<String, Object>>();

				for (Iterator<Map<String, Object>> iter = returnList.iterator(); iter
						.hasNext();) {
					Map<String, Object> remote = iter.next();

					Map<String, Object> toAdd = new HashMap<String, Object>();
					toAdd.put("id", remote.get("id"));

					String value = remote.get("value").toString();

					String[] splitted = value.split(" ");
					String reverseOrder = "";
					for (int i = splitted.length - 1; i >= 0; i--) {
						reverseOrder += splitted[i];
						if (splitted.length != 1) {
							reverseOrder += " ";
						}
					}
					log.debug("reverseOrder " + reverseOrder);
					toAdd.put("value", value);

					returnRtlList.add(toAdd);
				}

				return returnRtlList;
			} else {
				log.debug("Language doesnt requiere RTL!");

				return returnList;

			}

		} catch (Exception ex2) {
			log.error("[getLabelsByLanguage]: ", ex2);
			ex2.printStackTrace();
		}
		return null;
	}

	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id,
			int start, int max) {
		try {

			String sql = "select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id "
					+ "AND f.fieldvalues_id >= :start AND f.fieldvalues_id <  :max";

			Query query = em.createQuery(sql);
			query.setParameter("language_id", language_id);
			query.setParameter("start", start);
			query.setParameter("max", start + max);

			@SuppressWarnings("unchecked")
			List<Fieldlanguagesvalues> returnList = performReplace(query.getResultList());
			FieldLanguage fieldLanguage = fieldLanguageDaoImpl
					.getFieldLanguageById(language_id);

			log.debug("GEtting all fields by language : "
					+ fieldLanguage.getName());

			// Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				log.debug("language : " + fieldLanguage.getName()
						+ " requieres RTL");

				List<Fieldlanguagesvalues> returnRtlList = new LinkedList<Fieldlanguagesvalues>();

				for (Iterator<Fieldlanguagesvalues> iter = returnList
						.iterator(); iter.hasNext();) {
					Fieldlanguagesvalues remote = iter.next();
					Fieldlanguagesvalues toAdd = new Fieldlanguagesvalues();
					toAdd.setFieldlanguagesvalues_id(remote
							.getFieldlanguagesvalues_id());
					toAdd.setFieldvalues_id(remote.getFieldvalues_id());
					toAdd.setLanguage_id(remote.getLanguage_id());

					String[] splitted = remote.getValue().split(" ");
					String reverseOrder = "";
					for (int i = splitted.length - 1; i >= 0; i--) {
						reverseOrder += splitted[i];
						if (splitted.length != 1) {
							reverseOrder += " ";
						}
					}
					toAdd.setValue(reverseOrder);

					returnRtlList.add(toAdd);
				}

				return returnRtlList;
			} else {
				log.debug("language : " + fieldLanguage.getName()
						+ " requieres NO RTL");

				return returnList;

			}

		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public Long addFieldValueByFieldAndLanguage(Long field_id,
			Long language_id, String fieldvalue) {
		try {

			Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
			flv.setStarttime(new Date());
			flv.setValue(fieldvalue);
			flv.setLanguage_id(language_id);
			flv.setFieldvalues_id(field_id);
			flv.setDeleted("false");

			flv = em.merge(flv);
			Long fieldlanguagesvaluesId = flv.getFieldlanguagesvalues_id();

			return fieldlanguagesvaluesId;
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public void updateFieldValueByFieldAndLanguage(Fieldlanguagesvalues flv) {
		try {

			if (flv.getFieldlanguagesvalues_id() == null) {
				em.persist(flv);
			} else {
				if (!em.contains(flv)) {
					em.merge(flv);
				}
			}

		} catch (Exception ex2) {
			log.error("[updateFieldValueByFieldAndLanguage]: ", ex2);
		}

	}

	public Long addField(String fieldName) {
		try {

			Fieldvalues fl = new Fieldvalues();
			fl.setStarttime(new Date());
			fl.setName(fieldName);
			fl.setDeleted("false");

			fl = em.merge(fl);
			Long fieldId = fl.getFieldvalues_id();

			return fieldId;
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public Long addFieldById(String fieldName, Long fieldvalues_id) {
		try {

			Fieldvalues fl = new Fieldvalues();
			fl.setFieldvalues_id(fieldvalues_id);
			fl.setStarttime(new Date());
			fl.setName(fieldName);
			fl.setDeleted("false");

			fl = em.merge(fl);
			Long fieldId = fl.getFieldvalues_id();

			return fieldId;
		} catch (Exception ex2) {
			log.error("[getConfKey]: ", ex2);
		}
		return null;
	}

	public List<Fieldvalues> getMixedFieldValuesList(Long language_id)
			throws Exception {
		List<Fieldvalues> ll = this.getFieldsValues();
		for (Iterator<Fieldvalues> iter = ll.iterator(); iter.hasNext();) {
			Fieldvalues fv = iter.next();
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(
					fv.getFieldvalues_id(), language_id));
		}
		return ll;
	}

	public Fieldvalues getFieldvaluesById(Long fieldvalues_id, Long language_id) {
		try {
			// log.error("Long fieldvalues_id, Long language_id "+fieldvalues_id+" || "+language_id);
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(
					fieldvalues_id, language_id));
			return fv;
		} catch (Exception e) {
			log.error("[getFieldvaluesById] ", e);
		}
		return null;
	}

	/**
	 * update given Field and its Label by IDs
	 * 
	 * @param fieldvalues_id
	 * @param name
	 * @param fieldlanguagesvalues_id
	 * @param value
	 * @return
	 */
	public Long updateLabel(Long fieldvalues_id, String name,
			Long fieldlanguagesvalues_id, String value) {
		try {
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			if (fv == null) {
				return new Long(-24);
			} else {
				fv.setName(name);
				fv.setUpdatetime(new Date());
				this.updateField(fv);
			}
			Fieldlanguagesvalues flv = this
					.getFieldlanguagesvaluesById(fieldlanguagesvalues_id);
			if (flv == null) {
				return new Long(-25);
			} else {
				flv.setUpdatetime(new Date());
				flv.setValue(value);
				this.updateFieldLanguagesLabel(flv);
			}
			return fieldvalues_id;
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ", ex2);
		}
		return new Long(-1);
	}

	public Long addAndUpdateLabel(Long fieldvalues_id, String name,
			String value, Long language_id) {
		try {
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			if (fv == null) {
				return new Long(-24);
			} else {
				fv.setName(name);
				fv.setUpdatetime(new Date());
				this.updateField(fv);
			}
			this.addFieldValueByFieldAndLanguage(fieldvalues_id, language_id,
					value);
			return fieldvalues_id;
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ", ex2);
		}
		return new Long(-1);
	}

	public Long addFieldAndLabel(String name, String value, Long language_id) {
		try {
			Long fieldvalues_id = this.addField(name);
			if (fieldvalues_id > 0) {
				this.addFieldValueByFieldAndLanguage(fieldvalues_id,
						language_id, value);
				return fieldvalues_id;
			} else {
				return new Long(-1);
			}
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ", ex2);
		}
		return new Long(-1);
	}

	public SearchResult<Fieldvalues> getFieldsByLanguage(int start, int max, String orderby,
			boolean asc, Long language_id) {
		try {
			SearchResult<Fieldvalues> sresult = new SearchResult<Fieldvalues>();
			sresult.setObjectName(Fieldlanguagesvalues.class.getName());
			sresult.setRecords(this.selectMaxFromFieldsValues());
			sresult.setResult(this.getMixedFieldValuesList(start, max, orderby,
					asc, language_id));
			return sresult;
		} catch (Exception ex2) {
			log.error("[getFieldsByLanguage]: ", ex2);
		}
		return null;
	}

	private Long selectMaxFromFieldsValues() throws Exception {
		Query query = em
				.createQuery("select max(c.fieldvalues_id) from Fieldvalues c where c.deleted = 'false'");
		List<?> ll = query.getResultList();
		// log.error((Long)ll.get(0));
		return (Long) ll.get(0);
	}

	private List<Fieldvalues> getMixedFieldValuesList(int start, int max,
			String orderby, boolean asc, Long language_id) throws Exception {
		List<Fieldvalues> ll = this.getFieldsValues(start, max, orderby, asc);
		for (Iterator<Fieldvalues> iter = ll.iterator(); iter.hasNext();) {
			Fieldvalues fv = iter.next();
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(
					fv.getFieldvalues_id(), language_id));
		}
		return ll;
	}

	private List<Fieldvalues> getFieldsValues() throws Exception {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Fieldvalues> cq = cb.createQuery(Fieldvalues.class);
		Root<Fieldvalues> c = cq.from(Fieldvalues.class);
		Predicate condition = cb.equal(c.get("deleted"), "false");
		cq.where(condition);
		TypedQuery<Fieldvalues> q = em.createQuery(cq);
		List<Fieldvalues> ll = q.getResultList();
		return ll;
	}

	private List<Fieldvalues> getFieldsValues(int start, int max,
			String orderby, boolean asc) throws Exception {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Fieldvalues> cq = cb.createQuery(Fieldvalues.class);
		Root<Fieldvalues> c = cq.from(Fieldvalues.class);
		Predicate condition = cb.equal(c.get("deleted"), "false");
		cq.where(condition);
		cq.distinct(asc);
		if (asc) {
			cq.orderBy(cb.asc(c.get(orderby)));
		} else {
			cq.orderBy(cb.desc(c.get(orderby)));
		}
		TypedQuery<Fieldvalues> q = em.createQuery(cq);
		q.setFirstResult(start);
		q.setMaxResults(max);
		List<Fieldvalues> ll = q.getResultList();
		return ll;
	}

	public Fieldvalues getFieldvaluesById(Long fieldvalues_id) {
		String hql = "select f from Fieldvalues f WHERE f.fieldvalues_id = :fieldvalues_id ";
		Query query = em.createQuery(hql);
		query.setParameter("fieldvalues_id", fieldvalues_id);
		Fieldvalues fv = null;
		try {
			fv = (Fieldvalues) query.getSingleResult();
		} catch (NoResultException ex) {
		}
		return fv;
	}

	private Fieldlanguagesvalues getFieldlanguagesvaluesById(
			Long fieldlanguagesvalues_id) throws Exception {
		String hql = "select f from Fieldlanguagesvalues f WHERE f.fieldlanguagesvalues_id = :fieldlanguagesvalues_id ";
		Query query = em.createQuery(hql);
		query.setParameter("fieldlanguagesvalues_id", fieldlanguagesvalues_id);
		Fieldlanguagesvalues flv = null;
		try {
			flv = performReplace((Fieldlanguagesvalues) query.getSingleResult());
		} catch (NoResultException ex) {
		}
		return flv;
	}
	
	private Fieldlanguagesvalues performReplace(Fieldlanguagesvalues f) {
		String appName = cfgManagement.getAppName();
		return performReplace(f, appName);
	}
	
	private Fieldlanguagesvalues performReplace(Fieldlanguagesvalues f, String appName) {
		f.setValue(f.getValue().replaceAll("\\$APP_NAME", appName));
		return f;
	}
	
	private <T extends Collection<Fieldlanguagesvalues>> T performReplace(T flv) {
		String appName = cfgManagement.getAppName();
		
		for (Fieldlanguagesvalues f : flv) {
			performReplace(f, appName);
		}
		return flv;
	}
	
	private void updateField(Fieldvalues fv) throws Exception {
		if (fv.getFieldvalues_id() == null) {
			em.persist(fv);
		} else {
			if (!em.contains(fv)) {
				em.merge(fv);
			}
		}
	}

	private void updateFieldLanguagesLabel(Fieldlanguagesvalues flv)
			throws Exception {
		if (flv.getFieldlanguagesvalues_id() == null) {
			em.persist(flv);
		} else {
			if (!em.contains(flv)) {
				em.merge(flv);
			}
		}
	}

}
