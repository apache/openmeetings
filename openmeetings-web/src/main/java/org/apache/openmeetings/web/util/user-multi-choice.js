/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
function formatOmUser(user) {
	return "<span class='user " + (user.contact ? "external" : "internal") + "' >" + user.text + "</span>";
}
function escapeOmUserMarkup(m) {
	return m;
}