package org.openmeetings.app.backup;

import org.openmeetings.app.data.conference.PollManagement;
import org.openmeetings.app.persistence.beans.poll.PollType;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class PollTypeConverter extends OmConverter<PollType> {
	private PollManagement pollManagement;
	
	public PollTypeConverter() {
		//default constructor is for export
	}
	
	public PollTypeConverter(PollManagement pollManagement) {
		this.pollManagement = pollManagement;
	}
	
	public PollType read(InputNode node) throws Exception {
		return pollManagement.getPollType(getlongValue(node));
	}

	public void write(OutputNode node, PollType value) throws Exception {
		node.setData(true);
		node.setValue(value == null ? "0" : "" + value.getPollTypesId());
	}
}