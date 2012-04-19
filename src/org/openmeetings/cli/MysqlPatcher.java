package org.openmeetings.cli;

public class MysqlPatcher extends ConnectionPropertiesPatcher {
	@Override
	protected void patchDb(String[] tokens, String host, String _port, String _db) {
		String port = (_port == null) ? "3306" : _port;
		String db = (_db == null) ? "openmeetings" : _db;
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith("Url")) {
				String suffix = prop.substring(prop.indexOf('?'));
				String url = "Url=jdbc:mysql://" + host + ":" + port + "/" + db + suffix;
				tokens[i] = url;
			}
		}
	}
}
