/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var ULine = function(wb, s, sBtn) {
	const uline = Line(wb, s, sBtn);
	uline.stroke.width = 20;
	uline.opacity = .5;
	return uline;
};
