package org.openmeetings.app.backup;

import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.persistence.beans.adresses.States;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class StateConverter extends OmConverter<States> {
	private Statemanagement statemanagement;
	
	public StateConverter() {
		//default constructor is for export
	}
	
	public StateConverter(Statemanagement statemanagement) {
		this.statemanagement = statemanagement;
	}
	
	public States read(InputNode node) throws Exception {
		return statemanagement.getStateById(getlongValue(node));
	}

	public void write(OutputNode node, States value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getState_id());
	}
}