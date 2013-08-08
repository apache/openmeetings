package org.apache.openmeetings.web.admin.oauth;

import org.apache.openmeetings.data.oauth.OAuth2Dao;
import org.apache.openmeetings.persistence.beans.user.oauth.OAuthServer;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

public class OAuthPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private OAuthForm form;

	@SuppressWarnings("unchecked")
	public OAuthPanel(String id) {
		super(id);
		SearchableDataView<OAuthServer> dataView = new SearchableDataView<OAuthServer>("oauthServersList",
				new SearchableDataProvider<OAuthServer>(OAuth2Dao.class)) {

			private static final long serialVersionUID = -7701640237947383416L;

			@Override
			protected void populateItem(Item<OAuthServer> item) {
				final OAuthServer server = item.getModelObject();
				item.add(new Label("oauthServerId", String.valueOf(server.getId())));
				item.add(new Label("name", server.getName()));
				item.add(new AjaxEventBehavior("onclick") {

					private static final long serialVersionUID = 7380727643346943181L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(server);
						form.hideNewRecord();
						target.add(form);
						target.appendJavaScript("oauthPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
			
		};
		
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = -1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<OAuthServer> container = new DataViewContainer<OAuthServer>(listContainer, dataView, navigator);
		container.setLinks(new OmOrderByBorder<OAuthServer>("orderById", "oauthServerId", container),
						   new OmOrderByBorder<OAuthServer>("orderByName", "name", container));
		add(container.orderLinks);
		add(navigator);
		
		form = new OAuthForm("form", listContainer, new OAuthServer());
		form.showNewRecord();
        add(form);
	}

}
