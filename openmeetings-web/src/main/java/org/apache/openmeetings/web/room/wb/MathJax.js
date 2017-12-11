// MathJax single file build. Licenses of its components apply
/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax.js
 *
 *  The main support code for the MathJax Hub, including the
 *  Ajax, Callback, Messaging, and Object-Oriented Programming
 *  libraries, as well as the base Jax classes, and startup
 *  processing code.
 *
 *  ---------------------------------------------------------------------
 *
 *  Copyright (c) 2009-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


//
//  Check if browser can support MathJax (no one fails this nowadays)
//
if (document.getElementById && document.childNodes && document.createElement) {
//
//  Skip if MathJax is already loaded
//
if (!(window.MathJax && MathJax.Hub)) {

//
//  Get author configuration from MathJax variable, if any
//
if (window.MathJax) {window.MathJax = {AuthorConfig: window.MathJax}}
               else {window.MathJax = {}}

// MathJax.isPacked = true; // This line is uncommented by the packer.

MathJax.version = "2.7.2";
MathJax.fileversion = "2.7.2";
MathJax.cdnVersion = "2.7.2";  // specifies a revision to break caching
MathJax.cdnFileVersions = {};  // can be used to specify revisions for individual files

/**********************************************************/

(function (BASENAME) {
  var BASE = window[BASENAME];
  if (!BASE) {BASE = window[BASENAME] = {}}

  var PROTO = [];  // a static object used to indicate when a prototype is being created
  var OBJECT = function (def) {
    var obj = def.constructor; if (!obj) {obj = function () {}}
    for (var id in def) {if (id !== 'constructor' && def.hasOwnProperty(id)) {obj[id] = def[id]}}
    return obj;
  };
  var CONSTRUCTOR = function () {
    return function () {return arguments.callee.Init.call(this,arguments)};
  };

  BASE.Object = OBJECT({
    constructor: CONSTRUCTOR(),

    Subclass: function (def,classdef) {
      var obj = CONSTRUCTOR();
      obj.SUPER = this; obj.Init = this.Init;
      obj.Subclass = this.Subclass; obj.Augment = this.Augment;
      obj.protoFunction = this.protoFunction;
      obj.can = this.can; obj.has = this.has; obj.isa = this.isa;
      obj.prototype = new this(PROTO);
      obj.prototype.constructor = obj;  // the real constructor
      obj.Augment(def,classdef);
      return obj;
    },

    Init: function (args) {
      var obj = this;
      if (args.length === 1 && args[0] === PROTO) {return obj}
      if (!(obj instanceof args.callee)) {obj = new args.callee(PROTO)}
      return obj.Init.apply(obj,args) || obj;
    },

    Augment: function (def,classdef) {
      var id;
      if (def != null) {
        for (id in def) {if (def.hasOwnProperty(id)) {this.protoFunction(id,def[id])}}
        // MSIE doesn't list toString even if it is not native so handle it separately
        if (def.toString !== this.prototype.toString && def.toString !== {}.toString)
          {this.protoFunction('toString',def.toString)}
      }
      if (classdef != null) {
        for (id in classdef) {if (classdef.hasOwnProperty(id)) {this[id] = classdef[id]}}
      }
      return this;
    },

    protoFunction: function (id,def) {
      this.prototype[id] = def;
      if (typeof def === "function") {def.SUPER = this.SUPER.prototype}
    },

    prototype: {
      Init: function () {},
      SUPER: function (fn) {return fn.callee.SUPER},
      can: function (method) {return typeof(this[method]) === "function"},
      has: function (property) {return typeof(this[property]) !== "undefined"},
      isa: function (obj) {return (obj instanceof Object) && (this instanceof obj)}
    },

    can: function (method)   {return this.prototype.can.call(this,method)},
    has: function (property) {return this.prototype.has.call(this,property)},
    isa: function (obj) {
      var constructor = this;
      while (constructor) {
        if (constructor === obj) {return true} else {constructor = constructor.SUPER}
      }
      return false;
    },


    SimpleSUPER: OBJECT({
      constructor: function (def) {return this.SimpleSUPER.define(def)},

      define: function (src) {
	var dst = {};
	if (src != null) {
          for (var id in src) {if (src.hasOwnProperty(id)) {dst[id] = this.wrap(id,src[id])}}
	  // MSIE doesn't list toString even if it is not native so handle it separately
          if (src.toString !== this.prototype.toString && src.toString !== {}.toString)
            {dst.toString = this.wrap('toString',src.toString)}
	}
	return dst;
      },

      wrap: function (id,f) {
        if (typeof(f) !== 'function' || !f.toString().match(/\.\s*SUPER\s*\(/)) {return f}
        var fn = function () {
          this.SUPER = fn.SUPER[id];
          try {var result = f.apply(this,arguments)} catch (err) {delete this.SUPER; throw err}
          delete this.SUPER;
          return result;
        }
        fn.toString = function () {return f.toString.apply(f,arguments)}
        return fn;
      }

    })
  });

  BASE.Object.isArray = Array.isArray || function (obj) {
    return Object.prototype.toString.call(obj) === "[object Array]";
  };

  BASE.Object.Array = Array;

})("MathJax");

/**********************************************************/

/*
 *  Create a callback function from various forms of data:
 *
 *     MathJax.Callback(fn)    -- callback to a function
 *
 *     MathJax.Callback([fn])  -- callback to function
 *     MathJax.Callback([fn,data...])
 *                             -- callback to function with given data as arguments
 *     MathJax.Callback([object,fn])
 *                             -- call fn with object as "this"
 *     MathJax.Callback([object,fn,data...])
 *                             -- call fn with object as "this" and data as arguments
 *     MathJax.Callback(["method",object])
 *                             -- call method of object wth object as "this"
 *     MathJax.Callback(["method",object,data...])
 *                             -- as above, but with data as arguments to method
 *
 *     MathJax.Callback({hook: fn, data: [...], object: this})
 *                             -- give function, data, and object to act as "this" explicitly
 *
 *     MathJax.Callback("code")  -- callback that compiles and executes a string
 *
 *     MathJax.Callback([...],i)
 *                             -- use slice of array starting at i and interpret
 *                                result as above.  (Used for passing "arguments" array
 *                                and trimming initial arguments, if any.)
 */

/*
 *    MathJax.Callback.After([...],cb1,cb2,...)
 *                             -- make a callback that isn't called until all the other
 *                                ones are called first.  I.e., wait for a union of
 *                                callbacks to occur before making the given callback.
 */

/*
 *  MathJax.Callback.Queue([callback,...])
 *                             -- make a synchronized queue of commands that process
 *                                sequentially, waiting for those that return uncalled
 *                                callbacks.
 */

/*
 *  MathJax.Callback.Signal(name)
 *                             -- finds or creates a names signal, to which listeners
 *                                can be attached and are signaled by messages posted
 *                                to the signal.  Responses can be asynchronous.
 */

(function (BASENAME) {
  var BASE = window[BASENAME];
  if (!BASE) {BASE = window[BASENAME] = {}}
  var isArray = BASE.Object.isArray;
  //
  //  Create a callback from an associative array
  //
  var CALLBACK = function (data) {
    var cb = function () {return arguments.callee.execute.apply(arguments.callee,arguments)};
    for (var id in CALLBACK.prototype) {
      if (CALLBACK.prototype.hasOwnProperty(id)) {
        if (typeof(data[id]) !== 'undefined') {cb[id] = data[id]}
                                         else {cb[id] = CALLBACK.prototype[id]}
      }
    }
    cb.toString = CALLBACK.prototype.toString;
    return cb;
  };
  CALLBACK.prototype = {
    isCallback: true,
    hook: function () {},
    data: [],
    object: window,
    execute: function () {
      if (!this.called || this.autoReset) {
        this.called = !this.autoReset;
        return this.hook.apply(this.object,this.data.concat([].slice.call(arguments,0)));
      }
    },
    reset: function () {delete this.called},
    toString: function () {return this.hook.toString.apply(this.hook,arguments)}
  };
  var ISCALLBACK = function (f) {
    return (typeof(f) === "function" && f.isCallback);
  }

  //
  //  Evaluate a string in global context
  //
  var EVAL = function (code) {return eval.call(window,code)}
  var TESTEVAL = function () {
    EVAL("var __TeSt_VaR__ = 1"); // check if it works in global context
    if (window.__TeSt_VaR__) {
      try { delete window.__TeSt_VaR__; } // NOTE IE9 throws when in IE7 mode
      catch (error) { window.__TeSt_VaR__ = null; }
    } else {
      if (window.execScript) {
        // IE
        EVAL = function (code) {
          BASE.__code = code;
          code = "try {"+BASENAME+".__result = eval("+BASENAME+".__code)} catch(err) {"+BASENAME+".__result = err}";
          window.execScript(code);
          var result = BASE.__result; delete BASE.__result; delete BASE.__code;
          if (result instanceof Error) {throw result}
          return result;
        }
      } else {
        // Safari2
        EVAL = function (code) {
          BASE.__code = code;
          code = "try {"+BASENAME+".__result = eval("+BASENAME+".__code)} catch(err) {"+BASENAME+".__result = err}";
          var head = (document.getElementsByTagName("head"))[0]; if (!head) {head = document.body}
          var script = document.createElement("script");
          script.appendChild(document.createTextNode(code));
          head.appendChild(script); head.removeChild(script);
          var result = BASE.__result; delete BASE.__result; delete BASE.__code;
          if (result instanceof Error) {throw result}
          return result;
        }
      }
    }
    TESTEVAL = null;
  };

  //
  //  Create a callback from various types of data
  //
  var USING = function (args,i) {
    if (arguments.length > 1) {
      if (arguments.length === 2 && !(typeof arguments[0] === 'function') &&
          arguments[0] instanceof Object && typeof arguments[1] === 'number')
            {args = [].slice.call(args,i)}
      else {args = [].slice.call(arguments,0)}
    }
    if (isArray(args) && args.length === 1 && typeof(args[0]) === 'function') {args = args[0]}
    if (typeof args === 'function') {
      if (args.execute === CALLBACK.prototype.execute) {return args}
      return CALLBACK({hook: args});
    } else if (isArray(args)) {
      if (typeof(args[0]) === 'string' && args[1] instanceof Object &&
                 typeof args[1][args[0]] === 'function') {
        return CALLBACK({hook: args[1][args[0]], object: args[1], data: args.slice(2)});
      } else if (typeof args[0] === 'function') {
        return CALLBACK({hook: args[0], data: args.slice(1)});
      } else if (typeof args[1] === 'function') {
        return CALLBACK({hook: args[1], object: args[0], data: args.slice(2)});
      }
    } else if (typeof(args) === 'string') {
      if (TESTEVAL) TESTEVAL();
      return CALLBACK({hook: EVAL, data: [args]});
    } else if (args instanceof Object) {
      return CALLBACK(args);
    } else if (typeof(args) === 'undefined') {
      return CALLBACK({});
    }
    throw Error("Can't make callback from given data");
  };

  //
  //  Wait for a given time to elapse and then perform the callback
  //
  var DELAY = function (time,callback) {
    callback = USING(callback);
    callback.timeout = setTimeout(callback,time);
    return callback;
  };

  //
  //  Callback used by AFTER, QUEUE, and SIGNAL to check if calls have completed
  //
  var WAITFOR = function (callback,signal) {
    callback = USING(callback);
    if (!callback.called) {WAITSIGNAL(callback,signal); signal.pending++}
  };
  var WAITEXECUTE = function () {
    var signals = this.signal; delete this.signal;
    this.execute = this.oldExecute; delete this.oldExecute;
    var result = this.execute.apply(this,arguments);
    if (ISCALLBACK(result) && !result.called) {WAITSIGNAL(result,signals)} else {
      for (var i = 0, m = signals.length; i < m; i++) {
        signals[i].pending--;
        if (signals[i].pending <= 0) {signals[i].call()}
      }
    }
  };
  var WAITSIGNAL = function (callback,signals) {
    if (!isArray(signals)) {signals = [signals]}
    if (!callback.signal) {
      callback.oldExecute = callback.execute;
      callback.execute = WAITEXECUTE;
      callback.signal = signals;
    } else if (signals.length === 1) {callback.signal.push(signals[0])}
      else {callback.signal = callback.signal.concat(signals)}
  };

  //
  //  Create a callback that is called when a collection of other callbacks have
  //  all been executed.  If the callback gets called immediately (i.e., the
  //  others are all already called), check if it returns another callback
  //  and return that instead.
  //
  var AFTER = function (callback) {
    callback = USING(callback);
    callback.pending = 0;
    for (var i = 1, m = arguments.length; i < m; i++)
      {if (arguments[i]) {WAITFOR(arguments[i],callback)}}
    if (callback.pending === 0) {
      var result = callback();
      if (ISCALLBACK(result)) {callback = result}
    }
    return callback;
  };

  //
  //  An array of prioritized hooks that are executed sequentially
  //  with a given set of data.
  //
  var HOOKS = MathJax.Object.Subclass({
    //
    //  Initialize the array and the auto-reset status
    //
    Init: function (reset) {
      this.hooks = [];
      this.remove = []; // used when hooks are removed during execution of list
      this.reset = reset;
      this.running = false;
    },
    //
    //  Add a callback to the list, in priority order (default priority is 10)
    //
    Add: function (hook,priority) {
      if (priority == null) {priority = 10}
      if (!ISCALLBACK(hook)) {hook = USING(hook)}
      hook.priority = priority;
      var i = this.hooks.length;
      while (i > 0 && priority < this.hooks[i-1].priority) {i--}
      this.hooks.splice(i,0,hook);
      return hook;
    },
    Remove: function (hook) {
      for (var i = 0, m = this.hooks.length; i < m; i++) {
        if (this.hooks[i] === hook) {
          if (this.running) {this.remove.push(i)}
            else {this.hooks.splice(i,1)}
          return;
        }
      }
    },
    //
    //  Execute the list of callbacks, resetting them if requested.
    //  If any return callbacks, return a callback that will be
    //  executed when they all have completed.
    //  Remove any hooks that requested being removed during processing.
    //
    Execute: function () {
      var callbacks = [{}];
      this.running = true;
      for (var i = 0, m = this.hooks.length; i < m; i++) {
        if (this.reset) {this.hooks[i].reset()}
        var result = this.hooks[i].apply(window,arguments);
        if (ISCALLBACK(result) && !result.called) {callbacks.push(result)}
      }
      this.running = false;
      if (this.remove.length) {this.RemovePending()}
      if (callbacks.length === 1) {return null}
      if (callbacks.length === 2) {return callbacks[1]}
      return AFTER.apply({},callbacks);
    },
    //
    //  Remove hooks that asked to be removed during execution of list
    //
    RemovePending: function () {
      this.remove = this.remove.sort();
      for (var i = this.remove.length-1; i >= 0; i--) {this.hooks.splice(i,1)}
      this.remove = [];
    }

  });

  //
  //  Run an array of callbacks passing them the given data.
  //  (Legacy function, since this has been replaced by the HOOKS object).
  //
  var EXECUTEHOOKS = function (hooks,data,reset) {
    if (!hooks) {return null}
    if (!isArray(hooks)) {hooks = [hooks]}
    if (!isArray(data))  {data = (data == null ? [] : [data])}
    var handler = HOOKS(reset);
    for (var i = 0, m = hooks.length; i < m; i++) {handler.Add(hooks[i])}
    return handler.Execute.apply(handler,data);
  };

  //
  //  Command queue that performs commands in order, waiting when
  //  necessary for commands to complete asynchronousely
  //
  var QUEUE = BASE.Object.Subclass({
    //
    //  Create the queue and push any commands that are specified
    //
    Init: function () {
      this.pending = this.running = 0;
      this.queue = [];
      this.Push.apply(this,arguments);
    },
    //
    //  Add commands to the queue and run them. Adding a callback object
    //  (rather than a callback specification) queues a wait for that callback.
    //  Return the final callback for synchronization purposes.
    //
    Push: function () {
      var callback;
      for (var i = 0, m = arguments.length; i < m; i++) {
        callback = USING(arguments[i]);
        if (callback === arguments[i] && !callback.called)
          {callback = USING(["wait",this,callback])}
        this.queue.push(callback);
      }
      if (!this.running && !this.pending) {this.Process()}
      return callback;
    },
    //
    //  Process the command queue if we aren't waiting on another command
    //
    Process: function (queue) {
      while (!this.running && !this.pending && this.queue.length) {
        var callback = this.queue[0];
        queue = this.queue.slice(1); this.queue = [];
        this.Suspend(); var result = callback(); this.Resume();
        if (queue.length) {this.queue = queue.concat(this.queue)}
        if (ISCALLBACK(result) && !result.called) {WAITFOR(result,this)}
      }
    },
    //
    //  Suspend/Resume command processing on this queue
    //
    Suspend: function () {this.running++},
    Resume: function () {if (this.running) {this.running--}},
    //
    //  Used by WAITFOR to restart the queue when an action completes
    //
    call: function () {this.Process.apply(this,arguments)},
    wait: function (callback) {return callback}
  });

  //
  //  Create a named signal that listeners can attach to, to be signaled by
  //  postings made to the signal.  Posts are queued if they occur while one
  //  is already in process.
  //
  var SIGNAL = QUEUE.Subclass({
    Init: function (name) {
      QUEUE.prototype.Init.call(this);
      this.name = name;
      this.posted = [];              // the messages posted so far
      this.listeners = HOOKS(true);  // those with interest in this signal
      this.posting = false;
      this.callback = null;
    },
    //
    // Post a message to the signal listeners, with callback for when complete
    //
    Post: function (message,callback,forget) {
      callback = USING(callback);
      if (this.posting || this.pending) {
        this.Push(["Post",this,message,callback,forget]);
      } else {
        this.callback = callback; callback.reset();
        if (!forget) {this.posted.push(message)}
        this.Suspend(); this.posting = true;
        var result = this.listeners.Execute(message);
        if (ISCALLBACK(result) && !result.called) {WAITFOR(result,this)}
        this.Resume(); this.posting = false;
        if (!this.pending) {this.call()}
      }
      return callback;
    },
    //
    //  Clear the post history (so new listeners won't get old messages)
    //
    Clear: function (callback) {
      callback = USING(callback);
      if (this.posting || this.pending) {
        callback = this.Push(["Clear",this,callback]);
      } else {
        this.posted = [];
        callback();
      }
      return callback;
    },
    //
    //  Call the callback (all replies are in) and process the command queue
    //
    call: function () {this.callback(this); this.Process()},

    //
    //  A listener calls this to register interest in the signal (so it will be called
    //  when posts occur).  If ignorePast is true, it will not be sent the post history.
    //
    Interest: function (callback,ignorePast,priority) {
      callback = USING(callback);
      this.listeners.Add(callback,priority);
      if (!ignorePast) {
        for (var i = 0, m = this.posted.length; i < m; i++) {
          callback.reset();
          var result = callback(this.posted[i]);
          if (ISCALLBACK(result) && i === this.posted.length-1) {WAITFOR(result,this)}
        }
      }
      return callback;
    },
    //
    //  A listener calls this to remove itself from a signal
    //
    NoInterest: function (callback) {
      this.listeners.Remove(callback);
    },

    //
    //  Hook a callback to a particular message on this signal
    //
    MessageHook: function (msg,callback,priority) {
      callback = USING(callback);
      if (!this.hooks) {this.hooks = {}; this.Interest(["ExecuteHooks",this])}
      if (!this.hooks[msg]) {this.hooks[msg] = HOOKS(true)}
      this.hooks[msg].Add(callback,priority);
      for (var i = 0, m = this.posted.length; i < m; i++)
        {if (this.posted[i] == msg) {callback.reset(); callback(this.posted[i])}}
      callback.msg = msg; // keep track so we can remove it
      return callback;
    },
    //
    //  Execute the message hooks for the given message
    //
    ExecuteHooks: function (msg) {
      var type = (isArray(msg) ? msg[0] : msg);
      if (!this.hooks[type]) {return null}
      return this.hooks[type].Execute(msg);
    },
    //
    //  Remove a hook safely
    //
    RemoveHook: function (hook) {
      this.hooks[hook.msg].Remove(hook);
    }

  },{
    signals: {},  // the named signals
    find: function (name) {
      if (!SIGNAL.signals[name]) {SIGNAL.signals[name] = new SIGNAL(name)}
      return SIGNAL.signals[name];
    }
  });

  //
  //  The main entry-points
  //
  BASE.Callback = BASE.CallBack = USING;
  BASE.Callback.Delay = DELAY;
  BASE.Callback.After = AFTER;
  BASE.Callback.Queue = QUEUE;
  BASE.Callback.Signal = SIGNAL.find;
  BASE.Callback.Hooks = HOOKS;
  BASE.Callback.ExecuteHooks = EXECUTEHOOKS;
})("MathJax");


/**********************************************************/

(function (BASENAME) {
  var BASE = window[BASENAME];
  if (!BASE) {BASE = window[BASENAME] = {}}

  var isSafari2 = (navigator.vendor === "Apple Computer, Inc." &&
                   typeof navigator.vendorSub === "undefined");
  var sheets = 0; // used by Safari2

  //
  //  Update sheets count and look up the head object
  //
  var HEAD = function (head) {
    if (document.styleSheets && document.styleSheets.length > sheets)
      {sheets = document.styleSheets.length}
    if (!head) {
      head = document.head || ((document.getElementsByTagName("head"))[0]);
      if (!head) {head = document.body}
    }
    return head;
  };

  //
  //  Remove scripts that are completed so they don't clutter up the HEAD.
  //  This runs via setTimeout since IE7 can't remove the script while it is running.
  //
  var SCRIPTS = [];  // stores scripts to be removed after a delay
  var REMOVESCRIPTS = function () {
    for (var i = 0, m = SCRIPTS.length; i < m; i++) {BASE.Ajax.head.removeChild(SCRIPTS[i])}
    SCRIPTS = [];
  };

  var PATH = {};
  PATH[BASENAME] = "";                                        // empty path gets the root URL
  PATH.a11y = '[MathJax]/extensions/a11y';                    // a11y extensions
  PATH.Contrib = "https://cdn.mathjax.org/mathjax/contrib";   // the third-party extensions

  BASE.Ajax = {
    loaded: {},         // files already loaded
    loading: {},        // files currently in process of loading
    loadHooks: {},      // hooks to call when files are loaded
    timeout: 15*1000,   // timeout for loading of files (15 seconds)
    styleDelay: 1,      // delay to use before styles are available
    config: {
      root: "",         // URL of root directory to load from
      path: PATH        // paths to named URL's (e.g., [MathJax]/...)
    },
    params:  {},        // filled in from MathJax.js?...

    STATUS: {
      OK: 1,         // file is loading or did load OK
      ERROR: -1      // file timed out during load
    },

    //
    //  Return a complete URL to a file (replacing any root names)
    //
    fileURL: function (file) {
      var match;
      while ((match = file.match(/^\[([-._a-z0-9]+)\]/i)) && PATH.hasOwnProperty(match[1])) {
        file = (PATH[match[1]]||this.config.root) + file.substr(match[1].length+2);
      }
      return file;
    },
    //
    //  Replace root names if URL includes one
    //
    fileName: function (url) {
      var root = this.config.root;
      if (url.substr(0,root.length) === root) {url = "["+BASENAME+"]"+url.substr(root.length)}
      do {
        var recheck = false;
        for (var id in PATH) {if (PATH.hasOwnProperty(id) && PATH[id]) {
          if (url.substr(0,PATH[id].length) === PATH[id]) {
            url = "["+id+"]"+url.substr(PATH[id].length);
            recheck = true;
            break;
          }
        }}
      } while (recheck);
      return url;
    },
    //
    //  Cache-breaking revision number for file
    //
    fileRev: function (file) {
      var V = BASE.cdnFileVersions[file] || BASE.cdnVersion || '';
      if (V) {V = "?V="+V}
      return V;
    },
    urlRev: function (file) {return this.fileURL(file)+this.fileRev(file)},

    //
    //  Load a file if it hasn't been already.
    //  Make sure the file URL is "safe"?
    //
    Require: function (file,callback) {
      callback = BASE.Callback(callback); var type;
      if (file instanceof Object) {
        for (var i in file)
          {if (file.hasOwnProperty(i)) {type = i.toUpperCase(); file = file[i]}}
      } else {type = file.split(/\./).pop().toUpperCase()}
      if (this.params.noContrib && file.substr(0,9) === "[Contrib]") {
        callback(this.STATUS.ERROR);
      } else {
        file = this.fileURL(file);
        // FIXME: check that URL is OK
        if (this.loaded[file]) {
          callback(this.loaded[file]);
        } else {
          var FILE = {}; FILE[type] = file;
          this.Load(FILE,callback);
        }
      }
      return callback;
    },

    //
    //  Load a file regardless of where it is and whether it has
    //  already been loaded.
    //
    Load: function (file,callback) {
      callback = BASE.Callback(callback); var type;
      if (file instanceof Object) {
        for (var i in file)
          {if (file.hasOwnProperty(i)) {type = i.toUpperCase(); file = file[i]}}
      } else {type = file.split(/\./).pop().toUpperCase()}
      file = this.fileURL(file);
      if (this.loading[file]) {
        this.addHook(file,callback);
      } else {
        this.head = HEAD(this.head);
        if (this.loader[type]) {this.loader[type].call(this,file,callback)}
          else {throw Error("Can't load files of type "+type)}
      }
      return callback;
    },

    //
    //  Register a load hook for a particular file (it will be called when
    //  loadComplete() is called for that file)
    //
    LoadHook: function (file,callback,priority) {
      callback = BASE.Callback(callback);
      if (file instanceof Object)
        {for (var i in file) {if (file.hasOwnProperty(i)) {file = file[i]}}}
      file = this.fileURL(file);
      if (this.loaded[file]) {callback(this.loaded[file])}
        else {this.addHook(file,callback,priority)}
      return callback;
    },
    addHook: function (file,callback,priority) {
      if (!this.loadHooks[file]) {this.loadHooks[file] = MathJax.Callback.Hooks()}
      this.loadHooks[file].Add(callback,priority);
      callback.file = file;
    },
    removeHook: function (hook) {
      if (this.loadHooks[hook.file]) {
        this.loadHooks[hook.file].Remove(hook);
        if (!this.loadHooks[hook.file].hooks.length) {delete this.loadHooks[hook.file]}
      }
    },

    //
    //  Used when files are combined in a preloading configuration file
    //
    Preloading: function () {
      for (var i = 0, m = arguments.length; i < m; i++) {
        var file = this.fileURL(arguments[i]);
        if (!this.loading[file]) {this.loading[file] = {preloaded: true}}
      }
    },

    //
    //  Code used to load the various types of files
    //  (JS for JavaScript, CSS for style sheets)
    //
    loader: {
      //
      //  Create a SCRIPT tag to load the file
      //
      JS: function (file,callback) {
        var name = this.fileName(file);
        var script = document.createElement("script");
        var timeout = BASE.Callback(["loadTimeout",this,file]);
        this.loading[file] = {
          callback: callback,
          timeout: setTimeout(timeout,this.timeout),
          status: this.STATUS.OK,
          script: script
        };
        //
        // Add this to the structure above after it is created to prevent recursion
        //  when loading the initial localization file (before loading messsage is available)
        //
        this.loading[file].message = BASE.Message.File(name);
        script.onerror = timeout;  // doesn't work in IE and no apparent substitute
        script.type = "text/javascript";
        script.src = file+this.fileRev(name);
        this.head.appendChild(script);
      },
      //
      //  Create a LINK tag to load the style sheet
      //
      CSS: function (file,callback) {
        var name = this.fileName(file);
        var link = document.createElement("link");
        link.rel = "stylesheet"; link.type = "text/css";
        link.href = file+this.fileRev(name);
        this.loading[file] = {
          callback: callback,
          message: BASE.Message.File(name),
          status: this.STATUS.OK
        };
        this.head.appendChild(link);
        this.timer.create.call(this,[this.timer.file,file],link);
      }
    },

    //
    //  Timing code for checking when style sheets are available.
    //
    timer: {
      //
      //  Create the timing callback and start the timing loop.
      //  We use a delay because some browsers need it to allow the styles
      //  to be processed.
      //
      create: function (callback,node) {
        callback = BASE.Callback(callback);
        if (node.nodeName === "STYLE" && node.styleSheet &&
            typeof(node.styleSheet.cssText) !== 'undefined') {
          callback(this.STATUS.OK); // MSIE processes style immediately, but doesn't set its styleSheet!
        } else if (window.chrome && node.nodeName === "LINK") {
          callback(this.STATUS.OK); // Chrome doesn't give access to cssRules for stylesheet in
                                    //   a link node, so we can't detect when it is loaded.
        } else if (isSafari2) {
          this.timer.start(this,[this.timer.checkSafari2,sheets++,callback],this.styleDelay);
        } else {
          this.timer.start(this,[this.timer.checkLength,node,callback],this.styleDelay);
        }
        return callback;
      },
      //
      //  Start the timer for the given callback checker
      //
      start: function (AJAX,check,delay,timeout) {
        check = BASE.Callback(check);
        check.execute = this.execute; check.time = this.time;
        check.STATUS = AJAX.STATUS; check.timeout = timeout || AJAX.timeout;
        check.delay = check.total = delay || 0;
        if (delay) {setTimeout(check,delay)} else {check()}
      },
      //
      //  Increment the time total, increase the delay
      //  and test if we are past the timeout time.
      //
      time: function (callback) {
        this.total += this.delay;
        this.delay = Math.floor(this.delay * 1.05 + 5);
        if (this.total >= this.timeout) {callback(this.STATUS.ERROR); return 1}
        return 0;
      },
      //
      //  For JS file loads, call the proper routine according to status
      //
      file: function (file,status) {
        if (status < 0) {BASE.Ajax.loadTimeout(file)} else {BASE.Ajax.loadComplete(file)}
      },
      //
      //  Call the hook with the required data
      //
      execute: function () {this.hook.call(this.object,this,this.data[0],this.data[1])},
      //
      //  Safari2 doesn't set the link's stylesheet, so we need to look in the
      //  document.styleSheets array for the new sheet when it is created
      //
      checkSafari2: function (check,length,callback) {
        if (check.time(callback)) return;
        if (document.styleSheets.length > length &&
            document.styleSheets[length].cssRules &&
            document.styleSheets[length].cssRules.length)
          {callback(check.STATUS.OK)} else {setTimeout(check,check.delay)}
      },
      //
      //  Look for the stylesheets rules and check when they are defined
      //  and no longer of length zero.  (This assumes there actually ARE
      //  some rules in the stylesheet.)
      //
      checkLength: function (check,node,callback) {
        if (check.time(callback)) return;
        var isStyle = 0; var sheet = (node.sheet || node.styleSheet);
        try {if ((sheet.cssRules||sheet.rules||[]).length > 0) {isStyle = 1}} catch(err) {
          if (err.message.match(/protected variable|restricted URI/)) {isStyle = 1}
          else if (err.message.match(/Security error/)) {
            // Firefox3 gives "Security error" for missing files, so
            //   can't distinguish that from OK files on remote servers.
            //   or OK files in different directory from local files.
            isStyle = 1; // just say it is OK (can't really tell)
          }
        }
        if (isStyle) {
          // Opera 9.6 requires this setTimeout
          setTimeout(BASE.Callback([callback,check.STATUS.OK]),0);
        } else {
          setTimeout(check,check.delay);
        }
      }
    },

    //
    //  JavaScript code must call this when they are completely initialized
    //  (this allows them to perform asynchronous actions before indicating
    //  that they are complete).
    //
    loadComplete: function (file) {
      file = this.fileURL(file);
      var loading = this.loading[file];
      if (loading && !loading.preloaded) {
        BASE.Message.Clear(loading.message);
        clearTimeout(loading.timeout);
	if (loading.script) {
	  if (SCRIPTS.length === 0) {setTimeout(REMOVESCRIPTS,0)}
	  SCRIPTS.push(loading.script);
	}
        this.loaded[file] = loading.status; delete this.loading[file];
        this.addHook(file,loading.callback);
      } else {
        if (loading) {delete this.loading[file]}
        this.loaded[file] = this.STATUS.OK;
        loading = {status: this.STATUS.OK}
      }
      if (!this.loadHooks[file]) {return null}
      return this.loadHooks[file].Execute(loading.status);
    },

    //
    //  If a file fails to load within the timeout period (or the onerror handler
    //  is called), this routine runs to signal the error condition.
    //
    loadTimeout: function (file) {
      if (this.loading[file].timeout) {clearTimeout(this.loading[file].timeout)}
      this.loading[file].status = this.STATUS.ERROR;
      this.loadError(file);
      this.loadComplete(file);
    },

    //
    //  The default error hook for file load failures
    //
    loadError: function (file) {
      BASE.Message.Set(["LoadFailed","File failed to load: %1",file],null,2000);
      BASE.Hub.signal.Post(["file load error",file]);
    },

    //
    //  Defines a style sheet from a hash of style declarations (key:value pairs
    //  where the key is the style selector and the value is a hash of CSS attributes
    //  and values).
    //
    Styles: function (styles,callback) {
      var styleString = this.StyleString(styles);
      if (styleString === "") {
        callback = BASE.Callback(callback);
        callback();
      } else {
        var style = document.createElement("style"); style.type = "text/css";
        this.head = HEAD(this.head);
        this.head.appendChild(style);
        if (style.styleSheet && typeof(style.styleSheet.cssText) !== 'undefined') {
          style.styleSheet.cssText = styleString;
        } else {
          style.appendChild(document.createTextNode(styleString));
        }
        callback = this.timer.create.call(this,callback,style);
      }
      return callback;
    },

    //
    //  Create a stylesheet string from a style declaration object
    //
    StyleString: function (styles) {
      if (typeof(styles) === 'string') {return styles}
      var string = "", id, style;
      for (id in styles) {if (styles.hasOwnProperty(id)) {
        if (typeof styles[id] === 'string') {
          string += id + " {"+styles[id]+"}\n";
        } else if (BASE.Object.isArray(styles[id])) {
          for (var i = 0; i < styles[id].length; i++) {
            style = {}; style[id] = styles[id][i];
            string += this.StyleString(style);
          }
        } else if (id.substr(0,6) === '@media') {
          string += id + " {"+this.StyleString(styles[id])+"}\n";
        } else if (styles[id] != null) {
          style = [];
          for (var name in styles[id]) {if (styles[id].hasOwnProperty(name)) {
            if (styles[id][name] != null)
              {style[style.length] = name + ': ' + styles[id][name]}
          }}
          string += id +" {"+style.join('; ')+"}\n";
        }
      }}
      return string;
    }
  };

})("MathJax");

/**********************************************************/

MathJax.HTML = {
  //
  //  Create an HTML element with given attributes and content.
  //  The def parameter is an (optional) object containing key:value pairs
  //  of the attributes and their values, and contents is an (optional)
  //  array of strings to be inserted as text, or arrays of the form
  //  [type,def,contents] that describes an HTML element to be inserted
  //  into the current element.  Thus the contents can describe a complete
  //  HTML snippet of arbitrary complexity.  E.g.:
  //
  //    MathJax.HTML.Element("span",{id:"mySpan",style{"font-style":"italic"}},[
  //        "(See the ",["a",{href:"http://www.mathjax.org"},["MathJax home page"]],
  //        " for more details.)"]);
  //
  Element: function (type,def,contents) {
    var obj = document.createElement(type), id;
    if (def) {
      if (def.hasOwnProperty("style")) {
        var style = def.style; def.style = {};
        for (id in style) {if (style.hasOwnProperty(id))
          {def.style[id.replace(/-([a-z])/g,this.ucMatch)] = style[id]}}
      }
      MathJax.Hub.Insert(obj,def);
      for (id in def) {
        if (id === "role" || id.substr(0,5) === "aria-") obj.setAttribute(id,def[id]);
      }
    }
    if (contents) {
      if (!MathJax.Object.isArray(contents)) {contents = [contents]}
      for (var i = 0, m = contents.length; i < m; i++) {
        if (MathJax.Object.isArray(contents[i])) {
          obj.appendChild(this.Element(contents[i][0],contents[i][1],contents[i][2]));
        } else if (type === "script") { // IE throws an error if script is added as a text node
          this.setScript(obj, contents[i]);
        } else {
          obj.appendChild(document.createTextNode(contents[i]));
        }
      }
    }
    return obj;
  },
  ucMatch: function (match,c) {return c.toUpperCase()},
  addElement: function (span,type,def,contents) {return span.appendChild(this.Element(type,def,contents))},
  TextNode: function (text) {return document.createTextNode(text)},
  addText: function (span,text) {return span.appendChild(this.TextNode(text))},

  //
  //  Set and get the text of a script
  //
  setScript: function (script,text) {
    if (this.setScriptBug) {script.text = text} else {
      while (script.firstChild) {script.removeChild(script.firstChild)}
      this.addText(script,text);
    }
  },
  getScript: function (script) {
    var text = (script.text === "" ? script.innerHTML : script.text);
    return text.replace(/^\s+/,"").replace(/\s+$/,"");
  },

  //
  //  Manage cookies
  //
  Cookie: {
    prefix: "mjx",
    expires: 365,

    //
    //  Save an object as a named cookie
    //
    Set: function (name,def) {
      var keys = [];
      if (def) {
        for (var id in def) {if (def.hasOwnProperty(id)) {
          keys.push(id+":"+def[id].toString().replace(/&/g,"&&"));
        }}
      }
      var cookie = this.prefix+"."+name+"="+escape(keys.join('&;'));
      if (this.expires) {
        var time = new Date(); time.setDate(time.getDate() + this.expires);
        cookie += '; expires='+time.toGMTString();
      }
      try {document.cookie = cookie+"; path=/"} catch (err) {} // ignore errors saving cookies
    },

    //
    //  Get the contents of a named cookie and incorporate
    //  it into the given object (or return a fresh one)
    //
    Get: function (name,obj) {
      if (!obj) {obj = {}}
      var pattern = new RegExp("(?:^|;\\s*)"+this.prefix+"\\."+name+"=([^;]*)(?:;|$)");
      var match;
      try {match = pattern.exec(document.cookie)} catch (err) {}; // ignore errors reading cookies
      if (match && match[1] !== "") {
        var keys = unescape(match[1]).split('&;');
        for (var i = 0, m = keys.length; i < m; i++) {
          match = keys[i].match(/([^:]+):(.*)/);
          var value = match[2].replace(/&&/g,'&');
          if (value === "true") {value = true} else if (value === "false") {value = false}
            else if (value.match(/^-?(\d+(\.\d+)?|\.\d+)$/)) {value = parseFloat(value)}
          obj[match[1]] = value;
        }
      }
      return obj;
    }
  }

};


/**********************************************************/

MathJax.Localization = {

  locale: "en",
  directory: "[MathJax]/localization",
  strings: {
    // Currently, this list is not modified by the MathJax-i18n script. You can
    // run the following command in MathJax/unpacked/localization to update it:
    //
    // find . -name "*.js" | xargs grep menuTitle\: | grep -v qqq | sed 's/^\.\/\(.*\)\/.*\.js\:  /    "\1"\: \{/' | sed 's/,$/\},/' | sed 's/"English"/"English", isLoaded: true/' > tmp ; sort tmp > tmp2 ; sed '$ s/,$//' tmp2 ; rm tmp*
    //
    // This only takes languages with localization data so you must also add
    // the languages that use a remap but are not translated at all.
    //
    "ar": {menuTitle: "\u0627\u0644\u0639\u0631\u0628\u064A\u0629"},
    "ast": {menuTitle: "asturianu"},
    "bg": {menuTitle: "\u0431\u044A\u043B\u0433\u0430\u0440\u0441\u043A\u0438"},
    "bcc": {menuTitle: "\u0628\u0644\u0648\u0686\u06CC"},
    "br": {menuTitle: "brezhoneg"},
    "ca": {menuTitle: "catal\u00E0"},
    "cdo": {menuTitle: "M\u00ECng-d\u0115\u0324ng-ng\u1E73\u0304"},
    "cs": {menuTitle: "\u010De\u0161tina"},
    "da": {menuTitle: "dansk"},
    "de": {menuTitle: "Deutsch"},
    "diq": {menuTitle: "Zazaki"},
    "en": {menuTitle: "English", isLoaded: true},
    "eo": {menuTitle: "Esperanto"},
    "es": {menuTitle: "espa\u00F1ol"},
    "fa": {menuTitle: "\u0641\u0627\u0631\u0633\u06CC"},
    "fi": {menuTitle: "suomi"},
    "fr": {menuTitle: "fran\u00E7ais"},
    "gl": {menuTitle: "galego"},
    "he": {menuTitle: "\u05E2\u05D1\u05E8\u05D9\u05EA"},
    "ia": {menuTitle: "interlingua"},
    "it": {menuTitle: "italiano"},
    "ja": {menuTitle: "\u65E5\u672C\u8A9E"},
    "kn": {menuTitle: "\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"},
    "ko": {menuTitle: "\uD55C\uAD6D\uC5B4"},
    "lb": {menuTitle: "L\u00EBtzebuergesch"},
    "lki": {menuTitle: "\u0644\u06D5\u06A9\u06CC"},
    "lt": {menuTitle: "lietuvi\u0173"},
    "mk": {menuTitle: "\u043C\u0430\u043A\u0435\u0434\u043E\u043D\u0441\u043A\u0438"},
    "nl": {menuTitle: "Nederlands"},
    "oc": {menuTitle: "occitan"},
    "pl": {menuTitle: "polski"},
    "pt": {menuTitle: "portugu\u00EAs"},
    "pt-br": {menuTitle: "portugu\u00EAs do Brasil"},
    "ru": {menuTitle: "\u0440\u0443\u0441\u0441\u043A\u0438\u0439"},
    "sco": {menuTitle: "Scots"},
    "scn": {menuTitle: "sicilianu"},
    "sk": {menuTitle: "sloven\u010Dina"},
    "sl": {menuTitle: "sloven\u0161\u010Dina"},
    "sv": {menuTitle: "svenska"},
    "th": {menuTitle: "\u0E44\u0E17\u0E22"},
    "tr": {menuTitle: "T\u00FCrk\u00E7e"},
    "uk": {menuTitle: "\u0443\u043A\u0440\u0430\u0457\u043D\u0441\u044C\u043A\u0430"},
    "vi": {menuTitle: "Ti\u1EBFng Vi\u1EC7t"},
    "zh-hans": {menuTitle: "\u4E2D\u6587\uFF08\u7B80\u4F53\uFF09"},
    "zh-hant": {menuTitle: "\u6C49\u8BED"}
  },

  //
  //  The pattern for substitution escapes:
  //      %n or %{n} or %{plural:%n|option1|option1|...} or %c
  //
  pattern: /%(\d+|\{\d+\}|\{[a-z]+:\%\d+(?:\|(?:%\{\d+\}|%.|[^\}])*)+\}|.)/g,

  SPLIT: ("axb".split(/(x)/).length === 3 ?
    function (string,regex) {return string.split(regex)} :
    //
    //  IE8 and below don't do split() correctly when the pattern includes
    //    parentheses (the split should include the matched exrepssions).
    //    So implement it by hand here.
    //
    function (string,regex) {
      var result = [], match, last = 0;
      regex.lastIndex = 0;
      while ((match = regex.exec(string))) {
        result.push(string.substr(last,match.index-last));
        result.push.apply(result,match.slice(1));
        last = match.index + match[0].length;
      }
      result.push(string.substr(last));
      return result;
    }),

  _: function (id,phrase) {
    if (MathJax.Object.isArray(phrase)) {return this.processSnippet(id,phrase)}
    return this.processString(this.lookupPhrase(id,phrase),[].slice.call(arguments,2));
  },

  processString: function (string,args,domain) {
    //
    //  Process arguments for substitution
    //    If the argument is a snippet (and we are processing snippets) do so,
    //    Otherwise, if it is a number, convert it for the lacale
    //
    var i, m, isArray = MathJax.Object.isArray;
    for (i = 0, m = args.length; i < m; i++) {
      if (domain && isArray(args[i])) {args[i] = this.processSnippet(domain,args[i])}
    }
    //
    //  Split string at escapes and process them individually
    //
    var parts = this.SPLIT(string,this.pattern);
    for (i = 1, m = parts.length; i < m; i += 2) {
      var c = parts[i].charAt(0);  // first char will be { or \d or a char to be kept literally
      if (c >= "0" && c <= "9") {    // %n
        parts[i] = args[parts[i]-1];
        if (typeof parts[i] === "number") parts[i] = this.number(parts[i]);
      } else if (c === "{") {        // %{n} or %{plural:%n|...}
        c = parts[i].substr(1);
        if (c >= "0" && c <= "9") {  // %{n}
          parts[i] = args[parts[i].substr(1,parts[i].length-2)-1];
          if (typeof parts[i] === "number") parts[i] = this.number(parts[i]);
        } else {                     // %{plural:%n|...}
          var match = parts[i].match(/^\{([a-z]+):%(\d+)\|(.*)\}$/);
          if (match) {
            if (match[1] === "plural") {
              var n = args[match[2]-1];
              if (typeof n === "undefined") {
                parts[i] = "???";        // argument doesn't exist
              } else {
                n = this.plural(n) - 1;  // index of the form to use
                var plurals = match[3].replace(/(^|[^%])(%%)*%\|/g,"$1$2%\uEFEF").split(/\|/); // the parts (replacing %| with a special character)
                if (n >= 0 && n < plurals.length) {
                  parts[i] = this.processString(plurals[n].replace(/\uEFEF/g,"|"),args,domain);
                } else {
                  parts[i] = "???";      // no string for this index
                }
              }
            } else {parts[i] = "%"+parts[i]}  // not "plural", put back the % and leave unchanged
          }
        }
      }
      if (parts[i] == null) {parts[i] = "???"}
    }
    //
    //  If we are not forming a snippet, return the completed string
    //
    if (!domain) {return parts.join("")}
    //
    //  We need to return an HTML snippet, so buld it from the
    //  broken up string with inserted parts (that could be snippets)
    //
    var snippet = [], part = "";
    for (i = 0; i < m; i++) {
      part += parts[i]; i++;  // add the string and move on to substitution result
      if (i < m) {
        if (isArray(parts[i]))  {                // substitution was a snippet
          snippet.push(part);                        // add the accumulated string
          snippet = snippet.concat(parts[i]);        // concatenate the substution snippet
          part = "";                                 // start accumulating a new string
        } else {                                 // substitution was a string
          part += parts[i];                          // add to accumulating string
        }
      }
    }
    if (part !== "") {snippet.push(part)} // add final string
    return snippet;
  },

  processSnippet: function (domain,snippet) {
    var result = [];   // the new snippet
    //
    //  Look through the original snippet for
    //   strings or snippets to translate
    //
    for (var i = 0, m = snippet.length; i < m; i++) {
      if (MathJax.Object.isArray(snippet[i])) {
        //
        //  This could be a sub-snippet:
        //    ["tag"] or ["tag",{properties}] or ["tag",{properties},snippet]
        //  Or it could be something to translate:
        //    [id,string,args] or [domain,snippet]
        var data = snippet[i];
        if (typeof data[1] === "string") {        // [id,string,args]
          var id = data[0]; if (!MathJax.Object.isArray(id)) {id = [domain,id]}
          var phrase = this.lookupPhrase(id,data[1]);
          result = result.concat(this.processMarkdown(phrase,data.slice(2),domain));
        } else if (MathJax.Object.isArray(data[1])) {    // [domain,snippet]
          result = result.concat(this.processSnippet.apply(this,data));
        } else if (data.length >= 3) {            // ["tag",{properties},snippet]
          result.push([data[0],data[1],this.processSnippet(domain,data[2])]);
        } else {                                  // ["tag"] or ["tag",{properties}]
          result.push(snippet[i]);
        }
      } else {                                    // a string
        result.push(snippet[i]);
      }
    }
    return result;
  },

  markdownPattern: /(%.)|(\*{1,3})((?:%.|.)+?)\2|(`+)((?:%.|.)+?)\4|\[((?:%.|.)+?)\]\(([^\s\)]+)\)/,
  //   %c or *bold*, **italics**, ***bold-italics***, or `code`, or [link](url)

  processMarkdown: function (phrase,args,domain) {
    var result = [], data;
    //
    //  Split the string by the Markdown pattern
    //    (the text blocks are separated by
    //      c,stars,star-text,backtics,code-text,link-text,URL).
    //  Start with teh first text string from the split.
    //
    var parts = phrase.split(this.markdownPattern);
    var string = parts[0];
    //
    //  Loop through the matches and process them
    //
    for (var i = 1, m = parts.length; i < m; i += 8) {
      if (parts[i+1]) {        // stars (for bold/italic)
        //
        //  Select the tag to use by number of stars (three stars requires two tags)
        //
        data = this.processString(parts[i+2],args,domain);
        if (!MathJax.Object.isArray(data)) {data = [data]}
        data = [["b","i","i"][parts[i+1].length-1],{},data]; // number of stars determines type
        if (parts[i+1].length === 3) {data = ["b",{},data]}  // bold-italic
      } else if (parts[i+3]) { //  backtics (for code)
        //
        //  Remove one leading or trailing space, and process substitutions
        //  Make a <code> tag
        //
        data = this.processString(parts[i+4].replace(/^\s/,"").replace(/\s$/,""),args,domain);
        if (!MathJax.Object.isArray(data)) {data = [data]}
        data = ["code",{},data];
      } else if (parts[i+5]) { //  hyperlink
        //
        //  Process the link text, and make an <a> tag with the URL
        //
        data = this.processString(parts[i+5],args,domain);
        if (!MathJax.Object.isArray(data)) {data = [data]}
        data = ["a",{href:this.processString(parts[i+6],args),target:"_blank"},data];
      } else {
        //
        //  Escaped character (%c) gets added into the string.
        //
        string += parts[i]; data = null;
      }
      //
      //  If there is a tag to insert,
      //     Add any pending string, then push the tag
      //
      if (data) {
        result = this.concatString(result,string,args,domain);
        result.push(data); string = "";
      }
      //
      //  Process the string that follows matches pattern
      //
      if (parts[i+7] !== "") {string += parts[i+7]}
    };
    //
    //  Add any pending string and return the resulting snippet
    //
    result = this.concatString(result,string,args,domain);
    return result;
  },
  concatString: function (result,string,args,domain) {
    if (string != "") {
      //
      //  Process the substutions.
      //  If the result is not a snippet, turn it into one.
      //  Then concatenate the snippet to the current one
      //
      string = this.processString(string,args,domain);
      if (!MathJax.Object.isArray(string)) {string = [string]}
      result = result.concat(string);
    }
    return result;
  },

  lookupPhrase: function (id,phrase,domain) {
    //
    //  Get the domain and messageID
    //
    if (!domain) {domain = "_"}
    if (MathJax.Object.isArray(id)) {domain = (id[0] || "_"); id = (id[1] || "")}
    //
    //  Check if the data is available and if not,
    //    load it and throw a restart error so the calling
    //    code can wait for the load and try again.
    //
    var load = this.loadDomain(domain);
    if (load) {MathJax.Hub.RestartAfter(load)}
    //
    //  Look up the message in the localization data
    //    (if not found, the original English is used)
    //
    var localeData = this.strings[this.locale];
    if (localeData) {
      if (localeData.domains && domain in localeData.domains) {
        var domainData = localeData.domains[domain];
        if (domainData.strings && id in domainData.strings)
          {phrase = domainData.strings[id]}
      }
    }
    //
    //  return the translated phrase
    //
    return phrase;
  },

  //
  //  Load a langauge data file from the proper
  //  directory and file.
  //
  loadFile: function (file,data,callback) {
    callback = MathJax.Callback(callback);
    file = (data.file || file);  // the data's file name or the default name
    if (!file.match(/\.js$/)) {file += ".js"} // add .js if needed
    //
    //  Add the directory if the file doesn't
    //  contain a full URL already.
    //
    if (!file.match(/^([a-z]+:|\[MathJax\])/)) {
      var dir = (this.strings[this.locale].directory  ||
                 this.directory + "/" + this.locale ||
                 "[MathJax]/localization/" + this.locale);
      file = dir + "/" + file;
    }
    //
    //  Load the file and mark the data as loaded (even if it
    //  failed to load, so we don't continue to try to load it
    //  over and over).
    //
    var load = MathJax.Ajax.Require(file,function () {data.isLoaded = true; return callback()});
    //
    //  Return the callback if needed, otherwise null.
    //
    return (load.called ? null : load);
  },

  //
  //  Check to see if the localization data are loaded
  //  for the given domain; if not, load the data file,
  //  and return a callback for the loading operation.
  //  Otherwise return null (data are loaded).
  //
  loadDomain: function (domain,callback) {
    var load, localeData = this.strings[this.locale];
    if (localeData) {
      if (!localeData.isLoaded) {
        load = this.loadFile(this.locale,localeData);
        if (load) {
          return MathJax.Callback.Queue(
            load,["loadDomain",this,domain] // call again to load domain
          ).Push(callback||{});
        }
      }
      if (localeData.domains && domain in localeData.domains) {
        var domainData = localeData.domains[domain];
        if (!domainData.isLoaded) {
          load = this.loadFile(domain,domainData);
          if (load) {return MathJax.Callback.Queue(load).Push(callback)}
        }
      }
    }
    // localization data are loaded, so just do the callback
    return MathJax.Callback(callback)();
  },

  //
  //  Perform a function, properly handling
  //  restarts due to localization file loads.
  //
  //  Note that this may return before the function
  //  has been called successfully, so you should
  //  consider fn as running asynchronously.  (Callbacks
  //  can be used to synchronize it with other actions.)
  //
  Try: function (fn) {
    fn = MathJax.Callback(fn); fn.autoReset = true;
    try {fn()} catch (err) {
      if (!err.restart) {throw err}
      MathJax.Callback.After(["Try",this,fn],err.restart);
    }
  },

  //
  //  Reset the current language
  //
  resetLocale: function(locale) {
    // Selection algorithm:
    // 1) Downcase locale name (e.g. "en-US" => "en-us")
    // 2) Try a parent language (e.g. "en-us" => "en")
    // 3) Try the fallback specified in the data (e.g. "pt" => "pt-br")
    // 4) Otherwise don't change the locale.
    if (!locale) return;
    locale = locale.toLowerCase();
    while (!this.strings[locale]) {
      var dashPos = locale.lastIndexOf("-");
      if (dashPos === -1) return;
      locale = locale.substring(0, dashPos);
    }
    var remap = this.strings[locale].remap;
    this.locale = remap ? remap : locale;
  },

  //
  //  Set the current language
  //
  setLocale: function(locale) {
    this.resetLocale(locale);
    if (MathJax.Menu) {this.loadDomain("MathMenu")}
  },

  //
  //  Add or update a language or domain
  //
  addTranslation: function (locale,domain,definition) {
    var data = this.strings[locale], isNew = false;
    if (!data) {data = this.strings[locale] = {}; isNew = true}
    if (!data.domains) {data.domains = {}}
    if (domain) {
      if (!data.domains[domain]) {data.domains[domain] = {}}
      data = data.domains[domain];
    }
    MathJax.Hub.Insert(data,definition);
    if (isNew && MathJax.Menu.menu) {MathJax.Menu.CreateLocaleMenu()}
  },

  //
  //  Set CSS for an element based on font requirements
  //
  setCSS: function (div) {
    var locale = this.strings[this.locale];
    if (locale) {
      if (locale.fontFamily) {div.style.fontFamily = locale.fontFamily}
      if (locale.fontDirection) {
        div.style.direction = locale.fontDirection;
        if (locale.fontDirection === "rtl") {div.style.textAlign = "right"}
      }
    }
    return div;
  },

  //
  //  Get the language's font family or direction
  //
  fontFamily: function () {
    var locale = this.strings[this.locale];
    return (locale ? locale.fontFamily : null);
  },
  fontDirection: function () {
    var locale = this.strings[this.locale];
    return (locale ? locale.fontDirection : null);
  },

  //
  //  Get the language's plural index for a number
  //
  plural: function (n) {
    var locale = this.strings[this.locale];
    if (locale && locale.plural) {return locale.plural(n)}
    // default
    if (n == 1) {return 1} // one
    return 2; // other
  },

  //
  //  Convert a number to language-specific form
  //
  number: function(n) {
    var locale = this.strings[this.locale];
    if (locale && locale.number) {return locale.number(n)}
    // default
    return n;
  }
};


/**********************************************************/

MathJax.Message = {
  ready: false,  // used to tell when the styles are available
  log: [{}], current: null,
  textNodeBug: (navigator.vendor === "Apple Computer, Inc." &&
                typeof navigator.vendorSub === "undefined") ||
               (window.hasOwnProperty && window.hasOwnProperty("konqueror")), // Konqueror displays some gibberish with text.nodeValue = "..."

  styles: {
    "#MathJax_Message": {
      position: "fixed", left: "1px", bottom: "2px",
      'background-color': "#E6E6E6",  border: "1px solid #959595",
      margin: "0px", padding: "2px 8px",
      'z-index': "102", color: "black", 'font-size': "80%",
      width: "auto", 'white-space': "nowrap"
    },

    "#MathJax_MSIE_Frame": {
      position: "absolute",
      top:0, left: 0, width: "0px", 'z-index': 101,
      border: "0px", margin: "0px", padding: "0px"
    }
  },

  browsers: {
    MSIE: function (browser) {
      MathJax.Message.msieFixedPositionBug = ((document.documentMode||0) < 7);
      if (MathJax.Message.msieFixedPositionBug)
        {MathJax.Hub.config.styles["#MathJax_Message"].position = "absolute"}
      MathJax.Message.quirks = (document.compatMode === "BackCompat");
    },
    Chrome: function (browser) {
      MathJax.Hub.config.styles["#MathJax_Message"].bottom = "1.5em";
      MathJax.Hub.config.styles["#MathJax_Message"].left = "1em";
    }
  },

  Init: function (styles) {
    if (styles) {this.ready = true}
    if (!document.body || !this.ready) {return false}
    //
    //  ASCIIMathML replaces the entire page with a copy of itself (@#!#%@!!)
    //  so check that this.div is still part of the page, otherwise look up
    //  the copy and use that.
    //
    if (this.div && this.div.parentNode == null) {
      this.div = document.getElementById("MathJax_Message");
      if (this.div) {this.text = this.div.firstChild}
    }
    if (!this.div) {
      var frame = document.body;
      if (this.msieFixedPositionBug && window.attachEvent) {
        frame = this.frame = this.addDiv(document.body); frame.removeAttribute("id");
        frame.style.position = "absolute";
        frame.style.border = frame.style.margin = frame.style.padding = "0px";
        frame.style.zIndex = "101"; frame.style.height = "0px";
        frame = this.addDiv(frame);
        frame.id = "MathJax_MSIE_Frame";
        window.attachEvent("onscroll",this.MoveFrame);
        window.attachEvent("onresize",this.MoveFrame);
        this.MoveFrame();
      }
      this.div = this.addDiv(frame); this.div.style.display = "none";
      this.text = this.div.appendChild(document.createTextNode(""));
    }
    return true;
  },

  addDiv: function (parent) {
    var div = document.createElement("div");
    div.id = "MathJax_Message";
    if (parent.firstChild) {parent.insertBefore(div,parent.firstChild)}
      else {parent.appendChild(div)}
    return div;
  },

  MoveFrame: function () {
    var body = (MathJax.Message.quirks ? document.body : document.documentElement);
    var frame = MathJax.Message.frame;
    frame.style.left = body.scrollLeft + 'px';
    frame.style.top = body.scrollTop + 'px';
    frame.style.width = body.clientWidth + 'px';
    frame = frame.firstChild;
    frame.style.height = body.clientHeight + 'px';
  },

  localize: function (message) {
    return MathJax.Localization._(message,message);
  },

  filterText: function (text,n,id) {
    if (MathJax.Hub.config.messageStyle === "simple") {
      if (id === "LoadFile") {
        if (!this.loading) {this.loading = this.localize("Loading") + " "}
        text = this.loading; this.loading += ".";
      } else if (id === "ProcessMath") {
        if (!this.processing) {this.processing = this.localize("Processing") + " "}
        text = this.processing; this.processing += ".";
      } else if (id === "TypesetMath") {
        if (!this.typesetting) {this.typesetting = this.localize("Typesetting") + " "}
        text = this.typesetting; this.typesetting += ".";
      }
    }
    return text;
  },

  clearCounts: function () {
    delete this.loading;
    delete this.processing;
    delete this.typesetting;
  },

  Set: function (text,n,clearDelay) {
    if (n == null) {n = this.log.length; this.log[n] = {}}
    //
    //  Translate message if it is [id,message,arguments]
    //
    var id = "";
    if (MathJax.Object.isArray(text)) {
      id = text[0]; if (MathJax.Object.isArray(id)) {id = id[1]}
      //
      // Localization._() will throw a restart error if a localization file
      //   needs to be loaded, so trap that and redo the Set() call
      //   after it is loaded.
      //
      try {
        text = MathJax.Localization._.apply(MathJax.Localization,text);
      } catch (err) {
        if (!err.restart) {throw err}
        if (!err.restart.called) {
          //
          //  Mark it so we can tell if the Clear() comes before the message is displayed
          //
          if (this.log[n].restarted == null) {this.log[n].restarted = 0}
          this.log[n].restarted++; delete this.log[n].cleared;
          MathJax.Callback.After(["Set",this,text,n,clearDelay],err.restart);
          return n;
        }
      }
    }
    //
    // Clear the timout timer.
    //
    if (this.timer) {clearTimeout(this.timer); delete this.timer}
    //
    //  Save the message and filtered message.
    //
    this.log[n].text = text; this.log[n].filteredText = text = this.filterText(text,n,id);
    //
    //  Hook the message into the message list so we can tell
    //   what message to put up when this one is removed.
    //
    if (typeof(this.log[n].next) === "undefined") {
      this.log[n].next = this.current;
      if (this.current != null) {this.log[this.current].prev = n}
      this.current = n;
    }
    //
    //  Show the message if it is the currently active one.
    //
    if (this.current === n && MathJax.Hub.config.messageStyle !== "none") {
      if (this.Init()) {
        if (this.textNodeBug) {this.div.innerHTML = text} else {this.text.nodeValue = text}
        this.div.style.display = "";
        if (this.status) {window.status = ""; delete this.status}
      } else {
        window.status = text;
        this.status = true;
      }
    }
    //
    //  Check if the message was resetarted to load a localization file
    //    and if it has been cleared in the meanwhile.
    //
    if (this.log[n].restarted) {
      if (this.log[n].cleared) {clearDelay = 0}
      if (--this.log[n].restarted === 0) {delete this.log[n].cleared}
    }
    //
    //  Check if we need to clear the message automatically.
    //
    if (clearDelay) {setTimeout(MathJax.Callback(["Clear",this,n]),clearDelay)}
      else if (clearDelay == 0) {this.Clear(n,0)}
    //
    //  Return the message number.
    //
    return n;
  },

  Clear: function (n,delay) {
    //
    //  Detatch the message from the active list.
    //
    if (this.log[n].prev != null) {this.log[this.log[n].prev].next = this.log[n].next}
    if (this.log[n].next != null) {this.log[this.log[n].next].prev = this.log[n].prev}
    //
    //  If it is the current message, get the next one to show.
    //
    if (this.current === n) {
      this.current = this.log[n].next;
      if (this.text) {
        if (this.div.parentNode == null) {this.Init()} // see ASCIIMathML comments above
        if (this.current == null) {
          //
          //  If there are no more messages, remove the message box.
          //
          if (this.timer) {clearTimeout(this.timer); delete this.timer}
          if (delay == null) {delay = 600}
          if (delay === 0) {this.Remove()}
	    else {this.timer = setTimeout(MathJax.Callback(["Remove",this]),delay)}
        } else if (MathJax.Hub.config.messageStyle !== "none") {
          //
          //  If there is an old message, put it in place
          //
          if (this.textNodeBug) {this.div.innerHTML = this.log[this.current].filteredText}
                           else {this.text.nodeValue = this.log[this.current].filteredText}
        }
        if (this.status) {window.status = ""; delete this.status}
      } else if (this.status) {
        window.status = (this.current == null ? "" : this.log[this.current].text);
      }
    }
    //
    //  Clean up the log data no longer needed
    //
    delete this.log[n].next; delete this.log[n].prev;
    delete this.log[n].filteredText;
    //
    //  If this is a restarted localization message, mark that it has been cleared
    //    while waiting for the file to load.
    //
    if (this.log[n].restarted) {this.log[n].cleared = true}
  },

  Remove: function () {
    // FIXME:  do a fade out or something else interesting?
    this.text.nodeValue = "";
    this.div.style.display = "none";
  },

  File: function (file) {
    return this.Set(["LoadFile","Loading %1",file],null,null);
  },

  Log: function () {
    var strings = [];
    for (var i = 1, m = this.log.length; i < m; i++) {strings[i] = this.log[i].text}
    return strings.join("\n");
  }

};

/**********************************************************/

MathJax.Hub = {
  config: {
    root: "",
    config: [],      // list of configuration files to load
    styleSheets: [], // list of CSS files to load
    styles: {        // styles to generate in-line
      ".MathJax_Preview": {color: "#888"}
    },
    jax: [],         // list of input and output jax to load
    extensions: [],  // list of extensions to load
    preJax: null,    // pattern to remove from before math script tag
    postJax: null,   // pattern to remove from after math script tag
    displayAlign: 'center',       // how to align displayed equations (left, center, right)
    displayIndent: '0',           // indentation for displayed equations (when not centered)
    preRemoveClass: 'MathJax_Preview', // class of objects to remove preceeding math script
    showProcessingMessages: true, // display "Processing math: nn%" messages or not
    messageStyle: "normal",       // set to "none" or "simple" (for "Loading..." and "Processing...")
    delayStartupUntil: "none",    // set to "onload" to delay setup until the onload handler runs
                                  // set to "configured" to delay startup until MathJax.Hub.Configured() is called
                                  // set to a Callback to wait for before continuing with the startup
    skipStartupTypeset: false,    // set to true to skip PreProcess and Process during startup
    elements: [],             // array of elements to process when none is given explicitly
    positionToHash: true,    // after initial typeset pass, position to #hash location?

    showMathMenu: true,      // attach math context menu to typeset math?
    showMathMenuMSIE: true,  // separtely determine if MSIE should have math menu
                             //  (since the code for that is a bit delicate)

    menuSettings: {
      zoom: "None",        //  when to do MathZoom
      CTRL: false,         //    require CTRL for MathZoom?
      ALT: false,          //    require Alt or Option?
      CMD: false,          //    require CMD?
      Shift: false,        //    require Shift?
      discoverable: false, //  make math menu discoverable on hover?
      zscale: "200%",      //  the scaling factor for MathZoom
      renderer: null,      //  set when Jax are loaded
      font: "Auto",        //  what font HTML-CSS should use
      context: "MathJax",  //  or "Browser" for pass-through to browser menu
      locale: null,        //  the language to use for messages
      mpContext: false,    //  true means pass menu events to MathPlayer in IE
      mpMouse: false,      //  true means pass mouse events to MathPlayer in IE
      texHints: true,      //  include class names for TeXAtom elements
      FastPreview: null,   //  use PreviewHTML output as preview?
      assistiveMML: null,  //  include hidden MathML for screen readers?
      inTabOrder: true,    //  set to false if math elements should be included in the tabindex
      semantics: false     //  add semantics tag with original form in MathML output
    },

    errorSettings: {
       // localized HTML snippet structure for message to use
      message: ["[",["MathProcessingError","Math Processing Error"],"]"],
      style: {color: "#CC0000", "font-style":"italic"}  // style for message
    },

    ignoreMMLattributes: {}  // attributes not to copy to HTML-CSS or SVG output
                             //   from MathML input (in addition to the ones in MML.nocopyAttributes).
                             //   An id set to true will be ignored, one set to false will
                             //   be allowed (even if other criteria normally would prevent
                             //   it from being copied); use false carefully!
  },

  preProcessors: MathJax.Callback.Hooks(true), // list of callbacks for preprocessing (initialized by extensions)
  inputJax: {},          // mime-type mapped to input jax (by registration)
  outputJax: {order:{}}, // mime-type mapped to output jax list (by registration)

  processSectionDelay: 50, // pause between input and output phases of processing
  processUpdateTime: 250, // time between screen updates when processing math (milliseconds)
  processUpdateDelay: 10, // pause between screen updates to allow other processing (milliseconds)

  signal: MathJax.Callback.Signal("Hub"), // Signal used for Hub events

  Config: function (def) {
    this.Insert(this.config,def);
    if (this.config.Augment) {this.Augment(this.config.Augment)}
  },
  CombineConfig: function (name,def) {
    var config = this.config, id, parent; name = name.split(/\./);
    for (var i = 0, m = name.length; i < m; i++) {
      id = name[i]; if (!config[id]) {config[id] = {}}
      parent = config; config = config[id];
    }
    parent[id] = config = this.Insert(def,config);
    return config;
  },

  Register: {
    PreProcessor: function () {return MathJax.Hub.preProcessors.Add.apply(MathJax.Hub.preProcessors,arguments)},
    MessageHook: function () {return MathJax.Hub.signal.MessageHook.apply(MathJax.Hub.signal,arguments)},
    StartupHook: function () {return MathJax.Hub.Startup.signal.MessageHook.apply(MathJax.Hub.Startup.signal,arguments)},
    LoadHook: function () {return MathJax.Ajax.LoadHook.apply(MathJax.Ajax,arguments)}
  },
  UnRegister: {
    PreProcessor: function (hook) {MathJax.Hub.preProcessors.Remove(hook)},
    MessageHook: function (hook) {MathJax.Hub.signal.RemoveHook(hook)},
    StartupHook: function (hook) {MathJax.Hub.Startup.signal.RemoveHook(hook)},
    LoadHook: function (hook) {MathJax.Ajax.removeHook(hook)}
  },

  getAllJax: function (element) {
    var jax = [], scripts = this.elementScripts(element);
    for (var i = 0, m = scripts.length; i < m; i++) {
      if (scripts[i].MathJax && scripts[i].MathJax.elementJax)
        {jax.push(scripts[i].MathJax.elementJax)}
    }
    return jax;
  },

  getJaxByType: function (type,element) {
    var jax = [], scripts = this.elementScripts(element);
    for (var i = 0, m = scripts.length; i < m; i++) {
      if (scripts[i].MathJax && scripts[i].MathJax.elementJax &&
          scripts[i].MathJax.elementJax.mimeType === type)
            {jax.push(scripts[i].MathJax.elementJax)}
    }
    return jax;
  },

  getJaxByInputType: function (type,element) {
    var jax = [], scripts = this.elementScripts(element);
    for (var i = 0, m = scripts.length; i < m; i++) {
      if (scripts[i].MathJax && scripts[i].MathJax.elementJax &&
          scripts[i].type && scripts[i].type.replace(/ *;(.|\s)*/,"") === type)
        {jax.push(scripts[i].MathJax.elementJax)}
    }
    return jax;
  },

  getJaxFor: function (element) {
    if (typeof(element) === 'string') {element = document.getElementById(element)}
    if (element && element.MathJax) {return element.MathJax.elementJax}
    if (this.isMathJaxNode(element)) {
      if (!element.isMathJax) {element = element.firstChild}  // for NativeMML output
      while (element && !element.jaxID) {element = element.parentNode}
      if (element) {return MathJax.OutputJax[element.jaxID].getJaxFromMath(element)}
    }
    return null;
  },

  isJax: function (element) {
    if (typeof(element) === 'string') {element = document.getElementById(element)}
    if (this.isMathJaxNode(element)) {return 1}
    if (element && (element.tagName||"").toLowerCase() === 'script') {
      if (element.MathJax)
        {return (element.MathJax.state === MathJax.ElementJax.STATE.PROCESSED ? 1 : -1)}
      if (element.type && this.inputJax[element.type.replace(/ *;(.|\s)*/,"")]) {return -1}
    }
    return 0;
  },
  isMathJaxNode: function (element) {
    return !!element && (element.isMathJax || (element.className||"") === "MathJax_MathML");
  },

  setRenderer: function (renderer,type) {
    if (!renderer) return;
    if (!MathJax.OutputJax[renderer]) {
      this.config.menuSettings.renderer = "";
      var file = "[MathJax]/jax/output/"+renderer+"/config.js";
      return MathJax.Ajax.Require(file,["setRenderer",this,renderer,type]);
    } else {
      this.config.menuSettings.renderer = renderer;
      if (type == null) {type = "jax/mml"}
      var jax = this.outputJax;
      if (jax[type] && jax[type].length) {
        if (renderer !== jax[type][0].id) {
          jax[type].unshift(MathJax.OutputJax[renderer]);
          return this.signal.Post(["Renderer Selected",renderer]);
        }
      }
      return null;
    }
  },

  Queue: function () {
    return this.queue.Push.apply(this.queue,arguments);
  },

  Typeset: function (element,callback) {
    if (!MathJax.isReady) return null;
    var ec = this.elementCallback(element,callback);
    if (ec.count) {
      var queue = MathJax.Callback.Queue(
        ["PreProcess",this,ec.elements],
        ["Process",this,ec.elements]
      );
    }
    return queue.Push(ec.callback);
  },

  PreProcess: function (element,callback) {
    var ec = this.elementCallback(element,callback);
    var queue = MathJax.Callback.Queue();
    if (ec.count) {
      var elements = (ec.count === 1 ? [ec.elements] : ec.elements);
      queue.Push(["Post",this.signal,["Begin PreProcess",ec.elements]]);
      for (var i = 0, m = elements.length; i < m; i++) {
        if (elements[i]) {queue.Push(["Execute",this.preProcessors,elements[i]])}
      }
      queue.Push(["Post",this.signal,["End PreProcess",ec.elements]]);
    }
    return queue.Push(ec.callback);
  },

  Process:   function (element,callback) {return this.takeAction("Process",element,callback)},
  Update:    function (element,callback) {return this.takeAction("Update",element,callback)},
  Reprocess: function (element,callback) {return this.takeAction("Reprocess",element,callback)},
  Rerender:  function (element,callback) {return this.takeAction("Rerender",element,callback)},

  takeAction: function (action,element,callback) {
    var ec = this.elementCallback(element,callback);
    var elements = ec.elements;
    var queue = MathJax.Callback.Queue(["Clear",this.signal]);
    var state = {
      scripts: [],                  // filled in by prepareScripts
      start: new Date().getTime(),  // timer for processing messages
      i: 0, j: 0,                   // current script, current jax
      jax: {},                      // scripts grouped by output jax
      jaxIDs: []                    // id's of jax used
    };
    if (ec.count) {
      var delay = ["Delay",MathJax.Callback,this.processSectionDelay];
      if (!delay[2]) {delay = {}}
      queue.Push(
        ["clearCounts",MathJax.Message],
        ["Post",this.signal,["Begin "+action,elements]],
        ["Post",this.signal,["Begin Math",elements,action]],
        ["prepareScripts",this,action,elements,state],
        ["Post",this.signal,["Begin Math Input",elements,action]],
        ["processInput",this,state],
        ["Post",this.signal,["End Math Input",elements,action]],
        delay,
        ["prepareOutput",this,state,"preProcess"],
        delay,
        ["Post",this.signal,["Begin Math Output",elements,action]],
        ["processOutput",this,state],
        ["Post",this.signal,["End Math Output",elements,action]],
        delay,
        ["prepareOutput",this,state,"postProcess"],
        delay,
        ["Post",this.signal,["End Math",elements,action]],
        ["Post",this.signal,["End "+action,elements]],
        ["clearCounts",MathJax.Message]
      );
    }
    return queue.Push(ec.callback);
  },

  scriptAction: {
    Process: function (script) {},
    Update: function (script) {
      var jax = script.MathJax.elementJax;
      if (jax && jax.needsUpdate()) {jax.Remove(true); script.MathJax.state = jax.STATE.UPDATE}
        else {script.MathJax.state = jax.STATE.PROCESSED}
    },
    Reprocess: function (script) {
      var jax = script.MathJax.elementJax;
      if (jax) {jax.Remove(true); script.MathJax.state = jax.STATE.UPDATE}
    },
    Rerender: function (script) {
      var jax = script.MathJax.elementJax;
      if (jax) {jax.Remove(true); script.MathJax.state = jax.STATE.OUTPUT}
    }
  },

  prepareScripts: function (action,element,state) {
    if (arguments.callee.disabled) return;
    var scripts = this.elementScripts(element);
    var STATE = MathJax.ElementJax.STATE;
    for (var i = 0, m = scripts.length; i < m; i++) {
      var script = scripts[i];
      if (script.type && this.inputJax[script.type.replace(/ *;(.|\n)*/,"")]) {
        if (script.MathJax) {
          if (script.MathJax.elementJax && script.MathJax.elementJax.hover) {
            MathJax.Extension.MathEvents.Hover.ClearHover(script.MathJax.elementJax);
          }
          if (script.MathJax.state !== STATE.PENDING) {this.scriptAction[action](script)}
        }
        if (!script.MathJax) {script.MathJax = {state: STATE.PENDING}}
        if (script.MathJax.error) delete script.MathJax.error;
        if (script.MathJax.state !== STATE.PROCESSED) {state.scripts.push(script)}
      }
    }
  },

  checkScriptSiblings: function (script) {
    if (script.MathJax.checked) return;
    var config = this.config, pre = script.previousSibling;
    if (pre && pre.nodeName === "#text") {
      var preJax,postJax, post = script.nextSibling;
      if (post && post.nodeName !== "#text") {post = null}
      if (config.preJax) {
        if (typeof(config.preJax) === "string") {config.preJax = new RegExp(config.preJax+"$")}
        preJax = pre.nodeValue.match(config.preJax);
      }
      if (config.postJax && post) {
        if (typeof(config.postJax) === "string") {config.postJax = new RegExp("^"+config.postJax)}
        postJax = post.nodeValue.match(config.postJax);
      }
      if (preJax && (!config.postJax || postJax)) {
        pre.nodeValue  = pre.nodeValue.replace
          (config.preJax,(preJax.length > 1? preJax[1] : ""));
        pre = null;
      }
      if (postJax && (!config.preJax || preJax)) {
        post.nodeValue = post.nodeValue.replace
          (config.postJax,(postJax.length > 1? postJax[1] : ""));
      }
      if (pre && !pre.nodeValue.match(/\S/)) {pre = pre.previousSibling}
    }
    if (config.preRemoveClass && pre && pre.className === config.preRemoveClass)
      {script.MathJax.preview = pre}
    script.MathJax.checked = 1;
  },

  processInput: function (state) {
    var jax, STATE = MathJax.ElementJax.STATE;
    var script, prev, m = state.scripts.length;
    try {
      //
      //  Loop through the scripts
      //
      while (state.i < m) {
        script = state.scripts[state.i]; if (!script) {state.i++; continue}
        //
        //  Remove previous error marker, if any
        //
        prev = script.previousSibling;
        if (prev && prev.className === "MathJax_Error") {prev.parentNode.removeChild(prev)}
        //
        //  Check if already processed or needs processing
        //
        if (!script.parentNode || !script.MathJax || script.MathJax.state === STATE.PROCESSED) {state.i++; continue};
        if (!script.MathJax.elementJax || script.MathJax.state === STATE.UPDATE) {
          this.checkScriptSiblings(script);                 // remove preJax/postJax etc.
          var type = script.type.replace(/ *;(.|\s)*/,"");  // the input jax type
          var input = this.inputJax[type];                  // the input jax itself
          jax = input.Process(script,state);                // run the input jax
          if (typeof jax === 'function') {                  // if a callback was returned
            if (jax.called) continue;                       //   go back and call Process() again
            this.RestartAfter(jax);                         //   wait for the callback
          }
          jax = jax.Attach(script,input.id);                // register the jax on the script
          this.saveScript(jax,state,script,STATE);          // add script to state
          this.postInputHooks.Execute(jax,input.id,script); // run global jax filters
        } else if (script.MathJax.state === STATE.OUTPUT) {
          this.saveScript(script.MathJax.elementJax,state,script,STATE); // add script to state
        }
        //
        //  Go on to the next script, and check if we need to update the processing message
        //
        state.i++; var now = new Date().getTime();
        if (now - state.start > this.processUpdateTime && state.i < state.scripts.length)
          {state.start = now; this.RestartAfter(MathJax.Callback.Delay(1))}
      }
    } catch (err) {return this.processError(err,state,"Input")}
    //
    //  Put up final message, reset the state and return
    //
    if (state.scripts.length && this.config.showProcessingMessages)
      {MathJax.Message.Set(["ProcessMath","Processing math: %1%%",100],0)}
    state.start = new Date().getTime(); state.i = state.j = 0;
    return null;
  },
  postInputHooks: MathJax.Callback.Hooks(true),  // hooks to run after element jax is created
  saveScript: function (jax,state,script,STATE) {
    //
    //  Check that output jax exists
    //
    if (!this.outputJax[jax.mimeType]) {
      script.MathJax.state = STATE.UPDATE;
      throw Error("No output jax registered for "+jax.mimeType);
    }
    //
    //  Record the output jax
    //  and put this script in the queue for that jax
    //
    jax.outputJax = this.outputJax[jax.mimeType][0].id;
    if (!state.jax[jax.outputJax]) {
      if (state.jaxIDs.length === 0) {
        // use original array until we know there are more (rather than two copies)
        state.jax[jax.outputJax] = state.scripts;
      } else {
        if (state.jaxIDs.length === 1) // get the script so far for the existing jax
          {state.jax[state.jaxIDs[0]] = state.scripts.slice(0,state.i)}
        state.jax[jax.outputJax] = []; // start a new array for the new jax
      }
      state.jaxIDs.push(jax.outputJax); // save the ID of the jax
    }
    if (state.jaxIDs.length > 1) {state.jax[jax.outputJax].push(script)}
    //
    //  Mark script as needing output
    //
    script.MathJax.state = STATE.OUTPUT;
  },

  //
  //  Pre- and post-process scripts by jax
  //    (to get scaling factors, hide/show output, and so on)
  //  Since this can cause the jax to load, we need to trap restarts
  //
  prepareOutput: function (state,method) {
    while (state.j < state.jaxIDs.length) {
      var id = state.jaxIDs[state.j], JAX = MathJax.OutputJax[id];
      if (JAX[method]) {
        try {
          var result = JAX[method](state);
          if (typeof result === 'function') {
            if (result.called) continue;  // go back and try again
            this.RestartAfter(result);
          }
        } catch (err) {
          if (!err.restart) {
            MathJax.Message.Set(["PrepError","Error preparing %1 output (%2)",id,method],null,600);
            MathJax.Hub.lastPrepError = err;
            state.j++;
          }
          return MathJax.Callback.After(["prepareOutput",this,state,method],err.restart);
        }
      }
      state.j++;
    }
    return null;
  },

  processOutput: function (state) {
    var result, STATE = MathJax.ElementJax.STATE, script, m = state.scripts.length;
    try {
      //
      //  Loop through the scripts
      //
      while (state.i < m) {
        //
        //  Check that there is an element jax
        //
        script = state.scripts[state.i];
        if (!script || !script.parentNode || !script.MathJax || script.MathJax.error) {state.i++; continue}
        var jax = script.MathJax.elementJax; if (!jax) {state.i++; continue}
        //
        //  Call the output Jax's Process method (which will be its Translate()
        //  method once loaded).  Mark it as complete and remove the preview unless
        //  the Process() call returns an explicit false value (in which case, it will
        //  handle this later during the postProcess phase, as HTML-CSS does).
        //
        result = MathJax.OutputJax[jax.outputJax].Process(script,state);
        if (result !== false) {
          script.MathJax.state = STATE.PROCESSED;
          if (script.MathJax.preview) {
            script.MathJax.preview.innerHTML = "";
            script.MathJax.preview.style.display = "none";
          }
          //
          //  Signal that new math is available
          //
          this.signal.Post(["New Math",jax.inputID]); // FIXME: wait for this?  (i.e., restart if returns uncalled callback)
        }
        //
        //  Go on to next math expression
        //
        state.i++;
        //
        //  Update the processing message, if needed
        //
        var now = new Date().getTime();
        if (now - state.start > this.processUpdateTime && state.i < state.scripts.length)
          {state.start = now; this.RestartAfter(MathJax.Callback.Delay(this.processUpdateDelay))}
      }
    } catch (err) {return this.processError(err,state,"Output")}
    //
    //  Put up the typesetting-complete message
    //
    if (state.scripts.length && this.config.showProcessingMessages) {
      MathJax.Message.Set(["TypesetMath","Typesetting math: %1%%",100],0);
      MathJax.Message.Clear(0);
    }
    state.i = state.j = 0;
    return null;
  },

  processMessage: function (state,type) {
    var m = Math.floor(state.i/(state.scripts.length)*100);
    var message = (type === "Output" ? ["TypesetMath","Typesetting math: %1%%"] :
                                       ["ProcessMath","Processing math: %1%%"]);
    if (this.config.showProcessingMessages) {MathJax.Message.Set(message.concat(m),0)}
  },

  processError: function (err,state,type) {
    if (!err.restart) {
      if (!this.config.errorSettings.message) {throw err}
      this.formatError(state.scripts[state.i],err); state.i++;
    }
    this.processMessage(state,type);
    return MathJax.Callback.After(["process"+type,this,state],err.restart);
  },

  formatError: function (script,err) {
    var LOCALIZE = function (id,text,arg1,arg2) {return MathJax.Localization._(id,text,arg1,arg2)};
    //
    //  Get the error message, URL, and line, and save it for
    //    reporting in the Show Math As Error menu
    //
    var message = LOCALIZE("ErrorMessage","Error: %1",err.message)+"\n";
    if (err.sourceURL||err.fileName) message += "\n"+LOCALIZE("ErrorFile","file: %1",err.sourceURL||err.fileName);
    if (err.line||err.lineNumber) message += "\n"+LOCALIZE("ErrorLine","line: %1",err.line||err.lineNumber);
    message += "\n\n"+LOCALIZE("ErrorTips","Debugging tips: use %1, inspect %2 in the browser console","'unpacked/MathJax.js'","'MathJax.Hub.lastError'");
    script.MathJax.error = MathJax.OutputJax.Error.Jax(message,script);
    if (script.MathJax.elementJax)
      script.MathJax.error.inputID = script.MathJax.elementJax.inputID;
    //
    //  Create the [Math Processing Error] span
    //
    var errorSettings = this.config.errorSettings;
    var errorText = LOCALIZE(errorSettings.messageId,errorSettings.message);
    var error = MathJax.HTML.Element("span", {
      className:"MathJax_Error", jaxID:"Error", isMathJax:true,
      id: script.MathJax.error.inputID+"-Frame"
    },[["span",null,errorText]]);
    //
    //  Attach the menu events
    //
    MathJax.Ajax.Require("[MathJax]/extensions/MathEvents.js",function () {
      var EVENT = MathJax.Extension.MathEvents.Event,
          HUB = MathJax.Hub;
      error.oncontextmenu = EVENT.Menu;
      error.onmousedown = EVENT.Mousedown;
      error.onkeydown = EVENT.Keydown;
      error.tabIndex = HUB.getTabOrder(HUB.getJaxFor(script));
    });
    //
    //  Insert the error into the page and remove any preview
    //
    var node = document.getElementById(error.id);
    if (node) node.parentNode.removeChild(node);
    if (script.parentNode) script.parentNode.insertBefore(error,script);
    if (script.MathJax.preview) {
      script.MathJax.preview.innerHTML = "";
      script.MathJax.preview.style.display = "none";
    }
    //
    //  Save the error for debugging purposes
    //  Report the error as a signal
    //
    this.lastError = err;
    this.signal.Post(["Math Processing Error",script,err]);
  },

  RestartAfter: function (callback) {
    throw this.Insert(Error("restart"),{restart: MathJax.Callback(callback)});
  },

  elementCallback: function (element,callback) {
    if (callback == null && (MathJax.Object.isArray(element) || typeof element === 'function'))
      {try {MathJax.Callback(element); callback = element; element = null} catch(e) {}}
    if (element == null) {element = this.config.elements || []}
    if (this.isHTMLCollection(element)) {element = this.HTMLCollection2Array(element)}
    if (!MathJax.Object.isArray(element)) {element = [element]}
    element = [].concat(element); // make a copy so the original isn't changed
    for (var i = 0, m = element.length; i < m; i++)
      {if (typeof(element[i]) === 'string') {element[i] = document.getElementById(element[i])}}
    if (!document.body) {document.body = document.getElementsByTagName("body")[0]}
    if (element.length == 0) {element.push(document.body)}
    if (!callback) {callback = {}}
    return {
      count: element.length,
      elements: (element.length === 1 ? element[0] : element),
      callback: callback
    };
  },

  elementScripts: function (element) {
    var scripts = [];
    if (MathJax.Object.isArray(element) || this.isHTMLCollection(element)) {
      for (var i = 0, m = element.length; i < m; i++) {
        var alreadyDone = 0;
        for (var j = 0; j < i && !alreadyDone; j++)
          {alreadyDone = element[j].contains(element[i])}
        if (!alreadyDone) scripts.push.apply(scripts,this.elementScripts(element[i]));
      }
      return scripts;
    }
    if (typeof(element) === 'string') {element = document.getElementById(element)}
    if (!document.body) {document.body = document.getElementsByTagName("body")[0]}
    if (element == null) {element = document.body}
    if (element.tagName != null && element.tagName.toLowerCase() === "script") {return [element]}
    scripts = element.getElementsByTagName("script");
    if (this.msieHTMLCollectionBug) {scripts = this.HTMLCollection2Array(scripts)}
    return scripts;
  },

  //
  //  IE8 fails to check "obj instanceof HTMLCollection" for some values of obj.
  //
  isHTMLCollection: function (obj) {
    return ("HTMLCollection" in window && typeof(obj) === "object" && obj instanceof HTMLCollection);
  },
  //
  //  IE8 doesn't deal with HTMLCollection as an array, so convert to array
  //
  HTMLCollection2Array: function (nodes) {
    if (!this.msieHTMLCollectionBug) {return [].slice.call(nodes)}
    var NODES = [];
    for (var i = 0, m = nodes.length; i < m; i++) {NODES[i] = nodes[i]}
    return NODES;
  },

  Insert: function (dst,src) {
    for (var id in src) {if (src.hasOwnProperty(id)) {
      // allow for concatenation of arrays?
      if (typeof src[id] === 'object' && !(MathJax.Object.isArray(src[id])) &&
         (typeof dst[id] === 'object' || typeof dst[id] === 'function')) {
        this.Insert(dst[id],src[id]);
      } else {
        dst[id] = src[id];
      }
    }}
    return dst;
  },

  getTabOrder: function(script) {
    return this.config.menuSettings.inTabOrder ? 0 : -1;
  },

  // Old browsers (e.g. Internet Explorer <= 8) do not support trim().
  SplitList: ("trim" in String.prototype ?
              function (list) {return list.trim().split(/\s+/)} :
              function (list) {return list.replace(/^\s+/,'').
                                           replace(/\s+$/,'').split(/\s+/)})
};
MathJax.Hub.Insert(MathJax.Hub.config.styles,MathJax.Message.styles);
MathJax.Hub.Insert(MathJax.Hub.config.styles,{".MathJax_Error":MathJax.Hub.config.errorSettings.style});

//
//  Storage area for extensions and preprocessors
//
MathJax.Extension = {};

//
//  Hub Startup code
//
MathJax.Hub.Configured = MathJax.Callback({}); // called when configuration is complete
MathJax.Hub.Startup = {
  script: "", // the startup script from the SCRIPT call that loads MathJax.js
  queue:   MathJax.Callback.Queue(),           // Queue used for startup actions
  signal:  MathJax.Callback.Signal("Startup"), // Signal used for startup events
  params:  {},

  //
  //  Load the configuration files
  //
  Config: function () {
    this.queue.Push(["Post",this.signal,"Begin Config"]);
    //
    //  Make sure root is set before loading any files
    //
    if (MathJax.AuthorConfig && MathJax.AuthorConfig.root)
      MathJax.Ajax.config.root = MathJax.AuthorConfig.root;
    //
    //  If a locale is given as a parameter,
    //    set the locale and the default menu value for the locale
    //
    if (this.params.locale) {
      MathJax.Localization.resetLocale(this.params.locale);
      MathJax.Hub.config.menuSettings.locale = this.params.locale;
    }
    //
    //  Run the config files, if any are given in the parameter list
    //
    if (this.params.config) {
      var files = this.params.config.split(/,/);
      for (var i = 0, m = files.length; i < m; i++) {
        if (!files[i].match(/\.js$/)) {files[i] += ".js"}
        this.queue.Push(["Require",MathJax.Ajax,this.URL("config",files[i])]);
      }
    }
    //
    //  Perform author configuration from in-line MathJax = {...}
    //
    this.queue.Push(["Config",MathJax.Hub,MathJax.AuthorConfig]);
    //
    //  Run the deprecated configuration script, if any (ignoring return value)
    //  Wait for the startup delay signal
    //  Run the mathjax-config blocks
    //  Load the files in the configuration's config array
    //
    if (this.script.match(/\S/)) {this.queue.Push(this.script+";\n1;")}
    this.queue.Push(
      ["ConfigDelay",this],
      ["ConfigBlocks",this],
      [function (THIS) {return THIS.loadArray(MathJax.Hub.config.config,"config",null,true)},this],
      ["Post",this.signal,"End Config"]
    );
  },
  //
  //  Return the delay callback
  //
  ConfigDelay: function () {
    var delay = this.params.delayStartupUntil || MathJax.Hub.config.delayStartupUntil;
    if (delay === "onload") {return this.onload}
    if (delay === "configured") {return MathJax.Hub.Configured}
    return delay;
  },
  //
  //  Run the scripts of type=text/x-mathjax-config
  //
  ConfigBlocks: function () {
    var scripts = document.getElementsByTagName("script");
    var queue = MathJax.Callback.Queue();
    for (var i = 0, m = scripts.length; i < m; i++) {
      var type = String(scripts[i].type).replace(/ /g,"");
      if (type.match(/^text\/x-mathjax-config(;.*)?$/) && !type.match(/;executed=true/)) {
        scripts[i].type += ";executed=true";
        queue.Push(scripts[i].innerHTML+";\n1;");
      }
    }
    return queue.Push(function () {MathJax.Ajax.config.root = MathJax.Hub.config.root});
  },

  //
  //  Read cookie and set up menu defaults
  //  (set the locale according to the cookie)
  //  (adjust the jax to accommodate renderer preferences)
  //
  Cookie: function () {
    return this.queue.Push(
      ["Post",this.signal,"Begin Cookie"],
      ["Get",MathJax.HTML.Cookie,"menu",MathJax.Hub.config.menuSettings],
      [function (config) {
        var SETTINGS = config.menuSettings;
        if (SETTINGS.locale) MathJax.Localization.resetLocale(SETTINGS.locale);
        var renderer = config.menuSettings.renderer, jax = config.jax;
        if (renderer) {
          var name = "output/"+renderer; jax.sort();
          for (var i = 0, m = jax.length; i < m; i++) {
            if (jax[i].substr(0,7) === "output/") break;
          }
          if (i == m-1) {jax.pop()} else {
            while (i < m) {if (jax[i] === name) {jax.splice(i,1); break}; i++}
          }
          jax.unshift(name);
        }
        if (SETTINGS.CHTMLpreview != null) {
          if (SETTINGS.FastPreview == null) SETTINGS.FastPreview = SETTINGS.CHTMLpreview;
          delete SETTINGS.CHTMLpreview;
        }
        if (SETTINGS.FastPreview && !MathJax.Extension["fast-preview"])
          MathJax.Hub.config.extensions.push("fast-preview.js");
        if (config.menuSettings.assistiveMML && !MathJax.Extension.AssistiveMML)
          MathJax.Hub.config.extensions.push("AssistiveMML.js");
      },MathJax.Hub.config],
      ["Post",this.signal,"End Cookie"]
    );
  },
  //
  //  Setup stylesheets and extra styles
  //
  Styles: function () {
    return this.queue.Push(
      ["Post",this.signal,"Begin Styles"],
      ["loadArray",this,MathJax.Hub.config.styleSheets,"config"],
      ["Styles",MathJax.Ajax,MathJax.Hub.config.styles],
      ["Post",this.signal,"End Styles"]
    );
  },
  //
  //  Load the input and output jax
  //
  Jax: function () {
    var config = MathJax.Hub.config, jax = MathJax.Hub.outputJax;
    //  Save the order of the output jax since they are loading asynchronously
    for (var i = 0, m = config.jax.length, k = 0; i < m; i++) {
      var name = config.jax[i].substr(7);
      if (config.jax[i].substr(0,7) === "output/" && jax.order[name] == null)
        {jax.order[name] = k; k++}
    }
    var queue = MathJax.Callback.Queue();
    return queue.Push(
      ["Post",this.signal,"Begin Jax"],
      ["loadArray",this,config.jax,"jax","config.js"],
      ["Post",this.signal,"End Jax"]
    );
  },
  //
  //  Load the extensions
  //
  Extensions: function () {
    var queue = MathJax.Callback.Queue();
    return queue.Push(
      ["Post",this.signal,"Begin Extensions"],
      ["loadArray",this,MathJax.Hub.config.extensions,"extensions"],
      ["Post",this.signal,"End Extensions"]
    );
  },

  //
  //  Initialize the Message system
  //
  Message: function () {
    MathJax.Message.Init(true);
  },

  //
  //  Set the math menu renderer, if it isn't already
  //  (this must come after the jax are loaded)
  //
  Menu: function () {
    var menu = MathJax.Hub.config.menuSettings, jax = MathJax.Hub.outputJax, registered;
    for (var id in jax) {if (jax.hasOwnProperty(id)) {
      if (jax[id].length) {registered = jax[id]; break}
    }}
    if (registered && registered.length) {
      if (menu.renderer && menu.renderer !== registered[0].id)
        {registered.unshift(MathJax.OutputJax[menu.renderer])}
      menu.renderer = registered[0].id;
    }
  },

  //
  //  Set the location to the designated hash position
  //
  Hash: function () {
    if (MathJax.Hub.config.positionToHash && document.location.hash &&
        document.body && document.body.scrollIntoView) {
      var name = document.location.hash.substr(1);
      var target = document.getElementById(name);
      if (!target) {
        var a = document.getElementsByTagName("a");
        for (var i = 0, m = a.length; i < m; i++)
          {if (a[i].name === name) {target = a[i]; break}}
      }
      if (target) {
        while (!target.scrollIntoView) {target = target.parentNode}
        target = this.HashCheck(target);
        if (target && target.scrollIntoView)
          {setTimeout(function () {target.scrollIntoView(true)},1)}
      }
    }
  },
  HashCheck: function (target) {
    var jax = MathJax.Hub.getJaxFor(target);
    if (jax && MathJax.OutputJax[jax.outputJax].hashCheck)
      {target = MathJax.OutputJax[jax.outputJax].hashCheck(target)}
    return target;
  },

  //
  //  Load the Menu and Zoom code, if it hasn't already been loaded.
  //  This is called after the initial typeset, so should no longer be
  //  competing with other page loads, but will make these available
  //  if needed later on.
  //
  MenuZoom: function () {
    if (MathJax.Hub.config.showMathMenu) {
      if (!MathJax.Extension.MathMenu) {
        setTimeout(
          function () {
            MathJax.Callback.Queue(
              ["Require",MathJax.Ajax,"[MathJax]/extensions/MathMenu.js",{}],
              ["loadDomain",MathJax.Localization,"MathMenu"]
            )
          },1000
        );
      } else {
        setTimeout(
          MathJax.Callback(["loadDomain",MathJax.Localization,"MathMenu"]),
          1000
        );
      }
      if (!MathJax.Extension.MathZoom) {
        setTimeout(
          MathJax.Callback(["Require",MathJax.Ajax,"[MathJax]/extensions/MathZoom.js",{}]),
          2000
        );
      }
    }
  },

  //
  //  Setup the onload callback
  //
  onLoad: function () {
    var onload = this.onload =
      MathJax.Callback(function () {MathJax.Hub.Startup.signal.Post("onLoad")});
    if (document.body && document.readyState)
      if (MathJax.Hub.Browser.isMSIE) {
        // IE can change from loading to interactive before
        //  full page is ready, so go with complete (even though
        //  that means we may have to wait longer).
        if (document.readyState === "complete") {return [onload]}
      } else if (document.readyState !== "loading") {return [onload]}
    if (window.addEventListener) {
      window.addEventListener("load",onload,false);
      if (!this.params.noDOMContentEvent)
        {window.addEventListener("DOMContentLoaded",onload,false)}
    }
    else if (window.attachEvent) {window.attachEvent("onload",onload)}
    else {window.onload = onload}
    return onload;
  },

  //
  //  Perform the initial typesetting (or skip if configuration says to)
  //
  Typeset: function (element,callback) {
    if (MathJax.Hub.config.skipStartupTypeset) {return function () {}}
    return this.queue.Push(
      ["Post",this.signal,"Begin Typeset"],
      ["Typeset",MathJax.Hub,element,callback],
      ["Post",this.signal,"End Typeset"]
    );
  },

  //
  //  Create a URL in the MathJax hierarchy
  //
  URL: function (dir,name) {
    if (!name.match(/^([a-z]+:\/\/|\[|\/)/)) {name = "[MathJax]/"+dir+"/"+name}
    return name;
  },

  //
  //  Load an array of files, waiting for all of them
  //  to be loaded before going on
  //
  loadArray: function (files,dir,name,synchronous) {
    if (files) {
      if (!MathJax.Object.isArray(files)) {files = [files]}
      if (files.length) {
        var queue = MathJax.Callback.Queue(), callback = {}, file;
        for (var i = 0, m = files.length; i < m; i++) {
          file = this.URL(dir,files[i]);
          if (name) {file += "/" + name}
          if (synchronous) {queue.Push(["Require",MathJax.Ajax,file,callback])}
                      else {queue.Push(MathJax.Ajax.Require(file,callback))}
        }
        return queue.Push({}); // wait for everything to finish
      }
    }
    return null;
  }

};


/**********************************************************/

(function (BASENAME) {
  var BASE = window[BASENAME], ROOT = "["+BASENAME+"]";
  var HUB = BASE.Hub, AJAX = BASE.Ajax, CALLBACK = BASE.Callback;

  var JAX = MathJax.Object.Subclass({
    JAXFILE: "jax.js",
    require: null, // array of files to load before jax.js is complete
    config: {},
    //
    //  Make a subclass and return an instance of it.
    //  (FIXME: should we replace config with a copy of the constructor's
    //   config?  Otherwise all subclasses share the same config structure.)
    //
    Init: function (def,cdef) {
      if (arguments.length === 0) {return this}
      return (this.constructor.Subclass(def,cdef))();
    },
    //
    //  Augment by merging with class definition (not replacing)
    //
    Augment: function (def,cdef) {
      var cObject = this.constructor, ndef = {};
      if (def != null) {
        for (var id in def) {if (def.hasOwnProperty(id)) {
          if (typeof def[id] === "function")
            {cObject.protoFunction(id,def[id])} else {ndef[id] = def[id]}
        }}
        // MSIE doesn't list toString even if it is not native so handle it separately
        if (def.toString !== cObject.prototype.toString && def.toString !== {}.toString)
          {cObject.protoFunction('toString',def.toString)}
      }
      HUB.Insert(cObject.prototype,ndef);
      cObject.Augment(null,cdef);
      return this;
    },
    Translate: function (script,state) {
      throw Error(this.directory+"/"+this.JAXFILE+" failed to define the Translate() method");
    },
    Register: function (mimetype) {},
    Config: function () {
      this.config = HUB.CombineConfig(this.id,this.config);
      if (this.config.Augment) {this.Augment(this.config.Augment)}
    },
    Startup: function () {},
    loadComplete: function (file) {
      if (file === "config.js") {
        return AJAX.loadComplete(this.directory+"/"+file);
      } else {
        var queue = CALLBACK.Queue();
        queue.Push(
          HUB.Register.StartupHook("End Config",{}), // wait until config complete
          ["Post",HUB.Startup.signal,this.id+" Jax Config"],
          ["Config",this],
          ["Post",HUB.Startup.signal,this.id+" Jax Require"],
          // Config may set the required and extensions array,
          //  so use functions to delay making the reference until needed
          [function (THIS) {return MathJax.Hub.Startup.loadArray(THIS.require,this.directory)},this],
          [function (config,id) {return MathJax.Hub.Startup.loadArray(config.extensions,"extensions/"+id)},this.config||{},this.id],
          ["Post",HUB.Startup.signal,this.id+" Jax Startup"],
          ["Startup",this],
          ["Post",HUB.Startup.signal,this.id+" Jax Ready"]
        );
        if (this.copyTranslate) {
          queue.Push(
            [function (THIS) {
              THIS.preProcess  = THIS.preTranslate;
              THIS.Process     = THIS.Translate;
              THIS.postProcess = THIS.postTranslate;
            },this.constructor.prototype]
          );
        }
        return queue.Push(["loadComplete",AJAX,this.directory+"/"+file]);
      }
    }
  },{
    id: "Jax",
    version: "2.7.2",
    directory: ROOT+"/jax",
    extensionDir: ROOT+"/extensions"
  });

  /***********************************/

  BASE.InputJax = JAX.Subclass({
    elementJax: "mml",  // the element jax to load for this input jax
    sourceMenuTitle: /*_(MathMenu)*/ ["Original","Original Form"],
    copyTranslate: true,
    Process: function (script,state) {
      var queue = CALLBACK.Queue(), file;
      // Load any needed element jax
      var jax = this.elementJax; if (!BASE.Object.isArray(jax)) {jax = [jax]}
      for (var i = 0, m = jax.length; i < m; i++) {
        file = BASE.ElementJax.directory+"/"+jax[i]+"/"+this.JAXFILE;
        if (!this.require) {this.require = []}
          else if (!BASE.Object.isArray(this.require)) {this.require = [this.require]};
        this.require.push(file);  // so Startup will wait for it to be loaded
        queue.Push(AJAX.Require(file));
      }
      // Load the input jax
      file = this.directory+"/"+this.JAXFILE;
      var load = queue.Push(AJAX.Require(file));
      if (!load.called) {
        this.constructor.prototype.Process = function () {
          if (!load.called) {return load}
          throw Error(file+" failed to load properly");
        }
      }
      // Load the associated output jax
      jax = HUB.outputJax["jax/"+jax[0]];
      if (jax) {queue.Push(AJAX.Require(jax[0].directory+"/"+this.JAXFILE))}
      return queue.Push({});
    },
    needsUpdate: function (jax) {
      var script = jax.SourceElement();
      return (jax.originalText !== BASE.HTML.getScript(script));
    },
    Register: function (mimetype) {
      if (!HUB.inputJax) {HUB.inputJax = {}}
      HUB.inputJax[mimetype] = this;
    }
  },{
    id: "InputJax",
    version: "2.7.2",
    directory: JAX.directory+"/input",
    extensionDir: JAX.extensionDir
  });

  /***********************************/

  BASE.OutputJax = JAX.Subclass({
    copyTranslate: true,
    preProcess: function (state) {
      var load, file = this.directory+"/"+this.JAXFILE;
      this.constructor.prototype.preProcess = function (state) {
	if (!load.called) {return load}
        throw Error(file+" failed to load properly");
      }
      load = AJAX.Require(file);
      return load;
    },
    Register: function (mimetype) {
      var jax = HUB.outputJax;
      if (!jax[mimetype]) {jax[mimetype] = []}
      //  If the output jax is earlier in the original configuration list, put it first here
      if (jax[mimetype].length && (this.id === HUB.config.menuSettings.renderer ||
            (jax.order[this.id]||0) < (jax.order[jax[mimetype][0].id]||0)))
        {jax[mimetype].unshift(this)} else {jax[mimetype].push(this)}
      //  Make sure the element jax is loaded before Startup is called
      if (!this.require) {this.require = []}
        else if (!BASE.Object.isArray(this.require)) {this.require = [this.require]};
      this.require.push(BASE.ElementJax.directory+"/"+(mimetype.split(/\//)[1])+"/"+this.JAXFILE);
    },
    Remove: function (jax) {}
  },{
    id: "OutputJax",
    version: "2.7.2",
    directory: JAX.directory+"/output",
    extensionDir: JAX.extensionDir,
    fontDir: ROOT+(BASE.isPacked?"":"/..")+"/fonts",
    imageDir: ROOT+(BASE.isPacked?"":"/..")+"/images"
  });

  /***********************************/

  BASE.ElementJax = JAX.Subclass({
    // make a subclass, not an instance
    Init: function (def,cdef) {return this.constructor.Subclass(def,cdef)},

    inputJax: null,
    outputJax: null,
    inputID: null,
    originalText: "",
    mimeType: "",
    sourceMenuTitle: /*_(MathMenu)*/ ["MathMLcode","MathML Code"],

    Text: function (text,callback) {
      var script = this.SourceElement();
      BASE.HTML.setScript(script,text);
      script.MathJax.state = this.STATE.UPDATE;
      return HUB.Update(script,callback);
    },
    Reprocess: function (callback) {
      var script = this.SourceElement();
      script.MathJax.state = this.STATE.UPDATE;
      return HUB.Reprocess(script,callback);
    },
    Update: function (callback) {return this.Rerender(callback)},
    Rerender: function (callback) {
      var script = this.SourceElement();
      script.MathJax.state = this.STATE.OUTPUT;
      return HUB.Process(script,callback);
    },
    Remove: function (keep) {
      if (this.hover) {this.hover.clear(this)}
      BASE.OutputJax[this.outputJax].Remove(this);
      if (!keep) {
        HUB.signal.Post(["Remove Math",this.inputID]); // wait for this to finish?
        this.Detach();
      }
    },
    needsUpdate: function () {
      return BASE.InputJax[this.inputJax].needsUpdate(this);
    },

    SourceElement: function () {return document.getElementById(this.inputID)},

    Attach: function (script,inputJax) {
      var jax = script.MathJax.elementJax;
      if (script.MathJax.state === this.STATE.UPDATE) {
        jax.Clone(this);
      } else {
        jax = script.MathJax.elementJax = this;
        if (script.id) {this.inputID = script.id}
          else {script.id = this.inputID = BASE.ElementJax.GetID(); this.newID = 1}
      }
      jax.originalText = BASE.HTML.getScript(script);
      jax.inputJax = inputJax;
      if (jax.root) {jax.root.inputID = jax.inputID}
      return jax;
    },
    Detach: function () {
      var script = this.SourceElement(); if (!script) return;
      try {delete script.MathJax} catch(err) {script.MathJax = null}
      if (this.newID) {script.id = ""}
    },
    Clone: function (jax) {
      var id;
      for (id in this) {
        if (!this.hasOwnProperty(id)) continue;
        if (typeof(jax[id]) === 'undefined' && id !== 'newID') {delete this[id]}
      }
      for (id in jax) {
        if (!jax.hasOwnProperty(id)) continue;
        if (typeof(this[id]) === 'undefined' || (this[id] !== jax[id] && id !== 'inputID'))
          {this[id] = jax[id]}
      }
    }
  },{
    id: "ElementJax",
    version: "2.7.2",
    directory: JAX.directory+"/element",
    extensionDir: JAX.extensionDir,
    ID: 0,  // jax counter (for IDs)
    STATE: {
      PENDING: 1,      // script is identified as math but not yet processed
      PROCESSED: 2,    // script has been processed
      UPDATE: 3,       // elementJax should be updated
      OUTPUT: 4        // output should be updated (input is OK)
    },

    GetID: function () {this.ID++; return "MathJax-Element-"+this.ID},
    Subclass: function () {
      var obj = JAX.Subclass.apply(this,arguments);
      obj.loadComplete = this.prototype.loadComplete;
      return obj;
    }
  });
  BASE.ElementJax.prototype.STATE = BASE.ElementJax.STATE;

  //
  //  Some "Fake" jax used to allow menu access for "Math Processing Error" messages
  //
  BASE.OutputJax.Error = {
    id: "Error", version: "2.7.2", config: {}, errors: 0,
    ContextMenu: function () {return BASE.Extension.MathEvents.Event.ContextMenu.apply(BASE.Extension.MathEvents.Event,arguments)},
    Mousedown:   function () {return BASE.Extension.MathEvents.Event.AltContextMenu.apply(BASE.Extension.MathEvents.Event,arguments)},
    getJaxFromMath: function (math) {return (math.nextSibling.MathJax||{}).error},
    Jax: function (text,script) {
      var jax = MathJax.Hub.inputJax[script.type.replace(/ *;(.|\s)*/,"")];
      this.errors++;
      return {
        inputJax: (jax||{id:"Error"}).id,  // Use Error InputJax as fallback
        outputJax: "Error",
        inputID: "MathJax-Error-"+this.errors,
        sourceMenuTitle: /*_(MathMenu)*/ ["ErrorMessage","Error Message"],
        sourceMenuFormat: "Error",
        originalText: MathJax.HTML.getScript(script),
        errorText: text
      }
    }
  };
  BASE.InputJax.Error = {
    id: "Error", version: "2.7.2", config: {},
    sourceMenuTitle: /*_(MathMenu)*/ ["Original","Original Form"]
  };

})("MathJax");

/**********************************************************/

(function (BASENAME) {
  var BASE = window[BASENAME];
  if (!BASE) {BASE = window[BASENAME] = {}}

  var HUB = BASE.Hub; var STARTUP = HUB.Startup; var CONFIG = HUB.config;
  var HEAD = document.head || (document.getElementsByTagName("head")[0]);
  if (!HEAD) {HEAD = document.childNodes[0]};
  var scripts = (document.documentElement || document).getElementsByTagName("script");
  if (scripts.length === 0 && HEAD.namespaceURI)
    scripts = document.getElementsByTagNameNS(HEAD.namespaceURI,"script");
  var namePattern = new RegExp("(^|/)"+BASENAME+"\\.js(\\?.*)?$");
  for (var i = scripts.length-1; i >= 0; i--) {
    if ((scripts[i].src||"").match(namePattern)) {
      STARTUP.script = scripts[i].innerHTML;
      if (RegExp.$2) {
        var params = RegExp.$2.substr(1).split(/\&/);
        for (var j = 0, m = params.length; j < m; j++) {
          var KV = params[j].match(/(.*)=(.*)/);
          if (KV) {STARTUP.params[unescape(KV[1])] = unescape(KV[2])}
             else {STARTUP.params[params[j]] = true}
        }
      }
      CONFIG.root = scripts[i].src.replace(/(^|\/)[^\/]*(\?.*)?$/,'');
      BASE.Ajax.config.root = CONFIG.root;
      BASE.Ajax.params = STARTUP.params;
      break;
    }
  }

  var AGENT = navigator.userAgent;
  var BROWSERS = {
    isMac:       (navigator.platform.substr(0,3) === "Mac"),
    isPC:        (navigator.platform.substr(0,3) === "Win"),
    isMSIE:      ("ActiveXObject" in window && "clipboardData" in window),
    isEdge:      ("MSGestureEvent" in window && "chrome" in window &&
                     window.chrome.loadTimes == null),
    isFirefox:   (!!AGENT.match(/Gecko\//) && !AGENT.match(/like Gecko/)),
    isSafari:    (!!AGENT.match(/ (Apple)?WebKit\//) && !AGENT.match(/ like iPhone /) &&
                     (!window.chrome || window.chrome.app == null)),
    isChrome:    ("chrome" in window && window.chrome.loadTimes != null),
    isOpera:     ("opera" in window && window.opera.version != null),
    isKonqueror: ("konqueror" in window && navigator.vendor == "KDE"),
    versionAtLeast: function (v) {
      var bv = (this.version).split('.'); v = (new String(v)).split('.');
      for (var i = 0, m = v.length; i < m; i++)
        {if (bv[i] != v[i]) {return parseInt(bv[i]||"0") >= parseInt(v[i])}}
      return true;
    },
    Select: function (choices) {
      var browser = choices[HUB.Browser];
      if (browser) {return browser(HUB.Browser)}
      return null;
    }
  };

  var xAGENT = AGENT
    .replace(/^Mozilla\/(\d+\.)+\d+ /,"")                                   // remove initial Mozilla, which is never right
    .replace(/[a-z][-a-z0-9._: ]+\/\d+[^ ]*-[^ ]*\.([a-z][a-z])?\d+ /i,"")  // remove linux version
    .replace(/Gentoo |Ubuntu\/(\d+\.)*\d+ (\([^)]*\) )?/,"");               // special case for these

  HUB.Browser = HUB.Insert(HUB.Insert(new String("Unknown"),{version: "0.0"}),BROWSERS);
  for (var browser in BROWSERS) {if (BROWSERS.hasOwnProperty(browser)) {
    if (BROWSERS[browser] && browser.substr(0,2) === "is") {
      browser = browser.slice(2);
      if (browser === "Mac" || browser === "PC") continue;
      HUB.Browser = HUB.Insert(new String(browser),BROWSERS);
      var VERSION = new RegExp(
        ".*(Version/| Trident/.*; rv:)((?:\\d+\\.)+\\d+)|" +                      // for Safari, Opera10, and IE11+
        ".*("+browser+")"+(browser == "MSIE" ? " " : "/")+"((?:\\d+\\.)*\\d+)|"+  // for one of the main browsers
        "(?:^|\\(| )([a-z][-a-z0-9._: ]+|(?:Apple)?WebKit)/((?:\\d+\\.)+\\d+)");  // for unrecognized browser
      var MATCH = VERSION.exec(xAGENT) || ["","","","unknown","0.0"];
      HUB.Browser.name = (MATCH[1] != "" ? browser : (MATCH[3] || MATCH[5]));
      HUB.Browser.version = MATCH[2] || MATCH[4] || MATCH[6];
      break;
    }
  }};

  //
  //  Initial browser-specific info (e.g., touch up version or name, check for MathPlayer, etc.)
  //  Wrap in try/catch just in case of error (see issue #1155).
  //
  try {HUB.Browser.Select({
    Safari: function (browser) {
      var v = parseInt((String(browser.version).split("."))[0]);
      if (v > 85) {browser.webkit = browser.version}
      if      (v >= 538) {browser.version = "8.0"}
      else if (v >= 537) {browser.version = "7.0"}
      else if (v >= 536) {browser.version = "6.0"}
      else if (v >= 534) {browser.version = "5.1"}
      else if (v >= 533) {browser.version = "5.0"}
      else if (v >= 526) {browser.version = "4.0"}
      else if (v >= 525) {browser.version = "3.1"}
      else if (v >  500) {browser.version = "3.0"}
      else if (v >  400) {browser.version = "2.0"}
      else if (v >   85) {browser.version = "1.0"}
      browser.webkit = (navigator.appVersion.match(/WebKit\/(\d+)\./))[1];
      browser.isMobile = (navigator.appVersion.match(/Mobile/i) != null);
      browser.noContextMenu = browser.isMobile;
    },
    Firefox: function (browser) {
      if ((browser.version === "0.0" || AGENT.match(/Firefox/) == null) &&
           navigator.product === "Gecko") {
        var rv = AGENT.match(/[\/ ]rv:(\d+\.\d.*?)[\) ]/);
        if (rv) {browser.version = rv[1]}
        else {
          var date = (navigator.buildID||navigator.productSub||"0").substr(0,8);
          if      (date >= "20111220") {browser.version = "9.0"}
          else if (date >= "20111120") {browser.version = "8.0"}
          else if (date >= "20110927") {browser.version = "7.0"}
          else if (date >= "20110816") {browser.version = "6.0"}
          else if (date >= "20110621") {browser.version = "5.0"}
          else if (date >= "20110320") {browser.version = "4.0"}
          else if (date >= "20100121") {browser.version = "3.6"}
          else if (date >= "20090630") {browser.version = "3.5"}
          else if (date >= "20080617") {browser.version = "3.0"}
          else if (date >= "20061024") {browser.version = "2.0"}
        }
      }
      browser.isMobile = (navigator.appVersion.match(/Android/i) != null ||
                          AGENT.match(/ Fennec\//) != null ||
                          AGENT.match(/Mobile/) != null);
    },
    Chrome: function (browser) {
      browser.noContextMenu = browser.isMobile = !!navigator.userAgent.match(/ Mobile[ \/]/);
    },
    Opera: function (browser) {browser.version = opera.version()},
    Edge: function (browser) {
      browser.isMobile = !!navigator.userAgent.match(/ Phone/);
    },
    MSIE: function (browser) {
      browser.isMobile = !!navigator.userAgent.match(/ Phone/);
      browser.isIE9 = !!(document.documentMode && (window.performance || window.msPerformance));
      MathJax.HTML.setScriptBug = !browser.isIE9 || document.documentMode < 9;
      MathJax.Hub.msieHTMLCollectionBug = (document.documentMode < 9);
      //
      //  MathPlayer doesn't function properly in IE10, and not at all in IE11,
      //  so don't even try to load it.
      //
      if (document.documentMode < 10 && !STARTUP.params.NoMathPlayer) {
        try {
          new ActiveXObject("MathPlayer.Factory.1");
          browser.hasMathPlayer = true;
        } catch (err) {}
        try {
          if (browser.hasMathPlayer) {
            var mathplayer = document.createElement("object");
            mathplayer.id = "mathplayer"; mathplayer.classid = "clsid:32F66A20-7614-11D4-BD11-00104BD3F987";
            HEAD.appendChild(mathplayer);
            document.namespaces.add("m","http://www.w3.org/1998/Math/MathML");
            browser.mpNamespace = true;
            if (document.readyState && (document.readyState === "loading" ||
                                        document.readyState === "interactive")) {
              document.write('<?import namespace="m" implementation="#MathPlayer">');
              browser.mpImported = true;
            }
          } else {
            //  Adding any namespace avoids a crash in IE9 in IE9-standards mode
            //  (any reference to document.namespaces before document.readyState is
            //   "complete" causes an "unspecified error" to be thrown)
            document.namespaces.add("mjx_IE_fix","http://www.w3.org/1999/xlink");
          }
        } catch (err) {}
      }
    }
  });} catch (err) {
    console.error(err.message);
  }
  
MathJax.Ajax.Preloading(
"[MathJax]/jax/element/mml/jax.js",
"[MathJax]/extensions/MathEvents.js",
"[MathJax]/extensions/tex2jax.js",
"[MathJax]/jax/input/TeX/config.js",
"[MathJax]/jax/input/TeX/jax.js",
"[MathJax]/jax/output/SVG/config.js",
"[MathJax]/jax/output/SVG/jax.js",
"[MathJax]/jax/output/SVG/autoload/mtable.js",
"[MathJax]/jax/output/SVG/autoload/mglyph.js",
"[MathJax]/jax/output/SVG/autoload/mmultiscripts.js",
"[MathJax]/jax/output/SVG/autoload/annotation-xml.js",
"[MathJax]/jax/output/SVG/autoload/maction.js",
"[MathJax]/jax/output/SVG/autoload/multiline.js",
"[MathJax]/jax/output/SVG/autoload/menclose.js",
"[MathJax]/jax/output/SVG/autoload/ms.js",
"[MathJax]/jax/output/SVG/fonts/TeX/fontdata.js",
"[MathJax]/jax/output/SVG/fonts/TeX/fontdata-extra.js",
"[MathJax]/jax/output/SVG/fonts/TeX/AMS/Regular/Main.js",
"[MathJax]/jax/output/SVG/fonts/TeX/Size2/Regular/Main.js");
MathJax.Hub.Config({"v1.0-compatible":false});

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/element/mml/jax.js
 *  
 *  Implements the MML ElementJax that holds the internal represetation
 *  of the mathematics on the page.  Various InputJax will produce this
 *  format, and the OutputJax will display it in various formats.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2009-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.ElementJax.mml = MathJax.ElementJax({
  mimeType: "jax/mml"
},{
  id: "mml",
  version: "2.7.2",
  directory: MathJax.ElementJax.directory + "/mml",
  extensionDir: MathJax.ElementJax.extensionDir + "/mml",
  optableDir: MathJax.ElementJax.directory + "/mml/optable"
});

MathJax.ElementJax.mml.Augment({
  Init: function () {
    if (arguments.length === 1 && arguments[0].type === "math") {this.root = arguments[0]}
      else {this.root = MathJax.ElementJax.mml.math.apply(this,arguments)}
    if (this.root.attr && this.root.attr.mode) {
      if (!this.root.display && this.root.attr.mode === "display") {
        this.root.display = "block";
        this.root.attrNames.push("display");
      }
      delete this.root.attr.mode;
      for (var i = 0, m = this.root.attrNames.length; i < m; i++) {
        if (this.root.attrNames[i] === "mode") {this.root.attrNames.splice(i,1); break}
      }
    }
  }
},{
  INHERIT: "_inherit_",
  AUTO: "_auto_",
  SIZE: {
    INFINITY: "infinity",
    SMALL: "small",
    NORMAL: "normal",
    BIG: "big"
  },
  COLOR: {
    TRANSPARENT: "transparent"
  },
  VARIANT: {
    NORMAL: "normal",
    BOLD: "bold",
    ITALIC: "italic",
    BOLDITALIC: "bold-italic",
    DOUBLESTRUCK: "double-struck",
    FRAKTUR: "fraktur",
    BOLDFRAKTUR: "bold-fraktur",
    SCRIPT: "script",
    BOLDSCRIPT: "bold-script",
    SANSSERIF: "sans-serif",
    BOLDSANSSERIF: "bold-sans-serif",
    SANSSERIFITALIC: "sans-serif-italic",
    SANSSERIFBOLDITALIC: "sans-serif-bold-italic",
    MONOSPACE: "monospace",
    INITIAL: "inital",
    TAILED: "tailed",
    LOOPED: "looped",
    STRETCHED: "stretched",
    CALIGRAPHIC: "-tex-caligraphic",
    OLDSTYLE: "-tex-oldstyle"
  },
  FORM: {
    PREFIX: "prefix",
    INFIX: "infix",
    POSTFIX: "postfix"
  },
  LINEBREAK: {
    AUTO: "auto",
    NEWLINE: "newline",
    NOBREAK: "nobreak",
    GOODBREAK: "goodbreak",
    BADBREAK: "badbreak"
  },
  LINEBREAKSTYLE: {
    BEFORE: "before",
    AFTER: "after",
    DUPLICATE: "duplicate",
    INFIXLINBREAKSTYLE: "infixlinebreakstyle"
  },
  INDENTALIGN: {
    LEFT: "left",
    CENTER: "center",
    RIGHT: "right",
    AUTO: "auto",
    ID: "id",
    INDENTALIGN: "indentalign"
  },
  INDENTSHIFT: {
    INDENTSHIFT: "indentshift"
  },
  LINETHICKNESS: {
    THIN: "thin",
    MEDIUM: "medium",
    THICK: "thick"
  },
  NOTATION: {
    LONGDIV: "longdiv",
    ACTUARIAL: "actuarial",
    RADICAL: "radical",
    BOX: "box",
    ROUNDEDBOX: "roundedbox",
    CIRCLE: "circle",
    LEFT: "left",
    RIGHT: "right",
    TOP: "top",
    BOTTOM: "bottom",
    UPDIAGONALSTRIKE: "updiagonalstrike",
    DOWNDIAGONALSTRIKE: "downdiagonalstrike",
    UPDIAGONALARROW: "updiagonalarrow",
    VERTICALSTRIKE: "verticalstrike",
    HORIZONTALSTRIKE: "horizontalstrike",
    PHASORANGLE: "phasorangle",
    MADRUWB: "madruwb"
  },
  ALIGN: {
    TOP: "top",
    BOTTOM: "bottom",
    CENTER: "center",
    BASELINE: "baseline",
    AXIS: "axis",
    LEFT: "left",
    RIGHT: "right"
  },
  LINES: {
    NONE: "none",
    SOLID: "solid",
    DASHED: "dashed"
  },
  SIDE: {
    LEFT: "left",
    RIGHT: "right",
    LEFTOVERLAP: "leftoverlap",
    RIGHTOVERLAP: "rightoverlap"
  },
  WIDTH: {
    AUTO: "auto",
    FIT: "fit"
  },
  ACTIONTYPE: {
    TOGGLE: "toggle",
    STATUSLINE: "statusline",
    TOOLTIP: "tooltip",
    INPUT: "input"
  },
  LENGTH: {
    VERYVERYTHINMATHSPACE: "veryverythinmathspace",
    VERYTHINMATHSPACE: "verythinmathspace",
    THINMATHSPACE: "thinmathspace",
    MEDIUMMATHSPACE: "mediummathspace",
    THICKMATHSPACE: "thickmathspace",
    VERYTHICKMATHSPACE: "verythickmathspace",
    VERYVERYTHICKMATHSPACE: "veryverythickmathspace",
    NEGATIVEVERYVERYTHINMATHSPACE: "negativeveryverythinmathspace",
    NEGATIVEVERYTHINMATHSPACE: "negativeverythinmathspace",
    NEGATIVETHINMATHSPACE: "negativethinmathspace",
    NEGATIVEMEDIUMMATHSPACE: "negativemediummathspace",
    NEGATIVETHICKMATHSPACE: "negativethickmathspace",
    NEGATIVEVERYTHICKMATHSPACE: "negativeverythickmathspace",
    NEGATIVEVERYVERYTHICKMATHSPACE: "negativeveryverythickmathspace"
  },
  OVERFLOW: {
    LINBREAK: "linebreak",
    SCROLL: "scroll",
    ELIDE: "elide",
    TRUNCATE: "truncate",
    SCALE: "scale"
  },
  UNIT: {
    EM: "em",
    EX: "ex",
    PX: "px",
    IN: "in",
    CM: "cm",
    MM: "mm",
    PT: "pt",
    PC: "pc"
  },
  TEXCLASS: {
    ORD:   0,
    OP:    1,
    BIN:   2,
    REL:   3,
    OPEN:  4,
    CLOSE: 5,
    PUNCT: 6,
    INNER: 7,
    VCENTER: 8,
    NONE:   -1
  },
  TEXCLASSNAMES: ["ORD", "OP", "BIN", "REL", "OPEN", "CLOSE", "PUNCT", "INNER", "VCENTER"],
  skipAttributes: {
    texClass:true, useHeight:true, texprimestyle:true
  },
  copyAttributes: {
    displaystyle:1, scriptlevel:1, open:1, close:1, form:1,
    actiontype: 1,
    fontfamily:true, fontsize:true, fontweight:true, fontstyle:true,
    color:true, background:true,
    id:true, "class":1, href:true, style:true
  },
  copyAttributeNames: [
    "displaystyle", "scriptlevel", "open", "close", "form",  // force these to be copied
    "actiontype",
    "fontfamily", "fontsize", "fontweight", "fontstyle",
    "color", "background",
    "id", "class", "href", "style"
  ],
  nocopyAttributes: {
    fontfamily: true, fontsize: true, fontweight: true, fontstyle: true,
    color: true, background: true,
    id: true, 'class': true, href: true, style: true,
    xmlns: true
  },
  Error: function (message,def) {
    var mml = this.merror(message),
        dir = MathJax.Localization.fontDirection(),
        font = MathJax.Localization.fontFamily();
    if (def) {mml = mml.With(def)}
    if (dir || font) {
      mml = this.mstyle(mml);
      if (dir) {mml.dir = dir}
      if (font) {mml.style.fontFamily = "font-family: "+font}
    }
    return mml;
  }
});

(function (MML) {

  MML.mbase = MathJax.Object.Subclass({
    type: "base", isToken: false,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT
    },
    noInherit: {},
    noInheritAttribute: {
      texClass: true
    },
    getRemoved: {},
    linebreakContainer: false,
    
    Init: function () {
      this.data = [];
      if (this.inferRow && !(arguments.length === 1 && arguments[0].inferred))
        {this.Append(MML.mrow().With({inferred: true, notParent: true}))}
      this.Append.apply(this,arguments);
    },
    With: function (def) {
      for (var id in def) {if (def.hasOwnProperty(id)) {this[id] = def[id]}}
      return this;
    },
    Append: function () {
      if (this.inferRow && this.data.length) {
        this.data[0].Append.apply(this.data[0],arguments);
      } else {
        for (var i = 0, m = arguments.length; i < m; i++)
          {this.SetData(this.data.length,arguments[i])}
      }
    },
    SetData: function (i,item) {
      if (item != null) {
        if (!(item instanceof MML.mbase))
          {item = (this.isToken || this.isChars ? MML.chars(item) : MML.mtext(item))}
        item.parent = this;
        item.setInherit(this.inheritFromMe ? this : this.inherit);
      }
      this.data[i] = item;
    },
    Parent: function () {
      var parent = this.parent;
      while (parent && parent.notParent) {parent = parent.parent}
      return parent;
    },
    Get: function (name,nodefault,noself) {
      if (!noself) {
        if (this[name] != null) {return this[name]}
        if (this.attr && this.attr[name] != null) {return this.attr[name]}
      }
      // FIXME: should cache these values and get from cache
      // (clear cache when appended to a new object?)
      var parent = this.Parent();
      if (parent && parent["adjustChild_"+name] != null) {
        return (parent["adjustChild_"+name])(this.childPosition(),nodefault);
      }
      var obj = this.inherit; var root = obj;
      while (obj) {
        var value = obj[name]; if (value == null && obj.attr) {value = obj.attr[name]}
        if (obj.removedStyles && obj.getRemoved[name] && value == null) value = obj.removedStyles[obj.getRemoved[name]];
        if (value != null && obj.noInheritAttribute && !obj.noInheritAttribute[name]) {
          var noInherit = obj.noInherit[this.type];
          if (!(noInherit && noInherit[name])) {return value}
        }
        root = obj; obj = obj.inherit;
      }
      if (!nodefault) {
        if (this.defaults[name] === MML.AUTO) {return this.autoDefault(name)}
        if (this.defaults[name] !== MML.INHERIT && this.defaults[name] != null)
          {return this.defaults[name]}
        if (root) {return root.defaults[name]}
      }
      return null;
    },
    hasValue: function (name) {return (this.Get(name,true) != null)},
    getValues: function () {
      var values = {};
      for (var i = 0, m = arguments.length; i < m; i++)
        {values[arguments[i]] = this.Get(arguments[i])}
      return values;
    },
    adjustChild_scriptlevel:   function (i,nodef) {return this.Get("scriptlevel",nodef)},   // always inherit from parent
    adjustChild_displaystyle:  function (i,nodef) {return this.Get("displaystyle",nodef)},  // always inherit from parent
    adjustChild_texprimestyle: function (i,nodef) {return this.Get("texprimestyle",nodef)}, // always inherit from parent
    childPosition: function () {
      var child = this, parent = child.parent;
      while (parent.notParent) {child = parent; parent = child.parent}
      for (var i = 0, m = parent.data.length; i < m; i++) {if (parent.data[i] === child) {return i}}
      return null;
    },
    setInherit: function (obj) {
      if (obj !== this.inherit && this.inherit == null) {
        this.inherit = obj;
        for (var i = 0, m = this.data.length; i < m; i++) {
          if (this.data[i] && this.data[i].setInherit) {this.data[i].setInherit(obj)}
        }
      }
    },
    setTeXclass: function (prev) {
      this.getPrevClass(prev);
      return (typeof(this.texClass) !== "undefined" ? this : prev);
    },
    getPrevClass: function (prev) {
      if (prev) {
        this.prevClass = prev.Get("texClass");
        this.prevLevel = prev.Get("scriptlevel");
      }
    },
    updateTeXclass: function (core) {
      if (core) {
        this.prevClass = core.prevClass; delete core.prevClass;
        this.prevLevel = core.prevLevel; delete core.prevLevel;
        this.texClass = core.Get("texClass");
      }
    },
    texSpacing: function () {
      var prev = (this.prevClass != null ? this.prevClass : MML.TEXCLASS.NONE);
      var tex = (this.Get("texClass") || MML.TEXCLASS.ORD);
      if (prev === MML.TEXCLASS.NONE || tex === MML.TEXCLASS.NONE) {return ""}
      if (prev === MML.TEXCLASS.VCENTER) {prev = MML.TEXCLASS.ORD}
      if (tex  === MML.TEXCLASS.VCENTER) {tex  = MML.TEXCLASS.ORD}
      var space = this.TEXSPACE[prev][tex];
      if ((this.prevLevel > 0 || this.Get("scriptlevel") > 0) && space >= 0) {return ""}
      return this.TEXSPACELENGTH[Math.abs(space)];
    },
    TEXSPACELENGTH:[
      "",
      MML.LENGTH.THINMATHSPACE,
      MML.LENGTH.MEDIUMMATHSPACE,
      MML.LENGTH.THICKMATHSPACE
    ],
    // See TeXBook Chapter 18 (p. 170)
    TEXSPACE: [
      [ 0,-1, 2, 3, 0, 0, 0, 1], // ORD
      [-1,-1, 0, 3, 0, 0, 0, 1], // OP
      [ 2, 2, 0, 0, 2, 0, 0, 2], // BIN
      [ 3, 3, 0, 0, 3, 0, 0, 3], // REL
      [ 0, 0, 0, 0, 0, 0, 0, 0], // OPEN
      [ 0,-1, 2, 3, 0, 0, 0, 1], // CLOSE
      [ 1, 1, 0, 1, 1, 1, 1, 1], // PUNCT
      [ 1,-1, 2, 3, 1, 0, 1, 1]  // INNER
    ],
    autoDefault: function (name) {return ""},
    isSpacelike: function () {return false},
    isEmbellished: function () {return false},
    Core: function () {return this},
    CoreMO: function () {return this},
    childIndex: function(child) {
      if (child == null) return;
      for (var i = 0, m = this.data.length; i < m; i++) if (child === this.data[i]) return i;
    },
    CoreIndex: function () {
      return (this.inferRow ? this.data[0]||this : this).childIndex(this.Core());
    },
    hasNewline: function () {
      if (this.isEmbellished()) {return this.CoreMO().hasNewline()}
      if (this.isToken || this.linebreakContainer) {return false}
      for (var i = 0, m = this.data.length; i < m; i++) {
        if (this.data[i] && this.data[i].hasNewline()) {return true}
      }
      return false;
    },
    array: function () {if (this.inferred) {return this.data} else {return [this]}},
    toString: function () {return this.type+"("+this.data.join(",")+")"},
    getAnnotation: function () {return null}
  },{
    childrenSpacelike: function () {
      for (var i = 0, m = this.data.length; i < m; i++)
        {if (!this.data[i].isSpacelike()) {return false}}
      return true;
    },
    childEmbellished: function () {
      return (this.data[0] && this.data[0].isEmbellished());
    },
    childCore: function () {return (this.inferRow && this.data[0] ? this.data[0].Core() : this.data[0])},
    childCoreMO: function () {return (this.data[0] ? this.data[0].CoreMO() : null)},
    setChildTeXclass: function (prev) {
      if (this.data[0]) {
        prev = this.data[0].setTeXclass(prev);
        this.updateTeXclass(this.data[0]);
      }
      return prev;
    },
    setBaseTeXclasses: function (prev) {
      this.getPrevClass(prev); this.texClass = null;
      if (this.data[0]) {
        if (this.isEmbellished() || this.data[0].isa(MML.mi)) {
          prev = this.data[0].setTeXclass(prev);
          this.updateTeXclass(this.Core());
        } else {this.data[0].setTeXclass(); prev = this}
      } else {prev = this}
      for (var i = 1, m = this.data.length; i < m; i++)
        {if (this.data[i]) {this.data[i].setTeXclass()}}
      return prev;
    },
    setSeparateTeXclasses: function (prev) {
      this.getPrevClass(prev);
      for (var i = 0, m = this.data.length; i < m; i++)
        {if (this.data[i]) {this.data[i].setTeXclass()}}
      if (this.isEmbellished()) {this.updateTeXclass(this.Core())}
      return this;
    }
  });
  
  MML.mi = MML.mbase.Subclass({
    type: "mi", isToken: true,
    texClass: MML.TEXCLASS.ORD,
    defaults: {
      mathvariant: MML.AUTO,
      mathsize: MML.INHERIT,
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT
    },
    autoDefault: function (name) {
      if (name === "mathvariant") {
        var mi = (this.data[0]||"").toString();
        return (mi.length === 1 ||
               (mi.length === 2 && mi.charCodeAt(0) >= 0xD800 && mi.charCodeAt(0) < 0xDC00) ?
                  MML.VARIANT.ITALIC : MML.VARIANT.NORMAL);
      }
      return "";
    },
    setTeXclass: function (prev) {
      this.getPrevClass(prev);
      var name = this.data.join("");
      if (name.length > 1 && name.match(/^[a-z][a-z0-9]*$/i) &&
          this.texClass === MML.TEXCLASS.ORD) {
        this.texClass = MML.TEXCLASS.OP;
        this.autoOP = true;
      }
      return this;
    }
  });
  
  MML.mn = MML.mbase.Subclass({
    type: "mn", isToken: true,
    texClass: MML.TEXCLASS.ORD,
    defaults: {
      mathvariant: MML.INHERIT,
      mathsize: MML.INHERIT,
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT
    }
  });
  
  MML.mo = MML.mbase.Subclass({
    type: "mo", isToken: true,
    defaults: {
      mathvariant: MML.INHERIT,
      mathsize: MML.INHERIT,
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT,
      form: MML.AUTO,
      fence: MML.AUTO,
      separator: MML.AUTO,
      lspace: MML.AUTO,
      rspace: MML.AUTO,
      stretchy: MML.AUTO,
      symmetric: MML.AUTO,
      maxsize: MML.AUTO,
      minsize: MML.AUTO,
      largeop: MML.AUTO,
      movablelimits: MML.AUTO,
      accent: MML.AUTO,
      linebreak: MML.LINEBREAK.AUTO,
      lineleading: MML.INHERIT,
      linebreakstyle: MML.AUTO,
      linebreakmultchar: MML.INHERIT,
      indentalign: MML.INHERIT,
      indentshift: MML.INHERIT,
      indenttarget: MML.INHERIT,
      indentalignfirst: MML.INHERIT,
      indentshiftfirst: MML.INHERIT,
      indentalignlast: MML.INHERIT,
      indentshiftlast: MML.INHERIT,
      texClass: MML.AUTO
    },
    defaultDef: {
      form: MML.FORM.INFIX,
      fence: false,
      separator: false,
      lspace: MML.LENGTH.THICKMATHSPACE,
      rspace: MML.LENGTH.THICKMATHSPACE,
      stretchy: false,
      symmetric: false,
      maxsize: MML.SIZE.INFINITY,
      minsize: '0em', //'1em',
      largeop: false,
      movablelimits: false,
      accent: false,
      linebreak: MML.LINEBREAK.AUTO,
      lineleading: "1ex",
      linebreakstyle: "before",
      indentalign: MML.INDENTALIGN.AUTO,
      indentshift: "0",
      indenttarget: "",
      indentalignfirst: MML.INDENTALIGN.INDENTALIGN,
      indentshiftfirst: MML.INDENTSHIFT.INDENTSHIFT,
      indentalignlast: MML.INDENTALIGN.INDENTALIGN,
      indentshiftlast: MML.INDENTSHIFT.INDENTSHIFT,
      texClass: MML.TEXCLASS.REL // for MML, but TeX sets ORD explicitly
    },
    SPACE_ATTR: {lspace: 0x01, rspace: 0x02, form: 0x04},
    useMMLspacing: 0x07,
    autoDefault: function (name,nodefault) {
      var def = this.def;
      if (!def) {
        if (name === "form") {this.useMMLspacing &= ~this.SPACE_ATTR.form; return this.getForm()}
        var mo = this.data.join("");
        var forms = [this.Get("form"),MML.FORM.INFIX,MML.FORM.POSTFIX,MML.FORM.PREFIX];
        for (var i = 0, m = forms.length; i < m; i++) {
          var data = this.OPTABLE[forms[i]][mo];
          if (data) {def = this.makeDef(data); break}
        }
        if (!def) {def = this.CheckRange(mo)}
        if (!def && nodefault) {def = {}} else {
          if (!def) {def = MathJax.Hub.Insert({},this.defaultDef)}
          if (this.parent) {this.def = def} else {def = MathJax.Hub.Insert({},def)}
          def.form = forms[0];
        }
      }
      this.useMMLspacing &= ~(this.SPACE_ATTR[name] || 0);
      if (def[name] != null) {return def[name]}
      else if (!nodefault) {return this.defaultDef[name]}
      return "";
    },
    CheckRange: function (mo) {
      var n = mo.charCodeAt(0);
      if (n >= 0xD800 && n < 0xDC00) {n = (((n-0xD800)<<10)+(mo.charCodeAt(1)-0xDC00))+0x10000}
      for (var i = 0, m = this.RANGES.length; i < m && this.RANGES[i][0] <= n; i++) {
        if (n <= this.RANGES[i][1]) {
          if (this.RANGES[i][3]) {
            var file = MML.optableDir+"/"+this.RANGES[i][3]+".js";
            this.RANGES[i][3] = null;
            MathJax.Hub.RestartAfter(MathJax.Ajax.Require(file));
          }
          var data = MML.TEXCLASSNAMES[this.RANGES[i][2]];
          data = this.OPTABLE.infix[mo] = MML.mo.OPTYPES[data === "BIN" ? "BIN3" : data];
          return this.makeDef(data);
        }
      }
      return null;
    },
    makeDef: function (data) {
      if (data[2] == null) {data[2] = this.defaultDef.texClass}
      if (!data[3]) {data[3] = {}}
      var def = MathJax.Hub.Insert({},data[3]);
      def.lspace = this.SPACE[data[0]]; def.rspace = this.SPACE[data[1]];
      def.texClass = data[2];
      if (def.texClass === MML.TEXCLASS.REL &&
         (this.movablelimits || this.data.join("").match(/^[a-z]+$/i)))
             {def.texClass = MML.TEXCLASS.OP} // mark named operators as OP
      return def;
    },
    getForm: function () {
      var core = this, parent = this.parent, Parent = this.Parent();
      while (Parent && Parent.isEmbellished())
        {core = parent; parent = Parent.parent; Parent = Parent.Parent()}
      if (parent && parent.type === "mrow" && parent.NonSpaceLength() !== 1) {
        if (parent.FirstNonSpace() === core) {return MML.FORM.PREFIX}
        if (parent.LastNonSpace() === core) {return MML.FORM.POSTFIX}
      }
      return MML.FORM.INFIX;
    },
    isEmbellished: function () {return true},
    hasNewline: function () {return (this.Get("linebreak") === MML.LINEBREAK.NEWLINE)},
    CoreParent: function () {
      var parent = this;
      while (parent && parent.isEmbellished() &&
             parent.CoreMO() === this && !parent.isa(MML.math)) {parent = parent.Parent()}
      return parent;
    },
    CoreText: function (parent) {
      if (!parent) {return ""}
      if (parent.isEmbellished()) {return parent.CoreMO().data.join("")}
      while ((((parent.isa(MML.mrow) || parent.isa(MML.TeXAtom) ||
                parent.isa(MML.mstyle) || parent.isa(MML.mphantom)) &&
                parent.data.length === 1) || parent.isa(MML.munderover)) &&
                parent.data[0]) {parent = parent.data[0]}
      if (!parent.isToken) {return ""} else {return parent.data.join("")}
    },
    remapChars: {
      '*':"\u2217",
      '"':"\u2033",
      "\u00B0":"\u2218",
      "\u00B2":"2",
      "\u00B3":"3",
      "\u00B4":"\u2032",
      "\u00B9":"1"
    },
    remap: function (text,map) {
      text = text.replace(/-/g,"\u2212");
      if (map) {
        text = text.replace(/'/g,"\u2032").replace(/`/g,"\u2035");
        if (text.length === 1) {text = map[text]||text}
      }
      return text;
    },
    setTeXclass: function (prev) {
      var values = this.getValues("form","lspace","rspace","fence"); // sets useMMLspacing
      if (this.useMMLspacing) {this.texClass = MML.TEXCLASS.NONE; return this}
      if (values.fence && !this.texClass) {
        if (values.form === MML.FORM.PREFIX) {this.texClass = MML.TEXCLASS.OPEN}
        if (values.form === MML.FORM.POSTFIX) {this.texClass = MML.TEXCLASS.CLOSE}
      }
      this.texClass = this.Get("texClass");
      if (this.data.join("") === "\u2061") {
        // force previous node to be texClass OP, and skip this node
        if (prev) {prev.texClass = MML.TEXCLASS.OP; prev.fnOP = true}
        this.texClass = this.prevClass = MML.TEXCLASS.NONE;
        return prev;
      }
      return this.adjustTeXclass(prev);
    },
    adjustTeXclass: function (prev) {
      if (this.texClass === MML.TEXCLASS.NONE) {return prev}
      if (prev) {
        if (prev.autoOP && (this.texClass === MML.TEXCLASS.BIN ||
                            this.texClass === MML.TEXCLASS.REL))
          {prev.texClass = MML.TEXCLASS.ORD}
        this.prevClass = prev.texClass || MML.TEXCLASS.ORD;
        this.prevLevel = prev.Get("scriptlevel")
      } else {this.prevClass = MML.TEXCLASS.NONE}
      if (this.texClass === MML.TEXCLASS.BIN &&
            (this.prevClass === MML.TEXCLASS.NONE ||
             this.prevClass === MML.TEXCLASS.BIN ||
             this.prevClass === MML.TEXCLASS.OP ||
             this.prevClass === MML.TEXCLASS.REL ||
             this.prevClass === MML.TEXCLASS.OPEN ||
             this.prevClass === MML.TEXCLASS.PUNCT)) {
        this.texClass = MML.TEXCLASS.ORD;
      } else if (this.prevClass === MML.TEXCLASS.BIN &&
                   (this.texClass === MML.TEXCLASS.REL ||
                    this.texClass === MML.TEXCLASS.CLOSE ||
                    this.texClass === MML.TEXCLASS.PUNCT)) {
        prev.texClass = this.prevClass = MML.TEXCLASS.ORD;
      } else if (this.texClass === MML.TEXCLASS.BIN) {
        //
        // Check if node is the last one in its container since the rule
        // above only takes effect if there is a node that follows.
        //
        var child = this, parent = this.parent;
        while (parent && parent.parent && parent.isEmbellished() &&
              (parent.data.length === 1 ||
              (parent.type !== "mrow" && parent.Core() === child))) // handles msubsup and munderover
                 {child = parent; parent = parent.parent}
        if (parent.data[parent.data.length-1] === child) this.texClass = MML.TEXCLASS.ORD;
      }
      return this;
    }
  });
  
  MML.mtext = MML.mbase.Subclass({
    type: "mtext", isToken: true,
    isSpacelike: function () {return true},
    texClass: MML.TEXCLASS.ORD,
    defaults: {
      mathvariant: MML.INHERIT,
      mathsize: MML.INHERIT,
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT
    }
  });

  MML.mspace = MML.mbase.Subclass({
    type: "mspace", isToken: true,
    isSpacelike: function () {return true},
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      width: "0em",
      height: "0ex",
      depth: "0ex",
      linebreak: MML.LINEBREAK.AUTO
    },
    hasDimAttr: function () {
      return (this.hasValue("width") || this.hasValue("height") ||
              this.hasValue("depth"));
    },
    hasNewline: function () {
      // The MathML spec says that the linebreak attribute should be ignored
      // if any dimensional attribute is set.
      return (!this.hasDimAttr() &&
              this.Get("linebreak") === MML.LINEBREAK.NEWLINE);
    }
  });

  MML.ms = MML.mbase.Subclass({
    type: "ms", isToken: true,
    texClass: MML.TEXCLASS.ORD,
    defaults: {
      mathvariant: MML.INHERIT,
      mathsize: MML.INHERIT,
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT,
      lquote: '"',
      rquote: '"'
    }
  });

  MML.mglyph = MML.mbase.Subclass({
    type: "mglyph", isToken: true,
    texClass: MML.TEXCLASS.ORD,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      alt: "",
      src: "",
      width: MML.AUTO,
      height: MML.AUTO,
      valign: "0em"
    }
  });

  MML.mrow = MML.mbase.Subclass({
    type: "mrow",
    isSpacelike: MML.mbase.childrenSpacelike,
    inferred: false, notParent: false,
    isEmbellished: function () {
      var isEmbellished = false;
      for (var i = 0, m = this.data.length; i < m; i++) {
        if (this.data[i] == null) continue;
        if (this.data[i].isEmbellished()) {
          if (isEmbellished) {return false}
          isEmbellished = true; this.core = i;
        } else if (!this.data[i].isSpacelike()) {return false}
      }
      return isEmbellished;
    },
    NonSpaceLength: function () {
      var n = 0;
      for (var i = 0, m = this.data.length; i < m; i++)
        {if (this.data[i] && !this.data[i].isSpacelike()) {n++}}
      return n;
    },
    FirstNonSpace: function () {
      for (var i = 0, m = this.data.length; i < m; i++)
        {if (this.data[i] && !this.data[i].isSpacelike()) {return this.data[i]}}
      return null;
    },
    LastNonSpace: function () {
      for (var i = this.data.length-1; i >= 0; i--)
        {if (this.data[0] && !this.data[i].isSpacelike()) {return this.data[i]}}
      return null;
    },
    Core: function () {
      if (!(this.isEmbellished()) || typeof(this.core) === "undefined") {return this}
      return this.data[this.core];
    },
    CoreMO: function () {
      if (!(this.isEmbellished()) || typeof(this.core) === "undefined") {return this}
      return this.data[this.core].CoreMO();
    },
    toString: function () {
      if (this.inferred) {return '[' + this.data.join(',') + ']'}
      return this.SUPER(arguments).toString.call(this);
    },
    setTeXclass: function (prev) {
      var i, m = this.data.length;
      if ((this.open || this.close) && (!prev || !prev.fnOP)) {
        //
        // <mrow> came from \left...\right
        // so treat as subexpression (tex class INNER)
        //
        this.getPrevClass(prev); prev = null;
        for (i = 0; i < m; i++)
          {if (this.data[i]) {prev = this.data[i].setTeXclass(prev)}}
        if (!this.hasOwnProperty("texClass")) this.texClass = MML.TEXCLASS.INNER;
        return this;
      } else {
        //
        //  Normal <mrow>, so treat as
        //  thorugh mrow is not there
        //
        for (i = 0; i < m; i++)
          {if (this.data[i]) {prev = this.data[i].setTeXclass(prev)}}
        if (this.data[0]) {this.updateTeXclass(this.data[0])}
        return prev;
      }
    },
    getAnnotation: function (name) {
      if (this.data.length != 1) return null;
      return this.data[0].getAnnotation(name);
    }
  });

  MML.mfrac = MML.mbase.Subclass({
    type: "mfrac", num: 0, den: 1,
    linebreakContainer: true,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      linethickness: MML.LINETHICKNESS.MEDIUM,
      numalign: MML.ALIGN.CENTER,
      denomalign: MML.ALIGN.CENTER,
      bevelled: false
    },
    adjustChild_displaystyle: function (n) {return false},
    adjustChild_scriptlevel: function (n) {
      var level = this.Get("scriptlevel");
      if (!this.Get("displaystyle") || level > 0) {level++}
      return level;
    },
    adjustChild_texprimestyle: function (n) {
      if (n == this.den) {return true}
      return this.Get("texprimestyle");
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.msqrt = MML.mbase.Subclass({
    type: "msqrt",
    inferRow: true,
    linebreakContainer: true,
    texClass: MML.TEXCLASS.ORD,
    setTeXclass: MML.mbase.setSeparateTeXclasses,
    adjustChild_texprimestyle: function (n) {return true}
  });

  MML.mroot = MML.mbase.Subclass({
    type: "mroot",
    linebreakContainer: true,
    texClass: MML.TEXCLASS.ORD,
    adjustChild_displaystyle: function (n) {
      if (n === 1) {return false}
      return this.Get("displaystyle");
    },
    adjustChild_scriptlevel: function (n) {
      var level = this.Get("scriptlevel");
      if (n === 1) {level += 2}
      return level;
    },
    adjustChild_texprimestyle: function (n) {
      if (n === 0) {return true};
      return this.Get("texprimestyle");
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.mstyle = MML.mbase.Subclass({
    type: "mstyle",
    isSpacelike: MML.mbase.childrenSpacelike,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    inferRow: true,
    defaults: {
      scriptlevel: MML.INHERIT,
      displaystyle: MML.INHERIT,
      scriptsizemultiplier: Math.sqrt(1/2),
      scriptminsize: "8pt",
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      dir: MML.INHERIT,
      infixlinebreakstyle: MML.LINEBREAKSTYLE.BEFORE,
      decimalseparator: "."
    },
    adjustChild_scriptlevel: function (n) {
      var level = this.scriptlevel;
      if (level == null) {
        level = this.Get("scriptlevel");
      } else if (String(level).match(/^ *[-+]/)) {
        var LEVEL = this.Get("scriptlevel",null,true);
        level = LEVEL + parseInt(level);
      }
      return level;
    },
    inheritFromMe: true,
    noInherit: {
      mpadded: {width: true, height: true, depth: true, lspace: true, voffset: true},
      mtable:  {width: true, height: true, depth: true, align: true}
    },
    getRemoved: {fontfamily:"fontFamily", fontweight:"fontWeight", fontstyle:"fontStyle", fontsize:"fontSize"},
    setTeXclass: MML.mbase.setChildTeXclass
  });

  MML.merror = MML.mbase.Subclass({
    type: "merror",
    inferRow: true,
    linebreakContainer: true,
    texClass: MML.TEXCLASS.ORD
  });

  MML.mpadded = MML.mbase.Subclass({
    type: "mpadded",
    inferRow: true,
    isSpacelike: MML.mbase.childrenSpacelike,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      width: "",
      height: "",
      depth: "",
      lspace: 0,
      voffset: 0
    },
    setTeXclass: MML.mbase.setChildTeXclass
  });

  MML.mphantom = MML.mbase.Subclass({
    type: "mphantom",
    texClass: MML.TEXCLASS.ORD,
    inferRow: true,
    isSpacelike: MML.mbase.childrenSpacelike,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    setTeXclass: MML.mbase.setChildTeXclass
  });

  MML.mfenced = MML.mbase.Subclass({
    type: "mfenced",
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      open: '(',
      close: ')',
      separators: ','
    },
    addFakeNodes: function () {
      var values = this.getValues("open","close","separators");
      values.open = values.open.replace(/[ \t\n\r]/g,"");
      values.close = values.close.replace(/[ \t\n\r]/g,"");
      values.separators = values.separators.replace(/[ \t\n\r]/g,"");
      //
      //  Create a fake node for the open item
      //
      if (values.open !== "") {
        this.SetData("open",MML.mo(values.open).With({
          fence:true, form:MML.FORM.PREFIX, texClass:MML.TEXCLASS.OPEN
        }));
        //
        //  Clear flag for using MML spacing even though form is specified
        //
        this.data.open.useMMLspacing = 0;
      }
      //
      //  Create fake nodes for the separators
      //
      if (values.separators !== "") {
        while (values.separators.length < this.data.length)
          {values.separators += values.separators.charAt(values.separators.length-1)}
        for (var i = 1, m = this.data.length; i < m; i++) {
          if (this.data[i]) {
            this.SetData("sep"+i,MML.mo(values.separators.charAt(i-1)).With({separator:true}))
            this.data["sep"+i].useMMLspacing = 0;
          }
        }
      }
      //
      //  Create fake node for the close item
      //
      if (values.close !== "") {
        this.SetData("close",MML.mo(values.close).With({
          fence:true, form:MML.FORM.POSTFIX, texClass:MML.TEXCLASS.CLOSE
        }));
        //
        //  Clear flag for using MML spacing even though form is specified
        //
        this.data.close.useMMLspacing = 0;
      }
    },
    texClass: MML.TEXCLASS.OPEN,
    setTeXclass: function (prev) {
      this.addFakeNodes();
      this.getPrevClass(prev);
      if (this.data.open) {prev = this.data.open.setTeXclass(prev)}
      if (this.data[0]) {prev = this.data[0].setTeXclass(prev)}
      for (var i = 1, m = this.data.length; i < m; i++) {
        if (this.data["sep"+i]) {prev = this.data["sep"+i].setTeXclass(prev)}
        if (this.data[i]) {prev = this.data[i].setTeXclass(prev)}
      }
      if (this.data.close) {prev = this.data.close.setTeXclass(prev)}
      this.updateTeXclass(this.data.open);
      this.texClass = MML.TEXCLASS.INNER;
      return prev;
    }
  });

  MML.menclose = MML.mbase.Subclass({
    type: "menclose",
    inferRow: true,
    linebreakContainer: true,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      notation: MML.NOTATION.LONGDIV,
      texClass: MML.TEXCLASS.ORD
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.msubsup = MML.mbase.Subclass({
    type: "msubsup", base: 0, sub: 1, sup: 2,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      subscriptshift: "",
      superscriptshift: "",
      texClass: MML.AUTO
    },
    autoDefault: function (name) {
      if (name === "texClass")
        {return (this.isEmbellished() ? this.CoreMO().Get(name) : MML.TEXCLASS.ORD)}
      return 0;
    },
    adjustChild_displaystyle: function (n) {
      if (n > 0) {return false}
      return this.Get("displaystyle");
    },
    adjustChild_scriptlevel: function (n) {
      var level = this.Get("scriptlevel");
      if (n > 0) {level++}
      return level;
    },
    adjustChild_texprimestyle: function (n) {
      if (n === this.sub) {return true}
      return this.Get("texprimestyle");
    },
    setTeXclass: MML.mbase.setBaseTeXclasses
  });
  
  MML.msub = MML.msubsup.Subclass({type: "msub"});
  MML.msup = MML.msubsup.Subclass({type: "msup", sub:2, sup:1});
  MML.mmultiscripts = MML.msubsup.Subclass({
    type: "mmultiscripts",
    adjustChild_texprimestyle: function (n) {
      if (n % 2 === 1) {return true}
      return this.Get("texprimestyle");
    }
  });
  MML.mprescripts = MML.mbase.Subclass({type: "mprescripts"});
  MML.none = MML.mbase.Subclass({type: "none"});
  
  MML.munderover = MML.mbase.Subclass({
    type: "munderover",
    base: 0, under: 1, over: 2, sub: 1, sup: 2,
    ACCENTS: ["", "accentunder", "accent"],
    linebreakContainer: true,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      accent: MML.AUTO,
      accentunder: MML.AUTO,
      align: MML.ALIGN.CENTER,
      texClass: MML.AUTO,
      subscriptshift: "",  // when converted to msubsup by moveablelimits
      superscriptshift: "" // when converted to msubsup by moveablelimits
    },
    autoDefault: function (name) {
      if (name === "texClass")
        {return (this.isEmbellished() ? this.CoreMO().Get(name) : MML.TEXCLASS.ORD)}
      if (name === "accent" && this.data[this.over]) {return this.data[this.over].CoreMO().Get("accent")}
      if (name === "accentunder" && this.data[this.under]) {return this.data[this.under].CoreMO().Get("accent")}
      return false;
    },
    adjustChild_displaystyle: function (n) {
      if (n > 0) {return false}
      return this.Get("displaystyle");
    },
    adjustChild_scriptlevel: function (n) {
      var level = this.Get("scriptlevel");
      var force = (this.data[this.base] && !this.Get("displaystyle") &&
                   this.data[this.base].CoreMO().Get("movablelimits"));
      if (n == this.under && (force || !this.Get("accentunder"))) {level++}
      if (n == this.over  && (force || !this.Get("accent"))) {level++}
      return level;
    },
    adjustChild_texprimestyle: function (n) {
      if (n === this.base && this.data[this.over]) {return true}
      return this.Get("texprimestyle");
    },
    setTeXclass: MML.mbase.setBaseTeXclasses
  });
  
  MML.munder = MML.munderover.Subclass({type: "munder"});
  MML.mover = MML.munderover.Subclass({
    type: "mover", over: 1, under: 2, sup: 1, sub: 2,
    ACCENTS: ["", "accent", "accentunder"]
  });

  MML.mtable = MML.mbase.Subclass({
    type: "mtable",
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      align: MML.ALIGN.AXIS,
      rowalign: MML.ALIGN.BASELINE,
      columnalign: MML.ALIGN.CENTER,
      groupalign: "{left}",
      alignmentscope: true,
      columnwidth: MML.WIDTH.AUTO,
      width: MML.WIDTH.AUTO,
      rowspacing: "1ex",
      columnspacing: ".8em",
      rowlines: MML.LINES.NONE,
      columnlines: MML.LINES.NONE,
      frame: MML.LINES.NONE,
      framespacing: "0.4em 0.5ex",
      equalrows: false,
      equalcolumns: false,
      displaystyle: false,
      side: MML.SIDE.RIGHT,
      minlabelspacing: "0.8em",
      texClass: MML.TEXCLASS.ORD,
      useHeight: 1
    },
    adjustChild_displaystyle: function () {
      return (this.displaystyle != null ? this.displaystyle : this.defaults.displaystyle);
    },
    inheritFromMe: true,
    noInherit: {
      mover: {align: true},
      munder: {align: true},
      munderover: {align: true},
      mtable: {
        align: true, rowalign: true, columnalign: true, groupalign: true,
        alignmentscope: true, columnwidth: true, width: true, rowspacing: true,
        columnspacing: true, rowlines: true, columnlines: true, frame: true,
        framespacing: true, equalrows: true, equalcolumns: true, displaystyle: true,
        side: true, minlabelspacing: true, texClass: true, useHeight: 1
      }
    },
    linebreakContainer: true,
    Append: function () {
      for (var i = 0, m = arguments.length; i < m; i++) {
        if (!((arguments[i] instanceof MML.mtr) ||
              (arguments[i] instanceof MML.mlabeledtr))) {arguments[i] = MML.mtr(arguments[i])}
      }
      this.SUPER(arguments).Append.apply(this,arguments);
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.mtr = MML.mbase.Subclass({
    type: "mtr",
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      rowalign: MML.INHERIT,
      columnalign: MML.INHERIT,
      groupalign: MML.INHERIT
    },
    inheritFromMe: true,
    noInherit: {
      mrow: {rowalign: true, columnalign: true, groupalign: true},
      mtable: {rowalign: true, columnalign: true, groupalign: true}
    },
    linebreakContainer: true,
    Append: function () {
      for (var i = 0, m = arguments.length; i < m; i++) {
        if (!(arguments[i] instanceof MML.mtd)) {arguments[i] = MML.mtd(arguments[i])}
      }
      this.SUPER(arguments).Append.apply(this,arguments);
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.mtd = MML.mbase.Subclass({
    type: "mtd",
    inferRow: true,
    linebreakContainer: true,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      rowspan: 1,
      columnspan: 1,
      rowalign: MML.INHERIT,
      columnalign: MML.INHERIT,
      groupalign: MML.INHERIT
    },
    setTeXclass: MML.mbase.setSeparateTeXclasses
  });

  MML.maligngroup = MML.mbase.Subclass({
    type: "maligngroup",
    isSpacelike: function () {return true},
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      groupalign: MML.INHERIT
    },
    inheritFromMe: true,
    noInherit: {
      mrow: {groupalign: true},
      mtable: {groupalign: true}
    }
  });

  MML.malignmark = MML.mbase.Subclass({
    type: "malignmark",
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      edge: MML.SIDE.LEFT
    },
    isSpacelike: function () {return true}
  });

  MML.mlabeledtr = MML.mtr.Subclass({
    type: "mlabeledtr"
  });
  
  MML.maction = MML.mbase.Subclass({
    type: "maction",
    defaults: {
      mathbackground: MML.INHERIT,
      mathcolor: MML.INHERIT,
      actiontype: MML.ACTIONTYPE.TOGGLE,
      selection: 1
    },
    selected: function () {return this.data[this.Get("selection")-1] || MML.NULL},
    isEmbellished: function () {return this.selected().isEmbellished()},
    isSpacelike: function () {return this.selected().isSpacelike()},
    Core: function () {return this.selected().Core()},
    CoreMO: function () {return this.selected().CoreMO()},
    setTeXclass: function (prev) {
      if (this.Get("actiontype") === MML.ACTIONTYPE.TOOLTIP && this.data[1]) {
        // Make sure tooltip has proper spacing when typeset (see issue #412)
        this.data[1].setTeXclass();
      }
      var selected = this.selected();
      prev = selected.setTeXclass(prev);
      this.updateTeXclass(selected);
      return prev;
    }
  });
  
  MML.semantics = MML.mbase.Subclass({
    type: "semantics", notParent: true,
    isEmbellished: MML.mbase.childEmbellished,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    defaults: {
      definitionURL: null,
      encoding: null
    },
    setTeXclass: MML.mbase.setChildTeXclass,
    getAnnotation: function (name) {
      var encodingList = MathJax.Hub.config.MathMenu.semanticsAnnotations[name];
      if (encodingList) {
        for (var i = 0, m = this.data.length; i < m; i++) {
          var encoding = this.data[i].Get("encoding");
          if (encoding) {
            for (var j = 0, n = encodingList.length; j < n; j++) {
              if (encodingList[j] === encoding) return this.data[i];
            }
          }
        }
      }
      return null;
    }
  });
  MML.annotation = MML.mbase.Subclass({
    type: "annotation", isChars: true,
    linebreakContainer: true,
    defaults: {
      definitionURL: null,
      encoding: null,
      cd: "mathmlkeys",
      name: "",
      src: null
    }
  });
  MML["annotation-xml"] = MML.mbase.Subclass({
    type: "annotation-xml",
    linebreakContainer: true,
    defaults: {
      definitionURL: null,
      encoding: null,
      cd: "mathmlkeys",
      name: "",
      src: null
    }
  });

  MML.math = MML.mstyle.Subclass({
    type: "math",
    defaults: {
      mathvariant: MML.VARIANT.NORMAL,
      mathsize: MML.SIZE.NORMAL,
      mathcolor: "", // should be "black", but allow it to inherit from surrounding text
      mathbackground: MML.COLOR.TRANSPARENT,
      dir: "ltr",
      scriptlevel: 0,
      displaystyle: MML.AUTO,
      display: "inline",
      maxwidth: "",
      overflow: MML.OVERFLOW.LINEBREAK,
      altimg: "",
      'altimg-width': "",
      'altimg-height': "",
      'altimg-valign': "",
      alttext: "",
      cdgroup: "",
      scriptsizemultiplier: Math.sqrt(1/2),
      scriptminsize: "8px",    // should be 8pt, but that's too big
      infixlinebreakstyle: MML.LINEBREAKSTYLE.BEFORE,
      lineleading: "1ex",
      indentshift: "auto",     // use user configuration
      indentalign: MML.INDENTALIGN.AUTO,
      indentalignfirst: MML.INDENTALIGN.INDENTALIGN,
      indentshiftfirst: MML.INDENTSHIFT.INDENTSHIFT,
      indentalignlast:  MML.INDENTALIGN.INDENTALIGN,
      indentshiftlast:  MML.INDENTSHIFT.INDENTSHIFT,
      decimalseparator: ".",
      texprimestyle: false     // is it in TeX's C' style?
    },
    autoDefault: function (name) {
      if (name === "displaystyle") {return this.Get("display") === "block"}
      return "";
    },
    linebreakContainer: true,
    setTeXclass: MML.mbase.setChildTeXclass,
    getAnnotation: function (name) {
      if (this.data.length != 1) return null;
      return this.data[0].getAnnotation(name);
    }
  });
  
  MML.chars = MML.mbase.Subclass({
    type: "chars",
    Append: function () {this.data.push.apply(this.data,arguments)},
    value: function () {return this.data.join("")},
    toString: function () {return this.data.join("")}
  });
  
  MML.entity = MML.mbase.Subclass({
    type: "entity",
    Append: function () {this.data.push.apply(this.data,arguments)},
    value: function () {
      if (this.data[0].substr(0,2) === "#x") {return parseInt(this.data[0].substr(2),16)}
      else if (this.data[0].substr(0,1) === "#") {return parseInt(this.data[0].substr(1))}
      else {return 0}  // FIXME: look up named entities from table
    },
    toString: function () {
      var n = this.value();
      if (n <= 0xFFFF) {return String.fromCharCode(n)}
      n -= 0x10000;
      return String.fromCharCode((n>>10)+0xD800)
           + String.fromCharCode((n&0x3FF)+0xDC00);
    }
  });
  
  MML.xml = MML.mbase.Subclass({
    type: "xml",
    Init: function () {
      this.div = document.createElement("div");
      return this.SUPER(arguments).Init.apply(this,arguments);
    },
    Append: function () {
      for (var i = 0, m = arguments.length; i < m; i++) {
        var node = this.Import(arguments[i]);
        this.data.push(node);
        this.div.appendChild(node);
      }
    },
    Import: function (node) {
      if (document.importNode) {return document.importNode(node,true)}
      //
      //  IE < 9 doesn't have importNode, so fake it.
      //
      var nNode, i, m;
      if (node.nodeType === 1) { // ELEMENT_NODE
        nNode = document.createElement(node.nodeName);
        for (i = 0, m = node.attributes.length; i < m; i++) {
          var attribute = node.attributes[i];
          if (attribute.specified && attribute.nodeValue != null && attribute.nodeValue != '')
            {nNode.setAttribute(attribute.nodeName,attribute.nodeValue)}
          if (attribute.nodeName === "style") {nNode.style.cssText = attribute.nodeValue}
        }
        if (node.className) {nNode.className = node.className}
      } else if (node.nodeType === 3 || node.nodeType === 4) { // TEXT_NODE or CDATA_SECTION_NODE
        nNode = document.createTextNode(node.nodeValue);
      } else if (node.nodeType === 8) { // COMMENT_NODE
        nNode = document.createComment(node.nodeValue);
      } else {
        return document.createTextNode('');
      }
      for (i = 0, m = node.childNodes.length; i < m; i++)
        {nNode.appendChild(this.Import(node.childNodes[i]))}
      return nNode;
    },
    value: function () {return this.div},
    toString: function () {return this.div.innerHTML}
  });
  
  MML.TeXAtom = MML.mbase.Subclass({
    type: "texatom",
    linebreakContainer: true,
    inferRow: true, notParent: true,
    texClass: MML.TEXCLASS.ORD,
    Core: MML.mbase.childCore,
    CoreMO: MML.mbase.childCoreMO,
    isEmbellished: MML.mbase.childEmbellished,
    setTeXclass: function (prev) {
      this.data[0].setTeXclass();
      return this.adjustTeXclass(prev);
    },
    adjustTeXclass: MML.mo.prototype.adjustTeXclass
  });
  
  MML.NULL = MML.mbase().With({type:"null"});

  var TEXCLASS = MML.TEXCLASS;
  
  var MO = {
    ORD:        [0,0,TEXCLASS.ORD],
    ORD11:      [1,1,TEXCLASS.ORD],
    ORD21:      [2,1,TEXCLASS.ORD],
    ORD02:      [0,2,TEXCLASS.ORD],
    ORD55:      [5,5,TEXCLASS.ORD],
    OP:         [1,2,TEXCLASS.OP,{largeop: true, movablelimits: true, symmetric: true}],
    OPFIXED:    [1,2,TEXCLASS.OP,{largeop: true, movablelimits: true}],
    INTEGRAL:   [0,1,TEXCLASS.OP,{largeop: true, symmetric: true}],
    INTEGRAL2:  [1,2,TEXCLASS.OP,{largeop: true, symmetric: true}],
    BIN3:       [3,3,TEXCLASS.BIN],
    BIN4:       [4,4,TEXCLASS.BIN],
    BIN01:      [0,1,TEXCLASS.BIN],
    BIN5:       [5,5,TEXCLASS.BIN],
    TALLBIN:    [4,4,TEXCLASS.BIN,{stretchy: true}],
    BINOP:      [4,4,TEXCLASS.BIN,{largeop: true, movablelimits: true}],
    REL:        [5,5,TEXCLASS.REL],
    REL1:       [1,1,TEXCLASS.REL,{stretchy: true}],
    REL4:       [4,4,TEXCLASS.REL],
    RELSTRETCH: [5,5,TEXCLASS.REL,{stretchy: true}],
    RELACCENT:  [5,5,TEXCLASS.REL,{accent: true}],
    WIDEREL:    [5,5,TEXCLASS.REL,{accent: true, stretchy: true}],
    OPEN:       [0,0,TEXCLASS.OPEN,{fence: true, stretchy: true, symmetric: true}],
    CLOSE:      [0,0,TEXCLASS.CLOSE,{fence: true, stretchy: true, symmetric: true}],
    INNER:      [0,0,TEXCLASS.INNER],
    PUNCT:      [0,3,TEXCLASS.PUNCT],
    ACCENT:     [0,0,TEXCLASS.ORD,{accent: true}],
    WIDEACCENT: [0,0,TEXCLASS.ORD,{accent: true, stretchy: true}]
  };

  MML.mo.Augment({
    SPACE: [
      '0em',
      '0.1111em',
      '0.1667em',
      '0.2222em',
      '0.2667em',
      '0.3333em'
    ],
    RANGES: [
      [0x20,0x7F,TEXCLASS.REL,"BasicLatin"],
      [0xA0,0xFF,TEXCLASS.ORD,"Latin1Supplement"],
      [0x100,0x17F,TEXCLASS.ORD],
      [0x180,0x24F,TEXCLASS.ORD],
      [0x2B0,0x2FF,TEXCLASS.ORD,"SpacingModLetters"],
      [0x300,0x36F,TEXCLASS.ORD,"CombDiacritMarks"],
      [0x370,0x3FF,TEXCLASS.ORD,"GreekAndCoptic"],
      [0x1E00,0x1EFF,TEXCLASS.ORD],
      [0x2000,0x206F,TEXCLASS.PUNCT,"GeneralPunctuation"],
      [0x2070,0x209F,TEXCLASS.ORD],
      [0x20A0,0x20CF,TEXCLASS.ORD],
      [0x20D0,0x20FF,TEXCLASS.ORD,"CombDiactForSymbols"],
      [0x2100,0x214F,TEXCLASS.ORD,"LetterlikeSymbols"],
      [0x2150,0x218F,TEXCLASS.ORD],
      [0x2190,0x21FF,TEXCLASS.REL,"Arrows"],
      [0x2200,0x22FF,TEXCLASS.BIN,"MathOperators"],
      [0x2300,0x23FF,TEXCLASS.ORD,"MiscTechnical"],
      [0x2460,0x24FF,TEXCLASS.ORD],
      [0x2500,0x259F,TEXCLASS.ORD],
      [0x25A0,0x25FF,TEXCLASS.ORD,"GeometricShapes"],
      [0x2700,0x27BF,TEXCLASS.ORD,"Dingbats"],
      [0x27C0,0x27EF,TEXCLASS.ORD,"MiscMathSymbolsA"],
      [0x27F0,0x27FF,TEXCLASS.REL,"SupplementalArrowsA"],
      [0x2900,0x297F,TEXCLASS.REL,"SupplementalArrowsB"],
      [0x2980,0x29FF,TEXCLASS.ORD,"MiscMathSymbolsB"],
      [0x2A00,0x2AFF,TEXCLASS.BIN,"SuppMathOperators"],
      [0x2B00,0x2BFF,TEXCLASS.ORD,"MiscSymbolsAndArrows"],
      [0x1D400,0x1D7FF,TEXCLASS.ORD]
    ],
    OPTABLE: {
      prefix: {
        '\u2200': MO.ORD21,    // for all
        '\u2202': MO.ORD21,    // partial differential
        '\u2203': MO.ORD21,    // there exists
        '\u2207': MO.ORD21,    // nabla
        '\u220F': MO.OP,       // n-ary product
        '\u2210': MO.OP,       // n-ary coproduct
        '\u2211': MO.OP,       // n-ary summation
        '\u2212': MO.BIN01,    // minus sign
        '\u2213': MO.BIN01,    // minus-or-plus sign
        '\u221A': [1,1,TEXCLASS.ORD,{stretchy: true}], // square root
        '\u2220': MO.ORD,      // angle
        '\u222B': MO.INTEGRAL, // integral
        '\u222E': MO.INTEGRAL, // contour integral
        '\u22C0': MO.OP,       // n-ary logical and
        '\u22C1': MO.OP,       // n-ary logical or
        '\u22C2': MO.OP,       // n-ary intersection
        '\u22C3': MO.OP,       // n-ary union
        '\u2308': MO.OPEN,     // left ceiling
        '\u230A': MO.OPEN,     // left floor
        '\u27E8': MO.OPEN,     // mathematical left angle bracket
        '\u27EE': MO.OPEN,     // mathematical left flattened parenthesis
        '\u2A00': MO.OP,       // n-ary circled dot operator
        '\u2A01': MO.OP,       // n-ary circled plus operator
        '\u2A02': MO.OP,       // n-ary circled times operator
        '\u2A04': MO.OP,       // n-ary union operator with plus
        '\u2A06': MO.OP,       // n-ary square union operator
        '\u00AC': MO.ORD21,    // not sign
        '\u00B1': MO.BIN01,    // plus-minus sign
        '(': MO.OPEN,          // left parenthesis
        '+': MO.BIN01,         // plus sign
        '-': MO.BIN01,         // hyphen-minus
        '[': MO.OPEN,          // left square bracket
        '{': MO.OPEN,          // left curly bracket
        '|': MO.OPEN           // vertical line
      },
      postfix: {
        '!': [1,0,TEXCLASS.CLOSE], // exclamation mark
        '&': MO.ORD,           // ampersand
        '\u2032': MO.ORD02,    // prime
        '\u203E': MO.WIDEACCENT, // overline
        '\u2309': MO.CLOSE,    // right ceiling
        '\u230B': MO.CLOSE,    // right floor
        '\u23DE': MO.WIDEACCENT, // top curly bracket
        '\u23DF': MO.WIDEACCENT, // bottom curly bracket
        '\u266D': MO.ORD02,    // music flat sign
        '\u266E': MO.ORD02,    // music natural sign
        '\u266F': MO.ORD02,    // music sharp sign
        '\u27E9': MO.CLOSE,    // mathematical right angle bracket
        '\u27EF': MO.CLOSE,    // mathematical right flattened parenthesis
        '\u02C6': MO.WIDEACCENT, // modifier letter circumflex accent
        '\u02C7': MO.WIDEACCENT, // caron
        '\u02C9': MO.WIDEACCENT, // modifier letter macron
        '\u02CA': MO.ACCENT,   // modifier letter acute accent
        '\u02CB': MO.ACCENT,   // modifier letter grave accent
        '\u02D8': MO.ACCENT,   // breve
        '\u02D9': MO.ACCENT,   // dot above
        '\u02DC': MO.WIDEACCENT, // small tilde
        '\u0302': MO.WIDEACCENT, // combining circumflex accent
        '\u00A8': MO.ACCENT,   // diaeresis
        '\u00AF': MO.WIDEACCENT, // macron
        ')': MO.CLOSE,         // right parenthesis
        ']': MO.CLOSE,         // right square bracket
        '^': MO.WIDEACCENT,    // circumflex accent
        '_': MO.WIDEACCENT,    // low line
        '`': MO.ACCENT,        // grave accent
        '|': MO.CLOSE,         // vertical line
        '}': MO.CLOSE,         // right curly bracket
        '~': MO.WIDEACCENT     // tilde
      },
      infix: {
        '': MO.ORD,            // empty <mo>
        '%': [3,3,TEXCLASS.ORD], // percent sign
        '\u2022': MO.BIN4,     // bullet
        '\u2026': MO.INNER,    // horizontal ellipsis
        '\u2044': MO.TALLBIN,  // fraction slash
        '\u2061': MO.ORD,      // function application
        '\u2062': MO.ORD,      // invisible times
        '\u2063': [0,0,TEXCLASS.ORD,{linebreakstyle:"after", separator: true}], // invisible separator
        '\u2064': MO.ORD,      // invisible plus
        '\u2190': MO.WIDEREL,  // leftwards arrow
        '\u2191': MO.RELSTRETCH, // upwards arrow
        '\u2192': MO.WIDEREL,  // rightwards arrow
        '\u2193': MO.RELSTRETCH, // downwards arrow
        '\u2194': MO.WIDEREL,  // left right arrow
        '\u2195': MO.RELSTRETCH, // up down arrow
        '\u2196': MO.RELSTRETCH, // north west arrow
        '\u2197': MO.RELSTRETCH, // north east arrow
        '\u2198': MO.RELSTRETCH, // south east arrow
        '\u2199': MO.RELSTRETCH, // south west arrow
        '\u21A6': MO.WIDEREL,  // rightwards arrow from bar
        '\u21A9': MO.WIDEREL,  // leftwards arrow with hook
        '\u21AA': MO.WIDEREL,  // rightwards arrow with hook
        '\u21BC': MO.WIDEREL,  // leftwards harpoon with barb upwards
        '\u21BD': MO.WIDEREL,  // leftwards harpoon with barb downwards
        '\u21C0': MO.WIDEREL,  // rightwards harpoon with barb upwards
        '\u21C1': MO.WIDEREL,  // rightwards harpoon with barb downwards
        '\u21CC': MO.WIDEREL,  // rightwards harpoon over leftwards harpoon
        '\u21D0': MO.WIDEREL,  // leftwards double arrow
        '\u21D1': MO.RELSTRETCH, // upwards double arrow
        '\u21D2': MO.WIDEREL,  // rightwards double arrow
        '\u21D3': MO.RELSTRETCH, // downwards double arrow
        '\u21D4': MO.WIDEREL,  // left right double arrow
        '\u21D5': MO.RELSTRETCH, // up down double arrow
        '\u2208': MO.REL,      // element of
        '\u2209': MO.REL,      // not an element of
        '\u220B': MO.REL,      // contains as member
        '\u2212': MO.BIN4,     // minus sign
        '\u2213': MO.BIN4,     // minus-or-plus sign
        '\u2215': MO.TALLBIN,  // division slash
        '\u2216': MO.BIN4,     // set minus
        '\u2217': MO.BIN4,     // asterisk operator
        '\u2218': MO.BIN4,     // ring operator
        '\u2219': MO.BIN4,     // bullet operator
        '\u221D': MO.REL,      // proportional to
        '\u2223': MO.REL,      // divides
        '\u2225': MO.REL,      // parallel to
        '\u2227': MO.BIN4,     // logical and
        '\u2228': MO.BIN4,     // logical or
        '\u2229': MO.BIN4,     // intersection
        '\u222A': MO.BIN4,     // union
        '\u223C': MO.REL,      // tilde operator
        '\u2240': MO.BIN4,     // wreath product
        '\u2243': MO.REL,      // asymptotically equal to
        '\u2245': MO.REL,      // approximately equal to
        '\u2248': MO.REL,      // almost equal to
        '\u224D': MO.REL,      // equivalent to
        '\u2250': MO.REL,      // approaches the limit
        '\u2260': MO.REL,      // not equal to
        '\u2261': MO.REL,      // identical to
        '\u2264': MO.REL,      // less-than or equal to
        '\u2265': MO.REL,      // greater-than or equal to
        '\u226A': MO.REL,      // much less-than
        '\u226B': MO.REL,      // much greater-than
        '\u227A': MO.REL,      // precedes
        '\u227B': MO.REL,      // succeeds
        '\u2282': MO.REL,      // subset of
        '\u2283': MO.REL,      // superset of
        '\u2286': MO.REL,      // subset of or equal to
        '\u2287': MO.REL,      // superset of or equal to
        '\u228E': MO.BIN4,     // multiset union
        '\u2291': MO.REL,      // square image of or equal to
        '\u2292': MO.REL,      // square original of or equal to
        '\u2293': MO.BIN4,     // square cap
        '\u2294': MO.BIN4,     // square cup
        '\u2295': MO.BIN4,     // circled plus
        '\u2296': MO.BIN4,     // circled minus
        '\u2297': MO.BIN4,     // circled times
        '\u2298': MO.BIN4,     // circled division slash
        '\u2299': MO.BIN4,     // circled dot operator
        '\u22A2': MO.REL,      // right tack
        '\u22A3': MO.REL,      // left tack
        '\u22A4': MO.ORD55,    // down tack
        '\u22A5': MO.REL,      // up tack
        '\u22A8': MO.REL,      // true
        '\u22C4': MO.BIN4,     // diamond operator
        '\u22C5': MO.BIN4,     // dot operator
        '\u22C6': MO.BIN4,     // star operator
        '\u22C8': MO.REL,      // bowtie
        '\u22EE': MO.ORD55,    // vertical ellipsis
        '\u22EF': MO.INNER,    // midline horizontal ellipsis
        '\u22F1': [5,5,TEXCLASS.INNER], // down right diagonal ellipsis
        '\u25B3': MO.BIN4,     // white up-pointing triangle
        '\u25B5': MO.BIN4,     // white up-pointing small triangle
        '\u25B9': MO.BIN4,     // white right-pointing small triangle
        '\u25BD': MO.BIN4,     // white down-pointing triangle
        '\u25BF': MO.BIN4,     // white down-pointing small triangle
        '\u25C3': MO.BIN4,     // white left-pointing small triangle
        '\u2758': MO.REL,      // light vertical bar
        '\u27F5': MO.WIDEREL,  // long leftwards arrow
        '\u27F6': MO.WIDEREL,  // long rightwards arrow
        '\u27F7': MO.WIDEREL,  // long left right arrow
        '\u27F8': MO.WIDEREL,  // long leftwards double arrow
        '\u27F9': MO.WIDEREL,  // long rightwards double arrow
        '\u27FA': MO.WIDEREL,  // long left right double arrow
        '\u27FC': MO.WIDEREL,  // long rightwards arrow from bar
        '\u2A2F': MO.BIN4,     // vector or cross product
        '\u2A3F': MO.BIN4,     // amalgamation or coproduct
        '\u2AAF': MO.REL,      // precedes above single-line equals sign
        '\u2AB0': MO.REL,      // succeeds above single-line equals sign
        '\u00B1': MO.BIN4,     // plus-minus sign
        '\u00B7': MO.BIN4,     // middle dot
        '\u00D7': MO.BIN4,     // multiplication sign
        '\u00F7': MO.BIN4,     // division sign
        '*': MO.BIN3,          // asterisk
        '+': MO.BIN4,          // plus sign
        ',': [0,3,TEXCLASS.PUNCT,{linebreakstyle:"after", separator: true}], // comma
        '-': MO.BIN4,          // hyphen-minus
        '.': [3,3,TEXCLASS.ORD], // full stop
        '/': MO.ORD11,         // solidus
        ':': [1,2,TEXCLASS.REL], // colon
        ';': [0,3,TEXCLASS.PUNCT,{linebreakstyle:"after", separator: true}], // semicolon
        '<': MO.REL,           // less-than sign
        '=': MO.REL,           // equals sign
        '>': MO.REL,           // greater-than sign
        '?': [1,1,TEXCLASS.CLOSE], // question mark
        '\\': MO.ORD,          // reverse solidus
        '^': MO.ORD11,         // circumflex accent
        '_': MO.ORD11,         // low line
        '|': [2,2,TEXCLASS.ORD,{fence: true, stretchy: true, symmetric: true}], // vertical line
        '#': MO.ORD,           // #
        '$': MO.ORD,           // $
        //'\u002E': [0,3,TEXCLASS.PUNCT,{separator: true}], // \ldotp
        '\u02B9': MO.ORD,      // prime
        '\u0300': MO.ACCENT,   // \grave
        '\u0301': MO.ACCENT,   // \acute
        '\u0303': MO.WIDEACCENT, // \tilde
        '\u0304': MO.ACCENT,   // \bar
        '\u0306': MO.ACCENT,   // \breve
        '\u0307': MO.ACCENT,   // \dot
        '\u0308': MO.ACCENT,   // \ddot
        '\u030C': MO.ACCENT,   // \check
        '\u0332': MO.WIDEACCENT, // horizontal line
        '\u0338': MO.REL4,     // \not
        '\u2015': [0,0,TEXCLASS.ORD,{stretchy: true}], // horizontal line
        '\u2017': [0,0,TEXCLASS.ORD,{stretchy: true}], // horizontal line
        '\u2020': MO.BIN3,     // \dagger
        '\u2021': MO.BIN3,     // \ddagger
        '\u20D7': MO.ACCENT,   // \vec
        '\u2111': MO.ORD,      // \Im
        '\u2113': MO.ORD,      // \ell
        '\u2118': MO.ORD,      // \wp
        '\u211C': MO.ORD,      // \Re
        '\u2205': MO.ORD,      // \emptyset
        '\u221E': MO.ORD,      // \infty
        '\u2305': MO.BIN3,     // barwedge
        '\u2306': MO.BIN3,     // doublebarwedge
        '\u2322': MO.REL4,     // \frown
        '\u2323': MO.REL4,     // \smile
        '\u2329': MO.OPEN,     // langle
        '\u232A': MO.CLOSE,    // rangle
        '\u23AA': MO.ORD,      // \bracevert
        '\u23AF': [0,0,TEXCLASS.ORD,{stretchy: true}], // \underline
        '\u23B0': MO.OPEN,     // \lmoustache
        '\u23B1': MO.CLOSE,    // \rmoustache
        '\u2500': MO.ORD,      // horizontal line
        '\u25EF': MO.BIN3,     // \bigcirc
        '\u2660': MO.ORD,      // \spadesuit
        '\u2661': MO.ORD,      // \heartsuit
        '\u2662': MO.ORD,      // \diamondsuit
        '\u2663': MO.ORD,      // \clubsuit
        '\u3008': MO.OPEN,     // langle
        '\u3009': MO.CLOSE,    // rangle
        '\uFE37': MO.WIDEACCENT, // horizontal brace down
        '\uFE38': MO.WIDEACCENT  // horizontal brace up
      }
    }
  },{
    OPTYPES: MO
  });
  
  //
  //  These are not in the W3C table, but FF works this way,
  //  and it makes sense, so add it here
  //
  var OPTABLE = MML.mo.prototype.OPTABLE;
  OPTABLE.infix["^"] = MO.WIDEREL;
  OPTABLE.infix["_"] = MO.WIDEREL;
  OPTABLE.prefix["\u2223"] = MO.OPEN;
  OPTABLE.prefix["\u2225"] = MO.OPEN;
  OPTABLE.postfix["\u2223"] = MO.CLOSE;
  OPTABLE.postfix["\u2225"] = MO.CLOSE;
  
})(MathJax.ElementJax.mml);

MathJax.ElementJax.mml.loadComplete("jax.js");

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/extensions/MathEvents.js
 *
 *  Implements the event handlers needed by the output jax to perform
 *  menu, hover, and other events.
 *
 *  ---------------------------------------------------------------------
 *
 *  Copyright (c) 2011-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (HUB,HTML,AJAX,CALLBACK,LOCALE,OUTPUT,INPUT) {
  var VERSION = "2.7.2";

  var EXTENSION = MathJax.Extension;
  var ME = EXTENSION.MathEvents = {version: VERSION};

  var SETTINGS = HUB.config.menuSettings;

  var CONFIG = {
    hover: 500,              // time required to be considered a hover
    frame: {
      x: 3.5, y: 5,          // frame padding and
      bwidth: 1,             // frame border width (in pixels)
      bcolor: "#A6D",        // frame border color
      hwidth: "15px",        // haze width
      hcolor: "#83A"         // haze color
    },
    button: {
      x: -6, y: -3,          // menu button offsets
      wx: -2                 // button offset for full-width equations
    },
    fadeinInc: .2,           // increment for fade-in
    fadeoutInc: .05,         // increment for fade-out
    fadeDelay: 50,           // delay between fade-in or fade-out steps
    fadeoutStart: 400,       // delay before fade-out after mouseout
    fadeoutDelay: 15*1000,   // delay before automatic fade-out

    styles: {
      ".MathJax_Hover_Frame": {
        "border-radius": ".25em",                   // Opera 10.5 and IE9
        "-webkit-border-radius": ".25em",           // Safari and Chrome
        "-moz-border-radius": ".25em",              // Firefox
        "-khtml-border-radius": ".25em",            // Konqueror

        "box-shadow": "0px 0px 15px #83A",          // Opera 10.5 and IE9
        "-webkit-box-shadow": "0px 0px 15px #83A",  // Safari and Chrome
        "-moz-box-shadow": "0px 0px 15px #83A",     // Forefox
        "-khtml-box-shadow": "0px 0px 15px #83A",   // Konqueror

        border: "1px solid #A6D ! important",
        display: "inline-block", position:"absolute"
      },

      ".MathJax_Menu_Button .MathJax_Hover_Arrow": {
        position:"absolute",
        cursor:"pointer",
        display:"inline-block",
        border:"2px solid #AAA",
        "border-radius":"4px",
        "-webkit-border-radius": "4px",           // Safari and Chrome
        "-moz-border-radius": "4px",              // Firefox
        "-khtml-border-radius": "4px",            // Konqueror
        "font-family":"'Courier New',Courier",
        "font-size":"9px",
        color:"#F0F0F0"
      },
      ".MathJax_Menu_Button .MathJax_Hover_Arrow span": {
        display:"block",
        "background-color":"#AAA",
        border:"1px solid",
        "border-radius":"3px",
        "line-height":0,
        padding:"4px"
      },
      ".MathJax_Hover_Arrow:hover": {
        color:"white!important",
        border:"2px solid #CCC!important"
      },
      ".MathJax_Hover_Arrow:hover span": {
        "background-color":"#CCC!important"
      }
    }
  };


  //
  //  Common event-handling code
  //
  var EVENT = ME.Event = {

    LEFTBUTTON: 0,           // the event.button value for left button
    RIGHTBUTTON: 2,          // the event.button value for right button
    MENUKEY: "altKey",       // the event value for alternate context menu

    /*************************************************************/
    /*
     *  Enum element for key codes.
     */
    KEY: {
      RETURN: 13,
      ESCAPE: 27,
      SPACE: 32,
      LEFT: 37,
      UP: 38,
      RIGHT: 39,
      DOWN: 40
    },

    Mousedown: function (event) {return EVENT.Handler(event,"Mousedown",this)},
    Mouseup:   function (event) {return EVENT.Handler(event,"Mouseup",this)},
    Mousemove: function (event) {return EVENT.Handler(event,"Mousemove",this)},
    Mouseover: function (event) {return EVENT.Handler(event,"Mouseover",this)},
    Mouseout:  function (event) {return EVENT.Handler(event,"Mouseout",this)},
    Click:     function (event) {return EVENT.Handler(event,"Click",this)},
    DblClick:  function (event) {return EVENT.Handler(event,"DblClick",this)},
    Menu:      function (event) {return EVENT.Handler(event,"ContextMenu",this)},

    //
    //  Call the output jax's event handler or the zoom handler
    //
    Handler: function (event,type,math) {
      if (AJAX.loadingMathMenu) {return EVENT.False(event)}
      var jax = OUTPUT[math.jaxID];
      if (!event) {event = window.event}
      event.isContextMenu = (type === "ContextMenu");
      if (jax[type]) {return jax[type](event,math)}
      if (EXTENSION.MathZoom) {return EXTENSION.MathZoom.HandleEvent(event,type,math)}
    },

    //
    //  Try to cancel the event in every way we can
    //
    False: function (event) {
      if (!event) {event = window.event}
      if (event) {
        if (event.preventDefault) {event.preventDefault()} else {event.returnValue = false}
        if (event.stopPropagation) {event.stopPropagation()}
        event.cancelBubble = true;
      }
      return false;
    },

    //
    // Keydown event handler. Should only fire on Space key.
    //
    Keydown: function (event, math) {
      if (!event) event = window.event;
      if (event.keyCode === EVENT.KEY.SPACE) {
        EVENT.ContextMenu(event, this);
      };
    },

    //
    //  Load the contextual menu code, if needed, and post the menu
    //
    ContextMenu: function (event,math,force) {
      //
      //  Check if we are showing menus
      //
      var JAX = OUTPUT[math.jaxID], jax = JAX.getJaxFromMath(math);
      var show = (JAX.config.showMathMenu != null ? JAX : HUB).config.showMathMenu;
      if (!show || (SETTINGS.context !== "MathJax" && !force)) return;

      //
      //  Remove selections, remove hover fades
      //
      if (ME.msieEventBug) {event = window.event || event}
      EVENT.ClearSelection(); HOVER.ClearHoverTimer();
      if (jax.hover) {
        if (jax.hover.remove) {clearTimeout(jax.hover.remove); delete jax.hover.remove}
        jax.hover.nofade = true;
      }

      //
      //  If the menu code is loaded,
      //    Check if localization needs loading;
      //    If not, post the menu, and return.
      //    Otherwise wait for the localization to load
      //  Otherwse load the menu code.
      //  Try again after the file is loaded.
      //
      var MENU = MathJax.Menu; var load, fn;
      if (MENU) {
        if (MENU.loadingDomain) {return EVENT.False(event)}
        load = LOCALE.loadDomain("MathMenu");
        if (!load) {
          MENU.jax = jax;
          var source = MENU.menu.Find("Show Math As").submenu;
          source.items[0].name = jax.sourceMenuTitle;
          source.items[0].format = (jax.sourceMenuFormat||"MathML");
          source.items[1].name = INPUT[jax.inputJax].sourceMenuTitle;
          source.items[5].disabled = !INPUT[jax.inputJax].annotationEncoding;

          //
          // Try and find each known annotation format and enable the menu
          // items accordingly.
          //
          var annotations = source.items[2]; annotations.disabled = true;
          var annotationItems = annotations.submenu.items;
          annotationList = MathJax.Hub.Config.semanticsAnnotations;
          for (var i = 0, m = annotationItems.length; i < m; i++) {
            var name = annotationItems[i].name[1]
            if (jax.root && jax.root.getAnnotation(name) !== null) {
              annotations.disabled = false;
              annotationItems[i].hidden = false;
            } else {
              annotationItems[i].hidden = true;
            }
          }

          var MathPlayer = MENU.menu.Find("Math Settings","MathPlayer");
          MathPlayer.hidden = !(jax.outputJax === "NativeMML" && HUB.Browser.hasMathPlayer);
          return MENU.menu.Post(event);
        }
        MENU.loadingDomain = true;
        fn = function () {delete MENU.loadingDomain};
      } else {
        if (AJAX.loadingMathMenu) {return EVENT.False(event)}
        AJAX.loadingMathMenu = true;
        load = AJAX.Require("[MathJax]/extensions/MathMenu.js");
        fn = function () {
          delete AJAX.loadingMathMenu;
          if (!MathJax.Menu) {MathJax.Menu = {}}
        }
      }
      var ev = {
        pageX:event.pageX, pageY:event.pageY,
        clientX:event.clientX, clientY:event.clientY
      };
      CALLBACK.Queue(
        load, fn, // load the file and delete the marker when done
        ["ContextMenu",EVENT,ev,math,force]  // call this function again
      );
      return EVENT.False(event);
    },

    //
    //  Mousedown handler for alternate means of accessing menu
    //
    AltContextMenu: function (event,math) {
      var JAX = OUTPUT[math.jaxID];
      var show = (JAX.config.showMathMenu != null ? JAX : HUB).config.showMathMenu;
      if (show) {
        show = (JAX.config.showMathMenuMSIE != null ? JAX : HUB).config.showMathMenuMSIE;
        if (SETTINGS.context === "MathJax" && !SETTINGS.mpContext && show) {
          if (!ME.noContextMenuBug || event.button !== EVENT.RIGHTBUTTON) return;
        } else {
          if (!event[EVENT.MENUKEY] || event.button !== EVENT.LEFTBUTTON) return;
        }
        return JAX.ContextMenu(event,math,true);
      }
    },

    ClearSelection: function () {
      if (ME.safariContextMenuBug) {setTimeout("window.getSelection().empty()",0)}
      if (document.selection) {setTimeout("document.selection.empty()",0)}
    },

    getBBox: function (span) {
      span.appendChild(ME.topImg);
      var h = ME.topImg.offsetTop, d = span.offsetHeight-h, w = span.offsetWidth;
      span.removeChild(ME.topImg);
      return {w:w, h:h, d:d};
    }

  };

  //
  //  Handle hover "discoverability"
  //
  var HOVER = ME.Hover = {

    //
    //  Check if we are moving from a non-MathJax element to a MathJax one
    //  and either start fading in again (if it is fading out) or start the
    //  timer for the hover
    //
    Mouseover: function (event,math) {
      if (SETTINGS.discoverable || SETTINGS.zoom === "Hover") {
        var from = event.fromElement || event.relatedTarget,
            to   = event.toElement   || event.target;
        if (from && to && (HUB.isMathJaxNode(from) !== HUB.isMathJaxNode(to) ||
                           HUB.getJaxFor(from) !== HUB.getJaxFor(to))) {
          var jax = this.getJaxFromMath(math);
          if (jax.hover) {HOVER.ReHover(jax)} else {HOVER.HoverTimer(jax,math)}
          return EVENT.False(event);
        }
      }
    },
    //
    //  Check if we are moving from a MathJax element to a non-MathJax one
    //  and either start fading out, or clear the timer if we haven't
    //  hovered yet
    //
    Mouseout: function (event,math) {
      if (SETTINGS.discoverable || SETTINGS.zoom === "Hover") {
        var from = event.fromElement || event.relatedTarget,
            to   = event.toElement   || event.target;
        if (from && to && (HUB.isMathJaxNode(from) !== HUB.isMathJaxNode(to) ||
                           HUB.getJaxFor(from) !== HUB.getJaxFor(to))) {
          var jax = this.getJaxFromMath(math);
          if (jax.hover) {HOVER.UnHover(jax)} else {HOVER.ClearHoverTimer()}
          return EVENT.False(event);
        }
      }
    },
    //
    //  Restart hover timer if the mouse moves
    //
    Mousemove: function (event,math) {
      if (SETTINGS.discoverable || SETTINGS.zoom === "Hover") {
        var jax = this.getJaxFromMath(math); if (jax.hover) return;
        if (HOVER.lastX == event.clientX && HOVER.lastY == event.clientY) return;
        HOVER.lastX = event.clientX; HOVER.lastY = event.clientY;
        HOVER.HoverTimer(jax,math);
        return EVENT.False(event);
      }
    },

    //
    //  Clear the old timer and start a new one
    //
    HoverTimer: function (jax,math) {
      this.ClearHoverTimer();
      this.hoverTimer = setTimeout(CALLBACK(["Hover",this,jax,math]),CONFIG.hover);
    },
    ClearHoverTimer: function () {
      if (this.hoverTimer) {clearTimeout(this.hoverTimer); delete this.hoverTimer}
    },

    //
    //  Handle putting up the hover frame
    //
    Hover: function (jax,math) {
      //
      //  Check if Zoom handles the hover event
      //
      if (EXTENSION.MathZoom && EXTENSION.MathZoom.Hover({},math)) return;
      //
      //  Get the hover data
      //
      var JAX = OUTPUT[jax.outputJax],
          span = JAX.getHoverSpan(jax,math),
          bbox = JAX.getHoverBBox(jax,span,math),
          show = (JAX.config.showMathMenu != null ? JAX : HUB).config.showMathMenu;
      var dx = CONFIG.frame.x, dy = CONFIG.frame.y, dd = CONFIG.frame.bwidth;  // frame size
      if (ME.msieBorderWidthBug) {dd = 0}
      jax.hover = {opacity:0, id:jax.inputID+"-Hover"};
      //
      //  The frame and menu button
      //
      var frame = HTML.Element("span",{
         id:jax.hover.id, isMathJax: true,
         style:{display:"inline-block", width:0, height:0, position:"relative"}
        },[["span",{
          className:"MathJax_Hover_Frame", isMathJax: true,
          style:{
            display:"inline-block", position:"absolute",
            top:this.Px(-bbox.h-dy-dd-(bbox.y||0)), left:this.Px(-dx-dd+(bbox.x||0)),
            width:this.Px(bbox.w+2*dx), height:this.Px(bbox.h+bbox.d+2*dy),
            opacity:0, filter:"alpha(opacity=0)"
          }}
        ]]
      );
      var button = HTML.Element("span",{
         isMathJax: true, id:jax.hover.id+"Menu", className:"MathJax_Menu_Button",
         style:{display:"inline-block", "z-index": 1, width:0, height:0, position:"relative"}
        },[["span",{
            className: "MathJax_Hover_Arrow", isMathJax: true, math: math,
            onclick: this.HoverMenu, jax:JAX.id,
            style: {
              left:this.Px(bbox.w+dx+dd+(bbox.x||0)+CONFIG.button.x),
              top:this.Px(-bbox.h-dy-dd-(bbox.y||0)-CONFIG.button.y),
              opacity:0, filter:"alpha(opacity=0)"
            }
          },[["span",{isMathJax:true},"\u25BC"]]]]
      );
      if (bbox.width) {
        frame.style.width = button.style.width = bbox.width;
        frame.style.marginRight = button.style.marginRight = "-"+bbox.width;
        frame.firstChild.style.width = bbox.width;
        button.firstChild.style.left = "";
        button.firstChild.style.right = this.Px(CONFIG.button.wx);
      }
      //
      //  Add the frame and button
      //
      span.parentNode.insertBefore(frame,span);
      if (show) {span.parentNode.insertBefore(button,span)}
      if (span.style) {span.style.position = "relative"} // so math is on top of hover frame
      //
      //  Start the hover fade-in
      //
      this.ReHover(jax);
    },
    //
    //  Restart the hover fade in and fade-out timers
    //
    ReHover: function (jax) {
      if (jax.hover.remove) {clearTimeout(jax.hover.remove)}
      jax.hover.remove = setTimeout(CALLBACK(["UnHover",this,jax]),CONFIG.fadeoutDelay);
      this.HoverFadeTimer(jax,CONFIG.fadeinInc);
    },
    //
    //  Start the fade-out
    //
    UnHover: function (jax) {
      if (!jax.hover.nofade) {this.HoverFadeTimer(jax,-CONFIG.fadeoutInc,CONFIG.fadeoutStart)}
    },
    //
    //  Handle the fade-in and fade-out
    //
    HoverFade: function (jax) {
      delete jax.hover.timer;
      jax.hover.opacity = Math.max(0,Math.min(1,jax.hover.opacity + jax.hover.inc));
      jax.hover.opacity = Math.floor(1000*jax.hover.opacity)/1000;
      var frame = document.getElementById(jax.hover.id),
          button = document.getElementById(jax.hover.id+"Menu");
      frame.firstChild.style.opacity = jax.hover.opacity;
      frame.firstChild.style.filter = "alpha(opacity="+Math.floor(100*jax.hover.opacity)+")";
      if (button) {
        button.firstChild.style.opacity = jax.hover.opacity;
        button.firstChild.style.filter = frame.style.filter;
      }
      if (jax.hover.opacity === 1) {return}
      if (jax.hover.opacity > 0) {this.HoverFadeTimer(jax,jax.hover.inc); return}
      frame.parentNode.removeChild(frame);
      if (button) {button.parentNode.removeChild(button)}
      if (jax.hover.remove) {clearTimeout(jax.hover.remove)}
      delete jax.hover;
    },
    //
    //  Set the fade to in or out (via inc) and start the timer, if needed
    //
    HoverFadeTimer: function (jax,inc,delay) {
      jax.hover.inc = inc;
      if (!jax.hover.timer) {
        jax.hover.timer = setTimeout(CALLBACK(["HoverFade",this,jax]),(delay||CONFIG.fadeDelay));
      }
    },

    //
    //  Handle a click on the menu button
    //
    HoverMenu: function (event) {
      if (!event) {event = window.event}
      return OUTPUT[this.jax].ContextMenu(event,this.math,true);
    },

    //
    //  Clear all hover timers
    //
    ClearHover: function (jax) {
      if (jax.hover.remove) {clearTimeout(jax.hover.remove)}
      if (jax.hover.timer)  {clearTimeout(jax.hover.timer)}
      HOVER.ClearHoverTimer();
      delete jax.hover;
    },

    //
    //  Make a measurement in pixels
    //
    Px: function (m) {
      if (Math.abs(m) < .006) {return "0px"}
      return m.toFixed(2).replace(/\.?0+$/,"") + "px";
    },

    //
    //  Preload images so they show up with the menu
    //
    getImages: function () {
      if (SETTINGS.discoverable) {
        var menu = new Image();
        menu.src = CONFIG.button.src;
      }
    }

  };

  //
  //  Handle touch events.
  //
  //  Use double-tap-and-hold as a replacement for context menu event.
  //  Use double-tap as a replacement for double click.
  //
  var TOUCH = ME.Touch = {

    last: 0,          // time of last tap event
    delay: 500,       // delay time for double-click

    //
    //  Check if this is a double-tap, and if so, start the timer
    //  for the double-tap and hold (to trigger the contextual menu)
    //
    start: function (event) {
      var now = new Date().getTime();
      var dblTap = (now - TOUCH.last < TOUCH.delay && TOUCH.up);
      TOUCH.last = now; TOUCH.up = false;
      if (dblTap) {
        TOUCH.timeout = setTimeout(TOUCH.menu,TOUCH.delay,event,this);
        event.preventDefault();
      }
    },

    //
    //  Check if there is a timeout pending, i.e., we have a
    //  double-tap and were waiting to see if it is held long
    //  enough for the menu.  Since we got the end before the
    //  timeout, it is a double-click, not a double-tap-and-hold.
    //  Prevent the default action and issue a double click.
    //
    end: function (event) {
      var now = new Date().getTime();
      TOUCH.up = (now - TOUCH.last < TOUCH.delay);
      if (TOUCH.timeout) {
        clearTimeout(TOUCH.timeout);
        delete TOUCH.timeout; TOUCH.last = 0; TOUCH.up = false;
        event.preventDefault();
        return EVENT.Handler((event.touches[0]||event.touch),"DblClick",this);
      }
    },

    //
    //  If the timeout passes without an end event, we issue
    //  the contextual menu event.
    //
    menu: function (event,math) {
      delete TOUCH.timeout; TOUCH.last = 0; TOUCH.up = false;
      return EVENT.Handler((event.touches[0]||event.touch),"ContextMenu",math);
    }

  };

  /*
   * //
   * //  Mobile screens are small, so use larger version of arrow
   * //
   * if (HUB.Browser.isMobile) {
   *   var arrow = CONFIG.styles[".MathJax_Hover_Arrow"];
   *   arrow.width = "25px"; arrow.height = "18px";
   *   CONFIG.button.x = -6;
   * }
   */

  //
  //  Set up browser-specific values
  //
  HUB.Browser.Select({
    MSIE: function (browser) {
      var mode = (document.documentMode || 0);
      var isIE8 = browser.versionAtLeast("8.0");
      ME.msieBorderWidthBug = (document.compatMode === "BackCompat");  // borders are inside offsetWidth/Height
      ME.msieEventBug = browser.isIE9;           // must get event from window even though event is passed
      ME.msieAlignBug = (!isIE8 || mode < 8);    // inline-block spans don't rest on baseline
      if (mode < 9) {EVENT.LEFTBUTTON = 1}       // IE < 9 has wrong event.button values
    },
    Safari: function (browser) {
      ME.safariContextMenuBug = true;  // selection can be started by contextmenu event
    },
    Opera: function (browser) {
      ME.operaPositionBug = true;      // position is wrong unless border is used
    },
    Konqueror: function (browser) {
      ME.noContextMenuBug = true;      // doesn't produce contextmenu event
    }
  });

  //
  //  Used in measuring zoom and hover positions
  //
  ME.topImg = (ME.msieAlignBug ?
    HTML.Element("img",{style:{width:0,height:0,position:"relative"},src:"about:blank"}) :
    HTML.Element("span",{style:{width:0,height:0,display:"inline-block"}})
  );
  if (ME.operaPositionBug) {ME.topImg.style.border="1px solid"}

  //
  //  Get configuration from user
  //
  ME.config = CONFIG = HUB.CombineConfig("MathEvents",CONFIG);
  var SETFRAME = function () {
    var haze = CONFIG.styles[".MathJax_Hover_Frame"];
    haze.border = CONFIG.frame.bwidth+"px solid "+CONFIG.frame.bcolor+" ! important";
    haze["box-shadow"] = haze["-webkit-box-shadow"] =
      haze["-moz-box-shadow"] = haze["-khtml-box-shadow"] =
        "0px 0px "+CONFIG.frame.hwidth+" "+CONFIG.frame.hcolor;
  };

  //
  //  Queue the events needed for startup
  //
  CALLBACK.Queue(
    HUB.Register.StartupHook("End Config",{}), // wait until config is complete
    [SETFRAME],
    ["getImages",HOVER],
    ["Styles",AJAX,CONFIG.styles],
    ["Post",HUB.Startup.signal,"MathEvents Ready"],
    ["loadComplete",AJAX,"[MathJax]/extensions/MathEvents.js"]
  );

})(MathJax.Hub,MathJax.HTML,MathJax.Ajax,MathJax.Callback,
   MathJax.Localization,MathJax.OutputJax,MathJax.InputJax);

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/extensions/tex2jax.js
 *  
 *  Implements the TeX to Jax preprocessor that locates TeX code
 *  within the text of a document and replaces it with SCRIPT tags
 *  for processing by MathJax.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2009-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Extension.tex2jax = {
  version: "2.7.2",
  config: {
    inlineMath: [              // The start/stop pairs for in-line math
//    ['$','$'],               //  (comment out any you don't want, or add your own, but
      ['\\(','\\)']            //  be sure that you don't have an extra comma at the end)
    ],

    displayMath: [             // The start/stop pairs for display math
      ['$$','$$'],             //  (comment out any you don't want, or add your own, but
      ['\\[','\\]']            //  be sure that you don't have an extra comma at the end)
    ],

    balanceBraces: true,       // determines whether tex2jax requires braces to be
                               // balanced within math delimiters (allows for nested
                               // dollar signs).  Set to false to get pre-v2.0 compatibility.

    skipTags: ["script","noscript","style","textarea","pre","code","annotation","annotation-xml"],
                               // The names of the tags whose contents will not be
                               // scanned for math delimiters

    ignoreClass: "tex2jax_ignore",    // the class name of elements whose contents should
                                      // NOT be processed by tex2jax.  Note that this
                                      // is a regular expression, so be sure to quote any
                                      // regexp special characters

    processClass: "tex2jax_process",  // the class name of elements whose contents SHOULD
                                      // be processed when they appear inside ones that
                                      // are ignored.  Note that this is a regular expression,
                                      // so be sure to quote any regexp special characters

    processEscapes: false,     // set to true to allow \$ to produce a dollar without
                               //   starting in-line math mode

    processEnvironments: true, // set to true to process \begin{xxx}...\end{xxx} outside
                               //   of math mode, false to prevent that

    processRefs: true,         // set to true to process \ref{...} outside of math mode


    preview: "TeX"             // set to "none" to not insert MathJax_Preview spans
                               //   or set to an array specifying an HTML snippet
                               //   to use the same preview for every equation.

  },
  
  //
  //  Tags to ignore when searching for TeX in the page
  //
  ignoreTags: {
    br: (MathJax.Hub.Browser.isMSIE && document.documentMode < 9 ? "\n" : " "),
    wbr: "",
    "#comment": ""
  },
  
  PreProcess: function (element) {
    if (!this.configured) {
      this.config = MathJax.Hub.CombineConfig("tex2jax",this.config);
      if (this.config.Augment) {MathJax.Hub.Insert(this,this.config.Augment)}
      if (typeof(this.config.previewTeX) !== "undefined" && !this.config.previewTeX)
        {this.config.preview = "none"} // backward compatibility for previewTeX parameter
      this.configured = true;
    }
    if (typeof(element) === "string") {element = document.getElementById(element)}
    if (!element) {element = document.body}
    if (this.createPatterns()) {this.scanElement(element,element.nextSibling)}
  },
  
  createPatterns: function () {
    var starts = [], parts = [], i, m, config = this.config;
    this.match = {};
    for (i = 0, m = config.inlineMath.length; i < m; i++) {
      starts.push(this.patternQuote(config.inlineMath[i][0]));
      this.match[config.inlineMath[i][0]] = {
        mode: "",
        end: config.inlineMath[i][1],
        pattern: this.endPattern(config.inlineMath[i][1])
      };
    }
    for (i = 0, m = config.displayMath.length; i < m; i++) {
      starts.push(this.patternQuote(config.displayMath[i][0]));
      this.match[config.displayMath[i][0]] = {
        mode: "; mode=display",
        end: config.displayMath[i][1],
        pattern: this.endPattern(config.displayMath[i][1])
      };
    }
    if (starts.length) {parts.push(starts.sort(this.sortLength).join("|"))}
    if (config.processEnvironments) {parts.push("\\\\begin\\{([^}]*)\\}")}
    if (config.processEscapes)      {parts.push("\\\\*\\\\\\\$")}
    if (config.processRefs)         {parts.push("\\\\(eq)?ref\\{[^}]*\\}")}
    this.start = new RegExp(parts.join("|"),"g");
    this.skipTags = new RegExp("^("+config.skipTags.join("|")+")$","i");
    var ignore = [];
    if (MathJax.Hub.config.preRemoveClass) {ignore.push(MathJax.Hub.config.preRemoveClass)};
    if (config.ignoreClass) {ignore.push(config.ignoreClass)}
    this.ignoreClass = (ignore.length ? new RegExp("(^| )("+ignore.join("|")+")( |$)") : /^$/);
    this.processClass = new RegExp("(^| )("+config.processClass+")( |$)");
    return (parts.length > 0);
  },
  
  patternQuote: function (s) {return s.replace(/([\^$(){}+*?\-|\[\]\:\\])/g,'\\$1')},
  
  endPattern: function (end) {
    return new RegExp(this.patternQuote(end)+"|\\\\.|[{}]","g");
  },
  
  sortLength: function (a,b) {
    if (a.length !== b.length) {return b.length - a.length}
    return (a == b ? 0 : (a < b ? -1 : 1));
  },
  
  scanElement: function (element,stop,ignore) {
    var cname, tname, ignoreChild, process;
    while (element && element != stop) {
      if (element.nodeName.toLowerCase() === '#text') {
        if (!ignore) {element = this.scanText(element)}
      } else {
        cname = (typeof(element.className) === "undefined" ? "" : element.className);
        tname = (typeof(element.tagName)   === "undefined" ? "" : element.tagName);
        if (typeof(cname) !== "string") {cname = String(cname)} // jsxgraph uses non-string class names!
        process = this.processClass.exec(cname);
        if (element.firstChild && !cname.match(/(^| )MathJax/) &&
             (process || !this.skipTags.exec(tname))) {
          ignoreChild = (ignore || this.ignoreClass.exec(cname)) && !process;
          this.scanElement(element.firstChild,stop,ignoreChild);
        }
      }
      if (element) {element = element.nextSibling}
    }
  },
  
  scanText: function (element) {
    if (element.nodeValue.replace(/\s+/,'') == '') {return element}
    var match, prev;
    this.search = {start: true};
    this.pattern = this.start;
    while (element) {
      this.pattern.lastIndex = 0;
      while (element && element.nodeName.toLowerCase() === '#text' &&
            (match = this.pattern.exec(element.nodeValue))) {
        if (this.search.start) {element = this.startMatch(match,element)}
                          else {element = this.endMatch(match,element)}
      }
      if (this.search.matched) {element = this.encloseMath(element)}
      if (element) {
        do {prev = element; element = element.nextSibling}
          while (element && this.ignoreTags[element.nodeName.toLowerCase()] != null);
        if (!element || element.nodeName !== '#text')
          {return (this.search.close ? this.prevEndMatch() : prev)}
      }
    }
    return element;
  },
  
  startMatch: function (match,element) {
    var delim = this.match[match[0]];
    if (delim != null) {                              // a start delimiter
      this.search = {
        end: delim.end, mode: delim.mode, pcount: 0,
        open: element, olen: match[0].length, opos: this.pattern.lastIndex - match[0].length
      };
      this.switchPattern(delim.pattern);
    } else if (match[0].substr(0,6) === "\\begin") {  // \begin{...}
      this.search = {
        end: "\\end{"+match[1]+"}", mode: "; mode=display", pcount: 0,
        open: element, olen: 0, opos: this.pattern.lastIndex - match[0].length,
        isBeginEnd: true
      };
      this.switchPattern(this.endPattern(this.search.end));
    } else if (match[0].substr(0,4) === "\\ref" || match[0].substr(0,6) === "\\eqref") {
      this.search = {
        mode: "", end: "", open: element, pcount: 0,
        olen: 0, opos: this.pattern.lastIndex - match[0].length
      }
      return this.endMatch([""],element);
    } else {                                         // escaped dollar signs
      // put $ in a span so it doesn't get processed again
      // split off backslashes so they don't get removed later
      var slashes = match[0].substr(0,match[0].length-1), n, span;
      if (slashes.length % 2 === 0) {span = [slashes.replace(/\\\\/g,"\\")]; n = 1}
        else {span = [slashes.substr(1).replace(/\\\\/g,"\\"),"$"]; n = 0}
      span = MathJax.HTML.Element("span",null,span);
      var text = MathJax.HTML.TextNode(element.nodeValue.substr(0,match.index));
      element.nodeValue = element.nodeValue.substr(match.index + match[0].length - n);
      element.parentNode.insertBefore(span,element);
      element.parentNode.insertBefore(text,span);
      this.pattern.lastIndex = n;
    }
    return element;
  },
  
  endMatch: function (match,element) {
    var search = this.search;
    if (match[0] == search.end) {
      if (!search.close || search.pcount === 0) {
        search.close = element;
        search.cpos = this.pattern.lastIndex;
        search.clen = (search.isBeginEnd ? 0 : match[0].length);
      }
      if (search.pcount === 0) {
        search.matched = true;
        element = this.encloseMath(element);
        this.switchPattern(this.start);
      }
    }
    else if (match[0] === "{") {search.pcount++}
    else if (match[0] === "}" && search.pcount) {search.pcount--}
    return element;
  },
  prevEndMatch: function () {
    this.search.matched = true;
    var element = this.encloseMath(this.search.close);
    this.switchPattern(this.start);
    return element;
  },
  
  switchPattern: function (pattern) {
    pattern.lastIndex = this.pattern.lastIndex;
    this.pattern = pattern;
    this.search.start = (pattern === this.start);
  },
  
  encloseMath: function (element) {
    var search = this.search, close = search.close, CLOSE, math, next;
    if (search.cpos === close.length) {close = close.nextSibling}
       else {close = close.splitText(search.cpos)}
    if (!close) {CLOSE = close = MathJax.HTML.addText(search.close.parentNode,"")}
    search.close = close;
    math = (search.opos ? search.open.splitText(search.opos) : search.open);
    while ((next = math.nextSibling) && next !== close) {
      if (next.nodeValue !== null) {
        if (next.nodeName === "#comment") {
          math.nodeValue += next.nodeValue.replace(/^\[CDATA\[((.|\n|\r)*)\]\]$/,"$1");
        } else {
          math.nodeValue += next.nodeValue;
        }
      } else {
        var ignore = this.ignoreTags[next.nodeName.toLowerCase()];
        math.nodeValue += (ignore == null ? " " : ignore);
      }
      math.parentNode.removeChild(next);
    }
    var TeX = math.nodeValue.substr(search.olen,math.nodeValue.length-search.olen-search.clen);
    math.parentNode.removeChild(math);
    if (this.config.preview !== "none") {this.createPreview(search.mode,TeX)}
    math = this.createMathTag(search.mode,TeX);
    this.search = {}; this.pattern.lastIndex = 0;
    if (CLOSE) {CLOSE.parentNode.removeChild(CLOSE)}
    return math;
  },
  
  insertNode: function (node) {
    var search = this.search;
    search.close.parentNode.insertBefore(node,search.close);
  },
  
  createPreview: function (mode,tex) {
    var previewClass = MathJax.Hub.config.preRemoveClass;
    var preview = this.config.preview;
    if (preview === "none") return;
    if ((this.search.close.previousSibling||{}).className === previewClass) return;
    if (preview === "TeX") {preview = [this.filterPreview(tex)]}
    if (preview) {
      preview = MathJax.HTML.Element("span",{className:previewClass},preview);
      this.insertNode(preview);
    }
  },
  
  createMathTag: function (mode,tex) {
    var script = document.createElement("script");
    script.type = "math/tex" + mode;
    MathJax.HTML.setScript(script,tex);
    this.insertNode(script);
    return script;
  },
  
  filterPreview: function (tex) {return tex}

};

// We register the preprocessors with the following priorities:
// - mml2jax.js: 5
// - jsMath2jax.js: 8
// - asciimath2jax.js, tex2jax.js: 10 (default)
// See issues 18 and 484 and the other *2jax.js files.
MathJax.Hub.Register.PreProcessor(["PreProcess",MathJax.Extension.tex2jax]);
MathJax.Ajax.loadComplete("[MathJax]/extensions/tex2jax.js");

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/input/TeX/config.js
 *
 *  Initializes the TeX InputJax (the main definition is in
 *  MathJax/jax/input/TeX/jax.js, which is loaded when needed).
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2009-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.InputJax.TeX = MathJax.InputJax({
  id: "TeX",
  version: "2.7.2",
  directory: MathJax.InputJax.directory + "/TeX",
  extensionDir: MathJax.InputJax.extensionDir + "/TeX",
  
  config: {
    TagSide:       "right",
    TagIndent:     "0.8em",
    MultLineWidth: "85%",
    
    equationNumbers: {
      autoNumber: "none",  // "AMS" for standard AMS numbering,
                           //  or "all" for all displayed equations
      formatNumber: function (n) {return n},
      formatTag:    function (n) {return '('+n+')'},
      formatID:     function (n) {return 'mjx-eqn-'+String(n).replace(/[:"'<>&]/g,"")},
      formatURL:    function (id,base) {return base+'#'+escape(id)},
      useLabelIds:  true
    }
  },
  
  resetEquationNumbers: function () {}  // filled in by AMSmath extension
});
MathJax.InputJax.TeX.Register("math/tex");

MathJax.InputJax.TeX.loadComplete("config.js");

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/input/TeX/jax.js
 *  
 *  Implements the TeX InputJax that reads mathematics in
 *  TeX and LaTeX format and converts it to the MML ElementJax
 *  internal format.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2009-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (TEX,HUB,AJAX) {
  var MML, NBSP = "\u00A0"; 
  
  var _ = function (id) {
    return MathJax.Localization._.apply(MathJax.Localization,
      [["TeX", id]].concat([].slice.call(arguments,1)));
  };
  
  var isArray = MathJax.Object.isArray;

  var STACK = MathJax.Object.Subclass({
    Init: function (env,inner) {
      this.global = {isInner: inner};
      this.data = [STACKITEM.start(this.global)];
      if (env) {this.data[0].env = env}
      this.env = this.data[0].env;
    },
    Push: function () {
      var i, m, item, top;
      for (i = 0, m = arguments.length; i < m; i++) {
        item = arguments[i]; if (!item) continue;
        if (item instanceof MML.mbase) {item = STACKITEM.mml(item)}
        item.global = this.global;
        top = (this.data.length ? this.Top().checkItem(item) : true);
        if (top instanceof Array) {this.Pop(); this.Push.apply(this,top)}
        else if (top instanceof STACKITEM) {this.Pop(); this.Push(top)}
        else if (top) {
          this.data.push(item);
          if (item.env) {
            if (item.copyEnv !== false) {
              for (var id in this.env)
                {if (this.env.hasOwnProperty(id)) {item.env[id] = this.env[id]}}
            }
            this.env = item.env;
          } else {item.env = this.env}
        }
      }
    },
    Pop: function () {
      var item = this.data.pop(); if (!item.isOpen) {delete item.env}
      this.env = (this.data.length ? this.Top().env : {});
      return item;
    },
    Top: function (n) {
      if (n == null) {n = 1}
      if (this.data.length < n) {return null}
      return this.data[this.data.length-n];
    },
    Prev: function (noPop) {
      var top = this.Top();
      if (noPop) {return top.data[top.data.length-1]}
            else {return top.Pop()}
    },
    toString: function () {return "stack[\n  "+this.data.join("\n  ")+"\n]"}
  });
  
  var STACKITEM = STACK.Item = MathJax.Object.Subclass({
    type: "base",
    endError:   /*_()*/ ["ExtraOpenMissingClose","Extra open brace or missing close brace"],
    closeError: /*_()*/ ["ExtraCloseMissingOpen","Extra close brace or missing open brace"],
    rightError: /*_()*/ ["MissingLeftExtraRight","Missing \\left or extra \\right"],
    Init: function () {
      if (this.isOpen) {this.env = {}}
      this.data = [];
      this.Push.apply(this,arguments);
    },
    Push: function () {this.data.push.apply(this.data,arguments)},
    Pop: function () {return this.data.pop()},
    mmlData: function (inferred,forceRow) {
      if (inferred == null) {inferred = true}
      if (this.data.length === 1 && !forceRow) {return this.data[0]}
      return MML.mrow.apply(MML,this.data).With((inferred ? {inferred: true}: {}));
    },
    checkItem: function (item) {
      if (item.type === "over" && this.isOpen) {item.num = this.mmlData(false); this.data = []}
      if (item.type === "cell" && this.isOpen) {
        if (item.linebreak) {return false}
        TEX.Error(["Misplaced","Misplaced %1",item.name]);
      }
      if (item.isClose && this[item.type+"Error"]) {TEX.Error(this[item.type+"Error"])}
      if (!item.isNotStack) {return true}
      this.Push(item.data[0]); return false;
    },
    With: function (def) {
      for (var id in def) {if (def.hasOwnProperty(id)) {this[id] = def[id]}}
      return this;
    },
    toString: function () {return this.type+"["+this.data.join("; ")+"]"}
  });

  STACKITEM.start = STACKITEM.Subclass({
    type: "start", isOpen: true,
    Init: function (global) {
      this.SUPER(arguments).Init.call(this);
      this.global = global;
    },
    checkItem: function (item) {
      if (item.type === "stop") {return STACKITEM.mml(this.mmlData())}
      return this.SUPER(arguments).checkItem.call(this,item);
    }
  });

  STACKITEM.stop = STACKITEM.Subclass({
    type: "stop", isClose: true
  });

  STACKITEM.open = STACKITEM.Subclass({
    type: "open", isOpen: true,
    stopError: /*_()*/ ["ExtraOpenMissingClose","Extra open brace or missing close brace"],
    checkItem: function (item) {
      if (item.type === "close") {
        var mml = this.mmlData();
        return STACKITEM.mml(MML.TeXAtom(mml)); // TeXAtom make it an ORD to prevent spacing (FIXME: should be another way)
      }
      return this.SUPER(arguments).checkItem.call(this,item);
    }
  });

  STACKITEM.close = STACKITEM.Subclass({
    type: "close", isClose: true
  });

  STACKITEM.prime = STACKITEM.Subclass({
    type: "prime",
    checkItem: function (item) {
      if (this.data[0].type !== "msubsup") 
        {return [MML.msup(this.data[0],this.data[1]),item]}
      this.data[0].SetData(this.data[0].sup,this.data[1]);
      return [this.data[0],item];
    }
  });
  
  STACKITEM.subsup = STACKITEM.Subclass({
    type: "subsup",
    stopError: /*_()*/ ["MissingScript","Missing superscript or subscript argument"],
    supError:  /*_()*/ ["MissingOpenForSup","Missing open brace for superscript"],
    subError:  /*_()*/ ["MissingOpenForSub","Missing open brace for subscript"],
    checkItem: function (item) {
      if (item.type === "open" || item.type === "left") {return true}
      if (item.type === "mml") {
        if (this.primes) {
          if (this.position !== 2) {this.data[0].SetData(2,this.primes)}
            else {item.data[0] = MML.mrow(this.primes.With({variantForm:true}),item.data[0])}
        }
        this.data[0].SetData(this.position,item.data[0]);
        if (this.movesupsub != null) {this.data[0].movesupsub = this.movesupsub}
        return STACKITEM.mml(this.data[0]);
      }
      if (this.SUPER(arguments).checkItem.call(this,item))
        {TEX.Error(this[["","subError","supError"][this.position]])}
    },
    Pop: function () {}
  });

  STACKITEM.over = STACKITEM.Subclass({
    type: "over", isClose: true, name: "\\over",
    checkItem: function (item,stack) {
      if (item.type === "over")
        {TEX.Error(["AmbiguousUseOf","Ambiguous use of %1",item.name])}
      if (item.isClose) {
        var mml = MML.mfrac(this.num,this.mmlData(false));
        if (this.thickness != null) {mml.linethickness = this.thickness}
        if (this.open || this.close) {
          mml.texWithDelims = true;
          mml = TEX.fixedFence(this.open,mml,this.close);
        }
        return [STACKITEM.mml(mml), item];
      }
      return this.SUPER(arguments).checkItem.call(this,item);
    },
    toString: function () {return "over["+this.num+" / "+this.data.join("; ")+"]"}
  });

  STACKITEM.left = STACKITEM.Subclass({
    type: "left", isOpen: true, delim: '(',
    stopError: /*_()*/ ["ExtraLeftMissingRight", "Extra \\left or missing \\right"],
    checkItem: function (item) {
      if (item.type === "right")
        {return STACKITEM.mml(TEX.fenced(this.delim,this.mmlData(),item.delim))}
      return this.SUPER(arguments).checkItem.call(this,item);
    }
  });

  STACKITEM.right = STACKITEM.Subclass({
    type: "right", isClose: true, delim: ')'
  });

  STACKITEM.begin = STACKITEM.Subclass({
    type: "begin", isOpen: true,
    checkItem: function (item) {
      if (item.type === "end") {
        if (item.name !== this.name)
          {TEX.Error(["EnvBadEnd","\\begin{%1} ended with \\end{%2}",this.name,item.name])}
        if (!this.end) {return STACKITEM.mml(this.mmlData())}
        return this.parse[this.end].call(this.parse,this,this.data);
      }
      if (item.type === "stop")
        {TEX.Error(["EnvMissingEnd","Missing \\end{%1}",this.name])}
      return this.SUPER(arguments).checkItem.call(this,item);
    }
  });
  
  STACKITEM.end = STACKITEM.Subclass({
    type: "end", isClose: true
  });

  STACKITEM.style = STACKITEM.Subclass({
    type: "style",
    checkItem: function (item) {
      if (!item.isClose) {return this.SUPER(arguments).checkItem.call(this,item)}
      var mml = MML.mstyle.apply(MML,this.data).With(this.styles);
      return [STACKITEM.mml(mml),item];
    }
  });
  
  STACKITEM.position = STACKITEM.Subclass({
    type: "position",
    checkItem: function (item) {
      if (item.isClose) {TEX.Error(["MissingBoxFor","Missing box for %1",this.name])}
      if (item.isNotStack) {
        var mml = item.mmlData();
        switch (this.move) {
         case 'vertical':
          mml = MML.mpadded(mml).With({height: this.dh, depth: this.dd, voffset: this.dh});
          return [STACKITEM.mml(mml)];
         case 'horizontal':
          return [STACKITEM.mml(this.left),item,STACKITEM.mml(this.right)];
        }
      }
      return this.SUPER(arguments).checkItem.call(this,item);
    }
  });
  
  STACKITEM.array = STACKITEM.Subclass({
    type: "array", isOpen: true, copyEnv: false, arraydef: {},
    Init: function () {
      this.table = []; this.row = []; this.frame = []; this.hfill = [];
      this.SUPER(arguments).Init.apply(this,arguments);
    },
    checkItem: function (item) {
      if (item.isClose && item.type !== "over") {
        if (item.isEntry) {this.EndEntry(); this.clearEnv(); return false}
        if (item.isCR)    {this.EndEntry(); this.EndRow(); this.clearEnv(); return false}
        this.EndTable(); this.clearEnv();
        var scriptlevel = this.arraydef.scriptlevel; delete this.arraydef.scriptlevel;
        var mml = MML.mtable.apply(MML,this.table).With(this.arraydef);
        if (this.frame.length === 4) {
          mml.frame = (this.frame.dashed ? "dashed" : "solid");
        } else if (this.frame.length) {
          mml.hasFrame = true;
          if (this.arraydef.rowlines) {this.arraydef.rowlines = this.arraydef.rowlines.replace(/none( none)+$/,"none")}
          mml = MML.menclose(mml).With({notation: this.frame.join(" "), isFrame: true});
          if ((this.arraydef.columnlines||"none") != "none" ||
              (this.arraydef.rowlines||"none") != "none") {mml.padding = 0} // HTML-CSS jax implements this
        }
        if (scriptlevel) {mml = MML.mstyle(mml).With({scriptlevel: scriptlevel})}
        if (this.open || this.close) {mml = TEX.fenced(this.open,mml,this.close)}
        mml = STACKITEM.mml(mml);
        if (this.requireClose) {
          if (item.type === 'close') {return mml}
          TEX.Error(["MissingCloseBrace","Missing close brace"]);
        }
        return [mml,item];
      }
      return this.SUPER(arguments).checkItem.call(this,item);
    },
    EndEntry: function () {
      var mtd = MML.mtd.apply(MML,this.data);
      if (this.hfill.length) {
        if (this.hfill[0] === 0) mtd.columnalign = "right";
        if (this.hfill[this.hfill.length-1] === this.data.length)
          mtd.columnalign = (mtd.columnalign ? "center" : "left");
      }
      this.row.push(mtd); this.data = []; this.hfill = [];
    },
    EndRow:   function () {
      var mtr = MML.mtr;
      if (this.isNumbered && this.row.length === 3) {
        this.row.unshift(this.row.pop());  // move equation number to first position
        mtr = MML.mlabeledtr;
      }
      this.table.push(mtr.apply(MML,this.row)); this.row = [];
    },
    EndTable: function () {
      if (this.data.length || this.row.length) {this.EndEntry(); this.EndRow()}
      this.checkLines();
    },
    checkLines: function () {
      if (this.arraydef.rowlines) {
        var lines = this.arraydef.rowlines.split(/ /);
        if (lines.length === this.table.length) {
          this.frame.push("bottom"); lines.pop();
          this.arraydef.rowlines = lines.join(' ');
        } else if (lines.length < this.table.length-1) {
          this.arraydef.rowlines += " none";
        }
      }
      if (this.rowspacing) {
        var rows = this.arraydef.rowspacing.split(/ /);
        while (rows.length < this.table.length) {rows.push(this.rowspacing+"em")}
        this.arraydef.rowspacing = rows.join(' ');
      }
    },
    clearEnv: function () {
      for (var id in this.env) {if (this.env.hasOwnProperty(id)) {delete this.env[id]}}
    }
  });
  
  STACKITEM.cell = STACKITEM.Subclass({
    type: "cell", isClose: true
  });

  STACKITEM.mml = STACKITEM.Subclass({
    type: "mml", isNotStack: true,
    Add: function () {this.data.push.apply(this.data,arguments); return this}
  });
  
  STACKITEM.fn = STACKITEM.Subclass({
    type: "fn",
    checkItem: function (item) {
      if (this.data[0]) {
        if (item.isOpen) {return true}
        if (item.type !== "fn") {
          if (item.type !== "mml" || !item.data[0]) {return [this.data[0],item]}
          if (item.data[0].isa(MML.mspace)) {return [this.data[0],item]}
          var mml = item.data[0]; if (mml.isEmbellished()) {mml = mml.CoreMO()}
          if ([0,0,1,1,0,1,1,0,0,0][mml.Get("texClass")]) {return [this.data[0],item]}
        }
        return [this.data[0],MML.mo(MML.entity("#x2061")).With({texClass:MML.TEXCLASS.NONE}),item];
      }
      return this.SUPER(arguments).checkItem.apply(this,arguments);
    }
  });
  
  STACKITEM.not = STACKITEM.Subclass({
    type: "not",
    checkItem: function (item) {
      var mml, c;
      if (item.type === "open" || item.type === "left") {return true}
      if (item.type === "mml" && item.data[0].type.match(/^(mo|mi|mtext)$/)) {
        mml = item.data[0], c = mml.data.join("");
        if (c.length === 1 && !mml.movesupsub && mml.data.length === 1) {
          c = STACKITEM.not.remap[c.charCodeAt(0)];
          if (c) {mml.SetData(0,MML.chars(String.fromCharCode(c)))}
            else {mml.Append(MML.chars("\u0338"))}
          return item;
        }
      }
      //  \mathrel{\rlap{\notChar}}
      mml = MML.mpadded(MML.mtext("\u29F8")).With({width:0});
      mml = MML.TeXAtom(mml).With({texClass:MML.TEXCLASS.REL});
      return [mml,item];
    }
  });
  STACKITEM.not.remap = {
    0x2190:0x219A, 0x2192:0x219B, 0x2194:0x21AE,
    0x21D0:0x21CD, 0x21D2:0x21CF, 0x21D4:0x21CE,
    0x2208:0x2209, 0x220B:0x220C, 0x2223:0x2224, 0x2225:0x2226,
    0x223C:0x2241, 0x007E:0x2241, 0x2243:0x2244, 0x2245:0x2247,
    0x2248:0x2249, 0x224D:0x226D, 0x003D:0x2260, 0x2261:0x2262,
    0x003C:0x226E, 0x003E:0x226F, 0x2264:0x2270, 0x2265:0x2271,
    0x2272:0x2274, 0x2273:0x2275, 0x2276:0x2278, 0x2277:0x2279,
    0x227A:0x2280, 0x227B:0x2281, 0x2282:0x2284, 0x2283:0x2285,
    0x2286:0x2288, 0x2287:0x2289, 0x22A2:0x22AC, 0x22A8:0x22AD,
    0x22A9:0x22AE, 0x22AB:0x22AF, 0x227C:0x22E0, 0x227D:0x22E1,
    0x2291:0x22E2, 0x2292:0x22E3, 0x22B2:0x22EA, 0x22B3:0x22EB,
    0x22B4:0x22EC, 0x22B5:0x22ED, 0x2203:0x2204
  };
  
  STACKITEM.dots = STACKITEM.Subclass({
    type: "dots",
    checkItem: function (item) {
      if (item.type === "open" || item.type === "left") {return true}
      var dots = this.ldots;
      if (item.type === "mml" && item.data[0].isEmbellished()) {
        var tclass = item.data[0].CoreMO().Get("texClass");
        if (tclass === MML.TEXCLASS.BIN || tclass === MML.TEXCLASS.REL) {dots = this.cdots}
      }
      return [dots,item];
    }
  });
  

  var TEXDEF = {
    //
    //  Add new definitions without overriding user-defined ones
    //
    Add: function (src,dst,nouser) {
      if (!dst) {dst = this}
      for (var id in src) {if (src.hasOwnProperty(id)) {
        if (typeof src[id] === 'object' && !isArray(src[id]) &&
           (typeof dst[id] === 'object' || typeof dst[id] === 'function')) 
             {this.Add(src[id],dst[id],src[id],nouser)}
          else if (!dst[id] || !dst[id].isUser || !nouser) {dst[id] = src[id]}
      }}
      return dst;
    }
  };
  var STARTUP = function () {
    MML = MathJax.ElementJax.mml;
    HUB.Insert(TEXDEF,{
  
      // patterns for letters and numbers
      letter:  /[a-z]/i,
      digit:   /[0-9.]/,
      number:  /^(?:[0-9]+(?:\{,\}[0-9]{3})*(?:\.[0-9]*)*|\.[0-9]+)/,
    
      special: {
        '\\':  'ControlSequence',
        '{':   'Open',
        '}':   'Close',
        '~':   'Tilde',
        '^':   'Superscript',
        '_':   'Subscript',
        ' ':   'Space',
        "\t":  'Space',
        "\r":  'Space',
        "\n":  'Space',
        "'":   'Prime',
        '%':   'Comment',
        '&':   'Entry',
        '#':   'Hash',
        '\u00A0': 'Space',
        '\u2019': 'Prime'
      },
      
      remap: {
        '-':   '2212',
        '*':   '2217',
        '`':   '2018'   // map ` to back quote
      },
    
      mathchar0mi: {
	// Lower-case greek
	alpha:        '03B1',
	beta:         '03B2',
	gamma:        '03B3',
	delta:        '03B4',
	epsilon:      '03F5',
	zeta:         '03B6',
	eta:          '03B7',
	theta:        '03B8',
	iota:         '03B9',
	kappa:        '03BA',
	lambda:       '03BB',
	mu:           '03BC',
	nu:           '03BD',
	xi:           '03BE',
	omicron:      '03BF', // added for completeness
	pi:           '03C0',
	rho:          '03C1',
	sigma:        '03C3',
	tau:          '03C4',
	upsilon:      '03C5',
	phi:          '03D5',
	chi:          '03C7',
	psi:          '03C8',
	omega:        '03C9',
	varepsilon:   '03B5',
	vartheta:     '03D1',
	varpi:        '03D6',
	varrho:       '03F1',
	varsigma:     '03C2',
	varphi:       '03C6',
        
        // Ord symbols
        S:            ['00A7',{mathvariant: MML.VARIANT.NORMAL}],
        aleph:        ['2135',{mathvariant: MML.VARIANT.NORMAL}],
        hbar:         ['210F',{variantForm:true}],
        imath:        '0131',
        jmath:        '0237',
        ell:          '2113',
        wp:           ['2118',{mathvariant: MML.VARIANT.NORMAL}],
        Re:           ['211C',{mathvariant: MML.VARIANT.NORMAL}],
        Im:           ['2111',{mathvariant: MML.VARIANT.NORMAL}],
        partial:      ['2202',{mathvariant: MML.VARIANT.NORMAL}],
        infty:        ['221E',{mathvariant: MML.VARIANT.NORMAL}],
        prime:        ['2032',{mathvariant: MML.VARIANT.NORMAL, variantForm:true}],
        emptyset:     ['2205',{mathvariant: MML.VARIANT.NORMAL}],
        nabla:        ['2207',{mathvariant: MML.VARIANT.NORMAL}],
        top:          ['22A4',{mathvariant: MML.VARIANT.NORMAL}],
        bot:          ['22A5',{mathvariant: MML.VARIANT.NORMAL}],
        angle:        ['2220',{mathvariant: MML.VARIANT.NORMAL}],
        triangle:     ['25B3',{mathvariant: MML.VARIANT.NORMAL}],
        backslash:    ['2216',{mathvariant: MML.VARIANT.NORMAL, variantForm:true}],
        forall:       ['2200',{mathvariant: MML.VARIANT.NORMAL}],
        exists:       ['2203',{mathvariant: MML.VARIANT.NORMAL}],
        neg:          ['00AC',{mathvariant: MML.VARIANT.NORMAL}],
        lnot:         ['00AC',{mathvariant: MML.VARIANT.NORMAL}],
        flat:         ['266D',{mathvariant: MML.VARIANT.NORMAL}],
        natural:      ['266E',{mathvariant: MML.VARIANT.NORMAL}],
        sharp:        ['266F',{mathvariant: MML.VARIANT.NORMAL}],
        clubsuit:     ['2663',{mathvariant: MML.VARIANT.NORMAL}],
        diamondsuit:  ['2662',{mathvariant: MML.VARIANT.NORMAL}],
        heartsuit:    ['2661',{mathvariant: MML.VARIANT.NORMAL}],
        spadesuit:    ['2660',{mathvariant: MML.VARIANT.NORMAL}]
      },
        
      mathchar0mo: {
        surd:         '221A',

        // big ops
        coprod:       ['2210',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigvee:       ['22C1',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigwedge:     ['22C0',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        biguplus:     ['2A04',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigcap:       ['22C2',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigcup:       ['22C3',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        'int':        ['222B',{texClass: MML.TEXCLASS.OP}],
        intop:        ['222B',{texClass: MML.TEXCLASS.OP, movesupsub:true, movablelimits:true}],
        iint:         ['222C',{texClass: MML.TEXCLASS.OP}],
        iiint:        ['222D',{texClass: MML.TEXCLASS.OP}],
        prod:         ['220F',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        sum:          ['2211',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigotimes:    ['2A02',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigoplus:     ['2A01',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        bigodot:      ['2A00',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        oint:         ['222E',{texClass: MML.TEXCLASS.OP}],
        bigsqcup:     ['2A06',{texClass: MML.TEXCLASS.OP, movesupsub:true}],
        smallint:     ['222B',{largeop:false}],
        
        // binary operations
        triangleleft:      '25C3',
        triangleright:     '25B9',
        bigtriangleup:     '25B3',
        bigtriangledown:   '25BD',
        wedge:        '2227',
        land:         '2227',
        vee:          '2228',
        lor:          '2228',
        cap:          '2229',
        cup:          '222A',
        ddagger:      '2021',
        dagger:       '2020',
        sqcap:        '2293',
        sqcup:        '2294',
        uplus:        '228E',
        amalg:        '2A3F',
        diamond:      '22C4',
        bullet:       '2219',
        wr:           '2240',
        div:          '00F7',
        odot:         ['2299',{largeop: false}],
        oslash:       ['2298',{largeop: false}],
        otimes:       ['2297',{largeop: false}],
        ominus:       ['2296',{largeop: false}],
        oplus:        ['2295',{largeop: false}],
        mp:           '2213',
        pm:           '00B1',
        circ:         '2218',
        bigcirc:      '25EF',
        setminus:     ['2216',{variantForm:true}],
        cdot:         '22C5',
        ast:          '2217',
        times:        '00D7',
        star:         '22C6',
        
        // Relations
        propto:       '221D',
        sqsubseteq:   '2291',
        sqsupseteq:   '2292',
        parallel:     '2225',
        mid:          '2223',
        dashv:        '22A3',
        vdash:        '22A2',
        leq:          '2264',
        le:           '2264',
        geq:          '2265',
        ge:           '2265',
        lt:           '003C',
        gt:           '003E',
        succ:         '227B',
        prec:         '227A',
        approx:       '2248',
        succeq:       '2AB0',  // or '227C',
        preceq:       '2AAF',  // or '227D',
        supset:       '2283',
        subset:       '2282',
        supseteq:     '2287',
        subseteq:     '2286',
        'in':         '2208',
        ni:           '220B',
        notin:        '2209',
        owns:         '220B',
        gg:           '226B',
        ll:           '226A',
        sim:          '223C',
        simeq:        '2243',
        perp:         '22A5',
        equiv:        '2261',
        asymp:        '224D',
        smile:        '2323',
        frown:        '2322',
        ne:           '2260',
        neq:          '2260',
        cong:         '2245',
        doteq:        '2250',
        bowtie:       '22C8',
        models:       '22A8',
        
        notChar:      '29F8',
        
        
        // Arrows
        Leftrightarrow:     '21D4',
        Leftarrow:          '21D0',
        Rightarrow:         '21D2',
        leftrightarrow:     '2194',
        leftarrow:          '2190',
        gets:               '2190',
        rightarrow:         '2192',
        to:                 '2192',
        mapsto:             '21A6',
        leftharpoonup:      '21BC',
        leftharpoondown:    '21BD',
        rightharpoonup:     '21C0',
        rightharpoondown:   '21C1',
        nearrow:            '2197',
        searrow:            '2198',
        nwarrow:            '2196',
        swarrow:            '2199',
        rightleftharpoons:  '21CC',
        hookrightarrow:     '21AA',
        hookleftarrow:      '21A9',
        longleftarrow:      '27F5',
        Longleftarrow:      '27F8',
        longrightarrow:     '27F6',
        Longrightarrow:     '27F9',
        Longleftrightarrow: '27FA',
        longleftrightarrow: '27F7',
        longmapsto:         '27FC',
        
        
        // Misc.
        ldots:            '2026',
        cdots:            '22EF',
        vdots:            '22EE',
        ddots:            '22F1',
        dotsc:            '2026',  // dots with commas
        dotsb:            '22EF',  // dots with binary ops and relations
        dotsm:            '22EF',  // dots with multiplication
        dotsi:            '22EF',  // dots with integrals
        dotso:            '2026',  // other dots
        
        ldotp:            ['002E', {texClass: MML.TEXCLASS.PUNCT}],
        cdotp:            ['22C5', {texClass: MML.TEXCLASS.PUNCT}],
        colon:            ['003A', {texClass: MML.TEXCLASS.PUNCT}]
      },
      
      mathchar7: {
        Gamma:        '0393',
        Delta:        '0394',
        Theta:        '0398',
        Lambda:       '039B',
        Xi:           '039E',
        Pi:           '03A0',
        Sigma:        '03A3',
        Upsilon:      '03A5',
        Phi:          '03A6',
        Psi:          '03A8',
        Omega:        '03A9',
        
        '_':          '005F',
        '#':          '0023',
        '$':          '0024',
        '%':          '0025',
        '&':          '0026',
        And:          '0026'
      },
      
      delimiter: {
        '(':                '(',
        ')':                ')',
        '[':                '[',
        ']':                ']',
        '<':                '27E8',
        '>':                '27E9',
        '\\lt':             '27E8',
        '\\gt':             '27E9',
        '/':                '/',
        '|':                ['|',{texClass:MML.TEXCLASS.ORD}],
        '.':                '',
        '\\\\':             '\\',
        '\\lmoustache':     '23B0',  // non-standard
        '\\rmoustache':     '23B1',  // non-standard
        '\\lgroup':         '27EE',  // non-standard
        '\\rgroup':         '27EF',  // non-standard
        '\\arrowvert':      '23D0',
        '\\Arrowvert':      '2016',
        '\\bracevert':      '23AA',  // non-standard
        '\\Vert':           ['2016',{texClass:MML.TEXCLASS.ORD}],
        '\\|':              ['2016',{texClass:MML.TEXCLASS.ORD}],
        '\\vert':           ['|',{texClass:MML.TEXCLASS.ORD}],
        '\\uparrow':        '2191',
        '\\downarrow':      '2193',
        '\\updownarrow':    '2195',
        '\\Uparrow':        '21D1',
        '\\Downarrow':      '21D3',
        '\\Updownarrow':    '21D5',
        '\\backslash':      '\\',
        '\\rangle':         '27E9',
        '\\langle':         '27E8',
        '\\rbrace':         '}',
        '\\lbrace':         '{',
        '\\}':              '}',
        '\\{':              '{',
        '\\rceil':          '2309',
        '\\lceil':          '2308',
        '\\rfloor':         '230B',
        '\\lfloor':         '230A',
        '\\lbrack':         '[',
        '\\rbrack':         ']'
      },
      
      macros: {
        displaystyle:      ['SetStyle','D',true,0],
        textstyle:         ['SetStyle','T',false,0],
        scriptstyle:       ['SetStyle','S',false,1],
        scriptscriptstyle: ['SetStyle','SS',false,2],
        
        rm:                ['SetFont',MML.VARIANT.NORMAL],
        mit:               ['SetFont',MML.VARIANT.ITALIC],
        oldstyle:          ['SetFont',MML.VARIANT.OLDSTYLE],
        cal:               ['SetFont',MML.VARIANT.CALIGRAPHIC],
        it:                ['SetFont',"-tex-mathit"], // needs special handling
        bf:                ['SetFont',MML.VARIANT.BOLD],
        bbFont:            ['SetFont',MML.VARIANT.DOUBLESTRUCK],
        scr:               ['SetFont',MML.VARIANT.SCRIPT],
        frak:              ['SetFont',MML.VARIANT.FRAKTUR],
        sf:                ['SetFont',MML.VARIANT.SANSSERIF],
        tt:                ['SetFont',MML.VARIANT.MONOSPACE],

//      font:
        
        tiny:              ['SetSize',0.5],
        Tiny:              ['SetSize',0.6],  // non-standard
        scriptsize:        ['SetSize',0.7],
        small:             ['SetSize',0.85],
        normalsize:        ['SetSize',1.0],
        large:             ['SetSize',1.2],
        Large:             ['SetSize',1.44],
        LARGE:             ['SetSize',1.73],
        huge:              ['SetSize',2.07],
        Huge:              ['SetSize',2.49],
        
        arcsin:            ['NamedFn'],
        arccos:            ['NamedFn'],
        arctan:            ['NamedFn'],
        arg:               ['NamedFn'],
        cos:               ['NamedFn'],
        cosh:              ['NamedFn'],
        cot:               ['NamedFn'],
        coth:              ['NamedFn'],
        csc:               ['NamedFn'],
        deg:               ['NamedFn'],
        det:                'NamedOp',
        dim:               ['NamedFn'],
        exp:               ['NamedFn'],
        gcd:                'NamedOp',
        hom:               ['NamedFn'],
        inf:                'NamedOp',
        ker:               ['NamedFn'],
        lg:                ['NamedFn'],
        lim:                'NamedOp',
        liminf:            ['NamedOp','lim&thinsp;inf'],
        limsup:            ['NamedOp','lim&thinsp;sup'],
        ln:                ['NamedFn'],
        log:               ['NamedFn'],
        max:                'NamedOp',
        min:                'NamedOp',
        Pr:                 'NamedOp',
        sec:               ['NamedFn'],
        sin:               ['NamedFn'],
        sinh:              ['NamedFn'],
        sup:                'NamedOp',
        tan:               ['NamedFn'],
        tanh:              ['NamedFn'],
        
        limits:            ['Limits',1],
        nolimits:          ['Limits',0],

        overline:            ['UnderOver','00AF',null,1],
        underline:           ['UnderOver','005F'],
        overbrace:           ['UnderOver','23DE',1],
        underbrace:          ['UnderOver','23DF',1],
        overparen:           ['UnderOver','23DC'],
        underparen:          ['UnderOver','23DD'],
        overrightarrow:      ['UnderOver','2192'],
        underrightarrow:     ['UnderOver','2192'],
        overleftarrow:       ['UnderOver','2190'],
        underleftarrow:      ['UnderOver','2190'],
        overleftrightarrow:  ['UnderOver','2194'],
        underleftrightarrow: ['UnderOver','2194'],

        overset:            'Overset',
        underset:           'Underset',
        stackrel:           ['Macro','\\mathrel{\\mathop{#2}\\limits^{#1}}',2],
          
        over:               'Over',
        overwithdelims:     'Over',
        atop:               'Over',
        atopwithdelims:     'Over',
        above:              'Over',
        abovewithdelims:    'Over',
        brace:             ['Over','{','}'],
        brack:             ['Over','[',']'],
        choose:            ['Over','(',')'],
        
        frac:               'Frac',
        sqrt:               'Sqrt',
        root:               'Root',
        uproot:            ['MoveRoot','upRoot'],
        leftroot:          ['MoveRoot','leftRoot'],
        
        left:               'LeftRight',
        right:              'LeftRight',
        middle:             'Middle',

        llap:               'Lap',
        rlap:               'Lap',
        raise:              'RaiseLower',
        lower:              'RaiseLower',
        moveleft:           'MoveLeftRight',
        moveright:          'MoveLeftRight',

        ',':               ['Spacer',MML.LENGTH.THINMATHSPACE],
        ':':               ['Spacer',MML.LENGTH.MEDIUMMATHSPACE],  // for LaTeX
        '>':               ['Spacer',MML.LENGTH.MEDIUMMATHSPACE],
        ';':               ['Spacer',MML.LENGTH.THICKMATHSPACE],
        '!':               ['Spacer',MML.LENGTH.NEGATIVETHINMATHSPACE],
        enspace:           ['Spacer',".5em"],
        quad:              ['Spacer',"1em"],
        qquad:             ['Spacer',"2em"],
        thinspace:         ['Spacer',MML.LENGTH.THINMATHSPACE],
        negthinspace:      ['Spacer',MML.LENGTH.NEGATIVETHINMATHSPACE],
    
        hskip:              'Hskip',
        hspace:             'Hskip',
        kern:               'Hskip',
        mskip:              'Hskip',
        mspace:             'Hskip',
        mkern:              'Hskip',
        rule:               'rule',
        Rule:              ['Rule'],
        Space:             ['Rule','blank'],
    
        big:               ['MakeBig',MML.TEXCLASS.ORD,0.85],
        Big:               ['MakeBig',MML.TEXCLASS.ORD,1.15],
        bigg:              ['MakeBig',MML.TEXCLASS.ORD,1.45],
        Bigg:              ['MakeBig',MML.TEXCLASS.ORD,1.75],
        bigl:              ['MakeBig',MML.TEXCLASS.OPEN,0.85],
        Bigl:              ['MakeBig',MML.TEXCLASS.OPEN,1.15],
        biggl:             ['MakeBig',MML.TEXCLASS.OPEN,1.45],
        Biggl:             ['MakeBig',MML.TEXCLASS.OPEN,1.75],
        bigr:              ['MakeBig',MML.TEXCLASS.CLOSE,0.85],
        Bigr:              ['MakeBig',MML.TEXCLASS.CLOSE,1.15],
        biggr:             ['MakeBig',MML.TEXCLASS.CLOSE,1.45],
        Biggr:             ['MakeBig',MML.TEXCLASS.CLOSE,1.75],
        bigm:              ['MakeBig',MML.TEXCLASS.REL,0.85],
        Bigm:              ['MakeBig',MML.TEXCLASS.REL,1.15],
        biggm:             ['MakeBig',MML.TEXCLASS.REL,1.45],
        Biggm:             ['MakeBig',MML.TEXCLASS.REL,1.75],

        mathord:           ['TeXAtom',MML.TEXCLASS.ORD],
        mathop:            ['TeXAtom',MML.TEXCLASS.OP],
        mathopen:          ['TeXAtom',MML.TEXCLASS.OPEN],
        mathclose:         ['TeXAtom',MML.TEXCLASS.CLOSE],
        mathbin:           ['TeXAtom',MML.TEXCLASS.BIN],
        mathrel:           ['TeXAtom',MML.TEXCLASS.REL],
        mathpunct:         ['TeXAtom',MML.TEXCLASS.PUNCT],
        mathinner:         ['TeXAtom',MML.TEXCLASS.INNER],

        vcenter:           ['TeXAtom',MML.TEXCLASS.VCENTER],

        mathchoice:        ['Extension','mathchoice'],
        buildrel:           'BuildRel',
    
        hbox:               ['HBox',0],
        text:               'HBox',
        mbox:               ['HBox',0],
        fbox:               'FBox',

        strut:              'Strut',
        mathstrut:         ['Macro','\\vphantom{(}'],
        phantom:            'Phantom',
        vphantom:          ['Phantom',1,0],
        hphantom:          ['Phantom',0,1],
        smash:              'Smash',
    
        acute:             ['Accent', "00B4"],  // or 0301 or 02CA
        grave:             ['Accent', "0060"],  // or 0300 or 02CB
        ddot:              ['Accent', "00A8"],  // or 0308
        tilde:             ['Accent', "007E"],  // or 0303 or 02DC
        bar:               ['Accent', "00AF"],  // or 0304 or 02C9
        breve:             ['Accent', "02D8"],  // or 0306
        check:             ['Accent', "02C7"],  // or 030C
        hat:               ['Accent', "005E"],  // or 0302 or 02C6
        vec:               ['Accent', "2192"],  // or 20D7
        dot:               ['Accent', "02D9"],  // or 0307
        widetilde:         ['Accent', "007E",1], // or 0303 or 02DC
        widehat:           ['Accent', "005E",1], // or 0302 or 02C6

        matrix:             'Matrix',
        array:              'Matrix',
        pmatrix:           ['Matrix','(',')'],
        cases:             ['Matrix','{','',"left left",null,".1em",null,true],
        eqalign:           ['Matrix',null,null,"right left",MML.LENGTH.THICKMATHSPACE,".5em",'D'],
        displaylines:      ['Matrix',null,null,"center",null,".5em",'D'],
        cr:                 'Cr',
        '\\':               'CrLaTeX',
        newline:            'Cr',
        hline:             ['HLine','solid'],
        hdashline:         ['HLine','dashed'],
//      noalign:            'HandleNoAlign',
        eqalignno:         ['Matrix',null,null,"right left",MML.LENGTH.THICKMATHSPACE,".5em",'D',null,"right"],
        leqalignno:        ['Matrix',null,null,"right left",MML.LENGTH.THICKMATHSPACE,".5em",'D',null,"left"],
        hfill:              'HFill',
        hfil:               'HFill',   // \hfil treated as \hfill for now
        hfilll:             'HFill',   // \hfilll treated as \hfill for now

        //  TeX substitution macros
        bmod:              ['Macro','\\mmlToken{mo}[lspace="thickmathspace" rspace="thickmathspace"]{mod}'],
        pmod:              ['Macro','\\pod{\\mmlToken{mi}{mod}\\kern 6mu #1}',1],
        mod:               ['Macro','\\mathchoice{\\kern18mu}{\\kern12mu}{\\kern12mu}{\\kern12mu}\\mmlToken{mi}{mod}\\,\\,#1',1],
        pod:               ['Macro','\\mathchoice{\\kern18mu}{\\kern8mu}{\\kern8mu}{\\kern8mu}(#1)',1],
        iff:               ['Macro','\\;\\Longleftrightarrow\\;'],
        skew:              ['Macro','{{#2{#3\\mkern#1mu}\\mkern-#1mu}{}}',3],
        mathcal:           ['Macro','{\\cal #1}',1],
        mathscr:           ['Macro','{\\scr #1}',1],
        mathrm:            ['Macro','{\\rm #1}',1],
        mathbf:            ['Macro','{\\bf #1}',1],
        mathbb:            ['Macro','{\\bbFont #1}',1],
        Bbb:               ['Macro','{\\bbFont #1}',1],
        mathit:            ['Macro','{\\it #1}',1],
        mathfrak:          ['Macro','{\\frak #1}',1],
        mathsf:            ['Macro','{\\sf #1}',1],
        mathtt:            ['Macro','{\\tt #1}',1],
        textrm:            ['Macro','\\mathord{\\rm\\text{#1}}',1],
        textit:            ['Macro','\\mathord{\\it\\text{#1}}',1],
        textbf:            ['Macro','\\mathord{\\bf\\text{#1}}',1],
        textsf:            ['Macro','\\mathord{\\sf\\text{#1}}',1],
        texttt:            ['Macro','\\mathord{\\tt\\text{#1}}',1],
        pmb:               ['Macro','\\rlap{#1}\\kern1px{#1}',1],
        TeX:               ['Macro','T\\kern-.14em\\lower.5ex{E}\\kern-.115em X'],
        LaTeX:             ['Macro','L\\kern-.325em\\raise.21em{\\scriptstyle{A}}\\kern-.17em\\TeX'],
        ' ':               ['Macro','\\text{ }'],

        //  Specially handled
        not:                'Not',
        dots:               'Dots',
        space:              'Tilde',
        '\u00A0':           'Tilde',
        

        //  LaTeX
        begin:              'BeginEnd',
        end:                'BeginEnd',

        newcommand:        ['Extension','newcommand'],
        renewcommand:      ['Extension','newcommand'],
        newenvironment:    ['Extension','newcommand'],
        renewenvironment:  ['Extension','newcommand'],
        def:               ['Extension','newcommand'],
        let:               ['Extension','newcommand'],
        
        verb:              ['Extension','verb'],
        
        boldsymbol:        ['Extension','boldsymbol'],
        
        tag:               ['Extension','AMSmath'],
        notag:             ['Extension','AMSmath'],
        label:             ['Extension','AMSmath'],
        ref:               ['Extension','AMSmath'],
        eqref:             ['Extension','AMSmath'],
        nonumber:          ['Macro','\\notag'],

        //  Extensions to TeX
        unicode:           ['Extension','unicode'],
        color:              'Color',
        
        href:              ['Extension','HTML'],
        'class':           ['Extension','HTML'],
        style:             ['Extension','HTML'],
        cssId:             ['Extension','HTML'],
        bbox:              ['Extension','bbox'],
    
        mmlToken:           'MmlToken',

        require:            'Require'

      },
      
      environment: {
        array:        ['AlignedArray'],
        matrix:       ['Array',null,null,null,'c'],
        pmatrix:      ['Array',null,'(',')','c'],
        bmatrix:      ['Array',null,'[',']','c'],
        Bmatrix:      ['Array',null,'\\{','\\}','c'],
        vmatrix:      ['Array',null,'\\vert','\\vert','c'],
        Vmatrix:      ['Array',null,'\\Vert','\\Vert','c'],
        cases:        ['Array',null,'\\{','.','ll',null,".2em",'T'],

        equation:     [null,'Equation'],
        'equation*':  [null,'Equation'],

        eqnarray:     ['ExtensionEnv',null,'AMSmath'],
        'eqnarray*':  ['ExtensionEnv',null,'AMSmath'],

        align:        ['ExtensionEnv',null,'AMSmath'],
        'align*':     ['ExtensionEnv',null,'AMSmath'],
        aligned:      ['ExtensionEnv',null,'AMSmath'],
        multline:     ['ExtensionEnv',null,'AMSmath'],
        'multline*':  ['ExtensionEnv',null,'AMSmath'],
        split:        ['ExtensionEnv',null,'AMSmath'],
        gather:       ['ExtensionEnv',null,'AMSmath'],
        'gather*':    ['ExtensionEnv',null,'AMSmath'],
        gathered:     ['ExtensionEnv',null,'AMSmath'],
        alignat:      ['ExtensionEnv',null,'AMSmath'],
        'alignat*':   ['ExtensionEnv',null,'AMSmath'],
        alignedat:    ['ExtensionEnv',null,'AMSmath']
      },
      
      p_height: 1.2 / .85   // cmex10 height plus depth over .85

    });
    
    //
    //  Add macros defined in the configuration
    //
    if (this.config.Macros) {
      var MACROS = this.config.Macros;
      for (var id in MACROS) {if (MACROS.hasOwnProperty(id)) {
        if (typeof(MACROS[id]) === "string") {TEXDEF.macros[id] = ['Macro',MACROS[id]]}
        else {TEXDEF.macros[id] = ["Macro"].concat(MACROS[id])}
        TEXDEF.macros[id].isUser = true;
      }}
    }
  };
  
  /************************************************************************/
  /*
   *   The TeX Parser
   */

  var PARSE = MathJax.Object.Subclass({
    Init: function (string,env) {
      this.string = string; this.i = 0; this.macroCount = 0;
      var ENV; if (env) {ENV = {}; for (var id in env) {if (env.hasOwnProperty(id)) {ENV[id] = env[id]}}}
      this.stack = TEX.Stack(ENV,!!env);
      this.Parse(); this.Push(STACKITEM.stop());
    },
    Parse: function () {
      var c, n;
      while (this.i < this.string.length) {
        c = this.string.charAt(this.i++); n = c.charCodeAt(0);
        if (n >= 0xD800 && n < 0xDC00) {c += this.string.charAt(this.i++)}
        if (TEXDEF.special[c]) {this[TEXDEF.special[c]](c)}
        else if (TEXDEF.letter.test(c)) {this.Variable(c)}
        else if (TEXDEF.digit.test(c)) {this.Number(c)}
        else {this.Other(c)}
      }
    },
    Push: function () {this.stack.Push.apply(this.stack,arguments)},
    mml: function () {
      if (this.stack.Top().type !== "mml") {return null}
      return this.stack.Top().data[0];
    },
    mmlToken: function (token) {return token}, // used by boldsymbol extension

    /************************************************************************/
    /*
     *   Handle various token classes
     */

    /*
     *  Lookup a control-sequence and process it
     */
    ControlSequence: function (c) {
      var name = this.GetCS(), macro = this.csFindMacro(name);
      if (macro) {
        if (!isArray(macro)) {macro = [macro]}
        var fn = macro[0]; if (!(fn instanceof Function)) {fn = this[fn]}
        fn.apply(this,[c+name].concat(macro.slice(1)));
      } else if (TEXDEF.mathchar0mi[name])            {this.csMathchar0mi(name,TEXDEF.mathchar0mi[name])}
        else if (TEXDEF.mathchar0mo[name])            {this.csMathchar0mo(name,TEXDEF.mathchar0mo[name])}
        else if (TEXDEF.mathchar7[name])              {this.csMathchar7(name,TEXDEF.mathchar7[name])}
        else if (TEXDEF.delimiter["\\"+name] != null) {this.csDelimiter(name,TEXDEF.delimiter["\\"+name])}
        else                                          {this.csUndefined(c+name)}
    },
    //
    //  Look up a macro in the macros list
    //  (overridden in begingroup extension)
    //
    csFindMacro: function (name) {return TEXDEF.macros[name]},
    //
    //  Handle normal mathchar (as an mi)
    //
    csMathchar0mi: function (name,mchar) {
      var def = {mathvariant: MML.VARIANT.ITALIC};
      if (isArray(mchar)) {def = mchar[1]; mchar = mchar[0]}
      this.Push(this.mmlToken(MML.mi(MML.entity("#x"+mchar)).With(def)));
    },
    //
    //  Handle normal mathchar (as an mo)
    //
    csMathchar0mo: function (name,mchar) {
      var def = {stretchy: false};
      if (isArray(mchar)) {def = mchar[1]; def.stretchy = false; mchar = mchar[0]}
      this.Push(this.mmlToken(MML.mo(MML.entity("#x"+mchar)).With(def)));
    },
    //
    //  Handle mathchar in current family
    //
    csMathchar7: function (name,mchar) {
      var def = {mathvariant: MML.VARIANT.NORMAL};
      if (isArray(mchar)) {def = mchar[1]; mchar = mchar[0]}
      if (this.stack.env.font) {def.mathvariant = this.stack.env.font}
      this.Push(this.mmlToken(MML.mi(MML.entity("#x"+mchar)).With(def)));
    },
    //
    //  Handle delimiter
    //
    csDelimiter: function (name,delim) {
      var def = {};
      if (isArray(delim)) {def = delim[1]; delim = delim[0]}
      if (delim.length === 4) {delim = MML.entity('#x'+delim)} else {delim = MML.chars(delim)}
      this.Push(this.mmlToken(MML.mo(delim).With({fence: false, stretchy: false}).With(def)));
    },
    //
    //  Handle undefined control sequence
    //  (overridden in noUndefined extension)
    //
    csUndefined: function (name) {
      TEX.Error(["UndefinedControlSequence","Undefined control sequence %1",name]);
    },

    /*
     *  Handle a variable (a single letter)
     */
    Variable: function (c) {
      var def = {}; if (this.stack.env.font) {def.mathvariant = this.stack.env.font}
      this.Push(this.mmlToken(MML.mi(MML.chars(c)).With(def)));
    },

    /*
     *  Determine the extent of a number (pattern may need work)
     */
    Number: function (c) {
      var mml, n = this.string.slice(this.i-1).match(TEXDEF.number);
      if (n) {mml = MML.mn(n[0].replace(/[{}]/g,"")); this.i += n[0].length - 1}
        else {mml = MML.mo(MML.chars(c))}
      if (this.stack.env.font) {mml.mathvariant = this.stack.env.font}
      this.Push(this.mmlToken(mml));
    },
    
    /*
     *  Handle { and }
     */
    Open: function (c) {this.Push(STACKITEM.open())},
    Close: function (c) {this.Push(STACKITEM.close())},
    
    /*
     *  Handle tilde and spaces
     */
    Tilde: function (c) {this.Push(MML.mtext(MML.chars(NBSP)))},
    Space: function (c) {},
    
    /*
     *  Handle ^, _, and '
     */
    Superscript: function (c) {
      if (this.GetNext().match(/\d/)) // don't treat numbers as a unit
        {this.string = this.string.substr(0,this.i+1)+" "+this.string.substr(this.i+1)}
      var primes, base, top = this.stack.Top();
      if (top.type === "prime") {base = top.data[0]; primes = top.data[1]; this.stack.Pop()}
        else {base = this.stack.Prev(); if (!base) {base = MML.mi("")}}
      if (base.isEmbellishedWrapper) {base = base.data[0].data[0]}
      var movesupsub = base.movesupsub, position = base.sup;
      if ((base.type === "msubsup" && base.data[base.sup]) ||
          (base.type === "munderover" && base.data[base.over] && !base.subsupOK))
           {TEX.Error(["DoubleExponent","Double exponent: use braces to clarify"])}
      if (base.type !== "msubsup") {
        if (movesupsub) {
          if (base.type !== "munderover" || base.data[base.over]) {
            if (base.movablelimits && base.isa(MML.mi)) {base = this.mi2mo(base)}
            base = MML.munderover(base,null,null).With({movesupsub:true})
          }
          position = base.over;
        } else {
          base = MML.msubsup(base,null,null);
          position = base.sup;
        }
      }
      this.Push(STACKITEM.subsup(base).With({
        position: position, primes: primes, movesupsub: movesupsub
      }));
    },
    Subscript: function (c) {
      if (this.GetNext().match(/\d/)) // don't treat numbers as a unit
        {this.string = this.string.substr(0,this.i+1)+" "+this.string.substr(this.i+1)}
      var primes, base, top = this.stack.Top();
      if (top.type === "prime") {base = top.data[0]; primes = top.data[1]; this.stack.Pop()}
        else {base = this.stack.Prev(); if (!base) {base = MML.mi("")}}
      if (base.isEmbellishedWrapper) {base = base.data[0].data[0]}
      var movesupsub = base.movesupsub, position = base.sub;
      if ((base.type === "msubsup" && base.data[base.sub]) ||
          (base.type === "munderover" && base.data[base.under] && !base.subsupOK))
           {TEX.Error(["DoubleSubscripts","Double subscripts: use braces to clarify"])}
      if (base.type !== "msubsup") {
        if (movesupsub) {
          if (base.type !== "munderover" || base.data[base.under]) {
            if (base.movablelimits && base.isa(MML.mi)) {base = this.mi2mo(base)}
            base = MML.munderover(base,null,null).With({movesupsub:true})
          }
          position = base.under;
        } else {
          base = MML.msubsup(base,null,null);
          position = base.sub;
        }
      }
      this.Push(STACKITEM.subsup(base).With({
        position: position, primes: primes, movesupsub: movesupsub
      }));
    },
    PRIME: "\u2032", SMARTQUOTE: "\u2019",
    Prime: function (c) {
      var base = this.stack.Prev(); if (!base) {base = MML.mi()}
      if (base.type === "msubsup" && base.data[base.sup]) {
        TEX.Error(["DoubleExponentPrime",
                   "Prime causes double exponent: use braces to clarify"]);
      }
      var sup = ""; this.i--;
      do {sup += this.PRIME; this.i++, c = this.GetNext()}
        while (c === "'" || c === this.SMARTQUOTE);
      sup = ["","\u2032","\u2033","\u2034","\u2057"][sup.length] || sup;
      this.Push(STACKITEM.prime(base,this.mmlToken(MML.mo(sup))));
    },
    mi2mo: function (mi) {
      var mo = MML.mo();  mo.Append.apply(mo,mi.data); var id;
      for (id in mo.defaults)
        {if (mo.defaults.hasOwnProperty(id) && mi[id] != null) {mo[id] = mi[id]}}
      for (id in MML.copyAttributes)
        {if (MML.copyAttributes.hasOwnProperty(id) && mi[id] != null) {mo[id] = mi[id]}}
      mo.lspace = mo.rspace = "0";  // prevent mo from having space in NativeMML
      mo.useMMLspacing &= ~(mo.SPACE_ATTR.lspace | mo.SPACE_ATTR.rspace);  // don't count these explicit settings
      return mo;
    },
    
    /*
     *  Handle comments
     */
    Comment: function (c) {
      while (this.i < this.string.length && this.string.charAt(this.i) != "\n") {this.i++}
    },
    
    /*
     *  Handle hash marks outside of definitions
     */
    Hash: function (c) {
      TEX.Error(["CantUseHash1",
                 "You can't use 'macro parameter character #' in math mode"]);
    },
    
    /*
     *  Handle other characters (as <mo> elements)
     */
    Other: function (c) {
      var def, mo;
      if (this.stack.env.font) {def = {mathvariant: this.stack.env.font}}
      if (TEXDEF.remap[c]) {
        c = TEXDEF.remap[c];
        if (isArray(c)) {def = c[1]; c = c[0]}
        mo = MML.mo(MML.entity('#x'+c)).With(def);
      } else {
        mo = MML.mo(c).With(def);
      }
      if (mo.autoDefault("stretchy",true)) {mo.stretchy = false}
      if (mo.autoDefault("texClass",true) == "") {mo = MML.TeXAtom(mo)}
      this.Push(this.mmlToken(mo));
    },
    
    /************************************************************************/
    /*
     *   Macros
     */
    
    SetFont: function (name,font) {this.stack.env.font = font},
    SetStyle: function (name,texStyle,style,level) {
      this.stack.env.style = texStyle; this.stack.env.level = level;
      this.Push(STACKITEM.style().With({styles: {displaystyle: style, scriptlevel: level}}));
    },
    SetSize: function (name,size) {
      this.stack.env.size = size;
      this.Push(STACKITEM.style().With({styles: {mathsize: size+"em"}})); // convert to absolute?
    },

    Color: function (name) {
      var color = this.GetArgument(name);
      var old = this.stack.env.color; this.stack.env.color = color;
      var math = this.ParseArg(name);
      if (old) {this.stack.env.color} else {delete this.stack.env.color}
      this.Push(MML.mstyle(math).With({mathcolor: color}));
    },
    
    Spacer: function (name,space) {
      this.Push(MML.mspace().With({width: space, mathsize: MML.SIZE.NORMAL, scriptlevel:0}));
    },
    
    LeftRight: function (name) {
      this.Push(STACKITEM[name.substr(1)]().With({delim: this.GetDelimiter(name)}));
    },
    
    Middle: function (name) {
      var delim = this.GetDelimiter(name);
      this.Push(MML.TeXAtom().With({texClass:MML.TEXCLASS.CLOSE}));
      if (this.stack.Top().type !== "left")
        {TEX.Error(["MisplacedMiddle","%1 must be within \\left and \\right",name])}
      this.Push(MML.mo(delim).With({stretchy:true}));
      this.Push(MML.TeXAtom().With({texClass:MML.TEXCLASS.OPEN}));
    },
    
    NamedFn: function (name,id) {
      if (!id) {id = name.substr(1)};
      var mml = MML.mi(id).With({texClass: MML.TEXCLASS.OP});
      this.Push(STACKITEM.fn(this.mmlToken(mml)));
    },
    NamedOp: function (name,id) {
      if (!id) {id = name.substr(1)};
      id = id.replace(/&thinsp;/,"\u2006");
      var mml = MML.mo(id).With({
        movablelimits: true,
        movesupsub: true,
        form: MML.FORM.PREFIX,
        texClass: MML.TEXCLASS.OP
      });
      mml.useMMLspacing &= ~mml.SPACE_ATTR.form;  // don't count this explicit form setting
      this.Push(this.mmlToken(mml));
    },
    Limits: function (name,limits) {
      var op = this.stack.Prev("nopop");
      if (!op || (op.Get("texClass") !== MML.TEXCLASS.OP && op.movesupsub == null))
        {TEX.Error(["MisplacedLimits","%1 is allowed only on operators",name])}
      var top = this.stack.Top();
      if (op.type === "munderover" && !limits) {
        op = top.data[top.data.length-1] = MML.msubsup.apply(MML.subsup,op.data);
      } else if (op.type === "msubsup" && limits) {
        op = top.data[top.data.length-1] = MML.munderover.apply(MML.underover,op.data);
      }
      op.movesupsub = (limits ? true : false);
      op.Core().movablelimits = false;
      if (op.movablelimits) op.movablelimits = false;
    },
    
    Over: function (name,open,close) {
      var mml = STACKITEM.over().With({name: name});
      if (open || close) {
        mml.open = open; mml.close = close;
      } else if (name.match(/withdelims$/)) {
        mml.open  = this.GetDelimiter(name);
        mml.close = this.GetDelimiter(name);
      }
      if (name.match(/^\\above/)) {mml.thickness = this.GetDimen(name)}
      else if (name.match(/^\\atop/) || open || close) {mml.thickness = 0}
      this.Push(mml);
    },

    Frac: function (name) {
      var num = this.ParseArg(name);
      var den = this.ParseArg(name);
      this.Push(MML.mfrac(num,den));
    },

    Sqrt: function (name) {
      var n = this.GetBrackets(name), arg = this.GetArgument(name);
      if (arg === "\\frac") {arg += "{"+this.GetArgument(arg)+"}{"+this.GetArgument(arg)+"}"}
      var mml = TEX.Parse(arg,this.stack.env).mml();
      if (!n) {mml = MML.msqrt.apply(MML,mml.array())}
         else {mml = MML.mroot(mml,this.parseRoot(n))}
      this.Push(mml);
    },
    Root: function (name) {
      var n = this.GetUpTo(name,"\\of");
      var arg = this.ParseArg(name);
      this.Push(MML.mroot(arg,this.parseRoot(n)));
    },
    parseRoot: function (n) {
      var env = this.stack.env, inRoot = env.inRoot; env.inRoot = true;
      var parser = TEX.Parse(n,env); n = parser.mml(); var global = parser.stack.global;
      if (global.leftRoot || global.upRoot) {
        n = MML.mpadded(n);
        if (global.leftRoot) {n.width = global.leftRoot}
        if (global.upRoot) {n.voffset = global.upRoot; n.height = global.upRoot}
      }
      env.inRoot = inRoot;
      return n;
    },
    MoveRoot: function (name,id) {
      if (!this.stack.env.inRoot)
        {TEX.Error(["MisplacedMoveRoot","%1 can appear only within a root",name])}
      if (this.stack.global[id])
        {TEX.Error(["MultipleMoveRoot","Multiple use of %1",name])}
      var n = this.GetArgument(name);
      if (!n.match(/-?[0-9]+/))
        {TEX.Error(["IntegerArg","The argument to %1 must be an integer",name])}
      n = (n/15)+"em";
      if (n.substr(0,1) !== "-") {n = "+"+n}
      this.stack.global[id] = n;
    },
    
    Accent: function (name,accent,stretchy) {
      var c = this.ParseArg(name);
      var def = {accent: true}; if (this.stack.env.font) {def.mathvariant = this.stack.env.font}
      var mml = this.mmlToken(MML.mo(MML.entity("#x"+accent)).With(def));
      mml.stretchy = (stretchy ? true : false);
      var mo = (c.isEmbellished() ? c.CoreMO() : c);
      if (mo.isa(MML.mo)) mo.movablelimits = false;
      this.Push(MML.TeXAtom(MML.munderover(c,null,mml).With({accent: true})));
    },
    
    UnderOver: function (name,c,stack,noaccent) {
      var pos = {o: "over", u: "under"}[name.charAt(1)];
      var base = this.ParseArg(name);
      if (base.Get("movablelimits")) {base.movablelimits = false}
      if (base.isa(MML.munderover) && base.isEmbellished()) {
        base.Core().With({lspace:0,rspace:0}); // get spacing right for NativeMML
        base = MML.mrow(MML.mo().With({rspace:0}),base);  // add an empty <mi> so it's not embellished any more
      }
      var mml = MML.munderover(base,null,null);
      mml.SetData(
        mml[pos], 
        this.mmlToken(MML.mo(MML.entity("#x"+c)).With({stretchy:true, accent:!noaccent}))
      );
      if (stack) {mml = MML.TeXAtom(mml).With({texClass:MML.TEXCLASS.OP, movesupsub:true})}
      this.Push(mml.With({subsupOK:true}));
    },
    
    Overset: function (name) {
      var top = this.ParseArg(name), base = this.ParseArg(name);
      if (base.movablelimits) base.movablelimits = false;
      this.Push(MML.mover(base,top));
    },
    Underset: function (name) {
      var bot = this.ParseArg(name), base = this.ParseArg(name);
      if (base.movablelimits) base.movablelimits = false;
      this.Push(MML.munder(base,bot));
    },
    
    TeXAtom: function (name,mclass) {
      var def = {texClass: mclass}, mml;
      if (mclass == MML.TEXCLASS.OP) {
        def.movesupsub = def.movablelimits = true;
        var arg = this.GetArgument(name);
        var match = arg.match(/^\s*\\rm\s+([a-zA-Z0-9 ]+)$/);
        if (match) {
          def.mathvariant = MML.VARIANT.NORMAL;
          mml = STACKITEM.fn(this.mmlToken(MML.mi(match[1]).With(def)));
        } else {
          mml = STACKITEM.fn(MML.TeXAtom(TEX.Parse(arg,this.stack.env).mml()).With(def));
        }
      } else {mml = MML.TeXAtom(this.ParseArg(name)).With(def)}
      this.Push(mml);
    },
    
    MmlToken: function (name) {
      var type = this.GetArgument(name),
          attr = this.GetBrackets(name,"").replace(/^\s+/,""),
          data = this.GetArgument(name),
          def = {attrNames:[]}, match;
      if (!MML[type] || !MML[type].prototype.isToken)
        {TEX.Error(["NotMathMLToken","%1 is not a token element",type])}
      while (attr !== "") {
        match = attr.match(/^([a-z]+)\s*=\s*('[^']*'|"[^"]*"|[^ ,]*)\s*,?\s*/i);
        if (!match)
          {TEX.Error(["InvalidMathMLAttr","Invalid MathML attribute: %1",attr])}
        if (MML[type].prototype.defaults[match[1]] == null && !this.MmlTokenAllow[match[1]]) {
          TEX.Error(["UnknownAttrForElement",
                     "%1 is not a recognized attribute for %2",
                     match[1],type]);
        }
        var value = this.MmlFilterAttribute(match[1],match[2].replace(/^(['"])(.*)\1$/,"$2"));
        if (value) {
          if (value.toLowerCase() === "true") {value = true}
            else if (value.toLowerCase() === "false") {value = false}
          def[match[1]] = value;
          def.attrNames.push(match[1]);
        }
        attr = attr.substr(match[0].length);
      }
      this.Push(this.mmlToken(MML[type](data).With(def)));
    },
    MmlFilterAttribute: function (name,value) {return value},
    MmlTokenAllow: {
      fontfamily:1, fontsize:1, fontweight:1, fontstyle:1,
      color:1, background:1,
      id:1, "class":1, href:1, style:1
    },
    
    Strut: function (name) {
      this.Push(MML.mpadded(MML.mrow()).With({height: "8.6pt", depth: "3pt", width: 0}));
    },
    
    Phantom: function (name,v,h) {
      var box = MML.mphantom(this.ParseArg(name));
      if (v || h) {
        box = MML.mpadded(box);
        if (h) {box.height = box.depth = 0}
        if (v) {box.width = 0}
      }
      this.Push(MML.TeXAtom(box));
    },
    
    Smash: function (name) {
      var bt = this.trimSpaces(this.GetBrackets(name,""));
      var smash = MML.mpadded(this.ParseArg(name));
      switch (bt) {
        case "b": smash.depth = 0; break;
        case "t": smash.height = 0; break;
        default: smash.height = smash.depth = 0;
      }
      this.Push(MML.TeXAtom(smash));
    },
    
    Lap: function (name) {
      var mml = MML.mpadded(this.ParseArg(name)).With({width: 0});
      if (name === "\\llap") {mml.lspace = "-1width"}
      this.Push(MML.TeXAtom(mml));
    },
    
    RaiseLower: function (name) {
      var h = this.GetDimen(name);
      var item = STACKITEM.position().With({name: name, move: 'vertical'});
      if (h.charAt(0) === '-') {h = h.slice(1); name = {raise: "\\lower", lower: "\\raise"}[name.substr(1)]}
      if (name === "\\lower") {item.dh = '-'+h; item.dd = '+'+h} else {item.dh = '+'+h; item.dd = '-'+h}
      this.Push(item);
    },
    
    MoveLeftRight: function (name) {
      var h = this.GetDimen(name);
      var nh = (h.charAt(0) === '-' ? h.slice(1) : '-'+h);
      if (name === "\\moveleft") {var tmp = h; h = nh; nh = tmp}
      this.Push(STACKITEM.position().With({
        name: name, move: 'horizontal',
        left:  MML.mspace().With({width: h, mathsize: MML.SIZE.NORMAL}),
        right: MML.mspace().With({width: nh, mathsize: MML.SIZE.NORMAL})
      }));
    },
    
    Hskip: function (name) {
      this.Push(MML.mspace().With({width: this.GetDimen(name), mathsize: MML.SIZE.NORMAL}));
    },
    
    Rule: function (name,style) {
      var w = this.GetDimen(name),
          h = this.GetDimen(name),
          d = this.GetDimen(name);
      var def = {width:w, height:h, depth:d};
      if (style !== 'blank') {
        def.mathbackground = (this.stack.env.color || "black");
      }
      this.Push(MML.mspace().With(def));
    },
    rule: function (name) {
      var v = this.GetBrackets(name),
          w = this.GetDimen(name),
          h = this.GetDimen(name);
      var mml = MML.mspace().With({
        width: w, height:h,
        mathbackground: (this.stack.env.color || "black")
      });
      if (v) {
        mml = MML.mpadded(mml).With({voffset: v});
        if (v.match(/^\-/)) {
          mml.height = v;
          mml.depth = '+' + v.substr(1);
        } else {
          mml.height = '+' + v;
        }
      }
      this.Push(mml);
    },
    
    MakeBig: function (name,mclass,size) {
      size *= TEXDEF.p_height;
      size = String(size).replace(/(\.\d\d\d).+/,'$1')+"em";
      var delim = this.GetDelimiter(name,true);
      this.Push(MML.TeXAtom(MML.mo(delim).With({
        minsize: size, maxsize: size,
        fence: true, stretchy: true, symmetric: true
      })).With({texClass: mclass}));
    },
    
    BuildRel: function (name) {
      var top = this.ParseUpTo(name,"\\over");
      var bot = this.ParseArg(name);
      this.Push(MML.TeXAtom(MML.munderover(bot,null,top)).With({texClass: MML.TEXCLASS.REL}));
    },
    
    HBox: function (name,style) {
      this.Push.apply(this,this.InternalMath(this.GetArgument(name),style));
    },
    
    FBox: function (name) {
      this.Push(MML.menclose.apply(MML,this.InternalMath(this.GetArgument(name))).With({notation:"box"}));
    },
    
    Not: function (name) {
      this.Push(STACKITEM.not());
    },
    
    Dots: function (name) {
      this.Push(STACKITEM.dots().With({
        ldots: this.mmlToken(MML.mo(MML.entity("#x2026")).With({stretchy:false})),
        cdots: this.mmlToken(MML.mo(MML.entity("#x22EF")).With({stretchy:false}))
      }));
    },
    
    Require: function (name) {
      var file = this.GetArgument(name)
        .replace(/.*\//,"")            // remove any leading path
        .replace(/[^a-z0-9_.-]/ig,""); // remove illegal characters
      this.Extension(null,file);
    },
    
    Extension: function (name,file,array) {
      if (name && !typeof(name) === "string") {name = name.name}
      file = TEX.extensionDir+"/"+file;
      if (!file.match(/\.js$/)) {file += ".js"}
      if (!AJAX.loaded[AJAX.fileURL(file)]) {
        if (name != null) {delete TEXDEF[array || 'macros'][name.replace(/^\\/,"")]}
        HUB.RestartAfter(AJAX.Require(file));
      }
    },
    
    Macro: function (name,macro,argcount,def) {
      if (argcount) {
        var args = [];
        if (def != null) {
          var optional = this.GetBrackets(name);
          args.push(optional == null ? def : optional);
        }
        for (var i = args.length; i < argcount; i++) {args.push(this.GetArgument(name))}
        macro = this.SubstituteArgs(args,macro);
      }
      this.string = this.AddArgs(macro,this.string.slice(this.i));
      this.i = 0;
      if (++this.macroCount > TEX.config.MAXMACROS) {
        TEX.Error(["MaxMacroSub1",
                   "MathJax maximum macro substitution count exceeded; " +
                   "is there a recursive macro call?"]);
      }
    },
    
    Matrix: function (name,open,close,align,spacing,vspacing,style,cases,numbered) {
      var c = this.GetNext();
      if (c === "")
        {TEX.Error(["MissingArgFor","Missing argument for %1",name])}
      if (c === "{") {this.i++} else {this.string = c+"}"+this.string.slice(this.i+1); this.i = 0}
      var array = STACKITEM.array().With({
        requireClose: true,
        arraydef: {
          rowspacing: (vspacing||"4pt"),
          columnspacing: (spacing||"1em")
        }
      });
      if (cases)         {array.isCases = true}
      if (numbered)      {array.isNumbered = true; array.arraydef.side = numbered}
      if (open || close) {array.open = open; array.close = close}
      if (style === "D") {array.arraydef.displaystyle = true}
      if (align != null) {array.arraydef.columnalign = align}
      this.Push(array);
    },
    
    Entry: function (name) {
      this.Push(STACKITEM.cell().With({isEntry: true, name: name}));
      if (this.stack.Top().isCases) {
        //
        //  Make second column be in \text{...} (unless it is already
        //  in a \text{...}, for backward compatibility).
        //
        var string = this.string;
        var braces = 0, close = -1, i = this.i, m = string.length;
        //
        //  Look through the string character by character...
        //
        while (i < m) {
          var c = string.charAt(i);
          if (c === "{") {
            //
            //  Increase the nested brace count and go on
            //
            braces++;
            i++;
          } else if (c === "}") {
            //
            //  If there are too many close braces, just end (we will get an
            //    error message later when the rest of the string is parsed)
            //  Otherwise
            //    decrease the nested brace count,
            //    if it is now zero and we haven't already marked the end of the
            //      first brace group, record the position (use to check for \text{} later)
            //    go on to the next character.
            //
            if (braces === 0) {
              m = 0;
            } else {
              braces--;
              if (braces === 0 && close < 0) {
                close = i - this.i;
              }
              i++;
            }
          } else if (c === "&" && braces === 0) {
            //
            //  Extra alignment tabs are not allowed in cases
            //
            TEX.Error(["ExtraAlignTab","Extra alignment tab in \\cases text"]);
          } else if (c === "\\") {
            //
            //  If the macro is \cr or \\, end the search, otherwise skip the macro
            //  (multi-letter names don't matter, as we will skip the rest of the
            //   characters in the main loop)
            //
            if (string.substr(i).match(/^((\\cr)[^a-zA-Z]|\\\\)/)) {m = 0} else {i += 2}
          } else {
            //
            //  Go on to the next character
            //
            i++;
          }
        }
        //
        //  Check if the second column text is already in \text{},
        //  If not, process the second column as text and continue parsing from there,
        //    (otherwise process the second column as normal, since it is in \text{}
        //
        var text = string.substr(this.i,i-this.i);
        if (!text.match(/^\s*\\text[^a-zA-Z]/) || close !== text.replace(/\s+$/,'').length - 1) {
          this.Push.apply(this,this.InternalMath(text,0));
          this.i = i;
        }
      }
    },
    
    Cr: function (name) {
      this.Push(STACKITEM.cell().With({isCR: true, name: name}));
    },
    
    CrLaTeX: function (name) {
      var n;
      if (this.string.charAt(this.i) === "[") {
        n = this.GetBrackets(name,"").replace(/ /g,"").replace(/,/,".");
        if (n && !this.matchDimen(n)) {
          TEX.Error(["BracketMustBeDimension",
                     "Bracket argument to %1 must be a dimension",name]);
        }
      }
      this.Push(STACKITEM.cell().With({isCR: true, name: name, linebreak: true}));
      var top = this.stack.Top();
      if (top.isa(STACKITEM.array)) {
        if (n && top.arraydef.rowspacing) {
          var rows = top.arraydef.rowspacing.split(/ /);
          if (!top.rowspacing) {top.rowspacing = this.dimen2em(rows[0])}
          while (rows.length < top.table.length) {rows.push(this.Em(top.rowspacing))}
          rows[top.table.length-1] = this.Em(Math.max(0,top.rowspacing+this.dimen2em(n)));
          top.arraydef.rowspacing = rows.join(' ');
        }
      } else {
        if (n) {this.Push(MML.mspace().With({depth:n}))}
        this.Push(MML.mspace().With({linebreak:MML.LINEBREAK.NEWLINE}));
      }
    },
    emPerInch: 7.2,
    pxPerInch: 72,
    matchDimen: function (dim) {
      return dim.match(/^(-?(?:\.\d+|\d+(?:\.\d*)?))(px|pt|em|ex|mu|pc|in|mm|cm)$/);
    },
    dimen2em: function (dim) {
      var match = this.matchDimen(dim);
      var m = parseFloat(match[1]||"1"), unit = match[2];
      if (unit === "em") {return m}
      if (unit === "ex") {return m * .43}
      if (unit === "pt") {return m / 10}                    // 10 pt to an em
      if (unit === "pc") {return m * 1.2}                   // 12 pt to a pc
      if (unit === "px") {return m * this.emPerInch / this.pxPerInch}
      if (unit === "in") {return m * this.emPerInch}
      if (unit === "cm") {return m * this.emPerInch / 2.54} // 2.54 cm to an inch
      if (unit === "mm") {return m * this.emPerInch / 25.4} // 10 mm to a cm
      if (unit === "mu") {return m / 18}
      return 0;
    },
    Em: function (m) {
      if (Math.abs(m) < .0006) {return "0em"}
      return m.toFixed(3).replace(/\.?0+$/,"") + "em";
    },
    
    HLine: function (name,style) {
      if (style == null) {style = "solid"}
      var top = this.stack.Top();
      if (!top.isa(STACKITEM.array) || top.data.length)
        {TEX.Error(["Misplaced","Misplaced %1",name])}
      if (top.table.length == 0) {
        top.frame.push("top");
      } else {
        var lines = (top.arraydef.rowlines ? top.arraydef.rowlines.split(/ /) : []);
        while (lines.length < top.table.length) {lines.push("none")}
        lines[top.table.length-1] = style;
        top.arraydef.rowlines = lines.join(' ');
      }
    },
    
    HFill: function (name) {
      var top = this.stack.Top();
      if (top.isa(STACKITEM.array)) top.hfill.push(top.data.length);
        else TEX.Error(["UnsupportedHFill","Unsupported use of %1",name]);
    },
    

    
   /************************************************************************/
   /*
    *   LaTeX environments
    */

    BeginEnd: function (name) {
      var env = this.GetArgument(name), isEnd = false;
      if (env.match(/^\\end\\/)) {isEnd = true; env = env.substr(5)} // special \end{} for \newenvironment environments
      if (env.match(/\\/i)) {TEX.Error(["InvalidEnv","Invalid environment name '%1'",env])}
      var cmd = this.envFindName(env);
      if (!cmd) {TEX.Error(["UnknownEnv","Unknown environment '%1'",env])}
      if (!isArray(cmd)) {cmd = [cmd]}
      var end = (isArray(cmd[1]) ? cmd[1][0] : cmd[1]);
      var mml = STACKITEM.begin().With({name: env, end: end, parse:this});
      if (name === "\\end") {
        if (!isEnd && isArray(cmd[1]) && this[cmd[1][1]]) {
          mml = this[cmd[1][1]].apply(this,[mml].concat(cmd.slice(2)));
        } else {
          mml = STACKITEM.end().With({name: env});
        }
      } else {
        if (++this.macroCount > TEX.config.MAXMACROS) {
          TEX.Error(["MaxMacroSub2",
                     "MathJax maximum substitution count exceeded; " +
                     "is there a recursive latex environment?"]);
        }
        if (cmd[0] && this[cmd[0]]) {mml = this[cmd[0]].apply(this,[mml].concat(cmd.slice(2)))}
      }
      this.Push(mml);
    },
    envFindName: function (name) {return TEXDEF.environment[name]},
    
    Equation: function (begin,row) {return row},
    
    ExtensionEnv: function (begin,file) {this.Extension(begin.name,file,"environment")},
    
    Array: function (begin,open,close,align,spacing,vspacing,style,raggedHeight) {
      if (!align) {align = this.GetArgument("\\begin{"+begin.name+"}")}
      var lines = ("c"+align).replace(/[^clr|:]/g,'').replace(/[^|:]([|:])+/g,'$1');
      align = align.replace(/[^clr]/g,'').split('').join(' ');
      align = align.replace(/l/g,'left').replace(/r/g,'right').replace(/c/g,'center');
      var array = STACKITEM.array().With({
        arraydef: {
          columnalign: align,
          columnspacing: (spacing||"1em"),
          rowspacing: (vspacing||"4pt")
        }
      });
      if (lines.match(/[|:]/)) {
        if (lines.charAt(0).match(/[|:]/)) {array.frame.push("left"); array.frame.dashed = lines.charAt(0) === ":"}
        if (lines.charAt(lines.length-1).match(/[|:]/)) {array.frame.push("right")}
        lines = lines.substr(1,lines.length-2);
        array.arraydef.columnlines =
          lines.split('').join(' ').replace(/[^|: ]/g,'none').replace(/\|/g,'solid').replace(/:/g,'dashed');
      }
      if (open)  {array.open  = this.convertDelimiter(open)}
      if (close) {array.close = this.convertDelimiter(close)}
      if (style === "D") {array.arraydef.displaystyle = true}
         else if (style) {array.arraydef.displaystyle = false}
      if (style === "S") {array.arraydef.scriptlevel = 1} // FIXME: should use mstyle?
      if (raggedHeight)  {array.arraydef.useHeight = false}
      this.Push(begin);
      return array;
    },
    
    AlignedArray: function (begin) {
      var align = this.GetBrackets("\\begin{"+begin.name+"}");
      return this.setArrayAlign(this.Array.apply(this,arguments),align);
    },
    setArrayAlign: function (array,align) {
      align = this.trimSpaces(align||"");
      if (align === "t") {array.arraydef.align = "baseline 1"}
      else if (align === "b") {array.arraydef.align = "baseline -1"}
      else if (align === "c") {array.arraydef.align = "center"}
      else if (align) {array.arraydef.align = align} // FIXME: should be an error?
      return array;
    },
    
    /************************************************************************/
    /*
     *   String handling routines
     */

    /*
     *  Convert delimiter to character
     */
    convertDelimiter: function (c) {
      if (c) {c = TEXDEF.delimiter[c]}
      if (c == null) {return null}
      if (isArray(c)) {c = c[0]}
      if (c.length === 4) {c = String.fromCharCode(parseInt(c,16))}
      return c;
    },

    /*
     *  Trim spaces from a string
     */
    trimSpaces: function (text) {
      if (typeof(text) != 'string') {return text}
      var TEXT = text.replace(/^\s+|\s+$/g,'');
      if (TEXT.match(/\\$/) && text.match(/ $/)) TEXT += " ";
      return TEXT;
    },

    /*
     *   Check if the next character is a space
     */
    nextIsSpace: function () {
      return this.string.charAt(this.i).match(/\s/);
    },
    
    /*
     *  Get the next non-space character
     */
    GetNext: function () {
      while (this.nextIsSpace()) {this.i++}
      return this.string.charAt(this.i);
    },
  
    /*
     *  Get and return a control-sequence name
     */
    GetCS: function () {
      var CS = this.string.slice(this.i).match(/^([a-z]+|.) ?/i);
      if (CS) {this.i += CS[1].length; return CS[1]} else {this.i++; return " "}
    },

    /*
     *  Get and return a TeX argument (either a single character or control sequence,
     *  or the contents of the next set of braces).
     */
    GetArgument: function (name,noneOK) {
      switch (this.GetNext()) {
       case "":
        if (!noneOK) {TEX.Error(["MissingArgFor","Missing argument for %1",name])}
        return null;
       case '}':
        if (!noneOK) {
          TEX.Error(["ExtraCloseMissingOpen",
                     "Extra close brace or missing open brace"]);
        }
        return null;
       case '\\':
        this.i++; return "\\"+this.GetCS();
       case '{':
        var j = ++this.i, parens = 1;
        while (this.i < this.string.length) {
          switch (this.string.charAt(this.i++)) {
           case '\\':  this.i++; break;
           case '{':   parens++; break;
           case '}':
            if (--parens == 0) {return this.string.slice(j,this.i-1)}
            break;
          }
        }
        TEX.Error(["MissingCloseBrace","Missing close brace"]);
        break;
      }        
      return this.string.charAt(this.i++);
    },
    
    /*
     *  Get an optional LaTeX argument in brackets
     */
    GetBrackets: function (name,def) {
      if (this.GetNext() != '[') {return def};
      var j = ++this.i, parens = 0;
      while (this.i < this.string.length) {
        switch (this.string.charAt(this.i++)) {
         case '{':   parens++; break;
         case '\\':  this.i++; break;
         case '}':
          if (parens-- <= 0) {
            TEX.Error(["ExtraCloseLooking",
                       "Extra close brace while looking for %1","']'"]);
          }
          break;   
         case ']':
          if (parens == 0) {return this.string.slice(j,this.i-1)}
          break;
        }
      }
      TEX.Error(["MissingCloseBracket",
                 "Couldn't find closing ']' for argument to %1",name]);
    },
  
    /*
     *  Get the name of a delimiter (check it in the delimiter list).
     */
    GetDelimiter: function (name,braceOK) {
      while (this.nextIsSpace()) {this.i++}
      var c = this.string.charAt(this.i); this.i++;
      if (this.i <= this.string.length) {
        if (c == "\\") {c += this.GetCS(name)}
        else if (c === "{" && braceOK) {this.i--; c = this.GetArgument(name)}
        if (TEXDEF.delimiter[c] != null) {return this.convertDelimiter(c)}
      }
      TEX.Error(["MissingOrUnrecognizedDelim",
                 "Missing or unrecognized delimiter for %1",name]);
    },

    /*
     *  Get a dimension (including its units).
     */
    GetDimen: function (name) {
      var dimen;
      if (this.nextIsSpace()) {this.i++}
      if (this.string.charAt(this.i) == '{') {
        dimen = this.GetArgument(name);
        if (dimen.match(/^\s*([-+]?([.,]\d+|\d+([.,]\d*)?))\s*(pt|em|ex|mu|px|mm|cm|in|pc)\s*$/))
          {return dimen.replace(/ /g,"").replace(/,/,".")}
      } else {
        dimen = this.string.slice(this.i);
        var match = dimen.match(/^\s*(([-+]?([.,]\d+|\d+([.,]\d*)?))\s*(pt|em|ex|mu|px|mm|cm|in|pc)) ?/);
        if (match) {
          this.i += match[0].length;
          return match[1].replace(/ /g,"").replace(/,/,".");
        }
      }
      TEX.Error(["MissingDimOrUnits",
                 "Missing dimension or its units for %1",name]);
    },
    
    /*
     *  Get everything up to the given control sequence (token)
     */
    GetUpTo: function (name,token) {
      while (this.nextIsSpace()) {this.i++}
      var j = this.i, k, c, parens = 0;
      while (this.i < this.string.length) {
        k = this.i; c = this.string.charAt(this.i++);
        switch (c) {
         case '\\':  c += this.GetCS(); break;
         case '{':   parens++; break;
         case '}':
          if (parens == 0) {
            TEX.Error(["ExtraCloseLooking",
                       "Extra close brace while looking for %1",token])
          }
          parens--;
          break;
        }
        if (parens == 0 && c == token) {return this.string.slice(j,k)}
      }
      TEX.Error(["TokenNotFoundForCommand",
                 "Couldn't find %1 for %2",token,name]);
    },

    /*
     *  Parse various substrings
     */
    ParseArg: function (name) {return TEX.Parse(this.GetArgument(name),this.stack.env).mml()},
    ParseUpTo: function (name,token) {return TEX.Parse(this.GetUpTo(name,token),this.stack.env).mml()},
    
    /*
     *  Break up a string into text and math blocks
     */
    InternalMath: function (text,level) {
      var def = (this.stack.env.font ? {mathvariant: this.stack.env.font} : {});
      var mml = [], i = 0, k = 0, c, match = '', braces = 0;
      if (text.match(/\\?[${}\\]|\\\(|\\(eq)?ref\s*\{/)) {
        while (i < text.length) {
          c = text.charAt(i++);
          if (c === '$') {
            if (match === '$' && braces === 0) {
              mml.push(MML.TeXAtom(TEX.Parse(text.slice(k,i-1),{}).mml()));
              match = ''; k = i;
            } else if (match === '') {
              if (k < i-1) mml.push(this.InternalText(text.slice(k,i-1),def));
              match = '$'; k = i;
            }
          } else if (c === '{' && match !== '') {
            braces++;
          } else if (c === '}') {
            if (match === '}' && braces === 0) {
              mml.push(MML.TeXAtom(TEX.Parse(text.slice(k,i),{}).mml().With(def)));
              match = ''; k = i;
            } else if (match !== '') {
              if (braces) braces--;
            }
          } else if (c === '\\') {
            if (match === '' && text.substr(i).match(/^(eq)?ref\s*\{/)) {
              var len = RegExp["$&"].length;
              if (k < i-1) mml.push(this.InternalText(text.slice(k,i-1),def));
              match = '}'; k = i-1; i += len;
            } else {
              c = text.charAt(i++);
              if (c === '(' && match === '') {
                if (k < i-2) mml.push(this.InternalText(text.slice(k,i-2),def));
                match = ')'; k = i;
              } else if (c === ')' && match === ')' && braces === 0) {
                mml.push(MML.TeXAtom(TEX.Parse(text.slice(k,i-2),{}).mml()));
                match = ''; k = i;
              } else if (c.match(/[${}\\]/) && match === '')  {
                i--; text = text.substr(0,i-1) + text.substr(i); // remove \ from \$, \{, \}, or \\
              }
            }
          }
        }
        if (match !== '') TEX.Error(["MathNotTerminated","Math not terminated in text box"]);
      }
      if (k < text.length) mml.push(this.InternalText(text.slice(k),def));
      if (level != null) {
        mml = [MML.mstyle.apply(MML,mml).With({displaystyle:false,scriptlevel:level})];
      } else if (mml.length > 1) {
        mml = [MML.mrow.apply(MML,mml)];
      }
      return mml;
    },
    InternalText: function (text,def) {
      text = text.replace(/^\s+/,NBSP).replace(/\s+$/,NBSP);
      return MML.mtext(MML.chars(text)).With(def);
    },

    /*
     *  Replace macro paramters with their values
     */
    SubstituteArgs: function (args,string) {
      var text = ''; var newstring = ''; var c; var i = 0;
      while (i < string.length) {
        c = string.charAt(i++);
        if (c === "\\") {text += c + string.charAt(i++)}
        else if (c === '#') {
          c = string.charAt(i++);
          if (c === '#') {text += c} else {
            if (!c.match(/[1-9]/) || c > args.length) {
              TEX.Error(["IllegalMacroParam",
                         "Illegal macro parameter reference"]);
            }
            newstring = this.AddArgs(this.AddArgs(newstring,text),args[c-1]);
            text = '';
          }
        } else {text += c}
      }
      return this.AddArgs(newstring,text);
    },
    
    /*
     *  Make sure that macros are followed by a space if their names
     *  could accidentally be continued into the following text.
     */
    AddArgs: function (s1,s2) {
      if (s2.match(/^[a-z]/i) && s1.match(/(^|[^\\])(\\\\)*\\[a-z]+$/i)) {s1 += ' '}
      if (s1.length + s2.length > TEX.config.MAXBUFFER) {
        TEX.Error(["MaxBufferSize",
                   "MathJax internal buffer size exceeded; is there a recursive macro call?"]);
      }
      return s1+s2;
    }
    
  });
  
  /************************************************************************/

  TEX.Augment({
    Stack: STACK, Parse: PARSE, Definitions: TEXDEF, Startup: STARTUP,
    
    config: {
      MAXMACROS: 10000,    // maximum number of macro substitutions per equation
      MAXBUFFER: 5*1024    // maximum size of TeX string to process
    },
    
    sourceMenuTitle: /*_(MathMenu)*/ ["TeXCommands","TeX Commands"],
    annotationEncoding: "application/x-tex",

    prefilterHooks: MathJax.Callback.Hooks(true),    // hooks to run before processing TeX
    postfilterHooks: MathJax.Callback.Hooks(true),   // hooks to run after processing TeX
    
    //
    //  Check if AMSmath extension must be loaded and push
    //    it on the extensions array, if needed
    //
    Config: function () {
      this.SUPER(arguments).Config.apply(this,arguments);
      if (this.config.equationNumbers.autoNumber !== "none") {
        if (!this.config.extensions) {this.config.extensions = []}
        this.config.extensions.push("AMSmath.js");
      }
    },

    //
    //  Convert TeX to ElementJax
    //
    Translate: function (script) {
      var mml, isError = false, math = MathJax.HTML.getScript(script);
      var display = (script.type.replace(/\n/g," ").match(/(;|\s|\n)mode\s*=\s*display(;|\s|\n|$)/) != null);
      var data = {math:math, display:display, script:script};
      var callback = this.prefilterHooks.Execute(data); if (callback) return callback;
      math = data.math;
      try {
        mml = TEX.Parse(math).mml();
      } catch(err) {
        if (!err.texError) {throw err}
        mml = this.formatError(err,math,display,script);
        isError = true;
      }
      if (mml.isa(MML.mtable) && mml.displaystyle === "inherit") mml.displaystyle = display; // for tagged equations
      if (mml.inferred) {mml = MML.apply(MathJax.ElementJax,mml.data)} else {mml = MML(mml)}
      if (display) {mml.root.display = "block"}
      if (isError) {mml.texError = true}
      data.math = mml; 
      return this.postfilterHooks.Execute(data) || data.math;
    },
    prefilterMath: function (math,displaystyle,script) {
      return math;
    },
    postfilterMath: function (math,displaystyle,script) {
      this.combineRelations(math.root);
      return math;
    },
    formatError: function (err,math,display,script) {
      var message = err.message.replace(/\n.*/,"");
      HUB.signal.Post(["TeX Jax - parse error",message,math,display,script]);
      return MML.Error(message);
    },

    //
    //  Produce an error and stop processing this equation
    //
    Error: function (message) {
      //
      //  Translate message if it is ["id","message",args]
      //
      if (isArray(message)) {message = _.apply(_,message)}
      throw HUB.Insert(Error(message),{texError: true});
    },
    
    //
    //  Add a user-defined macro to the macro list
    //
    Macro: function (name,def,argn) {
      TEXDEF.macros[name] = ['Macro'].concat([].slice.call(arguments,1));
      TEXDEF.macros[name].isUser = true;
    },
    
    /*
     *  Create an mrow that has stretchy delimiters at either end, as needed
     */
    fenced: function (open,mml,close) {
      var mrow = MML.mrow().With({open:open, close:close, texClass:MML.TEXCLASS.INNER});
      mrow.Append(
        MML.mo(open).With({fence:true, stretchy:true, symmetric:true, texClass:MML.TEXCLASS.OPEN}),
        mml,
        MML.mo(close).With({fence:true, stretchy:true, symmetric:true, texClass:MML.TEXCLASS.CLOSE})
      );
      return mrow;
    },
    /*
     *  Create an mrow that has \mathchoice using \bigg and \big for the delimiters
     */
    fixedFence: function (open,mml,close) {
      var mrow = MML.mrow().With({open:open, close:close, texClass:MML.TEXCLASS.ORD});
      if (open) {mrow.Append(this.mathPalette(open,"l"))}
      if (mml.type === "mrow") {mrow.Append.apply(mrow,mml.data)} else {mrow.Append(mml)}
      if (close) {mrow.Append(this.mathPalette(close,"r"))}
      return mrow;
    },
    mathPalette: function (fence,side) {
      if (fence === '{' || fence === '}') {fence = "\\"+fence}
      var D = '{\\bigg'+side+' '+fence+'}', T = '{\\big'+side+' '+fence+'}';
      return TEX.Parse('\\mathchoice'+D+T+T+T,{}).mml();
    },
    
    //
    //  Combine adjacent <mo> elements that are relations
    //    (since MathML treats the spacing very differently)
    //
    combineRelations: function (mml) {
      var i, m, m1, m2;
      for (i = 0, m = mml.data.length; i < m; i++) {
        if (mml.data[i]) {
          if (mml.isa(MML.mrow)) {
            while (i+1 < m && (m1 = mml.data[i]) && (m2 = mml.data[i+1]) &&
                   m1.isa(MML.mo) && m2.isa(MML.mo) &&
                   m1.Get("texClass") === MML.TEXCLASS.REL &&
                   m2.Get("texClass") === MML.TEXCLASS.REL) {
              if (m1.variantForm == m2.variantForm &&
                  m1.Get("mathvariant") == m2.Get("mathvariant") && m1.style == m2.style &&
                  m1["class"] == m2["class"] && !m1.id && !m2.id) {
                m1.Append.apply(m1,m2.data);
                mml.data.splice(i+1,1); m--;
              } else {
                m1.rspace = m2.lspace = "0pt"; i++;
              }
            }
          }
          if (!mml.data[i].isToken) {this.combineRelations(mml.data[i])}
        }
      }
    }
  });

  //
  //  Add the default filters
  //
  TEX.prefilterHooks.Add(function (data) {
    data.math = TEX.prefilterMath(data.math,data.display,data.script);
  });
  TEX.postfilterHooks.Add(function (data) {
    data.math = TEX.postfilterMath(data.math,data.display,data.script);
  });

  TEX.loadComplete("jax.js");
  
})(MathJax.InputJax.TeX,MathJax.Hub,MathJax.Ajax);

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/config.js
 *  
 *  Initializes the SVG OutputJax (the main definition is in
 *  MathJax/jax/input/SVG/jax.js, which is loaded when needed).
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.OutputJax.SVG = MathJax.OutputJax({
  id: "SVG",
  version: "2.7.2",
  directory: MathJax.OutputJax.directory + "/SVG",
  extensionDir: MathJax.OutputJax.extensionDir + "/SVG",
  autoloadDir: MathJax.OutputJax.directory + "/SVG/autoload",
  fontDir: MathJax.OutputJax.directory + "/SVG/fonts", // font name added later
  
  config: {
    scale: 100, minScaleAdjust: 50, // global math scaling factor, and minimum adjusted scale factor
    font: "TeX",                    // currently the only font available
    blacker: 1,                     // stroke-width to make fonts blacker
    mtextFontInherit: false,        // to make <mtext> be in page font rather than MathJax font
    undefinedFamily: "STIXGeneral,'Arial Unicode MS',serif",  // fonts to use for missing characters

    addMMLclasses: false,           // keep MathML structure and use CSS classes to mark elements
    useFontCache: true,             // use <use> elements to re-use font paths rather than repeat paths every time
    useGlobalCache: true,           // store fonts in a global <defs> for use in all equations, or one in each equation

    EqnChunk: (MathJax.Hub.Browser.isMobile ? 10: 50),
                                    // number of equations to process before showing them
    EqnChunkFactor: 1.5,            // chunk size is multiplied by this after each chunk
    EqnChunkDelay: 100,             // milliseconds to delay between chunks (to let browser
                                    //   respond to other events)

    linebreaks: {
      automatic: false,   // when false, only process linebreak="newline",
                          // when true, insert line breaks automatically in long expressions.

      width: "container" // maximum width of a line for automatic line breaks (e.g. "30em").
                         // use "container" to compute size from containing element,
                         // use "nn% container" for a portion of the container,
                         // use "nn%" for a portion of the window size
    },
    
    merrorStyle: {
      fontSize:"90%", color:"#C00", background:"#FF8",
      border: "1px solid #C00", padding:"3px"
    },
    
    styles: {
      ".MathJax_SVG_Display": {
        "text-align": "center",
        margin:       "1em 0em"
      },
      
      //
      //  For mtextFontInherit version of \texttt{}
      //
      ".MathJax_SVG .MJX-monospace": {
        "font-family": "monospace"
      },
      
      //
      //  For mtextFontInherit version of \textsf{}
      //
      ".MathJax_SVG .MJX-sans-serif": {
        "font-family": "sans-serif"
      },
      
      //
      //  For tooltips
      //
      "#MathJax_SVG_Tooltip": {
        "background-color": "InfoBackground", color: "InfoText",
        border: "1px solid black",
        "box-shadow": "2px 2px 5px #AAAAAA",         // Opera 10.5
        "-webkit-box-shadow": "2px 2px 5px #AAAAAA", // Safari 3 and Chrome
        "-moz-box-shadow": "2px 2px 5px #AAAAAA",    // Forefox 3.5
        "-khtml-box-shadow": "2px 2px 5px #AAAAAA",  // Konqueror
        padding: "3px 4px",
        "z-index": 401
      }
    }
  }
});

if (!MathJax.Hub.config.delayJaxRegistration) {MathJax.OutputJax.SVG.Register("jax/mml")}

MathJax.OutputJax.SVG.loadComplete("config.js");

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/jax.js
 *
 *  Implements the SVG OutputJax that displays mathematics using
 *  SVG (or VML in IE) to position the characters from math fonts
 *  in their proper locations.
 *  
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


(function (AJAX,HUB,HTML,SVG) {
  var MML;
  var isArray = MathJax.Object.isArray;

  var SVGNS   = "http://www.w3.org/2000/svg";
  var XLINKNS = "http://www.w3.org/1999/xlink";

  var EVENT, TOUCH, HOVER; // filled in later

  //
  //  Get the URL of the page (for use with xlink:href) when there
  //  is a <base> element on the page.
  //  
  var SVGURL = (document.getElementsByTagName("base").length === 0) ? "" :
                String(document.location).replace(/#.*$/,"");

  SVG.Augment({
    HFUZZ: 2,     // adjustments for height and depth of final svg element
    DFUZZ: 2,     //   to get baselines right (fragile).
    
    config: {
      styles: {
        ".MathJax_SVG": {
          "display":         "inline",
          "font-style":      "normal",
          "font-weight":     "normal",
          "line-height":     "normal",
          "font-size":       "100%",
          "font-size-adjust":"none",
          "text-indent":     0,
          "text-align":      "left",
          "text-transform":  "none",
          "letter-spacing":  "normal",
          "word-spacing":    "normal",
          "word-wrap":       "normal",
          "white-space":     "nowrap",
          "float":           "none",
          "direction":       "ltr",
          "max-width": "none", "max-height": "none",
          "min-width": 0, "min-height": 0,
          border: 0, padding: 0, margin: 0
        },

        ".MathJax_SVG_Display": {
          position: "relative",
          display: "block!important",
          "text-indent": 0,
          "max-width": "none", "max-height": "none",
          "min-width": 0, "min-height": 0,
          width: "100%"
        },
        
        ".MathJax_SVG *": {
          transition: "none",
          "-webkit-transition": "none",
          "-moz-transition": "none",
          "-ms-transition": "none",
          "-o-transition": "none"
        },
        
        ".mjx-svg-href": {
          fill: "blue", stroke: "blue"
        },

        ".MathJax_SVG_Processing": {
          visibility: "hidden", position:"absolute", top:0, left:0,
          width:0, height: 0, overflow:"hidden", display:"block!important"
        },
        ".MathJax_SVG_Processed": {display:"none!important"},
        
        ".MathJax_SVG_ExBox": {
          display:"block!important", overflow:"hidden",
          width:"1px", height:"60ex",
          "min-height": 0, "max-height":"none",
          padding:0, border: 0, margin: 0
        },
        ".MathJax_SVG_LineBox": {display: "table!important"},
        ".MathJax_SVG_LineBox span": {
          display: "table-cell!important",
          width: "10000em!important",
          "min-width":0, "max-width":"none",
          padding:0, border:0, margin:0
        },
        
        "#MathJax_SVG_Tooltip": {
          position: "absolute", left: 0, top: 0,
          width: "auto", height: "auto",
          display: "none"
        }
      }
    },

    hideProcessedMath: true,           // use display:none until all math is processed

    fontNames: ["TeX","STIX","STIX-Web","Asana-Math",
                "Gyre-Termes","Gyre-Pagella","Latin-Modern","Neo-Euler"],


    Config: function () {
      this.SUPER(arguments).Config.apply(this,arguments);
      var settings = HUB.config.menuSettings, config = this.config, font = settings.font;
      if (settings.scale) {config.scale = settings.scale}
      if (font && font !== "Auto") {
        font = font.replace(/(Local|Web|Image)$/i,"");
        font = font.replace(/([a-z])([A-Z])/,"$1-$2");
        this.fontInUse = font;
      } else {
        this.fontInUse = config.font || "TeX";
      }
      if (this.fontNames.indexOf(this.fontInUse) < 0) {this.fontInUse = "TeX"}
      this.fontDir += "/" + this.fontInUse;
      if (!this.require) {this.require = []}
      this.require.push(this.fontDir+"/fontdata.js");
      this.require.push(MathJax.OutputJax.extensionDir+"/MathEvents.js");
    },

    Startup: function () {
      //  Set up event handling
      EVENT = MathJax.Extension.MathEvents.Event;
      TOUCH = MathJax.Extension.MathEvents.Touch;
      HOVER = MathJax.Extension.MathEvents.Hover;
      this.ContextMenu = EVENT.ContextMenu;
      this.Mousedown   = EVENT.AltContextMenu;
      this.Mouseover   = HOVER.Mouseover;
      this.Mouseout    = HOVER.Mouseout;
      this.Mousemove   = HOVER.Mousemove;

      // Make hidden div for doing tests and storing global SVG <defs>
      this.hiddenDiv = HTML.Element("div",{
        style:{visibility:"hidden", overflow:"hidden", position:"absolute", top:0,
               height:"1px", width: "auto", padding:0, border:0, margin:0,
               textAlign:"left", textIndent:0, textTransform:"none",
               lineHeight:"normal", letterSpacing:"normal", wordSpacing:"normal"}
      });
      if (!document.body.firstChild) {document.body.appendChild(this.hiddenDiv)}
        else {document.body.insertBefore(this.hiddenDiv,document.body.firstChild)}
      this.hiddenDiv = HTML.addElement(this.hiddenDiv,"div",{id:"MathJax_SVG_Hidden"});

      // Determine pixels-per-inch and em-size
      var div = HTML.addElement(this.hiddenDiv,"div",{style:{width:"5in"}});
      this.pxPerInch = div.offsetWidth/5; this.hiddenDiv.removeChild(div);
      
      // Used for measuring text sizes
      this.textSVG = this.Element("svg");
      
      // Global defs for font glyphs
      BBOX.GLYPH.defs = this.addElement(this.addElement(this.hiddenDiv.parentNode,"svg"),
                                          "defs",{id:"MathJax_SVG_glyphs"});

       // Used in preTranslate to get scaling factors
      this.ExSpan = HTML.Element("span",
        {style:{position:"absolute","font-size-adjust":"none"}},
        [["span",{className:"MathJax_SVG_ExBox"}]]
      );

      // Used in preTranslate to get linebreak width
      this.linebreakSpan = HTML.Element("span",{className:"MathJax_SVG_LineBox"},[["span"]]);

      // Set up styles
      return AJAX.Styles(this.config.styles,["InitializeSVG",this]);
    },
    
    //
    //  Handle initialization that requires styles to be set up
    //
    InitializeSVG: function () {
      //
      //  Get the default sizes (need styles in place to do this)
      //
      document.body.appendChild(this.ExSpan);
      document.body.appendChild(this.linebreakSpan);
      this.defaultEx    = this.ExSpan.firstChild.offsetHeight/60;
      this.defaultWidth = this.linebreakSpan.firstChild.offsetWidth;
      document.body.removeChild(this.linebreakSpan);
      document.body.removeChild(this.ExSpan);
    },

    preTranslate: function (state) {
      var scripts = state.jax[this.id], i, m = scripts.length, n,
          script, prev, span, div, test, jax, ex, em, maxwidth, relwidth = false, cwidth,
          linebreak = this.config.linebreaks.automatic, width = this.config.linebreaks.width;
      if (linebreak) {
        relwidth = (width.match(/^\s*(\d+(\.\d*)?%\s*)?container\s*$/) != null);
        if (relwidth) {width = width.replace(/\s*container\s*/,"")}
          else {maxwidth = this.defaultWidth}
        if (width === "") {width = "100%"}
      } else {maxwidth = 100000} // a big width, so no implicit line breaks
      //
      //  Loop through the scripts
      //
      for (i = 0; i < m; i++) {
        script = scripts[i]; if (!script.parentNode) continue;
        //
        //  Remove any existing output
        //
        prev = script.previousSibling;
        if (prev && String(prev.className).match(/^MathJax(_SVG)?(_Display)?( MathJax(_SVG)?_Process(ing|ed))?$/))
          {prev.parentNode.removeChild(prev)}
        if (script.MathJax.preview) script.MathJax.preview.style.display = "none";
        //
        //  Add the span, and a div if in display mode,
        //  then set the role and mark it as being processed
        //
        jax = script.MathJax.elementJax; if (!jax) continue;
        jax.SVG = {
          display: (jax.root.Get("display") === "block"),
          preview: (jax.SVG||{}).preview            // in case typeset calls are interleaved
        };
        span = div = HTML.Element("span",{
          style: {"font-size": this.config.scale+"%", display:"inline-block"},
 	  className:"MathJax_SVG", id:jax.inputID+"-Frame", isMathJax:true, jaxID:this.id,
          oncontextmenu:EVENT.Menu, onmousedown: EVENT.Mousedown,
          onmouseover:EVENT.Mouseover, onmouseout:EVENT.Mouseout, onmousemove:EVENT.Mousemove,
	  onclick:EVENT.Click, ondblclick:EVENT.DblClick,
          // Added for keyboard accessible menu.
          onkeydown: EVENT.Keydown, tabIndex: HUB.getTabOrder(jax)
        });
	if (HUB.Browser.noContextMenu) {
	  span.ontouchstart = TOUCH.start;
	  span.ontouchend = TOUCH.end;
	}
        if (jax.SVG.display) {
          div = HTML.Element("div",{className:"MathJax_SVG_Display"});
          div.appendChild(span);
        }
        div.className += " MathJax_SVG_Processing";
        script.parentNode.insertBefore(div,script);
        //
        //  Add the test span for determining scales and linebreak widths
        //
        script.parentNode.insertBefore(this.ExSpan.cloneNode(true),script);
        div.parentNode.insertBefore(this.linebreakSpan.cloneNode(true),div);
      }
      //
      //  Determine the scaling factors for each script
      //  (this only requires one reflow rather than a reflow for each equation)
      //
      var hidden = [];
      for (i = 0; i < m; i++) {
        script = scripts[i]; if (!script.parentNode) continue;
        test = script.previousSibling; div = test.previousSibling;
        jax = script.MathJax.elementJax; if (!jax) continue;
        ex = test.firstChild.offsetHeight/60;
        cwidth = Math.max(0,(div.previousSibling.firstChild.offsetWidth-2) / this.config.scale * 100);
        if (ex === 0 || ex === "NaN") {
          // can't read width, so move to hidden div for processing
          hidden.push(div);
          jax.SVG.isHidden = true;
          ex = this.defaultEx; cwidth = this.defaultWidth;
        }
        if (relwidth) {maxwidth = cwidth}
        jax.SVG.ex = ex;
        jax.SVG.em = em = ex / SVG.TeX.x_height * 1000; // scale ex to x_height
        jax.SVG.cwidth = cwidth/em * 1000;
        jax.SVG.lineWidth = (linebreak ? this.length2em(width,1,maxwidth/em*1000) : SVG.BIGDIMEN);
      }
      for (i = 0, n = hidden.length; i < n; i++) {
        this.hiddenDiv.appendChild(hidden[i]);
        this.addElement(this.hiddenDiv,"br");
      }
      //
      //  Remove the test spans used for determining scales and linebreak widths
      //
      for (i = 0; i < m; i++) {
        script = scripts[i]; if (!script.parentNode) continue;
        test = scripts[i].previousSibling; span = test.previousSibling;
        jax = scripts[i].MathJax.elementJax; if (!jax) continue;
        if (!jax.SVG.isHidden) {span = span.previousSibling}
        span.parentNode.removeChild(span);
        test.parentNode.removeChild(test);
        if (script.MathJax.preview) script.MathJax.preview.style.display = "";
      }
      //
      //  Set state variables used for displaying equations in chunks
      //
      state.SVGeqn = state.SVGlast = 0; state.SVGi = -1;
      state.SVGchunk = this.config.EqnChunk;
      state.SVGdelay = false;
    },

    Translate: function (script,state) {
      if (!script.parentNode) return;

      //
      //  If we are supposed to do a chunk delay, do it
      //  
      if (state.SVGdelay) {
        state.SVGdelay = false;
        HUB.RestartAfter(MathJax.Callback.Delay(this.config.EqnChunkDelay));
      }

      //
      //  Get the data about the math
      //
      var jax = script.MathJax.elementJax, math = jax.root, div, span,
          localCache = (SVG.config.useFontCache && !SVG.config.useGlobalCache);
      if (jax.SVG.isHidden) {
        span = document.getElementById(jax.inputID+"-Frame");
        div = (jax.SVG.display ? span.parentElement : span);
      } else {
        div = script.previousSibling;
        span = (jax.SVG.display ? (div||{}).firstChild||div : div);
      }
      if (!div) return;
      //
      //  Set the font metrics
      //
      this.em = MML.mbase.prototype.em = jax.SVG.em; this.ex = jax.SVG.ex;
      this.linebreakWidth = jax.SVG.lineWidth; this.cwidth = jax.SVG.cwidth;
      //
      //  Typeset the math
      //
      this.mathDiv = div;
      span.appendChild(this.textSVG);
      if (localCache) {SVG.resetGlyphs()}
      this.initSVG(math,span);
      math.setTeXclass();
      try {math.toSVG(span,div)} catch (err) {
        if (err.restart) {while (span.firstChild) {span.removeChild(span.firstChild)}}
        if (localCache) {BBOX.GLYPH.n--}
        throw err;
      }
      span.removeChild(this.textSVG);
      //
      //  Put it in place, and remove the processing marker
      //
      if (jax.SVG.isHidden) {script.parentNode.insertBefore(div,script)}
      div.className = div.className.split(/ /)[0];
      //
      //  Check if we are hiding the math until more is processed
      //
      if (this.hideProcessedMath) {
        //
        //  Hide the math and don't let its preview be removed
        //
        div.className += " MathJax_SVG_Processed";
        if (script.MathJax.preview) {
          jax.SVG.preview = script.MathJax.preview;
          delete script.MathJax.preview;
        }
        //
        //  Check if we should show this chunk of equations
        //
        state.SVGeqn += (state.i - state.SVGi); state.SVGi = state.i;
        if (state.SVGeqn >= state.SVGlast + state.SVGchunk) {
          this.postTranslate(state,true);
          state.SVGchunk = Math.floor(state.SVGchunk*this.config.EqnChunkFactor);
          state.SVGdelay = true;  // delay if there are more scripts
        }
      }
    },

    postTranslate: function (state,partial) {
      var scripts = state.jax[this.id];
      if (!this.hideProcessedMath) return;
      //
      //  Reveal this chunk of math
      //
      for (var i = state.SVGlast, m = state.SVGeqn; i < m; i++) {
        var script = scripts[i];
        if (script && script.MathJax.elementJax) {
          //
          //  Remove the processed marker
          //
          script.previousSibling.className = script.previousSibling.className.split(/ /)[0];
          var data = script.MathJax.elementJax.SVG;
          //
          //  Remove the preview, if any
          //
          if (data.preview) {
            data.preview.innerHTML = "";
            data.preview.style.display = "none";
            script.MathJax.preview = data.preview;
            delete data.preview;
          }
        }
      }
      //
      //  Save our place so we know what is revealed
      //
      state.SVGlast = state.SVGeqn;
    },
    
    resetGlyphs: function (reset) {
      if (this.config.useFontCache) {
        var GLYPH = BBOX.GLYPH;
        if (this.config.useGlobalCache) {
          GLYPH.defs = document.getElementById("MathJax_SVG_glyphs");
          GLYPH.defs.innerHTML = "";
        } else {
          GLYPH.defs = this.Element("defs");
          GLYPH.n++;
        }
        GLYPH.glyphs = {};
        if (reset) {GLYPH.n = 0}
      }
    },

    //
    //  Return the containing HTML element rather than the SVG element, since
    //  most browsers can't position to an SVG element properly.
    //
    hashCheck: function (target) {
      if (target && target.nodeName.toLowerCase() === "g")
        {do {target = target.parentNode} while (target && target.firstChild.nodeName !== "svg")}
      return target;
    },

    getJaxFromMath: function (math) {
      if (math.parentNode.className.match(/MathJax_SVG_Display/)) {math = math.parentNode}
      do {math = math.nextSibling} while (math && math.nodeName.toLowerCase() !== "script");
      return HUB.getJaxFor(math);
    },
    getHoverSpan: function (jax,math) {
      math.style.position = "relative"; // make sure inline containers have position set
      return math.firstChild;
    },
    getHoverBBox: function (jax,span,math) {
      var bbox = EVENT.getBBox(span.parentNode);
      bbox.h += 2; bbox.d -= 2; // bbox seems to be a bit off, so compensate (FIXME)
      return bbox;
    },
    
    Zoom: function (jax,span,math,Mw,Mh) {
      //
      //  Re-render at larger size
      //
      span.className = "MathJax_SVG";

      //
      //  get em size (taken from this.preTranslate)
      //
      var emex = span.appendChild(this.ExSpan.cloneNode(true));
      var ex = emex.firstChild.offsetHeight/60;
      this.em = MML.mbase.prototype.em = ex / SVG.TeX.x_height * 1000; this.ex = ex;
      this.linebreakWidth = jax.SVG.lineWidth; this.cwidth = jax.SVG.cwidth;
      emex.parentNode.removeChild(emex);

      span.appendChild(this.textSVG);
      this.mathDIV = span; this.zoomScale = parseInt(HUB.config.menuSettings.zscale) / 100;
      var tw = jax.root.data[0].SVGdata.tw; if (tw && tw < this.cwidth) this.cwidth = tw;
      this.idPostfix = "-zoom"; jax.root.toSVG(span,span); this.idPostfix = "";
      this.zoomScale = 1;
      span.removeChild(this.textSVG);
      
      //
      //  Don't allow overlaps on any edge
      //
      var svg = span.getElementsByTagName("svg")[0].style;
      svg.marginTop = svg.marginRight = svg.marginLeft = 0;
      if (svg.marginBottom.charAt(0) === "-")
        span.style.marginBottom = svg.marginBottom.substr(1);
      
      if (this.operaZoomRefresh)
        {setTimeout(function () {span.firstChild.style.border="1px solid transparent"},1)}
      //
      // WebKit bug (issue #749)
      //  
      if (span.offsetWidth < span.firstChild.offsetWidth) {
        span.style.minWidth = span.firstChild.offsetWidth + "px";
        math.style.minWidth = math.firstChild.offsetWidth + "px";
      }
      //
      //  Get height and width of zoomed math and original math
      //
      span.style.position = math.style.position = "absolute";
      var zW = span.offsetWidth, zH = span.offsetHeight,
          mH = math.offsetHeight, mW = math.offsetWidth;
      span.style.position = math.style.position = "";
      //
      return {Y:-EVENT.getBBox(span).h, mW:mW, mH:mH, zW:zW, zH:zH};
    },

    initSVG: function (math,span) {},
    
    Remove: function (jax) {
      var span = document.getElementById(jax.inputID+"-Frame");
      if (span) {
        if (jax.SVG.display) {span = span.parentNode}
        span.parentNode.removeChild(span);
      }
      delete jax.SVG;
    },
    
    Em: function (m) {
      if (Math.abs(m) < .0006) return "0";
      return m.toFixed(3).replace(/\.?0+$/,"") + "em";
    },
    Ex: function (m) {
      m = m / this.TeX.x_height;
      if (Math.abs(m) < .0006) return "0";
      return m.toFixed(3).replace(/\.?0+$/,"") + "ex";
    },
    Percent: function (m) {
      return (100*m).toFixed(1).replace(/\.?0+$/,"") + "%";
    },
    Fixed: function (m,n) {
      if (Math.abs(m) < .0006) return "0";
      return m.toFixed(n||3).replace(/\.?0+$/,"");
    },
    length2em: function (length,mu,size) {
      if (typeof(length) !== "string") {length = length.toString()}
      if (length === "") {return ""}
      if (length === MML.SIZE.NORMAL) {return 1000}
      if (length === MML.SIZE.BIG)    {return 2000}
      if (length === MML.SIZE.SMALL)  {return  710}
      if (length === "infinity")      {return SVG.BIGDIMEN}
      if (length.match(/mathspace$/)) {return 1000*SVG.MATHSPACE[length]}
      var emFactor = (this.zoomScale || 1) / SVG.em;
      var match = length.match(/^\s*([-+]?(?:\.\d+|\d+(?:\.\d*)?))?(pt|em|ex|mu|px|pc|in|mm|cm|%)?/);
      var m = parseFloat(match[1]||"1") * 1000, unit = match[2];
      if (size == null) {size = 1000};  if (mu == null) {mu = 1}
      if (unit === "em") {return m}
      if (unit === "ex") {return m * SVG.TeX.x_height/1000}
      if (unit === "%")  {return m / 100 * size / 1000}
      if (unit === "px") {return m * emFactor}
      if (unit === "pt") {return m / 10}                               // 10 pt to an em
      if (unit === "pc") {return m * 1.2}                              // 12 pt to a pc
      if (unit === "in") {return m * this.pxPerInch * emFactor}
      if (unit === "cm") {return m * this.pxPerInch * emFactor / 2.54} // 2.54 cm to an inch
      if (unit === "mm") {return m * this.pxPerInch * emFactor / 25.4} // 10 mm to a cm
      if (unit === "mu") {return m / 18 * mu}
      return m*size / 1000;  // relative to given size (or 1em as default)
    },
    thickness2em: function (length,mu) {
      var thick = SVG.TeX.rule_thickness;
      if (length === MML.LINETHICKNESS.MEDIUM) {return thick}
      if (length === MML.LINETHICKNESS.THIN)   {return .67*thick}
      if (length === MML.LINETHICKNESS.THICK)  {return 1.67*thick}
      return this.length2em(length,mu,thick);
    },

    getPadding: function (styles) {
      var padding = {top:0, right:0, bottom:0, left:0}, has = false;
      for (var id in padding) {if (padding.hasOwnProperty(id)) {
        var pad = styles["padding"+id.charAt(0).toUpperCase()+id.substr(1)];
        if (pad) {padding[id] = this.length2em(pad); has = true;}
      }}
      return (has ? padding : false);
    },
    getBorders: function (styles) {
      var border = {top:0, right:0, bottom:0, left:0}, has = false;
      for (var id in border) {if (border.hasOwnProperty(id)) {
        var ID = "border"+id.charAt(0).toUpperCase()+id.substr(1);
        var style = styles[ID+"Style"];
        if (style && style !== "none") {
          has = true;
          border[id] = this.length2em(styles[ID+"Width"]);
          border[id+"Style"] = styles[ID+"Style"];
          border[id+"Color"] = styles[ID+"Color"];
          if (border[id+"Color"] === "initial") {border[id+"Color"] = ""}
        } else {delete border[id]}
      }}
      return (has ? border : false);
    },

    Element: function (type,def) {
      var obj = (typeof(type) === "string" ? document.createElementNS(SVGNS,type) : type);
      obj.isMathJax = true;
      if (def) {for (var id in def) {if (def.hasOwnProperty(id)) {obj.setAttribute(id,def[id].toString())}}}
      return obj;
    },
    addElement: function (parent,type,def) {return parent.appendChild(this.Element(type,def))},
    TextNode: HTML.TextNode,
    addText: HTML.addText,
    ucMatch: HTML.ucMatch,

    HandleVariant: function (variant,scale,text) {
      var svg = BBOX.G();
      var n, N, c, font, VARIANT, i, m, id, M, RANGES;
      if (!variant) {variant = this.FONTDATA.VARIANT[MML.VARIANT.NORMAL]}
      if (variant.forceFamily) {
        text = BBOX.TEXT(scale,text,variant.font);
        if (variant.h != null) {text.h = variant.h}; if (variant.d != null) {text.d = variant.d}
        svg.Add(text); text = "";
      }
      VARIANT = variant;
      for (i = 0, m = text.length; i < m; i++) {
        variant = VARIANT;
        n = text.charCodeAt(i); c = text.charAt(i);
        if (n >= 0xD800 && n < 0xDBFF) {
          i++; n = (((n-0xD800)<<10)+(text.charCodeAt(i)-0xDC00))+0x10000;
          if (this.FONTDATA.RemapPlane1) {
            var nv = this.FONTDATA.RemapPlane1(n,variant);
            n = nv.n; variant = nv.variant;
          }
        } else {
          RANGES = this.FONTDATA.RANGES;
          for (id = 0, M = RANGES.length; id < M; id++) {
            if (RANGES[id].name === "alpha" && variant.noLowerCase) continue;
            N = variant["offset"+RANGES[id].offset];
            if (N && n >= RANGES[id].low && n <= RANGES[id].high) {
              if (RANGES[id].remap && RANGES[id].remap[n]) {
                n = N + RANGES[id].remap[n];
              } else {
                n = n - RANGES[id].low + N;
                if (RANGES[id].add) {n += RANGES[id].add}
              }
              if (variant["variant"+RANGES[id].offset])
                {variant = this.FONTDATA.VARIANT[variant["variant"+RANGES[id].offset]]}
              break;
            }
          }
        }
        if (variant.remap && variant.remap[n]) {
          n = variant.remap[n];
          if (variant.remap.variant) {variant = this.FONTDATA.VARIANT[variant.remap.variant]}
        } else if (this.FONTDATA.REMAP[n] && !variant.noRemap) {
          n = this.FONTDATA.REMAP[n];
        }
        if (isArray(n)) {variant = this.FONTDATA.VARIANT[n[1]]; n = n[0]}
        if (typeof(n) === "string") {
          text = n+text.substr(i+1);
          m = text.length; i = -1;
          continue;
        }
        font = this.lookupChar(variant,n); c = font[n];
        if (c) {
          if ((c[5] && c[5].space) || (c[5] === "" && c[0]+c[1] === 0)) {svg.w += c[2]} else {
            c = [scale,font.id+"-"+n.toString(16).toUpperCase()].concat(c);
            svg.Add(BBOX.GLYPH.apply(BBOX,c),svg.w,0);
          }
        } else if (this.FONTDATA.DELIMITERS[n]) {
          c = this.createDelimiter(n,0,1,font);
          svg.Add(c,svg.w,(this.FONTDATA.DELIMITERS[n].dir === "V" ? c.d: 0));
        } else {
          if (n <= 0xFFFF) {c = String.fromCharCode(n)} else {
            N = n - 0x10000;
            c = String.fromCharCode((N>>10)+0xD800)
              + String.fromCharCode((N&0x3FF)+0xDC00);
          }
          var box = BBOX.TEXT(scale*100/SVG.config.scale,c,{
            "font-family":variant.defaultFamily||SVG.config.undefinedFamily,
            "font-style":(variant.italic?"italic":""),
            "font-weight":(variant.bold?"bold":"")
          })
          if (variant.h != null) {box.h = variant.h}; if (variant.d != null) {box.d = variant.d}
          c = BBOX.G(); c.Add(box); svg.Add(c,svg.w,0);
          HUB.signal.Post(["SVG Jax - unknown char",n,variant]);
        }
      }
      if (text.length == 1 && font.skew && font.skew[n]) {svg.skew = font.skew[n]*1000}
      if (svg.element.childNodes.length === 1 && !svg.element.firstChild.getAttribute("x")) {
        svg.element = svg.element.firstChild;
        svg.removeable = false; svg.scale = scale;
      }
      return svg;
    },
        
    lookupChar: function (variant,n) {
      var i, m;
      if (!variant.FONTS) {
        var FONTS = this.FONTDATA.FONTS;
        var fonts = (variant.fonts || this.FONTDATA.VARIANT.normal.fonts);
        if (!(fonts instanceof Array)) {fonts = [fonts]}
        if (variant.fonts != fonts) {variant.fonts = fonts}
        variant.FONTS = [];
        for (i = 0, m = fonts.length; i < m; i++) {
          if (FONTS[fonts[i]]) {variant.FONTS.push(FONTS[fonts[i]])}
        }
      }
      for (i = 0, m = variant.FONTS.length; i < m; i++) {
        var font = variant.FONTS[i];
        if (typeof(font) === "string") {delete variant.FONTS; this.loadFont(font)}
        if (font[n]) {return font} else {this.findBlock(font,n)}
      }
      return {id:"unknown"};
    },

    findBlock: function (font,c) {
      if (font.Ranges) {
        // FIXME:  do binary search?
        for (var i = 0, m = font.Ranges.length; i < m; i++) {
          if (c <  font.Ranges[i][0]) return;
          if (c <= font.Ranges[i][1]) {
            var file = font.Ranges[i][2];
            for (var j = font.Ranges.length-1; j >= 0; j--)
              {if (font.Ranges[j][2] == file) {font.Ranges.splice(j,1)}}
            this.loadFont(font.directory+"/"+file+".js");
          }
        }
      }
    },
      
    loadFont: function (file) {
      HUB.RestartAfter(AJAX.Require(this.fontDir+"/"+file));
    },

    createDelimiter: function (code,HW,scale,font) {
      if (!scale) {scale = 1};
      var svg = BBOX.G();
      if (!code) {
        svg.Clean(); delete svg.element;
        svg.w = svg.r = this.TeX.nulldelimiterspace * scale;
        return svg;
      }
      if (!(HW instanceof Array)) {HW = [HW,HW]}
      var hw = HW[1]; HW = HW[0];
      var delim = {alias: code};
      while (delim.alias) {
        code = delim.alias; delim = this.FONTDATA.DELIMITERS[code];
        if (!delim) {delim = {HW: [0,this.FONTDATA.VARIANT[MML.VARIANT.NORMAL]]}}
      }
      if (delim.load) {HUB.RestartAfter(AJAX.Require(this.fontDir+"/fontdata-"+delim.load+".js"))}
      for (var i = 0, m = delim.HW.length; i < m; i++) {
        if (delim.HW[i][0]*scale >= HW-10-SVG.config.blacker || (i == m-1 && !delim.stretch)) {
          if (delim.HW[i][2]) {scale *= delim.HW[i][2]}
          if (delim.HW[i][3]) {code = delim.HW[i][3]}
          return this.createChar(scale,[code,delim.HW[i][1]],font).With({stretched: true});
        }
      }
      if (delim.stretch) {this["extendDelimiter"+delim.dir](svg,hw,delim.stretch,scale,font)}
      return svg;
    },
    createChar: function (scale,data,font) {
      var text = "", variant = {fonts: [data[1]], noRemap:true};
      if (font && font === MML.VARIANT.BOLD) {variant.fonts = [data[1]+"-bold",data[1]]}
      if (typeof(data[1]) !== "string") {variant = data[1]}
      if (data[0] instanceof Array) {
        for (var i = 0, m = data[0].length; i < m; i++) {text += String.fromCharCode(data[0][i])}
      } else {text = String.fromCharCode(data[0])}
      if (data[4]) {scale = scale*data[4]}
      var svg = this.HandleVariant(variant,scale,text);
      if (data[2]) {svg.x = data[2]*1000}
      if (data[3]) {svg.y = data[3]*1000}
      if (data[5]) {svg.h += data[5]*1000}
      if (data[6]) {svg.d += data[6]*1000}
      return svg;
    },
    extendDelimiterV: function (svg,H,delim,scale,font) {
      var top = this.createChar(scale,(delim.top||delim.ext),font);
      var bot = this.createChar(scale,(delim.bot||delim.ext),font);
      var h = top.h + top.d + bot.h + bot.d;
      var y = -top.h; svg.Add(top,0,y); y -= top.d;
      if (delim.mid) {var mid = this.createChar(scale,delim.mid,font); h += mid.h + mid.d}
      if (delim.min && H < h*delim.min) {H = h*delim.min}
      if (H > h) {
        var ext = this.createChar(scale,delim.ext,font);
        var k = (delim.mid ? 2 : 1), eH = (H-h) / k, s = (eH+100) / (ext.h+ext.d);
        while (k-- > 0) {
          var g = SVG.Element("g",{transform:"translate("+ext.y+","+(y-s*ext.h+50+ext.y)+") scale(1,"+s+")"});
          g.appendChild(ext.element.cloneNode(false)); svg.element.appendChild(g); y -= eH;
          if (delim.mid && k) {svg.Add(mid,0,y-mid.h); y -= (mid.h+mid.d)}
        }
      } else if (delim.mid) {
        y += (h - H)/2; svg.Add(mid,0,y-mid.h); y += -(mid.h + mid.d) + (h - H)/2;
      } else {
        y += (h - H);
      }
      svg.Add(bot,0,y-bot.h); svg.Clean();
      svg.scale = scale;
      svg.isMultiChar = true;
    },
    extendDelimiterH: function (svg,W,delim,scale,font) {
      var left  = this.createChar(scale,(delim.left||delim.rep),font);
      var right = this.createChar(scale,(delim.right||delim.rep),font);
      svg.Add(left,-left.l,0);
      var w = (left.r - left.l) + (right.r - right.l), x = left.r - left.l;
      if (delim.mid) {var mid = this.createChar(scale,delim.mid,font); w += mid.w}
      if (delim.min && W < w*delim.min) {W = w*delim.min}
      if (W > w) {
        var rep = this.createChar(scale,delim.rep,font), fuzz = delim.fuzz || 0;
        var k = (delim.mid ? 2 : 1), rW = (W-w) / k, s = (rW+fuzz) / (rep.r-rep.l);
        while (k-- > 0) {
          var g = SVG.Element("g",{transform:"translate("+(x-fuzz/2-s*rep.l+rep.x)+","+rep.y+") scale("+s+",1)"});
          g.appendChild(rep.element.cloneNode(false)); svg.element.appendChild(g); x += rW;
          if (delim.mid && k) {svg.Add(mid,x,0); x += mid.w}
        }
      } else if (delim.mid) {
        x -= (w - W)/2; svg.Add(mid,x,0); x += mid.w - (w - W)/2;
      } else {
        x -= (w - W);
      }
      svg.Add(right,x-right.l,0); svg.Clean();
      svg.scale = scale;
      svg.isMultiChar = true;
    },


    MATHSPACE: {
      veryverythinmathspace:  1/18,
      verythinmathspace:      2/18,
      thinmathspace:          3/18,
      mediummathspace:        4/18,
      thickmathspace:         5/18,
      verythickmathspace:     6/18,
      veryverythickmathspace: 7/18,
      negativeveryverythinmathspace:  -1/18,
      negativeverythinmathspace:      -2/18,
      negativethinmathspace:          -3/18,
      negativemediummathspace:        -4/18,
      negativethickmathspace:         -5/18,
      negativeverythickmathspace:     -6/18,
      negativeveryverythickmathspace: -7/18
    },

    //
    //  Units are em/1000 so quad is 1em
    //
    TeX: {
      x_height:         430.554,
      quad:            1000,
      num1:             676.508,
      num2:             393.732,
      num3:             443.73,
      denom1:           685.951,
      denom2:           344.841,
      sup1:             412.892,
      sup2:             362.892,
      sup3:             288.888,
      sub1:             150,
      sub2:             247.217,
      sup_drop:         386.108,
      sub_drop:          50,
      delim1:          2390,
      delim2:          1000,
      axis_height:      250,
      rule_thickness:    60,
      big_op_spacing1:  111.111,
      big_op_spacing2:  166.666,
      big_op_spacing3:  200,
      big_op_spacing4:  600,
      big_op_spacing5:  100,

      scriptspace:         100,
      nulldelimiterspace:  120,
      delimiterfactor:     901,
      delimitershortfall:  300,

      min_rule_thickness:  1.25,   // in pixels
      min_root_space:      1.5     // in pixels
    },

    BIGDIMEN: 10000000,
    NBSP:   "\u00A0"
  });
  
  var BBOX = SVG.BBOX = MathJax.Object.Subclass({
    type: "g", removeable: true,
    Init: function (def) {
      this.h = this.d = -SVG.BIGDIMEN; this.H = this.D = 0;
      this.w = this.r = 0; this.l = SVG.BIGDIMEN;
      this.x = this.y = 0; this.scale = 1; this.n = 0;
      if (this.type) {this.element = SVG.Element(this.type,def)}
    },
    With: function (def) {return HUB.Insert(this,def)},
    Add: function (svg,dx,dy,forcew,infront) {
      if (dx) {svg.x += dx}; if (dy) {svg.y += dy};
      if (svg.element) {
        if (svg.removeable && svg.element.childNodes.length === 1 && svg.n === 1) {
          var child = svg.element.firstChild, nodeName = child.nodeName.toLowerCase();
          if (nodeName === "use" || nodeName === "rect") {
            svg.element = child; svg.scale = svg.childScale;
            var x = svg.childX, y = svg.childY;
            svg.x += x; svg.y += y;
            svg.h -= y; svg.d += y; svg.H -= y; svg.D +=y;
            svg.w -= x; svg.r -= x; svg.l += x;
            svg.removeable = false;
            child.setAttribute("x",Math.floor(svg.x/svg.scale));
            child.setAttribute("y",Math.floor(svg.y/svg.scale));
          }
        }
        if (Math.abs(svg.x) < 1 && Math.abs(svg.y) < 1) {
          svg.remove = svg.removeable;
        } else {
          nodeName = svg.element.nodeName.toLowerCase();
          if (nodeName === "g") {
            if (!svg.element.firstChild) {svg.remove = svg.removeable}
              else {svg.element.setAttribute("transform","translate("+Math.floor(svg.x)+","+Math.floor(svg.y)+")")}
          } else if (nodeName === "line" || nodeName === "polygon" ||
                     nodeName === "path" || nodeName === "a") {
            var transform = svg.element.getAttribute("transform") || "";
            if (transform) transform = " "+transform;
            transform = "translate("+Math.floor(svg.x)+","+Math.floor(svg.y)+")"+transform;
            svg.element.setAttribute("transform",transform);
          } else {
            svg.element.setAttribute("x",Math.floor(svg.x/svg.scale));
            svg.element.setAttribute("y",Math.floor(svg.y/svg.scale));
          }
        }
        if (svg.remove) {
          this.n += svg.n;
          while (svg.element.firstChild) {
            if (infront && this.element.firstChild) {
              this.element.insertBefore(svg.element.firstChild,this.element.firstChild);
            } else {
              this.element.appendChild(svg.element.firstChild);
            }
          }
        } else {
          if (infront) {this.element.insertBefore(svg.element,this.element.firstChild)}
                  else {this.element.appendChild(svg.element)}
        }
        delete svg.element;
      }
      if (svg.hasIndent) {this.hasIndent = svg.hasIndent}
      if (svg.tw != null) {this.tw = svg.tw}
      if (svg.d - svg.y > this.d) {this.d = svg.d - svg.y; if (this.d > this.D) {this.D = this.d}}
      if (svg.y + svg.h > this.h) {this.h = svg.y + svg.h; if (this.h > this.H) {this.H = this.h}}
      if (svg.D - svg.y > this.D) {this.D = svg.D - svg.y}
      if (svg.y + svg.H > this.H) {this.H = svg.y + svg.H}
      if (svg.x + svg.l < this.l) {this.l = svg.x + svg.l}
      if (svg.x + svg.r > this.r) {this.r = svg.x + svg.r}
      if (forcew || svg.x + svg.w + (svg.X||0) > this.w) {this.w = svg.x + svg.w + (svg.X||0)}
      this.childScale = svg.scale; this.childX = svg.x; this.childY = svg.y; this.n++;
      return svg;
    },
    Align: function (svg,align,dx,dy,shift) {
      dx = ({left: dx, center: (this.w - svg.w)/2, right: this.w - svg.w - dx})[align] || 0;
      var w = this.w; this.Add(svg,dx+(shift||0),dy); this.w = w;
    },
    Clean: function () {
      if (this.h === -SVG.BIGDIMEN) {this.h = this.d = this.l = 0}
      return this;
    }
  });
  
  BBOX.ROW = BBOX.Subclass({
    Init: function () {
      this.SUPER(arguments).Init.call(this);
      this.svg = []; this.sh = this.sd = 0;
    },
    Check: function (data) {
      var svg = data.toSVG(); this.svg.push(svg);
      if (data.SVGcanStretch("Vertical")) {svg.mml = data}
      if (svg.h > this.sh) {this.sh = svg.h}
      if (svg.d > this.sd) {this.sd = svg.d}
    },
    Stretch: function () {
      for (var i = 0, m = this.svg.length; i < m; i++)
      {
        var svg = this.svg[i], mml = svg.mml;
        if (mml) {
          if (mml.forceStretch || mml.SVGdata.h !== this.sh || mml.SVGdata.d !== this.sd) {
            svg = mml.SVGstretchV(this.sh,this.sd);
          }
          mml.SVGdata.HW = this.sh; mml.SVGdata.D = this.sd;
        }
        if (svg.ic) {this.ic = svg.ic} else {delete this.ic}
        this.Add(svg,this.w,0,true);
      }
      delete this.svg;
    }
  });
  
  BBOX.RECT = BBOX.Subclass({
    type: "rect", removeable: false,
    Init: function (h,d,w,def) {
      if (def == null) {def = {stroke:"none"}}
      def.width = Math.floor(w); def.height = Math.floor(h+d);
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.h = this.H = h+d; this.d = this.D = this.l = 0; this.y = -d;
    }
  });
  
  BBOX.FRAME = BBOX.Subclass({
    type: "rect", removeable: false,
    Init: function (h,d,w,t,dash,color,def) {
      if (def == null) {def = {}}; def.fill = "none";
      def["stroke-width"] = SVG.Fixed(t,2);
      def.width = Math.floor(w-t); def.height = Math.floor(h+d-t);
      def.transform = "translate("+Math.floor(t/2)+","+Math.floor(-d+t/2)+")";
      if (dash === "dashed")
        {def["stroke-dasharray"] = [Math.floor(6*SVG.em),Math.floor(6*SVG.em)].join(" ")}
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.h = this.H = h;
      this.d = this.D = d; this.l = 0;
    }
  });
  
  BBOX.HLINE = BBOX.Subclass({
    type: "line", removeable: false,
    Init: function (w,t,dash,color,def) {
      if (def == null) {def = {"stroke-linecap":"square"}}
      if (color && color !== "") {def.stroke = color}
      def["stroke-width"] = SVG.Fixed(t,2);
      def.x1 = def.y1 = def.y2 = Math.floor(t/2); def.x2 = Math.floor(w-t/2);
      if (dash === "dashed") {
        var n = Math.floor(Math.max(0,w-t)/(6*t)), m = Math.floor(Math.max(0,w-t)/(2*n+1));
        def["stroke-dasharray"] = m+" "+m;
      }
      if (dash === "dotted") {
        def["stroke-dasharray"] = [1,Math.max(150,Math.floor(2*t))].join(" ");
        def["stroke-linecap"] = "round";
      }
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.l = 0; this.h = this.H = t; this.d = this.D = 0;
    }
  });

  BBOX.VLINE = BBOX.Subclass({
    type: "line", removeable: false,
    Init: function (h,t,dash,color,def) {
      if (def == null) {def = {"stroke-linecap":"square"}}
      if (color && color !== "") {def.stroke = color}
      def["stroke-width"] = SVG.Fixed(t,2);
      def.x1 = def.x2 = def.y1 = Math.floor(t/2); def.y2 = Math.floor(h-t/2);
      if (dash === "dashed") {
        var n = Math.floor(Math.max(0,h-t)/(6*t)), m = Math.floor(Math.max(0,h-t)/(2*n+1));
        def["stroke-dasharray"] = m+" "+m;
      }
      if (dash === "dotted") {
        def["stroke-dasharray"] = [1,Math.max(150,Math.floor(2*t))].join(" ");
        def["stroke-linecap"] = "round";
      }
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = t; this.l = 0; this.h = this.H = h; this.d = this.D = 0;
    }
  });

  BBOX.TEXT = BBOX.Subclass({
    type: "text", removeable: false,
    Init: function (scale,text,def) {
      if (!def) {def = {}}; def.stroke = "none";
      if (def["font-style"] === "") delete def["font-style"];
      if (def["font-weight"] === "") delete def["font-weight"];
      this.SUPER(arguments).Init.call(this,def);
      SVG.addText(this.element,text);
      SVG.textSVG.appendChild(this.element);
      var bbox = this.element.getBBox();
      SVG.textSVG.removeChild(this.element);
      scale *= 1000/SVG.em;
      this.element.setAttribute("transform","scale("+SVG.Fixed(scale)+") matrix(1 0 0 -1 0 0)");
      this.w = this.r = bbox.width*scale; this.l = 0;
      this.h = this.H = -bbox.y*scale;
      this.d = this.D = (bbox.height + bbox.y)*scale;
    }
  });
  
  BBOX.G = BBOX;
  
  BBOX.NULL = BBOX.Subclass({
    Init: function () {
      this.SUPER(arguments).Init.apply(this,arguments);
      this.Clean();
    }
  });
  
  BBOX.GLYPH = BBOX.Subclass({
    type: "path", removeable: false,
    Init: function (scale,id,h,d,w,l,r,p) {
      var def, t = SVG.config.blacker, GLYPH = BBOX.GLYPH;
      var cache = SVG.config.useFontCache;
      var transform = (scale === 1 ? null : "scale("+SVG.Fixed(scale)+")");
      if (cache && !SVG.config.useGlobalCache) {id = "E"+GLYPH.n+"-"+id}
      if (!cache || !GLYPH.glyphs[id]) {
        def = {"stroke-width":t};
        if (cache) {def.id = id} else if (transform) {def.transform = transform}
        def.d = (p ? "M"+p+"Z" : "");
        this.SUPER(arguments).Init.call(this,def);
        if (cache) {GLYPH.defs.appendChild(this.element); GLYPH.glyphs[id] = true;}
      }
      if (cache) {
        def = {}; if (transform) {def.transform = transform}
        this.element = SVG.Element("use",def);
        this.element.setAttributeNS(XLINKNS,"href",SVGURL+"#"+id);
      }
      this.h = (h+t) * scale; this.d = (d+t) * scale; this.w = (w+t/2) *scale;
      this.l = (l+t/2) * scale; this.r = (r+t/2) * scale;
      this.H = Math.max(0,this.h); this.D = Math.max(0,this.d);
      this.x = this.y = 0; this.scale = scale;
    }
  },{
    glyphs: {},            // which glpyhs have been used
    defs: null,            // the SVG <defs> element where glyphs are stored
    n: 0                   // the ID for local <defs> for self-contained SVG elements
  });
  
  HUB.Register.StartupHook("mml Jax Ready",function () {

    MML = MathJax.ElementJax.mml;

    MML.mbase.Augment({
      SVG: BBOX,
      toSVG: function () {
        this.SVGgetStyles();
	var variant = this.SVGgetVariant();
        var svg = this.SVG(); this.SVGgetScale(svg);
        this.SVGhandleSpace(svg);
        for (var i = 0, m = this.data.length; i < m; i++) {
          if (this.data[i]) {
            var child = svg.Add(this.data[i].toSVG(variant,svg.scale),svg.w,0,true);
            if (child.skew) {svg.skew = child.skew}
          }
        }
        svg.Clean(); var text = this.data.join("");
        if (svg.skew && text.length !== 1) {delete svg.skew}
        if (svg.r > svg.w && text.length === 1 && !variant.noIC)
          {svg.ic = svg.r - svg.w; svg.w = svg.r}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      
      SVGchildSVG: function (i) {
        return (this.data[i] ? this.data[i].toSVG() : BBOX());
      },
      
      SVGdataStretched: function (i,HW,D) {
        this.SVGdata = {HW:HW, D:D};
        if (!this.data[i]) {return BBOX()}
        if (D  != null) {return this.data[i].SVGstretchV(HW,D)}
        if (HW != null) {return this.data[i].SVGstretchH(HW)}
        return this.data[i].toSVG();
      },
      
      SVGsaveData: function (svg) {
        if (!this.SVGdata) {this.SVGdata = {}}
        this.SVGdata.w = svg.w, this.SVGdata.x = svg.x;
        this.SVGdata.h = svg.h, this.SVGdata.d = svg.d;
        if (svg.y) {this.SVGdata.h += svg.y; this.SVGdata.d -= svg.y}
        if (svg.X != null) {this.SVGdata.X = svg.X}
        if (svg.tw != null) {this.SVGdata.tw = svg.tw}
        if (svg.skew) {this.SVGdata.skew = svg.skew}
        if (svg.ic) {this.SVGdata.ic = svg.ic}
        if (this["class"]) {svg.removeable = false; SVG.Element(svg.element,{"class":this["class"]})}
        // FIXME:  if an element is split by linebreaking, the ID will be the same on both parts
        // FIXME:  if an element has an id, its zoomed copy will have the same ID
        if (this.id) {svg.removeable = false; SVG.Element(svg.element,{"id":this.id})}
        if (this.href) {this.SVGaddHref(svg)}
        if (SVG.config.addMMLclasses) {
          this.SVGaddClass(svg.element,"mjx-svg-"+this.type);
          svg.removeable = false;
        }
        var style = this.style;
        if (style && svg.element) {
          svg.element.style.cssText = style;
          if (svg.element.style.fontSize) {svg.element.style.fontSize = ""} // handled by scale
          svg.element.style.border = svg.element.style.padding = "";
          if (svg.removeable) {svg.removeable = (svg.element.style.cssText === "")}
        }
        this.SVGaddAttributes(svg);
      },
      SVGaddClass: function (node,name) {
        var classes = node.getAttribute("class");
        node.setAttribute("class",(classes ? classes+" " : "")+name);
      },
      SVGaddAttributes: function (svg) {
        //
        //  Copy RDFa, aria, and other tags from the MathML to the HTML-CSS
        //  output spans Don't copy those in the MML.nocopyAttributes list,
        //  the ignoreMMLattributes configuration list, or anything tha
        //  already exists as a property of the span (e.g., no "onlick", etc.)
        //  If a name in the ignoreMMLattributes object is set to false, then
        //  the attribute WILL be copied.
        //
        if (this.attrNames) {
          var copy = this.attrNames, skip = MML.nocopyAttributes, ignore = HUB.config.ignoreMMLattributes;
          var defaults = (this.type === "mstyle" ? MML.math.prototype.defaults : this.defaults);
          for (var i = 0, m = copy.length; i < m; i++) {
            var id = copy[i];
            if (ignore[id] == false || (!skip[id] && !ignore[id] &&
                defaults[id] == null && typeof(svg.element[id]) === "undefined")) {
              svg.element.setAttribute(id,this.attr[id]);
              svg.removeable = false;
            }
          }
        }
      },
      SVGaddHref: function (svg) {
	var a = SVG.Element("a",{"class":"mjx-svg-href"});
        a.setAttributeNS(XLINKNS,"href",this.href);
        a.onclick = this.SVGlink;
        SVG.addElement(a,"rect",{width:svg.w, height:svg.h+svg.d, y:-svg.d,
                                 fill:"none", stroke:"none", "pointer-events":"all"});
        if (svg.type === "svg") {
          // for svg element, put <a> inside the main <g> element
          var g = svg.element.firstChild;
          while (g.firstChild) {a.appendChild(g.firstChild)}
          g.appendChild(a);
        } else {
          a.appendChild(svg.element); svg.element = a;
        }
        svg.removeable = false;
      },
      //
      //  WebKit currently scrolls to the BOTTOM of an svg element if it contains the
      //  target of the link, so implement link by hand, to the containing span element.
      //  
      SVGlink: function () {
        var href = this.href.animVal;
        if (href.charAt(0) === "#") {
          var target = SVG.hashCheck(document.getElementById(href.substr(1)));
          if (target && target.scrollIntoView) 
            {setTimeout(function () {target.parentNode.scrollIntoView(true)},1)}
        }
        document.location = href;
      },
      
      SVGgetStyles: function () {
        if (this.style) {
          var span = HTML.Element("span");
          span.style.cssText = this.style;
          this.styles = this.SVGprocessStyles(span.style);
        }
      },
      SVGprocessStyles: function (style) {
        var styles = {border:SVG.getBorders(style), padding:SVG.getPadding(style)};
        if (!styles.border) {delete styles.border}
        if (!styles.padding) {delete styles.padding}
        if (style.fontSize) {styles.fontSize = style.fontSize}
        if (style.color) {styles.color = style.color}
        if (style.backgroundColor) {styles.background = style.backgroundColor}
        if (style.fontStyle)  {styles.fontStyle = style.fontStyle}
        if (style.fontWeight) {styles.fontWeight = style.fontWeight}
        if (style.fontFamily) {styles.fontFamily = style.fontFamily}
        if (styles.fontWeight && styles.fontWeight.match(/^\d+$/))
          {styles.fontWeight = (parseInt(styles.fontWeight) > 600 ? "bold" : "normal")}
        return styles;
      },
            
      SVGhandleSpace: function (svg) {
	if (this.useMMLspacing) {
	  if (this.type !== "mo") return;
	  var values = this.getValues("scriptlevel","lspace","rspace");
	  if (values.scriptlevel <= 0 || this.hasValue("lspace") || this.hasValue("rspace")) {
            var mu = this.SVGgetMu(svg);
	    values.lspace = Math.max(0,SVG.length2em(values.lspace,mu));
	    values.rspace = Math.max(0,SVG.length2em(values.rspace,mu));
	    var core = this, parent = this.Parent();
	    while (parent && parent.isEmbellished() && parent.Core() === core)
	      {core = parent; parent = parent.Parent()}
	    if (values.lspace) {svg.x += values.lspace}
	    if (values.rspace) {svg.X = values.rspace}
	  }
	} else {
	  var space = this.texSpacing();
          this.SVGgetScale();
	  if (space !== "") {svg.x += SVG.length2em(space,this.scale)*this.mscale}
        }
      },

      SVGhandleColor: function (svg) {
        var values = this.getValues("mathcolor","color");
        if (this.styles && this.styles.color && !values.color) {values.color = this.styles.color}
        if (values.color && !this.mathcolor) {values.mathcolor = values.color}
        if (values.mathcolor) {
          SVG.Element(svg.element,{fill:values.mathcolor,stroke:values.mathcolor})
          svg.removeable = false;
        }
        var borders = (this.styles||{}).border, padding = (this.styles||{}).padding,
            bleft = ((borders||{}).left||0), pleft = ((padding||{}).left||0), id;
	values.background = (this.mathbackground || this.background ||
                             (this.styles||{}).background || MML.COLOR.TRANSPARENT);
        if (bleft + pleft) {
          //
          //  Make a box and move the contents of svg to it,
          //    then add it back into svg, but offset by the left amount
          //
          var dup = BBOX(); for (id in svg) {if (svg.hasOwnProperty(id)) {dup[id] = svg[id]}}
          dup.x = 0; dup.y = 0;
          svg.element = SVG.Element("g"); svg.removeable = true;
          svg.Add(dup,bleft+pleft,0);
        }
        //
        //  Adjust size by padding and dashed borders (left is taken care of above)
        //
        if (padding) {svg.w += padding.right||0; svg.h += padding.top||0; svg.d += padding.bottom||0}
        if (borders) {svg.w += borders.right||0; svg.h += borders.top||0; svg.d += borders.bottom||0}
        //
        //  Add background color
        //
	if (values.background !== MML.COLOR.TRANSPARENT) {
          var nodeName = svg.element.nodeName.toLowerCase();
          if (nodeName !== "g" && nodeName !== "svg") {
            var g = SVG.Element("g"); g.appendChild(svg.element);
            svg.element = g; svg.removeable = true;
          }
          svg.Add(BBOX.RECT(svg.h,svg.d,svg.w,{fill:values.background,stroke:"none"}),0,0,false,true)
        }
        //
        //  Add borders
        //
        if (borders) {
          var dd = 5; // fuzz factor to avoid anti-alias problems at edges
          var sides = {
            left:  ["V",svg.h+svg.d,-dd,-svg.d],
            right: ["V",svg.h+svg.d,svg.w-borders.right+dd,-svg.d],
            top:   ["H",svg.w,0,svg.h-borders.top+dd],
            bottom:["H",svg.w,0,-svg.d-dd]
          }
          for (id in sides) {if (sides.hasOwnProperty(id)) {
            if (borders[id]) {
              var side = sides[id], box = BBOX[side[0]+"LINE"];
              svg.Add(box(side[1],borders[id],borders[id+"Style"],borders[id+"Color"]),side[2],side[3]);
            }
          }}
        }
      },
      
      SVGhandleVariant: function (variant,scale,text) {
        return SVG.HandleVariant(variant,scale,text);
      },

      SVGgetVariant: function () {
	var values = this.getValues("mathvariant","fontfamily","fontweight","fontstyle");
	var variant = values.mathvariant;
        if (this.variantForm) variant = "-"+SVG.fontInUse+"-variant";
        values.hasVariant = this.Get("mathvariant",true);  // null if not explicitly specified
        if (!values.hasVariant) {
          values.family = values.fontfamily;
          values.weight = values.fontweight;
          values.style  = values.fontstyle;
        }
        if (this.styles) {
          if (!values.style  && this.styles.fontStyle)  {values.style = this.styles.fontStyle}
          if (!values.weight && this.styles.fontWeight) {values.weight = this.styles.fontWeight}
          if (!values.family && this.styles.fontFamily) {values.family = this.styles.fontFamily}
        }
        if (values.family && !values.hasVariant) {
	  if (!values.weight && values.mathvariant.match(/bold/)) {values.weight = "bold"}
          if (!values.style && values.mathvariant.match(/italic/)) {values.style = "italic"}
          variant = {forceFamily: true, font: {"font-family":values.family}};
          if (values.style) {variant.font["font-style"] = values.style}
          if (values.weight) {variant.font["font-weight"] = values.weight}
	  return variant;
	}
        if (values.weight === "bold") {
          variant = {
            normal:MML.VARIANT.BOLD, italic:MML.VARIANT.BOLDITALIC,
            fraktur:MML.VARIANT.BOLDFRAKTUR, script:MML.VARIANT.BOLDSCRIPT,
            "sans-serif":MML.VARIANT.BOLDSANSSERIF,
            "sans-serif-italic":MML.VARIANT.SANSSERIFBOLDITALIC
          }[variant]||variant;
        } else if (values.weight === "normal") {
          variant = {
            bold:MML.VARIANT.normal, "bold-italic":MML.VARIANT.ITALIC,
            "bold-fraktur":MML.VARIANT.FRAKTUR, "bold-script":MML.VARIANT.SCRIPT,
            "bold-sans-serif":MML.VARIANT.SANSSERIF,
            "sans-serif-bold-italic":MML.VARIANT.SANSSERIFITALIC
          }[variant]||variant;
        }
        if (values.style === "italic") {
          variant = {
            normal:MML.VARIANT.ITALIC, bold:MML.VARIANT.BOLDITALIC,
            "sans-serif":MML.VARIANT.SANSSERIFITALIC,
            "bold-sans-serif":MML.VARIANT.SANSSERIFBOLDITALIC
          }[variant]||variant;
        } else if (values.style === "normal") {
          variant = {
            italic:MML.VARIANT.NORMAL, "bold-italic":MML.VARIANT.BOLD,
            "sans-serif-italic":MML.VARIANT.SANSSERIF,
            "sans-serif-bold-italic":MML.VARIANT.BOLDSANSSERIF
          }[variant]||variant;
        }
        if (!(variant in SVG.FONTDATA.VARIANT)) {
          // If the mathvariant value is invalid or not supported by this
          // font, fallback to normal. See issue 363.
          variant = "normal";
        }
	return SVG.FONTDATA.VARIANT[variant];
      },
      
      SVGgetScale: function (svg) {
        var scale = 1;
        if (this.mscale) {
          scale = this.scale;
        } else {
          var values = this.getValues("scriptlevel","fontsize");
          values.mathsize = (this.isToken ? this : this.Parent()).Get("mathsize");
          if ((this.styles||{}).fontSize && !values.fontsize) {values.fontsize = this.styles.fontSize}
          if (values.fontsize && !this.mathsize) {values.mathsize = values.fontsize}
          if (values.scriptlevel !== 0) {
            if (values.scriptlevel > 2) {values.scriptlevel = 2}
            scale = Math.pow(this.Get("scriptsizemultiplier"),values.scriptlevel);
            values.scriptminsize = SVG.length2em(this.Get("scriptminsize"))/1000;
            if (scale < values.scriptminsize) {scale = values.scriptminsize}
          }
          this.scale = scale; this.mscale = SVG.length2em(values.mathsize)/1000;
        }
        if (svg) {svg.scale = scale; if (this.isToken) {svg.scale *= this.mscale}}
	return scale * this.mscale;
      },
      SVGgetMu: function (svg) {
	var mu = 1, values = this.getValues("scriptlevel","scriptsizemultiplier");
        if (svg.scale && svg.scale !== 1) {mu = 1/svg.scale}
	if (values.scriptlevel !== 0) {
	  if (values.scriptlevel > 2) {values.scriptlevel = 2}
	  mu = Math.sqrt(Math.pow(values.scriptsizemultiplier,values.scriptlevel));
	}
	return mu;
      },

      SVGnotEmpty: function (data) {
	while (data) {
	  if ((data.type !== "mrow" && data.type !== "texatom") ||
	       data.data.length > 1) {return true}
	  data = data.data[0];
	}
	return false;
      },
      
      SVGcanStretch: function (direction) {
        var can = false;
	if (this.isEmbellished()) {
          var core = this.Core();
          if (core && core !== this) {
            can = core.SVGcanStretch(direction);
            if (can && core.forceStretch) {this.forceStretch = true}
          }
        }
        return can;
      },
      SVGstretchV: function (h,d) {return this.toSVG(h,d)},
      SVGstretchH: function (w) {return this.toSVG(w)},
      
      SVGlineBreaks: function () {return false}
      
    },{
      SVGemptySVG: function () {
        var svg = this.SVG();
        svg.Clean();
        this.SVGsaveData(svg);
	return svg;
      },
      SVGautoload: function () {
	var file = SVG.autoloadDir+"/"+this.type+".js";
	HUB.RestartAfter(AJAX.Require(file));
      },
      SVGautoloadFile: function (name) {
	var file = SVG.autoloadDir+"/"+name+".js";
	HUB.RestartAfter(AJAX.Require(file));
      }
    });

    MML.chars.Augment({
      toSVG: function (variant,scale,remap,chars) {
        var text = this.data.join("").replace(/[\u2061-\u2064]/g,""); // remove invisibles
        if (remap) {text = remap(text,chars)}
	return this.SVGhandleVariant(variant,scale,text);
      }
    });
    MML.entity.Augment({
      toSVG: function (variant,scale,remap,chars) {
        var text = this.toString().replace(/[\u2061-\u2064]/g,""); // remove invisibles
        if (remap) {text = remap(text,chars)}
	return this.SVGhandleVariant(variant,scale,text);
      }
    });

    MML.mo.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.svg = this.SVG();
        var scale = this.SVGgetScale(svg);
        this.SVGhandleSpace(svg);
        if (this.data.length == 0) {svg.Clean(); this.SVGsaveData(svg); return svg}
        //
        //  Stretch the operator, if that is requested
        //
        if (D != null) {return this.SVGstretchV(HW,D)}
        else if (HW != null) {return this.SVG.strechH(HW)}
        //
        //  Get the variant, and check for operator size
        //
        var variant = this.SVGgetVariant();
	var values = this.getValues("largeop","displaystyle");
	if (values.largeop)
	  {variant = SVG.FONTDATA.VARIANT[values.displaystyle ? "-largeOp" : "-smallOp"]}
        //
        //  Get character translation for superscript and accents
        //
        var parent = this.CoreParent(),
            isScript = (parent && parent.isa(MML.msubsup) && this !== parent.data[0]),
            mapchars = (isScript?this.remapChars:null);
        if (this.data.join("").length === 1 && parent && parent.isa(MML.munderover) &&
            this.CoreText(parent.data[parent.base]).length === 1) {
          var over = parent.data[parent.over], under = parent.data[parent.under];
          if (over && this === over.CoreMO() && parent.Get("accent")) {mapchars = SVG.FONTDATA.REMAPACCENT}
          else if (under && this === under.CoreMO() && parent.Get("accentunder")) {mapchars = SVG.FONTDATA.REMAPACCENTUNDER}
        }
        //
        //  Primes must come from another font
        //
        if (isScript && this.data.join("").match(/['`"\u00B4\u2032-\u2037\u2057]/))
          {variant = SVG.FONTDATA.VARIANT["-"+SVG.fontInUse+"-variant"]}
        //
        //  Typeset contents
        //
	for (var i = 0, m = this.data.length; i < m; i++) {
          if (this.data[i]) {
            var text = this.data[i].toSVG(variant,scale,this.remap,mapchars), x = svg.w;
            if (x === 0 && -text.l > 10*text.w) {x += -text.l} // initial combining character doesn't combine
            svg.Add(text,x,0,true);
            if (text.skew) {svg.skew = text.skew}
          }
        }
        svg.Clean();
	if (this.data.join("").length !== 1) {delete svg.skew}
        //
        //  Handle large operator centering
        //
	if (values.largeop) {
	  svg.y = SVG.TeX.axis_height - (svg.h - svg.d)/2/scale;
	  if (svg.r > svg.w) {svg.ic = svg.r - svg.w; svg.w = svg.r}
	}
        //
        //  Finish up
        //
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      SVGcanStretch: function (direction) {
	if (!this.Get("stretchy")) {return false}
	var c = this.data.join("");
	if (c.length > 1) {return false}
        var parent = this.CoreParent();
        if (parent && parent.isa(MML.munderover) && 
            this.CoreText(parent.data[parent.base]).length === 1) {
          var over = parent.data[parent.over], under = parent.data[parent.under];
          if (over && this === over.CoreMO() && parent.Get("accent")) {c = SVG.FONTDATA.REMAPACCENT[c]||c}
          else if (under && this === under.CoreMO() && parent.Get("accentunder")) {c = SVG.FONTDATA.REMAPACCENTUNDER[c]||c}
        }
	c = SVG.FONTDATA.DELIMITERS[c.charCodeAt(0)];
        var can = (c && c.dir == direction.substr(0,1));
        if (!can) {delete this.svg}
        this.forceStretch = can && (this.Get("minsize",true) || this.Get("maxsize",true));
        return can;
      },
      SVGstretchV: function (h,d) {
        var svg = this.svg || this.toSVG();
	var values = this.getValues("symmetric","maxsize","minsize");
	var axis = SVG.TeX.axis_height*svg.scale, mu = this.SVGgetMu(svg), H;
	if (values.symmetric) {H = 2*Math.max(h-axis,d+axis)} else {H = h + d}
	values.maxsize = SVG.length2em(values.maxsize,mu,svg.h+svg.d);
	values.minsize = SVG.length2em(values.minsize,mu,svg.h+svg.d);
	H = Math.max(values.minsize,Math.min(values.maxsize,H));
        if (H != values.minsize)
          {H = [Math.max(H*SVG.TeX.delimiterfactor/1000,H-SVG.TeX.delimitershortfall),H]}
	svg = SVG.createDelimiter(this.data.join("").charCodeAt(0),H,svg.scale);
	if (values.symmetric) {H = (svg.h + svg.d)/2 + axis}
	  else {H = (svg.h + svg.d) * h/(h + d)}
        svg.y = H - svg.h;
	this.SVGhandleSpace(svg);
	this.SVGhandleColor(svg);
        delete this.svg.element;
        this.SVGsaveData(svg);
        svg.stretched = true;
	return svg;
      },
      SVGstretchH: function (w) {
        var svg = this.svg || this.toSVG(), mu = this.SVGgetMu(svg);
	var values = this.getValues("maxsize","minsize","mathvariant","fontweight");
        // FIXME:  should take style="font-weight:bold" into account as well
	if ((values.fontweight === "bold" || parseInt(values.fontweight) >= 600) &&
            !this.Get("mathvariant",true)) {values.mathvariant = MML.VARIANT.BOLD}
	values.maxsize = SVG.length2em(values.maxsize,mu,svg.w);
	values.minsize = SVG.length2em(values.minsize,mu,svg.w);
	w = Math.max(values.minsize,Math.min(values.maxsize,w));
        svg = SVG.createDelimiter(this.data.join("").charCodeAt(0),w,svg.scale,values.mathvariant);
	this.SVGhandleSpace(svg);
	this.SVGhandleColor(svg);
        delete this.svg.element;
        this.SVGsaveData(svg);
        svg.stretched = true;
	return svg;
      }
    });
    
    MML.mn.Augment({
      SVGremapMinus: function (text) {return text.replace(/^-/,"\u2212")},
      toSVG: function () {
        this.SVGgetStyles();
	var variant = this.SVGgetVariant();
        var svg = this.SVG(); this.SVGgetScale(svg);
        this.SVGhandleSpace(svg);
        var remap = this.SVGremapMinus;
        for (var i = 0, m = this.data.length; i < m; i++) {
          if (this.data[i]) {
            var child = svg.Add(this.data[i].toSVG(variant,svg.scale,remap),svg.w,0,true);
            if (child.skew) {svg.skew = child.skew}
            remap = null;
          }
        }
        svg.Clean(); var text = this.data.join("");
        if (svg.skew && text.length !== 1) {delete svg.skew}
        if (svg.r > svg.w && text.length === 1 && !variant.noIC)
          {svg.ic = svg.r - svg.w; svg.w = svg.r}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
    }),

    MML.mtext.Augment({
      toSVG: function () {
        if (SVG.config.mtextFontInherit || this.Parent().type === "merror") {
          this.SVGgetStyles();
          var svg = this.SVG(), scale = this.SVGgetScale(svg);
          this.SVGhandleSpace(svg);
          var variant = this.SVGgetVariant(), def = {direction:this.Get("dir")};
          if (variant.bold)   {def["font-weight"] = "bold"}
          if (variant.italic) {def["font-style"] = "italic"}
          variant = this.Get("mathvariant");
          if (variant === "monospace") {def["class"] = "MJX-monospace"}
            else if (variant.match(/sans-serif/)) {def["class"] = "MJX-sans-serif"}
          svg.Add(BBOX.TEXT(scale*100/SVG.config.scale,this.data.join(""),def)); svg.Clean();
          this.SVGhandleColor(svg);
          this.SVGsaveData(svg);
          return svg;
        } else {
          return this.SUPER(arguments).toSVG.call(this);
        }
      }
    });
    
    MML.merror.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.SVG(), scale = SVG.length2em(this.styles.fontSize||1)/1000;
        this.SVGhandleSpace(svg);
        var def = (scale !== 1 ? {transform:"scale("+SVG.Fixed(scale)+")"} : {});
        var bbox = BBOX(def);
        bbox.Add(this.SVGchildSVG(0)); bbox.Clean();
        if (scale !== 1) {
          bbox.removeable = false;
          var adjust = ["w","h","d","l","r","D","H"];
          for (var i = 0, m = adjust.length; i < m; i++) {bbox[adjust[i]] *= scale}
        }
        svg.Add(bbox); svg.Clean();
        this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
        return svg;
      },
      SVGgetStyles: function () {
        var span = HTML.Element("span",{style: SVG.config.merrorStyle});
        this.styles = this.SVGprocessStyles(span.style);
        if (this.style) {
          span.style.cssText = this.style;
          HUB.Insert(this.styles,this.SVGprocessStyles(span.style));
        }
      }
    });

    MML.ms.Augment({toSVG: MML.mbase.SVGautoload});

    MML.mglyph.Augment({toSVG: MML.mbase.SVGautoload});

    MML.mspace.Augment({
      toSVG: function () {
        this.SVGgetStyles();
	var values = this.getValues("height","depth","width");
	values.mathbackground = this.mathbackground;
	if (this.background && !this.mathbackground) {values.mathbackground = this.background}
        var svg = this.SVG(); this.SVGgetScale(svg);
        var scale = this.mscale, mu = this.SVGgetMu(svg); 
	svg.h = SVG.length2em(values.height,mu) * scale;
        svg.d = SVG.length2em(values.depth,mu)  * scale;
	svg.w = svg.r = SVG.length2em(values.width,mu) * scale;
        if (svg.w < 0) {svg.x = svg.w; svg.w = svg.r = 0}
        if (svg.h < -svg.d) {svg.d = -svg.h}
        svg.l = 0; svg.Clean();
        this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
        return svg;
      }
    });

    MML.mphantom.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.SVG(); this.SVGgetScale(svg);
	if (this.data[0] != null) {
          this.SVGhandleSpace(svg); svg.Add(this.SVGdataStretched(0,HW,D)); svg.Clean();
          while (svg.element.firstChild) {svg.element.removeChild(svg.element.firstChild)}
	}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
        if (svg.removeable && !svg.element.firstChild) {delete svg.element}
	return svg;
      }
    });

    MML.mpadded.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.SVG();
	if (this.data[0] != null) {
          this.SVGgetScale(svg); this.SVGhandleSpace(svg);
          var pad = this.SVGdataStretched(0,HW,D), mu = this.SVGgetMu(svg);
	  var values = this.getValues("height","depth","width","lspace","voffset"), X = 0, Y = 0;
	  if (values.lspace)  {X = this.SVGlength2em(pad,values.lspace,mu)}
	  if (values.voffset) {Y = this.SVGlength2em(pad,values.voffset,mu)}
          var h = pad.h, d = pad.d, w = pad.w, y = pad.y; // these can change durring the Add() 
          svg.Add(pad,X,Y); svg.Clean();
          svg.h = h+y; svg.d = d-y; svg.w = w; svg.removeable = false;
	  if (values.height !== "") {svg.h = this.SVGlength2em(svg,values.height,mu,"h",0)}
	  if (values.depth  !== "") {svg.d = this.SVGlength2em(svg,values.depth,mu,"d",0)}
	  if (values.width  !== "") {svg.w = this.SVGlength2em(svg,values.width,mu,"w",0)}
	  if (svg.h > svg.H) {svg.H = svg.h}; if (svg.d > svg.D) {svg.D = svg.d}
	}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      SVGlength2em: function (svg,length,mu,d,m) {
	if (m == null) {m = -SVG.BIGDIMEN}
	var match = String(length).match(/width|height|depth/);
	var size = (match ? svg[match[0].charAt(0)] : (d ? svg[d] : 0));
	var v = SVG.length2em(length,mu,size/this.mscale)*this.mscale;
	if (d && String(length).match(/^\s*[-+]/))
	  {return Math.max(m,svg[d]+v)} else {return v}
      }
    });

    MML.mrow.Augment({
      SVG: BBOX.ROW,
      toSVG: function (h,d) {
        this.SVGgetStyles();
	var svg = this.SVG();
        this.SVGhandleSpace(svg);
        if (d != null) {svg.sh = h; svg.sd = d}
	for (var i = 0, m = this.data.length; i < m; i++)
          {if (this.data[i]) {svg.Check(this.data[i])}}
        svg.Stretch(); svg.Clean();
        if (this.data.length === 1 && this.data[0]) {
          var data = this.data[0].SVGdata;
          if (data.skew) {svg.skew = data.skew}
        }
        if (this.SVGlineBreaks(svg)) {svg = this.SVGmultiline(svg)}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      SVGlineBreaks: function (svg) {
        if (!this.parent.linebreakContainer) {return false}
        return (SVG.config.linebreaks.automatic &&
                svg.w > SVG.linebreakWidth) || this.hasNewline();
      },
      SVGmultiline: function (span) {MML.mbase.SVGautoloadFile("multiline")},
      SVGstretchH: function (w) {
	var svg = this.SVG();
        this.SVGhandleSpace(svg);
	for (var i = 0, m = this.data.length; i < m; i++)
          {svg.Add(this.SVGdataStretched(i,w),svg.w,0)}
        svg.Clean();
        this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
        return svg;
      }
    });

    MML.mstyle.Augment({
      toSVG: function () {
        this.SVGgetStyles();
        var svg = this.SVG();
	if (this.data[0] != null) {
          this.SVGhandleSpace(svg);
          var math = svg.Add(this.data[0].toSVG()); svg.Clean();
          if (math.ic) {svg.ic = math.ic}
	  this.SVGhandleColor(svg);
	}
        this.SVGsaveData(svg);
	return svg;
      },
      SVGstretchH: function (w) {
	return (this.data[0] != null ? this.data[0].SVGstretchH(w) : BBOX.NULL());
      },
      SVGstretchV: function (h,d) {
	return (this.data[0] != null ? this.data[0].SVGstretchV(h,d) : BBOX.NULL());
      }
    });

    MML.mfrac.Augment({
      toSVG: function () {
        this.SVGgetStyles();
        var svg = this.SVG(), scale = this.SVGgetScale(svg);
        var frac = BBOX(); frac.scale = svg.scale; this.SVGhandleSpace(frac);
        var num = this.SVGchildSVG(0), den = this.SVGchildSVG(1);
	var values = this.getValues("displaystyle","linethickness","numalign","denomalign","bevelled");
	var isDisplay = values.displaystyle;
	var a = SVG.TeX.axis_height * scale;
	if (values.bevelled) {
	  var delta = (isDisplay ? 400 : 150);
	  var H = Math.max(num.h+num.d,den.h+den.d)+2*delta;
          var bevel = SVG.createDelimiter(0x2F,H);
          frac.Add(num,0,(num.d-num.h)/2+a+delta);
          frac.Add(bevel,num.w-delta/2,(bevel.d-bevel.h)/2+a);
	  frac.Add(den,num.w+bevel.w-delta,(den.d-den.h)/2+a-delta);
	} else {
	  var W = Math.max(num.w,den.w);
	  var t = SVG.thickness2em(values.linethickness,this.scale)*this.mscale, p,q, u,v;
	  var mt = SVG.TeX.min_rule_thickness/SVG.em * 1000;
	  if (isDisplay) {u = SVG.TeX.num1; v = SVG.TeX.denom1}
	    else {u = (t === 0 ? SVG.TeX.num3 : SVG.TeX.num2); v = SVG.TeX.denom2}
	  u *= scale; v *= scale;
	  if (t === 0) {// \atop
	    p = Math.max((isDisplay ? 7 : 3) * SVG.TeX.rule_thickness, 2*mt); // force to at least 2 px
	    q = (u - num.d) - (den.h - v);
	    if (q < p) {u += (p - q)/2; v += (p - q)/2}
            frac.w = W; t = 0;
	  } else {// \over
	    p = Math.max((isDisplay ? 2 : 0) * mt + t, t/2 + 1.5*mt);  // force to be at least 1.5px
	    q = (u - num.d) - (a + t/2); if (q < p) {u += p - q}
	    q = (a - t/2) - (den.h - v); if (q < p) {v += p - q}
	    frac.Add(BBOX.RECT(t/2,t/2,W+2*t),0,a);
	  }
          frac.Align(num,values.numalign,t,u);
          frac.Align(den,values.denomalign,t,-v);
	}
        frac.Clean(); svg.Add(frac,0,0); svg.Clean();
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      SVGcanStretch: function (direction) {return false},
      SVGhandleSpace: function (svg) {
      	if (!this.texWithDelims) {
          //
          //  Add nulldelimiterspace around the fraction
          //   (TeXBook pg 150 and Appendix G rule 15e)
          //
          svg.x = svg.X = SVG.TeX.nulldelimiterspace * this.mscale;
        }
        this.SUPER(arguments).SVGhandleSpace.call(this,svg);
      }
    });

    MML.msqrt.Augment({
      toSVG: function () {
        this.SVGgetStyles();
        var svg = this.SVG(), scale = this.SVGgetScale(svg); this.SVGhandleSpace(svg);
	var base = this.SVGchildSVG(0), rule, surd;
	var t = SVG.TeX.rule_thickness * scale, p,q, H, x = 0;
	if (this.Get("displaystyle")) {p = SVG.TeX.x_height * scale} else {p = t}
	q = Math.max(t + p/4,1000*SVG.TeX.min_root_space/SVG.em);
        H = base.h + base.d + q + t;
	surd = SVG.createDelimiter(0x221A,H,scale);
	if (surd.h + surd.d > H) {q = ((surd.h+surd.d) - (H-t)) / 2}
        rule = BBOX.RECT(t,0,base.w);
	H = base.h + q + t;
	x = this.SVGaddRoot(svg,surd,x,surd.h+surd.d-H,scale);
        svg.Add(surd,x,H-surd.h);
        svg.Add(rule,x+surd.w,H-rule.h);
	svg.Add(base,x+surd.w,0);
        svg.Clean();
        svg.h += t; svg.H += t;
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      },
      SVGaddRoot: function (svg,surd,x,d,scale) {return x}
    });

    MML.mroot.Augment({
      toSVG: MML.msqrt.prototype.toSVG,
      SVGaddRoot: function (svg,surd,x,d,scale) {
        var dx = (surd.isMultiChar ? .55 : .65) * surd.w;
	if (this.data[1]) {
          var root = this.data[1].toSVG(); root.x = 0;
          var h = this.SVGrootHeight(surd.h+surd.d,scale,root)-d;
          var w = Math.min(root.w,root.r); // remove extra right-hand padding, if any
          x = Math.max(w,dx);
          svg.Add(root,x-w,h);
        } else {dx = x}
	return x - dx;
      },
      SVGrootHeight: function (d,scale,root) {
	return .45*(d-900*scale) + 600*scale + Math.max(0,root.d-75);
      }
    });

    MML.mfenced.Augment({
      SVG: BBOX.ROW,
      toSVG: function () {
        this.SVGgetStyles();
        var svg = this.SVG();
	this.SVGhandleSpace(svg);
	if (this.data.open) {svg.Check(this.data.open)}
	if (this.data[0] != null) {svg.Check(this.data[0])}
	for (var i = 1, m = this.data.length; i < m; i++) {
	  if (this.data[i]) {
	    if (this.data["sep"+i]) {svg.Check(this.data["sep"+i])}
	    svg.Check(this.data[i]);
	  }
	}
	if (this.data.close) {svg.Check(this.data.close)}
        svg.Stretch(); svg.Clean();
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      }
    });

    MML.menclose.Augment({toSVG: MML.mbase.SVGautoload});
    MML.maction.Augment({toSVG: MML.mbase.SVGautoload});

    MML.semantics.Augment({
      toSVG: function () {
        this.SVGgetStyles();
        var svg = this.SVG();
	if (this.data[0] != null) {
          this.SVGhandleSpace(svg);
	  svg.Add(this.data[0].toSVG()); svg.Clean();
	} else {svg.Clean()}
        this.SVGsaveData(svg);
	return svg;
      },
      SVGstretchH: function (w) {
	return (this.data[0] != null ? this.data[0].SVGstretchH(w) : BBOX.NULL());
      },
      SVGstretchV: function (h,d) {
	return (this.data[0] != null ? this.data[0].SVGstretchV(h,d) : BBOX.NULL());
      }
    });

    MML.munderover.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
	var values = this.getValues("displaystyle","accent","accentunder","align");
        var base = this.data[this.base];
	if (!values.displaystyle && base != null &&
	    (base.movablelimits || base.CoreMO().Get("movablelimits")))
	      {return MML.msubsup.prototype.toSVG.call(this)}
        var svg = this.SVG(), scale = this.SVGgetScale(svg); this.SVGhandleSpace(svg);
	var boxes = [], stretch = [], box, i, m, W = -SVG.BIGDIMEN, WW = W;
	for (i = 0, m = this.data.length; i < m; i++) {
	  if (this.data[i] != null) {
	    if (i == this.base) {
              boxes[i] = this.SVGdataStretched(i,HW,D);
	      stretch[i] = (D != null || HW == null) && this.data[i].SVGcanStretch("Horizontal");
              if (this.data[this.over] && values.accent) {
                boxes[i].h = Math.max(boxes[i].h,SVG.TeX.x_height); // min height of 1ex (#1706)
              }
            } else {
              boxes[i] = this.data[i].toSVG(); boxes[i].x = 0; delete boxes[i].X;
	      stretch[i] = this.data[i].SVGcanStretch("Horizontal");
	    }
	    if (boxes[i].w > WW) {WW = boxes[i].w}
	    if (!stretch[i] && WW > W) {W = WW}
	  }
	}
	if (D == null && HW != null) {W = HW} else if (W == -SVG.BIGDIMEN) {W = WW}
        for (i = WW = 0, m = this.data.length; i < m; i++) {if (this.data[i]) {
          if (stretch[i]) {
            boxes[i] = this.data[i].SVGstretchH(W);
            if (i !== this.base) {boxes[i].x = 0; delete boxes[i].X}
          }
          if (boxes[i].w > WW) {WW = boxes[i].w}
        }}
        var t = SVG.TeX.rule_thickness * this.mscale;
	var x, y, z1, z2, z3, dw, k, delta = 0;
	base = boxes[this.base] || {w:0, h:0, d:0, H:0, D:0, l:0, r:0, y:0, scale:scale};
        if (base.ic) {delta = 1.3*base.ic + .05} // adjust faked IC to be more in line with expeted results
	for (i = 0, m = this.data.length; i < m; i++) {
	  if (this.data[i] != null) {
	    box = boxes[i];
	    z3 = SVG.TeX.big_op_spacing5 * scale;
	    var accent = (i != this.base && values[this.ACCENTS[i]]);
	    if (accent && box.w <= 1) {
              box.x = -box.l;
              boxes[i] = BBOX.G().With({removeable: false});
              boxes[i].Add(box); boxes[i].Clean();
              boxes[i].w = -box.l; box = boxes[i];
            }
	    dw = {left:0, center:(WW-box.w)/2, right:WW-box.w}[values.align];
	    x = dw; y = 0;
	    if (i == this.over) {
	      if (accent) {
		k = t * scale; z3 = 0;
		if (base.skew) {
                  x += base.skew; svg.skew = base.skew;
                  if (x+box.w > WW) {svg.skew += (WW-box.w-x)/2}
                }
	      } else {
		z1 = SVG.TeX.big_op_spacing1 * scale;
		z2 = SVG.TeX.big_op_spacing3 * scale;
		k = Math.max(z1,z2-Math.max(0,box.d));
	      }
	      k = Math.max(k,1500/SVG.em);
	      x += delta/2; y = base.y + base.h + box.d + k;
	      box.h += z3; if (box.h > box.H) {box.H = box.h}
	    } else if (i == this.under) {
	      if (accent) {
		k = 3*t * scale; z3 = 0;
	      } else {
		z1 = SVG.TeX.big_op_spacing2 * scale;
		z2 = SVG.TeX.big_op_spacing4 * scale;
		k = Math.max(z1,z2-box.h);
	      }
	      k = Math.max(k,1500/SVG.em);
	      x -= delta/2; y = base.y -(base.d + box.h + k);
	      box.d += z3; if (box.d > box.D) {box.D = box.d}
	    }
	    svg.Add(box,x,y);
	  }
	}
        svg.Clean();
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      }
    });

    MML.msubsup.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.SVG(), scale = this.SVGgetScale(svg); this.SVGhandleSpace(svg);
	var mu = this.SVGgetMu(svg);
        var base = svg.Add(this.SVGdataStretched(this.base,HW,D));
	var sscale = (this.data[this.sup] || this.data[this.sub] || this).SVGgetScale();
	var x_height = SVG.TeX.x_height * scale, s = SVG.TeX.scriptspace * scale;
	var sup, sub;
	if (this.SVGnotEmpty(this.data[this.sup])) {
	  sup = this.data[this.sup].toSVG();
	  sup.w += s; sup.r = Math.max(sup.w,sup.r);
	}
	if (this.SVGnotEmpty(this.data[this.sub])) {
	  sub = this.data[this.sub].toSVG();
	  sub.w += s; sub.r = Math.max(sub.w,sub.r);
	}
	var q = SVG.TeX.sup_drop * sscale, r = SVG.TeX.sub_drop * sscale;
	var u = base.h+(base.y||0) - q, v = base.d-(base.y||0) + r, delta = 0, p;
	if (base.ic) {
          base.w -= base.ic;       // remove IC (added by mo and mi)
          delta = 1.3*base.ic+.05; // adjust faked IC to be more in line with expeted results
        }
	if (this.data[this.base] &&
	   (this.data[this.base].type === "mi" || this.data[this.base].type === "mo")) {
	  if (this.data[this.base].data.join("").length === 1 && base.scale === 1 &&
	      !base.stretched && !this.data[this.base].Get("largeop")) {u = v = 0}
	}
	var min = this.getValues("subscriptshift","superscriptshift");
	min.subscriptshift   = (min.subscriptshift === ""   ? 0 : SVG.length2em(min.subscriptshift,mu));
	min.superscriptshift = (min.superscriptshift === "" ? 0 : SVG.length2em(min.superscriptshift,mu));
        var x = base.w + base.x;
	if (!sup) {
	  if (sub) {
	    v = Math.max(v,SVG.TeX.sub1*scale,sub.h-(4/5)*x_height,min.subscriptshift);
            svg.Add(sub,x,-v); this.data[this.sub].SVGdata.dy = -v;
	  }
	} else {
	  if (!sub) {
	    var values = this.getValues("displaystyle","texprimestyle");
	    p = SVG.TeX[(values.displaystyle ? "sup1" : (values.texprimestyle ? "sup3" : "sup2"))];
	    u = Math.max(u,p*scale,sup.d+(1/4)*x_height,min.superscriptshift);
            svg.Add(sup,x+delta,u);
            this.data[this.sup].SVGdata.dx = delta; 
            this.data[this.sup].SVGdata.dy = u;
	  } else {
	    v = Math.max(v,SVG.TeX.sub2*scale);
	    var t = SVG.TeX.rule_thickness * scale;
	    if ((u - sup.d) - (sub.h - v) < 3*t) {
	      v = 3*t - u + sup.d + sub.h;
	      q = (4/5)*x_height - (u - sup.d);
	      if (q > 0) {u += q; v -= q}
	    }
            svg.Add(sup,x+delta,Math.max(u,min.superscriptshift));
	    svg.Add(sub,x,-Math.max(v,min.subscriptshift));
            this.data[this.sup].SVGdata.dx = delta; 
            this.data[this.sup].SVGdata.dy = Math.max(u,min.superscriptshift);
            this.data[this.sub].SVGdata.dy = -Math.max(v,min.subscriptshift);
	  }
	}
        svg.Clean();
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      }
    });

    MML.mmultiscripts.Augment({toSVG: MML.mbase.SVGautoload});
    MML.mtable.Augment({toSVG: MML.mbase.SVGautoload});
    MML["annotation-xml"].Augment({toSVG: MML.mbase.SVGautoload});

    MML.math.Augment({
      SVG: BBOX.Subclass({type:"svg", removeable: false}),
      toSVG: function (span,div) {
        var CONFIG = SVG.config;
        //
        //  All the data should be in an inferrerd row
        //
        if (this.data[0]) {
          this.SVGgetStyles();
	  MML.mbase.prototype.displayAlign = HUB.config.displayAlign;
	  MML.mbase.prototype.displayIndent = HUB.config.displayIndent;
          if (String(HUB.config.displayIndent).match(/^0($|[a-z%])/i))
            MML.mbase.prototype.displayIndent = "0";
          //
          //  Put content in a <g> with defaults and matrix that flips y axis.
          //  Put that in an <svg> with xlink defined.
          //
          var box = BBOX.G(); box.Add(this.data[0].toSVG(),0,0,true); box.Clean();
          this.SVGhandleColor(box);
          SVG.Element(box.element,{
            stroke:"currentColor", fill:"currentColor", "stroke-width":0,
            transform: "matrix(1 0 0 -1 0 0)"
          });
          box.removeable = false;
          var svg = this.SVG();
          svg.element.setAttribute("xmlns:xlink",XLINKNS);
          if (CONFIG.useFontCache && !CONFIG.useGlobalCache)
            {svg.element.appendChild(BBOX.GLYPH.defs)}
          svg.Add(box); svg.Clean();
          this.SVGsaveData(svg);
          //
          //  If this element is not the top-level math element
          //    remove the transform and return the svg object
          //    (issue #614).
          //
          if (!span) {
            svg.element = svg.element.firstChild;  // remove <svg> element
            svg.element.removeAttribute("transform");
            svg.removable = true;
            return svg;
          }
          //
          //  Style the <svg> to get the right size and placement
          //
          var l = Math.max(-svg.l,0), r = Math.max(svg.r-svg.w,0);
          var style = svg.element.style, px = SVG.TeX.x_height/SVG.ex;
          var H = (Math.ceil(svg.H/px)+1)*px+SVG.HFUZZ,  // round to pixels and add padding
              D = (Math.ceil(svg.D/px)+1)*px+SVG.DFUZZ;
          var w = l + svg.w + r;
          svg.element.setAttribute("width",SVG.Ex(w));
          svg.element.setAttribute("height",SVG.Ex(H+D));
          style.verticalAlign = SVG.Ex(-D);
          if (l) style.marginLeft = SVG.Ex(-l);
          if (r) style.marginRight = SVG.Ex(-r);
          svg.element.setAttribute("viewBox",SVG.Fixed(-l,1)+" "+SVG.Fixed(-H,1)+" "+
                                             SVG.Fixed(w,1)+" "+SVG.Fixed(H+D,1));
          //
          //  If there is extra height or depth, hide that
          //
	  if (svg.H > svg.h) style.marginTop = SVG.Ex(svg.h-H);
	  if (svg.D > svg.d) {
	    style.marginBottom = SVG.Ex(svg.d-D);
	    style.verticalAlign = SVG.Ex(-svg.d);
	  }
          //
          //  The approximate ex can cause full-width equations to be too wide,
          //    so if they are close to full width, make sure they aren't too big.
          //
          if (Math.abs(w-SVG.cwidth) < 10)
            style.maxWidth = SVG.Fixed(SVG.cwidth*SVG.em/1000);
          //
          //  Add it to the MathJax span
          //
          var alttext = this.Get("alttext");
          if (alttext && !svg.element.getAttribute("aria-label")) svg.element.setAttribute("aria-label",alttext);
          if (!svg.element.getAttribute("role")) svg.element.setAttribute("role","img");
          svg.element.setAttribute("focusable","false");
          span.appendChild(svg.element);
          svg.element = null;
          //
          //  Handle indentalign and indentshift for single-line displays
          //
          if (!this.isMultiline && this.Get("display") === "block" && !svg.hasIndent) {
            var values = this.getValues("indentalignfirst","indentshiftfirst","indentalign","indentshift");
            if (values.indentalignfirst !== MML.INDENTALIGN.INDENTALIGN) {values.indentalign = values.indentalignfirst}
            if (values.indentalign === MML.INDENTALIGN.AUTO) {values.indentalign = this.displayAlign}
            if (values.indentshiftfirst !== MML.INDENTSHIFT.INDENTSHIFT) {values.indentshift = values.indentshiftfirst}
            if (values.indentshift === "auto") {values.indentshift = "0"}
            var shift = SVG.length2em(values.indentshift,1,SVG.cwidth);
            if (this.displayIndent !== "0") {
              var indent = SVG.length2em(this.displayIndent,1,SVG.cwidth);
              shift += (values.indentalign === MML.INDENTALIGN.RIGHT ? -indent : indent);
            }
            div.style.textAlign = values.indentalign;
            if (shift) {
              HUB.Insert(style,({
                left: {marginLeft: SVG.Ex(shift)},
                right: {marginRight: SVG.Ex(-shift), marginLeft: SVG.Ex(Math.max(0,shift-w))},
                center: {marginLeft: SVG.Ex(shift), marginRight: SVG.Ex(-shift)}
              })[values.indentalign]);
            }
          }
        }
	return span;
      }
    });

    MML.TeXAtom.Augment({
      toSVG: function (HW,D) {
        this.SVGgetStyles();
        var svg = this.SVG();
        this.SVGhandleSpace(svg);
	if (this.data[0] != null) {
          var box = this.SVGdataStretched(0,HW,D), y = 0;
          if (this.texClass === MML.TEXCLASS.VCENTER)
            {y = SVG.TeX.axis_height - (box.h+box.d)/2 + box.d}
          svg.Add(box,0,y);
          svg.ic = box.ic; svg.skew = box.skew;
	}
	this.SVGhandleColor(svg);
        this.SVGsaveData(svg);
	return svg;
      }
    });

    //
    //  Make sure these don't generate output
    //
    MML.maligngroup.Augment({toSVG: MML.mbase.SVGemptySVG});
    MML.malignmark.Augment({toSVG: MML.mbase.SVGemptySVG});
    MML.mprescripts.Augment({toSVG: MML.mbase.SVGemptySVG});
    MML.none.Augment({toSVG: MML.mbase.SVGemptySVG});

    //
    //  Loading isn't complete until the element jax is modified,
    //  but can't call loadComplete within the callback for "mml Jax Ready"
    //  (it would call SVG's Require routine, asking for the mml jax again)
    //  so wait until after the mml jax has finished processing.
    //  
    //  We also need to wait for the onload handler to run, since the loadComplete
    //  will call Config and Startup, which need to modify the body.
    //
    HUB.Register.StartupHook("onLoad",function () {
      setTimeout(MathJax.Callback(["loadComplete",SVG,"jax.js"]),0);
    });
  });

  HUB.Browser.Select({
    Opera: function (browser) {
      SVG.Augment({
        operaZoomRefresh: true          // Opera needs a kick to redraw zoomed equations
      });
    }
  });
    
  HUB.Register.StartupHook("End Cookie", function () {
    if (HUB.config.menuSettings.zoom !== "None")
      {AJAX.Require("[MathJax]/extensions/MathZoom.js")}
  });
  
  if (!document.createElementNS) {
    //
    //  Try to handle SVG in IE8 and below, but fail
    //  (but don't crash on loading the file, so no delay for loadComplete)
    //
    if (!document.namespaces.svg) {document.namespaces.add("svg",SVGNS)}
    SVG.Augment({
      Element: function (type,def) {
        var obj = (typeof(type) === "string" ? document.createElement("svg:"+type) : type);
        obj.isMathJax = true;
        if (def) {for (var id in def) {if (def.hasOwnProperty(id)) {obj.setAttribute(id,def[id].toString())}}}
        return obj;
      }
    });
  }
  
})(MathJax.Ajax, MathJax.Hub, MathJax.HTML, MathJax.OutputJax.SVG);

/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/mtable.js
 *  
 *  Implements the SVG output for <mtable> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG,
      BBOX = SVG.BBOX;
  
  MML.mtable.Augment({
    toSVG: function (span) {
      this.SVGgetStyles();
      var svg = this.SVG(), scale = this.SVGgetScale(svg);
      if (this.data.length === 0) {this.SVGsaveData(svg);return svg}
      var values = this.getValues("columnalign","rowalign","columnspacing","rowspacing",
                                  "columnwidth","equalcolumns","equalrows",
                                  "columnlines","rowlines","frame","framespacing",
                                  "align","useHeight","width","side","minlabelspacing");
      //  Handle relative width as fixed width in relation to container
      if (values.width.match(/%$/))
        {svg.width = values.width = SVG.Em((SVG.cwidth/1000)*(parseFloat(values.width)/100))}

      var mu = this.SVGgetMu(svg);
      var LABEL = -1;

      var H = [], D = [], W = [], A = [], C = [], i, j, J = -1,
          m, M, s, row, cell, mo, HD;
      var LH = SVG.FONTDATA.lineH * scale * values.useHeight,
          LD = SVG.FONTDATA.lineD * scale * values.useHeight;

      //
      //  Create cells and measure columns and rows
      //
      for (i = 0, m = this.data.length; i < m; i++) {
        row = this.data[i]; s = (row.type === "mlabeledtr" ? LABEL : 0);
        A[i] = []; H[i] = LH; D[i] = LD;
        for (j = s, M = row.data.length + s; j < M; j++) {
          if (W[j] == null) {
            if (j > J) {J = j}
            C[j] = BBOX.G();
            W[j] = -SVG.BIGDIMEN;
          }
          cell = row.data[j-s];
          A[i][j] = cell.toSVG();
//          if (row.data[j-s].isMultiline) {A[i][j].style.width = "100%"}
          if (cell.isEmbellished()) {
            mo = cell.CoreMO();
            var min = mo.Get("minsize",true);
            if (min) {
              if (mo.SVGcanStretch("Vertical")) {
                HD = mo.SVGdata.h + mo.SVGdata.d;
                if (HD) {
                  min = SVG.length2em(min,mu,HD);
                  if (min*mo.SVGdata.h/HD > H[i]) {H[i] = min*mo.SVGdata.h/HD}
                  if (min*mo.SVGdata.d/HD > D[i]) {D[i] = min*mo.SVGdata.d/HD}
                }
              } else if (mo.SVGcanStretch("Horizontal")) {
                min = SVG.length2em(min,mu,mo.SVGdata.w);
                if (min > W[j]) {W[j] = min}
              }
            }
          }
          if (A[i][j].h > H[i]) {H[i] = A[i][j].h}
          if (A[i][j].d > D[i]) {D[i] = A[i][j].d}
          if (A[i][j].w > W[j]) {W[j] = A[i][j].w}
        }
      }

      //
      //  Determine spacing and alignment
      //
      var SPLIT = MathJax.Hub.SplitList;
      var CSPACE = SPLIT(values.columnspacing),
          RSPACE = SPLIT(values.rowspacing),
          CALIGN = SPLIT(values.columnalign),
          RALIGN = SPLIT(values.rowalign),
          CLINES = SPLIT(values.columnlines),
          RLINES = SPLIT(values.rowlines),
          CWIDTH = SPLIT(values.columnwidth),
          RCALIGN = [];
      for (i = 0, m = CSPACE.length; i < m; i++) {CSPACE[i] = SVG.length2em(CSPACE[i],mu)}
      for (i = 0, m = RSPACE.length; i < m; i++) {RSPACE[i] = SVG.length2em(RSPACE[i],mu)}
      while (CSPACE.length <  J) {CSPACE.push(CSPACE[CSPACE.length-1])}
      while (CALIGN.length <= J) {CALIGN.push(CALIGN[CALIGN.length-1])}
      while (CLINES.length <  J) {CLINES.push(CLINES[CLINES.length-1])}
      while (CWIDTH.length <= J) {CWIDTH.push(CWIDTH[CWIDTH.length-1])}
      while (RSPACE.length <  A.length) {RSPACE.push(RSPACE[RSPACE.length-1])}
      while (RALIGN.length <= A.length) {RALIGN.push(RALIGN[RALIGN.length-1])}
      while (RLINES.length <  A.length) {RLINES.push(RLINES[RLINES.length-1])}
      if (C[LABEL]) {
        CALIGN[LABEL] = (values.side.substr(0,1) === "l" ? "left" : "right");
        CSPACE[LABEL] = -W[LABEL];
      }
      //
      //  Override row data
      //
      for (i = 0, m = A.length; i < m; i++) {
        row = this.data[i]; RCALIGN[i] = [];
        if (row.rowalign) {RALIGN[i] = row.rowalign}
        if (row.columnalign) {
          RCALIGN[i] = SPLIT(row.columnalign);
          while (RCALIGN[i].length <= J) {RCALIGN[i].push(RCALIGN[i][RCALIGN[i].length-1])}
        }
      }

      //
      //  Handle equal heights
      //
      if (values.equalrows) {
        // FIXME:  should really be based on row align (below is for baseline)
        var Hm = Math.max.apply(Math,H), Dm = Math.max.apply(Math,D);
        for (i = 0, m = A.length; i < m; i++)
          {s = ((Hm + Dm) - (H[i] + D[i])) / 2;  H[i] += s; D[i] += s}
      }

      //  FIXME:  do background colors for entire cell (include half the intercolumn space?)
      
      //
      //  Determine array total height
      //
      HD = H[0] + D[A.length-1];
      for (i = 0, m = A.length-1; i < m; i++)
        {HD += Math.max(0,D[i]+H[i+1]+RSPACE[i])}
      //
      //  Determine frame and line sizes
      //
      var fx = 0, fy = 0, fW, fH = HD;
      if (values.frame !== "none" ||
         (values.columnlines+values.rowlines).match(/solid|dashed/)) {
        var frameSpacing = SPLIT(values.framespacing);
        if (frameSpacing.length != 2) {
          // invalid attribute value: use the default.
          frameSpacing = SPLIT(this.defaults.framespacing);
        }
        fx = SVG.length2em(frameSpacing[0],mu);
        fy = SVG.length2em(frameSpacing[1],mu);
        fH = HD + 2*fy; // fW waits until svg.w is determined
      }
      //
      //  Compute alignment
      //
      var Y, fY, n = "";
      if (typeof(values.align) !== "string") {values.align = String(values.align)}
      if (values.align.match(/(top|bottom|center|baseline|axis)( +(-?\d+))?/))
        {n = RegExp.$3||""; values.align = RegExp.$1} else {values.align = this.defaults.align}
      if (n !== "") {
        //
        //  Find the height of the given row
        //
        n = parseInt(n);
        if (n < 0) {n = A.length + 1 + n}
        if (n < 1) {n = 1} else if (n > A.length) {n = A.length}
        Y = 0; fY = -(HD + fy) + H[0];
        for (i = 0, m = n-1; i < m; i++) {
          // FIXME:  Should handle values.align for final row
          var dY = Math.max(0,D[i]+H[i+1]+RSPACE[i]);
          Y += dY; fY += dY;
        }
      } else {
        Y = ({
          top:    -(H[0] + fy),
          bottom:   HD + fy - H[0],
          center:   HD/2 - H[0],
          baseline: HD/2 - H[0],
          axis:     HD/2 + SVG.TeX.axis_height*scale - H[0]
        })[values.align];
        fY = ({
          top:      -(HD + 2*fy),
          bottom:   0,
          center:   -(HD/2 + fy),
          baseline: -(HD/2 + fy),
          axis:     SVG.TeX.axis_height*scale - HD/2 - fy
        })[values.align];
      }
            
      var WW, WP = 0, Wt = 0, Wp = 0, p = 0, f = 0, P = [], F = [], Wf = 1;
      //
      if (values.equalcolumns && values.width !== "auto") {
        //
        //  Handle equalcolumns for percent-width and fixed-width tables
        //

        //  Get total width minus column spacing
        WW = SVG.length2em(values.width,mu);
        for (i = 0, m = Math.min(J,CSPACE.length); i < m; i++) {WW -= CSPACE[i]}
        //  Determine individual column widths
        WW /= J;
        for (i = 0, m = Math.min(J+1,CWIDTH.length); i < m; i++) {W[i] = WW}
      } else {
        //
        //  Get column widths for fit and percentage columns
        //
        //  Calculate the natural widths and percentage widths,
        //    while keeping track of the fit and percentage columns
        for(i = 0, m = Math.min(J+1,CWIDTH.length); i < m; i++) {
          if (CWIDTH[i] === "auto") {Wt += W[i]}
          else if (CWIDTH[i] === "fit") {F[f] = i; f++; Wt += W[i]}
          else if (CWIDTH[i].match(/%$/))
            {P[p] = i; p++; Wp += W[i]; WP += SVG.length2em(CWIDTH[i],mu,1)}
          else {W[i] = SVG.length2em(CWIDTH[i],mu); Wt += W[i]}
        }
        // Get the full width (excluding inter-column spacing)
        if (values.width === "auto") {
          if (WP > .98) {Wf = Wp/(Wt+Wp); WW = Wt + Wp} else {WW = Wt / (1-WP)}
        } else {
          WW = SVG.length2em(values.width,mu);
          for (i = 0, m = Math.min(J,CSPACE.length); i < m; i++) {WW -= CSPACE[i]}
        }
        //  Determine the relative column widths
        for (i = 0, m = P.length; i < m; i++) {
          W[P[i]] = SVG.length2em(CWIDTH[P[i]],mu,WW*Wf); Wt += W[P[i]];
        }
        //  Stretch fit columns, if any, otherwise stretch (or shrink) everything
        if (Math.abs(WW - Wt) > .01) {
          if (f && WW > Wt) {
            WW = (WW - Wt) / f; for (i = 0, m = F.length; i < m; i++) {W[F[i]] += WW}
          } else {WW = WW/Wt; for (j = 0; j <= J; j++) {W[j] *= WW}}
        }
        //
        //  Handle equal columns
        //
        if (values.equalcolumns) {
          var Wm = Math.max.apply(Math,W);
          for (j = 0; j <= J; j++) {W[j] = Wm}
        }
      }
      
      //
      //  Lay out array columns
      //
      var y = Y, dy, align; s = (C[LABEL] ? LABEL : 0);
      for (j = s; j <= J; j++) {
        C[j].w = W[j];
        for (i = 0, m = A.length; i < m; i++) {
          if (A[i][j]) {
            s = (this.data[i].type === "mlabeledtr" ? LABEL : 0);
            cell = this.data[i].data[j-s];
	    if (cell.SVGcanStretch("Horizontal")) {
	      A[i][j] = cell.SVGstretchH(W[j]);
	    } else if (cell.SVGcanStretch("Vertical")) {
	      mo = cell.CoreMO();
	      var symmetric = mo.symmetric; mo.symmetric = false;
	      A[i][j] = cell.SVGstretchV(H[i],D[i]);
	      mo.symmetric = symmetric;
	    }
            align = cell.rowalign||this.data[i].rowalign||RALIGN[i];
            dy = ({top:    H[i] - A[i][j].h,
                   bottom: A[i][j].d - D[i],
                   center: ((H[i]-D[i]) - (A[i][j].h-A[i][j].d))/2,
                   baseline: 0, axis: 0})[align] || 0; // FIXME:  handle axis better?
            align = (cell.columnalign||RCALIGN[i][j]||CALIGN[j])
            C[j].Align(A[i][j],align,0,y+dy);
          }
          if (i < A.length-1) {y -= Math.max(0,D[i]+H[i+1]+RSPACE[i])}
        }
        y = Y;
      }

      //
      //  Place the columns and add column lines
      //
      var lw = 1.5*SVG.em;
      var x = fx - lw/2;
      for (j = 0; j <= J; j++) {
        svg.Add(C[j],x,0); x += W[j] + CSPACE[j];
        if (CLINES[j] !== "none" && j < J && j !== LABEL)
        {svg.Add(BBOX.VLINE(fH,lw,CLINES[j]),x-CSPACE[j]/2,fY)}
      }
      svg.w += fx; svg.d = -fY; svg.h = fH+fY;
      fW = svg.w;
      
      //
      //  Add frame
      //
      if (values.frame !== "none") {
        svg.Add(BBOX.HLINE(fW,lw,values.frame),0,fY+fH-lw);
        svg.Add(BBOX.HLINE(fW,lw,values.frame),0,fY);
        svg.Add(BBOX.VLINE(fH,lw,values.frame),0,fY);
        svg.Add(BBOX.VLINE(fH,lw,values.frame),fW-lw,fY);
      }

      //
      //  Add row lines
      //
      y = Y - lw/2;
      for (i = 0, m = A.length-1; i < m; i++) {
        dy = Math.max(0,D[i]+H[i+1]+RSPACE[i]);
        if (RLINES[i] !== MML.LINES.NONE && RLINES[i] !== "")
          {svg.Add(BBOX.HLINE(fW,lw,RLINES[i]),0,y-D[i]-(dy-D[i]-H[i+1])/2)}
        y -= dy;
      }
      
      //
      //  Finish the table
      //
      svg.Clean();
      this.SVGhandleSpace(svg);
      this.SVGhandleColor(svg);
      
      //
      //  Place the labels, if any
      //
      if (C[LABEL]) {
        svg.tw = Math.max(svg.w,svg.r) - Math.min(0,svg.l);
        var indent = this.getValues("indentalignfirst","indentshiftfirst","indentalign","indentshift");
        if (indent.indentalignfirst !== MML.INDENTALIGN.INDENTALIGN) {indent.indentalign = indent.indentalignfirst}
        if (indent.indentalign === MML.INDENTALIGN.AUTO) {indent.indentalign = this.displayAlign}
        if (indent.indentshiftfirst !== MML.INDENTSHIFT.INDENTSHIFT) {indent.indentshift = indent.indentshiftfirst}
        if (indent.indentshift === "auto" || indent.indentshift === "") {indent.indentshift = "0"}
        var shift = SVG.length2em(indent.indentshift,mu,SVG.cwidth);
        var labelspace = SVG.length2em(values.minlabelspacing,mu,SVG.cwidth);
        var labelW = labelspace + C[LABEL].w, labelshift = 0, tw = svg.w;
        var dIndent = SVG.length2em(this.displayIndent,mu,SVG.cwidth);
        s = (CALIGN[LABEL] === MML.INDENTALIGN.RIGHT ? -1 : 1);
        if (indent.indentalign === MML.INDENTALIGN.CENTER) {
          var dx = (SVG.cwidth-tw)/2; shift += dIndent;
          if (labelW + s*labelshift > dx + s*shift) {
            indent.indentalign = CALIGN[LABEL];
            shift = s*(labelW + s*labelshift); tw += labelW + Math.max(0,shift);
          }
        } else if (CALIGN[LABEL] === indent.indentalign) {
          if (dIndent < 0) {labelshift = s*dIndent; dIndent = 0}
          shift += s*dIndent; if (labelW > s*shift) shift = s*labelW; shift += labelshift;
          tw += s*shift;
        } else {
          shift -= s*dIndent;
          if (tw - s*shift + labelW > SVG.cwidth) {
            shift = s*(tw + labelW - SVG.cwidth);
            if (s*shift > 0) {tw = SVG.cwidth + s*shift; shift = 0}
          }
        }
        var eqn = svg; svg = this.SVG();
        svg.hasIndent = true;
        svg.w = svg.r = Math.max(tw,SVG.cwidth); 
        svg.Align(C[LABEL],CALIGN[LABEL],0,0,labelshift);
        svg.Align(eqn,indent.indentalign,0,0,shift);
        svg.tw = tw;
      }
      
      this.SVGsaveData(svg);
      return svg;
    },
    SVGhandleSpace: function (svg) {
      if (!this.hasFrame && !svg.width) {svg.x = svg.X = 167}
      this.SUPER(arguments).SVGhandleSpace.call(this,svg);
    }
  });
  
  MML.mtd.Augment({
    toSVG: function (HW,D) {
      var svg = this.svg = this.SVG();
      if (this.data[0]) {
        svg.Add(this.SVGdataStretched(0,HW,D));
        svg.Clean();
      }
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    }
  });

  MathJax.Hub.Startup.signal.Post("SVG mtable Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/mtable.js");
  
});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/mglyph.js
 *  
 *  Implements the SVG output for <mglyph> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG,
      BBOX = SVG.BBOX,
      LOCALE = MathJax.Localization;
  
  var XLINKNS = "http://www.w3.org/1999/xlink";

  BBOX.MGLYPH = BBOX.Subclass({
    type: "image", removeable: false,
    Init: function (img,w,h,align,mu,def) {
      if (def == null) {def = {}}
      var W = img.width*1000/SVG.em, H = img.height*1000/SVG.em;
      var WW = W, HH = H, y = 0;
      if (w !== "") {W = SVG.length2em(w,mu,WW); H = (WW ? W/WW * HH : 0)}
      if (h !== "") {H = SVG.length2em(h,mu,HH); if (w === "") {W = (HH ? H/HH * WW : 0)}}
      if (align !== "" && align.match(/\d/)) {y = SVG.length2em(align,mu); def.y = -y}
      def.height = Math.floor(H); def.width = Math.floor(W);
      def.transform = "translate(0,"+H+") matrix(1 0 0 -1 0 0)";
      def.preserveAspectRatio = "none";
      this.SUPER(arguments).Init.call(this,def);
      this.element.setAttributeNS(XLINKNS,"href",img.SRC);
      this.w = this.r = W; this.h = this.H = H + y;
      this.d = this.D = -y; this.l = 0;
    }
  });
  
  MML.mglyph.Augment({
    toSVG: function (variant,scale) {
      this.SVGgetStyles(); var svg = this.SVG(), img, err;
      this.SVGhandleSpace(svg);
      var values = this.getValues("src","width","height","valign","alt");
      if (values.src === "") {
        values = this.getValues("index","fontfamily");
        if (values.index) {
          if (!scale) {scale = this.SVGgetScale()}
          var def = {}; if (values.fontfamily) {def["font-family"] = values.fontfamily}
          svg.Add(BBOX.TEXT(scale,String.fromCharCode(values.index),def));
        }
      } else {
        if (!this.img) {this.img = MML.mglyph.GLYPH[values.src]}
        if (!this.img) {
          this.img = MML.mglyph.GLYPH[values.src] = {img: new Image(), status: "pending"};
          img = this.img.img;
          img.onload  = MathJax.Callback(["SVGimgLoaded",this]);
          img.onerror = MathJax.Callback(["SVGimgError",this]);
          img.src = img.SRC = values.src;
          MathJax.Hub.RestartAfter(img.onload);
        }
        if (this.img.status !== "OK") {
          err = MML.Error(
            LOCALE._(["MathML","BadMglyph"],"Bad mglyph: %1",values.src),
            {mathsize:"75%"});
          this.Append(err); svg = err.toSVG(); this.data.pop();
        } else {
          var mu = this.SVGgetMu(svg);
          svg.Add(BBOX.MGLYPH(this.img.img,values.width,values.height,values.valign,mu,
                              {alt:values.alt, title:values.alt}));
        }
      }
      svg.Clean();
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    },
    SVGimgLoaded: function (event,status) {
      if (typeof(event) === "string") {status = event}
      this.img.status = (status || "OK")
    },
    SVGimgError: function () {this.img.img.onload("error")}
  },{
    GLYPH: {}    // global list of all loaded glyphs
  });
  
  MathJax.Hub.Startup.signal.Post("SVG mglyph Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/mglyph.js");
  
});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/mmultiscripts.js
 *  
 *  Implements the SVG output for <mmultiscripts> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG;
  
  MML.mmultiscripts.Augment({
    toSVG: function (HW,D) {
      this.SVGgetStyles();
      var svg = this.SVG(), scale = this.SVGgetScale(svg); this.SVGhandleSpace(svg);
      var base = (this.data[this.base] ? this.SVGdataStretched(this.base,HW,D) : SVG.BBOX.G().Clean());
      var x_height = SVG.TeX.x_height * scale,
          s = SVG.TeX.scriptspace * scale * .75;  // FIXME: .75 can be removed when IC is right?

      var BOX = this.SVGgetScripts(s);
      var sub = BOX[0], sup = BOX[1], presub = BOX[2], presup = BOX[3];

      var sscale = (this.data[1]||this).SVGgetScale();
      var q = SVG.TeX.sup_drop * sscale, r = SVG.TeX.sub_drop * sscale;
      var u = base.h - q, v = base.d + r, delta = 0, p;
      if (base.ic) {delta = base.ic}
      if (this.data[this.base] &&
         (this.data[this.base].type === "mi" || this.data[this.base].type === "mo")) {
        if (this.data[this.base].data.join("").length === 1 && base.scale === 1 &&
            !base.stretched && !this.data[this.base].Get("largeop")) {u = v = 0}
      }
      var min = this.getValues("subscriptshift","superscriptshift"), mu = this.SVGgetMu(svg);
      min.subscriptshift   = (min.subscriptshift === ""   ? 0 : SVG.length2em(min.subscriptshift,mu));
      min.superscriptshift = (min.superscriptshift === "" ? 0 : SVG.length2em(min.superscriptshift,mu));

      var dx = 0;
      if (presub) {dx = presub.w+delta} else if (presup) {dx = presup.w-delta}
      svg.Add(base,Math.max(0,dx),0);

      if (!sup && !presup) {
        v = Math.max(v,SVG.TeX.sub1*scale,min.subscriptshift);
        if (sub)    {v = Math.max(v,sub.h-(4/5)*x_height)}
        if (presub) {v = Math.max(v,presub.h-(4/5)*x_height)}
        if (sub)    {svg.Add(sub,dx+base.w+s-delta,-v)}
        if (presub) {svg.Add(presub,0,-v)}
      } else {
        if (!sub && !presub) {
          var values = this.getValues("displaystyle","texprimestyle");
          p = SVG.TeX[(values.displaystyle ? "sup1" : (values.texprimestyle ? "sup3" : "sup2"))];
          u = Math.max(u,p*scale,min.superscriptshift);
          if (sup)    {u = Math.max(u,sup.d+(1/4)*x_height)}
          if (presup) {u = Math.max(u,presup.d+(1/4)*x_height)}
          if (sup)    {svg.Add(sup,dx+base.w+s,u)}
          if (presup) {svg.Add(presup,0,u)}
        } else {
          v = Math.max(v,SVG.TeX.sub2*scale);
          var t = SVG.TeX.rule_thickness * scale;
          var h = (sub||presub).h, d = (sup||presup).d;
          if (presub) {h = Math.max(h,presub.h)}
          if (presup) {d = Math.max(d,presup.d)}
          if ((u - d) - (h - v) < 3*t) {
            v = 3*t - u + d + h; q = (4/5)*x_height - (u - d);
            if (q > 0) {u += q; v -= q}
          }
          u = Math.max(u,min.superscriptshift); v = Math.max(v,min.subscriptshift);
          if (sup)    {svg.Add(sup,dx+base.w+s,u)}
          if (presup) {svg.Add(presup,dx+delta-presup.w,u)}
          if (sub)    {svg.Add(sub,dx+base.w+s-delta,-v)}
          if (presub) {svg.Add(presub,dx-presub.w,-v)}
        }
      }
      svg.Clean();
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      var data = this.SVGdata;
      data.dx = dx; data.s = s; data.u = u, data.v = v; data.delta = delta;      
      return svg;
    },
    SVGgetScripts: function (s) {
      var sup, sub, BOX = [];
      var i = 1, m = this.data.length, W = 0;
      for (var k = 0; k < 4; k += 2) {
        while (i < m && (this.data[i]||{}).type !== "mprescripts") {
          var box = [null,null,null,null];
          for (var j = k; j < k+2; j++) {
            if (this.data[i] && this.data[i].type !== "none" && this.data[i].type !== "mprescripts") {
              if (!BOX[j]) {BOX[j] = SVG.BBOX.G()}
              box[j] = this.data[i].toSVG();
            }
            if ((this.data[i]||{}).type !== "mprescripts") i++;
          }
          var isPre = (k === 2);
          if (isPre) W += Math.max((box[k]||{w:0}).w,(box[k+1]||{w:0}).w);
          if (box[k])   BOX[k].Add(box[k].With({x:W-(isPre?box[k].w:0)}));
          if (box[k+1]) BOX[k+1].Add(box[k+1].With({x:W-(isPre?box[k+1].w:0)}));
          sub = BOX[k]||{w:0}; sup = BOX[k+1]||{w:0};
          sub.w = sup.w = W = Math.max(sub.w,sup.w);
        }
        i++; W = 0;
      }
      for (j = 0; j < 4; j++) {if (BOX[j]) {BOX[j].w += s; BOX[j].Clean()}}
      return BOX;
    }
  });
  
  MathJax.Hub.Startup.signal.Post("SVG mmultiscripts Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/mmultiscripts.js");

});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/annotation-xml.js
 *  
 *  Implements the SVG output for <annotation-xml> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2013-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG;
  var BBOX = SVG.BBOX;
  
  BBOX.FOREIGN = BBOX.Subclass({type: "foreignObject", removeable: false});

  MML["annotation-xml"].Augment({
    toSVG: function () {
      var svg = this.SVG(); this.SVGhandleSpace(svg);
      var encoding = this.Get("encoding");
      for (var i = 0, m = this.data.length; i < m; i++)
        {svg.Add(this.data[i].toSVG(encoding),svg.w,0)}
      svg.Clean();
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    }
  });
  
  MML.xml.Augment({
    toSVG: function (encoding) {
      //
      //  Get size of xml content
      //
      var span = SVG.textSVG.parentNode;
      SVG.mathDiv.style.width = "auto";  // Firefox returns offsetWidth = 0 without this
      span.insertBefore(this.div,SVG.textSVG);
      var w = this.div.offsetWidth, h = this.div.offsetHeight;
      var strut = MathJax.HTML.addElement(this.div,"span",{
        style:{display:"inline-block", overflow:"hidden", height:h+"px",
               width:"1px", marginRight:"-1px"}
      });
      var d = this.div.offsetHeight - h; h -= d;
      this.div.removeChild(strut);
      span.removeChild(this.div); SVG.mathDiv.style.width = "";
      //
      //  Create foreignObject element for the content
      //
      var scale = 1000/SVG.em;
      var svg = BBOX.FOREIGN({
        y:(-h)+"px", width:w+"px", height:(h+d)+"px",
        transform:"scale("+scale+") matrix(1 0 0 -1 0 0)"
      });
      //
      //  Add the children to the foreignObject
      //
      for (var i = 0, m = this.data.length; i < m; i++) 
        {svg.element.appendChild(this.data[i].cloneNode(true))}
      //
      //  Set the scale and finish up
      //
      svg.w = w*scale; svg.h = h*scale; svg.d = d*scale;
      svg.r = svg.w; svg.l = 0;
      svg.Clean();
      this.SVGsaveData(svg);
      return svg;
    }
  });
  
  MathJax.Hub.Startup.signal.Post("SVG annotation-xml Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/annotation-xml.js");

});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/maction.js
 *  
 *  Implements the SVG output for <maction> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax["SVG"];
  
  var currentTip, hover, clear;

  //
  //  Add configuration for tooltips
  //
  var CONFIG = SVG.config.tooltip = MathJax.Hub.Insert({
    delayPost: 600, delayClear: 600,
    offsetX: 10, offsetY: 5
  },SVG.config.tooltip||{});
  
  
  MML.maction.Augment({
    SVGtooltip: MathJax.HTML.addElement(document.body,"div",{id:"MathJax_SVG_Tooltip"}),
    
    toSVG: function (HW,D) {
      this.SVGgetStyles();
      var svg = this.SVG();
      var selected = this.selected();
      if (selected.type == "null") {this.SVGsaveData(svg);return svg;}
      svg.Add(this.SVGdataStretched(this.Get("selection")-1,HW,D));
      svg.removeable = false;
      this.SVGhandleHitBox(svg);
      this.SVGhandleSpace(svg);
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    },
    SVGhandleHitBox: function (svg) {
      var frame = SVG.Element("rect",
        {width:svg.w, height:svg.h+svg.d, y:-svg.d, fill:"none", "pointer-events":"all"});
      svg.element.insertBefore(frame,svg.element.firstChild);
      var type = this.Get("actiontype");
      if (this.SVGaction[type])
        {this.SVGaction[type].call(this,svg,svg.element,this.Get("selection"))}
    },
    SVGstretchH: MML.mbase.prototype.SVGstretchH,
    SVGstretchV: MML.mbase.prototype.SVGstretchV,
    
    //
    //  Implementations for the various actions
    //
    SVGaction: {
      toggle: function (svg,frame,selection) {
        this.selection = selection;
        SVG.Element(frame,{cursor:"pointer"});
        frame.onclick = MathJax.Callback(["SVGclick",this]);
      },
      
      statusline: function (svg,frame,selection) {
        frame.onmouseover = MathJax.Callback(["SVGsetStatus",this]),
        frame.onmouseout  = MathJax.Callback(["SVGclearStatus",this]);
        frame.onmouseover.autoReset = frame.onmouseout.autoReset = true;
      },
      
      tooltip: function(svg,frame,selection) {
        frame.onmouseover = MathJax.Callback(["SVGtooltipOver",this]),
        frame.onmouseout  = MathJax.Callback(["SVGtooltipOut",this]);
        frame.onmouseover.autoReset = frame.onmouseout.autoReset = true;
      }
    },
    
    //
    //  Handle a click on the maction element
    //    (remove the original rendering and rerender)
    //
    SVGclick: function (event) {
      this.selection++;
      if (this.selection > this.data.length) {this.selection = 1}
      var math = this; while (math.type !== "math") {math = math.inherit}
      var jax = MathJax.Hub.getJaxFor(math.inputID); //, hover = !!jax.hover;
      jax.Update();
      /* 
       * if (hover) {
       *   var span = document.getElementById(jax.inputID+"-Span");
       *   MathJax.Extension.MathEvents.Hover.Hover(jax,span);
       * }
       */
      return MathJax.Extension.MathEvents.Event.False(event);
    },
    
    //
    //  Set/Clear the window status message
    //
    SVGsetStatus: function (event) {
      // FIXME:  Do something better with non-token elements
      this.messageID = MathJax.Message.Set
        ((this.data[1] && this.data[1].isToken) ?
             this.data[1].data.join("") : this.data[1].toString());
    },
    SVGclearStatus: function (event) {
      if (this.messageID) {MathJax.Message.Clear(this.messageID,0)}
      delete this.messageID;
    },
    
    //
    //  Handle tooltips
    //
    SVGtooltipOver: function (event) {
      if (!event) {event = window.event}
      if (clear) {clearTimeout(clear); clear = null}
      if (hover) {clearTimeout(hover)}
      var x = event.pageX; var y = event.pageY;
      if (x == null) {
        x = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
        y = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
      }
      var callback = MathJax.Callback(["SVGtooltipPost",this,x+CONFIG.offsetX,y+CONFIG.offsetY])
      hover = setTimeout(callback,CONFIG.delayPost);
    },
    SVGtooltipOut: function (event) {
      if (hover) {clearTimeout(hover); hover = null}
      if (clear) {clearTimeout(clear)}
      var callback = MathJax.Callback(["SVGtooltipClear",this,80]);
      clear = setTimeout(callback,CONFIG.delayClear);
    },
    SVGtooltipPost: function (x,y) {
      hover = null; if (clear) {clearTimeout(clear); clear = null}

      //
      //  Get the tip div and show it at the right location, then clear its contents
      //
      var tip = this.SVGtooltip;
      tip.style.display = "block"; tip.style.opacity = "";
      if (this === currentTip) return;
      tip.style.left = x+"px"; tip.style.top = y+"px";
      tip.innerHTML = ''; var span = MathJax.HTML.addElement(tip,"span");

      //
      //  Get the sizes from the jax (FIXME: should calculate again?)
      //
      var math = this; while (math.type !== "math") {math = math.inherit}
      var jax = MathJax.Hub.getJaxFor(math.inputID);
      this.em = MML.mbase.prototype.em = jax.SVG.em; this.ex = jax.SVG.ex;
      this.linebreakWidth = jax.SVG.lineWidth; this.cwidth = jax.SVG.cwidth;

      //
      //  Make a new math element and temporarily move the tooltip to it
      //  Display the math containing the tip, but check for errors
      //  Then put the tip back into the maction element
      //
      var mml = this.data[1];
      math = MML.math(mml);
      try {math.toSVG(span,tip)} catch(err) {
        this.SetData(1,mml); tip.style.display = "none";
        if (!err.restart) {throw err}
        MathJax.Callback.After(["SVGtooltipPost",this,x,y],err.restart);
        return;
      }
      this.SetData(1,mml);

      currentTip = this;
    },
    SVGtooltipClear: function (n) {
      var tip = this.SVGtooltip;
      if (n <= 0) {
        tip.style.display = "none";
        tip.style.opacity = "";
        clear = null;
      } else {
        tip.style.opacity = n/100;
        clear = setTimeout(MathJax.Callback(["SVGtooltipClear",this,n-20]),50);
      }
    }
  });

  MathJax.Hub.Startup.signal.Post("SVG maction Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/maction.js");
  
});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/multiline.js
 *  
 *  Implements the SVG output for <mrow>'s that contain line breaks.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG,
      BBOX = SVG.BBOX;
      
  //
  //  Penalties for the various line breaks
  //
  var PENALTY = {
    newline:         0,
    nobreak:   1000000,
    goodbreak:   [-200],
    badbreak:    [+200],
    auto:           [0],
    
    toobig:        800,
    nestfactor:    400,
    spacefactor:  -100,
    spaceoffset:     2,
    spacelimit:      1,  // spaces larger than this get a penalty boost
    fence:         500,
    close:         500
  };
  
  var ENDVALUES = {linebreakstyle: "after"};

  
  /**************************************************************************/
  
  MML.mrow.Augment({
    //
    // Handle breaking an mrow into separate lines
    //
    SVGmultiline: function (svg) {

      //
      //  Find the parent element and mark it as multiline
      //
      var parent = this;
      while (parent.inferred || (parent.parent && parent.parent.type === "mrow" &&
             parent.isEmbellished())) {parent = parent.parent}
      var isTop = ((parent.type === "math" && parent.Get("display") === "block") ||
                    parent.type === "mtd");
      parent.isMultiline = true;
      
      //
      //  Default values for the line-breaking parameters
      //
      var VALUES = this.getValues(
        "linebreak","linebreakstyle","lineleading","linebreakmultchar",
        "indentalign","indentshift",
        "indentalignfirst","indentshiftfirst",
        "indentalignlast","indentshiftlast"
      );
      if (VALUES.linebreakstyle === MML.LINEBREAKSTYLE.INFIXLINEBREAKSTYLE) 
        {VALUES.linebreakstyle = this.Get("infixlinebreakstyle")}
      VALUES.lineleading = SVG.length2em(VALUES.lineleading,1,0.5);

      //
      //  Start with a fresh SVG element
      //  and make it full width if we are breaking to a specific width
      //    in the top-level math element
      //
      svg = this.SVG();
      if (isTop && parent.type !== "mtd") {
        if (SVG.linebreakWidth < SVG.BIGDIMEN) {svg.w = SVG.linebreakWidth}
          else {svg.w = SVG.cwidth}
      }

      var state = {
            n: 0, Y: 0,
            scale: this.scale || 1,
            isTop: isTop,
            values: {},
            VALUES: VALUES
          },
          align = this.SVGgetAlign(state,{}),
          shift = this.SVGgetShift(state,{},align),
          start = [],
          end = {
            index:[], penalty:PENALTY.nobreak,
            w:0, W:shift, shift:shift, scanW:shift,
            nest: 0
          },
          broken = false;
          
      //
      //  Break the expression at its best line breaks
      //
      while (this.SVGbetterBreak(end,state) && 
             (end.scanW >= SVG.linebreakWidth || end.penalty === PENALTY.newline)) {
        this.SVGaddLine(svg,start,end.index,state,end.values,broken);
        start = end.index.slice(0); broken = true;
        align = this.SVGgetAlign(state,end.values);
        shift = this.SVGgetShift(state,end.values,align);
        if (align === MML.INDENTALIGN.CENTER) {shift = 0}
        end.W = end.shift = end.scanW = shift; end.penalty = PENALTY.nobreak;
      }
      state.isLast = true;
      this.SVGaddLine(svg,start,[],state,ENDVALUES,broken);

      this.SVGhandleSpace(svg);
      this.SVGhandleColor(svg);
      svg.isMultiline = true;

      this.SVGsaveData(svg);
      return svg;
    }
  });
  
  /**************************************************************************/

  MML.mbase.Augment({
    SVGlinebreakPenalty: PENALTY,

    /****************************************************************/
    //
    //  Locate the next linebreak that is better than the current one
    //
    SVGbetterBreak: function (info,state) {
      if (this.isToken) {return false}  // FIXME: handle breaking of token elements
      if (this.isEmbellished()) {
        info.embellished = this;
        return this.CoreMO().SVGbetterBreak(info,state);
      }
      if (this.linebreakContainer) {return false}
      //
      //  Get the current breakpoint position and other data
      //
      var index = info.index.slice(0), i = info.index.shift(),
          m = this.data.length, W, w, scanW, broken = (info.index.length > 0), better = false;
      if (i == null) {i = -1}; if (!broken) {i++; info.W += info.w; info.w = 0}
      scanW = info.scanW = info.W; info.nest++;
      //
      //  Look through the line for breakpoints,
      //    (as long as we are not too far past the breaking width)
      //
      while (i < m && info.scanW < 1.33*SVG.linebreakWidth) {
        if (this.data[i]) {
          if (this.data[i].SVGbetterBreak(info,state)) {
            better = true; index = [i].concat(info.index); W = info.W; w = info.w;
            if (info.penalty === PENALTY.newline) {
              info.index = index;
              if (info.nest) {info.nest--}
              return true;
            }
          }
          scanW = (broken ? info.scanW : this.SVGaddWidth(i,info,scanW));
        }
        info.index = []; i++; broken = false;
      }
      if (info.nest) {info.nest--}
      info.index = index;
      if (better) {info.W = W}
      return better;
    },
    SVGaddWidth: function (i,info,scanW) {
      if (this.data[i]) {
        var svg = this.data[i].SVGdata;
        scanW += svg.w + svg.x; if (svg.X) {scanW += svg.X}
        info.W = info.scanW = scanW; info.w = 0;
      }
      return scanW;
    },
    
    /****************************************************************/
    //
    //  Create a new line and move the required elements into it
    //  Position it using proper alignment and indenting
    //
    SVGaddLine: function (svg,start,end,state,values,broken) {
      //
      //  Create a box for the line, with empty BBox
      //    fill it with the proper elements,
      //    and clean up the bbox
      //
      var line = BBOX();
      state.first = broken; state.last = true;
      this.SVGmoveLine(start,end,line,state,values);
      line.Clean();
      //
      //  Get the alignment and shift values
      //
      var align = this.SVGgetAlign(state,values),
          shift = this.SVGgetShift(state,values,align);
      //
      //  Set the Y offset based on previous depth, leading, and current height
      //
      if (state.n > 0) {
        var LHD = SVG.FONTDATA.baselineskip * state.scale;
        var leading = (state.values.lineleading == null ? state.VALUES : state.values).lineleading * state.scale;
        state.Y -= Math.max(LHD,state.d + line.h + leading);
      }
      //
      //  Place the new line
      //
      if (line.w + shift > svg.w) svg.w = line.w + shift;
      svg.Align(line,align,0,state.Y,shift);
      //
      //  Save the values needed for the future
      //
      state.d = line.d; state.values = values; state.n++;
    },
    
    /****************************************************************/
    //
    //  Get alignment and shift values from the given data
    //
    SVGgetAlign: function (state,values) {
      var cur = values, prev = state.values, def = state.VALUES, align;
      if (state.n === 0)     {align = cur.indentalignfirst || prev.indentalignfirst || def.indentalignfirst}
      else if (state.isLast) {align = prev.indentalignlast || def.indentalignlast}
      else                   {align = prev.indentalign || def.indentalign}
      if (align === MML.INDENTALIGN.INDENTALIGN) {align = prev.indentalign || def.indentalign}
      if (align === MML.INDENTALIGN.AUTO) {align = (state.isTop ? this.displayAlign : MML.INDENTALIGN.LEFT)}
      return align;
    },
    SVGgetShift: function (state,values,align) {
      var cur = values, prev = state.values, def = state.VALUES, shift;
      if (state.n === 0)     {shift = cur.indentshiftfirst || prev.indentshiftfirst || def.indentshiftfirst}
      else if (state.isLast) {shift = prev.indentshiftlast || def.indentshiftlast}
      else                   {shift = prev.indentshift || def.indentshift}
      if (shift === MML.INDENTSHIFT.INDENTSHIFT) {shift = prev.indentshift || def.indentshift}
      if (shift === "auto" || shift === "") {shift = "0"}
      shift = SVG.length2em(shift,1,SVG.cwidth);
      if (state.isTop && this.displayIndent !== "0") {
        var indent = SVG.length2em(this.displayIndent,1,SVG.cwidth);
        shift += (align === MML.INDENTALIGN.RIGHT ? -indent: indent);
      }
      return shift;
    },
    
    /****************************************************************/
    //
    //  Move the selected elements into the new line,
    //    moving whole items when possible, and parts of ones
    //    that are split by a line break.
    //  
    SVGmoveLine: function (start,end,svg,state,values) {
      var i = start[0], j = end[0];
      if (i == null) {i = -1}; if (j == null) {j = this.data.length-1}
      if (i === j && start.length > 1) {
        //
        //  If starting and ending in the same element move the subpiece to the new line
        //
        this.data[i].SVGmoveSlice(start.slice(1),end.slice(1),svg,state,values,"paddingLeft");
      } else {
        //
        //  Otherwise, move the remainder of the initial item
        //  and any others up to the last one
        //
        var last = state.last; state.last = false;
        while (i < j) {
          if (this.data[i]) {
            if (start.length <= 1) {this.data[i].SVGmove(svg,state,values)}
              else {this.data[i].SVGmoveSlice(start.slice(1),[],svg,state,values,"paddingLeft")}
          }
          i++; state.first = false; start = [];
        }
        //
        //  If the last item is complete, move it,
        //    otherwise move the first part of it up to the split
        //
        state.last = last;
        if (this.data[i]) {
          if (end.length <= 1) {this.data[i].SVGmove(svg,state,values)}
            else {this.data[i].SVGmoveSlice([],end.slice(1),svg,state,values,"paddingRight")}
        }
      }
    },
    
    /****************************************************************/
    //
    //  Split an element and copy the selected items into the new part
    //
    SVGmoveSlice: function (start,end,svg,state,values,padding) {
      //
      //  Create a new container for the slice of the element
      //  Move the selected portion into the slice
      //
      var slice = BBOX();
      this.SVGmoveLine(start,end,slice,state,values);
      slice.Clean();
      if (this.href) {this.SVGaddHref(slice)}
      this.SVGhandleColor(slice);
      svg.Add(slice,svg.w,0,true);
      return slice;
    },

    /****************************************************************/
    //
    //  Move an element from its original position to its new location in
    //    a split element or the new line's position
    //
    SVGmove: function (line,state,values) {
      // FIXME:  handle linebreakstyle === "duplicate"
      // FIXME:  handle linebreakmultchar
      if (!(state.first || state.last) ||
           (state.first && state.values.linebreakstyle === MML.LINEBREAKSTYLE.BEFORE) ||
           (state.last && values.linebreakstyle === MML.LINEBREAKSTYLE.AFTER)) {
        //
        //  Recreate output
        //  Remove padding (if first, remove at leftt, if last remove at right)
        //  Add to line
        //
        var svg = this.toSVG(this.SVGdata.HW,this.SVGdata.D);
        if (state.first || state.nextIsFirst) {svg.x = 0}
        if (state.last && svg.X) {svg.X = 0}
        line.Add(svg,line.w,0,true);
      }
      if (state.first && svg && svg.w === 0) {state.nextIsFirst = true}
        else {delete state.nextIsFirst}
    }
  });
      
  /**************************************************************************/

  MML.mfenced.Augment({
    SVGbetterBreak: function (info,state) {
      //
      //  Get the current breakpoint position and other data
      //
      var index = info.index.slice(0), i = info.index.shift(),
          m = this.data.length, W, w, scanW, broken = (info.index.length > 0), better = false;
      if (i == null) {i = -1}; if (!broken) {i++; info.W += info.w; info.w = 0}
      scanW = info.scanW = info.W; info.nest++;
      //
      //  Create indices that include the delimiters and separators
      //
      if (!this.dataI) {
        this.dataI = [];
        if (this.data.open) {this.dataI.push("open")}
        if (m) {this.dataI.push(0)}
        for (var j = 1; j < m; j++) {
          if (this.data["sep"+j]) {this.dataI.push("sep"+j)}
          this.dataI.push(j);
        }
        if (this.data.close) {this.dataI.push("close")}
      }
      m = this.dataI.length;
      //
      //  Look through the line for breakpoints, including the open, close, and separators
      //    (as long as we are not too far past the breaking width)
      //
      while (i < m && info.scanW < 1.33*SVG.linebreakWidth) {
        var k = this.dataI[i];
        if (this.data[k]) {
          if (this.data[k].SVGbetterBreak(info,state)) {
            better = true; index = [i].concat(info.index); W = info.W; w = info.w;
            if (info.penalty === PENALTY.newline) {
              info.index = index;
              if (info.nest) {info.nest--}
              return true;
            }
          }
          scanW = (broken ? info.scanW : this.SVGaddWidth(i,info,scanW));
        }
        info.index = []; i++; broken = false;
      }
      if (info.nest) {info.nest--}
      info.index = index;
      if (better) {info.W = W; info.w = w}
      return better;
    },

    SVGmoveLine: function (start,end,svg,state,values) {
      var i = start[0], j = end[0];
      if (i == null) {i = -1}; if (j == null) {j = this.dataI.length-1}
      if (i === j && start.length > 1) {
        //
        //  If starting and ending in the same element move the subpiece to the new line
        //
        this.data[this.dataI[i]].SVGmoveSlice(start.slice(1),end.slice(1),svg,state,values,"paddingLeft");
      } else {
        //
        //  Otherwise, move the remainder of the initial item
        //  and any others (including open and separators) up to the last one
        //
        var last = state.last; state.last = false; var k = this.dataI[i];
        while (i < j) {
          if (this.data[k]) {
            if (start.length <= 1) {this.data[k].SVGmove(svg,state,values)}
              else {this.data[k].SVGmoveSlice(start.slice(1),[],svg,state,values,"paddingLeft")}
          }
          i++; k = this.dataI[i]; state.first = false; start = [];
        }
        //
        //  If the last item is complete, move it
        //
        state.last = last;
        if (this.data[k]) {
          if (end.length <= 1) {this.data[k].SVGmove(svg,state,values)}
            else {this.data[k].SVGmoveSlice([],end.slice(1),svg,state,values,"paddingRight")}
        }
      }
    }
    
  });
  
  /**************************************************************************/

  MML.msubsup.Augment({
    SVGbetterBreak: function (info,state) {
      if (!this.data[this.base]) {return false}
      //
      //  Get the current breakpoint position and other data
      //
      var index = info.index.slice(0), i = info.index.shift(),
          W, w, scanW, broken = (info.index.length > 0), better = false;
      if (!broken) {info.W += info.w; info.w = 0}
      scanW = info.scanW = info.W;
      //
      //  Record the width of the base and the super- and subscripts
      //
      if (i == null) {this.SVGdata.dw = this.SVGdata.w - this.data[this.base].SVGdata.w}
      //
      //  Check if the base can be broken
      //
      if (this.data[this.base].SVGbetterBreak(info,state)) {
        better = true; index = [this.base].concat(info.index); W = info.W; w = info.w;
        if (info.penalty === PENALTY.newline) {better = broken = true}
      }
      //
      //  Add in the base if it is unbroken, and add the scripts
      //
      if (!broken) {this.SVGaddWidth(this.base,info,scanW)}
      info.scanW += this.SVGdata.dw; info.W = info.scanW;
      info.index = []; if (better) {info.W = W; info.w = w; info.index = index}
      return better;
    },
    
    SVGmoveLine: function (start,end,svg,state,values) {
      //
      //  Move the proper part of the base
      //
      if (this.data[this.base]) {
        if (start.length > 1) {
          this.data[this.base].SVGmoveSlice(start.slice(1),end.slice(1),svg,state,values,"paddingLeft");
        } else {
          if (end.length <= 1) {this.data[this.base].SVGmove(svg,state,values)}
            else {this.data[this.base].SVGmoveSlice([],end.slice(1),svg,state,values,"paddingRight")}
        }
      }
      //
      //  If this is the end, check for super and subscripts, and move those
      //  by moving the stack that contains them, and shifting by the amount of the
      //  base that has been removed.  Remove the empty base box from the stack.
      //
      if (end.length === 0) {
        var sup = this.data[this.sup], sub = this.data[this.sub], w = svg.w, data;
        if (sup) {data = sup.SVGdata||{}; svg.Add(sup.toSVG(),w+(data.dx||0),data.dy)}
        if (sub) {data = sub.SVGdata||{}; svg.Add(sub.toSVG(),w+(data.dx||0),data.dy)}
      }
    }

  });
  
  /**************************************************************************/

  MML.mmultiscripts.Augment({
    SVGbetterBreak: function (info,state) {
      if (!this.data[this.base]) {return false}
      //
      //  Get the current breakpoint position and other data
      //
      var index = info.index.slice(0); info.index.shift();
      var W, w, scanW, broken = (info.index.length > 0), better = false;
      if (!broken) {info.W += info.w; info.w = 0}
      info.scanW = info.W;
      //
      //  The width of the postscripts
      //
      var dw = this.SVGdata.w - this.data[this.base].SVGdata.w - this.SVGdata.dx;
      //
      //  Add in the prescripts
      //  
      info.scanW += this.SVGdata.dx; scanW = info.scanW;
      //
      //  Check if the base can be broken (but don't break between prescripts and base)
      //
      if (this.data[this.base].SVGbetterBreak(info,state)) {
        better = true; index = [this.base].concat(info.index); W = info.W; w = info.w;
        if (info.penalty === PENALTY.newline) {better = broken = true}
      }
      //
      //  Add in the base if it is unbroken, and add the postscripts
      //
      if (!broken) {this.SVGaddWidth(this.base,info,scanW)}
      info.scanW += dw; info.W = info.scanW;
      info.index = []; if (better) {info.W = W; info.w = w; info.index = index}
      return better;
    },
    
    SVGmoveLine: function (start,end,svg,state,values) {
      var dx, data = this.SVGdata;
      //
      //  If this is the start, move the prescripts, if any.
      //
      if (start.length < 1) {
        this.scriptBox = this.SVGgetScripts(this.SVGdata.s);
        var presub = this.scriptBox[2], presup = this.scriptBox[3]; dx = svg.w + data.dx;
        if (presup) {svg.Add(presup,dx+data.delta-presup.w,data.u)}
        if (presub) {svg.Add(presub,dx-presub.w,-data.v)}
      }
      //
      //  Move the proper part of the base
      //
      if (this.data[this.base]) {
        if (start.length > 1) {
          this.data[this.base].SVGmoveSlice(start.slice(1),end.slice(1),svg,state,values,"paddingLeft");
        } else {
          if (end.length <= 1) {this.data[this.base].SVGmove(svg,state,values)}
            else {this.data[this.base].SVGmoveSlice([],end.slice(1),svg,state,values,"paddingRight")}
        }
      }
      //
      //  If this is the end, move the postscripts, if any.
      //
      if (end.length === 0) {
        var sub = this.scriptBox[0], sup = this.scriptBox[1]; dx = svg.w + data.s;
        if (sup) {svg.Add(sup,dx,data.u)}
        if (sub) {svg.Add(sub,dx-data.delta,-data.v)}
        delete this.scriptBox;
      }
    }

  });
  
  /**************************************************************************/

  MML.mo.Augment({
    //
    //  Override the method for checking line breaks to properly handle <mo>
    //
    SVGbetterBreak: function (info,state) {
      if (info.values && info.values.last === this) {return false}
      var values = this.getValues(
        "linebreak","linebreakstyle","lineleading","linebreakmultchar",
        "indentalign","indentshift",
        "indentalignfirst","indentshiftfirst",
        "indentalignlast","indentshiftlast",
        "texClass", "fence"
      );
      if (values.linebreakstyle === MML.LINEBREAKSTYLE.INFIXLINEBREAKSTYLE) 
        {values.linebreakstyle = this.Get("infixlinebreakstyle")}
      //
      //  Adjust nesting by TeX class (helps output that does not include
      //  mrows for nesting, but can leave these unbalanced.
      //
      if (values.texClass === MML.TEXCLASS.OPEN) {info.nest++}
      if (values.texClass === MML.TEXCLASS.CLOSE && info.nest) {info.nest--}
      //
      //  Get the default penalty for this location
      //
      var W = info.scanW, mo = info.embellished; delete info.embellished;
      if (!mo || !mo.SVGdata) {mo = this}
      var svg = mo.SVGdata, w = svg.w + svg.x;
      if (values.linebreakstyle === MML.LINEBREAKSTYLE.AFTER) {W += w; w = 0}
      if (W - info.shift === 0 && values.linebreak !== MML.LINEBREAK.NEWLINE)
        {return false} // don't break at zero width (FIXME?)
      var offset = SVG.linebreakWidth - W;
      // adjust offest for explicit first-line indent and align
      if (state.n === 0 && (values.indentshiftfirst !== state.VALUES.indentshiftfirst ||
          values.indentalignfirst !== state.VALUES.indentalignfirst)) {
        var align = this.SVGgetAlign(state,values),
            shift = this.SVGgetShift(state,values,align);
        offset += (info.shift - shift);
      }
      //
      var penalty = Math.floor(offset / SVG.linebreakWidth * 1000);
      if (penalty < 0) {penalty = PENALTY.toobig - 3*penalty}
      if (values.fence) {penalty += PENALTY.fence}
      if ((values.linebreakstyle === MML.LINEBREAKSTYLE.AFTER &&
          values.texClass === MML.TEXCLASS.OPEN) ||
          values.texClass === MML.TEXCLASS.CLOSE) {penalty += PENALTY.close}
      penalty += info.nest * PENALTY.nestfactor;
      //
      //  Get the penalty for this type of break and
      //    use it to modify the default penalty
      //
      var linebreak = PENALTY[values.linebreak||MML.LINEBREAK.AUTO];
      if (!MathJax.Object.isArray(linebreak)) {
        //  for breaks past the width, don't modify penalty
        if (offset >= 0) {penalty = linebreak * info.nest}
      } else {penalty = Math.max(1,penalty + linebreak[0] * info.nest)}
      //
      //  If the penalty is no better than the current one, return false
      //  Otherwise save the data for this breakpoint and return true
      //
      if (penalty >= info.penalty) {return false}
      info.penalty = penalty; info.values = values; info.W = W; info.w = w;
      values.lineleading = SVG.length2em(values.lineleading,1,state.VALUES.lineleading);
      values.last = this;
      return true;
    }
  });
  
  /**************************************************************************/

  MML.mspace.Augment({
    //
    //  Override the method for checking line breaks to properly handle <mspace>
    //
    SVGbetterBreak: function (info,state) {
      if (info.values && info.values.last === this) {return false}
      var values = this.getValues("linebreak");
      var linebreakValue = values.linebreak;
      if (!linebreakValue || this.hasDimAttr()) {
        // The MathML spec says that the linebreak attribute should be ignored
        // if any dimensional attribute is set.
        linebreakValue = MML.LINEBREAK.AUTO;
      }
      //
      //  Get the default penalty for this location
      //
      var W = info.scanW, svg = this.SVGdata, w = svg.w + svg.x;
      if (W - info.shift === 0) {return false} // don't break at zero width (FIXME?)
      var offset = SVG.linebreakWidth - W;
      //
      var penalty = Math.floor(offset / SVG.linebreakWidth * 1000);
      if (penalty < 0) {penalty = PENALTY.toobig - 3*penalty}
      penalty += info.nest * PENALTY.nestfactor;
      //
      //  Get the penalty for this type of break and
      //    use it to modify the default penalty
      //
      var linebreak = PENALTY[linebreakValue];
      if (linebreakValue === MML.LINEBREAK.AUTO && w >= PENALTY.spacelimit*1000 &&
          !this.mathbackground && !this.backrgound)
        {linebreak = [(w/1000+PENALTY.spaceoffset)*PENALTY.spacefactor]}
      if (!MathJax.Object.isArray(linebreak)) {
        //  for breaks past the width, don't modify penalty
        if (offset >= 0) {penalty = linebreak * info.nest}
      } else {penalty = Math.max(1,penalty + linebreak[0] * info.nest)}
      //
      //  If the penalty is no better than the current one, return false
      //  Otherwise save the data for this breakpoint and return true
      //
      if (penalty >= info.penalty) {return false}
      info.penalty = penalty; info.values = values; info.W = W; info.w = w;
      values.lineleading = state.VALUES.lineleading;
      values.linebreakstyle = "before"; values.last = this;
      return true;
    }
  });
  
  //
  //  Hook into the mathchoice extension
  //
  MathJax.Hub.Register.StartupHook("TeX mathchoice Ready",function () {
    MML.TeXmathchoice.Augment({
      SVGbetterBreak: function (info,state) {
        return this.Core().SVGbetterBreak(info,state);
      },
      SVGmoveLine: function (start,end,svg,state,values) {
        return this.Core().SVGmoveSlice(start,end,svg,state,values);
      }
    });
  });
  
  //
  //  Have maction process only the selected item
  //
  MML.maction.Augment({
    SVGbetterBreak: function (info,state) {
      return this.Core().SVGbetterBreak(info,state);
    },
    SVGmoveLine: function (start,end,svg,state,values) {
      return this.Core().SVGmoveSlice(start,end,svg,state,values);
    },
  });
  
  //
  //  Have semantics only do the first element
  //  (FIXME:  do we need to do anything special about annotation-xml?)
  //
  MML.semantics.Augment({
    SVGbetterBreak: function (info,state) {
      return (this.data[0] ? this.data[0].SVGbetterBreak(info,state) : false);
    },
    SVGmoveLine: function (start,end,svg,state,values) {
      return (this.data[0] ? this.data[0].SVGmoveSlice(start,end,svg,state,values) : null);
    }
  });
  
  /**************************************************************************/

  MathJax.Hub.Startup.signal.Post("SVG multiline Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/multiline.js");
  
});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/menclose.js
 *  
 *  Implements the SVG output for <menclose> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG,
      BBOX = SVG.BBOX;
      
  BBOX.ELLIPSE = BBOX.Subclass({
    type: "ellipse", removeable: false,
    Init: function (h,d,w,t,color,def) {
      if (def == null) {def = {}}; def.fill = "none";
      if (color) {def.stroke = color}
      def["stroke-width"] = t.toFixed(2).replace(/\.?0+$/,"");
      def.cx = Math.floor(w/2); def.cy = Math.floor((h+d)/2-d);
      def.rx = Math.floor((w-t)/2); def.ry = Math.floor((h+d-t)/2);
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.h = this.H = h;
      this.d = this.D = d; this.l = 0;
    }
  });
  
  BBOX.DLINE = BBOX.Subclass({
    type: "line", removeable: false,
    Init: function (h,d,w,t,color,updown,def) {
      if (def == null) {def = {}}; def.fill = "none";
      if (color) {def.stroke = color}
      def["stroke-width"] = t.toFixed(2).replace(/\.?0+$/,"");
      if (updown == "up") {
        def.x1 = Math.floor(t/2); def.y1 = Math.floor(t/2-d);
        def.x2 = Math.floor(w-t/2); def.y2 = Math.floor(h-t/2);
      } else {
        def.x1 = Math.floor(t/2); def.y1 = Math.floor(h-t/2);
        def.x2 = Math.floor(w-t/2); def.y2 = Math.floor(t/2-d);
      }
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.h = this.H = h;
      this.d = this.D = d; this.l = 0;
    }
  });
  
  BBOX.FPOLY = BBOX.Subclass({
    type: "polygon", removeable: false,
    Init: function (points,color,def) {
      if (def == null) {def = {}}
      if (color) {def.fill = color}
      var P = [], mx = 100000000, my = mx, Mx = -mx, My = Mx;
      for (var i = 0, m = points.length; i < m; i++) {
        var x = points[i][0], y = points[i][1];
        if (x > Mx) {Mx = x}; if (x < mx) {mx = x}
        if (y > My) {My = y}; if (y < my) {my = y}
        P.push(Math.floor(x)+","+Math.floor(y));
      }
      def.points = P.join(" ");
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = Mx; this.h = this.H = My;
      this.d = this.D = -my; this.l = -mx;
    }
  });

  BBOX.PPATH = BBOX.Subclass({
    type: "path", removeable: false,
    Init: function (h,d,w,p,t,color,def) {
      if (def == null) {def = {}}; def.fill = "none";
      if (color) {def.stroke = color}
      def["stroke-width"] = t.toFixed(2).replace(/\.?0+$/,"");
      def.d = p;
      this.SUPER(arguments).Init.call(this,def);
      this.w = this.r = w; this.h = this.H = h+d;
      this.d = this.D = this.l = 0; this.y = -d;
    }
  });
  
  MML.menclose.Augment({
    toSVG: function (HW,DD) {
      this.SVGgetStyles();

      var svg = this.SVG(), scale = this.SVGgetScale(svg);
      this.SVGhandleSpace(svg);
      var base = this.SVGdataStretched(0,HW,DD);

      var values = this.getValues("notation","thickness","padding","mathcolor","color");
      if (values.color && !this.mathcolor) {values.mathcolor = values.color}
      if (values.thickness == null) {values.thickness = ".075em"}
      if (values.padding == null)   {values.padding   = ".2em"}
      var mu = this.SVGgetMu(svg);
      var p = SVG.length2em(values.padding,mu,1/SVG.em) * scale;  // padding for enclosure
      var t = SVG.length2em(values.thickness,mu,1/SVG.em);        // thickness of lines
      t = Math.max(1/SVG.em,t);  // see issue #414
      var H = base.h+p+t, D = base.d+p+t, W = base.w+2*(p+t);
      var dx = 0, w, h, i, m, borders = [false,false,false,false];

      // perform some reduction e.g. eliminate duplicate notations.
      var nl = MathJax.Hub.SplitList(values.notation), notation = {};
      for (i = 0, m = nl.length; i < m; i++) notation[nl[i]] = true;
      if (notation[MML.NOTATION.UPDIAGONALARROW]) notation[MML.NOTATION.UPDIAGONALSTRIKE] = false;
      
      for (var n in notation) {
        if (!notation.hasOwnProperty(n) || !notation[n]) continue;
        switch (n) {
          case MML.NOTATION.BOX:
            borders = [true,true,true,true];
            break;

          case MML.NOTATION.ROUNDEDBOX:
            svg.Add(BBOX.FRAME(H,D,W,t,"solid",values.mathcolor,
                     {rx:Math.floor(Math.min(H+D-t,W-t)/4)}));
            break;
            
          case MML.NOTATION.CIRCLE:
            svg.Add(BBOX.ELLIPSE(H,D,W,t,values.mathcolor));
            break;

          case MML.NOTATION.ACTUARIAL:
            borders[0] = true;
          case MML.NOTATION.RIGHT:
            borders[1] = true;
            break;

          case MML.NOTATION.LEFT:
            borders[3] = true;
            break;
            
          case MML.NOTATION.TOP:
            borders[0] = true;
            break;
            
          case MML.NOTATION.BOTTOM:
            borders[2] = true;
            break;
            
          case MML.NOTATION.VERTICALSTRIKE:
            svg.Add(BBOX.VLINE(H+D,t,"solid",values.mathcolor),(W-t)/2,-D);
            break;
            
          case MML.NOTATION.HORIZONTALSTRIKE:
            svg.Add(BBOX.HLINE(W,t,"solid",values.mathcolor),0,(H+D-t)/2-D);
            break;

          case MML.NOTATION.UPDIAGONALSTRIKE:
            svg.Add(BBOX.DLINE(H,D,W,t,values.mathcolor,"up"));
            break;

          case MML.NOTATION.UPDIAGONALARROW:
              var l = Math.sqrt(W*W + (H+D)*(H+D)), f = 1/l * 10/SVG.em * t/.075;
              w = W * f; h = (H+D) * f; var x = .4*h;
              svg.Add(BBOX.DLINE(H-.5*h,D,W-.5*w,t,values.mathcolor,"up"));
              svg.Add(BBOX.FPOLY(
                [[x+w,h], [x-.4*h,.4*w], [x+.3*w,.3*h], [x+.4*h,-.4*w], [x+w,h]],
                values.mathcolor),W-w-x,H-h);
            break;

          case MML.NOTATION.DOWNDIAGONALSTRIKE:
            svg.Add(BBOX.DLINE(H,D,W,t,values.mathcolor,"down"));
            break;
            
          case MML.NOTATION.PHASORANGLE:
            borders[2] = true; W -= 2*p; p = (H+D)/2; W += p;
            svg.Add(BBOX.DLINE(H,D,p,t,values.mathcolor,"up"));
            break;

          case MML.NOTATION.MADRUWB:
            borders[1] = borders[2] = true;
            break;

          case MML.NOTATION.RADICAL:
            svg.Add(BBOX.PPATH(H,D,W,
              "M "+this.SVGxy(t/2,.4*(H+D)) +
              " L "+this.SVGxy(p,t/2) +
              " L "+this.SVGxy(2*p,H+D-t/2) +
              " L "+this.SVGxy(W,H+D-t/2),
              t,values.mathcolor),0,t);
            dx = p;
            break;
            
          case MML.NOTATION.LONGDIV:
            svg.Add(BBOX.PPATH(H,D,W,
              "M "+this.SVGxy(t/2,t/2) +
              " a "+this.SVGxy(p,(H+D)/2-2*t) + " 0 0,1 " + this.SVGxy(t/2,H+D-t) +
              " L "+this.SVGxy(W,H+D-t/2),
              t,values.mathcolor),0,t/2);
            dx = p;
            break;
        }
      }
      var sides = [["H",W,0,H-t],["V",H+D,W-t,-D],["H",W,0,-D],["V",H+D,0,-D]];
      for (i = 0; i < 4; i++) {
        if (borders[i]) {
          var side = sides[i];
          svg.Add(BBOX[side[0]+"LINE"](side[1],t,"solid",values.mathcolor),side[2],side[3]);
        }
      }
      svg.Add(base,dx+p+t,0,false,true);
      svg.Clean();
      this.SVGhandleSpace(svg);
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    },
    
    SVGxy: function (x,y) {return Math.floor(x)+","+Math.floor(y)}
    
  });
  
  MathJax.Hub.Startup.signal.Post("SVG menclose Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/menclose.js");
  
});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/autoload/ms.js
 *  
 *  Implements the SVG output for <ms> elements.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {
  var VERSION = "2.7.2";
  var MML = MathJax.ElementJax.mml,
      SVG = MathJax.OutputJax.SVG;
  
  MML.ms.Augment({
    toSVG: function () {
      this.SVGgetStyles();
      var svg = this.SVG(); this.SVGhandleSpace(svg);
      var values = this.getValues("lquote","rquote","mathvariant");
      if (!this.hasValue("lquote") || values.lquote === '"') values.lquote = "\u201C";
      if (!this.hasValue("rquote") || values.rquote === '"') values.rquote = "\u201D";
      if (values.lquote === "\u201C" && values.mathvariant === "monospace") values.lquote = '"';
      if (values.rquote === "\u201D" && values.mathvariant === "monospace") values.rquote = '"';
      var variant = this.SVGgetVariant(), scale = this.SVGgetScale();
      var text = values.lquote+this.data.join("")+values.rquote;  // FIXME:  handle mglyph?
      svg.Add(this.SVGhandleVariant(variant,scale,text));
      svg.Clean();
      this.SVGhandleColor(svg);
      this.SVGsaveData(svg);
      return svg;
    }
  });
  
  MathJax.Hub.Startup.signal.Post("SVG ms Ready");
  MathJax.Ajax.loadComplete(SVG.autoloadDir+"/ms.js");

});


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/fonts/TeX/fontdata.js
 *  
 *  Initializes the SVG OutputJax to use the MathJax TeX fonts
 *  for displaying mathematics.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (SVG,MML,AJAX,HUB) {
  var VERSION = "2.7.2";
  
  var MAIN   = "MathJax_Main",
      BOLD   = "MathJax_Main-bold",
      ITALIC = "MathJax_Math-italic",
      AMS    = "MathJax_AMS",
      SIZE1  = "MathJax_Size1",
      SIZE2  = "MathJax_Size2",
      SIZE3  = "MathJax_Size3",
      SIZE4  = "MathJax_Size4";
  var H = "H", V = "V", EXTRAH = {load:"extra", dir:H}, EXTRAV = {load:"extra", dir:V};
  var STDHW = [[1000,MAIN],[1200,SIZE1],[1800,SIZE2],[2400,SIZE3],[3000,SIZE4]];
  var ARROWREP = [0x2212,MAIN,0,0,0,-.31,-.31];  // add depth for arrow extender
  var DARROWREP = [0x3D,MAIN,0,0,0,0,.1];        // add depth for arrow extender

  SVG.Augment({
    FONTDATA: {
      version: VERSION,
      
      baselineskip: 1200,
      lineH: 800, lineD: 200,
      
      FONTS: {
        "MathJax_Main":             "Main/Regular/Main.js",
        "MathJax_Main-bold":        "Main/Bold/Main.js",
        "MathJax_Main-italic":      "Main/Italic/Main.js",
        "MathJax_Math-italic":      "Math/Italic/Main.js",
        "MathJax_Math-bold-italic": "Math/BoldItalic/Main.js",
        "MathJax_Caligraphic":      "Caligraphic/Regular/Main.js",
        "MathJax_Size1":            "Size1/Regular/Main.js",
        "MathJax_Size2":            "Size2/Regular/Main.js",
        "MathJax_Size3":            "Size3/Regular/Main.js",
        "MathJax_Size4":            "Size4/Regular/Main.js",
        "MathJax_AMS":              "AMS/Regular/Main.js",
        "MathJax_Fraktur":          "Fraktur/Regular/Main.js",
        "MathJax_Fraktur-bold":     "Fraktur/Bold/Main.js",
        "MathJax_SansSerif":        "SansSerif/Regular/Main.js",
        "MathJax_SansSerif-bold":   "SansSerif/Bold/Main.js",
        "MathJax_SansSerif-italic": "SansSerif/Italic/Main.js",
        "MathJax_Script":           "Script/Regular/Main.js",
        "MathJax_Typewriter":       "Typewriter/Regular/Main.js",
        "MathJax_Caligraphic-bold": "Caligraphic/Bold/Main.js"
      },
      
      VARIANT: {
        "normal": {fonts:[MAIN,SIZE1,AMS],
                   offsetG: 0x03B1, variantG: "italic",
                   remap: {0x391:0x41, 0x392:0x42, 0x395:0x45, 0x396:0x5A, 0x397:0x48,
                           0x399:0x49, 0x39A:0x4B, 0x39C:0x4D, 0x39D:0x4E, 0x39F:0x4F,
                           0x3A1:0x50, 0x3A4:0x54, 0x3A7:0x58,
                           0x2016:0x2225,
                           0x2216:[0x2216,"-TeX-variant"],  // \smallsetminus
                           0x210F:[0x210F,"-TeX-variant"],  // \hbar
                           0x2032:[0x27,"sans-serif-italic"],  // HACK: a smaller prime
                           0x29F8:[0x002F,MML.VARIANT.ITALIC]}},
        "bold":   {fonts:[BOLD,SIZE1,AMS], bold:true,
                   offsetG: 0x03B1, variantG: "bold-italic",
                   remap: {0x391:0x41, 0x392:0x42, 0x395:0x45, 0x396:0x5A, 0x397:0x48,
                           0x399:0x49, 0x39A:0x4B, 0x39C:0x4D, 0x39D:0x4E, 0x39F:0x4F,
                           0x3A1:0x50, 0x3A4:0x54, 0x3A7:0x58, 0x29F8:[0x002F,"bold-italic"],
                           0x2016:0x2225,
                           0x219A:"\u2190\u0338", 0x219B:"\u2192\u0338", 0x21AE:"\u2194\u0338",
                           0x21CD:"\u21D0\u0338", 0x21CE:"\u21D4\u0338", 0x21CF:"\u21D2\u0338",
                           0x2204:"\u2203\u0338", 0x2224:"\u2223\u0338", 0x2226:"\u2225\u0338",
                           0x2241:"\u223C\u0338", 0x2247:"\u2245\u0338", 
                           0x226E:"<\u0338", 0x226F:">\u0338",
                           0x2270:"\u2264\u0338", 0x2271:"\u2265\u0338",
                           0x2280:"\u227A\u0338", 0x2281:"\u227B\u0338",
                           0x2288:"\u2286\u0338", 0x2289:"\u2287\u0338",
                           0x22AC:"\u22A2\u0338", 0x22AD:"\u22A8\u0338",
//                         0x22AE:"\u22A9\u0338", 0x22AF:"\u22AB\u0338",
                           0x22E0:"\u227C\u0338", 0x22E1:"\u227D\u0338"//,
//                         0x22EA:"\u22B2\u0338", 0x22EB:"\u22B3\u0338",
//                         0x22EC:"\u22B4\u0338", 0x22ED:"\u22B5\u0338"
                  }},
        "italic": {fonts:[ITALIC,"MathJax_Main-italic",MAIN,SIZE1,AMS], italic:true,
                   remap: {0x391:0x41, 0x392:0x42, 0x395:0x45, 0x396:0x5A, 0x397:0x48,
                           0x399:0x49, 0x39A:0x4B, 0x39C:0x4D, 0x39D:0x4E, 0x39F:0x4F,
                           0x3A1:0x50, 0x3A4:0x54, 0x3A7:0x58}},
        "bold-italic": {fonts:["MathJax_Math-bold-italic",BOLD,SIZE1,AMS], bold:true, italic:true,
                   remap: {0x391:0x41, 0x392:0x42, 0x395:0x45, 0x396:0x5A, 0x397:0x48,
                           0x399:0x49, 0x39A:0x4B, 0x39C:0x4D, 0x39D:0x4E, 0x39F:0x4F,
                           0x3A1:0x50, 0x3A4:0x54, 0x3A7:0x58}},
        "double-struck": {fonts:[AMS, MAIN]},
        "fraktur": {fonts:["MathJax_Fraktur",MAIN,SIZE1,AMS]},
        "bold-fraktur": {fonts:["MathJax_Fraktur-bold",BOLD,SIZE1,AMS], bold:true},
        "script": {fonts:["MathJax_Script",MAIN,SIZE1,AMS]},
        "bold-script": {fonts:["MathJax_Script",BOLD,SIZE1,AMS], bold:true},
        "sans-serif": {fonts:["MathJax_SansSerif",MAIN,SIZE1,AMS]},
        "bold-sans-serif": {fonts:["MathJax_SansSerif-bold",BOLD,SIZE1,AMS], bold:true},
        "sans-serif-italic": {fonts:["MathJax_SansSerif-italic","MathJax_Main-italic",SIZE1,AMS], italic:true},
        "sans-serif-bold-italic": {fonts:["MathJax_SansSerif-italic","MathJax_Main-italic",SIZE1,AMS], bold:true, italic:true},
        "monospace": {fonts:["MathJax_Typewriter",MAIN,SIZE1,AMS]},
        "-tex-caligraphic": {fonts:["MathJax_Caligraphic",MAIN], offsetA: 0x41, variantA: "italic"},
        "-tex-oldstyle": {fonts:["MathJax_Caligraphic",MAIN]},
        "-tex-mathit": {fonts:["MathJax_Main-italic",ITALIC,MAIN,SIZE1,AMS], italic:true, noIC: true,
                   remap: {0x391:0x41, 0x392:0x42, 0x395:0x45, 0x396:0x5A, 0x397:0x48,
                           0x399:0x49, 0x39A:0x4B, 0x39C:0x4D, 0x39D:0x4E, 0x39F:0x4F,
                           0x3A1:0x50, 0x3A4:0x54, 0x3A7:0x58}},
        "-TeX-variant": {fonts:[AMS,MAIN,SIZE1],   // HACK: to get larger prime for \prime
                   remap: {
                     0x2268: 0xE00C, 0x2269: 0xE00D, 0x2270: 0xE011, 0x2271: 0xE00E,
                     0x2A87: 0xE010, 0x2A88: 0xE00F, 0x2224: 0xE006, 0x2226: 0xE007,
                     0x2288: 0xE016, 0x2289: 0xE018, 0x228A: 0xE01A, 0x228B: 0xE01B,
                     0x2ACB: 0xE017, 0x2ACC: 0xE019, 0x03DC: 0xE008, 0x03F0: 0xE009,
                     0x2216:[0x2216,MML.VARIANT.NORMAL], // \setminus
                     0x210F:[0x210F,MML.VARIANT.NORMAL]  // \hslash
                   }},
        "-largeOp": {fonts:[SIZE2,SIZE1,MAIN]},
        "-smallOp": {fonts:[SIZE1,MAIN]},
        "-tex-caligraphic-bold": {fonts:["MathJax_Caligraphic-bold","MathJax_Main-bold","MathJax_Main","MathJax_Math","MathJax_Size1"], bold:true,
                                  offsetA: 0x41, variantA: "bold-italic"},
        "-tex-oldstyle-bold": {fonts:["MathJax_Caligraphic-bold","MathJax_Main-bold","MathJax_Main","MathJax_Math","MathJax_Size1"], bold:true}
      },
      
      RANGES: [
        {name: "alpha", low: 0x61, high: 0x7A, offset: "A", add: 32},
        {name: "number", low: 0x30, high: 0x39, offset: "N"},
        {name: "greek", low: 0x03B1, high: 0x03F6, offset: "G"}
      ],
      
      RULECHAR: 0x2212,
      
      REMAP: {
        0xA: 0x20,                      // newline
        0x00A0: 0x20,                   // non-breaking space
        0x203E: 0x2C9,                  // overline
        0x20D0: 0x21BC, 0x20D1: 0x21C0, // combining left and right harpoons
        0x20D6: 0x2190, 0x20E1: 0x2194, // combining left arrow and lef-right arrow
        0x20EC: 0x21C1, 0x20ED: 0x21BD, // combining low right and left harpoons
        0x20EE: 0x2190, 0x20EF: 0x2192, // combining low left and right arrows
        0x20F0: 0x2A,                   // combining asterisk
        0xFE37: 0x23DE, 0xFE38: 0x23DF, // OverBrace, UnderBrace

        0xB7: 0x22C5,                   // center dot
        0x2B9: 0x2032,                  // prime,
        0x3D2: 0x3A5,                   // Upsilon
        0x2206: 0x394,                  // increment
        0x2015: 0x2014, 0x2017: 0x5F,   // horizontal bars
        0x2022: 0x2219, 0x2044: 0x2F,   // bullet, fraction slash
        0x2305: 0x22BC, 0x2306: 0x2A5E, // barwedge, doublebarwedge
        0x25AA: 0x25A0, 0x25B4: 0x25B2, // blacksquare, blacktriangle
        0x25B5: 0x25B3, 0x25B8: 0x25B6, // triangle, blacktriangleright
        0x25BE: 0x25BC, 0x25BF: 0x25BD, // blacktriangledown, triangledown
        0x25C2: 0x25C0,                 // blacktriangleleft
        0x2329: 0x27E8, 0x232A: 0x27E9, // langle, rangle
        0x3008: 0x27E8, 0x3009: 0x27E9, // langle, rangle
        0x2758: 0x2223,                 // VerticalSeparator
        0x2A2F: 0xD7,                   // cross product

        0x25FB: 0x25A1, 0x25FC: 0x25A0, // square, blacksquare

        //
        //  Letter-like symbols (that appear elsewhere)
        //
        0x2102: [0x0043,MML.VARIANT.DOUBLESTRUCK],
//      0x210A: [0x0067,MML.VARIANT.SCRIPT],
        0x210B: [0x0048,MML.VARIANT.SCRIPT],
        0x210C: [0x0048,MML.VARIANT.FRAKTUR],
        0x210D: [0x0048,MML.VARIANT.DOUBLESTRUCK],
        0x210E: [0x0068,MML.VARIANT.ITALIC],
        0x2110: [0x004A,MML.VARIANT.SCRIPT],
        0x2111: [0x0049,MML.VARIANT.FRAKTUR],
        0x2112: [0x004C,MML.VARIANT.SCRIPT],
        0x2115: [0x004E,MML.VARIANT.DOUBLESTRUCK],
        0x2119: [0x0050,MML.VARIANT.DOUBLESTRUCK],
        0x211A: [0x0051,MML.VARIANT.DOUBLESTRUCK],
        0x211B: [0x0052,MML.VARIANT.SCRIPT],
        0x211C: [0x0052,MML.VARIANT.FRAKTUR],
        0x211D: [0x0052,MML.VARIANT.DOUBLESTRUCK],
        0x2124: [0x005A,MML.VARIANT.DOUBLESTRUCK],
        0x2126: [0x03A9,MML.VARIANT.NORMAL],
        0x2128: [0x005A,MML.VARIANT.FRAKTUR],
        0x212C: [0x0042,MML.VARIANT.SCRIPT],
        0x212D: [0x0043,MML.VARIANT.FRAKTUR],
//      0x212F: [0x0065,MML.VARIANT.SCRIPT],
        0x2130: [0x0045,MML.VARIANT.SCRIPT],
        0x2131: [0x0046,MML.VARIANT.SCRIPT],
        0x2133: [0x004D,MML.VARIANT.SCRIPT],
//      0x2134: [0x006F,MML.VARIANT.SCRIPT],

        0x2247: 0x2246,                 // wrong placement of this character
        0x231C: 0x250C, 0x231D:0x2510,  // wrong placement of \ulcorner, \urcorner
        0x231E: 0x2514, 0x231F:0x2518,  // wrong placement of \llcorner, \lrcorner

        //
        //  compound symbols not in these fonts
        //  
        0x2204: "\u2203\u0338",    // \not\exists
        0x220C: "\u220B\u0338",    // \not\ni
        0x2244: "\u2243\u0338",    // \not\simeq
        0x2249: "\u2248\u0338",    // \not\approx
        0x2262: "\u2261\u0338",    // \not\equiv
        0x226D: "\u224D\u0338",    // \not\asymp
        0x2274: "\u2272\u0338",    // \not\lesssim
        0x2275: "\u2273\u0338",    // \not\gtrsim
        0x2278: "\u2276\u0338",    // \not\lessgtr
        0x2279: "\u2277\u0338",    // \not\gtrless
        0x2284: "\u2282\u0338",    // \not\subset
        0x2285: "\u2283\u0338",    // \not\supset
        0x22E2: "\u2291\u0338",    // \not\sqsubseteq
        0x22E3: "\u2292\u0338",    // \not\sqsupseteq

        0x2A0C: "\u222C\u222C",    // quadruple integral

        0x2033: "\u2032\u2032",        // double prime
        0x2034: "\u2032\u2032\u2032",  // triple prime
        0x2036: "\u2035\u2035",        // double back prime
        0x2037: "\u2035\u2035\u2035",  // trile back prime
        0x2057: "\u2032\u2032\u2032\u2032",  // quadruple prime
        0x20DB: "...",                 // combining three dots above (only works with mover/under)
        0x20DC: "...."                 // combining four dots above (only works with mover/under)
      },
      
      REMAPACCENT: {
        "\u2192":"\u20D7",
        "\u2032":"'",
        "\u2035":"`"
      },
      REMAPACCENTUNDER: {
      },
      
      PLANE1MAP: [
        [0x1D400,0x1D419, 0x41, MML.VARIANT.BOLD],
        [0x1D41A,0x1D433, 0x61, MML.VARIANT.BOLD],
        [0x1D434,0x1D44D, 0x41, MML.VARIANT.ITALIC],
        [0x1D44E,0x1D467, 0x61, MML.VARIANT.ITALIC],
        [0x1D468,0x1D481, 0x41, MML.VARIANT.BOLDITALIC],
        [0x1D482,0x1D49B, 0x61, MML.VARIANT.BOLDITALIC],
        [0x1D49C,0x1D4B5, 0x41, MML.VARIANT.SCRIPT],
//      [0x1D4B6,0x1D4CF, 0x61, MML.VARIANT.SCRIPT],
//      [0x1D4D0,0x1D4E9, 0x41, MML.VARIANT.BOLDSCRIPT],
//      [0x1D4EA,0x1D503, 0x61, MML.VARIANT.BOLDSCRIPT],
        [0x1D504,0x1D51D, 0x41, MML.VARIANT.FRAKTUR],
        [0x1D51E,0x1D537, 0x61, MML.VARIANT.FRAKTUR],
        [0x1D538,0x1D551, 0x41, MML.VARIANT.DOUBLESTRUCK],
//      [0x1D552,0x1D56B, 0x61, MML.VARIANT.DOUBLESTRUCK],
        [0x1D56C,0x1D585, 0x41, MML.VARIANT.BOLDFRAKTUR],
        [0x1D586,0x1D59F, 0x61, MML.VARIANT.BOLDFRAKTUR],
        [0x1D5A0,0x1D5B9, 0x41, MML.VARIANT.SANSSERIF],
        [0x1D5BA,0x1D5D3, 0x61, MML.VARIANT.SANSSERIF],
        [0x1D5D4,0x1D5ED, 0x41, MML.VARIANT.BOLDSANSSERIF],
        [0x1D5EE,0x1D607, 0x61, MML.VARIANT.BOLDSANSSERIF],
        [0x1D608,0x1D621, 0x41, MML.VARIANT.SANSSERIFITALIC],
        [0x1D622,0x1D63B, 0x61, MML.VARIANT.SANSSERIFITALIC],
//      [0x1D63C,0x1D655, 0x41, MML.VARIANT.SANSSERIFBOLDITALIC],
//      [0x1D656,0x1D66F, 0x61, MML.VARIANT.SANSSERIFBOLDITALIC],
        [0x1D670,0x1D689, 0x41, MML.VARIANT.MONOSPACE],
        [0x1D68A,0x1D6A3, 0x61, MML.VARIANT.MONOSPACE],
        
        [0x1D6A8,0x1D6C1, 0x391, MML.VARIANT.BOLD],
//      [0x1D6C2,0x1D6E1, 0x3B1, MML.VARIANT.BOLD],
        [0x1D6E2,0x1D6FA, 0x391, MML.VARIANT.ITALIC],
        [0x1D6FC,0x1D71B, 0x3B1, MML.VARIANT.ITALIC],
        [0x1D71C,0x1D734, 0x391, MML.VARIANT.BOLDITALIC],
        [0x1D736,0x1D755, 0x3B1, MML.VARIANT.BOLDITALIC],
        [0x1D756,0x1D76E, 0x391, MML.VARIANT.BOLDSANSSERIF],
//      [0x1D770,0x1D78F, 0x3B1, MML.VARIANT.BOLDSANSSERIF],
        [0x1D790,0x1D7A8, 0x391, MML.VARIANT.SANSSERIFBOLDITALIC],
//      [0x1D7AA,0x1D7C9, 0x3B1, MML.VARIANT.SANSSERIFBOLDITALIC],
        
        [0x1D7CE,0x1D7D7, 0x30, MML.VARIANT.BOLD],
//      [0x1D7D8,0x1D7E1, 0x30, MML.VARIANT.DOUBLESTRUCK],
        [0x1D7E2,0x1D7EB, 0x30, MML.VARIANT.SANSSERIF],
        [0x1D7EC,0x1D7F5, 0x30, MML.VARIANT.BOLDSANSSERIF],
        [0x1D7F6,0x1D7FF, 0x30, MML.VARIANT.MONOSPACE]
      ],

      REMAPGREEK: {
        0x391: 0x41, 0x392: 0x42, 0x395: 0x45, 0x396: 0x5A,
        0x397: 0x48, 0x399: 0x49, 0x39A: 0x4B, 0x39C: 0x4D,
        0x39D: 0x4E, 0x39F: 0x4F, 0x3A1: 0x50, 0x3A2: 0x398,
        0x3A4: 0x54, 0x3A7: 0x58, 0x3AA: 0x2207,
        0x3CA: 0x2202, 0x3CB: 0x3F5, 0x3CC: 0x3D1, 0x3CD: 0x3F0,
        0x3CE: 0x3D5, 0x3CF: 0x3F1, 0x3D0: 0x3D6
      },
      
      RemapPlane1: function (n,variant) {
        for (var i = 0, m = this.PLANE1MAP.length; i < m; i++) {
          if (n < this.PLANE1MAP[i][0]) break;
          if (n <= this.PLANE1MAP[i][1]) {
            n = n - this.PLANE1MAP[i][0] + this.PLANE1MAP[i][2];
            if (this.REMAPGREEK[n]) {n = this.REMAPGREEK[n]}
            variant = this.VARIANT[this.PLANE1MAP[i][3]];
            break;
          }
        }
        return {n: n, variant: variant};
      },
      
      DELIMITERS: {
        0x0028: // (
        {
          dir: V, HW: STDHW,
          stretch: {top: [0x239B,SIZE4], ext: [0x239C,SIZE4], bot: [0x239D,SIZE4]}
        },
        0x0029: // )
        {
          dir: V, HW: STDHW,
          stretch: {top:[0x239E,SIZE4], ext:[0x239F,SIZE4], bot:[0x23A0,SIZE4]}
        },
        0x002F: // /
        {
          dir: V, HW: STDHW
        },
        0x005B: // [
        {
          dir: V, HW: STDHW,
          stretch: {top:[0x23A1,SIZE4], ext:[0x23A2,SIZE4], bot:[0x23A3,SIZE4]}
        },
        0x005C: // \
        {
          dir: V, HW: STDHW
        },
        0x005D: // ]
        {
          dir: V, HW: STDHW,
          stretch: {top:[0x23A4,SIZE4], ext:[0x23A5,SIZE4], bot:[0x23A6,SIZE4]}
        },
        0x007B: // {
        {
          dir: V, HW: STDHW,
          stretch: {top:[0x23A7,SIZE4], mid:[0x23A8,SIZE4], bot:[0x23A9,SIZE4], ext:[0x23AA,SIZE4]}
        },
        0x007C: // |
        {
          dir: V, HW: [[1000,MAIN]], stretch: {ext:[0x2223,MAIN]}
        },
        0x007D: // }
        {
          dir: V, HW: STDHW,
          stretch: {top: [0x23AB,SIZE4], mid:[0x23AC,SIZE4], bot: [0x23AD,SIZE4], ext: [0x23AA,SIZE4]}
        },
        0x00AF: // macron
        {
          dir: H, HW: [[.59,MAIN]], stretch: {rep:[0xAF,MAIN]}
        },
        0x02C6: // wide hat
        {
          dir: H, HW: [[267+250,MAIN],[567+250,SIZE1],[1005+330,SIZE2],[1447+330,SIZE3],[1909,SIZE4]]
        },
        0x02DC: // wide tilde
        {
          dir: H, HW: [[333+250,MAIN],[555+250,SIZE1],[1000+330,SIZE2],[1443+330,SIZE3],[1887,SIZE4]]
        },
        0x2013: // en-dash
        {
          dir: H, HW: [[500,MAIN]], stretch: {rep:[0x2013,MAIN]}
        },
        0x2016: // vertical arrow extension
        {
          dir: V, HW: [[602,SIZE1],[1000,MAIN,null,0x2225]], stretch: {ext:[0x2225,MAIN]}
        },
        0x2190: // left arrow
        {
          dir: H, HW: [[1000,MAIN]], stretch: {left:[0x2190,MAIN], rep:ARROWREP, fuzz:300}
        },
        0x2191: // \uparrow
        {
          dir: V, HW: [[888,MAIN]], stretch: {top:[0x2191,SIZE1], ext:[0x23D0,SIZE1]}
        },
        0x2192: // right arrow
        {
          dir: H, HW: [[1000,MAIN]], stretch: {rep:ARROWREP, right:[0x2192,MAIN], fuzz:300}
        },
        0x2193: // \downarrow
        {
          dir: V, HW: [[888,MAIN]], stretch: {ext:[0x23D0,SIZE1], bot:[0x2193,SIZE1]}
        },
        0x2194: // left-right arrow
        {
          dir: H, HW: [[1000,MAIN]],
          stretch: {left:[0x2190,MAIN], rep:ARROWREP, right:[0x2192,MAIN], fuzz:300}
        },
        0x2195: // \updownarrow
        {
          dir: V, HW: [[1044,MAIN]],
          stretch: {top:[0x2191,SIZE1], ext:[0x23D0,SIZE1], bot:[0x2193,SIZE1]}
        },
        0x21D0: // left double arrow
        {
          dir: H, HW: [[1000,MAIN]], stretch: {left:[0x21D0,MAIN], rep:DARROWREP, fuzz:300}
        },
        0x21D1: // \Uparrow
        {
          dir: V, HW: [[888,MAIN]], stretch: {top:[0x21D1,SIZE1], ext:[0x2016,SIZE1]}
        },
        0x21D2: // right double arrow
        {
          dir: H, HW: [[1000,MAIN]], stretch: {rep:DARROWREP, right:[0x21D2,MAIN], fuzz:300}
        },
        0x21D3: // \Downarrow
        {
          dir: V, HW: [[888,MAIN]], stretch: {ext:[0x2016,SIZE1], bot:[0x21D3,SIZE1]}
        },
        0x21D4: // left-right double arrow
        {
          dir: H, HW: [[1000,MAIN]],
          stretch: {left:[0x21D0,MAIN], rep:DARROWREP, right:[0x21D2,MAIN], fuzz:300}
        },
        0x21D5: // \Updownarrow
        {
          dir: V, HW: [[1044,MAIN]],
          stretch: {top:[0x21D1,SIZE1], ext:[0x2016,SIZE1], bot:[0x21D3,SIZE1]}
        },
        0x2212: // horizontal line
        {
          dir: H, HW: [[.5,MAIN,0,0x2013]], stretch: {rep:ARROWREP, fuzz:300}
         },
        0x221A: // \surd
        {
          dir: V, HW: STDHW,
          stretch: {top:[0xE001,SIZE4], ext:[0xE000,SIZE4], bot:[0x23B7,SIZE4], fullExtenders:true}
        },
        0x2223: // \vert
        {
          dir: V, HW: [[1000,MAIN]], stretch: {ext:[0x2223,MAIN]}
        },
        0x2225: // \Vert
        {
          dir: V, HW: [[1000,MAIN]], stretch: {ext:[0x2225,MAIN]}
        },
        0x2308: // \lceil
        {
          dir: V, HW: STDHW, stretch: {top:[0x23A1,SIZE4], ext:[0x23A2,SIZE4]}
        },
        0x2309: // \rceil
        {
          dir: V, HW: STDHW, stretch: {top:[0x23A4,SIZE4], ext:[0x23A5,SIZE4]}
        },
        0x230A: // \lfloor
        {
          dir: V, HW: STDHW, stretch: {ext:[0x23A2,SIZE4], bot:[0x23A3,SIZE4]}
        },
        0x230B: // \rfloor
        {
          dir: V, HW: STDHW, stretch: {ext:[0x23A5,SIZE4], bot:[0x23A6,SIZE4]}
        },
        0x23AA: // \bracevert
        {
          dir: V, HW: [[320,SIZE4]],
          stretch: {top:[0x23AA,SIZE4], ext:[0x23AA,SIZE4], bot:[0x23AA,SIZE4]}
        },
        0x23B0: // \lmoustache
        {
          dir: V, HW: [[989,MAIN]],
          stretch: {top:[0x23A7,SIZE4], ext:[0x23AA,SIZE4], bot:[0x23AD,SIZE4]}
        },
        0x23B1: // \rmoustache
        {
          dir: V, HW: [[989,MAIN]],
          stretch: {top:[0x23AB,SIZE4], ext:[0x23AA,SIZE4], bot:[0x23A9,SIZE4]}
        },
        0x23D0: // vertical line extension
        {
          dir: V, HW: [[602,SIZE1],[1000,MAIN,null,0x2223]], stretch: {ext:[0x2223,MAIN]}
        },
        0x23DE: // horizontal brace down
        {
          dir: H, HW: [],
          stretch: {min:.9, left:[0xE150,SIZE4], mid:[[0xE153,0xE152],SIZE4], right:[0xE151,SIZE4], rep:[0xE154,SIZE4]}
        },
        0x23DF: // horizontal brace up
        {
          dir: H, HW: [],
          stretch: {min:.9, left:[0xE152,SIZE4], mid:[[0xE151,0xE150],SIZE4], right:[0xE153,SIZE4], rep:[0xE154,SIZE4]}
        },
        0x27E8: // \langle
        {
          dir: V, HW: STDHW
        },
        0x27E9: // \rangle
        {
          dir: V, HW: STDHW
        },
        0x27EE: // \lgroup
        {
          dir: V, HW: [[989,MAIN]],
          stretch: {top:[0x23A7,SIZE4], ext:[0x23AA,SIZE4], bot:[0x23A9,SIZE4]}
        },
        0x27EF: // \rgroup
        {
          dir: V, HW: [[989,MAIN]],
          stretch: {top:[0x23AB,SIZE4], ext:[0x23AA,SIZE4], bot:[0x23AD,SIZE4]}
        },
        0x002D: {alias: 0x2212, dir:H}, // minus
        0x005E: {alias: 0x02C6, dir:H}, // wide hat
        0x005F: {alias: 0x2013, dir:H}, // low line
        0x007E: {alias: 0x02DC, dir:H}, // wide tilde
        0x02C9: {alias: 0x00AF, dir:H}, // macron
        0x0302: {alias: 0x02C6, dir:H}, // wide hat
        0x0303: {alias: 0x02DC, dir:H}, // wide tilde
        0x030C: {alias: 0x02C7, dir:H}, // wide caron
        0x0332: {alias: 0x2013, dir:H}, // combining low line
        0x2014: {alias: 0x2013, dir:H}, // em-dash
        0x2015: {alias: 0x2013, dir:H}, // horizontal line
        0x2017: {alias: 0x2013, dir:H}, // horizontal line
        0x203E: {alias: 0x00AF, dir:H}, // over line
        0x20D7: {alias: 0x2192, dir:H}, // combining over right arrow (vector arrow)
        0x2215: {alias: 0x002F, dir:V}, // division slash
        0x2329: {alias: 0x27E8, dir:V}, // langle
        0x232A: {alias: 0x27E9, dir:V}, // rangle
        0x23AF: {alias: 0x2013, dir:H}, // horizontal line extension
        0x2500: {alias: 0x2013, dir:H}, // horizontal line
        0x2758: {alias: 0x2223, dir:V}, // vertical separator
        0x3008: {alias: 0x27E8, dir:V}, // langle
        0x3009: {alias: 0x27E9, dir:V}, // rangle
        0xFE37: {alias: 0x23DE, dir:H}, // horizontal brace down
        0xFE38: {alias: 0x23DF, dir:H},  // horizontal brace up

        0x003D: EXTRAH, // equal sign
        0x219E: EXTRAH, // left two-headed arrow
        0x21A0: EXTRAH, // right two-headed arrow
        0x21A4: EXTRAH, // left arrow from bar
        0x21A5: EXTRAV, // up arrow from bar
        0x21A6: EXTRAH, // right arrow from bar
        0x21A7: EXTRAV, // down arrow from bar
        0x21B0: EXTRAV, // up arrow with top leftwards
        0x21B1: EXTRAV, // up arrow with top right
        0x21BC: EXTRAH, // left harpoon with barb up
        0x21BD: EXTRAH, // left harpoon with barb down
        0x21BE: EXTRAV, // up harpoon with barb right
        0x21BF: EXTRAV, // up harpoon with barb left
        0x21C0: EXTRAH, // right harpoon with barb up
        0x21C1: EXTRAH, // right harpoon with barb down
        0x21C2: EXTRAV, // down harpoon with barb right
        0x21C3: EXTRAV, // down harpoon with barb left
        0x21DA: EXTRAH, // left triple arrow
        0x21DB: EXTRAH, // right triple arrow
        0x23B4: EXTRAH, // top square bracket
        0x23B5: EXTRAH, // bottom square bracket
        0x23DC: EXTRAH, // top paren
        0x23DD: EXTRAH, // bottom paren
        0x23E0: EXTRAH, // top tortoise shell
        0x23E1: EXTRAH, // bottom tortoise shell
        0x2906: EXTRAH, // leftwards double arrow from bar
        0x2907: EXTRAH, // rightwards double arrow from bar
        0x294E: EXTRAH, // left barb up right barb up harpoon
        0x294F: EXTRAV, // up barb right down barb right harpoon
        0x2950: EXTRAH, // left barb dow right barb down harpoon
        0x2951: EXTRAV, // up barb left down barb left harpoon
        0x295A: EXTRAH, // leftwards harpoon with barb up from bar
        0x295B: EXTRAH, // rightwards harpoon with barb up from bar
        0x295C: EXTRAV, // up harpoon with barb right from bar
        0x295D: EXTRAV, // down harpoon with barb right from bar
        0x295E: EXTRAH, // leftwards harpoon with barb down from bar
        0x295F: EXTRAH, // rightwards harpoon with barb down from bar
        0x2960: EXTRAV, // up harpoon with barb left from bar
        0x2961: EXTRAV, // down harpoon with barb left from bar
        0x2312: {alias: 0x23DC, dir:H}, // arc
        0x2322: {alias: 0x23DC, dir:H}, // frown
        0x2323: {alias: 0x23DD, dir:H}, // smile
        0x27F5: {alias: 0x2190, dir:H}, // long left arrow
        0x27F6: {alias: 0x2192, dir:H}, // long right arrow
        0x27F7: {alias: 0x2194, dir:H}, // long left-right arrow
        0x27F8: {alias: 0x21D0, dir:H}, // long left double arrow
        0x27F9: {alias: 0x21D2, dir:H}, // long right double arrow
        0x27FA: {alias: 0x21D4, dir:H}, // long left-right double arrow
        0x27FB: {alias: 0x21A4, dir:H}, // long left arrow from bar
        0x27FC: {alias: 0x21A6, dir:H}, // long right arrow from bar
        0x27FD: {alias: 0x2906, dir:H}, // long left double arrow from bar
        0x27FE: {alias: 0x2907, dir:H}  // long right double arrow from bar
      }
    }
  });

  
  SVG.FONTDATA.FONTS['MathJax_Main'] = {
    directory: 'Main/Regular',
    family: 'MathJax_Main',
    id: 'MJMAIN',
    skew: {
      0x131: 0.0278,
      0x237: 0.0833,
      0x2113: 0.111,
      0x2118: 0.111,
      0x2202: 0.0833
    },
    Ranges: [
      [0x20,0x7F,"BasicLatin"],
      [0x100,0x17F,"LatinExtendedA"],
      [0x180,0x24F,"LatinExtendedB"],
      [0x2B0,0x2FF,"SpacingModLetters"],
      [0x300,0x36F,"CombDiacritMarks"],
      [0x370,0x3FF,"GreekAndCoptic"],
      [0x2100,0x214F,"LetterlikeSymbols"],
      [0x25A0,0x25FF,"GeometricShapes"],
      [0x2600,0x26FF,"MiscSymbols"],
      [0x2A00,0x2AFF,"SuppMathOperators"]
    ],
  
      // SPACE
      0x20: [0,0,250,0,0,''],
  
      // LEFT PARENTHESIS
      0x28: [750,250,389,94,333,'94 250Q94 319 104 381T127 488T164 576T202 643T244 695T277 729T302 750H315H319Q333 750 333 741Q333 738 316 720T275 667T226 581T184 443T167 250T184 58T225 -81T274 -167T316 -220T333 -241Q333 -250 318 -250H315H302L274 -226Q180 -141 137 -14T94 250'],
  
      // RIGHT PARENTHESIS
      0x29: [750,250,389,55,294,'60 749L64 750Q69 750 74 750H86L114 726Q208 641 251 514T294 250Q294 182 284 119T261 12T224 -76T186 -143T145 -194T113 -227T90 -246Q87 -249 86 -250H74Q66 -250 63 -250T58 -247T55 -238Q56 -237 66 -225Q221 -64 221 250T66 725Q56 737 55 738Q55 746 60 749'],
  
      // PLUS SIGN
      0x2B: [583,82,778,56,722,'56 237T56 250T70 270H369V420L370 570Q380 583 389 583Q402 583 409 568V270H707Q722 262 722 250T707 230H409V-68Q401 -82 391 -82H389H387Q375 -82 369 -68V230H70Q56 237 56 250'],
  
      // COMMA
      0x2C: [121,195,278,78,210,'78 35T78 60T94 103T137 121Q165 121 187 96T210 8Q210 -27 201 -60T180 -117T154 -158T130 -185T117 -194Q113 -194 104 -185T95 -172Q95 -168 106 -156T131 -126T157 -76T173 -3V9L172 8Q170 7 167 6T161 3T152 1T140 0Q113 0 96 17'],
  
      // FULL STOP
      0x2E: [120,0,278,78,199,'78 60Q78 84 95 102T138 120Q162 120 180 104T199 61Q199 36 182 18T139 0T96 17T78 60'],
  
      // SOLIDUS
      0x2F: [750,250,500,56,444,'423 750Q432 750 438 744T444 730Q444 725 271 248T92 -240Q85 -250 75 -250Q68 -250 62 -245T56 -231Q56 -221 230 257T407 740Q411 750 423 750'],
  
      // DIGIT ZERO
      0x30: [666,22,500,39,460,'96 585Q152 666 249 666Q297 666 345 640T423 548Q460 465 460 320Q460 165 417 83Q397 41 362 16T301 -15T250 -22Q224 -22 198 -16T137 16T82 83Q39 165 39 320Q39 494 96 585ZM321 597Q291 629 250 629Q208 629 178 597Q153 571 145 525T137 333Q137 175 145 125T181 46Q209 16 250 16Q290 16 318 46Q347 76 354 130T362 333Q362 478 354 524T321 597'],
  
      // DIGIT ONE
      0x31: [666,0,500,83,427,'213 578L200 573Q186 568 160 563T102 556H83V602H102Q149 604 189 617T245 641T273 663Q275 666 285 666Q294 666 302 660V361L303 61Q310 54 315 52T339 48T401 46H427V0H416Q395 3 257 3Q121 3 100 0H88V46H114Q136 46 152 46T177 47T193 50T201 52T207 57T213 61V578'],
  
      // DIGIT TWO
      0x32: [666,0,500,50,449,'109 429Q82 429 66 447T50 491Q50 562 103 614T235 666Q326 666 387 610T449 465Q449 422 429 383T381 315T301 241Q265 210 201 149L142 93L218 92Q375 92 385 97Q392 99 409 186V189H449V186Q448 183 436 95T421 3V0H50V19V31Q50 38 56 46T86 81Q115 113 136 137Q145 147 170 174T204 211T233 244T261 278T284 308T305 340T320 369T333 401T340 431T343 464Q343 527 309 573T212 619Q179 619 154 602T119 569T109 550Q109 549 114 549Q132 549 151 535T170 489Q170 464 154 447T109 429'],
  
      // DIGIT THREE
      0x33: [665,22,500,42,457,'127 463Q100 463 85 480T69 524Q69 579 117 622T233 665Q268 665 277 664Q351 652 390 611T430 522Q430 470 396 421T302 350L299 348Q299 347 308 345T337 336T375 315Q457 262 457 175Q457 96 395 37T238 -22Q158 -22 100 21T42 130Q42 158 60 175T105 193Q133 193 151 175T169 130Q169 119 166 110T159 94T148 82T136 74T126 70T118 67L114 66Q165 21 238 21Q293 21 321 74Q338 107 338 175V195Q338 290 274 322Q259 328 213 329L171 330L168 332Q166 335 166 348Q166 366 174 366Q202 366 232 371Q266 376 294 413T322 525V533Q322 590 287 612Q265 626 240 626Q208 626 181 615T143 592T132 580H135Q138 579 143 578T153 573T165 566T175 555T183 540T186 520Q186 498 172 481T127 463'],
  
      // DIGIT FOUR
      0x34: [677,0,500,28,471,'462 0Q444 3 333 3Q217 3 199 0H190V46H221Q241 46 248 46T265 48T279 53T286 61Q287 63 287 115V165H28V211L179 442Q332 674 334 675Q336 677 355 677H373L379 671V211H471V165H379V114Q379 73 379 66T385 54Q393 47 442 46H471V0H462ZM293 211V545L74 212L183 211H293'],
  
      // DIGIT FIVE
      0x35: [666,22,500,50,449,'164 157Q164 133 148 117T109 101H102Q148 22 224 22Q294 22 326 82Q345 115 345 210Q345 313 318 349Q292 382 260 382H254Q176 382 136 314Q132 307 129 306T114 304Q97 304 95 310Q93 314 93 485V614Q93 664 98 664Q100 666 102 666Q103 666 123 658T178 642T253 634Q324 634 389 662Q397 666 402 666Q410 666 410 648V635Q328 538 205 538Q174 538 149 544L139 546V374Q158 388 169 396T205 412T256 420Q337 420 393 355T449 201Q449 109 385 44T229 -22Q148 -22 99 32T50 154Q50 178 61 192T84 210T107 214Q132 214 148 197T164 157'],
  
      // DIGIT SIX
      0x36: [666,22,500,41,456,'42 313Q42 476 123 571T303 666Q372 666 402 630T432 550Q432 525 418 510T379 495Q356 495 341 509T326 548Q326 592 373 601Q351 623 311 626Q240 626 194 566Q147 500 147 364L148 360Q153 366 156 373Q197 433 263 433H267Q313 433 348 414Q372 400 396 374T435 317Q456 268 456 210V192Q456 169 451 149Q440 90 387 34T253 -22Q225 -22 199 -14T143 16T92 75T56 172T42 313ZM257 397Q227 397 205 380T171 335T154 278T148 216Q148 133 160 97T198 39Q222 21 251 21Q302 21 329 59Q342 77 347 104T352 209Q352 289 347 316T329 361Q302 397 257 397'],
  
      // DIGIT SEVEN
      0x37: [676,22,500,55,485,'55 458Q56 460 72 567L88 674Q88 676 108 676H128V672Q128 662 143 655T195 646T364 644H485V605L417 512Q408 500 387 472T360 435T339 403T319 367T305 330T292 284T284 230T278 162T275 80Q275 66 275 52T274 28V19Q270 2 255 -10T221 -22Q210 -22 200 -19T179 0T168 40Q168 198 265 368Q285 400 349 489L395 552H302Q128 552 119 546Q113 543 108 522T98 479L95 458V455H55V458'],
  
      // DIGIT EIGHT
      0x38: [666,22,500,43,457,'70 417T70 494T124 618T248 666Q319 666 374 624T429 515Q429 485 418 459T392 417T361 389T335 371T324 363L338 354Q352 344 366 334T382 323Q457 264 457 174Q457 95 399 37T249 -22Q159 -22 101 29T43 155Q43 263 172 335L154 348Q133 361 127 368Q70 417 70 494ZM286 386L292 390Q298 394 301 396T311 403T323 413T334 425T345 438T355 454T364 471T369 491T371 513Q371 556 342 586T275 624Q268 625 242 625Q201 625 165 599T128 534Q128 511 141 492T167 463T217 431Q224 426 228 424L286 386ZM250 21Q308 21 350 55T392 137Q392 154 387 169T375 194T353 216T330 234T301 253T274 270Q260 279 244 289T218 306L210 311Q204 311 181 294T133 239T107 157Q107 98 150 60T250 21'],
  
      // DIGIT NINE
      0x39: [666,22,500,42,456,'352 287Q304 211 232 211Q154 211 104 270T44 396Q42 412 42 436V444Q42 537 111 606Q171 666 243 666Q245 666 249 666T257 665H261Q273 665 286 663T323 651T370 619T413 560Q456 472 456 334Q456 194 396 97Q361 41 312 10T208 -22Q147 -22 108 7T68 93T121 149Q143 149 158 135T173 96Q173 78 164 65T148 49T135 44L131 43Q131 41 138 37T164 27T206 22H212Q272 22 313 86Q352 142 352 280V287ZM244 248Q292 248 321 297T351 430Q351 508 343 542Q341 552 337 562T323 588T293 615T246 625Q208 625 181 598Q160 576 154 546T147 441Q147 358 152 329T172 282Q197 248 244 248'],
  
      // COLON
      0x3A: [430,0,278,78,199,'78 370Q78 394 95 412T138 430Q162 430 180 414T199 371Q199 346 182 328T139 310T96 327T78 370ZM78 60Q78 84 95 102T138 120Q162 120 180 104T199 61Q199 36 182 18T139 0T96 17T78 60'],
  
      // SEMICOLON
      0x3B: [430,194,278,78,202,'78 370Q78 394 95 412T138 430Q162 430 180 414T199 371Q199 346 182 328T139 310T96 327T78 370ZM78 60Q78 85 94 103T137 121Q202 121 202 8Q202 -44 183 -94T144 -169T118 -194Q115 -194 106 -186T95 -174Q94 -171 107 -155T137 -107T160 -38Q161 -32 162 -22T165 -4T165 4Q165 5 161 4T142 0Q110 0 94 18T78 60'],
  
      // LESS-THAN SIGN
      0x3C: [540,40,778,83,695,'694 -11T694 -19T688 -33T678 -40Q671 -40 524 29T234 166L90 235Q83 240 83 250Q83 261 91 266Q664 540 678 540Q681 540 687 534T694 519T687 505Q686 504 417 376L151 250L417 124Q686 -4 687 -5Q694 -11 694 -19'],
  
      // EQUALS SIGN
      0x3D: [367,-133,778,56,722,'56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153'],
  
      // GREATER-THAN SIGN
      0x3E: [540,40,778,82,694,'84 520Q84 528 88 533T96 539L99 540Q106 540 253 471T544 334L687 265Q694 260 694 250T687 235Q685 233 395 96L107 -40H101Q83 -38 83 -20Q83 -19 83 -17Q82 -10 98 -1Q117 9 248 71Q326 108 378 132L626 250L378 368Q90 504 86 509Q84 513 84 520'],
  
      // LEFT SQUARE BRACKET
      0x5B: [750,250,278,118,255,'118 -250V750H255V710H158V-210H255V-250H118'],
  
      // REVERSE SOLIDUS
      0x5C: [750,250,500,56,444,'56 731Q56 740 62 745T75 750Q85 750 92 740Q96 733 270 255T444 -231Q444 -239 438 -244T424 -250Q414 -250 407 -240Q404 -236 230 242T56 731'],
  
      // RIGHT SQUARE BRACKET
      0x5D: [750,250,278,22,159,'22 710V750H159V-250H22V-210H119V710H22'],
  
      // CIRCUMFLEX ACCENT
      0x5E: [694,-531,500,112,387,'112 560L249 694L257 686Q387 562 387 560L361 531Q359 532 303 581L250 627L195 580Q182 569 169 557T148 538L140 532Q138 530 125 546L112 560'],
  
      // LATIN SMALL LETTER A
      0x61: [448,11,500,34,493,'137 305T115 305T78 320T63 359Q63 394 97 421T218 448Q291 448 336 416T396 340Q401 326 401 309T402 194V124Q402 76 407 58T428 40Q443 40 448 56T453 109V145H493V106Q492 66 490 59Q481 29 455 12T400 -6T353 12T329 54V58L327 55Q325 52 322 49T314 40T302 29T287 17T269 6T247 -2T221 -8T190 -11Q130 -11 82 20T34 107Q34 128 41 147T68 188T116 225T194 253T304 268H318V290Q318 324 312 340Q290 411 215 411Q197 411 181 410T156 406T148 403Q170 388 170 359Q170 334 154 320ZM126 106Q126 75 150 51T209 26Q247 26 276 49T315 109Q317 116 318 175Q318 233 317 233Q309 233 296 232T251 223T193 203T147 166T126 106'],
  
      // LATIN SMALL LETTER B
      0x62: [695,11,556,20,522,'307 -11Q234 -11 168 55L158 37Q156 34 153 28T147 17T143 10L138 1L118 0H98V298Q98 599 97 603Q94 622 83 628T38 637H20V660Q20 683 22 683L32 684Q42 685 61 686T98 688Q115 689 135 690T165 693T176 694H179V543Q179 391 180 391L183 394Q186 397 192 401T207 411T228 421T254 431T286 439T323 442Q401 442 461 379T522 216Q522 115 458 52T307 -11ZM182 98Q182 97 187 90T196 79T206 67T218 55T233 44T250 35T271 29T295 26Q330 26 363 46T412 113Q424 148 424 212Q424 287 412 323Q385 405 300 405Q270 405 239 390T188 347L182 339V98'],
  
      // LATIN SMALL LETTER C
      0x63: [448,12,444,34,415,'370 305T349 305T313 320T297 358Q297 381 312 396Q317 401 317 402T307 404Q281 408 258 408Q209 408 178 376Q131 329 131 219Q131 137 162 90Q203 29 272 29Q313 29 338 55T374 117Q376 125 379 127T395 129H409Q415 123 415 120Q415 116 411 104T395 71T366 33T318 2T249 -11Q163 -11 99 53T34 214Q34 318 99 383T250 448T370 421T404 357Q404 334 387 320'],
  
      // LATIN SMALL LETTER D
      0x64: [695,11,556,34,535,'376 495Q376 511 376 535T377 568Q377 613 367 624T316 637H298V660Q298 683 300 683L310 684Q320 685 339 686T376 688Q393 689 413 690T443 693T454 694H457V390Q457 84 458 81Q461 61 472 55T517 46H535V0Q533 0 459 -5T380 -11H373V44L365 37Q307 -11 235 -11Q158 -11 96 50T34 215Q34 315 97 378T244 442Q319 442 376 393V495ZM373 342Q328 405 260 405Q211 405 173 369Q146 341 139 305T131 211Q131 155 138 120T173 59Q203 26 251 26Q322 26 373 103V342'],
  
      // LATIN SMALL LETTER E
      0x65: [448,11,444,28,415,'28 218Q28 273 48 318T98 391T163 433T229 448Q282 448 320 430T378 380T406 316T415 245Q415 238 408 231H126V216Q126 68 226 36Q246 30 270 30Q312 30 342 62Q359 79 369 104L379 128Q382 131 395 131H398Q415 131 415 121Q415 117 412 108Q393 53 349 21T250 -11Q155 -11 92 58T28 218ZM333 275Q322 403 238 411H236Q228 411 220 410T195 402T166 381T143 340T127 274V267H333V275'],
  
      // LATIN SMALL LETTER F
      0x66: [705,0,306,26,372,'273 0Q255 3 146 3Q43 3 34 0H26V46H42Q70 46 91 49Q99 52 103 60Q104 62 104 224V385H33V431H104V497L105 564L107 574Q126 639 171 668T266 704Q267 704 275 704T289 705Q330 702 351 679T372 627Q372 604 358 590T321 576T284 590T270 627Q270 647 288 667H284Q280 668 273 668Q245 668 223 647T189 592Q183 572 182 497V431H293V385H185V225Q185 63 186 61T189 57T194 54T199 51T206 49T213 48T222 47T231 47T241 46T251 46H282V0H273'],
  
      // LATIN SMALL LETTER G
      0x67: [453,206,500,29,485,'329 409Q373 453 429 453Q459 453 472 434T485 396Q485 382 476 371T449 360Q416 360 412 390Q410 404 415 411Q415 412 416 414V415Q388 412 363 393Q355 388 355 386Q355 385 359 381T368 369T379 351T388 325T392 292Q392 230 343 187T222 143Q172 143 123 171Q112 153 112 133Q112 98 138 81Q147 75 155 75T227 73Q311 72 335 67Q396 58 431 26Q470 -13 470 -72Q470 -139 392 -175Q332 -206 250 -206Q167 -206 107 -175Q29 -140 29 -75Q29 -39 50 -15T92 18L103 24Q67 55 67 108Q67 155 96 193Q52 237 52 292Q52 355 102 398T223 442Q274 442 318 416L329 409ZM299 343Q294 371 273 387T221 404Q192 404 171 388T145 343Q142 326 142 292Q142 248 149 227T179 192Q196 182 222 182Q244 182 260 189T283 207T294 227T299 242Q302 258 302 292T299 343ZM403 -75Q403 -50 389 -34T348 -11T299 -2T245 0H218Q151 0 138 -6Q118 -15 107 -34T95 -74Q95 -84 101 -97T122 -127T170 -155T250 -167Q319 -167 361 -139T403 -75'],
  
      // LATIN SMALL LETTER H
      0x68: [695,0,556,25,542,'41 46H55Q94 46 102 60V68Q102 77 102 91T102 124T102 167T103 217T103 272T103 329Q103 366 103 407T103 482T102 542T102 586T102 603Q99 622 88 628T43 637H25V660Q25 683 27 683L37 684Q47 685 66 686T103 688Q120 689 140 690T170 693T181 694H184V367Q244 442 328 442Q451 442 463 329Q464 322 464 190V104Q464 66 466 59T477 49Q498 46 526 46H542V0H534L510 1Q487 2 460 2T422 3Q319 3 310 0H302V46H318Q379 46 379 62Q380 64 380 200Q379 335 378 343Q372 371 358 385T334 402T308 404Q263 404 229 370Q202 343 195 315T187 232V168V108Q187 78 188 68T191 55T200 49Q221 46 249 46H265V0H257L234 1Q210 2 183 2T145 3Q42 3 33 0H25V46H41'],
  
      // LATIN SMALL LETTER I
      0x69: [669,0,278,26,255,'69 609Q69 637 87 653T131 669Q154 667 171 652T188 609Q188 579 171 564T129 549Q104 549 87 564T69 609ZM247 0Q232 3 143 3Q132 3 106 3T56 1L34 0H26V46H42Q70 46 91 49Q100 53 102 60T104 102V205V293Q104 345 102 359T88 378Q74 385 41 385H30V408Q30 431 32 431L42 432Q52 433 70 434T106 436Q123 437 142 438T171 441T182 442H185V62Q190 52 197 50T232 46H255V0H247'],
  
      // LATIN SMALL LETTER J
      0x6A: [669,205,306,-55,218,'98 609Q98 637 116 653T160 669Q183 667 200 652T217 609Q217 579 200 564T158 549Q133 549 116 564T98 609ZM28 -163Q58 -168 64 -168Q124 -168 135 -77Q137 -65 137 141T136 353Q132 371 120 377T72 385H52V408Q52 431 54 431L58 432Q62 432 70 432T87 433T108 434T133 436Q151 437 171 438T202 441T214 442H218V184Q217 -36 217 -59T211 -98Q195 -145 153 -175T58 -205Q9 -205 -23 -179T-55 -117Q-55 -94 -40 -79T-2 -64T36 -79T52 -118Q52 -143 28 -163'],
  
      // LATIN SMALL LETTER K
      0x6B: [695,0,528,20,511,'36 46H50Q89 46 97 60V68Q97 77 97 91T97 124T98 167T98 217T98 272T98 329Q98 366 98 407T98 482T98 542T97 586T97 603Q94 622 83 628T38 637H20V660Q20 683 22 683L32 684Q42 685 61 686T98 688Q115 689 135 690T165 693T176 694H179V463L180 233L240 287Q300 341 304 347Q310 356 310 364Q310 383 289 385H284V431H293Q308 428 412 428Q475 428 484 431H489V385H476Q407 380 360 341Q286 278 286 274Q286 273 349 181T420 79Q434 60 451 53T500 46H511V0H505Q496 3 418 3Q322 3 307 0H299V46H306Q330 48 330 65Q330 72 326 79Q323 84 276 153T228 222L176 176V120V84Q176 65 178 59T189 49Q210 46 238 46H254V0H246Q231 3 137 3T28 0H20V46H36'],
  
      // LATIN SMALL LETTER L
      0x6C: [695,0,278,26,263,'42 46H56Q95 46 103 60V68Q103 77 103 91T103 124T104 167T104 217T104 272T104 329Q104 366 104 407T104 482T104 542T103 586T103 603Q100 622 89 628T44 637H26V660Q26 683 28 683L38 684Q48 685 67 686T104 688Q121 689 141 690T171 693T182 694H185V379Q185 62 186 60Q190 52 198 49Q219 46 247 46H263V0H255L232 1Q209 2 183 2T145 3T107 3T57 1L34 0H26V46H42'],
  
      // LATIN SMALL LETTER M
      0x6D: [443,0,833,25,819,'41 46H55Q94 46 102 60V68Q102 77 102 91T102 122T103 161T103 203Q103 234 103 269T102 328V351Q99 370 88 376T43 385H25V408Q25 431 27 431L37 432Q47 433 65 434T102 436Q119 437 138 438T167 441T178 442H181V402Q181 364 182 364T187 369T199 384T218 402T247 421T285 437Q305 442 336 442Q351 442 364 440T387 434T406 426T421 417T432 406T441 395T448 384T452 374T455 366L457 361L460 365Q463 369 466 373T475 384T488 397T503 410T523 422T546 432T572 439T603 442Q729 442 740 329Q741 322 741 190V104Q741 66 743 59T754 49Q775 46 803 46H819V0H811L788 1Q764 2 737 2T699 3Q596 3 587 0H579V46H595Q656 46 656 62Q657 64 657 200Q656 335 655 343Q649 371 635 385T611 402T585 404Q540 404 506 370Q479 343 472 315T464 232V168V108Q464 78 465 68T468 55T477 49Q498 46 526 46H542V0H534L510 1Q487 2 460 2T422 3Q319 3 310 0H302V46H318Q379 46 379 62Q380 64 380 200Q379 335 378 343Q372 371 358 385T334 402T308 404Q263 404 229 370Q202 343 195 315T187 232V168V108Q187 78 188 68T191 55T200 49Q221 46 249 46H265V0H257L234 1Q210 2 183 2T145 3Q42 3 33 0H25V46H41'],
  
      // LATIN SMALL LETTER N
      0x6E: [443,0,556,25,542,'41 46H55Q94 46 102 60V68Q102 77 102 91T102 122T103 161T103 203Q103 234 103 269T102 328V351Q99 370 88 376T43 385H25V408Q25 431 27 431L37 432Q47 433 65 434T102 436Q119 437 138 438T167 441T178 442H181V402Q181 364 182 364T187 369T199 384T218 402T247 421T285 437Q305 442 336 442Q450 438 463 329Q464 322 464 190V104Q464 66 466 59T477 49Q498 46 526 46H542V0H534L510 1Q487 2 460 2T422 3Q319 3 310 0H302V46H318Q379 46 379 62Q380 64 380 200Q379 335 378 343Q372 371 358 385T334 402T308 404Q263 404 229 370Q202 343 195 315T187 232V168V108Q187 78 188 68T191 55T200 49Q221 46 249 46H265V0H257L234 1Q210 2 183 2T145 3Q42 3 33 0H25V46H41'],
  
      // LATIN SMALL LETTER O
      0x6F: [448,10,500,28,471,'28 214Q28 309 93 378T250 448Q340 448 405 380T471 215Q471 120 407 55T250 -10Q153 -10 91 57T28 214ZM250 30Q372 30 372 193V225V250Q372 272 371 288T364 326T348 362T317 390T268 410Q263 411 252 411Q222 411 195 399Q152 377 139 338T126 246V226Q126 130 145 91Q177 30 250 30'],
  
      // LATIN SMALL LETTER P
      0x70: [443,194,556,20,522,'36 -148H50Q89 -148 97 -134V-126Q97 -119 97 -107T97 -77T98 -38T98 6T98 55T98 106Q98 140 98 177T98 243T98 296T97 335T97 351Q94 370 83 376T38 385H20V408Q20 431 22 431L32 432Q42 433 61 434T98 436Q115 437 135 438T165 441T176 442H179V416L180 390L188 397Q247 441 326 441Q407 441 464 377T522 216Q522 115 457 52T310 -11Q242 -11 190 33L182 40V-45V-101Q182 -128 184 -134T195 -145Q216 -148 244 -148H260V-194H252L228 -193Q205 -192 178 -192T140 -191Q37 -191 28 -194H20V-148H36ZM424 218Q424 292 390 347T305 402Q234 402 182 337V98Q222 26 294 26Q345 26 384 80T424 218'],
  
      // LATIN SMALL LETTER Q
      0x71: [442,194,528,33,535,'33 218Q33 308 95 374T236 441H246Q330 441 381 372L387 364Q388 364 404 403L420 442H457V156Q457 -132 458 -134Q462 -142 470 -145Q491 -148 519 -148H535V-194H527L504 -193Q480 -192 453 -192T415 -191Q312 -191 303 -194H295V-148H311Q339 -148 360 -145Q369 -141 371 -135T373 -106V-41V49Q313 -11 236 -11Q154 -11 94 53T33 218ZM376 300Q346 389 278 401Q275 401 269 401T261 402Q211 400 171 350T131 214Q131 137 165 82T253 27Q296 27 328 54T376 118V300'],
  
      // LATIN SMALL LETTER R
      0x72: [443,0,392,20,364,'36 46H50Q89 46 97 60V68Q97 77 97 91T98 122T98 161T98 203Q98 234 98 269T98 328L97 351Q94 370 83 376T38 385H20V408Q20 431 22 431L32 432Q42 433 60 434T96 436Q112 437 131 438T160 441T171 442H174V373Q213 441 271 441H277Q322 441 343 419T364 373Q364 352 351 337T313 322Q288 322 276 338T263 372Q263 381 265 388T270 400T273 405Q271 407 250 401Q234 393 226 386Q179 341 179 207V154Q179 141 179 127T179 101T180 81T180 66V61Q181 59 183 57T188 54T193 51T200 49T207 48T216 47T225 47T235 46T245 46H276V0H267Q249 3 140 3Q37 3 28 0H20V46H36'],
  
      // LATIN SMALL LETTER S
      0x73: [448,11,394,33,359,'295 316Q295 356 268 385T190 414Q154 414 128 401Q98 382 98 349Q97 344 98 336T114 312T157 287Q175 282 201 278T245 269T277 256Q294 248 310 236T342 195T359 133Q359 71 321 31T198 -10H190Q138 -10 94 26L86 19L77 10Q71 4 65 -1L54 -11H46H42Q39 -11 33 -5V74V132Q33 153 35 157T45 162H54Q66 162 70 158T75 146T82 119T101 77Q136 26 198 26Q295 26 295 104Q295 133 277 151Q257 175 194 187T111 210Q75 227 54 256T33 318Q33 357 50 384T93 424T143 442T187 447H198Q238 447 268 432L283 424L292 431Q302 440 314 448H322H326Q329 448 335 442V310L329 304H301Q295 310 295 316'],
  
      // LATIN SMALL LETTER T
      0x74: [615,10,389,18,333,'27 422Q80 426 109 478T141 600V615H181V431H316V385H181V241Q182 116 182 100T189 68Q203 29 238 29Q282 29 292 100Q293 108 293 146V181H333V146V134Q333 57 291 17Q264 -10 221 -10Q187 -10 162 2T124 33T105 68T98 100Q97 107 97 248V385H18V422H27'],
  
      // LATIN SMALL LETTER U
      0x75: [443,11,556,25,542,'383 58Q327 -10 256 -10H249Q124 -10 105 89Q104 96 103 226Q102 335 102 348T96 369Q86 385 36 385H25V408Q25 431 27 431L38 432Q48 433 67 434T105 436Q122 437 142 438T172 441T184 442H187V261Q188 77 190 64Q193 49 204 40Q224 26 264 26Q290 26 311 35T343 58T363 90T375 120T379 144Q379 145 379 161T380 201T380 248V315Q380 361 370 372T320 385H302V431Q304 431 378 436T457 442H464V264Q464 84 465 81Q468 61 479 55T524 46H542V0Q540 0 467 -5T390 -11H383V58'],
  
      // LATIN SMALL LETTER V
      0x76: [431,11,528,19,508,'338 431Q344 429 422 429Q479 429 503 431H508V385H497Q439 381 423 345Q421 341 356 172T288 -2Q283 -11 263 -11Q244 -11 239 -2Q99 359 98 364Q93 378 82 381T43 385H19V431H25L33 430Q41 430 53 430T79 430T104 429T122 428Q217 428 232 431H240V385H226Q187 384 184 370Q184 366 235 234L286 102L377 341V349Q377 363 367 372T349 383T335 385H331V431H338'],
  
      // LATIN SMALL LETTER W
      0x77: [431,11,722,18,703,'90 368Q84 378 76 380T40 385H18V431H24L43 430Q62 430 84 429T116 428Q206 428 221 431H229V385H215Q177 383 177 368Q177 367 221 239L265 113L339 328L333 345Q323 374 316 379Q308 384 278 385H258V431H264Q270 428 348 428Q439 428 454 431H461V385H452Q404 385 404 369Q404 366 418 324T449 234T481 143L496 100L537 219Q579 341 579 347Q579 363 564 373T530 385H522V431H529Q541 428 624 428Q692 428 698 431H703V385H697Q696 385 691 385T682 384Q635 377 619 334L559 161Q546 124 528 71Q508 12 503 1T487 -11H479Q460 -11 456 -4Q455 -3 407 133L361 267Q359 263 266 -4Q261 -11 243 -11H238Q225 -11 220 -3L90 368'],
  
      // LATIN SMALL LETTER X
      0x78: [431,0,528,11,516,'201 0Q189 3 102 3Q26 3 17 0H11V46H25Q48 47 67 52T96 61T121 78T139 96T160 122T180 150L226 210L168 288Q159 301 149 315T133 336T122 351T113 363T107 370T100 376T94 379T88 381T80 383Q74 383 44 385H16V431H23Q59 429 126 429Q219 429 229 431H237V385Q201 381 201 369Q201 367 211 353T239 315T268 274L272 270L297 304Q329 345 329 358Q329 364 327 369T322 376T317 380T310 384L307 385H302V431H309Q324 428 408 428Q487 428 493 431H499V385H492Q443 385 411 368Q394 360 377 341T312 257L296 236L358 151Q424 61 429 57T446 50Q464 46 499 46H516V0H510H502Q494 1 482 1T457 2T432 2T414 3Q403 3 377 3T327 1L304 0H295V46H298Q309 46 320 51T331 63Q331 65 291 120L250 175Q249 174 219 133T185 88Q181 83 181 74Q181 63 188 55T206 46Q208 46 208 23V0H201'],
  
      // LATIN SMALL LETTER Y
      0x79: [431,204,528,19,508,'69 -66Q91 -66 104 -80T118 -116Q118 -134 109 -145T91 -160Q84 -163 97 -166Q104 -168 111 -168Q131 -168 148 -159T175 -138T197 -106T213 -75T225 -43L242 0L170 183Q150 233 125 297Q101 358 96 368T80 381Q79 382 78 382Q66 385 34 385H19V431H26L46 430Q65 430 88 429T122 428Q129 428 142 428T171 429T200 430T224 430L233 431H241V385H232Q183 385 185 366L286 112Q286 113 332 227L376 341V350Q376 365 366 373T348 383T334 385H331V431H337H344Q351 431 361 431T382 430T405 429T422 429Q477 429 503 431H508V385H497Q441 380 422 345Q420 343 378 235T289 9T227 -131Q180 -204 113 -204Q69 -204 44 -177T19 -116Q19 -89 35 -78T69 -66'],
  
      // LATIN SMALL LETTER Z
      0x7A: [431,0,444,28,401,'42 263Q44 270 48 345T53 423V431H393Q399 425 399 415Q399 403 398 402L381 378Q364 355 331 309T265 220L134 41L182 40H206Q254 40 283 46T331 77Q352 105 359 185L361 201Q361 202 381 202H401V196Q401 195 393 103T384 6V0H209L34 1L31 3Q28 8 28 17Q28 30 29 31T160 210T294 394H236Q169 393 152 388Q127 382 113 367Q89 344 82 264V255H42V263'],
  
      // LEFT CURLY BRACKET
      0x7B: [750,250,500,65,434,'434 -231Q434 -244 428 -250H410Q281 -250 230 -184Q225 -177 222 -172T217 -161T213 -148T211 -133T210 -111T209 -84T209 -47T209 0Q209 21 209 53Q208 142 204 153Q203 154 203 155Q189 191 153 211T82 231Q71 231 68 234T65 250T68 266T82 269Q116 269 152 289T203 345Q208 356 208 377T209 529V579Q209 634 215 656T244 698Q270 724 324 740Q361 748 377 749Q379 749 390 749T408 750H428Q434 744 434 732Q434 719 431 716Q429 713 415 713Q362 710 332 689T296 647Q291 634 291 499V417Q291 370 288 353T271 314Q240 271 184 255L170 250L184 245Q202 239 220 230T262 196T290 137Q291 131 291 1Q291 -134 296 -147Q306 -174 339 -192T415 -213Q429 -213 431 -216Q434 -219 434 -231'],
  
      // VERTICAL LINE
      0x7C: [750,249,278,119,159,'139 -249H137Q125 -249 119 -235V251L120 737Q130 750 139 750Q152 750 159 735V-235Q151 -249 141 -249H139'],
  
      // RIGHT CURLY BRACKET
      0x7D: [750,250,500,65,434,'65 731Q65 745 68 747T88 750Q171 750 216 725T279 670Q288 649 289 635T291 501Q292 362 293 357Q306 312 345 291T417 269Q428 269 431 266T434 250T431 234T417 231Q380 231 345 210T298 157Q293 143 292 121T291 -28V-79Q291 -134 285 -156T256 -198Q202 -250 89 -250Q71 -250 68 -247T65 -230Q65 -224 65 -223T66 -218T69 -214T77 -213Q91 -213 108 -210T146 -200T183 -177T207 -139Q208 -134 209 3L210 139Q223 196 280 230Q315 247 330 250Q305 257 280 270Q225 304 212 352L210 362L209 498Q208 635 207 640Q195 680 154 696T77 713Q68 713 67 716T65 731'],
  
      // DIAERESIS
      0xA8: [669,-554,500,95,405,'95 612Q95 633 112 651T153 669T193 652T210 612Q210 588 194 571T152 554L127 560Q95 577 95 612ZM289 611Q289 634 304 649T335 668Q336 668 340 668T346 669Q369 669 386 652T404 612T387 572T346 554Q323 554 306 570T289 611'],
  
      // NOT SIGN
      0xAC: [356,-89,667,56,611,'56 323T56 336T70 356H596Q603 353 611 343V102Q598 89 591 89Q587 89 584 90T579 94T575 98T572 102L571 209V316H70Q56 323 56 336'],
  
      // MACRON
      0xAF: [590,-544,500,69,430,'69 544V590H430V544H69'],
  
      // DEGREE SIGN
      0xB0: [715,-542,500,147,352,'147 628Q147 669 179 692T244 715Q298 715 325 689T352 629Q352 592 323 567T249 542Q202 542 175 567T147 628ZM313 628Q313 660 300 669T259 678H253Q248 678 242 678T234 679Q217 679 207 674T192 659T188 644T187 629Q187 600 198 590Q210 579 250 579H265Q279 579 288 581T305 595T313 628'],
  
      // PLUS-MINUS SIGN
      0xB1: [666,0,778,56,722,'56 320T56 333T70 353H369V502Q369 651 371 655Q376 666 388 666Q402 666 405 654T409 596V500V353H707Q722 345 722 333Q722 320 707 313H409V40H707Q722 32 722 20T707 0H70Q56 7 56 20T70 40H369V313H70Q56 320 56 333'],
  
      // ACUTE ACCENT
      0xB4: [699,-505,500,203,393,'349 699Q367 699 380 686T393 656Q393 651 392 647T387 637T380 627T367 616T351 602T330 585T303 563L232 505L217 519Q203 533 204 533Q204 534 229 567T282 636T313 678L316 681Q318 684 321 686T328 692T337 697T349 699'],
  
      // MULTIPLICATION SIGN
      0xD7: [491,-9,778,147,630,'630 29Q630 9 609 9Q604 9 587 25T493 118L389 222L284 117Q178 13 175 11Q171 9 168 9Q160 9 154 15T147 29Q147 36 161 51T255 146L359 250L255 354Q174 435 161 449T147 471Q147 480 153 485T168 490Q173 490 175 489Q178 487 284 383L389 278L493 382Q570 459 587 475T609 491Q630 491 630 471Q630 464 620 453T522 355L418 250L522 145Q606 61 618 48T630 29'],
  
      // DIVISION SIGN
      0xF7: [537,36,778,56,721,'318 466Q318 500 339 518T386 537Q418 537 438 517T458 466Q458 438 440 417T388 396Q355 396 337 417T318 466ZM56 237T56 250T70 270H706Q721 262 721 250T706 230H70Q56 237 56 250ZM318 34Q318 68 339 86T386 105Q418 105 438 85T458 34Q458 6 440 -15T388 -36Q355 -36 337 -15T318 34'],
  
      // MODIFIER LETTER CIRCUMFLEX ACCENT
      0x2C6: [694,-531,500,112,387,'112 560L249 694L257 686Q387 562 387 560L361 531Q359 532 303 581L250 627L195 580Q182 569 169 557T148 538L140 532Q138 530 125 546L112 560'],
  
      // CARON
      0x2C7: [644,-513,500,114,385,'114 611L127 630L136 644Q138 644 193 612Q248 581 250 581L306 612Q361 644 363 644L385 611L318 562L249 513L114 611'],
  
      // MODIFIER LETTER MACRON
      0x2C9: [590,-544,500,69,430,'69 544V590H430V544H69'],
  
      // MODIFIER LETTER ACUTE ACCENT
      0x2CA: [699,-505,500,203,393,'349 699Q367 699 380 686T393 656Q393 651 392 647T387 637T380 627T367 616T351 602T330 585T303 563L232 505L217 519Q203 533 204 533Q204 534 229 567T282 636T313 678L316 681Q318 684 321 686T328 692T337 697T349 699'],
  
      // MODIFIER LETTER GRAVE ACCENT
      0x2CB: [699,-505,500,106,296,'106 655Q106 671 119 685T150 699Q166 699 177 688Q190 671 222 629T275 561T295 533T282 519L267 505L196 563Q119 626 113 634Q106 643 106 655'],
  
      // BREVE
      0x2D8: [694,-515,500,92,407,'250 515Q179 515 138 565T92 683V694H129V689Q129 688 129 683T130 675Q137 631 169 599T248 567Q304 567 337 608T370 689V694H407V683Q403 617 361 566T250 515'],
  
      // DOT ABOVE
      0x2D9: [669,-549,500,190,309,'190 609Q190 637 208 653T252 669Q275 667 292 652T309 609Q309 579 292 564T250 549Q225 549 208 564T190 609'],
  
      // SMALL TILDE
      0x2DC: [668,-565,500,83,416,'179 601Q164 601 151 595T131 584T111 565L97 577L83 588Q83 589 95 603T121 633T142 654Q165 668 187 668T253 650T320 632Q335 632 348 638T368 649T388 668L402 656L416 645Q375 586 344 572Q330 565 313 565Q292 565 248 583T179 601'],
  
      // EN DASH
      0x2013: [285,-248,500,0,499,'0 248V285H499V248H0'],
  
      // EM DASH
      0x2014: [285,-248,1000,0,999,'0 248V285H999V248H0'],
  
      // LEFT SINGLE QUOTATION MARK
      0x2018: [694,-379,278,64,199,'64 494Q64 548 86 597T131 670T160 694Q163 694 172 685T182 672Q182 669 170 656T144 625T116 573T101 501Q101 489 102 489T107 491T120 497T138 500Q163 500 180 483T198 440T181 397T139 379Q110 379 87 405T64 494'],
  
      // RIGHT SINGLE QUOTATION MARK
      0x2019: [694,-379,278,78,212,'78 634Q78 659 95 676T138 694Q166 694 189 668T212 579Q212 525 190 476T146 403T118 379Q114 379 105 388T95 401Q95 404 107 417T133 448T161 500T176 572Q176 584 175 584T170 581T157 576T139 573Q114 573 96 590T78 634'],
  
      // LEFT DOUBLE QUOTATION MARK
      0x201C: [694,-379,500,128,466,'128 494Q128 528 137 560T158 616T185 658T209 685T223 694T236 685T245 670Q244 668 231 654T204 622T178 571T164 501Q164 489 165 489T170 491T183 497T201 500Q226 500 244 483T262 440T245 397T202 379Q173 379 151 405T128 494ZM332 494Q332 528 341 560T362 616T389 658T413 685T427 694T439 685T449 672Q449 669 437 656T411 625T383 573T368 501Q368 489 369 489T374 491T387 497T405 500Q430 500 448 483T466 440T449 397T406 379Q377 379 355 405T332 494'],
  
      // RIGHT DOUBLE QUOTATION MARK
      0x201D: [694,-379,500,34,372,'34 634Q34 659 50 676T93 694Q121 694 144 668T168 579Q168 525 146 476T101 403T73 379Q69 379 60 388T50 401Q50 404 62 417T88 448T116 500T131 572Q131 584 130 584T125 581T112 576T94 573Q69 573 52 590T34 634ZM238 634Q238 659 254 676T297 694Q325 694 348 668T372 579Q372 525 350 476T305 403T277 379Q273 379 264 388T254 401Q254 404 266 417T292 448T320 500T335 572Q335 584 334 584T329 581T316 576T298 573Q273 573 256 590T238 634'],
  
      // DAGGER
      0x2020: [705,216,444,54,389,'182 675Q195 705 222 705Q234 705 243 700T253 691T263 675L262 655Q262 620 252 549T240 454V449Q250 451 288 461T346 472T377 461T389 431Q389 417 379 404T346 390Q327 390 288 401T243 412H240V405Q245 367 250 339T258 301T261 274T263 225Q263 124 255 -41T239 -213Q236 -216 222 -216H217Q206 -216 204 -212T200 -186Q199 -175 199 -168Q181 38 181 225Q181 265 182 280T191 327T204 405V412H201Q196 412 157 401T98 390Q76 390 66 403T55 431T65 458T98 472Q116 472 155 462T205 449Q204 452 204 460T201 490T193 547Q182 619 182 655V675'],
  
      // DOUBLE DAGGER
      0x2021: [705,205,444,54,389,'181 658Q181 705 222 705T263 658Q263 633 252 572T240 497Q240 496 241 496Q243 496 285 507T345 519Q365 519 376 508T388 478Q388 466 384 458T375 447T361 438H344Q318 438 282 448T241 459Q240 458 240 456Q240 449 251 384T263 297Q263 278 255 267T238 253T222 250T206 252T190 266T181 297Q181 323 192 383T204 458Q204 459 203 459Q198 459 162 449T101 438H84Q74 443 70 446T61 457T56 478Q56 497 67 508T99 519Q117 519 159 508T203 496Q204 496 204 499Q204 507 193 572T181 658ZM181 202Q181 249 222 249T263 202Q263 185 259 161T249 103T240 48V41H243Q248 41 287 52T346 63T377 52T389 22Q389 8 379 -5T346 -19Q327 -19 288 -8T243 3H240V-4Q243 -24 249 -58T259 -117T263 -158Q263 -177 255 -188T238 -202T222 -205T206 -203T190 -189T181 -158Q181 -141 185 -117T195 -59T204 -4V3H201Q196 3 157 -8T98 -19Q76 -19 66 -6T55 22T65 49T98 63Q117 63 156 52T201 41H204V48Q201 68 195 102T185 161T181 202'],
  
      // HORIZONTAL ELLIPSIS
      0x2026: [120,0,1172,78,1093,'78 60Q78 84 95 102T138 120Q162 120 180 104T199 61Q199 36 182 18T139 0T96 17T78 60ZM525 60Q525 84 542 102T585 120Q609 120 627 104T646 61Q646 36 629 18T586 0T543 17T525 60ZM972 60Q972 84 989 102T1032 120Q1056 120 1074 104T1093 61Q1093 36 1076 18T1033 0T990 17T972 60'],
  
      // PRIME
      0x2032: [560,-43,275,30,262,'79 43Q73 43 52 49T30 61Q30 68 85 293T146 528Q161 560 198 560Q218 560 240 545T262 501Q262 496 260 486Q259 479 173 263T84 45T79 43'],
  
      // COMBINING RIGHT ARROW ABOVE
      0x20D7: [714,-516,0,-471,-29,'-123 694Q-123 702 -118 708T-103 714Q-93 714 -88 706T-80 687T-67 660T-40 633Q-29 626 -29 615Q-29 606 -36 600T-53 590T-83 571T-121 531Q-135 516 -143 516T-157 522T-163 536T-152 559T-129 584T-116 595H-287L-458 596Q-459 597 -461 599T-466 602T-469 607T-471 615Q-471 622 -458 635H-99Q-123 673 -123 694'],
  
      // LEFTWARDS ARROW
      0x2190: [511,11,1000,55,944,'944 261T944 250T929 230H165Q167 228 182 216T211 189T244 152T277 96T303 25Q308 7 308 0Q308 -11 288 -11Q281 -11 278 -11T272 -7T267 2T263 21Q245 94 195 151T73 236Q58 242 55 247Q55 254 59 257T73 264Q121 283 158 314T215 375T247 434T264 480L267 497Q269 503 270 505T275 509T288 511Q308 511 308 500Q308 493 303 475Q293 438 278 406T246 352T215 315T185 287T165 270H929Q944 261 944 250'],
  
      // UPWARDS ARROW
      0x2191: [694,193,500,17,483,'27 414Q17 414 17 433Q17 437 17 439T17 444T19 447T20 450T22 452T26 453T30 454T36 456Q80 467 120 494T180 549Q227 607 238 678Q240 694 251 694Q259 694 261 684Q261 677 265 659T284 608T320 549Q340 525 363 507T405 479T440 463T467 455T479 451Q483 447 483 433Q483 413 472 413Q467 413 458 416Q342 448 277 545L270 555V-179Q262 -193 252 -193H250H248Q236 -193 230 -179V555L223 545Q192 499 146 467T70 424T27 414'],
  
      // RIGHTWARDS ARROW
      0x2192: [511,11,1000,56,944,'56 237T56 250T70 270H835Q719 357 692 493Q692 494 692 496T691 499Q691 511 708 511H711Q720 511 723 510T729 506T732 497T735 481T743 456Q765 389 816 336T935 261Q944 258 944 250Q944 244 939 241T915 231T877 212Q836 186 806 152T761 85T740 35T732 4Q730 -6 727 -8T711 -11Q691 -11 691 0Q691 7 696 25Q728 151 835 230H70Q56 237 56 250'],
  
      // DOWNWARDS ARROW
      0x2193: [694,194,500,17,483,'473 86Q483 86 483 67Q483 63 483 61T483 56T481 53T480 50T478 48T474 47T470 46T464 44Q428 35 391 14T316 -55T264 -168Q264 -170 263 -173T262 -180T261 -184Q259 -194 251 -194Q242 -194 238 -176T221 -121T180 -49Q169 -34 155 -21T125 2T95 20T67 33T44 42T27 47L21 49Q17 53 17 67Q17 87 28 87Q33 87 42 84Q158 52 223 -45L230 -55V312Q230 391 230 482T229 591Q229 662 231 676T243 693Q244 694 251 694Q264 692 270 679V-55L277 -45Q307 1 353 33T430 76T473 86'],
  
      // LEFT RIGHT ARROW
      0x2194: [511,11,1000,55,944,'263 479Q267 501 271 506T288 511Q308 511 308 500Q308 493 303 475Q293 438 278 406T246 352T215 315T185 287T165 270H835Q729 349 696 475Q691 493 691 500Q691 511 711 511Q720 511 723 510T729 506T732 497T735 481T743 456Q765 389 816 336T935 261Q944 258 944 250Q944 244 939 241T915 231T877 212Q836 186 806 152T761 85T740 35T732 4Q730 -6 727 -8T711 -11Q691 -11 691 0Q691 7 696 25Q728 151 835 230H165Q167 228 182 216T211 189T244 152T277 96T303 25Q308 7 308 0Q308 -11 288 -11Q281 -11 278 -11T272 -7T267 2T263 21Q245 94 195 151T73 236Q58 242 55 247Q55 254 59 257T73 264Q144 292 194 349T263 479'],
  
      // UP DOWN ARROW
      0x2195: [772,272,500,17,483,'27 492Q17 492 17 511Q17 515 17 517T17 522T19 525T20 528T22 530T26 531T30 532T36 534Q80 545 120 572T180 627Q210 664 223 701T238 755T250 772T261 762Q261 757 264 741T282 691T319 628Q352 589 390 566T454 536L479 529Q483 525 483 511Q483 491 472 491Q467 491 458 494Q342 526 277 623L270 633V-133L277 -123Q307 -77 353 -45T430 -2T473 8Q483 8 483 -11Q483 -15 483 -17T483 -22T481 -25T480 -28T478 -30T474 -31T470 -32T464 -34Q407 -49 364 -84T300 -157T270 -223T261 -262Q259 -272 250 -272Q242 -272 239 -255T223 -201T180 -127Q169 -112 155 -99T125 -76T95 -58T67 -45T44 -36T27 -31L21 -29Q17 -25 17 -11Q17 9 28 9Q33 9 42 6Q158 -26 223 -123L230 -133V633L223 623Q192 577 146 545T70 502T27 492'],
  
      // NORTH WEST ARROW
      0x2196: [720,195,1000,29,944,'204 662Q257 662 301 676T369 705T394 720Q398 720 407 711T417 697Q417 688 389 671T310 639T212 623Q176 623 153 628Q151 628 221 557T546 232Q942 -164 943 -168Q944 -170 944 -174Q944 -182 938 -188T924 -195Q922 -195 916 -193Q912 -191 517 204Q440 281 326 394T166 553L121 598Q126 589 126 541Q126 438 70 349Q59 332 52 332Q48 332 39 341T29 355Q29 358 38 372T57 407T77 464T86 545Q86 583 78 614T63 663T55 683Q55 693 65 693Q73 693 82 688Q136 662 204 662'],
  
      // NORTH EAST ARROW
      0x2197: [720,195,1000,55,971,'582 697Q582 701 591 710T605 720Q607 720 630 706T697 677T795 662Q830 662 863 670T914 686T934 694Q942 694 944 685Q944 680 936 663T921 615T913 545Q913 490 927 446T956 379T970 355Q970 351 961 342T947 332Q940 332 929 349Q874 436 874 541Q874 590 878 598L832 553Q787 508 673 395T482 204Q87 -191 83 -193Q77 -195 75 -195Q67 -195 61 -189T55 -174Q55 -170 56 -168Q58 -164 453 232Q707 487 777 557T847 628Q824 623 787 623Q689 623 599 679Q582 690 582 697'],
  
      // SOUTH EAST ARROW
      0x2198: [695,220,1000,55,970,'55 675Q55 683 60 689T75 695Q77 695 83 693Q87 691 482 296Q532 246 605 174T717 62T799 -20T859 -80T878 -97Q874 -93 874 -41Q874 64 929 151Q940 168 947 168Q951 168 960 159T970 145Q970 143 956 121T928 54T913 -45Q913 -83 920 -114T936 -163T944 -185Q942 -194 934 -194Q932 -194 914 -186T864 -170T795 -162Q743 -162 698 -176T630 -205T605 -220Q601 -220 592 -211T582 -197Q582 -187 611 -170T691 -138T787 -123Q824 -123 847 -128Q848 -128 778 -57T453 268Q58 664 56 668Q55 670 55 675'],
  
      // SOUTH WEST ARROW
      0x2199: [695,220,1000,29,944,'126 -41Q126 -92 121 -97Q121 -98 139 -80T200 -20T281 61T394 173T517 296Q909 690 916 693Q922 695 924 695Q932 695 938 689T944 674Q944 670 943 668Q942 664 546 268Q292 13 222 -57T153 -128Q176 -123 212 -123Q310 -123 400 -179Q417 -190 417 -197Q417 -201 408 -210T394 -220Q392 -220 369 -206T302 -177T204 -162Q131 -162 67 -194Q63 -195 59 -192T55 -183Q55 -180 62 -163T78 -115T86 -45Q86 10 72 54T44 120T29 145Q29 149 38 158T52 168Q59 168 70 151Q126 62 126 -41'],
  
      // RIGHTWARDS ARROW FROM BAR
      0x21A6: [511,11,1000,54,944,'95 155V109Q95 83 92 73T75 63Q61 63 58 74T54 130Q54 140 54 180T55 250Q55 421 57 425Q61 437 75 437Q88 437 91 428T95 393V345V270H835Q719 357 692 493Q692 494 692 496T691 499Q691 511 708 511H711Q720 511 723 510T729 506T732 497T735 481T743 456Q765 389 816 336T935 261Q944 258 944 250Q944 244 939 241T915 231T877 212Q836 186 806 152T761 85T740 35T732 4Q730 -6 727 -8T711 -11Q691 -11 691 0Q691 7 696 25Q728 151 835 230H95V155'],
  
      // LEFTWARDS ARROW WITH HOOK
      0x21A9: [511,11,1126,55,1070,'903 424T903 444T929 464Q976 464 1023 434T1070 347Q1070 316 1055 292T1016 256T971 237T929 230H165Q167 228 182 216T211 189T244 152T277 96T303 25Q308 7 308 0Q308 -11 288 -11Q281 -11 278 -11T272 -7T267 2T263 21Q245 94 195 151T73 236Q58 242 55 247Q55 254 59 257T73 264Q121 283 158 314T215 375T247 434T264 480L267 497Q269 503 270 505T275 509T288 511Q308 511 308 500Q308 493 303 475Q293 438 278 406T246 352T215 315T185 287T165 270H926Q929 270 941 271T960 275T978 280T998 290T1015 307Q1030 325 1030 347Q1030 355 1027 364T1014 387T983 411T929 424H928Q903 424 903 444'],
  
      // RIGHTWARDS ARROW WITH HOOK
      0x21AA: [511,11,1126,55,1070,'55 347Q55 380 72 404T113 441T159 458T197 464Q222 464 222 444Q222 429 204 426T157 417T110 387Q95 369 95 347Q95 339 98 330T111 307T142 283T196 270H961Q845 357 818 493Q818 494 818 496T817 499Q817 511 834 511H837Q846 511 849 510T855 506T858 497T861 481T869 456Q891 389 942 336T1061 261Q1070 258 1070 250Q1070 244 1065 241T1041 231T1003 212Q962 186 932 152T887 85T866 35T858 4Q856 -6 853 -8T837 -11Q817 -11 817 0Q817 7 822 25Q854 151 961 230H196Q149 230 102 260T55 347'],
  
      // LEFTWARDS HARPOON WITH BARB UPWARDS
      0x21BC: [511,-230,1000,55,944,'62 230Q56 236 55 244Q55 252 57 255T69 265Q114 292 151 326T208 391T243 448T265 491T273 509Q276 511 288 511Q304 511 306 505Q309 501 303 484Q293 456 279 430T251 383T223 344T196 313T173 291T156 276L148 270H929Q944 261 944 250T929 230H62'],
  
      // LEFTWARDS HARPOON WITH BARB DOWNWARDS
      0x21BD: [270,11,1000,55,944,'55 256Q56 264 62 270H929Q944 261 944 250T929 230H148Q149 229 165 215T196 185T231 145T270 87T303 16Q309 -1 306 -5Q304 -11 288 -11Q279 -11 276 -10T269 -4T264 10T253 36T231 75Q172 173 69 235Q59 242 57 245T55 256'],
  
      // RIGHTWARDS HARPOON WITH BARB UPWARDS
      0x21C0: [511,-230,1000,56,945,'691 500Q691 511 711 511Q720 511 723 510T730 504T735 490T746 464T768 425Q796 378 835 339T897 285T933 263Q941 258 942 256T944 245T937 230H70Q56 237 56 250T70 270H852Q802 308 762 364T707 455T691 500'],
  
      // RIGHTWARDS HARPOON WITH BARB DOWNWARDS
      0x21C1: [270,11,1000,56,944,'56 237T56 250T70 270H937Q944 263 944 256Q944 251 944 250T943 246T940 242T933 238Q794 153 734 7Q729 -7 726 -9T711 -11Q695 -11 693 -5Q690 -1 696 16Q721 84 763 139T852 230H70Q56 237 56 250'],
  
      // RIGHTWARDS HARPOON OVER LEFTWARDS HARPOON
      0x21CC: [671,11,1000,55,945,'691 660Q691 671 711 671Q720 671 723 670T730 664T735 650T746 624T768 585Q797 538 836 499T897 445T933 423Q941 418 942 416T944 405T937 390H70Q56 397 56 410T70 430H852Q802 468 762 524T707 615T691 660ZM55 256Q56 264 62 270H929Q944 261 944 250T929 230H148Q149 229 165 215T196 185T231 145T270 87T303 16Q309 -1 306 -5Q304 -11 288 -11Q279 -11 276 -10T269 -4T264 10T253 36T231 75Q172 173 69 235Q59 242 57 245T55 256'],
  
      // LEFTWARDS DOUBLE ARROW
      0x21D0: [525,24,1000,56,945,'944 153Q944 140 929 133H318L328 123Q379 69 414 0Q419 -13 419 -17Q419 -24 399 -24Q388 -24 385 -23T377 -12Q332 77 253 144T72 237Q62 240 59 242T56 250T59 257T70 262T89 268T119 278T160 296Q303 366 377 512Q382 522 385 523T401 525Q419 524 419 515Q419 510 414 500Q379 431 328 377L318 367H929Q944 359 944 347Q944 336 930 328L602 327H274L264 319Q225 289 147 250Q148 249 165 241T210 217T264 181L274 173H930Q931 172 933 171T936 169T938 167T941 164T942 162T943 158T944 153'],
  
      // UPWARDS DOUBLE ARROW
      0x21D1: [694,194,611,31,579,'228 -179Q227 -180 226 -182T223 -186T221 -189T218 -192T214 -193T208 -194Q196 -194 189 -181L188 125V430L176 419Q122 369 59 338Q46 330 40 330Q38 330 31 337V350Q31 362 33 365T46 374Q60 381 77 390T128 426T190 484T247 567T292 677Q295 688 298 692Q302 694 305 694Q313 694 318 677Q334 619 363 568T420 485T481 427T532 391T564 374Q575 368 577 365T579 350V337Q572 330 570 330Q564 330 551 338Q487 370 435 419L423 430L422 125V-181Q409 -194 401 -194Q397 -194 394 -193T388 -189T385 -184T382 -180V-177V475L373 487Q331 541 305 602Q304 601 300 591T290 571T278 548T260 519T238 488L229 476L228 148V-179'],
  
      // RIGHTWARDS DOUBLE ARROW
      0x21D2: [525,24,1000,56,944,'580 514Q580 525 596 525Q601 525 604 525T609 525T613 524T615 523T617 520T619 517T622 512Q659 438 720 381T831 300T927 263Q944 258 944 250T935 239T898 228T840 204Q696 134 622 -12Q618 -21 615 -22T600 -24Q580 -24 580 -17Q580 -13 585 0Q620 69 671 123L681 133H70Q56 140 56 153Q56 168 72 173H725L735 181Q774 211 852 250Q851 251 834 259T789 283T735 319L725 327H72Q56 332 56 347Q56 360 70 367H681L671 377Q638 412 609 458T580 514'],
  
      // DOWNWARDS DOUBLE ARROW
      0x21D3: [694,194,611,31,579,'401 694Q412 694 422 681V375L423 70L435 81Q487 130 551 162Q564 170 570 170Q572 170 579 163V150Q579 138 577 135T564 126Q541 114 518 99T453 48T374 -46T318 -177Q313 -194 305 -194T293 -178T272 -119T225 -31Q158 70 46 126Q35 132 33 135T31 150V163Q38 170 40 170Q46 170 59 162Q122 131 176 81L188 70V375L189 681Q199 694 208 694Q219 694 228 680V352L229 25L238 12Q279 -42 305 -102Q344 -23 373 13L382 25V678Q387 692 401 694'],
  
      // LEFT RIGHT DOUBLE ARROW
      0x21D4: [526,25,1000,33,966,'308 524Q318 526 323 526Q340 526 340 514Q340 507 336 499Q326 476 314 454T292 417T274 391T260 374L255 368Q255 367 500 367Q744 367 744 368L739 374Q734 379 726 390T707 416T685 453T663 499Q658 511 658 515Q658 525 680 525Q687 524 690 523T695 519T701 507Q766 359 902 287Q921 276 939 269T961 259T966 250Q966 246 965 244T960 240T949 236T930 228T902 213Q763 137 701 -7Q697 -16 695 -19T690 -23T680 -25Q658 -25 658 -15Q658 -11 663 1Q673 24 685 46T707 83T725 109T739 126L744 132Q744 133 500 133Q255 133 255 132L260 126Q265 121 273 110T292 84T314 47T336 1Q341 -11 341 -15Q341 -25 319 -25Q312 -24 309 -23T304 -19T298 -7Q233 141 97 213Q83 221 70 227T51 235T41 239T35 243T34 250T35 256T40 261T51 265T70 273T97 287Q235 363 299 509Q305 522 308 524ZM792 319L783 327H216Q183 294 120 256L110 250L120 244Q173 212 207 181L216 173H783L792 181Q826 212 879 244L889 250L879 256Q826 288 792 319'],
  
      // UP DOWN DOUBLE ARROW
      0x21D5: [772,272,611,31,579,'290 755Q298 772 305 772T318 757T343 706T393 633Q431 588 473 558T545 515T579 497V484Q579 464 570 464Q564 464 550 470Q485 497 423 550L422 400V100L423 -50Q485 3 550 30Q565 36 570 36Q579 36 579 16V3Q575 -1 549 -12T480 -53T393 -132Q361 -172 342 -208T318 -258T305 -272T293 -258T268 -208T217 -132Q170 -80 128 -51T61 -12T31 3V16Q31 36 40 36Q46 36 61 30Q86 19 109 6T146 -18T173 -38T188 -50V550Q186 549 173 539T147 519T110 495T61 470Q46 464 40 464Q31 464 31 484V497Q34 500 63 513T135 557T217 633Q267 692 290 755ZM374 598Q363 610 351 625T332 651T316 676T305 695L294 676Q282 657 267 636T236 598L228 589V-89L236 -98Q247 -110 259 -125T278 -151T294 -176T305 -195L316 -176Q328 -157 343 -136T374 -98L382 -89V589L374 598'],
  
      // FOR ALL
      0x2200: [694,22,556,0,556,'0 673Q0 684 7 689T20 694Q32 694 38 680T82 567L126 451H430L473 566Q483 593 494 622T512 668T519 685Q524 694 538 694Q556 692 556 674Q556 670 426 329T293 -15Q288 -22 278 -22T263 -15Q260 -11 131 328T0 673ZM414 410Q414 411 278 411T142 410L278 55L414 410'],
  
      // PARTIAL DIFFERENTIAL
      0x2202: [715,22,531,42,567,'202 508Q179 508 169 520T158 547Q158 557 164 577T185 624T230 675T301 710L333 715H345Q378 715 384 714Q447 703 489 661T549 568T566 457Q566 362 519 240T402 53Q321 -22 223 -22Q123 -22 73 56Q42 102 42 148V159Q42 276 129 370T322 465Q383 465 414 434T455 367L458 378Q478 461 478 515Q478 603 437 639T344 676Q266 676 223 612Q264 606 264 572Q264 547 246 528T202 508ZM430 306Q430 372 401 400T333 428Q270 428 222 382Q197 354 183 323T150 221Q132 149 132 116Q132 21 232 21Q244 21 250 22Q327 35 374 112Q389 137 409 196T430 306'],
  
      // THERE EXISTS
      0x2203: [694,0,556,56,500,'56 661T56 674T70 694H487Q497 686 500 679V15Q497 10 487 1L279 0H70Q56 7 56 20T70 40H460V327H84Q70 334 70 347T84 367H460V654H70Q56 661 56 674'],
  
      // EMPTY SET
      0x2205: [772,78,500,39,460,'331 696Q335 708 339 722T345 744T350 759T357 769T367 772Q374 772 381 767T388 754Q388 746 377 712L366 673L378 661Q460 575 460 344Q460 281 456 234T432 126T373 27Q319 -22 250 -22Q214 -22 180 -7Q168 -3 168 -4L159 -33Q148 -71 142 -75Q138 -78 132 -78Q124 -78 118 -72T111 -60Q111 -52 122 -18L133 21L125 29Q39 111 39 344Q39 596 137 675Q187 716 251 716Q265 716 278 714T296 710T315 703T331 696ZM276 676Q264 679 246 679Q196 679 159 631Q134 597 128 536T121 356Q121 234 127 174T151 80L234 366Q253 430 275 506T308 618L318 654Q318 656 294 669L276 676ZM181 42Q207 16 250 16Q291 16 324 47Q354 78 366 136T378 356Q378 470 372 528T349 616L348 613Q348 611 264 326L181 42'],
  
      // NABLA
      0x2207: [683,33,833,46,786,'46 676Q46 679 51 683H781Q786 679 786 676Q786 674 617 326T444 -26Q439 -33 416 -33T388 -26Q385 -22 216 326T46 676ZM697 596Q697 597 445 597T193 596Q195 591 319 336T445 80L697 596'],
  
      // ELEMENT OF
      0x2208: [541,41,667,84,583,'84 250Q84 372 166 450T360 539Q361 539 377 539T419 540T469 540H568Q583 532 583 520Q583 511 570 501L466 500Q355 499 329 494Q280 482 242 458T183 409T147 354T129 306T124 272V270H568Q583 262 583 250T568 230H124V228Q124 207 134 177T167 112T231 48T328 7Q355 1 466 0H570Q583 -10 583 -20Q583 -32 568 -40H471Q464 -40 446 -40T417 -41Q262 -41 172 45Q84 127 84 250'],
  
      // stix-negated (vert) set membership, variant
      0x2209: [716,215,667,84,584,'196 25Q84 109 84 250Q84 372 166 450T360 539Q361 539 375 539T413 540T460 540L547 707Q550 716 563 716Q570 716 575 712T581 703T583 696T505 540H568Q583 532 583 520Q583 511 570 501L484 500L366 270H568Q583 262 583 250T568 230H346L247 38Q284 16 328 7Q355 1 466 0H570Q583 -10 583 -20Q583 -32 568 -40H471Q464 -40 447 -40T419 -41Q304 -41 228 3Q117 -211 115 -212Q111 -215 104 -215T92 -212T86 -204T84 -197Q84 -190 89 -183L196 25ZM214 61L301 230H124V228Q124 196 147 147T214 61ZM321 270L440 500Q353 499 329 494Q280 482 242 458T183 409T147 354T129 306T124 272V270H321'],
  
      // CONTAINS AS MEMBER
      0x220B: [541,40,667,83,582,'83 520Q83 532 98 540H195Q202 540 220 540T249 541Q404 541 494 455Q582 374 582 250Q582 165 539 99T434 0T304 -39Q297 -40 195 -40H98Q83 -32 83 -20Q83 -10 96 0H200Q311 1 337 6Q369 14 401 28Q422 39 445 55Q484 85 508 127T537 191T542 228V230H98Q84 237 84 250T98 270H542V272Q542 280 539 295T527 336T497 391T445 445Q422 461 401 472Q386 479 374 483T347 491T325 495T298 498T273 499T239 500T200 500L96 501Q83 511 83 520'],
  
      // MINUS SIGN
      0x2212: [270,-230,778,84,694,'84 237T84 250T98 270H679Q694 262 694 250T679 230H98Q84 237 84 250'],
  
      // MINUS-OR-PLUS SIGN
      0x2213: [500,166,778,56,722,'56 467T56 480T70 500H707Q722 492 722 480T707 460H409V187H707Q722 179 722 167Q722 154 707 147H409V0V-93Q409 -144 406 -155T389 -166Q376 -166 372 -155T368 -105Q368 -96 368 -62T369 -2V147H70Q56 154 56 167T70 187H369V460H70Q56 467 56 480'],
  
      // DIVISION SLASH
      0x2215: [750,250,500,56,444,'423 750Q432 750 438 744T444 730Q444 725 271 248T92 -240Q85 -250 75 -250Q68 -250 62 -245T56 -231Q56 -221 230 257T407 740Q411 750 423 750'],
  
      // SET MINUS
      0x2216: [750,250,500,56,444,'56 731Q56 740 62 745T75 750Q85 750 92 740Q96 733 270 255T444 -231Q444 -239 438 -244T424 -250Q414 -250 407 -240Q404 -236 230 242T56 731'],
  
      // ASTERISK OPERATOR
      0x2217: [465,-35,500,64,435,'229 286Q216 420 216 436Q216 454 240 464Q241 464 245 464T251 465Q263 464 273 456T283 436Q283 419 277 356T270 286L328 328Q384 369 389 372T399 375Q412 375 423 365T435 338Q435 325 425 315Q420 312 357 282T289 250L355 219L425 184Q434 175 434 161Q434 146 425 136T401 125Q393 125 383 131T328 171L270 213Q283 79 283 63Q283 53 276 44T250 35Q231 35 224 44T216 63Q216 80 222 143T229 213L171 171Q115 130 110 127Q106 124 100 124Q87 124 76 134T64 161Q64 166 64 169T67 175T72 181T81 188T94 195T113 204T138 215T170 230T210 250L74 315Q65 324 65 338Q65 353 74 363T98 374Q106 374 116 368T171 328L229 286'],
  
      // RING OPERATOR
      0x2218: [444,-55,500,55,444,'55 251Q55 328 112 386T249 444T386 388T444 249Q444 171 388 113T250 55Q170 55 113 112T55 251ZM245 403Q188 403 142 361T96 250Q96 183 141 140T250 96Q284 96 313 109T354 135T375 160Q403 197 403 250Q403 313 360 358T245 403'],
  
      // BULLET OPERATOR
      0x2219: [444,-55,500,55,444,'55 251Q55 328 112 386T249 444T386 388T444 249Q444 171 388 113T250 55Q170 55 113 112T55 251'],
  
      // SQUARE ROOT
      0x221A: [800,200,833,71,853,'95 178Q89 178 81 186T72 200T103 230T169 280T207 309Q209 311 212 311H213Q219 311 227 294T281 177Q300 134 312 108L397 -77Q398 -77 501 136T707 565T814 786Q820 800 834 800Q841 800 846 794T853 782V776L620 293L385 -193Q381 -200 366 -200Q357 -200 354 -197Q352 -195 256 15L160 225L144 214Q129 202 113 190T95 178'],
  
      // PROPORTIONAL TO
      0x221D: [442,11,778,56,722,'56 124T56 216T107 375T238 442Q260 442 280 438T319 425T352 407T382 385T406 361T427 336T442 315T455 297T462 285L469 297Q555 442 679 442Q687 442 722 437V398H718Q710 400 694 400Q657 400 623 383T567 343T527 294T503 253T495 235Q495 231 520 192T554 143Q625 44 696 44Q717 44 719 46H722V-5Q695 -11 678 -11Q552 -11 457 141Q455 145 454 146L447 134Q362 -11 235 -11Q157 -11 107 56ZM93 213Q93 143 126 87T220 31Q258 31 292 48T349 88T389 137T413 178T421 196Q421 200 396 239T362 288Q322 345 288 366T213 387Q163 387 128 337T93 213'],
  
      // INFINITY
      0x221E: [442,11,1000,55,944,'55 217Q55 305 111 373T254 442Q342 442 419 381Q457 350 493 303L507 284L514 294Q618 442 747 442Q833 442 888 374T944 214Q944 128 889 59T743 -11Q657 -11 580 50Q542 81 506 128L492 147L485 137Q381 -11 252 -11Q166 -11 111 57T55 217ZM907 217Q907 285 869 341T761 397Q740 397 720 392T682 378T648 359T619 335T594 310T574 285T559 263T548 246L543 238L574 198Q605 158 622 138T664 94T714 61T765 51Q827 51 867 100T907 217ZM92 214Q92 145 131 89T239 33Q357 33 456 193L425 233Q364 312 334 337Q285 380 233 380Q171 380 132 331T92 214'],
  
      // ANGLE
      0x2220: [694,0,722,55,666,'71 0L68 2Q65 3 63 5T58 11T55 20Q55 22 57 28Q67 43 346 361Q397 420 474 508Q595 648 616 671T647 694T661 688T666 674Q666 668 663 663Q662 662 627 622T524 503T390 350L120 41L386 40H653Q666 30 666 20Q666 8 651 0H71'],
  
      // DIVIDES
      0x2223: [750,249,278,119,159,'139 -249H137Q125 -249 119 -235V251L120 737Q130 750 139 750Q152 750 159 735V-235Q151 -249 141 -249H139'],
  
      // PARALLEL TO
      0x2225: [750,250,500,132,368,'133 736Q138 750 153 750Q164 750 170 739Q172 735 172 250T170 -239Q164 -250 152 -250Q144 -250 138 -244L137 -243Q133 -241 133 -179T132 250Q132 731 133 736ZM329 739Q334 750 346 750Q353 750 361 744L362 743Q366 741 366 679T367 250T367 -178T362 -243L361 -244Q355 -250 347 -250Q335 -250 329 -239Q327 -235 327 250T329 739'],
  
      // LOGICAL AND
      0x2227: [598,22,667,55,611,'318 591Q325 598 333 598Q344 598 348 591Q349 590 414 445T545 151T611 -4Q609 -22 591 -22Q588 -22 586 -21T581 -20T577 -17T575 -13T572 -9T570 -4L333 528L96 -4Q87 -20 80 -21Q78 -22 75 -22Q57 -22 55 -4Q55 2 120 150T251 444T318 591'],
  
      // LOGICAL OR
      0x2228: [598,22,667,55,611,'55 580Q56 587 61 592T75 598Q86 598 96 580L333 48L570 580Q579 596 586 597Q588 598 591 598Q609 598 611 580Q611 574 546 426T415 132T348 -15Q343 -22 333 -22T318 -15Q317 -14 252 131T121 425T55 580'],
  
      // stix-intersection, serifs
      0x2229: [598,22,667,55,611,'88 -21T75 -21T55 -7V200Q55 231 55 280Q56 414 60 428Q61 430 61 431Q77 500 152 549T332 598Q443 598 522 544T610 405Q611 399 611 194V-7Q604 -22 591 -22Q582 -22 572 -9L570 405Q563 433 556 449T529 485Q498 519 445 538T334 558Q251 558 179 518T96 401Q95 396 95 193V-7Q88 -21 75 -21'],
  
      // stix-union, serifs
      0x222A: [598,22,667,55,611,'591 598H592Q604 598 611 583V376Q611 345 611 296Q610 162 606 148Q605 146 605 145Q586 68 507 23T333 -22Q268 -22 209 -1T106 66T56 173Q55 180 55 384L56 585Q66 598 75 598Q85 598 95 585V378L96 172L98 162Q112 95 181 57T332 18Q415 18 487 58T570 175Q571 180 571 383V583Q579 598 591 598'],
  
      // INTEGRAL
      0x222B: [716,216,417,55,472,'151 -112Q151 -150 106 -161Q106 -165 114 -172T134 -179Q155 -179 170 -146Q181 -120 188 -64T206 101T232 310Q256 472 277 567Q308 716 392 716Q434 716 453 681T472 613Q472 590 458 577T424 564Q404 564 390 578T376 612Q376 650 421 661Q421 663 418 667T407 675T393 679Q387 679 380 675Q360 665 350 619T326 438Q302 190 253 -57Q235 -147 201 -186Q174 -213 138 -216Q93 -216 74 -181T55 -113Q55 -91 69 -78T103 -64Q123 -64 137 -78T151 -112'],
  
      // TILDE OPERATOR
      0x223C: [367,-133,778,55,722,'55 166Q55 241 101 304T222 367Q260 367 296 349T362 304T421 252T484 208T554 189Q616 189 655 236T694 338Q694 350 698 358T708 367Q722 367 722 334Q722 260 677 197T562 134H554Q517 134 481 152T414 196T355 248T292 293T223 311Q179 311 145 286Q109 257 96 218T80 156T69 133Q55 133 55 166'],
  
      // WREATH PRODUCT
      0x2240: [583,83,278,55,222,'55 569Q55 583 83 583Q122 583 151 565T194 519T215 464T222 411Q222 360 194 304T139 193T111 89Q111 38 134 -7T195 -55Q222 -57 222 -69Q222 -83 189 -83Q130 -83 93 -33T55 90Q55 130 72 174T110 252T148 328T166 411Q166 462 144 507T83 555Q55 556 55 569'],
  
      // ASYMPTOTICALLY EQUAL TO
      0x2243: [464,-36,778,55,722,'55 283Q55 356 103 409T217 463Q262 463 297 447T395 382Q431 355 446 344T493 320T554 307H558Q613 307 652 344T694 433Q694 464 708 464T722 432Q722 356 673 304T564 251H554Q510 251 465 275T387 329T310 382T223 407H219Q164 407 122 367Q91 333 85 295T76 253T69 250Q55 250 55 283ZM56 56Q56 71 72 76H706Q722 70 722 56Q722 44 707 36H70Q56 43 56 56'],
  
      // APPROXIMATELY EQUAL TO
      0x2245: [589,-22,1000,55,722,'55 388Q55 463 101 526T222 589Q260 589 296 571T362 526T421 474T484 430T554 411Q616 411 655 458T694 560Q694 572 698 580T708 589Q722 589 722 556Q722 482 677 419T562 356H554Q517 356 481 374T414 418T355 471T292 515T223 533Q179 533 145 508Q109 479 96 440T80 378T69 355Q55 355 55 388ZM56 236Q56 249 70 256H707Q722 248 722 236Q722 225 708 217L390 216H72Q56 221 56 236ZM56 42Q56 57 72 62H708Q722 52 722 42Q722 30 707 22H70Q56 29 56 42'],
  
      // ALMOST EQUAL TO
      0x2248: [483,-55,778,55,722,'55 319Q55 360 72 393T114 444T163 472T205 482Q207 482 213 482T223 483Q262 483 296 468T393 413L443 381Q502 346 553 346Q609 346 649 375T694 454Q694 465 698 474T708 483Q722 483 722 452Q722 386 675 338T555 289Q514 289 468 310T388 357T308 404T224 426Q164 426 125 393T83 318Q81 289 69 289Q55 289 55 319ZM55 85Q55 126 72 159T114 210T163 238T205 248Q207 248 213 248T223 249Q262 249 296 234T393 179L443 147Q502 112 553 112Q609 112 649 141T694 220Q694 249 708 249T722 217Q722 153 675 104T555 55Q514 55 468 76T388 123T308 170T224 192Q164 192 125 159T83 84Q80 55 69 55Q55 55 55 85'],
  
      // EQUIVALENT TO
      0x224D: [484,-16,778,55,722,'55 464Q55 471 60 477T74 484Q80 484 108 464T172 420T268 376T389 356Q436 356 483 368T566 399T630 436T675 467T695 482Q701 484 703 484Q711 484 716 478T722 464Q722 454 707 442Q550 316 389 316Q338 316 286 329T195 362T124 402T76 437T57 456Q55 462 55 464ZM57 45Q66 58 109 88T230 151T381 183Q438 183 494 168T587 135T658 94T703 61T720 45Q722 39 722 36Q722 28 717 22T703 16Q697 16 669 36T606 80T510 124T389 144Q341 144 294 132T211 101T147 64T102 33T82 18Q76 16 74 16Q66 16 61 22T55 36Q55 39 57 45'],
  
      // APPROACHES THE LIMIT
      0x2250: [670,-133,778,56,722,'56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153ZM329 610Q329 634 346 652T389 670Q413 670 431 654T450 611Q450 586 433 568T390 550T347 567T329 610'],
  
      // stix-not (vert) equals
      0x2260: [716,215,778,56,722,'166 -215T159 -215T147 -212T141 -204T139 -197Q139 -190 144 -183L306 133H70Q56 140 56 153Q56 168 72 173H327L406 327H72Q56 332 56 347Q56 360 70 367H426Q597 702 602 707Q605 716 618 716Q625 716 630 712T636 703T638 696Q638 692 471 367H707Q722 359 722 347Q722 336 708 328L451 327L371 173H708Q722 163 722 153Q722 140 707 133H351Q175 -210 170 -212Q166 -215 159 -215'],
  
      // IDENTICAL TO
      0x2261: [464,-36,778,56,722,'56 444Q56 457 70 464H707Q722 456 722 444Q722 430 706 424H72Q56 429 56 444ZM56 237T56 250T70 270H707Q722 262 722 250T707 230H70Q56 237 56 250ZM56 56Q56 71 72 76H706Q722 70 722 56Q722 44 707 36H70Q56 43 56 56'],
  
      // LESS-THAN OR EQUAL TO
      0x2264: [636,138,778,83,694,'674 636Q682 636 688 630T694 615T687 601Q686 600 417 472L151 346L399 228Q687 92 691 87Q694 81 694 76Q694 58 676 56H670L382 192Q92 329 90 331Q83 336 83 348Q84 359 96 365Q104 369 382 500T665 634Q669 636 674 636ZM84 -118Q84 -108 99 -98H678Q694 -104 694 -118Q694 -130 679 -138H98Q84 -131 84 -118'],
  
      // GREATER-THAN OR EQUAL TO
      0x2265: [636,138,778,82,694,'83 616Q83 624 89 630T99 636Q107 636 253 568T543 431T687 361Q694 356 694 346T687 331Q685 329 395 192L107 56H101Q83 58 83 76Q83 77 83 79Q82 86 98 95Q117 105 248 167Q326 204 378 228L626 346L360 472Q291 505 200 548Q112 589 98 597T83 616ZM84 -118Q84 -108 99 -98H678Q694 -104 694 -118Q694 -130 679 -138H98Q84 -131 84 -118'],
  
      // MUCH LESS-THAN
      0x226A: [568,67,1000,56,944,'639 -48Q639 -54 634 -60T619 -67H618Q612 -67 536 -26Q430 33 329 88Q61 235 59 239Q56 243 56 250T59 261Q62 266 336 415T615 567L619 568Q622 567 625 567Q639 562 639 548Q639 540 633 534Q632 532 374 391L117 250L374 109Q632 -32 633 -34Q639 -40 639 -48ZM944 -48Q944 -54 939 -60T924 -67H923Q917 -67 841 -26Q735 33 634 88Q366 235 364 239Q361 243 361 250T364 261Q367 266 641 415T920 567L924 568Q927 567 930 567Q944 562 944 548Q944 540 938 534Q937 532 679 391L422 250L679 109Q937 -32 938 -34Q944 -40 944 -48'],
  
      // MUCH GREATER-THAN
      0x226B: [567,67,1000,55,944,'55 539T55 547T60 561T74 567Q81 567 207 498Q297 449 365 412Q633 265 636 261Q639 255 639 250Q639 241 626 232Q614 224 365 88Q83 -65 79 -66Q76 -67 73 -67Q65 -67 60 -61T55 -47Q55 -39 61 -33Q62 -33 95 -15T193 39T320 109L321 110H322L323 111H324L325 112L326 113H327L329 114H330L331 115H332L333 116L334 117H335L336 118H337L338 119H339L340 120L341 121H342L343 122H344L345 123H346L347 124L348 125H349L351 126H352L353 127H354L355 128L356 129H357L358 130H359L360 131H361L362 132L363 133H364L365 134H366L367 135H368L369 136H370L371 137L372 138H373L374 139H375L376 140L378 141L576 251Q63 530 62 533Q55 539 55 547ZM360 539T360 547T365 561T379 567Q386 567 512 498Q602 449 670 412Q938 265 941 261Q944 255 944 250Q944 241 931 232Q919 224 670 88Q388 -65 384 -66Q381 -67 378 -67Q370 -67 365 -61T360 -47Q360 -39 366 -33Q367 -33 400 -15T498 39T625 109L626 110H627L628 111H629L630 112L631 113H632L634 114H635L636 115H637L638 116L639 117H640L641 118H642L643 119H644L645 120L646 121H647L648 122H649L650 123H651L652 124L653 125H654L656 126H657L658 127H659L660 128L661 129H662L663 130H664L665 131H666L667 132L668 133H669L670 134H671L672 135H673L674 136H675L676 137L677 138H678L679 139H680L681 140L683 141L881 251Q368 530 367 533Q360 539 360 547'],
  
      // PRECEDES
      0x227A: [539,41,778,84,694,'84 249Q84 262 91 266T117 270Q120 270 126 270T137 269Q388 273 512 333T653 512Q657 539 676 539Q685 538 689 532T694 520V515Q689 469 672 431T626 366T569 320T500 286T435 265T373 249Q379 248 404 242T440 233T477 221T533 199Q681 124 694 -17Q694 -41 674 -41Q658 -41 653 -17Q646 41 613 84T533 154T418 197T284 220T137 229H114Q104 229 98 230T88 235T84 249'],
  
      // SUCCEEDS
      0x227B: [539,41,778,83,694,'84 517Q84 539 102 539Q115 539 119 529T125 503T137 459T171 404Q277 275 640 269H661Q694 269 694 249T661 229H640Q526 227 439 214T283 173T173 98T124 -17Q118 -41 103 -41Q83 -41 83 -17Q88 29 105 67T151 132T208 178T277 212T342 233T404 249Q401 250 380 254T345 263T302 276T245 299Q125 358 92 468Q84 502 84 517'],
  
      // SUBSET OF
      0x2282: [541,41,778,84,694,'84 250Q84 372 166 450T360 539Q361 539 370 539T395 539T430 540T475 540T524 540H679Q694 532 694 520Q694 511 681 501L522 500H470H441Q366 500 338 496T266 472Q244 461 224 446T179 404T139 337T124 250V245Q124 157 185 89Q244 25 328 7Q348 2 366 2T522 0H681Q694 -10 694 -20Q694 -32 679 -40H526Q510 -40 480 -40T434 -41Q350 -41 289 -25T172 45Q84 127 84 250'],
  
      // SUPERSET OF
      0x2283: [541,40,778,83,693,'83 520Q83 532 98 540H251Q267 540 297 540T343 541Q427 541 488 525T605 455Q693 374 693 250Q693 165 650 99T545 0T415 -39Q407 -40 251 -40H98Q83 -32 83 -20Q83 -10 96 0H255H308H337Q412 0 439 4T512 28Q533 39 553 54T599 96T639 163T654 250Q654 341 592 411Q557 449 512 472Q468 491 439 495T335 500H306H255L96 501Q83 511 83 520'],
  
      // SUBSET OF OR EQUAL TO
      0x2286: [637,138,778,84,694,'84 346Q84 468 166 546T360 635Q361 635 370 635T395 635T430 636T475 636T524 636H679Q694 628 694 616Q694 607 681 597L522 596H470H441Q366 596 338 592T266 568Q244 557 224 542T179 500T139 433T124 346V341Q124 253 185 185Q244 121 328 103Q348 98 366 98T522 96H681Q694 86 694 76Q694 64 679 56H526Q510 56 480 56T434 55Q350 55 289 71T172 141Q84 223 84 346ZM104 -131T104 -118T118 -98H679Q694 -106 694 -118T679 -138H118Q104 -131 104 -118'],
  
      // SUPERSET OF OR EQUAL TO
      0x2287: [637,138,778,83,693,'83 616Q83 628 98 636H251Q267 636 297 636T343 637Q427 637 488 621T605 551Q693 470 693 346Q693 261 650 195T545 96T415 57Q407 56 251 56H98Q83 64 83 76Q83 86 96 96H255H308H337Q412 96 439 100T512 124Q533 135 553 150T599 192T639 259T654 346Q654 437 592 507Q557 545 512 568Q468 587 439 591T335 596H306H255L96 597Q83 607 83 616ZM84 -131T84 -118T98 -98H659Q674 -106 674 -118T659 -138H98Q84 -131 84 -118'],
  
      // MULTISET UNION
      0x228E: [598,22,667,55,611,'591 598H592Q604 598 611 583V376Q611 345 611 296Q610 162 606 148Q605 146 605 145Q586 68 507 23T333 -22Q268 -22 209 -1T106 66T56 173Q55 180 55 384L56 585Q66 598 75 598Q85 598 95 585V378L96 172L98 162Q112 95 181 57T332 18Q415 18 487 58T570 175Q571 180 571 383V583Q579 598 591 598ZM313 406Q313 417 313 435T312 459Q312 483 316 493T333 503T349 494T353 461V406V325H515Q516 325 519 323T527 316T531 305T527 294T520 287T515 285H353V204V152Q353 127 350 117T333 107T316 117T312 152Q312 158 312 175T313 204V285H151Q150 285 147 287T139 294T135 305T139 316T146 323T151 325H313V406'],
  
      // SQUARE IMAGE OF OR EQUAL TO
      0x2291: [636,138,778,84,714,'94 620Q98 632 110 636H699Q714 628 714 616T699 596H134V96H698Q714 90 714 76Q714 64 699 56H109Q104 59 95 69L94 344V620ZM84 -118Q84 -103 100 -98H698Q714 -104 714 -118Q714 -130 699 -138H98Q84 -131 84 -118'],
  
      // SQUARE ORIGINAL OF OR EQUAL TO
      0x2292: [636,138,778,64,694,'64 603T64 616T78 636H668Q675 633 683 623V69Q675 59 668 56H78Q64 63 64 76Q64 91 80 96H643V596H78Q64 603 64 616ZM64 -118Q64 -108 79 -98H678Q694 -104 694 -118Q694 -130 679 -138H78Q64 -131 64 -118'],
  
      // stix-square intersection, serifs
      0x2293: [598,0,667,61,605,'83 0Q79 0 76 1T71 3T67 6T65 9T63 13T61 16V301L62 585Q70 595 76 598H592Q602 590 605 583V15Q598 2 587 0Q583 0 580 1T575 3T571 6T569 9T567 13T565 16V558H101V15Q94 2 83 0'],
  
      // stix-square union, serifs
      0x2294: [598,0,667,61,605,'77 0Q65 4 61 16V301L62 585Q72 598 81 598Q94 598 101 583V40H565V583Q573 598 585 598Q598 598 605 583V15Q602 10 592 1L335 0H77'],
  
      // stix-circled plus (with rim)
      0x2295: [583,83,778,56,722,'56 250Q56 394 156 488T384 583Q530 583 626 485T722 250Q722 110 625 14T390 -83Q249 -83 153 14T56 250ZM364 542Q308 539 251 509T148 418T96 278V270H369V542H364ZM681 278Q675 338 650 386T592 462T522 509T458 535T412 542H409V270H681V278ZM96 222Q104 150 139 95T219 12T302 -29T366 -42H369V230H96V222ZM681 222V230H409V-42H412Q429 -42 456 -36T521 -10T590 37T649 113T681 222'],
  
      // CIRCLED MINUS
      0x2296: [583,83,778,56,722,'56 250Q56 394 156 488T384 583Q530 583 626 485T722 250Q722 110 625 14T390 -83Q249 -83 153 14T56 250ZM681 278Q669 385 591 463T381 542Q283 542 196 471T96 278V270H681V278ZM275 -42T388 -42T585 32T681 222V230H96V222Q108 107 191 33'],
  
      // stix-circled times (with rim)
      0x2297: [583,83,778,56,722,'56 250Q56 394 156 488T384 583Q530 583 626 485T722 250Q722 110 625 14T390 -83Q249 -83 153 14T56 250ZM582 471Q531 510 496 523Q446 542 381 542Q324 542 272 519T196 471L389 278L485 375L582 471ZM167 442Q95 362 95 250Q95 137 167 58L359 250L167 442ZM610 58Q682 138 682 250Q682 363 610 442L418 250L610 58ZM196 29Q209 16 230 2T295 -27T388 -42Q409 -42 429 -40T465 -33T496 -23T522 -11T544 1T561 13T574 22T582 29L388 222L196 29'],
  
      // CIRCLED DIVISION SLASH
      0x2298: [583,83,778,56,722,'56 250Q56 394 156 488T384 583Q530 583 626 485T722 250Q722 110 625 14T390 -83Q249 -83 153 14T56 250ZM582 471Q581 472 571 480T556 491T539 502T517 514T491 525T460 534T424 539T381 542Q272 542 184 460T95 251Q95 198 113 150T149 80L167 58L582 471ZM388 -42Q513 -42 597 44T682 250Q682 363 610 442L196 29Q209 16 229 2T295 -27T388 -42'],
  
      // CIRCLED DOT OPERATOR
      0x2299: [583,83,778,56,722,'56 250Q56 394 156 488T384 583Q530 583 626 485T722 250Q722 110 625 14T390 -83Q249 -83 153 14T56 250ZM682 250Q682 322 649 387T546 497T381 542Q272 542 184 459T95 250Q95 132 178 45T389 -42Q515 -42 598 45T682 250ZM311 250Q311 285 332 304T375 328Q376 328 382 328T392 329Q424 326 445 305T466 250Q466 217 445 195T389 172Q354 172 333 195T311 250'],
  
      // RIGHT TACK
      0x22A2: [695,0,611,55,555,'55 678Q55 679 56 681T58 684T61 688T65 691T70 693T77 694Q88 692 95 679V367H540Q555 359 555 347Q555 334 540 327H95V15Q88 2 77 0Q73 0 70 1T65 3T61 6T59 9T57 13T55 16V678'],
  
      // LEFT TACK
      0x22A3: [695,0,611,54,555,'515 678Q515 679 516 681T518 684T521 688T525 691T530 693T537 694Q548 692 555 679V15Q548 2 537 0Q533 0 530 1T525 3T521 6T519 9T517 13T515 16V327H71Q70 327 67 329T59 336T55 347T59 358T66 365T71 367H515V678'],
  
      // DOWN TACK
      0x22A4: [668,0,778,55,723,'55 642T55 648T59 659T66 666T71 668H708Q723 660 723 648T708 628H409V15Q402 2 391 0Q387 0 384 1T379 3T375 6T373 9T371 13T369 16V628H71Q70 628 67 630T59 637'],
  
      // UP TACK
      0x22A5: [669,0,778,54,723,'369 652Q369 653 370 655T372 658T375 662T379 665T384 667T391 668Q402 666 409 653V40H708Q723 32 723 20T708 0H71Q70 0 67 2T59 9T55 20T59 31T66 38T71 40H369V652'],
  
      // TRUE
      0x22A8: [750,249,867,119,812,'139 -249H137Q125 -249 119 -235V251L120 737Q130 750 139 750Q152 750 159 735V367H796Q811 359 811 347Q811 336 797 328L479 327H161L159 328V172L161 173H797Q798 172 800 171T803 169T805 167T808 164T809 162T810 158T811 153Q811 140 796 133H159V-235Q151 -249 141 -249H139'],
  
      // DIAMOND OPERATOR
      0x22C4: [488,-12,500,12,488,'242 486Q245 488 250 488Q256 488 258 486Q262 484 373 373T486 258T488 250T486 242T373 127T258 14Q256 12 250 12Q245 12 242 14Q237 16 127 126T14 242Q12 245 12 250T14 258Q16 263 126 373T242 486ZM439 250L250 439L61 250L250 61L439 250'],
  
      // DOT OPERATOR
      0x22C5: [310,-190,278,78,199,'78 250Q78 274 95 292T138 310Q162 310 180 294T199 251Q199 226 182 208T139 190T96 207T78 250'],
  
      // STAR OPERATOR
      0x22C6: [486,-16,500,3,497,'210 282Q210 284 225 381T241 480Q241 484 245 484Q249 486 251 486Q258 486 260 477T272 406Q275 390 276 380Q290 286 290 282L388 299Q484 314 487 314H488Q497 314 497 302Q497 297 434 266Q416 257 404 251L315 206L361 118Q372 98 383 75T401 40L407 28Q407 16 395 16Q394 16 392 16L390 17L250 159L110 17L108 16Q106 16 105 16Q93 16 93 28L99 40Q105 52 116 75T139 118L185 206L96 251Q6 296 4 300Q3 301 3 302Q3 314 12 314H13Q16 314 112 299L210 282'],
  
      // BOWTIE
      0x22C8: [505,5,900,26,873,'833 50T833 250T832 450T659 351T487 250T658 150T832 50Q833 50 833 250ZM873 10Q866 -5 854 -5Q851 -5 845 -3L449 226L260 115Q51 -5 43 -5Q39 -5 35 -1T28 7L26 11V489Q33 505 43 505Q51 505 260 385L449 274L845 503Q851 505 853 505Q866 505 873 490V10ZM412 250L67 450Q66 450 66 250T67 50Q69 51 240 150T412 250'],
  
      // VERTICAL ELLIPSIS
      0x22EE: [900,30,278,78,199,'78 30Q78 54 95 72T138 90Q162 90 180 74T199 31Q199 6 182 -12T139 -30T96 -13T78 30ZM78 440Q78 464 95 482T138 500Q162 500 180 484T199 441Q199 416 182 398T139 380T96 397T78 440ZM78 840Q78 864 95 882T138 900Q162 900 180 884T199 841Q199 816 182 798T139 780T96 797T78 840'],
  
      // MIDLINE HORIZONTAL ELLIPSIS
      0x22EF: [310,-190,1172,78,1093,'78 250Q78 274 95 292T138 310Q162 310 180 294T199 251Q199 226 182 208T139 190T96 207T78 250ZM525 250Q525 274 542 292T585 310Q609 310 627 294T646 251Q646 226 629 208T586 190T543 207T525 250ZM972 250Q972 274 989 292T1032 310Q1056 310 1074 294T1093 251Q1093 226 1076 208T1033 190T990 207T972 250'],
  
      // DOWN RIGHT DIAGONAL ELLIPSIS
      0x22F1: [820,-100,1282,133,1148,'133 760Q133 784 150 802T193 820Q217 820 235 804T254 761Q254 736 237 718T194 700T151 717T133 760ZM580 460Q580 484 597 502T640 520Q664 520 682 504T701 461Q701 436 684 418T641 400T598 417T580 460ZM1027 160Q1027 184 1044 202T1087 220Q1111 220 1129 204T1148 161Q1148 136 1131 118T1088 100T1045 117T1027 160'],
  
      // LEFT CEILING
      0x2308: [750,250,444,174,422,'174 734Q178 746 190 750H298H369Q400 750 411 747T422 730T411 713T372 709Q365 709 345 709T310 710H214V-235Q206 -248 196 -250Q192 -250 189 -249T184 -247T180 -244T178 -241T176 -237T174 -234V734'],
  
      // RIGHT CEILING
      0x2309: [750,250,444,21,269,'21 717T21 730T32 746T75 750H147H256Q266 742 269 735V-235Q262 -248 251 -250Q247 -250 244 -249T239 -247T235 -244T233 -241T231 -237T229 -234V710H133Q119 710 99 710T71 709Q43 709 32 713'],
  
      // LEFT FLOOR
      0x230A: [751,251,444,174,423,'174 734Q174 735 175 737T177 740T180 744T184 747T189 749T196 750Q206 748 214 735V-210H310H373Q401 -210 411 -213T422 -230T411 -247T369 -251Q362 -251 338 -251T298 -250H190Q178 -246 174 -234V734'],
  
      // RIGHT FLOOR
      0x230B: [751,250,444,21,269,'229 734Q229 735 230 737T232 740T235 744T239 747T244 749T251 750Q262 748 269 735V-235Q266 -240 256 -249L147 -250H77Q43 -250 32 -247T21 -230T32 -213T72 -209Q79 -209 99 -209T133 -210H229V734'],
  
      // stix-small down curve
      0x2322: [388,-122,1000,55,944,'55 141Q55 149 72 174T125 234T209 303T329 360T478 388H526Q649 383 765 319Q814 291 858 250T923 179T944 141Q944 133 938 128T924 122Q914 124 912 125T902 139Q766 328 500 328Q415 328 342 308T225 258T150 199T102 148T84 124Q81 122 75 122Q55 127 55 141'],
  
      // stix-small up curve
      0x2323: [378,-134,1000,55,944,'923 378Q944 378 944 358Q944 345 912 311T859 259Q710 134 500 134Q288 134 140 259Q55 336 55 358Q55 366 61 372T75 378Q78 378 84 376Q86 376 101 356T147 310T221 257T339 212T500 193Q628 193 734 236Q841 282 903 363Q914 378 923 378'],
  
      // UPPER LEFT OR LOWER RIGHT CURLY BRACKET SECTION
      0x23B0: [744,244,412,56,357,'357 741V726Q357 720 349 715Q261 655 242 539Q240 526 240 454T239 315T239 247Q240 235 240 124V40Q240 -17 233 -53T201 -130Q155 -206 78 -244H69H64Q58 -244 57 -243T56 -234Q56 -232 56 -231V-225Q56 -218 63 -215Q153 -153 170 -39Q172 -25 173 119V219Q173 245 174 249Q173 258 173 376V460Q173 515 178 545T201 611Q244 695 327 741L334 744H354L357 741'],
  
      // UPPER RIGHT OR LOWER LEFT CURLY BRACKET SECTION
      0x23B1: [744,244,412,55,357,'78 744Q153 706 196 640T239 492V376Q239 341 239 314T238 271T238 253Q239 251 239 223V119V49Q239 -39 254 -85Q263 -111 275 -134T301 -172T326 -197T346 -213T356 -221T357 -232V-241L354 -244H334Q264 -209 222 -146T174 -12Q173 -6 173 95Q173 134 173 191T174 250Q173 258 173 382V451Q173 542 159 585Q145 626 120 658T75 706T56 723V731Q56 741 57 742T66 744H78'],
  
      // MATHEMATICAL LEFT ANGLE BRACKET
      0x27E8: [750,250,389,109,333,'333 -232Q332 -239 327 -244T313 -250Q303 -250 296 -240Q293 -233 202 6T110 250T201 494T296 740Q299 745 306 749L309 750Q312 750 313 750Q331 750 333 732Q333 727 243 489Q152 252 152 250T243 11Q333 -227 333 -232'],
  
      // MATHEMATICAL RIGHT ANGLE BRACKET
      0x27E9: [750,250,389,55,279,'55 732Q56 739 61 744T75 750Q85 750 92 740Q95 733 186 494T278 250T187 6T92 -240Q85 -250 75 -250Q67 -250 62 -245T55 -232Q55 -227 145 11Q236 248 236 250T145 489Q55 727 55 732'],
  
      // MATHEMATICAL LEFT FLATTENED PARENTHESIS
      0x27EE: [744,244,412,173,357,'357 741V726Q357 720 349 715Q261 655 242 539Q240 526 240 394V331Q240 259 239 250Q240 242 240 119V49Q240 -42 254 -85Q263 -111 275 -134T301 -172T326 -197T346 -213T356 -221T357 -232V-241L354 -244H334Q264 -209 222 -146T174 -12Q173 -6 173 95Q173 134 173 191T174 250Q173 260 173 376V460Q173 515 178 545T201 611Q244 695 327 741L334 744H354L357 741'],
  
      // MATHEMATICAL RIGHT FLATTENED PARENTHESIS
      0x27EF: [744,244,412,55,240,'78 744Q153 706 196 640T239 492V376Q239 339 239 311T238 269T238 252Q240 236 240 124V40Q240 -18 233 -53T202 -130Q156 -206 79 -244H70H65Q58 -244 57 -242T56 -231T57 -220T64 -215Q153 -154 170 -39Q173 -18 174 119V247Q173 249 173 382V451Q173 542 159 585Q145 626 120 658T75 706T56 723V731Q56 741 57 742T66 744H78'],
  
      // LONG LEFTWARDS ARROW
      0x27F5: [511,11,1609,55,1525,'165 270H1510Q1525 262 1525 250T1510 230H165Q167 228 182 216T211 189T244 152T277 96T303 25Q308 7 308 0Q308 -11 288 -11Q281 -11 278 -11T272 -7T267 2T263 21Q245 94 195 151T73 236Q58 242 55 247Q55 254 59 257T73 264Q121 283 158 314T215 375T247 434T264 480L267 497Q269 503 270 505T275 509T288 511Q308 511 308 500Q308 493 303 475Q293 438 278 406T246 352T215 315T185 287T165 270'],
  
      // LONG RIGHTWARDS ARROW
      0x27F6: [511,11,1638,84,1553,'84 237T84 250T98 270H1444Q1328 357 1301 493Q1301 494 1301 496T1300 499Q1300 511 1317 511H1320Q1329 511 1332 510T1338 506T1341 497T1344 481T1352 456Q1374 389 1425 336T1544 261Q1553 258 1553 250Q1553 244 1548 241T1524 231T1486 212Q1445 186 1415 152T1370 85T1349 35T1341 4Q1339 -6 1336 -8T1320 -11Q1300 -11 1300 0Q1300 7 1305 25Q1337 151 1444 230H98Q84 237 84 250'],
  
      // LONG LEFT RIGHT ARROW
      0x27F7: [511,11,1859,55,1803,'165 270H1694Q1578 357 1551 493Q1551 494 1551 496T1550 499Q1550 511 1567 511H1570Q1579 511 1582 510T1588 506T1591 497T1594 481T1602 456Q1624 389 1675 336T1794 261Q1803 258 1803 250Q1803 244 1798 241T1774 231T1736 212Q1695 186 1665 152T1620 85T1599 35T1591 4Q1589 -6 1586 -8T1570 -11Q1550 -11 1550 0Q1550 7 1555 25Q1587 151 1694 230H165Q167 228 182 216T211 189T244 152T277 96T303 25Q308 7 308 0Q308 -11 288 -11Q281 -11 278 -11T272 -7T267 2T263 21Q245 94 195 151T73 236Q58 242 55 247Q55 254 59 257T73 264Q121 283 158 314T215 375T247 434T264 480L267 497Q269 503 270 505T275 509T288 511Q308 511 308 500Q308 493 303 475Q293 438 278 406T246 352T215 315T185 287T165 270'],
  
      // LONG LEFTWARDS DOUBLE ARROW
      0x27F8: [525,24,1609,56,1554,'274 173H1539Q1540 172 1542 171T1545 169T1547 167T1550 164T1551 162T1552 158T1553 153Q1553 140 1538 133H318L328 123Q379 69 414 0Q419 -13 419 -17Q419 -24 399 -24Q388 -24 385 -23T377 -12Q332 77 253 144T72 237Q62 240 59 242T56 250T59 257T70 262T89 268T119 278T160 296Q303 366 377 512Q382 522 385 523T401 525Q419 524 419 515Q419 510 414 500Q379 431 328 377L318 367H1538Q1553 359 1553 347Q1553 336 1539 328L1221 327H903L900 328L602 327H274L264 319Q225 289 147 250Q148 249 165 241T210 217T264 181L274 173'],
  
      // LONG RIGHTWARDS DOUBLE ARROW
      0x27F9: [525,24,1638,56,1582,'1218 514Q1218 525 1234 525Q1239 525 1242 525T1247 525T1251 524T1253 523T1255 520T1257 517T1260 512Q1297 438 1358 381T1469 300T1565 263Q1582 258 1582 250T1573 239T1536 228T1478 204Q1334 134 1260 -12Q1256 -21 1253 -22T1238 -24Q1218 -24 1218 -17Q1218 -13 1223 0Q1258 69 1309 123L1319 133H70Q56 140 56 153Q56 168 72 173H1363L1373 181Q1412 211 1490 250Q1489 251 1472 259T1427 283T1373 319L1363 327H710L707 328L390 327H72Q56 332 56 347Q56 360 70 367H1319L1309 377Q1276 412 1247 458T1218 514'],
  
      // LONG LEFT RIGHT DOUBLE ARROW
      0x27FA: [525,24,1858,56,1802,'1438 514Q1438 525 1454 525Q1459 525 1462 525T1467 525T1471 524T1473 523T1475 520T1477 517T1480 512Q1517 438 1578 381T1689 300T1785 263Q1802 258 1802 250T1793 239T1756 228T1698 204Q1554 134 1480 -12Q1476 -21 1473 -22T1458 -24Q1438 -24 1438 -17Q1438 -13 1443 0Q1478 69 1529 123L1539 133H318L328 123Q379 69 414 0Q419 -13 419 -17Q419 -24 399 -24Q388 -24 385 -23T377 -12Q332 77 253 144T72 237Q62 240 59 242T56 250T59 257T70 262T89 268T119 278T160 296Q303 366 377 512Q382 522 385 523T401 525Q419 524 419 515Q419 510 414 500Q379 431 328 377L318 367H1539L1529 377Q1496 412 1467 458T1438 514ZM274 173H1583L1593 181Q1632 211 1710 250Q1709 251 1692 259T1647 283T1593 319L1583 327H930L927 328L602 327H274L264 319Q225 289 147 250Q148 249 165 241T210 217T264 181L274 173'],
  
      // LONG RIGHTWARDS ARROW FROM BAR
      0x27FC: [511,11,1638,54,1553,'95 155V109Q95 83 92 73T75 63Q61 63 58 74T54 130Q54 140 54 180T55 250Q55 421 57 425Q61 437 75 437Q88 437 91 428T95 393V345V270H1444Q1328 357 1301 493Q1301 494 1301 496T1300 499Q1300 511 1317 511H1320Q1329 511 1332 510T1338 506T1341 497T1344 481T1352 456Q1374 389 1425 336T1544 261Q1553 258 1553 250Q1553 244 1548 241T1524 231T1486 212Q1445 186 1415 152T1370 85T1349 35T1341 4Q1339 -6 1336 -8T1320 -11Q1300 -11 1300 0Q1300 7 1305 25Q1337 151 1444 230H95V155'],
  
      // PRECEDES ABOVE SINGLE-LINE EQUALS SIGN
      0x2AAF: [636,138,778,84,694,'84 346Q84 359 91 363T117 367Q120 367 126 367T137 366Q388 370 512 430T653 609Q657 636 676 636Q685 635 689 629T694 618V612Q689 566 672 528T626 463T569 417T500 383T435 362T373 346Q379 345 404 339T440 330T477 318T533 296Q592 266 630 223T681 145T694 78Q694 57 674 57Q662 57 657 67T652 92T640 135T606 191Q500 320 137 326H114Q104 326 98 327T88 332T84 346ZM84 -131T84 -118T98 -98H679Q694 -106 694 -118T679 -138H98Q84 -131 84 -118'],
  
      // SUCCEEDS ABOVE SINGLE-LINE EQUALS SIGN
      0x2AB0: [636,138,778,83,694,'84 614Q84 636 102 636Q115 636 119 626T125 600T137 556T171 501Q277 372 640 366H661Q694 366 694 346T661 326H640Q578 325 526 321T415 307T309 280T222 237T156 172T124 83Q122 66 118 62T103 57Q100 57 98 57T95 58T93 59T90 62T85 67Q83 71 83 80Q88 126 105 164T151 229T208 275T277 309T342 330T404 346Q401 347 380 351T345 360T302 373T245 396Q125 455 92 565Q84 599 84 614ZM84 -131T84 -118T98 -98H679Q694 -106 694 -118T679 -138H98Q84 -131 84 -118']
  };

  SVG.FONTDATA.FONTS['MathJax_Math-italic'] = {
    directory: 'Math/Italic',
    family: 'MathJax_Math',
    id: 'MJMATHI',
    style: 'italic',
    skew: {
      0x41: 0.139,
      0x42: 0.0833,
      0x43: 0.0833,
      0x44: 0.0556,
      0x45: 0.0833,
      0x46: 0.0833,
      0x47: 0.0833,
      0x48: 0.0556,
      0x49: 0.111,
      0x4A: 0.167,
      0x4B: 0.0556,
      0x4C: 0.0278,
      0x4D: 0.0833,
      0x4E: 0.0833,
      0x4F: 0.0833,
      0x50: 0.0833,
      0x51: 0.0833,
      0x52: 0.0833,
      0x53: 0.0833,
      0x54: 0.0833,
      0x55: 0.0278,
      0x58: 0.0833,
      0x5A: 0.0833,
      0x63: 0.0556,
      0x64: 0.167,
      0x65: 0.0556,
      0x66: 0.167,
      0x67: 0.0278,
      0x68: -0.0278,
      0x6C: 0.0833,
      0x6F: 0.0556,
      0x70: 0.0833,
      0x71: 0.0833,
      0x72: 0.0556,
      0x73: 0.0556,
      0x74: 0.0833,
      0x75: 0.0278,
      0x76: 0.0278,
      0x77: 0.0833,
      0x78: 0.0278,
      0x79: 0.0556,
      0x7A: 0.0556,
      0x393: 0.0833,
      0x394: 0.167,
      0x398: 0.0833,
      0x39B: 0.167,
      0x39E: 0.0833,
      0x3A0: 0.0556,
      0x3A3: 0.0833,
      0x3A5: 0.0556,
      0x3A6: 0.0833,
      0x3A8: 0.0556,
      0x3A9: 0.0833,
      0x3B1: 0.0278,
      0x3B2: 0.0833,
      0x3B4: 0.0556,
      0x3B5: 0.0833,
      0x3B6: 0.0833,
      0x3B7: 0.0556,
      0x3B8: 0.0833,
      0x3B9: 0.0556,
      0x3BC: 0.0278,
      0x3BD: 0.0278,
      0x3BE: 0.111,
      0x3BF: 0.0556,
      0x3C1: 0.0833,
      0x3C2: 0.0833,
      0x3C4: 0.0278,
      0x3C5: 0.0278,
      0x3C6: 0.0833,
      0x3C7: 0.0556,
      0x3C8: 0.111,
      0x3D1: 0.0833,
      0x3D5: 0.0833,
      0x3F1: 0.0833,
      0x3F5: 0.0556
    },
  
      // SPACE
      0x20: [0,0,250,0,0,''],
  
      // SOLIDUS
      0x2F: [716,215,778,139,638,'166 -215T159 -215T147 -212T141 -204T139 -197Q139 -190 144 -183Q157 -157 378 274T602 707Q605 716 618 716Q625 716 630 712T636 703T638 696Q638 691 406 241T170 -212Q166 -215 159 -215'],
  
      // LATIN CAPITAL LETTER A
      0x41: [716,0,750,35,726,'208 74Q208 50 254 46Q272 46 272 35Q272 34 270 22Q267 8 264 4T251 0Q249 0 239 0T205 1T141 2Q70 2 50 0H42Q35 7 35 11Q37 38 48 46H62Q132 49 164 96Q170 102 345 401T523 704Q530 716 547 716H555H572Q578 707 578 706L606 383Q634 60 636 57Q641 46 701 46Q726 46 726 36Q726 34 723 22Q720 7 718 4T704 0Q701 0 690 0T651 1T578 2Q484 2 455 0H443Q437 6 437 9T439 27Q443 40 445 43L449 46H469Q523 49 533 63L521 213H283L249 155Q208 86 208 74ZM516 260Q516 271 504 416T490 562L463 519Q447 492 400 412L310 260L413 259Q516 259 516 260'],
  
      // LATIN CAPITAL LETTER B
      0x42: [683,0,759,35,756,'231 637Q204 637 199 638T194 649Q194 676 205 682Q206 683 335 683Q594 683 608 681Q671 671 713 636T756 544Q756 480 698 429T565 360L555 357Q619 348 660 311T702 219Q702 146 630 78T453 1Q446 0 242 0Q42 0 39 2Q35 5 35 10Q35 17 37 24Q42 43 47 45Q51 46 62 46H68Q95 46 128 49Q142 52 147 61Q150 65 219 339T288 628Q288 635 231 637ZM649 544Q649 574 634 600T585 634Q578 636 493 637Q473 637 451 637T416 636H403Q388 635 384 626Q382 622 352 506Q352 503 351 500L320 374H401Q482 374 494 376Q554 386 601 434T649 544ZM595 229Q595 273 572 302T512 336Q506 337 429 337Q311 337 310 336Q310 334 293 263T258 122L240 52Q240 48 252 48T333 46Q422 46 429 47Q491 54 543 105T595 229'],
  
      // LATIN CAPITAL LETTER C
      0x43: [705,22,715,50,760,'50 252Q50 367 117 473T286 641T490 704Q580 704 633 653Q642 643 648 636T656 626L657 623Q660 623 684 649Q691 655 699 663T715 679T725 690L740 705H746Q760 705 760 698Q760 694 728 561Q692 422 692 421Q690 416 687 415T669 413H653Q647 419 647 422Q647 423 648 429T650 449T651 481Q651 552 619 605T510 659Q484 659 454 652T382 628T299 572T226 479Q194 422 175 346T156 222Q156 108 232 58Q280 24 350 24Q441 24 512 92T606 240Q610 253 612 255T628 257Q648 257 648 248Q648 243 647 239Q618 132 523 55T319 -22Q206 -22 128 53T50 252'],
  
      // LATIN CAPITAL LETTER D
      0x44: [683,0,828,33,803,'287 628Q287 635 230 637Q207 637 200 638T193 647Q193 655 197 667T204 682Q206 683 403 683Q570 682 590 682T630 676Q702 659 752 597T803 431Q803 275 696 151T444 3L430 1L236 0H125H72Q48 0 41 2T33 11Q33 13 36 25Q40 41 44 43T67 46Q94 46 127 49Q141 52 146 61Q149 65 218 339T287 628ZM703 469Q703 507 692 537T666 584T629 613T590 629T555 636Q553 636 541 636T512 636T479 637H436Q392 637 386 627Q384 623 313 339T242 52Q242 48 253 48T330 47Q335 47 349 47T373 46Q499 46 581 128Q617 164 640 212T683 339T703 469'],
  
      // LATIN CAPITAL LETTER E
      0x45: [680,0,738,31,764,'492 213Q472 213 472 226Q472 230 477 250T482 285Q482 316 461 323T364 330H312Q311 328 277 192T243 52Q243 48 254 48T334 46Q428 46 458 48T518 61Q567 77 599 117T670 248Q680 270 683 272Q690 274 698 274Q718 274 718 261Q613 7 608 2Q605 0 322 0H133Q31 0 31 11Q31 13 34 25Q38 41 42 43T65 46Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 659Q197 676 203 680H757Q764 676 764 669Q764 664 751 557T737 447Q735 440 717 440H705Q698 445 698 453L701 476Q704 500 704 528Q704 558 697 578T678 609T643 625T596 632T532 634H485Q397 633 392 631Q388 629 386 622Q385 619 355 499T324 377Q347 376 372 376H398Q464 376 489 391T534 472Q538 488 540 490T557 493Q562 493 565 493T570 492T572 491T574 487T577 483L544 351Q511 218 508 216Q505 213 492 213'],
  
      // LATIN CAPITAL LETTER F
      0x46: [680,0,643,31,749,'48 1Q31 1 31 11Q31 13 34 25Q38 41 42 43T65 46Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 659Q197 676 203 680H742Q749 676 749 669Q749 664 736 557T722 447Q720 440 702 440H690Q683 445 683 453Q683 454 686 477T689 530Q689 560 682 579T663 610T626 626T575 633T503 634H480Q398 633 393 631Q388 629 386 623Q385 622 352 492L320 363H375Q378 363 398 363T426 364T448 367T472 374T489 386Q502 398 511 419T524 457T529 475Q532 480 548 480H560Q567 475 567 470Q567 467 536 339T502 207Q500 200 482 200H470Q463 206 463 212Q463 215 468 234T473 274Q473 303 453 310T364 317H309L277 190Q245 66 245 60Q245 46 334 46H359Q365 40 365 39T363 19Q359 6 353 0H336Q295 2 185 2Q120 2 86 2T48 1'],
  
      // LATIN CAPITAL LETTER G
      0x47: [705,22,786,50,760,'50 252Q50 367 117 473T286 641T490 704Q580 704 633 653Q642 643 648 636T656 626L657 623Q660 623 684 649Q691 655 699 663T715 679T725 690L740 705H746Q760 705 760 698Q760 694 728 561Q692 422 692 421Q690 416 687 415T669 413H653Q647 419 647 422Q647 423 648 429T650 449T651 481Q651 552 619 605T510 659Q492 659 471 656T418 643T357 615T294 567T236 496T189 394T158 260Q156 242 156 221Q156 173 170 136T206 79T256 45T308 28T353 24Q407 24 452 47T514 106Q517 114 529 161T541 214Q541 222 528 224T468 227H431Q425 233 425 235T427 254Q431 267 437 273H454Q494 271 594 271Q634 271 659 271T695 272T707 272Q721 272 721 263Q721 261 719 249Q714 230 709 228Q706 227 694 227Q674 227 653 224Q646 221 643 215T629 164Q620 131 614 108Q589 6 586 3Q584 1 581 1Q571 1 553 21T530 52Q530 53 528 52T522 47Q448 -22 322 -22Q201 -22 126 55T50 252'],
  
      // LATIN CAPITAL LETTER H
      0x48: [683,0,831,31,888,'228 637Q194 637 192 641Q191 643 191 649Q191 673 202 682Q204 683 219 683Q260 681 355 681Q389 681 418 681T463 682T483 682Q499 682 499 672Q499 670 497 658Q492 641 487 638H485Q483 638 480 638T473 638T464 637T455 637Q416 636 405 634T387 623Q384 619 355 500Q348 474 340 442T328 395L324 380Q324 378 469 378H614L615 381Q615 384 646 504Q674 619 674 627T617 637Q594 637 587 639T580 648Q580 650 582 660Q586 677 588 679T604 682Q609 682 646 681T740 680Q802 680 835 681T871 682Q888 682 888 672Q888 645 876 638H874Q872 638 869 638T862 638T853 637T844 637Q805 636 794 634T776 623Q773 618 704 340T634 58Q634 51 638 51Q646 48 692 46H723Q729 38 729 37T726 19Q722 6 716 0H701Q664 2 567 2Q533 2 504 2T458 2T437 1Q420 1 420 10Q420 15 423 24Q428 43 433 45Q437 46 448 46H454Q481 46 514 49Q520 50 522 50T528 55T534 64T540 82T547 110T558 153Q565 181 569 198Q602 330 602 331T457 332H312L279 197Q245 63 245 58Q245 51 253 49T303 46H334Q340 38 340 37T337 19Q333 6 327 0H312Q275 2 178 2Q144 2 115 2T69 2T48 1Q31 1 31 10Q31 12 34 24Q39 43 44 45Q48 46 59 46H65Q92 46 125 49Q139 52 144 61Q147 65 216 339T285 628Q285 635 228 637'],
  
      // LATIN CAPITAL LETTER I
      0x49: [683,0,440,26,504,'43 1Q26 1 26 10Q26 12 29 24Q34 43 39 45Q42 46 54 46H60Q120 46 136 53Q137 53 138 54Q143 56 149 77T198 273Q210 318 216 344Q286 624 286 626Q284 630 284 631Q274 637 213 637H193Q184 643 189 662Q193 677 195 680T209 683H213Q285 681 359 681Q481 681 487 683H497Q504 676 504 672T501 655T494 639Q491 637 471 637Q440 637 407 634Q393 631 388 623Q381 609 337 432Q326 385 315 341Q245 65 245 59Q245 52 255 50T307 46H339Q345 38 345 37T342 19Q338 6 332 0H316Q279 2 179 2Q143 2 113 2T65 2T43 1'],
  
      // LATIN CAPITAL LETTER J
      0x4A: [683,22,555,57,633,'447 625Q447 637 354 637H329Q323 642 323 645T325 664Q329 677 335 683H352Q393 681 498 681Q541 681 568 681T605 682T619 682Q633 682 633 672Q633 670 630 658Q626 642 623 640T604 637Q552 637 545 623Q541 610 483 376Q420 128 419 127Q397 64 333 21T195 -22Q137 -22 97 8T57 88Q57 130 80 152T132 174Q177 174 182 130Q182 98 164 80T123 56Q115 54 115 53T122 44Q148 15 197 15Q235 15 271 47T324 130Q328 142 387 380T447 625'],
  
      // LATIN CAPITAL LETTER K
      0x4B: [683,0,849,31,889,'285 628Q285 635 228 637Q205 637 198 638T191 647Q191 649 193 661Q199 681 203 682Q205 683 214 683H219Q260 681 355 681Q389 681 418 681T463 682T483 682Q500 682 500 674Q500 669 497 660Q496 658 496 654T495 648T493 644T490 641T486 639T479 638T470 637T456 637Q416 636 405 634T387 623L306 305Q307 305 490 449T678 597Q692 611 692 620Q692 635 667 637Q651 637 651 648Q651 650 654 662T659 677Q662 682 676 682Q680 682 711 681T791 680Q814 680 839 681T869 682Q889 682 889 672Q889 650 881 642Q878 637 862 637Q787 632 726 586Q710 576 656 534T556 455L509 418L518 396Q527 374 546 329T581 244Q656 67 661 61Q663 59 666 57Q680 47 717 46H738Q744 38 744 37T741 19Q737 6 731 0H720Q680 3 625 3Q503 3 488 0H478Q472 6 472 9T474 27Q478 40 480 43T491 46H494Q544 46 544 71Q544 75 517 141T485 216L427 354L359 301L291 248L268 155Q245 63 245 58Q245 51 253 49T303 46H334Q340 37 340 35Q340 19 333 5Q328 0 317 0Q314 0 280 1T180 2Q118 2 85 2T49 1Q31 1 31 11Q31 13 34 25Q38 41 42 43T65 46Q92 46 125 49Q139 52 144 61Q147 65 216 339T285 628'],
  
      // LATIN CAPITAL LETTER L
      0x4C: [683,2,681,32,647,'228 637Q194 637 192 641Q191 643 191 649Q191 673 202 682Q204 683 217 683Q271 680 344 680Q485 680 506 683H518Q524 677 524 674T522 656Q517 641 513 637H475Q406 636 394 628Q387 624 380 600T313 336Q297 271 279 198T252 88L243 52Q243 48 252 48T311 46H328Q360 46 379 47T428 54T478 72T522 106T564 161Q580 191 594 228T611 270Q616 273 628 273H641Q647 264 647 262T627 203T583 83T557 9Q555 4 553 3T537 0T494 -1Q483 -1 418 -1T294 0H116Q32 0 32 10Q32 17 34 24Q39 43 44 45Q48 46 59 46H65Q92 46 125 49Q139 52 144 61Q147 65 216 339T285 628Q285 635 228 637'],
  
      // LATIN CAPITAL LETTER M
      0x4D: [684,0,970,35,1051,'289 629Q289 635 232 637Q208 637 201 638T194 648Q194 649 196 659Q197 662 198 666T199 671T201 676T203 679T207 681T212 683T220 683T232 684Q238 684 262 684T307 683Q386 683 398 683T414 678Q415 674 451 396L487 117L510 154Q534 190 574 254T662 394Q837 673 839 675Q840 676 842 678T846 681L852 683H948Q965 683 988 683T1017 684Q1051 684 1051 673Q1051 668 1048 656T1045 643Q1041 637 1008 637Q968 636 957 634T939 623Q936 618 867 340T797 59Q797 55 798 54T805 50T822 48T855 46H886Q892 37 892 35Q892 19 885 5Q880 0 869 0Q864 0 828 1T736 2Q675 2 644 2T609 1Q592 1 592 11Q592 13 594 25Q598 41 602 43T625 46Q652 46 685 49Q699 52 704 61Q706 65 742 207T813 490T848 631L654 322Q458 10 453 5Q451 4 449 3Q444 0 433 0Q418 0 415 7Q413 11 374 317L335 624L267 354Q200 88 200 79Q206 46 272 46H282Q288 41 289 37T286 19Q282 3 278 1Q274 0 267 0Q265 0 255 0T221 1T157 2Q127 2 95 1T58 0Q43 0 39 2T35 11Q35 13 38 25T43 40Q45 46 65 46Q135 46 154 86Q158 92 223 354T289 629'],
  
      // LATIN CAPITAL LETTER N
      0x4E: [683,0,803,31,888,'234 637Q231 637 226 637Q201 637 196 638T191 649Q191 676 202 682Q204 683 299 683Q376 683 387 683T401 677Q612 181 616 168L670 381Q723 592 723 606Q723 633 659 637Q635 637 635 648Q635 650 637 660Q641 676 643 679T653 683Q656 683 684 682T767 680Q817 680 843 681T873 682Q888 682 888 672Q888 650 880 642Q878 637 858 637Q787 633 769 597L620 7Q618 0 599 0Q585 0 582 2Q579 5 453 305L326 604L261 344Q196 88 196 79Q201 46 268 46H278Q284 41 284 38T282 19Q278 6 272 0H259Q228 2 151 2Q123 2 100 2T63 2T46 1Q31 1 31 10Q31 14 34 26T39 40Q41 46 62 46Q130 49 150 85Q154 91 221 362L289 634Q287 635 234 637'],
  
      // LATIN CAPITAL LETTER O
      0x4F: [704,22,763,50,740,'740 435Q740 320 676 213T511 42T304 -22Q207 -22 138 35T51 201Q50 209 50 244Q50 346 98 438T227 601Q351 704 476 704Q514 704 524 703Q621 689 680 617T740 435ZM637 476Q637 565 591 615T476 665Q396 665 322 605Q242 542 200 428T157 216Q157 126 200 73T314 19Q404 19 485 98T608 313Q637 408 637 476'],
  
      // LATIN CAPITAL LETTER P
      0x50: [683,0,642,33,751,'287 628Q287 635 230 637Q206 637 199 638T192 648Q192 649 194 659Q200 679 203 681T397 683Q587 682 600 680Q664 669 707 631T751 530Q751 453 685 389Q616 321 507 303Q500 302 402 301H307L277 182Q247 66 247 59Q247 55 248 54T255 50T272 48T305 46H336Q342 37 342 35Q342 19 335 5Q330 0 319 0Q316 0 282 1T182 2Q120 2 87 2T51 1Q33 1 33 11Q33 13 36 25Q40 41 44 43T67 46Q94 46 127 49Q141 52 146 61Q149 65 218 339T287 628ZM645 554Q645 567 643 575T634 597T609 619T560 635Q553 636 480 637Q463 637 445 637T416 636T404 636Q391 635 386 627Q384 621 367 550T332 412T314 344Q314 342 395 342H407H430Q542 342 590 392Q617 419 631 471T645 554'],
  
      // LATIN CAPITAL LETTER Q
      0x51: [704,194,791,50,740,'399 -80Q399 -47 400 -30T402 -11V-7L387 -11Q341 -22 303 -22Q208 -22 138 35T51 201Q50 209 50 244Q50 346 98 438T227 601Q351 704 476 704Q514 704 524 703Q621 689 680 617T740 435Q740 255 592 107Q529 47 461 16L444 8V3Q444 2 449 -24T470 -66T516 -82Q551 -82 583 -60T625 -3Q631 11 638 11Q647 11 649 2Q649 -6 639 -34T611 -100T557 -165T481 -194Q399 -194 399 -87V-80ZM636 468Q636 523 621 564T580 625T530 655T477 665Q429 665 379 640Q277 591 215 464T153 216Q153 110 207 59Q231 38 236 38V46Q236 86 269 120T347 155Q372 155 390 144T417 114T429 82T435 55L448 64Q512 108 557 185T619 334T636 468ZM314 18Q362 18 404 39L403 49Q399 104 366 115Q354 117 347 117Q344 117 341 117T337 118Q317 118 296 98T274 52Q274 18 314 18'],
  
      // LATIN CAPITAL LETTER R
      0x52: [683,21,759,33,755,'230 637Q203 637 198 638T193 649Q193 676 204 682Q206 683 378 683Q550 682 564 680Q620 672 658 652T712 606T733 563T739 529Q739 484 710 445T643 385T576 351T538 338L545 333Q612 295 612 223Q612 212 607 162T602 80V71Q602 53 603 43T614 25T640 16Q668 16 686 38T712 85Q717 99 720 102T735 105Q755 105 755 93Q755 75 731 36Q693 -21 641 -21H632Q571 -21 531 4T487 82Q487 109 502 166T517 239Q517 290 474 313Q459 320 449 321T378 323H309L277 193Q244 61 244 59Q244 55 245 54T252 50T269 48T302 46H333Q339 38 339 37T336 19Q332 6 326 0H311Q275 2 180 2Q146 2 117 2T71 2T50 1Q33 1 33 10Q33 12 36 24Q41 43 46 45Q50 46 61 46H67Q94 46 127 49Q141 52 146 61Q149 65 218 339T287 628Q287 635 230 637ZM630 554Q630 586 609 608T523 636Q521 636 500 636T462 637H440Q393 637 386 627Q385 624 352 494T319 361Q319 360 388 360Q466 361 492 367Q556 377 592 426Q608 449 619 486T630 554'],
  
      // LATIN CAPITAL LETTER S
      0x53: [705,22,613,52,645,'308 24Q367 24 416 76T466 197Q466 260 414 284Q308 311 278 321T236 341Q176 383 176 462Q176 523 208 573T273 648Q302 673 343 688T407 704H418H425Q521 704 564 640Q565 640 577 653T603 682T623 704Q624 704 627 704T632 705Q645 705 645 698T617 577T585 459T569 456Q549 456 549 465Q549 471 550 475Q550 478 551 494T553 520Q553 554 544 579T526 616T501 641Q465 662 419 662Q362 662 313 616T263 510Q263 480 278 458T319 427Q323 425 389 408T456 390Q490 379 522 342T554 242Q554 216 546 186Q541 164 528 137T492 78T426 18T332 -20Q320 -22 298 -22Q199 -22 144 33L134 44L106 13Q83 -14 78 -18T65 -22Q52 -22 52 -14Q52 -11 110 221Q112 227 130 227H143Q149 221 149 216Q149 214 148 207T144 186T142 153Q144 114 160 87T203 47T255 29T308 24'],
  
      // LATIN CAPITAL LETTER T
      0x54: [677,0,584,21,704,'40 437Q21 437 21 445Q21 450 37 501T71 602L88 651Q93 669 101 677H569H659Q691 677 697 676T704 667Q704 661 687 553T668 444Q668 437 649 437Q640 437 637 437T631 442L629 445Q629 451 635 490T641 551Q641 586 628 604T573 629Q568 630 515 631Q469 631 457 630T439 622Q438 621 368 343T298 60Q298 48 386 46Q418 46 427 45T436 36Q436 31 433 22Q429 4 424 1L422 0Q419 0 415 0Q410 0 363 1T228 2Q99 2 64 0H49Q43 6 43 9T45 27Q49 40 55 46H83H94Q174 46 189 55Q190 56 191 56Q196 59 201 76T241 233Q258 301 269 344Q339 619 339 625Q339 630 310 630H279Q212 630 191 624Q146 614 121 583T67 467Q60 445 57 441T43 437H40'],
  
      // LATIN CAPITAL LETTER U
      0x55: [683,22,683,60,767,'107 637Q73 637 71 641Q70 643 70 649Q70 673 81 682Q83 683 98 683Q139 681 234 681Q268 681 297 681T342 682T362 682Q378 682 378 672Q378 670 376 658Q371 641 366 638H364Q362 638 359 638T352 638T343 637T334 637Q295 636 284 634T266 623Q265 621 238 518T184 302T154 169Q152 155 152 140Q152 86 183 55T269 24Q336 24 403 69T501 205L552 406Q599 598 599 606Q599 633 535 637Q511 637 511 648Q511 650 513 660Q517 676 519 679T529 683Q532 683 561 682T645 680Q696 680 723 681T752 682Q767 682 767 672Q767 650 759 642Q756 637 737 637Q666 633 648 597Q646 592 598 404Q557 235 548 205Q515 105 433 42T263 -22Q171 -22 116 34T60 167V183Q60 201 115 421Q164 622 164 628Q164 635 107 637'],
  
      // LATIN CAPITAL LETTER V
      0x56: [683,22,583,52,769,'52 648Q52 670 65 683H76Q118 680 181 680Q299 680 320 683H330Q336 677 336 674T334 656Q329 641 325 637H304Q282 635 274 635Q245 630 242 620Q242 618 271 369T301 118L374 235Q447 352 520 471T595 594Q599 601 599 609Q599 633 555 637Q537 637 537 648Q537 649 539 661Q542 675 545 679T558 683Q560 683 570 683T604 682T668 681Q737 681 755 683H762Q769 676 769 672Q769 655 760 640Q757 637 743 637Q730 636 719 635T698 630T682 623T670 615T660 608T652 599T645 592L452 282Q272 -9 266 -16Q263 -18 259 -21L241 -22H234Q216 -22 216 -15Q213 -9 177 305Q139 623 138 626Q133 637 76 637H59Q52 642 52 648'],
  
      // LATIN CAPITAL LETTER W
      0x57: [683,22,944,51,1048,'436 683Q450 683 486 682T553 680Q604 680 638 681T677 682Q695 682 695 674Q695 670 692 659Q687 641 683 639T661 637Q636 636 621 632T600 624T597 615Q597 603 613 377T629 138L631 141Q633 144 637 151T649 170T666 200T690 241T720 295T759 362Q863 546 877 572T892 604Q892 619 873 628T831 637Q817 637 817 647Q817 650 819 660Q823 676 825 679T839 682Q842 682 856 682T895 682T949 681Q1015 681 1034 683Q1048 683 1048 672Q1048 666 1045 655T1038 640T1028 637Q1006 637 988 631T958 617T939 600T927 584L923 578L754 282Q586 -14 585 -15Q579 -22 561 -22Q546 -22 542 -17Q539 -14 523 229T506 480L494 462Q472 425 366 239Q222 -13 220 -15T215 -19Q210 -22 197 -22Q178 -22 176 -15Q176 -12 154 304T131 622Q129 631 121 633T82 637H58Q51 644 51 648Q52 671 64 683H76Q118 680 176 680Q301 680 313 683H323Q329 677 329 674T327 656Q322 641 318 637H297Q236 634 232 620Q262 160 266 136L501 550L499 587Q496 629 489 632Q483 636 447 637Q428 637 422 639T416 648Q416 650 418 660Q419 664 420 669T421 676T424 680T428 682T436 683'],
  
      // LATIN CAPITAL LETTER X
      0x58: [683,0,828,26,852,'42 0H40Q26 0 26 11Q26 15 29 27Q33 41 36 43T55 46Q141 49 190 98Q200 108 306 224T411 342Q302 620 297 625Q288 636 234 637H206Q200 643 200 645T202 664Q206 677 212 683H226Q260 681 347 681Q380 681 408 681T453 682T473 682Q490 682 490 671Q490 670 488 658Q484 643 481 640T465 637Q434 634 411 620L488 426L541 485Q646 598 646 610Q646 628 622 635Q617 635 609 637Q594 637 594 648Q594 650 596 664Q600 677 606 683H618Q619 683 643 683T697 681T738 680Q828 680 837 683H845Q852 676 852 672Q850 647 840 637H824Q790 636 763 628T722 611T698 593L687 584Q687 585 592 480L505 384Q505 383 536 304T601 142T638 56Q648 47 699 46Q734 46 734 37Q734 35 732 23Q728 7 725 4T711 1Q708 1 678 1T589 2Q528 2 496 2T461 1Q444 1 444 10Q444 11 446 25Q448 35 450 39T455 44T464 46T480 47T506 54Q523 62 523 64Q522 64 476 181L429 299Q241 95 236 84Q232 76 232 72Q232 53 261 47Q262 47 267 47T273 46Q276 46 277 46T280 45T283 42T284 35Q284 26 282 19Q279 6 276 4T261 1Q258 1 243 1T201 2T142 2Q64 2 42 0'],
  
      // LATIN CAPITAL LETTER Y
      0x59: [683,-1,581,30,763,'66 637Q54 637 49 637T39 638T32 641T30 647T33 664T42 682Q44 683 56 683Q104 680 165 680Q288 680 306 683H316Q322 677 322 674T320 656Q316 643 310 637H298Q242 637 242 624Q242 619 292 477T343 333L346 336Q350 340 358 349T379 373T411 410T454 461Q546 568 561 587T577 618Q577 634 545 637Q528 637 528 647Q528 649 530 661Q533 676 535 679T549 683Q551 683 578 682T657 680Q684 680 713 681T746 682Q763 682 763 673Q763 669 760 657T755 643Q753 637 734 637Q662 632 617 587Q608 578 477 424L348 273L322 169Q295 62 295 57Q295 46 363 46Q379 46 384 45T390 35Q390 33 388 23Q384 6 382 4T366 1Q361 1 324 1T232 2Q170 2 138 2T102 1Q84 1 84 9Q84 14 87 24Q88 27 89 30T90 35T91 39T93 42T96 44T101 45T107 45T116 46T129 46Q168 47 180 50T198 63Q201 68 227 171L252 274L129 623Q128 624 127 625T125 627T122 629T118 631T113 633T105 634T96 635T83 636T66 637'],
  
      // LATIN CAPITAL LETTER Z
      0x5A: [683,0,683,58,723,'58 8Q58 23 64 35Q64 36 329 334T596 635L586 637Q575 637 512 637H500H476Q442 637 420 635T365 624T311 598T266 548T228 469Q227 466 226 463T224 458T223 453T222 450L221 448Q218 443 202 443Q185 443 182 453L214 561Q228 606 241 651Q249 679 253 681Q256 683 487 683H718Q723 678 723 675Q723 673 717 649Q189 54 188 52L185 49H274Q369 50 377 51Q452 60 500 100T579 247Q587 272 590 277T603 282H607Q628 282 628 271Q547 5 541 2Q538 0 300 0H124Q58 0 58 8'],
  
      // LATIN SMALL LETTER A
      0x61: [441,10,529,33,506,'33 157Q33 258 109 349T280 441Q331 441 370 392Q386 422 416 422Q429 422 439 414T449 394Q449 381 412 234T374 68Q374 43 381 35T402 26Q411 27 422 35Q443 55 463 131Q469 151 473 152Q475 153 483 153H487Q506 153 506 144Q506 138 501 117T481 63T449 13Q436 0 417 -8Q409 -10 393 -10Q359 -10 336 5T306 36L300 51Q299 52 296 50Q294 48 292 46Q233 -10 172 -10Q117 -10 75 30T33 157ZM351 328Q351 334 346 350T323 385T277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q217 26 254 59T298 110Q300 114 325 217T351 328'],
  
      // LATIN SMALL LETTER B
      0x62: [694,11,429,40,422,'73 647Q73 657 77 670T89 683Q90 683 161 688T234 694Q246 694 246 685T212 542Q204 508 195 472T180 418L176 399Q176 396 182 402Q231 442 283 442Q345 442 383 396T422 280Q422 169 343 79T173 -11Q123 -11 82 27T40 150V159Q40 180 48 217T97 414Q147 611 147 623T109 637Q104 637 101 637H96Q86 637 83 637T76 640T73 647ZM336 325V331Q336 405 275 405Q258 405 240 397T207 376T181 352T163 330L157 322L136 236Q114 150 114 114Q114 66 138 42Q154 26 178 26Q211 26 245 58Q270 81 285 114T318 219Q336 291 336 325'],
  
      // LATIN SMALL LETTER C
      0x63: [442,12,433,34,430,'34 159Q34 268 120 355T306 442Q362 442 394 418T427 355Q427 326 408 306T360 285Q341 285 330 295T319 325T330 359T352 380T366 386H367Q367 388 361 392T340 400T306 404Q276 404 249 390Q228 381 206 359Q162 315 142 235T121 119Q121 73 147 50Q169 26 205 26H209Q321 26 394 111Q403 121 406 121Q410 121 419 112T429 98T420 83T391 55T346 25T282 0T202 -11Q127 -11 81 37T34 159'],
  
      // LATIN SMALL LETTER D
      0x64: [694,10,520,33,523,'366 683Q367 683 438 688T511 694Q523 694 523 686Q523 679 450 384T375 83T374 68Q374 26 402 26Q411 27 422 35Q443 55 463 131Q469 151 473 152Q475 153 483 153H487H491Q506 153 506 145Q506 140 503 129Q490 79 473 48T445 8T417 -8Q409 -10 393 -10Q359 -10 336 5T306 36L300 51Q299 52 296 50Q294 48 292 46Q233 -10 172 -10Q117 -10 75 30T33 157Q33 205 53 255T101 341Q148 398 195 420T280 442Q336 442 364 400Q369 394 369 396Q370 400 396 505T424 616Q424 629 417 632T378 637H357Q351 643 351 645T353 664Q358 683 366 683ZM352 326Q329 405 277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q233 26 290 98L298 109L352 326'],
  
      // LATIN SMALL LETTER E
      0x65: [443,11,466,39,430,'39 168Q39 225 58 272T107 350T174 402T244 433T307 442H310Q355 442 388 420T421 355Q421 265 310 237Q261 224 176 223Q139 223 138 221Q138 219 132 186T125 128Q125 81 146 54T209 26T302 45T394 111Q403 121 406 121Q410 121 419 112T429 98T420 82T390 55T344 24T281 -1T205 -11Q126 -11 83 42T39 168ZM373 353Q367 405 305 405Q272 405 244 391T199 357T170 316T154 280T149 261Q149 260 169 260Q282 260 327 284T373 353'],
  
      // LATIN SMALL LETTER F
      0x66: [705,205,490,55,550,'118 -162Q120 -162 124 -164T135 -167T147 -168Q160 -168 171 -155T187 -126Q197 -99 221 27T267 267T289 382V385H242Q195 385 192 387Q188 390 188 397L195 425Q197 430 203 430T250 431Q298 431 298 432Q298 434 307 482T319 540Q356 705 465 705Q502 703 526 683T550 630Q550 594 529 578T487 561Q443 561 443 603Q443 622 454 636T478 657L487 662Q471 668 457 668Q445 668 434 658T419 630Q412 601 403 552T387 469T380 433Q380 431 435 431Q480 431 487 430T498 424Q499 420 496 407T491 391Q489 386 482 386T428 385H372L349 263Q301 15 282 -47Q255 -132 212 -173Q175 -205 139 -205Q107 -205 81 -186T55 -132Q55 -95 76 -78T118 -61Q162 -61 162 -103Q162 -122 151 -136T127 -157L118 -162'],
  
      // LATIN SMALL LETTER G
      0x67: [442,205,477,10,480,'311 43Q296 30 267 15T206 0Q143 0 105 45T66 160Q66 265 143 353T314 442Q361 442 401 394L404 398Q406 401 409 404T418 412T431 419T447 422Q461 422 470 413T480 394Q480 379 423 152T363 -80Q345 -134 286 -169T151 -205Q10 -205 10 -137Q10 -111 28 -91T74 -71Q89 -71 102 -80T116 -111Q116 -121 114 -130T107 -144T99 -154T92 -162L90 -164H91Q101 -167 151 -167Q189 -167 211 -155Q234 -144 254 -122T282 -75Q288 -56 298 -13Q311 35 311 43ZM384 328L380 339Q377 350 375 354T369 368T359 382T346 393T328 402T306 405Q262 405 221 352Q191 313 171 233T151 117Q151 38 213 38Q269 38 323 108L331 118L384 328'],
  
      // LATIN SMALL LETTER H
      0x68: [694,11,576,48,555,'137 683Q138 683 209 688T282 694Q294 694 294 685Q294 674 258 534Q220 386 220 383Q220 381 227 388Q288 442 357 442Q411 442 444 415T478 336Q478 285 440 178T402 50Q403 36 407 31T422 26Q450 26 474 56T513 138Q516 149 519 151T535 153Q555 153 555 145Q555 144 551 130Q535 71 500 33Q466 -10 419 -10H414Q367 -10 346 17T325 74Q325 90 361 192T398 345Q398 404 354 404H349Q266 404 205 306L198 293L164 158Q132 28 127 16Q114 -11 83 -11Q69 -11 59 -2T48 16Q48 30 121 320L195 616Q195 629 188 632T149 637H128Q122 643 122 645T124 664Q129 683 137 683'],
  
      // LATIN SMALL LETTER I
      0x69: [661,11,345,21,302,'184 600Q184 624 203 642T247 661Q265 661 277 649T290 619Q290 596 270 577T226 557Q211 557 198 567T184 600ZM21 287Q21 295 30 318T54 369T98 420T158 442Q197 442 223 419T250 357Q250 340 236 301T196 196T154 83Q149 61 149 51Q149 26 166 26Q175 26 185 29T208 43T235 78T260 137Q263 149 265 151T282 153Q302 153 302 143Q302 135 293 112T268 61T223 11T161 -11Q129 -11 102 10T74 74Q74 91 79 106T122 220Q160 321 166 341T173 380Q173 404 156 404H154Q124 404 99 371T61 287Q60 286 59 284T58 281T56 279T53 278T49 278T41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER J
      0x6A: [661,204,412,-12,403,'297 596Q297 627 318 644T361 661Q378 661 389 651T403 623Q403 595 384 576T340 557Q322 557 310 567T297 596ZM288 376Q288 405 262 405Q240 405 220 393T185 362T161 325T144 293L137 279Q135 278 121 278H107Q101 284 101 286T105 299Q126 348 164 391T252 441Q253 441 260 441T272 442Q296 441 316 432Q341 418 354 401T367 348V332L318 133Q267 -67 264 -75Q246 -125 194 -164T75 -204Q25 -204 7 -183T-12 -137Q-12 -110 7 -91T53 -71Q70 -71 82 -81T95 -112Q95 -148 63 -167Q69 -168 77 -168Q111 -168 139 -140T182 -74L193 -32Q204 11 219 72T251 197T278 308T289 365Q289 372 288 376'],
  
      // LATIN SMALL LETTER K
      0x6B: [694,11,521,48,503,'121 647Q121 657 125 670T137 683Q138 683 209 688T282 694Q294 694 294 686Q294 679 244 477Q194 279 194 272Q213 282 223 291Q247 309 292 354T362 415Q402 442 438 442Q468 442 485 423T503 369Q503 344 496 327T477 302T456 291T438 288Q418 288 406 299T394 328Q394 353 410 369T442 390L458 393Q446 405 434 405H430Q398 402 367 380T294 316T228 255Q230 254 243 252T267 246T293 238T320 224T342 206T359 180T365 147Q365 130 360 106T354 66Q354 26 381 26Q429 26 459 145Q461 153 479 153H483Q499 153 499 144Q499 139 496 130Q455 -11 378 -11Q333 -11 305 15T277 90Q277 108 280 121T283 145Q283 167 269 183T234 206T200 217T182 220H180Q168 178 159 139T145 81T136 44T129 20T122 7T111 -2Q98 -11 83 -11Q66 -11 57 -1T48 16Q48 26 85 176T158 471L195 616Q196 629 188 632T149 637H144Q134 637 131 637T124 640T121 647'],
  
      // LATIN SMALL LETTER L
      0x6C: [695,12,298,38,266,'117 59Q117 26 142 26Q179 26 205 131Q211 151 215 152Q217 153 225 153H229Q238 153 241 153T246 151T248 144Q247 138 245 128T234 90T214 43T183 6T137 -11Q101 -11 70 11T38 85Q38 97 39 102L104 360Q167 615 167 623Q167 626 166 628T162 632T157 634T149 635T141 636T132 637T122 637Q112 637 109 637T101 638T95 641T94 647Q94 649 96 661Q101 680 107 682T179 688Q194 689 213 690T243 693T254 694Q266 694 266 686Q266 675 193 386T118 83Q118 81 118 75T117 65V59'],
  
      // LATIN SMALL LETTER M
      0x6D: [443,11,878,21,857,'21 287Q22 293 24 303T36 341T56 388T88 425T132 442T175 435T205 417T221 395T229 376L231 369Q231 367 232 367L243 378Q303 442 384 442Q401 442 415 440T441 433T460 423T475 411T485 398T493 385T497 373T500 364T502 357L510 367Q573 442 659 442Q713 442 746 415T780 336Q780 285 742 178T704 50Q705 36 709 31T724 26Q752 26 776 56T815 138Q818 149 821 151T837 153Q857 153 857 145Q857 144 853 130Q845 101 831 73T785 17T716 -10Q669 -10 648 17T627 73Q627 92 663 193T700 345Q700 404 656 404H651Q565 404 506 303L499 291L466 157Q433 26 428 16Q415 -11 385 -11Q372 -11 364 -4T353 8T350 18Q350 29 384 161L420 307Q423 322 423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 112 181Q151 335 151 342Q154 357 154 369Q154 405 129 405Q107 405 92 377T69 316T57 280Q55 278 41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER N
      0x6E: [443,11,600,21,580,'21 287Q22 293 24 303T36 341T56 388T89 425T135 442Q171 442 195 424T225 390T231 369Q231 367 232 367L243 378Q304 442 382 442Q436 442 469 415T503 336T465 179T427 52Q427 26 444 26Q450 26 453 27Q482 32 505 65T540 145Q542 153 560 153Q580 153 580 145Q580 144 576 130Q568 101 554 73T508 17T439 -10Q392 -10 371 17T350 73Q350 92 386 193T423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 112 180T152 343Q153 348 153 366Q153 405 129 405Q91 405 66 305Q60 285 60 284Q58 278 41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER O
      0x6F: [441,11,485,34,476,'201 -11Q126 -11 80 38T34 156Q34 221 64 279T146 380Q222 441 301 441Q333 441 341 440Q354 437 367 433T402 417T438 387T464 338T476 268Q476 161 390 75T201 -11ZM121 120Q121 70 147 48T206 26Q250 26 289 58T351 142Q360 163 374 216T388 308Q388 352 370 375Q346 405 306 405Q243 405 195 347Q158 303 140 230T121 120'],
  
      // LATIN SMALL LETTER P
      0x70: [443,194,503,-39,497,'23 287Q24 290 25 295T30 317T40 348T55 381T75 411T101 433T134 442Q209 442 230 378L240 387Q302 442 358 442Q423 442 460 395T497 281Q497 173 421 82T249 -10Q227 -10 210 -4Q199 1 187 11T168 28L161 36Q160 35 139 -51T118 -138Q118 -144 126 -145T163 -148H188Q194 -155 194 -157T191 -175Q188 -187 185 -190T172 -194Q170 -194 161 -194T127 -193T65 -192Q-5 -192 -24 -194H-32Q-39 -187 -39 -183Q-37 -156 -26 -148H-6Q28 -147 33 -136Q36 -130 94 103T155 350Q156 355 156 364Q156 405 131 405Q109 405 94 377T71 316T59 280Q57 278 43 278H29Q23 284 23 287ZM178 102Q200 26 252 26Q282 26 310 49T356 107Q374 141 392 215T411 325V331Q411 405 350 405Q339 405 328 402T306 393T286 380T269 365T254 350T243 336T235 326L232 322Q232 321 229 308T218 264T204 212Q178 106 178 102'],
  
      // LATIN SMALL LETTER Q
      0x71: [442,194,446,33,460,'33 157Q33 258 109 349T280 441Q340 441 372 389Q373 390 377 395T388 406T404 418Q438 442 450 442Q454 442 457 439T460 434Q460 425 391 149Q320 -135 320 -139Q320 -147 365 -148H390Q396 -156 396 -157T393 -175Q389 -188 383 -194H370Q339 -192 262 -192Q234 -192 211 -192T174 -192T157 -193Q143 -193 143 -185Q143 -182 145 -170Q149 -154 152 -151T172 -148Q220 -148 230 -141Q238 -136 258 -53T279 32Q279 33 272 29Q224 -10 172 -10Q117 -10 75 30T33 157ZM352 326Q329 405 277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q233 26 290 98L298 109L352 326'],
  
      // LATIN SMALL LETTER R
      0x72: [443,11,451,21,430,'21 287Q22 290 23 295T28 317T38 348T53 381T73 411T99 433T132 442Q161 442 183 430T214 408T225 388Q227 382 228 382T236 389Q284 441 347 441H350Q398 441 422 400Q430 381 430 363Q430 333 417 315T391 292T366 288Q346 288 334 299T322 328Q322 376 378 392Q356 405 342 405Q286 405 239 331Q229 315 224 298T190 165Q156 25 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 114 189T154 366Q154 405 128 405Q107 405 92 377T68 316T57 280Q55 278 41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER S
      0x73: [443,10,469,53,419,'131 289Q131 321 147 354T203 415T300 442Q362 442 390 415T419 355Q419 323 402 308T364 292Q351 292 340 300T328 326Q328 342 337 354T354 372T367 378Q368 378 368 379Q368 382 361 388T336 399T297 405Q249 405 227 379T204 326Q204 301 223 291T278 274T330 259Q396 230 396 163Q396 135 385 107T352 51T289 7T195 -10Q118 -10 86 19T53 87Q53 126 74 143T118 160Q133 160 146 151T160 120Q160 94 142 76T111 58Q109 57 108 57T107 55Q108 52 115 47T146 34T201 27Q237 27 263 38T301 66T318 97T323 122Q323 150 302 164T254 181T195 196T148 231Q131 256 131 289'],
  
      // LATIN SMALL LETTER T
      0x74: [626,11,361,19,330,'26 385Q19 392 19 395Q19 399 22 411T27 425Q29 430 36 430T87 431H140L159 511Q162 522 166 540T173 566T179 586T187 603T197 615T211 624T229 626Q247 625 254 615T261 596Q261 589 252 549T232 470L222 433Q222 431 272 431H323Q330 424 330 420Q330 398 317 385H210L174 240Q135 80 135 68Q135 26 162 26Q197 26 230 60T283 144Q285 150 288 151T303 153H307Q322 153 322 145Q322 142 319 133Q314 117 301 95T267 48T216 6T155 -11Q125 -11 98 4T59 56Q57 64 57 83V101L92 241Q127 382 128 383Q128 385 77 385H26'],
  
      // LATIN SMALL LETTER U
      0x75: [442,11,572,21,551,'21 287Q21 295 30 318T55 370T99 420T158 442Q204 442 227 417T250 358Q250 340 216 246T182 105Q182 62 196 45T238 27T291 44T328 78L339 95Q341 99 377 247Q407 367 413 387T427 416Q444 431 463 431Q480 431 488 421T496 402L420 84Q419 79 419 68Q419 43 426 35T447 26Q469 29 482 57T512 145Q514 153 532 153Q551 153 551 144Q550 139 549 130T540 98T523 55T498 17T462 -8Q454 -10 438 -10Q372 -10 347 46Q345 45 336 36T318 21T296 6T267 -6T233 -11Q189 -11 155 7Q103 38 103 113Q103 170 138 262T173 379Q173 380 173 381Q173 390 173 393T169 400T158 404H154Q131 404 112 385T82 344T65 302T57 280Q55 278 41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER V
      0x76: [443,11,485,21,467,'173 380Q173 405 154 405Q130 405 104 376T61 287Q60 286 59 284T58 281T56 279T53 278T49 278T41 278H27Q21 284 21 287Q21 294 29 316T53 368T97 419T160 441Q202 441 225 417T249 361Q249 344 246 335Q246 329 231 291T200 202T182 113Q182 86 187 69Q200 26 250 26Q287 26 319 60T369 139T398 222T409 277Q409 300 401 317T383 343T365 361T357 383Q357 405 376 424T417 443Q436 443 451 425T467 367Q467 340 455 284T418 159T347 40T241 -11Q177 -11 139 22Q102 54 102 117Q102 148 110 181T151 298Q173 362 173 380'],
  
      // LATIN SMALL LETTER W
      0x77: [443,11,716,21,690,'580 385Q580 406 599 424T641 443Q659 443 674 425T690 368Q690 339 671 253Q656 197 644 161T609 80T554 12T482 -11Q438 -11 404 5T355 48Q354 47 352 44Q311 -11 252 -11Q226 -11 202 -5T155 14T118 53T104 116Q104 170 138 262T173 379Q173 380 173 381Q173 390 173 393T169 400T158 404H154Q131 404 112 385T82 344T65 302T57 280Q55 278 41 278H27Q21 284 21 287Q21 293 29 315T52 366T96 418T161 441Q204 441 227 416T250 358Q250 340 217 250T184 111Q184 65 205 46T258 26Q301 26 334 87L339 96V119Q339 122 339 128T340 136T341 143T342 152T345 165T348 182T354 206T362 238T373 281Q402 395 406 404Q419 431 449 431Q468 431 475 421T483 402Q483 389 454 274T422 142Q420 131 420 107V100Q420 85 423 71T442 42T487 26Q558 26 600 148Q609 171 620 213T632 273Q632 306 619 325T593 357T580 385'],
  
      // LATIN SMALL LETTER X
      0x78: [442,11,572,35,522,'52 289Q59 331 106 386T222 442Q257 442 286 424T329 379Q371 442 430 442Q467 442 494 420T522 361Q522 332 508 314T481 292T458 288Q439 288 427 299T415 328Q415 374 465 391Q454 404 425 404Q412 404 406 402Q368 386 350 336Q290 115 290 78Q290 50 306 38T341 26Q378 26 414 59T463 140Q466 150 469 151T485 153H489Q504 153 504 145Q504 144 502 134Q486 77 440 33T333 -11Q263 -11 227 52Q186 -10 133 -10H127Q78 -10 57 16T35 71Q35 103 54 123T99 143Q142 143 142 101Q142 81 130 66T107 46T94 41L91 40Q91 39 97 36T113 29T132 26Q168 26 194 71Q203 87 217 139T245 247T261 313Q266 340 266 352Q266 380 251 392T217 404Q177 404 142 372T93 290Q91 281 88 280T72 278H58Q52 284 52 289'],
  
      // LATIN SMALL LETTER Y
      0x79: [443,205,490,21,497,'21 287Q21 301 36 335T84 406T158 442Q199 442 224 419T250 355Q248 336 247 334Q247 331 231 288T198 191T182 105Q182 62 196 45T238 27Q261 27 281 38T312 61T339 94Q339 95 344 114T358 173T377 247Q415 397 419 404Q432 431 462 431Q475 431 483 424T494 412T496 403Q496 390 447 193T391 -23Q363 -106 294 -155T156 -205Q111 -205 77 -183T43 -117Q43 -95 50 -80T69 -58T89 -48T106 -45Q150 -45 150 -87Q150 -107 138 -122T115 -142T102 -147L99 -148Q101 -153 118 -160T152 -167H160Q177 -167 186 -165Q219 -156 247 -127T290 -65T313 -9T321 21L315 17Q309 13 296 6T270 -6Q250 -11 231 -11Q185 -11 150 11T104 82Q103 89 103 113Q103 170 138 262T173 379Q173 380 173 381Q173 390 173 393T169 400T158 404H154Q131 404 112 385T82 344T65 302T57 280Q55 278 41 278H27Q21 284 21 287'],
  
      // LATIN SMALL LETTER Z
      0x7A: [442,11,465,35,468,'347 338Q337 338 294 349T231 360Q211 360 197 356T174 346T162 335T155 324L153 320Q150 317 138 317Q117 317 117 325Q117 330 120 339Q133 378 163 406T229 440Q241 442 246 442Q271 442 291 425T329 392T367 375Q389 375 411 408T434 441Q435 442 449 442H462Q468 436 468 434Q468 430 463 420T449 399T432 377T418 358L411 349Q368 298 275 214T160 106L148 94L163 93Q185 93 227 82T290 71Q328 71 360 90T402 140Q406 149 409 151T424 153Q443 153 443 143Q443 138 442 134Q425 72 376 31T278 -11Q252 -11 232 6T193 40T155 57Q111 57 76 -3Q70 -11 59 -11H54H41Q35 -5 35 -2Q35 13 93 84Q132 129 225 214T340 322Q352 338 347 338'],
  
      // GREEK CAPITAL LETTER GAMMA
      0x393: [680,-1,615,31,721,'49 1Q31 1 31 10Q31 12 34 24Q39 43 44 45Q48 46 59 46H65Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 661Q197 674 203 680H714Q721 676 721 669Q721 664 708 557T694 447Q692 440 674 440H662Q655 445 655 454Q655 455 658 480T661 534Q661 572 652 592Q638 619 603 626T501 634H471Q398 633 393 630Q389 628 386 622Q385 619 315 341T245 60Q245 46 333 46H345Q366 46 366 35Q366 33 363 21T358 6Q356 1 339 1Q334 1 292 1T187 2Q122 2 88 2T49 1'],
  
      // GREEK CAPITAL LETTER THETA
      0x398: [704,22,763,50,740,'740 435Q740 320 676 213T511 42T304 -22Q207 -22 138 35T51 201Q50 209 50 244Q50 346 98 438T227 601Q351 704 476 704Q514 704 524 703Q621 689 680 617T740 435ZM640 466Q640 523 625 565T583 628T532 658T479 668Q370 668 273 559T151 255Q150 245 150 213Q150 156 165 116T207 55T259 26T313 17Q385 17 451 63T561 184Q590 234 615 312T640 466ZM510 276Q510 278 512 288L515 298Q515 299 384 299H253L250 285Q246 271 244 268T231 265H227Q216 265 214 266T207 274Q207 278 223 345T244 416Q247 419 260 419H263Q280 419 280 408Q280 406 278 396L275 386Q275 385 406 385H537L540 399Q544 413 546 416T559 419H563Q574 419 576 418T583 410Q583 403 566 339Q549 271 544 267Q542 265 538 265H530H527Q510 265 510 276'],
  
      // GREEK CAPITAL LETTER LAMDA
      0x39B: [716,0,694,35,670,'135 2Q114 2 90 2T60 1Q35 1 35 11Q35 28 42 40Q45 46 55 46Q119 46 151 94Q153 97 325 402T498 709Q505 716 526 716Q543 716 549 710Q550 709 560 548T580 224T591 57Q594 52 595 52Q603 47 638 46H663Q670 39 670 35Q669 12 657 0H644Q613 2 530 2Q497 2 469 2T424 2T405 1Q388 1 388 10Q388 15 391 24Q392 27 393 32T395 38T397 41T401 44T406 45T415 46Q473 46 487 64L472 306Q468 365 465 426T459 518L457 550Q456 550 328 322T198 88Q196 80 196 77Q196 49 243 46Q261 46 261 35Q261 34 259 22Q256 7 254 4T240 0Q237 0 211 1T135 2'],
  
      // GREEK CAPITAL LETTER XI
      0x39E: [678,0,742,53,777,'222 668Q222 670 229 677H654Q677 677 705 677T740 678Q764 678 770 676T777 667Q777 662 764 594Q761 579 757 559T751 528L749 519Q747 512 729 512H717Q710 519 710 525Q712 532 715 559T719 591Q718 595 711 595Q682 598 486 598Q252 598 246 592Q239 587 228 552L216 517Q214 512 197 512H185Q178 517 178 522Q178 524 198 591T222 668ZM227 262Q218 262 215 262T209 266L207 270L227 356Q247 435 250 439Q253 443 260 443H267H280Q287 438 287 433Q287 430 285 420T280 402L278 393Q278 392 431 392H585L590 415Q595 436 598 439T612 443H628Q635 438 635 433Q635 431 615 351T594 268Q592 262 575 262H572Q556 262 556 272Q556 280 560 293L565 313H258L252 292Q248 271 245 267T230 262H227ZM60 0Q53 4 53 11Q53 14 68 89T84 169Q88 176 98 176H104H116Q123 169 123 163Q122 160 117 127T112 88Q112 80 243 80H351H454Q554 80 574 81T597 88V89Q603 100 610 121T622 157T630 174Q633 176 646 176H658Q665 171 665 166Q665 164 643 89T618 7Q616 2 607 1T548 0H335H60'],
  
      // GREEK CAPITAL LETTER PI
      0x3A0: [681,0,831,31,887,'48 1Q31 1 31 10Q31 12 34 24Q39 43 44 45Q48 46 59 46H65Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 661Q197 674 203 680H541Q621 680 709 680T812 681Q841 681 855 681T877 679T886 676T887 670Q887 663 885 656Q880 637 875 635Q871 634 860 634H854Q827 634 794 631Q780 628 775 619Q773 614 704 338T634 58Q634 51 638 51Q646 48 692 46H723Q729 38 729 37T726 19Q722 6 716 0H701Q664 2 567 2Q533 2 504 2T458 2T437 1Q420 1 420 10Q420 15 423 24Q428 43 433 45Q437 46 448 46H454Q481 46 514 49Q528 52 533 61Q536 67 572 209T642 491T678 632Q678 634 533 634H388Q387 631 316 347T245 59Q245 55 246 54T253 50T270 48T303 46H334Q340 38 340 37T337 19Q333 6 327 0H312Q275 2 178 2Q144 2 115 2T69 2T48 1'],
  
      // GREEK CAPITAL LETTER SIGMA
      0x3A3: [683,0,780,58,806,'65 0Q58 4 58 11Q58 16 114 67Q173 119 222 164L377 304Q378 305 340 386T261 552T218 644Q217 648 219 660Q224 678 228 681Q231 683 515 683H799Q804 678 806 674Q806 667 793 559T778 448Q774 443 759 443Q747 443 743 445T739 456Q739 458 741 477T743 516Q743 552 734 574T710 609T663 627T596 635T502 637Q480 637 469 637H339Q344 627 411 486T478 341V339Q477 337 477 336L457 318Q437 300 398 265T322 196L168 57Q167 56 188 56T258 56H359Q426 56 463 58T537 69T596 97T639 146T680 225Q686 243 689 246T702 250H705Q726 250 726 239Q726 238 683 123T639 5Q637 1 610 1Q577 0 348 0H65'],
  
      // GREEK CAPITAL LETTER UPSILON
      0x3A5: [706,0,583,28,700,'45 535Q34 535 31 536T28 544Q28 554 39 578T70 631T126 683T206 705Q230 705 251 698T295 671T330 612T344 514Q344 477 342 473V472Q343 472 347 480T361 509T380 547Q471 704 596 704Q615 704 625 702Q659 692 679 663T700 595Q700 565 696 552T687 537T670 535Q656 535 653 536T649 543Q649 544 649 550T650 562Q650 589 629 605T575 621Q502 621 448 547T365 361Q290 70 290 60Q290 46 379 46H404Q410 40 410 39T408 19Q404 6 398 0H381Q340 2 225 2Q184 2 149 2T94 2T69 1Q61 1 58 1T53 4T51 10Q51 11 53 23Q54 25 55 30T56 36T58 40T60 43T62 44T67 46T73 46T82 46H89Q144 46 163 49T190 62L198 93Q206 124 217 169T241 262T262 350T274 404Q281 445 281 486V494Q281 621 185 621Q147 621 116 601T74 550Q71 539 66 537T45 535'],
  
      // GREEK CAPITAL LETTER PHI
      0x3A6: [683,0,667,24,642,'356 624Q356 637 267 637H243Q237 642 237 645T239 664Q243 677 249 683H264Q342 681 429 681Q565 681 571 683H583Q589 677 589 674T587 656Q582 641 578 637H540Q516 637 504 637T479 633T463 630T454 623T448 613T443 597T438 576Q436 566 434 556T430 539L428 533Q442 533 472 526T543 502T613 451T642 373Q642 301 567 241T386 158L336 150Q332 150 331 146Q310 66 310 60Q310 46 399 46H424Q430 40 430 39T428 19Q424 6 418 0H401Q360 2 247 2Q207 2 173 2T119 2T95 1Q87 1 84 1T79 4T77 10Q77 11 79 23Q80 25 81 30T82 36T84 40T86 43T88 44T93 46T99 46T108 46H115Q170 46 189 49T216 62Q220 74 228 107L239 150L223 152Q139 164 82 205T24 311Q24 396 125 462Q207 517 335 533L346 578Q356 619 356 624ZM130 291Q130 203 241 188H249Q249 190 287 342L325 495H324Q313 495 291 491T229 466T168 414Q130 357 130 291ZM536 393Q536 440 507 463T418 496L341 187L351 189Q443 201 487 255Q536 314 536 393'],
  
      // GREEK CAPITAL LETTER PSI
      0x3A8: [683,0,612,21,692,'216 151Q48 174 48 329Q48 361 56 403T65 458Q65 482 58 494T43 507T28 510T21 520Q21 528 23 534T29 544L32 546H72H94Q110 546 119 544T139 536T154 514T159 476V465Q159 445 149 399T138 314Q142 229 197 201Q223 187 226 190L233 218Q240 246 253 300T280 407Q333 619 333 625Q333 637 244 637H220Q214 642 214 645T216 664Q220 677 226 683H241Q321 681 405 681Q543 681 549 683H560Q566 677 566 674T564 656Q559 641 555 637H517Q448 636 436 628Q429 623 423 600T373 404L320 192Q370 201 419 248Q451 281 469 317T500 400T518 457Q529 486 542 505T569 532T594 543T621 546H644H669Q692 546 692 536Q691 509 676 509Q623 509 593 399Q587 377 579 355T552 301T509 244T446 195T359 159Q324 151 314 151Q311 151 310 150T298 106T287 60Q287 46 376 46H401Q407 40 407 39T405 19Q401 6 395 0H378Q337 2 224 2Q184 2 150 2T96 2T72 1Q64 1 61 1T56 4T54 10Q54 11 56 23Q57 25 58 30T59 36T61 40T63 43T65 44T70 46T76 46T85 46H92Q147 46 166 49T193 62L204 106Q216 149 216 151'],
  
      // GREEK CAPITAL LETTER OMEGA
      0x3A9: [704,0,772,80,786,'125 84Q127 78 194 76H243V78Q243 122 208 215T165 350Q164 359 162 389Q162 522 272 610Q328 656 396 680T525 704Q628 704 698 661Q734 637 755 601T781 544T786 504Q786 439 747 374T635 226T537 109Q518 81 518 77Q537 76 557 76Q608 76 620 78T640 92Q646 100 656 119T673 155T683 172Q690 173 698 173Q718 173 718 162Q718 161 681 82T642 2Q639 0 550 0H461Q455 5 455 9T458 28Q472 78 510 149T584 276T648 402T677 525Q677 594 636 631T530 668Q476 668 423 641T335 568Q284 499 271 400Q270 388 270 348Q270 298 277 228T285 115Q285 82 280 49T271 6Q269 1 258 1T175 0H87Q83 3 80 7V18Q80 22 82 98Q84 156 85 163T91 172Q94 173 104 173T119 172Q124 169 124 126Q125 104 125 84'],
  
      // GREEK SMALL LETTER ALPHA
      0x3B1: [442,11,640,34,603,'34 156Q34 270 120 356T309 442Q379 442 421 402T478 304Q484 275 485 237V208Q534 282 560 374Q564 388 566 390T582 393Q603 393 603 385Q603 376 594 346T558 261T497 161L486 147L487 123Q489 67 495 47T514 26Q528 28 540 37T557 60Q559 67 562 68T577 70Q597 70 597 62Q597 56 591 43Q579 19 556 5T512 -10H505Q438 -10 414 62L411 69L400 61Q390 53 370 41T325 18T267 -2T203 -11Q124 -11 79 39T34 156ZM208 26Q257 26 306 47T379 90L403 112Q401 255 396 290Q382 405 304 405Q235 405 183 332Q156 292 139 224T121 120Q121 71 146 49T208 26'],
  
      // GREEK SMALL LETTER BETA
      0x3B2: [705,194,566,23,573,'29 -194Q23 -188 23 -186Q23 -183 102 134T186 465Q208 533 243 584T309 658Q365 705 429 705H431Q493 705 533 667T573 570Q573 465 469 396L482 383Q533 332 533 252Q533 139 448 65T257 -10Q227 -10 203 -2T165 17T143 40T131 59T126 65L62 -188Q60 -194 42 -194H29ZM353 431Q392 431 427 419L432 422Q436 426 439 429T449 439T461 453T472 471T484 495T493 524T501 560Q503 569 503 593Q503 611 502 616Q487 667 426 667Q384 667 347 643T286 582T247 514T224 455Q219 439 186 308T152 168Q151 163 151 147Q151 99 173 68Q204 26 260 26Q302 26 349 51T425 137Q441 171 449 214T457 279Q457 337 422 372Q380 358 347 358H337Q258 358 258 389Q258 396 261 403Q275 431 353 431'],
  
      // GREEK SMALL LETTER GAMMA
      0x3B3: [441,216,518,11,543,'31 249Q11 249 11 258Q11 275 26 304T66 365T129 418T206 441Q233 441 239 440Q287 429 318 386T371 255Q385 195 385 170Q385 166 386 166L398 193Q418 244 443 300T486 391T508 430Q510 431 524 431H537Q543 425 543 422Q543 418 522 378T463 251T391 71Q385 55 378 6T357 -100Q341 -165 330 -190T303 -216Q286 -216 286 -188Q286 -138 340 32L346 51L347 69Q348 79 348 100Q348 257 291 317Q251 355 196 355Q148 355 108 329T51 260Q49 251 47 251Q45 249 31 249'],
  
      // GREEK SMALL LETTER DELTA
      0x3B4: [717,10,444,36,451,'195 609Q195 656 227 686T302 717Q319 716 351 709T407 697T433 690Q451 682 451 662Q451 644 438 628T403 612Q382 612 348 641T288 671T249 657T235 628Q235 584 334 463Q401 379 401 292Q401 169 340 80T205 -10H198Q127 -10 83 36T36 153Q36 286 151 382Q191 413 252 434Q252 435 245 449T230 481T214 521T201 566T195 609ZM112 130Q112 83 136 55T204 27Q233 27 256 51T291 111T309 178T316 232Q316 267 309 298T295 344T269 400L259 396Q215 381 183 342T137 256T118 179T112 130'],
  
      // GREEK SMALL LETTER EPSILON
      0x3B5: [452,23,466,27,428,'190 -22Q124 -22 76 11T27 107Q27 174 97 232L107 239L99 248Q76 273 76 304Q76 364 144 408T290 452H302Q360 452 405 421Q428 405 428 392Q428 381 417 369T391 356Q382 356 371 365T338 383T283 392Q217 392 167 368T116 308Q116 289 133 272Q142 263 145 262T157 264Q188 278 238 278H243Q308 278 308 247Q308 206 223 206Q177 206 142 219L132 212Q68 169 68 112Q68 39 201 39Q253 39 286 49T328 72T345 94T362 105Q376 103 376 88Q376 79 365 62T334 26T275 -8T190 -22'],
  
      // GREEK SMALL LETTER ZETA
      0x3B6: [704,204,438,44,471,'296 643Q298 704 324 704Q342 704 342 687Q342 682 339 664T336 633Q336 623 337 618T338 611Q339 612 341 612Q343 614 354 616T374 618L384 619H394Q471 619 471 586Q467 548 386 546H372Q338 546 320 564L311 558Q235 506 175 398T114 190Q114 171 116 155T125 127T137 104T153 86T171 72T192 61T213 53T235 46T256 39L322 16Q389 -10 389 -80Q389 -119 364 -154T300 -202Q292 -204 274 -204Q247 -204 225 -196Q210 -192 193 -182T172 -167Q167 -159 173 -148Q180 -139 191 -139Q195 -139 221 -153T283 -168Q298 -166 310 -152T322 -117Q322 -91 302 -75T250 -51T183 -29T116 4T65 62T44 160Q44 287 121 410T293 590L302 595Q296 613 296 643'],
  
      // GREEK SMALL LETTER ETA
      0x3B7: [443,216,497,21,503,'21 287Q22 290 23 295T28 317T38 348T53 381T73 411T99 433T132 442Q156 442 175 435T205 417T221 395T229 376L231 369Q231 367 232 367L243 378Q304 442 382 442Q436 442 469 415T503 336V326Q503 302 439 53Q381 -182 377 -189Q364 -216 332 -216Q319 -216 310 -208T299 -186Q299 -177 358 57L420 307Q423 322 423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 114 189T154 366Q154 405 128 405Q107 405 92 377T68 316T57 280Q55 278 41 278H27Q21 284 21 287'],
  
      // GREEK SMALL LETTER THETA
      0x3B8: [705,10,469,35,462,'35 200Q35 302 74 415T180 610T319 704Q320 704 327 704T339 705Q393 701 423 656Q462 596 462 495Q462 380 417 261T302 66T168 -10H161Q125 -10 99 10T60 63T41 130T35 200ZM383 566Q383 668 330 668Q294 668 260 623T204 521T170 421T157 371Q206 370 254 370L351 371Q352 372 359 404T375 484T383 566ZM113 132Q113 26 166 26Q181 26 198 36T239 74T287 161T335 307L340 324H145Q145 321 136 286T120 208T113 132'],
  
      // GREEK SMALL LETTER IOTA
      0x3B9: [442,10,354,48,333,'139 -10Q111 -10 92 0T64 25T52 52T48 74Q48 89 55 109T85 199T135 375L137 384Q139 394 140 397T145 409T151 422T160 431T173 439T190 442Q202 442 213 435T225 410Q225 404 214 358T181 238T137 107Q126 74 126 54Q126 43 126 39T130 31T142 27H147Q206 27 255 78Q272 98 281 114T290 138T295 149T313 153Q321 153 324 153T329 152T332 149T332 143Q332 106 276 48T145 -10H139'],
  
      // GREEK SMALL LETTER KAPPA
      0x3BA: [442,11,576,48,554,'83 -11Q70 -11 62 -4T51 8T49 17Q49 30 96 217T147 414Q160 442 193 442Q205 441 213 435T223 422T225 412Q225 401 208 337L192 270Q193 269 208 277T235 292Q252 304 306 349T396 412T467 431Q489 431 500 420T512 391Q512 366 494 347T449 327Q430 327 418 338T405 368Q405 370 407 380L397 375Q368 360 315 315L253 266L240 257H245Q262 257 300 251T366 230Q422 203 422 150Q422 140 417 114T411 67Q411 26 437 26Q484 26 513 137Q516 149 519 151T535 153Q554 153 554 144Q554 121 527 64T457 -7Q447 -10 431 -10Q386 -10 360 17T333 90Q333 108 336 122T339 146Q339 170 320 186T271 209T222 218T185 221H180L155 122Q129 22 126 16Q113 -11 83 -11'],
  
      // GREEK SMALL LETTER LAMDA
      0x3BB: [694,12,583,47,557,'166 673Q166 685 183 694H202Q292 691 316 644Q322 629 373 486T474 207T524 67Q531 47 537 34T546 15T551 6T555 2T556 -2T550 -11H482Q457 3 450 18T399 152L354 277L340 262Q327 246 293 207T236 141Q211 112 174 69Q123 9 111 -1T83 -12Q47 -12 47 20Q47 37 61 52T199 187Q229 216 266 252T321 306L338 322Q338 323 288 462T234 612Q214 657 183 657Q166 657 166 673'],
  
      // GREEK SMALL LETTER MU
      0x3BC: [442,216,603,23,580,'58 -216Q44 -216 34 -208T23 -186Q23 -176 96 116T173 414Q186 442 219 442Q231 441 239 435T249 423T251 413Q251 401 220 279T187 142Q185 131 185 107V99Q185 26 252 26Q261 26 270 27T287 31T302 38T315 45T327 55T338 65T348 77T356 88T365 100L372 110L408 253Q444 395 448 404Q461 431 491 431Q504 431 512 424T523 412T525 402L449 84Q448 79 448 68Q448 43 455 35T476 26Q485 27 496 35Q517 55 537 131Q543 151 547 152Q549 153 557 153H561Q580 153 580 144Q580 138 575 117T555 63T523 13Q510 0 491 -8Q483 -10 467 -10Q446 -10 429 -4T402 11T385 29T376 44T374 51L368 45Q362 39 350 30T324 12T288 -4T246 -11Q199 -11 153 12L129 -85Q108 -167 104 -180T92 -202Q76 -216 58 -216'],
  
      // GREEK SMALL LETTER NU
      0x3BD: [442,2,494,45,530,'74 431Q75 431 146 436T219 442Q231 442 231 434Q231 428 185 241L137 51H140L150 55Q161 59 177 67T214 86T261 119T312 165Q410 264 445 394Q458 442 496 442Q509 442 519 434T530 411Q530 390 516 352T469 262T388 162T267 70T106 5Q81 -2 71 -2Q66 -2 59 -1T51 1Q45 5 45 11Q45 13 88 188L132 364Q133 377 125 380T86 385H65Q59 391 59 393T61 412Q65 431 74 431'],
  
      // GREEK SMALL LETTER XI
      0x3BE: [704,205,438,21,443,'268 632Q268 704 296 704Q314 704 314 687Q314 682 311 664T308 635T309 620V616H315Q342 619 360 619Q443 619 443 586Q439 548 358 546H344Q326 546 317 549T290 566Q257 550 226 505T195 405Q195 381 201 364T211 342T218 337Q266 347 298 347Q375 347 375 314Q374 297 359 288T327 277T280 275Q234 275 208 283L195 286Q149 260 119 214T88 130Q88 116 90 108Q101 79 129 63T229 20Q238 17 243 15Q337 -21 354 -33Q383 -53 383 -94Q383 -137 351 -171T273 -205Q240 -205 202 -190T158 -167Q156 -163 156 -159Q156 -151 161 -146T176 -140Q182 -140 189 -143Q232 -168 274 -168Q286 -168 292 -165Q313 -151 313 -129Q313 -112 301 -104T232 -75Q214 -68 204 -64Q198 -62 171 -52T136 -38T107 -24T78 -8T56 12T36 37T26 66T21 103Q21 149 55 206T145 301L154 307L148 313Q141 319 136 323T124 338T111 358T103 382T99 413Q99 471 143 524T259 602L271 607Q268 618 268 632'],
  
      // GREEK SMALL LETTER OMICRON
      0x3BF: [441,11,485,34,476,'201 -11Q126 -11 80 38T34 156Q34 221 64 279T146 380Q222 441 301 441Q333 441 341 440Q354 437 367 433T402 417T438 387T464 338T476 268Q476 161 390 75T201 -11ZM121 120Q121 70 147 48T206 26Q250 26 289 58T351 142Q360 163 374 216T388 308Q388 352 370 375Q346 405 306 405Q243 405 195 347Q158 303 140 230T121 120'],
  
      // GREEK SMALL LETTER PI
      0x3C0: [431,11,570,19,573,'132 -11Q98 -11 98 22V33L111 61Q186 219 220 334L228 358H196Q158 358 142 355T103 336Q92 329 81 318T62 297T53 285Q51 284 38 284Q19 284 19 294Q19 300 38 329T93 391T164 429Q171 431 389 431Q549 431 553 430Q573 423 573 402Q573 371 541 360Q535 358 472 358H408L405 341Q393 269 393 222Q393 170 402 129T421 65T431 37Q431 20 417 5T381 -10Q370 -10 363 -7T347 17T331 77Q330 86 330 121Q330 170 339 226T357 318T367 358H269L268 354Q268 351 249 275T206 114T175 17Q164 -11 132 -11'],
  
      // GREEK SMALL LETTER RHO
      0x3C1: [442,216,517,23,510,'58 -216Q25 -216 23 -186Q23 -176 73 26T127 234Q143 289 182 341Q252 427 341 441Q343 441 349 441T359 442Q432 442 471 394T510 276Q510 219 486 165T425 74T345 13T266 -10H255H248Q197 -10 165 35L160 41L133 -71Q108 -168 104 -181T92 -202Q76 -216 58 -216ZM424 322Q424 359 407 382T357 405Q322 405 287 376T231 300Q217 269 193 170L176 102Q193 26 260 26Q298 26 334 62Q367 92 389 158T418 266T424 322'],
  
      // GREEK SMALL LETTER FINAL SIGMA
      0x3C2: [442,107,363,30,405,'31 207Q31 306 115 374T302 442Q341 442 373 430T405 400Q405 392 399 383T379 374Q373 375 348 390T296 405Q222 405 160 357T98 249Q98 232 103 218T112 195T132 175T154 159T186 141T219 122Q234 114 255 102T286 85T299 78L302 74Q306 71 308 69T315 61T322 51T328 40T332 25T334 8Q334 -31 305 -69T224 -107Q194 -107 163 -92Q156 -88 156 -80Q156 -73 162 -67T178 -61Q186 -61 190 -63Q209 -71 224 -71Q244 -71 253 -59T263 -30Q263 -25 263 -21T260 -12T255 -4T248 3T239 9T227 17T213 25T195 34T174 46Q170 48 150 58T122 74T97 90T70 112T51 137T36 169T31 207'],
  
      // GREEK SMALL LETTER SIGMA
      0x3C3: [431,11,571,31,572,'184 -11Q116 -11 74 34T31 147Q31 247 104 333T274 430Q275 431 414 431H552Q553 430 555 429T559 427T562 425T565 422T567 420T569 416T570 412T571 407T572 401Q572 357 507 357Q500 357 490 357T476 358H416L421 348Q439 310 439 263Q439 153 359 71T184 -11ZM361 278Q361 358 276 358Q152 358 115 184Q114 180 114 178Q106 141 106 117Q106 67 131 47T188 26Q242 26 287 73Q316 103 334 153T356 233T361 278'],
  
      // GREEK SMALL LETTER TAU
      0x3C4: [431,13,437,18,517,'39 284Q18 284 18 294Q18 301 45 338T99 398Q134 425 164 429Q170 431 332 431Q492 431 497 429Q517 424 517 402Q517 388 508 376T485 360Q479 358 389 358T299 356Q298 355 283 274T251 109T233 20Q228 5 215 -4T186 -13Q153 -13 153 20V30L203 192Q214 228 227 272T248 336L254 357Q254 358 208 358Q206 358 197 358T183 359Q105 359 61 295Q56 287 53 286T39 284'],
  
      // GREEK SMALL LETTER UPSILON
      0x3C5: [443,10,540,21,523,'413 384Q413 406 432 424T473 443Q492 443 507 425T523 367Q523 334 508 270T468 153Q424 63 373 27T282 -10H268Q220 -10 186 2T135 36T111 78T104 121Q104 170 138 262T173 379Q173 380 173 381Q173 390 173 393T169 400T158 404H154Q131 404 112 385T82 344T65 302T57 280Q55 278 41 278H27Q21 284 21 287Q21 299 34 333T82 404T161 441Q200 441 225 419T250 355Q248 336 247 334Q247 331 232 291T201 199T185 118Q185 68 211 47T275 26Q317 26 355 57T416 132T452 216T465 277Q465 301 457 318T439 343T421 361T413 384'],
  
      // GREEK SMALL LETTER PHI
      0x3C6: [442,218,654,50,618,'92 210Q92 176 106 149T142 108T185 85T220 72L235 70L237 71L250 112Q268 170 283 211T322 299T370 375T429 423T502 442Q547 442 582 410T618 302Q618 224 575 152T457 35T299 -10Q273 -10 273 -12L266 -48Q260 -83 252 -125T241 -179Q236 -203 215 -212Q204 -218 190 -218Q159 -215 159 -185Q159 -175 214 -2L209 0Q204 2 195 5T173 14T147 28T120 46T94 71T71 103T56 142T50 190Q50 238 76 311T149 431H162Q183 431 183 423Q183 417 175 409Q134 361 114 300T92 210ZM574 278Q574 320 550 344T486 369Q437 369 394 329T323 218Q309 184 295 109L286 64Q304 62 306 62Q423 62 498 131T574 278'],
  
      // GREEK SMALL LETTER CHI
      0x3C7: [443,204,626,24,600,'576 -125Q576 -147 547 -175T487 -204H476Q394 -204 363 -157Q334 -114 293 26L284 59Q283 58 248 19T170 -66T92 -151T53 -191Q49 -194 43 -194Q36 -194 31 -189T25 -177T38 -154T151 -30L272 102L265 131Q189 405 135 405Q104 405 87 358Q86 351 68 351Q48 351 48 361Q48 369 56 386T89 423T148 442Q224 442 258 400Q276 375 297 320T330 222L341 180Q344 180 455 303T573 429Q579 431 582 431Q600 431 600 414Q600 407 587 392T477 270Q356 138 353 134L362 102Q392 -10 428 -89T490 -168Q504 -168 517 -156T536 -126Q539 -116 543 -115T557 -114T571 -115Q576 -118 576 -125'],
  
      // GREEK SMALL LETTER PSI
      0x3C8: [694,205,651,21,634,'161 441Q202 441 226 417T250 358Q250 338 218 252T187 127Q190 85 214 61Q235 43 257 37Q275 29 288 29H289L371 360Q455 691 456 692Q459 694 472 694Q492 694 492 687Q492 678 411 356Q329 28 329 27T335 26Q421 26 498 114T576 278Q576 302 568 319T550 343T532 361T524 384Q524 405 541 424T583 443Q602 443 618 425T634 366Q634 337 623 288T605 220Q573 125 492 57T329 -11H319L296 -104Q272 -198 272 -199Q270 -205 252 -205H239Q233 -199 233 -197Q233 -192 256 -102T279 -9Q272 -8 265 -8Q106 14 106 139Q106 174 139 264T173 379Q173 380 173 381Q173 390 173 393T169 400T158 404H154Q131 404 112 385T82 344T65 302T57 280Q55 278 41 278H27Q21 284 21 287Q21 299 34 333T82 404T161 441'],
  
      // GREEK SMALL LETTER OMEGA
      0x3C9: [443,12,622,15,604,'495 384Q495 406 514 424T555 443Q574 443 589 425T604 364Q604 334 592 278T555 155T483 38T377 -11Q297 -11 267 66Q266 68 260 61Q201 -11 125 -11Q15 -11 15 139Q15 230 56 325T123 434Q135 441 147 436Q160 429 160 418Q160 406 140 379T94 306T62 208Q61 202 61 187Q61 124 85 100T143 76Q201 76 245 129L253 137V156Q258 297 317 297Q348 297 348 261Q348 243 338 213T318 158L308 135Q309 133 310 129T318 115T334 97T358 83T393 76Q456 76 501 148T546 274Q546 305 533 325T508 357T495 384'],
  
      // GREEK THETA SYMBOL
      0x3D1: [705,11,591,21,563,'537 500Q537 474 533 439T524 383L521 362Q558 355 561 351Q563 349 563 345Q563 321 552 318Q542 318 521 323L510 326Q496 261 459 187T362 51T241 -11Q100 -11 100 105Q100 139 127 242T154 366Q154 405 128 405Q107 405 92 377T68 316T57 280Q55 278 41 278H27Q21 284 21 287Q21 291 27 313T47 368T79 418Q103 442 134 442Q169 442 201 419T233 344Q232 330 206 228T180 98Q180 26 247 26Q292 26 332 90T404 260L427 349Q422 349 398 359T339 392T289 440Q265 476 265 520Q265 590 312 647T417 705Q463 705 491 670T528 592T537 500ZM464 564Q464 668 413 668Q373 668 339 622T304 522Q304 494 317 470T349 431T388 406T421 391T435 387H436L443 415Q450 443 457 485T464 564'],
  
      // GREEK PHI SYMBOL
      0x3D5: [694,205,596,42,579,'409 688Q413 694 421 694H429H442Q448 688 448 686Q448 679 418 563Q411 535 404 504T392 458L388 442Q388 441 397 441T429 435T477 418Q521 397 550 357T579 260T548 151T471 65T374 11T279 -10H275L251 -105Q245 -128 238 -160Q230 -192 227 -198T215 -205H209Q189 -205 189 -198Q189 -193 211 -103L234 -11Q234 -10 226 -10Q221 -10 206 -8T161 6T107 36T62 89T43 171Q43 231 76 284T157 370T254 422T342 441Q347 441 348 445L378 567Q409 686 409 688ZM122 150Q122 116 134 91T167 53T203 35T237 27H244L337 404Q333 404 326 403T297 395T255 379T211 350T170 304Q152 276 137 237Q122 191 122 150ZM500 282Q500 320 484 347T444 385T405 400T381 404H378L332 217L284 29Q284 27 285 27Q293 27 317 33T357 47Q400 66 431 100T475 170T494 234T500 282'],
  
      // GREEK PI SYMBOL
      0x3D6: [431,10,828,19,823,'206 -10Q158 -10 136 24T114 110Q114 233 199 349L205 358H184Q144 358 121 347Q108 340 95 330T75 312T61 295T53 285Q51 284 38 284Q19 284 19 294Q19 300 38 329T93 391T164 429Q171 431 532 431Q799 431 803 430Q823 423 823 402Q823 377 801 364Q790 358 766 358Q748 358 748 357Q748 355 749 348T752 327T754 297Q754 258 738 207T693 107T618 24T520 -10Q488 -10 466 2T432 36T416 77T411 120Q411 128 410 128T404 122Q373 71 323 31T206 -10ZM714 296Q714 316 707 358H251Q250 357 244 348T230 328T212 301T193 267T176 229T164 187T159 144Q159 62 222 62Q290 62 349 127T432 285Q433 286 434 288T435 291T437 293T440 294T444 294T452 294H466Q472 288 472 286Q472 285 464 244T456 170Q456 62 534 62Q604 62 659 139T714 296'],
  
      // GREEK RHO SYMBOL
      0x3F1: [442,194,517,67,510,'205 -174Q136 -174 102 -153T67 -76Q67 -25 91 85T127 234Q143 289 182 341Q252 427 341 441Q343 441 349 441T359 442Q432 442 471 394T510 276Q510 169 431 80T253 -10Q226 -10 204 -2T169 19T146 44T132 64L128 73Q128 72 124 53T116 5T112 -44Q112 -68 117 -78T150 -95T236 -102Q327 -102 356 -111T386 -154Q386 -166 384 -178Q381 -190 378 -192T361 -194H348Q342 -188 342 -179Q342 -169 315 -169Q294 -169 264 -171T205 -174ZM424 322Q424 359 407 382T357 405Q322 405 287 376T231 300Q221 276 204 217Q188 152 188 116Q188 68 210 47T259 26Q297 26 334 62Q367 92 389 158T418 266T424 322'],
  
      // GREEK LUNATE EPSILON SYMBOL
      0x3F5: [431,11,406,40,382,'227 -11Q149 -11 95 41T40 174Q40 262 87 322Q121 367 173 396T287 430Q289 431 329 431H367Q382 426 382 411Q382 385 341 385H325H312Q191 385 154 277L150 265H327Q340 256 340 246Q340 228 320 219H138V217Q128 187 128 143Q128 77 160 52T231 26Q258 26 284 36T326 57T343 68Q350 68 354 58T358 39Q358 36 357 35Q354 31 337 21T289 0T227 -11']
  };

  SVG.FONTDATA.FONTS[MAIN][0x2212][0] = SVG.FONTDATA.FONTS[MAIN][0x002B][0]; // minus is size
  SVG.FONTDATA.FONTS[MAIN][0x2212][1] = SVG.FONTDATA.FONTS[MAIN][0x002B][1]; // minus is size
  SVG.FONTDATA.FONTS[MAIN][0x22EE][0]  += 400;  // adjust height for \vdots
  SVG.FONTDATA.FONTS[MAIN][0x22F1][0]  += 700;  // adjust height for \ddots

  //
  //  Add some spacing characters (more will come later)
  //
  MathJax.Hub.Insert(SVG.FONTDATA.FONTS[MAIN],{
    0x2000: [0,0,500,0,0,{space:1}],     // en quad
    0x2001: [0,0,1000,0,0,{space:1}],    // em quad
    0x2002: [0,0,500,0,0,{space:1}],     // en space
    0x2003: [0,0,1000,0,0,{space:1}],    // em space
    0x2004: [0,0,333,0,0,{space:1}],     // 3-per-em space
    0x2005: [0,0,250,0,0,{space:1}],     // 4-per-em space
    0x2006: [0,0,167,0,0,{space:1}],     // 6-per-em space
    0x2009: [0,0,167,0,0,{space:1}],     // thin space
    0x200A: [0,0,83,0,0,{space:1}],      // hair space
    0x200B: [0,0,0,0,0,{space:1}],       // zero-width space
    0xEEE0: [0,0,-575,0,0,{space:1}],
    0xEEE1: [0,0,-300,0,0,{space:1}],
    0xEEE8: [0,0,25,0,0,{space:1}]
  });

  HUB.Register.StartupHook("SVG Jax Require",function () {
    HUB.Register.LoadHook(SVG.fontDir+"/Size4/Regular/Main.js",function () {
      SVG.FONTDATA.FONTS[SIZE4][0xE154][0] += 200;  // adjust height for brace extender
      SVG.FONTDATA.FONTS[SIZE4][0xE154][1] += 200;  // adjust depth for brace extender
    });
    
    SVG.FONTDATA.FONTS[MAIN][0x2245][2] -= 222; // fix incorrect right bearing in font
    HUB.Register.LoadHook(SVG.fontDir+"/Main/Bold/MathOperators.js",function () {
      SVG.FONTDATA.FONTS[BOLD][0x2245][2] -= 106; // fix incorrect right bearing in font
    });

    HUB.Register.LoadHook(SVG.fontDir+"/Typewriter/Regular/BasicLatin.js",function () {
      SVG.FONTDATA.FONTS['MathJax_Typewriter'][0x20][2] += 275; // fix incorrect width
    });

    AJAX.loadComplete(SVG.fontDir + "/fontdata.js");
  });
  
})(MathJax.OutputJax.SVG,MathJax.ElementJax.mml,MathJax.Ajax,MathJax.Hub);


/* -*- Mode: Javascript; indent-tabs-mode:nil; js-indent-level: 2 -*- */
/* vim: set ts=2 et sw=2 tw=80: */

/*************************************************************
 *
 *  MathJax/jax/output/SVG/fonts/TeX/fontdata-extra.js
 *  
 *  Adds extra stretchy characters to the TeX font data.
 *
 *  ---------------------------------------------------------------------
 *  
 *  Copyright (c) 2011-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function (SVG) {
  var VERSION = "2.7.2";
  
  var DELIMITERS = SVG.FONTDATA.DELIMITERS;

  var MAIN   = "MathJax_Main",
      BOLD   = "MathJax_Main-bold",
      AMS    = "MathJax_AMS",
      SIZE1  = "MathJax_Size1",
      SIZE4  = "MathJax_Size4";
  var H = "H", V = "V";
  var ARROWREP = [0x2212,MAIN,0,0,0,-.31,-.31];   // add depth for arrow extender
  var DARROWREP = [0x3D,MAIN,0,0,0,0,.1];         // add depth for arrow extender
  
  var delim = {
    0x003D: // equal sign
    {
      dir: H, HW: [[767,MAIN]], stretch: {rep:[0x003D,MAIN]}
    },
    0x219E: // left two-headed arrow
    {
      dir: H, HW: [[1000,AMS]], stretch: {left:[0x219E,AMS], rep:ARROWREP}
    },
    0x21A0: // right two-headed arrow
    {
      dir: H, HW: [[1000,AMS]], stretch: {right:[0x21A0,AMS], rep:ARROWREP}
    },
    0x21A4: // left arrow from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x2190,MAIN], rep:ARROWREP, right:[0x2223,SIZE1,0,-.05,.9]}
    },
    0x21A5: // up arrow from bar
    {
      dir: V, HW: [],
      stretch: {min:.6, bot:[0x22A5,BOLD,0,0,.75], ext:[0x23D0,SIZE1], top:[0x2191,SIZE1]}
    },
    0x21A6: // right arrow from bar
    {
      dir: H, HW: [[1000,MAIN]],
      stretch: {left:[0x2223,SIZE1,-.09,-.05,.9], rep:ARROWREP, right:[0x2192,MAIN]}
    },
    0x21A7: // down arrow from bar
    {
      dir: V, HW: [],
      stretch: {min:.6, top:[0x22A4,BOLD,0,0,.75], ext:[0x23D0,SIZE1], bot:[0x2193,SIZE1]}
    },
    0x21B0: // up arrow with top leftwards
    {
      dir: V, HW: [[722,AMS]],
      stretch: {top:[0x21B0,AMS], ext:[0x23D0,SIZE1,.097]}
    },
    0x21B1: // up arrow with top right
    {
      dir: V, HW: [[722,AMS]],
      stretch: {top:[0x21B1,AMS,.27], ext:[0x23D0,SIZE1]}
    },
    0x21BC: // left harpoon with barb up
    {
      dir: H, HW: [[1000,MAIN]],
      stretch: {left:[0x21BC,MAIN], rep:ARROWREP}
    },
    0x21BD: // left harpoon with barb down
    {
      dir: H, HW: [[1000,MAIN]],
      stretch: {left:[0x21BD,MAIN], rep:ARROWREP}
    },
    0x21BE: // up harpoon with barb right
    {
      dir: V, HW: [[888,AMS]],
      stretch: {top:[0x21BE,AMS,.12,0,1.1], ext:[0x23D0,SIZE1]}
    },
    0x21BF: // up harpoon with barb left
    {
      dir: V, HW: [[888,AMS]],
      stretch: {top:[0x21BF,AMS,.12,0,1.1], ext:[0x23D0,SIZE1]}
    },
    0x21C0: // right harpoon with barb up
    {
      dir: H, HW: [[1000,MAIN]],
      stretch: {right:[0x21C0,MAIN], rep:ARROWREP}
    },
    0x21C1: // right harpoon with barb down
    {
      dir: H, HW: [[1000,MAIN]],
      stretch: {right:[0x21C1,MAIN], rep:ARROWREP}
    },
    0x21C2: // down harpoon with barb right
    {
      dir: V, HW: [[888,AMS]],
      stretch: {bot:[0x21C2,AMS,.12,0,1.1], ext:[0x23D0,SIZE1]}
    },
    0x21C3: // down harpoon with barb left
    {
      dir: V, HW: [[888,AMS]],
      stretch: {bot:[0x21C3,AMS,.12,0,1.1], ext:[0x23D0,SIZE1]}
    },
    0x21DA: // left triple arrow
    {
      dir: H, HW: [[1000,AMS]],
      stretch: {left:[0x21DA,AMS], rep:[0x2261,MAIN]}
    },
    0x21DB: // right triple arrow
    {
      dir: H, HW: [[1000,AMS]],
      stretch: {right:[0x21DB,AMS], rep:[0x2261,MAIN]}
    },
    0x23B4: // top square bracket
    {
      dir: H, HW: [],
      stretch: {min:.5, left:[0x250C,AMS,0,-.1], rep:[0x2212,MAIN,0,.325], right:[0x2510,AMS,0,-.1]}
    },
    0x23B5: // bottom square bracket
    {
      dir: H, HW: [],
      stretch: {min:.5, left:[0x2514,AMS,0,.26], rep:[0x2212,MAIN,0,0,0,.25], right:[0x2518,AMS,0,.26]}
    },
    0x23DC: // top paren
    {
      dir: H, HW: [[778,AMS,0,0x2322],[100,MAIN,0,0x2322]],
      stretch: {left:[0xE150,SIZE4], rep:[0xE154,SIZE4], right:[0xE151,SIZE4]}
    },
    0x23DD: // bottom paren
    {
      dir: H, HW: [[778,AMS,0,0x2323],[100,MAIN,0,0x2323]],
      stretch: {left:[0xE152,SIZE4], rep:[0xE154,SIZE4], right:[0xE153,SIZE4]}
    },
    0x23E0: // top tortoise shell
    {
      dir: H, HW: [],
      stretch: {min:1.25, left:[0x2CA,MAIN,-.1], rep:[0x2C9,MAIN,-.05,.13], right:[0x2CB,MAIN], fullExtenders:true}
    },
    0x23E1: // bottom tortoise shell
    {
      dir: H, HW: [],
      stretch: {min:1.5, left:[0x2CB,MAIN,-.1,.1], rep:[0x2C9,MAIN,-.1], right:[0x2CA,MAIN,-.1,.1], fullExtenders:true}
    },
    0x2906: // leftwards double arrow from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x21D0,MAIN], rep:DARROWREP, right:[0x2223,SIZE1,0,-.1]}
    },
    0x2907: // rightwards double arrow from bar
    {
      dir: H, HW: [],
      stretch: {min:.7, left:[0x22A8,AMS,0,-.12], rep:DARROWREP, right:[0x21D2,MAIN]}
    },
    0x294E: // left barb up right barb up harpoon
    {
      dir: H, HW: [],
      stretch: {min:.5, left:[0x21BC,MAIN], rep:ARROWREP, right:[0x21C0,MAIN]}
    },
    0x294F: // up barb right down barb right harpoon
    {
      dir: V, HW: [],
      stretch: {min:.5, top:[0x21BE,AMS,.12,0,1.1], ext:[0x23D0,SIZE1], bot:[0x21C2,AMS,.12,0,1.1]}
    },
    0x2950: // left barb dow right barb down harpoon
    {
      dir: H, HW: [],
      stretch: {min:.5, left:[0x21BD,MAIN], rep:ARROWREP, right:[0x21C1,MAIN]}
    },
    0x2951: // up barb left down barb left harpoon
    {
      dir: V, HW: [],
      stretch: {min:.5, top:[0x21BF,AMS,.12,0,1.1], ext:[0x23D0,SIZE1], bot:[0x21C3,AMS,.12,0,1.1]}
    },
    0x295A: // leftwards harpoon with barb up from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x21BC,MAIN], rep:ARROWREP, right:[0x2223,SIZE1,0,-.05,.9]}
    },
    0x295B: // rightwards harpoon with barb up from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x2223,SIZE1,-.05,-.05,.9], rep:ARROWREP, right:[0x21C0,MAIN]}
    },
    0x295C: // up harpoon with barb right from bar
    {
      dir: V, HW: [],
      stretch: {min:.7, bot:[0x22A5,BOLD,0,0,.75], ext:[0x23D0,SIZE1], top:[0x21BE,AMS,.12,0,1.1]}
    },
    0x295D: // down harpoon with barb right from bar
    {
      dir: V, HW: [],
      stretch: {min:.7, top:[0x22A4,BOLD,0,0,.75], ext:[0x23D0,SIZE1], bot:[0x21C2,AMS,.12,0,1.1]}
    },
    0x295E: // leftwards harpoon with barb down from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x21BD,MAIN], rep:ARROWREP, right:[0x2223,SIZE1,0,-.05,.9]}
    },
    0x295F: // rightwards harpoon with barb down from bar
    {
      dir: H, HW: [],
      stretch: {min:1, left:[0x2223,SIZE1,-.05,-.05,.9], rep:ARROWREP, right:[0x21C1,MAIN]}
    },
    0x2960: // up harpoon with barb left from bar
    {
      dir: V, HW: [],
      stretch: {min:.7, bot:[0x22A5,BOLD,0,0,.75], ext:[0x23D0,SIZE1], top:[0x21BF,AMS,.12,0,1.1]}
    },
    0x2961: // down harpoon with barb left from bar
    {
      dir: V, HW: [],
      stretch: {min:.7, top:[0x22A4,BOLD,0,0,.75], ext:[0x23D0,SIZE1], bot:[0x21C3,AMS,.12,0,1.1]}
    }
  };
  
  for (var id in delim) {if (delim.hasOwnProperty(id)) {DELIMITERS[id] = delim[id]}};

  MathJax.Ajax.loadComplete(SVG.fontDir + "/fontdata-extra.js");

})(MathJax.OutputJax.SVG);


MathJax.Hub.Register.StartupHook("SVG Jax Ready",function () {

/*************************************************************
 *
 *  MathJax/jax/output/SVG/fonts/TeX/svg/AMS/Regular/Main.js
 *
 *  Copyright (c) 2011-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

MathJax.OutputJax.SVG.FONTDATA.FONTS['MathJax_AMS'] = {
  directory: 'AMS/Regular',
  family: 'MathJax_AMS',
  id: 'MJAMS',
  Ranges: [
    [0x80,0xFF,"Latin1Supplement"],
    [0x100,0x17F,"LatinExtendedA"],
    [0x2B0,0x2FF,"SpacingModLetters"],
    [0x300,0x36F,"CombDiacritMarks"],
    [0x370,0x3FF,"GreekAndCoptic"],
    [0x2000,0x206F,"GeneralPunctuation"],
    [0x2100,0x214F,"LetterlikeSymbols"],
    [0x2190,0x21FF,"Arrows"],
    [0x2200,0x22FF,"MathOperators"],
    [0x2300,0x23FF,"MiscTechnical"],
    [0x2460,0x24FF,"EnclosedAlphanum"],
    [0x2500,0x257F,"BoxDrawing"],
    [0x25A0,0x25FF,"GeometricShapes"],
    [0x2600,0x26FF,"MiscSymbols"],
    [0x2700,0x27BF,"Dingbats"],
    [0x2980,0x29FF,"MiscMathSymbolsB"],
    [0x2A00,0x2AFF,"SuppMathOperators"],
    [0xE000,0xF8FF,"PUA"]
  ],

    // SPACE
    0x20: [0,0,250,0,0,''],

    // LATIN CAPITAL LETTER A
    0x41: [701,1,722,17,703,'130 -1H63Q34 -1 26 2T17 17Q17 24 22 29T35 35Q49 35 64 44T88 66Q101 93 210 383Q331 693 335 697T346 701T357 697Q358 696 493 399Q621 104 633 83Q656 35 686 35Q693 35 698 30T703 17Q703 5 693 2T643 -1H541Q388 -1 386 1Q378 6 378 16Q378 24 383 29T397 35Q412 35 434 45T456 65Q456 93 428 170L419 197H197L195 179Q184 134 184 97Q184 82 186 71T190 55T198 45T205 39T214 36L219 35Q241 31 241 17Q241 5 233 2T196 -1H130ZM493 68Q493 51 481 35H619Q604 56 515 256Q486 321 468 361L348 637Q347 637 330 592T313 543Q313 538 358 436T448 219T493 68ZM404 235Q404 239 355 355T295 488L275 430Q241 348 208 232H306Q404 232 404 235ZM155 48Q151 55 148 88V117L135 86Q118 47 117 46L110 37L135 35H159Q157 41 155 48'],

    // LATIN CAPITAL LETTER B
    0x42: [683,1,667,11,620,'11 665Q11 672 22 683H213Q407 681 431 677Q582 649 582 515Q582 488 573 468Q554 413 484 372L474 366H475Q620 317 620 178Q620 115 568 69T420 6Q393 1 207 -1H22Q11 10 11 18Q11 35 51 35Q79 37 88 39T102 52Q107 70 107 341T102 630Q97 640 88 643T51 648H46Q11 648 11 665ZM142 341Q142 129 141 88T134 37Q133 36 133 35H240L233 48L229 61V623L233 635L240 648H133L138 639Q142 621 142 341ZM284 370Q365 378 391 411T417 508Q417 551 406 581T378 624T347 643T320 648Q298 648 278 635Q267 628 266 611T264 492V370H284ZM546 515Q546 551 531 577T494 617T454 635T422 641L411 643L420 630Q439 604 445 579T452 510V504Q452 481 451 467T441 430T415 383Q420 383 439 391T483 413T527 455T546 515ZM585 185Q585 221 570 249T534 294T490 320T453 334T436 337L435 336L440 330Q445 325 452 315T467 288T479 246T484 188Q484 145 474 110T454 62T442 48Q442 47 444 47Q450 47 470 54T517 75T564 119T585 185ZM449 184Q449 316 358 332Q355 332 335 333T302 335H264V199Q266 68 270 57Q275 50 289 43Q300 37 324 37Q449 37 449 184'],

    // LATIN CAPITAL LETTER C
    0x43: [702,19,722,39,684,'684 131Q684 125 672 109T633 71T573 29T489 -5T386 -19Q330 -19 276 -3T174 46T91 134T44 261Q39 283 39 341T44 421Q66 538 143 611T341 699Q344 699 364 700T395 701Q449 698 503 677T585 655Q603 655 611 662T620 678T625 694T639 702Q650 702 657 690V481L653 474Q640 467 628 472Q624 476 618 496T595 541Q562 587 507 625T390 663H381Q337 663 299 625Q212 547 212 336Q212 249 233 179Q274 30 405 30Q533 30 641 130Q658 147 666 147Q671 147 677 143T684 131ZM250 625Q264 643 261 643Q238 635 214 620T161 579T110 510T79 414Q74 384 74 341T79 268Q89 213 113 169T164 101T217 61T260 39L277 34Q270 41 264 48Q199 111 181 254Q178 281 178 344T181 434Q200 559 250 625ZM621 565V625Q617 623 613 623Q603 619 590 619H575L588 605Q608 583 610 579L621 565'],

    // LATIN CAPITAL LETTER D
    0x44: [683,1,722,16,688,'16 666Q16 675 28 683H193Q329 683 364 682T430 672Q534 650 600 585T686 423Q688 406 688 352Q688 274 673 226Q641 130 565 72T381 1Q368 -1 195 -1H28Q16 5 16 16Q16 35 53 35Q68 36 75 37T87 42T95 52Q98 61 98 341T95 630Q91 640 83 643T53 648Q16 648 16 666ZM237 646Q237 648 184 648H128Q128 647 133 632Q136 620 136 341Q136 64 133 50L128 35H237L230 48L226 61V343Q228 620 231 633Q232 636 237 646ZM264 61Q278 40 310 35Q363 35 401 55T461 112T496 193T513 295Q515 333 515 349Q515 411 504 459Q481 598 373 641Q351 648 321 648Q304 648 292 643T277 635T264 621V61ZM461 628Q462 627 471 616T489 594T509 559T529 509T544 441T550 352Q550 165 479 75L468 59Q474 61 484 65T522 87T573 128T618 195T650 290Q654 322 654 354Q654 418 638 464T581 552Q559 576 529 595T480 621L461 628'],

    // LATIN CAPITAL LETTER E
    0x45: [683,1,667,12,640,'12 666Q12 675 24 683H582Q590 680 593 672V588Q593 514 591 502T575 490Q567 490 563 495T555 517Q552 556 517 590Q486 623 445 634T340 648H282Q266 636 264 620T260 492V370H277Q329 375 358 391T404 439Q420 480 420 506Q420 529 436 529Q445 529 451 521Q455 517 455 361Q455 333 455 298T456 253Q456 217 453 207T437 197Q420 196 420 217Q420 240 406 270Q377 328 284 335H260V201Q261 174 261 134Q262 73 264 61T278 38Q281 36 282 35H331Q400 35 449 50Q571 93 602 179Q605 203 622 203Q629 203 634 197T640 183Q638 181 624 95T604 3L600 -1H24Q12 5 12 16Q12 35 51 35Q92 38 97 52Q102 60 102 341T97 632Q91 645 51 648Q12 648 12 666ZM137 341Q137 131 136 89T130 37Q129 36 129 35H235Q233 41 231 48L226 61V623L231 635L235 648H129Q132 641 133 638T135 603T137 517T137 341ZM557 603V648H504Q504 646 515 639Q527 634 542 619L557 603ZM420 317V397L406 383Q394 370 380 363L366 355Q373 350 382 346Q400 333 409 328L420 317ZM582 61L586 88Q585 88 582 83Q557 61 526 46L511 37L542 35H577Q577 36 578 39T580 49T582 61'],

    // LATIN CAPITAL LETTER F
    0x46: [683,1,611,12,584,'584 499Q569 490 566 490Q558 490 552 497T546 515Q546 535 533 559Q526 574 506 593T469 621Q415 648 326 648Q293 648 287 647T275 641Q264 630 263 617Q262 609 260 492V370L275 372Q323 376 350 392T393 441Q409 473 409 506Q409 529 427 529Q437 529 442 519Q444 511 444 362Q444 212 442 206Q436 197 426 197Q409 197 409 217Q409 265 375 299Q346 328 280 335H260V206Q260 70 262 63Q265 46 276 41T326 35Q362 35 366 28Q377 17 366 3L360 -1H24Q12 5 12 16Q12 35 51 35Q92 38 97 52Q102 60 102 341T97 632Q91 645 51 648Q12 648 12 666Q12 675 24 683H573Q576 678 584 670V499ZM137 341Q137 131 136 89T130 37Q129 36 129 35H182Q233 35 233 39Q226 54 225 92T224 346L226 623L231 635L235 648H129Q132 641 133 638T135 603T137 517T137 341ZM549 603V648H495L506 641Q531 621 533 619L549 603ZM409 317V395L400 386Q390 376 375 366L357 355L373 346Q394 331 397 328L409 317'],

    // LATIN CAPITAL LETTER G
    0x47: [702,19,778,39,749,'737 285Q749 277 749 268Q749 260 744 255T730 250Q695 250 677 217Q666 195 666 119Q666 52 664 50Q656 36 555 3Q483 -16 415 -19Q364 -19 348 -17Q226 -3 146 70T44 261Q39 283 39 341T44 421Q66 538 143 611T341 699Q344 699 364 700T395 701Q449 698 503 677T585 655Q603 655 611 662T620 678T625 694T639 702Q650 702 657 690V481L653 474Q640 467 628 472Q624 476 618 496T595 541Q562 587 507 625T390 663H381Q337 663 299 625Q213 547 213 337Q213 75 341 23Q357 19 397 19Q440 19 462 22T492 30T513 45V119Q513 184 506 203Q491 237 435 250Q421 250 415 257Q404 267 415 281L421 285H737ZM250 43Q250 45 243 55T225 87T203 139T185 224T177 343V361Q184 533 250 625Q264 643 261 643Q238 635 214 620T161 579T110 510T79 414Q74 384 74 341T79 268Q106 117 230 52L250 43ZM621 565V625Q617 623 613 623Q603 619 590 619H575L588 605Q608 583 610 579L621 565ZM655 250H517L524 241Q548 213 548 149V114V39Q549 39 562 44T592 55T615 63L630 70V134Q632 190 634 204T648 237Q655 245 655 250'],

    // LATIN CAPITAL LETTER H
    0x48: [683,1,778,14,762,'14 666Q14 675 26 683H344L351 679Q361 665 351 655Q344 648 317 648Q287 645 282 641Q270 637 269 623T266 497V370H511V497Q511 519 510 553Q509 615 507 626T496 641H495Q489 645 459 648Q420 648 420 665Q420 672 426 679L433 683H751Q762 676 762 666Q762 648 724 648Q684 645 677 632Q675 626 675 341Q675 57 677 52Q684 38 724 35Q762 35 762 16Q762 6 751 -1H433L426 3Q420 10 420 17Q420 35 459 35Q501 38 506 52Q511 64 511 190V323H266V190Q266 60 271 52Q276 38 317 35Q342 35 351 28Q360 17 351 3L344 -1H26Q14 5 14 16Q14 35 53 35Q94 38 99 52Q104 60 104 341T99 632Q93 645 53 648Q14 648 14 666ZM233 341V553Q233 635 239 648H131Q134 641 135 638T137 603T139 517T139 341Q139 131 138 89T132 37Q131 36 131 35H239Q233 47 233 129V341ZM639 341V489Q639 548 639 576T640 620T642 639T646 648H537L542 639Q546 625 546 341Q546 130 545 88T538 37Q537 36 537 35H646Q643 41 643 42T641 55T639 84T639 140V341'],

    // LATIN CAPITAL LETTER I
    0x49: [683,1,389,20,369,'20 666Q20 676 31 683H358Q369 676 369 666Q369 648 331 648Q288 645 282 632Q278 626 278 341Q278 57 282 50Q286 42 295 40T331 35Q369 35 369 16Q369 6 358 -1H31Q20 4 20 16Q20 35 58 35Q84 37 93 39T107 50Q113 60 113 341Q113 623 107 632Q101 645 58 648Q20 648 20 666ZM249 35Q246 40 246 41T244 54T242 83T242 139V341Q242 632 244 639L249 648H140Q146 634 147 596T149 341Q149 124 148 86T140 35H249'],

    // LATIN CAPITAL LETTER J
    0x4A: [683,77,500,6,478,'79 103Q108 103 129 83T151 38Q151 9 130 -15Q116 -34 130 -37Q133 -39 157 -39Q208 -39 219 -8L226 3V305Q226 612 224 621Q220 636 211 641T166 647Q137 647 128 654Q119 665 128 679L135 683H466Q478 677 478 666Q478 647 439 647Q399 644 393 632Q388 620 388 347Q386 69 384 59Q364 -6 316 -39T184 -77H172Q102 -77 56 -48T6 30Q6 62 26 82T79 103ZM353 354Q353 556 354 596T361 645Q362 646 362 647H253Q257 639 258 628T261 547T262 312V-4L255 -17Q248 -29 250 -29Q253 -29 258 -28T277 -20T302 -5T327 22T348 65Q350 74 353 354ZM115 36Q115 47 105 57T79 67Q73 67 67 66T52 56T44 34Q44 9 62 -8Q66 -11 71 -15T81 -22T86 -24L90 -13Q100 3 102 5Q115 22 115 36'],

    // LATIN CAPITAL LETTER K
    0x4B: [683,1,778,22,768,'22 666Q22 676 33 683H351L358 679Q368 665 358 655Q351 648 324 648Q288 645 280 637Q275 631 274 605T273 477L275 343L382 446Q473 530 492 553T512 599Q512 617 502 631T475 648Q455 651 455 666Q455 677 465 680T510 683H593H720Q732 676 732 666Q732 659 727 654T713 648Q670 648 589 581Q567 562 490 489T413 415Q413 413 554 245T711 61Q737 35 751 35Q758 35 763 29T768 15Q768 6 758 -1H624Q491 -1 486 3Q480 10 480 17Q480 25 487 30T506 35Q518 36 520 38T520 48L400 195L302 310L286 297L273 283V170Q275 65 277 57Q280 41 300 38Q302 37 324 35Q349 35 358 28Q367 17 358 3L351 -1H33Q22 4 22 16Q22 35 60 35Q101 38 106 52Q111 60 111 341T106 632Q100 645 60 648Q22 648 22 666ZM240 341V553Q240 635 246 648H138Q141 641 142 638T144 603T146 517T146 341Q146 131 145 89T139 37Q138 36 138 35H246Q240 47 240 129V341ZM595 632L615 648H535L542 637Q542 636 544 625T549 610V595L562 606Q565 608 577 618T595 632ZM524 226L386 388Q386 389 378 382T358 361Q330 338 330 333Q330 332 330 332L331 330L533 90Q558 55 558 41V35H684L671 50Q667 54 524 226'],

    // LATIN CAPITAL LETTER L
    0x4C: [683,1,667,12,640,'12 666Q12 675 24 683H333L340 679Q350 665 340 655Q333 648 309 648Q287 646 279 643T266 630Q264 623 264 346Q264 68 266 57Q274 40 284 35H340Q413 37 460 55Q514 78 553 117T602 197Q605 221 622 221Q629 221 634 215T640 201Q638 194 625 105T611 12Q611 6 600 -1H24Q12 5 12 16Q12 35 51 35Q92 38 97 52Q102 60 102 341T97 632Q91 645 51 648Q12 648 12 666ZM137 341Q137 131 136 89T130 37Q129 36 129 35H237Q235 41 233 48L229 61L226 339Q226 621 229 628Q230 630 231 636T233 643V648H129Q132 641 133 638T135 603T137 517T137 341ZM580 48Q580 59 583 74T586 97Q586 98 585 97T579 92T571 86Q549 64 513 43L500 35H577L580 48'],

    // LATIN CAPITAL LETTER M
    0x4D: [683,1,944,17,926,'18 666Q18 677 27 680T73 683H146Q261 683 266 679L465 215Q469 215 566 443Q663 676 668 681Q673 683 790 683H908L915 679Q924 664 915 655Q912 648 897 648Q851 639 835 606L833 346Q833 86 835 79Q838 69 849 58T873 41Q877 40 887 38T901 35Q926 35 926 16Q926 6 915 -1H604L597 3Q588 19 597 28Q600 35 615 35Q660 42 673 68L679 79V339Q679 409 679 443T679 520T679 580T677 597Q646 521 584 375T473 117T424 3Q416 -1 410 -1T401 1Q399 3 273 301L148 599L146 343Q146 86 148 79Q152 69 163 58T186 41Q190 40 200 38T215 35Q226 35 235 28Q244 17 235 3L228 -1H28Q17 4 17 17Q17 35 39 35Q84 42 97 68L104 79V639L88 641Q72 644 53 648Q34 648 26 651T18 666ZM457 166Q451 169 449 171T435 198T404 268T344 412L244 648H157L166 637Q169 633 293 346L413 66Q424 88 435 117L457 166ZM817 646Q817 648 766 648H715V72L708 57Q701 45 697 41L695 37Q695 35 757 35H819L813 46Q802 61 800 76Q797 105 797 346L799 612L804 626Q812 638 815 641L817 646ZM124 42Q119 42 119 38Q119 35 128 35Q132 35 132 36Q125 42 124 42'],

    // LATIN CAPITAL LETTER N
    0x4E: [683,20,722,20,702,'20 664Q20 666 31 683H142Q256 683 258 681Q259 680 279 653T342 572T422 468L582 259V425Q582 451 582 490T583 541Q583 611 573 628T522 648Q500 648 493 654Q484 665 493 679L500 683H691Q702 676 702 666Q702 657 698 652Q688 648 680 648Q633 648 627 612Q624 601 624 294V-8Q616 -20 607 -20Q601 -20 596 -15Q593 -13 371 270L156 548L153 319Q153 284 153 234T152 167Q152 103 156 78T172 44T213 34Q236 34 242 28Q253 17 242 3L236 -1H36Q24 6 24 16Q24 34 56 34Q58 35 69 36T86 40T100 50T109 72Q111 83 111 345V603L96 619Q72 643 44 648Q20 648 20 664ZM413 419L240 648H120L136 628Q137 626 361 341T587 54L589 68Q589 78 589 121V192L413 419'],

    // LATIN CAPITAL LETTER O
    0x4F: [701,19,778,34,742,'131 601Q180 652 249 676T387 701Q485 701 562 661Q628 629 671 575T731 448Q742 410 742 341T731 234Q707 140 646 81Q549 -19 389 -19Q228 -19 131 81Q57 155 37 274Q34 292 34 341Q34 392 37 410Q58 528 131 601ZM568 341Q568 613 437 659Q406 664 395 665Q329 665 286 625Q232 571 213 439Q210 408 210 341Q210 275 213 245Q232 111 286 57Q309 37 342 23Q357 19 389 19Q420 19 437 23Q469 38 491 57Q568 132 568 341ZM174 341Q174 403 177 441T197 535T249 639Q246 639 224 627T193 608Q189 606 183 601T169 589T155 577Q69 488 69 344Q69 133 231 52Q244 45 246 45Q248 45 246 48Q231 69 222 85T200 141T177 239Q174 269 174 341ZM708 341Q708 415 684 475T635 563T582 610Q578 612 565 619T546 630Q533 637 531 637Q530 637 530 636V635L531 634Q562 591 577 543Q602 471 602 341V316Q602 264 599 230T580 144T531 48L530 47V46Q530 45 531 45Q533 45 547 52T583 75T622 105Q708 195 708 341'],

    // LATIN CAPITAL LETTER P
    0x50: [683,1,611,16,597,'16 666Q16 675 28 683H195Q334 683 370 682T437 672Q511 657 554 611T597 495Q597 343 404 309Q402 308 401 308Q381 303 319 303H261V181Q261 157 262 120Q262 60 267 50T304 36Q310 35 313 35Q352 35 352 17Q352 10 346 3L339 -1H28Q16 5 16 16Q16 35 53 35Q68 36 75 37T87 42T95 52Q98 61 98 341T95 630Q91 640 83 643T53 648Q16 648 16 666ZM235 35Q228 46 227 84Q226 129 226 337V621L230 635L237 648H128Q128 647 133 632Q136 620 136 341Q136 64 133 50L128 35H235ZM301 341H313Q339 341 354 344T389 362T417 410T426 498Q426 586 401 616T322 647Q301 647 293 643Q271 637 264 621Q261 617 261 479V341H301ZM429 350Q431 350 443 353T476 367T515 391T548 432T562 490Q562 550 524 592Q507 607 484 619Q481 621 448 635L433 639L439 621Q462 578 462 506Q462 448 454 413T437 366T428 350H429'],

    // LATIN CAPITAL LETTER Q
    0x51: [701,181,778,34,742,'480 -10Q480 -13 486 -24T507 -50T541 -80T588 -104T648 -114Q666 -114 688 -110T714 -106Q724 -106 728 -114T729 -130Q723 -145 663 -163T548 -181Q503 -181 463 -169T395 -139T343 -97T307 -56T284 -19L280 -3L262 1Q188 24 131 81Q57 155 37 275Q34 292 34 342T37 410Q58 528 131 601Q179 652 248 676T388 701Q485 701 562 661Q698 595 731 448Q742 410 742 341T731 235Q707 141 646 81Q616 50 575 27T493 -5L480 -10ZM568 342Q568 613 437 659L395 666Q329 666 286 626Q232 570 213 439Q210 408 210 342T213 246Q231 113 286 57Q309 37 342 23Q357 19 389 19Q420 19 437 23Q469 38 491 57Q568 134 568 342ZM174 341V354Q174 393 175 419T183 484T205 561T246 635L249 639Q246 639 224 627T193 608Q189 606 183 601T169 589T155 577Q69 491 69 344Q69 133 231 52Q247 42 247 46Q247 46 246 48Q231 69 222 85T200 141T177 239Q174 269 174 341ZM708 341Q708 410 689 467T640 556T588 606T546 630Q532 638 531 638Q530 638 531 635Q563 590 577 543Q602 472 602 341V316Q602 264 599 230T580 144T531 48Q529 44 532 45T546 52Q575 68 596 84T642 128T683 200T706 299Q708 327 708 341ZM391 -17H333Q329 -15 326 -15Q324 -15 324 -17Q324 -21 362 -68Q424 -130 506 -143Q518 -144 544 -144Q569 -144 577 -143L589 -141L575 -139Q544 -127 509 -101T453 -37L442 -19L391 -17'],

    // LATIN CAPITAL LETTER R
    0x52: [683,1,722,16,705,'17 665Q17 672 28 683H221Q415 681 439 677Q461 673 481 667T516 654T544 639T566 623T584 607T597 592T607 578T614 565T618 554L621 548Q626 530 626 497Q626 447 613 419Q578 348 473 326L455 321Q462 310 473 292T517 226T578 141T637 72T686 35Q705 30 705 16Q705 7 693 -1H510Q503 6 404 159L306 310H268V183Q270 67 271 59Q274 42 291 38Q295 37 319 35Q344 35 353 28Q362 17 353 3L346 -1H28Q16 5 16 16Q16 35 55 35Q96 38 101 52Q106 60 106 341T101 632Q95 645 55 648Q17 648 17 665ZM241 35Q238 42 237 45T235 78T233 163T233 337V621L237 635L244 648H133Q136 641 137 638T139 603T141 517T141 341Q141 131 140 89T134 37Q133 36 133 35H241ZM457 496Q457 540 449 570T425 615T400 634T377 643Q374 643 339 648Q300 648 281 635Q271 628 270 610T268 481V346H284Q327 346 375 352Q421 364 439 392T457 496ZM492 537T492 496T488 427T478 389T469 371T464 361Q464 360 465 360Q469 360 497 370Q593 400 593 495Q593 592 477 630L457 637L461 626Q474 611 488 561Q492 537 492 496ZM464 243Q411 317 410 317Q404 317 401 315Q384 315 370 312H346L526 35H619L606 50Q553 109 464 243'],

    // LATIN CAPITAL LETTER S
    0x53: [702,12,556,28,528,'54 238Q72 238 72 212Q72 174 106 121Q113 110 132 90T166 59Q221 23 264 23Q315 23 348 41Q368 50 384 79Q393 102 393 129Q393 181 356 219T221 299Q120 343 74 390T28 501Q28 561 55 610Q98 682 212 699Q214 699 231 700T261 701Q309 698 340 687T408 675Q431 678 445 690T465 702Q474 702 481 690V497L477 490Q464 481 450 490Q446 500 446 501Q446 546 386 606T260 666Q215 666 182 639T148 565Q148 528 186 496T319 428Q352 414 370 405T418 379T468 338T506 284Q528 239 528 191Q528 102 456 46T266 -10Q211 -10 176 2T110 15Q86 9 73 -1T53 -12Q44 -12 37 -1V112V182Q37 214 40 226T54 238ZM446 619Q446 648 444 648Q439 646 435 644Q425 644 415 639H404L417 624Q435 606 439 601L446 592V619ZM124 619L128 635Q126 635 108 617Q64 576 64 502Q64 489 65 479T76 449T102 414T150 376T228 335Q335 291 381 245T427 128Q427 94 419 75L415 61Q421 61 448 88Q490 127 490 190Q490 233 475 264Q456 299 430 321Q402 349 369 367T287 404T204 441Q138 481 119 526Q113 544 113 565Q113 596 124 619ZM75 43Q76 43 90 46T110 50H119L106 64L74 101Q72 101 72 72T75 43'],

    // LATIN CAPITAL LETTER T
    0x54: [683,1,667,33,635,'33 672Q36 680 44 683H624Q632 680 635 672V490L631 483Q621 479 617 479Q611 479 606 485T600 499Q600 525 584 552Q577 567 558 588T524 617Q479 642 426 646L415 648V355Q415 62 422 52Q425 42 434 40T473 35Q500 35 509 28Q518 17 509 3L502 -1H166L160 3Q149 17 160 28Q167 35 195 35Q224 37 234 39T249 52Q253 66 253 355V648L242 646Q192 642 144 617Q129 609 110 588T84 552Q69 527 69 499Q69 490 64 484T50 478Q39 478 33 490V672ZM113 639L126 648H69V597L84 612Q93 623 113 639ZM389 35Q382 46 381 86Q380 134 380 350V648H289V350Q289 199 288 131T286 53T280 35H389ZM600 597V648H542L555 639Q575 623 584 612L600 597'],

    // LATIN CAPITAL LETTER U
    0x55: [683,19,722,16,709,'16 666Q16 677 28 683H341L348 679Q359 665 348 654Q342 648 315 648Q270 644 266 632Q262 627 262 598T261 399Q261 372 261 325T260 260Q260 149 274 99T339 30Q355 25 393 25Q430 25 457 33T494 49T519 72Q562 115 575 205Q576 219 576 379Q576 538 575 550Q568 597 550 622T506 648Q498 648 493 654T487 667T499 683H697Q709 675 709 667T704 654T690 648Q653 648 633 597Q624 573 622 546T619 377Q617 193 613 174Q596 95 544 41Q477 -19 355 -19H344Q275 -16 226 5T153 57T120 110T106 154Q101 172 99 399Q99 618 95 632Q88 644 53 648Q16 648 16 666ZM228 639L233 648H128Q128 647 133 632Q135 621 135 412Q135 197 137 185Q148 115 181 79Q209 51 235 41Q242 36 258 31T277 25Q276 27 268 38T254 59T241 92T228 145Q226 161 226 399Q226 632 228 639ZM604 621Q606 626 619 648H577L586 634Q587 632 591 625T595 614L597 608L604 621'],

    // LATIN CAPITAL LETTER V
    0x56: [683,20,722,0,719,'316 683Q327 676 327 666Q327 648 302 648Q272 642 258 628Q249 621 249 608Q252 589 263 556T289 485T322 406T357 325T388 256T411 205L420 185Q423 185 473 317Q547 497 547 590Q547 621 541 632T516 648Q501 648 498 654Q488 664 498 679L504 683H607H660Q695 683 707 680T719 667Q719 660 714 654T700 648Q678 648 658 628L642 614L513 301Q484 231 449 148T397 25T380 -15Q373 -20 368 -20Q361 -20 358 -15Q354 -13 287 135T149 438T67 610Q45 648 18 648Q11 648 6 653T0 666Q0 677 9 680T59 683H164H316ZM216 614Q216 620 216 622T216 628T216 633T217 635T218 638T219 640T221 644T224 648H84L96 632Q118 592 236 330L367 43L387 88L404 132L380 185Q250 468 222 568Q216 590 216 614ZM576 645Q584 628 584 597L587 568L598 597Q609 624 618 637L624 648H600Q576 648 576 645'],

    // LATIN CAPITAL LETTER W
    0x57: [683,19,1000,5,994,'785 664Q785 670 795 683H982Q994 675 994 665Q994 650 975 648Q953 643 939 619Q931 593 823 292T710 -15Q706 -19 699 -19T688 -15Q682 -6 639 107T555 328T513 437Q513 438 500 409T462 325T413 212Q315 -14 310 -17Q308 -19 302 -19T288 -15L57 619Q45 643 24 648Q5 650 5 665Q5 677 17 683H146H200Q256 683 270 681T285 666Q285 659 280 654T268 648Q253 648 239 634Q230 630 230 619Q230 598 264 481L362 192Q363 193 428 341T493 492Q493 496 473 546T446 608Q426 648 399 648Q392 648 387 653T382 667Q382 678 393 683H679Q690 670 690 665Q690 662 685 655T673 648Q653 648 633 632L622 625V610Q626 576 657 479T719 300T751 218Q754 218 779 294Q847 492 847 581Q847 648 802 648Q796 648 791 652T785 664ZM194 623Q194 630 199 648H82L90 632Q99 616 199 332L302 50Q303 50 322 94T342 141Q342 142 305 245T231 467T194 623ZM585 620Q585 634 593 648H530Q466 648 466 645Q479 632 595 323L699 54Q701 56 718 103T735 154L702 245Q585 562 585 620ZM884 572L890 587Q896 602 903 620T915 645Q915 648 893 648H868L875 634Q883 598 883 576Q883 572 884 572'],

    // LATIN CAPITAL LETTER X
    0x58: [683,1,722,16,705,'22 666Q22 677 31 680T80 683H184H335Q346 675 346 667Q346 660 341 655Q335 648 315 648Q280 644 273 637Q273 630 300 583T356 492T386 448Q430 504 450 535T474 577T478 601Q478 620 469 634T444 648Q428 648 428 666Q428 678 436 680T488 683H559H630Q673 683 681 681T690 666Q690 648 673 648Q652 648 619 637Q571 615 517 550Q490 517 450 464T410 408Q415 399 501 273T617 106Q648 61 661 48T688 35Q705 35 705 16Q705 5 695 -1H539Q384 -1 379 3Q373 10 373 17Q373 27 380 31T408 35Q459 40 459 49Q459 59 418 129T335 259Q334 260 332 260Q328 260 273 197Q210 127 208 117Q199 104 199 82Q199 57 213 46T239 35Q247 35 252 29T257 15Q257 10 256 7T253 3T248 0L246 -1H28Q16 7 16 15T21 29T35 35Q61 35 117 88Q289 279 304 297Q307 303 255 377Q117 586 79 626Q60 648 39 648Q32 648 27 653T22 666ZM237 639V648H173Q113 647 113 646Q113 642 137 612Q186 546 302 373T453 139Q497 63 497 43Q497 39 495 35H559Q622 35 622 37Q622 38 583 94T486 233T373 399T277 552T237 639ZM553 637L566 648H504L508 637Q510 630 515 615V603L528 615Q529 616 539 625T553 637ZM170 46Q169 49 167 58T164 70V83L137 59L113 35H175Q175 38 170 46'],

    // LATIN CAPITAL LETTER Y
    0x59: [683,1,722,16,704,'16 659T16 667T28 683H295Q306 676 306 666Q306 648 284 648Q258 648 255 641Q255 634 265 615T339 479Q418 339 421 339L455 394Q489 448 523 502L557 557Q560 566 560 582Q560 637 504 648Q489 648 486 655Q475 664 486 679L493 683H693Q704 675 704 667Q704 650 684 648Q672 645 653 623Q633 604 614 576T517 426L439 301V183Q442 62 444 59Q449 35 504 35Q521 35 528 30Q538 16 528 3L521 -1H195L188 3Q178 16 188 30Q195 35 213 35Q266 35 273 59Q274 61 277 163V261L75 621Q64 638 58 643T37 648Q28 648 22 653ZM219 637V648H101Q110 634 215 446L313 270V166Q310 59 306 48L301 35H415L410 48Q404 65 404 175V290L317 443Q230 601 226 612Q219 625 219 637ZM608 630L624 648H575Q584 632 588 623L595 610L608 630'],

    // LATIN CAPITAL LETTER Z
    0x5A: [683,1,667,29,635,'39 -1Q29 9 29 12Q29 23 60 77T219 337L410 648H364Q261 648 210 628Q168 612 142 588T109 545T97 509T88 490Q85 489 80 489Q72 489 61 503L70 588Q72 607 75 628T79 662T81 675Q84 677 88 681Q90 683 341 683H592Q604 673 604 666Q604 662 412 348L221 37Q221 35 301 35Q406 35 446 48Q504 68 543 111T597 212Q602 239 617 239Q624 239 629 234T635 223Q635 215 621 113T604 8L597 1Q595 -1 317 -1H39ZM148 637L166 648H112V632Q111 629 110 622T108 612Q108 608 110 608T116 612T129 623T148 637ZM552 646Q552 648 504 648Q452 648 450 643Q448 639 266 343T77 37Q77 35 128 35H179L366 339L552 646ZM572 35Q581 89 581 97L561 77Q542 59 526 48L508 37L539 35H572'],

    // LATIN SMALL LETTER K
    0x6B: [683,1,556,17,534,'519 443Q519 426 497 426Q458 422 361 335Q328 308 315 295Q307 289 310 286T383 193T466 88Q507 35 517 35Q534 35 534 16Q534 5 524 -1H304L297 3Q288 19 297 28Q300 35 317 35Q320 36 324 36T330 37T333 39Q334 39 334 40Q334 47 304 86T244 162L215 199Q212 202 206 199Q201 195 201 137V121Q201 35 230 35Q238 35 243 29T248 15Q248 4 237 -1H28L21 3Q17 13 17 17Q17 24 22 29T35 35Q55 35 61 70Q63 78 63 341T61 612Q55 648 35 648Q27 648 22 654T17 668Q17 678 26 682Q27 683 28 683H108H147Q156 683 162 683T174 683T182 683T187 682T191 681T194 680T197 678T201 675V461L204 246L244 281Q254 291 272 307Q317 349 326 360T339 386Q340 390 340 398Q340 426 321 426Q314 426 309 431T304 445Q304 456 315 461H508Q519 448 519 443ZM166 359V648H126Q89 648 89 645Q89 644 89 644T90 643T91 640T93 634T95 626Q99 612 99 341T95 57Q94 53 93 49T91 43T90 39L89 37Q89 35 133 35Q176 35 176 37Q175 38 175 39Q175 42 170 57Q166 70 166 359ZM410 423Q412 425 407 426Q404 426 393 426Q373 426 373 423Q374 422 375 417T377 410Q377 399 379 399Q406 419 410 423ZM460 37Q460 41 368 152L281 263Q280 263 259 246L239 228Q298 157 355 79Q370 61 370 41V35H417Q460 35 460 37']
};

MathJax.Ajax.loadComplete(MathJax.OutputJax.SVG.fontDir+"/AMS/Regular/Main.js");

/*************************************************************
 *
 *  MathJax/jax/output/SVG/fonts/TeX/svg/Size2/Regular/Main.js
 *
 *  Copyright (c) 2011-2017 The MathJax Consortium
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

MathJax.OutputJax.SVG.FONTDATA.FONTS['MathJax_Size2'] = {
  directory: 'Size2/Regular',
  family: 'MathJax_Size2',
  id: 'MJSZ2',

    // SPACE
    0x20: [0,0,250,0,0,''],

    // LEFT PARENTHESIS
    0x28: [1150,649,597,180,561,'180 96T180 250T205 541T266 770T353 944T444 1069T527 1150H555Q561 1144 561 1141Q561 1137 545 1120T504 1072T447 995T386 878T330 721T288 513T272 251Q272 133 280 56Q293 -87 326 -209T399 -405T475 -531T536 -609T561 -640Q561 -643 555 -649H527Q483 -612 443 -568T353 -443T266 -270T205 -41'],

    // RIGHT PARENTHESIS
    0x29: [1150,649,597,35,417,'35 1138Q35 1150 51 1150H56H69Q113 1113 153 1069T243 944T330 771T391 541T416 250T391 -40T330 -270T243 -443T152 -568T69 -649H56Q43 -649 39 -647T35 -637Q65 -607 110 -548Q283 -316 316 56Q324 133 324 251Q324 368 316 445Q278 877 48 1123Q36 1137 35 1138'],

    // SOLIDUS
    0x2F: [1150,649,811,56,754,'78 -649Q56 -646 56 -625Q56 -614 382 261T712 1140Q716 1150 732 1150Q754 1147 754 1126Q754 1116 428 240T98 -639Q94 -649 78 -649'],

    // LEFT SQUARE BRACKET
    0x5B: [1150,649,472,224,455,'224 -649V1150H455V1099H275V-598H455V-649H224'],

    // REVERSE SOLIDUS
    0x5C: [1150,649,811,54,754,'754 -625Q754 -649 731 -649Q715 -649 712 -639Q709 -635 383 242T55 1124Q54 1135 61 1142T80 1150Q92 1150 98 1140Q101 1137 427 262T754 -625'],

    // RIGHT SQUARE BRACKET
    0x5D: [1150,649,472,16,247,'16 1099V1150H247V-649H16V-598H196V1099H16'],

    // LEFT CURLY BRACKET
    0x7B: [1150,649,667,119,547,'547 -643L541 -649H528Q515 -649 503 -645Q324 -582 293 -466Q289 -449 289 -428T287 -200L286 42L284 53Q274 98 248 135T196 190T146 222L121 235Q119 239 119 250Q119 262 121 266T133 273Q262 336 284 449L286 460L287 701Q287 737 287 794Q288 949 292 963Q293 966 293 967Q325 1080 508 1148Q516 1150 527 1150H541L547 1144V1130Q547 1117 546 1115T536 1109Q480 1086 437 1046T381 950L379 940L378 699Q378 657 378 594Q377 452 374 438Q373 437 373 436Q350 348 243 282Q192 257 186 254L176 251L188 245Q211 236 234 223T287 189T340 135T373 65Q373 64 374 63Q377 49 378 -93Q378 -156 378 -198L379 -438L381 -449Q393 -504 436 -544T536 -608Q544 -611 545 -613T547 -629V-643'],

    // RIGHT CURLY BRACKET
    0x7D: [1150,649,667,119,547,'119 1130Q119 1144 121 1147T135 1150H139Q151 1150 182 1138T252 1105T326 1046T373 964Q378 942 378 702Q378 469 379 462Q386 394 439 339Q482 296 535 272Q544 268 545 266T547 251Q547 241 547 238T542 231T531 227T510 217T477 194Q390 129 379 39Q378 32 378 -201Q378 -441 373 -463Q342 -580 165 -644Q152 -649 139 -649Q125 -649 122 -646T119 -629Q119 -622 119 -619T121 -614T124 -610T132 -607T143 -602Q195 -579 235 -539T285 -447Q286 -435 287 -199T289 51Q294 74 300 91T329 138T390 197Q412 213 436 226T475 244L489 250L472 258Q455 265 430 279T377 313T327 366T293 434Q289 451 289 472T287 699Q286 941 285 948Q279 978 262 1005T227 1048T184 1080T151 1100T129 1109L127 1110Q119 1113 119 1130'],

    // MODIFIER LETTER CIRCUMFLEX ACCENT
    0x2C6: [772,-565,1000,-5,1004,'1004 603Q1004 600 999 583T991 565L960 574Q929 582 866 599T745 631L500 698Q497 698 254 631Q197 616 134 599T39 574L8 565Q5 565 0 582T-5 603L26 614Q58 624 124 646T248 687L499 772Q999 604 1004 603'],

    // SMALL TILDE
    0x2DC: [750,-611,1000,0,999,'296 691Q258 691 216 683T140 663T79 639T34 619T16 611Q13 619 8 628L0 644L36 662Q206 749 321 749Q410 749 517 710T703 670Q741 670 783 678T859 698T920 722T965 742T983 750Q986 742 991 733L999 717L963 699Q787 611 664 611Q594 611 484 651T296 691'],

    // COMBINING CIRCUMFLEX ACCENT
    0x302: [772,-565,0,-1005,4,'4 603Q4 600 -1 583T-9 565L-40 574Q-71 582 -134 599T-255 631L-500 698Q-503 698 -746 631Q-803 616 -866 599T-961 574L-992 565Q-995 565 -1000 582T-1005 603L-974 614Q-942 624 -876 646T-752 687L-501 772Q-1 604 4 603'],

    // COMBINING TILDE
    0x303: [750,-611,0,-1000,-1,'-704 691Q-742 691 -784 683T-860 663T-921 639T-966 619T-984 611Q-987 619 -992 628L-1000 644L-964 662Q-794 749 -679 749Q-590 749 -483 710T-297 670Q-259 670 -217 678T-141 698T-80 722T-35 742T-17 750Q-14 742 -9 733L-1 717L-37 699Q-213 611 -336 611Q-405 611 -515 651T-704 691'],

    // N-ARY PRODUCT
    0x220F: [950,450,1278,56,1221,'220 812Q220 813 218 819T214 829T208 840T199 853T185 866T166 878T140 887T107 893T66 896H56V950H1221V896H1211Q1080 896 1058 812V-311Q1076 -396 1211 -396H1221V-450H725V-396H735Q864 -396 888 -314Q889 -312 889 -311V896H388V292L389 -311Q405 -396 542 -396H552V-450H56V-396H66Q195 -396 219 -314Q220 -312 220 -311V812'],

    // N-ARY COPRODUCT
    0x2210: [950,450,1278,56,1221,'220 812Q220 813 218 819T214 829T208 840T199 853T185 866T166 878T140 887T107 893T66 896H56V950H552V896H542Q411 896 389 812L388 208V-396H889V812Q889 813 887 819T883 829T877 840T868 853T854 866T835 878T809 887T776 893T735 896H725V950H1221V896H1211Q1080 896 1058 812V-311Q1076 -396 1211 -396H1221V-450H56V-396H66Q195 -396 219 -314Q220 -312 220 -311V812'],

    // N-ARY SUMMATION
    0x2211: [950,450,1444,55,1388,'60 948Q63 950 665 950H1267L1325 815Q1384 677 1388 669H1348L1341 683Q1320 724 1285 761Q1235 809 1174 838T1033 881T882 898T699 902H574H543H251L259 891Q722 258 724 252Q725 250 724 246Q721 243 460 -56L196 -356Q196 -357 407 -357Q459 -357 548 -357T676 -358Q812 -358 896 -353T1063 -332T1204 -283T1307 -196Q1328 -170 1348 -124H1388Q1388 -125 1381 -145T1356 -210T1325 -294L1267 -449L666 -450Q64 -450 61 -448Q55 -446 55 -439Q55 -437 57 -433L590 177Q590 178 557 222T452 366T322 544L56 909L55 924Q55 945 60 948'],

    // SQUARE ROOT
    0x221A: [1150,650,1000,111,1020,'1001 1150Q1017 1150 1020 1132Q1020 1127 741 244L460 -643Q453 -650 436 -650H424Q423 -647 423 -645T421 -640T419 -631T415 -617T408 -594T399 -560T385 -512T367 -448T343 -364T312 -259L203 119L138 41L111 67L212 188L264 248L472 -474L983 1140Q988 1150 1001 1150'],

    // INTEGRAL
    0x222B: [1361,862,556,55,944,'114 -798Q132 -824 165 -824H167Q195 -824 223 -764T275 -600T320 -391T362 -164Q365 -143 367 -133Q439 292 523 655T645 1127Q651 1145 655 1157T672 1201T699 1257T733 1306T777 1346T828 1360Q884 1360 912 1325T944 1245Q944 1220 932 1205T909 1186T887 1183Q866 1183 849 1198T832 1239Q832 1287 885 1296L882 1300Q879 1303 874 1307T866 1313Q851 1323 833 1323Q819 1323 807 1311T775 1255T736 1139T689 936T633 628Q574 293 510 -5T410 -437T355 -629Q278 -862 165 -862Q125 -862 92 -831T55 -746Q55 -711 74 -698T112 -685Q133 -685 150 -700T167 -741Q167 -789 114 -798'],

    // DOUBLE INTEGRAL
    0x222C: [1361,862,1084,55,1472,'114 -798Q132 -824 165 -824H167Q195 -824 223 -764T275 -600T320 -391T362 -164Q365 -143 367 -133Q439 292 523 655T645 1127Q651 1145 655 1157T672 1201T699 1257T733 1306T777 1346T828 1360Q884 1360 912 1325T944 1245Q944 1220 932 1205T909 1186T887 1183Q866 1183 849 1198T832 1239Q832 1287 885 1296L882 1300Q879 1303 874 1307T866 1313Q851 1323 833 1323Q819 1323 807 1311T775 1255T736 1139T689 936T633 628Q574 293 510 -5T410 -437T355 -629Q278 -862 165 -862Q125 -862 92 -831T55 -746Q55 -711 74 -698T112 -685Q133 -685 150 -700T167 -741Q167 -789 114 -798ZM642 -798Q660 -824 693 -824H695Q723 -824 751 -764T803 -600T848 -391T890 -164Q893 -143 895 -133Q967 292 1051 655T1173 1127Q1179 1145 1183 1157T1200 1201T1227 1257T1261 1306T1305 1346T1356 1360Q1412 1360 1440 1325T1472 1245Q1472 1220 1460 1205T1437 1186T1415 1183Q1394 1183 1377 1198T1360 1239Q1360 1287 1413 1296L1410 1300Q1407 1303 1402 1307T1394 1313Q1379 1323 1361 1323Q1347 1323 1335 1311T1303 1255T1264 1139T1217 936T1161 628Q1102 293 1038 -5T938 -437T883 -629Q806 -862 693 -862Q653 -862 620 -831T583 -746Q583 -711 602 -698T640 -685Q661 -685 678 -700T695 -741Q695 -789 642 -798'],

    // TRIPLE INTEGRAL
    0x222D: [1361,862,1592,55,1980,'114 -798Q132 -824 165 -824H167Q195 -824 223 -764T275 -600T320 -391T362 -164Q365 -143 367 -133Q439 292 523 655T645 1127Q651 1145 655 1157T672 1201T699 1257T733 1306T777 1346T828 1360Q884 1360 912 1325T944 1245Q944 1220 932 1205T909 1186T887 1183Q866 1183 849 1198T832 1239Q832 1287 885 1296L882 1300Q879 1303 874 1307T866 1313Q851 1323 833 1323Q819 1323 807 1311T775 1255T736 1139T689 936T633 628Q574 293 510 -5T410 -437T355 -629Q278 -862 165 -862Q125 -862 92 -831T55 -746Q55 -711 74 -698T112 -685Q133 -685 150 -700T167 -741Q167 -789 114 -798ZM642 -798Q660 -824 693 -824H695Q723 -824 751 -764T803 -600T848 -391T890 -164Q893 -143 895 -133Q967 292 1051 655T1173 1127Q1179 1145 1183 1157T1200 1201T1227 1257T1261 1306T1305 1346T1356 1360Q1412 1360 1440 1325T1472 1245Q1472 1220 1460 1205T1437 1186T1415 1183Q1394 1183 1377 1198T1360 1239Q1360 1287 1413 1296L1410 1300Q1407 1303 1402 1307T1394 1313Q1379 1323 1361 1323Q1347 1323 1335 1311T1303 1255T1264 1139T1217 936T1161 628Q1102 293 1038 -5T938 -437T883 -629Q806 -862 693 -862Q653 -862 620 -831T583 -746Q583 -711 602 -698T640 -685Q661 -685 678 -700T695 -741Q695 -789 642 -798ZM1150 -798Q1168 -824 1201 -824H1203Q1231 -824 1259 -764T1311 -600T1356 -391T1398 -164Q1401 -143 1403 -133Q1475 292 1559 655T1681 1127Q1687 1145 1691 1157T1708 1201T1735 1257T1769 1306T1813 1346T1864 1360Q1920 1360 1948 1325T1980 1245Q1980 1220 1968 1205T1945 1186T1923 1183Q1902 1183 1885 1198T1868 1239Q1868 1287 1921 1296L1918 1300Q1915 1303 1910 1307T1902 1313Q1887 1323 1869 1323Q1855 1323 1843 1311T1811 1255T1772 1139T1725 936T1669 628Q1610 293 1546 -5T1446 -437T1391 -629Q1314 -862 1201 -862Q1161 -862 1128 -831T1091 -746Q1091 -711 1110 -698T1148 -685Q1169 -685 1186 -700T1203 -741Q1203 -789 1150 -798'],

    // CONTOUR INTEGRAL
    0x222E: [1360,862,556,55,944,'114 -798Q132 -824 165 -824H167Q195 -824 223 -764T275 -600T320 -391T362 -164Q365 -143 367 -133Q382 -52 390 2Q314 40 276 99Q230 167 230 249Q230 363 305 436T484 519H494L503 563Q587 939 632 1087T727 1298Q774 1360 828 1360Q884 1360 912 1325T944 1245Q944 1220 932 1205T909 1186T887 1183Q866 1183 849 1198T832 1239Q832 1287 885 1296L882 1300Q879 1303 874 1307T866 1313Q851 1323 833 1323Q766 1323 688 929Q662 811 610 496Q770 416 770 249Q770 147 701 68T516 -21H506L497 -65Q407 -464 357 -623T237 -837Q203 -862 165 -862Q125 -862 92 -831T55 -746Q55 -711 74 -698T112 -685Q133 -685 150 -700T167 -741Q167 -789 114 -798ZM480 478Q460 478 435 470T380 444T327 401T287 335T271 249Q271 124 375 56L397 43L431 223L485 478H480ZM519 20Q545 20 578 33T647 72T706 144T730 249Q730 383 603 455Q603 454 597 421T582 343T569 276Q516 22 515 20H519'],

    // N-ARY LOGICAL AND
    0x22C0: [950,450,1111,55,1055,'1055 -401Q1055 -419 1042 -434T1007 -450Q977 -450 963 -423Q959 -417 757 167L555 750L353 167Q151 -417 147 -423Q134 -450 104 -450Q84 -450 70 -436T55 -401Q55 -394 56 -390Q59 -381 284 270T512 925Q525 950 555 950Q583 950 597 926Q599 923 825 270T1054 -391Q1055 -394 1055 -401'],

    // N-ARY LOGICAL OR
    0x22C1: [950,450,1111,55,1055,'55 900Q55 919 69 934T103 950Q134 950 147 924Q152 913 353 333L555 -250L757 333Q958 913 963 924Q978 950 1007 950Q1028 950 1041 935T1055 901Q1055 894 1054 891Q1052 884 826 231T597 -426Q583 -450 556 -450Q527 -450 512 -424Q510 -421 285 229T56 890Q55 893 55 900'],

    // N-ARY INTERSECTION
    0x22C2: [949,451,1111,55,1055,'57 516Q68 602 104 675T190 797T301 882T423 933T542 949Q594 949 606 948Q780 928 901 815T1048 545Q1053 516 1053 475T1055 49Q1055 -406 1054 -410Q1051 -427 1037 -438T1006 -450T976 -439T958 -411Q957 -407 957 37Q957 484 956 494Q945 643 831 747T554 852Q481 852 411 826Q301 786 232 696T154 494Q153 484 153 37Q153 -407 152 -411Q148 -428 135 -439T104 -450T73 -439T56 -410Q55 -406 55 49Q56 505 57 516'],

    // N-ARY UNION
    0x22C3: [950,449,1111,55,1055,'56 911Q58 926 71 938T103 950Q120 950 134 939T152 911Q153 907 153 463Q153 16 154 6Q165 -143 279 -247T556 -352Q716 -352 830 -248T956 6Q957 16 957 463Q957 907 958 911Q962 928 975 939T1006 950T1037 939T1054 911Q1055 906 1055 451Q1054 -5 1053 -16Q1029 -207 889 -328T555 -449Q363 -449 226 -331T62 -45Q57 -16 57 25T55 451Q55 906 56 911'],

    // LEFT CEILING
    0x2308: [1150,649,528,224,511,'224 -649V1150H511V1099H275V-649H224'],

    // RIGHT CEILING
    0x2309: [1150,649,528,16,303,'16 1099V1150H303V-649H252V1099H16'],

    // LEFT FLOOR
    0x230A: [1150,649,528,224,511,'224 -649V1150H275V-598H511V-649H224'],

    // RIGHT FLOOR
    0x230B: [1150,649,528,16,303,'252 -598V1150H303V-649H16V-598H252'],

    // MATHEMATICAL LEFT ANGLE BRACKET
    0x27E8: [1150,649,611,112,524,'112 244V258L473 1130Q482 1150 498 1150Q511 1150 517 1142T523 1125V1118L344 685Q304 587 257 473T187 305L165 251L344 -184L523 -616V-623Q524 -634 517 -641T499 -649Q484 -649 473 -629L112 244'],

    // MATHEMATICAL RIGHT ANGLE BRACKET
    0x27E9: [1150,649,611,85,498,'112 -649Q103 -649 95 -642T87 -623V-616L266 -184L445 251Q445 252 356 466T178 898T86 1123Q85 1134 93 1142T110 1150Q126 1150 133 1137Q134 1136 317 695L498 258V244L317 -194Q134 -635 133 -636Q126 -649 112 -649'],

    // N-ARY CIRCLED DOT OPERATOR
    0x2A00: [949,449,1511,56,1454,'668 944Q697 949 744 949Q803 949 814 948Q916 937 1006 902T1154 826T1262 730T1336 638T1380 563Q1454 415 1454 250Q1454 113 1402 -14T1258 -238T1036 -391T755 -449Q608 -449 477 -392T255 -240T110 -16T56 250Q56 387 105 510T239 723T434 871T668 944ZM755 -352Q922 -352 1061 -269T1278 -48T1356 250Q1356 479 1202 652T809 850Q798 851 747 851Q634 851 527 806T337 682T204 491T154 251Q154 128 201 17T329 -176T521 -304T755 -352ZM665 250Q665 290 692 315T758 341Q792 339 818 315T845 250Q845 211 819 186T755 160Q716 160 691 186T665 250'],

    // N-ARY CIRCLED PLUS OPERATOR
    0x2A01: [949,449,1511,56,1454,'668 944Q697 949 744 949Q803 949 814 948Q916 937 1006 902T1154 826T1262 730T1336 638T1380 563Q1454 415 1454 250Q1454 113 1402 -14T1258 -238T1036 -391T755 -449Q608 -449 477 -392T255 -240T110 -16T56 250Q56 387 105 510T239 723T434 871T668 944ZM706 299V850H704Q519 832 386 725T198 476Q181 433 169 379T156 300Q156 299 431 299H706ZM1116 732Q1054 778 982 807T871 842T810 849L804 850V299H1079Q1354 299 1354 300Q1354 311 1352 329T1336 402T1299 506T1228 620T1116 732ZM706 -350V201H431Q156 201 156 200Q156 189 158 171T174 98T211 -6T282 -120T395 -232Q428 -257 464 -277T527 -308T587 -328T636 -339T678 -346T706 -350ZM1354 200Q1354 201 1079 201H804V-350Q808 -349 838 -345T887 -338T940 -323T1010 -295Q1038 -282 1067 -265T1144 -208T1229 -121T1301 0T1349 158Q1354 188 1354 200'],

    // N-ARY CIRCLED TIMES OPERATOR
    0x2A02: [949,449,1511,56,1454,'668 944Q697 949 744 949Q803 949 814 948Q916 937 1006 902T1154 826T1262 730T1336 638T1380 563Q1454 415 1454 250Q1454 113 1402 -14T1258 -238T1036 -391T755 -449Q608 -449 477 -392T255 -240T110 -16T56 250Q56 387 105 510T239 723T434 871T668 944ZM1143 709Q1138 714 1129 722T1086 752T1017 791T925 826T809 850Q798 851 747 851H728Q659 851 571 823T408 741Q367 713 367 709L755 320L1143 709ZM297 639Q296 639 282 622T247 570T205 491T169 382T154 250T168 118T204 9T247 -70T282 -122L297 -139L685 250L297 639ZM1213 -139Q1214 -139 1228 -122T1263 -70T1305 9T1341 118T1356 250T1342 382T1306 491T1263 570T1228 622L1213 639L825 250L1213 -139ZM367 -209Q373 -215 384 -224T434 -258T514 -302T622 -336T755 -352T887 -338T996 -302T1075 -259T1126 -224L1143 -209L755 180Q754 180 561 -14T367 -209'],

    // N-ARY UNION OPERATOR WITH PLUS
    0x2A04: [950,449,1111,55,1055,'56 911Q58 926 71 938T103 950Q120 950 134 939T152 911Q153 907 153 463Q153 16 154 6Q165 -143 279 -247T556 -352Q716 -352 830 -248T956 6Q957 16 957 463Q957 907 958 911Q962 928 975 939T1006 950T1037 939T1054 911Q1055 906 1055 451Q1054 -5 1053 -16Q1029 -207 889 -328T555 -449Q363 -449 226 -331T62 -45Q57 -16 57 25T55 451Q55 906 56 911ZM507 554Q511 570 523 581T554 593Q571 593 585 582T603 554Q604 551 604 443V338H709Q817 338 820 337Q835 334 847 321T859 290Q859 254 819 241Q816 240 709 240H604V134Q604 48 604 34T598 11Q583 -15 555 -15Q526 -15 512 11Q507 20 507 34T506 134V240H401H344Q292 240 278 246Q251 259 251 290Q251 309 264 321T290 337Q293 338 401 338H506V443Q506 551 507 554'],

    // N-ARY SQUARE UNION OPERATOR
    0x2A06: [950,450,1111,54,1056,'56 911Q60 927 72 938T103 950Q120 950 134 939T152 911Q153 907 153 277V-352H957V277Q957 907 958 911Q962 928 975 939T1006 950T1036 939T1054 911V891Q1054 871 1054 836T1054 754T1054 647T1055 525T1055 390T1055 250T1055 111T1055 -24T1055 -147T1054 -253T1054 -335T1054 -391V-411Q1047 -442 1016 -449Q1011 -450 552 -450L94 -449Q63 -439 56 -411V-391Q56 -371 56 -336T56 -254T56 -147T55 -25T55 110T55 250T55 389T55 524T55 647T56 753T56 835T56 891V911']
};

MathJax.Ajax.loadComplete(MathJax.OutputJax.SVG.fontDir+"/Size2/Regular/Main.js");

 });

HUB.Browser.Select(MathJax.Message.browsers);

  if (BASE.AuthorConfig && typeof BASE.AuthorConfig.AuthorInit === "function") {BASE.AuthorConfig.AuthorInit()}
  HUB.queue = BASE.Callback.Queue();
  HUB.queue.Push(
    ["Post",STARTUP.signal,"Begin"],
    ["Config",STARTUP],
    ["Cookie",STARTUP],
    ["Styles",STARTUP],
    ["Message",STARTUP],
    function () {
      // Do Jax and Extensions in parallel, but wait for them all to complete
      var queue = BASE.Callback.Queue(
        STARTUP.Jax(),
        STARTUP.Extensions()
      );
      return queue.Push({});
    },
    ["Menu",STARTUP],
    STARTUP.onLoad(),
    function () {MathJax.isReady = true}, // indicates that MathJax is ready to process math
    ["Typeset",STARTUP],
    ["Hash",STARTUP],
    ["MenuZoom",STARTUP],
    ["Post",STARTUP.signal,"End"]
  );

})("MathJax");

}}
