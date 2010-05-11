package org.openmeetings.app.conference.configutils;

import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.openmeetings.app.conference.videobeans.UserSecurity;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class UserConfigFactory {

	protected static Logger log = Red5LoggerFactory.getLogger(UserConfigFactory.class, ScopeApplicationAdapter.webAppRootKey);

	protected static String userconfigFileName = "WEB-INF/configbandwidth.xml";
	 
	private HashMap<String,UserSecurity> userConfig = new HashMap<String,UserSecurity>();

	public UserConfigFactory() { }

	public void getUserConfigFile() {
		try {

			String completePath = System.getProperties().get("/xmlcrmred5").toString()+userconfigFileName;
			//log.debug("completePath: " + completePath);
			
			DOMParser bandwidthParser = new DOMParser();

			bandwidthParser.parse(completePath);

			Document doc = bandwidthParser.getDocument();

			recurseDOMTree(doc);

		} catch (Exception err) {
			System.out.println("Err " + err);
			log.error("Err: " + err);
		}
	}

	public void recurseDOMTree(Node node) throws Exception {

		switch (node.getNodeType()) {

			case Node.ELEMENT_NODE:

				String name = node.getNodeName();

				NamedNodeMap attributes = node.getAttributes();

				if (name.equalsIgnoreCase("user")) {

					UserSecurity userSecurity = new UserSecurity();
					userSecurity.setUsername(attributes.getNamedItem("name").getNodeValue());
					userSecurity.setUserpass(attributes.getNamedItem("password").getNodeValue());
					userSecurity.setUserrole(attributes.getNamedItem("role").getNodeValue());
								
					log.debug("----------------> "+userSecurity.getUsername());
					userConfig.put("userrealm"+userConfig.size(), userSecurity);

				}

				break;

		}

		NodeList children = node.getChildNodes();

		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				recurseDOMTree(children.item(i));
			}
		}

	}

	public String checkUser(String userName, String userPass) throws Exception{
		Iterator<String> iter = userConfig.keySet().iterator();
		while(iter.hasNext()){
			UserSecurity userSecurity = userConfig.get(iter.next());
			log.debug("----> pingTime"+userSecurity.getUsername());

			if (userSecurity.getUsername().equals(userName) && userSecurity.getUserpass().equals(userPass)){
				log.debug("Found it: "+userSecurity.getUsername());
				return userSecurity.getUserrole();
			}
		}
		return "No";
	}

	/**
	 * @return the userConfig
	 */
	public HashMap<String, UserSecurity> getUserConfig() {
		return userConfig;
	}

	/**
	 * @param userConfig the userConfig to set
	 */
	public void setUserConfig(HashMap<String, UserSecurity> userConfig) {
		this.userConfig = userConfig;
	}


}
