/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
const WbToolBase = require('./wb-tool-base');

module.exports = class ShapeBase extends WbToolBase {
	constructor() {
		super();
		Object.assign(this, {
			fill: {
				enabled: true
				, color: '#FFFF33'
			}
			, stroke: {
				enabled: true
				, color: '#FF6600'
				, width: 5
			}
			, opacity: 1
		});
	}
};
