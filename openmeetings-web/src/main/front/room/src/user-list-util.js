/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */
let options;

function _hasRight(_inRights, _ref) {
	const ref = _ref || options.rights;
	let _rights;
	if (Array.isArray(_inRights)) {
		_rights = _inRights;
	} else {
		if ('SUPER_MODERATOR' === _inRights) {
			return ref.includes(_inRights);
		}
		_rights = [_inRights];
	}
	const rights = ['SUPER_MODERATOR', 'MODERATOR', ..._rights];
	for (let i = 0; i < rights.length; ++i) {
		if (ref.includes(rights[i])) {
			return true;
		}
	}
	return false;
}

module.exports = {
	init: function(opts) {
		options = opts;
	}
	, hasRight: _hasRight
};
