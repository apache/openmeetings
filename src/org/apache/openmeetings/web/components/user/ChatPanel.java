package org.apache.openmeetings.web.components.user;

import org.apache.openmeetings.web.components.UserPanel;

public class ChatPanel extends UserPanel {
	private static final long serialVersionUID = -9144707674886211557L;

	public ChatPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		setMarkupId(id);
	}

}
