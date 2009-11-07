package org.openmeetings.app.data.basic;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmeetings.app.data.beans.basic.SearchResult;
import org.openmeetings.app.hibernate.beans.lang.FieldLanguage;
import org.openmeetings.app.hibernate.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.hibernate.beans.lang.Fieldvalues;
import org.openmeetings.app.hibernate.utils.HibernateUtil;

/**
 * 
 * @author sebastianwagner
 *
 */
public class Fieldmanagment {

	private static final Logger log = Red5LoggerFactory.getLogger(Fieldmanagment.class, "openmeetings");

	private static Fieldmanagment instance = null;

	private Fieldmanagment() {
	} 

	public static synchronized Fieldmanagment getInstance() {
		if (instance == null) {
			instance = new Fieldmanagment();
		}
		return instance;
	}
	
	//Reflect the Reverse Order!!
	public Fieldlanguagesvalues getFieldByIdAndLanguageByNavi(Long fieldvalues_id, Long language_id) {
		try {
			if (fieldvalues_id == null) {
				return null;
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id AND f.fieldvalues_id = :fieldvalues_id");
			query.setLong("fieldvalues_id", fieldvalues_id);
			query.setLong("language_id", language_id);
			Fieldlanguagesvalues flv = (Fieldlanguagesvalues) query.uniqueResult();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			FieldLanguage fieldLanguage = FieldLanguageDaoImpl.getInstance().getFieldLanguageById(language_id);

			//Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				Fieldlanguagesvalues remote = flv;
				Fieldlanguagesvalues toAdd = new Fieldlanguagesvalues();
				toAdd.setFieldlanguagesvalues_id(remote.getFieldlanguagesvalues_id());
				toAdd.setFieldvalues_id(remote.getFieldvalues_id());
				toAdd.setLanguage_id(remote.getLanguage_id());

				String[] splitted = remote.getValue().split(" ");
				String reverseOrder = "";
				for (int i=splitted.length-1;i>=0;i--) {
					reverseOrder += splitted[i];
					if (splitted.length != 1) {
						reverseOrder += " ";
					}
				}
				toAdd.setValue(reverseOrder);
				
				return toAdd;
			} else {
				return flv;
			}
			
		} catch (HibernateException ex) {
			log.error("[getFieldByIdAndLanguage]: " , ex);
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: " , ex2);
		}
		return null;
	}

	public Fieldlanguagesvalues getFieldByIdAndLanguage(Long fieldvalues_id, Long language_id) {
		try {
			
			String hql = "select f from Fieldlanguagesvalues f " +
							"WHERE f.language_id = :language_id " +
							"AND f.fieldvalues_id = :fieldvalues_id";
			
			Fieldlanguagesvalues flv = null;
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			Query query = session.createQuery(hql);
			
			query.setLong("fieldvalues_id", fieldvalues_id);
			query.setLong("language_id", language_id);
			List<Fieldlanguagesvalues> fList = query.list();
			
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			if (fList.size() > 0) {
				flv = fList.get(0);
			}
			
			return flv;
		} catch (HibernateException ex) {
			log.error("[getFieldByIdAndLanguage]: " , ex);
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: " , ex2);
		}
		return null;
	}
	
