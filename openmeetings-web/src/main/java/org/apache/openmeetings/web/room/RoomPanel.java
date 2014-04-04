package org.apache.openmeetings.web.room;

import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;

	public RoomPanel(String id, long roomId) {
		super(id);
	}

	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(false), getMainPage().getMenu().setVisible(false)
				, getMainPage().getTopLinks().setVisible(false));
	}
	
	@Override
	public void cleanup(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(true), getMainPage().getMenu().setVisible(true)
				, getMainPage().getTopLinks().setVisible(true));
	}
}
