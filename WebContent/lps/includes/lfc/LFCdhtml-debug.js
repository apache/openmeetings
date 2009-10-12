var $dhtml = true;
var $as3 = false;
var $js1 = true;
var $swf7 = false;
var $swf8 = false;
var $svg = false;
var $as2 = false;
var $swf9 = false;
var $profile = false;
var $runtime = "dhtml";
var $swf10 = false;
var $debug = true;
var $j2me = false;
var _Copyright = "Portions of this file are copyright (c) 2001-2008 by Laszlo Systems, Inc.  All rights reserved.";
var Debug = {backtraceStack: [], uncaughtBacktraceStack: null, FUNCTION_NAME: "displayName", FUNCTION_FILENAME: "_dbg_filename", FUNCTION_LINENO: "_dbg_lineno"};
Debug.backtraceStack.maxDepth = 100;
var $modules = {};
$modules.runtime = this;
$modules.lz = $modules.runtime;
$modules.user = $modules.lz;
var global = $modules.user;
var __ES3Globals = {Array: Array, Boolean: Boolean, Date: Date, Function: Function, Math: Math, Number: Number, Object: Object, RegExp: RegExp, String: String, Error: Error, EvalError: EvalError, RangeError: RangeError, ReferenceError: ReferenceError, SyntaxError: SyntaxError, TypeError: TypeError, URIError: URIError};
var globalValue = (function  () {
var $lzsc$temp = function  (name_$1) {
if (name_$1 == "lz") {
return lz
};
if (name_$1 == "LzMouseKernel") {
return LzMouseKernel
};
if (name_$1.charAt(0) == "<" && name_$1.charAt(name_$1.length - 1) == ">") {
return lz[name_$1.substring(1, name_$1.length - 1)]
};
return this[name_$1] || global[name_$1] || __ES3Globals[name_$1]
};
$lzsc$temp["displayName"] = "compiler/LzRuntime.lzs#101/19";
return $lzsc$temp
})();
var Instance = (function  () {
var $lzsc$temp = function  () {
this.constructor = arguments.callee;
this.$lzsc$initialize.apply(this, arguments)
};
$lzsc$temp["displayName"] = "constructor";
return $lzsc$temp
})();
Instance.prototype.constructor = Instance;
Instance.classname = "Instance";
Instance.prototype.classname = "Object";
Instance._dbg_typename = "Class";
(function  () {
var $lzsc$temp = function  () {
var addProperties_$1 = (function  () {
var $lzsc$temp = function  (plist_$1) {
for (var i_$2 = plist_$1.length - 1;i_$2 >= 1;i_$2 -= 2) {
var value_$3 = plist_$1[i_$2];
var name_$4 = plist_$1[i_$2 - 1];
if (value_$3 !== void 0 || !(name_$4 in this)) {
this[name_$4] = value_$3
};
if (!(value_$3 instanceof Function)) {
continue
};
var xtor_$5 = this.constructor;
if (value_$3.hasOwnProperty("$superclasses")) {
var os_$6 = value_$3.$superclasses, found_$7 = false;
for (var j_$8 = os_$6.length - 1;j_$8 >= 0;j_$8--) {
if (os_$6[j_$8] === xtor_$5) {
found_$7 = true;
break
}};
if (!found_$7) {
value_$3.$superclasses.push(xtor_$5)
}} else {
if (value_$3.hasOwnProperty("$superclass") && value_$3.$superclass !== xtor_$5) {
var $superclass_$9 = value_$3.$superclass;
delete value_$3.$superclass;
value_$3.$superclasses = [$superclass_$9, xtor_$5]
} else {
value_$3.$superclass = xtor_$5
}};
if (!value_$3._dbg_typename) {
value_$3._dbg_owner = this;
value_$3._dbg_typename = (function  () {
var $lzsc$temp = function  () {
var t_$1 = Debug.functionName(this._dbg_owner._dbg_prototype_for);
return t_$1 + " function"
};
$lzsc$temp["displayName"] = "_dbg_typename";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "addProperties";
return $lzsc$temp
})();
addProperties_$1.call(Instance.prototype, ["addProperties", addProperties_$1])
};
$lzsc$temp["displayName"] = "compiler/Class.lzs#54/2";
return $lzsc$temp
})()();
Instance.prototype.addProperties(["addProperty", (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
this.addProperties([name_$1, value_$2])
};
$lzsc$temp["displayName"] = "addProperty";
return $lzsc$temp
})()]);
Instance.prototype.addProperty("nextMethod", (function  () {
var $lzsc$temp = function  (currentMethod, nextMethodName) {
var next_$1;
if (currentMethod.hasOwnProperty("$superclass")) {
next_$1 = currentMethod.$superclass.prototype[nextMethodName]
} else {
if (currentMethod.hasOwnProperty("$superclasses")) {
var $superclasses_$2 = currentMethod.$superclasses;
for (var i_$3 = $superclasses_$2.length - 1;i_$3 >= 0;i_$3--) {
var sc_$4 = $superclasses_$2[i_$3];
if (this instanceof sc_$4) {
next_$1 = sc_$4.prototype[nextMethodName];
break
}}}};
if (!next_$1) {
next_$1 = (function  () {
var $lzsc$temp = function  () {
Debug.error("super.%s is undefined in %w", nextMethodName, currentMethod)
};
$lzsc$temp["displayName"] = "next";
return $lzsc$temp
})()
};
return next_$1
};
$lzsc$temp["displayName"] = "nextMethod";
return $lzsc$temp
})());
Instance.prototype.addProperty("$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})());
var Class = {prototype: new Instance(), addProperty: Instance.prototype.addProperty, addProperties: (function  () {
var $lzsc$temp = function  (plist_$1) {
this.prototype.addProperties(plist_$1)
};
$lzsc$temp["displayName"] = "addProperties";
return $lzsc$temp
})(), addStaticProperty: (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
this[name_$1] = value_$2;
if (value_$2 instanceof Function && !value_$2._dbg_typename) {
value_$2._dbg_owner = this;
value_$2._dbg_typename = (function  () {
var $lzsc$temp = function  () {
return Debug.functionName(this._dbg_owner) + " static function"
};
$lzsc$temp["displayName"] = "_dbg_typename";
return $lzsc$temp
})()
}};
$lzsc$temp["displayName"] = "addStaticProperty";
return $lzsc$temp
})(), allClasses: {Instance: Instance}, make: (function  () {
var $lzsc$temp = function  (classname_$1, mixinsAndSuperclass_$2, instanceProperties_$3, staticProperties_$4) {
var superclass = null;
if (mixinsAndSuperclass_$2 instanceof Array) {
for (var k_$9 = mixinsAndSuperclass_$2.length - 1;k_$9 >= 0;k_$9--) {
var c_$10 = mixinsAndSuperclass_$2[k_$9];
if (c_$10 instanceof Function) {
if (superclass) {
Debug.error("Class.make: Multiple superclasses %s and %s for class %s", superclass, c_$10, classname_$1)
};
mixinsAndSuperclass_$2.splice(k_$9, 1);
superclass = c_$10
}}} else {
if (mixinsAndSuperclass_$2 instanceof Function) {
superclass = mixinsAndSuperclass_$2;
mixinsAndSuperclass_$2 = null
} else {
if (mixinsAndSuperclass_$2) {
Debug.error("Class.make: invalid superclass %w", mixinsAndSuperclass_$2)
}}};
if (!superclass) {
superclass = Instance
};
var nc_$11 = (function  () {
var $lzsc$temp = function  () {
this.constructor = arguments.callee;
if (this.$lzsc$initialize !== Instance.prototype.$lzsc$initialize) {
this.$lzsc$initialize.apply(this, arguments)
}};
$lzsc$temp["displayName"] = "constructor";
return $lzsc$temp
})();
nc_$11.constructor = this;
nc_$11.classname = classname_$1;
nc_$11._dbg_typename = this._dbg_name;
nc_$11[Debug.FUNCTION_NAME] = classname_$1;
var xtor_$12 = (function  () {
var $lzsc$temp = function  () {
this.constructor = superclass
};
$lzsc$temp["displayName"] = "prototype";
return $lzsc$temp
})();
xtor_$12.prototype = superclass.prototype;
var prototype_$13 = new xtor_$12();
if (mixinsAndSuperclass_$2 instanceof Array) {
for (var i_$6 = mixinsAndSuperclass_$2.length - 1;i_$6 >= 0;i_$6--) {
var t_$14 = mixinsAndSuperclass_$2[i_$6];
prototype_$13 = t_$14.makeInterstitial(prototype_$13, i_$6 > 0 ? mixinsAndSuperclass_$2[i_$6 - 1] : nc_$11)
}};
nc_$11.prototype = prototype_$13;
prototype_$13._dbg_prototype_for = nc_$11;
this.addStaticProperty.call(nc_$11, "addStaticProperty", this.addStaticProperty);
nc_$11.addStaticProperty("addProperty", this.addProperty);
nc_$11.addStaticProperty("addProperties", this.addProperties);
if (staticProperties_$4) {
for (var i_$6 = staticProperties_$4.length - 1;i_$6 >= 1;i_$6 -= 2) {
var value_$7 = staticProperties_$4[i_$6];
var name_$8 = staticProperties_$4[i_$6 - 1];
nc_$11.addStaticProperty(name_$8, value_$7)
}};
if (instanceProperties_$3) {
nc_$11.addProperties(instanceProperties_$3)
};
if (this.allClasses[classname_$1]) {
Debug.error("Redefining %s from %w to %w", classname_$1, this.allClasses[classname_$1], nc_$11)
};
global[classname_$1] = this.allClasses[classname_$1] = nc_$11;
return nc_$11
};
$lzsc$temp["displayName"] = "make";
return $lzsc$temp
})()};
Class._dbg_typename = "Metaclass";
Class._dbg_name = "Class";
Class.addProperties._dbg_typename = "Class static function";
Class.addStaticProperty._dbg_typename = "Class static function";
Class.make._dbg_typename = "Class static function";
var Mixin = {prototype: new Instance(), allMixins: {}, addProperty: (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
this.prototype[name_$1] = value_$2;
this.instanceProperties.push(name_$1, value_$2);
var impls_$3 = this.implementations;
for (var mash_$4 in impls_$3) {
var t_$5 = impls_$3[mash_$4];
t_$5.addProperty(name_$1, value_$2)
};
if (value_$2 instanceof Function && !value_$2._dbg_typename) {
value_$2._dbg_typename = this.classname + " function"
}};
$lzsc$temp["displayName"] = "addProperty";
return $lzsc$temp
})(), addStaticProperty: (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
this[name_$1] = value_$2;
if (value_$2 instanceof Function && !value_$2._dbg_typename) {
value_$2._dbg_typename = this.classname + " static function"
}};
$lzsc$temp["displayName"] = "addStaticProperty";
return $lzsc$temp
})(), makeInterstitial: (function  () {
var $lzsc$temp = function  (superclassInstance_$1, sharable_$2) {
var impls_$3 = this.implementations;
var interstitialname_$4 = this.classname + "+" + superclassInstance_$1.constructor.classname;
var prototypename_$5 = sharable_$2.classname + "|" + interstitialname_$4;
if (impls_$3[prototypename_$5]) {
Debug.debug("Sharable interstitial: %s", prototypename_$5);
return impls_$3[prototypename_$5]
};
superclassInstance_$1.addProperties(this.instanceProperties);
var xtor_$6 = (function  () {
var $lzsc$temp = function  () {
this.constructor = arguments.callee
};
$lzsc$temp["displayName"] = "interstitial";
return $lzsc$temp
})();
xtor_$6.prototype = superclassInstance_$1;
xtor_$6.classname = interstitialname_$4;
xtor_$6._dbg_typename = "Interstitial";
xtor_$6[Debug.FUNCTION_NAME] = xtor_$6.classname;
var t_$7 = new xtor_$6();
impls_$3[prototypename_$5] = t_$7;
return t_$7
};
$lzsc$temp["displayName"] = "makeInterstitial";
return $lzsc$temp
})(), $lzsc$isa: (function  () {
var $lzsc$temp = function  (obj_$1) {
var impls_$2 = this.implementations;
for (var prototypename_$3 in impls_$2) {
if (obj_$1 instanceof impls_$2[prototypename_$3].constructor) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "$lzsc$isa";
return $lzsc$temp
})(), make: (function  () {
var $lzsc$temp = function  (classname_$1, superMixin_$2, instanceProperties_$3, staticProperties_$4) {
var nt_$5 = {constructor: this, classname: classname_$1, _dbg_typename: this._dbg_name, _dbg_name: classname_$1, prototype: superMixin_$2 ? superMixin_$2.make() : new Object(), instanceProperties: superMixin_$2 ? superMixin_$2.instanceProperties.slice(0) : new Array(), implementations: {}};
this.addStaticProperty.call(nt_$5, "addStaticProperty", this.addStaticProperty);
nt_$5.addStaticProperty("addProperty", this.addProperty);
nt_$5.addStaticProperty("makeInterstitial", this.makeInterstitial);
nt_$5.addStaticProperty("$lzsc$isa", this.$lzsc$isa);
if (staticProperties_$4) {
for (var i_$6 = staticProperties_$4.length - 1;i_$6 >= 1;i_$6 -= 2) {
var value_$7 = staticProperties_$4[i_$6];
var name_$8 = staticProperties_$4[i_$6 - 1];
nt_$5.addStaticProperty(name_$8, value_$7)
}};
if (instanceProperties_$3) {
for (var i_$6 = instanceProperties_$3.length - 1;i_$6 >= 1;i_$6 -= 2) {
var value_$7 = instanceProperties_$3[i_$6];
var name_$8 = instanceProperties_$3[i_$6 - 1];
nt_$5.addProperty(name_$8, value_$7)
}};
global[classname_$1] = this.allMixins[classname_$1] = nt_$5;
return nt_$5
};
$lzsc$temp["displayName"] = "make";
return $lzsc$temp
})()};
Mixin._dbg_typename = "Metaclass";
Mixin._dbg_name = "Mixin";
Mixin.addStaticProperty._dbg_typename = "Mixin static function";
Mixin.addProperty._dbg_typename = "Mixin static function";
Mixin.makeInterstitial._dbg_typename = "Mixin static function";
Mixin.make._dbg_typename = "Mixin static function";
Class.make("LzDebugConsole", null, ["saved_msgs", void 0, "addText", (function  () {
var $lzsc$temp = function  (msg_$1) {
var str_$2;
try {
if (msg_$1 && (Function["$lzsc$isa"] ? Function.$lzsc$isa(msg_$1["toHTML"]) : msg_$1["toHTML"] instanceof Function)) {
str_$2 = msg_$1.toHTML()
} else {
str_$2 = String(msg_$1)["toHTML"]()
}}
catch (e) {
str_$2 = "" + msg_$1
};
if (navigator.platform == "rhino") {
try {
print(str_$2);
return
}
catch (e) {

}};
this.addHTMLText(str_$2)
};
$lzsc$temp["displayName"] = "addText";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "echo", (function  () {
var $lzsc$temp = function  (str_$1, newLine_$2) {
switch (arguments.length) {
case 1:
newLine_$2 = true
}};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "addHTMLText", (function  () {
var $lzsc$temp = function  (msg_$1) {

};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "makeObjectLink", (function  () {
var $lzsc$temp = function  (rep_$1, id_$2, attrs_$3) {
switch (arguments.length) {
case 2:
attrs_$3 = null
};
return undefined
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "SimpleExprPattern", new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$"), "ElementPattern", new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)", "g"), "isSimpleExpr", (function  () {
var $lzsc$temp = function  (expr_$1) {
return expr_$1.match(this.SimpleExprPattern)
};
$lzsc$temp["displayName"] = "isSimpleExpr";
return $lzsc$temp
})(), "evalSimpleExpr", (function  () {
var $lzsc$temp = function  (expr_$1) {
var parts_$2 = expr_$1.match(this.ElementPattern);
var val_$3 = globalValue(parts_$2[0]) || Debug.environment[parts_$2[0]];
for (var i_$4 = 1, l_$5 = parts_$2.length;i_$4 < l_$5;i_$4++) {
val_$3 = val_$3[parts_$2[i_$4]]
};
return val_$3
};
$lzsc$temp["displayName"] = "evalSimpleExpr";
return $lzsc$temp
})(), "doEval", (function  () {
var $lzsc$temp = function  (expr_$1) {
if (this.isSimpleExpr(expr_$1)) {
var simple_$2 = true;
try {
var val_$3 = this.evalSimpleExpr(expr_$1);
if (val_$3 === void 0) {
simple_$2 = false
}}
catch (e) {
simple_$2 = false
}};
if (simple_$2) {
Debug.displayResult(val_$3)
} else {
Debug.warn("Unable to evaluate %s", expr_$1)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#20/1";
return $lzsc$temp
})()(LzDebugConsole);
Class.make("LzBootstrapDebugConsole", LzDebugConsole, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
this.saved_msgs = new Array()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "echo", (function  () {
var $lzsc$temp = function  (str_$1, newLine_$2) {
switch (arguments.length) {
case 1:
newLine_$2 = true
};
this.addHTMLText("<i>" + str_$1 + "</i>" + (newLine_$2 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "addHTMLText", (function  () {
var $lzsc$temp = function  (msg_$1) {
this.saved_msgs.push(msg_$1)
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "makeObjectLink", (function  () {
var $lzsc$temp = function  (rep_$1, id_$2, attrs_$3) {
switch (arguments.length) {
case 2:
attrs_$3 = null
};
if (id_$2 != null) {
return '<a title="#' + id_$2 + '">' + rep_$1 + "</a>"
};
return rep_$1
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "doEval", (function  () {
var $lzsc$temp = function  (expr_$1) {
try {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["doEval"] || this.nextMethod(arguments.callee, "doEval")).call(this, expr_$1)
}
catch (e) {
Debug.error(e)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#179/1";
return $lzsc$temp
})()(LzBootstrapDebugConsole);
Class.make("LzBootstrapDebugLogger", null, ["log", (function  () {
var $lzsc$temp = function  (msg_$1) {
if (("console" in global) && typeof console.log == "function") {
var fn_$4 = "log";
try {
if (msg_$1 instanceof LzError) {
fn_$4 = "error"
} else {
if (msg_$1 instanceof LzWarning) {
fn_$4 = "warn"
} else {
if (msg_$1 instanceof LzInfo) {
fn_$4 = "info"
} else {
if (msg_$1 instanceof LzDebug) {
fn_$4 = "debug"
}}}};
if (typeof console[fn_$4] != "function") {
fn_$4 = "log"
};
if (console[fn_$4].length == 0 && (msg_$1 instanceof LzMessage || msg_$1 instanceof LzSourceMessage)) {
console[fn_$4].apply(console, msg_$1.toArray());
return
}}
catch (e) {

};
console[fn_$4](msg_$1.toString())
}};
$lzsc$temp["displayName"] = "log";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#244/1";
return $lzsc$temp
})()(LzBootstrapDebugLogger);
Class.make("LzBootstrapDebugService", null, ["FUNCTION_NAME", "displayName", "FUNCTION_FILENAME", "_dbg_filename", "FUNCTION_LINENO", "_dbg_lineno", "backtraceStack", void 0, "uncaughtBacktraceStack", void 0, "log_all_writes", false, "logger", void 0, "console", void 0, "window", void 0, "environment", {}, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (logger_$1, console_$2) {
switch (arguments.length) {
case 0:
logger_$1 = null;;case 1:
console_$2 = null
};
if (logger_$1 == null) {
logger_$1 = new LzBootstrapDebugLogger()
};
if (console_$2 == null) {
console_$2 = new LzBootstrapDebugConsole()
};
if (!$as3) {
var copy_$3 = {backtraceStack: true, uncaughtBacktraceStack: true};
for (var k_$4 in copy_$3) {
this[k_$4] = Debug[k_$4]
}};
this.log_all_writes = !(!globalValue("logdebug"));
this.log_all_writes = global["console"] && typeof global.console["log"] == "function";
this.logger = logger_$1;
this.console = console_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "log", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.logger.log.apply(this.logger, args_$1)
};
$lzsc$temp["displayName"] = "log";
return $lzsc$temp
})(), "makeObjectLink", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.console.makeObjectLink.apply(this.console, args_$1)
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "displayResult", (function  () {
var $lzsc$temp = function  (result_$1) {
switch (arguments.length) {
case 0:
result_$1 = void 0
};
if (result_$1 !== void 0) {
this.__write(result_$1)
}};
$lzsc$temp["displayName"] = "displayResult";
return $lzsc$temp
})(), "__write", (function  () {
var $lzsc$temp = function  (msg_$1) {
if (this.log_all_writes) {
this.logger.log(msg_$1)
};
this.console.addText(msg_$1)
};
$lzsc$temp["displayName"] = "__write";
return $lzsc$temp
})(), "debug", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
this.__write("DEBUG: " + args_$1.join(" "))
};
$lzsc$temp["displayName"] = "debug";
return $lzsc$temp
})(), "info", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
this.__write("INFO: " + args_$1.join(" "))
};
$lzsc$temp["displayName"] = "info";
return $lzsc$temp
})(), "warn", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
this.__write("WARNING: " + args_$1.join(" "))
};
$lzsc$temp["displayName"] = "warn";
return $lzsc$temp
})(), "error", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
this.__write("ERROR: " + args_$1.join(" "))
};
$lzsc$temp["displayName"] = "error";
return $lzsc$temp
})(), "deprecated", (function  () {
var $lzsc$temp = function  (obj_$1, method_$2, replacement_$3) {
Debug.info("%w.%s is deprecated.  Use %w.%s instead", obj_$1, method_$2, obj_$1, replacement_$3)
};
$lzsc$temp["displayName"] = "deprecated";
return $lzsc$temp
})(), "evalCarefully", (function  () {
var $lzsc$temp = function  (fileName_$1, lineNumber_$2, closure_$3, context_$4) {
try {
return closure_$3.call(context_$4)
}
catch (e) {
$reportSourceWarning(fileName_$1, lineNumber_$2, e)
}};
$lzsc$temp["displayName"] = "evalCarefully";
return $lzsc$temp
})(), "ignoringErrors", (function  () {
var $lzsc$temp = function  (closure_$1, context_$2, errval_$3) {
try {
return closure_$1.call(context_$2)
}
catch (e) {
return errval_$3
}};
$lzsc$temp["displayName"] = "ignoringErrors";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzBootstrapDebugService.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
if (this === Debug) {
return "#Debug"
} else {
return null
}};
$lzsc$temp["displayName"] = "LzBootstrapDebugService.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#314/1";
return $lzsc$temp
})()(LzBootstrapDebugService);
var Debug = new LzBootstrapDebugService();
function trace () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
Debug.info.apply(Debug, args_$1)
};
function $reportSourceWarning (filename_$1, lineNumber_$2, msg_$3, fatal_$4) {
var warning_$5 = filename_$1 + "@" + lineNumber_$2 + ": " + msg_$3 + "\n";
Debug[fatal_$4 ? "error" : "warning"](warning_$5)
};
function $reportUndefinedObjectProperty (filename_$1, lineNumber_$2, propertyName_$3) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$1, lineNumber_$2, "undefined object does not have a property '" + propertyName_$3 + "'", true);
arguments.callee._dbg_recursive_call = false
}};
$reportUndefinedObjectProperty._dbg_recursive_call = false;
function $reportUndefinedProperty (filename_$1, lineNumber_$2, propertyName_$3) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$1, lineNumber_$2, "reference to undefined property '" + propertyName_$3 + "'", false);
arguments.callee._dbg_recursive_call = false
}};
$reportUndefinedProperty._dbg_recursive_call = false;
function $reportUndefinedVariable (filename_$1, lineNumber_$2, variableName_$3) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$1, lineNumber_$2, "reference to undefined variable '" + variableName_$3 + "'", true);
arguments.callee._dbg_recursive_call = false
}};
$reportUndefinedVariable._dbg_recursive_call = false;
function $reportNotFunction (filename_$1, lineNumber_$2, name_$3, value_$4) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
var msg_$5 = "call to non-function";
if (typeof name_$3 == "string") {
msg_$5 += " '" + name_$3 + "'"
};
msg_$5 += " (type '" + typeof value_$4 + "')";
if (typeof value_$4 == "undefined") {
msg_$5 = "call to undefined function";
if (typeof name_$3 == "string") {
msg_$5 += " '" + name_$3 + "'"
}};
$reportSourceWarning(filename_$1, lineNumber_$2, msg_$5, true);
arguments.callee._dbg_recursive_call = false
}};
$reportNotFunction._dbg_recursive_call = false;
function $reportUndefinedMethod (filename_$1, lineNumber_$2, name_$3, value_$4) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
var msg_$5 = "call to non-method";
if (typeof name_$3 == "string") {
msg_$5 += " '" + name_$3 + "'"
};
msg_$5 += " (type '" + typeof value_$4 + "')";
if (typeof value_$4 == "undefined") {
msg_$5 = "call to undefined method";
if (typeof name_$3 == "string") {
msg_$5 += " '" + name_$3 + "'"
}};
$reportSourceWarning(filename_$1, lineNumber_$2, msg_$5, true);
arguments.callee._dbg_recursive_call = false
}};
$reportUndefinedMethod._dbg_recursive_call = false;
if (typeof window.addEventListener == "function") {
window.onerror = (function  () {
var $lzsc$temp = function  (errorString_$1, fileName_$2, lineNo_$3) {
if (Debug.uncaughtBacktraceStack) {
errorString_$1 = new String(errorString_$1);
errorString_$1.$lzsc$b = Debug.uncaughtBacktraceStack;
Debug.uncaughtBacktraceStack = null;
fileName_$2 = null;
lineNo_$3 = null
};
$reportSourceWarning(fileName_$2, lineNo_$3, errorString_$1, true);
return false
};
$lzsc$temp["displayName"] = "window.onerror";
return $lzsc$temp
})()
};
Class.make("LzBootstrapMessage", null, ["message", "", "length", 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (message_$1) {
switch (arguments.length) {
case 0:
message_$1 = null
};
if (message_$1 != null) {
this.appendInternal("" + message_$1, message_$1)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "appendInternal", (function  () {
var $lzsc$temp = function  (str_$1, obj_$2) {
switch (arguments.length) {
case 1:
obj_$2 = null
};
this.message += str_$1;
this.length = this.message.length
};
$lzsc$temp["displayName"] = "appendInternal";
return $lzsc$temp
})(), "append", (function  () {
var $lzsc$temp = function  () {
var str_$1 = Array.prototype.slice.call(arguments, 0);
var len_$2 = str_$1.length;
for (var i_$3 = 0;i_$3 < len_$2;i_$3++) {
this.appendInternal(String(str_$1[i_$3]))
}};
$lzsc$temp["displayName"] = "append";
return $lzsc$temp
})(), "charAt", (function  () {
var $lzsc$temp = function  (index_$1) {
return this.message.charAt(index_$1)
};
$lzsc$temp["displayName"] = "charAt";
return $lzsc$temp
})(), "charCodeAt", (function  () {
var $lzsc$temp = function  (index_$1) {
return this.message.charCodeAt(index_$1)
};
$lzsc$temp["displayName"] = "charCodeAt";
return $lzsc$temp
})(), "indexOf", (function  () {
var $lzsc$temp = function  (key_$1) {
return this.message.indexOf(key_$1)
};
$lzsc$temp["displayName"] = "indexOf";
return $lzsc$temp
})(), "lastIndexOf", (function  () {
var $lzsc$temp = function  (key_$1) {
return this.message.lastIndexOf(key_$1)
};
$lzsc$temp["displayName"] = "lastIndexOf";
return $lzsc$temp
})(), "toLowerCase", (function  () {
var $lzsc$temp = function  () {
return new LzMessage(this.message.toLowerCase())
};
$lzsc$temp["displayName"] = "toLowerCase";
return $lzsc$temp
})(), "toUpperCase", (function  () {
var $lzsc$temp = function  () {
return new LzMessage(this.message.toUpperCase())
};
$lzsc$temp["displayName"] = "toUpperCase";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.message || ""
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "valueOf", (function  () {
var $lzsc$temp = function  () {
return this.message || ""
};
$lzsc$temp["displayName"] = "valueOf";
return $lzsc$temp
})(), "concat", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return new LzMessage(this.message.concat.apply(this.message, args_$1))
};
$lzsc$temp["displayName"] = "concat";
return $lzsc$temp
})(), "slice", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.slice.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "slice";
return $lzsc$temp
})(), "split", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.split.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "split";
return $lzsc$temp
})(), "substr", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.substr.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "substr";
return $lzsc$temp
})(), "substring", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.substring.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "substring";
return $lzsc$temp
})(), "toHTML", (function  () {
var $lzsc$temp = function  () {
return this["toString"]().toHTML()
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], ["xmlEscapeChars", {"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&apos;"}, "xmlEscape", (function  () {
var $lzsc$temp = function  (input_$1) {
if (input_$1 && (typeof input_$1 == "string" || (String["$lzsc$isa"] ? String.$lzsc$isa(input_$1) : input_$1 instanceof String))) {
var escChars_$2 = LzBootstrapMessage.xmlEscapeChars;
var len_$3 = input_$1.length;
var output_$4 = "";
for (var i_$5 = 0;i_$5 < len_$3;i_$5++) {
var code_$6 = input_$1.charCodeAt(i_$5);
if (code_$6 < 32) {
output_$4 += "&#x" + code_$6.toString(16) + ";"
} else {
var c_$7 = input_$1.charAt(i_$5);
output_$4 += escChars_$2[c_$7] || c_$7
}};
return output_$4
} else {
return input_$1
}};
$lzsc$temp["displayName"] = "xmlEscape";
return $lzsc$temp
})()]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "compiler/LzMessage.lzs#16/1";
return $lzsc$temp
})()(LzBootstrapMessage);
var LzMessage = LzBootstrapMessage;
String.prototype.toHTML = (function  () {
var $lzsc$temp = function  () {
return LzMessage.xmlEscape(this)
};
$lzsc$temp["displayName"] = "String.prototype.toHTML";
return $lzsc$temp
})();
Mixin.make("LzFormatter", null, ["toNumber", (function  () {
var $lzsc$temp = function  (s_$1) {
return Number(s_$1)
};
$lzsc$temp["displayName"] = "toNumber";
return $lzsc$temp
})(), "pad", (function  () {
var $lzsc$temp = function  (value_$1, widthMin_$2, decMax_$3, pad_$4, sign_$5, radix_$6, force_$7) {
switch (arguments.length) {
case 0:
value_$1 = "";;case 1:
widthMin_$2 = 0;;case 2:
decMax_$3 = null;;case 3:
pad_$4 = " ";;case 4:
sign_$5 = "-";;case 5:
radix_$6 = 10;;case 6:
force_$7 = false
};
var isNumber_$8 = typeof value_$1 == "number";
if (isNumber_$8) {
if (decMax_$3 != null) {
var precision_$9 = Math.pow(10, -decMax_$3);
value_$1 = Math.round(value_$1 / precision_$9) * precision_$9
};
value_$1 = Number(value_$1).toString(radix_$6);
if (sign_$5 != "-") {
if (value_$1.indexOf("-") != 0) {
if (value_$1 != 0) {
value_$1 = sign_$5 + value_$1
} else {
value_$1 = " " + value_$1
}}}} else {
value_$1 = "" + value_$1
};
var strlen_$10 = value_$1.length;
if (decMax_$3 != null) {
if (isNumber_$8) {
var decimal_$11 = value_$1.lastIndexOf(".");
if (decimal_$11 == -1) {
var decimals_$12 = 0;
if (force_$7 || decMax_$3 > 0) {
value_$1 += "."
}} else {
var decimals_$12 = strlen_$10 - (decimal_$11 + 1)
};
if (decimals_$12 > decMax_$3) {
value_$1 = value_$1.substring(0, strlen_$10 - (decimals_$12 - decMax_$3))
} else {
for (var i_$13 = decimals_$12;i_$13 < decMax_$3;i_$13++) {
value_$1 += "0"
}}} else {
value_$1 = value_$1.substring(0, decMax_$3)
}};
strlen_$10 = value_$1.length;
var leftJustify_$14 = false;
if (widthMin_$2 < 0) {
widthMin_$2 = -widthMin_$2;
leftJustify_$14 = true
};
if (strlen_$10 >= widthMin_$2) {
return value_$1
};
if (leftJustify_$14) {
for (var i_$13 = strlen_$10;i_$13 < widthMin_$2;i_$13++) {
value_$1 = value_$1 + " "
}} else {
sign_$5 = null;
if (pad_$4 != " ") {
if (" +-".indexOf(value_$1.substring(0, 1)) >= 0) {
sign_$5 = value_$1.substring(0, 1);
value_$1 = value_$1.substring(1)
}};
for (var i_$13 = strlen_$10;i_$13 < widthMin_$2;i_$13++) {
value_$1 = pad_$4 + value_$1
};
if (sign_$5 != null) {
value_$1 = sign_$5 + value_$1
}};
return value_$1
};
$lzsc$temp["displayName"] = "pad";
return $lzsc$temp
})(), "formatToString", (function  () {
var $lzsc$temp = function  (control) {
var getarg_$8;
var consumearg_$9;
getarg_$8 = (function  () {
var $lzsc$temp = function  (i_$1) {
if (i_$1 >= al) {
return null
};
return args[i_$1]
};
$lzsc$temp["displayName"] = "getarg";
return $lzsc$temp
})();
consumearg_$9 = (function  () {
var $lzsc$temp = function  (i_$1) {
if (i_$1 >= al) {
Debug.warn("%#0.48w: insufficient arguments", control)
}};
$lzsc$temp["displayName"] = "consumearg";
return $lzsc$temp
})();
switch (arguments.length) {
case 0:
control = ""
};
var args = Array.prototype.slice.call(arguments, 1);
var al = args.length;
if (!(typeof control == "string" || (String["$lzsc$isa"] ? String.$lzsc$isa(control) : control instanceof String)) || al > 0 != control.indexOf("%") >= 0) {
args = [control].concat(args);
al++;
var out_$1 = new LzMessage();
for (var i_$2 = 0;i_$2 < al;i_$2++) {
var arg_$3 = args[i_$2];
var sep_$4 = i_$2 == al - 1 ? "\n" : " ";
out_$1.append(arg_$3);
out_$1.appendInternal(sep_$4)
};
return out_$1
};
var ctrl_$5 = "" + control;
var argno_$6 = 0;
var maxarg_$7 = 0;
var base_$10 = 0, limit_$11 = ctrl_$5.length;
var start_$12 = 0, end_$13 = 0;
var out_$1 = new LzMessage();
while (start_$12 < limit_$11) {
end_$13 = ctrl_$5.indexOf("%");
if (end_$13 == -1) {
out_$1.append(ctrl_$5.substring(start_$12, limit_$11));
break
};
out_$1.append(ctrl_$5.substring(start_$12, end_$13));
base_$10 = end_$13;
start_$12 = end_$13 + 1;
end_$13 = end_$13 + 2;
var sign_$14 = "-";
var pad_$15 = " ";
var alternate_$16 = false;
var length_$17 = "";
var precision_$18 = null;
var directive_$19 = null;
var object_$20 = null;
while (start_$12 < limit_$11 && directive_$19 == null) {
var char_$21 = ctrl_$5.substring(start_$12, end_$13);
start_$12 = end_$13++;
switch (char_$21) {
case "-":
length_$17 = char_$21;
break;;case "+":
case " ":
sign_$14 = char_$21;
break;;case "#":
alternate_$16 = true;
break;;case "0":
if (length_$17 === "" && precision_$18 === null) {
pad_$15 = char_$21;
break
};;case "1":
case "2":
case "3":
case "4":
case "5":
case "6":
case "7":
case "8":
case "9":
if (precision_$18 !== null) {
precision_$18 += char_$21
} else {
length_$17 += char_$21
};
break;;case "$":
argno_$6 = length_$17 - 1;
length_$17 = "";
break;;case "*":
if (precision_$18 !== null) {
precision_$18 = getarg_$8(argno_$6);
consumearg_$9(argno_$6++)
} else {
length_$17 = getarg_$8(argno_$6);
consumearg_$9(argno_$6++)
};
break;;case ".":
precision_$18 = "";
break;;case "h":
case "l":
break;;case "=":
object_$20 = getarg_$8(argno_$6);
consumearg_$9(argno_$6++);
break;;default:
directive_$19 = char_$21;
break
}};
var value_$22 = getarg_$8(argno_$6);
if (object_$20 == null) {
object_$20 = value_$22
};
var decimals_$23 = null;
var force_$24 = false;
if (precision_$18 !== null) {
decimals_$23 = 1 * precision_$18
} else {
switch (directive_$19) {
case "F":
case "E":
case "G":
case "f":
case "e":
case "g":
decimals_$23 = 6;
force_$24 = alternate_$16;
break;;case "O":
case "o":
if (alternate_$16 && value_$22 != 0) {
out_$1.append("0")
};
break;;case "X":
case "x":
if (alternate_$16 && value_$22 != 0) {
out_$1.append("0" + directive_$19)
};
break
}};
var radix_$25 = 10;
switch (directive_$19) {
case "o":
case "O":
radix_$25 = 8;
break;;case "x":
case "X":
radix_$25 = 16;
break
};
switch (directive_$19) {
case "U":
case "O":
case "X":
case "u":
case "o":
case "x":
if (value_$22 < 0) {
value_$22 = -value_$22;
var wid_$26 = Math.abs(parseInt(length_$17, 10));
if (isNaN(wid_$26)) {
wid_$26 = this.toNumber(value_$22).toString(radix_$25).length
};
var max_$27 = Math.pow(radix_$25, wid_$26);
value_$22 = max_$27 - value_$22
};
break
};
switch (directive_$19) {
case "D":
case "U":
case "I":
case "O":
case "X":
case "F":
case "E":
case "G":
value_$22 = this.toNumber(value_$22);
out_$1.appendInternal(this.pad(value_$22, length_$17, decimals_$23, pad_$15, sign_$14, radix_$25, force_$24).toUpperCase(), object_$20);
consumearg_$9(argno_$6++);
break;;case "c":
value_$22 = String.fromCharCode(value_$22);;case "w":
var width_$28 = decimals_$23 || Debug.printLength;
out_$1.appendInternal(this.pad(Debug.__String(value_$22, true, width_$28, alternate_$16), length_$17, null, pad_$15, sign_$14, radix_$25, force_$24), object_$20);
consumearg_$9(argno_$6++);
break;;case "s":
var str_$29;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(value_$22) : value_$22 instanceof Function) {
str_$29 = Debug.functionName(value_$22, false);
if (!str_$29) {
str_$29 = "function () {\u2026}"
}} else {
if (typeof value_$22 == "number") {
str_$29 = Number(value_$22).toString(radix_$25)
} else {
str_$29 = "" + value_$22
}};
if (alternate_$16) {
var width_$28 = decimals_$23 || Debug.printLength;
str_$29 = Debug.abbreviate(str_$29, width_$28);
decimals_$23 = null
};
out_$1.appendInternal(this.pad(str_$29, length_$17, decimals_$23, pad_$15, sign_$14, radix_$25, force_$24), object_$20);
consumearg_$9(argno_$6++);
break;;case "d":
case "u":
case "i":
case "o":
case "x":
case "f":
case "e":
case "g":
value_$22 = this.toNumber(value_$22);
out_$1.appendInternal(this.pad(value_$22, length_$17, decimals_$23, pad_$15, sign_$14, radix_$25, force_$24), object_$20);
consumearg_$9(argno_$6++);
break;;case "%":
out_$1.append("%");
break;;default:
out_$1.append(ctrl_$5.substring(base_$10, start_$12));
break
};
ctrl_$5 = ctrl_$5.substring(start_$12, limit_$11);
base_$10 = 0, limit_$11 = ctrl_$5.length;
start_$12 = 0, end_$13 = 0;
if (argno_$6 > maxarg_$7) {
maxarg_$7 = argno_$6
}};
if (maxarg_$7 < al) {
Debug.warn("%#0.48w: excess arguments", control);
out_$1.appendInternal(" ");
for (;maxarg_$7 < al;maxarg_$7++) {
var arg_$3 = getarg_$8(maxarg_$7);
var sep_$4 = maxarg_$7 == al - 1 ? "\n" : " ";
out_$1.append(arg_$3);
out_$1.appendInternal(sep_$4)
}};
return out_$1
};
$lzsc$temp["displayName"] = "formatToString";
return $lzsc$temp
})()], null);
Class.make("LzDebugMessage", LzBootstrapMessage, ["objects", [], "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (message_$1) {
switch (arguments.length) {
case 0:
message_$1 = null
};
this.objects = [];
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, message_$1)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toLowerCase", (function  () {
var $lzsc$temp = function  () {
var msg_$1 = new LzMessage(this.message.toLowerCase());
msg_$1.objects = this.objects.concat();
return msg_$1
};
$lzsc$temp["displayName"] = "toLowerCase";
return $lzsc$temp
})(), "toUpperCase", (function  () {
var $lzsc$temp = function  () {
var msg_$1 = new LzMessage(this.message.toUpperCase());
msg_$1.objects = this.objects.concat();
return msg_$1
};
$lzsc$temp["displayName"] = "toUpperCase";
return $lzsc$temp
})(), "concat", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
var msg_$2 = new LzMessage(this.message.concat.apply(this.message, args_$1));
var offset_$3 = this.message.length;
for (var i_$4 = 0;i_$4 < args_$1.length;i_$4++) {
var arg_$5 = args_$1[i_$4];
if (LzDebugMessage["$lzsc$isa"] ? LzDebugMessage.$lzsc$isa(arg_$5) : arg_$5 instanceof LzDebugMessage) {
var ao_$6 = arg_$5.objects;
for (var j_$7 = 0;j_$7 < ao_$6.length;j_$7++) {
var od_$8 = ao_$6[j_$7];
msg_$2.objects.push({id: od_$8.id, start: od_$8.start + offset_$3, end: od_$8.end + offset_$3})
}};
offset_$3 += String(arg_$5).length
};
return msg_$2
};
$lzsc$temp["displayName"] = "concat";
return $lzsc$temp
})(), "slice", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.slice.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "slice";
return $lzsc$temp
})(), "split", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.split.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "split";
return $lzsc$temp
})(), "substr", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.substr.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "substr";
return $lzsc$temp
})(), "substring", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.message.substring.apply(this.message, args_$1)
};
$lzsc$temp["displayName"] = "substring";
return $lzsc$temp
})(), "appendInternal", (function  () {
var $lzsc$temp = function  (str_$1, obj_$2) {
switch (arguments.length) {
case 1:
obj_$2 = null
};
if (obj_$2 != null) {
var id_$3 = Debug.IDForObject(obj_$2)
} else {
var id_$3 = null
};
if (id_$3 == null) {
this.message += str_$1
} else {
if (LzDebugMessage["$lzsc$isa"] ? LzDebugMessage.$lzsc$isa(obj_$2) : obj_$2 instanceof LzDebugMessage) {
var arg_$4 = obj_$2;
var offset_$5 = this.message.length;
this.message += arg_$4.message;
var ao_$6 = arg_$4.objects;
for (var j_$7 = 0;j_$7 < ao_$6.length;j_$7++) {
var od_$8 = ao_$6[j_$7];
this.objects.push({id: od_$8.id, start: od_$8.start + offset_$5, end: od_$8.end + offset_$5})
}} else {
var start_$9 = this.message.length;
this.message += str_$1;
var end_$10 = this.message.length;
this.objects.push({id: id_$3, start: start_$9, end: end_$10})
}};
this.length = this.message.length
};
$lzsc$temp["displayName"] = "appendInternal";
return $lzsc$temp
})(), "append", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
var len_$2 = args_$1.length;
for (var i_$3 = 0;i_$3 < len_$2;i_$3++) {
var arg_$4 = args_$1[i_$3];
if (!((String["$lzsc$isa"] ? String.$lzsc$isa(arg_$4) : arg_$4 instanceof String) && arg_$4["constructor"] === String) && ((Object["$lzsc$isa"] ? Object.$lzsc$isa(arg_$4) : arg_$4 instanceof Object) || Debug.isObjectLike(arg_$4) || Debug.IDForObject(arg_$4) != null)) {
var str_$5 = Debug.__String(arg_$4, true, Infinity, true);
this.appendInternal(str_$5, arg_$4)
} else {
this.appendInternal(String(arg_$4))
}}};
$lzsc$temp["displayName"] = "append";
return $lzsc$temp
})(), "toArray", (function  () {
var $lzsc$temp = function  (linkMaker_$1) {
switch (arguments.length) {
case 0:
linkMaker_$1 = null
};
if (linkMaker_$1 == null) {
linkMaker_$1 = (function  () {
var $lzsc$temp = function  (rep_$1, id_$2) {
return Debug.ObjectForID(id_$2)
};
$lzsc$temp["displayName"] = "linkMaker";
return $lzsc$temp
})()
};
var msg_$2 = this.message;
var base_$3 = 0;
var limit_$4 = msg_$2.length;
var start_$5 = 0;
var end_$6 = 0;
var objs_$7 = this.objects;
var id_$8;
var array_$9 = [];
var len_$10 = objs_$7.length;
for (var i_$11 = 0;i_$11 < len_$10;i_$11++) {
var annot_$12 = objs_$7[i_$11];
start_$5 = annot_$12.start;
end_$6 = annot_$12.end;
id_$8 = annot_$12.id;
array_$9.push(msg_$2.substring(base_$3, start_$5).toHTML());
array_$9.push(linkMaker_$1(msg_$2.substring(start_$5, end_$6).toHTML(), id_$8));
base_$3 = end_$6
};
array_$9.push(msg_$2.substring(base_$3, limit_$4).toHTML());
return array_$9
};
$lzsc$temp["displayName"] = "toArray";
return $lzsc$temp
})(), "toHTML", (function  () {
var $lzsc$temp = function  () {
return( this.toArray((function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return Debug.makeObjectLink.apply(Debug, args_$1)
};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#204/25";
return $lzsc$temp
})()).join(""))
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], ["xmlEscape", LzBootstrapMessage.xmlEscape]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#29/1";
return $lzsc$temp
})()(LzDebugMessage);
var LzMessage = LzDebugMessage;
Class.make("LzSourceMessage", null, ["file", void 0, "line", void 0, "message", void 0, "length", void 0, "backtrace", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
if ("backtraceStack" in Debug) {
var skip_$4;
if ((String["$lzsc$isa"] ? String.$lzsc$isa(message_$3) : message_$3 instanceof String) && message_$3["$lzsc$b"]) {
Debug.backtraceStack = message_$3.$lzsc$b;
skip_$4 = 2
} else {
var bts_$5 = Debug.backtraceStack;
var btsl_$6 = bts_$5.length;
skip_$4 = 3;
for (var i_$7 = btsl_$6 - 1;i_$7 > skip_$4;i_$7--) {
var callee_$8 = bts_$5[i_$7].callee;
if (callee_$8 === $reportSourceWarning) {
skip_$4 = btsl_$6 - i_$7 + 2
} else {
if (callee_$8 === Debug.warnInternal) {
skip_$4 = btsl_$6 - i_$7 + 1 + 2;
break
}}}};
if (Debug.backtraceStack.length > skip_$4) {
this.backtrace = Debug.backtrace(skip_$4);
if (file_$1 == null && this.backtrace) {
var top_$9 = this.backtrace.userStackFrame();
if (top_$9) {
file_$1 = top_$9.filename();
line_$2 = top_$9.lineno()
}}}};
this.file = file_$1;
this.line = line_$2;
if (message_$3 instanceof LzMessage) {
this.message = message_$3
} else {
this.message = new LzMessage(message_$3)
};
this.length = this.toString().length
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "locationString", (function  () {
var $lzsc$temp = function  (prefix_$1) {
switch (arguments.length) {
case 0:
prefix_$1 = ""
};
var str_$2 = prefix_$1;
if (this.file) {
if (str_$2.length) {
str_$2 += " "
};
str_$2 += "@";
str_$2 += this.file;
if (this.line) {
str_$2 += "#";
str_$2 += this.line
}};
if (str_$2.length) {
str_$2 += ": "
};
return str_$2
};
$lzsc$temp["displayName"] = "locationString";
return $lzsc$temp
})(), "toArray", (function  () {
var $lzsc$temp = function  () {
var array_$1 = [this.locationString(this["constructor"].type)];
if (LzMessage["$lzsc$isa"] ? LzMessage.$lzsc$isa(this.message) : this.message instanceof LzMessage) {
return array_$1.concat(this.message.toArray())
};
return array_$1.concat("" + this.message)
};
$lzsc$temp["displayName"] = "toArray";
return $lzsc$temp
})(), "toStringInternal", (function  () {
var $lzsc$temp = function  (conversion_$1) {
return this.locationString(this["constructor"].type) + this.message[conversion_$1]()
};
$lzsc$temp["displayName"] = "toStringInternal";
return $lzsc$temp
})(), "_dbg_name", (function  () {
var $lzsc$temp = function  () {
return this.locationString("") + this.message
};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#391/19";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.toStringInternal("toString") + "\n"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "toHTML", (function  () {
var $lzsc$temp = function  () {
var id_$1 = Debug.IDForObject(this);
return Debug.makeObjectLink(this.toStringInternal("toHTML"), id_$1, this["constructor"]) + "\n"
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], ["type", "", "color", "#000000", "level", 0, "levelMax", 5, "format", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, control_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
control_$3 = ""
};
var args_$4 = Array.prototype.slice.call(arguments, 3);
var debug_$5 = Debug;
var message_$6 = debug_$5.formatToString.apply(debug_$5, [control_$3].concat(args_$4));
if (file_$1 == null) {
for (var i_$7 = 0;i_$7 < args_$4.length;i_$7++) {
var arg_$8 = args_$4[i_$7];
if (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(arg_$8) : arg_$8 instanceof LzNode) {
file_$1 = arg_$8[Debug.FUNCTION_FILENAME] || null;
line_$2 = arg_$8[Debug.FUNCTION_LINENO] || 0
}}};
return new this(file_$1, line_$2, message_$6)
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})()]);
Class.make("LzWarning", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "WARNING", "color", "#ff9900", "format", LzSourceMessage.format]);
Class.make("LzError", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "ERROR", "color", "#ff0000", "format", LzSourceMessage.format]);
Class.make("LzInfo", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "INFO", "color", "#0066cc", "format", LzSourceMessage.format]);
Class.make("LzDebug", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "DEBUG", "color", "#00cc00", "format", LzSourceMessage.format]);
global._dbg_name = "global";
Class.make("LzDebugService", [LzFormatter, LzBootstrapDebugService], ["base", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (base_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, base_$1 ? base_$1.logger : null, base_$1 ? base_$1.console : null);
this.base = base_$1;
if (!$as3) {
var copy_$2 = {backtraceStack: true, uncaughtBacktraceStack: true};
for (var k_$3 in copy_$2) {
this[k_$3] = base_$1[k_$3]
}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "printLength", 1024, "printDepth", 8, "inspectPrintLength", 128, "inspectPrintDepth", 1, "printDetectCircular", null, "messageLevel", "ALL", "showInternalProperties", false, "atFreshLine", true, "atPrompt", false, "attachDebugConsole", (function  () {
var $lzsc$temp = function  (console_$1) {
var old_$2 = this.console;
this.console = console_$1;
var sm_$3 = old_$2.saved_msgs;
var sml_$4 = sm_$3.length;
for (var i_$5 = 0;i_$5 < sml_$4;i_$5++) {
this.console.addText(sm_$3[i_$5])
};
return this
};
$lzsc$temp["displayName"] = "attachDebugConsole";
return $lzsc$temp
})(), "freshLine", (function  () {
var $lzsc$temp = function  () {
if (!this.atFreshLine) {
this.console.addText("\n");
this.atFreshLine = true
};
this.atPrompt = false
};
$lzsc$temp["displayName"] = "freshLine";
return $lzsc$temp
})(), "freshPrompt", (function  () {
var $lzsc$temp = function  () {
if (!this.atPrompt) {
this.freshLine();
this.console.echo("lzx&gt; ", false);
this.atPrompt = true
}};
$lzsc$temp["displayName"] = "freshPrompt";
return $lzsc$temp
})(), "echo", (function  () {
var $lzsc$temp = function  (str_$1, newLine_$2) {
switch (arguments.length) {
case 1:
newLine_$2 = true
};
this.console.echo(str_$1, newLine_$2);
this.atPrompt = false;
this.atFreshLine = newLine_$2
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "doEval", (function  () {
var $lzsc$temp = function  (expr_$1) {
this.freshPrompt();
this.echo(String(expr_$1)["toHTML"]());
try {
this.console.doEval(expr_$1)
}
catch (e) {
this.error(e)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {
this.console.clear()
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "displayObj", (function  () {
var $lzsc$temp = function  (id_$1) {
var obj_$2 = this.ObjectForID(id_$1);
this.freshPrompt();
this.echo(this.formatToString("Debug.inspect(%0.48w)", obj_$2)["toHTML"]());
this.displayResult(this.inspect(obj_$2))
};
$lzsc$temp["displayName"] = "displayObj";
return $lzsc$temp
})(), "updateOutputState", (function  () {
var $lzsc$temp = function  (msg_$1) {
var str_$2 = String(msg_$1);
this.atFreshLine = str_$2.charAt(str_$2.length - 1) == "\n";
if (str_$2.length) {
this.atPrompt = false
}};
$lzsc$temp["displayName"] = "updateOutputState";
return $lzsc$temp
})(), "displayResult", (function  () {
var $lzsc$temp = function  (result_$1) {
switch (arguments.length) {
case 0:
result_$1 = void 0
};
var e_$2 = this.environment;
if (result_$1 !== void 0) {
if (result_$1 !== e_$2["_"]) {
if (e_$2["__"] !== void 0) {
e_$2.___ = e_$2.__
};
if (e_$2["_"] !== void 0) {
e_$2.__ = e_$2._
};
e_$2._ = result_$1
}};
this.freshLine();
if (result_$1 !== void 0) {
this.format("%#w", result_$1)
};
this.freshPrompt()
};
$lzsc$temp["displayName"] = "displayResult";
return $lzsc$temp
})(), "__write", (function  () {
var $lzsc$temp = function  (msg_$1) {
if (this.log_all_writes || !(!globalValue("logdebug"))) {
this.logger.log(msg_$1)
};
this.console.addText(msg_$1);
this.updateOutputState(msg_$1)
};
$lzsc$temp["displayName"] = "__write";
return $lzsc$temp
})(), "write", (function  () {
var $lzsc$temp = function  () {
var any_$1 = Array.prototype.slice.call(arguments, 0);
var msg_$2;
msg_$2 = this.formatToString.apply(this, any_$1);
this.freshLine();
this.__write(msg_$2)
};
$lzsc$temp["displayName"] = "write";
return $lzsc$temp
})(), "format", (function  () {
var $lzsc$temp = function  (control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 1);
this.__write(this.formatToString.apply(this, [control_$1].concat(args_$2)))
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})(), "warnInternal", (function  () {
var $lzsc$temp = function  (xtor_$1, control_$2) {
var args_$3 = Array.prototype.slice.call(arguments, 2);
var sourceMessage_$4 = LzSourceMessage;
var level_$5 = sourceMessage_$4.level;
if (level_$5 > sourceMessage_$4.levelMax) {
return
};
try {
sourceMessage_$4.level = level_$5 + 1;
var msg_$6 = xtor_$1["format"].apply(xtor_$1, [null, null, control_$2].concat(args_$3));
var mls_$7 = LzDebugService.messageLevels;
var t_$8 = xtor_$1["type"];
if ((t_$8 in mls_$7) ? mls_$7[t_$8] >= mls_$7[this.messageLevel] : true) {
this.freshLine();
this.__write(msg_$6)
}}
finally {
sourceMessage_$4.level = level_$5
};
return msg_$6
};
$lzsc$temp["displayName"] = "warnInternal";
return $lzsc$temp
})(), "warn", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzWarning].concat(args_$1))
};
$lzsc$temp["displayName"] = "warn";
return $lzsc$temp
})(), "error", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzError].concat(args_$1))
};
$lzsc$temp["displayName"] = "error";
return $lzsc$temp
})(), "info", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzInfo].concat(args_$1))
};
$lzsc$temp["displayName"] = "info";
return $lzsc$temp
})(), "debug", (function  () {
var $lzsc$temp = function  () {
var args_$1 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzDebug].concat(args_$1))
};
$lzsc$temp["displayName"] = "debug";
return $lzsc$temp
})(), "inspect", (function  () {
var $lzsc$temp = function  (obj_$1) {
var msg_$2 = this.inspectInternal(obj_$1);
this.freshLine();
this.console.addHTMLText(msg_$2);
this.updateOutputState(msg_$2);
return obj_$1
};
$lzsc$temp["displayName"] = "inspect";
return $lzsc$temp
})(), "explainStyleBindings", (function  () {
var $lzsc$temp = function  (node_$1, showInherited_$2) {
switch (arguments.length) {
case 1:
showInherited_$2 = false
};
var style_$3 = LzCSSStyle;
var pc_$4 = style_$3.getPropertyCache(node_$1);
var ppc_$5 = style_$3.getPropertyCache(node_$1.immediateparent);
var rc_$6 = style_$3.getRulesCache(node_$1);
var ps_$7 = [];
var sp_$8 = node_$1.__LZStyledProperties;
for (var i_$9 = 0, l_$10 = sp_$8.length;i_$9 < l_$10;i_$9++) {
var p_$11 = sp_$8[i_$9];
if (showInherited_$2 || pc_$4[p_$11] !== ppc_$5[p_$11]) {
ps_$7.push(p_$11)
}};
ps_$7.sort(this.caseInsensitiveOrdering);
var rs_$12 = [];
for (var j_$13 = 0, m_$14 = rc_$6.length;j_$13 < m_$14;j_$13++) {
var r_$15 = rc_$6[j_$13];
var rp_$16 = r_$15.properties;
for (var i_$9 = 0, l_$10 = ps_$7.length;i_$9 < l_$10;i_$9++) {
var p_$11 = ps_$7[i_$9];
if (p_$11 in rp_$16) {
rs_$12.push(r_$15);
break
}}};
var msg_$17 = "";
var vs_$18 = [];
for (var j_$13 = 0, m_$14 = rs_$12.length;j_$13 < m_$14;j_$13++) {
var r_$15 = rs_$12[j_$13];
var rp_$16 = r_$15.properties;
msg_$17 += this.formatToString("/* @%s#%d (specificity %d, order %d) */\n", r_$15[Debug.FUNCTION_FILENAME], r_$15[Debug.FUNCTION_LINENO], r_$15.specificity, r_$15._lexorder).toHTML();
msg_$17 += this.formatToString("%w {\n", r_$15).toHTML();
for (var i_$9 = 0, l_$10 = ps_$7.length;i_$9 < l_$10;i_$9++) {
var p_$11 = ps_$7[i_$9];
if (p_$11 in rp_$16) {
var x_$19 = vs_$18[i_$9];
msg_$17 += this.formatToString("  %s<span style='color:#931391'>%s</span>: ", x_$19 ? "<i style='text-decoration: line-through'>" : "", p_$11);
msg_$17 += this.formatToString("%w", rp_$16[p_$11]).toHTML();
msg_$17 += this.formatToString("%s;\n", x_$19 ? "</i>" : "");
vs_$18[i_$9] = true
}};
msg_$17 += this.formatToString("}\n").toHTML()
};
this.freshLine();
this.console.addHTMLText(msg_$17);
this.updateOutputState(msg_$17);
return node_$1
};
$lzsc$temp["displayName"] = "explainStyleBindings";
return $lzsc$temp
})(), "objseq", 0, "id_to_object_table", [], "IDForObject", (function  () {
var $lzsc$temp = function  (obj_$1, force_$2) {
switch (arguments.length) {
case 1:
force_$2 = false
};
var id_$3;
var ot_$4 = this.id_to_object_table;
for (id_$3 = ot_$4.length - 1;id_$3 >= 0;id_$3--) {
if (ot_$4[id_$3] === obj_$1) {
return id_$3
}};
if (!force_$2) {
if (!this.isObjectLike(obj_$1)) {
return null
}};
id_$3 = this.objseq++;
this.id_to_object_table[id_$3] = obj_$1;
return id_$3
};
$lzsc$temp["displayName"] = "IDForObject";
return $lzsc$temp
})(), "ObjectForID", (function  () {
var $lzsc$temp = function  (id_$1) {
return this.id_to_object_table[id_$1]
};
$lzsc$temp["displayName"] = "ObjectForID";
return $lzsc$temp
})(), "__typeof", (function  () {
var $lzsc$temp = function  (thing_$1) {
try {
var n_$2 = typeof thing_$1;
if (this.isObjectLike(thing_$1)) {
var oc_$3 = (Object["$lzsc$isa"] ? Object.$lzsc$isa(thing_$1) : thing_$1 instanceof Object) && thing_$1["constructor"];
var user_name_$4 = null;
if ("_dbg_typename" in thing_$1) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(thing_$1._dbg_typename) : thing_$1._dbg_typename instanceof Function) {
try {
user_name_$4 = thing_$1._dbg_typename()
}
catch (e_$8) {

}} else {
if (typeof thing_$1._dbg_typename == "string") {
user_name_$4 = thing_$1._dbg_typename
}}};
if (this.isStringLike(user_name_$4)) {
n_$2 = user_name_$4
} else {
if (oc_$3) {
var ocn_$5 = this.functionName(oc_$3, true);
if (ocn_$5) {
n_$2 = ocn_$5
} else {
ocn_$5 = this.functionName(oc_$3, false);
if (!ocn_$5) {
var ts_$6 = thing_$1.toString();
var s_$7 = "[object ".length;
var e_$8 = ts_$6.indexOf("]");
if (ts_$6.indexOf("[object ") == 0 && e_$8 == ts_$6.length - 1) {
ocn_$5 = ts_$6.substring(s_$7, e_$8)
} else {
if (ts_$6.indexOf("[") == 0 && ts_$6.indexOf("]") == ts_$6.length - 1) {
ocn_$5 = ts_$6.substring(1, ts_$6.length - 1)
}}};
if (ocn_$5) {
if (oc_$3 !== globalValue(ocn_$5)) {
var id_$9 = this.IDForObject(oc_$3, true);
ocn_$5 += "#" + id_$9
};
n_$2 = ocn_$5
}};
if (oc_$3 && !(oc_$3["$lzsc$isa"] ? oc_$3.$lzsc$isa(thing_$1) : thing_$1 instanceof oc_$3)) {
if (thing_$1 === oc_$3["prototype"]) {

} else {
n_$2 = "\xBF" + n_$2 + "?"
}} else {
if (oc_$3["prototype"] && (Function["$lzsc$isa"] ? Function.$lzsc$isa(oc_$3.prototype["isPrototypeOf"]) : oc_$3.prototype["isPrototypeOf"] instanceof Function) && !oc_$3.prototype.isPrototypeOf(thing_$1)) {
if (thing_$1 === oc_$3.prototype) {

} else {
n_$2 = "\xA1" + n_$2 + "!"
}}}}}};
try {
if (this.isArrayLike(thing_$1)) {
n_$2 += "(" + thing_$1.length + ")"
}}
catch (e_$8) {

}}
catch (e_$8) {
try {
n_$2 = this.formatToString("Error: %0.24#s computing __typeof", e_$8)
}
catch (e_$8) {
n_$2 = "Recursive error computing __typeof"
}};
return n_$2
};
$lzsc$temp["displayName"] = "__typeof";
return $lzsc$temp
})(), "functionName", (function  () {
var $lzsc$temp = function  (fn_$1, mustBeUnique_$2) {
switch (arguments.length) {
case 1:
mustBeUnique_$2 = false
};
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(fn_$1) : fn_$1 instanceof Function) {
var dn_$3 = Debug.FUNCTION_NAME;
if (fn_$1.hasOwnProperty(dn_$3)) {
var n_$4 = fn_$1[dn_$3]
} else {
var fstring_$5 = fn_$1["toString"]();
var s_$6 = "function ".length;
var e_$7 = fstring_$5.indexOf("(");
if (fstring_$5.indexOf("function ") == 0 && e_$7 > s_$6) {
while (fstring_$5.charAt(s_$6) == " ") {
s_$6++
};
while (fstring_$5.charAt(e_$7 - 1) == " ") {
e_$7--
};
n_$4 = fstring_$5.substring(s_$6, e_$7)
}};
if (n_$4) {
if (!mustBeUnique_$2 || fn_$1 === globalValue(n_$4)) {
return n_$4
}}};
return null
};
$lzsc$temp["displayName"] = "functionName";
return $lzsc$temp
})(), "__StringDescription", (function  () {
var $lzsc$temp = function  (thing, pretty_$1, limit_$2, unique_$3, depth_$4) {
try {
if (thing === void 0) {
return {pretty: true, description: "(void 0)"}};
if (thing === null) {
return {pretty: true, description: "null"}};
var t_$5 = typeof thing;
var debug_name_$6 = null;
var s_$7 = "";
if (this.isObjectLike(thing)) {
var opl_$8 = this.printLength;
try {
this.printLength = limit_$2 < this.inspectPrintLength ? limit_$2 : this.inspectPrintLength;
if ((Function["$lzsc$isa"] ? Function.$lzsc$isa(thing["hasOwnProperty"]) : thing["hasOwnProperty"] instanceof Function) && thing.hasOwnProperty("_dbg_prototype_for")) {
debug_name_$6 = this.functionName(thing._dbg_prototype_for) + ".prototype"
} else {
var dn_$9 = ("_dbg_name" in thing) ? thing._dbg_name : null;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(dn_$9) : dn_$9 instanceof Function) {
try {
debug_name_$6 = thing._dbg_name()
}
catch (e) {

}} else {
if (this.isStringLike(dn_$9)) {
debug_name_$6 = dn_$9
}}}}
finally {
this.printLength = opl_$8
}};
if (this.isStringLike(debug_name_$6)) {
if (debug_name_$6.charAt(0) != "#" && unique_$3) {
pretty_$1 = false
};
s_$7 = this.stringEscape(debug_name_$6)
} else {
if (t_$5 == "null" || t_$5 == "number" || t_$5 == "boolean") {
pretty_$1 = true;
s_$7 = String(thing)
} else {
if (this.isStringLike(thing)) {
if ((String["$lzsc$isa"] ? String.$lzsc$isa(thing) : thing instanceof String) && thing["constructor"] !== String) {
pretty_$1 = false
};
s_$7 = this.abbreviate(thing, limit_$2);
var prc_$10 = s_$7 === thing;
if (!prc_$10 && unique_$3) {
pretty_$1 = false
};
s_$7 = this.stringEscape(s_$7, true);
if (t_$5 == "string" && prc_$10) {
return {pretty: pretty_$1, description: s_$7}};
this.IDForObject(thing, true)
} else {
if (t_$5 == "function" || (Function["$lzsc$isa"] ? Function.$lzsc$isa(thing) : thing instanceof Function)) {
var n_$11 = this.functionName(thing, unique_$3);
if (n_$11 != null) {
s_$7 = n_$11
} else {
if (unique_$3) {
pretty_$1 = false
};
s_$7 = this.functionName(thing, false);
if (s_$7 == null) {
s_$7 = ""
}}} else {
if (this.isObjectLike(thing)) {
if (!thing["constructor"] || !thing.constructor["prototype"] || !(Function["$lzsc$isa"] ? Function.$lzsc$isa(thing.constructor.prototype["isPrototypeOf"]) : thing.constructor.prototype["isPrototypeOf"] instanceof Function) || !thing.constructor.prototype.isPrototypeOf(thing)) {
pretty_$1 = false
};
if (thing["constructor"] && (thing.constructor === Date || thing.constructor === Boolean || thing.constructor === Number)) {
s_$7 = thing.toString();
if (s_$7 == null) {
s_$7 = ""
}} else {
if (String["$lzsc$isa"] ? String.$lzsc$isa(thing) : thing instanceof String) {

} else {
if ((Function["$lzsc$isa"] ? Function.$lzsc$isa(thing["toString"]) : thing["toString"] instanceof Function) && thing.toString !== {}.toString && thing.toString !== [].toString && (s_$7 = (function  () {
var $lzsc$temp = function  () {
try {
var u_$1 = thing.toString();
if (typeof u_$1 != "undefined" && u_$1 != "undefined") {
return u_$1
}}
catch (e) {
return ""
}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1009/24";
return $lzsc$temp
})()())) {
if (unique_$3) {
pretty_$1 = false
}} else {
var names_$12 = [];
var indices_$13 = this.isArrayLike(thing) ? [] : null;
this.objectOwnProperties(thing, names_$12, indices_$13, limit_$2);
if (indices_$13) {
indices_$13.sort(this.numericOrdering)
};
var notPretty_$14 = indices_$13 ? this.isArrayLike(thing) && thing["constructor"] !== Array : this.isObjectLike(thing) && thing["constructor"] !== Object;
if (notPretty_$14 && unique_$3) {
pretty_$1 = false
};
if (indices_$13) {
var next_$15 = 0;
for (var i_$16 = 0;i_$16 < indices_$13.length && s_$7.length < limit_$2;i_$16++) {
var key_$17 = indices_$13[i_$16];
if (key_$17 != next_$15) {
s_$7 += "\u2026, "
};
s_$7 += this.__String(thing[key_$17], true, limit_$2 - 5, false, depth_$4 - 1) + ", ";
next_$15 = key_$17 + 1
};
if (s_$7 != "" && i_$16 == indices_$13.length) {
s_$7 = s_$7.substring(0, s_$7.length - 2)
};
s_$7 = "[" + s_$7 + "]"
} else {
var ellip_$18 = true;
names_$12.sort(this.caseInsensitiveOrdering);
for (var i_$16 = 0;i_$16 < names_$12.length && s_$7.length < limit_$2;i_$16++) {
var e = names_$12[i_$16];
var v_$19 = thing[e];
var tv_$20 = typeof v_$19;
var dtv_$21 = this.__typeof(v_$19);
if (tv_$20 != "undefined" && tv_$20 != "function" && "" + v_$19 != "" && !this.internalProperty(e) && !this.internalProperty(dtv_$21)) {
ellip_$18 = true;
s_$7 += "" + e + ": " + this.__String(v_$19, true, limit_$2 - 5, false, depth_$4 - 1) + ", "
} else {
if (ellip_$18) {
s_$7 += "\u2026, ";
ellip_$18 = false
}}};
if (s_$7 != "" && i_$16 == names_$12.length) {
s_$7 = s_$7.substring(0, s_$7.length - 2)
};
s_$7 = "{" + s_$7 + "}"
}}}}} else {
pretty_$1 = !unique_$3;
s_$7 = String(thing)
}}}}}}
catch (e) {
pretty_$1 = false;
try {
s_$7 = this.formatToString("Error: %0.24#s computing __String", e)
}
catch (e) {
s_$7 = "Recursive error computing __String"
}};
return {pretty: pretty_$1, description: s_$7}};
$lzsc$temp["displayName"] = "__StringDescription";
return $lzsc$temp
})(), "__String", (function  () {
var $lzsc$temp = function  (thing_$1, pretty_$2, limit_$3, unique_$4, depth_$5) {
switch (arguments.length) {
case 1:
pretty_$2 = true;;case 2:
limit_$3 = void 0;;case 3:
unique_$4 = void 0;;case 4:
depth_$5 = void 0
};
var origPrintLength_$6 = this.printLength;
var origPrintDepth_$7 = this.printDepth;
var origPrintDetectCircular_$8 = this.printDetectCircular;
if (limit_$3 === void 0) {
limit_$3 = pretty_$2 ? origPrintLength_$6 : 64
};
if (unique_$4 === void 0) {
unique_$4 = !pretty_$2
};
if (depth_$5 === void 0) {
depth_$5 = origPrintDepth_$7
};
var circular_$9 = origPrintDetectCircular_$8;
if (depth_$5 < 0) {
return "\u2026"
};
var s_$10 = "";
var id_$11 = this.IDForObject(thing_$1);
if (limit_$3 > 0) {
try {
if (id_$11 !== null && circular_$9 != null && circular_$9[id_$11]) {
var cache_$12 = circular_$9[id_$11];
if (pretty_$2 && cache_$12 && cache_$12 !== true) {
s_$10 = cache_$12
} else {
unique_$4 = true
}} else {
if (id_$11 !== null) {
if (!(Array["$lzsc$isa"] ? Array.$lzsc$isa(circular_$9) : circular_$9 instanceof Array)) {
circular_$9 = []
};
circular_$9[id_$11] = true
};
this.printLength = limit_$3;
this.printDepth = depth_$5;
this.printDetectCircular = circular_$9;
var d_$13 = this.__StringDescription(thing_$1, pretty_$2, limit_$3, unique_$4, depth_$5);
s_$10 = d_$13.description;
pretty_$2 = d_$13.pretty;
if (s_$10 && pretty_$2 && id_$11 !== null) {
circular_$9[id_$11] = s_$10
}};
if (pretty_$2 && (!unique_$4 || !this.isObjectLike(thing_$1)) && s_$10 != "" && s_$10.length < limit_$3) {
return s_$10
}}
finally {
this.printLength = origPrintLength_$6;
this.printDepth = origPrintDepth_$7;
this.printDetectCircular = origPrintDetectCircular_$8
}};
if (s_$10.length >= limit_$3 && id_$11 === null) {
id_$11 = this.IDForObject(thing_$1, true)
};
var r_$14 = "\xAB";
r_$14 += this.__typeof(thing_$1);
if (unique_$4 && id_$11 !== null) {
r_$14 += "#" + id_$11
};
if (s_$10 != "") {
var room_$15 = limit_$3 - r_$14.length - 4;
if (room_$15 > 0) {
r_$14 += "| ";
r_$14 += this.abbreviate(s_$10, room_$15)
}};
r_$14 += "\xBB";
return r_$14
};
$lzsc$temp["displayName"] = "__String";
return $lzsc$temp
})(), "inspectContext", null, "inspectInternal", (function  () {
var $lzsc$temp = function  (obj_$1, showInternalProperties_$2) {
switch (arguments.length) {
case 1:
showInternalProperties_$2 = void 0
};
var si_$3 = showInternalProperties_$2 === void 0 ? Debug.showInternalProperties : false;
var octx_$4 = this.inspectContext;
var opdc_$5 = this.printDetectCircular;
try {
if (!this.isObjectLike(obj_$1)) {
var pl_$6 = Infinity
} else {
var pl_$6 = this.inspectPrintLength
};
this.inspectContext = obj_$1;
var id_$7 = this.IDForObject(obj_$1);
if (id_$7 !== null) {
var pdc_$8 = this.printDetectCircular = [];
var d_$9 = this.__StringDescription(obj_$1, true, pl_$6, false, 0);
pdc_$8[id_$7] = d_$9.pretty ? d_$9.description : true
};
var name_$10 = LzMessage.xmlEscape(this.__String(obj_$1, false, pl_$6, true, 0));
if (!this.isObjectLike(obj_$1)) {
return name_$10
};
var names_$11 = [];
var indices_$12 = this.isArrayLike(obj_$1) && !this.isStringLike(obj_$1) ? [] : null;
this.objectOwnProperties(obj_$1, names_$11, indices_$12, Infinity, si_$3);
names_$11.sort(this.caseInsensitiveOrdering);
if (indices_$12) {
indices_$12.sort(this.numericOrdering)
};
var description_$13 = "";
var nnames_$14 = names_$11.length;
var val_$15;
var wid_$16 = 0;
if (("markGeneration" in this) && this.markGeneration > 0) {
for (var i_$17 = 0;i_$17 < nnames_$14;i_$17++) {
var keywidth_$18 = names_$11[i_$17].length;
if (keywidth_$18 > wid_$16) {
wid_$16 = keywidth_$18
}}};
if (indices_$12) {
var keywidth_$18 = ("" + obj_$1.length).length;
if (keywidth_$18 > wid_$16) {
wid_$16 = keywidth_$18
}};
var last_$19;
for (var i_$17 = 0;i_$17 < nnames_$14;i_$17++) {
var key_$20 = names_$11[i_$17];
if (key_$20 != last_$19) {
last_$19 = key_$20;
val_$15 = obj_$1[key_$20];
if (si_$3 || !this.internalProperty(String(key_$20)) && !this.internalProperty(this.__typeof(val_$15))) {
description_$13 += "  " + this.computeSlotDescription(obj_$1, key_$20, val_$15, wid_$16) + "\n"
}}};
if (indices_$12) {
for (var i_$17 = 0;i_$17 < indices_$12.length;i_$17++) {
var key_$20 = indices_$12[i_$17];
val_$15 = obj_$1[key_$20];
description_$13 += "  " + this.computeSlotDescription(obj_$1, key_$20, val_$15, wid_$16) + "\n"
}};
if (("markGeneration" in this) && this.markGeneration > 0) {
var leaked_$21 = this.annotation.leaked;
if (this.isObjectLike(obj_$1) && (Function["$lzsc$isa"] ? Function.$lzsc$isa(obj_$1["hasOwnProperty"]) : obj_$1["hasOwnProperty"] instanceof Function) && obj_$1.hasOwnProperty(leaked_$21) && obj_$1[leaked_$21]) {
name_$10 += " (\xA3" + obj_$1[leaked_$21] + ")"
}}}
catch (e) {
try {
description_$13 = this.formatToString("Error: %0.24#s computing inspectInternal", e).toHTML()
}
catch (e) {
description_$13 = "Recursive error computing inspectInternal"
}}
finally {
this.printDetectCircular = opdc_$5;
this.inspectContext = octx_$4
};
if (description_$13 != "") {
description_$13 = " {\n" + description_$13 + "}"
};
return name_$10 + description_$13
};
$lzsc$temp["displayName"] = "inspectInternal";
return $lzsc$temp
})(), "computeSlotDescription", (function  () {
var $lzsc$temp = function  (obj_$1, key_$2, val_$3, wid_$4) {
var r_$5 = key_$2 + ":";
wid_$4++;
try {
if (("markGeneration" in this) && this.markGeneration > 0) {
var annotation_$6 = this.annotation;
var leaked_$7 = annotation_$6.leaked;
var why_$8 = annotation_$6.why;
var wf_$9 = "        ";
wid_$4 += wf_$9.length;
if ((Object["$lzsc$isa"] ? Object.$lzsc$isa(val_$3) : val_$3 instanceof Object) && (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$3["hasOwnProperty"]) : val_$3["hasOwnProperty"] instanceof Function) && val_$3.hasOwnProperty(leaked_$7) && val_$3[leaked_$7] && (!obj_$1.hasOwnProperty(leaked_$7) || val_$3[why_$8].indexOf(obj_$1[why_$8]) == 0)) {
r_$5 += this.pad(" (\xA3" + val_$3[leaked_$7] + ")", wf_$9.length)
} else {
r_$5 += wf_$9
}};
var ostr_$10 = LzMessage.xmlEscape(this.__String(val_$3, true, this.inspectPrintLength, false, this.inspectPrintDepth));
var id_$11 = this.IDForObject(val_$3);
r_$5 = this.pad(r_$5, wid_$4);
r_$5 += " " + this.console.makeObjectLink(ostr_$10, id_$11)
}
catch (e) {
try {
r_$5 += this.formatToString(" Error: %0.24#s computing slot description", e).toHTML()
}
catch (e) {
r_$5 += " Error computing description"
}};
return r_$5
};
$lzsc$temp["displayName"] = "computeSlotDescription";
return $lzsc$temp
})(), "isObjectLike", (function  () {
var $lzsc$temp = function  (obj_$1) {
return obj_$1 && ((Object["$lzsc$isa"] ? Object.$lzsc$isa(obj_$1) : obj_$1 instanceof Object) || typeof obj_$1 == "object")
};
$lzsc$temp["displayName"] = "isObjectLike";
return $lzsc$temp
})(), "isArrayLike", (function  () {
var $lzsc$temp = function  (obj_$1) {
if (obj_$1 && ((Array["$lzsc$isa"] ? Array.$lzsc$isa(obj_$1) : obj_$1 instanceof Array) || obj_$1["length"] != void 0)) {
var ol_$2 = obj_$1.length;
return (typeof ol_$2 == "number" || (Number["$lzsc$isa"] ? Number.$lzsc$isa(ol_$2) : ol_$2 instanceof Number)) && (ol_$2 | 0) === ol_$2 && ol_$2 >= 0
};
return false
};
$lzsc$temp["displayName"] = "isArrayLike";
return $lzsc$temp
})(), "isStringLike", (function  () {
var $lzsc$temp = function  (obj_$1) {
return typeof obj_$1 == "string" || (String["$lzsc$isa"] ? String.$lzsc$isa(obj_$1) : obj_$1 instanceof String) || (LzMessage["$lzsc$isa"] ? LzMessage.$lzsc$isa(obj_$1) : obj_$1 instanceof LzMessage)
};
$lzsc$temp["displayName"] = "isStringLike";
return $lzsc$temp
})(), "caseInsensitiveOrdering", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var al_$3 = a_$1.toLowerCase();
var bl_$4 = b_$2.toLowerCase();
return (al_$3 > bl_$4) - (al_$3 < bl_$4)
};
$lzsc$temp["displayName"] = "caseInsensitiveOrdering";
return $lzsc$temp
})(), "numericOrdering", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var al_$3 = Number(a_$1);
var bl_$4 = Number(b_$2);
return Number(al_$3 > bl_$4) - Number(al_$3 < bl_$4)
};
$lzsc$temp["displayName"] = "numericOrdering";
return $lzsc$temp
})(), "internalProperty", (function  () {
var $lzsc$temp = function  (str_$1) {
var ipp_$2 = LzDebugService.internalPropertyPrefixes;
for (var key_$3 = ipp_$2.length - 1;key_$3 >= 0;key_$3--) {
if (str_$1.indexOf(ipp_$2[key_$3]) == 0) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "internalProperty";
return $lzsc$temp
})(), "abbreviate", (function  () {
var $lzsc$temp = function  (s_$1, l_$2) {
switch (arguments.length) {
case 1:
l_$2 = null
};
if (!l_$2) {
l_$2 = Debug.printLength
};
var ellipsis_$3 = "\u2026";
if (s_$1.length > l_$2 - ellipsis_$3.length) {
s_$1 = s_$1.substring(0, l_$2 - ellipsis_$3.length) + ellipsis_$3
};
return s_$1
};
$lzsc$temp["displayName"] = "abbreviate";
return $lzsc$temp
})(), "stringEscape", (function  () {
var $lzsc$temp = function  (s_$1, quoted_$2) {
switch (arguments.length) {
case 1:
quoted_$2 = false
};
s_$1 = s_$1.split("\\").join("\\\\");
var np_$3 = LzDebugService.singleEscapeCharacters;
var skip_$4 = '"';
var quote_$5 = "";
var ignore_$6 = "'";
if (quoted_$2) {
ignore_$6 = "";
var singles_$7 = s_$1.split("'").length;
var doubles_$8 = s_$1.split('"').length;
if (singles_$7 > doubles_$8) {
skip_$4 = "'";
quote_$5 = '"'
} else {
skip_$4 = '"';
quote_$5 = "'"
}};
for (var i_$9 = 0, l_$10 = np_$3.length;i_$9 < l_$10;i_$9 += 2) {
var rep_$11 = np_$3[i_$9];
var ch_$12 = np_$3[i_$9 + 1];
if (ch_$12 != "\\" && ch_$12 != skip_$4 && ch_$12 != ignore_$6) {
s_$1 = s_$1.split(ch_$12).join(rep_$11)
}};
return quote_$5 + s_$1 + quote_$5
};
$lzsc$temp["displayName"] = "stringEscape";
return $lzsc$temp
})(), "versionInfo", (function  () {
var $lzsc$temp = function  () {
this.write(LzCanvas.versionInfoString())
};
$lzsc$temp["displayName"] = "versionInfo";
return $lzsc$temp
})(), "bugReport", (function  () {
var $lzsc$temp = function  (error_$1, verbose) {
var inspect;
inspect = (function  () {
var $lzsc$temp = function  (obj_$1, verbose_$2) {
var id_$3 = verbose_$2 && Debug.IDForObject(obj_$1);
if (id_$3 && !(id_$3 in inspected)) {
inspected[id_$3] = obj_$1
};
return obj_$1
};
$lzsc$temp["displayName"] = "inspect";
return $lzsc$temp
})();
switch (arguments.length) {
case 0:
error_$1 = null;;case 1:
verbose = null
};
if (error_$1 == null) {
try {
with (this.environment) {
error_$1 = _
}}
catch (e) {

}};
if (typeof error_$1 == "number") {
error_$1 = this.ObjectForID(error_$1)
};
if (!(LzSourceMessage["$lzsc$isa"] ? LzSourceMessage.$lzsc$isa(error_$1) : error_$1 instanceof LzSourceMessage)) {
this.error("You must provide a debugger message to report.  Please inspect a debugger message and try again.");
return
};
if (!(LzBacktrace["$lzsc$isa"] ? LzBacktrace.$lzsc$isa(error_$1.backtrace) : error_$1.backtrace instanceof LzBacktrace)) {
this.error("Backtraces must be on to report a bug.  Please enable backtracing and try again.");
return
};
if (verbose == null) {
verbose = this.showInternalProperties
};
var inspected = {};
this.format("Please copy the following information into your bug report:\n\n---START OF BUG REPORT---\n\nLPS VERSION INFORMATION:\n");
this.versionInfo();
this.format("\nERROR MESSAGE: %s", error_$1);
this.format("\nERROR BACKTRACE:");
var that = this;
error_$1.backtrace.map((function  () {
var $lzsc$temp = function  (frame_$1) {
that.format("\n%w", frame_$1);
that.format("\n  this: %#w", inspect(frame_$1["this"], true));
var args_$2 = frame_$1.arguments;
for (var i_$3 = 0;i_$3 < args_$2.length;i_$3++) {
that.format("\n  arg %2d: %#w", i_$3, inspect(args_$2[i_$3], verbose))
}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1618/7";
return $lzsc$temp
})());
var keys_$2 = [];
for (var id_$3 in inspected) {
keys_$2.push(id_$3)
};
if (keys_$2.length > 0) {
this.format("\n\nOBJECT DETAILS:");
keys_$2.sort((function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var al_$3 = parseInt(a_$1);
var bl_$4 = parseInt(b_$2);
return (al_$3 > bl_$4 ? 1 : 0) - (al_$3 < bl_$4 ? 1 : 0)
};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1634/17";
return $lzsc$temp
})());
for (var i_$4 = 0;i_$4 < keys_$2.length;i_$4++) {
var obj_$5 = inspected[keys_$2[i_$4]];
this.format("\n");
this.inspect(obj_$5);
this.format("\n")
}};
this.format("\n---END OF BUG REPORT---\n")
};
$lzsc$temp["displayName"] = "bugReport";
return $lzsc$temp
})(), "objectOwnProperties", (function  () {
var $lzsc$temp = function  (obj_$1, names_$2, indices_$3, limit_$4, nonEnumerable_$5) {
switch (arguments.length) {
case 1:
names_$2 = null;;case 2:
indices_$3 = null;;case 3:
limit_$4 = Infinity;;case 4:
nonEnumerable_$5 = false
};
var alen_$6 = false;
if (this.isArrayLike(obj_$1)) {
alen_$6 = obj_$1.length
};
var proto_$7 = false;
try {
proto_$7 = obj_$1["constructor"] && typeof obj_$1.constructor["prototype"] == "object" ? obj_$1.constructor.prototype : false
}
catch (e) {

};
for (var key_$8 in obj_$1) {
var isown_$9 = false;
if (!proto_$7) {
isown_$9 = true
} else {
try {
isown_$9 = obj_$1.hasOwnProperty(key_$8)
}
catch (e) {

};
if (!isown_$9) {
var pk_$10;
try {
pk_$10 = proto_$7[key_$8]
}
catch (e) {

};
isown_$9 = obj_$1[key_$8] !== pk_$10
}};
if (isown_$9) {
if (alen_$6 != false && (key_$8 | 0) == key_$8 && 0 <= key_$8 && key_$8 < alen_$6) {
if (indices_$3) {
indices_$3.push(Number(key_$8));
if (--limit_$4 == 0) {
return
}}} else {
if (names_$2) {
names_$2.push(key_$8);
if (--limit_$4 == 0) {
return
}}}}}};
$lzsc$temp["displayName"] = "objectOwnProperties";
return $lzsc$temp
})()], ["messageLevels", {ALL: 0, MONITOR: 1, TRACE: 2, DEBUG: 3, INFO: 4, WARNING: 5, ERROR: 6, NONE: 7}, "internalPropertyPrefixes", ["$", "__", "_dbg_"], "singleEscapeCharacters", ["\\b", "\b", "\\t", "\t", "\\n", "\n", "\\v", String.fromCharCode(11), "\\f", "\f", "\\r", "\r", '\\"', '"', "\\'", "'", "\\\\", "\\"]]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#100/1";
return $lzsc$temp
})()(LzDebugService);
var Debug = new LzDebugService(Debug);
var __LzDebug = Debug;
Class.make("LzDHTMLDebugConsole", LzBootstrapDebugConsole, ["DebugWindow", null, "__reNewline", RegExp("&#xa;|&#xA;|&#10;|\\n", "g"), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (iframe_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.DebugWindow = iframe_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "addHTMLText", (function  () {
var $lzsc$temp = function  (str_$1) {
var dw_$2 = this.DebugWindow;
var dwd_$3 = dw_$2.document;
var span_$4 = dwd_$3.createElement("span");
var dwdb_$5 = dwd_$3.body;
span_$4.innerHTML = '<span class="OUTPUT">' + str_$1.replace(this.__reNewline, "<br />") + "</span>";
dwdb_$5.appendChild(span_$4);
dw_$2.scrollTo(0, dwdb_$5.scrollHeight)
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {
var dw_$1 = this.DebugWindow;
dw_$1.document.body.innerHTML = ""
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "echo", (function  () {
var $lzsc$temp = function  (str_$1, newLine_$2) {
switch (arguments.length) {
case 1:
newLine_$2 = true
};
this.addHTMLText('<span class="DEBUG">' + str_$1 + "</span>" + (newLine_$2 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "doEval", (function  () {
var $lzsc$temp = function  (expr_$1) {
try {
with (Debug.environment) {
var value_$2 = window.eval("(" + expr_$1 + ")")
};
Debug.displayResult(value_$2)
}
catch (e) {
if (!(SyntaxError["$lzsc$isa"] ? SyntaxError.$lzsc$isa(e) : e instanceof SyntaxError)) {
Debug.error("%s", e);
return
};
try {
with (Debug.environment) {
var value_$2 = window.eval(expr_$1)
};
Debug.displayResult(value_$2)
}
catch (e) {
Debug.error("%s", e)
}}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})(), "makeObjectLink", (function  () {
var $lzsc$temp = function  (rep_$1, id_$2, attrs_$3) {
switch (arguments.length) {
case 2:
attrs_$3 = null
};
var type_$4 = attrs_$3 && attrs_$3["type"] ? attrs_$3.type : "INSPECT";
if (id_$2 != null) {
var obj_$5 = Debug.ObjectForID(id_$2);
var tip_$6 = Debug.formatToString("Inspect %0.32#w", obj_$5).toString().toHTML();
return '<span class="' + type_$4 + '" title="' + tip_$6 + '" onclick="javascript:window.parent.$modules.lz.Debug.displayObj(' + id_$2 + ')">' + rep_$1 + "</span>"
};
return rep_$1
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#7/1";
return $lzsc$temp
})()(LzDHTMLDebugConsole);
Class.make("LzDHTMLDebugService", LzDebugService, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (base_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, base_$1);
var copy_$2 = {backtraceStack: true, uncaughtBacktraceStack: true, sourceWarningHistory: true, logger: true, console: true};
for (var k_$3 in copy_$2) {
this[k_$3] = base_$1[k_$3]
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "createDebugIframe", (function  () {
var $lzsc$temp = function  () {
var debugurl_$1 = lz.embed.options.serverroot + "lps/includes/laszlo-debugger.html";
var form_$2 = '<form id="dhtml-debugger-input" onsubmit="$modules.lz.Debug.doEval(document.getElementById(\'LaszloDebuggerInput\').value); return false" action="#">';
var iframe_$3 = '<iframe id="LaszloDebugger" name="LaszloDebugger" src="' + debugurl_$1 + '" width="100%" height="200"></iframe>';
var inputdiv_$4 = '<div><input id="LaszloDebuggerInput" style="width:78%;" type="text"/><input type="button" onclick="$modules.lz.Debug.doEval(document.getElementById(\'LaszloDebuggerInput\').value); return false" value="eval"/><input type="button" onclick="$modules.lz.Debug.clear(); return false" value="clear"/><input type="button" onclick="$modules.lz.Debug.bugReport(); return false" value="bug report"/></div></form>';
var debugdiv_$5 = document.createElement("div");
debugdiv_$5.innerHTML = form_$2 + iframe_$3 + inputdiv_$4;
debugdiv_$5.onmouseover = (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event
};
e_$1.cancelBubble = true;
LzKeyboardKernel.setKeyboardControl(false, true);
return false
};
$lzsc$temp["displayName"] = "debugdiv.onmouseover";
return $lzsc$temp
})();
var y_$6 = canvas.height - 230;
lz.embed.__setAttr(debugdiv_$5, "style", "position:absolute;z-index:10000000;top:" + y_$6 + "px;width:100%;");
canvas.sprite.__LZdiv.appendChild(debugdiv_$5);
var style_$7 = debugdiv_$5.style;
style_$7.position = "absolute";
style_$7.top = y_$6;
style_$7.zIndex = 10000000;
style_$7.width = "100%";
return window.frames["LaszloDebugger"]
};
$lzsc$temp["displayName"] = "createDebugIframe";
return $lzsc$temp
})(), "makeDebugWindow", (function  () {
var $lzsc$temp = function  () {
for (var n_$1 in __ES3Globals) {
var p_$2 = __ES3Globals[n_$1];
try {
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(p_$2) : p_$2 instanceof Function)) {
if (!p_$2._dbg_name) {
p_$2._dbg_name = n_$1
}} else {
if (!Debug.functionName(p_$2)) {
p_$2[Debug.FUNCTION_NAME] = n_$1
}}}
catch (e) {

}};
if (global["lzconsoledebug"] == "true") {
this.attachDebugConsole(new LzFlashRemoteDebugConsole())
} else {
new (lz.LzDebugWindow)()
}};
$lzsc$temp["displayName"] = "makeDebugWindow";
return $lzsc$temp
})(), "hasFeature", (function  () {
var $lzsc$temp = function  (feature_$1, level_$2) {
return document.implementation && document.implementation.hasFeature && document.implementation.hasFeature(feature_$1, level_$2)
};
$lzsc$temp["displayName"] = "hasFeature";
return $lzsc$temp
})(), "__StringDescription", (function  () {
var $lzsc$temp = function  (thing_$1, pretty_$2, limit_$3, unique_$4, depth_$5) {
var nodeToString;
nodeToString = (function  () {
var $lzsc$temp = function  (node_$1) {
var tn_$2 = node_$1.nodeName || "";
var path_$3 = tn_$2.toLowerCase();
var sprite_$4 = node_$1.owner;
var spritedivpath_$5;
if (sprite_$4 instanceof LzSprite && sprite_$4.owner.sprite === sprite_$4) {
for (var key_$6 in sprite_$4) {
if (sprite_$4[key_$6] === node_$1) {
spritedivpath_$5 = Debug.formatToString("%w/@sprite/@%s", sprite_$4.owner, key_$6)
}}};
if (node_$1.nodeType == 1) {
var id_$7 = node_$1.id;
var cn_$8 = node_$1.className;
if (id_$7 && !spritedivpath_$5) {
path_$3 += "#" + encodeURIComponent(id_$7)
} else {
if (cn_$8) {
var more_$9 = cn_$8.indexOf(" ");
if (more_$9 == -1) {
more_$9 = cn_$8.length
};
path_$3 += "." + cn_$8.substring(0, more_$9)
}}};
if (spritedivpath_$5) {
return spritedivpath_$5 + (path_$3.length > 0 ? "/" + path_$3 : "")
};
var parent_$10 = node_$1.parentNode;
if (parent_$10) {
var index_$11, count_$12 = 0;
for (var sibling_$13 = parent_$10.firstChild;sibling_$13;sibling_$13 = sibling_$13.nextSibling) {
if (tn_$2 == sibling_$13.nodeName) {
count_$12++;
if (index_$11) {
break
}};
if (node_$1 === sibling_$13) {
index_$11 = count_$12
}};
if (count_$12 > 1) {
path_$3 += "[" + index_$11 + "]"
};
try {
return nodeToString(parent_$10) + "/" + path_$3
}
catch (e) {
return "\u2026/" + path_$3
}};
return path_$3
};
$lzsc$temp["displayName"] = "nodeToString";
return $lzsc$temp
})();
try {
if ((!(!window.HTMLElement) ? thing_$1 instanceof HTMLElement : typeof thing_$1 == "object" && !thing_$1.constructor) && !isNaN(Number(thing_$1["nodeType"]))) {
var style_$6 = thing_$1.style.cssText;
if (style_$6 != "") {
style_$6 = '[@style="' + style_$6 + '"]'
};
return {pretty: pretty_$2, description: nodeToString(thing_$1) + style_$6}} else {
if (this.hasFeature("mouseevents", "2.0") && (global["MouseEvent"]["$lzsc$isa"] ? global["MouseEvent"].$lzsc$isa(thing_$1) : thing_$1 instanceof global["MouseEvent"])) {
var desc_$7 = thing_$1.type;
if (thing_$1.shiftKey) {
desc_$7 = "shift-" + desc_$7
};
if (thing_$1.ctrlKey) {
desc_$7 = "ctrl-" + desc_$7
};
if (thing_$1.metaKey) {
desc_$7 = "meta-" + desc_$7
};
if (thing_$1.altKey) {
desc_$7 = "alt-" + desc_$7
};
switch (thing_$1.detail) {
case 2:
desc_$7 = "double-" + desc_$7;
break;;case 3:
desc_$7 = "triple-" + desc_$7;
break
};
switch (thing_$1.button) {
case 1:
desc_$7 += "-middle";
break;;case 2:
desc_$7 += "-right";
break
};
return {pretty: pretty_$2, description: desc_$7}}}}
catch (e) {

};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__StringDescription"] || this.nextMethod(arguments.callee, "__StringDescription")).call(this, thing_$1, pretty_$2, limit_$3, unique_$4, depth_$5)
};
$lzsc$temp["displayName"] = "__StringDescription";
return $lzsc$temp
})(), "functionName", (function  () {
var $lzsc$temp = function  (fn_$1, mustBeUnique_$2) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(fn_$1) : fn_$1 instanceof Function) {
if (fn_$1.hasOwnProperty("tagname")) {
var n_$3 = fn_$1.tagname;
if (!mustBeUnique_$2 || fn_$1 === lz[n_$3]) {
return "<" + n_$3 + ">"
} else {
return null
}};
if (fn_$1.hasOwnProperty("classname")) {
var n_$3 = fn_$1.classname;
if (!mustBeUnique_$2 || fn_$1 === eval(n_$3)) {
return n_$3
} else {
return null
}}};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["functionName"] || this.nextMethod(arguments.callee, "functionName")).call(this, fn_$1, mustBeUnique_$2)
};
$lzsc$temp["displayName"] = "functionName";
return $lzsc$temp
})(), "objectOwnProperties", (function  () {
var $lzsc$temp = function  (obj_$1, names_$2, indices_$3, limit_$4, nonEnumerable_$5) {
switch (arguments.length) {
case 1:
names_$2 = null;;case 2:
indices_$3 = null;;case 3:
limit_$4 = Infinity;;case 4:
nonEnumerable_$5 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["objectOwnProperties"] || this.nextMethod(arguments.callee, "objectOwnProperties")).call(this, obj_$1, names_$2, indices_$3, limit_$4, nonEnumerable_$5);
var proto_$6 = false;
try {
proto_$6 = obj_$1["constructor"] && typeof obj_$1.constructor["prototype"] == "object" ? obj_$1.constructor.prototype : false
}
catch (e) {

};
if (names_$2 && nonEnumerable_$5 && proto_$6) {
var unenumerated_$7 = {callee: true, length: true, constructor: true, prototype: true};
for (var key_$8 in unenumerated_$7) {
var isown_$9 = false;
try {
isown_$9 = obj_$1.hasOwnProperty(key_$8)
}
catch (e) {

};
if (!isown_$9) {
var pk_$10;
try {
pk_$10 = proto_$6[key_$8]
}
catch (e) {

};
isown_$9 = obj_$1[key_$8] !== pk_$10
};
if (isown_$9) {
for (var i_$11 = 0, l_$12 = names_$2.length;i_$11 < l_$12;i_$11++) {
if (names_$2[i_$11] == key_$8) {
isown_$9 = false;
break
}}};
if (isown_$9) {
names_$2.push(key_$8)
}}}};
$lzsc$temp["displayName"] = "objectOwnProperties";
return $lzsc$temp
})(), "enableInspectMouseHandlers", (function  () {
var $lzsc$temp = function  (div_$1, enable_$2) {
if (enable_$2) {
div_$1.prev_onclick = div_$1.onclick;
div_$1.style.prev_border = div_$1.style.border;
div_$1.style.prev_margin = div_$1.style.margin;
div_$1.style.border = "1px solid red";
div_$1.style.margin = "-1px";
div_$1.onclick = (function  () {
var $lzsc$temp = function  (e_$1) {
Debug.write("view = ", this.owner.owner)
};
$lzsc$temp["displayName"] = "div.onclick";
return $lzsc$temp
})()
} else {
div_$1.onclick = div_$1.prev_onclick;
div_$1.style.border = div_$1.style.prev_border;
div_$1.style.margin = div_$1.style.prev_margin;
delete div_$1.prev_onclick;
delete div_$1.prev_margin;
delete div_$1.prev_border
}};
$lzsc$temp["displayName"] = "enableInspectMouseHandlers";
return $lzsc$temp
})(), "showDivs", (function  () {
var $lzsc$temp = function  (enable_$1) {
if (enable_$1 == null) {
enable_$1 = true
};
Debug._showDivs(canvas, enable_$1)
};
$lzsc$temp["displayName"] = "showDivs";
return $lzsc$temp
})(), "_showDivs", (function  () {
var $lzsc$temp = function  (view_$1, enable_$2) {
var k_$3 = view_$1.sprite;
if (k_$3 != null) {
var div_$4 = k_$3.__LZdiv;
if (div_$4 != null) {
this.enableInspectMouseHandlers(div_$4, enable_$2)
}};
for (var i_$5 = 0;i_$5 < view_$1.subviews.length;i_$5++) {
var cv_$6 = view_$1.subviews[i_$5];
Debug._showDivs(cv_$6, enable_$2)
}};
$lzsc$temp["displayName"] = "_showDivs";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#117/1";
return $lzsc$temp
})()(LzDHTMLDebugService);
var Debug = new LzDHTMLDebugService(Debug);
var __LzDebug = Debug;
Class.make("LzDebuggerWindowConsoleBridge", LzDebugConsole, ["DebugWindow", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (view_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.DebugWindow = view_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__reNewline", RegExp("&#xa;|&#xA;|&#10;|\\n", "g"), "addHTMLText", (function  () {
var $lzsc$temp = function  (str_$1) {
this.DebugWindow.addHTMLText(str_$1.replace(this.__reNewline, "<br />"))
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {
this.DebugWindow.clearWindow()
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "echo", (function  () {
var $lzsc$temp = function  (str_$1, newLine_$2) {
switch (arguments.length) {
case 1:
newLine_$2 = true
};
this.addHTMLText('<span style="color: #00cc00">' + str_$1 + "</span>" + (newLine_$2 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "makeObjectLink", (function  () {
var $lzsc$temp = function  (rep_$1, id_$2, attrs_$3) {
switch (arguments.length) {
case 2:
attrs_$3 = null
};
var color_$4 = attrs_$3 && attrs_$3["color"] ? attrs_$3.color : "#0000ff";
var decoration_$5 = attrs_$3 && attrs_$3["type"] ? "none" : "underline";
if (id_$2 != null) {
var obj_$6 = Debug.ObjectForID(id_$2);
var tip_$7 = Debug.formatToString("Inspect %0.32#w", obj_$6).toString().toHTML();
return '<span style="cursor: pointer; text-decoration: ' + decoration_$5 + "; color: " + color_$4 + '" title="' + tip_$7 + '" onclick="javascript:$modules.lz.Debug.displayObj(' + id_$2 + ')">' + rep_$1 + "</span>"
};
return rep_$1
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "doEval", (function  () {
var $lzsc$temp = function  (expr_$1) {
try {
with (Debug.environment) {
var value_$2 = window.eval("(" + expr_$1 + ")")
};
Debug.displayResult(value_$2)
}
catch (e) {
if (!(SyntaxError["$lzsc$isa"] ? SyntaxError.$lzsc$isa(e) : e instanceof SyntaxError)) {
Debug.error("%s", e);
return
};
try {
with (Debug.environment) {
var value_$2 = window.eval(expr_$1)
};
Debug.displayResult(value_$2)
}
catch (e) {
Debug.error("%s", e)
}}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebuggerWindowConsoleBridge.js#11/1";
return $lzsc$temp
})()(LzDebuggerWindowConsoleBridge);
Debug.sourceWarningHistory = [];
var $reportSourceWarning = (function  () {
var $lzsc$temp = function  (filename_$1, lineNumber_$2, msg_$3, fatal_$4) {
var warning_$5 = new (fatal_$4 ? LzError : LzWarning)(filename_$1, lineNumber_$2, msg_$3);
var warningString_$6 = warning_$5.toString();
if (Debug.sourceWarningHistory[warningString_$6]) {
return
};
Debug.sourceWarningHistory[warningString_$6] = true;
if (Debug.remoteDebug) {
if (Debug.inEvalRequest) {
Debug.xmlwarnings.push([filename_$1, lineNumber_$2, msg_$3])
} else {
Debug.sockWriteWarning(filename_$1, lineNumber_$2, msg_$3)
}} else {
Debug.freshLine();
Debug.__write(warning_$5)
}};
$lzsc$temp["displayName"] = "debugger/LzCompiler.lzs#19/28";
return $lzsc$temp
})();
Debug.stackOverflow = (function  () {
var $lzsc$temp = function  () {
var bs_$1 = this.backtraceStack;
try {
var old_$2 = bs_$1.maxDepth;
bs_$1.maxDepth *= 1.25;
throw new Error(Debug.error("Stack overflow: %s", Debug.backtrace(bs_$1.length - 50)))
}
finally {
bs_$1.maxDepth = old_$2
}};
$lzsc$temp["displayName"] = "Debug.stackOverflow";
return $lzsc$temp
})();
var __LzStackFrame = (function  () {
var $lzsc$temp = function  (args_$1) {
if (args_$1 instanceof Array) {
this["this"] = args_$1["this"];
this["function"] = args_$1.callee;
this.__lineno = ("lineno" in args_$1) ? args_$1.lineno : this["function"][Debug.FUNCTION_LINENO]
};
this.arguments = args_$1.concat()
};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#41/22";
return $lzsc$temp
})();
__LzStackFrame.prototype.isUserFrame = (function  () {
var $lzsc$temp = function  () {
return this["function"][Debug.FUNCTION_FILENAME].indexOf("lfc") != 0
};
$lzsc$temp["displayName"] = "__LzStackFrame.prototype.isUserFrame";
return $lzsc$temp
})();
__LzStackFrame.prototype.filename = (function  () {
var $lzsc$temp = function  () {
return this["function"][Debug.FUNCTION_FILENAME]
};
$lzsc$temp["displayName"] = "__LzStackFrame.prototype.filename";
return $lzsc$temp
})();
__LzStackFrame.prototype.lineno = (function  () {
var $lzsc$temp = function  () {
return this.__lineno
};
$lzsc$temp["displayName"] = "__LzStackFrame.prototype.lineno";
return $lzsc$temp
})();
__LzStackFrame.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
var callee_$1 = this["function"];
var filename_$2 = callee_$1[Debug.FUNCTION_FILENAME];
var lineno_$3 = this.__lineno;
return Debug.formatToString("%0.64w @%s#%d", callee_$1, filename_$2, lineno_$3)
};
$lzsc$temp["displayName"] = "__LzStackFrame.prototype._dbg_name";
return $lzsc$temp
})();
__LzStackFrame.prototype._dbg_typename = "StackFrame";
var LzBacktrace = (function  () {
var $lzsc$temp = function  (skip_$1) {
switch (arguments.length) {
case 0:
skip_$1 = 0
};
var bs_$2 = Debug.backtraceStack;
var l_$3 = bs_$2.length - skip_$1;
this.length = l_$3;
for (var i_$4 = 0;i_$4 < l_$3;i_$4++) {
var fr_$5 = bs_$2[i_$4];
if (!fr_$5.hasOwnProperty("__LzStackFrame") || fr_$5["lineno"] != fr_$5.__LzStackFrame.__lineno) {
fr_$5.__LzStackFrame = new __LzStackFrame(fr_$5)
};
this[i_$4] = fr_$5.__LzStackFrame
}};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#102/19";
return $lzsc$temp
})();
LzBacktrace.prototype.userStackFrame = (function  () {
var $lzsc$temp = function  () {
for (var i_$1 = this.length - 1;i_$1 >= 0;i_$1--) {
var fr_$2 = this[i_$1];
if (fr_$2.isUserFrame()) {
return fr_$2
}}};
$lzsc$temp["displayName"] = "LzBacktrace.prototype.userStackFrame";
return $lzsc$temp
})();
LzBacktrace.prototype.map = (function  () {
var $lzsc$temp = function  (fn_$1, limit_$2) {
if (!(fn_$1 instanceof Function)) {
return
};
if (!limit_$2) {
limit_$2 = this.length
};
for (var i_$3 = this.length - 1;i_$3 >= 0 && limit_$2 > 0;i_$3--, limit_$2--) {
fn_$1(this[i_$3])
}};
$lzsc$temp["displayName"] = "LzBacktrace.prototype.map";
return $lzsc$temp
})();
LzBacktrace.prototype.toStringInternal = (function  () {
var $lzsc$temp = function  (printer_$1, length_$2) {
switch (arguments.length) {
case 0:
printer_$1 = (function  () {
var $lzsc$temp = function  (o_$1) {
return Debug.__String(o_$1["function"])
};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#160/59";
return $lzsc$temp
})();;case 1:
length_$2 = Debug.printLength
};
var backtrace_$3 = "";
var sep_$4 = " <- ";
for (var i_$5 = this.length - 1;i_$5 >= 0 && backtrace_$3.length < length_$2;i_$5--) {
backtrace_$3 += printer_$1(this[i_$5]) + sep_$4
};
if (backtrace_$3 != "" && i_$5 < 0) {
backtrace_$3 = backtrace_$3.substring(0, backtrace_$3.length - sep_$4.length)
};
backtrace_$3 = Debug.abbreviate(backtrace_$3, length_$2);
return backtrace_$3
};
$lzsc$temp["displayName"] = "LzBacktrace.prototype.toStringInternal";
return $lzsc$temp
})();
LzBacktrace.prototype.toString = (function  () {
var $lzsc$temp = function  () {
return this.toStringInternal()
};
$lzsc$temp["displayName"] = "LzBacktrace.prototype.toString";
return $lzsc$temp
})();
LzBacktrace.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
return( this.toStringInternal((function  () {
var $lzsc$temp = function  (o_$1) {
return Debug.functionName(o_$1["function"], false)
};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#187/32";
return $lzsc$temp
})(), 75))
};
$lzsc$temp["displayName"] = "LzBacktrace.prototype._dbg_name";
return $lzsc$temp
})();
LzBacktrace.prototype._dbg_typename = "Backtrace";
Debug.backtrace = (function  () {
var $lzsc$temp = function  (skip_$1) {
switch (arguments.length) {
case 0:
skip_$1 = 1
};
if (Debug.backtraceStack.length > skip_$1) {
return new LzBacktrace(skip_$1)
}};
$lzsc$temp["displayName"] = "Debug.backtrace";
return $lzsc$temp
})();
Class.make("LzTrace", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "TRACE", "color", "#00cc00", "format", LzSourceMessage.format]);
Debug.traceMessage = (function  () {
var $lzsc$temp = function  (control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 1);
return this.warnInternal.apply(this, [LzTrace, control_$1].concat(args_$2))
};
$lzsc$temp["displayName"] = "Debug.traceMessage";
return $lzsc$temp
})();
Debug.trace = (function  () {
var $lzsc$temp = function  (who_$1, methodName_$2) {
if (who_$1[methodName_$2] instanceof Function) {
var f = who_$1[methodName_$2];
var m_$3 = (function  () {
var $lzsc$temp = function  () {
Debug.traceMessage("[%6.2f] %s.apply(%w, %w)", new Date().getTime() % 1000000, f, this, arguments);
var r_$1 = f.apply(this, arguments);
Debug.traceMessage("[%6.2f] %s -> %w", new Date().getTime() % 1000000, f, r_$1);
return r_$1
};
$lzsc$temp["displayName"] = "debugger/LzTrace.lzs#69/13";
return $lzsc$temp
})();
m_$3._dbg_previous_definition = f;
if (Instance["$lzsc$isa"] ? Instance.$lzsc$isa(who_$1) : who_$1 instanceof Instance) {
who_$1.addProperty(methodName_$2, m_$3)
} else {
who_$1[methodName_$2] = m_$3
};
return who_$1[methodName_$2] === m_$3
} else {
Debug.error("%w.%s is not a function", who_$1, methodName_$2)
};
return false
};
$lzsc$temp["displayName"] = "Debug.trace";
return $lzsc$temp
})();
Debug.untrace = (function  () {
var $lzsc$temp = function  (who_$1, methodName_$2) {
if (who_$1[methodName_$2] instanceof Function) {
var f_$3 = who_$1[methodName_$2];
var p_$4 = f_$3["_dbg_previous_definition"];
if (p_$4) {
if (who_$1.hasOwnProperty(methodName_$2)) {
delete who_$1[methodName_$2]
};
if (who_$1[methodName_$2] !== p_$4) {
if (Instance["$lzsc$isa"] ? Instance.$lzsc$isa(who_$1) : who_$1 instanceof Instance) {
who_$1.addProperty(methodName_$2, p_$4)
} else {
who_$1[methodName_$2] = p_$4
}};
return who_$1[methodName_$2] === p_$4
} else {
Debug.error("%w.%s is not being traced", who_$1, methodName_$2)
}} else {
Debug.error("%w.%s is not a function", who_$1, methodName_$2)
};
return false
};
$lzsc$temp["displayName"] = "Debug.untrace";
return $lzsc$temp
})();
Class.make("LzMonitor", LzSourceMessage, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (file_$1, line_$2, message_$3) {
switch (arguments.length) {
case 0:
file_$1 = null;;case 1:
line_$2 = 0;;case 2:
message_$3 = ""
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$1, line_$2, message_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["type", "MONITOR", "color", "#00cc00", "format", LzSourceMessage.format]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "debugger/LzMonitor.lzs#19/1";
return $lzsc$temp
})()(LzMonitor);
Debug.monitorMessage = (function  () {
var $lzsc$temp = function  (control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 1);
return this.warnInternal.apply(this, [LzMonitor, control_$1].concat(args_$2))
};
$lzsc$temp["displayName"] = "Debug.monitorMessage";
return $lzsc$temp
})();
Debug.monitor = (function  () {
var $lzsc$temp = function  (who, what) {
var o = who[what];
var s_$1 = (function  () {
var $lzsc$temp = function  (n_$1) {
var c_$2 = arguments.callee.caller;
if (!c_$2 && Debug.backtraceStack instanceof Array) {
var bs_$3 = Debug.backtraceStack;
c_$2 = bs_$3[bs_$3.length - 2].callee
};
Debug.monitorMessage("[%6.2f] %s: %w.%s: %w -> %w", new Date().getTime() % 1000000, c_$2 || "(unknown)", who, what, o, n_$1);
return o = n_$1
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#23/11";
return $lzsc$temp
})();
var g_$2 = (function  () {
var $lzsc$temp = function  () {
return o
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#37/11";
return $lzsc$temp
})();
try {
delete who[what];
who.__defineGetter__(what, g_$2);
who.__defineSetter__(what, s_$1);
with (who) {
if (eval(what) !== o) {
throw new Error("Debug.monitor: failed to install functional getter/setter")
}}}
catch (e) {
Debug.error("Debug.monitor: Can't monitor %s.%s", who, what);
delete who[what];
who[what] = o
}};
$lzsc$temp["displayName"] = "Debug.monitor";
return $lzsc$temp
})();
Debug.unmonitor = (function  () {
var $lzsc$temp = function  (who_$1, what_$2) {
var o_$3 = who_$1[what_$2];
delete who_$1[what_$2];
who_$1[what_$2] = o_$3
};
$lzsc$temp["displayName"] = "Debug.unmonitor";
return $lzsc$temp
})();
Debug.annotation = {marked: "_dbg_marked", why: "_dbg_why", size: "_dbg_smoots", total: "_dbg_weight", leaked: "_dbg_leaked"};
Debug.allAnnotations = [];
(function  () {
var $lzsc$temp = function  () {
for (var a_$1 in Debug.annotation) {
Debug.allAnnotations.push(Debug.annotation[a_$1])
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#27/2";
return $lzsc$temp
})()();
Debug.markGeneration = 0;
Debug.noteWhy = false;
Debug.findLeaks = false;
Debug.leaks = [];
Debug.debugTrace = false;
Debug.loops = 0;
Debug.loopCount = 0;
Debug.loopElapsed = 0;
Debug.mark = (function  () {
var $lzsc$temp = function  (o_$1) {
if (!o_$1 || !(typeof o_$1 == "object" || typeof o_$1 == "movieclip") || !(typeof o_$1["hasOwnProperty"] == "function")) {
if (this.debugTrace) {
console.log("Not marking %s\n", o_$1)
};
return false
};
try {
var annotation_$2 = this.annotation;
delete o_$1[annotation_$2.leaked];
o_$1[annotation_$2.marked] = this.markGeneration
}
catch (e) {
return false
};
return true
};
$lzsc$temp["displayName"] = "Debug.mark";
return $lzsc$temp
})();
Debug.isMarked = (function  () {
var $lzsc$temp = function  (o_$1) {
if (!o_$1 || !(typeof o_$1 == "object" || typeof o_$1 == "movieclip") || !(typeof o_$1["hasOwnProperty"] == "function")) {
if (this.debugTrace) {
console.log("Not tracing %s\n", o_$1)
};
return true
};
var marked_$2 = this.annotation.marked;
try {
if (!o_$1.hasOwnProperty(marked_$2)) {
return null
};
return o_$1[marked_$2] == this.markGeneration
}
catch (e) {
if (this.debugTrace) {
console.log("Not tracing %s\n", o_$1)
};
return true
}};
$lzsc$temp["displayName"] = "Debug.isMarked";
return $lzsc$temp
})();
Debug.obstack = [];
Debug.traceStep = (function  () {
var $lzsc$temp = function  (steps_$1, milliseconds_$2) {
switch (arguments.length) {
case 0:
steps_$1 = Infinity;;case 1:
milliseconds_$2 = 1300
};
var loopStart_$3 = new Date().getTime();
var loopCount_$4 = 0;
var loopElapsed_$5 = 0;
var os_$6 = this.obstack;
var dopath_$7 = this.noteWhy || this.debugTrace;
var annotation_$8 = this.annotation;
var marked_$9 = annotation_$8.marked;
var why_$10 = annotation_$8.why;
var size_$11 = annotation_$8.size;
var total_$12 = annotation_$8.total;
var leaked_$13 = annotation_$8.leaked;
while (loopCount_$4++ < steps_$1 && (loopElapsed_$5 = new Date().getTime() - loopStart_$3) < milliseconds_$2) {
while (os_$6.length > 0 && os_$6[0].length == 0) {
os_$6.shift();
if (os_$6.length == 0) {
LzTimeKernel.clearInterval(this.backgroundTask);
this.loops++;
this.loopCount += loopCount_$4;
this.loopElapsed += loopElapsed_$5;
this.debug("%d loops @ %0.0d iterations, %0.2d milliseconds", this.loops, this.loopCount / this.loops, this.loopElapsed / this.loops);
this.format(" \u2026 done!\n");
return true
}};
var ose_$14 = os_$6[0];
var o_$15 = ose_$14.pop();
var oo_$16 = o_$15;
var name_$17 = ose_$14.pop();
var wasMarked_$18 = this.isMarked(o_$15);
var wasLeaked_$19 = wasMarked_$18 === null;
if (wasMarked_$18) {
continue
};
if (dopath_$7) {
var path_$22 = ose_$14.path.concat(name_$17);
try {
o_$15[why_$10] = path_$22.join("\x01")
}
catch (e) {

}};
if (o_$15 !== oo_$16) {
Debug.debug("Annotating %s[%s] (%#w) caused allocation of %#w", path_$22, name_$17, oo_$16, o_$15)
};
if (this.findLeaks && wasLeaked_$19) {
this.leaks.push(o_$15)
};
if (!this.mark(o_$15)) {
continue
};
var obSize_$23 = 0;
var queuedSlots_$24 = [];
if (dopath_$7) {
queuedSlots_$24.path = path_$22;
var ancestors_$25 = ose_$14.ancestors;
queuedSlots_$24.ancestors = ancestors_$25.concat();
queuedSlots_$24.ancestors.push(o_$15)
};
var ownProperties_$26 = [];
try {
this.objectOwnProperties(o_$15, ownProperties_$26, ownProperties_$26, Infinity, true)
}
catch (e) {

};
for (var i_$27 = ownProperties_$26.length - 1;i_$27 >= 0;i_$27--) {
var p_$21 = ownProperties_$26[i_$27];
obSize_$23 += 2;
try {
var v_$28 = o_$15[p_$21];
if (typeof v_$28 == "string") {
obSize_$23 += Math.ceil(v_$28.length / 4)
};
if (!(v_$28 && typeof v_$28 == "object" && v_$28["hasOwnProperty"] instanceof Function)) {
if (this.debugTrace) {

};
v_$28 = null
} else {
if (v_$28 !== o_$15[p_$21]) {
if (this.debugTrace) {

};
v_$28 = null
} else {
v_$28[size_$11] = 0;
if (v_$28 !== o_$15[p_$21]) {
if (this.debugTrace) {
Debug.debug("Mutating %s[%s] (%#w) caused allocation of %#w", o_$15, p_$21, v_$28, o_$15[p_$21])
};
v_$28 = null
}}}}
catch (err) {
if (this.debugTrace) {
Debug.debug("Mutating %s[%s] (%#w) caused %#w", o_$15, p_$21, v_$28, err)
};
v_$28 = null
};
if (v_$28 && !this.isMarked(v_$28)) {
queuedSlots_$24.push(p_$21, v_$28)
}};
o_$15[size_$11] = obSize_$23;
if (dopath_$7) {
o_$15[total_$12] = obSize_$23;
if (wasLeaked_$19) {
o_$15[leaked_$13] = obSize_$23
};
var al_$29 = ancestors_$25.length;
for (var i_$27 = al_$29 - 1;i_$27 >= 0;i_$27--) {
var ai_$30 = ancestors_$25[i_$27];
if (ai_$30) {
ai_$30[total_$12] += obSize_$23;
if (wasLeaked_$19) {
if (ai_$30.hasOwnProperty(leaked_$13)) {
if (this.debugTrace) {
if (o_$15[why_$10].indexOf(ai_$30[why_$10]) != 0) {
console.log("%w(%s) +> %w(%s)\n", o_$15, o_$15[why_$10], ai_$30, ai_$30[why_$10]);
console.log("%w[%d]\n", ancestors_$25, i_$27)
}};
ai_$30[leaked_$13] += obSize_$23
}}}}};
if (queuedSlots_$24.length) {
os_$6.push(queuedSlots_$24)
}};
this.loops++;
this.loopCount += loopCount_$4;
this.loopElapsed += loopElapsed_$5;
return false
};
$lzsc$temp["displayName"] = "Debug.traceStep";
return $lzsc$temp
})();
Debug.initTrace = (function  () {
var $lzsc$temp = function  (findLeaks_$1, noteWhy_$2) {
switch (arguments.length) {
case 0:
findLeaks_$1 = false;;case 1:
noteWhy_$2 = false
};
this.markGeneration++;
this.loops = this.loopCount = this.loopElapsed = 0;
this.findLeaks = findLeaks_$1;
if (findLeaks_$1) {
this.leaks = []
} else {
delete this.leaks
};
this.noteWhy = noteWhy_$2;
for (var t_$3 = this;t_$3 && t_$3 !== Object.prototype;) {
this.mark(t_$3);
t_$3 = ("__proto__" in t_$3) && typeof t_$3.__proto__ == "object" ? t_$3.__proto__ : (("constructor" in t_$3) && typeof t_$3.constructor.prototype == "object" ? t_$3.constructor.prototype : null)
};
if ("frameElement" in global) {
this.mark(global.frameElement)
};
if ("_" in global) {
this.mark(global._)
};
if ("__" in global) {
this.mark(global.__)
};
if ("___" in global) {
this.mark(global.___)
};
if ("console" in global) {
this.mark(global.console)
};
var osel_$4 = ["global", global];
osel_$4.path = [];
osel_$4.ancestors = [];
this.obstack[0] = osel_$4;
this.backgroundTask = LzTimeKernel.setInterval((function  () {
var $lzsc$temp = function  () {
Debug.traceStep()
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#439/50";
return $lzsc$temp
})(), 1400)
};
$lzsc$temp["displayName"] = "Debug.initTrace";
return $lzsc$temp
})();
Debug.markObjects = (function  () {
var $lzsc$temp = function  () {
Debug.warn("Memory tracing is for experimental use only in this runtime.");
Debug.format("Marking objects \u2026 ");
LzTimeKernel.setTimeout((function  () {
var $lzsc$temp = function  () {
Debug.initTrace()
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#453/27";
return $lzsc$temp
})(), 10)
};
$lzsc$temp["displayName"] = "Debug.markObjects";
return $lzsc$temp
})();
Debug.findNewObjects = (function  () {
var $lzsc$temp = function  () {
if (this.markGeneration > 0) {
Debug.warn("Memory tracing is for experimental use only in this runtime.");
Debug.format("Finding new objects \u2026 ");
LzTimeKernel.setTimeout((function  () {
var $lzsc$temp = function  () {
Debug.initTrace(true, true)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#468/29";
return $lzsc$temp
})(), 10)
} else {
Debug.error("Call %w first", Debug.markObjects)
}};
$lzsc$temp["displayName"] = "Debug.findNewObjects";
return $lzsc$temp
})();
Class.make("__LzLeak", null, ["obj", void 0, "path", "", "parent", void 0, "property", "", "leaked", 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (o_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var annotations_$2 = Debug.annotation;
var why_$3 = annotations_$2.why;
var leaked_$4 = annotations_$2.leaked;
this.obj = o_$1;
if (o_$1 && (why_$3 in o_$1) && (leaked_$4 in o_$1)) {
var path_$5 = o_$1[why_$3].split("\x01");
this.property = path_$5.pop();
this.path = path_$5.join(".");
try {
var p_$6 = eval(path_$5[0]);
var pl_$7 = path_$5.length;
for (var i_$8 = 1;i_$8 < pl_$7;i_$8++) {
p_$6 = p_$6[path_$5[i_$8]]
};
this.parent = p_$6
}
catch (e) {

};
this.leaked = Number(o_$1[leaked_$4])
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
if (this.obj) {
return Debug.formatToString("%=s.%s: (\xA3%d) %0.48#w", this["parent"], this.path, this.property, this.leaked, this["obj"])
} else {
return "" + this.obj
}};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
Class.make("__LzLeaks", null, ["length", 0, "sort", Array.prototype.sort, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var l_$1 = Debug.leaks;
var ll_$2 = l_$1.length;
var annotations_$3 = Debug.annotation;
var why = annotations_$3.why;
var size_$4 = annotations_$3.size;
var leaked_$5 = "_dbg_check";
l_$1.sort((function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
try {
var an_$3 = a_$1[why];
var bn_$4 = b_$2[why];
return (an_$3 > bn_$4) - (an_$3 < bn_$4)
}
catch (e) {
return -1
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#541/12";
return $lzsc$temp
})());
this.length = 0;
for (var i_$6 = 0;i_$6 < ll_$2;i_$6 = j_$7) {
var j_$7 = i_$6 + 1;
try {
var p_$8 = l_$1[i_$6];
p_$8[leaked_$5] = p_$8[size_$4];
var pn_$9 = p_$8[why];
if (typeof pn_$9 != "undefined") {
while (j_$7 < ll_$2) {
var c_$10 = l_$1[j_$7];
var cn_$11 = c_$10[why];
if (typeof cn_$11 != "undefined") {
if (cn_$11.indexOf(pn_$9) == 0) {
if (c_$10 !== p_$8) {
p_$8[leaked_$5] += c_$10[size_$4]
} else {
if (Debug.debugTrace) {
console.log("%s is %s\n", pn_$9, cn_$11)
}};
j_$7++;
continue
}};
break
}};
this[this.length++] = new __LzLeak(p_$8)
}
catch (e) {
j_$7++
}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "_dbg_name", (function  () {
var $lzsc$temp = function  () {
var leakage_$1 = 0;
var n_$2 = this.length;
for (var i_$3 = n_$2 - 1;i_$3 >= 0;i_$3--) {
var s_$4 = this[i_$3]["leaked"];
if (!isNaN(s_$4)) {
leakage_$1 += s_$4
}};
return Debug.formatToString("%d smoots [%d objects @ ~%0.0d smoots each]", leakage_$1, n_$2, leakage_$1 / n_$2)
};
$lzsc$temp["displayName"] = "_dbg_name";
return $lzsc$temp
})()], null);
Debug.whyAlive = (function  () {
var $lzsc$temp = function  (top_$1) {
switch (arguments.length) {
case 0:
top_$1 = 10
};
Debug.warn("Memory tracing is for experimental use only in this runtime.");
if (this["leaks"]) {
var l_$2 = new __LzLeaks();
var ll_$3 = l_$2.length;
l_$2.sort((function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var al_$3 = a_$1.leaked;
var bl_$4 = b_$2.leaked;
return (al_$3 < bl_$4) - (al_$3 > bl_$4)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#614/12";
return $lzsc$temp
})());
Debug.format("%w:\n", l_$2);
if (top_$1 > ll_$3) {
top_$1 = ll_$3
};
for (var i_$4 = 0;i_$4 < top_$1;i_$4++) {
Debug.format("%w\n", l_$2[i_$4].toString())
};
if (top_$1 < ll_$3) {
var rest_$5 = 0;
var n_$6 = ll_$3 - i_$4;
for (;i_$4 < ll_$3;i_$4++) {
var lil_$7 = l_$2[i_$4].leaked;
if (!isNaN(lil_$7)) {
rest_$5 += lil_$7
}};
Debug.format("%=s [%d more @ ~%0.0d smoots each]", l_$2, "\u2026", n_$6, rest_$5 / n_$6)
};
return l_$2
} else {
Debug.error("Call %w first", Debug.findNewObjects)
}};
$lzsc$temp["displayName"] = "Debug.whyAlive";
return $lzsc$temp
})();
Class.make("LzDeclaredEventClass", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ready", false, "sendEvent", (function  () {
var $lzsc$temp = function  (eventValue_$1) {
switch (arguments.length) {
case 0:
eventValue_$1 = null
}};
$lzsc$temp["displayName"] = "sendEvent";
return $lzsc$temp
})(), "clearDelegates", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})(), "removeDelegate", (function  () {
var $lzsc$temp = function  (d_$1) {
switch (arguments.length) {
case 0:
d_$1 = null
}};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "getDelegateCount", (function  () {
var $lzsc$temp = function  () {
return 0
};
$lzsc$temp["displayName"] = "getDelegateCount";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzDeclaredEvent"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "core/LzDefs.lzs#21/1";
return $lzsc$temp
})()(LzDeclaredEventClass);
var LzDeclaredEvent = new LzDeclaredEventClass();
Class.make("LzValueExpr", null, null, null);
Class.make("LzInitExpr", LzValueExpr, null, null);
Class.make("LzOnceExpr", LzInitExpr, ["methodName", void 0, "_dbg_name", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (initMethod_$1, description_$2) {
switch (arguments.length) {
case 1:
description_$2 = null
};
this.methodName = initMethod_$1;
this._dbg_name = description_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzConstraintExpr", LzOnceExpr, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (constraintMethod_$1, description_$2) {
switch (arguments.length) {
case 1:
description_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, constraintMethod_$1, description_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzAlwaysExpr", LzConstraintExpr, ["dependenciesName", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (constraintMethod_$1, dependenciesMethod_$2, description_$3) {
switch (arguments.length) {
case 2:
description_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, constraintMethod_$1, description_$3);
this.dependenciesName = dependenciesMethod_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzStyleExpr", LzValueExpr, ["_dbg_name", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzStyleAttr", LzStyleExpr, ["sourceAttributeName", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (sourceAttributeName_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.sourceAttributeName = sourceAttributeName_$1;
this._dbg_name = "attr(" + this.sourceAttributeName + ")"
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzStyleIdent", LzStyleExpr, ["sourceValueID", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (sourceValueID_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.sourceValueID = sourceValueID_$1;
this._dbg_name = sourceValueID_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
function LzInheritedHash (parent_$1) {
if (parent_$1) {
for (var key_$2 in parent_$1) {
this[key_$2] = parent_$1[key_$2]
}}};
var lz;
(function  () {
var $lzsc$temp = function  () {
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(lz) : lz instanceof Object) {

} else {
if (!lz) {
lz = new LzInheritedHash()
} else {
Debug.error("Can't create `lz` namespace, already bound to %w", lz)
}}};
$lzsc$temp["displayName"] = "core/LzDefs.lzs#249/3";
return $lzsc$temp
})()();
lz.DeclaredEventClass = LzDeclaredEventClass;
lz.Formatter = LzFormatter;
lz.colors = {aliceblue: 15792383, antiquewhite: 16444375, aqua: 65535, aquamarine: 8388564, azure: 15794175, beige: 16119260, bisque: 16770244, black: 0, blanchedalmond: 16772045, blue: 255, blueviolet: 9055202, brown: 10824234, burlywood: 14596231, cadetblue: 6266528, chartreuse: 8388352, chocolate: 13789470, coral: 16744272, cornflowerblue: 6591981, cornsilk: 16775388, crimson: 14423100, cyan: 65535, darkblue: 139, darkcyan: 35723, darkgoldenrod: 12092939, darkgray: 11119017, darkgreen: 25600, darkgrey: 11119017, darkkhaki: 12433259, darkmagenta: 9109643, darkolivegreen: 5597999, darkorange: 16747520, darkorchid: 10040012, darkred: 9109504, darksalmon: 15308410, darkseagreen: 9419919, darkslateblue: 4734347, darkslategray: 3100495, darkslategrey: 3100495, darkturquoise: 52945, darkviolet: 9699539, deeppink: 16716947, deepskyblue: 49151, dimgray: 6908265, dimgrey: 6908265, dodgerblue: 2003199, firebrick: 11674146, floralwhite: 16775920, forestgreen: 2263842, fuchsia: 16711935, gainsboro: 14474460, ghostwhite: 16316671, gold: 16766720, goldenrod: 14329120, gray: 8421504, green: 32768, greenyellow: 11403055, grey: 8421504, honeydew: 15794160, hotpink: 16738740, indianred: 13458524, indigo: 4915330, ivory: 16777200, khaki: 15787660, lavender: 15132410, lavenderblush: 16773365, lawngreen: 8190976, lemonchiffon: 16775885, lightblue: 11393254, lightcoral: 15761536, lightcyan: 14745599, lightgoldenrodyellow: 16448210, lightgray: 13882323, lightgreen: 9498256, lightgrey: 13882323, lightpink: 16758465, lightsalmon: 16752762, lightseagreen: 2142890, lightskyblue: 8900346, lightslategray: 7833753, lightslategrey: 7833753, lightsteelblue: 11584734, lightyellow: 16777184, lime: 65280, limegreen: 3329330, linen: 16445670, magenta: 16711935, maroon: 8388608, mediumaquamarine: 6737322, mediumblue: 205, mediumorchid: 12211667, mediumpurple: 9662683, mediumseagreen: 3978097, mediumslateblue: 8087790, mediumspringgreen: 64154, mediumturquoise: 4772300, mediumvioletred: 13047173, midnightblue: 1644912, mintcream: 16121850, mistyrose: 16770273, moccasin: 16770229, navajowhite: 16768685, navy: 128, oldlace: 16643558, olive: 8421376, olivedrab: 7048739, orange: 16753920, orangered: 16729344, orchid: 14315734, palegoldenrod: 15657130, palegreen: 10025880, paleturquoise: 11529966, palevioletred: 14381203, papayawhip: 16773077, peachpuff: 16767673, peru: 13468991, pink: 16761035, plum: 14524637, powderblue: 11591910, purple: 8388736, red: 16711680, rosybrown: 12357519, royalblue: 4286945, saddlebrown: 9127187, salmon: 16416882, sandybrown: 16032864, seagreen: 3050327, seashell: 16774638, sienna: 10506797, silver: 12632256, skyblue: 8900331, slateblue: 6970061, slategray: 7372944, slategrey: 7372944, snow: 16775930, springgreen: 65407, steelblue: 4620980, tan: 13808780, teal: 32896, thistle: 14204888, tomato: 16737095, turquoise: 4251856, violet: 15631086, wheat: 16113331, white: 16777215, whitesmoke: 16119285, yellow: 16776960, yellowgreen: 10145074};
Class.make("LzCache", null, ["size", void 0, "slots", void 0, "destroyable", void 0, "capacity", void 0, "curslot", void 0, "data", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (size_$1, slots_$2, destroyable_$3) {
switch (arguments.length) {
case 0:
size_$1 = 16;;case 1:
slots_$2 = 2;;case 2:
destroyable_$3 = true
};
this.size = size_$1;
this.slots = slots_$2;
this.destroyable = destroyable_$3;
this.clear()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {
this.curslot = 0;
this.capacity = 0;
var sl_$1 = this.slots;
if (!this.data) {
this.data = new Array(sl_$1)
};
var d_$2 = this.data;
for (var i_$3 = 0;i_$3 < sl_$1;++i_$3) {
if (this.destroyable) {
var dobj_$4 = d_$2[i_$3];
for (var k_$5 in dobj_$4) {
dobj_$4[k_$5].destroy()
}};
d_$2[i_$3] = {}}};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "ensureSlot", (function  () {
var $lzsc$temp = function  () {
if (++this.capacity > this.size) {
var nexts_$1 = (this.curslot + 1) % this.slots;
var d_$2 = this.data;
if (this.destroyable) {
var dobj_$3 = d_$2[nexts_$1];
for (var k_$4 in dobj_$3) {
dobj_$3[k_$4].destroy()
}};
d_$2[nexts_$1] = {};
this.curslot = nexts_$1;
this.capacity = 1
}};
$lzsc$temp["displayName"] = "ensureSlot";
return $lzsc$temp
})(), "put", (function  () {
var $lzsc$temp = function  (key_$1, val_$2) {
var old_$3 = this.get(key_$1);
if (old_$3 === void 0) {
this.ensureSlot()
};
this.data[this.curslot][key_$1] = val_$2;
return old_$3
};
$lzsc$temp["displayName"] = "put";
return $lzsc$temp
})(), "get", (function  () {
var $lzsc$temp = function  (key_$1) {
var sl_$2 = this.slots;
var cs_$3 = this.curslot;
var d_$4 = this.data;
for (var i_$5 = 0;i_$5 < sl_$2;++i_$5) {
var idx_$6 = (cs_$3 + i_$5) % sl_$2;
var val_$7 = d_$4[idx_$6][key_$1];
if (val_$7 !== void 0) {
if (idx_$6 != cs_$3) {
delete d_$4[idx_$6][key_$1];
this.ensureSlot();
d_$4[this.curslot][key_$1] = val_$7
};
return val_$7
}};
return void 0
};
$lzsc$temp["displayName"] = "get";
return $lzsc$temp
})()], null);
Class.make("LzEventable", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__LZdeleted", false, "_events", null, "ondestroy", LzDeclaredEvent, "destroy", (function  () {
var $lzsc$temp = function  () {
if (this.ondestroy.ready) {
this.ondestroy.sendEvent(this)
};
this.__LZdeleted = true;
this.__LZdelegatesQueue = null;
this.__LZdeferDelegates = false;
if (this._events != null) {
for (var i_$1 = this._events.length - 1;i_$1 >= 0;i_$1--) {
this._events[i_$1].clearDelegates()
}};
this._events = null
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "__LZdeferDelegates", false, "__LZdelegatesQueue", null, "childOf", (function  () {
var $lzsc$temp = function  (node_$1, ignore_$2) {
switch (arguments.length) {
case 1:
ignore_$2 = null
};
return false
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "setAttribute", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2, ifchanged_$3) {
switch (arguments.length) {
case 2:
ifchanged_$3 = null
};
if (ifchanged_$3 !== null) {
Debug.info("%w.setAttribute(%w, %w, %w):  The third parameter (ifchanged) is deprecated.  Use `if (this.%2$s !== %3$w) this.setAttribute(%2$w, %3$w)` instead", this, prop_$1, val_$2, ifchanged_$3)
};
if (this.__LZdeleted) {
return
};
var s_$4 = "$lzc$set_" + prop_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[s_$4]) : this[s_$4] instanceof Function) {
this[s_$4](val_$2)
} else {
this[prop_$1] = val_$2;
var evt_$5 = this["on" + prop_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa(evt_$5) : evt_$5 instanceof LzEvent) {
if (evt_$5.ready) {
evt_$5.sendEvent(val_$2)
}}}};
$lzsc$temp["displayName"] = "setAttribute";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzEventable.prototype[Debug.FUNCTION_FILENAME] = null;
LzEventable.prototype[Debug.FUNCTION_LINENO] = null;
LzEventable.prototype._dbg_typename = null;
LzEventable.prototype[Debug.FUNCTION_NAME] = null
}}};
$lzsc$temp["displayName"] = "core/LzEventable.lzs#34/1";
return $lzsc$temp
})()(LzEventable);
lz.Eventable = LzEventable;
Class.make("LzStyleAttrBinder", LzEventable, ["target", void 0, "dest", void 0, "source", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (target_$1, dest_$2, source_$3) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.target = target_$1;
this.dest = dest_$2;
this.source = source_$3
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "bind", (function  () {
var $lzsc$temp = function  (ignore_$1) {
switch (arguments.length) {
case 0:
ignore_$1 = null
};
var target_$2 = this.target;
var dest_$3 = this.dest;
var oldvalue_$4 = target_$2[dest_$3];
var newvalue_$5 = target_$2[this.source];
if (newvalue_$5 !== oldvalue_$4 || !target_$2.inited) {
if (!target_$2.__LZdeleted) {
var $lzsc$1522050764 = "$lzc$set_" + dest_$3;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(target_$2[$lzsc$1522050764]) : target_$2[$lzsc$1522050764] instanceof Function) {
target_$2[$lzsc$1522050764](newvalue_$5)
} else {
target_$2[dest_$3] = newvalue_$5;
var $lzsc$1304013616 = target_$2["on" + dest_$3];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1304013616) : $lzsc$1304013616 instanceof LzEvent) {
if ($lzsc$1304013616.ready) {
$lzsc$1304013616.sendEvent(newvalue_$5)
}}}}}};
$lzsc$temp["displayName"] = "bind";
return $lzsc$temp
})()], null);
Class.make("PresentationType", null, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
return value_$1
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function  () {
var $lzsc$temp = function  (value_$1) {
return String(value_$1)
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()]);
Class.make("DefaultPresentationType", PresentationType, null, ["accept", PresentationType.accept, "present", (function  () {
var $lzsc$temp = function  (value_$1) {
return value_$1
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()]);
Class.make("StringPresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
return String(value_$1)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", PresentationType.present]);
Class.make("BooleanPresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
switch (value_$1.toLowerCase()) {
case "":
case "0":
case "false":
return false;;default:
return true
}};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", PresentationType.present]);
Class.make("NumberPresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
return Number(value_$1)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", PresentationType.present]);
Class.make("ColorPresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
return LzColorUtils.hextoint(value_$1)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function  () {
var $lzsc$temp = function  (value_$1) {
var ctab_$2 = lz.colors;
for (var name_$3 in ctab_$2) {
if (ctab_$2[name_$3] === value_$1) {
return name_$3
}};
return LzColorUtils.inttohex(value_$1)
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()]);
Class.make("ExpressionPresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
switch (value_$1) {
case "undefined":
return void 0;;case "null":
return null;;case "false":
return false;;case "true":
return true;;case "NaN":
return 0 / 0;;case "Infinity":
return Infinity;;case "-Infinity":
return -Infinity;;case "":
return ""
};
if (!isNaN(value_$1)) {
return Number(value_$1)
};
return String(value_$1)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", PresentationType.present]);
Class.make("SizePresentationType", PresentationType, null, ["accept", (function  () {
var $lzsc$temp = function  (value_$1) {
if (value_$1 == "null") {
return null
};
return Number(value_$1)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", PresentationType.present]);
Class.make("LzNode", LzEventable, ["__LZisnew", false, "__LZdeferredcarr", null, "classChildren", null, "animators", null, "_instanceAttrs", null, "_instanceChildren", null, "__LzValueExprs", null, "__LZhasConstraint", (function  () {
var $lzsc$temp = function  (attr_$1) {
return attr_$1 in this.__LzValueExprs
};
$lzsc$temp["displayName"] = "__LZhasConstraint";
return $lzsc$temp
})(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.__LZUID = "__U" + ++LzNode.__UIDs;
this.__LZdeferDelegates = true;
if (attrs_$2) {
if (attrs_$2["$lzc$bind_id"]) {
this.$lzc$bind_id = attrs_$2.$lzc$bind_id;
delete attrs_$2.$lzc$bind_id
};
if (attrs_$2["$lzc$bind_name"]) {
this.$lzc$bind_name = attrs_$2.$lzc$bind_name;
delete attrs_$2.$lzc$bind_name
}};
var bindid_$5 = this.$lzc$bind_id;
if (bindid_$5) {
bindid_$5.call(null, this)
};
var bindname_$6 = this.$lzc$bind_name;
if (bindname_$6) {
bindname_$6.call(null, this)
};
this._instanceAttrs = attrs_$2;
this._instanceChildren = children_$3;
var iargs_$7 = new LzInheritedHash(this["constructor"].attributes);
if (!(LzState["$lzsc$isa"] ? LzState.$lzsc$isa(this) : this instanceof LzState)) {
for (var key_$8 in iargs_$7) {
var val_$9 = iargs_$7[key_$8];
if (!(LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(val_$9) : val_$9 instanceof LzInitExpr)) {
var setr_$10 = "$lzc$set_" + key_$8;
if (!this[setr_$10]) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$9) : val_$9 instanceof Function) {
this.addProperty(key_$8, val_$9)
} else {
if (val_$9 !== void 0) {
this[key_$8] = val_$9
} else {
if (this[key_$8] === void 0) {
this[key_$8] = void 0
}}};
delete iargs_$7[key_$8]
} else {
if (this[key_$8] === void 0) {
this[key_$8] = null
}}}}};
if (attrs_$2) {
LzNode.mergeAttributes(attrs_$2, iargs_$7)
};
this.__LZisnew = !instcall_$4;
var classChildren_$11 = this["constructor"]["children"];
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(classChildren_$11) : classChildren_$11 instanceof Array) {
children_$3 = LzNode.mergeChildren(children_$3, classChildren_$11)
};
if (iargs_$7["datapath"] != null) {
delete iargs_$7["$datapath"]
};
var cargs_$12 = this.__LzValueExprs = {};
for (var key_$8 in iargs_$7) {
var val_$9 = iargs_$7[key_$8];
if (LzValueExpr["$lzsc$isa"] ? LzValueExpr.$lzsc$isa(val_$9) : val_$9 instanceof LzValueExpr) {
cargs_$12[key_$8] = val_$9;
delete iargs_$7[key_$8]
}};
this.construct(parent_$1, iargs_$7);
if (this.__LZdeleted) {
return
};
for (var key_$8 in cargs_$12) {
iargs_$7[key_$8] = cargs_$12[key_$8]
};
this.__LzValueExprs = null;
this.__LZapplyArgs(iargs_$7, true);
if (this.__LZdeleted) {
return
};
this.__LZdeferDelegates = false;
var evq_$13 = this.__LZdelegatesQueue;
if (evq_$13) {
LzDelegate.__LZdrainDelegatesQueue(evq_$13);
this.__LZdelegatesQueue = null
};
if (this.onconstruct.ready) {
this.onconstruct.sendEvent(this)
};
if (children_$3 && children_$3.length) {
this.createChildren(children_$3)
} else {
this.__LZinstantiationDone()
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "oninit", LzDeclaredEvent, "onconstruct", LzDeclaredEvent, "ondata", LzDeclaredEvent, "clonenumber", null, "onclonenumber", LzDeclaredEvent, "__LZinstantiated", false, "__LZpreventSubInit", null, "__LZresolveDict", null, "__LZsourceLocation", null, "__LZUID", null, "__LZPropertyCache", null, "__LZStyledProperties", null, "__LZRuleCache", null, "__LZdelegates", null, "isinited", false, "inited", false, "oninited", LzDeclaredEvent, "subnodes", null, "datapath", null, "$lzc$set_datapath", (function  () {
var $lzsc$temp = function  (dp_$1) {
if (null != this.datapath && dp_$1 !== LzNode._ignoreAttribute) {
this.datapath.setXPath(dp_$1)
} else {
new LzDatapath(this, {xpath: dp_$1})
}};
$lzsc$temp["displayName"] = "$lzc$set_datapath";
return $lzsc$temp
})(), "initstage", null, "$isstate", false, "doneClassRoot", false, "parent", void 0, "cloneManager", null, "name", null, "$lzc$bind_name", null, "id", null, "$lzc$set_id", -1, "$lzc$bind_id", null, "defaultplacement", null, "placement", null, "$lzc$set_placement", -1, "$cfn", 0, "immediateparent", null, "dependencies", null, "classroot", void 0, "nodeLevel", 0, "lookupSourceLocator", (function  () {
var $lzsc$temp = function  (sourceLocator_$1) {
return LzNode.sourceLocatorTable[sourceLocator_$1]
};
$lzsc$temp["displayName"] = "lookupSourceLocator";
return $lzsc$temp
})(), "__LZstyleBindAttribute", (function  () {
var $lzsc$temp = function  (attr_$1, prop_$2, type_$3) {
var pc_$4 = this["__LZPropertyCache"] || LzCSSStyle.getPropertyCache(this);
if (!(prop_$2 in pc_$4)) {
Debug.warn("No CSS value found for node %#w for property name %s", this, prop_$2);
pc_$4[prop_$2] = void 0
};
var sp_$5 = this.__LZStyledProperties;
if (sp_$5 == null) {
sp_$5 = this.__LZStyledProperties = []
};
sp_$5.push(prop_$2);
var styleValue_$6 = pc_$4[prop_$2];
if (typeof styleValue_$6 == "string" && styleValue_$6.length > 2 && styleValue_$6.indexOf("0x") == 0 && !isNaN(styleValue_$6)) {
Debug.warn("Invalid CSS value for %w.%s: `%#w`.  Use: `#%06x`.", this, prop_$2, styleValue_$6, Number(styleValue_$6));
styleValue_$6 = Number(styleValue_$6)
};
if (LzStyleExpr["$lzsc$isa"] ? LzStyleExpr.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleExpr) {
if (LzStyleAttr["$lzsc$isa"] ? LzStyleAttr.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleAttr) {
var sa_$7 = styleValue_$6;
var source_$8 = sa_$7.sourceAttributeName;
var binder_$9 = new LzStyleAttrBinder(this, attr_$1, source_$8);
if (!this.__LZdelegates) {
this.__LZdelegates = []
};
this.__LZdelegates.push(new LzDelegate(binder_$9, "bind", this, "on" + source_$8));
binder_$9.bind()
} else {
if (LzStyleIdent["$lzsc$isa"] ? LzStyleIdent.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleIdent) {
var si_$10 = styleValue_$6;
this.acceptAttribute(attr_$1, type_$3, si_$10.sourceValueID)
} else {
Debug.error("Unknown style expression %w", styleValue_$6)
}}} else {
if (!this.__LZdeleted) {
var $lzsc$1240580997 = "$lzc$set_" + attr_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$1240580997]) : this[$lzsc$1240580997] instanceof Function) {
this[$lzsc$1240580997](styleValue_$6)
} else {
this[attr_$1] = styleValue_$6;
var $lzsc$1838694998 = this["on" + attr_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1838694998) : $lzsc$1838694998 instanceof LzEvent) {
if ($lzsc$1838694998.ready) {
$lzsc$1838694998.sendEvent(styleValue_$6)
}}}}}};
$lzsc$temp["displayName"] = "__LZstyleBindAttribute";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
this.__LZdelayedSetters = LzNode.__LZdelayedSetters;
this.earlySetters = LzNode.earlySetters;
var lp_$3 = parent_$1;
this.parent = lp_$3;
if (lp_$3) {
var ip_$4 = lp_$3;
if (args_$2["ignoreplacement"] || this.ignoreplacement) {
this.placement = null
} else {
var thisplacement_$5 = args_$2["placement"] || lp_$3.defaultplacement;
while (thisplacement_$5 != null) {
if (ip_$4.determinePlacement == LzNode.prototype.determinePlacement) {
var pp_$6 = ip_$4.searchSubnodes("name", thisplacement_$5);
if (pp_$6 == null) {
pp_$6 = ip_$4
}} else {
var pp_$6 = ip_$4.determinePlacement(this, thisplacement_$5, args_$2)
};
thisplacement_$5 = pp_$6 != ip_$4 ? pp_$6.defaultplacement : null;
ip_$4 = pp_$6
};
this.placement = thisplacement_$5
};
if (!this.__LZdeleted) {
var ip_subnodes_$7 = ip_$4.subnodes;
if (ip_subnodes_$7 == null) {
ip_$4.subnodes = [this]
} else {
ip_subnodes_$7[ip_subnodes_$7.length] = this
}};
var nl_$8 = ip_$4.nodeLevel;
this.nodeLevel = nl_$8 ? nl_$8 + 1 : 1;
this.immediateparent = ip_$4
} else {
this.nodeLevel = 1
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
return
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "__LZinstantiationDone", (function  () {
var $lzsc$temp = function  () {
this.__LZinstantiated = true;
if (!this.__LZdeleted && (!this.immediateparent || this.immediateparent.isinited || this.initstage == "early" || this.__LZisnew && lz.Instantiator.syncNew)) {
this.__LZcallInit()
}};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "__LZsetPreventInit", (function  () {
var $lzsc$temp = function  () {
this.__LZpreventSubInit = []
};
$lzsc$temp["displayName"] = "__LZsetPreventInit";
return $lzsc$temp
})(), "__LZclearPreventInit", (function  () {
var $lzsc$temp = function  () {
var lzp_$1 = this.__LZpreventSubInit;
this.__LZpreventSubInit = null;
var l_$2 = lzp_$1.length;
for (var i_$3 = 0;i_$3 < l_$2;i_$3++) {
lzp_$1[i_$3].__LZcallInit()
}};
$lzsc$temp["displayName"] = "__LZclearPreventInit";
return $lzsc$temp
})(), "__LZcallInit", (function  () {
var $lzsc$temp = function  (an_$1) {
switch (arguments.length) {
case 0:
an_$1 = null
};
if (this.parent && this.parent.__LZpreventSubInit) {
this.parent.__LZpreventSubInit.push(this);
return
};
this.isinited = true;
this.__LZresolveReferences();
if (this.__LZdeleted) {
return
};
var sl_$2 = this.subnodes;
if (sl_$2) {
for (var i_$3 = 0;i_$3 < sl_$2.length;) {
var s_$4 = sl_$2[i_$3++];
var t_$5 = sl_$2[i_$3];
if (s_$4.isinited || !s_$4.__LZinstantiated) {
continue
};
s_$4.__LZcallInit();
if (this.__LZdeleted) {
return
};
if (t_$5 != sl_$2[i_$3]) {
while (i_$3 > 0) {
if (t_$5 == sl_$2[--i_$3]) {
break
}}}}};
if (this.__LZsourceLocation) {
LzNode.sourceLocatorTable[this.__LZsourceLocation] = this
};
this.init();
if (this.oninit.ready) {
this.oninit.sendEvent(this)
};
if (this.datapath && this.datapath.__LZApplyDataOnInit) {
this.datapath.__LZApplyDataOnInit()
};
this.inited = true;
if (this.oninited.ready) {
this.oninited.sendEvent(true)
}};
$lzsc$temp["displayName"] = "__LZcallInit";
return $lzsc$temp
})(), "completeInstantiation", (function  () {
var $lzsc$temp = function  () {
if (!this.isinited) {
var myis_$1 = this.initstage;
this.initstage = "early";
if (myis_$1 == "defer") {
lz.Instantiator.createImmediate(this, this.__LZdeferredcarr)
} else {
lz.Instantiator.completeTrickle(this)
}}};
$lzsc$temp["displayName"] = "completeInstantiation";
return $lzsc$temp
})(), "ignoreplacement", false, "__LZapplyArgs", (function  () {
var $lzsc$temp = function  (args_$1, constcall_$2) {
switch (arguments.length) {
case 1:
constcall_$2 = null
};
var oset_$3 = {};
var hasset_$4 = null;
var hasearly_$5 = null;
var inits_$6 = null;
var constraints_$7 = null;
for (var key_$8 in args_$1) {
var val_$9 = args_$1[key_$8];
var setr_$10 = "$lzc$set_" + key_$8;
if (oset_$3[key_$8] || args_$1[key_$8] === LzNode._ignoreAttribute) {
continue
};
oset_$3[key_$8] = true;
if (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(val_$9) : val_$9 instanceof LzInitExpr) {
if (val_$9 instanceof LzConstraintExpr) {
if (constraints_$7 == null) {
constraints_$7 = []
};
constraints_$7.push(val_$9)
} else {
if (val_$9 instanceof LzOnceExpr) {
if (inits_$6 == null) {
inits_$6 = []
};
inits_$6.push(val_$9)
} else {
Debug.debug("Unknown init expr: %w", val_$9)
}};
if (this[key_$8] === void 0) {
this[key_$8] = null
}} else {
if (!this[setr_$10]) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$9) : val_$9 instanceof Function) {
this.addProperty(key_$8, val_$9)
} else {
if (val_$9 !== void 0) {
this[key_$8] = val_$9
} else {
if (this[key_$8] === void 0) {
this[key_$8] = void 0
}}};
if (!constcall_$2) {
var evt_$11 = "on" + key_$8;
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa(this[evt_$11]) : this[evt_$11] instanceof LzEvent) {
if (this[evt_$11].ready) {
this[evt_$11].sendEvent(args_$1[key_$8])
}}}} else {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[setr_$10]) : this[setr_$10] instanceof Function) {
if (key_$8 in this.earlySetters) {
if (hasearly_$5 == null) {
hasearly_$5 = []
};
hasearly_$5[this.earlySetters[key_$8]] = key_$8
} else {
if (hasset_$4 == null) {
hasset_$4 = []
};
hasset_$4.push(key_$8)
}}}}};
if (hasearly_$5) {
for (var i_$12 = 1, l_$13 = hasearly_$5.length;i_$12 < l_$13;i_$12++) {
if (i_$12 in hasearly_$5) {
if (this.__LZdeleted) {
return
};
var key_$8 = hasearly_$5[i_$12];
var setr_$10 = "$lzc$set_" + key_$8;
this[setr_$10](args_$1[key_$8])
}}};
if (hasset_$4) {
for (var i_$12 = hasset_$4.length - 1;i_$12 >= 0;i_$12--) {
if (this.__LZdeleted) {
return
};
var key_$8 = hasset_$4[i_$12];
var setr_$10 = "$lzc$set_" + key_$8;
this[setr_$10](args_$1[key_$8])
}};
if (inits_$6 != null) {
this.__LZstoreAttr(inits_$6, "$inits")
};
if (constraints_$7 != null) {
this.__LZstoreAttr(constraints_$7, "$constraints")
}};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "createChildren", (function  () {
var $lzsc$temp = function  (carr_$1) {
if (this.__LZdeleted) {
return
};
if ("defer" == this.initstage) {
this.__LZdeferredcarr = carr_$1
} else {
if ("late" == this.initstage) {
lz.Instantiator.trickleInstantiate(this, carr_$1)
} else {
if (this.__LZisnew && lz.Instantiator.syncNew || "immediate" == this.initstage) {
lz.Instantiator.createImmediate(this, carr_$1)
} else {
lz.Instantiator.requestInstantiation(this, carr_$1)
}}}};
$lzsc$temp["displayName"] = "createChildren";
return $lzsc$temp
})(), "getExpectedAttribute", (function  () {
var $lzsc$temp = function  (prop_$1) {
var e_prop_$2 = "e_" + prop_$1;
if (!this[e_prop_$2]) {
this[e_prop_$2] = {}};
if (this[e_prop_$2].v == null) {
return this[prop_$1]
};
return this[e_prop_$2].v
};
$lzsc$temp["displayName"] = "getExpectedAttribute";
return $lzsc$temp
})(), "setExpectedAttribute", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2) {
var e_prop_$3 = "e_" + prop_$1;
if (!this[e_prop_$3]) {
this[e_prop_$3] = {}};
this[e_prop_$3].v = val_$2
};
$lzsc$temp["displayName"] = "setExpectedAttribute";
return $lzsc$temp
})(), "addToExpectedAttribute", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2) {
var e_prop_$3 = "e_" + prop_$1;
if (!this[e_prop_$3]) {
this[e_prop_$3] = {}};
if (this[e_prop_$3].v == null) {
this[e_prop_$3].v = this[prop_$1]
};
this[e_prop_$3].v += val_$2
};
$lzsc$temp["displayName"] = "addToExpectedAttribute";
return $lzsc$temp
})(), "__LZincrementCounter", (function  () {
var $lzsc$temp = function  (prop_$1) {
var e_prop_$2 = "e_" + prop_$1;
var tep_$3 = this[e_prop_$2];
if (!tep_$3) {
tep_$3 = this[e_prop_$2] = {}};
if (!tep_$3.c) {
tep_$3.c = 0
};
tep_$3.c += 1
};
$lzsc$temp["displayName"] = "__LZincrementCounter";
return $lzsc$temp
})(), "makeChild", (function  () {
var $lzsc$temp = function  (e_$1, async_$2) {
switch (arguments.length) {
case 1:
async_$2 = null
};
if (this.__LZdeleted) {
return
};
for (var p_$3 = this;p_$3 != canvas;p_$3 = p_$3.immediateparent) {
if (p_$3 == null) {
break
};
if (p_$3.__LZdeleted) {
Debug.error("%w.makeChild(%w, %w) when %w.__LZdeleted", this, e_$1, async_$2, p_$3)
}};
var x_$4 = e_$1["class"];
if (!x_$4) {
if (e_$1["tag"]) {
x_$4 = lz[e_$1.tag]
};
if (!x_$4 && e_$1["name"]) {
Debug.deprecated(arguments.callee, "name", "class");
x_$4 = lz[e_$1.name]
}};
var ok_$5 = Function["$lzsc$isa"] ? Function.$lzsc$isa(x_$4) : x_$4 instanceof Function;
if (!ok_$5) {
var name_$6 = e_$1["tag"] || e_$1["name"];
if (name_$6) {
name_$6 = "<" + name_$6 + ">"
} else {
name_$6 = "a class"
};
Debug.error("Attempt to instantiate %s, which has not been defined", name_$6)
};
var w_$7;
if (x_$4) {
w_$7 = new x_$4(this, e_$1.attrs, ("children" in e_$1) ? e_$1.children : null, async_$2)
};
return w_$7
};
$lzsc$temp["displayName"] = "makeChild";
return $lzsc$temp
})(), "$lzc$set_$setters", -1, "$lzc$set_$classrootdepth", (function  () {
var $lzsc$temp = function  (d_$1) {
if (!d_$1) {
return
};
var p_$2 = this.parent;
while (--d_$1 > 0) {
p_$2 = p_$2.parent
};
this.classroot = p_$2
};
$lzsc$temp["displayName"] = "$lzc$set_$classrootdepth";
return $lzsc$temp
})(), "dataBindAttribute", (function  () {
var $lzsc$temp = function  (attr_$1, path_$2, type_$3) {
if (path_$2 == null) {
Debug.warn('No value for %w.%s="$path{%w}"', this, attr_$1, path_$2)
};
if (!this.datapath) {
this.$lzc$set_datapath(".")
};
if (!this.__LZdelegates) {
this.__LZdelegates = []
};
this.__LZdelegates.push(new LzDataAttrBind(this.datapath, attr_$1, path_$2, type_$3))
};
$lzsc$temp["displayName"] = "dataBindAttribute";
return $lzsc$temp
})(), "__LZdelayedSetters", void 0, "earlySetters", void 0, "$lzc$set_$delegates", (function  () {
var $lzsc$temp = function  (delarr_$1) {
var resarray_$2 = [];
var l_$3 = delarr_$1.length;
for (var i_$4 = 0;i_$4 < l_$3;i_$4 += 3) {
if (delarr_$1[i_$4 + 2]) {
resarray_$2.push(delarr_$1[i_$4], delarr_$1[i_$4 + 1], delarr_$1[i_$4 + 2])
} else {
var m_$5 = delarr_$1[i_$4 + 1];
if (!this.__LZdelegates) {
this.__LZdelegates = []
};
this.__LZdelegates.push(new LzDelegate(this, m_$5, this, delarr_$1[i_$4]))
}};
if (resarray_$2.length) {
this.__LZstoreAttr(resarray_$2, "$delegates")
}};
$lzsc$temp["displayName"] = "$lzc$set_$delegates";
return $lzsc$temp
})(), "__LZstoreAttr", (function  () {
var $lzsc$temp = function  (val_$1, prop_$2) {
if (this.__LZresolveDict == null) {
this.__LZresolveDict = {}};
this.__LZresolveDict[prop_$2] = val_$1
};
$lzsc$temp["displayName"] = "__LZstoreAttr";
return $lzsc$temp
})(), "__LZresolveReferences", (function  () {
var $lzsc$temp = function  () {
var rdict_$1 = this.__LZresolveDict;
if (rdict_$1 != null) {
this.__LZresolveDict = null;
var inits_$2 = rdict_$1["$inits"];
if (inits_$2 != null) {
for (var i_$3 = 0, l_$4 = inits_$2.length;i_$3 < l_$4;i_$3++) {
this[inits_$2[i_$3].methodName](null);
if (this.__LZdeleted) {
return
}}};
var constraints_$5 = rdict_$1["$constraints"];
if (constraints_$5 != null) {
for (var i_$3 = 0, l_$4 = constraints_$5.length;i_$3 < l_$4;i_$3++) {
this.applyConstraintExpr(constraints_$5[i_$3]);
if (this.__LZdeleted) {
return
}}};
for (var r_$6 in rdict_$1) {
if (r_$6 == "$inits" || r_$6 == "$constraints" || r_$6 == "$delegates") {
continue
};
if (r_$6 in this.__LZdelayedSetters) {
this[this.__LZdelayedSetters[r_$6]](rdict_$1[r_$6])
} else {
Debug.warn("No delayed setter for %s", r_$6)
}};
if (rdict_$1["$delegates"]) {
this.__LZsetDelegates(rdict_$1.$delegates)
}}};
$lzsc$temp["displayName"] = "__LZresolveReferences";
return $lzsc$temp
})(), "__LZsetDelegates", (function  () {
var $lzsc$temp = function  (delarr_$1) {
if (delarr_$1.length && !this.__LZdelegates) {
this.__LZdelegates = []
};
var l_$2 = delarr_$1.length;
for (var i_$3 = 0;i_$3 < l_$2;i_$3 += 3) {
var sendermethodname_$4 = delarr_$1[i_$3 + 2];
var sender_$5 = sendermethodname_$4 != null ? this[sendermethodname_$4]() : null;
if (sender_$5 == null) {
sender_$5 = this
};
var meth_$6 = delarr_$1[i_$3 + 1];
this.__LZdelegates.push(new LzDelegate(this, meth_$6, sender_$5, delarr_$1[i_$3]))
}};
$lzsc$temp["displayName"] = "__LZsetDelegates";
return $lzsc$temp
})(), "applyConstraint", (function  () {
var $lzsc$temp = function  (attribute_$1, constraint_$2, dependencies_$3) {
Debug.deprecated(this, arguments.callee, this.applyConstraintMethod);
var constraintMethodName_$4 = "$cf" + this.$cfn++;
this.addProperty(constraintMethodName_$4, constraint_$2);
return this.applyConstraintMethod(constraintMethodName_$4, dependencies_$3)
};
$lzsc$temp["displayName"] = "applyConstraint";
return $lzsc$temp
})(), "applyConstraintMethod", (function  () {
var $lzsc$temp = function  (constraintMethodName_$1, dependencies_$2) {
if (!(arguments.length == 2 && typeof constraintMethodName_$1 == "string" && this[constraintMethodName_$1] instanceof Function && (dependencies_$2 == null || dependencies_$2 instanceof Array))) {
Debug.error("%w.%s: invalid arguments %w", this, arguments.callee, arguments)
};
if (dependencies_$2 && dependencies_$2.length > 0) {
if (!this.__LZdelegates) {
this.__LZdelegates = []
};
var dp_$3;
for (var i_$4 = 0, l_$5 = dependencies_$2.length;i_$4 < l_$5;i_$4 += 2) {
dp_$3 = dependencies_$2[i_$4];
if (dp_$3) {
var d_$6 = new LzDelegate(this, constraintMethodName_$1, dp_$3, "on" + dependencies_$2[i_$4 + 1]);
this.__LZdelegates.push(d_$6)
}}};
this[constraintMethodName_$1](null)
};
$lzsc$temp["displayName"] = "applyConstraintMethod";
return $lzsc$temp
})(), "applyConstraintExpr", (function  () {
var $lzsc$temp = function  (expr_$1) {
var constraintMethodName_$2 = expr_$1.methodName;
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(this[constraintMethodName_$2]) : this[constraintMethodName_$2] instanceof Function)) {
Debug.debug("Bad constraint %w on %w", expr_$1, this)
};
var dependencies_$3 = null;
if (expr_$1 instanceof LzAlwaysExpr) {
var c_$4 = expr_$1;
var dependenciesMethodName_$5 = c_$4.dependenciesName;
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(this[dependenciesMethodName_$5]) : this[dependenciesMethodName_$5] instanceof Function)) {
Debug.debug("Bad constraint dependencies %w on %w", expr_$1, this)
};
dependencies_$3 = this[dependenciesMethodName_$5]()
};
this.applyConstraintMethod(constraintMethodName_$2, dependencies_$3)
};
$lzsc$temp["displayName"] = "applyConstraintExpr";
return $lzsc$temp
})(), "releaseConstraint", (function  () {
var $lzsc$temp = function  (attr_$1) {
if (this._instanceAttrs != null) {
var c_$2 = this._instanceAttrs[attr_$1];
if (c_$2 instanceof LzConstraintExpr) {
var m_$3 = c_$2.methodName;
return this.releaseConstraintMethod(m_$3)
}};
return false
};
$lzsc$temp["displayName"] = "releaseConstraint";
return $lzsc$temp
})(), "releaseConstraintMethod", (function  () {
var $lzsc$temp = function  (constraintMethodName_$1) {
var result_$2 = false;
var dels_$3 = this.__LZdelegates;
if (dels_$3) {
for (var i_$4 = 0;i_$4 < dels_$3.length;) {
var del_$5 = dels_$3[i_$4];
if ((LzDelegate["$lzsc$isa"] ? LzDelegate.$lzsc$isa(del_$5) : del_$5 instanceof LzDelegate) && del_$5.c === this && del_$5.m === this[constraintMethodName_$1]) {
del_$5.unregisterAll();
dels_$3.splice(i_$4, 1);
result_$2 = true
} else {
i_$4++
}}};
return result_$2
};
$lzsc$temp["displayName"] = "releaseConstraintMethod";
return $lzsc$temp
})(), "$lzc$set_name", (function  () {
var $lzsc$temp = function  (name_$1) {
if (!(name_$1 === null || typeof name_$1 == "string")) {
Debug.error("Invalid name %#w for %w", name_$1, this);
return
};
var old_$2 = this.name;
var p_$3 = this.parent;
var ip_$4 = this.immediateparent;
if (old_$2 && old_$2 != name_$1) {
if (this.$lzc$bind_name) {
if (globalValue(old_$2) === this) {
this.$lzc$bind_name.call(null, this, false)
}};
if (p_$3) {
if (old_$2 && p_$3[old_$2] === this) {
p_$3[old_$2] = null
}};
if (ip_$4) {
if (old_$2 && ip_$4[old_$2] === this) {
ip_$4[old_$2] = null
}}};
if (name_$1 && name_$1.length) {
if (p_$3 && p_$3[name_$1] && p_$3[name_$1] !== this) {
Debug.warn("Redefining %w.%s from %w to %w", p_$3, name_$1, p_$3[name_$1], this)
};
if (p_$3) {
p_$3[name_$1] = this
};
if (ip_$4 && ip_$4[name_$1] && ip_$4[name_$1] !== this) {
Debug.warn("Redefining %w.%s from %w to %w", ip_$4, name_$1, ip_$4[name_$1], this)
};
if (ip_$4) {
ip_$4[name_$1] = this
}};
this.name = name_$1
};
$lzsc$temp["displayName"] = "$lzc$set_name";
return $lzsc$temp
})(), "defaultSet", (function  () {
var $lzsc$temp = function  (val_$1, prop_$2) {
if (val_$1 != null) {
this[prop_$2] = val_$1
}};
$lzsc$temp["displayName"] = "defaultSet";
return $lzsc$temp
})(), "setDatapath", (function  () {
var $lzsc$temp = function  (dp_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_datapath(dp_$1)
};
$lzsc$temp["displayName"] = "setDatapath";
return $lzsc$temp
})(), "data", null, "$lzc$set_data", (function  () {
var $lzsc$temp = function  (data_$1) {
this.data = data_$1;
if (LzDataNodeMixin["$lzsc$isa"] ? LzDataNodeMixin.$lzsc$isa(data_$1) : data_$1 instanceof LzDataNodeMixin) {
var dp_$2 = this.datapath || new LzDatapath(this);
dp_$2.setPointer(data_$1)
};
if (this.ondata.ready) {
this.ondata.sendEvent(data_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "setData", (function  () {
var $lzsc$temp = function  (data_$1, ignore_$2) {
switch (arguments.length) {
case 1:
ignore_$2 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_data(data_$1)
};
$lzsc$temp["displayName"] = "setData";
return $lzsc$temp
})(), "applyData", (function  () {
var $lzsc$temp = function  (data_$1) {

};
$lzsc$temp["displayName"] = "applyData";
return $lzsc$temp
})(), "updateData", (function  () {
var $lzsc$temp = function  () {
return void 0
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "setSelected", (function  () {
var $lzsc$temp = function  (sel_$1) {

};
$lzsc$temp["displayName"] = "setSelected";
return $lzsc$temp
})(), "options", {}, "$lzc$set_options", (function  () {
var $lzsc$temp = function  (hash_$1) {
if (this.options === this["constructor"].prototype.options) {
this.options = new LzInheritedHash(this.options)
};
for (var key_$2 in hash_$1) {
this.options[key_$2] = hash_$1[key_$2]
}};
$lzsc$temp["displayName"] = "$lzc$set_options";
return $lzsc$temp
})(), "getOption", (function  () {
var $lzsc$temp = function  (key_$1) {
return this.options[key_$1]
};
$lzsc$temp["displayName"] = "getOption";
return $lzsc$temp
})(), "setOption", (function  () {
var $lzsc$temp = function  (key_$1, val_$2) {
if (this.options === this["constructor"].prototype.options) {
this.options = new LzInheritedHash(this.options)
};
this.options[key_$1] = val_$2
};
$lzsc$temp["displayName"] = "setOption";
return $lzsc$temp
})(), "determinePlacement", (function  () {
var $lzsc$temp = function  (aSub_$1, placement_$2, args_$3) {
if (placement_$2 == null) {
var p_$4 = null
} else {
var p_$4 = this.searchSubnodes("name", placement_$2)
};
return p_$4 == null ? this : p_$4
};
$lzsc$temp["displayName"] = "determinePlacement";
return $lzsc$temp
})(), "searchImmediateSubnodes", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2) {
var s_$3 = this.subnodes;
if (s_$3 == null) {
return null
};
for (var i_$4 = s_$3.length - 1;i_$4 >= 0;i_$4--) {
var si_$5 = s_$3[i_$4];
if (si_$5[prop_$1] == val_$2) {
return si_$5
}};
return null
};
$lzsc$temp["displayName"] = "searchImmediateSubnodes";
return $lzsc$temp
})(), "searchSubnodes", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2) {
var nextS_$3 = this.subnodes ? this.subnodes.concat() : [];
while (nextS_$3.length > 0) {
var s_$4 = nextS_$3;
nextS_$3 = new Array();
for (var i_$5 = s_$4.length - 1;i_$5 >= 0;i_$5--) {
var si_$6 = s_$4[i_$5];
if (si_$6[prop_$1] == val_$2) {
return si_$6
};
var sis_$7 = si_$6.subnodes;
if (sis_$7) {
for (var j_$8 = sis_$7.length - 1;j_$8 >= 0;j_$8--) {
nextS_$3.push(sis_$7[j_$8])
}}}};
return null
};
$lzsc$temp["displayName"] = "searchSubnodes";
return $lzsc$temp
})(), "searchParents", (function  () {
var $lzsc$temp = function  (prop_$1) {
var sview_$2 = this;
do{
sview_$2 = sview_$2.immediateparent;
if (sview_$2 == null) {
Debug.error("searchParents got null immediateparent", this);
return
};
if (sview_$2[prop_$1] != null) {
return sview_$2
}} while (sview_$2 != canvas)
};
$lzsc$temp["displayName"] = "searchParents";
return $lzsc$temp
})(), "getUID", (function  () {
var $lzsc$temp = function  () {
return this.__LZUID
};
$lzsc$temp["displayName"] = "getUID";
return $lzsc$temp
})(), "childOf", (function  () {
var $lzsc$temp = function  (node_$1, ignore_$2) {
switch (arguments.length) {
case 1:
ignore_$2 = null
};
if (node_$1 == null) {
return false
};
var pv_$3 = this;
while (pv_$3.nodeLevel >= node_$1.nodeLevel) {
if (pv_$3 == node_$1) {
return true
};
pv_$3 = pv_$3.immediateparent
};
return false
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
if (this.__LZdeleted == true) {
return
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this);
if (this.subnodes != null) {
for (var i_$1 = this.subnodes.length - 1;i_$1 >= 0;i_$1--) {
this.subnodes[i_$1].destroy()
}};
if (this.$lzc$bind_id) {
this.$lzc$bind_id.call(null, this, false)
};
if (this.$lzc$bind_name) {
this.$lzc$bind_name.call(null, this, false)
};
var parent_$2 = this.parent;
var name_$3 = this.name;
if (parent_$2 != null && name_$3 != null) {
if (parent_$2[name_$3] === this) {
parent_$2[name_$3] = null
};
if (this.immediateparent[name_$3] === this) {
this.immediateparent[name_$3] == null
}};
if (this.__LZdelegates != null) {
for (var i_$1 = this.__LZdelegates.length - 1;i_$1 >= 0;i_$1--) {
this.__LZdelegates[i_$1].unregisterAll()
}};
this.__LZdelegates = null;
if (this.immediateparent && this.immediateparent.subnodes) {
for (var i_$1 = this.immediateparent.subnodes.length - 1;i_$1 >= 0;i_$1--) {
if (this.immediateparent.subnodes[i_$1] == this) {
this.immediateparent.subnodes.splice(i_$1, 1);
break
}}};
this.data = null
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "animate", (function  () {
var $lzsc$temp = function  (prop_$1, to_$2, duration_$3, isRelative_$4, moreargs_$5) {
switch (arguments.length) {
case 3:
isRelative_$4 = null;;case 4:
moreargs_$5 = null
};
if (duration_$3 == 0) {
var val_$6 = isRelative_$4 ? this[prop_$1] + to_$2 : to_$2;
if (!this.__LZdeleted) {
var $lzsc$681746910 = "$lzc$set_" + prop_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$681746910]) : this[$lzsc$681746910] instanceof Function) {
this[$lzsc$681746910](val_$6)
} else {
this[prop_$1] = val_$6;
var $lzsc$682472128 = this["on" + prop_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$682472128) : $lzsc$682472128 instanceof LzEvent) {
if ($lzsc$682472128.ready) {
$lzsc$682472128.sendEvent(val_$6)
}}}};
return null
};
var args_$7 = {attribute: prop_$1, to: to_$2, duration: duration_$3, start: true, relative: isRelative_$4, target: this};
for (var p_$8 in moreargs_$5) {
args_$7[p_$8] = moreargs_$5[p_$8]
};
var animator_$9 = new LzAnimator(null, args_$7);
return animator_$9
};
$lzsc$temp["displayName"] = "animate";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.getDebugIdentification()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "getDebugIdentification", (function  () {
var $lzsc$temp = function  () {
var s_$1 = this["constructor"].tagname;
if (this["name"] != null) {
s_$1 += " name: " + this.name
};
if (this["id"] != null) {
s_$1 += " id: " + this.id
};
return s_$1
};
$lzsc$temp["displayName"] = "getDebugIdentification";
return $lzsc$temp
})(), "$lzc$set_$datapath", (function  () {
var $lzsc$temp = function  (dpobj_$1) {
if (dpobj_$1 === LzNode._ignoreAttribute) {
return
} else {
if (!(dpobj_$1 instanceof Object)) {
Debug.debug("%s on non-object %w?", arguments.callee, dpobj_$1)
}};
this.makeChild(dpobj_$1, true)
};
$lzsc$temp["displayName"] = "$lzc$set_$datapath";
return $lzsc$temp
})(), "acceptTypeValue", (function  () {
var $lzsc$temp = function  (type_$1, value_$2) {
var presentationtype_$3 = type_$1 ? LzNode.presentationtypes[type_$1] : null;
if (value_$2 != null) {
if (presentationtype_$3 != null) {
value_$2 = presentationtype_$3.accept(value_$2)
} else {
value_$2 = DefaultPresentationType.accept(value_$2)
}};
return value_$2
};
$lzsc$temp["displayName"] = "acceptTypeValue";
return $lzsc$temp
})(), "acceptAttribute", (function  () {
var $lzsc$temp = function  (name_$1, type_$2, value_$3) {
value_$3 = this.acceptTypeValue(type_$2, value_$3);
if (this[name_$1] !== value_$3) {
if (!this.__LZdeleted) {
var $lzsc$551727635 = "$lzc$set_" + name_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$551727635]) : this[$lzsc$551727635] instanceof Function) {
this[$lzsc$551727635](value_$3)
} else {
this[name_$1] = value_$3;
var $lzsc$311101761 = this["on" + name_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$311101761) : $lzsc$311101761 instanceof LzEvent) {
if ($lzsc$311101761.ready) {
$lzsc$311101761.sendEvent(value_$3)
}}}}}};
$lzsc$temp["displayName"] = "acceptAttribute";
return $lzsc$temp
})(), "presentTypeValue", (function  () {
var $lzsc$temp = function  (type_$1, value_$2) {
var presentationtype_$3 = LzNode.presentationtypes[type_$1];
if (presentationtype_$3 != null && presentationtype_$3["present"]) {
value_$2 = presentationtype_$3.present(value_$2)
} else {
value_$2 = PresentationType.present(value_$2)
};
return value_$2
};
$lzsc$temp["displayName"] = "presentTypeValue";
return $lzsc$temp
})(), "presentAttribute", (function  () {
var $lzsc$temp = function  (name_$1, type_$2) {
return this.presentTypeValue(type_$2, this[name_$1])
};
$lzsc$temp["displayName"] = "presentAttribute";
return $lzsc$temp
})(), "$lzc$presentAttribute_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2, name_$3, type_$4) {
return [self_$2, name_$3]
};
$lzsc$temp["displayName"] = "$lzc$presentAttribute_dependencies";
return $lzsc$temp
})()], ["tagname", "node", "attributes", new LzInheritedHash(), "mergeAttributes", (function  () {
var $lzsc$temp = function  (attrs_$1, dattrs_$2) {
for (var k_$3 in attrs_$1) {
var attrk_$4 = attrs_$1[k_$3];
if (attrk_$4 === LzNode._ignoreAttribute) {
delete dattrs_$2[k_$3]
} else {
if (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(attrk_$4) : attrk_$4 instanceof LzInitExpr) {
dattrs_$2[k_$3] = attrk_$4
} else {
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(attrk_$4) : attrk_$4 instanceof Object) {
var dattrk_$5 = dattrs_$2[k_$3];
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(dattrk_$5) : dattrk_$5 instanceof Object) {
if ((Array["$lzsc$isa"] ? Array.$lzsc$isa(attrk_$4) : attrk_$4 instanceof Array) && (Array["$lzsc$isa"] ? Array.$lzsc$isa(dattrk_$5) : dattrk_$5 instanceof Array)) {
dattrs_$2[k_$3] = attrk_$4.concat(dattrk_$5);
continue
} else {
if ((attrk_$4.constructor === Object || (LzInheritedHash["$lzsc$isa"] ? LzInheritedHash.$lzsc$isa(attrk_$4) : attrk_$4 instanceof LzInheritedHash)) && (dattrk_$5.constructor === Object || (LzInheritedHash["$lzsc$isa"] ? LzInheritedHash.$lzsc$isa(dattrk_$5) : dattrk_$5 instanceof LzInheritedHash))) {
var tmp_$6 = new LzInheritedHash(dattrk_$5);
for (var j_$7 in attrk_$4) {
tmp_$6[j_$7] = attrk_$4[j_$7]
};
dattrs_$2[k_$3] = tmp_$6;
continue
}}}};
dattrs_$2[k_$3] = attrk_$4
}}}};
$lzsc$temp["displayName"] = "mergeAttributes";
return $lzsc$temp
})(), "mergeChildren", (function  () {
var $lzsc$temp = function  (children_$1, superclasschildren_$2) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(superclasschildren_$2) : superclasschildren_$2 instanceof Array) {
children_$1 = superclasschildren_$2.concat((Array["$lzsc$isa"] ? Array.$lzsc$isa(children_$1) : children_$1 instanceof Array) ? children_$1 : [])
};
return children_$1
};
$lzsc$temp["displayName"] = "mergeChildren";
return $lzsc$temp
})(), "sourceLocatorTable", {}, "_ignoreAttribute", {toString: (function  () {
var $lzsc$temp = function  () {
return "_ignoreAttribute"
};
$lzsc$temp["displayName"] = "core/LzNode.lzs#991/44";
return $lzsc$temp
})()}, "__LZdelayedSetters", new LzInheritedHash(), "earlySetters", new LzInheritedHash({name: 1, $events: 2, $delegates: 3, $classrootdepth: 4, $datapath: 5}), "__UIDs", 0, "presentationtypes", {string: StringPresentationType, number: NumberPresentationType, numberExpression: NumberPresentationType, color: ColorPresentationType, "boolean": BooleanPresentationType, inheritableBoolean: BooleanPresentationType, expression: ExpressionPresentationType, size: SizePresentationType}]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzNode.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
var nodePath;
nodePath = (function  () {
var $lzsc$temp = function  (node_$1, limit_$2) {
switch (arguments.length) {
case 1:
limit_$2 = Infinity
};
if (node_$1 === canvas) {
return ""
};
if (node_$1 === Debug.inspectContext) {
return "."
};
var nid_$3 = node_$1.id;
if (typeof nid_$3 == "string" && globalValue(nid_$3) === node_$1) {
return "#" + nid_$3
};
var nn_$4 = node_$1.name;
if (typeof nn_$4 == "string" && globalValue(nn_$4) === node_$1) {
return "#" + nn_$4
};
var parent_$5 = node_$1.immediateparent || node_$1.parent;
var path_$6 = "";
if (parent_$5 != null) {
if (typeof nn_$4 == "string" && parent_$5[nn_$4] === node_$1) {
path_$6 = "@" + nn_$4
} else {
var nct_$7 = node_$1.constructor.tagname;
path_$6 = nct_$7;
var sn_$8 = parent_$5.subnodes;
var index_$9, count_$10 = 0;
for (var i_$11 = 0, len_$12 = sn_$8.length;i_$11 < len_$12;i_$11++) {
var sibling_$13 = sn_$8[i_$11];
if (nct_$7 == sibling_$13.constructor.tagname) {
count_$10++;
if (index_$9) {
break
}};
if (node_$1 === sibling_$13) {
index_$9 = count_$10
}};
if (count_$10 > 1) {
path_$6 += "[" + index_$9 + "]"
}};
if (path_$6.length >= limit_$2) {
return "\u2026"
};
try {
return nodePath(parent_$5, limit_$2 - path_$6.length - 1) + "/" + path_$6
}
catch (e) {
return "\u2026/" + path_$6
}};
return path_$6
};
$lzsc$temp["displayName"] = "nodePath";
return $lzsc$temp
})();
var dn_$1 = nodePath(this, Debug.printLength);
if (dn_$1 != "") {
return dn_$1
};
var ts_$2 = this.toString();
if (ts_$2 == this.getDebugIdentification()) {
return ""
} else {
return ts_$2
}};
$lzsc$temp["displayName"] = "LzNode.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "core/LzNode.lzs#35/1";
return $lzsc$temp
})()(LzNode);
lz[LzNode.tagname] = LzNode;
Class.make("$lzc$class_userClassPlacement", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, placement_$2, ignore_$3, ignoremetoo_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
placement_$2 = null;;case 2:
ignore_$3 = null;;case 3:
ignoremetoo_$4 = null
};
parent_$1.defaultplacement = placement_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
Class.make("LzDelegate", null, ["__delegateID", 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (context_$1, methodName_$2, eventSender_$3, eventName_$4) {
switch (arguments.length) {
case 2:
eventSender_$3 = null;;case 3:
eventName_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
if (LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(context_$1) : context_$1 instanceof LzEventable) {

} else {
if (!(Object["$lzsc$isa"] ? Object.$lzsc$isa(context_$1) : context_$1 instanceof Object)) {
Debug.error("Invalid context: %w (for method %s)", context_$1, methodName_$2);
return
} else {

}};
this.c = context_$1;
var m = context_$1[methodName_$2];
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(m) : m instanceof Function) {
this.m = m;
if (m.length != 1) {
Debug.warn("Invalid delegate: %s.%s => %w (must accept one argument)", context_$1, methodName_$2, m)
}} else {
Debug.error("Invalid delegate: %s.%s => %w (must be a Function)", context_$1, methodName_$2, m)
};
if (eventSender_$3 != null) {
this.register(eventSender_$3, eventName_$4)
};
this.__delegateID = LzDelegate.__nextID++
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "c", void 0, "m", void 0, "lastevent", 0, "enabled", true, "event_called", false, "execute", (function  () {
var $lzsc$temp = function  (eventValue_$1) {
var context_$2 = this.c;
if (this.enabled && context_$2) {
if (context_$2["__LZdeleted"]) {
return
};
var m_$3 = this.m;
return m_$3 && m_$3.call(context_$2, eventValue_$1)
}};
$lzsc$temp["displayName"] = "execute";
return $lzsc$temp
})(), "register", (function  () {
var $lzsc$temp = function  (eventSender_$1, eventName_$2) {
if (LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(eventSender_$1) : eventSender_$1 instanceof LzEventable) {

} else {
if (!(Object["$lzsc$isa"] ? Object.$lzsc$isa(eventSender_$1) : eventSender_$1 instanceof Object)) {
Debug.error("Invalid event sender: %w (for event %s)", eventSender_$1, eventName_$2);
return
} else {

}};
if (this.c["__LZdeleted"]) {
return
};
var anEvent_$3 = eventSender_$1[eventName_$2];
if (!(LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa(anEvent_$3) : anEvent_$3 instanceof LzEvent)) {
if (anEvent_$3 && !(LzDeclaredEventClass["$lzsc$isa"] ? LzDeclaredEventClass.$lzsc$isa(anEvent_$3) : anEvent_$3 instanceof LzDeclaredEventClass)) {
Debug.error("Invalid event: %w.%s => %w", eventSender_$1, eventName_$2, anEvent_$3)
};
anEvent_$3 = new LzEvent(eventSender_$1, eventName_$2, this)
} else {
anEvent_$3.addDelegate(this)
};
this[this.lastevent++] = anEvent_$3
};
$lzsc$temp["displayName"] = "register";
return $lzsc$temp
})(), "unregisterAll", (function  () {
var $lzsc$temp = function  () {
for (var i_$1 = 0;i_$1 < this.lastevent;i_$1++) {
this[i_$1].removeDelegate(this);
this[i_$1] = null
};
this.lastevent = 0
};
$lzsc$temp["displayName"] = "unregisterAll";
return $lzsc$temp
})(), "unregisterFrom", (function  () {
var $lzsc$temp = function  (event_$1) {
var keep_$2 = [];
for (var i_$3 = 0;i_$3 < this.lastevent;i_$3++) {
var ev_$4 = this[i_$3];
if (ev_$4 === event_$1) {
ev_$4.removeDelegate(this)
} else {
keep_$2.push(ev_$4)
};
this[i_$3] = null
};
this.lastevent = 0;
var len_$5 = keep_$2.length;
for (var i_$3 = 0;i_$3 < len_$5;i_$3++) {
this[this.lastevent++] = keep_$2[i_$3]
}};
$lzsc$temp["displayName"] = "unregisterFrom";
return $lzsc$temp
})(), "disable", (function  () {
var $lzsc$temp = function  () {
this.enabled = false
};
$lzsc$temp["displayName"] = "disable";
return $lzsc$temp
})(), "enable", (function  () {
var $lzsc$temp = function  () {
this.enabled = true
};
$lzsc$temp["displayName"] = "enable";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Delegate for " + this.c + " calls " + this.m + " " + this.__delegateID
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["__nextID", 1, "__LZdrainDelegatesQueue", (function  () {
var $lzsc$temp = function  (evq_$1) {
var n_$2 = evq_$1.length;
var i_$3 = 0;
if (i_$3 < n_$2) {
var calledDelegates_$4 = new Array();
var lockedEvents_$5 = new Array();
while (i_$3 < n_$2) {
var e_$6 = evq_$1[i_$3];
var d_$7 = evq_$1[i_$3 + 1];
var eventValue_$8 = evq_$1[i_$3 + 2];
e_$6.locked = true;
e_$6.ready = false;
lockedEvents_$5.push(e_$6);
if (!d_$7.event_called) {
d_$7.event_called = true;
calledDelegates_$4.push(d_$7);
if (d_$7.c && !d_$7.c.__LZdeleted && d_$7.m) {
d_$7.m.call(d_$7.c, eventValue_$8)
}};
i_$3 += 3
};
while (d_$7 = calledDelegates_$4.pop()) {
d_$7.event_called = false
};
while (e_$6 = lockedEvents_$5.pop()) {
e_$6.locked = false;
e_$6.ready = e_$6.delegateList.length != 0
}};
evq_$1.length = 0
};
$lzsc$temp["displayName"] = "__LZdrainDelegatesQueue";
return $lzsc$temp
})()]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzDelegate.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
var name_$1 = Debug.formatToString("%0.48w/<handler", this.c);
var ev0_$2 = this[0];
if (ev0_$2) {
name_$1 += Debug.formatToString(" name='%s'", ev0_$2._dbg_eventName);
if (ev0_$2._dbg_eventSender !== this.c) {
name_$1 += Debug.formatToString(" reference='%w'", ev0_$2._dbg_eventSender)
};
if (Debug.functionName(this.m).indexOf("$") != 0) {
name_$1 += Debug.formatToString(" method='%w'", this.m)
};
if (this[1]) {
name_$1 += " \u2026"
}};
name_$1 += ">";
return name_$1
};
$lzsc$temp["displayName"] = "LzDelegate.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#84/1";
return $lzsc$temp
})()(LzDelegate);
lz.Delegate = LzDelegate;
Class.make("LzEvent", LzDeclaredEventClass, ["delegateList", null, "_dbg_eventSender", void 0, "_dbg_eventName", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (eventSender_$1, eventName_$2, d_$3) {
switch (arguments.length) {
case 2:
d_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var _evs_$4 = eventSender_$1["_events"];
if (_evs_$4 == null) {
eventSender_$1._events = [this]
} else {
_evs_$4.push(this)
};
eventSender_$1[eventName_$2] = this;
if (d_$3) {
this.delegateList = [d_$3];
this.ready = true
} else {
this.delegateList = []
};
this._dbg_eventSender = eventSender_$1;
this._dbg_eventName = eventName_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "locked", false, "addDelegate", (function  () {
var $lzsc$temp = function  (d_$1) {
this.ready = true;
this.delegateList.push(d_$1)
};
$lzsc$temp["displayName"] = "addDelegate";
return $lzsc$temp
})(), "sendEvent", (function  () {
var $lzsc$temp = function  (eventValue_$1) {
switch (arguments.length) {
case 0:
eventValue_$1 = null
};
if (this.locked || !this.ready) {
return
};
this.locked = true;
this.ready = false;
var dlist_$3 = this.delegateList;
var dll_$4 = dlist_$3.length;
var calledDelegates_$5 = new Array();
var d_$6;
for (var i_$7 = dll_$4;i_$7 >= 0;i_$7--) {
d_$6 = dlist_$3[i_$7];
if (d_$6 && d_$6.enabled && !d_$6.event_called) {
d_$6.event_called = true;
calledDelegates_$5.push(d_$6);
var c_$8 = d_$6.c;
if (c_$8 && !c_$8.__LZdeleted) {
if (c_$8.__LZdeferDelegates) {
var evq_$9 = c_$8.__LZdelegatesQueue;
if (!evq_$9) {
evq_$9 = c_$8.__LZdelegatesQueue = new Array()
};
evq_$9.push(this, d_$6, eventValue_$1)
} else {
if (d_$6.m) {
d_$6.m.call(c_$8, eventValue_$1)
}}}}};
while (d_$6 = calledDelegates_$5.pop()) {
d_$6.event_called = false
};
this.locked = false;
this.ready = dlist_$3.length != 0
};
$lzsc$temp["displayName"] = "sendEvent";
return $lzsc$temp
})(), "removeDelegate", (function  () {
var $lzsc$temp = function  (d_$1) {
switch (arguments.length) {
case 0:
d_$1 = null
};
var dlist_$2 = this.delegateList;
var dll_$3 = dlist_$2.length;
for (var i_$4 = 0;i_$4 < dll_$3;i_$4++) {
if (dlist_$2[i_$4] === d_$1) {
dlist_$2.splice(i_$4, 1);
break
}};
this.ready = dlist_$2.length != 0
};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "clearDelegates", (function  () {
var $lzsc$temp = function  () {
var dlist_$1 = this.delegateList;
while (dlist_$1.length) {
dlist_$1[0].unregisterFrom(this)
};
this.ready = false
};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})(), "getDelegateCount", (function  () {
var $lzsc$temp = function  () {
return this.delegateList.length
};
$lzsc$temp["displayName"] = "getDelegateCount";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzEvent"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzEvent.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
return Debug.formatToString("%0.48w/<event name='%s'>", this._dbg_eventSender, this._dbg_eventName)
};
$lzsc$temp["displayName"] = "LzEvent.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#441/1";
return $lzsc$temp
})()(LzEvent);
lz.Event = LzEvent;
Class.make("LzKernelUtils", null, null, ["CSSDimension", (function  () {
var $lzsc$temp = function  (value_$1, units_$2) {
switch (arguments.length) {
case 1:
units_$2 = "px"
};
var result_$3 = value_$1;
if (isNaN(value_$1)) {
if (typeof value_$1 == "string" && value_$1.indexOf("%") == value_$1.length - 1 && !isNaN(value_$1.substring(0, value_$1.length - 1))) {
return value_$1
} else {
result_$3 = 0;
Debug.warn("%w: coerced %w to %w", arguments.callee, value_$1, result_$3)
}} else {
if (value_$1 === Infinity) {
result_$3 = ~0 >>> 1
} else {
if (value_$1 === -Infinity) {
result_$3 = ~(~0 >>> 1)
}}};
return Math.round(result_$3) + units_$2
};
$lzsc$temp["displayName"] = "CSSDimension";
return $lzsc$temp
})()]);
var LzIdleKernel = {__callbacks: [], __update: (function  () {
var $lzsc$temp = function  () {
var kernel_$1 = LzIdleKernel;
var callbacks_$2 = kernel_$1.__callbacks;
var now_$3 = LzTimeKernel.getTimer();
for (var i_$4 = callbacks_$2.length - 2;i_$4 >= 0;i_$4 -= 2) {
var scope_$5 = callbacks_$2[i_$4];
var funcname_$6 = callbacks_$2[i_$4 + 1];
scope_$5[funcname_$6](now_$3)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#15/16";
return $lzsc$temp
})(), __intervalID: null, addCallback: (function  () {
var $lzsc$temp = function  (scope_$1, funcname_$2) {
var kernel_$3 = LzIdleKernel;
var callbacks_$4 = kernel_$3.__callbacks.slice(0);
for (var i_$5 = callbacks_$4.length - 2;i_$5 >= 0;i_$5 -= 2) {
if (callbacks_$4[i_$5] === scope_$1 && callbacks_$4[i_$5 + 1] == funcname_$2) {
return
}};
callbacks_$4.push(scope_$1, funcname_$2);
kernel_$3.__callbacks = callbacks_$4;
if (callbacks_$4.length > 0 && kernel_$3.__intervalID == null) {
kernel_$3.__intervalID = setInterval(LzIdleKernel.__update, 1000 / kernel_$3.__fps)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#29/19";
return $lzsc$temp
})(), removeCallback: (function  () {
var $lzsc$temp = function  (scope_$1, funcname_$2) {
var kernel_$3 = LzIdleKernel;
var callbacks_$4 = kernel_$3.__callbacks.slice(0);
for (var i_$5 = callbacks_$4.length - 2;i_$5 >= 0;i_$5 -= 2) {
if (callbacks_$4[i_$5] === scope_$1 && callbacks_$4[i_$5 + 1] == funcname_$2) {
var removed_$6 = callbacks_$4.splice(i_$5, 2)
}};
kernel_$3.__callbacks = callbacks_$4;
if (callbacks_$4.length == 0 && kernel_$3.__intervalID != null) {
clearInterval(kernel_$3.__intervalID);
kernel_$3.__intervalID = null
};
return removed_$6
};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#45/22";
return $lzsc$temp
})(), __fps: 30, setFrameRate: (function  () {
var $lzsc$temp = function  (fps_$1) {
LzIdleKernel.__fps = fps_$1;
if (LzIdleKernel.__intervalID != null) {
clearInterval(LzIdleKernel.__intervalID);
LzIdleKernel.__intervalID = setInterval(LzIdleKernel.__update, 1000 / fps_$1)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#65/20";
return $lzsc$temp
})()};
Class.make("LzLibraryCleanup", LzNode, ["lib", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$lzc$set_libname", (function  () {
var $lzsc$temp = function  (val_$1) {
this.lib = LzLibrary.findLibrary(val_$1);
this.lib.loadfinished()
};
$lzsc$temp["displayName"] = "$lzc$set_libname";
return $lzsc$temp
})()], ["attributes", new LzInheritedHash(LzNode.attributes)]);
var LzResourceLibrary = {};
var getTimer = (function  () {
var $lzsc$temp = function  () {
return LzTimeKernel.getTimer()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LFC.js#16/20";
return $lzsc$temp
})();
global = window;
lz.BrowserUtils = {__scopeid: 0, __scopes: [], getcallbackstr: (function  () {
var $lzsc$temp = function  (scope_$1, name_$2) {
var sc_$3 = lz.BrowserUtils.__scopeid++;
if (scope_$1.__callbacks == null) {
scope_$1.__callbacks = {sc: sc_$3}} else {
scope_$1.__callbacks[sc_$3] = sc_$3
};
lz.BrowserUtils.__scopes[sc_$3] = scope_$1;
return "if (lz.BrowserUtils.__scopes[" + sc_$3 + "]) lz.BrowserUtils.__scopes[" + sc_$3 + "]." + name_$2 + ".apply(lz.BrowserUtils.__scopes[" + sc_$3 + "], [])"
};
$lzsc$temp["displayName"] = "getcallbackstr";
return $lzsc$temp
})(), getcallbackfunc: (function  () {
var $lzsc$temp = function  (scope_$1, name, args) {
var sc = lz.BrowserUtils.__scopeid++;
if (scope_$1.__callbacks == null) {
scope_$1.__callbacks = {sc: sc}} else {
scope_$1.__callbacks[sc] = sc
};
lz.BrowserUtils.__scopes[sc] = scope_$1;
return (function  () {
var $lzsc$temp = function  () {
var s_$1 = lz.BrowserUtils.__scopes[sc];
if (s_$1) {
return s_$1[name].apply(s_$1, args)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzBrowserUtils.js#45/16";
return $lzsc$temp
})()
};
$lzsc$temp["displayName"] = "getcallbackfunc";
return $lzsc$temp
})(), removecallback: (function  () {
var $lzsc$temp = function  (scope_$1) {
if (scope_$1.__callbacks != null) {
for (var i_$2 in scope_$1.__callbacks) {
var sc_$3 = scope_$1.__callbacks[i_$2];
delete lz.BrowserUtils.__scopes[sc_$3]
};
delete scope_$1.__callbacks
}};
$lzsc$temp["displayName"] = "removecallback";
return $lzsc$temp
})(), hasFeature: (function  () {
var $lzsc$temp = function  (feature_$1, level_$2) {
return document.implementation && document.implementation.hasFeature && document.implementation.hasFeature(feature_$1, level_$2)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzBrowserUtils.js#65/18";
return $lzsc$temp
})()};
var LzPool = (function  () {
var $lzsc$temp = function  (getter_$1, cacheHit_$2, destroyer_$3, owner_$4) {
this.cache = {};
if (typeof getter_$1 == "function") {
this.getter = getter_$1
};
if (typeof cacheHit_$2 == "function") {
this.cacheHit = cacheHit_$2
};
if (typeof destroyer_$3 == "function") {
this.destroyer = destroyer_$3
};
if (owner_$4) {
this.owner = owner_$4
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzPool.js#13/14";
return $lzsc$temp
})();
LzPool.prototype.cache = null;
LzPool.prototype.get = (function  () {
var $lzsc$temp = function  (id_$1, skipcache_$2) {
var args_$3 = Array.prototype.slice.call(arguments, 2);
var itm_$4 = this.cache[id_$1];
if (skipcache_$2 || itm_$4 == null) {
args_$3.unshift(id_$1);
itm_$4 = this.getter.apply(this, args_$3);
if (!skipcache_$2) {
this.cache[id_$1] = itm_$4
}} else {
if (this.cacheHit) {
args_$3.unshift(id_$1, itm_$4);
this.cacheHit.apply(this, args_$3)
}};
if (this.owner) {
itm_$4.owner = this.owner
};
return itm_$4
};
$lzsc$temp["displayName"] = "LzPool.prototype.get";
return $lzsc$temp
})();
LzPool.prototype.flush = (function  () {
var $lzsc$temp = function  (id_$1) {
if (this.destroyer) {
this.destroyer(id_$1, this.cache[id_$1])
};
delete this.cache[id_$1]
};
$lzsc$temp["displayName"] = "LzPool.prototype.flush";
return $lzsc$temp
})();
LzPool.prototype.destroy = (function  () {
var $lzsc$temp = function  () {
for (var id_$1 in this.cache) {
this.flush(id_$1)
};
this.owner = null;
this.cache = null
};
$lzsc$temp["displayName"] = "LzPool.prototype.destroy";
return $lzsc$temp
})();
LzPool.prototype.getter = null;
LzPool.prototype.destroyer = null;
LzPool.prototype.cacheHit = null;
var LzKeyboardKernel = {__downKeysHash: {}, __keyboardEvent: (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event
};
var delta_$2 = {};
var dirty_$3 = false;
var k_$4 = e_$1["keyCode"];
var t_$5 = e_$1.type;
var dh_$6 = LzKeyboardKernel.__downKeysHash;
if (k_$4 >= 0 && k_$4 != 16 && k_$4 != 17 && k_$4 != 18 && k_$4 != 224) {
var s_$7 = String.fromCharCode(k_$4).toLowerCase();
if (t_$5 == "keyup") {
if (dh_$6[s_$7] != null) {
delta_$2[s_$7] = false;
dirty_$3 = true
};
dh_$6[s_$7] = null
} else {
if (t_$5 == "keydown") {
if (dh_$6[s_$7] == null) {
delta_$2[s_$7] = true;
dirty_$3 = true
};
dh_$6[s_$7] = k_$4
}}};
if (LzKeyboardKernel.__updateControlKeys(e_$1, delta_$2)) {
dirty_$3 = true
};
if (dirty_$3) {
var scope_$8 = LzKeyboardKernel.__scope;
var callback_$9 = LzKeyboardKernel.__callback;
if (scope_$8 && scope_$8[callback_$9]) {
scope_$8[callback_$9](delta_$2, k_$4, "on" + t_$5)
}};
if (k_$4 >= 0) {
if (k_$4 == 9) {
e_$1.cancelBubble = true;
return false
} else {
if (LzKeyboardKernel.__cancelKeys && (k_$4 == 13 || k_$4 == 0 || k_$4 == 37 || k_$4 == 38 || k_$4 == 39 || k_$4 == 40 || k_$4 == 8)) {
e_$1.cancelBubble = true;
return false
}}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#16/23";
return $lzsc$temp
})(), __updateControlKeys: (function  () {
var $lzsc$temp = function  (e_$1, delta_$2) {
var quirks_$3 = LzSprite.quirks;
var dh_$4 = LzKeyboardKernel.__downKeysHash;
var dirty_$5 = false;
if (delta_$2) {
var send_$6 = false
} else {
delta_$2 = {};
var send_$6 = true
};
var alt_$7 = e_$1["altKey"];
if (dh_$4["alt"] != null != alt_$7) {
dh_$4["alt"] = alt_$7 ? 18 : null;
delta_$2["alt"] = alt_$7;
dirty_$5 = true;
if (quirks_$3["alt_key_sends_control"]) {
delta_$2["control"] = delta_$2["alt"]
}};
var ctrl_$8 = e_$1["ctrlKey"];
if (dh_$4["control"] != null != ctrl_$8) {
dh_$4["control"] = ctrl_$8 ? 17 : null;
delta_$2["control"] = ctrl_$8;
dirty_$5 = true
};
var shift_$9 = e_$1["shiftKey"];
if (dh_$4["shift"] != null != shift_$9) {
dh_$4["shift"] = shift_$9 ? 16 : null;
delta_$2["shift"] = shift_$9;
dirty_$5 = true
};
if (quirks_$3["hasmetakey"]) {
var meta_$10 = e_$1["metaKey"];
if (dh_$4["meta"] != null != meta_$10) {
dh_$4["meta"] = meta_$10 ? 224 : null;
delta_$2["meta"] = meta_$10;
dirty_$5 = true;
delta_$2["control"] = meta_$10;
if (!meta_$10) {
LzKeyboardKernel.__allKeysUp();
dirty_$5 = false
}}};
if (dirty_$5 && send_$6) {
var scope_$11 = LzKeyboardKernel.__scope;
var callback_$12 = LzKeyboardKernel.__callback;
if (scope_$11 && scope_$11[callback_$12]) {
scope_$11[callback_$12](delta_$2, 0, "on" + e_$1.type)
}};
return dirty_$5
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#71/27";
return $lzsc$temp
})(), __allKeysUp: (function  () {
var $lzsc$temp = function  () {
var delta_$1 = null;
var stuck_$2 = false;
var keys_$3 = null;
var dh_$4 = LzKeyboardKernel.__downKeysHash;
for (var key_$5 in dh_$4) {
if (dh_$4[key_$5] != null) {
stuck_$2 = true;
if (!delta_$1) {
delta_$1 = {}};
delta_$1[key_$5] = false;
if (key_$5.length == 1) {
if (!keys_$3) {
keys_$3 = []
};
keys_$3.push(dh_$4[key_$5])
};
dh_$4[key_$5] = null
}};
var scope_$6 = LzKeyboardKernel.__scope;
var callback_$7 = LzKeyboardKernel.__callback;
if (stuck_$2 && scope_$6 && scope_$6[callback_$7]) {
if (!keys_$3) {
scope_$6[callback_$7](delta_$1, 0, "onkeyup")
} else {
for (var i_$8 = 0, l_$9 = keys_$3.length;i_$8 < l_$9;i_$8++) {
scope_$6[callback_$7](delta_$1, keys_$3[i_$8], "onkeyup")
}}};
LzKeyboardKernel.__downKeysHash = {}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#134/19";
return $lzsc$temp
})(), __callback: null, __scope: null, __cancelKeys: true, __lockFocus: null, setCallback: (function  () {
var $lzsc$temp = function  (scope_$1, keyboardcallback_$2) {
this.__scope = scope_$1;
this.__callback = keyboardcallback_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#167/19";
return $lzsc$temp
})(), setKeyboardControl: (function  () {
var $lzsc$temp = function  (dhtmlKeyboardControl_$1, force_$2) {
if (!force_$2 && LzKeyboardKernel.__lockFocus) {
dhtmlKeyboardControl_$1 = true
};
var handler_$3 = null;
var setcontrol_$4 = lz && lz.embed && lz.embed.options && lz.embed.options.cancelkeyboardcontrol != true || true;
if (setcontrol_$4 && dhtmlKeyboardControl_$1) {
handler_$3 = LzKeyboardKernel.__keyboardEvent
};
if (LzSprite.quirks.keyboardlistentotop) {
var doc_$5 = window.top.document
} else {
var doc_$5 = document
};
doc_$5.onkeydown = handler_$3;
doc_$5.onkeyup = handler_$3;
doc_$5.onkeypress = handler_$3
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#171/26";
return $lzsc$temp
})(), gotLastFocus: (function  () {
var $lzsc$temp = function  () {
if (!LzSprite.__mouseActivationDiv.mouseisover) {
LzKeyboardKernel.setKeyboardControl(false)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#192/20";
return $lzsc$temp
})(), setGlobalFocusTrap: (function  () {
var $lzsc$temp = function  (istrapped_$1) {
LzKeyboardKernel.__lockFocus = istrapped_$1;
if (LzSprite.quirks.activate_on_mouseover) {
var activationdiv_$2 = LzSprite.__mouseActivationDiv;
if (istrapped_$1) {
activationdiv_$2.onmouseover()
} else {
activationdiv_$2.onmouseout()
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#197/26";
return $lzsc$temp
})()};
var LzMouseKernel = {__lastMouseDown: null, __lastMouseOver: null, __x: 0, __y: 0, owner: null, __showncontextmenu: null, __mouseEvent: (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event
};
var target_$2 = e_$1["target"] ? e_$1.target : e_$1["srcElement"];
var eventname_$3 = "on" + e_$1.type;
if (window["LzKeyboardKernel"] && LzKeyboardKernel["__updateControlKeys"]) {
LzKeyboardKernel.__updateControlKeys(e_$1)
};
var lzinputproto_$4 = window["LzInputTextSprite"] && LzInputTextSprite.prototype;
if (lzinputproto_$4 && lzinputproto_$4.__lastshown != null) {
if (LzSprite.quirks.fix_ie_clickable) {
lzinputproto_$4.__hideIfNotFocused(eventname_$3, target_$2)
} else {
if (eventname_$3 != "onmousemove") {
lzinputproto_$4.__hideIfNotFocused()
}}};
if (eventname_$3 == "onmousemove") {
LzMouseKernel.__sendMouseMove(e_$1);
if (lzinputproto_$4 && lzinputproto_$4.__lastshown != null) {
if (target_$2 && target_$2.owner && !(target_$2.owner instanceof LzInputTextSprite)) {
if (!lzinputproto_$4.__lastshown.__isMouseOver()) {
lzinputproto_$4.__lastshown.__hide()
}}}} else {
if (eventname_$3 == "oncontextmenu" || e_$1.button == 2) {
LzMouseKernel.__sendMouseMove(e_$1);
if (LzSprite.prototype.quirks.has_dom2_mouseevents) {
if (eventname_$3 == "oncontextmenu") {
var cmenu_$5 = LzMouseKernel.__findContextMenu(e_$1);
if (cmenu_$5 != null) {
return false
} else {
return true
}} else {
if (eventname_$3 == "onmousedown") {
if (target_$2) {
return LzMouseKernel.__showContextMenu(e_$1)
}}}} else {
if (eventname_$3 == "oncontextmenu") {
if (target_$2) {
return LzMouseKernel.__showContextMenu(e_$1)
}}}} else {
LzMouseKernel.__sendEvent(eventname_$3)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#23/20";
return $lzsc$temp
})(), __sendEvent: (function  () {
var $lzsc$temp = function  (eventname_$1, view_$2) {
if (eventname_$1 == "onclick" || eventname_$1 == "onmousedown" || eventname_$1 == "onmouseup") {
if (LzMouseKernel.__showncontextmenu) {
LzMouseKernel.__showncontextmenu.__hide()
}};
if (eventname_$1 == "onclick" && view_$2 == null) {
return
};
if (LzMouseKernel.__callback) {
LzMouseKernel.__scope[LzMouseKernel.__callback](eventname_$1, view_$2)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#95/19";
return $lzsc$temp
})(), __callback: null, __scope: null, __mouseupEvent: (function  () {
var $lzsc$temp = function  (e_$1) {
if (LzMouseKernel.__lastMouseDown != null) {
LzMouseKernel.__lastMouseDown.__globalmouseup(e_$1)
} else {
if (!e_$1) {
e_$1 = window.event
};
var target_$2 = e_$1["target"] ? e_$1.target : e_$1["srcElement"];
if (target_$2 && target_$2.owner !== LzSprite.__rootSprite) {
return
};
LzMouseKernel.__mouseEvent(e_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#118/22";
return $lzsc$temp
})(), setCallback: (function  () {
var $lzsc$temp = function  (scope_$1, funcname_$2) {
this.__scope = scope_$1;
this.__callback = funcname_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#136/19";
return $lzsc$temp
})(), __mousecontrol: false, setMouseControl: (function  () {
var $lzsc$temp = function  (ison_$1) {
if (ison_$1 == LzMouseKernel.__mousecontrol) {
return
};
LzMouseKernel.__mousecontrol = ison_$1;
if (ison_$1) {
lz.embed.attachEventHandler(document, "mousemove", LzMouseKernel, "__mouseEvent");
lz.embed.attachEventHandler(document, "mousedown", LzMouseKernel, "__mouseEvent");
lz.embed.attachEventHandler(document, "mouseup", LzMouseKernel, "__mouseupEvent");
lz.embed.attachEventHandler(document, "click", LzMouseKernel, "__mouseEvent");
try {
if (window.top != window) {
lz.embed.attachEventHandler(window.top.document, "mouseup", LzMouseKernel, "__mouseupEvent")
}}
catch (e) {

}} else {
lz.embed.removeEventHandler(document, "mousemove", LzMouseKernel, "__mouseEvent");
lz.embed.removeEventHandler(document, "mousedown", LzMouseKernel, "__mouseEvent");
lz.embed.removeEventHandler(document, "mouseup", LzMouseKernel, "__mouseupEvent");
lz.embed.removeEventHandler(document, "click", LzMouseKernel, "__mouseEvent");
try {
if (window.top != window) {
lz.embed.removeEventHandler(window.top.document, "mouseup", LzMouseKernel, "__mouseupEvent")
}}
catch (e) {

}};
document.oncontextmenu = ison_$1 ? LzMouseKernel.__mouseEvent : null
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#142/23";
return $lzsc$temp
})(), __showhand: "pointer", showHandCursor: (function  () {
var $lzsc$temp = function  (show_$1) {
var c_$2 = show_$1 == true ? "pointer" : "default";
this.__showhand = c_$2;
LzMouseKernel.setCursorGlobal(c_$2)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#176/22";
return $lzsc$temp
})(), setCursorGlobal: (function  () {
var $lzsc$temp = function  (n_$1) {
if (LzSprite.quirks.no_cursor_colresize) {
return
};
var n_$1 = LzSprite.__defaultStyles.hyphenate(n_$1);
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv", "cursor", n_$1);
LzSprite.prototype.__setCSSClassProperty(".lzdiv", "cursor", n_$1);
LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv", "cursor", n_$1);
LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv", "cursor", n_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#187/23";
return $lzsc$temp
})(), restoreCursor: (function  () {
var $lzsc$temp = function  () {
if (LzSprite.quirks.no_cursor_colresize) {
return
};
if (LzMouseKernel.__amLocked) {
return
};
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv", "cursor", LzMouseKernel.__showhand);
LzSprite.prototype.__setCSSClassProperty(".lzdiv", "cursor", "default");
LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv", "cursor", "default");
LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv", "cursor", "default")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#204/21";
return $lzsc$temp
})(), lock: (function  () {
var $lzsc$temp = function  () {
LzMouseKernel.__amLocked = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#219/12";
return $lzsc$temp
})(), unlock: (function  () {
var $lzsc$temp = function  () {
LzMouseKernel.__amLocked = false;
LzMouseKernel.restoreCursor()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#227/14";
return $lzsc$temp
})(), disableMouseTemporarily: (function  () {
var $lzsc$temp = function  () {
this.setGlobalClickable(false);
this.__resetonmouseover = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#232/31";
return $lzsc$temp
})(), __resetonmouseover: false, __resetMouse: (function  () {
var $lzsc$temp = function  () {
if (this.__resetonmouseover) {
this.__resetonmouseover = false;
this.setGlobalClickable(true);
var cs_$1 = this.__cachedSelection;
if (cs_$1) {
var sprite_$2 = cs_$1.s;
sprite_$2.setSelection(cs_$1.st, cs_$1.st + cs_$1.sz);
cs_$1 = null
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#237/20";
return $lzsc$temp
})(), __cachedSelection: null, __globalClickable: true, setGlobalClickable: (function  () {
var $lzsc$temp = function  (isclickable_$1) {
var el_$2 = document.getElementById("lzcanvasclickdiv");
this.__globalClickable = isclickable_$1;
el_$2.style.display = isclickable_$1 ? "" : "none"
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#251/26";
return $lzsc$temp
})(), __sendMouseMove: (function  () {
var $lzsc$temp = function  (e_$1, offsetx_$2, offsety_$3) {
if (e_$1.pageX || e_$1.pageY) {
LzMouseKernel.__x = e_$1.pageX;
LzMouseKernel.__y = e_$1.pageY
} else {
if (e_$1.clientX || e_$1.clientY) {
var body_$4 = document.body, docElem_$5 = document.documentElement;
LzMouseKernel.__x = e_$1.clientX + body_$4.scrollLeft + docElem_$5.scrollLeft;
LzMouseKernel.__y = e_$1.clientY + body_$4.scrollTop + docElem_$5.scrollTop
}};
if (offsetx_$2) {
LzMouseKernel.__x += offsetx_$2
};
if (offsety_$3) {
LzMouseKernel.__y += offsety_$3
};
if (e_$1.type == "mousemove") {
LzMouseKernel.__sendEvent("onmousemove")
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#256/23";
return $lzsc$temp
})(), __showContextMenu: (function  () {
var $lzsc$temp = function  (e_$1) {
var cmenu_$2 = LzMouseKernel.__findContextMenu(e_$1);
if (cmenu_$2) {
cmenu_$2.kernel.__show();
return cmenu_$2.kernel.showbuiltins
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#278/25";
return $lzsc$temp
})(), __findContextMenu: (function  () {
var $lzsc$temp = function  (e_$1) {
var cmenu_$2 = LzSprite.__rootSprite.__contextmenu;
var quirks_$3 = LzSprite.quirks;
if (document.elementFromPoint) {
var swf8mode_$4 = quirks_$3.swf8_contextmenu;
var x_$5 = LzMouseKernel.__x;
var y_$6 = LzMouseKernel.__y;
var rootdiv_$7 = LzSprite.__rootSprite.__LZdiv;
var arr_$8 = [];
if (quirks_$3.fix_contextmenu) {
arr_$8.push(rootdiv_$7, rootdiv_$7.style.display);
var rootprevZ_$9 = rootdiv_$7.style.zIndex;
rootdiv_$7.style.zIndex = -1000;
var rootclickdiv_$10 = LzSprite.__rootSprite.__LZclickcontainerdiv;
var clickprevZ_$11 = rootclickdiv_$10.style.zIndex;
arr_$8.push(rootclickdiv_$10, rootclickdiv_$10.style.display);
rootclickdiv_$10.style.zIndex = -9999
};
do{
var elem_$12 = document.elementFromPoint(x_$5, y_$6);
if (!elem_$12) {
break
} else {
var owner_$13 = elem_$12.owner;
if (!owner_$13) {

} else {
if (owner_$13.__contextmenu) {
cmenu_$2 = owner_$13.__contextmenu;
break
} else {
if (quirks_$3.ie_elementfrompoint && owner_$13.scrolldiv === elem_$12) {

} else {
if (swf8mode_$4 && (owner_$13.__LZdiv === elem_$12 && owner_$13.bgcolor != null || owner_$13 instanceof LzTextSprite)) {
break
}}}};
arr_$8.push(elem_$12, elem_$12.style.display);
elem_$12.style.display = "none"
}} while (elem_$12 !== rootdiv_$7 && elem_$12.tagName != "HTML");
for (var i_$14 = arr_$8.length - 1;i_$14 >= 0;i_$14 -= 2) {
arr_$8[i_$14 - 1].style.display = arr_$8[i_$14]
};
if (quirks_$3.fix_contextmenu) {
rootdiv_$7.style.zIndex = rootprevZ_$9;
rootclickdiv_$10.style.zIndex = clickprevZ_$11
}} else {
var sprite_$15 = (e_$1.srcElement || e_$1.target).owner;
if (sprite_$15) {
while (sprite_$15.__parent) {
if (sprite_$15.__contextmenu) {
var mpos_$16 = sprite_$15.getMouse();
if (mpos_$16.x >= 0 && mpos_$16.x < sprite_$15.width && mpos_$16.y >= 0 && mpos_$16.y < sprite_$15.height) {
cmenu_$2 = sprite_$15.__contextmenu;
break
}};
sprite_$15 = sprite_$15.__parent
}}};
return cmenu_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#286/25";
return $lzsc$temp
})()};
Class.make("LzBrowserKernel", null, null, ["loadURL", (function  () {
var $lzsc$temp = function  (url_$1, target_$2, features_$3) {
switch (arguments.length) {
case 1:
target_$2 = null;;case 2:
features_$3 = null
};
if (target_$2 != null) {
if (features_$3 != null) {
window.open(url_$1, target_$2, features_$3)
} else {
window.open(url_$1, target_$2)
}} else {
window.location = url_$1
}};
$lzsc$temp["displayName"] = "loadURL";
return $lzsc$temp
})(), "loadJS", (function  () {
var $lzsc$temp = function  (js_$1, target_$2) {
LzBrowserKernel.loadURL("javascript:" + js_$1 + ";void(0);", target_$2)
};
$lzsc$temp["displayName"] = "loadJS";
return $lzsc$temp
})(), "callJS", (function  () {
var $lzsc$temp = function  (methodname_$1, callback_$2) {
var scope_$3 = null;
var method_$4 = eval(methodname_$1);
var path_$5 = methodname_$1.split(".");
if (path_$5.length > 1) {
path_$5.pop();
scope_$3 = eval(path_$5.join("."))
};
var args_$6 = Array.prototype.slice.call(arguments, 2);
if (method_$4) {
var ret_$7 = method_$4.apply(scope_$3, args_$6)
};
if (callback_$2 && typeof callback_$2 == "function") {
callback_$2(ret_$7)
};
return ret_$7
};
$lzsc$temp["displayName"] = "callJS";
return $lzsc$temp
})(), "setHistory", (function  () {
var $lzsc$temp = function  (n_$1) {
lz.embed.history.set(n_$1)
};
$lzsc$temp["displayName"] = "setHistory";
return $lzsc$temp
})(), "callMethod", (function  () {
var $lzsc$temp = function  (js_$1) {
return eval(js_$1)
};
$lzsc$temp["displayName"] = "callMethod";
return $lzsc$temp
})(), "getVersion", (function  () {
var $lzsc$temp = function  () {
return navigator.userAgent
};
$lzsc$temp["displayName"] = "getVersion";
return $lzsc$temp
})(), "getOS", (function  () {
var $lzsc$temp = function  () {
return navigator.platform
};
$lzsc$temp["displayName"] = "getOS";
return $lzsc$temp
})(), "getLoadURL", (function  () {
var $lzsc$temp = function  () {
if (LzSprite.__rootSprite && LzSprite.__rootSprite._url) {
var url_$1 = LzSprite.__rootSprite._url
} else {
var url_$1 = lz.embed.__propcache.url
};
if (!url_$1) {
url_$1 = new String(window.location)
};
var colon_$2 = url_$1.indexOf(":");
var slash_$3 = url_$1.indexOf("/");
if (colon_$2 > -1) {
if (url_$1.indexOf("://") == colon_$2) {
return url_$1
} else {
if (url_$1.charAt(colon_$2 + 1) == "/") {
url_$1 = url_$1.substring(0, colon_$2 + 1) + "/" + url_$1.substring(colon_$2 + 1);
return url_$1
} else {
var lzu_$4 = new LzURL(new String(window.location));
url_$1 = url_$1.substring(0, colon_$2 + 1) + "/" + lzu_$4.path + url_$1.substring(colon_$2 + 1);
return url_$1
}}} else {
if (slash_$3 == 0) {
return url_$1
} else {
var loc_$5 = new String(window.location);
var lastslash_$6 = loc_$5.lastIndexOf("/");
loc_$5 = loc_$5.substring(0, lastslash_$6 + 1);
return loc_$5 + url_$1
}}};
$lzsc$temp["displayName"] = "getLoadURL";
return $lzsc$temp
})(), "getInitArg", (function  () {
var $lzsc$temp = function  (name_$1) {
return global[name_$1]
};
$lzsc$temp["displayName"] = "getInitArg";
return $lzsc$temp
})(), "getAppID", (function  () {
var $lzsc$temp = function  () {
return LzSprite.__rootSprite._id
};
$lzsc$temp["displayName"] = "getAppID";
return $lzsc$temp
})(), "isAAActive", (function  () {
var $lzsc$temp = function  () {
Debug.warn("LzBrowserKernel.isAAActive not yet fully implemented");
return false
};
$lzsc$temp["displayName"] = "isAAActive";
return $lzsc$temp
})()]);
var LzSprite = (function  () {
var $lzsc$temp = function  (owner_$1, isroot_$2) {
if (owner_$1 == null) {
return
};
this.constructor = arguments.callee;
this.owner = owner_$1;
this.uid = LzSprite.prototype.uid++;
this.aadescriptionDiv = null;
var quirks_$3 = this.quirks;
if (isroot_$2) {
this.isroot = true;
this.__initdone = false;
LzSprite.__rootSprite = this;
var div = document.createElement("div");
div.className = "lzcanvasdiv";
if (quirks_$3.ie6_improve_memory_performance) {
try {
document.execCommand("BackgroundImageCache", false, true)
}
catch (err) {

}};
var p_$4 = lz.embed.__propcache;
var rootcontainer_$5 = LzSprite.__rootSpriteContainer = p_$4.appenddiv;
var appcontainer_$6 = rootcontainer_$5;
rootcontainer_$5.style.margin = 0;
rootcontainer_$5.style.padding = 0;
rootcontainer_$5.style.border = "0 none";
rootcontainer_$5.style.overflow = "hidden";
if (quirks_$3["container_divs_require_overflow"]) {
appcontainer_$6 = document.createElement("div");
appcontainer_$6.className = "lzappoverflow";
rootcontainer_$5.appendChild(appcontainer_$6);
appcontainer_$6.owner = this;
LzSprite.__rootSpriteOverflowContainer = appcontainer_$6
};
if (quirks_$3.fix_contextmenu) {
var cxdiv_$7 = document.createElement("div");
cxdiv_$7.className = "lzcanvascontextdiv";
cxdiv_$7.id = "lzcanvascontextdiv";
appcontainer_$6.appendChild(cxdiv_$7);
cxdiv_$7.owner = this;
this.__LZcontextcontainerdiv = cxdiv_$7
};
if (p_$4.bgcolor) {
div.style.backgroundColor = p_$4.bgcolor;
this.bgcolor = p_$4.bgcolor
};
if (p_$4.id) {
this._id = p_$4.id
};
if (p_$4.url) {
this._url = p_$4.url
};
if (p_$4.cancelkeyboardcontrol) {
lz.embed.options.cancelkeyboardcontrol = p_$4.cancelkeyboardcontrol
};
if (p_$4.serverroot) {
lz.embed.options.serverroot = p_$4.serverroot
};
lz.embed.options.approot = typeof p_$4.approot == "string" ? p_$4.approot : "";
appcontainer_$6.appendChild(div);
this.__LZdiv = div;
if (quirks_$3.fix_clickable) {
var cdiv_$8 = document.createElement("div");
cdiv_$8.className = "lzcanvasclickdiv";
cdiv_$8.id = "lzcanvasclickdiv";
appcontainer_$6.appendChild(cdiv_$8);
this.__LZclickcontainerdiv = cdiv_$8
};
if (quirks_$3["css_hide_canvas_during_init"]) {
var cssname_$9 = "display";
var cssval_$10 = "none";
if (quirks_$3["safari_visibility_instead_of_display"]) {
cssname_$9 = "visibility";
cssval_$10 = "hidden"
};
this.__LZdiv.style[cssname_$9] = cssval_$10;
if (quirks_$3["fix_clickable"]) {
this.__LZclickcontainerdiv.style[cssname_$9] = cssval_$10
};
if (quirks_$3["fix_contextmenu"]) {
this.__LZcontextcontainerdiv.style[cssname_$9] = cssval_$10
}};
if (quirks_$3.activate_on_mouseover) {
div.mouseisover = false;
div.onmouseover = (function  () {
var $lzsc$temp = function  (e_$1) {
if (LzSprite.quirks.focus_on_mouseover) {
if (LzSprite.prototype.getSelectedText() == "") {
div.focus()
}};
if (LzInputTextSprite.prototype.__focusedSprite == null) {
LzKeyboardKernel.setKeyboardControl(true)
};
LzMouseKernel.setMouseControl(true);
this.mouseisover = true
};
$lzsc$temp["displayName"] = "div.onmouseover";
return $lzsc$temp
})();
div.onmouseout = (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event;
var el_$2 = e_$1.toElement
} else {
var el_$2 = e_$1.relatedTarget
};
var quirks_$3 = LzSprite.quirks;
if (quirks_$3.inputtext_anonymous_div) {
try {
el_$2 && el_$2.parentNode
}
catch (e_$1) {
return
}};
var mousein_$4 = false;
if (el_$2) {
var cm_$5 = LzContextMenuKernel.lzcontextmenu;
if (el_$2.owner && el_$2.className.indexOf("lz") == 0) {
mousein_$4 = true
} else {
if (cm_$5 && (el_$2 === cm_$5 || el_$2.parentNode === cm_$5)) {
mousein_$4 = true
}}};
if (mousein_$4) {
var wasClickable_$6 = LzMouseKernel.__globalClickable;
if (quirks_$3.fix_ie_clickable) {
LzMouseKernel.setGlobalClickable(true)
};
if (quirks_$3.focus_on_mouseover) {
if (LzInputTextSprite.prototype.__lastshown == null) {
if (LzSprite.prototype.getSelectedText() == "") {
div.focus()
}}};
LzKeyboardKernel.setKeyboardControl(true);
LzMouseKernel.setMouseControl(true);
LzMouseKernel.__resetMouse();
this.mouseisover = true;
if (quirks_$3.fix_clickable && !wasClickable_$6 && LzMouseKernel.__globalClickable) {
var target_$7 = e_$1["target"] ? e_$1.target : e_$1["srcElement"];
if (target_$7) {
var owner_$8 = target_$7["owner"];
if (LzSprite["$lzsc$isa"] ? LzSprite.$lzsc$isa(owner_$8) : owner_$8 instanceof LzSprite) {
owner_$8 = owner_$8["owner"]
};
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(owner_$8) : owner_$8 instanceof LzView) {
LzMouseKernel.__sendEvent("onmouseout", owner_$8)
}}}} else {
if (quirks_$3.focus_on_mouseover) {
if (LzInputTextSprite.prototype.__lastshown == null) {
if (LzSprite.prototype.getSelectedText() == "") {
div.blur()
}}};
LzKeyboardKernel.setKeyboardControl(false);
LzMouseKernel.setMouseControl(false);
this.mouseisover = false
}};
$lzsc$temp["displayName"] = "div.onmouseout";
return $lzsc$temp
})();
LzSprite.__mouseActivationDiv = div
}} else {
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lzdiv";
if (quirks_$3.fix_clickable) {
this.__LZclickcontainerdiv = document.createElement("div");
this.__LZclickcontainerdiv.className = "lzdiv"
}};
this.__LZdiv.owner = this;
if (quirks_$3.fix_clickable) {
this.__LZclickcontainerdiv.owner = this
};
if (quirks_$3.ie_leak_prevention) {
this.__sprites[this.uid] = this
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#15/16";
return $lzsc$temp
})();
LzSprite.prototype._dbg_typename = "LzSprite";
LzSprite.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
var div_$1 = this.__LZdiv;
var d_$2 = div_$1;
var x_$3 = 0, y_$4 = 0;
if (d_$2.offsetParent) {
do{
x_$3 += d_$2.offsetLeft;
y_$4 += d_$2.offsetTop
} while (d_$2 = d_$2.offsetParent)
};
return Debug.formatToString("%w/@sprite [%s x %s]*[1 0 %s, 0 1 %s, 0 0 1]", this.owner.sprite === this ? this.owner : "(orphan)", div_$1.offsetWidth || 0, div_$1.offsetHeight || 0, x_$3 || 0, y_$4 || 0)
};
$lzsc$temp["displayName"] = "LzSprite.prototype._dbg_name";
return $lzsc$temp
})();
LzSprite.__defaultStyles = {lzdiv: {position: "absolute"}, lzclickdiv: {position: "absolute"}, lzcanvasdiv: {position: "absolute"}, lzcanvasclickdiv: {zIndex: 100000, position: "absolute"}, lzcanvascontextdiv: {position: "absolute"}, lzappoverflow: {position: "absolute", overflow: "hidden"}, lztextcontainer: {position: "absolute", overflow: "hidden", paddingTop: "2px", paddingRight: "2px", paddingBottom: "2px", paddingLeft: "2px", cursor: "default"}, lztextcontainer_click: {position: "absolute", paddingTop: "2px", paddingRight: "2px", paddingBottom: "2px", paddingLeft: "2px", cursor: "default"}, lzinputtextcontainer: {position: "absolute", overflow: "hidden", paddingTop: "0px", paddingRight: "3px", paddingBottom: "4px", paddingLeft: "1px"}, lzinputtextcontainer_click: {position: "absolute", paddingTop: "0px", paddingRight: "3px", paddingBottom: "4px", paddingLeft: "1px"}, lzinputtextmultilinecontainer: {position: "absolute", overflow: "hidden", paddingTop: "1px", paddingRight: "3px", paddingBottom: "3px", paddingLeft: "1px"}, lzinputtextmultilinecontainer_click: {position: "absolute", paddingTop: "1px", paddingRight: "3px", paddingBottom: "3px", paddingLeft: "1px"}, lztext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", whiteSpace: "normal", position: "absolute", overflow: "hidden", textAlign: "left", textIndent: "0px", letterSpacing: "0px", textDecoration: "none"}, lzswftext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", whiteSpace: "normal", position: "absolute", overflow: "hidden", lineHeight: "1.2em", textAlign: "left", textIndent: "0px", letterSpacing: "0.025em", textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all"}, lzinputtext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", textAlign: "left", textIndent: "0px", letterSpacing: "0px", textDecoration: "none"}, lzswfinputtext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", lineHeight: "1.2em", textAlign: "left", textIndent: "0px", letterSpacing: "0.025em", textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all", outline: "none", resize: "none"}, lzswfinputtextmultiline: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", overflow: "hidden", lineHeight: "1.2em", textAlign: "left", textIndent: "0px", letterSpacing: "0.025em", textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all", outline: "none", resize: "none"}, lztextlink: {cursor: "pointer"}, lzaccessibilitydiv: {display: "none"}, lzcontext: {position: "absolute"}, lzimg: {position: "absolute", backgroundRepeat: "no-repeat"}, "#lzcontextmenu div.separator": {borderTop: "1px solid #808080", borderLeft: "none", borderRight: "none", borderBottom: "1px solid #d4d0c8", margin: "7px 0px"}, "#lzTextSizeCache": {zoom: 1}, "#lzcontextmenu a": {color: "#000", display: "block", textDecoration: "none", cursor: "default"}, "#lzcontextmenu a:hover": {color: "#FFF", backgroundColor: "#333"}, "#lzcontextmenu a.disabled": {color: "#999 !important"}, "#lzcontextmenu": {position: "absolute", zIndex: 10000000, backgroundColor: "#CCC", border: "1px outset #999", padding: "4px", fontFamily: "Verdana,Vera,sans-serif", fontSize: "13px", "float": "left", margin: "2px", minWidth: "100px"}, writeCSS: (function  () {
var $lzsc$temp = function  () {
var rules_$1 = [];
var css_$2 = "";
for (var classname_$3 in this) {
if (classname_$3 == "writeCSS" || classname_$3 == "hyphenate" || classname_$3 == "__replace" || classname_$3 == "__re") {
continue
};
css_$2 += classname_$3.indexOf("#") == -1 ? "." : "";
css_$2 += classname_$3 + "{";
for (var n_$4 in this[classname_$3]) {
var v_$5 = this[classname_$3][n_$4];
css_$2 += this.hyphenate(n_$4) + ":" + v_$5 + ";"
};
css_$2 += "}"
};
document.write('<style type="text/css">' + css_$2 + "</style>")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#501/15";
return $lzsc$temp
})(), __re: new RegExp("[A-Z]", "g"), hyphenate: (function  () {
var $lzsc$temp = function  (n_$1) {
return n_$1.replace(this.__re, this.__replace)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#520/16";
return $lzsc$temp
})(), __replace: (function  () {
var $lzsc$temp = function  (found_$1) {
return "-" + found_$1.toLowerCase()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#523/16";
return $lzsc$temp
})()};
LzSprite.prototype.uid = 0;
LzSprite.quirks = {fix_clickable: true, fix_ie_background_height: false, fix_ie_clickable: false, ie_alpha_image_loader: false, ie_leak_prevention: false, prevent_selection: false, ie_elementfrompoint: false, invisible_parent_image_sizing_fix: false, emulate_flash_font_metrics: true, inner_html_strips_newlines: true, inner_html_no_entity_apos: false, css_hide_canvas_during_init: true, firefox_autocomplete_bug: false, hand_pointer_for_clickable: true, alt_key_sends_control: false, safari_textarea_subtract_scrollbar_height: false, no_cursor_colresize: false, safari_visibility_instead_of_display: false, preload_images_only_once: false, absolute_position_accounts_for_offset: false, canvas_div_cannot_be_clipped: false, inputtext_parents_cannot_contain_clip: false, set_height_for_multiline_inputtext: false, ie_opacity: false, text_measurement_use_insertadjacenthtml: false, text_content_use_inner_text: false, text_selection_use_range: false, document_size_use_offsetheight: false, text_ie_carriagereturn: false, ie_paste_event: false, safari_paste_event: false, text_event_charcode: true, keypress_function_keys: true, ie_timer_closure: false, keyboardlistentotop: false, document_size_compute_correct_height: false, ie_mouse_events: false, fix_inputtext_with_parent_resource: false, activate_on_mouseover: true, ie6_improve_memory_performance: false, text_height_includes_padding: false, inputtext_size_includes_margin: false, listen_for_mouseover_out: true, focus_on_mouseover: true, textstyle_on_textdiv: false, textdeco_on_textdiv: false, use_css_sprites: true, preload_images: true, scrollbar_width: 15, inputtext_strips_newlines: false, swf8_contextmenu: true, inputtext_anonymous_div: false, clipped_scrollbar_causes_display_turd: false, hasmetakey: false, textgrabsinputtextfocus: false, input_highlight_bug: false, autoscroll_textarea: false, fix_contextmenu: true, size_blank_to_zero: true, has_dom2_mouseevents: false, container_divs_require_overflow: false};
LzSprite.prototype.capabilities = {rotation: false, scalecanvastopercentage: false, readcanvassizefromsprite: true, opacity: true, colortransform: false, audio: false, accessibility: true, htmlinputtext: false, advancedfonts: false, bitmapcaching: false, persistence: false, clickmasking: false, minimize_opacity_changes: false, history: true, runtimemenus: false, setclipboard: false, proxypolicy: false, linescrolling: false, disableglobalfocustrap: true, allowfullscreen: false, setid: true};
LzSprite.__updateQuirks = (function  () {
var $lzsc$temp = function  () {
var quirks_$1 = LzSprite.quirks;
if (window["lz"] && lz.embed && lz.embed.browser) {
var browser_$2 = lz.embed.browser;
if (browser_$2.isIE) {
if (browser_$2.version < 7) {
quirks_$1["ie_alpha_image_loader"] = true;
quirks_$1["document_size_compute_correct_height"] = true;
quirks_$1["ie6_improve_memory_performance"] = true
} else {
quirks_$1["prevent_selection"] = true;
quirks_$1["invisible_parent_image_sizing_fix"] = true;
if (browser_$2.osversion >= 6) {
quirks_$1["ie_alpha_image_loader"] = true
}};
quirks_$1["ie_opacity"] = true;
quirks_$1["ie_timer_closure"] = true;
quirks_$1["ie_leak_prevention"] = true;
quirks_$1["fix_ie_clickable"] = true;
quirks_$1["fix_ie_background_height"] = true;
quirks_$1["inner_html_no_entity_apos"] = true;
LzSprite.prototype.capabilities["minimize_opacity_changes"] = true;
quirks_$1["set_height_for_multiline_inputtext"] = true;
quirks_$1["text_measurement_use_insertadjacenthtml"] = true;
quirks_$1["text_content_use_inner_text"] = true;
quirks_$1["text_selection_use_range"] = true;
quirks_$1["text_ie_carriagereturn"] = true;
quirks_$1["ie_paste_event"] = true;
quirks_$1["keypress_function_keys"] = false;
quirks_$1["text_event_charcode"] = false;
quirks_$1["ie_mouse_events"] = true;
quirks_$1["fix_inputtext_with_parent_resource"] = true;
quirks_$1["inputtext_size_includes_margin"] = true;
quirks_$1["focus_on_mouseover"] = false;
quirks_$1["textstyle_on_textdiv"] = true;
quirks_$1["scrollbar_width"] = 17;
quirks_$1["use_css_sprites"] = !quirks_$1["ie_alpha_image_loader"];
quirks_$1["textgrabsinputtextfocus"] = true;
quirks_$1["ie_elementfrompoint"] = true
} else {
if (browser_$2.isSafari) {
quirks_$1["alt_key_sends_control"] = true;
quirks_$1["safari_visibility_instead_of_display"] = true;
quirks_$1["absolute_position_accounts_for_offset"] = true;
if (browser_$2.version < 525.18) {
quirks_$1["canvas_div_cannot_be_clipped"] = true;
quirks_$1["invisible_parent_image_sizing_fix"] = true;
quirks_$1["safari_textarea_subtract_scrollbar_height"] = true
};
quirks_$1["document_size_use_offsetheight"] = true;
if (browser_$2.version > 523.1) {
LzSprite.prototype.capabilities["rotation"] = true;
LzSprite.__defaultStyles.lzdiv.WebkitTransformOrigin = "0 0"
};
quirks_$1["safari_paste_event"] = true;
quirks_$1["keypress_function_keys"] = false;
if (browser_$2.version < 523.15) {
quirks_$1["keyboardlistentotop"] = true
};
if (browser_$2.version >= 530.19) {
LzSprite.prototype.capabilities["threedtransform"] = true
};
if (browser_$2.isIphone) {
quirks_$1["canvas_div_cannot_be_clipped"] = true
};
quirks_$1["inputtext_strips_newlines"] = true;
quirks_$1["prevent_selection"] = true;
quirks_$1["container_divs_require_overflow"] = true
} else {
if (browser_$2.isOpera) {
quirks_$1["invisible_parent_image_sizing_fix"] = true;
quirks_$1["no_cursor_colresize"] = true;
quirks_$1["absolute_position_accounts_for_offset"] = true;
quirks_$1["canvas_div_cannot_be_clipped"] = true;
quirks_$1["document_size_use_offsetheight"] = true;
quirks_$1["text_event_charcode"] = false;
quirks_$1["textdeco_on_textdiv"] = true;
quirks_$1["text_ie_carriagereturn"] = true
} else {
if (browser_$2.isFirefox) {
quirks_$1["inputtext_anonymous_div"] = true;
if (browser_$2.OS == "Windows") {
quirks_$1["clipped_scrollbar_causes_display_turd"] = true;
quirks_$1["input_highlight_bug"] = true
};
if (browser_$2.version < 2) {
quirks_$1["firefox_autocomplete_bug"] = true
} else {
if (browser_$2.version < 3) {
LzSprite.__defaultStyles.lzswftext.lineHeight = "119%";
LzSprite.__defaultStyles.lzswfinputtext.lineHeight = "119%";
LzSprite.__defaultStyles.lzswfinputtextmultiline.lineHeight = "119%"
} else {
if (browser_$2.version < 4) {
if (browser_$2.subversion < 6) {
quirks_$1["text_height_includes_padding"] = true
};
if (browser_$2.version < 3.5) {
quirks_$1["container_divs_require_overflow"] = true
}}}};
quirks_$1["autoscroll_textarea"] = true;
if (browser_$2.version >= 3.5) {
LzSprite.prototype.capabilities["rotation"] = true;
LzSprite.__defaultStyles.lzdiv.MozTransformOrigin = "0 0"
}}}}};
if (browser_$2.OS == "Mac") {
quirks_$1["detectstuckkeys"] = true
};
var defaultStyles_$3 = LzSprite.__defaultStyles;
var tc_$4 = defaultStyles_$3.lztextcontainer;
var itc_$5 = defaultStyles_$3.lzinputtextcontainer;
var itmc_$6 = defaultStyles_$3.lzinputtextmultilinecontainer;
if (quirks_$1["hand_pointer_for_clickable"]) {
defaultStyles_$3.lzclickdiv.cursor = "pointer"
};
if (quirks_$1["inner_html_strips_newlines"] == true) {
LzSprite.prototype.inner_html_strips_newlines_re = RegExp("$", "mg")
};
if (browser_$2.isFirefox) {
defaultStyles_$3.lzimg["MozUserSelect"] = "none"
} else {
if (browser_$2.isSafari) {
defaultStyles_$3.lzimg["WebkitUserSelect"] = "none"
} else {
defaultStyles_$3.lzimg["UserSelect"] = "none"
}};
LzSprite.prototype.br_to_newline_re = RegExp("<br/>", "mg");
if (lz.BrowserUtils.hasFeature("mouseevents", "2.0")) {
quirks_$1["has_dom2_mouseevents"] = true
}};
LzSprite.prototype.quirks = quirks_$1
};
$lzsc$temp["displayName"] = "LzSprite.__updateQuirks";
return $lzsc$temp
})();
LzSprite.__updateQuirks();
LzSprite.__defaultStyles.writeCSS();
LzSprite.setRootX = (function  () {
var $lzsc$temp = function  (v_$1) {
var rootcontainer_$2 = LzSprite.__rootSpriteContainer;
rootcontainer_$2.style.position = "absolute";
rootcontainer_$2.style.left = LzSprite.prototype.CSSDimension(v_$1);
LzScreenKernel.__resizeEvent()
};
$lzsc$temp["displayName"] = "LzSprite.setRootX";
return $lzsc$temp
})();
LzSprite.setRootWidth = (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.__rootSpriteContainer.style.width = LzSprite.prototype.CSSDimension(v_$1);
LzScreenKernel.__resizeEvent()
};
$lzsc$temp["displayName"] = "LzSprite.setRootWidth";
return $lzsc$temp
})();
LzSprite.setRootY = (function  () {
var $lzsc$temp = function  (v_$1) {
var rootcontainer_$2 = LzSprite.__rootSpriteContainer;
rootcontainer_$2.style.position = "absolute";
rootcontainer_$2.style.top = LzSprite.prototype.CSSDimension(v_$1);
LzScreenKernel.__resizeEvent()
};
$lzsc$temp["displayName"] = "LzSprite.setRootY";
return $lzsc$temp
})();
LzSprite.setRootHeight = (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.__rootSpriteContainer.style.height = LzSprite.prototype.CSSDimension(v_$1);
LzScreenKernel.__resizeEvent()
};
$lzsc$temp["displayName"] = "LzSprite.setRootHeight";
return $lzsc$temp
})();
LzSprite.prototype.__LZdiv = null;
LzSprite.prototype.__LZimg = null;
LzSprite.prototype.__LZclick = null;
LzSprite.prototype.x = null;
LzSprite.prototype.y = null;
LzSprite.prototype.opacity = null;
LzSprite.prototype.width = null;
LzSprite.prototype.height = null;
LzSprite.prototype.playing = false;
LzSprite.prototype.clickable = false;
LzSprite.prototype.frame = 1;
LzSprite.prototype.frames = null;
LzSprite.prototype.blankimage = "lps/includes/blank.gif";
LzSprite.prototype.resource = null;
LzSprite.prototype.source = null;
LzSprite.prototype.visible = null;
LzSprite.prototype.text = null;
LzSprite.prototype.clip = null;
LzSprite.prototype.stretches = null;
LzSprite.prototype.resourceWidth = null;
LzSprite.prototype.resourceHeight = null;
LzSprite.prototype.cursor = null;
LzSprite.prototype._w = "0pt";
LzSprite.prototype._h = "0pt";
LzSprite.prototype.__LZcontext = null;
LzSprite.prototype.init = (function  () {
var $lzsc$temp = function  (v_$1) {
this.setVisible(v_$1);
if (this.isroot) {
if (this.quirks["css_hide_canvas_during_init"]) {
var cssname_$2 = "display";
if (this.quirks["safari_visibility_instead_of_display"]) {
cssname_$2 = "visibility"
};
this.__LZdiv.style[cssname_$2] = "";
if (this.quirks["fix_clickable"]) {
this.__LZclickcontainerdiv.style[cssname_$2] = ""
};
if (this.quirks["fix_contextmenu"]) {
this.__LZcontextcontainerdiv.style[cssname_$2] = ""
}};
if (this._id) {
lz.embed[this._id]._ready(this.owner)
};
this.__initdone = true
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.init";
return $lzsc$temp
})();
LzSprite.prototype.__topZ = 1;
LzSprite.prototype.__parent = null;
LzSprite.prototype.__children = null;
LzSprite.prototype.addChildSprite = (function  () {
var $lzsc$temp = function  (sprite_$1) {
if (sprite_$1.__parent != null) {
return
};
if (this.stretches != null && this.__warnstretches != true) {
Debug.warn("Due to limitations in the DHTML runtime, stretches will only apply to resources in this view, and doesn't affect child views.");
this.__warnstretches = true
};
if (this.color != null && this.__warncolorcascade != true) {
Debug.warn("Due to limitations in the DHTML runtime, color will only apply to resources in this view, and doesn't affect child views.");
this.__warncolorcascade = true
};
sprite_$1.__parent = this;
if (this.__children) {
this.__children.push(sprite_$1)
} else {
this.__children = [sprite_$1]
};
this.__LZdiv.appendChild(sprite_$1.__LZdiv);
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv.appendChild(sprite_$1.__LZclickcontainerdiv)
};
sprite_$1.__setZ(++this.__topZ)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.addChildSprite";
return $lzsc$temp
})();
LzSprite.prototype.setResource = (function  () {
var $lzsc$temp = function  (r_$1) {
if (this.resource == r_$1) {
return
};
this.resource = r_$1;
if (r_$1.indexOf("http:") == 0 || r_$1.indexOf("https:") == 0) {
this.skiponload = false;
this.setSource(r_$1);
return
};
var res_$2 = LzResourceLibrary[r_$1];
if (res_$2) {
this.resourceWidth = res_$2.width;
this.resourceHeight = res_$2.height;
if (this.quirks.use_css_sprites && res_$2.sprite) {
var baseurl_$3 = this.getBaseUrl(res_$2);
this.__csssprite = baseurl_$3 + res_$2.sprite
} else {
this.__csssprite = null;
if (this.__bgimage) {
this.__setBGImage(null)
}}};
var urls_$4 = this.getResourceUrls(r_$1);
this.owner.resourceevent("totalframes", urls_$4.length);
this.frames = urls_$4;
if (this.quirks.preload_images && !(this.stretches == null && this.__csssprite)) {
this.__preloadFrames()
};
this.skiponload = true;
this.setSource(urls_$4[0], true)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setResource";
return $lzsc$temp
})();
LzSprite.prototype.getResourceUrls = (function  () {
var $lzsc$temp = function  (resourcename_$1) {
var urls_$2 = [];
var res_$3 = LzResourceLibrary[resourcename_$1];
if (!res_$3) {
Debug.warn("Could not find resource named %#s", resourcename_$1);
return urls_$2
};
var baseurl_$4 = this.getBaseUrl(res_$3);
for (var i_$5 = 0;i_$5 < res_$3.frames.length;i_$5++) {
urls_$2[i_$5] = baseurl_$4 + res_$3.frames[i_$5]
};
return urls_$2
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getResourceUrls";
return $lzsc$temp
})();
LzSprite.prototype.getBaseUrl = (function  () {
var $lzsc$temp = function  (resource_$1) {
if (resource_$1.ptype == "sr") {
return lz.embed.options.serverroot
} else {
return lz.embed.options.approot
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.getBaseUrl";
return $lzsc$temp
})();
LzSprite.prototype.CSSDimension = LzKernelUtils.CSSDimension;
LzSprite.prototype.loading = false;
LzSprite.prototype.setSource = (function  () {
var $lzsc$temp = function  (url_$1, usecache_$2) {
if (url_$1 == null || url_$1 == "null") {
this.unload();
return
};
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && url_$1 != null) {
this.__sizedtozero = false;
this.__LZdiv.style.width = this._w;
this.__LZdiv.style.height = this._h
}};
if (usecache_$2 != true) {
this.skiponload = false;
this.resource = url_$1;
if (this.playing) {
this.stop()
};
this.__updateLoadStatus(0);
this.__csssprite = null;
if (this.__bgimage) {
this.__setBGImage(null)
}};
if (usecache_$2 == "memorycache") {
usecache_$2 = true
};
if (this.loading) {
if (this.__ImgPool && this.source) {
this.__ImgPool.flush(this.source)
};
this.__destroyImage(null, this.__LZimg);
this.__LZimg = null
};
this.source = url_$1;
if (this.stretches == null && this.__csssprite) {
if (!this.__LZimg) {
var im_$3 = document.createElement("img");
im_$3.className = "lzimg";
im_$3.owner = this;
im_$3.src = lz.embed.options.serverroot + LzSprite.prototype.blankimage;
this.__bindImage(im_$3)
};
this.__updateStretches();
var imgurl_$4 = this.__csssprite ? this.__csssprite : url_$1;
this.__setBGImage(imgurl_$4);
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: this.skiponload});
return
};
if (!this.quirks.preload_images) {
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: this.skiponload})
};
this.loading = true;
if (!this.__ImgPool) {
this.__ImgPool = new LzPool(LzSprite.prototype.__getImage, LzSprite.prototype.__gotImage, LzSprite.prototype.__destroyImage, this)
};
var im_$3 = this.__ImgPool.get(url_$1, usecache_$2 != true);
this.__bindImage(im_$3);
if (this.loading) {
if (this.skiponload && this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(im_$3)
}} else {
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(im_$3)
} else {
if (this.stretches) {
this.__updateStretches()
}}};
if (this.clickable) {
this.setClickable(true)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setSource";
return $lzsc$temp
})();
LzSprite.prototype.__bindImage = (function  () {
var $lzsc$temp = function  (im_$1) {
if (this.__LZimg && this.__LZimg.owner) {
this.__LZdiv.replaceChild(im_$1, this.__LZimg);
this.__LZimg = im_$1
} else {
this.__LZimg = im_$1;
this.__LZdiv.appendChild(this.__LZimg)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__bindImage";
return $lzsc$temp
})();
LzSprite.prototype.__setBGImage = (function  () {
var $lzsc$temp = function  (url_$1) {
var bgurl_$2 = url_$1 ? "url('" + url_$1 + "')" : null;
this.__bgimage = this.__LZimg.style.backgroundImage = bgurl_$2
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__setBGImage";
return $lzsc$temp
})();
if (LzSprite.quirks.ie_alpha_image_loader) {
LzSprite.prototype.__updateIEAlpha = (function  () {
var $lzsc$temp = function  (who_$1) {
var w_$2 = this.resourceWidth;
var h_$3 = this.resourceHeight;
if (this.stretches == "both") {
w_$2 = "100%";
h_$3 = "100%"
} else {
if (this.stretches == "width") {
w_$2 = "100%"
} else {
if (this.stretches == "height") {
h_$3 = "100%"
}}};
if (w_$2 == null) {
w_$2 = this.width == null ? "100%" : this.CSSDimension(this.width)
};
if (h_$3 == null) {
h_$3 = this.height == null ? "100%" : this.CSSDimension(this.height)
};
who_$1.style.width = w_$2;
who_$1.style.height = h_$3
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__updateIEAlpha";
return $lzsc$temp
})()
};
LzSprite.prototype.setClickable = (function  () {
var $lzsc$temp = function  (c_$1) {
c_$1 = c_$1 == true;
if (this.clickable == c_$1) {
return
};
if (this.__LZimg != null) {
if (this.__LZdiv._clickable) {
this.__setClickable(false, this.__LZdiv)
};
if (!this.__LZclick) {
if (this.quirks.fix_ie_clickable) {
this.__LZclick = document.createElement("img");
this.__LZclick.src = lz.embed.options.serverroot + LzSprite.prototype.blankimage
} else {
this.__LZclick = document.createElement("div")
};
this.__LZclick.owner = this;
this.__LZclick.className = "lzclickdiv";
this.__LZclick.style.width = this._w;
this.__LZclick.style.height = this._h;
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv.appendChild(this.__LZclick)
} else {
this.__LZdiv.appendChild(this.__LZclick)
}};
this.__setClickable(c_$1, this.__LZclick);
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable) {
this.__LZclickcontainerdiv.style.display = c_$1 && this.visible ? "" : "none";
this.__LZclick.style.display = c_$1 && this.visible ? "" : "none"
} else {
this.__LZclick.style.display = c_$1 ? "" : "none"
}}} else {
if (this.quirks.fix_clickable) {
if (!this.__LZclick) {
if (this.quirks.fix_ie_clickable) {
this.__LZclick = document.createElement("img");
this.__LZclick.src = lz.embed.options.serverroot + LzSprite.prototype.blankimage
} else {
this.__LZclick = document.createElement("div")
};
this.__LZclick.owner = this;
this.__LZclick.className = "lzclickdiv";
this.__LZclick.style.width = this._w;
this.__LZclick.style.height = this._h;
this.__LZclickcontainerdiv.appendChild(this.__LZclick)
};
this.__setClickable(c_$1, this.__LZclick);
if (this.quirks.fix_ie_clickable) {
this.__LZclick.style.display = c_$1 && this.visible ? "" : "none"
} else {
this.__LZclick.style.display = c_$1 ? "" : "none"
}} else {
this.__setClickable(c_$1, this.__LZdiv)
}};
this.clickable = c_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setClickable";
return $lzsc$temp
})();
LzSprite.prototype.__setClickable = (function  () {
var $lzsc$temp = function  (c_$1, div_$2) {
if (div_$2._clickable == c_$1) {
return
};
div_$2._clickable = c_$1;
var f_$3 = c_$1 ? LzSprite.prototype.__clickDispatcher : null;
div_$2.onclick = f_$3;
div_$2.onmousedown = f_$3;
div_$2.onmouseup = f_$3;
div_$2.onmousemove = f_$3;
if (this.quirks.ie_mouse_events) {
div_$2.ondrag = f_$3;
div_$2.ondblclick = f_$3;
div_$2.onmouseover = f_$3;
div_$2.onmouseout = f_$3
} else {
if (this.quirks.listen_for_mouseover_out) {
div_$2.onmouseover = f_$3;
div_$2.onmouseout = f_$3
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__setClickable";
return $lzsc$temp
})();
LzSprite.prototype.__clickDispatcher = (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event
};
if (e_$1.button == 2) {
return false
};
this.owner.__mouseEvent(e_$1);
return false
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__clickDispatcher";
return $lzsc$temp
})();
LzSprite.prototype.__mouseisdown = false;
LzSprite.prototype.__mouseEvent = (function  () {
var $lzsc$temp = function  (e_$1, artificial_$2) {
if (artificial_$2) {
var eventname_$3 = e_$1;
e_$1 = {}} else {
var eventname_$3 = "on" + e_$1.type;
if (LzKeyboardKernel && LzKeyboardKernel["__updateControlKeys"]) {
LzKeyboardKernel.__updateControlKeys(e_$1);
if (LzKeyboardKernel.__cancelKeys) {
e_$1.cancelBubble = true
}}};
if (this.quirks.ie_mouse_events) {
if (eventname_$3 == "onmouseenter") {
eventname_$3 = "onmouseover"
} else {
if (eventname_$3 == "onmouseleave") {
eventname_$3 = "onmouseout"
} else {
if (eventname_$3 == "ondblclick") {
this.__mouseEvent("onmousedown", true);
this.__mouseEvent("onmouseup", true);
this.__mouseEvent("onclick", true);
return
} else {
if (eventname_$3 == "ondrag") {
return
}}}}};
LzMouseKernel.__sendMouseMove(e_$1);
if (eventname_$3 == "onmousemove") {
return
} else {
if (eventname_$3 == "onmousedown") {
this.__mouseisdown = true;
LzMouseKernel.__lastMouseDown = this
} else {
if (eventname_$3 == "onmouseup") {
e_$1.cancelBubble = false;
if (LzMouseKernel.__lastMouseDown !== this) {
return
} else {
if (this.quirks.ie_mouse_events) {
if (this.__isMouseOver()) {
this.__mouseisdown = false
}} else {
this.__mouseisdown = false
};
if (this.__mouseisdown == false) {
LzMouseKernel.__lastMouseDown = null
}}} else {
if (eventname_$3 == "onmouseupoutside") {
this.__mouseisdown = false
} else {
if (eventname_$3 == "onmouseover") {
LzMouseKernel.__lastMouseOver = this;
if (this.quirks.activate_on_mouseover) {
var activationdiv_$4 = LzSprite.__mouseActivationDiv;
if (!activationdiv_$4.mouseisover) {
activationdiv_$4.onmouseover()
}}}}}}};
if (this.owner.mouseevent) {
if (LzMouseKernel.__lastMouseDown) {
if (eventname_$3 == "onmouseover" || eventname_$3 == "onmouseout") {
var sendevents_$5 = false;
if (this.quirks.ie_mouse_events) {
var over_$6 = this.__isMouseOver();
if (over_$6 && eventname_$3 == "onmouseover" || !over_$6 && eventname_$3 == "onmouseout") {
sendevents_$5 = true
}} else {
if (LzMouseKernel.__lastMouseDown === this) {
sendevents_$5 = true
}};
if (eventname_$3 == "onmouseover") {
LzMouseKernel.__lastMouseOver = this
} else {
if (sendevents_$5 && LzMouseKernel.__lastMouseOver === this) {
LzMouseKernel.__lastMouseOver = null
}};
if (sendevents_$5) {
LzMouseKernel.__sendEvent(eventname_$3, this.owner);
var dragname_$7 = eventname_$3 == "onmouseover" ? "onmousedragin" : "onmousedragout";
LzMouseKernel.__sendEvent(dragname_$7, this.owner)
};
return
}};
if (this.quirks.fix_clickable && !LzMouseKernel.__globalClickable) {
if (eventname_$3 == "onmouseout" || eventname_$3 == "onmouseover") {
return
}};
LzMouseKernel.__sendEvent(eventname_$3, this.owner)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__mouseEvent";
return $lzsc$temp
})();
LzSprite.prototype.__isMouseOver = (function  () {
var $lzsc$temp = function  (e_$1) {
var p_$2 = this.getMouse();
var visible_$3 = this.__findParents("visible", false);
if (visible_$3.length) {
return false
};
return p_$2.x >= 0 && p_$2.y >= 0 && p_$2.x < this.width && p_$2.y < this.height
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__isMouseOver";
return $lzsc$temp
})();
LzSprite.prototype.__globalmouseup = (function  () {
var $lzsc$temp = function  (e_$1) {
if (this.__mouseisdown) {
if (!this.quirks.ie_mouse_events) {
this.__mouseEvent(e_$1)
};
this.__mouseEvent("onmouseupoutside", true)
};
LzMouseKernel.__lastMouseDown = null;
if (LzMouseKernel.__lastMouseOver) {
LzMouseKernel.__lastMouseOver.__mouseEvent("onmouseover", true);
LzMouseKernel.__lastMouseOver = null
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__globalmouseup";
return $lzsc$temp
})();
LzSprite.prototype.setX = (function  () {
var $lzsc$temp = function  (x_$1) {
if (x_$1 == null || x_$1 == this.x) {
return
};
this.__poscacheid = -1;
this.x = x_$1;
x_$1 = this.CSSDimension(x_$1);
if (this._x != x_$1) {
this._x = x_$1;
this.__LZdiv.style.left = x_$1;
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv.style.left = x_$1
};
if (this.quirks.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.left = x_$1
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setX";
return $lzsc$temp
})();
LzSprite.prototype.setWidth = (function  () {
var $lzsc$temp = function  (w_$1) {
if (w_$1 == null || w_$1 < 0 || this.width == w_$1) {
return
};
this.width = w_$1;
w_$1 = this.CSSDimension(w_$1);
if (this._w != w_$1) {
this._w = w_$1;
var size_$2 = w_$1;
var quirks_$3 = this.quirks;
if (quirks_$3.size_blank_to_zero) {
if (this.bgcolor == null && this.source == null && !this.clip && !(this instanceof LzTextSprite)) {
this.__sizedtozero = true;
size_$2 = "0px"
}};
this.__LZdiv.style.width = size_$2;
if (this.clip) {
this.__updateClip()
};
if (this.stretches) {
this.__updateStretches()
};
if (this.__LZclick) {
this.__LZclick.style.width = w_$1
};
if (this.__LZcontext) {
this.__LZcontext.style.width = w_$1
};
if (this.isroot && quirks_$3.container_divs_require_overflow) {
LzSprite.__rootSpriteOverflowContainer.style.width = w_$1
};
return w_$1
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setWidth";
return $lzsc$temp
})();
LzSprite.prototype.setY = (function  () {
var $lzsc$temp = function  (y_$1) {
if (y_$1 == null || y_$1 == this.y) {
return
};
this.__poscacheid = -1;
this.y = y_$1;
y_$1 = this.CSSDimension(y_$1);
if (this._y != y_$1) {
this._y = y_$1;
this.__LZdiv.style.top = y_$1;
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv.style.top = y_$1
};
if (this.quirks.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.top = y_$1
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setY";
return $lzsc$temp
})();
LzSprite.prototype.setHeight = (function  () {
var $lzsc$temp = function  (h_$1) {
if (h_$1 == null || h_$1 < 0 || this.height == h_$1) {
return
};
this.height = h_$1;
h_$1 = this.CSSDimension(h_$1);
if (this._h != h_$1) {
this._h = h_$1;
var size_$2 = h_$1;
var quirks_$3 = this.quirks;
if (quirks_$3.size_blank_to_zero) {
if (this.bgcolor == null && this.source == null && !this.clip && !(this instanceof LzTextSprite)) {
this.__sizedtozero = true;
size_$2 = "0px"
}};
this.__LZdiv.style.height = size_$2;
if (this.clip) {
this.__updateClip()
};
if (this.stretches) {
this.__updateStretches()
};
if (this.__LZclick) {
this.__LZclick.style.height = h_$1
};
if (this.__LZcontext) {
this.__LZcontext.style.height = h_$1
};
if (this.isroot && quirks_$3.container_divs_require_overflow) {
LzSprite.__rootSpriteOverflowContainer.style.height = h_$1
};
return h_$1
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setHeight";
return $lzsc$temp
})();
LzSprite.prototype.setMaxLength = (function  () {
var $lzsc$temp = function  (v_$1) {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.setMaxLength";
return $lzsc$temp
})();
LzSprite.prototype.setPattern = (function  () {
var $lzsc$temp = function  (v_$1) {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.setPattern";
return $lzsc$temp
})();
LzSprite.prototype.setVisible = (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.visible == v_$1) {
return
};
this.visible = v_$1;
this.__LZdiv.style.display = v_$1 && this.opacity != 0 ? "" : "none";
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable && this.__LZclick) {
this.__LZclick.style.display = v_$1 && this.clickable ? "" : "none"
};
var vis_$2 = v_$1 ? "" : "none";
this.__LZclickcontainerdiv.style.display = vis_$2;
if (this.quirks.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.display = vis_$2
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setVisible";
return $lzsc$temp
})();
LzSprite.prototype.setColor = (function  () {
var $lzsc$temp = function  (c_$1) {
if (this.color == c_$1) {
return
};
this.color = c_$1;
this.__LZdiv.style.color = LzColorUtils.inttohex(c_$1)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setColor";
return $lzsc$temp
})();
LzSprite.prototype.setBGColor = (function  () {
var $lzsc$temp = function  (c_$1) {
if (this.bgcolor == c_$1) {
return
};
this.bgcolor = c_$1;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && c_$1 != null) {
this.__sizedtozero = false;
this.__LZdiv.style.width = this._w;
this.__LZdiv.style.height = this._h
}};
this.__LZdiv.style.backgroundColor = c_$1 == null ? "transparent" : LzColorUtils.inttohex(c_$1);
if (this.quirks.fix_ie_background_height) {
if (this.height != null && this.height < 2) {
this.setSource(lz.embed.options.serverroot + LzSprite.prototype.blankimage, true)
} else {
if (!this._fontSize) {
this.__LZdiv.style.fontSize = "0px"
}}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setBGColor";
return $lzsc$temp
})();
LzSprite.prototype.setOpacity = (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.opacity == o_$1 || o_$1 < 0) {
return
};
this.opacity = o_$1;
var factor_$2 = 100;
if (this.capabilities.minimize_opacity_changes) {
factor_$2 = 10
};
o_$1 = parseInt(o_$1 * factor_$2) / factor_$2;
if (o_$1 != this._opacity) {
this._opacity = o_$1;
this.__LZdiv.style.display = this.visible && o_$1 != 0 ? "" : "none";
if (this.quirks.ie_opacity) {
if (o_$1 == 1) {
this.__LZdiv.style.filter = ""
} else {
this.__LZdiv.style.filter = "alpha(opacity=" + parseInt(o_$1 * 100) + ")"
}} else {
if (o_$1 == 1) {
this.__LZdiv.style.opacity = ""
} else {
this.__LZdiv.style.opacity = o_$1
}}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setOpacity";
return $lzsc$temp
})();
LzSprite.prototype.play = (function  () {
var $lzsc$temp = function  (f_$1) {
if (!this.frames || this.frames.length < 2) {
return
};
f_$1 = parseInt(f_$1);
if (!isNaN(f_$1)) {
this.__setFrame(f_$1)
};
if (this.playing == true) {
return
};
this.playing = true;
this.owner.resourceevent("play", null, true);
LzIdleKernel.addCallback(this, "__incrementFrame")
};
$lzsc$temp["displayName"] = "LzSprite.prototype.play";
return $lzsc$temp
})();
LzSprite.prototype.stop = (function  () {
var $lzsc$temp = function  (f_$1) {
if (!this.frames || this.frames.length < 2) {
return
};
if (this.playing == true) {
this.playing = false;
this.owner.resourceevent("stop", null, true);
LzIdleKernel.removeCallback(this, "__incrementFrame")
};
f_$1 = parseInt(f_$1);
if (!isNaN(f_$1)) {
this.__setFrame(f_$1)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.stop";
return $lzsc$temp
})();
LzSprite.prototype.__incrementFrame = (function  () {
var $lzsc$temp = function  () {
var newframe_$1 = this.frame + 1 > this.frames.length ? 1 : this.frame + 1;
this.__setFrame(newframe_$1)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__incrementFrame";
return $lzsc$temp
})();
if (LzSprite.quirks.preload_images_only_once) {
LzSprite.prototype.__preloadurls = {}};
LzSprite.prototype.__preloadFrames = (function  () {
var $lzsc$temp = function  () {
if (!this.__ImgPool) {
this.__ImgPool = new LzPool(LzSprite.prototype.__getImage, LzSprite.prototype.__gotImage, LzSprite.prototype.__destroyImage, this)
};
var l_$1 = this.frames.length;
for (var i_$2 = 0;i_$2 < l_$1;i_$2++) {
var src_$3 = this.frames[i_$2];
if (this.quirks.preload_images_only_once) {
if (i_$2 > 0 && LzSprite.prototype.__preloadurls[src_$3]) {
continue
};
LzSprite.prototype.__preloadurls[src_$3] = true
};
var im_$4 = this.__ImgPool.get(src_$3, false, true);
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(im_$4)
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__preloadFrames";
return $lzsc$temp
})();
LzSprite.prototype.__findParents = (function  () {
var $lzsc$temp = function  (prop_$1, value_$2) {
var parents_$3 = [];
var root_$4 = LzSprite.__rootSprite;
var sprite_$5 = this;
while (sprite_$5 !== root_$4) {
if (sprite_$5[prop_$1] == value_$2) {
parents_$3.push(sprite_$5)
};
sprite_$5 = sprite_$5.__parent
};
return parents_$3
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__findParents";
return $lzsc$temp
})();
LzSprite.prototype.__imgonload = (function  () {
var $lzsc$temp = function  (i_$1, cacheHit_$2) {
if (this.loading != true) {
return
};
if (this.__imgtimoutid != null) {
clearTimeout(this.__imgtimoutid);
this.__imgtimoutid = null
};
this.loading = false;
if (!cacheHit_$2) {
if (this.quirks.ie_alpha_image_loader) {
i_$1._parent.style.display = ""
} else {
i_$1.style.display = ""
}};
this.resourceWidth = cacheHit_$2 && i_$1["__LZreswidth"] ? i_$1.__LZreswidth : i_$1.width;
this.resourceHeight = cacheHit_$2 && i_$1["__LZresheight"] ? i_$1.__LZresheight : i_$1.height;
if (!cacheHit_$2) {
if (this.quirks.invisible_parent_image_sizing_fix && this.resourceWidth == 0) {
var f_$3 = (function  () {
var $lzsc$temp = function  (i_$1) {
this.resourceWidth = i_$1.width;
this.resourceHeight = i_$1.height
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1811/21";
return $lzsc$temp
})();
this.__processHiddenParents(f_$3, i_$1)
};
if (this.quirks.ie_alpha_image_loader) {
i_$1._parent.__lastcondition = "__imgonload"
} else {
i_$1.__lastcondition = "__imgonload";
i_$1.__LZreswidth = this.resourceWidth;
i_$1.__LZresheight = this.resourceHeight
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else {
if (this.stretches) {
this.__updateStretches()
}}};
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: this.skiponload});
if (this.skiponload != true) {
this.__updateLoadStatus(1)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$1)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__imgonload";
return $lzsc$temp
})();
LzSprite.prototype.__processHiddenParents = (function  () {
var $lzsc$temp = function  (method_$1) {
var sprites_$2 = this.__findParents("visible", false);
var vals_$3 = [];
var l_$4 = sprites_$2.length;
for (var n_$5 = 0;n_$5 < l_$4;n_$5++) {
var v_$6 = sprites_$2[n_$5];
vals_$3[n_$5] = v_$6.__LZdiv.style.display;
v_$6.__LZdiv.style.display = ""
};
var args_$7 = Array.prototype.slice.call(arguments, 1);
var result_$8 = method_$1.apply(this, args_$7);
for (var n_$5 = 0;n_$5 < l_$4;n_$5++) {
var v_$6 = sprites_$2[n_$5];
v_$6.__LZdiv.style.display = vals_$3[n_$5]
};
return result_$8
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__processHiddenParents";
return $lzsc$temp
})();
LzSprite.prototype.__imgonerror = (function  () {
var $lzsc$temp = function  (i_$1, cacheHit_$2) {
if (this.loading != true) {
return
};
if (this.__imgtimoutid != null) {
clearTimeout(this.__imgtimoutid);
this.__imgtimoutid = null
};
this.loading = false;
this.resourceWidth = 1;
this.resourceHeight = 1;
if (!cacheHit_$2) {
if (this.quirks.ie_alpha_image_loader) {
i_$1._parent.__lastcondition = "__imgonerror"
} else {
i_$1.__lastcondition = "__imgonerror"
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else {
if (this.stretches) {
this.__updateStretches()
}}};
this.owner.resourceloaderror();
if (this.skiponload != true) {
this.__updateLoadStatus(0)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$1)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__imgonerror";
return $lzsc$temp
})();
LzSprite.prototype.__imgontimeout = (function  () {
var $lzsc$temp = function  (i_$1, cacheHit_$2) {
if (this.loading != true) {
return
};
this.__imgtimoutid = null;
this.loading = false;
this.resourceWidth = 1;
this.resourceHeight = 1;
if (!cacheHit_$2) {
if (this.quirks.ie_alpha_image_loader) {
i_$1._parent.__lastcondition = "__imgontimeout"
} else {
i_$1.__lastcondition = "__imgontimeout"
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else {
if (this.stretches) {
this.__updateStretches()
}}};
this.owner.resourceloadtimeout();
if (this.skiponload != true) {
this.__updateLoadStatus(0)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$1)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__imgontimeout";
return $lzsc$temp
})();
LzSprite.prototype.__updateLoadStatus = (function  () {
var $lzsc$temp = function  (val_$1) {
this.owner.resourceevent("loadratio", val_$1);
this.owner.resourceevent("framesloadratio", val_$1)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__updateLoadStatus";
return $lzsc$temp
})();
LzSprite.prototype.__destroyImage = (function  () {
var $lzsc$temp = function  (url_$1, img_$2) {
if (img_$2) {
if (img_$2.owner) {
var owner_$3 = img_$2.owner;
if (owner_$3.__imgtimoutid != null) {
clearTimeout(owner_$3.__imgtimoutid);
owner_$3.__imgtimoutid = null
};
lz.BrowserUtils.removecallback(owner_$3)
};
LzSprite.prototype.__clearImageEvents(img_$2);
LzSprite.prototype.__discardElement(img_$2)
};
if (LzSprite.quirks.preload_images_only_once) {
LzSprite.prototype.__preloadurls[url_$1] = null
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__destroyImage";
return $lzsc$temp
})();
LzSprite.prototype.__clearImageEvents = (function  () {
var $lzsc$temp = function  (img_$1) {
if (!img_$1 || img_$1.__cleared) {
return
};
if (LzSprite.quirks.ie_alpha_image_loader) {
var sizer_$2 = img_$1.sizer;
if (sizer_$2) {
if (sizer_$2.tId) {
clearTimeout(sizer_$2.tId)
};
sizer_$2.onerror = null;
sizer_$2.onload = null;
sizer_$2.onloadforeal = null;
sizer_$2._parent = null;
var dummyimg_$3 = {width: sizer_$2.width, height: sizer_$2.height, src: sizer_$2.src};
LzSprite.prototype.__discardElement(sizer_$2);
img_$1.sizer = dummyimg_$3
}} else {
img_$1.onerror = null;
img_$1.onload = null
};
img_$1.__cleared = true
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__clearImageEvents";
return $lzsc$temp
})();
LzSprite.prototype.__gotImage = (function  () {
var $lzsc$temp = function  (url_$1, obj_$2, skiploader_$3) {
if (this.owner.skiponload || skiploader_$3 == true) {
this.owner[obj_$2.__lastcondition]({width: this.owner.resourceWidth, height: this.owner.resourceHeight}, true)
} else {
if (LzSprite.quirks.ie_alpha_image_loader) {
this.owner[obj_$2.__lastcondition](obj_$2.sizer, true)
} else {
this.owner[obj_$2.__lastcondition](obj_$2, true)
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__gotImage";
return $lzsc$temp
})();
LzSprite.prototype.__getImage = (function  () {
var $lzsc$temp = function  (url_$1, skiploader_$2) {
if (LzSprite.quirks.ie_alpha_image_loader) {
var im = document.createElement("div");
im.style.overflow = "hidden";
if (this.owner && skiploader_$2 != true) {
im.owner = this.owner;
if (!im.sizer) {
im.sizer = document.createElement("img");
im.sizer._parent = im
};
im.sizer.onload = (function  () {
var $lzsc$temp = function  () {
im.sizer.tId = setTimeout(this.onloadforeal, 1)
};
$lzsc$temp["displayName"] = "im.sizer.onload";
return $lzsc$temp
})();
im.sizer.onloadforeal = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonload", [im.sizer]);
im.sizer.onerror = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonerror", [im.sizer]);
var callback_$3 = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgontimeout", [im.sizer]);
this.owner.__imgtimoutid = setTimeout(callback_$3, canvas.medialoadtimeout);
im.sizer.src = url_$1
};
if (!skiploader_$2) {
im.style.display = "none"
};
if (this.owner.stretches) {
im.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url_$1 + "',sizingMethod='scale')"
} else {
im.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url_$1 + "')"
}} else {
var im = document.createElement("img");
im.className = "lzimg";
if (!skiploader_$2) {
im.style.display = "none"
};
if (this.owner && skiploader_$2 != true) {
im.owner = this.owner;
im.onload = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonload", [im]);
im.onerror = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonerror", [im]);
var callback_$3 = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgontimeout", [im]);
this.owner.__imgtimoutid = setTimeout(callback_$3, canvas.medialoadtimeout)
};
im.src = url_$1
};
if (im) {
im.__lastcondition = "__imgonload"
};
return im
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__getImage";
return $lzsc$temp
})();
LzSprite.prototype.setClip = (function  () {
var $lzsc$temp = function  (c_$1) {
if (this.clip == c_$1) {
return
};
this.clip = c_$1;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && c_$1) {
this.__sizedtozero = false;
this.__LZdiv.style.width = this._w;
this.__LZdiv.style.height = this._h
}};
this.__updateClip()
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setClip";
return $lzsc$temp
})();
LzSprite.prototype.__updateClip = (function  () {
var $lzsc$temp = function  () {
if (this.isroot && this.quirks.canvas_div_cannot_be_clipped) {
return
};
if (this.clip && this.width != null && this.width >= 0 && this.height != null && this.height >= 0) {
var s_$1 = "rect(0px " + this._w + " " + this._h + " 0px)";
this.__LZdiv.style.clip = s_$1
} else {
if (this.__LZdiv.style.clip) {
var s_$1 = "";
this.__LZdiv.style.clip = s_$1
} else {
return
}};
var quirks_$2 = this.quirks;
if (quirks_$2.fix_clickable) {
this.__LZclickcontainerdiv.style.clip = s_$1
};
if (quirks_$2.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.clip = s_$1
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__updateClip";
return $lzsc$temp
})();
LzSprite.prototype.stretchResource = (function  () {
var $lzsc$temp = function  (s_$1) {
s_$1 = s_$1 != "none" ? s_$1 : null;
if (this.stretches == s_$1) {
return
};
this.stretches = s_$1;
if (!(s_$1 == null && this.__csssprite) && this.__bgimage) {
if (this.quirks.preload_images) {
this.__preloadFrames()
};
this.__setBGImage(null);
this.__setFrame(this.frame, true)
};
this.__updateStretches()
};
$lzsc$temp["displayName"] = "LzSprite.prototype.stretchResource";
return $lzsc$temp
})();
LzSprite.prototype.__updateStretches = (function  () {
var $lzsc$temp = function  () {
if (this.loading) {
return
};
if (this.quirks.ie_alpha_image_loader) {
return
};
if (this.__LZimg) {
var dsp_$1 = this.__LZimg.style.display;
this.__LZimg.style.display = "none";
if (this.stretches == "both") {
this.__LZimg.width = this.width;
this.__LZimg.height = this.height
} else {
if (this.stretches == "height") {
this.__LZimg.width = this.resourceWidth;
this.__LZimg.height = this.height
} else {
if (this.stretches == "width") {
this.__LZimg.width = this.width;
this.__LZimg.height = this.resourceHeight
} else {
this.__LZimg.width = this.resourceWidth;
this.__LZimg.height = this.resourceHeight
}}};
this.__LZimg.style.display = dsp_$1
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__updateStretches";
return $lzsc$temp
})();
LzSprite.prototype.predestroy = (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.predestroy";
return $lzsc$temp
})();
LzSprite.prototype.destroy = (function  () {
var $lzsc$temp = function  () {
if (this.__LZdeleted == true) {
return
};
this.__LZdeleted = true;
if (this.__parent && !this.__parent.__LZdeleted) {
var pc_$1 = this.__parent.__children;
for (var i_$2 = pc_$1.length - 1;i_$2 >= 0;i_$2--) {
if (pc_$1[i_$2] === this) {
pc_$1.splice(i_$2, 1);
break
}}};
if (this.__ImgPool) {
this.__ImgPool.destroy()
};
if (this.__LZimg) {
this.__discardElement(this.__LZimg)
};
if (this.__LZclick) {
this.__setClickable(false, this.__LZclick);
this.__discardElement(this.__LZclick)
};
if (this.__LzInputDiv) {
this.__setTextEvents(false);
this.__discardElement(this.__LzInputDiv)
};
if (this.__LZdiv) {
if (this.isroot) {
if (this.quirks.activate_on_mouseover) {
this.__LZdiv.onmouseover = null;
this.__LZdiv.onmouseout = null
}};
this.__LZdiv.onselectstart = null;
this.__setClickable(false, this.__LZdiv);
this.__discardElement(this.__LZdiv)
};
if (this.__LZinputclickdiv) {
if (this.quirks.ie_mouse_events) {
this.__LZinputclickdiv.onmouseenter = null
} else {
this.__LZinputclickdiv.onmouseover = null
};
this.__discardElement(this.__LZinputclickdiv)
};
if (this.__LZclickcontainerdiv) {
this.__discardElement(this.__LZclickcontainerdiv)
};
if (this.__LZcontextcontainerdiv) {
this.__discardElement(this.__LZcontextcontainerdiv)
};
if (this.__LZcontext) {
this.__discardElement(this.__LZcontext)
};
if (this.__LZtextdiv) {
this.__discardElement(this.__LZtextdiv)
};
if (this.__LZcanvas) {
this.__discardElement(this.__LZcanvas)
};
this.__ImgPool = null;
if (this.quirks.ie_leak_prevention) {
delete this.__sprites[this.uid]
};
if (this.isroot) {
lz.BrowserUtils.scopes = null
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.destroy";
return $lzsc$temp
})();
LzSprite.prototype.getMouse = (function  () {
var $lzsc$temp = function  () {
var p_$1 = this.__getPos();
return {x: LzMouseKernel.__x - p_$1.x, y: LzMouseKernel.__y - p_$1.y}};
$lzsc$temp["displayName"] = "LzSprite.prototype.getMouse";
return $lzsc$temp
})();
LzSprite.prototype.__poscache = null;
LzSprite.prototype.__poscacheid = 0;
LzSprite.__poscachecnt = 0;
LzSprite.prototype.__getPos = (function  () {
var $lzsc$temp = function  () {
if (!LzSprite.__rootSprite.__initdone) {
return lz.embed.getAbsolutePosition(this.__LZdiv)
};
var dirty_$1 = false;
var attached_$2 = true;
var root_$3 = LzSprite.__rootSprite;
var pp_$4, ppmax_$5;
for (var p_$6 = this;p_$6 !== root_$3;p_$6 = pp_$4) {
pp_$4 = p_$6.__parent;
if (pp_$4) {
if (p_$6.__poscacheid < pp_$4.__poscacheid) {
dirty_$1 = true;
ppmax_$5 = pp_$4
}} else {
attached_$2 = false;
break
}};
if (dirty_$1 && attached_$2) {
var next_$7 = ++LzSprite.__poscachecnt;
for (var p_$6 = this;p_$6 !== ppmax_$5;p_$6 = p_$6.__parent) {
p_$6.__poscache = null;
p_$6.__poscacheid = next_$7
}};
var pos_$8 = this.__poscache;
if (!pos_$8) {
pos_$8 = this.__processHiddenParents(lz.embed.getAbsolutePosition, this.__LZdiv);
if (attached_$2) {
this.__poscache = pos_$8
}};
return pos_$8
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__getPos";
return $lzsc$temp
})();
LzSprite.prototype.getWidth = (function  () {
var $lzsc$temp = function  () {
var w_$1 = this.__LZdiv.clientWidth;
return w_$1 == 0 ? this.width : w_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getWidth";
return $lzsc$temp
})();
LzSprite.prototype.getHeight = (function  () {
var $lzsc$temp = function  () {
var h_$1 = this.__LZdiv.clientHeight;
return h_$1 == 0 ? this.height : h_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getHeight";
return $lzsc$temp
})();
LzSprite.prototype.setCursor = (function  () {
var $lzsc$temp = function  (c_$1) {
if (this.quirks.no_cursor_colresize) {
return
};
if (c_$1 == this.cursor) {
return
};
if (this.clickable != true) {
this.setClickable(true)
};
this.cursor = c_$1;
var c_$1 = LzSprite.__defaultStyles.hyphenate(c_$1);
this.__LZclick.style.cursor = c_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setCursor";
return $lzsc$temp
})();
LzSprite.prototype.setShowHandCursor = (function  () {
var $lzsc$temp = function  (s_$1) {
if (s_$1 == true) {
this.setCursor("pointer")
} else {
this.setCursor("default")
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setShowHandCursor";
return $lzsc$temp
})();
LzSprite.prototype.getMCRef = (function  () {
var $lzsc$temp = function  () {
return this.__LZdiv
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getMCRef";
return $lzsc$temp
})();
LzSprite.prototype.getContext = (function  () {
var $lzsc$temp = function  () {
return this.getMCRef()
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getContext";
return $lzsc$temp
})();
LzSprite.prototype.bringToFront = (function  () {
var $lzsc$temp = function  () {
if (!this.__parent) {
Debug.warn("bringToFront with no parent");
return
};
if (this.__parent.__children.length < 2) {
return
};
this.__setZ(++this.__parent.__topZ)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.bringToFront";
return $lzsc$temp
})();
LzSprite.prototype.__setZ = (function  () {
var $lzsc$temp = function  (z_$1) {
this.__LZdiv.style.zIndex = z_$1;
var quirks_$2 = this.quirks;
if (quirks_$2.fix_clickable) {
this.__LZclickcontainerdiv.style.zIndex = z_$1
};
if (quirks_$2.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.zIndex = z_$1
};
this.__z = z_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__setZ";
return $lzsc$temp
})();
LzSprite.prototype.__zCompare = (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
if (a_$1.__z < b_$2.__z) {
return -1
};
if (a_$1.__z > b_$2.__z) {
return 1
};
return 0
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__zCompare";
return $lzsc$temp
})();
LzSprite.prototype.sendToBack = (function  () {
var $lzsc$temp = function  () {
if (!this.__parent) {
Debug.warn("sendToBack with no parent");
return
};
var c_$1 = this.__parent.__children;
if (c_$1.length < 2) {
return
};
c_$1.sort(LzSprite.prototype.__zCompare);
this.sendBehind(c_$1[0])
};
$lzsc$temp["displayName"] = "LzSprite.prototype.sendToBack";
return $lzsc$temp
})();
LzSprite.prototype.sendBehind = (function  () {
var $lzsc$temp = function  (behindSprite_$1) {
if (!behindSprite_$1) {
return
};
if (!this.__parent) {
Debug.warn("sendBehind with no parent");
return
};
var c_$2 = this.__parent.__children;
if (c_$2.length < 2) {
return
};
c_$2.sort(LzSprite.prototype.__zCompare);
var behindZ_$3 = false;
for (var i_$4 = 0;i_$4 < c_$2.length;i_$4++) {
var s_$5 = c_$2[i_$4];
if (s_$5 == behindSprite_$1) {
behindZ_$3 = behindSprite_$1.__z
};
if (behindZ_$3 != false) {
s_$5.__setZ(++s_$5.__z)
}};
this.__setZ(behindZ_$3)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.sendBehind";
return $lzsc$temp
})();
LzSprite.prototype.sendInFrontOf = (function  () {
var $lzsc$temp = function  (frontSprite_$1) {
if (!frontSprite_$1) {
return
};
if (!this.__parent) {
Debug.warn("sendInFrontOf with no parent");
return
};
var c_$2 = this.__parent.__children;
if (c_$2.length < 2) {
return
};
c_$2.sort(LzSprite.prototype.__zCompare);
var frontZ_$3 = false;
for (var i_$4 = 0;i_$4 < c_$2.length;i_$4++) {
var s_$5 = c_$2[i_$4];
if (frontZ_$3 != false) {
s_$5.__setZ(++s_$5.__z)
};
if (s_$5 == frontSprite_$1) {
frontZ_$3 = frontSprite_$1.__z + 1
}};
this.__setZ(frontZ_$3)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.sendInFrontOf";
return $lzsc$temp
})();
LzSprite.prototype.__setFrame = (function  () {
var $lzsc$temp = function  (f_$1, force_$2) {
if (f_$1 < 1) {
f_$1 = 1
} else {
if (f_$1 > this.frames.length) {
f_$1 = this.frames.length
}};
var skipevent_$3 = false;
if (force_$2) {
skipevent_$3 = f_$1 == this.frame
} else {
if (f_$1 == this.frame) {
return
}};
this.frame = f_$1;
if (this.stretches == null && this.__csssprite) {
if (!this.__bgimage) {
this.__LZimg.src = lz.embed.options.resourceroot + LzSprite.prototype.blankimage;
this.__setBGImage(this.__csssprite)
};
var x_$4 = (this.frame - 1) * -this.resourceWidth;
var y_$5 = 0;
this.__LZimg.style.backgroundPosition = x_$4 + "px " + y_$5 + "px"
} else {
var url_$6 = this.frames[this.frame - 1];
this.setSource(url_$6, true)
};
if (skipevent_$3) {
return
};
this.owner.resourceevent("frame", this.frame);
if (this.frames.length == this.frame) {
this.owner.resourceevent("lastframe", null, true)
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__setFrame";
return $lzsc$temp
})();
LzSprite.prototype.__discardElement = (function  () {
var $lzsc$temp = function  (element_$1) {
if (LzSprite.quirks.ie_leak_prevention) {
if (!element_$1 || !element_$1.nodeType) {
return
};
if (element_$1.nodeType >= 1 && element_$1.nodeType < 13) {
if (element_$1.owner) {
element_$1.owner = null
};
var garbageBin_$2 = document.getElementById("__LZIELeakGarbageBin");
if (!garbageBin_$2) {
garbageBin_$2 = document.createElement("DIV");
garbageBin_$2.id = "__LZIELeakGarbageBin";
garbageBin_$2.style.display = "none";
document.body.appendChild(garbageBin_$2)
};
garbageBin_$2.appendChild(element_$1);
garbageBin_$2.innerHTML = ""
}} else {
if (element_$1.parentNode) {
element_$1.parentNode.removeChild(element_$1)
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__discardElement";
return $lzsc$temp
})();
LzSprite.prototype.getZ = (function  () {
var $lzsc$temp = function  () {
return this.__z
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getZ";
return $lzsc$temp
})();
LzSprite.prototype.updateResourceSize = (function  () {
var $lzsc$temp = function  () {
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: true})
};
$lzsc$temp["displayName"] = "LzSprite.prototype.updateResourceSize";
return $lzsc$temp
})();
LzSprite.prototype.unload = (function  () {
var $lzsc$temp = function  () {
this.resource = null;
this.source = null;
this.resourceWidth = null;
this.resourceHeight = null;
if (this.__ImgPool) {
this.__ImgPool.destroy();
this.__ImgPool = null
};
if (this.__LZimg) {
this.__destroyImage(null, this.__LZimg);
this.__LZimg = null
};
this.__updateLoadStatus(0)
};
$lzsc$temp["displayName"] = "LzSprite.prototype.unload";
return $lzsc$temp
})();
LzSprite.prototype.__setCSSClassProperty = (function  () {
var $lzsc$temp = function  (classname_$1, name_$2, value_$3) {
var rulename_$4 = document.all ? "rules" : "cssRules";
var sheets_$5 = document.styleSheets;
var sl_$6 = sheets_$5.length - 1;
for (var i_$7 = sl_$6;i_$7 >= 0;i_$7--) {
var rules_$8 = sheets_$5[i_$7][rulename_$4];
var rl_$9 = rules_$8.length - 1;
for (var j_$10 = rl_$9;j_$10 >= 0;j_$10--) {
if (rules_$8[j_$10].selectorText == classname_$1) {
rules_$8[j_$10].style[name_$2] = value_$3
}}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.__setCSSClassProperty";
return $lzsc$temp
})();
LzSprite.prototype.setDefaultContextMenu = (function  () {
var $lzsc$temp = function  (cmenu_$1) {
LzSprite.__rootSprite.__contextmenu = cmenu_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setDefaultContextMenu";
return $lzsc$temp
})();
LzSprite.prototype.setContextMenu = (function  () {
var $lzsc$temp = function  (cmenu_$1) {
this.__contextmenu = cmenu_$1;
if (!this.quirks.fix_contextmenu || this.__LZcontext) {
return
};
var sprites_$2 = this.__findParents("__LZcontextcontainerdiv", null);
for (var i_$3 = sprites_$2.length - 1;i_$3 >= 0;i_$3--) {
var sprite_$4 = sprites_$2[i_$3];
var parentcontainer_$5 = sprite_$4.__parent.__LZcontextcontainerdiv;
var cxdiv_$6 = document.createElement("div");
cxdiv_$6.className = "lzdiv";
parentcontainer_$5.appendChild(cxdiv_$6);
this.__copystyles(sprite_$4.__LZdiv, cxdiv_$6);
if (sprite_$4._id && !cxdiv_$6.id) {
cxdiv_$6.id = "context" + sprite_$4._id
};
cxdiv_$6.owner = sprite_$4;
sprite_$4.__LZcontextcontainerdiv = cxdiv_$6
};
var cxdiv_$6 = document.createElement("div");
cxdiv_$6.className = "lzcontext";
this.__LZcontextcontainerdiv.appendChild(cxdiv_$6);
this.__LZcontext = cxdiv_$6;
cxdiv_$6.style.width = this._w;
cxdiv_$6.style.height = this._h;
cxdiv_$6.owner = this
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setContextMenu";
return $lzsc$temp
})();
LzSprite.prototype.__copystyles = (function  () {
var $lzsc$temp = function  (from_$1, to_$2) {
to_$2.style.left = from_$1.style.left;
to_$2.style.top = from_$1.style.top;
to_$2.style.display = from_$1.style.display;
to_$2.style.clip = from_$1.style.clip;
to_$2.style.zIndex = from_$1.style.zIndex
};
$lzsc$temp["displayName"] = "LzSprite.prototype.__copystyles";
return $lzsc$temp
})();
LzSprite.prototype.getContextMenu = (function  () {
var $lzsc$temp = function  () {
return this.__contextmenu
};
$lzsc$temp["displayName"] = "LzSprite.prototype.getContextMenu";
return $lzsc$temp
})();
LzSprite.prototype.setRotation = (function  () {
var $lzsc$temp = function  (r_$1) {
var browser_$2 = lz.embed.browser;
if (browser_$2.isSafari) {
this.__LZdiv.style["WebkitTransform"] = "rotate(" + r_$1 + "deg)"
} else {
if (browser_$2.isFirefox) {
this.__LZdiv.style["MozTransform"] = "rotate(" + r_$1 + "deg)"
}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setRotation";
return $lzsc$temp
})();
if (LzSprite.quirks.ie_leak_prevention) {
LzSprite.prototype.__sprites = {};
function __cleanUpForIE () {
LzTextSprite.prototype.__cleanupdivs();
LzTextSprite.prototype._sizecache = {};
var obj_$1 = LzSprite.prototype.__sprites;
for (var i_$2 in obj_$1) {
obj_$1[i_$2].destroy();
obj_$1[i_$2] = null
};
LzSprite.prototype.__sprites = {}};
lz.embed.attachEventHandler(window, "beforeunload", window, "__cleanUpForIE")
};
LzSprite.prototype.getSelectedText = (function  () {
var $lzsc$temp = function  () {
if (window.getSelection) {
return window.getSelection().toString()
} else {
if (document.selection) {
return document.selection.createRange().text.toString()
} else {
if (document.getSelection) {
return document.getSelection()
}}}};
$lzsc$temp["displayName"] = "LzSprite.prototype.getSelectedText";
return $lzsc$temp
})();
LzSprite.prototype.setAADescription = (function  () {
var $lzsc$temp = function  (s_$1) {
var aadiv_$2 = this.aadescriptionDiv;
if (aadiv_$2 == null) {
this.aadescriptionDiv = aadiv_$2 = document.createElement("LABEL");
aadiv_$2.className = "lzaccessibilitydiv";
if (!this.__LZdiv.id) {
this.__LZdiv.id = "sprite_" + this.uid
};
var $lzsc$1940235851 = this.__LZdiv.id;
if (!aadiv_$2.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(aadiv_$2["$lzc$set_for"]) : aadiv_$2["$lzc$set_for"] instanceof Function) {
aadiv_$2["$lzc$set_for"]($lzsc$1940235851)
} else {
aadiv_$2["for"] = $lzsc$1940235851;
var $lzsc$1266362526 = aadiv_$2["onfor"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1266362526) : $lzsc$1266362526 instanceof LzEvent) {
if ($lzsc$1266362526.ready) {
$lzsc$1266362526.sendEvent($lzsc$1940235851)
}}}};
this.__LZdiv.appendChild(aadiv_$2)
};
aadiv_$2.innerHTML = s_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAADescription";
return $lzsc$temp
})();
LzSprite.prototype.setAccessible = (function  () {
var $lzsc$temp = function  (accessible_$1) {
LzSprite.__rootSprite.accessible = accessible_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAccessible";
return $lzsc$temp
})();
LzSprite.prototype._accProps = null;
LzSprite.prototype.setAAActive = (function  () {
var $lzsc$temp = function  (s_$1) {
this.__LzAccessibilityActive = s_$1
};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAAActive";
return $lzsc$temp
})();
LzSprite.prototype.setAASilent = (function  () {
var $lzsc$temp = function  (s_$1) {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAASilent";
return $lzsc$temp
})();
LzSprite.prototype.setAAName = (function  () {
var $lzsc$temp = function  (s_$1) {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAAName";
return $lzsc$temp
})();
LzSprite.prototype.aafocus = (function  () {
var $lzsc$temp = function  () {
try {
if (this.__LZdiv != null) {
this.__LZdiv.blur();
this.__LZdiv.focus()
}}
catch (e) {

}};
$lzsc$temp["displayName"] = "LzSprite.prototype.aafocus";
return $lzsc$temp
})();
LzSprite.prototype.setAATabIndex = (function  () {
var $lzsc$temp = function  (s_$1) {

};
$lzsc$temp["displayName"] = "LzSprite.prototype.setAATabIndex";
return $lzsc$temp
})();
LzSprite.prototype.sendAAEvent = (function  () {
var $lzsc$temp = function  (childID_$1, eventType_$2, nonHTML_$3) {
try {
if (this.__LZdiv != null) {
this.__LZdiv.focus()
}}
catch (e) {

}};
$lzsc$temp["displayName"] = "LzSprite.prototype.sendAAEvent";
return $lzsc$temp
})();
LzSprite.prototype.setID = (function  () {
var $lzsc$temp = function  (id_$1) {
if (!this._id) {
this._id = id_$1
};
if (!this.__LZdiv.id) {
this.__LZdiv.id = this._dbg_typename + id_$1
};
if (!this.__LZclickcontainerdiv.id) {
this.__LZclickcontainerdiv.id = "click" + id_$1
};
if (this.__LZcontextcontainerdiv && !this.__LZcontextcontainerdiv.id) {
this.__LZcontextcontainerdiv.id = this.__LZcontextcontainerdiv.id = "context" + id_$1
}};
$lzsc$temp["displayName"] = "LzSprite.prototype.setID";
return $lzsc$temp
})();
Class.make("LzLibrary", LzNode, ["loaded", false, "loading", false, "sprite", null, "href", void 0, "stage", "late", "onload", LzDeclaredEvent, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
this.stage = args_$2.stage;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.sprite = new LzSprite(this, false, args_$2);
LzLibrary.libraries[args_$2.name] = this
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
if (this.stage == "late") {
this.load()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
if (this.sprite) {
this.sprite.destroy();
this.sprite = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Library " + this.href + " named " + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "load", (function  () {
var $lzsc$temp = function  () {
if (this.loading || this.loaded) {
return
};
this.loading = true;
lz.embed.__dhtmlLoadLibrary(this.href)
};
$lzsc$temp["displayName"] = "load";
return $lzsc$temp
})(), "loadfinished", (function  () {
var $lzsc$temp = function  () {
this.loading = false;
if (this.onload.ready) {
this.onload.sendEvent(true)
}};
$lzsc$temp["displayName"] = "loadfinished";
return $lzsc$temp
})()], ["tagname", "import", "attributes", new LzInheritedHash(LzNode.attributes), "libraries", {}, "findLibrary", (function  () {
var $lzsc$temp = function  (libname_$1) {
return LzLibrary.libraries[libname_$1]
};
$lzsc$temp["displayName"] = "findLibrary";
return $lzsc$temp
})(), "stripQueryString", (function  () {
var $lzsc$temp = function  (str_$1) {
if (str_$1.indexOf("?") > 0) {
str_$1 = str_$1.substring(0, str_$1.indexOf("?"))
};
return str_$1
};
$lzsc$temp["displayName"] = "stripQueryString";
return $lzsc$temp
})(), "__LZsnippetLoaded", (function  () {
var $lzsc$temp = function  (url_$1) {
url_$1 = LzLibrary.stripQueryString(url_$1);
var lib_$2 = null;
var libs_$3 = LzLibrary.libraries;
for (var l_$4 in libs_$3) {
var libhref_$5 = LzLibrary.stripQueryString(libs_$3[l_$4].href);
if (libhref_$5 == url_$1) {
lib_$2 = libs_$3[l_$4];
break
}};
if (lib_$2 == null) {
Debug.error("could not find library with href", url_$1)
} else {
lib_$2.loaded = true;
canvas.initiatorAddNode({attrs: {libname: lib_$2.name}, "class": LzLibraryCleanup}, 1);
canvas.initDone()
}};
$lzsc$temp["displayName"] = "__LZsnippetLoaded";
return $lzsc$temp
})()]);
lz[LzLibrary.tagname] = LzLibrary;
var LzTextSprite = (function  () {
var $lzsc$temp = function  (owner_$1) {
if (owner_$1 == null) {
return
};
this.constructor = arguments.callee;
this.owner = owner_$1;
this.uid = LzSprite.prototype.uid++;
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lztextcontainer";
this.scrolldiv = this.__LZtextdiv = document.createElement("div");
this.scrolldiv.owner = this;
this.scrolldiv.className = "lztext";
this.__LZdiv.appendChild(this.scrolldiv);
if (this.quirks.emulate_flash_font_metrics) {
this.scrolldiv.className = "lzswftext"
};
this.__LZdiv.owner = this;
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv = document.createElement("div");
this.__LZclickcontainerdiv.className = "lztextcontainer_click";
this.__LZclickcontainerdiv.owner = this
};
if (this.quirks.ie_leak_prevention) {
this.__sprites[this.uid] = this
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#14/20";
return $lzsc$temp
})();
LzTextSprite.prototype = new LzSprite(null);
LzTextSprite.prototype._dbg_typename = "LzTextSprite";
LzTextSprite.prototype.__initTextProperties = (function  () {
var $lzsc$temp = function  (args_$1) {
this.setFontName(args_$1.font);
this.setFontStyle(args_$1.fontstyle);
this.setFontSize(args_$1.fontsize)
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__initTextProperties";
return $lzsc$temp
})();
LzTextSprite.prototype._fontStyle = "normal";
LzTextSprite.prototype._fontWeight = "normal";
LzTextSprite.prototype._fontSize = "11px";
LzTextSprite.prototype._fontFamily = "Verdana,Vera,sans-serif";
LzTextSprite.prototype._whiteSpace = "normal";
LzTextSprite.prototype._textAlign = "left";
LzTextSprite.prototype._textIndent = "0px";
LzTextSprite.prototype.__LZtextIndent = 0;
LzTextSprite.prototype._letterSpacing = "0px";
LzTextSprite.prototype._textDecoration = "none";
LzTextSprite.prototype.__wpadding = 4;
LzTextSprite.prototype.__hpadding = 4;
LzTextSprite.prototype.__sizecacheupperbound = 1000;
LzTextSprite.prototype.selectable = true;
LzTextSprite.prototype.text = "";
LzTextSprite.prototype.resize = true;
LzTextSprite.prototype.restrict = null;
LzTextSprite.prototype.setFontSize = (function  () {
var $lzsc$temp = function  (fsize_$1) {
if (fsize_$1 == null || fsize_$1 < 0) {
return
};
var fp_$2 = this.CSSDimension(fsize_$1);
if (this._fontSize != fp_$2) {
this._fontSize = fp_$2;
this.scrolldiv.style.fontSize = fp_$2;
if (this.quirks["emulate_flash_font_metrics"]) {
var lh_$3 = Math.round(fsize_$1 * 1.2);
this.scrolldiv.style.lineHeight = this.CSSDimension(lh_$3);
this._lineHeight = lh_$3
};
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setFontSize";
return $lzsc$temp
})();
LzTextSprite.prototype.setFontStyle = (function  () {
var $lzsc$temp = function  (fstyle_$1) {
var fweight_$2;
if (fstyle_$1 == "plain") {
fweight_$2 = "normal";
fstyle_$1 = "normal"
} else {
if (fstyle_$1 == "bold") {
fweight_$2 = "bold";
fstyle_$1 = "normal"
} else {
if (fstyle_$1 == "italic") {
fweight_$2 = "normal";
fstyle_$1 = "italic"
} else {
if (fstyle_$1 == "bold italic" || fstyle_$1 == "bolditalic") {
fweight_$2 = "bold";
fstyle_$1 = "italic"
}}}};
if (fweight_$2 != this._fontWeight) {
this._fontWeight = fweight_$2;
this.scrolldiv.style.fontWeight = fweight_$2;
this.__updatefieldsize()
};
if (fstyle_$1 != this._fontStyle) {
this._fontStyle = fstyle_$1;
this.scrolldiv.style.fontStyle = fstyle_$1;
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setFontStyle";
return $lzsc$temp
})();
LzTextSprite.prototype.setFontName = (function  () {
var $lzsc$temp = function  (fname_$1) {
if (fname_$1 != this._fontFamily) {
this._fontFamily = fname_$1;
this.scrolldiv.style.fontFamily = fname_$1;
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setFontName";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextColor = LzSprite.prototype.setColor;
LzTextSprite.prototype.lineHeight = null;
LzTextSprite.prototype.scrollTop = null;
LzTextSprite.prototype.scrollHeight = null;
LzTextSprite.prototype.scrollLeft = null;
LzTextSprite.prototype.scrollWidth = null;
LzTextSprite.prototype.scrollevents = false;
LzTextSprite.prototype.setScrollEvents = (function  () {
var $lzsc$temp = function  (on_$1) {
var scrolldiv_$2 = this.scrolldiv;
var sdc_$3 = scrolldiv_$2.className;
if (sdc_$3 == "lzswftext" || sdc_$3 == "lzswfinputtextmultiline") {
var ht_$4 = this.height;
var wt_$5 = this.width;
var cdim_$6 = this.CSSDimension;
if (on_$1) {
this.scrollevents = true;
scrolldiv_$2.style.overflow = "scroll";
ht_$4 += this.quirks.scrollbar_width;
wt_$5 += this.quirks.scrollbar_width
} else {
this.scrollevents = false;
scrolldiv_$2.style.overflow = "hidden"
};
var hp_$7 = cdim_$6(ht_$4);
var wp_$8 = cdim_$6(wt_$5);
if (on_$1) {
scrolldiv_$2.style.clip = "rect(0 " + wp_$8 + " " + hp_$7 + " 0)"
} else {
if (scrolldiv_$2.style.clip) {
scrolldiv_$2.style.clip = ""
}};
scrolldiv_$2.style.height = hp_$7;
scrolldiv_$2.style.width = wp_$8
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setScrollEvents";
return $lzsc$temp
})();
LzTextSprite.prototype.__updatefieldsize = (function  () {
var $lzsc$temp = function  () {
var lineHeight_$1 = this.getLineHeight();
if (this.lineHeight !== lineHeight_$1) {
this.lineHeight = lineHeight_$1;
this.owner.scrollevent("lineHeight", lineHeight_$1)
};
if (!this.scrollevents) {
return
};
this.__updatefieldprop("scrollHeight");
this.__updatefieldprop("scrollTop");
this.__updatefieldprop("scrollWidth");
this.__updatefieldprop("scrollLeft")
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__updatefieldsize";
return $lzsc$temp
})();
LzTextSprite.prototype.__updatefieldprop = (function  () {
var $lzsc$temp = function  (name_$1) {
var val_$2 = this.scrolldiv[name_$1];
if (this[name_$1] !== val_$2) {
this[name_$1] = val_$2;
this.owner.scrollevent(name_$1, val_$2)
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__updatefieldprop";
return $lzsc$temp
})();
LzTextSprite.prototype.setText = (function  () {
var $lzsc$temp = function  (t_$1, force_$2) {
if (this.multiline && t_$1 && t_$1.indexOf("\n") >= 0) {
if (this.quirks["inner_html_strips_newlines"]) {
t_$1 = t_$1.replace(this.inner_html_strips_newlines_re, "<br/>")
}};
if (t_$1 && this.quirks["inner_html_no_entity_apos"]) {
t_$1 = t_$1.replace(RegExp("&apos;", "mg"), "&#39;")
};
if (force_$2 != true && this.text == t_$1) {
return
};
this.text = t_$1;
this.scrolldiv.innerHTML = t_$1;
this.__updatefieldsize();
if (this.resize && this.multiline == false) {
this.setWidth(this.getTextWidth())
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setText";
return $lzsc$temp
})();
LzTextSprite.prototype.setMultiline = (function  () {
var $lzsc$temp = function  (m_$1) {
m_$1 = m_$1 == true;
if (this.multiline == m_$1) {
return
};
this.multiline = m_$1;
if (m_$1) {
if (this._whiteSpace != "normal") {
this._whiteSpace = "normal";
this.scrolldiv.style.whiteSpace = "normal"
}} else {
if (this._whiteSpace != "nowrap") {
this._whiteSpace = "nowrap";
this.scrolldiv.style.whiteSpace = "nowrap"
}};
if (this.quirks["text_height_includes_padding"]) {
this.__hpadding = m_$1 ? 3 : 4
};
this.setText(this.text, true)
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setMultiline";
return $lzsc$temp
})();
LzTextSprite.prototype.setPattern = (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 == null || val_$1 == "") {
this.restrict = null
} else {
if (RegExp("^\\[.*\\]\\*$").test(val_$1)) {
this.restrict = RegExp(val_$1.substring(0, val_$1.length - 1) + "|[\\r\\n]", "g")
} else {
Debug.warn('LzTextSprite.setPattern argument %w must be of the form "[...]*"', val_$1)
}}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setPattern";
return $lzsc$temp
})();
LzTextSprite.prototype.getTextWidth = (function  () {
var $lzsc$temp = function  () {
var width_$1;
var scrolldiv_$2 = this.scrolldiv;
var className_$3 = scrolldiv_$2.className;
var style_$4 = scrolldiv_$2.style.cssText;
var styleKey_$5 = className_$3 + "/" + style_$4;
var cv_$6 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$5 && this._cacheTextKey == this.text && ("width" in cv_$6)) {
width_$1 = cv_$6.width
} else {
width_$1 = this.getTextDimension("width")
};
if (width_$1 != 0 && this.quirks["emulate_flash_font_metrics"]) {
width_$1 += this.__wpadding
};
return width_$1
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getTextWidth";
return $lzsc$temp
})();
LzTextSprite.prototype.getLineHeight = (function  () {
var $lzsc$temp = function  () {
if (this._lineHeight) {
return this._lineHeight
};
var scrolldiv_$1 = this.scrolldiv;
var className_$2 = scrolldiv_$1.className;
var style_$3 = scrolldiv_$1.style.cssText;
var styleKey_$4 = className_$2 + "/" + style_$3;
var cv_$5 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$4 && ("lineheight" in cv_$5)) {
var lineheight_$6 = cv_$5.lineheight
} else {
var lineheight_$6 = this.getTextDimension("lineheight")
};
return lineheight_$6
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getLineHeight";
return $lzsc$temp
})();
LzTextSprite.prototype.getTextHeight = LzTextSprite.prototype.getLineHeight;
LzTextSprite.prototype.getTextfieldHeight = (function  () {
var $lzsc$temp = function  () {
var fieldHeight_$1 = null;
if (this.multiline && this.text != "") {
var scrolldiv_$2 = this.scrolldiv;
var className_$3 = scrolldiv_$2.className;
var style_$4 = scrolldiv_$2.style.cssText;
var styleKey_$5 = className_$3 + "/" + style_$4;
var cv_$6 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$5 && this._cacheTextKey == this.text && ("height" in cv_$6)) {
fieldHeight_$1 = cv_$6.height
} else {
fieldHeight_$1 = this.getTextDimension("height")
};
if (this.quirks["safari_textarea_subtract_scrollbar_height"]) {
fieldHeight_$1 += 24
}} else {
fieldHeight_$1 = this.getLineHeight()
};
if (this.quirks["emulate_flash_font_metrics"]) {
fieldHeight_$1 += this.__hpadding
};
return fieldHeight_$1
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getTextfieldHeight";
return $lzsc$temp
})();
LzTextSprite.prototype._sizecache = {counter: 0};
if (LzSprite.quirks.ie_leak_prevention) {
LzTextSprite.prototype.__divstocleanup = [];
LzTextSprite.prototype.__cleanupdivs = (function  () {
var $lzsc$temp = function  () {
var obj_$1 = LzTextSprite.prototype.__divstocleanup;
var func_$2 = LzSprite.prototype.__discardElement;
var l_$3 = obj_$1.length;
for (var i_$4 = 0;i_$4 < l_$3;i_$4++) {
func_$2(obj_$1[i_$4])
};
LzTextSprite.prototype.__divstocleanup = []
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__cleanupdivs";
return $lzsc$temp
})()
};
LzTextSprite.prototype._cacheStyleKey = null;
LzTextSprite.prototype._cacheTextKey = null;
LzTextSprite.prototype._cachevalue = null;
LzTextSprite.prototype.getTextDimension = (function  () {
var $lzsc$temp = function  (dimension_$1) {
var string_$2 = this.text;
var width_$3 = "auto";
switch (dimension_$1) {
case "lineheight":
if (this._lineHeight) {
return this._lineHeight
};
string_$2 = 'Yq_gy"9;';
break;;case "height":
width_$3 = this.CSSDimension(this.width);
break;;case "width":
if (this.text == "") {
return 0
};
break;;default:
Debug.error("Unknown dimension: %w", dimension_$1)
};
var scrolldiv_$4 = this.scrolldiv;
var className_$5 = scrolldiv_$4.className;
var style_$6 = scrolldiv_$4.style.cssText;
var styleKey_$7 = className_$5 + "/" + style_$6;
var cv_$8 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$7 && (dimension_$1 == "lineheight" || this._cacheTextKey == string_$2) && (dimension_$1 in cv_$8)) {
return cv_$8[dimension_$1]
};
this._cacheStyleKey = styleKey_$7;
if (dimension_$1 != "lineheight") {
this._cacheTextKey = string_$2
};
var sds_$9 = scrolldiv_$4.style;
style_$6 = "overflow: visible; width: " + width_$3 + "; height: auto; " + (sds_$9.fontSize ? "font-size: " + sds_$9.fontSize + "; " : "") + (sds_$9.fontWeight ? "font-weight: " + sds_$9.fontWeight + "; " : "") + (sds_$9.fontStyle ? "font-style: " + sds_$9.fontStyle + "; " : "") + (sds_$9.fontFamily ? "font-family: " + sds_$9.fontFamily + "; " : "") + (sds_$9.lineHeight ? "line-height: " + sds_$9.lineHeight + "; " : "") + (sds_$9.letterSpacing ? "letter-spacing: " + sds_$9.letterSpacing + "; " : "") + (sds_$9.whiteSpace ? "white-space: " + sds_$9.whiteSpace + "; " : "");
var cacheFullKey_$10 = className_$5 + "/" + style_$6 + "{" + string_$2 + "}";
var ltsp_$11 = LzTextSprite.prototype;
var _sizecache_$12 = ltsp_$11._sizecache;
var cv_$8 = this._cachevalue = _sizecache_$12[cacheFullKey_$10];
if (cv_$8 && (dimension_$1 in cv_$8)) {
return cv_$8[dimension_$1]
};
var root_$13 = document.getElementById("lzTextSizeCache");
if (_sizecache_$12.counter > 0 && _sizecache_$12.counter % this.__sizecacheupperbound == 0) {
ltsp_$11._sizecache = _sizecache_$12 = {counter: 0};
cv_$8 = null;
if (LzSprite.quirks.ie_leak_prevention) {
ltsp_$11.__cleanupdivs()
};
if (root_$13) {
root_$13.innerHTML = ""
}};
if (!cv_$8) {
cv_$8 = this._cachevalue = _sizecache_$12[cacheFullKey_$10] = {}};
if (!root_$13) {
root_$13 = document.createElement("div");
lz.embed.__setAttr(root_$13, "id", "lzTextSizeCache");
document.body.appendChild(root_$13)
};
var tagname_$14 = "div";
var mdivKey_$15 = className_$5 + "/" + style_$6 + "div";
var mdiv_$16 = _sizecache_$12[mdivKey_$15];
if (mdiv_$16) {
this.__setTextContent(mdiv_$16, scrolldiv_$4.tagName, string_$2)
} else {
if (this.quirks["text_measurement_use_insertadjacenthtml"]) {
var html_$17 = "<" + tagname_$14 + ' id="testSpan' + _sizecache_$12.counter + '"';
html_$17 += ' class="' + className_$5 + '"';
html_$17 += ' style="' + style_$6 + '">';
html_$17 += string_$2;
html_$17 += "</" + tagname_$14 + ">";
root_$13.insertAdjacentHTML("beforeEnd", html_$17);
var mdiv_$16 = document.all["testSpan" + _sizecache_$12.counter];
if (this.quirks.ie_leak_prevention) {
ltsp_$11.__divstocleanup.push(mdiv_$16)
}} else {
var mdiv_$16 = document.createElement(tagname_$14);
lz.embed.__setAttr(mdiv_$16, "class", className_$5);
lz.embed.__setAttr(mdiv_$16, "style", style_$6);
this.__setTextContent(mdiv_$16, scrolldiv_$4.tagName, string_$2);
root_$13.appendChild(mdiv_$16)
};
_sizecache_$12[mdivKey_$15] = mdiv_$16
};
mdiv_$16.style.display = "inline";
cv_$8[dimension_$1] = dimension_$1 == "width" ? mdiv_$16.clientWidth : mdiv_$16.clientHeight;
mdiv_$16.style.display = "none";
_sizecache_$12.counter++;
return cv_$8[dimension_$1]
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getTextDimension";
return $lzsc$temp
})();
LzTextSprite.prototype.__setTextContent = (function  () {
var $lzsc$temp = function  (mdiv_$1, tagname_$2, string_$3) {
switch (tagname_$2) {
case "DIV":
mdiv_$1.innerHTML = string_$3;
break;;case "INPUT":
case "TEXTAREA":
if (this.quirks["text_content_use_inner_text"]) {
mdiv_$1.innerText = string_$3
} else {
mdiv_$1.textContent = string_$3
};
break;;default:
Debug.error("Unknown tagname: %w", tagname_$2)
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__setTextContent";
return $lzsc$temp
})();
LzTextSprite.prototype.setSelectable = (function  () {
var $lzsc$temp = function  (s_$1) {
this.selectable = s_$1;
var browser_$2 = lz.embed.browser;
if (s_$1) {
this.__LZdiv.style["cursor"] = "auto";
if (browser_$2.isIE) {
this.__LZdiv.onselectstart = null
} else {
if (browser_$2.isFirefox) {
this.__LZdiv.style["MozUserSelect"] = "text"
} else {
if (browser_$2.isSafari) {
this.__LZdiv.style["WebkitUserSelect"] = "text"
} else {
this.__LZdiv.style["UserSelect"] = "text"
}}}} else {
this.__LZdiv.style["cursor"] = "";
if (browser_$2.isIE) {
this.__LZdiv.onselectstart = LzTextSprite.prototype.__cancelhandler
} else {
if (browser_$2.isFirefox) {
this.__LZdiv.style["MozUserSelect"] = "none"
} else {
if (browser_$2.isSafari) {
this.__LZdiv.style["WebkitUserSelect"] = "none"
} else {
this.__LZdiv.style["UserSelect"] = "none"
}}}}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setSelectable";
return $lzsc$temp
})();
LzTextSprite.prototype.__cancelhandler = (function  () {
var $lzsc$temp = function  () {
return false
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__cancelhandler";
return $lzsc$temp
})();
LzTextSprite.prototype.setResize = (function  () {
var $lzsc$temp = function  (r_$1) {
this.resize = r_$1 == true
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setResize";
return $lzsc$temp
})();
LzTextSprite.prototype.setSelection = (function  () {
var $lzsc$temp = function  (start_$1, end_$2) {
switch (arguments.length) {
case 1:
end_$2 = null
};
if (end_$2 == null) {
end_$2 = start_$1
};
if (this.quirks["text_selection_use_range"]) {
var range_$3 = document.body.createTextRange();
range_$3.moveToElementText(this.scrolldiv);
if (start_$1 > end_$2) {
var st_$4 = start_$1;
start_$1 = end_$2;
end_$2 = st_$4
};
var st_$4 = start_$1;
var ed_$5 = end_$2 - range_$3.text.length;
range_$3.moveStart("character", st_$4);
range_$3.moveEnd("character", ed_$5);
range_$3.select()
} else {
var range_$3 = document.createRange();
var offset_$6 = 0;
range_$3.setStart(this.scrolldiv.childNodes[0], start_$1);
range_$3.setEnd(this.scrolldiv.childNodes[0], end_$2);
var sel_$7 = window.getSelection();
sel_$7.removeAllRanges();
sel_$7.addRange(range_$3)
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setSelection";
return $lzsc$temp
})();
LzTextSprite.prototype.__findNodeByOffset = (function  () {
var $lzsc$temp = function  (offset_$1) {
var node_$2 = this.scrolldiv.childNodes[0];
var curroffset_$3 = 0;
while (node_$2) {
if (node_$2.nodeType == 3) {
offset_$1 += node_$2.textContent.length
} else {
if (node_$2.nodeType == 1 && node_$2.nodeName == "BR") {
offset_$1 += 1
}};
if (curroffset_$3 >= offset_$1) {
return node_$2
};
node_$2 = node_$2.nextSibling
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__findNodeByOffset";
return $lzsc$temp
})();
LzTextSprite.prototype.__getGlobalRange = (function  () {
var $lzsc$temp = function  () {
var browser_$1 = lz.embed.browser;
var userSelection_$2;
if (this.quirks["text_selection_use_range"]) {
userSelection_$2 = document.selection.createRange()
} else {
if (window.getSelection) {
userSelection_$2 = window.getSelection()
}};
try {
if (userSelection_$2) {
if (this.quirks["text_selection_use_range"]) {
return userSelection_$2
} else {
if (userSelection_$2.getRangeAt) {
return userSelection_$2.getRangeAt(0)
} else {
var range_$3 = document.createRange();
range_$3.setStart(userSelection_$2.anchorNode, userSelection_$2.anchorOffset);
range_$3.setEnd(userSelection_$2.focusNode, userSelection_$2.focusOffset);
return range_$3
}}}}
catch (e) {

}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__getGlobalRange";
return $lzsc$temp
})();
LzTextSprite.prototype.__textareaToRange = (function  () {
var $lzsc$temp = function  (textarea_$1) {
var bookmark_$2 = textarea_$1.getBookmark();
var contents_$3, originalContents_$4;
originalContents_$4 = contents_$3 = this.scrolldiv.innerHTML;
var owner_$5 = this.__getRangeOwner(textarea_$1);
if (!(owner_$5 instanceof LzTextSprite)) {
return
};
do{
var marker_$6 = "~~~" + Math.random() + "~~~"
} while (contents_$3.indexOf(marker_$6) != -1);
textarea_$1.text = marker_$6 + textarea_$1.text + marker_$6;
contents_$3 = this.scrolldiv.innerHTML;
contents_$3 = contents_$3.replace("<BR>", " ");
var range_$7 = {};
range_$7.startOffset = contents_$3.indexOf(marker_$6);
contents_$3 = contents_$3.replace(marker_$6, "");
range_$7.endOffset = contents_$3.indexOf(marker_$6);
this.scrolldiv.innerHTML = originalContents_$4;
textarea_$1.moveToBookmark(bookmark_$2);
textarea_$1.select();
return range_$7
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__textareaToRange";
return $lzsc$temp
})();
LzTextSprite.prototype.__getRangeOwner = (function  () {
var $lzsc$temp = function  (range_$1) {
if (!range_$1) {
return
};
if (this.quirks["text_selection_use_range"]) {
var range_$1 = range_$1.duplicate();
range_$1.collapse();
return range_$1.parentElement().owner
} else {
if (range_$1.startContainer.parentNode == range_$1.endContainer.parentNode) {
return range_$1.startContainer.parentNode.owner
}}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__getRangeOwner";
return $lzsc$temp
})();
LzTextSprite.prototype.__getOffset = (function  () {
var $lzsc$temp = function  (node_$1) {
var offset_$2 = 0;
while (node_$1 = node_$1.previousSibling) {
if (node_$1.nodeType == 3) {
offset_$2 += node_$1.textContent.length
} else {
if (node_$1.nodeType == 1 && node_$1.nodeName == "BR") {
offset_$2 += 1
}}};
return offset_$2
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.__getOffset";
return $lzsc$temp
})();
LzTextSprite.prototype.getSelectionPosition = (function  () {
var $lzsc$temp = function  () {
var range_$1 = this.__getGlobalRange();
if (this.__getRangeOwner(range_$1) === this) {
if (this.quirks["text_selection_use_range"]) {
range_$1 = this.__textareaToRange(range_$1);
return range_$1.startOffset
} else {
var offset_$2 = 0;
if (this.multiline) {
offset_$2 = this.__getOffset(range_$1.startContainer)
};
return range_$1.startOffset + offset_$2
}} else {
return -1
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getSelectionPosition";
return $lzsc$temp
})();
LzTextSprite.prototype.getSelectionSize = (function  () {
var $lzsc$temp = function  () {
var range_$1 = this.__getGlobalRange();
if (this.__getRangeOwner(range_$1) === this) {
if (this.quirks["text_selection_use_range"]) {
range_$1 = this.__textareaToRange(range_$1)
} else {
if (this.multiline) {
var startoffset_$2 = this.__getOffset(range_$1.startContainer);
var endoffset_$3 = this.__getOffset(range_$1.endContainer);
return range_$1.endOffset + endoffset_$3 - (range_$1.startOffset + startoffset_$2)
}};
return range_$1.endOffset - range_$1.startOffset
} else {
return -1
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getSelectionSize";
return $lzsc$temp
})();
LzTextSprite.prototype.getScroll = (function  () {
var $lzsc$temp = function  () {
Debug.warn("LzTextSprite.getScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getScroll";
return $lzsc$temp
})();
LzTextSprite.prototype.getMaxScroll = (function  () {
var $lzsc$temp = function  () {
Debug.warn("LzTextSprite.getMaxScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.getMaxScroll";
return $lzsc$temp
})();
LzTextSprite.prototype.setScroll = (function  () {
var $lzsc$temp = function  () {
Debug.warn("LzTextSprite.setScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setScroll";
return $lzsc$temp
})();
LzTextSprite.prototype.setYScroll = (function  () {
var $lzsc$temp = function  (n_$1) {
this.scrolldiv.scrollTop = this.scrollTop = -n_$1
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setYScroll";
return $lzsc$temp
})();
LzTextSprite.prototype.setXScroll = (function  () {
var $lzsc$temp = function  (n_$1) {
this.scrolldiv.scrollLeft = this.scrollLeft = -n_$1
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setXScroll";
return $lzsc$temp
})();
LzTextSprite.prototype.setX = (function  () {
var $lzsc$temp = function  (x_$1) {
var scrolling_$2 = this.scrollevents;
var turd_$3 = scrolling_$2 && this.quirks["clipped_scrollbar_causes_display_turd"];
if (scrolling_$2) {
var scrolldiv_$4 = this.scrolldiv;
var oldLeft_$5 = scrolldiv_$4.scrollLeft;
var oldTop_$6 = scrolldiv_$4.scrollTop;
if (turd_$3) {
scrolldiv_$4.style.overflow = "hidden";
scrolldiv_$4.style.paddingRight = scrolldiv_$4.style.paddingBottom = this.quirks.scrollbar_width
}};
LzSprite.prototype.setX.call(this, x_$1);
if (scrolling_$2) {
if (turd_$3) {
scrolldiv_$4.style.overflow = "scroll";
scrolldiv_$4.style.paddingRight = scrolldiv_$4.style.paddingBottom = "0"
};
scrolldiv_$4.scrollLeft = oldLeft_$5;
scrolldiv_$4.scrollTop = oldTop_$6
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setX";
return $lzsc$temp
})();
LzTextSprite.prototype.setY = (function  () {
var $lzsc$temp = function  (y_$1) {
var scrolling_$2 = this.scrollevents;
var turd_$3 = scrolling_$2 && this.quirks["clipped_scrollbar_causes_display_turd"];
if (scrolling_$2) {
var scrolldiv_$4 = this.scrolldiv;
var oldLeft_$5 = scrolldiv_$4.scrollLeft;
var oldTop_$6 = scrolldiv_$4.scrollTop;
if (turd_$3) {
scrolldiv_$4.style.overflow = "hidden";
scrolldiv_$4.style.paddingRight = scrolldiv_$4.style.paddingBottom = this.quirks.scrollbar_width
}};
LzSprite.prototype.setY.call(this, y_$1);
if (scrolling_$2) {
if (turd_$3) {
scrolldiv_$4.style.overflow = "scroll";
scrolldiv_$4.style.paddingRight = scrolldiv_$4.style.paddingBottom = "0"
};
scrolldiv_$4.scrollLeft = oldLeft_$5;
scrolldiv_$4.scrollTop = oldTop_$6
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setY";
return $lzsc$temp
})();
LzTextSprite.prototype.setWidth = (function  () {
var $lzsc$temp = function  (w_$1, force_$2) {
if (w_$1 == null || w_$1 < 0 || isNaN(w_$1)) {
return
};
var nw_$3 = LzSprite.prototype.setWidth.call(this, w_$1);
var wt_$4 = w_$1;
var scrolldiv_$5 = this.scrolldiv;
var style_$6 = scrolldiv_$5.style;
var cdim_$7 = this.CSSDimension;
var wp_$8 = cdim_$7(wt_$4);
var h_$9 = this.height;
var hp_$10 = cdim_$7(h_$9 != null ? h_$9 : 0);
if (this.scrollevents) {
wt_$4 += this.quirks.scrollbar_width
};
var wtInd_$11 = this.__LZtextIndent < 0 ? -1 * this.__LZtextIndent : 0;
if (wt_$4 >= wtInd_$11) {
wt_$4 -= wtInd_$11
};
wp_$8 = cdim_$7(wt_$4);
if (style_$6.width != wp_$8) {
scrolldiv_$5.style.width = wp_$8;
if (this.scrollevents) {
scrolldiv_$5.style.clip = "rect(0 " + wp_$8 + " " + hp_$10 + " 0)"
};
this.__updatefieldsize()
};
return nw_$3
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setWidth";
return $lzsc$temp
})();
LzTextSprite.prototype.setHeight = (function  () {
var $lzsc$temp = function  (h_$1) {
if (h_$1 == null || h_$1 < 0 || isNaN(h_$1)) {
return
};
var nh_$2 = LzSprite.prototype.setHeight.call(this, h_$1);
var ht_$3 = h_$1;
var scrolldiv_$4 = this.scrolldiv;
var style_$5 = scrolldiv_$4.style;
var cdim_$6 = this.CSSDimension;
var w_$7 = this.width;
var wp_$8 = cdim_$6(w_$7 != null ? w_$7 : 0);
var hp_$9 = cdim_$6(ht_$3);
if (this.scrollevents) {
ht_$3 += this.quirks.scrollbar_width
};
hp_$9 = cdim_$6(ht_$3);
if (style_$5.height != hp_$9) {
scrolldiv_$4.style.height = cdim_$6(ht_$3);
if (this.scrollevents) {
scrolldiv_$4.style.clip = "rect(0 " + wp_$8 + " " + hp_$9 + " 0)"
};
this.__updatefieldsize()
};
return nh_$2
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setHeight";
return $lzsc$temp
})();
LzTextSprite.prototype.enableClickableLinks = (function  () {
var $lzsc$temp = function  (enabled_$1) {

};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.enableClickableLinks";
return $lzsc$temp
})();
LzTextSprite.prototype.makeTextLink = (function  () {
var $lzsc$temp = function  (str_$1, value_$2) {
LzTextSprite.addLinkID(this.owner);
var uid_$3 = this.owner.getUID();
return '<span class="lztextlink" onclick="javascript:$modules.lz.__callTextLink(\'' + uid_$3 + "', '" + value_$2 + "')\">" + str_$1 + "</span>"
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.makeTextLink";
return $lzsc$temp
})();
$modules.lz.__callTextLink = (function  () {
var $lzsc$temp = function  (viewID_$1, val_$2) {
var view_$3 = LzTextSprite.linkIDMap[viewID_$1];
if (view_$3 != null) {
view_$3.ontextlink.sendEvent(val_$2)
}};
$lzsc$temp["displayName"] = "$modules.lz.__callTextLink";
return $lzsc$temp
})();
LzTextSprite.linkIDMap = [];
LzTextSprite.addLinkID = (function  () {
var $lzsc$temp = function  (view_$1) {
LzTextSprite.linkIDMap[view_$1.getUID()] = view_$1
};
$lzsc$temp["displayName"] = "LzTextSprite.addLinkID";
return $lzsc$temp
})();
LzTextSprite.deleteLinkID = (function  () {
var $lzsc$temp = function  (UID_$1) {
delete LzTextSprite.linkIDMap[UID_$1]
};
$lzsc$temp["displayName"] = "LzTextSprite.deleteLinkID";
return $lzsc$temp
})();
LzTextSprite.prototype._viewdestroy = LzSprite.prototype.destroy;
LzTextSprite.prototype.destroy = (function  () {
var $lzsc$temp = function  () {
LzTextSprite.deleteLinkID(this.owner.getUID());
this._viewdestroy()
};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.destroy";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextAlign = (function  () {
var $lzsc$temp = function  (align_$1) {
if (this._textAlign != align_$1) {
this._textAlign = align_$1;
this.scrolldiv.style.textAlign = align_$1
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setTextAlign";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextIndent = (function  () {
var $lzsc$temp = function  (indent_$1) {
var indentPx_$2 = this.CSSDimension(indent_$1);
if (this._textIndent != indentPx_$2) {
var negInd_$3 = indent_$1 < 0 || this.__LZtextIndent < 0;
this._textIndent = indentPx_$2;
this.__LZtextIndent = indent_$1;
this.scrolldiv.style.textIndent = indentPx_$2;
if (negInd_$3) {
this.scrolldiv.style.paddingLeft = indent_$1 >= 0 ? "" : indentPx_$2.substr(1);
this.setWidth(this.width, true)
}}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setTextIndent";
return $lzsc$temp
})();
LzTextSprite.prototype.setLetterSpacing = (function  () {
var $lzsc$temp = function  (spacing_$1) {
spacing_$1 = this.CSSDimension(spacing_$1);
if (this._letterSpacing != spacing_$1) {
this._letterSpacing = spacing_$1;
this.scrolldiv.style.letterSpacing = spacing_$1
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setLetterSpacing";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextDecoration = (function  () {
var $lzsc$temp = function  (decoration_$1) {
if (this._textDecoration != decoration_$1) {
this._textDecoration = decoration_$1;
this.scrolldiv.style.textDecoration = decoration_$1
}};
$lzsc$temp["displayName"] = "LzTextSprite.prototype.setTextDecoration";
return $lzsc$temp
})();
var LzInputTextSprite = (function  () {
var $lzsc$temp = function  (owner_$1) {
if (owner_$1 == null) {
return
};
this.constructor = arguments.callee;
this.owner = owner_$1;
this.uid = LzSprite.prototype.uid++;
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lzinputtextcontainer";
this.__LZdiv.owner = this;
this.dragging = false;
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv = document.createElement("div");
this.__LZclickcontainerdiv.className = "lzinputtextcontainer_click";
this.__LZclickcontainerdiv.owner = this
};
if (this.quirks.ie_leak_prevention) {
this.__sprites[this.uid] = this
};
this.__createInputText()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#12/25";
return $lzsc$temp
})();
LzInputTextSprite.prototype = new LzTextSprite(null);
LzInputTextSprite.prototype._dbg_typename = "LzInputTextSprite";
LzInputTextSprite.prototype.__lastshown = null;
LzInputTextSprite.prototype.__focusedSprite = null;
LzInputTextSprite.prototype.__lastfocus = null;
LzInputTextSprite.prototype._cancelfocus = false;
LzInputTextSprite.prototype._cancelblur = false;
LzInputTextSprite.prototype.____crregexp = new RegExp("\\r\\n", "g");
LzInputTextSprite.prototype.__createInputText = (function  () {
var $lzsc$temp = function  (t_$1) {
if (this.__LzInputDiv) {
return
};
var type_$2 = "";
if (this.owner) {
if (this.owner.password) {
type_$2 = "password"
} else {
if (this.owner.multiline) {
type_$2 = "multiline"
}}};
this.__createInputDiv(type_$2);
if (t_$1 == null) {
t_$1 = ""
};
lz.embed.__setAttr(this.__LzInputDiv, "value", t_$1);
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable) {
this.__LZinputclickdiv = document.createElement("img");
this.__LZinputclickdiv.src = lz.embed.options.serverroot + LzSprite.prototype.blankimage
} else {
this.__LZinputclickdiv = document.createElement("div")
};
this.__LZinputclickdiv.className = "lzclickdiv";
this.__LZinputclickdiv.owner = this;
if (this.quirks.ie_mouse_events) {
this.__LZinputclickdiv.onmouseenter = this.__handlemouse
} else {
this.__LZinputclickdiv.onmouseover = this.__handlemouse
};
if (this.quirks.input_highlight_bug) {
var ffoxdiv_$3 = document.createElement("div");
ffoxdiv_$3.style.backgroundColor = "white";
ffoxdiv_$3.style.width = "0px";
this.__LZclickcontainerdiv.appendChild(ffoxdiv_$3);
ffoxdiv_$3.appendChild(this.__LZinputclickdiv)
} else {
this.__LZclickcontainerdiv.appendChild(this.__LZinputclickdiv)
}};
this.__LZdiv.appendChild(this.__LzInputDiv);
this.__setTextEvents(true)
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__createInputText";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__createInputDiv = (function  () {
var $lzsc$temp = function  (type_$1) {
if (type_$1 === "password") {
this.multiline = false;
this.__LzInputDiv = document.createElement("input");
this.__LZdiv.className = "lzinputtextcontainer";
lz.embed.__setAttr(this.__LzInputDiv, "type", "password")
} else {
if (type_$1 === "multiline") {
this.multiline = true;
this.__LzInputDiv = document.createElement("textarea");
this.__LZdiv.className = "lzinputtextmultilinecontainer"
} else {
this.multiline = false;
this.__LzInputDiv = document.createElement("input");
this.__LZdiv.className = "lzinputtextcontainer";
lz.embed.__setAttr(this.__LzInputDiv, "type", "text")
}};
if (this.quirks.fix_clickable) {
this.__LZclickcontainerdiv.className = this.__LZdiv.className + "_click"
};
if (this.quirks.firefox_autocomplete_bug) {
lz.embed.__setAttr(this.__LzInputDiv, "autocomplete", "off")
};
this.__LzInputDiv.owner = this;
if (this.quirks.emulate_flash_font_metrics) {
if (this.owner && this.owner.multiline) {
this.__LzInputDiv.className = "lzswfinputtextmultiline"
} else {
this.__LzInputDiv.className = "lzswfinputtext"
}} else {
this.__LzInputDiv.className = "lzinputtext"
};
if (this.owner) {
lz.embed.__setAttr(this.__LzInputDiv, "name", this.owner.name)
};
this.scrolldiv = this.__LzInputDiv;
this.scrolldiv.owner = this
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__createInputDiv";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setMultiline = (function  () {
var $lzsc$temp = function  (ml_$1) {
var oldval_$2 = this.multiline;
this.multiline = ml_$1 == true;
if (oldval_$2 != null && this.multiline != oldval_$2) {
var olddiv_$3 = this.__LzInputDiv;
this.__setTextEvents(false);
this.__createInputDiv(ml_$1 ? "multiline" : "");
var newdiv_$4 = this.__LzInputDiv;
lz.embed.__setAttr(newdiv_$4, "style", olddiv_$3.style.cssText);
var oldleft_$5 = olddiv_$3.scrollLeft;
var oldtop_$6 = olddiv_$3.scrollTop;
this.__discardElement(olddiv_$3);
this.__LZdiv.appendChild(newdiv_$4);
this.setScrollEvents(this.owner.scrollevents);
newdiv_$4.scrollLeft = oldleft_$5;
newdiv_$4.scrollTop = oldtop_$6;
this.__setTextEvents(true);
this.setText(this.text, true)
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setMultiline";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__handlemouse = (function  () {
var $lzsc$temp = function  (e_$1) {
var sprite_$2 = this.owner;
if (!sprite_$2 || !sprite_$2.owner || sprite_$2.selectable != true) {
return
};
if (sprite_$2.__fix_inputtext_with_parent_resource) {
if (!this.__shown) {
sprite_$2.setClickable(true);
sprite_$2.select()
}} else {
sprite_$2.__show()
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__handlemouse";
return $lzsc$temp
})();
LzInputTextSprite.prototype.init = (function  () {
var $lzsc$temp = function  (v_$1) {
this.setVisible(v_$1);
if (this.quirks["fix_inputtext_with_parent_resource"]) {
var sprites_$2 = this.__findParents("clickable", true);
var l_$3 = sprites_$2.length;
if (l_$3) {
for (var n_$4 = 0;n_$4 < l_$3;n_$4++) {
var v_$1 = sprites_$2[n_$4];
if (v_$1.resource != null) {
this.setClickable(true);
this.__fix_inputtext_with_parent_resource = true
}}}}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.init";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__show = (function  () {
var $lzsc$temp = function  () {
if (this.__shown == true || this.disabled == true) {
return
};
this.__hideIfNotFocused();
LzInputTextSprite.prototype.__lastshown = this;
this.__shown = true;
if (this.quirks["inputtext_parents_cannot_contain_clip"]) {
var sprites_$1 = this.__findParents("clip", true);
var l_$2 = sprites_$1.length;
if (l_$2 > 1) {
if (this._shownclipvals == null) {
this._shownclipvals = [];
this._shownclippedsprites = sprites_$1;
for (var n_$3 = 0;n_$3 < l_$2;n_$3++) {
var v_$4 = sprites_$1[n_$3];
this._shownclipvals[n_$3] = v_$4.__LZclickcontainerdiv.style.clip;
v_$4.__LZclickcontainerdiv.style.clip = "rect(auto auto auto auto)"
}}}};
LzMouseKernel.setGlobalClickable(false);
if (LzSprite.quirks.prevent_selection) {
this.__LZdiv.onselectstart = null
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__show";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__hideIfNotFocused = (function  () {
var $lzsc$temp = function  (eventname_$1, target_$2) {
var lzinppr_$3 = LzInputTextSprite.prototype;
if (lzinppr_$3.__lastshown == null) {
return
};
var quirks_$4 = LzSprite.quirks;
if (quirks_$4.textgrabsinputtextfocus) {
var s_$5 = window.event;
if (s_$5 && s_$5.srcElement && s_$5.srcElement.owner && s_$5.srcElement.owner instanceof LzTextSprite) {
if (eventname_$1 == "onmousedown") {
lzinppr_$3.__lastshown.gotFocus()
};
return
}};
if (lzinppr_$3.__focusedSprite != lzinppr_$3.__lastshown) {
lzinppr_$3.__lastshown.__hide()
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__hideIfNotFocused";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__hide = (function  () {
var $lzsc$temp = function  (ignore_$1) {
if (this.__shown != true || this.disabled == true) {
return
};
if (LzInputTextSprite.prototype.__lastshown == this) {
LzInputTextSprite.prototype.__lastshown = null
};
this.__shown = false;
if (this.quirks["inputtext_parents_cannot_contain_clip"]) {
if (this._shownclipvals != null) {
for (var n_$2 = 0;n_$2 < this._shownclipvals.length;n_$2++) {
var v_$3 = this._shownclippedsprites[n_$2];
v_$3.__LZclickcontainerdiv.style.clip = this._shownclipvals[n_$2]
};
this._shownclipvals = null;
this._shownclippedsprites = null
}};
LzMouseKernel.setGlobalClickable(true);
if (this.__fix_inputtext_with_parent_resource) {
this.setClickable(false)
};
if (LzInputTextSprite.prototype.__lastshown == null) {
if (LzSprite.quirks.prevent_selection) {
this.__LZdiv.onselectstart = LzTextSprite.prototype.__cancelhandler
}}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__hide";
return $lzsc$temp
})();
LzInputTextSprite.prototype.gotBlur = (function  () {
var $lzsc$temp = function  () {
if (LzInputTextSprite.prototype.__focusedSprite != this) {
return
};
this.deselect()
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.gotBlur";
return $lzsc$temp
})();
LzInputTextSprite.prototype.gotFocus = (function  () {
var $lzsc$temp = function  () {
if (LzInputTextSprite.prototype.__focusedSprite == this) {
return
};
this.select()
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.gotFocus";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setText = (function  () {
var $lzsc$temp = function  (t_$1) {
if (this.capabilities["htmlinputtext"]) {
if (t_$1.indexOf("<br/>") != -1) {
t_$1 = t_$1.replace(this.br_to_newline_re, "\r")
}};
this.text = t_$1;
this.__createInputText(t_$1);
this.__LzInputDiv.value = t_$1;
this.__updatefieldsize()
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setText";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__setTextEvents = (function  () {
var $lzsc$temp = function  (c_$1) {
var div_$2 = this.__LzInputDiv;
var f_$3 = c_$1 ? this.__textEvent : null;
div_$2.onblur = f_$3;
div_$2.onfocus = f_$3;
if (this.quirks.ie_mouse_events) {
div_$2.ondrag = f_$3;
div_$2.ondblclick = f_$3;
div_$2.onmouseenter = f_$3;
div_$2.onmouseleave = f_$3
} else {
div_$2.onmouseover = f_$3;
div_$2.onmouseout = f_$3
};
div_$2.onmousemove = f_$3;
div_$2.onmousedown = f_$3;
div_$2.onmouseup = f_$3;
div_$2.onclick = f_$3;
div_$2.onkeyup = f_$3;
div_$2.onkeydown = f_$3;
div_$2.onkeypress = f_$3;
div_$2.onchange = f_$3;
if (this.quirks.ie_paste_event || this.quirks.safari_paste_event) {
div_$2.onpaste = c_$1 ? (function  () {
var $lzsc$temp = function  (e_$1) {
this.owner.__pasteHandlerEx(e_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#408/27";
return $lzsc$temp
})() : null
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__setTextEvents";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__pasteHandlerEx = (function  () {
var $lzsc$temp = function  (evt_$1) {
var checkre_$2 = !(!this.restrict);
var checkml_$3 = this.multiline && this.owner.maxlength > 0;
if (checkre_$2 || checkml_$3) {
evt_$1 = evt_$1 ? evt_$1 : window.event;
if (this.quirks.safari_paste_event) {
var txt_$4 = evt_$1.clipboardData.getData("text/plain")
} else {
var txt_$4 = window.clipboardData.getData("TEXT");
txt_$4 = txt_$4.replace(this.____crregexp, "\n")
};
var stopPaste_$5 = false;
var selsize_$6 = this.getSelectionSize();
if (selsize_$6 < 0) {
selsize_$6 = 0
};
if (checkre_$2) {
var matched_$7 = txt_$4.match(this.restrict);
if (matched_$7 == null) {
var newtxt_$8 = ""
} else {
var newtxt_$8 = matched_$7.join("")
};
stopPaste_$5 = newtxt_$8 != txt_$4;
txt_$4 = newtxt_$8
};
if (checkml_$3) {
var max_$9 = this.owner.maxlength + selsize_$6;
if (this.quirks.text_ie_carriagereturn) {
var len_$10 = this.__LzInputDiv.value.replace(this.____crregexp, "\n").length
} else {
var len_$10 = this.__LzInputDiv.value.length
};
var maxchars_$11 = max_$9 - len_$10;
if (maxchars_$11 > 0) {
if (txt_$4.length > maxchars_$11) {
txt_$4 = txt_$4.substring(0, maxchars_$11);
stopPaste_$5 = true
}} else {
txt_$4 = "";
stopPaste_$5 = true
}};
if (stopPaste_$5) {
evt_$1.returnValue = false;
if (evt_$1.preventDefault) {
evt_$1.preventDefault()
};
if (txt_$4.length > 0) {
if (this.quirks.safari_paste_event) {
var val_$12 = this.__LzInputDiv.value;
var selpos_$13 = this.getSelectionPosition();
this.__LzInputDiv.value = val_$12.substring(0, selpos_$13) + txt_$4 + val_$12.substring(selpos_$13 + selsize_$6);
selpos_$13 += txt_$4.length;
this.__LzInputDiv.setSelectionRange(selpos_$13, selpos_$13)
} else {
var range_$14 = document.selection.createRange();
range_$14.text = txt_$4
}}}}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__pasteHandlerEx";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__pasteHandler = (function  () {
var $lzsc$temp = function  () {
var selpos = this.getSelectionPosition();
var selsize = this.getSelectionSize();
var val = this.__LzInputDiv.value;
var that = this;
setTimeout((function  () {
var $lzsc$temp = function  () {
var checkre_$1 = !(!that.restrict);
var checkml_$2 = that.multiline && that.owner.maxlength > 0;
var newval_$3 = that.__LzInputDiv.value;
var newlen_$4 = newval_$3.length;
var max_$5 = that.owner.maxlength;
if (checkre_$1 || checkml_$2 && newlen_$4 > max_$5) {
var len_$6 = val.length;
var newc_$7 = newval_$3.substr(selpos, newlen_$4 - len_$6 + selsize);
if (checkre_$1) {
var matched_$8 = newc_$7.match(that.restrict);
newc_$7 = matched_$8 != null ? matched_$8.join("") : ""
};
if (checkml_$2) {
var maxchars_$9 = max_$5 + selsize - len_$6;
newc_$7 = newc_$7.substring(0, maxchars_$9)
};
that.__LzInputDiv.value = val.substring(0, selpos) + newc_$7 + val.substring(selpos + selsize);
selpos += newc_$7.length;
that.__LzInputDiv.setSelectionRange(selpos, selpos)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#495/16";
return $lzsc$temp
})(), 1)
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__pasteHandler";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__textEvent = (function  () {
var $lzsc$temp = function  (evt_$1) {
if (!evt_$1) {
evt_$1 = window.event
};
var sprite_$2 = this.owner;
var view_$3 = this.owner.owner;
if (sprite_$2.__LZdeleted == true) {
return
};
if (sprite_$2.__skipevent) {
sprite_$2.__skipevent = false;
return
};
var eventname_$4 = "on" + evt_$1.type;
var quirks_$5 = sprite_$2.quirks;
LzMouseKernel.__sendMouseMove(evt_$1);
if (quirks_$5.ie_mouse_events) {
if (eventname_$4 == "onmouseenter") {
eventname_$4 = "onmouseover"
} else {
if (eventname_$4 == "onmouseleave") {
eventname_$4 = "onmouseout"
} else {
if (eventname_$4 == "ondblclick") {
if (sprite_$2.clickable) {
sprite_$2.__mouseEvent("onmousedown", true);
sprite_$2.__mouseEvent("onmouseup", true);
sprite_$2.__mouseEvent("onclick", true)
};
return false
} else {
if (eventname_$4 == "ondrag") {
return false
}}}}};
if (quirks_$5.autoscroll_textarea) {
if (eventname_$4 == "onmousedown") {
sprite_$2.dragging = true
} else {
if (eventname_$4 == "onmouseup" || eventname_$4 == "onmouseout") {
sprite_$2.dragging = false
}}};
if (sprite_$2.__shown != true) {
if (eventname_$4 == "onfocus") {
sprite_$2.__skipevent = true;
sprite_$2.__show();
sprite_$2.__LzInputDiv.blur();
LzInputTextSprite.prototype.__lastfocus = sprite_$2;
LzKeyboardKernel.setKeyboardControl(true)
}};
if (eventname_$4 == "onfocus" || eventname_$4 == "onmousedown") {
if (eventname_$4 == "onfocus") {
LzMouseKernel.setGlobalClickable(false)
};
LzInputTextSprite.prototype.__focusedSprite = sprite_$2;
sprite_$2.__show();
if (eventname_$4 == "onfocus" && sprite_$2._cancelfocus) {
sprite_$2._cancelfocus = false;
return
};
if (window["LzKeyboardKernel"]) {
LzKeyboardKernel.__cancelKeys = false
}} else {
if (eventname_$4 == "onblur") {
if (window["LzKeyboardKernel"]) {
LzKeyboardKernel.__cancelKeys = true
};
if (LzInputTextSprite.prototype.__focusedSprite === sprite_$2) {
LzInputTextSprite.prototype.__focusedSprite = null
};
if (sprite_$2.__fix_inputtext_with_parent_resource && sprite_$2.__isMouseOver()) {
sprite_$2.select();
return
};
sprite_$2.__hide();
if (sprite_$2._cancelblur) {
sprite_$2._cancelblur = false;
return
}} else {
if (eventname_$4 == "onmousemove") {
if (quirks_$5.autoscroll_textarea && sprite_$2.dragging) {
var d_$6 = sprite_$2.__LzInputDiv;
var y_$7 = evt_$1.pageY - d_$6.offsetTop;
if (y_$7 <= 3) {
d_$6.scrollTop -= sprite_$2.lineHeight ? sprite_$2.lineHeight : 10
};
if (y_$7 >= d_$6.clientHeight - 3) {
d_$6.scrollTop += sprite_$2.lineHeight ? sprite_$2.lineHeight : 10
}};
return
} else {
if (eventname_$4 == "onkeypress") {
if (sprite_$2.restrict || sprite_$2.multiline && view_$3.maxlength && view_$3.maxlength < Infinity) {
var keycode_$8 = evt_$1.keyCode;
var charcode_$9 = quirks_$5.text_event_charcode ? evt_$1.charCode : evt_$1.keyCode;
var validChar_$10 = !(evt_$1.ctrlKey || evt_$1.altKey) && (charcode_$9 >= 32 || keycode_$8 == 13);
if (validChar_$10) {
var prevent_$11 = false;
if (keycode_$8 != 13 && sprite_$2.restrict) {
prevent_$11 = 0 > String.fromCharCode(charcode_$9).search(sprite_$2.restrict)
};
if (!prevent_$11) {
var selsize_$12 = sprite_$2.getSelectionSize();
if (selsize_$12 <= 0) {
if (quirks_$5.text_ie_carriagereturn) {
var val_$13 = sprite_$2.__LzInputDiv.value.replace(sprite_$2.____crregexp, "\n")
} else {
var val_$13 = sprite_$2.__LzInputDiv.value
};
var len_$14 = val_$13.length, max_$15 = view_$3.maxlength;
if (len_$14 >= max_$15) {
prevent_$11 = true
}}};
if (prevent_$11) {
evt_$1.returnValue = false;
if (evt_$1.preventDefault) {
evt_$1.preventDefault()
}}} else {
if (quirks_$5.keypress_function_keys) {
var ispaste_$16 = false;
if (evt_$1.ctrlKey && !evt_$1.altKey && !evt_$1.shiftKey) {
var c_$17 = String.fromCharCode(charcode_$9);
ispaste_$16 = c_$17 == "v" || c_$17 == "V"
} else {
if (evt_$1.shiftKey && !evt_$1.altKey && !evt_$1.ctrlKey) {
ispaste_$16 = keycode_$8 == 45
}};
if (ispaste_$16) {
if (sprite_$2.restrict) {
sprite_$2.__pasteHandler()
} else {
var len_$14 = sprite_$2.__LzInputDiv.value.length, max_$15 = view_$3.maxlength;
if (len_$14 < max_$15 || sprite_$2.getSelectionSize() > 0) {
sprite_$2.__pasteHandler()
} else {
evt_$1.returnValue = false;
if (evt_$1.preventDefault) {
evt_$1.preventDefault()
}}}}}};
sprite_$2.__updatefieldsize()
};
return
}}}};
if (view_$3) {
if (eventname_$4 == "onkeydown" || eventname_$4 == "onkeyup") {
var d_$6 = sprite_$2.__LzInputDiv;
var v_$18 = d_$6.value;
if (v_$18 != sprite_$2.text) {
sprite_$2.text = v_$18;
sprite_$2.__updatefieldsize();
view_$3.inputtextevent("onchange", v_$18)
};
if (quirks_$5.autoscroll_textarea && eventname_$4 == "onkeydown" && d_$6.selectionStart == v_$18.length) {
d_$6.scrollTop = d_$6.scrollHeight - d_$6.clientHeight + 20
}} else {
if (eventname_$4 == "onmousedown" || eventname_$4 == "onmouseup" || eventname_$4 == "onmouseover" || eventname_$4 == "onmouseout" || eventname_$4 == "onclick") {
sprite_$2.__mouseEvent(evt_$1);
evt_$1.cancelBubble = true;
if (eventname_$4 == "onmouseout") {
if (!sprite_$2.__isMouseOver()) {
sprite_$2.__hide()
}};
return
};
view_$3.inputtextevent(eventname_$4)
}}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__textEvent";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setClickable = (function  () {
var $lzsc$temp = function  (val_$1) {
this.clickable = val_$1
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setClickable";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setEnabled = (function  () {
var $lzsc$temp = function  (val_$1) {
this.disabled = !val_$1;
this.__LzInputDiv.disabled = this.disabled
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setEnabled";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setMaxLength = (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 == Infinity) {
val_$1 = ~0 >>> 1
};
this.__LzInputDiv.maxLength = val_$1
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setMaxLength";
return $lzsc$temp
})();
LzInputTextSprite.prototype.select = (function  () {
var $lzsc$temp = function  () {
this.__show();
try {
this.__LzInputDiv.focus()
}
catch (err) {

};
LzInputTextSprite.prototype.__lastfocus = this;
setTimeout(LzInputTextSprite.prototype.__selectLastFocused, 50);
if (window["LzKeyboardKernel"]) {
LzKeyboardKernel.__cancelKeys = false
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.select";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__selectLastFocused = (function  () {
var $lzsc$temp = function  () {
if (LzInputTextSprite.prototype.__lastfocus != null) {
LzInputTextSprite.prototype.__lastfocus.__LzInputDiv.select()
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.__selectLastFocused";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setSelection = (function  () {
var $lzsc$temp = function  (start_$1, end_$2) {
switch (arguments.length) {
case 1:
end_$2 = null
};
if (end_$2 == null) {
end_$2 = start_$1
};
this._cancelblur = true;
this.__show();
LzInputTextSprite.prototype.__lastfocus = this;
if (this.quirks["text_selection_use_range"]) {
var range_$3 = this.__LzInputDiv.createTextRange();
var val_$4 = this.__LzInputDiv.value;
if (start_$1 > end_$2) {
var st_$5 = start_$1;
start_$1 = end_$2;
end_$2 = st_$5
};
if (this.multiline) {
var offset_$6 = 0;
var startcounter_$7 = 0;
while (offset_$6 < start_$1) {
offset_$6 = val_$4.indexOf("\r\n", offset_$6 + 2);
if (offset_$6 == -1) {
break
};
startcounter_$7++
};
var midcounter_$8 = 0;
while (offset_$6 < end_$2) {
offset_$6 = val_$4.indexOf("\r\n", offset_$6 + 2);
if (offset_$6 == -1) {
break
};
midcounter_$8++
};
var endcounter_$9 = 0;
while (offset_$6 < val_$4.length) {
offset_$6 = val_$4.indexOf("\r\n", offset_$6 + 2);
if (offset_$6 == -1) {
break
};
endcounter_$9++
};
var tl_$10 = range_$3.text.length;
var st_$5 = start_$1;
var ed_$11 = end_$2 - val_$4.length + startcounter_$7 + midcounter_$8 + endcounter_$9 + 1
} else {
var st_$5 = start_$1;
var ed_$11 = end_$2 - range_$3.text.length
};
range_$3.moveStart("character", st_$5);
range_$3.moveEnd("character", ed_$11);
range_$3.select()
} else {
this.__LzInputDiv.setSelectionRange(start_$1, end_$2)
};
this.__LzInputDiv.focus();
if (window["LzKeyboardKernel"]) {
LzKeyboardKernel.__cancelKeys = false
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setSelection";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getSelectionPosition = (function  () {
var $lzsc$temp = function  () {
if (!this.__shown || this.disabled == true) {
return -1
};
if (this.quirks["text_selection_use_range"]) {
if (this.multiline) {
var p_$1 = this._getTextareaSelection()
} else {
var p_$1 = this._getTextSelection()
};
if (p_$1) {
return p_$1.start
} else {
return -1
}} else {
return this.__LzInputDiv.selectionStart
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.getSelectionPosition";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getSelectionSize = (function  () {
var $lzsc$temp = function  () {
if (!this.__shown || this.disabled == true) {
return -1
};
if (this.quirks["text_selection_use_range"]) {
if (this.multiline) {
var p_$1 = this._getTextareaSelection()
} else {
var p_$1 = this._getTextSelection()
};
if (p_$1) {
return p_$1.end - p_$1.start
} else {
return -1
}} else {
return this.__LzInputDiv.selectionEnd - this.__LzInputDiv.selectionStart
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.getSelectionSize";
return $lzsc$temp
})();
if (LzSprite.quirks["text_selection_use_range"]) {
LzInputTextSprite.prototype._getTextSelection = (function  () {
var $lzsc$temp = function  () {
this.__LzInputDiv.focus();
var range_$1 = document.selection.createRange();
var bookmark_$2 = range_$1.getBookmark();
var originalContents_$3 = contents = this.__LzInputDiv.value;
do{
var marker_$4 = "~~~" + Math.random() + "~~~"
} while (contents.indexOf(marker_$4) != -1);
var parent_$5 = range_$1.parentElement();
if (parent_$5 == null || !(parent_$5.type == "text" || parent_$5.type == "textarea")) {
return
};
range_$1.text = marker_$4 + range_$1.text + marker_$4;
contents = this.__LzInputDiv.value;
var result_$6 = {};
result_$6.start = contents.indexOf(marker_$4);
contents = contents.replace(marker_$4, "");
result_$6.end = contents.indexOf(marker_$4);
this.__LzInputDiv.value = originalContents_$3;
range_$1.moveToBookmark(bookmark_$2);
range_$1.select();
return result_$6
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype._getTextSelection";
return $lzsc$temp
})();
LzInputTextSprite.prototype._getTextareaSelection = (function  () {
var $lzsc$temp = function  () {
var textarea_$1 = this.__LzInputDiv;
var selection_range_$2 = document.selection.createRange().duplicate();
if (selection_range_$2.parentElement() == textarea_$1) {
var before_range_$3 = document.body.createTextRange();
before_range_$3.moveToElementText(textarea_$1);
before_range_$3.setEndPoint("EndToStart", selection_range_$2);
var after_range_$4 = document.body.createTextRange();
after_range_$4.moveToElementText(textarea_$1);
after_range_$4.setEndPoint("StartToEnd", selection_range_$2);
var before_finished_$5 = false, selection_finished_$6 = false, after_finished_$7 = false;
var before_text_$8, untrimmed_before_text_$9, selection_text_$10, untrimmed_selection_text_$11, after_text_$12, untrimmed_after_text_$13;
before_text_$8 = untrimmed_before_text_$9 = before_range_$3.text;
selection_text_$10 = untrimmed_selection_text_$11 = selection_range_$2.text;
after_text_$12 = untrimmed_after_text_$13 = after_range_$4.text;
do{
if (!before_finished_$5) {
if (before_range_$3.compareEndPoints("StartToEnd", before_range_$3) == 0) {
before_finished_$5 = true
} else {
before_range_$3.moveEnd("character", -1);
if (before_range_$3.text == before_text_$8) {
untrimmed_before_text_$9 += "\r\n"
} else {
before_finished_$5 = true
}}};
if (!selection_finished_$6) {
if (selection_range_$2.compareEndPoints("StartToEnd", selection_range_$2) == 0) {
selection_finished_$6 = true
} else {
selection_range_$2.moveEnd("character", -1);
if (selection_range_$2.text == selection_text_$10) {
untrimmed_selection_text_$11 += "\r\n"
} else {
selection_finished_$6 = true
}}};
if (!after_finished_$7) {
if (after_range_$4.compareEndPoints("StartToEnd", after_range_$4) == 0) {
after_finished_$7 = true
} else {
after_range_$4.moveEnd("character", -1);
if (after_range_$4.text == after_text_$12) {
untrimmed_after_text_$13 += "\r\n"
} else {
after_finished_$7 = true
}}}} while (!before_finished_$5 || !selection_finished_$6 || !after_finished_$7);
var untrimmed_text_$14 = untrimmed_before_text_$9 + untrimmed_selection_text_$11 + untrimmed_after_text_$13;
var untrimmed_successful_$15 = false;
if (textarea_$1.value == untrimmed_text_$14) {
untrimmed_successful_$15 = true
};
var startPoint_$16 = untrimmed_before_text_$9.length;
var endPoint_$17 = startPoint_$16 + untrimmed_selection_text_$11.length;
var selected_text_$18 = untrimmed_selection_text_$11;
var val_$19 = this.__LzInputDiv.value;
var offset_$20 = 0;
var startcounter_$21 = 0;
while (offset_$20 < startPoint_$16) {
offset_$20 = val_$19.indexOf("\r\n", offset_$20 + 2);
if (offset_$20 == -1) {
break
};
startcounter_$21++
};
var midcounter_$22 = 0;
while (offset_$20 < endPoint_$17) {
offset_$20 = val_$19.indexOf("\r\n", offset_$20 + 2);
if (offset_$20 == -1) {
break
};
midcounter_$22++
};
var endcounter_$23 = 0;
while (offset_$20 < val_$19.length) {
offset_$20 = val_$19.indexOf("\r\n", offset_$20 + 2);
if (offset_$20 == -1) {
break
};
endcounter_$23++
};
startPoint_$16 -= startcounter_$21;
endPoint_$17 -= midcounter_$22 + startcounter_$21;
return {start: startPoint_$16, end: endPoint_$17}}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype._getTextareaSelection";
return $lzsc$temp
})()
};
LzInputTextSprite.prototype.deselect = (function  () {
var $lzsc$temp = function  () {
this.__hide();
if (this.__LzInputDiv && this.__LzInputDiv.blur) {
this.__LzInputDiv.blur()
};
if (window["LzKeyboardKernel"]) {
LzKeyboardKernel.__cancelKeys = true
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.deselect";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__fontStyle = "normal";
LzInputTextSprite.prototype.__fontWeight = "normal";
LzInputTextSprite.prototype.__fontSize = "11px";
LzInputTextSprite.prototype.__fontFamily = "Verdana,Vera,sans-serif";
LzInputTextSprite.prototype.__setFontSize = LzTextSprite.prototype.setFontSize;
LzInputTextSprite.prototype.setFontSize = (function  () {
var $lzsc$temp = function  (fsize_$1) {
this.__setFontSize(fsize_$1);
if (this.__fontSize != this._fontSize) {
this.__fontSize = this._fontSize;
this.__LzInputDiv.style.fontSize = this._fontSize
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setFontSize";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__setFontStyle = LzTextSprite.prototype.setFontStyle;
LzInputTextSprite.prototype.setFontStyle = (function  () {
var $lzsc$temp = function  (fstyle_$1) {
this.__setFontStyle(fstyle_$1);
if (this.__fontStyle != this._fontStyle) {
this.__fontStyle = this._fontStyle;
this.__LzInputDiv.style.fontStyle = this._fontStyle
};
if (this.__fontWeight != this._fontWeight) {
this.__fontWeight = this._fontWeight;
this.__LzInputDiv.style.fontWeight = this._fontWeight
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setFontStyle";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__setFontName = LzTextSprite.prototype.setFontName;
LzInputTextSprite.prototype.setFontName = (function  () {
var $lzsc$temp = function  (fname_$1) {
this.__setFontName(fname_$1);
if (this.__fontFamily != this._fontFamily) {
this.__fontFamily = this._fontFamily;
this.__LzInputDiv.style.fontFamily = this._fontFamily
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setFontName";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setWidth = (function  () {
var $lzsc$temp = function  (w_$1) {
if (w_$1 == null || w_$1 < 0 || isNaN(w_$1)) {
return
};
var nw_$2 = LzTextSprite.prototype.setWidth.call(this, w_$1);
if (this.quirks.fix_clickable && nw_$2 != null) {
this.__LZinputclickdiv.style.width = nw_$2
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setWidth";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setHeight = (function  () {
var $lzsc$temp = function  (h_$1) {
if (h_$1 == null || h_$1 < 0 || isNaN(h_$1)) {
return
};
var nh_$2 = LzTextSprite.prototype.setHeight.call(this, h_$1);
if (this.quirks.fix_clickable && nh_$2 != null) {
this.__LZinputclickdiv.style.height = nh_$2
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setHeight";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setColor = (function  () {
var $lzsc$temp = function  (c_$1) {
if (this.color == c_$1) {
return
};
this.color = c_$1;
this.__LzInputDiv.style.color = LzColorUtils.inttohex(c_$1)
};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.setColor";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getText = (function  () {
var $lzsc$temp = function  () {
if (this.multiline && this.quirks.text_ie_carriagereturn) {
return this.__LzInputDiv.value.replace(this.____crregexp, "\n")
} else {
return this.__LzInputDiv.value
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.prototype.getText";
return $lzsc$temp
})();
LzInputTextSprite.findSelection = (function  () {
var $lzsc$temp = function  () {
if (LzInputTextSprite.__focusedSprite && LzInputTextSprite.__focusedSprite.owner) {
return LzInputTextSprite.__focusedSprite.owner
}};
$lzsc$temp["displayName"] = "LzInputTextSprite.findSelection";
return $lzsc$temp
})();
if (LzSprite.quirks.prevent_selection) {
document.onselectstart = (function  () {
var $lzsc$temp = function  (e_$1) {
if (!e_$1) {
e_$1 = window.event;
var targ_$2 = e_$1.srcElement
} else {
var targ_$2 = e_$1.srcElement.parentNode
};
if (targ_$2.owner instanceof LzTextSprite) {
if (!targ_$2.owner.selectable) {
return false
}} else {
return false
}};
$lzsc$temp["displayName"] = "document.onselectstart";
return $lzsc$temp
})()
};
var LzXMLParser = {parseXML: (function  () {
var $lzsc$temp = function  (str_$1, trimwhitespace_$2, nsprefix_$3) {
var parser_$4 = new DOMParser();
var doc_$5 = parser_$4.parseFromString(str_$1, "text/xml");
var err_$6 = this.getParserError(doc_$5);
if (err_$6) {
throw new Error(err_$6)
} else {
return doc_$5.firstChild
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#15/15";
return $lzsc$temp
})(), getParserError: (function  () {
var $lzsc$temp = function  (doc_$1) {
var browser_$2 = lz.embed.browser;
if (browser_$2.isIE) {
return this.__checkIE(doc_$1)
} else {
if (browser_$2.isFirefox || browser_$2.isOpera) {
return this.__checkFirefox(doc_$1)
} else {
if (browser_$2.isSafari) {
return this.__checkSafari(doc_$1)
}}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#30/21";
return $lzsc$temp
})(), __checkIE: (function  () {
var $lzsc$temp = function  (doc_$1) {
var perr_$2 = doc_$1.parseError;
if (perr_$2.errorCode != 0) {
return perr_$2.reason
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#41/16";
return $lzsc$temp
})(), __checkFirefox: (function  () {
var $lzsc$temp = function  (doc_$1) {
var c_$2 = doc_$1.documentElement;
if (c_$2 && c_$2.nodeName == "parsererror") {
var msg_$3 = c_$2.firstChild.nodeValue;
return msg_$3.match(".*")[0]
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#47/21";
return $lzsc$temp
})(), __checkSafari: (function  () {
var $lzsc$temp = function  (doc_$1) {
var c_$2 = doc_$1.documentElement;
if (c_$2 instanceof HTMLElement) {
(c_$2 = c_$2.firstChild) && (c_$2 = c_$2.firstChild)
} else {
c_$2 = c_$2.firstChild
};
if (c_$2 && c_$2.nodeName == "parsererror") {
var msg_$3 = c_$2.childNodes[1].textContent;
return msg_$3.match("[^:]*: (.*)")[1]
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#56/20";
return $lzsc$temp
})()};
if (typeof DOMParser == "undefined") {
var DOMParser = (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#83/21";
return $lzsc$temp
})();
DOMParser.prototype.parseFromString = (function  () {
var $lzsc$temp = function  (str_$1, contentType_$2) {
if (typeof window.ActiveXObject != "undefined") {
var progIDs_$3 = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.3.0", "MSXML.DomDocument"];
var xmlDOM_$4 = null;
for (var i_$5 = 0;i_$5 < progIDs_$3.length;i_$5++) {
try {
xmlDOM_$4 = new ActiveXObject(progIDs_$3[i_$5]);
break
}
catch (ex) {

}};
if (xmlDOM_$4 == null) {
Debug.error("Could not instantiate a XML DOM ActiveXObject")
};
xmlDOM_$4.loadXML(str_$1);
return xmlDOM_$4
} else {
if (typeof XMLHttpRequest != "undefined") {
contentType_$2 = contentType_$2 || "application/xml";
var req_$6 = new XMLHttpRequest();
req_$6.open("GET", "data:" + contentType_$2 + ";charset=utf-8," + encodeURIComponent(str_$1), false);
if (req_$6.overrideMimeType) {
req_$6.overrideMimeType(contentType_$2)
};
req_$6.send(null);
return req_$6.responseXML
}}};
$lzsc$temp["displayName"] = "DOMParser.prototype.parseFromString";
return $lzsc$temp
})()
};
var LzXMLTranslator = {whitespacePat: new RegExp("^\\s*$"), stringTrimPat: new RegExp("^\\s+|\\s+$", "g"), copyXML: (function  () {
var $lzsc$temp = function  (xmldoc_$1, trimwhitespace_$2, nsprefix_$3) {
var lfcnode_$4 = this.copyBrowserXML(xmldoc_$1, true, trimwhitespace_$2, nsprefix_$3);
if (lfcnode_$4 instanceof LzDataElement) {
return lfcnode_$4
} else {
return null
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#22/10";
return $lzsc$temp
})(), copyBrowserNode: (function  () {
var $lzsc$temp = function  (node_$1, ignorewhite_$2, trimwhite_$3, nsprefix_$4) {
var type_$5 = node_$1.nodeType;
if (type_$5 == 3 || type_$5 == 4) {
var nv_$6 = node_$1.nodeValue;
if (!(ignorewhite_$2 && this.whitespacePat.test(nv_$6))) {
if (trimwhite_$3) {
nv_$6 = nv_$6.replace(this.stringTrimPat, "")
};
return new LzDataText(nv_$6)
}} else {
if (type_$5 == 1 || type_$5 == 9) {
var nname_$7 = !nsprefix_$4 && (node_$1.localName || node_$1.baseName) || node_$1.nodeName;
var cattrs_$8 = {};
var nattrs_$9 = node_$1.attributes;
if (nattrs_$9) {
for (var k_$10 = 0, len_$11 = nattrs_$9.length;k_$10 < len_$11;k_$10++) {
var attrnode_$12 = nattrs_$9[k_$10];
if (attrnode_$12) {
var attrname_$13 = !nsprefix_$4 && (attrnode_$12.localName || attrnode_$12.baseName) || attrnode_$12.name;
cattrs_$8[attrname_$13] = attrnode_$12.value
}}};
var lfcnode_$14 = new LzDataElement(nname_$7);
lfcnode_$14.attributes = cattrs_$8;
return lfcnode_$14
} else {

}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#35/18";
return $lzsc$temp
})(), copyBrowserXML: (function  () {
var $lzsc$temp = function  (xmlnode_$1, ignorewhite_$2, trimwhite_$3, nsprefix_$4) {
var document_$5 = new LzDataElement(null);
if (!xmlnode_$1.firstChild) {
return document_$5.appendChild(this.copyBrowserNode(xmlnode_$1, ignorewhite_$2, trimwhite_$3, nsprefix_$4))
};
var wsPat_$6 = this.whitespacePat;
var trimPat_$7 = this.stringTrimPat;
var lfcparent_$8 = document_$5;
var next_$9, node_$10 = xmlnode_$1;
for (;;) {
var type_$11 = node_$10.nodeType;
if (type_$11 == 3 || type_$11 == 4) {
var nv_$12 = node_$10.nodeValue;
if (!(ignorewhite_$2 && wsPat_$6.test(nv_$12))) {
if (trimwhite_$3) {
nv_$12 = nv_$12.replace(trimPat_$7, "")
};
var cnodes_$13 = lfcparent_$8.childNodes;
var last_$14 = cnodes_$13[cnodes_$13.length - 1];
if (last_$14 instanceof LzDataText) {
last_$14.data += nv_$12
} else {
var lfcnode_$15 = new LzDataText(nv_$12);
lfcnode_$15.parentNode = lfcparent_$8;
lfcnode_$15.ownerDocument = document_$5;
lfcnode_$15.__LZo = cnodes_$13.push(lfcnode_$15) - 1
}}} else {
if (type_$11 == 1 || type_$11 == 9) {
var nname_$16 = !nsprefix_$4 && (node_$10.localName || node_$10.baseName) || node_$10.nodeName;
var cattrs_$17 = {};
var nattrs_$18 = node_$10.attributes;
if (nattrs_$18) {
for (var k_$19 = 0, len_$20 = nattrs_$18.length;k_$19 < len_$20;k_$19++) {
var attrnode_$21 = nattrs_$18[k_$19];
if (attrnode_$21) {
var attrname_$22 = !nsprefix_$4 && (attrnode_$21.localName || attrnode_$21.baseName) || attrnode_$21.name;
cattrs_$17[attrname_$22] = attrnode_$21.value
}}};
var lfcnode_$15 = new LzDataElement(nname_$16);
lfcnode_$15.attributes = cattrs_$17;
lfcnode_$15.parentNode = lfcparent_$8;
lfcnode_$15.ownerDocument = document_$5;
lfcnode_$15.__LZo = lfcparent_$8.childNodes.push(lfcnode_$15) - 1;
if (next_$9 = node_$10.firstChild) {
lfcparent_$8 = lfcnode_$15;
node_$10 = next_$9;
continue
}} else {

}};
while (!(next_$9 = node_$10.nextSibling)) {
node_$10 = node_$10.parentNode;
lfcparent_$8 = lfcparent_$8.parentNode;
if (node_$10 === xmlnode_$1) {
return document_$5.childNodes[0]
}};
node_$10 = next_$9
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#78/17";
return $lzsc$temp
})()};
var LzHTTPLoader = (function  () {
var $lzsc$temp = function  (owner_$1, proxied_$2) {
this.owner = owner_$1;
this.options = {parsexml: true, serverproxyargs: null};
this.requestheaders = {};
this.requestmethod = LzHTTPLoader.GET_METHOD;
this.__loaderid = LzHTTPLoader.loaderIDCounter++
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#12/20";
return $lzsc$temp
})();
LzHTTPLoader.GET_METHOD = "GET";
LzHTTPLoader.POST_METHOD = "POST";
LzHTTPLoader.PUT_METHOD = "PUT";
LzHTTPLoader.DELETE_METHOD = "DELETE";
LzHTTPLoader.activeRequests = {};
LzHTTPLoader.loaderIDCounter = 0;
LzHTTPLoader.prototype.loadSuccess = (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {

};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.loadSuccess";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadError = (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {

};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.loadError";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadTimeout = (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {

};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.loadTimeout";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadContent = (function  () {
var $lzsc$temp = function  (self_$1, content_$2) {
if (this.options["parsexml"]) {
this.translateXML()
} else {
this.loadSuccess(this, content_$2)
}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.loadContent";
return $lzsc$temp
})();
LzHTTPLoader.prototype.translateXML = (function  () {
var $lzsc$temp = function  () {
var xml_$1 = this.responseXML;
if (xml_$1 == null || xml_$1.childNodes.length == 0 || lz.embed.browser.isFirefox && LzXMLParser.getParserError(xml_$1) != null) {
this.loadError(this, null)
} else {
var elt_$2;
var nodes_$3 = xml_$1.childNodes;
for (var i_$4 = 0;i_$4 < nodes_$3.length;i_$4++) {
var child_$5 = nodes_$3.item(i_$4);
if (child_$5.nodeType == 1) {
elt_$2 = child_$5;
break
}};
if (elt_$2 != null) {
var lzxdata_$6 = LzXMLTranslator.copyXML(elt_$2, this.options.trimwhitespace, this.options.nsprefix);
this.loadSuccess(this, lzxdata_$6)
} else {
this.loadError(this, null)
}}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.translateXML";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponse = (function  () {
var $lzsc$temp = function  () {
return this.responseText
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getResponse";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseStatus = (function  () {
var $lzsc$temp = function  () {
return this.responseStatus
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getResponseStatus";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseHeaders = (function  () {
var $lzsc$temp = function  () {
return this.responseHeaders
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getResponseHeaders";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseHeader = (function  () {
var $lzsc$temp = function  (key_$1) {
return this.responseHeaders[key_$1]
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getResponseHeader";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setRequestHeaders = (function  () {
var $lzsc$temp = function  (obj_$1) {
this.requestheaders = obj_$1
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setRequestHeaders";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setRequestHeader = (function  () {
var $lzsc$temp = function  (key_$1, val_$2) {
this.requestheaders[key_$1] = val_$2
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setRequestHeader";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setOption = (function  () {
var $lzsc$temp = function  (key_$1, val_$2) {
this.options[key_$1] = val_$2
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setOption";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getOption = (function  () {
var $lzsc$temp = function  (key_$1) {
return this.options[key_$1]
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getOption";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setProxied = (function  () {
var $lzsc$temp = function  (proxied_$1) {
this.setOption("proxied", proxied_$1)
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setProxied";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueryParams = (function  () {
var $lzsc$temp = function  (qparams_$1) {
this.queryparams = qparams_$1
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setQueryParams";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueryString = (function  () {
var $lzsc$temp = function  (qstring_$1) {
this.querystring = qstring_$1
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setQueryString";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueueing = (function  () {
var $lzsc$temp = function  (queuing_$1) {
this.setOption("queuing", queuing_$1)
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setQueueing";
return $lzsc$temp
})();
LzHTTPLoader.prototype.abort = (function  () {
var $lzsc$temp = function  () {
if (this.req) {
this.__abort = true;
this.req.abort();
this.req = null;
this.removeTimeout(this)
}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.abort";
return $lzsc$temp
})();
LzHTTPLoader.prototype.open = (function  () {
var $lzsc$temp = function  (method_$1, url_$2, username_$3, password_$4) {
if (this.req) {
Debug.warn("pending request for id=%s", this.__loaderid);
this.abort()
};
this.req = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
this.responseStatus = 0;
this.responseHeaders = null;
this.responseText = null;
this.responseXML = null;
this.__abort = false;
this.__timeout = false;
this.requesturl = url_$2;
this.requestmethod = method_$1
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.open";
return $lzsc$temp
})();
LzHTTPLoader.prototype.send = (function  () {
var $lzsc$temp = function  (content_$1) {
this.loadXMLDoc(this.requestmethod, this.requesturl, this.requestheaders, content_$1, true)
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.send";
return $lzsc$temp
})();
LzHTTPLoader.prototype.makeProxiedURL = (function  () {
var $lzsc$temp = function  (proxyurl_$1, url_$2, httpmethod_$3, lzt_$4, headers_$5, postbody_$6) {
var params_$7 = {serverproxyargs: this.options.serverproxyargs, sendheaders: this.options.sendheaders, trimwhitespace: this.options.trimwhitespace, nsprefix: this.options.nsprefix, timeout: this.timeout, cache: this.options.cacheable, ccache: this.options.ccache, proxyurl: proxyurl_$1, url: url_$2, secure: this.secure, postbody: postbody_$6, headers: headers_$5, httpmethod: httpmethod_$3, service: lzt_$4};
return lz.Browser.makeProxiedURL(params_$7)
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.makeProxiedURL";
return $lzsc$temp
})();
LzHTTPLoader.prototype.timeout = Infinity;
LzHTTPLoader.prototype.setTimeout = (function  () {
var $lzsc$temp = function  (timeout_$1) {
this.timeout = timeout_$1
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setTimeout";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setupTimeout = (function  () {
var $lzsc$temp = function  (loader_$1, duration_$2) {
var endtime_$3 = new Date().getTime() + duration_$2;
var lid_$4 = loader_$1.__loaderid;
LzHTTPLoader.activeRequests[lid_$4] = [loader_$1, endtime_$3];
var timeoutid_$5 = setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts(" + lid_$4 + ")", duration_$2);
LzHTTPLoader.activeRequests[lid_$4][2] = timeoutid_$5
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.setupTimeout";
return $lzsc$temp
})();
LzHTTPLoader.prototype.removeTimeout = (function  () {
var $lzsc$temp = function  (loader_$1) {
var lid_$2 = loader_$1.__loaderid;
if (lid_$2 != null) {
var reqarr_$3 = LzHTTPLoader.activeRequests[lid_$2];
if (reqarr_$3 && reqarr_$3[0] === loader_$1) {
clearTimeout(reqarr_$3[2]);
delete LzHTTPLoader.activeRequests[lid_$2]
}}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.removeTimeout";
return $lzsc$temp
})();
LzHTTPLoader.__LZcheckXMLHTTPTimeouts = (function  () {
var $lzsc$temp = function  (lid_$1) {
var reqarr_$2 = LzHTTPLoader.activeRequests[lid_$1];
if (reqarr_$2) {
var now_$3 = new Date().getTime();
var loader_$4 = reqarr_$2[0];
var dstimeout_$5 = reqarr_$2[1];
if (now_$3 >= dstimeout_$5) {
delete LzHTTPLoader.activeRequests[lid_$1];
loader_$4.__timeout = true;
if (loader_$4.req) {
loader_$4.req.abort()
};
loader_$4.req = null;
loader_$4.loadTimeout(loader_$4, null)
} else {
var timeoutid_$6 = setTimeout("LzHTTPLoader.__LZcheckXMLHTTPTimeouts(" + lid_$1 + ")", now_$3 - dstimeout_$5);
reqarr_$2[2] = timeoutid_$6
}}};
$lzsc$temp["displayName"] = "LzHTTPLoader.__LZcheckXMLHTTPTimeouts";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getElapsedTime = (function  () {
var $lzsc$temp = function  () {
return new Date().getTime() - this.gstart
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.getElapsedTime";
return $lzsc$temp
})();
LzHTTPLoader.prototype.__setRequestHeaders = (function  () {
var $lzsc$temp = function  (xhr_$1, headers_$2) {
if (headers_$2 != null) {
for (var key_$3 in headers_$2) {
var val_$4 = headers_$2[key_$3];
xhr_$1.setRequestHeader(key_$3, val_$4)
}}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.__setRequestHeaders";
return $lzsc$temp
})();
LzHTTPLoader.prototype.__getAllResponseHeaders = (function  () {
var $lzsc$temp = function  (xhr_$1) {
var re_$2 = new RegExp("^([-\\w]+):\\s*(\\S(?:.*\\S)?)\\s*$", "mg");
var respheader_$3 = xhr_$1.getAllResponseHeaders();
var allheaders_$4 = {};
var header_$5;
while ((header_$5 = re_$2.exec(respheader_$3)) != null) {
allheaders_$4[header_$5[1]] = header_$5[2]
};
return allheaders_$4
};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.__getAllResponseHeaders";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadXMLDoc = (function  () {
var $lzsc$temp = function  (method_$1, url_$2, headers_$3, postbody_$4, ignorewhite_$5) {
if (this.req) {
var self = this;
this.req.onreadystatechange = (function  () {
var $lzsc$temp = function  () {
var xhr_$1 = self.req;
if (xhr_$1 == null) {
return
};
if (xhr_$1.readyState == 4) {
if (self.__timeout) {

} else {
if (self.__abort) {

} else {
self.removeTimeout(self);
self.req = null;
var status_$2 = -1;
try {
status_$2 = xhr_$1.status
}
catch (e) {

};
self.responseStatus = status_$2;
if (status_$2 == 200 || status_$2 == 304) {
self.responseXML = xhr_$1.responseXML;
self.responseText = xhr_$1.responseText;
self.responseHeaders = self.__getAllResponseHeaders(xhr_$1);
self.loadContent(self, self.responseText)
} else {
self.loadError(self, null)
}}}}};
$lzsc$temp["displayName"] = "this.req.onreadystatechange";
return $lzsc$temp
})();
try {
this.req.open(method_$1, url_$2, true)
}
catch (e) {
this.req = null;
this.loadError(this, null);
return
};
if (method_$1 == "POST" && headers_$3["content-type"] == null) {
headers_$3["content-type"] = "application/x-www-form-urlencoded"
};
this.__setRequestHeaders(this.req, headers_$3);
this.gstart = new Date().getTime();
try {
this.req.send(postbody_$4)
}
catch (e) {
this.req = null;
this.loadError(this, null);
return
};
if (isFinite(this.timeout)) {
this.setupTimeout(this, this.timeout)
}}};
$lzsc$temp["displayName"] = "LzHTTPLoader.prototype.loadXMLDoc";
return $lzsc$temp
})();
var LzScreenKernel = {width: null, height: null, __resizeEvent: (function  () {
var $lzsc$temp = function  () {
var rootcontainerdiv_$1 = LzSprite.__rootSpriteContainer;
LzScreenKernel.width = rootcontainerdiv_$1.offsetWidth;
LzScreenKernel.height = rootcontainerdiv_$1.offsetHeight;
if (LzScreenKernel.__callback) {
LzScreenKernel.__scope[LzScreenKernel.__callback]({width: LzScreenKernel.width, height: LzScreenKernel.height})
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#16/21";
return $lzsc$temp
})(), __init: (function  () {
var $lzsc$temp = function  () {
lz.embed.attachEventHandler(window.top, "resize", LzScreenKernel, "__resizeEvent")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#59/14";
return $lzsc$temp
})(), __callback: null, __scope: null, setCallback: (function  () {
var $lzsc$temp = function  (scope_$1, funcname_$2) {
this.__scope = scope_$1;
this.__callback = funcname_$2;
this.__init();
this.__resizeEvent()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#64/19";
return $lzsc$temp
})()};
Class.make("LzContextMenuKernel", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (newowner_$1) {
this.owner = newowner_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "owner", null, "showbuiltins", false, "_delegate", null, "setDelegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
this._delegate = delegate_$1
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "addItem", (function  () {
var $lzsc$temp = function  (item_$1) {

};
$lzsc$temp["displayName"] = "addItem";
return $lzsc$temp
})(), "hideBuiltInItems", (function  () {
var $lzsc$temp = function  () {
this.showbuiltins = false
};
$lzsc$temp["displayName"] = "hideBuiltInItems";
return $lzsc$temp
})(), "showBuiltInItems", (function  () {
var $lzsc$temp = function  () {
this.showbuiltins = true
};
$lzsc$temp["displayName"] = "showBuiltInItems";
return $lzsc$temp
})(), "clearItems", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "clearItems";
return $lzsc$temp
})(), "__show", (function  () {
var $lzsc$temp = function  () {
var owner_$1 = this.owner;
var del_$2 = this._delegate;
if (del_$2 != null) {
del_$2.execute(owner_$1)
};
if (owner_$1.onmenuopen.ready) {
owner_$1.onmenuopen.sendEvent(owner_$1)
};
var o_$3 = "";
var shownItems_$4 = 0;
var items_$5 = owner_$1.getItems();
var _items_$6 = {};
for (var i_$7 = 0;i_$7 < items_$5.length;i_$7++) {
var v_$8 = items_$5[i_$7].kernel.cmenuitem;
var caption_$9 = v_$8.caption;
if (v_$8.visible != true || (caption_$9 in _items_$6)) {
continue
};
_items_$6[caption_$9] = true;
shownItems_$4 += 1;
if (v_$8.separatorBefore) {
o_$3 += '<div class="separator"></div>'
};
if (v_$8.enabled) {
o_$3 += '<a href="" onmouseup="LzMouseKernel.__showncontextmenu.__select(arguments[0],' + i_$7 + ');">'
} else {
o_$3 += '<a href="" class="disabled">'
};
o_$3 += caption_$9 + "</a>"
};
LzMouseKernel.__showncontextmenu = this;
var s_$10 = LzContextMenuKernel.lzcontextmenu || LzContextMenuKernel.__create();
s_$10.innerHTML = o_$3;
var width_$11 = s_$10.offsetWidth;
var x_$12 = LzMouseKernel.__x;
if (x_$12 + width_$11 > LzScreenKernel.width) {
x_$12 += LzScreenKernel.width - (x_$12 + width_$11)
};
var height_$13 = s_$10.offsetHeight;
var y_$14 = LzMouseKernel.__y;
if (y_$14 + height_$13 > LzScreenKernel.height) {
y_$14 += LzScreenKernel.height - (y_$14 + height_$13)
};
s_$10.style.left = x_$12 + "px";
s_$10.style.top = y_$14 + "px";
if (shownItems_$4 && !this.showbuiltins) {
s_$10.style.display = "block"
}};
$lzsc$temp["displayName"] = "__show";
return $lzsc$temp
})(), "__hide", (function  () {
var $lzsc$temp = function  () {
var s_$1 = LzContextMenuKernel.lzcontextmenu;
if (s_$1) {
s_$1.style.display = "none"
};
LzMouseKernel.__showncontextmenu = null
};
$lzsc$temp["displayName"] = "__hide";
return $lzsc$temp
})(), "__select", (function  () {
var $lzsc$temp = function  (e_$1, i_$2) {
e_$1 = e_$1 || window.event;
var leftbutton_$3 = LzSprite.quirks.ie_mouse_events ? 1 : 0;
if (e_$1.button == leftbutton_$3) {
this.__hide();
var items_$4 = this.owner.getItems();
if (items_$4[i_$2]) {
items_$4[i_$2].kernel.__select()
}}};
$lzsc$temp["displayName"] = "__select";
return $lzsc$temp
})()], ["lzcontextmenu", null, "__create", (function  () {
var $lzsc$temp = function  () {
var s_$1 = document.getElementById("lzcontextmenu");
if (!s_$1) {
s_$1 = document.createElement("div");
lz.embed.__setAttr(s_$1, "id", "lzcontextmenu");
lz.embed.__setAttr(s_$1, "style", "display: none");
var stopevent_$2 = (function  () {
var $lzsc$temp = function  (e_$1) {
if (e_$1) {
e_$1.preventDefault();
e_$1.stopPropagation()
} else {
e_$1 = window.event;
e_$1.returnValue = false;
e_$1.cancelBubble = true
};
return false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzContextMenuKernel.lzs#82/25";
return $lzsc$temp
})();
s_$1.onmousedown = stopevent_$2;
s_$1.onmouseup = stopevent_$2;
s_$1.onclick = stopevent_$2;
document.body.appendChild(s_$1);
LzContextMenuKernel.lzcontextmenu = s_$1
};
return s_$1
};
$lzsc$temp["displayName"] = "__create";
return $lzsc$temp
})()]);
Class.make("LzContextMenuItemKernel", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (newowner_$1, title_$2, del_$3) {
this.owner = newowner_$1;
this.cmenuitem = {visible: true, enabled: true, separatorBefore: false, caption: title_$2};
this.setDelegate(del_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "owner", null, "cmenuitem", null, "_delegate", null, "setDelegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
this._delegate = delegate_$1
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "setCaption", (function  () {
var $lzsc$temp = function  (caption_$1) {
this.cmenuitem.caption = caption_$1
};
$lzsc$temp["displayName"] = "setCaption";
return $lzsc$temp
})(), "getCaption", (function  () {
var $lzsc$temp = function  () {
return this.cmenuitem.caption
};
$lzsc$temp["displayName"] = "getCaption";
return $lzsc$temp
})(), "setEnabled", (function  () {
var $lzsc$temp = function  (val_$1) {
this.cmenuitem.enabled = val_$1
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setSeparatorBefore", (function  () {
var $lzsc$temp = function  (val_$1) {
this.cmenuitem.separatorBefore = val_$1
};
$lzsc$temp["displayName"] = "setSeparatorBefore";
return $lzsc$temp
})(), "setVisible", (function  () {
var $lzsc$temp = function  (val_$1) {
this.cmenuitem.visible = val_$1
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "__select", (function  () {
var $lzsc$temp = function  () {
var owner_$1 = this.owner;
var delegate_$2 = this._delegate;
if (delegate_$2 != null) {
if (delegate_$2 instanceof LzDelegate) {
delegate_$2.execute(owner_$1)
} else {
if (typeof delegate_$2 == "function") {
delegate_$2()
} else {
Debug.error("LzContextMenuItem.setDelegate must be passed a delegate", owner_$1, delegate_$2)
}}};
if (owner_$1.onselect.ready) {
owner_$1.onselect.sendEvent(owner_$1)
}};
$lzsc$temp["displayName"] = "__select";
return $lzsc$temp
})()], null);
if (LzSprite.quirks.ie_timer_closure) {
(function  () {
var $lzsc$temp = function  (f_$1) {
window.setTimeout = f_$1(window.setTimeout);
window.setInterval = f_$1(window.setInterval)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#18/4";
return $lzsc$temp
})()((function  () {
var $lzsc$temp = function  (f) {
return (function  () {
var $lzsc$temp = function  (c, t_$1) {
var a = Array.prototype.slice.call(arguments, 2);
if (typeof c != "function") {
c = new Function(c)
};
return( f((function  () {
var $lzsc$temp = function  () {
c.apply(this, a)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#26/18";
return $lzsc$temp
})(), t_$1))
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#22/12";
return $lzsc$temp
})()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#21/6";
return $lzsc$temp
})())
};
var LzTimeKernel = {setTimeout: (function  () {
var $lzsc$temp = function  () {
return window.setTimeout.apply(window, arguments)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#34/17";
return $lzsc$temp
})(), setInterval: (function  () {
var $lzsc$temp = function  () {
return window.setInterval.apply(window, arguments)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#37/19";
return $lzsc$temp
})(), clearTimeout: (function  () {
var $lzsc$temp = function  (id_$1) {
return window.clearTimeout(id_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#40/20";
return $lzsc$temp
})(), clearInterval: (function  () {
var $lzsc$temp = function  (id_$1) {
return window.clearInterval(id_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#43/21";
return $lzsc$temp
})(), startTime: new Date().valueOf(), getTimer: (function  () {
var $lzsc$temp = function  () {
return new Date().valueOf() - LzTimeKernel.startTime
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#49/16";
return $lzsc$temp
})()};
Class.make("LzView", LzNode, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__LZlayout", void 0, "__LZstoredbounds", void 0, "__movecounter", 0, "__mousecache", null, "playing", false, "_visible", void 0, "$lzc$set_visible", (function  () {
var $lzsc$temp = function  (amVisible_$1) {
if (this._visible == amVisible_$1) {
return
};
this._visible = amVisible_$1;
if (amVisible_$1) {
var v_$2 = "visible"
} else {
if (amVisible_$1 == null) {
Debug.info("%w.%s(%w) is deprecated.  Perhaps you meant %w.%s(%s)?  If not, use %w.%s('collapse').", this, arguments.callee, amVisible_$1, this, arguments.callee, false, this, this.setVisibility);
var v_$2 = "collapse"
} else {
var v_$2 = "hidden"
}};
this.visibility = v_$2;
if (this.onvisibility.ready) {
this.onvisibility.sendEvent(this.visibility)
};
this.__LZupdateShown()
};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "onaddsubview", LzDeclaredEvent, "onblur", LzDeclaredEvent, "onclick", LzDeclaredEvent, "onclickable", LzDeclaredEvent, "onfocus", LzDeclaredEvent, "onframe", LzDeclaredEvent, "onheight", LzDeclaredEvent, "onkeyup", LzDeclaredEvent, "onkeydown", LzDeclaredEvent, "onlastframe", LzDeclaredEvent, "onload", LzDeclaredEvent, "onframesloadratio", LzDeclaredEvent, "onloadratio", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "onmousedown", LzDeclaredEvent, "onmouseout", LzDeclaredEvent, "onmouseover", LzDeclaredEvent, "onmousetrackover", LzDeclaredEvent, "onmousetrackup", LzDeclaredEvent, "onmousetrackout", LzDeclaredEvent, "onmouseup", LzDeclaredEvent, "onmousedragin", LzDeclaredEvent, "onmousedragout", LzDeclaredEvent, "onmouseupoutside", LzDeclaredEvent, "onopacity", LzDeclaredEvent, "onplay", LzDeclaredEvent, "onremovesubview", LzDeclaredEvent, "onresource", LzDeclaredEvent, "onresourceheight", LzDeclaredEvent, "onresourcewidth", LzDeclaredEvent, "onrotation", LzDeclaredEvent, "onstop", LzDeclaredEvent, "ontotalframes", LzDeclaredEvent, "onunstretchedheight", LzDeclaredEvent, "onunstretchedwidth", LzDeclaredEvent, "onvisible", LzDeclaredEvent, "onvisibility", LzDeclaredEvent, "onwidth", LzDeclaredEvent, "onx", LzDeclaredEvent, "onxoffset", LzDeclaredEvent, "ony", LzDeclaredEvent, "onyoffset", LzDeclaredEvent, "onfont", LzDeclaredEvent, "onfontsize", LzDeclaredEvent, "onfontstyle", LzDeclaredEvent, "ondblclick", LzDeclaredEvent, "DOUBLE_CLICK_TIME", 500, "capabilities", void 0, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1 ? parent_$1 : canvas, args_$2);
this.__LZdelayedSetters = LzView.__LZdelayedSetters;
this.earlySetters = LzView.earlySetters;
this.mask = this.immediateparent.mask;
this.__makeSprite(args_$2);
this.capabilities = this.sprite.capabilities;
if (this.capabilities.setid) {
this.sprite.setID(this._dbg_name())
};
if (args_$2["width"] != null || this.__LZhasConstraint("width")) {
this.hassetwidth = true;
this.__LZcheckwidth = false
};
if (args_$2["height"] != null || this.__LZhasConstraint("height")) {
this.hassetheight = true;
this.__LZcheckheight = false
};
if (args_$2["clip"]) {
this.clip = args_$2.clip;
this.makeMasked()
};
if (args_$2["stretches"] != null) {
this.$lzc$set_stretches(args_$2.stretches);
args_$2.stretches = LzNode._ignoreAttribute
};
if (args_$2["resource"] != null) {
this.$lzc$set_resource(args_$2.resource);
args_$2.resource = LzNode._ignoreAttribute
};
if (args_$2["fgcolor"] != null) {
this.hasfgcolor = true
};
if (args_$2["valign"] && args_$2["y"]) {
Debug.warn(this, "y attribute ignored; superseded by valign constraint.")
};
if (args_$2["align"] && args_$2["x"]) {
Debug.warn(this, "x attribute ignored; superseded by align constraint.")
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__spriteAttribute", (function  () {
var $lzsc$temp = function  (attrname_$1, value_$2) {
if (this[attrname_$1]) {
if (!this.__LZdeleted) {
var $lzsc$1431604258 = "$lzc$set_" + attrname_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$1431604258]) : this[$lzsc$1431604258] instanceof Function) {
this[$lzsc$1431604258](value_$2)
} else {
this[attrname_$1] = value_$2;
var $lzsc$2054567139 = this["on" + attrname_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$2054567139) : $lzsc$2054567139 instanceof LzEvent) {
if ($lzsc$2054567139.ready) {
$lzsc$2054567139.sendEvent(value_$2)
}}}}}};
$lzsc$temp["displayName"] = "__spriteAttribute";
return $lzsc$temp
})(), "__makeSprite", (function  () {
var $lzsc$temp = function  (args_$1) {
this.sprite = new LzSprite(this, false)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
if (this.sprite) {
this.sprite.init(this.visible)
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "addSubview", (function  () {
var $lzsc$temp = function  (s_$1) {
if (s_$1.addedToParent) {
return
};
if (this.sprite) {
this.sprite.addChildSprite(s_$1.sprite)
};
if (this.subviews.length == 0) {
this.subviews = []
};
this.subviews.push(s_$1);
s_$1.addedToParent = true;
if (this.__LZcheckwidth) {
this.__LZcheckwidthFunction(s_$1)
};
if (this.__LZcheckheight) {
this.__LZcheckheightFunction(s_$1)
};
if (this.onaddsubview.ready) {
this.onaddsubview.sendEvent(s_$1)
}};
$lzsc$temp["displayName"] = "addSubview";
return $lzsc$temp
})(), "__LZinstantiationDone", (function  () {
var $lzsc$temp = function  () {
var vip_$1 = this.immediateparent;
if (vip_$1) {
vip_$1.addSubview(this)
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZinstantiationDone"] || this.nextMethod(arguments.callee, "__LZinstantiationDone")).call(this)
};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "mask", void 0, "focusable", false, "focustrap", void 0, "clip", false, "$lzc$set_clip", -1, "align", "left", "$lzc$set_align", (function  () {
var $lzsc$temp = function  (align_$1) {
var map_$2;
map_$2 = (function  () {
var $lzsc$temp = function  (align_$1) {
switch (align_$1) {
case "center":
return "__LZalignCenter";;case "right":
return "__LZalignRight";;case "left":
return null
};
Debug.error("%w.setAttribute(%w, %w): Invalid argument.  Valid choices are: 'left', 'center', or 'right'.", view, "align", align_$1)
};
$lzsc$temp["displayName"] = "map";
return $lzsc$temp
})();
if (this.align == align_$1) {
return
};
var view = this;
var from_$3 = map_$2(this.align);
var to_$4 = map_$2(align_$1);
if (from_$3 != null) {
this.releaseConstraintMethod(from_$3)
};
if (to_$4 != null) {
this.applyConstraintMethod(to_$4, [this.immediateparent, "width", this, "width"])
} else {
this.$lzc$set_x(0)
};
this.align = align_$1
};
$lzsc$temp["displayName"] = "$lzc$set_align";
return $lzsc$temp
})(), "valign", "top", "$lzc$set_valign", (function  () {
var $lzsc$temp = function  (valign) {
var map_$1;
map_$1 = (function  () {
var $lzsc$temp = function  (align_$1) {
switch (align_$1) {
case "middle":
return "__LZvalignMiddle";;case "bottom":
return "__LZvalignBottom";;case "top":
return null
};
Debug.error("%w.setAttribute(%w, %w): Invalid argument.  Valid choices are: 'top', 'middle', or 'bottom'.", view, "valign", valign)
};
$lzsc$temp["displayName"] = "map";
return $lzsc$temp
})();
if (this.valign == valign) {
return
};
var view = this;
var from_$2 = map_$1(this.valign);
var to_$3 = map_$1(valign);
if (from_$2 != null) {
this.releaseConstraintMethod(from_$2)
};
if (to_$3 != null) {
this.applyConstraintMethod(to_$3, [this.immediateparent, "height", this, "height"])
} else {
this.$lzc$set_y(0)
};
this.valign = valign
};
$lzsc$temp["displayName"] = "$lzc$set_valign";
return $lzsc$temp
})(), "source", void 0, "$lzc$set_source", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setSource(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_source";
return $lzsc$temp
})(), "clickregion", void 0, "$lzc$set_clickregion", (function  () {
var $lzsc$temp = function  (cr_$1) {
if (this.capabilities.clickregion) {
this.sprite.setClickRegion(cr_$1)
} else {
LzView.__warnCapability("view.clickregion", "clickregion")
};
this.clickregion = cr_$1
};
$lzsc$temp["displayName"] = "$lzc$set_clickregion";
return $lzsc$temp
})(), "cursor", void 0, "fgcolor", 0, "hasfgcolor", false, "onfgcolor", LzDeclaredEvent, "$lzc$set_fgcolor", (function  () {
var $lzsc$temp = function  (c_$1) {
if (c_$1 != null && isNaN(c_$1)) {
c_$1 = this.acceptTypeValue("color", c_$1)
};
this.sprite.setColor(c_$1);
this.fgcolor = c_$1;
if (this.onfgcolor.ready) {
this.onfgcolor.sendEvent(c_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_fgcolor";
return $lzsc$temp
})(), "font", void 0, "fontname", void 0, "$lzc$set_font", (function  () {
var $lzsc$temp = function  (val_$1) {
this.font = val_$1;
this.fontname = val_$1;
if (this.onfont.ready) {
this.onfont.sendEvent(this.font)
}};
$lzsc$temp["displayName"] = "$lzc$set_font";
return $lzsc$temp
})(), "fontstyle", void 0, "$lzc$set_fontstyle", (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 == "plain" || val_$1 == "bold" || val_$1 == "italic" || val_$1 == "bolditalic" || val_$1 == "bold italic") {
this.fontstyle = val_$1;
if (this.onfontstyle.ready) {
this.onfontstyle.sendEvent(this.fontstyle)
}} else {
Debug.warn('invalid font style "%s" on %w', val_$1, this)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontstyle";
return $lzsc$temp
})(), "fontsize", void 0, "$lzc$set_fontsize", (function  () {
var $lzsc$temp = function  (val_$1) {
if (!(val_$1 <= 0 || isNaN(val_$1))) {
this.fontsize = val_$1;
if (this.onfontsize.ready) {
this.onfontsize.sendEvent(this.fontsize)
}} else {
Debug.warn("invalid font size", val_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontsize";
return $lzsc$temp
})(), "stretches", "none", "$lzc$set_stretches", (function  () {
var $lzsc$temp = function  (stretch_$1) {
if (!(stretch_$1 == "none" || stretch_$1 == "both" || stretch_$1 == "width" || stretch_$1 == "height")) {
var newstretch_$2 = stretch_$1 == null ? "both" : (stretch_$1 == "x" ? "width" : (stretch_$1 == "y" ? "height" : "none"));
if (newstretch_$2 != "none") {
Debug.info("%w.%s(%w) is deprecated.  Use %w.%s(%w) instead.", this, arguments.callee, stretch_$1, this, arguments.callee, newstretch_$2)
};
stretch_$1 = newstretch_$2
} else {
if (this.stretches == stretch_$1) {
return
}};
this.stretches = stretch_$1;
this.sprite.stretchResource(stretch_$1);
if (stretch_$1 == "width" || stretch_$1 == "both") {
this._setrescwidth = true;
this.__LZcheckwidth = true;
this.reevaluateSize("width")
};
if (stretch_$1 == "height" || stretch_$1 == "both") {
this._setrescheight = true;
this.__LZcheckheight = true;
this.reevaluateSize("height")
}};
$lzsc$temp["displayName"] = "$lzc$set_stretches";
return $lzsc$temp
})(), "layout", void 0, "$lzc$set_layout", (function  () {
var $lzsc$temp = function  (layoutobj_$1) {
this.layout = layoutobj_$1;
if (!this.isinited) {
this.__LZstoreAttr(layoutobj_$1, "layout");
return
};
var classname_$2 = layoutobj_$1["class"];
if (classname_$2 == null) {
classname_$2 = "simplelayout"
};
if (this.__LZlayout) {
this.__LZlayout.destroy()
};
if (classname_$2 != "none") {
var o_$3 = {};
for (var i_$4 in layoutobj_$1) {
if (i_$4 != "class") {
o_$3[i_$4] = layoutobj_$1[i_$4]
}};
if (classname_$2 == "null") {
this.__LZlayout = null;
return
};
this.__LZlayout = new (lz[classname_$2])(this, o_$3)
}};
$lzsc$temp["displayName"] = "$lzc$set_layout";
return $lzsc$temp
})(), "aaactive", void 0, "$lzc$set_aaactive", (function  () {
var $lzsc$temp = function  (s_$1) {
if (this.capabilities.accessibility) {
this.aaactive = s_$1;
this.sprite.setAAActive(s_$1)
} else {
LzView.__warnCapability("view.aaactive", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aaactive";
return $lzsc$temp
})(), "aaname", void 0, "$lzc$set_aaname", (function  () {
var $lzsc$temp = function  (s_$1) {
if (this.capabilities.accessibility) {
this.aaname = s_$1;
this.sprite.setAAName(s_$1)
} else {
LzView.__warnCapability("view.aaname", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aaname";
return $lzsc$temp
})(), "aadescription", void 0, "$lzc$set_aadescription", (function  () {
var $lzsc$temp = function  (s_$1) {
if (this.capabilities.accessibility) {
this.aadescription = s_$1;
this.sprite.setAADescription(s_$1)
} else {
LzView.__warnCapability("view.aadescription", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aadescription";
return $lzsc$temp
})(), "aatabindex", void 0, "$lzc$set_aatabindex", (function  () {
var $lzsc$temp = function  (s_$1) {
if (this.capabilities.accessibility) {
this.aatabindex = s_$1;
this.sprite.setAATabIndex(s_$1)
} else {
LzView.__warnCapability("view.aatabindex", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aatabindex";
return $lzsc$temp
})(), "aasilent", void 0, "$lzc$set_aasilent", (function  () {
var $lzsc$temp = function  (s_$1) {
if (this.capabilities.accessibility) {
this.aasilent = s_$1;
this.sprite.setAASilent(s_$1)
} else {
LzView.__warnCapability("view.aasilent", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aasilent";
return $lzsc$temp
})(), "sendAAEvent", (function  () {
var $lzsc$temp = function  (childID_$1, eventType_$2, nonHTML_$3) {
switch (arguments.length) {
case 2:
nonHTML_$3 = false
};
if (this.capabilities.accessibility) {
this.sprite.sendAAEvent(childID_$1, eventType_$2, nonHTML_$3)
} else {
LzView.__warnCapability("view.sendAAEvent()", "accessibility")
}};
$lzsc$temp["displayName"] = "sendAAEvent";
return $lzsc$temp
})(), "sprite", null, "visible", true, "visibility", "collapse", "$lzc$set_visibility", (function  () {
var $lzsc$temp = function  (amVisible_$1) {
if (this.visibility == amVisible_$1) {
return
};
this.visibility = amVisible_$1;
if (!(amVisible_$1 == "visible" || amVisible_$1 == "hidden" || amVisible_$1 == "collapse")) {
Debug.error("%w.%s called with unknown arg '%s' use 'visible', 'hidden', or 'collapse'.", this, arguments.callee, amVisible_$1)
};
if (this.onvisibility.ready) {
this.onvisibility.sendEvent(amVisible_$1)
};
this.__LZupdateShown()
};
$lzsc$temp["displayName"] = "$lzc$set_visibility";
return $lzsc$temp
})(), "__LZvizO", true, "__LZvizLoad", true, "__LZvizDat", true, "opacity", 1, "$lzc$set_opacity", (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.capabilities.opacity) {
this.sprite.setOpacity(v_$1)
} else {
LzView.__warnCapability("view.opacity", "opacity")
};
this.opacity = v_$1;
if (this.onopacity.ready) {
this.onopacity.sendEvent(v_$1)
};
var coviz_$2 = this.__LZvizO;
var newoviz_$3 = v_$1 != 0;
if (coviz_$2 != newoviz_$3) {
this.__LZvizO = newoviz_$3;
this.__LZupdateShown()
}};
$lzsc$temp["displayName"] = "$lzc$set_opacity";
return $lzsc$temp
})(), "$lzc$set_alpha", (function  () {
var $lzsc$temp = function  (v_$1) {
this.$lzc$set_opacity(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_alpha";
return $lzsc$temp
})(), "bgcolor", null, "onbgcolor", LzDeclaredEvent, "$lzc$set_bgcolor", (function  () {
var $lzsc$temp = function  (bgc_$1) {
if (bgc_$1 != null && isNaN(bgc_$1)) {
bgc_$1 = this.acceptTypeValue("color", bgc_$1)
};
this.sprite.setBGColor(bgc_$1);
this.bgcolor = bgc_$1;
if (this.onbgcolor.ready) {
this.onbgcolor.sendEvent(bgc_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_bgcolor";
return $lzsc$temp
})(), "x", 0, "__set_x_memo", void 0, "$lzc$set_x", (function  () {
var $lzsc$temp = function  (v_$1) {
this.x = v_$1;
if (this.__set_x_memo === v_$1) {
if (this.onx.ready) {
this.onx.sendEvent(this.x)
};
return
};
this.__set_x_memo = v_$1;
if (this.__LZhasoffset) {
if (this.capabilities.rotation) {
v_$1 -= this.xoffset * this.__LZrcos - this.yoffset * this.__LZrsin
} else {
v_$1 -= this.xoffset
}};
if (this.pixellock) {
v_$1 = Math.floor(v_$1)
};
this.sprite.setX(v_$1);
var vip_$2 = this.immediateparent;
if (vip_$2.__LZcheckwidth) {
vip_$2.__LZcheckwidthFunction(this)
};
if (this.onx.ready) {
this.onx.sendEvent(this.x)
}};
$lzsc$temp["displayName"] = "$lzc$set_x";
return $lzsc$temp
})(), "y", 0, "__set_y_memo", void 0, "$lzc$set_y", (function  () {
var $lzsc$temp = function  (v_$1) {
this.y = v_$1;
if (this.__set_y_memo === v_$1) {
if (this.ony.ready) {
this.ony.sendEvent(this.y)
};
return
};
this.__set_y_memo = v_$1;
if (this.__LZhasoffset) {
if (this.capabilities.rotation) {
v_$1 -= this.xoffset * this.__LZrsin + this.yoffset * this.__LZrcos
} else {
v_$1 -= this.yoffset
}};
if (this.pixellock) {
v_$1 = Math.floor(v_$1)
};
this.sprite.setY(v_$1);
var vip_$2 = this.immediateparent;
if (vip_$2.__LZcheckheight) {
vip_$2.__LZcheckheightFunction(this)
};
if (this.ony.ready) {
this.ony.sendEvent(this.y)
}};
$lzsc$temp["displayName"] = "$lzc$set_y";
return $lzsc$temp
})(), "rotation", 0, "$lzc$set_rotation", (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.capabilities.rotation) {
this.sprite.setRotation(v_$1)
} else {
LzView.__warnCapability("view.rotation", "rotation")
};
this.rotation = v_$1;
var rrad_$2 = Math.PI / 180 * this.rotation;
this.__LZrsin = Math.sin(rrad_$2);
this.__LZrcos = Math.cos(rrad_$2);
if (this.onrotation.ready) {
this.onrotation.sendEvent(v_$1)
};
if (this.__LZhasoffset) {
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y)
};
var vip_$3 = this.immediateparent;
if (vip_$3.__LZcheckwidth) {
vip_$3.__LZcheckwidthFunction(this)
};
if (vip_$3.__LZcheckheight) {
vip_$3.__LZcheckheightFunction(this)
}};
$lzsc$temp["displayName"] = "$lzc$set_rotation";
return $lzsc$temp
})(), "width", 0, "__set_width_memo", void 0, "$lzc$set_width", (function  () {
var $lzsc$temp = function  (v_$1) {
if (v_$1 != null) {
this.hassetwidth = true;
this.width = v_$1
} else {
this.hassetwidth = false
};
if (this.__set_width_memo === v_$1) {
if (this.onwidth.ready) {
this.onwidth.sendEvent(this.width)
};
return
};
this.__set_width_memo = v_$1;
if (v_$1 == null) {
this.__LZcheckwidth = true;
if (this._setrescwidth) {
this.unstretchedwidth = null;
this._xscale = 1
};
this.reevaluateSize("width");
return
};
if (this.pixellock) {
v_$1 = Math.floor(v_$1)
};
if (this._setrescwidth) {
var xscale_$2 = this.unstretchedwidth == 0 ? 100 : v_$1 / this.unstretchedwidth;
this._xscale = xscale_$2
} else {
this.__LZcheckwidth = false
};
this.sprite.setWidth(v_$1);
var vip_$3 = this.immediateparent;
if (vip_$3 && vip_$3.__LZcheckwidth) {
vip_$3.__LZcheckwidthFunction(this)
};
if (this.onwidth.ready) {
this.onwidth.sendEvent(this.width)
}};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "height", 0, "__set_height_memo", void 0, "$lzc$set_height", (function  () {
var $lzsc$temp = function  (v_$1) {
if (v_$1 != null) {
this.hassetheight = true;
this.height = v_$1
} else {
this.hassetheight = false
};
if (this.__set_height_memo === v_$1) {
if (this.onheight.ready) {
this.onheight.sendEvent(this.height)
};
return
};
this.__set_height_memo = v_$1;
if (v_$1 == null) {
this.__LZcheckheight = true;
if (this._setrescheight) {
this.unstretchedheight = null;
this._yscale = 1
};
this.reevaluateSize("height");
return
};
if (this.pixellock) {
v_$1 = Math.floor(v_$1)
};
if (this._setrescheight) {
this._yscale = this.unstretchedheight == 0 ? 100 : v_$1 / this.unstretchedheight
} else {
this.__LZcheckheight = false
};
this.sprite.setHeight(v_$1);
var vip_$2 = this.immediateparent;
if (vip_$2 && vip_$2.__LZcheckheight) {
vip_$2.__LZcheckheightFunction(this)
};
if (this.onheight.ready) {
this.onheight.sendEvent(this.height)
}};
$lzsc$temp["displayName"] = "$lzc$set_height";
return $lzsc$temp
})(), "unstretchedwidth", 0, "unstretchedheight", 0, "subviews", [], "xoffset", 0, "$lzc$set_xoffset", (function  () {
var $lzsc$temp = function  (o_$1) {
this.__LZhasoffset = o_$1 != 0;
this.xoffset = o_$1;
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y);
if (this.onxoffset.ready) {
this.onxoffset.sendEvent(o_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_xoffset";
return $lzsc$temp
})(), "yoffset", 0, "$lzc$set_yoffset", (function  () {
var $lzsc$temp = function  (o_$1) {
this.__LZhasoffset = o_$1 != 0;
this.yoffset = o_$1;
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y);
if (this.onyoffset.ready) {
this.onyoffset.sendEvent(o_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_yoffset";
return $lzsc$temp
})(), "__LZrsin", 0, "__LZrcos", 1, "_xscale", 1, "_yscale", 1, "totalframes", 1, "frame", 1, "$lzc$set_frame", (function  () {
var $lzsc$temp = function  (n_$1) {
this.frame = n_$1;
this.stop(n_$1);
if (this.onframe.ready) {
this.onframe.sendEvent(n_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_frame";
return $lzsc$temp
})(), "framesloadratio", 0, "loadratio", 0, "hassetheight", false, "hassetwidth", false, "addedToParent", null, "masked", false, "pixellock", null, "clickable", false, "$lzc$set_clickable", (function  () {
var $lzsc$temp = function  (amclickable_$1) {
this.sprite.setClickable(amclickable_$1);
this.clickable = amclickable_$1;
if (this.onclickable.ready) {
this.onclickable.sendEvent(amclickable_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_clickable";
return $lzsc$temp
})(), "showhandcursor", null, "$lzc$set_showhandcursor", (function  () {
var $lzsc$temp = function  (s_$1) {
this.showhandcursor = s_$1;
this.sprite.setShowHandCursor(s_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_showhandcursor";
return $lzsc$temp
})(), "resource", null, "$lzc$set_resource", (function  () {
var $lzsc$temp = function  (resourceName_$1) {
if (resourceName_$1 == null || resourceName_$1 == this._resource) {
return
};
this.resource = this._resource = resourceName_$1;
this.sprite.setResource(resourceName_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_resource";
return $lzsc$temp
})(), "resourcewidth", 0, "resourceheight", 0, "__LZcheckwidth", true, "__LZcheckheight", true, "__LZhasoffset", null, "__LZoutlieheight", null, "__LZoutliewidth", null, "setLayout", (function  () {
var $lzsc$temp = function  (layoutobj_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_layout(layoutobj_$1)
};
$lzsc$temp["displayName"] = "setLayout";
return $lzsc$temp
})(), "setFontName", (function  () {
var $lzsc$temp = function  (val_$1, prop_$2) {
switch (arguments.length) {
case 1:
prop_$2 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_font(val_$1)
};
$lzsc$temp["displayName"] = "setFontName";
return $lzsc$temp
})(), "_setrescwidth", false, "_setrescheight", false, "searchSubviews", (function  () {
var $lzsc$temp = function  (prop_$1, val_$2) {
var nextS_$3 = this.subviews.concat();
while (nextS_$3.length > 0) {
var s_$4 = nextS_$3;
nextS_$3 = new Array();
for (var i_$5 = s_$4.length - 1;i_$5 >= 0;i_$5--) {
var si_$6 = s_$4[i_$5];
if (si_$6[prop_$1] == val_$2) {
return si_$6
};
var sis_$7 = si_$6.subviews;
for (var j_$8 = sis_$7.length - 1;j_$8 >= 0;j_$8--) {
nextS_$3.push(sis_$7[j_$8])
}}};
return null
};
$lzsc$temp["displayName"] = "searchSubviews";
return $lzsc$temp
})(), "layouts", null, "releaseLayouts", (function  () {
var $lzsc$temp = function  () {
Debug.deprecated(this, arguments.callee, this.setAttribute);
if (this.layouts) {
for (var i_$1 = this.layouts.length - 1;i_$1 >= 0;i_$1--) {
this.layouts[i_$1].releaseLayout()
}}};
$lzsc$temp["displayName"] = "releaseLayouts";
return $lzsc$temp
})(), "_resource", null, "setResource", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_resource(v_$1)
};
$lzsc$temp["displayName"] = "setResource";
return $lzsc$temp
})(), "resourceload", (function  () {
var $lzsc$temp = function  (i_$1) {
if ("resource" in i_$1) {
this.resource = i_$1.resource;
if (this.onresource.ready) {
this.onresource.sendEvent(i_$1.resource)
}};
if (this.resourcewidth != i_$1.width) {
if ("width" in i_$1) {
this.resourcewidth = i_$1.width;
if (this.onresourcewidth.ready) {
this.onresourcewidth.sendEvent(i_$1.width)
}};
if (!this.hassetwidth && this.resourcewidth != this.width || this._setrescwidth && this.unstretchedwidth != this.resourcewidth) {
this.updateWidth(this.resourcewidth)
}};
if (this.resourceheight != i_$1.height) {
if ("height" in i_$1) {
this.resourceheight = i_$1.height;
if (this.onresourceheight.ready) {
this.onresourceheight.sendEvent(i_$1.height)
}};
if (!this.hassetheight && this.resourceheight != this.height || this._setrescheight && this.unstretchedheight != this.resourceheight) {
this.updateHeight(this.resourceheight)
}};
if (i_$1.skiponload != true) {
if (this.onload.ready) {
this.onload.sendEvent(this)
}}};
$lzsc$temp["displayName"] = "resourceload";
return $lzsc$temp
})(), "resourceloaderror", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
this.resourcewidth = 0;
this.resourceheight = 0;
if (this.onresourcewidth.ready) {
this.onresourcewidth.sendEvent(0)
};
if (this.onresourceheight.ready) {
this.onresourceheight.sendEvent(0)
};
this.reevaluateSize();
if (this.onerror.ready) {
this.onerror.sendEvent(e_$1)
}};
$lzsc$temp["displayName"] = "resourceloaderror";
return $lzsc$temp
})(), "resourceloadtimeout", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
this.resourcewidth = 0;
this.resourceheight = 0;
if (this.onresourcewidth.ready) {
this.onresourcewidth.sendEvent(0)
};
if (this.onresourceheight.ready) {
this.onresourceheight.sendEvent(0)
};
this.reevaluateSize();
if (this.ontimeout.ready) {
this.ontimeout.sendEvent(e_$1)
}};
$lzsc$temp["displayName"] = "resourceloadtimeout";
return $lzsc$temp
})(), "resourceevent", (function  () {
var $lzsc$temp = function  (name_$1, value_$2, eventonly_$3, force_$4) {
switch (arguments.length) {
case 2:
eventonly_$3 = false;;case 3:
force_$4 = false
};
var sendevent_$5 = force_$4 == true || eventonly_$3 == true || this[name_$1] != value_$2;
if (eventonly_$3 != true) {
this[name_$1] = value_$2
};
if (sendevent_$5) {
var ev_$6 = this["on" + name_$1];
if (ev_$6.ready) {
ev_$6.sendEvent(value_$2)
}}};
$lzsc$temp["displayName"] = "resourceevent";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
if (this.__LZdeleted) {
return
};
if (this.sprite) {
this.sprite.predestroy()
};
var vip_$1 = this.immediateparent;
if (this.addedToParent) {
var svs_$2 = vip_$1.subviews;
if (svs_$2 != null) {
for (var i_$3 = svs_$2.length - 1;i_$3 >= 0;i_$3--) {
if (svs_$2[i_$3] == this) {
svs_$2.splice(i_$3, 1);
break
}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this);
if (this.sprite) {
this.sprite.destroy()
};
this.$lzc$set_visible(false);
var vip_$1 = this.immediateparent;
if (this.addedToParent) {
if (vip_$1["__LZoutliewidth"] == this) {
vip_$1.__LZoutliewidth = null
};
if (vip_$1["__LZoutlieheight"] == this) {
vip_$1.__LZoutlieheight = null
};
if (vip_$1.onremovesubview.ready) {
vip_$1.onremovesubview.sendEvent(this)
}}};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "setVisible", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(v_$1)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "setVisibility", (function  () {
var $lzsc$temp = function  (amVisible_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visibility(amVisible_$1)
};
$lzsc$temp["displayName"] = "setVisibility";
return $lzsc$temp
})(), "__LZupdateShown", (function  () {
var $lzsc$temp = function  () {
if (this.visibility == "collapse") {
var shown_$1 = this.__LZvizO && this.__LZvizDat && this.__LZvizLoad
} else {
var shown_$1 = this.visibility == "visible"
};
if (shown_$1 != this.visible) {
this.visible = shown_$1;
if (this.sprite) {
this.sprite.setVisible(shown_$1)
};
var vip_$2 = this.immediateparent;
if (vip_$2 && vip_$2.__LZcheckwidth) {
vip_$2.__LZcheckwidthFunction(this)
};
if (vip_$2 && vip_$2.__LZcheckheight) {
vip_$2.__LZcheckheightFunction(this)
};
if (this.onvisible.ready) {
this.onvisible.sendEvent(shown_$1)
}}};
$lzsc$temp["displayName"] = "__LZupdateShown";
return $lzsc$temp
})(), "setWidth", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_width(v_$1)
};
$lzsc$temp["displayName"] = "setWidth";
return $lzsc$temp
})(), "setHeight", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_height(v_$1)
};
$lzsc$temp["displayName"] = "setHeight";
return $lzsc$temp
})(), "setOpacity", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_opacity(v_$1)
};
$lzsc$temp["displayName"] = "setOpacity";
return $lzsc$temp
})(), "setX", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_x(v_$1)
};
$lzsc$temp["displayName"] = "setX";
return $lzsc$temp
})(), "setY", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_y(v_$1)
};
$lzsc$temp["displayName"] = "setY";
return $lzsc$temp
})(), "setRotation", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_rotation(v_$1)
};
$lzsc$temp["displayName"] = "setRotation";
return $lzsc$temp
})(), "setAlign", (function  () {
var $lzsc$temp = function  (align_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_align(align_$1)
};
$lzsc$temp["displayName"] = "setAlign";
return $lzsc$temp
})(), "__LZalignCenter", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
var vip_$2 = this.immediateparent;
this.$lzc$set_x(vip_$2.width / 2 - this.width / 2)
};
$lzsc$temp["displayName"] = "__LZalignCenter";
return $lzsc$temp
})(), "__LZalignRight", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
var vip_$2 = this.immediateparent;
this.$lzc$set_x(vip_$2.width - this.width)
};
$lzsc$temp["displayName"] = "__LZalignRight";
return $lzsc$temp
})(), "setXOffset", (function  () {
var $lzsc$temp = function  (o_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_xoffset(o_$1)
};
$lzsc$temp["displayName"] = "setXOffset";
return $lzsc$temp
})(), "setYOffset", (function  () {
var $lzsc$temp = function  (o_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_yoffset(o_$1)
};
$lzsc$temp["displayName"] = "setYOffset";
return $lzsc$temp
})(), "getBounds", (function  () {
var $lzsc$temp = function  () {
var mtrix_$1 = [-this.xoffset, -this.yoffset, this.width - this.xoffset, -this.yoffset, -this.xoffset, this.height - this.yoffset, this.width - this.xoffset, this.height - this.yoffset, this.rotation, this.x, this.y];
if (this.__LZstoredbounds) {
var i_$2 = mtrix_$1.length - 1;
while (mtrix_$1[i_$2] == LzView.__LZlastmtrix[i_$2]) {
if (i_$2-- == 0) {
return this.__LZstoredbounds
}}};
var o_$3 = {};
for (var i_$2 = 0;i_$2 < 8;i_$2 += 2) {
var x_$4 = mtrix_$1[i_$2];
var y_$5 = mtrix_$1[i_$2 + 1];
var cx_$6 = x_$4 * this.__LZrcos - y_$5 * this.__LZrsin;
var cy_$7 = x_$4 * this.__LZrsin + y_$5 * this.__LZrcos;
if (o_$3.xoffset == null || o_$3.xoffset > cx_$6) {
o_$3.xoffset = cx_$6
};
if (o_$3.yoffset == null || o_$3.yoffset > cy_$7) {
o_$3.yoffset = cy_$7
};
if (o_$3.width == null || o_$3.width < cx_$6) {
o_$3.width = cx_$6
};
if (o_$3.height == null || o_$3.height < cy_$7) {
o_$3.height = cy_$7
}};
o_$3.width -= o_$3.xoffset;
o_$3.height -= o_$3.yoffset;
o_$3.x = this.x + o_$3.xoffset;
o_$3.y = this.y + o_$3.yoffset;
this.__LZstoredbounds = o_$3;
LzView.__LZlastmtrix = mtrix_$1;
return o_$3
};
$lzsc$temp["displayName"] = "getBounds";
return $lzsc$temp
})(), "$lzc$getBounds_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "rotation", self_$2, "x", self_$2, "y", self_$2, "width", self_$2, "height"]
};
$lzsc$temp["displayName"] = "$lzc$getBounds_dependencies";
return $lzsc$temp
})(), "setValign", (function  () {
var $lzsc$temp = function  (valign_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_valign(valign_$1)
};
$lzsc$temp["displayName"] = "setValign";
return $lzsc$temp
})(), "__LZvalignMiddle", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
var vip_$2 = this.immediateparent;
this.$lzc$set_y(vip_$2.height / 2 - this.height / 2)
};
$lzsc$temp["displayName"] = "__LZvalignMiddle";
return $lzsc$temp
})(), "__LZvalignBottom", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
var vip_$2 = this.immediateparent;
this.$lzc$set_y(vip_$2.height - this.height)
};
$lzsc$temp["displayName"] = "__LZvalignBottom";
return $lzsc$temp
})(), "setColor", (function  () {
var $lzsc$temp = function  (c_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fgcolor(c_$1)
};
$lzsc$temp["displayName"] = "setColor";
return $lzsc$temp
})(), "getColor", (function  () {
var $lzsc$temp = function  () {
return this.sprite.getColor()
};
$lzsc$temp["displayName"] = "getColor";
return $lzsc$temp
})(), "setColorTransform", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.capabilities.colortransform) {
this.sprite.setColorTransform(o_$1)
} else {
LzView.__warnCapability("view.setColorTransform()", "colortransform")
}};
$lzsc$temp["displayName"] = "setColorTransform";
return $lzsc$temp
})(), "getColorTransform", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.colortransform) {
return this.sprite.getColorTransform()
} else {
LzView.__warnCapability("view.getColorTransform()", "colortransform")
}};
$lzsc$temp["displayName"] = "getColorTransform";
return $lzsc$temp
})(), "__LZcheckSize", (function  () {
var $lzsc$temp = function  (sview_$1, axis_$2, xory_$3) {
if (sview_$1.addedToParent) {
if (sview_$1.__LZhasoffset || sview_$1.rotation != 0) {
var bobj_$4 = sview_$1.getBounds()
} else {
var bobj_$4 = sview_$1
};
var ss_$5 = bobj_$4[xory_$3] + bobj_$4[axis_$2];
var ts_$6 = this["_setresc" + axis_$2] ? this["unstretched" + axis_$2] : this[axis_$2];
if (ss_$5 > ts_$6 && sview_$1.visible) {
this["__LZoutlie" + axis_$2] = sview_$1;
if (axis_$2 == "width") {
this.updateWidth(ss_$5)
} else {
this.updateHeight(ss_$5)
}} else {
if (this["__LZoutlie" + axis_$2] == sview_$1 && (ss_$5 < ts_$6 || !sview_$1.visible)) {
this.reevaluateSize(axis_$2)
}}}};
$lzsc$temp["displayName"] = "__LZcheckSize";
return $lzsc$temp
})(), "__LZcheckwidthFunction", (function  () {
var $lzsc$temp = function  (sview_$1) {
this.__LZcheckSize(sview_$1, "width", "x")
};
$lzsc$temp["displayName"] = "__LZcheckwidthFunction";
return $lzsc$temp
})(), "__LZcheckheightFunction", (function  () {
var $lzsc$temp = function  (sview_$1) {
this.__LZcheckSize(sview_$1, "height", "y")
};
$lzsc$temp["displayName"] = "__LZcheckheightFunction";
return $lzsc$temp
})(), "measureSize", (function  () {
var $lzsc$temp = function  (axis_$1) {
var w_$2 = this["resource" + axis_$1];
for (var i_$3 = this.subviews.length - 1;i_$3 >= 0;i_$3--) {
var sview_$4 = this.subviews[i_$3];
if (sview_$4.visible) {
if (sview_$4.__LZhasoffset || sview_$4.rotation != 0) {
var bobj_$5 = sview_$4.getBounds()
} else {
var bobj_$5 = sview_$4
};
var svs_$6 = bobj_$5[axis_$1 == "width" ? "x" : "y"] + bobj_$5[axis_$1];
if (svs_$6 > w_$2) {
w_$2 = svs_$6
}}};
return w_$2
};
$lzsc$temp["displayName"] = "measureSize";
return $lzsc$temp
})(), "measureWidth", (function  () {
var $lzsc$temp = function  () {
return this.measureSize("width")
};
$lzsc$temp["displayName"] = "measureWidth";
return $lzsc$temp
})(), "measureHeight", (function  () {
var $lzsc$temp = function  () {
return this.measureSize("height")
};
$lzsc$temp["displayName"] = "measureHeight";
return $lzsc$temp
})(), "updateSize", (function  () {
var $lzsc$temp = function  (axis_$1, newsize_$2) {
if (axis_$1 == "width") {
this.updateWidth(newsize_$2)
} else {
this.updateHeight(newsize_$2)
}};
$lzsc$temp["displayName"] = "updateSize";
return $lzsc$temp
})(), "updateWidth", (function  () {
var $lzsc$temp = function  (newsize_$1) {
if (this._setrescwidth) {
this.unstretchedwidth = newsize_$1;
if (this.hassetwidth) {
var scale_$2 = this.width / newsize_$1;
this._xscale = scale_$2
};
if (this.onunstretchedwidth.ready) {
this.onunstretchedwidth.sendEvent(newsize_$1)
}};
if (!this.hassetwidth) {
this.width = newsize_$1;
this.sprite.setWidth(newsize_$1);
if (this.onwidth.ready) {
this.onwidth.sendEvent(newsize_$1)
};
var vip_$3 = this.immediateparent;
if (vip_$3.__LZcheckwidth) {
vip_$3.__LZcheckwidthFunction(this)
}}};
$lzsc$temp["displayName"] = "updateWidth";
return $lzsc$temp
})(), "updateHeight", (function  () {
var $lzsc$temp = function  (newsize_$1) {
if (this._setrescheight) {
this.unstretchedheight = newsize_$1;
if (this.hassetheight) {
var scale_$2 = this.height / newsize_$1;
this._yscale = scale_$2
};
if (this.onunstretchedheight) {
if (this.onunstretchedheight.ready) {
this.onunstretchedheight.sendEvent(newsize_$1)
}}};
if (!this.hassetheight) {
this.height = newsize_$1;
this.sprite.setHeight(newsize_$1);
if (this.onheight.ready) {
this.onheight.sendEvent(newsize_$1)
};
var vip_$3 = this.immediateparent;
if (vip_$3.__LZcheckheight) {
vip_$3.__LZcheckheightFunction(this)
}}};
$lzsc$temp["displayName"] = "updateHeight";
return $lzsc$temp
})(), "reevaluateSize", (function  () {
var $lzsc$temp = function  (ia_$1) {
switch (arguments.length) {
case 0:
ia_$1 = null
};
if (ia_$1 == null) {
var axis_$2 = "height";
this.reevaluateSize("width")
} else {
var axis_$2 = ia_$1
};
if (this["hasset" + axis_$2] && !this["_setresc" + axis_$2]) {
return
};
var o_$3 = this["_setresc" + axis_$2] ? this["unstretched" + axis_$2] : this[axis_$2];
var w_$4 = this["resource" + axis_$2] || 0;
this["__LZoutlie" + axis_$2] = this;
for (var i_$5 = this.subviews.length - 1;i_$5 >= 0;i_$5--) {
var sv_$6 = this.subviews[i_$5];
if (sv_$6.__LZhasoffset || sv_$6.rotation != 0) {
var b_$7 = sv_$6.getBounds();
var svs_$8 = b_$7[axis_$2 == "width" ? "x" : "y"] + b_$7[axis_$2]
} else {
var svs_$8 = sv_$6[axis_$2 == "width" ? "x" : "y"] + sv_$6[axis_$2]
};
if (sv_$6.visible && svs_$8 > w_$4) {
w_$4 = svs_$8;
this["__LZoutlie" + axis_$2] = sv_$6
}};
if (o_$3 != w_$4) {
if (axis_$2 == "width") {
this.updateWidth(w_$4)
} else {
this.updateHeight(w_$4)
}}};
$lzsc$temp["displayName"] = "reevaluateSize";
return $lzsc$temp
})(), "updateResourceSize", (function  () {
var $lzsc$temp = function  () {
this.sprite.updateResourceSize();
this.reevaluateSize()
};
$lzsc$temp["displayName"] = "updateResourceSize";
return $lzsc$temp
})(), "setAttributeRelative", (function  () {
var $lzsc$temp = function  (prop_$1, refView_$2) {
var tLink_$3 = this.getLinkage(refView_$2);
var val_$4 = refView_$2[prop_$1];
if (prop_$1 == "x" || prop_$1 == "y") {
tLink_$3.update(prop_$1);
var $lzsc$2145741278 = (val_$4 - tLink_$3.offset[prop_$1]) / tLink_$3.scale[prop_$1];
if (!this.__LZdeleted) {
var $lzsc$130621300 = "$lzc$set_" + prop_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$130621300]) : this[$lzsc$130621300] instanceof Function) {
this[$lzsc$130621300]($lzsc$2145741278)
} else {
this[prop_$1] = $lzsc$2145741278;
var $lzsc$407947654 = this["on" + prop_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$407947654) : $lzsc$407947654 instanceof LzEvent) {
if ($lzsc$407947654.ready) {
$lzsc$407947654.sendEvent($lzsc$2145741278)
}}}}} else {
if (prop_$1 == "width" || prop_$1 == "height") {
var axis_$5 = prop_$1 == "width" ? "x" : "y";
tLink_$3.update(axis_$5);
var $lzsc$54463718 = val_$4 / tLink_$3.scale[axis_$5];
if (!this.__LZdeleted) {
var $lzsc$1952726361 = "$lzc$set_" + prop_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$1952726361]) : this[$lzsc$1952726361] instanceof Function) {
this[$lzsc$1952726361]($lzsc$54463718)
} else {
this[prop_$1] = $lzsc$54463718;
var $lzsc$1540391106 = this["on" + prop_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1540391106) : $lzsc$1540391106 instanceof LzEvent) {
if ($lzsc$1540391106.ready) {
$lzsc$1540391106.sendEvent($lzsc$54463718)
}}}}} else {

}}};
$lzsc$temp["displayName"] = "setAttributeRelative";
return $lzsc$temp
})(), "$lzc$setAttributeRelative_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2, prop_$3, refView_$4) {
var tLink_$5 = who_$1.getLinkage(refView_$4);
var pass_$6 = 2;
var d_$7 = [];
if (prop_$3 == "width") {
var ax_$8 = "x"
} else {
if (prop_$3 == "height") {
var ax_$8 = "y"
} else {
var ax_$8 = prop_$3
}};
var sax_$9 = ax_$8 == "x" ? "width" : "height";
while (pass_$6) {
if (pass_$6 == 2) {
var carr_$10 = tLink_$5.uplinkArray
} else {
var carr_$10 = tLink_$5.downlinkArray
};
pass_$6--;
for (var i_$11 = carr_$10.length - 1;i_$11 >= 0;i_$11--) {
d_$7.push(carr_$10[i_$11], ax_$8);
if (d_$7["_setresc" + sax_$9]) {
d_$7.push([carr_$10[i_$11], sax_$9])
}}};
return d_$7
};
$lzsc$temp["displayName"] = "$lzc$setAttributeRelative_dependencies";
return $lzsc$temp
})(), "getAttributeRelative", (function  () {
var $lzsc$temp = function  (prop_$1, refView_$2) {
var tLink_$3 = this.getLinkage(refView_$2);
if (prop_$1 == "x" || prop_$1 == "y") {
tLink_$3.update(prop_$1);
return tLink_$3.offset[prop_$1] + tLink_$3.scale[prop_$1] * this[prop_$1]
} else {
if (prop_$1 == "width" || prop_$1 == "height") {
var axis_$4 = prop_$1 == "width" ? "x" : "y";
tLink_$3.update(axis_$4);
return tLink_$3.scale[axis_$4] * this[prop_$1]
} else {

}}};
$lzsc$temp["displayName"] = "getAttributeRelative";
return $lzsc$temp
})(), "$lzc$getAttributeRelative_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2, prop_$3, refView_$4) {
var tLink_$5 = self_$2.getLinkage(refView_$4);
var pass_$6 = 2;
var d_$7 = [self_$2, prop_$3];
if (prop_$3 == "width") {
var ax_$8 = "x"
} else {
if (prop_$3 == "height") {
var ax_$8 = "y"
} else {
var ax_$8 = prop_$3
}};
var sax_$9 = ax_$8 == "x" ? "width" : "height";
while (pass_$6) {
if (pass_$6 == 2) {
var carr_$10 = tLink_$5.uplinkArray
} else {
var carr_$10 = tLink_$5.downlinkArray
};
pass_$6--;
for (var i_$11 = carr_$10.length - 1;i_$11 >= 0;i_$11--) {
var ci_$12 = carr_$10[i_$11];
d_$7.push(ci_$12, ax_$8);
if (ci_$12["_setresc" + sax_$9]) {
d_$7.push(ci_$12, sax_$9)
}}};
return d_$7
};
$lzsc$temp["displayName"] = "$lzc$getAttributeRelative_dependencies";
return $lzsc$temp
})(), "__LZviewLinks", null, "getLinkage", (function  () {
var $lzsc$temp = function  (refView_$1) {
if (this.__LZviewLinks == null) {
this.__LZviewLinks = new Object()
};
var uid_$2 = refView_$1.getUID();
if (this.__LZviewLinks[uid_$2] == null) {
this.__LZviewLinks[uid_$2] = new LzViewLinkage(this, refView_$1)
};
return this.__LZviewLinks[uid_$2]
};
$lzsc$temp["displayName"] = "getLinkage";
return $lzsc$temp
})(), "mouseevent", (function  () {
var $lzsc$temp = function  (eventname_$1) {
if (this[eventname_$1] && this[eventname_$1].ready) {
this[eventname_$1].sendEvent(this)
}};
$lzsc$temp["displayName"] = "mouseevent";
return $lzsc$temp
})(), "getMouse", (function  () {
var $lzsc$temp = function  (xory_$1) {
if (this.__movecounter != lz.GlobalMouse.__movecounter || this.__mousecache == null) {
this.__movecounter = lz.GlobalMouse.__movecounter;
this.__mousecache = this.sprite.getMouse(xory_$1)
};
if (xory_$1 == null) {
return this.__mousecache
};
return this.__mousecache[xory_$1]
};
$lzsc$temp["displayName"] = "getMouse";
return $lzsc$temp
})(), "$lzc$getMouse_dependencies", (function  () {
var $lzsc$temp = function  () {
var ignore_$1 = Array.prototype.slice.call(arguments, 0);
return [lz.Idle, "idle"]
};
$lzsc$temp["displayName"] = "$lzc$getMouse_dependencies";
return $lzsc$temp
})(), "containsPt", (function  () {
var $lzsc$temp = function  (x_$1, y_$2) {
var offsetx_$3 = 0;
var offsety_$4 = 0;
var view_$5 = this;
do{
if (!view_$5.visible) {
return false
};
if (view_$5.masked || view_$5 === this) {
var vx_$6 = x_$1 - offsetx_$3;
var vy_$7 = y_$2 - offsety_$4;
if (vx_$6 < 0 || vx_$6 >= view_$5.width || vy_$7 < 0 || vy_$7 >= view_$5.height) {
return false
}};
offsetx_$3 -= view_$5.x;
offsety_$4 -= view_$5.y;
view_$5 = view_$5.immediateparent
} while (view_$5 !== canvas);
return true
};
$lzsc$temp["displayName"] = "containsPt";
return $lzsc$temp
})(), "bringToFront", (function  () {
var $lzsc$temp = function  () {
if (!this.sprite) {
Debug.warn("no sprite on ", this);
return
};
this.sprite.bringToFront()
};
$lzsc$temp["displayName"] = "bringToFront";
return $lzsc$temp
})(), "getDepthList", (function  () {
var $lzsc$temp = function  () {
var o_$1 = this.subviews.concat();
o_$1.sort(this.__zCompare);
return o_$1
};
$lzsc$temp["displayName"] = "getDepthList";
return $lzsc$temp
})(), "__zCompare", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var az_$3 = a_$1.sprite.getZ();
var bz_$4 = b_$2.sprite.getZ();
if (az_$3 < bz_$4) {
return -1
};
if (az_$3 > bz_$4) {
return 1
};
return 0
};
$lzsc$temp["displayName"] = "__zCompare";
return $lzsc$temp
})(), "sendBehind", (function  () {
var $lzsc$temp = function  (v_$1) {
return v_$1 ? this.sprite.sendBehind(v_$1.sprite) : false
};
$lzsc$temp["displayName"] = "sendBehind";
return $lzsc$temp
})(), "sendInFrontOf", (function  () {
var $lzsc$temp = function  (v_$1) {
return v_$1 ? this.sprite.sendInFrontOf(v_$1.sprite) : false
};
$lzsc$temp["displayName"] = "sendInFrontOf";
return $lzsc$temp
})(), "sendToBack", (function  () {
var $lzsc$temp = function  () {
this.sprite.sendToBack()
};
$lzsc$temp["displayName"] = "sendToBack";
return $lzsc$temp
})(), "setResourceNumber", (function  () {
var $lzsc$temp = function  (n_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_frame(n_$1)
};
$lzsc$temp["displayName"] = "setResourceNumber";
return $lzsc$temp
})(), "stretchResource", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_stretches(v_$1)
};
$lzsc$temp["displayName"] = "stretchResource";
return $lzsc$temp
})(), "setBGColor", (function  () {
var $lzsc$temp = function  (bgc_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_bgcolor(bgc_$1)
};
$lzsc$temp["displayName"] = "setBGColor";
return $lzsc$temp
})(), "setSource", (function  () {
var $lzsc$temp = function  (source_$1, cache_$2, headers_$3, filetype_$4) {
switch (arguments.length) {
case 1:
cache_$2 = null;;case 2:
headers_$3 = null;;case 3:
filetype_$4 = null
};
this.sprite.setSource(source_$1, cache_$2, headers_$3, filetype_$4)
};
$lzsc$temp["displayName"] = "setSource";
return $lzsc$temp
})(), "unload", (function  () {
var $lzsc$temp = function  () {
this._resource = null;
this.sprite.unload()
};
$lzsc$temp["displayName"] = "unload";
return $lzsc$temp
})(), "makeMasked", (function  () {
var $lzsc$temp = function  () {
this.sprite.setClip(true);
this.masked = true;
this.mask = this
};
$lzsc$temp["displayName"] = "makeMasked";
return $lzsc$temp
})(), "removeMask", (function  () {
var $lzsc$temp = function  () {
this.sprite.setClip(false);
this.masked = false;
this.mask = null
};
$lzsc$temp["displayName"] = "removeMask";
return $lzsc$temp
})(), "setClickable", (function  () {
var $lzsc$temp = function  (amclickable_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_clickable(amclickable_$1)
};
$lzsc$temp["displayName"] = "setClickable";
return $lzsc$temp
})(), "$lzc$set_cursor", (function  () {
var $lzsc$temp = function  (cursor_$1) {
this.sprite.setCursor(cursor_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_cursor";
return $lzsc$temp
})(), "setCursor", (function  () {
var $lzsc$temp = function  (cursor_$1) {
switch (arguments.length) {
case 0:
cursor_$1 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_cursor(cursor_$1)
};
$lzsc$temp["displayName"] = "setCursor";
return $lzsc$temp
})(), "$lzc$set_play", (function  () {
var $lzsc$temp = function  (b_$1) {
if (b_$1) {
this.play()
} else {
this.stop()
}};
$lzsc$temp["displayName"] = "$lzc$set_play";
return $lzsc$temp
})(), "setPlay", (function  () {
var $lzsc$temp = function  (b_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_play(b_$1)
};
$lzsc$temp["displayName"] = "setPlay";
return $lzsc$temp
})(), "getMCRef", (function  () {
var $lzsc$temp = function  () {
return this.sprite.getMCRef()
};
$lzsc$temp["displayName"] = "getMCRef";
return $lzsc$temp
})(), "play", (function  () {
var $lzsc$temp = function  (f_$1, rel_$2) {
switch (arguments.length) {
case 0:
f_$1 = null;;case 1:
rel_$2 = false
};
this.sprite.play(f_$1, rel_$2)
};
$lzsc$temp["displayName"] = "play";
return $lzsc$temp
})(), "stop", (function  () {
var $lzsc$temp = function  (f_$1, rel_$2) {
switch (arguments.length) {
case 0:
f_$1 = null;;case 1:
rel_$2 = false
};
this.sprite.stop(f_$1, rel_$2)
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "setVolume", (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.capabilities.audio) {
this.sprite.setVolume(v_$1)
} else {
LzView.__warnCapability("view.setVolume()", "audio")
}};
$lzsc$temp["displayName"] = "setVolume";
return $lzsc$temp
})(), "getVolume", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
return this.sprite.getVolume()
} else {
LzView.__warnCapability("view.getVolume()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getVolume";
return $lzsc$temp
})(), "setPan", (function  () {
var $lzsc$temp = function  (p_$1) {
if (this.capabilities.audio) {
this.sprite.setPan(p_$1)
} else {
LzView.__warnCapability("view.setPan()", "audio")
}};
$lzsc$temp["displayName"] = "setPan";
return $lzsc$temp
})(), "getPan", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
return this.sprite.getPan()
} else {
LzView.__warnCapability("view.getPan()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getPan";
return $lzsc$temp
})(), "getZ", (function  () {
var $lzsc$temp = function  () {
return this.sprite.getZ()
};
$lzsc$temp["displayName"] = "getZ";
return $lzsc$temp
})(), "seek", (function  () {
var $lzsc$temp = function  (secs_$1) {
var m_$2 = this.getMCRef();
if (m_$2.isaudio == true) {
m_$2.seek(secs_$1, this.playing)
} else {
var f_$3 = secs_$1 * canvas.framerate;
if (this.playing) {
this.play(f_$3, true)
} else {
this.stop(f_$3, true)
}}};
$lzsc$temp["displayName"] = "seek";
return $lzsc$temp
})(), "getCurrentTime", (function  () {
var $lzsc$temp = function  () {
var m_$1 = this.getMCRef();
if (m_$1.isaudio == true) {
return m_$1.getCurrentTime()
} else {
return this.frame / canvas.framerate
}};
$lzsc$temp["displayName"] = "getCurrentTime";
return $lzsc$temp
})(), "$lzc$getCurrentTime_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "frame"]
};
$lzsc$temp["displayName"] = "$lzc$getCurrentTime_dependencies";
return $lzsc$temp
})(), "getTotalTime", (function  () {
var $lzsc$temp = function  () {
var m_$1 = this.getMCRef();
if (m_$1.isaudio == true) {
return m_$1.getTotalTime()
} else {
return this.totalframes / canvas.framerate
}};
$lzsc$temp["displayName"] = "getTotalTime";
return $lzsc$temp
})(), "$lzc$getTotalTime_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "load"]
};
$lzsc$temp["displayName"] = "$lzc$getTotalTime_dependencies";
return $lzsc$temp
})(), "getID3", (function  () {
var $lzsc$temp = function  () {
var m_$1 = this.getMCRef();
if (m_$1.isaudio == true) {
return m_$1.getID3()
};
return null
};
$lzsc$temp["displayName"] = "getID3";
return $lzsc$temp
})(), "setShowHandCursor", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_showhandcursor(s_$1)
};
$lzsc$temp["displayName"] = "setShowHandCursor";
return $lzsc$temp
})(), "setAccessible", (function  () {
var $lzsc$temp = function  (accessible_$1) {
if (this.capabilities.accessibility) {
this.sprite.setAccessible(accessible_$1)
} else {
LzView.__warnCapability("view.setAccessible()", "accessibility")
}};
$lzsc$temp["displayName"] = "setAccessible";
return $lzsc$temp
})(), "setAAActive", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aaactive(s_$1)
};
$lzsc$temp["displayName"] = "setAAActive";
return $lzsc$temp
})(), "setAAName", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aaname(s_$1)
};
$lzsc$temp["displayName"] = "setAAName";
return $lzsc$temp
})(), "setAADescription", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aadescription(s_$1)
};
$lzsc$temp["displayName"] = "setAADescription";
return $lzsc$temp
})(), "setAATabIndex", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aatabindex(s_$1)
};
$lzsc$temp["displayName"] = "setAATabIndex";
return $lzsc$temp
})(), "setAASilent", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aasilent(s_$1)
};
$lzsc$temp["displayName"] = "setAASilent";
return $lzsc$temp
})(), "shouldYieldFocus", (function  () {
var $lzsc$temp = function  () {
return true
};
$lzsc$temp["displayName"] = "shouldYieldFocus";
return $lzsc$temp
})(), "blurring", false, "getProxyURL", (function  () {
var $lzsc$temp = function  (url_$1) {
switch (arguments.length) {
case 0:
url_$1 = null
};
var proxy_$2 = this.proxyurl;
if (proxy_$2 == null) {
return null
} else {
if (typeof proxy_$2 == "string") {
return proxy_$2
} else {
if (typeof proxy_$2 == "function") {
return proxy_$2(url_$1)
} else {
Debug.error("Unknown value for proxyurl expression %w on %w", proxy_$2, this)
}}}};
$lzsc$temp["displayName"] = "getProxyURL";
return $lzsc$temp
})(), "__LZcheckProxyPolicy", (function  () {
var $lzsc$temp = function  (url_$1) {
if (this.__proxypolicy != null) {
return this.__proxypolicy(url_$1)
};
var pol_$2 = LzView.__LZproxypolicies;
for (var i_$3 = pol_$2.length - 1;i_$3 >= 0;i_$3--) {
var resp_$4 = pol_$2[i_$3](url_$1);
if (resp_$4 != null) {
return resp_$4
}};
return canvas.proxied
};
$lzsc$temp["displayName"] = "__LZcheckProxyPolicy";
return $lzsc$temp
})(), "setProxyPolicy", (function  () {
var $lzsc$temp = function  (f_$1) {
this.__proxypolicy = f_$1
};
$lzsc$temp["displayName"] = "setProxyPolicy";
return $lzsc$temp
})(), "__proxypolicy", null, "setProxyURL", (function  () {
var $lzsc$temp = function  (f_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_proxyurl(f_$1)
};
$lzsc$temp["displayName"] = "setProxyURL";
return $lzsc$temp
})(), "proxyurl", (function  () {
var $lzsc$temp = function  (url_$1) {
return canvas.getProxyURL(url_$1)
};
$lzsc$temp["displayName"] = "views/LaszloView.lzs#3269/16";
return $lzsc$temp
})(), "$lzc$set_proxyurl", (function  () {
var $lzsc$temp = function  (f_$1) {
this.proxyurl = f_$1
};
$lzsc$temp["displayName"] = "$lzc$set_proxyurl";
return $lzsc$temp
})(), "contextmenu", null, "$lzc$set_contextmenu", (function  () {
var $lzsc$temp = function  (cmenu_$1) {
this.contextmenu = cmenu_$1;
this.sprite.setContextMenu(cmenu_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_contextmenu";
return $lzsc$temp
})(), "setContextMenu", (function  () {
var $lzsc$temp = function  (cmenu_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_contextmenu(cmenu_$1)
};
$lzsc$temp["displayName"] = "setContextMenu";
return $lzsc$temp
})(), "getContextMenu", (function  () {
var $lzsc$temp = function  () {
Debug.deprecated(this, arguments.callee, "contextmenu");
return this.contextmenu
};
$lzsc$temp["displayName"] = "getContextMenu";
return $lzsc$temp
})(), "getNextSelection", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "getNextSelection";
return $lzsc$temp
})(), "getPrevSelection", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "getPrevSelection";
return $lzsc$temp
})(), "cachebitmap", false, "$lzc$set_cachebitmap", (function  () {
var $lzsc$temp = function  (cache_$1) {
if (cache_$1 != this.cachebitmap) {
this.cachebitmap = cache_$1;
if (this.capabilities.bitmapcaching) {
this.sprite.setBitmapCache(cache_$1)
} else {
LzView.__warnCapability("view.cachebitmap", "bitmapcaching")
}}};
$lzsc$temp["displayName"] = "$lzc$set_cachebitmap";
return $lzsc$temp
})()], ["tagname", "view", "attributes", new LzInheritedHash(LzNode.attributes), "__LZdelayedSetters", new LzInheritedHash(LzNode.__LZdelayedSetters), "earlySetters", new LzInheritedHash(LzNode.earlySetters), "__LZlastmtrix", [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], "__LZproxypolicies", [], "addProxyPolicy", (function  () {
var $lzsc$temp = function  (f_$1) {
LzView.__LZproxypolicies.push(f_$1)
};
$lzsc$temp["displayName"] = "addProxyPolicy";
return $lzsc$temp
})(), "removeProxyPolicy", (function  () {
var $lzsc$temp = function  (f_$1) {
var pol_$2 = LzView.__LZproxypolicies;
for (var i_$3 = 0;i_$3 < pol_$2.length;i_$3++) {
if (pol_$2[i_$3] == f_$1) {
LzView.__LZproxypolicies = pol_$2.splice(i_$3, 1);
return true
}};
return false
};
$lzsc$temp["displayName"] = "removeProxyPolicy";
return $lzsc$temp
})(), "__warnCapability", (function  () {
var $lzsc$temp = function  (msg_$1, capabilityname_$2) {
switch (arguments.length) {
case 1:
capabilityname_$2 = ""
};
var check_$3 = capabilityname_$2 == "" ? "" : 'Check "canvas.capabilities.' + capabilityname_$2 + '" to avoid this warning.';
Debug.warn("The %s runtime does not support %s. %s", canvas["runtime"], msg_$1, check_$3)
};
$lzsc$temp["displayName"] = "__warnCapability";
return $lzsc$temp
})()]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzView.__LZdelayedSetters.layout = "$lzc$set_layout";
LzView.earlySetters.clickregion = 7;
LzView.earlySetters.stretches = 8
}}};
$lzsc$temp["displayName"] = "views/LaszloView.lzs#46/1";
return $lzsc$temp
})()(LzView);
lz[LzView.tagname] = LzView;
Class.make("LzText", [LzFormatter, LzView], ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "maxlines", 1, "selectable", false, "onselectable", LzDeclaredEvent, "$lzc$set_selectable", (function  () {
var $lzsc$temp = function  (isSel_$1) {
this.selectable = isSel_$1;
this.tsprite.setSelectable(isSel_$1);
if (this.onselectable.ready) {
this.onselectable.sendEvent(isSel_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_selectable";
return $lzsc$temp
})(), "antiAliasType", "advanced", "$lzc$set_antiAliasType", (function  () {
var $lzsc$temp = function  (aliasType_$1) {
if (this.capabilities.advancedfonts) {
if (aliasType_$1 == "normal" || aliasType_$1 == "advanced") {
this.antiAliasType = aliasType_$1;
this.tsprite.setAntiAliasType(aliasType_$1)
} else {
Debug.warn("antiAliasType invalid, must be 'normal' or 'advanced', but you said '" + aliasType_$1 + "'")
}} else {
LzView.__warnCapability("text.setAntiAliasType()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_antiAliasType";
return $lzsc$temp
})(), "gridFit", "pixel", "$lzc$set_gridFit", (function  () {
var $lzsc$temp = function  (gridFit_$1) {
if (this.capabilities.advancedfonts) {
if (gridFit_$1 == "none" || gridFit_$1 == "pixel" || gridFit_$1 == "subpixel") {
this.gridFit = gridFit_$1;
this.tsprite.setGridFit(gridFit_$1)
} else {
Debug.warn("gridFit invalid, must be 'none', 'pixel', or 'subpixel' but you said '" + gridFit_$1 + "'")
}} else {
LzView.__warnCapability("text.setGridFit()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_gridFit";
return $lzsc$temp
})(), "sharpness", 0, "$lzc$set_sharpness", (function  () {
var $lzsc$temp = function  (sharpness_$1) {
if (this.capabilities.advancedfonts) {
if (sharpness_$1 >= -400 && sharpness_$1 <= 400) {
this.sharpness = sharpness_$1;
this.tsprite.setSharpness(sharpness_$1)
} else {
Debug.warn("sharpness out of range, must be -400 to 400")
}} else {
LzView.__warnCapability("text.setSharpness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_sharpness";
return $lzsc$temp
})(), "thickness", 0, "$lzc$set_thickness", (function  () {
var $lzsc$temp = function  (thickness_$1) {
if (this.capabilities.advancedfonts) {
if (thickness_$1 >= -200 && thickness_$1 <= 200) {
this.thickness = thickness_$1;
this.tsprite.setThickness(thickness_$1)
} else {
Debug.warn("thickness out of range, must be -200 to 200")
}} else {
LzView.__warnCapability("text.setThickness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_thickness";
return $lzsc$temp
})(), "sizeToHeight", void 0, "$lzc$set_width", (function  () {
var $lzsc$temp = function  (val_$1) {
var tsprite_$2 = this.tsprite;
tsprite_$2.setWidth(val_$1);
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_width"] || this.nextMethod(arguments.callee, "$lzc$set_width")).call(this, val_$1);
if (this.scrollwidth < this.width) {
this.scrollwidth = this.width
};
this.updateAttribute("maxhscroll", this.scrollwidth - this.width);
if (this.sizeToHeight) {
var h_$3 = tsprite_$2.getTextfieldHeight();
if (h_$3 > 0 && h_$3 != this.height) {
this.$lzc$set_height(h_$3)
}}};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "getDefaultWidth", (function  () {
var $lzsc$temp = function  () {
return 0
};
$lzsc$temp["displayName"] = "getDefaultWidth";
return $lzsc$temp
})(), "updateAttribute", (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
this[name_$1] = value_$2;
var event_$3 = this["on" + name_$1];
if (event_$3.ready) {
event_$3.sendEvent(value_$2)
}};
$lzsc$temp["displayName"] = "updateAttribute";
return $lzsc$temp
})(), "updateLineAttribute", (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
var tsprite_$3 = this.tsprite;
var lineNo_$4;
if (this.capabilities.linescrolling) {
lineNo_$4 = tsprite_$3.pixelToLineNo(value_$2)
} else {
lineNo_$4 = Math.round(value_$2 / this.lineheight) + 1
};
this.updateAttribute(name_$1, lineNo_$4)
};
$lzsc$temp["displayName"] = "updateLineAttribute";
return $lzsc$temp
})(), "lineheight", 0, "$lzc$set_lineheight", (function  () {
var $lzsc$temp = function  (x_$1) {
Debug.error("lineheight is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_lineheight";
return $lzsc$temp
})(), "onlineheight", LzDeclaredEvent, "scrollevents", false, "$lzc$set_scrollevents", (function  () {
var $lzsc$temp = function  (on_$1) {
this.scrollevents = on_$1;
this.tsprite.setScrollEvents(on_$1);
if (this.onscrollevents.ready) {
this.onscrollevents.sendEvent(on_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_scrollevents";
return $lzsc$temp
})(), "onscrollevents", LzDeclaredEvent, "yscroll", 0, "$lzc$set_yscroll", (function  () {
var $lzsc$temp = function  (n_$1) {
if (n_$1 > 0) {
Debug.warn("Invalid value for %w.yscroll: %w", this, n_$1);
n_$1 = 0
};
this.tsprite.setYScroll(n_$1);
this.updateAttribute("yscroll", n_$1);
this.updateLineAttribute("scroll", -n_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_yscroll";
return $lzsc$temp
})(), "onyscroll", LzDeclaredEvent, "scrollheight", 0, "$lzc$set_scrollheight", (function  () {
var $lzsc$temp = function  (x_$1) {
Debug.error("scrollheight is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_scrollheight";
return $lzsc$temp
})(), "onscrollheight", LzDeclaredEvent, "xscroll", 0, "$lzc$set_xscroll", (function  () {
var $lzsc$temp = function  (n_$1) {
if (n_$1 > 0) {
Debug.warn("Invalid value for %w.xscroll: %w", this, n_$1);
n_$1 = 0
};
this.tsprite.setXScroll(n_$1);
this.updateAttribute("xscroll", n_$1);
this.updateAttribute("hscroll", -n_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_xscroll";
return $lzsc$temp
})(), "onxscroll", LzDeclaredEvent, "scrollwidth", 0, "$lzc$set_scrollwidth", (function  () {
var $lzsc$temp = function  (x_$1) {
Debug.error("scrollwidth is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_scrollwidth";
return $lzsc$temp
})(), "onscrollwidth", LzDeclaredEvent, "scroll", 1, "$lzc$set_scroll", (function  () {
var $lzsc$temp = function  (n_$1) {
if (n_$1 < 1 || n_$1 > this.maxscroll) {
Debug.warn("Invalid valuefor %w.scroll: %w (limits [1, %w])", this, n_$1, this.maxscroll);
n_$1 = n_$1 < 1 ? 1 : this.maxscroll
};
var tsprite_$2 = this.tsprite;
var pixel_$3;
if (this.capabilities.linescrolling) {
pixel_$3 = tsprite_$2.lineNoToPixel(n_$1)
} else {
pixel_$3 = (n_$1 - 1) * this.lineheight
};
this.$lzc$set_yscroll(-pixel_$3)
};
$lzsc$temp["displayName"] = "$lzc$set_scroll";
return $lzsc$temp
})(), "onscroll", LzDeclaredEvent, "maxscroll", 1, "$lzc$set_maxscroll", (function  () {
var $lzsc$temp = function  (x_$1) {
Debug.error("maxscroll is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_maxscroll";
return $lzsc$temp
})(), "onmaxscroll", LzDeclaredEvent, "hscroll", 0, "$lzc$set_hscroll", (function  () {
var $lzsc$temp = function  (n_$1) {
if (n_$1 < 0 || n_$1 > this.maxhscroll) {
Debug.warn("Invalid value for %w.hscroll: %w (limits [0, %w])", this, n_$1, this.maxhscroll);
n_$1 = n_$1 < 1 ? 1 : this.maxhscroll
};
this.$lzc$set_xscroll(-n_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_hscroll";
return $lzsc$temp
})(), "onhscroll", LzDeclaredEvent, "maxhscroll", 0, "$lzc$set_maxhscroll", (function  () {
var $lzsc$temp = function  (x_$1) {
Debug.error("maxhscroll is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_maxhscroll";
return $lzsc$temp
})(), "onmaxhscroll", LzDeclaredEvent, "scrollevent", (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
switch (name_$1) {
case "scrollTop":
this.updateAttribute("yscroll", -value_$2);
this.updateLineAttribute("scroll", value_$2);
break;;case "scrollLeft":
this.updateAttribute("xscroll", -value_$2);
this.updateAttribute("hscroll", value_$2);
break;;case "scrollHeight":
this.updateAttribute("scrollheight", value_$2);
this.updateLineAttribute("maxscroll", Math.max(0, value_$2 - this.height));
break;;case "scrollWidth":
this.updateAttribute("scrollwidth", value_$2);
this.updateAttribute("maxhscroll", Math.max(0, value_$2 - this.width));
break;;case "lineHeight":
this.updateAttribute("lineheight", value_$2);
if (this.inited) {
this.updateLineAttribute("scroll", -this.yscroll);
this.updateLineAttribute("maxscroll", Math.max(0, this.scrollheight - this.height))
};
break;;default:
Debug.error("%w: Uknown scrollevent %s (%w)", arguments.callee, name_$1, value_$2)
}};
$lzsc$temp["displayName"] = "scrollevent";
return $lzsc$temp
})(), "multiline", void 0, "$lzc$set_multiline", (function  () {
var $lzsc$temp = function  (ml_$1) {
this.multiline = ml_$1 = !(!ml_$1);
this.tsprite.setMultiline(ml_$1);
this._updateSize()
};
$lzsc$temp["displayName"] = "$lzc$set_multiline";
return $lzsc$temp
})(), "resize", true, "$lzc$set_resize", (function  () {
var $lzsc$temp = function  (val_$1) {
this.resize = val_$1;
this.tsprite.setResize(val_$1);
this._updateSize()
};
$lzsc$temp["displayName"] = "$lzc$set_resize";
return $lzsc$temp
})(), "text", "", "$lzc$set_text", (function  () {
var $lzsc$temp = function  (t_$1) {
t_$1 = String(t_$1);
if (t_$1 == this.getText()) {
if (this.ontext.ready) {
this.ontext.sendEvent(t_$1)
};
return
};
var tsprite_$2 = this.tsprite;
if (this.visible) {
tsprite_$2.setVisible(this.visible)
};
if (t_$1.length > this.maxlength) {
t_$1 = t_$1.substring(0, this.maxlength)
};
tsprite_$2.setText(t_$1);
this.text = t_$1;
this._updateSize();
if (this.ontext.ready) {
this.ontext.sendEvent(t_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_text";
return $lzsc$temp
})(), "_updateSize", (function  () {
var $lzsc$temp = function  () {
if (!this.isinited) {
return
};
if (this.width == 0 || this.resize && this.multiline == false) {
var w_$1 = this.getTextWidth();
if (w_$1 != this.width) {
this.$lzc$set_width(w_$1)
}};
if (this.sizeToHeight) {
var h_$2 = this.tsprite.getTextfieldHeight();
if (h_$2 > 0 && h_$2 != this.height) {
this.$lzc$set_height(h_$2)
}}};
$lzsc$temp["displayName"] = "_updateSize";
return $lzsc$temp
})(), "ontext", LzDeclaredEvent, "ontextlink", LzDeclaredEvent, "maxlength", Infinity, "$lzc$set_maxlength", (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 == null) {
val_$1 = Infinity
};
if (isNaN(val_$1)) {
Debug.warn("Invalid value for %w.maxlength: %w", this, val_$1);
return
};
this.maxlength = val_$1;
this.tsprite.setMaxLength(val_$1);
if (this.onmaxlength.ready) {
this.onmaxlength.sendEvent(val_$1)
};
var t_$2 = this.getText();
if (t_$2 && t_$2.length > this.maxlength) {
this._updateSize()
}};
$lzsc$temp["displayName"] = "$lzc$set_maxlength";
return $lzsc$temp
})(), "onmaxlength", LzDeclaredEvent, "pattern", void 0, "$lzc$set_pattern", (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 == null || val_$1 == "") {
return
};
this.pattern = val_$1;
this.tsprite.setPattern(val_$1);
if (this.onpattern.ready) {
this.onpattern.sendEvent(val_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_pattern";
return $lzsc$temp
})(), "onpattern", LzDeclaredEvent, "$lzc$set_fontstyle", (function  () {
var $lzsc$temp = function  (fstyle_$1) {
if (fstyle_$1 == "plain" || fstyle_$1 == "bold" || fstyle_$1 == "italic" || fstyle_$1 == "bolditalic" || fstyle_$1 == "bold italic") {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_fontstyle"] || this.nextMethod(arguments.callee, "$lzc$set_fontstyle")).call(this, fstyle_$1);
this.tsprite.setFontStyle(fstyle_$1);
this._updateSize()
} else {
Debug.warn("invalid font style", fstyle_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontstyle";
return $lzsc$temp
})(), "$lzc$set_font", (function  () {
var $lzsc$temp = function  (fname_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_font"] || this.nextMethod(arguments.callee, "$lzc$set_font")).call(this, fname_$1);
this.tsprite.setFontName(fname_$1);
this._updateSize()
};
$lzsc$temp["displayName"] = "$lzc$set_font";
return $lzsc$temp
})(), "$lzc$set_fontsize", (function  () {
var $lzsc$temp = function  (fsize_$1) {
if (fsize_$1 <= 0 || isNaN(fsize_$1)) {
Debug.warn("invalid font size", fsize_$1)
} else {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_fontsize"] || this.nextMethod(arguments.callee, "$lzc$set_fontsize")).call(this, fsize_$1);
this.tsprite.setFontSize(fsize_$1);
this._updateSize()
}};
$lzsc$temp["displayName"] = "$lzc$set_fontsize";
return $lzsc$temp
})(), "textalign", "left", "$lzc$set_textalign", (function  () {
var $lzsc$temp = function  (align_$1) {
align_$1 = align_$1 ? align_$1.toLowerCase() : "left";
if (!(align_$1 == "left" || align_$1 == "right" || align_$1 == "center" || align_$1 == "justify")) {
Debug.warn("Invalid value for %w.textalign: %w", this, align_$1);
align_$1 = "left"
};
this.textalign = align_$1;
this.tsprite.setTextAlign(align_$1);
this._updateSize()
};
$lzsc$temp["displayName"] = "$lzc$set_textalign";
return $lzsc$temp
})(), "textindent", 0, "$lzc$set_textindent", (function  () {
var $lzsc$temp = function  (indent_$1) {
if (indent_$1 < 0 || isNaN(indent_$1)) {
Debug.warn("invalid text indent", indent_$1)
} else {
this.textindent = indent_$1;
this.tsprite.setTextIndent(indent_$1);
this._updateSize()
}};
$lzsc$temp["displayName"] = "$lzc$set_textindent";
return $lzsc$temp
})(), "letterspacing", 0, "$lzc$set_letterspacing", (function  () {
var $lzsc$temp = function  (spacing_$1) {
if (spacing_$1 < 0 || isNaN(spacing_$1)) {
Debug.warn("invalid letter spacing", spacing_$1)
} else {
this.letterspacing = spacing_$1;
this.tsprite.setLetterSpacing(spacing_$1);
this._updateSize()
}};
$lzsc$temp["displayName"] = "$lzc$set_letterspacing";
return $lzsc$temp
})(), "textdecoration", "none", "$lzc$set_textdecoration", (function  () {
var $lzsc$temp = function  (decoration_$1) {
decoration_$1 = decoration_$1 ? decoration_$1.toLowerCase() : "none";
if (!(decoration_$1 == "none" || decoration_$1 == "underline")) {
Debug.warn("Invalid value for %w.textdecoration: %w", this, decoration_$1);
decoration_$1 = "none"
};
this.textdecoration = decoration_$1;
this.tsprite.setTextDecoration(decoration_$1);
this._updateSize()
};
$lzsc$temp["displayName"] = "$lzc$set_textdecoration";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
this.multiline = ("multiline" in args_$2) ? args_$2.multiline : null;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.sizeToHeight = false;
for (var arg_$3 in LzText.fontArgToAttr) {
var attr_$4 = LzText.fontArgToAttr[arg_$3];
if (!(arg_$3 in args_$2)) {
args_$2[arg_$3] = this.searchParents(attr_$4)[attr_$4]
};
this[attr_$4] = args_$2[arg_$3]
};
if (!("fgcolor" in args_$2)) {
var sview_$5 = this;
do{
sview_$5 = sview_$5.immediateparent;
var fg_$6 = sview_$5["hasfgcolor"];
if (fg_$6 != null && fg_$6) {
args_$2["fgcolor"] = sview_$5["fgcolor"];
break
}} while (sview_$5 != canvas)
};
var tsprite_$7 = this.tsprite;
tsprite_$7.__initTextProperties(args_$2);
for (var arg_$3 in LzText.fontArgToAttr) {
delete args_$2[arg_$3]
};
this.yscroll = 0;
this.xscroll = 0;
this.resize = ("resize" in args_$2) ? !(!args_$2.resize) : this.resize;
this.$lzc$set_resize(this.resize);
if (args_$2["maxlength"] != null) {
this.$lzc$set_maxlength(args_$2.maxlength)
};
this.text = args_$2["text"] != null ? String(args_$2.text) : "";
if (this.text.length > this.maxlength) {
this.text = this.text.substring(0, this.maxlength)
};
this.$lzc$set_multiline(this.multiline);
tsprite_$7.setText(this.text);
if (!this.hassetwidth) {
if (this.multiline) {
args_$2.width = this.parent.width
} else {
if (this.text != null && this.text != "" && this.text.length > 0) {
args_$2.width = this.getTextWidth()
} else {
args_$2.width = this.getDefaultWidth()
}}} else {
this.$lzc$set_resize(false)
};
if (!this.hassetheight) {
this.sizeToHeight = true
} else {
if (args_$2["height"] != null) {
this.$lzc$set_height(args_$2.height)
}};
if (args_$2["pattern"] != null) {
this.$lzc$set_pattern(args_$2.pattern)
};
if (this.capabilities.advancedfonts) {
if (!("antiAliasType" in args_$2)) {
this.$lzc$set_antiAliasType("advanced")
};
if (!("gridFit" in args_$2)) {
this.$lzc$set_gridFit("subpixel")
}};
this._updateSize()
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
this._updateSize()
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "tsprite", void 0, "__makeSprite", (function  () {
var $lzsc$temp = function  (args_$1) {
this.sprite = this.tsprite = new LzTextSprite(this, args_$1)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "getMCRef", (function  () {
var $lzsc$temp = function  () {
return this.tsprite.getMCRef()
};
$lzsc$temp["displayName"] = "getMCRef";
return $lzsc$temp
})(), "setResize", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_resize(val_$1)
};
$lzsc$temp["displayName"] = "setResize";
return $lzsc$temp
})(), "addText", (function  () {
var $lzsc$temp = function  (t_$1) {
this.$lzc$set_text(this.getText() + t_$1)
};
$lzsc$temp["displayName"] = "addText";
return $lzsc$temp
})(), "clearText", (function  () {
var $lzsc$temp = function  () {
this.$lzc$set_text("")
};
$lzsc$temp["displayName"] = "clearText";
return $lzsc$temp
})(), "setMaxLength", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_maxlength(val_$1)
};
$lzsc$temp["displayName"] = "setMaxLength";
return $lzsc$temp
})(), "setPattern", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_pattern(val_$1)
};
$lzsc$temp["displayName"] = "setPattern";
return $lzsc$temp
})(), "getTextWidth", (function  () {
var $lzsc$temp = function  () {
return this.tsprite.getTextWidth()
};
$lzsc$temp["displayName"] = "getTextWidth";
return $lzsc$temp
})(), "$lzc$getTextWidth_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getTextWidth_dependencies";
return $lzsc$temp
})(), "getTextHeight", (function  () {
var $lzsc$temp = function  () {
return this.tsprite.getTextfieldHeight()
};
$lzsc$temp["displayName"] = "getTextHeight";
return $lzsc$temp
})(), "$lzc$getTextHeight_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getTextHeight_dependencies";
return $lzsc$temp
})(), "applyData", (function  () {
var $lzsc$temp = function  (d_$1) {
if (null == d_$1) {
this.clearText()
} else {
this.$lzc$set_text(d_$1)
}};
$lzsc$temp["displayName"] = "applyData";
return $lzsc$temp
})(), "setScroll", (function  () {
var $lzsc$temp = function  (h_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_scroll(h_$1)
};
$lzsc$temp["displayName"] = "setScroll";
return $lzsc$temp
})(), "getScroll", (function  () {
var $lzsc$temp = function  () {
return this.scroll
};
$lzsc$temp["displayName"] = "getScroll";
return $lzsc$temp
})(), "getMaxScroll", (function  () {
var $lzsc$temp = function  () {
return this.maxscroll
};
$lzsc$temp["displayName"] = "getMaxScroll";
return $lzsc$temp
})(), "$lzc$getMaxScroll_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "maxscroll"]
};
$lzsc$temp["displayName"] = "$lzc$getMaxScroll_dependencies";
return $lzsc$temp
})(), "getBottomScroll", (function  () {
var $lzsc$temp = function  () {
return this.scroll + this.height / this.lineheight
};
$lzsc$temp["displayName"] = "getBottomScroll";
return $lzsc$temp
})(), "setXScroll", (function  () {
var $lzsc$temp = function  (n_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_xscroll(n_$1)
};
$lzsc$temp["displayName"] = "setXScroll";
return $lzsc$temp
})(), "setYScroll", (function  () {
var $lzsc$temp = function  (n_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_yscroll(n_$1)
};
$lzsc$temp["displayName"] = "setYScroll";
return $lzsc$temp
})(), "setHScroll", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_hscroll(s_$1)
};
$lzsc$temp["displayName"] = "setHScroll";
return $lzsc$temp
})(), "annotateAAimg", (function  () {
var $lzsc$temp = function  (txt_$1) {
if (typeof txt_$1 == "undefined") {
return
};
if (txt_$1.length == 0) {
return
};
var ntxt_$2 = "";
var start_$3 = 0;
var end_$4 = 0;
var i_$5;
var IMGSTART_$6 = "<img ";
while (true) {
i_$5 = txt_$1.indexOf(IMGSTART_$6, start_$3);
if (i_$5 < 0) {
ntxt_$2 += txt_$1.substring(start_$3);
break
};
ntxt_$2 += txt_$1.substring(start_$3, i_$5 + IMGSTART_$6.length);
start_$3 = i_$5 + IMGSTART_$6.length;
var attrs_$7 = {};
end_$4 = start_$3 + this.parseImgAttributes(attrs_$7, txt_$1.substring(start_$3));
ntxt_$2 += txt_$1.substring(start_$3, end_$4 + 1);
if (attrs_$7["alt"] != null) {
var altval_$8 = attrs_$7["alt"];
ntxt_$2 += "[image " + altval_$8 + "]"
};
start_$3 = end_$4 + 1
};
return ntxt_$2
};
$lzsc$temp["displayName"] = "annotateAAimg";
return $lzsc$temp
})(), "parseImgAttributes", (function  () {
var $lzsc$temp = function  (attrs_$1, str_$2) {
var i_$3;
var end_$4 = 0;
var ATTNAME_$5 = "attrname";
var ATTVAL_$6 = "attrval";
var WHITESPACE_$7 = "whitespace";
var WHITESPACE2_$8 = "whitespace2";
var mode_$9 = WHITESPACE_$7;
var smax_$10 = str_$2.length;
var attrname_$11;
var attrval_$12;
var delimiter_$13;
for (i_$3 = 0;i_$3 < smax_$10;i_$3++) {
end_$4 = i_$3;
var c_$14 = str_$2.charAt(i_$3);
if (c_$14 == ">") {
break
};
if (mode_$9 == WHITESPACE_$7) {
if (c_$14 != " ") {
mode_$9 = ATTNAME_$5;
attrname_$11 = c_$14
}} else {
if (mode_$9 == ATTNAME_$5) {
if (c_$14 == " " || c_$14 == "=") {
mode_$9 = WHITESPACE2_$8
} else {
attrname_$11 += c_$14
}} else {
if (mode_$9 == WHITESPACE2_$8) {
if (c_$14 == " " || c_$14 == "=") {
continue
} else {
mode_$9 = ATTVAL_$6;
delimiter_$13 = c_$14;
attrval_$12 = ""
}} else {
if (mode_$9 == ATTVAL_$6) {
if (c_$14 != delimiter_$13) {
attrval_$12 += c_$14
} else {
mode_$9 = WHITESPACE_$7;
attrs_$1[attrname_$11] = attrval_$12
}}}}}};
return end_$4
};
$lzsc$temp["displayName"] = "parseImgAttributes";
return $lzsc$temp
})(), "setText", (function  () {
var $lzsc$temp = function  (t_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_text(t_$1)
};
$lzsc$temp["displayName"] = "setText";
return $lzsc$temp
})(), "format", (function  () {
var $lzsc$temp = function  (control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 1);
this.$lzc$set_text(this.formatToString.apply(this, [control_$1].concat(args_$2)))
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})(), "addFormat", (function  () {
var $lzsc$temp = function  (control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 1);
this.$lzc$set_text(this.getText() + this.formatToString.apply(this, [control_$1].concat(args_$2)))
};
$lzsc$temp["displayName"] = "addFormat";
return $lzsc$temp
})(), "updateMaxLines", (function  () {
var $lzsc$temp = function  () {
var newlin_$1 = Math.floor(this.height / (this.font.height - 1));
if (newlin_$1 != this.maxlines) {
this.maxlines = newlin_$1
}};
$lzsc$temp["displayName"] = "updateMaxLines";
return $lzsc$temp
})(), "getText", (function  () {
var $lzsc$temp = function  () {
return this.text
};
$lzsc$temp["displayName"] = "getText";
return $lzsc$temp
})(), "$lzc$getText_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getText_dependencies";
return $lzsc$temp
})(), "escapeText", (function  () {
var $lzsc$temp = function  (ts_$1) {
var t_$2 = ts_$1 == null ? this.text : ts_$1;
var i_$3;
for (var ec_$4 in LzText.escapeChars) {
while (t_$2.indexOf(ec_$4) > -1) {
i_$3 = t_$2.indexOf(ec_$4);
t_$2 = t_$2.substring(0, i_$3) + LzText.escapeChars[ec_$4] + t_$2.substring(i_$3 + 1)
}};
return t_$2
};
$lzsc$temp["displayName"] = "escapeText";
return $lzsc$temp
})(), "setSelectable", (function  () {
var $lzsc$temp = function  (isSel_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_selectable(isSel_$1)
};
$lzsc$temp["displayName"] = "setSelectable";
return $lzsc$temp
})(), "setFontSize", (function  () {
var $lzsc$temp = function  (fsize_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fontsize(fsize_$1)
};
$lzsc$temp["displayName"] = "setFontSize";
return $lzsc$temp
})(), "setFontStyle", (function  () {
var $lzsc$temp = function  (fstyle_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fontstyle(fstyle_$1)
};
$lzsc$temp["displayName"] = "setFontStyle";
return $lzsc$temp
})(), "setMultiline", (function  () {
var $lzsc$temp = function  (ml_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_multiline(ml_$1)
};
$lzsc$temp["displayName"] = "setMultiline";
return $lzsc$temp
})(), "setBorder", (function  () {
var $lzsc$temp = function  (onroff_$1) {
this.tsprite.setBorder(onroff_$1)
};
$lzsc$temp["displayName"] = "setBorder";
return $lzsc$temp
})(), "setWordWrap", (function  () {
var $lzsc$temp = function  (wrap_$1) {
this.tsprite.setWordWrap(wrap_$1)
};
$lzsc$temp["displayName"] = "setWordWrap";
return $lzsc$temp
})(), "setEmbedFonts", (function  () {
var $lzsc$temp = function  (onroff_$1) {
this.tsprite.setEmbedFonts(onroff_$1)
};
$lzsc$temp["displayName"] = "setEmbedFonts";
return $lzsc$temp
})(), "setAntiAliasType", (function  () {
var $lzsc$temp = function  (aliasType_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_antiAliasType(aliasType_$1)
};
$lzsc$temp["displayName"] = "setAntiAliasType";
return $lzsc$temp
})(), "getAntiAliasType", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.advancedfonts) {
return this.antiAliasType
} else {
LzView.__warnCapability("text.getAntiAliasType()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getAntiAliasType";
return $lzsc$temp
})(), "setGridFit", (function  () {
var $lzsc$temp = function  (gridFit_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_gridFit(gridFit_$1)
};
$lzsc$temp["displayName"] = "setGridFit";
return $lzsc$temp
})(), "getGridFit", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.advancedfonts) {
return this.gridFit
} else {
LzView.__warnCapability("text.getGridFit()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getGridFit";
return $lzsc$temp
})(), "setSharpness", (function  () {
var $lzsc$temp = function  (sharpness_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_sharpness(sharpness_$1)
};
$lzsc$temp["displayName"] = "setSharpness";
return $lzsc$temp
})(), "getSharpness", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.advancedfonts) {
return this.sharpness
} else {
LzView.__warnCapability("text.getSharpness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getSharpness";
return $lzsc$temp
})(), "setThickness", (function  () {
var $lzsc$temp = function  (thickness_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_thickness(thickness_$1)
};
$lzsc$temp["displayName"] = "setThickness";
return $lzsc$temp
})(), "getThickness", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.advancedfonts) {
return this.thickness
} else {
LzView.__warnCapability("text.getThickness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getThickness";
return $lzsc$temp
})(), "setSelection", (function  () {
var $lzsc$temp = function  (start_$1, end_$2) {
switch (arguments.length) {
case 1:
end_$2 = null
};
if (end_$2 == null) {
end_$2 = start_$1
};
this.tsprite.setSelection(start_$1, end_$2)
};
$lzsc$temp["displayName"] = "setSelection";
return $lzsc$temp
})(), "getSelectionPosition", (function  () {
var $lzsc$temp = function  () {
return this.tsprite.getSelectionPosition()
};
$lzsc$temp["displayName"] = "getSelectionPosition";
return $lzsc$temp
})(), "getSelectionSize", (function  () {
var $lzsc$temp = function  () {
return this.tsprite.getSelectionSize()
};
$lzsc$temp["displayName"] = "getSelectionSize";
return $lzsc$temp
})(), "makeTextLink", (function  () {
var $lzsc$temp = function  (str_$1, value_$2) {
return this.tsprite.makeTextLink(str_$1, value_$2)
};
$lzsc$temp["displayName"] = "makeTextLink";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzText: " + this.getText()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "text", "attributes", new LzInheritedHash(LzView.attributes), "fontArgToAttr", {font: "fontname", fontsize: "fontsize", fontstyle: "fontstyle"}, "escapeChars", {">": "&gt;", "<": "&lt;"}]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzText.attributes.pixellock = true;
LzText.attributes.clip = true;
LzText.attributes.selectable = false;
LzText.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
var id_$1 = LzView.prototype._dbg_name.call(this);
if (id_$1 != this.toString()) {
return id_$1
} else {
var contents_$2 = this.getText();
if (contents_$2) {
return Debug.stringEscape(contents_$2, true)
}}};
$lzsc$temp["displayName"] = "LzText.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "views/LzText.lzs#102/1";
return $lzsc$temp
})()(LzText);
lz[LzText.tagname] = LzText;
Class.make("LzInputText", LzText, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "password", void 0, "onenabled", LzDeclaredEvent, "getDefaultWidth", (function  () {
var $lzsc$temp = function  () {
return 100
};
$lzsc$temp["displayName"] = "getDefaultWidth";
return $lzsc$temp
})(), "_onfocusDel", null, "_onblurDel", null, "_modemanagerDel", null, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
this.password = ("password" in args_$2) ? !(!args_$2.password) : false;
this.resize = ("resize" in args_$2) ? !(!args_$2.resize) : false;
this.focusable = true;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this._onfocusDel = new LzDelegate(this, "_gotFocusEvent", this, "onfocus");
this._onblurDel = new LzDelegate(this, "_gotBlurEvent", this, "onblur");
this._modemanagerDel = new LzDelegate(this, "_modechanged", lz.ModeManager, "onmode")
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
if (this._onfocusDel) {
this._onfocusDel.unregisterAll();
this._onfocusDel = null
};
if (this._onblurDel) {
this._onblurDel.unregisterAll();
this._onblurDel = null
};
if (this._modemanagerDel) {
this._modemanagerDel.unregisterAll();
this._modemanagerDel = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "isprite", void 0, "__makeSprite", (function  () {
var $lzsc$temp = function  (args_$1) {
this.sprite = this.tsprite = this.isprite = new LzInputTextSprite(this, args_$1)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "_focused", false, "_gotFocusEvent", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
this._focused = true;
var isprite_$2 = this.sprite;
isprite_$2.gotFocus()
};
$lzsc$temp["displayName"] = "_gotFocusEvent";
return $lzsc$temp
})(), "_gotBlurEvent", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
this._focused = false;
var isprite_$2 = this.sprite;
isprite_$2.gotBlur()
};
$lzsc$temp["displayName"] = "_gotBlurEvent";
return $lzsc$temp
})(), "inputtextevent", (function  () {
var $lzsc$temp = function  (eventname_$1, value_$2) {
switch (arguments.length) {
case 1:
value_$2 = null
};
if (eventname_$1 == "onfocus" && this._focused) {
return
};
if (eventname_$1 == "onblur" && !this._focused) {
return
};
if (eventname_$1 == "onfocus") {
this._focused = true;
if (lz.Focus.getFocus() !== this) {
var tabdown_$3 = lz.Keys.isKeyDown("tab");
lz.Focus.setFocus(this, tabdown_$3)
}} else {
if (eventname_$1 == "onchange") {
var isprite_$4 = this.sprite;
this.text = isprite_$4.getText();
if (this.multiline && this.sizeToHeight && this.height != isprite_$4.getTextfieldHeight()) {
this.$lzc$set_height(isprite_$4.getTextfieldHeight())
};
if (this.ontext.ready) {
this.ontext.sendEvent(value_$2)
}} else {
if (eventname_$1 == "onblur") {
this._focused = false;
if (lz.Focus.getFocus() === this) {
lz.Focus.clearFocus()
}} else {
Debug.warn("unhandled inputtextevent='%s' in %#w", eventname_$1, this)
}}}};
$lzsc$temp["displayName"] = "inputtextevent";
return $lzsc$temp
})(), "updateData", (function  () {
var $lzsc$temp = function  () {
var isprite_$1 = this.sprite;
return isprite_$1.getText()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "enabled", true, "$lzc$set_enabled", (function  () {
var $lzsc$temp = function  (enabled_$1) {
this.focusable = true;
this.enabled = enabled_$1;
var isprite_$2 = this.sprite;
isprite_$2.setEnabled(enabled_$1);
if (this.onenabled.ready) {
this.onenabled.sendEvent(enabled_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_enabled";
return $lzsc$temp
})(), "setEnabled", (function  () {
var $lzsc$temp = function  (enabled_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_enabled(enabled_$1)
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setHTML", (function  () {
var $lzsc$temp = function  (htmlp_$1) {
if (this.capabilities["htmlinputtext"]) {
var isprite_$2 = this.sprite;
isprite_$2.setHTML(htmlp_$1)
} else {
LzView.__warnCapability("inputtext.setHTML()", "htmlinputtext")
}};
$lzsc$temp["displayName"] = "setHTML";
return $lzsc$temp
})(), "getText", (function  () {
var $lzsc$temp = function  () {
return this.sprite.getText()
};
$lzsc$temp["displayName"] = "getText";
return $lzsc$temp
})(), "_allowselectable", true, "_selectable", void 0, "_modechanged", (function  () {
var $lzsc$temp = function  (modalview_$1) {
this._setallowselectable(!modalview_$1 || lz.ModeManager.__LZallowInput(modalview_$1, this))
};
$lzsc$temp["displayName"] = "_modechanged";
return $lzsc$temp
})(), "_setallowselectable", (function  () {
var $lzsc$temp = function  (value_$1) {
this._allowselectable = value_$1;
this.$lzc$set_selectable(this._selectable)
};
$lzsc$temp["displayName"] = "_setallowselectable";
return $lzsc$temp
})(), "$lzc$set_selectable", (function  () {
var $lzsc$temp = function  (value_$1) {
this._selectable = value_$1;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_selectable"] || this.nextMethod(arguments.callee, "$lzc$set_selectable")).call(this, this._allowselectable ? value_$1 : false)
};
$lzsc$temp["displayName"] = "$lzc$set_selectable";
return $lzsc$temp
})()], ["tagname", "inputtext", "attributes", new LzInheritedHash(LzText.attributes)]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzNode.mergeAttributes({selectable: true, enabled: true}, LzInputText.attributes)
}}};
$lzsc$temp["displayName"] = "views/LzInputText.lzs#59/1";
return $lzsc$temp
})()(LzInputText);
lz[LzInputText.tagname] = LzInputText;
Class.make("LzViewLinkage", null, ["scale", 1, "offset", 0, "uplinkArray", null, "downlinkArray", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (fromView_$1, toView_$2) {
this.scale = new Object();
this.offset = new Object();
if (fromView_$1 == toView_$2) {
return
};
this.uplinkArray = [];
var pview_$3 = fromView_$1;
do{
pview_$3 = pview_$3.immediateparent;
this.uplinkArray.push(pview_$3)
} while (pview_$3 != toView_$2 && pview_$3 != canvas);
this.downlinkArray = [];
if (pview_$3 == toView_$2) {
return
};
var pview_$3 = toView_$2;
do{
pview_$3 = pview_$3.immediateparent;
this.downlinkArray.push(pview_$3)
} while (pview_$3 != canvas);
while (this.uplinkArray.length > 1 && this.downlinkArray[this.downlinkArray.length - 1] == this.uplinkArray[this.uplinkArray.length - 1] && this.downlinkArray[this.downlinkArray.length - 2] == this.uplinkArray[this.uplinkArray.length - 2]) {
this.downlinkArray.pop();
this.uplinkArray.pop()
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "update", (function  () {
var $lzsc$temp = function  (xory_$1) {
var tscale_$2 = 1;
var toffset_$3 = 0;
var scale_$4 = "_" + xory_$1 + "scale";
if (this.uplinkArray) {
var ual_$5 = this.uplinkArray.length;
for (var i_$6 = 0;i_$6 < ual_$5;i_$6++) {
var a_$7 = this.uplinkArray[i_$6];
tscale_$2 *= a_$7[scale_$4];
toffset_$3 += a_$7[xory_$1] / tscale_$2
}};
if (this.downlinkArray) {
for (var i_$6 = this.downlinkArray.length - 1;i_$6 >= 0;i_$6--) {
var a_$7 = this.downlinkArray[i_$6];
toffset_$3 -= a_$7[xory_$1] / tscale_$2;
tscale_$2 /= a_$7[scale_$4]
}};
this.scale[xory_$1] = tscale_$2;
this.offset[xory_$1] = toffset_$3
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzViewLinkage.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
return "[" + this.scale.x + " 0 " + this.offset.x + " 0 " + this.scale.y + " " + this.offset.y + " 0 0 1]"
};
$lzsc$temp["displayName"] = "LzViewLinkage.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "views/LzViewLinkage.lzs#36/1";
return $lzsc$temp
})()(LzViewLinkage);
Class.make("LzCanvas", LzView, ["updatePercentCreatedEnabled", true, "resourcetable", void 0, "_lzinitialsubviews", [], "totalnodes", void 0, "framerate", 30, "onframerate", LzDeclaredEvent, "creatednodes", void 0, "__LZproxied", void 0, "embedfonts", void 0, "lpsbuild", void 0, "lpsbuilddate", void 0, "appbuilddate", void 0, "runtime", void 0, "allowfullscreen", void 0, "fullscreen", void 0, "onfullscreen", LzDeclaredEvent, "__LZmouseupDel", void 0, "__LZmousedownDel", void 0, "__LZmousemoveDel", void 0, "__LZDefaultCanvasMenu", void 0, "httpdataprovider", null, "defaultdataprovider", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2, children_$3, async_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
args_$2 = null;;case 2:
children_$3 = null;;case 3:
async_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, args_$2, children_$3, async_$4);
this.datasets = {};
this.__LZcheckwidth = null;
this.__LZcheckheight = null;
this.hassetwidth = true;
this.hassetheight = true
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args) {
var getQueryArg_$3;
getQueryArg_$3 = (function  () {
var $lzsc$temp = function  (name_$1, initname_$2) {
var arg_$3 = args[name_$1];
delete args[name_$1];
if (arg_$3 != null) {
return !(!arg_$3)
} else {
if (initname_$2 != null) {
var initarg_$4 = lz.Browser.getInitArg(initname_$2);
if (initarg_$4 != null) {
return initarg_$4 == "true"
}}};
return void 0
};
$lzsc$temp["displayName"] = "getQueryArg";
return $lzsc$temp
})();
this.__makeSprite(null);
var capabilities_$2 = this.sprite.capabilities;
this.capabilities = capabilities_$2;
this.immediateparent = this;
this.datapath = new LzDatapath(this);
this.mask = null;
this.accessible = getQueryArg_$3("accessible", null);
if (capabilities_$2.accessibility == true) {
this.sprite.setAccessible(this.accessible);
if (this.accessible) {
this.sprite.setAAActive(true);
this.sprite.setAASilent(false)
}} else {
if (this.accessible) {
Debug.warn("This runtime doesn't support accessibility.");
this.accessible = false
}};
this.history = getQueryArg_$3("history", "history");
if (this.history && capabilities_$2.history != true) {
Debug.warn("This runtime doesn't support history.");
this.history = false
};
this.allowfullscreen = getQueryArg_$3("allowfullscreen", "allowfullscreen");
if (this.allowfullscreen && capabilities_$2.allowfullscreen != true) {
Debug.warn("This runtime doesn't support full screen mode.");
this.allowfullscreen = false
};
this.fullscreen = false;
this.viewLevel = 0;
this.resourcetable = {};
this.totalnodes = 0;
this.creatednodes = 0;
this.percentcreated = 0;
if (!args.framerate) {
args.framerate = 30
};
this.proxied = getQueryArg_$3("proxied", "lzproxied");
if (this.proxied == null) {
this.proxied = args.__LZproxied == "true"
};
if (typeof args.proxyurl == "undefined") {
this.proxyurl = lz.Browser.getBaseURL().toString()
};
if (args.focustrap) {
if (capabilities_$2.disableglobalfocustrap != true) {
delete args.focustrap
}};
LzScreenKernel.setCallback(this, "__windowResize");
delete args.width;
delete args.height;
if (args["fgcolor"] != null) {
this.hasfgcolor = true
};
this.lpsversion = args.lpsversion + "." + this.__LZlfcversion;
delete args.lpsversion;
this.__LZdelayedSetters = LzView.__LZdelayedSetters;
this.earlySetters = LzView.earlySetters;
if (!this.version) {
this.version = this.lpsversion
};
this.isinited = false;
this._lzinitialsubviews = [];
this.datasets = {};
global.canvas = this;
this.parent = this;
this.makeMasked();
this.__LZmouseupDel = new LzDelegate(this, "__LZmouseup", lz.GlobalMouse, "onmouseup");
this.__LZmousedownDel = new LzDelegate(this, "__LZmousedown", lz.GlobalMouse, "onmousedown");
this.__LZmousemoveDel = new LzDelegate(this, "__LZmousemove", lz.GlobalMouse, "onmousemove");
this.defaultdataprovider = this.httpdataprovider = new LzHTTPDataProvider();
this.id = lz.Browser.getAppID()
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZmouseup", (function  () {
var $lzsc$temp = function  (e_$1) {
if (this.onmouseup.ready) {
this.onmouseup.sendEvent()
}};
$lzsc$temp["displayName"] = "__LZmouseup";
return $lzsc$temp
})(), "__LZmousemove", (function  () {
var $lzsc$temp = function  (e_$1) {
if (this.onmousemove.ready) {
this.onmousemove.sendEvent()
}};
$lzsc$temp["displayName"] = "__LZmousemove";
return $lzsc$temp
})(), "__LZmousedown", (function  () {
var $lzsc$temp = function  (e_$1) {
if (this.onmousedown.ready) {
this.onmousedown.sendEvent()
}};
$lzsc$temp["displayName"] = "__LZmousedown";
return $lzsc$temp
})(), "__makeSprite", (function  () {
var $lzsc$temp = function  (args_$1) {
this.sprite = new LzSprite(this, true)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "onmouseleave", LzDeclaredEvent, "onmouseenter", LzDeclaredEvent, "onpercentcreated", LzDeclaredEvent, "onmousemove", LzDeclaredEvent, "onafterinit", LzDeclaredEvent, "lpsversion", void 0, "lpsrelease", void 0, "version", null, "__LZlfcversion", "0", "proxied", true, "dataloadtimeout", 30000, "medialoadtimeout", 30000, "mediaerrortimeout", 4500, "percentcreated", void 0, "datasets", null, "compareVersion", (function  () {
var $lzsc$temp = function  (ver_$1, over_$2) {
switch (arguments.length) {
case 1:
over_$2 = null
};
if (over_$2 == null) {
over_$2 = this.lpsversion
};
if (ver_$1 == over_$2) {
return 0
};
var ver1_$3 = ver_$1.split(".");
var ver2_$4 = over_$2.split(".");
var i_$5 = 0;
while (i_$5 < ver1_$3.length || i_$5 < ver2_$4.length) {
var my_$6 = Number(ver1_$3[i_$5]) || 0;
var oth_$7 = Number(ver2_$4[i_$5++]) || 0;
if (my_$6 < oth_$7) {
return -1
} else {
if (my_$6 > oth_$7) {
return 1
}}};
return 0
};
$lzsc$temp["displayName"] = "compareVersion";
return $lzsc$temp
})(), "$lzc$set_resource", (function  () {
var $lzsc$temp = function  (v_$1) {
Debug.error("You cannot set a resource on the canvas.")
};
$lzsc$temp["displayName"] = "$lzc$set_resource";
return $lzsc$temp
})(), "$lzc$set_focustrap", (function  () {
var $lzsc$temp = function  (istrapped_$1) {
lz.Keys.setGlobalFocusTrap(istrapped_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_focustrap";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "This is the canvas"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "$lzc$set_framerate", (function  () {
var $lzsc$temp = function  (fps_$1) {
fps_$1 *= 1;
if (fps_$1 < 1) {
fps_$1 = 1
} else {
if (fps_$1 > 1000) {
fps_$1 = 1000
}};
this.framerate = fps_$1;
lz.Idle.setFrameRate(fps_$1);
if (this.onframerate.ready) {
this.onframerate.sendEvent(fps_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_framerate";
return $lzsc$temp
})(), "$lzc$set_fullscreen", (function  () {
var $lzsc$temp = function  (fullscreen_$1) {
switch (arguments.length) {
case 0:
fullscreen_$1 = true
};
if (this.sprite.capabilities.allowfullscreen == true) {
LzScreenKernel.showFullScreen(fullscreen_$1)
} else {
LzView.__warnCapability("canvas.setAttribute('fullscreen', " + fullscreen_$1 + ")")
}};
$lzsc$temp["displayName"] = "$lzc$set_fullscreen";
return $lzsc$temp
})(), "__fullscreenEventCallback", (function  () {
var $lzsc$temp = function  (result_$1, isFullscreen_$2) {
this.fullscreen = isFullscreen_$2;
this.onfullscreen.sendEvent(result_$1)
};
$lzsc$temp["displayName"] = "__fullscreenEventCallback";
return $lzsc$temp
})(), "__fullscreenErrorCallback", (function  () {
var $lzsc$temp = function  (errorMessage_$1) {
if (this.allowfullscreen == false) {
Debug.error('Please set <canvas allowfullscreen="true" /> for fullscreen support')
} else {
var message_$2 = "Usage of fullscreen feature is supported starting with Flash Player 9.0.28 (Windows & OS X) and 9.0.115.0 (Linux).\n" + "You are currently using Flash Player " + lz.Browser.getVersion();
if (errorMessage_$1 != null) {
message_$2 = "Flash Player Security Error " + errorMessage_$1 + "\n" + message_$2
};
Debug.error(message_$2);
Debug.error("Check your SWF embed code for missing param tag " + '<param name="allowFullScreen" value="true" /> inside the <object> tag,\n or ' + 'missing attribute allowFullScreen="true" in <embed> tag.\n' + 'canvas.setAttribute("fullscreen", true) can be called only in response to a mouse click or keypress.\n')
}};
$lzsc$temp["displayName"] = "__fullscreenErrorCallback";
return $lzsc$temp
})(), "$lzc$set_allowfullscreen", (function  () {
var $lzsc$temp = function  (fs_$1) {
this.allowfullscreen = fs_$1
};
$lzsc$temp["displayName"] = "$lzc$set_allowfullscreen";
return $lzsc$temp
})(), "initDone", (function  () {
var $lzsc$temp = function  () {
var sva_$1 = [];
var svb_$2 = [];
var isv_$3 = this._lzinitialsubviews;
for (var i_$4 = 0, len_$5 = isv_$3.length;i_$4 < len_$5;++i_$4) {
var isi_$6 = isv_$3[i_$4];
if (isi_$6["attrs"] && isi_$6.attrs["initimmediate"]) {
sva_$1.push(isi_$6)
} else {
svb_$2.push(isi_$6)
}};
sva_$1.push.apply(sva_$1, svb_$2);
this._lzinitialsubviews = [];
lz.Instantiator.requestInstantiation(this, sva_$1)
};
$lzsc$temp["displayName"] = "initDone";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
this.sprite.init(true);
if (this.history == true) {
lz.History.__start(this.id)
};
if (this.contextmenu == null) {
this.buildDefaultMenu()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "deferInit", true, "__LZinstantiationDone", (function  () {
var $lzsc$temp = function  () {
this.__LZinstantiated = true;
if (this.deferInit) {
this.deferInit = false;
return
};
this.percentcreated = 1;
this.updatePercentCreatedEnabled = false;
if (this.onpercentcreated.ready) {
this.onpercentcreated.sendEvent(this.percentcreated)
};
lz.Instantiator.resume();
this.__LZcallInit()
};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "updatePercentCreated", (function  () {
var $lzsc$temp = function  () {
this.percentcreated = Math.max(this.percentcreated, this.creatednodes / this.totalnodes);
this.percentcreated = Math.min(0.99, this.percentcreated);
if (this.onpercentcreated.ready) {
this.onpercentcreated.sendEvent(this.percentcreated)
}};
$lzsc$temp["displayName"] = "updatePercentCreated";
return $lzsc$temp
})(), "initiatorAddNode", (function  () {
var $lzsc$temp = function  (e_$1, n_$2) {
this.totalnodes += n_$2;
this._lzinitialsubviews.push(e_$1)
};
$lzsc$temp["displayName"] = "initiatorAddNode";
return $lzsc$temp
})(), "__LZcallInit", (function  () {
var $lzsc$temp = function  (an_$1) {
switch (arguments.length) {
case 0:
an_$1 = null
};
if (this.isinited) {
return
};
this.isinited = true;
this.__LZresolveReferences();
var sl_$2 = this.subnodes;
if (sl_$2) {
for (var i_$3 = 0;i_$3 < sl_$2.length;) {
var s_$4 = sl_$2[i_$3++];
var t_$5 = sl_$2[i_$3];
if (s_$4.isinited || !s_$4.__LZinstantiated) {
continue
};
s_$4.__LZcallInit();
if (t_$5 != sl_$2[i_$3]) {
while (i_$3 > 0) {
if (t_$5 == sl_$2[--i_$3]) {
break
}}}}};
if (this.__LZsourceLocation) {
LzNode.sourceLocatorTable[this.__LZsourceLocation] = this
};
this.init();
if (this.oninit.ready) {
this.oninit.sendEvent(this)
};
if (this.onafterinit.ready) {
this.onafterinit.sendEvent(this)
};
if (this.datapath && this.datapath.__LZApplyDataOnInit) {
this.datapath.__LZApplyDataOnInit()
};
this.inited = true;
if (this.oninited.ready) {
this.oninited.sendEvent(true)
}};
$lzsc$temp["displayName"] = "__LZcallInit";
return $lzsc$temp
})(), "isProxied", (function  () {
var $lzsc$temp = function  () {
return this.proxied
};
$lzsc$temp["displayName"] = "isProxied";
return $lzsc$temp
})(), "$lzc$set_width", (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.setRootWidth(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "$lzc$set_x", (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.setRootX(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_x";
return $lzsc$temp
})(), "$lzc$set_height", (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.setRootHeight(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_height";
return $lzsc$temp
})(), "$lzc$set_y", (function  () {
var $lzsc$temp = function  (v_$1) {
LzSprite.setRootY(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_y";
return $lzsc$temp
})(), "setDefaultContextMenu", (function  () {
var $lzsc$temp = function  (cmenu_$1) {
this.$lzc$set_contextmenu(cmenu_$1);
this.sprite.setDefaultContextMenu(cmenu_$1)
};
$lzsc$temp["displayName"] = "setDefaultContextMenu";
return $lzsc$temp
})(), "buildDefaultMenu", (function  () {
var $lzsc$temp = function  () {
this.__LZDefaultCanvasMenu = new LzContextMenu();
this.__LZDefaultCanvasMenu.hideBuiltInItems();
var defaultMenuItem_$1 = new LzContextMenuItem("About OpenLaszlo...", new LzDelegate(this, "__LZdefaultMenuItemHandler"));
this.__LZDefaultCanvasMenu.addItem(defaultMenuItem_$1);
if (this.proxied) {
var viewSourceMenuItem_$2 = new LzContextMenuItem("View Source", new LzDelegate(this, "__LZviewSourceMenuItemHandler"));
this.__LZDefaultCanvasMenu.addItem(viewSourceMenuItem_$2)
};
this.setDefaultContextMenu(this.__LZDefaultCanvasMenu)
};
$lzsc$temp["displayName"] = "buildDefaultMenu";
return $lzsc$temp
})(), "__LZdefaultMenuItemHandler", (function  () {
var $lzsc$temp = function  (item_$1) {
lz.Browser.loadURL("http://www.openlaszlo.org", "lz_about")
};
$lzsc$temp["displayName"] = "__LZdefaultMenuItemHandler";
return $lzsc$temp
})(), "__LZviewSourceMenuItemHandler", (function  () {
var $lzsc$temp = function  (item_$1) {
var url_$2 = lz.Browser.getBaseURL() + "?lzt=source";
lz.Browser.loadURL(url_$2, "lz_source")
};
$lzsc$temp["displayName"] = "__LZviewSourceMenuItemHandler";
return $lzsc$temp
})(), "__windowResize", (function  () {
var $lzsc$temp = function  (size_$1) {
this.width = size_$1.width;
if (this.onwidth.ready) {
this.onwidth.sendEvent(this.width)
};
this.sprite.setWidth(this.width);
this.height = size_$1.height;
if (this.onheight.ready) {
this.onheight.sendEvent(this.height)
};
this.sprite.setHeight(this.height)
};
$lzsc$temp["displayName"] = "__windowResize";
return $lzsc$temp
})(), "LzInstantiateView", (function  () {
var $lzsc$temp = function  (e_$1, tn_$2) {
switch (arguments.length) {
case 1:
tn_$2 = 1
};
canvas.initiatorAddNode(e_$1, tn_$2)
};
$lzsc$temp["displayName"] = "LzInstantiateView";
return $lzsc$temp
})(), "lzAddLocalData", (function  () {
var $lzsc$temp = function  (name_$1, d_$2, trimwhitespace_$3, nsprefix_$4) {
switch (arguments.length) {
case 3:
nsprefix_$4 = false
};
return new LzDataset(canvas, {name: name_$1, initialdata: d_$2, trimwhitespace: trimwhitespace_$3, nsprefix: nsprefix_$4})
};
$lzsc$temp["displayName"] = "lzAddLocalData";
return $lzsc$temp
})()], ["tagname", "canvas", "attributes", new LzInheritedHash(LzView.attributes), "versionInfoString", (function  () {
var $lzsc$temp = function  () {
return "URL: " + lz.Browser.getLoadURL() + "\n" + "LPS\n" + "  Version: " + canvas.lpsversion + "\n" + "  Release: " + canvas.lpsrelease + "\n" + "  Build: " + canvas.lpsbuild + "\n" + "  Date: " + canvas.lpsbuilddate + "\n" + "Application\n" + "  Date: " + canvas.appbuilddate + "\n" + "Target: " + canvas.runtime + "\n" + "Runtime: " + lz.Browser.getVersion() + "\n" + "OS: " + lz.Browser.getOS() + "\n"
};
$lzsc$temp["displayName"] = "versionInfoString";
return $lzsc$temp
})()]);
lz[LzCanvas.tagname] = LzCanvas;
var canvas;
Class.make("LzScript", LzNode, ["src", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4);
attrs_$2.script()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["tagname", "script", "attributes", new LzInheritedHash(LzNode.attributes)]);
lz[LzScript.tagname] = LzScript;
Class.make("LzAnimatorGroup", LzNode, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "updateDel", void 0, "crepeat", void 0, "startTime", void 0, "__LZpauseTime", void 0, "actAnim", void 0, "notstarted", void 0, "needsrestart", void 0, "attribute", void 0, "start", true, "from", void 0, "to", void 0, "duration", void 0, "indirect", false, "relative", false, "motion", "easeboth", "repeat", 1, "$lzc$set_repeat", (function  () {
var $lzsc$temp = function  (val_$1) {
if (val_$1 <= 0) {
Debug.info("%w.%s: value was <= 0, use Infinity instead", this, arguments.callee);
val_$1 = Infinity
};
this.repeat = val_$1
};
$lzsc$temp["displayName"] = "$lzc$set_repeat";
return $lzsc$temp
})(), "paused", false, "$lzc$set_paused", (function  () {
var $lzsc$temp = function  (val_$1) {
this.pause(val_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_paused";
return $lzsc$temp
})(), "started", void 0, "target", void 0, "process", "sequential", "isactive", false, "ontarget", LzDeclaredEvent, "onduration", LzDeclaredEvent, "onstarted", LzDeclaredEvent, "onstart", LzDeclaredEvent, "onpaused", LzDeclaredEvent, "onstop", LzDeclaredEvent, "onrepeat", LzDeclaredEvent, "animatorProps", {attribute: true, from: true, duration: true, to: true, relative: true, target: true}, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
var ip_$3 = this.immediateparent;
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(ip_$3) : ip_$3 instanceof LzAnimatorGroup) {
for (var k_$4 in this.animatorProps) {
if (args_$2[k_$4] == null) {
args_$2[k_$4] = ip_$3[k_$4]
}};
if (ip_$3.animators == null) {
ip_$3.animators = [this]
} else {
ip_$3.animators.push(this)
};
args_$2.start = LzNode._ignoreAttribute
} else {
this.target = ip_$3
};
if (!this.updateDel) {
this.updateDel = new LzDelegate(this, "update")
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
if (!this.target) {
this.target = this.immediateparent
};
if (this.started) {
this.doStart()
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this)
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "$lzc$set_target", (function  () {
var $lzsc$temp = function  (new_target_$1) {
this.target = new_target_$1;
var nodes_$2 = this.subnodes;
if (nodes_$2) {
for (var i_$3 = 0;i_$3 < nodes_$2.length;i_$3++) {
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(nodes_$2[i_$3]) : nodes_$2[i_$3] instanceof LzAnimatorGroup) {
nodes_$2[i_$3].$lzc$set_target(new_target_$1)
}}};
if (this.ontarget.ready) {
this.ontarget.sendEvent(new_target_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_target";
return $lzsc$temp
})(), "setTarget", (function  () {
var $lzsc$temp = function  (n_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_target(n_$1)
};
$lzsc$temp["displayName"] = "setTarget";
return $lzsc$temp
})(), "$lzc$set_start", (function  () {
var $lzsc$temp = function  (start_$1) {
this.started = start_$1;
if (this.onstarted.ready) {
this.onstarted.sendEvent(start_$1)
};
if (!this.isinited) {
return
};
if (start_$1) {
this.doStart()
} else {
this.stop()
}};
$lzsc$temp["displayName"] = "$lzc$set_start";
return $lzsc$temp
})(), "doStart", (function  () {
var $lzsc$temp = function  () {
if (this.isactive) {
return false
};
if (this.onstart.ready) {
this.onstart.sendEvent(new Date().getTime())
};
this.isactive = true;
this.prepareStart();
this.updateDel.register(lz.Idle, "onidle");
return true
};
$lzsc$temp["displayName"] = "doStart";
return $lzsc$temp
})(), "prepareStart", (function  () {
var $lzsc$temp = function  () {
this.crepeat = this.repeat;
for (var i_$1 = this.animators.length - 1;i_$1 >= 0;i_$1--) {
this.animators[i_$1].notstarted = true
};
this.actAnim = this.animators.concat()
};
$lzsc$temp["displayName"] = "prepareStart";
return $lzsc$temp
})(), "resetAnimator", (function  () {
var $lzsc$temp = function  () {
this.actAnim = this.animators.concat();
for (var i_$1 = this.animators.length - 1;i_$1 >= 0;i_$1--) {
this.animators[i_$1].needsrestart = true
}};
$lzsc$temp["displayName"] = "resetAnimator";
return $lzsc$temp
})(), "update", (function  () {
var $lzsc$temp = function  (time_$1) {
if (this.paused) {
return false
};
var animend_$2 = this.actAnim.length - 1;
if (animend_$2 > 0 && this.process == "sequential") {
animend_$2 = 0
};
for (var i_$3 = animend_$2;i_$3 >= 0;i_$3--) {
var a_$4 = this.actAnim[i_$3];
if (a_$4.notstarted) {
a_$4.isactive = true;
a_$4.prepareStart();
a_$4.notstarted = false
} else {
if (a_$4.needsrestart) {
a_$4.resetAnimator();
a_$4.needsrestart = false
}};
if (a_$4.update(time_$1)) {
this.actAnim.splice(i_$3, 1)
}};
if (!this.actAnim.length) {
return this.checkRepeat()
};
return false
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})(), "pause", (function  () {
var $lzsc$temp = function  (dop_$1) {
switch (arguments.length) {
case 0:
dop_$1 = null
};
if (dop_$1 == null) {
dop_$1 = !this.paused
};
if (this.paused && !dop_$1) {
this.__LZaddToStartTime(new Date().getTime() - this.__LZpauseTime)
} else {
if (!this.paused && dop_$1) {
this.__LZpauseTime = new Date().getTime()
}};
this.paused = dop_$1;
if (this.onpaused.ready) {
this.onpaused.sendEvent(dop_$1)
}};
$lzsc$temp["displayName"] = "pause";
return $lzsc$temp
})(), "__LZaddToStartTime", (function  () {
var $lzsc$temp = function  (ptime_$1) {
this.startTime += ptime_$1;
if (this.actAnim) {
for (var i_$2 = 0;i_$2 < this.actAnim.length;i_$2++) {
this.actAnim[i_$2].__LZaddToStartTime(ptime_$1)
}}};
$lzsc$temp["displayName"] = "__LZaddToStartTime";
return $lzsc$temp
})(), "stop", (function  () {
var $lzsc$temp = function  () {
if (this.actAnim) {
var animend_$1 = this.actAnim.length - 1;
if (animend_$1 > 0 && this.process == "sequential") {
animend_$1 = 0
};
for (var i_$2 = animend_$1;i_$2 >= 0;i_$2--) {
this.actAnim[i_$2].stop()
}};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "setDuration", (function  () {
var $lzsc$temp = function  (duration_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_duration(duration_$1)
};
$lzsc$temp["displayName"] = "setDuration";
return $lzsc$temp
})(), "$lzc$set_duration", (function  () {
var $lzsc$temp = function  (duration_$1) {
if (isNaN(duration_$1)) {
duration_$1 = 0
} else {
duration_$1 = Number(duration_$1)
};
this.duration = duration_$1;
var sn_$2 = this.subnodes;
if (sn_$2) {
for (var i_$3 = 0;i_$3 < sn_$2.length;++i_$3) {
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(sn_$2[i_$3]) : sn_$2[i_$3] instanceof LzAnimatorGroup) {
sn_$2[i_$3].$lzc$set_duration(duration_$1)
}}};
this.onduration.sendEvent(duration_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_duration";
return $lzsc$temp
})(), "__LZfinalizeAnim", (function  () {
var $lzsc$temp = function  () {
this.__LZhalt()
};
$lzsc$temp["displayName"] = "__LZfinalizeAnim";
return $lzsc$temp
})(), "__LZhalt", (function  () {
var $lzsc$temp = function  () {
this.isactive = false;
this.updateDel.unregisterAll();
if (this.onstop.ready) {
this.onstop.sendEvent(new Date().getTime())
}};
$lzsc$temp["displayName"] = "__LZhalt";
return $lzsc$temp
})(), "checkRepeat", (function  () {
var $lzsc$temp = function  () {
if (this.crepeat == 1) {
this.__LZfinalizeAnim();
return true
} else {
this.crepeat--;
if (this.onrepeat.ready) {
this.onrepeat.sendEvent(new Date().getTime())
};
this.resetAnimator();
return false
}};
$lzsc$temp["displayName"] = "checkRepeat";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.stop();
this.updateDel.unregisterAll();
this.animators = null;
this.actAnim = null;
var ip_$1 = this.immediateparent;
var parAnim_$2 = ip_$1.animators;
if (parAnim_$2 && parAnim_$2.length) {
for (var i_$3 = 0;i_$3 < parAnim_$2.length;i_$3++) {
if (parAnim_$2[i_$3] == this) {
parAnim_$2.splice(i_$3, 1);
break
}};
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(ip_$1) : ip_$1 instanceof LzAnimatorGroup) {
var activeAnim_$4 = ip_$1.actAnim;
if (activeAnim_$4 && activeAnim_$4.length) {
for (var i_$3 = 0;i_$3 < activeAnim_$4.length;i_$3++) {
if (activeAnim_$4[i_$3] == this) {
activeAnim_$4.splice(i_$3, 1);
break
}}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
if (this.animators) {
return "Group of " + this.animators.length
};
return "Empty group"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "animatorgroup", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzAnimatorGroup.attributes.start = true;
LzAnimatorGroup.attributes.ignoreplacement = true
}}};
$lzsc$temp["displayName"] = "controllers/LzAnimatorGroup.lzs#44/1";
return $lzsc$temp
})()(LzAnimatorGroup);
lz[LzAnimatorGroup.tagname] = LzAnimatorGroup;
Class.make("LzAnimator", LzAnimatorGroup, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "calcMethod", void 0, "lastIterationTime", void 0, "currentValue", void 0, "doBegin", void 0, "beginPoleDelta", 0.25, "endPoleDelta", 0.25, "primary_K", void 0, "origto", void 0, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.calcMethod = this.calcNextValue;
this.primary_K = 1
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_motion", (function  () {
var $lzsc$temp = function  (eparam_$1) {
this.motion = eparam_$1;
if (eparam_$1 == "linear") {
this.calcMethod = this.calcNextValueLinear
} else {
this.calcMethod = this.calcNextValue;
if (eparam_$1 == "easeout") {
this.beginPoleDelta = 100
} else {
if (eparam_$1 == "easein") {
this.endPoleDelta = 15
}}}};
$lzsc$temp["displayName"] = "$lzc$set_motion";
return $lzsc$temp
})(), "setMotion", (function  () {
var $lzsc$temp = function  (eparam_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_motion(eparam_$1)
};
$lzsc$temp["displayName"] = "setMotion";
return $lzsc$temp
})(), "$lzc$set_to", (function  () {
var $lzsc$temp = function  (eparam_$1) {
this.origto = Number(eparam_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_to";
return $lzsc$temp
})(), "setTo", (function  () {
var $lzsc$temp = function  (eparam_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_to(eparam_$1)
};
$lzsc$temp["displayName"] = "setTo";
return $lzsc$temp
})(), "calcControlValues", (function  () {
var $lzsc$temp = function  (cval_$1) {
switch (arguments.length) {
case 0:
cval_$1 = null
};
this.currentValue = cval_$1 || 0;
var dir_$2 = this.indirect ? -1 : 1;
if (this.currentValue < this.to) {
this.beginPole = this.currentValue - dir_$2 * this.beginPoleDelta;
this.endPole = this.to + dir_$2 * this.endPoleDelta
} else {
this.beginPole = this.currentValue + dir_$2 * this.beginPoleDelta;
this.endPole = this.to - dir_$2 * this.endPoleDelta
};
this.primary_K = 1;
var kN_$3 = 1 * (this.beginPole - this.to) * (this.currentValue - this.endPole);
var kD_$4 = 1 * (this.beginPole - this.currentValue) * (this.to - this.endPole);
if (kD_$4 != 0) {
this.primary_K = Math.abs(kN_$3 / kD_$4)
}};
$lzsc$temp["displayName"] = "calcControlValues";
return $lzsc$temp
})(), "doStart", (function  () {
var $lzsc$temp = function  () {
if (this.isactive) {
return
};
this.isactive = true;
this.prepareStart();
this.updateDel.register(lz.Idle, "onidle")
};
$lzsc$temp["displayName"] = "doStart";
return $lzsc$temp
})(), "prepareStart", (function  () {
var $lzsc$temp = function  () {
this.crepeat = this.repeat;
var targ_$1 = this.target;
var attr_$2 = this.attribute;
if (this.from != null) {
var $lzsc$1285133406 = Number(this.from);
if (!targ_$1.__LZdeleted) {
var $lzsc$1587863669 = "$lzc$set_" + attr_$2;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$1[$lzsc$1587863669]) : targ_$1[$lzsc$1587863669] instanceof Function) {
targ_$1[$lzsc$1587863669]($lzsc$1285133406)
} else {
targ_$1[attr_$2] = $lzsc$1285133406;
var $lzsc$403879842 = targ_$1["on" + attr_$2];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$403879842) : $lzsc$403879842 instanceof LzEvent) {
if ($lzsc$403879842.ready) {
$lzsc$403879842.sendEvent($lzsc$1285133406)
}}}}};
if (this.relative) {
this.to = this.origto
} else {
this.to = this.origto - targ_$1.getExpectedAttribute(attr_$2)
};
targ_$1.addToExpectedAttribute(attr_$2, this.to);
targ_$1.__LZincrementCounter(attr_$2);
this.currentValue = 0;
this.calcControlValues();
this.doBegin = true
};
$lzsc$temp["displayName"] = "prepareStart";
return $lzsc$temp
})(), "resetAnimator", (function  () {
var $lzsc$temp = function  () {
var targ_$1 = this.target;
var attr_$2 = this.attribute;
var from_$3 = this.from;
if (from_$3 != null) {
if (!targ_$1.__LZdeleted) {
var $lzsc$1072295158 = "$lzc$set_" + attr_$2;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$1[$lzsc$1072295158]) : targ_$1[$lzsc$1072295158] instanceof Function) {
targ_$1[$lzsc$1072295158](from_$3)
} else {
targ_$1[attr_$2] = from_$3;
var $lzsc$798655353 = targ_$1["on" + attr_$2];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$798655353) : $lzsc$798655353 instanceof LzEvent) {
if ($lzsc$798655353.ready) {
$lzsc$798655353.sendEvent(from_$3)
}}}};
var d_$4 = from_$3 - targ_$1.getExpectedAttribute(attr_$2);
targ_$1.addToExpectedAttribute(attr_$2, d_$4)
};
if (!this.relative) {
this.to = this.origto - targ_$1.getExpectedAttribute(attr_$2);
this.calcControlValues()
};
targ_$1.addToExpectedAttribute(attr_$2, this.to);
targ_$1.__LZincrementCounter(attr_$2);
this.currentValue = 0;
this.doBegin = true
};
$lzsc$temp["displayName"] = "resetAnimator";
return $lzsc$temp
})(), "beginAnimator", (function  () {
var $lzsc$temp = function  (time_$1) {
this.startTime = time_$1;
this.lastIterationTime = time_$1;
if (this.onstart.ready) {
this.onstart.sendEvent(time_$1)
};
this.doBegin = false
};
$lzsc$temp["displayName"] = "beginAnimator";
return $lzsc$temp
})(), "stop", (function  () {
var $lzsc$temp = function  () {
if (!this.isactive) {
return
};
var targ_$1 = this.target;
var e_prop_$2 = "e_" + this.attribute;
if (!targ_$1[e_prop_$2].c) {
targ_$1[e_prop_$2].c = 0
};
targ_$1[e_prop_$2].c -= 1;
if (targ_$1[e_prop_$2].c <= 0) {
targ_$1[e_prop_$2].c = 0;
targ_$1[e_prop_$2].v = null
} else {
targ_$1[e_prop_$2].v -= this.to - this.currentValue
};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "__LZfinalizeAnim", (function  () {
var $lzsc$temp = function  () {
var targ_$1 = this.target;
var attr_$2 = this.attribute;
var e_prop_$3 = "e_" + attr_$2;
if (!targ_$1[e_prop_$3].c) {
targ_$1[e_prop_$3].c = 0
};
targ_$1[e_prop_$3].c -= 1;
if (targ_$1[e_prop_$3].c <= 0) {
targ_$1[e_prop_$3].c = 0;
var $lzsc$1980123651 = targ_$1[e_prop_$3].v;
if (!targ_$1.__LZdeleted) {
var $lzsc$724469992 = "$lzc$set_" + attr_$2;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$1[$lzsc$724469992]) : targ_$1[$lzsc$724469992] instanceof Function) {
targ_$1[$lzsc$724469992]($lzsc$1980123651)
} else {
targ_$1[attr_$2] = $lzsc$1980123651;
var $lzsc$500701902 = targ_$1["on" + attr_$2];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$500701902) : $lzsc$500701902 instanceof LzEvent) {
if ($lzsc$500701902.ready) {
$lzsc$500701902.sendEvent($lzsc$1980123651)
}}}};
targ_$1[e_prop_$3].v = null
};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "__LZfinalizeAnim";
return $lzsc$temp
})(), "calcNextValue", (function  () {
var $lzsc$temp = function  (timeDifference_$1) {
var nextValue_$2 = this.currentValue;
var aEndPole_$3 = this.endPole;
var aBeginPole_$4 = this.beginPole;
var K_$5 = Math.exp(timeDifference_$1 * 1 / this.duration * Math.log(this.primary_K));
if (K_$5 != 1) {
var aNumerator_$6 = aBeginPole_$4 * aEndPole_$3 * (1 - K_$5);
var aDenominator_$7 = aEndPole_$3 - K_$5 * aBeginPole_$4;
if (aDenominator_$7 != 0) {
nextValue_$2 = aNumerator_$6 / aDenominator_$7
}};
return nextValue_$2
};
$lzsc$temp["displayName"] = "calcNextValue";
return $lzsc$temp
})(), "calcNextValueLinear", (function  () {
var $lzsc$temp = function  (timeDifference_$1) {
var elapsed_$2 = timeDifference_$1 / this.duration;
return elapsed_$2 * this.to
};
$lzsc$temp["displayName"] = "calcNextValueLinear";
return $lzsc$temp
})(), "update", (function  () {
var $lzsc$temp = function  (time_$1) {
if (this.doBegin) {
this.beginAnimator(time_$1)
} else {
if (!this.paused) {
var aTotalTimeDifference_$2 = time_$1 - this.startTime;
if (aTotalTimeDifference_$2 < this.duration) {
this.setValue(this.calcMethod(aTotalTimeDifference_$2));
this.lastIterationTime = time_$1
} else {
this.setValue(this.to);
return this.checkRepeat()
}}};
return false
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})(), "setValue", (function  () {
var $lzsc$temp = function  (value_$1) {
var targ_$2 = this.target;
var attr_$3 = this.attribute;
var aDiff_$4 = value_$1 - this.currentValue;
var $lzsc$128865961 = targ_$2[attr_$3] + aDiff_$4;
if (!targ_$2.__LZdeleted) {
var $lzsc$1568519831 = "$lzc$set_" + attr_$3;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$2[$lzsc$1568519831]) : targ_$2[$lzsc$1568519831] instanceof Function) {
targ_$2[$lzsc$1568519831]($lzsc$128865961)
} else {
targ_$2[attr_$3] = $lzsc$128865961;
var $lzsc$1208228442 = targ_$2["on" + attr_$3];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1208228442) : $lzsc$1208228442 instanceof LzEvent) {
if ($lzsc$1208228442.ready) {
$lzsc$1208228442.sendEvent($lzsc$128865961)
}}}};
this.currentValue = value_$1
};
$lzsc$temp["displayName"] = "setValue";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Animator for " + this.target + " attribute:" + this.attribute + " to:" + this.to
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "animator", "attributes", new LzInheritedHash(LzAnimatorGroup.attributes)]);
lz[LzAnimator.tagname] = LzAnimator;
Class.make("LzLayout", LzNode, ["vip", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "initDelegate", void 0, "locked", 2, "$lzc$set_locked", (function  () {
var $lzsc$temp = function  (locked_$1) {
if (this.locked == locked_$1) {
return
};
if (locked_$1) {
this.lock()
} else {
this.unlock()
}};
$lzsc$temp["displayName"] = "$lzc$set_locked";
return $lzsc$temp
})(), "subviews", null, "updateDelegate", void 0, "delegates", void 0, "construct", (function  () {
var $lzsc$temp = function  (view_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).apply(this, arguments);
this.subviews = new Array();
this.vip = this.immediateparent;
if (this.vip.layouts == null) {
this.vip.layouts = [this]
} else {
this.vip.layouts.push(this)
};
this.updateDelegate = new LzDelegate(this, "update");
this.delegates = [this.updateDelegate];
if (this.immediateparent.isinited) {
this.__parentInit()
} else {
this.initDelegate = new LzDelegate(this, "__parentInit", this.immediateparent, "oninit");
this.delegates.push(this.initDelegate)
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZapplyArgs", (function  () {
var $lzsc$temp = function  (args_$1, constcall_$2) {
switch (arguments.length) {
case 1:
constcall_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZapplyArgs"] || this.nextMethod(arguments.callee, "__LZapplyArgs")).call(this, args_$1, constcall_$2);
if (this.__LZdeleted) {
return
};
this.delegates.push(new LzDelegate(this, "gotNewSubview", this.immediateparent, "onaddsubview"));
this.delegates.push(new LzDelegate(this, "removeSubview", this.immediateparent, "onremovesubview"));
var vsl_$3 = this.vip.subviews.length;
for (var i_$4 = 0;i_$4 < vsl_$3;i_$4++) {
this.gotNewSubview(this.vip.subviews[i_$4])
}};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.releaseLayout();
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "reset", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
if (this.locked) {
return
};
this.update(e_$1)
};
$lzsc$temp["displayName"] = "reset";
return $lzsc$temp
})(), "addSubview", (function  () {
var $lzsc$temp = function  (sd_$1) {
if (sd_$1.getOption("layoutAfter")) {
this.__LZinsertAfter(sd_$1, sd_$1.getOption("layoutAfter"))
} else {
this.subviews.push(sd_$1)
}};
$lzsc$temp["displayName"] = "addSubview";
return $lzsc$temp
})(), "gotNewSubview", (function  () {
var $lzsc$temp = function  (sd_$1) {
if (!sd_$1.getOption("ignorelayout")) {
this.addSubview(sd_$1)
}};
$lzsc$temp["displayName"] = "gotNewSubview";
return $lzsc$temp
})(), "removeSubview", (function  () {
var $lzsc$temp = function  (sd_$1) {
for (var i_$2 = this.subviews.length - 1;i_$2 >= 0;i_$2--) {
if (this.subviews[i_$2] == sd_$1) {
this.subviews.splice(i_$2, 1);
break
}};
this.reset()
};
$lzsc$temp["displayName"] = "removeSubview";
return $lzsc$temp
})(), "ignore", (function  () {
var $lzsc$temp = function  (s_$1) {
for (var i_$2 = this.subviews.length - 1;i_$2 >= 0;i_$2--) {
if (this.subviews[i_$2] == s_$1) {
this.subviews.splice(i_$2, 1);
break
}};
this.reset()
};
$lzsc$temp["displayName"] = "ignore";
return $lzsc$temp
})(), "lock", (function  () {
var $lzsc$temp = function  () {
this.locked = true
};
$lzsc$temp["displayName"] = "lock";
return $lzsc$temp
})(), "unlock", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
};
this.locked = false;
this.reset()
};
$lzsc$temp["displayName"] = "unlock";
return $lzsc$temp
})(), "__parentInit", (function  () {
var $lzsc$temp = function  (v_$1) {
switch (arguments.length) {
case 0:
v_$1 = null
};
if (this.locked == 2) {
if (this.isinited) {
this.unlock()
} else {
new LzDelegate(this, "unlock", this, "oninit")
}}};
$lzsc$temp["displayName"] = "__parentInit";
return $lzsc$temp
})(), "releaseLayout", (function  () {
var $lzsc$temp = function  () {
if (this.delegates) {
for (var i_$1 = this.delegates.length - 1;i_$1 >= 0;i_$1--) {
this.delegates[i_$1].unregisterAll()
}};
if (this.immediateparent && this.vip.layouts) {
for (var i_$1 = this.vip.layouts.length - 1;i_$1 >= 0;i_$1--) {
if (this.vip.layouts[i_$1] == this) {
this.vip.layouts.splice(i_$1, 1)
}}}};
$lzsc$temp["displayName"] = "releaseLayout";
return $lzsc$temp
})(), "setLayoutOrder", (function  () {
var $lzsc$temp = function  (sub1_$1, sub2_$2) {
for (var i2_$3 = this.subviews.length - 1;i2_$3 >= 0;i2_$3--) {
if (this.subviews[i2_$3] === sub2_$2) {
this.subviews.splice(i2_$3, 1);
break
}};
if (i2_$3 == -1) {
Debug.warn("second argument for setLayoutOrder() is not a subview");
return
};
if (sub1_$1 == "first") {
this.subviews.unshift(sub2_$2)
} else {
if (sub1_$1 == "last") {
this.subviews.push(sub2_$2)
} else {
for (var i_$4 = this.subviews.length - 1;i_$4 >= 0;i_$4--) {
if (this.subviews[i_$4] === sub1_$1) {
this.subviews.splice(i_$4 + 1, 0, sub2_$2);
break
}};
if (i_$4 == -1) {
if (sub1_$1 == sub2_$2) {
Debug.warn("%w is the same as %w", sub1_$1, sub2_$2)
} else {
Debug.warn("first argument for setLayoutOrder() is not a subview")
};
this.subviews.splice(i2_$3, 0, sub2_$2)
}}};
this.reset();
return
};
$lzsc$temp["displayName"] = "setLayoutOrder";
return $lzsc$temp
})(), "swapSubviewOrder", (function  () {
var $lzsc$temp = function  (sub1_$1, sub2_$2) {
var s1p_$3 = -1;
var s2p_$4 = -1;
for (var i_$5 = this.subviews.length - 1;i_$5 >= 0 && (s1p_$3 < 0 || s2p_$4 < 0);i_$5--) {
if (this.subviews[i_$5] === sub1_$1) {
s1p_$3 = i_$5
};
if (this.subviews[i_$5] === sub2_$2) {
s2p_$4 = i_$5
}};
if (s1p_$3 >= 0 && s2p_$4 >= 0) {
this.subviews[s2p_$4] = sub1_$1;
this.subviews[s1p_$3] = sub2_$2
} else {
Debug.warn("Invalid subviews for swapSubviewOrder()")
};
this.reset();
return
};
$lzsc$temp["displayName"] = "swapSubviewOrder";
return $lzsc$temp
})(), "__LZinsertAfter", (function  () {
var $lzsc$temp = function  (newsub_$1, oldsub_$2) {
for (var i_$3 = this.subviews.length - 1;i_$3 >= 0;i_$3--) {
if (this.subviews[i_$3] == oldsub_$2) {
this.subviews.splice(i_$3, 0, newsub_$1)
}}};
$lzsc$temp["displayName"] = "__LZinsertAfter";
return $lzsc$temp
})(), "update", (function  () {
var $lzsc$temp = function  (e_$1) {
switch (arguments.length) {
case 0:
e_$1 = null
}};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzLayout for view " + this.immediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "layout", "attributes", new LzInheritedHash(LzNode.attributes)]);
lz[LzLayout.tagname] = LzLayout;
Class.make("LzFont", null, ["style", void 0, "name", void 0, "height", void 0, "ascent", void 0, "descent", void 0, "advancetable", void 0, "lsbtable", void 0, "rsbtable", void 0, "fontobject", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (fontobject_$1, attrs_$2, style_$3) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.name = fontobject_$1.name;
this.style = style_$3;
this.fontobject = fontobject_$1;
fontobject_$1[style_$3] = this;
for (var k_$4 in attrs_$2) {
if (k_$4 == "leading") {
continue
};
this[k_$4] = attrs_$2[k_$4]
};
this.height = this.ascent + this.descent;
this.advancetable[13] = this.advancetable[32];
this.advancetable[160] = 0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "leading", 2, "toString", (function  () {
var $lzsc$temp = function  () {
return "Font style " + this.style + " of name " + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
lz.Font = LzFont;
Class.make("LzSelectionManager", LzNode, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "sel", "setSelected", "selectedHash", void 0, "selected", void 0, "toggle", void 0, "lastRangeStart", void 0, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.toggle = args_$2.toggle == true;
if (args_$2.sel != null) {
this.sel = args_$2.sel
};
this.selected = [];
this.selectedHash = {};
this.lastRangeStart = null
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "select", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.isSelected(o_$1) && (this.toggle || this.isMultiSelect(o_$1))) {
this.unselect(o_$1)
} else {
if (this.selected.length > 0 && this.isRangeSelect(o_$1)) {
var s_$2 = this.lastRangeStart || this.selected[0];
this.selectRange(s_$2, o_$1)
} else {
if (!this.isMultiSelect(o_$1)) {
this.clearSelection()
};
this.makeSelected(o_$1)
}}};
$lzsc$temp["displayName"] = "select";
return $lzsc$temp
})(), "isSelected", (function  () {
var $lzsc$temp = function  (o_$1) {
return this.selectedHash[o_$1.__LZUID] == true
};
$lzsc$temp["displayName"] = "isSelected";
return $lzsc$temp
})(), "makeSelected", (function  () {
var $lzsc$temp = function  (s_$1) {
if (!this.selectedHash[s_$1.__LZUID]) {
this.selectedHash[s_$1.__LZUID] = true;
this.selected.push(s_$1);
s_$1[this.sel](true)
}};
$lzsc$temp["displayName"] = "makeSelected";
return $lzsc$temp
})(), "unselect", (function  () {
var $lzsc$temp = function  (o_$1) {
var selh_$2 = this.selectedHash;
var sela_$3 = this.selected;
if (selh_$2[o_$1.__LZUID]) {
for (var i_$4 = sela_$3.length - 1;i_$4 >= 0;i_$4--) {
if (sela_$3[i_$4] === o_$1) {
delete selh_$2[o_$1.__LZUID];
sela_$3.splice(i_$4, 1);
o_$1[this.sel](false);
return
}}}};
$lzsc$temp["displayName"] = "unselect";
return $lzsc$temp
})(), "clearSelection", (function  () {
var $lzsc$temp = function  () {
var sela_$1 = this.selected;
this.selected = [];
this.selectedHash = {};
this.lastRangeStart = null;
var s_$2;
while (s_$2 = sela_$1.pop()) {
s_$2[this.sel](false)
}};
$lzsc$temp["displayName"] = "clearSelection";
return $lzsc$temp
})(), "getSelection", (function  () {
var $lzsc$temp = function  () {
return this.selected.concat()
};
$lzsc$temp["displayName"] = "getSelection";
return $lzsc$temp
})(), "selectRange", (function  () {
var $lzsc$temp = function  (s_$1, e_$2) {
var pview_$3 = s_$1.immediateparent;
var svs_$4 = pview_$3.subviews;
var st_$5 = -1;
var en_$6 = -1;
for (var i_$7 = 0;i_$7 < svs_$4.length && (st_$5 == -1 || en_$6 == -1);i_$7++) {
if (svs_$4[i_$7] === s_$1) {
st_$5 = i_$7
};
if (svs_$4[i_$7] === e_$2) {
en_$6 = i_$7
}};
var dir_$8 = st_$5 > en_$6 ? -1 : 1;
this.clearSelection();
this.lastRangeStart = s_$1;
if (st_$5 != -1 && en_$6 != -1) {
for (var i_$7 = st_$5;i_$7 != en_$6 + dir_$8;i_$7 += dir_$8) {
this.makeSelected(svs_$4[i_$7])
}}};
$lzsc$temp["displayName"] = "selectRange";
return $lzsc$temp
})(), "isMultiSelect", (function  () {
var $lzsc$temp = function  (o_$1) {
return lz.Keys.isKeyDown("control")
};
$lzsc$temp["displayName"] = "isMultiSelect";
return $lzsc$temp
})(), "isRangeSelect", (function  () {
var $lzsc$temp = function  (o_$1) {
return lz.Keys.isKeyDown("shift")
};
$lzsc$temp["displayName"] = "isRangeSelect";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzSelectionManager"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "selectionmanager", "attributes", new LzInheritedHash(LzNode.attributes)]);
lz[LzSelectionManager.tagname] = LzSelectionManager;
Class.make("LzDataSelectionManager", LzSelectionManager, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "manager", void 0, "singleClone", void 0, "makeSelected", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.manager == null) {
this.manager = o_$1.cloneManager
};
var so_$2 = o_$1.datapath.p;
if (so_$2 && !so_$2.sel) {
so_$2.sel = true;
this.selected.push(so_$2);
if (this.manager == null) {
this.singleClone = o_$1
};
o_$1.datapath.setSelected(true)
}};
$lzsc$temp["displayName"] = "makeSelected";
return $lzsc$temp
})(), "unselect", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.manager == null) {
this.manager = o_$1.cloneManager
};
var sela_$2 = this.selected;
var so_$3 = o_$1.datapath.p;
for (var i_$4 = sela_$2.length - 1;i_$4 >= 0;i_$4--) {
if (sela_$2[i_$4] === so_$3) {
so_$3.sel = false;
sela_$2.splice(i_$4, 1);
if (o_$1 == this.singleClone) {
this.singleClone = null
};
o_$1.datapath.setSelected(false);
return
}}};
$lzsc$temp["displayName"] = "unselect";
return $lzsc$temp
})(), "selectRange", (function  () {
var $lzsc$temp = function  (s_$1, e_$2) {
if (this.manager == null) {
this.manager = e_$2.cloneManager;
if (this.manager == null) {
return
}};
var nodes_$3 = this.manager.nodes;
var st_$4 = -1;
var en_$5 = -1;
var ennode_$6 = e_$2.datapath.p;
for (var i_$7 = 0;i_$7 < nodes_$3.length && (st_$4 == -1 || en_$5 == -1);i_$7++) {
if (nodes_$3[i_$7] === s_$1) {
st_$4 = i_$7
};
if (nodes_$3[i_$7] === ennode_$6) {
en_$5 = i_$7
}};
var dir_$8 = st_$4 > en_$5 ? -1 : 1;
this.clearSelection();
this.lastRangeStart = s_$1;
if (st_$4 != -1 && en_$5 != -1) {
for (var i_$7 = st_$4;i_$7 != en_$5 + dir_$8;i_$7 += dir_$8) {
var p_$9 = nodes_$3[i_$7];
p_$9.sel = true;
this.selected.push(p_$9);
this.__LZsetSelected(p_$9, true)
}}};
$lzsc$temp["displayName"] = "selectRange";
return $lzsc$temp
})(), "getSelection", (function  () {
var $lzsc$temp = function  () {
var sela_$1 = this.selected;
var r_$2 = [];
for (var i_$3 = 0;i_$3 < sela_$1.length;i_$3++) {
r_$2.push(new LzDatapointer(null, {pointer: sela_$1[i_$3]}))
};
return r_$2
};
$lzsc$temp["displayName"] = "getSelection";
return $lzsc$temp
})(), "clearSelection", (function  () {
var $lzsc$temp = function  () {
var sela_$1 = this.selected;
this.selected = [];
this.lastRangeStart = null;
var p_$2;
while (p_$2 = sela_$1.pop()) {
p_$2.sel = false;
this.__LZsetSelected(p_$2, false)
}};
$lzsc$temp["displayName"] = "clearSelection";
return $lzsc$temp
})(), "isSelected", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.manager == null) {
this.manager = o_$1.cloneManager
};
var so_$2 = o_$1.datapath.p;
return so_$2 && so_$2.sel
};
$lzsc$temp["displayName"] = "isSelected";
return $lzsc$temp
})(), "__LZsetSelected", (function  () {
var $lzsc$temp = function  (p_$1, val_$2) {
if (this.manager != null) {
var cl_$3 = this.manager.getCloneForNode(p_$1, true);
if (cl_$3) {
cl_$3.datapath.setSelected(val_$2)
} else {

}} else {
if (!val_$2) {
var scl_$4 = this.singleClone;
if (scl_$4 != null && scl_$4.datapath.p === p_$1) {
this.singleClone = null;
scl_$4.datapath.setSelected(val_$2)
}}}};
$lzsc$temp["displayName"] = "__LZsetSelected";
return $lzsc$temp
})()], ["tagname", "dataselectionmanager", "attributes", new LzInheritedHash(LzSelectionManager.attributes)]);
lz[LzDataSelectionManager.tagname] = LzDataSelectionManager;
Class.make("LzCommand", LzNode, ["active", true, "keys", null, "$lzc$set_key", (function  () {
var $lzsc$temp = function  (k_$1) {
var oldKeys_$2 = this.keys;
if (oldKeys_$2) {
lz.Keys.removeKeyComboCall(this, oldKeys_$2)
};
this.keys = k_$1;
lz.Keys.callOnKeyCombo(this, k_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_key";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
var oldKeys_$1 = this.keys;
if (oldKeys_$1) {
lz.Keys.removeKeyComboCall(this, oldKeys_$1)
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "onselect", LzDeclaredEvent, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "setKeys", (function  () {
var $lzsc$temp = function  (k_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_key(k_$1)
};
$lzsc$temp["displayName"] = "setKeys";
return $lzsc$temp
})(), "execute", (function  () {
var $lzsc$temp = function  (d_$1) {
if (this.active) {
if (this.onselect.ready) {
this.onselect.sendEvent(d_$1)
}}};
$lzsc$temp["displayName"] = "execute";
return $lzsc$temp
})(), "keysToString", (function  () {
var $lzsc$temp = function  () {
var s_$1 = "";
var keys_$2 = this.keys;
if (keys_$2) {
var dk_$3 = LzCommand.DisplayKeys;
var k_$4 = "";
var l_$5 = keys_$2.length - 1;
for (var i_$6 = 0;i_$6 < l_$5;i_$6++) {
k_$4 = keys_$2[i_$6];
if (k_$4 in dk_$3) {
k_$4 = dk_$3[k_$4]
};
s_$1 = s_$1 + k_$4 + "+"
};
k_$4 = keys_$2[i_$6];
if (k_$4 in dk_$3) {
k_$4 = dk_$3[k_$4]
};
s_$1 = s_$1 + k_$4
};
return s_$1
};
$lzsc$temp["displayName"] = "keysToString";
return $lzsc$temp
})()], ["tagname", "command", "attributes", new LzInheritedHash(LzNode.attributes), "DisplayKeys", {control: "Ctrl", shift: "Shift", alt: "Alt"}]);
lz[LzCommand.tagname] = LzCommand;
Class.make("LzState", LzNode, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__LZpool", [], "__LZstatedelegates", void 0, "onapply", LzDeclaredEvent, "onremove", LzDeclaredEvent, "onapplied", LzDeclaredEvent, "applied", false, "$lzc$set_applied", (function  () {
var $lzsc$temp = function  (v_$1) {
if (v_$1) {
if (this.isinited) {
this.apply()
} else {
this.applyOnInit = true
}} else {
if (this.isinited) {
this.remove()
}}};
$lzsc$temp["displayName"] = "$lzc$set_applied";
return $lzsc$temp
})(), "isapplied", false, "$lzc$set_apply", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setApply(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_apply";
return $lzsc$temp
})(), "asyncnew", false, "subh", null, "pooling", false, "$lzc$set_asyncnew", (function  () {
var $lzsc$temp = function  (v_$1) {
this.__LZsetProperty(v_$1, "asyncnew")
};
$lzsc$temp["displayName"] = "$lzc$set_asyncnew";
return $lzsc$temp
})(), "$lzc$set_pooling", (function  () {
var $lzsc$temp = function  (v_$1) {
this.__LZsetProperty(v_$1, "pooling")
};
$lzsc$temp["displayName"] = "$lzc$set_pooling";
return $lzsc$temp
})(), "$lzc$set___LZsourceLocation", (function  () {
var $lzsc$temp = function  (v_$1) {
this.__LZsetProperty(v_$1, "__LZsourceLocation")
};
$lzsc$temp["displayName"] = "$lzc$set___LZsourceLocation";
return $lzsc$temp
})(), "heldArgs", void 0, "handlerMethodNames", void 0, "releasedconstraints", void 0, "appliedChildren", void 0, "applyOnInit", false, "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.heldArgs = {};
this.handlerMethodNames = {};
this.appliedChildren = []
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
if (this.applyOnInit) {
this.apply()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "createChildren", (function  () {
var $lzsc$temp = function  (carr_$1) {
this.subh = carr_$1;
this.__LZinstantiationDone()
};
$lzsc$temp["displayName"] = "createChildren";
return $lzsc$temp
})(), "setApply", (function  () {
var $lzsc$temp = function  (doapply_$1) {
Debug.deprecated(this, arguments.callee, "setAttribute('applied', " + doapply_$1 + ")");
if (typeof doapply_$1 == "function") {
this.addProperty("apply", doapply_$1);
return
};
this.$lzc$set_applied(doapply_$1)
};
$lzsc$temp["displayName"] = "setApply";
return $lzsc$temp
})(), "apply", (function  () {
var $lzsc$temp = function  () {
if (this.applied) {
return
};
var parent_$1 = this.parent;
this.applied = this.isapplied = true;
var pia_$2 = parent_$1._instanceAttrs;
if (pia_$2) {
for (var key_$3 in this.heldArgs) {
if (LzConstraintExpr["$lzsc$isa"] ? LzConstraintExpr.$lzsc$isa(pia_$2[key_$3]) : pia_$2[key_$3] instanceof LzConstraintExpr) {
if (this.releasedconstraints == null) {
this.releasedconstraints = []
};
var constraintMethodName_$4 = pia_$2[key_$3].methodName;
if (parent_$1.releaseConstraintMethod(constraintMethodName_$4)) {
this.releasedconstraints.push(constraintMethodName_$4)
}}}};
var od_$5 = parent_$1.__LZdelegates;
parent_$1.__LZdelegates = null;
parent_$1.__LZapplyArgs(this.heldArgs);
if (this.subh) {
var shl_$6 = this.subh.length
};
parent_$1.__LZsetPreventInit();
for (var i_$7 = 0;i_$7 < shl_$6;i_$7++) {
if (this.__LZpool && this.__LZpool[i_$7]) {
this.appliedChildren.push(this.__LZretach(this.__LZpool[i_$7]))
} else {
this.appliedChildren.push(parent_$1.makeChild(this.subh[i_$7], this.asyncnew))
}};
parent_$1.__LZclearPreventInit();
parent_$1.__LZresolveReferences();
this.__LZstatedelegates = parent_$1.__LZdelegates;
parent_$1.__LZdelegates = od_$5;
if (this.onapply.ready) {
this.onapply.sendEvent(this)
};
if (this.onapplied.ready) {
this.onapplied.sendEvent(true)
}};
$lzsc$temp["displayName"] = "apply";
return $lzsc$temp
})(), "remove", (function  () {
var $lzsc$temp = function  () {
if (!this.applied) {
return
};
this.applied = this.isapplied = false;
if (this.onremove.ready) {
this.onremove.sendEvent(this)
};
if (this.onapplied.ready) {
this.onapplied.sendEvent(false)
};
if (this.__LZstatedelegates) {
for (var i_$1 = 0;i_$1 < this.__LZstatedelegates.length;i_$1++) {
this.__LZstatedelegates[i_$1].unregisterAll()
}};
if (this.pooling && this.appliedChildren.length) {
this.__LZpool = []
};
for (var i_$1 = 0;i_$1 < this.appliedChildren.length;i_$1++) {
var ac_$2 = this.appliedChildren[i_$1];
if (this.pooling) {
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ac_$2) : ac_$2 instanceof LzView) {
this.__LZpool.push(this.__LZdetach(ac_$2))
} else {
ac_$2.destroy();
this.__LZpool.push(null)
}} else {
ac_$2.destroy()
}};
this.appliedChildren = [];
if (this.releasedconstraints != null) {
this.releasedconstraints = null
}};
$lzsc$temp["displayName"] = "remove";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.pooling = false;
this.remove();
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "__LZapplyArgs", (function  () {
var $lzsc$temp = function  (args_$1, constcall_$2) {
switch (arguments.length) {
case 1:
constcall_$2 = null
};
var stateArgs_$3 = {};
var held_$4 = this.heldArgs;
var handlers_$5 = this.handlerMethodNames;
for (var key_$6 in args_$1) {
var val_$7 = args_$1[key_$6];
var setr_$8 = "$lzc$set_" + key_$6;
if ((Function["$lzsc$isa"] ? Function.$lzsc$isa(this[setr_$8]) : this[setr_$8] instanceof Function) || (key_$6 in handlers_$5)) {
stateArgs_$3[key_$6] = val_$7
} else {
held_$4[key_$6] = val_$7
}};
for (var key_$6 in stateArgs_$3) {
var val_$7 = stateArgs_$3[key_$6];
if (LzOnceExpr["$lzsc$isa"] ? LzOnceExpr.$lzsc$isa(val_$7) : val_$7 instanceof LzOnceExpr) {
var methodName_$9 = val_$7.methodName;
if (methodName_$9 in held_$4) {
stateArgs_$3[methodName_$9] = held_$4[methodName_$9];
delete held_$4[methodName_$9]
} else {

};
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(val_$7) : val_$7 instanceof LzAlwaysExpr) {
var dependenciesName_$10 = val_$7.dependenciesName;
if (dependenciesName_$10 in held_$4) {
stateArgs_$3[dependenciesName_$10] = held_$4[dependenciesName_$10];
delete held_$4[dependenciesName_$10]
} else {

}}}};
var rename_$11 = null;
for (var key_$6 in held_$4) {
var val_$7 = held_$4[key_$6];
if (LzOnceExpr["$lzsc$isa"] ? LzOnceExpr.$lzsc$isa(val_$7) : val_$7 instanceof LzOnceExpr) {
if (rename_$11 == null) {
rename_$11 = []
};
rename_$11.push(key_$6, val_$7)
}};
if (rename_$11 != null) {
for (var i_$12 = 0, l_$13 = rename_$11.length;i_$12 < l_$13;i_$12 += 2) {
var key_$6 = rename_$11[i_$12];
var expr_$14 = rename_$11[i_$12 + 1];
var methodName_$9 = expr_$14.methodName;
var newMethodName_$15 = methodName_$9 + this.__LZUID;
var dbgName_$16 = null;
dbgName_$16 = expr_$14._dbg_name;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(held_$4[methodName_$9]) : held_$4[methodName_$9] instanceof Function) {
held_$4[newMethodName_$15] = held_$4[methodName_$9];
delete held_$4[methodName_$9]
} else {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[methodName_$9]) : this[methodName_$9] instanceof Function) {
held_$4[newMethodName_$15] = this[methodName_$9]
}};
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(expr_$14) : expr_$14 instanceof LzAlwaysExpr) {
var dependenciesName_$10 = expr_$14.dependenciesName;
var newDependenciesName_$17 = dependenciesName_$10 + this.__LZUID;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(held_$4[dependenciesName_$10]) : held_$4[dependenciesName_$10] instanceof Function) {
held_$4[newDependenciesName_$17] = held_$4[dependenciesName_$10];
delete held_$4[dependenciesName_$10]
} else {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[dependenciesName_$10]) : this[dependenciesName_$10] instanceof Function) {
held_$4[newDependenciesName_$17] = this[dependenciesName_$10]
}};
held_$4[key_$6] = new (expr_$14.constructor)(newMethodName_$15, newDependenciesName_$17, dbgName_$16)
} else {
held_$4[key_$6] = new (expr_$14.constructor)(newMethodName_$15, dbgName_$16)
}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZapplyArgs"] || this.nextMethod(arguments.callee, "__LZapplyArgs")).call(this, stateArgs_$3)
};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "$lzc$set_$delegates", (function  () {
var $lzsc$temp = function  (delarr_$1) {
var pardels_$2 = [];
var mydels_$3 = [];
for (var i_$4 = 0;i_$4 < delarr_$1.length;i_$4 += 3) {
if (LzState.events[delarr_$1[i_$4]] && !delarr_$1[i_$4 + 2]) {
var arrtopush_$5 = mydels_$3;
var mname_$6 = delarr_$1[i_$4 + 1];
if (this.heldArgs[mname_$6]) {
this.addProperty(mname_$6, this.heldArgs[mname_$6]);
delete this.heldArgs[mname_$6]
};
this.handlerMethodNames[mname_$6] = true
} else {
var arrtopush_$5 = pardels_$2
};
arrtopush_$5.push(delarr_$1[i_$4], delarr_$1[i_$4 + 1], delarr_$1[i_$4 + 2])
};
if (mydels_$3.length) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_$delegates"] || this.nextMethod(arguments.callee, "$lzc$set_$delegates")).call(this, mydels_$3)
};
if (pardels_$2.length) {
this.heldArgs.$delegates = pardels_$2
}};
$lzsc$temp["displayName"] = "$lzc$set_$delegates";
return $lzsc$temp
})(), "__LZsetProperty", (function  () {
var $lzsc$temp = function  (prop_$1, propname_$2) {
this[propname_$2] = prop_$1
};
$lzsc$temp["displayName"] = "__LZsetProperty";
return $lzsc$temp
})(), "__LZdetach", (function  () {
var $lzsc$temp = function  (aview_$1) {
aview_$1.$lzc$set_visible(false);
return aview_$1
};
$lzsc$temp["displayName"] = "__LZdetach";
return $lzsc$temp
})(), "__LZretach", (function  () {
var $lzsc$temp = function  (aview_$1) {
aview_$1.$lzc$set_visible(true);
return aview_$1
};
$lzsc$temp["displayName"] = "__LZretach";
return $lzsc$temp
})()], ["tagname", "state", "attributes", new LzInheritedHash(LzNode.attributes), "props", {apply: true}, "events", {onremove: true, onapply: true, onapplied: true}]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
prototype.$isstate = true
}}};
$lzsc$temp["displayName"] = "helpers/LzState.lzs#92/1";
return $lzsc$temp
})()(LzState);
lz[LzState.tagname] = LzState;
Mixin.make("LzDataNodeMixin", null, ["onownerDocument", LzDeclaredEvent, "onDocumentChange", LzDeclaredEvent, "onparentNode", LzDeclaredEvent, "onchildNode", LzDeclaredEvent, "onchildNodes", LzDeclaredEvent, "onattributes", LzDeclaredEvent, "onnodeName", LzDeclaredEvent, "nodeType", void 0, "parentNode", null, "ownerDocument", void 0, "childNodes", null, "__LZo", -1, "__LZcoDirty", true, "sel", false, "__LZuserData", null, "__LZuserHandler", null, "getParent", (function  () {
var $lzsc$temp = function  () {
return this.parentNode
};
$lzsc$temp["displayName"] = "getParent";
return $lzsc$temp
})(), "getOffset", (function  () {
var $lzsc$temp = function  () {
if (!this.parentNode) {
return 0
};
if (this.parentNode.__LZcoDirty) {
this.parentNode.__LZupdateCO()
};
return this.__LZo
};
$lzsc$temp["displayName"] = "getOffset";
return $lzsc$temp
})(), "getPreviousSibling", (function  () {
var $lzsc$temp = function  () {
if (!this.parentNode) {
return null
};
if (this.parentNode.__LZcoDirty) {
this.parentNode.__LZupdateCO()
};
return this.parentNode.childNodes[this.__LZo - 1]
};
$lzsc$temp["displayName"] = "getPreviousSibling";
return $lzsc$temp
})(), "$lzc$getPreviousSibling_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [this.parentNode, "childNodes", this, "parentNode"]
};
$lzsc$temp["displayName"] = "$lzc$getPreviousSibling_dependencies";
return $lzsc$temp
})(), "getNextSibling", (function  () {
var $lzsc$temp = function  () {
if (!this.parentNode) {
return null
};
if (this.parentNode.__LZcoDirty) {
this.parentNode.__LZupdateCO()
};
return this.parentNode.childNodes[this.__LZo + 1]
};
$lzsc$temp["displayName"] = "getNextSibling";
return $lzsc$temp
})(), "$lzc$getNextSibling_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [this.parentNode, "childNodes", this, "parentNode"]
};
$lzsc$temp["displayName"] = "$lzc$getNextSibling_dependencies";
return $lzsc$temp
})(), "childOfNode", (function  () {
var $lzsc$temp = function  (el_$1, allowself_$2) {
switch (arguments.length) {
case 1:
allowself_$2 = false
};
var p_$3 = allowself_$2 ? this : this.parentNode;
while (p_$3) {
if (p_$3 === el_$1) {
return true
};
p_$3 = p_$3.parentNode
};
return false
};
$lzsc$temp["displayName"] = "childOfNode";
return $lzsc$temp
})(), "childOf", (function  () {
var $lzsc$temp = function  (el_$1, allowself_$2) {
switch (arguments.length) {
case 1:
allowself_$2 = false
};
return this.childOfNode(el_$1, allowself_$2)
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "$lzc$set_ownerDocument", (function  () {
var $lzsc$temp = function  (ownerDoc_$1) {
this.ownerDocument = ownerDoc_$1;
if (this.childNodes) {
for (var i_$2 = 0;i_$2 < this.childNodes.length;i_$2++) {
this.childNodes[i_$2].$lzc$set_ownerDocument(ownerDoc_$1)
}};
if (this.onownerDocument.ready) {
this.onownerDocument.sendEvent(ownerDoc_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_ownerDocument";
return $lzsc$temp
})(), "setOwnerDocument", (function  () {
var $lzsc$temp = function  (ownerDoc_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_ownerDocument(ownerDoc_$1)
};
$lzsc$temp["displayName"] = "setOwnerDocument";
return $lzsc$temp
})(), "cloneNode", (function  () {
var $lzsc$temp = function  (deep_$1) {
switch (arguments.length) {
case 0:
deep_$1 = false
};
return undefined
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "serialize", (function  () {
var $lzsc$temp = function  () {
return undefined
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "__LZlockFromUpdate", (function  () {
var $lzsc$temp = function  (locker_$1) {
this.ownerDocument.__LZdoLock(locker_$1)
};
$lzsc$temp["displayName"] = "__LZlockFromUpdate";
return $lzsc$temp
})(), "__LZunlockFromUpdate", (function  () {
var $lzsc$temp = function  (locker_$1) {
this.ownerDocument.__LZdoUnlock(locker_$1)
};
$lzsc$temp["displayName"] = "__LZunlockFromUpdate";
return $lzsc$temp
})(), "setUserData", (function  () {
var $lzsc$temp = function  (key_$1, data_$2, handler_$3) {
switch (arguments.length) {
case 2:
handler_$3 = null
};
if (this.__LZuserData == null) {
this.__LZuserData = {}};
if (handler_$3 != null) {
Debug.warn("use of the handler arg to setUserData is not currently implemented")
};
var prevdata_$4 = this.__LZuserData[key_$1];
if (data_$2 != null) {
this.__LZuserData[key_$1] = data_$2
} else {
if (prevdata_$4 != null) {
delete this.__LZuserData[key_$1]
}};
return prevdata_$4 != null ? prevdata_$4 : null
};
$lzsc$temp["displayName"] = "setUserData";
return $lzsc$temp
})(), "getUserData", (function  () {
var $lzsc$temp = function  (key_$1) {
if (this.__LZuserData == null) {
return null
} else {
var udata_$2 = this.__LZuserData[key_$1];
return udata_$2 != null ? udata_$2 : null
}};
$lzsc$temp["displayName"] = "getUserData";
return $lzsc$temp
})()], null);
lz.DataNodeMixin = LzDataNodeMixin;
Class.make("LzDataNode", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "lz.DataNode"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["ELEMENT_NODE", 1, "TEXT_NODE", 3, "DOCUMENT_NODE", 9, "stringToLzData", (function  () {
var $lzsc$temp = function  (str_$1, trimwhitespace_$2, nsprefix_$3) {
switch (arguments.length) {
case 1:
trimwhitespace_$2 = false;;case 2:
nsprefix_$3 = false
};
Debug.info("lz.DataNode.stringToLzData is deprecated.  Use `lz.DataElement.stringToLzData` instead.");
return LzDataElement.stringToLzData(str_$1, trimwhitespace_$2, nsprefix_$3)
};
$lzsc$temp["displayName"] = "stringToLzData";
return $lzsc$temp
})()]);
lz.DataNode = LzDataNode;
Mixin.make("LzDataElementMixin", null, ["__LZchangeQ", null, "__LZlocker", null, "nodeName", null, "attributes", null, "insertBefore", (function  () {
var $lzsc$temp = function  (newChild_$1, refChild_$2) {
if (newChild_$1 == null) {
return null
} else {
if (refChild_$2 == null) {
return this.appendChild(newChild_$1)
} else {
var off_$3 = this.__LZgetCO(refChild_$2);
if (off_$3 >= 0) {
var samenode_$4 = newChild_$1 === refChild_$2;
if (newChild_$1.parentNode != null) {
if (newChild_$1.parentNode === this) {
if (!samenode_$4) {
var nchildoff_$5 = this.__LZremoveChild(newChild_$1);
if (nchildoff_$5 != -1 && nchildoff_$5 < off_$3) {
off_$3 -= 1
}}} else {
newChild_$1.parentNode.removeChild(newChild_$1)
}};
if (!samenode_$4) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$3, 0, newChild_$1)
};
newChild_$1.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$1.parentNode = this;
if (newChild_$1.onparentNode.ready) {
newChild_$1.onparentNode.sendEvent(this)
};
if (this.onchildNodes.ready) {
this.onchildNodes.sendEvent(newChild_$1)
};
this.ownerDocument.handleDocumentChange("insertBefore", this, 0);
return newChild_$1
};
return null
}}};
$lzsc$temp["displayName"] = "insertBefore";
return $lzsc$temp
})(), "replaceChild", (function  () {
var $lzsc$temp = function  (newChild_$1, oldChild_$2) {
if (newChild_$1 == null) {
return null
} else {
var off_$3 = this.__LZgetCO(oldChild_$2);
if (off_$3 >= 0) {
var samenode_$4 = newChild_$1 === oldChild_$2;
if (newChild_$1.parentNode != null) {
if (newChild_$1.parentNode === this) {
if (!samenode_$4) {
var nchildoff_$5 = this.__LZremoveChild(newChild_$1);
if (nchildoff_$5 != -1 && nchildoff_$5 < off_$3) {
off_$3 -= 1
}}} else {
newChild_$1.parentNode.removeChild(newChild_$1)
}};
if (!samenode_$4) {
newChild_$1.__LZo = off_$3;
this.childNodes[off_$3] = newChild_$1
};
newChild_$1.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$1.parentNode = this;
if (newChild_$1.onparentNode.ready) {
newChild_$1.onparentNode.sendEvent(this)
};
if (this.onchildNodes.ready) {
this.onchildNodes.sendEvent(newChild_$1)
};
this.ownerDocument.handleDocumentChange("childNodes", this, 0, newChild_$1);
return newChild_$1
};
return null
}};
$lzsc$temp["displayName"] = "replaceChild";
return $lzsc$temp
})(), "removeChild", (function  () {
var $lzsc$temp = function  (oldChild_$1) {
var off_$2 = this.__LZgetCO(oldChild_$1);
if (off_$2 >= 0) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$2, 1);
if (this.onchildNodes.ready) {
this.onchildNodes.sendEvent(oldChild_$1)
};
this.ownerDocument.handleDocumentChange("removeChild", this, 0, oldChild_$1);
return oldChild_$1
};
return null
};
$lzsc$temp["displayName"] = "removeChild";
return $lzsc$temp
})(), "appendChild", (function  () {
var $lzsc$temp = function  (newChild_$1) {
if (newChild_$1 == null) {
return null
} else {
if (newChild_$1.parentNode != null) {
if (newChild_$1.parentNode === this) {
this.__LZremoveChild(newChild_$1)
} else {
newChild_$1.parentNode.removeChild(newChild_$1)
}};
this.childNodes.push(newChild_$1);
newChild_$1.__LZo = this.childNodes.length - 1;
newChild_$1.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$1.parentNode = this;
if (newChild_$1.onparentNode.ready) {
newChild_$1.onparentNode.sendEvent(this)
};
if (this.onchildNodes.ready) {
this.onchildNodes.sendEvent(newChild_$1)
};
this.ownerDocument.handleDocumentChange("appendChild", this, 0, newChild_$1);
return newChild_$1
}};
$lzsc$temp["displayName"] = "appendChild";
return $lzsc$temp
})(), "hasChildNodes", (function  () {
var $lzsc$temp = function  () {
return this.childNodes.length > 0
};
$lzsc$temp["displayName"] = "hasChildNodes";
return $lzsc$temp
})(), "cloneNode", (function  () {
var $lzsc$temp = function  (deep_$1) {
switch (arguments.length) {
case 0:
deep_$1 = false
};
var n_$2 = new LzDataElement(this.nodeName, this.attributes);
if (deep_$1) {
var cn_$3 = this.childNodes;
var copy_$4 = [];
for (var i_$5 = cn_$3.length - 1;i_$5 >= 0;--i_$5) {
copy_$4[i_$5] = cn_$3[i_$5].cloneNode(true)
};
n_$2.$lzc$set_childNodes(copy_$4)
};
return n_$2
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "getAttr", (function  () {
var $lzsc$temp = function  (name_$1) {
return this.attributes[name_$1]
};
$lzsc$temp["displayName"] = "getAttr";
return $lzsc$temp
})(), "$lzc$getAttr_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "attributes"]
};
$lzsc$temp["displayName"] = "$lzc$getAttr_dependencies";
return $lzsc$temp
})(), "setAttr", (function  () {
var $lzsc$temp = function  (name_$1, value_$2) {
value_$2 = String(value_$2);
this.attributes[name_$1] = value_$2;
if (this.onattributes.ready) {
this.onattributes.sendEvent(name_$1)
};
this.ownerDocument.handleDocumentChange("attributes", this, 1, {name: name_$1, value: value_$2, type: "set"});
return value_$2
};
$lzsc$temp["displayName"] = "setAttr";
return $lzsc$temp
})(), "removeAttr", (function  () {
var $lzsc$temp = function  (name_$1) {
var v_$2 = this.attributes[name_$1];
delete this.attributes[name_$1];
if (this.onattributes.ready) {
this.onattributes.sendEvent(name_$1)
};
this.ownerDocument.handleDocumentChange("attributes", this, 1, {name: name_$1, value: v_$2, type: "remove"});
return v_$2
};
$lzsc$temp["displayName"] = "removeAttr";
return $lzsc$temp
})(), "hasAttr", (function  () {
var $lzsc$temp = function  (name_$1) {
return this.attributes[name_$1] != null
};
$lzsc$temp["displayName"] = "hasAttr";
return $lzsc$temp
})(), "$lzc$hasAttr_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [self_$2, "attributes"]
};
$lzsc$temp["displayName"] = "$lzc$hasAttr_dependencies";
return $lzsc$temp
})(), "getFirstChild", (function  () {
var $lzsc$temp = function  () {
return this.childNodes[0]
};
$lzsc$temp["displayName"] = "getFirstChild";
return $lzsc$temp
})(), "$lzc$getFirstChild_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [this, "childNodes"]
};
$lzsc$temp["displayName"] = "$lzc$getFirstChild_dependencies";
return $lzsc$temp
})(), "getLastChild", (function  () {
var $lzsc$temp = function  () {
return this.childNodes[this.childNodes.length - 1]
};
$lzsc$temp["displayName"] = "getLastChild";
return $lzsc$temp
})(), "$lzc$getLastChild_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2) {
return [this, "childNodes"]
};
$lzsc$temp["displayName"] = "$lzc$getLastChild_dependencies";
return $lzsc$temp
})(), "__LZgetCO", (function  () {
var $lzsc$temp = function  (child_$1) {
if (child_$1 != null) {
var cn_$2 = this.childNodes;
if (!this.__LZcoDirty) {
var i_$3 = child_$1.__LZo;
if (cn_$2[i_$3] === child_$1) {
return i_$3
}} else {
for (var i_$3 = cn_$2.length - 1;i_$3 >= 0;--i_$3) {
if (cn_$2[i_$3] === child_$1) {
return i_$3
}}}};
return -1
};
$lzsc$temp["displayName"] = "__LZgetCO";
return $lzsc$temp
})(), "__LZremoveChild", (function  () {
var $lzsc$temp = function  (oldChild_$1) {
var off_$2 = this.__LZgetCO(oldChild_$1);
if (off_$2 >= 0) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$2, 1)
};
return off_$2
};
$lzsc$temp["displayName"] = "__LZremoveChild";
return $lzsc$temp
})(), "__LZupdateCO", (function  () {
var $lzsc$temp = function  () {
var cn_$1 = this.childNodes;
for (var i_$2 = 0, len_$3 = cn_$1.length;i_$2 < len_$3;i_$2++) {
cn_$1[i_$2].__LZo = i_$2
};
this.__LZcoDirty = false
};
$lzsc$temp["displayName"] = "__LZupdateCO";
return $lzsc$temp
})(), "$lzc$set_attributes", (function  () {
var $lzsc$temp = function  (attrs_$1) {
var a_$2 = {};
for (var k_$3 in attrs_$1) {
var val_$4 = attrs_$1[k_$3];
if (typeof val_$4 != "string") {
Debug.info("In a future release, lz.DataElement will coerce the non-string value %w to a String for the attribute %w.  You may safely ignore this as long as you expect to get a String value back.", val_$4, k_$3)
};
a_$2[k_$3] = attrs_$1[k_$3]
};
this.attributes = a_$2;
if (this.onattributes.ready) {
this.onattributes.sendEvent(a_$2)
};
this.ownerDocument.handleDocumentChange("attributes", this, 1)
};
$lzsc$temp["displayName"] = "$lzc$set_attributes";
return $lzsc$temp
})(), "setAttrs", (function  () {
var $lzsc$temp = function  (attrs_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_attributes(attrs_$1)
};
$lzsc$temp["displayName"] = "setAttrs";
return $lzsc$temp
})(), "$lzc$set_childNodes", (function  () {
var $lzsc$temp = function  (children_$1) {
if (!children_$1) {
children_$1 = []
};
this.childNodes = children_$1;
if (children_$1.length > 0) {
var notifyParent_$2 = true;
var otherParent_$3 = children_$1[0].parentNode;
if (otherParent_$3 != null && otherParent_$3 !== this && otherParent_$3.childNodes === children_$1) {
notifyParent_$2 = false;
otherParent_$3.$lzc$set_childNodes([])
};
for (var i_$4 = 0;i_$4 < children_$1.length;i_$4++) {
var c_$5 = children_$1[i_$4];
if (c_$5) {
if (notifyParent_$2 && c_$5.parentNode != null) {
if (c_$5.parentNode !== this) {
c_$5.parentNode.removeChild(c_$5)
}};
c_$5.$lzc$set_ownerDocument(this.ownerDocument);
c_$5.parentNode = this;
if (c_$5.onparentNode.ready) {
c_$5.onparentNode.sendEvent(this)
};
c_$5.__LZo = i_$4
}}};
this.__LZcoDirty = false;
if (this.onchildNodes.ready) {
this.onchildNodes.sendEvent(children_$1)
};
this.ownerDocument.handleDocumentChange("childNodes", this, 0)
};
$lzsc$temp["displayName"] = "$lzc$set_childNodes";
return $lzsc$temp
})(), "setChildNodes", (function  () {
var $lzsc$temp = function  (children_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_childNodes(children_$1)
};
$lzsc$temp["displayName"] = "setChildNodes";
return $lzsc$temp
})(), "$lzc$set_nodeName", (function  () {
var $lzsc$temp = function  (name_$1) {
this.nodeName = name_$1;
if (this.onnodeName.ready) {
this.onnodeName.sendEvent(name_$1)
};
if (this.parentNode) {
if (this.parentNode.onchildNodes.ready) {
this.parentNode.onchildNodes.sendEvent(this)
};
if (this.parentNode.onchildNode.ready) {
this.parentNode.onchildNode.sendEvent(this)
}};
this.ownerDocument.handleDocumentChange("childNodeName", this.parentNode, 0);
this.ownerDocument.handleDocumentChange("nodeName", this, 1)
};
$lzsc$temp["displayName"] = "$lzc$set_nodeName";
return $lzsc$temp
})(), "setNodeName", (function  () {
var $lzsc$temp = function  (name_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_nodeName(name_$1)
};
$lzsc$temp["displayName"] = "setNodeName";
return $lzsc$temp
})(), "__LZgetText", (function  () {
var $lzsc$temp = function  () {
var s_$1 = "";
var cn_$2 = this.childNodes;
for (var i_$3 = 0, len_$4 = cn_$2.length;i_$3 < len_$4;i_$3++) {
var c_$5 = cn_$2[i_$3];
if (c_$5.nodeType == LzDataElement.TEXT_NODE) {
s_$1 += c_$5.data
}};
return s_$1
};
$lzsc$temp["displayName"] = "__LZgetText";
return $lzsc$temp
})(), "getElementsByTagName", (function  () {
var $lzsc$temp = function  (name_$1) {
var r_$2 = [];
var cn_$3 = this.childNodes;
for (var i_$4 = 0, len_$5 = cn_$3.length;i_$4 < len_$5;i_$4++) {
if (cn_$3[i_$4].nodeName == name_$1) {
r_$2.push(cn_$3[i_$4])
}};
return r_$2
};
$lzsc$temp["displayName"] = "getElementsByTagName";
return $lzsc$temp
})(), "__LZlt", "<", "__LZgt", ">", "serialize", (function  () {
var $lzsc$temp = function  () {
return this.serializeInternal()
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "serializeInternal", (function  () {
var $lzsc$temp = function  (len_$1) {
switch (arguments.length) {
case 0:
len_$1 = Infinity
};
var s_$2 = this.__LZlt + this.nodeName;
var attrs_$3 = this.attributes;
for (var k_$4 in attrs_$3) {
s_$2 += " " + k_$4 + '="' + LzDataElement.__LZXMLescape(attrs_$3[k_$4]) + '"';
if (s_$2.length > len_$1) {
break
}};
var cn_$5 = this.childNodes;
if (s_$2.length <= len_$1 && cn_$5.length) {
s_$2 += this.__LZgt;
for (var i_$6 = 0, cnlen_$7 = cn_$5.length;i_$6 < cnlen_$7;i_$6++) {
s_$2 += cn_$5[i_$6].serialize();
if (s_$2.length > len_$1) {
break
}};
s_$2 += this.__LZlt + "/" + this.nodeName + this.__LZgt
} else {
s_$2 += "/" + this.__LZgt
};
if (s_$2.length > len_$1) {
s_$2 = Debug.abbreviate(s_$2, len_$1)
};
return s_$2
};
$lzsc$temp["displayName"] = "serializeInternal";
return $lzsc$temp
})(), "dataElementMixin_dbg_name", (function  () {
var $lzsc$temp = function  () {
return this.serializeInternal(Debug.printLength)
};
$lzsc$temp["displayName"] = "dataElementMixin_dbg_name";
return $lzsc$temp
})(), "handleDocumentChange", (function  () {
var $lzsc$temp = function  (what_$1, who_$2, type_$3, cobj_$4) {
switch (arguments.length) {
case 3:
cobj_$4 = null
};
var o_$5 = {who: who_$2, what: what_$1, type: type_$3};
if (cobj_$4) {
o_$5.cobj = cobj_$4
};
if (this.__LZchangeQ) {
this.__LZchangeQ.push(o_$5)
} else {
if (this.onDocumentChange.ready) {
this.onDocumentChange.sendEvent(o_$5)
}}};
$lzsc$temp["displayName"] = "handleDocumentChange";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.serialize()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "__LZdoLock", (function  () {
var $lzsc$temp = function  (locker_$1) {
if (!this.__LZchangeQ) {
this.__LZchangeQ = [];
this.__LZlocker = locker_$1
}};
$lzsc$temp["displayName"] = "__LZdoLock";
return $lzsc$temp
})(), "__LZdoUnlock", (function  () {
var $lzsc$temp = function  (locker_$1) {
if (this.__LZlocker != locker_$1) {
return
};
var lzq_$2 = this.__LZchangeQ;
this.__LZchangeQ = null;
this.__LZlocker = null;
if (lzq_$2 != null) {
for (var i_$3 = 0, len_$4 = lzq_$2.length;i_$3 < len_$4;i_$3++) {
var sendit_$5 = true;
var tc_$6 = lzq_$2[i_$3];
for (var j_$7 = 0;j_$7 < i_$3;j_$7++) {
var oc_$8 = lzq_$2[j_$7];
if (tc_$6.who == oc_$8.who && tc_$6.what == oc_$8.what && tc_$6.type == oc_$8.type) {
sendit_$5 = false;
break
}};
if (sendit_$5) {
this.handleDocumentChange(tc_$6.what, tc_$6.who, tc_$6.type)
}}}};
$lzsc$temp["displayName"] = "__LZdoUnlock";
return $lzsc$temp
})()], null);
lz.DataElementMixin = LzDataElementMixin;
Class.make("LzDataElement", [LzDataElementMixin, LzDataNodeMixin, LzDataNode], ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (name_$1, attributes_$2, children_$3) {
switch (arguments.length) {
case 1:
attributes_$2 = null;;case 2:
children_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.nodeName = name_$1;
this.nodeType = LzDataElement.ELEMENT_NODE;
this.ownerDocument = this;
if (attributes_$2) {
this.$lzc$set_attributes(attributes_$2)
} else {
this.attributes = {}};
if (children_$3) {
this.$lzc$set_childNodes(children_$3)
} else {
this.childNodes = [];
this.__LZcoDirty = false
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["NODE_CLONED", 1, "NODE_IMPORTED", 2, "NODE_DELETED", 3, "NODE_RENAMED", 4, "NODE_ADOPTED", 5, "makeNodeList", (function  () {
var $lzsc$temp = function  (count_$1, name_$2) {
var a_$3 = [];
for (var i_$4 = 0;i_$4 < count_$1;i_$4++) {
a_$3[i_$4] = new LzDataElement(name_$2)
};
return a_$3
};
$lzsc$temp["displayName"] = "makeNodeList";
return $lzsc$temp
})(), "valueToElement", (function  () {
var $lzsc$temp = function  (o_$1) {
return new LzDataElement("element", {}, LzDataElement.__LZv2E(o_$1))
};
$lzsc$temp["displayName"] = "valueToElement";
return $lzsc$temp
})(), "__LZv2E", (function  () {
var $lzsc$temp = function  (o_$1) {
var c_$2 = [];
if (typeof o_$1 == "object") {
if ((LzDataElement["$lzsc$isa"] ? LzDataElement.$lzsc$isa(o_$1) : o_$1 instanceof LzDataElement) || (LzDataText["$lzsc$isa"] ? LzDataText.$lzsc$isa(o_$1) : o_$1 instanceof LzDataText)) {
c_$2[0] = o_$1
} else {
if (Date["$lzsc$isa"] ? Date.$lzsc$isa(o_$1) : o_$1 instanceof Date) {

} else {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(o_$1) : o_$1 instanceof Array) {
var tag_$3 = o_$1.__LZtag != null ? o_$1.__LZtag : "item";
for (var i_$4 = 0;i_$4 < o_$1.length;i_$4++) {
var tmpC_$5 = LzDataElement.__LZv2E(o_$1[i_$4]);
c_$2[i_$4] = new LzDataElement(tag_$3, null, tmpC_$5)
}} else {
var i_$4 = 0;
for (var k_$6 in o_$1) {
if (k_$6.indexOf("__LZ") == 0) {
continue
};
c_$2[i_$4++] = new LzDataElement(k_$6, null, LzDataElement.__LZv2E(o_$1[k_$6]))
}}}}} else {
if (o_$1 != null) {
c_$2[0] = new LzDataText(o_$1)
}};
return c_$2.length != 0 ? c_$2 : null
};
$lzsc$temp["displayName"] = "__LZv2E";
return $lzsc$temp
})(), "ELEMENT_NODE", 1, "TEXT_NODE", 3, "DOCUMENT_NODE", 9, "__LZescapechars", {"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&apos;"}, "__LZXMLescape", (function  () {
var $lzsc$temp = function  (t_$1) {
if (typeof t_$1 != "string") {
return t_$1
};
var escChars_$2 = LzDataElement.__LZescapechars;
var olen_$3 = t_$1.length;
var r_$4 = "";
for (var i_$5 = 0;i_$5 < olen_$3;i_$5++) {
var code_$6 = t_$1.charCodeAt(i_$5);
if (code_$6 < 32) {
r_$4 += "&#x" + code_$6.toString(16) + ";"
} else {
var c_$7 = t_$1.charAt(i_$5);
r_$4 += escChars_$2[c_$7] || c_$7
}};
return r_$4
};
$lzsc$temp["displayName"] = "__LZXMLescape";
return $lzsc$temp
})(), "stringToLzData", (function  () {
var $lzsc$temp = function  (str_$1, trimwhitespace_$2, nsprefix_$3) {
switch (arguments.length) {
case 1:
trimwhitespace_$2 = false;;case 2:
nsprefix_$3 = false
};
if (str_$1 != null && str_$1 != "") {
var nativexml_$4;
try {
nativexml_$4 = LzXMLParser.parseXML(str_$1, trimwhitespace_$2, nsprefix_$3)
}
catch (e) {
Debug.warn("Cannot parse xml-string '%s': %w", str_$1, e)
};
if (nativexml_$4 != null) {
var lfcnode_$5 = LzXMLTranslator.copyXML(nativexml_$4, trimwhitespace_$2, nsprefix_$3);
return lfcnode_$5
}};
return null
};
$lzsc$temp["displayName"] = "stringToLzData";
return $lzsc$temp
})(), "whitespaceChars", {" ": true, "\r": true, "\n": true, "\t": true}, "trim", (function  () {
var $lzsc$temp = function  (str_$1) {
var whitech_$2 = LzDataElement.whitespaceChars;
var len_$3 = str_$1.length;
var sindex_$4 = 0;
var eindex_$5 = len_$3 - 1;
var ch_$6;
while (sindex_$4 < len_$3) {
ch_$6 = str_$1.charAt(sindex_$4);
if (whitech_$2[ch_$6] != true) {
break
};
sindex_$4++
};
while (eindex_$5 > sindex_$4) {
ch_$6 = str_$1.charAt(eindex_$5);
if (whitech_$2[ch_$6] != true) {
break
};
eindex_$5--
};
return str_$1.slice(sindex_$4, eindex_$5 + 1)
};
$lzsc$temp["displayName"] = "trim";
return $lzsc$temp
})()]);
lz.DataElement = LzDataElement;
Class.make("LzDataText", [LzDataNodeMixin, LzDataNode], ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (text_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.nodeType = LzDataElement.TEXT_NODE;
this.data = text_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ondata", LzDeclaredEvent, "nodeName", "#text", "data", "", "$lzc$set_data", (function  () {
var $lzsc$temp = function  (newdata_$1) {
this.data = newdata_$1;
if (this.ondata.ready) {
this.ondata.sendEvent(newdata_$1)
};
if (this.ownerDocument) {
this.ownerDocument.handleDocumentChange("data", this, 1)
}};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "setData", (function  () {
var $lzsc$temp = function  (newdata_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_data(newdata_$1)
};
$lzsc$temp["displayName"] = "setData";
return $lzsc$temp
})(), "cloneNode", (function  () {
var $lzsc$temp = function  (deep_$1) {
switch (arguments.length) {
case 0:
deep_$1 = false
};
var n_$2 = new LzDataText(this.data);
return n_$2
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "serialize", (function  () {
var $lzsc$temp = function  () {
return LzDataElement.__LZXMLescape(this.data)
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.data
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
lz.DataText = LzDataText;
Class.make("LzDataRequest", LzEventable, ["requestor", null, "src", null, "timeout", Infinity, "status", null, "rawdata", null, "error", null, "onstatus", LzDeclaredEvent, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (requestor_$1) {
switch (arguments.length) {
case 0:
requestor_$1 = null
};
this.requestor = requestor_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], ["SUCCESS", "success", "TIMEOUT", "timeout", "ERROR", "error", "READY", "ready", "LOADING", "loading"]);
lz.DataRequest = LzDataRequest;
Class.make("LzDataProvider", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "doRequest", (function  () {
var $lzsc$temp = function  (dreq_$1) {

};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "abortLoadForRequest", (function  () {
var $lzsc$temp = function  (dreq_$1) {

};
$lzsc$temp["displayName"] = "abortLoadForRequest";
return $lzsc$temp
})()], null);
lz.DataProvider = LzDataProvider;
Class.make("LzHTTPDataRequest", LzDataRequest, ["method", "GET", "postbody", void 0, "proxied", void 0, "proxyurl", void 0, "multirequest", false, "queuerequests", false, "queryparams", null, "requestheaders", null, "getresponseheaders", false, "responseheaders", void 0, "cacheable", false, "clientcacheable", false, "trimwhitespace", false, "nsprefix", false, "serverproxyargs", null, "xmldata", null, "loadtime", 0, "loadstarttime", void 0, "secure", false, "secureport", void 0, "parsexml", true, "loader", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (requestor_$1) {
switch (arguments.length) {
case 0:
requestor_$1 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, requestor_$1)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], null);
lz.HTTPDataRequest = LzHTTPDataRequest;
Class.make("LzHTTPDataProvider", LzDataProvider, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "makeLoader", (function  () {
var $lzsc$temp = function  (dreq_$1) {
var proxied_$2 = dreq_$1.proxied;
var tloader_$3 = new LzHTTPLoader(this, proxied_$2);
dreq_$1.loader = tloader_$3;
tloader_$3.loadSuccess = this.loadSuccess;
tloader_$3.loadError = this.loadError;
tloader_$3.loadTimeout = this.loadTimeout;
tloader_$3.setProxied(proxied_$2);
var secure_$4 = dreq_$1.secure;
if (secure_$4 == null) {
if (dreq_$1.src.substring(0, 5) == "https") {
secure_$4 = true
}};
tloader_$3.secure = secure_$4;
if (secure_$4) {
tloader_$3.baserequest = lz.Browser.getBaseURL(secure_$4, dreq_$1.secureport);
tloader_$3.secureport = dreq_$1.secureport
};
return tloader_$3
};
$lzsc$temp["displayName"] = "makeLoader";
return $lzsc$temp
})(), "abortLoadForRequest", (function  () {
var $lzsc$temp = function  (dreq_$1) {
dreq_$1.loader.abort()
};
$lzsc$temp["displayName"] = "abortLoadForRequest";
return $lzsc$temp
})(), "doRequest", (function  () {
var $lzsc$temp = function  (dreq_$1) {
var httpdreq_$2 = dreq_$1;
if (!httpdreq_$2.src) {
return
};
var proxied_$3 = httpdreq_$2.proxied;
var tloader_$4 = httpdreq_$2.loader;
if (tloader_$4 == null || httpdreq_$2.multirequest == true || httpdreq_$2.queuerequests == true) {
tloader_$4 = this.makeLoader(httpdreq_$2)
};
tloader_$4.dataRequest = httpdreq_$2;
tloader_$4.setQueueing(httpdreq_$2.queuerequests);
tloader_$4.setTimeout(httpdreq_$2.timeout);
tloader_$4.setOption("serverproxyargs", httpdreq_$2.serverproxyargs);
tloader_$4.setOption("cacheable", httpdreq_$2.cacheable == true);
tloader_$4.setOption("timeout", httpdreq_$2.timeout);
tloader_$4.setOption("trimwhitespace", httpdreq_$2.trimwhitespace == true);
tloader_$4.setOption("nsprefix", httpdreq_$2.nsprefix == true);
tloader_$4.setOption("sendheaders", httpdreq_$2.getresponseheaders == true);
tloader_$4.setOption("parsexml", httpdreq_$2.parsexml);
if (httpdreq_$2.clientcacheable != null) {
tloader_$4.setOption("ccache", httpdreq_$2.clientcacheable)
};
var headers_$5 = {};
var headerparams_$6 = httpdreq_$2.requestheaders;
if (headerparams_$6 != null) {
var headernames_$7 = headerparams_$6.getNames();
for (var i_$8 = 0;i_$8 < headernames_$7.length;i_$8++) {
var key_$9 = headernames_$7[i_$8];
var val_$10 = headerparams_$6.getValue(key_$9);
if (proxied_$3) {
headers_$5[key_$9] = val_$10
} else {
tloader_$4.setRequestHeader(key_$9, val_$10)
}}};
var qparams_$11 = httpdreq_$2.queryparams;
var hasquerydata_$12 = true;
var postbody_$13 = httpdreq_$2.postbody;
if (postbody_$13 == null && qparams_$11 != null) {
postbody_$13 = qparams_$11.serialize("=", "&", true)
} else {
hasquerydata_$12 = false
};
tloader_$4.setOption("hasquerydata", hasquerydata_$12);
var lzurl_$14 = new LzURL(httpdreq_$2.src);
if (httpdreq_$2.method == "GET") {
if (lzurl_$14.query == null) {
lzurl_$14.query = postbody_$13
} else {
if (postbody_$13 != null) {
lzurl_$14.query += "&" + postbody_$13
}};
postbody_$13 = null
};
var cachebreak_$15 = "__lzbc__=" + new Date().getTime();
if (!proxied_$3 && httpdreq_$2.method == "POST" && (postbody_$13 == null || postbody_$13 == "")) {
postbody_$13 = cachebreak_$15
};
var url_$16;
if (proxied_$3) {
url_$16 = tloader_$4.makeProxiedURL(httpdreq_$2.proxyurl, lzurl_$14.toString(), httpdreq_$2.method, "xmldata", headers_$5, postbody_$13);
var marker_$17 = url_$16.indexOf("?");
var uquery_$18 = url_$16.substring(marker_$17 + 1, url_$16.length);
var url_noquery_$19 = url_$16.substring(0, marker_$17);
url_$16 = url_noquery_$19 + "?" + cachebreak_$15;
postbody_$13 = uquery_$18
} else {
if (!httpdreq_$2.clientcacheable) {
if (httpdreq_$2.method == "GET") {
if (lzurl_$14.query == null) {
lzurl_$14.query = cachebreak_$15
} else {
lzurl_$14.query += "&" + cachebreak_$15
}}};
url_$16 = lzurl_$14.toString()
};
httpdreq_$2.loadstarttime = new Date().getTime();
httpdreq_$2.status = LzDataRequest.LOADING;
tloader_$4.open(proxied_$3 ? "POST" : httpdreq_$2.method, url_$16, null, null);
tloader_$4.send(postbody_$13)
};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "loadSuccess", (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {
var dreq_$3 = loader_$1.dataRequest;
dreq_$3.status = LzDataRequest.SUCCESS;
loader_$1.owner.loadResponse(dreq_$3, data_$2)
};
$lzsc$temp["displayName"] = "loadSuccess";
return $lzsc$temp
})(), "loadError", (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {
var dreq_$3 = loader_$1.dataRequest;
dreq_$3.status = LzDataRequest.ERROR;
loader_$1.owner.loadResponse(dreq_$3, data_$2)
};
$lzsc$temp["displayName"] = "loadError";
return $lzsc$temp
})(), "loadTimeout", (function  () {
var $lzsc$temp = function  (loader_$1, data_$2) {
var dreq_$3 = loader_$1.dataRequest;
dreq_$3.loadtime = new Date().getTime() - dreq_$3.loadstarttime;
dreq_$3.status = LzDataRequest.TIMEOUT;
dreq_$3.onstatus.sendEvent(dreq_$3)
};
$lzsc$temp["displayName"] = "loadTimeout";
return $lzsc$temp
})(), "setRequestError", (function  () {
var $lzsc$temp = function  (dreq_$1, msg_$2) {
dreq_$1.error = msg_$2;
dreq_$1.status = LzDataRequest.ERROR
};
$lzsc$temp["displayName"] = "setRequestError";
return $lzsc$temp
})(), "loadResponse", (function  () {
var $lzsc$temp = function  (dreq_$1, data_$2) {
dreq_$1.loadtime = new Date().getTime() - dreq_$1.loadstarttime;
dreq_$1.rawdata = dreq_$1.loader.getResponse();
if (data_$2 == null) {
this.setRequestError(dreq_$1, "client could not parse XML from server");
dreq_$1.onstatus.sendEvent(dreq_$1);
return
};
var proxied_$3 = dreq_$1.proxied;
if (!dreq_$1.parsexml) {
dreq_$1.onstatus.sendEvent(dreq_$1);
return
} else {
if (proxied_$3 && data_$2.childNodes[0].nodeName == "error") {
this.setRequestError(dreq_$1, data_$2.childNodes[0].attributes["msg"]);
dreq_$1.onstatus.sendEvent(dreq_$1);
return
}};
var headers_$4 = new (lz.Param)();
var content_$5 = null;
if (proxied_$3) {
var hos_$6 = data_$2.childNodes.length > 1 && data_$2.childNodes[1].childNodes ? data_$2.childNodes[1].childNodes : null;
if (hos_$6 != null) {
for (var i_$7 = 0;i_$7 < hos_$6.length;i_$7++) {
var h_$8 = hos_$6[i_$7];
if (h_$8.attributes) {
headers_$4.addValue(h_$8.attributes.name, h_$8.attributes.value)
}}};
if (data_$2.childNodes[0].childNodes) {
content_$5 = data_$2.childNodes[0].childNodes[0]
}} else {
var hos_$6 = dreq_$1.loader.getResponseHeaders();
if (hos_$6) {
headers_$4.addObject(hos_$6)
};
content_$5 = data_$2
};
dreq_$1.xmldata = content_$5;
dreq_$1.responseheaders = headers_$4;
dreq_$1.onstatus.sendEvent(dreq_$1)
};
$lzsc$temp["displayName"] = "loadResponse";
return $lzsc$temp
})()], null);
lz.HTTPDataProvider = LzHTTPDataProvider;
Class.make("LzDataset", [LzDataElementMixin, LzDataNodeMixin, LzNode], ["rawdata", null, "dataprovider", void 0, "multirequest", false, "dataRequest", null, "dataRequestClass", LzHTTPDataRequest, "dsloadDel", null, "errorstring", void 0, "reqOnInitDel", void 0, "secureport", void 0, "proxyurl", null, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "timeout", 60000, "$lzc$set_timeout", (function  () {
var $lzsc$temp = function  (t_$1) {
this.timeout = t_$1
};
$lzsc$temp["displayName"] = "$lzc$set_timeout";
return $lzsc$temp
})(), "postbody", null, "$lzc$set_postbody", (function  () {
var $lzsc$temp = function  (s_$1) {
this.postbody = s_$1
};
$lzsc$temp["displayName"] = "$lzc$set_postbody";
return $lzsc$temp
})(), "acceptencodings", false, "type", null, "params", null, "nsprefix", false, "getresponseheaders", false, "querytype", "GET", "$lzc$set_querytype", (function  () {
var $lzsc$temp = function  (reqtype_$1) {
this.querytype = reqtype_$1.toUpperCase()
};
$lzsc$temp["displayName"] = "$lzc$set_querytype";
return $lzsc$temp
})(), "trimwhitespace", false, "cacheable", false, "clientcacheable", false, "querystring", null, "src", null, "$lzc$set_src", (function  () {
var $lzsc$temp = function  (src_$1) {
this.src = src_$1;
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "$lzc$set_src";
return $lzsc$temp
})(), "autorequest", false, "$lzc$set_autorequest", (function  () {
var $lzsc$temp = function  (b_$1) {
this.autorequest = b_$1
};
$lzsc$temp["displayName"] = "$lzc$set_autorequest";
return $lzsc$temp
})(), "request", false, "$lzc$set_request", (function  () {
var $lzsc$temp = function  (b_$1) {
this.request = b_$1;
if (b_$1 && !this.isinited) {
this.reqOnInitDel = new LzDelegate(this, "doRequest", this, "oninit")
}};
$lzsc$temp["displayName"] = "$lzc$set_request";
return $lzsc$temp
})(), "headers", null, "proxied", null, "$lzc$set_proxied", (function  () {
var $lzsc$temp = function  (val_$1) {
var nval_$2 = {"true": true, "false": false, "null": null, inherit: null}[val_$1];
if (nval_$2 !== void 0) {
this.proxied = nval_$2
} else {
Debug.warn("%w.proxied must be one of 'inherit', 'true', or 'false', but was %w", this, val_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_proxied";
return $lzsc$temp
})(), "isProxied", (function  () {
var $lzsc$temp = function  () {
return this.proxied != null ? this.proxied : canvas.proxied
};
$lzsc$temp["displayName"] = "isProxied";
return $lzsc$temp
})(), "responseheaders", null, "queuerequests", false, "oncanvas", void 0, "$lzc$set_initialdata", (function  () {
var $lzsc$temp = function  (d_$1) {
if (d_$1 != null) {
var e_$2 = LzDataElement.stringToLzData(d_$1, this.trimwhitespace, this.nsprefix);
if (e_$2 != null) {
this.$lzc$set_data(e_$2.childNodes)
}}};
$lzsc$temp["displayName"] = "$lzc$set_initialdata";
return $lzsc$temp
})(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
args_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, args_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
this.nodeType = LzDataElement.DOCUMENT_NODE;
this.ownerDocument = this;
this.attributes = {};
this.childNodes = [];
this.queuerequests = false;
this.oncanvas = parent_$1 == canvas || parent_$1 == null;
if (!("proxyurl" in args_$2)) {
this.proxyurl = canvas.getProxyURL()
};
if (("timeout" in args_$2) && args_$2.timeout) {
this.timeout = args_$2.timeout
} else {
this.timeout = canvas.dataloadtimeout
};
if (("dataprovider" in args_$2) && args_$2.dataprovider) {
this.dataprovider = args_$2.dataprovider
} else {
this.dataprovider = canvas.defaultdataprovider
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_name", (function  () {
var $lzsc$temp = function  (name_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_name"] || this.nextMethod(arguments.callee, "$lzc$set_name")).call(this, name_$1);
if (name_$1 != null) {
this.nodeName = name_$1;
if (this.oncanvas) {
canvas[name_$1] = this
} else {
name_$1 = this.parent.getUID() + "." + name_$1
};
if (canvas.datasets[name_$1] != null) {
Debug.warn("A dataset already exists with the name '%s': %w", name_$1, canvas.datasets[name_$1])
};
canvas.datasets[name_$1] = this
}};
$lzsc$temp["displayName"] = "$lzc$set_name";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.$lzc$set_childNodes([]);
this.dataRequest = null;
if (this.dsloadDel) {
this.dsloadDel.unregisterAll()
};
var name_$1 = this.name;
if (this.oncanvas) {
if (canvas[name_$1] === this) {
delete canvas[name_$1]
}} else {
name_$1 = this.parent.getUID() + "." + name_$1
};
if (canvas.datasets[name_$1] === this) {
delete canvas.datasets[name_$1]
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "getErrorString", (function  () {
var $lzsc$temp = function  () {
return this.errorstring
};
$lzsc$temp["displayName"] = "getErrorString";
return $lzsc$temp
})(), "getLoadTime", (function  () {
var $lzsc$temp = function  () {
var dreq_$1 = this.dataRequest;
return dreq_$1 ? dreq_$1.loadtime : 0
};
$lzsc$temp["displayName"] = "getLoadTime";
return $lzsc$temp
})(), "getSrc", (function  () {
var $lzsc$temp = function  () {
return this.src
};
$lzsc$temp["displayName"] = "getSrc";
return $lzsc$temp
})(), "getQueryString", (function  () {
var $lzsc$temp = function  () {
if (typeof this.querystring == "undefined") {
return ""
} else {
return this.querystring
}};
$lzsc$temp["displayName"] = "getQueryString";
return $lzsc$temp
})(), "getParams", (function  () {
var $lzsc$temp = function  () {
if (this.params == null) {
this.params = new (lz.Param)()
};
return this.params
};
$lzsc$temp["displayName"] = "getParams";
return $lzsc$temp
})(), "$lzc$set_data", (function  () {
var $lzsc$temp = function  (data_$1) {
if (data_$1 == null) {
return
} else {
if (data_$1 instanceof Array) {
this.$lzc$set_childNodes(data_$1)
} else {
this.$lzc$set_childNodes([data_$1])
}};
this.data = data_$1;
if (this.ondata.ready) {
this.ondata.sendEvent(this)
}};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "gotError", (function  () {
var $lzsc$temp = function  (e_$1) {
this.errorstring = e_$1;
if (this.onerror.ready) {
this.onerror.sendEvent(this)
}};
$lzsc$temp["displayName"] = "gotError";
return $lzsc$temp
})(), "gotTimeout", (function  () {
var $lzsc$temp = function  () {
if (this.ontimeout.ready) {
this.ontimeout.sendEvent(this)
}};
$lzsc$temp["displayName"] = "gotTimeout";
return $lzsc$temp
})(), "getContext", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
switch (arguments.length) {
case 0:
chgpkg_$1 = null
};
return this
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "getDataset", (function  () {
var $lzsc$temp = function  () {
return this
};
$lzsc$temp["displayName"] = "getDataset";
return $lzsc$temp
})(), "getPointer", (function  () {
var $lzsc$temp = function  () {
var dp_$1 = new LzDatapointer(null);
dp_$1.p = this.getContext();
return dp_$1
};
$lzsc$temp["displayName"] = "getPointer";
return $lzsc$temp
})(), "setQueryString", (function  () {
var $lzsc$temp = function  (s_$1) {
this.params = null;
if (typeof s_$1 == "object") {
if (s_$1 instanceof lz.Param) {
this.querystring = s_$1.toString()
} else {
var p_$2 = new (lz.Param)();
for (var n_$3 in s_$1) {
p_$2.setValue(n_$3, s_$1[n_$3], true)
};
this.querystring = p_$2.toString();
p_$2.destroy()
}} else {
this.querystring = s_$1
};
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryString";
return $lzsc$temp
})(), "setQueryParam", (function  () {
var $lzsc$temp = function  (key_$1, val_$2) {
this.querystring = null;
if (!this.params) {
this.params = new (lz.Param)()
};
this.params.setValue(key_$1, val_$2);
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryParam";
return $lzsc$temp
})(), "setQueryParams", (function  () {
var $lzsc$temp = function  (obj_$1) {
this.querystring = null;
if (!this.params) {
this.params = new (lz.Param)()
};
if (obj_$1) {
this.params.addObject(obj_$1)
} else {
if (obj_$1 == null) {
this.params.remove()
}};
if (obj_$1 && this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryParams";
return $lzsc$temp
})(), "setSrc", (function  () {
var $lzsc$temp = function  (src_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_src(src_$1)
};
$lzsc$temp["displayName"] = "setSrc";
return $lzsc$temp
})(), "setProxyRequests", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_proxied(val_$1)
};
$lzsc$temp["displayName"] = "setProxyRequests";
return $lzsc$temp
})(), "setRequest", (function  () {
var $lzsc$temp = function  (b_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_request(b_$1)
};
$lzsc$temp["displayName"] = "setRequest";
return $lzsc$temp
})(), "setAutorequest", (function  () {
var $lzsc$temp = function  (b_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_autorequest(b_$1)
};
$lzsc$temp["displayName"] = "setAutorequest";
return $lzsc$temp
})(), "setPostBody", (function  () {
var $lzsc$temp = function  (str_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_postbody(str_$1)
};
$lzsc$temp["displayName"] = "setPostBody";
return $lzsc$temp
})(), "setQueryType", (function  () {
var $lzsc$temp = function  (reqtype_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_querytype(reqtype_$1)
};
$lzsc$temp["displayName"] = "setQueryType";
return $lzsc$temp
})(), "setInitialData", (function  () {
var $lzsc$temp = function  (d_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_initialdata(d_$1)
};
$lzsc$temp["displayName"] = "setInitialData";
return $lzsc$temp
})(), "abort", (function  () {
var $lzsc$temp = function  () {
var dreq_$1 = this.dataRequest;
if (dreq_$1) {
this.dataprovider.abortLoadForRequest(dreq_$1)
}};
$lzsc$temp["displayName"] = "abort";
return $lzsc$temp
})(), "doRequest", (function  () {
var $lzsc$temp = function  (ignore_$1) {
switch (arguments.length) {
case 0:
ignore_$1 = null
};
if (!this.src) {
return
};
if (this.multirequest || this.dataRequest == null || this.queuerequests) {
this.dataRequest = new (this.dataRequestClass)(this)
};
var dreq_$2 = this.dataRequest;
dreq_$2.src = this.src;
dreq_$2.timeout = this.timeout;
dreq_$2.status = LzDataRequest.READY;
dreq_$2.method = this.querytype;
dreq_$2.postbody = null;
if (this.querystring) {
dreq_$2.queryparams = new (lz.Param)();
dreq_$2.queryparams.addObject(lz.Param.parseQueryString(this.querystring))
} else {
dreq_$2.queryparams = this.params
};
if (this.querytype.toUpperCase() == "POST") {
dreq_$2.postbody = this.postbody;
if (dreq_$2.queryparams) {
var lzpostbody_$3 = dreq_$2.queryparams.getValue("lzpostbody");
if (lzpostbody_$3 != null) {
dreq_$2.queryparams.remove("lzpostbody");
dreq_$2.postbody = lzpostbody_$3
}}};
dreq_$2.proxied = this.isProxied();
dreq_$2.proxyurl = this.proxyurl;
dreq_$2.queuerequests = this.queuerequests;
dreq_$2.requestheaders = this.headers;
dreq_$2.getresponseheaders = this.getresponseheaders;
dreq_$2.secureport = this.secureport;
dreq_$2.cacheable = this.cacheable;
dreq_$2.clientcacheable = this.clientcacheable;
dreq_$2.trimwhitespace = this.trimwhitespace;
dreq_$2.nsprefix = this.nsprefix;
if (this.dsloadDel == null) {
this.dsloadDel = new LzDelegate(this, "handleDataResponse", dreq_$2, "onstatus")
} else {
this.dsloadDel.register(dreq_$2, "onstatus")
};
this.dataprovider.doRequest(dreq_$2)
};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "handleDataResponse", (function  () {
var $lzsc$temp = function  (datareq_$1) {
if (this.dsloadDel != null) {
this.dsloadDel.unregisterFrom(datareq_$1.onstatus)
};
this.rawdata = datareq_$1.rawdata;
this.errorstring = null;
if (datareq_$1.status == LzDataRequest.SUCCESS) {
if (this.responseheaders != null) {
this.responseheaders.destroy()
};
this.responseheaders = datareq_$1.responseheaders;
this.$lzc$set_data(datareq_$1.xmldata)
} else {
if (datareq_$1.status == LzDataRequest.ERROR) {
this.gotError(datareq_$1.error)
} else {
if (datareq_$1.status == LzDataRequest.TIMEOUT) {
this.gotTimeout()
}}}};
$lzsc$temp["displayName"] = "handleDataResponse";
return $lzsc$temp
})(), "setHeader", (function  () {
var $lzsc$temp = function  (k_$1, val_$2) {
if (!this.headers) {
this.headers = new (lz.Param)()
};
this.headers.setValue(k_$1, val_$2)
};
$lzsc$temp["displayName"] = "setHeader";
return $lzsc$temp
})(), "getRequestHeaderParams", (function  () {
var $lzsc$temp = function  () {
return this.headers
};
$lzsc$temp["displayName"] = "getRequestHeaderParams";
return $lzsc$temp
})(), "clearRequestHeaderParams", (function  () {
var $lzsc$temp = function  () {
if (this.headers) {
this.headers.remove()
}};
$lzsc$temp["displayName"] = "clearRequestHeaderParams";
return $lzsc$temp
})(), "getResponseHeader", (function  () {
var $lzsc$temp = function  (name_$1) {
var headers_$2 = this.responseheaders;
if (headers_$2) {
var val_$3 = headers_$2.getValues(name_$1);
if (val_$3 && val_$3.length == 1) {
return val_$3[0]
} else {
return val_$3
}};
return void 0
};
$lzsc$temp["displayName"] = "getResponseHeader";
return $lzsc$temp
})(), "getAllResponseHeaders", (function  () {
var $lzsc$temp = function  () {
return this.responseheaders
};
$lzsc$temp["displayName"] = "getAllResponseHeaders";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "LzDataset " + ":" + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["tagname", "dataset", "attributes", new LzInheritedHash(LzNode.attributes), "slashPat", "/", "queryStringToTable", (function  () {
var $lzsc$temp = function  (query_$1) {
var queries_$2 = {};
var parameters_$3 = query_$1.split("&");
for (var i_$4 = 0;i_$4 < parameters_$3.length;++i_$4) {
var key_$5 = parameters_$3[i_$4];
var value_$6 = "";
var n_$7 = key_$5.indexOf("=");
if (n_$7 > 0) {
value_$6 = unescape(key_$5.substring(n_$7 + 1));
key_$5 = key_$5.substring(0, n_$7)
};
if (key_$5 in queries_$2) {
var prev_$8 = queries_$2[key_$5];
if (prev_$8 instanceof Array) {
prev_$8.push(value_$6)
} else {
queries_$2[key_$5] = [prev_$8, value_$6]
}} else {
queries_$2[key_$5] = value_$6
}};
return queries_$2
};
$lzsc$temp["displayName"] = "queryStringToTable";
return $lzsc$temp
})()]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzDataset.attributes.name = "localdata"
}}};
$lzsc$temp["displayName"] = "data/LzDataset.lzs#89/1";
return $lzsc$temp
})()(LzDataset);
lz[LzDataset.tagname] = LzDataset;
Class.make("__LzHttpDatasetPoolClass", null, ["_uid", 0, "_p", [], "get", (function  () {
var $lzsc$temp = function  (dataDel_$1, errorDel_$2, timeoutDel_$3, acceptenc_$4) {
switch (arguments.length) {
case 0:
dataDel_$1 = null;;case 1:
errorDel_$2 = null;;case 2:
timeoutDel_$3 = null;;case 3:
acceptenc_$4 = false
};
var dset_$5;
if (this._p.length > 0) {
dset_$5 = this._p.pop()
} else {
dset_$5 = new LzDataset(null, {name: "LzHttpDatasetPool" + this._uid, type: "http", acceptencodings: acceptenc_$4});
this._uid++
};
if (dataDel_$1 != null) {
dataDel_$1.register(dset_$5, "ondata")
};
if (errorDel_$2 != null) {
errorDel_$2.register(dset_$5, "onerror")
};
if (timeoutDel_$3 != null) {
timeoutDel_$3.register(dset_$5, "ontimeout")
};
return dset_$5
};
$lzsc$temp["displayName"] = "get";
return $lzsc$temp
})(), "recycle", (function  () {
var $lzsc$temp = function  (dset_$1) {
dset_$1.setQueryParams(null);
dset_$1.$lzc$set_postbody(null);
dset_$1.clearRequestHeaderParams();
dset_$1.ondata.clearDelegates();
dset_$1.ontimeout.clearDelegates();
dset_$1.onerror.clearDelegates();
dset_$1.$lzc$set_data([]);
this._p.push(dset_$1)
};
$lzsc$temp["displayName"] = "recycle";
return $lzsc$temp
})()], null);
var LzHttpDatasetPool = new __LzHttpDatasetPoolClass();
Class.make("LzDatapointer", LzNode, ["$lzc$set_xpath", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setXPath(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_xpath";
return $lzsc$temp
})(), "$lzc$set_context", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setDataContext(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_context";
return $lzsc$temp
})(), "$lzc$set_pointer", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setPointer(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_pointer";
return $lzsc$temp
})(), "$lzc$set_p", (function  () {
var $lzsc$temp = function  (v_$1) {
this.setPointer(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_p";
return $lzsc$temp
})(), "p", null, "context", null, "__LZtracking", null, "__LZtrackDel", null, "xpath", null, "parsedPath", null, "__LZlastdotdot", null, "__LZspecialDotDot", false, "__LZdotdotCheckDel", null, "errorDel", null, "timeoutDel", null, "rerunxpath", false, "onp", LzDeclaredEvent, "onDocumentChange", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "onrerunxpath", LzDeclaredEvent, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
parent_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "gotError", (function  () {
var $lzsc$temp = function  (ds_$1) {
if (this.onerror.ready) {
this.onerror.sendEvent(ds_$1)
}};
$lzsc$temp["displayName"] = "gotError";
return $lzsc$temp
})(), "gotTimeout", (function  () {
var $lzsc$temp = function  (ds_$1) {
if (this.ontimeout.ready) {
this.ontimeout.sendEvent(ds_$1)
}};
$lzsc$temp["displayName"] = "gotTimeout";
return $lzsc$temp
})(), "xpathQuery", (function  () {
var $lzsc$temp = function  (p_$1) {
var pp_$2 = this.parsePath(p_$1);
var ppcontext_$3 = pp_$2.getContext(this);
var nodes_$4 = this.__LZgetNodes(pp_$2, ppcontext_$3 ? ppcontext_$3 : this.p);
if (nodes_$4 == null) {
return null
};
if (pp_$2.aggOperator != null) {
if (pp_$2.aggOperator == "last") {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$4) : nodes_$4 instanceof Array) {
return nodes_$4.length
} else {
if (!ppcontext_$3 && nodes_$4 === this.p) {
if (pp_$2.selectors && pp_$2.selectors.length > 0) {
var sel_$5 = pp_$2.selectors;
var i_$6 = 0;
while (sel_$5[i_$6] == "." && i_$6 < sel_$5.length) {
++i_$6
};
return i_$6 != sel_$5.length ? 1 : this.__LZgetLast()
} else {
return this.__LZgetLast()
}} else {
return 1
}}} else {
if (pp_$2.aggOperator == "position") {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$4) : nodes_$4 instanceof Array) {
var rarr_$7 = [];
for (var i_$6 = 0;i_$6 < nodes_$4.length;i_$6++) {
rarr_$7.push(i_$6 + 1)
};
return rarr_$7
} else {
if (!ppcontext_$3 && nodes_$4 === this.p) {
if (pp_$2.selectors && pp_$2.selectors.length > 0) {
var sel_$5 = pp_$2.selectors;
var i_$6 = 0;
while (sel_$5[i_$6] == "." && i_$6 < sel_$5.length) {
++i_$6
};
return i_$6 != sel_$5.length ? 1 : this.__LZgetPosition()
} else {
return this.__LZgetPosition()
}} else {
return 1
}}}}} else {
if (pp_$2.operator != null) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$4) : nodes_$4 instanceof Array) {
var oarr_$8 = [];
for (var i_$6 = 0;i_$6 < nodes_$4.length;i_$6++) {
oarr_$8.push(this.__LZprocessOperator(nodes_$4[i_$6], pp_$2))
};
return oarr_$8
} else {
return this.__LZprocessOperator(nodes_$4, pp_$2)
}} else {
return nodes_$4
}}};
$lzsc$temp["displayName"] = "xpathQuery";
return $lzsc$temp
})(), "$lzc$xpathQuery_dependencies", (function  () {
var $lzsc$temp = function  (who_$1, self_$2, p_$3) {
if (this["parsePath"]) {
var pp_$4 = this.parsePath(p_$3);
return [pp_$4.hasDotDot ? self_$2.context.getContext().ownerDocument : self_$2, "DocumentChange"]
} else {
return [self_$2, "DocumentChange"]
}};
$lzsc$temp["displayName"] = "$lzc$xpathQuery_dependencies";
return $lzsc$temp
})(), "setPointer", (function  () {
var $lzsc$temp = function  (p_$1) {
this.setXPath(null);
if (p_$1 != null) {
this.setDataContext(p_$1.ownerDocument)
} else {
this.__LZsetTracking(null)
};
var dc_$2 = this.data != p_$1;
var pc_$3 = this.p != p_$1;
this.p = p_$1;
this.data = p_$1;
this.__LZsendUpdate(dc_$2, pc_$3);
return p_$1 != null
};
$lzsc$temp["displayName"] = "setPointer";
return $lzsc$temp
})(), "getDataset", (function  () {
var $lzsc$temp = function  () {
if (this.p == null) {
if (this.context === this) {
return null
} else {
return this.context.getDataset()
}} else {
return this.p.ownerDocument
}};
$lzsc$temp["displayName"] = "getDataset";
return $lzsc$temp
})(), "setXPath", (function  () {
var $lzsc$temp = function  (p_$1) {
if (!p_$1) {
this.xpath = null;
this.parsedPath = null;
if (this.p) {
this.__LZsetTracking(this.p.ownerDocument)
};
return false
};
this.xpath = p_$1;
this.parsedPath = this.parsePath(p_$1);
var ppcontext_$2 = this.parsedPath.getContext(this);
if (this.rerunxpath && this.parsedPath.hasDotDot && !ppcontext_$2) {
this.__LZspecialDotDot = true
} else {
if (this.__LZdotdotCheckDel) {
this.__LZdotdotCheckDel.unregisterAll()
}};
this.setDataContext(ppcontext_$2);
return this.runXPath()
};
$lzsc$temp["displayName"] = "setXPath";
return $lzsc$temp
})(), "runXPath", (function  () {
var $lzsc$temp = function  () {
if (!this.parsedPath) {
return false
};
var newc_$1 = null;
if (this.context && ((LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(this.context) : this.context instanceof LzDatapointer) || (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(this.context) : this.context instanceof LzDataset) || (AnonDatasetGenerator["$lzsc$isa"] ? AnonDatasetGenerator.$lzsc$isa(this.context) : this.context instanceof AnonDatasetGenerator))) {
newc_$1 = this.context.getContext()
};
if (newc_$1) {
var n_$2 = this.__LZgetNodes(this.parsedPath, newc_$1, 0)
} else {
var n_$2 = null
};
if (n_$2 == null) {
this.__LZHandleNoNodes();
return false
} else {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(n_$2) : n_$2 instanceof Array) {
this.__LZHandleMultiNodes(n_$2);
return false
} else {
this.__LZHandleSingleNode(n_$2);
return true
}}};
$lzsc$temp["displayName"] = "runXPath";
return $lzsc$temp
})(), "__LZsetupDotDot", (function  () {
var $lzsc$temp = function  (p_$1) {
if (this.__LZlastdotdot != p_$1.ownerDocument) {
if (this.__LZdotdotCheckDel == null) {
this.__LZdotdotCheckDel = new LzDelegate(this, "__LZcheckDotDot")
} else {
this.__LZdotdotCheckDel.unregisterAll()
};
this.__LZlastdotdot = p_$1.ownerDocument;
this.__LZdotdotCheckDel.register(this.__LZlastdotdot, "onDocumentChange")
}};
$lzsc$temp["displayName"] = "__LZsetupDotDot";
return $lzsc$temp
})(), "__LZHandleSingleNode", (function  () {
var $lzsc$temp = function  (n_$1) {
if (this.__LZspecialDotDot) {
this.__LZsetupDotDot(n_$1)
};
this.__LZupdateLocked = true;
this.__LZpchanged = n_$1 != this.p;
this.p = n_$1;
this.__LZsetData();
this.__LZupdateLocked = false;
this.__LZsendUpdate()
};
$lzsc$temp["displayName"] = "__LZHandleSingleNode";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function  () {
var $lzsc$temp = function  () {
var pc_$1 = this.p != null;
var dc_$2 = this.data != null;
this.p = null;
this.data = null;
this.__LZsendUpdate(dc_$2, pc_$1)
};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function  () {
var $lzsc$temp = function  (n_$1) {
Debug.error("%w matched %d nodes", this, n_$1.length);
this.__LZHandleNoNodes();
return null
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "__LZsetData", (function  () {
var $lzsc$temp = function  () {
if (this.parsedPath && this.parsedPath.aggOperator != null) {
if (this.parsedPath.aggOperator == "last") {
this.data = this.__LZgetLast();
this.__LZsendUpdate(true)
} else {
if (this.parsedPath.aggOperator == "position") {
this.data = this.__LZgetPosition();
this.__LZsendUpdate(true)
}}} else {
if (this.parsedPath && this.parsedPath.operator != null) {
this.__LZsimpleOperatorUpdate()
} else {
if (this.data != this.p) {
this.data = this.p;
this.__LZsendUpdate(true)
}}}};
$lzsc$temp["displayName"] = "__LZsetData";
return $lzsc$temp
})(), "__LZgetLast", (function  () {
var $lzsc$temp = function  () {
var ctx_$1 = this.context;
if (ctx_$1 == null || ctx_$1 === this || !(LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(ctx_$1) : ctx_$1 instanceof LzDatapointer)) {
return 1
} else {
return ctx_$1.__LZgetLast() || 1
}};
$lzsc$temp["displayName"] = "__LZgetLast";
return $lzsc$temp
})(), "__LZgetPosition", (function  () {
var $lzsc$temp = function  () {
var ctx_$1 = this.context;
if (ctx_$1 == null || ctx_$1 === this || !(LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(ctx_$1) : ctx_$1 instanceof LzDatapointer)) {
return 1
} else {
return ctx_$1.__LZgetPosition() || 1
}};
$lzsc$temp["displayName"] = "__LZgetPosition";
return $lzsc$temp
})(), "__LZupdateLocked", false, "__LZpchanged", false, "__LZdchanged", false, "__LZsendUpdate", (function  () {
var $lzsc$temp = function  (upd_$1, upp_$2) {
switch (arguments.length) {
case 0:
upd_$1 = false;;case 1:
upp_$2 = false
};
this.__LZdchanged = upd_$1 || this.__LZdchanged;
this.__LZpchanged = upp_$2 || this.__LZpchanged;
if (this.__LZupdateLocked) {
return false
};
if (this.__LZdchanged) {
if (this.ondata.ready) {
this.ondata.sendEvent(this.data)
};
this.__LZdchanged = false
};
if (this.__LZpchanged) {
if (this.onp.ready) {
this.onp.sendEvent(this.p)
};
this.__LZpchanged = false;
if (this.onDocumentChange.ready) {
this.onDocumentChange.sendEvent({who: this.p, type: 2, what: "context"})
}};
return true
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "isValid", (function  () {
var $lzsc$temp = function  () {
return this.p != null
};
$lzsc$temp["displayName"] = "isValid";
return $lzsc$temp
})(), "__LZsimpleOperatorUpdate", (function  () {
var $lzsc$temp = function  () {
var ndat_$1 = this.p != null ? this.__LZprocessOperator(this.p, this.parsedPath) : void 0;
var dchanged_$2 = false;
if (this.data != ndat_$1 || this.parsedPath.operator == "attributes") {
this.data = ndat_$1;
dchanged_$2 = true
};
this.__LZsendUpdate(dchanged_$2)
};
$lzsc$temp["displayName"] = "__LZsimpleOperatorUpdate";
return $lzsc$temp
})(), "parsePath", (function  () {
var $lzsc$temp = function  (pa_$1) {
if (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(pa_$1) : pa_$1 instanceof LzDatapath) {
var xp_$2 = pa_$1.xpath
} else {
var xp_$2 = pa_$1
};
var ppc_$3 = LzDatapointer.ppcache;
var q_$4 = ppc_$3[xp_$2];
if (q_$4 == null) {
q_$4 = new LzParsedPath(xp_$2, this);
ppc_$3[xp_$2] = q_$4
};
return q_$4
};
$lzsc$temp["displayName"] = "parsePath";
return $lzsc$temp
})(), "getLocalDataContext", (function  () {
var $lzsc$temp = function  (pp_$1) {
var n_$2 = this.parent;
if (pp_$1) {
var a_$3 = pp_$1;
for (var i_$4 = 0;i_$4 < a_$3.length && n_$2 != null;i_$4++) {
n_$2 = n_$2[a_$3[i_$4]]
};
if (n_$2 != null && !(LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$2) : n_$2 instanceof LzDataset) && (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$2["localdata"]) : n_$2["localdata"] instanceof LzDataset)) {
n_$2 = n_$2["localdata"];
return n_$2
}};
if (n_$2 != null && (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$2) : n_$2 instanceof LzDataset)) {
return n_$2
} else {
Debug.warn('local dataset "%w" not found in %w', pp_$1, this.parent);
return null
}};
$lzsc$temp["displayName"] = "getLocalDataContext";
return $lzsc$temp
})(), "__LZgetNodes", (function  () {
var $lzsc$temp = function  (pathobj_$1, p_$2, startpoint_$3) {
switch (arguments.length) {
case 2:
startpoint_$3 = 0
};
if (p_$2 == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (pathobj_$1.selectors != null) {
var pathlen_$4 = pathobj_$1.selectors.length;
for (var i_$5 = startpoint_$3;i_$5 < pathlen_$4;i_$5++) {
var cp_$6 = pathobj_$1.selectors[i_$5];
var specialop_$7 = LzDatapointer.pathSymbols[cp_$6] || 0;
var posnext_$8 = pathobj_$1.selectors[i_$5 + 1];
if (posnext_$8 && !(String["$lzsc$isa"] ? String.$lzsc$isa(posnext_$8) : posnext_$8 instanceof String) && posnext_$8["pred"] == "range") {
var range_$9 = pathobj_$1.selectors[++i_$5]
} else {
var range_$9 = null
};
var np_$10 = null;
if ((Object["$lzsc$isa"] ? Object.$lzsc$isa(cp_$6) : cp_$6 instanceof Object) && ("pred" in cp_$6) && null != cp_$6.pred) {
if (cp_$6.pred == "hasattr") {
p_$2 = p_$2.hasAttr(cp_$6.attr) ? p_$2 : null
} else {
if (cp_$6.pred == "attrval") {
if (p_$2.attributes != null) {
p_$2 = p_$2.attributes[cp_$6.attr] == cp_$6.val ? p_$2 : null
} else {
p_$2 = null
}}}} else {
if (specialop_$7 == 0) {
np_$10 = this.nodeByName(cp_$6, range_$9, p_$2)
} else {
if (specialop_$7 == 1) {
p_$2 = p_$2.ownerDocument
} else {
if (specialop_$7 == 2) {
p_$2 = p_$2.parentNode
} else {
if (specialop_$7 == 3) {
np_$10 = [];
if (p_$2.childNodes) {
var cnodes_$11 = p_$2.childNodes;
var len_$12 = cnodes_$11.length;
var rleft_$13 = range_$9 != null ? range_$9[0] : 0;
var rright_$14 = range_$9 != null ? range_$9[1] : len_$12;
var cnt_$15 = 0;
for (var j_$16 = 0;j_$16 < len_$12;j_$16++) {
var cn_$17 = cnodes_$11[j_$16];
if (cn_$17.nodeType == LzDataElement.ELEMENT_NODE) {
cnt_$15++;
if (cnt_$15 >= rleft_$13) {
np_$10.push(cn_$17)
};
if (cnt_$15 == rright_$14) {
break
}}}}}}}}};
if (np_$10 != null) {
if (np_$10.length > 1) {
if (i_$5 == pathlen_$4 - 1) {
return np_$10
};
var rval_$18 = [];
for (var j_$16 = 0;j_$16 < np_$10.length;j_$16++) {
var r_$19 = this.__LZgetNodes(pathobj_$1, np_$10[j_$16], i_$5 + 1);
if (r_$19 != null) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(r_$19) : r_$19 instanceof Array) {
for (var n_$20 = 0;n_$20 < r_$19.length;n_$20++) {
rval_$18.push(r_$19[n_$20])
}} else {
rval_$18.push(r_$19)
}}};
if (rval_$18.length == 0) {
return null
} else {
if (rval_$18.length == 1) {
return rval_$18[0]
} else {
return rval_$18
}}} else {
p_$2 = np_$10[0]
}};
if (p_$2 == null) {
return null
}}};
return p_$2
};
$lzsc$temp["displayName"] = "__LZgetNodes";
return $lzsc$temp
})(), "getContext", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
switch (arguments.length) {
case 0:
chgpkg_$1 = null
};
return this.p
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "nodeByName", (function  () {
var $lzsc$temp = function  (name_$1, range_$2, p_$3) {
if (!p_$3) {
p_$3 = this.p;
if (!this.p) {
return null
}};
var o_$4 = [];
if (p_$3.childNodes != null) {
var cnodes_$5 = p_$3.childNodes;
var len_$6 = cnodes_$5.length;
var rleft_$7 = range_$2 != null ? range_$2[0] : 0;
var rright_$8 = range_$2 != null ? range_$2[1] : len_$6;
var cnt_$9 = 0;
for (var i_$10 = 0;i_$10 < len_$6;i_$10++) {
var cn_$11 = cnodes_$5[i_$10];
if (cn_$11.nodeName == name_$1) {
cnt_$9++;
if (cnt_$9 >= rleft_$7) {
o_$4.push(cn_$11)
};
if (cnt_$9 == rright_$8) {
break
}}}};
return o_$4
};
$lzsc$temp["displayName"] = "nodeByName";
return $lzsc$temp
})(), "$lzc$set_rerunxpath", (function  () {
var $lzsc$temp = function  (rrx_$1) {
this.rerunxpath = rrx_$1;
if (this.onrerunxpath.ready) {
this.onrerunxpath.sendEvent(rrx_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_rerunxpath";
return $lzsc$temp
})(), "dupePointer", (function  () {
var $lzsc$temp = function  () {
var dp_$1 = new LzDatapointer();
dp_$1.setFromPointer(this);
return dp_$1
};
$lzsc$temp["displayName"] = "dupePointer";
return $lzsc$temp
})(), "__LZdoSelect", (function  () {
var $lzsc$temp = function  (selector_$1, amnt_$2) {
var np_$3 = this.p;
for (;np_$3 != null && amnt_$2 > 0;amnt_$2--) {
if (np_$3.nodeType == LzDataNode.TEXT_NODE) {
if (selector_$1 == "getFirstChild") {
break
}};
np_$3 = np_$3[selector_$1]()
};
if (np_$3 != null && amnt_$2 == 0) {
this.setPointer(np_$3);
return true
};
return false
};
$lzsc$temp["displayName"] = "__LZdoSelect";
return $lzsc$temp
})(), "selectNext", (function  () {
var $lzsc$temp = function  (amnt_$1) {
switch (arguments.length) {
case 0:
amnt_$1 = null
};
return this.__LZdoSelect("getNextSibling", amnt_$1 ? amnt_$1 : 1)
};
$lzsc$temp["displayName"] = "selectNext";
return $lzsc$temp
})(), "selectPrev", (function  () {
var $lzsc$temp = function  (amnt_$1) {
switch (arguments.length) {
case 0:
amnt_$1 = null
};
return this.__LZdoSelect("getPreviousSibling", amnt_$1 ? amnt_$1 : 1)
};
$lzsc$temp["displayName"] = "selectPrev";
return $lzsc$temp
})(), "selectChild", (function  () {
var $lzsc$temp = function  (amnt_$1) {
switch (arguments.length) {
case 0:
amnt_$1 = null
};
return this.__LZdoSelect("getFirstChild", amnt_$1 ? amnt_$1 : 1)
};
$lzsc$temp["displayName"] = "selectChild";
return $lzsc$temp
})(), "selectParent", (function  () {
var $lzsc$temp = function  (amnt_$1) {
switch (arguments.length) {
case 0:
amnt_$1 = null
};
return this.__LZdoSelect("getParent", amnt_$1 ? amnt_$1 : 1)
};
$lzsc$temp["displayName"] = "selectParent";
return $lzsc$temp
})(), "selectNextParent", (function  () {
var $lzsc$temp = function  () {
var op_$1 = this.p;
if (this.selectParent() && this.selectNext()) {
return true
} else {
this.setPointer(op_$1);
return false
}};
$lzsc$temp["displayName"] = "selectNextParent";
return $lzsc$temp
})(), "getNodeOffset", (function  () {
var $lzsc$temp = function  () {
Debug.info("%w.%s is deprecated.  Use XPath `position()` operator or LzDatapointer.getXPathIndex() instead.", this, arguments.callee);
return this.getXPathIndex()
};
$lzsc$temp["displayName"] = "getNodeOffset";
return $lzsc$temp
})(), "getXPathIndex", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return 0
};
return this.p.getOffset() + 1
};
$lzsc$temp["displayName"] = "getXPathIndex";
return $lzsc$temp
})(), "getNodeType", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
return this.p.nodeType
};
$lzsc$temp["displayName"] = "getNodeType";
return $lzsc$temp
})(), "getNodeName", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
return this.p.nodeName
};
$lzsc$temp["displayName"] = "getNodeName";
return $lzsc$temp
})(), "setNodeName", (function  () {
var $lzsc$temp = function  (name_$1) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.$lzc$set_nodeName(name_$1)
}};
$lzsc$temp["displayName"] = "setNodeName";
return $lzsc$temp
})(), "getNodeAttributes", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
return this.p.attributes
};
return null
};
$lzsc$temp["displayName"] = "getNodeAttributes";
return $lzsc$temp
})(), "getNodeAttribute", (function  () {
var $lzsc$temp = function  (name_$1) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
return this.p.attributes[name_$1]
};
return null
};
$lzsc$temp["displayName"] = "getNodeAttribute";
return $lzsc$temp
})(), "setNodeAttribute", (function  () {
var $lzsc$temp = function  (name_$1, val_$2) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.setAttr(name_$1, val_$2)
}};
$lzsc$temp["displayName"] = "setNodeAttribute";
return $lzsc$temp
})(), "deleteNodeAttribute", (function  () {
var $lzsc$temp = function  (name_$1) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.removeAttr(name_$1)
}};
$lzsc$temp["displayName"] = "deleteNodeAttribute";
return $lzsc$temp
})(), "getNodeText", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
return this.p.__LZgetText()
};
return null
};
$lzsc$temp["displayName"] = "getNodeText";
return $lzsc$temp
})(), "setNodeText", (function  () {
var $lzsc$temp = function  (val_$1) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
var foundit_$2 = false;
var cnodes_$3 = this.p.childNodes;
for (var i_$4 = 0;i_$4 < cnodes_$3.length;i_$4++) {
var cn_$5 = cnodes_$3[i_$4];
if (cn_$5.nodeType == LzDataElement.TEXT_NODE) {
cn_$5.$lzc$set_data(val_$1);
foundit_$2 = true;
break
}};
if (!foundit_$2) {
this.p.appendChild(new LzDataText(val_$1))
}}};
$lzsc$temp["displayName"] = "setNodeText";
return $lzsc$temp
})(), "getNodeCount", (function  () {
var $lzsc$temp = function  () {
if (!this.p || this.p.nodeType == LzDataElement.TEXT_NODE) {
return 0
};
return this.p.childNodes.length || 0
};
$lzsc$temp["displayName"] = "getNodeCount";
return $lzsc$temp
})(), "addNode", (function  () {
var $lzsc$temp = function  (name_$1, text_$2, attrs_$3) {
switch (arguments.length) {
case 1:
text_$2 = null;;case 2:
attrs_$3 = null
};
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
var nn_$4 = new LzDataElement(name_$1, attrs_$3);
if (text_$2 != null) {
nn_$4.appendChild(new LzDataText(text_$2))
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.appendChild(nn_$4)
};
return nn_$4
};
$lzsc$temp["displayName"] = "addNode";
return $lzsc$temp
})(), "deleteNode", (function  () {
var $lzsc$temp = function  () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
var op_$1 = this.p;
if (!this.rerunxpath) {
if (!(this.selectNext() || this.selectPrev())) {
this.__LZHandleNoNodes()
}};
op_$1.parentNode.removeChild(op_$1);
return op_$1
};
$lzsc$temp["displayName"] = "deleteNode";
return $lzsc$temp
})(), "sendDataChange", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
this.getDataset().sendDataChange(chgpkg_$1)
};
$lzsc$temp["displayName"] = "sendDataChange";
return $lzsc$temp
})(), "comparePointer", (function  () {
var $lzsc$temp = function  (ptr_$1) {
return this.p == ptr_$1.p
};
$lzsc$temp["displayName"] = "comparePointer";
return $lzsc$temp
})(), "addNodeFromPointer", (function  () {
var $lzsc$temp = function  (dp_$1) {
if (!dp_$1.p) {
return null
};
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
var n_$2 = dp_$1.p.cloneNode(true);
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.appendChild(n_$2)
};
return new LzDatapointer(null, {pointer: n_$2})
};
$lzsc$temp["displayName"] = "addNodeFromPointer";
return $lzsc$temp
})(), "setFromPointer", (function  () {
var $lzsc$temp = function  (dp_$1) {
this.setPointer(dp_$1.p)
};
$lzsc$temp["displayName"] = "setFromPointer";
return $lzsc$temp
})(), "__LZprocessOperator", (function  () {
var $lzsc$temp = function  (p_$1, pp_$2) {
if (p_$1 == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
var op_$3 = pp_$2.operator;
switch (op_$3) {
case "serialize":
return p_$1.serialize();;case "text":
return p_$1.nodeType != LzDataElement.TEXT_NODE ? p_$1.__LZgetText() : void 0;;case "name":
return p_$1.nodeName;;default:
if (pp_$2.hasAttrOper) {
if (p_$1.nodeType != LzDataElement.TEXT_NODE && p_$1["attributes"]) {
if (op_$3 == "attributes") {
return p_$1.attributes
} else {
return p_$1.attributes[op_$3.substr(11)]
}} else {
return
}} else {
Debug.error("Unknown operator '%s' in '%s'", op_$3, arguments.callee)
}
}};
$lzsc$temp["displayName"] = "__LZprocessOperator";
return $lzsc$temp
})(), "makeRootNode", (function  () {
var $lzsc$temp = function  () {
return new LzDataElement("root")
};
$lzsc$temp["displayName"] = "makeRootNode";
return $lzsc$temp
})(), "finishRootNode", (function  () {
var $lzsc$temp = function  (n_$1) {
return n_$1.childNodes[0]
};
$lzsc$temp["displayName"] = "finishRootNode";
return $lzsc$temp
})(), "makeElementNode", (function  () {
var $lzsc$temp = function  (attrs_$1, name_$2, par_$3) {
var tn_$4 = new LzDataElement(name_$2, attrs_$1);
par_$3.appendChild(tn_$4);
return tn_$4
};
$lzsc$temp["displayName"] = "makeElementNode";
return $lzsc$temp
})(), "makeTextNode", (function  () {
var $lzsc$temp = function  (text_$1, par_$2) {
var tn_$3 = new LzDataText(text_$1);
par_$2.appendChild(tn_$3);
return tn_$3
};
$lzsc$temp["displayName"] = "makeTextNode";
return $lzsc$temp
})(), "serialize", (function  () {
var $lzsc$temp = function  () {
if (this.p == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
return this.p.serialize()
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "setDataContext", (function  () {
var $lzsc$temp = function  (dc_$1, ignore_$2) {
switch (arguments.length) {
case 1:
ignore_$2 = false
};
if (dc_$1 == null) {
this.context = this;
if (this.p) {
this.__LZsetTracking(this.p.ownerDocument)
}} else {
if (this.context != dc_$1) {
this.context = dc_$1;
if (this.errorDel != null) {
this.errorDel.unregisterAll();
this.timeoutDel.unregisterAll()
};
this.__LZsetTracking(dc_$1);
var hasxpath_$3 = this.xpath != null;
if (hasxpath_$3) {
if (this.errorDel == null) {
this.errorDel = new LzDelegate(this, "gotError");
this.timeoutDel = new LzDelegate(this, "gotTimeout")
};
this.errorDel.register(dc_$1, "onerror");
this.timeoutDel.register(dc_$1, "ontimeout")
}}}};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "__LZcheckChange", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
if (!this.rerunxpath) {
if (!this.p || chgpkg_$1.who == this.context) {
this.runXPath()
} else {
if (this.__LZneedsOpUpdate(chgpkg_$1)) {
this.__LZsimpleOperatorUpdate()
}};
return false
} else {
if (chgpkg_$1.type == 2 || (chgpkg_$1.type == 0 || chgpkg_$1.type == 1 && this.parsedPath && this.parsedPath.hasOpSelector) && (this.parsedPath && this.parsedPath.hasDotDot || this.p == null || this.p.childOfNode(chgpkg_$1.who))) {
this.runXPath();
return true
} else {
if (this.__LZneedsOpUpdate(chgpkg_$1)) {
this.__LZsimpleOperatorUpdate()
}};
return false
}};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZneedsOpUpdate", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
switch (arguments.length) {
case 0:
chgpkg_$1 = null
};
var ppath_$2 = this.parsedPath;
if (ppath_$2 != null && ppath_$2.operator != null) {
var who_$3 = chgpkg_$1.who;
var type_$4 = chgpkg_$1.type;
if (ppath_$2.operator != "text") {
return type_$4 == 1 && who_$3 == this.p
} else {
return type_$4 == 0 && who_$3 == this.p || who_$3.parentNode == this.p && who_$3.nodeType == LzDataElement.TEXT_NODE
}} else {
return false
}};
$lzsc$temp["displayName"] = "__LZneedsOpUpdate";
return $lzsc$temp
})(), "__LZcheckDotDot", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
var who_$2 = chgpkg_$1.who;
var type_$3 = chgpkg_$1.type;
if ((type_$3 == 0 || type_$3 == 1 && this.parsedPath.hasOpSelector) && this.context.getContext().childOfNode(who_$2)) {
this.runXPath()
}};
$lzsc$temp["displayName"] = "__LZcheckDotDot";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.__LZsetTracking(null);
if (this.errorDel) {
this.errorDel.unregisterAll()
};
if (this.timeoutDel) {
this.timeoutDel.unregisterAll()
};
if (this.__LZdotdotCheckDel) {
this.__LZdotdotCheckDel.unregisterAll()
};
this.p = null;
this.data = null;
this.__LZlastdotdot = null;
this.context = null;
this.__LZtracking = null;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).apply(this, arguments)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "__LZsetTracking", (function  () {
var $lzsc$temp = function  (who_$1, force_$2, needscheck_$3) {
switch (arguments.length) {
case 1:
force_$2 = false;;case 2:
needscheck_$3 = false
};
var tracking_$4 = this.__LZtracking;
var trackDel_$5 = this.__LZtrackDel;
if (who_$1) {
if (tracking_$4 != null && tracking_$4.length > 1) {
Debug.error("%w.__LZtracking is %w, expecting an array of length 1", this, this.__LZtracking)
};
if (tracking_$4 != null && tracking_$4.length == 1 && tracking_$4[0] === who_$1) {
return
};
if (trackDel_$5) {
trackDel_$5.unregisterAll()
};
var hasxpath_$6 = force_$2 || this.xpath;
if (hasxpath_$6) {
if (!trackDel_$5) {
this.__LZtrackDel = trackDel_$5 = new LzDelegate(this, "__LZcheckChange")
};
this.__LZtracking = tracking_$4 = [who_$1];
trackDel_$5.register(who_$1, "onDocumentChange")
}} else {
this.__LZtracking = [];
if (trackDel_$5) {
this.__LZtrackDel.unregisterAll()
}}};
$lzsc$temp["displayName"] = "__LZsetTracking";
return $lzsc$temp
})()], ["tagname", "datapointer", "attributes", {ignoreplacement: true}, "ppcache", {}, "pathSymbols", {"/": 1, "..": 2, "*": 3, ".": 4}]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzDatapointer.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
if (this.p) {
return this.p._dbg_name()
};
return null
};
$lzsc$temp["displayName"] = "LzDatapointer.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "data/LzDatapointer.lzs#76/1";
return $lzsc$temp
})()(LzDatapointer);
lz[LzDatapointer.tagname] = LzDatapointer;
Class.make("LzParam", LzEventable, ["d", null, "delimiter", "&", "$lzc$set_delimiter", (function  () {
var $lzsc$temp = function  (d_$1) {
this.setDelimiter(d_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_delimiter";
return $lzsc$temp
})(), "separator", "=", "$lzc$set_separator", (function  () {
var $lzsc$temp = function  (s_$1) {
this.setSeparator(s_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_separator";
return $lzsc$temp
})(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.d = {}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "parseQueryString", (function  () {
var $lzsc$temp = function  (query_$1) {
Debug.info(this, ".parseQueryString is deprecated. Use lz.Param.parseQueryString instead");
return lz.Param.parseQueryString(query_$1)
};
$lzsc$temp["displayName"] = "parseQueryString";
return $lzsc$temp
})(), "addObject", (function  () {
var $lzsc$temp = function  (o_$1) {
for (var n_$2 in o_$1) {
this.setValue(n_$2, o_$1[n_$2])
}};
$lzsc$temp["displayName"] = "addObject";
return $lzsc$temp
})(), "clone", (function  () {
var $lzsc$temp = function  (arg_$1) {
switch (arguments.length) {
case 0:
arg_$1 = null
};
if (arg_$1) {
Debug.info("%w.%s is deprecated. Do not pass as argument to clone()", this, arguments.callee)
};
var o_$2 = new (lz.Param)();
for (var n_$3 in this.d) {
o_$2.d[n_$3] = this.d[n_$3].concat()
};
return o_$2
};
$lzsc$temp["displayName"] = "clone";
return $lzsc$temp
})(), "remove", (function  () {
var $lzsc$temp = function  (name_$1) {
switch (arguments.length) {
case 0:
name_$1 = null
};
if (name_$1 == null) {
this.d = {}} else {
var a_$2 = this.d[name_$1];
if (a_$2 != null) {
a_$2.shift();
if (!a_$2.length) {
delete this.d[name_$1]
}}}};
$lzsc$temp["displayName"] = "remove";
return $lzsc$temp
})(), "setValue", (function  () {
var $lzsc$temp = function  (name_$1, val_$2, enc_$3) {
switch (arguments.length) {
case 2:
enc_$3 = false
};
if (enc_$3) {
val_$2 = encodeURIComponent(val_$2)
};
this.d[name_$1] = [val_$2]
};
$lzsc$temp["displayName"] = "setValue";
return $lzsc$temp
})(), "addValue", (function  () {
var $lzsc$temp = function  (name_$1, val_$2, enc_$3) {
switch (arguments.length) {
case 2:
enc_$3 = false
};
if (enc_$3) {
val_$2 = encodeURIComponent(val_$2)
};
var a_$4 = this.d[name_$1];
if (a_$4 == null) {
this.d[name_$1] = [val_$2]
} else {
a_$4.push(val_$2)
}};
$lzsc$temp["displayName"] = "addValue";
return $lzsc$temp
})(), "getValue", (function  () {
var $lzsc$temp = function  (name_$1) {
var a_$2 = this.d[name_$1];
if (a_$2 != null) {
return a_$2[0]
};
return null
};
$lzsc$temp["displayName"] = "getValue";
return $lzsc$temp
})(), "getValues", (function  () {
var $lzsc$temp = function  (name_$1) {
var a_$2 = this.d[name_$1];
if (a_$2 != null) {
return a_$2.concat()
};
return null
};
$lzsc$temp["displayName"] = "getValues";
return $lzsc$temp
})(), "getValueNoCase", (function  () {
var $lzsc$temp = function  (name_$1) {
Debug.deprecated(this, arguments.callee, this.getValues);
var a_$2 = this.getValues(name_$1);
return a_$2 != null && a_$2.length == 1 ? a_$2[0] : a_$2
};
$lzsc$temp["displayName"] = "getValueNoCase";
return $lzsc$temp
})(), "getNames", (function  () {
var $lzsc$temp = function  () {
var o_$1 = [];
for (var n_$2 in this.d) {
o_$1.push(n_$2)
};
return o_$1
};
$lzsc$temp["displayName"] = "getNames";
return $lzsc$temp
})(), "setDelimiter", (function  () {
var $lzsc$temp = function  (d_$1) {
var o_$2 = this.delimiter;
this.delimiter = d_$1;
return o_$2
};
$lzsc$temp["displayName"] = "setDelimiter";
return $lzsc$temp
})(), "setSeparator", (function  () {
var $lzsc$temp = function  (s_$1) {
var o_$2 = this.separator;
this.separator = s_$1;
return o_$2
};
$lzsc$temp["displayName"] = "setSeparator";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return this.serialize()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "serialize", (function  () {
var $lzsc$temp = function  (sep_$1, delim_$2, uriencode_$3) {
switch (arguments.length) {
case 0:
sep_$1 = null;;case 1:
delim_$2 = null;;case 2:
uriencode_$3 = false
};
var sep_$1 = sep_$1 == null ? this.separator : sep_$1;
var dlm_$4 = delim_$2 == null ? this.delimiter : delim_$2;
var o_$5 = "";
var c_$6 = false;
for (var mk_$7 in this.d) {
var n_$8 = this.d[mk_$7];
if (n_$8 != null) {
for (var i_$9 = 0;i_$9 < n_$8.length;++i_$9) {
if (c_$6) {
o_$5 += dlm_$4
};
o_$5 += mk_$7 + sep_$1 + (uriencode_$3 ? encodeURIComponent(n_$8[i_$9]) : n_$8[i_$9]);
c_$6 = true
}}};
return o_$5
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})()], ["parseQueryString", (function  () {
var $lzsc$temp = function  (query_$1) {
var parameters_$2 = query_$1.split("&");
var queries_$3 = {};
for (var i_$4 = 0;i_$4 < parameters_$2.length;++i_$4) {
var key_$5 = parameters_$2[i_$4];
var value_$6 = "";
var n_$7 = key_$5.indexOf("=");
if (n_$7 > 0) {
value_$6 = unescape(key_$5.substring(n_$7 + 1));
key_$5 = key_$5.substring(0, n_$7)
};
queries_$3[key_$5] = value_$6
};
return queries_$3
};
$lzsc$temp["displayName"] = "parseQueryString";
return $lzsc$temp
})()]);
lz.Param = LzParam;
Class.make("LzParsedPath", null, ["path", null, "selectors", null, "context", null, "dsetname", null, "dsrcname", null, "islocaldata", null, "operator", null, "aggOperator", null, "hasAttrOper", false, "hasOpSelector", false, "hasDotDot", false, "getContext", (function  () {
var $lzsc$temp = function  (dp_$1) {
if (this.context != null) {
return this.context
} else {
if (this.islocaldata != null) {
return dp_$1.getLocalDataContext(this.islocaldata)
} else {
if (this.dsrcname != null) {
return canvas[this.dsrcname][this.dsetname]
} else {
if (this.dsetname != null) {
return canvas.datasets[this.dsetname]
}}}};
return null
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (pa_$1, node_$2) {
this.path = pa_$1;
this.selectors = [];
var sourceindex_$3 = pa_$1.indexOf(":/");
if (sourceindex_$3 > -1) {
var sourceparts_$4 = pa_$1.substring(0, sourceindex_$3).split(":");
if (sourceparts_$4.length > 1) {
var dsrc_$5 = LzParsedPath.trim(sourceparts_$4[0]);
var dset_$6 = LzParsedPath.trim(sourceparts_$4[1]);
if (dsrc_$5 == "local") {
this.islocaldata = dset_$6.split(".")
} else {
if (canvas[dsrc_$5][dset_$6] == null) {
Debug.error("couldn't find dataset for %w (canvas[%#w][%#w])", pa_$1, dsrc_$5, dset_$6)
};
this.dsrcname = dsrc_$5;
this.dsetname = dset_$6
}} else {
var name_$7 = LzParsedPath.trim(sourceparts_$4[0]);
if (name_$7 == "new") {
this.context = new AnonDatasetGenerator(this)
} else {
if (canvas.datasets[name_$7] == null) {
Debug.error("couldn't find dataset for %w (canvas.datasets[%#w])", pa_$1, name_$7)
};
this.dsetname = name_$7
}};
var rest_$8 = pa_$1.substring(sourceindex_$3 + 2)
} else {
var rest_$8 = pa_$1
};
var nodes_$9 = [];
var currnode_$10 = "";
var instring_$11 = false;
var escape_$12 = false;
for (var i_$13 = 0;i_$13 < rest_$8.length;i_$13++) {
var c_$14 = rest_$8.charAt(i_$13);
if (c_$14 == "\\" && escape_$12 == false) {
escape_$12 = true;
continue
} else {
if (escape_$12 == true) {
escape_$12 = false;
currnode_$10 += c_$14;
continue
} else {
if (instring_$11 == false && c_$14 == "/") {
nodes_$9.push(currnode_$10);
currnode_$10 = "";
continue
} else {
if (c_$14 == "'") {
instring_$11 = instring_$11 ? false : true
}}}};
currnode_$10 += c_$14
};
nodes_$9.push(currnode_$10);
if (nodes_$9 != null) {
for (var i_$13 = 0;i_$13 < nodes_$9.length;i_$13++) {
var cnode_$15 = LzParsedPath.trim(nodes_$9[i_$13]);
if (i_$13 == nodes_$9.length - 1) {
if (cnode_$15.charAt(0) == "@") {
this.hasAttrOper = true;
if (cnode_$15.charAt(1) == "*") {
this.operator = "attributes"
} else {
this.operator = "attributes." + cnode_$15.substring(1, cnode_$15.length)
};
continue
} else {
if (cnode_$15.charAt(cnode_$15.length - 1) == ")") {
if (cnode_$15.indexOf("last") > -1) {
this.aggOperator = "last"
} else {
if (cnode_$15.indexOf("position") > -1) {
this.aggOperator = "position"
} else {
if (cnode_$15.indexOf("name") > -1) {
this.operator = "name"
} else {
if (cnode_$15.indexOf("text") > -1) {
this.operator = "text"
} else {
if (cnode_$15.indexOf("serialize") > -1) {
this.operator = "serialize"
} else {
Debug.warn("Unknown operator: '%s'", cnode_$15)
}}}}};
continue
} else {
if (cnode_$15 == "") {
continue
}}}};
var preds_$16 = cnode_$15.split("[");
var n_$17 = LzParsedPath.trim(preds_$16[0]);
this.selectors.push(n_$17 == "" ? "/" : n_$17);
if (n_$17 == "" || n_$17 == "..") {
this.hasDotDot = true
};
if (preds_$16 != null) {
for (var j_$18 = 1;j_$18 < preds_$16.length;j_$18++) {
var pred_$19 = LzParsedPath.trim(preds_$16[j_$18]);
pred_$19 = pred_$19.substring(0, pred_$19.length - 1);
if (LzParsedPath.trim(pred_$19).charAt(0) == "@") {
var attrpred_$20 = pred_$19.split("=");
var a_$21;
var tattr_$22 = attrpred_$20.shift().substring(1);
if (attrpred_$20.length > 0) {
var aval_$23 = LzParsedPath.trim(attrpred_$20.join("="));
aval_$23 = aval_$23.substring(1, aval_$23.length - 1);
a_$21 = {pred: "attrval", attr: LzParsedPath.trim(tattr_$22), val: LzParsedPath.trim(aval_$23)}} else {
a_$21 = {pred: "hasattr", attr: LzParsedPath.trim(tattr_$22)}};
this.selectors.push(a_$21);
this.hasOpSelector = true
} else {
var a_$21 = pred_$19.split("-");
a_$21[0] = LzParsedPath.trim(a_$21[0]);
if (a_$21[0] == "") {
a_$21[0] = 1
};
if (a_$21[1] != null) {
a_$21[1] = LzParsedPath.trim(a_$21[1])
};
if (a_$21[1] == "") {
a_$21[1] = Infinity
} else {
if (a_$21.length == 1) {
a_$21[1] = a_$21[0]
}};
a_$21.pred = "range";
this.selectors.push(a_$21)
}}}}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Parsed path for path: " + this.path
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "debugWrite", (function  () {
var $lzsc$temp = function  () {
Debug.write(this);
Debug.write("  c:" + this.context + "|");
Debug.write("  n:" + this.selectors.join("|") + "|");
Debug.write("  d:" + this.operator + "|");
Debug.write("  ")
};
$lzsc$temp["displayName"] = "debugWrite";
return $lzsc$temp
})()], ["trim", (function  () {
var $lzsc$temp = function  (s_$1) {
var st_$2 = 0;
var dotrim_$3 = false;
while (s_$1.charAt(st_$2) == " ") {
st_$2++;
dotrim_$3 = true
};
var len_$4 = s_$1.length - st_$2;
while (s_$1.charAt(st_$2 + len_$4 - 1) == " ") {
len_$4--;
dotrim_$3 = true
};
return dotrim_$3 ? s_$1.substr(st_$2, len_$4) : s_$1
};
$lzsc$temp["displayName"] = "trim";
return $lzsc$temp
})()]);
Class.make("AnonDatasetGenerator", LzEventable, ["pp", null, "__LZdepChildren", null, "onDocumentChange", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "noncontext", true, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (pp_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.pp = pp_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getContext", (function  () {
var $lzsc$temp = function  () {
var d_$1 = new LzDataset(null, {name: null});
var ppsel_$2 = this.pp.selectors;
if (ppsel_$2 != null) {
var dp_$3 = d_$1.getPointer();
for (var i_$4 = 0;i_$4 < ppsel_$2.length;i_$4++) {
if (ppsel_$2[i_$4] == "/") {
continue
};
dp_$3.addNode(ppsel_$2[i_$4]);
dp_$3.selectChild()
}};
return d_$1
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "getDataset", (function  () {
var $lzsc$temp = function  () {
return null
};
$lzsc$temp["displayName"] = "getDataset";
return $lzsc$temp
})()], null);
Class.make("LzDatapath", LzDatapointer, ["datacontrolsvisibility", true, "$lzc$set_datacontrolsvisibility", (function  () {
var $lzsc$temp = function  (v_$1) {
this.datacontrolsvisibility = v_$1
};
$lzsc$temp["displayName"] = "$lzc$set_datacontrolsvisibility";
return $lzsc$temp
})(), "__LZtakeDPSlot", true, "storednodes", null, "__LZneedsUpdateAfterInit", false, "__LZdepChildren", null, "sel", false, "__LZisclone", false, "pooling", false, "replication", void 0, "axis", "y", "spacing", 0, "sortpath", void 0, "$lzc$set_sortpath", (function  () {
var $lzsc$temp = function  (xpath_$1) {
this.setOrder(xpath_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_sortpath";
return $lzsc$temp
})(), "setOrder", (function  () {
var $lzsc$temp = function  (xpath_$1, comparator_$2) {
switch (arguments.length) {
case 1:
comparator_$2 = null
};
if (this.__LZisclone) {
this.immediateparent.cloneManager.setOrder(xpath_$1, comparator_$2)
} else {
this.sortpath = xpath_$1;
if (comparator_$2 != null) {
this.sortorder = comparator_$2
}}};
$lzsc$temp["displayName"] = "setOrder";
return $lzsc$temp
})(), "sortorder", "ascending", "$lzc$set_sortorder", (function  () {
var $lzsc$temp = function  (comparator_$1) {
this.setComparator(comparator_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_sortorder";
return $lzsc$temp
})(), "setComparator", (function  () {
var $lzsc$temp = function  (comparator_$1) {
if (this.__LZisclone) {
this.immediateparent.cloneManager.setComparator(comparator_$1)
} else {
this.sortorder = comparator_$1
}};
$lzsc$temp["displayName"] = "setComparator";
return $lzsc$temp
})(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (v_$1, args_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 1:
args_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, v_$1, args_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (v_$1, args_$2) {
this.rerunxpath = true;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, v_$1, args_$2);
if (args_$2.datacontrolsvisibility != null) {
this.datacontrolsvisibility = args_$2.datacontrolsvisibility
};
if (this.__LZtakeDPSlot) {
if (this.immediateparent.datapath != null) {
Debug.warn("overriding datapath %w with %w", this.immediateparent.datapath, this)
};
this.immediateparent.datapath = this;
var pdp_$3 = this.immediateparent.searchParents("datapath").datapath;
if (pdp_$3 != null) {
var tarr_$4 = pdp_$3.__LZdepChildren;
if (tarr_$4 != null) {
pdp_$3.__LZdepChildren = [];
for (var i_$5 = tarr_$4.length - 1;i_$5 >= 0;i_$5--) {
var c_$6 = tarr_$4[i_$5];
if (c_$6 !== this && !(LzDataAttrBind["$lzsc$isa"] ? LzDataAttrBind.$lzsc$isa(c_$6) : c_$6 instanceof LzDataAttrBind) && c_$6.immediateparent != this.immediateparent && c_$6.immediateparent.childOf(this.immediateparent)) {
c_$6.setDataContext(this, true)
} else {
pdp_$3.__LZdepChildren.push(c_$6)
}}}} else {
Debug.warn("unexpected state for '%w', couldn't find parent-datapath!", this)
}}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function  () {
var $lzsc$temp = function  (nodes_$1) {
var clonetype_$2;
if (this.replication == "lazy") {
clonetype_$2 = LzLazyReplicationManager
} else {
if (this.replication == "resize") {
clonetype_$2 = LzResizeReplicationManager
} else {
clonetype_$2 = LzReplicationManager
}};
this.storednodes = nodes_$1;
var rman_$3 = new clonetype_$2(this, this._instanceAttrs);
this.storednodes = null;
return rman_$3
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "setNodes", (function  () {
var $lzsc$temp = function  (nodes_$1) {
var rman_$2 = this.__LZHandleMultiNodes(nodes_$1);
if (!rman_$2) {
rman_$2 = this
};
rman_$2.__LZsetTracking(null);
if (nodes_$1) {
for (var i_$3 = 0;i_$3 < nodes_$1.length;i_$3++) {
var n_$4 = nodes_$1[i_$3];
var own_$5 = n_$4.ownerDocument;
rman_$2.__LZsetTracking(own_$5, true, n_$4 != own_$5)
}}};
$lzsc$temp["displayName"] = "setNodes";
return $lzsc$temp
})(), "__LZsendUpdate", (function  () {
var $lzsc$temp = function  (upd_$1, upp_$2) {
switch (arguments.length) {
case 0:
upd_$1 = false;;case 1:
upp_$2 = false
};
var pchg_$3 = this.__LZpchanged;
if (!(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsendUpdate"] || this.nextMethod(arguments.callee, "__LZsendUpdate")).call(this, upd_$1, upp_$2)) {
return false
};
if (this.immediateparent.isinited) {
this.__LZApplyData(pchg_$3)
} else {
this.__LZneedsUpdateAfterInit = true
};
return true
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "__LZApplyDataOnInit", (function  () {
var $lzsc$temp = function  () {
if (this.__LZneedsUpdateAfterInit) {
this.__LZApplyData();
this.__LZneedsUpdateAfterInit = false
}};
$lzsc$temp["displayName"] = "__LZApplyDataOnInit";
return $lzsc$temp
})(), "__LZApplyData", (function  () {
var $lzsc$temp = function  (force_$1) {
switch (arguments.length) {
case 0:
force_$1 = false
};
var ip_$2 = this.immediateparent;
if (this.datacontrolsvisibility) {
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ip_$2) : ip_$2 instanceof LzView) {
var ipview_$3 = ip_$2;
ipview_$3.__LZvizDat = this.p != null;
ipview_$3.__LZupdateShown()
}};
var cdat_$4 = force_$1 || ip_$2.data != this.data || this.parsedPath && this.parsedPath.operator == "attributes";
this.data = this.data == null ? null : this.data;
ip_$2.data = this.data;
if (cdat_$4) {
if (ip_$2.ondata.ready) {
ip_$2.ondata.sendEvent(this.data)
};
var ppath_$5 = this.parsedPath;
if (ppath_$5 && (ppath_$5.operator != null || ppath_$5.aggOperator != null)) {
ip_$2.applyData(this.data)
}}};
$lzsc$temp["displayName"] = "__LZApplyData";
return $lzsc$temp
})(), "setDataContext", (function  () {
var $lzsc$temp = function  (dc_$1, implicit_$2) {
switch (arguments.length) {
case 1:
implicit_$2 = false
};
if (dc_$1 == null && this.immediateparent != null) {
dc_$1 = this.immediateparent.searchParents("datapath").datapath;
implicit_$2 = true
} else {
if (this.immediateparent == null) {
Debug.warn("immediateparent is null for %w in '%s'", this, arguments.callee)
}};
if (dc_$1 == this.context) {
return
};
if (implicit_$2) {
if (dc_$1.__LZdepChildren == null) {
dc_$1.__LZdepChildren = [this]
} else {
dc_$1.__LZdepChildren.push(this)
}} else {
if (this.context && (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(this.context) : this.context instanceof LzDatapath)) {
var dclist_$3 = this.context.__LZdepChildren;
if (dclist_$3) {
for (var i_$4 = 0;i_$4 < dclist_$3.length;i_$4++) {
if (dclist_$3[i_$4] === this) {
dclist_$3.splice(i_$4, 1);
break
}}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, dc_$1)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "destroy", (function  () {
var $lzsc$temp = function  () {
this.__LZupdateLocked = true;
var context_$1 = this.context;
if (context_$1 && !context_$1.__LZdeleted && (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(context_$1) : context_$1 instanceof LzDatapath)) {
var dca_$2 = context_$1.__LZdepChildren;
if (dca_$2 != null) {
for (var i_$3 = 0;i_$3 < dca_$2.length;i_$3++) {
if (dca_$2[i_$3] === this) {
dca_$2.splice(i_$3, 1);
break
}}}};
var ip_$4 = this.immediateparent;
if (!ip_$4.__LZdeleted) {
var depChildren_$5 = this.__LZdepChildren;
if (depChildren_$5 != null && depChildren_$5.length > 0) {
var dnpar_$6 = ip_$4.searchParents("datapath").datapath;
for (var i_$3 = 0;i_$3 < depChildren_$5.length;i_$3++) {
depChildren_$5[i_$3].setDataContext(dnpar_$6, true)
};
this.__LZdepChildren = null
}};
if (ip_$4.datapath === this) {
ip_$4.datapath = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).apply(this, arguments)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "updateData", (function  () {
var $lzsc$temp = function  () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function  () {
var $lzsc$temp = function  (recursive_$1) {
switch (arguments.length) {
case 0:
recursive_$1 = false
};
if (!recursive_$1 && this.p) {
this.p.__LZlockFromUpdate(this)
};
var ppdo_$2 = this.parsedPath ? this.parsedPath.operator : null;
if (ppdo_$2 != null) {
var dat_$3 = this.immediateparent.updateData();
if (dat_$3 !== void 0) {
if (ppdo_$2 == "name") {
this.setNodeName(dat_$3)
} else {
if (ppdo_$2 == "text") {
this.setNodeText(dat_$3)
} else {
if (ppdo_$2 == "attributes") {
this.p.$lzc$set_attributes(dat_$3)
} else {
this.setNodeAttribute(ppdo_$2.substring(11), dat_$3)
}}}}};
var depChildren_$4 = this.__LZdepChildren;
if (depChildren_$4 != null) {
for (var i_$5 = 0;i_$5 < depChildren_$4.length;i_$5++) {
depChildren_$4[i_$5].__LZupdateData(true)
}};
if (!recursive_$1 && this.p) {
this.p.__LZunlockFromUpdate(this)
}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Datapath for " + this.immediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "__LZcheckChange", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
if (!(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZcheckChange"] || this.nextMethod(arguments.callee, "__LZcheckChange")).call(this, chgpkg_$1)) {
if (chgpkg_$1.who.childOfNode(this.p, true) && this.onDocumentChange.ready) {
this.onDocumentChange.sendEvent(chgpkg_$1)
}};
return false
};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZsetTracking", (function  () {
var $lzsc$temp = function  (who_$1, additive_$2, needscheck_$3) {
switch (arguments.length) {
case 1:
additive_$2 = false;;case 2:
needscheck_$3 = false
};
if (!who_$1 || !additive_$2) {
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsetTracking"] || this.nextMethod(arguments.callee, "__LZsetTracking")).call(this, who_$1, true)
};
var tracking_$4 = this.__LZtracking;
var trackDel_$5 = this.__LZtrackDel;
if (!(tracking_$4 instanceof Array)) {
Debug.error("%w.__LZtracking is %w, expecting an array", this, tracking_$4)
};
if (needscheck_$3) {
var len_$6 = tracking_$4.length;
for (var i_$7 = 0;i_$7 < len_$6;i_$7++) {
if (tracking_$4[i_$7] === who_$1) {
return
}}};
if (!trackDel_$5) {
this.__LZtrackDel = trackDel_$5 = new LzDelegate(this, "__LZcheckChange")
};
tracking_$4.push(who_$1);
trackDel_$5.register(who_$1, "onDocumentChange")
};
$lzsc$temp["displayName"] = "__LZsetTracking";
return $lzsc$temp
})(), "$lzc$set___LZmanager", (function  () {
var $lzsc$temp = function  (m_$1) {
this.__LZisclone = true;
this.immediateparent.cloneManager = m_$1;
this.parsedPath = m_$1.parsedPath;
this.xpath = m_$1.xpath;
this.setDataContext(m_$1)
};
$lzsc$temp["displayName"] = "$lzc$set___LZmanager";
return $lzsc$temp
})(), "setClonePointer", (function  () {
var $lzsc$temp = function  (p_$1) {
var pc_$2 = this.p != p_$1;
this.p = p_$1;
if (pc_$2) {
if (p_$1 && this.sel != p_$1.sel) {
this.sel = p_$1.sel || false;
this.immediateparent.setSelected(this.sel)
};
this.__LZpchanged = true;
this.__LZsetData()
}};
$lzsc$temp["displayName"] = "setClonePointer";
return $lzsc$temp
})(), "setSelected", (function  () {
var $lzsc$temp = function  (torf_$1) {
this.p.sel = torf_$1;
this.sel = torf_$1;
this.immediateparent.setSelected(torf_$1)
};
$lzsc$temp["displayName"] = "setSelected";
return $lzsc$temp
})(), "__LZgetLast", (function  () {
var $lzsc$temp = function  () {
var context_$1 = this.context;
if (this.__LZisclone) {
return context_$1.nodes.length
} else {
if (this.p == context_$1.getContext() && (LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(context_$1) : context_$1 instanceof LzDatapointer)) {
return context_$1.__LZgetLast()
} else {
return 1
}}};
$lzsc$temp["displayName"] = "__LZgetLast";
return $lzsc$temp
})(), "__LZgetPosition", (function  () {
var $lzsc$temp = function  () {
if (this.__LZisclone) {
return this.immediateparent.clonenumber + 1
} else {
var context_$1 = this.context;
if (this.p == context_$1.getContext() && (LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(context_$1) : context_$1 instanceof LzDatapointer)) {
return context_$1.__LZgetPosition()
} else {
return 1
}}};
$lzsc$temp["displayName"] = "__LZgetPosition";
return $lzsc$temp
})()], ["tagname", "datapath", "attributes", new LzInheritedHash(LzDatapointer.attributes)]);
lz[LzDatapath.tagname] = LzDatapath;
Class.make("LzReplicationManager", LzDatapath, ["asyncnew", true, "initialnodes", void 0, "clonePool", void 0, "cloneClass", void 0, "cloneParent", void 0, "cloneAttrs", void 0, "cloneChildren", void 0, "hasdata", void 0, "orderpath", void 0, "comparator", void 0, "__LZxpathconstr", null, "__LZxpathdepend", null, "visible", true, "__LZpreventXPathUpdate", false, "nodes", void 0, "clones", void 0, "__LZdataoffset", 0, "onnodes", LzDeclaredEvent, "onclones", LzDeclaredEvent, "onvisible", LzDeclaredEvent, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$1, args_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function  () {
var $lzsc$temp = function  () {
return false
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2) {
this.pooling = this.getDefaultPooling();
this.__LZtakeDPSlot = false;
this.datacontrolsvisibility = false;
var view_$3 = odp_$1.immediateparent;
this.classroot = view_$3.classroot;
if (view_$3 === canvas) {
this.nodes = [];
this.clones = [];
this.clonePool = [];
Debug.error("LzReplicationManager constructed at canvas. A datapath cannot be defined on the canvas");
return
};
this.datapath = this;
var name_$4 = view_$3.name;
if (name_$4 != null) {
args_$2.name = name_$4;
view_$3.immediateparent[name_$4] = null;
view_$3.parent[name_$4] = null
};
var idbinder_$5 = view_$3.$lzc$bind_id;
if (idbinder_$5 != null) {
idbinder_$5.call(null, view_$3, false);
view_$3.$lzc$bind_id = null;
this.$lzc$bind_id = idbinder_$5;
idbinder_$5.call(null, this)
};
var namebinder_$6 = view_$3.$lzc$bind_name;
if (namebinder_$6 != null) {
namebinder_$6.call(null, view_$3, false);
view_$3.$lzc$bind_name = null;
this.$lzc$bind_name = namebinder_$6;
namebinder_$6.call(null, this)
};
args_$2.xpath = LzNode._ignoreAttribute;
if (odp_$1.sortpath != null) {
args_$2.sortpath = odp_$1.sortpath
};
if (odp_$1.sortorder != null || odp_$1.sortorder) {
args_$2.sortorder = odp_$1.sortorder
};
this.initialnodes = odp_$1.storednodes;
if (odp_$1.__LZspecialDotDot) {
this.__LZspecialDotDot = true;
if (odp_$1.__LZdotdotCheckDel) {
odp_$1.__LZdotdotCheckDel.unregisterAll()
};
odp_$1.__LZspecialDotDot = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, view_$3.parent, args_$2);
if (args_$2.name != null && view_$3.parent != view_$3.immediateparent) {
view_$3.immediateparent[args_$2.name] = this
};
this.xpath = odp_$1.xpath;
this.parsedPath = odp_$1.parsedPath;
this.cloneClass = view_$3.constructor;
this.cloneParent = view_$3.parent;
var cloneAttrs_$7 = new LzInheritedHash(view_$3._instanceAttrs);
cloneAttrs_$7.datapath = LzNode._ignoreAttribute;
cloneAttrs_$7.$datapath = {"class": lz.datapath};
cloneAttrs_$7.$datapath.attrs = {datacontrolsvisibility: odp_$1.datacontrolsvisibility, __LZmanager: this};
delete cloneAttrs_$7.id;
delete cloneAttrs_$7.name;
delete cloneAttrs_$7.$lzc$bind_id;
delete cloneAttrs_$7.$lzc$bind_name;
this.cloneAttrs = cloneAttrs_$7;
if (odp_$1.datacontrolsvisibility) {
this.visible = true
} else {
if (!view_$3.isinited) {
var vis_$8 = this.__LZgetInstanceAttr(view_$3, "visible");
if (typeof vis_$8 == "boolean" || (Boolean["$lzsc$isa"] ? Boolean.$lzsc$isa(vis_$8) : vis_$8 instanceof Boolean)) {
this.visible = vis_$8
} else {
this.visible = view_$3.visible
}} else {
this.visible = view_$3.visible
}};
if (args_$2.pooling != null) {
this.pooling = args_$2.pooling
};
var dp_$9 = this.__LZgetInstanceAttr(view_$3, "datapath");
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(dp_$9) : dp_$9 instanceof LzAlwaysExpr) {
var dpCons_$10 = dp_$9;
this.__LZxpathconstr = view_$3[dpCons_$10.methodName];
this.__LZxpathdepend = view_$3[dpCons_$10.dependenciesName];
this.__LZpreventXPathUpdate = true;
this.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr", "__LZxpathdepend"));
this.__LZpreventXPathUpdate = false;
if (this.pooling) {
view_$3.releaseConstraintMethod(dpCons_$10.methodName)
}} else {
var xp_$11 = this.__LZgetInstanceAttr(odp_$1, "xpath");
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(xp_$11) : xp_$11 instanceof LzAlwaysExpr) {
var refObj_$12 = new LzRefNode(this);
var xpCons_$13 = xp_$11;
refObj_$12.__LZxpathconstr = odp_$1[xpCons_$13.methodName];
refObj_$12.__LZxpathdepend = odp_$1[xpCons_$13.dependenciesName];
this.__LZpreventXPathUpdate = true;
refObj_$12.applyConstraintExpr(new LzAlwaysExpr("__LZxpathconstr", "__LZxpathdepend"));
this.__LZpreventXPathUpdate = false;
if (this.pooling) {
odp_$1.releaseConstraintMethod(xpCons_$13.methodName)
}}};
this.__LZsetCloneAttrs();
if (view_$3._instanceChildren) {
this.cloneChildren = view_$3._instanceChildren.concat()
} else {
this.cloneChildren = []
};
var mycontext_$14 = odp_$1.context;
this.clones = [];
this.clonePool = [];
if (this.pooling) {
odp_$1.$lzc$set___LZmanager(this);
this.clones.push(view_$3);
view_$3.immediateparent.addSubview(view_$3)
} else {
this.destroyClone(view_$3)
};
this.setDataContext(mycontext_$14, mycontext_$14 instanceof LzDatapointer)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZgetInstanceAttr", (function  () {
var $lzsc$temp = function  (node_$1, attr_$2) {
var ia_$3 = node_$1._instanceAttrs;
if (ia_$3 && (attr_$2 in ia_$3)) {
return ia_$3[attr_$2]
} else {
var ca_$4 = node_$1["constructor"].attributes;
if (ca_$4 && (attr_$2 in ca_$4)) {
return ca_$4[attr_$2]
}};
return void 0
};
$lzsc$temp["displayName"] = "__LZgetInstanceAttr";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "__LZapplyArgs", (function  () {
var $lzsc$temp = function  (args_$1, constcall_$2) {
switch (arguments.length) {
case 1:
constcall_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZapplyArgs"] || this.nextMethod(arguments.callee, "__LZapplyArgs")).call(this, args_$1, constcall_$2);
if (this.__LZdeleted) {
return
};
this.__LZHandleMultiNodes(this.initialnodes);
this.initialnodes = null;
if (this.visible == false) {
this.$lzc$set_visible(false)
}};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "setDataContext", (function  () {
var $lzsc$temp = function  (p_$1, implicit_$2) {
switch (arguments.length) {
case 1:
implicit_$2 = false
};
if (p_$1 == null && this.immediateparent != null && this.immediateparent["datapath"] != null) {
p_$1 = this.immediateparent.datapath;
implicit_$2 = true
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, p_$1, implicit_$2)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "getCloneNumber", (function  () {
var $lzsc$temp = function  (n_$1) {
return this.clones[n_$1]
};
$lzsc$temp["displayName"] = "getCloneNumber";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function  () {
var $lzsc$temp = function  () {
this.nodes = [];
var cls_$1 = this.clones;
while (cls_$1.length) {
if (this.pooling) {
this.poolClone()
} else {
var v_$2 = cls_$1.pop();
this.destroyClone(v_$2)
}}};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZHandleSingleNode", (function  () {
var $lzsc$temp = function  (n_$1) {
this.__LZHandleMultiNodes([n_$1])
};
$lzsc$temp["displayName"] = "__LZHandleSingleNode";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function  () {
var $lzsc$temp = function  (n_$1) {
var layouts_$2 = this.parent && this.parent.layouts ? this.parent.layouts : [];
for (var i_$3 = 0;i_$3 < layouts_$2.length;++i_$3) {
layouts_$2[i_$3].lock()
};
this.hasdata = true;
var lastnodes_$4 = this.nodes;
this.nodes = n_$1;
if (this.onnodes.ready) {
this.onnodes.sendEvent(this.nodes)
};
if (this.__LZspecialDotDot) {
this.__LZsetupDotDot(n_$1[0])
};
if (this.orderpath != null && this.nodes) {
this.nodes = this.mergesort(this.nodes, 0, this.nodes.length - 1)
};
this.__LZadjustVisibleClones(lastnodes_$4, true);
var len_$5 = this.clones.length;
for (var i_$3 = 0;i_$3 < len_$5;i_$3++) {
var cl_$6 = this.clones[i_$3];
var iplusoffset_$7 = i_$3 + this.__LZdataoffset;
cl_$6.clonenumber = iplusoffset_$7;
if (this.nodes) {
cl_$6.datapath.setClonePointer(this.nodes[iplusoffset_$7])
};
if (cl_$6.onclonenumber.ready) {
cl_$6.onclonenumber.sendEvent(iplusoffset_$7)
}};
if (this.onclones.ready) {
this.onclones.sendEvent(this.clones)
};
for (var i_$3 = 0;i_$3 < layouts_$2.length;++i_$3) {
layouts_$2[i_$3].unlock()
};
return null
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function  () {
var $lzsc$temp = function  (lastnodes_$1, newnodes_$2) {
var stpt_$3 = this.__LZdiffArrays(lastnodes_$1, this.nodes);
if (!this.pooling) {
while (this.clones.length > stpt_$3) {
var v_$4 = this.clones.pop();
this.destroyClone(v_$4)
}};
lz.Instantiator.enableDataReplicationQueuing(this);
while (this.nodes && this.nodes.length > this.clones.length) {
var cl_$5 = this.getNewClone();
if (!cl_$5) {
break
};
this.clones.push(cl_$5)
};
lz.Instantiator.clearDataReplicationQueue(this);
while (this.nodes && this.nodes.length < this.clones.length) {
this.poolClone()
}};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})(), "mergesort", (function  () {
var $lzsc$temp = function  (arr_$1, lo_$2, hi_$3) {
if (lo_$2 < hi_$3) {
var mid_$4 = lo_$2 + Math.floor((hi_$3 - lo_$2) / 2);
var a_$5 = this.mergesort(arr_$1, lo_$2, mid_$4);
var b_$6 = this.mergesort(arr_$1, mid_$4 + 1, hi_$3)
} else {
if (arr_$1.length == 0) {
return []
} else {
return [arr_$1[lo_$2]]
}};
var r_$7 = [];
var ia_$8 = 0;
var ib_$9 = 0;
var al_$10 = a_$5.length;
var bl_$11 = b_$6.length;
while (ia_$8 < al_$10 && ib_$9 < bl_$11) {
if (this.orderf(b_$6[ib_$9], a_$5[ia_$8]) == 1) {
r_$7.push(b_$6[ib_$9++])
} else {
r_$7.push(a_$5[ia_$8++])
}};
while (ia_$8 < al_$10) {
r_$7.push(a_$5[ia_$8++])
};
while (ib_$9 < bl_$11) {
r_$7.push(b_$6[ib_$9++])
};
return r_$7
};
$lzsc$temp["displayName"] = "mergesort";
return $lzsc$temp
})(), "orderf", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var op_$3 = this.orderpath;
this.p = a_$1;
var aa_$4 = this.xpathQuery(op_$3);
this.p = b_$2;
var bb_$5 = this.xpathQuery(op_$3);
this.p = null;
if (aa_$4 == null || aa_$4 == "") {
aa_$4 = "\n"
};
if (bb_$5 == null || bb_$5 == "") {
bb_$5 = "\n"
};
return this.comparator(aa_$4, bb_$5)
};
$lzsc$temp["displayName"] = "orderf";
return $lzsc$temp
})(), "ascDict", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
if (a_$1.toLowerCase() < b_$2.toLowerCase()) {
return 1
} else {
return 0
}};
$lzsc$temp["displayName"] = "ascDict";
return $lzsc$temp
})(), "descDict", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
if (a_$1.toLowerCase() > b_$2.toLowerCase()) {
return 1
} else {
return 0
}};
$lzsc$temp["displayName"] = "descDict";
return $lzsc$temp
})(), "setOrder", (function  () {
var $lzsc$temp = function  (xpath_$1, comparator_$2) {
switch (arguments.length) {
case 1:
comparator_$2 = null
};
this.orderpath = null;
if (comparator_$2 != null) {
this.setComparator(comparator_$2)
};
this.orderpath = xpath_$1;
if (this.nodes && this.nodes.length) {
this.__LZHandleMultiNodes(this.nodes)
}};
$lzsc$temp["displayName"] = "setOrder";
return $lzsc$temp
})(), "setComparator", (function  () {
var $lzsc$temp = function  (comparator_$1) {
if (comparator_$1 == "descending") {
comparator_$1 = this.descDict
} else {
if (comparator_$1 == "ascending") {
comparator_$1 = this.ascDict
} else {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(comparator_$1) : comparator_$1 instanceof Function) {

} else {
Debug.error("Invalid comparator: %s", comparator_$1)
}}};
this.comparator = comparator_$1;
if (this.orderpath != null && this.nodes && this.nodes.length) {
this.__LZHandleMultiNodes(this.nodes)
}};
$lzsc$temp["displayName"] = "setComparator";
return $lzsc$temp
})(), "getNewClone", (function  () {
var $lzsc$temp = function  (forceNew_$1) {
switch (arguments.length) {
case 0:
forceNew_$1 = null
};
if (!this.cloneParent) {
return null
};
if (this.clonePool.length) {
var v_$2 = this.reattachClone(this.clonePool.pop())
} else {
var v_$2 = new (this.cloneClass)(this.cloneParent, this.cloneAttrs, this.cloneChildren, forceNew_$1 == null ? this.asyncnew : !forceNew_$1)
};
if (this.visible == false) {
v_$2.$lzc$set_visible(false)
};
return v_$2
};
$lzsc$temp["displayName"] = "getNewClone";
return $lzsc$temp
})(), "poolClone", (function  () {
var $lzsc$temp = function  () {
var v_$1 = this.clones.pop();
this.detachClone(v_$1);
this.clonePool.push(v_$1)
};
$lzsc$temp["displayName"] = "poolClone";
return $lzsc$temp
})(), "destroyClone", (function  () {
var $lzsc$temp = function  (v_$1) {
v_$1.destroy()
};
$lzsc$temp["displayName"] = "destroyClone";
return $lzsc$temp
})(), "$lzc$set_datapath", (function  () {
var $lzsc$temp = function  (xp_$1) {
this.setXPath(xp_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_datapath";
return $lzsc$temp
})(), "setXPath", (function  () {
var $lzsc$temp = function  (xp_$1) {
if (this.__LZpreventXPathUpdate) {
return false
};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setXPath"] || this.nextMethod(arguments.callee, "setXPath")).apply(this, arguments)
};
$lzsc$temp["displayName"] = "setXPath";
return $lzsc$temp
})(), "handleDeletedNode", (function  () {
var $lzsc$temp = function  (c_$1) {
var tclone_$2 = this.clones[c_$1];
if (this.pooling) {
this.detachClone(tclone_$2);
this.clonePool.push(tclone_$2)
} else {
this.destroyClone(tclone_$2)
};
this.nodes.splice(c_$1, 1);
this.clones.splice(c_$1, 1)
};
$lzsc$temp["displayName"] = "handleDeletedNode";
return $lzsc$temp
})(), "getCloneForNode", (function  () {
var $lzsc$temp = function  (p_$1, dontmake_$2) {
switch (arguments.length) {
case 1:
dontmake_$2 = false
};
var cls_$3 = this.clones;
var len_$4 = cls_$3.length;
for (var i_$5 = 0;i_$5 < len_$4;i_$5++) {
if (cls_$3[i_$5].datapath.p == p_$1) {
return cls_$3[i_$5]
}};
return null
};
$lzsc$temp["displayName"] = "getCloneForNode";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "ReplicationManager in " + this.immediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "setVisible", (function  () {
var $lzsc$temp = function  (vis_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(vis_$1)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "$lzc$set_visible", (function  () {
var $lzsc$temp = function  (vis_$1) {
this.visible = vis_$1;
var cls_$2 = this.clones;
var len_$3 = cls_$2.length;
for (var i_$4 = 0;i_$4 < len_$3;i_$4++) {
cls_$2[i_$4].$lzc$set_visible(vis_$1)
};
if (this.onvisible.ready) {
this.onvisible.sendEvent(vis_$1)
}};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "__LZcheckChange", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
this.p = this.nodes[0];
var didrun_$2 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZcheckChange"] || this.nextMethod(arguments.callee, "__LZcheckChange")).call(this, chgpkg_$1);
this.p = null;
if (!didrun_$2) {
var who_$3 = chgpkg_$1.who;
var cls_$4 = this.clones;
var len_$5 = cls_$4.length;
for (var i_$6 = 0;i_$6 < len_$5;i_$6++) {
var cl_$7 = cls_$4[i_$6];
var dp_$8 = cl_$7.datapath;
if (dp_$8.__LZneedsOpUpdate(chgpkg_$1)) {
dp_$8.__LZsetData()
};
if (who_$3.childOfNode(dp_$8.p, true)) {
if (dp_$8.onDocumentChange.ready) {
dp_$8.onDocumentChange.sendEvent(chgpkg_$1)
}}}};
return false
};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZneedsOpUpdate", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
switch (arguments.length) {
case 0:
chgpkg_$1 = null
};
return false
};
$lzsc$temp["displayName"] = "__LZneedsOpUpdate";
return $lzsc$temp
})(), "getContext", (function  () {
var $lzsc$temp = function  (chgpkg_$1) {
switch (arguments.length) {
case 0:
chgpkg_$1 = null
};
return this.nodes[0]
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "detachClone", (function  () {
var $lzsc$temp = function  (cl_$1) {
if (cl_$1.isdetatchedclone) {
return
};
cl_$1.$lzc$set_visible(false);
cl_$1.addedToParent = false;
var svs_$2 = cl_$1.immediateparent.subviews;
for (var i_$3 = svs_$2.length - 1;i_$3 >= 0;i_$3--) {
if (svs_$2[i_$3] == cl_$1) {
svs_$2.splice(i_$3, 1);
break
}};
cl_$1.datapath.__LZtrackDel.unregisterAll();
var onremsub_$4 = cl_$1.immediateparent.onremovesubview;
if (onremsub_$4.ready) {
onremsub_$4.sendEvent(cl_$1)
};
cl_$1.isdetatchedclone = true;
cl_$1.datapath.p = null
};
$lzsc$temp["displayName"] = "detachClone";
return $lzsc$temp
})(), "reattachClone", (function  () {
var $lzsc$temp = function  (cl_$1) {
if (!cl_$1.isdetatchedclone) {
return cl_$1
};
cl_$1.immediateparent.addSubview(cl_$1);
cl_$1.$lzc$set_visible(this.visible);
cl_$1.isdetatchedclone = false;
return cl_$1
};
$lzsc$temp["displayName"] = "reattachClone";
return $lzsc$temp
})(), "__LZdiffArrays", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var i_$3 = 0;
var al_$4 = a_$1 ? a_$1.length : 0;
var bl_$5 = b_$2 ? b_$2.length : 0;
while (i_$3 < al_$4 && i_$3 < bl_$5) {
if (a_$1[i_$3] != b_$2[i_$3]) {
return i_$3
};
i_$3++
};
return i_$3
};
$lzsc$temp["displayName"] = "__LZdiffArrays";
return $lzsc$temp
})(), "updateData", (function  () {
var $lzsc$temp = function  () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function  () {
var $lzsc$temp = function  (recursive_$1) {
switch (arguments.length) {
case 0:
recursive_$1 = false
};
var cls_$2 = this.clones;
var len_$3 = cls_$2.length;
for (var i_$4 = 0;i_$4 < len_$3;i_$4++) {
cls_$2[i_$4].datapath.updateData()
}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})()], null);
lz.ReplicationManager = LzReplicationManager;
Class.make("LzRefNode", LzNode, ["__LZxpathconstr", null, "__LZxpathdepend", null, "xpath", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (parent_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$1, attrs_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$lzc$set_xpath", (function  () {
var $lzsc$temp = function  (v_$1) {
this.parent.$lzc$set_xpath(v_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_xpath";
return $lzsc$temp
})()], null);
Class.make("LzDataAttrBind", LzDatapointer, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (ndpath_$1, attr_$2, path_$3, type_$4) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, ndpath_$1);
this.type = type_$4;
this.setAttr = attr_$2;
this.pathparent = ndpath_$1;
this.node = ndpath_$1.immediateparent;
this.setXPath(path_$3);
this.rerunxpath = true;
if (ndpath_$1.__LZdepChildren == null) {
ndpath_$1.__LZdepChildren = [this]
} else {
ndpath_$1.__LZdepChildren.push(this)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$pathbinding", true, "setAttr", void 0, "pathparent", void 0, "node", void 0, "type", void 0, "__LZsendUpdate", (function  () {
var $lzsc$temp = function  (upd_$1, upp_$2) {
switch (arguments.length) {
case 0:
upd_$1 = false;;case 1:
upp_$2 = false
};
var pchg_$3 = this.__LZpchanged;
var result_$4 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsendUpdate"] || this.nextMethod(arguments.callee, "__LZsendUpdate")).call(this, upd_$1, upp_$2);
if (result_$4) {
var data_$5 = this.data;
var node_$6 = this.node;
var attr_$7 = this.setAttr;
if (data_$5 == null) {
data_$5 = null
};
var newvalue_$8 = node_$6.acceptTypeValue(this.type, data_$5);
if (pchg_$3 || node_$6[attr_$7] !== newvalue_$8 || !node_$6.inited || this.parsedPath.operator == "attributes") {
if (!node_$6.__LZdeleted) {
var $lzsc$270662287 = "$lzc$set_" + attr_$7;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(node_$6[$lzsc$270662287]) : node_$6[$lzsc$270662287] instanceof Function) {
node_$6[$lzsc$270662287](newvalue_$8)
} else {
node_$6[attr_$7] = newvalue_$8;
var $lzsc$1036128654 = node_$6["on" + attr_$7];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1036128654) : $lzsc$1036128654 instanceof LzEvent) {
if ($lzsc$1036128654.ready) {
$lzsc$1036128654.sendEvent(newvalue_$8)
}}}}}};
return result_$4
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "unregisterAll", (function  () {
var $lzsc$temp = function  () {
var dca_$1 = this.pathparent.__LZdepChildren;
if (dca_$1 != null) {
for (var i_$2 = 0;i_$2 < dca_$1.length;i_$2++) {
if (dca_$1[i_$2] === this) {
dca_$1.splice(i_$2, 1);
break
}}};
this.destroy()
};
$lzsc$temp["displayName"] = "unregisterAll";
return $lzsc$temp
})(), "setDataContext", (function  () {
var $lzsc$temp = function  (dc_$1, implicit_$2) {
switch (arguments.length) {
case 1:
implicit_$2 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, dc_$1 || this.pathparent, implicit_$2)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "updateData", (function  () {
var $lzsc$temp = function  () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function  () {
var $lzsc$temp = function  (recursive_$1) {
switch (arguments.length) {
case 0:
recursive_$1 = false
};
var ppdo_$2 = this.parsedPath.operator;
if (ppdo_$2 != null) {
var dat_$3 = this.node.presentAttribute(this.setAttr, this.type);
if (this.data != dat_$3) {
if (ppdo_$2 == "name") {
this.setNodeName(dat_$3)
} else {
if (ppdo_$2 == "text") {
this.setNodeText(dat_$3)
} else {
if (ppdo_$2 == "attributes") {
this.p.$lzc$set_attributes(dat_$3)
} else {
this.setNodeAttribute(ppdo_$2.substring(11), dat_$3)
}}}}}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "binder " + this.xpath
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzDataAttrBind.prototype._dbg_name = (function  () {
var $lzsc$temp = function  () {
return Debug.formatToString('%w.%s="$path{%w}"', this.node, this.setAttr, this.xpath)
};
$lzsc$temp["displayName"] = "LzDataAttrBind.prototype._dbg_name";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "data/LzDataAttrBind.lzs#12/1";
return $lzsc$temp
})()(LzDataAttrBind);
Class.make("LzLazyReplicationManager", LzReplicationManager, ["sizeAxis", void 0, "cloneimmediateparent", void 0, "updateDel", void 0, "__LZoldnodelen", void 0, "viewsize", 0, "totalsize", 0, "mask", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$1, args_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function  () {
var $lzsc$temp = function  () {
return true
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2) {
if (args_$2.pooling != null) {
args_$2.pooling = true;
Debug.warn("Invalid pooling argument specified " + "with lazy replication in %w", this)
};
if (args_$2.axis != null) {
this.axis = args_$2.axis
};
this.sizeAxis = this.axis == "x" ? "width" : "height";
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, odp_$1, args_$2);
this.mask = odp_$1.immediateparent.immediateparent.mask;
var cloneopt_$3;
if (this.cloneAttrs.options != null) {
cloneopt_$3 = new LzInheritedHash(this.cloneAttrs.options);
cloneopt_$3["ignorelayout"] = true
} else {
cloneopt_$3 = {ignorelayout: true}};
var firstcl_$4 = this.clones[0];
if (firstcl_$4) {
firstcl_$4.setOption("ignorelayout", true);
var layo_$5 = firstcl_$4.immediateparent.layouts;
if (layo_$5 != null) {
for (var i_$6 = 0;i_$6 < layo_$5.length;i_$6++) {
layo_$5[i_$6].removeSubview(firstcl_$4)
}}};
this.cloneAttrs.options = cloneopt_$3;
var v_$7 = this.getNewClone(true);
this.cloneimmediateparent = v_$7.immediateparent;
if (this.initialnodes) {
v_$7.datapath.setClonePointer(this.initialnodes[1])
};
this.viewsize = v_$7[this.sizeAxis];
v_$7.datapath.setClonePointer(null);
this.clones.push(v_$7);
if (args_$2.spacing == null) {
args_$2.spacing = 0
};
this.totalsize = this.viewsize + args_$2.spacing;
var $lzsc$2106456963 = this.axis;
var $lzsc$897864328 = this.totalsize;
if (!v_$7.__LZdeleted) {
var $lzsc$2044967555 = "$lzc$set_" + $lzsc$2106456963;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(v_$7[$lzsc$2044967555]) : v_$7[$lzsc$2044967555] instanceof Function) {
v_$7[$lzsc$2044967555]($lzsc$897864328)
} else {
v_$7[$lzsc$2106456963] = $lzsc$897864328;
var $lzsc$1836183566 = v_$7["on" + $lzsc$2106456963];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1836183566) : $lzsc$1836183566 instanceof LzEvent) {
if ($lzsc$1836183566.ready) {
$lzsc$1836183566.sendEvent($lzsc$897864328)
}}}};
this.__LZdataoffset = 0;
this.updateDel = new LzDelegate(this, "__LZhandleUpdate");
this.updateDel.register(this.cloneimmediateparent, "on" + this.axis);
this.updateDel.register(this.mask, "on" + this.sizeAxis)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZhandleUpdate", (function  () {
var $lzsc$temp = function  (ignore_$1) {
this.__LZadjustVisibleClones(null, null)
};
$lzsc$temp["displayName"] = "__LZhandleUpdate";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function  () {
var $lzsc$temp = function  () {
var cloneopt_$1;
if (this.cloneAttrs.options != null) {
cloneopt_$1 = new LzInheritedHash(this.cloneAttrs.options);
cloneopt_$1["ignorelayout"] = true
} else {
cloneopt_$1 = {ignorelayout: true}};
this.cloneAttrs.options = cloneopt_$1
};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function  () {
var $lzsc$temp = function  () {
this.__LZHandleMultiNodes([])
};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function  () {
var $lzsc$temp = function  (lastnodes_$1, newnodes_$2) {
var cloneip_$3 = this.cloneimmediateparent;
var _nodes_$4 = this.nodes;
var _axis_$5 = this.axis;
var _sizeAxis_$6 = this.sizeAxis;
var _totalsize_$7 = this.totalsize;
if (_nodes_$4) {
var nodelen_$8 = _nodes_$4.length;
if (this.__LZoldnodelen != nodelen_$8) {
var $lzsc$270494986 = nodelen_$8 * _totalsize_$7 - this.spacing;
if (!cloneip_$3.__LZdeleted) {
var $lzsc$564540103 = "$lzc$set_" + _sizeAxis_$6;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cloneip_$3[$lzsc$564540103]) : cloneip_$3[$lzsc$564540103] instanceof Function) {
cloneip_$3[$lzsc$564540103]($lzsc$270494986)
} else {
cloneip_$3[_sizeAxis_$6] = $lzsc$270494986;
var $lzsc$409563546 = cloneip_$3["on" + _sizeAxis_$6];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$409563546) : $lzsc$409563546 instanceof LzEvent) {
if ($lzsc$409563546.ready) {
$lzsc$409563546.sendEvent($lzsc$270494986)
}}}};
this.__LZoldnodelen = nodelen_$8
}};
if (!(this.mask && this.mask["hasset" + _sizeAxis_$6])) {
return
};
var newstart_$9 = 0;
if (_totalsize_$7 != 0) {
newstart_$9 = Math.floor(-cloneip_$3[_axis_$5] / _totalsize_$7);
if (0 > newstart_$9) {
newstart_$9 = 0
}};
var oldstart_$10 = 0;
var oldlength_$11 = this.clones.length;
var offset_$12 = newstart_$9 - this.__LZdataoffset;
var remainder_$13 = newstart_$9 * _totalsize_$7 + cloneip_$3[_axis_$5];
var newlength_$14 = 0;
if (typeof remainder_$13 == "number") {
newlength_$14 = 1 + Math.floor((this.mask[_sizeAxis_$6] - remainder_$13) / _totalsize_$7)
};
if (_nodes_$4 != null) {
var nodelen_$8 = _nodes_$4.length;
if (newlength_$14 + newstart_$9 > nodelen_$8) {
newlength_$14 = nodelen_$8 - newstart_$9
}};
if (offset_$12 == 0 && newlength_$14 == oldlength_$11) {
return
};
lz.Instantiator.enableDataReplicationQueuing(this);
var oldclones_$15 = this.clones;
this.clones = [];
for (var i_$16 = 0;i_$16 < newlength_$14;i_$16++) {
var cl_$17 = null;
if (i_$16 + offset_$12 < 0) {
if (newlength_$14 + offset_$12 < oldlength_$11 && oldlength_$11 > 0) {
cl_$17 = oldclones_$15[--oldlength_$11]
} else {
cl_$17 = this.getNewClone()
}} else {
if (i_$16 + offset_$12 >= oldlength_$11) {
if (oldstart_$10 < offset_$12 && oldstart_$10 < oldlength_$11) {
cl_$17 = oldclones_$15[oldstart_$10++]
} else {
cl_$17 = this.getNewClone()
}}};
if (cl_$17) {
this.clones[i_$16] = cl_$17;
var $lzsc$923546569 = (i_$16 + newstart_$9) * _totalsize_$7;
if (!cl_$17.__LZdeleted) {
var $lzsc$322670226 = "$lzc$set_" + _axis_$5;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$17[$lzsc$322670226]) : cl_$17[$lzsc$322670226] instanceof Function) {
cl_$17[$lzsc$322670226]($lzsc$923546569)
} else {
cl_$17[_axis_$5] = $lzsc$923546569;
var $lzsc$1215912586 = cl_$17["on" + _axis_$5];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1215912586) : $lzsc$1215912586 instanceof LzEvent) {
if ($lzsc$1215912586.ready) {
$lzsc$1215912586.sendEvent($lzsc$923546569)
}}}};
cl_$17.clonenumber = newstart_$9 + i_$16;
if (_nodes_$4) {
cl_$17.datapath.setClonePointer(_nodes_$4[newstart_$9 + i_$16])
};
if (cl_$17.onclonenumber.ready) {
cl_$17.onclonenumber.sendEvent(i_$16)
}} else {
this.clones[i_$16] = oldclones_$15[i_$16 + offset_$12]
}};
var cpool_$18 = this.clonePool;
while (oldstart_$10 < offset_$12 && oldstart_$10 < oldlength_$11) {
var v_$19 = oldclones_$15[oldstart_$10++];
this.detachClone(v_$19);
cpool_$18.push(v_$19)
};
while (oldlength_$11 > newlength_$14 + offset_$12 && oldlength_$11 > 0) {
var v_$19 = oldclones_$15[--oldlength_$11];
this.detachClone(v_$19);
cpool_$18.push(v_$19)
};
this.__LZdataoffset = newstart_$9;
lz.Instantiator.clearDataReplicationQueue(this)
};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
return "Lazy clone manager in " + this.cloneimmediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "getCloneForNode", (function  () {
var $lzsc$temp = function  (p_$1, dontmake_$2) {
switch (arguments.length) {
case 1:
dontmake_$2 = false
};
var cl_$3 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["getCloneForNode"] || this.nextMethod(arguments.callee, "getCloneForNode")).call(this, p_$1) || null;
if (!cl_$3 && !dontmake_$2) {
cl_$3 = this.getNewClone();
cl_$3.datapath.setClonePointer(p_$1);
this.detachClone(cl_$3);
this.clonePool.push(cl_$3)
};
return cl_$3
};
$lzsc$temp["displayName"] = "getCloneForNode";
return $lzsc$temp
})(), "getCloneNumber", (function  () {
var $lzsc$temp = function  (n_$1) {
return this.getCloneForNode(this.nodes[n_$1])
};
$lzsc$temp["displayName"] = "getCloneNumber";
return $lzsc$temp
})()], null);
lz.LazyReplicationManager = LzLazyReplicationManager;
Class.make("LzResizeReplicationManager", LzLazyReplicationManager, ["datasizevar", void 0, "__LZresizeupdating", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 2:
children_$3 = null;;case 3:
instcall_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$1, args_$2, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function  () {
var $lzsc$temp = function  () {
return false
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (odp_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, odp_$1, args_$2);
this.datasizevar = "$" + this.getUID() + "track"
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZHandleCloneResize", (function  () {
var $lzsc$temp = function  (s_$1) {
var p_$2 = this.datapath.p;
if (p_$2) {
var cloneManager_$3 = this.cloneManager;
var datasizevar_$4 = cloneManager_$3.datasizevar;
var osize_$5 = p_$2.getUserData(datasizevar_$4) || cloneManager_$3.viewsize;
if (s_$1 != osize_$5) {
p_$2.setUserData(datasizevar_$4, s_$1);
cloneManager_$3.__LZadjustVisibleClones(null, false)
}}};
$lzsc$temp["displayName"] = "data/LzResizeReplicationManager.lzs#125/29";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsetCloneAttrs"] || this.nextMethod(arguments.callee, "__LZsetCloneAttrs")).call(this);
var cattrs_$1 = this.cloneAttrs;
cattrs_$1.__LZHandleCloneResize = this.__LZHandleCloneResize;
if (!cattrs_$1["$delegates"]) {
cattrs_$1.$delegates = []
};
cattrs_$1.$delegates.push("on" + (this.axis == "y" ? "height" : "width"), "__LZHandleCloneResize", null)
};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "getPositionByNode", (function  () {
var $lzsc$temp = function  (n_$1) {
var pos_$2 = -this.spacing;
var cnode_$3;
if (this.nodes != null) {
for (var i_$4 = 0;i_$4 < this.nodes.length;i_$4++) {
cnode_$3 = this.nodes[i_$4];
if (n_$1 == this.nodes[i_$4]) {
return pos_$2 + this.spacing
};
pos_$2 += this.spacing + (cnode_$3.getUserData(this.datasizevar) || this.viewsize)
}};
return undefined
};
$lzsc$temp["displayName"] = "getPositionByNode";
return $lzsc$temp
})(), "__LZreleaseClone", (function  () {
var $lzsc$temp = function  (v_$1) {
this.detachClone(v_$1);
this.clonePool.push(v_$1)
};
$lzsc$temp["displayName"] = "__LZreleaseClone";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function  () {
var $lzsc$temp = function  (lastnodes_$1, newnodes_$2) {
if (!this.mask["hasset" + this.sizeAxis]) {
return
};
if (this.__LZresizeupdating) {
return
};
this.__LZresizeupdating = true;
var nodelen_$3 = this.nodes != null ? this.nodes.length : 0;
var newstart_$4 = Math.floor(-this.cloneimmediateparent[this.axis]);
if (0 > newstart_$4) {
newstart_$4 = 0
};
var masksize_$5 = this.mask[this.sizeAxis];
var newoffset_$6 = -1;
var oldoffset_$7 = this.__LZdataoffset;
if (newnodes_$2) {
while (this.clones.length) {
this.poolClone()
};
var oldclones_$8 = null;
var ocl_$9 = 0
} else {
var oldclones_$8 = this.clones;
var ocl_$9 = oldclones_$8.length
};
this.clones = [];
var cpos_$10 = -this.spacing;
var inwindow_$11 = false;
var newend_$12 = -1;
var newstartpos_$13;
var notfirst_$14 = true;
for (var i_$15 = 0;i_$15 < nodelen_$3;i_$15++) {
var cnode_$16 = this.nodes[i_$15];
var ds_$17 = cnode_$16.getUserData(this.datasizevar);
var csiz_$18 = ds_$17 == null ? this.viewsize : ds_$17;
cpos_$10 += this.spacing;
if (!inwindow_$11 && newoffset_$6 == -1 && cpos_$10 - newstart_$4 + csiz_$18 >= 0) {
notfirst_$14 = i_$15 != 0;
inwindow_$11 = true;
newstartpos_$13 = cpos_$10;
newoffset_$6 = i_$15;
var firstkept_$19 = i_$15 - oldoffset_$7;
firstkept_$19 = firstkept_$19 > ocl_$9 ? ocl_$9 : firstkept_$19;
if (firstkept_$19 > 0) {
for (var j_$20 = 0;j_$20 < firstkept_$19;j_$20++) {
var v_$21 = oldclones_$8[j_$20];
this.__LZreleaseClone(v_$21)
}}} else {
if (inwindow_$11 && cpos_$10 - newstart_$4 > masksize_$5) {
inwindow_$11 = false;
newend_$12 = i_$15 - newoffset_$6;
var lastkept_$22 = i_$15 - oldoffset_$7;
lastkept_$22 = lastkept_$22 < 0 ? 0 : lastkept_$22;
if (lastkept_$22 < ocl_$9) {
for (var j_$20 = lastkept_$22;j_$20 < ocl_$9;j_$20++) {
var v_$21 = oldclones_$8[j_$20];
this.__LZreleaseClone(v_$21)
}}}};
if (inwindow_$11) {
if (i_$15 >= oldoffset_$7 && i_$15 < oldoffset_$7 + ocl_$9) {
var cl_$23 = oldclones_$8[i_$15 - oldoffset_$7]
} else {
var cl_$23 = null
};
this.clones[i_$15 - newoffset_$6] = cl_$23
};
cpos_$10 += csiz_$18
};
var clpos_$24 = newstartpos_$13;
if (notfirst_$14) {
clpos_$24 += this.spacing
};
for (var i_$15 = 0;i_$15 < this.clones.length;i_$15++) {
var cnode_$16 = this.nodes[i_$15 + newoffset_$6];
var cl_$23 = this.clones[i_$15];
if (!cl_$23) {
cl_$23 = this.getNewClone();
cl_$23.clonenumber = i_$15 + newoffset_$6;
cl_$23.datapath.setClonePointer(cnode_$16);
if (cl_$23.onclonenumber.ready) {
cl_$23.onclonenumber.sendEvent(i_$15 + newoffset_$6)
};
this.clones[i_$15] = cl_$23
};
this.clones[i_$15] = cl_$23;
var $lzsc$1054179104 = this.axis;
if (!cl_$23.__LZdeleted) {
var $lzsc$1782836662 = "$lzc$set_" + $lzsc$1054179104;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$23[$lzsc$1782836662]) : cl_$23[$lzsc$1782836662] instanceof Function) {
cl_$23[$lzsc$1782836662](clpos_$24)
} else {
cl_$23[$lzsc$1054179104] = clpos_$24;
var $lzsc$1470281739 = cl_$23["on" + $lzsc$1054179104];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1470281739) : $lzsc$1470281739 instanceof LzEvent) {
if ($lzsc$1470281739.ready) {
$lzsc$1470281739.sendEvent(clpos_$24)
}}}};
var ds_$17 = cnode_$16.getUserData(this.datasizevar);
var csiz_$18 = ds_$17 == null ? this.viewsize : ds_$17;
if (cl_$23[this.sizeAxis] != csiz_$18) {
var $lzsc$2088038050 = this.sizeAxis;
if (!cl_$23.__LZdeleted) {
var $lzsc$1945758709 = "$lzc$set_" + $lzsc$2088038050;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$23[$lzsc$1945758709]) : cl_$23[$lzsc$1945758709] instanceof Function) {
cl_$23[$lzsc$1945758709](csiz_$18)
} else {
cl_$23[$lzsc$2088038050] = csiz_$18;
var $lzsc$240349529 = cl_$23["on" + $lzsc$2088038050];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$240349529) : $lzsc$240349529 instanceof LzEvent) {
if ($lzsc$240349529.ready) {
$lzsc$240349529.sendEvent(csiz_$18)
}}}}};
clpos_$24 += csiz_$18 + this.spacing
};
this.__LZdataoffset = newoffset_$6;
var $lzsc$1523315763 = this.cloneimmediateparent;
var $lzsc$1437218829 = this.sizeAxis;
if (!$lzsc$1523315763.__LZdeleted) {
var $lzsc$1710795753 = "$lzc$set_" + $lzsc$1437218829;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa($lzsc$1523315763[$lzsc$1710795753]) : $lzsc$1523315763[$lzsc$1710795753] instanceof Function) {
$lzsc$1523315763[$lzsc$1710795753](cpos_$10)
} else {
$lzsc$1523315763[$lzsc$1437218829] = cpos_$10;
var $lzsc$39371592 = $lzsc$1523315763["on" + $lzsc$1437218829];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$39371592) : $lzsc$39371592 instanceof LzEvent) {
if ($lzsc$39371592.ready) {
$lzsc$39371592.sendEvent(cpos_$10)
}}}};
this.__LZresizeupdating = false
};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})()], null);
lz.ResizeReplicationManager = LzResizeReplicationManager;
Class.make("LzColorUtils", null, null, ["stringToColor", (function  () {
var $lzsc$temp = function  (s_$1) {
if (typeof s_$1 != "string") {
return s_$1
};
if (lz.colors[s_$1] != null) {
return lz.colors[s_$1]
};
if (global[s_$1] != null) {
return global[s_$1]
};
if (s_$1.indexOf("rgb") != -1) {
return LzColorUtils.fromrgb(s_$1)
};
var n_$2 = Number(s_$1);
if (isNaN(n_$2)) {
return s_$1
} else {
return n_$2
}};
$lzsc$temp["displayName"] = "stringToColor";
return $lzsc$temp
})(), "fromrgb", (function  () {
var $lzsc$temp = function  (s_$1) {
if (typeof s_$1 != "string") {
return s_$1
};
if (s_$1.indexOf("rgb") == -1) {
return LzColorUtils.stringToColor(s_$1)
};
var parts_$2 = s_$1.substring(s_$1.indexOf("(") + 1, s_$1.indexOf(")")).split(",");
var color_$3 = (parts_$2[0] << 16) + (parts_$2[1] << 8) + parts_$2[2] * 1;
if (parts_$2.length > 3) {
color_$3 += parts_$2[3] * 0.01
};
if (typeof color_$3 == "number") {
return color_$3
};
Debug.warn("invalid color string: " + s_$1);
return 0
};
$lzsc$temp["displayName"] = "fromrgb";
return $lzsc$temp
})(), "dectohex", (function  () {
var $lzsc$temp = function  (n_$1, p_$2) {
switch (arguments.length) {
case 1:
p_$2 = 0
};
if (typeof n_$1 != "number") {
return n_$1
};
n_$1 = n_$1 & 16777215;
var hex_$3 = n_$1.toString(16);
var pad_$4 = p_$2 - hex_$3.length;
while (pad_$4 > 0) {
hex_$3 = "0" + hex_$3;
pad_$4--
};
return hex_$3
};
$lzsc$temp["displayName"] = "dectohex";
return $lzsc$temp
})(), "torgb", (function  () {
var $lzsc$temp = function  (s_$1) {
if (typeof s_$1 == "string" && s_$1.indexOf("rgb") != -1) {
return s_$1
};
var n_$2 = LzColorUtils.inttohex(s_$1);
if (typeof n_$2 != "string") {
return n_$2
};
if (typeof s_$1 == "number" || lz.colors[s_$1] != null) {
s_$1 = n_$2
};
if (s_$1.length < 6) {
s_$1 = "#" + s_$1.charAt(1) + s_$1.charAt(1) + s_$1.charAt(2) + s_$1.charAt(2) + s_$1.charAt(3) + s_$1.charAt(3) + (s_$1.length > 4 ? s_$1.charAt(4) + s_$1.charAt(4) : "")
};
return (s_$1.length > 7 ? "rgba(" : "rgb(") + parseInt(s_$1.substring(1, 3), 16) + "," + parseInt(s_$1.substring(3, 5), 16) + "," + parseInt(s_$1.substring(5, 7), 16) + (s_$1.length > 7 ? "," + parseInt(s_$1.substring(7), 16) : "") + ")"
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})(), "tohsv", (function  () {
var $lzsc$temp = function  (rgb_$1) {
var r_$2 = (rgb_$1 >> 16 & 255) / 255, g_$3 = (rgb_$1 >> 8 & 255) / 255, b_$4 = (rgb_$1 & 255) / 255;
var min_$5 = Math.min(r_$2, Math.min(g_$3, b_$4)), max_$6 = Math.max(r_$2, Math.max(g_$3, b_$4));
var v_$7 = max_$6;
var delta_$8 = max_$6 - min_$5;
if (delta_$8 == 0) {
return {h: 0, s: 0, v: v_$7}};
var s_$9 = delta_$8 / max_$6;
if (r_$2 == max_$6) {
var h_$10 = (g_$3 - b_$4) / delta_$8
} else {
if (g_$3 == max_$6) {
var h_$10 = 2 + (b_$4 - r_$2) / delta_$8
} else {
var h_$10 = 4 + (r_$2 - g_$3) / delta_$8
}};
h_$10 *= 60;
if (h_$10 < 0) {
h_$10 += 360
};
return {h: h_$10, s: s_$9, v: v_$7}};
$lzsc$temp["displayName"] = "tohsv";
return $lzsc$temp
})(), "fromhsv", (function  () {
var $lzsc$temp = function  (h_$1, s_$2, v_$3) {
var t_$4 = h_$1 / 60;
var ti_$5 = Math.floor(t_$4);
var hi_$6 = ti_$5 % 6;
var f_$7 = t_$4 - ti_$5;
var p_$8 = v_$3 * (1 - s_$2);
var q_$9 = v_$3 * (1 - f_$7 * s_$2);
var t_$4 = v_$3 * (1 - (1 - f_$7) * s_$2);
var r_$10, g_$11, b_$12;
switch (hi_$6) {
case 0:
r_$10 = v_$3;
g_$11 = t_$4;
b_$12 = p_$8;
break;;case 1:
r_$10 = q_$9;
g_$11 = v_$3;
b_$12 = p_$8;
break;;case 2:
r_$10 = p_$8;
g_$11 = v_$3;
b_$12 = t_$4;
break;;case 3:
r_$10 = p_$8;
g_$11 = q_$9;
b_$12 = v_$3;
break;;case 4:
r_$10 = t_$4;
g_$11 = p_$8;
b_$12 = v_$3;
break;;case 5:
r_$10 = v_$3;
g_$11 = p_$8;
b_$12 = q_$9;
break
};
return r_$10 * 255 << 16 | g_$11 * 255 << 8 | b_$12 * 255
};
$lzsc$temp["displayName"] = "fromhsv";
return $lzsc$temp
})(), "convertColor", (function  () {
var $lzsc$temp = function  (s_$1) {
if (s_$1 == "null" || s_$1 == null) {
return null
};
return LzColorUtils.hextoint(s_$1)
};
$lzsc$temp["displayName"] = "convertColor";
return $lzsc$temp
})(), "hextoint", (function  () {
var $lzsc$temp = function  (s_$1) {
var n_$2 = LzColorUtils.stringToColor(s_$1);
if (typeof n_$2 != "string") {
return n_$2
};
var hex_$3 = s_$1;
hex_$3 = hex_$3.slice(1);
var alpha_$4 = 0;
if (hex_$3.length > 6) {
alpha_$4 = parseInt(hex_$3.slice(6), 16) / 25500;
hex_$3 = hex_$3.slice(0, 6)
};
var n_$2 = parseInt(hex_$3, 16);
switch (hex_$3.length) {
case 3:
return ((n_$2 & 3840) << 8 | (n_$2 & 240) << 4 | n_$2 & 15) * 17 + alpha_$4;;case 6:
return n_$2 + alpha_$4;;default:
break
};
Debug.warn("invalid color string: " + s_$1);
return 0
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), "inttohex", (function  () {
var $lzsc$temp = function  (n_$1, p_$2) {
switch (arguments.length) {
case 1:
p_$2 = 6
};
var s_$3 = LzColorUtils.stringToColor(n_$1);
if (typeof s_$3 != "number") {
return s_$3
};
return "#" + LzColorUtils.dectohex(s_$3, p_$2)
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})()]);
Class.make("LzUtilsClass", null, ["__SimpleExprPattern", void 0, "__ElementPattern", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
this.__SimpleExprPattern = new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$");
this.__ElementPattern = new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)", "g")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "color", {hextoint: (function  () {
var $lzsc$temp = function  (value_$1) {
Debug.warn("lz.Utils.color.hextoint() is deprecated.  Use LzColorUtils.hextoint() instead.");
return LzColorUtils.hextoint(value_$1)
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), inttohex: (function  () {
var $lzsc$temp = function  (c_$1) {
Debug.warn("lz.Utils.color.inttohex() is deprecated.  Use LzColorUtils.inttohex() instead.");
return LzColorUtils.inttohex(c_$1)
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})(), torgb: (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.warn("lz.Utils.color.torgb() is deprecated.  Use LzColorUtils.torgb() instead.");
return LzColorUtils.torgb(s_$1)
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})()}, "hextoint", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, LzColorUtils.hextoint);
return LzColorUtils.hextoint(s_$1)
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), "inttohex", (function  () {
var $lzsc$temp = function  (n_$1, p_$2) {
switch (arguments.length) {
case 1:
p_$2 = 6
};
Debug.deprecated(this, arguments.callee, LzColorUtils.inttohex);
return LzColorUtils.inttohex(n_$1, p_$2)
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})(), "dectohex", (function  () {
var $lzsc$temp = function  (n_$1, p_$2) {
switch (arguments.length) {
case 1:
p_$2 = 0
};
return LzColorUtils.dectohex(n_$1, p_$2)
};
$lzsc$temp["displayName"] = "dectohex";
return $lzsc$temp
})(), "stringToColor", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, LzColorUtils.stringToColor);
return LzColorUtils.stringToColor(s_$1)
};
$lzsc$temp["displayName"] = "stringToColor";
return $lzsc$temp
})(), "torgb", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, LzColorUtils.torgb);
return LzColorUtils.torgb(s_$1)
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})(), "fromrgb", (function  () {
var $lzsc$temp = function  (s_$1) {
Debug.deprecated(this, arguments.callee, LzColorUtils.fromrgb);
return LzColorUtils.fromrgb(s_$1)
};
$lzsc$temp["displayName"] = "fromrgb";
return $lzsc$temp
})(), "colornames", lz.colors, "__unpackList", (function  () {
var $lzsc$temp = function  (argstr_$1, scope_$2) {
switch (arguments.length) {
case 1:
scope_$2 = null
};
if (argstr_$1 == "") {
return []
};
if (scope_$2 == null) {
scope_$2 = canvas
};
var args_$3 = argstr_$1.split(",");
for (var i_$4 = 0;i_$4 < args_$3.length;i_$4++) {
var a_$5 = args_$3[i_$4];
if (a_$5 == "") {
continue
};
while (a_$5.charAt(0) == " ") {
a_$5 = a_$5.substring(1, a_$5.length)
};
var n_$6 = parseFloat(a_$5);
if (!isNaN(n_$6)) {
args_$3[i_$4] = n_$6
} else {
if (a_$5.indexOf("'") != -1) {
var s_$7 = a_$5.indexOf("'") + 1;
var e_$8 = a_$5.lastIndexOf("'");
args_$3[i_$4] = a_$5.substring(s_$7, e_$8)
} else {
if (a_$5 == "true" || a_$5 == "false") {
args_$3[i_$4] = a_$5 == "true"
} else {
if (scope_$2[a_$5]) {
args_$3[i_$4] = scope_$2[a_$5]
}}}}};
return args_$3
};
$lzsc$temp["displayName"] = "__unpackList";
return $lzsc$temp
})(), "safeEval", (function  () {
var $lzsc$temp = function  (js_$1) {
if (js_$1.indexOf("new ") == 0) {
return this.safeNew(js_$1)
};
var s_$2 = js_$1.indexOf("(");
var argstr_$3 = null;
if (s_$2 != -1) {
var e_$4 = js_$1.lastIndexOf(")");
argstr_$3 = js_$1.substring(s_$2 + 1, e_$4);
js_$1 = js_$1.substring(0, s_$2)
};
var scope_$5 = null, val_$6;
if (js_$1.match(this.__SimpleExprPattern)) {
var parts_$7 = js_$1.match(this.__ElementPattern);
val_$6 = globalValue(parts_$7[0]);
for (var i_$8 = 1, l_$9 = parts_$7.length;i_$8 < l_$9;i_$8++) {
scope_$5 = val_$6;
val_$6 = val_$6[parts_$7[i_$8]]
}};
if (argstr_$3 == null) {
return val_$6
};
var args_$10 = lz.Utils.__unpackList(argstr_$3, scope_$5);
if (val_$6) {
var result_$11 = val_$6.apply(scope_$5, args_$10);
return result_$11
}};
$lzsc$temp["displayName"] = "safeEval";
return $lzsc$temp
})(), "safeNew", (function  () {
var $lzsc$temp = function  (js_$1) {
var orig_$2 = js_$1;
var newpos_$3 = js_$1.indexOf("new ");
if (newpos_$3 == -1) {
return js_$1
};
js_$1 = js_$1.substring(newpos_$3 + 4);
var s_$4 = js_$1.indexOf("(");
if (s_$4 != -1) {
var e_$5 = js_$1.indexOf(")");
var args_$6 = js_$1.substring(s_$4 + 1, e_$5);
js_$1 = js_$1.substring(0, s_$4)
};
var obj_$7 = globalValue(js_$1);
if (!obj_$7) {
return
};
var args_$6 = lz.Utils.__unpackList(args_$6);
var size_$8 = args_$6.length;
if (size_$8 == 0) {
return new obj_$7()
} else {
if (size_$8 == 1) {
return new obj_$7(args_$6[0])
} else {
if (size_$8 == 2) {
return new obj_$7(args_$6[0], args_$6[1])
} else {
if (size_$8 == 3) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2])
} else {
if (size_$8 == 4) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3])
} else {
if (size_$8 == 5) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4])
} else {
if (size_$8 == 6) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5])
} else {
if (size_$8 == 7) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5], args_$6[6])
} else {
if (size_$8 == 8) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5], args_$6[6], args_$6[7])
} else {
if (size_$8 == 9) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5], args_$6[6], args_$6[7], args_$6[8])
} else {
if (size_$8 == 10) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5], args_$6[6], args_$6[7], args_$6[8], args_$6[9])
} else {
if (size_$8 == 11) {
return new obj_$7(args_$6[0], args_$6[1], args_$6[2], args_$6[3], args_$6[4], args_$6[5], args_$6[6], args_$6[7], args_$6[8], args_$6[9], args_$6[10])
} else {
Debug.warn("Too many arguments", args_$6)
}}}}}}}}}}}}};
$lzsc$temp["displayName"] = "safeNew";
return $lzsc$temp
})()], null);
lz.Utils = new LzUtilsClass();
var LzUtils = lz.Utils;
Class.make("LzInstantiatorService", LzEventable, ["checkQDel", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.checkQDel = new LzDelegate(this, "checkQ")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "halted", false, "isimmediate", false, "isdatareplicating", false, "istrickling", false, "isUpdating", false, "safe", true, "timeout", 500, "makeQ", [], "trickleQ", [], "tricklingQ", [], "datareplQ", null, "dataQ", [], "syncNew", true, "trickletime", 10, "setSafeInstantiation", (function  () {
var $lzsc$temp = function  (isSafe_$1) {
this.safe = isSafe_$1;
if (this.instanceQ.length) {
this.timeout = Infinity
}};
$lzsc$temp["displayName"] = "setSafeInstantiation";
return $lzsc$temp
})(), "requestInstantiation", (function  () {
var $lzsc$temp = function  (v_$1, children_$2) {
if (this.isimmediate) {
this.createImmediate(v_$1, children_$2.concat())
} else {
var c_$3 = this.newReverseArray(children_$2);
if (this.isdatareplicating) {
this.datareplQ.push(c_$3, v_$1)
} else {
if (this.istrickling) {
this.tricklingQ.push(v_$1, c_$3)
} else {
this.makeQ.push(v_$1, c_$3);
this.checkUpdate()
}}}};
$lzsc$temp["displayName"] = "requestInstantiation";
return $lzsc$temp
})(), "enableDataReplicationQueuing", (function  () {
var $lzsc$temp = function  (rman_$1) {
if (this.isdatareplicating) {
this.dataQ.push(this.datareplQ)
} else {
this.isdatareplicating = true
};
this.datareplQ = [];
this.datareplQ.push(rman_$1)
};
$lzsc$temp["displayName"] = "enableDataReplicationQueuing";
return $lzsc$temp
})(), "clearDataReplicationQueue", (function  () {
var $lzsc$temp = function  (rman_$1) {
var drq_$2 = this.datareplQ;
if (this.dataQ.length > 0) {
this.datareplQ = this.dataQ.pop()
} else {
this.isdatareplicating = false;
this.datareplQ = null
};
if (drq_$2.shift() !== rman_$1) {
Debug.error("%w.clearDataReplicationQueue: expected %w", this, rman_$1)
};
var cpar_$3 = rman_$1.cloneParent;
var mq_$4 = this.makeQ;
var mqlen_$5 = mq_$4.length;
var offset_$6 = mqlen_$5;
for (var i_$7 = 0;i_$7 < mqlen_$5;i_$7 += 2) {
if (mq_$4[i_$7].parent === cpar_$3) {
offset_$6 = i_$7;
break
}};
drq_$2.push(0, offset_$6);
drq_$2.reverse();
mq_$4.splice.apply(mq_$4, drq_$2);
this.checkUpdate()
};
$lzsc$temp["displayName"] = "clearDataReplicationQueue";
return $lzsc$temp
})(), "newReverseArray", (function  () {
var $lzsc$temp = function  (arr_$1) {
var n_$2 = arr_$1.length;
var a_$3 = Array(n_$2);
for (var i_$4 = 0, j_$5 = n_$2 - 1;i_$4 < n_$2;) {
a_$3[i_$4++] = arr_$1[j_$5--]
};
return a_$3
};
$lzsc$temp["displayName"] = "newReverseArray";
return $lzsc$temp
})(), "checkUpdate", (function  () {
var $lzsc$temp = function  () {
if (!(this.isUpdating || this.halted)) {
this.checkQDel.register(lz.Idle, "onidle");
this.isUpdating = true
}};
$lzsc$temp["displayName"] = "checkUpdate";
return $lzsc$temp
})(), "checkQ", (function  () {
var $lzsc$temp = function  (ignoreme_$1) {
switch (arguments.length) {
case 0:
ignoreme_$1 = null
};
if (!this.makeQ.length) {
if (!this.tricklingQ.length) {
if (!this.trickleQ.length) {
this.checkQDel.unregisterAll();
this.isUpdating = false;
return
} else {
var p_$2 = this.trickleQ.shift();
var c_$3 = this.trickleQ.shift();
this.tricklingQ.push(p_$2, this.newReverseArray(c_$3))
}};
this.istrickling = true;
this.makeSomeViews(this.tricklingQ, this.trickletime);
this.istrickling = false
} else {
canvas.creatednodes += this.makeSomeViews(this.makeQ, this.timeout);
if (canvas.updatePercentCreatedEnabled) {
canvas.updatePercentCreated()
}}};
$lzsc$temp["displayName"] = "checkQ";
return $lzsc$temp
})(), "makeSomeViews", (function  () {
var $lzsc$temp = function  (cq_$1, otime_$2) {
var itime_$3 = new Date().getTime();
var num_$4 = 0;
while (new Date().getTime() - itime_$3 < otime_$2 && cq_$1.length) {
var len_$5 = cq_$1.length;
var larr_$6 = cq_$1[len_$5 - 1];
var par_$7 = cq_$1[len_$5 - 2];
var parDone_$8 = false;
if (par_$7["__LZdeleted"] || larr_$6[0] && larr_$6[0]["__LZdeleted"]) {
cq_$1.length -= 2;
continue
};
try {
while (new Date().getTime() - itime_$3 < otime_$2) {
if (len_$5 != cq_$1.length) {
break
};
if (!larr_$6.length) {
parDone_$8 = true;
break
};
var c_$12 = larr_$6.pop();
if (c_$12) {
par_$7.makeChild(c_$12, true);
num_$4++
}}}
finally {

};
if (parDone_$8) {
cq_$1.length = len_$5 - 2;
par_$7.__LZinstantiationDone()
}};
return num_$4
};
$lzsc$temp["displayName"] = "makeSomeViews";
return $lzsc$temp
})(), "trickleInstantiate", (function  () {
var $lzsc$temp = function  (v_$1, children_$2) {
this.trickleQ.push(v_$1, children_$2);
this.checkUpdate()
};
$lzsc$temp["displayName"] = "trickleInstantiate";
return $lzsc$temp
})(), "createImmediate", (function  () {
var $lzsc$temp = function  (v_$1, children_$2) {
var c_$3 = this.newReverseArray(children_$2);
var wasimmediate_$4 = this.isimmediate;
this.isimmediate = true;
this.makeSomeViews([v_$1, c_$3], Infinity);
this.isimmediate = wasimmediate_$4
};
$lzsc$temp["displayName"] = "createImmediate";
return $lzsc$temp
})(), "completeTrickle", (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.tricklingQ[0] == v_$1) {
var wasimmediate_$2 = this.isimmediate;
this.isimmediate = true;
this.makeSomeViews(this.tricklingQ, Infinity);
this.isimmediate = wasimmediate_$2;
this.tricklingQ = []
} else {
var tq_$3 = this.trickleQ;
var tql_$4 = tq_$3.length;
for (var i_$5 = 0;i_$5 < tql_$4;i_$5 += 2) {
if (tq_$3[i_$5] == v_$1) {
var dchil_$6 = tq_$3[i_$5 + 1];
tq_$3.splice(i_$5, 2);
this.createImmediate(v_$1, dchil_$6);
return
}}}};
$lzsc$temp["displayName"] = "completeTrickle";
return $lzsc$temp
})(), "traceQ", (function  () {
var $lzsc$temp = function  () {
Debug.info("****start trace");
var mq_$1 = this.makeQ;
for (var i_$2 = 0;i_$2 < mq_$1.length;i_$2 += 2) {
var par_$3 = mq_$1[i_$2];
var larr_$4 = mq_$1[i_$2 + 1];
var s_$5 = "";
for (var k_$6 = 0;k_$6 < larr_$4.length;k_$6++) {
s_$5 += larr_$4[k_$6]["class"].tagname + " |"
};
Debug.write("%w : |%s >>> %s", par_$3, s_$5, par_$3.getUID())
};
Debug.info("****trace done")
};
$lzsc$temp["displayName"] = "traceQ";
return $lzsc$temp
})(), "halt", (function  () {
var $lzsc$temp = function  () {
this.isUpdating = false;
this.halted = true;
this.checkQDel.unregisterAll()
};
$lzsc$temp["displayName"] = "halt";
return $lzsc$temp
})(), "resume", (function  () {
var $lzsc$temp = function  () {
this.halted = false;
this.checkUpdate()
};
$lzsc$temp["displayName"] = "resume";
return $lzsc$temp
})(), "drainQ", (function  () {
var $lzsc$temp = function  (limit_$1) {
var to_$2 = this.timeout;
var tt_$3 = this.trickletime;
var h_$4 = this.halted;
this.timeout = limit_$1;
this.trickletime = limit_$1;
this.halted = false;
this.isUpdating = true;
this.checkQ();
this.halted = h_$4;
this.timeout = to_$2;
this.trickletime = tt_$3;
return !this.isUpdating
};
$lzsc$temp["displayName"] = "drainQ";
return $lzsc$temp
})()], ["LzInstantiator", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzInstantiatorService.LzInstantiator = new LzInstantiatorService()
}}};
$lzsc$temp["displayName"] = "services/LzInstantiator.lzs#34/1";
return $lzsc$temp
})()(LzInstantiatorService);
lz.Instantiator = LzInstantiatorService.LzInstantiator;
Class.make("LzGlobalMouseService", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "onmousemove", LzDeclaredEvent, "onmouseup", LzDeclaredEvent, "onmouseupoutside", LzDeclaredEvent, "onmouseover", LzDeclaredEvent, "onmouseout", LzDeclaredEvent, "onmousedown", LzDeclaredEvent, "onmousedragin", LzDeclaredEvent, "onmousedragout", LzDeclaredEvent, "onmouseleave", LzDeclaredEvent, "onmouseenter", LzDeclaredEvent, "onclick", LzDeclaredEvent, "ondblclick", LzDeclaredEvent, "__movecounter", 0, "__mouseEvent", (function  () {
var $lzsc$temp = function  (eventname_$1, view_$2) {
if (eventname_$1 == "onmouseleave") {
canvas.onmouseleave.sendEvent()
} else {
if (eventname_$1 == "onmousemove") {
this.__movecounter++
}};
var ev_$3 = this[eventname_$1];
if (ev_$3) {
if (ev_$3.ready) {
ev_$3.sendEvent(view_$2)
}} else {
Debug.debug("Unknown mouse event %s", eventname_$1)
}};
$lzsc$temp["displayName"] = "__mouseEvent";
return $lzsc$temp
})()], ["LzGlobalMouse", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzGlobalMouseService.LzGlobalMouse = new LzGlobalMouseService()
}}};
$lzsc$temp["displayName"] = "services/LzGlobalMouse.lzs#81/1";
return $lzsc$temp
})()(LzGlobalMouseService);
lz.GlobalMouseService = LzGlobalMouseService;
lz.GlobalMouse = LzGlobalMouseService.LzGlobalMouse;
Class.make("LzBrowserService", null, ["capabilities", LzSprite.prototype.capabilities, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "loadURL", (function  () {
var $lzsc$temp = function  (url_$1, target_$2, features_$3) {
switch (arguments.length) {
case 1:
target_$2 = null;;case 2:
features_$3 = null
};
LzBrowserKernel.loadURL(url_$1, target_$2, features_$3)
};
$lzsc$temp["displayName"] = "loadURL";
return $lzsc$temp
})(), "loadJS", (function  () {
var $lzsc$temp = function  (js_$1, target_$2) {
switch (arguments.length) {
case 1:
target_$2 = null
};
LzBrowserKernel.loadJS.apply(null, arguments)
};
$lzsc$temp["displayName"] = "loadJS";
return $lzsc$temp
})(), "callJS", (function  () {
var $lzsc$temp = function  (methodname_$1, callback_$2, args_$3) {
switch (arguments.length) {
case 1:
callback_$2 = null;;case 2:
args_$3 = null
};
try {
return LzBrowserKernel.callJS.apply(null, arguments)
}
catch (e) {
Debug.error("lz.Browser.callJS() failed:", e);
return null
}};
$lzsc$temp["displayName"] = "callJS";
return $lzsc$temp
})(), "getVersion", (function  () {
var $lzsc$temp = function  () {
return LzBrowserKernel.getVersion()
};
$lzsc$temp["displayName"] = "getVersion";
return $lzsc$temp
})(), "getOS", (function  () {
var $lzsc$temp = function  () {
return LzBrowserKernel.getOS()
};
$lzsc$temp["displayName"] = "getOS";
return $lzsc$temp
})(), "getLoadURL", (function  () {
var $lzsc$temp = function  () {
return LzBrowserKernel.getLoadURL()
};
$lzsc$temp["displayName"] = "getLoadURL";
return $lzsc$temp
})(), "getInitArg", (function  () {
var $lzsc$temp = function  (name_$1) {
return LzBrowserKernel.getInitArg(name_$1)
};
$lzsc$temp["displayName"] = "getInitArg";
return $lzsc$temp
})(), "getAppID", (function  () {
var $lzsc$temp = function  () {
return LzBrowserKernel.getAppID()
};
$lzsc$temp["displayName"] = "getAppID";
return $lzsc$temp
})(), "showMenu", (function  () {
var $lzsc$temp = function  (truefalse_$1) {
if (this.capabilities.runtimemenus) {
LzBrowserKernel.showMenu(truefalse_$1)
} else {
LzView.__warnCapability("lz.Browser.showMenu()", "runtimemenus")
}};
$lzsc$temp["displayName"] = "showMenu";
return $lzsc$temp
})(), "setClipboard", (function  () {
var $lzsc$temp = function  (str_$1) {
if (this.capabilities.setclipboard) {
LzBrowserKernel.setClipboard(str_$1)
} else {
LzView.__warnCapability("lz.Browser.setClipboard()", "setclipboard")
}};
$lzsc$temp["displayName"] = "setClipboard";
return $lzsc$temp
})(), "isAAActive", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.accessibility) {
return LzBrowserKernel.isAAActive()
} else {
LzView.__warnCapability("lz.Browser.isAAActive()", "accessibility");
return false
}};
$lzsc$temp["displayName"] = "isAAActive";
return $lzsc$temp
})(), "updateAccessibility", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.accessibility) {
LzBrowserKernel.updateAccessibility()
} else {
LzView.__warnCapability("lz.Browser.updateAccessibility()", "accessibility")
}};
$lzsc$temp["displayName"] = "updateAccessibility";
return $lzsc$temp
})(), "loadProxyPolicy", (function  () {
var $lzsc$temp = function  (url_$1) {
if (this.capabilities.proxypolicy) {
LzBrowserKernel.loadProxyPolicy(url_$1)
} else {
LzView.__warnCapability("lz.Browser.loadProxyPolicy()", "proxypolicy")
}};
$lzsc$temp["displayName"] = "loadProxyPolicy";
return $lzsc$temp
})(), "postToLps", true, "parsedloadurl", null, "defaultPortNums", {http: 80, https: 443}, "getBaseURL", (function  () {
var $lzsc$temp = function  (secure_$1, port_$2) {
switch (arguments.length) {
case 0:
secure_$1 = null;;case 1:
port_$2 = null
};
var url_$3 = this.getLoadURLAsLzURL();
if (secure_$1) {
url_$3.protocol = "https"
};
if (port_$2) {
url_$3.port = port_$2
} else {
if (secure_$1 && port_$2 == null) {
url_$3.port = this.defaultPortNums[url_$3.protocol]
}};
url_$3.query = null;
return url_$3
};
$lzsc$temp["displayName"] = "getBaseURL";
return $lzsc$temp
})(), "getLoadURLAsLzURL", (function  () {
var $lzsc$temp = function  () {
if (!this.parsedloadurl) {
this.parsedloadurl = new LzURL(this.getLoadURL())
};
return this.parsedloadurl.dupe()
};
$lzsc$temp["displayName"] = "getLoadURLAsLzURL";
return $lzsc$temp
})(), "toAbsoluteURL", (function  () {
var $lzsc$temp = function  (url_$1, secure_$2) {
if (url_$1.indexOf("://") > -1 || url_$1.indexOf("/@WEBAPP@/") == 0 || url_$1.indexOf("file:") == 0) {
return url_$1
};
var u_$3 = this.getLoadURLAsLzURL();
u_$3.query = null;
if (url_$1.indexOf(":") > -1) {
var colon_$4 = url_$1.indexOf(":");
var loadUrlIsSecure_$5 = u_$3.protocol == "https";
u_$3.protocol = url_$1.substring(0, colon_$4);
if (secure_$2 || loadUrlIsSecure_$5) {
if (u_$3.protocol == "http") {
u_$3.protocol = "https"
}};
var path_$6 = url_$1.substring(colon_$4 + 1, url_$1.length);
if (path_$6.charAt(0) == "/") {
u_$3.path = url_$1.substring(colon_$4 + 1);
u_$3.file = null
} else {
u_$3.file = url_$1.substring(colon_$4 + 1)
}} else {
if (url_$1.charAt(0) == "/") {
u_$3.path = url_$1;
u_$3.file = null
} else {
u_$3.file = url_$1
}};
return u_$3.toString()
};
$lzsc$temp["displayName"] = "toAbsoluteURL";
return $lzsc$temp
})(), "xmlEscape", (function  () {
var $lzsc$temp = function  (str_$1) {
return LzDataElement.__LZXMLescape(str_$1)
};
$lzsc$temp["displayName"] = "xmlEscape";
return $lzsc$temp
})(), "urlEscape", (function  () {
var $lzsc$temp = function  (str_$1) {
Debug.info("lz.Browser.urlEscape is deprecated, use encodeURIComponent instead");
return encodeURIComponent(str_$1)
};
$lzsc$temp["displayName"] = "urlEscape";
return $lzsc$temp
})(), "urlUnescape", (function  () {
var $lzsc$temp = function  (str_$1) {
Debug.info("lz.Browser.urlUnescape is deprecated, use decodeURIComponent instead");
return decodeURIComponent(str_$1)
};
$lzsc$temp["displayName"] = "urlUnescape";
return $lzsc$temp
})(), "usePost", (function  () {
var $lzsc$temp = function  () {
return this.postToLps && this.supportsPost()
};
$lzsc$temp["displayName"] = "usePost";
return $lzsc$temp
})(), "supportsPost", (function  () {
var $lzsc$temp = function  () {
return true
};
$lzsc$temp["displayName"] = "supportsPost";
return $lzsc$temp
})(), "makeProxiedURL", (function  () {
var $lzsc$temp = function  (params_$1) {
var headers_$2 = params_$1.headers;
var postbody_$3 = params_$1.postbody;
var proxyurl_$4 = params_$1.proxyurl;
var custom_args_$5 = params_$1.serverproxyargs;
var qargs_$6;
if (custom_args_$5) {
qargs_$6 = {url: this.toAbsoluteURL(params_$1.url, params_$1.secure), lzt: params_$1.service, reqtype: params_$1.httpmethod.toUpperCase()};
for (var opt_$7 in custom_args_$5) {
qargs_$6[opt_$7] = custom_args_$5[opt_$7]
}} else {
qargs_$6 = {url: this.toAbsoluteURL(params_$1.url, params_$1.secure), lzt: params_$1.service, reqtype: params_$1.httpmethod.toUpperCase(), sendheaders: params_$1.sendheaders, trimwhitespace: params_$1.trimwhitespace, nsprefix: params_$1.trimwhitespace, timeout: params_$1.timeout, cache: params_$1.cacheable, ccache: params_$1.ccache}};
if (postbody_$3 != null) {
qargs_$6.lzpostbody = postbody_$3
};
qargs_$6.lzr = $runtime;
if (headers_$2 != null) {
var headerString_$8 = "";
for (var hname_$9 in headers_$2) {
headerString_$8 += hname_$9 + ": " + headers_$2[hname_$9] + "\n"
};
if (headerString_$8 != "") {
qargs_$6["headers"] = headerString_$8
}};
if (!params_$1.ccache) {
qargs_$6.__lzbc__ = new Date().getTime()
};
var sep_$10 = "?";
for (var key_$11 in qargs_$6) {
var val_$12 = qargs_$6[key_$11];
if (typeof val_$12 == "string") {
val_$12 = encodeURIComponent(val_$12);
val_$12 = val_$12.replace(LzDataset.slashPat, "%2F")
};
proxyurl_$4 += sep_$10 + key_$11 + "=" + val_$12;
sep_$10 = "&"
};
return proxyurl_$4
};
$lzsc$temp["displayName"] = "makeProxiedURL";
return $lzsc$temp
})()], ["LzBrowser", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzBrowserService.LzBrowser = new LzBrowserService()
}}};
$lzsc$temp["displayName"] = "services/LzBrowser.lzs#33/1";
return $lzsc$temp
})()(LzBrowserService);
lz.BrowserService = LzBrowserService;
lz.Browser = LzBrowserService.LzBrowser;
Class.make("LzContextMenu", LzNode, ["onmenuopen", LzDeclaredEvent, "kernel", null, "items", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (del_$1, attrs_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 0:
del_$1 = null;;case 1:
attrs_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(del_$1) : del_$1 instanceof LzNode) ? del_$1 : null, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(del_$1) : del_$1 instanceof LzNode) ? attrs_$2 : {delegate: del_$1}, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
this.kernel = new LzContextMenuKernel(this);
this.items = [];
var del_$3 = args_$2 && args_$2["delegate"] || null;
delete args_$2["delegate"];
this.$lzc$set_delegate(del_$3)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
var ip_$1 = this.immediateparent;
if (ip_$1 && (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ip_$1) : ip_$1 instanceof LzView)) {
ip_$1.$lzc$set_contextmenu(this)
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "$lzc$set_delegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
this.kernel.setDelegate(delegate_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_delegate";
return $lzsc$temp
})(), "setDelegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_delegate(delegate_$1)
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "addItem", (function  () {
var $lzsc$temp = function  (item_$1) {
this.items.push(item_$1);
this.kernel.addItem(item_$1)
};
$lzsc$temp["displayName"] = "addItem";
return $lzsc$temp
})(), "hideBuiltInItems", (function  () {
var $lzsc$temp = function  () {
this.kernel.hideBuiltInItems()
};
$lzsc$temp["displayName"] = "hideBuiltInItems";
return $lzsc$temp
})(), "showBuiltInItems", (function  () {
var $lzsc$temp = function  () {
this.kernel.showBuiltInItems()
};
$lzsc$temp["displayName"] = "showBuiltInItems";
return $lzsc$temp
})(), "clearItems", (function  () {
var $lzsc$temp = function  () {
this.items = [];
this.kernel.clearItems()
};
$lzsc$temp["displayName"] = "clearItems";
return $lzsc$temp
})(), "getItems", (function  () {
var $lzsc$temp = function  () {
return this.items
};
$lzsc$temp["displayName"] = "getItems";
return $lzsc$temp
})(), "makeMenuItem", (function  () {
var $lzsc$temp = function  (title_$1, delegate_$2) {
var item_$3 = new LzContextMenuItem(title_$1, delegate_$2);
return item_$3
};
$lzsc$temp["displayName"] = "makeMenuItem";
return $lzsc$temp
})()], ["tagname", "contextmenu", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzContextMenu.attributes.ignoreplacement = true
}}};
$lzsc$temp["displayName"] = "services/LzContextMenu.lzs#32/1";
return $lzsc$temp
})()(LzContextMenu);
lz[LzContextMenu.tagname] = LzContextMenu;
Class.make("LzContextMenuItem", LzNode, ["onselect", LzDeclaredEvent, "kernel", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (title_$1, del_$2, children_$3, instcall_$4) {
switch (arguments.length) {
case 1:
del_$2 = null;;case 2:
children_$3 = null;;case 3:
instcall_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(title_$1) : title_$1 instanceof LzNode) ? title_$1 : null, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(title_$1) : title_$1 instanceof LzNode) ? del_$2 : {title: title_$1, delegate: del_$2}, children_$3, instcall_$4)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function  () {
var $lzsc$temp = function  (parent_$1, args_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$1, args_$2);
var title_$3 = args_$2 && args_$2["title"] || "";
delete args_$2["title"];
var del_$4 = args_$2 && args_$2["delegate"] || null;
delete args_$2["delegate"];
this.kernel = new LzContextMenuItemKernel(this, title_$3, del_$4);
var ip_$5 = this.immediateparent;
if (ip_$5 && (LzContextMenu["$lzsc$isa"] ? LzContextMenu.$lzsc$isa(ip_$5) : ip_$5 instanceof LzContextMenu)) {
ip_$5.addItem(this)
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_delegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
this.kernel.setDelegate(delegate_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_delegate";
return $lzsc$temp
})(), "$lzc$set_caption", (function  () {
var $lzsc$temp = function  (caption_$1) {
this.kernel.setCaption(caption_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_caption";
return $lzsc$temp
})(), "$lzc$set_enabled", (function  () {
var $lzsc$temp = function  (val_$1) {
this.kernel.setEnabled(val_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_enabled";
return $lzsc$temp
})(), "$lzc$set_separatorbefore", (function  () {
var $lzsc$temp = function  (val_$1) {
this.kernel.setSeparatorBefore(val_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_separatorbefore";
return $lzsc$temp
})(), "$lzc$set_visible", (function  () {
var $lzsc$temp = function  (val_$1) {
this.kernel.setVisible(val_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "setDelegate", (function  () {
var $lzsc$temp = function  (delegate_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_delegate(delegate_$1)
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "setCaption", (function  () {
var $lzsc$temp = function  (caption_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_caption(caption_$1)
};
$lzsc$temp["displayName"] = "setCaption";
return $lzsc$temp
})(), "getCaption", (function  () {
var $lzsc$temp = function  () {
return this.kernel.getCaption()
};
$lzsc$temp["displayName"] = "getCaption";
return $lzsc$temp
})(), "setEnabled", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_enabled(val_$1)
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setSeparatorBefore", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_separatorbefore(val_$1)
};
$lzsc$temp["displayName"] = "setSeparatorBefore";
return $lzsc$temp
})(), "setVisible", (function  () {
var $lzsc$temp = function  (val_$1) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(val_$1)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})()], ["tagname", "contextmenuitem", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "services/LzContextMenu.lzs#188/1";
return $lzsc$temp
})()(LzContextMenuItem);
lz[LzContextMenuItem.tagname] = LzContextMenuItem;
Class.make("LzModeManagerService", LzEventable, ["onmode", LzDeclaredEvent, "__LZlastclick", null, "__LZlastClickTime", 0, "willCall", false, "eventsLocked", false, "modeArray", new Array(), "remotedebug", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
LzMouseKernel.setCallback(this, "rawMouseEvent")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "makeModal", (function  () {
var $lzsc$temp = function  (view_$1) {
if (view_$1 && (this.modeArray.length == 0 || !this.hasMode(view_$1))) {
this.modeArray.push(view_$1);
if (this.onmode.ready) {
this.onmode.sendEvent(view_$1)
};
var f_$2 = lz.Focus.getFocus();
if (f_$2 && !f_$2.childOf(view_$1)) {
lz.Focus.clearFocus()
}}};
$lzsc$temp["displayName"] = "makeModal";
return $lzsc$temp
})(), "release", (function  () {
var $lzsc$temp = function  (view_$1) {
var marr_$2 = this.modeArray;
for (var i_$3 = marr_$2.length - 1;i_$3 >= 0;i_$3--) {
if (marr_$2[i_$3] === view_$1) {
marr_$2.splice(i_$3, marr_$2.length - i_$3);
var newmode_$4 = marr_$2[i_$3 - 1];
if (this.onmode.ready) {
this.onmode.sendEvent(newmode_$4 || null)
};
var f_$5 = lz.Focus.getFocus();
if (newmode_$4 && f_$5 && !f_$5.childOf(newmode_$4)) {
lz.Focus.clearFocus()
};
return
}}};
$lzsc$temp["displayName"] = "release";
return $lzsc$temp
})(), "releaseAll", (function  () {
var $lzsc$temp = function  () {
this.modeArray = new Array();
if (this.onmode.ready) {
this.onmode.sendEvent(null)
}};
$lzsc$temp["displayName"] = "releaseAll";
return $lzsc$temp
})(), "handleMouseEvent", (function  () {
var $lzsc$temp = function  (view_$1, eventStr_$2) {
if (eventStr_$2 == "onmouseup") {
lz.Track.__LZmouseup(null)
};
if (view_$1 == null) {
view_$1 = this.__findInputtextSelection()
};
lz.GlobalMouse.__mouseEvent(eventStr_$2, view_$1);
if (view_$1 == null || this.eventsLocked) {
return
};
var dosend_$3 = true;
for (var i_$4 = this.modeArray.length - 1;dosend_$3 && i_$4 >= 0;--i_$4) {
var mView_$5 = this.modeArray[i_$4];
if (!mView_$5) {
continue
};
if ($as2 || $as3) {
if (this.remotedebug == null) {
this.remotedebug = lz.Browser.getInitArg("lzconsoledebug") == "true"
}};
if (!this.remotedebug && view_$1.childOf(Debug.console.window)) {
break
};
if (view_$1.childOf(mView_$5)) {
break
} else {
dosend_$3 = mView_$5.passModeEvent ? mView_$5.passModeEvent(eventStr_$2, view_$1) : false
}};
if (dosend_$3) {
if (eventStr_$2 == "onclick") {
if (this.__LZlastclick === view_$1 && view_$1.ondblclick.ready && LzTimeKernel.getTimer() - this.__LZlastClickTime < view_$1.DOUBLE_CLICK_TIME) {
eventStr_$2 = "ondblclick";
lz.GlobalMouse.__mouseEvent(eventStr_$2, view_$1);
this.__LZlastclick = null
} else {
this.__LZlastclick = view_$1;
this.__LZlastClickTime = LzTimeKernel.getTimer()
}};
view_$1.mouseevent(eventStr_$2);
if (eventStr_$2 == "onmousedown") {
lz.Focus.__LZcheckFocusChange(view_$1)
}}};
$lzsc$temp["displayName"] = "handleMouseEvent";
return $lzsc$temp
})(), "__LZallowInput", (function  () {
var $lzsc$temp = function  (modalview_$1, input_$2) {
if ($as2 || $as3) {
if (this.remotedebug == null) {
this.remotedebug = lz.Browser.getInitArg("lzconsoledebug") == "true"
}};
if (!this.remotedebug && input_$2.childOf(Debug.console.window)) {
return true
};
return input_$2.childOf(modalview_$1)
};
$lzsc$temp["displayName"] = "__LZallowInput";
return $lzsc$temp
})(), "__LZallowFocus", (function  () {
var $lzsc$temp = function  (view_$1) {
var len_$2 = this.modeArray.length;
return len_$2 == 0 || view_$1.childOf(this.modeArray[len_$2 - 1])
};
$lzsc$temp["displayName"] = "__LZallowFocus";
return $lzsc$temp
})(), "globalLockMouseEvents", (function  () {
var $lzsc$temp = function  () {
this.eventsLocked = true
};
$lzsc$temp["displayName"] = "globalLockMouseEvents";
return $lzsc$temp
})(), "globalUnlockMouseEvents", (function  () {
var $lzsc$temp = function  () {
this.eventsLocked = false
};
$lzsc$temp["displayName"] = "globalUnlockMouseEvents";
return $lzsc$temp
})(), "hasMode", (function  () {
var $lzsc$temp = function  (view_$1) {
var marr_$2 = this.modeArray;
for (var i_$3 = marr_$2.length - 1;i_$3 >= 0;i_$3--) {
if (view_$1 === marr_$2[i_$3]) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "hasMode";
return $lzsc$temp
})(), "getModalView", (function  () {
var $lzsc$temp = function  () {
return this.modeArray[this.modeArray.length - 1] || null
};
$lzsc$temp["displayName"] = "getModalView";
return $lzsc$temp
})(), "__findInputtextSelection", (function  () {
var $lzsc$temp = function  () {
return LzInputTextSprite.findSelection()
};
$lzsc$temp["displayName"] = "__findInputtextSelection";
return $lzsc$temp
})(), "rawMouseEvent", (function  () {
var $lzsc$temp = function  (eventname_$1, view_$2) {
if (eventname_$1 == "onmousemove") {
lz.GlobalMouse.__mouseEvent("onmousemove", null)
} else {
this.handleMouseEvent(view_$2, eventname_$1)
}};
$lzsc$temp["displayName"] = "rawMouseEvent";
return $lzsc$temp
})()], ["LzModeManager", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzModeManagerService.LzModeManager = new LzModeManagerService()
}}};
$lzsc$temp["displayName"] = "services/LzModeManager.lzs#53/1";
return $lzsc$temp
})()(LzModeManagerService);
lz.ModeManagerService = LzModeManagerService;
lz.ModeManager = LzModeManagerService.LzModeManager;
Class.make("LzCursorService", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "showHandCursor", (function  () {
var $lzsc$temp = function  (show_$1) {
LzMouseKernel.showHandCursor(show_$1)
};
$lzsc$temp["displayName"] = "showHandCursor";
return $lzsc$temp
})(), "setCursorGlobal", (function  () {
var $lzsc$temp = function  (resource_$1) {
LzMouseKernel.setCursorGlobal(resource_$1)
};
$lzsc$temp["displayName"] = "setCursorGlobal";
return $lzsc$temp
})(), "lock", (function  () {
var $lzsc$temp = function  () {
LzMouseKernel.lock()
};
$lzsc$temp["displayName"] = "lock";
return $lzsc$temp
})(), "unlock", (function  () {
var $lzsc$temp = function  () {
LzMouseKernel.unlock()
};
$lzsc$temp["displayName"] = "unlock";
return $lzsc$temp
})(), "restoreCursor", (function  () {
var $lzsc$temp = function  () {
LzMouseKernel.restoreCursor()
};
$lzsc$temp["displayName"] = "restoreCursor";
return $lzsc$temp
})()], ["LzCursor", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzCursorService.LzCursor = new LzCursorService()
}}};
$lzsc$temp["displayName"] = "services/LzCursor.lzs#46/1";
return $lzsc$temp
})()(LzCursorService);
lz.CursorService = LzCursorService;
lz.Cursor = LzCursorService.LzCursor;
Class.make("LzURL", null, ["protocol", null, "host", null, "port", null, "path", null, "file", null, "query", null, "fragment", null, "_parsed", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (url_$1) {
switch (arguments.length) {
case 0:
url_$1 = null
};
if (url_$1 != null) {
this.parseURL(url_$1)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "parseURL", (function  () {
var $lzsc$temp = function  (url_$1) {
if (this._parsed == url_$1) {
return
};
this._parsed = url_$1;
var i0_$2 = 0;
var i1_$3 = url_$1.indexOf(":");
var iquery_$4 = url_$1.indexOf("?", i0_$2);
var ifrag_$5 = url_$1.indexOf("#", i0_$2);
var iopt_$6 = url_$1.length;
if (ifrag_$5 != -1) {
iopt_$6 = ifrag_$5
};
if (iquery_$4 != -1) {
iopt_$6 = iquery_$4
};
if (i1_$3 != -1) {
this.protocol = url_$1.substring(i0_$2, i1_$3);
if (url_$1.substring(i1_$3 + 1, i1_$3 + 3) == "//") {
i0_$2 = i1_$3 + 3;
i1_$3 = url_$1.indexOf("/", i0_$2);
if (i1_$3 == -1) {
i1_$3 = iopt_$6
};
var hostPort_$7 = url_$1.substring(i0_$2, i1_$3);
var i_$8 = hostPort_$7.indexOf(":");
if (i_$8 == -1) {
this.host = hostPort_$7;
this.port = null
} else {
this.host = hostPort_$7.substring(0, i_$8);
this.port = hostPort_$7.substring(i_$8 + 1)
}} else {
i1_$3++
};
i0_$2 = i1_$3
};
i1_$3 = iopt_$6;
this._splitPath(url_$1.substring(i0_$2, i1_$3));
if (ifrag_$5 != -1) {
this.fragment = url_$1.substring(ifrag_$5 + 1, url_$1.length)
} else {
ifrag_$5 = url_$1.length
};
if (iquery_$4 != -1) {
this.query = url_$1.substring(iquery_$4 + 1, ifrag_$5)
}};
$lzsc$temp["displayName"] = "parseURL";
return $lzsc$temp
})(), "_splitPath", (function  () {
var $lzsc$temp = function  (pathfile_$1) {
if (pathfile_$1 == "") {
return
};
var ls_$2 = pathfile_$1.lastIndexOf("/");
if (ls_$2 != -1) {
this.path = pathfile_$1.substring(0, ls_$2 + 1);
this.file = pathfile_$1.substring(ls_$2 + 1, pathfile_$1.length);
if (this.file == "") {
this.file = null
};
return
};
this.path = null;
this.file = pathfile_$1
};
$lzsc$temp["displayName"] = "_splitPath";
return $lzsc$temp
})(), "dupe", (function  () {
var $lzsc$temp = function  () {
var o_$1 = new LzURL();
o_$1.protocol = this.protocol;
o_$1.host = this.host;
o_$1.port = this.port;
o_$1.path = this.path;
o_$1.file = this.file;
o_$1.query = this.query;
o_$1.fragment = this.fragment;
return o_$1
};
$lzsc$temp["displayName"] = "dupe";
return $lzsc$temp
})(), "toString", (function  () {
var $lzsc$temp = function  () {
var out_$1 = "";
if (this.protocol != null) {
out_$1 += this.protocol + ":";
if (this.host != null) {
out_$1 += "//" + this.host;
if (null != this.port && lz.Browser.defaultPortNums[this.protocol] != this.port) {
out_$1 += ":" + this.port
}}};
if (this.path != null) {
out_$1 += this.path
};
if (null != this.file) {
out_$1 += this.file
};
if (null != this.query) {
out_$1 += "?" + this.query
};
if (null != this.fragment) {
out_$1 += "#" + this.fragment
};
return out_$1
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], ["merge", (function  () {
var $lzsc$temp = function  (url_$1, defaults_$2) {
var m_$3 = new LzURL();
var tocopy_$4 = {protocol: true, host: true, port: true, path: true, file: true, query: true, fragment: true};
for (var key_$5 in tocopy_$4) {
m_$3[key_$5] = url_$1[key_$5] != null ? url_$1[key_$5] : defaults_$2[key_$5]
};
return m_$3
};
$lzsc$temp["displayName"] = "merge";
return $lzsc$temp
})()]);
lz.URL = LzURL;
Class.make("LzKeysService", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
LzKeyboardKernel.setCallback(this, "__keyEvent");
if (lz.embed["mousewheel"]) {
lz.embed.mousewheel.setCallback(this, "__mousewheelEvent")
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "downKeysHash", {}, "downKeysArray", [], "keycombos", {}, "onkeydown", LzDeclaredEvent, "onkeyup", LzDeclaredEvent, "onmousewheeldelta", LzDeclaredEvent, "codemap", {shift: 16, control: 17, alt: 18}, "ctrlKey", false, "__keyEvent", (function  () {
var $lzsc$temp = function  (delta_$1, k_$2, type_$3, ctrlKey_$4) {
switch (arguments.length) {
case 3:
ctrlKey_$4 = false
};
this.ctrlKey = ctrlKey_$4;
var cm_$5 = this.codemap;
for (var key_$6 in delta_$1) {
var down_$7 = delta_$1[key_$6];
if (cm_$5[key_$6] != null) {
k_$2 = cm_$5[key_$6]
};
if (down_$7) {
this.gotKeyDown(k_$2)
} else {
this.gotKeyUp(k_$2)
}}};
$lzsc$temp["displayName"] = "__keyEvent";
return $lzsc$temp
})(), "__allKeysUp", (function  () {
var $lzsc$temp = function  () {
LzKeyboardKernel.__allKeysUp()
};
$lzsc$temp["displayName"] = "__allKeysUp";
return $lzsc$temp
})(), "gotKeyDown", (function  () {
var $lzsc$temp = function  (kC_$1, info_$2) {
switch (arguments.length) {
case 1:
info_$2 = null
};
var dkhash_$3 = this.downKeysHash;
var dkeys_$4 = this.downKeysArray;
var firstkeydown_$5 = !dkhash_$3[kC_$1];
if (firstkeydown_$5) {
dkhash_$3[kC_$1] = true;
dkeys_$4.push(kC_$1);
dkeys_$4.sort()
};
if (firstkeydown_$5 || info_$2 != "extra") {
if (dkhash_$3[229] != true) {
if (this.onkeydown.ready) {
this.onkeydown.sendEvent(kC_$1)
}}};
if (firstkeydown_$5) {
var cp_$6 = this.keycombos;
for (var i_$7 = 0;i_$7 < dkeys_$4.length && cp_$6 != null;i_$7++) {
cp_$6 = cp_$6[dkeys_$4[i_$7]]
};
if (cp_$6 != null && ("delegates" in cp_$6)) {
var del_$8 = cp_$6.delegates;
for (var i_$7 = 0;i_$7 < del_$8.length;i_$7++) {
del_$8[i_$7].execute(dkeys_$4)
}}}};
$lzsc$temp["displayName"] = "gotKeyDown";
return $lzsc$temp
})(), "gotKeyUp", (function  () {
var $lzsc$temp = function  (kC_$1) {
var dkhash_$2 = this.downKeysHash;
var isDown_$3 = dkhash_$2[kC_$1];
delete dkhash_$2[kC_$1];
var dkeys_$4 = this.downKeysArray;
dkeys_$4.length = 0;
for (var k_$5 in dkhash_$2) {
dkeys_$4.push(k_$5)
};
if (isDown_$3 && this.onkeyup.ready) {
this.onkeyup.sendEvent(kC_$1)
}};
$lzsc$temp["displayName"] = "gotKeyUp";
return $lzsc$temp
})(), "isKeyDown", (function  () {
var $lzsc$temp = function  (k_$1) {
if (typeof k_$1 == "string") {
return this.downKeysHash[this.keyCodes[k_$1.toLowerCase()]] == true
} else {
var down_$2 = true;
var dkhash_$3 = this.downKeysHash;
var kc_$4 = this.keyCodes;
for (var i_$5 = 0;i_$5 < k_$1.length;i_$5++) {
down_$2 = down_$2 && dkhash_$3[kc_$4[k_$1[i_$5].toLowerCase()]] == true
};
return down_$2
}};
$lzsc$temp["displayName"] = "isKeyDown";
return $lzsc$temp
})(), "callOnKeyCombo", (function  () {
var $lzsc$temp = function  (d_$1, kCArr_$2) {
var kc_$3 = this.keyCodes;
var kcSorted_$4 = [];
for (var i_$5 = 0;i_$5 < kCArr_$2.length;i_$5++) {
kcSorted_$4.push(kc_$3[kCArr_$2[i_$5].toLowerCase()])
};
kcSorted_$4.sort();
var cp_$6 = this.keycombos;
for (var i_$5 = 0;i_$5 < kcSorted_$4.length;i_$5++) {
var cpnext_$7 = cp_$6[kcSorted_$4[i_$5]];
if (cpnext_$7 == null) {
cp_$6[kcSorted_$4[i_$5]] = cpnext_$7 = {delegates: []}};
cp_$6 = cpnext_$7
};
cp_$6.delegates.push(d_$1)
};
$lzsc$temp["displayName"] = "callOnKeyCombo";
return $lzsc$temp
})(), "removeKeyComboCall", (function  () {
var $lzsc$temp = function  (d_$1, kCArr_$2) {
var kc_$3 = this.keyCodes;
var kcSorted_$4 = [];
for (var i_$5 = 0;i_$5 < kCArr_$2.length;i_$5++) {
kcSorted_$4.push(kc_$3[kCArr_$2[i_$5].toLowerCase()])
};
kcSorted_$4.sort();
var cp_$6 = this.keycombos;
for (var i_$5 = 0;i_$5 < kcSorted_$4.length;i_$5++) {
cp_$6 = cp_$6[kcSorted_$4[i_$5]];
if (cp_$6 == null) {
return false
}};
for (var i_$5 = cp_$6.delegates.length - 1;i_$5 >= 0;i_$5--) {
if (cp_$6.delegates[i_$5] == d_$1) {
cp_$6.delegates.splice(i_$5, 1)
}}};
$lzsc$temp["displayName"] = "removeKeyComboCall";
return $lzsc$temp
})(), "enableEnter", (function  () {
var $lzsc$temp = function  (onroff_$1) {
Debug.write("lz.Keys.enableEnter not yet defined")
};
$lzsc$temp["displayName"] = "enableEnter";
return $lzsc$temp
})(), "mousewheeldelta", 0, "__mousewheelEvent", (function  () {
var $lzsc$temp = function  (d_$1) {
this.mousewheeldelta = d_$1;
if (this.onmousewheeldelta.ready) {
this.onmousewheeldelta.sendEvent(d_$1)
}};
$lzsc$temp["displayName"] = "__mousewheelEvent";
return $lzsc$temp
})(), "gotLastFocus", (function  () {
var $lzsc$temp = function  (ignore_$1) {
LzKeyboardKernel.gotLastFocus()
};
$lzsc$temp["displayName"] = "gotLastFocus";
return $lzsc$temp
})(), "setGlobalFocusTrap", (function  () {
var $lzsc$temp = function  (istrapped_$1) {
LzKeyboardKernel.setGlobalFocusTrap(istrapped_$1)
};
$lzsc$temp["displayName"] = "setGlobalFocusTrap";
return $lzsc$temp
})(), "keyCodes", {"0": 48, ")": 48, ";": 186, ":": 186, "1": 49, "!": 49, "=": 187, "+": 187, "2": 50, "@": 50, "<": 188, ",": 188, "3": 51, "#": 51, "-": 189, "_": 189, "4": 52, "$": 52, ">": 190, ".": 190, "5": 53, "%": 53, "/": 191, "?": 191, "6": 54, "^": 54, "`": 192, "~": 192, "7": 55, "&": 55, "[": 219, "{": 219, "8": 56, "*": 56, "\\": 220, "|": 220, "9": 57, "(": 57, "]": 221, "}": 221, '"': 222, "'": 222, a: 65, b: 66, c: 67, d: 68, e: 69, f: 70, g: 71, h: 72, i: 73, j: 74, k: 75, l: 76, m: 77, n: 78, o: 79, p: 80, q: 81, r: 82, s: 83, t: 84, u: 85, v: 86, w: 87, x: 88, y: 89, z: 90, numbpad0: 96, numbpad1: 97, numbpad2: 98, numbpad3: 99, numbpad4: 100, numbpad5: 101, numbpad6: 102, numbpad7: 103, numbpad8: 104, numbpad9: 105, multiply: 106, "add": 107, subtract: 109, decimal: 110, divide: 111, f1: 112, f2: 113, f3: 114, f4: 115, f5: 116, f6: 117, f7: 118, f8: 119, f9: 120, f10: 121, f11: 122, f12: 123, backspace: 8, tab: 9, clear: 12, enter: 13, shift: 16, control: 17, alt: 18, "pause": 19, "break": 19, capslock: 20, esc: 27, spacebar: 32, pageup: 33, pagedown: 34, end: 35, home: 36, leftarrow: 37, uparrow: 38, rightarrow: 39, downarrow: 40, insert: 45, "delete": 46, help: 47, numlock: 144, screenlock: 145, "IME": 229}], ["LzKeys", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzKeysService.LzKeys = new LzKeysService()
}}};
$lzsc$temp["displayName"] = "services/LzKeys.lzs#40/1";
return $lzsc$temp
})()(LzKeysService);
lz.KeysService = LzKeysService;
lz.Keys = LzKeysService.LzKeys;
Class.make("LzAudioService", null, ["capabilities", LzSprite.prototype.capabilities, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "playSound", (function  () {
var $lzsc$temp = function  (snd_$1) {
if (this.capabilities.audio) {
LzAudioKernel.playSound(snd_$1)
} else {
LzView.__warnCapability("lz.Audio.playSound()", "audio")
}};
$lzsc$temp["displayName"] = "playSound";
return $lzsc$temp
})(), "stopSound", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
LzAudioKernel.stopSound()
} else {
LzView.__warnCapability("lz.Audio.stopSound()", "audio")
}};
$lzsc$temp["displayName"] = "stopSound";
return $lzsc$temp
})(), "startSound", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
LzAudioKernel.startSound()
} else {
LzView.__warnCapability("lz.Audio.startSound()", "audio")
}};
$lzsc$temp["displayName"] = "startSound";
return $lzsc$temp
})(), "getVolume", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
return LzAudioKernel.getVolume()
} else {
LzView.__warnCapability("lz.Audio.getVolume()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getVolume";
return $lzsc$temp
})(), "setVolume", (function  () {
var $lzsc$temp = function  (v_$1) {
if (this.capabilities.audio) {
LzAudioKernel.setVolume(v_$1)
} else {
LzView.__warnCapability("lz.Audio.setVolume()", "audio")
}};
$lzsc$temp["displayName"] = "setVolume";
return $lzsc$temp
})(), "getPan", (function  () {
var $lzsc$temp = function  () {
if (this.capabilities.audio) {
return LzAudioKernel.getPan()
} else {
LzView.__warnCapability("lz.Audio.getPan()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getPan";
return $lzsc$temp
})(), "setPan", (function  () {
var $lzsc$temp = function  (p_$1) {
if (this.capabilities.audio) {
LzAudioKernel.setPan(p_$1)
} else {
LzView.__warnCapability("lz.Audio.setPan()", "audio")
}};
$lzsc$temp["displayName"] = "setPan";
return $lzsc$temp
})()], ["LzAudio", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzAudioService.LzAudio = new LzAudioService()
}}};
$lzsc$temp["displayName"] = "services/LzAudio.lzs#27/1";
return $lzsc$temp
})()(LzAudioService);
lz.AudioService = LzAudioService;
lz.Audio = LzAudioService.LzAudio;
Class.make("LzHistoryService", LzEventable, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "isReady", false, "ready", false, "onready", LzDeclaredEvent, "persist", false, "_persistso", null, "offset", 0, "__lzdirty", false, "__lzhistq", [], "__lzcurrstate", {}, "capabilities", LzSprite.prototype.capabilities, "onoffset", LzDeclaredEvent, "receiveHistory", (function  () {
var $lzsc$temp = function  (o_$1) {
if (this.persist && !this._persistso) {
this.__initPersist()
};
var len_$2 = this.__lzhistq.length;
var offset_$3 = o_$1 * 1;
if (!offset_$3) {
offset_$3 = 0
} else {
if (offset_$3 > len_$2 - 1) {
offset_$3 = len_$2
}};
var h_$4 = this.__lzhistq[offset_$3];
for (var u_$5 in h_$4) {
var obj_$6 = h_$4[u_$5];
var $lzsc$1187697897 = global[obj_$6.c];
var $lzsc$317867907 = obj_$6.n;
var $lzsc$1334163744 = obj_$6.v;
if (!$lzsc$1187697897.__LZdeleted) {
var $lzsc$333066833 = "$lzc$set_" + $lzsc$317867907;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa($lzsc$1187697897[$lzsc$333066833]) : $lzsc$1187697897[$lzsc$333066833] instanceof Function) {
$lzsc$1187697897[$lzsc$333066833]($lzsc$1334163744)
} else {
$lzsc$1187697897[$lzsc$317867907] = $lzsc$1334163744;
var $lzsc$1061885623 = $lzsc$1187697897["on" + $lzsc$317867907];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$1061885623) : $lzsc$1061885623 instanceof LzEvent) {
if ($lzsc$1061885623.ready) {
$lzsc$1061885623.sendEvent($lzsc$1334163744)
}}}}};
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_offset"]) : this["$lzc$set_offset"] instanceof Function) {
this["$lzc$set_offset"](offset_$3)
} else {
this["offset"] = offset_$3;
var $lzsc$264555506 = this["onoffset"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$264555506) : $lzsc$264555506 instanceof LzEvent) {
if ($lzsc$264555506.ready) {
$lzsc$264555506.sendEvent(offset_$3)
}}}};
return offset_$3
};
$lzsc$temp["displayName"] = "receiveHistory";
return $lzsc$temp
})(), "receiveEvent", (function  () {
var $lzsc$temp = function  (n_$1, v_$2) {
if (!canvas.__LZdeleted) {
var $lzsc$883541403 = "$lzc$set_" + n_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(canvas[$lzsc$883541403]) : canvas[$lzsc$883541403] instanceof Function) {
canvas[$lzsc$883541403](v_$2)
} else {
canvas[n_$1] = v_$2;
var $lzsc$654763208 = canvas["on" + n_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$654763208) : $lzsc$654763208 instanceof LzEvent) {
if ($lzsc$654763208.ready) {
$lzsc$654763208.sendEvent(v_$2)
}}}}};
$lzsc$temp["displayName"] = "receiveEvent";
return $lzsc$temp
})(), "getCanvasAttribute", (function  () {
var $lzsc$temp = function  (n_$1) {
return canvas[n_$1]
};
$lzsc$temp["displayName"] = "getCanvasAttribute";
return $lzsc$temp
})(), "setCanvasAttribute", (function  () {
var $lzsc$temp = function  (n_$1, v_$2) {
this.receiveEvent(n_$1, v_$2)
};
$lzsc$temp["displayName"] = "setCanvasAttribute";
return $lzsc$temp
})(), "callMethod", (function  () {
var $lzsc$temp = function  (js_$1) {
return LzBrowserKernel.callMethod(js_$1)
};
$lzsc$temp["displayName"] = "callMethod";
return $lzsc$temp
})(), "save", (function  () {
var $lzsc$temp = function  (who_$1, prop_$2, val_$3) {
if (typeof who_$1 != "string") {
if (who_$1["id"]) {
who_$1 = who_$1["id"]
};
Debug.warn("Warning: this.save() requires a view ID to be passed in as a string for the first argument.");
if (!who_$1) {
return
}};
if (val_$3 == null) {
val_$3 = global[who_$1][prop_$2]
};
this.__lzcurrstate[who_$1] = {c: who_$1, n: prop_$2, v: val_$3};
this.__lzdirty = true
};
$lzsc$temp["displayName"] = "save";
return $lzsc$temp
})(), "commit", (function  () {
var $lzsc$temp = function  () {
if (!this.__lzdirty) {
return
};
this.__lzhistq[this.offset] = this.__lzcurrstate;
this.__lzhistq.length = this.offset + 1;
if (this.persist) {
if (!this._persistso) {
this.__initPersist()
};
this._persistso.data[this.offset] = this.__lzcurrstate
};
this.__lzcurrstate = {};
this.__lzdirty = false
};
$lzsc$temp["displayName"] = "commit";
return $lzsc$temp
})(), "move", (function  () {
var $lzsc$temp = function  (by_$1) {
switch (arguments.length) {
case 0:
by_$1 = 1
};
this.commit();
var o_$2 = this.offset + by_$1;
if (0 >= o_$2) {
o_$2 = 0
};
if (this.__lzhistq.length >= o_$2) {
LzBrowserKernel.setHistory(o_$2)
}};
$lzsc$temp["displayName"] = "move";
return $lzsc$temp
})(), "next", (function  () {
var $lzsc$temp = function  () {
this.move(1)
};
$lzsc$temp["displayName"] = "next";
return $lzsc$temp
})(), "prev", (function  () {
var $lzsc$temp = function  () {
this.move(-1)
};
$lzsc$temp["displayName"] = "prev";
return $lzsc$temp
})(), "__initPersist", (function  () {
var $lzsc$temp = function  () {
if (this.persist) {
if (!this._persistso) {
this._persistso = LzBrowserKernel.getPersistedObject("historystate")
};
if (this._persistso && this._persistso.data) {
var d_$1 = this._persistso.data;
this.__lzhistq = [];
for (var i_$2 in d_$1) {
this.__lzhistq[i_$2] = d_$1[i_$2]
}}} else {
if (this._persistso) {
this._persistso = null
}}};
$lzsc$temp["displayName"] = "__initPersist";
return $lzsc$temp
})(), "clear", (function  () {
var $lzsc$temp = function  () {
if (this.persist) {
if (!this._persistso) {
this._persistso = LzBrowserKernel.getPersistedObject("historystate")
};
this._persistso.clear()
};
this.__lzhistq = [];
this.offset = 0;
if (this.onoffset.ready) {
this.onoffset.sendEvent(0)
}};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "setPersist", (function  () {
var $lzsc$temp = function  (p_$1) {
if (this.capabilities.persistence) {
this.persist = p_$1
} else {
Debug.warn("The %s runtime does not support %s", canvas["runtime"], "lz.History.setPersist()")
}};
$lzsc$temp["displayName"] = "setPersist";
return $lzsc$temp
})(), "__start", (function  () {
var $lzsc$temp = function  (id_$1) {
lz.Browser.callJS("lz.embed.history.listen('" + id_$1 + "')");
this.isReady = true;
this.ready = true;
if (this.onready.ready) {
this.onready.sendEvent(true)
}};
$lzsc$temp["displayName"] = "__start";
return $lzsc$temp
})()], ["LzHistory", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzHistoryService.LzHistory = new LzHistoryService()
}}};
$lzsc$temp["displayName"] = "services/LzHistory.lzs#29/1";
return $lzsc$temp
})()(LzHistoryService);
lz.HistoryService = LzHistoryService;
lz.History = LzHistoryService.LzHistory;
Class.make("LzTrackService", LzEventable, ["__LZreg", new Object(), "__LZactivegroups", null, "__LZtrackDel", null, "__LZmouseupDel", null, "__LZdestroydel", null, "__LZlastmouseup", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.__LZtrackDel = new LzDelegate(this, "__LZtrack");
this.__LZmouseupDel = new LzDelegate(this, "__LZmouseup", lz.GlobalMouse, "onmouseup");
this.__LZdestroydel = new LzDelegate(this, "__LZdestroyitem");
this.__LZactivegroups = []
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "register", (function  () {
var $lzsc$temp = function  (v_$1, group_$2) {
if (v_$1 == null || group_$2 == null) {
return
};
var reglist_$3 = this.__LZreg[group_$2];
if (!reglist_$3) {
this.__LZreg[group_$2] = reglist_$3 = [];
reglist_$3.__LZlasthit = null;
reglist_$3.__LZactive = false
};
reglist_$3.push(v_$1);
this.__LZdestroydel.register(v_$1, "ondestroy")
};
$lzsc$temp["displayName"] = "register";
return $lzsc$temp
})(), "unregister", (function  () {
var $lzsc$temp = function  (v_$1, group_$2) {
if (v_$1 == null || group_$2 == null) {
return
};
var reglist_$3 = this.__LZreg[group_$2];
if (reglist_$3) {
for (var i_$4 = 0;i_$4 < reglist_$3.length;i_$4++) {
if (reglist_$3[i_$4] == v_$1) {
if (reglist_$3.__LZlasthit == v_$1) {
if (this.__LZlastmouseup == v_$1) {
this.__LZlastmouseup = null
};
reglist_$3.__LZlasthit = null
};
reglist_$3.splice(i_$4, 1)
}};
if (reglist_$3.length == 0) {
if (reglist_$3.__LZactive) {
this.deactivate(group_$2)
};
delete this.__LZreg[group_$2]
}};
this.__LZdestroydel.unregisterFrom(v_$1.ondestroy)
};
$lzsc$temp["displayName"] = "unregister";
return $lzsc$temp
})(), "__LZdestroyitem", (function  () {
var $lzsc$temp = function  (v_$1) {
for (var group_$2 in this.__LZreg) {
this.unregister(v_$1, group_$2)
}};
$lzsc$temp["displayName"] = "__LZdestroyitem";
return $lzsc$temp
})(), "activate", (function  () {
var $lzsc$temp = function  (group_$1) {
var reglist_$2 = this.__LZreg[group_$1];
if (reglist_$2 && !reglist_$2.__LZactive) {
reglist_$2.__LZactive = true;
var agroups_$3 = this.__LZactivegroups;
if (agroups_$3.length == 0) {
this.__LZtrackDel.register(lz.Idle, "onidle")
};
agroups_$3.push(reglist_$2)
}};
$lzsc$temp["displayName"] = "activate";
return $lzsc$temp
})(), "deactivate", (function  () {
var $lzsc$temp = function  (group_$1) {
var reglist_$2 = this.__LZreg[group_$1];
if (reglist_$2 && reglist_$2.__LZactive) {
var agroups_$3 = this.__LZactivegroups;
for (var i_$4 = 0;i_$4 < agroups_$3.length;++i_$4) {
if (agroups_$3[i_$4] == reglist_$2) {
agroups_$3.splice(i_$4, 1);
break
}};
if (agroups_$3.length == 0) {
this.__LZtrackDel.unregisterAll()
};
reglist_$2.__LZactive = false;
if (this.__LZlastmouseup == reglist_$2.__LZlasthit) {
this.__LZlastmouseup = null
};
reglist_$2.__LZlasthit = null
}};
$lzsc$temp["displayName"] = "deactivate";
return $lzsc$temp
})(), "__LZtopview", (function  () {
var $lzsc$temp = function  (a_$1, b_$2) {
var atemp_$3 = a_$1;
var btemp_$4 = b_$2;
while (atemp_$3.nodeLevel < btemp_$4.nodeLevel) {
btemp_$4 = btemp_$4.immediateparent;
if (btemp_$4 == a_$1) {
return b_$2
}};
while (btemp_$4.nodeLevel < atemp_$3.nodeLevel) {
atemp_$3 = atemp_$3.immediateparent;
if (atemp_$3 == b_$2) {
return a_$1
}};
while (atemp_$3.immediateparent != btemp_$4.immediateparent) {
atemp_$3 = atemp_$3.immediateparent;
btemp_$4 = btemp_$4.immediateparent
};
return atemp_$3.getZ() > btemp_$4.getZ() ? a_$1 : b_$2
};
$lzsc$temp["displayName"] = "__LZtopview";
return $lzsc$temp
})(), "__LZfindTopmost", (function  () {
var $lzsc$temp = function  (vlist_$1) {
var top_$2 = vlist_$1[0];
for (var i_$3 = 1;i_$3 < vlist_$1.length;i_$3++) {
top_$2 = this.__LZtopview(top_$2, vlist_$1[i_$3])
};
return top_$2
};
$lzsc$temp["displayName"] = "__LZfindTopmost";
return $lzsc$temp
})(), "__LZtrackgroup", (function  () {
var $lzsc$temp = function  (group_$1, hitlist_$2) {
for (var i_$3 = 0;i_$3 < group_$1.length;i_$3++) {
var v_$4 = group_$1[i_$3];
if (v_$4.visible) {
var vpos_$5 = v_$4.getMouse(null);
if (v_$4.containsPt(vpos_$5.x, vpos_$5.y)) {
hitlist_$2.push(v_$4)
}}}};
$lzsc$temp["displayName"] = "__LZtrackgroup";
return $lzsc$temp
})(), "__LZtrack", (function  () {
var $lzsc$temp = function  (ignore_$1) {
var foundviews_$2 = [];
var agroups_$3 = this.__LZactivegroups;
for (var i_$4 = 0;i_$4 < agroups_$3.length;++i_$4) {
var thisgroup_$5 = agroups_$3[i_$4];
var hitlist_$6 = [];
this.__LZtrackgroup(thisgroup_$5, hitlist_$6);
var lhit_$7 = thisgroup_$5.__LZlasthit;
if (hitlist_$6.length) {
var fd_$8 = this.__LZfindTopmost(hitlist_$6);
if (fd_$8 == lhit_$7) {
continue
};
foundviews_$2.push(fd_$8)
} else {
var fd_$8 = null
};
if (lhit_$7) {
var onmtrackout_$9 = lhit_$7.onmousetrackout;
if (onmtrackout_$9.ready) {
onmtrackout_$9.sendEvent(lhit_$7)
}};
thisgroup_$5.__LZlasthit = fd_$8
};
for (var i_$4 = 0, len_$10 = foundviews_$2.length;i_$4 < len_$10;++i_$4) {
var v_$11 = foundviews_$2[i_$4];
if (v_$11.onmousetrackover.ready) {
v_$11.onmousetrackover.sendEvent(v_$11)
}}};
$lzsc$temp["displayName"] = "__LZtrack";
return $lzsc$temp
})(), "__LZmouseup", (function  () {
var $lzsc$temp = function  (ignore_$1) {
var agroups_$2 = this.__LZactivegroups.slice();
for (var i_$3 = 0;i_$3 < agroups_$2.length;++i_$3) {
var lhit_$4 = agroups_$2[i_$3].__LZlasthit;
if (lhit_$4) {
var onmtrackup_$5 = lhit_$4.onmousetrackup;
if (onmtrackup_$5.ready) {
if (this.__LZlastmouseup == lhit_$4) {
this.__LZlastmouseup = null
} else {
this.__LZlastmouseup = lhit_$4;
onmtrackup_$5.sendEvent(lhit_$4)
}}}}};
$lzsc$temp["displayName"] = "__LZmouseup";
return $lzsc$temp
})()], ["LzTrack", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzTrackService.LzTrack = new LzTrackService()
}}};
$lzsc$temp["displayName"] = "services/LzTrack.lzs#106/1";
return $lzsc$temp
})()(LzTrackService);
lz.TrackService = LzTrackService;
lz.Track = LzTrackService.LzTrack;
Class.make("LzIdleEvent", LzEvent, ["registered", false, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (eventSender_$1, eventName_$2, d_$3) {
switch (arguments.length) {
case 2:
d_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, eventSender_$1, eventName_$2, d_$3);
if (this.ready && !this.registered) {
this.registered = true;
LzIdleKernel.addCallback(this, "sendEvent")
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "addDelegate", (function  () {
var $lzsc$temp = function  (d_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["addDelegate"] || this.nextMethod(arguments.callee, "addDelegate")).call(this, d_$1);
if (this.ready && !this.registered) {
this.registered = true;
LzIdleKernel.addCallback(this, "sendEvent")
}};
$lzsc$temp["displayName"] = "addDelegate";
return $lzsc$temp
})(), "removeDelegate", (function  () {
var $lzsc$temp = function  (d_$1) {
switch (arguments.length) {
case 0:
d_$1 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["removeDelegate"] || this.nextMethod(arguments.callee, "removeDelegate")).call(this, d_$1);
if (!this.ready && this.registered) {
LzIdleKernel.removeCallback(this, "sendEvent");
this.registered = false
}};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "clearDelegates", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["clearDelegates"] || this.nextMethod(arguments.callee, "clearDelegates")).call(this);
if (!this.ready && this.registered) {
LzIdleKernel.removeCallback(this, "sendEvent");
this.registered = false
}};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})()], null);
Class.make("LzIdleService", LzEventable, ["coi", void 0, "regNext", false, "removeCOI", null, "onidle", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.coi = new Array();
this.removeCOI = new LzDelegate(this, "removeCallIdleDelegates");
this.onidle = new LzIdleEvent(this, "onidle")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "callOnIdle", (function  () {
var $lzsc$temp = function  (d_$1) {
this.coi.push(d_$1);
if (!this.regNext) {
this.regNext = true;
this.removeCOI.register(this, "onidle")
}};
$lzsc$temp["displayName"] = "callOnIdle";
return $lzsc$temp
})(), "removeCallIdleDelegates", (function  () {
var $lzsc$temp = function  (t_$1) {
var arr_$2 = this.coi.slice(0);
this.coi.length = 0;
for (var i_$3 = 0;i_$3 < arr_$2.length;i_$3++) {
arr_$2[i_$3].execute(t_$1)
};
if (this.coi.length == 0) {
this.removeCOI.unregisterFrom(this.onidle);
this.regNext = false
}};
$lzsc$temp["displayName"] = "removeCallIdleDelegates";
return $lzsc$temp
})(), "setFrameRate", (function  () {
var $lzsc$temp = function  (fps_$1) {
switch (arguments.length) {
case 0:
fps_$1 = 30
};
LzIdleKernel.setFrameRate(fps_$1)
};
$lzsc$temp["displayName"] = "setFrameRate";
return $lzsc$temp
})()], ["LzIdle", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzIdleService.LzIdle = new LzIdleService()
}}};
$lzsc$temp["displayName"] = "services/LzIdle.lzs#78/1";
return $lzsc$temp
})()(LzIdleService);
lz.IdleService = LzIdleService;
lz.Idle = LzIdleService.LzIdle;
Class.make("LzCSSStyleRule", null, ["selector", void 0, "properties", void 0, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  (selector_$1, properties_$2, source_$3, line_$4) {
switch (arguments.length) {
case 2:
source_$3 = null;;case 3:
line_$4 = null
};
this.selector = selector_$1;
this.properties = properties_$2;
this[Debug.FUNCTION_FILENAME] = source_$3;
this[Debug.FUNCTION_LINENO] = line_$4
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "specificity", 0, "parsed", null, "_lexorder", void 0, "getSpecificity", (function  () {
var $lzsc$temp = function  () {
if (!this.specificity) {
var s_$1 = 0;
var p_$2 = this.parsed;
if (p_$2.type == LzCSSStyle._sel_compound) {
for (var i_$3 = 0, l_$4 = p_$2.length;i_$3 < l_$4;i_$3++) {
s_$1 += LzCSSStyle.getSelectorSpecificity(p_$2[i_$3])
}} else {
s_$1 = LzCSSStyle.getSelectorSpecificity(p_$2)
};
this.specificity = s_$1
};
return this.specificity
};
$lzsc$temp["displayName"] = "getSpecificity";
return $lzsc$temp
})(), "_dbg_name", (function  () {
var $lzsc$temp = function  () {
var pname_$1;
pname_$1 = (function  () {
var $lzsc$temp = function  (rp_$1) {
var rptn_$2 = rp_$1["tagname"];
var rpi_$3 = rp_$1["id"];
var rpa_$4 = rp_$1["attrname"];
if (!(rptn_$2 || rpi_$3 || rpa_$4)) {
return "*"
};
return (rptn_$2 ? rptn_$2 : "") + (rpi_$3 ? "#" + rpi_$3 : "") + (rpa_$4 ? "[" + rpa_$4 + "=" + rp_$1.attrvalue + "]" : "")
};
$lzsc$temp["displayName"] = "pname";
return $lzsc$temp
})();
var rp_$2 = this.parsed;
if (rp_$2["length"]) {
var n_$3 = "";
for (var i_$4 = 0;i_$4 < rp_$2.length;i_$4++) {
n_$3 += pname_$1(rp_$2[i_$4]) + " "
};
n_$3 = n_$3.substring(0, n_$3.length - 1)
} else {
var n_$3 = pname_$1(rp_$2)
};
return n_$3
};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#73/19";
return $lzsc$temp
})(), "equal", (function  () {
var $lzsc$temp = function  (that_$1) {
var equal_$2;
equal_$2 = (function  () {
var $lzsc$temp = function  (pa_$1, pb_$2) {
return pa_$1["tagname"] == pb_$2["tagname"] && pa_$1["id"] == pb_$2["id"] && pa_$1["attrname"] == pb_$2["attrname"] && pa_$1["attrvalue"] == pb_$2["attrvalue"]
};
$lzsc$temp["displayName"] = "equal";
return $lzsc$temp
})();
var rap_$3 = this.parsed;
var rbp_$4 = that_$1.parsed;
if (rap_$3["length"] != rbp_$4["length"]) {
return false
};
if (rap_$3["length"]) {
for (var i_$5 = rap_$3.length - 1;i_$5 >= 0;i_$5--) {
if (!equal_$2(rap_$3[i_$5], rbp_$4[i_$5])) {
return false
}}};
if (!equal_$2(rap_$3, rbp_$4)) {
return false
};
var aprops_$6 = this.properties;
var bprops_$7 = that_$1.properties;
for (var ak_$8 in aprops_$6) {
if (aprops_$6[ak_$8] !== bprops_$7[ak_$8]) {
return false
}};
for (var bk_$9 in bprops_$7) {
if (aprops_$6[bk_$9] !== bprops_$7[bk_$9]) {
return false
}};
return true
};
$lzsc$temp["displayName"] = "equal";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzCSSStyleRule.prototype[Debug.FUNCTION_FILENAME] = null;
LzCSSStyleRule.prototype[Debug.FUNCTION_LINENO] = null
}}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#18/1";
return $lzsc$temp
})()(LzCSSStyleRule);
lz.CSSStyleRule = LzCSSStyleRule;
Class.make("LzCSSStyleClass", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getComputedStyle", (function  () {
var $lzsc$temp = function  (node_$1) {
var csssd_$2 = new LzCSSStyleDeclaration();
csssd_$2.setNode(node_$1);
return csssd_$2
};
$lzsc$temp["displayName"] = "getComputedStyle";
return $lzsc$temp
})(), "getPropertyValueFor", (function  () {
var $lzsc$temp = function  (node_$1, pname_$2) {
var pc_$3 = node_$1["__LZPropertyCache"];
if (!pc_$3) {
pc_$3 = this.getPropertyCache(node_$1)
};
if (pname_$2 in pc_$3) {
return pc_$3[pname_$2]
};
Debug.warn("No CSS value found for node %#w for property name %s", node_$1, pname_$2);
return pc_$3[pname_$2] = void 0
};
$lzsc$temp["displayName"] = "getPropertyValueFor";
return $lzsc$temp
})(), "getPropertyCache", (function  () {
var $lzsc$temp = function  (node_$1) {
if (!node_$1 || node_$1 === canvas) {
return {}};
var pc_$2 = node_$1["__LZPropertyCache"];
if (pc_$2) {
return pc_$2
};
var ip_$3 = node_$1.immediateparent;
if (!ip_$3 || ip_$3 === canvas) {
var ipc_$4 = {}} else {
var ipc_$4 = ip_$3["__LZPropertyCache"] || this.getPropertyCache(ip_$3)
};
var rules_$5 = node_$1["__LZRuleCache"];
if (!rules_$5) {
rules_$5 = this.getRulesCache(node_$1)
};
if (rules_$5.length == 0) {
return node_$1.__LZPropertyCache = ipc_$4
};
var pc_$2 = {};
for (var k_$6 in ipc_$4) {
pc_$2[k_$6] = ipc_$4[k_$6]
};
for (var i_$7 = rules_$5.length - 1;i_$7 >= 0;i_$7--) {
var props_$8 = rules_$5[i_$7].properties;
for (var pname_$9 in props_$8) {
pc_$2[pname_$9] = props_$8[pname_$9]
}};
return node_$1.__LZPropertyCache = pc_$2
};
$lzsc$temp["displayName"] = "getPropertyCache";
return $lzsc$temp
})(), "getRulesCache", (function  () {
var $lzsc$temp = function  (node_$1) {
var rules_$2 = node_$1["__LZRuleCache"];
if (rules_$2) {
return rules_$2
};
rules_$2 = new Array();
var tryRules_$3 = new Array();
if (this._rulenum != this._lastSort) {
this._sortRules()
};
var id_$4 = node_$1["id"];
if (id_$4) {
var ir_$5 = this._idRules;
if (id_$4 in ir_$5) {
tryRules_$3 = tryRules_$3.concat(ir_$5[id_$4])
}};
var ar_$6 = this._attrRules;
for (var k_$7 in ar_$6) {
if (node_$1[k_$7]) {
var rs_$8 = ar_$6[k_$7][node_$1[k_$7]];
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(rs_$8) : rs_$8 instanceof Array) {
tryRules_$3 = tryRules_$3.concat(rs_$8)
}}};
var cr_$9 = this._tagRules;
for (var tn_$10 in cr_$9) {
var c_$11 = lz[tn_$10];
if (c_$11 && (c_$11["$lzsc$isa"] ? c_$11.$lzsc$isa(node_$1) : node_$1 instanceof c_$11)) {
tryRules_$3 = tryRules_$3.concat(cr_$9[tn_$10])
}};
var rr_$12 = this._rules;
for (var i_$13 = rr_$12.length - 1;i_$13 >= 0;i_$13--) {
tryRules_$3.push(rr_$12[i_$13])
};
var sortNeeded_$14 = false;
var lastSpecificity_$15 = Infinity;
for (var i_$13 = 0, l_$16 = tryRules_$3.length;i_$13 < l_$16;i_$13++) {
var r_$17 = tryRules_$3[i_$13];
if (!sortNeeded_$14) {
var rs_$8 = r_$17.specificity;
if (!rs_$8 || rs_$8 >= lastSpecificity_$15) {
sortNeeded_$14 = true
} else {
lastSpecificity_$15 = rs_$8
}};
var rp_$18 = r_$17.parsed;
var rpt_$19 = rp_$18.type;
var compound_$20 = rpt_$19 == this._sel_compound;
if (compound_$20) {
rp_$18 = rp_$18[rp_$18.length - 1];
rpt_$19 = rp_$18.type
};
var rptn_$21 = rp_$18.tagname;
var rpc_$22 = rptn_$21 ? lz[rptn_$21] : null;
var rpi_$23 = rp_$18["id"];
var rpa_$24 = rp_$18["attrname"];
if ((!rptn_$21 || rpc_$22 && (rpc_$22["$lzsc$isa"] ? rpc_$22.$lzsc$isa(node_$1) : node_$1 instanceof rpc_$22)) && (!rpi_$23 || node_$1["id"] == rpi_$23) && (!rpa_$24 || node_$1[rpa_$24] == rp_$18.attrvalue)) {
if (!compound_$20) {
rules_$2.push(r_$17)
} else {
if (this._compoundSelectorApplies(r_$17.parsed, node_$1)) {
rules_$2.push(r_$17)
}}}};
if (sortNeeded_$14) {
rules_$2.sort(this.__compareSpecificity)
};
node_$1.__LZRuleCache = rules_$2;
return rules_$2
};
$lzsc$temp["displayName"] = "getRulesCache";
return $lzsc$temp
})(), "getSelectorSpecificity", (function  () {
var $lzsc$temp = function  (parsedsel_$1) {
switch (parsedsel_$1.type) {
case this._sel_tag:
case this._sel_star:
return 1;;case this._sel_id:
return 100;;case this._sel_attribute:
return 10;;case this._sel_tagAndAttr:
return 11
}};
$lzsc$temp["displayName"] = "getSelectorSpecificity";
return $lzsc$temp
})(), "__compareSpecificity", (function  () {
var $lzsc$temp = function  (rA_$1, rB_$2) {
var specificityA_$3 = rA_$1.specificity;
var specificityB_$4 = rB_$2.specificity;
if (specificityA_$3 != specificityB_$4) {
return specificityA_$3 < specificityB_$4 ? 1 : -1
};
var rap_$5 = rA_$1.parsed;
var rbp_$6 = rB_$2.parsed;
var lexorder_$7 = rA_$1._lexorder < rB_$2._lexorder ? 1 : -1;
if (!rap_$5["length"] && !rbp_$6["length"]) {
var ratn_$8 = rap_$5["tagname"];
var rbtn_$9 = rbp_$6["tagname"];
if (!ratn_$8 || !rbtn_$9 || ratn_$8 == rbtn_$9) {
return lexorder_$7
};
var rac_$10 = lz[ratn_$8];
var rbc_$11 = lz[rbtn_$9];
if (rac_$10 && rbc_$11) {
if (rbc_$11.prototype.isPrototypeOf(rac_$10.prototype)) {
return -1
};
if (rac_$10.prototype.isPrototypeOf(rbc_$11.prototype)) {
return 1
}};
return lexorder_$7
};
for (var i_$12 = 0;i_$12 < rap_$5.length;i_$12++) {
var rapi_$13 = rap_$5[i_$12];
var rbpi_$14 = rbp_$6[i_$12];
if (!rapi_$13 || !rbpi_$14) {
break
};
var ratn_$8 = rapi_$13["tagname"];
var rbtn_$9 = rbpi_$14["tagname"];
if (ratn_$8 && rbtn_$9 && ratn_$8 != rbtn_$9) {
var rac_$10 = lz[ratn_$8];
var rbc_$11 = lz[rbtn_$9];
if (rac_$10 && rbc_$11) {
if (rbc_$11.prototype.isPrototypeOf(rac_$10.prototype)) {
return -1
};
if (rac_$10.prototype.isPrototypeOf(rbc_$11.prototype)) {
return 1
}}}};
return lexorder_$7
};
$lzsc$temp["displayName"] = "__compareSpecificity";
return $lzsc$temp
})(), "_printRuleArray", (function  () {
var $lzsc$temp = function  (arr_$1) {
for (var i_$2 = 0;i_$2 < arr_$1.length;i_$2++) {
Debug.write(i_$2, arr_$1[i_$2])
}};
$lzsc$temp["displayName"] = "_printRuleArray";
return $lzsc$temp
})(), "_compoundSelectorApplies", (function  () {
var $lzsc$temp = function  (parsedsel_$1, startnode_$2) {
for (var node_$3 = startnode_$2, i_$4 = parsedsel_$1.length - 1;i_$4 >= 0 && node_$3 !== canvas;i_$4--, node_$3 = node_$3.parent) {
var rp_$5 = parsedsel_$1[i_$4];
var rptn_$6 = rp_$5.tagname;
var rpc_$7 = rptn_$6 ? lz[rptn_$6] : null;
var rpi_$8 = rp_$5["id"];
var rpa_$9 = rp_$5["attrname"];
while (node_$3 !== canvas) {
if ((!rptn_$6 || rpc_$7 && (rpc_$7["$lzsc$isa"] ? rpc_$7.$lzsc$isa(node_$3) : node_$3 instanceof rpc_$7)) && (!rpi_$8 || node_$3.id == rpi_$8) && (!rpa_$9 || node_$3[rpa_$9] == rp_$5.attrvalue)) {
if (i_$4 == 0) {
return true
} else {
break
}} else {
if (node_$3 === startnode_$2) {
return false
}};
node_$3 = node_$3.parent
}};
return false
};
$lzsc$temp["displayName"] = "_compoundSelectorApplies";
return $lzsc$temp
})(), "_sel_unknown", 0, "_sel_star", 1, "_sel_id", 2, "_sel_tag", 3, "_sel_compound", 4, "_sel_attribute", 5, "_sel_tagAndAttr", 6, "_rules", new Array(), "_attrRules", {}, "_idRules", {}, "_tagRules", {}, "_rulenum", 0, "_lastSort", -1, "_sortRules", (function  () {
var $lzsc$temp = function  () {
var deleteDuplicates_$1;
deleteDuplicates_$1 = (function  () {
var $lzsc$temp = function  (sortedRules_$1) {
for (var i_$2 = sortedRules_$1.length - 2;i_$2 >= 0;i_$2--) {
if (sortedRules_$1[i_$2].equal(sortedRules_$1[i_$2 + 1])) {
sortedRules_$1.splice(i_$2 + 1, 1)
}};
return sortedRules_$1
};
$lzsc$temp["displayName"] = "deleteDuplicates";
return $lzsc$temp
})();
if (this._rulenum != this._lastSort) {
this._rules.sort(this.__compareSpecificity);
deleteDuplicates_$1(this._rules);
for (var k_$2 in this._attrRules) {
var ars_$3 = this._attrRules[k_$2];
for (var v_$4 in ars_$3) {
ars_$3[v_$4].sort(this.__compareSpecificity);
deleteDuplicates_$1(ars_$3[v_$4])
}};
for (var k_$2 in this._idRules) {
this._idRules[k_$2].sort(this.__compareSpecificity);
deleteDuplicates_$1(this._idRules[k_$2])
};
for (var k_$2 in this._tagRules) {
this._tagRules[k_$2].sort(this.__compareSpecificity);
deleteDuplicates_$1(this._tagRules[k_$2])
};
this._lastSort = this._rulenum
}};
$lzsc$temp["displayName"] = "_sortRules";
return $lzsc$temp
})(), "_addRule", (function  () {
var $lzsc$temp = function  (r_$1) {
r_$1._lexorder = this._rulenum++;
var sel_$2 = r_$1.selector;
r_$1.parsed = null;
var lastsel_$3;
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(sel_$2) : sel_$2 instanceof Array) {
r_$1.parsed = [];
r_$1.parsed.type = this._sel_compound;
for (var i_$4 = 0;i_$4 < sel_$2.length;i_$4++) {
r_$1.parsed.push(this._parseSelector(sel_$2[i_$4]))
};
lastsel_$3 = r_$1.parsed[r_$1.parsed.length - 1]
} else {
r_$1.parsed = this._parseSelector(sel_$2);
lastsel_$3 = r_$1.parsed
};
r_$1.getSpecificity();
if (lastsel_$3.type == this._sel_attribute || lastsel_$3.type == this._sel_tagAndAttr) {
var attr_$5 = lastsel_$3.attrname;
var atab_$6 = this._attrRules[attr_$5];
if (!atab_$6) {
atab_$6 = this._attrRules[attr_$5] = {}};
var val_$7 = lastsel_$3.attrvalue;
var vtab_$8 = atab_$6[val_$7];
if (!vtab_$8) {
vtab_$8 = atab_$6[val_$7] = []
};
vtab_$8.push(r_$1)
} else {
if (lastsel_$3.type == this._sel_id) {
var id_$9 = lastsel_$3.id;
if (!this._idRules[id_$9]) {
this._idRules[id_$9] = []
};
this._idRules[id_$9].push(r_$1)
} else {
if (lastsel_$3.type == this._sel_tag) {
var tag_$10 = lastsel_$3.tagname;
if (!this._tagRules[tag_$10]) {
this._tagRules[tag_$10] = []
};
this._tagRules[tag_$10].push(r_$1)
} else {
Debug.error("Unknown cohort for rule: %w", r_$1);
this._rules.push(r_$1)
}}}};
$lzsc$temp["displayName"] = "_addRule";
return $lzsc$temp
})(), "_parseSelector", (function  () {
var $lzsc$temp = function  (sel_$1) {
switch (typeof sel_$1) {
case "object":
if (sel_$1.simpleselector) {
sel_$1.type = this._sel_tagAndAttr;
sel_$1.tagname = sel_$1.simpleselector
} else {
sel_$1.type = this._sel_attribute
};
return sel_$1;
break;;case "string":
return this._parseStringSelector(sel_$1);
break
}};
$lzsc$temp["displayName"] = "_parseSelector";
return $lzsc$temp
})(), "_parseStringSelector", (function  () {
var $lzsc$temp = function  (sel_$1) {
var parsed_$2 = {};
if (sel_$1 == "*") {
parsed_$2.type = this._sel_star
} else {
var index_$3 = sel_$1.indexOf("#");
if (index_$3 >= 0) {
parsed_$2.id = sel_$1.substring(index_$3 + 1);
parsed_$2.type = this._sel_id
} else {
parsed_$2.type = this._sel_tag;
parsed_$2.tagname = sel_$1
}};
return parsed_$2
};
$lzsc$temp["displayName"] = "_parseStringSelector";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#131/1";
return $lzsc$temp
})()(LzCSSStyleClass);
var LzCSSStyle = new LzCSSStyleClass();
lz.CSSStyle = LzCSSStyleClass;
Class.make("LzCSSStyleDeclaration", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {

};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "_node", null, "getPropertyValue", (function  () {
var $lzsc$temp = function  (pname_$1) {
return LzCSSStyle.getPropertyValueFor(this._node, pname_$1)
};
$lzsc$temp["displayName"] = "getPropertyValue";
return $lzsc$temp
})(), "setNode", (function  () {
var $lzsc$temp = function  (node_$1) {
this._node = node_$1
};
$lzsc$temp["displayName"] = "setNode";
return $lzsc$temp
})()], null);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {

}}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#659/1";
return $lzsc$temp
})()(LzCSSStyleDeclaration);
lz.CSSStyleDeclaration = LzCSSStyleDeclaration;
Class.make("LzStyleSheet", null, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (title_$1, href_$2, media_$3, sstype_$4) {
this.type = sstype_$4;
this.disabled = false;
this.ownerNode = null;
this.parentStyleSheet = null;
this.href = href_$2;
this.title = title_$1;
this.media = media_$3
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "type", null, "disabled", null, "ownerNode", null, "parentStyleSheet", null, "href", null, "title", null, "media", null], null);
Class.make("LzCSSStyleSheet", LzStyleSheet, ["$lzsc$initialize", (function  () {
var $lzsc$temp = function  (title_$1, href_$2, media_$3, sstype_$4, ownerRule_$5, cssRules_$6) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, title_$1, href_$2, media_$3, sstype_$4);
this.ownerRule = ownerRule_$5;
this.cssRules = cssRules_$6
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ownerRule", null, "cssRules", null, "insertRule", (function  () {
var $lzsc$temp = function  (rule_$1, index_$2) {
if (!this.cssRules) {
this.cssRules = []
};
if (index_$2 < 0) {
return null
};
if (index_$2 < this.cssRules.length) {
this.cssRules.splice(index_$2, 0, rule_$1);
return index_$2
};
if (index_$2 == this.cssRules.length) {
return this.cssRules.push(rule_$1) - 1
};
return null
};
$lzsc$temp["displayName"] = "insertRule";
return $lzsc$temp
})()], null);
lz.CSSStyleSheet = LzCSSStyleSheet;
Class.make("LzFocusService", LzEventable, ["onfocus", LzDeclaredEvent, "onescapefocus", LzDeclaredEvent, "lastfocus", null, "csel", null, "cseldest", null, "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.upDel = new LzDelegate(this, "gotKeyUp", lz.Keys, "onkeyup");
this.downDel = new LzDelegate(this, "gotKeyDown", lz.Keys, "onkeydown");
this.lastfocusDel = new LzDelegate(lz.Keys, "gotLastFocus", this, "onescapefocus")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "upDel", void 0, "downDel", void 0, "lastfocusDel", void 0, "focuswithkey", false, "__LZskipblur", false, "__LZsfnextfocus", -1, "__LZsfrunning", false, "gotKeyUp", (function  () {
var $lzsc$temp = function  (kC_$1) {
if (this.csel && this.csel.onkeyup.ready) {
this.csel.onkeyup.sendEvent(kC_$1)
}};
$lzsc$temp["displayName"] = "gotKeyUp";
return $lzsc$temp
})(), "gotKeyDown", (function  () {
var $lzsc$temp = function  (kC_$1) {
if (this.csel && this.csel.onkeydown.ready) {
this.csel.onkeydown.sendEvent(kC_$1)
};
if (kC_$1 == lz.Keys.keyCodes.tab) {
if (lz.Keys.isKeyDown("shift")) {
this.prev()
} else {
this.next()
}}};
$lzsc$temp["displayName"] = "gotKeyDown";
return $lzsc$temp
})(), "__LZcheckFocusChange", (function  () {
var $lzsc$temp = function  (v_$1) {
if (v_$1.focusable) {
this.setFocus(v_$1, false)
}};
$lzsc$temp["displayName"] = "__LZcheckFocusChange";
return $lzsc$temp
})(), "setFocus", (function  () {
var $lzsc$temp = function  (newsel_$1, fwkey_$2) {
switch (arguments.length) {
case 1:
fwkey_$2 = null
};
if (this.__LZsfrunning) {
this.__LZsfnextfocus = newsel_$1;
return
};
if (this.cseldest == newsel_$1) {
return
};
var prevsel_$3 = this.csel;
if (prevsel_$3 && !prevsel_$3.shouldYieldFocus()) {
return
};
if (newsel_$1 && !newsel_$1.focusable) {
newsel_$1 = this.getNext(newsel_$1);
if (this.cseldest == newsel_$1) {
return
}};
if (prevsel_$3) {
prevsel_$3.blurring = true
};
this.__LZsfnextfocus = -1;
this.__LZsfrunning = true;
this.cseldest = newsel_$1;
if (fwkey_$2 != null) {
this.focuswithkey = !(!fwkey_$2)
};
if (!this.__LZskipblur) {
this.__LZskipblur = true;
if (prevsel_$3 && prevsel_$3.onblur.ready) {
prevsel_$3.onblur.sendEvent(newsel_$1);
var next_$4 = this.__LZsfnextfocus;
if (next_$4 != -1) {
if (next_$4 && !next_$4.focusable) {
next_$4 = this.getNext(next_$4)
};
if (next_$4 != newsel_$1) {
this.__LZsfrunning = false;
this.setFocus(next_$4);
return
}}}};
this.lastfocus = prevsel_$3;
this.csel = newsel_$1;
this.__LZskipblur = false;
if (prevsel_$3) {
prevsel_$3.blurring = false
};
if ($dhtml && canvas.accessible) {
if (newsel_$1 && newsel_$1.sprite != null) {
newsel_$1.sprite.aafocus()
}};
if (newsel_$1 && newsel_$1.onfocus.ready) {
newsel_$1.onfocus.sendEvent(newsel_$1);
var next_$4 = this.__LZsfnextfocus;
if (next_$4 != -1) {
if (next_$4 && !next_$4.focusable) {
next_$4 = this.getNext(next_$4)
};
if (next_$4 != newsel_$1) {
this.__LZsfrunning = false;
this.setFocus(next_$4);
return
}}};
if (this.onfocus.ready) {
this.onfocus.sendEvent(newsel_$1);
var next_$4 = this.__LZsfnextfocus;
if (next_$4 != -1) {
if (next_$4 && !next_$4.focusable) {
next_$4 = this.getNext(next_$4)
};
if (next_$4 != newsel_$1) {
this.__LZsfrunning = false;
this.setFocus(next_$4);
return
}}};
this.__LZsfrunning = false
};
$lzsc$temp["displayName"] = "setFocus";
return $lzsc$temp
})(), "clearFocus", (function  () {
var $lzsc$temp = function  () {
this.setFocus(null)
};
$lzsc$temp["displayName"] = "clearFocus";
return $lzsc$temp
})(), "getFocus", (function  () {
var $lzsc$temp = function  () {
return this.csel
};
$lzsc$temp["displayName"] = "getFocus";
return $lzsc$temp
})(), "next", (function  () {
var $lzsc$temp = function  () {
this.genMoveSelection(1)
};
$lzsc$temp["displayName"] = "next";
return $lzsc$temp
})(), "prev", (function  () {
var $lzsc$temp = function  () {
this.genMoveSelection(-1)
};
$lzsc$temp["displayName"] = "prev";
return $lzsc$temp
})(), "getNext", (function  () {
var $lzsc$temp = function  (focusview_$1) {
switch (arguments.length) {
case 0:
focusview_$1 = null
};
return this.moveSelSubview(focusview_$1 || this.csel, 1, false)
};
$lzsc$temp["displayName"] = "getNext";
return $lzsc$temp
})(), "getPrev", (function  () {
var $lzsc$temp = function  (focusview_$1) {
switch (arguments.length) {
case 0:
focusview_$1 = null
};
return this.moveSelSubview(focusview_$1 || this.csel, -1, false)
};
$lzsc$temp["displayName"] = "getPrev";
return $lzsc$temp
})(), "genMoveSelection", (function  () {
var $lzsc$temp = function  (movedir_$1) {
var sel_$2 = this.csel;
var check_$3 = sel_$2;
while (sel_$2 && check_$3 != canvas) {
if (!check_$3.visible) {
sel_$2 = null
};
check_$3 = check_$3.immediateparent
};
if (sel_$2 == null) {
sel_$2 = lz.ModeManager.getModalView()
};
var meth_$4 = "get" + (movedir_$1 == 1 ? "Next" : "Prev") + "Selection";
var v_$5 = sel_$2 ? sel_$2[meth_$4]() : null;
if (v_$5 == null) {
v_$5 = this.moveSelSubview(sel_$2, movedir_$1, true)
};
if (lz.ModeManager.__LZallowFocus(v_$5)) {
this.setFocus(v_$5, true)
}};
$lzsc$temp["displayName"] = "genMoveSelection";
return $lzsc$temp
})(), "accumulateSubviews", (function  () {
var $lzsc$temp = function  (accum_$1, v_$2, includep_$3, top_$4) {
if (v_$2 == includep_$3 || v_$2.focusable && v_$2.visible) {
accum_$1.push(v_$2)
};
if (top_$4 || !v_$2.focustrap && v_$2.visible) {
for (var i_$5 = 0;i_$5 < v_$2.subviews.length;i_$5++) {
this.accumulateSubviews(accum_$1, v_$2.subviews[i_$5], includep_$3, false)
}}};
$lzsc$temp["displayName"] = "accumulateSubviews";
return $lzsc$temp
})(), "moveSelSubview", (function  () {
var $lzsc$temp = function  (v_$1, mvdir_$2, sendEsc_$3) {
var root_$4 = v_$1 || canvas;
while (!root_$4.focustrap && root_$4.immediateparent && root_$4 != root_$4.immediateparent) {
root_$4 = root_$4.immediateparent
};
var focusgroup_$5 = [];
this.accumulateSubviews(focusgroup_$5, root_$4, v_$1, true);
var index_$6 = -1;
var fglen_$7 = focusgroup_$5.length;
var escape_$8 = false;
for (var i_$9 = 0;i_$9 < fglen_$7;++i_$9) {
if (focusgroup_$5[i_$9] === v_$1) {
escape_$8 = mvdir_$2 == -1 && i_$9 == 0 || mvdir_$2 == 1 && i_$9 == fglen_$7 - 1;
index_$6 = i_$9;
break
}};
if (sendEsc_$3 && escape_$8) {
this.onescapefocus.sendEvent()
};
if (index_$6 == -1 && mvdir_$2 == -1) {
index_$6 = 0
};
index_$6 = (index_$6 + mvdir_$2 + fglen_$7) % fglen_$7;
return focusgroup_$5[index_$6]
};
$lzsc$temp["displayName"] = "moveSelSubview";
return $lzsc$temp
})()], ["LzFocus", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzFocusService.LzFocus = new LzFocusService()
}}};
$lzsc$temp["displayName"] = "services/LzFocus.lzs#26/1";
return $lzsc$temp
})()(LzFocusService);
lz.FocusService = LzFocusService;
lz.Focus = LzFocusService.LzFocus;
Class.make("LzTimerService", null, ["timerList", new Object(), "$lzsc$initialize", (function  () {
var $lzsc$temp = function  () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "execDelegate", (function  () {
var $lzsc$temp = function  (p_$1) {
var del_$2 = p_$1.delegate;
lz.Timer.removeTimerWithID(del_$2, p_$1.id);
if (del_$2.enabled && del_$2.c) {
del_$2.execute(new Date().getTime())
}};
$lzsc$temp["displayName"] = "services/LzTimer.lzs#105/33";
return $lzsc$temp
})(), "removeTimerWithID", (function  () {
var $lzsc$temp = function  (d_$1, id_$2) {
var delID_$3 = d_$1.__delegateID;
var tle_$4 = this.timerList[delID_$3];
if (tle_$4 != null) {
if (tle_$4 instanceof Array) {
for (var i_$5 = 0;i_$5 < tle_$4.length;i_$5++) {
if (tle_$4[i_$5] == id_$2) {
tle_$4.splice(i_$5, 1);
if (tle_$4.length == 0) {
delete this.timerList[delID_$3]
};
break
}}} else {
if (tle_$4 == id_$2) {
delete this.timerList[delID_$3]
}}}};
$lzsc$temp["displayName"] = "removeTimerWithID";
return $lzsc$temp
})(), "addTimer", (function  () {
var $lzsc$temp = function  (d_$1, millisecs_$2) {
if (!millisecs_$2 || millisecs_$2 < 1) {
millisecs_$2 = 1
};
var p_$3 = {delegate: d_$1};
var id_$4 = LzTimeKernel.setTimeout(this.execDelegate, millisecs_$2, p_$3);
p_$3.id = id_$4;
if (id_$4 instanceof Array) {
Debug.error("setTimeout result type is unexpected; lz.Timer will fail")
};
var delID_$5 = d_$1.__delegateID;
var tle_$6 = this.timerList[delID_$5];
if (tle_$6 == null) {
this.timerList[delID_$5] = id_$4
} else {
if (!(tle_$6 instanceof Array)) {
this.timerList[delID_$5] = [tle_$6, id_$4]
} else {
tle_$6.push(id_$4)
}};
return id_$4
};
$lzsc$temp["displayName"] = "addTimer";
return $lzsc$temp
})(), "removeTimer", (function  () {
var $lzsc$temp = function  (d_$1) {
var delID_$2 = d_$1.__delegateID;
var tle_$3 = this.timerList[delID_$2];
var id_$4 = null;
if (tle_$3 != null) {
if (tle_$3 instanceof Array) {
id_$4 = tle_$3.shift();
LzTimeKernel.clearTimeout(id_$4);
if (tle_$3.length == 0) {
delete this.timerList[delID_$2]
}} else {
id_$4 = tle_$3;
LzTimeKernel.clearTimeout(id_$4);
delete this.timerList[delID_$2]
}};
return id_$4
};
$lzsc$temp["displayName"] = "removeTimer";
return $lzsc$temp
})(), "resetTimer", (function  () {
var $lzsc$temp = function  (d_$1, millisecs_$2) {
this.removeTimer(d_$1);
return this.addTimer(d_$1, millisecs_$2)
};
$lzsc$temp["displayName"] = "resetTimer";
return $lzsc$temp
})(), "countTimers", (function  () {
var $lzsc$temp = function  (d_$1) {
var tle_$2 = this.timerList[d_$1.__delegateID];
if (tle_$2 == null) {
return 0
} else {
if (tle_$2 instanceof Array) {
return tle_$2.length
} else {
return 1
}}};
$lzsc$temp["displayName"] = "countTimers";
return $lzsc$temp
})()], ["LzTimer", void 0]);
(function  () {
var $lzsc$temp = function  ($lzsc$c_$1) {
with ($lzsc$c_$1) {
with ($lzsc$c_$1.prototype) {
LzTimerService.LzTimer = new LzTimerService()
}}};
$lzsc$temp["displayName"] = "services/LzTimer.lzs#70/1";
return $lzsc$temp
})()(LzTimerService);
lz.TimerService = LzTimerService;
lz.Timer = LzTimerService.LzTimer;
(function  () {
var $lzsc$temp = function  () {
for (var k_$1 in lz) {
var v_$2 = lz[k_$1];
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(v_$2) : v_$2 instanceof Function) && v_$2["_dbg_name"] == null) {
try {
v_$2._dbg_name = "lz." + k_$1
}
catch (e) {

}}}};
$lzsc$temp["displayName"] = "debugger/LzInit.lzs#11/4";
return $lzsc$temp
})()();