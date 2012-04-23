package org.openmeetings.cli;

public class PostgresPatcher extends ConnectionPropertiesPatcher {
	@Override
	protected void patchDb(String[] tokens, String host, String _port, String _db) {
		String port = (_port == null) ? "5432" : _port;
		String db = (_db == null) ? "openmeetings" : _db;
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith("Url")) {
				String connectionURL = "jdbc:postgresql://" + host + ":" + port + "/" + db; 
				connectionProperties.setConnectionURL(connectionURL);
				String url = "Url=" + connectionURL;
				tokens[i] = url;
			}
		}
	}
}
