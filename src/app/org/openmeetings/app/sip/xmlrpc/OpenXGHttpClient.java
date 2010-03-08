package org.openmeetings.app.sip.xmlrpc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.hibernate.beans.basic.Configuration;
import org.openmeetings.app.sip.xmlrpc.custom.OpenXGCustomXMLMarshall;
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
	        String strURL = "http://*****/manager/xml_rpc_server.php";
	        // Get file to be posted
	        String stringToPost = "TEST - String";
	        
	        
	        //File f = new File("NewFileISO.xml");
	        
	        // Prepare HTTP post
	        PostMethod post = new PostMethod(strURL);
	        post.addRequestHeader("User-Agent", "OpenSIPg XML_RPC Client");

	        // Request content will be retrieved directly
	        // from the input stream
	        //RequestEntity entity = new FileRequestEntity(f, "text/xml; charset=ISO-8859-1"); 
	        
	        //String encodedString = this.readFile("/root/openmeetings/red5/webapps/openmeetings/WEB-INF/classes/NewFileISO.xml");
	        
	        RequestEntity entity = new ByteArrayRequestEntity(stringToPost.getBytes(Charset.forName("ISO-8859-1")));
	        
//	        // Prepare HTTP post
//	        PostMethod post = new PostMethod(strURL);
	        
	        post.getParams().setContentCharset("ISO-8859-1");
	        post.getParams().setVersion(HttpVersion.HTTP_1_0);
	        
	        // Request content will be retrieved directly
	        // from the input stream
