/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
var ShapeBase = function() {
	const base = Base();
	base.fill = {enabled: true, color: '#FFFF33'};
	base.stroke = {enabled: true, color: '#FF6600', width: 5};
	base.opacity = 1;
	return base;
};
