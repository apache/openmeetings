package org.openmeetings.cli;

public class DerbyPatcher extends ConnectionPropertiesPatcher {
	@Override
	protected void patchDb(String[] tokens, String host, String _port, String _db) {
		String db = (_db == null) ? "openmeetings" : _db;
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith("Url")) {
				String suffix = prop.substring(prop.indexOf(';'));
				String connectionURL = "jdbc:derby" + ":" + db; 
				connectionProperties.setConnectionURL(connectionURL);
				String url = "Url=" + connectionURL + suffix;
				tokens[i] = url;
			}
		}
	}
}
