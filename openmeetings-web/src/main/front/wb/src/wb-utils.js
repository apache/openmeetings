/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */

module.exports = {
	safeRemove: (e) => {
		if (typeof(e) === 'object') {
			e.remove();
		}
	}
};
