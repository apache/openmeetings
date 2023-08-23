// Licensed under the Apache License, Version 2.0 (the \"License\") http://www.apache.org/licenses/LICENSE-2.0

const { merge } = require('webpack-merge');
const common = require('./webpack-css.common.js');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = merge(common, {
	mode: 'development',
	devtool: 'inline-source-map',
	plugins: [
		new MiniCssExtractPlugin({
			// Options similar to the same options in webpackOptions.output
			// both options are optional
			filename: 'theme.css',
			chunkFilename: '[id].css',
		}),
	],
	output: {
		filename: '[name].js',
	},
});
