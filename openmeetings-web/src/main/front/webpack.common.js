// Licensed under the Apache License, Version 2.0 (the \"License\") http://www.apache.org/licenses/LICENSE-2.0
const CircularDependencyPlugin = require('circular-dependency-plugin')

module.exports = {
	entry: {
		main: {
			import: './src/main/index.js'
		},
		chat: {
			import: './src/chat/index.js'
		},
		settings: {
			import: './src/settings/index.js'
		},
		room: {
			import: './src/room/index.js'
		},
		wb: {
			import: './src/wb/index.js'
		},
	},
	externals: {
		'../main/omutils': 'OmUtil',
		'../main/settings': 'Settings',
		'../chat/chat': 'Chat',
		'../settings/video-util': 'VideoUtil',
		'../settings/mic-level': 'MicLevel',
		'../settings/WebRtcPeer': 'WebRtcPeer',
		'../settings/settings': 'VideoSettings',
		'../wb/interview-area': 'InterviewWbArea',
		'../wb/wb-area': 'DrawWbArea',
	},
	plugins: [
		new CircularDependencyPlugin({
			exclude: /node_modules/,
			failOnError: true,
		}),
	],
	output: {
		path: `${process.env.outDir}/js/`,
	},
};
