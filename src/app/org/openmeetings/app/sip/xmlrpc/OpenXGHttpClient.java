package org.openmeetings.app.sip.xmlrpc;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class OpenXGHttpClient {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGHttpClient.class, "openmeetings");

	private static OpenXGHttpClient instance = null;

	public static synchronized OpenXGHttpClient getInstance() {
		if (instance == null) {
			instance = new OpenXGHttpClient();
		}
		return instance;
	}
	
	public void testConnection() {
		try {
			
			log.debug("Test Connection");
			
			// Get target URL
	        String strURL = "http://******/manager/xml_rpc_server.php";
	        // Get file to be posted
	        String stringToPost = "<?xml version=\"1.0\" ?>"+ System.getProperty("line.separator") +
										"<methodCall>"+ System.getProperty("line.separator") +
										"	<methodName>OpenSIPg.UserCreate</methodName>"+ System.getProperty("line.separator") +
										"	<params>"+ System.getProperty("line.separator") +
										"		<param>"+ System.getProperty("line.separator") +
										"			<value>"+ System.getProperty("line.separator") +
										"				<struct>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>client_id</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>user_admin</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>digest</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>23fe6626265cb55fc7b631f8c043ea1a</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>userid</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>067201101</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>domain</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>voipt3multifi</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>first_name</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>Matti</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>middle_i</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>X</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>last_name</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>Virtanen</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>password</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>password</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>community_code</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>999</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>language_code</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>fi</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>email</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>matti@suckscom</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"					<member>"+ System.getProperty("line.separator") +
										"						<name>adminid</name>"+ System.getProperty("line.separator") +
										"						<value>"+ System.getProperty("line.separator") +
										"							<string>matti</string>"+ System.getProperty("line.separator") +
										"						</value>"+ System.getProperty("line.separator") +
										"					</member>"+ System.getProperty("line.separator") +
										"				</struct>"+ System.getProperty("line.separator") +
										"			</value>"+ System.getProperty("line.separator") +
										"		</param>"+ System.getProperty("line.separator") +
										"	</params>"+ System.getProperty("line.separator") +
										"</methodCall>";
	        
	        // Prepare HTTP post
	        PostMethod post = new PostMethod(strURL);
	        // Request content will be retrieved directly
	        // from the input stream
	        RequestEntity entity = new StringRequestEntity(stringToPost);
	        post.setRequestEntity(entity);
	        
	        
	        // Get HTTP client
	        HttpClient httpclient = new HttpClient();
	        // Execute request
	            int result = httpclient.executeMethod(post);
	            // Display status code
	            System.out.println("Response status code: " + result);
	            // Display response
	            System.out.println("Response body: ");
	            System.out.println(post.getResponseBodyAsString());

			
			
		} catch (Exception err) {
			log.error("[testConnection]",err);
		}
	}
	
	public String digest_calculate(Object[] params) throws Exception {
		String stringToMd5 = "";
		
		for (int i=0;i<params.length;i++) {
			stringToMd5 += params[i];
		}
		
		return MD5.do_checksum(stringToMd5);
		
	}

}
