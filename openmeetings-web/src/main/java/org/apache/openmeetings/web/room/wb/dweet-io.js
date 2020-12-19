//     dweet.io.js
//     http://dweet.io
//     (c) 2014 Jim Heising and Bug Labs, Inc.
//     dweet.io.js may be freely distributed under the MIT license.
(function () {

	var isNode = true;

	// Is this loading into node.js?
	try {
		isNode = (require);
	}
	catch (e) {
		isNode = false;
	}

	var io;
	var request;

	var LAST_THING_NAME = "last-thing.dat";
	var DWEET_SERVER = "https://dweet.io:443";
	var STRICT_SSL = true;
	var REQUEST_TIMEOUT = 5000;
	var lastThing;

	if (isNode) {
		io = require("socket.io-client");
		request = require("request");

		if (require("fs").existsSync(LAST_THING_NAME)) {
			try {
				lastThing = require("fs").readFileSync(LAST_THING_NAME).toString();
			}
			catch (e) {
			}
		}
	}
	else {
		request = function (options, callback) {
			var self = this;
			var src = options.url + (options.url.indexOf("?") + 1 ? "&" : "?");
			var params = [];
			var param_name = "";

			for (param_name in options.json) {
				params.push(param_name + "=" + encodeURIComponent(options.json[param_name]));
			}

			// Generate a unique callbackname
			var callbackName = "callback";
			var index = 0;
			while (window.dweetCallback[callbackName + index]) {
				index++;
			}

			callbackName = callbackName + index;
			window.dweetCallback[callbackName] = function (data) {
				callback(null, data, data);
			};

			// We're going to load everything with JSONP.
			params.push("callback=dweetCallback." + callbackName);
			params.push("_" + "=" + Date.now());

			src += params.join("&");

			dweet_script_loader(src, function (script) {
				script.parentNode.removeChild(script);
				window.dweetCallback[callbackName] = undefined;
				delete window.dweetCallback[callbackName];
			});
		};

		window.dweetCallback = {};

		(function () {
			var re = /ded|co/;
			var onload = 'onload';
			var onreadystatechange = 'onreadystatechange';
			var readyState = 'readyState';

			var load = function (src, fn) {
				var script = document.createElement('script');
				script[onload] = script[onreadystatechange] = function () {
					if (!this[readyState] || re.test(this[readyState])) {
						script[onload] = script[onreadystatechange] = null;
						fn && fn(script);
						script = null;
					}
				};
				script.async = true;
				script.src = src;
				document.body.appendChild(script);
			};
			window.dweet_script_loader = function (srces, fn) {
				if (typeof srces == 'string') {
					load(srces, fn);
					return;
				}
				var src = srces.shift();
				load(src, function (script) {
					if (srces.length) {
						window.dweet_script_loader(srces, fn);
					}
					else {
						fn && fn(script);
					}
				});
			};
		})();
	}

	function isArray(obj) {
		return Object.prototype.toString.call( obj ) === '[object Array]'
	}

	function isFunction(obj) {
		return typeof obj === 'function';
	}

	var dweetioClient = function () {
		var self = this;
		var socket;
		var listenCallbacks = {};
		var currentThing = lastThing;

		function normalizeDweet(dweet) {
			if (dweet.created) {
				dweet.created = new Date(dweet.created);
			}

			return dweet;
		}

		function normalizeDweets(dweets) {
			if (dweets instanceof Array) {
				for (var index = 0; index < dweets.length; index++) {
					var dweet = dweets[index];
					normalizeDweet(dweet);
				}
			}
			else {
				normalizeDweet(dweets);
			}

			return dweets;
		}

		function parseBody(body) {
			var responseData;

			try {
				if (typeof body == 'string' || body instanceof String) {
					responseData = JSON.parse(body);
				}
				else {
					responseData = body;
				}
			}
			catch (e) {
			}

			return responseData;
		}

		function processResponse(body) {
			var err;

			var responseData = parseBody(body);

			if (!responseData) {
				err = new Error("server returned an invalid response");
			}
			else if (responseData["this"] == "failed") {
				err = new Error(responseData["because"]);
			}

			return err;
		}

		function createKeyedURL(url, key) {
			if (key) {
				return url + (url.indexOf("?") + 1 ? "&" : "?") + "key=" + encodeURIComponent(key);
			}

			return url;
		}

		function processDweetResponse(err, callback, body) {
			var responseData = parseBody(body);

			if (!err) {
				err = processResponse(responseData);
			}

			if (responseData && responseData["with"]) {
				if (callback) callback(err, normalizeDweets(responseData["with"]));
			}
			else {
				if (callback) callback("no response from server", undefined);
			}
		}

		self.set_server = function (server, strictSSL) {
			DWEET_SERVER = server;
			STRICT_SSL = strictSSL;

			if (isNode) {
				if (strictSSL)
					require('https').globalAgent.options.rejectUnauthorized = true;
				else
					require('https').globalAgent.options.rejectUnauthorized = false;
			}
		}

		self.dweet = function (data, callback) {
			if (currentThing) {
				self.dweet_for(currentThing, data, callback);
			}
			else {
				request({
					url: DWEET_SERVER + "/dweet",
					jar: true,
					method: "POST",
					followAllRedirects: true,
					timeout: REQUEST_TIMEOUT,
					strictSSL: STRICT_SSL,
					json: data
				}, function (err, response, body) {
					var responseData = parseBody(body);

					if (responseData["with"] && responseData["with"].thing != currentThing) {
						currentThing = responseData["with"].thing;

						if (isNode) {
							require("fs").writeFile(LAST_THING_NAME, currentThing);
						}
					}

					processDweetResponse(err, callback, responseData);
				});
			}
		};

		self.dweet_for = function (thing, data, key, callback) {
			if (isFunction(key)) {
				callback = key;
				key = null;
			}

			request({
				url: createKeyedURL(DWEET_SERVER + "/dweet/for/" + thing, key),
				jar: true,
				method: "POST",
				followAllRedirects: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL,
				json: data
			}, function (err, response, body) {
				processDweetResponse(err, callback, body);
			});
		}

		self.get_latest_dweet_for = function (thing, key, callback) {
			if (isFunction(key)) {
				callback = key;
				key = null;
			}

			request({
				url: createKeyedURL(DWEET_SERVER + "/get/latest/dweet/for/" + thing, key),
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				processDweetResponse(err, callback, body);
			});
		}

		self.get_all_dweets_for = function (thing, key, callback) {
			if (isFunction(key)) {
				callback = key;
				key = null;
			}

			request({
				url: createKeyedURL(DWEET_SERVER + "/get/dweets/for/" + thing, key),
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				processDweetResponse(err, callback, body);
			});
		}
		
		self.get_key_for = function (account, thingname, callback) {
            request({
                url: createKeyedURL(DWEET_SERVER + "/get/key/for/" + account + '/' + thingname),
                jar: true,
                timeout: REQUEST_TIMEOUT,
                strictSSL: STRICT_SSL
            }, function (err, response, body) {
                processDweetResponse(err, callback, body);
            });
		}

		self.create_key_for = function (account, thingname, callback) {
            request({
                url: createKeyedURL(DWEET_SERVER + "/create/key/for/" + account + '/' + thingname),
                jar: true,
                timeout: REQUEST_TIMEOUT,
                strictSSL: STRICT_SSL
            }, function (err, response, body) {
                processDweetResponse(err, callback, body);
            });
		}
		
		self.get_keys_for_account = function (account, callback) {
            request({
                url: createKeyedURL(DWEET_SERVER + "/get/keys/for/" + account),
                jar: true,
                timeout: REQUEST_TIMEOUT,
                strictSSL: STRICT_SSL
            }, function (err, response, body) {
                processDweetResponse(err, callback, body);
            });
		}

		self.get_keys_for_account = function (account, startPosition, endPosition, callback) {
            request({
                url: createKeyedURL(DWEET_SERVER + "/get/keys/for/" + account + '?startPosition=' + startPosition + '&endPosition=' + endPosition),
                jar: true,
                timeout: REQUEST_TIMEOUT,
                strictSSL: STRICT_SSL
            }, function (err, response, body) {
                processDweetResponse(err, callback, body);
            });			
		}


		self.listen_for = function (thing, key, callback) {
			if (isFunction(key)) {
				callback = key;
				key = null;
			}

			// Initialize our callback list
			if (!listenCallbacks[thing]) {
				listenCallbacks[thing] = [];
			}

			// Add this to our callbacks
			if (listenCallbacks[thing].indexOf(callback) == -1) {
				listenCallbacks[thing].push(callback);
			}

			function createSocket() {
				socket = io.connect(DWEET_SERVER + "/stream");

				socket.on("connect", function () {
					// Subscribe to all of the things that we might have asked for before connecting
					for (var id in listenCallbacks) {
						socket.emit("subscribe", {thing: id, key: key});
					}
				});

				socket.on("new_dweet", function (msg) {
					if (listenCallbacks[msg.thing]) {
						normalizeDweets(msg);

						var callbacks = listenCallbacks[msg.thing];
						for (var index = 0; index < callbacks.length; index++) {
							callbacks[index](msg);
						}
					}
				});
			}

			if (!socket) {
				if (isNode) {
					createSocket();
				}
				else {
					dweet_script_loader([DWEET_SERVER + "/socket.io/socket.io.js"], function () {
						io = window.io;
						createSocket();
					});
				}
			}
			if (socket) {
				socket.emit("subscribe", {thing: thing, key: key});
			}
		}

		self.stop_listening = function () {
			listenCallbacks = {};

			if (socket) {
				socket.disconnect();
				socket = undefined;
			}
		}

		self.stop_listening_for = function (thing) {
			listenCallbacks[thing] = undefined;
			delete listenCallbacks[thing];

			if (socket) {
				socket.emit("unsubscribe", {thing: thing});
			}
		}

		self.lock = function (thing, lock, key, callback) {
			request({
				url: DWEET_SERVER + "/lock/" + thing + "?lock=" + lock + "&key=" + key,
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				if (callback) callback(err);
			});
		}

		self.unlock = function (thing, key, callback) {
			request({
				url: createKeyedURL(DWEET_SERVER + "/unlock/" + thing, key),
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				if (callback) callback(err);
			});
		}

		self.remove_lock = function (lock, key, callback) {
			request({
				url: DWEET_SERVER + "/remove/lock/" + lock + "?key=" + key,
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				if (callback) callback(err);
			});
		}

		self.set_alert = function(thing, recipients, condition, key, callback)
		{
			if(isArray(recipients))
			{
				recipients = recipients.join();
			}

			request({
				url: createKeyedURL(DWEET_SERVER + "/alert/" + encodeURIComponent(recipients) + "/when/" + thing + "/" + encodeURIComponent(condition), key),
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				if (callback) callback(err);
			});
		}

		self.get_alert = function(thing, key, callback)
		{
			request({
				url: createKeyedURL(DWEET_SERVER + "/get/alert/for/" + thing, key),
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				var responseData = parseBody(body);

				if (callback) callback(err, responseData["with"]);
			});
		}

		self.remove_alert = function(thing, key, callback)
		{
			request({
				url: DWEET_SERVER + "/remove/alert/for/" + thing + "?key=" + key,
				jar: true,
				timeout: REQUEST_TIMEOUT,
				strictSSL: STRICT_SSL
			}, function (err, response, body) {
				if (!err) {
					err = processResponse(body);
				}

				if (callback) callback(err);
			});
		}
	};

	if (isNode) {
		module.exports = dweetioClient;
	}
	else {
		window.dweetio = new dweetioClient();
	}
})();