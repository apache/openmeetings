package org.openmeetings.app.data.user;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.persistence.beans.lang.Fieldlanguagesvalues;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.app.templates.RegisterUserTemplate;
import org.openmeetings.utils.mail.MailHandler;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class Emailmanagement {

	private static final Logger log = Red5LoggerFactory.getLogger(
			Emailmanagement.class, ScopeApplicationAdapter.webAppRootKey);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private Configurationmanagement cfgManagement;
	@Autowired
	private Fieldmanagment fieldmanagment;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private RegisterUserTemplate registerUserTemplate;

	private boolean checkUserLevel(int user_level) {
		if (user_level > 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get a Mail-Object by its Mail-Id
	 * 
	 * @param mail_id
	 * @return
	 */
	/*
	 * public Emails getEmailById(long mail_id) { try { Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); Query query = session.createQuery(
	 * "select c from Emails as c where c.mail_id = :mail_id AND deleted <> :deleted"
	 * ); query.setParameter("mail_id", mail_id); query.setParameter("deleted",
	 * "true"); List ll = query.getResultList(); tx.commit();
	 * HibernateUtil.closeSession(idf); if (ll.size() > 0) { return (Emails)
	 * ll.get(0); } } catch (HibernateException ex) { log.error("[getEmailById]"
	 * ,ex); } catch (Exception ex2) { log.error("[getEmailById]" ,ex2); }
	 * return null; }
	 */

	/*
	 * public List getemails(Long USER_ID) { try { Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); Query query = session
	 * .createQuery("select c from emails as c where c.USER_ID = :USER_ID");
	 * query.setParameter("USER_ID", USER_ID.longValue()); List lt =
	 * query.getResultList(); tx.commit(); HibernateUtil.closeSession(idf);
	 * return lt; } catch (HibernateException ex) { log.error("getemails",ex); }
	 * catch (Exception ex2) { log.error("getemails",ex2); } return null; }
	 */

	/*
	 * public Adresses_Emails getAdresses_EmailsByMail(String email) { try {
	 * String hql = "select c from Adresses_Emails as c " +
	 * " where c.mail.email = :email "; Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); Query query =
	 * session.createQuery(hql); query.setParameter("email", email);
	 * Adresses_Emails e = (Adresses_Emails) query.getSingleResult();
	 * tx.commit(); HibernateUtil.closeSession(idf); return e; } catch
	 * (HibernateException ex) { log.error("getAdresses_EmailsByMail",ex); }
	 * catch (Exception ex2) { log.error("getAdresses_EmailsByMail",ex2); }
	 * return null; }
	 */

	/**
	 * 
	 * @param address
	 * @return
	 */
	// ---------------------------------------------------------------------------
	/*
	 * public Adresses_Emails getAdresses_EmailsByAddress(Long address) {
	 * 
	 * log.debug("Emailmanagement.getAdresses_EmailsByAddress");
	 * 
	 * try { String hql = "select c from Adresses_Emails as c " +
	 * " where c.adresses_id = :address "; Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); Query query =
	 * session.createQuery(hql); query.setParameter("address", address);
	 * 
	 * Adresses_Emails e = (Adresses_Emails) query.getSingleResult();
	 * tx.commit(); HibernateUtil.closeSession(idf); return e; } catch
	 * (HibernateException ex) { log.error("getAdresses_EmailsByAddress",ex); }
	 * catch (Exception ex2) { log.error("getAdresses_EmailsByAddress",ex2); }
	 * 
	 * return null; }
	 */
	// ---------------------------------------------------------------------------

	/**
	 * @author becherer
	 */
	// --------------------------------------------------------------------------------
	/*
	 * public void updateEmail(Emails mail){
	 * 
	 * log.debug("Emailmanagement.updateEmail : " + mail.getEmail());
	 * 
	 * try{ Object idf = HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin();
	 * 
	 * session.mergeOrUpdate(mail); tx.commit();
	 * HibernateUtil.closeSession(idf); }catch(Exception e){
	 * log.error("Emailmanagement.updateEmail : Error updating email : " +
	 * e.getMessage()); }
	 * 
	 * }
	 */
	// --------------------------------------------------------------------------------

	/*
	 * public List getemailsCon(int CONTACT_ID) { try { Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); Query query = session
	 * .createQuery("select c from emails as c where c.CONTACT_ID = :CONTACT_ID"
	 * ); query.setParameter("CONTACT_ID", CONTACT_ID); List lt =
	 * query.getResultList(); tx.commit(); HibernateUtil.closeSession(idf);
	 * return lt; } catch (HibernateException ex) {
	 * log.error("getemailsCon",ex); } catch (Exception ex2) {
	 * log.error("getemailsCon",ex2); } return null; }
	 */

	/**
	 * Adds a mail with its connection to the adress table
	 * 
	 * @param EMail
	 * @param adresses_id
	 * @param Username
	 * @param Userpass
	 * @param comment
	 * @param sendWelcomeMail
	 * @return the new mail_id or -1
	 */
	/*
	 * public Long registerEmail(String EMail, long adresses_id, String comment)
	 * { Long mail_id = this.registerEmail(EMail, comment); if (mail_id != null)
	 * { try {
	 * 
	 * Adresses_Emails addr_emails = new Adresses_Emails();
	 * addr_emails.setAdresses_id(adresses_id);
	 * addr_emails.setMail(this.getEmailById(mail_id));
	 * addr_emails.setStarttime(new Date()); addr_emails.setDeleted("false");
	 * 
	 * Object idf = HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); long addr_emails_id = (Long)
	 * session.merge(addr_emails); tx.commit(); HibernateUtil.closeSession(idf);
	 * log.error("registerEmail addr_emails: " + addr_emails_id);
	 * 
	 * 
	 * return mail_id; } catch (HibernateException ex) {
	 * log.error("Error registerEmail: " ,ex); } catch (Exception ex2) {
	 * log.error("Error registerEmail: " ,ex2); } } return null; }
	 */

	/**
	 * adds a mail-address to the mail table
	 * 
	 * @param EMail
	 * @param Username
	 * @param Userpass
	 * @param comment
	 * @return
	 */
	/*
	 * public Long registerEmail(String EMail, String comment) { try { Emails
	 * emails = new Emails(); emails.setEmail(EMail); emails.setStarttime(new
	 * Date()); emails.setComment(comment); emails.setDeleted("false");
	 * 
	 * Object idf = HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); long email_id = (Long)
	 * session.merge(emails); tx.commit(); HibernateUtil.closeSession(idf);
	 * log.error("registerEmail id: " + email_id);
	 * 
	 * return email_id;
	 * 
	 * } catch (HibernateException ex) { log.error("registerEmail: " ,ex); }
	 * catch (Exception ex2) { log.error("registerEmail: " ,ex2); }
	 * 
	 * return null;
	 * 
	 * }
	 */

	/**
	 * sends a mail adress to the user with his account data
	 * 
	 * @param Username
	 * @param Userpass
	 * @param EMail
	 * @return
	 * @throws Exception
	 */
	public String sendMail(String Username, String Userpass, String EMail,
			String link, Boolean sendEmailWithVerficationCode) {
		String succ = "valid email";

		Integer sendEmailAtRegister = Integer.valueOf(
				cfgManagement.getConfKey(3, "sendEmailAtRegister")
						.getConf_value()).intValue();

		if (sendEmailAtRegister == 1) {

			Long default_lang_id = Long.valueOf(
					cfgManagement.getConfKey(3, "default_lang_id")
							.getConf_value()).longValue();

			if (sendEmailWithVerficationCode) {

				String verification_url = link;

				String template = registerUserTemplate
						.getRegisterUserWithVerificationTemplate(Username,
								Userpass, EMail, default_lang_id,
								verification_url);
				Fieldlanguagesvalues label = fieldmanagment
						.getFieldByIdAndLanguage(new Long(512), default_lang_id);

				succ = mailHandler.sendMail(EMail, label.getValue(), template);

			} else {

				String template = registerUserTemplate
						.getRegisterUserTemplate(Username, Userpass, EMail,
								default_lang_id);
				Fieldlanguagesvalues label = fieldmanagment
						.getFieldByIdAndLanguage(new Long(512), default_lang_id);

				succ = mailHandler.sendMail(EMail, label.getValue(), template);
			}

			return succ;
		} else {
			return "success";
		}
	}

	public String addEmailCon(String EMail, int CONTACT_ID) {
		String succ = "invalid email";
		// Emails emails = new Emails();
		// long time = CalenderI.getTimeStampMili();
		// emails.setEmail(EMail);
		// emails.setCONTACT_ID(CONTACT_ID);
		// emails.setStartdate(time);
		// emails.setUpdatedate(time);
		// try {
		// Object idf = HibernateUtil.createSession(); EntityManager session =
		// HibernateUtil.getSession();
		// EntityTransaction tx = session.getTransaction();
		// tx.begin();
		// session.merge(emails);
		// session.flush();
		// session.clear();
		// session.refresh(emails);
		// tx.commit();
		// HibernateUtil.closeSession(idf);
		// succ = "valid email";
		// } catch( HibernateException ex ) {
		// succ = "Error: "+ex;
		// } catch ( Exception ex2 ){
		// succ = "Error: "+ex2;
		// }
		return succ;
	}

	/**
	 * delete a Email-Object by a given Id
	 * 
	 * @param mail_id
	 */
	/*
	 * public void deleteEMailByID(long mail_id) { try { Emails mail =
	 * this.getEmailById(mail_id); Object idf = HibernateUtil.createSession();
	 * EntityManager session = HibernateUtil.getSession(); EntityTransaction tx
	 * = session.getTransaction(); tx.begin(); session.remove(mail);
	 * tx.commit(); HibernateUtil.closeSession(idf); } catch (HibernateException
	 * ex) { log.error("deleteEMailByID",ex); } catch (Exception ex2) {
	 * log.error("deleteEMailByID",ex2); } }
	 */

	// TODO: Change code sothat it doesn't usw HQL
	/*
	 * public String deleteEMailByUserID(int USER_ID) { String result =
	 * "Fehler im Bestellvorgang"; try { Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); String hqlDelete =
	 * "delete emails where USER_ID = :USER_ID"; int deletedEntities =
	 * session.createQuery(hqlDelete).setParameter( "USER_ID",
	 * USER_ID).executeUpdate(); //session.flush();
	 * 
	 * tx.commit(); HibernateUtil.closeSession(idf); result = "Erfolgreich" +
	 * deletedEntities; } catch (HibernateException ex) {
	 * log.error("deleteEMailByUserID",ex); } catch (Exception ex2) {
	 * log.error("deleteEMailByUserID",ex2); } return result; }
	 */

	/**
	 * Checks if a mail is already taken by someone else
	 * 
	 * @param email
	 * @return
	 */
	public boolean checkUserEMail(String email) {
		try {
			if (email.length() == 0)
				return true;
			log.debug("checkUserMail: " + email);
			Query query = em
					.createQuery("select c from Adresses as c where c.email LIKE :email AND c.deleted <> :deleted");
			query.setParameter("email", email);
			query.setParameter("deleted", "true");
			int count = query.getResultList().size();
			log.debug("size: " + count);

			if (count > 0) {
				return false;
			}
		} catch (Exception ex2) {
			log.error("checkUserEMail: ", ex2);
		}
		return true;
	}

	/**
	 * update a Email-Object by a given id
	 * 
	 * @param mail_id
	 * @param user_id
	 * @param email
	 * @return
	 */
	/*
	 * public Emails updateUserEmail(long mail_id, Long user_id, String email) {
	 * try { Emails mail = this.getEmailById(mail_id); mail.setEmail(email);
	 * mail.setUpdatetime(new Date()); Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); EntityTransaction tx =
	 * session.getTransaction(); tx.begin(); session.persist(mail); tx.commit();
	 * HibernateUtil.closeSession(idf); return mail; } catch (HibernateException
	 * ex) { log.error("[updateUserEmail] "+ex); } catch (Exception ex2) {
	 * log.error("[updateUserEmail] "+ex2); } return null; }
	 */

	/*
	 * public String updateContactEmail(int MAIL_ID, int Contact_ID, String
	 * email) { String res = "Fehler beim Update"; try { // Object idf =
	 * HibernateUtil.createSession(); EntityManager session =
	 * HibernateUtil.getSession(); // EntityTransaction tx =
	 * session.getTransaction(); // tx.begin(); // String hqlUpdate =
	 * "update emails set email= :email, CONTACT_ID = :CONTACT_ID, updatedate = :updatedate where MAIL_ID= :MAIL_ID"
	 * ; // int updatedEntities = session.createQuery( hqlUpdate ) //
	 * .setParameter("email",email) // .setParameter( "CONTACT_ID", Contact_ID )
	 * // .setParameter( "updatedate", CalenderI.getTimeStampMili() ) //
	 * .setParameter( "MAIL_ID", MAIL_ID ) // .executeUpdate(); // res =
	 * "Success"+updatedEntities; // tx.commit(); //
	 * HibernateUtil.closeSession(idf); } catch (HibernateException ex) {
	 * log.error("updateContactEmail",ex); } catch (Exception ex2) {
	 * log.error("updateContactEmail",ex2); } return res; }
	 */

}
