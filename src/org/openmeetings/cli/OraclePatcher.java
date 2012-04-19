package org.openmeetings.cli;

public class OraclePatcher extends ConnectionPropertiesPatcher {
	@Override
	protected void patchDb(String[] tokens, String host, String _port, String _db) {
		String port = (_port == null) ? "1521" : _port;
		String db = (_db == null) ? "openmeetings" : _db;
		for (int i = 0; i < tokens.length; ++i) {
			String prop = tokens[i].trim();
			if (prop.startsWith("Url")) {
				String url = "Url=jdbc:oracle:thin:@" + host + ":" + port + ":" + db;
				tokens[i] = url;
			}
		}
	}
}
