package org.openmeetings.app.conference.videobeans;

public class UserSecurity {
	  
	public String username;
	public String userpass;
	public String userrole;
	
	public UserSecurity(){ }
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the userpass
	 */
	public String getUserpass() {
		return userpass;
	}
	/**
	 * @param userpass the userpass to set
	 */
	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}
	/**
	 * @return the userrole
	 */
	public String getUserrole() {
		return userrole;
	}
	/**
	 * @param userrole the userrole to set
	 */
	public void setUserrole(String userrole) {
		this.userrole = userrole;
	}
	
	
}
