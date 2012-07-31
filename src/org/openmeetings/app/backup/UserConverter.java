package org.openmeetings.app.backup;

import java.util.Map;

import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.persistence.beans.user.Users;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class UserConverter extends OmConverter<Users> {
	private Usermanagement userManagement;
	private Map<Long, Long> idMap;
	
	public UserConverter() {
		//default constructor is for export
	}
	
	public UserConverter(Usermanagement userManagement, Map<Long, Long> idMap) {
		this.userManagement = userManagement;
		this.idMap = idMap;
	}
	
	public Users read(InputNode node) throws Exception {
		long oldOrgId = getlongValue(node);
		long newId = idMap.containsKey(oldOrgId) ? idMap.get(oldOrgId) : oldOrgId;
		
		return userManagement.getUserById(newId);
	}

	public void write(OutputNode node, Users value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getUser_id());
	}
}