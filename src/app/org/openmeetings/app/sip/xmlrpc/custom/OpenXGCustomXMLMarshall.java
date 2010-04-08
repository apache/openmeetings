package org.openmeetings.app.sip.xmlrpc.custom;

import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 *
 * This class takes a Bean and marshalls it into XML the way OpenXG wants it
 *
 */
public class OpenXGCustomXMLMarshall {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGCustomXMLMarshall.class, "openmeetings");

	private static OpenXGCustomXMLMarshall instance = null;

	public static synchronized OpenXGCustomXMLMarshall getInstance() {
		if (instance == null) {
			instance = new OpenXGCustomXMLMarshall();
		}
		return instance;
	}
	
	public String openSIPgUserCreate(String client_id, String digest, String userid, String domain, 
			String first_name, String middle_i, String last_name, String password, String community_code, 
			String language_code, String email, String adminid) {
		
		String stringToPost = "<?xml version=\"1.0\" ?>"+ 
								"<methodCall>"+ 
								"	<methodName>OpenSIPg.UserCreate</methodName>"+ 
								"	<params>"+ 
								"		<param>"+ 
								"			<value>"+ 
								"				<struct>"+ 
								"					<member>"+ 
								"						<name>client_id</name>"+ 
								"						<value>"+ 
								"							<string>"+ client_id +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>digest</name>"+ 
								"						<value>"+ 
								"							<string>"+ digest +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>userid</name>"+ 
								"						<value>"+ 
								"							<string>"+ userid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>domain</name>"+ 
								"						<value>"+ 
								"							<string>"+ domain +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>first_name</name>"+ 
								"						<value>"+ 
								"							<string>"+ first_name +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>middle_i</name>"+ 
								"						<value>"+ 
								"							<string>"+ middle_i +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>last_name</name>"+ 
								"						<value>"+ 
								"							<string>"+ last_name +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>password</name>"+ 
								"						<value>"+ 
								"							<string>"+ password +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>community_code</name>"+ 
								"						<value>"+ 
								"							<string>"+ community_code +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>language_code</name>"+ 
								"						<value>"+ 
								"							<string>"+ language_code +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>email</name>"+ 
								"						<value>"+ 
								"							<string>"+ email +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>adminid</name>"+ 
								"						<value>"+ 
								"							<string>"+ adminid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"				</struct>"+ 
								"			</value>"+ 
								"		</param>"+ 
								"	</params>"+ 
								"</methodCall>";
		
		return stringToPost;
	}
	
	public String openSIPgURIUserIDAdd(String client_id, String digest, 
			String userid, String domain, 
			String adminid) {
		
//		  $params = array(client_id=>$client_id, digest=>$digest, userid=>$userid,
//				  domain=>$domain, adminid=>$adminid);
		
		String stringToPost = "<?xml version=\"1.0\" ?>"+ 
								"<methodCall>"+ 
								"	<methodName>OpenSIPg.UserConferenceAdd</methodName>"+ 
								"	<params>"+ 
								"		<param>"+ 
								"			<value>"+ 
								"				<struct>"+ 
								"					<member>"+ 
								"						<name>client_id</name>"+ 
								"						<value>"+ 
								"							<string>"+ client_id +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>digest</name>"+ 
								"						<value>"+ 
								"							<string>"+ digest +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>userid</name>"+ 
								"						<value>"+ 
								"							<string>"+ userid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>domain</name>"+ 
								"						<value>"+ 
								"							<string>"+ domain +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>adminid</name>"+ 
								"						<value>"+ 
								"							<string>"+ adminid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"				</struct>"+ 
								"			</value>"+ 
								"		</param>"+ 
								"	</params>"+ 
								"</methodCall>";
		
		return stringToPost;								
		
	}
	
	
	public String openSIPgCreateConference(String client_id, String digest, 
							String userid, String domain, 
							String start_stamp, String stop_stamp, 
							String language_code, String adminid) {
		
		
//		$params = array(client_id=>$client_id, digest=>$digest, userid=>$userid,
//				  domain=>$domain, start_stamp=>$start_stamp,
//				  stop_stamp=>$stop_stamp, language_code=>$language_code,
//				  adminid=>$adminid);
		
		String stringToPost = "<?xml version=\"1.0\" ?>"+ 
								"<methodCall>"+ 
								"	<methodName>OpenSIPg.UserConferenceAdd</methodName>"+ 
								"	<params>"+ 
								"		<param>"+ 
								"			<value>"+ 
								"				<struct>"+ 
								"					<member>"+ 
								"						<name>client_id</name>"+ 
								"						<value>"+ 
								"							<string>"+ client_id +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>digest</name>"+ 
								"						<value>"+ 
								"							<string>"+ digest +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>userid</name>"+ 
								"						<value>"+ 
								"							<string>"+ userid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>domain</name>"+ 
								"						<value>"+ 
								"							<string>"+ domain +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>start_stamp</name>"+ 
								"						<value>"+ 
								"							<string>"+ start_stamp +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>stop_stamp</name>"+ 
								"						<value>"+ 
								"							<string>"+ stop_stamp +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>language_code</name>"+ 
								"						<value>"+ 
								"							<string>"+ language_code +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"					<member>"+ 
								"						<name>adminid</name>"+ 
								"						<value>"+ 
								"							<string>"+ adminid +"</string>"+ 
								"						</value>"+ 
								"					</member>"+ 
								"				</struct>"+ 
								"			</value>"+ 
								"		</param>"+ 
								"	</params>"+ 
								"</methodCall>";
		
		return stringToPost;
	}

}
