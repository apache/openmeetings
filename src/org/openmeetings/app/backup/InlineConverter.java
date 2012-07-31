package org.openmeetings.app.backup;

import org.simpleframework.xml.stream.InputNode;

public abstract class InlineConverter<T> extends OmConverter<T> {
	
	String getNextValue(InputNode parent, String name) throws Exception {
		InputNode node = parent.getNext(name);
		return node != null ? node.getValue() : null;
	}
}
