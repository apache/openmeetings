/*
Author: Oliver Steele
Copyright: Copyright 2006, 2008 Oliver Steele.  All rights reserved.
Homepage: http://osteele.com/sources/openlaszlo/json
License: MIT License.
Version: 1.0

== JSON.stringify
function JSON.stringify(value, [options]);

Usage:
  JSON.stringify(123) // => '123'
  JSON.stringify([1,2,3]) // => '[1,2,3]'
  JSON.stringify({a:1,b:2}) // => '{"a":1,"b":2}'

== JSON.parse
function JSON.parse(string, [options]);

Parses the first JSON expression from +string+.  If
+options.allowSuffix+ is not specified (the default), the expression
must span the string or the parse produces an error.

Errors are signalled by returning undefined, and optionally invoking
+options.errorHandler+ (see below).

Usage:
  JSON.parse('123') // => 123
  JSON.parse('[1,2,3]') // => [1,2,3]
  JSON.parse('{"a":1,"b":2}') // => {a: 1, b: 2}
  JSON.parse('[') // => undefined (parse error)

+options+ can contain these properties:
* allowSuffix: allow text to follow the expression.  This can be
  used to pack multiple JSON expressions into a single string.
+ startIndex: the index into the string to begin parsing.  This is
  useful in conjunction with allowSuffix.
* errorHandler: function (message: string, index: number): void
  If an error occurs, the errorHandler is invoked with two arguments:
  an error string and a character offset.

=== Notes
JSON.parse accepts these strings don't conform to the JSON grammar:
* '1.e1' -- JSON requires digits after the decimal point
* '01' -- JSON doesn't permit leading zeros.
* '"\a"' -- JSON doesn't permit nonescape characters to be encoded

The parser does not accept unquoted object property names such as {a:
1}.  These are valid JavaScript (when the property name is a valid
identifier), but they are not in the JSON grammar.
*/

/*
Implementation notes:
* use for(;;) instead of for(in) where order matters, since Flash
  iterates backwards.
* test with obj[n] instead of obj.n, to avoid debugger warnings
 */

var JSON = {};

JSON.stringify = function (value):String {
    var generator = new JSON.generator();
    return generator.generate(value);
};

JSON.parse = function (str:String, options=null) {
    var parser = new JSON.parser();
    if (options != null) {
        parser.setOptions(options);
    }
    return parser.parse(str);
};

JSON.HEXDIGITS = "0123456789abcdef";

JSON.generator = function () {};

JSON.generator.CHARACTER_QUOTES = {
    '\\': '\\\\',
    '\"': '\\\"',
    '\b': '\\b',
    '\f': '\\f',
    '\n': '\\n',
    '\r': '\\r',
    '\t': '\\t'
};

JSON.generator.prototype.primitiveExceptions = {
    'NaN': 'null',
    'Infinity': 'null',
    '-Infinity': 'null'
};

JSON.generator.prototype.generate = function (value, options) {
    this.segments = [];
    this.appendValue(value);
    return this.segments.join('');
};

JSON.generator.prototype.appendValue = function (value) {
    switch (typeof value) {
    case 'string':
        this.appendString(value);
        break;
    case 'object':
        this.findObjectAppender(value).apply(this, [value]);
        break;
    default:
        this.appendPrimitive(value);
    }
};

JSON.generator.prototype.appendPrimitive = function (v) {
    var s = String(v);
    if (this.primitiveExceptions[s])
        s = this.primitiveExceptions[s];
    this.segments.push(s);
};

JSON.generator.prototype.appendString = function (string) {
    var segments = this.segments;
    var escapes = JSON.generator.CHARACTER_QUOTES;
    var start = 0;
    var i = 0;
    segments.push('"');
    while (i < string.length) {
        var c = string.charAt(i++);
        if (escapes[c]) {
            if (i-1 > start)
                segments.push(string.slice(start, i-1));
            segments.push(escapes[c]);
            start = i;
        } else if (c < ' ' || c > '~') {
            var n = c.charCodeAt(0);
            segments.push('\\u');
            for (var shift = 16; (shift -= 4) >= 0; )
                segments.push(JSON.HEXDIGITS.charAt((n >> shift) & 15));
            start = i;
        } // else collect into current segment
    }
    if (i > start)
        segments.push(string.slice(start, i));
    segments.push('"');
};

// This implements an ordered hash --- ordered, so that Object comes
// last
JSON.generator.prototype.objectAppenders = [
    // treat the Object types that correspond to primitives as though
    // they were the corresponding primitives, so that, for example,
    // {true, new Boolean(true)}, {true, new Boolean(true)}, and {'a',
    // new String('a')} are all equivalence classes for stringifying
    [Boolean, JSON.generator.prototype.appendPrimitive],
    [Number, JSON.generator.prototype.appendPrimitive],
    [String, JSON.generator.prototype.appendString],
    [Array, function (ar) {
        var segments = this.segments;
        segments.push("[");
        for (var i = 0; i < ar.length; i++) {
            if (i > 0) segments.push(",");
            this.appendValue(ar[i]);
        }
        segments.push("]");
    }],
    // This must come last, since it's a superclass
    [Object, function (object) {
        var segments = this.segments;
        segments.push("{");
        var count = 0;
        for (var key in object) {
            if (count++) segments.push(",");
            this.appendValue(key);
            segments.push(":");
            this.appendValue(object[key]);
        }
        segments.push("}");
    }]];