	public Long deleteFieldlanguagesvaluesById(Long fieldlanguagesvalues_id) {
		try {
			Fieldlanguagesvalues flv = this.getFieldlanguagesvaluesById(fieldlanguagesvalues_id);
			if (flv == null) {
				return new Long(-27);
			}
			
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.delete(flv);
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return new Long(-28);
		} catch (HibernateException ex) {
			log.error("[getFieldByIdAndLanguage]: " , ex);
		} catch (Exception ex2) {
			log.error("[getFieldByIdAndLanguage]: " , ex2);
		}
		return new Long(-1);
	}	

	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			Query query = session.createQuery("select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id ");
			query.setLong("language_id", language_id);
			List<Fieldlanguagesvalues> returnList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);

			return returnList;
		} catch (HibernateException ex) {
			log.error("[getConfKey]: " + ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: " + ex2);
		}
		return null;
	}

	public List<Map> getLabelsByLanguage(Long language_id, int start, int max) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			String sql = "select new Map(f.fieldvalues_id as id, f.value as value) from Fieldlanguagesvalues f " +
					"WHERE f.language_id = :language_id " +
					"AND f.fieldvalues_id >= :start AND f.fieldvalues_id <  :max";

			Query query = session.createQuery(sql);
			query.setLong("language_id", language_id);
			query.setLong("start", start);
			query.setLong("max", start+max);
			
			List<Map> returnList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
			
			FieldLanguage fieldLanguage = FieldLanguageDaoImpl.getInstance().getFieldLanguageById(language_id);

			//Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				
				List<Map> returnRtlList = new LinkedList<Map>();
				//List<Fieldlanguagesvalues> returnRtlList = new LinkedList<Fieldlanguagesvalues>();
				
				for (Iterator<Map> iter = returnList.iterator();iter.hasNext();) {
					Map remote = iter.next();
					
					Map toAdd = new HashMap();
					toAdd.put("id",remote.get("id"));

					String value = remote.get("value").toString();
					
					String[] splitted = value.split(" ");
					String reverseOrder = "";
					for (int i=splitted.length-1;i>=0;i--) {
						reverseOrder += splitted[i];
						if (splitted.length != 1) {
							reverseOrder += " ";
						}
					}
					toAdd.put("value",value);
					
					returnRtlList.add(toAdd);
				}
				
				return returnRtlList;
			} else {
				
				return returnList;
				
			}
			
			
		} catch (HibernateException ex) {
			log.error("[getLabelsByLanguage]: " , ex);
		} catch (Exception ex2) {
			log.error("[getLabelsByLanguage]: " , ex2);
		}
		return null;
	}
	
	public List<Fieldlanguagesvalues> getAllFieldsByLanguage(Long language_id, int start, int max) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			
			String sql = "select f from Fieldlanguagesvalues f WHERE f.language_id = :language_id " +
					"AND f.fieldvalues_id >= :start AND f.fieldvalues_id <  :max";
//			log.debug("getAllFieldsByLanguage sql: "+sql);
//			log.debug("getAllFieldsByLanguage language_id: "+language_id);
//			log.debug("getAllFieldsByLanguage start: "+start);
//			log.debug("getAllFieldsByLanguage max: "+max);

			Query query = session.createQuery(sql);
			query.setLong("language_id", language_id);
			query.setLong("start", start);
			query.setLong("max", start+max);
			
			List<Fieldlanguagesvalues> returnList = query.list();
			tx.commit();
			HibernateUtil.closeSession(idf);
