package org.openmeetings.test.basic;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.FieldLanguageDaoImpl;
import org.openmeetings.app.data.basic.Navimanagement;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Salutationmanagement;
import org.openmeetings.app.data.user.Usermanagement;

import junit.framework.TestCase;

public class StartUpData extends TestCase {
	
	public StartUpData (String testname){
		super(testname);
	}
	
	public void testGenerateBasicNavi() throws Exception{
		
		Usermanagement.getInstance().addUserLevel("User", 1);
		Usermanagement.getInstance().addUserLevel("Moderator", 2);
		Usermanagement.getInstance().addUserLevel("Admin", 3);	
		
		Navimanagement.getInstance().addGlobalStructure("home", 1, 124, true, true, 1, "home","false", 586L);
		
		Navimanagement.getInstance().addGlobalStructure("conf", 2, 1, false, true, 1, "conference","false", 586L);
		Navimanagement.getInstance().addMainStructure("conf1", 1, 2, true, true, 1, "meeting", 2, "false");
		Navimanagement.getInstance().addMainStructure("conf2", 2, 3, true, true, 1, "classroom", 2, "false");
		
		Navimanagement.getInstance().addGlobalStructure("settings", 3, 4, false, true, 1, "setings","false", 586L);
		Navimanagement.getInstance().addMainStructure("userself", 1, 5, true, false, 1, "userself",3, "false");
		Navimanagement.getInstance().addMainStructure("roomconfiguremod", 2, 192, true, false, 1, "roomconfiguremod",3, "false");
		
		Navimanagement.getInstance().addGlobalStructure("admin", 4, 6, false, true, 2, "admin","false", 586L);
		Navimanagement.getInstance().addMainStructure("useradmin", 1, 125, true, false, 2, "useradmin",4, "false");
		Navimanagement.getInstance().addMainStructure("groupadmin", 1, 126, true, false, 2, "groupadmin",4, "false");
		Navimanagement.getInstance().addMainStructure("orgadmin", 1, 127, true, false, 3, "orgadmin",4, "false");
		Navimanagement.getInstance().addMainStructure("roomadmin", 1, 186, true, false, 3, "roomadmin",4, "false");
		
		Salutationmanagement.getInstance().addUserSalutation("Herr",261);
		Salutationmanagement.getInstance().addUserSalutation("Frau",262);
		
		//TODO: Load States from seperate XML-File
		Statemanagement.getInstance().addState("Deutschland");
		Statemanagement.getInstance().addState("France");
		Statemanagement.getInstance().addState("Italy");
		Statemanagement.getInstance().addState("Spain");
		Statemanagement.getInstance().addState("USA");
		Statemanagement.getInstance().addState("United Kingdom");
		Statemanagement.getInstance().addState("Ireland");
		Statemanagement.getInstance().addState("Danemark");
		
		//Add user
		Long user_id = Usermanagement.getInstance().registerUserInit(new Long(3),3, 1, 1, 
				"swagner", "test", "lastname", "firstname", 
				"seba.wagner@gmail.com", new java.util.Date(), "street", "no", "fax", 
				"zip", 1, "town", 0, false, null, "phone", "", false,
				"","","",false,"", false,
				"",
				"",
				false,
				true);
		
		//Add default group
		Long organisation_id = Organisationmanagement.getInstance().addOrganisation("default", user_id);
		
		//Add user to default group
		Long organisation_usersid = Organisationmanagement.getInstance().addUserToOrganisation(new Long(1), organisation_id, new Long(1),"");
		
//		Configurationmanagement.getInstance().addConfByKey(3, "allow_frontend_register", "1", 1, "");
//		
//		Configurationmanagement.getInstance().addConfByKey(3, "default_group_id", "1", 1, "");
//		
//		//this domain_id is the Organisation of users who register through the frontend
//		Configurationmanagement.getInstance().addConfByKey(3, "default_domain_id", "1", 1, "");
//		
//		Configurationmanagement.getInstance().addConfByKey(3, "smtp_server", "smtp.xmlcrm.org", 1, "this is the smtp server to send messages");
//		Configurationmanagement.getInstance().addConfByKey(3, "system_email_addr", "openmeetings@xmlcrm.org", 1, "all send EMails by the system will have this address");
//		Configurationmanagement.getInstance().addConfByKey(3, "email_username", "openmeetings@xmlcrm.org", 1, "System auth email username");
//		Configurationmanagement.getInstance().addConfByKey(3, "email_userpass", "tony123", 1, "System auth email password");		
//			
//		Configurationmanagement.getInstance().addConfByKey(3, "default_lang", "EN", 1, "Default System Language for tamplates");
//		Configurationmanagement.getInstance().addConfByKey(3, "register_mail_subject", "SignUp", 1, "The Subject for Mails sended at registration");
				
		//Todo: Load default language ID from Database
		
	}
}
