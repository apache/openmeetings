/*
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
var CSSEmoticon = function() {
	this.escapeCharacters = [")", "(", "*", "[", "]", "{", "}", "|", "^", "<", ">", "\\", "?", "+", "=", "."];

	this.threeCharacterEmoticons = [
		":-)", ":o)", ":c)", ":^)", ":-D", ":-(", ":-9", ";-)", ":-P", ":-p", ":-Þ", ":-b", ":-O", ":-/", ":-X", ":-#", ":'(", "B-)", "8-)", ";*(", ":-*", ":-\\",
		"?-)" // <== This is my own invention, it's a smiling pirate (with an eye-patch)!
	];
	this.threeCharEmoticons = this.threeCharacterEmoticons.slice();
	this.twoCharacterEmoticons = [ // separate these out so that we can add a letter-spacing between the characters for better proportions
		":)", ":]", "=]", "=)", "8)", ":}", ":D", ":(", ":[", ":{", "=(", ";)", ";]", ";D", ":P", ":p", "=P", "=p", ":b", ":Þ", ":O", ":/", "=/", ":S", ":#", ":X", "B)", ":|", ":\\", "=\\", ":*", ":&gt;", ":&lt;"
	];
	this.twoCharEmoticons = this.twoCharacterEmoticons.slice();

	this.specialEmoticons = { // emoticons to be treated with a special class, hash specifies the additional class to add, along with standard css-emoticon class
		"&gt;:)": {cssClass: "red-emoticon small-emoticon spaced-emoticon"},
		"&gt;;)": {cssClass: "red-emoticon small-emoticon spaced-emoticon"},
		"&gt;:(": {cssClass: "red-emoticon small-emoticon spaced-emoticon"},
		"&gt;: )": {cssClass: "red-emoticon small-emoticon"},
		"&gt;; )": {cssClass: "red-emoticon small-emoticon"},
		"&gt;: (": {cssClass: "red-emoticon small-emoticon"},
		";(": {cssClass: "red-emoticon spaced-emoticon"},
		"&lt;3": {cssClass: "pink-emoticon counter-rotated"},
		"O_O": {cssClass: "no-rotate"},
		"o_o": {cssClass: "no-rotate"},
		"0_o": {cssClass: "no-rotate"},
		"O_o": {cssClass: "no-rotate"},
		"T_T": {cssClass: "no-rotate"},
		"^_^": {cssClass: "no-rotate"},
		"O:)": {cssClass: "small-emoticon spaced-emoticon"},
		"O: )": {cssClass: "small-emoticon"},
		"8D": {cssClass: "small-emoticon spaced-emoticon"},
		"XD": {cssClass: "small-emoticon spaced-emoticon"},
		"xD": {cssClass: "small-emoticon spaced-emoticon"},
		"=D": {cssClass: "small-emoticon spaced-emoticon"},
		"8O": {cssClass: "small-emoticon spaced-emoticon"},
		"[+=..]": {cssClass: "no-rotate nintendo-controller"}
	};

	var specialRegex = new RegExp('(\\' + this.escapeCharacters.join('|\\') + ')', 'g');
	// One of these characters must be present before the matched emoticon, or the matched emoticon must be the first character in the container HTML
	//  This is to ensure that the characters in the middle of HTML properties or URLs are not matched as emoticons
	//  Below matches ^ (first character in container HTML), \s (whitespace like space or tab), or \0 (NULL character)
	// (<\\S+.*>) matches <\\S+.*> (matches an HTML tag like <span> or <div>), but haven't quite gotten it working yet, need to push this fix now
	var preMatch = '(^|[\\s\\0])';

	for (var i = this.threeCharacterEmoticons.length - 1; i >= 0; --i) {
		this.threeCharacterEmoticons[i] = this.threeCharacterEmoticons[i].replace(specialRegex, '\\$1');
		this.threeCharacterEmoticons[i] = new RegExp(preMatch + '(' + this.threeCharacterEmoticons[i] + ')', 'g');
	}

	for (var i = this.twoCharacterEmoticons.length - 1; i >= 0; --i) {
		this.twoCharacterEmoticons[i] = this.twoCharacterEmoticons[i].replace(specialRegex, '\\$1');
		this.twoCharacterEmoticons[i] = new RegExp(preMatch + '(' + this.twoCharacterEmoticons[i] + ')', 'g');
	}

	for (var emoticon in this.specialEmoticons) {
		this.specialEmoticons[emoticon].regexp = emoticon.replace(specialRegex, '\\$1');
		this.specialEmoticons[emoticon].regexp = new RegExp(preMatch + '(' + this.specialEmoticons[emoticon].regexp + ')', 'g');
	}

	this.defaults = {animate: true, delay: 500, exclude: 'pre,code,.no-emoticons'}
};

CSSEmoticon.prototype.emoticonize = function (str, options) {
	// $.extend({}, this.defaults, options);
	var opts = {};

	for (var key in this.defaults) {
		opts[key] = this.defaults[key];
	}

	for (var key in options) {
		opts[key] = options[key];
	}

	var exclude = 'span.css-emoticon';
	if (opts.exclude) {
		exclude += ',' + opts.exclude;
	}
	var excludeArray = exclude.split(',');

	var cssClass = 'css-emoticon';
	if (opts.animate) {
		cssClass += ' un-transformed-emoticon animated-emoticon';
	}

	for (var emoticon in this.specialEmoticons) {
		var specialCssClass = cssClass + " " + this.specialEmoticons[emoticon].cssClass;
		str = str.replace(this.specialEmoticons[emoticon].regexp, "$1<span class='" + specialCssClass + "'>$2</span>");
	}

	for (var key in this.threeCharacterEmoticons) {
		var regexp = this.threeCharacterEmoticons[key];
		str = str.replace(regexp, "$1<span class='" + cssClass + "'>$2</span>");
	}

	for (var key in this.twoCharacterEmoticons) {
		var regexp = this.twoCharacterEmoticons[key];
		str = str.replace(regexp, "$1<span class='" + cssClass + " spaced-emoticon'>$2</span>");
	}

	// animate emoticons
	if (opts.animate) {
		setTimeout(function () {
			var untransformed = document.body.getElementsByClassName("un-transformed-emoticon");
			for(var key in untransformed) {
				if(typeof untransformed[key] == "object") {
					untransformed[key].classList.remove("un-transformed-emoticon");
				}
			}
		}, opts.delay);
	}

	return str;
};
