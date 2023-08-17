/*
 * Based on
 *
 * jQuery CSSEmoticons plugin 0.2.9
 *
 * Copyright (c) 2010 Steve Schwartz (JangoSteve)
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Date: Sun Oct 22 1:00:00 2010 -0500
 */
const escapeCharacters = [")", "(", "*", "[", "]", "{", "}", "|", "^", "<", ">", "\\", "?", "+", "=", "."]
	, specialRegex = new RegExp('(\\' + escapeCharacters.join('|\\') + ')', 'g')
	// One of these characters must be present before the matched emoticon, or the matched emoticon must be the first character in the container HTML
	//  This is to ensure that the characters in the middle of HTML properties or URLs are not matched as emoticons
	//  Below matches ^ (first character in container HTML), \s (whitespace like space or tab), or \0 (NULL character)
	// (<\\S+.*>) matches <\\S+.*> (matches an HTML tag like <span> or <div>), but haven't quite gotten it working yet, need to push this fix now
	, preMatch = '(^|[\\s\\0])'
	, emoticons = []
	, matchers = []
	, defaults = {animate: true, delay: 500, exclude: 'pre,code,.no-emoticons'};

function createMatcher(m) {
	const str = m.text.replace(specialRegex, '\\$1');
	m.regexp = new RegExp(preMatch + '(' + str + ')', 'g');
	return m;
}
function addMatchers(arr) {
	for (let i = 0; i < arr.length; ++i) {
		const o = arr[i]
		let m = typeof(o) === 'object' ? JSON.parse(JSON.stringify(o)) : {text: o, cssClass: ' '};
		emoticons.push(m.text);

		matchers.push(createMatcher(m));
		if (m.text.indexOf('=') > -1) {
			m = JSON.parse(JSON.stringify(m));
			m.text = m.text.replace(/=/g, '&#61;').replace(/[+]/g, '&#43;');
			matchers.push(createMatcher(m));
		}
		if (m.text.indexOf('\'') > -1) {
			m = JSON.parse(JSON.stringify(m));
			m.text = m.text.replace(/'/g, '&#39;');
			matchers.push(createMatcher(m));
		}
	}
}
addMatchers([
	":-)", ":o)", ":c)", ":^)", ":-D", ":-(", ":-9", ";-)", ":-P", ":-p", ":-Þ", ":-b", ":-O", ":-/", ":-X", ":-#", ":'(", "B-)", "8-)", ";*(", ":-*", ":-\\",
	"?-)" // <== This is my own invention, it's a smiling pirate (with an eye-patch)!
]);
addMatchers([ // separate these out so that we can add a letter-spacing between the characters for better proportions
	":)", ":]", "=]", "=)", "8)", ":}", ":D", ":(", ":[", ":{", "=(", ";)", ";]", ";D", ":P", ":p", "=P", "=p", ":b", ":Þ", ":O", ":/", "=/", ":S", ":#", ":X", "B)", ":|", ":\\", "=\\", ":*", ":&gt;", ":&lt;"
]);
addMatchers([ // emoticons to be treated with a special class, hash specifies the additional class to add, along with standard css-emoticon class
	{text: "&gt;:)", cssClass: "red-emoticon small-emoticon spaced-emoticon"},
	{text: "&gt;;)", cssClass: "red-emoticon small-emoticon spaced-emoticon"},
	{text: "&gt;:(", cssClass: "red-emoticon small-emoticon spaced-emoticon"},
	{text: "&gt;: )", cssClass: "red-emoticon small-emoticon"},
	{text: "&gt;; )", cssClass: "red-emoticon small-emoticon"},
	{text: "&gt;: (", cssClass: "red-emoticon small-emoticon"},
	{text: ";(", cssClass: "red-emoticon spaced-emoticon"},
	{text: "&lt;3", cssClass: "pink-emoticon counter-rotated"},
	{text: "O_O", cssClass: "no-rotate"},
	{text: "o_o", cssClass: "no-rotate"},
	{text: "0_o", cssClass: "no-rotate"},
	{text: "O_o", cssClass: "no-rotate"},
	{text: "T_T", cssClass: "no-rotate"},
	{text: "^_^", cssClass: "no-rotate"},
	{text: "O:)", cssClass: "small-emoticon spaced-emoticon"},
	{text: "O: )", cssClass: "small-emoticon"},
	{text: "8D", cssClass: "small-emoticon spaced-emoticon"},
	{text: "XD", cssClass: "small-emoticon spaced-emoticon"},
	{text: "xD", cssClass: "small-emoticon spaced-emoticon"},
	{text: "=D", cssClass: "small-emoticon spaced-emoticon"},
	{text: "8O", cssClass: "small-emoticon spaced-emoticon"},
	{text: "[+=..]", cssClass: "no-rotate nintendo-controller"}
]);

module.exports = {
	emoticons: emoticons
	, matchers: matchers
	, defaults: defaults
	, emoticonize: function(str, options) {
		const opts = $.extend({}, defaults, options);

		let cssClass = 'css-emoticon';
		if (opts.animate) {
			cssClass += ' un-transformed-emoticon animated-emoticon';
		}
		for (let i = 0; i < matchers.length; ++i) {
			const m = matchers[i];
			const css = cssClass + " " + m.cssClass;
			str = str.replace(m.regexp, "$1<span class='" + css + "'>$2</span>");
		}
		return str;
	}
	, animate: function(options) {
		const opts = $.extend({}, defaults, options);
		// animate emoticons
		if (opts.animate) {
			setTimeout(function () {
				$('.un-transformed-emoticon').removeClass('un-transformed-emoticon');
			}, opts.delay);
		}
	}
};
