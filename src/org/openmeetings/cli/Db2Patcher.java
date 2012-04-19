package org.openmeetings.cli;

public class Db2Patcher extends ConnectionPropertiesPatcher {
	@Override
	protected void patchDb(String[] tokens, String host, String _port, String _db) {
		String port = (_port == null) ? "50000" : _port;
		String db = (_db == null) ? "openmeet" : _db;
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith("Url")) {
				String url = "Url=jdbc:db2://" + host + ":" + port + "/" + db;
				tokens[i] = url;
			}
		}
	}
}
