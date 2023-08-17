// Licensed under the Apache License, Version 2.0 (the \"License\") http://www.apache.org/licenses/LICENSE-2.0

const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
	mode: 'production',
	output: {
		filename: '[name].min.js',
	},
});
