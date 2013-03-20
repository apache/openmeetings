package org.apache.openmeetings.web.components.user.calendar;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.data.calendar.daos.AppointmentReminderTypDao;
import org.apache.openmeetings.data.conference.RoomManager;
import org.apache.openmeetings.data.conference.dao.RoomDao;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.calendar.AppointmentReminderTyps;
import org.apache.openmeetings.persistence.beans.domain.Organisation_Users;
import org.apache.openmeetings.persistence.beans.room.Room;
import org.apache.openmeetings.persistence.beans.room.RoomType;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractFormDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class AppointmentDialog extends AbstractFormDialog<Appointment> {
	private static final long serialVersionUID = 7553035786264113827L;
	private AppointmentForm form;
	
	public AppointmentDialog(String id, String title, IModel<Appointment> model) {
		super(id, title, model, true);
		form = new AppointmentForm("appForm", new CompoundPropertyModel<Appointment>(this.getModel()));
		add(form);
	}

	@Override
	protected DialogButton getSubmitButton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Form<?> getForm() {
		return this.form;
	}

	@Override
	protected void onOpen(AjaxRequestTarget target) {
		target.add(this.form);
	}
	
	@Override
	protected void onError(AjaxRequestTarget target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onSubmit(AjaxRequestTarget target) {
		// TODO Auto-generated method stub
		
	}
	
	private class AppointmentForm extends Form<Appointment> {
		private static final long serialVersionUID = -1764738237821487526L;
		private boolean createRoom = true;

		public AppointmentForm(String id, IModel<Appointment> model) {
			super(id, model);
			setOutputMarkupId(true);
			
			add(new RequiredTextField<String>("appointmentName"));
			add(new TextArea<String>("appointmentDescription"));
			add(new TextField<String>("appointmentLocation"));
			add(new DateTimeField("appointmentStarttime"));
			add(new DateTimeField("appointmentEndtime"));
			final PasswordTextField pwd = new PasswordTextField("password");
			pwd.setEnabled(isPwdProtected());
			pwd.setOutputMarkupId(true);
			add(pwd);
			
			add(new DropDownChoice<AppointmentReminderTyps>(
					"remind"
					, Application.getBean(AppointmentReminderTypDao.class).getAppointmentReminderTypList()
					, new ChoiceRenderer<AppointmentReminderTyps>("name", "typId")));
			
			final DropDownChoice<RoomType> roomType = new DropDownChoice<RoomType>(
					"room.roomtype"
					, Application.getBean(RoomManager.class).getAllRoomTypes()
					, new ChoiceRenderer<RoomType>("name", "roomtypes_id"));
			roomType.setEnabled(createRoom);
			roomType.setOutputMarkupId(true);
			add(roomType);
			
			final DropDownChoice<Room> room = new DropDownChoice<Room>(
					"room"
					, getRoomList()
					, new ChoiceRenderer<Room>("name", "rooms_id"));
			room.setEnabled(!createRoom);
			room.setOutputMarkupId(true);
			add(room);
			add(new AjaxCheckBox("createRoom", new PropertyModel<Boolean>(this, "createRoom")) {
				private static final long serialVersionUID = -3743113990890386035L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					createRoom = getConvertedInput();
					target.add(roomType.setEnabled(createRoom), room.setEnabled(!createRoom));
				}
			});
			add(new AjaxCheckBox("isPasswordProtected") {
				private static final long serialVersionUID = 6041200584296439976L;

				@Override
				protected void onUpdate(AjaxRequestTarget target) {
					AppointmentForm.this.getModelObject().setIsPasswordProtected(getConvertedInput());
					pwd.setEnabled(isPwdProtected());
					target.add(pwd);
				}
			});
		}
		
		private boolean isPwdProtected() {
			return Boolean.TRUE.equals(getModelObject().getIsPasswordProtected());
		}
		
		private List<Room> getRoomList() {
			//FIXME need to be reviewed
			List<Room> result = new ArrayList<Room>();
			RoomDao dao = Application.getBean(RoomDao.class);
			result.addAll(dao.getPublicRooms());
			for (Organisation_Users ou : Application.getBean(UsersDao.class).get(WebSession.getUserId()).getOrganisation_users()) {
				result.addAll(dao.getOrganisationRooms(ou.getOrganisation().getOrganisation_id()));
			}
			if (getModelObject().getRoom() != null && getModelObject().getRoom().getAppointment()) { //FIXME review
				result.add(getModelObject().getRoom());
			}
			return result;
		}
	}
}