JSON.generator.prototype.findObjectAppender = function (object) {
    if (object == null)
        return function (object) {this.segments.push("null")};
    for (var i = 0; i < this.objectAppenders.length; i++) {
        var entry = this.objectAppenders[i];
        if (object instanceof entry[0])
            return entry[1];
    }
    // program error
};


JSON.parser = function () {
    this.errorHandler = null;
};

JSON.parser.WHITESPACE = " \t\n\r\f";

JSON.parser.CHARACTER_UNQUOTES = {
    'b': '\b',
    'f': '\f',
    'n': '\n',
    'r': '\r',
    't': '\t'
};

JSON.parser.prototype.setOptions = function (options) {
    if (options['errorHandler'])
        this.errorHandler = options.errorHandler;
};

JSON.parser.prototype.parse = function (str) {
    this.string = str;
    this.index = 0;
    this.message = null;
    var value = this.readValue();
    if (!this.message && this.next())
        value = this.error("extra characters at the end of the string");
    if (this.message && this.errorHandler)
        this.errorHandler(this.message, this.index);
    return value;
};

JSON.parser.prototype.error = function (message) {
    this.message = message;
    return undefined;
}
    
JSON.parser.prototype.readValue = function () {
    var c = this.next();
    var fn = c && this.table[c];
    if (fn)
        return fn.apply(this);
    var keywords = {'true': true, 'false': false, 'null': null};
    var s = this.string;
    var i = this.index-1;
    for (var w in keywords) {
        if (s.slice(i, i+w.length) == w) {
            this.index = i+w.length;
            return keywords[w];
        }
    }
    if (c) return this.error("invalid character: '" + c + "'");
    return this.error("empty expression");
}

JSON.parser.prototype.table = {
    '{': function () {
        var o = {};
        var c;
        var count = 0;
        while ((c = this.next()) != '}') {
            if (count) {
                if (c != ',')
                    this.error("missing ','");
            } else if (c == ',') {
                return this.error("extra ','");
            } else
                --this.index;
            var k = this.readValue();
            if (typeof k == "undefined") return undefined;
            if (this.next() != ':') return this.error("missing ':'");
            var v = this.readValue();
            if (typeof v == "undefined") return undefined;
            o[k] = v;
            count++;
        }
        return o;
    },
    '[': function () {
        var ar = [];
        var c;
        while ((c = this.next()) != ']') {
            if (!c) return this.error("unmatched '['");
            if (ar.length) {
                if (c != ',')
                    this.error("missing ','");
            } else if (c == ',') {
                return this.error("extra ','");
            } else
                --this.index;
            var n = this.readValue();
            if (typeof n == "undefined") return undefined;
            ar.push(n);
        }
        return ar;
    },
    '"': function () {
        var s = this.string;
        var i = this.index;
        var start = i;
        var segments = [];
        var c;
        while ((c = s.charAt(i++)) != '"') {
            //if (i == s.length) return this.error("unmatched '\"'");
            if (!c) return this.error("umatched '\"'");
            if (c == '\\') {
                if (start < i-1)
                    segments.push(s.slice(start, i-1));
                c = s.charAt(i++);
                if (c == 'u') {
                    var code = 0;
                    start = i;
                    while (i < start+4) {
                        c = s.charAt(i++);
                        var n = JSON.HEXDIGITS.indexOf(c.toLowerCase());
                        if (n < 0) return this.error("invalid unicode digit: '"+c+"'");
                        code = code * 16 + n;
                    }
                    segments.push(String.fromCharCode(code));
                } else
                    segments.push(JSON.parser.CHARACTER_UNQUOTES[c] || c);
                start = i;
            }
        }
        if (start < i-1)
            segments.push(s.slice(start, i-1));
        this.index = i;
        return segments.length == 1 ? segments[0] : segments.join('');
    },
    // Also any digit.  The statement that follows this table
    // definition fills in the digits.
    '-': function () {
        var s = this.string;
        var i = this.index;
        var start = i-1;
        var state = 'int';
        var permittedSigns = '-';
        var transitions = {
            'int+.': 'frac',
            'int+e': 'exp',
            'frac+e': 'exp'
        };
        do {
            var c = s.charAt(i++);
            if (!c) break;
            if ('0' <= c && c <= '9') continue;
            if (permittedSigns.indexOf(c) >= 0) {
                permittedSigns = '';
                continue;
            }
            state = transitions[state+'+'+c.toLowerCase()];
            if (state == 'exp') permittedSigns = '+-';
        } while (state);
        this.index = --i;
        s = s.slice(start, i)
        if (s == '-') return this.error("invalid number");
        return Number(s);
    }
};
// copy table['_'] to each of table[i] | i <- '0'..'9':
(function (table) {
    for (var i = 0; i <= 9; i++)
        table[String(i)] = table['-'];
})(JSON.parser.prototype.table);

// return the next non-whitespace character, or undefined
JSON.parser.prototype.next = function () {
    var s = this.string;
    var i = this.index;
    do {
        if (i == s.length) return undefined;
        var c = s.charAt(i++);
    } while (JSON.parser.WHITESPACE.indexOf(c) >= 0);
    this.index = i;
    return c;
};
