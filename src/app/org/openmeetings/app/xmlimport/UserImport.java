package org.openmeetings.app.xmlimport;

import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openmeetings.app.data.user.Emailmanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class UserImport {

	private static final Logger log = Red5LoggerFactory.getLogger(
			UserImport.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private Usermanagement userManagement;
	@Autowired
	private Organisationmanagement organisationmanagement;
	@Autowired
	private Emailmanagement emailManagement;
	@Autowired
	private UsersDaoImpl usersDao;
	@Autowired
	private Statemanagement statemanagement;

	public Long addUsersByDocument(InputStream is) throws Exception {

		SAXReader reader = new SAXReader();
		Document document = reader.read(is);

		Element root = document.getRootElement();

		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element itemObject = i.next();
			if (itemObject.getName().equals("users")) {
				this.addUsersByDocument(itemObject);
			}
		}

		return null;
	}

	private Long addUsersByDocument(Element userRoot) throws Exception {

		for (@SuppressWarnings("unchecked")
		Iterator<Element> i = userRoot.elementIterator("user"); i.hasNext();) {
			Element itemUsers = i.next();

			Users us = new Users();

			us.setAge(CalendarPatterns.parseDate(itemUsers.element("age")
					.getText()));
			us.setAvailible(Integer.valueOf(
					itemUsers.element("availible").getText()).intValue());
			us.setDeleted(itemUsers.element("deleted").getText());
			us.setFirstname(itemUsers.element("firstname").getText());
			us.setLastname(itemUsers.element("lastname").getText());
			us.setLogin(itemUsers.element("login").getText());
			us.setPassword(itemUsers.element("pass").getText());

			us.setPictureuri(itemUsers.element("pictureuri").getText());
			if (itemUsers.element("language_id").getText().length() > 0)
				us.setLanguage_id(Long.valueOf(
						itemUsers.element("language_id").getText()).longValue());

			us.setStatus(Integer.valueOf(itemUsers.element("status").getText())
					.intValue());
			us.setRegdate(CalendarPatterns.parseDate(itemUsers.element(
					"regdate").getText()));
			us.setTitle_id(Integer.valueOf(
					itemUsers.element("title_id").getText()).intValue());
			us.setLevel_id(Long
					.valueOf(itemUsers.element("level_id").getText())
					.longValue());

			String additionalname = itemUsers.element("additionalname")
					.getText();
			String comment = itemUsers.element("comment").getText();
			// A User can not have a deleted Adress, you cannot delete the
			// Adress of an User
			// String deleted = u.getAdresses().getDeleted()
			// Phone Number not done yet
			String fax = itemUsers.element("fax").getText();
			Long state_id = Long.valueOf(
					itemUsers.element("state_id").getText()).longValue();
			String street = itemUsers.element("street").getText();
			String town = itemUsers.element("town").getText();
			String zip = itemUsers.element("zip").getText();

			String phone = "";
			if (itemUsers.element("phone") != null) {
				phone = itemUsers.element("phone").getText();
			}

			String email = "";
			if (itemUsers.element("mail") != null) {
				email = itemUsers.element("mail").getText();
			}
			// = .getText();

			boolean mailCheck = true;

			for (@SuppressWarnings("unchecked")
			Iterator<Element> itMail = itemUsers.elementIterator("emails"); itMail
					.hasNext();) {
				Element itemElement = itMail.next();
				for (@SuppressWarnings("unchecked")
				Iterator<Element> mailIterator = itemElement
						.elementIterator("mail"); mailIterator.hasNext();) {
					Element mailElement = mailIterator.next();
					email = mailElement.getText();
					if (!emailManagement.checkUserEMail(mailElement.getText())) {
						mailCheck = false;
						log.info("mailCheck = " + mailCheck);
					}
				}
			}

			// check for duplicate Login or mail:
			if (usersDao.checkUserLogin(us.getLogin()) && mailCheck) {
				us.setAdresses(street, zip,
						town, statemanagement.getStateById(state_id), additionalname, comment, fax, phone, email);

				userManagement.addUser(us);

				for (@SuppressWarnings("unchecked")
				Iterator<Element> itOrga = itemUsers
						.elementIterator("organisations"); itOrga.hasNext();) {
					Element itemElement = itOrga.next();
					for (@SuppressWarnings("unchecked")
					Iterator<Element> orgIterator = itemElement
							.elementIterator("organisation_id"); orgIterator
							.hasNext();) {
						Element orgElement = orgIterator.next();
						Long organisation_id = Long.valueOf(
								orgElement.getText()).longValue();
						organisationmanagement.addUserToOrganisation(
								us.getUser_id(), organisation_id, null, "");
					}
				}

			}
		}

		return null;
	}

}
