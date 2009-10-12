package org.openmeetings.test.init;

import junit.framework.TestCase;

import org.openmeetings.app.data.basic.Fieldmanagment;
import org.openmeetings.app.data.basic.Navimanagement;

public class AddDefaultfield extends TestCase {
	
	public AddDefaultfield (String testname){
		super(testname);
	}
	
	public void testaddDefaultField(){
		
//		Fieldmanagment.getInstance().addFourFieldValues("organisationtablelist_idrow", 164, "Organisations-ID", "Organisation-ID", "Organisation-ID", "Organisation-ID");
//		Fieldmanagment.getInstance().addFourFieldValues("organisationtablelist_namerow", 165, "Name", "name", "name", "name");
//		Fieldmanagment.getInstance().addFourFieldValues("uservalue_levelid1", 166, "Benutzer", "user", "user", "user");
//		Fieldmanagment.getInstance().addFourFieldValues("uservalue_levelid2", 167, "Moderator", "mod", "mod", "mod");
//		Fieldmanagment.getInstance().addFourFieldValues("uservalue_levelid3", 168, "Admin", "admin", "admin", "admin");
//		Fieldmanagment.getInstance().addFourFieldValues("uservalue_levellabel", 169, "Benuterrolle", "userlevel", "userlevel", "userlevel");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_header", 170, "Organisation", "organisation", "organisation", "organisation");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_orgname", 171, "Name", "name", "name", "name");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_orgname", 172, "Organisation hinzufügen", "add organisation", "add organisation", "add organisation");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_orgname", 173, "Organisation hinzufügen", "add organisation", "add organisation", "add organisation");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwin", 174, "abbrechen", "cancel", "cancel", "cancel");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwin", 175, "hinzufügen", "add", "add", "add");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwin", 176, "Organisation entfernen", "remove organisation", "remove organisation", "remove organisation");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userlist", 177, "Benutzer", "user", "user", "user");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userlistadd", 178, "Benutzer hinzufügen", "add user", "add user", "add user");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userlistdelete", 179, "Benutzer entfernen", "delete user", "delete user", "delete user");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwinheader", 180, "Benutzer hinzufügen", "add user", "add user", "add user");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwinsearchfield", 181, "Benutzer suchen", "search user", "search user", "search user");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwinsearchbtn", 182, "suchen", "search", "search", "search");
//		Fieldmanagment.getInstance().addFourFieldValues("orgvalue_userwinsearchresult", 183, "Benutzer", "user", "user", "user");
//		Fieldmanagment.getInstance().addFourFieldValues("loginwin_chooseorganisation", 184, "Organisation", "organisation", "organisation", "organisation");
//		Fieldmanagment.getInstance().addFourFieldValues("loginwin_chooseorganisationbtn", 185, "auswählen", "enter", "enter", "enter");
//		Fieldmanagment.getInstance().addFourFieldValues("navi_roomadmin", 186, "Konferenzräume", "conferencerooms", "conferencerooms", "conferencerooms");
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 187, "Konferenzräume", "Conferencerooms", "Conferencerooms", "Conferencerooms");
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 188, "ID", "id", "id", "id");
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 189, "Name", "Name", "Name", "Name");
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 190, "öffentlich", "public", "public", "public");
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 191, "Organisationen", "organisations", "organisations", "organisations");
		
