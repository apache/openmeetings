// Licensed under the Apache License, Version 2.0 (the \"License\") http://www.apache.org/licenses/LICENSE-2.0
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = {
	module: {
		rules: [{
			test: /\.png$/i,
			use: [
				'ignore-loader',
			],
		},{
			test: /\.s[ac]ss$/i,
			use: [
				MiniCssExtractPlugin.loader,
				{
					loader: 'css-loader',
					options: {
						url: false,
					},
				},
				"sass-loader",
			],
		}],
	},
	entry: {
		_theme: {
			import: './src/index.js'
		}
	},
	output: {
		path: `${process.env.outDir}/css/`,
	},
};
