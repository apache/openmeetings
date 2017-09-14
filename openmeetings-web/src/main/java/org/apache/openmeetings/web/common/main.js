/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
Wicket.BrowserInfo.collectExtraInfo = function(info) {
	var l = window.location;
	info.codebase = l.origin + l.pathname;
	info.settings = JSON.stringify(getSettings());
};
function getSettings() {
	var s = localStorage.getItem("openmeetings");
	if (!!s) {
		s = JSON.parse(s);
	}
	return s || {};
}
function saveSetting(name, val) {
	var s = getSettings();
	s[name] = val;
	localStorage.setItem("openmeetings", JSON.stringify(s));
}