//		Fieldmanagment.getInstance().addFourFieldValues("roomadmin_header", 192, "Konferenzräume", "Conferencerooms", "Conferencerooms", "Conferencerooms");
//		Fieldmanagment.getInstance().addFourFieldValues("roomvalue_name", 193, "Name", "name", "name", "name");
//		Fieldmanagment.getInstance().addFourFieldValues("roomvalue_type", 194, "Typ", "type", "type", "type");
//		Fieldmanagment.getInstance().addFourFieldValues("roomvalue_ispublic", 195, "öffentlich", "public", "public", "public");
//		Fieldmanagment.getInstance().addFourFieldValues("roomvalue_comment", 196, "Kommentar", "comment", "comment", "comment");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveicon", 197, "Speichern", "save", "save", "save");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_openicon", 198, "Öffnen", "load", "load", "load");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveaswinheader", 199, "Speichern unter", "save as", "save as", "save as");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveaswintext", 200, "Dateiname", "filename", "filename", "filename");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveaswintext", 201, "Dateiname", "filename", "filename", "filename");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveaswinbtn1", 202, "Abbrechen", "cancel", "cancel", "cancel");
//		Fieldmanagment.getInstance().addFourFieldValues("whiteboard_saveaswinbtn2", 203, "Speichern", "save", "save", "save");
//		Fieldmanagment.getInstance().addFourFieldValues("rpcerrorwin_header", 204, "Fehler", "error", "error", "error");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_header", 205, "Laden", "loading", "loading", "loading");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_messsload", 206, "Objekte geladen", "objects loaded", "objects loaded", "objects loaded");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_messsync", 207, "Synchronisiere Clients. Restliche Clients: ", "snychronizing clients, clients to wait:", "snychronizing clients, clients to wait:", "snychronizing clients, clients to wait:");
//		Fieldmanagment.getInstance().addFourFieldValues("loadimage_messload", 208, "Lade Bilddaten", "Loading Imagedata", "Loading Imagedata", "Loading Imagedata");
//		Fieldmanagment.getInstance().addFourFieldValues("loadimage_messsync", 209, "Synchronisiere Clients. Restliche Clients: ", "snychronizing clients, clients to wait:", "snychronizing clients, clients to wait:", "snychronizing clients, clients to wait:");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_confirmheader", 210, "Zeichenbrett leeren", "clear drawarea", "clear drawarea", "clear drawarea");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_confirmmess", 211, "Zeichnbrett leeren, alle bisherigen Änderungen gehen damit verloren!", "clear drawarea, all data on whiteboard will be lost", "clear drawarea, all data on whiteboard will be lost", "clear drawarea, all data on whiteboard will be lost");
//		Fieldmanagment.getInstance().addFourFieldValues("loadwml_confirmmess2", 212, "Bestätigung anfordern vor dem Laden einer Datei", "Confirm before loading a file", "Confirm before loading a file", "Confirm before loading a file");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitation_btn", 213, "Einladung versenden", "Send invitation", "Send invitation", "Send invitation");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_header", 214, "Einladung versenden", "Send invitation", "Send invitation", "Send invitation");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_subject", 215, "Betreff", "Subject", "Subject", "Subject");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_recipient", 216, "Empfänger", "Recipient", "Recipient", "Recipient");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_message", 217, "Nachricht", "Message", "Message", "Message");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_btn_confirm", 218, "abschicken", "Send", "Send", "Send");
//		Fieldmanagment.getInstance().addFourFieldValues("send_invitationwin_btn_cancel", 219, "abbrechen", "cancel", "cancel", "cancel");
//		Fieldmanagment.getInstance().addFourFieldValues("send_chat_text_btn", 220, "senden", "send", "appeler", "emitir");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_header", 221, "Benutzerdaten", "Userdata", "userdata", "userdata");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_subject", 222, "Nickname für diese Konference", "Your nickname for this conference", "Your nickname for this conference", "Your nickname for this conference");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_login", 223, "Spitzname/Alias", "nick", "nick", "nick");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_firstname", 224, "Vorname", "firstname", "firstname", "firstname");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_lastname", 225, "Nachname", "lastname", "lastname", "lastname");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_mail", 226, "EMail", "email", "email", "email");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_lang", 227, "Sprache", "language", "language", "language");
//		Fieldmanagment.getInstance().addFourFieldValues("invited_userwin_enter", 228, "Absenden", "enter", "enter", "enter");
		
	}

}