//	        RequestEntity entity = new StringRequestEntity(stringToPost);
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
	
	private String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc
					.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.forName("ISO-8859-1").decode(bb).toString();
		} finally {
			stream.close();
		}
	}
	
	public String digest_calculate(Object[] params) throws Exception {
		String stringToMd5 = "";
		
		for (int i=0;i<params.length;i++) {
			stringToMd5 += params[i];
		}
		
		return MD5.do_checksum(stringToMd5);
		
	}
	
	public void openSIPgUserCreateTest() {
		try {
			
		    String first_name = "Matti";
		    String middle_i = "X";
		    String last_name = "Virtanen";
		    String email = "test@test.de";
		    String password = "password";
			
			this.openSIPgUserCreateUser(first_name, middle_i, last_name, email, password);
		
		} catch (Exception err) {

			log.error("[openSIPgUserCreateTest]",err);
			
		}
	}

	public void openSIPgUserCreateUser(String first_name, String middle_i, 
			String last_name, String email, String password) {
		try {
			
			//Check if the OpenXG Gateway is enabled in general
			Configuration sip_openxg_enable = Configurationmanagement.getInstance().getConfKey(3L, "sip.openxg.enable");
			
			if (sip_openxg_enable == null || !sip_openxg_enable.getConf_value().equals("yes")) {
				log.debug("SIP is disabled");
				return;
			}
			
			//client_id and client_secret
			Configuration openxg_client_id = Configurationmanagement.getInstance().getConfKey(3L, "openxg.client.id");
			Configuration openxg_client_secret = Configurationmanagement.getInstance().getConfKey(3L, "openxg.client.secret");
			if (openxg_client_id == null || openxg_client_secret == null) {
				throw new Exception("openxg.client.id or openxg.client.secret missing in Configuration table");
			}
			String client_id = openxg_client_id.getConf_value();
			String client_secret = openxg_client_secret.getConf_value();
		    
			//domain
			Configuration openxg_client_domain = Configurationmanagement.getInstance().getConfKey(3L, "openxg.client.domain");
			if (openxg_client_domain == null) {
				throw new Exception("openxg.client.domain missing in Configuration table");
			}
		    String domain = openxg_client_domain.getConf_value();
		    
		    //openxg_community_code
		    Configuration openxg_community_code = Configurationmanagement.getInstance().getConfKey(3L, "openxg.community.code");
			if (openxg_community_code == null) {
				throw new Exception("openxg.community.code missing in Configuration table");
			}
		    String community_code = openxg_community_code.getConf_value();
		    
		    //language_code
		    Configuration openxg_language_code = Configurationmanagement.getInstance().getConfKey(3L, "openxg.language.code");
			if (openxg_language_code == null) {
				throw new Exception("openxg.language.code missing in Configuration table");
			}
		    String language_code = openxg_language_code.getConf_value();
		    
		    //adminid
		    Configuration openxg_adminid = Configurationmanagement.getInstance().getConfKey(3L, "openxg.adminid");
			if (openxg_language_code == null) {
				throw new Exception("openxg.adminid missing in Configuration table");
			}
		    String adminid = openxg_adminid.getConf_value();
		    
		    
		    //Calculate the number in national format
		    Configuration sip_phonerange_start = Configurationmanagement.getInstance().getConfKey(3L, "sip.phonerange.start");
		    Configuration sip_phonerange = Configurationmanagement.getInstance().getConfKey(3L, "sip.phonerange");
		    Configuration sip_phonerange_currentindex = Configurationmanagement.getInstance().getConfKey(3L, "sip.phonerange.currentindex");
		    if (sip_phonerange_start == null || sip_phonerange == null || sip_phonerange_currentindex == null) {
		    	throw new Exception("sip.phonerange.start, sip.phonerange or sip.phonerange.currentindex missing in Configuration table");
		    }
		    
		    Long sipPhoneRangeStart = Long.parseLong(sip_phonerange_start.getConf_value());
		    Long sipPhoneRange = Long.parseLong(sip_phonerange.getConf_value());
		    Long sipPhoneRangeCurrentIndex = Long.parseLong(sip_phonerange_currentindex.getConf_value());
		    
		    if (sipPhoneRangeCurrentIndex >= sipPhoneRange) {
		    	throw new Exception("You have no more numbers, you need to allocate more numbers and alter the Configuration value sip.phonerange");
		    }
		    
		    Long useridAsNumber = sipPhoneRangeStart + sipPhoneRange + sipPhoneRangeCurrentIndex;
		    
		    log.debug("(sip_phonerange_start.getConf_value().length()+1) "+(sip_phonerange_start.getConf_value().length()+1));
		    
		    String userid = String.format("%0"+(sip_phonerange_start.getConf_value().length()+1)+"d", useridAsNumber);
		    
		    sipPhoneRangeCurrentIndex++;
		    sip_phonerange_currentindex.setConf_value(""+sipPhoneRangeCurrentIndex);
		    
		    Configurationmanagement.getInstance().updateConfig(sip_phonerange_currentindex);
		    
		    String digest = this.digest_calculate(new Object[]{client_id, userid, domain,
						 first_name, middle_i, last_name, password, community_code,
						 language_code, email, adminid, client_secret});
			
			this.openSIPgUserCreate(client_id, digest, userid, domain, first_name, 
					middle_i, last_name, password, community_code, language_code,
					email, adminid);
		
		} catch (Exception err) {

			log.error("[openSIPgUserCreateTest]",err);
			
		}
	}
	
	
	
	public void openSIPgUserCreate(String client_id, String digest, String userid, String domain, 
			String first_name, String middle_i, String last_name, String password, String community_code, 
			String language_code, String email, String adminid) {
		try {
			
			String strURL = "https://85.134.48.179:443/manager/xml_rpc_server.php";
			
			// Prepare HTTP post
	        PostMethod post = new PostMethod(strURL);
	        post.addRequestHeader("User-Agent", "OpenSIPg XML_RPC Client");

	        //Get the XML-String representative
	        String stringToPost = OpenXGCustomXMLMarshall.getInstance().
		        							openSIPgUserCreate(client_id, digest, userid, 
		        									domain, first_name, middle_i, last_name, password, 
		        									community_code, language_code, email, adminid);
	        
	        //log.debug(stringToPost);
	        
	        RequestEntity entity = new ByteArrayRequestEntity(stringToPost.getBytes(Charset.forName("ISO-8859-1")));
	        
//	        // Prepare HTTP post
	        
	        post.getParams().setContentCharset("ISO-8859-1");
	        post.getParams().setVersion(HttpVersion.HTTP_1_0);
	        
	        // Request content will be retrieved directly
	        // from the input stream
	        post.setRequestEntity(entity);
	        
	        Protocol easyhttps = new Protocol("https", (ProtocolSocketFactory)new EasySSLProtocolSocketFactory(), 443);
	        Protocol.registerProtocol("https", easyhttps);

	        
	        // Get HTTP client
	        HttpClient httpclient = new HttpClient();
	        
	        // Execute request
            int result = httpclient.executeMethod(post);
            // Display status code
            log.debug("Response status code: " + result);
            // Display response
            log.debug("Response body: ");
            log.debug(post.getResponseBodyAsString());
			
		} catch (Exception err) {
			
			log.error("[openSIPgUserCreate]",err);
			
		}
	}


}
