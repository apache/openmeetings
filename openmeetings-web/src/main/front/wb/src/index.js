/* Licensed under the Apache License, Version 2.0 (the "License") http://www.apache.org/licenses/LICENSE-2.0 */

// HACK!!!!! to avoid eval to determine MathJax version
const fs = require('fs')
	, pkg_src = fs.readFileSync(__dirname + '/../node_modules/mathjax-full/package.json', 'utf8');

window.PACKAGE_VERSION = JSON.parse(pkg_src).version;
// END HACK

Object.assign(window, {
	InterviewWbArea: require('./interview-area')
	, DrawWbArea: require('./wb-area')
});
