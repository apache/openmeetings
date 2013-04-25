package org.apache.openmeetings.web.components.admin;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.ConfirmCallListener;
import org.apache.openmeetings.web.components.FormSaveRefreshPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

public abstract class AdminSavePanel<T> extends FormSaveRefreshPanel<T> {
	private static final long serialVersionUID = -8916631148087019924L;
	private Label newRecord;
	
	public AdminSavePanel(String id, final Form<T> form) {
		super(id, form);
		
		newRecord = new Label("newRecord", Model.of(WebSession.getString(344L)));
		add(newRecord.setVisible(false).setOutputMarkupId(true));
		
		// add a new button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-new-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				newRecord.setVisible(true);
				target.add(newRecord);
				onNewSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				onNewError(target, form);
			}
		});

		// add a cancel button that can be used to submit the form via ajax
		add(new AjaxButton("ajax-cancel-button", form) {
			private static final long serialVersionUID = 839803820502260006L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmCallListener(833L));
			}
			
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so that it is hidden
				target.add(feedback);
				hideNewRecord();
				onDeleteSubmit(target, form);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// repaint the feedback panel so errors are shown
				target.add(feedback);
				hideNewRecord();
				onDeleteError(target, form);
			}
		});
	}
	
	/**
	 * Hide the new record text
	 */
	public void hideNewRecord() {
		newRecord.setVisible(false);
	}
	
	/**
	 * Hide the new record text
	 */
	public void showNewRecord() {
		newRecord.setVisible(true);
	}

	protected abstract void onNewSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onNewError(AjaxRequestTarget target, Form<?> form);

	protected abstract void onDeleteSubmit(AjaxRequestTarget target, Form<?> form);
	protected abstract void onDeleteError(AjaxRequestTarget target, Form<?> form);
}
