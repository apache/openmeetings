package org.openmeetings.app.sip.xmlrpc;

import java.net.URL;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.openmeetings.app.remote.red5.ClientListManager;
import org.openmeetings.utils.crypt.MD5;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;


public class OpenXGClient {
	
	private static final Logger log = Red5LoggerFactory.getLogger(OpenXGHttpClient.class, "openmeetings");

	private static OpenXGClient instance = null;

	public static synchronized OpenXGClient getInstance() {
		if (instance == null) {
			instance = new OpenXGClient();
		}
		return instance;
	}
	
	public void testConnection() {
		try {
			
			log.debug("Test Connection");
			
//		    // Create a trust manager that does not validate certificate chains
//		    TrustManager[] trustAllCerts = new TrustManager[] {
//		        new X509TrustManager() {
//		            public X509Certificate[] getAcceptedIssuers() {
//		                return null;
//		            }
//		 
//		            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//		                // Trust always
//		            }
//		 
//		            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//		                // Trust always
//		            }
//		        }
//		    };
//		 
//		    // Install the all-trusting trust manager
//		    SSLContext sc = SSLContext.getInstance("SSL");
//		    // Create empty HostnameVerifier
//		    HostnameVerifier hv = new HostnameVerifier() {
//		                public boolean verify(String arg0, SSLSession arg1) {
//		                        return true;
//		                }
//		    };
//
//		    sc.init(null, trustAllCerts, new java.security.SecureRandom());
//		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//		    HttpsURLConnection.setDefaultHostnameVerifier(hv);
//			
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//			
//			log.debug("config User Agent "+config.getUserAgent());
			
			config.setUserAgent("OpenSIPg XML_RPC Client");
			
		    config.setServerURL(new URL("http://****/manager/xml_rpc_server.php"));
		    XmlRpcClient client = new XmlRpcClient();
		    
		    client.setConfig(config);
		    
		    client.setXmlWriterFactory(new OpenXGXmlWriterFactory());
		    
		    String client_id = "user_admin";
		    String client_secret = "*******";
		    
		    
		    String userid = "067201101";
		    String domain = "voipt3.multi.fi";
		    String first_name = "Matti";
		    String middle_i = "X";
		    String last_name = "Virtanen";
		    String password = "password";
		    String community_code = "999";
		    String language_code = "fi";
		    String email = "matti@fun.com";
		    String adminid = "matti";
		    
		    String digest = this.digest_calculate(new Object[]{client_id, userid, domain,
					 first_name, middle_i, last_name,
					 password, community_code,
					 language_code, email, adminid,
					 client_secret});
		    
//		    $digest = digest_calculate($client_id, $userid, $domain,
//			$adminid, $client_secret);
//	  		$params = array(client_id=>$client_id, digest=>$digest, userid=>$userid,
//			domain=>$domain, adminid=>$adminid);
		    
		    
		    log.debug(digest);
		    
		    //function user_info($userid, $domain, $adminid, &$error)
		    Object[] params = new Object[]{
		    		client_id, digest, userid,
					  domain, first_name,
					  middle_i, last_name,
					  password, community_code,
					  language_code, email,
					  adminid};
		    
		    Object[] struct = new Object[]{params};
		    
		    Object result = client.execute("OpenSIPg.UserCreate", struct);
		    
		    if (result != null) {
		    	log.debug(result.getClass().getName());
		    	
		    	if (result instanceof Map) {
		    		
		    		Map mapResults = (Map) result;
		    		
		    		for (Iterator iter = mapResults.keySet().iterator();iter.hasNext();) {
		    			
		    			Object key = iter.next();
		    			log.debug("-- key "+key+" value "+mapResults.get(key));
		    			
		    		}
		    		
		    	}
		    }
		    
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
