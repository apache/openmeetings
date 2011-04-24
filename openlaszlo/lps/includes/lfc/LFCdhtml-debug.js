var $runtime = "dhtml";
var $dhtml = true;
var $as3 = false;
var $as2 = false;
var $swf10 = false;
var $j2me = false;
var $debug = true;
var $js1 = true;
var $backtrace = false;
var $swf7 = false;
var $swf9 = false;
var $swf8 = false;
var $svg = false;
var $profile = false;
var _Copyright = "Portions of this file are copyright (c) 2001-2010 by Laszlo Systems, Inc.  All rights reserved.";
var Debug = {backtraceStack: [], uncaughtBacktraceStack: null, FUNCTION_NAME: "displayName", FUNCTION_FILENAME: "_dbg_filename", FUNCTION_LINENO: "_dbg_lineno"};
Debug.backtraceStack.maxDepth = 100;
var $modules = {};
$modules.runtime = this;
$modules.lz = $modules.runtime;
$modules.user = $modules.lz;
var global = $modules.user;
var __ES3Globals = {Array: Array, Boolean: Boolean, Date: Date, Function: Function, Math: Math, Number: Number, Object: Object, RegExp: RegExp, String: String, Error: Error, EvalError: EvalError, RangeError: RangeError, ReferenceError: ReferenceError, SyntaxError: SyntaxError, TypeError: TypeError, URIError: URIError};
var globalValue = (function () {
var $lzsc$temp = function (name_$0) {
if (name_$0.charAt(0) == "<" && name_$0.charAt(name_$0.length - 1) == ">") {
return lz[name_$0.substring(1, name_$0.length - 1)]
} else if (name_$0 in this) {
return this[name_$0]
} else if (name_$0 in global) {
return global[name_$0]
} else if (name_$0 in __ES3Globals) {
return __ES3Globals[name_$0]
};
return void 0
};
$lzsc$temp["displayName"] = "compiler/LzRuntime.lzs#95/19";
return $lzsc$temp
})();
var $lzsc$issubclassof = (function () {
var $lzsc$temp = function (one_$0, other_$1) {
return one_$0 === other_$1 || (other_$1["$lzsc$isa"] ? other_$1.$lzsc$isa(one_$0.prototype) : one_$0.prototype instanceof other_$1)
};
$lzsc$temp["displayName"] = "compiler/LzRuntime.lzs#148/26";
return $lzsc$temp
})();
var $lzc$validateReferenceDependencies = (function () {
var $lzsc$temp = function (dependencies_$0, referenceNames_$1) {
for (var i_$2 = 0, l_$3 = referenceNames_$1.length;i_$2 < l_$3;i_$2++) {
var j_$4 = i_$2 + i_$2;
var dc_$5 = dependencies_$0[j_$4];
var dp_$6 = dependencies_$0[j_$4 + 1];
if (!(LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(dc_$5) : dc_$5 instanceof LzEventable)) {
dependencies_$0[j_$4] = new Error("Invalid dependency context");
dependencies_$0[j_$4 + 1] = Debug.formatToString("Unable to create dependency on %=s.%s", dc_$5, referenceNames_$1[i_$2], dp_$6)
}};
return dependencies_$0
};
$lzsc$temp["displayName"] = "compiler/LzRuntime.lzs#177/44";
return $lzsc$temp
})();
var $lzc$getFunctionDependencies = (function () {
var $lzsc$temp = function (fnnm_$0, self_$1, context_$2, args_$3, ctnm_$4) {
switch (arguments.length) {
case 4:
ctnm_$4 = null
};
var deps_$5 = [], depfn_$6 = null;
try {
depfn_$6 = context_$2["$lzc$" + fnnm_$0 + "_dependencies"]
}
catch (e_$7) {};
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(depfn_$6) : depfn_$6 instanceof Function)) {
return [new Error("Invalid dependency method"), Debug.formatToString("Unable to create dependency on %=s.%s", context_$2, ctnm_$4, fnnm_$0)]
} else {
try {
deps_$5 = depfn_$6.apply(context_$2, [self_$1, context_$2].concat(args_$3))
}
catch (e_$7) {
Debug.warn("Error: %w computing dependencies of %.64w.%s", e_$7, context_$2, fnnm_$0)
}};
return deps_$5
};
$lzsc$temp["displayName"] = "compiler/LzRuntime.lzs#194/36";
return $lzsc$temp
})();
var Instance = (function () {
var $lzsc$temp = function () {
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
(function () {
var $lzsc$temp = function () {
var addProperties_$0 = (function () {
var $lzsc$temp = function (plist_$0) {
for (var i_$1 = plist_$0.length - 1;i_$1 >= 1;i_$1 -= 2) {
var value_$2 = plist_$0[i_$1];
var name_$3 = plist_$0[i_$1 - 1];
if (value_$2 !== void 0 || !(name_$3 in this)) {
this[name_$3] = value_$2
};
if (!(value_$2 instanceof Function)) continue;
var xtor_$4 = this.constructor;
if (value_$2.hasOwnProperty("$superclasses")) {
var os_$5 = value_$2.$superclasses, found_$6 = false;
for (var j_$7 = os_$5.length - 1;j_$7 >= 0;j_$7--) {
if (os_$5[j_$7] === xtor_$4) {
found_$6 = true;
break
}};
if (!found_$6) {
value_$2.$superclasses.push(xtor_$4)
}} else if (value_$2.hasOwnProperty("$superclass") && value_$2.$superclass !== xtor_$4) {
var $8 = value_$2.$superclass;
delete value_$2.$superclass;
value_$2.$superclasses = [$8, xtor_$4]
} else {
value_$2.$superclass = xtor_$4
};
if (!value_$2._dbg_typename) {
value_$2._dbg_owner = this;
value_$2._dbg_typename = (function () {
var $lzsc$temp = function () {
var t_$0 = Debug.functionName(this._dbg_owner._dbg_prototype_for);
return t_$0 + " function"
};
$lzsc$temp["displayName"] = "_dbg_typename";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "addProperties";
return $lzsc$temp
})();
addProperties_$0.call(Instance.prototype, ["addProperties", addProperties_$0])
};
$lzsc$temp["displayName"] = "compiler/Class.lzs#54/2";
return $lzsc$temp
})()();
Instance.prototype.addProperties(["addProperty", (function () {
var $lzsc$temp = function (name_$0, value_$1) {
this.addProperties([name_$0, value_$1])
};
$lzsc$temp["displayName"] = "addProperty";
return $lzsc$temp
})()]);
Instance.prototype.addProperty("nextMethod", (function () {
var $lzsc$temp = function (currentMethod, nextMethodName) {
var next_$0;
if (currentMethod.hasOwnProperty("$superclass")) {
next_$0 = currentMethod.$superclass.prototype[nextMethodName]
} else if (currentMethod.hasOwnProperty("$superclasses")) {
var $1 = currentMethod.$superclasses;
for (var i_$2 = $1.length - 1;i_$2 >= 0;i_$2--) {
var sc_$3 = $1[i_$2];
if (this instanceof sc_$3) {
next_$0 = sc_$3.prototype[nextMethodName];
break
}}};
if (!next_$0) {
next_$0 = (function () {
var $lzsc$temp = function () {
Debug.error("super.%s is undefined in %w", nextMethodName, currentMethod)
};
$lzsc$temp["displayName"] = "compiler/Class.lzs#166/12";
return $lzsc$temp
})()
};
return next_$0
};
$lzsc$temp["displayName"] = "nextMethod";
return $lzsc$temp
})());
Instance.prototype.addProperty("$lzsc$initialize", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})());
if (lz.embed.browser.isSafari && lz.embed.browser.version >= "531.21" && lz.embed.browser.version < "533.16") {
Instance.prototype.addProperty("$lzsc$safarikludge", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "$lzsc$safarikludge";
return $lzsc$temp
})())
};
var Class = {prototype: new Instance(), addProperty: Instance.prototype.addProperty, addProperties: (function () {
var $lzsc$temp = function (plist_$0) {
this.prototype.addProperties(plist_$0)
};
$lzsc$temp["displayName"] = "addProperties";
return $lzsc$temp
})(), addStaticProperty: (function () {
var $lzsc$temp = function (name_$0, value_$1) {
this[name_$0] = value_$1;
if (value_$1 instanceof Function && !value_$1._dbg_typename) {
value_$1._dbg_owner = this;
value_$1._dbg_typename = (function () {
var $lzsc$temp = function () {
return Debug.functionName(this._dbg_owner) + " static function"
};
$lzsc$temp["displayName"] = "_dbg_typename";
return $lzsc$temp
})()
}};
$lzsc$temp["displayName"] = "addStaticProperty";
return $lzsc$temp
})(), allClasses: {Instance: Instance}, make: (function () {
var $lzsc$temp = function (classname_$0, instanceProperties, mixinsAndSuperclass_$1, staticProperties_$2, interfaces_$3) {
switch (arguments.length) {
case 1:
instanceProperties = null;;case 2:
mixinsAndSuperclass_$1 = null;;case 3:
staticProperties_$2 = null;;case 4:
interfaces_$3 = null
};
var superclass = null;
if (mixinsAndSuperclass_$1 instanceof Array) {
for (var k_$4 = mixinsAndSuperclass_$1.length - 1;k_$4 >= 0;k_$4--) {
var c_$5 = mixinsAndSuperclass_$1[k_$4];
if (c_$5 instanceof Function) {
if (superclass) {
Debug.error("Class.make: Multiple superclasses %s and %s for class %s", superclass, c_$5, classname_$0)
};
mixinsAndSuperclass_$1.splice(k_$4, 1);
superclass = c_$5
}}} else if (mixinsAndSuperclass_$1 instanceof Function) {
superclass = mixinsAndSuperclass_$1;
mixinsAndSuperclass_$1 = null
} else {
if (mixinsAndSuperclass_$1) {
Debug.error("Class.make: invalid superclass %w", mixinsAndSuperclass_$1)
}};
if (!superclass) {
superclass = Instance
};
var nc_$6 = (function () {
var $lzsc$temp = function () {
this.constructor = arguments.callee;
if (this["$lzsc$safarikludge"] && this.$lzsc$safarikludge !== Instance.prototype.$lzsc$safarikludge) {
this.$lzsc$safarikludge()
};
if (this.$lzsc$initialize !== Instance.prototype.$lzsc$initialize) {
this.$lzsc$initialize.apply(this, arguments)
}};
$lzsc$temp["displayName"] = "constructor";
return $lzsc$temp
})();
nc_$6.constructor = this;
nc_$6.classname = classname_$0;
nc_$6._dbg_typename = this._dbg_name;
nc_$6[Debug.FUNCTION_NAME] = classname_$0;
var xtor_$7 = (function () {
var $lzsc$temp = function () {
this.constructor = superclass
};
$lzsc$temp["displayName"] = "prototype";
return $lzsc$temp
})();
xtor_$7.prototype = superclass.prototype;
var prototype_$8 = new xtor_$7();
if (mixinsAndSuperclass_$1 instanceof Array) {
for (var i_$9 = mixinsAndSuperclass_$1.length - 1;i_$9 >= 0;i_$9--) {
var t_$a = mixinsAndSuperclass_$1[i_$9];
prototype_$8 = t_$a.makeInterstitial(prototype_$8, i_$9 > 0 ? mixinsAndSuperclass_$1[i_$9 - 1] : nc_$6)
}};
if (interfaces_$3 instanceof Array) {
for (var i_$9 = interfaces_$3.length - 1;i_$9 >= 0;i_$9--) {
var t_$a = interfaces_$3[i_$9];
t_$a.addImplementation(classname_$0, nc_$6)
}};
nc_$6.prototype = prototype_$8;
prototype_$8._dbg_prototype_for = nc_$6;
this.addStaticProperty.call(nc_$6, "addStaticProperty", this.addStaticProperty);
nc_$6.addStaticProperty("addProperty", this.addProperty);
nc_$6.addStaticProperty("addProperties", this.addProperties);
if (staticProperties_$2) {
for (var i_$9 = staticProperties_$2.length - 1;i_$9 >= 1;i_$9 -= 2) {
var value_$b = staticProperties_$2[i_$9];
var name_$c = staticProperties_$2[i_$9 - 1];
nc_$6.addStaticProperty(name_$c, value_$b)
}};
if (instanceProperties) {
nc_$6.addProperties(instanceProperties);
if (lz.embed.browser.isSafari && lz.embed.browser.version == "531.21") {
nc_$6.addProperty("$lzsc$safarikludge", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$safarikludge"] || this.nextMethod(arguments.callee, "$lzsc$safarikludge")).call(this);
for (var i_$0 = instanceProperties.length - 1;i_$0 >= 1;i_$0 -= 2) {
var value_$1 = instanceProperties[i_$0];
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(value_$1) : value_$1 instanceof Function) continue;
var name_$2 = instanceProperties[i_$0 - 1];
if (value_$1 !== void 0 || !(name_$2 in this)) {
this[name_$2] = value_$1
}}};
$lzsc$temp["displayName"] = "$lzsc$safarikludge";
return $lzsc$temp
})())
}};
if (this.allClasses[classname_$0]) {
Debug.error("Redefining %s from %w to %w", classname_$0, this.allClasses[classname_$0], nc_$6)
};
global[classname_$0] = this.allClasses[classname_$0] = nc_$6;
return nc_$6
};
$lzsc$temp["displayName"] = "make";
return $lzsc$temp
})()};
Class._dbg_typename = "Metaclass";
Class._dbg_name = "Class";
Class.addProperties._dbg_typename = "Class static function";
Class.addStaticProperty._dbg_typename = "Class static function";
Class.make._dbg_typename = "Class static function";
var Mixin = {prototype: new Instance(), allMixins: {}, addProperty: (function () {
var $lzsc$temp = function (name_$0, value_$1) {
this.prototype[name_$0] = value_$1;
this.instanceProperties.push(name_$0, value_$1);
var impls_$2 = this.implementations;
for (var mash_$3 in impls_$2) {
var t_$4 = impls_$2[mash_$3];
t_$4.addProperty(name_$0, value_$1)
};
if (value_$1 instanceof Function && !value_$1._dbg_typename) {
value_$1._dbg_typename = this.classname + " function"
}};
$lzsc$temp["displayName"] = "addProperty";
return $lzsc$temp
})(), addStaticProperty: (function () {
var $lzsc$temp = function (name_$0, value_$1) {
this[name_$0] = value_$1;
if (value_$1 instanceof Function && !value_$1._dbg_typename) {
value_$1._dbg_typename = this.classname + " static function"
}};
$lzsc$temp["displayName"] = "addStaticProperty";
return $lzsc$temp
})(), makeInterstitial: (function () {
var $lzsc$temp = function (superclassInstance_$0, sharable_$1) {
var impls_$2 = this.implementations;
var interstitialname_$3 = this.classname + "+" + superclassInstance_$0.constructor.classname;
var prototypename_$4 = sharable_$1.classname + "|" + interstitialname_$3;
if (impls_$2[prototypename_$4]) {
Debug.debug("Sharable interstitial: %s", prototypename_$4);
return impls_$2[prototypename_$4]
};
superclassInstance_$0.addProperties(this.instanceProperties);
var xtor_$5 = (function () {
var $lzsc$temp = function () {
this.constructor = arguments.callee
};
$lzsc$temp["displayName"] = "interstitial";
return $lzsc$temp
})();
xtor_$5.prototype = superclassInstance_$0;
xtor_$5.classname = interstitialname_$3;
xtor_$5._dbg_typename = "Interstitial";
xtor_$5[Debug.FUNCTION_NAME] = xtor_$5.classname;
var t_$6 = new xtor_$5();
impls_$2[prototypename_$4] = t_$6;
return t_$6
};
$lzsc$temp["displayName"] = "makeInterstitial";
return $lzsc$temp
})(), addImplementation: (function () {
var $lzsc$temp = function (classname_$0, constructor_$1) {
this.implementations[classname_$0] = {constructor: constructor_$1}};
$lzsc$temp["displayName"] = "addImplementation";
return $lzsc$temp
})(), $lzsc$isa: (function () {
var $lzsc$temp = function (obj_$0) {
var impls_$1 = this.implementations;
for (var prototypename_$2 in impls_$1) {
if (obj_$0 instanceof impls_$1[prototypename_$2].constructor) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "$lzsc$isa";
return $lzsc$temp
})(), make: (function () {
var $lzsc$temp = function (classname_$0, instanceProperties_$1, superMixin_$2, staticProperties_$3, interfaces_$4) {
switch (arguments.length) {
case 1:
instanceProperties_$1 = null;;case 2:
superMixin_$2 = null;;case 3:
staticProperties_$3 = null;;case 4:
interfaces_$4 = null
};
var nt_$5 = {constructor: this, classname: classname_$0, _dbg_typename: this._dbg_name, _dbg_name: classname_$0, prototype: superMixin_$2 ? superMixin_$2.make() : new Object(), instanceProperties: superMixin_$2 ? superMixin_$2.instanceProperties.slice(0) : new Array(), implementations: {}};
this.addStaticProperty.call(nt_$5, "addStaticProperty", this.addStaticProperty);
nt_$5.addStaticProperty("addProperty", this.addProperty);
nt_$5.addStaticProperty("makeInterstitial", this.makeInterstitial);
nt_$5.addStaticProperty("addImplementation", this.addImplementation);
nt_$5.addStaticProperty("$lzsc$isa", this.$lzsc$isa);
if (staticProperties_$3) {
for (var i_$6 = staticProperties_$3.length - 1;i_$6 >= 1;i_$6 -= 2) {
var value_$7 = staticProperties_$3[i_$6];
var name_$8 = staticProperties_$3[i_$6 - 1];
nt_$5.addStaticProperty(name_$8, value_$7)
}};
if (instanceProperties_$1) {
for (var i_$6 = instanceProperties_$1.length - 1;i_$6 >= 1;i_$6 -= 2) {
var value_$7 = instanceProperties_$1[i_$6];
var name_$8 = instanceProperties_$1[i_$6 - 1];
nt_$5.addProperty(name_$8, value_$7)
}};
global[classname_$0] = this.allMixins[classname_$0] = nt_$5;
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
Class.make("LzDebugConsole", ["saved_msgs", void 0, "canvasConsoleWindow", (function () {
var $lzsc$temp = function () {
return null
};
$lzsc$temp["displayName"] = "canvasConsoleWindow";
return $lzsc$temp
})(), "addText", (function () {
var $lzsc$temp = function (msg_$0) {
var str_$1;
try {
if (msg_$0 && (Function["$lzsc$isa"] ? Function.$lzsc$isa(msg_$0["toHTML"]) : msg_$0["toHTML"] instanceof Function)) {
str_$1 = msg_$0.toHTML()
} else {
str_$1 = String(msg_$0)["toHTML"]()
}}
catch (e_$2) {
str_$1 = "" + msg_$0
};
if (navigator.platform == "rhino") {
try {
print(str_$1);
return
}
catch (e_$2) {}};
this.addHTMLText(str_$1)
};
$lzsc$temp["displayName"] = "addText";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "ensureVisible", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "ensureVisible";
return $lzsc$temp
})(), "echo", (function () {
var $lzsc$temp = function (str_$0, newLine_$1) {
switch (arguments.length) {
case 1:
newLine_$1 = true
}};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "addHTMLText", (function () {
var $lzsc$temp = function (msg_$0) {};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "makeObjectLink", (function () {
var $lzsc$temp = function (rep_$0, id_$1, attrs_$2) {
switch (arguments.length) {
case 2:
attrs_$2 = null
};
return undefined
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "SimpleExprPattern", new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$"), "ElementPattern", new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)", "g"), "isSimpleExpr", (function () {
var $lzsc$temp = function (expr_$0) {
return expr_$0.match(this.SimpleExprPattern)
};
$lzsc$temp["displayName"] = "isSimpleExpr";
return $lzsc$temp
})(), "evalSimpleExpr", (function () {
var $lzsc$temp = function (expr_$0) {
try {
var parts_$1 = expr_$0.match(this.ElementPattern);
var val_$2 = globalValue(parts_$1[0]) || Debug.environment[parts_$1[0]];
for (var i_$3 = 1, l_$4 = parts_$1.length;i_$3 < l_$4;i_$3++) {
val_$2 = val_$2[parts_$1[i_$3]]
};
return val_$2
}
catch ($lzsc$e) {
if (Error["$lzsc$isa"] ? Error.$lzsc$isa($lzsc$e) : $lzsc$e instanceof Error) {
lz.$lzsc$thrownError = $lzsc$e
};
throw $lzsc$e
}};
$lzsc$temp["displayName"] = "evalSimpleExpr";
return $lzsc$temp
})(), "doEval", (function () {
var $lzsc$temp = function (expr_$0) {
if (this.isSimpleExpr(expr_$0)) {
var simple_$1 = true;
try {
var val_$2 = this.evalSimpleExpr(expr_$0);
if (val_$2 === void 0) {
simple_$1 = false
}}
catch (e_$3) {
simple_$1 = false
}};
if (simple_$1) {
Debug.displayResult(val_$2)
} else {
Debug.warn("Unable to evaluate %s", expr_$0)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#20/1";
return $lzsc$temp
})()(LzDebugConsole);
Class.make("LzBootstrapDebugConsole", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
this.saved_msgs = new Array()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "echo", (function () {
var $lzsc$temp = function (str_$0, newLine_$1) {
switch (arguments.length) {
case 1:
newLine_$1 = true
};
this.addHTMLText("<i>" + str_$0 + "</i>" + (newLine_$1 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "addHTMLText", (function () {
var $lzsc$temp = function (msg_$0) {
this.saved_msgs.push(msg_$0)
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "makeObjectLink", (function () {
var $lzsc$temp = function (rep_$0, id_$1, attrs_$2) {
switch (arguments.length) {
case 2:
attrs_$2 = null
};
if (id_$1 != null) {
return '<a title="#' + id_$1 + '">' + rep_$0 + "</a>"
};
return rep_$0
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "doEval", (function () {
var $lzsc$temp = function (expr_$0) {
try {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["doEval"] || this.nextMethod(arguments.callee, "doEval")).call(this, expr_$0)
}
catch (e_$1) {
Debug.error(e_$1)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()], LzDebugConsole);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#196/1";
return $lzsc$temp
})()(LzBootstrapDebugConsole);
Class.make("LzBootstrapDebugLogger", ["log", (function () {
var $lzsc$temp = function (msg_$0) {
if (("console" in global) && typeof console.log == "function") {
var fn_$1 = "log";
try {
if (msg_$0 instanceof LzError) {
fn_$1 = "error"
} else if (msg_$0 instanceof LzWarning) {
fn_$1 = "warn"
} else if (msg_$0 instanceof LzInfo) {
fn_$1 = "info"
} else if (msg_$0 instanceof LzDebug) {
fn_$1 = "debug"
};
if (typeof console[fn_$1] != "function") {
fn_$1 = "log"
};
if (console[fn_$1].length == 0 && (msg_$0 instanceof LzMessage || msg_$0 instanceof LzSourceMessage)) {
console[fn_$1].apply(console, msg_$0.toArray());
return
}}
catch (e_$2) {};
console[fn_$1](msg_$0.toString())
}};
$lzsc$temp["displayName"] = "log";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#261/1";
return $lzsc$temp
})()(LzBootstrapDebugLogger);
Class.make("LzBootstrapDebugService", ["FUNCTION_NAME", "displayName", "FUNCTION_FILENAME", "_dbg_filename", "FUNCTION_LINENO", "_dbg_lineno", "backtraceStack", [], "uncaughtBacktraceStack", null, "log_all_writes", false, "logger", void 0, "console", void 0, "window", void 0, "environment", {}, "$lzsc$initialize", (function () {
var $lzsc$temp = function (logger_$0, console_$1) {
switch (arguments.length) {
case 0:
logger_$0 = null;;case 1:
console_$1 = null
};
if (logger_$0 == null) {
logger_$0 = new LzBootstrapDebugLogger()
};
if (console_$1 == null) {
console_$1 = new LzBootstrapDebugConsole()
};
var copy_$2 = {backtraceStack: true, uncaughtBacktraceStack: true};
for (var k_$3 in copy_$2) {
this[k_$3] = Debug[k_$3]
};
this.log_all_writes = global["console"] && typeof global.console["log"] == "function";
this.logger = logger_$0;
this.console = console_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ensureVisible", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "ensureVisible";
return $lzsc$temp
})(), "log", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.logger.log.apply(this.logger, args_$0)
};
$lzsc$temp["displayName"] = "log";
return $lzsc$temp
})(), "makeObjectLink", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.console.makeObjectLink.apply(this.console, args_$0)
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "displayResult", (function () {
var $lzsc$temp = function (result_$0) {
switch (arguments.length) {
case 0:
result_$0 = void 0
};
if (result_$0 !== void 0) {
this.__write(result_$0)
}};
$lzsc$temp["displayName"] = "displayResult";
return $lzsc$temp
})(), "__write", (function () {
var $lzsc$temp = function (msg_$0) {
if (this.log_all_writes) {
this.logger.log(msg_$0)
};
this.console.addText(msg_$0)
};
$lzsc$temp["displayName"] = "__write";
return $lzsc$temp
})(), "debug", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
this.__write("DEBUG: " + args_$0.join(" "))
};
$lzsc$temp["displayName"] = "debug";
return $lzsc$temp
})(), "info", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
this.__write("INFO: " + args_$0.join(" "))
};
$lzsc$temp["displayName"] = "info";
return $lzsc$temp
})(), "warn", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
this.__write("WARNING: " + args_$0.join(" "))
};
$lzsc$temp["displayName"] = "warn";
return $lzsc$temp
})(), "error", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
this.__write("ERROR: " + args_$0.join(" "))
};
$lzsc$temp["displayName"] = "error";
return $lzsc$temp
})(), "deprecated", (function () {
var $lzsc$temp = function (obj_$0, method_$1, replacement_$2) {
Debug.info("%w.%=s is deprecated.  Use %w.%=s instead", obj_$0, method_$1, Debug.methodName(obj_$0, method_$1), obj_$0, replacement_$2, Debug.methodName(obj_$0, replacement_$2))
};
$lzsc$temp["displayName"] = "deprecated";
return $lzsc$temp
})(), "readOnly", (function () {
var $lzsc$temp = function (obj_$0, attribute_$1, value_$2) {
Debug.warn("%w.%s is read-only.  %w ignored", obj_$0, attribute_$1, value_$2)
};
$lzsc$temp["displayName"] = "readOnly";
return $lzsc$temp
})(), "evalCarefully", (function () {
var $lzsc$temp = function (fileName_$0, lineNumber_$1, closure_$2, context_$3) {
try {
return closure_$2.call(context_$3)
}
catch (e_$4) {
$reportSourceWarning(fileName_$0, lineNumber_$1, e_$4)
}};
$lzsc$temp["displayName"] = "evalCarefully";
return $lzsc$temp
})(), "ignoringErrors", (function () {
var $lzsc$temp = function (closure_$0, context_$1, errval_$2) {
try {
return closure_$0.call(context_$1)
}
catch (e_$3) {
return errval_$2
}};
$lzsc$temp["displayName"] = "ignoringErrors";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzBootstrapDebugService.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
if (this === Debug) {
return "#Debug"
} else return null
};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#541/49";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#331/1";
return $lzsc$temp
})()(LzBootstrapDebugService);
var Debug = new LzBootstrapDebugService();
function trace () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
Debug.info.apply(Debug, args_$0)
};
var $reportSourceWarning = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, msg_$2, fatal_$3) {
var warning_$4 = filename_$0 + "@" + lineNumber_$1 + ": " + msg_$2 + "\n";
Debug[fatal_$3 ? "error" : "warn"](warning_$4)
};
$lzsc$temp["displayName"] = "$reportSourceWarning";
return $lzsc$temp
})();
var $reportedError = null;
var $reportException = (function () {
var $lzsc$temp = function (fileName_$0, lineNumber_$1, e_$2) {
if (e_$2 !== $reportedError) {
$reportedError = e_$2;
$reportSourceWarning(fileName_$0, lineNumber_$1, e_$2, true)
}};
$lzsc$temp["displayName"] = "$reportException";
return $lzsc$temp
})();
var $reportUndefinedObjectProperty = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, propertyName_$2) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$0, lineNumber_$1, "undefined object does not have a property '" + propertyName_$2 + "'", true);
arguments.callee._dbg_recursive_call = false
}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#603/38";
return $lzsc$temp
})();
$reportUndefinedObjectProperty._dbg_recursive_call = false;
var $reportUndefinedProperty = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, propertyName_$2) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$0, lineNumber_$1, "reference to undefined property '" + propertyName_$2 + "'", false);
arguments.callee._dbg_recursive_call = false
}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#614/32";
return $lzsc$temp
})();
$reportUndefinedProperty._dbg_recursive_call = false;
var $reportUndefinedVariable = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, variableName_$2) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
$reportSourceWarning(filename_$0, lineNumber_$1, "reference to undefined variable '" + variableName_$2 + "'", true);
arguments.callee._dbg_recursive_call = false
}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#625/32";
return $lzsc$temp
})();
$reportUndefinedVariable._dbg_recursive_call = false;
var $reportNotFunction = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, name_$2, value_$3) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
var msg_$4 = "call to non-function";
if (typeof name_$2 == "string") msg_$4 += " '" + name_$2 + "'";
msg_$4 += " (type '" + typeof value_$3 + "')";
if (typeof value_$3 == "undefined") {
msg_$4 = "call to undefined function";
if (typeof name_$2 == "string") msg_$4 += " '" + name_$2 + "'"
};
$reportSourceWarning(filename_$0, lineNumber_$1, msg_$4, true);
arguments.callee._dbg_recursive_call = false
}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#636/26";
return $lzsc$temp
})();
$reportNotFunction._dbg_recursive_call = false;
var $reportUndefinedMethod = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, name_$2, value_$3) {
if (!arguments.callee._dbg_recursive_call) {
arguments.callee._dbg_recursive_call = true;
var msg_$4 = "call to non-method";
if (typeof name_$2 == "string") msg_$4 += " '" + name_$2 + "'";
msg_$4 += " (type '" + typeof value_$3 + "')";
if (typeof value_$3 == "undefined") {
msg_$4 = "call to undefined method";
if (typeof name_$2 == "string") msg_$4 += " '" + name_$2 + "'"
};
$reportSourceWarning(filename_$0, lineNumber_$1, msg_$4, true);
arguments.callee._dbg_recursive_call = false
}};
$lzsc$temp["displayName"] = "compiler/LzBootstrapDebugService.lzs#656/30";
return $lzsc$temp
})();
$reportUndefinedMethod._dbg_recursive_call = false;
Class.make("LzBootstrapMessage", ["message", "", "length", 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (message_$0) {
switch (arguments.length) {
case 0:
message_$0 = null
};
if (message_$0 != null) {
this.appendInternal("" + message_$0, message_$0)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "appendInternal", (function () {
var $lzsc$temp = function (str_$0, obj_$1) {
switch (arguments.length) {
case 1:
obj_$1 = null
};
this.message += str_$0;
this.length = this.message.length
};
$lzsc$temp["displayName"] = "appendInternal";
return $lzsc$temp
})(), "append", (function () {
var $lzsc$temp = function () {
var str_$0 = Array.prototype.slice.call(arguments, 0);
var len_$1 = str_$0.length;
for (var i_$2 = 0;i_$2 < len_$1;i_$2++) {
this.appendInternal(String(str_$0[i_$2]))
}};
$lzsc$temp["displayName"] = "append";
return $lzsc$temp
})(), "charAt", (function () {
var $lzsc$temp = function (index_$0) {
return this.message.charAt(index_$0)
};
$lzsc$temp["displayName"] = "charAt";
return $lzsc$temp
})(), "charCodeAt", (function () {
var $lzsc$temp = function (index_$0) {
return this.message.charCodeAt(index_$0)
};
$lzsc$temp["displayName"] = "charCodeAt";
return $lzsc$temp
})(), "indexOf", (function () {
var $lzsc$temp = function (key_$0) {
return this.message.indexOf(key_$0)
};
$lzsc$temp["displayName"] = "indexOf";
return $lzsc$temp
})(), "lastIndexOf", (function () {
var $lzsc$temp = function (key_$0) {
return this.message.lastIndexOf(key_$0)
};
$lzsc$temp["displayName"] = "lastIndexOf";
return $lzsc$temp
})(), "toLowerCase", (function () {
var $lzsc$temp = function () {
return new LzMessage(this.message.toLowerCase())
};
$lzsc$temp["displayName"] = "toLowerCase";
return $lzsc$temp
})(), "toUpperCase", (function () {
var $lzsc$temp = function () {
return new LzMessage(this.message.toUpperCase())
};
$lzsc$temp["displayName"] = "toUpperCase";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.message || ""
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "valueOf", (function () {
var $lzsc$temp = function () {
return this.message || ""
};
$lzsc$temp["displayName"] = "valueOf";
return $lzsc$temp
})(), "concat", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return new LzMessage(this.message.concat.apply(this.message, args_$0))
};
$lzsc$temp["displayName"] = "concat";
return $lzsc$temp
})(), "slice", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.slice.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "slice";
return $lzsc$temp
})(), "split", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.split.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "split";
return $lzsc$temp
})(), "substr", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.substr.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "substr";
return $lzsc$temp
})(), "substring", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.substring.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "substring";
return $lzsc$temp
})(), "toHTML", (function () {
var $lzsc$temp = function () {
return this["toString"]().toHTML()
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], null, ["xmlEscape", (function () {
var $lzsc$temp = function (input_$0) {
if (input_$0 && (typeof input_$0 == "string" || (String["$lzsc$isa"] ? String.$lzsc$isa(input_$0) : input_$0 instanceof String))) {
var len_$1 = input_$0.length;
var output_$2 = "";
for (var i_$3 = 0;i_$3 < len_$1;i_$3++) {
var c_$4 = input_$0.charAt(i_$3);
switch (c_$4) {
case "<":
output_$2 += "&lt;";break;;case "&":
output_$2 += "&amp;";break;;default:
output_$2 += c_$4
}};
return output_$2
} else {
return input_$0
}};
$lzsc$temp["displayName"] = "xmlEscape";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "compiler/LzMessage.lzs#16/1";
return $lzsc$temp
})()(LzBootstrapMessage);
var LzMessage = LzBootstrapMessage;
String.prototype.toHTML = (function () {
var $lzsc$temp = function () {
return LzMessage.xmlEscape(this)
};
$lzsc$temp["displayName"] = "compiler/LzMessage.lzs#177/27";
return $lzsc$temp
})();
Class.make("LzFormatCallback", ["callback", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (callback_$0) {
this.callback = callback_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "call", (function () {
var $lzsc$temp = function () {
return this.callback.call(null)
};
$lzsc$temp["displayName"] = "call";
return $lzsc$temp
})()]);
Mixin.make("LzFormatter", ["toNumber", (function () {
var $lzsc$temp = function (s_$0) {
return Number(s_$0)
};
$lzsc$temp["displayName"] = "toNumber";
return $lzsc$temp
})(), "pad", (function () {
var $lzsc$temp = function (value_$0, widthMin_$1, decMax_$2, pad_$3, sign_$4, radix_$5, force_$6) {
switch (arguments.length) {
case 0:
value_$0 = "";;case 1:
widthMin_$1 = 0;;case 2:
decMax_$2 = null;;case 3:
pad_$3 = " ";;case 4:
sign_$4 = "-";;case 5:
radix_$5 = 10;;case 6:
force_$6 = false
};
var isNumber_$7 = typeof value_$0 == "number";
if (isNumber_$7) {
if (decMax_$2 != null) {
var precision_$8 = Math.pow(10, -decMax_$2);
value_$0 = Math.round(value_$0 / precision_$8) * precision_$8
};
value_$0 = Number(value_$0).toString(radix_$5);
if (sign_$4 != "-") {
if (value_$0.indexOf("-") != 0) {
if (value_$0 != 0) {
value_$0 = sign_$4 + value_$0
} else {
value_$0 = " " + value_$0
}}}} else {
value_$0 = "" + value_$0
};
var strlen_$9 = value_$0.length;
if (decMax_$2 != null) {
if (isNumber_$7) {
var decimal_$a = value_$0.lastIndexOf(".");
if (decimal_$a == -1) {
var decimals_$b = 0;
if (force_$6 || decMax_$2 > 0) {
value_$0 += "."
}} else {
var decimals_$b = strlen_$9 - (decimal_$a + 1)
};
if (decimals_$b > decMax_$2) {
value_$0 = value_$0.substring(0, strlen_$9 - (decimals_$b - decMax_$2))
} else {
for (var i_$c = decimals_$b;i_$c < decMax_$2;i_$c++) value_$0 += "0"
}} else {
value_$0 = value_$0.substring(0, decMax_$2)
}};
strlen_$9 = value_$0.length;
var leftJustify_$d = false;
if (widthMin_$1 < 0) {
widthMin_$1 = -widthMin_$1;
leftJustify_$d = true
};
if (strlen_$9 >= widthMin_$1) {
return value_$0
};
if (leftJustify_$d) {
for (var i_$c = strlen_$9;i_$c < widthMin_$1;i_$c++) value_$0 = value_$0 + " "
} else {
sign_$4 = null;
if (pad_$3 != " ") {
if (" +-".indexOf(value_$0.substring(0, 1)) >= 0) {
sign_$4 = value_$0.substring(0, 1);
value_$0 = value_$0.substring(1)
}};
for (var i_$c = strlen_$9;i_$c < widthMin_$1;i_$c++) value_$0 = pad_$3 + value_$0;
if (sign_$4 != null) {
value_$0 = sign_$4 + value_$0
}};
return value_$0
};
$lzsc$temp["displayName"] = "pad";
return $lzsc$temp
})(), "abbreviate", (function () {
var $lzsc$temp = function (s_$0, l_$1) {
switch (arguments.length) {
case 1:
l_$1 = Infinity
};
if (s_$0) {
var ellipsis_$2 = "\u2026";
if (s_$0.length > l_$1 - ellipsis_$2.length) {
s_$0 = s_$0.substring(0, l_$1 - ellipsis_$2.length) + ellipsis_$2
}};
return s_$0
};
$lzsc$temp["displayName"] = "abbreviate";
return $lzsc$temp
})(), "stringEscape", (function () {
var $lzsc$temp = function (s_$0, quoted_$1) {
switch (arguments.length) {
case 1:
quoted_$1 = false
};
var sec_$2 = LzFormatter.singleEscapeCharacters;
var skip_$3 = '"';
var quote_$4 = "";
var ignore_$5 = "'";
if (quoted_$1) {
ignore_$5 = "";
var singles_$6 = s_$0.split("'").length;
var doubles_$7 = s_$0.split('"').length;
if (singles_$6 > doubles_$7) {
skip_$3 = "'";
quote_$4 = '"'
} else {
skip_$3 = '"';
quote_$4 = "'"
}};
var output_$8 = "";
for (var i_$9 = 0, l_$a = s_$0.length;i_$9 < l_$a;i_$9++) {
var ch_$b = s_$0.charAt(i_$9);
var cc_$c = s_$0.charCodeAt(i_$9);
if (cc_$c in sec_$2) {
if (ch_$b != skip_$3 && ch_$b != ignore_$5) {
output_$8 += sec_$2[cc_$c]
} else {
output_$8 += ch_$b
}} else if (cc_$c >= 0 && cc_$c <= 31 || cc_$c >= 127 && cc_$c <= 159) {
output_$8 += "\\x" + this.pad(cc_$c, 2, 0, "0", "", 16)
} else {
output_$8 += ch_$b
}};
return quote_$4 + output_$8 + quote_$4
};
$lzsc$temp["displayName"] = "stringEscape";
return $lzsc$temp
})(), "formatToString", (function () {
var $lzsc$temp = function (control) {
var getarg_$7;
var consumearg_$8;
getarg_$7 = (function () {
var $lzsc$temp = function (i_$0) {
if (i_$0 >= al) {
return null
};
return args[i_$0]
};
$lzsc$temp["displayName"] = "getarg";
return $lzsc$temp
})();
consumearg_$8 = (function () {
var $lzsc$temp = function (i_$0) {
if (i_$0 >= al) {
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
var out_$0 = new LzMessage();
for (var i_$1 = 0;i_$1 < al;i_$1++) {
var arg_$2 = args[i_$1];
var sep_$3 = i_$1 == al - 1 ? "\n" : " ";
out_$0.append(arg_$2);
out_$0.appendInternal(sep_$3)
};
return out_$0
};
var ctrl_$4 = "" + control;
var argno_$5 = 0;
var maxarg_$6 = 0;
var base_$9 = 0, limit_$a = ctrl_$4.length;
var start_$b = 0, end_$c = 0;
var out_$0 = new LzMessage();
while (start_$b < limit_$a) {
end_$c = ctrl_$4.indexOf("%");
if (end_$c == -1) {
out_$0.append(ctrl_$4.substring(start_$b, limit_$a));
break
};
out_$0.append(ctrl_$4.substring(start_$b, end_$c));
base_$9 = end_$c;
start_$b = end_$c + 1;
end_$c = end_$c + 2;
var sign_$d = "-";
var pad_$e = " ";
var alternate_$f = false;
var length_$g = "";
var precision_$h = null;
var directive_$i = null;
var object_$j = null;
while (start_$b < limit_$a && directive_$i == null) {
var char_$k = ctrl_$4.substring(start_$b, end_$c);
start_$b = end_$c++;
switch (char_$k) {
case "-":
length_$g = char_$k;break;;case "+":
case " ":
sign_$d = char_$k;break;;case "#":
alternate_$f = true;break;;case "0":
if (length_$g === "" && precision_$h === null) {
pad_$e = char_$k;
break
};case "1":
case "2":
case "3":
case "4":
case "5":
case "6":
case "7":
case "8":
case "9":
if (precision_$h !== null) {
precision_$h += char_$k
} else {
length_$g += char_$k
}break;;case "$":
argno_$5 = length_$g - 1;length_$g = "";break;;case "*":
if (precision_$h !== null) {
precision_$h = getarg_$7(argno_$5);
consumearg_$8(argno_$5++)
} else {
length_$g = getarg_$7(argno_$5);
consumearg_$8(argno_$5++)
}break;;case ".":
precision_$h = "";break;;case "h":
case "l":
break;;case "=":
object_$j = getarg_$7(argno_$5);consumearg_$8(argno_$5++);break;;case "^":
object_$j = new LzFormatCallback(getarg_$7(argno_$5));consumearg_$8(argno_$5++);break;;default:
directive_$i = char_$k;break
}};
var value_$l = getarg_$7(argno_$5);
if (object_$j == null) {
object_$j = value_$l
};
var decimals_$m = null;
var force_$n = false;
if (precision_$h !== null) {
decimals_$m = 1 * precision_$h
} else {
switch (directive_$i) {
case "F":
case "E":
case "G":
case "f":
case "e":
case "g":
decimals_$m = 6;force_$n = alternate_$f;break;;case "O":
case "o":
if (alternate_$f && value_$l != 0) {
out_$0.append("0")
}break;;case "X":
case "x":
if (alternate_$f && value_$l != 0) {
out_$0.append("0" + directive_$i)
}break
}};
var radix_$o = 10;
switch (directive_$i) {
case "o":
case "O":
radix_$o = 8;break;;case "x":
case "X":
radix_$o = 16;break
};
switch (directive_$i) {
case "U":
case "O":
case "X":
case "u":
case "o":
case "x":
if (value_$l < 0) {
value_$l = -value_$l;
var wid_$p = Math.abs(parseInt(length_$g, 10));
if (isNaN(wid_$p)) {
wid_$p = this.toNumber(value_$l).toString(radix_$o).length
};
var max_$q = Math.pow(radix_$o, wid_$p);
value_$l = max_$q - value_$l
}break
};
switch (directive_$i) {
case "D":
case "U":
case "I":
case "O":
case "X":
case "F":
case "E":
case "G":
value_$l = this.toNumber(value_$l);out_$0.appendInternal(this.pad(value_$l, length_$g, decimals_$m, pad_$e, sign_$d, radix_$o, force_$n).toUpperCase(), object_$j);consumearg_$8(argno_$5++);break;;case "c":
value_$l = String.fromCharCode(value_$l);;case "w":
{
var width_$r = decimals_$m || Debug.printLength;
out_$0.appendInternal(this.pad(Debug.__String(value_$l, true, width_$r, alternate_$f), length_$g, null, pad_$e, sign_$d, radix_$o, force_$n), object_$j);
consumearg_$8(argno_$5++);
break
};case "s":
var str_$s;if (Function["$lzsc$isa"] ? Function.$lzsc$isa(value_$l) : value_$l instanceof Function) {
str_$s = Debug.functionName(value_$l, false);
if (!str_$s) {
str_$s = "function () {\u2026}"
}} else if (typeof value_$l == "number") {
str_$s = Number(value_$l).toString(radix_$o)
} else if (directive_$i == "w" && typeof value_$l == "string") {
str_$s = this.stringEscape(value_$l, true)
} else {
str_$s = "" + value_$l
}if (alternate_$f) {
var width_$r = decimals_$m || Debug.printLength;
if (width_$r) {
str_$s = this.abbreviate(str_$s, width_$r);
decimals_$m = null
}}out_$0.appendInternal(this.pad(str_$s, length_$g, decimals_$m, pad_$e, sign_$d, radix_$o, force_$n), object_$j);consumearg_$8(argno_$5++);break;;case "d":
case "u":
case "i":
case "o":
case "x":
case "f":
case "e":
case "g":
value_$l = this.toNumber(value_$l);out_$0.appendInternal(this.pad(value_$l, length_$g, decimals_$m, pad_$e, sign_$d, radix_$o, force_$n), object_$j);consumearg_$8(argno_$5++);break;;case "%":
out_$0.append("%");break;;default:
out_$0.append(ctrl_$4.substring(base_$9, start_$b));break
};
ctrl_$4 = ctrl_$4.substring(start_$b, limit_$a);
base_$9 = 0, limit_$a = ctrl_$4.length;
start_$b = 0, end_$c = 0;
if (argno_$5 > maxarg_$6) {
maxarg_$6 = argno_$5
}};
if (maxarg_$6 < al) {
Debug.warn("%#0.48w: excess arguments", control);
out_$0.appendInternal(" ");
for (;maxarg_$6 < al;maxarg_$6++) {
var arg_$2 = getarg_$7(maxarg_$6);
var sep_$3 = maxarg_$6 == al - 1 ? "\n" : " ";
out_$0.append(arg_$2);
out_$0.appendInternal(sep_$3)
}};
return out_$0
};
$lzsc$temp["displayName"] = "formatToString";
return $lzsc$temp
})()], null, ["singleEscapeCharacters", (function () {
var $lzsc$temp = function (np_$0) {
var result_$1 = [];
for (var i_$2 = 0, l_$3 = np_$0.length;i_$2 < l_$3;i_$2 += 2) {
var rep_$4 = np_$0[i_$2];
var ch_$5 = np_$0[i_$2 + 1];
result_$1[ch_$5.charCodeAt(0)] = rep_$4
};
return result_$1
};
$lzsc$temp["displayName"] = "compiler/LzFormatter.lzs#269/40";
return $lzsc$temp
})()(["\\b", "\b", "\\t", "\t", "\\n", "\n", "\\v", String.fromCharCode(11), "\\f", "\f", "\\r", "\r", '\\"', '"', "\\'", "'", "\\\\", "\\"])]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "compiler/LzFormatter.lzs#118/1";
return $lzsc$temp
})()(LzFormatter);
Class.make("LzDebugMessage", ["objects", [], "$lzsc$initialize", (function () {
var $lzsc$temp = function (message_$0) {
switch (arguments.length) {
case 0:
message_$0 = null
};
this.objects = [];
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, message_$0)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toLowerCase", (function () {
var $lzsc$temp = function () {
var msg_$0 = new LzMessage(this.message.toLowerCase());
msg_$0.objects = this.objects.concat();
return msg_$0
};
$lzsc$temp["displayName"] = "toLowerCase";
return $lzsc$temp
})(), "toUpperCase", (function () {
var $lzsc$temp = function () {
var msg_$0 = new LzMessage(this.message.toUpperCase());
msg_$0.objects = this.objects.concat();
return msg_$0
};
$lzsc$temp["displayName"] = "toUpperCase";
return $lzsc$temp
})(), "concat", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
var msg_$1 = new LzMessage(this.message.concat.apply(this.message, args_$0));
var offset_$2 = this.message.length;
for (var i_$3 = 0;i_$3 < args_$0.length;i_$3++) {
var arg_$4 = args_$0[i_$3];
if (LzDebugMessage["$lzsc$isa"] ? LzDebugMessage.$lzsc$isa(arg_$4) : arg_$4 instanceof LzDebugMessage) {
var ao_$5 = arg_$4.objects;
for (var j_$6 = 0;j_$6 < ao_$5.length;j_$6++) {
var od_$7 = ao_$5[j_$6];
msg_$1.objects.push({id: od_$7.id, start: od_$7.start + offset_$2, end: od_$7.end + offset_$2})
}};
offset_$2 += String(arg_$4).length
};
return msg_$1
};
$lzsc$temp["displayName"] = "concat";
return $lzsc$temp
})(), "slice", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.slice.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "slice";
return $lzsc$temp
})(), "split", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.split.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "split";
return $lzsc$temp
})(), "substr", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.substr.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "substr";
return $lzsc$temp
})(), "substring", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.message.substring.apply(this.message, args_$0)
};
$lzsc$temp["displayName"] = "substring";
return $lzsc$temp
})(), "appendInternal", (function () {
var $lzsc$temp = function (str_$0, obj_$1) {
switch (arguments.length) {
case 1:
obj_$1 = null
};
if (obj_$1 != null) {
var id_$2 = Debug.IDForObject(obj_$1)
} else {
var id_$2 = null
};
if (id_$2 == null) {
this.message += str_$0
} else if (LzDebugMessage["$lzsc$isa"] ? LzDebugMessage.$lzsc$isa(obj_$1) : obj_$1 instanceof LzDebugMessage) {
var arg_$3 = obj_$1;
var offset_$4 = this.message.length;
this.message += arg_$3.message;
var ao_$5 = arg_$3.objects;
for (var j_$6 = 0;j_$6 < ao_$5.length;j_$6++) {
var od_$7 = ao_$5[j_$6];
this.objects.push({id: od_$7.id, start: od_$7.start + offset_$4, end: od_$7.end + offset_$4})
}} else {
var start_$8 = this.message.length;
this.message += str_$0;
var end_$9 = this.message.length;
this.objects.push({id: id_$2, start: start_$8, end: end_$9})
};
this.length = this.message.length
};
$lzsc$temp["displayName"] = "appendInternal";
return $lzsc$temp
})(), "append", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
var len_$1 = args_$0.length;
for (var i_$2 = 0;i_$2 < len_$1;i_$2++) {
var arg_$3 = args_$0[i_$2];
if (!((String["$lzsc$isa"] ? String.$lzsc$isa(arg_$3) : arg_$3 instanceof String) && arg_$3["constructor"] === String) && ((Object["$lzsc$isa"] ? Object.$lzsc$isa(arg_$3) : arg_$3 instanceof Object) || Debug.isObjectLike(arg_$3) || Debug.IDForObject(arg_$3) != null)) {
var str_$4 = Debug.__String(arg_$3, true, Infinity, true);
this.appendInternal(str_$4, arg_$3)
} else {
this.appendInternal(String(arg_$3))
}}};
$lzsc$temp["displayName"] = "append";
return $lzsc$temp
})(), "toArray", (function () {
var $lzsc$temp = function (linkMaker_$0) {
switch (arguments.length) {
case 0:
linkMaker_$0 = null
};
if (linkMaker_$0 == null) {
linkMaker_$0 = (function () {
var $lzsc$temp = function (rep_$0, id_$1) {
return Debug.ObjectForID(id_$1)
};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#174/19";
return $lzsc$temp
})()
};
var msg_$1 = this.message;
var base_$2 = 0;
var limit_$3 = msg_$1.length;
var start_$4 = 0;
var end_$5 = 0;
var objs_$6 = this.objects;
var id_$7;
var array_$8 = [];
var len_$9 = objs_$6.length;
for (var i_$a = 0;i_$a < len_$9;i_$a++) {
var annot_$b = objs_$6[i_$a];
start_$4 = annot_$b.start;
end_$5 = annot_$b.end;
id_$7 = annot_$b.id;
array_$8.push(msg_$1.substring(base_$2, start_$4).toHTML());
array_$8.push(linkMaker_$0(msg_$1.substring(start_$4, end_$5).toHTML(), id_$7));
base_$2 = end_$5
};
array_$8.push(msg_$1.substring(base_$2, limit_$3).toHTML());
return array_$8
};
$lzsc$temp["displayName"] = "toArray";
return $lzsc$temp
})(), "toHTML", (function () {
var $lzsc$temp = function () {
return( this.toArray((function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return Debug.makeObjectLink.apply(Debug, args_$0)
};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#204/25";
return $lzsc$temp
})()).join(""))
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], LzBootstrapMessage, ["xmlEscape", LzBootstrapMessage.xmlEscape]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#29/1";
return $lzsc$temp
})()(LzDebugMessage);
var LzMessage = LzDebugMessage;
Class.make("LzSourceMessage", ["file", void 0, "__filePrefix", "@", "line", void 0, "__linePrefix", "\u2248", "message", void 0, "length", void 0, "backtrace", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
var bts_$4 = Debug.backtrace();
if (bts_$4 != null) {
var btsl_$5 = bts_$4.length;
var limit_$6 = btsl_$5;
for (var i_$7 = btsl_$5 - 1;i_$7 > 0;i_$7--) {
var callee_$8 = bts_$4[i_$7].callee;
if (callee_$8 === $reportException) {
limit_$6 = i_$7;
break
} else if (callee_$8 === $reportSourceWarning) {
limit_$6 = i_$7
} else if (callee_$8 === Debug.warnInternal) {
limit_$6 = i_$7 - 1
}};
if (btsl_$5 >= limit_$6) {
for (var i_$7 = btsl_$5 - 1;i_$7 >= limit_$6;i_$7--) {
var frame_$9 = bts_$4[i_$7];
delete bts_$4[i_$7];
bts_$4["__" + i_$7] = frame_$9
};
bts_$4.length = limit_$6;
this.backtrace = bts_$4;
var top_$a = this.backtrace.userStackFrame();
if (top_$a) {
if (file_$0 == null) {
file_$0 = top_$a.filename();
line_$1 = top_$a.lineno();
this.__linePrefix = "#"
} else if (file_$0 == top_$a.filename() && line_$1 == top_$a.lineno()) {
this.__linePrefix = "#"
}}}};
if (file_$0 == null && node_$3 != null) {
file_$0 = node_$3[Debug.FUNCTION_FILENAME];
this.__filePrefix = "%";
if (node_$3[Debug.FUNCTION_LINENO]) {
line_$1 = node_$3[Debug.FUNCTION_LINENO];
this.__linePrefix = "#"
}};
this.file = file_$0;
this.line = line_$1;
if (LzMessage["$lzsc$isa"] ? LzMessage.$lzsc$isa(message_$2) : message_$2 instanceof LzMessage) {
this.message = message_$2
} else {
this.message = new LzMessage(message_$2)
};
this.length = this.toString().length
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "locationString", (function () {
var $lzsc$temp = function (prefix_$0) {
switch (arguments.length) {
case 0:
prefix_$0 = ""
};
var str_$1 = prefix_$0;
if (this.file) {
if (str_$1.length) {
str_$1 += " "
};
str_$1 += this.__filePrefix;
str_$1 += this.file;
if (this.line) {
str_$1 += this.__linePrefix;
str_$1 += this.line
}};
if (str_$1.length) {
str_$1 += ": "
};
return str_$1
};
$lzsc$temp["displayName"] = "locationString";
return $lzsc$temp
})(), "toArray", (function () {
var $lzsc$temp = function () {
var array_$0 = [this.locationString(this["constructor"].type)];
if (LzMessage["$lzsc$isa"] ? LzMessage.$lzsc$isa(this.message) : this.message instanceof LzMessage) {
return array_$0.concat(this.message.toArray())
};
return array_$0.concat("" + this.message)
};
$lzsc$temp["displayName"] = "toArray";
return $lzsc$temp
})(), "toStringInternal", (function () {
var $lzsc$temp = function (conversion_$0) {
return this.locationString(this["constructor"].type) + this.message[conversion_$0]()
};
$lzsc$temp["displayName"] = "toStringInternal";
return $lzsc$temp
})(), "_dbg_name", (function () {
var $lzsc$temp = function () {
return this.locationString("") + this.message
};
$lzsc$temp["displayName"] = "debugger/LzMessage.lzs#413/19";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.toStringInternal("toString") + "\n"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "toHTML", (function () {
var $lzsc$temp = function () {
var id_$0 = Debug.IDForObject(this);
return Debug.makeObjectLink(this.toStringInternal("toHTML"), id_$0, this["constructor"]) + "\n"
};
$lzsc$temp["displayName"] = "toHTML";
return $lzsc$temp
})()], null, ["type", "", "color", "#000000", "level", 0, "levelMax", 5, "format", (function () {
var $lzsc$temp = function (file_$0, line_$1, control_$2) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
control_$2 = ""
};
var args_$3 = Array.prototype.slice.call(arguments, 3);
var debug_$4 = Debug;
var message_$5 = debug_$4.formatToString.apply(debug_$4, [control_$2].concat(args_$3));
var node_$6 = null;
if (file_$0 == null) {
for (var i_$7 = 0;i_$7 < args_$3.length;i_$7++) {
var arg_$8 = args_$3[i_$7];
if ((LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(arg_$8) : arg_$8 instanceof LzNode) && arg_$8[Debug.FUNCTION_FILENAME]) {
node_$6 = arg_$8;
break
}}};
return new this(file_$0, line_$1, message_$5, node_$6)
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})()]);
Class.make("LzWarning", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "WARNING", "color", "#ff9900", "format", LzSourceMessage.format]);
Class.make("LzError", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3);
Debug.lastError = this
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "ERROR", "color", "#ff0000", "format", LzSourceMessage.format]);
Class.make("LzInfo", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "INFO", "color", "#0066cc", "format", LzSourceMessage.format]);
Class.make("LzDebug", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "DEBUG", "color", "#00cc00", "format", LzSourceMessage.format]);
global._dbg_name = "global";
Class.make("LzDebugService", ["base", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (base_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, base_$0 != null ? base_$0.logger : null, base_$0 != null ? base_$0.console : null);
if (base_$0 != null) {
this.base = base_$0;
var copy_$1 = {backtraceStack: true, uncaughtBacktraceStack: true};
for (var k_$2 in copy_$1) {
this[k_$2] = base_$0[k_$2]
}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "printLength", 1024, "printDepth", 8, "inspectPrintLength", 128, "inspectPrintDepth", 1, "printDetectCircular", null, "messageLevel", "ALL", "showInternalProperties", false, "atFreshLine", true, "atPrompt", false, "canvasConsoleWindow", null, "attachDebugConsole", (function () {
var $lzsc$temp = function (console_$0) {
var old_$1 = this.console;
this.console = console_$0;
this.canvasConsoleWindow = this.console.canvasConsoleWindow();
var sm_$2 = old_$1.saved_msgs;
var sml_$3 = sm_$2.length;
for (var i_$4 = 0;i_$4 < sml_$3;i_$4++) {
this.console.addText(sm_$2[i_$4])
};
return this
};
$lzsc$temp["displayName"] = "attachDebugConsole";
return $lzsc$temp
})(), "ensureVisible", (function () {
var $lzsc$temp = function () {
this.console.ensureVisible()
};
$lzsc$temp["displayName"] = "ensureVisible";
return $lzsc$temp
})(), "freshLine", (function () {
var $lzsc$temp = function () {
if (!this.atFreshLine) {
this.console.addText("\n");
this.atFreshLine = true
};
this.atPrompt = false
};
$lzsc$temp["displayName"] = "freshLine";
return $lzsc$temp
})(), "freshPrompt", (function () {
var $lzsc$temp = function () {
if (!this.atPrompt) {
this.freshLine();
this.console.echo("lzx&gt; ", false);
this.atPrompt = true
}};
$lzsc$temp["displayName"] = "freshPrompt";
return $lzsc$temp
})(), "echo", (function () {
var $lzsc$temp = function (str_$0, newLine_$1) {
switch (arguments.length) {
case 1:
newLine_$1 = true
};
this.console.echo(str_$0, newLine_$1);
this.atPrompt = false;
this.atFreshLine = newLine_$1
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "doEval", (function () {
var $lzsc$temp = function (expr_$0) {
this.freshPrompt();
this.echo(String(expr_$0)["toHTML"]());
try {
this.console.doEval(expr_$0)
}
catch (e_$1) {
this.error(e_$1)
}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {
this.console.clear()
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "displayObj", (function () {
var $lzsc$temp = function (id_$0) {
var obj_$1 = this.ObjectForID(id_$0);
if (LzFormatCallback["$lzsc$isa"] ? LzFormatCallback.$lzsc$isa(obj_$1) : obj_$1 instanceof LzFormatCallback) {
return obj_$1.call()
};
this.freshPrompt();
this.echo(this.formatToString("Debug.inspect(%0.48w)", obj_$1)["toHTML"]());
this.displayResult(this.inspect(obj_$1))
};
$lzsc$temp["displayName"] = "displayObj";
return $lzsc$temp
})(), "updateOutputState", (function () {
var $lzsc$temp = function (msg_$0) {
var str_$1 = String(msg_$0);
this.atFreshLine = str_$1.charAt(str_$1.length - 1) == "\n";
if (str_$1.length) {
this.atPrompt = false;
this.console.ensureVisible()
}};
$lzsc$temp["displayName"] = "updateOutputState";
return $lzsc$temp
})(), "displayResult", (function () {
var $lzsc$temp = function (result_$0) {
switch (arguments.length) {
case 0:
result_$0 = void 0
};
var e_$1 = this.environment;
if (result_$0 !== void 0) {
if (result_$0 !== e_$1["_"]) {
if (e_$1["__"] !== void 0) {
e_$1.___ = e_$1.__
};
if (e_$1["_"] !== void 0) {
e_$1.__ = e_$1._
};
e_$1._ = result_$0
}};
this.freshLine();
if (result_$0 !== void 0) {
this.format("%#w", result_$0)
};
this.freshPrompt()
};
$lzsc$temp["displayName"] = "displayResult";
return $lzsc$temp
})(), "__write", (function () {
var $lzsc$temp = function (msg_$0) {
if (this.log_all_writes || !(!LzBrowserKernel.getInitArg("logdebug"))) {
this.logger.log(msg_$0)
};
this.console.addText(msg_$0);
this.updateOutputState(msg_$0)
};
$lzsc$temp["displayName"] = "__write";
return $lzsc$temp
})(), "write", (function () {
var $lzsc$temp = function () {
var any_$0 = Array.prototype.slice.call(arguments, 0);
var msg_$1;
msg_$1 = this.formatToString.apply(this, any_$0);
this.freshLine();
this.__write(msg_$1)
};
$lzsc$temp["displayName"] = "write";
return $lzsc$temp
})(), "format", (function () {
var $lzsc$temp = function (control_$0) {
var args_$1 = Array.prototype.slice.call(arguments, 1);
this.__write(this.formatToString.apply(this, [control_$0].concat(args_$1)))
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})(), "warnInternal", (function () {
var $lzsc$temp = function (xtor_$0, control_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 2);
var sourceMessage_$3 = LzSourceMessage;
var level_$4 = sourceMessage_$3.level;
if (level_$4 > sourceMessage_$3.levelMax) {
return
};
try {
sourceMessage_$3.level = level_$4 + 1;
var msg_$5 = xtor_$0["format"].apply(xtor_$0, [null, null, control_$1].concat(args_$2));
var mls_$6 = LzDebugService.messageLevels;
var t_$7 = xtor_$0["type"];
if ((t_$7 in mls_$6) ? mls_$6[t_$7] >= mls_$6[this.messageLevel] : true) {
this.freshLine();
this.__write(msg_$5)
}}
finally {
sourceMessage_$3.level = level_$4
};
return msg_$5
};
$lzsc$temp["displayName"] = "warnInternal";
return $lzsc$temp
})(), "warn", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzWarning].concat(args_$0))
};
$lzsc$temp["displayName"] = "warn";
return $lzsc$temp
})(), "error", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzError].concat(args_$0))
};
$lzsc$temp["displayName"] = "error";
return $lzsc$temp
})(), "info", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzInfo].concat(args_$0))
};
$lzsc$temp["displayName"] = "info";
return $lzsc$temp
})(), "debug", (function () {
var $lzsc$temp = function () {
var args_$0 = Array.prototype.slice.call(arguments, 0);
return this.warnInternal.apply(this, [LzDebug].concat(args_$0))
};
$lzsc$temp["displayName"] = "debug";
return $lzsc$temp
})(), "inspect", (function () {
var $lzsc$temp = function (obj_$0) {
var msg_$1 = this.inspectInternal(obj_$0);
this.freshLine();
this.console.addHTMLText(msg_$1);
this.updateOutputState(msg_$1);
return obj_$0
};
$lzsc$temp["displayName"] = "inspect";
return $lzsc$temp
})(), "explainStyleBindings", (function () {
var $lzsc$temp = function (node_$0, showInherited_$1) {
switch (arguments.length) {
case 1:
showInherited_$1 = false
};
var style_$2 = LzCSSStyle;
var pc_$3 = style_$2.getPropertyCache(node_$0);
var ppc_$4 = style_$2.getPropertyCache(node_$0.immediateparent);
var rc_$5 = style_$2.getRulesCache(node_$0);
var ps_$6 = [];
var sp_$7 = node_$0.__LZCSSProp;
if (sp_$7) {
for (var a_$8 in sp_$7) {
var p_$9 = sp_$7[a_$8];
if (showInherited_$1 || pc_$3[p_$9] !== ppc_$4[p_$9]) {
ps_$6.push(p_$9)
}}};
ps_$6.sort(this.caseInsensitiveOrdering);
var rs_$a = [];
for (var j_$b = 0, m_$c = rc_$5.length;j_$b < m_$c;j_$b++) {
var r_$d = rc_$5[j_$b];
var rp_$e = r_$d.properties;
for (var i_$f = 0, l_$g = ps_$6.length;i_$f < l_$g;i_$f++) {
var p_$9 = ps_$6[i_$f];
if (p_$9 in rp_$e) {
rs_$a.push(r_$d);
break
}}};
var msg_$h = "";
var vs_$i = [];
for (var j_$b = 0, m_$c = rs_$a.length;j_$b < m_$c;j_$b++) {
var r_$d = rs_$a[j_$b];
var rp_$e = r_$d.properties;
msg_$h += this.formatToString("/* @%s#%d (specificity %d, order %d) */\n", r_$d[Debug.FUNCTION_FILENAME], r_$d[Debug.FUNCTION_LINENO], r_$d.specificity, r_$d._lexorder).toHTML();
msg_$h += this.formatToString("%w {\n", r_$d).toHTML();
for (var i_$f = 0, l_$g = ps_$6.length;i_$f < l_$g;i_$f++) {
var p_$9 = ps_$6[i_$f];
if (p_$9 in rp_$e) {
var x_$j = vs_$i[i_$f];
msg_$h += this.formatToString("  %s<span style='color:#931391'>%s</span>: ", x_$j ? "<i style='text-decoration: line-through'>" : "", p_$9);
msg_$h += this.formatToString("%w", rp_$e[p_$9]).toHTML();
msg_$h += this.formatToString("%s;\n", x_$j ? "</i>" : "");
vs_$i[i_$f] = true
}};
msg_$h += this.formatToString("}\n").toHTML()
};
this.freshLine();
this.console.addHTMLText(msg_$h);
this.updateOutputState(msg_$h);
return node_$0
};
$lzsc$temp["displayName"] = "explainStyleBindings";
return $lzsc$temp
})(), "objseq", 0, "id_to_object_table", [], "IDForObject", (function () {
var $lzsc$temp = function (obj_$0, force_$1) {
switch (arguments.length) {
case 1:
force_$1 = false
};
var id_$2;
var ot_$3 = this.id_to_object_table;
for (id_$2 = ot_$3.length - 1;id_$2 >= 0;id_$2--) {
if (ot_$3[id_$2] === obj_$0) {
return id_$2
}};
if (!force_$1) {
if (!this.isObjectLike(obj_$0)) {
return null
}};
id_$2 = this.objseq++;
this.id_to_object_table[id_$2] = obj_$0;
return id_$2
};
$lzsc$temp["displayName"] = "IDForObject";
return $lzsc$temp
})(), "ObjectForID", (function () {
var $lzsc$temp = function (id_$0) {
return this.id_to_object_table[id_$0]
};
$lzsc$temp["displayName"] = "ObjectForID";
return $lzsc$temp
})(), "__typeof", (function () {
var $lzsc$temp = function (thing_$0) {
try {
var n_$1 = typeof thing_$0;
if (this.isObjectLike(thing_$0)) {
var oc_$2 = (Object["$lzsc$isa"] ? Object.$lzsc$isa(thing_$0) : thing_$0 instanceof Object) && thing_$0["constructor"];
var user_name_$3 = null;
if ("_dbg_typename" in thing_$0) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(thing_$0._dbg_typename) : thing_$0._dbg_typename instanceof Function) {
try {
user_name_$3 = thing_$0._dbg_typename()
}
catch (e_$4) {}} else if (typeof thing_$0._dbg_typename == "string") {
user_name_$3 = thing_$0._dbg_typename
}};
if (this.isStringLike(user_name_$3)) {
n_$1 = user_name_$3
} else if (oc_$2) {
var ocn_$5 = this.functionName(oc_$2, true);
if (ocn_$5) {
n_$1 = ocn_$5
} else {
ocn_$5 = this.functionName(oc_$2, false);
if (!ocn_$5) {
var ts_$6 = thing_$0.toString();
var s_$7 = "[object ".length;
var e_$4 = ts_$6.indexOf("]");
if (ts_$6.indexOf("[object ") == 0 && e_$4 == ts_$6.length - 1) {
ocn_$5 = ts_$6.substring(s_$7, e_$4)
} else if (ts_$6.indexOf("[") == 0 && ts_$6.indexOf("]") == ts_$6.length - 1) {
ocn_$5 = ts_$6.substring(1, ts_$6.length - 1)
}};
if (ocn_$5) {
if (oc_$2 !== globalValue(ocn_$5)) {
var id_$8 = this.IDForObject(oc_$2, true);
ocn_$5 += "#" + id_$8
};
n_$1 = ocn_$5
}};
if (oc_$2 && !(oc_$2["$lzsc$isa"] ? oc_$2.$lzsc$isa(thing_$0) : thing_$0 instanceof oc_$2)) {
if (thing_$0 === oc_$2["prototype"]) {

} else {
n_$1 = "\xBF" + n_$1 + "?"
}} else if (oc_$2["prototype"] && (Function["$lzsc$isa"] ? Function.$lzsc$isa(oc_$2.prototype["isPrototypeOf"]) : oc_$2.prototype["isPrototypeOf"] instanceof Function) && !oc_$2.prototype.isPrototypeOf(thing_$0)) {
if (thing_$0 === oc_$2.prototype) {

} else {
n_$1 = "\xA1" + n_$1 + "!"
}}}};
try {
if (this.isArrayLike(thing_$0)) {
n_$1 += "(" + thing_$0.length + ")"
}}
catch (e_$4) {}}
catch (e_$4) {
try {
n_$1 = this.formatToString("Error: %0.24#s computing __typeof", e_$4)
}
catch (e_$4) {
n_$1 = "Recursive error computing __typeof"
}};
return n_$1
};
$lzsc$temp["displayName"] = "__typeof";
return $lzsc$temp
})(), "functionName", (function () {
var $lzsc$temp = function (fn_$0, mustBeUnique_$1) {
switch (arguments.length) {
case 1:
mustBeUnique_$1 = false
};
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(fn_$0) : fn_$0 instanceof Function) {
var dn_$2 = Debug.FUNCTION_NAME;
if (fn_$0.hasOwnProperty(dn_$2)) {
var n_$3 = fn_$0[dn_$2]
} else {
var fstring_$4 = fn_$0["toString"]();
var s_$5 = "function ".length;
var e_$6 = fstring_$4.indexOf("(");
if (fstring_$4.indexOf("function ") == 0 && e_$6 > s_$5) {
while (fstring_$4.charAt(s_$5) == " ") {
s_$5++
};
while (fstring_$4.charAt(e_$6 - 1) == " ") {
e_$6--
};
n_$3 = fstring_$4.substring(s_$5, e_$6)
}};
if (n_$3) {
if (!mustBeUnique_$1 || fn_$0 === globalValue(n_$3)) {
return n_$3
}}};
return null
};
$lzsc$temp["displayName"] = "functionName";
return $lzsc$temp
})(), "methodName", (function () {
var $lzsc$temp = function (o_$0, f_$1) {
return this.functionName(f_$1)
};
$lzsc$temp["displayName"] = "methodName";
return $lzsc$temp
})(), "__StringDescription", (function () {
var $lzsc$temp = function (thing, escape_$0, limit_$1, readable_$2, depth_$3) {
try {
if (thing === void 0) {
return {readable: true, description: "(void 0)"}};
if (thing === null) {
return {readable: true, description: "null"}};
var t_$4 = typeof thing;
var isreadable_$5 = false;
var debug_name_$6 = null;
var s_$7 = "";
if (this.isObjectLike(thing)) {
var opl_$8 = this.printLength;
try {
this.printLength = limit_$1 < this.inspectPrintLength ? limit_$1 : this.inspectPrintLength;
if ((Function["$lzsc$isa"] ? Function.$lzsc$isa(thing["hasOwnProperty"]) : thing["hasOwnProperty"] instanceof Function) && thing.hasOwnProperty("_dbg_prototype_for")) {
debug_name_$6 = this.functionName(thing._dbg_prototype_for) + ".prototype"
} else {
var dn_$9 = ("_dbg_name" in thing) ? thing._dbg_name : null;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(dn_$9) : dn_$9 instanceof Function) {
try {
debug_name_$6 = thing._dbg_name()
}
catch (e_$a) {}} else if (this.isStringLike(dn_$9)) {
debug_name_$6 = dn_$9
}}}
finally {
this.printLength = opl_$8
}};
if (this.isStringLike(debug_name_$6)) {
if (debug_name_$6.charAt(0) == "#") {
isreadable_$5 = true
};
s_$7 = this.stringEscape(debug_name_$6)
} else if (t_$4 == "null" || t_$4 == "number" || t_$4 == "boolean") {
isreadable_$5 = true;
s_$7 = String(thing)
} else if (this.isStringLike(thing)) {
s_$7 = this.abbreviate(thing, limit_$1);
isreadable_$5 = escape_$0 && t_$4 == "string" && s_$7 === thing;
if (escape_$0) {
s_$7 = this.stringEscape(s_$7, true)
}} else if (t_$4 == "function" || (Function["$lzsc$isa"] ? Function.$lzsc$isa(thing) : thing instanceof Function)) {
var n_$b = this.functionName(thing, true);
if (n_$b != null) {
isreadable_$5 = true;
s_$7 = n_$b
} else {
s_$7 = this.functionName(thing, false);
if (s_$7 == null) {
s_$7 = ""
}}} else if (this.isObjectLike(thing)) {
if (thing["constructor"] && (thing.constructor === Date || thing.constructor === Boolean || thing.constructor === Number)) {
s_$7 = thing.toString();
if (s_$7 == null) {
s_$7 = ""
}} else if (String["$lzsc$isa"] ? String.$lzsc$isa(thing) : thing instanceof String) {

} else if ((Function["$lzsc$isa"] ? Function.$lzsc$isa(thing["toString"]) : thing["toString"] instanceof Function) && thing.toString !== {}.toString && thing.toString !== [].toString && (s_$7 = (function () {
var $lzsc$temp = function () {
try {
var u_$0 = thing.toString();
if (typeof u_$0 != "undefined" && u_$0 != "undefined") {
return u_$0
}}
catch (e_$1) {
return ""
}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1071/24";
return $lzsc$temp
})()())) {

} else {
var names_$c = [];
var indices_$d = this.isArrayLike(thing) ? [] : null;
this.objectOwnProperties(thing, names_$c, indices_$d, limit_$1);
if (indices_$d) {
indices_$d.sort(this.numericOrdering)
};
if (indices_$d) {
var next_$e = 0;
for (var i_$f = 0;i_$f < indices_$d.length && s_$7.length < limit_$1;i_$f++) {
var key_$g = indices_$d[i_$f];
if (key_$g != next_$e) {
s_$7 += "\u2026, "
};
s_$7 += this.__String(thing[key_$g], true, limit_$1 - 5, false, depth_$3 - 1) + ", ";
next_$e = key_$g + 1
};
if (s_$7 != "" && i_$f == indices_$d.length) {
s_$7 = s_$7.substring(0, s_$7.length - 2)
};
s_$7 = "[" + s_$7 + "]"
} else {
var ellip_$h = true;
names_$c.sort(this.caseInsensitiveOrdering);
for (var i_$f = 0;i_$f < names_$c.length && s_$7.length < limit_$1;i_$f++) {
var e_$a = names_$c[i_$f];
var v_$i = thing[e_$a];
var tv_$j = typeof v_$i;
var dtv_$k = this.__typeof(v_$i);
if (tv_$j != "undefined" && tv_$j != "function" && "" + v_$i != "" && !this.internalProperty(e_$a) && !this.internalProperty(dtv_$k)) {
ellip_$h = true;
s_$7 += "" + e_$a + ": " + this.__String(v_$i, true, limit_$1 - 5, false, depth_$3 - 1) + ", "
} else {
if (ellip_$h) {
s_$7 += "\u2026, ";
ellip_$h = false
}}};
if (s_$7 != "" && i_$f == names_$c.length) {
s_$7 = s_$7.substring(0, s_$7.length - 2)
};
s_$7 = "{" + s_$7 + "}"
}}} else {
s_$7 = String(thing)
}}
catch (e_$a) {
try {
s_$7 = this.formatToString("Error: %0.24#s computing __String", e_$a)
}
catch (e_$a) {
s_$7 = "Recursive error computing __String"
}};
return {readable: isreadable_$5, description: s_$7}};
$lzsc$temp["displayName"] = "__StringDescription";
return $lzsc$temp
})(), "__String", (function () {
var $lzsc$temp = function (thing_$0, escape_$1, limit_$2, readable_$3, depth_$4) {
switch (arguments.length) {
case 1:
escape_$1 = true;;case 2:
limit_$2 = void 0;;case 3:
readable_$3 = void 0;;case 4:
depth_$4 = void 0
};
var origPrintLength_$5 = this.printLength;
var origPrintDepth_$6 = this.printDepth;
var origPrintDetectCircular_$7 = this.printDetectCircular;
if (limit_$2 === void 0) {
limit_$2 = origPrintLength_$5
};
if (readable_$3 === void 0) {
readable_$3 = !escape_$1
};
if (depth_$4 === void 0) {
depth_$4 = origPrintDepth_$6
};
var circular_$8 = origPrintDetectCircular_$7;
if (depth_$4 < 0) {
return "\u2026"
};
var s_$9 = "";
var isreadable_$a = false;
var id_$b = this.IDForObject(thing_$0);
if (limit_$2 > 0) {
try {
if (id_$b !== null && circular_$8 != null && circular_$8[id_$b]) {
var cache_$c = circular_$8[id_$b];
if (cache_$c && cache_$c !== true) {
s_$9 = cache_$c.description;
isreadable_$a = cache_$c.readable
} else {
readable_$3 = true
}} else {
if (id_$b !== null) {
if (!(Array["$lzsc$isa"] ? Array.$lzsc$isa(circular_$8) : circular_$8 instanceof Array)) {
circular_$8 = []
};
circular_$8[id_$b] = true
};
this.printLength = limit_$2;
this.printDepth = depth_$4;
this.printDetectCircular = circular_$8;
var d_$d = this.__StringDescription(thing_$0, escape_$1, limit_$2, readable_$3, depth_$4);
s_$9 = d_$d.description;
isreadable_$a = d_$d.readable;
if (s_$9 && id_$b !== null) {
circular_$8[id_$b] = d_$d
}}}
finally {
this.printLength = origPrintLength_$5;
this.printDepth = origPrintDepth_$6;
this.printDetectCircular = origPrintDetectCircular_$7
}};
if ((isreadable_$a || !readable_$3) && s_$9 && s_$9.length < limit_$2) {
return s_$9
};
var r_$e = "\xAB";
r_$e += this.__typeof(thing_$0);
var room_$f = limit_$2 - r_$e.length - 4;
if (readable_$3 && (!isreadable_$a || s_$9 && s_$9.length >= room_$f) && id_$b === null) {
id_$b = this.IDForObject(thing_$0, true)
};
if (readable_$3 && id_$b !== null) {
r_$e += "#" + id_$b
};
if (s_$9) {
if (room_$f > 0) {
r_$e += "| ";
r_$e += this.abbreviate(s_$9, room_$f)
}};
r_$e += "\xBB";
return r_$e
};
$lzsc$temp["displayName"] = "__String";
return $lzsc$temp
})(), "inspectContext", null, "inspectInternal", (function () {
var $lzsc$temp = function (obj_$0, showInternalProperties_$1) {
switch (arguments.length) {
case 1:
showInternalProperties_$1 = void 0
};
var si_$2 = showInternalProperties_$1 === void 0 ? Debug.showInternalProperties : false;
var octx_$3 = this.inspectContext;
var opdc_$4 = this.printDetectCircular;
try {
var isobj_$5 = this.isObjectLike(obj_$0);
if (!isobj_$5) {
var esc_$6 = false;
var pl_$7 = Infinity;
var pr_$8 = false
} else {
var esc_$6 = true;
var pl_$7 = this.inspectPrintLength;
var pr_$8 = true
};
this.inspectContext = obj_$0;
var id_$9 = this.IDForObject(obj_$0);
if (isobj_$5 && id_$9 !== null) {
var pdc_$a = this.printDetectCircular = [];
var d_$b = this.__StringDescription(obj_$0, true, pl_$7, true, 0);
pdc_$a[id_$9] = d_$b.readable ? d_$b.description : true
};
var name_$c = LzMessage.xmlEscape(this.__String(obj_$0, esc_$6, pl_$7, pr_$8, 0));
if (!isobj_$5) {
return name_$c
};
var names_$d = [];
var indices_$e = this.isArrayLike(obj_$0) && !this.isStringLike(obj_$0) ? [] : null;
this.objectOwnProperties(obj_$0, names_$d, indices_$e, Infinity, si_$2);
names_$d.sort(this.caseInsensitiveOrdering);
if (indices_$e) {
indices_$e.sort(this.numericOrdering)
};
var description_$f = "";
var nnames_$g = names_$d.length;
var val_$h;
var wid_$i = 0;
if (("markGeneration" in this) && this.markGeneration > 0) {
for (var i_$j = 0;i_$j < nnames_$g;i_$j++) {
var keywidth_$k = names_$d[i_$j].length;
if (keywidth_$k > wid_$i) {
wid_$i = keywidth_$k
}}};
if (indices_$e) {
var keywidth_$k = ("" + obj_$0.length).length;
if (keywidth_$k > wid_$i) {
wid_$i = keywidth_$k
}};
var last_$l;
for (var i_$j = 0;i_$j < nnames_$g;i_$j++) {
var key_$m = names_$d[i_$j];
if (key_$m != last_$l) {
last_$l = key_$m;
val_$h = obj_$0[key_$m];
if (si_$2 || !this.internalProperty(String(key_$m)) && !this.internalProperty(this.__typeof(val_$h))) {
description_$f += "  " + this.computeSlotDescription(obj_$0, key_$m, val_$h, wid_$i) + "\n"
}}};
if (indices_$e) {
for (var i_$j = 0;i_$j < indices_$e.length;i_$j++) {
var key_$m = indices_$e[i_$j];
val_$h = obj_$0[key_$m];
description_$f += "  " + this.computeSlotDescription(obj_$0, key_$m, val_$h, wid_$i) + "\n"
}};
if (("markGeneration" in this) && this.markGeneration > 0) {
var leaked_$n = this.annotation.leaked;
if (this.isObjectLike(obj_$0) && (Function["$lzsc$isa"] ? Function.$lzsc$isa(obj_$0["hasOwnProperty"]) : obj_$0["hasOwnProperty"] instanceof Function) && obj_$0.hasOwnProperty(leaked_$n) && obj_$0[leaked_$n]) {
name_$c += " (\xA3" + obj_$0[leaked_$n] + ")"
}}}
catch (e_$o) {
try {
description_$f = this.formatToString("Error: %0.24#s computing inspectInternal", e_$o).toHTML()
}
catch (e_$o) {
description_$f = "Recursive error computing inspectInternal"
}}
finally {
this.printDetectCircular = opdc_$4;
this.inspectContext = octx_$3
};
if (description_$f != "") {
description_$f = " {\n" + description_$f + "}"
};
return name_$c + description_$f
};
$lzsc$temp["displayName"] = "inspectInternal";
return $lzsc$temp
})(), "computeSlotDescription", (function () {
var $lzsc$temp = function (obj_$0, key_$1, val_$2, wid_$3) {
var r_$4 = key_$1 + ":";
wid_$3++;
try {
if (("markGeneration" in this) && this.markGeneration > 0) {
var annotation_$5 = this.annotation;
var leaked_$6 = annotation_$5.leaked;
var why_$7 = annotation_$5.why;
var wf_$8 = "        ";
wid_$3 += wf_$8.length;
if ((Object["$lzsc$isa"] ? Object.$lzsc$isa(val_$2) : val_$2 instanceof Object) && (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$2["hasOwnProperty"]) : val_$2["hasOwnProperty"] instanceof Function) && val_$2.hasOwnProperty(leaked_$6) && val_$2[leaked_$6] && (!obj_$0.hasOwnProperty(leaked_$6) || val_$2[why_$7].indexOf(obj_$0[why_$7]) == 0)) {
r_$4 += this.pad(" (\xA3" + val_$2[leaked_$6] + ")", wf_$8.length)
} else {
r_$4 += wf_$8
}};
var ostr_$9 = LzMessage.xmlEscape(this.__String(val_$2, true, this.inspectPrintLength, false, this.inspectPrintDepth));
var id_$a = this.IDForObject(val_$2);
r_$4 = this.pad(r_$4, wid_$3);
r_$4 += " " + this.console.makeObjectLink(ostr_$9, id_$a)
}
catch (e_$b) {
try {
r_$4 += this.formatToString(" Error: %0.24#s computing slot description", e_$b).toHTML()
}
catch (e_$b) {
r_$4 += " Error computing description"
}};
return r_$4
};
$lzsc$temp["displayName"] = "computeSlotDescription";
return $lzsc$temp
})(), "isObjectLike", (function () {
var $lzsc$temp = function (obj_$0) {
return obj_$0 && ((Object["$lzsc$isa"] ? Object.$lzsc$isa(obj_$0) : obj_$0 instanceof Object) || typeof obj_$0 == "object")
};
$lzsc$temp["displayName"] = "isObjectLike";
return $lzsc$temp
})(), "isArrayLike", (function () {
var $lzsc$temp = function (obj_$0) {
if (obj_$0 && ((Array["$lzsc$isa"] ? Array.$lzsc$isa(obj_$0) : obj_$0 instanceof Array) || obj_$0["length"] != void 0)) {
var ol_$1 = obj_$0.length;
return (typeof ol_$1 == "number" || (Number["$lzsc$isa"] ? Number.$lzsc$isa(ol_$1) : ol_$1 instanceof Number)) && (ol_$1 | 0) === ol_$1 && ol_$1 >= 0
};
return false
};
$lzsc$temp["displayName"] = "isArrayLike";
return $lzsc$temp
})(), "isStringLike", (function () {
var $lzsc$temp = function (obj_$0) {
return typeof obj_$0 == "string" || (String["$lzsc$isa"] ? String.$lzsc$isa(obj_$0) : obj_$0 instanceof String) || (LzMessage["$lzsc$isa"] ? LzMessage.$lzsc$isa(obj_$0) : obj_$0 instanceof LzMessage)
};
$lzsc$temp["displayName"] = "isStringLike";
return $lzsc$temp
})(), "caseInsensitiveOrdering", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var al_$2 = a_$0.toLowerCase();
var bl_$3 = b_$1.toLowerCase();
return (al_$2 > bl_$3) - (al_$2 < bl_$3)
};
$lzsc$temp["displayName"] = "caseInsensitiveOrdering";
return $lzsc$temp
})(), "numericOrdering", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var al_$2 = Number(a_$0);
var bl_$3 = Number(b_$1);
return Number(al_$2 > bl_$3) - Number(al_$2 < bl_$3)
};
$lzsc$temp["displayName"] = "numericOrdering";
return $lzsc$temp
})(), "internalProperty", (function () {
var $lzsc$temp = function (str_$0) {
var ipp_$1 = LzDebugService.internalPropertyPrefixes;
for (var key_$2 = ipp_$1.length - 1;key_$2 >= 0;key_$2--) {
if (str_$0.indexOf(ipp_$1[key_$2]) == 0) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "internalProperty";
return $lzsc$temp
})(), "abbreviate", (function () {
var $lzsc$temp = function (s_$0, l_$1) {
switch (arguments.length) {
case 1:
l_$1 = NaN
};
if (isNaN(l_$1)) {
l_$1 = Debug.printLength
};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["abbreviate"] || this.nextMethod(arguments.callee, "abbreviate")).call(this, s_$0, l_$1)
};
$lzsc$temp["displayName"] = "abbreviate";
return $lzsc$temp
})(), "versionInfo", (function () {
var $lzsc$temp = function () {
this.write(LzCanvas.versionInfoString())
};
$lzsc$temp["displayName"] = "versionInfo";
return $lzsc$temp
})(), "lastError", null, "bugReport", (function () {
var $lzsc$temp = function (error_$0, verbose) {
var inspect;
inspect = (function () {
var $lzsc$temp = function (obj_$0, verbose_$1) {
var id_$2 = verbose_$1 && Debug.IDForObject(obj_$0);
if (id_$2 && !(id_$2 in inspected)) {
inspected[id_$2] = obj_$0
};
return obj_$0
};
$lzsc$temp["displayName"] = "inspect";
return $lzsc$temp
})();
switch (arguments.length) {
case 0:
error_$0 = null;;case 1:
verbose = null
};
if (error_$0 == null) {
try {
with (this.environment) {
if (LzSourceMessage["$lzsc$isa"] ? LzSourceMessage.$lzsc$isa(_) : _ instanceof LzSourceMessage) {
error_$0 = _
} else {
error_$0 = this.lastError
}}}
catch (e_$1) {}};
if (typeof error_$0 == "number") {
error_$0 = this.ObjectForID(error_$0)
};
if (!(LzSourceMessage["$lzsc$isa"] ? LzSourceMessage.$lzsc$isa(error_$0) : error_$0 instanceof LzSourceMessage)) {
this.error("You must provide a debugger message to report.  Please inspect a debugger message and try again.");
return
};
if (!(LzBacktrace["$lzsc$isa"] ? LzBacktrace.$lzsc$isa(error_$0.backtrace) : error_$0.backtrace instanceof LzBacktrace)) {
this.error("Backtraces must be on to report a bug.  Please enable backtracing and try again.");
return
};
if (verbose == null) {
verbose = this.showInternalProperties
};
var inspected = {};
this.format("Please copy the following information into your bug report:\n\n---START OF BUG REPORT---\n\nLPS VERSION INFORMATION:\n");
this.versionInfo();
this.format("\nERROR MESSAGE: %s", error_$0);
this.format("\nERROR BACKTRACE:");
var that = this;
error_$0.backtrace.map((function () {
var $lzsc$temp = function (frame_$0) {
that.format("\n%w", frame_$0);
if (frame_$0.context) {
that.format("\n  this: %#w", inspect(frame_$0.context, true))
};
var args_$1 = frame_$0.arguments;
for (var i_$2 = 0;i_$2 < args_$1.length;i_$2 += 2) {
that.format("\n  %s: %#w", args_$1[i_$2], inspect(args_$1[i_$2 + 1], verbose))
}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1633/7";
return $lzsc$temp
})());
var keys_$2 = [];
for (var id_$3 in inspected) {
keys_$2.push(id_$3)
};
if (keys_$2.length > 0) {
this.format("\n\nOBJECT DETAILS:");
keys_$2.sort((function () {
var $lzsc$temp = function (a_$0, b_$1) {
var al_$2 = parseInt(a_$0);
var bl_$3 = parseInt(b_$1);
return (al_$2 > bl_$3 ? 1 : 0) - (al_$2 < bl_$3 ? 1 : 0)
};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#1651/17";
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
})(), "objectOwnProperties", (function () {
var $lzsc$temp = function (obj_$0, names_$1, indices_$2, limit_$3, nonEnumerable_$4) {
switch (arguments.length) {
case 1:
names_$1 = null;;case 2:
indices_$2 = null;;case 3:
limit_$3 = Infinity;;case 4:
nonEnumerable_$4 = false
};
var alen_$5 = false;
if (this.isArrayLike(obj_$0)) {
alen_$5 = obj_$0.length
};
var proto_$6 = false;
try {
proto_$6 = obj_$0["constructor"] && typeof obj_$0.constructor["prototype"] == "object" ? obj_$0.constructor.prototype : false
}
catch (e_$7) {};
for (var key_$8 in obj_$0) {
var isown_$9 = false;
if (!proto_$6) {
isown_$9 = true
} else {
try {
isown_$9 = obj_$0.hasOwnProperty(key_$8)
}
catch (e_$7) {};
if (!isown_$9) {
var pk_$a;
try {
pk_$a = proto_$6[key_$8]
}
catch (e_$7) {};
isown_$9 = obj_$0[key_$8] !== pk_$a
}};
if (isown_$9) {
if (alen_$5 != false && (key_$8 | 0) == key_$8 && 0 <= key_$8 && key_$8 < alen_$5) {
if (indices_$2) {
indices_$2.push(Number(key_$8));
if (--limit_$3 == 0) {
break
}}} else {
if (names_$1) {
names_$1.push(key_$8);
if (--limit_$3 == 0) {
break
}}}}}};
$lzsc$temp["displayName"] = "objectOwnProperties";
return $lzsc$temp
})(), "stackOverflow", (function () {
var $lzsc$temp = function () {
var bs_$0 = this.backtraceStack;
try {
var old_$1 = bs_$0.maxDepth;
bs_$0.maxDepth *= 1.25;
throw new Error(Debug.error("Stack overflow: %s", Debug.backtrace(bs_$0.length - 50)))
}
finally {
bs_$0.maxDepth = old_$1
}};
$lzsc$temp["displayName"] = "stackOverflow";
return $lzsc$temp
})(), "backtrace", (function () {
var $lzsc$temp = function (skip_$0) {
switch (arguments.length) {
case 0:
skip_$0 = 1
};
if (Debug.backtraceStack.length > skip_$0) {
return new LzBacktrace(skip_$0)
};
return null
};
$lzsc$temp["displayName"] = "backtrace";
return $lzsc$temp
})()], [LzFormatter, LzBootstrapDebugService], ["messageLevels", {ALL: 0, MONITOR: 1, TRACE: 2, DEBUG: 3, INFO: 4, WARNING: 5, ERROR: 6, NONE: 7}, "internalPropertyPrefixes", ["$", "__", "_dbg_", "LzDeclared"]]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/LzDebug.lzs#107/1";
return $lzsc$temp
})()(LzDebugService);
var Debug = new LzDebugService(Debug);
var __LzDebug = Debug;
Class.make("LzDHTMLDebugConsole", ["DebugWindow", null, "__reNewline", RegExp("&#xa;|&#xA;|&#10;|\\n", "g"), "$lzsc$initialize", (function () {
var $lzsc$temp = function (iframe_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.DebugWindow = iframe_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "addHTMLText", (function () {
var $lzsc$temp = function (str_$0) {
var dw_$1 = this.DebugWindow;
var dwd_$2 = dw_$1.document;
var span_$3 = dwd_$2.createElement("span");
var dwdb_$4 = dwd_$2.body;
span_$3.innerHTML = '<span class="OUTPUT">' + str_$0.replace(this.__reNewline, "<br />") + "</span>";
dwdb_$4.appendChild(span_$3);
dw_$1.scrollTo(0, dwdb_$4.scrollHeight)
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {
var dw_$0 = this.DebugWindow;
dw_$0.document.body.innerHTML = ""
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "echo", (function () {
var $lzsc$temp = function (str_$0, newLine_$1) {
switch (arguments.length) {
case 1:
newLine_$1 = true
};
this.addHTMLText('<span class="DEBUG">' + str_$0 + "</span>" + (newLine_$1 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "doEval", (function () {
var $lzsc$temp = function (expr_$0) {
try {
with (Debug.environment) {
var value_$1 = window.eval("(" + expr_$0 + ")")
};
Debug.displayResult(value_$1)
}
catch (e_$2) {
if (!(SyntaxError["$lzsc$isa"] ? SyntaxError.$lzsc$isa(e_$2) : e_$2 instanceof SyntaxError)) {
Debug.error("%s", e_$2);
return
};
try {
with (Debug.environment) {
var value_$1 = window.eval(expr_$0)
};
Debug.displayResult(value_$1)
}
catch (e_$2) {
Debug.error("%s", e_$2)
}}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})(), "makeObjectLink", (function () {
var $lzsc$temp = function (rep_$0, id_$1, attrs_$2) {
switch (arguments.length) {
case 2:
attrs_$2 = null
};
var type_$3 = attrs_$2 && attrs_$2["type"] ? attrs_$2.type : "INSPECT";
if (id_$1 != null) {
var obj_$4 = Debug.ObjectForID(id_$1);
var tip_$5 = Debug.formatToString("Inspect %0.32#w", obj_$4).toString().toHTML();
return '<span class="' + type_$3 + '" title="' + tip_$5 + '" onclick="window.parent.$modules.lz.Debug.objectLinkHandler(event, ' + id_$1 + ')">' + rep_$0 + "</span>"
};
return rep_$0
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})()], LzBootstrapDebugConsole);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#12/1";
return $lzsc$temp
})()(LzDHTMLDebugConsole);
Class.make("LzDHTMLDebugService", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (base_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, base_$0);
var copy_$1 = {backtraceStack: true, uncaughtBacktraceStack: true, logger: true, console: true};
for (var k_$2 in copy_$1) {
this[k_$2] = base_$0[k_$2]
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "createDebugIframe", (function () {
var $lzsc$temp = function () {
var debugurl_$0 = lz.embed.options.serverroot + "lps/includes/laszlo-debugger.html";
var form_$1 = '<form id="dhtml-debugger-input" onsubmit="$modules.lz.Debug.doEval(document.getElementById(\'LaszloDebuggerInput\').value); return false" action="#">';
var iframe_$2 = '<iframe id="LaszloDebugger" name="LaszloDebugger" src="' + debugurl_$0 + '" width="100%" height="200"></iframe>';
var inputdiv_$3 = '<div><input id="LaszloDebuggerInput" style="width:78%;" type="text"/><input type="button" onclick="$modules.lz.Debug.doEval(document.getElementById(\'LaszloDebuggerInput\').value); return false" value="eval"/><input type="button" onclick="$modules.lz.Debug.clear(); return false" value="clear"/><input type="button" onclick="$modules.lz.Debug.bugReport(); return false" value="bug report"/></div></form>';
var debugdiv_$4 = document.createElement("div");
debugdiv_$4.innerHTML = form_$1 + iframe_$2 + inputdiv_$3;
debugdiv_$4.onmouseover = (function () {
var $lzsc$temp = function (e_$0) {
if (!e_$0) e_$0 = global.window.event;
e_$0.cancelBubble = true;
LzKeyboardKernel.setKeyboardControl(false, true);
return false
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#148/28";
return $lzsc$temp
})();
var y_$5 = canvas.height - 230;
lz.embed.__setAttr(debugdiv_$4, "style", "position:absolute;z-index:10000000;top:" + y_$5 + "px;width:100%;");
canvas.sprite.__LZdiv.appendChild(debugdiv_$4);
var style_$6 = debugdiv_$4.style;
style_$6.position = "absolute";
style_$6.top = y_$5;
style_$6.zIndex = 10000000;
style_$6.width = "100%";
return global.window.frames["LaszloDebugger"]
};
$lzsc$temp["displayName"] = "createDebugIframe";
return $lzsc$temp
})(), "makeDebugWindow", (function () {
var $lzsc$temp = function () {
for (var n_$0 in __ES3Globals) {
var p_$1 = __ES3Globals[n_$0];
try {
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(p_$1) : p_$1 instanceof Function)) {
if (!p_$1._dbg_name) {
p_$1._dbg_name = n_$0
}} else if (!Debug.functionName(p_$1)) {
p_$1[Debug.FUNCTION_NAME] = n_$0
}}
catch (e_$2) {}};
if (LzBrowserKernel.getInitArg("lzconsoledebug") == "true") {
this.attachDebugConsole(new LzDHTMLDebugConsole(this.createDebugIframe()))
} else {
new (lz.LzDebugWindow)()
}};
$lzsc$temp["displayName"] = "makeDebugWindow";
return $lzsc$temp
})(), "objectLinkHandler", (function () {
var $lzsc$temp = function (event_$0, id_$1) {
event_$0.cancelBubble = true;
if (event_$0.stopPropagation) {
event_$0.stopPropagation()
};
this.displayObj(id_$1);
return false
};
$lzsc$temp["displayName"] = "objectLinkHandler";
return $lzsc$temp
})(), "hasFeature", (function () {
var $lzsc$temp = function (feature_$0, level_$1) {
return document.implementation && document.implementation.hasFeature && document.implementation.hasFeature(feature_$0, level_$1)
};
$lzsc$temp["displayName"] = "hasFeature";
return $lzsc$temp
})(), "__StringDescription", (function () {
var $lzsc$temp = function (thing_$0, escape_$1, limit_$2, readable_$3, depth_$4) {
var nodeToString;
nodeToString = (function () {
var $lzsc$temp = function (node_$0) {
var tn_$1 = node_$0.nodeName || "";
var path_$2 = tn_$1.toLowerCase();
var sprite_$3 = node_$0.owner;
var spritedivpath_$4;
if (sprite_$3 instanceof LzSprite && sprite_$3.owner.sprite === sprite_$3) {
for (var key_$5 in sprite_$3) {
if (sprite_$3[key_$5] === node_$0) {
spritedivpath_$4 = Debug.formatToString("%w/@sprite/@%s", sprite_$3.owner, key_$5)
}}};
if (node_$0.nodeType == 1) {
var id_$6 = node_$0.id;
var cn_$7 = node_$0.className;
if (id_$6 && !spritedivpath_$4) {
path_$2 += "#" + encodeURIComponent(id_$6)
} else if (cn_$7) {
var more_$8 = cn_$7.indexOf(" ");
if (more_$8 == -1) {
more_$8 = cn_$7.length
};
path_$2 += "." + cn_$7.substring(0, more_$8)
}};
if (spritedivpath_$4) {
return spritedivpath_$4 + (path_$2.length > 0 ? "/" + path_$2 : "")
};
var parent_$9 = node_$0.parentNode;
if (parent_$9) {
var index_$a, count_$b = 0;
for (var sibling_$c = parent_$9.firstChild;sibling_$c;sibling_$c = sibling_$c.nextSibling) {
if (tn_$1 == sibling_$c.nodeName) {
count_$b++;
if (index_$a) break
};
if (node_$0 === sibling_$c) {
index_$a = count_$b
}};
if (count_$b > 1) {
path_$2 += "[" + index_$a + "]"
};
try {
return nodeToString(parent_$9) + "/" + path_$2
}
catch (e_$d) {
return "\u2026/" + path_$2
}};
return path_$2
};
$lzsc$temp["displayName"] = "nodeToString";
return $lzsc$temp
})();
try {
if ((!(!global.window.HTMLElement) ? thing_$0 instanceof HTMLElement : typeof thing_$0 == "object" && !thing_$0.constructor) && !isNaN(Number(thing_$0["nodeType"]))) {
var style_$5 = thing_$0.style.cssText;
if (style_$5 != "") {
style_$5 = '[@style="' + style_$5 + '"]'
};
return {readable: false, description: nodeToString(thing_$0) + style_$5}} else if (this.hasFeature("mouseevents", "2.0") && (global["MouseEvent"]["$lzsc$isa"] ? global["MouseEvent"].$lzsc$isa(thing_$0) : thing_$0 instanceof global["MouseEvent"])) {
var desc_$6 = thing_$0.type;
if (thing_$0.shiftKey) {
desc_$6 = "shift-" + desc_$6
};
if (thing_$0.ctrlKey) {
desc_$6 = "ctrl-" + desc_$6
};
if (thing_$0.metaKey) {
desc_$6 = "meta-" + desc_$6
};
if (thing_$0.altKey) {
desc_$6 = "alt-" + desc_$6
};
switch (thing_$0.detail) {
case 2:
desc_$6 = "double-" + desc_$6;break;;case 3:
desc_$6 = "triple-" + desc_$6;break
};
switch (thing_$0.button) {
case 1:
desc_$6 += "-middle";break;;case 2:
desc_$6 += "-right";break
};
return {readable: false, description: desc_$6}}}
catch (e_$7) {};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__StringDescription"] || this.nextMethod(arguments.callee, "__StringDescription")).call(this, thing_$0, escape_$1, limit_$2, readable_$3, depth_$4)
};
$lzsc$temp["displayName"] = "__StringDescription";
return $lzsc$temp
})(), "functionName", (function () {
var $lzsc$temp = function (fn_$0, mustBeUnique_$1) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(fn_$0) : fn_$0 instanceof Function) {
if (fn_$0.hasOwnProperty("tagname")) {
var n_$2 = fn_$0.tagname;
if (!mustBeUnique_$1 || fn_$0 === lz[n_$2]) {
return "<" + n_$2 + ">"
} else {
return null
}}};
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["functionName"] || this.nextMethod(arguments.callee, "functionName")).call(this, fn_$0, mustBeUnique_$1)
};
$lzsc$temp["displayName"] = "functionName";
return $lzsc$temp
})(), "objectOwnProperties", (function () {
var $lzsc$temp = function (obj_$0, names_$1, indices_$2, limit_$3, nonEnumerable_$4) {
switch (arguments.length) {
case 1:
names_$1 = null;;case 2:
indices_$2 = null;;case 3:
limit_$3 = Infinity;;case 4:
nonEnumerable_$4 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["objectOwnProperties"] || this.nextMethod(arguments.callee, "objectOwnProperties")).call(this, obj_$0, names_$1, indices_$2, limit_$3, nonEnumerable_$4);
var proto_$5 = false;
try {
proto_$5 = obj_$0["constructor"] && typeof obj_$0.constructor["prototype"] == "object" ? obj_$0.constructor.prototype : false
}
catch (e_$6) {};
if (names_$1 && nonEnumerable_$4 && proto_$5) {
var unenumerated_$7 = {callee: true, length: true, constructor: true, prototype: true};
for (var key_$8 in unenumerated_$7) {
var isown_$9 = false;
try {
isown_$9 = obj_$0.hasOwnProperty(key_$8)
}
catch (e_$6) {};
if (!isown_$9) {
var pk_$a;
try {
pk_$a = proto_$5[key_$8]
}
catch (e_$6) {};
isown_$9 = obj_$0[key_$8] !== pk_$a
};
if (isown_$9) {
for (var i_$b = 0, l_$c = names_$1.length;i_$b < l_$c;i_$b++) {
if (names_$1[i_$b] == key_$8) {
isown_$9 = false;
break
}}};
if (isown_$9) {
names_$1.push(key_$8)
}}}};
$lzsc$temp["displayName"] = "objectOwnProperties";
return $lzsc$temp
})(), "enableInspectMouseHandlers", (function () {
var $lzsc$temp = function (div_$0, enable_$1) {
if (enable_$1) {
div_$0.prev_onclick = div_$0.onclick;
div_$0.style.prev_border = div_$0.style.border;
div_$0.style.prev_margin = div_$0.style.margin;
div_$0.style.border = "1px solid red";
div_$0.style.margin = "-1px";
div_$0.onclick = (function () {
var $lzsc$temp = function (e_$0) {
Debug.write("view = ", this.owner.owner)
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#404/21";
return $lzsc$temp
})()
} else {
div_$0.onclick = div_$0.prev_onclick;
div_$0.style.border = div_$0.style.prev_border;
div_$0.style.margin = div_$0.style.prev_margin;
delete div_$0.prev_onclick;
delete div_$0.prev_margin;
delete div_$0.prev_border
}};
$lzsc$temp["displayName"] = "enableInspectMouseHandlers";
return $lzsc$temp
})(), "showDivs", (function () {
var $lzsc$temp = function (enable_$0) {
if (enable_$0 == null) enable_$0 = true;
Debug._showDivs(canvas, enable_$0)
};
$lzsc$temp["displayName"] = "showDivs";
return $lzsc$temp
})(), "_showDivs", (function () {
var $lzsc$temp = function (view_$0, enable_$1) {
var k_$2 = view_$0.sprite;
if (k_$2 != null) {
var div_$3 = k_$2.__LZdiv;
if (div_$3 != null) {
this.enableInspectMouseHandlers(div_$3, enable_$1)
}};
for (var i_$4 = 0;i_$4 < view_$0.subviews.length;i_$4++) {
var cv_$5 = view_$0.subviews[i_$4];
Debug._showDivs(cv_$5, enable_$1)
}};
$lzsc$temp["displayName"] = "_showDivs";
return $lzsc$temp
})()], LzDebugService);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#121/1";
return $lzsc$temp
})()(LzDHTMLDebugService);
var Debug = new LzDHTMLDebugService(Debug);
var __LzDebug = Debug;
if (lz.embed.browser.isIE) {
Error.prototype.toString = (function () {
var $lzsc$temp = function () {
return this.name + ": " + this.message
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebug.js#460/30";
return $lzsc$temp
})()
};
Class.make("LzDebuggerWindowConsoleBridge", ["DebugWindow", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (view_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.DebugWindow = view_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "canvasConsoleWindow", (function () {
var $lzsc$temp = function () {
return this.DebugWindow
};
$lzsc$temp["displayName"] = "canvasConsoleWindow";
return $lzsc$temp
})(), "__reNewline", RegExp("&#xa;|&#xA;|&#10;|\\n", "g"), "addHTMLText", (function () {
var $lzsc$temp = function (str_$0) {
this.DebugWindow.addHTMLText(str_$0.replace(this.__reNewline, "<br />"))
};
$lzsc$temp["displayName"] = "addHTMLText";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {
this.DebugWindow.clearWindow()
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "ensureVisible", (function () {
var $lzsc$temp = function () {
this.DebugWindow.ensureVisible()
};
$lzsc$temp["displayName"] = "ensureVisible";
return $lzsc$temp
})(), "echo", (function () {
var $lzsc$temp = function (str_$0, newLine_$1) {
switch (arguments.length) {
case 1:
newLine_$1 = true
};
this.addHTMLText('<span style="color: #00cc00">' + str_$0 + "</span>" + (newLine_$1 ? "\n" : ""))
};
$lzsc$temp["displayName"] = "echo";
return $lzsc$temp
})(), "makeObjectLink", (function () {
var $lzsc$temp = function (rep_$0, id_$1, attrs_$2) {
switch (arguments.length) {
case 2:
attrs_$2 = null
};
var color_$3 = attrs_$2 && attrs_$2["color"] ? attrs_$2.color : "#0000ff";
var decoration_$4 = attrs_$2 && attrs_$2["type"] ? "none" : "underline";
if (id_$1 != null) {
var obj_$5 = Debug.ObjectForID(id_$1);
var tip_$6 = Debug.formatToString("Inspect %0.32#w", obj_$5).toString().toHTML();
return '<span style="cursor: pointer; text-decoration: ' + decoration_$4 + "; color: " + color_$3 + '" title="' + tip_$6 + '" onclick="$modules.lz.Debug.objectLinkHandler(event, ' + id_$1 + ')">' + rep_$0 + "</span>"
};
return rep_$0
};
$lzsc$temp["displayName"] = "makeObjectLink";
return $lzsc$temp
})(), "doEval", (function () {
var $lzsc$temp = function (expr_$0) {
try {
with (Debug.environment) {
var value_$1 = window.eval("(" + expr_$0 + ")")
};
Debug.displayResult(value_$1)
}
catch (e_$2) {
if (!(SyntaxError["$lzsc$isa"] ? SyntaxError.$lzsc$isa(e_$2) : e_$2 instanceof SyntaxError)) {
Debug.error("%s", e_$2);
return
};
try {
with (Debug.environment) {
var value_$1 = window.eval(expr_$0)
};
Debug.displayResult(value_$1)
}
catch (e_$2) {
Debug.error("%s", e_$2)
}}};
$lzsc$temp["displayName"] = "doEval";
return $lzsc$temp
})()], LzDebugConsole);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzDebuggerWindowConsoleBridge.js#11/1";
return $lzsc$temp
})()(LzDebuggerWindowConsoleBridge);
Debug.sourceWarningHistory = {};
var $reportSourceWarning = (function () {
var $lzsc$temp = function (filename_$0, lineNumber_$1, msg_$2, fatal_$3) {
var warning_$4 = new (fatal_$3 ? LzError : LzWarning)(filename_$0, lineNumber_$1, msg_$2);
var warningString_$5 = warning_$4.toString();
if (Debug.sourceWarningHistory[warningString_$5]) {
return
};
Debug.sourceWarningHistory[warningString_$5] = true;
Debug.freshLine();
Debug.__write(warning_$4)
};
$lzsc$temp["displayName"] = "debugger/LzCompiler.lzs#28/28";
return $lzsc$temp
})();
Class.make("__LzStackFrame", ["__filename", void 0, "__lineno", void 0, "arguments", void 0, "context", void 0, "callee", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (args_$0) {
if (args_$0["this"]) {
this.context = args_$0["this"]
};
this.callee = args_$0.callee;
this.__filename = args_$0.filename;
this.__lineno = args_$0.lineno;
this.arguments = args_$0.concat()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "isUserFrame", (function () {
var $lzsc$temp = function () {
return this.__filename.indexOf("lfc") != 0
};
$lzsc$temp["displayName"] = "isUserFrame";
return $lzsc$temp
})(), "filename", (function () {
var $lzsc$temp = function () {
return this.__filename
};
$lzsc$temp["displayName"] = "filename";
return $lzsc$temp
})(), "lineno", (function () {
var $lzsc$temp = function () {
return this.__lineno
};
$lzsc$temp["displayName"] = "lineno";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
var callee_$0 = this.callee;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(callee_$0) : callee_$0 instanceof Function) {
return Debug.functionName(callee_$0, false)
};
return "" + callee_$0
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
__LzStackFrame.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
return Debug.formatToString("%0.64s @%s#%d", this.callee, this.__filename, this.__lineno)
};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#81/40";
return $lzsc$temp
})();
__LzStackFrame.prototype._dbg_typename = "StackFrame"
}}};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#20/1";
return $lzsc$temp
})()(__LzStackFrame);
Class.make("LzBacktrace", ["length", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (skip_$0) {
switch (arguments.length) {
case 0:
skip_$0 = 0
};
var bs_$1 = Debug.backtraceStack;
var l_$2 = bs_$1.length - skip_$0;
this.length = l_$2;
for (var i_$3 = 0;i_$3 < l_$2;i_$3++) {
var fr_$4 = bs_$1[i_$3];
if (!fr_$4.hasOwnProperty("__LzStackFrame") || fr_$4["lineno"] != fr_$4.__LzStackFrame.lineno()) {
fr_$4.__LzStackFrame = new __LzStackFrame(fr_$4)
};
this[i_$3] = fr_$4.__LzStackFrame
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "userStackFrame", (function () {
var $lzsc$temp = function () {
for (var i_$0 = this.length - 1;i_$0 >= 0;i_$0--) {
var fr_$1 = this[i_$0];
if (fr_$1.isUserFrame()) {
return fr_$1
}};
return null
};
$lzsc$temp["displayName"] = "userStackFrame";
return $lzsc$temp
})(), "map", (function () {
var $lzsc$temp = function (fn_$0, limit_$1) {
switch (arguments.length) {
case 1:
limit_$1 = NaN
};
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(fn_$0) : fn_$0 instanceof Function) {
if (isNaN(limit_$1)) {
limit_$1 = this.length
};
for (var i_$2 = this.length - 1;i_$2 >= 0 && limit_$1 > 0;i_$2--, limit_$1--) {
fn_$0(this[i_$2])
}}};
$lzsc$temp["displayName"] = "map";
return $lzsc$temp
})(), "toStringInternal", (function () {
var $lzsc$temp = function (length_$0) {
switch (arguments.length) {
case 0:
length_$0 = NaN
};
if (isNaN(length_$0)) {
length_$0 = Debug.printLength
};
var backtrace_$1 = "";
var sep_$2 = " <- ";
for (var i_$3 = this.length - 1;i_$3 >= 0 && backtrace_$1.length < length_$0;i_$3--) {
backtrace_$1 += this[i_$3] + sep_$2
};
if (backtrace_$1 != "" && i_$3 < 0) {
backtrace_$1 = backtrace_$1.substring(0, backtrace_$1.length - sep_$2.length)
};
backtrace_$1 = Debug.abbreviate(backtrace_$1, length_$0);
return backtrace_$1
};
$lzsc$temp["displayName"] = "toStringInternal";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.toStringInternal()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzBacktrace.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
return this.toStringInternal(Debug.inspectPrintLength)
};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#188/37";
return $lzsc$temp
})();
LzBacktrace.prototype._dbg_typename = "Backtrace"
}}};
$lzsc$temp["displayName"] = "debugger/LzBacktrace.lzs#105/1";
return $lzsc$temp
})()(LzBacktrace);
Class.make("LzTrace", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "TRACE", "color", "#00cc00", "format", LzSourceMessage.format]);
Debug.traceMessage = (function () {
var $lzsc$temp = function (control_$0) {
var args_$1 = Array.prototype.slice.call(arguments, 1);
return this.warnInternal.apply(this, [LzTrace, control_$0].concat(args_$1))
};
$lzsc$temp["displayName"] = "debugger/LzTrace.lzs#44/22";
return $lzsc$temp
})();
Debug.trace = (function () {
var $lzsc$temp = function (who_$0, methodName_$1) {
if (who_$0[methodName_$1] instanceof Function) {
var f = who_$0[methodName_$1];
var m_$2 = (function () {
var $lzsc$temp = function () {
Debug.traceMessage("[%6.2f] %s.apply(%w, %w)", new Date().getTime() % 1000000, f, this, arguments);
var r_$0 = f.apply(this, arguments);
Debug.traceMessage("[%6.2f] %s -> %w", new Date().getTime() % 1000000, f, r_$0);
return r_$0
};
$lzsc$temp["displayName"] = "debugger/LzTrace.lzs#69/13";
return $lzsc$temp
})();
m_$2._dbg_previous_definition = f;
if (Instance["$lzsc$isa"] ? Instance.$lzsc$isa(who_$0) : who_$0 instanceof Instance) {
who_$0.addProperty(methodName_$1, m_$2)
} else {
who_$0[methodName_$1] = m_$2
};
return who_$0[methodName_$1] === m_$2
} else {
Debug.error("%w.%s is not a function", who_$0, methodName_$1)
};
return false
};
$lzsc$temp["displayName"] = "debugger/LzTrace.lzs#66/15";
return $lzsc$temp
})();
Debug.untrace = (function () {
var $lzsc$temp = function (who_$0, methodName_$1) {
if (who_$0[methodName_$1] instanceof Function) {
var f_$2 = who_$0[methodName_$1];
var p_$3 = f_$2["_dbg_previous_definition"];
if (p_$3) {
if (who_$0.hasOwnProperty(methodName_$1)) {
delete who_$0[methodName_$1]
};
if (who_$0[methodName_$1] !== p_$3) {
if (Instance["$lzsc$isa"] ? Instance.$lzsc$isa(who_$0) : who_$0 instanceof Instance) {
who_$0.addProperty(methodName_$1, p_$3)
} else {
who_$0[methodName_$1] = p_$3
}};
return who_$0[methodName_$1] === p_$3
} else {
Debug.error("%w.%s is not being traced", who_$0, methodName_$1)
}} else {
Debug.error("%w.%s is not a function", who_$0, methodName_$1)
};
return false
};
$lzsc$temp["displayName"] = "debugger/LzTrace.lzs#101/17";
return $lzsc$temp
})();
Class.make("LzMonitor", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (file_$0, line_$1, message_$2, node_$3) {
switch (arguments.length) {
case 0:
file_$0 = null;;case 1:
line_$1 = 0;;case 2:
message_$2 = "";;case 3:
node_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, file_$0, line_$1, message_$2, node_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzSourceMessage, ["type", "MONITOR", "color", "#00cc00", "format", LzSourceMessage.format]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "debugger/LzMonitor.lzs#19/1";
return $lzsc$temp
})()(LzMonitor);
Debug.monitorMessage = (function () {
var $lzsc$temp = function (control_$0) {
var args_$1 = Array.prototype.slice.call(arguments, 1);
return this.warnInternal.apply(this, [LzMonitor, control_$0].concat(args_$1))
};
$lzsc$temp["displayName"] = "debugger/LzMonitor.lzs#44/24";
return $lzsc$temp
})();
Debug.monitor = (function () {
var $lzsc$temp = function (who, what) {
var o = who[what];
var s_$0 = (function () {
var $lzsc$temp = function (n_$0) {
var c_$1 = arguments.callee.caller;
if (!c_$1 && Debug.backtraceStack instanceof Array) {
var bs_$2 = Debug.backtraceStack;
c_$1 = bs_$2[bs_$2.length - 2].callee
};
Debug.monitorMessage("[%6.2f] %s: %w.%s: %w -> %w", new Date().getTime() % 1000000, c_$1 || "(unknown)", who, what, o, n_$0);
return o = n_$0
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#23/11";
return $lzsc$temp
})();
var g_$1 = (function () {
var $lzsc$temp = function () {
return o
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#37/11";
return $lzsc$temp
})();
try {
delete who[what];
who.__defineGetter__(what, g_$1);
who.__defineSetter__(what, s_$0);
with (who) {
if (eval(what) !== o) {
throw new Error("Debug.monitor: failed to install functional getter/setter")
}}}
catch (e_$2) {
Debug.error("Debug.monitor: Can't monitor %s.%s", who, what);
delete who[what];
who[what] = o
}};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#21/17";
return $lzsc$temp
})();
Debug.unmonitor = (function () {
var $lzsc$temp = function (who_$0, what_$1) {
var o_$2 = who_$0[what_$1];
delete who_$0[what_$1];
who_$0[what_$1] = o_$2
};
$lzsc$temp["displayName"] = "debugger/platform/dhtml/LzMonitor.js#71/19";
return $lzsc$temp
})();
Debug.annotation = {marked: "_dbg_marked", why: "_dbg_why", size: "_dbg_smoots", total: "_dbg_weight", leaked: "_dbg_leaked"};
Debug.allAnnotations = [];
(function () {
var $lzsc$temp = function () {
for (var a_$0 in Debug.annotation) {
Debug.allAnnotations.push(Debug.annotation[a_$0])
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
Debug.mark = (function () {
var $lzsc$temp = function (o_$0) {
if (!o_$0 || !(typeof o_$0 == "object" || typeof o_$0 == "movieclip") || !(typeof o_$0["hasOwnProperty"] == "function")) {
if (this.debugTrace) {
console.log("Not marking %s\n", o_$0)
};
return false
};
try {
var annotation_$1 = this.annotation;
delete o_$0[annotation_$1.leaked];
o_$0[annotation_$1.marked] = this.markGeneration
}
catch (e_$2) {
return false
};
return true
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#55/14";
return $lzsc$temp
})();
Debug.isMarked = (function () {
var $lzsc$temp = function (o_$0) {
if (!o_$0 || !(typeof o_$0 == "object" || typeof o_$0 == "movieclip") || !(typeof o_$0["hasOwnProperty"] == "function")) {
if (this.debugTrace) {
console.log("Not tracing %s\n", o_$0)
};
return true
};
var marked_$1 = this.annotation.marked;
try {
if (!o_$0.hasOwnProperty(marked_$1)) {
return null
};
return o_$0[marked_$1] == this.markGeneration
}
catch (e_$2) {
if (this.debugTrace) {
console.log("Not tracing %s\n", o_$0)
};
return true
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#88/18";
return $lzsc$temp
})();
Debug.obstack = [];
Debug.traceStep = (function () {
var $lzsc$temp = function (steps_$0, milliseconds_$1) {
switch (arguments.length) {
case 0:
steps_$0 = Infinity;;case 1:
milliseconds_$1 = 1300
};
var loopStart_$2 = new Date().getTime();
var loopCount_$3 = 0;
var loopElapsed_$4 = 0;
var os_$5 = this.obstack;
var dopath_$6 = this.noteWhy || this.debugTrace;
var annotation_$7 = this.annotation;
var marked_$8 = annotation_$7.marked;
var why_$9 = annotation_$7.why;
var size_$a = annotation_$7.size;
var total_$b = annotation_$7.total;
var leaked_$c = annotation_$7.leaked;
while (loopCount_$3++ < steps_$0 && (loopElapsed_$4 = new Date().getTime() - loopStart_$2) < milliseconds_$1) {
while (os_$5.length > 0 && os_$5[0].length == 0) {
os_$5.shift();
if (os_$5.length == 0) {
LzTimeKernel.clearInterval(this.backgroundTask);
this.loops++;
this.loopCount += loopCount_$3;
this.loopElapsed += loopElapsed_$4;
this.debug("%d loops @ %0.0d iterations, %0.2d milliseconds", this.loops, this.loopCount / this.loops, this.loopElapsed / this.loops);
this.format(" \u2026 done!\n");
return true
}};
var ose_$d = os_$5[0];
var o_$e = ose_$d.pop();
var oo_$f = o_$e;
var name_$g = ose_$d.pop();
var wasMarked_$h = this.isMarked(o_$e);
var wasLeaked_$i = wasMarked_$h === null;
if (wasMarked_$h) {
continue
};
if (dopath_$6) {
var path_$j = ose_$d.path.concat(name_$g);
try {
o_$e[why_$9] = path_$j.join("\x01")
}
catch (e_$k) {}};
if (o_$e !== oo_$f) {
Debug.debug("Annotating %s[%s] (%#w) caused allocation of %#w", path_$j, name_$g, oo_$f, o_$e)
};
if (this.findLeaks && wasLeaked_$i) {
this.leaks.push(o_$e)
};
if (!this.mark(o_$e)) {
continue
};
var obSize_$l = 0;
var queuedSlots_$m = [];
if (dopath_$6) {
queuedSlots_$m.path = path_$j;
var ancestors_$n = ose_$d.ancestors;
queuedSlots_$m.ancestors = ancestors_$n.concat();
queuedSlots_$m.ancestors.push(o_$e)
};
var ownProperties_$o = [];
try {
this.objectOwnProperties(o_$e, ownProperties_$o, ownProperties_$o, Infinity, true)
}
catch (e_$k) {};
for (var i_$p = ownProperties_$o.length - 1;i_$p >= 0;i_$p--) {
var p_$q = ownProperties_$o[i_$p];
obSize_$l += 2;
try {
var v_$r = o_$e[p_$q];
if (typeof v_$r == "string") {
obSize_$l += Math.ceil(v_$r.length / 4)
};
if (!(v_$r && typeof v_$r == "object" && v_$r["hasOwnProperty"] instanceof Function)) {
if (this.debugTrace) {};
v_$r = null
} else if (v_$r !== o_$e[p_$q]) {
if (this.debugTrace) {};
v_$r = null
} else {
v_$r[size_$a] = 0;
if (v_$r !== o_$e[p_$q]) {
if (this.debugTrace) {
Debug.debug("Mutating %s[%s] (%#w) caused allocation of %#w", o_$e, p_$q, v_$r, o_$e[p_$q])
};
v_$r = null
}}}
catch (err_$s) {
if (this.debugTrace) {
Debug.debug("Mutating %s[%s] (%#w) caused %#w", o_$e, p_$q, v_$r, err_$s)
};
v_$r = null
};
if (v_$r && !this.isMarked(v_$r)) {
queuedSlots_$m.push(p_$q, v_$r)
}};
o_$e[size_$a] = obSize_$l;
if (dopath_$6) {
o_$e[total_$b] = obSize_$l;
if (wasLeaked_$i) {
o_$e[leaked_$c] = obSize_$l
};
var al_$t = ancestors_$n.length;
for (var i_$p = al_$t - 1;i_$p >= 0;i_$p--) {
var ai_$u = ancestors_$n[i_$p];
if (ai_$u) {
ai_$u[total_$b] += obSize_$l;
if (wasLeaked_$i) {
if (ai_$u.hasOwnProperty(leaked_$c)) {
if (this.debugTrace) {
if (o_$e[why_$9].indexOf(ai_$u[why_$9]) != 0) {
console.log("%w(%s) +> %w(%s)\n", o_$e, o_$e[why_$9], ai_$u, ai_$u[why_$9]);
console.log("%w[%d]\n", ancestors_$n, i_$p)
}};
ai_$u[leaked_$c] += obSize_$l
}}}}};
if (queuedSlots_$m.length) {
os_$5.push(queuedSlots_$m)
}};
this.loops++;
this.loopCount += loopCount_$3;
this.loopElapsed += loopElapsed_$4;
return false
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#150/19";
return $lzsc$temp
})();
Debug.initTrace = (function () {
var $lzsc$temp = function (findLeaks_$0, noteWhy_$1) {
switch (arguments.length) {
case 0:
findLeaks_$0 = false;;case 1:
noteWhy_$1 = false
};
this.markGeneration++;
this.loops = this.loopCount = this.loopElapsed = 0;
this.findLeaks = findLeaks_$0;
if (findLeaks_$0) {
this.leaks = []
} else {
delete this.leaks
};
this.noteWhy = noteWhy_$1;
for (var t_$2 = this;t_$2 && t_$2 !== Object.prototype;) {
this.mark(t_$2);
t_$2 = ("__proto__" in t_$2) && typeof t_$2.__proto__ == "object" ? t_$2.__proto__ : (("constructor" in t_$2) && typeof t_$2.constructor.prototype == "object" ? t_$2.constructor.prototype : null)
};
if ("frameElement" in global) {
this.mark(global.frameElement)
};
if ("_" in Debug.environment) {
this.mark(Debug.environment._)
};
if ("__" in Debug.environment) {
this.mark(Debug.environment.__)
};
if ("___" in Debug.environment) {
this.mark(Debug.environment.___)
};
if ("console" in global) {
this.mark(global.console)
};
var osel_$3 = ["global", global];
osel_$3.path = [];
osel_$3.ancestors = [];
this.obstack[0] = osel_$3;
this.backgroundTask = LzTimeKernel.setInterval((function () {
var $lzsc$temp = function () {
Debug.traceStep()
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#439/50";
return $lzsc$temp
})(), 1400)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#392/19";
return $lzsc$temp
})();
Debug.markObjects = (function () {
var $lzsc$temp = function () {
Debug.warn("Memory tracing is for experimental use only in this runtime.");
Debug.format("Marking objects \u2026 ");
LzTimeKernel.setTimeout((function () {
var $lzsc$temp = function () {
Debug.initTrace()
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#453/27";
return $lzsc$temp
})(), 10)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#446/21";
return $lzsc$temp
})();
Debug.findNewObjects = (function () {
var $lzsc$temp = function () {
if (this.markGeneration > 0) {
Debug.warn("Memory tracing is for experimental use only in this runtime.");
Debug.format("Finding new objects \u2026 ");
LzTimeKernel.setTimeout((function () {
var $lzsc$temp = function () {
Debug.initTrace(true, true)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#468/29";
return $lzsc$temp
})(), 10)
} else {
Debug.error("Call %w first", Debug.markObjects)
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#460/24";
return $lzsc$temp
})();
Class.make("__LzLeak", ["obj", void 0, "path", "", "parent", void 0, "property", "", "leaked", 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (o_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var annotations_$1 = Debug.annotation;
var why_$2 = annotations_$1.why;
var leaked_$3 = annotations_$1.leaked;
this.obj = o_$0;
if (o_$0 && (why_$2 in o_$0) && (leaked_$3 in o_$0)) {
var path_$4 = o_$0[why_$2].split("\x01");
this.property = path_$4.pop();
this.path = path_$4.join(".");
try {
var p_$5 = eval(path_$4[0]);
var pl_$6 = path_$4.length;
for (var i_$7 = 1;i_$7 < pl_$6;i_$7++) {
p_$5 = p_$5[path_$4[i_$7]]
};
this.parent = p_$5
}
catch (e_$8) {};
this.leaked = Number(o_$0[leaked_$3])
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
if (this.obj) {
return Debug.formatToString("%=s.%s: (\xA3%d) %0.48#w", this["parent"], this.path, this.property, this.leaked, this["obj"])
} else {
return "" + this.obj
}};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()]);
Class.make("__LzLeaks", ["length", 0, "sort", Array.prototype.sort, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var l_$0 = Debug.leaks;
var ll_$1 = l_$0.length;
var annotations_$2 = Debug.annotation;
var why = annotations_$2.why;
var size_$3 = annotations_$2.size;
var leaked_$4 = "_dbg_check";
l_$0.sort((function () {
var $lzsc$temp = function (a_$0, b_$1) {
try {
var an_$2 = a_$0[why];
var bn_$3 = b_$1[why];
return (an_$2 > bn_$3) - (an_$2 < bn_$3)
}
catch (e_$4) {
return -1
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#541/12";
return $lzsc$temp
})());
this.length = 0;
for (var i_$5 = 0;i_$5 < ll_$1;i_$5 = j_$6) {
var j_$6 = i_$5 + 1;
try {
var p_$7 = l_$0[i_$5];
p_$7[leaked_$4] = p_$7[size_$3];
var pn_$8 = p_$7[why];
if (typeof pn_$8 != "undefined") {
while (j_$6 < ll_$1) {
var c_$9 = l_$0[j_$6];
var cn_$a = c_$9[why];
if (typeof cn_$a != "undefined") {
if (cn_$a.indexOf(pn_$8) == 0) {
if (c_$9 !== p_$7) {
p_$7[leaked_$4] += c_$9[size_$3]
} else {
if (Debug.debugTrace) {
console.log("%s is %s\n", pn_$8, cn_$a)
}};
j_$6++;
continue
}};
break
}};
this[this.length++] = new __LzLeak(p_$7)
}
catch (e_$b) {
j_$6++
}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "_dbg_name", (function () {
var $lzsc$temp = function () {
var leakage_$0 = 0;
var n_$1 = this.length;
for (var i_$2 = n_$1 - 1;i_$2 >= 0;i_$2--) {
var s_$3 = this[i_$2]["leaked"];
if (!isNaN(s_$3)) {
leakage_$0 += s_$3
}};
return Debug.formatToString("%d smoots [%d objects @ ~%0.0d smoots each]", leakage_$0, n_$1, leakage_$0 / n_$1)
};
$lzsc$temp["displayName"] = "_dbg_name";
return $lzsc$temp
})()]);
Debug.whyAlive = (function () {
var $lzsc$temp = function (top_$0) {
switch (arguments.length) {
case 0:
top_$0 = 10
};
Debug.warn("Memory tracing is for experimental use only in this runtime.");
if (this["leaks"]) {
var l_$1 = new __LzLeaks();
var ll_$2 = l_$1.length;
l_$1.sort((function () {
var $lzsc$temp = function (a_$0, b_$1) {
var al_$2 = a_$0.leaked;
var bl_$3 = b_$1.leaked;
return (al_$2 < bl_$3) - (al_$2 > bl_$3)
};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#614/12";
return $lzsc$temp
})());
Debug.format("%w:\n", l_$1);
if (top_$0 > ll_$2) {
top_$0 = ll_$2
};
for (var i_$3 = 0;i_$3 < top_$0;i_$3++) {
Debug.format("%w\n", l_$1[i_$3].toString())
};
if (top_$0 < ll_$2) {
var rest_$4 = 0;
var n_$5 = ll_$2 - i_$3;
for (;i_$3 < ll_$2;i_$3++) {
var lil_$6 = l_$1[i_$3].leaked;
if (!isNaN(lil_$6)) {
rest_$4 += lil_$6
}};
Debug.format("%=s [%d more @ ~%0.0d smoots each]", l_$1, "\u2026", n_$5, rest_$4 / n_$5)
};
return l_$1
} else {
Debug.error("Call %w first", Debug.findNewObjects)
}};
$lzsc$temp["displayName"] = "debugger/LzMemory.lzs#604/18";
return $lzsc$temp
})();
Class.make("LzValueExpr");
Class.make("LzAttributeDescriptor", ["attribute", void 0, "type", void 0, "value", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, value_$2) {
switch (arguments.length) {
case 2:
value_$2 = null
};
this.attribute = attribute_$0;
this.type = type_$1;
this.value = value_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzValueExpr);
Class.make("LzInitExpr", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, value_$2) {
switch (arguments.length) {
case 2:
value_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, attribute_$0, type_$1, value_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzAttributeDescriptor);
Class.make("LzOnceExpr", ["methodName", void 0, "_dbg_name", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, initMethod_$2, description_$3) {
switch (arguments.length) {
case 3:
description_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, attribute_$0, type_$1);
this.methodName = initMethod_$2;
this._dbg_name = description_$3
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzInitExpr);
Class.make("LzConstraintExpr", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, constraintMethod_$2, description_$3) {
switch (arguments.length) {
case 3:
description_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, attribute_$0, type_$1, constraintMethod_$2, description_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzOnceExpr);
Class.make("LzStyleConstraintExpr", ["property", void 0, "fallback", void 0, "warn", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, property_$2, fallback_$3, warn_$4) {
switch (arguments.length) {
case 3:
fallback_$3 = void 0;;case 4:
warn_$4 = true
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, attribute_$0, type_$1, null);
this.property = property_$2;
this.type = type_$1;
this.fallback = fallback_$3;
this.warn = warn_$4;
this._dbg_name = attribute_$0 + "\"=$style{'" + property_$2 + "'}\""
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzConstraintExpr);
Class.make("LzAlwaysExpr", ["dependenciesName", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (attribute_$0, type_$1, constraintMethod_$2, dependenciesMethod_$3, description_$4) {
switch (arguments.length) {
case 4:
description_$4 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, attribute_$0, type_$1, constraintMethod_$2, description_$4);
this.dependenciesName = dependenciesMethod_$3
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzConstraintExpr);
Class.make("LzStyleExpr", ["_dbg_name", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzValueExpr);
Class.make("LzStyleAttr", ["sourceAttributeName", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (sourceAttributeName_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.sourceAttributeName = sourceAttributeName_$0;
this._dbg_name = "attr(" + this.sourceAttributeName + ")"
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzStyleExpr);
Class.make("LzStyleIdent", ["sourceValueID", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (sourceValueID_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.sourceValueID = sourceValueID_$0;
this._dbg_name = sourceValueID_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzStyleExpr);
function LzInheritedHash (parent_$0) {
if (parent_$0) {
for (var key_$1 in parent_$0) {
this[key_$1] = parent_$0[key_$1]
}}};
var lz;
(function () {
var $lzsc$temp = function () {
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(lz) : lz instanceof Object) {

} else if (!lz) {
lz = new LzInheritedHash()
} else {
Debug.error("Can't create `lz` namespace, already bound to %w", lz)
}};
$lzsc$temp["displayName"] = "core/LzDefs.lzs#225/3";
return $lzsc$temp
})()();
lz.Formatter = LzFormatter;
Class.make("LzDeclaredEventClass", ["actual", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (actual_$0) {
switch (arguments.length) {
case 0:
actual_$0 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.actual = actual_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ready", false, "sendEvent", (function () {
var $lzsc$temp = function (eventValue_$0) {
switch (arguments.length) {
case 0:
eventValue_$0 = null
}};
$lzsc$temp["displayName"] = "sendEvent";
return $lzsc$temp
})(), "clearDelegates", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})(), "removeDelegate", (function () {
var $lzsc$temp = function (d_$0) {
switch (arguments.length) {
case 0:
d_$0 = null
}};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "getDelegateCount", (function () {
var $lzsc$temp = function () {
return 0
};
$lzsc$temp["displayName"] = "getDelegateCount";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "LzDeclaredEvent"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "core/LzDeclaredEvent.lzs#21/1";
return $lzsc$temp
})()(LzDeclaredEventClass);
lz.DeclaredEventClass = LzDeclaredEventClass;
var LzDeclaredEvent = new LzDeclaredEventClass();
lz.colors = {aliceblue: 15792383, antiquewhite: 16444375, aqua: 65535, aquamarine: 8388564, azure: 15794175, beige: 16119260, bisque: 16770244, black: 0, blanchedalmond: 16772045, blue: 255, blueviolet: 9055202, brown: 10824234, burlywood: 14596231, cadetblue: 6266528, chartreuse: 8388352, chocolate: 13789470, coral: 16744272, cornflowerblue: 6591981, cornsilk: 16775388, crimson: 14423100, cyan: 65535, darkblue: 139, darkcyan: 35723, darkgoldenrod: 12092939, darkgray: 11119017, darkgreen: 25600, darkgrey: 11119017, darkkhaki: 12433259, darkmagenta: 9109643, darkolivegreen: 5597999, darkorange: 16747520, darkorchid: 10040012, darkred: 9109504, darksalmon: 15308410, darkseagreen: 9419919, darkslateblue: 4734347, darkslategray: 3100495, darkslategrey: 3100495, darkturquoise: 52945, darkviolet: 9699539, deeppink: 16716947, deepskyblue: 49151, dimgray: 6908265, dimgrey: 6908265, dodgerblue: 2003199, firebrick: 11674146, floralwhite: 16775920, forestgreen: 2263842, fuchsia: 16711935, gainsboro: 14474460, ghostwhite: 16316671, gold: 16766720, goldenrod: 14329120, gray: 8421504, green: 32768, greenyellow: 11403055, grey: 8421504, honeydew: 15794160, hotpink: 16738740, indianred: 13458524, indigo: 4915330, ivory: 16777200, khaki: 15787660, lavender: 15132410, lavenderblush: 16773365, lawngreen: 8190976, lemonchiffon: 16775885, lightblue: 11393254, lightcoral: 15761536, lightcyan: 14745599, lightgoldenrodyellow: 16448210, lightgray: 13882323, lightgreen: 9498256, lightgrey: 13882323, lightpink: 16758465, lightsalmon: 16752762, lightseagreen: 2142890, lightskyblue: 8900346, lightslategray: 7833753, lightslategrey: 7833753, lightsteelblue: 11584734, lightyellow: 16777184, lime: 65280, limegreen: 3329330, linen: 16445670, magenta: 16711935, maroon: 8388608, mediumaquamarine: 6737322, mediumblue: 205, mediumorchid: 12211667, mediumpurple: 9662683, mediumseagreen: 3978097, mediumslateblue: 8087790, mediumspringgreen: 64154, mediumturquoise: 4772300, mediumvioletred: 13047173, midnightblue: 1644912, mintcream: 16121850, mistyrose: 16770273, moccasin: 16770229, navajowhite: 16768685, navy: 128, oldlace: 16643558, olive: 8421376, olivedrab: 7048739, orange: 16753920, orangered: 16729344, orchid: 14315734, palegoldenrod: 15657130, palegreen: 10025880, paleturquoise: 11529966, palevioletred: 14381203, papayawhip: 16773077, peachpuff: 16767673, peru: 13468991, pink: 16761035, plum: 14524637, powderblue: 11591910, purple: 8388736, red: 16711680, rosybrown: 12357519, royalblue: 4286945, saddlebrown: 9127187, salmon: 16416882, sandybrown: 16032864, seagreen: 3050327, seashell: 16774638, sienna: 10506797, silver: 12632256, skyblue: 8900331, slateblue: 6970061, slategray: 7372944, slategrey: 7372944, snow: 16775930, springgreen: 65407, steelblue: 4620980, tan: 13808780, teal: 32896, thistle: 14204888, tomato: 16737095, turquoise: 4251856, violet: 15631086, wheat: 16113331, white: 16777215, whitesmoke: 16119285, yellow: 16776960, yellowgreen: 10145074, transparent: null};
Class.make("LzCache", ["size", void 0, "slots", void 0, "destroyable", void 0, "capacity", void 0, "curslot", void 0, "data", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (size_$0, slots_$1, destroyable_$2) {
switch (arguments.length) {
case 0:
size_$0 = 16;;case 1:
slots_$1 = 2;;case 2:
destroyable_$2 = true
};
this.size = size_$0;
this.slots = slots_$1;
this.destroyable = destroyable_$2;
this.clear()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {
this.curslot = 0;
this.capacity = 0;
var sl_$0 = this.slots;
if (!this.data) this.data = new Array(sl_$0);
var d_$1 = this.data;
for (var i_$2 = 0;i_$2 < sl_$0;++i_$2) {
if (this.destroyable) {
var dobj_$3 = d_$1[i_$2];
for (var k_$4 in dobj_$3) {
dobj_$3[k_$4].destroy()
}};
d_$1[i_$2] = {}}};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "ensureSlot", (function () {
var $lzsc$temp = function () {
if (++this.capacity > this.size) {
var nexts_$0 = (this.curslot + 1) % this.slots;
var d_$1 = this.data;
if (this.destroyable) {
var dobj_$2 = d_$1[nexts_$0];
for (var k_$3 in dobj_$2) {
dobj_$2[k_$3].destroy()
}};
d_$1[nexts_$0] = {};
this.curslot = nexts_$0;
this.capacity = 1
}};
$lzsc$temp["displayName"] = "ensureSlot";
return $lzsc$temp
})(), "put", (function () {
var $lzsc$temp = function (key_$0, val_$1) {
var old_$2 = this.get(key_$0);
if (old_$2 === void 0) {
this.ensureSlot()
};
this.data[this.curslot][key_$0] = val_$1;
return old_$2
};
$lzsc$temp["displayName"] = "put";
return $lzsc$temp
})(), "get", (function () {
var $lzsc$temp = function (key_$0) {
var sl_$1 = this.slots;
var cs_$2 = this.curslot;
var d_$3 = this.data;
for (var i_$4 = 0;i_$4 < sl_$1;++i_$4) {
var idx_$5 = (cs_$2 + i_$4) % sl_$1;
var val_$6 = d_$3[idx_$5][key_$0];
if (val_$6 !== void 0) {
if (idx_$5 != cs_$2) {
delete d_$3[idx_$5][key_$0];
this.ensureSlot();
d_$3[this.curslot][key_$0] = val_$6
};
return val_$6
}};
return void 0
};
$lzsc$temp["displayName"] = "get";
return $lzsc$temp
})()]);
Class.make("LzEventable", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__LZdeleted", false, "_events", null, "__delegates", null, "ondestroy", LzDeclaredEvent, "destroy", (function () {
var $lzsc$temp = function () {
if (this.ondestroy.ready) this.ondestroy.sendEvent(this);
this.__LZdeleted = true;
this.__LZdelegatesQueue = null;
this.__LZdeferDelegates = false;
if (this._events != null) {
for (var i_$0 = this._events.length - 1;i_$0 >= 0;i_$0--) {
this._events[i_$0].clearDelegates()
};
this._events = null
};
if (this.__delegates != null) this.removeDelegates()
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "removeDelegates", (function () {
var $lzsc$temp = function () {
if (this.__delegates != null) {
for (var i_$0 = this.__delegates.length - 1;i_$0 >= 0;i_$0--) {
var del_$1 = this.__delegates[i_$0];
if (del_$1.__LZdeleted != true) {
del_$1.destroy()
}};
this.__delegates = null
}};
$lzsc$temp["displayName"] = "removeDelegates";
return $lzsc$temp
})(), "__LZdeferDelegates", false, "__LZdelegatesQueue", null, "childOf", (function () {
var $lzsc$temp = function (node_$0, ignore_$1) {
switch (arguments.length) {
case 1:
ignore_$1 = null
};
return false
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "customSetters", {}, "__invokeCustomSetter", (function () {
var $lzsc$temp = function (prop_$0, val_$1) {
return false
};
$lzsc$temp["displayName"] = "__invokeCustomSetter";
return $lzsc$temp
})(), "setAttribute", (function () {
var $lzsc$temp = function (prop_$0, val_$1, ifchanged_$2) {
switch (arguments.length) {
case 2:
ifchanged_$2 = null
};
if (ifchanged_$2 !== null) {
Debug.info("%w.setAttribute(%w, %w, %w):  The third parameter (ifchanged) is deprecated.  Use `if (this.%2$s !== %3$w) this.setAttribute(%2$w, %3$w)` instead", this, prop_$0, val_$1, ifchanged_$2)
};
if (this.__LZdeleted) {
return
};
if (this.customSetters[prop_$0]) {
if (this.__invokeCustomSetter(prop_$0, val_$1) == true) {
return
}};
var s_$3 = "$lzc$set_" + prop_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[s_$3]) : this[s_$3] instanceof Function) {
this[s_$3](val_$1)
} else {
this[prop_$0] = val_$1;
var evt_$4 = this["on" + prop_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa(evt_$4) : evt_$4 instanceof LzEvent) {
if (evt_$4.ready) evt_$4.sendEvent(val_$1)
}}};
$lzsc$temp["displayName"] = "setAttribute";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzEventable.prototype[Debug.FUNCTION_FILENAME] = null;
LzEventable.prototype[Debug.FUNCTION_LINENO] = null;
LzEventable.prototype._dbg_typename = null;
LzEventable.prototype[Debug.FUNCTION_NAME] = null
}}};
$lzsc$temp["displayName"] = "core/LzEventable.lzs#27/1";
return $lzsc$temp
})()(LzEventable);
lz.Eventable = LzEventable;
Class.make("LzStyleAttrBinder", ["target", void 0, "dest", void 0, "source", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (target_$0, dest_$1, source_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.target = target_$0;
this.dest = dest_$1;
this.source = source_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "bind", (function () {
var $lzsc$temp = function (ignore_$0) {
switch (arguments.length) {
case 0:
ignore_$0 = null
};
var target_$1 = this.target;
var dest_$2 = this.dest;
var oldvalue_$3 = target_$1[dest_$2];
var newvalue_$4 = target_$1[this.source];
if (newvalue_$4 !== oldvalue_$3 || !target_$1.inited) {
{
if (!target_$1.__LZdeleted) {
var $lzsc$fjcgmu = "$lzc$set_" + dest_$2;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(target_$1[$lzsc$fjcgmu]) : target_$1[$lzsc$fjcgmu] instanceof Function) {
target_$1[$lzsc$fjcgmu](newvalue_$4)
} else {
target_$1[dest_$2] = newvalue_$4;
var $lzsc$e5xbzw = target_$1["on" + dest_$2];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$e5xbzw) : $lzsc$e5xbzw instanceof LzEvent) {
if ($lzsc$e5xbzw.ready) {
$lzsc$e5xbzw.sendEvent(newvalue_$4)
}}}}}}};
$lzsc$temp["displayName"] = "bind";
return $lzsc$temp
})()], LzEventable);
Class.make("$lz$class_TypeService", ["PresentationTypes", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.PresentationTypes = {}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "acceptTypeValue", (function () {
var $lzsc$temp = function (type_$0, value_$1, node_$2, attribute_$3) {
var presentationtype_$4 = type_$0 ? this.PresentationTypes[type_$0] : null;
if (value_$1 != null) {
if (presentationtype_$4 != null) {
return presentationtype_$4.accept(value_$1, node_$2, attribute_$3)
} else {
Debug.warn("No <type> named %w", type_$0)
}};
return value_$1
};
$lzsc$temp["displayName"] = "acceptTypeValue";
return $lzsc$temp
})(), "presentTypeValue", (function () {
var $lzsc$temp = function (type_$0, value_$1, node_$2, attribute_$3) {
var presentationtype_$4 = this.PresentationTypes[type_$0];
if (presentationtype_$4 != null) {
return presentationtype_$4.present(value_$1, node_$2, attribute_$3)
} else {
Debug.warn("No <type> named %w", type_$0)
};
return value_$1
};
$lzsc$temp["displayName"] = "presentTypeValue";
return $lzsc$temp
})(), "addType", (function () {
var $lzsc$temp = function (type_$0, presentationType_$1) {
if (this.PresentationTypes[type_$0]) {
Debug.error("Redefining %s from %w to %w", type_$0, this.PresentationTypes[type_$0], presentationType_$1)
};
this.PresentationTypes[type_$0] = presentationType_$1
};
$lzsc$temp["displayName"] = "addType";
return $lzsc$temp
})(), "addTypeAlias", (function () {
var $lzsc$temp = function (alias_$0, type_$1) {
var aliasType_$2 = this.PresentationTypes[type_$1];
if (!aliasType_$2) {
Debug.error("No <type> named %w", type_$1);
return
};
if (this.PresentationTypes[alias_$0]) {
Debug.error("Redefining %s from %w to %w", alias_$0, this.PresentationTypes[alias_$0], this.PresentationTypes[alias_$0])
};
this.PresentationTypes[alias_$0] = aliasType_$2
};
$lzsc$temp["displayName"] = "addTypeAlias";
return $lzsc$temp
})()], null, ["Type", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
$lz$class_TypeService.Type = new $lz$class_TypeService()
}}};
$lzsc$temp["displayName"] = "core/PresentationTypes.lzs#16/1";
return $lzsc$temp
})()($lz$class_TypeService);
lz.TypeService = $lz$class_TypeService;
lz.Type = lz.TypeService.Type;
Class.make("$lz$class_PresentationType", ["_dbg_typename", "type", "_dbg_name", (function () {
var $lzsc$temp = function () {
return this.constructor.lzxtype
};
$lzsc$temp["displayName"] = "core/PresentationTypes.lzs#157/19";
return $lzsc$temp
})(), "accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
Debug.error("%w.accept must be defined", this)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
return String(value_$0)
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()], null, ["nullValue", null]);
Class.make("$lz$class_StringPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
return String(value_$0)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})()], $lz$class_PresentationType, ["lzxtype", "string", "nullValue", ""]);
lz.Type.addType("string", new $lz$class_StringPresentationType());
lz.Type.addTypeAlias("html", "string");
Class.make("$lz$class_TextPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
var escChars_$3 = $lz$class_TextPresentationType.xmlEscapes;
var result_$4 = "";
for (var i_$5 = 0, l_$6 = value_$0.length;i_$5 < l_$6;i_$5++) {
var c_$7 = value_$0.charAt(i_$5);
result_$4 += escChars_$3[c_$7] || c_$7
};
return result_$4
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
var result_$3 = "";
for (var i_$4 = 0, l_$5 = value_$0.length;i_$4 < l_$5;i_$4++) {
var c_$6 = value_$0.charAt(i_$4);
if (c_$6 == "&") {
var e_$7 = value_$0.indexOf(";", i_$4);
if (e_$7 > i_$4) {
var p_$8 = value_$0.substring(i_$4, e_$7);
switch (p_$8) {
case "amp":
break;;case "lt":
c_$6 = "<";break;;case "gt":
c_$6 = ">";break;;case "quot":
c_$6 = '"';break;;case "apos":
c_$6 = "'";break;;default:
c_$6 = "&" + p_$8 + ";"
};
i_$4 = e_$7
}};
result_$3 += c_$6
};
return result_$3
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()], $lz$class_PresentationType, ["lzxtype", "text", "nullValue", "", "xmlEscapes", {"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&apos;"}]);
lz.Type.addTypeAlias("text", "string");
Class.make("$lz$class_BooleanPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
switch (value_$0.toLowerCase()) {
case "":
case "0":
case "false":
return false;;default:
return true
}};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", false, "lzxtype", "boolean"]);
lz.Type.addType("boolean", new $lz$class_BooleanPresentationType());
lz.Type.addTypeAlias("inheritableBoolean", "boolean");
Class.make("$lz$class_NumberPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
return Number(value_$0)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", 0, "lzxtype", "number"]);
lz.Type.addType("number", new $lz$class_NumberPresentationType());
lz.Type.addTypeAlias("numberExpression", "number");
Class.make("$lz$class_ColorPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
if (value_$0 == -1) {
return null
};
return LzColorUtils.hextoint(value_$0)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
var ctab_$3 = lz.colors;
for (var name_$4 in ctab_$3) {
if (ctab_$3[name_$4] === value_$0) {
return name_$4
}};
return LzColorUtils.inttohex(value_$0)
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", 0, "lzxtype", "color"]);
lz.Type.addType("color", new $lz$class_ColorPresentationType());
Class.make("$lz$class_ExpressionPresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
switch (value_$0) {
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
if (!isNaN(value_$0)) {
return Number(value_$0)
};
return String(value_$0)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", null, "lzxtype", "expression"]);
lz.Type.addType("expression", new $lz$class_ExpressionPresentationType());
Class.make("$lz$class_SizePresentationType", ["accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
if (value_$0 == "null") {
return null
};
return Number(value_$0)
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", null, "lzxtype", "size"]);
lz.Type.addType("size", new $lz$class_SizePresentationType());
Class.make("$lz$class_CSSDeclarationPresentationType", ["PropRE", new RegExp("^\\s*(\\S*)\\s*:\\s*(\\S*)\\s*$"), "HyphenRE", new RegExp("-(\\w)", "g"), "CapitalRE", new RegExp("[:upper:]", "g"), "accept", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
var props_$3 = value_$0.split(",");
var result_$4 = {};
for (var i_$5 = 0, len_$6 = props_$3.length;i_$5 < len_$6;i_$5++) {
var prop_$7 = props_$3[i_$5];
var parts_$8 = prop_$7.match(this.PropRE);
if (parts_$8.length = 3) {
var attr_$9 = parts_$8[1].replace(this.HyphenRE, (function () {
var $lzsc$temp = function (m_$0, p_$1) {
return p_$1[1].toUpperCase()
};
$lzsc$temp["displayName"] = "core/PresentationTypes.lzs#417/52";
return $lzsc$temp
})());
result_$4[attr_$9] = parts_$8[2]
}};
return result_$4
};
$lzsc$temp["displayName"] = "accept";
return $lzsc$temp
})(), "present", (function () {
var $lzsc$temp = function (value_$0, node_$1, attribute_$2) {
var props_$3 = [];
for (var attr_$4 in value_$0) {
var prop_$5 = attr_$4.replace(this.CapitalRE, (function () {
var $lzsc$temp = function (m_$0, p_$1) {
return "-" + p_$1[1].toLowerCase()
};
$lzsc$temp["displayName"] = "core/PresentationTypes.lzs#426/47";
return $lzsc$temp
})());
props_$3.push(prop_$5 + ": " + value_$0[attr_$4])
};
return props_$3.join(", ")
};
$lzsc$temp["displayName"] = "present";
return $lzsc$temp
})()], $lz$class_PresentationType, ["nullValue", {}, "lzxtype", "css"]);
lz.Type.addType("css", new $lz$class_CSSDeclarationPresentationType());
Class.make("LzNode", ["__LZisnew", false, "__LZdeferredcarr", null, "classChildren", null, "animators", null, "__animatedAttributes", void 0, "_instanceAttrs", null, "_instanceChildren", null, "__LzValueExprs", null, "__LZhasConstraint", (function () {
var $lzsc$temp = function (attr_$0) {
return (attr_$0 in this.__LzValueExprs) && !(LzStyleConstraintExpr["$lzsc$isa"] ? LzStyleConstraintExpr.$lzsc$isa(this.__LzValueExprs[attr_$0]) : this.__LzValueExprs[attr_$0] instanceof LzStyleConstraintExpr)
};
$lzsc$temp["displayName"] = "__LZhasConstraint";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.__LZUID = "__U" + ++LzNode.__UIDs;
this.__LZdeferDelegates = true;
if (attrs_$1) {
if (attrs_$1["$lzc$bind_id"]) {
this.$lzc$bind_id = attrs_$1.$lzc$bind_id
};
if (attrs_$1["$lzc$bind_name"]) {
this.$lzc$bind_name = attrs_$1.$lzc$bind_name
}};
var bindid_$4 = this.$lzc$bind_id;
if (bindid_$4) {
bindid_$4.call(null, this)
};
var bindname_$5 = this.$lzc$bind_name;
if (bindname_$5) {
bindname_$5.call(null, this)
};
this._instanceAttrs = attrs_$1;
this._instanceChildren = children_$2;
var iargs_$6 = new LzInheritedHash(this["constructor"].attributes);
if (!(LzState["$lzsc$isa"] ? LzState.$lzsc$isa(this) : this instanceof LzState)) {
for (var key_$7 in iargs_$6) {
var expr_$8 = iargs_$6[key_$7];
if (expr_$8 && (LzAttributeDescriptor["$lzsc$isa"] ? LzAttributeDescriptor.$lzsc$isa(expr_$8) : expr_$8 instanceof LzAttributeDescriptor)) {
var val_$9 = expr_$8.value
} else {
var val_$9 = expr_$8
};
if (!(expr_$8 && (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(expr_$8) : expr_$8 instanceof LzInitExpr))) {
var setr_$a = "$lzc$set_" + key_$7;
if (!this[setr_$a]) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$9) : val_$9 instanceof Function) {
this.addProperty(key_$7, val_$9)
} else if (val_$9 !== void 0) {
this[key_$7] = val_$9
};
delete iargs_$6[key_$7];
continue
}};
if (this[key_$7] === void 0) {
this[key_$7] = null
}}};
if (attrs_$1) {
LzNode.mergeAttributes(attrs_$1, iargs_$6)
};
if (bindid_$4) {
iargs_$6.$lzc$bind_id = LzNode._ignoreAttribute
};
if (bindname_$5) {
iargs_$6.$lzc$bind_name = LzNode._ignoreAttribute
};
this.__LZisnew = !instcall_$3;
var classChildren_$b = this["constructor"]["children"];
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(classChildren_$b) : classChildren_$b instanceof Array) {
children_$2 = LzNode.mergeChildren(children_$2, classChildren_$b)
};
if (iargs_$6["datapath"] != null) {
delete iargs_$6["$datapath"]
};
var cargs_$c = this.__LzValueExprs = {};
for (var key_$7 in iargs_$6) {
var val_$9 = iargs_$6[key_$7];
if (LzValueExpr["$lzsc$isa"] ? LzValueExpr.$lzsc$isa(val_$9) : val_$9 instanceof LzValueExpr) {
cargs_$c[key_$7] = val_$9;
delete iargs_$6[key_$7]
}};
try {
this.construct(parent_$0, iargs_$6)
}
catch (e_$d) {
if (e_$d === LzNode.__LzEarlyAbort) {
return
} else {
throw e_$d
}};
for (var key_$7 in cargs_$c) {
iargs_$6[key_$7] = cargs_$c[key_$7]
};
this.__LzValueExprs = null;
this.__LZapplyArgs(iargs_$6, true);
if (this.__LZdeleted) {
return
};
this.__LZdeferDelegates = false;
var evq_$e = this.__LZdelegatesQueue;
if (evq_$e) {
LzDelegate.__LZdrainDelegatesQueue(evq_$e);
this.__LZdelegatesQueue = null
};
if (this.onconstruct.ready) this.onconstruct.sendEvent(this);
if (children_$2 && children_$2.length) {
this.createChildren(children_$2)
} else {
this.__LZinstantiationDone()
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "oninit", LzDeclaredEvent, "onconstruct", LzDeclaredEvent, "ondata", LzDeclaredEvent, "clonenumber", null, "onclonenumber", LzDeclaredEvent, "__LZinstantiated", false, "__LZpreventSubInit", null, "__LZresolveDict", null, "__LZsourceLocation", null, "__LZUID", void 0, "__LZPropertyCache", null, "__LZCSSProp", null, "__LZCSSType", null, "__LZCSSFallback", null, "__LZRuleCache", null, "__LZconstraintdelegates", null, "isinited", false, "inited", false, "oninited", LzDeclaredEvent, "subnodes", null, "datapath", null, "$lzc$set_datapath", (function () {
var $lzsc$temp = function (dp_$0) {
if (null != this.datapath && dp_$0 !== LzNode._ignoreAttribute) {
this.datapath.setXPath(dp_$0)
} else {
new LzDatapath(this, {xpath: dp_$0})
}};
$lzsc$temp["displayName"] = "$lzc$set_datapath";
return $lzsc$temp
})(), "initstage", null, "$isstate", false, "parent", void 0, "cloneManager", null, "name", null, "$lzc$bind_name", null, "id", null, "$lzc$set_id", -1, "$lzc$bind_id", null, "defaultplacement", null, "placement", null, "$lzc$set_placement", -1, "$cfn", 0, "immediateparent", null, "dependencies", null, "classroot", void 0, "nodeLevel", 0, "lookupSourceLocator", (function () {
var $lzsc$temp = function (sourceLocator_$0) {
return LzNode.sourceLocatorTable[sourceLocator_$0]
};
$lzsc$temp["displayName"] = "lookupSourceLocator";
return $lzsc$temp
})(), "styleclass", "", "onstyleclass", LzDeclaredEvent, "__LZCSSStyleclass", null, "$lzc$set_styleclass", (function () {
var $lzsc$temp = function (classes_$0) {
this.styleclass = classes_$0;
if (classes_$0.indexOf(" ") >= 0) {
this.__LZCSSStyleclass = " " + classes_$0 + " "
} else {
this.__LZCSSStyleclass = null
};
if (this.onstyleclass.ready) {
this.onstyleclass.sendEvent(classes_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_styleclass";
return $lzsc$temp
})(), "__LZCSSDependencies", null, "__applyCSSConstraints", (function () {
var $lzsc$temp = function () {
var dependencies_$0 = this.__LZCSSDependencies;
if (dependencies_$0) {
this.__LZCSSDependencies = null;
var cdel_$1 = this.__applyCSSConstraintDel;
if (cdel_$1) {
cdel_$1.unregisterAll()
} else {
if (!this.__LZconstraintdelegates) {
this.__LZconstraintdelegates = []
};
cdel_$1 = this.__applyCSSConstraintDel = new LzDelegate(this, "__reapplyCSS");
this.__LZconstraintdelegates.push(cdel_$1)
};
for (var prop_$2 in dependencies_$0) {
var eventname_$3 = "on" + prop_$2;
var nodes_$4 = dependencies_$0[prop_$2];
for (var i_$5 = 0, l_$6 = nodes_$4.length;i_$5 < l_$6;i_$5++) {
var node_$7 = nodes_$4[i_$5];
cdel_$1.register(node_$7, eventname_$3)
}}}};
$lzsc$temp["displayName"] = "__applyCSSConstraints";
return $lzsc$temp
})(), "__reapplyCSS", (function () {
var $lzsc$temp = function (ignore_$0) {
switch (arguments.length) {
case 0:
ignore_$0 = null
};
var oldpropmap_$1 = LzCSSStyle.getPropertyCache(this);
this.__LZRuleCache = null;
this.__LZPropertyCache = null;
var newpropmap_$2 = LzCSSStyle.getPropertyCache(this);
var attrmap_$3 = this.__LZCSSProp;
var typemap_$4 = this.__LZCSSType;
var fallbackmap_$5 = this.__LZCSSFallback;
for (var attr_$6 in attrmap_$3) {
var prop_$7 = attrmap_$3[attr_$6];
if (oldpropmap_$1[prop_$7] != newpropmap_$2[prop_$7]) {
this.__LZstyleBindAttribute(attr_$6, prop_$7, typemap_$4[attr_$6], fallbackmap_$5[attr_$6])
}};
this.__applyCSSConstraints()
};
$lzsc$temp["displayName"] = "__reapplyCSS";
return $lzsc$temp
})(), "__LZstyleBindAttribute", (function () {
var $lzsc$temp = function (attr_$0, prop_$1, type_$2, fallback_$3, warn_$4) {
switch (arguments.length) {
case 3:
fallback_$3 = void 0;;case 4:
warn_$4 = true
};
var pc_$5 = this["__LZPropertyCache"] || LzCSSStyle.getPropertyCache(this);
if (!(prop_$1 in pc_$5) && fallback_$3 === void 0 && warn_$4 != false) {
Debug.warn("%w.%s: No applicable value for CSS property %w and there is no default.", this, attr_$0, prop_$1);
pc_$5[prop_$1] = void 0
};
if (!this.__LZCSSProp) {
this.__LZCSSProp = {}};
this.__LZCSSProp[attr_$0] = prop_$1;
if (!this.__LZCSSType) {
this.__LZCSSType = {}};
this.__LZCSSType[attr_$0] = type_$2;
if (!this.__LZCSSFallback) {
this.__LZCSSFallback = {}};
this.__LZCSSFallback[attr_$0] = fallback_$3;
var styleValue_$6 = pc_$5[prop_$1];
if (typeof styleValue_$6 == "string" && styleValue_$6.length > 2 && styleValue_$6.indexOf("0x") == 0 && !isNaN(styleValue_$6)) {
Debug.warn("%w.%s: Invalid value for CSS property %w: `%#w`.  Use: `#%06x`.", this, attr_$0, prop_$1, styleValue_$6, Number(styleValue_$6));
styleValue_$6 = Number(styleValue_$6)
};
if (LzStyleExpr["$lzsc$isa"] ? LzStyleExpr.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleExpr) {
if (LzStyleAttr["$lzsc$isa"] ? LzStyleAttr.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleAttr) {
var sa_$7 = styleValue_$6;
var source_$8 = sa_$7.sourceAttributeName;
var binder_$9 = new LzStyleAttrBinder(this, attr_$0, source_$8);
if (!this.__LZconstraintdelegates) {
this.__LZconstraintdelegates = []
};
this.__LZconstraintdelegates.push(new LzDelegate(binder_$9, "bind", this, "on" + source_$8));
binder_$9.bind()
} else if (LzStyleIdent["$lzsc$isa"] ? LzStyleIdent.$lzsc$isa(styleValue_$6) : styleValue_$6 instanceof LzStyleIdent) {
var si_$a = styleValue_$6;
this.acceptAttribute(attr_$0, type_$2, si_$a.sourceValueID)
} else {
Debug.error("Unknown style expression %w", styleValue_$6)
}} else if (styleValue_$6 !== void 0) {
if (this[attr_$0] !== styleValue_$6) {
{
if (!this.__LZdeleted) {
var $lzsc$3qildn = "$lzc$set_" + attr_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$3qildn]) : this[$lzsc$3qildn] instanceof Function) {
this[$lzsc$3qildn](styleValue_$6)
} else {
this[attr_$0] = styleValue_$6;
var $lzsc$ytke7s = this["on" + attr_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$ytke7s) : $lzsc$ytke7s instanceof LzEvent) {
if ($lzsc$ytke7s.ready) {
$lzsc$ytke7s.sendEvent(styleValue_$6)
}}}}}}} else if (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(fallback_$3) : fallback_$3 instanceof LzInitExpr) {
this.applyConstraintExpr(fallback_$3)
} else {
if (this[attr_$0] !== fallback_$3) {
{
if (!this.__LZdeleted) {
var $lzsc$oyqonh = "$lzc$set_" + attr_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$oyqonh]) : this[$lzsc$oyqonh] instanceof Function) {
this[$lzsc$oyqonh](fallback_$3)
} else {
this[attr_$0] = fallback_$3;
var $lzsc$m7v22k = this["on" + attr_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$m7v22k) : $lzsc$m7v22k instanceof LzEvent) {
if ($lzsc$m7v22k.ready) {
$lzsc$m7v22k.sendEvent(fallback_$3)
}}}}}}}};
$lzsc$temp["displayName"] = "__LZstyleBindAttribute";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
var lp_$2 = parent_$0;
this.parent = lp_$2;
if (lp_$2) {
var ip_$3 = lp_$2;
if (args_$1["ignoreplacement"] || this.ignoreplacement) {
this.placement = null
} else {
var thisplacement_$4 = args_$1["placement"] || lp_$2.defaultplacement;
while (thisplacement_$4 != null) {
if (ip_$3.determinePlacement == LzNode.prototype.determinePlacement) {
var pp_$5 = ip_$3.searchSubnodes("name", thisplacement_$4);
if (pp_$5 == null) pp_$5 = ip_$3
} else {
var pp_$5 = ip_$3.determinePlacement(this, thisplacement_$4, args_$1)
};
thisplacement_$4 = pp_$5 != ip_$3 ? pp_$5.defaultplacement : null;
ip_$3 = pp_$5
};
this.placement = thisplacement_$4
};
if (this.__LZdeleted) {
throw LzNode.__LzEarlyAbort
};
var ip_subnodes_$6 = ip_$3.subnodes;
if (ip_subnodes_$6 == null) {
ip_$3.subnodes = [this]
} else {
ip_subnodes_$6[ip_subnodes_$6.length] = this
};
var nl_$7 = ip_$3.nodeLevel;
this.nodeLevel = nl_$7 ? nl_$7 + 1 : 1;
this.immediateparent = ip_$3
} else {
this.nodeLevel = 1
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
return
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "__LZinstantiationDone", (function () {
var $lzsc$temp = function () {
this.__LZinstantiated = true;
if (!this.__LZdeleted && (!this.immediateparent || this.immediateparent.isinited || this.initstage == "early" || this.__LZisnew && lz.Instantiator.syncNew)) {
this.__LZcallInit()
}};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "__LZsetPreventInit", (function () {
var $lzsc$temp = function () {
this.__LZpreventSubInit = []
};
$lzsc$temp["displayName"] = "__LZsetPreventInit";
return $lzsc$temp
})(), "__LZclearPreventInit", (function () {
var $lzsc$temp = function () {
var lzp_$0 = this.__LZpreventSubInit;
this.__LZpreventSubInit = null;
var l_$1 = lzp_$0.length;
for (var i_$2 = 0;i_$2 < l_$1;i_$2++) {
lzp_$0[i_$2].__LZcallInit()
}};
$lzsc$temp["displayName"] = "__LZclearPreventInit";
return $lzsc$temp
})(), "__LZcallInit", (function () {
var $lzsc$temp = function (an_$0) {
switch (arguments.length) {
case 0:
an_$0 = null
};
if (this.parent && this.parent.__LZpreventSubInit) {
this.parent.__LZpreventSubInit.push(this);
return
};
this.isinited = true;
if (this.__LZresolveDict != null) this.__LZresolveReferences();
if (this.__LZdeleted) return;
var sl_$1 = this.subnodes;
if (sl_$1) {
for (var i_$2 = 0;i_$2 < sl_$1.length;) {
var s_$3 = sl_$1[i_$2++];
var t_$4 = sl_$1[i_$2];
if (s_$3.isinited || !s_$3.__LZinstantiated) continue;
s_$3.__LZcallInit();
if (this.__LZdeleted) return;
if (t_$4 != sl_$1[i_$2]) {
while (i_$2 > 0) {
if (t_$4 == sl_$1[--i_$2]) break
}}}};
if (this.__LZsourceLocation) {
LzNode.sourceLocatorTable[this.__LZsourceLocation] = this
};
this.init();
if (this.oninit.ready) this.oninit.sendEvent(this);
if (this.datapath && this.datapath.__LZApplyDataOnInit) {
this.datapath.__LZApplyDataOnInit()
};
this.inited = true;
if (this.oninited.ready) {
this.oninited.sendEvent(true)
}};
$lzsc$temp["displayName"] = "__LZcallInit";
return $lzsc$temp
})(), "completeInstantiation", (function () {
var $lzsc$temp = function () {
if (!this.isinited) {
var myis_$0 = this.initstage;
this.initstage = "early";
if (myis_$0 == "defer") {
lz.Instantiator.createImmediate(this, this.__LZdeferredcarr)
} else {
lz.Instantiator.completeTrickle(this)
}}};
$lzsc$temp["displayName"] = "completeInstantiation";
return $lzsc$temp
})(), "ignoreplacement", false, "$lzc$set_$delegates", -1, "$lzc$set_$classrootdepth", -1, "$lzc$set_$datapath", -1, "__LZapplyArgs", (function () {
var $lzsc$temp = function (args_$0, constcall_$1) {
var ignore_$2 = LzNode._ignoreAttribute;
var oset_$3 = {};
var hasset_$4 = null;
var inits_$5 = null;
var constraints_$6 = null;
if ("name" in args_$0) {
this.$lzc$set_name(args_$0.name);
delete args_$0.name
};
for (var key_$7 in args_$0) {
var expr_$8 = args_$0[key_$7];
if (expr_$8 && (LzAttributeDescriptor["$lzsc$isa"] ? LzAttributeDescriptor.$lzsc$isa(expr_$8) : expr_$8 instanceof LzAttributeDescriptor)) {
var val_$9 = expr_$8.value
} else {
var val_$9 = expr_$8
};
if (oset_$3[key_$7] || val_$9 === ignore_$2) continue;
oset_$3[key_$7] = true;
var setr_$a = "$lzc$set_" + key_$7;
if (expr_$8 && (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(expr_$8) : expr_$8 instanceof LzInitExpr)) {
if (expr_$8 instanceof LzConstraintExpr) {
if (constraints_$6 == null) {
constraints_$6 = []
};
constraints_$6.push(expr_$8)
} else if (expr_$8 instanceof LzOnceExpr) {
if (inits_$5 == null) {
inits_$5 = []
};
inits_$5.push(expr_$8)
} else {
Debug.debug("Unknown init expr: %w", expr_$8)
};
if (this[key_$7] === void 0) {
this[key_$7] = null
}} else if (!this[setr_$a]) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(val_$9) : val_$9 instanceof Function) {
this.addProperty(key_$7, val_$9)
} else if (val_$9 !== void 0) {
this[key_$7] = val_$9
};
if (!constcall_$1) {
var thisevent_$b = this["on" + key_$7];
if (thisevent_$b) {
if (thisevent_$b.ready) {
thisevent_$b.sendEvent(args_$0[key_$7])
}}}} else if (this[setr_$a] != -1) {
if (hasset_$4 == null) {
hasset_$4 = []
};
hasset_$4.push(setr_$a, val_$9);
if (this[key_$7] === void 0) {
this[key_$7] = null
}}};
if ("$delegates" in args_$0) {
var $c = args_$0.$delegates;
var cdels_$d = this.__LZconstraintdelegates;
var resarray_$e;
for (var i_$f = 0, l_$g = $c.length;i_$f < l_$g;i_$f += 3) {
if ($c[i_$f + 2]) {
if (resarray_$e == null) {
resarray_$e = []
};
resarray_$e.push($c[i_$f], $c[i_$f + 1], $c[i_$f + 2])
} else {
var m_$h = $c[i_$f + 1];
if (!cdels_$d) {
cdels_$d = this.__LZconstraintdelegates = []
};
cdels_$d.push(new LzDelegate(this, m_$h, this, $c[i_$f]))
}};
if (resarray_$e != null) {
this.__LZstoreAttr(resarray_$e, "$delegates")
}};
if ("$classrootdepth" in args_$0) {
var $i = args_$0.$classrootdepth;
if ($i) {
var p_$j = this.parent;
while (--$i > 0) {
p_$j = p_$j.parent
};
this.classroot = p_$j
}};
if (("$datapath" in args_$0) && args_$0.$datapath !== ignore_$2) {
var $k = args_$0.$datapath;
if (!($k instanceof Object)) {
Debug.debug("`$datapath` is non-object %w?", $k)
};
this.makeChild($k, true)
};
if (hasset_$4) {
for (var i_$f = 0, l_$g = hasset_$4.length;i_$f < l_$g;i_$f += 2) {
if (this.__LZdeleted) return;
this[hasset_$4[i_$f]](hasset_$4[i_$f + 1])
}};
if (inits_$5 != null) {
this.__LZstoreAttr(inits_$5, "$inits")
};
if (constraints_$6 != null) {
this.__LZstoreAttr(constraints_$6, "$constraints")
}};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "createChildren", (function () {
var $lzsc$temp = function (carr_$0) {
if (this.__LZdeleted) return;
if ("defer" == this.initstage) {
this.__LZdeferredcarr = carr_$0
} else if ("late" == this.initstage) {
lz.Instantiator.trickleInstantiate(this, carr_$0)
} else if (this.__LZisnew && lz.Instantiator.syncNew || "immediate" == this.initstage) {
lz.Instantiator.createImmediate(this, carr_$0)
} else {
lz.Instantiator.requestInstantiation(this, carr_$0)
}};
$lzsc$temp["displayName"] = "createChildren";
return $lzsc$temp
})(), "makeChild", (function () {
var $lzsc$temp = function (e_$0, async_$1) {
switch (arguments.length) {
case 1:
async_$1 = null
};
if (this.__LZdeleted) {
return
};
for (var p_$2 = this;p_$2 != canvas;p_$2 = p_$2.immediateparent) {
if (p_$2 == null) break;
if (p_$2.__LZdeleted) {
Debug.error("%w.makeChild(%w, %w) when %w.__LZdeleted", this, e_$0, async_$1, p_$2)
}};
var x_$3 = e_$0["class"];
if (!x_$3) {
if (e_$0["tag"]) {
x_$3 = lz[e_$0.tag]
}};
var ok_$4 = Function["$lzsc$isa"] ? Function.$lzsc$isa(x_$3) : x_$3 instanceof Function;
if (!ok_$4) {
var name_$5 = e_$0["tag"] || e_$0["name"];
if (name_$5) {
name_$5 = "<" + name_$5 + ">"
} else {
name_$5 = "a class"
};
Debug.error("Attempt to instantiate %s, which has not been defined", name_$5)
};
var w_$6;
if (x_$3) {
w_$6 = new x_$3(this, e_$0.attrs, ("children" in e_$0) ? e_$0.children : null, async_$1)
};
return w_$6
};
$lzsc$temp["displayName"] = "makeChild";
return $lzsc$temp
})(), "$lzc$set_$setters", (function () {
var $lzsc$temp = function (value_$0) {
Debug.readOnly(this, "$setters", value_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_$setters";
return $lzsc$temp
})(), "dataBindAttribute", (function () {
var $lzsc$temp = function (attr_$0, path_$1, type_$2) {
if (path_$1 == null) {
Debug.warn('No value for %w.%s="$path{%w}"', this, attr_$0, path_$1)
};
if (!this.datapath) {
this.$lzc$set_datapath(".")
};
if (!this.__LZconstraintdelegates) {
this.__LZconstraintdelegates = []
};
this.__LZconstraintdelegates.push(new LzDataAttrBind(this.datapath, attr_$0, path_$1, type_$2))
};
$lzsc$temp["displayName"] = "dataBindAttribute";
return $lzsc$temp
})(), "__LZstoreAttr", (function () {
var $lzsc$temp = function (val_$0, prop_$1) {
if (this.__LZresolveDict == null) {
this.__LZresolveDict = {}};
this.__LZresolveDict[prop_$1] = val_$0
};
$lzsc$temp["displayName"] = "__LZstoreAttr";
return $lzsc$temp
})(), "__LZresolveReferences", (function () {
var $lzsc$temp = function () {
var rdict_$0 = this.__LZresolveDict;
if (rdict_$0 != null) {
this.__LZresolveDict = null;
var inits_$1 = rdict_$0["$inits"];
if (inits_$1 != null) {
for (var i_$2 = 0, l_$3 = inits_$1.length;i_$2 < l_$3;i_$2++) {
this[inits_$1[i_$2].methodName](null);
if (this.__LZdeleted) return
}};
var constraints_$4 = rdict_$0["$constraints"];
if (constraints_$4 != null) {
for (var i_$2 = 0, l_$3 = constraints_$4.length;i_$2 < l_$3;i_$2++) {
this.applyConstraintExpr(constraints_$4[i_$2]);
if (this.__LZdeleted) return
}};
if (this.__LZCSSDependencies != null) {
this.__applyCSSConstraints()
};
if (this["__LZresolveOtherReferences"]) {
this.__LZresolveOtherReferences(rdict_$0)
};
if (rdict_$0["$delegates"]) this.__LZsetDelegates(rdict_$0.$delegates)
}};
$lzsc$temp["displayName"] = "__LZresolveReferences";
return $lzsc$temp
})(), "__LZsetDelegates", (function () {
var $lzsc$temp = function (delarr_$0) {
if (delarr_$0.length < 1) {
return
};
var cdels_$1 = this.__LZconstraintdelegates;
if (!cdels_$1) {
cdels_$1 = this.__LZconstraintdelegates = []
};
var l_$2 = delarr_$0.length;
for (var i_$3 = 0;i_$3 < l_$2;i_$3 += 3) {
var sendermethodname_$4 = delarr_$0[i_$3 + 2];
var sender_$5 = sendermethodname_$4 != null ? this[sendermethodname_$4]() : null;
if (sender_$5 == null) sender_$5 = this;
var meth_$6 = delarr_$0[i_$3 + 1];
cdels_$1.push(new LzDelegate(this, meth_$6, sender_$5, delarr_$0[i_$3]))
}};
$lzsc$temp["displayName"] = "__LZsetDelegates";
return $lzsc$temp
})(), "applyConstraint", (function () {
var $lzsc$temp = function (attribute_$0, constraint_$1, dependencies_$2) {
Debug.deprecated(this, arguments.callee, this.applyConstraintMethod);
var constraintMethodName_$3 = "$cf" + this.$cfn++;
this.addProperty(constraintMethodName_$3, constraint_$1);
return this.applyConstraintMethod(constraintMethodName_$3, dependencies_$2)
};
$lzsc$temp["displayName"] = "applyConstraint";
return $lzsc$temp
})(), "applyConstraintMethod", (function () {
var $lzsc$temp = function (constraintMethodName_$0, dependencies_$1) {
if (!(arguments.length == 2 && typeof constraintMethodName_$0 == "string" && this[constraintMethodName_$0] instanceof Function && (dependencies_$1 == null || dependencies_$1 instanceof Array))) {
Debug.error("%w.%s: invalid arguments %w", this, arguments.callee, arguments)
};
if (dependencies_$1 && dependencies_$1.length > 0) {
var cdels_$2 = this.__LZconstraintdelegates;
if (!cdels_$2) {
cdels_$2 = this.__LZconstraintdelegates = []
};
var dp_$3;
for (var i_$4 = 0, l_$5 = dependencies_$1.length;i_$4 < l_$5;i_$4 += 2) {
dp_$3 = dependencies_$1[i_$4];
if (dp_$3) {
cdels_$2.push(new LzDelegate(this, constraintMethodName_$0, dp_$3, "on" + dependencies_$1[i_$4 + 1]))
}}};
this[constraintMethodName_$0](null)
};
$lzsc$temp["displayName"] = "applyConstraintMethod";
return $lzsc$temp
})(), "applyConstraintExpr", (function () {
var $lzsc$temp = function (expr_$0) {
if (expr_$0 instanceof LzStyleConstraintExpr) {
var se_$1 = expr_$0;
this.__LZstyleBindAttribute(se_$1.attribute, se_$1.property, se_$1.type, se_$1.fallback, se_$1.warn);
return
};
var constraintMethodName_$2 = expr_$0.methodName;
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(this[constraintMethodName_$2]) : this[constraintMethodName_$2] instanceof Function)) {
Debug.error("Bad constraint %w on %w", expr_$0, this);
return
};
var dependencies_$3 = null;
if (expr_$0 instanceof LzAlwaysExpr) {
var c_$4 = expr_$0;
var dependenciesMethodName_$5 = c_$4.dependenciesName;
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(this[dependenciesMethodName_$5]) : this[dependenciesMethodName_$5] instanceof Function)) {
Debug.error("Bad dependencies for constraint %.64w.%w", this, expr_$0)
} else {
try {
dependencies_$3 = this[dependenciesMethodName_$5]();
for (var i_$6 = 0, l_$7 = dependencies_$3.length;i_$6 < l_$7;i_$6 += 2) {
var dp_$8 = dependencies_$3[i_$6];
if (dp_$8 != null && !(LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(dp_$8) : dp_$8 instanceof LzEventable)) {
dependencies_$3[i_$6] = null
}}}
catch (e_$9) {
Debug.error("Error: %w computing dependencies for constraint %.64w.%w", e_$9, this, expr_$0)
}}};
this.applyConstraintMethod(constraintMethodName_$2, dependencies_$3)
};
$lzsc$temp["displayName"] = "applyConstraintExpr";
return $lzsc$temp
})(), "releaseConstraint", (function () {
var $lzsc$temp = function (attr_$0) {
if (this._instanceAttrs != null) {
var c_$1 = this._instanceAttrs[attr_$0];
if (c_$1 instanceof LzConstraintExpr) {
var m_$2 = c_$1.methodName;
return this.releaseConstraintMethod(m_$2)
}};
return false
};
$lzsc$temp["displayName"] = "releaseConstraint";
return $lzsc$temp
})(), "releaseConstraintMethod", (function () {
var $lzsc$temp = function (constraintMethodName_$0) {
var result_$1 = false;
var cdels_$2 = this.__LZconstraintdelegates;
if (cdels_$2) {
for (var i_$3 = 0;i_$3 < cdels_$2.length;) {
var del_$4 = cdels_$2[i_$3];
if ((LzDelegate["$lzsc$isa"] ? LzDelegate.$lzsc$isa(del_$4) : del_$4 instanceof LzDelegate) && del_$4.c === this && del_$4.m === this[constraintMethodName_$0]) {
if (del_$4.__LZdeleted != true) {
del_$4.destroy()
};
cdels_$2.splice(i_$3, 1);
result_$1 = true
} else {
i_$3++
}}};
return result_$1
};
$lzsc$temp["displayName"] = "releaseConstraintMethod";
return $lzsc$temp
})(), "$lzc$set_name", (function () {
var $lzsc$temp = function (name_$0) {
if (!(name_$0 === null || typeof name_$0 == "string")) {
Debug.error("Invalid name %#w for %w", name_$0, this);
return
};
var old_$1 = this.name;
var p_$2 = this.parent;
var ip_$3 = this.immediateparent;
if (old_$1 && old_$1 != name_$0) {
if (this.$lzc$bind_name) {
if (globalValue(old_$1) === this) {
this.$lzc$bind_name.call(null, this, false)
}};
if (p_$2) {
if (old_$1 && p_$2[old_$1] === this) {
p_$2[old_$1] = null
}};
if (ip_$3) {
if (old_$1 && ip_$3[old_$1] === this) {
ip_$3[old_$1] = null
}}};
if (name_$0 && name_$0.length) {
if (p_$2 && p_$2[name_$0] && p_$2[name_$0] !== this) {
Debug.warn("Redefining %w.%s from %w to %w", p_$2, name_$0, p_$2[name_$0], this)
};
if (p_$2) {
p_$2[name_$0] = this
};
if (ip_$3 && ip_$3[name_$0] && ip_$3[name_$0] !== this) {
Debug.warn("Redefining %w.%s from %w to %w", ip_$3, name_$0, ip_$3[name_$0], this)
};
if (ip_$3) {
ip_$3[name_$0] = this
}};
this.name = name_$0
};
$lzsc$temp["displayName"] = "$lzc$set_name";
return $lzsc$temp
})(), "setDatapath", (function () {
var $lzsc$temp = function (dp_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_datapath(dp_$0)
};
$lzsc$temp["displayName"] = "setDatapath";
return $lzsc$temp
})(), "data", null, "$lzc$set_data", (function () {
var $lzsc$temp = function (data_$0) {
this.data = data_$0;
if (LzDataNodeMixin["$lzsc$isa"] ? LzDataNodeMixin.$lzsc$isa(data_$0) : data_$0 instanceof LzDataNodeMixin) {
var dp_$1 = this.datapath || new LzDatapath(this);
dp_$1.setPointer(data_$0)
};
if (this.ondata.ready) this.ondata.sendEvent(data_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "setData", (function () {
var $lzsc$temp = function (data_$0, ignore_$1) {
switch (arguments.length) {
case 1:
ignore_$1 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_data(data_$0)
};
$lzsc$temp["displayName"] = "setData";
return $lzsc$temp
})(), "applyData", (function () {
var $lzsc$temp = function (data_$0) {};
$lzsc$temp["displayName"] = "applyData";
return $lzsc$temp
})(), "updateData", (function () {
var $lzsc$temp = function () {
return void 0
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "setSelected", (function () {
var $lzsc$temp = function (sel_$0) {};
$lzsc$temp["displayName"] = "setSelected";
return $lzsc$temp
})(), "options", {}, "$lzc$set_options", (function () {
var $lzsc$temp = function (hash_$0) {
if (this.options === this["constructor"].prototype.options) {
this.options = new LzInheritedHash(this.options)
};
for (var key_$1 in hash_$0) {
this.options[key_$1] = hash_$0[key_$1]
}};
$lzsc$temp["displayName"] = "$lzc$set_options";
return $lzsc$temp
})(), "getOption", (function () {
var $lzsc$temp = function (key_$0) {
return this.options[key_$0]
};
$lzsc$temp["displayName"] = "getOption";
return $lzsc$temp
})(), "setOption", (function () {
var $lzsc$temp = function (key_$0, val_$1) {
if (this.options === this["constructor"].prototype.options) {
this.options = new LzInheritedHash(this.options)
};
this.options[key_$0] = val_$1
};
$lzsc$temp["displayName"] = "setOption";
return $lzsc$temp
})(), "determinePlacement", (function () {
var $lzsc$temp = function (aSub_$0, placement_$1, args_$2) {
if (placement_$1 == null) {
var p_$3 = null
} else {
var p_$3 = this.searchSubnodes("name", placement_$1)
};
return p_$3 == null ? this : p_$3
};
$lzsc$temp["displayName"] = "determinePlacement";
return $lzsc$temp
})(), "searchImmediateSubnodes", (function () {
var $lzsc$temp = function (prop_$0, val_$1) {
var s_$2 = this.subnodes;
if (s_$2 == null) return null;
for (var i_$3 = s_$2.length - 1;i_$3 >= 0;i_$3--) {
var si_$4 = s_$2[i_$3];
if (si_$4[prop_$0] == val_$1) {
return si_$4
}};
return null
};
$lzsc$temp["displayName"] = "searchImmediateSubnodes";
return $lzsc$temp
})(), "searchSubnodes", (function () {
var $lzsc$temp = function (prop_$0, val_$1) {
var nextS_$2 = this.subnodes ? this.subnodes.concat() : [];
while (nextS_$2.length > 0) {
var s_$3 = nextS_$2;
nextS_$2 = new Array();
for (var i_$4 = s_$3.length - 1;i_$4 >= 0;i_$4--) {
var si_$5 = s_$3[i_$4];
if (si_$5[prop_$0] == val_$1) {
return si_$5
};
var sis_$6 = si_$5.subnodes;
if (sis_$6) {
for (var j_$7 = sis_$6.length - 1;j_$7 >= 0;j_$7--) {
nextS_$2.push(sis_$6[j_$7])
}}}};
return null
};
$lzsc$temp["displayName"] = "searchSubnodes";
return $lzsc$temp
})(), "searchParents", (function () {
var $lzsc$temp = function (prop_$0) {
return this.searchParentAttrs([prop_$0])[prop_$0]
};
$lzsc$temp["displayName"] = "searchParents";
return $lzsc$temp
})(), "searchParentAttrs", (function () {
var $lzsc$temp = function (proplist_$0) {
var out_$1 = {};
if (!proplist_$0.length) return out_$1;
var props_$2 = proplist_$0.slice();
var sview_$3 = this;
do {
sview_$3 = sview_$3.immediateparent;
if (sview_$3 == null) {
Debug.error("searchParentAttrs got null immediateparent", this);
return out_$1
};
var i_$4 = 0;
var l_$5 = props_$2.length;
while (i_$4 < l_$5) {
var prop_$6 = props_$2[i_$4];
if (sview_$3[prop_$6] != null) {
out_$1[prop_$6] = sview_$3;
props_$2.splice(i_$4, 1);
l_$5--
} else {
i_$4++
}}} while (sview_$3 != canvas && l_$5 > 0);
return out_$1
};
$lzsc$temp["displayName"] = "searchParentAttrs";
return $lzsc$temp
})(), "getUID", (function () {
var $lzsc$temp = function () {
return this.__LZUID
};
$lzsc$temp["displayName"] = "getUID";
return $lzsc$temp
})(), "childOf", (function () {
var $lzsc$temp = function (node_$0, ignore_$1) {
switch (arguments.length) {
case 1:
ignore_$1 = null
};
if (node_$0 == null) {
return false
};
var pv_$2 = this;
while (pv_$2.nodeLevel >= node_$0.nodeLevel) {
if (pv_$2 == node_$0) {
return true
};
pv_$2 = pv_$2.immediateparent
};
return false
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted == true) {
return
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this);
if (this.subnodes != null) {
for (var i_$0 = this.subnodes.length - 1;i_$0 >= 0;i_$0--) {
this.subnodes[i_$0].destroy()
}};
if (this.$lzc$bind_id) {
this.$lzc$bind_id.call(null, this, false)
};
if (this.$lzc$bind_name) {
this.$lzc$bind_name.call(null, this, false)
};
var parent_$1 = this.parent;
var name_$2 = this.name;
if (parent_$1 != null && name_$2 != null) {
if (parent_$1[name_$2] === this) {
parent_$1[name_$2] = null
};
if (this.immediateparent[name_$2] === this) {
this.immediateparent[name_$2] == null
}};
if (this.__LZconstraintdelegates != null) {
this.__LZconstraintdelegates = null
};
if (this.immediateparent && this.immediateparent.subnodes) {
for (var i_$0 = this.immediateparent.subnodes.length - 1;i_$0 >= 0;i_$0--) {
if (this.immediateparent.subnodes[i_$0] === this) {
this.immediateparent.subnodes.splice(i_$0, 1);
break
}}};
this.data = null
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "animate", (function () {
var $lzsc$temp = function (prop_$0, to_$1, duration_$2, isRelative_$3, moreargs_$4) {
switch (arguments.length) {
case 3:
isRelative_$3 = null;;case 4:
moreargs_$4 = null
};
if (duration_$2 == 0) {
var val_$5 = isRelative_$3 ? this[prop_$0] + to_$1 : to_$1;
{
if (!this.__LZdeleted) {
var $lzsc$m5lv62 = "$lzc$set_" + prop_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$m5lv62]) : this[$lzsc$m5lv62] instanceof Function) {
this[$lzsc$m5lv62](val_$5)
} else {
this[prop_$0] = val_$5;
var $lzsc$13ptjt = this["on" + prop_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$13ptjt) : $lzsc$13ptjt instanceof LzEvent) {
if ($lzsc$13ptjt.ready) {
$lzsc$13ptjt.sendEvent(val_$5)
}}}}};
return null
};
var args_$6 = {attribute: prop_$0, to: to_$1, duration: duration_$2, relative: isRelative_$3, target: this};
for (var p_$7 in moreargs_$4) args_$6[p_$7] = moreargs_$4[p_$7];
return new LzAnimator(null, args_$6)
};
$lzsc$temp["displayName"] = "animate";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.getDebugIdentification()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "getDebugIdentification", (function () {
var $lzsc$temp = function () {
var s_$0 = this["constructor"].tagname;
if (this["name"] != null) {
s_$0 += " name: " + this.name
};
if (this["id"] != null) {
s_$0 += " id: " + this.id
};
return s_$0
};
$lzsc$temp["displayName"] = "getDebugIdentification";
return $lzsc$temp
})(), "nodePath", (function () {
var $lzsc$temp = function (node_$0, limit_$1) {
switch (arguments.length) {
case 1:
limit_$1 = Infinity
};
if (node_$0 === canvas) {
return ""
};
if (node_$0 === Debug.inspectContext) {
return "."
};
var nid_$2 = node_$0.id;
if (typeof nid_$2 == "string" && globalValue(nid_$2) === node_$0) {
return "#" + nid_$2
};
var nn_$3 = node_$0.name;
if (typeof nn_$3 == "string" && globalValue(nn_$3) === node_$0) {
return "#" + nn_$3
};
var parent_$4 = node_$0.immediateparent || node_$0.parent;
var path_$5 = "";
if (parent_$4 != null) {
if (typeof nn_$3 == "string" && parent_$4[nn_$3] === node_$0) {
path_$5 = "@" + nn_$3
} else {
var nct_$6 = node_$0.constructor.tagname;
if (!nct_$6) {
path_$5 = "anonymous"
} else {
path_$5 = nct_$6;
var sn_$7 = parent_$4.subnodes;
var index_$8, count_$9 = 0;
for (var i_$a = 0, len_$b = sn_$7.length;i_$a < len_$b;i_$a++) {
var sibling_$c = sn_$7[i_$a];
if (nct_$6 == sibling_$c.constructor.tagname) {
count_$9++;
if (index_$8) break
};
if (node_$0 === sibling_$c) {
index_$8 = count_$9
}};
if (count_$9 > 1) {
path_$5 += "[" + index_$8 + "]"
}}};
if (path_$5.length >= limit_$1) {
return "\u2026"
};
try {
return this.nodePath(parent_$4, limit_$1 - path_$5.length - 1) + "/" + path_$5
}
catch (e_$d) {
return "\u2026/" + path_$5
}};
return path_$5
};
$lzsc$temp["displayName"] = "nodePath";
return $lzsc$temp
})(), "acceptTypeValue", (function () {
var $lzsc$temp = function (type_$0, value_$1, node_$2, attribute_$3) {
Debug.deprecated(this, arguments.callee, lz.Type.acceptTypeValue);
return lz.Type.acceptTypeValue(type_$0, value_$1, node_$2, attribute_$3)
};
$lzsc$temp["displayName"] = "acceptTypeValue";
return $lzsc$temp
})(), "acceptAttribute", (function () {
var $lzsc$temp = function (name_$0, type_$1, value_$2) {
value_$2 = lz.Type.acceptTypeValue(type_$1, value_$2, this, name_$0);
if (this[name_$0] !== value_$2) {
{
if (!this.__LZdeleted) {
var $lzsc$8m5o1m = "$lzc$set_" + name_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$8m5o1m]) : this[$lzsc$8m5o1m] instanceof Function) {
this[$lzsc$8m5o1m](value_$2)
} else {
this[name_$0] = value_$2;
var $lzsc$oopj22 = this["on" + name_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$oopj22) : $lzsc$oopj22 instanceof LzEvent) {
if ($lzsc$oopj22.ready) {
$lzsc$oopj22.sendEvent(value_$2)
}}}}}}};
$lzsc$temp["displayName"] = "acceptAttribute";
return $lzsc$temp
})(), "presentTypeValue", (function () {
var $lzsc$temp = function (type_$0, value_$1, node_$2, attribute_$3) {
Debug.deprecated(this, arguments.callee, lz.Type.presentTypeValue);
return lz.Type.presentTypeValue(type_$0, value_$1, node_$2, attribute_$3)
};
$lzsc$temp["displayName"] = "presentTypeValue";
return $lzsc$temp
})(), "presentAttribute", (function () {
var $lzsc$temp = function (name_$0, type_$1) {
return lz.Type.presentTypeValue(type_$1, this[name_$0], this, name_$0)
};
$lzsc$temp["displayName"] = "presentAttribute";
return $lzsc$temp
})(), "$lzc$presentAttribute_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1, name_$2, type_$3) {
return [self_$1, name_$2]
};
$lzsc$temp["displayName"] = "$lzc$presentAttribute_dependencies";
return $lzsc$temp
})(), "ontransition", LzDeclaredEvent, "transition", void 0, "$lzc$set_transition", (function () {
var $lzsc$temp = function (css_$0) {
if (transition_$6 === css_$0) return;
this.transition = css_$0;
var transitions_$1 = {};
var counter_$2 = 0;
var transitionlist_$3 = css_$0.split(",");
for (var i_$4 = 0, l_$5 = transitionlist_$3.length;i_$4 < l_$5;i_$4++) {
var transition_$6 = transitionlist_$3[i_$4].split(" ");
var attr_$7 = transition_$6.shift();
if (attr_$7) {
var duration_$8 = transition_$6.shift();
duration_$8 = parseFloat(duration_$8) * 1000;
if (!duration_$8 || isNaN(duration_$8)) continue;
var motion_$9 = transition_$6.shift();
if (motion_$9 == "ease-in") {
motion_$9 = "easein"
} else if (motion_$9 == "ease-out") {
motion_$9 = "easeout"
} else if (motion_$9 == "linear") {

} else {
motion_$9 = "ease"
};
transitions_$1[attr_$7] = {duration: duration_$8, motion: motion_$9};
counter_$2++
}};
this.__transitionAttributes = transitions_$1;
this.customSetters = transitions_$1
};
$lzsc$temp["displayName"] = "$lzc$set_transition";
return $lzsc$temp
})(), "__invokeCustomSetter", (function () {
var $lzsc$temp = function (prop_$0, val_$1) {
if (this.inited != true) return false;
var ta_$2 = this.__transitionAttributes && this.__transitionAttributes[prop_$0];
if (ta_$2 && this[prop_$0] !== val_$1) {
this.animate(prop_$0, val_$1, ta_$2.duration, false, {motion: ta_$2.motion})
};
return true
};
$lzsc$temp["displayName"] = "__invokeCustomSetter";
return $lzsc$temp
})()], LzEventable, ["tagname", "node", "attributes", new LzInheritedHash(), "mergeAttributes", (function () {
var $lzsc$temp = function (attrs_$0, dattrs_$1) {
for (var k_$2 in attrs_$0) {
var attrk_$3 = attrs_$0[k_$2];
if (attrk_$3 === LzNode._ignoreAttribute) {
delete dattrs_$1[k_$2]
} else if (LzInitExpr["$lzsc$isa"] ? LzInitExpr.$lzsc$isa(attrk_$3) : attrk_$3 instanceof LzInitExpr) {
dattrs_$1[k_$2] = attrk_$3
} else {
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(attrk_$3) : attrk_$3 instanceof Object) {
var dattrk_$4 = dattrs_$1[k_$2];
if (Object["$lzsc$isa"] ? Object.$lzsc$isa(dattrk_$4) : dattrk_$4 instanceof Object) {
if ((Array["$lzsc$isa"] ? Array.$lzsc$isa(attrk_$3) : attrk_$3 instanceof Array) && (Array["$lzsc$isa"] ? Array.$lzsc$isa(dattrk_$4) : dattrk_$4 instanceof Array)) {
dattrs_$1[k_$2] = attrk_$3.concat(dattrk_$4);
continue
} else if ((attrk_$3.constructor === Object || (LzInheritedHash["$lzsc$isa"] ? LzInheritedHash.$lzsc$isa(attrk_$3) : attrk_$3 instanceof LzInheritedHash)) && (dattrk_$4.constructor === Object || (LzInheritedHash["$lzsc$isa"] ? LzInheritedHash.$lzsc$isa(dattrk_$4) : dattrk_$4 instanceof LzInheritedHash))) {
var tmp_$5 = new LzInheritedHash(dattrk_$4);
for (var j_$6 in attrk_$3) {
tmp_$5[j_$6] = attrk_$3[j_$6]
};
dattrs_$1[k_$2] = tmp_$5;
continue
}}};
dattrs_$1[k_$2] = attrk_$3
}}};
$lzsc$temp["displayName"] = "mergeAttributes";
return $lzsc$temp
})(), "mergeChildren", (function () {
var $lzsc$temp = function (children_$0, superclasschildren_$1) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(superclasschildren_$1) : superclasschildren_$1 instanceof Array) {
children_$0 = superclasschildren_$1.concat((Array["$lzsc$isa"] ? Array.$lzsc$isa(children_$0) : children_$0 instanceof Array) ? children_$0 : [])
};
return children_$0
};
$lzsc$temp["displayName"] = "mergeChildren";
return $lzsc$temp
})(), "__LzEarlyAbort", {toString: (function () {
var $lzsc$temp = function () {
return "Early Abort"
};
$lzsc$temp["displayName"] = "core/LzNode.lzs#362/43";
return $lzsc$temp
})()}, "sourceLocatorTable", {}, "_ignoreAttribute", {toString: (function () {
var $lzsc$temp = function () {
return "_ignoreAttribute"
};
$lzsc$temp["displayName"] = "core/LzNode.lzs#1173/44";
return $lzsc$temp
})()}, "__UIDs", 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
var dn_$0 = this.nodePath(this, Debug.printLength);
if (dn_$0 != "") {
return dn_$0
};
var ts_$1 = this.toString();
if (ts_$1 == this.getDebugIdentification()) {
return ""
} else {
return ts_$1
}};
$lzsc$temp["displayName"] = "core/LzNode.lzs#2358/34";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "core/LzNode.lzs#35/1";
return $lzsc$temp
})()(LzNode);
lz[LzNode.tagname] = LzNode;
Class.make("$lzc$class_userClassPlacement", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, placement_$1, ignore_$2, ignoremetoo_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
placement_$1 = null;;case 2:
ignore_$2 = null;;case 3:
ignoremetoo_$3 = null
};
parent_$0.defaultplacement = placement_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()]);
Class.make("LzDelegate", ["__delegateID", 0, "__events", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (context_$0, methodName_$1, eventSender_$2, eventName_$3) {
switch (arguments.length) {
case 2:
eventSender_$2 = null;;case 3:
eventName_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
if (!(LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(context_$0) : context_$0 instanceof LzEventable)) {
Debug.error("Not creating delegate: invalid context: %.64w.%s", context_$0, methodName_$1);
return
};
if (context_$0 == null || context_$0["__LZdeleted"]) {
Debug.warn("Not creating delegate:  %s context %.64w.%s", context_$0 == null ? "null" : "deleted", context_$0, methodName_$1);
return
};
this.c = context_$0;
var m_$4 = context_$0[methodName_$1];
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(m_$4) : m_$4 instanceof Function)) {
Debug.error("Not creating delegate:  invalid method %.64w.%=s (must be a Function)", context_$0, m_$4, methodName_$1);
return
};
this.m = m_$4;
if (m_$4.length != 1) {
Debug.warn("Invalid delegate method %.64w.%=s (must accept one argument)", context_$0, m_$4, methodName_$1)
};
if (eventSender_$2 != null) {
this.register(eventSender_$2, eventName_$3)
};
this.__delegateID = LzDelegate.__nextID++
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "c", void 0, "m", void 0, "hasevents", false, "enabled", true, "event_called", false, "execute", (function () {
var $lzsc$temp = function (eventValue_$0) {
var context_$1 = this.c;
if (this.enabled && context_$1) {
if (context_$1["__LZdeleted"]) {
return
};
var m_$2 = this.m;
return m_$2 && m_$2.call(context_$1, eventValue_$0)
}};
$lzsc$temp["displayName"] = "execute";
return $lzsc$temp
})(), "register", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1) {
var compatibilitymode_$2 = false;
if (!(LzEventable["$lzsc$isa"] ? LzEventable.$lzsc$isa(eventSender_$0) : eventSender_$0 instanceof LzEventable)) {
Debug.error("Not registering %.64w: invalid event sender: %.64w.%s", this, eventSender_$0, eventName_$1);
return
};
if (this.c == null || this.c["__LZdeleted"]) {
return
};
if (eventSender_$0 !== this.c && !compatibilitymode_$2) {
if (this.__tracked == false) {
this.__tracked = true;
var _dels_$3 = this.c["__delegates"];
if (_dels_$3 == null) {
this.c.__delegates = [this]
} else {
_dels_$3.push(this)
}}};
var anEvent_$4 = eventSender_$0[eventName_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa(anEvent_$4) : anEvent_$4 instanceof LzEvent) {
anEvent_$4.addDelegate(this)
} else {
if (anEvent_$4 && !(LzDeclaredEventClass["$lzsc$isa"] ? LzDeclaredEventClass.$lzsc$isa(anEvent_$4) : anEvent_$4 instanceof LzDeclaredEventClass)) {
Debug.error("Not registering %.64w:  invalid event: %.64w.%=s", this, eventSender_$0, anEvent_$4, eventName_$1);
return
};
var eventclass_$5 = anEvent_$4 && anEvent_$4.actual || LzEvent;
anEvent_$4 = new eventclass_$5(eventSender_$0, eventName_$1, this)
};
var events_$6 = this.__events;
if (events_$6 == null) {
this.__events = [anEvent_$4]
} else {
events_$6.push(anEvent_$4)
};
this.hasevents = true
};
$lzsc$temp["displayName"] = "register";
return $lzsc$temp
})(), "unregisterAll", (function () {
var $lzsc$temp = function () {
if (this.hasevents == false) return;
var events_$0 = this.__events;
for (var i_$1 = 0, l_$2 = events_$0.length;i_$1 < l_$2;i_$1++) {
events_$0[i_$1].removeDelegate(this)
};
events_$0.length = 0;
this.hasevents = false
};
$lzsc$temp["displayName"] = "unregisterAll";
return $lzsc$temp
})(), "unregisterFrom", (function () {
var $lzsc$temp = function (event_$0) {
if (this.hasevents == false) return;
var events_$1 = this.__events;
for (var i_$2 = 0, l_$3 = events_$1.length;i_$2 < l_$3;i_$2++) {
var ev_$4 = events_$1[i_$2];
if (ev_$4 === event_$0) {
ev_$4.removeDelegate(this);
events_$1.splice(i_$2, 1)
}};
this.hasevents = events_$1.length > 0
};
$lzsc$temp["displayName"] = "unregisterFrom";
return $lzsc$temp
})(), "disable", (function () {
var $lzsc$temp = function () {
this.enabled = false
};
$lzsc$temp["displayName"] = "disable";
return $lzsc$temp
})(), "enable", (function () {
var $lzsc$temp = function () {
this.enabled = true
};
$lzsc$temp["displayName"] = "enable";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Delegate for " + this.c + " calls " + this.m + " " + this.__delegateID
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "__tracked", false, "__LZdeleted", false, "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted == true) return;
this.__LZdeleted = true;
if (this.hasevents) this.unregisterAll();
this.hasevents = false;
this.__events = null;
this.c = null;
this.m = null
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})()], null, ["__nextID", 1, "__LZdrainDelegatesQueue", (function () {
var $lzsc$temp = function (evq_$0) {
var n_$1 = evq_$0.length;
var i_$2 = 0;
if (i_$2 < n_$1) {
var calledDelegates_$3 = new Array();
var lockedEvents_$4 = new Array();
while (i_$2 < n_$1) {
var e_$5 = evq_$0[i_$2];
var d_$6 = evq_$0[i_$2 + 1];
var eventValue_$7 = evq_$0[i_$2 + 2];
e_$5.locked = true;
e_$5.ready = false;
lockedEvents_$4.push(e_$5);
if (!d_$6.event_called) {
d_$6.event_called = true;
calledDelegates_$3.push(d_$6);
if (d_$6.c && !d_$6.c.__LZdeleted && d_$6.m) {
d_$6.m.call(d_$6.c, eventValue_$7)
}};
i_$2 += 3
};
while (d_$6 = calledDelegates_$3.pop()) {
d_$6.event_called = false
};
while (e_$5 = lockedEvents_$4.pop()) {
e_$5.locked = false;
e_$5.ready = e_$5.delegateList.length != 0
}};
evq_$0.length = 0
};
$lzsc$temp["displayName"] = "__LZdrainDelegatesQueue";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzDelegate.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
var name_$0 = Debug.formatToString("%.48w/<handler", this.c);
var method_$1 = Debug.functionName(this.m);
if (method_$1.indexOf("$") != 0) {
name_$0 += " method=" + Debug.stringEscape(method_$1, true)
};
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(this.__events) : this.__events instanceof Array) {
var ev0_$2 = this.__events[0];
if (ev0_$2) {
name_$0 += Debug.formatToString(" name='%s'", ev0_$2._dbg_eventName);
if (ev0_$2._dbg_eventSender !== this.c) {
name_$0 += Debug.formatToString(" reference='%.64w'", ev0_$2._dbg_eventSender)
};
if (this.__events[1]) {
name_$0 += " \u2026"
}}};
name_$0 += ">";
return name_$0
};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#406/36";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#80/1";
return $lzsc$temp
})()(LzDelegate);
lz.Delegate = LzDelegate;
Class.make("LzEvent", ["delegateList", null, "_dbg_eventSender", void 0, "_dbg_eventName", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1, d_$2) {
switch (arguments.length) {
case 2:
d_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
var _evs_$3 = eventSender_$0["_events"];
if (_evs_$3 == null) {
eventSender_$0._events = [this]
} else {
_evs_$3.push(this)
};
eventSender_$0[eventName_$1] = this;
if (d_$2) {
this.delegateList = [d_$2];
this.ready = true
} else {
this.delegateList = []
};
this._dbg_eventSender = eventSender_$0;
this._dbg_eventName = eventName_$1
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "locked", false, "addDelegate", (function () {
var $lzsc$temp = function (d_$0) {
this.ready = true;
this.delegateList.push(d_$0)
};
$lzsc$temp["displayName"] = "addDelegate";
return $lzsc$temp
})(), "sendEvent", (function () {
var $lzsc$temp = function (eventValue_$0) {
switch (arguments.length) {
case 0:
eventValue_$0 = null
};
if (this.locked || !this.ready) {
return
};
this.locked = true;
this.ready = false;
var dlist_$1 = this.delegateList;
var calledDelegates_$2 = new Array();
var d_$3;
var i_$4 = dlist_$1.length;
while (--i_$4 >= 0) {
d_$3 = dlist_$1[i_$4];
if (d_$3 && d_$3.enabled && !d_$3.event_called) {
d_$3.event_called = true;
calledDelegates_$2.push(d_$3);
var c_$5 = d_$3.c;
if (c_$5 && !c_$5.__LZdeleted) {
if (c_$5.__LZdeferDelegates) {
var evq_$6 = c_$5.__LZdelegatesQueue;
if (!evq_$6) {
c_$5.__LZdelegatesQueue = [this, d_$3, eventValue_$0]
} else {
evq_$6.push(this, d_$3, eventValue_$0)
}} else if (d_$3.m) {
d_$3.m.call(c_$5, eventValue_$0)
}}}};
while (d_$3 = calledDelegates_$2.pop()) {
d_$3.event_called = false
};
this.locked = false;
this.ready = dlist_$1.length != 0
};
$lzsc$temp["displayName"] = "sendEvent";
return $lzsc$temp
})(), "removeDelegate", (function () {
var $lzsc$temp = function (d_$0) {
switch (arguments.length) {
case 0:
d_$0 = null
};
var dlist_$1 = this.delegateList;
for (var i_$2 = 0, l_$3 = dlist_$1.length;i_$2 < l_$3;i_$2++) {
if (dlist_$1[i_$2] === d_$0) {
dlist_$1.splice(i_$2, 1);
break
}};
this.ready = dlist_$1.length != 0
};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "clearDelegates", (function () {
var $lzsc$temp = function () {
var dlist_$0 = this.delegateList;
while (dlist_$0.length) {
dlist_$0[0].unregisterFrom(this)
};
this.ready = false
};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})(), "getDelegateCount", (function () {
var $lzsc$temp = function () {
return this.delegateList.length
};
$lzsc$temp["displayName"] = "getDelegateCount";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "LzEvent"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], LzDeclaredEventClass);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzEvent.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
return Debug.formatToString("%0.48w/<event name='%s'>", this._dbg_eventSender, this._dbg_eventName)
};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#699/31";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "events/LaszloEvents.lzs#516/1";
return $lzsc$temp
})()(LzEvent);
lz.Event = LzEvent;
Class.make("LzNotifyingEvent", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1, d_$2) {
switch (arguments.length) {
case 2:
d_$2 = null
};
var wasready_$3 = this.ready;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, eventSender_$0, eventName_$1, d_$2);
if (this.ready != wasready_$3) {
this.notify(this.ready)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "addDelegate", (function () {
var $lzsc$temp = function (d_$0) {
var wasready_$1 = this.ready;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["addDelegate"] || this.nextMethod(arguments.callee, "addDelegate")).call(this, d_$0);
if (this.ready != wasready_$1) {
this.notify(this.ready)
}};
$lzsc$temp["displayName"] = "addDelegate";
return $lzsc$temp
})(), "removeDelegate", (function () {
var $lzsc$temp = function (d_$0) {
switch (arguments.length) {
case 0:
d_$0 = null
};
var wasready_$1 = this.ready;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["removeDelegate"] || this.nextMethod(arguments.callee, "removeDelegate")).call(this, d_$0);
if (this.ready != wasready_$1) {
this.notify(this.ready)
}};
$lzsc$temp["displayName"] = "removeDelegate";
return $lzsc$temp
})(), "clearDelegates", (function () {
var $lzsc$temp = function () {
var wasready_$0 = this.ready;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["clearDelegates"] || this.nextMethod(arguments.callee, "clearDelegates")).call(this);
if (this.ready != wasready_$0) {
this.notify(this.ready)
}};
$lzsc$temp["displayName"] = "clearDelegates";
return $lzsc$temp
})(), "notify", (function () {
var $lzsc$temp = function (ready_$0) {};
$lzsc$temp["displayName"] = "notify";
return $lzsc$temp
})()], LzEvent);
lz.NotifyingEvent = LzNotifyingEvent;
var LzResourceLibrary = {};
Class.make("LzKernelUtils", null, null, ["CSSDimension", (function () {
var $lzsc$temp = function (value_$0, units_$1) {
switch (arguments.length) {
case 1:
units_$1 = "px"
};
if (value_$0 == 0) return value_$0;
var result_$2 = value_$0;
if (isNaN(value_$0)) {
if (typeof value_$0 == "string" && value_$0.indexOf("%") == value_$0.length - 1 && !isNaN(value_$0.substring(0, value_$0.length - 1))) {
return value_$0
} else {
result_$2 = 0;
Debug.warn("%w: coerced %w to %w", arguments.callee, value_$0, result_$2)
}} else if (value_$0 === Infinity) {
result_$2 = ~0 >>> 1
} else if (value_$0 === -Infinity) {
result_$2 = ~(~0 >>> 1)
};
return result_$2 + units_$1
};
$lzsc$temp["displayName"] = "CSSDimension";
return $lzsc$temp
})(), "range", (function () {
var $lzsc$temp = function (val_$0, max_$1, min_$2) {
switch (arguments.length) {
case 2:
min_$2 = null
};
val_$0 = val_$0 > max_$1 ? max_$1 : val_$0;
if (min_$2 != null) {
val_$0 = val_$0 < min_$2 ? min_$2 : val_$0
};
return val_$0
};
$lzsc$temp["displayName"] = "range";
return $lzsc$temp
})(), "rect", (function () {
var $lzsc$temp = function (context_$0, x_$1, y_$2, width_$3, height_$4, topleftradius_$5, toprightradius_$6, bottomrightradius_$7, bottomleftradius_$8) {
switch (arguments.length) {
case 5:
topleftradius_$5 = 0;;case 6:
toprightradius_$6 = null;;case 7:
bottomrightradius_$7 = null;;case 8:
bottomleftradius_$8 = null
};
var maxsize_$9 = Math.min(width_$3, height_$4) * 0.5;
topleftradius_$5 = LzKernelUtils.range(topleftradius_$5 || 0, maxsize_$9, 0);
if (bottomleftradius_$8 == null) {
bottomleftradius_$8 = bottomrightradius_$7 = toprightradius_$6 = topleftradius_$5
} else {
bottomleftradius_$8 = LzKernelUtils.range(bottomleftradius_$8 || 0, maxsize_$9, 0);
bottomrightradius_$7 = LzKernelUtils.range(bottomrightradius_$7 || 0, maxsize_$9, 0);
toprightradius_$6 = LzKernelUtils.range(toprightradius_$6 || 0, maxsize_$9, 0)
};
var curvemethod_$a = context_$0["curveTo"] ? "curveTo" : "quadraticCurveTo";
context_$0.moveTo(x_$1, y_$2 + topleftradius_$5);
context_$0.lineTo(x_$1, y_$2 + height_$4 - bottomleftradius_$8);
if (bottomleftradius_$8 != 0) {
context_$0[curvemethod_$a](x_$1, y_$2 + height_$4, x_$1 + bottomleftradius_$8, y_$2 + height_$4)
};
context_$0.lineTo(x_$1 + width_$3 - bottomrightradius_$7, y_$2 + height_$4);
if (bottomrightradius_$7 != 0) {
context_$0[curvemethod_$a](x_$1 + width_$3, y_$2 + height_$4, x_$1 + width_$3, y_$2 + height_$4 - bottomrightradius_$7)
};
context_$0.lineTo(x_$1 + width_$3, y_$2 + toprightradius_$6);
if (toprightradius_$6 != 0) {
context_$0[curvemethod_$a](x_$1 + width_$3, y_$2, x_$1 + width_$3 - toprightradius_$6, y_$2)
};
context_$0.lineTo(x_$1 + topleftradius_$5, y_$2);
if (topleftradius_$5 != 0) {
context_$0[curvemethod_$a](x_$1, y_$2, x_$1, y_$2 + topleftradius_$5)
}};
$lzsc$temp["displayName"] = "rect";
return $lzsc$temp
})(), "parselzoptions", (function () {
var $lzsc$temp = function (lzopts_$0) {
var tokens_$1 = lzopts_$0.split(new RegExp("([,()])"));
var KEY_$2 = 1;
var ARGS_$3 = 2;
var options_$4 = {};
var mystate_$5 = KEY_$2;
var vals_$6 = [];
var lastkey_$7 = null;
var nvals_$8 = 0;
while (tokens_$1.length > 0) {
var token_$9 = tokens_$1[0];
var tokens_$1 = tokens_$1.slice(1);
if (token_$9 == "") continue;
switch (mystate_$5) {
case KEY_$2:
if (token_$9 == ",") {
if (lastkey_$7 != null && nvals_$8 == 0) {
options_$4[lastkey_$7] = [true]
}} else if (token_$9 == "(") {
mystate_$5 = ARGS_$3;
vals_$6 = [];
options_$4[lastkey_$7] = vals_$6
} else {
lastkey_$7 = token_$9
}break;;case ARGS_$3:
if (token_$9 == ")") {
lastkey_$7 = null;
mystate_$5 = KEY_$2;
nvals_$8 = 0
} else if (token_$9 == ",") {

} else {
vals_$6.push(token_$9);
nvals_$8++
}break
}};
if (lastkey_$7 != null && nvals_$8 == 0) {
options_$4[lastkey_$7] = [true]
};
return options_$4
};
$lzsc$temp["displayName"] = "parselzoptions";
return $lzsc$temp
})()]);
var LzIdleKernel = {__callbacks: [], __update: (function () {
var $lzsc$temp = function () {
var kernel_$0 = LzIdleKernel;
var callbacks_$1 = kernel_$0.__callbacks;
var now_$2 = LzTimeKernel.getTimer();
for (var i_$3 = callbacks_$1.length - 2;i_$3 >= 0;i_$3 -= 2) {
var scope_$4 = callbacks_$1[i_$3];
var funcname_$5 = callbacks_$1[i_$3 + 1];
scope_$4[funcname_$5](now_$2)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#15/16";
return $lzsc$temp
})(), __intervalID: null, addCallback: (function () {
var $lzsc$temp = function (scope_$0, funcname_$1) {
var kernel_$2 = LzIdleKernel;
var callbacks_$3 = kernel_$2.__callbacks;
for (var i_$4 = callbacks_$3.length - 2;i_$4 >= 0;i_$4 -= 2) {
if (callbacks_$3[i_$4] === scope_$0 && callbacks_$3[i_$4 + 1] == funcname_$1) {
return
}};
(kernel_$2.__callbacks = callbacks_$3.slice(0)).push(scope_$0, funcname_$1);
if (kernel_$2.__intervalID == null) {
kernel_$2.__intervalID = setInterval(LzIdleKernel.__update, 1000 / kernel_$2.__fps)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#29/19";
return $lzsc$temp
})(), removeCallback: (function () {
var $lzsc$temp = function (scope_$0, funcname_$1) {
var kernel_$2 = LzIdleKernel;
var callbacks_$3 = kernel_$2.__callbacks;
for (var i_$4 = callbacks_$3.length - 2;i_$4 >= 0;i_$4 -= 2) {
if (callbacks_$3[i_$4] === scope_$0 && callbacks_$3[i_$4 + 1] == funcname_$1) {
kernel_$2.__callbacks = callbacks_$3 = callbacks_$3.slice(0);
var removed_$5 = callbacks_$3.splice(i_$4, 2);
if (callbacks_$3.length == 0) {
clearInterval(kernel_$2.__intervalID);
kernel_$2.__intervalID = null
};
return removed_$5
}}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#45/22";
return $lzsc$temp
})(), __fps: 30, setFrameRate: (function () {
var $lzsc$temp = function (fps_$0) {
LzIdleKernel.__fps = fps_$0;
if (LzIdleKernel.__intervalID != null) {
clearInterval(LzIdleKernel.__intervalID);
LzIdleKernel.__intervalID = setInterval(LzIdleKernel.__update, 1000 / fps_$0)
}};
$lzsc$temp["displayName"] = "kernel/LzIdleKernel.lzs#67/20";
return $lzsc$temp
})()};
Class.make("LzLibraryCleanup", ["lib", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$lzc$set_libname", (function () {
var $lzsc$temp = function (val_$0) {
this.lib = LzLibrary.findLibrary(val_$0);
this.lib.loadfinished()
};
$lzsc$temp["displayName"] = "$lzc$set_libname";
return $lzsc$temp
})()], LzNode, ["attributes", new LzInheritedHash(LzNode.attributes)]);
var getTimer = (function () {
var $lzsc$temp = function () {
return LzTimeKernel.getTimer()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LFC.js#14/20";
return $lzsc$temp
})();
global = window;
lz.BrowserUtils = {__scopeid: 0, __scopes: [], getcallbackstr: (function () {
var $lzsc$temp = function (scope_$0, name_$1) {
var sc_$2 = lz.BrowserUtils.__scopeid++;
if (scope_$0.__callbacks == null) {
scope_$0.__callbacks = {sc: sc_$2}} else {
scope_$0.__callbacks[sc_$2] = sc_$2
};
lz.BrowserUtils.__scopes[sc_$2] = scope_$0;
return "if (lz.BrowserUtils.__scopes[" + sc_$2 + "]) lz.BrowserUtils.__scopes[" + sc_$2 + "]." + name_$1 + ".apply(lz.BrowserUtils.__scopes[" + sc_$2 + "], [])"
};
$lzsc$temp["displayName"] = "getcallbackstr";
return $lzsc$temp
})(), getcallbackfunc: (function () {
var $lzsc$temp = function (scope_$0, name, args) {
var sc = lz.BrowserUtils.__scopeid++;
if (scope_$0.__callbacks == null) {
scope_$0.__callbacks = {sc: sc}} else {
scope_$0.__callbacks[sc] = sc
};
lz.BrowserUtils.__scopes[sc] = scope_$0;
return (function () {
var $lzsc$temp = function () {
var s_$0 = lz.BrowserUtils.__scopes[sc];
if (s_$0) return s_$0[name].apply(s_$0, args)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzBrowserUtils.js#45/16";
return $lzsc$temp
})()
};
$lzsc$temp["displayName"] = "getcallbackfunc";
return $lzsc$temp
})(), removecallback: (function () {
var $lzsc$temp = function (scope_$0) {
if (scope_$0.__callbacks != null) {
for (var i_$1 in scope_$0.__callbacks) {
var sc_$2 = scope_$0.__callbacks[i_$1];
delete lz.BrowserUtils.__scopes[sc_$2]
};
delete scope_$0.__callbacks
}};
$lzsc$temp["displayName"] = "removecallback";
return $lzsc$temp
})(), hasFeature: (function () {
var $lzsc$temp = function (feature_$0, level_$1) {
return document.implementation && document.implementation.hasFeature && document.implementation.hasFeature(feature_$0, level_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzBrowserUtils.js#65/18";
return $lzsc$temp
})()};
var LzPool = (function () {
var $lzsc$temp = function (getter_$0, cacheHit_$1, destroyer_$2, owner_$3) {
this.cache = {};
if (typeof getter_$0 == "function") this.getter = getter_$0;
if (typeof cacheHit_$1 == "function") this.cacheHit = cacheHit_$1;
if (typeof destroyer_$2 == "function") this.destroyer = destroyer_$2;
if (owner_$3) this.owner = owner_$3
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzPool.js#13/14";
return $lzsc$temp
})();
LzPool.prototype.cache = null;
LzPool.prototype.get = (function () {
var $lzsc$temp = function (id_$0, skipcache_$1) {
var args_$2 = Array.prototype.slice.call(arguments, 2);
var itm_$3 = this.cache[id_$0];
if (skipcache_$1 || itm_$3 == null) {
args_$2.unshift(id_$0);
itm_$3 = this.getter.apply(this, args_$2);
if (!skipcache_$1) this.cache[id_$0] = itm_$3
} else if (this.cacheHit) {
args_$2.unshift(id_$0, itm_$3);
this.cacheHit.apply(this, args_$2)
};
if (this.owner) itm_$3.owner = this.owner;
return itm_$3
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzPool.js#25/24";
return $lzsc$temp
})();
LzPool.prototype.flush = (function () {
var $lzsc$temp = function (id_$0) {
if (this.destroyer) this.destroyer(id_$0, this.cache[id_$0]);
delete this.cache[id_$0]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzPool.js#39/26";
return $lzsc$temp
})();
LzPool.prototype.destroy = (function () {
var $lzsc$temp = function () {
for (var id_$0 in this.cache) {
this.flush(id_$0)
};
this.owner = null;
this.cache = null
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzPool.js#45/28";
return $lzsc$temp
})();
LzPool.prototype.getter = null;
LzPool.prototype.destroyer = null;
LzPool.prototype.cacheHit = null;
var LzKeyboardKernel = {__downKeysHash: {}, __keyboardEvent: (function () {
var $lzsc$temp = function (e_$0) {
if (!e_$0) e_$0 = window.event;
var delta_$1 = {};
var dirty_$2 = false;
var k_$3 = e_$0["keyCode"];
var t_$4 = e_$0.type;
var dh_$5 = LzKeyboardKernel.__downKeysHash;
if (k_$3 >= 0 && k_$3 != 16 && k_$3 != 17 && k_$3 != 18 && k_$3 != 224) {
var s_$6 = String.fromCharCode(k_$3).toLowerCase();
if (t_$4 == "keyup") {
if (dh_$5[s_$6] != null) {
delta_$1[s_$6] = false;
dirty_$2 = true
};
dh_$5[s_$6] = null
} else if (t_$4 == "keydown") {
if (dh_$5[s_$6] == null) {
delta_$1[s_$6] = true;
dirty_$2 = true
};
dh_$5[s_$6] = k_$3
}};
if (LzKeyboardKernel.__updateControlKeys(e_$0, delta_$1)) {
dirty_$2 = true
};
if (dirty_$2) {
var scope_$7 = LzKeyboardKernel.__scope;
var callback_$8 = LzKeyboardKernel.__callback;
if (scope_$7 && scope_$7[callback_$8]) {
scope_$7[callback_$8](delta_$1, k_$3, "on" + t_$4)
}};
if (k_$3 >= 0) {
if (k_$3 == 9) {
e_$0.cancelBubble = true;
return false
} else if (LzKeyboardKernel.__cancelKeys && (k_$3 == 13 || k_$3 == 0 || k_$3 == 37 || k_$3 == 38 || k_$3 == 39 || k_$3 == 40 || k_$3 == 8)) {
e_$0.cancelBubble = true;
return false
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#16/23";
return $lzsc$temp
})(), __updateControlKeys: (function () {
var $lzsc$temp = function (e_$0, delta_$1) {
var quirks_$2 = LzSprite.quirks;
var dh_$3 = LzKeyboardKernel.__downKeysHash;
var dirty_$4 = false;
if (delta_$1) {
var send_$5 = false
} else {
delta_$1 = {};
var send_$5 = true
};
var alt_$6 = e_$0["altKey"];
if (dh_$3["alt"] != null != alt_$6) {
dh_$3["alt"] = alt_$6 ? 18 : null;
delta_$1["alt"] = alt_$6;
dirty_$4 = true;
if (quirks_$2["alt_key_sends_control"]) {
delta_$1["control"] = delta_$1["alt"]
}};
var ctrl_$7 = e_$0["ctrlKey"];
if (dh_$3["control"] != null != ctrl_$7) {
dh_$3["control"] = ctrl_$7 ? 17 : null;
delta_$1["control"] = ctrl_$7;
dirty_$4 = true
};
var shift_$8 = e_$0["shiftKey"];
if (dh_$3["shift"] != null != shift_$8) {
dh_$3["shift"] = shift_$8 ? 16 : null;
delta_$1["shift"] = shift_$8;
dirty_$4 = true
};
if (quirks_$2["hasmetakey"]) {
var meta_$9 = e_$0["metaKey"];
if (dh_$3["meta"] != null != meta_$9) {
dh_$3["meta"] = meta_$9 ? 224 : null;
delta_$1["meta"] = meta_$9;
dirty_$4 = true;
delta_$1["control"] = meta_$9
}};
if (dirty_$4 && send_$5) {
var scope_$a = LzKeyboardKernel.__scope;
var callback_$b = LzKeyboardKernel.__callback;
if (scope_$a && scope_$a[callback_$b]) {
scope_$a[callback_$b](delta_$1, 0, "on" + e_$0.type)
}};
return dirty_$4
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#71/27";
return $lzsc$temp
})(), __allKeysUp: (function () {
var $lzsc$temp = function () {
var delta_$0 = null;
var stuck_$1 = false;
var keys_$2 = null;
var dh_$3 = LzKeyboardKernel.__downKeysHash;
for (var key_$4 in dh_$3) {
if (dh_$3[key_$4] != null) {
stuck_$1 = true;
if (!delta_$0) {
delta_$0 = {}};
delta_$0[key_$4] = false;
if (key_$4.length == 1) {
if (!keys_$2) {
keys_$2 = []
};
keys_$2.push(dh_$3[key_$4])
};
dh_$3[key_$4] = null
}};
var scope_$5 = LzKeyboardKernel.__scope;
var callback_$6 = LzKeyboardKernel.__callback;
if (stuck_$1 && scope_$5 && scope_$5[callback_$6]) {
if (!keys_$2) {
scope_$5[callback_$6](delta_$0, 0, "onkeyup")
} else for (var i_$7 = 0, l_$8 = keys_$2.length;i_$7 < l_$8;i_$7++) {
scope_$5[callback_$6](delta_$0, keys_$2[i_$7], "onkeyup")
}};
LzKeyboardKernel.__downKeysHash = {}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#136/19";
return $lzsc$temp
})(), __callback: null, __scope: null, __cancelKeys: true, __lockFocus: null, setCallback: (function () {
var $lzsc$temp = function (scope_$0, keyboardcallback_$1) {
this.__scope = scope_$0;
this.__callback = keyboardcallback_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#169/19";
return $lzsc$temp
})(), __lastcontrolscope: null, setKeyboardControl: (function () {
var $lzsc$temp = function (dhtmlKeyboardControl_$0, force_$1) {
if (!force_$1 && LzKeyboardKernel.__lockFocus) {
dhtmlKeyboardControl_$0 = true
};
var handler_$2 = null;
var setcontrol_$3 = LzSprite.__rootSprite.options.cancelkeyboardcontrol != true || true;
if (setcontrol_$3 && dhtmlKeyboardControl_$0) {
handler_$2 = LzKeyboardKernel.__keyboardEvent
};
if (LzSprite.quirks.keyboardlistentotop) {
var doc_$4 = window.top.document
} else {
var doc_$4 = document
};
var lastscope_$5 = LzKeyboardKernel.__lastcontrolscope;
if (lastscope_$5 && lastscope_$5 != doc_$4) {
lastscope_$5.onkeydown = lastscope_$5.onkeyup = lastscope_$5.onkeypress = null;
if (handler_$2) {
LzKeyboardKernel.__lastcontrolscope = doc_$4
}};
doc_$4.onkeydown = handler_$2;
doc_$4.onkeyup = handler_$2;
doc_$4.onkeypress = handler_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#174/26";
return $lzsc$temp
})(), gotLastFocus: (function () {
var $lzsc$temp = function () {
if (!LzSprite.__mouseActivationDiv.mouseisover) LzKeyboardKernel.setKeyboardControl(false)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#205/20";
return $lzsc$temp
})(), setGlobalFocusTrap: (function () {
var $lzsc$temp = function (istrapped_$0) {
LzKeyboardKernel.__lockFocus = istrapped_$0;
if (LzSprite.quirks.activate_on_mouseover) {
var activationdiv_$1 = LzSprite.__mouseActivationDiv;
if (istrapped_$0) {
activationdiv_$1.onmouseover()
} else {
activationdiv_$1.onmouseout()
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzKeyboardKernel.js#210/26";
return $lzsc$temp
})()};
var LzMouseKernel = {__lastMouseDown: null, __lastMouseOver: null, __x: 0, __y: 0, owner: null, __showncontextmenu: null, __mouseEvent: (function () {
var $lzsc$temp = function (e_$0) {
e_$0 = e_$0 || window.event;
var target_$1 = e_$0.target || e_$0.srcElement;
var eventname_$2 = "on" + e_$0.type;
if (window["LzKeyboardKernel"] && LzKeyboardKernel["__updateControlKeys"]) {
LzKeyboardKernel.__updateControlKeys(e_$0)
};
if (LzSprite.prototype.capabilities.touchevents) {
var maxfingers_$3 = 1;
if (eventname_$2 === "ontouchstart") {
eventname_$2 = "onmousedown"
} else if (eventname_$2 === "ontouchmove") {
eventname_$2 = "onmousemove"
} else if (eventname_$2 === "ontouchend") {
eventname_$2 = "onmouseup";
maxfingers_$3 = 0
};
if (e_$0.touches.length != maxfingers_$3) {
return true
}};
var lzinputproto_$4 = window["LzInputTextSprite"] && LzInputTextSprite.prototype;
if (lzinputproto_$4 && lzinputproto_$4.__lastshown != null) {
if (LzSprite.quirks.fix_ie_clickable) {
lzinputproto_$4.__hideIfNotFocused(eventname_$2)
} else if (eventname_$2 != "onmousemove") {
lzinputproto_$4.__hideIfNotFocused()
}};
if (eventname_$2 === "onmousemove") {
LzMouseKernel.__sendMouseMove(e_$0);
if (lzinputproto_$4 && lzinputproto_$4.__lastshown != null) {
if (target_$1 && target_$1.owner && !(target_$1.owner instanceof LzInputTextSprite)) {
if (!lzinputproto_$4.__lastshown.__isMouseOver()) {
lzinputproto_$4.__lastshown.__hide()
}}}} else if (eventname_$2 === "oncontextmenu" || e_$0.button == 2) {
return LzMouseKernel.__handleContextMenu(e_$0)
} else {
LzMouseKernel.__sendEvent(eventname_$2)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#23/20";
return $lzsc$temp
})(), __sendEvent: (function () {
var $lzsc$temp = function (eventname_$0, view_$1) {
if (eventname_$0 === "onclick" && view_$1 == null) {
return
};
if (LzMouseKernel.__callback) {
LzMouseKernel.__scope[LzMouseKernel.__callback](eventname_$0, view_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#82/19";
return $lzsc$temp
})(), __callback: null, __scope: null, setCallback: (function () {
var $lzsc$temp = function (scope_$0, funcname_$1) {
this.__scope = scope_$0;
this.__callback = funcname_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#96/19";
return $lzsc$temp
})(), __mousecontrol: false, setMouseControl: (function () {
var $lzsc$temp = function (ison_$0) {
if (ison_$0 === LzMouseKernel.__mousecontrol) return;
LzMouseKernel.__mousecontrol = ison_$0;
LzMouseKernel.__sendEvent(ison_$0 ? "onmouseenter" : "onmouseleave");
var method_$1 = lz.embed[ison_$0 ? "attachEventHandler" : "removeEventHandler"];
if (LzSprite.prototype.capabilities.touchevents) {
method_$1(document, "touchmove", LzMouseKernel, "__mouseEvent")
} else {
method_$1(document, "mousemove", LzMouseKernel, "__mouseEvent")
};
document.oncontextmenu = ison_$0 ? LzMouseKernel.__mouseEvent : null
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#102/23";
return $lzsc$temp
})(), __showhand: "pointer", showHandCursor: (function () {
var $lzsc$temp = function (show_$0) {
var c_$1 = show_$0 === true ? "pointer" : "default";
this.__showhand = c_$1;
LzMouseKernel.setCursorGlobal(c_$1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#131/22";
return $lzsc$temp
})(), setCursorGlobal: (function () {
var $lzsc$temp = function (n_$0) {
if (LzSprite.quirks.no_cursor_colresize) {
return
};
var n_$0 = LzSprite.__defaultStyles.hyphenate(n_$0);
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv", "cursor", n_$0);
LzSprite.prototype.__setCSSClassProperty(".lzdiv", "cursor", n_$0);
LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv", "cursor", n_$0);
LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv", "cursor", n_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#142/23";
return $lzsc$temp
})(), restoreCursor: (function () {
var $lzsc$temp = function () {
if (LzSprite.quirks.no_cursor_colresize) {
return
};
if (LzMouseKernel.__amLocked) return;
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv", "cursor", LzMouseKernel.__showhand);
LzSprite.prototype.__setCSSClassProperty(".lzdiv", "cursor", "default");
LzSprite.prototype.__setCSSClassProperty(".lzcanvasdiv", "cursor", "default");
LzSprite.prototype.__setCSSClassProperty(".lzcanvasclickdiv", "cursor", "default")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#159/21";
return $lzsc$temp
})(), lock: (function () {
var $lzsc$temp = function () {
LzMouseKernel.__amLocked = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#174/12";
return $lzsc$temp
})(), unlock: (function () {
var $lzsc$temp = function () {
LzMouseKernel.__amLocked = false;
LzMouseKernel.restoreCursor()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#182/14";
return $lzsc$temp
})(), disableMouseTemporarily: (function () {
var $lzsc$temp = function () {
this.setGlobalClickable(false);
this.__resetonmouseover = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#187/31";
return $lzsc$temp
})(), __resetonmouseover: false, __resetMouse: (function () {
var $lzsc$temp = function () {
if (this.__resetonmouseover) {
this.__resetonmouseover = false;
this.setGlobalClickable(true)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#192/20";
return $lzsc$temp
})(), __globalClickable: true, setGlobalClickable: (function () {
var $lzsc$temp = function (isclickable_$0) {
if (isclickable_$0 === this.__globalClickable) return;
this.__globalClickable = isclickable_$0;
var el_$1 = document.getElementById("lzcanvasclickdiv");
if (LzSprite.quirks.fix_ie_clickable) {
LzSprite.prototype.__setCSSClassProperty(".lzclickdiv", "display", isclickable_$0 ? "" : "none")
};
if (el_$1) el_$1.style.display = isclickable_$0 ? "" : "none"
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#199/26";
return $lzsc$temp
})(), __sendMouseMove: (function () {
var $lzsc$temp = function (e_$0, offsetx_$1, offsety_$2) {
var sendmousemove_$3 = "mousemove" === e_$0.type;
var returnvalue_$4 = true;
if (LzSprite.prototype.capabilities.touchevents) {
var touches_$5 = e_$0.touches;
var touch_$6 = touches_$5 && touches_$5[0];
if (touch_$6) {
LzMouseKernel.__x = touch_$6.pageX;
LzMouseKernel.__y = touch_$6.pageY
};
sendmousemove_$3 = true;
returnvalue_$4 = false
} else if (e_$0.pageX || e_$0.pageY) {
LzMouseKernel.__x = e_$0.pageX;
LzMouseKernel.__y = e_$0.pageY
} else if (e_$0.clientX || e_$0.clientY) {
var body_$7 = document.body, docElem_$8 = document.documentElement;
LzMouseKernel.__x = e_$0.clientX + body_$7.scrollLeft + docElem_$8.scrollLeft;
LzMouseKernel.__y = e_$0.clientY + body_$7.scrollTop + docElem_$8.scrollTop
};
if (offsetx_$1) {
LzMouseKernel.__x += offsetx_$1
};
if (offsety_$2) {
LzMouseKernel.__y += offsety_$2
};
if (sendmousemove_$3) {
LzMouseKernel.__sendEvent("onmousemove");
return returnvalue_$4
};
return false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#210/23";
return $lzsc$temp
})(), __contextmenumouse2: false, __handleContextMenu: (function () {
var $lzsc$temp = function (e_$0) {
LzMouseKernel.__sendMouseMove(e_$0);
var cmenu_$1 = LzMouseKernel.__findContextMenu(e_$0);
if (cmenu_$1) {
var eventname_$2 = "on" + e_$0.type;
var showbuiltins_$3 = cmenu_$1.kernel.showbuiltins;
var viamouse2_$4 = false;
if (LzSprite.prototype.quirks.has_dom2_mouseevents) {
if (eventname_$2 === "oncontextmenu") {
if (LzMouseKernel.__contextmenumouse2) {
LzMouseKernel.__contextmenumouse2 = false;
return false
}} else if (eventname_$2 === "onmousedown" && e_$0.button == 2) {
viamouse2_$4 = true
} else {
return true
}} else if (eventname_$2 !== "oncontextmenu") {
return true
};
var target_$5 = e_$0.target || e_$0.srcElement;
if (target_$5 && target_$5.owner && showbuiltins_$3 !== true) {
LzMouseKernel.__contextmenumouse2 = viamouse2_$4;
if (LzMouseKernel.__showncontextmenu) {
LzContextMenuKernel.lzcontextmenu.hide()
} else {
cmenu_$1.kernel.__show()
};
return false
}};
return true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#251/27";
return $lzsc$temp
})(), __findContextMenu: (function () {
var $lzsc$temp = function (e_$0) {
var cmenu_$1 = LzSprite.__rootSprite.__contextmenu;
var quirks_$2 = LzSprite.quirks;
if (document.elementFromPoint) {
var swf8mode_$3 = quirks_$2.swf8_contextmenu;
if (window.pageXOffset) {
var x_$4 = e_$0.pageX - window.top.pageXOffset;
var y_$5 = e_$0.pageY - window.top.pageYOffset
} else {
var x_$4 = e_$0.clientX;
var y_$5 = e_$0.clientY
};
var rootdiv_$6 = LzSprite.__rootSprite.__LZdiv;
var arr_$7 = [];
if (quirks_$2.fix_contextmenu) {
arr_$7.push(rootdiv_$6, rootdiv_$6.style.display);
var rootprevZ_$8 = rootdiv_$6.style.zIndex;
rootdiv_$6.style.zIndex = -1000;
var rootclickdiv_$9 = LzSprite.__rootSprite.__LZclickcontainerdiv;
var clickprevZ_$a = rootclickdiv_$9.style.zIndex;
arr_$7.push(rootclickdiv_$9, rootclickdiv_$9.style.display);
rootclickdiv_$9.style.zIndex = -9999
};
do {
var elem_$b = document.elementFromPoint(x_$4, y_$5);
if (!elem_$b) {
break
} else {
var owner_$c = elem_$b.owner;
if (!owner_$c) {

} else if (owner_$c.__contextmenu) {
cmenu_$1 = owner_$c.__contextmenu;
break
} else if (quirks_$2.ie_elementfrompoint && owner_$c.scrolldiv === elem_$b) {

} else if (swf8mode_$3 && (owner_$c.__LZdiv === elem_$b && owner_$c.bgcolor != null || owner_$c instanceof LzTextSprite)) {
break
};
arr_$7.push(elem_$b, elem_$b.style.display);
elem_$b.style.display = "none"
}} while (elem_$b !== rootdiv_$6 && elem_$b.tagName != "HTML");
for (var i_$d = arr_$7.length - 1;i_$d >= 0;i_$d -= 2) {
arr_$7[i_$d - 1].style.display = arr_$7[i_$d]
};
if (quirks_$2.fix_contextmenu) {
rootdiv_$6.style.zIndex = rootprevZ_$8;
rootclickdiv_$9.style.zIndex = clickprevZ_$a
}} else {
var sprite_$e = (e_$0.srcElement || e_$0.target).owner;
if (sprite_$e) {
while (sprite_$e.__parent) {
if (sprite_$e.__contextmenu) {
var mpos_$f = sprite_$e.getMouse();
if (mpos_$f.x >= 0 && mpos_$f.x < sprite_$e.width && mpos_$f.y >= 0 && mpos_$f.y < sprite_$e.height) {
cmenu_$1 = sprite_$e.__contextmenu;
break
}};
sprite_$e = sprite_$e.__parent
}}};
return cmenu_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzMouseKernel.js#303/25";
return $lzsc$temp
})()};
Class.make("LzBrowserKernel", null, null, ["loadURL", (function () {
var $lzsc$temp = function (url_$0, target_$1, features_$2) {
switch (arguments.length) {
case 1:
target_$1 = null;;case 2:
features_$2 = null
};
if (target_$1 != null) {
if (features_$2 != null) {
window.open(url_$0, target_$1, features_$2)
} else {
window.open(url_$0, target_$1)
}} else {
window.location = url_$0
}};
$lzsc$temp["displayName"] = "loadURL";
return $lzsc$temp
})(), "loadJS", (function () {
var $lzsc$temp = function (js_$0, target_$1) {
LzBrowserKernel.loadURL("javascript:" + js_$0 + ";void(0);", target_$1)
};
$lzsc$temp["displayName"] = "loadJS";
return $lzsc$temp
})(), "callJS", (function () {
var $lzsc$temp = function (methodname_$0, callback_$1) {
var scope_$2 = null;
var method_$3 = eval(methodname_$0);
var path_$4 = methodname_$0.split(".");
if (path_$4.length > 1) {
path_$4.pop();
scope_$2 = eval(path_$4.join("."))
};
var args_$5 = Array.prototype.slice.call(arguments, 2);
if (method_$3) {
var ret_$6 = method_$3.apply(scope_$2, args_$5)
};
if (callback_$1 && typeof callback_$1 == "function") callback_$1(ret_$6);
return ret_$6
};
$lzsc$temp["displayName"] = "callJS";
return $lzsc$temp
})(), "setHistory", (function () {
var $lzsc$temp = function (n_$0) {
lz.embed.history.set(n_$0)
};
$lzsc$temp["displayName"] = "setHistory";
return $lzsc$temp
})(), "callMethod", (function () {
var $lzsc$temp = function (js_$0) {
return eval(js_$0)
};
$lzsc$temp["displayName"] = "callMethod";
return $lzsc$temp
})(), "getVersion", (function () {
var $lzsc$temp = function () {
return navigator.userAgent
};
$lzsc$temp["displayName"] = "getVersion";
return $lzsc$temp
})(), "getOS", (function () {
var $lzsc$temp = function () {
return navigator.platform
};
$lzsc$temp["displayName"] = "getOS";
return $lzsc$temp
})(), "getLoadURL", (function () {
var $lzsc$temp = function () {
if (LzSprite.__rootSprite && LzSprite.__rootSprite._url) {
var url_$0 = LzSprite.__rootSprite._url
} else {
var url_$0 = lz.embed.__propcache.url
};
if (!url_$0) url_$0 = new String(window.location);
var colon_$1 = url_$0.indexOf(":");
var slash_$2 = url_$0.indexOf("/");
if (colon_$1 > -1) {
if (url_$0.indexOf("://") == colon_$1) {
return url_$0
} else if (url_$0.charAt(colon_$1 + 1) == "/") {
url_$0 = url_$0.substring(0, colon_$1 + 1) + "/" + url_$0.substring(colon_$1 + 1);
return url_$0
} else {
var lzu_$3 = new LzURL(new String(window.location));
url_$0 = url_$0.substring(0, colon_$1 + 1) + "/" + lzu_$3.path + url_$0.substring(colon_$1 + 1);
return url_$0
}} else {
if (slash_$2 == 0) {
return url_$0
} else {
var loc_$4 = new String(window.location);
var lastslash_$5 = loc_$4.lastIndexOf("/");
loc_$4 = loc_$4.substring(0, lastslash_$5 + 1);
return loc_$4 + url_$0
}}};
$lzsc$temp["displayName"] = "getLoadURL";
return $lzsc$temp
})(), "getInitArg", (function () {
var $lzsc$temp = function (name_$0) {
var initargs_$1 = global;
var id_$2 = LzSprite.__rootSprite._id;
if (id_$2) {
initargs_$1 = lz.embed.applications[id_$2].initargs
};
if (name_$0 == null) return initargs_$1;
return initargs_$1[name_$0]
};
$lzsc$temp["displayName"] = "getInitArg";
return $lzsc$temp
})(), "getLzOption", (function () {
var $lzsc$temp = function (name_$0) {
var options_$1 = global;
var id_$2 = LzSprite.__rootSprite._id;
if (id_$2) {
options_$1 = lz.embed.applications[id_$2].options
};
if (name_$0 == null) return options_$1;
return options_$1[name_$0]
};
$lzsc$temp["displayName"] = "getLzOption";
return $lzsc$temp
})(), "getAppID", (function () {
var $lzsc$temp = function () {
return LzSprite.__rootSprite._id
};
$lzsc$temp["displayName"] = "getAppID";
return $lzsc$temp
})(), "isAAActive", (function () {
var $lzsc$temp = function () {
Debug.warn("LzBrowserKernel.isAAActive not yet fully implemented");
return false
};
$lzsc$temp["displayName"] = "isAAActive";
return $lzsc$temp
})()]);
var LzSprite = (function () {
var $lzsc$temp = function (owner_$0, isroot_$1) {
if (owner_$0 == null) return;
this.constructor = arguments.callee;
this.owner = owner_$0;
this.uid = LzSprite.prototype.uid++;
this.aadescriptionDiv = null;
this.__csscache = {};
var quirks_$2 = this.quirks;
if (isroot_$1) {
this.isroot = true;
LzSprite.__rootSprite = this;
var div = document.createElement("div");
div.className = "lzcanvasdiv";
quirks_$2["scrollbar_width"] = LzSprite._getScrollbarWidth();
if (quirks_$2.ie6_improve_memory_performance) {
try {
document.execCommand("BackgroundImageCache", false, true)
}
catch (err_$3) {}};
var p_$4 = lz.embed.__propcache;
var rootcontainer_$5 = LzSprite.__rootSpriteContainer = p_$4.appenddiv;
var appcontainer_$6 = rootcontainer_$5;
rootcontainer_$5.style.margin = 0;
rootcontainer_$5.style.padding = 0;
rootcontainer_$5.style.border = "0 none";
rootcontainer_$5.style.overflow = "hidden";
rootcontainer_$5.style.textAlign = "left";
if (quirks_$2["container_divs_require_overflow"]) {
appcontainer_$6 = document.createElement("div");
appcontainer_$6.className = "lzappoverflow";
rootcontainer_$5.appendChild(appcontainer_$6);
appcontainer_$6.owner = this;
LzSprite.__rootSpriteOverflowContainer = appcontainer_$6
};
if (quirks_$2.fix_contextmenu) {
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
var options_$8 = p_$4.options;
if (options_$8) {
this.options = options_$8
};
LzSprite.blankimage = options_$8.serverroot + LzSprite.blankimage;
if (quirks_$2.use_css_sprites && options_$8.usemastersprite) {
quirks_$2.use_css_master_sprite = options_$8.usemastersprite;
var mastersprite = LzResourceLibrary && LzResourceLibrary.__allcss && LzResourceLibrary.__allcss.path;
if (mastersprite) {
LzSprite.__masterspriteurl = LzSprite.__rootSprite.options.approot + mastersprite;
var masterspriteimg_$9 = new Image();
masterspriteimg_$9.src = mastersprite;
masterspriteimg_$9.onerror = (function () {
var $lzsc$temp = function () {
Debug.warn("Error loading master sprite:", mastersprite)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#105/47";
return $lzsc$temp
})()
}};
LzSprite.__defaultStyles.writeCSS(quirks_$2.write_css_with_createstylesheet);
appcontainer_$6.appendChild(div);
this.__LZdiv = div;
if (quirks_$2.fix_clickable) {
var cdiv_$a = document.createElement("div");
cdiv_$a.className = "lzcanvasclickdiv";
cdiv_$a.id = "lzcanvasclickdiv";
appcontainer_$6.appendChild(cdiv_$a);
this.__LZclickcontainerdiv = cdiv_$a;
LzSprite.__setClickable(true, appcontainer_$6)
};
if (quirks_$2["css_hide_canvas_during_init"]) {
var cssname_$b = "display";
var cssval_$c = "none";
if (quirks_$2["safari_visibility_instead_of_display"]) {
cssname_$b = "visibility";
cssval_$c = "hidden"
};
this.__LZdiv.style[cssname_$b] = cssval_$c;
if (quirks_$2["fix_clickable"]) this.__LZclickcontainerdiv.style[cssname_$b] = cssval_$c;
if (quirks_$2["fix_contextmenu"]) this.__LZcontextcontainerdiv.style[cssname_$b] = cssval_$c
};
if (quirks_$2.activate_on_mouseover) {
div.mouseisover = false;
div.onmouseover = (function () {
var $lzsc$temp = function (e_$0) {
if (LzSprite.quirks.keyboardlistentotop_in_frame) {
if (LzSprite.__rootSprite.options.cancelkeyboardcontrol != true) {
LzSprite.quirks.keyboardlistentotop = true;
LzKeyboardKernel.setKeyboardControl(true)
}};
if (LzSprite.quirks.focus_on_mouseover) {
if (LzSprite.prototype.getSelectedText() == "") {
div.focus()
}};
if (LzInputTextSprite.prototype.__focusedSprite == null) LzKeyboardKernel.setKeyboardControl(true);
LzMouseKernel.setMouseControl(true);
this.mouseisover = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#142/31";
return $lzsc$temp
})();
div.onmouseout = (function () {
var $lzsc$temp = function (e_$0) {
if (!e_$0) {
e_$0 = window.event;
var el_$1 = e_$0.toElement
} else {
var el_$1 = e_$0.relatedTarget
};
var quirks_$2 = LzSprite.quirks;
if (quirks_$2.inputtext_anonymous_div) {
try {
el_$1 && el_$1.parentNode
}
catch (e_$0) {
return
}};
var mousein_$3 = false;
if (el_$1) {
var cm_$4 = LzContextMenuKernel.lzcontextmenu;
if (el_$1.owner && el_$1.className.indexOf("lz") == 0) {
mousein_$3 = true
} else if (cm_$4 && (el_$1 === cm_$4 || el_$1.parentNode === cm_$4)) {
mousein_$3 = true
}};
if (mousein_$3) {
var wasClickable_$5 = LzMouseKernel.__globalClickable;
if (quirks_$2.fix_ie_clickable) {
LzMouseKernel.setGlobalClickable(true)
};
if (quirks_$2.focus_on_mouseover) {
if (LzInputTextSprite.prototype.__lastshown == null) {
if (LzSprite.prototype.getSelectedText() == "") {
div.focus()
}}};
LzKeyboardKernel.setKeyboardControl(true);
LzMouseKernel.setMouseControl(true);
LzMouseKernel.__resetMouse();
this.mouseisover = true;
if (quirks_$2.fix_clickable && !wasClickable_$5 && LzMouseKernel.__globalClickable) {
var target_$6 = e_$0.target || e_$0.srcElement;
if (target_$6) {
var owner_$7 = target_$6["owner"];
if (LzSprite["$lzsc$isa"] ? LzSprite.$lzsc$isa(owner_$7) : owner_$7 instanceof LzSprite) {
owner_$7 = owner_$7["owner"]
};
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(owner_$7) : owner_$7 instanceof LzView) {
LzMouseKernel.__sendEvent("onmouseout", owner_$7)
}}}} else {
if (quirks_$2.focus_on_mouseover) {
if (LzInputTextSprite.prototype.__lastshown == null) {
if (LzSprite.prototype.getSelectedText() == "") {
div.blur()
}}};
LzKeyboardKernel.setKeyboardControl(false);
LzMouseKernel.setMouseControl(false);
this.mouseisover = false
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#159/30";
return $lzsc$temp
})();
if (LzSprite.quirks.keyboardlistentotop_in_frame) {
window.onfocus = (function () {
var $lzsc$temp = function (e_$0) {
if (LzSprite.__rootSprite.options.cancelkeyboardcontrol != true) {
div.onmouseover()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#248/34";
return $lzsc$temp
})()
};
LzSprite.__mouseActivationDiv = div
};
LzFontManager.__createContainerDiv();
if (quirks_$2.prevent_selection) {
document.onselectstart = (function () {
var $lzsc$temp = function (e_$0) {
if (!e_$0) {
e_$0 = window.event;
var targ_$1 = e_$0.srcElement
} else {
var targ_$1 = e_$0.srcElement.parentNode
};
if (targ_$1.owner instanceof LzTextSprite) {
if (!targ_$1.owner.selectable) {
return false
}} else {
return false
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#266/38";
return $lzsc$temp
})()
}} else {
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lzdiv"
};
this.__LZdiv.owner = this;
if (quirks_$2.ie_leak_prevention) {
this.__sprites[this.uid] = this
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#18/16";
return $lzsc$temp
})();
LzSprite.prototype._dbg_typename = "LzSprite";
LzSprite.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
var div_$0 = this.__LZdiv;
var d_$1 = div_$0;
var x_$2 = 0, y_$3 = 0;
if (d_$1.offsetParent) {
do {
x_$2 += d_$1.offsetLeft;
y_$3 += d_$1.offsetTop
} while (d_$1 = d_$1.offsetParent)
};
return Debug.formatToString("%w/@sprite [%s x %s]*[1 0 %s, 0 1 %s, 0 0 1]", this.owner.sprite === this ? this.owner : "(orphan)", div_$0.offsetWidth || 0, div_$0.offsetHeight || 0, x_$2 || 0, y_$3 || 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#306/36";
return $lzsc$temp
})();
LzSprite.__defaultStyles = {lzdiv: {position: "absolute", borderStyle: "solid", borderWidth: 0}, lzclickdiv: {position: "absolute", borderStyle: "solid", borderColor: "transparent", borderWidth: 0}, lzcanvasdiv: {position: "absolute"}, lzcanvasclickdiv: {zIndex: 100000, position: "absolute"}, lzcanvascontextdiv: {position: "absolute"}, lzappoverflow: {position: "absolute", overflow: "hidden"}, lztextcontainer: {position: "absolute", cursor: "default"}, lzinputtextcontainer: {position: "absolute", overflow: "hidden"}, lzinputtextcontainer_click: {position: "absolute"}, lztext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", whiteSpace: "nowrap", position: "absolute", textAlign: "left", textIndent: 0, letterSpacing: 0, textDecoration: "none"}, lzswftext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", whiteSpace: "nowrap", position: "absolute", lineHeight: "1.2em", textAlign: "left", textIndent: 0, letterSpacing: 0, textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all", padding: "2px"}, lzinputtext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", textAlign: "left", textIndent: 0, letterSpacing: 0, textDecoration: "none", whiteSpace: "nowrap"}, lzswfinputtext: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", lineHeight: "1.2em", textAlign: "left", textIndent: 0, letterSpacing: 0, textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all", outline: "none", paddingTop: "1px", paddingBottom: "3px", paddingRight: "3px", paddingLeft: "1px", whiteSpace: "nowrap"}, lzswfinputtextmultiline: {fontFamily: "Verdana,Vera,sans-serif", fontStyle: "normal", fontWeight: "normal", fontSize: "11px", width: "100%", height: "100%", borderWidth: 0, backgroundColor: "transparent", position: "absolute", overflow: "hidden", lineHeight: "1.2em", textAlign: "left", textIndent: 0, letterSpacing: 0, textDecoration: "none", wordWrap: "break-word", MsWordBreak: "break-all", outline: "none", whiteSpace: "pre-wrap", paddingTop: "1px", paddingBottom: "3px", paddingRight: "3px", paddingLeft: "1px"}, lztextlink: {cursor: "pointer"}, lzaccessibilitydiv: {display: "none"}, lzcontext: {position: "absolute", borderStyle: "solid", borderColor: "transparent", borderWidth: 0}, lzimg: {position: "absolute", backgroundRepeat: "no-repeat", border: "0 none"}, lzgraphicscanvas: {position: "absolute"}, "#lzTextSizeCache": {position: "absolute", top: "-20000px", left: "-20000px"}, writeCSS: (function () {
var $lzsc$temp = function (isIE_$0) {
var rules_$1 = [];
var css_$2 = "";
for (var classname_$3 in this) {
if (classname_$3 == "writeCSS" || classname_$3 == "hyphenate" || classname_$3 == "__replace" || classname_$3 == "__re") continue;
css_$2 += classname_$3.indexOf("#") == -1 ? "." : "";
css_$2 += classname_$3 + "{";
for (var n_$4 in this[classname_$3]) {
var v_$5 = this[classname_$3][n_$4];
css_$2 += this.hyphenate(n_$4) + ":" + v_$5 + ";"
};
css_$2 += "}"
};
css_$2 += LzFontManager.generateCSS();
if (isIE_$0) {
if (!document.styleSheets["lzstyles"]) {
var ss_$6 = document.createStyleSheet();
ss_$6.owningElement.id = "lzstyles";
ss_$6.cssText = css_$2
}} else {
var o_$7 = document.createElement("style");
lz.embed.__setAttr(o_$7, "type", "text/css");
o_$7.appendChild(document.createTextNode(css_$2));
document.getElementsByTagName("head")[0].appendChild(o_$7)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#503/15";
return $lzsc$temp
})(), __re: new RegExp("[A-Z]", "g"), hyphenate: (function () {
var $lzsc$temp = function (n_$0) {
return n_$0.replace(this.__re, this.__replace)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#534/16";
return $lzsc$temp
})(), __replace: (function () {
var $lzsc$temp = function (found_$0) {
return "-" + found_$0.toLowerCase()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#537/16";
return $lzsc$temp
})()};
LzSprite.__styleNames = {borderRadius: "borderRadius", userSelect: "userSelect", transformOrigin: "transformOrigin", transform: "transform", boxShadow: "boxShadow"};
LzSprite.prototype.uid = 0;
LzSprite.quirks = {fix_clickable: true, fix_ie_background_height: false, fix_ie_clickable: false, ie_alpha_image_loader: false, ie_leak_prevention: false, prevent_selection: false, ie_elementfrompoint: false, invisible_parent_image_sizing_fix: false, emulate_flash_font_metrics: true, inner_html_strips_newlines: true, inner_html_no_entity_apos: false, css_hide_canvas_during_init: true, firefox_autocomplete_bug: false, hand_pointer_for_clickable: true, alt_key_sends_control: false, safari_textarea_subtract_scrollbar_height: false, no_cursor_colresize: false, safari_visibility_instead_of_display: false, preload_images_only_once: false, absolute_position_accounts_for_offset: false, canvas_div_cannot_be_clipped: false, inputtext_parents_cannot_contain_clip: false, set_height_for_multiline_inputtext: false, ie_opacity: false, text_measurement_use_insertadjacenthtml: false, text_content_use_inner_text: false, text_selection_use_range: false, document_size_use_offsetheight: false, text_ie_carriagereturn: false, ie_paste_event: false, safari_paste_event: false, text_event_charcode: true, keypress_function_keys: true, ie_timer_closure: false, keyboardlistentotop: false, document_size_compute_correct_height: false, ie_mouse_events: false, activate_on_mouseover: true, ie6_improve_memory_performance: false, text_height_includes_padding: false, inputtext_size_includes_margin: false, listen_for_mouseover_out: true, focus_on_mouseover: true, textstyle_on_textdiv: false, textdeco_on_textdiv: false, use_css_sprites: true, preload_images: true, scrollbar_width: 15, inputtext_strips_newlines: false, swf8_contextmenu: true, inputtext_anonymous_div: false, clipped_scrollbar_causes_display_turd: false, hasmetakey: true, textgrabsinputtextfocus: false, input_highlight_bug: false, autoscroll_textarea: false, fix_contextmenu: true, size_blank_to_zero: true, has_dom2_mouseevents: false, container_divs_require_overflow: false, fix_ie_css_syntax: false, match_swf_letter_spacing: false, use_css_master_sprite: false, write_css_with_createstylesheet: false, inputtext_use_background_image: false, show_img_before_changing_size: false, use_filter_for_dropshadow: false, keyboardlistentotop_in_frame: false, forcemeasurescrollheight: false, resize2dcanvas: false, textmeasurementalphastring: false, textlinksneedmouseevents: false, dont_clip_clickdivs: false, explicitly_set_border_radius: false, prevent_selection_with_onselectstart: false};
LzSprite.prototype.capabilities = {rotation: false, scalecanvastopercentage: false, readcanvassizefromsprite: true, opacity: true, colortransform: false, audio: false, accessibility: true, htmlinputtext: false, advancedfonts: false, bitmapcaching: false, persistence: false, clickregion: false, minimize_opacity_changes: false, history: true, runtimemenus: false, setclipboard: false, proxypolicy: false, linescrolling: false, allowfullscreen: false, setid: true, globalfocustrap: false, "2dcanvas": true, dropshadows: false, cornerradius: false, rgba: false, css2boxmodel: true, medialoading: true, backgroundrepeat: true, touchevents: false, directional_layout: false, scaling: false, customcontextmenu: true};
LzSprite.__updateQuirks = (function () {
var $lzsc$temp = function () {
var quirks_$0 = LzSprite.quirks;
var capabilities_$1 = LzSprite.prototype.capabilities;
var stylenames_$2 = LzSprite.__styleNames;
var defaultStyles_$3 = LzSprite.__defaultStyles;
if (window["lz"] && lz.embed && lz.embed.browser) {
var browser_$4 = lz.embed.browser;
if (browser_$4.isIE) {
if (browser_$4.version < 7) {
quirks_$0["ie_alpha_image_loader"] = true;
quirks_$0["document_size_compute_correct_height"] = true;
quirks_$0["ie6_improve_memory_performance"] = true
} else {
quirks_$0["prevent_selection"] = true;
quirks_$0["invisible_parent_image_sizing_fix"] = true;
if (browser_$4.osversion >= 6) {
quirks_$0["ie_alpha_image_loader"] = true
};
if (browser_$4.version > 7) {
quirks_$0["resize2dcanvas"] = true
}};
quirks_$0["ie_opacity"] = true;
quirks_$0["ie_timer_closure"] = true;
quirks_$0["ie_leak_prevention"] = true;
quirks_$0["fix_ie_clickable"] = true;
quirks_$0["fix_ie_background_height"] = true;
quirks_$0["inner_html_no_entity_apos"] = true;
capabilities_$1["minimize_opacity_changes"] = true;
quirks_$0["set_height_for_multiline_inputtext"] = true;
quirks_$0["text_measurement_use_insertadjacenthtml"] = true;
quirks_$0["text_content_use_inner_text"] = true;
quirks_$0["text_selection_use_range"] = true;
quirks_$0["text_ie_carriagereturn"] = true;
quirks_$0["ie_paste_event"] = true;
quirks_$0["keypress_function_keys"] = false;
quirks_$0["text_event_charcode"] = false;
quirks_$0["ie_mouse_events"] = true;
quirks_$0["inputtext_size_includes_margin"] = true;
quirks_$0["focus_on_mouseover"] = false;
quirks_$0["textstyle_on_textdiv"] = true;
quirks_$0["use_css_sprites"] = !quirks_$0["ie_alpha_image_loader"];
quirks_$0["textgrabsinputtextfocus"] = true;
quirks_$0["ie_elementfrompoint"] = true;
quirks_$0["fix_ie_css_syntax"] = true;
quirks_$0["write_css_with_createstylesheet"] = true;
quirks_$0["hasmetakey"] = false;
quirks_$0["inputtext_use_background_image"] = true;
quirks_$0["show_img_before_changing_size"] = true;
quirks_$0["use_filter_for_dropshadow"] = true;
quirks_$0["forcemeasurescrollheight"] = true;
defaultStyles_$3["lzswfinputtext"].resize = "none";
defaultStyles_$3["lzswfinputtextmultiline"].resize = "none";
capabilities_$1["dropshadows"] = true;
defaultStyles_$3["#lzTextSizeCache"].zoom = 1;
defaultStyles_$3["#lzTextSizeCache"].position = "relative";
quirks_$0["prevent_selection_with_onselectstart"] = true
} else if (browser_$4.isSafari || browser_$4.isChrome) {
stylenames_$2.borderRadius = "WebkitBorderRadius";
stylenames_$2.borderTopLeftRadius = "WebkitBorderTopLeftRadius";
stylenames_$2.borderTopRightRadius = "WebkitBorderTopRightRadius";
stylenames_$2.borderBottomRightRadius = "WebkitBorderBottomRightRadius";
stylenames_$2.borderBottomLeftRadius = "WebkitBorderBottomLeftRadius";
stylenames_$2.boxShadow = "WebkitBoxShadow";
stylenames_$2.userSelect = "WebkitUserSelect";
stylenames_$2.transform = "WebkitTransform";
stylenames_$2.transformOrigin = "WebkitTransformOrigin";
quirks_$0["safari_visibility_instead_of_display"] = true;
quirks_$0["absolute_position_accounts_for_offset"] = true;
if (browser_$4.version < 525.18) {
quirks_$0["canvas_div_cannot_be_clipped"] = true;
quirks_$0["invisible_parent_image_sizing_fix"] = true;
quirks_$0["safari_textarea_subtract_scrollbar_height"] = true
};
quirks_$0["document_size_use_offsetheight"] = true;
if (browser_$4.version > 523.1) {
capabilities_$1["rotation"] = true;
capabilities_$1["scaling"] = true;
capabilities_$1["dropshadows"] = true;
capabilities_$1["cornerradius"] = true;
quirks_$0["explicitly_set_border_radius"] = true;
capabilities_$1["rgba"] = true
};
quirks_$0["safari_paste_event"] = true;
quirks_$0["keypress_function_keys"] = false;
if (browser_$4.version < 523.15) {
quirks_$0["keyboardlistentotop"] = true
};
if (window.top !== window) {
quirks_$0["keyboardlistentotop_in_frame"] = true
};
if (browser_$4.version >= 530.19) {
capabilities_$1["threedtransform"] = true
};
if (browser_$4.isIphone) {
quirks_$0["canvas_div_cannot_be_clipped"] = true;
capabilities_$1["touchevents"] = true
};
quirks_$0["inputtext_strips_newlines"] = true;
quirks_$0["prevent_selection"] = true;
quirks_$0["container_divs_require_overflow"] = true;
quirks_$0["forcemeasurescrollheight"] = true;
defaultStyles_$3.lzswfinputtext.paddingTop = "0px";
defaultStyles_$3.lzswfinputtext.paddingBottom = "2px";
defaultStyles_$3.lzswfinputtext.paddingLeft = "1px";
defaultStyles_$3.lzswfinputtext.paddingRight = "3px";
defaultStyles_$3.lzswfinputtextmultiline.paddingTop = "2px";
defaultStyles_$3.lzswfinputtextmultiline.paddingBottom = "2px";
defaultStyles_$3.lzswfinputtextmultiline.paddingLeft = "2px";
defaultStyles_$3.lzswfinputtextmultiline.paddingRight = "2px"
} else if (browser_$4.isOpera) {
quirks_$0["invisible_parent_image_sizing_fix"] = true;
quirks_$0["no_cursor_colresize"] = true;
quirks_$0["absolute_position_accounts_for_offset"] = true;
quirks_$0["canvas_div_cannot_be_clipped"] = true;
quirks_$0["document_size_use_offsetheight"] = true;
quirks_$0["text_event_charcode"] = false;
quirks_$0["textdeco_on_textdiv"] = true;
quirks_$0["text_ie_carriagereturn"] = true;
quirks_$0["textmeasurementalphastring"] = true;
if (browser_$4.version >= 10.6) {
defaultStyles_$3.lzswftext.wordWrap = "normal";
defaultStyles_$3.lzswfinputtext.wordWrap = "normal";
defaultStyles_$3.lzswfinputtextmultiline.wordWrap = "normal";
quirks_$0["dont_clip_clickdivs"] = true
}} else if (browser_$4.isFirefox) {
stylenames_$2.borderRadius = "MozBorderRadius";
stylenames_$2.boxShadow = "MozBoxShadow";
stylenames_$2.userSelect = "MozUserSelect";
stylenames_$2.transform = "MozTransform";
stylenames_$2.transformOrigin = "MozTransformOrigin";
quirks_$0["inputtext_anonymous_div"] = true;
if (browser_$4.OS == "Windows") {
quirks_$0["clipped_scrollbar_causes_display_turd"] = true;
quirks_$0["input_highlight_bug"] = true
};
if (browser_$4.version < 2) {
quirks_$0["firefox_autocomplete_bug"] = true
} else if (browser_$4.version < 3) {
defaultStyles_$3.lzswftext.lineHeight = "119%";
defaultStyles_$3.lzswfinputtext.lineHeight = "119%";
defaultStyles_$3.lzswfinputtextmultiline.lineHeight = "119%"
} else if (browser_$4.version < 4) {
if (browser_$4.subversion < 6) {
quirks_$0["text_height_includes_padding"] = true
};
if (browser_$4.version < 3.5) {
quirks_$0["container_divs_require_overflow"] = true
}};
quirks_$0["autoscroll_textarea"] = true;
if (browser_$4.version >= 3.5) {
capabilities_$1["rotation"] = true;
capabilities_$1["scaling"] = true
};
if (browser_$4.version >= 3.1) {
capabilities_$1["dropshadows"] = true;
capabilities_$1["cornerradius"] = true;
capabilities_$1["rgba"] = true
}};
if (browser_$4.OS == "Mac") {
quirks_$0["detectstuckkeys"] = true;
quirks_$0["alt_key_sends_control"] = true;
quirks_$0["match_swf_letter_spacing"] = true
};
if (browser_$4.OS == "Android") {
capabilities_$1["touchevents"] = true
};
if (quirks_$0["hand_pointer_for_clickable"]) {
defaultStyles_$3.lzclickdiv.cursor = "pointer"
};
if (quirks_$0["inner_html_strips_newlines"] == true) {
LzSprite.prototype.inner_html_strips_newlines_re = RegExp("$", "mg")
};
defaultStyles_$3.lzimg[stylenames_$2.userSelect] = "none";
if (capabilities_$1.rotation) {
defaultStyles_$3.lzdiv[stylenames_$2.transformOrigin] = "0 0"
};
if (quirks_$0["inputtext_use_background_image"]) {
defaultStyles_$3.lzinputtext["background"] = defaultStyles_$3.lzswfinputtext["background"] = defaultStyles_$3.lzswfinputtextmultiline["background"] = "url(" + LzSprite.blankimage + ")"
};
LzSprite.prototype.br_to_newline_re = RegExp("<br/>", "mg");
if (lz.BrowserUtils.hasFeature("mouseevents", "2.0")) {
quirks_$0["has_dom2_mouseevents"] = true
};
if (quirks_$0["match_swf_letter_spacing"]) {
defaultStyles_$3.lzswftext.letterSpacing = defaultStyles_$3.lzswfinputtext.letterSpacing = defaultStyles_$3.lzswfinputtextmultiline.letterSpacing = "0.01em"
}};
LzSprite.prototype.quirks = quirks_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#684/27";
return $lzsc$temp
})();
LzSprite._getScrollbarWidth = (function () {
var $lzsc$temp = function () {
var div_$0 = document.createElement("div");
div_$0.style.width = "50px";
div_$0.style.height = "50px";
div_$0.style.overflow = "hidden";
div_$0.style.position = "absolute";
div_$0.style.top = "-200px";
div_$0.style.left = "-200px";
var div2_$1 = document.createElement("div");
div2_$1.style.height = "100px";
div_$0.appendChild(div2_$1);
var body_$2 = document.body;
body_$2.appendChild(div_$0);
var w1_$3 = div_$0.clientWidth;
div_$0.style.overflowY = "scroll";
var w2_$4 = div_$0.clientWidth;
LzSprite.prototype.__discardElement(div_$0);
return w1_$3 - w2_$4
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1009/31";
return $lzsc$temp
})();
LzSprite.__updateQuirks();
LzSprite.setRootX = (function () {
var $lzsc$temp = function (v_$0) {
var rootcontainer_$1 = LzSprite.__rootSpriteContainer;
rootcontainer_$1.style.position = "absolute";
rootcontainer_$1.style.left = LzSprite.prototype.CSSDimension(v_$0);
setTimeout(LzScreenKernel.__resizeEvent, 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1046/21";
return $lzsc$temp
})();
LzSprite.setRootWidth = (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.__rootSpriteContainer.style.width = LzSprite.prototype.CSSDimension(v_$0);
setTimeout(LzScreenKernel.__resizeEvent, 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1060/25";
return $lzsc$temp
})();
LzSprite.setRootY = (function () {
var $lzsc$temp = function (v_$0) {
var rootcontainer_$1 = LzSprite.__rootSpriteContainer;
rootcontainer_$1.style.position = "absolute";
rootcontainer_$1.style.top = LzSprite.prototype.CSSDimension(v_$0);
setTimeout(LzScreenKernel.__resizeEvent, 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1072/21";
return $lzsc$temp
})();
LzSprite.setRootHeight = (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.__rootSpriteContainer.style.height = LzSprite.prototype.CSSDimension(v_$0);
setTimeout(LzScreenKernel.__resizeEvent, 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1086/26";
return $lzsc$temp
})();
LzSprite.prototype.__LZdiv = null;
LzSprite.prototype.__LZimg = null;
LzSprite.prototype.__LZclick = null;
LzSprite.prototype.x = null;
LzSprite.prototype.y = null;
LzSprite.prototype.opacity = 1;
LzSprite.prototype.width = null;
LzSprite.prototype.height = null;
LzSprite.prototype.playing = false;
LzSprite.prototype.clickable = false;
LzSprite.prototype.frame = 1;
LzSprite.prototype.frames = null;
LzSprite.blankimage = "lps/includes/blank.gif";
LzSprite.prototype.resource = null;
LzSprite.prototype.source = null;
LzSprite.prototype.visible = true;
LzSprite.prototype.clip = null;
LzSprite.prototype.stretches = null;
LzSprite.prototype.resourceWidth = null;
LzSprite.prototype.resourceHeight = null;
LzSprite.prototype.cursor = null;
LzSprite.prototype._w = "0pt";
LzSprite.prototype._h = "0pt";
LzSprite.prototype.__LZcontext = null;
LzSprite.prototype.initted = false;
LzSprite.prototype.init = (function () {
var $lzsc$temp = function (v_$0) {
this.setVisible(v_$0);
if (this.isroot) {
if (this.quirks["css_hide_canvas_during_init"]) {
var cssname_$1 = "display";
if (this.quirks["safari_visibility_instead_of_display"]) {
cssname_$1 = "visibility"
};
this.__LZdiv.style[cssname_$1] = "";
if (this.quirks["fix_clickable"]) this.__LZclickcontainerdiv.style[cssname_$1] = "";
if (this.quirks["fix_contextmenu"]) this.__LZcontextcontainerdiv.style[cssname_$1] = ""
};
if (this._id) {
lz.embed[this._id]._ready(this.owner)
}};
this.initted = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1143/27";
return $lzsc$temp
})();
LzSprite.prototype.__topZ = 1;
LzSprite.prototype.__parent = null;
LzSprite.prototype.__children = null;
LzSprite.__warnonce = {};
LzSprite.prototype.addChildSprite = (function () {
var $lzsc$temp = function (sprite_$0) {
if (sprite_$0.__parent != null) return;
if (this.stretches != null && LzSprite.__warnonce.stretches != true) {
Debug.warn("Due to limitations in the DHTML runtime, stretches will only apply to the view %w, and doesn't affect child views.", this.owner);
LzSprite.__warnonce.stretches = true
};
sprite_$0.__parent = this;
if (this.__children) {
this.__children.push(sprite_$0)
} else {
this.__children = [sprite_$0]
};
this.__LZdiv.appendChild(sprite_$0.__LZdiv);
if (sprite_$0.__LZclickcontainerdiv) {
if (!this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv = this.__createContainerDivs("click")
};
this.__LZclickcontainerdiv.appendChild(sprite_$0.__LZclickcontainerdiv)
};
sprite_$0.__setZ(++this.__topZ)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1185/37";
return $lzsc$temp
})();
LzSprite.prototype.setResource = (function () {
var $lzsc$temp = function (r_$0) {
if (this.resource == r_$0) return;
this.resource = r_$0;
if (r_$0.indexOf("http:") == 0 || r_$0.indexOf("https:") == 0) {
this.skiponload = false;
this.setSource(r_$0);
return
};
var res_$1 = LzResourceLibrary[r_$0];
if (res_$1) {
this.resourceWidth = res_$1.width;
this.resourceHeight = res_$1.height;
if (this.quirks.use_css_sprites) {
if (this.quirks.use_css_master_sprite && res_$1.spriteoffset != null) {
this.__csssprite = LzSprite.__masterspriteurl;
this.__cssspriteoffset = res_$1.spriteoffset
} else if (res_$1.sprite) {
this.__csssprite = this.getBaseUrl(res_$1) + res_$1.sprite;
this.__cssspriteoffset = 0
}} else {
this.__csssprite = null;
if (this.__bgimage) this.__setBGImage(null)
}};
var urls_$2 = this.getResourceUrls(r_$0);
this.owner.resourceevent("totalframes", urls_$2.length);
this.frames = urls_$2;
if (this.quirks.preload_images && !(this.stretches == null && this.__csssprite)) {
this.__preloadFrames()
};
this.skiponload = true;
this.setSource(urls_$2[0], true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1215/34";
return $lzsc$temp
})();
LzSprite.prototype.getResourceUrls = (function () {
var $lzsc$temp = function (resourcename_$0) {
var urls_$1 = [];
var res_$2 = LzResourceLibrary[resourcename_$0];
if (!res_$2) {
Debug.warn("Could not find resource named %#s", resourcename_$0);
return urls_$1
};
var baseurl_$3 = this.getBaseUrl(res_$2);
for (var i_$4 = 0;i_$4 < res_$2.frames.length;i_$4++) {
urls_$1[i_$4] = baseurl_$3 + res_$2.frames[i_$4]
};
return urls_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1257/38";
return $lzsc$temp
})();
LzSprite.prototype.getBaseUrl = (function () {
var $lzsc$temp = function (resource_$0) {
return LzSprite.__rootSprite.options[resource_$0.ptype == "sr" ? "serverroot" : "approot"]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1277/33";
return $lzsc$temp
})();
LzSprite.prototype.CSSDimension = LzKernelUtils.CSSDimension;
LzSprite.prototype.loading = false;
LzSprite.prototype.setSource = (function () {
var $lzsc$temp = function (url_$0, usecache_$1) {
if (url_$0 == null || url_$0 == "null") {
this.unload();
return
};
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && url_$0 != null) {
this.__restoreSize()
}};
if (usecache_$1 == "reset") {
usecache_$1 = false
} else if (usecache_$1 != true) {
this.skiponload = false;
this.resource = url_$0;
if (this.playing) this.stop();
this.__updateLoadStatus(0);
this.__csssprite = null;
if (this.__bgimage) this.__setBGImage(null)
};
if (usecache_$1 == "memorycache") {
usecache_$1 = true
};
if (this.loading) {
if (this.__ImgPool && this.source) {
this.__ImgPool.flush(this.source)
};
this.__destroyImage(null, this.__LZimg);
this.__LZimg = null
};
this.source = url_$0;
if (this.backgroundrepeat) {
this.__createIMG();
this.__setBGImage(url_$0);
this.__updateBackgroundRepeat();
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: this.skiponload});
return
} else if (this.stretches == null && this.__csssprite) {
this.__createIMG();
this.__updateStretches();
this.__setBGImage(this.__csssprite);
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
var im_$2 = this.__ImgPool.get(url_$0, usecache_$1 != true);
this.__bindImage(im_$2);
if (this.loading) {
if (this.skiponload && this.quirks.ie_alpha_image_loader) this.__updateIEAlpha(im_$2)
} else {
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(im_$2)
} else if (this.stretches) {
this.__updateStretches()
}};
if (this.clickable) this.setClickable(true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1288/32";
return $lzsc$temp
})();
LzSprite.prototype.__bindImage = (function () {
var $lzsc$temp = function (im_$0) {
if (this.__LZimg && this.__LZimg.owner) {
this.__LZdiv.replaceChild(im_$0, this.__LZimg);
this.__LZimg = im_$0
} else {
this.__LZimg = im_$0;
this.__LZdiv.appendChild(this.__LZimg)
};
if (this.cornerradius != null) {
this.__applyCornerRadius(im_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1374/34";
return $lzsc$temp
})();
LzSprite.prototype.__setBGImage = (function () {
var $lzsc$temp = function (url_$0) {
if (this.__LZimg) {
var bgurl_$1 = url_$0 ? "url('" + url_$0 + "')" : null;
this.__bgimage = this.__LZimg.style.backgroundImage = bgurl_$1
};
if (bgurl_$1 != null) {
var y_$2 = -this.__cssspriteoffset || 0;
this.__LZimg.style.backgroundPosition = "0 " + y_$2 + "px"
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1389/35";
return $lzsc$temp
})();
LzSprite.prototype.__createIMG = (function () {
var $lzsc$temp = function () {
if (!this.__LZimg) {
var im_$0 = document.createElement("img");
im_$0.className = "lzimg";
im_$0.owner = this;
im_$0.src = LzSprite.blankimage;
this.__bindImage(im_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1400/34";
return $lzsc$temp
})();
if (LzSprite.quirks.ie_alpha_image_loader) {
LzSprite.prototype.__updateIEAlpha = (function () {
var $lzsc$temp = function (who_$0) {
var w_$1 = this.resourceWidth;
var h_$2 = this.resourceHeight;
if (this.stretches == "both") {
w_$1 = "100%";
h_$2 = "100%"
} else if (this.stretches == "width") {
w_$1 = "100%"
} else if (this.stretches == "height") {
h_$2 = "100%"
};
if (w_$1 == null) w_$1 = this.width == null ? "100%" : this.CSSDimension(this.width);
if (h_$2 == null) h_$2 = this.height == null ? "100%" : this.CSSDimension(this.height);
who_$0.style.width = w_$1;
who_$0.style.height = h_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1417/42";
return $lzsc$temp
})()
};
LzSprite.prototype.setClickable = (function () {
var $lzsc$temp = function (c_$0) {
c_$0 = !(!c_$0);
if (this.clickable === c_$0) return;
if (!this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv = this.__createContainerDivs("click")
};
if (this.__LZimg != null) {
if (!this.__LZclick) {
if (this.quirks.fix_ie_clickable) {
this.__LZclick = document.createElement("img");
this.__LZclick.src = LzSprite.blankimage
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
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable) {
var clickstyle_$1 = c_$0 && this.visible ? "" : "none";
this.__LZclickcontainerdiv.style.display = clickstyle_$1;
this.__LZclick.style.display = clickstyle_$1
} else {
this.__LZclick.style.display = c_$0 ? "" : "none"
}}} else {
if (this.quirks.fix_clickable) {
if (!this.__LZclick) {
if (this.quirks.fix_ie_clickable) {
this.__LZclick = document.createElement("img");
this.__LZclick.src = LzSprite.blankimage
} else {
this.__LZclick = document.createElement("div")
};
this.__LZclick.owner = this;
this.__LZclick.className = "lzclickdiv";
this.__LZclick.style.width = this._w;
this.__LZclick.style.height = this._h;
this.__LZclickcontainerdiv.appendChild(this.__LZclick)
};
if (this.quirks.fix_ie_clickable) {
this.__LZclick.style.display = c_$0 && this.visible ? "" : "none"
} else {
this.__LZclick.style.display = c_$0 ? "" : "none"
}}};
this.clickable = c_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1440/35";
return $lzsc$temp
})();
LzSprite.__setClickable = (function () {
var $lzsc$temp = function (c_$0, div_$1) {
if (div_$1._clickable === c_$0) return;
div_$1._clickable = c_$0;
var f_$2 = c_$0 ? LzSprite.__clickDispatcher : null;
if (LzSprite.prototype.capabilities.touchevents) {
div_$1.ontouchstart = f_$2;
div_$1.ontouchmove = f_$2;
div_$1.ontouchend = f_$2
} else {
div_$1.onclick = f_$2;
div_$1.onmousedown = f_$2;
div_$1.onmouseup = f_$2;
div_$1.onmousemove = f_$2;
if (LzSprite.quirks.listen_for_mouseover_out) {
div_$1.onmouseover = f_$2;
div_$1.onmouseout = f_$2
};
if (LzSprite.quirks.ie_mouse_events) {
div_$1.ondrag = f_$2;
div_$1.ondblclick = f_$2
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1517/27";
return $lzsc$temp
})();
LzSprite.__clickDispatcher = (function () {
var $lzsc$temp = function (e_$0) {
e_$0 = e_$0 || window.event;
e_$0.cancelBubble = true;
if (e_$0.button === 2) {
return LzMouseKernel.__handleContextMenu(e_$0)
};
if (LzKeyboardKernel && LzKeyboardKernel["__updateControlKeys"]) {
LzKeyboardKernel.__updateControlKeys(e_$0);
if (LzKeyboardKernel.__cancelKeys) {
e_$0.cancelBubble = true
}};
if (LzMouseKernel.__sendMouseMove(e_$0)) {
return
};
var target_$1 = e_$0.target || e_$0.srcElement;
var owner_$2 = target_$1 && target_$1.owner;
if (!owner_$2) {
return
};
var eventname_$3 = "on" + e_$0.type;
if (LzSprite.quirks.ie_mouse_events) {
if (eventname_$3 === "onmouseenter") {
eventname_$3 = "onmouseover"
} else if (eventname_$3 === "onmouseleave") {
eventname_$3 = "onmouseout"
} else if (eventname_$3 === "ondblclick") {
owner_$2.__mouseEvent("onmousedown");
owner_$2.__mouseEvent("onmouseup");
owner_$2.__mouseEvent("onclick");
return false
} else if (eventname_$3 === "ondrag") {
return false
}};
if (LzSprite.prototype.capabilities.touchevents) {
if (eventname_$3 === "ontouchstart") {
if (e_$0.touches.length != 1) {
return true
};
eventname_$3 = "onmousedown";
owner_$2.__mouseEvent("onmouseover");
owner_$2.__mouseEvent("onmousedown")
} else if (eventname_$3 === "ontouchmove") {
if (e_$0.touches.length != 1) {
return true
};
LzMouseKernel.__sendMouseMove(e_$0);
eventname_$3 = "onmousemove"
} else if (eventname_$3 === "ontouchend") {
if (e_$0.touches.length != 0) {
return true
};
eventname_$3 = "onmouseup";
e_$0.cancelBubble = false;
var callback_$4 = lz.BrowserUtils.getcallbackfunc(owner_$2, "__mouseEvent", ["onmouseout"]);
setTimeout(callback_$4, 0);
var callback_$4 = lz.BrowserUtils.getcallbackfunc(owner_$2, "__mouseEvent", ["onclick"]);
setTimeout(callback_$4, 0)
} else {
return true
}};
if (owner_$2.isroot === true) {
if (eventname_$3 === "onmouseup") {
var lastmousedown_$5 = LzMouseKernel.__lastMouseDown;
if (lastmousedown_$5 && lastmousedown_$5 !== owner_$2) {
lastmousedown_$5.__globalmouseup(e_$0)
};
var focusedsprite_$6 = LzInputTextSprite.prototype.__focusedSprite;
if (focusedsprite_$6 && focusedsprite_$6 !== owner_$2) {
focusedsprite_$6.deselect()
}};
LzMouseKernel.__sendEvent(eventname_$3, null);
return false
};
if (owner_$2 instanceof LzInputTextSprite && owner_$2.selectable === true) {
if (eventname_$3 === "onmousedown") {
LzInputTextSprite.prototype.__focusedSprite = owner_$2
} else if (eventname_$3 === "onmouseout") {
if (!owner_$2.__isMouseOver()) {
owner_$2.__hide()
}} else {
owner_$2.__show()
};
if (eventname_$3 !== "onmouseout") {
owner_$2.__mouseEvent(eventname_$3)
};
return
};
if (eventname_$3 == "onmousedown") {
owner_$2.__mouseisdown = true;
LzMouseKernel.__lastMouseDown = owner_$2
} else if (eventname_$3 === "onmouseup") {
var lastmousedown_$5 = LzMouseKernel.__lastMouseDown;
if (lastmousedown_$5 && lastmousedown_$5 !== owner_$2) {
lastmousedown_$5.__globalmouseup(e_$0)
};
var focusedsprite_$6 = LzInputTextSprite.prototype.__focusedSprite;
if (focusedsprite_$6 && focusedsprite_$6 !== owner_$2) {
focusedsprite_$6.deselect()
};
if (LzMouseKernel.__lastMouseDown !== owner_$2) {
return
} else {
owner_$2.__mouseisdown = false;
LzMouseKernel.__lastMouseDown = null
};
e_$0.cancelBubble = false
};
return owner_$2.__mouseEvent(eventname_$3) || false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1549/30";
return $lzsc$temp
})();
LzSprite.prototype.__mouseisdown = false;
LzSprite.prototype.__mouseEvent = (function () {
var $lzsc$temp = function (eventname_$0) {
if (eventname_$0 == "onmousedown") {
var focusedsprite_$1 = LzInputTextSprite.prototype.__focusedSprite;
if (focusedsprite_$1 && focusedsprite_$1 != this) {
focusedsprite_$1.deselect()
}} else if (eventname_$0 == "onmouseover") {
LzMouseKernel.__lastMouseOver = this;
if (this.quirks.activate_on_mouseover) {
var activationdiv_$2 = LzSprite.__mouseActivationDiv;
if (!activationdiv_$2.mouseisover) {
activationdiv_$2.onmouseover()
}}};
if (this.owner.mouseevent) {
if (LzMouseKernel.__lastMouseDown) {
if (eventname_$0 === "onmouseover" || eventname_$0 === "onmouseout") {
var sendevents_$3 = LzMouseKernel.__lastMouseDown === this;
if (eventname_$0 == "onmouseover") {
LzMouseKernel.__lastMouseOver = this
} else if (sendevents_$3 && LzMouseKernel.__lastMouseOver === this) {
LzMouseKernel.__lastMouseOver = null
};
if (sendevents_$3) {
LzMouseKernel.__sendEvent(eventname_$0, this.owner);
var dragname_$4 = eventname_$0 == "onmouseover" ? "onmousedragin" : "onmousedragout";
LzMouseKernel.__sendEvent(dragname_$4, this.owner)
};
return
}};
if (this.quirks.fix_clickable && !LzMouseKernel.__globalClickable) {
if (lz["html"] && this.owner && (lz.html["$lzsc$isa"] ? lz.html.$lzsc$isa(this.owner) : this.owner instanceof lz.html) && (eventname_$0 == "onmouseout" || eventname_$0 == "onmouseover")) {
return
}};
LzMouseKernel.__sendEvent(eventname_$0, this.owner)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1745/35";
return $lzsc$temp
})();
LzSprite.prototype.__isMouseOver = (function () {
var $lzsc$temp = function (e_$0) {
var p_$1 = this.getMouse();
var visible_$2 = this.__findParents("visible", false, true);
if (visible_$2.length) return false;
return p_$1.x >= 0 && p_$1.y >= 0 && p_$1.x < this.width && p_$1.y < this.height
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1815/36";
return $lzsc$temp
})();
LzSprite.prototype.__globalmouseup = (function () {
var $lzsc$temp = function (e_$0) {
if (this.__mouseisdown) {
this.__mouseisdown = false;
LzMouseKernel.__sendMouseMove(e_$0);
this.__mouseEvent("onmouseup");
this.__mouseEvent("onmouseupoutside")
};
if (LzMouseKernel.__lastMouseOver) {
LzMouseKernel.__lastMouseOver.__mouseEvent("onmouseover")
};
LzMouseKernel.__lastMouseDown = null
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1827/38";
return $lzsc$temp
})();
LzSprite.prototype.xoffset = 0;
LzSprite.prototype._xoffset = 0;
LzSprite.prototype.setX = (function () {
var $lzsc$temp = function (x_$0) {
if (x_$0 == null || x_$0 == this.x && this._xoffset == this.xoffset) return;
this.__poscacheid = -1;
this._xoffset = this.xoffset;
this.x = x_$0;
x_$0 = this.CSSDimension(x_$0 + this.xoffset);
if (this._x != x_$0) {
this._x = x_$0;
this.__LZdiv.style.left = x_$0;
if (this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style.left = x_$0
};
if (this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.left = x_$0
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1850/27";
return $lzsc$temp
})();
LzSprite.prototype.setWidth = (function () {
var $lzsc$temp = function (w_$0) {
if (w_$0 < 0 || this.width == w_$0) return;
this.width = w_$0;
w_$0 = this.CSSDimension(w_$0);
if (this._w != w_$0) {
this._w = w_$0;
var size_$1 = w_$0;
var quirks_$2 = this.quirks;
if (quirks_$2.size_blank_to_zero) {
if (this.bgcolor == null && this.source == null && !this.clip && !(this instanceof LzTextSprite) && !this.shadow && !this.borderwidth) {
this.__sizedtozero = true;
size_$1 = 0
}};
this.applyCSS("width", size_$1);
if (this.__LZcontext) this.__LZcontext.style.width = w_$0;
if (this.clip) this.__updateClip();
if (this.isroot) {
if (quirks_$2.container_divs_require_overflow) {
LzSprite.__rootSpriteOverflowContainer.style.width = w_$0
}} else {
if (this.stretches) this.__updateStretches();
if (this.backgroundrepeat) this.__updateBackgroundRepeat();
if (this.__LZclick) this.__LZclick.style.width = w_$0;
if (this.__LZcanvas) this.__resizecanvas()
};
return w_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1868/31";
return $lzsc$temp
})();
LzSprite.prototype.yoffset = 0;
LzSprite.prototype._yoffset = 0;
LzSprite.prototype.setY = (function () {
var $lzsc$temp = function (y_$0) {
if (y_$0 == null || y_$0 == this.y && this._yoffset == this.yoffset) return;
this.__poscacheid = -1;
this.y = y_$0;
this._yoffset = this.yoffset;
y_$0 = this.CSSDimension(y_$0 + this.yoffset);
if (this._y != y_$0) {
this._y = y_$0;
this.__LZdiv.style.top = y_$0;
if (this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style.top = y_$0
};
if (this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.top = y_$0
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1905/27";
return $lzsc$temp
})();
LzSprite.prototype.setHeight = (function () {
var $lzsc$temp = function (h_$0) {
if (h_$0 < 0 || this.height == h_$0) return;
this.height = h_$0;
h_$0 = this.CSSDimension(h_$0);
if (this._h != h_$0) {
this._h = h_$0;
var size_$1 = h_$0;
var quirks_$2 = this.quirks;
if (quirks_$2.size_blank_to_zero) {
if (this.bgcolor == null && this.source == null && !this.clip && !(this instanceof LzTextSprite) && !this.shadow && !this.borderwidth) {
this.__sizedtozero = true;
size_$1 = 0
}};
this.applyCSS("height", size_$1);
if (this.__LZcontext) this.__LZcontext.style.height = h_$0;
if (this.clip) this.__updateClip();
if (this.isroot) {
if (quirks_$2.container_divs_require_overflow) {
LzSprite.__rootSpriteOverflowContainer.style.height = h_$0
}} else {
if (this.stretches) this.__updateStretches();
if (this.backgroundrepeat) this.__updateBackgroundRepeat();
if (this.__LZclick) this.__LZclick.style.height = h_$0;
if (this.__LZcanvas) this.__resizecanvas()
};
return h_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1924/32";
return $lzsc$temp
})();
LzSprite.prototype.setMaxLength = (function () {
var $lzsc$temp = function (v_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1962/35";
return $lzsc$temp
})();
LzSprite.prototype.setPattern = (function () {
var $lzsc$temp = function (v_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1969/33";
return $lzsc$temp
})();
LzSprite.prototype.setVisible = (function () {
var $lzsc$temp = function (v_$0) {
if (this.visible === v_$0) return;
this.visible = v_$0;
var divdisplay_$1 = v_$0 ? "" : "none";
this.applyCSS("display", divdisplay_$1);
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable && this.__LZclick) {
this.__LZclick.style.display = v_$0 && this.clickable ? "" : "none"
};
if (this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style.display = divdisplay_$1
};
if (this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.display = divdisplay_$1
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1973/33";
return $lzsc$temp
})();
LzSprite.prototype.setBGColor = (function () {
var $lzsc$temp = function (c_$0) {
if (c_$0 != null && !this.capabilities.rgba) {
c_$0 = c_$0 | 0
};
if (this.bgcolor == c_$0) return;
this.bgcolor = c_$0;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && c_$0 != null) {
this.__restoreSize()
}};
this.__LZdiv.style.backgroundColor = c_$0 == null ? "transparent" : LzColorUtils.torgb(c_$0);
if (this.quirks.fix_ie_background_height) {
if (this.height != null && this.height < 2) {
this.setSource(LzSprite.blankimage, true)
} else if (!this._fontSize) {
this.__LZdiv.style.fontSize = 0
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#1996/33";
return $lzsc$temp
})();
LzSprite.prototype.__restoreSize = (function () {
var $lzsc$temp = function () {
if (this.__sizedtozero) {
this.__sizedtozero = false;
this.applyCSS("width", this._w);
this.applyCSS("height", this._h)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2018/36";
return $lzsc$temp
})();
LzSprite.prototype.__filters = null;
LzSprite.prototype.setFilter = (function () {
var $lzsc$temp = function (name_$0, value_$1) {
if (this.__filters == null) {
this.__filters = {}};
this.__filters[name_$0] = value_$1;
var filterstr_$2 = "";
for (var i_$3 in this.__filters) {
filterstr_$2 += this.__filters[i_$3]
};
return filterstr_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2029/32";
return $lzsc$temp
})();
LzSprite.prototype.setOpacity = (function () {
var $lzsc$temp = function (o_$0) {
if (this.opacity == o_$0 || o_$0 < 0) return;
this.opacity = o_$0;
var factor_$1 = 100;
if (this.capabilities.minimize_opacity_changes) {
factor_$1 = 10
};
o_$0 = (o_$0 * factor_$1 | 0) / factor_$1;
if (o_$0 != this._opacity) {
this._opacity = o_$0;
if (this.quirks.ie_opacity) {
this.__LZdiv.style.filter = this.setFilter("opacity", o_$0 == 1 ? "" : "alpha(opacity=" + (o_$0 * 100 | 0) + ")")
} else {
this.__LZdiv.style.opacity = o_$0 == 1 ? "" : o_$0
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2042/33";
return $lzsc$temp
})();
LzSprite.prototype.play = (function () {
var $lzsc$temp = function (f_$0) {
if (!this.frames || this.frames.length < 2) return;
f_$0 = f_$0 | 0;
if (!isNaN(f_$0)) {
this.__setFrame(f_$0)
};
if (this.playing == true) return;
this.playing = true;
this.owner.resourceevent("play", null, true);
LzIdleKernel.addCallback(this, "__incrementFrame")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2063/27";
return $lzsc$temp
})();
LzSprite.prototype.stop = (function () {
var $lzsc$temp = function (f_$0) {
if (!this.frames || this.frames.length < 2) return;
if (this.playing == true) {
this.playing = false;
this.owner.resourceevent("stop", null, true);
LzIdleKernel.removeCallback(this, "__incrementFrame")
};
f_$0 = f_$0 | 0;
if (!isNaN(f_$0)) {
this.__setFrame(f_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2076/27";
return $lzsc$temp
})();
LzSprite.prototype.__incrementFrame = (function () {
var $lzsc$temp = function () {
var newframe_$0 = this.frame + 1 > this.frames.length ? 1 : this.frame + 1;
this.__setFrame(newframe_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2093/39";
return $lzsc$temp
})();
if (LzSprite.quirks.preload_images_only_once) {
LzSprite.prototype.__preloadurls = {}};
LzSprite.prototype.__preloadFrames = (function () {
var $lzsc$temp = function () {
if (!this.__ImgPool) {
this.__ImgPool = new LzPool(LzSprite.prototype.__getImage, LzSprite.prototype.__gotImage, LzSprite.prototype.__destroyImage, this)
};
var l_$0 = this.frames.length;
for (var i_$1 = 0;i_$1 < l_$0;i_$1++) {
var src_$2 = this.frames[i_$1];
if (this.quirks.preload_images_only_once) {
if (i_$1 > 0 && LzSprite.prototype.__preloadurls[src_$2]) {
continue
};
LzSprite.prototype.__preloadurls[src_$2] = true
};
var im_$3 = this.__ImgPool.get(src_$2, false, true);
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(im_$3)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2105/38";
return $lzsc$temp
})();
LzSprite.prototype.__findParents = (function () {
var $lzsc$temp = function (prop_$0, value_$1, onlyone_$2) {
var parents_$3 = [];
var root_$4 = LzSprite.__rootSprite;
var sprite_$5 = this;
while (sprite_$5 && sprite_$5 !== root_$4) {
if (sprite_$5[prop_$0] == value_$1) {
parents_$3.push(sprite_$5);
if (onlyone_$2) return parents_$3
};
sprite_$5 = sprite_$5.__parent
};
return parents_$3
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2130/36";
return $lzsc$temp
})();
LzSprite.prototype.__imgonload = (function () {
var $lzsc$temp = function (i_$0, cacheHit_$1) {
if (this.loading != true) return;
if (this.__imgtimoutid != null) {
clearTimeout(this.__imgtimoutid);
this.__imgtimoutid = null
};
this.loading = false;
if (!cacheHit_$1) {
if (this.quirks.ie_alpha_image_loader) {
i_$0._parent.style.display = ""
} else {
i_$0.style.display = ""
}};
this.resourceWidth = cacheHit_$1 && i_$0["__LZreswidth"] ? i_$0.__LZreswidth : i_$0.width;
this.resourceHeight = cacheHit_$1 && i_$0["__LZresheight"] ? i_$0.__LZresheight : i_$0.height;
if (!cacheHit_$1) {
if (this.quirks.invisible_parent_image_sizing_fix && this.resourceWidth == 0) {
var f_$2 = (function () {
var $lzsc$temp = function (i_$0) {
this.resourceWidth = i_$0.width;
this.resourceHeight = i_$0.height
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2171/21";
return $lzsc$temp
})();
this.__processHiddenParents(f_$2, i_$0)
};
if (this.quirks.ie_alpha_image_loader) {
i_$0._parent.__lastcondition = "__imgonload"
} else {
i_$0.__lastcondition = "__imgonload";
i_$0.__LZreswidth = this.resourceWidth;
i_$0.__LZresheight = this.resourceHeight
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else if (this.stretches) {
this.__updateStretches()
}};
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: this.skiponload});
if (this.skiponload != true) {
this.__updateLoadStatus(1)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2147/34";
return $lzsc$temp
})();
LzSprite.prototype.__processHiddenParents = (function () {
var $lzsc$temp = function (method_$0) {
var sprites_$1 = this.__findParents("visible", false);
var l_$2 = sprites_$1.length;
for (var n_$3 = 0;n_$3 < l_$2;n_$3++) {
sprites_$1[n_$3].__LZdiv.style.display = ""
};
var args_$4 = Array.prototype.slice.call(arguments, 1);
var result_$5 = method_$0.apply(this, args_$4);
for (var n_$3 = 0;n_$3 < l_$2;n_$3++) {
var sprite_$6 = sprites_$1[n_$3];
sprite_$6.__LZdiv.style.display = sprite_$6.__csscache.__LZdivdisplay || ""
};
return result_$5
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2216/45";
return $lzsc$temp
})();
LzSprite.prototype.__imgonerror = (function () {
var $lzsc$temp = function (i_$0, cacheHit_$1) {
if (this.loading != true) return;
if (this.__imgtimoutid != null) {
clearTimeout(this.__imgtimoutid);
this.__imgtimoutid = null
};
this.loading = false;
this.resourceWidth = 1;
this.resourceHeight = 1;
if (!cacheHit_$1) {
if (this.quirks.ie_alpha_image_loader) {
i_$0._parent.__lastcondition = "__imgonerror"
} else {
i_$0.__lastcondition = "__imgonerror"
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else if (this.stretches) {
this.__updateStretches()
}};
this.owner.resourceloaderror();
if (this.skiponload != true) {
this.__updateLoadStatus(0)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2240/35";
return $lzsc$temp
})();
LzSprite.prototype.__imgontimeout = (function () {
var $lzsc$temp = function (i_$0, cacheHit_$1) {
if (this.loading != true) return;
this.__imgtimoutid = null;
this.loading = false;
this.resourceWidth = 1;
this.resourceHeight = 1;
if (!cacheHit_$1) {
if (this.quirks.ie_alpha_image_loader) {
i_$0._parent.__lastcondition = "__imgontimeout"
} else {
i_$0.__lastcondition = "__imgontimeout"
};
if (this.quirks.ie_alpha_image_loader) {
this.__updateIEAlpha(this.__LZimg)
} else if (this.stretches) {
this.__updateStretches()
}};
this.owner.resourceloadtimeout();
if (this.skiponload != true) {
this.__updateLoadStatus(0)
};
if (this.quirks.ie_alpha_image_loader) {
this.__clearImageEvents(this.__LZimg)
} else {
this.__clearImageEvents(i_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2280/37";
return $lzsc$temp
})();
LzSprite.prototype.__updateLoadStatus = (function () {
var $lzsc$temp = function (val_$0) {
this.owner.resourceevent("loadratio", val_$0);
this.owner.resourceevent("framesloadratio", val_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2317/41";
return $lzsc$temp
})();
LzSprite.prototype.__destroyImage = (function () {
var $lzsc$temp = function (url_$0, img_$1) {
if (img_$1) {
if (img_$1.owner) {
var owner_$2 = img_$1.owner;
if (owner_$2.__imgtimoutid != null) {
clearTimeout(owner_$2.__imgtimoutid);
owner_$2.__imgtimoutid = null
};
lz.BrowserUtils.removecallback(owner_$2)
};
LzSprite.prototype.__clearImageEvents(img_$1);
LzSprite.prototype.__discardElement(img_$1)
};
if (LzSprite.quirks.preload_images_only_once) {
LzSprite.prototype.__preloadurls[url_$0] = null
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2331/37";
return $lzsc$temp
})();
LzSprite.prototype.__clearImageEvents = (function () {
var $lzsc$temp = function (img_$0) {
if (!img_$0 || img_$0.__cleared) return;
if (LzSprite.quirks.ie_alpha_image_loader) {
var sizer_$1 = img_$0.sizer;
if (sizer_$1) {
if (sizer_$1.tId) clearTimeout(sizer_$1.tId);
sizer_$1.onerror = null;
sizer_$1.onload = null;
sizer_$1.onloadforeal = null;
sizer_$1._parent = null;
var dummyimg_$2 = {width: sizer_$1.width, height: sizer_$1.height, src: sizer_$1.src};
LzSprite.prototype.__discardElement(sizer_$1);
img_$0.sizer = dummyimg_$2
}} else {
img_$0.onerror = null;
img_$0.onload = null
};
img_$0.__cleared = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2354/41";
return $lzsc$temp
})();
LzSprite.prototype.__gotImage = (function () {
var $lzsc$temp = function (url_$0, obj_$1, skiploader_$2) {
if (this.owner.skiponload || skiploader_$2 == true) {
this.owner[obj_$1.__lastcondition]({width: this.owner.resourceWidth, height: this.owner.resourceHeight}, true)
} else {
if (LzSprite.quirks.ie_alpha_image_loader) {
this.owner[obj_$1.__lastcondition](obj_$1.sizer, true)
} else {
this.owner[obj_$1.__lastcondition](obj_$1, true)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2380/33";
return $lzsc$temp
})();
LzSprite.prototype.__getImage = (function () {
var $lzsc$temp = function (url_$0, skiploader_$1) {
if (LzSprite.quirks.ie_alpha_image_loader) {
var im = document.createElement("div");
im.style.overflow = "hidden";
if (this.owner && skiploader_$1 != true) {
im.owner = this.owner;
if (!im.sizer) {
im.sizer = document.createElement("img");
im.sizer._parent = im
};
im.sizer.onload = (function () {
var $lzsc$temp = function () {
im.sizer.tId = setTimeout(this.onloadforeal, 1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2411/31";
return $lzsc$temp
})();
im.sizer.onloadforeal = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonload", [im.sizer]);
im.sizer.onerror = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonerror", [im.sizer]);
var callback_$2 = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgontimeout", [im.sizer]);
this.owner.__imgtimoutid = setTimeout(callback_$2, LzSprite.medialoadtimeout);
im.sizer.src = url_$0
};
if (!skiploader_$1) im.style.display = "none";
if (this.owner.stretches) {
im.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url_$0 + "',sizingMethod='scale')"
} else {
im.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + url_$0 + "')"
}} else {
var im = document.createElement("img");
im.className = "lzimg";
if (!skiploader_$1) im.style.display = "none";
if (this.owner && skiploader_$1 != true) {
im.owner = this.owner;
im.onload = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonload", [im]);
im.onerror = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgonerror", [im]);
var callback_$2 = lz.BrowserUtils.getcallbackfunc(this.owner, "__imgontimeout", [im]);
this.owner.__imgtimoutid = setTimeout(callback_$2, LzSprite.medialoadtimeout)
};
im.src = url_$0
};
if (im) im.__lastcondition = "__imgonload";
return im
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2398/33";
return $lzsc$temp
})();
LzSprite.prototype.setClip = (function () {
var $lzsc$temp = function (c_$0) {
if (this.clip === c_$0) return;
this.clip = c_$0;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && c_$0) {
this.__restoreSize()
}};
this.__updateClip()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2448/30";
return $lzsc$temp
})();
LzSprite.prototype._clip = "";
LzSprite.prototype.__updateClip = (function () {
var $lzsc$temp = function () {
var quirks_$0 = this.quirks;
if (this.isroot && this.quirks.canvas_div_cannot_be_clipped) return;
var clipcss_$1 = "";
if (this.clip && this.width != null && this.width >= 0 && this.height != null && this.height >= 0) {
clipcss_$1 = "rect(0px " + this._w + " " + this._h + " 0px)"
} else if (this._clip) {
clipcss_$1 = quirks_$0.fix_ie_css_syntax ? "rect(auto auto auto auto)" : ""
};
if (clipcss_$1 !== this._clip) {
this._clip = clipcss_$1;
this.__LZdiv.style.clip = clipcss_$1
} else {
return
};
if (quirks_$0.fix_clickable && quirks_$0.dont_clip_clickdivs != true) {
if (this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style.clip = clipcss_$1
}};
if (quirks_$0.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.clip = clipcss_$1
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2464/35";
return $lzsc$temp
})();
LzSprite.prototype.stretchResource = (function () {
var $lzsc$temp = function (s_$0) {
s_$0 = s_$0 != "none" ? s_$0 : null;
if (this.stretches == s_$0) return;
this.stretches = s_$0;
if (!(s_$0 == null && this.__csssprite) && this.__bgimage) {
if (this.quirks.preload_images) this.__preloadFrames();
this.__setBGImage(null);
this.__setFrame(this.frame, true)
};
this.__updateStretches()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2504/38";
return $lzsc$temp
})();
LzSprite.prototype.__updateStretches = (function () {
var $lzsc$temp = function () {
if (this.loading) return;
var quirks_$0 = this.quirks;
if (quirks_$0.ie_alpha_image_loader) return;
var img_$1 = this.__LZimg;
if (img_$1) {
if (quirks_$0.show_img_before_changing_size) {
var imgstyle_$2 = img_$1.style;
var olddisplay_$3 = imgstyle_$2.display;
imgstyle_$2.display = "none"
};
if (this.stretches == "both") {
img_$1.width = this.width;
img_$1.height = this.height
} else if (this.stretches == "height") {
img_$1.width = this.resourceWidth;
img_$1.height = this.height
} else if (this.stretches == "width") {
img_$1.width = this.width;
img_$1.height = this.resourceHeight
} else {
img_$1.width = this.resourceWidth;
img_$1.height = this.resourceHeight
};
if (quirks_$0.show_img_before_changing_size) {
imgstyle_$2.display = olddisplay_$3
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2522/40";
return $lzsc$temp
})();
LzSprite.prototype.predestroy = (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2553/33";
return $lzsc$temp
})();
LzSprite.prototype.destroy = (function () {
var $lzsc$temp = function (parentvalid_$0) {
switch (arguments.length) {
case 0:
parentvalid_$0 = true
};
if (this.__LZdeleted == true) return;
this.__LZdeleted = true;
if (parentvalid_$0) {
if (this.__parent) {
var pc_$1 = this.__parent.__children;
for (var i_$2 = pc_$1.length - 1;i_$2 >= 0;i_$2--) {
if (pc_$1[i_$2] === this) {
pc_$1.splice(i_$2, 1);
break
}}}};
if (this.__ImgPool) this.__ImgPool.destroy();
if (this.__LZimg) this.__discardElement(this.__LZimg);
this.__skipdiscards = parentvalid_$0 != true;
if (this.__LZclick) {
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
};
if (LzSprite.quirks.prevent_selection) {
this.__LZdiv.onselectstart = null
}};
if (LzSprite.quirks.prevent_selection_with_onselectstart) {
if (this.selectable) {
this.setSelectable(false)
}};
this.__discardElement(this.__LZdiv)
};
if (this.__LZinputclickdiv) {
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
if (this.quirks.ie_leak_prevention) {
this.__LZcanvas.owner = null;
this.__LZcanvas.getContext = null
};
this.__discardElement(this.__LZcanvas)
};
this.__ImgPool = null;
if (this.quirks.ie_leak_prevention) {
delete this.__sprites[this.uid]
};
if (this.isroot) {
lz.BrowserUtils.scopes = null
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2556/30";
return $lzsc$temp
})();
LzSprite.prototype.getMouse = (function () {
var $lzsc$temp = function () {
var p_$0 = this.__getPos();
return {x: LzMouseKernel.__x - p_$0.x, y: LzMouseKernel.__y - p_$0.y}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2643/31";
return $lzsc$temp
})();
LzSprite.prototype.__poscache = null;
LzSprite.prototype.__poscacheid = 0;
LzSprite.__poscachecnt = 0;
LzSprite.prototype.__getPos = (function () {
var $lzsc$temp = function () {
if (!LzSprite.__rootSprite.initted) {
return lz.embed.getAbsolutePosition(this.__LZdiv)
};
var dirty_$0 = false;
var attached_$1 = true;
var root_$2 = LzSprite.__rootSprite;
var pp_$3, ppmax_$4;
for (var p_$5 = this;p_$5 !== root_$2;p_$5 = pp_$3) {
pp_$3 = p_$5.__parent;
if (pp_$3) {
if (p_$5.__poscacheid < pp_$3.__poscacheid) {
dirty_$0 = true;
ppmax_$4 = pp_$3
}} else {
attached_$1 = false;
break
}};
if (dirty_$0 && attached_$1) {
var next_$6 = ++LzSprite.__poscachecnt;
for (var p_$5 = this;p_$5 !== ppmax_$4;p_$5 = p_$5.__parent) {
p_$5.__poscache = null;
p_$5.__poscacheid = next_$6
}};
var pos_$7 = this.__poscache;
if (!pos_$7) {
pos_$7 = this.__processHiddenParents(lz.embed.getAbsolutePosition, this.__LZdiv);
if (attached_$1) {
this.__poscache = pos_$7
}};
return pos_$7
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2665/31";
return $lzsc$temp
})();
LzSprite.prototype.getWidth = (function () {
var $lzsc$temp = function () {
var w_$0 = this.__LZdiv.clientWidth;
return w_$0 == 0 ? this.width : w_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2712/31";
return $lzsc$temp
})();
LzSprite.prototype.getHeight = (function () {
var $lzsc$temp = function () {
var h_$0 = this.__LZdiv.clientHeight;
return h_$0 == 0 ? this.height : h_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2718/32";
return $lzsc$temp
})();
LzSprite.prototype.setCursor = (function () {
var $lzsc$temp = function (c_$0) {
if (this.quirks.no_cursor_colresize) {
return
};
if (c_$0 === this.cursor) return;
if (this.clickable !== true) this.setClickable(true);
this.cursor = c_$0;
if (this.quirks.fix_clickable) {
this.__LZclick.style.cursor = LzSprite.__defaultStyles.hyphenate(c_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2731/32";
return $lzsc$temp
})();
LzSprite.prototype.setShowHandCursor = (function () {
var $lzsc$temp = function (s_$0) {
if (s_$0 == true) {
this.setCursor("pointer")
} else {
this.setCursor("default")
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2749/40";
return $lzsc$temp
})();
LzSprite.prototype.getDisplayObject = (function () {
var $lzsc$temp = function () {
return this.__LZdiv
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2757/39";
return $lzsc$temp
})();
LzSprite.prototype.__LZcanvas = null;
LzSprite.prototype.getContext = (function () {
var $lzsc$temp = function () {
if (this.__LZcanvas && this.__LZcanvas.getContext) {
return this.__LZcanvas.getContext("2d")
};
var canvas_$0 = document.createElement("canvas");
canvas_$0.owner = this;
this.__LZcanvas = canvas_$0;
canvas_$0.className = "lzgraphicscanvas";
if (this.__LZdiv.firstChild) {
this.__LZdiv.insertBefore(canvas_$0, this.__LZdiv.firstChild)
} else {
this.__LZdiv.appendChild(canvas_$0)
};
lz.embed.__setAttr(canvas_$0, "width", this.width);
lz.embed.__setAttr(canvas_$0, "height", this.height);
if (this.cornerradius != null) {
this.__applyCornerRadius(canvas_$0)
};
if (lz.embed.browser.isIE) {
this.__maxTries = 10;
this.__initcanvasie()
} else {
return canvas_$0.getContext("2d")
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2764/33";
return $lzsc$temp
})();
LzSprite.prototype.setContextCallback = (function () {
var $lzsc$temp = function (callbackscope_$0, callbackname_$1) {
this.__canvascallbackscope = callbackscope_$0;
this.__canvascallbackname = callbackname_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2800/41";
return $lzsc$temp
})();
LzSprite.prototype.bringToFront = (function () {
var $lzsc$temp = function () {
if (!this.__parent) {
Debug.warn("bringToFront with no parent");
return
};
var c_$0 = this.__parent.__children;
if (c_$0.length < 2) return;
c_$0.sort(LzSprite.prototype.__zCompare);
this.sendInFrontOf(c_$0[c_$0.length - 1])
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2806/35";
return $lzsc$temp
})();
LzSprite.prototype.__setZ = (function () {
var $lzsc$temp = function (z_$0) {
this.__LZdiv.style.zIndex = z_$0;
var quirks_$1 = this.quirks;
if (quirks_$1.fix_clickable && this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style.zIndex = z_$0
};
if (quirks_$1.fix_contextmenu && this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style.zIndex = z_$0
};
this.__z = z_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2824/29";
return $lzsc$temp
})();
LzSprite.prototype.__zCompare = (function () {
var $lzsc$temp = function (a_$0, b_$1) {
if (a_$0.__z < b_$1.__z) return -1;
if (a_$0.__z > b_$1.__z) return 1;
return 0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2839/33";
return $lzsc$temp
})();
LzSprite.prototype.sendToBack = (function () {
var $lzsc$temp = function () {
if (!this.__parent) {
Debug.warn("sendToBack with no parent");
return
};
var c_$0 = this.__parent.__children;
if (c_$0.length < 2) return;
c_$0.sort(LzSprite.prototype.__zCompare);
this.sendBehind(c_$0[0])
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2847/33";
return $lzsc$temp
})();
LzSprite.prototype.sendBehind = (function () {
var $lzsc$temp = function (behindSprite_$0) {
if (!behindSprite_$0 || behindSprite_$0 === this) return;
if (!this.__parent) {
Debug.warn("sendBehind with no parent");
return
};
var c_$1 = this.__parent.__children;
if (c_$1.length < 2) return;
c_$1.sort(LzSprite.prototype.__zCompare);
var behindZ_$2 = false;
for (var i_$3 = 0;i_$3 < c_$1.length;i_$3++) {
var s_$4 = c_$1[i_$3];
if (s_$4 == behindSprite_$0) behindZ_$2 = behindSprite_$0.__z;
if (behindZ_$2 != false) {
s_$4.__setZ(++s_$4.__z)
}};
this.__setZ(behindZ_$2)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2862/33";
return $lzsc$temp
})();
LzSprite.prototype.sendInFrontOf = (function () {
var $lzsc$temp = function (frontSprite_$0) {
if (!frontSprite_$0 || frontSprite_$0 === this) return;
if (!this.__parent) {
Debug.warn("sendInFrontOf with no parent");
return
};
var c_$1 = this.__parent.__children;
if (c_$1.length < 2) return;
c_$1.sort(LzSprite.prototype.__zCompare);
var frontZ_$2 = false;
for (var i_$3 = 0;i_$3 < c_$1.length;i_$3++) {
var s_$4 = c_$1[i_$3];
if (frontZ_$2 != false) {
s_$4.__setZ(++s_$4.__z)
};
if (s_$4 == frontSprite_$0) frontZ_$2 = frontSprite_$0.__z + 1
};
this.__setZ(frontZ_$2)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2888/36";
return $lzsc$temp
})();
LzSprite.prototype.__setFrame = (function () {
var $lzsc$temp = function (f_$0, force_$1) {
if (f_$0 < 1) {
f_$0 = 1
} else if (f_$0 > this.frames.length) {
f_$0 = this.frames.length
};
var skipevent_$2 = false;
if (force_$1) {
skipevent_$2 = f_$0 == this.frame
} else if (f_$0 == this.frame) {
return
};
this.frame = f_$0;
var url_$3 = this.frames[this.frame - 1];
if (this.backgroundrepeat) {
this.__setBGImage(url_$3);
this.__updateBackgroundRepeat()
} else if (this.stretches == null && this.__csssprite) {
if (!this.__bgimage) {
this.__createIMG();
this.__setBGImage(this.__csssprite)
};
var x_$4 = (this.frame - 1) * -this.resourceWidth;
var y_$5 = -this.__cssspriteoffset || 0;
this.__LZimg.style.backgroundPosition = x_$4 + "px " + y_$5 + "px"
} else {
this.setSource(url_$3, true)
};
if (skipevent_$2) return;
this.owner.resourceevent("frame", this.frame);
if (this.frames.length == this.frame) this.owner.resourceevent("lastframe", null, true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2918/33";
return $lzsc$temp
})();
LzSprite.prototype.__discardElement = (function () {
var $lzsc$temp = function (element_$0) {
if (this.__skipdiscards) return;
if (element_$0.parentNode) element_$0.parentNode.removeChild(element_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2961/39";
return $lzsc$temp
})();
LzSprite.prototype.getZ = (function () {
var $lzsc$temp = function () {
return this.__z
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2970/27";
return $lzsc$temp
})();
LzSprite.prototype.updateResourceSize = (function () {
var $lzsc$temp = function () {
this.owner.resourceload({width: this.resourceWidth, height: this.resourceHeight, resource: this.resource, skiponload: true})
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2974/41";
return $lzsc$temp
})();
LzSprite.prototype.unload = (function () {
var $lzsc$temp = function () {
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
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2978/29";
return $lzsc$temp
})();
LzSprite.prototype.__setCSSClassProperty = (function () {
var $lzsc$temp = function (classname_$0, name_$1, value_$2) {
var rulename_$3 = document.all ? "rules" : "cssRules";
var sheets_$4 = document.styleSheets;
var sl_$5 = sheets_$4.length - 1;
for (var i_$6 = sl_$5;i_$6 >= 0;i_$6--) {
var rules_$7 = sheets_$4[i_$6][rulename_$3];
var rl_$8 = rules_$7.length - 1;
for (var j_$9 = rl_$8;j_$9 >= 0;j_$9--) {
if (rules_$7[j_$9].selectorText == classname_$0) {
rules_$7[j_$9].style[name_$1] = value_$2
}}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#2997/44";
return $lzsc$temp
})();
LzSprite.prototype.setDefaultContextMenu = (function () {
var $lzsc$temp = function (cmenu_$0) {
LzSprite.__rootSprite.__contextmenu = cmenu_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3017/44";
return $lzsc$temp
})();
LzSprite.prototype.setContextMenu = (function () {
var $lzsc$temp = function (cmenu_$0) {
this.__contextmenu = cmenu_$0;
if (!this.quirks.fix_contextmenu || this.__LZcontext) return;
var cxdiv_$1 = document.createElement("div");
cxdiv_$1.className = "lzcontext";
if (!this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv = this.__createContainerDivs("context")
};
this.__LZcontextcontainerdiv.appendChild(cxdiv_$1);
this.__LZcontext = cxdiv_$1;
this.__LZcontext.style.width = this._w;
this.__LZcontext.style.height = this._h;
cxdiv_$1.owner = this
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3026/37";
return $lzsc$temp
})();
LzSprite.prototype.__createContainerDivs = (function () {
var $lzsc$temp = function (typestring_$0) {
var propname_$1 = "__LZ" + typestring_$0 + "containerdiv";
var copyclip_$2 = true;
if (this.quirks.dont_clip_clickdivs && typestring_$0 === "click") {
copyclip_$2 = false
};
if (this[propname_$1]) {
return this[propname_$1]
};
var sprites_$3 = this.__findParents(propname_$1, null);
for (var i_$4 = sprites_$3.length - 1;i_$4 >= 0;i_$4--) {
var sprite_$5 = sprites_$3[i_$4];
var newdiv_$6 = document.createElement("div");
newdiv_$6.className = sprite_$5 instanceof LzTextSprite ? "lztextcontainer" : "lzdiv";
var parentcontainer_$7 = sprite_$5.__parent && sprite_$5.__parent[propname_$1];
if (parentcontainer_$7) {
parentcontainer_$7.appendChild(newdiv_$6)
};
this.__copystyles(sprite_$5.__LZdiv, newdiv_$6, copyclip_$2);
if (sprite_$5._id && !newdiv_$6.id) {
newdiv_$6.id = typestring_$0 + sprite_$5._id
};
newdiv_$6.owner = sprite_$5;
sprite_$5[propname_$1] = newdiv_$6
};
return newdiv_$6
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3046/44";
return $lzsc$temp
})();
LzSprite.prototype.__copystyles = (function () {
var $lzsc$temp = function (from_$0, to_$1, copyclip_$2) {
copyclip_$2 = copyclip_$2 == null ? true : copyclip_$2;
var sprite_$3 = from_$0.owner;
var left_$4 = sprite_$3._x;
if (left_$4) {
to_$1.style.left = left_$4
};
var top_$5 = sprite_$3._y;
if (top_$5) {
to_$1.style.top = top_$5
};
var display_$6 = sprite_$3.__csscache.__LZdivdisplay || "";
if (display_$6) {
to_$1.style.display = display_$6
};
to_$1.style.zIndex = sprite_$3._z || from_$0.style.zIndex;
if (copyclip_$2 && sprite_$3._clip) {
to_$1.style.clip = sprite_$3._clip
};
if (sprite_$3._transform) {
to_$1.style[stylename] = sprite_$3._transform
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3105/35";
return $lzsc$temp
})();
LzSprite.prototype.getContextMenu = (function () {
var $lzsc$temp = function () {
return this.__contextmenu
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3134/37";
return $lzsc$temp
})();
LzSprite.prototype.rotation = 0;
LzSprite.prototype.setRotation = (function () {
var $lzsc$temp = function (r_$0) {
if (this.rotation == r_$0) return;
this.rotation = r_$0;
this._rotation = "rotate(" + r_$0 + "deg) ";
this.__updateTransform()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3139/34";
return $lzsc$temp
})();
LzSprite.prototype._transform = "";
LzSprite.prototype.__updateTransform = (function () {
var $lzsc$temp = function (r_$0) {
var css_$1 = (this._xscale || "") + (this._yscale || "") + (this._rotation || "");
if (css_$1 === this._transform) return;
this._transform = css_$1;
var stylename_$2 = LzSprite.__styleNames.transform;
this.__LZdiv.style[stylename_$2] = css_$1;
if (this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv.style[stylename_$2] = css_$1
};
if (this.__LZcontextcontainerdiv) {
this.__LZcontextcontainerdiv.style[stylename_$2] = css_$1
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3147/40";
return $lzsc$temp
})();
LzSprite.prototype.backgroundrepeat = null;
LzSprite.prototype.tilex = false;
LzSprite.prototype.tiley = false;
LzSprite.prototype.setBackgroundRepeat = (function () {
var $lzsc$temp = function (backgroundrepeat_$0) {
if (this.backgroundrepeat == backgroundrepeat_$0) return;
var x_$1 = false;
var y_$2 = false;
if (backgroundrepeat_$0 == "repeat") {
x_$1 = y_$2 = true
} else if (backgroundrepeat_$0 == "repeat-x") {
x_$1 = true
} else if (backgroundrepeat_$0 == "repeat-y") {
y_$2 = true
};
this.tilex = x_$1;
this.tiley = y_$2;
this.backgroundrepeat = backgroundrepeat_$0;
if (!this.__LZimg) this.__createIMG();
this.__updateBackgroundRepeat();
if (backgroundrepeat_$0) {
this.__setBGImage(this.source);
this.__LZimg.src = LzSprite.blankimage
} else {
if (this.__bgimage) this.__setBGImage(null);
backgroundrepeat_$0 = "";
this.skiponload = true;
this.setSource(this.source, "reset")
};
this.__LZdiv.style.backgroundRepeat = backgroundrepeat_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3165/42";
return $lzsc$temp
})();
LzSprite.prototype.__updateBackgroundRepeat = (function () {
var $lzsc$temp = function () {
if (this.__LZimg) {
this.__LZimg.style.backgroundRepeat = this.backgroundrepeat;
this.__LZimg.style.backgroundPosition = "0 0";
this.__LZimg.width = this.backgroundrepeat ? this.width : this.resourceWidth;
this.__LZimg.height = this.backgroundrepeat ? this.height : this.resourceHeight
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3194/47";
return $lzsc$temp
})();
if (LzSprite.quirks.ie_leak_prevention) {
LzSprite.prototype.__sprites = {};
function __cleanUpForIE () {
LzTextSprite.prototype.__cleanupdivs();
LzTextSprite.prototype._sizecache = {};
var obj_$0 = LzSprite.prototype.__sprites;
for (var i_$1 in obj_$0) {
obj_$0[i_$1].destroy();
obj_$0[i_$1] = null
};
LzSprite.prototype.__sprites = {}};
lz.embed.attachEventHandler(window, "beforeunload", window, "__cleanUpForIE");
LzSprite.prototype.__discardElement = (function () {
var $lzsc$temp = function (element_$0) {
if (!element_$0 || !element_$0.nodeType) return;
if (element_$0.nodeType >= 1 && element_$0.nodeType < 13) {
if (element_$0.owner) element_$0.owner = null;
var garbageBin_$1 = document.getElementById("__LZIELeakGarbageBin");
if (!garbageBin_$1) {
garbageBin_$1 = document.createElement("DIV");
garbageBin_$1.id = "__LZIELeakGarbageBin";
garbageBin_$1.style.display = "none";
document.body.appendChild(garbageBin_$1)
};
garbageBin_$1.appendChild(element_$0);
garbageBin_$1.innerHTML = ""
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3221/43";
return $lzsc$temp
})()
};
LzSprite.prototype.getSelectedText = (function () {
var $lzsc$temp = function () {
if (window.getSelection) {
return window.getSelection().toString()
} else if (document.selection) {
return document.selection.createRange().text.toString()
} else if (document.getSelection) {
return document.getSelection()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3245/38";
return $lzsc$temp
})();
LzSprite.prototype.setAADescription = (function () {
var $lzsc$temp = function (s_$0) {
var aadiv_$1 = this.aadescriptionDiv;
if (aadiv_$1 == null) {
this.aadescriptionDiv = aadiv_$1 = document.createElement("LABEL");
aadiv_$1.className = "lzaccessibilitydiv";
if (!this.__LZdiv.id) this.__LZdiv.id = "sprite_" + this.uid;
lz.embed.__setAttr(aadiv_$1, "for", this.__LZdiv.id);
this.__LZdiv.appendChild(aadiv_$1)
};
aadiv_$1.innerHTML = s_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3259/39";
return $lzsc$temp
})();
LzSprite.prototype.setAccessible = (function () {
var $lzsc$temp = function (accessible_$0) {
LzSprite.__rootSprite.accessible = accessible_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3279/36";
return $lzsc$temp
})();
LzSprite.prototype._accProps = null;
LzSprite.prototype.setAAActive = (function () {
var $lzsc$temp = function (s_$0) {
this.__LzAccessibilityActive = s_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3296/34";
return $lzsc$temp
})();
LzSprite.prototype.setAASilent = (function () {
var $lzsc$temp = function (s_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3306/34";
return $lzsc$temp
})();
LzSprite.prototype.setAAName = (function () {
var $lzsc$temp = function (s_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3315/32";
return $lzsc$temp
})();
LzSprite.prototype.aafocus = (function () {
var $lzsc$temp = function () {
try {
if (this.__LZdiv != null) {
this.__LZdiv.blur();
this.__LZdiv.focus()
}}
catch (e_$0) {}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3323/30";
return $lzsc$temp
})();
LzSprite.prototype.setAATabIndex = (function () {
var $lzsc$temp = function (s_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3337/36";
return $lzsc$temp
})();
LzSprite.prototype.sendAAEvent = (function () {
var $lzsc$temp = function (childID_$0, eventType_$1, nonHTML_$2) {
try {
if (this.__LZdiv != null) {
this.__LZdiv.focus()
}}
catch (e_$3) {}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3344/34";
return $lzsc$temp
})();
LzSprite.prototype.setID = (function () {
var $lzsc$temp = function (id_$0) {
if (!this._id) this._id = id_$0;
if (!this.__LZdiv.id) this.__LZdiv.id = this._dbg_typename + id_$0;
if (this.__LZclickcontainerdiv && !this.__LZclickcontainerdiv.id) this.__LZclickcontainerdiv.id = "click" + id_$0;
if (this.__LZcontextcontainerdiv && !this.__LZcontextcontainerdiv.id) this.__LZcontextcontainerdiv.id = this.__LZcontextcontainerdiv.id = "context" + id_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3353/28";
return $lzsc$temp
})();
LzSprite.prototype.__resizecanvas = (function () {
var $lzsc$temp = function () {
if (this.width > 0 && this.height > 0) {
if (this.__LZcanvas) {
lz.embed.__setAttr(this.__LZcanvas, "width", this.width);
lz.embed.__setAttr(this.__LZcanvas, "height", this.height);
this.__docanvascallback()
};
if (this.__LZcanvas && this["_canvashidden"]) {
this._canvashidden = false;
this.applyCSS("display", "", "__LZcanvas")
}} else if (this.__LZcanvas && this["_canvashidden"] != true) {
this._canvashidden = true;
this.applyCSS("display", "none", "__LZcanvas")
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3360/37";
return $lzsc$temp
})();
LzSprite.prototype.__docanvascallback = (function () {
var $lzsc$temp = function () {
var callback_$0 = this.__canvascallbackscope[this.__canvascallbackname];
if (callback_$0) {
callback_$0.call(this.__canvascallbackscope, this.__LZcanvas.getContext("2d"));
if (LzSprite.quirks.resize2dcanvas) {
var canvassize_$1 = this.__LZcanvas.firstChild;
canvassize_$1.style.width = this._w;
canvassize_$1.style.height = this._h
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3378/41";
return $lzsc$temp
})();
LzSprite.prototype.__initcanvasie = (function () {
var $lzsc$temp = function () {
if (this.__canvasTId) clearTimeout(this.__canvasTId);
try {
if (this.__LZcanvas && this.__LZcanvas.parentNode != null) {
this.__LZcanvas = G_vmlCanvasManager.initElement(this.__LZcanvas);
this.__docanvascallback();
return
}}
catch (e_$0) {};
if (--this.__maxTries > 0) {
var callback_$1 = lz.BrowserUtils.getcallbackstr(this, "__initcanvasie");
this.__canvasTId = setTimeout(callback_$1, 50)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3391/37";
return $lzsc$temp
})();
LzSprite.prototype.__getShadowCSS = (function () {
var $lzsc$temp = function (shadowcolor_$0, shadowdistance_$1, shadowangle_$2, shadowblurradius_$3) {
if (shadowcolor_$0 == null || shadowdistance_$1 == 0 && shadowblurradius_$3 == 0) {
return ""
};
if (this.capabilities.minimize_opacity_changes) {
shadowdistance_$1 = Math.round(shadowdistance_$1);
shadowblurradius_$3 = Math.round(shadowblurradius_$3);
shadowangle_$2 = Math.round(shadowangle_$2)
};
if (this.quirks.use_filter_for_dropshadow) {
if (shadowdistance_$1 == 0) {
this.xoffset = this.yoffset = -shadowblurradius_$3;
this.applyCSS("left", this.x + this.xoffset);
this.applyCSS("top", this.y + this.yoffset);
if (shadowblurradius_$3 > 0) {
var hexcolor_$4 = LzColorUtils.inttohex(shadowcolor_$0);
return "progid:DXImageTransform.Microsoft.Glow(Color='" + hexcolor_$4 + "',Strength=" + shadowblurradius_$3 + ")"
}} else {
shadowangle_$2 += 90;
var hexcolor_$4 = LzColorUtils.inttohex(shadowcolor_$0);
return "progid:DXImageTransform.Microsoft.Shadow(Color='" + hexcolor_$4 + "',Direction=" + shadowangle_$2 + ",Strength=" + shadowdistance_$1 + ")"
}} else {
var radians_$5 = shadowangle_$2 * Math.PI / 180;
var xoffset_$6 = this.CSSDimension(Math.cos(radians_$5) * shadowdistance_$1);
var yoffset_$7 = this.CSSDimension(Math.sin(radians_$5) * shadowdistance_$1);
var rgbcolor_$8 = LzColorUtils.torgb(shadowcolor_$0);
return rgbcolor_$8 + " " + xoffset_$6 + " " + yoffset_$7 + " " + this.CSSDimension(shadowblurradius_$3)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3409/37";
return $lzsc$temp
})();
LzSprite.prototype.shadow = null;
LzSprite.prototype.updateShadow = (function () {
var $lzsc$temp = function (shadowcolor_$0, shadowdistance_$1, shadowangle_$2, shadowblurradius_$3) {
var newshadow_$4 = this.__getShadowCSS(shadowcolor_$0, shadowdistance_$1, shadowangle_$2, shadowblurradius_$3);
if (newshadow_$4 === this.shadow) return;
this.shadow = newshadow_$4;
if (this.quirks.use_filter_for_dropshadow) {
this.__LZdiv.style.filter = this.setFilter("shadow", newshadow_$4)
} else {
var cssname_$5 = LzSprite.__styleNames.boxShadow;
if (this.__LZcanvas) {
this.__LZdiv.style[cssname_$5] = "";
this.__LZcanvas.style[cssname_$5] = newshadow_$4
} else {
this.__LZdiv.style[cssname_$5] = newshadow_$4
}};
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero) {
this.__restoreSize()
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3450/35";
return $lzsc$temp
})();
LzSprite.prototype.cornerradius = null;
LzSprite.prototype.setCornerRadius = (function () {
var $lzsc$temp = function (radii_$0) {
var css_$1 = "";
for (var i_$2 = 0, l_$3 = radii_$0.length;i_$2 < l_$3;i_$2++) {
radii_$0[i_$2] = this.CSSDimension(radii_$0[i_$2])
};
css_$1 = radii_$0.join(" ");
if (css_$1 == this.cornerradius) return;
this.cornerradius = css_$1;
this.__applyCornerRadius(this.__LZdiv);
if (this.__LZclick) {
this.__applyCornerRadius(this.__LZclick)
};
if (this.__LZcontext) {
this.__applyCornerRadius(this.__LZcontext)
};
if (this.__LZcanvas) {
this.__applyCornerRadius(this.__LZcanvas)
};
if (this.__LZimg) {
this.__applyCornerRadius(this.__LZimg)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3477/38";
return $lzsc$temp
})();
LzSprite.prototype.__applyCornerRadius = (function () {
var $lzsc$temp = function (div_$0) {
var stylenames_$1 = LzSprite.__styleNames;
if (this.quirks.explicitly_set_border_radius) {
var radii_$2 = this.cornerradius.split(" ");
div_$0.style[stylenames_$1.borderTopLeftRadius] = radii_$2[0];
div_$0.style[stylenames_$1.borderTopRightRadius] = radii_$2[1];
div_$0.style[stylenames_$1.borderBottomRightRadius] = radii_$2[2];
div_$0.style[stylenames_$1.borderBottomLeftRadius] = radii_$2[3]
} else {
div_$0.style[stylenames_$1.borderRadius] = this.cornerradius
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3502/42";
return $lzsc$temp
})();
LzSprite.prototype.__csscache;
LzSprite.prototype.setCSS = (function () {
var $lzsc$temp = function (name_$0, value_$1, isdimension_$2) {
if (isdimension_$2) value_$1 = this.CSSDimension(value_$1);
var callback_$3 = this["set_" + name_$0];
if (callback_$3) {
callback_$3.call(this, value_$1)
} else {
this.applyCSS(name_$0, value_$1);
if (this.__LZclickcontainerdiv) {
this.applyCSS(name_$0, value_$1, "__LZclickcontainerdiv")
};
if (this.__LZcontextcontainerdiv) {
this.applyCSS(name_$0, value_$1, "__LZcontextcontainerdiv")
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3520/29";
return $lzsc$temp
})();
LzSprite.prototype.applyCSS = (function () {
var $lzsc$temp = function (name_$0, value_$1, divname_$2) {
if (!divname_$2) divname_$2 = "__LZdiv";
var key_$3 = divname_$2 + name_$0;
var cache_$4 = this.__csscache;
if (cache_$4[key_$3] === value_$1) {
return
};
var styleobject_$5 = this[divname_$2].style;
cache_$4[key_$3] = styleobject_$5[name_$0] = value_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3543/31";
return $lzsc$temp
})();
LzSprite.prototype.set_borderColor = (function () {
var $lzsc$temp = function (color_$0) {
if (color_$0 == null) color_$0 = "";
this.__LZdiv.style.borderColor = color_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3573/38";
return $lzsc$temp
})();
LzSprite.prototype.borderwidth = 0;
LzSprite.prototype.set_borderWidth = (function () {
var $lzsc$temp = function (width_$0) {
if (this.borderwidth === width_$0) return;
this.borderwidth = width_$0;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && width_$0 != null) {
this.__restoreSize()
}};
if (width_$0 == 0) {
width_$0 = ""
};
this.__LZdiv.style.borderWidth = width_$0;
if (this.__LZclick) {
this.__LZclick.style.borderWidth = width_$0
};
if (this.__LZcontext) {
this.__LZcontext.style.borderWidth = width_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3579/38";
return $lzsc$temp
})();
LzSprite.prototype.set_padding = (function () {
var $lzsc$temp = function (padding_$0) {
if (this.padding === padding_$0) return;
this.padding = padding_$0;
if (this.quirks.size_blank_to_zero) {
if (this.__sizedtozero && padding_$0 != null) {
this.__restoreSize()
}};
if (padding_$0 == 0) {
padding_$0 = ""
};
if (this.__LZclick) {
this.__LZclick.style.padding = padding_$0
};
if (this.__LZcontext) {
this.__LZcontext.style.padding = padding_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3602/34";
return $lzsc$temp
})();
LzSprite.medialoadtimeout = 30000;
LzSprite.setMediaLoadTimeout = (function () {
var $lzsc$temp = function (ms_$0) {
LzSprite.medialoadtimeout = ms_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3625/32";
return $lzsc$temp
})();
LzSprite.setMediaErrorTimeout = (function () {
var $lzsc$temp = function (ms_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3629/33";
return $lzsc$temp
})();
LzSprite.prototype.xscale = 1;
LzSprite.prototype.setXScale = (function () {
var $lzsc$temp = function (xscale_$0) {
if (this.xscale == xscale_$0) return;
this.xscale = xscale_$0;
this._xscale = "scaleX(" + xscale_$0 + ") ";
this.__updateTransform()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3634/32";
return $lzsc$temp
})();
LzSprite.prototype.yscale = 1;
LzSprite.prototype.setYScale = (function () {
var $lzsc$temp = function (yscale_$0) {
if (this.yscale == yscale_$0) return;
this.yscale = yscale_$0;
this._yscale = "scaleY(" + yscale_$0 + ") ";
this.__updateTransform()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzSprite.js#3642/32";
return $lzsc$temp
})();
Class.make("LzLibrary", ["loaded", false, "loading", false, "sprite", null, "href", void 0, "stage", "late", "onload", LzDeclaredEvent, "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
this.stage = args_$1.stage;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
this.sprite = new LzSprite(this, false, args_$1);
LzLibrary.libraries[args_$1.name] = this
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
if (this.stage == "late") {
this.load()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.sprite) {
this.sprite.destroy();
this.sprite = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Library " + this.href + " named " + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "load", (function () {
var $lzsc$temp = function () {
if (this.loading || this.loaded) {
return
};
this.loading = true;
lz.embed.__dhtmlLoadLibrary(this.href)
};
$lzsc$temp["displayName"] = "load";
return $lzsc$temp
})(), "loadfinished", (function () {
var $lzsc$temp = function () {
this.loading = false;
if (this.onload.ready) this.onload.sendEvent(true)
};
$lzsc$temp["displayName"] = "loadfinished";
return $lzsc$temp
})()], LzNode, ["tagname", "import", "attributes", new LzInheritedHash(LzNode.attributes), "libraries", {}, "findLibrary", (function () {
var $lzsc$temp = function (libname_$0) {
return LzLibrary.libraries[libname_$0]
};
$lzsc$temp["displayName"] = "findLibrary";
return $lzsc$temp
})(), "stripQueryString", (function () {
var $lzsc$temp = function (str_$0) {
if (str_$0.indexOf("?") > 0) {
str_$0 = str_$0.substring(0, str_$0.indexOf("?"))
};
return str_$0
};
$lzsc$temp["displayName"] = "stripQueryString";
return $lzsc$temp
})(), "__LZsnippetLoaded", (function () {
var $lzsc$temp = function (url_$0) {
url_$0 = LzLibrary.stripQueryString(url_$0);
var lib_$1 = null;
var libs_$2 = LzLibrary.libraries;
for (var l_$3 in libs_$2) {
var libhref_$4 = LzLibrary.stripQueryString(libs_$2[l_$3].href);
if (libhref_$4 == url_$0) {
lib_$1 = libs_$2[l_$3];
break
}};
if (lib_$1 == null) {
Debug.error("could not find library with href", url_$0)
} else {
lib_$1.loaded = true;
lib_$1.parent.__LzLibraryLoaded(lib_$1.name)
}};
$lzsc$temp["displayName"] = "__LZsnippetLoaded";
return $lzsc$temp
})()]);
lz[LzLibrary.tagname] = LzLibrary;
var LzTextSprite = (function () {
var $lzsc$temp = function (owner_$0) {
if (owner_$0 == null) return;
this.constructor = arguments.callee;
this.owner = owner_$0;
this.uid = LzSprite.prototype.uid++;
this.__csscache = {};
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lztextcontainer";
this.scrolldiv = this.__LZtextdiv = document.createElement("div");
this.scrolldivtagname = "div";
this.scrolldiv.owner = this;
if (this.quirks.emulate_flash_font_metrics) {
this.className = this.scrolldiv.className = "lzswftext"
} else {
this.className = this.scrolldiv.className = "lztext"
};
this.__LZdiv.appendChild(this.scrolldiv);
this.__LZdiv.owner = this;
if (this.quirks.ie_leak_prevention) {
this.__sprites[this.uid] = this
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#14/20";
return $lzsc$temp
})();
LzTextSprite.prototype = new LzSprite(null);
LzTextSprite.prototype._dbg_typename = "LzTextSprite";
LzTextSprite.prototype.__initTextProperties = (function () {
var $lzsc$temp = function (args_$0) {
this.setFontName(args_$0.font);
this.setFontStyle(args_$0.fontstyle);
this.setFontSize(args_$0.fontsize);
this.setTextColor(args_$0.fgcolor)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#46/47";
return $lzsc$temp
})();
LzTextSprite.prototype._fontStyle = "normal";
LzTextSprite.prototype._fontWeight = "normal";
LzTextSprite.prototype._fontSize = "11px";
LzTextSprite.prototype._fontFamily = "Verdana,Vera,sans-serif";
LzTextSprite.prototype._whiteSpace = "nowrap";
LzTextSprite.prototype._textAlign = "left";
LzTextSprite.prototype._textIndent = 0;
LzTextSprite.prototype.__LZtextIndent = 0;
LzTextSprite.prototype._letterSpacing = 0;
LzTextSprite.prototype._textDecoration = "none";
LzTextSprite.prototype.__wpadding = 4;
LzTextSprite.prototype.__hpadding = 4;
LzTextSprite.prototype.__sizecacheupperbound = 1000;
LzTextSprite.prototype.selectable = false;
LzTextSprite.prototype.text = "";
LzTextSprite.prototype.resize = true;
LzTextSprite.prototype.restrict = null;
LzTextSprite.prototype.scrolldiv = null;
LzTextSprite.prototype.scrolldivtagname = null;
LzTextSprite.prototype.setFontSize = (function () {
var $lzsc$temp = function (fsize_$0) {
if (fsize_$0 == null || fsize_$0 < 0) return;
var fp_$1 = this.CSSDimension(fsize_$0);
if (this._fontSize != fp_$1) {
this._fontSize = fp_$1;
this.scrolldiv.style.fontSize = fp_$1;
if (this.quirks["emulate_flash_font_metrics"]) {
var lh_$2 = Math.round(fsize_$0 * 1.2);
this.scrolldiv.style.lineHeight = this.CSSDimension(lh_$2);
this._lineHeight = lh_$2
};
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#76/38";
return $lzsc$temp
})();
LzTextSprite.prototype.setFontStyle = (function () {
var $lzsc$temp = function (fstyle_$0) {
var fweight_$1;
if (fstyle_$0 == "plain") {
fweight_$1 = "normal";
fstyle_$0 = "normal"
} else if (fstyle_$0 == "bold") {
fweight_$1 = "bold";
fstyle_$0 = "normal"
} else if (fstyle_$0 == "italic") {
fweight_$1 = "normal";
fstyle_$0 = "italic"
} else if (fstyle_$0 == "bold italic" || fstyle_$0 == "bolditalic") {
fweight_$1 = "bold";
fstyle_$0 = "italic"
};
var changed_$2 = false;
if (fweight_$1 != this._fontWeight) {
this._fontWeight = fweight_$1;
this.scrolldiv.style.fontWeight = fweight_$1;
changed_$2 = true
};
if (fstyle_$0 != this._fontStyle) {
this._fontStyle = fstyle_$0;
this.scrolldiv.style.fontStyle = fstyle_$0;
changed_$2 = true
};
if (changed_$2) {
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#95/39";
return $lzsc$temp
})();
LzTextSprite.prototype.setFontName = (function () {
var $lzsc$temp = function (fname_$0) {
if (fname_$0 != this._fontFamily) {
this._fontFamily = fname_$0;
this.scrolldiv.style.fontFamily = fname_$0;
this.__updatefieldsize()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#129/38";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextColor = (function () {
var $lzsc$temp = function (c_$0) {
if (this.textcolor === c_$0) return;
this.textcolor = c_$0;
this.__LZdiv.style.color = LzColorUtils.inttohex(c_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#137/39";
return $lzsc$temp
})();
LzTextSprite.prototype.lineHeight = null;
LzTextSprite.prototype.scrollTop = null;
LzTextSprite.prototype.scrollHeight = null;
LzTextSprite.prototype.scrollLeft = null;
LzTextSprite.prototype.scrollWidth = null;
LzTextSprite.prototype.scrolling = false;
LzTextSprite.prototype.setScrolling = (function () {
var $lzsc$temp = function (on_$0) {
var sdc_$1 = this.className;
if (sdc_$1 == "lzswftext" || sdc_$1 == "lzswfinputtextmultiline") {
var ht_$2 = this.height;
var wt_$3 = this.width;
var cdim_$4 = this.CSSDimension;
if (on_$0 || sdc_$1 == "lzswfinputtextmultiline") {
this.scrolling = on_$0;
this.applyCSS("overflow", "scroll", "scrolldiv");
ht_$2 += this.quirks.scrollbar_width;
wt_$3 += this.quirks.scrollbar_width
} else {
this.scrolling = false;
this.applyCSS("overflow", "", "scrolldiv")
};
var scrolldiv_$5 = this.scrolldiv;
var hp_$6 = cdim_$4(ht_$2);
var wp_$7 = cdim_$4(wt_$3);
if (on_$0) {
scrolldiv_$5.style.clip = "rect(0 " + wp_$7 + " " + hp_$6 + " 0)"
} else if (scrolldiv_$5.style.clip) {
scrolldiv_$5.style.clip = this.quirks["fix_ie_css_syntax"] ? "rect(auto auto auto auto)" : ""
};
scrolldiv_$5.style.width = wp_$7;
scrolldiv_$5.style.height = hp_$6
};
return on_$0 && this.scrolling
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#154/39";
return $lzsc$temp
})();
LzTextSprite.prototype.scrollevents = false;
LzTextSprite.prototype.setScrollEvents = (function () {
var $lzsc$temp = function (on_$0) {
this.scrollevents = this.setScrolling(on_$0);
this.__updatefieldsize()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#193/42";
return $lzsc$temp
})();
LzTextSprite.prototype.__updatefieldsizeTID = null;
LzTextSprite.prototype.__updatefieldsize = (function () {
var $lzsc$temp = function () {
var loaded_$0 = LzFontManager.isFontLoaded(this, this._fontFamily, this._fontStyle, this._fontWeight);
if (!loaded_$0 || !this.initted) return;
this.owner._updateSize();
var cstr_$1 = lz.BrowserUtils.getcallbackfunc(this, "__updatefieldsizeCallback", []);
if (this.__updatefieldsizeTID != null) {
clearTimeout(this.__updatefieldsizeTID)
};
this.__updatefieldsizeTID = setTimeout(cstr_$1, 0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#204/44";
return $lzsc$temp
})();
LzTextSprite.prototype.__fontLoaded = (function () {
var $lzsc$temp = function () {
this._cachevalue = this._cacheStyleKey = this._cacheTextKey = null;
this.__updatefieldsize()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#216/39";
return $lzsc$temp
})();
LzTextSprite.prototype.__updatefieldsizeCallback = (function () {
var $lzsc$temp = function () {
var lineHeight_$0 = this.getLineHeight();
if (this.lineHeight !== lineHeight_$0) {
this.lineHeight = lineHeight_$0;
this.owner.scrollevent("lineHeight", lineHeight_$0)
};
if (!this.scrollevents) return;
this.__updatefieldprop("scrollHeight");
this.__updatefieldprop("scrollTop");
this.__updatefieldprop("scrollWidth");
this.__updatefieldprop("scrollLeft")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#222/52";
return $lzsc$temp
})();
LzTextSprite.prototype.setMaxLength = (function () {
var $lzsc$temp = function (val_$0) {
return
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#242/39";
return $lzsc$temp
})();
LzTextSprite.prototype.__updatefieldprop = (function () {
var $lzsc$temp = function (name_$0) {
var val_$1 = this.scrolldiv[name_$0];
if (this[name_$0] !== val_$1 || name_$0 == "scrollHeight") {
this[name_$0] = val_$1;
this.owner.scrollevent(name_$0, val_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#247/44";
return $lzsc$temp
})();
LzTextSprite.prototype.setText = (function () {
var $lzsc$temp = function (t_$0, force_$1) {
if (this.multiline && t_$0 && t_$0.indexOf("\n") >= 0) {
if (this.quirks["inner_html_strips_newlines"]) {
t_$0 = t_$0.replace(this.inner_html_strips_newlines_re, "<br/>")
}};
if (t_$0 && this.quirks["inner_html_no_entity_apos"]) {
t_$0 = t_$0.replace(RegExp("&apos;", "mg"), "&#39;")
};
if (force_$1 != true && this.text == t_$0) return;
this.text = t_$0;
this.scrolldiv.innerHTML = t_$0;
this.__updatefieldsize()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#261/34";
return $lzsc$temp
})();
LzTextSprite.prototype.multiline = false;
LzTextSprite.prototype.setMultiline = (function () {
var $lzsc$temp = function (m_$0) {
this.multiline = m_$0;
if (m_$0) {
var whitespace_$1 = "normal";
this.applyCSS("overflow", "hidden", "scrolldiv")
} else {
var whitespace_$1 = this.className === "lzswfinputtextmultiline" ? "pre-wrap" : "nowrap";
this.applyCSS("overflow", "", "scrolldiv")
};
if (this._whiteSpace !== whitespace_$1) {
this._whiteSpace = whitespace_$1;
this.scrolldiv.style.whiteSpace = whitespace_$1
};
if (this.quirks["text_height_includes_padding"]) {
this.__hpadding = m_$0 ? 3 : 4
};
this.setText(this.text, true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#289/39";
return $lzsc$temp
})();
LzTextSprite.prototype.setPattern = (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 == null || val_$0 == "") {
this.restrict = null
} else if (val_$0.substring(0, 1) == "[" && val_$0.substring(val_$0.length - 2, val_$0.length) == "]*") {
this.restrict = RegExp(val_$0.substring(0, val_$0.length - 1) + "|[\\r\\n]", "g")
} else {
Debug.error('LzTextSprite.setPattern argument %w must be of the form "[...]*"', val_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#321/37";
return $lzsc$temp
})();
LzTextSprite.prototype.getTextWidth = (function () {
var $lzsc$temp = function (force_$0) {
if (!this.initted && !force_$0) return 0;
var width_$1;
var styleKey_$2 = this.className + "/" + this.scrolldiv.style.cssText;
var cv_$3 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$2 && this._cacheTextKey == this.text && ("width" in cv_$3)) {
width_$1 = cv_$3.width
} else {
width_$1 = this.getTextDimension("width")
};
if (width_$1 != 0 && this.quirks["emulate_flash_font_metrics"]) {
width_$1 += this.__wpadding
};
return width_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#340/39";
return $lzsc$temp
})();
LzTextSprite.prototype.getLineHeight = (function () {
var $lzsc$temp = function () {
if (this._lineHeight) return this._lineHeight;
if (!this.initted) return 0;
var styleKey_$0 = this.className + "/" + this.scrolldiv.style.cssText;
var cv_$1 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$0 && ("lineheight" in cv_$1)) {
var lineheight_$2 = cv_$1.lineheight
} else {
var lineheight_$2 = this.getTextDimension("lineheight")
};
this._lineHeight = lineheight_$2;
return lineheight_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#363/40";
return $lzsc$temp
})();
LzTextSprite.prototype.getTextfieldHeight = (function () {
var $lzsc$temp = function (force_$0) {
if (!this.initted && !force_$0) return 0;
var fieldHeight_$1 = null;
if (this.multiline && this.text != "") {
var styleKey_$2 = this.className + "/" + this.scrolldiv.style.cssText;
var cv_$3 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$2 && this._cacheTextKey == this.text && ("height" in cv_$3)) {
fieldHeight_$1 = cv_$3.height
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
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#384/45";
return $lzsc$temp
})();
LzTextSprite.prototype._sizecache = {counter: 0};
if (LzSprite.quirks.ie_leak_prevention) {
LzTextSprite.prototype.__divstocleanup = [];
LzTextSprite.prototype.__cleanupdivs = (function () {
var $lzsc$temp = function () {
var obj_$0 = LzTextSprite.prototype.__divstocleanup;
var func_$1 = LzSprite.prototype.__discardElement;
var l_$2 = obj_$0.length;
for (var i_$3 = 0;i_$3 < l_$2;i_$3++) {
func_$1(obj_$0[i_$3])
};
LzTextSprite.prototype.__divstocleanup = []
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#424/44";
return $lzsc$temp
})()
};
LzTextSprite.prototype._cacheStyleKey = null;
LzTextSprite.prototype._cacheTextKey = null;
LzTextSprite.prototype._cachevalue = null;
LzTextSprite.prototype.getTextDimension = (function () {
var $lzsc$temp = function (dimension_$0) {
var string_$1 = this.text;
var width_$2 = "auto";
switch (dimension_$0) {
case "lineheight":
if (this._lineHeight) {
return this._lineHeight
}if (LzSprite.prototype.quirks["textmeasurementalphastring"]) {
string_$1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
} else {
string_$1 = 'Yq_gy"9;'
}break;;case "height":
width_$2 = this.CSSDimension(this.width);break;;case "width":
if (this.text == "") {
return 0
}break;;default:
{
Debug.error("Unknown dimension: %w", dimension_$0)
}};
var scrolldiv_$3 = this.scrolldiv;
var className_$4 = this.className;
var styleKey_$5 = className_$4 + "/" + scrolldiv_$3.style.cssText;
var cv_$6 = this._cachevalue;
if (this._cacheStyleKey == styleKey_$5 && (dimension_$0 == "lineheight" || this._cacheTextKey == string_$1) && (dimension_$0 in cv_$6)) {
return cv_$6[dimension_$0]
};
this._cacheStyleKey = styleKey_$5;
if (dimension_$0 != "lineheight") {
this._cacheTextKey = string_$1
};
var style_$7 = "padding:0px;overflow:visible;width:" + width_$2 + ";height:auto;" + (this._fontSize ? "font-size:" + this._fontSize + ";" : "") + (this._fontWeight ? "font-weight:" + this._fontWeight + ";" : "") + (this._fontStyle ? "font-style:" + this._fontStyle + ";" : "") + (this._fontFamily ? "font-family:" + this._fontFamily + ";" : "") + (this._fontSize ? "font-size:" + this._fontSize + ";" : "") + (this._letterSpacing ? "letter-spacing:" + this._letterSpacing + ";" : "") + (this._whiteSpace ? "white-space:" + this._whiteSpace + ";" : "");
this._cachevalue = LzFontManager.getSize(dimension_$0, className_$4, style_$7, this.scrolldivtagname, string_$1);
return this._cachevalue[dimension_$0]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#445/43";
return $lzsc$temp
})();
LzTextSprite.prototype.setSelectable = (function () {
var $lzsc$temp = function (s_$0) {
this.selectable = s_$0;
this.__LZdiv.style.cursor = s_$0 ? "auto" : "";
if (this.quirks["prevent_selection_with_onselectstart"]) {
var handler_$1 = s_$0 ? this.__onselectstartHandler : this.__cancelhandler;
this.__LZdiv.onselectstart = handler_$1
} else {
var selectstyle_$2 = s_$0 ? "text" : "none";
var stylename_$3 = LzSprite.__styleNames.userSelect;
this.__LZdiv.style[stylename_$3] = selectstyle_$2
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#512/40";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$__mouseEvent = LzSprite.prototype.__mouseEvent;
LzTextSprite.prototype.__mouseEvent = (function () {
var $lzsc$temp = function (eventname_$0) {
this.$LzSprite$__mouseEvent(eventname_$0);
return this.selectable
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#529/39";
return $lzsc$temp
})();
LzTextSprite.prototype.__onselectstartHandler = (function () {
var $lzsc$temp = function (e_$0) {
e_$0 = e_$0 || window.event;
e_$0.cancelBubble = true;
return true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#539/49";
return $lzsc$temp
})();
LzTextSprite.prototype.__cancelhandler = (function () {
var $lzsc$temp = function () {
return false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#546/42";
return $lzsc$temp
})();
LzTextSprite.prototype.setResize = (function () {
var $lzsc$temp = function (r_$0) {
this.resize = r_$0;
this.applyCSS("overflow", r_$0 ? "" : "hidden", "scrolldiv")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#550/36";
return $lzsc$temp
})();
LzTextSprite.prototype.setSelection = (function () {
var $lzsc$temp = function (start_$0, end_$1) {
switch (arguments.length) {
case 1:
end_$1 = null
};
if (end_$1 == null) {
end_$1 = start_$0
};
if (this.quirks["text_selection_use_range"]) {
var range_$2 = document.body.createTextRange();
range_$2.moveToElementText(this.scrolldiv);
if (start_$0 > end_$1) {
var st_$3 = start_$0;
start_$0 = end_$1;
end_$1 = st_$3
};
var st_$3 = start_$0;
var ed_$4 = end_$1 - range_$2.text.length;
range_$2.moveStart("character", st_$3);
range_$2.moveEnd("character", ed_$4);
range_$2.select()
} else {
var range_$2 = document.createRange();
var offset_$5 = 0;
range_$2.setStart(this.scrolldiv.childNodes[0], start_$0);
range_$2.setEnd(this.scrolldiv.childNodes[0], end_$1);
var sel_$6 = window.getSelection();
sel_$6.removeAllRanges();
sel_$6.addRange(range_$2)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#555/39";
return $lzsc$temp
})();
LzTextSprite.prototype.__findNodeByOffset = (function () {
var $lzsc$temp = function (offset_$0) {
var node_$1 = this.scrolldiv.childNodes[0];
var curroffset_$2 = 0;
while (node_$1) {
if (node_$1.nodeType == 3) {
offset_$0 += node_$1.textContent.length
} else if (node_$1.nodeType == 1 && node_$1.nodeName == "BR") {
offset_$0 += 1
};
if (curroffset_$2 >= offset_$0) {
return node_$1
};
node_$1 = node_$1.nextSibling
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#588/45";
return $lzsc$temp
})();
LzTextSprite.prototype.__getGlobalRange = (function () {
var $lzsc$temp = function () {
var userSelection_$0;
if (this.quirks["text_selection_use_range"]) {
userSelection_$0 = document.selection.createRange()
} else if (window.getSelection) {
userSelection_$0 = window.getSelection()
};
try {
if (userSelection_$0) {
if (this.quirks["text_selection_use_range"]) {
return userSelection_$0
} else if (userSelection_$0.getRangeAt) {
return userSelection_$0.getRangeAt(0)
} else {
var range_$1 = document.createRange();
range_$1.setStart(userSelection_$0.anchorNode, userSelection_$0.anchorOffset);
range_$1.setEnd(userSelection_$0.focusNode, userSelection_$0.focusOffset);
return range_$1
}}}
catch (e_$2) {}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#605/43";
return $lzsc$temp
})();
LzTextSprite.prototype.__textareaToRange = (function () {
var $lzsc$temp = function (textarea_$0) {
var bookmark_$1 = textarea_$0.getBookmark();
var contents_$2, originalContents_$3;
originalContents_$3 = contents_$2 = this.scrolldiv.innerHTML;
var owner_$4 = this.__getRangeOwner(textarea_$0);
if (!(owner_$4 instanceof LzTextSprite)) {
return
};
do {
var marker_$5 = "~~~" + Math.random() + "~~~"
} while (contents_$2.indexOf(marker_$5) != -1);
textarea_$0.text = marker_$5 + textarea_$0.text + marker_$5;
contents_$2 = this.scrolldiv.innerHTML;
contents_$2 = contents_$2.replace("<BR>", " ");
var range_$6 = {};
range_$6.startOffset = contents_$2.indexOf(marker_$5);
contents_$2 = contents_$2.replace(marker_$5, "");
range_$6.endOffset = contents_$2.indexOf(marker_$5);
this.scrolldiv.innerHTML = originalContents_$3;
textarea_$0.moveToBookmark(bookmark_$1);
textarea_$0.select();
return range_$6
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#632/44";
return $lzsc$temp
})();
LzTextSprite.prototype.__getRangeOwner = (function () {
var $lzsc$temp = function (range_$0) {
if (!range_$0) return;
if (this.quirks["text_selection_use_range"]) {
var range_$0 = range_$0.duplicate();
range_$0.collapse();
return range_$0.parentElement().owner
} else {
if (range_$0.startContainer.parentNode == range_$0.endContainer.parentNode) return range_$0.startContainer.parentNode.owner
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#665/42";
return $lzsc$temp
})();
LzTextSprite.prototype.__getOffset = (function () {
var $lzsc$temp = function (node_$0) {
var offset_$1 = 0;
while (node_$0 = node_$0.previousSibling) {
if (node_$0.nodeType == 3) {
offset_$1 += node_$0.textContent.length
} else if (node_$0.nodeType == 1 && node_$0.nodeName == "BR") {
offset_$1 += 1
}};
return offset_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#677/38";
return $lzsc$temp
})();
LzTextSprite.prototype.getSelectionPosition = (function () {
var $lzsc$temp = function () {
var range_$0 = this.__getGlobalRange();
if (this.__getRangeOwner(range_$0) === this) {
if (this.quirks["text_selection_use_range"]) {
range_$0 = this.__textareaToRange(range_$0);
return range_$0.startOffset
} else {
var offset_$1 = 0;
if (this.multiline) {
offset_$1 = this.__getOffset(range_$0.startContainer)
};
return range_$0.startOffset + offset_$1
}} else {
return -1
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#690/47";
return $lzsc$temp
})();
LzTextSprite.prototype.getSelectionSize = (function () {
var $lzsc$temp = function () {
var range_$0 = this.__getGlobalRange();
if (this.__getRangeOwner(range_$0) === this) {
if (this.quirks["text_selection_use_range"]) {
range_$0 = this.__textareaToRange(range_$0)
} else {
if (this.multiline) {
var startoffset_$1 = this.__getOffset(range_$0.startContainer);
var endoffset_$2 = this.__getOffset(range_$0.endContainer);
return range_$0.endOffset + endoffset_$2 - (range_$0.startOffset + startoffset_$1)
}};
return range_$0.endOffset - range_$0.startOffset
} else {
return -1
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#708/43";
return $lzsc$temp
})();
LzTextSprite.prototype.getScroll = (function () {
var $lzsc$temp = function () {
Debug.warn("LzTextSprite.getScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#726/36";
return $lzsc$temp
})();
LzTextSprite.prototype.getMaxScroll = (function () {
var $lzsc$temp = function () {
Debug.warn("LzTextSprite.getMaxScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#733/39";
return $lzsc$temp
})();
LzTextSprite.prototype.setScroll = (function () {
var $lzsc$temp = function () {
Debug.warn("LzTextSprite.setScroll is not implemented yet.")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#740/36";
return $lzsc$temp
})();
LzTextSprite.prototype.setYScroll = (function () {
var $lzsc$temp = function (n_$0) {
this.scrolldiv.scrollTop = this.scrollTop = -n_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#747/37";
return $lzsc$temp
})();
LzTextSprite.prototype.setXScroll = (function () {
var $lzsc$temp = function (n_$0) {
this.scrolldiv.scrollLeft = this.scrollLeft = -n_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#751/37";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$setX = LzSprite.prototype.setX;
LzTextSprite.prototype.setX = (function () {
var $lzsc$temp = function (x_$0) {
var scrolling_$1 = this.scrolling;
var turd_$2 = scrolling_$1 && this.quirks["clipped_scrollbar_causes_display_turd"];
if (scrolling_$1) {
var scrolldiv_$3 = this.scrolldiv;
var oldLeft_$4 = scrolldiv_$3.scrollLeft;
var oldTop_$5 = scrolldiv_$3.scrollTop;
if (turd_$2) {
this.applyCSS("overflow", "hidden", "scrolldiv");
scrolldiv_$3.style.paddingRight = scrolldiv_$3.style.paddingBottom = this.quirks.scrollbar_width
}};
this.$LzSprite$setX(x_$0);
if (scrolling_$1) {
if (turd_$2) {
this.applyCSS("overflow", "scroll", "scrolldiv");
scrolldiv_$3.style.paddingRight = scrolldiv_$3.style.paddingBottom = "0"
};
scrolldiv_$3.scrollLeft = oldLeft_$4;
scrolldiv_$3.scrollTop = oldTop_$5
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#756/31";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$setY = LzSprite.prototype.setY;
LzTextSprite.prototype.setY = (function () {
var $lzsc$temp = function (y_$0) {
var scrolling_$1 = this.scrolling;
var turd_$2 = scrolling_$1 && this.quirks["clipped_scrollbar_causes_display_turd"];
if (scrolling_$1) {
var scrolldiv_$3 = this.scrolldiv;
var oldLeft_$4 = scrolldiv_$3.scrollLeft;
var oldTop_$5 = scrolldiv_$3.scrollTop;
if (turd_$2) {
this.applyCSS("overflow", "hidden", "scrolldiv");
scrolldiv_$3.style.paddingRight = scrolldiv_$3.style.paddingBottom = this.quirks.scrollbar_width
}};
this.$LzSprite$setY(y_$0);
if (scrolling_$1) {
if (turd_$2) {
this.applyCSS("overflow", "scroll", "scrolldiv");
scrolldiv_$3.style.paddingRight = scrolldiv_$3.style.paddingBottom = "0"
};
scrolldiv_$3.scrollLeft = oldLeft_$4;
scrolldiv_$3.scrollTop = oldTop_$5
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#780/31";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$setWidth = LzSprite.prototype.setWidth;
LzTextSprite.prototype.__widthcss = 0;
LzTextSprite.prototype.setWidth = (function () {
var $lzsc$temp = function (w_$0) {
if (w_$0 == null || w_$0 < 0 || isNaN(w_$0)) return;
var nw_$1 = this.$LzSprite$setWidth(w_$0);
if (nw_$1 == null) return;
var scrolldivwidth_$2 = w_$0 >= this.__wpadding ? w_$0 - this.__wpadding : 0;
if (this.scrolling) {
scrolldivwidth_$2 += this.quirks.scrollbar_width
};
var clipwidth_$3 = scrolldivwidth_$2;
var wtInd_$4 = this.__LZtextIndent < 0 ? -1 * this.__LZtextIndent : 0;
if (scrolldivwidth_$2 >= wtInd_$4) {
scrolldivwidth_$2 -= wtInd_$4
};
var cdim_$5 = this.CSSDimension;
var scrolldivcss_$6 = cdim_$5(scrolldivwidth_$2);
if (scrolldivcss_$6 !== this.__widthcss) {
this.__widthcss = scrolldivcss_$6;
var scrolldiv_$7 = this.scrolldiv;
scrolldiv_$7.style.width = scrolldivcss_$6;
if (this.scrolling) {
var hp_$8 = cdim_$5(this.height || 0);
var wp_$9 = cdim_$5(clipwidth_$3);
scrolldiv_$7.style.clip = "rect(0 " + wp_$9 + " " + hp_$8 + " 0)"
}};
return scrolldivwidth_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#805/35";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$setHeight = LzSprite.prototype.setHeight;
LzTextSprite.prototype.__heightcss = 0;
LzTextSprite.prototype.setHeight = (function () {
var $lzsc$temp = function (h_$0) {
if (h_$0 == null || h_$0 < 0 || isNaN(h_$0)) return;
var nh_$1 = this.$LzSprite$setHeight(h_$0);
if (nh_$1 == null) return;
var ht_$2 = h_$0 >= this.__hpadding ? h_$0 - this.__hpadding : 0;
if (this.scrolling || this.classname == "lzswfinputtextmultiline") {
ht_$2 += this.quirks.scrollbar_width
};
var cdim_$3 = this.CSSDimension;
var hp_$4 = cdim_$3(ht_$2);
if (hp_$4 !== this.__heightcss) {
this.__heightcss = hp_$4;
var scrolldiv_$5 = this.scrolldiv;
scrolldiv_$5.style.height = hp_$4;
if (this.scrolling) {
var wp_$6 = cdim_$3(this.width || 0);
scrolldiv_$5.style.clip = "rect(0 " + wp_$6 + " " + hp_$4 + " 0)"
}};
return ht_$2
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#853/36";
return $lzsc$temp
})();
LzTextSprite.prototype.enableClickableLinks = (function () {
var $lzsc$temp = function (enabled_$0) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#897/47";
return $lzsc$temp
})();
LzTextSprite.prototype.makeTextLink = (function () {
var $lzsc$temp = function (str_$0, value_$1) {
LzTextSprite.addLinkID(this);
var uid_$2 = this.uid;
return '<span class="lztextlink" onclick="javascript:$modules.lz.__callTextLink(\'' + uid_$2 + "', '" + value_$1 + "')\">" + str_$0 + "</span>"
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#900/39";
return $lzsc$temp
})();
$modules.lz.__callTextLink = (function () {
var $lzsc$temp = function (spriteID_$0, val_$1) {
var sprite_$2 = LzTextSprite.linkIDMap[spriteID_$0];
if (sprite_$2 != null) {
sprite_$2.owner.ontextlink.sendEvent(val_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#907/30";
return $lzsc$temp
})();
LzTextSprite.linkIDMap = [];
LzTextSprite.addLinkID = (function () {
var $lzsc$temp = function (sprite_$0) {
LzTextSprite.linkIDMap[sprite_$0.uid] = sprite_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#919/26";
return $lzsc$temp
})();
LzTextSprite.deleteLinkID = (function () {
var $lzsc$temp = function (UID_$0) {
delete LzTextSprite.linkIDMap[UID_$0]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#924/29";
return $lzsc$temp
})();
LzTextSprite.prototype.$LzSprite$destroy = LzSprite.prototype.destroy;
LzTextSprite.prototype.destroy = (function () {
var $lzsc$temp = function () {
LzTextSprite.deleteLinkID(this.owner.getUID());
this.$LzSprite$destroy(this)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#929/34";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextAlign = (function () {
var $lzsc$temp = function (align_$0) {
if (this._textAlign != align_$0) {
this._textAlign = align_$0;
this.scrolldiv.style.textAlign = align_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#934/39";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextIndent = (function () {
var $lzsc$temp = function (indent_$0) {
var indentPx_$1 = this.CSSDimension(indent_$0);
if (this._textIndent != indentPx_$1) {
var negInd_$2 = indent_$0 < 0 || this.__LZtextIndent < 0;
this._textIndent = indentPx_$1;
this.__LZtextIndent = indent_$0;
this.scrolldiv.style.textIndent = indentPx_$1;
if (negInd_$2) {
this.scrolldiv.style.paddingLeft = indent_$0 >= 0 ? "" : indentPx_$1.substr(1);
var nw_$3 = this.setWidth(this.width)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#941/40";
return $lzsc$temp
})();
LzTextSprite.prototype.setLetterSpacing = (function () {
var $lzsc$temp = function (spacing_$0) {
spacing_$0 = this.CSSDimension(spacing_$0);
if (this._letterSpacing != spacing_$0) {
this._letterSpacing = spacing_$0;
this.scrolldiv.style.letterSpacing = spacing_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#959/43";
return $lzsc$temp
})();
LzTextSprite.prototype.setTextDecoration = (function () {
var $lzsc$temp = function (decoration_$0) {
if (this._textDecoration != decoration_$0) {
this._textDecoration = decoration_$0;
this.scrolldiv.style.textDecoration = decoration_$0
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#968/44";
return $lzsc$temp
})();
LzTextSprite.prototype.getDisplayObject = (function () {
var $lzsc$temp = function () {
return this.scrolldiv
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#975/43";
return $lzsc$temp
})();
LzTextSprite.prototype.updateShadow = (function () {
var $lzsc$temp = function (shadowcolor_$0, shadowdistance_$1, shadowangle_$2, shadowblurradius_$3) {
var cssString_$4 = this.__getShadowCSS(shadowcolor_$0, shadowdistance_$1, shadowangle_$2, shadowblurradius_$3);
if (this.quirks.use_filter_for_dropshadow) {
this.scrolldiv.style.filter = this.setFilter("shadow", cssString_$4)
} else {
this.scrolldiv.style.textShadow = cssString_$4
};
this.shadow = cssString_$4;
this.applyCSS("overflow", "", "scrolldiv")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTextSprite.js#979/39";
return $lzsc$temp
})();
var LzInputTextSprite = (function () {
var $lzsc$temp = function (owner_$0) {
if (owner_$0 == null) return;
this.constructor = arguments.callee;
this.owner = owner_$0;
this.uid = LzSprite.prototype.uid++;
this.__csscache = {};
this.__LZdiv = document.createElement("div");
this.__LZdiv.className = "lzinputtextcontainer";
this.__LZdiv.owner = this;
if (this.quirks.autoscroll_textarea) {
this.dragging = false
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
LzInputTextSprite.prototype.__createInputText = (function () {
var $lzsc$temp = function (t_$0) {
if (this.__LzInputDiv) return;
var type_$1 = "";
if (this.owner) {
if (this.owner.password) {
type_$1 = "password"
} else if (this.owner.multiline) {
type_$1 = "multiline"
}};
this.__createInputDiv(type_$1);
t_$0 = t_$0 || "";
lz.embed.__setAttr(this.__LzInputDiv, "value", t_$0);
if (this.quirks.fix_clickable) {
if (this.quirks.fix_ie_clickable) {
this.__LZinputclickdiv = document.createElement("img");
this.__LZinputclickdiv.src = LzSprite.blankimage
} else {
this.__LZinputclickdiv = document.createElement("div")
};
this.__LZinputclickdiv.className = "lzclickdiv";
this.__LZinputclickdiv.owner = this;
this.setClickable(true);
if (!this.__LZclickcontainerdiv) {
this.__LZclickcontainerdiv = this.__createContainerDivs("click")
};
if (this.quirks.input_highlight_bug) {
var ffoxdiv_$2 = document.createElement("div");
ffoxdiv_$2.style.backgroundColor = "white";
ffoxdiv_$2.style.width = 0;
this.__LZclickcontainerdiv.appendChild(ffoxdiv_$2);
ffoxdiv_$2.appendChild(this.__LZinputclickdiv)
} else {
this.__LZclickcontainerdiv.appendChild(this.__LZinputclickdiv)
}};
this.__LZdiv.appendChild(this.__LzInputDiv);
this.__setTextEvents(true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#71/49";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__createInputDiv = (function () {
var $lzsc$temp = function (type_$0) {
var tagname_$1 = "input";
if (type_$0 === "password") {
this.multiline = false;
this.__LzInputDiv = document.createElement(tagname_$1);
lz.embed.__setAttr(this.__LzInputDiv, "type", "password")
} else if (type_$0 === "multiline") {
tagname_$1 = "textarea";
this.multiline = true;
this.__LzInputDiv = document.createElement(tagname_$1)
} else {
this.multiline = false;
this.__LzInputDiv = document.createElement(tagname_$1);
lz.embed.__setAttr(this.__LzInputDiv, "type", "text")
};
if (this.quirks.firefox_autocomplete_bug) {
lz.embed.__setAttr(this.__LzInputDiv, "autocomplete", "off")
};
this.__LzInputDiv.owner = this;
if (this.quirks.emulate_flash_font_metrics) {
if (this.multiline) {
this.className = this.__LzInputDiv.className = "lzswfinputtextmultiline";
this._whiteSpace = "pre-wrap"
} else {
this.className = this.__LzInputDiv.className = "lzswfinputtext"
}} else {
this.className = this.__LzInputDiv.className = "lzinputtext"
};
if (this.owner) {
lz.embed.__setAttr(this.__LzInputDiv, "name", this.owner.name)
};
this.scrolldiv = this.__LzInputDiv;
this.scrolldivtagname = tagname_$1;
this.scrolldiv.owner = this;
this.setScrolling(this.multiline)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#129/48";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setMultiline = (function () {
var $lzsc$temp = function (ml_$0) {
var oldval_$1 = this.multiline;
this.multiline = ml_$0;
if (this.multiline !== oldval_$1) {
var olddiv_$2 = this.__LzInputDiv;
this.__setTextEvents(false);
this.__createInputDiv(ml_$0 ? "multiline" : "");
var newdiv_$3 = this.__LzInputDiv;
lz.embed.__setAttr(newdiv_$3, "style", olddiv_$2.style.cssText);
if (this.quirks["fix_ie_css_syntax"]) {
newdiv_$3.style.fontStyle = olddiv_$2.style.fontStyle;
newdiv_$3.style.fontWeight = olddiv_$2.style.fontWeight;
newdiv_$3.style.fontSize = olddiv_$2.style.fontSize;
newdiv_$3.style.fontFamily = olddiv_$2.style.fontFamily;
newdiv_$3.style.color = olddiv_$2.style.color
};
var oldleft_$4 = olddiv_$2.scrollLeft;
var oldtop_$5 = olddiv_$2.scrollTop;
this.__discardElement(olddiv_$2);
this.__LZdiv.appendChild(newdiv_$3);
this.setScrollEvents(this.owner.scrollevents);
newdiv_$3.scrollLeft = oldleft_$4;
newdiv_$3.scrollTop = oldtop_$5;
this.__setTextEvents(true);
this.setText(this.text, true)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#171/44";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__show = (function () {
var $lzsc$temp = function () {
if (this.__shown == true || this.disabled == true) return;
this.__hideIfNotFocused();
LzInputTextSprite.prototype.__lastshown = this;
this.__shown = true;
if (this.quirks["inputtext_parents_cannot_contain_clip"]) {
var sprites_$0 = this.__findParents("clip", true);
var l_$1 = sprites_$0.length;
if (l_$1 > 1) {
if (this._shownclipvals == null) {
this._shownclipvals = [];
this._shownclippedsprites = sprites_$0;
for (var n_$2 = 0;n_$2 < l_$1;n_$2++) {
var v_$3 = sprites_$0[n_$2];
this._shownclipvals[n_$2] = v_$3.__LZclickcontainerdiv.style.clip;
var noclip_$4 = this.quirks["fix_ie_css_syntax"] ? "rect(auto auto auto auto)" : "";
v_$3.__LZclickcontainerdiv.style.clip = noclip_$4
}}}};
LzMouseKernel.setGlobalClickable(false);
if (LzSprite.quirks.prevent_selection) {
this.__LZdiv.onselectstart = this.__onselectstartHandler
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#213/38";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__hideIfNotFocused = (function () {
var $lzsc$temp = function (eventname_$0) {
var lzinppr_$1 = LzInputTextSprite.prototype;
if (lzinppr_$1.__lastshown == null) return;
var quirks_$2 = LzSprite.quirks;
if (quirks_$2.textgrabsinputtextfocus) {
var s_$3 = window.event;
if (s_$3 && s_$3.srcElement && s_$3.srcElement.owner && s_$3.srcElement.owner instanceof LzTextSprite) {
if (eventname_$0 == "onmousedown") {
lzinppr_$1.__lastshown.gotFocus()
};
return
}};
if (lzinppr_$1.__focusedSprite != lzinppr_$1.__lastshown) {
lzinppr_$1.__lastshown.__hide()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#247/50";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__hide = (function () {
var $lzsc$temp = function (ignore_$0) {
if (this.__shown != true || this.disabled == true) return;
if (LzInputTextSprite.prototype.__lastshown == this) {
LzInputTextSprite.prototype.__lastshown = null
};
this.__shown = false;
if (this.quirks["inputtext_parents_cannot_contain_clip"]) {
if (this._shownclipvals != null) {
for (var n_$1 = 0;n_$1 < this._shownclipvals.length;n_$1++) {
var v_$2 = this._shownclippedsprites[n_$1];
v_$2.__LZclickcontainerdiv.style.clip = this._shownclipvals[n_$1]
};
this._shownclipvals = null;
this._shownclippedsprites = null
}};
LzMouseKernel.setGlobalClickable(true);
if (LzSprite.quirks.prevent_selection) {
if (LzInputTextSprite.prototype.__lastshown == null) {
this.__LZdiv.onselectstart = LzTextSprite.prototype.__cancelhandler
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#267/38";
return $lzsc$temp
})();
LzInputTextSprite.prototype.gotBlur = (function () {
var $lzsc$temp = function () {
if (LzInputTextSprite.prototype.__focusedSprite != this) return;
this.deselect()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#299/39";
return $lzsc$temp
})();
LzInputTextSprite.prototype.gotFocus = (function () {
var $lzsc$temp = function () {
if (LzInputTextSprite.prototype.__focusedSprite == this) return;
this.select()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#306/40";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setText = (function () {
var $lzsc$temp = function (t_$0) {
if (this.capabilities["htmlinputtext"]) {
if (t_$0.indexOf("<br/>") != -1) {
t_$0 = t_$0.replace(this.br_to_newline_re, "\r")
}};
this.text = t_$0;
this.__createInputText(t_$0);
this.__LzInputDiv.value = t_$0;
this.__updatefieldsize()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#312/39";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__setTextEvents = (function () {
var $lzsc$temp = function (c_$0) {
var div_$1 = this.__LzInputDiv;
var f_$2 = c_$0 ? this.__textEvent : null;
div_$1.onblur = f_$2;
div_$1.onfocus = f_$2;
if (this.quirks.ie_mouse_events) {
div_$1.onmouseleave = f_$2
} else {
div_$1.onmouseout = f_$2
};
div_$1.onmousemove = f_$2;
div_$1.onmousedown = f_$2;
div_$1.onmouseup = f_$2;
div_$1.onkeyup = f_$2;
div_$1.onkeydown = f_$2;
div_$1.onkeypress = f_$2;
div_$1.onchange = f_$2;
if (this.quirks.ie_paste_event || this.quirks.safari_paste_event) {
div_$1.onpaste = c_$0 ? (function () {
var $lzsc$temp = function (e_$0) {
this.owner.__pasteHandlerEx(e_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#348/27";
return $lzsc$temp
})() : null
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#329/47";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__pasteHandlerEx = (function () {
var $lzsc$temp = function (evt_$0) {
var checkre_$1 = !(!this.restrict);
var checkml_$2 = this.multiline && this.owner.maxlength > 0;
if (checkre_$1 || checkml_$2) {
evt_$0 = evt_$0 || window.event;
if (this.quirks.safari_paste_event) {
var txt_$3 = evt_$0.clipboardData.getData("text/plain")
} else {
var txt_$3 = window.clipboardData.getData("TEXT");
txt_$3 = txt_$3.replace(this.____crregexp, "\n")
};
var stopPaste_$4 = false;
var selsize_$5 = this.getSelectionSize();
if (selsize_$5 < 0) selsize_$5 = 0;
if (checkre_$1) {
var matched_$6 = txt_$3.match(this.restrict);
if (matched_$6 == null) {
var newtxt_$7 = ""
} else {
var newtxt_$7 = matched_$6.join("")
};
stopPaste_$4 = newtxt_$7 != txt_$3;
txt_$3 = newtxt_$7
};
if (checkml_$2) {
var max_$8 = this.owner.maxlength + selsize_$5;
if (this.quirks.text_ie_carriagereturn) {
var len_$9 = this.__LzInputDiv.value.replace(this.____crregexp, "\n").length
} else {
var len_$9 = this.__LzInputDiv.value.length
};
var maxchars_$a = max_$8 - len_$9;
if (maxchars_$a > 0) {
if (txt_$3.length > maxchars_$a) {
txt_$3 = txt_$3.substring(0, maxchars_$a);
stopPaste_$4 = true
}} else {
txt_$3 = "";
stopPaste_$4 = true
}};
if (stopPaste_$4) {
evt_$0.returnValue = false;
if (evt_$0.preventDefault) {
evt_$0.preventDefault()
};
if (txt_$3.length > 0) {
if (this.quirks.safari_paste_event) {
var val_$b = this.__LzInputDiv.value;
var selpos_$c = this.getSelectionPosition();
this.__LzInputDiv.value = val_$b.substring(0, selpos_$c) + txt_$3 + val_$b.substring(selpos_$c + selsize_$5);
selpos_$c += txt_$3.length;
this.__LzInputDiv.setSelectionRange(selpos_$c, selpos_$c)
} else {
var range_$d = document.selection.createRange();
range_$d.text = txt_$3
}}}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#352/48";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__pasteHandler = (function () {
var $lzsc$temp = function () {
var selpos = this.getSelectionPosition();
var selsize = this.getSelectionSize();
var val = this.__LzInputDiv.value;
var that = this;
setTimeout((function () {
var $lzsc$temp = function () {
var checkre_$0 = !(!that.restrict);
var checkml_$1 = that.multiline && that.owner.maxlength > 0;
var newval_$2 = that.__LzInputDiv.value;
var newlen_$3 = newval_$2.length;
var max_$4 = that.owner.maxlength;
if (checkre_$0 || checkml_$1 && newlen_$3 > max_$4) {
var len_$5 = val.length;
var newc_$6 = newval_$2.substr(selpos, newlen_$3 - len_$5 + selsize);
if (checkre_$0) {
var matched_$7 = newc_$6.match(that.restrict);
newc_$6 = matched_$7 != null ? matched_$7.join("") : ""
};
if (checkml_$1) {
var maxchars_$8 = max_$4 + selsize - len_$5;
newc_$6 = newc_$6.substring(0, maxchars_$8)
};
that.__LzInputDiv.value = val.substring(0, selpos) + newc_$6 + val.substring(selpos + selsize);
selpos += newc_$6.length;
that.__LzInputDiv.setSelectionRange(selpos, selpos)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#436/16";
return $lzsc$temp
})(), 1)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#429/46";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__textEvent = (function () {
var $lzsc$temp = function (evt_$0) {
evt_$0 = evt_$0 || window.event;
var sprite_$1 = this.owner;
if (sprite_$1.__LZdeleted == true) return;
if (sprite_$1.__skipevent) {
sprite_$1.__skipevent = false;
return
};
var eventname_$2 = "on" + evt_$0.type;
if (LzSprite.quirks.ie_mouse_events) {
if (eventname_$2 === "onmouseleave") {
eventname_$2 = "onmouseout"
}};
var quirks_$3 = sprite_$1.quirks;
if (eventname_$2 === "onmousedown" || eventname_$2 === "onmouseup" || eventname_$2 === "onmouseout" || eventname_$2 === "onmousemove") {
if (quirks_$3.autoscroll_textarea) {
if (eventname_$2 === "onmousedown") {
sprite_$1.dragging = true
} else if (eventname_$2 === "onmouseup" || eventname_$2 === "onmouseout") {
sprite_$1.dragging = false
} else if (eventname_$2 === "onmousemove") {
if (sprite_$1.dragging) {
var d_$4 = sprite_$1.__LzInputDiv;
var y_$5 = evt_$0.pageY - d_$4.offsetTop;
if (y_$5 <= 3) {
d_$4.scrollTop -= sprite_$1.lineHeight ? sprite_$1.lineHeight : 10
};
if (y_$5 >= d_$4.clientHeight - 3) {
d_$4.scrollTop += sprite_$1.lineHeight ? sprite_$1.lineHeight : 10
}}}};
if (eventname_$2 === "onmouseout") {
sprite_$1.__mouseEvent(eventname_$2)
} else if (eventname_$2 === "onmousedown") {
sprite_$1.__mouseisdown = true
} else if (eventname_$2 === "onmouseup") {
evt_$0.cancelBubble = true;
if (!sprite_$1.__isMouseOver()) {
sprite_$1.__globalmouseup(evt_$0);
sprite_$1.deselect()
} else {
sprite_$1.__mouseEvent(eventname_$2)
}};
return
};
if (sprite_$1.__shown != true) {
if (eventname_$2 === "onfocus") {
sprite_$1.__skipevent = true;
sprite_$1.__show();
sprite_$1.__LzInputDiv.blur();
LzInputTextSprite.prototype.__lastfocus = sprite_$1;
LzKeyboardKernel.setKeyboardControl(true)
}};
var view_$6 = this.owner.owner;
if (eventname_$2 === "onfocus") {
LzMouseKernel.setGlobalClickable(false);
LzInputTextSprite.prototype.__focusedSprite = sprite_$1;
sprite_$1.__show();
if (sprite_$1._cancelfocus) {
sprite_$1._cancelfocus = false;
return
};
if (window["LzKeyboardKernel"]) LzKeyboardKernel.__cancelKeys = false
} else if (eventname_$2 === "onblur") {
if (window["LzKeyboardKernel"]) LzKeyboardKernel.__cancelKeys = true;
if (LzInputTextSprite.prototype.__focusedSprite === sprite_$1) {
LzInputTextSprite.prototype.__focusedSprite = null
};
sprite_$1.__hide();
if (sprite_$1._cancelblur) {
sprite_$1._cancelblur = false;
return
}} else if (eventname_$2 === "onkeypress") {
if (sprite_$1.restrict || sprite_$1.multiline && view_$6.maxlength && view_$6.maxlength < Infinity) {
var keycode_$7 = evt_$0.keyCode;
var charcode_$8 = quirks_$3.text_event_charcode ? evt_$0.charCode : evt_$0.keyCode;
var validChar_$9 = !(evt_$0.ctrlKey || evt_$0.altKey) && (charcode_$8 >= 32 || keycode_$7 === 13);
if (validChar_$9) {
var prevent_$a = false;
if (keycode_$7 != 13 && sprite_$1.restrict) {
prevent_$a = 0 > String.fromCharCode(charcode_$8).search(sprite_$1.restrict)
};
if (!prevent_$a) {
var selsize_$b = sprite_$1.getSelectionSize();
if (selsize_$b <= 0) {
if (quirks_$3.text_ie_carriagereturn) {
var val_$c = sprite_$1.__LzInputDiv.value.replace(sprite_$1.____crregexp, "\n")
} else {
var val_$c = sprite_$1.__LzInputDiv.value
};
var len_$d = val_$c.length, max_$e = view_$6.maxlength;
if (len_$d >= max_$e) {
prevent_$a = true
}}};
if (prevent_$a) {
evt_$0.returnValue = false;
if (evt_$0.preventDefault) {
evt_$0.preventDefault()
}}} else {
if (quirks_$3.keypress_function_keys) {
var ispaste_$f = false;
if (evt_$0.ctrlKey && !evt_$0.altKey && !evt_$0.shiftKey) {
var c_$g = String.fromCharCode(charcode_$8);
ispaste_$f = c_$g === "v" || c_$g === "V"
} else if (evt_$0.shiftKey && !evt_$0.altKey && !evt_$0.ctrlKey) {
ispaste_$f = keycode_$7 === 45
};
if (ispaste_$f) {
if (sprite_$1.restrict) {
sprite_$1.__pasteHandler()
} else {
var len_$d = sprite_$1.__LzInputDiv.value.length, max_$e = view_$6.maxlength;
if (len_$d < max_$e || sprite_$1.getSelectionSize() > 0) {
sprite_$1.__pasteHandler()
} else {
evt_$0.returnValue = false;
if (evt_$0.preventDefault) {
evt_$0.preventDefault()
}}}}}};
sprite_$1.__updatefieldsize()
};
return
};
if (view_$6) {
if (eventname_$2 === "onkeydown" || eventname_$2 === "onkeyup") {
var d_$4 = sprite_$1.__LzInputDiv;
var v_$h = d_$4.value;
if (v_$h != sprite_$1.text) {
sprite_$1.text = v_$h;
if (sprite_$1.multiline) {
if (sprite_$1.quirks["forcemeasurescrollheight"]) {
d_$4.style.height = 0;
var oldscroll_$i = d_$4.scrollTop;
d_$4.scrollTop = 0;
if (sprite_$1._h != 0) {
d_$4.style.height = sprite_$1._h
};
if (oldscroll_$i != 0) {
d_$4.scrollTop = oldscroll_$i
}}};
view_$6.inputtextevent("onchange", v_$h)
};
if (quirks_$3.autoscroll_textarea && eventname_$2 === "onkeydown" && d_$4.selectionStart === v_$h.length) {
d_$4.scrollTop = d_$4.scrollHeight - d_$4.clientHeight + 20
}} else {
view_$6.inputtextevent(eventname_$2)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#472/43";
return $lzsc$temp
})();
LzInputTextSprite.prototype.$LzTextSprite$setClickable = LzTextSprite.prototype.setClickable;
LzInputTextSprite.prototype.setClickable = (function () {
var $lzsc$temp = function (clickable_$0) {
this.__clickable = clickable_$0;
this.$LzTextSprite$setClickable(true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#705/44";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setEnabled = (function () {
var $lzsc$temp = function (val_$0) {
this.disabled = !val_$0;
this.__LzInputDiv.disabled = this.disabled
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#712/42";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setMaxLength = (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 == Infinity) {
val_$0 = ~0 >>> 1
};
var t_$1 = this.getText();
this.__LzInputDiv.maxLength = val_$0;
if (t_$1 && t_$1.length > val_$0) {
this.owner._updateSize()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#717/44";
return $lzsc$temp
})();
LzInputTextSprite.prototype.select = (function () {
var $lzsc$temp = function () {
this.__show();
try {
this.__LzInputDiv.focus()
}
catch (err_$0) {};
LzInputTextSprite.prototype.__lastfocus = this;
setTimeout(LzInputTextSprite.prototype.__selectLastFocused, 50);
if (window["LzKeyboardKernel"]) LzKeyboardKernel.__cancelKeys = false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#730/38";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__selectLastFocused = (function () {
var $lzsc$temp = function () {
if (LzInputTextSprite.prototype.__lastfocus != null) {
LzInputTextSprite.prototype.__lastfocus.__LzInputDiv.select()
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#744/51";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setSelection = (function () {
var $lzsc$temp = function (start_$0, end_$1) {
switch (arguments.length) {
case 1:
end_$1 = null
};
if (end_$1 == null) {
end_$1 = start_$0
};
this._cancelblur = true;
this.__show();
LzInputTextSprite.prototype.__lastfocus = this;
if (this.quirks["text_selection_use_range"]) {
var range_$2 = this.__LzInputDiv.createTextRange();
var val_$3 = this.__LzInputDiv.value;
if (start_$0 > end_$1) {
var st_$4 = start_$0;
start_$0 = end_$1;
end_$1 = st_$4
};
if (this.multiline) {
var offset_$5 = 0;
var startcounter_$6 = 0;
while (offset_$5 < start_$0) {
offset_$5 = val_$3.indexOf("\r\n", offset_$5 + 2);
if (offset_$5 == -1) break;
startcounter_$6++
};
var midcounter_$7 = 0;
while (offset_$5 < end_$1) {
offset_$5 = val_$3.indexOf("\r\n", offset_$5 + 2);
if (offset_$5 == -1) break;
midcounter_$7++
};
var endcounter_$8 = 0;
while (offset_$5 < val_$3.length) {
offset_$5 = val_$3.indexOf("\r\n", offset_$5 + 2);
if (offset_$5 == -1) break;
endcounter_$8++
};
var tl_$9 = range_$2.text.length;
var st_$4 = start_$0;
var ed_$a = end_$1 - val_$3.length + startcounter_$6 + midcounter_$7 + endcounter_$8 + 1
} else {
var st_$4 = start_$0;
var ed_$a = end_$1 - range_$2.text.length
};
range_$2.moveStart("character", st_$4);
range_$2.moveEnd("character", ed_$a);
range_$2.select()
} else {
this.__LzInputDiv.setSelectionRange(start_$0, end_$1)
};
this.__LzInputDiv.focus();
if (window["LzKeyboardKernel"]) LzKeyboardKernel.__cancelKeys = false
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#751/44";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getSelectionPosition = (function () {
var $lzsc$temp = function () {
if (!this.__shown || this.disabled == true) return -1;
if (this.quirks["text_selection_use_range"]) {
if (this.multiline) {
var p_$0 = this._getTextareaSelection()
} else {
var p_$0 = this._getTextSelection()
};
if (p_$0) {
return p_$0.start
} else {
return -1
}} else {
return this.__LzInputDiv.selectionStart
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#816/52";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getSelectionSize = (function () {
var $lzsc$temp = function () {
if (!this.__shown || this.disabled == true) return -1;
if (this.quirks["text_selection_use_range"]) {
if (this.multiline) {
var p_$0 = this._getTextareaSelection()
} else {
var p_$0 = this._getTextSelection()
};
if (p_$0) {
return p_$0.end - p_$0.start
} else {
return -1
}} else {
return this.__LzInputDiv.selectionEnd - this.__LzInputDiv.selectionStart
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#835/48";
return $lzsc$temp
})();
if (LzSprite.quirks["text_selection_use_range"]) {
LzInputTextSprite.prototype._getTextSelection = (function () {
var $lzsc$temp = function () {
this.__LzInputDiv.focus();
var range_$0 = document.selection.createRange();
var bookmark_$1 = range_$0.getBookmark();
var originalContents_$2 = contents = this.__LzInputDiv.value;
do {
var marker_$3 = "~~~" + Math.random() + "~~~"
} while (contents.indexOf(marker_$3) != -1);
var parent_$4 = range_$0.parentElement();
if (parent_$4 == null || !(parent_$4.type == "text" || parent_$4.type == "textarea")) {
return
};
range_$0.text = marker_$3 + range_$0.text + marker_$3;
contents = this.__LzInputDiv.value;
var result_$5 = {};
result_$5.start = contents.indexOf(marker_$3);
contents = contents.replace(marker_$3, "");
result_$5.end = contents.indexOf(marker_$3);
this.__LzInputDiv.value = originalContents_$2;
range_$0.moveToBookmark(bookmark_$1);
range_$0.select();
return result_$5
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#854/49";
return $lzsc$temp
})();
LzInputTextSprite.prototype._getTextareaSelection = (function () {
var $lzsc$temp = function () {
var textarea_$0 = this.__LzInputDiv;
var selection_range_$1 = document.selection.createRange().duplicate();
if (selection_range_$1.parentElement() == textarea_$0) {
var before_range_$2 = document.body.createTextRange();
before_range_$2.moveToElementText(textarea_$0);
before_range_$2.setEndPoint("EndToStart", selection_range_$1);
var after_range_$3 = document.body.createTextRange();
after_range_$3.moveToElementText(textarea_$0);
after_range_$3.setEndPoint("StartToEnd", selection_range_$1);
var before_finished_$4 = false, selection_finished_$5 = false, after_finished_$6 = false;
var before_text_$7, untrimmed_before_text_$8, selection_text_$9, untrimmed_selection_text_$a, after_text_$b, untrimmed_after_text_$c;
before_text_$7 = untrimmed_before_text_$8 = before_range_$2.text;
selection_text_$9 = untrimmed_selection_text_$a = selection_range_$1.text;
after_text_$b = untrimmed_after_text_$c = after_range_$3.text;
do {
if (!before_finished_$4) {
if (before_range_$2.compareEndPoints("StartToEnd", before_range_$2) == 0) {
before_finished_$4 = true
} else {
before_range_$2.moveEnd("character", -1);
if (before_range_$2.text == before_text_$7) {
untrimmed_before_text_$8 += "\r\n"
} else {
before_finished_$4 = true
}}};
if (!selection_finished_$5) {
if (selection_range_$1.compareEndPoints("StartToEnd", selection_range_$1) == 0) {
selection_finished_$5 = true
} else {
selection_range_$1.moveEnd("character", -1);
if (selection_range_$1.text == selection_text_$9) {
untrimmed_selection_text_$a += "\r\n"
} else {
selection_finished_$5 = true
}}};
if (!after_finished_$6) {
if (after_range_$3.compareEndPoints("StartToEnd", after_range_$3) == 0) {
after_finished_$6 = true
} else {
after_range_$3.moveEnd("character", -1);
if (after_range_$3.text == after_text_$b) {
untrimmed_after_text_$c += "\r\n"
} else {
after_finished_$6 = true
}}}} while (!before_finished_$4 || !selection_finished_$5 || !after_finished_$6);
var untrimmed_text_$d = untrimmed_before_text_$8 + untrimmed_selection_text_$a + untrimmed_after_text_$c;
var untrimmed_successful_$e = false;
if (textarea_$0.value == untrimmed_text_$d) {
untrimmed_successful_$e = true
};
var startPoint_$f = untrimmed_before_text_$8.length;
var endPoint_$g = startPoint_$f + untrimmed_selection_text_$a.length;
var selected_text_$h = untrimmed_selection_text_$a;
var val_$i = this.__LzInputDiv.value;
var offset_$j = 0;
var startcounter_$k = 0;
while (offset_$j < startPoint_$f) {
offset_$j = val_$i.indexOf("\r\n", offset_$j + 2);
if (offset_$j == -1) break;
startcounter_$k++
};
var midcounter_$l = 0;
while (offset_$j < endPoint_$g) {
offset_$j = val_$i.indexOf("\r\n", offset_$j + 2);
if (offset_$j == -1) break;
midcounter_$l++
};
var endcounter_$m = 0;
while (offset_$j < val_$i.length) {
offset_$j = val_$i.indexOf("\r\n", offset_$j + 2);
if (offset_$j == -1) break;
endcounter_$m++
};
startPoint_$f -= startcounter_$k;
endPoint_$g -= midcounter_$l + startcounter_$k;
return {start: startPoint_$f, end: endPoint_$g}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#884/53";
return $lzsc$temp
})()
};
LzInputTextSprite.prototype.deselect = (function () {
var $lzsc$temp = function () {
this.__hide();
if (this.__LzInputDiv && this.__LzInputDiv.blur) this.__LzInputDiv.blur();
if (window["LzKeyboardKernel"]) LzKeyboardKernel.__cancelKeys = true
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#997/40";
return $lzsc$temp
})();
LzInputTextSprite.prototype.__fontStyle = "normal";
LzInputTextSprite.prototype.__fontWeight = "normal";
LzInputTextSprite.prototype.__fontSize = "11px";
LzInputTextSprite.prototype.__fontFamily = "Verdana,Vera,sans-serif";
LzInputTextSprite.prototype.$LzTextSprite$setFontSize = LzTextSprite.prototype.setFontSize;
LzInputTextSprite.prototype.setFontSize = (function () {
var $lzsc$temp = function (fsize_$0) {
this.$LzTextSprite$setFontSize(fsize_$0);
if (this.__fontSize != this._fontSize) {
this.__fontSize = this._fontSize;
this.__LzInputDiv.style.fontSize = this._fontSize
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1012/43";
return $lzsc$temp
})();
LzInputTextSprite.prototype.$LzTextSprite$setFontStyle = LzTextSprite.prototype.setFontStyle;
LzInputTextSprite.prototype.setFontStyle = (function () {
var $lzsc$temp = function (fstyle_$0) {
this.$LzTextSprite$setFontStyle(fstyle_$0);
if (this.__fontStyle != this._fontStyle) {
this.__fontStyle = this._fontStyle;
this.__LzInputDiv.style.fontStyle = this._fontStyle
};
if (this.__fontWeight != this._fontWeight) {
this.__fontWeight = this._fontWeight;
this.__LzInputDiv.style.fontWeight = this._fontWeight
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1021/44";
return $lzsc$temp
})();
LzInputTextSprite.prototype.$LzTextSprite$setFontName = LzTextSprite.prototype.setFontName;
LzInputTextSprite.prototype.setFontName = (function () {
var $lzsc$temp = function (fname_$0) {
this.$LzTextSprite$setFontName(fname_$0);
if (this.__fontFamily != this._fontFamily) {
this.__fontFamily = this._fontFamily;
this.__LzInputDiv.style.fontFamily = this._fontFamily
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1034/43";
return $lzsc$temp
})();
LzInputTextSprite.prototype.$LzTextSprite$setWidth = LzTextSprite.prototype.setWidth;
LzInputTextSprite.prototype.__iwidthcss = 0;
LzInputTextSprite.prototype.setWidth = (function () {
var $lzsc$temp = function (w_$0) {
if (w_$0 == null || w_$0 < 0 || isNaN(w_$0)) return;
var nw_$1 = this.$LzTextSprite$setWidth(w_$0);
if (nw_$1 == null) return;
if (this.quirks.fix_clickable && nw_$1 !== null) {
nw_$1 = this.CSSDimension(nw_$1);
if (nw_$1 !== this.__iwidthcss) {
this.__iwidthcss = nw_$1;
this.__LZinputclickdiv.style.width = nw_$1
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1044/40";
return $lzsc$temp
})();
LzInputTextSprite.prototype.$LzTextSprite$setHeight = LzTextSprite.prototype.setHeight;
LzInputTextSprite.prototype.__iheightcss = 0;
LzInputTextSprite.prototype.setHeight = (function () {
var $lzsc$temp = function (h_$0) {
if (h_$0 == null || h_$0 < 0 || isNaN(h_$0)) return;
var nh_$1 = this.$LzTextSprite$setHeight(h_$0);
if (nh_$1 == null) return;
if (this.quirks.fix_clickable && nh_$1 !== null) {
nh_$1 = this.CSSDimension(nh_$1);
if (nh_$1 !== this.__iheightcss) {
this.__iheightcss = nh_$1;
this.__LZinputclickdiv.style.height = nh_$1
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1063/41";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setColor = (function () {
var $lzsc$temp = function (c_$0) {
if (this.color == c_$0) return;
this.color = c_$0;
this.__LzInputDiv.style.color = LzColorUtils.inttohex(c_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1081/40";
return $lzsc$temp
})();
LzInputTextSprite.prototype.getText = (function () {
var $lzsc$temp = function () {
if (this.multiline && this.quirks.text_ie_carriagereturn) {
return this.__LzInputDiv.value.replace(this.____crregexp, "\n")
} else {
return this.__LzInputDiv.value
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1087/39";
return $lzsc$temp
})();
LzInputTextSprite.findSelection = (function () {
var $lzsc$temp = function () {
if (LzInputTextSprite.__focusedSprite && LzInputTextSprite.__focusedSprite.owner) {
return LzInputTextSprite.__focusedSprite.owner
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1098/35";
return $lzsc$temp
})();
LzInputTextSprite.prototype.setTextColor = (function () {
var $lzsc$temp = function (c_$0) {
if (this.textcolor === c_$0) return;
this.textcolor = c_$0;
this.scrolldiv.style.color = LzColorUtils.inttohex(c_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzInputTextSprite.js#1105/44";
return $lzsc$temp
})();
var LzXMLParser = {parseXML: (function () {
var $lzsc$temp = function (str_$0, trimwhitespace_$1, nsprefix_$2) {
try {
var parser_$3 = new DOMParser();
var doc_$4 = parser_$3.parseFromString(str_$0, "text/xml");
var err_$5 = this.getParserError(doc_$4);
if (err_$5) {
throw new Error(err_$5)
} else {
return doc_$4.firstChild
}}
catch ($lzsc$e) {
if (Error["$lzsc$isa"] ? Error.$lzsc$isa($lzsc$e) : $lzsc$e instanceof Error) {
lz.$lzsc$thrownError = $lzsc$e
};
throw $lzsc$e
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#15/15";
return $lzsc$temp
})(), getParserError: (function () {
var $lzsc$temp = function (doc_$0) {
var browser_$1 = lz.embed.browser;
if (browser_$1.isIE) {
return this.__checkIE(doc_$0)
} else if (browser_$1.isFirefox || browser_$1.isOpera) {
return this.__checkFirefox(doc_$0)
} else if (browser_$1.isSafari) {
return this.__checkSafari(doc_$0)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#31/21";
return $lzsc$temp
})(), __checkIE: (function () {
var $lzsc$temp = function (doc_$0) {
var perr_$1 = doc_$0.parseError;
if (perr_$1.errorCode != 0) {
return perr_$1.reason
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#42/16";
return $lzsc$temp
})(), __checkFirefox: (function () {
var $lzsc$temp = function (doc_$0) {
var c_$1 = doc_$0.documentElement;
if (c_$1 && c_$1.nodeName == "parsererror") {
var msg_$2 = c_$1.firstChild.nodeValue;
return msg_$2.match(".*")[0]
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#48/21";
return $lzsc$temp
})(), __checkSafari: (function () {
var $lzsc$temp = function (doc_$0) {
var c_$1 = doc_$0.documentElement;
if (c_$1 instanceof HTMLElement) {
(c_$1 = c_$1.firstChild) && (c_$1 = c_$1.firstChild)
} else {
c_$1 = c_$1.firstChild
};
if (c_$1 && c_$1.nodeName == "parsererror") {
var msg_$2 = c_$1.childNodes[1].textContent;
return msg_$2.match("[^:]*: (.*)")[1]
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#57/20";
return $lzsc$temp
})()};
if (typeof DOMParser == "undefined") {
var DOMParser = (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#84/21";
return $lzsc$temp
})();
DOMParser.prototype.parseFromString = (function () {
var $lzsc$temp = function (str_$0, contentType_$1) {
if (typeof window.ActiveXObject != "undefined") {
var progIDs_$2 = ["Msxml2.DOMDocument.6.0", "Msxml2.DOMDocument.3.0", "MSXML.DomDocument"];
var xmlDOM_$3 = null;
for (var i_$4 = 0;i_$4 < progIDs_$2.length;i_$4++) {
try {
xmlDOM_$3 = new ActiveXObject(progIDs_$2[i_$4]);
break
}
catch (ex_$5) {}};
if (xmlDOM_$3 == null) {
Debug.error("Could not instantiate a XML DOM ActiveXObject")
};
xmlDOM_$3.loadXML(str_$0);
return xmlDOM_$3
} else if (typeof XMLHttpRequest != "undefined") {
contentType_$1 = contentType_$1 || "application/xml";
var req_$6 = new XMLHttpRequest();
req_$6.open("GET", "data:" + contentType_$1 + ";charset=utf-8," + encodeURIComponent(str_$0), false);
if (req_$6.overrideMimeType) {
req_$6.overrideMimeType(contentType_$1)
};
req_$6.send(null);
return req_$6.responseXML
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLParser.js#85/43";
return $lzsc$temp
})()
};
var LzXMLTranslator = {whitespacePat: new RegExp("^\\s*$"), stringTrimPat: new RegExp("^\\s+|\\s+$", "g"), copyXML: (function () {
var $lzsc$temp = function (xmldoc_$0, trimwhitespace_$1, nsprefix_$2) {
var lfcnode_$3 = this.copyBrowserXML(xmldoc_$0, true, trimwhitespace_$1, nsprefix_$2);
if (lfcnode_$3 instanceof LzDataElement) {
return lfcnode_$3
} else {
return null
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#22/10";
return $lzsc$temp
})(), copyBrowserNode: (function () {
var $lzsc$temp = function (node_$0, ignorewhite_$1, trimwhite_$2, nsprefix_$3) {
var type_$4 = node_$0.nodeType;
if (type_$4 == 3 || type_$4 == 4) {
var nv_$5 = node_$0.nodeValue;
if (!(ignorewhite_$1 && this.whitespacePat.test(nv_$5))) {
if (trimwhite_$2) {
nv_$5 = nv_$5.replace(this.stringTrimPat, "")
};
return new LzDataText(nv_$5)
}} else if (type_$4 == 1 || type_$4 == 9) {
var nname_$6 = !nsprefix_$3 && (node_$0.localName || node_$0.baseName) || node_$0.nodeName;
var cattrs_$7 = {};
var nattrs_$8 = node_$0.attributes;
if (nattrs_$8) {
for (var k_$9 = 0, len_$a = nattrs_$8.length;k_$9 < len_$a;k_$9++) {
var attrnode_$b = nattrs_$8[k_$9];
if (attrnode_$b) {
var attrname_$c = !nsprefix_$3 && (attrnode_$b.localName || attrnode_$b.baseName) || attrnode_$b.name;
cattrs_$7[attrname_$c] = attrnode_$b.value
}}};
var lfcnode_$d = new LzDataElement(nname_$6);
lfcnode_$d.attributes = cattrs_$7;
return lfcnode_$d
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#35/18";
return $lzsc$temp
})(), copyBrowserXML: (function () {
var $lzsc$temp = function (xmlnode_$0, ignorewhite_$1, trimwhite_$2, nsprefix_$3) {
var document_$4 = new LzDataElement(null);
if (!xmlnode_$0.firstChild) {
return document_$4.appendChild(this.copyBrowserNode(xmlnode_$0, ignorewhite_$1, trimwhite_$2, nsprefix_$3))
};
var wsPat_$5 = this.whitespacePat;
var trimPat_$6 = this.stringTrimPat;
var lfcparent_$7 = document_$4;
var next_$8, node_$9 = xmlnode_$0;
for (;;) {
var type_$a = node_$9.nodeType;
if (type_$a == 3 || type_$a == 4) {
var nv_$b = node_$9.nodeValue;
if (!(ignorewhite_$1 && wsPat_$5.test(nv_$b))) {
if (trimwhite_$2) {
nv_$b = nv_$b.replace(trimPat_$6, "")
};
var cnodes_$c = lfcparent_$7.childNodes;
var last_$d = cnodes_$c[cnodes_$c.length - 1];
if (last_$d instanceof LzDataText) {
last_$d.data += nv_$b
} else {
var lfcnode_$e = new LzDataText(nv_$b);
lfcnode_$e.parentNode = lfcparent_$7;
lfcnode_$e.ownerDocument = document_$4;
lfcnode_$e.__LZo = cnodes_$c.push(lfcnode_$e) - 1
}}} else if (type_$a == 1 || type_$a == 9) {
var nname_$f = !nsprefix_$3 && (node_$9.localName || node_$9.baseName) || node_$9.nodeName;
var cattrs_$g = {};
var nattrs_$h = node_$9.attributes;
if (nattrs_$h) {
for (var k_$i = 0, len_$j = nattrs_$h.length;k_$i < len_$j;k_$i++) {
var attrnode_$k = nattrs_$h[k_$i];
if (attrnode_$k) {
var attrname_$l = !nsprefix_$3 && (attrnode_$k.localName || attrnode_$k.baseName) || attrnode_$k.name;
cattrs_$g[attrname_$l] = attrnode_$k.value
}}};
var lfcnode_$e = new LzDataElement(nname_$f);
lfcnode_$e.attributes = cattrs_$g;
lfcnode_$e.parentNode = lfcparent_$7;
lfcnode_$e.ownerDocument = document_$4;
lfcnode_$e.__LZo = lfcparent_$7.childNodes.push(lfcnode_$e) - 1;
if (next_$8 = node_$9.firstChild) {
lfcparent_$7 = lfcnode_$e;
node_$9 = next_$8;
continue
}};
while (!(next_$8 = node_$9.nextSibling)) {
node_$9 = node_$9.parentNode;
lfcparent_$7 = lfcparent_$7.parentNode;
if (node_$9 === xmlnode_$0) {
return document_$4.childNodes[0]
}};
node_$9 = next_$8
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzXMLTranslator.js#78/17";
return $lzsc$temp
})()};
var LzHTTPLoader = (function () {
var $lzsc$temp = function (owner_$0, proxied_$1) {
this.owner = owner_$0;
this.options = {parsexml: true, serverproxyargs: null};
this.requestheaders = {};
this.requestmethod = LzHTTPLoader.GET_METHOD
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#12/20";
return $lzsc$temp
})();
LzHTTPLoader.GET_METHOD = "GET";
LzHTTPLoader.POST_METHOD = "POST";
LzHTTPLoader.PUT_METHOD = "PUT";
LzHTTPLoader.DELETE_METHOD = "DELETE";
LzHTTPLoader.prototype.__timeoutID = 0;
LzHTTPLoader.prototype.loadSuccess = (function () {
var $lzsc$temp = function (loader_$0, data_$1) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#30/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadError = (function () {
var $lzsc$temp = function (loader_$0, data_$1) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#31/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadTimeout = (function () {
var $lzsc$temp = function (loader_$0, data_$1) {};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#32/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadContent = (function () {
var $lzsc$temp = function (self_$0, content_$1) {
if (this.options["parsexml"]) {
this.translateXML()
} else {
this.loadSuccess(this, content_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#34/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.translateXML = (function () {
var $lzsc$temp = function () {
var xml_$0 = this.responseXML;
if (xml_$0 == null || xml_$0.childNodes.length == 0 || lz.embed.browser.isFirefox && LzXMLParser.getParserError(xml_$0) != null) {
this.loadError(this, null)
} else {
var elt_$1;
var nodes_$2 = xml_$0.childNodes;
for (var i_$3 = 0;i_$3 < nodes_$2.length;i_$3++) {
var child_$4 = nodes_$2.item(i_$3);
if (child_$4.nodeType == 1) {
elt_$1 = child_$4;
break
}};
if (elt_$1 != null) {
var lzxdata_$5 = LzXMLTranslator.copyXML(elt_$1, this.options.trimwhitespace, this.options.nsprefix);
this.loadSuccess(this, lzxdata_$5)
} else {
this.loadError(this, null)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#43/39";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponse = (function () {
var $lzsc$temp = function () {
return this.responseText
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#76/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseStatus = (function () {
var $lzsc$temp = function () {
return this.responseStatus
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#81/44";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseHeaders = (function () {
var $lzsc$temp = function () {
return this.responseHeaders
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#86/45";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getResponseHeader = (function () {
var $lzsc$temp = function (key_$0) {
return this.responseHeaders[key_$0]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#90/44";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setRequestHeaders = (function () {
var $lzsc$temp = function (obj_$0) {
this.requestheaders = obj_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#97/44";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setRequestHeader = (function () {
var $lzsc$temp = function (key_$0, val_$1) {
this.requestheaders[key_$0] = val_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#105/43";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setOption = (function () {
var $lzsc$temp = function (key_$0, val_$1) {
this.options[key_$0] = val_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#110/36";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getOption = (function () {
var $lzsc$temp = function (key_$0) {
return this.options[key_$0]
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#115/36";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setProxied = (function () {
var $lzsc$temp = function (proxied_$0) {
this.setOption("proxied", proxied_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#120/37";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueryParams = (function () {
var $lzsc$temp = function (qparams_$0) {
this.queryparams = qparams_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#125/41";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueryString = (function () {
var $lzsc$temp = function (qstring_$0) {
this.querystring = qstring_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#130/41";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setQueueing = (function () {
var $lzsc$temp = function (queuing_$0) {
this.setOption("queuing", queuing_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#138/38";
return $lzsc$temp
})();
LzHTTPLoader.prototype.abort = (function () {
var $lzsc$temp = function () {
if (this.req) {
this.__abort = true;
this.req.abort();
this.req = null;
this.removeTimeout(this)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#143/32";
return $lzsc$temp
})();
LzHTTPLoader.prototype.open = (function () {
var $lzsc$temp = function (method_$0, url_$1, username_$2, password_$3) {
if (this.req) {
Debug.warn("pending request for %w", this);
this.abort()
};
this.req = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
this.responseStatus = 0;
this.responseHeaders = null;
this.responseText = null;
this.responseXML = null;
this.__abort = false;
this.__timeout = false;
this.__timeoutID = 0;
this.requesturl = url_$1;
this.requestmethod = method_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#152/31";
return $lzsc$temp
})();
LzHTTPLoader.prototype.send = (function () {
var $lzsc$temp = function (content_$0) {
this.loadXMLDoc(this.requestmethod, this.requesturl, this.requestheaders, content_$0, true)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#173/31";
return $lzsc$temp
})();
LzHTTPLoader.prototype.makeProxiedURL = (function () {
var $lzsc$temp = function (proxyurl_$0, url_$1, httpmethod_$2, lzt_$3, headers_$4, postbody_$5) {
var params_$6 = {serverproxyargs: this.options.serverproxyargs, sendheaders: this.options.sendheaders, trimwhitespace: this.options.trimwhitespace, nsprefix: this.options.nsprefix, timeout: this.timeout, cache: this.options.cacheable, ccache: this.options.ccache, proxyurl: proxyurl_$0, url: url_$1, secure: this.secure, postbody: postbody_$5, headers: headers_$4, httpmethod: httpmethod_$2, service: lzt_$3};
return lz.Browser.makeProxiedURL(params_$6)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#185/41";
return $lzsc$temp
})();
LzHTTPLoader.prototype.timeout = Infinity;
LzHTTPLoader.prototype.setTimeout = (function () {
var $lzsc$temp = function (timeout_$0) {
this.timeout = timeout_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#208/37";
return $lzsc$temp
})();
LzHTTPLoader.prototype.setupTimeout = (function () {
var $lzsc$temp = function (loader_$0, duration_$1) {
loader_$0.__timeoutID = setTimeout(LzHTTPLoader.__LZhandleXMLHTTPTimeout, duration_$1, loader_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#214/39";
return $lzsc$temp
})();
LzHTTPLoader.prototype.removeTimeout = (function () {
var $lzsc$temp = function (loader_$0) {
var tid_$1 = loader_$0.__timeoutID;
if (tid_$1 != 0) {
clearTimeout(tid_$1)
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#219/40";
return $lzsc$temp
})();
LzHTTPLoader.__LZhandleXMLHTTPTimeout = (function () {
var $lzsc$temp = function (loader_$0) {
loader_$0.__timeout = true;
if (loader_$0.req) {
loader_$0.req.abort();
loader_$0.req = null
};
loader_$0.loadTimeout(loader_$0, null)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#227/41";
return $lzsc$temp
})();
LzHTTPLoader.prototype.getElapsedTime = (function () {
var $lzsc$temp = function () {
return new Date().getTime() - this.gstart
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#236/41";
return $lzsc$temp
})();
LzHTTPLoader.prototype.__setRequestHeaders = (function () {
var $lzsc$temp = function (xhr_$0, headers_$1) {
if (headers_$1 != null) {
for (var key_$2 in headers_$1) {
var val_$3 = headers_$1[key_$2];
xhr_$0.setRequestHeader(key_$2, val_$3)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#241/46";
return $lzsc$temp
})();
LzHTTPLoader.prototype.__getAllResponseHeaders = (function () {
var $lzsc$temp = function (xhr_$0) {
var re_$1 = new RegExp("^([-\\w]+):\\s*(\\S(?:.*\\S)?)\\s*$", "mg");
var respheader_$2 = xhr_$0.getAllResponseHeaders();
var allheaders_$3 = {};
var header_$4;
while ((header_$4 = re_$1.exec(respheader_$2)) != null) {
allheaders_$3[header_$4[1]] = header_$4[2]
};
return allheaders_$3
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#251/50";
return $lzsc$temp
})();
LzHTTPLoader.prototype.loadXMLDoc = (function () {
var $lzsc$temp = function (method_$0, url_$1, headers_$2, postbody_$3, ignorewhite_$4) {
if (this.req) {
var self = this;
this.req.onreadystatechange = (function () {
var $lzsc$temp = function () {
var xhr_$0 = self.req;
if (xhr_$0 == null) {
return
};
if (xhr_$0.readyState == 4) {
if (self.__timeout) {

} else if (self.__abort) {

} else {
self.removeTimeout(self);
self.req = null;
var status_$1 = -1;
try {
status_$1 = xhr_$0.status
}
catch (e_$2) {};
self.responseStatus = status_$1;
if (status_$1 == 200 || status_$1 == 304) {
self.responseXML = xhr_$0.responseXML;
self.responseText = xhr_$0.responseText;
self.responseHeaders = self.__getAllResponseHeaders(xhr_$0);
self.loadContent(self, self.responseText)
} else {
self.loadError(self, null)
}}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#268/39";
return $lzsc$temp
})();
try {
this.req.open(method_$0, url_$1, true)
}
catch (e_$5) {
this.req = null;
this.loadError(this, null);
return
};
if (method_$0 == "POST" && headers_$2["content-type"] == null) {
headers_$2["content-type"] = "application/x-www-form-urlencoded"
};
this.__setRequestHeaders(this.req, headers_$2);
this.gstart = new Date().getTime();
try {
this.req.send(postbody_$3)
}
catch (e_$5) {
this.req = null;
this.loadError(this, null);
return
};
if (isFinite(this.timeout)) {
this.setupTimeout(this, this.timeout)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#264/37";
return $lzsc$temp
})();
LzHTTPLoader.prototype.destroy = (function () {
var $lzsc$temp = function () {
return
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzHTTPLoader.js#341/34";
return $lzsc$temp
})();
var LzScreenKernel = {width: null, height: null, __resizeEvent: (function () {
var $lzsc$temp = function () {
var rootcontainerdiv_$0 = LzSprite.__rootSpriteContainer;
LzScreenKernel.width = rootcontainerdiv_$0.offsetWidth;
LzScreenKernel.height = rootcontainerdiv_$0.offsetHeight;
if (LzScreenKernel.__callback) LzScreenKernel.__scope[LzScreenKernel.__callback]({width: LzScreenKernel.width, height: LzScreenKernel.height})
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#16/21";
return $lzsc$temp
})(), __init: (function () {
var $lzsc$temp = function () {
lz.embed.attachEventHandler(window.top, "resize", LzScreenKernel, "__resizeEvent")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#59/14";
return $lzsc$temp
})(), __callback: null, __scope: null, setCallback: (function () {
var $lzsc$temp = function (scope_$0, funcname_$1) {
this.__scope = scope_$0;
this.__callback = funcname_$1;
this.__init();
this.__resizeEvent()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzScreenKernel.js#64/19";
return $lzsc$temp
})()};
Class.make("LzContextMenuKernel", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (newowner_$0) {
this.owner = newowner_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "owner", null, "showbuiltins", false, "_delegate", null, "setDelegate", (function () {
var $lzsc$temp = function (delegate_$0) {
this._delegate = delegate_$0
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "addItem", (function () {
var $lzsc$temp = function (item_$0) {};
$lzsc$temp["displayName"] = "addItem";
return $lzsc$temp
})(), "hideBuiltInItems", (function () {
var $lzsc$temp = function () {
this.showbuiltins = false
};
$lzsc$temp["displayName"] = "hideBuiltInItems";
return $lzsc$temp
})(), "showBuiltInItems", (function () {
var $lzsc$temp = function () {
this.showbuiltins = true
};
$lzsc$temp["displayName"] = "showBuiltInItems";
return $lzsc$temp
})(), "clearItems", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "clearItems";
return $lzsc$temp
})(), "__show", (function () {
var $lzsc$temp = function () {
if (LzMouseKernel.__showncontextmenu === this) {
return
} else {
LzMouseKernel.__showncontextmenu = this
};
var owner_$0 = this.owner;
var del_$1 = this._delegate;
if (del_$1 != null) del_$1.execute(owner_$0);
if (owner_$0.onmenuopen.ready) owner_$0.onmenuopen.sendEvent(owner_$0);
var classlist_$2 = [];
var items_$3 = owner_$0.getItems();
var _items_$4 = {};
for (var i_$5 = 0;i_$5 < items_$3.length;i_$5++) {
var v_$6 = items_$3[i_$5].kernel.cmenuitem;
var caption_$7 = v_$6.caption;
if (v_$6.visible != true || (caption_$7 in _items_$4)) {
continue
};
_items_$4[caption_$7] = true;
if (v_$6.separatorBefore) {
classlist_$2.push({type: "separator"})
};
if (v_$6.enabled) {
classlist_$2.push({type: "text", label: caption_$7, offset: i_$5})
} else {
classlist_$2.push({type: "disabled", label: caption_$7, offset: i_$5})
}};
var s_$8 = LzContextMenuKernel.lzcontextmenu || LzContextMenuKernel.__create();
s_$8.setItems(classlist_$2);
s_$8.show()
};
$lzsc$temp["displayName"] = "__show";
return $lzsc$temp
})(), "__hide", (function () {
var $lzsc$temp = function () {
LzMouseKernel.__showncontextmenu = null
};
$lzsc$temp["displayName"] = "__hide";
return $lzsc$temp
})(), "__select", (function () {
var $lzsc$temp = function (i_$0) {
var items_$1 = this.owner.getItems();
if (items_$1 && items_$1[i_$0]) items_$1[i_$0].kernel.__select()
};
$lzsc$temp["displayName"] = "__select";
return $lzsc$temp
})()], null, ["lzcontextmenu", null, "__create", (function () {
var $lzsc$temp = function () {
var s_$0 = LzContextMenuKernel.lzcontextmenu;
if (!s_$0) {
LzContextMenuKernel.lzcontextmenu = s_$0 = new (lz.lzcontextmenu)(canvas)
};
return s_$0
};
$lzsc$temp["displayName"] = "__create";
return $lzsc$temp
})()]);
Class.make("LzContextMenuItemKernel", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (newowner_$0, title_$1, del_$2) {
this.owner = newowner_$0;
this.cmenuitem = {visible: true, enabled: true, separatorBefore: false, caption: title_$1};
this.setDelegate(del_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "owner", null, "cmenuitem", null, "_delegate", null, "setDelegate", (function () {
var $lzsc$temp = function (delegate_$0) {
this._delegate = delegate_$0
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "setCaption", (function () {
var $lzsc$temp = function (caption_$0) {
this.cmenuitem.caption = caption_$0
};
$lzsc$temp["displayName"] = "setCaption";
return $lzsc$temp
})(), "getCaption", (function () {
var $lzsc$temp = function () {
return this.cmenuitem.caption
};
$lzsc$temp["displayName"] = "getCaption";
return $lzsc$temp
})(), "setEnabled", (function () {
var $lzsc$temp = function (val_$0) {
this.cmenuitem.enabled = val_$0
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setSeparatorBefore", (function () {
var $lzsc$temp = function (val_$0) {
this.cmenuitem.separatorBefore = val_$0
};
$lzsc$temp["displayName"] = "setSeparatorBefore";
return $lzsc$temp
})(), "setVisible", (function () {
var $lzsc$temp = function (val_$0) {
this.cmenuitem.visible = val_$0
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "__select", (function () {
var $lzsc$temp = function () {
var owner_$0 = this.owner;
var delegate_$1 = this._delegate;
if (delegate_$1 != null) {
if (delegate_$1 instanceof LzDelegate) {
delegate_$1.execute(owner_$0)
} else if (typeof delegate_$1 === "function") {
delegate_$1()
} else {
Debug.error("LzContextMenuItem.setDelegate must be passed a delegate", owner_$0, delegate_$1)
}};
if (owner_$0.onselect.ready) owner_$0.onselect.sendEvent(owner_$0)
};
$lzsc$temp["displayName"] = "__select";
return $lzsc$temp
})()]);
if (LzSprite.quirks.ie_timer_closure) {
(function () {
var $lzsc$temp = function (f_$0) {
window.setTimeout = f_$0(window.setTimeout);
window.setInterval = f_$0(window.setInterval)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#18/4";
return $lzsc$temp
})()((function () {
var $lzsc$temp = function (f) {
return (function () {
var $lzsc$temp = function (c, t_$0) {
var a = Array.prototype.slice.call(arguments, 2);
if (typeof c != "function") c = new Function(c);
return( f((function () {
var $lzsc$temp = function () {
c.apply(this, a)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#26/18";
return $lzsc$temp
})(), t_$0))
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#22/12";
return $lzsc$temp
})()
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#21/6";
return $lzsc$temp
})())
};
var LzTimeKernel = {setTimeout: (function () {
var $lzsc$temp = function () {
return window.setTimeout.apply(window, arguments)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#34/17";
return $lzsc$temp
})(), setInterval: (function () {
var $lzsc$temp = function () {
return window.setInterval.apply(window, arguments)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#37/19";
return $lzsc$temp
})(), clearTimeout: (function () {
var $lzsc$temp = function (id_$0) {
return window.clearTimeout(id_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#40/20";
return $lzsc$temp
})(), clearInterval: (function () {
var $lzsc$temp = function (id_$0) {
return window.clearInterval(id_$0)
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#43/21";
return $lzsc$temp
})(), startTime: new Date().valueOf(), getTimer: (function () {
var $lzsc$temp = function () {
return new Date().valueOf() - LzTimeKernel.startTime
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzTimeKernel.js#49/16";
return $lzsc$temp
})()};
var LzFontManager = new Object();
LzFontManager.fonts = {};
LzFontManager.addFont = (function () {
var $lzsc$temp = function (fontname_$0, fontstyle_$1, fontweight_$2, path_$3, ptype_$4) {
var fontobj_$5 = {name: fontname_$0, style: fontstyle_$1, weight: fontweight_$2, url: path_$3, ptype: ptype_$4};
this.fonts[fontname_$0 + "_" + fontstyle_$1 + "_" + fontweight_$2] = fontobj_$5
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#35/25";
return $lzsc$temp
})();
LzFontManager.generateCSS = (function () {
var $lzsc$temp = function () {
var fonts_$0 = this.fonts;
var output_$1 = "";
for (var i_$2 in fonts_$0) {
var font_$3 = fonts_$0[i_$2];
var url_$4 = this.getURL(font_$3);
var i_$2 = url_$4.lastIndexOf(".ttf");
var ieurl_$5 = url_$4.substring(0, i_$2) + ".eot";
output_$1 += "@font-face{font-family:" + font_$3.name + ";src:url(" + ieurl_$5 + ');src:local("' + font_$3.name + '"), url(' + url_$4 + ') format("truetype");font-weight:' + font_$3.weight + ";font-style:" + font_$3.style + ";}"
};
return output_$1
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#42/29";
return $lzsc$temp
})();
LzFontManager.getURL = (function () {
var $lzsc$temp = function (font_$0) {
return LzSprite.prototype.getBaseUrl(font_$0) + font_$0.url
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#55/24";
return $lzsc$temp
})();
LzFontManager.__fontloadstate = {counter: 0};
LzFontManager.__fontloadcallbacks = {};
LzFontManager.isFontLoaded = (function () {
var $lzsc$temp = function (sprite_$0, fontname_$1, fontstyle_$2, fontweight_$3) {
var font_$4 = this.fonts[fontname_$1 + "_" + fontstyle_$2 + "_" + fontweight_$3];
if (!font_$4) return true;
var url_$5 = this.getURL(font_$4);
var fontloadstate_$6 = this.__fontloadstate[url_$5];
if (fontloadstate_$6) {
var loadingstatus_$7 = fontloadstate_$6.state;
if (loadingstatus_$7 >= 2) {
return true
}} else {
var style_$8 = "font-family:" + fontname_$1 + ";font-style:" + fontstyle_$2 + ";font-weight:" + fontweight_$3 + ";width:auto;height:auto;";
var mdiv_$9 = this.__createMeasureDiv("lzswftext", style_$8);
this.__setTextContent(mdiv_$9, "div", 'Yq_gy"9;ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789-=abcdefghijklmnopqrstuvwxyz');
mdiv_$9.style.display = "inline";
var width_$a = mdiv_$9.clientWidth;
var height_$b = mdiv_$9.clientHeight;
mdiv_$9.style.display = "none";
var fontloadstate_$6 = {state: 1, timer: new Date().valueOf()};
this.__fontloadstate[url_$5] = fontloadstate_$6;
this.__fontloadstate.counter++;
var cstr_$c = lz.BrowserUtils.getcallbackfunc(LzFontManager, "__measurefontdiv", [mdiv_$9, width_$a, height_$b, url_$5]);
fontloadstate_$6.TID = setInterval(cstr_$c, Math.random() * 20 + 30)
};
this.__fontloadcallbacks[sprite_$0.uid] = sprite_$0
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#64/30";
return $lzsc$temp
})();
LzFontManager.fontloadtimeout = 15000;
LzFontManager.__measurefontdiv = (function () {
var $lzsc$temp = function (mdiv_$0, width_$1, height_$2, url_$3) {
mdiv_$0.style.display = "inline";
var newwidth_$4 = mdiv_$0.clientWidth;
var newheight_$5 = mdiv_$0.clientHeight;
mdiv_$0.style.display = "none";
var fontloadstate_$6 = this.__fontloadstate[url_$3];
if (newwidth_$4 == width_$1 && newheight_$5 == height_$2) {
var timediff_$7 = new Date().valueOf() - fontloadstate_$6.timer;
if (timediff_$7 < this.fontloadtimeout) {
return
};
fontloadstate_$6.state = 3;
Debug.warn("Timeout loading font %w: the font size didn't change.", url_$3)
} else {
fontloadstate_$6.state = 2
};
clearInterval(fontloadstate_$6.TID);
this.__fontloadstate.counter--;
if (this.__fontloadstate.counter != 0) return;
this.__clearMeasureCache();
var callbacks_$8 = this.__fontloadcallbacks;
for (var i_$9 in callbacks_$8) {
var sprite_$a = callbacks_$8[i_$9];
if (sprite_$a) {
sprite_$a.__fontLoaded()
}};
delete this.__fontloadcallbacks
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#107/34";
return $lzsc$temp
})();
LzFontManager.__sizecache = {counter: 0};
LzFontManager.__rootdiv = null;
LzFontManager.__clearMeasureCache = (function () {
var $lzsc$temp = function () {
this.__sizecache = {counter: 0};
if (LzSprite.quirks.ie_leak_prevention) {
LzTextSprite.prototype.__cleanupdivs()
};
if (this.__rootdiv) {
this.__rootdiv.innerHTML = ""
}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#154/37";
return $lzsc$temp
})();
LzFontManager.__createContainerDiv = (function () {
var $lzsc$temp = function () {
var textsizecache_$0 = document.createElement("div");
lz.embed.__setAttr(textsizecache_$0, "id", "lzTextSizeCache");
document.body.appendChild(textsizecache_$0);
this.__rootdiv = document.getElementById("lzTextSizeCache")
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#163/38";
return $lzsc$temp
})();
LzFontManager.getSize = (function () {
var $lzsc$temp = function (dimension_$0, className_$1, style_$2, tagname_$3, string_$4) {
var cacheFullKey_$5 = className_$1 + "/" + style_$2 + "{" + string_$4 + "}";
var __sizecache_$6 = this.__sizecache;
var cv_$7 = __sizecache_$6[cacheFullKey_$5];
if (cv_$7 && (dimension_$0 in cv_$7)) {
return cv_$7
};
if (__sizecache_$6.counter > 0 && __sizecache_$6.counter % this.__sizecacheupperbound == 0) {
this.__clearMeasureCache();
cv_$7 = null
};
if (!cv_$7) {
cv_$7 = __sizecache_$6[cacheFullKey_$5] = {}};
var divCacheKey_$8 = className_$1 + "/" + style_$2 + "/" + tagname_$3;
var mdiv_$9 = __sizecache_$6[divCacheKey_$8];
if (!mdiv_$9) {
var mdiv_$9 = this.__createMeasureDiv(className_$1, style_$2);
__sizecache_$6[divCacheKey_$8] = mdiv_$9
};
this.__setTextContent(mdiv_$9, tagname_$3, string_$4);
mdiv_$9.style.display = "inline";
cv_$7[dimension_$0] = dimension_$0 == "width" ? mdiv_$9.clientWidth : mdiv_$9.clientHeight;
mdiv_$9.style.display = "none";
return cv_$7
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#171/25";
return $lzsc$temp
})();
LzFontManager.__createMeasureDiv = (function () {
var $lzsc$temp = function (className_$0, style_$1) {
var tagname_$2 = "div";
var __sizecache_$3 = this.__sizecache;
if (LzSprite.prototype.quirks["text_measurement_use_insertadjacenthtml"]) {
var html_$4 = "<" + tagname_$2 + ' id="testSpan' + __sizecache_$3.counter + '"';
html_$4 += ' class="' + className_$0 + '"';
html_$4 += ' style="' + style_$1 + '">';
html_$4 += "</" + tagname_$2 + ">";
this.__rootdiv.insertAdjacentHTML("beforeEnd", html_$4);
var mdiv_$5 = document.all["testSpan" + __sizecache_$3.counter];
if (LzSprite.prototype.quirks.ie_leak_prevention) {
LzTextSprite.prototype.__divstocleanup.push(mdiv_$5)
}} else {
var mdiv_$5 = document.createElement(tagname_$2);
lz.embed.__setAttr(mdiv_$5, "class", className_$0);
lz.embed.__setAttr(mdiv_$5, "style", style_$1);
this.__rootdiv.appendChild(mdiv_$5)
};
__sizecache_$3.counter++;
return mdiv_$5
};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#207/36";
return $lzsc$temp
})();
LzFontManager.__setTextContent = (function () {
var $lzsc$temp = function (mdiv_$0, tagname_$1, string_$2) {
switch (tagname_$1) {
case "div":
mdiv_$0.innerHTML = string_$2;break;;case "input":
case "textarea":
if (LzSprite.prototype.quirks["text_content_use_inner_text"]) {
mdiv_$0.innerText = string_$2
} else {
mdiv_$0.textContent = string_$2
}break;;default:
{
Debug.error("Unknown tagname: %w", tagname_$1)
}}};
$lzsc$temp["displayName"] = "kernel/dhtml/LzFontManager.js#232/34";
return $lzsc$temp
})();
Class.make("LzView", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "__LZlayout", void 0, "__LZstoredbounds", void 0, "__movecounter", 0, "__mousecache", null, "playing", false, "_visible", void 0, "$lzc$set_visible", (function () {
var $lzsc$temp = function (amVisible_$0) {
if (this._visible == amVisible_$0) return;
this._visible = amVisible_$0;
if (amVisible_$0) {
var v_$1 = "visible"
} else if (amVisible_$0 == null) {
Debug.info("%w.%s(%w) is deprecated.  Perhaps you meant %w.%s(%s)?  If not, use %w.%s('collapse').", this, arguments.callee, amVisible_$0, this, arguments.callee, false, this, this.setVisibility);
var v_$1 = "collapse"
} else {
var v_$1 = "hidden"
};
this.visibility = v_$1;
if (this.onvisibility.ready) this.onvisibility.sendEvent(this.visibility);
this.__LZupdateShown()
};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "onaddsubview", LzDeclaredEvent, "onblur", LzDeclaredEvent, "onclick", LzDeclaredEvent, "onclickable", LzDeclaredEvent, "onfocus", LzDeclaredEvent, "onframe", LzDeclaredEvent, "onheight", LzDeclaredEvent, "onkeyup", LzDeclaredEvent, "onkeydown", LzDeclaredEvent, "onlastframe", LzDeclaredEvent, "onload", LzDeclaredEvent, "onframesloadratio", LzDeclaredEvent, "onloadratio", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "onmousedown", LzDeclaredEvent, "onmouseout", LzDeclaredEvent, "onmouseover", LzDeclaredEvent, "onmousetrackover", LzDeclaredEvent, "onmousetrackup", LzDeclaredEvent, "onmousetrackout", LzDeclaredEvent, "onmouseup", LzDeclaredEvent, "onmousedragin", LzDeclaredEvent, "onmousedragout", LzDeclaredEvent, "onmouseupoutside", LzDeclaredEvent, "onopacity", LzDeclaredEvent, "onplay", LzDeclaredEvent, "onremovesubview", LzDeclaredEvent, "onresource", LzDeclaredEvent, "onresourceheight", LzDeclaredEvent, "onresourcewidth", LzDeclaredEvent, "onrotation", LzDeclaredEvent, "onstop", LzDeclaredEvent, "ontotalframes", LzDeclaredEvent, "onunstretchedheight", LzDeclaredEvent, "onunstretchedwidth", LzDeclaredEvent, "onvisible", LzDeclaredEvent, "onvisibility", LzDeclaredEvent, "onwidth", LzDeclaredEvent, "onx", LzDeclaredEvent, "onxoffset", LzDeclaredEvent, "ony", LzDeclaredEvent, "onyoffset", LzDeclaredEvent, "onfont", LzDeclaredEvent, "onfontsize", LzDeclaredEvent, "onfontstyle", LzDeclaredEvent, "ondblclick", LzDeclaredEvent, "DOUBLE_CLICK_TIME", 500, "onclip", LzDeclaredEvent, "capabilities", void 0, "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0 ? parent_$0 : canvas, args_$1);
this.mask = this.immediateparent.mask;
this.__makeSprite(args_$1);
this.capabilities = this.sprite.capabilities;
if (this.capabilities.setid) {
this.sprite.setID(this._dbg_name())
};
if (args_$1["width"] != null || this.__LZhasConstraint("width")) {
this.hassetwidth = true;
this.__LZcheckwidth = false
};
if (args_$1["height"] != null || this.__LZhasConstraint("height")) {
this.hassetheight = true;
this.__LZcheckheight = false
};
if (args_$1["clip"]) {
this.clip = args_$1.clip;
this.makeMasked()
};
var ignore_$2 = LzNode._ignoreAttribute;
if (args_$1["stretches"] != null) {
this.$lzc$set_stretches(args_$1.stretches);
args_$1.stretches = ignore_$2
};
if (args_$1["resource"] != null) {
this.$lzc$set_resource(args_$1.resource);
args_$1.resource = ignore_$2
};
if (args_$1["valign"] && args_$1["y"]) {
Debug.warn(this, "y attribute ignored; superseded by valign constraint.")
};
if (args_$1["align"] && args_$1["x"]) {
Debug.warn(this, "x attribute ignored; superseded by align constraint.")
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__spriteAttribute", (function () {
var $lzsc$temp = function (attrname_$0, value_$1) {
if (this[attrname_$0]) {
if (!this.__LZdeleted) {
var $lzsc$7b6zea = "$lzc$set_" + attrname_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$7b6zea]) : this[$lzsc$7b6zea] instanceof Function) {
this[$lzsc$7b6zea](value_$1)
} else {
this[attrname_$0] = value_$1;
var $lzsc$cza49x = this["on" + attrname_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$cza49x) : $lzsc$cza49x instanceof LzEvent) {
if ($lzsc$cza49x.ready) {
$lzsc$cza49x.sendEvent(value_$1)
}}}}}};
$lzsc$temp["displayName"] = "__spriteAttribute";
return $lzsc$temp
})(), "__makeSprite", (function () {
var $lzsc$temp = function (args_$0) {
this.sprite = new LzSprite(this, false)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
if (this.__updateshadowoninit) {
delete this.__updateshadowoninit;
this.__updateShadow()
};
if (this.sprite) {
this.sprite.init(this.visible)
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "addSubview", (function () {
var $lzsc$temp = function (s_$0) {
if (s_$0.addedToParent) return;
if (this.sprite) {
this.sprite.addChildSprite(s_$0.sprite)
};
if (this.subviews.length == 0) {
this.subviews = []
};
this.subviews.push(s_$0);
s_$0.addedToParent = true;
if (this.__LZcheckwidth) this.__LZcheckwidthFunction(s_$0);
if (this.__LZcheckheight) this.__LZcheckheightFunction(s_$0);
if (this.onaddsubview.ready) this.onaddsubview.sendEvent(s_$0)
};
$lzsc$temp["displayName"] = "addSubview";
return $lzsc$temp
})(), "__LZinstantiationDone", (function () {
var $lzsc$temp = function () {
var vip_$0 = this.immediateparent;
if (vip_$0) {
vip_$0.addSubview(this)
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZinstantiationDone"] || this.nextMethod(arguments.callee, "__LZinstantiationDone")).call(this)
};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "mask", void 0, "focusable", false, "focustrap", void 0, "clip", false, "$lzc$set_clip", (function () {
var $lzsc$temp = function (c_$0) {
this.clip = c_$0;
if (c_$0) {
this.makeMasked()
} else {
this.removeMask()
};
if (this.onclip.ready) this.onclip.sendEvent(this.clip)
};
$lzsc$temp["displayName"] = "$lzc$set_clip";
return $lzsc$temp
})(), "align", "left", "$lzc$set_align", (function () {
var $lzsc$temp = function (align_$0) {
var map_$1;
map_$1 = (function () {
var $lzsc$temp = function (align_$0) {
switch (align_$0) {
case "center":
return "__LZalignCenter";;case "right":
return "__LZalignRight";;case "left":
return null
};
Debug.error("%w.setAttribute(%w, %w): Invalid argument.  Valid choices are: 'left', 'center', or 'right'.", view, "align", align_$0)
};
$lzsc$temp["displayName"] = "map";
return $lzsc$temp
})();
if (this.align == align_$0) return;
var view = this;
var from_$2 = map_$1(this.align);
var to_$3 = map_$1(align_$0);
if (from_$2 != null) {
this.releaseConstraintMethod(from_$2)
};
if (to_$3 != null) {
this.applyConstraintMethod(to_$3, [this.immediateparent, "width", this, "width"])
} else {
this.$lzc$set_x(0)
};
this.align = align_$0
};
$lzsc$temp["displayName"] = "$lzc$set_align";
return $lzsc$temp
})(), "valign", "top", "$lzc$set_valign", (function () {
var $lzsc$temp = function (valign) {
var map_$0;
map_$0 = (function () {
var $lzsc$temp = function (align_$0) {
switch (align_$0) {
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
if (this.valign == valign) return;
var view = this;
var from_$1 = map_$0(this.valign);
var to_$2 = map_$0(valign);
if (from_$1 != null) {
this.releaseConstraintMethod(from_$1)
};
if (to_$2 != null) {
this.applyConstraintMethod(to_$2, [this.immediateparent, "height", this, "height"])
} else {
this.$lzc$set_y(0)
};
this.valign = valign
};
$lzsc$temp["displayName"] = "$lzc$set_valign";
return $lzsc$temp
})(), "source", void 0, "$lzc$set_source", (function () {
var $lzsc$temp = function (v_$0) {
this.setSource(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_source";
return $lzsc$temp
})(), "clickregion", void 0, "onclickregion", LzDeclaredEvent, "$lzc$set_clickregion", (function () {
var $lzsc$temp = function (region_$0) {
if (this.capabilities.clickregion) {
if (this.clickregion !== region_$0) {
if (!this.clickable) {
this.$lzc$set_clickable(true)
};
this.sprite.setClickRegion(region_$0)
}} else {
LzView.__warnCapability("view.clickregion", "clickregion")
};
this.clickregion = region_$0;
if (this.onclickregion.ready) this.onclickregion.sendEvent(region_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_clickregion";
return $lzsc$temp
})(), "cursor", void 0, "fgcolor", null, "onfgcolor", LzDeclaredEvent, "$lzc$set_fgcolor", (function () {
var $lzsc$temp = function (c_$0) {
if (this.tintcolor != "") {
Debug.warn("Setting fgcolor when tintcolor is already set on", this)
};
if (c_$0 != null && isNaN(c_$0)) {
c_$0 = lz.Type.acceptTypeValue("color", c_$0, this, "fgcolor")
};
this.fgcolor = c_$0;
if (this.onfgcolor.ready) this.onfgcolor.sendEvent(c_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_fgcolor";
return $lzsc$temp
})(), "font", void 0, "$lzc$set_font", (function () {
var $lzsc$temp = function (val_$0) {
this.font = val_$0;
if (this.onfont.ready) {
this.onfont.sendEvent(this.font)
}};
$lzsc$temp["displayName"] = "$lzc$set_font";
return $lzsc$temp
})(), "fontstyle", void 0, "$lzc$set_fontstyle", (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 == "plain" || val_$0 == "bold" || val_$0 == "italic" || val_$0 == "bolditalic" || val_$0 == "bold italic") {
this.fontstyle = val_$0;
if (this.onfontstyle.ready) {
this.onfontstyle.sendEvent(this.fontstyle)
}} else {
Debug.warn('invalid font style "%s" on %w', val_$0, this)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontstyle";
return $lzsc$temp
})(), "fontsize", void 0, "$lzc$set_fontsize", (function () {
var $lzsc$temp = function (val_$0) {
if (!(val_$0 <= 0 || isNaN(val_$0))) {
this.fontsize = val_$0;
if (this.onfontsize.ready) {
this.onfontsize.sendEvent(this.fontsize)
}} else {
Debug.warn("invalid font size", val_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontsize";
return $lzsc$temp
})(), "stretches", "none", "$lzc$set_stretches", (function () {
var $lzsc$temp = function (stretch_$0) {
if (!(stretch_$0 == "none" || stretch_$0 == "both" || stretch_$0 == "width" || stretch_$0 == "height")) {
var newstretch_$1 = stretch_$0 == null ? "both" : (stretch_$0 == "x" ? "width" : (stretch_$0 == "y" ? "height" : "none"));
if (newstretch_$1 != "none") {
Debug.info("%w.%s(%w) is deprecated.  Use %w.%s(%w) instead.", this, arguments.callee, stretch_$0, this, arguments.callee, newstretch_$1)
};
stretch_$0 = newstretch_$1
} else if (this.stretches == stretch_$0) {
return
};
if (this.backgroundrepeat != "norepeat") {
Debug.warn("Backgroundrepeat and stretches can't be on at the same time. Canceling backgroundrepeat.", this);
this.$lzc$set_backgroundrepeat("norepeat")
};
this.stretches = stretch_$0;
this.sprite.stretchResource(stretch_$0);
if (stretch_$0 == "width" || stretch_$0 == "both") {
this._setrescwidth = true;
this.__LZcheckwidth = true;
this.reevaluateSize("width")
};
if (stretch_$0 == "height" || stretch_$0 == "both") {
this._setrescheight = true;
this.__LZcheckheight = true;
this.reevaluateSize("height")
}};
$lzsc$temp["displayName"] = "$lzc$set_stretches";
return $lzsc$temp
})(), "backgroundrepeat", "norepeat", "onbackgroundrepeat", LzDeclaredEvent, "$lzc$set_backgroundrepeat", (function () {
var $lzsc$temp = function (backgroundrepeat_$0) {
if (!this.capabilities.backgroundrepeat) {
LzView.__warnCapability("view.backgroundrepeat", "backgroundrepeat");
return
} else if (backgroundrepeat_$0 != "repeat" && backgroundrepeat_$0 != "repeat-x" && backgroundrepeat_$0 != "repeat-y" && backgroundrepeat_$0 != "norepeat") {
Debug.warn("backgroundrepeat must be set to 'repeat', 'repeat-x', 'repeat-y' or 'norepeat':", backgroundrepeat_$0);
return
};
if (backgroundrepeat_$0 !== this.backgroundrepeat) {
if (backgroundrepeat_$0 != "norepeat" && this.stretches != "none") {
Debug.warn("Backgroundrepeat and stretches can't be on at the same time. Canceling stretches.", this);
this.$lzc$set_stretches("none")
};
this.backgroundrepeat = backgroundrepeat_$0;
if (backgroundrepeat_$0 == "norepeat") backgroundrepeat_$0 = null;
this.sprite.setBackgroundRepeat(backgroundrepeat_$0)
};
if (this.onbackgroundrepeat.ready) this.onbackgroundrepeat.sendEvent(this.backgroundrepeat)
};
$lzsc$temp["displayName"] = "$lzc$set_backgroundrepeat";
return $lzsc$temp
})(), "layout", void 0, "$lzc$set_layout", (function () {
var $lzsc$temp = function (layoutobj_$0) {
this.layout = layoutobj_$0;
if (!this.isinited) {
this.__LZstoreAttr(layoutobj_$0, "layout");
return
};
var classname_$1 = layoutobj_$0["class"];
if (classname_$1 == null) {
classname_$1 = "simplelayout"
};
if (this.__LZlayout) {
this.__LZlayout.destroy()
};
if (classname_$1 != "none") {
var o_$2 = {};
for (var i_$3 in layoutobj_$0) {
if (i_$3 != "class") {
o_$2[i_$3] = layoutobj_$0[i_$3]
}};
if (classname_$1 == "null") {
this.__LZlayout = null;
return
};
this.__LZlayout = new (lz[classname_$1])(this, o_$2)
}};
$lzsc$temp["displayName"] = "$lzc$set_layout";
return $lzsc$temp
})(), "aaactive", void 0, "$lzc$set_aaactive", (function () {
var $lzsc$temp = function (s_$0) {
if (this.capabilities.accessibility) {
this.aaactive = s_$0;
this.sprite.setAAActive(s_$0)
} else {
LzView.__warnCapability("view.aaactive", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aaactive";
return $lzsc$temp
})(), "aaname", void 0, "$lzc$set_aaname", (function () {
var $lzsc$temp = function (s_$0) {
if (this.capabilities.accessibility) {
this.aaname = s_$0;
this.sprite.setAAName(s_$0)
} else {
LzView.__warnCapability("view.aaname", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aaname";
return $lzsc$temp
})(), "aadescription", void 0, "$lzc$set_aadescription", (function () {
var $lzsc$temp = function (s_$0) {
if (this.capabilities.accessibility) {
this.aadescription = s_$0;
this.sprite.setAADescription(s_$0)
} else {
LzView.__warnCapability("view.aadescription", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aadescription";
return $lzsc$temp
})(), "aatabindex", void 0, "$lzc$set_aatabindex", (function () {
var $lzsc$temp = function (s_$0) {
if (this.capabilities.accessibility) {
this.aatabindex = s_$0;
this.sprite.setAATabIndex(s_$0)
} else {
LzView.__warnCapability("view.aatabindex", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aatabindex";
return $lzsc$temp
})(), "aasilent", void 0, "$lzc$set_aasilent", (function () {
var $lzsc$temp = function (s_$0) {
if (this.capabilities.accessibility) {
this.aasilent = s_$0;
this.sprite.setAASilent(s_$0)
} else {
LzView.__warnCapability("view.aasilent", "accessibility")
}};
$lzsc$temp["displayName"] = "$lzc$set_aasilent";
return $lzsc$temp
})(), "sendAAEvent", (function () {
var $lzsc$temp = function (childID_$0, eventType_$1, nonHTML_$2) {
switch (arguments.length) {
case 2:
nonHTML_$2 = false
};
if (this.capabilities.accessibility) {
this.sprite.sendAAEvent(childID_$0, eventType_$1, nonHTML_$2)
} else {
LzView.__warnCapability("view.sendAAEvent()", "accessibility")
}};
$lzsc$temp["displayName"] = "sendAAEvent";
return $lzsc$temp
})(), "sprite", null, "visible", true, "visibility", "collapse", "$lzc$set_visibility", (function () {
var $lzsc$temp = function (amVisible_$0) {
if (this.visibility == amVisible_$0) return;
this.visibility = amVisible_$0;
if (!(amVisible_$0 == "visible" || amVisible_$0 == "hidden" || amVisible_$0 == "collapse")) {
Debug.error("%w.%s called with unknown arg '%s' use 'visible', 'hidden', or 'collapse'.", this, arguments.callee, amVisible_$0)
};
if (this.onvisibility.ready) this.onvisibility.sendEvent(amVisible_$0);
this.__LZupdateShown()
};
$lzsc$temp["displayName"] = "$lzc$set_visibility";
return $lzsc$temp
})(), "__LZvizO", true, "__LZvizLoad", true, "__LZvizDat", true, "opacity", 1, "$lzc$set_opacity", (function () {
var $lzsc$temp = function (v_$0) {
if (this.opacity !== v_$0) {
this.opacity = v_$0;
if (this.capabilities.opacity) {
this.sprite.setOpacity(v_$0)
} else {
LzView.__warnCapability("view.opacity", "opacity")
};
var newoviz_$1 = v_$0 != 0;
if (this.__LZvizO != newoviz_$1) {
this.__LZvizO = newoviz_$1;
this.__LZupdateShown()
}};
if (this.onopacity.ready) this.onopacity.sendEvent(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_opacity";
return $lzsc$temp
})(), "bgcolor", null, "onbgcolor", LzDeclaredEvent, "$lzc$set_bgcolor", (function () {
var $lzsc$temp = function (bgc_$0) {
if (bgc_$0 != null && isNaN(bgc_$0)) {
bgc_$0 = lz.Type.acceptTypeValue("color", bgc_$0, this, "bgcolor")
} else if (bgc_$0 < 0) {
bgc_$0 = null
};
this.sprite.setBGColor(bgc_$0);
this.bgcolor = bgc_$0;
if (this.onbgcolor.ready) this.onbgcolor.sendEvent(bgc_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_bgcolor";
return $lzsc$temp
})(), "x", 0, "__set_x_memo", void 0, "$lzc$set_x", (function () {
var $lzsc$temp = function (v_$0) {
this.x = v_$0;
if (this.__set_x_memo === v_$0) {
if (this.onx.ready) {
this.onx.sendEvent(this.x)
};
return
};
this.__set_x_memo = v_$0;
this.__mousecache = null;
if (this.__LZhasoffset) {
if (this.capabilities.rotation) {
v_$0 -= this.xoffset * this.__LZrcos - this.yoffset * this.__LZrsin
} else {
v_$0 -= this.xoffset
}};
if (this.pixellock) {
v_$0 = v_$0 | 0
};
this.sprite.setX(v_$0);
var vip_$1 = this.immediateparent;
if (vip_$1.__LZcheckwidth) {
vip_$1.__LZcheckwidthFunction(this)
};
if (this.onx.ready) {
this.onx.sendEvent(this.x)
}};
$lzsc$temp["displayName"] = "$lzc$set_x";
return $lzsc$temp
})(), "y", 0, "__set_y_memo", void 0, "$lzc$set_y", (function () {
var $lzsc$temp = function (v_$0) {
this.y = v_$0;
if (this.__set_y_memo === v_$0) {
if (this.ony.ready) {
this.ony.sendEvent(this.y)
};
return
};
this.__set_y_memo = v_$0;
this.__mousecache = null;
if (this.__LZhasoffset) {
if (this.capabilities.rotation) {
v_$0 -= this.xoffset * this.__LZrsin + this.yoffset * this.__LZrcos
} else {
v_$0 -= this.yoffset
}};
if (this.pixellock) {
v_$0 = v_$0 | 0
};
this.sprite.setY(v_$0);
var vip_$1 = this.immediateparent;
if (vip_$1.__LZcheckheight) {
vip_$1.__LZcheckheightFunction(this)
};
if (this.ony.ready) {
this.ony.sendEvent(this.y)
}};
$lzsc$temp["displayName"] = "$lzc$set_y";
return $lzsc$temp
})(), "rotation", 0, "$lzc$set_rotation", (function () {
var $lzsc$temp = function (v_$0) {
if (this.capabilities.rotation) {
this.sprite.setRotation(v_$0)
} else {
LzView.__warnCapability("view.rotation", "rotation")
};
this.rotation = v_$0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.xscale != 1 || this.yscale != 1;
var rrad_$1 = Math.PI / 180 * this.rotation;
this.__LZrsin = Math.sin(rrad_$1);
this.__LZrcos = Math.cos(rrad_$1);
if (this.onrotation.ready) this.onrotation.sendEvent(v_$0);
if (this.__LZhasoffset) {
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y)
};
var vip_$2 = this.immediateparent;
if (vip_$2.__LZcheckwidth) vip_$2.__LZcheckwidthFunction(this);
if (vip_$2.__LZcheckheight) vip_$2.__LZcheckheightFunction(this)
};
$lzsc$temp["displayName"] = "$lzc$set_rotation";
return $lzsc$temp
})(), "width", 0, "__set_width_memo", void 0, "$lzc$set_width", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0 != null) {
this.hassetwidth = true;
this.width = v_$0
} else {
this.hassetwidth = false
};
if (this.__set_width_memo === v_$0) {
if (this.onwidth.ready) {
this.onwidth.sendEvent(this.width)
};
return
};
this.__set_width_memo = v_$0;
if (v_$0 == null) {
this.__LZcheckwidth = true;
if (this._setrescwidth) {
this.unstretchedwidth = null
};
this.reevaluateSize("width");
return
};
if (this.pixellock) {
v_$0 = v_$0 | 0
};
if (!this._setrescwidth) {
this.__LZcheckwidth = false
};
if (!(LzText["$lzsc$isa"] ? LzText.$lzsc$isa(this) : this instanceof LzText)) {
this.sprite.setWidth(v_$0)
};
var vip_$1 = this.immediateparent;
if (vip_$1 && vip_$1.__LZcheckwidth) {
vip_$1.__LZcheckwidthFunction(this)
};
if (this.onwidth.ready) {
this.onwidth.sendEvent(this.width)
}};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "height", 0, "__set_height_memo", void 0, "$lzc$set_height", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0 != null) {
this.hassetheight = true;
this.height = v_$0
} else {
this.hassetheight = false
};
if (this.__set_height_memo === v_$0) {
if (this.onheight.ready) {
this.onheight.sendEvent(this.height)
};
return
};
this.__set_height_memo = v_$0;
if (v_$0 == null) {
this.__LZcheckheight = true;
if (this._setrescheight) {
this.unstretchedheight = null
};
this.reevaluateSize("height");
return
};
if (this.pixellock) {
v_$0 = v_$0 | 0
};
if (!this._setrescheight) {
this.__LZcheckheight = false
};
this.sprite.setHeight(v_$0);
var vip_$1 = this.immediateparent;
if (vip_$1 && vip_$1.__LZcheckheight) {
vip_$1.__LZcheckheightFunction(this)
};
if (this.onheight.ready) {
this.onheight.sendEvent(this.height)
}};
$lzsc$temp["displayName"] = "$lzc$set_height";
return $lzsc$temp
})(), "unstretchedwidth", 0, "unstretchedheight", 0, "subviews", [], "xoffset", 0, "$lzc$set_xoffset", (function () {
var $lzsc$temp = function (o_$0) {
this.xoffset = o_$0;
this.__LZhasoffset = this.xoffset != 0 || this.yoffset != 0 || this.__widthoffset != 0 || this.__heightoffset != 0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.xscale != 1 || this.yscale != 1;
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y);
if (this.onxoffset.ready) this.onxoffset.sendEvent(o_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_xoffset";
return $lzsc$temp
})(), "yoffset", 0, "$lzc$set_yoffset", (function () {
var $lzsc$temp = function (o_$0) {
this.yoffset = o_$0;
this.__LZhasoffset = this.xoffset != 0 || this.yoffset != 0 || this.__widthoffset != 0 || this.__heightoffset != 0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.xscale != 1 || this.yscale != 1;
this.__set_x_memo = void 0;
this.$lzc$set_x(this.x);
this.__set_y_memo = void 0;
this.$lzc$set_y(this.y);
if (this.onyoffset.ready) this.onyoffset.sendEvent(o_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_yoffset";
return $lzsc$temp
})(), "__LZrsin", 0, "__LZrcos", 1, "totalframes", 1, "frame", 1, "$lzc$set_frame", (function () {
var $lzsc$temp = function (n_$0) {
this.frame = n_$0;
this.stop(n_$0);
if (this.onframe.ready) this.onframe.sendEvent(n_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_frame";
return $lzsc$temp
})(), "framesloadratio", 0, "loadratio", 0, "hassetheight", false, "hassetwidth", false, "addedToParent", null, "masked", false, "pixellock", null, "clickable", false, "$lzc$set_clickable", (function () {
var $lzsc$temp = function (amclickable_$0) {
this.sprite.setClickable(amclickable_$0);
this.clickable = amclickable_$0;
if (this.onclickable.ready) this.onclickable.sendEvent(amclickable_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_clickable";
return $lzsc$temp
})(), "showhandcursor", null, "$lzc$set_showhandcursor", (function () {
var $lzsc$temp = function (s_$0) {
this.showhandcursor = s_$0;
this.sprite.setShowHandCursor(s_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_showhandcursor";
return $lzsc$temp
})(), "resource", null, "$lzc$set_resource", (function () {
var $lzsc$temp = function (resourceName_$0) {
if (resourceName_$0 == null || resourceName_$0 == this._resource) return;
this.resource = this._resource = resourceName_$0;
this.sprite.setResource(resourceName_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_resource";
return $lzsc$temp
})(), "resourcewidth", 0, "resourceheight", 0, "__LZcheckwidth", true, "__LZcheckheight", true, "__LZhasoffset", null, "__LZoutlieheight", null, "__LZoutliewidth", null, "setLayout", (function () {
var $lzsc$temp = function (layoutobj_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_layout(layoutobj_$0)
};
$lzsc$temp["displayName"] = "setLayout";
return $lzsc$temp
})(), "setFontName", (function () {
var $lzsc$temp = function (val_$0, prop_$1) {
switch (arguments.length) {
case 1:
prop_$1 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_font(val_$0)
};
$lzsc$temp["displayName"] = "setFontName";
return $lzsc$temp
})(), "_setrescwidth", false, "_setrescheight", false, "searchSubviews", (function () {
var $lzsc$temp = function (prop_$0, val_$1) {
var nextS_$2 = this.subviews.concat();
while (nextS_$2.length > 0) {
var s_$3 = nextS_$2;
nextS_$2 = new Array();
for (var i_$4 = s_$3.length - 1;i_$4 >= 0;i_$4--) {
var si_$5 = s_$3[i_$4];
if (si_$5[prop_$0] == val_$1) {
return si_$5
};
var sis_$6 = si_$5.subviews;
for (var j_$7 = sis_$6.length - 1;j_$7 >= 0;j_$7--) {
nextS_$2.push(sis_$6[j_$7])
}}};
return null
};
$lzsc$temp["displayName"] = "searchSubviews";
return $lzsc$temp
})(), "layouts", null, "_resource", null, "setResource", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_resource(v_$0)
};
$lzsc$temp["displayName"] = "setResource";
return $lzsc$temp
})(), "resourceload", (function () {
var $lzsc$temp = function (i_$0) {
if ("resource" in i_$0) {
this.resource = i_$0.resource;
if (this.onresource.ready) this.onresource.sendEvent(i_$0.resource)
};
if (this.resourcewidth != i_$0.width) {
if ("width" in i_$0) {
this.resourcewidth = i_$0.width;
if (this.onresourcewidth.ready) this.onresourcewidth.sendEvent(i_$0.width)
};
if (!this.hassetwidth && this.resourcewidth != this.width || this._setrescwidth && this.unstretchedwidth != this.resourcewidth) {
this.updateWidth(this.resourcewidth)
}};
if (this.resourceheight != i_$0.height) {
if ("height" in i_$0) {
this.resourceheight = i_$0.height;
if (this.onresourceheight.ready) this.onresourceheight.sendEvent(i_$0.height)
};
if (!this.hassetheight && this.resourceheight != this.height || this._setrescheight && this.unstretchedheight != this.resourceheight) {
this.updateHeight(this.resourceheight)
}};
if (i_$0.skiponload != true) {
if (this.onload.ready) this.onload.sendEvent(this)
}};
$lzsc$temp["displayName"] = "resourceload";
return $lzsc$temp
})(), "resourceloaderror", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
this.resourcewidth = 0;
this.resourceheight = 0;
if (this.onresourcewidth.ready) this.onresourcewidth.sendEvent(0);
if (this.onresourceheight.ready) this.onresourceheight.sendEvent(0);
this.reevaluateSize();
if (this.onerror.ready) this.onerror.sendEvent(e_$0)
};
$lzsc$temp["displayName"] = "resourceloaderror";
return $lzsc$temp
})(), "resourceloadtimeout", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
this.resourcewidth = 0;
this.resourceheight = 0;
if (this.onresourcewidth.ready) this.onresourcewidth.sendEvent(0);
if (this.onresourceheight.ready) this.onresourceheight.sendEvent(0);
this.reevaluateSize();
if (this.ontimeout.ready) this.ontimeout.sendEvent(e_$0)
};
$lzsc$temp["displayName"] = "resourceloadtimeout";
return $lzsc$temp
})(), "resourceevent", (function () {
var $lzsc$temp = function (name_$0, value_$1, eventonly_$2, force_$3) {
switch (arguments.length) {
case 2:
eventonly_$2 = false;;case 3:
force_$3 = false
};
var sendevent_$4 = force_$3 == true || eventonly_$2 == true || this[name_$0] != value_$1;
if (eventonly_$2 != true) this[name_$0] = value_$1;
if (sendevent_$4) {
var ev_$5 = this["on" + name_$0];
if (ev_$5.ready) ev_$5.sendEvent(value_$1)
}};
$lzsc$temp["displayName"] = "resourceevent";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted) return;
var vip_$0 = this.immediateparent;
var parentvalid_$1 = vip_$0 && !vip_$0.__LZdeleted;
if (parentvalid_$1) {
if (this.sprite) this.sprite.predestroy();
if (this.addedToParent) {
var svs_$2 = vip_$0.subviews;
if (svs_$2 != null) {
for (var i_$3 = svs_$2.length - 1;i_$3 >= 0;i_$3--) {
if (svs_$2[i_$3] == this) {
svs_$2.splice(i_$3, 1);
break
}}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this);
if (this.sprite) {
this.sprite.destroy(parentvalid_$1)
};
if (parentvalid_$1) {
this.$lzc$set_visible(false);
if (this.addedToParent) {
if (vip_$0["__LZoutliewidth"] == this) {
vip_$0.__LZoutliewidth = null
};
if (vip_$0["__LZoutlieheight"] == this) {
vip_$0.__LZoutlieheight = null
};
if (vip_$0.onremovesubview.ready) vip_$0.onremovesubview.sendEvent(this)
}}};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "setVisible", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(v_$0)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "setVisibility", (function () {
var $lzsc$temp = function (amVisible_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visibility(amVisible_$0)
};
$lzsc$temp["displayName"] = "setVisibility";
return $lzsc$temp
})(), "__LZupdateShown", (function () {
var $lzsc$temp = function () {
if (this.visibility == "collapse") {
var shown_$0 = this.__LZvizO && this.__LZvizDat && this.__LZvizLoad
} else {
var shown_$0 = this.visibility == "visible"
};
if (shown_$0 != this.visible) {
this.visible = shown_$0;
if (this.sprite) {
this.sprite.setVisible(shown_$0)
};
var vip_$1 = this.immediateparent;
if (vip_$1 && vip_$1.__LZcheckwidth) vip_$1.__LZcheckwidthFunction(this);
if (vip_$1 && vip_$1.__LZcheckheight) vip_$1.__LZcheckheightFunction(this);
if (this.onvisible.ready) this.onvisible.sendEvent(shown_$0)
}};
$lzsc$temp["displayName"] = "__LZupdateShown";
return $lzsc$temp
})(), "setWidth", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_width(v_$0)
};
$lzsc$temp["displayName"] = "setWidth";
return $lzsc$temp
})(), "setHeight", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_height(v_$0)
};
$lzsc$temp["displayName"] = "setHeight";
return $lzsc$temp
})(), "setOpacity", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_opacity(v_$0)
};
$lzsc$temp["displayName"] = "setOpacity";
return $lzsc$temp
})(), "setX", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_x(v_$0)
};
$lzsc$temp["displayName"] = "setX";
return $lzsc$temp
})(), "setY", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_y(v_$0)
};
$lzsc$temp["displayName"] = "setY";
return $lzsc$temp
})(), "setRotation", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_rotation(v_$0)
};
$lzsc$temp["displayName"] = "setRotation";
return $lzsc$temp
})(), "setAlign", (function () {
var $lzsc$temp = function (align_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_align(align_$0)
};
$lzsc$temp["displayName"] = "setAlign";
return $lzsc$temp
})(), "__LZalignCenter", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
var vip_$1 = this.immediateparent;
this.$lzc$set_x(vip_$1.width / 2 - this.width / 2)
};
$lzsc$temp["displayName"] = "__LZalignCenter";
return $lzsc$temp
})(), "__LZalignRight", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
var vip_$1 = this.immediateparent;
this.$lzc$set_x(vip_$1.width - this.width)
};
$lzsc$temp["displayName"] = "__LZalignRight";
return $lzsc$temp
})(), "setXOffset", (function () {
var $lzsc$temp = function (o_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_xoffset(o_$0)
};
$lzsc$temp["displayName"] = "setXOffset";
return $lzsc$temp
})(), "setYOffset", (function () {
var $lzsc$temp = function (o_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_yoffset(o_$0)
};
$lzsc$temp["displayName"] = "setYOffset";
return $lzsc$temp
})(), "getBounds", (function () {
var $lzsc$temp = function () {
var width_$0 = (this.width + this.__widthoffset) * this.xscale;
var height_$1 = (this.height + this.__heightoffset) * this.yscale;
var mtrix_$2 = [-this.xoffset, -this.yoffset, width_$0 - this.xoffset, -this.yoffset, -this.xoffset, height_$1 - this.yoffset, width_$0 - this.xoffset, height_$1 - this.yoffset, this.rotation, this.x, this.y];
if (this.__LZstoredbounds) {
var i_$3 = mtrix_$2.length - 1;
while (mtrix_$2[i_$3] == LzView.__LZlastmtrix[i_$3]) {
if (i_$3-- == 0) {
return this.__LZstoredbounds
}}};
var o_$4 = {};
for (var i_$3 = 0;i_$3 < 8;i_$3 += 2) {
var x_$5 = mtrix_$2[i_$3];
var y_$6 = mtrix_$2[i_$3 + 1];
var cx_$7 = x_$5 * this.__LZrcos - y_$6 * this.__LZrsin;
var cy_$8 = x_$5 * this.__LZrsin + y_$6 * this.__LZrcos;
if (o_$4.xoffset == null || o_$4.xoffset > cx_$7) {
o_$4.xoffset = cx_$7
};
if (o_$4.yoffset == null || o_$4.yoffset > cy_$8) {
o_$4.yoffset = cy_$8
};
if (o_$4.width == null || o_$4.width < cx_$7) {
o_$4.width = cx_$7
};
if (o_$4.height == null || o_$4.height < cy_$8) {
o_$4.height = cy_$8
}};
o_$4.width -= o_$4.xoffset;
o_$4.height -= o_$4.yoffset;
o_$4.x = this.x + o_$4.xoffset;
o_$4.y = this.y + o_$4.yoffset;
this.__LZstoredbounds = o_$4;
LzView.__LZlastmtrix = mtrix_$2;
return o_$4
};
$lzsc$temp["displayName"] = "getBounds";
return $lzsc$temp
})(), "$lzc$getBounds_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "rotation", self_$1, "x", self_$1, "y", self_$1, "width", self_$1, "height"]
};
$lzsc$temp["displayName"] = "$lzc$getBounds_dependencies";
return $lzsc$temp
})(), "setValign", (function () {
var $lzsc$temp = function (valign_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_valign(valign_$0)
};
$lzsc$temp["displayName"] = "setValign";
return $lzsc$temp
})(), "__LZvalignMiddle", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
var vip_$1 = this.immediateparent;
this.$lzc$set_y(vip_$1.height / 2 - this.height / 2)
};
$lzsc$temp["displayName"] = "__LZvalignMiddle";
return $lzsc$temp
})(), "__LZvalignBottom", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
var vip_$1 = this.immediateparent;
this.$lzc$set_y(vip_$1.height - this.height)
};
$lzsc$temp["displayName"] = "__LZvalignBottom";
return $lzsc$temp
})(), "setColor", (function () {
var $lzsc$temp = function (c_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fgcolor(c_$0)
};
$lzsc$temp["displayName"] = "setColor";
return $lzsc$temp
})(), "getColor", (function () {
var $lzsc$temp = function () {
Debug.deprecated(this, arguments.callee, this.fgcolor);
return this.fgcolor
};
$lzsc$temp["displayName"] = "getColor";
return $lzsc$temp
})(), "setColorTransform", (function () {
var $lzsc$temp = function (o_$0) {
if (this.capabilities.colortransform) {
Debug.deprecated(this, arguments.callee, this.colortransform);
this.$lzc$set_colortransform({redMultiplier: o_$0.ra != null ? o_$0.ra / 100 : 1, redOffset: o_$0.rb, greenMultiplier: o_$0.ga != null ? o_$0.ga / 100 : 1, greenOffset: o_$0.gb, blueMultiplier: o_$0.ba != null ? o_$0.ba / 100 : 1, blueOffset: o_$0.bb, alphaMultiplier: o_$0.aa != null ? o_$0.aa / 100 : 1, alphaOffset: o_$0.ab})
} else {
LzView.__warnCapability("view.setColorTransform()", "colortransform")
}};
$lzsc$temp["displayName"] = "setColorTransform";
return $lzsc$temp
})(), "oncolortransform", LzDeclaredEvent, "colortransform", void 0, "$lzc$set_colortransform", (function () {
var $lzsc$temp = function (o_$0) {
if (this.capabilities.colortransform) {
if (this.colortransform === o_$0) {
this.oncolortransform.sendEvent(o_$0);
return
};
if (o_$0.redMultiplier == null) o_$0.redMultiplier = 1;
if (o_$0.redOffset == null) o_$0.redOffset = 0;
if (o_$0.greenMultiplier == null) o_$0.greenMultiplier = 1;
if (o_$0.greenOffset == null) o_$0.greenOffset = 0;
if (o_$0.blueMultiplier == null) o_$0.blueMultiplier = 1;
if (o_$0.blueOffset == null) o_$0.blueOffset = 0;
if (o_$0.alphaMultiplier == null) o_$0.alphaMultiplier = 1;
if (o_$0.alphaOffset == null) o_$0.alphaOffset = 0;
this.colortransform = o_$0;
this.sprite.setColorTransform(o_$0);
if (this.oncolortransform.ready) this.oncolortransform.sendEvent(o_$0);
if (o_$0.redOffset || o_$0.greenOffset || o_$0.blueOffset || o_$0.alphaOffset) {
this.tintcolor = LzColorUtils.rgbatoint(o_$0.redOffset, o_$0.greenOffset, o_$0.blueOffset, o_$0.alphaOffset);
if (this.ontintcolor.ready) this.ontintcolor.sendEvent(this.tintcolor)
}} else {
LzView.__warnCapability("view.colortransform", "colortransform")
}};
$lzsc$temp["displayName"] = "$lzc$set_colortransform";
return $lzsc$temp
})(), "getColorTransform", (function () {
var $lzsc$temp = function () {
Debug.deprecated(this, arguments.callee, this.tintcolor);
if (this.capabilities.colortransform) {
return this.sprite.getColorTransform()
} else {
LzView.__warnCapability("view.getColorTransform()", "colortransform")
}};
$lzsc$temp["displayName"] = "getColorTransform";
return $lzsc$temp
})(), "__LZcheckSize", (function () {
var $lzsc$temp = function (sview_$0, axis_$1, xory_$2) {
if (sview_$0.addedToParent) {
if (sview_$0.usegetbounds) {
var bobj_$3 = sview_$0.getBounds()
} else {
var bobj_$3 = sview_$0
};
var ss_$4 = bobj_$3[xory_$2] + bobj_$3[axis_$1];
var ts_$5 = this["_setresc" + axis_$1] ? this["unstretched" + axis_$1] : this[axis_$1];
if (ss_$4 > ts_$5 && sview_$0.visible) {
this["__LZoutlie" + axis_$1] = sview_$0;
if (axis_$1 == "width") {
this.updateWidth(ss_$4)
} else this.updateHeight(ss_$4)
} else if (this["__LZoutlie" + axis_$1] == sview_$0 && (ss_$4 < ts_$5 || !sview_$0.visible)) {
this.reevaluateSize(axis_$1)
}}};
$lzsc$temp["displayName"] = "__LZcheckSize";
return $lzsc$temp
})(), "__LZcheckwidthFunction", (function () {
var $lzsc$temp = function (sview_$0) {
this.__LZcheckSize(sview_$0, "width", "x")
};
$lzsc$temp["displayName"] = "__LZcheckwidthFunction";
return $lzsc$temp
})(), "__LZcheckheightFunction", (function () {
var $lzsc$temp = function (sview_$0) {
this.__LZcheckSize(sview_$0, "height", "y")
};
$lzsc$temp["displayName"] = "__LZcheckheightFunction";
return $lzsc$temp
})(), "measureSize", (function () {
var $lzsc$temp = function (axis_$0) {
var w_$1 = this["resource" + axis_$0];
for (var i_$2 = this.subviews.length - 1;i_$2 >= 0;i_$2--) {
var sview_$3 = this.subviews[i_$2];
if (sview_$3.visible) {
if (sview_$3.usegetbounds) {
var bobj_$4 = sview_$3.getBounds()
} else {
var bobj_$4 = sview_$3
};
var svs_$5 = bobj_$4[axis_$0 == "width" ? "x" : "y"] + bobj_$4[axis_$0];
if (svs_$5 > w_$1) {
w_$1 = svs_$5
}}};
return w_$1
};
$lzsc$temp["displayName"] = "measureSize";
return $lzsc$temp
})(), "measureWidth", (function () {
var $lzsc$temp = function () {
return this.measureSize("width")
};
$lzsc$temp["displayName"] = "measureWidth";
return $lzsc$temp
})(), "measureHeight", (function () {
var $lzsc$temp = function () {
return this.measureSize("height")
};
$lzsc$temp["displayName"] = "measureHeight";
return $lzsc$temp
})(), "updateSize", (function () {
var $lzsc$temp = function (axis_$0, newsize_$1) {
if (axis_$0 == "width") {
this.updateWidth(newsize_$1)
} else this.updateHeight(newsize_$1)
};
$lzsc$temp["displayName"] = "updateSize";
return $lzsc$temp
})(), "updateWidth", (function () {
var $lzsc$temp = function (newsize_$0) {
if (this._setrescwidth) {
this.unstretchedwidth = newsize_$0;
if (this.onunstretchedwidth.ready) this.onunstretchedwidth.sendEvent(newsize_$0)
};
if (!this.hassetwidth) {
this.width = newsize_$0;
this.sprite.setWidth(newsize_$0);
if (this.onwidth.ready) this.onwidth.sendEvent(newsize_$0);
var vip_$1 = this.immediateparent;
if (vip_$1.__LZcheckwidth) vip_$1.__LZcheckwidthFunction(this)
}};
$lzsc$temp["displayName"] = "updateWidth";
return $lzsc$temp
})(), "updateHeight", (function () {
var $lzsc$temp = function (newsize_$0) {
if (this._setrescheight) {
this.unstretchedheight = newsize_$0;
if (this.onunstretchedheight.ready) this.onunstretchedheight.sendEvent(newsize_$0)
};
if (!this.hassetheight) {
this.height = newsize_$0;
this.sprite.setHeight(newsize_$0);
if (this.onheight.ready) this.onheight.sendEvent(newsize_$0);
var vip_$1 = this.immediateparent;
if (vip_$1.__LZcheckheight) vip_$1.__LZcheckheightFunction(this)
}};
$lzsc$temp["displayName"] = "updateHeight";
return $lzsc$temp
})(), "reevaluateSize", (function () {
var $lzsc$temp = function (ia_$0) {
switch (arguments.length) {
case 0:
ia_$0 = null
};
if (ia_$0 == null) {
var axis_$1 = "height";
this.reevaluateSize("width")
} else {
var axis_$1 = ia_$0
};
if (this["hasset" + axis_$1] && !this["_setresc" + axis_$1]) return;
var o_$2 = this["_setresc" + axis_$1] ? this["unstretched" + axis_$1] : this[axis_$1];
var w_$3 = this["resource" + axis_$1] || 0;
this["__LZoutlie" + axis_$1] = this;
for (var i_$4 = this.subviews.length - 1;i_$4 >= 0;i_$4--) {
var sv_$5 = this.subviews[i_$4];
if (sv_$5.usegetbounds) {
var b_$6 = sv_$5.getBounds();
var svs_$7 = b_$6[axis_$1 == "width" ? "x" : "y"] + b_$6[axis_$1]
} else {
var svs_$7 = sv_$5[axis_$1 == "width" ? "x" : "y"] + sv_$5[axis_$1]
};
if (sv_$5.visible && svs_$7 > w_$3) {
w_$3 = svs_$7;
this["__LZoutlie" + axis_$1] = sv_$5
}};
if (o_$2 != w_$3) {
if (axis_$1 == "width") {
this.updateWidth(w_$3)
} else this.updateHeight(w_$3)
}};
$lzsc$temp["displayName"] = "reevaluateSize";
return $lzsc$temp
})(), "updateResourceSize", (function () {
var $lzsc$temp = function () {
this.sprite.updateResourceSize();
this.reevaluateSize()
};
$lzsc$temp["displayName"] = "updateResourceSize";
return $lzsc$temp
})(), "setAttributeRelative", (function () {
var $lzsc$temp = function (prop_$0, refView_$1) {
var tLink_$2 = this.getLinkage(refView_$1);
var val_$3 = refView_$1[prop_$0];
if (prop_$0 == "x" || prop_$0 == "y") {
tLink_$2.update(prop_$0);
{
var $lzsc$smrj6m = (val_$3 - tLink_$2[prop_$0 + "offset"]) / tLink_$2[prop_$0 + "scale"];
if (!this.__LZdeleted) {
var $lzsc$flneg3 = "$lzc$set_" + prop_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$flneg3]) : this[$lzsc$flneg3] instanceof Function) {
this[$lzsc$flneg3]($lzsc$smrj6m)
} else {
this[prop_$0] = $lzsc$smrj6m;
var $lzsc$ix8bya = this["on" + prop_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$ix8bya) : $lzsc$ix8bya instanceof LzEvent) {
if ($lzsc$ix8bya.ready) {
$lzsc$ix8bya.sendEvent($lzsc$smrj6m)
}}}}}} else if (prop_$0 == "width" || prop_$0 == "height") {
var axis_$4 = prop_$0 == "width" ? "x" : "y";
tLink_$2.update(axis_$4);
var scale_$5 = axis_$4 + "scale";
{
var $lzsc$68vidy = val_$3 * refView_$1[scale_$5] / tLink_$2[scale_$5] / this[scale_$5];
if (!this.__LZdeleted) {
var $lzsc$tf075h = "$lzc$set_" + prop_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[$lzsc$tf075h]) : this[$lzsc$tf075h] instanceof Function) {
this[$lzsc$tf075h]($lzsc$68vidy)
} else {
this[prop_$0] = $lzsc$68vidy;
var $lzsc$jvz64o = this["on" + prop_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$jvz64o) : $lzsc$jvz64o instanceof LzEvent) {
if ($lzsc$jvz64o.ready) {
$lzsc$jvz64o.sendEvent($lzsc$68vidy)
}}}}}}};
$lzsc$temp["displayName"] = "setAttributeRelative";
return $lzsc$temp
})(), "$lzc$setAttributeRelative_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1, prop_$2, refView_$3) {
var tLink_$4 = who_$0.getLinkage(refView_$3);
var pass_$5 = 2;
var d_$6 = [];
if (prop_$2 == "width") {
var ax_$7 = "x"
} else if (prop_$2 == "height") {
var ax_$7 = "y"
} else {
var ax_$7 = prop_$2
};
var sax_$8 = ax_$7 == "x" ? "width" : "height";
while (pass_$5) {
if (pass_$5 == 2) {
var carr_$9 = tLink_$4.uplinkArray
} else {
var carr_$9 = tLink_$4.downlinkArray
};
pass_$5--;
for (var i_$a = carr_$9.length - 1;i_$a >= 0;i_$a--) {
d_$6.push(carr_$9[i_$a], ax_$7);
if (d_$6["_setresc" + sax_$8]) {
d_$6.push([carr_$9[i_$a], sax_$8])
}}};
return d_$6
};
$lzsc$temp["displayName"] = "$lzc$setAttributeRelative_dependencies";
return $lzsc$temp
})(), "getAttributeRelative", (function () {
var $lzsc$temp = function (prop_$0, refView_$1) {
if (refView_$1 === this) {
return this[prop_$0]
};
var tLink_$2 = this.getLinkage(refView_$1);
if (prop_$0 == "x" || prop_$0 == "y") {
tLink_$2.update(prop_$0);
return this[prop_$0] * tLink_$2[prop_$0 + "scale"] + tLink_$2[prop_$0 + "offset"]
} else if (prop_$0 == "width" || prop_$0 == "height") {
var axis_$3 = prop_$0 == "width" ? "x" : "y";
tLink_$2.update(axis_$3);
var scale_$4 = axis_$3 + "scale";
return this[prop_$0] * this[scale_$4] * tLink_$2[scale_$4] / refView_$1[scale_$4]
}};
$lzsc$temp["displayName"] = "getAttributeRelative";
return $lzsc$temp
})(), "$lzc$getAttributeRelative_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1, prop_$2, refView_$3) {
var tLink_$4 = self_$1.getLinkage(refView_$3);
var pass_$5 = 2;
var d_$6 = [self_$1, prop_$2];
if (prop_$2 == "width") {
var ax_$7 = "x"
} else if (prop_$2 == "height") {
var ax_$7 = "y"
} else {
var ax_$7 = prop_$2
};
var sax_$8 = ax_$7 == "x" ? "width" : "height";
while (pass_$5) {
if (pass_$5 == 2) {
var carr_$9 = tLink_$4.uplinkArray
} else {
var carr_$9 = tLink_$4.downlinkArray
};
pass_$5--;
for (var i_$a = carr_$9.length - 1;i_$a >= 0;i_$a--) {
var ci_$b = carr_$9[i_$a];
d_$6.push(ci_$b, ax_$7);
if (ci_$b["_setresc" + sax_$8]) {
d_$6.push(ci_$b, sax_$8)
}}};
return d_$6
};
$lzsc$temp["displayName"] = "$lzc$getAttributeRelative_dependencies";
return $lzsc$temp
})(), "__LZviewLinks", null, "getLinkage", (function () {
var $lzsc$temp = function (refView_$0) {
if (this.__LZviewLinks == null) {
this.__LZviewLinks = new Object()
};
var uid_$1 = refView_$0.getUID();
if (this.__LZviewLinks[uid_$1] == null) {
this.__LZviewLinks[uid_$1] = new LzViewLinkage(this, refView_$0)
};
return this.__LZviewLinks[uid_$1]
};
$lzsc$temp["displayName"] = "getLinkage";
return $lzsc$temp
})(), "mouseevent", (function () {
var $lzsc$temp = function (eventname_$0) {
if (this[eventname_$0] && this[eventname_$0].ready) this[eventname_$0].sendEvent(this)
};
$lzsc$temp["displayName"] = "mouseevent";
return $lzsc$temp
})(), "getMouse", (function () {
var $lzsc$temp = function (xory_$0) {
switch (arguments.length) {
case 0:
xory_$0 = null
};
if (this.__mousecache == null || this.__movecounter !== lz.GlobalMouse.__movecounter) {
this.__movecounter = lz.GlobalMouse.__movecounter;
this.__mousecache = this.sprite.getMouse()
};
if (xory_$0 == null) return this.__mousecache;
return this.__mousecache[xory_$0]
};
$lzsc$temp["displayName"] = "getMouse";
return $lzsc$temp
})(), "$lzc$getMouse_dependencies", (function () {
var $lzsc$temp = function () {
var ignore_$0 = Array.prototype.slice.call(arguments, 0);
return [lz.Idle, "idle"]
};
$lzsc$temp["displayName"] = "$lzc$getMouse_dependencies";
return $lzsc$temp
})(), "containsPt", (function () {
var $lzsc$temp = function (x_$0, y_$1) {
var offsetx_$2 = 0;
var offsety_$3 = 0;
var view_$4 = this;
do {
if (!view_$4.visible) return false;
if (view_$4.masked || view_$4 === this) {
var vx_$5 = x_$0 - offsetx_$2;
var vy_$6 = y_$1 - offsety_$3;
if (vx_$5 < 0 || vx_$5 >= view_$4.width || vy_$6 < 0 || vy_$6 >= view_$4.height) {
return false
}};
offsetx_$2 -= view_$4.x;
offsety_$3 -= view_$4.y;
view_$4 = view_$4.immediateparent
} while (view_$4 !== canvas);
return true
};
$lzsc$temp["displayName"] = "containsPt";
return $lzsc$temp
})(), "bringToFront", (function () {
var $lzsc$temp = function () {
if (!this.sprite) {
Debug.warn("no sprite on ", this);
return
};
this.sprite.bringToFront()
};
$lzsc$temp["displayName"] = "bringToFront";
return $lzsc$temp
})(), "getDepthList", (function () {
var $lzsc$temp = function () {
var o_$0 = this.subviews.concat();
o_$0.sort(this.__zCompare);
return o_$0
};
$lzsc$temp["displayName"] = "getDepthList";
return $lzsc$temp
})(), "__zCompare", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var az_$2 = a_$0.sprite.getZ();
var bz_$3 = b_$1.sprite.getZ();
if (az_$2 < bz_$3) return -1;
if (az_$2 > bz_$3) return 1;
return 0
};
$lzsc$temp["displayName"] = "__zCompare";
return $lzsc$temp
})(), "sendBehind", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0 === this) return;
return v_$0 ? this.sprite.sendBehind(v_$0.sprite) : false
};
$lzsc$temp["displayName"] = "sendBehind";
return $lzsc$temp
})(), "sendInFrontOf", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0 === this) return;
return v_$0 ? this.sprite.sendInFrontOf(v_$0.sprite) : false
};
$lzsc$temp["displayName"] = "sendInFrontOf";
return $lzsc$temp
})(), "sendToBack", (function () {
var $lzsc$temp = function () {
this.sprite.sendToBack()
};
$lzsc$temp["displayName"] = "sendToBack";
return $lzsc$temp
})(), "setResourceNumber", (function () {
var $lzsc$temp = function (n_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_frame(n_$0)
};
$lzsc$temp["displayName"] = "setResourceNumber";
return $lzsc$temp
})(), "stretchResource", (function () {
var $lzsc$temp = function (v_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_stretches(v_$0)
};
$lzsc$temp["displayName"] = "stretchResource";
return $lzsc$temp
})(), "setBGColor", (function () {
var $lzsc$temp = function (bgc_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_bgcolor(bgc_$0)
};
$lzsc$temp["displayName"] = "setBGColor";
return $lzsc$temp
})(), "setSource", (function () {
var $lzsc$temp = function (source_$0, cache_$1, headers_$2, filetype_$3) {
switch (arguments.length) {
case 1:
cache_$1 = null;;case 2:
headers_$2 = null;;case 3:
filetype_$3 = null
};
this.sprite.setSource(source_$0, cache_$1, headers_$2, filetype_$3)
};
$lzsc$temp["displayName"] = "setSource";
return $lzsc$temp
})(), "unload", (function () {
var $lzsc$temp = function () {
this._resource = null;
this.sprite.unload()
};
$lzsc$temp["displayName"] = "unload";
return $lzsc$temp
})(), "makeMasked", (function () {
var $lzsc$temp = function () {
this.sprite.setClip(true);
this.masked = true;
this.mask = this
};
$lzsc$temp["displayName"] = "makeMasked";
return $lzsc$temp
})(), "removeMask", (function () {
var $lzsc$temp = function () {
this.sprite.setClip(false);
this.masked = false;
this.mask = null
};
$lzsc$temp["displayName"] = "removeMask";
return $lzsc$temp
})(), "setClickable", (function () {
var $lzsc$temp = function (amclickable_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_clickable(amclickable_$0)
};
$lzsc$temp["displayName"] = "setClickable";
return $lzsc$temp
})(), "$lzc$set_cursor", (function () {
var $lzsc$temp = function (cursor_$0) {
this.sprite.setCursor(cursor_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_cursor";
return $lzsc$temp
})(), "setCursor", (function () {
var $lzsc$temp = function (cursor_$0) {
switch (arguments.length) {
case 0:
cursor_$0 = null
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_cursor(cursor_$0)
};
$lzsc$temp["displayName"] = "setCursor";
return $lzsc$temp
})(), "$lzc$set_play", (function () {
var $lzsc$temp = function (b_$0) {
if (b_$0) {
this.play()
} else {
this.stop()
}};
$lzsc$temp["displayName"] = "$lzc$set_play";
return $lzsc$temp
})(), "setPlay", (function () {
var $lzsc$temp = function (b_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_play(b_$0)
};
$lzsc$temp["displayName"] = "setPlay";
return $lzsc$temp
})(), "getMCRef", (function () {
var $lzsc$temp = function () {
Debug.deprecated(this, arguments.callee, this.getDisplayObject);
return this.getDisplayObject()
};
$lzsc$temp["displayName"] = "getMCRef";
return $lzsc$temp
})(), "getDisplayObject", (function () {
var $lzsc$temp = function () {
return this.sprite.getDisplayObject()
};
$lzsc$temp["displayName"] = "getDisplayObject";
return $lzsc$temp
})(), "play", (function () {
var $lzsc$temp = function (f_$0, rel_$1) {
switch (arguments.length) {
case 0:
f_$0 = null;;case 1:
rel_$1 = false
};
this.sprite.play(f_$0, rel_$1)
};
$lzsc$temp["displayName"] = "play";
return $lzsc$temp
})(), "stop", (function () {
var $lzsc$temp = function (f_$0, rel_$1) {
switch (arguments.length) {
case 0:
f_$0 = null;;case 1:
rel_$1 = false
};
this.sprite.stop(f_$0, rel_$1)
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "setVolume", (function () {
var $lzsc$temp = function (v_$0) {
if (this.capabilities.audio) {
this.sprite.setVolume(v_$0)
} else {
LzView.__warnCapability("view.setVolume()", "audio")
}};
$lzsc$temp["displayName"] = "setVolume";
return $lzsc$temp
})(), "getVolume", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
return this.sprite.getVolume()
} else {
LzView.__warnCapability("view.getVolume()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getVolume";
return $lzsc$temp
})(), "setPan", (function () {
var $lzsc$temp = function (p_$0) {
if (this.capabilities.audio) {
this.sprite.setPan(p_$0)
} else {
LzView.__warnCapability("view.setPan()", "audio")
}};
$lzsc$temp["displayName"] = "setPan";
return $lzsc$temp
})(), "getPan", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
return this.sprite.getPan()
} else {
LzView.__warnCapability("view.getPan()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getPan";
return $lzsc$temp
})(), "getZ", (function () {
var $lzsc$temp = function () {
return this.sprite.getZ()
};
$lzsc$temp["displayName"] = "getZ";
return $lzsc$temp
})(), "seek", (function () {
var $lzsc$temp = function (secs_$0) {
if (this.capabilities.audio) {
if (this.sprite.isaudio) {
this.sprite.seek(secs_$0, this.playing);
return
}};
var frames_$1 = secs_$0 * canvas.framerate;
if (this.playing) {
this.play(frames_$1, true)
} else {
this.stop(frames_$1, true)
}};
$lzsc$temp["displayName"] = "seek";
return $lzsc$temp
})(), "getCurrentTime", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
if (this.sprite.isaudio) {
return this.sprite.getCurrentTime()
}};
return this.frame / canvas.framerate
};
$lzsc$temp["displayName"] = "getCurrentTime";
return $lzsc$temp
})(), "$lzc$getCurrentTime_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "frame"]
};
$lzsc$temp["displayName"] = "$lzc$getCurrentTime_dependencies";
return $lzsc$temp
})(), "getTotalTime", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
if (this.sprite.isaudio) {
return this.sprite.getTotalTime()
}};
return this.totalframes / canvas.framerate
};
$lzsc$temp["displayName"] = "getTotalTime";
return $lzsc$temp
})(), "$lzc$getTotalTime_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "load"]
};
$lzsc$temp["displayName"] = "$lzc$getTotalTime_dependencies";
return $lzsc$temp
})(), "getID3", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
if (this.sprite.isaudio) {
return this.sprite.getID3()
}} else {
LzView.__warnCapability("view.getID3()", "audio")
};
return null
};
$lzsc$temp["displayName"] = "getID3";
return $lzsc$temp
})(), "setShowHandCursor", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_showhandcursor(s_$0)
};
$lzsc$temp["displayName"] = "setShowHandCursor";
return $lzsc$temp
})(), "setAccessible", (function () {
var $lzsc$temp = function (accessible_$0) {
if (this.capabilities.accessibility) {
this.sprite.setAccessible(accessible_$0)
} else {
LzView.__warnCapability("view.setAccessible()", "accessibility")
}};
$lzsc$temp["displayName"] = "setAccessible";
return $lzsc$temp
})(), "setAAActive", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aaactive(s_$0)
};
$lzsc$temp["displayName"] = "setAAActive";
return $lzsc$temp
})(), "setAAName", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aaname(s_$0)
};
$lzsc$temp["displayName"] = "setAAName";
return $lzsc$temp
})(), "setAADescription", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aadescription(s_$0)
};
$lzsc$temp["displayName"] = "setAADescription";
return $lzsc$temp
})(), "setAATabIndex", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aatabindex(s_$0)
};
$lzsc$temp["displayName"] = "setAATabIndex";
return $lzsc$temp
})(), "setAASilent", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_aasilent(s_$0)
};
$lzsc$temp["displayName"] = "setAASilent";
return $lzsc$temp
})(), "shouldYieldFocus", (function () {
var $lzsc$temp = function () {
return true
};
$lzsc$temp["displayName"] = "shouldYieldFocus";
return $lzsc$temp
})(), "blurring", false, "getProxyURL", (function () {
var $lzsc$temp = function (url_$0) {
switch (arguments.length) {
case 0:
url_$0 = null
};
var proxy_$1 = this.proxyurl;
if (proxy_$1 == null) {
return null
} else if (typeof proxy_$1 == "string") {
return proxy_$1
} else if (typeof proxy_$1 == "function") {
return proxy_$1(url_$0)
} else {
Debug.error("Unknown value for proxyurl expression %w on %w", proxy_$1, this)
}};
$lzsc$temp["displayName"] = "getProxyURL";
return $lzsc$temp
})(), "__LZcheckProxyPolicy", (function () {
var $lzsc$temp = function (url_$0) {
if (this.__proxypolicy != null) {
return this.__proxypolicy(url_$0)
};
var pol_$1 = LzView.__LZproxypolicies;
for (var i_$2 = pol_$1.length - 1;i_$2 >= 0;i_$2--) {
var resp_$3 = pol_$1[i_$2](url_$0);
if (resp_$3 != null) return resp_$3
};
return canvas.proxied
};
$lzsc$temp["displayName"] = "__LZcheckProxyPolicy";
return $lzsc$temp
})(), "setProxyPolicy", (function () {
var $lzsc$temp = function (f_$0) {
this.__proxypolicy = f_$0
};
$lzsc$temp["displayName"] = "setProxyPolicy";
return $lzsc$temp
})(), "__proxypolicy", null, "setProxyURL", (function () {
var $lzsc$temp = function (f_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_proxyurl(f_$0)
};
$lzsc$temp["displayName"] = "setProxyURL";
return $lzsc$temp
})(), "proxyurl", (function () {
var $lzsc$temp = function (url_$0) {
return canvas.getProxyURL(url_$0)
};
$lzsc$temp["displayName"] = "views/LaszloView.lzs#3442/16";
return $lzsc$temp
})(), "$lzc$set_proxyurl", (function () {
var $lzsc$temp = function (f_$0) {
this.proxyurl = f_$0
};
$lzsc$temp["displayName"] = "$lzc$set_proxyurl";
return $lzsc$temp
})(), "contextmenu", null, "$lzc$set_contextmenu", (function () {
var $lzsc$temp = function (cmenu_$0) {
this.contextmenu = cmenu_$0;
this.sprite.setContextMenu(cmenu_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_contextmenu";
return $lzsc$temp
})(), "setContextMenu", (function () {
var $lzsc$temp = function (cmenu_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_contextmenu(cmenu_$0)
};
$lzsc$temp["displayName"] = "setContextMenu";
return $lzsc$temp
})(), "getContextMenu", (function () {
var $lzsc$temp = function () {
Debug.deprecated(this, arguments.callee, "contextmenu");
return this.contextmenu
};
$lzsc$temp["displayName"] = "getContextMenu";
return $lzsc$temp
})(), "getNextSelection", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "getNextSelection";
return $lzsc$temp
})(), "getPrevSelection", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "getPrevSelection";
return $lzsc$temp
})(), "cachebitmap", false, "$lzc$set_cachebitmap", (function () {
var $lzsc$temp = function (cache_$0) {
if (cache_$0 != this.cachebitmap) {
this.cachebitmap = cache_$0;
if (this.capabilities.bitmapcaching) {
this.sprite.setBitmapCache(cache_$0)
} else {
LzView.__warnCapability("view.cachebitmap", "bitmapcaching")
}}};
$lzsc$temp["displayName"] = "$lzc$set_cachebitmap";
return $lzsc$temp
})(), "oncontext", LzDeclaredEvent, "context", null, "$lzc$set_context", (function () {
var $lzsc$temp = function (context_$0) {
this.context = context_$0;
if (this.oncontext.ready) {
this.oncontext.sendEvent(context_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_context";
return $lzsc$temp
})(), "createContext", (function () {
var $lzsc$temp = function () {
if (this.capabilities["2dcanvas"]) {
this.sprite.setContextCallback(this, "$lzc$set_context");
var context_$0 = this.sprite.getContext();
if (context_$0) {
{
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_context"]) : this["$lzc$set_context"] instanceof Function) {
this["$lzc$set_context"](context_$0)
} else {
this["context"] = context_$0;
var $lzsc$ctoqpm = this["oncontext"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$ctoqpm) : $lzsc$ctoqpm instanceof LzEvent) {
if ($lzsc$ctoqpm.ready) {
$lzsc$ctoqpm.sendEvent(context_$0)
}}}}}}} else {
LzView.__warnCapability("view.createContext", "2dcanvas")
}};
$lzsc$temp["displayName"] = "createContext";
return $lzsc$temp
})(), "onshadowangle", LzDeclaredEvent, "shadowangle", 0, "$lzc$set_shadowangle", (function () {
var $lzsc$temp = function (angle_$0) {
if (this.capabilities["dropshadows"]) {
this.shadowangle = angle_$0;
this.__updateShadow();
if (this.onshadowangle.ready) {
this.onshadowangle.sendEvent(angle_$0)
}} else {
LzView.__warnCapability("view.shadowangle", "dropshadows")
}};
$lzsc$temp["displayName"] = "$lzc$set_shadowangle";
return $lzsc$temp
})(), "onshadowdistance", LzDeclaredEvent, "shadowdistance", 10, "$lzc$set_shadowdistance", (function () {
var $lzsc$temp = function (distance_$0) {
if (this.capabilities["dropshadows"]) {
if (distance_$0 < 0.01) distance_$0 = 0;
this.shadowdistance = distance_$0;
this.__updateShadow();
if (this.onshadowdistance.ready) {
this.onshadowdistance.sendEvent(distance_$0)
}} else {
LzView.__warnCapability("view.shadowdistance", "dropshadows")
}};
$lzsc$temp["displayName"] = "$lzc$set_shadowdistance";
return $lzsc$temp
})(), "onshadowcolor", LzDeclaredEvent, "shadowcolor", 0, "$lzc$set_shadowcolor", (function () {
var $lzsc$temp = function (color_$0) {
if (this.capabilities["dropshadows"]) {
this.shadowcolor = LzColorUtils.torgb(color_$0);
this.__updateShadow();
if (this.onshadowcolor.ready) {
this.onshadowcolor.sendEvent(color_$0)
}} else {
LzView.__warnCapability("view.shadowcolor", "dropshadows")
}};
$lzsc$temp["displayName"] = "$lzc$set_shadowcolor";
return $lzsc$temp
})(), "onshadowblurradius", LzDeclaredEvent, "shadowblurradius", 4, "$lzc$set_shadowblurradius", (function () {
var $lzsc$temp = function (blurradius_$0) {
if (this.capabilities["dropshadows"]) {
if (blurradius_$0 < 0.01) blurradius_$0 = 0;
this.shadowblurradius = blurradius_$0;
this.__updateShadow();
if (this.onshadowblurradius.ready) {
this.onshadowblurradius.sendEvent(blurradius_$0)
}} else {
LzView.__warnCapability("view.shadowblurradius", "dropshadows")
}};
$lzsc$temp["displayName"] = "$lzc$set_shadowblurradius";
return $lzsc$temp
})(), "__updateShadow", (function () {
var $lzsc$temp = function () {
if (!this.isinited) {
this.__updateshadowoninit = true
} else {
this.sprite.updateShadow(this.shadowcolor, this.shadowdistance, this.shadowangle, this.shadowblurradius)
}};
$lzsc$temp["displayName"] = "__updateShadow";
return $lzsc$temp
})(), "ontintcolor", LzDeclaredEvent, "tintcolor", "", "$lzc$set_tintcolor", (function () {
var $lzsc$temp = function (color_$0) {
if (this.capabilities.colortransform) {
if (this.fgcolor != null) {
Debug.warn("Setting tintcolor when fgcolor is already set on", this)
};
var obj_$1 = {redMultiplier: 0, greenMultiplier: 0, blueMultiplier: 0, alphaMultiplier: 1};
if (color_$0 != null && color_$0 != "") {
if (isNaN(color_$0)) {
var color_$0 = lz.Type.acceptTypeValue("color", color_$0, this, "tintcolor")
};
var rgba_$2 = LzColorUtils.inttorgba(color_$0);
obj_$1.redOffset = rgba_$2[0];
obj_$1.greenOffset = rgba_$2[1];
obj_$1.blueOffset = rgba_$2[2];
if (rgba_$2[3] != null) {
obj_$1.alphaOffset = rgba_$2[3]
}};
this.$lzc$set_colortransform(obj_$1);
return
} else {
LzView.__warnCapability("view.tintcolor", "colortransform")
};
this.tintcolor = color_$0;
if (this.ontintcolor.ready) this.ontintcolor.sendEvent(color_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_tintcolor";
return $lzsc$temp
})(), "oncornerradius", LzDeclaredEvent, "cornerradius", "4", "$lzc$set_cornerradius", (function () {
var $lzsc$temp = function (radius_$0) {
if (this.capabilities["cornerradius"]) {
if (this.cornerradius !== radius_$0) {
var radii_$1 = String(radius_$0).split(" ");
var l_$2 = radii_$1.length;
if (l_$2 == 0) return;
if (l_$2 <= 1) radii_$1[1] = radii_$1[0];
if (l_$2 <= 3) radii_$1[3] = radii_$1[1];
if (l_$2 <= 2) radii_$1[2] = radii_$1[0];
for (var i_$3 = 0, l_$2 = radii_$1.length;i_$3 < l_$2;i_$3++) {
radii_$1[i_$3] = parseFloat(radii_$1[i_$3])
};
this.sprite.setCornerRadius(radii_$1)
}} else {
LzView.__warnCapability("view.cornerradius", "cornerradius")
};
this.cornerradius = radius_$0;
if (this.oncornerradius.ready) this.oncornerradius.sendEvent(radius_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_cornerradius";
return $lzsc$temp
})(), "isMouseOver", (function () {
var $lzsc$temp = function () {
var mousepos_$0 = this.getMouse();
return this.containsPt(mousepos_$0.x, mousepos_$0.y)
};
$lzsc$temp["displayName"] = "isMouseOver";
return $lzsc$temp
})(), "$lzc$isMouseOver_dependencies", (function () {
var $lzsc$temp = function () {
var ignore_$0 = Array.prototype.slice.call(arguments, 0);
return [lz.Idle, "idle"]
};
$lzsc$temp["displayName"] = "$lzc$isMouseOver_dependencies";
return $lzsc$temp
})(), "isInFrontOf", (function () {
var $lzsc$temp = function (sibling_$0) {
if (!sibling_$0 || sibling_$0.parent !== this.parent) return null;
return this.sprite.getZ() > sibling_$0.sprite.getZ()
};
$lzsc$temp["displayName"] = "isInFrontOf";
return $lzsc$temp
})(), "isBehind", (function () {
var $lzsc$temp = function (sibling_$0) {
if (!sibling_$0 || sibling_$0.parent !== this.parent) return null;
return this.sprite.getZ() < sibling_$0.sprite.getZ()
};
$lzsc$temp["displayName"] = "isBehind";
return $lzsc$temp
})(), "__widthoffset", 0, "__heightoffset", 0, "__styleinfo", {}, "setCSS", (function () {
var $lzsc$temp = function (stylename_$0, value_$1) {
var styleinfo_$2 = this.__styleinfo[stylename_$0];
if (!styleinfo_$2) {
Debug.warn("Unknown CSS property", stylename_$0);
return
};
if (this.capabilities[styleinfo_$2.capability]) {
this.sprite.setCSS(stylename_$0, value_$1, styleinfo_$2.isdimension)
} else {
LzView.__warnCapability("view." + stylename_$0.toLowerCase(), styleinfo_$2.capability);
return
};
if (styleinfo_$2.affectsoffset) {
this.__LZhasoffset = this.xoffset != 0 || this.yoffset != 0 || this.__widthoffset != 0 || this.__heightoffset != 0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.xscale != 1 || this.yscale != 1;
var vip_$3 = this.immediateparent;
if (vip_$3.__LZcheckwidth) vip_$3.__LZcheckwidthFunction(this);
if (vip_$3.__LZcheckheight) vip_$3.__LZcheckheightFunction(this)
}};
$lzsc$temp["displayName"] = "setCSS";
return $lzsc$temp
})(), "__LZresolveOtherReferences", (function () {
var $lzsc$temp = function (rdict_$0) {
var layout_$1 = rdict_$0["layout"];
if (layout_$1 != null) {
this.$lzc$set_layout(layout_$1)
}};
$lzsc$temp["displayName"] = "__LZresolveOtherReferences";
return $lzsc$temp
})(), "usegetbounds", false, "xscale", 1, "onxscale", LzDeclaredEvent, "$lzc$set_xscale", (function () {
var $lzsc$temp = function (xscale_$0) {
if (this.capabilities.scaling) {
this.xscale = xscale_$0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.xscale != 1 || this.yscale != 1;
this.sprite.setXScale(xscale_$0)
} else {
LzView.__warnCapability("view.xscale", "scaling")
};
if (this.onxscale.ready) this.onxscale.sendEvent(xscale_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_xscale";
return $lzsc$temp
})(), "yscale", 1, "onyscale", LzDeclaredEvent, "$lzc$set_yscale", (function () {
var $lzsc$temp = function (yscale_$0) {
if (this.capabilities.scaling) {
this.yscale = yscale_$0;
this.usegetbounds = this.__LZhasoffset || this.rotation != 0 || this.yscale != 1 || this.yscale != 1;
this.sprite.setYScale(yscale_$0)
} else {
LzView.__warnCapability("view.yscale", "scaling")
};
if (this.onyscale.ready) this.onyscale.sendEvent(yscale_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_yscale";
return $lzsc$temp
})()], LzNode, ["tagname", "view", "attributes", new LzInheritedHash(LzNode.attributes), "__LZlastmtrix", [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], "__LZproxypolicies", [], "addProxyPolicy", (function () {
var $lzsc$temp = function (f_$0) {
LzView.__LZproxypolicies.push(f_$0)
};
$lzsc$temp["displayName"] = "addProxyPolicy";
return $lzsc$temp
})(), "removeProxyPolicy", (function () {
var $lzsc$temp = function (f_$0) {
var pol_$1 = LzView.__LZproxypolicies;
for (var i_$2 = 0;i_$2 < pol_$1.length;i_$2++) {
if (pol_$1[i_$2] == f_$0) {
LzView.__LZproxypolicies = pol_$1.splice(i_$2, 1);
return true
}};
return false
};
$lzsc$temp["displayName"] = "removeProxyPolicy";
return $lzsc$temp
})(), "__warnCapability", (function () {
var $lzsc$temp = function (msg_$0, capabilityname_$1) {
switch (arguments.length) {
case 1:
capabilityname_$1 = ""
};
var check_$2 = capabilityname_$1 == "" ? "" : 'Check "canvas.capabilities.' + capabilityname_$1 + '" to avoid this warning.';
Debug.warn("The %s runtime does not support %s. %s", canvas["runtime"], msg_$0, check_$2)
};
$lzsc$temp["displayName"] = "__warnCapability";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "views/LaszloView.lzs#46/1";
return $lzsc$temp
})()(LzView);
lz[LzView.tagname] = LzView;
Class.make("LzTextscrollEvent", ["__senderscope", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1, d_$2) {
switch (arguments.length) {
case 2:
d_$2 = null
};
this.__senderscope = eventSender_$0;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, eventSender_$0, eventName_$1, d_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "notify", (function () {
var $lzsc$temp = function (ready_$0) {
if (this.__senderscope) {
var scope_$1 = this.__senderscope;
if (ready_$0) {
scope_$1.__scrollEventListeners++
} else {
scope_$1.__scrollEventListeners--
};
var scrollevents_$2 = scope_$1.__scrollEventListeners > 0 || scope_$1.__userscrollevents;
if (scrollevents_$2 != scope_$1.scrollevents) {
scope_$1.__set_scrollevents(scrollevents_$2)
}}};
$lzsc$temp["displayName"] = "notify";
return $lzsc$temp
})()], LzNotifyingEvent, ["LzDeclaredTextscrollEvent", void 0]);
LzTextscrollEvent.LzDeclaredTextscrollEvent = new LzDeclaredEventClass(LzTextscrollEvent);
Class.make("LzTextlinkEvent", ["__senderscope", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1, d_$2) {
switch (arguments.length) {
case 2:
d_$2 = null
};
this.__senderscope = eventSender_$0;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, eventSender_$0, eventName_$1, d_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "notify", (function () {
var $lzsc$temp = function (ready_$0) {
var scope_$1 = this.__senderscope;
if (scope_$1 && scope_$1.tsprite) {
scope_$1.tsprite.activateLinks(ready_$0)
}};
$lzsc$temp["displayName"] = "notify";
return $lzsc$temp
})()], LzNotifyingEvent, ["LzDeclaredTextlinkEvent", void 0]);
LzTextlinkEvent.LzDeclaredTextlinkEvent = new LzDeclaredEventClass(LzTextlinkEvent);
Class.make("LzText", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "selectable", false, "hasdirectionallayout", false, "onselectable", LzDeclaredEvent, "$lzc$set_selectable", (function () {
var $lzsc$temp = function (isSel_$0) {
isSel_$0 = !(!isSel_$0);
this.selectable = isSel_$0;
this.tsprite.setSelectable(isSel_$0);
if (this.onselectable.ready) this.onselectable.sendEvent(isSel_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_selectable";
return $lzsc$temp
})(), "direction", "ltr", "ondirection", LzDeclaredEvent, "$lzc$set_direction", (function () {
var $lzsc$temp = function (dir_$0) {
this.direction = dir_$0;
if (this.capabilities.directional_layout) {
this.tsprite.setDirection(dir_$0)
} else {
LzView.__warnCapability("text.directional_layout", "direction")
};
if (this.ondirection.ready) this.ondirection.sendEvent(dir_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_direction";
return $lzsc$temp
})(), "antiAliasType", "advanced", "$lzc$set_antiAliasType", (function () {
var $lzsc$temp = function (aliasType_$0) {
if (this.capabilities.advancedfonts) {
if (aliasType_$0 == "normal" || aliasType_$0 == "advanced") {
this.antiAliasType = aliasType_$0;
this.tsprite.setAntiAliasType(aliasType_$0)
} else {
Debug.warn("antiAliasType invalid, must be 'normal' or 'advanced', but you said '" + aliasType_$0 + "'")
}} else {
LzView.__warnCapability("text.setAntiAliasType()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_antiAliasType";
return $lzsc$temp
})(), "gridFit", "pixel", "$lzc$set_gridFit", (function () {
var $lzsc$temp = function (gridFit_$0) {
if (this.capabilities.advancedfonts) {
if (gridFit_$0 == "none" || gridFit_$0 == "pixel" || gridFit_$0 == "subpixel") {
this.gridFit = gridFit_$0;
this.tsprite.setGridFit(gridFit_$0)
} else {
Debug.warn("gridFit invalid, must be 'none', 'pixel', or 'subpixel' but you said '" + gridFit_$0 + "'")
}} else {
LzView.__warnCapability("text.setGridFit()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_gridFit";
return $lzsc$temp
})(), "sharpness", 0, "$lzc$set_sharpness", (function () {
var $lzsc$temp = function (sharpness_$0) {
if (this.capabilities.advancedfonts) {
if (sharpness_$0 >= -400 && sharpness_$0 <= 400) {
this.sharpness = sharpness_$0;
this.tsprite.setSharpness(sharpness_$0)
} else {
Debug.warn("sharpness out of range, must be -400 to 400")
}} else {
LzView.__warnCapability("text.setSharpness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_sharpness";
return $lzsc$temp
})(), "thickness", 0, "$lzc$set_thickness", (function () {
var $lzsc$temp = function (thickness_$0) {
if (this.capabilities.advancedfonts) {
if (thickness_$0 >= -200 && thickness_$0 <= 200) {
this.thickness = thickness_$0;
this.tsprite.setThickness(thickness_$0)
} else {
Debug.warn("thickness out of range, must be -200 to 200")
}} else {
LzView.__warnCapability("text.setThickness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "$lzc$set_thickness";
return $lzsc$temp
})(), "$lzc$set_clip", (function () {
var $lzsc$temp = function (c_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_clip"] || this.nextMethod(arguments.callee, "$lzc$set_clip")).call(this, c_$0);
if (this.isinited && this.scrollevents && !this.clip) {
Debug.warn("You have set 'clip' to false on text view ", this, ", 'scrollevents' will be set to false as well");
this.$lzc$set_scrollevents(false)
}};
$lzsc$temp["displayName"] = "$lzc$set_clip";
return $lzsc$temp
})(), "$lzc$set_width", (function () {
var $lzsc$temp = function (val_$0) {
this.tsprite.setWidth(val_$0);
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_width"] || this.nextMethod(arguments.callee, "$lzc$set_width")).call(this, val_$0);
if (this.scrollwidth < this.width) {
this.scrollwidth = this.width
};
this.updateAttribute("maxhscroll", this.scrollwidth - this.width);
if (!this.hassetheight) {
var h_$1 = this.tsprite.getTextfieldHeight();
if (h_$1 > 0 && h_$1 != this.height) {
this.updateHeight(h_$1)
}}};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "getDefaultWidth", (function () {
var $lzsc$temp = function () {
return 0
};
$lzsc$temp["displayName"] = "getDefaultWidth";
return $lzsc$temp
})(), "updateAttribute", (function () {
var $lzsc$temp = function (name_$0, value_$1) {
this[name_$0] = value_$1;
var event_$2 = this["on" + name_$0];
if (event_$2.ready) {
event_$2.sendEvent(value_$1)
}};
$lzsc$temp["displayName"] = "updateAttribute";
return $lzsc$temp
})(), "updateLineAttribute", (function () {
var $lzsc$temp = function (name_$0, value_$1) {
var lineNo_$2;
if (this.capabilities.linescrolling) {
lineNo_$2 = this.tsprite.pixelToLineNo(value_$1)
} else {
lineNo_$2 = Math.ceil(value_$1 / this.lineheight) + 1
};
this.updateAttribute(name_$0, lineNo_$2)
};
$lzsc$temp["displayName"] = "updateLineAttribute";
return $lzsc$temp
})(), "lineheight", 0, "$lzc$set_lineheight", (function () {
var $lzsc$temp = function (x_$0) {
Debug.error("lineheight is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_lineheight";
return $lzsc$temp
})(), "onlineheight", LzDeclaredEvent, "__scrollEventListeners", 0, "scrollevents", false, "__userscrollevents", false, "$lzc$set_scrollevents", (function () {
var $lzsc$temp = function (on_$0) {
this.__userscrollevents = on_$0;
if (this.scrollevents !== on_$0) {
this.__set_scrollevents(on_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_scrollevents";
return $lzsc$temp
})(), "__set_scrollevents", (function () {
var $lzsc$temp = function (on_$0) {
if (this.scrollevents == on_$0) return;
this.scrollevents = on_$0;
if (this.isinited && this.scrollevents && !this.clip) {
Debug.warn("You have set scrollevents to true on text view ", this, ", but you must also set clip='true' on this view to have scrolling work correctly")
};
this.tsprite.setScrollEvents(on_$0);
if (this.onscrollevents.ready) this.onscrollevents.sendEvent(on_$0)
};
$lzsc$temp["displayName"] = "__set_scrollevents";
return $lzsc$temp
})(), "onscrollevents", LzDeclaredEvent, "yscroll", 0, "$lzc$set_yscroll", (function () {
var $lzsc$temp = function (n_$0) {
if (n_$0 > 0) {
Debug.warn("Invalid value for %w.yscroll: %w", this, n_$0);
n_$0 = 0
};
this.tsprite.setYScroll(n_$0);
this.updateAttribute("yscroll", n_$0);
this.updateLineAttribute("scroll", -n_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_yscroll";
return $lzsc$temp
})(), "onyscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "scrollheight", 0, "$lzc$set_scrollheight", (function () {
var $lzsc$temp = function (x_$0) {
Debug.error("scrollheight is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_scrollheight";
return $lzsc$temp
})(), "onscrollheight", LzTextscrollEvent.LzDeclaredTextscrollEvent, "xscroll", 0, "$lzc$set_xscroll", (function () {
var $lzsc$temp = function (n_$0) {
if (n_$0 > 0) {
Debug.warn("Invalid value for %w.xscroll: %w", this, n_$0);
n_$0 = 0
};
this.tsprite.setXScroll(n_$0);
this.updateAttribute("xscroll", n_$0);
this.updateAttribute("hscroll", -n_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_xscroll";
return $lzsc$temp
})(), "onxscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "scrollwidth", 0, "$lzc$set_scrollwidth", (function () {
var $lzsc$temp = function (x_$0) {
Debug.error("scrollwidth is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_scrollwidth";
return $lzsc$temp
})(), "onscrollwidth", LzTextscrollEvent.LzDeclaredTextscrollEvent, "scroll", 1, "$lzc$set_scroll", (function () {
var $lzsc$temp = function (n_$0) {
if (n_$0 < 1 || n_$0 > this.maxscroll) {
Debug.warn("Invalid valuefor %w.scroll: %w (limits [1, %w])", this, n_$0, this.maxscroll);
n_$0 = n_$0 < 1 ? 1 : this.maxscroll
};
var pixel_$1;
if (this.capabilities.linescrolling) {
pixel_$1 = this.tsprite.lineNoToPixel(n_$0)
} else {
pixel_$1 = (n_$0 - 1) * this.lineheight
};
this.$lzc$set_yscroll(-pixel_$1)
};
$lzsc$temp["displayName"] = "$lzc$set_scroll";
return $lzsc$temp
})(), "onscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "maxscroll", 1, "$lzc$set_maxscroll", (function () {
var $lzsc$temp = function (x_$0) {
Debug.error("maxscroll is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_maxscroll";
return $lzsc$temp
})(), "onmaxscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "hscroll", 0, "$lzc$set_hscroll", (function () {
var $lzsc$temp = function (n_$0) {
if (n_$0 < 0 || n_$0 > this.maxhscroll) {
Debug.warn("Invalid value for %w.hscroll: %w (limits [0, %w])", this, n_$0, this.maxhscroll);
n_$0 = n_$0 < 1 ? 1 : this.maxhscroll
};
this.$lzc$set_xscroll(-n_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_hscroll";
return $lzsc$temp
})(), "onhscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "maxhscroll", 0, "$lzc$set_maxhscroll", (function () {
var $lzsc$temp = function (x_$0) {
Debug.error("maxhscroll is read-only")
};
$lzsc$temp["displayName"] = "$lzc$set_maxhscroll";
return $lzsc$temp
})(), "onmaxhscroll", LzTextscrollEvent.LzDeclaredTextscrollEvent, "scrollevent", (function () {
var $lzsc$temp = function (name_$0, value_$1) {
switch (name_$0) {
case "scrollTop":
this.updateAttribute("yscroll", -value_$1);this.updateLineAttribute("scroll", value_$1);break;;case "scrollLeft":
this.updateAttribute("xscroll", -value_$1);this.updateAttribute("hscroll", value_$1);break;;case "scrollHeight":
this.updateAttribute("scrollheight", value_$1);this.updateLineAttribute("maxscroll", Math.max(0, value_$1 - this.height));break;;case "scrollWidth":
this.updateAttribute("scrollwidth", value_$1);this.updateAttribute("maxhscroll", Math.max(0, value_$1 - this.width));break;;case "lineHeight":
this.updateAttribute("lineheight", value_$1);if (this.inited) {
this.updateLineAttribute("scroll", -this.yscroll)
}break;;default:
{
Debug.error("%w: Uknown scrollevent %s (%w)", arguments.callee, name_$0, value_$1)
}}};
$lzsc$temp["displayName"] = "scrollevent";
return $lzsc$temp
})(), "multiline", false, "$lzc$set_multiline", (function () {
var $lzsc$temp = function (ml_$0) {
ml_$0 = !(!ml_$0);
if (ml_$0 !== this.multiline) {
this.multiline = ml_$0;
this.tsprite.setMultiline(ml_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_multiline";
return $lzsc$temp
})(), "resize", true, "$lzc$set_resize", (function () {
var $lzsc$temp = function (val_$0) {
val_$0 = !(!val_$0);
if (val_$0 !== this.resize) {
this.resize = val_$0;
this.tsprite.setResize(val_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_resize";
return $lzsc$temp
})(), "text", "", "$lzc$set_text", (function () {
var $lzsc$temp = function (t_$0) {
t_$0 = String(t_$0);
if (t_$0 == this.getText()) {
if (this.ontext.ready) this.ontext.sendEvent(t_$0);
return
};
if (this.visible) this.tsprite.setVisible(this.visible);
if (t_$0.length > this.maxlength) {
t_$0 = t_$0.substring(0, this.maxlength)
};
this.tsprite.setText(t_$0);
this.text = t_$0;
if (this.ontext.ready) this.ontext.sendEvent(t_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_text";
return $lzsc$temp
})(), "_updateSize", (function () {
var $lzsc$temp = function () {
if (!this.isinited) {
return
};
if (this.width == 0 || this.resize && this.multiline == false) {
var w_$0 = this.tsprite.getTextWidth();
if (w_$0 != this.width) {
this.$lzc$set_width(w_$0)
}};
if (!this.hassetheight) {
var h_$1 = this.tsprite.getTextfieldHeight();
if (h_$1 > 0 && h_$1 != this.height) {
this.updateHeight(h_$1)
}}};
$lzsc$temp["displayName"] = "_updateSize";
return $lzsc$temp
})(), "ontext", LzDeclaredEvent, "ontextlink", LzDeclaredEvent, "maxlength", Infinity, "$lzc$set_maxlength", (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 == null) {
val_$0 = Infinity
};
if (this.maxlength === val_$0) return;
if (isNaN(val_$0)) {
Debug.warn("Invalid value for %w.maxlength: %w", this, val_$0);
return
};
this.maxlength = val_$0;
this.tsprite.setMaxLength(val_$0);
if (this.onmaxlength.ready) this.onmaxlength.sendEvent(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_maxlength";
return $lzsc$temp
})(), "onmaxlength", LzDeclaredEvent, "pattern", void 0, "$lzc$set_pattern", (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 == null || val_$0 == "") return;
this.pattern = val_$0;
this.tsprite.setPattern(val_$0);
if (this.onpattern.ready) this.onpattern.sendEvent(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_pattern";
return $lzsc$temp
})(), "onpattern", LzDeclaredEvent, "$lzc$set_fontstyle", (function () {
var $lzsc$temp = function (fstyle_$0) {
if (fstyle_$0 == "plain" || fstyle_$0 == "bold" || fstyle_$0 == "italic" || fstyle_$0 == "bolditalic" || fstyle_$0 == "bold italic") {
var oldval_$1 = this.fontstyle;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_fontstyle"] || this.nextMethod(arguments.callee, "$lzc$set_fontstyle")).call(this, fstyle_$0);
if (this.__keepconstraint != true && this["__cascadeDelfontstyle"]) {
this.__cascadeDelfontstyle.unregisterAll();
this.__cascadeDelfontstyle = null
};
if (oldval_$1 !== this.fontstyle) {
this.tsprite.setFontStyle(fstyle_$0)
}} else {
Debug.warn("invalid font style", fstyle_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_fontstyle";
return $lzsc$temp
})(), "$lzc$set_font", (function () {
var $lzsc$temp = function (fname_$0) {
var oldval_$1 = this.font;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_font"] || this.nextMethod(arguments.callee, "$lzc$set_font")).call(this, fname_$0);
if (this.__keepconstraint != true && this["__cascadeDelfont"]) {
this.__cascadeDelfont.unregisterAll();
this.__cascadeDelfont = null
};
if (oldval_$1 !== this.font) {
this.tsprite.setFontName(fname_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_font";
return $lzsc$temp
})(), "$lzc$set_fontsize", (function () {
var $lzsc$temp = function (fsize_$0) {
if (fsize_$0 <= 0 || isNaN(fsize_$0)) {
Debug.warn("invalid font size", fsize_$0)
} else {
var oldval_$1 = this.fontsize;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_fontsize"] || this.nextMethod(arguments.callee, "$lzc$set_fontsize")).call(this, fsize_$0);
if (this.__keepconstraint != true && this["__cascadeDelfontsize"]) {
this.__cascadeDelfontsize.unregisterAll();
this.__cascadeDelfontsize = null
};
if (oldval_$1 !== this.fontsize) {
this.tsprite.setFontSize(fsize_$0)
}}};
$lzsc$temp["displayName"] = "$lzc$set_fontsize";
return $lzsc$temp
})(), "$lzc$set_fgcolor", (function () {
var $lzsc$temp = function (color_$0) {
var oldval_$1 = this.fgcolor;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_fgcolor"] || this.nextMethod(arguments.callee, "$lzc$set_fgcolor")).call(this, color_$0);
if (this.__keepconstraint != true && this["__cascadeDelfgcolor"]) {
this.__cascadeDelfgcolor.unregisterAll();
this.__cascadeDelfgcolor = null
};
if (oldval_$1 !== this.fgcolor) {
this.tsprite.setTextColor(this.fgcolor)
}};
$lzsc$temp["displayName"] = "$lzc$set_fgcolor";
return $lzsc$temp
})(), "__keepconstraint", false, "$lzc$set_cascadedfontsize", (function () {
var $lzsc$temp = function (val_$0) {
this.__keepconstraint = true;
this.$lzc$set_fontsize(val_$0);
this.__keepconstraint = false
};
$lzsc$temp["displayName"] = "$lzc$set_cascadedfontsize";
return $lzsc$temp
})(), "$lzc$set_cascadedfont", (function () {
var $lzsc$temp = function (val_$0) {
this.__keepconstraint = true;
this.$lzc$set_font(val_$0);
this.__keepconstraint = false
};
$lzsc$temp["displayName"] = "$lzc$set_cascadedfont";
return $lzsc$temp
})(), "$lzc$set_cascadedfontstyle", (function () {
var $lzsc$temp = function (val_$0) {
this.__keepconstraint = true;
this.$lzc$set_fontstyle(val_$0);
this.__keepconstraint = false
};
$lzsc$temp["displayName"] = "$lzc$set_cascadedfontstyle";
return $lzsc$temp
})(), "$lzc$set_cascadedfgcolor", (function () {
var $lzsc$temp = function (val_$0) {
this.__keepconstraint = true;
this.$lzc$set_fgcolor(val_$0);
this.__keepconstraint = false
};
$lzsc$temp["displayName"] = "$lzc$set_cascadedfgcolor";
return $lzsc$temp
})(), "textalign", "left", "$lzc$set_textalign", (function () {
var $lzsc$temp = function (align_$0) {
align_$0 = align_$0 ? align_$0.toLowerCase() : "left";
if (!(align_$0 == "left" || align_$0 == "right" || align_$0 == "center" || align_$0 == "justify")) {
Debug.warn("Invalid value for %w.textalign: %w", this, align_$0);
align_$0 = "left"
};
this.textalign = align_$0;
this.tsprite.setTextAlign(align_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_textalign";
return $lzsc$temp
})(), "textindent", 0, "$lzc$set_textindent", (function () {
var $lzsc$temp = function (indent_$0) {
if (isNaN(indent_$0)) {
Debug.warn("invalid text indent", indent_$0)
} else {
this.textindent = indent_$0;
this.tsprite.setTextIndent(indent_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_textindent";
return $lzsc$temp
})(), "letterspacing", 0, "$lzc$set_letterspacing", (function () {
var $lzsc$temp = function (spacing_$0) {
if (isNaN(spacing_$0)) {
Debug.warn("invalid letter spacing", spacing_$0)
} else {
this.letterspacing = spacing_$0;
this.tsprite.setLetterSpacing(spacing_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_letterspacing";
return $lzsc$temp
})(), "textdecoration", "none", "$lzc$set_textdecoration", (function () {
var $lzsc$temp = function (decoration_$0) {
decoration_$0 = decoration_$0 ? decoration_$0.toLowerCase() : "none";
if (!(decoration_$0 == "none" || decoration_$0 == "underline")) {
Debug.warn("Invalid value for %w.textdecoration: %w", this, decoration_$0);
decoration_$0 = "none"
};
this.textdecoration = decoration_$0;
this.tsprite.setTextDecoration(decoration_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_textdecoration";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
this.hasdirectionallayout = ("hasdirectionallayout" in args_$1) ? args_$1.hasdirectionallayout : false;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
var cascadeattrs_$2 = {font: null, fontsize: null, fontstyle: null, fgcolor: null};
var initattrs_$3 = {hassetheight: this.hassetheight, height: this.height};
var searchattrs_$4 = [];
for (var attr_$5 in cascadeattrs_$2) {
if (attr_$5 in args_$1) {
this[attr_$5] = initattrs_$3[attr_$5] = args_$1[attr_$5]
} else {
searchattrs_$4.push(attr_$5)
}};
if (searchattrs_$4.length > 0) {
var parents_$6 = this.searchParentAttrs(searchattrs_$4);
for (var attr_$5 in parents_$6) {
var par_$7 = parents_$6[attr_$5];
this[attr_$5] = initattrs_$3[attr_$5] = par_$7[attr_$5];
this["__cascadeDel" + attr_$5] = new LzDelegate(this, "$lzc$set_cascaded" + attr_$5, par_$7, "on" + attr_$5)
}};
this.tsprite.__initTextProperties(initattrs_$3);
this.yscroll = 0;
this.xscroll = 0;
this.$lzc$set_resize(("resize" in args_$1) ? !(!args_$1.resize) : this.resize);
if (args_$1["maxlength"] != null) {
this.$lzc$set_maxlength(args_$1.maxlength)
};
this.text = args_$1["text"] != null ? String(args_$1.text) : "";
if (this.text.length > this.maxlength) {
this.text = this.text.substring(0, this.maxlength)
};
this.$lzc$set_multiline(("multiline" in args_$1) ? args_$1.multiline : this.multiline);
this.tsprite.setText(this.text);
if (!this.hassetwidth) {
if (this.multiline) {
args_$1.width = this.parent.width
} else {
if (this.text != null && this.text != "" && this.text.length > 0) {
args_$1.width = this.tsprite.getTextWidth()
} else {
args_$1.width = this.getDefaultWidth()
}}} else {
this.$lzc$set_resize(false)
};
if (this.hassetheight && ("height" in args_$1)) {
this.$lzc$set_height(args_$1.height)
};
if (args_$1["pattern"] != null) {
this.$lzc$set_pattern(args_$1.pattern)
};
if (this.capabilities.advancedfonts) {
if (!("antiAliasType" in args_$1)) {
this.$lzc$set_antiAliasType("advanced")
};
if (!("gridFit" in args_$1)) {
this.$lzc$set_gridFit("subpixel")
}};
if (LzSprite.quirks && LzSprite.quirks.textlinksneedmouseevents) {
this.ontextlink = LzTextlinkEvent.LzDeclaredTextlinkEvent
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
if (this.scrollevents && !this.clip) {
Debug.warn("You have set scrollevents to true on text view ", this, ", but you must also set clip='true' on this view to have scrolling work correctly")
};
this._updateSize()
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "tsprite", void 0, "__makeSprite", (function () {
var $lzsc$temp = function (args_$0) {
this.sprite = this.tsprite = new LzTextSprite(this, args_$0, this.hasdirectionallayout)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "setResize", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_resize(val_$0)
};
$lzsc$temp["displayName"] = "setResize";
return $lzsc$temp
})(), "addText", (function () {
var $lzsc$temp = function (t_$0) {
this.$lzc$set_text(this.getText() + t_$0)
};
$lzsc$temp["displayName"] = "addText";
return $lzsc$temp
})(), "clearText", (function () {
var $lzsc$temp = function () {
this.$lzc$set_text("")
};
$lzsc$temp["displayName"] = "clearText";
return $lzsc$temp
})(), "setMaxLength", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_maxlength(val_$0)
};
$lzsc$temp["displayName"] = "setMaxLength";
return $lzsc$temp
})(), "setPattern", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_pattern(val_$0)
};
$lzsc$temp["displayName"] = "setPattern";
return $lzsc$temp
})(), "getTextWidth", (function () {
var $lzsc$temp = function () {
return this.tsprite.getTextWidth(true)
};
$lzsc$temp["displayName"] = "getTextWidth";
return $lzsc$temp
})(), "$lzc$getTextWidth_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getTextWidth_dependencies";
return $lzsc$temp
})(), "getTextHeight", (function () {
var $lzsc$temp = function () {
return this.tsprite.getTextfieldHeight(true)
};
$lzsc$temp["displayName"] = "getTextHeight";
return $lzsc$temp
})(), "$lzc$getTextHeight_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getTextHeight_dependencies";
return $lzsc$temp
})(), "applyData", (function () {
var $lzsc$temp = function (d_$0) {
if (null == d_$0) {
this.clearText()
} else {
this.$lzc$set_text(d_$0)
}};
$lzsc$temp["displayName"] = "applyData";
return $lzsc$temp
})(), "setScroll", (function () {
var $lzsc$temp = function (h_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_scroll(h_$0)
};
$lzsc$temp["displayName"] = "setScroll";
return $lzsc$temp
})(), "getScroll", (function () {
var $lzsc$temp = function () {
return this.scroll
};
$lzsc$temp["displayName"] = "getScroll";
return $lzsc$temp
})(), "getMaxScroll", (function () {
var $lzsc$temp = function () {
return this.maxscroll
};
$lzsc$temp["displayName"] = "getMaxScroll";
return $lzsc$temp
})(), "$lzc$getMaxScroll_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "maxscroll"]
};
$lzsc$temp["displayName"] = "$lzc$getMaxScroll_dependencies";
return $lzsc$temp
})(), "getBottomScroll", (function () {
var $lzsc$temp = function () {
return this.scroll + this.height / this.lineheight
};
$lzsc$temp["displayName"] = "getBottomScroll";
return $lzsc$temp
})(), "setXScroll", (function () {
var $lzsc$temp = function (n_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_xscroll(n_$0)
};
$lzsc$temp["displayName"] = "setXScroll";
return $lzsc$temp
})(), "setYScroll", (function () {
var $lzsc$temp = function (n_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_yscroll(n_$0)
};
$lzsc$temp["displayName"] = "setYScroll";
return $lzsc$temp
})(), "setHScroll", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_hscroll(s_$0)
};
$lzsc$temp["displayName"] = "setHScroll";
return $lzsc$temp
})(), "annotateAAimg", (function () {
var $lzsc$temp = function (txt_$0) {
if (typeof txt_$0 == "undefined") {
return
};
if (txt_$0.length == 0) {
return
};
var ntxt_$1 = "";
var start_$2 = 0;
var end_$3 = 0;
var i_$4;
var IMGSTART_$5 = "<img ";
while (true) {
i_$4 = txt_$0.indexOf(IMGSTART_$5, start_$2);
if (i_$4 < 0) {
ntxt_$1 += txt_$0.substring(start_$2);
break
};
ntxt_$1 += txt_$0.substring(start_$2, i_$4 + IMGSTART_$5.length);
start_$2 = i_$4 + IMGSTART_$5.length;
var attrs_$6 = {};
end_$3 = start_$2 + this.parseImgAttributes(attrs_$6, txt_$0.substring(start_$2));
ntxt_$1 += txt_$0.substring(start_$2, end_$3 + 1);
if (attrs_$6["alt"] != null) {
var altval_$7 = attrs_$6["alt"];
ntxt_$1 += "[image " + altval_$7 + "]"
};
start_$2 = end_$3 + 1
};
return ntxt_$1
};
$lzsc$temp["displayName"] = "annotateAAimg";
return $lzsc$temp
})(), "parseImgAttributes", (function () {
var $lzsc$temp = function (attrs_$0, str_$1) {
var i_$2;
var end_$3 = 0;
var ATTNAME_$4 = "attrname";
var ATTVAL_$5 = "attrval";
var WHITESPACE_$6 = "whitespace";
var WHITESPACE2_$7 = "whitespace2";
var mode_$8 = WHITESPACE_$6;
var smax_$9 = str_$1.length;
var attrname_$a;
var attrval_$b;
var delimiter_$c;
for (i_$2 = 0;i_$2 < smax_$9;i_$2++) {
end_$3 = i_$2;
var c_$d = str_$1.charAt(i_$2);
if (c_$d == ">") {
break
};
if (mode_$8 == WHITESPACE_$6) {
if (c_$d != " ") {
mode_$8 = ATTNAME_$4;
attrname_$a = c_$d
}} else if (mode_$8 == ATTNAME_$4) {
if (c_$d == " " || c_$d == "=") {
mode_$8 = WHITESPACE2_$7
} else {
attrname_$a += c_$d
}} else if (mode_$8 == WHITESPACE2_$7) {
if (c_$d == " " || c_$d == "=") {
continue
} else {
mode_$8 = ATTVAL_$5;
delimiter_$c = c_$d;
attrval_$b = ""
}} else if (mode_$8 == ATTVAL_$5) {
if (c_$d != delimiter_$c) {
attrval_$b += c_$d
} else {
mode_$8 = WHITESPACE_$6;
attrs_$0[attrname_$a] = attrval_$b
}}};
return end_$3
};
$lzsc$temp["displayName"] = "parseImgAttributes";
return $lzsc$temp
})(), "setText", (function () {
var $lzsc$temp = function (t_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_text(t_$0)
};
$lzsc$temp["displayName"] = "setText";
return $lzsc$temp
})(), "format", (function () {
var $lzsc$temp = function (control_$0) {
var args_$1 = Array.prototype.slice.call(arguments, 1);
this.$lzc$set_text(this.formatToString.apply(this, [control_$0].concat(args_$1)).toString().toHTML())
};
$lzsc$temp["displayName"] = "format";
return $lzsc$temp
})(), "addFormat", (function () {
var $lzsc$temp = function (control_$0) {
var args_$1 = Array.prototype.slice.call(arguments, 1);
this.$lzc$set_text(this.getText() + this.formatToString.apply(this, [control_$0].concat(args_$1)).toString().toHTML())
};
$lzsc$temp["displayName"] = "addFormat";
return $lzsc$temp
})(), "getText", (function () {
var $lzsc$temp = function () {
return this.text
};
$lzsc$temp["displayName"] = "getText";
return $lzsc$temp
})(), "$lzc$getText_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "text"]
};
$lzsc$temp["displayName"] = "$lzc$getText_dependencies";
return $lzsc$temp
})(), "escapeText", (function () {
var $lzsc$temp = function (ts_$0) {
var t_$1 = ts_$0 == null ? this.text : ts_$0;
var i_$2;
for (var ec_$3 in LzText.escapeChars) {
while (t_$1.indexOf(ec_$3) > -1) {
i_$2 = t_$1.indexOf(ec_$3);
t_$1 = t_$1.substring(0, i_$2) + LzText.escapeChars[ec_$3] + t_$1.substring(i_$2 + 1)
}};
return t_$1
};
$lzsc$temp["displayName"] = "escapeText";
return $lzsc$temp
})(), "setSelectable", (function () {
var $lzsc$temp = function (isSel_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_selectable(isSel_$0)
};
$lzsc$temp["displayName"] = "setSelectable";
return $lzsc$temp
})(), "setFontSize", (function () {
var $lzsc$temp = function (fsize_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fontsize(fsize_$0)
};
$lzsc$temp["displayName"] = "setFontSize";
return $lzsc$temp
})(), "setFontStyle", (function () {
var $lzsc$temp = function (fstyle_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_fontstyle(fstyle_$0)
};
$lzsc$temp["displayName"] = "setFontStyle";
return $lzsc$temp
})(), "setMultiline", (function () {
var $lzsc$temp = function (ml_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_multiline(ml_$0)
};
$lzsc$temp["displayName"] = "setMultiline";
return $lzsc$temp
})(), "setBorder", (function () {
var $lzsc$temp = function (onroff_$0) {
this.tsprite.setBorder(onroff_$0)
};
$lzsc$temp["displayName"] = "setBorder";
return $lzsc$temp
})(), "setWordWrap", (function () {
var $lzsc$temp = function (wrap_$0) {
this.tsprite.setWordWrap(wrap_$0)
};
$lzsc$temp["displayName"] = "setWordWrap";
return $lzsc$temp
})(), "setEmbedFonts", (function () {
var $lzsc$temp = function (onroff_$0) {
this.tsprite.setEmbedFonts(onroff_$0)
};
$lzsc$temp["displayName"] = "setEmbedFonts";
return $lzsc$temp
})(), "setAntiAliasType", (function () {
var $lzsc$temp = function (aliasType_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_antiAliasType(aliasType_$0)
};
$lzsc$temp["displayName"] = "setAntiAliasType";
return $lzsc$temp
})(), "getAntiAliasType", (function () {
var $lzsc$temp = function () {
if (this.capabilities.advancedfonts) {
return this.antiAliasType
} else {
LzView.__warnCapability("text.getAntiAliasType()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getAntiAliasType";
return $lzsc$temp
})(), "setGridFit", (function () {
var $lzsc$temp = function (gridFit_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_gridFit(gridFit_$0)
};
$lzsc$temp["displayName"] = "setGridFit";
return $lzsc$temp
})(), "getGridFit", (function () {
var $lzsc$temp = function () {
if (this.capabilities.advancedfonts) {
return this.gridFit
} else {
LzView.__warnCapability("text.getGridFit()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getGridFit";
return $lzsc$temp
})(), "setSharpness", (function () {
var $lzsc$temp = function (sharpness_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_sharpness(sharpness_$0)
};
$lzsc$temp["displayName"] = "setSharpness";
return $lzsc$temp
})(), "getSharpness", (function () {
var $lzsc$temp = function () {
if (this.capabilities.advancedfonts) {
return this.sharpness
} else {
LzView.__warnCapability("text.getSharpness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getSharpness";
return $lzsc$temp
})(), "setThickness", (function () {
var $lzsc$temp = function (thickness_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_thickness(thickness_$0)
};
$lzsc$temp["displayName"] = "setThickness";
return $lzsc$temp
})(), "getThickness", (function () {
var $lzsc$temp = function () {
if (this.capabilities.advancedfonts) {
return this.thickness
} else {
LzView.__warnCapability("text.getThickness()", "advancedfonts")
}};
$lzsc$temp["displayName"] = "getThickness";
return $lzsc$temp
})(), "setSelection", (function () {
var $lzsc$temp = function (start_$0, end_$1) {
switch (arguments.length) {
case 1:
end_$1 = null
};
if (end_$1 == null) {
end_$1 = start_$0
};
this.tsprite.setSelection(start_$0, end_$1)
};
$lzsc$temp["displayName"] = "setSelection";
return $lzsc$temp
})(), "getSelectionPosition", (function () {
var $lzsc$temp = function () {
return this.tsprite.getSelectionPosition()
};
$lzsc$temp["displayName"] = "getSelectionPosition";
return $lzsc$temp
})(), "getSelectionSize", (function () {
var $lzsc$temp = function () {
return this.tsprite.getSelectionSize()
};
$lzsc$temp["displayName"] = "getSelectionSize";
return $lzsc$temp
})(), "makeTextLink", (function () {
var $lzsc$temp = function (str_$0, value_$1) {
return this.tsprite.makeTextLink(str_$0, value_$1)
};
$lzsc$temp["displayName"] = "makeTextLink";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "LzText: " + this.getText()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], [LzFormatter, LzView], ["tagname", "text", "attributes", new LzInheritedHash(LzView.attributes), "escapeChars", {">": "&gt;", "<": "&lt;"}]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzText.attributes.pixellock = true;
LzText.attributes.selectable = false;
LzText.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
var id_$0 = LzView.prototype._dbg_name.call(this);
if (id_$0 != this.toString()) {
return id_$0
} else {
var contents_$1 = this.getText();
if (contents_$1) {
return Debug.stringEscape(contents_$1, true)
}}};
$lzsc$temp["displayName"] = "views/LzText.lzs#1786/34";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "views/LzText.lzs#193/1";
return $lzsc$temp
})()(LzText);
lz[LzText.tagname] = LzText;
Class.make("LzInputText", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "password", void 0, "onenabled", LzDeclaredEvent, "getDefaultWidth", (function () {
var $lzsc$temp = function () {
return 100
};
$lzsc$temp["displayName"] = "getDefaultWidth";
return $lzsc$temp
})(), "_onfocusDel", null, "_onblurDel", null, "_modemanagerDel", null, "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
this.password = ("password" in args_$1) ? !(!args_$1.password) : false;
this.focusable = true;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
this.$lzc$set_resize(("resize" in args_$1) ? !(!args_$1.resize) : false);
this._onfocusDel = new LzDelegate(this, "_gotFocusEvent", this, "onfocus");
this._onblurDel = new LzDelegate(this, "_gotBlurEvent", this, "onblur");
this._modemanagerDel = new LzDelegate(this, "_modechanged", lz.ModeManager, "onmode")
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "isprite", void 0, "__makeSprite", (function () {
var $lzsc$temp = function (args_$0) {
this.sprite = this.tsprite = this.isprite = new LzInputTextSprite(this, args_$0, this.hasdirectionallayout)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "_focused", false, "_gotFocusEvent", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
this._focused = true;
this.isprite.gotFocus()
};
$lzsc$temp["displayName"] = "_gotFocusEvent";
return $lzsc$temp
})(), "_gotBlurEvent", (function () {
var $lzsc$temp = function (e_$0) {
switch (arguments.length) {
case 0:
e_$0 = null
};
this._focused = false;
this.isprite.gotBlur()
};
$lzsc$temp["displayName"] = "_gotBlurEvent";
return $lzsc$temp
})(), "inputtextevent", (function () {
var $lzsc$temp = function (eventname_$0, value_$1) {
switch (arguments.length) {
case 1:
value_$1 = null
};
if (eventname_$0 == "onfocus" && this._focused) return;
if (eventname_$0 == "onblur" && !this._focused) return;
if (eventname_$0 == "onfocus") {
this._focused = true;
if (lz.Focus.getFocus() !== this) {
var tabdown_$2 = lz.Keys.isKeyDown("tab");
lz.Focus.setFocus(this, tabdown_$2);
if (lz.Focus.getFocus() !== this && this._focused) {
this._gotBlurEvent()
}}} else if (eventname_$0 == "onchange") {
this.text = this.isprite.getText();
if (this.multiline && !this.hassetheight) {
var textheight_$3 = this.isprite.getTextfieldHeight();
if (this.height != textheight_$3) {
this.updateHeight(textheight_$3)
}};
if (this.ontext.ready) this.ontext.sendEvent(this.text)
} else if (eventname_$0 == "onblur") {
this._focused = false;
if (lz.Focus.getFocus() === this) {
lz.Focus.clearFocus()
}} else {
Debug.warn("unhandled inputtextevent='%s' in %#w", eventname_$0, this)
}};
$lzsc$temp["displayName"] = "inputtextevent";
return $lzsc$temp
})(), "updateData", (function () {
var $lzsc$temp = function () {
return this.isprite.getText()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "enabled", true, "$lzc$set_enabled", (function () {
var $lzsc$temp = function (enabled_$0) {
this.focusable = true;
this.enabled = enabled_$0;
this.isprite.setEnabled(enabled_$0);
if (this.onenabled.ready) this.onenabled.sendEvent(enabled_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_enabled";
return $lzsc$temp
})(), "setEnabled", (function () {
var $lzsc$temp = function (enabled_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_enabled(enabled_$0)
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setHTML", (function () {
var $lzsc$temp = function (htmlp_$0) {
if (this.capabilities["htmlinputtext"]) {
this.isprite.setHTML(htmlp_$0)
} else {
LzView.__warnCapability("inputtext.setHTML()", "htmlinputtext")
}};
$lzsc$temp["displayName"] = "setHTML";
return $lzsc$temp
})(), "getText", (function () {
var $lzsc$temp = function () {
if (this.isprite != null) {
return this.isprite.getText()
} else {
return ""
}};
$lzsc$temp["displayName"] = "getText";
return $lzsc$temp
})(), "_allowselectable", true, "_selectable", void 0, "_modechanged", (function () {
var $lzsc$temp = function (modalview_$0) {
this._setallowselectable(!modalview_$0 || lz.ModeManager.__LZallowInput(modalview_$0, this))
};
$lzsc$temp["displayName"] = "_modechanged";
return $lzsc$temp
})(), "_setallowselectable", (function () {
var $lzsc$temp = function (value_$0) {
this._allowselectable = value_$0;
this.$lzc$set_selectable(this._selectable)
};
$lzsc$temp["displayName"] = "_setallowselectable";
return $lzsc$temp
})(), "$lzc$set_selectable", (function () {
var $lzsc$temp = function (value_$0) {
this._selectable = value_$0;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_selectable"] || this.nextMethod(arguments.callee, "$lzc$set_selectable")).call(this, this._allowselectable ? value_$0 : false)
};
$lzsc$temp["displayName"] = "$lzc$set_selectable";
return $lzsc$temp
})()], LzText, ["tagname", "inputtext", "attributes", new LzInheritedHash(LzText.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.mergeAttributes({selectable: true, enabled: true, clip: true}, LzInputText.attributes)
}}};
$lzsc$temp["displayName"] = "views/LzInputText.lzs#59/1";
return $lzsc$temp
})()(LzInputText);
lz[LzInputText.tagname] = LzInputText;
Class.make("LzViewLinkage", ["xscale", 1, "yscale", 1, "xoffset", 0, "yoffset", 0, "uplinkArray", null, "downlinkArray", null, "fromView", void 0, "toView", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (fromView_$0, toView_$1) {
this.fromView = fromView_$0;
this.toView = toView_$1;
if (fromView_$0 === toView_$1) {
return
};
var uplinkArray_$2 = this.uplinkArray = [];
var pview_$3 = fromView_$0;
do {
pview_$3 = pview_$3.immediateparent;
uplinkArray_$2.push(pview_$3)
} while (pview_$3 !== canvas);
var downlinkArray_$4 = this.downlinkArray = [];
var pview_$3 = toView_$1;
do {
pview_$3 = pview_$3.immediateparent;
downlinkArray_$4.push(pview_$3)
} while (pview_$3 !== canvas);
while (uplinkArray_$2.length > 1 && downlinkArray_$4[downlinkArray_$4.length - 1] === uplinkArray_$2[uplinkArray_$2.length - 1] && downlinkArray_$4[downlinkArray_$4.length - 2] === uplinkArray_$2[uplinkArray_$2.length - 2]) {
downlinkArray_$4.pop();
uplinkArray_$2.pop()
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "update", (function () {
var $lzsc$temp = function (xory_$0) {
var scale_$1 = xory_$0 + "scale";
var offset_$2 = xory_$0 + "offset";
var uplinkArray_$3 = this.uplinkArray;
var downlinkArray_$4 = this.downlinkArray;
var upscale_$5 = 1;
var upoffset_$6 = 0;
if (uplinkArray_$3) {
var i_$7 = uplinkArray_$3.length - 1;
var v_$8 = uplinkArray_$3[i_$7--];
upscale_$5 *= v_$8[scale_$1];
for (;i_$7 >= 0;i_$7--) {
v_$8 = uplinkArray_$3[i_$7];
upoffset_$6 += v_$8[xory_$0] * upscale_$5;
upscale_$5 *= v_$8[scale_$1]
}};
var downscale_$9 = 1;
var downoffset_$a = 0;
if (downlinkArray_$4) {
var i_$7 = downlinkArray_$4.length - 1;
var v_$8 = downlinkArray_$4[i_$7--];
downscale_$9 *= v_$8[scale_$1];
for (;i_$7 >= 0;i_$7--) {
v_$8 = downlinkArray_$4[i_$7];
downoffset_$a += v_$8[xory_$0] * downscale_$9;
downscale_$9 *= v_$8[scale_$1]
}};
this[scale_$1] = upscale_$5 / downscale_$9;
this[offset_$2] = (upoffset_$6 - downoffset_$a) / downscale_$9
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzViewLinkage.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
this.update("x");
this.update("y");
return Debug.formatToString("%w -> %w: [%d 0 %d 0 %d %d 0 0 1]", this.fromView, this.toView, this.xscale, this.xoffset, this.yscale, this.yoffset)
};
$lzsc$temp["displayName"] = "views/LzViewLinkage.lzs#177/41";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "views/LzViewLinkage.lzs#40/1";
return $lzsc$temp
})()(LzViewLinkage);
Class.make("LzCanvas", ["updatePercentCreatedEnabled", true, "_lzinitialsubviews", [], "totalnodes", void 0, "framerate", 30, "onframerate", LzDeclaredEvent, "creatednodes", void 0, "__LZproxied", void 0, "embedfonts", void 0, "lpsbuild", void 0, "lpsbuilddate", void 0, "appbuilddate", void 0, "runtime", void 0, "allowfullscreen", void 0, "fullscreen", void 0, "onfullscreen", LzDeclaredEvent, "__LZmouseupDel", void 0, "__LZmousedownDel", void 0, "__LZmousemoveDel", void 0, "__LZDefaultCanvasMenu", void 0, "httpdataprovider", null, "defaultdataprovider", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, args_$1, children_$2, async_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
args_$1 = null;;case 2:
children_$2 = null;;case 3:
async_$3 = null
};
if (!args_$1["medialoadtimeout"]) args_$1.medialoadtimeout = this.medialoadtimeout;
if (!args_$1["mediaerrortimeout"]) args_$1.mediaerrortimeout = this.mediaerrortimeout;
if (args_$1["fontsize"] != null) {
this.fontsize = LzCanvas.attributes.fontsize.fallback = args_$1.fontsize;
delete args_$1.fontsize
};
if (args_$1["fontstyle"] != null) {
this.fontstyle = LzCanvas.attributes.fontstyle.fallback = args_$1.fontstyle;
delete args_$1.fontstyle
};
if (args_$1["font"] != null) {
this.font = LzCanvas.attributes.font.fallback = args_$1.font;
delete args_$1.font
};
if (args_$1["bgcolor"] != null) {
LzCanvas.attributes.bgcolor.fallback = args_$1.bgcolor;
delete args_$1.bgcolor
};
if (args_$1["width"] != null) {
LzCanvas.attributes.width.fallback = args_$1.width;
delete args_$1.width
};
if (args_$1["height"] != null) {
LzCanvas.attributes.height.fallback = args_$1.height;
delete args_$1.height
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, args_$1, children_$2, async_$3);
if (this.fgcolor == null) this.fgcolor = 0;
if (this.fontsize == null) this.fontsize = 11;
this.datasets = {};
this.__LZcheckwidth = null;
this.__LZcheckheight = null;
this.hassetwidth = true;
this.hassetheight = true
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args) {
var getQueryArg_$2;
getQueryArg_$2 = (function () {
var $lzsc$temp = function (name_$0, initname_$1) {
var arg_$2 = args[name_$0];
delete args[name_$0];
if (arg_$2 != null) {
return !(!arg_$2)
} else if (initname_$1 != null) {
var initarg_$3 = lz.Browser.getInitArg(initname_$1);
if (initarg_$3 != null) {
return initarg_$3 == "true"
}};
return void 0
};
$lzsc$temp["displayName"] = "getQueryArg";
return $lzsc$temp
})();
this.__makeSprite(null);
var capabilities_$1 = this.sprite.capabilities;
this.capabilities = capabilities_$1;
this.immediateparent = this;
this.datapath = new LzDatapath(this);
this.mask = null;
this.accessible = getQueryArg_$2("accessible", null);
if (capabilities_$1.accessibility == true) {
this.sprite.setAccessible(this.accessible);
if (this.accessible) {
this.sprite.setAAActive(true);
this.sprite.setAASilent(false)
}} else if (this.accessible) {
Debug.warn("This runtime doesn't support accessibility.");
this.accessible = false
};
this.history = getQueryArg_$2("history", "history");
if (this.history && capabilities_$1.history != true) {
Debug.warn("This runtime doesn't support history.");
this.history = false
};
this.allowfullscreen = getQueryArg_$2("allowfullscreen", "allowfullscreen");
if (this.allowfullscreen && capabilities_$1.allowfullscreen != true) {
Debug.warn("This runtime doesn't support full screen mode.");
this.allowfullscreen = false
};
this.fullscreen = false;
this.viewLevel = 0;
this.totalnodes = 0;
this.creatednodes = 0;
this.percentcreated = 0;
if (!args.framerate) {
args.framerate = 30
};
this.proxied = getQueryArg_$2("proxied", "lzproxied");
if (this.proxied == null) {
this.proxied = args.__LZproxied == "true"
};
if (typeof args.proxyurl == "undefined") {
this.proxyurl = lz.Browser.getBaseURL().toString()
};
if (args.focustrap != null) {
if (capabilities_$1.globalfocustrap != true) {
delete args.focustrap
}};
LzScreenKernel.setCallback(this, "__windowResize");
delete args.width;
delete args.height;
if (capabilities_$1.allowfullscreen == true) {
LzScreenKernel.setFullscreenCallback(this, "__fullscreenEventCallback", "__fullscreenErrorCallback")
};
this.lpsversion = args.lpsversion + "." + this.__LZlfcversion;
delete args.lpsversion;
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
})(), "__LZmouseup", (function () {
var $lzsc$temp = function (e_$0) {
if (this.onmouseup.ready) this.onmouseup.sendEvent()
};
$lzsc$temp["displayName"] = "__LZmouseup";
return $lzsc$temp
})(), "__LZmousemove", (function () {
var $lzsc$temp = function (e_$0) {
if (this.onmousemove.ready) this.onmousemove.sendEvent()
};
$lzsc$temp["displayName"] = "__LZmousemove";
return $lzsc$temp
})(), "__LZmousedown", (function () {
var $lzsc$temp = function (e_$0) {
if (this.onmousedown.ready) this.onmousedown.sendEvent()
};
$lzsc$temp["displayName"] = "__LZmousedown";
return $lzsc$temp
})(), "__makeSprite", (function () {
var $lzsc$temp = function (args_$0) {
this.sprite = new LzSprite(this, true)
};
$lzsc$temp["displayName"] = "__makeSprite";
return $lzsc$temp
})(), "onmouseleave", LzDeclaredEvent, "onmouseenter", LzDeclaredEvent, "onpercentcreated", LzDeclaredEvent, "onmousemove", LzDeclaredEvent, "onafterinit", LzDeclaredEvent, "lpsversion", void 0, "lpsrelease", void 0, "version", null, "scriptlimits", void 0, "__LZlfcversion", "0", "proxied", true, "dataloadtimeout", 30000, "medialoadtimeout", 30000, "$lzc$set_medialoadtimeout", (function () {
var $lzsc$temp = function (ms_$0) {
if (this.capabilities["medialoading"]) {
LzSprite.setMediaLoadTimeout(ms_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_medialoadtimeout";
return $lzsc$temp
})(), "mediaerrortimeout", 4500, "$lzc$set_mediaerrortimeout", (function () {
var $lzsc$temp = function (ms_$0) {
if (this.capabilities["medialoading"]) {
LzSprite.setMediaErrorTimeout(ms_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_mediaerrortimeout";
return $lzsc$temp
})(), "percentcreated", void 0, "datasets", null, "compareVersion", (function () {
var $lzsc$temp = function (ver_$0, over_$1) {
switch (arguments.length) {
case 1:
over_$1 = null
};
if (over_$1 == null) {
over_$1 = this.lpsversion
};
if (ver_$0 == over_$1) return 0;
var ver1_$2 = ver_$0.split(".");
var ver2_$3 = over_$1.split(".");
var i_$4 = 0;
while (i_$4 < ver1_$2.length || i_$4 < ver2_$3.length) {
var my_$5 = Number(ver1_$2[i_$4]) || 0;
var oth_$6 = Number(ver2_$3[i_$4++]) || 0;
if (my_$5 < oth_$6) {
return -1
} else if (my_$5 > oth_$6) {
return 1
}};
return 0
};
$lzsc$temp["displayName"] = "compareVersion";
return $lzsc$temp
})(), "$lzc$set_resource", (function () {
var $lzsc$temp = function (v_$0) {
Debug.error("You cannot set a resource on the canvas.")
};
$lzsc$temp["displayName"] = "$lzc$set_resource";
return $lzsc$temp
})(), "$lzc$set_focustrap", (function () {
var $lzsc$temp = function (istrapped_$0) {
lz.Keys.setGlobalFocusTrap(istrapped_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_focustrap";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "This is the canvas"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "$lzc$set_framerate", (function () {
var $lzsc$temp = function (fps_$0) {
fps_$0 *= 1;
if (fps_$0 < 1) {
fps_$0 = 1
} else if (fps_$0 > 1000) {
fps_$0 = 1000
};
this.framerate = fps_$0;
lz.Idle.setFrameRate(fps_$0);
if (this.onframerate.ready) this.onframerate.sendEvent(fps_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_framerate";
return $lzsc$temp
})(), "$lzc$set_fullscreen", (function () {
var $lzsc$temp = function (fullscreen_$0) {
switch (arguments.length) {
case 0:
fullscreen_$0 = true
};
if (this.capabilities.allowfullscreen == true) {
LzScreenKernel.showFullScreen(fullscreen_$0)
} else {
LzView.__warnCapability("canvas.setAttribute('fullscreen', " + fullscreen_$0 + ")")
}};
$lzsc$temp["displayName"] = "$lzc$set_fullscreen";
return $lzsc$temp
})(), "__fullscreenEventCallback", (function () {
var $lzsc$temp = function (result_$0, isFullscreen_$1) {
this.fullscreen = isFullscreen_$1;
if (this.onfullscreen.ready) this.onfullscreen.sendEvent(result_$0)
};
$lzsc$temp["displayName"] = "__fullscreenEventCallback";
return $lzsc$temp
})(), "__fullscreenErrorCallback", (function () {
var $lzsc$temp = function (errorMessage_$0) {
if (this.allowfullscreen == false) {
Debug.error('Please set <canvas allowfullscreen="true" /> for fullscreen support')
} else {
var message_$1 = "Usage of fullscreen feature is supported starting with Flash Player 9.0.28 (Windows & OS X) and 9.0.115.0 (Linux).\n" + "You are currently using Flash Player " + lz.Browser.getVersion();
if (errorMessage_$0 != null) {
message_$1 = "Flash Player Security Error " + errorMessage_$0 + "\n" + message_$1
};
Debug.error(message_$1);
Debug.error("Check your SWF embed code for missing param tag " + '<param name="allowFullScreen" value="true" /> inside the <object> tag,\n or ' + 'missing attribute allowFullScreen="true" in <embed> tag.\n' + 'canvas.setAttribute("fullscreen", true) can be called only in response to a mouse click or keypress.\n')
}};
$lzsc$temp["displayName"] = "__fullscreenErrorCallback";
return $lzsc$temp
})(), "$lzc$set_allowfullscreen", (function () {
var $lzsc$temp = function (fs_$0) {
this.allowfullscreen = fs_$0
};
$lzsc$temp["displayName"] = "$lzc$set_allowfullscreen";
return $lzsc$temp
})(), "initDone", (function () {
var $lzsc$temp = function () {
var sva_$0 = [];
var svb_$1 = [];
var isv_$2 = this._lzinitialsubviews;
for (var i_$3 = 0, len_$4 = isv_$2.length;i_$3 < len_$4;++i_$3) {
var isi_$5 = isv_$2[i_$3];
if (isi_$5["attrs"] && isi_$5.attrs["initimmediate"]) {
sva_$0.push(isi_$5)
} else {
svb_$1.push(isi_$5)
}};
sva_$0.push.apply(sva_$0, svb_$1);
this._lzinitialsubviews = [];
lz.Instantiator.requestInstantiation(this, sva_$0)
};
$lzsc$temp["displayName"] = "initDone";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
this.sprite.init(true);
if (this.history == true) {
lz.History.__start(this.id)
};
if (this.contextmenu == null) {
this.buildDefaultMenu()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "deferInit", true, "__LZinstantiationDone", (function () {
var $lzsc$temp = function () {
this.__LZinstantiated = true;
if (this.deferInit) {
this.deferInit = false;
return
};
this.percentcreated = 1;
this.updatePercentCreatedEnabled = false;
if (this.onpercentcreated.ready) this.onpercentcreated.sendEvent(this.percentcreated);
lz.Instantiator.resume();
this.__LZcallInit()
};
$lzsc$temp["displayName"] = "__LZinstantiationDone";
return $lzsc$temp
})(), "updatePercentCreated", (function () {
var $lzsc$temp = function () {
this.percentcreated = Math.max(this.percentcreated, this.creatednodes / this.totalnodes);
this.percentcreated = Math.min(0.99, this.percentcreated);
if (this.onpercentcreated.ready) this.onpercentcreated.sendEvent(this.percentcreated)
};
$lzsc$temp["displayName"] = "updatePercentCreated";
return $lzsc$temp
})(), "initiatorAddNode", (function () {
var $lzsc$temp = function (e_$0, n_$1) {
this.totalnodes += n_$1;
this._lzinitialsubviews.push(e_$0)
};
$lzsc$temp["displayName"] = "initiatorAddNode";
return $lzsc$temp
})(), "__LZcallInit", (function () {
var $lzsc$temp = function (an_$0) {
switch (arguments.length) {
case 0:
an_$0 = null
};
if (this.isinited) return;
this.isinited = true;
if (this.__LZresolveDict != null) this.__LZresolveReferences();
var sl_$1 = this.subnodes;
if (sl_$1) {
for (var i_$2 = 0;i_$2 < sl_$1.length;) {
var s_$3 = sl_$1[i_$2++];
var t_$4 = sl_$1[i_$2];
if (s_$3.isinited || !s_$3.__LZinstantiated) continue;
s_$3.__LZcallInit();
if (t_$4 != sl_$1[i_$2]) {
while (i_$2 > 0) {
if (t_$4 == sl_$1[--i_$2]) break
}}}};
if (this.__LZsourceLocation) {
LzNode.sourceLocatorTable[this.__LZsourceLocation] = this
};
this.init();
if (this.oninit.ready) this.oninit.sendEvent(this);
if (this.onafterinit.ready) this.onafterinit.sendEvent(this);
if (this.datapath && this.datapath.__LZApplyDataOnInit) {
this.datapath.__LZApplyDataOnInit()
};
this.inited = true;
if (this.oninited.ready) {
this.oninited.sendEvent(true)
}};
$lzsc$temp["displayName"] = "__LZcallInit";
return $lzsc$temp
})(), "isProxied", (function () {
var $lzsc$temp = function () {
return this.proxied
};
$lzsc$temp["displayName"] = "isProxied";
return $lzsc$temp
})(), "$lzc$set_width", (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.setRootWidth(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_width";
return $lzsc$temp
})(), "$lzc$set_x", (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.setRootX(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_x";
return $lzsc$temp
})(), "$lzc$set_height", (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.setRootHeight(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_height";
return $lzsc$temp
})(), "$lzc$set_y", (function () {
var $lzsc$temp = function (v_$0) {
LzSprite.setRootY(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_y";
return $lzsc$temp
})(), "setDefaultContextMenu", (function () {
var $lzsc$temp = function (cmenu_$0) {
this.$lzc$set_contextmenu(cmenu_$0);
this.sprite.setDefaultContextMenu(cmenu_$0)
};
$lzsc$temp["displayName"] = "setDefaultContextMenu";
return $lzsc$temp
})(), "buildDefaultMenu", (function () {
var $lzsc$temp = function () {
if (!this.capabilities.customcontextmenu) return;
this.__LZDefaultCanvasMenu = new LzContextMenu();
this.__LZDefaultCanvasMenu.hideBuiltInItems();
var defaultMenuItem_$0 = new LzContextMenuItem("About OpenLaszlo...", new LzDelegate(this, "__LZdefaultMenuItemHandler"));
this.__LZDefaultCanvasMenu.addItem(defaultMenuItem_$0);
if (this.proxied) {
var viewSourceMenuItem_$1 = new LzContextMenuItem("View Source", new LzDelegate(this, "__LZviewSourceMenuItemHandler"));
this.__LZDefaultCanvasMenu.addItem(viewSourceMenuItem_$1)
};
this.setDefaultContextMenu(this.__LZDefaultCanvasMenu)
};
$lzsc$temp["displayName"] = "buildDefaultMenu";
return $lzsc$temp
})(), "__LZdefaultMenuItemHandler", (function () {
var $lzsc$temp = function (item_$0) {
lz.Browser.loadURL("http://www.openlaszlo.org", "lz_about")
};
$lzsc$temp["displayName"] = "__LZdefaultMenuItemHandler";
return $lzsc$temp
})(), "__LZviewSourceMenuItemHandler", (function () {
var $lzsc$temp = function (item_$0) {
var url_$1 = lz.Browser.getBaseURL() + "?lzt=source";
lz.Browser.loadURL(url_$1, "lz_source")
};
$lzsc$temp["displayName"] = "__LZviewSourceMenuItemHandler";
return $lzsc$temp
})(), "__windowResize", (function () {
var $lzsc$temp = function (size_$0) {
this.width = size_$0.width;
if (this.onwidth.ready) this.onwidth.sendEvent(this.width);
this.sprite.setWidth(this.width);
this.height = size_$0.height;
if (this.onheight.ready) this.onheight.sendEvent(this.height);
this.sprite.setHeight(this.height)
};
$lzsc$temp["displayName"] = "__windowResize";
return $lzsc$temp
})(), "LzInstantiateView", (function () {
var $lzsc$temp = function (e_$0, tn_$1) {
switch (arguments.length) {
case 1:
tn_$1 = 1
};
canvas.initiatorAddNode(e_$0, tn_$1)
};
$lzsc$temp["displayName"] = "LzInstantiateView";
return $lzsc$temp
})(), "lzAddLocalData", (function () {
var $lzsc$temp = function (name_$0, d_$1, trimwhitespace_$2, nsprefix_$3) {
switch (arguments.length) {
case 3:
nsprefix_$3 = false
};
return new LzDataset(canvas, {name: name_$0, initialdata: d_$1, trimwhitespace: trimwhitespace_$2, nsprefix: nsprefix_$3})
};
$lzsc$temp["displayName"] = "lzAddLocalData";
return $lzsc$temp
})(), "__LzLibraryLoaded", (function () {
var $lzsc$temp = function (libname_$0) {
canvas.initiatorAddNode({attrs: {libname: libname_$0}, "class": LzLibraryCleanup}, 1);
canvas.initDone()
};
$lzsc$temp["displayName"] = "__LzLibraryLoaded";
return $lzsc$temp
})()], LzView, ["tagname", "canvas", "attributes", new LzInheritedHash(LzView.attributes), "versionInfoString", (function () {
var $lzsc$temp = function () {
return "URL: " + lz.Browser.getLoadURL() + "\n" + "LPS\n" + "  Version: " + canvas.lpsversion + "\n" + "  Release: " + canvas.lpsrelease + "\n" + "  Build: " + canvas.lpsbuild + "\n" + "  Date: " + canvas.lpsbuilddate + "\n" + "Application\n" + "  Date: " + canvas.appbuilddate + "\n" + "Target: " + canvas.runtime + "\n" + "Runtime: " + lz.Browser.getVersion() + "\n" + "OS: " + lz.Browser.getOS() + "\n"
};
$lzsc$temp["displayName"] = "versionInfoString";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
attributes.bgcolor = new LzStyleConstraintExpr("bgcolor", "color", "background-color", 16777215, false);
attributes.font = new LzStyleConstraintExpr("font", "string", "font-family", "Verdana,Vera,sans-serif", false);
attributes.fontsize = new LzStyleConstraintExpr("fontsize", "number", "font-size", 11, false);
attributes.fontstyle = new LzStyleConstraintExpr("fontstyle", "string", "font-style", "plain", false);
attributes.fgcolor = new LzStyleConstraintExpr("fgcolor", "color", "font-color", 0, false);
attributes.width = new LzStyleConstraintExpr("width", "string", "width", "100%", false);
attributes.height = new LzStyleConstraintExpr("height", "string", "height", "100%", false)
}}};
$lzsc$temp["displayName"] = "views/LaszloCanvas.lzs#42/1";
return $lzsc$temp
})()(LzCanvas);
lz[LzCanvas.tagname] = LzCanvas;
var canvas;
Class.make("LzScript", ["src", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3);
attrs_$1.script()
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzNode, ["tagname", "script", "attributes", new LzInheritedHash(LzNode.attributes)]);
lz[LzScript.tagname] = LzScript;
Class.make("LzAnimatorGroup", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
if (attrs_$1 && ("start" in attrs_$1)) {
attrs_$1.started = attrs_$1.start;
attrs_$1.start = LzNode._ignoreAttribute
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "updateDel", void 0, "crepeat", void 0, "startTime", void 0, "__LZpauseTime", void 0, "actAnim", void 0, "notstarted", void 0, "needsrestart", void 0, "attribute", void 0, "start", true, "$lzc$set_start", (function () {
var $lzsc$temp = function (val_$0) {
this.start = val_$0;
this.$lzc$set_started(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_start";
return $lzsc$temp
})(), "started", void 0, "onstarted", LzDeclaredEvent, "$lzc$set_started", (function () {
var $lzsc$temp = function (val_$0) {
this.started = val_$0;
if (this.onstarted.ready) this.onstarted.sendEvent(val_$0);
if (!this.isinited) {
return
};
if (val_$0) {
this.doStart()
} else {
this.stop()
}};
$lzsc$temp["displayName"] = "$lzc$set_started";
return $lzsc$temp
})(), "onstart", LzDeclaredEvent, "onstop", LzDeclaredEvent, "from", void 0, "to", void 0, "duration", void 0, "onduration", LzDeclaredEvent, "$lzc$set_duration", (function () {
var $lzsc$temp = function (duration_$0) {
if (isNaN(duration_$0)) {
duration_$0 = 0
} else {
duration_$0 = Number(duration_$0)
};
this.duration = duration_$0;
var sn_$1 = this.subnodes;
if (sn_$1) {
for (var i_$2 = 0;i_$2 < sn_$1.length;++i_$2) {
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(sn_$1[i_$2]) : sn_$1[i_$2] instanceof LzAnimatorGroup) {
sn_$1[i_$2].$lzc$set_duration(duration_$0)
}}};
if (this.onduration.ready) this.onduration.sendEvent(duration_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_duration";
return $lzsc$temp
})(), "indirect", false, "relative", false, "motion", "easeboth", "repeat", 1, "onrepeat", LzDeclaredEvent, "$lzc$set_repeat", (function () {
var $lzsc$temp = function (val_$0) {
if (val_$0 <= 0) {
Debug.info("%w.%s: value was <= 0, use Infinity instead", this, arguments.callee);
val_$0 = Infinity
};
this.repeat = val_$0
};
$lzsc$temp["displayName"] = "$lzc$set_repeat";
return $lzsc$temp
})(), "paused", false, "onpaused", LzDeclaredEvent, "$lzc$set_paused", (function () {
var $lzsc$temp = function (val_$0) {
if (this.paused && !val_$0) {
this.__LZaddToStartTime(new Date().getTime() - this.__LZpauseTime)
} else if (!this.paused && val_$0) {
this.__LZpauseTime = new Date().getTime()
};
this.paused = val_$0;
if (this.onpaused.ready) this.onpaused.sendEvent(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_paused";
return $lzsc$temp
})(), "target", void 0, "ontarget", LzDeclaredEvent, "$lzc$set_target", (function () {
var $lzsc$temp = function (new_target_$0) {
this.target = new_target_$0;
var nodes_$1 = this.subnodes;
if (nodes_$1) {
for (var i_$2 = 0;i_$2 < nodes_$1.length;i_$2++) {
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(nodes_$1[i_$2]) : nodes_$1[i_$2] instanceof LzAnimatorGroup) {
nodes_$1[i_$2].$lzc$set_target(new_target_$0)
}}};
if (this.ontarget.ready) this.ontarget.sendEvent(new_target_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_target";
return $lzsc$temp
})(), "process", "sequential", "isactive", false, "animatorProps", {attribute: true, from: true, duration: true, to: true, relative: true, target: true, process: true, indirect: true, motion: true}, "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
var ip_$2 = this.immediateparent;
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(ip_$2) : ip_$2 instanceof LzAnimatorGroup) {
for (var k_$3 in this.animatorProps) {
if (args_$1[k_$3] == null) {
args_$1[k_$3] = ip_$2[k_$3]
}};
if (ip_$2.animators == null) {
ip_$2.animators = [this]
} else {
ip_$2.animators.push(this)
};
args_$1.started = LzNode._ignoreAttribute
} else {
this.target = ip_$2
};
if (!this.updateDel) this.updateDel = new LzDelegate(this, "update")
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
if (!this.target) this.target = this.immediateparent;
if (this.started) this.doStart();
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this)
};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "doStart", (function () {
var $lzsc$temp = function () {
if (this.isactive) return false;
this.isactive = true;
if (this.onstart.ready) this.onstart.sendEvent(new Date().getTime());
this.prepareStart();
this.updateDel.register(lz.Idle, "onidle");
return true
};
$lzsc$temp["displayName"] = "doStart";
return $lzsc$temp
})(), "prepareStart", (function () {
var $lzsc$temp = function () {
this.crepeat = this.repeat;
for (var i_$0 = this.animators.length - 1;i_$0 >= 0;i_$0--) {
this.animators[i_$0].notstarted = true
};
this.actAnim = this.animators.concat()
};
$lzsc$temp["displayName"] = "prepareStart";
return $lzsc$temp
})(), "resetAnimator", (function () {
var $lzsc$temp = function () {
this.actAnim = this.animators.concat();
for (var i_$0 = this.animators.length - 1;i_$0 >= 0;i_$0--) {
this.animators[i_$0].needsrestart = true
}};
$lzsc$temp["displayName"] = "resetAnimator";
return $lzsc$temp
})(), "update", (function () {
var $lzsc$temp = function (time_$0) {
if (this.paused) {
return false
};
var animend_$1 = this.actAnim.length - 1;
if (animend_$1 > 0 && this.process == "sequential") animend_$1 = 0;
for (var i_$2 = animend_$1;i_$2 >= 0;i_$2--) {
var a_$3 = this.actAnim[i_$2];
if (a_$3.notstarted) {
a_$3.isactive = true;
a_$3.prepareStart();
a_$3.notstarted = false
} else if (a_$3.needsrestart) {
a_$3.resetAnimator();
a_$3.needsrestart = false
};
if (a_$3.update(time_$0)) {
this.actAnim.splice(i_$2, 1)
}};
if (!this.actAnim.length) {
return this.checkRepeat()
};
return false
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})(), "pause", (function () {
var $lzsc$temp = function (dop_$0) {
switch (arguments.length) {
case 0:
dop_$0 = null
};
if (dop_$0 == null) {
dop_$0 = !this.paused
};
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_paused(dop_$0)
};
$lzsc$temp["displayName"] = "pause";
return $lzsc$temp
})(), "__LZaddToStartTime", (function () {
var $lzsc$temp = function (ptime_$0) {
this.startTime += ptime_$0;
if (this.actAnim) {
for (var i_$1 = 0;i_$1 < this.actAnim.length;i_$1++) {
this.actAnim[i_$1].__LZaddToStartTime(ptime_$0)
}}};
$lzsc$temp["displayName"] = "__LZaddToStartTime";
return $lzsc$temp
})(), "stop", (function () {
var $lzsc$temp = function () {
if (!this.isactive) return;
this.isactive = false;
if (this.actAnim) {
var animend_$0 = this.actAnim.length - 1;
if (animend_$0 > 0 && this.process == "sequential") animend_$0 = 0;
for (var i_$1 = animend_$0;i_$1 >= 0;i_$1--) {
this.actAnim[i_$1].stop()
}};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "__LZfinalizeAnim", (function () {
var $lzsc$temp = function () {
this.__LZhalt()
};
$lzsc$temp["displayName"] = "__LZfinalizeAnim";
return $lzsc$temp
})(), "__LZhalt", (function () {
var $lzsc$temp = function () {
this.isactive = false;
this.updateDel.unregisterAll();
if (this.onstop.ready) this.onstop.sendEvent(new Date().getTime())
};
$lzsc$temp["displayName"] = "__LZhalt";
return $lzsc$temp
})(), "checkRepeat", (function () {
var $lzsc$temp = function () {
if (this.crepeat == 1) {
this.__LZfinalizeAnim();
return true
} else {
this.crepeat--;
if (this.onrepeat.ready) this.onrepeat.sendEvent(new Date().getTime());
this.resetAnimator();
return false
}};
$lzsc$temp["displayName"] = "checkRepeat";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
this.stop();
this.animators = null;
this.actAnim = null;
var ip_$0 = this.immediateparent;
var parAnim_$1 = ip_$0.animators;
if (parAnim_$1 && parAnim_$1.length) {
for (var i_$2 = 0;i_$2 < parAnim_$1.length;i_$2++) {
if (parAnim_$1[i_$2] == this) {
parAnim_$1.splice(i_$2, 1);
break
}};
if (LzAnimatorGroup["$lzsc$isa"] ? LzAnimatorGroup.$lzsc$isa(ip_$0) : ip_$0 instanceof LzAnimatorGroup) {
var activeAnim_$3 = ip_$0.actAnim;
if (activeAnim_$3 && activeAnim_$3.length) {
for (var i_$2 = 0;i_$2 < activeAnim_$3.length;i_$2++) {
if (activeAnim_$3[i_$2] == this) {
activeAnim_$3.splice(i_$2, 1);
break
}}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
if (this.animators) {
return "Group of " + this.animators.length
};
return "Empty group"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], LzNode, ["tagname", "animatorgroup", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzAnimatorGroup.attributes.started = true;
LzAnimatorGroup.attributes.ignoreplacement = true
}}};
$lzsc$temp["displayName"] = "animators/LzAnimatorGroup.lzs#46/1";
return $lzsc$temp
})()(LzAnimatorGroup);
lz[LzAnimatorGroup.tagname] = LzAnimatorGroup;
Class.make("LzAnimator", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "calcMethod", void 0, "currentValue", void 0, "doBegin", void 0, "beginPoleDelta", 0.25, "endPoleDelta", 0.25, "primary_K", 1, "origto", void 0, "beginPole", void 0, "endPole", void 0, "counterkey", void 0, "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
this.calcMethod = this.calcNextValue
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_motion", (function () {
var $lzsc$temp = function (eparam_$0) {
this.motion = eparam_$0;
if (eparam_$0 == "linear") {
this.calcMethod = this.calcNextValueLinear
} else {
this.calcMethod = this.calcNextValue;
this.beginPoleDelta = 0.25;
this.endPoleDelta = 0.25;
if (eparam_$0 == "easeout") {
this.beginPoleDelta = 100
} else if (eparam_$0 == "easein") {
this.endPoleDelta = 15
}}};
$lzsc$temp["displayName"] = "$lzc$set_motion";
return $lzsc$temp
})(), "$lzc$set_to", (function () {
var $lzsc$temp = function (eparam_$0) {
this.origto = Number(eparam_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_to";
return $lzsc$temp
})(), "onattribute", LzDeclaredEvent, "$lzc$set_attribute", (function () {
var $lzsc$temp = function (attribute_$0) {
this.attribute = attribute_$0;
this.counterkey = attribute_$0 + "_lzcounter";
if (this.onattribute.ready) this.onattribute.sendEvent(attribute_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_attribute";
return $lzsc$temp
})(), "calcControlValues", (function () {
var $lzsc$temp = function () {
var cval_$0 = 0;
this.currentValue = cval_$0;
var to_$1 = this.to;
var dir_$2 = this.indirect ? -1 : 1;
if (cval_$0 < to_$1) {
this.beginPole = cval_$0 - dir_$2 * this.beginPoleDelta;
this.endPole = to_$1 + dir_$2 * this.endPoleDelta
} else {
this.beginPole = cval_$0 + dir_$2 * this.beginPoleDelta;
this.endPole = to_$1 - dir_$2 * this.endPoleDelta
};
this.primary_K = 1;
var kN_$3 = 1 * (this.beginPole - to_$1) * (cval_$0 - this.endPole);
var kD_$4 = 1 * (this.beginPole - cval_$0) * (to_$1 - this.endPole);
if (kD_$4 != 0) this.primary_K = Math.abs(kN_$3 / kD_$4)
};
$lzsc$temp["displayName"] = "calcControlValues";
return $lzsc$temp
})(), "doStart", (function () {
var $lzsc$temp = function () {
if (this.isactive) return;
this.isactive = true;
this.prepareStart();
this.updateDel.register(lz.Idle, "onidle")
};
$lzsc$temp["displayName"] = "doStart";
return $lzsc$temp
})(), "updateCounter", (function () {
var $lzsc$temp = function (delta_$0) {
var expected_$1 = this.target.__animatedAttributes;
var val_$2 = expected_$1[this.counterkey];
if (val_$2 == null) {
val_$2 = delta_$0
} else {
val_$2 += delta_$0
};
if (val_$2 == 0) {
delete expected_$1[this.counterkey]
} else {
expected_$1[this.counterkey] = val_$2
};
return val_$2
};
$lzsc$temp["displayName"] = "updateCounter";
return $lzsc$temp
})(), "prepareStart", (function () {
var $lzsc$temp = function () {
this.crepeat = this.repeat;
var targ_$0 = this.target;
var attr_$1 = this.attribute;
var expected_$2 = targ_$0.__animatedAttributes;
if (expected_$2 == null) {
expected_$2 = targ_$0.__animatedAttributes = {}};
if (this.from != null) {
{
var $lzsc$j2870l = Number(this.from);
if (!targ_$0.__LZdeleted) {
var $lzsc$54vsn = "$lzc$set_" + attr_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$0[$lzsc$54vsn]) : targ_$0[$lzsc$54vsn] instanceof Function) {
targ_$0[$lzsc$54vsn]($lzsc$j2870l)
} else {
targ_$0[attr_$1] = $lzsc$j2870l;
var $lzsc$af5f1l = targ_$0["on" + attr_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$af5f1l) : $lzsc$af5f1l instanceof LzEvent) {
if ($lzsc$af5f1l.ready) {
$lzsc$af5f1l.sendEvent($lzsc$j2870l)
}}}}}};
if (expected_$2[attr_$1] == null) {
expected_$2[attr_$1] = Number(targ_$0[attr_$1])
};
if (this.relative) {
this.to = this.origto
} else {
this.to = this.origto - expected_$2[attr_$1]
};
expected_$2[attr_$1] += this.to;
this.updateCounter(1);
this.currentValue = 0;
this.calcControlValues();
this.doBegin = true
};
$lzsc$temp["displayName"] = "prepareStart";
return $lzsc$temp
})(), "resetAnimator", (function () {
var $lzsc$temp = function () {
var targ_$0 = this.target;
var attr_$1 = this.attribute;
var expected_$2 = targ_$0.__animatedAttributes;
if (expected_$2[attr_$1] == null) {
expected_$2[attr_$1] = Number(targ_$0[attr_$1])
};
var from_$3 = this.from;
if (from_$3 != null) {
expected_$2[attr_$1] += Number(from_$3 - expected_$2[attr_$1]);
{
var $lzsc$igi8xw = Number(from_$3);
if (!targ_$0.__LZdeleted) {
var $lzsc$wzrf4g = "$lzc$set_" + attr_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$0[$lzsc$wzrf4g]) : targ_$0[$lzsc$wzrf4g] instanceof Function) {
targ_$0[$lzsc$wzrf4g]($lzsc$igi8xw)
} else {
targ_$0[attr_$1] = $lzsc$igi8xw;
var $lzsc$momqa1 = targ_$0["on" + attr_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$momqa1) : $lzsc$momqa1 instanceof LzEvent) {
if ($lzsc$momqa1.ready) {
$lzsc$momqa1.sendEvent($lzsc$igi8xw)
}}}}}};
if (!this.relative) {
var newto_$4 = this.origto - expected_$2[attr_$1];
if (this.to != newto_$4) {
this.to = newto_$4;
this.calcControlValues()
}};
expected_$2[attr_$1] += this.to;
this.updateCounter(1);
this.currentValue = 0;
this.doBegin = true
};
$lzsc$temp["displayName"] = "resetAnimator";
return $lzsc$temp
})(), "beginAnimator", (function () {
var $lzsc$temp = function (time_$0) {
this.startTime = time_$0;
if (this.onstart.ready) this.onstart.sendEvent(time_$0);
this.doBegin = false
};
$lzsc$temp["displayName"] = "beginAnimator";
return $lzsc$temp
})(), "stop", (function () {
var $lzsc$temp = function () {
if (!this.isactive) return;
this.isactive = false;
var attr_$0 = this.attribute;
var expected_$1 = this.target.__animatedAttributes;
if (this.updateCounter(-1) == 0) {
delete expected_$1[attr_$0]
} else {
expected_$1[attr_$0] -= this.to - this.currentValue
};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "stop";
return $lzsc$temp
})(), "__LZfinalizeAnim", (function () {
var $lzsc$temp = function () {
var targ_$0 = this.target;
var attr_$1 = this.attribute;
var expected_$2 = targ_$0.__animatedAttributes;
if (this.updateCounter(-1) == 0) {
var val_$3 = expected_$2[attr_$1];
delete expected_$2[attr_$1];
{
if (!targ_$0.__LZdeleted) {
var $lzsc$rcq7a4 = "$lzc$set_" + attr_$1;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$0[$lzsc$rcq7a4]) : targ_$0[$lzsc$rcq7a4] instanceof Function) {
targ_$0[$lzsc$rcq7a4](val_$3)
} else {
targ_$0[attr_$1] = val_$3;
var $lzsc$xda0d6 = targ_$0["on" + attr_$1];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$xda0d6) : $lzsc$xda0d6 instanceof LzEvent) {
if ($lzsc$xda0d6.ready) {
$lzsc$xda0d6.sendEvent(val_$3)
}}}}}};
this.__LZhalt()
};
$lzsc$temp["displayName"] = "__LZfinalizeAnim";
return $lzsc$temp
})(), "calcNextValue", (function () {
var $lzsc$temp = function (timeDifference_$0) {
var nextValue_$1 = this.currentValue;
var aEndPole_$2 = this.endPole;
var aBeginPole_$3 = this.beginPole;
var K_$4 = Math.exp(timeDifference_$0 * 1 / this.duration * Math.log(this.primary_K));
if (K_$4 != 1) {
var aNumerator_$5 = aBeginPole_$3 * aEndPole_$2 * (1 - K_$4);
var aDenominator_$6 = aEndPole_$2 - K_$4 * aBeginPole_$3;
if (aDenominator_$6 != 0) nextValue_$1 = aNumerator_$5 / aDenominator_$6
};
return nextValue_$1
};
$lzsc$temp["displayName"] = "calcNextValue";
return $lzsc$temp
})(), "calcNextValueLinear", (function () {
var $lzsc$temp = function (timeDifference_$0) {
var elapsed_$1 = timeDifference_$0 / this.duration;
return elapsed_$1 * this.to
};
$lzsc$temp["displayName"] = "calcNextValueLinear";
return $lzsc$temp
})(), "update", (function () {
var $lzsc$temp = function (time_$0) {
if (this.doBegin) {
this.beginAnimator(time_$0)
} else {
if (!this.paused) {
var aTotalTimeDifference_$1 = time_$0 - this.startTime;
var checkrepeat_$2 = false;
if (aTotalTimeDifference_$1 < this.duration) {
var value_$3 = this.calcMethod(aTotalTimeDifference_$1)
} else {
var value_$3 = this.to;
checkrepeat_$2 = true
};
var targ_$4 = this.target;
var attr_$5 = this.attribute;
{
var $lzsc$qlqd17 = targ_$4[attr_$5] + (value_$3 - this.currentValue);
if (!targ_$4.__LZdeleted) {
var $lzsc$lmk3yn = "$lzc$set_" + attr_$5;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(targ_$4[$lzsc$lmk3yn]) : targ_$4[$lzsc$lmk3yn] instanceof Function) {
targ_$4[$lzsc$lmk3yn]($lzsc$qlqd17)
} else {
targ_$4[attr_$5] = $lzsc$qlqd17;
var $lzsc$gztggd = targ_$4["on" + attr_$5];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$gztggd) : $lzsc$gztggd instanceof LzEvent) {
if ($lzsc$gztggd.ready) {
$lzsc$gztggd.sendEvent($lzsc$qlqd17)
}}}}};
this.currentValue = value_$3;
if (checkrepeat_$2) {
return this.checkRepeat()
}}};
return false
};
$lzsc$temp["displayName"] = "update";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Animator for " + this.target + " attribute:" + this.attribute + " to:" + this.to
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], LzAnimatorGroup, ["tagname", "animator", "attributes", new LzInheritedHash(LzAnimatorGroup.attributes)]);
lz[LzAnimator.tagname] = LzAnimator;
Class.make("LzContextMenu", ["onmenuopen", LzDeclaredEvent, "kernel", null, "items", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (del_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
del_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(del_$0) : del_$0 instanceof LzNode) ? del_$0 : null, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(del_$0) : del_$0 instanceof LzNode) ? attrs_$1 : {delegate: del_$0}, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
this.kernel = new LzContextMenuKernel(this);
this.items = [];
var del_$2 = args_$1 && args_$1["delegate"] || null;
delete args_$1["delegate"];
this.$lzc$set_delegate(del_$2)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
var ip_$0 = this.immediateparent;
if (ip_$0 && (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ip_$0) : ip_$0 instanceof LzView)) {
ip_$0.$lzc$set_contextmenu(this)
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "$lzc$set_delegate", (function () {
var $lzsc$temp = function (delegate_$0) {
this.kernel.setDelegate(delegate_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_delegate";
return $lzsc$temp
})(), "setDelegate", (function () {
var $lzsc$temp = function (delegate_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_delegate(delegate_$0)
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "addItem", (function () {
var $lzsc$temp = function (item_$0) {
if (canvas.capabilities.customcontextmenu) {
this.items.push(item_$0);
this.kernel.addItem(item_$0)
} else {
LzView.__warnCapability("LzContextMenu.addItem", "customcontextmenu")
}};
$lzsc$temp["displayName"] = "addItem";
return $lzsc$temp
})(), "hideBuiltInItems", (function () {
var $lzsc$temp = function () {
this.kernel.hideBuiltInItems()
};
$lzsc$temp["displayName"] = "hideBuiltInItems";
return $lzsc$temp
})(), "showBuiltInItems", (function () {
var $lzsc$temp = function () {
this.kernel.showBuiltInItems()
};
$lzsc$temp["displayName"] = "showBuiltInItems";
return $lzsc$temp
})(), "clearItems", (function () {
var $lzsc$temp = function () {
if (canvas.capabilities.customcontextmenu) {
this.items = [];
this.kernel.clearItems()
} else {
LzView.__warnCapability("LzContextMenu.clearItems", "customcontextmenu")
}};
$lzsc$temp["displayName"] = "clearItems";
return $lzsc$temp
})(), "getItems", (function () {
var $lzsc$temp = function () {
return this.items
};
$lzsc$temp["displayName"] = "getItems";
return $lzsc$temp
})(), "makeMenuItem", (function () {
var $lzsc$temp = function (title_$0, delegate_$1) {
var item_$2 = new LzContextMenuItem(title_$0, delegate_$1);
return item_$2
};
$lzsc$temp["displayName"] = "makeMenuItem";
return $lzsc$temp
})()], LzNode, ["tagname", "contextmenu", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzContextMenu.attributes.ignoreplacement = true
}}};
$lzsc$temp["displayName"] = "helpers/LzContextMenu.lzs#32/1";
return $lzsc$temp
})()(LzContextMenu);
lz[LzContextMenu.tagname] = LzContextMenu;
Class.make("LzContextMenuItem", ["onselect", LzDeclaredEvent, "kernel", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (title_$0, del_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 1:
del_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(title_$0) : title_$0 instanceof LzNode) ? title_$0 : null, (LzNode["$lzsc$isa"] ? LzNode.$lzsc$isa(title_$0) : title_$0 instanceof LzNode) ? del_$1 : {title: title_$0, delegate: del_$1}, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1);
var title_$2 = args_$1 && args_$1["title"] || "";
delete args_$1["title"];
var del_$3 = args_$1 && args_$1["delegate"] || null;
delete args_$1["delegate"];
this.kernel = new LzContextMenuItemKernel(this, title_$2, del_$3);
var ip_$4 = this.immediateparent;
if (ip_$4 && (LzContextMenu["$lzsc$isa"] ? LzContextMenu.$lzsc$isa(ip_$4) : ip_$4 instanceof LzContextMenu)) {
ip_$4.addItem(this)
}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_delegate", (function () {
var $lzsc$temp = function (delegate_$0) {
this.kernel.setDelegate(delegate_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_delegate";
return $lzsc$temp
})(), "$lzc$set_caption", (function () {
var $lzsc$temp = function (caption_$0) {
this.kernel.setCaption(caption_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_caption";
return $lzsc$temp
})(), "$lzc$set_enabled", (function () {
var $lzsc$temp = function (val_$0) {
this.kernel.setEnabled(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_enabled";
return $lzsc$temp
})(), "$lzc$set_separatorbefore", (function () {
var $lzsc$temp = function (val_$0) {
this.kernel.setSeparatorBefore(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_separatorbefore";
return $lzsc$temp
})(), "$lzc$set_visible", (function () {
var $lzsc$temp = function (val_$0) {
this.kernel.setVisible(val_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "setDelegate", (function () {
var $lzsc$temp = function (delegate_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_delegate(delegate_$0)
};
$lzsc$temp["displayName"] = "setDelegate";
return $lzsc$temp
})(), "setCaption", (function () {
var $lzsc$temp = function (caption_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_caption(caption_$0)
};
$lzsc$temp["displayName"] = "setCaption";
return $lzsc$temp
})(), "getCaption", (function () {
var $lzsc$temp = function () {
return this.kernel.getCaption()
};
$lzsc$temp["displayName"] = "getCaption";
return $lzsc$temp
})(), "setEnabled", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_enabled(val_$0)
};
$lzsc$temp["displayName"] = "setEnabled";
return $lzsc$temp
})(), "setSeparatorBefore", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_separatorbefore(val_$0)
};
$lzsc$temp["displayName"] = "setSeparatorBefore";
return $lzsc$temp
})(), "setVisible", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(val_$0)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})()], LzNode, ["tagname", "contextmenuitem", "attributes", new LzInheritedHash(LzNode.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "helpers/LzContextMenu.lzs#196/1";
return $lzsc$temp
})()(LzContextMenuItem);
lz[LzContextMenuItem.tagname] = LzContextMenuItem;
Class.make("LzFont", ["style", void 0, "name", void 0, "height", void 0, "ascent", void 0, "descent", void 0, "advancetable", void 0, "lsbtable", void 0, "rsbtable", void 0, "fontobject", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (fontobject_$0, attrs_$1, style_$2) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.name = fontobject_$0.name;
this.style = style_$2;
this.fontobject = fontobject_$0;
fontobject_$0[style_$2] = this;
for (var k_$3 in attrs_$1) {
if (k_$3 == "leading") continue;
this[k_$3] = attrs_$1[k_$3]
};
this.height = this.ascent + this.descent;
this.advancetable[13] = this.advancetable[32];
this.advancetable[160] = 0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "leading", 2, "toString", (function () {
var $lzsc$temp = function () {
return "Font style " + this.style + " of name " + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()]);
lz.Font = LzFont;
Class.make("LzState", ["heldArgs", void 0, "releasedconstraints", void 0, "appliedChildren", void 0, "applyOnInit", false, "__LZpool", null, "__LZstateconstraintdelegates", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
this.heldArgs = {};
this.appliedChildren = [];
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "onapply", LzDeclaredEvent, "onremove", LzDeclaredEvent, "applied", false, "$lzc$set_applied", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0) {
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
})(), "onapplied", LzDeclaredEvent, "asyncnew", false, "subh", null, "pooling", false, "$lzc$set_asyncnew", (function () {
var $lzsc$temp = function (v_$0) {
this.__LZsetProperty(v_$0, "asyncnew")
};
$lzsc$temp["displayName"] = "$lzc$set_asyncnew";
return $lzsc$temp
})(), "$lzc$set_pooling", (function () {
var $lzsc$temp = function (v_$0) {
this.__LZsetProperty(v_$0, "pooling")
};
$lzsc$temp["displayName"] = "$lzc$set_pooling";
return $lzsc$temp
})(), "$lzc$set___LZsourceLocation", (function () {
var $lzsc$temp = function (v_$0) {
this.__LZsetProperty(v_$0, "__LZsourceLocation")
};
$lzsc$temp["displayName"] = "$lzc$set___LZsourceLocation";
return $lzsc$temp
})(), "init", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["init"] || this.nextMethod(arguments.callee, "init")).call(this);
if (this.applyOnInit) {
this.apply()
}};
$lzsc$temp["displayName"] = "init";
return $lzsc$temp
})(), "createChildren", (function () {
var $lzsc$temp = function (carr_$0) {
this.subh = carr_$0;
this.__LZinstantiationDone()
};
$lzsc$temp["displayName"] = "createChildren";
return $lzsc$temp
})(), "apply", (function () {
var $lzsc$temp = function () {
if (this.applied) {
return
};
var parent_$0 = this.parent;
this.applied = true;
var pia_$1 = parent_$0._instanceAttrs;
if (pia_$1) {
for (var key_$2 in this.heldArgs) {
if (LzConstraintExpr["$lzsc$isa"] ? LzConstraintExpr.$lzsc$isa(pia_$1[key_$2]) : pia_$1[key_$2] instanceof LzConstraintExpr) {
if (this.releasedconstraints == null) {
this.releasedconstraints = []
};
var constraintMethodName_$3 = pia_$1[key_$2].methodName;
if (parent_$0.releaseConstraintMethod(constraintMethodName_$3)) {
this.releasedconstraints.push(constraintMethodName_$3)
}}}};
var od_$4 = parent_$0.__LZconstraintdelegates;
parent_$0.__LZconstraintdelegates = null;
parent_$0.__LZapplyArgs(this.heldArgs, null);
if (this.subh) var shl_$5 = this.subh.length;
parent_$0.__LZsetPreventInit();
for (var i_$6 = 0;i_$6 < shl_$5;i_$6++) {
if (this.__LZpool && this.__LZpool[i_$6]) {
this.appliedChildren.push(this.__LZretach(this.__LZpool[i_$6]))
} else {
this.appliedChildren.push(parent_$0.makeChild(this.subh[i_$6], this.asyncnew))
}};
parent_$0.__LZclearPreventInit();
parent_$0.__LZresolveReferences();
this.__LZstateconstraintdelegates = parent_$0.__LZconstraintdelegates;
parent_$0.__LZconstraintdelegates = od_$4;
if (this.onapply.ready) this.onapply.sendEvent(this);
if (this.onapplied.ready) this.onapplied.sendEvent(true)
};
$lzsc$temp["displayName"] = "apply";
return $lzsc$temp
})(), "remove", (function () {
var $lzsc$temp = function () {
if (!this.applied) {
return
};
this.applied = false;
if (this.onremove.ready) this.onremove.sendEvent(this);
if (this.onapplied.ready) this.onapplied.sendEvent(false);
var dels_$0 = this.__LZstateconstraintdelegates;
if (dels_$0) {
for (var i_$1 = 0, l_$2 = dels_$0.length;i_$1 < l_$2;i_$1++) {
var del_$3 = dels_$0[i_$1];
if (del_$3.__LZdeleted == false) {
del_$3.destroy()
}};
this.__LZstateconstraintdelegates = null
};
if (this.pooling && this.appliedChildren.length) {
this.__LZpool = []
};
for (var i_$1 = 0;i_$1 < this.appliedChildren.length;i_$1++) {
var ac_$4 = this.appliedChildren[i_$1];
if (this.pooling) {
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ac_$4) : ac_$4 instanceof LzView) {
this.__LZpool.push(this.__LZdetach(ac_$4))
} else {
ac_$4.destroy();
this.__LZpool.push(null)
}} else {
ac_$4.destroy()
}};
this.appliedChildren = [];
if (this.releasedconstraints != null) {
this.releasedconstraints = null
}};
$lzsc$temp["displayName"] = "remove";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted) return;
this.pooling = false;
this.remove();
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "__LZapplyArgs", (function () {
var $lzsc$temp = function (args_$0, constcall_$1) {
var stateArgs_$2 = {};
var held_$3 = this.heldArgs;
var $4 = null;
for (var key_$5 in args_$0) {
var val_$6 = args_$0[key_$5];
var setr_$7 = "$lzc$set_" + key_$5;
if (key_$5 == "$delegates") {
$4 = val_$6
} else if (this[setr_$7]) {
stateArgs_$2[key_$5] = val_$6
} else {
held_$3[key_$5] = val_$6
}};
if ($4 != null) {
var pardels_$8 = null;
var mydels_$9 = null;
var ignore_$a = LzNode._ignoreAttribute;
for (var i_$b = 0, l_$c = $4.length;i_$b < $4.length;i_$b += 3) {
if (LzState.events[$4[i_$b]] && !$4[i_$b + 2]) {
if (mydels_$9 == null) {
mydels_$9 = []
};
var arrtopush_$d = mydels_$9;
var mname_$e = $4[i_$b + 1];
if (mname_$e in held_$3) {
stateArgs_$2[mname_$e] = held_$3[mname_$e];
delete held_$3[mname_$e]
}} else {
if (pardels_$8 == null) {
pardels_$8 = []
};
var arrtopush_$d = pardels_$8
};
arrtopush_$d.push($4[i_$b], $4[i_$b + 1], $4[i_$b + 2])
};
if (mydels_$9 != null) {
stateArgs_$2.$delegates = mydels_$9
};
if (pardels_$8 != null) {
held_$3.$delegates = pardels_$8
}};
for (var key_$5 in stateArgs_$2) {
var val_$6 = stateArgs_$2[key_$5];
if (LzOnceExpr["$lzsc$isa"] ? LzOnceExpr.$lzsc$isa(val_$6) : val_$6 instanceof LzOnceExpr) {
var methodName_$f = val_$6.methodName;
if (methodName_$f in held_$3) {
stateArgs_$2[methodName_$f] = held_$3[methodName_$f];
delete held_$3[methodName_$f]
};
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(val_$6) : val_$6 instanceof LzAlwaysExpr) {
var dependenciesName_$g = val_$6.dependenciesName;
if (dependenciesName_$g in held_$3) {
stateArgs_$2[dependenciesName_$g] = held_$3[dependenciesName_$g];
delete held_$3[dependenciesName_$g]
}}}};
var rename_$h = null;
for (var key_$5 in held_$3) {
var val_$6 = held_$3[key_$5];
if (LzOnceExpr["$lzsc$isa"] ? LzOnceExpr.$lzsc$isa(val_$6) : val_$6 instanceof LzOnceExpr) {
if (rename_$h == null) {
rename_$h = []
};
rename_$h.push(key_$5, val_$6)
}};
if (rename_$h != null) {
for (var i_$b = 0, l_$c = rename_$h.length;i_$b < l_$c;i_$b += 2) {
var key_$5 = rename_$h[i_$b];
var expr_$i = rename_$h[i_$b + 1];
var methodName_$f = expr_$i.methodName;
var newMethodName_$j = methodName_$f + this.__LZUID;
var dbgName_$k = null;
dbgName_$k = expr_$i._dbg_name;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(held_$3[methodName_$f]) : held_$3[methodName_$f] instanceof Function) {
held_$3[newMethodName_$j] = held_$3[methodName_$f];
delete held_$3[methodName_$f]
} else if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[methodName_$f]) : this[methodName_$f] instanceof Function) {
held_$3[newMethodName_$j] = this[methodName_$f]
};
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(expr_$i) : expr_$i instanceof LzAlwaysExpr) {
var dependenciesName_$g = expr_$i.dependenciesName;
var newDependenciesName_$l = dependenciesName_$g + this.__LZUID;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(held_$3[dependenciesName_$g]) : held_$3[dependenciesName_$g] instanceof Function) {
held_$3[newDependenciesName_$l] = held_$3[dependenciesName_$g];
delete held_$3[dependenciesName_$g]
} else if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this[dependenciesName_$g]) : this[dependenciesName_$g] instanceof Function) {
held_$3[newDependenciesName_$l] = this[dependenciesName_$g]
};
held_$3[key_$5] = new (expr_$i.constructor)(expr_$i.attribute, expr_$i.type, newMethodName_$j, newDependenciesName_$l, dbgName_$k)
} else {
held_$3[key_$5] = new (expr_$i.constructor)(expr_$i.attribute, expr_$i.type, newMethodName_$j, dbgName_$k)
}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZapplyArgs"] || this.nextMethod(arguments.callee, "__LZapplyArgs")).call(this, stateArgs_$2, constcall_$1)
};
$lzsc$temp["displayName"] = "__LZapplyArgs";
return $lzsc$temp
})(), "__LZsetProperty", (function () {
var $lzsc$temp = function (prop_$0, propname_$1) {
this[propname_$1] = prop_$0
};
$lzsc$temp["displayName"] = "__LZsetProperty";
return $lzsc$temp
})(), "__LZdetach", (function () {
var $lzsc$temp = function (aview_$0) {
aview_$0.$lzc$set_visible(false);
return aview_$0
};
$lzsc$temp["displayName"] = "__LZdetach";
return $lzsc$temp
})(), "__LZretach", (function () {
var $lzsc$temp = function (aview_$0) {
aview_$0.$lzc$set_visible(true);
return aview_$0
};
$lzsc$temp["displayName"] = "__LZretach";
return $lzsc$temp
})()], LzNode, ["tagname", "state", "attributes", new LzInheritedHash(LzNode.attributes), "events", {onremove: true, onapply: true, onapplied: true}]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
prototype.$isstate = true
}}};
$lzsc$temp["displayName"] = "helpers/LzState.lzs#92/1";
return $lzsc$temp
})()(LzState);
lz[LzState.tagname] = LzState;
Class.make("LzURL", ["protocol", null, "host", null, "port", null, "path", null, "file", null, "query", null, "fragment", null, "_parsed", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (url_$0) {
switch (arguments.length) {
case 0:
url_$0 = null
};
if (url_$0 != null) {
this.parseURL(url_$0)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "parseURL", (function () {
var $lzsc$temp = function (url_$0) {
if (this._parsed == url_$0) return;
this._parsed = url_$0;
var i0_$1 = 0;
var i1_$2 = url_$0.indexOf(":");
var iquery_$3 = url_$0.indexOf("?", i0_$1);
var ifrag_$4 = url_$0.indexOf("#", i0_$1);
var iopt_$5 = url_$0.length;
if (ifrag_$4 != -1) {
iopt_$5 = ifrag_$4
};
if (iquery_$3 != -1) {
iopt_$5 = iquery_$3
};
if (i1_$2 != -1) {
this.protocol = url_$0.substring(i0_$1, i1_$2);
if (url_$0.substring(i1_$2 + 1, i1_$2 + 3) == "//") {
i0_$1 = i1_$2 + 3;
i1_$2 = url_$0.indexOf("/", i0_$1);
if (i1_$2 == -1) {
i1_$2 = iopt_$5
};
var hostPort_$6 = url_$0.substring(i0_$1, i1_$2);
var i_$7 = hostPort_$6.indexOf(":");
if (i_$7 == -1) {
this.host = hostPort_$6;
this.port = null
} else {
this.host = hostPort_$6.substring(0, i_$7);
this.port = hostPort_$6.substring(i_$7 + 1)
}} else {
i1_$2++
};
i0_$1 = i1_$2
};
i1_$2 = iopt_$5;
this._splitPath(url_$0.substring(i0_$1, i1_$2));
if (ifrag_$4 != -1) {
this.fragment = url_$0.substring(ifrag_$4 + 1, url_$0.length)
} else {
ifrag_$4 = url_$0.length
};
if (iquery_$3 != -1) {
this.query = url_$0.substring(iquery_$3 + 1, ifrag_$4)
}};
$lzsc$temp["displayName"] = "parseURL";
return $lzsc$temp
})(), "_splitPath", (function () {
var $lzsc$temp = function (pathfile_$0) {
if (pathfile_$0 == "") {
return
};
var ls_$1 = pathfile_$0.lastIndexOf("/");
if (ls_$1 != -1) {
this.path = pathfile_$0.substring(0, ls_$1 + 1);
this.file = pathfile_$0.substring(ls_$1 + 1, pathfile_$0.length);
if (this.file == "") {
this.file = null
};
return
};
this.path = null;
this.file = pathfile_$0
};
$lzsc$temp["displayName"] = "_splitPath";
return $lzsc$temp
})(), "dupe", (function () {
var $lzsc$temp = function () {
var o_$0 = new LzURL();
o_$0.protocol = this.protocol;
o_$0.host = this.host;
o_$0.port = this.port;
o_$0.path = this.path;
o_$0.file = this.file;
o_$0.query = this.query;
o_$0.fragment = this.fragment;
return o_$0
};
$lzsc$temp["displayName"] = "dupe";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
var out_$0 = "";
if (this.protocol != null) {
out_$0 += this.protocol + ":";
if (this.host != null) {
out_$0 += "//" + this.host;
if (null != this.port && lz.Browser.defaultPortNums[this.protocol] != this.port) {
out_$0 += ":" + this.port
}}};
if (this.path != null) {
out_$0 += this.path
};
if (null != this.file) {
out_$0 += this.file
};
if (null != this.query) {
out_$0 += "?" + this.query
};
if (null != this.fragment) {
out_$0 += "#" + this.fragment
};
return out_$0
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], null, ["merge", (function () {
var $lzsc$temp = function (url_$0, defaults_$1) {
var m_$2 = new LzURL();
var tocopy_$3 = {protocol: true, host: true, port: true, path: true, file: true, query: true, fragment: true};
for (var key_$4 in tocopy_$3) {
m_$2[key_$4] = url_$0[key_$4] != null ? url_$0[key_$4] : defaults_$1[key_$4]
};
return m_$2
};
$lzsc$temp["displayName"] = "merge";
return $lzsc$temp
})()]);
lz.URL = LzURL;
Mixin.make("LzDataNodeMixin", ["onownerDocument", LzDeclaredEvent, "onDocumentChange", LzDeclaredEvent, "onparentNode", LzDeclaredEvent, "onchildNode", LzDeclaredEvent, "onchildNodes", LzDeclaredEvent, "onattributes", LzDeclaredEvent, "onnodeName", LzDeclaredEvent, "nodeType", void 0, "parentNode", null, "ownerDocument", void 0, "childNodes", null, "__LZo", -1, "__LZcoDirty", true, "sel", false, "__LZuserData", null, "__LZuserHandler", null, "getParent", (function () {
var $lzsc$temp = function () {
return this.parentNode
};
$lzsc$temp["displayName"] = "getParent";
return $lzsc$temp
})(), "getOffset", (function () {
var $lzsc$temp = function () {
if (!this.parentNode) return 0;
if (this.parentNode.__LZcoDirty) this.parentNode.__LZupdateCO();
return this.__LZo
};
$lzsc$temp["displayName"] = "getOffset";
return $lzsc$temp
})(), "getPreviousSibling", (function () {
var $lzsc$temp = function () {
if (!this.parentNode) return null;
if (this.parentNode.__LZcoDirty) this.parentNode.__LZupdateCO();
return this.parentNode.childNodes[this.__LZo - 1]
};
$lzsc$temp["displayName"] = "getPreviousSibling";
return $lzsc$temp
})(), "$lzc$getPreviousSibling_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [this.parentNode, "childNodes", this, "parentNode"]
};
$lzsc$temp["displayName"] = "$lzc$getPreviousSibling_dependencies";
return $lzsc$temp
})(), "getNextSibling", (function () {
var $lzsc$temp = function () {
if (!this.parentNode) return null;
if (this.parentNode.__LZcoDirty) this.parentNode.__LZupdateCO();
return this.parentNode.childNodes[this.__LZo + 1]
};
$lzsc$temp["displayName"] = "getNextSibling";
return $lzsc$temp
})(), "$lzc$getNextSibling_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [this.parentNode, "childNodes", this, "parentNode"]
};
$lzsc$temp["displayName"] = "$lzc$getNextSibling_dependencies";
return $lzsc$temp
})(), "childOfNode", (function () {
var $lzsc$temp = function (el_$0, allowself_$1) {
switch (arguments.length) {
case 1:
allowself_$1 = false
};
var p_$2 = allowself_$1 ? this : this.parentNode;
while (p_$2) {
if (p_$2 === el_$0) return true;
p_$2 = p_$2.parentNode
};
return false
};
$lzsc$temp["displayName"] = "childOfNode";
return $lzsc$temp
})(), "childOf", (function () {
var $lzsc$temp = function (el_$0, allowself_$1) {
switch (arguments.length) {
case 1:
allowself_$1 = false
};
return this.childOfNode(el_$0, allowself_$1)
};
$lzsc$temp["displayName"] = "childOf";
return $lzsc$temp
})(), "$lzc$set_ownerDocument", (function () {
var $lzsc$temp = function (ownerDoc_$0) {
this.ownerDocument = ownerDoc_$0;
if (this.childNodes) {
for (var i_$1 = 0;i_$1 < this.childNodes.length;i_$1++) {
this.childNodes[i_$1].$lzc$set_ownerDocument(ownerDoc_$0)
}};
if (this.onownerDocument.ready) {
this.onownerDocument.sendEvent(ownerDoc_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_ownerDocument";
return $lzsc$temp
})(), "setOwnerDocument", (function () {
var $lzsc$temp = function (ownerDoc_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_ownerDocument(ownerDoc_$0)
};
$lzsc$temp["displayName"] = "setOwnerDocument";
return $lzsc$temp
})(), "cloneNode", (function () {
var $lzsc$temp = function (deep_$0) {
switch (arguments.length) {
case 0:
deep_$0 = false
};
return undefined
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "serialize", (function () {
var $lzsc$temp = function () {
return undefined
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "__LZlockFromUpdate", (function () {
var $lzsc$temp = function (locker_$0) {
this.ownerDocument.__LZdoLock(locker_$0)
};
$lzsc$temp["displayName"] = "__LZlockFromUpdate";
return $lzsc$temp
})(), "__LZunlockFromUpdate", (function () {
var $lzsc$temp = function (locker_$0) {
this.ownerDocument.__LZdoUnlock(locker_$0)
};
$lzsc$temp["displayName"] = "__LZunlockFromUpdate";
return $lzsc$temp
})(), "setUserData", (function () {
var $lzsc$temp = function (key_$0, data_$1, handler_$2) {
switch (arguments.length) {
case 2:
handler_$2 = null
};
if (this.__LZuserData == null) {
this.__LZuserData = {}};
if (handler_$2 != null) {
Debug.warn("use of the handler arg to setUserData is not currently implemented")
};
var prevdata_$3 = this.__LZuserData[key_$0];
if (data_$1 != null) {
this.__LZuserData[key_$0] = data_$1
} else if (prevdata_$3 != null) {
delete this.__LZuserData[key_$0]
};
return prevdata_$3 != null ? prevdata_$3 : null
};
$lzsc$temp["displayName"] = "setUserData";
return $lzsc$temp
})(), "getUserData", (function () {
var $lzsc$temp = function (key_$0) {
if (this.__LZuserData == null) {
return null
} else {
var udata_$1 = this.__LZuserData[key_$0];
return udata_$1 != null ? udata_$1 : null
}};
$lzsc$temp["displayName"] = "getUserData";
return $lzsc$temp
})()]);
lz.DataNodeMixin = LzDataNodeMixin;
Class.make("LzDataNode", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "lz.DataNode"
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], LzEventable, ["ELEMENT_NODE", 1, "TEXT_NODE", 3, "DOCUMENT_NODE", 9, "stringToLzData", (function () {
var $lzsc$temp = function (str_$0, trimwhitespace_$1, nsprefix_$2) {
switch (arguments.length) {
case 1:
trimwhitespace_$1 = false;;case 2:
nsprefix_$2 = false
};
Debug.info("lz.DataNode.stringToLzData is deprecated.  Use `lz.DataElement.stringToLzData` instead.");
return LzDataElement.stringToLzData(str_$0, trimwhitespace_$1, nsprefix_$2)
};
$lzsc$temp["displayName"] = "stringToLzData";
return $lzsc$temp
})()]);
lz.DataNode = LzDataNode;
Mixin.make("LzDataElementMixin", ["__LZchangeQ", null, "__LZlocker", null, "nodeName", null, "attributes", null, "insertBefore", (function () {
var $lzsc$temp = function (newChild_$0, refChild_$1) {
if (newChild_$0 == null) {
return null
} else if (refChild_$1 == null) {
return this.appendChild(newChild_$0)
} else {
var off_$2 = this.__LZgetCO(refChild_$1);
if (off_$2 >= 0) {
var samenode_$3 = newChild_$0 === refChild_$1;
if (newChild_$0.parentNode != null) {
if (newChild_$0.parentNode === this) {
if (!samenode_$3) {
var nchildoff_$4 = this.__LZremoveChild(newChild_$0);
if (nchildoff_$4 != -1 && nchildoff_$4 < off_$2) {
off_$2 -= 1
}}} else {
newChild_$0.parentNode.removeChild(newChild_$0)
}};
if (!samenode_$3) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$2, 0, newChild_$0)
};
newChild_$0.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$0.parentNode = this;
if (newChild_$0.onparentNode.ready) newChild_$0.onparentNode.sendEvent(this);
if (this.onchildNodes.ready) this.onchildNodes.sendEvent(newChild_$0);
this.ownerDocument.handleDocumentChange("insertBefore", this, 0);
return newChild_$0
};
return null
}};
$lzsc$temp["displayName"] = "insertBefore";
return $lzsc$temp
})(), "replaceChild", (function () {
var $lzsc$temp = function (newChild_$0, oldChild_$1) {
if (newChild_$0 == null) {
return null
} else {
var off_$2 = this.__LZgetCO(oldChild_$1);
if (off_$2 >= 0) {
var samenode_$3 = newChild_$0 === oldChild_$1;
if (newChild_$0.parentNode != null) {
if (newChild_$0.parentNode === this) {
if (!samenode_$3) {
var nchildoff_$4 = this.__LZremoveChild(newChild_$0);
if (nchildoff_$4 != -1 && nchildoff_$4 < off_$2) {
off_$2 -= 1
}}} else {
newChild_$0.parentNode.removeChild(newChild_$0)
}};
if (!samenode_$3) {
newChild_$0.__LZo = off_$2;
this.childNodes[off_$2] = newChild_$0
};
newChild_$0.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$0.parentNode = this;
if (newChild_$0.onparentNode.ready) newChild_$0.onparentNode.sendEvent(this);
if (this.onchildNodes.ready) this.onchildNodes.sendEvent(newChild_$0);
this.ownerDocument.handleDocumentChange("childNodes", this, 0, newChild_$0);
return newChild_$0
};
return null
}};
$lzsc$temp["displayName"] = "replaceChild";
return $lzsc$temp
})(), "removeChild", (function () {
var $lzsc$temp = function (oldChild_$0) {
var off_$1 = this.__LZgetCO(oldChild_$0);
if (off_$1 >= 0) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$1, 1);
if (this.onchildNodes.ready) this.onchildNodes.sendEvent(oldChild_$0);
this.ownerDocument.handleDocumentChange("removeChild", this, 0, oldChild_$0);
return oldChild_$0
};
return null
};
$lzsc$temp["displayName"] = "removeChild";
return $lzsc$temp
})(), "appendChild", (function () {
var $lzsc$temp = function (newChild_$0) {
if (newChild_$0 == null) {
return null
} else {
if (newChild_$0.parentNode != null) {
if (newChild_$0.parentNode === this) {
this.__LZremoveChild(newChild_$0)
} else {
newChild_$0.parentNode.removeChild(newChild_$0)
}};
this.childNodes.push(newChild_$0);
newChild_$0.__LZo = this.childNodes.length - 1;
newChild_$0.$lzc$set_ownerDocument(this.ownerDocument);
newChild_$0.parentNode = this;
if (newChild_$0.onparentNode.ready) newChild_$0.onparentNode.sendEvent(this);
if (this.onchildNodes.ready) this.onchildNodes.sendEvent(newChild_$0);
this.ownerDocument.handleDocumentChange("appendChild", this, 0, newChild_$0);
return newChild_$0
}};
$lzsc$temp["displayName"] = "appendChild";
return $lzsc$temp
})(), "hasChildNodes", (function () {
var $lzsc$temp = function () {
return this.childNodes.length > 0
};
$lzsc$temp["displayName"] = "hasChildNodes";
return $lzsc$temp
})(), "cloneNode", (function () {
var $lzsc$temp = function (deep_$0) {
switch (arguments.length) {
case 0:
deep_$0 = false
};
var n_$1 = new LzDataElement(this.nodeName, this.attributes);
if (deep_$0) {
var cn_$2 = this.childNodes;
var copy_$3 = [];
for (var i_$4 = cn_$2.length - 1;i_$4 >= 0;--i_$4) {
copy_$3[i_$4] = cn_$2[i_$4].cloneNode(true)
};
n_$1.$lzc$set_childNodes(copy_$3)
};
return n_$1
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "getAttr", (function () {
var $lzsc$temp = function (name_$0) {
return this.attributes[name_$0]
};
$lzsc$temp["displayName"] = "getAttr";
return $lzsc$temp
})(), "$lzc$getAttr_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "attributes"]
};
$lzsc$temp["displayName"] = "$lzc$getAttr_dependencies";
return $lzsc$temp
})(), "setAttr", (function () {
var $lzsc$temp = function (name_$0, value_$1) {
value_$1 = String(value_$1);
this.attributes[name_$0] = value_$1;
if (this.onattributes.ready) this.onattributes.sendEvent(name_$0);
this.ownerDocument.handleDocumentChange("attributes", this, 1, {name: name_$0, value: value_$1, type: "set"});
return value_$1
};
$lzsc$temp["displayName"] = "setAttr";
return $lzsc$temp
})(), "removeAttr", (function () {
var $lzsc$temp = function (name_$0) {
var v_$1 = this.attributes[name_$0];
delete this.attributes[name_$0];
if (this.onattributes.ready) this.onattributes.sendEvent(name_$0);
this.ownerDocument.handleDocumentChange("attributes", this, 1, {name: name_$0, value: v_$1, type: "remove"});
return v_$1
};
$lzsc$temp["displayName"] = "removeAttr";
return $lzsc$temp
})(), "hasAttr", (function () {
var $lzsc$temp = function (name_$0) {
return this.attributes[name_$0] != null
};
$lzsc$temp["displayName"] = "hasAttr";
return $lzsc$temp
})(), "$lzc$hasAttr_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [self_$1, "attributes"]
};
$lzsc$temp["displayName"] = "$lzc$hasAttr_dependencies";
return $lzsc$temp
})(), "getFirstChild", (function () {
var $lzsc$temp = function () {
return this.childNodes[0]
};
$lzsc$temp["displayName"] = "getFirstChild";
return $lzsc$temp
})(), "$lzc$getFirstChild_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [this, "childNodes"]
};
$lzsc$temp["displayName"] = "$lzc$getFirstChild_dependencies";
return $lzsc$temp
})(), "getLastChild", (function () {
var $lzsc$temp = function () {
return this.childNodes[this.childNodes.length - 1]
};
$lzsc$temp["displayName"] = "getLastChild";
return $lzsc$temp
})(), "$lzc$getLastChild_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1) {
return [this, "childNodes"]
};
$lzsc$temp["displayName"] = "$lzc$getLastChild_dependencies";
return $lzsc$temp
})(), "__LZgetCO", (function () {
var $lzsc$temp = function (child_$0) {
if (child_$0 != null) {
var cn_$1 = this.childNodes;
if (!this.__LZcoDirty) {
var i_$2 = child_$0.__LZo;
if (cn_$1[i_$2] === child_$0) {
return i_$2
}} else {
for (var i_$2 = cn_$1.length - 1;i_$2 >= 0;--i_$2) {
if (cn_$1[i_$2] === child_$0) {
return i_$2
}}}};
return -1
};
$lzsc$temp["displayName"] = "__LZgetCO";
return $lzsc$temp
})(), "__LZremoveChild", (function () {
var $lzsc$temp = function (oldChild_$0) {
var off_$1 = this.__LZgetCO(oldChild_$0);
if (off_$1 >= 0) {
this.__LZcoDirty = true;
this.childNodes.splice(off_$1, 1)
};
return off_$1
};
$lzsc$temp["displayName"] = "__LZremoveChild";
return $lzsc$temp
})(), "__LZupdateCO", (function () {
var $lzsc$temp = function () {
var cn_$0 = this.childNodes;
for (var i_$1 = 0, len_$2 = cn_$0.length;i_$1 < len_$2;i_$1++) {
cn_$0[i_$1].__LZo = i_$1
};
this.__LZcoDirty = false
};
$lzsc$temp["displayName"] = "__LZupdateCO";
return $lzsc$temp
})(), "$lzc$set_attributes", (function () {
var $lzsc$temp = function (attrs_$0) {
var a_$1 = {};
for (var k_$2 in attrs_$0) {
var val_$3 = attrs_$0[k_$2];
if (typeof val_$3 != "string") {
Debug.info("In a future release, lz.DataElement will coerce the non-string value %w to a String for the attribute %w.  You may safely ignore this as long as you expect to get a String value back.", val_$3, k_$2)
};
a_$1[k_$2] = attrs_$0[k_$2]
};
this.attributes = a_$1;
if (this.onattributes.ready) this.onattributes.sendEvent(a_$1);
this.ownerDocument.handleDocumentChange("attributes", this, 1)
};
$lzsc$temp["displayName"] = "$lzc$set_attributes";
return $lzsc$temp
})(), "setAttrs", (function () {
var $lzsc$temp = function (attrs_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_attributes(attrs_$0)
};
$lzsc$temp["displayName"] = "setAttrs";
return $lzsc$temp
})(), "$lzc$set_childNodes", (function () {
var $lzsc$temp = function (children_$0) {
if (!children_$0) children_$0 = [];
this.childNodes = children_$0;
if (children_$0.length > 0) {
var notifyParent_$1 = true;
var otherParent_$2 = children_$0[0].parentNode;
if (otherParent_$2 != null && otherParent_$2 !== this && otherParent_$2.childNodes === children_$0) {
notifyParent_$1 = false;
otherParent_$2.$lzc$set_childNodes([])
};
for (var i_$3 = 0;i_$3 < children_$0.length;i_$3++) {
var c_$4 = children_$0[i_$3];
if (c_$4) {
if (notifyParent_$1 && c_$4.parentNode != null) {
if (c_$4.parentNode !== this) {
c_$4.parentNode.removeChild(c_$4)
}};
c_$4.$lzc$set_ownerDocument(this.ownerDocument);
c_$4.parentNode = this;
if (c_$4.onparentNode.ready) c_$4.onparentNode.sendEvent(this);
c_$4.__LZo = i_$3
}}};
this.__LZcoDirty = false;
if (this.onchildNodes.ready) this.onchildNodes.sendEvent(children_$0);
this.ownerDocument.handleDocumentChange("childNodes", this, 0)
};
$lzsc$temp["displayName"] = "$lzc$set_childNodes";
return $lzsc$temp
})(), "setChildNodes", (function () {
var $lzsc$temp = function (children_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_childNodes(children_$0)
};
$lzsc$temp["displayName"] = "setChildNodes";
return $lzsc$temp
})(), "$lzc$set_nodeName", (function () {
var $lzsc$temp = function (name_$0) {
this.nodeName = name_$0;
if (this.onnodeName.ready) this.onnodeName.sendEvent(name_$0);
if (this.parentNode) {
if (this.parentNode.onchildNodes.ready) this.parentNode.onchildNodes.sendEvent(this);
if (this.parentNode.onchildNode.ready) this.parentNode.onchildNode.sendEvent(this)
};
this.ownerDocument.handleDocumentChange("childNodeName", this.parentNode, 0);
this.ownerDocument.handleDocumentChange("nodeName", this, 1)
};
$lzsc$temp["displayName"] = "$lzc$set_nodeName";
return $lzsc$temp
})(), "setNodeName", (function () {
var $lzsc$temp = function (name_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_nodeName(name_$0)
};
$lzsc$temp["displayName"] = "setNodeName";
return $lzsc$temp
})(), "__LZgetText", (function () {
var $lzsc$temp = function () {
var s_$0 = "";
var cn_$1 = this.childNodes;
for (var i_$2 = 0, len_$3 = cn_$1.length;i_$2 < len_$3;i_$2++) {
var c_$4 = cn_$1[i_$2];
if (c_$4.nodeType == LzDataElement.TEXT_NODE) {
s_$0 += c_$4.data
}};
return s_$0
};
$lzsc$temp["displayName"] = "__LZgetText";
return $lzsc$temp
})(), "getElementsByTagName", (function () {
var $lzsc$temp = function (name_$0) {
var r_$1 = [];
var cn_$2 = this.childNodes;
for (var i_$3 = 0, len_$4 = cn_$2.length;i_$3 < len_$4;i_$3++) {
if (cn_$2[i_$3].nodeName == name_$0) {
r_$1.push(cn_$2[i_$3])
}};
return r_$1
};
$lzsc$temp["displayName"] = "getElementsByTagName";
return $lzsc$temp
})(), "__LZlt", "<", "__LZgt", ">", "serialize", (function () {
var $lzsc$temp = function () {
return this.serializeInternal()
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "serializeInternal", (function () {
var $lzsc$temp = function (len_$0) {
switch (arguments.length) {
case 0:
len_$0 = Infinity
};
var s_$1 = this.__LZlt + this.nodeName;
var attrs_$2 = this.attributes;
for (var k_$3 in attrs_$2) {
s_$1 += " " + k_$3 + '="' + LzDataElement.__LZXMLescape(attrs_$2[k_$3]) + '"';
if (s_$1.length > len_$0) {
break
}};
var cn_$4 = this.childNodes;
if (s_$1.length <= len_$0 && cn_$4.length) {
s_$1 += this.__LZgt;
for (var i_$5 = 0, cnlen_$6 = cn_$4.length;i_$5 < cnlen_$6;i_$5++) {
s_$1 += cn_$4[i_$5].serialize();
if (s_$1.length > len_$0) {
break
}};
s_$1 += this.__LZlt + "/" + this.nodeName + this.__LZgt
} else {
s_$1 += "/" + this.__LZgt
};
if (s_$1.length > len_$0) {
s_$1 = Debug.abbreviate(s_$1, len_$0)
};
return s_$1
};
$lzsc$temp["displayName"] = "serializeInternal";
return $lzsc$temp
})(), "_dbg_name", (function () {
var $lzsc$temp = function () {
return this.serializeInternal(Debug.printLength)
};
$lzsc$temp["displayName"] = "_dbg_name";
return $lzsc$temp
})(), "handleDocumentChange", (function () {
var $lzsc$temp = function (what_$0, who_$1, type_$2, cobj_$3) {
switch (arguments.length) {
case 3:
cobj_$3 = null
};
var o_$4 = {who: who_$1, what: what_$0, type: type_$2};
if (cobj_$3) o_$4.cobj = cobj_$3;
if (this.__LZchangeQ) {
this.__LZchangeQ.push(o_$4)
} else {
if (this.onDocumentChange.ready) this.onDocumentChange.sendEvent(o_$4)
}};
$lzsc$temp["displayName"] = "handleDocumentChange";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.serialize()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "__LZdoLock", (function () {
var $lzsc$temp = function (locker_$0) {
if (!this.__LZchangeQ) {
this.__LZchangeQ = [];
this.__LZlocker = locker_$0
}};
$lzsc$temp["displayName"] = "__LZdoLock";
return $lzsc$temp
})(), "__LZdoUnlock", (function () {
var $lzsc$temp = function (locker_$0) {
if (this.__LZlocker != locker_$0) {
return
};
var lzq_$1 = this.__LZchangeQ;
this.__LZchangeQ = null;
this.__LZlocker = null;
if (lzq_$1 != null) {
for (var i_$2 = 0, len_$3 = lzq_$1.length;i_$2 < len_$3;i_$2++) {
var sendit_$4 = true;
var tc_$5 = lzq_$1[i_$2];
for (var j_$6 = 0;j_$6 < i_$2;j_$6++) {
var oc_$7 = lzq_$1[j_$6];
if (tc_$5.who == oc_$7.who && tc_$5.what == oc_$7.what && tc_$5.type == oc_$7.type) {
sendit_$4 = false;
break
}};
if (sendit_$4) {
this.handleDocumentChange(tc_$5.what, tc_$5.who, tc_$5.type)
}}}};
$lzsc$temp["displayName"] = "__LZdoUnlock";
return $lzsc$temp
})()]);
lz.DataElementMixin = LzDataElementMixin;
Class.make("LzDataElement", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (name_$0, attributes_$1, children_$2) {
switch (arguments.length) {
case 1:
attributes_$1 = null;;case 2:
children_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.nodeName = name_$0;
this.nodeType = LzDataElement.ELEMENT_NODE;
this.ownerDocument = this;
if (attributes_$1) {
this.$lzc$set_attributes(attributes_$1)
} else {
this.attributes = {}};
if (children_$2) {
this.$lzc$set_childNodes(children_$2)
} else {
this.childNodes = [];
this.__LZcoDirty = false
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], [LzDataElementMixin, LzDataNodeMixin, LzDataNode], ["NODE_CLONED", 1, "NODE_IMPORTED", 2, "NODE_DELETED", 3, "NODE_RENAMED", 4, "NODE_ADOPTED", 5, "makeNodeList", (function () {
var $lzsc$temp = function (count_$0, name_$1) {
var a_$2 = [];
for (var i_$3 = 0;i_$3 < count_$0;i_$3++) {
a_$2[i_$3] = new LzDataElement(name_$1)
};
return a_$2
};
$lzsc$temp["displayName"] = "makeNodeList";
return $lzsc$temp
})(), "valueToElement", (function () {
var $lzsc$temp = function (o_$0) {
return new LzDataElement("element", {}, LzDataElement.__LZv2E(o_$0))
};
$lzsc$temp["displayName"] = "valueToElement";
return $lzsc$temp
})(), "__LZdateToJSON", (function () {
var $lzsc$temp = function (d_$0) {
var pad2_$1;
var pad3_$2;
pad2_$1 = (function () {
var $lzsc$temp = function (n_$0) {
return (n_$0 < 10 ? "0" : "") + n_$0
};
$lzsc$temp["displayName"] = "pad2";
return $lzsc$temp
})();
pad3_$2 = (function () {
var $lzsc$temp = function (n_$0) {
return (n_$0 < 10 ? "00" : (n_$0 < 100 ? "0" : "")) + n_$0
};
$lzsc$temp["displayName"] = "pad3";
return $lzsc$temp
})();
if (isFinite(d_$0.valueOf())) {
return d_$0.getUTCFullYear() + "-" + pad2_$1(d_$0.getUTCMonth() + 1) + "-" + pad2_$1(d_$0.getUTCDate()) + "T" + pad2_$1(d_$0.getUTCHours()) + ":" + pad2_$1(d_$0.getUTCMinutes()) + ":" + pad2_$1(d_$0.getUTCSeconds()) + "." + pad3_$2(d_$0.getUTCMilliseconds()) + "Z"
} else {
return null
}};
$lzsc$temp["displayName"] = "__LZdateToJSON";
return $lzsc$temp
})(), "__LZv2E", (function () {
var $lzsc$temp = function (o_$0) {
var c_$1 = [];
if (typeof o_$0 == "object") {
if ((LzDataElement["$lzsc$isa"] ? LzDataElement.$lzsc$isa(o_$0) : o_$0 instanceof LzDataElement) || (LzDataText["$lzsc$isa"] ? LzDataText.$lzsc$isa(o_$0) : o_$0 instanceof LzDataText)) {
c_$1[0] = o_$0
} else if (Date["$lzsc$isa"] ? Date.$lzsc$isa(o_$0) : o_$0 instanceof Date) {
var d_$2 = LzDataElement.__LZdateToJSON(o_$0);
if (d_$2 != null) {
c_$1[0] = new LzDataText(d_$2)
}} else if (Array["$lzsc$isa"] ? Array.$lzsc$isa(o_$0) : o_$0 instanceof Array) {
var tag_$3 = o_$0.__LZtag != null ? o_$0.__LZtag : "item";
for (var i_$4 = 0;i_$4 < o_$0.length;i_$4++) {
var tmpC_$5 = LzDataElement.__LZv2E(o_$0[i_$4]);
c_$1[i_$4] = new LzDataElement(tag_$3, null, tmpC_$5)
}} else {
var i_$4 = 0;
for (var k_$6 in o_$0) {
if (k_$6.indexOf("__LZ") == 0) continue;
c_$1[i_$4++] = new LzDataElement(k_$6, null, LzDataElement.__LZv2E(o_$0[k_$6]))
}}} else if (o_$0 != null) {
c_$1[0] = new LzDataText(String(o_$0))
};
return c_$1.length != 0 ? c_$1 : null
};
$lzsc$temp["displayName"] = "__LZv2E";
return $lzsc$temp
})(), "ELEMENT_NODE", 1, "TEXT_NODE", 3, "DOCUMENT_NODE", 9, "__LZescapechars", {"&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&apos;"}, "__LZXMLescape", (function () {
var $lzsc$temp = function (t_$0) {
if (typeof t_$0 != "string") return t_$0;
var escChars_$1 = LzDataElement.__LZescapechars;
var olen_$2 = t_$0.length;
var r_$3 = "";
for (var i_$4 = 0;i_$4 < olen_$2;i_$4++) {
var code_$5 = t_$0.charCodeAt(i_$4);
if (code_$5 < 32) {
r_$3 += "&#x" + code_$5.toString(16) + ";"
} else {
var c_$6 = t_$0.charAt(i_$4);
r_$3 += escChars_$1[c_$6] || c_$6
}};
return r_$3
};
$lzsc$temp["displayName"] = "__LZXMLescape";
return $lzsc$temp
})(), "stringToLzData", (function () {
var $lzsc$temp = function (str_$0, trimwhitespace_$1, nsprefix_$2) {
switch (arguments.length) {
case 1:
trimwhitespace_$1 = false;;case 2:
nsprefix_$2 = false
};
if (str_$0 != null && str_$0 != "") {
var nativexml_$3;
try {
nativexml_$3 = LzXMLParser.parseXML(str_$0, trimwhitespace_$1, nsprefix_$2)
}
catch (e_$4) {
Debug.warn("Cannot parse xml-string '%s': %w", str_$0, e_$4)
};
if (nativexml_$3 != null) {
var lfcnode_$5 = LzXMLTranslator.copyXML(nativexml_$3, trimwhitespace_$1, nsprefix_$2);
return lfcnode_$5
}};
return null
};
$lzsc$temp["displayName"] = "stringToLzData";
return $lzsc$temp
})(), "whitespaceChars", {" ": true, "\r": true, "\n": true, "\t": true}, "trim", (function () {
var $lzsc$temp = function (str_$0) {
var whitech_$1 = LzDataElement.whitespaceChars;
var len_$2 = str_$0.length;
var sindex_$3 = 0;
var eindex_$4 = len_$2 - 1;
var ch_$5;
while (sindex_$3 < len_$2) {
ch_$5 = str_$0.charAt(sindex_$3);
if (whitech_$1[ch_$5] != true) break;
sindex_$3++
};
while (eindex_$4 > sindex_$3) {
ch_$5 = str_$0.charAt(eindex_$4);
if (whitech_$1[ch_$5] != true) break;
eindex_$4--
};
return str_$0.slice(sindex_$3, eindex_$4 + 1)
};
$lzsc$temp["displayName"] = "trim";
return $lzsc$temp
})()]);
lz.DataElement = LzDataElement;
Class.make("LzDataText", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (text_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.nodeType = LzDataElement.TEXT_NODE;
this.data = text_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ondata", LzDeclaredEvent, "nodeName", "#text", "data", "", "$lzc$set_data", (function () {
var $lzsc$temp = function (newdata_$0) {
this.data = newdata_$0;
if (this.ondata.ready) {
this.ondata.sendEvent(newdata_$0)
};
if (this.ownerDocument) {
this.ownerDocument.handleDocumentChange("data", this, 1)
}};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "setData", (function () {
var $lzsc$temp = function (newdata_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_data(newdata_$0)
};
$lzsc$temp["displayName"] = "setData";
return $lzsc$temp
})(), "cloneNode", (function () {
var $lzsc$temp = function (deep_$0) {
switch (arguments.length) {
case 0:
deep_$0 = false
};
var n_$1 = new LzDataText(this.data);
return n_$1
};
$lzsc$temp["displayName"] = "cloneNode";
return $lzsc$temp
})(), "serialize", (function () {
var $lzsc$temp = function () {
return LzDataElement.__LZXMLescape(this.data)
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.data
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "_dbg_name", (function () {
var $lzsc$temp = function () {
var s_$0 = this.serialize();
var len_$1 = Debug.printLength;
if (s_$0.length > len_$1) {
s_$0 = Debug.abbreviate(s_$0, len_$1)
};
return s_$0
};
$lzsc$temp["displayName"] = "_dbg_name";
return $lzsc$temp
})()], [LzDataNodeMixin, LzDataNode]);
lz.DataText = LzDataText;
Class.make("LzDataRequest", ["requestor", null, "src", null, "timeout", Infinity, "status", null, "rawdata", null, "error", null, "onstatus", LzDeclaredEvent, "$lzsc$initialize", (function () {
var $lzsc$temp = function (requestor_$0) {
switch (arguments.length) {
case 0:
requestor_$0 = null
};
this.requestor = requestor_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzEventable, ["SUCCESS", "success", "TIMEOUT", "timeout", "ERROR", "error", "READY", "ready", "LOADING", "loading"]);
lz.DataRequest = LzDataRequest;
Class.make("LzDataProvider", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "doRequest", (function () {
var $lzsc$temp = function (dreq_$0) {};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "abortLoadForRequest", (function () {
var $lzsc$temp = function (dreq_$0) {};
$lzsc$temp["displayName"] = "abortLoadForRequest";
return $lzsc$temp
})()], LzEventable);
lz.DataProvider = LzDataProvider;
Class.make("LzHTTPDataRequest", ["method", "GET", "postbody", void 0, "proxied", void 0, "proxyurl", void 0, "multirequest", false, "queuerequests", false, "queryparams", null, "requestheaders", null, "getresponseheaders", false, "responseheaders", void 0, "cacheable", false, "clientcacheable", false, "trimwhitespace", false, "nsprefix", false, "serverproxyargs", null, "xmldata", null, "loadtime", 0, "loadstarttime", void 0, "secure", false, "secureport", void 0, "parsexml", true, "loader", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (requestor_$0) {
switch (arguments.length) {
case 0:
requestor_$0 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, requestor_$0)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzDataRequest);
lz.HTTPDataRequest = LzHTTPDataRequest;
Class.make("LzHTTPDataProvider", ["__loaders", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "makeLoader", (function () {
var $lzsc$temp = function (dreq_$0) {
var proxied_$1 = dreq_$0.proxied;
if (this.__loaders == null) this.__loaders = [];
var tloader_$2 = new LzHTTPLoader(this, proxied_$1);
this.__loaders.push(tloader_$2);
dreq_$0.loader = tloader_$2;
tloader_$2.loadSuccess = this.loadSuccess;
tloader_$2.loadError = this.loadError;
tloader_$2.loadTimeout = this.loadTimeout;
tloader_$2.setProxied(proxied_$1);
var secure_$3 = dreq_$0.secure;
if (secure_$3 == null) {
if (dreq_$0.src.substring(0, 5) == "https") {
secure_$3 = true
}};
tloader_$2.secure = secure_$3;
if (secure_$3) {
tloader_$2.baserequest = lz.Browser.getBaseURL(secure_$3, dreq_$0.secureport);
tloader_$2.secureport = dreq_$0.secureport
};
return tloader_$2
};
$lzsc$temp["displayName"] = "makeLoader";
return $lzsc$temp
})(), "abortLoadForRequest", (function () {
var $lzsc$temp = function (dreq_$0) {
dreq_$0.loader.abort()
};
$lzsc$temp["displayName"] = "abortLoadForRequest";
return $lzsc$temp
})(), "doRequest", (function () {
var $lzsc$temp = function (dreq_$0) {
var httpdreq_$1 = dreq_$0;
if (!httpdreq_$1.src) return;
var proxied_$2 = httpdreq_$1.proxied;
var tloader_$3 = httpdreq_$1.loader;
if (tloader_$3 == null || httpdreq_$1.multirequest == true || httpdreq_$1.queuerequests == true) {
tloader_$3 = this.makeLoader(httpdreq_$1)
};
tloader_$3.dataRequest = httpdreq_$1;
tloader_$3.setQueueing(httpdreq_$1.queuerequests);
tloader_$3.setTimeout(httpdreq_$1.timeout);
tloader_$3.setOption("serverproxyargs", httpdreq_$1.serverproxyargs);
tloader_$3.setOption("cacheable", httpdreq_$1.cacheable == true);
tloader_$3.setOption("timeout", httpdreq_$1.timeout);
tloader_$3.setOption("trimwhitespace", httpdreq_$1.trimwhitespace == true);
tloader_$3.setOption("nsprefix", httpdreq_$1.nsprefix == true);
tloader_$3.setOption("sendheaders", httpdreq_$1.getresponseheaders == true);
tloader_$3.setOption("parsexml", httpdreq_$1.parsexml);
if (httpdreq_$1.clientcacheable != null) {
tloader_$3.setOption("ccache", httpdreq_$1.clientcacheable)
};
var headers_$4 = {};
var headerparams_$5 = httpdreq_$1.requestheaders;
if (headerparams_$5 != null) {
var headernames_$6 = headerparams_$5.getNames();
for (var i_$7 = 0;i_$7 < headernames_$6.length;i_$7++) {
var key_$8 = headernames_$6[i_$7];
var val_$9 = headerparams_$5.getValue(key_$8);
if (proxied_$2) {
headers_$4[key_$8] = val_$9
} else {
tloader_$3.setRequestHeader(key_$8, val_$9)
}}};
var qparams_$a = httpdreq_$1.queryparams;
var hasquerydata_$b = true;
var postbody_$c = httpdreq_$1.postbody;
if (postbody_$c == null && qparams_$a != null) {
postbody_$c = qparams_$a.serialize("=", "&", true)
} else {
hasquerydata_$b = false
};
tloader_$3.setOption("hasquerydata", hasquerydata_$b);
var lzurl_$d = new LzURL(httpdreq_$1.src);
if (httpdreq_$1.method == "GET") {
if (lzurl_$d.query == null) {
lzurl_$d.query = postbody_$c
} else {
if (postbody_$c != null) {
lzurl_$d.query += "&" + postbody_$c
}};
postbody_$c = null
};
var cachebreak_$e = "__lzbc__=" + new Date().getTime();
if (!proxied_$2 && httpdreq_$1.method == "POST" && (postbody_$c == null || postbody_$c == "")) {
postbody_$c = cachebreak_$e
};
var url_$f;
if (proxied_$2) {
url_$f = tloader_$3.makeProxiedURL(httpdreq_$1.proxyurl, lzurl_$d.toString(), httpdreq_$1.method, "xmldata", headers_$4, postbody_$c);
var marker_$g = url_$f.indexOf("?");
var uquery_$h = url_$f.substring(marker_$g + 1, url_$f.length);
var url_noquery_$i = url_$f.substring(0, marker_$g);
url_$f = url_noquery_$i + "?" + cachebreak_$e;
postbody_$c = uquery_$h
} else {
if (!httpdreq_$1.clientcacheable) {
if (httpdreq_$1.method == "GET") {
if (lzurl_$d.query == null) {
lzurl_$d.query = cachebreak_$e
} else {
lzurl_$d.query += "&" + cachebreak_$e
}}};
url_$f = lzurl_$d.toString()
};
httpdreq_$1.loadstarttime = new Date().getTime();
httpdreq_$1.status = LzDataRequest.LOADING;
tloader_$3.open(proxied_$2 ? "POST" : httpdreq_$1.method, url_$f, null, null);
tloader_$3.send(postbody_$c)
};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "loadSuccess", (function () {
var $lzsc$temp = function (loader_$0, data_$1) {
var dreq_$2 = loader_$0.dataRequest;
dreq_$2.status = LzDataRequest.SUCCESS;
loader_$0.owner.loadResponse(dreq_$2, data_$1)
};
$lzsc$temp["displayName"] = "loadSuccess";
return $lzsc$temp
})(), "loadError", (function () {
var $lzsc$temp = function (loader_$0, data_$1) {
var dreq_$2 = loader_$0.dataRequest;
dreq_$2.status = LzDataRequest.ERROR;
loader_$0.owner.loadResponse(dreq_$2, data_$1)
};
$lzsc$temp["displayName"] = "loadError";
return $lzsc$temp
})(), "loadTimeout", (function () {
var $lzsc$temp = function (loader_$0, data_$1) {
var dreq_$2 = loader_$0.dataRequest;
dreq_$2.loadtime = new Date().getTime() - dreq_$2.loadstarttime;
dreq_$2.status = LzDataRequest.TIMEOUT;
if (dreq_$2.onstatus.ready) dreq_$2.onstatus.sendEvent(dreq_$2)
};
$lzsc$temp["displayName"] = "loadTimeout";
return $lzsc$temp
})(), "setRequestError", (function () {
var $lzsc$temp = function (dreq_$0, msg_$1) {
dreq_$0.error = msg_$1;
dreq_$0.status = LzDataRequest.ERROR
};
$lzsc$temp["displayName"] = "setRequestError";
return $lzsc$temp
})(), "loadResponse", (function () {
var $lzsc$temp = function (dreq_$0, data_$1) {
dreq_$0.loadtime = new Date().getTime() - dreq_$0.loadstarttime;
dreq_$0.rawdata = dreq_$0.loader.getResponse();
if (data_$1 == null) {
this.setRequestError(dreq_$0, "client could not parse XML from server");
if (dreq_$0.onstatus.ready) dreq_$0.onstatus.sendEvent(dreq_$0);
return
};
var proxied_$2 = dreq_$0.proxied;
if (!dreq_$0.parsexml) {
if (dreq_$0.onstatus.ready) dreq_$0.onstatus.sendEvent(dreq_$0);
return
} else if (proxied_$2 && data_$1.childNodes[0].nodeName == "error") {
this.setRequestError(dreq_$0, data_$1.childNodes[0].attributes["msg"]);
if (dreq_$0.onstatus.ready) dreq_$0.onstatus.sendEvent(dreq_$0);
return
};
var headers_$3 = new (lz.Param)();
var content_$4 = null;
if (proxied_$2) {
var hos_$5 = data_$1.childNodes.length > 1 && data_$1.childNodes[1].childNodes ? data_$1.childNodes[1].childNodes : null;
if (hos_$5 != null) {
for (var i_$6 = 0;i_$6 < hos_$5.length;i_$6++) {
var h_$7 = hos_$5[i_$6];
if (h_$7.attributes) headers_$3.addValue(h_$7.attributes.name, h_$7.attributes.value)
}};
if (data_$1.childNodes[0].childNodes) content_$4 = data_$1.childNodes[0].childNodes[0]
} else {
var hos_$5 = dreq_$0.loader.getResponseHeaders();
if (hos_$5) {
headers_$3.addObject(hos_$5)
};
content_$4 = data_$1
};
dreq_$0.xmldata = content_$4;
dreq_$0.responseheaders = headers_$3;
if (dreq_$0.onstatus.ready) dreq_$0.onstatus.sendEvent(dreq_$0)
};
$lzsc$temp["displayName"] = "loadResponse";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
for (var i_$0 = this.__loaders.length - 1;i_$0 > -1;i_$0--) {
this.__loaders[i_$0].destroy()
};
this.__loaders = null;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})()], LzDataProvider);
lz.HTTPDataProvider = LzHTTPDataProvider;
Class.make("LzDataset", ["rawdata", null, "dataprovider", void 0, "multirequest", false, "dataRequest", null, "dataRequestClass", LzHTTPDataRequest, "dsloadDel", null, "errorstring", void 0, "reqOnInitDel", void 0, "secureport", void 0, "proxyurl", null, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "timeout", 60000, "$lzc$set_timeout", (function () {
var $lzsc$temp = function (t_$0) {
this.timeout = t_$0
};
$lzsc$temp["displayName"] = "$lzc$set_timeout";
return $lzsc$temp
})(), "postbody", null, "$lzc$set_postbody", (function () {
var $lzsc$temp = function (s_$0) {
this.postbody = s_$0
};
$lzsc$temp["displayName"] = "$lzc$set_postbody";
return $lzsc$temp
})(), "acceptencodings", false, "type", null, "params", null, "nsprefix", false, "getresponseheaders", false, "querytype", "GET", "$lzc$set_querytype", (function () {
var $lzsc$temp = function (reqtype_$0) {
this.querytype = reqtype_$0.toUpperCase()
};
$lzsc$temp["displayName"] = "$lzc$set_querytype";
return $lzsc$temp
})(), "trimwhitespace", false, "cacheable", false, "clientcacheable", false, "querystring", null, "src", null, "$lzc$set_src", (function () {
var $lzsc$temp = function (src_$0) {
this.src = src_$0;
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "$lzc$set_src";
return $lzsc$temp
})(), "autorequest", false, "$lzc$set_autorequest", (function () {
var $lzsc$temp = function (b_$0) {
this.autorequest = b_$0
};
$lzsc$temp["displayName"] = "$lzc$set_autorequest";
return $lzsc$temp
})(), "request", false, "$lzc$set_request", (function () {
var $lzsc$temp = function (b_$0) {
this.request = b_$0;
if (b_$0 && !this.isinited) {
this.reqOnInitDel = new LzDelegate(this, "doRequest", this, "oninit")
}};
$lzsc$temp["displayName"] = "$lzc$set_request";
return $lzsc$temp
})(), "headers", null, "proxied", null, "$lzc$set_proxied", (function () {
var $lzsc$temp = function (val_$0) {
var nval_$1 = {"true": true, "false": false, "null": null, inherit: null}[val_$0];
if (nval_$1 !== void 0) {
this.proxied = nval_$1
} else {
Debug.warn("%w.proxied must be one of 'inherit', 'true', or 'false', but was %w", this, val_$0)
}};
$lzsc$temp["displayName"] = "$lzc$set_proxied";
return $lzsc$temp
})(), "isProxied", (function () {
var $lzsc$temp = function () {
return this.proxied != null ? this.proxied : canvas.proxied
};
$lzsc$temp["displayName"] = "isProxied";
return $lzsc$temp
})(), "responseheaders", null, "queuerequests", false, "oncanvas", void 0, "$lzc$set_initialdata", (function () {
var $lzsc$temp = function (d_$0) {
if (d_$0 != null) {
var e_$1 = LzDataElement.stringToLzData(d_$0, this.trimwhitespace, this.nsprefix);
if (e_$1 != null) {
this.$lzc$set_data(e_$1.childNodes)
}}};
$lzsc$temp["displayName"] = "$lzc$set_initialdata";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, args_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
args_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, args_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (parent_$0, args_$1) {
this.nodeType = LzDataElement.DOCUMENT_NODE;
this.ownerDocument = this;
this.attributes = {};
this.childNodes = [];
this.queuerequests = false;
this.oncanvas = parent_$0 == canvas || parent_$0 == null;
if (!("proxyurl" in args_$1)) {
this.proxyurl = canvas.getProxyURL()
};
if (("timeout" in args_$1) && args_$1.timeout) {
this.timeout = args_$1.timeout
} else {
this.timeout = canvas.dataloadtimeout
};
if (("dataprovider" in args_$1) && args_$1.dataprovider) {
this.dataprovider = args_$1.dataprovider
} else {
this.dataprovider = canvas.defaultdataprovider
};
if ("autorequest" in args_$1) {
this.autorequest = args_$1.autorequest
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, parent_$0, args_$1)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "$lzc$set_name", (function () {
var $lzsc$temp = function (name_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzc$set_name"] || this.nextMethod(arguments.callee, "$lzc$set_name")).call(this, name_$0);
if (name_$0 != null) {
this.nodeName = name_$0;
if (this.oncanvas) {
canvas[name_$0] = this
} else {
name_$0 = this.parent.getUID() + "." + name_$0
};
if (canvas.datasets[name_$0] != null) {
Debug.warn("A dataset already exists with the name '%s': %w", name_$0, canvas.datasets[name_$0])
};
canvas.datasets[name_$0] = this
}};
$lzsc$temp["displayName"] = "$lzc$set_name";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
this.$lzc$set_childNodes([]);
this.dataRequest = null;
var name_$0 = this.name;
if (this.oncanvas) {
if (canvas[name_$0] === this) {
delete canvas[name_$0]
}} else {
name_$0 = this.parent.getUID() + "." + name_$0
};
if (canvas.datasets[name_$0] === this) {
delete canvas.datasets[name_$0]
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "getErrorString", (function () {
var $lzsc$temp = function () {
return this.errorstring
};
$lzsc$temp["displayName"] = "getErrorString";
return $lzsc$temp
})(), "getLoadTime", (function () {
var $lzsc$temp = function () {
var dreq_$0 = this.dataRequest;
return dreq_$0 ? dreq_$0.loadtime : 0
};
$lzsc$temp["displayName"] = "getLoadTime";
return $lzsc$temp
})(), "getSrc", (function () {
var $lzsc$temp = function () {
return this.src
};
$lzsc$temp["displayName"] = "getSrc";
return $lzsc$temp
})(), "getQueryString", (function () {
var $lzsc$temp = function () {
if (typeof this.querystring == "undefined") {
return ""
} else {
return this.querystring
}};
$lzsc$temp["displayName"] = "getQueryString";
return $lzsc$temp
})(), "getParams", (function () {
var $lzsc$temp = function () {
if (this.params == null) {
this.params = new (lz.Param)()
};
return this.params
};
$lzsc$temp["displayName"] = "getParams";
return $lzsc$temp
})(), "$lzc$set_data", (function () {
var $lzsc$temp = function (data_$0) {
if (data_$0 == null) {
return
} else if (data_$0 instanceof Array) {
this.$lzc$set_childNodes(data_$0)
} else {
this.$lzc$set_childNodes([data_$0])
};
this.data = data_$0;
if (this.ondata.ready) this.ondata.sendEvent(this)
};
$lzsc$temp["displayName"] = "$lzc$set_data";
return $lzsc$temp
})(), "gotError", (function () {
var $lzsc$temp = function (e_$0) {
this.errorstring = e_$0;
if (this.onerror.ready) this.onerror.sendEvent(this)
};
$lzsc$temp["displayName"] = "gotError";
return $lzsc$temp
})(), "gotTimeout", (function () {
var $lzsc$temp = function () {
if (this.ontimeout.ready) this.ontimeout.sendEvent(this)
};
$lzsc$temp["displayName"] = "gotTimeout";
return $lzsc$temp
})(), "getContext", (function () {
var $lzsc$temp = function (chgpkg_$0) {
switch (arguments.length) {
case 0:
chgpkg_$0 = null
};
return this
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "getDataset", (function () {
var $lzsc$temp = function () {
return this
};
$lzsc$temp["displayName"] = "getDataset";
return $lzsc$temp
})(), "getPointer", (function () {
var $lzsc$temp = function () {
var dp_$0 = new LzDatapointer(null);
dp_$0.p = this.getContext();
return dp_$0
};
$lzsc$temp["displayName"] = "getPointer";
return $lzsc$temp
})(), "setQueryString", (function () {
var $lzsc$temp = function (s_$0) {
this.params = null;
if (typeof s_$0 == "object") {
if (s_$0 instanceof lz.Param) {
this.querystring = s_$0.toString()
} else {
var p_$1 = new (lz.Param)();
for (var n_$2 in s_$0) {
p_$1.setValue(n_$2, s_$0[n_$2], true)
};
this.querystring = p_$1.toString();
p_$1.destroy()
}} else {
this.querystring = s_$0
};
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryString";
return $lzsc$temp
})(), "setQueryParam", (function () {
var $lzsc$temp = function (key_$0, val_$1) {
this.querystring = null;
if (!this.params) {
this.params = new (lz.Param)()
};
this.params.setValue(key_$0, val_$1);
if (this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryParam";
return $lzsc$temp
})(), "setQueryParams", (function () {
var $lzsc$temp = function (obj_$0) {
this.querystring = null;
if (!this.params) {
this.params = new (lz.Param)()
};
if (obj_$0) {
this.params.addObject(obj_$0)
} else if (obj_$0 == null) {
this.params.remove()
};
if (obj_$0 && this.autorequest) {
this.doRequest()
}};
$lzsc$temp["displayName"] = "setQueryParams";
return $lzsc$temp
})(), "setSrc", (function () {
var $lzsc$temp = function (src_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_src(src_$0)
};
$lzsc$temp["displayName"] = "setSrc";
return $lzsc$temp
})(), "setProxyRequests", (function () {
var $lzsc$temp = function (val_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_proxied(val_$0)
};
$lzsc$temp["displayName"] = "setProxyRequests";
return $lzsc$temp
})(), "setRequest", (function () {
var $lzsc$temp = function (b_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_request(b_$0)
};
$lzsc$temp["displayName"] = "setRequest";
return $lzsc$temp
})(), "setAutorequest", (function () {
var $lzsc$temp = function (b_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_autorequest(b_$0)
};
$lzsc$temp["displayName"] = "setAutorequest";
return $lzsc$temp
})(), "setPostBody", (function () {
var $lzsc$temp = function (str_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_postbody(str_$0)
};
$lzsc$temp["displayName"] = "setPostBody";
return $lzsc$temp
})(), "setQueryType", (function () {
var $lzsc$temp = function (reqtype_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_querytype(reqtype_$0)
};
$lzsc$temp["displayName"] = "setQueryType";
return $lzsc$temp
})(), "setInitialData", (function () {
var $lzsc$temp = function (d_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_initialdata(d_$0)
};
$lzsc$temp["displayName"] = "setInitialData";
return $lzsc$temp
})(), "abort", (function () {
var $lzsc$temp = function () {
var dreq_$0 = this.dataRequest;
if (dreq_$0) {
this.dataprovider.abortLoadForRequest(dreq_$0)
}};
$lzsc$temp["displayName"] = "abort";
return $lzsc$temp
})(), "doRequest", (function () {
var $lzsc$temp = function (ignore_$0) {
switch (arguments.length) {
case 0:
ignore_$0 = null
};
if (!this.src) return;
if (this.multirequest || this.dataRequest == null || this.queuerequests) {
this.dataRequest = new (this.dataRequestClass)(this)
};
var dreq_$1 = this.dataRequest;
dreq_$1.src = this.src;
dreq_$1.timeout = this.timeout;
dreq_$1.status = LzDataRequest.READY;
dreq_$1.method = this.querytype;
dreq_$1.postbody = null;
if (this.querystring) {
dreq_$1.queryparams = new (lz.Param)();
dreq_$1.queryparams.addObject(lz.Param.parseQueryString(this.querystring))
} else {
dreq_$1.queryparams = this.params
};
if (this.querytype.toUpperCase() == "POST") {
dreq_$1.postbody = this.postbody;
if (dreq_$1.queryparams) {
var lzpostbody_$2 = dreq_$1.queryparams.getValue("lzpostbody");
if (lzpostbody_$2 != null) {
dreq_$1.queryparams.remove("lzpostbody");
dreq_$1.postbody = lzpostbody_$2
}}};
dreq_$1.proxied = this.isProxied();
dreq_$1.proxyurl = this.proxyurl;
dreq_$1.queuerequests = this.queuerequests;
dreq_$1.requestheaders = this.headers;
dreq_$1.getresponseheaders = this.getresponseheaders;
dreq_$1.secureport = this.secureport;
dreq_$1.cacheable = this.cacheable;
dreq_$1.clientcacheable = this.clientcacheable;
dreq_$1.trimwhitespace = this.trimwhitespace;
dreq_$1.nsprefix = this.nsprefix;
if (this.dsloadDel == null) {
this.dsloadDel = new LzDelegate(this, "handleDataResponse", dreq_$1, "onstatus")
} else {
this.dsloadDel.register(dreq_$1, "onstatus")
};
this.dataprovider.doRequest(dreq_$1)
};
$lzsc$temp["displayName"] = "doRequest";
return $lzsc$temp
})(), "handleDataResponse", (function () {
var $lzsc$temp = function (datareq_$0) {
if (this.dsloadDel != null) {
this.dsloadDel.unregisterFrom(datareq_$0.onstatus)
};
this.rawdata = datareq_$0.rawdata;
this.errorstring = null;
if (datareq_$0.status == LzDataRequest.SUCCESS) {
if (this.responseheaders != null) {
this.responseheaders.destroy()
};
this.responseheaders = datareq_$0.responseheaders;
this.$lzc$set_data(datareq_$0.xmldata)
} else if (datareq_$0.status == LzDataRequest.ERROR) {
this.gotError(datareq_$0.error)
} else if (datareq_$0.status == LzDataRequest.TIMEOUT) {
this.gotTimeout()
}};
$lzsc$temp["displayName"] = "handleDataResponse";
return $lzsc$temp
})(), "setHeader", (function () {
var $lzsc$temp = function (k_$0, val_$1) {
if (!this.headers) {
this.headers = new (lz.Param)()
};
this.headers.setValue(k_$0, val_$1)
};
$lzsc$temp["displayName"] = "setHeader";
return $lzsc$temp
})(), "getRequestHeaderParams", (function () {
var $lzsc$temp = function () {
return this.headers
};
$lzsc$temp["displayName"] = "getRequestHeaderParams";
return $lzsc$temp
})(), "clearRequestHeaderParams", (function () {
var $lzsc$temp = function () {
if (this.headers) {
this.headers.remove()
}};
$lzsc$temp["displayName"] = "clearRequestHeaderParams";
return $lzsc$temp
})(), "getResponseHeader", (function () {
var $lzsc$temp = function (name_$0) {
var headers_$1 = this.responseheaders;
if (headers_$1) {
var val_$2 = headers_$1.getValues(name_$0);
if (val_$2 && val_$2.length == 1) {
return val_$2[0]
} else {
return val_$2
}};
return void 0
};
$lzsc$temp["displayName"] = "getResponseHeader";
return $lzsc$temp
})(), "getAllResponseHeaders", (function () {
var $lzsc$temp = function () {
return this.responseheaders
};
$lzsc$temp["displayName"] = "getAllResponseHeaders";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "LzDataset " + ":" + this.name
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], [LzDataElementMixin, LzDataNodeMixin, LzNode], ["tagname", "dataset", "attributes", new LzInheritedHash(LzNode.attributes), "slashPat", "/", "queryStringToTable", (function () {
var $lzsc$temp = function (query_$0) {
var queries_$1 = {};
var parameters_$2 = query_$0.split("&");
for (var i_$3 = 0;i_$3 < parameters_$2.length;++i_$3) {
var key_$4 = parameters_$2[i_$3];
var value_$5 = "";
var n_$6 = key_$4.indexOf("=");
if (n_$6 > 0) {
value_$5 = unescape(key_$4.substring(n_$6 + 1));
key_$4 = key_$4.substring(0, n_$6)
};
if (key_$4 in queries_$1) {
var prev_$7 = queries_$1[key_$4];
if (prev_$7 instanceof Array) {
prev_$7.push(value_$5)
} else {
queries_$1[key_$4] = [prev_$7, value_$5]
}} else {
queries_$1[key_$4] = value_$5
}};
return queries_$1
};
$lzsc$temp["displayName"] = "queryStringToTable";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzDataset.attributes.name = "localdata"
}}};
$lzsc$temp["displayName"] = "data/LzDataset.lzs#89/1";
return $lzsc$temp
})()(LzDataset);
lz[LzDataset.tagname] = LzDataset;
Class.make("__LzHttpDatasetPoolClass", ["_uid", 0, "_p", [], "get", (function () {
var $lzsc$temp = function (dataDel_$0, errorDel_$1, timeoutDel_$2, acceptenc_$3) {
switch (arguments.length) {
case 0:
dataDel_$0 = null;;case 1:
errorDel_$1 = null;;case 2:
timeoutDel_$2 = null;;case 3:
acceptenc_$3 = false
};
var dset_$4;
if (this._p.length > 0) {
dset_$4 = this._p.pop()
} else {
dset_$4 = new LzDataset(null, {name: "LzHttpDatasetPool" + this._uid, type: "http", acceptencodings: acceptenc_$3});
this._uid++
};
if (dataDel_$0 != null) {
dataDel_$0.register(dset_$4, "ondata")
};
if (errorDel_$1 != null) {
errorDel_$1.register(dset_$4, "onerror")
};
if (timeoutDel_$2 != null) {
timeoutDel_$2.register(dset_$4, "ontimeout")
};
return dset_$4
};
$lzsc$temp["displayName"] = "get";
return $lzsc$temp
})(), "recycle", (function () {
var $lzsc$temp = function (dset_$0) {
dset_$0.setQueryParams(null);
dset_$0.$lzc$set_postbody(null);
dset_$0.clearRequestHeaderParams();
dset_$0.ondata.clearDelegates();
dset_$0.ontimeout.clearDelegates();
dset_$0.onerror.clearDelegates();
dset_$0.$lzc$set_data([]);
this._p.push(dset_$0)
};
$lzsc$temp["displayName"] = "recycle";
return $lzsc$temp
})()]);
var LzHttpDatasetPool = new __LzHttpDatasetPoolClass();
Class.make("LzDatapointer", ["$lzc$set_xpath", (function () {
var $lzsc$temp = function (v_$0) {
this.setXPath(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_xpath";
return $lzsc$temp
})(), "$lzc$set_context", (function () {
var $lzsc$temp = function (v_$0) {
this.setDataContext(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_context";
return $lzsc$temp
})(), "$lzc$set_pointer", (function () {
var $lzsc$temp = function (v_$0) {
this.setPointer(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_pointer";
return $lzsc$temp
})(), "$lzc$set_p", (function () {
var $lzsc$temp = function (v_$0) {
this.setPointer(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_p";
return $lzsc$temp
})(), "p", null, "context", null, "__LZtracking", null, "__LZtrackDel", null, "xpath", null, "parsedPath", null, "__LZlastdotdot", null, "__LZspecialDotDot", false, "__LZdotdotCheckDel", null, "errorDel", null, "timeoutDel", null, "rerunxpath", false, "onp", LzDeclaredEvent, "onDocumentChange", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "onrerunxpath", LzDeclaredEvent, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 0:
parent_$0 = null;;case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "gotError", (function () {
var $lzsc$temp = function (ds_$0) {
if (this.onerror.ready) this.onerror.sendEvent(ds_$0)
};
$lzsc$temp["displayName"] = "gotError";
return $lzsc$temp
})(), "gotTimeout", (function () {
var $lzsc$temp = function (ds_$0) {
if (this.ontimeout.ready) this.ontimeout.sendEvent(ds_$0)
};
$lzsc$temp["displayName"] = "gotTimeout";
return $lzsc$temp
})(), "xpathQuery", (function () {
var $lzsc$temp = function (p_$0) {
var pp_$1 = this.parsePath(p_$0);
var ppcontext_$2 = pp_$1.getContext(this);
var nodes_$3 = this.__LZgetNodes(pp_$1, ppcontext_$2 ? ppcontext_$2 : this.p);
if (nodes_$3 == null) return null;
if (pp_$1.aggOperator != null) {
if (pp_$1.aggOperator == "last") {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$3) : nodes_$3 instanceof Array) {
return nodes_$3.length
} else {
if (!ppcontext_$2 && nodes_$3 === this.p) {
if (pp_$1.selectors && pp_$1.selectors.length > 0) {
var sel_$4 = pp_$1.selectors;
var i_$5 = 0;
while (sel_$4[i_$5] == "." && i_$5 < sel_$4.length) {
++i_$5
};
return i_$5 != sel_$4.length ? 1 : this.__LZgetLast()
} else {
return this.__LZgetLast()
}} else {
return 1
}}} else if (pp_$1.aggOperator == "position") {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$3) : nodes_$3 instanceof Array) {
var rarr_$6 = [];
for (var i_$5 = 0;i_$5 < nodes_$3.length;i_$5++) {
rarr_$6.push(i_$5 + 1)
};
return rarr_$6
} else {
if (!ppcontext_$2 && nodes_$3 === this.p) {
if (pp_$1.selectors && pp_$1.selectors.length > 0) {
var sel_$4 = pp_$1.selectors;
var i_$5 = 0;
while (sel_$4[i_$5] == "." && i_$5 < sel_$4.length) {
++i_$5
};
return i_$5 != sel_$4.length ? 1 : this.__LZgetPosition()
} else {
return this.__LZgetPosition()
}} else {
return 1
}}}} else if (pp_$1.operator != null) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(nodes_$3) : nodes_$3 instanceof Array) {
var oarr_$7 = [];
for (var i_$5 = 0;i_$5 < nodes_$3.length;i_$5++) {
oarr_$7.push(this.__LZprocessOperator(nodes_$3[i_$5], pp_$1))
};
return oarr_$7
} else {
return this.__LZprocessOperator(nodes_$3, pp_$1)
}} else {
return nodes_$3
}};
$lzsc$temp["displayName"] = "xpathQuery";
return $lzsc$temp
})(), "$lzc$xpathQuery_dependencies", (function () {
var $lzsc$temp = function (who_$0, self_$1, p_$2) {
if (this["parsePath"]) {
var pp_$3 = this.parsePath(p_$2);
return [pp_$3.hasDotDot ? self_$1.context.getContext().ownerDocument : self_$1, "DocumentChange"]
} else {
return [self_$1, "DocumentChange"]
}};
$lzsc$temp["displayName"] = "$lzc$xpathQuery_dependencies";
return $lzsc$temp
})(), "setPointer", (function () {
var $lzsc$temp = function (p_$0) {
this.setXPath(null);
if (p_$0 != null) {
this.setDataContext(p_$0.ownerDocument)
} else {
this.__LZsetTracking(null)
};
var dc_$1 = this.data != p_$0;
var pc_$2 = this.p != p_$0;
this.p = p_$0;
this.data = p_$0;
this.__LZsendUpdate(dc_$1, pc_$2);
return p_$0 != null
};
$lzsc$temp["displayName"] = "setPointer";
return $lzsc$temp
})(), "getDataset", (function () {
var $lzsc$temp = function () {
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
})(), "setXPath", (function () {
var $lzsc$temp = function (p_$0) {
if (!p_$0) {
this.xpath = null;
this.parsedPath = null;
if (this.p) this.__LZsetTracking(this.p.ownerDocument);
return false
};
this.xpath = p_$0;
this.parsedPath = this.parsePath(p_$0);
var ppcontext_$1 = this.parsedPath.getContext(this);
if (this.rerunxpath && this.parsedPath.hasDotDot && !ppcontext_$1) {
this.__LZspecialDotDot = true
} else {
if (this.__LZdotdotCheckDel) {
this.__LZdotdotCheckDel.unregisterAll()
}};
this.setDataContext(ppcontext_$1);
return this.runXPath()
};
$lzsc$temp["displayName"] = "setXPath";
return $lzsc$temp
})(), "runXPath", (function () {
var $lzsc$temp = function () {
if (!this.parsedPath) {
return false
};
var newc_$0 = null;
if (this.context && ((LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(this.context) : this.context instanceof LzDatapointer) || (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(this.context) : this.context instanceof LzDataset) || (AnonDatasetGenerator["$lzsc$isa"] ? AnonDatasetGenerator.$lzsc$isa(this.context) : this.context instanceof AnonDatasetGenerator))) {
newc_$0 = this.context.getContext()
};
if (newc_$0) {
var n_$1 = this.__LZgetNodes(this.parsedPath, newc_$0, 0)
} else {
var n_$1 = null
};
if (n_$1 == null) {
this.__LZHandleNoNodes();
return false
} else if (Array["$lzsc$isa"] ? Array.$lzsc$isa(n_$1) : n_$1 instanceof Array) {
this.__LZHandleMultiNodes(n_$1);
return false
} else {
this.__LZHandleSingleNode(n_$1);
return true
}};
$lzsc$temp["displayName"] = "runXPath";
return $lzsc$temp
})(), "__LZsetupDotDot", (function () {
var $lzsc$temp = function (p_$0) {
if (this.__LZlastdotdot != p_$0.ownerDocument) {
if (this.__LZdotdotCheckDel == null) {
this.__LZdotdotCheckDel = new LzDelegate(this, "__LZcheckDotDot")
} else {
this.__LZdotdotCheckDel.unregisterAll()
};
this.__LZlastdotdot = p_$0.ownerDocument;
this.__LZdotdotCheckDel.register(this.__LZlastdotdot, "onDocumentChange")
}};
$lzsc$temp["displayName"] = "__LZsetupDotDot";
return $lzsc$temp
})(), "__LZHandleSingleNode", (function () {
var $lzsc$temp = function (n_$0) {
if (this.__LZspecialDotDot) this.__LZsetupDotDot(n_$0);
this.__LZupdateLocked = true;
this.__LZpchanged = n_$0 != this.p;
this.p = n_$0;
this.__LZsetData();
this.__LZupdateLocked = false;
this.__LZsendUpdate()
};
$lzsc$temp["displayName"] = "__LZHandleSingleNode";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function () {
var $lzsc$temp = function () {
var pc_$0 = this.p != null;
var dc_$1 = this.data != null;
this.p = null;
this.data = null;
this.__LZsendUpdate(dc_$1, pc_$0)
};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function () {
var $lzsc$temp = function (n_$0) {
Debug.error("%w matched %d nodes", this, n_$0.length);
this.__LZHandleNoNodes();
return null
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "__LZsetData", (function () {
var $lzsc$temp = function () {
if (this.parsedPath && this.parsedPath.aggOperator != null) {
if (this.parsedPath.aggOperator == "last") {
this.data = this.__LZgetLast();
this.__LZsendUpdate(true)
} else if (this.parsedPath.aggOperator == "position") {
this.data = this.__LZgetPosition();
this.__LZsendUpdate(true)
}} else if (this.parsedPath && this.parsedPath.operator != null) {
this.__LZsimpleOperatorUpdate()
} else {
if (this.data != this.p) {
this.data = this.p;
this.__LZsendUpdate(true)
}}};
$lzsc$temp["displayName"] = "__LZsetData";
return $lzsc$temp
})(), "__LZgetLast", (function () {
var $lzsc$temp = function () {
var ctx_$0 = this.context;
if (ctx_$0 == null || ctx_$0 === this || !(LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(ctx_$0) : ctx_$0 instanceof LzDatapointer)) {
return 1
} else {
return ctx_$0.__LZgetLast() || 1
}};
$lzsc$temp["displayName"] = "__LZgetLast";
return $lzsc$temp
})(), "__LZgetPosition", (function () {
var $lzsc$temp = function () {
var ctx_$0 = this.context;
if (ctx_$0 == null || ctx_$0 === this || !(LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(ctx_$0) : ctx_$0 instanceof LzDatapointer)) {
return 1
} else {
return ctx_$0.__LZgetPosition() || 1
}};
$lzsc$temp["displayName"] = "__LZgetPosition";
return $lzsc$temp
})(), "__LZupdateLocked", false, "__LZpchanged", false, "__LZdchanged", false, "__LZsendUpdate", (function () {
var $lzsc$temp = function (upd_$0, upp_$1) {
switch (arguments.length) {
case 0:
upd_$0 = false;;case 1:
upp_$1 = false
};
this.__LZdchanged = upd_$0 || this.__LZdchanged;
this.__LZpchanged = upp_$1 || this.__LZpchanged;
if (this.__LZupdateLocked) {
return false
};
if (this.__LZdchanged) {
if (this.ondata.ready) this.ondata.sendEvent(this.data);
this.__LZdchanged = false
};
if (this.__LZpchanged) {
if (this.onp.ready) this.onp.sendEvent(this.p);
this.__LZpchanged = false;
if (this.onDocumentChange.ready) this.onDocumentChange.sendEvent({who: this.p, type: 2, what: "context"})
};
return true
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "isValid", (function () {
var $lzsc$temp = function () {
return this.p != null
};
$lzsc$temp["displayName"] = "isValid";
return $lzsc$temp
})(), "__LZsimpleOperatorUpdate", (function () {
var $lzsc$temp = function () {
var ndat_$0 = this.p != null ? this.__LZprocessOperator(this.p, this.parsedPath) : void 0;
var dchanged_$1 = false;
if (this.data != ndat_$0 || this.parsedPath.operator == "attributes") {
this.data = ndat_$0;
dchanged_$1 = true
};
this.__LZsendUpdate(dchanged_$1)
};
$lzsc$temp["displayName"] = "__LZsimpleOperatorUpdate";
return $lzsc$temp
})(), "parsePath", (function () {
var $lzsc$temp = function (pa_$0) {
if (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(pa_$0) : pa_$0 instanceof LzDatapath) {
var xp_$1 = pa_$0.xpath
} else {
var xp_$1 = pa_$0
};
var ppc_$2 = LzDatapointer.ppcache;
var q_$3 = ppc_$2[xp_$1];
if (q_$3 == null) {
q_$3 = new LzParsedPath(xp_$1, this);
ppc_$2[xp_$1] = q_$3
};
return q_$3
};
$lzsc$temp["displayName"] = "parsePath";
return $lzsc$temp
})(), "getLocalDataContext", (function () {
var $lzsc$temp = function (pp_$0) {
var n_$1 = this.parent;
if (pp_$0) {
var a_$2 = pp_$0;
for (var i_$3 = 0;i_$3 < a_$2.length && n_$1 != null;i_$3++) {
n_$1 = n_$1[a_$2[i_$3]]
};
if (n_$1 != null && !(LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$1) : n_$1 instanceof LzDataset) && (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$1["localdata"]) : n_$1["localdata"] instanceof LzDataset)) {
n_$1 = n_$1["localdata"];
return n_$1
}};
if (n_$1 != null && (LzDataset["$lzsc$isa"] ? LzDataset.$lzsc$isa(n_$1) : n_$1 instanceof LzDataset)) {
return n_$1
} else {
Debug.warn('local dataset "%w" not found in %w', pp_$0, this.parent);
return null
}};
$lzsc$temp["displayName"] = "getLocalDataContext";
return $lzsc$temp
})(), "__LZgetNodes", (function () {
var $lzsc$temp = function (pathobj_$0, p_$1, startpoint_$2) {
switch (arguments.length) {
case 2:
startpoint_$2 = 0
};
if (p_$1 == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (pathobj_$0.selectors != null) {
var pathlen_$3 = pathobj_$0.selectors.length;
for (var i_$4 = startpoint_$2;i_$4 < pathlen_$3;i_$4++) {
var cp_$5 = pathobj_$0.selectors[i_$4];
var specialop_$6 = LzDatapointer.pathSymbols[cp_$5] || 0;
var posnext_$7 = pathobj_$0.selectors[i_$4 + 1];
if (posnext_$7 && !(String["$lzsc$isa"] ? String.$lzsc$isa(posnext_$7) : posnext_$7 instanceof String) && posnext_$7["pred"] == "range") {
var range_$8 = pathobj_$0.selectors[++i_$4]
} else {
var range_$8 = null
};
var np_$9 = null;
if ((Object["$lzsc$isa"] ? Object.$lzsc$isa(cp_$5) : cp_$5 instanceof Object) && ("pred" in cp_$5) && null != cp_$5.pred) {
if (cp_$5.pred == "hasattr") {
p_$1 = p_$1.hasAttr(cp_$5.attr) ? p_$1 : null
} else if (cp_$5.pred == "attrval") {
if (p_$1.attributes != null) {
p_$1 = p_$1.attributes[cp_$5.attr] == cp_$5.val ? p_$1 : null
} else {
p_$1 = null
}}} else if (specialop_$6 == 0) {
np_$9 = this.nodeByName(cp_$5, range_$8, p_$1)
} else if (specialop_$6 == 1) {
p_$1 = p_$1.ownerDocument
} else if (specialop_$6 == 2) {
p_$1 = p_$1.parentNode
} else if (specialop_$6 == 3) {
np_$9 = [];
if (p_$1.childNodes) {
var cnodes_$a = p_$1.childNodes;
var len_$b = cnodes_$a.length;
var rleft_$c = range_$8 != null ? range_$8[0] : 0;
var rright_$d = range_$8 != null ? range_$8[1] : len_$b;
var cnt_$e = 0;
for (var j_$f = 0;j_$f < len_$b;j_$f++) {
var cn_$g = cnodes_$a[j_$f];
if (cn_$g.nodeType == LzDataElement.ELEMENT_NODE) {
cnt_$e++;
if (cnt_$e >= rleft_$c) {
np_$9.push(cn_$g)
};
if (cnt_$e == rright_$d) {
break
}}}}};
if (np_$9 != null) {
if (np_$9.length > 1) {
if (i_$4 == pathlen_$3 - 1) {
return np_$9
};
var rval_$h = [];
for (var j_$f = 0;j_$f < np_$9.length;j_$f++) {
var r_$i = this.__LZgetNodes(pathobj_$0, np_$9[j_$f], i_$4 + 1);
if (r_$i != null) {
if (Array["$lzsc$isa"] ? Array.$lzsc$isa(r_$i) : r_$i instanceof Array) {
for (var n_$j = 0;n_$j < r_$i.length;n_$j++) {
rval_$h.push(r_$i[n_$j])
}} else {
rval_$h.push(r_$i)
}}};
if (rval_$h.length == 0) {
return null
} else if (rval_$h.length == 1) {
return rval_$h[0]
} else {
return rval_$h
}} else {
p_$1 = np_$9[0]
}};
if (p_$1 == null) {
return null
}}};
return p_$1
};
$lzsc$temp["displayName"] = "__LZgetNodes";
return $lzsc$temp
})(), "getContext", (function () {
var $lzsc$temp = function (chgpkg_$0) {
switch (arguments.length) {
case 0:
chgpkg_$0 = null
};
return this.p
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "nodeByName", (function () {
var $lzsc$temp = function (name_$0, range_$1, p_$2) {
if (!p_$2) {
p_$2 = this.p;
if (!this.p) return null
};
var o_$3 = [];
if (p_$2.childNodes != null) {
var cnodes_$4 = p_$2.childNodes;
var len_$5 = cnodes_$4.length;
var rleft_$6 = range_$1 != null ? range_$1[0] : 0;
var rright_$7 = range_$1 != null ? range_$1[1] : len_$5;
var cnt_$8 = 0;
for (var i_$9 = 0;i_$9 < len_$5;i_$9++) {
var cn_$a = cnodes_$4[i_$9];
if (cn_$a.nodeName == name_$0) {
cnt_$8++;
if (cnt_$8 >= rleft_$6) {
o_$3.push(cn_$a)
};
if (cnt_$8 == rright_$7) {
break
}}}};
return o_$3
};
$lzsc$temp["displayName"] = "nodeByName";
return $lzsc$temp
})(), "$lzc$set_rerunxpath", (function () {
var $lzsc$temp = function (rrx_$0) {
this.rerunxpath = rrx_$0;
if (this.onrerunxpath.ready) this.onrerunxpath.sendEvent(rrx_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_rerunxpath";
return $lzsc$temp
})(), "dupePointer", (function () {
var $lzsc$temp = function () {
var dp_$0 = new LzDatapointer();
dp_$0.setFromPointer(this);
return dp_$0
};
$lzsc$temp["displayName"] = "dupePointer";
return $lzsc$temp
})(), "__LZdoSelect", (function () {
var $lzsc$temp = function (selector_$0, amnt_$1) {
var np_$2 = this.p;
for (;np_$2 != null && amnt_$1 > 0;amnt_$1--) {
if (np_$2.nodeType == LzDataNode.TEXT_NODE) {
if (selector_$0 == "getFirstChild") break
};
np_$2 = np_$2[selector_$0]()
};
if (np_$2 != null && amnt_$1 == 0) {
this.setPointer(np_$2);
return true
};
return false
};
$lzsc$temp["displayName"] = "__LZdoSelect";
return $lzsc$temp
})(), "selectNext", (function () {
var $lzsc$temp = function (amnt_$0) {
switch (arguments.length) {
case 0:
amnt_$0 = null
};
return this.__LZdoSelect("getNextSibling", amnt_$0 ? amnt_$0 : 1)
};
$lzsc$temp["displayName"] = "selectNext";
return $lzsc$temp
})(), "selectPrev", (function () {
var $lzsc$temp = function (amnt_$0) {
switch (arguments.length) {
case 0:
amnt_$0 = null
};
return this.__LZdoSelect("getPreviousSibling", amnt_$0 ? amnt_$0 : 1)
};
$lzsc$temp["displayName"] = "selectPrev";
return $lzsc$temp
})(), "selectChild", (function () {
var $lzsc$temp = function (amnt_$0) {
switch (arguments.length) {
case 0:
amnt_$0 = null
};
return this.__LZdoSelect("getFirstChild", amnt_$0 ? amnt_$0 : 1)
};
$lzsc$temp["displayName"] = "selectChild";
return $lzsc$temp
})(), "selectParent", (function () {
var $lzsc$temp = function (amnt_$0) {
switch (arguments.length) {
case 0:
amnt_$0 = null
};
return this.__LZdoSelect("getParent", amnt_$0 ? amnt_$0 : 1)
};
$lzsc$temp["displayName"] = "selectParent";
return $lzsc$temp
})(), "selectNextParent", (function () {
var $lzsc$temp = function () {
var op_$0 = this.p;
if (this.selectParent() && this.selectNext()) {
return true
} else {
this.setPointer(op_$0);
return false
}};
$lzsc$temp["displayName"] = "selectNextParent";
return $lzsc$temp
})(), "getNodeOffset", (function () {
var $lzsc$temp = function () {
Debug.info("%w.%s is deprecated.  Use XPath `position()` operator or LzDatapointer.getXPathIndex() instead.", this, arguments.callee);
return this.getXPathIndex()
};
$lzsc$temp["displayName"] = "getNodeOffset";
return $lzsc$temp
})(), "getXPathIndex", (function () {
var $lzsc$temp = function () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return 0
};
return this.p.getOffset() + 1
};
$lzsc$temp["displayName"] = "getXPathIndex";
return $lzsc$temp
})(), "getNodeType", (function () {
var $lzsc$temp = function () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
return this.p.nodeType
};
$lzsc$temp["displayName"] = "getNodeType";
return $lzsc$temp
})(), "getNodeName", (function () {
var $lzsc$temp = function () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
return this.p.nodeName
};
$lzsc$temp["displayName"] = "getNodeName";
return $lzsc$temp
})(), "setNodeName", (function () {
var $lzsc$temp = function (name_$0) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.$lzc$set_nodeName(name_$0)
}};
$lzsc$temp["displayName"] = "setNodeName";
return $lzsc$temp
})(), "getNodeAttributes", (function () {
var $lzsc$temp = function () {
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
})(), "getNodeAttribute", (function () {
var $lzsc$temp = function (name_$0) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
return this.p.attributes[name_$0]
};
return null
};
$lzsc$temp["displayName"] = "getNodeAttribute";
return $lzsc$temp
})(), "setNodeAttribute", (function () {
var $lzsc$temp = function (name_$0, val_$1) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.setAttr(name_$0, val_$1)
}};
$lzsc$temp["displayName"] = "setNodeAttribute";
return $lzsc$temp
})(), "deleteNodeAttribute", (function () {
var $lzsc$temp = function (name_$0) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.removeAttr(name_$0)
}};
$lzsc$temp["displayName"] = "deleteNodeAttribute";
return $lzsc$temp
})(), "getNodeText", (function () {
var $lzsc$temp = function () {
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
})(), "setNodeText", (function () {
var $lzsc$temp = function (val_$0) {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
var foundit_$1 = false;
var cnodes_$2 = this.p.childNodes;
for (var i_$3 = 0;i_$3 < cnodes_$2.length;i_$3++) {
var cn_$4 = cnodes_$2[i_$3];
if (cn_$4.nodeType == LzDataElement.TEXT_NODE) {
cn_$4.$lzc$set_data(val_$0);
foundit_$1 = true;
break
}};
if (!foundit_$1) {
this.p.appendChild(new LzDataText(val_$0))
}}};
$lzsc$temp["displayName"] = "setNodeText";
return $lzsc$temp
})(), "getNodeCount", (function () {
var $lzsc$temp = function () {
if (!this.p || this.p.nodeType == LzDataElement.TEXT_NODE) return 0;
return this.p.childNodes.length || 0
};
$lzsc$temp["displayName"] = "getNodeCount";
return $lzsc$temp
})(), "addNode", (function () {
var $lzsc$temp = function (name_$0, text_$1, attrs_$2) {
switch (arguments.length) {
case 1:
text_$1 = null;;case 2:
attrs_$2 = null
};
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
var nn_$3 = new LzDataElement(name_$0, attrs_$2);
if (text_$1 != null) {
nn_$3.appendChild(new LzDataText(text_$1))
};
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.appendChild(nn_$3)
};
return nn_$3
};
$lzsc$temp["displayName"] = "addNode";
return $lzsc$temp
})(), "deleteNode", (function () {
var $lzsc$temp = function () {
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
var op_$0 = this.p;
if (!this.rerunxpath) {
if (!(this.selectNext() || this.selectPrev())) {
this.__LZHandleNoNodes()
}};
op_$0.parentNode.removeChild(op_$0);
return op_$0
};
$lzsc$temp["displayName"] = "deleteNode";
return $lzsc$temp
})(), "sendDataChange", (function () {
var $lzsc$temp = function (chgpkg_$0) {
this.getDataset().sendDataChange(chgpkg_$0)
};
$lzsc$temp["displayName"] = "sendDataChange";
return $lzsc$temp
})(), "comparePointer", (function () {
var $lzsc$temp = function (ptr_$0) {
return this.p == ptr_$0.p
};
$lzsc$temp["displayName"] = "comparePointer";
return $lzsc$temp
})(), "addNodeFromPointer", (function () {
var $lzsc$temp = function (dp_$0) {
if (!dp_$0.p) return null;
if (!this.p) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
var n_$1 = dp_$0.p.cloneNode(true);
if (this.p.nodeType != LzDataElement.TEXT_NODE) {
this.p.appendChild(n_$1)
};
return new LzDatapointer(null, {pointer: n_$1})
};
$lzsc$temp["displayName"] = "addNodeFromPointer";
return $lzsc$temp
})(), "setFromPointer", (function () {
var $lzsc$temp = function (dp_$0) {
this.setPointer(dp_$0.p)
};
$lzsc$temp["displayName"] = "setFromPointer";
return $lzsc$temp
})(), "__LZprocessOperator", (function () {
var $lzsc$temp = function (p_$0, pp_$1) {
if (p_$0 == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return
};
var op_$2 = pp_$1.operator;
switch (op_$2) {
case "serialize":
return p_$0.serialize();;case "text":
return p_$0.nodeType != LzDataElement.TEXT_NODE ? p_$0.__LZgetText() : void 0;;case "name":
return p_$0.nodeName;;default:
if (pp_$1.hasAttrOper) {
if (p_$0.nodeType != LzDataElement.TEXT_NODE && p_$0["attributes"]) {
if (op_$2 == "attributes") {
return p_$0.attributes
} else {
return p_$0.attributes[op_$2.substr(11)]
}} else {
return
}} else {
Debug.error("Unknown operator '%s' in '%s'", op_$2, arguments.callee)
}}};
$lzsc$temp["displayName"] = "__LZprocessOperator";
return $lzsc$temp
})(), "makeRootNode", (function () {
var $lzsc$temp = function () {
return new LzDataElement("root")
};
$lzsc$temp["displayName"] = "makeRootNode";
return $lzsc$temp
})(), "finishRootNode", (function () {
var $lzsc$temp = function (n_$0) {
return n_$0.childNodes[0]
};
$lzsc$temp["displayName"] = "finishRootNode";
return $lzsc$temp
})(), "makeElementNode", (function () {
var $lzsc$temp = function (attrs_$0, name_$1, par_$2) {
var tn_$3 = new LzDataElement(name_$1, attrs_$0);
par_$2.appendChild(tn_$3);
return tn_$3
};
$lzsc$temp["displayName"] = "makeElementNode";
return $lzsc$temp
})(), "makeTextNode", (function () {
var $lzsc$temp = function (text_$0, par_$1) {
var tn_$2 = new LzDataText(text_$0);
par_$1.appendChild(tn_$2);
return tn_$2
};
$lzsc$temp["displayName"] = "makeTextNode";
return $lzsc$temp
})(), "serialize", (function () {
var $lzsc$temp = function () {
if (this.p == null) {
Debug.info("%s: p is null in %s", arguments.callee, this);
return null
};
return this.p.serialize()
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})(), "setDataContext", (function () {
var $lzsc$temp = function (dc_$0, ignore_$1) {
switch (arguments.length) {
case 1:
ignore_$1 = false
};
if (dc_$0 == null) {
this.context = this;
if (this.p) {
this.__LZsetTracking(this.p.ownerDocument)
}} else if (this.context != dc_$0) {
this.context = dc_$0;
if (this.errorDel != null) {
this.errorDel.unregisterAll();
this.timeoutDel.unregisterAll()
};
this.__LZsetTracking(dc_$0);
var hasxpath_$2 = this.xpath != null;
if (hasxpath_$2) {
if (this.errorDel == null) {
this.errorDel = new LzDelegate(this, "gotError");
this.timeoutDel = new LzDelegate(this, "gotTimeout")
};
this.errorDel.register(dc_$0, "onerror");
this.timeoutDel.register(dc_$0, "ontimeout")
}}};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "__LZcheckChange", (function () {
var $lzsc$temp = function (chgpkg_$0) {
if (!this.rerunxpath) {
if (!this.p || chgpkg_$0.who == this.context) {
this.runXPath()
} else if (this.__LZneedsOpUpdate(chgpkg_$0)) {
this.__LZsimpleOperatorUpdate()
};
return false
} else {
if (chgpkg_$0.type == 2 || (chgpkg_$0.type == 0 || chgpkg_$0.type == 1 && this.parsedPath && this.parsedPath.hasOpSelector) && (this.parsedPath && this.parsedPath.hasDotDot || this.p == null || this.p.childOfNode(chgpkg_$0.who))) {
this.runXPath();
return true
} else if (this.__LZneedsOpUpdate(chgpkg_$0)) {
this.__LZsimpleOperatorUpdate()
};
return false
}};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZneedsOpUpdate", (function () {
var $lzsc$temp = function (chgpkg_$0) {
switch (arguments.length) {
case 0:
chgpkg_$0 = null
};
var ppath_$1 = this.parsedPath;
if (ppath_$1 != null && ppath_$1.operator != null) {
var who_$2 = chgpkg_$0.who;
var type_$3 = chgpkg_$0.type;
if (ppath_$1.operator != "text") {
return type_$3 == 1 && who_$2 == this.p
} else {
return type_$3 == 0 && who_$2 == this.p || who_$2.parentNode == this.p && who_$2.nodeType == LzDataElement.TEXT_NODE
}} else {
return false
}};
$lzsc$temp["displayName"] = "__LZneedsOpUpdate";
return $lzsc$temp
})(), "__LZcheckDotDot", (function () {
var $lzsc$temp = function (chgpkg_$0) {
var who_$1 = chgpkg_$0.who;
var type_$2 = chgpkg_$0.type;
if ((type_$2 == 0 || type_$2 == 1 && this.parsedPath.hasOpSelector) && this.context.getContext().childOfNode(who_$1)) {
this.runXPath()
}};
$lzsc$temp["displayName"] = "__LZcheckDotDot";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted) return;
this.__LZsetTracking(null);
this.p = null;
this.data = null;
this.__LZlastdotdot = null;
this.context = null;
this.__LZtracking = null;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "__LZsetTracking", (function () {
var $lzsc$temp = function (who_$0, force_$1, needscheck_$2) {
switch (arguments.length) {
case 1:
force_$1 = false;;case 2:
needscheck_$2 = false
};
var tracking_$3 = this.__LZtracking;
var trackDel_$4 = this.__LZtrackDel;
if (who_$0) {
if (tracking_$3 != null && tracking_$3.length > 1) {
Debug.error("%w.__LZtracking is %w, expecting an array of length 1", this, this.__LZtracking)
};
if (tracking_$3 != null && tracking_$3.length == 1 && tracking_$3[0] === who_$0) {
return
};
if (trackDel_$4) {
trackDel_$4.unregisterAll()
};
var hasxpath_$5 = force_$1 || this.xpath;
if (hasxpath_$5) {
if (!trackDel_$4) {
this.__LZtrackDel = trackDel_$4 = new LzDelegate(this, "__LZcheckChange")
};
this.__LZtracking = tracking_$3 = [who_$0];
trackDel_$4.register(who_$0, "onDocumentChange")
}} else {
this.__LZtracking = [];
if (trackDel_$4) {
this.__LZtrackDel.unregisterAll()
}}};
$lzsc$temp["displayName"] = "__LZsetTracking";
return $lzsc$temp
})()], LzNode, ["tagname", "datapointer", "attributes", {ignoreplacement: true}, "ppcache", {}, "pathSymbols", {"/": 1, "..": 2, "*": 3, ".": 4}]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzDatapointer.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
if (this.p) {
return this.p._dbg_name()
};
return null
};
$lzsc$temp["displayName"] = "data/LzDatapointer.lzs#1339/37";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "data/LzDatapointer.lzs#76/1";
return $lzsc$temp
})()(LzDatapointer);
lz[LzDatapointer.tagname] = LzDatapointer;
Class.make("LzParam", ["d", null, "delimiter", "&", "$lzc$set_delimiter", (function () {
var $lzsc$temp = function (d_$0) {
this.setDelimiter(d_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_delimiter";
return $lzsc$temp
})(), "separator", "=", "$lzc$set_separator", (function () {
var $lzsc$temp = function (s_$0) {
this.setSeparator(s_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_separator";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.d = {}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "parseQueryString", (function () {
var $lzsc$temp = function (query_$0) {
Debug.info(this, ".parseQueryString is deprecated. Use lz.Param.parseQueryString instead");
return lz.Param.parseQueryString(query_$0)
};
$lzsc$temp["displayName"] = "parseQueryString";
return $lzsc$temp
})(), "addObject", (function () {
var $lzsc$temp = function (o_$0) {
for (var n_$1 in o_$0) {
this.setValue(n_$1, o_$0[n_$1])
}};
$lzsc$temp["displayName"] = "addObject";
return $lzsc$temp
})(), "clone", (function () {
var $lzsc$temp = function (arg_$0) {
switch (arguments.length) {
case 0:
arg_$0 = null
};
if (arg_$0) Debug.info("%w.%s is deprecated. Do not pass as argument to clone()", this, arguments.callee);
var o_$1 = new (lz.Param)();
for (var n_$2 in this.d) {
o_$1.d[n_$2] = this.d[n_$2].concat()
};
return o_$1
};
$lzsc$temp["displayName"] = "clone";
return $lzsc$temp
})(), "remove", (function () {
var $lzsc$temp = function (name_$0) {
switch (arguments.length) {
case 0:
name_$0 = null
};
if (name_$0 == null) {
this.d = {}} else {
var a_$1 = this.d[name_$0];
if (a_$1 != null) {
a_$1.shift();
if (!a_$1.length) {
delete this.d[name_$0]
}}}};
$lzsc$temp["displayName"] = "remove";
return $lzsc$temp
})(), "setValue", (function () {
var $lzsc$temp = function (name_$0, val_$1, enc_$2) {
switch (arguments.length) {
case 2:
enc_$2 = false
};
if (enc_$2) val_$1 = encodeURIComponent(val_$1);
this.d[name_$0] = [val_$1]
};
$lzsc$temp["displayName"] = "setValue";
return $lzsc$temp
})(), "addValue", (function () {
var $lzsc$temp = function (name_$0, val_$1, enc_$2) {
switch (arguments.length) {
case 2:
enc_$2 = false
};
if (enc_$2) val_$1 = encodeURIComponent(val_$1);
var a_$3 = this.d[name_$0];
if (a_$3 == null) {
this.d[name_$0] = [val_$1]
} else {
a_$3.push(val_$1)
}};
$lzsc$temp["displayName"] = "addValue";
return $lzsc$temp
})(), "getValue", (function () {
var $lzsc$temp = function (name_$0) {
var a_$1 = this.d[name_$0];
if (a_$1 != null) {
return a_$1[0]
};
return null
};
$lzsc$temp["displayName"] = "getValue";
return $lzsc$temp
})(), "getValues", (function () {
var $lzsc$temp = function (name_$0) {
var a_$1 = this.d[name_$0];
if (a_$1 != null) {
return a_$1.concat()
};
return null
};
$lzsc$temp["displayName"] = "getValues";
return $lzsc$temp
})(), "getValueNoCase", (function () {
var $lzsc$temp = function (name_$0) {
Debug.deprecated(this, arguments.callee, this.getValues);
var a_$1 = this.getValues(name_$0);
return a_$1 != null && a_$1.length == 1 ? a_$1[0] : a_$1
};
$lzsc$temp["displayName"] = "getValueNoCase";
return $lzsc$temp
})(), "getNames", (function () {
var $lzsc$temp = function () {
var o_$0 = [];
for (var n_$1 in this.d) {
o_$0.push(n_$1)
};
return o_$0
};
$lzsc$temp["displayName"] = "getNames";
return $lzsc$temp
})(), "setDelimiter", (function () {
var $lzsc$temp = function (d_$0) {
var o_$1 = this.delimiter;
this.delimiter = d_$0;
return o_$1
};
$lzsc$temp["displayName"] = "setDelimiter";
return $lzsc$temp
})(), "setSeparator", (function () {
var $lzsc$temp = function (s_$0) {
var o_$1 = this.separator;
this.separator = s_$0;
return o_$1
};
$lzsc$temp["displayName"] = "setSeparator";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return this.serialize()
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "serialize", (function () {
var $lzsc$temp = function (sep_$0, delim_$1, uriencode_$2) {
switch (arguments.length) {
case 0:
sep_$0 = null;;case 1:
delim_$1 = null;;case 2:
uriencode_$2 = false
};
var sep_$0 = sep_$0 == null ? this.separator : sep_$0;
var dlm_$3 = delim_$1 == null ? this.delimiter : delim_$1;
var o_$4 = "";
var c_$5 = false;
for (var mk_$6 in this.d) {
var n_$7 = this.d[mk_$6];
if (n_$7 != null) {
for (var i_$8 = 0;i_$8 < n_$7.length;++i_$8) {
if (c_$5) o_$4 += dlm_$3;
o_$4 += mk_$6 + sep_$0 + (uriencode_$2 ? encodeURIComponent(n_$7[i_$8]) : n_$7[i_$8]);
c_$5 = true
}}};
return o_$4
};
$lzsc$temp["displayName"] = "serialize";
return $lzsc$temp
})()], LzEventable, ["parseQueryString", (function () {
var $lzsc$temp = function (query_$0) {
var queries_$1 = {};
if (!query_$0) return queries_$1;
var parameters_$2 = query_$0.split("&");
for (var i_$3 = 0;i_$3 < parameters_$2.length;++i_$3) {
var key_$4 = parameters_$2[i_$3];
var value_$5 = "";
var n_$6 = key_$4.indexOf("=");
if (n_$6 > 0) {
value_$5 = unescape(key_$4.substring(n_$6 + 1));
key_$4 = key_$4.substring(0, n_$6)
};
queries_$1[key_$4] = value_$5
};
return queries_$1
};
$lzsc$temp["displayName"] = "parseQueryString";
return $lzsc$temp
})()]);
lz.Param = LzParam;
Class.make("LzParsedPath", ["path", null, "selectors", null, "context", null, "dsetname", null, "dsrcname", null, "islocaldata", null, "operator", null, "aggOperator", null, "hasAttrOper", false, "hasOpSelector", false, "hasDotDot", false, "getContext", (function () {
var $lzsc$temp = function (dp_$0) {
if (this.context != null) {
return this.context
} else {
if (this.islocaldata != null) {
return dp_$0.getLocalDataContext(this.islocaldata)
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
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (pa_$0, node_$1) {
this.path = pa_$0;
this.selectors = [];
var sourceindex_$2 = pa_$0.indexOf(":/");
if (sourceindex_$2 > -1) {
var sourceparts_$3 = pa_$0.substring(0, sourceindex_$2).split(":");
if (sourceparts_$3.length > 1) {
var dsrc_$4 = LzParsedPath.trim(sourceparts_$3[0]);
var dset_$5 = LzParsedPath.trim(sourceparts_$3[1]);
if (dsrc_$4 == "local") {
this.islocaldata = dset_$5.split(".")
} else {
if (canvas[dsrc_$4][dset_$5] == null) {
Debug.error("couldn't find dataset for %w (canvas[%#w][%#w])", pa_$0, dsrc_$4, dset_$5)
};
this.dsrcname = dsrc_$4;
this.dsetname = dset_$5
}} else {
var name_$6 = LzParsedPath.trim(sourceparts_$3[0]);
if (name_$6 == "new") {
this.context = new AnonDatasetGenerator(this)
} else {
if (canvas.datasets[name_$6] == null) {
Debug.error("couldn't find dataset for %w (canvas.datasets[%#w])", pa_$0, name_$6)
};
this.dsetname = name_$6
}};
var rest_$7 = pa_$0.substring(sourceindex_$2 + 2)
} else {
var rest_$7 = pa_$0
};
var nodes_$8 = [];
var currnode_$9 = "";
var instring_$a = false;
var escape_$b = false;
for (var i_$c = 0;i_$c < rest_$7.length;i_$c++) {
var c_$d = rest_$7.charAt(i_$c);
if (c_$d == "\\" && escape_$b == false) {
escape_$b = true;
continue
} else if (escape_$b == true) {
escape_$b = false;
currnode_$9 += c_$d;
continue
} else if (instring_$a == false && c_$d == "/") {
nodes_$8.push(currnode_$9);
currnode_$9 = "";
continue
} else if (c_$d == "'") {
instring_$a = instring_$a ? false : true
};
currnode_$9 += c_$d
};
nodes_$8.push(currnode_$9);
if (nodes_$8 != null) {
for (var i_$c = 0;i_$c < nodes_$8.length;i_$c++) {
var cnode_$e = LzParsedPath.trim(nodes_$8[i_$c]);
if (i_$c == nodes_$8.length - 1) {
if (cnode_$e.charAt(0) == "@") {
this.hasAttrOper = true;
if (cnode_$e.charAt(1) == "*") {
this.operator = "attributes"
} else {
this.operator = "attributes." + cnode_$e.substring(1, cnode_$e.length)
};
continue
} else if (cnode_$e.charAt(cnode_$e.length - 1) == ")") {
if (cnode_$e.indexOf("last") > -1) {
this.aggOperator = "last"
} else if (cnode_$e.indexOf("position") > -1) {
this.aggOperator = "position"
} else if (cnode_$e.indexOf("name") > -1) {
this.operator = "name"
} else if (cnode_$e.indexOf("text") > -1) {
this.operator = "text"
} else if (cnode_$e.indexOf("serialize") > -1) {
this.operator = "serialize"
} else {
Debug.warn("Unknown operator: '%s'", cnode_$e)
};
continue
} else if (cnode_$e == "") {
continue
}};
var preds_$f = cnode_$e.split("[");
var n_$g = LzParsedPath.trim(preds_$f[0]);
this.selectors.push(n_$g == "" ? "/" : n_$g);
if (n_$g == "" || n_$g == "..") {
this.hasDotDot = true
};
if (preds_$f != null) {
for (var j_$h = 1;j_$h < preds_$f.length;j_$h++) {
var pred_$i = LzParsedPath.trim(preds_$f[j_$h]);
pred_$i = pred_$i.substring(0, pred_$i.length - 1);
if (LzParsedPath.trim(pred_$i).charAt(0) == "@") {
var attrpred_$j = pred_$i.split("=");
var a_$k;
var tattr_$l = attrpred_$j.shift().substring(1);
if (attrpred_$j.length > 0) {
var aval_$m = LzParsedPath.trim(attrpred_$j.join("="));
aval_$m = aval_$m.substring(1, aval_$m.length - 1);
a_$k = {pred: "attrval", attr: LzParsedPath.trim(tattr_$l), val: LzParsedPath.trim(aval_$m)}} else {
a_$k = {pred: "hasattr", attr: LzParsedPath.trim(tattr_$l)}};
this.selectors.push(a_$k);
this.hasOpSelector = true
} else {
var a_$k = pred_$i.split("-");
a_$k[0] = LzParsedPath.trim(a_$k[0]);
if (a_$k[0] == "") {
a_$k[0] = 1
};
if (a_$k[1] != null) {
a_$k[1] = LzParsedPath.trim(a_$k[1])
};
if (a_$k[1] == "") {
a_$k[1] = Infinity
} else if (a_$k.length == 1) {
a_$k[1] = a_$k[0]
};
a_$k.pred = "range";
this.selectors.push(a_$k)
}}}}}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Parsed path for path: " + this.path
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "debugWrite", (function () {
var $lzsc$temp = function () {
Debug.write(this);
Debug.write("  c:" + this.context + "|");
Debug.write("  n:" + this.selectors.join("|") + "|");
Debug.write("  d:" + this.operator + "|");
Debug.write("  ")
};
$lzsc$temp["displayName"] = "debugWrite";
return $lzsc$temp
})()], null, ["trim", (function () {
var $lzsc$temp = function (s_$0) {
var st_$1 = 0;
var dotrim_$2 = false;
while (s_$0.charAt(st_$1) == " ") {
st_$1++;
dotrim_$2 = true
};
var len_$3 = s_$0.length - st_$1;
while (s_$0.charAt(st_$1 + len_$3 - 1) == " ") {
len_$3--;
dotrim_$2 = true
};
return dotrim_$2 ? s_$0.substr(st_$1, len_$3) : s_$0
};
$lzsc$temp["displayName"] = "trim";
return $lzsc$temp
})()]);
Class.make("AnonDatasetGenerator", ["pp", null, "__LZdepChildren", null, "onDocumentChange", LzDeclaredEvent, "onerror", LzDeclaredEvent, "ontimeout", LzDeclaredEvent, "noncontext", true, "$lzsc$initialize", (function () {
var $lzsc$temp = function (pp_$0) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.pp = pp_$0
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getContext", (function () {
var $lzsc$temp = function () {
var d_$0 = new LzDataset(null, {name: null});
var ppsel_$1 = this.pp.selectors;
if (ppsel_$1 != null) {
var dp_$2 = d_$0.getPointer();
for (var i_$3 = 0;i_$3 < ppsel_$1.length;i_$3++) {
if (ppsel_$1[i_$3] == "/") continue;
dp_$2.addNode(ppsel_$1[i_$3]);
dp_$2.selectChild()
}};
return d_$0
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "getDataset", (function () {
var $lzsc$temp = function () {
return null
};
$lzsc$temp["displayName"] = "getDataset";
return $lzsc$temp
})()], LzEventable);
Class.make("LzDatapath", ["datacontrolsvisibility", true, "$lzc$set_datacontrolsvisibility", (function () {
var $lzsc$temp = function (v_$0) {
this.datacontrolsvisibility = v_$0
};
$lzsc$temp["displayName"] = "$lzc$set_datacontrolsvisibility";
return $lzsc$temp
})(), "__LZtakeDPSlot", true, "storednodes", null, "__LZneedsUpdateAfterInit", false, "__LZdepChildren", null, "sel", false, "__LZisclone", false, "pooling", false, "replication", void 0, "axis", "y", "spacing", 0, "sortpath", void 0, "$lzc$set_sortpath", (function () {
var $lzsc$temp = function (xpath_$0) {
this.setOrder(xpath_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_sortpath";
return $lzsc$temp
})(), "setOrder", (function () {
var $lzsc$temp = function (xpath_$0, comparator_$1) {
switch (arguments.length) {
case 1:
comparator_$1 = null
};
if (this.__LZisclone) {
this.immediateparent.cloneManager.setOrder(xpath_$0, comparator_$1)
} else {
this.sortpath = xpath_$0;
if (comparator_$1 != null) {
this.sortorder = comparator_$1
}}};
$lzsc$temp["displayName"] = "setOrder";
return $lzsc$temp
})(), "sortorder", "ascending", "$lzc$set_sortorder", (function () {
var $lzsc$temp = function (comparator_$0) {
this.setComparator(comparator_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_sortorder";
return $lzsc$temp
})(), "setComparator", (function () {
var $lzsc$temp = function (comparator_$0) {
if (this.__LZisclone) {
this.immediateparent.cloneManager.setComparator(comparator_$0)
} else {
this.sortorder = comparator_$0
}};
$lzsc$temp["displayName"] = "setComparator";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (v_$0, args_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 1:
args_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, v_$0, args_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (v_$0, args_$1) {
this.rerunxpath = true;
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, v_$0, args_$1);
if (args_$1.datacontrolsvisibility != null) {
this.datacontrolsvisibility = args_$1.datacontrolsvisibility
};
if (this.__LZtakeDPSlot) {
if (this.immediateparent.datapath != null) {
Debug.warn("overriding datapath %w with %w", this.immediateparent.datapath, this)
};
this.immediateparent.datapath = this;
var pdp_$2 = this.immediateparent.searchParents("datapath").datapath;
if (pdp_$2 != null) {
var tarr_$3 = pdp_$2.__LZdepChildren;
if (tarr_$3 != null) {
pdp_$2.__LZdepChildren = [];
for (var i_$4 = tarr_$3.length - 1;i_$4 >= 0;i_$4--) {
var c_$5 = tarr_$3[i_$4];
if (c_$5 !== this && !(LzDataAttrBind["$lzsc$isa"] ? LzDataAttrBind.$lzsc$isa(c_$5) : c_$5 instanceof LzDataAttrBind) && c_$5.immediateparent != this.immediateparent && c_$5.immediateparent.childOf(this.immediateparent)) {
c_$5.setDataContext(this, true)
} else {
pdp_$2.__LZdepChildren.push(c_$5)
}}}} else {
Debug.warn("unexpected state for '%w', couldn't find parent-datapath!", this)
}}};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function () {
var $lzsc$temp = function (nodes_$0) {
var clonetype_$1;
if (this.replication == "lazy") {
clonetype_$1 = LzLazyReplicationManager
} else if (this.replication == "resize") {
clonetype_$1 = LzResizeReplicationManager
} else {
clonetype_$1 = LzReplicationManager
};
this.storednodes = nodes_$0;
var rman_$2 = new clonetype_$1(this, this._instanceAttrs);
this.storednodes = null;
return rman_$2
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "setNodes", (function () {
var $lzsc$temp = function (nodes_$0) {
var rman_$1 = this.__LZHandleMultiNodes(nodes_$0);
if (!rman_$1) rman_$1 = this;
rman_$1.__LZsetTracking(null);
if (nodes_$0) {
for (var i_$2 = 0;i_$2 < nodes_$0.length;i_$2++) {
var n_$3 = nodes_$0[i_$2];
var own_$4 = n_$3.ownerDocument;
rman_$1.__LZsetTracking(own_$4, true, n_$3 != own_$4)
}}};
$lzsc$temp["displayName"] = "setNodes";
return $lzsc$temp
})(), "__LZsendUpdate", (function () {
var $lzsc$temp = function (upd_$0, upp_$1) {
switch (arguments.length) {
case 0:
upd_$0 = false;;case 1:
upp_$1 = false
};
var pchg_$2 = this.__LZpchanged;
if (!(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsendUpdate"] || this.nextMethod(arguments.callee, "__LZsendUpdate")).call(this, upd_$0, upp_$1)) {
return false
};
if (this.immediateparent.isinited) {
this.__LZApplyData(pchg_$2)
} else {
this.__LZneedsUpdateAfterInit = true
};
return true
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "__LZApplyDataOnInit", (function () {
var $lzsc$temp = function () {
if (this.__LZneedsUpdateAfterInit) {
this.__LZApplyData();
this.__LZneedsUpdateAfterInit = false
}};
$lzsc$temp["displayName"] = "__LZApplyDataOnInit";
return $lzsc$temp
})(), "__LZApplyData", (function () {
var $lzsc$temp = function (force_$0) {
switch (arguments.length) {
case 0:
force_$0 = false
};
var ip_$1 = this.immediateparent;
if (this.datacontrolsvisibility) {
if (LzView["$lzsc$isa"] ? LzView.$lzsc$isa(ip_$1) : ip_$1 instanceof LzView) {
var ipview_$2 = ip_$1;
ipview_$2.__LZvizDat = this.p != null;
ipview_$2.__LZupdateShown()
}};
var cdat_$3 = force_$0 || ip_$1.data != this.data || this.parsedPath && this.parsedPath.operator == "attributes";
this.data = this.data == null ? null : this.data;
ip_$1.data = this.data;
if (cdat_$3) {
if (ip_$1.ondata.ready) ip_$1.ondata.sendEvent(this.data);
var ppath_$4 = this.parsedPath;
if (ppath_$4 && (ppath_$4.operator != null || ppath_$4.aggOperator != null)) {
ip_$1.applyData(this.data)
}}};
$lzsc$temp["displayName"] = "__LZApplyData";
return $lzsc$temp
})(), "setDataContext", (function () {
var $lzsc$temp = function (dc_$0, implicit_$1) {
switch (arguments.length) {
case 1:
implicit_$1 = false
};
if (dc_$0 == null && this.immediateparent != null) {
dc_$0 = this.immediateparent.searchParents("datapath").datapath;
implicit_$1 = true
} else {
if (this.immediateparent == null) {
Debug.warn("immediateparent is null for %w in '%s'", this, arguments.callee)
}};
if (dc_$0 == this.context) return;
if (implicit_$1) {
if (dc_$0.__LZdepChildren == null) {
dc_$0.__LZdepChildren = [this]
} else {
dc_$0.__LZdepChildren.push(this)
}} else {
if (this.context && (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(this.context) : this.context instanceof LzDatapath)) {
var dclist_$2 = this.context.__LZdepChildren;
if (dclist_$2) {
for (var i_$3 = 0;i_$3 < dclist_$2.length;i_$3++) {
if (dclist_$2[i_$3] === this) {
dclist_$2.splice(i_$3, 1);
break
}}}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, dc_$0)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted) return;
this.__LZupdateLocked = true;
var context_$0 = this.context;
if (context_$0 && !context_$0.__LZdeleted && (LzDatapath["$lzsc$isa"] ? LzDatapath.$lzsc$isa(context_$0) : context_$0 instanceof LzDatapath)) {
var dca_$1 = context_$0.__LZdepChildren;
if (dca_$1 != null) {
for (var i_$2 = 0;i_$2 < dca_$1.length;i_$2++) {
if (dca_$1[i_$2] === this) {
dca_$1.splice(i_$2, 1);
break
}}}};
var ip_$3 = this.immediateparent;
if (!ip_$3.__LZdeleted) {
var depChildren_$4 = this.__LZdepChildren;
if (depChildren_$4 != null && depChildren_$4.length > 0) {
var dnpar_$5 = ip_$3.searchParents("datapath").datapath;
for (var i_$2 = 0;i_$2 < depChildren_$4.length;i_$2++) {
depChildren_$4[i_$2].setDataContext(dnpar_$5, true)
};
this.__LZdepChildren = null
}};
if (ip_$3.datapath === this) {
ip_$3.datapath = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "updateData", (function () {
var $lzsc$temp = function () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function () {
var $lzsc$temp = function (recursive_$0) {
switch (arguments.length) {
case 0:
recursive_$0 = false
};
if (!recursive_$0 && this.p) {
this.p.__LZlockFromUpdate(this)
};
var ppdo_$1 = this.parsedPath ? this.parsedPath.operator : null;
if (ppdo_$1 != null) {
var dat_$2 = this.immediateparent.updateData();
if (dat_$2 !== void 0) {
if (ppdo_$1 == "name") {
this.setNodeName(dat_$2)
} else if (ppdo_$1 == "text") {
this.setNodeText(dat_$2)
} else if (ppdo_$1 == "attributes") {
this.p.$lzc$set_attributes(dat_$2)
} else {
this.setNodeAttribute(ppdo_$1.substring(11), dat_$2)
}}};
var depChildren_$3 = this.__LZdepChildren;
if (depChildren_$3 != null) {
for (var i_$4 = 0;i_$4 < depChildren_$3.length;i_$4++) {
depChildren_$3[i_$4].__LZupdateData(true)
}};
if (!recursive_$0 && this.p) {
this.p.__LZunlockFromUpdate(this)
}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Datapath for " + this.immediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "__LZcheckChange", (function () {
var $lzsc$temp = function (chgpkg_$0) {
if (!(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZcheckChange"] || this.nextMethod(arguments.callee, "__LZcheckChange")).call(this, chgpkg_$0)) {
if (chgpkg_$0.who.childOfNode(this.p, true)) {
if (this.onDocumentChange.ready) this.onDocumentChange.sendEvent(chgpkg_$0)
}};
return false
};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZsetTracking", (function () {
var $lzsc$temp = function (who_$0, additive_$1, needscheck_$2) {
switch (arguments.length) {
case 1:
additive_$1 = false;;case 2:
needscheck_$2 = false
};
if (!who_$0 || !additive_$1) {
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsetTracking"] || this.nextMethod(arguments.callee, "__LZsetTracking")).call(this, who_$0, true)
};
var tracking_$3 = this.__LZtracking;
var trackDel_$4 = this.__LZtrackDel;
if (!(tracking_$3 instanceof Array)) {
Debug.error("%w.__LZtracking is %w, expecting an array", this, tracking_$3)
};
if (needscheck_$2) {
var len_$5 = tracking_$3.length;
for (var i_$6 = 0;i_$6 < len_$5;i_$6++) {
if (tracking_$3[i_$6] === who_$0) {
return
}}};
if (!trackDel_$4) {
this.__LZtrackDel = trackDel_$4 = new LzDelegate(this, "__LZcheckChange")
};
tracking_$3.push(who_$0);
trackDel_$4.register(who_$0, "onDocumentChange")
};
$lzsc$temp["displayName"] = "__LZsetTracking";
return $lzsc$temp
})(), "$lzc$set___LZmanager", (function () {
var $lzsc$temp = function (m_$0) {
this.__LZisclone = true;
this.immediateparent.cloneManager = m_$0;
this.parsedPath = m_$0.parsedPath;
this.xpath = m_$0.xpath;
this.setDataContext(m_$0)
};
$lzsc$temp["displayName"] = "$lzc$set___LZmanager";
return $lzsc$temp
})(), "setClonePointer", (function () {
var $lzsc$temp = function (p_$0) {
var pc_$1 = this.p != p_$0;
this.p = p_$0;
if (pc_$1) {
if (p_$0 && this.sel != p_$0.sel) {
this.sel = p_$0.sel || false;
this.immediateparent.setSelected(this.sel)
};
this.__LZpchanged = true;
this.__LZsetData()
}};
$lzsc$temp["displayName"] = "setClonePointer";
return $lzsc$temp
})(), "setSelected", (function () {
var $lzsc$temp = function (torf_$0) {
this.p.sel = torf_$0;
this.sel = torf_$0;
this.immediateparent.setSelected(torf_$0)
};
$lzsc$temp["displayName"] = "setSelected";
return $lzsc$temp
})(), "__LZgetLast", (function () {
var $lzsc$temp = function () {
var context_$0 = this.context;
if (this.__LZisclone) {
return context_$0.nodes.length
} else if (this.p == context_$0.getContext() && (LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(context_$0) : context_$0 instanceof LzDatapointer)) {
return context_$0.__LZgetLast()
} else {
return 1
}};
$lzsc$temp["displayName"] = "__LZgetLast";
return $lzsc$temp
})(), "__LZgetPosition", (function () {
var $lzsc$temp = function () {
if (this.__LZisclone) {
return this.immediateparent.clonenumber + 1
} else {
var context_$0 = this.context;
if (this.p == context_$0.getContext() && (LzDatapointer["$lzsc$isa"] ? LzDatapointer.$lzsc$isa(context_$0) : context_$0 instanceof LzDatapointer)) {
return context_$0.__LZgetPosition()
} else {
return 1
}}};
$lzsc$temp["displayName"] = "__LZgetPosition";
return $lzsc$temp
})()], LzDatapointer, ["tagname", "datapath", "attributes", new LzInheritedHash(LzDatapointer.attributes)]);
lz[LzDatapath.tagname] = LzDatapath;
Class.make("LzReplicationManager", ["asyncnew", true, "initialnodes", void 0, "clonePool", void 0, "cloneClass", void 0, "cloneParent", void 0, "cloneAttrs", void 0, "cloneChildren", void 0, "hasdata", void 0, "orderpath", void 0, "comparator", void 0, "__LZxpathconstr", null, "__LZxpathdepend", null, "visible", true, "__LZpreventXPathUpdate", false, "nodes", void 0, "clones", void 0, "__LZdataoffset", 0, "onnodes", LzDeclaredEvent, "onclones", LzDeclaredEvent, "onvisible", LzDeclaredEvent, "$lzsc$initialize", (function () {
var $lzsc$temp = function (odp_$0, args_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$0, args_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function () {
var $lzsc$temp = function () {
return false
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (odp_$0, args_$1) {
this.pooling = this.getDefaultPooling();
this.__LZtakeDPSlot = false;
this.datacontrolsvisibility = false;
var view_$2 = odp_$0.immediateparent;
this.classroot = view_$2.classroot;
if (view_$2 === canvas) {
this.nodes = [];
this.clones = [];
this.clonePool = [];
Debug.error("LzReplicationManager constructed at canvas. A datapath cannot be defined on the canvas");
return
};
this.datapath = this;
var name_$3 = view_$2.name;
if (name_$3 != null) {
args_$1.name = name_$3;
view_$2.immediateparent[name_$3] = null;
view_$2.parent[name_$3] = null
};
var idbinder_$4 = view_$2.$lzc$bind_id;
if (idbinder_$4 != null) {
idbinder_$4.call(null, view_$2, false);
view_$2.$lzc$bind_id = null;
this.$lzc$bind_id = idbinder_$4;
idbinder_$4.call(null, this)
};
var namebinder_$5 = view_$2.$lzc$bind_name;
if (namebinder_$5 != null) {
namebinder_$5.call(null, view_$2, false);
view_$2.$lzc$bind_name = null;
this.$lzc$bind_name = namebinder_$5;
namebinder_$5.call(null, this)
};
args_$1.xpath = LzNode._ignoreAttribute;
if (odp_$0.sortpath != null) {
args_$1.sortpath = odp_$0.sortpath
};
if (odp_$0.sortorder != null || odp_$0.sortorder) {
args_$1.sortorder = odp_$0.sortorder
};
this.initialnodes = odp_$0.storednodes;
if (odp_$0.__LZspecialDotDot) {
this.__LZspecialDotDot = true;
if (odp_$0.__LZdotdotCheckDel) {
odp_$0.__LZdotdotCheckDel.unregisterAll()
};
odp_$0.__LZspecialDotDot = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, view_$2.parent, args_$1);
if (args_$1.name != null && view_$2.parent != view_$2.immediateparent) {
view_$2.immediateparent[args_$1.name] = this
};
this.xpath = odp_$0.xpath;
this.parsedPath = odp_$0.parsedPath;
this.cloneClass = view_$2.constructor;
this.cloneParent = view_$2.parent;
var cloneAttrs_$6 = new LzInheritedHash(view_$2._instanceAttrs);
cloneAttrs_$6.datapath = LzNode._ignoreAttribute;
cloneAttrs_$6.$datapath = {"class": lz.datapath};
cloneAttrs_$6.$datapath.attrs = {datacontrolsvisibility: odp_$0.datacontrolsvisibility, __LZmanager: this};
delete cloneAttrs_$6.id;
delete cloneAttrs_$6.name;
delete cloneAttrs_$6.$lzc$bind_id;
delete cloneAttrs_$6.$lzc$bind_name;
this.cloneAttrs = cloneAttrs_$6;
if (odp_$0.datacontrolsvisibility) {
this.visible = true
} else {
if (!view_$2.isinited) {
var vis_$7 = this.__LZgetInstanceAttr(view_$2, "visible");
if (typeof vis_$7 == "boolean" || (Boolean["$lzsc$isa"] ? Boolean.$lzsc$isa(vis_$7) : vis_$7 instanceof Boolean)) {
this.visible = vis_$7
} else {
this.visible = view_$2.visible
}} else {
this.visible = view_$2.visible
}};
if (args_$1.pooling != null) {
this.pooling = args_$1.pooling
};
var dp_$8 = this.__LZgetInstanceAttr(view_$2, "datapath");
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(dp_$8) : dp_$8 instanceof LzAlwaysExpr) {
var dpCons_$9 = dp_$8;
this.__LZxpathconstr = view_$2[dpCons_$9.methodName];
this.__LZxpathdepend = view_$2[dpCons_$9.dependenciesName];
this.__LZpreventXPathUpdate = true;
this.applyConstraintExpr(new LzAlwaysExpr("datapath", "string", "__LZxpathconstr", "__LZxpathdepend"));
this.__LZpreventXPathUpdate = false;
if (this.pooling) {
view_$2.releaseConstraintMethod(dpCons_$9.methodName)
}} else {
var xp_$a = this.__LZgetInstanceAttr(odp_$0, "xpath");
if (LzAlwaysExpr["$lzsc$isa"] ? LzAlwaysExpr.$lzsc$isa(xp_$a) : xp_$a instanceof LzAlwaysExpr) {
var refObj_$b = new LzRefNode(this);
var xpCons_$c = xp_$a;
refObj_$b.__LZxpathconstr = odp_$0[xpCons_$c.methodName];
refObj_$b.__LZxpathdepend = odp_$0[xpCons_$c.dependenciesName];
this.__LZpreventXPathUpdate = true;
refObj_$b.applyConstraintExpr(new LzAlwaysExpr("xpath", "string", "__LZxpathconstr", "__LZxpathdepend"));
this.__LZpreventXPathUpdate = false;
if (this.pooling) {
odp_$0.releaseConstraintMethod(xpCons_$c.methodName)
}}};
this.__LZsetCloneAttrs();
if (view_$2._instanceChildren) {
this.cloneChildren = view_$2._instanceChildren.concat()
} else {
this.cloneChildren = []
};
var mycontext_$d = odp_$0.context;
this.clones = [];
this.clonePool = [];
if (this.pooling) {
odp_$0.$lzc$set___LZmanager(this);
this.clones.push(view_$2);
view_$2.immediateparent.addSubview(view_$2)
} else {
this.destroyClone(view_$2)
};
this.setDataContext(mycontext_$d, mycontext_$d instanceof LzDatapointer)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZgetInstanceAttr", (function () {
var $lzsc$temp = function (node_$0, attr_$1) {
var ia_$2 = node_$0._instanceAttrs;
if (ia_$2 && (attr_$1 in ia_$2)) {
return ia_$2[attr_$1]
} else {
var ca_$3 = node_$0["constructor"].attributes;
if (ca_$3 && (attr_$1 in ca_$3)) {
return ca_$3[attr_$1]
}};
return void 0
};
$lzsc$temp["displayName"] = "__LZgetInstanceAttr";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "__LZapplyArgs", (function () {
var $lzsc$temp = function (args_$0, constcall_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZapplyArgs"] || this.nextMethod(arguments.callee, "__LZapplyArgs")).call(this, args_$0, constcall_$1);
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
})(), "setDataContext", (function () {
var $lzsc$temp = function (p_$0, implicit_$1) {
switch (arguments.length) {
case 1:
implicit_$1 = false
};
if (p_$0 == null && this.immediateparent != null && this.immediateparent["datapath"] != null) {
p_$0 = this.immediateparent.datapath;
implicit_$1 = true
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, p_$0, implicit_$1)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "getCloneNumber", (function () {
var $lzsc$temp = function (n_$0) {
return this.clones[n_$0]
};
$lzsc$temp["displayName"] = "getCloneNumber";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function () {
var $lzsc$temp = function () {
this.nodes = [];
var cls_$0 = this.clones;
while (cls_$0.length) {
if (this.pooling) {
this.poolClone()
} else {
var v_$1 = cls_$0.pop();
this.destroyClone(v_$1)
}}};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZHandleSingleNode", (function () {
var $lzsc$temp = function (n_$0) {
this.__LZHandleMultiNodes([n_$0])
};
$lzsc$temp["displayName"] = "__LZHandleSingleNode";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function () {
var $lzsc$temp = function (n_$0) {
var layouts_$1 = this.parent && this.parent.layouts ? this.parent.layouts : [];
for (var i_$2 = 0;i_$2 < layouts_$1.length;++i_$2) {
layouts_$1[i_$2].lock()
};
this.hasdata = true;
var lastnodes_$3 = this.nodes;
this.nodes = n_$0;
if (this.onnodes.ready) this.onnodes.sendEvent(this.nodes);
if (this.__LZspecialDotDot) this.__LZsetupDotDot(n_$0[0]);
if (this.orderpath != null && this.nodes) {
this.nodes = this.mergesort(this.nodes, 0, this.nodes.length - 1)
};
this.__LZadjustVisibleClones(lastnodes_$3, true);
var len_$4 = this.clones.length;
for (var i_$2 = 0;i_$2 < len_$4;i_$2++) {
var cl_$5 = this.clones[i_$2];
var iplusoffset_$6 = i_$2 + this.__LZdataoffset;
cl_$5.clonenumber = iplusoffset_$6;
if (this.nodes) {
cl_$5.datapath.setClonePointer(this.nodes[iplusoffset_$6])
};
if (cl_$5.onclonenumber.ready) cl_$5.onclonenumber.sendEvent(iplusoffset_$6)
};
if (this.onclones.ready) this.onclones.sendEvent(this.clones);
for (var i_$2 = 0;i_$2 < layouts_$1.length;++i_$2) {
layouts_$1[i_$2].unlock()
};
return null
};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function () {
var $lzsc$temp = function (lastnodes_$0, newnodes_$1) {
var stpt_$2 = this.__LZdiffArrays(lastnodes_$0, this.nodes);
if (!this.pooling) {
while (this.clones.length > stpt_$2) {
var v_$3 = this.clones.pop();
this.destroyClone(v_$3)
}};
lz.Instantiator.enableDataReplicationQueuing(this);
while (this.nodes && this.nodes.length > this.clones.length) {
var cl_$4 = this.getNewClone();
if (!cl_$4) break;
this.clones.push(cl_$4)
};
lz.Instantiator.clearDataReplicationQueue(this);
while (this.nodes && this.nodes.length < this.clones.length) {
this.poolClone()
}};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})(), "mergesort", (function () {
var $lzsc$temp = function (arr_$0, lo_$1, hi_$2) {
if (lo_$1 < hi_$2) {
var mid_$3 = lo_$1 + Math.floor((hi_$2 - lo_$1) / 2);
var a_$4 = this.mergesort(arr_$0, lo_$1, mid_$3);
var b_$5 = this.mergesort(arr_$0, mid_$3 + 1, hi_$2)
} else if (arr_$0.length == 0) {
return []
} else {
return [arr_$0[lo_$1]]
};
var r_$6 = [];
var ia_$7 = 0;
var ib_$8 = 0;
var al_$9 = a_$4.length;
var bl_$a = b_$5.length;
while (ia_$7 < al_$9 && ib_$8 < bl_$a) {
if (this.orderf(b_$5[ib_$8], a_$4[ia_$7]) == 1) {
r_$6.push(b_$5[ib_$8++])
} else {
r_$6.push(a_$4[ia_$7++])
}};
while (ia_$7 < al_$9) r_$6.push(a_$4[ia_$7++]);
while (ib_$8 < bl_$a) r_$6.push(b_$5[ib_$8++]);
return r_$6
};
$lzsc$temp["displayName"] = "mergesort";
return $lzsc$temp
})(), "orderf", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var op_$2 = this.orderpath;
this.p = a_$0;
var aa_$3 = this.xpathQuery(op_$2);
this.p = b_$1;
var bb_$4 = this.xpathQuery(op_$2);
this.p = null;
if (aa_$3 == null || aa_$3 == "") aa_$3 = "\n";
if (bb_$4 == null || bb_$4 == "") bb_$4 = "\n";
return this.comparator(aa_$3, bb_$4)
};
$lzsc$temp["displayName"] = "orderf";
return $lzsc$temp
})(), "ascDict", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
if (a_$0.toLowerCase() < b_$1.toLowerCase()) {
return 1
} else {
return 0
}};
$lzsc$temp["displayName"] = "ascDict";
return $lzsc$temp
})(), "descDict", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
if (a_$0.toLowerCase() > b_$1.toLowerCase()) {
return 1
} else {
return 0
}};
$lzsc$temp["displayName"] = "descDict";
return $lzsc$temp
})(), "setOrder", (function () {
var $lzsc$temp = function (xpath_$0, comparator_$1) {
switch (arguments.length) {
case 1:
comparator_$1 = null
};
this.orderpath = null;
if (comparator_$1 != null) {
this.setComparator(comparator_$1)
};
this.orderpath = xpath_$0;
if (this.nodes && this.nodes.length) {
this.__LZHandleMultiNodes(this.nodes)
}};
$lzsc$temp["displayName"] = "setOrder";
return $lzsc$temp
})(), "setComparator", (function () {
var $lzsc$temp = function (comparator_$0) {
if (comparator_$0 == "descending") {
comparator_$0 = this.descDict
} else if (comparator_$0 == "ascending") {
comparator_$0 = this.ascDict
} else if (Function["$lzsc$isa"] ? Function.$lzsc$isa(comparator_$0) : comparator_$0 instanceof Function) {

} else {
Debug.error("Invalid comparator: %s", comparator_$0)
};
this.comparator = comparator_$0;
if (this.orderpath != null && this.nodes && this.nodes.length) {
this.__LZHandleMultiNodes(this.nodes)
}};
$lzsc$temp["displayName"] = "setComparator";
return $lzsc$temp
})(), "getNewClone", (function () {
var $lzsc$temp = function (forceNew_$0) {
switch (arguments.length) {
case 0:
forceNew_$0 = null
};
if (!this.cloneParent) {
return null
};
if (this.clonePool.length) {
var v_$1 = this.reattachClone(this.clonePool.pop())
} else {
var v_$1 = new (this.cloneClass)(this.cloneParent, this.cloneAttrs, this.cloneChildren, forceNew_$0 == null ? this.asyncnew : !forceNew_$0)
};
if (this.visible == false) v_$1.$lzc$set_visible(false);
return v_$1
};
$lzsc$temp["displayName"] = "getNewClone";
return $lzsc$temp
})(), "poolClone", (function () {
var $lzsc$temp = function () {
var v_$0 = this.clones.pop();
this.detachClone(v_$0);
this.clonePool.push(v_$0)
};
$lzsc$temp["displayName"] = "poolClone";
return $lzsc$temp
})(), "destroyClone", (function () {
var $lzsc$temp = function (v_$0) {
v_$0.destroy()
};
$lzsc$temp["displayName"] = "destroyClone";
return $lzsc$temp
})(), "$lzc$set_datapath", (function () {
var $lzsc$temp = function (xp_$0) {
this.setXPath(xp_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_datapath";
return $lzsc$temp
})(), "setXPath", (function () {
var $lzsc$temp = function (xp_$0) {
if (this.__LZpreventXPathUpdate) return false;
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setXPath"] || this.nextMethod(arguments.callee, "setXPath")).apply(this, arguments)
};
$lzsc$temp["displayName"] = "setXPath";
return $lzsc$temp
})(), "handleDeletedNode", (function () {
var $lzsc$temp = function (c_$0) {
var tclone_$1 = this.clones[c_$0];
if (this.pooling) {
this.detachClone(tclone_$1);
this.clonePool.push(tclone_$1)
} else {
this.destroyClone(tclone_$1)
};
this.nodes.splice(c_$0, 1);
this.clones.splice(c_$0, 1)
};
$lzsc$temp["displayName"] = "handleDeletedNode";
return $lzsc$temp
})(), "getCloneForNode", (function () {
var $lzsc$temp = function (p_$0, dontmake_$1) {
switch (arguments.length) {
case 1:
dontmake_$1 = false
};
var cls_$2 = this.clones;
var len_$3 = cls_$2.length;
for (var i_$4 = 0;i_$4 < len_$3;i_$4++) {
if (cls_$2[i_$4].datapath.p == p_$0) {
return cls_$2[i_$4]
}};
return null
};
$lzsc$temp["displayName"] = "getCloneForNode";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "ReplicationManager in " + this.immediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "setVisible", (function () {
var $lzsc$temp = function (vis_$0) {
Debug.deprecated(this, arguments.callee, this.setAttribute);
this.$lzc$set_visible(vis_$0)
};
$lzsc$temp["displayName"] = "setVisible";
return $lzsc$temp
})(), "$lzc$set_visible", (function () {
var $lzsc$temp = function (vis_$0) {
this.visible = vis_$0;
var cls_$1 = this.clones;
var len_$2 = cls_$1.length;
for (var i_$3 = 0;i_$3 < len_$2;i_$3++) {
cls_$1[i_$3].$lzc$set_visible(vis_$0)
};
if (this.onvisible.ready) this.onvisible.sendEvent(vis_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_visible";
return $lzsc$temp
})(), "__LZcheckChange", (function () {
var $lzsc$temp = function (chgpkg_$0) {
this.p = this.nodes[0];
var didrun_$1 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZcheckChange"] || this.nextMethod(arguments.callee, "__LZcheckChange")).call(this, chgpkg_$0);
this.p = null;
if (!didrun_$1) {
var who_$2 = chgpkg_$0.who;
var cls_$3 = this.clones;
var len_$4 = cls_$3.length;
for (var i_$5 = 0;i_$5 < len_$4;i_$5++) {
var cl_$6 = cls_$3[i_$5];
var dp_$7 = cl_$6.datapath;
if (dp_$7.__LZneedsOpUpdate(chgpkg_$0)) {
dp_$7.__LZsetData()
};
if (who_$2.childOfNode(dp_$7.p, true)) {
if (dp_$7.onDocumentChange.ready) dp_$7.onDocumentChange.sendEvent(chgpkg_$0)
}}};
return false
};
$lzsc$temp["displayName"] = "__LZcheckChange";
return $lzsc$temp
})(), "__LZneedsOpUpdate", (function () {
var $lzsc$temp = function (chgpkg_$0) {
switch (arguments.length) {
case 0:
chgpkg_$0 = null
};
return false
};
$lzsc$temp["displayName"] = "__LZneedsOpUpdate";
return $lzsc$temp
})(), "getContext", (function () {
var $lzsc$temp = function (chgpkg_$0) {
switch (arguments.length) {
case 0:
chgpkg_$0 = null
};
return this.nodes[0]
};
$lzsc$temp["displayName"] = "getContext";
return $lzsc$temp
})(), "detachClone", (function () {
var $lzsc$temp = function (cl_$0) {
if (cl_$0.isdetatchedclone) return;
cl_$0.$lzc$set_visible(false);
cl_$0.addedToParent = false;
var svs_$1 = cl_$0.immediateparent.subviews;
for (var i_$2 = svs_$1.length - 1;i_$2 >= 0;i_$2--) {
if (svs_$1[i_$2] == cl_$0) {
svs_$1.splice(i_$2, 1);
break
}};
cl_$0.datapath.__LZtrackDel.unregisterAll();
var onremsub_$3 = cl_$0.immediateparent.onremovesubview;
if (onremsub_$3.ready) onremsub_$3.sendEvent(cl_$0);
cl_$0.isdetatchedclone = true;
cl_$0.datapath.p = null
};
$lzsc$temp["displayName"] = "detachClone";
return $lzsc$temp
})(), "reattachClone", (function () {
var $lzsc$temp = function (cl_$0) {
if (!cl_$0.isdetatchedclone) return cl_$0;
cl_$0.immediateparent.addSubview(cl_$0);
cl_$0.$lzc$set_visible(this.visible);
cl_$0.isdetatchedclone = false;
return cl_$0
};
$lzsc$temp["displayName"] = "reattachClone";
return $lzsc$temp
})(), "__LZdiffArrays", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var i_$2 = 0;
var al_$3 = a_$0 ? a_$0.length : 0;
var bl_$4 = b_$1 ? b_$1.length : 0;
while (i_$2 < al_$3 && i_$2 < bl_$4) {
if (a_$0[i_$2] != b_$1[i_$2]) {
return i_$2
};
i_$2++
};
return i_$2
};
$lzsc$temp["displayName"] = "__LZdiffArrays";
return $lzsc$temp
})(), "updateData", (function () {
var $lzsc$temp = function () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function () {
var $lzsc$temp = function (recursive_$0) {
switch (arguments.length) {
case 0:
recursive_$0 = false
};
var cls_$1 = this.clones;
var len_$2 = cls_$1.length;
for (var i_$3 = 0;i_$3 < len_$2;i_$3++) {
cls_$1[i_$3].datapath.updateData()
}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})()], LzDatapath);
lz.ReplicationManager = LzReplicationManager;
Class.make("LzRefNode", ["__LZxpathconstr", null, "__LZxpathdepend", null, "xpath", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$0, attrs_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 1:
attrs_$1 = null;;case 2:
children_$2 = null;;case 3:
instcall_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$0, attrs_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$lzc$set_xpath", (function () {
var $lzsc$temp = function (v_$0) {
this.parent.$lzc$set_xpath(v_$0)
};
$lzsc$temp["displayName"] = "$lzc$set_xpath";
return $lzsc$temp
})()], LzNode);
Class.make("LzDataAttrBind", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (ndpath_$0, attr_$1, path_$2, type_$3) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, ndpath_$0);
this.type = type_$3;
this.setAttr = attr_$1;
this.pathparent = ndpath_$0;
this.node = ndpath_$0.immediateparent;
this.setXPath(path_$2);
this.rerunxpath = true;
if (ndpath_$0.__LZdepChildren == null) {
ndpath_$0.__LZdepChildren = [this]
} else {
ndpath_$0.__LZdepChildren.push(this)
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "$pathbinding", true, "setAttr", void 0, "pathparent", void 0, "node", void 0, "type", void 0, "__LZlast", -1, "__LZsendUpdate", (function () {
var $lzsc$temp = function (upd_$0, upp_$1) {
switch (arguments.length) {
case 0:
upd_$0 = false;;case 1:
upp_$1 = false
};
var pchg_$2 = this.__LZpchanged;
var result_$3 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsendUpdate"] || this.nextMethod(arguments.callee, "__LZsendUpdate")).call(this, upd_$0, upp_$1);
if (result_$3) {
var data_$4 = this.data;
var node_$5 = this.node;
var attr_$6 = this.setAttr;
if (data_$4 == null) {
data_$4 = null
};
var newvalue_$7 = lz.Type.acceptTypeValue(this.type, data_$4, node_$5, attr_$6);
if (pchg_$2 || node_$5[attr_$6] !== newvalue_$7 || !node_$5.inited || this.parsedPath.operator == "attributes") {
{
if (!node_$5.__LZdeleted) {
var $lzsc$fcn694 = "$lzc$set_" + attr_$6;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(node_$5[$lzsc$fcn694]) : node_$5[$lzsc$fcn694] instanceof Function) {
node_$5[$lzsc$fcn694](newvalue_$7)
} else {
node_$5[attr_$6] = newvalue_$7;
var $lzsc$o6sk0 = node_$5["on" + attr_$6];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$o6sk0) : $lzsc$o6sk0 instanceof LzEvent) {
if ($lzsc$o6sk0.ready) {
$lzsc$o6sk0.sendEvent(newvalue_$7)
}}}}}}};
return result_$3
};
$lzsc$temp["displayName"] = "__LZsendUpdate";
return $lzsc$temp
})(), "destroy", (function () {
var $lzsc$temp = function () {
if (this.__LZdeleted) return;
var dca_$0 = this.pathparent.__LZdepChildren;
if (dca_$0 != null) {
for (var i_$1 = 0;i_$1 < dca_$0.length;i_$1++) {
if (dca_$0[i_$1] === this) {
dca_$0.splice(i_$1, 1);
break
}}};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["destroy"] || this.nextMethod(arguments.callee, "destroy")).call(this)
};
$lzsc$temp["displayName"] = "destroy";
return $lzsc$temp
})(), "setDataContext", (function () {
var $lzsc$temp = function (dc_$0, implicit_$1) {
switch (arguments.length) {
case 1:
implicit_$1 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["setDataContext"] || this.nextMethod(arguments.callee, "setDataContext")).call(this, dc_$0 || this.pathparent, implicit_$1)
};
$lzsc$temp["displayName"] = "setDataContext";
return $lzsc$temp
})(), "updateData", (function () {
var $lzsc$temp = function () {
this.__LZupdateData()
};
$lzsc$temp["displayName"] = "updateData";
return $lzsc$temp
})(), "__LZupdateData", (function () {
var $lzsc$temp = function (recursive_$0) {
switch (arguments.length) {
case 0:
recursive_$0 = false
};
var ppdo_$1 = this.parsedPath.operator;
if (ppdo_$1 != null) {
var dat_$2 = this.node.presentAttribute(this.setAttr, this.type);
if (this.data != dat_$2) {
if (ppdo_$1 == "name") {
this.setNodeName(dat_$2)
} else if (ppdo_$1 == "text") {
this.setNodeText(dat_$2)
} else if (ppdo_$1 == "attributes") {
this.p.$lzc$set_attributes(dat_$2)
} else {
this.setNodeAttribute(ppdo_$1.substring(11), dat_$2)
}}}};
$lzsc$temp["displayName"] = "__LZupdateData";
return $lzsc$temp
})(), "__LZHandleMultiNodes", (function () {
var $lzsc$temp = function (n_$0) {
var pp_$1 = this.parsedPath;
if (pp_$1 && pp_$1.aggOperator == "last") {
this.__LZlast = n_$0.length;
this.__LZHandleSingleNode(n_$0[0]);
return null
} else {
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZHandleMultiNodes"] || this.nextMethod(arguments.callee, "__LZHandleMultiNodes")).call(this, n_$0)
}};
$lzsc$temp["displayName"] = "__LZHandleMultiNodes";
return $lzsc$temp
})(), "__LZgetLast", (function () {
var $lzsc$temp = function () {
return this.__LZlast != -1 ? this.__LZlast : (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZgetLast"] || this.nextMethod(arguments.callee, "__LZgetLast")).call(this)
};
$lzsc$temp["displayName"] = "__LZgetLast";
return $lzsc$temp
})(), "runXPath", (function () {
var $lzsc$temp = function () {
this.__LZlast = -1;
return (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["runXPath"] || this.nextMethod(arguments.callee, "runXPath")).call(this)
};
$lzsc$temp["displayName"] = "runXPath";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "binder " + this.xpath
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})()], LzDatapointer);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzDataAttrBind.prototype._dbg_name = (function () {
var $lzsc$temp = function () {
return Debug.formatToString('%w.%s="$path{%w}"', this.node, this.setAttr, this.xpath)
};
$lzsc$temp["displayName"] = "data/LzDataAttrBind.lzs#173/43";
return $lzsc$temp
})()
}}};
$lzsc$temp["displayName"] = "data/LzDataAttrBind.lzs#12/1";
return $lzsc$temp
})()(LzDataAttrBind);
Class.make("LzLazyReplicationManager", ["sizeAxis", void 0, "cloneimmediateparent", void 0, "updateDel", void 0, "__LZoldnodelen", void 0, "viewsize", 0, "totalsize", 0, "mask", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function (odp_$0, args_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$0, args_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function () {
var $lzsc$temp = function () {
return true
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (odp_$0, args_$1) {
if (args_$1.pooling != null) {
args_$1.pooling = true;
Debug.warn("Invalid pooling argument specified " + "with lazy replication in %w", this)
};
if (args_$1.axis != null) {
this.axis = args_$1.axis
};
this.sizeAxis = this.axis == "x" ? "width" : "height";
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, odp_$0, args_$1);
this.mask = odp_$0.immediateparent.immediateparent.mask;
var cloneopt_$2;
if (this.cloneAttrs.options != null) {
cloneopt_$2 = new LzInheritedHash(this.cloneAttrs.options);
cloneopt_$2["ignorelayout"] = true
} else {
cloneopt_$2 = {ignorelayout: true}};
var firstcl_$3 = this.clones[0];
if (firstcl_$3) {
firstcl_$3.setOption("ignorelayout", true);
var layo_$4 = firstcl_$3.immediateparent.layouts;
if (layo_$4 != null) {
for (var i_$5 = 0;i_$5 < layo_$4.length;i_$5++) {
layo_$4[i_$5].removeSubview(firstcl_$3)
}}};
this.cloneAttrs.options = cloneopt_$2;
var v_$6 = this.getNewClone(true);
this.cloneimmediateparent = v_$6.immediateparent;
if (this.initialnodes) {
v_$6.datapath.setClonePointer(this.initialnodes[1])
};
this.viewsize = v_$6[this.sizeAxis];
v_$6.datapath.setClonePointer(null);
this.clones.push(v_$6);
if (args_$1.spacing == null) {
args_$1.spacing = 0
};
this.totalsize = this.viewsize + args_$1.spacing;
{
var $lzsc$y67dk = this.axis;
var $lzsc$a4uci7 = this.totalsize;
if (!v_$6.__LZdeleted) {
var $lzsc$hiz5xd = "$lzc$set_" + $lzsc$y67dk;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(v_$6[$lzsc$hiz5xd]) : v_$6[$lzsc$hiz5xd] instanceof Function) {
v_$6[$lzsc$hiz5xd]($lzsc$a4uci7)
} else {
v_$6[$lzsc$y67dk] = $lzsc$a4uci7;
var $lzsc$vsp9uf = v_$6["on" + $lzsc$y67dk];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$vsp9uf) : $lzsc$vsp9uf instanceof LzEvent) {
if ($lzsc$vsp9uf.ready) {
$lzsc$vsp9uf.sendEvent($lzsc$a4uci7)
}}}}};
this.__LZdataoffset = 0;
this.updateDel = new LzDelegate(this, "__LZhandleUpdate");
this.updateDel.register(this.cloneimmediateparent, "on" + this.axis);
this.updateDel.register(this.mask, "on" + this.sizeAxis)
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZhandleUpdate", (function () {
var $lzsc$temp = function (ignore_$0) {
this.__LZadjustVisibleClones(null, null)
};
$lzsc$temp["displayName"] = "__LZhandleUpdate";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function () {
var $lzsc$temp = function () {
var cloneopt_$0;
if (this.cloneAttrs.options != null) {
cloneopt_$0 = new LzInheritedHash(this.cloneAttrs.options);
cloneopt_$0["ignorelayout"] = true
} else {
cloneopt_$0 = {ignorelayout: true}};
this.cloneAttrs.options = cloneopt_$0
};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "__LZHandleNoNodes", (function () {
var $lzsc$temp = function () {
this.__LZHandleMultiNodes([])
};
$lzsc$temp["displayName"] = "__LZHandleNoNodes";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function () {
var $lzsc$temp = function (lastnodes_$0, newnodes_$1) {
var cloneip_$2 = this.cloneimmediateparent;
var _nodes_$3 = this.nodes;
var _axis_$4 = this.axis;
var _sizeAxis_$5 = this.sizeAxis;
var _totalsize_$6 = this.totalsize;
if (_nodes_$3) {
var nodelen_$7 = _nodes_$3.length;
if (this.__LZoldnodelen != nodelen_$7) {
{
var $lzsc$gxaf1z = nodelen_$7 * _totalsize_$6 - this.spacing;
if (!cloneip_$2.__LZdeleted) {
var $lzsc$svn2rs = "$lzc$set_" + _sizeAxis_$5;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cloneip_$2[$lzsc$svn2rs]) : cloneip_$2[$lzsc$svn2rs] instanceof Function) {
cloneip_$2[$lzsc$svn2rs]($lzsc$gxaf1z)
} else {
cloneip_$2[_sizeAxis_$5] = $lzsc$gxaf1z;
var $lzsc$6i499e = cloneip_$2["on" + _sizeAxis_$5];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$6i499e) : $lzsc$6i499e instanceof LzEvent) {
if ($lzsc$6i499e.ready) {
$lzsc$6i499e.sendEvent($lzsc$gxaf1z)
}}}}};
this.__LZoldnodelen = nodelen_$7
}};
if (!(this.mask && this.mask["hasset" + _sizeAxis_$5])) return;
var newstart_$8 = 0;
if (_totalsize_$6 != 0) {
newstart_$8 = Math.floor(-cloneip_$2[_axis_$4] / _totalsize_$6);
if (0 > newstart_$8) newstart_$8 = 0
};
var oldstart_$9 = 0;
var oldlength_$a = this.clones.length;
var offset_$b = newstart_$8 - this.__LZdataoffset;
var remainder_$c = newstart_$8 * _totalsize_$6 + cloneip_$2[_axis_$4];
var newlength_$d = 0;
if (typeof remainder_$c == "number") {
newlength_$d = 1 + Math.floor((this.mask[_sizeAxis_$5] - remainder_$c) / _totalsize_$6)
};
if (_nodes_$3 != null) {
var nodelen_$7 = _nodes_$3.length;
if (newlength_$d + newstart_$8 > nodelen_$7) {
newlength_$d = nodelen_$7 - newstart_$8
}};
if (offset_$b == 0 && newlength_$d == oldlength_$a) return;
lz.Instantiator.enableDataReplicationQueuing(this);
var oldclones_$e = this.clones;
this.clones = [];
for (var i_$f = 0;i_$f < newlength_$d;i_$f++) {
var cl_$g = null;
if (i_$f + offset_$b < 0) {
if (newlength_$d + offset_$b < oldlength_$a && oldlength_$a > 0) {
cl_$g = oldclones_$e[--oldlength_$a]
} else {
cl_$g = this.getNewClone()
}} else if (i_$f + offset_$b >= oldlength_$a) {
if (oldstart_$9 < offset_$b && oldstart_$9 < oldlength_$a) {
cl_$g = oldclones_$e[oldstart_$9++]
} else {
cl_$g = this.getNewClone()
}};
if (cl_$g) {
this.clones[i_$f] = cl_$g;
{
var $lzsc$xnxvuq = (i_$f + newstart_$8) * _totalsize_$6;
if (!cl_$g.__LZdeleted) {
var $lzsc$sbeuax = "$lzc$set_" + _axis_$4;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$g[$lzsc$sbeuax]) : cl_$g[$lzsc$sbeuax] instanceof Function) {
cl_$g[$lzsc$sbeuax]($lzsc$xnxvuq)
} else {
cl_$g[_axis_$4] = $lzsc$xnxvuq;
var $lzsc$f4evwa = cl_$g["on" + _axis_$4];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$f4evwa) : $lzsc$f4evwa instanceof LzEvent) {
if ($lzsc$f4evwa.ready) {
$lzsc$f4evwa.sendEvent($lzsc$xnxvuq)
}}}}};
cl_$g.clonenumber = newstart_$8 + i_$f;
if (_nodes_$3) {
cl_$g.datapath.setClonePointer(_nodes_$3[newstart_$8 + i_$f])
};
if (cl_$g.onclonenumber.ready) cl_$g.onclonenumber.sendEvent(i_$f)
} else {
this.clones[i_$f] = oldclones_$e[i_$f + offset_$b]
}};
var cpool_$h = this.clonePool;
while (oldstart_$9 < offset_$b && oldstart_$9 < oldlength_$a) {
var v_$i = oldclones_$e[oldstart_$9++];
this.detachClone(v_$i);
cpool_$h.push(v_$i)
};
while (oldlength_$a > newlength_$d + offset_$b && oldlength_$a > 0) {
var v_$i = oldclones_$e[--oldlength_$a];
this.detachClone(v_$i);
cpool_$h.push(v_$i)
};
this.__LZdataoffset = newstart_$8;
lz.Instantiator.clearDataReplicationQueue(this)
};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})(), "toString", (function () {
var $lzsc$temp = function () {
return "Lazy clone manager in " + this.cloneimmediateparent
};
$lzsc$temp["displayName"] = "toString";
return $lzsc$temp
})(), "getCloneForNode", (function () {
var $lzsc$temp = function (p_$0, dontmake_$1) {
switch (arguments.length) {
case 1:
dontmake_$1 = false
};
var cl_$2 = (arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["getCloneForNode"] || this.nextMethod(arguments.callee, "getCloneForNode")).call(this, p_$0) || null;
if (!cl_$2 && !dontmake_$1) {
cl_$2 = this.getNewClone();
cl_$2.datapath.setClonePointer(p_$0);
this.detachClone(cl_$2);
this.clonePool.push(cl_$2)
};
return cl_$2
};
$lzsc$temp["displayName"] = "getCloneForNode";
return $lzsc$temp
})(), "getCloneNumber", (function () {
var $lzsc$temp = function (n_$0) {
return this.getCloneForNode(this.nodes[n_$0])
};
$lzsc$temp["displayName"] = "getCloneNumber";
return $lzsc$temp
})()], LzReplicationManager);
lz.LazyReplicationManager = LzLazyReplicationManager;
Class.make("LzResizeReplicationManager", ["datasizevar", void 0, "__LZresizeupdating", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (odp_$0, args_$1, children_$2, instcall_$3) {
switch (arguments.length) {
case 2:
children_$2 = null;;case 3:
instcall_$3 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, odp_$0, args_$1, children_$2, instcall_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getDefaultPooling", (function () {
var $lzsc$temp = function () {
return false
};
$lzsc$temp["displayName"] = "getDefaultPooling";
return $lzsc$temp
})(), "construct", (function () {
var $lzsc$temp = function (odp_$0, args_$1) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["construct"] || this.nextMethod(arguments.callee, "construct")).call(this, odp_$0, args_$1);
this.datasizevar = "$" + this.getUID() + "track"
};
$lzsc$temp["displayName"] = "construct";
return $lzsc$temp
})(), "__LZHandleCloneResize", (function () {
var $lzsc$temp = function (s_$0) {
var p_$1 = this.datapath.p;
if (p_$1) {
var cloneManager_$2 = this.cloneManager;
var datasizevar_$3 = cloneManager_$2.datasizevar;
var osize_$4 = p_$1.getUserData(datasizevar_$3) || cloneManager_$2.viewsize;
if (s_$0 != osize_$4) {
p_$1.setUserData(datasizevar_$3, s_$0);
cloneManager_$2.__LZadjustVisibleClones(null, false)
}}};
$lzsc$temp["displayName"] = "data/LzResizeReplicationManager.lzs#125/29";
return $lzsc$temp
})(), "__LZsetCloneAttrs", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["__LZsetCloneAttrs"] || this.nextMethod(arguments.callee, "__LZsetCloneAttrs")).call(this);
var cattrs_$0 = this.cloneAttrs;
cattrs_$0.__LZHandleCloneResize = this.__LZHandleCloneResize;
if (!cattrs_$0["$delegates"]) {
cattrs_$0.$delegates = []
};
cattrs_$0.$delegates.push("on" + (this.axis == "y" ? "height" : "width"), "__LZHandleCloneResize", null)
};
$lzsc$temp["displayName"] = "__LZsetCloneAttrs";
return $lzsc$temp
})(), "getPositionByNode", (function () {
var $lzsc$temp = function (n_$0) {
var pos_$1 = -this.spacing;
var cnode_$2;
if (this.nodes != null) {
for (var i_$3 = 0;i_$3 < this.nodes.length;i_$3++) {
cnode_$2 = this.nodes[i_$3];
if (n_$0 == this.nodes[i_$3]) {
return pos_$1 + this.spacing
};
pos_$1 += this.spacing + (cnode_$2.getUserData(this.datasizevar) || this.viewsize)
}};
return undefined
};
$lzsc$temp["displayName"] = "getPositionByNode";
return $lzsc$temp
})(), "__LZreleaseClone", (function () {
var $lzsc$temp = function (v_$0) {
this.detachClone(v_$0);
this.clonePool.push(v_$0)
};
$lzsc$temp["displayName"] = "__LZreleaseClone";
return $lzsc$temp
})(), "__LZadjustVisibleClones", (function () {
var $lzsc$temp = function (lastnodes_$0, newnodes_$1) {
if (!this.mask["hasset" + this.sizeAxis]) return;
if (this.__LZresizeupdating) return;
this.__LZresizeupdating = true;
var nodelen_$2 = this.nodes != null ? this.nodes.length : 0;
var newstart_$3 = Math.floor(-this.cloneimmediateparent[this.axis]);
if (0 > newstart_$3) newstart_$3 = 0;
var masksize_$4 = this.mask[this.sizeAxis];
var newoffset_$5 = -1;
var oldoffset_$6 = this.__LZdataoffset;
if (newnodes_$1) {
while (this.clones.length) this.poolClone();
var oldclones_$7 = null;
var ocl_$8 = 0
} else {
var oldclones_$7 = this.clones;
var ocl_$8 = oldclones_$7.length
};
this.clones = [];
var cpos_$9 = -this.spacing;
var inwindow_$a = false;
var newend_$b = -1;
var newstartpos_$c;
var notfirst_$d = true;
for (var i_$e = 0;i_$e < nodelen_$2;i_$e++) {
var cnode_$f = this.nodes[i_$e];
var ds_$g = cnode_$f.getUserData(this.datasizevar);
var csiz_$h = ds_$g == null ? this.viewsize : ds_$g;
cpos_$9 += this.spacing;
if (!inwindow_$a && newoffset_$5 == -1 && cpos_$9 - newstart_$3 + csiz_$h >= 0) {
notfirst_$d = i_$e != 0;
inwindow_$a = true;
newstartpos_$c = cpos_$9;
newoffset_$5 = i_$e;
var firstkept_$i = i_$e - oldoffset_$6;
firstkept_$i = firstkept_$i > ocl_$8 ? ocl_$8 : firstkept_$i;
if (firstkept_$i > 0) {
for (var j_$j = 0;j_$j < firstkept_$i;j_$j++) {
var v_$k = oldclones_$7[j_$j];
this.__LZreleaseClone(v_$k)
}}} else if (inwindow_$a && cpos_$9 - newstart_$3 > masksize_$4) {
inwindow_$a = false;
newend_$b = i_$e - newoffset_$5;
var lastkept_$l = i_$e - oldoffset_$6;
lastkept_$l = lastkept_$l < 0 ? 0 : lastkept_$l;
if (lastkept_$l < ocl_$8) {
for (var j_$j = lastkept_$l;j_$j < ocl_$8;j_$j++) {
var v_$k = oldclones_$7[j_$j];
this.__LZreleaseClone(v_$k)
}}};
if (inwindow_$a) {
if (i_$e >= oldoffset_$6 && i_$e < oldoffset_$6 + ocl_$8) {
var cl_$m = oldclones_$7[i_$e - oldoffset_$6]
} else {
var cl_$m = null
};
this.clones[i_$e - newoffset_$5] = cl_$m
};
cpos_$9 += csiz_$h
};
var clpos_$n = newstartpos_$c;
if (notfirst_$d) clpos_$n += this.spacing;
for (var i_$e = 0;i_$e < this.clones.length;i_$e++) {
var cnode_$f = this.nodes[i_$e + newoffset_$5];
var cl_$m = this.clones[i_$e];
if (!cl_$m) {
cl_$m = this.getNewClone();
cl_$m.clonenumber = i_$e + newoffset_$5;
cl_$m.datapath.setClonePointer(cnode_$f);
if (cl_$m.onclonenumber.ready) cl_$m.onclonenumber.sendEvent(i_$e + newoffset_$5);
this.clones[i_$e] = cl_$m
};
this.clones[i_$e] = cl_$m;
{
var $lzsc$cl8ywv = this.axis;
if (!cl_$m.__LZdeleted) {
var $lzsc$lpa5ea = "$lzc$set_" + $lzsc$cl8ywv;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$m[$lzsc$lpa5ea]) : cl_$m[$lzsc$lpa5ea] instanceof Function) {
cl_$m[$lzsc$lpa5ea](clpos_$n)
} else {
cl_$m[$lzsc$cl8ywv] = clpos_$n;
var $lzsc$m5zz8w = cl_$m["on" + $lzsc$cl8ywv];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$m5zz8w) : $lzsc$m5zz8w instanceof LzEvent) {
if ($lzsc$m5zz8w.ready) {
$lzsc$m5zz8w.sendEvent(clpos_$n)
}}}}};
var ds_$g = cnode_$f.getUserData(this.datasizevar);
var csiz_$h = ds_$g == null ? this.viewsize : ds_$g;
if (cl_$m[this.sizeAxis] != csiz_$h) {
{
var $lzsc$tvnh8q = this.sizeAxis;
if (!cl_$m.__LZdeleted) {
var $lzsc$yd93el = "$lzc$set_" + $lzsc$tvnh8q;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(cl_$m[$lzsc$yd93el]) : cl_$m[$lzsc$yd93el] instanceof Function) {
cl_$m[$lzsc$yd93el](csiz_$h)
} else {
cl_$m[$lzsc$tvnh8q] = csiz_$h;
var $lzsc$anijxg = cl_$m["on" + $lzsc$tvnh8q];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$anijxg) : $lzsc$anijxg instanceof LzEvent) {
if ($lzsc$anijxg.ready) {
$lzsc$anijxg.sendEvent(csiz_$h)
}}}}}};
clpos_$n += csiz_$h + this.spacing
};
this.__LZdataoffset = newoffset_$5;
{
var $lzsc$4c3tl7 = this.cloneimmediateparent;
var $lzsc$sj41i3 = this.sizeAxis;
if (!$lzsc$4c3tl7.__LZdeleted) {
var $lzsc$39utoh = "$lzc$set_" + $lzsc$sj41i3;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa($lzsc$4c3tl7[$lzsc$39utoh]) : $lzsc$4c3tl7[$lzsc$39utoh] instanceof Function) {
$lzsc$4c3tl7[$lzsc$39utoh](cpos_$9)
} else {
$lzsc$4c3tl7[$lzsc$sj41i3] = cpos_$9;
var $lzsc$9jahab = $lzsc$4c3tl7["on" + $lzsc$sj41i3];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$9jahab) : $lzsc$9jahab instanceof LzEvent) {
if ($lzsc$9jahab.ready) {
$lzsc$9jahab.sendEvent(cpos_$9)
}}}}};
this.__LZresizeupdating = false
};
$lzsc$temp["displayName"] = "__LZadjustVisibleClones";
return $lzsc$temp
})()], LzLazyReplicationManager);
lz.ResizeReplicationManager = LzResizeReplicationManager;
Class.make("LzColorUtils", null, null, ["__cache", {counter: 0}, "stringToColor", (function () {
var $lzsc$temp = function (s_$0) {
if (typeof s_$0 != "string") return s_$0;
if (s_$0 in lz.colors) return lz.colors[s_$0];
if (s_$0 in global) return global[s_$0];
if (s_$0.indexOf("rgb") != -1) return LzColorUtils.fromrgb(s_$0);
var n_$1 = Number(s_$0);
if (isNaN(n_$1)) {
return s_$0
} else {
return n_$1
}};
$lzsc$temp["displayName"] = "stringToColor";
return $lzsc$temp
})(), "fromrgb", (function () {
var $lzsc$temp = function (s_$0) {
if (typeof s_$0 != "string") return s_$0;
if (s_$0.indexOf("rgb") == -1) return LzColorUtils.stringToColor(s_$0);
var parts_$1 = s_$0.substring(s_$0.indexOf("(") + 1, s_$0.lastIndexOf(")")).split(",");
var color_$2 = LzColorUtils.rgbatoint(parts_$1[0], parts_$1[1], parts_$1[2], parts_$1[3]);
if (!isNaN(color_$2)) {
return color_$2
};
Debug.warn("invalid color string: " + s_$0);
return 0
};
$lzsc$temp["displayName"] = "fromrgb";
return $lzsc$temp
})(), "dectohex", (function () {
var $lzsc$temp = function (n_$0, padding_$1) {
switch (arguments.length) {
case 1:
padding_$1 = 0
};
if (typeof n_$0 != "number") return n_$0;
n_$0 = n_$0 & 16777215;
var hex_$2 = n_$0.toString(16);
var pad_$3 = padding_$1 - hex_$2.length;
while (pad_$3 > 0) {
hex_$2 = "0" + hex_$2;
pad_$3--
};
return hex_$2
};
$lzsc$temp["displayName"] = "dectohex";
return $lzsc$temp
})(), "torgb", (function () {
var $lzsc$temp = function (s_$0) {
if (typeof s_$0 == "string" && s_$0.indexOf("rgb") != -1) return s_$0;
var n_$1 = LzColorUtils.inttohex(s_$0);
if (typeof n_$1 != "string") return n_$1;
if (typeof s_$0 == "number" || lz.colors[s_$0] != null) s_$0 = n_$1;
var cache_$2 = LzColorUtils.__cache;
var key_$3 = "torgb" + n_$1;
if (cache_$2[key_$3]) return cache_$2[key_$3];
if (++cache_$2.counter > 1000) {
cache_$2 = {counter: 0}};
if (s_$0.length < 6) {
s_$0 = "#" + s_$0.charAt(1) + s_$0.charAt(1) + s_$0.charAt(2) + s_$0.charAt(2) + s_$0.charAt(3) + s_$0.charAt(3) + (s_$0.length > 4 ? s_$0.charAt(4) + s_$0.charAt(4) : "")
};
cache_$2[key_$3] = (s_$0.length > 7 ? "rgba(" : "rgb(") + parseInt(s_$0.substring(1, 3), 16) + "," + parseInt(s_$0.substring(3, 5), 16) + "," + parseInt(s_$0.substring(5, 7), 16) + (s_$0.length > 7 ? "," + parseInt(s_$0.substring(7), 16) / 255 : "") + ")";
return cache_$2[key_$3]
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})(), "tohsv", (function () {
var $lzsc$temp = function (rgb_$0) {
var cache_$1 = LzColorUtils.__cache;
var key_$2 = "tohsv" + rgb_$0;
if (cache_$1[key_$2]) return cache_$1[key_$2];
if (++cache_$1.counter > 1000) {
cache_$1 = {counter: 0}};
rgb_$0 = LzColorUtils.hextoint(rgb_$0);
var r_$3 = (rgb_$0 >> 16 & 255) / 255, g_$4 = (rgb_$0 >> 8 & 255) / 255, b_$5 = (rgb_$0 & 255) / 255;
var min_$6 = Math.min(r_$3, Math.min(g_$4, b_$5)), max_$7 = Math.max(r_$3, Math.max(g_$4, b_$5));
var v_$8 = max_$7;
var delta_$9 = max_$7 - min_$6;
if (delta_$9 == 0) {
cache_$1[key_$2] = {h: 0, s: 0, v: v_$8}} else {
var s_$a = delta_$9 / max_$7;
if (r_$3 == max_$7) {
var h_$b = (g_$4 - b_$5) / delta_$9
} else if (g_$4 == max_$7) {
var h_$b = 2 + (b_$5 - r_$3) / delta_$9
} else {
var h_$b = 4 + (r_$3 - g_$4) / delta_$9
};
h_$b *= 60;
if (h_$b < 0) {
h_$b += 360
};
cache_$1[key_$2] = {h: h_$b, s: s_$a, v: v_$8}};
return cache_$1[key_$2]
};
$lzsc$temp["displayName"] = "tohsv";
return $lzsc$temp
})(), "fromhsv", (function () {
var $lzsc$temp = function (h_$0, s_$1, v_$2) {
var cache_$3 = LzColorUtils.__cache;
var key_$4 = "fromhsv" + h_$0 + s_$1 + v_$2;
if (cache_$3[key_$4]) return cache_$3[key_$4];
if (++cache_$3.counter > 1000) {
cache_$3 = {counter: 0}};
var t_$5 = h_$0 / 60;
var ti_$6 = t_$5 | 0;
var hi_$7 = ti_$6 % 6;
var f_$8 = t_$5 - ti_$6;
var p_$9 = v_$2 * (1 - s_$1);
var q_$a = v_$2 * (1 - f_$8 * s_$1);
var t_$5 = v_$2 * (1 - (1 - f_$8) * s_$1);
var r_$b, g_$c, b_$d;
switch (hi_$7) {
case 0:
r_$b = v_$2;g_$c = t_$5;b_$d = p_$9;break;;case 1:
r_$b = q_$a;g_$c = v_$2;b_$d = p_$9;break;;case 2:
r_$b = p_$9;g_$c = v_$2;b_$d = t_$5;break;;case 3:
r_$b = p_$9;g_$c = q_$a;b_$d = v_$2;break;;case 4:
r_$b = t_$5;g_$c = p_$9;b_$d = v_$2;break;;case 5:
r_$b = v_$2;g_$c = p_$9;b_$d = q_$a;break
};
return cache_$3[key_$4] = LzColorUtils.rgbatoint(r_$b * 255, g_$c * 255, b_$d * 255)
};
$lzsc$temp["displayName"] = "fromhsv";
return $lzsc$temp
})(), "convertColor", (function () {
var $lzsc$temp = function (s_$0) {
if (s_$0 == "null" || s_$0 == null) return null;
return LzColorUtils.hextoint(s_$0)
};
$lzsc$temp["displayName"] = "convertColor";
return $lzsc$temp
})(), "hextoint", (function () {
var $lzsc$temp = function (s_$0) {
var n_$1 = LzColorUtils.stringToColor(s_$0);
if (typeof n_$1 != "string") return n_$1;
var cache_$2 = LzColorUtils.__cache;
var key_$3 = "hextoint" + s_$0;
if (cache_$2[key_$3]) return cache_$2[key_$3];
if (++cache_$2.counter > 1000) {
cache_$2 = {counter: 0}};
var hex_$4 = s_$0;
hex_$4 = hex_$4.slice(1);
var alpha_$5 = 0;
if (hex_$4.length > 6) {
alpha_$5 = parseInt(hex_$4.slice(6), 16) / 25500;
hex_$4 = hex_$4.slice(0, 6)
};
var n_$1 = parseInt(hex_$4, 16);
switch (hex_$4.length) {
case 3:
hex_$4 = ((n_$1 & 3840) << 8 | (n_$1 & 240) << 4 | n_$1 & 15) * 17 + alpha_$5;break;;case 6:
hex_$4 = n_$1 + alpha_$5;break;;default:
{
Debug.warn("invalid color string: " + s_$0)
}hex_$4 = 0;break
};
cache_$2[key_$3] = hex_$4;
return hex_$4
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), "inttohex", (function () {
var $lzsc$temp = function (n_$0, padding_$1) {
switch (arguments.length) {
case 1:
padding_$1 = 6
};
var s_$2 = LzColorUtils.stringToColor(n_$0);
if (typeof s_$2 != "number") return s_$2;
var cache_$3 = LzColorUtils.__cache;
var key_$4 = "inttohex" + s_$2;
if (cache_$3[key_$4]) return cache_$3[key_$4];
if (++cache_$3.counter > 1000) {
cache_$3 = {counter: 0}};
var hex_$5 = "#" + LzColorUtils.dectohex(s_$2, padding_$1);
var alpha_$6 = LzColorUtils.findalpha(n_$0);
if (alpha_$6 != null) {
var alphastring_$7 = alpha_$6.toString(16);
if (alphastring_$7.length == 1) {
alphastring_$7 = "0" + alphastring_$7
};
hex_$5 += alphastring_$7
};
cache_$3[key_$4] = hex_$5;
return hex_$5
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})(), "inttocolorobj", (function () {
var $lzsc$temp = function (val_$0) {
var rgba_$1 = LzColorUtils.hextoint(val_$0);
var cache_$2 = LzColorUtils.__cache;
var key_$3 = "inttocolorobj" + rgba_$1;
if (cache_$2[key_$3]) return cache_$2[key_$3];
if (++cache_$2.counter > 1000) {
cache_$2 = {counter: 0}};
var color_$4 = rgba_$1 | 0;
var alpha_$5 = LzColorUtils.findalpha(rgba_$1);
if (alpha_$5 != null) {
alpha_$5 *= 100 / 255
};
cache_$2[key_$3] = {color: color_$4, alpha: alpha_$5};
return cache_$2[key_$3]
};
$lzsc$temp["displayName"] = "inttocolorobj";
return $lzsc$temp
})(), "rgbatoint", (function () {
var $lzsc$temp = function (r_$0, g_$1, b_$2, a_$3) {
switch (arguments.length) {
case 3:
a_$3 = null
};
var color_$4 = (r_$0 & 255) << 16 | (g_$1 & 255) << 8 | b_$2 & 255;
if (a_$3 != null) {
color_$4 += a_$3 * 0.01
};
return color_$4
};
$lzsc$temp["displayName"] = "rgbatoint";
return $lzsc$temp
})(), "inttorgba", (function () {
var $lzsc$temp = function (rgb_$0) {
return [rgb_$0 >> 16 & 255, rgb_$0 >> 8 & 255, rgb_$0 & 255, LzColorUtils.findalpha(rgb_$0)]
};
$lzsc$temp["displayName"] = "inttorgba";
return $lzsc$temp
})(), "findalpha", (function () {
var $lzsc$temp = function (rgba_$0) {
if (rgba_$0 == null) return;
rgba_$0 = Number(rgba_$0);
var alpha_$1 = rgba_$0 - (rgba_$0 | 0);
if (alpha_$1 != 0 && !isNaN(alpha_$1)) {
return alpha_$1 * 25600 | 0
}};
$lzsc$temp["displayName"] = "findalpha";
return $lzsc$temp
})(), "applyTransform", (function () {
var $lzsc$temp = function (color_$0, trans_$1) {
var rgb_$2 = LzColorUtils.inttorgba(color_$0);
var red_$3 = rgb_$2[0];
var green_$4 = rgb_$2[1];
var blue_$5 = rgb_$2[2];
var alpha_$6 = rgb_$2[3];
red_$3 = red_$3 * trans_$1["redMultiplier"] + trans_$1["redOffset"];
red_$3 = Math.min(red_$3, 255);
green_$4 = green_$4 * trans_$1["greenMultiplier"] + trans_$1["greenOffset"];
green_$4 = Math.min(green_$4, 255);
blue_$5 = blue_$5 * trans_$1["blueMultiplier"] + trans_$1["blueOffset"];
blue_$5 = Math.min(blue_$5, 255);
if (alpha_$6 != null) {
alpha_$6 = alpha_$6 * trans_$1["alphaMultiplier"] + trans_$1["alphaOffset"];
alpha_$6 = Math.min(alpha_$6, 255)
};
return LzColorUtils.rgbatoint(red_$3, green_$4, blue_$5, alpha_$6)
};
$lzsc$temp["displayName"] = "applyTransform";
return $lzsc$temp
})()]);
lz.ColorUtils = LzColorUtils;
Class.make("LzUtilsClass", ["__SimpleExprPattern", void 0, "__ElementPattern", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
this.__SimpleExprPattern = new RegExp("^\\s*([$_A-Za-z][$\\w]*)((\\s*\\.\\s*[$_A-Za-z][$\\w]*)|(\\s*\\[\\s*\\d+\\s*\\]))*\\s*$");
this.__ElementPattern = new RegExp("([$_A-Za-z][$\\w]*)|(\\d+)", "g")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "color", {hextoint: (function () {
var $lzsc$temp = function (value_$0) {
Debug.warn("lz.Utils.color.hextoint() is deprecated.  Use LzColorUtils.hextoint() instead.");
return LzColorUtils.hextoint(value_$0)
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), inttohex: (function () {
var $lzsc$temp = function (c_$0) {
Debug.warn("lz.Utils.color.inttohex() is deprecated.  Use LzColorUtils.inttohex() instead.");
return LzColorUtils.inttohex(c_$0)
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})(), torgb: (function () {
var $lzsc$temp = function (s_$0) {
Debug.warn("lz.Utils.color.torgb() is deprecated.  Use LzColorUtils.torgb() instead.");
return LzColorUtils.torgb(s_$0)
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})()}, "hextoint", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, LzColorUtils.hextoint);
return LzColorUtils.hextoint(s_$0)
};
$lzsc$temp["displayName"] = "hextoint";
return $lzsc$temp
})(), "inttohex", (function () {
var $lzsc$temp = function (n_$0, padding_$1) {
switch (arguments.length) {
case 1:
padding_$1 = 6
};
Debug.deprecated(this, arguments.callee, LzColorUtils.inttohex);
return LzColorUtils.inttohex(n_$0, padding_$1)
};
$lzsc$temp["displayName"] = "inttohex";
return $lzsc$temp
})(), "dectohex", (function () {
var $lzsc$temp = function (n_$0, padding_$1) {
switch (arguments.length) {
case 1:
padding_$1 = 0
};
return LzColorUtils.dectohex(n_$0, padding_$1)
};
$lzsc$temp["displayName"] = "dectohex";
return $lzsc$temp
})(), "stringToColor", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, LzColorUtils.stringToColor);
return LzColorUtils.stringToColor(s_$0)
};
$lzsc$temp["displayName"] = "stringToColor";
return $lzsc$temp
})(), "torgb", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, LzColorUtils.torgb);
return LzColorUtils.torgb(s_$0)
};
$lzsc$temp["displayName"] = "torgb";
return $lzsc$temp
})(), "fromrgb", (function () {
var $lzsc$temp = function (s_$0) {
Debug.deprecated(this, arguments.callee, LzColorUtils.fromrgb);
return LzColorUtils.fromrgb(s_$0)
};
$lzsc$temp["displayName"] = "fromrgb";
return $lzsc$temp
})(), "colornames", lz.colors, "__parseArgs", (function () {
var $lzsc$temp = function (argstr_$0, scope_$1) {
switch (arguments.length) {
case 1:
scope_$1 = null
};
if (argstr_$0 == "") return [];
if (scope_$1 == null) scope_$1 = canvas;
var args_$2 = [];
var lastquote_$3 = null;
var currarg_$4 = "";
for (var i_$5 = 0, l_$6 = argstr_$0.length;i_$5 < l_$6;i_$5++) {
var lc_$7 = c_$8;
var c_$8 = argstr_$0.charAt(i_$5);
if (c_$8 == '"' || c_$8 == "'") {
if (lastquote_$3 == null) {
lastquote_$3 = c_$8
} else if (lastquote_$3 == c_$8) {
lastquote_$3 = null
}} else if (c_$8 == ",") {
if (!lastquote_$3) {
args_$2.push(currarg_$4);
currarg_$4 = "";
continue
}} else if (c_$8 == " " || c_$8 == "\t" || c_$8 == "\n" || c_$8 == "\r") {
if (lastquote_$3 == null && (lc_$7 == "," || lc_$7 == " " || lc_$7 == "\t" || lc_$7 == "\n" || lc_$7 == "\r")) {
continue
}};
currarg_$4 += c_$8
};
args_$2.push(currarg_$4);
for (var i_$5 = 0;i_$5 < args_$2.length;i_$5++) {
var a_$9 = args_$2[i_$5];
if (a_$9 == "") continue;
var firstchar_$a = a_$9.charAt(a_$9);
var n_$b = parseFloat(a_$9);
if (!isNaN(n_$b)) {
args_$2[i_$5] = n_$b
} else if (firstchar_$a == '"' || firstchar_$a == "'") {
var e_$c = a_$9.lastIndexOf(firstchar_$a);
args_$2[i_$5] = a_$9.substring(1, e_$c)
} else if (a_$9 == "true" || a_$9 == "false") {
args_$2[i_$5] = a_$9 == "true"
} else if (scope_$1[a_$9]) {
args_$2[i_$5] = scope_$1[a_$9]
} else {
args_$2[i_$5] = null
}};
return args_$2
};
$lzsc$temp["displayName"] = "__parseArgs";
return $lzsc$temp
})(), "safeEval", (function () {
var $lzsc$temp = function (js_$0) {
if (js_$0.indexOf("new ") == 0) return this.safeNew(js_$0);
var s_$1 = js_$0.indexOf("(");
var argstr_$2 = null;
if (s_$1 != -1) {
var e_$3 = js_$0.lastIndexOf(")");
argstr_$2 = js_$0.substring(s_$1 + 1, e_$3);
js_$0 = js_$0.substring(0, s_$1)
};
var scope_$4 = null, val_$5;
if (js_$0.match(this.__SimpleExprPattern)) {
var parts_$6 = js_$0.match(this.__ElementPattern);
val_$5 = globalValue(parts_$6[0]);
for (var i_$7 = 1, l_$8 = parts_$6.length;i_$7 < l_$8;i_$7++) {
scope_$4 = val_$5;
val_$5 = val_$5[parts_$6[i_$7]]
}};
if (argstr_$2 == null) {
return val_$5
};
var args_$9 = lz.Utils.__parseArgs(argstr_$2, scope_$4);
if (val_$5) {
var result_$a = val_$5.apply(scope_$4, args_$9);
return result_$a
}};
$lzsc$temp["displayName"] = "safeEval";
return $lzsc$temp
})(), "safeNew", (function () {
var $lzsc$temp = function (js_$0) {
var orig_$1 = js_$0;
var newpos_$2 = js_$0.indexOf("new ");
if (newpos_$2 == -1) return js_$0;
js_$0 = js_$0.substring(newpos_$2 + 4);
var s_$3 = js_$0.indexOf("(");
if (s_$3 != -1) {
var e_$4 = js_$0.indexOf(")");
var args_$5 = js_$0.substring(s_$3 + 1, e_$4);
js_$0 = js_$0.substring(0, s_$3)
};
var obj_$6 = globalValue(js_$0);
if (!obj_$6) return;
var args_$5 = lz.Utils.__parseArgs(args_$5);
var size_$7 = args_$5.length;
if (size_$7 == 0) {
return new obj_$6()
} else if (size_$7 == 1) {
return new obj_$6(args_$5[0])
} else if (size_$7 == 2) {
return new obj_$6(args_$5[0], args_$5[1])
} else if (size_$7 == 3) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2])
} else if (size_$7 == 4) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3])
} else if (size_$7 == 5) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4])
} else if (size_$7 == 6) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5])
} else if (size_$7 == 7) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5], args_$5[6])
} else if (size_$7 == 8) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5], args_$5[6], args_$5[7])
} else if (size_$7 == 9) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5], args_$5[6], args_$5[7], args_$5[8])
} else if (size_$7 == 10) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5], args_$5[6], args_$5[7], args_$5[8], args_$5[9])
} else if (size_$7 == 11) {
return new obj_$6(args_$5[0], args_$5[1], args_$5[2], args_$5[3], args_$5[4], args_$5[5], args_$5[6], args_$5[7], args_$5[8], args_$5[9], args_$5[10])
} else {
Debug.warn("Too many arguments", args_$5)
}};
$lzsc$temp["displayName"] = "safeNew";
return $lzsc$temp
})()]);
lz.Utils = new LzUtilsClass();
var LzUtils = lz.Utils;
Class.make("LzInstantiatorService", ["checkQDel", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.checkQDel = new LzDelegate(this, "checkQ")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "halted", false, "isimmediate", false, "isdatareplicating", false, "istrickling", false, "isUpdating", false, "safe", true, "timeout", 500, "makeQ", [], "trickleQ", [], "tricklingQ", [], "datareplQ", null, "dataQ", [], "syncNew", true, "trickletime", 10, "setSafeInstantiation", (function () {
var $lzsc$temp = function (isSafe_$0) {
this.safe = isSafe_$0;
if (this.instanceQ.length) {
this.timeout = Infinity
}};
$lzsc$temp["displayName"] = "setSafeInstantiation";
return $lzsc$temp
})(), "requestInstantiation", (function () {
var $lzsc$temp = function (v_$0, children_$1) {
if (this.isimmediate) {
this.createImmediate(v_$0, children_$1.concat())
} else {
var c_$2 = this.newReverseArray(children_$1);
if (this.isdatareplicating) {
this.datareplQ.push(c_$2, v_$0)
} else if (this.istrickling) {
this.tricklingQ.push(v_$0, c_$2)
} else {
this.makeQ.push(v_$0, c_$2);
this.checkUpdate()
}}};
$lzsc$temp["displayName"] = "requestInstantiation";
return $lzsc$temp
})(), "enableDataReplicationQueuing", (function () {
var $lzsc$temp = function (rman_$0) {
if (this.isdatareplicating) {
this.dataQ.push(this.datareplQ)
} else {
this.isdatareplicating = true
};
this.datareplQ = [];
this.datareplQ.push(rman_$0)
};
$lzsc$temp["displayName"] = "enableDataReplicationQueuing";
return $lzsc$temp
})(), "clearDataReplicationQueue", (function () {
var $lzsc$temp = function (rman_$0) {
var drq_$1 = this.datareplQ;
if (this.dataQ.length > 0) {
this.datareplQ = this.dataQ.pop()
} else {
this.isdatareplicating = false;
this.datareplQ = null
};
if (drq_$1.shift() !== rman_$0) {
Debug.error("%w.clearDataReplicationQueue: expected %w", this, rman_$0)
};
var cpar_$2 = rman_$0.cloneParent;
var mq_$3 = this.makeQ;
var mqlen_$4 = mq_$3.length;
var offset_$5 = mqlen_$4;
for (var i_$6 = 0;i_$6 < mqlen_$4;i_$6 += 2) {
if (mq_$3[i_$6].parent === cpar_$2) {
offset_$5 = i_$6;
break
}};
drq_$1.push(0, offset_$5);
drq_$1.reverse();
mq_$3.splice.apply(mq_$3, drq_$1);
this.checkUpdate()
};
$lzsc$temp["displayName"] = "clearDataReplicationQueue";
return $lzsc$temp
})(), "newReverseArray", (function () {
var $lzsc$temp = function (arr_$0) {
var n_$1 = arr_$0.length;
var a_$2 = Array(n_$1);
for (var i_$3 = 0, j_$4 = n_$1 - 1;i_$3 < n_$1;) {
a_$2[i_$3++] = arr_$0[j_$4--]
};
return a_$2
};
$lzsc$temp["displayName"] = "newReverseArray";
return $lzsc$temp
})(), "checkUpdate", (function () {
var $lzsc$temp = function () {
if (!(this.isUpdating || this.halted)) {
this.checkQDel.register(lz.Idle, "onidle");
this.isUpdating = true
}};
$lzsc$temp["displayName"] = "checkUpdate";
return $lzsc$temp
})(), "checkQ", (function () {
var $lzsc$temp = function (ignoreme_$0) {
switch (arguments.length) {
case 0:
ignoreme_$0 = null
};
if (!this.makeQ.length) {
if (!this.tricklingQ.length) {
if (!this.trickleQ.length) {
this.checkQDel.unregisterAll();
this.isUpdating = false;
return
} else {
var p_$1 = this.trickleQ.shift();
var c_$2 = this.trickleQ.shift();
this.tricklingQ.push(p_$1, this.newReverseArray(c_$2))
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
})(), "makeSomeViews", (function () {
var $lzsc$temp = function (cq_$0, otime_$1) {
var itime_$2 = new Date().getTime();
var num_$3 = 0;
while (new Date().getTime() - itime_$2 < otime_$1 && cq_$0.length) {
var len_$4 = cq_$0.length;
var larr_$5 = cq_$0[len_$4 - 1];
var par_$6 = cq_$0[len_$4 - 2];
var parDone_$7 = false;
if (par_$6["__LZdeleted"] || larr_$5[0] && larr_$5[0]["__LZdeleted"]) {
cq_$0.length -= 2;
continue
};
try {
while (new Date().getTime() - itime_$2 < otime_$1) {
if (len_$4 != cq_$0.length) {
break
};
if (!larr_$5.length) {
parDone_$7 = true;
break
};
var c_$8 = larr_$5.pop();
if (c_$8) {
par_$6.makeChild(c_$8, true);
num_$3++
}}}
finally {};
if (parDone_$7) {
cq_$0.length = len_$4 - 2;
par_$6.__LZinstantiationDone()
}};
return num_$3
};
$lzsc$temp["displayName"] = "makeSomeViews";
return $lzsc$temp
})(), "trickleInstantiate", (function () {
var $lzsc$temp = function (v_$0, children_$1) {
this.trickleQ.push(v_$0, children_$1);
this.checkUpdate()
};
$lzsc$temp["displayName"] = "trickleInstantiate";
return $lzsc$temp
})(), "createImmediate", (function () {
var $lzsc$temp = function (v_$0, children_$1) {
var c_$2 = this.newReverseArray(children_$1);
var wasimmediate_$3 = this.isimmediate;
this.isimmediate = true;
this.makeSomeViews([v_$0, c_$2], Infinity);
this.isimmediate = wasimmediate_$3
};
$lzsc$temp["displayName"] = "createImmediate";
return $lzsc$temp
})(), "completeTrickle", (function () {
var $lzsc$temp = function (v_$0) {
if (this.tricklingQ[0] == v_$0) {
var wasimmediate_$1 = this.isimmediate;
this.isimmediate = true;
this.makeSomeViews(this.tricklingQ, Infinity);
this.isimmediate = wasimmediate_$1;
this.tricklingQ = []
} else {
var tq_$2 = this.trickleQ;
var tql_$3 = tq_$2.length;
for (var i_$4 = 0;i_$4 < tql_$3;i_$4 += 2) {
if (tq_$2[i_$4] == v_$0) {
var dchil_$5 = tq_$2[i_$4 + 1];
tq_$2.splice(i_$4, 2);
this.createImmediate(v_$0, dchil_$5);
return
}}}};
$lzsc$temp["displayName"] = "completeTrickle";
return $lzsc$temp
})(), "traceQ", (function () {
var $lzsc$temp = function () {
Debug.info("****start trace");
var mq_$0 = this.makeQ;
for (var i_$1 = 0;i_$1 < mq_$0.length;i_$1 += 2) {
var par_$2 = mq_$0[i_$1];
var larr_$3 = mq_$0[i_$1 + 1];
var s_$4 = "";
for (var k_$5 = 0;k_$5 < larr_$3.length;k_$5++) {
s_$4 += larr_$3[k_$5]["class"].tagname + " |"
};
Debug.write("%w : |%s >>> %s", par_$2, s_$4, par_$2.getUID())
};
Debug.info("****trace done")
};
$lzsc$temp["displayName"] = "traceQ";
return $lzsc$temp
})(), "halt", (function () {
var $lzsc$temp = function () {
this.isUpdating = false;
this.halted = true;
this.checkQDel.unregisterAll()
};
$lzsc$temp["displayName"] = "halt";
return $lzsc$temp
})(), "resume", (function () {
var $lzsc$temp = function () {
this.halted = false;
this.checkUpdate()
};
$lzsc$temp["displayName"] = "resume";
return $lzsc$temp
})(), "drainQ", (function () {
var $lzsc$temp = function (limit_$0) {
var to_$1 = this.timeout;
var tt_$2 = this.trickletime;
var h_$3 = this.halted;
this.timeout = limit_$0;
this.trickletime = limit_$0;
this.halted = false;
this.isUpdating = true;
this.checkQ();
this.halted = h_$3;
this.timeout = to_$1;
this.trickletime = tt_$2;
return !this.isUpdating
};
$lzsc$temp["displayName"] = "drainQ";
return $lzsc$temp
})()], LzEventable, ["LzInstantiator", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzInstantiatorService.LzInstantiator = new LzInstantiatorService()
}}};
$lzsc$temp["displayName"] = "services/LzInstantiator.lzs#34/1";
return $lzsc$temp
})()(LzInstantiatorService);
lz.Instantiator = LzInstantiatorService.LzInstantiator;
Class.make("LzGlobalMouseService", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "onmousemove", LzDeclaredEvent, "onmouseup", LzDeclaredEvent, "onmouseupoutside", LzDeclaredEvent, "onmouseover", LzDeclaredEvent, "onmouseout", LzDeclaredEvent, "onmousedown", LzDeclaredEvent, "onmousedragin", LzDeclaredEvent, "onmousedragout", LzDeclaredEvent, "onmouseleave", LzDeclaredEvent, "onmouseenter", LzDeclaredEvent, "onmouseevent", LzDeclaredEvent, "onclick", LzDeclaredEvent, "ondblclick", LzDeclaredEvent, "__movecounter", 0, "__mouseactive", true, "__mouseEvent", (function () {
var $lzsc$temp = function (eventname_$0, view_$1) {
if (eventname_$0 == "onmouseleave") {
this.__mouseactive = false;
if (canvas.onmouseleave.ready) {
canvas.onmouseleave.sendEvent(null)
}} else if (eventname_$0 == "onmouseenter") {
this.__mouseactive = true;
if (canvas.onmouseenter.ready) {
canvas.onmouseenter.sendEvent(null)
}} else if (eventname_$0 == "onmousemove") {
this.__movecounter++
};
var ev_$2 = this[eventname_$0];
if (ev_$2) {
if (ev_$2.ready) {
ev_$2.sendEvent(view_$1)
};
if (this.onmouseevent.ready) {
this.onmouseevent.sendEvent({type: eventname_$0, target: view_$1})
}} else {
Debug.debug("Unknown mouse event %s", eventname_$0)
}};
$lzsc$temp["displayName"] = "__mouseEvent";
return $lzsc$temp
})(), "__mouseUpOutsideHandler", (function () {
var $lzsc$temp = function () {
LzMouseKernel.__mouseUpOutsideHandler()
};
$lzsc$temp["displayName"] = "__mouseUpOutsideHandler";
return $lzsc$temp
})()], LzEventable, ["LzGlobalMouse", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzGlobalMouseService.LzGlobalMouse = new LzGlobalMouseService()
}}};
$lzsc$temp["displayName"] = "services/LzGlobalMouse.lzs#81/1";
return $lzsc$temp
})()(LzGlobalMouseService);
lz.GlobalMouseService = LzGlobalMouseService;
lz.GlobalMouse = LzGlobalMouseService.LzGlobalMouse;
Class.make("LzBrowserService", ["capabilities", LzSprite.prototype.capabilities, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "loadURL", (function () {
var $lzsc$temp = function (url_$0, target_$1, features_$2) {
switch (arguments.length) {
case 1:
target_$1 = null;;case 2:
features_$2 = null
};
LzBrowserKernel.loadURL(url_$0, target_$1, features_$2)
};
$lzsc$temp["displayName"] = "loadURL";
return $lzsc$temp
})(), "loadJS", (function () {
var $lzsc$temp = function (js_$0, target_$1) {
switch (arguments.length) {
case 1:
target_$1 = null
};
LzBrowserKernel.loadJS.apply(null, arguments)
};
$lzsc$temp["displayName"] = "loadJS";
return $lzsc$temp
})(), "callJS", (function () {
var $lzsc$temp = function (methodname_$0, callback_$1, args_$2) {
switch (arguments.length) {
case 1:
callback_$1 = null;;case 2:
args_$2 = null
};
try {
return LzBrowserKernel.callJS.apply(null, arguments)
}
catch (e_$3) {
Debug.error("lz.Browser.callJS() failed:", e_$3);
return null
}};
$lzsc$temp["displayName"] = "callJS";
return $lzsc$temp
})(), "getVersion", (function () {
var $lzsc$temp = function () {
return LzBrowserKernel.getVersion()
};
$lzsc$temp["displayName"] = "getVersion";
return $lzsc$temp
})(), "getOS", (function () {
var $lzsc$temp = function () {
return LzBrowserKernel.getOS()
};
$lzsc$temp["displayName"] = "getOS";
return $lzsc$temp
})(), "getLoadURL", (function () {
var $lzsc$temp = function () {
return LzBrowserKernel.getLoadURL()
};
$lzsc$temp["displayName"] = "getLoadURL";
return $lzsc$temp
})(), "getURL", (function () {
var $lzsc$temp = function () {
return LzBrowserKernel.callJS("eval", false, "window.location.href")
};
$lzsc$temp["displayName"] = "getURL";
return $lzsc$temp
})(), "getInitArg", (function () {
var $lzsc$temp = function (name_$0) {
switch (arguments.length) {
case 0:
name_$0 = null
};
return LzBrowserKernel.getInitArg(name_$0)
};
$lzsc$temp["displayName"] = "getInitArg";
return $lzsc$temp
})(), "oldOptionName", (function () {
var $lzsc$temp = function (key_$0) {
return LzBrowserService.sOptionNameMap[key_$0]
};
$lzsc$temp["displayName"] = "oldOptionName";
return $lzsc$temp
})(), "getLzOption", (function () {
var $lzsc$temp = function (name_$0) {
switch (arguments.length) {
case 0:
name_$0 = null
};
var val_$1 = LzBrowserKernel.getLzOption(name_$0);
if (val_$1 == null) {
var lzname_$2 = LzBrowserService.sOptionNameMap[name_$0];
if (lzname_$2 != null) {
name_$0 = lzname_$2
};
return LzBrowserKernel.getInitArg(name_$0)
}};
$lzsc$temp["displayName"] = "getLzOption";
return $lzsc$temp
})(), "getAppID", (function () {
var $lzsc$temp = function () {
return LzBrowserKernel.getAppID()
};
$lzsc$temp["displayName"] = "getAppID";
return $lzsc$temp
})(), "showMenu", (function () {
var $lzsc$temp = function (truefalse_$0) {
if (this.capabilities.runtimemenus) {
LzBrowserKernel.showMenu(truefalse_$0)
} else {
LzView.__warnCapability("lz.Browser.showMenu()", "runtimemenus")
}};
$lzsc$temp["displayName"] = "showMenu";
return $lzsc$temp
})(), "setClipboard", (function () {
var $lzsc$temp = function (str_$0) {
if (this.capabilities.setclipboard) {
LzBrowserKernel.setClipboard(str_$0)
} else {
LzView.__warnCapability("lz.Browser.setClipboard()", "setclipboard")
}};
$lzsc$temp["displayName"] = "setClipboard";
return $lzsc$temp
})(), "setWindowTitle", (function () {
var $lzsc$temp = function (str_$0) {
LzBrowserKernel.callJS("eval", null, 'top.document.title="' + str_$0 + '"')
};
$lzsc$temp["displayName"] = "setWindowTitle";
return $lzsc$temp
})(), "isAAActive", (function () {
var $lzsc$temp = function () {
if (this.capabilities.accessibility) {
return LzBrowserKernel.isAAActive()
} else {
LzView.__warnCapability("lz.Browser.isAAActive()", "accessibility");
return false
}};
$lzsc$temp["displayName"] = "isAAActive";
return $lzsc$temp
})(), "updateAccessibility", (function () {
var $lzsc$temp = function () {
if (this.capabilities.accessibility) {
LzBrowserKernel.updateAccessibility()
} else {
LzView.__warnCapability("lz.Browser.updateAccessibility()", "accessibility")
}};
$lzsc$temp["displayName"] = "updateAccessibility";
return $lzsc$temp
})(), "loadProxyPolicy", (function () {
var $lzsc$temp = function (url_$0) {
if (this.capabilities.proxypolicy) {
LzBrowserKernel.loadProxyPolicy(url_$0)
} else {
LzView.__warnCapability("lz.Browser.loadProxyPolicy()", "proxypolicy")
}};
$lzsc$temp["displayName"] = "loadProxyPolicy";
return $lzsc$temp
})(), "postToLps", true, "parsedloadurl", null, "defaultPortNums", {http: 80, https: 443}, "getBaseURL", (function () {
var $lzsc$temp = function (secure_$0, port_$1) {
switch (arguments.length) {
case 0:
secure_$0 = null;;case 1:
port_$1 = null
};
var url_$2 = this.getLoadURLAsLzURL();
if (secure_$0) {
url_$2.protocol = "https"
};
if (port_$1) {
url_$2.port = port_$1
} else if (secure_$0 && port_$1 == null) {
url_$2.port = this.defaultPortNums[url_$2.protocol]
};
url_$2.query = null;
return url_$2
};
$lzsc$temp["displayName"] = "getBaseURL";
return $lzsc$temp
})(), "getLoadURLAsLzURL", (function () {
var $lzsc$temp = function () {
if (!this.parsedloadurl) {
this.parsedloadurl = new LzURL(this.getLoadURL())
};
return this.parsedloadurl.dupe()
};
$lzsc$temp["displayName"] = "getLoadURLAsLzURL";
return $lzsc$temp
})(), "toAbsoluteURL", (function () {
var $lzsc$temp = function (url_$0, secure_$1) {
if (url_$0.indexOf("://") > -1 || url_$0.indexOf("/@WEBAPP@/") == 0 || url_$0.indexOf("file:") == 0) {
return url_$0
};
var u_$2 = this.getLoadURLAsLzURL();
u_$2.query = null;
if (url_$0.indexOf(":") > -1) {
var colon_$3 = url_$0.indexOf(":");
var loadUrlIsSecure_$4 = u_$2.protocol == "https";
u_$2.protocol = url_$0.substring(0, colon_$3);
if (secure_$1 || loadUrlIsSecure_$4) {
if (u_$2.protocol == "http") {
u_$2.protocol = "https"
}};
var path_$5 = url_$0.substring(colon_$3 + 1, url_$0.length);
if (path_$5.charAt(0) == "/") {
u_$2.path = url_$0.substring(colon_$3 + 1);
u_$2.file = null
} else {
u_$2.file = url_$0.substring(colon_$3 + 1)
}} else {
if (url_$0.charAt(0) == "/") {
u_$2.path = url_$0;
u_$2.file = null
} else {
u_$2.file = url_$0
}};
return u_$2.toString()
};
$lzsc$temp["displayName"] = "toAbsoluteURL";
return $lzsc$temp
})(), "xmlEscape", (function () {
var $lzsc$temp = function (str_$0) {
return LzDataElement.__LZXMLescape(str_$0)
};
$lzsc$temp["displayName"] = "xmlEscape";
return $lzsc$temp
})(), "urlEscape", (function () {
var $lzsc$temp = function (str_$0) {
Debug.info("lz.Browser.urlEscape is deprecated, use encodeURIComponent instead");
return encodeURIComponent(str_$0)
};
$lzsc$temp["displayName"] = "urlEscape";
return $lzsc$temp
})(), "urlUnescape", (function () {
var $lzsc$temp = function (str_$0) {
Debug.info("lz.Browser.urlUnescape is deprecated, use decodeURIComponent instead");
return decodeURIComponent(str_$0)
};
$lzsc$temp["displayName"] = "urlUnescape";
return $lzsc$temp
})(), "usePost", (function () {
var $lzsc$temp = function () {
return this.postToLps && this.supportsPost()
};
$lzsc$temp["displayName"] = "usePost";
return $lzsc$temp
})(), "supportsPost", (function () {
var $lzsc$temp = function () {
return true
};
$lzsc$temp["displayName"] = "supportsPost";
return $lzsc$temp
})(), "makeProxiedURL", (function () {
var $lzsc$temp = function (params_$0) {
var headers_$1 = params_$0.headers;
var postbody_$2 = params_$0.postbody;
var proxyurl_$3 = params_$0.proxyurl;
var custom_args_$4 = params_$0.serverproxyargs;
var qargs_$5;
if (custom_args_$4) {
qargs_$5 = {url: this.toAbsoluteURL(params_$0.url, params_$0.secure), lzt: params_$0.service, reqtype: params_$0.httpmethod.toUpperCase()};
for (var opt_$6 in custom_args_$4) {
qargs_$5[opt_$6] = custom_args_$4[opt_$6]
}} else {
qargs_$5 = {url: this.toAbsoluteURL(params_$0.url, params_$0.secure), lzt: params_$0.service, reqtype: params_$0.httpmethod.toUpperCase(), sendheaders: params_$0.sendheaders, trimwhitespace: params_$0.trimwhitespace, nsprefix: params_$0.trimwhitespace, timeout: params_$0.timeout, cache: params_$0.cacheable, ccache: params_$0.ccache}};
if (postbody_$2 != null) {
qargs_$5.lzpostbody = postbody_$2
};
qargs_$5.lzr = $runtime;
if (headers_$1 != null) {
var headerString_$7 = "";
for (var hname_$8 in headers_$1) {
headerString_$7 += hname_$8 + ": " + headers_$1[hname_$8] + "\n"
};
if (headerString_$7 != "") {
qargs_$5["headers"] = headerString_$7
}};
if (!params_$0.ccache) {
qargs_$5.__lzbc__ = new Date().getTime()
};
var sep_$9 = "?";
for (var key_$a in qargs_$5) {
var val_$b = qargs_$5[key_$a];
if (typeof val_$b == "string") {
val_$b = encodeURIComponent(val_$b);
val_$b = val_$b.replace(LzDataset.slashPat, "%2F")
};
proxyurl_$3 += sep_$9 + key_$a + "=" + val_$b;
sep_$9 = "&"
};
return proxyurl_$3
};
$lzsc$temp["displayName"] = "makeProxiedURL";
return $lzsc$temp
})()], null, ["LzBrowser", void 0, "sOptionNameMap", {runtime: "lzr", backtrace: "lzbacktrace", proxied: "lzproxied", usemastersprite: "lzusemastersprite"}]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzBrowserService.LzBrowser = new LzBrowserService()
}}};
$lzsc$temp["displayName"] = "services/LzBrowser.lzs#33/1";
return $lzsc$temp
})()(LzBrowserService);
lz.BrowserService = LzBrowserService;
lz.Browser = LzBrowserService.LzBrowser;
Class.make("LzModeManagerService", ["onmode", LzDeclaredEvent, "__LZlastclick", null, "__LZlastClickTime", 0, "willCall", false, "eventsLocked", false, "modeArray", new Array(), "remotedebug", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
LzMouseKernel.setCallback(this, "rawMouseEvent")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "makeModal", (function () {
var $lzsc$temp = function (view_$0) {
if (view_$0 && (this.modeArray.length == 0 || !this.hasMode(view_$0))) {
this.modeArray.push(view_$0);
if (this.onmode.ready) this.onmode.sendEvent(view_$0);
var f_$1 = lz.Focus.getFocus();
if (f_$1 && !f_$1.childOf(view_$0)) {
lz.Focus.clearFocus()
}}};
$lzsc$temp["displayName"] = "makeModal";
return $lzsc$temp
})(), "release", (function () {
var $lzsc$temp = function (view_$0) {
var marr_$1 = this.modeArray;
for (var i_$2 = marr_$1.length - 1;i_$2 >= 0;i_$2--) {
if (marr_$1[i_$2] === view_$0) {
marr_$1.splice(i_$2, marr_$1.length - i_$2);
var newmode_$3 = marr_$1[i_$2 - 1];
if (this.onmode.ready) this.onmode.sendEvent(newmode_$3 || null);
var f_$4 = lz.Focus.getFocus();
if (newmode_$3 && f_$4 && !f_$4.childOf(newmode_$3)) {
lz.Focus.clearFocus()
};
return
}}};
$lzsc$temp["displayName"] = "release";
return $lzsc$temp
})(), "releaseAll", (function () {
var $lzsc$temp = function () {
this.modeArray = new Array();
if (this.onmode.ready) this.onmode.sendEvent(null)
};
$lzsc$temp["displayName"] = "releaseAll";
return $lzsc$temp
})(), "handleMouseEvent", (function () {
var $lzsc$temp = function (view_$0, eventStr_$1) {
if (eventStr_$1 == "onmouseup") lz.Track.__LZmouseup(null);
if (view_$0 == null) {
view_$0 = this.__findInputtextSelection()
};
lz.GlobalMouse.__mouseEvent(eventStr_$1, view_$0);
if (this.eventsLocked) {
return
};
var dosend_$2 = true;
var isDebugView_$3 = Debug.canvasConsoleWindow && view_$0 && view_$0.childOf(Debug.canvasConsoleWindow);
dosend_$2 = !isDebugView_$3;
for (var i_$4 = this.modeArray.length - 1;dosend_$2 && i_$4 >= 0;--i_$4) {
var mView_$5 = this.modeArray[i_$4];
if (!mView_$5) {
continue
};
if (view_$0 && view_$0.childOf(mView_$5)) {
break
} else {
dosend_$2 = mView_$5.passModeEvent ? mView_$5.passModeEvent(eventStr_$1, view_$0) : false
}};
if (!view_$0) {
return
};
dosend_$2 = dosend_$2 || isDebugView_$3;
if (dosend_$2) {
if (eventStr_$1 == "onclick") {
if (this.__LZlastclick === view_$0 && view_$0.ondblclick.ready && LzTimeKernel.getTimer() - this.__LZlastClickTime < view_$0.DOUBLE_CLICK_TIME) {
eventStr_$1 = "ondblclick";
lz.GlobalMouse.__mouseEvent(eventStr_$1, view_$0);
this.__LZlastclick = null
} else {
this.__LZlastclick = view_$0;
this.__LZlastClickTime = LzTimeKernel.getTimer()
}};
view_$0.mouseevent(eventStr_$1);
if (eventStr_$1 == "onmousedown") {
lz.Focus.__LZcheckFocusChange(view_$0)
}}};
$lzsc$temp["displayName"] = "handleMouseEvent";
return $lzsc$temp
})(), "__LZallowInput", (function () {
var $lzsc$temp = function (modalview_$0, input_$1) {
if (Debug.canvasConsoleWindow && input_$1.childOf(Debug.canvasConsoleWindow)) {
return true
};
return input_$1.childOf(modalview_$0)
};
$lzsc$temp["displayName"] = "__LZallowInput";
return $lzsc$temp
})(), "__LZallowFocus", (function () {
var $lzsc$temp = function (view_$0) {
var len_$1 = this.modeArray.length;
return len_$1 == 0 || view_$0.childOf(this.modeArray[len_$1 - 1])
};
$lzsc$temp["displayName"] = "__LZallowFocus";
return $lzsc$temp
})(), "globalLockMouseEvents", (function () {
var $lzsc$temp = function () {
this.eventsLocked = true
};
$lzsc$temp["displayName"] = "globalLockMouseEvents";
return $lzsc$temp
})(), "globalUnlockMouseEvents", (function () {
var $lzsc$temp = function () {
this.eventsLocked = false
};
$lzsc$temp["displayName"] = "globalUnlockMouseEvents";
return $lzsc$temp
})(), "hasMode", (function () {
var $lzsc$temp = function (view_$0) {
var marr_$1 = this.modeArray;
for (var i_$2 = marr_$1.length - 1;i_$2 >= 0;i_$2--) {
if (view_$0 === marr_$1[i_$2]) {
return true
}};
return false
};
$lzsc$temp["displayName"] = "hasMode";
return $lzsc$temp
})(), "getModalView", (function () {
var $lzsc$temp = function () {
return this.modeArray[this.modeArray.length - 1] || null
};
$lzsc$temp["displayName"] = "getModalView";
return $lzsc$temp
})(), "__findInputtextSelection", (function () {
var $lzsc$temp = function () {
return LzInputTextSprite.findSelection()
};
$lzsc$temp["displayName"] = "__findInputtextSelection";
return $lzsc$temp
})(), "rawMouseEvent", (function () {
var $lzsc$temp = function (eventname_$0, view_$1) {
if (eventname_$0 == "onmousemove") {
lz.GlobalMouse.__mouseEvent("onmousemove", null)
} else {
this.handleMouseEvent(view_$1, eventname_$0)
}};
$lzsc$temp["displayName"] = "rawMouseEvent";
return $lzsc$temp
})()], LzEventable, ["LzModeManager", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzModeManagerService.LzModeManager = new LzModeManagerService()
}}};
$lzsc$temp["displayName"] = "services/LzModeManager.lzs#54/1";
return $lzsc$temp
})()(LzModeManagerService);
lz.ModeManagerService = LzModeManagerService;
lz.ModeManager = LzModeManagerService.LzModeManager;
Class.make("LzCursorService", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "showHandCursor", (function () {
var $lzsc$temp = function (show_$0) {
LzMouseKernel.showHandCursor(show_$0)
};
$lzsc$temp["displayName"] = "showHandCursor";
return $lzsc$temp
})(), "setCursorGlobal", (function () {
var $lzsc$temp = function (resource_$0) {
LzMouseKernel.setCursorGlobal(resource_$0)
};
$lzsc$temp["displayName"] = "setCursorGlobal";
return $lzsc$temp
})(), "lock", (function () {
var $lzsc$temp = function () {
LzMouseKernel.lock()
};
$lzsc$temp["displayName"] = "lock";
return $lzsc$temp
})(), "unlock", (function () {
var $lzsc$temp = function () {
LzMouseKernel.unlock()
};
$lzsc$temp["displayName"] = "unlock";
return $lzsc$temp
})(), "restoreCursor", (function () {
var $lzsc$temp = function () {
LzMouseKernel.restoreCursor()
};
$lzsc$temp["displayName"] = "restoreCursor";
return $lzsc$temp
})()], LzEventable, ["LzCursor", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzCursorService.LzCursor = new LzCursorService()
}}};
$lzsc$temp["displayName"] = "services/LzCursor.lzs#46/1";
return $lzsc$temp
})()(LzCursorService);
lz.CursorService = LzCursorService;
lz.Cursor = LzCursorService.LzCursor;
Class.make("LzKeysService", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
LzKeyboardKernel.setCallback(this, "__keyEvent");
if (lz.embed["mousewheel"]) {
lz.embed.mousewheel.setCallback(this, "__mousewheelEvent")
}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "downKeysHash", {}, "downKeysArray", [], "keycombos", {}, "onkeydown", LzDeclaredEvent, "onkeyup", LzDeclaredEvent, "onmousewheeldelta", LzDeclaredEvent, "onkeyevent", LzDeclaredEvent, "codemap", {shift: 16, control: 17, alt: 18}, "ctrlKey", false, "__keyEvent", (function () {
var $lzsc$temp = function (delta_$0, k_$1, type_$2, ctrlKey_$3) {
switch (arguments.length) {
case 3:
ctrlKey_$3 = false
};
this.ctrlKey = ctrlKey_$3;
var cm_$4 = this.codemap;
for (var key_$5 in delta_$0) {
var down_$6 = delta_$0[key_$5];
if (cm_$4[key_$5] != null) k_$1 = cm_$4[key_$5];
if (down_$6) {
this.gotKeyDown(k_$1)
} else {
this.gotKeyUp(k_$1)
}}};
$lzsc$temp["displayName"] = "__keyEvent";
return $lzsc$temp
})(), "__allKeysUp", (function () {
var $lzsc$temp = function () {
this.downKeysHash = {};
this.downKeysArray = [];
LzKeyboardKernel.__allKeysUp()
};
$lzsc$temp["displayName"] = "__allKeysUp";
return $lzsc$temp
})(), "__browserTabEvent", (function () {
var $lzsc$temp = function (shiftdown_$0) {
LzKeyboardKernel.__browserTabEvent(shiftdown_$0)
};
$lzsc$temp["displayName"] = "__browserTabEvent";
return $lzsc$temp
})(), "gotKeyDown", (function () {
var $lzsc$temp = function (kC_$0, info_$1) {
switch (arguments.length) {
case 1:
info_$1 = null
};
var dkhash_$2 = this.downKeysHash;
var dkeys_$3 = this.downKeysArray;
var firstkeydown_$4 = !dkhash_$2[kC_$0];
if (firstkeydown_$4) {
dkhash_$2[kC_$0] = true;
dkeys_$3.push(kC_$0);
dkeys_$3.sort()
};
if (firstkeydown_$4 || info_$1 != "extra") {
if (dkhash_$2[229] != true) {
if (this.onkeydown.ready) this.onkeydown.sendEvent(kC_$0);
if (this.onkeyevent.ready) {
this.onkeyevent.sendEvent({type: "onkeydown", key: kC_$0})
}}};
if (firstkeydown_$4) {
var cp_$5 = this.keycombos;
for (var i_$6 = 0;i_$6 < dkeys_$3.length && cp_$5 != null;i_$6++) {
cp_$5 = cp_$5[dkeys_$3[i_$6]]
};
if (cp_$5 != null && ("delegates" in cp_$5)) {
var del_$7 = cp_$5.delegates;
for (var i_$6 = 0;i_$6 < del_$7.length;i_$6++) {
del_$7[i_$6].execute(dkeys_$3)
}}}};
$lzsc$temp["displayName"] = "gotKeyDown";
return $lzsc$temp
})(), "gotKeyUp", (function () {
var $lzsc$temp = function (kC_$0) {
var dkhash_$1 = this.downKeysHash;
var isDown_$2 = dkhash_$1[kC_$0];
delete dkhash_$1[kC_$0];
var dkeys_$3 = this.downKeysArray;
dkeys_$3.length = 0;
for (var k_$4 in dkhash_$1) {
dkeys_$3.push(k_$4)
};
if (isDown_$2) {
if (this.onkeyup.ready) this.onkeyup.sendEvent(kC_$0);
if (this.onkeyevent.ready) {
this.onkeyevent.sendEvent({type: "onkeyup", key: kC_$0})
}}};
$lzsc$temp["displayName"] = "gotKeyUp";
return $lzsc$temp
})(), "isKeyDown", (function () {
var $lzsc$temp = function (k_$0) {
if (typeof k_$0 == "string") {
return this.downKeysHash[this.keyCodes[k_$0.toLowerCase()]] == true
} else {
var down_$1 = true;
var dkhash_$2 = this.downKeysHash;
var kc_$3 = this.keyCodes;
for (var i_$4 = 0;i_$4 < k_$0.length;i_$4++) {
down_$1 = down_$1 && dkhash_$2[kc_$3[k_$0[i_$4].toLowerCase()]] == true
};
return down_$1
}};
$lzsc$temp["displayName"] = "isKeyDown";
return $lzsc$temp
})(), "callOnKeyCombo", (function () {
var $lzsc$temp = function (d_$0, kCArr_$1) {
var kc_$2 = this.keyCodes;
var kcSorted_$3 = [];
for (var i_$4 = 0;i_$4 < kCArr_$1.length;i_$4++) {
kcSorted_$3.push(kc_$2[kCArr_$1[i_$4].toLowerCase()])
};
kcSorted_$3.sort();
var cp_$5 = this.keycombos;
for (var i_$4 = 0;i_$4 < kcSorted_$3.length;i_$4++) {
var cpnext_$6 = cp_$5[kcSorted_$3[i_$4]];
if (cpnext_$6 == null) {
cp_$5[kcSorted_$3[i_$4]] = cpnext_$6 = {delegates: []}};
cp_$5 = cpnext_$6
};
cp_$5.delegates.push(d_$0)
};
$lzsc$temp["displayName"] = "callOnKeyCombo";
return $lzsc$temp
})(), "removeKeyComboCall", (function () {
var $lzsc$temp = function (d_$0, kCArr_$1) {
var kc_$2 = this.keyCodes;
var kcSorted_$3 = [];
for (var i_$4 = 0;i_$4 < kCArr_$1.length;i_$4++) {
kcSorted_$3.push(kc_$2[kCArr_$1[i_$4].toLowerCase()])
};
kcSorted_$3.sort();
var cp_$5 = this.keycombos;
for (var i_$4 = 0;i_$4 < kcSorted_$3.length;i_$4++) {
cp_$5 = cp_$5[kcSorted_$3[i_$4]];
if (cp_$5 == null) {
return false
}};
for (var i_$4 = cp_$5.delegates.length - 1;i_$4 >= 0;i_$4--) {
if (cp_$5.delegates[i_$4] == d_$0) {
cp_$5.delegates.splice(i_$4, 1)
}}};
$lzsc$temp["displayName"] = "removeKeyComboCall";
return $lzsc$temp
})(), "enableEnter", (function () {
var $lzsc$temp = function (onroff_$0) {
Debug.write("lz.Keys.enableEnter not yet defined")
};
$lzsc$temp["displayName"] = "enableEnter";
return $lzsc$temp
})(), "mousewheeldelta", 0, "__mousewheelEvent", (function () {
var $lzsc$temp = function (d_$0) {
if (lz.GlobalMouse.__mouseactive) {
this.mousewheeldelta = d_$0;
if (this.onmousewheeldelta.ready) this.onmousewheeldelta.sendEvent(d_$0);
if (this.onkeyevent.ready) {
this.onkeyevent.sendEvent({type: "onmousewheeldelta", key: d_$0})
}}};
$lzsc$temp["displayName"] = "__mousewheelEvent";
return $lzsc$temp
})(), "gotLastFocus", (function () {
var $lzsc$temp = function (ignore_$0) {
LzKeyboardKernel.gotLastFocus()
};
$lzsc$temp["displayName"] = "gotLastFocus";
return $lzsc$temp
})(), "setGlobalFocusTrap", (function () {
var $lzsc$temp = function (istrapped_$0) {
if (canvas.capabilities.globalfocustrap) {
LzKeyboardKernel.setGlobalFocusTrap(istrapped_$0)
} else {
LzView.__warnCapability("lz.Keys.setGlobalFocusTrap(" + istrapped_$0 + ")", "globalfocustrap")
}};
$lzsc$temp["displayName"] = "setGlobalFocusTrap";
return $lzsc$temp
})(), "keyCodes", {"0": 48, ")": 48, ";": 186, ":": 186, "1": 49, "!": 49, "=": 187, "+": 187, "2": 50, "@": 50, "<": 188, ",": 188, "3": 51, "#": 51, "-": 189, "_": 189, "4": 52, "$": 52, ">": 190, ".": 190, "5": 53, "%": 53, "/": 191, "?": 191, "6": 54, "^": 54, "`": 192, "~": 192, "7": 55, "&": 55, "[": 219, "{": 219, "8": 56, "*": 56, "\\": 220, "|": 220, "9": 57, "(": 57, "]": 221, "}": 221, '"': 222, "'": 222, a: 65, b: 66, c: 67, d: 68, e: 69, f: 70, g: 71, h: 72, i: 73, j: 74, k: 75, l: 76, m: 77, n: 78, o: 79, p: 80, q: 81, r: 82, s: 83, t: 84, u: 85, v: 86, w: 87, x: 88, y: 89, z: 90, numbpad0: 96, numbpad1: 97, numbpad2: 98, numbpad3: 99, numbpad4: 100, numbpad5: 101, numbpad6: 102, numbpad7: 103, numbpad8: 104, numbpad9: 105, multiply: 106, "add": 107, subtract: 109, decimal: 110, divide: 111, f1: 112, f2: 113, f3: 114, f4: 115, f5: 116, f6: 117, f7: 118, f8: 119, f9: 120, f10: 121, f11: 122, f12: 123, backspace: 8, tab: 9, clear: 12, enter: 13, shift: 16, control: 17, alt: 18, "pause": 19, "break": 19, capslock: 20, esc: 27, spacebar: 32, pageup: 33, pagedown: 34, end: 35, home: 36, leftarrow: 37, uparrow: 38, rightarrow: 39, downarrow: 40, insert: 45, "delete": 46, help: 47, numlock: 144, screenlock: 145, "IME": 229}], LzEventable, ["LzKeys", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzKeysService.LzKeys = new LzKeysService()
}}};
$lzsc$temp["displayName"] = "services/LzKeys.lzs#43/1";
return $lzsc$temp
})()(LzKeysService);
lz.KeysService = LzKeysService;
lz.Keys = LzKeysService.LzKeys;
Class.make("LzAudioService", ["capabilities", LzSprite.prototype.capabilities, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "playSound", (function () {
var $lzsc$temp = function (snd_$0) {
if (this.capabilities.audio) {
LzAudioKernel.playSound(snd_$0)
} else {
LzView.__warnCapability("lz.Audio.playSound()", "audio")
}};
$lzsc$temp["displayName"] = "playSound";
return $lzsc$temp
})(), "stopSound", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
LzAudioKernel.stopSound()
} else {
LzView.__warnCapability("lz.Audio.stopSound()", "audio")
}};
$lzsc$temp["displayName"] = "stopSound";
return $lzsc$temp
})(), "startSound", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
LzAudioKernel.startSound()
} else {
LzView.__warnCapability("lz.Audio.startSound()", "audio")
}};
$lzsc$temp["displayName"] = "startSound";
return $lzsc$temp
})(), "getVolume", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
return LzAudioKernel.getVolume()
} else {
LzView.__warnCapability("lz.Audio.getVolume()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getVolume";
return $lzsc$temp
})(), "setVolume", (function () {
var $lzsc$temp = function (v_$0) {
if (this.capabilities.audio) {
LzAudioKernel.setVolume(v_$0)
} else {
LzView.__warnCapability("lz.Audio.setVolume()", "audio")
}};
$lzsc$temp["displayName"] = "setVolume";
return $lzsc$temp
})(), "getPan", (function () {
var $lzsc$temp = function () {
if (this.capabilities.audio) {
return LzAudioKernel.getPan()
} else {
LzView.__warnCapability("lz.Audio.getPan()", "audio")
};
return NaN
};
$lzsc$temp["displayName"] = "getPan";
return $lzsc$temp
})(), "setPan", (function () {
var $lzsc$temp = function (p_$0) {
if (this.capabilities.audio) {
LzAudioKernel.setPan(p_$0)
} else {
LzView.__warnCapability("lz.Audio.setPan()", "audio")
}};
$lzsc$temp["displayName"] = "setPan";
return $lzsc$temp
})()], null, ["LzAudio", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzAudioService.LzAudio = new LzAudioService()
}}};
$lzsc$temp["displayName"] = "services/LzAudio.lzs#27/1";
return $lzsc$temp
})()(LzAudioService);
lz.AudioService = LzAudioService;
lz.Audio = LzAudioService.LzAudio;
Class.make("LzHistoryService", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "isReady", false, "ready", false, "onready", LzDeclaredEvent, "persist", false, "_persistso", null, "offset", 0, "__lzdirty", false, "__lzhistq", [], "__lzcurrstate", {}, "capabilities", LzSprite.prototype.capabilities, "onoffset", LzDeclaredEvent, "receiveHistory", (function () {
var $lzsc$temp = function (o_$0) {
if (this.persist && !this._persistso) {
this.__initPersist()
};
var len_$1 = this.__lzhistq.length;
var offset_$2 = o_$0 * 1;
if (!offset_$2) {
offset_$2 = 0
} else if (offset_$2 > len_$1 - 1) {
offset_$2 = len_$1
};
var h_$3 = this.__lzhistq[offset_$2];
for (var u_$4 in h_$3) {
var obj_$5 = h_$3[u_$4];
{
var $lzsc$cjvzeb = global[obj_$5.c];
var $lzsc$ui31rf = obj_$5.n;
var $lzsc$6u9njx = obj_$5.v;
if (!$lzsc$cjvzeb.__LZdeleted) {
var $lzsc$vjput0 = "$lzc$set_" + $lzsc$ui31rf;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa($lzsc$cjvzeb[$lzsc$vjput0]) : $lzsc$cjvzeb[$lzsc$vjput0] instanceof Function) {
$lzsc$cjvzeb[$lzsc$vjput0]($lzsc$6u9njx)
} else {
$lzsc$cjvzeb[$lzsc$ui31rf] = $lzsc$6u9njx;
var $lzsc$k1o5wc = $lzsc$cjvzeb["on" + $lzsc$ui31rf];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$k1o5wc) : $lzsc$k1o5wc instanceof LzEvent) {
if ($lzsc$k1o5wc.ready) {
$lzsc$k1o5wc.sendEvent($lzsc$6u9njx)
}}}}}};
this.offset = offset_$2;
if (this.onoffset.ready) this.onoffset.sendEvent(offset_$2);
return offset_$2
};
$lzsc$temp["displayName"] = "receiveHistory";
return $lzsc$temp
})(), "receiveEvent", (function () {
var $lzsc$temp = function (n_$0, v_$1) {
{
if (!canvas.__LZdeleted) {
var $lzsc$m0auwi = "$lzc$set_" + n_$0;
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(canvas[$lzsc$m0auwi]) : canvas[$lzsc$m0auwi] instanceof Function) {
canvas[$lzsc$m0auwi](v_$1)
} else {
canvas[n_$0] = v_$1;
var $lzsc$xcl27w = canvas["on" + n_$0];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$xcl27w) : $lzsc$xcl27w instanceof LzEvent) {
if ($lzsc$xcl27w.ready) {
$lzsc$xcl27w.sendEvent(v_$1)
}}}}}};
$lzsc$temp["displayName"] = "receiveEvent";
return $lzsc$temp
})(), "getCanvasAttribute", (function () {
var $lzsc$temp = function (n_$0) {
return canvas[n_$0]
};
$lzsc$temp["displayName"] = "getCanvasAttribute";
return $lzsc$temp
})(), "setCanvasAttribute", (function () {
var $lzsc$temp = function (n_$0, v_$1) {
this.receiveEvent(n_$0, v_$1)
};
$lzsc$temp["displayName"] = "setCanvasAttribute";
return $lzsc$temp
})(), "callMethod", (function () {
var $lzsc$temp = function (js_$0) {
return LzBrowserKernel.callMethod(js_$0)
};
$lzsc$temp["displayName"] = "callMethod";
return $lzsc$temp
})(), "save", (function () {
var $lzsc$temp = function (who_$0, prop_$1, val_$2) {
if (typeof who_$0 != "string") {
if (who_$0["id"]) who_$0 = who_$0["id"];
Debug.warn("Warning: this.save() requires a view ID to be passed in as a string for the first argument.");
if (!who_$0) return
};
if (val_$2 == null) val_$2 = global[who_$0][prop_$1];
this.__lzcurrstate[who_$0] = {c: who_$0, n: prop_$1, v: val_$2};
this.__lzdirty = true
};
$lzsc$temp["displayName"] = "save";
return $lzsc$temp
})(), "commit", (function () {
var $lzsc$temp = function () {
if (!this.__lzdirty) return;
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
})(), "move", (function () {
var $lzsc$temp = function (by_$0) {
switch (arguments.length) {
case 0:
by_$0 = 1
};
this.commit();
var o_$1 = this.offset + by_$0;
if (0 >= o_$1) o_$1 = 0;
if (this.__lzhistq.length >= o_$1) {
LzBrowserKernel.setHistory(o_$1)
}};
$lzsc$temp["displayName"] = "move";
return $lzsc$temp
})(), "next", (function () {
var $lzsc$temp = function () {
this.move(1)
};
$lzsc$temp["displayName"] = "next";
return $lzsc$temp
})(), "prev", (function () {
var $lzsc$temp = function () {
this.move(-1)
};
$lzsc$temp["displayName"] = "prev";
return $lzsc$temp
})(), "__initPersist", (function () {
var $lzsc$temp = function () {
if (this.persist) {
if (!this._persistso) {
this._persistso = LzBrowserKernel.getPersistedObject("historystate")
};
if (this._persistso && this._persistso.data) {
var d_$0 = this._persistso.data;
this.__lzhistq = [];
for (var i_$1 in d_$0) {
this.__lzhistq[i_$1] = d_$0[i_$1]
}}} else {
if (this._persistso) this._persistso = null
}};
$lzsc$temp["displayName"] = "__initPersist";
return $lzsc$temp
})(), "clear", (function () {
var $lzsc$temp = function () {
if (this.persist) {
if (!this._persistso) {
this._persistso = LzBrowserKernel.getPersistedObject("historystate")
};
this._persistso.clear()
};
this.__lzhistq = [];
this.offset = 0;
if (this.onoffset.ready) this.onoffset.sendEvent(0)
};
$lzsc$temp["displayName"] = "clear";
return $lzsc$temp
})(), "setPersist", (function () {
var $lzsc$temp = function (p_$0) {
if (this.capabilities.persistence) {
this.persist = p_$0
} else {
Debug.warn("The %s runtime does not support %s", canvas["runtime"], "lz.History.setPersist()")
}};
$lzsc$temp["displayName"] = "setPersist";
return $lzsc$temp
})(), "__start", (function () {
var $lzsc$temp = function (id_$0) {
lz.Browser.callJS("lz.embed.history.listen('" + id_$0 + "')");
this.isReady = true;
this.ready = true;
if (this.onready.ready) this.onready.sendEvent(true)
};
$lzsc$temp["displayName"] = "__start";
return $lzsc$temp
})()], LzEventable, ["LzHistory", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzHistoryService.LzHistory = new LzHistoryService()
}}};
$lzsc$temp["displayName"] = "services/LzHistory.lzs#29/1";
return $lzsc$temp
})()(LzHistoryService);
lz.HistoryService = LzHistoryService;
lz.History = LzHistoryService.LzHistory;
Class.make("LzTrackService", ["__LZreg", new Object(), "__LZactivegroups", null, "__LZtrackDel", null, "__LZmouseupDel", null, "__LZdestroydel", null, "__LZlastmouseup", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.__LZtrackDel = new LzDelegate(this, "__LZtrack");
this.__LZmouseupDel = new LzDelegate(this, "__LZmouseup", lz.GlobalMouse, "onmouseup");
this.__LZdestroydel = new LzDelegate(this, "__LZdestroyitem");
this.__LZactivegroups = []
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "register", (function () {
var $lzsc$temp = function (v_$0, group_$1) {
if (v_$0 == null || group_$1 == null) return;
var reglist_$2 = this.__LZreg[group_$1];
if (!reglist_$2) {
this.__LZreg[group_$1] = reglist_$2 = [];
reglist_$2.__LZlasthit = null;
reglist_$2.__LZactive = false
};
reglist_$2.push(v_$0);
this.__LZdestroydel.register(v_$0, "ondestroy")
};
$lzsc$temp["displayName"] = "register";
return $lzsc$temp
})(), "unregister", (function () {
var $lzsc$temp = function (v_$0, group_$1) {
if (v_$0 == null || group_$1 == null) return;
var reglist_$2 = this.__LZreg[group_$1];
if (reglist_$2) {
for (var i_$3 = 0;i_$3 < reglist_$2.length;i_$3++) {
if (reglist_$2[i_$3] == v_$0) {
if (reglist_$2.__LZlasthit == v_$0) {
if (this.__LZlastmouseup == v_$0) {
this.__LZlastmouseup = null
};
reglist_$2.__LZlasthit = null
};
reglist_$2.splice(i_$3, 1)
}};
if (reglist_$2.length == 0) {
if (reglist_$2.__LZactive) {
this.deactivate(group_$1)
};
delete this.__LZreg[group_$1]
}};
this.__LZdestroydel.unregisterFrom(v_$0.ondestroy)
};
$lzsc$temp["displayName"] = "unregister";
return $lzsc$temp
})(), "__LZdestroyitem", (function () {
var $lzsc$temp = function (v_$0) {
for (var group_$1 in this.__LZreg) {
this.unregister(v_$0, group_$1)
}};
$lzsc$temp["displayName"] = "__LZdestroyitem";
return $lzsc$temp
})(), "activate", (function () {
var $lzsc$temp = function (group_$0) {
var reglist_$1 = this.__LZreg[group_$0];
if (reglist_$1 && !reglist_$1.__LZactive) {
reglist_$1.__LZactive = true;
var agroups_$2 = this.__LZactivegroups;
if (agroups_$2.length == 0) {
this.__LZtrackDel.register(lz.Idle, "onidle")
};
agroups_$2.push(reglist_$1)
}};
$lzsc$temp["displayName"] = "activate";
return $lzsc$temp
})(), "deactivate", (function () {
var $lzsc$temp = function (group_$0) {
var reglist_$1 = this.__LZreg[group_$0];
if (reglist_$1 && reglist_$1.__LZactive) {
var agroups_$2 = this.__LZactivegroups;
for (var i_$3 = 0;i_$3 < agroups_$2.length;++i_$3) {
if (agroups_$2[i_$3] == reglist_$1) {
agroups_$2.splice(i_$3, 1);
break
}};
if (agroups_$2.length == 0) {
this.__LZtrackDel.unregisterAll()
};
reglist_$1.__LZactive = false;
if (this.__LZlastmouseup == reglist_$1.__LZlasthit) {
this.__LZlastmouseup = null
};
reglist_$1.__LZlasthit = null
}};
$lzsc$temp["displayName"] = "deactivate";
return $lzsc$temp
})(), "__LZtopview", (function () {
var $lzsc$temp = function (a_$0, b_$1) {
var atemp_$2 = a_$0;
var btemp_$3 = b_$1;
while (atemp_$2.nodeLevel < btemp_$3.nodeLevel) {
btemp_$3 = btemp_$3.immediateparent;
if (btemp_$3 == a_$0) return b_$1
};
while (btemp_$3.nodeLevel < atemp_$2.nodeLevel) {
atemp_$2 = atemp_$2.immediateparent;
if (atemp_$2 == b_$1) return a_$0
};
while (atemp_$2.immediateparent != btemp_$3.immediateparent) {
atemp_$2 = atemp_$2.immediateparent;
btemp_$3 = btemp_$3.immediateparent
};
return atemp_$2.getZ() > btemp_$3.getZ() ? a_$0 : b_$1
};
$lzsc$temp["displayName"] = "__LZtopview";
return $lzsc$temp
})(), "__LZfindTopmost", (function () {
var $lzsc$temp = function (vlist_$0) {
var top_$1 = vlist_$0[0];
for (var i_$2 = 1;i_$2 < vlist_$0.length;i_$2++) {
top_$1 = this.__LZtopview(top_$1, vlist_$0[i_$2])
};
return top_$1
};
$lzsc$temp["displayName"] = "__LZfindTopmost";
return $lzsc$temp
})(), "__LZtrackgroup", (function () {
var $lzsc$temp = function (group_$0, hitlist_$1) {
for (var i_$2 = 0;i_$2 < group_$0.length;i_$2++) {
var v_$3 = group_$0[i_$2];
if (v_$3.visible) {
var vpos_$4 = v_$3.getMouse(null);
if (v_$3.containsPt(vpos_$4.x, vpos_$4.y)) {
hitlist_$1.push(v_$3)
}}}};
$lzsc$temp["displayName"] = "__LZtrackgroup";
return $lzsc$temp
})(), "__LZtrack", (function () {
var $lzsc$temp = function (ignore_$0) {
var foundviews_$1 = [];
var agroups_$2 = this.__LZactivegroups;
for (var i_$3 = 0;i_$3 < agroups_$2.length;++i_$3) {
var thisgroup_$4 = agroups_$2[i_$3];
var hitlist_$5 = [];
this.__LZtrackgroup(thisgroup_$4, hitlist_$5);
var lhit_$6 = thisgroup_$4.__LZlasthit;
if (hitlist_$5.length) {
var fd_$7 = this.__LZfindTopmost(hitlist_$5);
if (fd_$7 == lhit_$6) continue;
foundviews_$1.push(fd_$7)
} else {
var fd_$7 = null
};
if (lhit_$6) {
var onmtrackout_$8 = lhit_$6.onmousetrackout;
if (onmtrackout_$8.ready) onmtrackout_$8.sendEvent(lhit_$6)
};
thisgroup_$4.__LZlasthit = fd_$7
};
for (var i_$3 = 0, len_$9 = foundviews_$1.length;i_$3 < len_$9;++i_$3) {
var v_$a = foundviews_$1[i_$3];
if (v_$a.onmousetrackover.ready) v_$a.onmousetrackover.sendEvent(v_$a)
}};
$lzsc$temp["displayName"] = "__LZtrack";
return $lzsc$temp
})(), "__LZmouseup", (function () {
var $lzsc$temp = function (ignore_$0) {
var agroups_$1 = this.__LZactivegroups.slice();
for (var i_$2 = 0;i_$2 < agroups_$1.length;++i_$2) {
var lhit_$3 = agroups_$1[i_$2].__LZlasthit;
if (lhit_$3) {
var onmtrackup_$4 = lhit_$3.onmousetrackup;
if (onmtrackup_$4.ready) {
if (this.__LZlastmouseup == lhit_$3) {
this.__LZlastmouseup = null
} else {
this.__LZlastmouseup = lhit_$3;
onmtrackup_$4.sendEvent(lhit_$3)
}}}}};
$lzsc$temp["displayName"] = "__LZmouseup";
return $lzsc$temp
})()], LzEventable, ["LzTrack", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzTrackService.LzTrack = new LzTrackService()
}}};
$lzsc$temp["displayName"] = "services/LzTrack.lzs#106/1";
return $lzsc$temp
})()(LzTrackService);
lz.TrackService = LzTrackService;
lz.Track = LzTrackService.LzTrack;
Class.make("LzIdleEvent", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (eventSender_$0, eventName_$1, d_$2) {
switch (arguments.length) {
case 2:
d_$2 = null
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, eventSender_$0, eventName_$1, d_$2)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "notify", (function () {
var $lzsc$temp = function (ready_$0) {
if (ready_$0) {
LzIdleKernel.addCallback(this, "sendEvent")
} else if (!ready_$0) {
LzIdleKernel.removeCallback(this, "sendEvent")
}};
$lzsc$temp["displayName"] = "notify";
return $lzsc$temp
})()], LzNotifyingEvent);
Class.make("LzIdleService", ["coi", void 0, "regNext", false, "removeCOI", null, "onidle", new LzDeclaredEventClass(LzIdleEvent), "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.coi = new Array();
this.removeCOI = new LzDelegate(this, "removeCallIdleDelegates")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "callOnIdle", (function () {
var $lzsc$temp = function (d_$0) {
this.coi.push(d_$0);
if (!this.regNext) {
this.regNext = true;
this.removeCOI.register(this, "onidle")
}};
$lzsc$temp["displayName"] = "callOnIdle";
return $lzsc$temp
})(), "removeCallIdleDelegates", (function () {
var $lzsc$temp = function (t_$0) {
var arr_$1 = this.coi.slice(0);
this.coi.length = 0;
for (var i_$2 = 0;i_$2 < arr_$1.length;i_$2++) {
arr_$1[i_$2].execute(t_$0)
};
if (this.coi.length == 0) {
this.removeCOI.unregisterFrom(this.onidle);
this.regNext = false
}};
$lzsc$temp["displayName"] = "removeCallIdleDelegates";
return $lzsc$temp
})(), "setFrameRate", (function () {
var $lzsc$temp = function (fps_$0) {
switch (arguments.length) {
case 0:
fps_$0 = 30
};
LzIdleKernel.setFrameRate(fps_$0)
};
$lzsc$temp["displayName"] = "setFrameRate";
return $lzsc$temp
})()], LzEventable, ["LzIdle", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzIdleService.LzIdle = new LzIdleService()
}}};
$lzsc$temp["displayName"] = "services/LzIdle.lzs#59/1";
return $lzsc$temp
})()(LzIdleService);
lz.IdleService = LzIdleService;
lz.Idle = LzIdleService.LzIdle;
Class.make("LzCSSStyleRule", ["parsed", null, "specificity", 0, "properties", void 0, "_dbg_filename", null, "_dbg_lineno", 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (selector_$0, properties_$1, source_$2, line_$3) {
this.parsed = selector_$0;
if (selector_$0["length"]) {
var s_$4 = 0;
for (var i_$5 = 0, l_$6 = selector_$0.length;i_$5 < l_$6;i_$5++) {
s_$4 += selector_$0[i_$5].s
};
this.specificity = s_$4
} else {
this.specificity = selector_$0.s
};
this.properties = properties_$1;
this[Debug.FUNCTION_FILENAME] = source_$2;
this[Debug.FUNCTION_LINENO] = line_$3
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "_lexorder", void 0, "_dbg_name", (function () {
var $lzsc$temp = function () {
var pname;
pname = (function () {
var $lzsc$temp = function (rp_$0) {
var rptn_$1 = rp_$0["t"];
var rpi_$2 = rp_$0["i"];
var rpa_$3 = rp_$0["a"];
if (!(rptn_$1 || rpi_$2 || rpa_$3)) {
return "*"
};
var rpv_$4 = rp_$0["v"];
var rpm_$5 = rp_$0["m"] || "=";
return (rptn_$1 ? rptn_$1 : "") + (rpi_$2 ? "#" + rpi_$2 : "") + (rpa_$3 ? (rpa_$3 == "styleclass" ? "." + rpv_$4 : "[" + rpa_$3 + (rpv_$4 ? rpm_$5 + rpv_$4 : "") + "]") : "") + (("&" in rp_$0) ? pname(rp_$0["&"]) : "")
};
$lzsc$temp["displayName"] = "pname";
return $lzsc$temp
})();
var rp_$0 = this.parsed;
if (rp_$0["length"]) {
var n_$1 = "";
for (var i_$2 = 0;i_$2 < rp_$0.length;i_$2++) {
n_$1 += pname(rp_$0[i_$2]) + " "
};
n_$1 = n_$1.substring(0, n_$1.length - 1)
} else {
var n_$1 = pname(rp_$0)
};
return n_$1
};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#72/19";
return $lzsc$temp
})(), "equal", (function () {
var $lzsc$temp = function (that_$0) {
var equal;
equal = (function () {
var $lzsc$temp = function (pa_$0, pb_$1) {
return pa_$0["t"] == pb_$1["t"] && pa_$0["i"] == pb_$1["i"] && pa_$0["a"] == pb_$1["a"] && pa_$0["v"] == pb_$1["v"] && pa_$0["m"] == pb_$1["m"] && (!pa_$0["&"] && !pb_$1["&"] || pa_$0["&"] && pb_$1["&"] && equal(pa_$0["&"], pb_$1["&"]))
};
$lzsc$temp["displayName"] = "equal";
return $lzsc$temp
})();
var rap_$1 = this.parsed;
var rbp_$2 = that_$0.parsed;
if (rap_$1["length"] != rbp_$2["length"]) {
return false
};
if (rap_$1["length"]) {
for (var i_$3 = rap_$1.length - 1;i_$3 >= 0;i_$3--) {
if (!equal(rap_$1[i_$3], rbp_$2[i_$3])) {
return false
}}};
if (!equal(rap_$1, rbp_$2)) {
return false
};
var aprops_$4 = this.properties;
var bprops_$5 = that_$0.properties;
for (var ak_$6 in aprops_$4) {
if (aprops_$4[ak_$6] !== bprops_$5[ak_$6]) {
return false
}};
for (var bk_$7 in bprops_$5) {
if (aprops_$4[bk_$7] !== bprops_$5[bk_$7]) {
return false
}};
return true
};
$lzsc$temp["displayName"] = "equal";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#18/1";
return $lzsc$temp
})()(LzCSSStyleRule);
lz.CSSStyleRule = LzCSSStyleRule;
Class.make("LzCSSStyleDependencies", ["count", 0, "deps", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
this.deps = {}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "gather", (function () {
var $lzsc$temp = function (node_$0, attr_$1) {
if (!(attr_$1 in this.deps)) {
this.deps[attr_$1] = [node_$0];
this.count++;
return
};
var nodes_$2 = this.deps[attr_$1];
for (var i_$3 = 0, l_$4 = nodes_$2.length;i_$3 < l_$4;i_$3++) {
if (node_$0 === nodes_$2[i_$3]) {
return
}};
this.deps[attr_$1].push(node_$0);
this.count++
};
$lzsc$temp["displayName"] = "gather";
return $lzsc$temp
})()]);
Class.make("LzCSSStyleClass", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {
this._rules = [];
this._attrRules = {};
this._idRules = {};
this._tagRules = {}};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "getComputedStyle", (function () {
var $lzsc$temp = function (node_$0) {
var csssd_$1 = new LzCSSStyleDeclaration();
csssd_$1.setNode(node_$0);
return csssd_$1
};
$lzsc$temp["displayName"] = "getComputedStyle";
return $lzsc$temp
})(), "getPropertyValueFor", (function () {
var $lzsc$temp = function (node_$0, pname_$1) {
var pc_$2 = node_$0["__LZPropertyCache"] || this.getPropertyCache(node_$0);
if (pname_$1 in pc_$2) {
return pc_$2[pname_$1]
};
Debug.warn("No CSS value found for node %#w for property name %s", node_$0, pname_$1);
return pc_$2[pname_$1] = void 0
};
$lzsc$temp["displayName"] = "getPropertyValueFor";
return $lzsc$temp
})(), "getPropertyCache", (function () {
var $lzsc$temp = function (node_$0) {
if (!node_$0) return {};
var pc_$1 = node_$0["__LZPropertyCache"];
if (pc_$1) {
return pc_$1
};
var ip_$2 = node_$0.immediateparent;
if (!ip_$2 || ip_$2 === canvas) {
var ipc_$3 = {}} else {
var ipc_$3 = ip_$2["__LZPropertyCache"] || this.getPropertyCache(ip_$2)
};
var rules_$4 = node_$0["__LZRuleCache"] || this.getRulesCache(node_$0);
if (rules_$4.length == 0) {
return node_$0.__LZPropertyCache = ipc_$3
};
var pc_$1 = {};
for (var k_$5 in ipc_$3) {
pc_$1[k_$5] = ipc_$3[k_$5]
};
for (var i_$6 = rules_$4.length - 1;i_$6 >= 0;i_$6--) {
var props_$7 = rules_$4[i_$6].properties;
for (var pname_$8 in props_$7) {
pc_$1[pname_$8] = props_$7[pname_$8]
}};
return node_$0.__LZPropertyCache = pc_$1
};
$lzsc$temp["displayName"] = "getPropertyCache";
return $lzsc$temp
})(), "getRulesCache", (function () {
var $lzsc$temp = function (node_$0) {
var rules_$1 = node_$0["__LZRuleCache"];
if (rules_$1) {
return rules_$1
};
rules_$1 = new Array();
var tryRules_$2 = new Array();
if (this._rulenum != this._lastSort) {
this._sortRules()
};
var id_$3 = node_$0["id"];
if (id_$3) {
var ir_$4 = this._idRules;
if (id_$3 in ir_$4) {
tryRules_$2 = tryRules_$2.concat(ir_$4[id_$3])
}};
var ar_$5 = this._attrRules;
for (var k_$6 in ar_$5) {
if (node_$0[k_$6] !== void 0) {
tryRules_$2 = tryRules_$2.concat(ar_$5[k_$6])
}};
var cr_$7 = this._tagRules;
for (var tn_$8 in cr_$7) {
var c_$9 = lz[tn_$8];
if (c_$9 && (c_$9["$lzsc$isa"] ? c_$9.$lzsc$isa(node_$0) : node_$0 instanceof c_$9)) {
tryRules_$2 = tryRules_$2.concat(cr_$7[tn_$8])
}};
tryRules_$2.concat(this._rules);
var deps_$a = new LzCSSStyleDependencies();
var sortNeeded_$b = false;
var lastSpecificity_$c = Infinity;
for (var i_$d = 0, l_$e = tryRules_$2.length;i_$d < l_$e;i_$d++) {
var r_$f = tryRules_$2[i_$d];
if (!sortNeeded_$b) {
var rs_$g = r_$f.specificity;
if (!rs_$g || rs_$g >= lastSpecificity_$c) {
sortNeeded_$b = true
} else {
lastSpecificity_$c = rs_$g
}};
var rp_$h = r_$f.parsed;
var compound_$i = !(!rp_$h["length"]);
if (compound_$i) {
rp_$h = rp_$h[rp_$h.length - 1]
};
var rptn_$j = rp_$h["t"];
var rpc_$k = rptn_$j ? lz[rptn_$j] : null;
var rpi_$l = rp_$h["i"];
var applies_$m = false;
if ((!rptn_$j || rpc_$k && (rpc_$k["$lzsc$isa"] ? rpc_$k.$lzsc$isa(node_$0) : node_$0 instanceof rpc_$k)) && (!rpi_$l || node_$0["id"] == rpi_$l)) {
if (!compound_$i) {
if (!rp_$h["a"] && !rp_$h["&"]) {
applies_$m = true
} else {
var tp_$n = rp_$h["a"] ? rp_$h : rp_$h["&"];
var tpa_$o = tp_$n.a;
do {
applies_$m = false;
if (node_$0[tpa_$o] !== void 0) {
if (tpa_$o != "name") {
deps_$a.gather(node_$0, tpa_$o)
};
if (!("v" in tp_$n)) {
applies_$m = true
} else {
var nav_$p = node_$0[tpa_$o];
nav_$p += "";
var tpv_$q = tp_$n.v;
if (!tp_$n["m"]) {
applies_$m = nav_$p == tpv_$q
} else {
var tpm_$r = tp_$n.m;
if (tpm_$r == "~=") {
applies_$m = nav_$p == tpv_$q || node_$0.__LZCSSStyleclass && node_$0.__LZCSSStyleclass.indexOf(" " + tpv_$q + " ") >= 0
} else if (tpm_$r == "|=") {
applies_$m = nav_$p.indexOf(tpv_$q + "-") == 0
} else {
Debug.error("Unknown attribute match %#s", tp_$n.m)
}}}};
if (applies_$m && tp_$n["&"]) {
tp_$n = tp_$n["&"];
tpa_$o = tp_$n.a
} else {
tp_$n = null
}} while (tp_$n)
}} else {
applies_$m = this._compoundSelectorApplies(r_$f.parsed, node_$0, deps_$a)
}};
if (applies_$m) {
rules_$1.push(r_$f)
}};
if (sortNeeded_$b) {
rules_$1.sort(this.__compareSpecificity)
};
node_$0.__LZRuleCache = rules_$1;
if (deps_$a.count > 0) {
node_$0.__LZCSSDependencies = deps_$a.deps
};
return rules_$1
};
$lzsc$temp["displayName"] = "getRulesCache";
return $lzsc$temp
})(), "__compareSpecificity", (function () {
var $lzsc$temp = function (rA_$0, rB_$1) {
var specificityA_$2 = rA_$0.specificity;
var specificityB_$3 = rB_$1.specificity;
if (specificityA_$2 != specificityB_$3) {
return specificityA_$2 < specificityB_$3 ? 1 : -1
};
var rap_$4 = rA_$0.parsed;
var rbp_$5 = rB_$1.parsed;
var lexorder_$6 = rA_$0._lexorder < rB_$1._lexorder ? 1 : -1;
if (!rap_$4["length"] && !rbp_$5["length"]) {
var ratn_$7 = rap_$4["t"];
var rbtn_$8 = rbp_$5["t"];
if (!ratn_$7 || !rbtn_$8 || ratn_$7 == rbtn_$8) {
return lexorder_$6
};
var rac_$9 = lz[ratn_$7];
var rbc_$a = lz[rbtn_$8];
if (rac_$9 && rbc_$a) {
if ($lzsc$issubclassof(rac_$9, rbc_$a)) {
return -1
};
if ($lzsc$issubclassof(rbc_$a, rac_$9)) {
return 1
}};
return lexorder_$6
};
for (var i_$b = 0;i_$b < rap_$4.length;i_$b++) {
var rapi_$c = rap_$4[i_$b];
var rbpi_$d = rbp_$5[i_$b];
if (!rapi_$c || !rbpi_$d) {
break
};
var ratn_$7 = rapi_$c["t"];
var rbtn_$8 = rbpi_$d["t"];
if (ratn_$7 && rbtn_$8 && ratn_$7 != rbtn_$8) {
var rac_$9 = lz[ratn_$7];
var rbc_$a = lz[rbtn_$8];
if (rac_$9 && rbc_$a) {
if ($lzsc$issubclassof(rac_$9, rbc_$a)) {
return -1
};
if ($lzsc$issubclassof(rbc_$a, rac_$9)) {
return 1
}}}};
return lexorder_$6
};
$lzsc$temp["displayName"] = "__compareSpecificity";
return $lzsc$temp
})(), "_printRuleArray", (function () {
var $lzsc$temp = function (arr_$0) {
for (var i_$1 = 0;i_$1 < arr_$0.length;i_$1++) {
Debug.write(i_$1, arr_$0[i_$1])
}};
$lzsc$temp["displayName"] = "_printRuleArray";
return $lzsc$temp
})(), "_compoundSelectorApplies", (function () {
var $lzsc$temp = function (parsedsel_$0, startnode_$1, deps_$2) {
for (var node_$3 = startnode_$1, i_$4 = parsedsel_$0.length - 1;i_$4 >= 0 && node_$3 !== canvas;i_$4--, node_$3 = node_$3.parent) {
var rp_$5 = parsedsel_$0[i_$4];
var rptn_$6 = rp_$5["t"];
var rpc_$7 = rptn_$6 ? lz[rptn_$6] : null;
var rpi_$8 = rp_$5["i"];
while (node_$3 !== canvas) {
var applies_$9 = false;
if ((!rptn_$6 || rpc_$7 && (rpc_$7["$lzsc$isa"] ? rpc_$7.$lzsc$isa(node_$3) : node_$3 instanceof rpc_$7)) && (!rpi_$8 || node_$3["id"] == rpi_$8)) {
if (!rp_$5["a"] && !rp_$5["&"]) {
applies_$9 = true
} else {
var tp_$a = rp_$5["a"] ? rp_$5 : rp_$5["&"];
var tpa_$b = tp_$a.a;
do {
applies_$9 = false;
if (node_$3[tpa_$b] !== void 0) {
if (tpa_$b != "name") {
deps_$2.gather(node_$3, tpa_$b)
};
if (!("v" in tp_$a)) {
applies_$9 = true
} else {
var nav_$c = node_$3[tpa_$b];
nav_$c += "";
var tpv_$d = tp_$a.v;
if (!tp_$a["m"]) {
applies_$9 = nav_$c == tpv_$d
} else {
var tpm_$e = tp_$a.m;
if (tpm_$e == "~=") {
applies_$9 = nav_$c == tpv_$d || node_$3.__LZCSSStyleclass && node_$3.__LZCSSStyleclass.indexOf(" " + tpv_$d + " ") >= 0
} else if (tpm_$e == "|=") {
applies_$9 = nav_$c.indexOf(tpv_$d + "-") == 0
} else {
Debug.error("Unknown attribute match %#s", tp_$a.m)
}}}};
if (applies_$9 && tp_$a["&"]) {
tp_$a = tp_$a["&"];
tpa_$b = tp_$a.a
} else {
tp_$a = null
}} while (tp_$a)
};
if (applies_$9) {
if (i_$4 == 0) {
return true
} else {
break
}}};
if (node_$3 === startnode_$1) {
return false
};
node_$3 = node_$3.parent
}};
return false
};
$lzsc$temp["displayName"] = "_compoundSelectorApplies";
return $lzsc$temp
})(), "_rules", void 0, "_attrRules", void 0, "_idRules", void 0, "_tagRules", void 0, "_rulenum", 0, "_lastSort", -1, "_sortRules", (function () {
var $lzsc$temp = function () {
var deleteDuplicates_$0;
deleteDuplicates_$0 = (function () {
var $lzsc$temp = function (sortedRules_$0) {
for (var i_$1 = sortedRules_$0.length - 2;i_$1 >= 0;i_$1--) {
if (sortedRules_$0[i_$1].equal(sortedRules_$0[i_$1 + 1])) {
sortedRules_$0.splice(i_$1 + 1, 1)
}}};
$lzsc$temp["displayName"] = "deleteDuplicates";
return $lzsc$temp
})();
if (this._rulenum != this._lastSort) {
this._rules.sort(this.__compareSpecificity);
deleteDuplicates_$0(this._rules);
for (var k_$1 in this._attrRules) {
var ark_$2 = this._attrRules[k_$1];
ark_$2.sort(this.__compareSpecificity);
deleteDuplicates_$0(ark_$2)
};
for (var k_$1 in this._idRules) {
var irk_$3 = this._idRules[k_$1];
irk_$3.sort(this.__compareSpecificity);
deleteDuplicates_$0(irk_$3)
};
for (var k_$1 in this._tagRules) {
var trk_$4 = this._tagRules[k_$1];
trk_$4.sort(this.__compareSpecificity);
deleteDuplicates_$0(trk_$4)
};
this._lastSort = this._rulenum
}};
$lzsc$temp["displayName"] = "_sortRules";
return $lzsc$temp
})(), "_addRule", (function () {
var $lzsc$temp = function (r_$0) {
r_$0._lexorder = this._rulenum++;
var lastsel_$1 = r_$0.parsed;
if (lastsel_$1["length"]) {
lastsel_$1 = lastsel_$1[lastsel_$1.length - 1]
};
if ("i" in lastsel_$1) {
var id_$2 = lastsel_$1.i;
var itab_$3 = this._idRules[id_$2];
if (!itab_$3) {
itab_$3 = this._idRules[id_$2] = []
};
itab_$3.push(r_$0)
} else if ("a" in lastsel_$1) {
var attr_$4 = lastsel_$1.a;
var atab_$5 = this._attrRules[attr_$4];
if (!atab_$5) {
atab_$5 = this._attrRules[attr_$4] = []
};
atab_$5.push(r_$0)
} else if ("t" in lastsel_$1) {
var tag_$6 = lastsel_$1.t;
var ttab_$7 = this._tagRules[tag_$6];
if (!ttab_$7) {
ttab_$7 = this._tagRules[tag_$6] = []
};
ttab_$7.push(r_$0)
} else {
this._rules.push(r_$0)
}};
$lzsc$temp["displayName"] = "_addRule";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#168/1";
return $lzsc$temp
})()(LzCSSStyleClass);
var LzCSSStyle = new LzCSSStyleClass();
lz.CSSStyle = LzCSSStyleClass;
Class.make("LzCSSStyleDeclaration", ["$lzsc$initialize", (function () {
var $lzsc$temp = function () {};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "_node", null, "getPropertyValue", (function () {
var $lzsc$temp = function (pname_$0) {
return LzCSSStyle.getPropertyValueFor(this._node, pname_$0)
};
$lzsc$temp["displayName"] = "getPropertyValue";
return $lzsc$temp
})(), "setNode", (function () {
var $lzsc$temp = function (node_$0) {
this._node = node_$0
};
$lzsc$temp["displayName"] = "setNode";
return $lzsc$temp
})()]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {}};
$lzsc$temp["displayName"] = "services/LzCSSStyle.lzs#749/1";
return $lzsc$temp
})()(LzCSSStyleDeclaration);
lz.CSSStyleDeclaration = LzCSSStyleDeclaration;
Class.make("LzStyleSheet", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (title_$0, href_$1, media_$2, sstype_$3) {
this.type = sstype_$3;
this.disabled = false;
this.ownerNode = null;
this.parentStyleSheet = null;
this.href = href_$1;
this.title = title_$0;
this.media = media_$2
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "type", null, "disabled", null, "ownerNode", null, "parentStyleSheet", null, "href", null, "title", null, "media", null]);
Class.make("LzCSSStyleSheet", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (title_$0, href_$1, media_$2, sstype_$3, ownerRule_$4, cssRules_$5) {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, title_$0, href_$1, media_$2, sstype_$3);
this.ownerRule = ownerRule_$4;
this.cssRules = cssRules_$5
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "ownerRule", null, "cssRules", null, "insertRule", (function () {
var $lzsc$temp = function (rule_$0, index_$1) {
if (!this.cssRules) {
this.cssRules = []
};
if (index_$1 < 0) {
return null
};
if (index_$1 < this.cssRules.length) {
this.cssRules.splice(index_$1, 0, rule_$0);
return index_$1
};
if (index_$1 == this.cssRules.length) {
return this.cssRules.push(rule_$0) - 1
};
return null
};
$lzsc$temp["displayName"] = "insertRule";
return $lzsc$temp
})()], LzStyleSheet);
lz.CSSStyleSheet = LzCSSStyleSheet;
Class.make("LzFocusService", ["onfocus", LzDeclaredEvent, "onescapefocus", LzDeclaredEvent, "lastfocus", null, "csel", null, "cseldest", null, "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this);
this.upDel = new LzDelegate(this, "gotKeyUp", lz.Keys, "onkeyup");
this.downDel = new LzDelegate(this, "gotKeyDown", lz.Keys, "onkeydown");
this.lastfocusDel = new LzDelegate(lz.Keys, "gotLastFocus", this, "onescapefocus")
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "upDel", void 0, "downDel", void 0, "lastfocusDel", void 0, "focuswithkey", false, "__LZskipblur", false, "__LZsfnextfocus", -1, "__LZsfrunning", false, "gotKeyUp", (function () {
var $lzsc$temp = function (kC_$0) {
if (this.csel && this.csel.onkeyup.ready) this.csel.onkeyup.sendEvent(kC_$0)
};
$lzsc$temp["displayName"] = "gotKeyUp";
return $lzsc$temp
})(), "gotKeyDown", (function () {
var $lzsc$temp = function (kC_$0) {
if (this.csel && this.csel.onkeydown.ready) this.csel.onkeydown.sendEvent(kC_$0);
if (kC_$0 == lz.Keys.keyCodes.tab) {
if (lz.Keys.isKeyDown("shift")) {
this.prev()
} else {
this.next()
}}};
$lzsc$temp["displayName"] = "gotKeyDown";
return $lzsc$temp
})(), "__LZcheckFocusChange", (function () {
var $lzsc$temp = function (v_$0) {
if (v_$0.focusable) {
this.setFocus(v_$0, false)
}};
$lzsc$temp["displayName"] = "__LZcheckFocusChange";
return $lzsc$temp
})(), "setFocus", (function () {
var $lzsc$temp = function (newsel_$0, fwkey_$1) {
switch (arguments.length) {
case 1:
fwkey_$1 = null
};
if (this.__LZsfrunning) {
this.__LZsfnextfocus = newsel_$0;
return
};
if (this.cseldest == newsel_$0) {
return
};
var prevsel_$2 = this.csel;
if (prevsel_$2 && !prevsel_$2.shouldYieldFocus()) {
return
};
if (newsel_$0 && !newsel_$0.focusable) {
newsel_$0 = this.getNext(newsel_$0);
if (this.cseldest == newsel_$0) {
return
}};
if (prevsel_$2) {
prevsel_$2.blurring = true
};
this.__LZsfnextfocus = -1;
this.__LZsfrunning = true;
this.cseldest = newsel_$0;
if (fwkey_$1 != null) {
this.focuswithkey = !(!fwkey_$1)
};
if (!this.__LZskipblur) {
this.__LZskipblur = true;
if (prevsel_$2 && prevsel_$2.onblur.ready) {
prevsel_$2.onblur.sendEvent(newsel_$0);
var next_$3 = this.__LZsfnextfocus;
if (next_$3 != -1) {
if (next_$3 && !next_$3.focusable) {
next_$3 = this.getNext(next_$3)
};
if (next_$3 != newsel_$0) {
this.__LZsfrunning = false;
this.setFocus(next_$3);
return
}}}};
this.lastfocus = prevsel_$2;
this.csel = newsel_$0;
this.__LZskipblur = false;
if (prevsel_$2) {
prevsel_$2.blurring = false
};
if (canvas.accessible && newsel_$0 && newsel_$0.sprite != null) {
newsel_$0.sprite.aafocus()
};
if (newsel_$0 && newsel_$0.onfocus.ready) {
newsel_$0.onfocus.sendEvent(newsel_$0);
var next_$3 = this.__LZsfnextfocus;
if (next_$3 != -1) {
if (next_$3 && !next_$3.focusable) {
next_$3 = this.getNext(next_$3)
};
if (next_$3 != newsel_$0) {
this.__LZsfrunning = false;
this.setFocus(next_$3);
return
}}};
if (this.onfocus.ready) {
this.onfocus.sendEvent(newsel_$0);
var next_$3 = this.__LZsfnextfocus;
if (next_$3 != -1) {
if (next_$3 && !next_$3.focusable) {
next_$3 = this.getNext(next_$3)
};
if (next_$3 != newsel_$0) {
this.__LZsfrunning = false;
this.setFocus(next_$3);
return
}}};
this.__LZsfrunning = false
};
$lzsc$temp["displayName"] = "setFocus";
return $lzsc$temp
})(), "clearFocus", (function () {
var $lzsc$temp = function () {
this.setFocus(null)
};
$lzsc$temp["displayName"] = "clearFocus";
return $lzsc$temp
})(), "getFocus", (function () {
var $lzsc$temp = function () {
return this.csel
};
$lzsc$temp["displayName"] = "getFocus";
return $lzsc$temp
})(), "next", (function () {
var $lzsc$temp = function () {
this.genMoveSelection(1)
};
$lzsc$temp["displayName"] = "next";
return $lzsc$temp
})(), "prev", (function () {
var $lzsc$temp = function () {
this.genMoveSelection(-1)
};
$lzsc$temp["displayName"] = "prev";
return $lzsc$temp
})(), "getNext", (function () {
var $lzsc$temp = function (focusview_$0) {
switch (arguments.length) {
case 0:
focusview_$0 = null
};
return this.moveSelSubview(focusview_$0 || this.csel, 1, false)
};
$lzsc$temp["displayName"] = "getNext";
return $lzsc$temp
})(), "getPrev", (function () {
var $lzsc$temp = function (focusview_$0) {
switch (arguments.length) {
case 0:
focusview_$0 = null
};
return this.moveSelSubview(focusview_$0 || this.csel, -1, false)
};
$lzsc$temp["displayName"] = "getPrev";
return $lzsc$temp
})(), "genMoveSelection", (function () {
var $lzsc$temp = function (movedir_$0) {
var sel_$1 = this.csel;
var check_$2 = sel_$1;
while (sel_$1 && check_$2 != canvas) {
if (!check_$2.visible) {
sel_$1 = null
};
check_$2 = check_$2.immediateparent
};
if (sel_$1 == null) {
sel_$1 = lz.ModeManager.getModalView()
};
var meth_$3 = "get" + (movedir_$0 == 1 ? "Next" : "Prev") + "Selection";
var v_$4 = sel_$1 ? sel_$1[meth_$3]() : null;
if (v_$4 == null) {
v_$4 = this.moveSelSubview(sel_$1, movedir_$0, true)
};
if (lz.ModeManager.__LZallowFocus(v_$4)) {
this.setFocus(v_$4, true)
}};
$lzsc$temp["displayName"] = "genMoveSelection";
return $lzsc$temp
})(), "accumulateSubviews", (function () {
var $lzsc$temp = function (accum_$0, v_$1, includep_$2, top_$3) {
if (v_$1 == includep_$2 || v_$1.focusable && v_$1.visible) accum_$0.push(v_$1);
if (top_$3 || !v_$1.focustrap && v_$1.visible) for (var i_$4 = 0;i_$4 < v_$1.subviews.length;i_$4++) this.accumulateSubviews(accum_$0, v_$1.subviews[i_$4], includep_$2, false)
};
$lzsc$temp["displayName"] = "accumulateSubviews";
return $lzsc$temp
})(), "moveSelSubview", (function () {
var $lzsc$temp = function (v_$0, mvdir_$1, sendEsc_$2) {
var root_$3 = v_$0 || canvas;
while (!root_$3.focustrap && root_$3.immediateparent && root_$3 != root_$3.immediateparent) root_$3 = root_$3.immediateparent;
var focusgroup_$4 = [];
this.accumulateSubviews(focusgroup_$4, root_$3, v_$0, true);
var index_$5 = -1;
var fglen_$6 = focusgroup_$4.length;
var escape_$7 = false;
for (var i_$8 = 0;i_$8 < fglen_$6;++i_$8) {
if (focusgroup_$4[i_$8] === v_$0) {
escape_$7 = mvdir_$1 == -1 && i_$8 == 0 || mvdir_$1 == 1 && i_$8 == fglen_$6 - 1;
index_$5 = i_$8;
break
}};
if (sendEsc_$2 && escape_$7 && this.onescapefocus.ready) {
this.onescapefocus.sendEvent()
};
if (index_$5 == -1 && mvdir_$1 == -1) index_$5 = 0;
index_$5 = (index_$5 + mvdir_$1 + fglen_$6) % fglen_$6;
return focusgroup_$4[index_$5]
};
$lzsc$temp["displayName"] = "moveSelSubview";
return $lzsc$temp
})()], LzEventable, ["LzFocus", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzFocusService.LzFocus = new LzFocusService()
}}};
$lzsc$temp["displayName"] = "services/LzFocus.lzs#26/1";
return $lzsc$temp
})()(LzFocusService);
lz.FocusService = LzFocusService;
lz.Focus = LzFocusService.LzFocus;
Class.make("LzTimerService", ["timerList", new Object(), "$lzsc$initialize", (function () {
var $lzsc$temp = function () {
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})(), "execDelegate", (function () {
var $lzsc$temp = function (p_$0) {
var del_$1 = p_$0.delegate;
lz.Timer.removeTimerWithID(del_$1, p_$0.id);
if (del_$1.enabled && del_$1.c) {
del_$1.execute(new Date().getTime())
}};
$lzsc$temp["displayName"] = "services/LzTimer.lzs#105/33";
return $lzsc$temp
})(), "removeTimerWithID", (function () {
var $lzsc$temp = function (d_$0, id_$1) {
var delID_$2 = d_$0.__delegateID;
var tle_$3 = this.timerList[delID_$2];
if (tle_$3 != null) {
if (tle_$3 instanceof Array) {
for (var i_$4 = 0;i_$4 < tle_$3.length;i_$4++) {
if (tle_$3[i_$4] == id_$1) {
tle_$3.splice(i_$4, 1);
if (tle_$3.length == 0) delete this.timerList[delID_$2];
break
}}} else if (tle_$3 == id_$1) {
delete this.timerList[delID_$2]
}}};
$lzsc$temp["displayName"] = "removeTimerWithID";
return $lzsc$temp
})(), "addTimer", (function () {
var $lzsc$temp = function (d_$0, millisecs_$1) {
if (!millisecs_$1 || millisecs_$1 < 1) millisecs_$1 = 1;
var p_$2 = {delegate: d_$0};
var id_$3 = LzTimeKernel.setTimeout(this.execDelegate, millisecs_$1, p_$2);
p_$2.id = id_$3;
if (id_$3 instanceof Array) {
Debug.error("setTimeout result type is unexpected; lz.Timer will fail")
};
var delID_$4 = d_$0.__delegateID;
var tle_$5 = this.timerList[delID_$4];
if (tle_$5 == null) {
this.timerList[delID_$4] = id_$3
} else if (!(tle_$5 instanceof Array)) {
this.timerList[delID_$4] = [tle_$5, id_$3]
} else {
tle_$5.push(id_$3)
};
return id_$3
};
$lzsc$temp["displayName"] = "addTimer";
return $lzsc$temp
})(), "removeTimer", (function () {
var $lzsc$temp = function (d_$0) {
var delID_$1 = d_$0.__delegateID;
var tle_$2 = this.timerList[delID_$1];
var id_$3 = null;
if (tle_$2 != null) {
if (tle_$2 instanceof Array) {
id_$3 = tle_$2.shift();
LzTimeKernel.clearTimeout(id_$3);
if (tle_$2.length == 0) delete this.timerList[delID_$1]
} else {
id_$3 = tle_$2;
LzTimeKernel.clearTimeout(id_$3);
delete this.timerList[delID_$1]
}};
return id_$3
};
$lzsc$temp["displayName"] = "removeTimer";
return $lzsc$temp
})(), "resetTimer", (function () {
var $lzsc$temp = function (d_$0, millisecs_$1) {
this.removeTimer(d_$0);
return this.addTimer(d_$0, millisecs_$1)
};
$lzsc$temp["displayName"] = "resetTimer";
return $lzsc$temp
})(), "countTimers", (function () {
var $lzsc$temp = function (d_$0) {
var tle_$1 = this.timerList[d_$0.__delegateID];
if (tle_$1 == null) {
return 0
} else if (tle_$1 instanceof Array) {
return tle_$1.length
} else return 1
};
$lzsc$temp["displayName"] = "countTimers";
return $lzsc$temp
})()], null, ["LzTimer", void 0]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzTimerService.LzTimer = new LzTimerService()
}}};
$lzsc$temp["displayName"] = "services/LzTimer.lzs#70/1";
return $lzsc$temp
})()(LzTimerService);
lz.TimerService = LzTimerService;
lz.Timer = LzTimerService.LzTimer;
(function () {
var $lzsc$temp = function () {
for (var k_$0 in lz) {
var v_$1 = lz[k_$0];
if (!(Function["$lzsc$isa"] ? Function.$lzsc$isa(v_$1) : v_$1 instanceof Function) && v_$1["_dbg_name"] == null) {
try {
v_$1._dbg_name = "lz." + k_$0
}
catch (e_$2) {}}}};
$lzsc$temp["displayName"] = "debugger/LzInit.lzs#11/4";
return $lzsc$temp
})()();
Class.make("$lzc$class_lzcontextmenuseparator", ["$ii6lnt1", (function () {
var $lzsc$temp = function ($0) {
this.parent.registerRedraw(this)
};
$lzsc$temp["displayName"] = "handle oninit";
return $lzsc$temp
})(), "$ii6lnt2", (function () {
var $lzsc$temp = function ($0) {
this.parent.unregisterRedraw(this)
};
$lzsc$temp["displayName"] = "handle ondestroy";
return $lzsc$temp
})(), "redraw", (function () {
var $lzsc$temp = function (context_$a_$0) {
context_$a_$0.moveTo(0, this.y + 9);
context_$a_$0.lineTo(this.parent.width - 3, this.y + 9);
context_$a_$0.strokeStyle = "#E5E5E5";
context_$a_$0.stroke()
};
$lzsc$temp["displayName"] = "redraw";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3) {
switch (arguments.length) {
case 0:
parent_$a_$0 = null;;case 1:
attrs_$b_$1 = null;;case 2:
children_$c_$2 = null;;case 3:
async_$d_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzView, ["tagname", "lzcontextmenuseparator", "attributes", new LzInheritedHash(LzView.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.mergeAttributes({$delegates: ["oninit", "$ii6lnt1", null, "ondestroy", "$ii6lnt2", null], height: 10}, $lzc$class_lzcontextmenuseparator.attributes)
}}};
$lzsc$temp["displayName"] = "contextmenu.lzx#8/1";
return $lzsc$temp
})()($lzc$class_lzcontextmenuseparator);
Class.make("$lzc$class_lzcontextmenutext", ["$ii6lnt3", (function () {
var $lzsc$temp = function ($0) {
this.parent.__overnow = this.data;
this.parent.registerRedraw(this)
};
$lzsc$temp["displayName"] = "handle onmouseover";
return $lzsc$temp
})(), "$ii6lnt4", (function () {
var $lzsc$temp = function ($0) {
this.parent.__overnow = null;
this.parent.unregisterRedraw(this)
};
$lzsc$temp["displayName"] = "handle onmouseout";
return $lzsc$temp
})(), "$ii6lnt5", (function () {
var $lzsc$temp = function ($0) {
this.parent.select(this.data);
this.parent.unregisterRedraw(this)
};
$lzsc$temp["displayName"] = "handle onmousedown";
return $lzsc$temp
})(), "redraw", (function () {
var $lzsc$temp = function (context_$a_$0) {
context_$a_$0.rect(this.x, this.y + 3, this.parent.width - 3, this.height);
context_$a_$0.fillStyle = "#CCCCCC";
context_$a_$0.fill()
};
$lzsc$temp["displayName"] = "redraw";
return $lzsc$temp
})(), "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3) {
switch (arguments.length) {
case 0:
parent_$a_$0 = null;;case 1:
attrs_$b_$1 = null;;case 2:
children_$c_$2 = null;;case 3:
async_$d_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzText, ["tagname", "lzcontextmenutext", "attributes", new LzInheritedHash(LzText.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.mergeAttributes({$delegates: ["onmouseover", "$ii6lnt3", null, "onmouseout", "$ii6lnt4", null, "onmousedown", "$ii6lnt5", null], clickable: true, fgcolor: LzColorUtils.convertColor("0x0"), fontsize: 11, fontstyle: "plain"}, $lzc$class_lzcontextmenutext.attributes)
}}};
$lzsc$temp["displayName"] = "contextmenu.lzx#23/1";
return $lzsc$temp
})()($lzc$class_lzcontextmenutext);
Class.make("$lzc$class_lzcontextmenudisabled", ["$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3) {
switch (arguments.length) {
case 0:
parent_$a_$0 = null;;case 1:
attrs_$b_$1 = null;;case 2:
children_$c_$2 = null;;case 3:
async_$d_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzText, ["tagname", "lzcontextmenudisabled", "attributes", new LzInheritedHash(LzText.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.mergeAttributes({fgcolor: LzColorUtils.convertColor("0xcccccc"), fontsize: 11, fontstyle: "plain"}, $lzc$class_lzcontextmenudisabled.attributes)
}}};
$lzsc$temp["displayName"] = "contextmenu.lzx#44/1";
return $lzsc$temp
})()($lzc$class_lzcontextmenudisabled);
Class.make("$lzc$class_ii6lntc", ["$ii6lnt7", (function () {
var $lzsc$temp = function ($0) {
var $1 = this.parent.container.width + 9;
if ($1 !== this["width"] || !this.inited) {
{
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_width"]) : this["$lzc$set_width"] instanceof Function) {
this["$lzc$set_width"]($1)
} else {
this["width"] = $1;
var $lzsc$u27gga = this["onwidth"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$u27gga) : $lzsc$u27gga instanceof LzEvent) {
if ($lzsc$u27gga.ready) {
$lzsc$u27gga.sendEvent($1)
}}}}}}};
$lzsc$temp["displayName"] = "width='${...}'";
return $lzsc$temp
})(), "$ii6lnt8", (function () {
var $lzsc$temp = function () {
try {
return $lzc$validateReferenceDependencies([this.parent.container, "width"], ["this.parent.container"])
}
catch ($lzsc$e) {
if (Error["$lzsc$isa"] ? Error.$lzsc$isa($lzsc$e) : $lzsc$e instanceof Error) {
lz.$lzsc$thrownError = $lzsc$e
};
throw $lzsc$e
}};
$lzsc$temp["displayName"] = "width dependencies";
return $lzsc$temp
})(), "$ii6lnt9", (function () {
var $lzsc$temp = function ($0) {
var $1 = this.parent.container.height + 9;
if ($1 !== this["height"] || !this.inited) {
{
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_height"]) : this["$lzc$set_height"] instanceof Function) {
this["$lzc$set_height"]($1)
} else {
this["height"] = $1;
var $lzsc$5yvnff = this["onheight"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$5yvnff) : $lzsc$5yvnff instanceof LzEvent) {
if ($lzsc$5yvnff.ready) {
$lzsc$5yvnff.sendEvent($1)
}}}}}}};
$lzsc$temp["displayName"] = "height='${...}'";
return $lzsc$temp
})(), "$ii6lnta", (function () {
var $lzsc$temp = function () {
try {
return $lzc$validateReferenceDependencies([this.parent.container, "height"], ["this.parent.container"])
}
catch ($lzsc$e) {
if (Error["$lzsc$isa"] ? Error.$lzsc$isa($lzsc$e) : $lzsc$e instanceof Error) {
lz.$lzsc$thrownError = $lzsc$e
};
throw $lzsc$e
}};
$lzsc$temp["displayName"] = "height dependencies";
return $lzsc$temp
})(), "$ii6lntb", (function () {
var $lzsc$temp = function ($0) {
this.createContext()
};
$lzsc$temp["displayName"] = "handle oninit";
return $lzsc$temp
})(), "__doredraw", (function () {
var $lzsc$temp = function (ignore_$a_$0) {
switch (arguments.length) {
case 0:
ignore_$a_$0 = null
};
if (this.visible && this.width && this.height && this.context) this.redraw(this.context)
};
$lzsc$temp["displayName"] = "__doredraw";
return $lzsc$temp
})(), "redraw", (function () {
var $lzsc$temp = function (context_$a_$0) {
switch (arguments.length) {
case 0:
context_$a_$0 = this.context
};
context_$a_$0.beginPath();
context_$a_$0.clearRect(0, 0, this.width, this.height);
LzKernelUtils.rect(context_$a_$0, 2.5, 3.5, this.width - 3, this.height - 3, this.classroot.inset);
context_$a_$0.fillStyle = "#000000";
context_$a_$0.globalAlpha = 0.2;
context_$a_$0.fill();
context_$a_$0.beginPath();
LzKernelUtils.rect(context_$a_$0, 0, 0, this.width - 3, this.height - 3, this.classroot.inset);
context_$a_$0.globalAlpha = 0.9;
context_$a_$0.fillStyle = "#FFFFFF";
context_$a_$0.fill();
context_$a_$0.globalAlpha = 1;
context_$a_$0.strokeStyle = "#CCCCCC";
context_$a_$0.stroke();
for (var uid_$b_$1 in this.parent.__drawnitems) {
context_$a_$0.beginPath();
this.parent.__drawnitems[uid_$b_$1].redraw(context_$a_$0)
}};
$lzsc$temp["displayName"] = "redraw";
return $lzsc$temp
})(), "$classrootdepth", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3) {
switch (arguments.length) {
case 0:
parent_$a_$0 = null;;case 1:
attrs_$b_$1 = null;;case 2:
children_$c_$2 = null;;case 3:
async_$d_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzView, ["displayName", "<anonymous extends='view'>", "attributes", new LzInheritedHash(LzView.attributes)]);
Class.make("$lzc$class_lzcontextmenu", ["inset", void 0, "__drawnitems", void 0, "__overnow", void 0, "$ii6lnt6", (function () {
var $lzsc$temp = function ($0) {
this.__globalmousedel = new (lz.Delegate)(this, "__handlemouse")
};
$lzsc$temp["displayName"] = "handle oninit";
return $lzsc$temp
})(), "select", (function () {
var $lzsc$temp = function (offset_$a_$0) {
var cmenu_$b_$1 = LzMouseKernel.__showncontextmenu;
if (cmenu_$b_$1) cmenu_$b_$1.__select(offset_$a_$0);
this.hide()
};
$lzsc$temp["displayName"] = "select";
return $lzsc$temp
})(), "show", (function () {
var $lzsc$temp = function () {
if (this.visible && this.__overnow != null) {
this.select(this.__overnow);
this.__overnow = null;
return
};
this.__overnow = null;
var pos_$a_$0 = canvas.getMouse();
if (pos_$a_$0.x > canvas.width - this.width) pos_$a_$0.x = canvas.width - this.width;
if (pos_$a_$0.y > canvas.height - this.height) pos_$a_$0.y = canvas.height - this.height;
this.bringToFront();
{
var $lzsc$fqbz7i = pos_$a_$0.x;
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_x"]) : this["$lzc$set_x"] instanceof Function) {
this["$lzc$set_x"]($lzsc$fqbz7i)
} else {
this["x"] = $lzsc$fqbz7i;
var $lzsc$b6a4ve = this["onx"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$b6a4ve) : $lzsc$b6a4ve instanceof LzEvent) {
if ($lzsc$b6a4ve.ready) {
$lzsc$b6a4ve.sendEvent($lzsc$fqbz7i)
}}}}};
{
var $lzsc$gwx168 = pos_$a_$0.y;
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_y"]) : this["$lzc$set_y"] instanceof Function) {
this["$lzc$set_y"]($lzsc$gwx168)
} else {
this["y"] = $lzsc$gwx168;
var $lzsc$gic2md = this["ony"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$gic2md) : $lzsc$gic2md instanceof LzEvent) {
if ($lzsc$gic2md.ready) {
$lzsc$gic2md.sendEvent($lzsc$gwx168)
}}}}};
{
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_visible"]) : this["$lzc$set_visible"] instanceof Function) {
this["$lzc$set_visible"](true)
} else {
this["visible"] = true;
var $lzsc$ymoazg = this["onvisible"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$ymoazg) : $lzsc$ymoazg instanceof LzEvent) {
if ($lzsc$ymoazg.ready) {
$lzsc$ymoazg.sendEvent(true)
}}}}};
this.__globalmousedel.register(lz.GlobalMouse, "onmousedown")
};
$lzsc$temp["displayName"] = "show";
return $lzsc$temp
})(), "hide", (function () {
var $lzsc$temp = function () {
this.__globalmousedel.unregisterAll();
var cmenu_$a_$0 = LzMouseKernel.__showncontextmenu;
if (cmenu_$a_$0) cmenu_$a_$0.__hide();
{
if (!this.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(this["$lzc$set_visible"]) : this["$lzc$set_visible"] instanceof Function) {
this["$lzc$set_visible"](false)
} else {
this["visible"] = false;
var $lzsc$apyxje = this["onvisible"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$apyxje) : $lzsc$apyxje instanceof LzEvent) {
if ($lzsc$apyxje.ready) {
$lzsc$apyxje.sendEvent(false)
}}}}}};
$lzsc$temp["displayName"] = "hide";
return $lzsc$temp
})(), "setItems", (function () {
var $lzsc$temp = function (newitems_$a_$0) {
this.__drawnitems = {};
var subviews_$b_$1 = this.container.subviews;
for (var i_$c_$2 = subviews_$b_$1.length - 1;i_$c_$2 >= 0;i_$c_$2--) {
var subview_$d_$3 = subviews_$b_$1[i_$c_$2];
if (!subview_$d_$3) continue;
subview_$d_$3.destroy()
};
var l_$e_$4 = newitems_$a_$0.length;
var ypos_$f_$5 = 0;
for (var i_$c_$2 = 0;i_$c_$2 < l_$e_$4;i_$c_$2++) {
var item_$g_$6 = newitems_$a_$0[i_$c_$2];
var classref_$h_$7 = lz["lzcontextmenu" + item_$g_$6.type];
if (classref_$h_$7) {
var newview_$i_$8 = new classref_$h_$7(this, {data: item_$g_$6.offset, text: item_$g_$6.label});
{
if (!newview_$i_$8.__LZdeleted) {
if (Function["$lzsc$isa"] ? Function.$lzsc$isa(newview_$i_$8["$lzc$set_y"]) : newview_$i_$8["$lzc$set_y"] instanceof Function) {
newview_$i_$8["$lzc$set_y"](ypos_$f_$5)
} else {
newview_$i_$8["y"] = ypos_$f_$5;
var $lzsc$jdizyw = newview_$i_$8["ony"];
if (LzEvent["$lzsc$isa"] ? LzEvent.$lzsc$isa($lzsc$jdizyw) : $lzsc$jdizyw instanceof LzEvent) {
if ($lzsc$jdizyw.ready) {
$lzsc$jdizyw.sendEvent(ypos_$f_$5)
}}}}};
ypos_$f_$5 += newview_$i_$8.height
}};
this.items = newitems_$a_$0;
this.background.redraw()
};
$lzsc$temp["displayName"] = "setItems";
return $lzsc$temp
})(), "registerRedraw", (function () {
var $lzsc$temp = function (who_$a_$0) {
this.__drawnitems[who_$a_$0.getUID()] = who_$a_$0;
this.background.redraw()
};
$lzsc$temp["displayName"] = "registerRedraw";
return $lzsc$temp
})(), "unregisterRedraw", (function () {
var $lzsc$temp = function (who_$a_$0) {
delete this.__drawnitems[who_$a_$0.getUID()];
this.background.redraw()
};
$lzsc$temp["displayName"] = "unregisterRedraw";
return $lzsc$temp
})(), "__handlemouse", (function () {
var $lzsc$temp = function (view_$a_$0) {
if (!view_$a_$0) {
this.hide();
return
};
do {
if (lz.lzcontextmenu["$lzsc$isa"] ? lz.lzcontextmenu.$lzsc$isa(view_$a_$0) : view_$a_$0 instanceof lz.lzcontextmenu) {
return
};
view_$a_$0 = view_$a_$0.immediateparent
} while (view_$a_$0 !== canvas);
this.hide()
};
$lzsc$temp["displayName"] = "__handlemouse";
return $lzsc$temp
})(), "background", void 0, "container", void 0, "$lzsc$initialize", (function () {
var $lzsc$temp = function (parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3) {
switch (arguments.length) {
case 0:
parent_$a_$0 = null;;case 1:
attrs_$b_$1 = null;;case 2:
children_$c_$2 = null;;case 3:
async_$d_$3 = false
};
(arguments.callee["$superclass"] && arguments.callee.$superclass.prototype["$lzsc$initialize"] || this.nextMethod(arguments.callee, "$lzsc$initialize")).call(this, parent_$a_$0, attrs_$b_$1, children_$c_$2, async_$d_$3)
};
$lzsc$temp["displayName"] = "$lzsc$initialize";
return $lzsc$temp
})()], LzView, ["tagname", "lzcontextmenu", "children", [{attrs: {$classrootdepth: 1, $delegates: ["oninit", "$ii6lntb", null, "onwidth", "__doredraw", null, "onheight", "__doredraw", null, "oncontext", "__doredraw", null, "onvisible", "__doredraw", null], height: new LzAlwaysExpr("height", "size", "$ii6lnt9", "$ii6lnta", "height='${...}'"), name: "background", width: new LzAlwaysExpr("width", "size", "$ii6lnt7", "$ii6lnt8", "width='${...}'")}, "class": $lzc$class_ii6lntc}, {attrs: {$classrootdepth: 1, name: "container", x: 3, y: 3}, "class": LzView}, {attrs: "container", "class": $lzc$class_userClassPlacement}], "attributes", new LzInheritedHash(LzView.attributes)]);
(function () {
var $lzsc$temp = function ($0) {
with ($0) with ($0.prototype) {
{
LzNode.mergeAttributes({$delegates: ["oninit", "$ii6lnt6", null], __drawnitems: {}, __overnow: null, inset: 5, options: {ignorelayout: true}, visible: false}, $lzc$class_lzcontextmenu.attributes)
}}};
$lzsc$temp["displayName"] = "contextmenu.lzx#46/1";
return $lzsc$temp
})()($lzc$class_lzcontextmenu);
lz["lzcontextmenuseparator"] = $lzc$class_lzcontextmenuseparator;
lz["lzcontextmenutext"] = $lzc$class_lzcontextmenutext;
lz["lzcontextmenudisabled"] = $lzc$class_lzcontextmenudisabled;
lz["lzcontextmenu"] = $lzc$class_lzcontextmenu;