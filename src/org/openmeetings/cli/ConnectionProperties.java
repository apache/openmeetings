package org.openmeetings.cli;

public class ConnectionProperties {

	private String driverName = "org.apache.derby.jdbc.ClientDriver";
	private String connectionURL = "jdbc:derby:openmeetings";
	private String connectionLogin = "user";
	private String connectionPass = "secret";

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}

	public String getConnectionLogin() {
		return connectionLogin;
	}

	public void setConnectionLogin(String connectionLogin) {
		this.connectionLogin = connectionLogin;
	}

	public String getConnectionPass() {
		return connectionPass;
	}

	public void setConnectionPass(String connectionPass) {
		this.connectionPass = connectionPass;
	}
}