//			
//			for (Iterator<Fieldlanguagesvalues> iter = returnList.iterator();iter.hasNext();){
//				Fieldlanguagesvalues flang = iter.next();
//				log.debug("IDs: "+flang.getFieldlanguagesvalues_id()+" "+flang.getFieldvalues_id());
//				
//			}
			
			FieldLanguage fieldLanguage = FieldLanguageDaoImpl.getInstance().getFieldLanguageById(language_id);

			//Check for Right To Left Languages
			if (fieldLanguage.getRtl()) {
				
				List<Fieldlanguagesvalues> returnRtlList = new LinkedList<Fieldlanguagesvalues>();
				
				for (Iterator<Fieldlanguagesvalues> iter = returnList.iterator();iter.hasNext();) {
					Fieldlanguagesvalues remote = iter.next();
					Fieldlanguagesvalues toAdd = new Fieldlanguagesvalues();
					toAdd.setFieldlanguagesvalues_id(remote.getFieldlanguagesvalues_id());
					toAdd.setFieldvalues_id(remote.getFieldvalues_id());
					toAdd.setLanguage_id(remote.getLanguage_id());

					String[] splitted = remote.getValue().split(" ");
					String reverseOrder = "";
					for (int i=splitted.length-1;i>=0;i--) {
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
				
				return returnList;
				
			}
			
			
		} catch (HibernateException ex) {
			log.error("[getConfKey]: " , ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: " , ex2);
		}
		return null;
	}
	
	public Long addFieldValueByFieldAndLanguage(Long field_id,
			Long language_id, String fieldvalue) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			Fieldlanguagesvalues flv = new Fieldlanguagesvalues();
			flv.setStarttime(new Date());
			flv.setValue(fieldvalue);
			flv.setLanguage_id(language_id);
			flv.setFieldvalues_id(field_id);
			flv.setDeleted("false");

			Long fieldlanguagesvaluesId = (Long) session.save(flv);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return fieldlanguagesvaluesId;
		} catch (HibernateException ex) {
			log.error("[getConfKey]: ",ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: ",ex2);
		}
		return null;
	}
	
	public void updateFieldValueByFieldAndLanguage(Fieldlanguagesvalues flv) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			session.update(flv);

			tx.commit();
			HibernateUtil.closeSession(idf);

		} catch (HibernateException ex) {
			log.error("[updateFieldValueByFieldAndLanguage]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFieldValueByFieldAndLanguage]: ",ex2);
		}

	}	

	public Long addField(String fieldName) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			Fieldvalues fl = new Fieldvalues();
			fl.setStarttime(new Date());
			fl.setName(fieldName);
			fl.setDeleted("false");

			Long fieldId = (Long)session.save(fl);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return fieldId;
		} catch (HibernateException ex) {
			log.error("[getConfKey]: ",ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: ",ex2);
		}
		return null;
	}
	
	public Long addFieldById(String fieldName, Long fieldvalues_id) {
		try {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();

			Fieldvalues fl = new Fieldvalues();
			fl.setFieldvalues_id(fieldvalues_id);
			fl.setStarttime(new Date());
			fl.setName(fieldName);
			fl.setDeleted("false");

			Long fieldId = (Long)session.save(fl);

			tx.commit();
			HibernateUtil.closeSession(idf);
			
			return fieldId;
		} catch (HibernateException ex) {
			log.error("[getConfKey]: ",ex);
		} catch (Exception ex2) {
			log.error("[getConfKey]: ",ex2);
		}
		return null;
	}	
	
	public List<Fieldvalues> getMixedFieldValuesList(Long language_id) throws Exception {
		List<Fieldvalues> ll = this.getFieldsValues();
		for (Iterator<Fieldvalues> iter = ll.iterator(); iter.hasNext();){
			Fieldvalues fv = iter.next();
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(fv.getFieldvalues_id(), language_id));
		}
		return ll;
	}	
	
	public Fieldvalues getFieldvaluesById(Long fieldvalues_id, Long language_id) {
		try {
			//log.error("Long fieldvalues_id, Long language_id "+fieldvalues_id+" || "+language_id);
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(fieldvalues_id, language_id));
			return fv;
		} catch (Exception e) {
			log.error("[getFieldvaluesById] ",e);
		}
		return null;
	}
	
	/**
	 * update given Field and its Label by IDs
	 * @param fieldvalues_id
	 * @param name
	 * @param fieldlanguagesvalues_id
	 * @param value
	 * @return
	 */
	public Long updateLabel(Long fieldvalues_id, String name, Long fieldlanguagesvalues_id, String value) {
		try {
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			if (fv==null) {
				return new Long(-24);
			} else {
				fv.setName(name);
				fv.setUpdatetime(new Date());
				this.updateField(fv);
			}
			Fieldlanguagesvalues flv = this.getFieldlanguagesvaluesById(fieldlanguagesvalues_id);
			if (flv==null) {
				return new Long(-25);
			} else {
				flv.setUpdatetime(new Date());
				flv.setValue(value);
				this.updateFieldLanguagesLabel(flv);
			}
			return fieldvalues_id;
		} catch (HibernateException ex) {
			log.error("[updateFieldLanguagesLabel]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ",ex2);
		}
		return new Long(-1);
	}
	
	public Long addAndUpdateLabel(Long fieldvalues_id, String name, String value, Long language_id) {
		try {
			Fieldvalues fv = this.getFieldvaluesById(fieldvalues_id);
			if (fv==null) {
				return new Long(-24);
			} else {
				fv.setName(name);
				fv.setUpdatetime(new Date());
				this.updateField(fv);
			}
			this.addFieldValueByFieldAndLanguage(fieldvalues_id, language_id, value);
			return fieldvalues_id;
		} catch (HibernateException ex) {
			log.error("[updateFieldLanguagesLabel]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ",ex2);
		}
		return new Long(-1);
	}	
	
	public Long addFieldAndLabel(String name, String value, Long language_id) {
		try {
			Long fieldvalues_id = this.addField(name);
			if (fieldvalues_id>0) {
				this.addFieldValueByFieldAndLanguage(fieldvalues_id, language_id, value);
				return fieldvalues_id;
			} else {
				return new Long(-1);
			}
		} catch (HibernateException ex) {
			log.error("[updateFieldLanguagesLabel]: ",ex);
		} catch (Exception ex2) {
			log.error("[updateFieldLanguagesLabel]: ",ex2);
		}
		return new Long(-1);
	}	
	
	public SearchResult getFieldsByLanguage(int start ,int max, String orderby, boolean asc, Long language_id){
		try {
			SearchResult sresult = new SearchResult();
			sresult.setObjectName(Fieldlanguagesvalues.class.getName());
			sresult.setRecords(this.selectMaxFromFieldsValues());
			sresult.setResult(this.getMixedFieldValuesList(start, max, orderby, asc, language_id));
			return sresult;
		} catch (HibernateException ex) {
			log.error("[getFieldsByLanguage]: ",ex);
		} catch (Exception ex2) {
			log.error("[getFieldsByLanguage]: ",ex2);
		}
		return null;
	}

	private Long selectMaxFromFieldsValues() throws Exception{
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery("select max(c.fieldvalues_id) from Fieldvalues c where c.deleted = 'false'"); 
		List ll = query.list();
		tx.commit();
		HibernateUtil.closeSession(idf);
		//log.error((Long)ll.get(0));
		return (Long)ll.get(0);				
	}	
	
	private List<Fieldvalues> getMixedFieldValuesList(int start ,int max, String orderby, boolean asc, Long language_id) throws Exception {
		List<Fieldvalues> ll = this.getFieldsValues(start, max, orderby, asc);
		for (Iterator<Fieldvalues> iter = ll.iterator(); iter.hasNext();){
			Fieldvalues fv = iter.next();
			fv.setFieldlanguagesvalue(this.getFieldByIdAndLanguage(fv.getFieldvalues_id(), language_id));
		}
		return ll;
	}
	
	private List<Fieldvalues> getFieldsValues() throws Exception {
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(Fieldvalues.class, "openmeetings");
		crit.add(Restrictions.eq("deleted", "false"));
		List<Fieldvalues> ll = crit.list();
		tx.commit();
		HibernateUtil.closeSession(idf);
		return ll;
	}
	
	private List<Fieldvalues> getFieldsValues(int start ,int max, String orderby, boolean asc) throws Exception {
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(Fieldvalues.class, "openmeetings");
		crit.add(Restrictions.eq("deleted", "false"));
		crit.setFirstResult(start);
		crit.setMaxResults(max);
		if (asc) crit.addOrder(Order.asc(orderby));
		else crit.addOrder(Order.desc(orderby));
		List<Fieldvalues> ll = crit.list();
		tx.commit();
		HibernateUtil.closeSession(idf);
		return ll;
	}		
	
	public Fieldvalues getFieldvaluesById(Long fieldvalues_id) throws Exception {
		String hql = "select f from Fieldvalues f WHERE f.fieldvalues_id = :fieldvalues_id ";
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setLong("fieldvalues_id", fieldvalues_id);
		Fieldvalues fv = (Fieldvalues) query.uniqueResult();
		tx.commit();
		HibernateUtil.closeSession(idf);
		return fv;
	}
	
	private Fieldlanguagesvalues getFieldlanguagesvaluesById(Long fieldlanguagesvalues_id) throws Exception {
		String hql = "select f from Fieldlanguagesvalues f WHERE f.fieldlanguagesvalues_id = :fieldlanguagesvalues_id ";
		Object idf = HibernateUtil.createSession();
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		Query query = session.createQuery(hql);
		query.setLong("fieldlanguagesvalues_id", fieldlanguagesvalues_id);
		Fieldlanguagesvalues flv = (Fieldlanguagesvalues) query.uniqueResult();
		tx.commit();
		HibernateUtil.closeSession(idf);
		return flv;
	}
	
	private void updateField(Fieldvalues fv) throws Exception {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(fv);
			tx.commit();
			HibernateUtil.closeSession(idf);
	}
	
	private void updateFieldLanguagesLabel(Fieldlanguagesvalues flv) throws Exception {
			Object idf = HibernateUtil.createSession();
			Session session = HibernateUtil.getSession();
			Transaction tx = session.beginTransaction();
			session.update(flv);
			tx.commit();
			HibernateUtil.closeSession(idf);
	}	

}
