/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var ULine = function(wb, s) {
	const uline = Line(wb, s);
	uline.stroke.width = 20;
	uline.opacity = .5;
	return uline;
};
